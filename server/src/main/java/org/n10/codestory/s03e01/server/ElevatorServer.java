package org.n10.codestory.s03e01.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.n10.codestory.s03e01.core.ElevatorPlayer;
import org.n10.codestory.s03e01.core.ElevatorRequest;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class ElevatorServer implements Container {

	private ElevatorPlayer player = new ElevatorPlayer();

	public static void main(String[] args) throws IOException {
		Container container = new ElevatorServer();
		Server server = new ContainerServer(container);
		Connection connection = new SocketConnection(server);
		SocketAddress address = new InetSocketAddress(4567);
		connection.connect(address);
	}

	@Override
	public void handle(Request request, Response response) {
		long time = System.currentTimeMillis();
		response.setContentType("text/plain");
		response.setValue("Content-Type", "text/plain");
		response.setValue("Server", "N10 Server/1.0 (Simple 4.0)");
		response.setDate("Date", time);
		response.setDate("Last-Modified", time);
		response.setCode(200);

		String target = request.getPath().getRelative("/");
		if ("/resets".equals(target)) {
			response.setValue("Content-Type", "text/html");
		}

		ElevatorRequest elevatorRequest = new ElevatorRequest();
		elevatorRequest.setTarget(target);
		for (String parameter : ElevatorPlayer.PARAMS) {
			elevatorRequest.addParameter(parameter, request.getParameter(parameter));
		}

		try {
			player.play(elevatorRequest, response.getPrintStream());
			response.getPrintStream().close();
		} catch (IOException e) {
			e.printStackTrace();
			response.setCode(500);
		}
	}
}
