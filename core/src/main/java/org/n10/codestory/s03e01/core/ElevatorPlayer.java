package org.n10.codestory.s03e01.core;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;

public class ElevatorPlayer {

	public static final String[] PARAMS = new String[] { "atFloor", "to", "floorToGo", "threshold", "lowerFloor", "higherFloor", "cause", "cabinSize" };
	private ElevatorEngine elevator = new StateSmartElevator();
	private TreeMap<String, String> resets = new TreeMap<String, String>();

	public void play(ElevatorRequest request, PrintStream stream) throws IOException {
		StringBuilder logBuilder = new StringBuilder();
		logBuilder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
		logBuilder.append(" ").append(elevator.printState());

		String logTarget = "";
		switch (request.getTarget()) {
		case "/nextCommand":
			String command = "";
			synchronized (elevator) {
				command = elevator.nextCommand().toString();
				stream.println(command);
			}
			logTarget = String.format("[%s = %s]", request.getTarget(), command);
			break;
		case "/call":
			Integer atFloor = Integer.valueOf(request.getParameter("atFloor"));
			Direction to = Direction.valueOf(request.getParameter("to"));
			synchronized (elevator) {
				elevator.call(atFloor, to);
			}
			logTarget = String.format("[%s atFloor %d to go %s]", request.getTarget(), atFloor, to);
			break;
		case "/go":
			Integer floorToGo = Integer.valueOf(request.getParameter("floorToGo"));
			synchronized (elevator) {
				elevator.go(floorToGo);
			}
			logTarget = String.format("[%s to %s]", request.getTarget(), floorToGo);
			break;
		case "/userHasEntered":
			synchronized (elevator) {
				elevator.userHasEntered(null);
			}
			logTarget = String.format("[%s]", request.getTarget());
			break;
		case "/userHasExited":
			synchronized (elevator) {
				elevator.userHasExited(null);
			}
			logTarget = String.format("[%s]", request.getTarget());
			break;
		case "/limit":
			Integer threshold = Integer.valueOf(request.getParameter("threshold"));
			synchronized (elevator) {
				elevator.limit(threshold);
			}
			logTarget = String.format("[%s limit to %d]", request.getTarget(), threshold);
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
			Integer cabinSize = null;
			if (request.getParameter("cabinSize") != null) {
				higherFloor = Integer.valueOf(request.getParameter("cabinSize"));
			}
			String cause = request.getParameter("cause");
			synchronized (elevator) {
				elevator.reset(lowerFloor, higherFloor, cabinSize, cause);
			}

			resets.put(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()), cause);
			while (resets.size() > 20) {
				resets.remove(resets.firstKey());
			}

			logTarget = String.format("[%s cause %s]", request.getTarget(), cause);
			break;
		case "/resets":
			stream.println("<head><meta http-equiv=\"refresh\" content=\"5\"></head><body>");
			for (Entry<String, String> reset : resets.entrySet()) {
				stream.println(String.format("%s: %s<br/>", reset.getKey(), reset.getValue()));
			}
			stream.println("</body>");
			break;
		default:
			stream.println("WHAT?");
			logTarget = String.format("[%s]", request.getTarget());
		}
		logBuilder.append(String.format(" %-50s ", logTarget));

		logBuilder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
		logBuilder.append(" ").append(elevator.printState()).append(" ");
		System.out.println(logBuilder.toString());
	}
}