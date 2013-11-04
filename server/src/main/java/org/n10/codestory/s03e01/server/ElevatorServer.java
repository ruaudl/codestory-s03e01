package org.n10.codestory.s03e01.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.core.StateSmartElevator;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class ElevatorServer implements Container {

	private ElevatorEngine elevator = new StateSmartElevator();

	public static void main(String[] args) throws IOException {
		Container container = new ElevatorServer();
		Server server = new ContainerServer(container);
		Connection connection = new SocketConnection(server);
		SocketAddress address = new InetSocketAddress(4567);
		connection.connect(address);
	}

	@Override
	public void handle(Request request, Response response) {
		String path = request.getPath().getRelative("/");
		long time = System.currentTimeMillis();
		response.setContentType("text/plain");
		response.setValue("Content-Type", "text/plain");
		response.setValue("Server", "N10 Server/1.0 (Simple 4.0)");
		response.setDate("Date", time);
		response.setDate("Last-Modified", time);
		response.setCode(200);

		try {
			PrintStream printStream = response.getPrintStream();
			switch (path) {
			case "/nextCommand":
				synchronized (elevator) {
					Command nextCommand = elevator.nextCommand();
					System.out.println(String.format("%s %s", path, nextCommand));
					printStream.println(nextCommand.toString());
				}
			case "/call":
				Integer atFloor = Integer.valueOf(request.getParameter("atFloor"));
				Direction to = Direction.valueOf(request.getParameter("to"));
				synchronized (elevator) {
					elevator.call(atFloor, to);
				}
				System.out.println(String.format("%s atFloor %d to %s", path, atFloor, to));
				printStream.println("OK");
				break;
			case "/go":
				Integer floorToGo = Integer.valueOf(request.getParameter("floorToGo"));
				synchronized (elevator) {
					elevator.go(floorToGo);
				}
				printStream.println("OK");
				break;
			case "/userHasEntered":
			case "/userHasExited":
				System.out.println(path);
				printStream.println("OK");
				break;
			case "/reset":
				String cause = request.getParameter("cause");
				synchronized (elevator) {
					elevator.reset(cause);
				}
				System.out.println(String.format("%s cause %s", path, cause));
				printStream.println("OK");
				break;
			default:
				System.out.println(path);
				printStream.println("KO");
			}
			printStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			response.setCode(500);
		}
	}
}
