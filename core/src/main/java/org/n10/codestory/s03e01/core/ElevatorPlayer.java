package org.n10.codestory.s03e01.core;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;

public class ElevatorPlayer {

	public static final String[] PARAMS = new String[] { "atFloor", "to", "floorToGo", "threshold", "lowerFloor", "higherFloor", "cause" };

	private ElevatorEngine elevator = new StateSmartElevator();

	public void play(ElevatorRequest request, PrintStream stream) throws IOException {
		switch (request.getTarget()) {
		case "/nextCommand":
			synchronized (elevator) {
				String command = elevator.nextCommand().toString();
				System.out.println(String.format("%s %s", request.getTarget(), command));
				stream.println(command);
			}
			break;
		case "/call":
			Integer atFloor = Integer.valueOf(request.getParameter("atFloor"));
			Direction to = Direction.valueOf(request.getParameter("to"));
			synchronized (elevator) {
				elevator.call(atFloor, to);
			}
			System.out.println(String.format("%s atFloor %d to %s", request.getTarget(), atFloor, to));
			break;
		case "/go":
			Integer floorToGo = Integer.valueOf(request.getParameter("floorToGo"));
			synchronized (elevator) {
				elevator.go(floorToGo);
			}
			break;
		case "/userHasEntered":
		case "/userHasExited":
			System.out.println(request.getTarget());
			break;
		case "/limit":
			Integer threshold = Integer.valueOf(request.getParameter("threshold"));
			synchronized (elevator) {
				elevator.limit(threshold);
			}
			System.out.println(String.format("%s limit to %d", request.getTarget(), threshold));
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

			System.out.println(String.format("%s cause %s", request.getTarget(), cause));
			break;
		default:
			System.out.println(request.getTarget());
			stream.println("WHAT?");
		}
	}

}