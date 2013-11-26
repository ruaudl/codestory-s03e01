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

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String[] PARAMS = new String[] { "atFloor", "to", "floorToGo", "threshold", "lowerFloor", "higherFloor", "cause", "cabinSize", "cabinCount", "cabin" };
	private ElevatorEngine elevator = new StateSmartElevator();
	private TreeMap<String, String> resets = new TreeMap<String, String>();

	public void play(ElevatorRequest request, PrintStream stream) throws IOException {
		StringBuilder logBuilder = new StringBuilder();
		logBuilder.append(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
		logBuilder.append(" ").append(elevator.printState());

		String logTarget = "";
		switch (request.getTarget()) {
		case "/nextCommands":
			String command = "";
			synchronized (elevator) {
				command = elevator.nextCommand().toString() + "\nNOTHING";
				stream.println(command);
			}
			logTarget = String.format("[%s = %s]", request.getTarget(), command);
			break;
		case "/call":
			Integer atFloor = request.getParameterAsInteger("atFloor");
			Direction to = Direction.valueOf(request.getParameter("to"));
			synchronized (elevator) {
				elevator.call(atFloor, to);
			}
			logTarget = String.format("[%s atFloor %d to go %s]", request.getTarget(), atFloor, to);
			break;
		case "/go":
			Integer floorToGo = request.getParameterAsInteger("floorToGo");
			Integer cabin = request.getParameterAsInteger("cabin");
			synchronized (elevator) {
				elevator.go(floorToGo, cabin);
			}
			logTarget = String.format("[%s to %s in cabin %s]", request.getTarget(), floorToGo, cabin);
			break;
		case "/userHasEntered":
			Integer cabinId = request.getParameterAsInteger("cabin");
			synchronized (elevator) {
				elevator.userHasEntered(null, cabinId);
			}
			logTarget = String.format("[%s in cabin %s]", request.getTarget(), cabinId);
			break;
		case "/userHasExited":
			Integer idCabin = request.getParameterAsInteger("cabin");
			synchronized (elevator) {
				elevator.userHasExited(null, idCabin);
			}
			logTarget = String.format("[%s in cabin %s]", request.getTarget(), idCabin);
			break;
		case "/limit":
			Integer threshold = request.getParameterAsInteger("threshold");
			synchronized (elevator) {
				elevator.limit(threshold);
			}
			logTarget = String.format("[%s limit to %d]", request.getTarget(), threshold);
			break;
		case "/reset":
			Integer lowerFloor = request.getParameterAsInteger("lowerFloor");
			Integer higherFloor = request.getParameterAsInteger("higherFloor");
			Integer cabinSize = request.getParameterAsInteger("cabinSize");
			Integer cabinCount = request.getParameterAsInteger("cabinCount");
			String cause = request.getParameter("cause");
			synchronized (elevator) {
				elevator.reset(lowerFloor, higherFloor, cabinSize, cabinCount, cause);
			}
			logTarget = String.format("[%s cause %s from %s to %s max %s nb %s]", request.getTarget(), cause, lowerFloor, higherFloor, cabinSize, cabinCount);
			registerReset(logTarget);
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

		logBuilder.append(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
		logBuilder.append(" ").append(elevator.printState()).append(" ");
		System.out.println(logBuilder.toString());
	}

	private void registerReset(String log) {
		resets.put(new SimpleDateFormat(DATE_FORMAT).format(new Date()), log);
		while (resets.size() > 20) {
			resets.remove(resets.firstKey());
		}
	}
}