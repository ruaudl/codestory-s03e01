package org.n10.codestory.s03e01;

import static org.fest.assertions.Assertions.*;

import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.ElevatorEngine;

public final class ElevatorAssert {

	private ElevatorAssert() {
	}

	public static void assertCommands(ElevatorEngine elevator, Command... commands) {
		for (Command command : commands) {
			assertThat(elevator.nextCommand()).isEqualTo(command);
		}
	}
}