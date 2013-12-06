package org.n10.codestory.s03e01.core;

import static org.fest.assertions.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.ElevatorEngine;

public final class ElevatorAssert {

	private ElevatorAssert() {
	}

	public static List<Command> c(Command... commands) {
		return Arrays.asList(commands);
	}

	@SafeVarargs
	public static void assertCommands(ElevatorEngine elevator, List<Command>... commands) {
		for (List<Command> command : commands) {
			assertThat(elevator.nextCommand()).isEqualTo(command);
		}
	}

	@SafeVarargs
	public static void assertManyCommands(ElevatorEngine elevator, int times, List<Command>... commands) {
		for (int i = 0; i < times; i++) {
			assertCommands(elevator, commands);
		}
	}

	public static void assertCommands(ElevatorEngine elevator, Command... commands) {
		for (Command command : commands) {
			assertThat(elevator.nextCommand().get(0)).isEqualTo(command);
		}
	}

	public static void assertManyCommands(ElevatorEngine elevator, int times, Command command) {
		for (int i = 0; i < times; i++) {
			assertCommands(elevator, command);
		}
	}
}