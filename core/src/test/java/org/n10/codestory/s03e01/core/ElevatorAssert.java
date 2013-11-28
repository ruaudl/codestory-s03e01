package org.n10.codestory.s03e01.core;

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

	public static void assertManyCommands(ElevatorEngine elevator, int times, Command command) {
		for (int i = 0; i < times; i++) {
			assertThat(elevator.nextCommand()).isEqualTo(command);
		}
	}
}