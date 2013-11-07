package org.n10.codestory.s03e01;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.inject.Produces;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.core.StateSmartElevator;

import com.google.common.util.concurrent.Service;

@WebServlet("/")
public class ElevatorApplication extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ElevatorEngine elevator = new StateSmartElevator();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();
		if (path == null) {
			path = request.getServletPath();
		}
		String target = path.substring(path.lastIndexOf("/"));
		switch (target) {
		case "/nextCommand":
			synchronized (elevator) {
				Command nextCommand = elevator.nextCommand();
				response.getWriter().println(nextCommand);
				// baseRequest.getResponse().getWriter().println(nextCommand);
				System.out.println(String.format("%s %s", target, nextCommand));
			}
			break;
		case "/call":
			Integer atFloor = Integer.valueOf(request.getParameter("atFloor"));
			Direction to = Direction.valueOf(request.getParameter("to"));
			synchronized (elevator) {
				elevator.call(atFloor, to);
			}
			System.out.println(String.format("%s atFloor %d to %s", target, atFloor, to));
			break;
		case "/go":
			Integer floorToGo = Integer.valueOf(request.getParameter("floorToGo"));
			synchronized (elevator) {
				elevator.go(floorToGo);
			}
			// logger.info(format("%s floorToGo %d", target, floorToGo));
			break;
		case "/userHasEntered":
		case "/userHasExited":
			System.out.println(target);
			break;
		case "/limit":
			Integer threshold = Integer.valueOf(request.getParameter("threshold"));
			synchronized (elevator) {
				elevator.limit(threshold);
			}
			System.out.println(String.format("%s limit to %d", target, threshold));
			break;
		case "/reset":
			Integer lowerFloor = null;
			if (request.getParameter("lowerFloor") != null) {
				lowerFloor = Integer.valueOf(request.getParameter("lowerFloor"));
			}
			Integer higherFloor = null;
			if (request.getParameter("higherFloor") != null) {
				higherFloor = Integer.valueOf(request.getParameter("higherFloor"));
			}
			String cause = request.getParameter("cause");
			synchronized (elevator) {
				elevator.reset(lowerFloor, higherFloor, cause);
			}
			System.out.println(String.format("%s cause %s", target, cause));
			break;
		default:
			System.out.println(target);
		}
		// baseRequest.setHandled(true);
	}

	@Override
	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		super.doGet(arg0, arg1);
	}

	@Produces
	public Set<Service> getServices() {
		return Collections.emptySet();
	}
}
