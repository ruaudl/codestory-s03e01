package org.n10.codestory.s03e01;

import static elevator.Command.*;
import static org.n10.codestory.s03e01.ElevatorAssert.*;

import org.junit.Test;

import elevator.engine.ElevatorEngine;

public class OmnibusElevatorTest {

	@Test
	public void shouldCycle() {
		ElevatorEngine elevator = new CyclingOmnibusElevator();
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 5; i++) {
				assertCommands(elevator, UP, OPEN, CLOSE);
			}
			for (int i = 0; i < 5; i++) {
				assertCommands(elevator, DOWN, OPEN, CLOSE);
			}
		}
	}

	@Test
	public void shouldCycleWithState() {
		ElevatorEngine elevator = new StateOmnibusElevator();
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 5; i++) {
				assertCommands(elevator, OPEN, CLOSE, UP);
			}
			for (int i = 0; i < 5; i++) {
				assertCommands(elevator, OPEN, CLOSE, DOWN);
			}
		}
	}
}