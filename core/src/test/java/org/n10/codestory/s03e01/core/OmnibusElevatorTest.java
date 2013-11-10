package org.n10.codestory.s03e01.core;

import static org.n10.codestory.s03e01.core.ElevatorAssert.*;
import static org.n10.codestory.s03e01.api.Command.*;

import org.junit.Test;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.core.CyclingOmnibusElevator;
import org.n10.codestory.s03e01.core.StateOmnibusElevator;

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
			for (int i = 0; i < ElevatorEngine.HIGHER_FLOOR; i++) {
				assertCommands(elevator, OPEN, CLOSE, UP);
			}
			for (int i = 0; i < ElevatorEngine.HIGHER_FLOOR; i++) {
				assertCommands(elevator, OPEN, CLOSE, DOWN);
			}
		}
	}
}