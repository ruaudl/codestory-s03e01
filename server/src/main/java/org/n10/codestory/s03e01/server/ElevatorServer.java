package org.n10.codestory.s03e01.server;

import static spark.Spark.*;

import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.core.StateSmartElevator;

import spark.Request;
import spark.Response;
import spark.Route;

public class ElevatorServer {

	private ElevatorEngine elevator = new StateSmartElevator();

	public static void main(String[] args) {
		new ElevatorServer().start();
	}

	public void start() {
		get(new Route("/:path") {
			@Override
			public Object handle(Request request, Response response) {
				String path = request.params(":path");
				switch (path) {
				case "nextCommand":
					synchronized (elevator) {
						Command nextCommand = elevator.nextCommand();
						System.out.println(String.format("%s %s", path, nextCommand));
						return nextCommand;
					}
				case "call":
					Integer atFloor = Integer.valueOf(request.queryParams("atFloor"));
					Direction to = Direction.valueOf(request.queryParams("to"));
					synchronized (elevator) {
						elevator.call(atFloor, to);
					}
					System.out.println(String.format("%s atFloor %d to %s", path, atFloor, to));
					break;
				case "go":
					Integer floorToGo = Integer.valueOf(request.queryParams("floorToGo"));
					synchronized (elevator) {
						elevator.go(floorToGo);
					}
					break;
				case "userHasEntered":
				case "userHasExited":
					System.out.println(path);
					break;
				case "reset":
					String cause = request.queryParams("cause");
					synchronized (elevator) {
						elevator.reset(cause);
					}
					System.out.println(String.format("%s cause %s", path, cause));
					break;
				default:
					System.out.println(path);
				}
				return "";
			}
		});
	}
}
