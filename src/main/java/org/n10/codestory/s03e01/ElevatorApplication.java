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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/")
public class ElevatorApplication extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
        
        private static final Logger LOGGER = LoggerFactory.getLogger(ElevatorApplication.class);
        
	private ElevatorEngine elevator = new StateSmartElevator();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();
		if(path == null) {
			path = request.getServletPath();
		}
		String target = path.substring(path.lastIndexOf("/"));
		switch (target) {
        case "/nextCommand":
            synchronized (elevator) {
                Command nextCommand = elevator.nextCommand();
                LOGGER.info("nextCommand : {}", nextCommand);
                LOGGER.info("Current state {}", elevator);
                response.getWriter().println(nextCommand);
//                baseRequest.getResponse().getWriter().println(nextCommand);
//                System.out.println(String.format("%s %s", target, nextCommand));
            }
            break;
        case "/call":
            Integer atFloor = Integer.valueOf(request.getParameter("atFloor"));
            Direction to = Direction.valueOf(request.getParameter("to"));
            LOGGER.info("Call atFloor {} to {}", atFloor, to);
            synchronized (elevator) {
                elevator.call(atFloor, to);
            }
//            System.out.println(String.format("%s atFloor %d to %s", target, atFloor, to));
            break;
        case "/go":
            Integer floorToGo = Integer.valueOf(request.getParameter("floorToGo"));
            LOGGER.info("Go to {}", floorToGo);
            synchronized (elevator) {
                elevator.go(floorToGo);
            }
//            logger.info(format("%s floorToGo %d", target, floorToGo));
            break;
        case "/userHasEntered":
        case "/userHasExited":
        	LOGGER.info(target);
            break;
        case "/reset":
            String cause = request.getParameter("cause");
            String lowerFloor = request.getParameter("lowerFloor");
            String higherFloor = request.getParameter("higherFloor");
            LOGGER.warn("Reset caused by {}", cause);
            LOGGER.warn("Reset lower floor set to {}", lowerFloor);
            LOGGER.warn("Reset higher floor set to {}", higherFloor);
            synchronized (elevator) {
                elevator.reset(cause);
            }
//            System.out.println(String.format("%s cause %s", target, cause));
            break;
        default:
        	LOGGER.info(target);
    }
//    baseRequest.setHandled(true);
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
