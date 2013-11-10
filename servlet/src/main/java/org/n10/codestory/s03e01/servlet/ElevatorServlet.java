package org.n10.codestory.s03e01.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.inject.Produces;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n10.codestory.s03e01.core.ElevatorPlayer;
import org.n10.codestory.s03e01.core.ElevatorRequest;

import com.google.common.util.concurrent.Service;

@WebServlet("/")
public class ElevatorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ElevatorPlayer player = new ElevatorPlayer();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();
		if (path == null) {
			path = request.getServletPath();
		}
		String target = path.substring(path.lastIndexOf("/"));

		ElevatorRequest elevatorRequest = new ElevatorRequest();
		elevatorRequest.setTarget(target);
		for (String parameter : ElevatorPlayer.PARAMS) {
			elevatorRequest.addParameter(parameter, request.getParameter(parameter));
		}

		player.play(elevatorRequest, new PrintStream(response.getOutputStream()));
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
