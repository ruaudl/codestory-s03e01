package org.n10.codestory.s03e01.core;

import static org.n10.codestory.s03e01.api.Command.*;
import static org.n10.codestory.s03e01.core.ElevatorAssert.*;

import org.junit.Test;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.core.SimpleSmartElevator;
import org.n10.codestory.s03e01.core.StateSmartElevator;

public class SmartElevatorTest {

	@Test
	public void shouldDoNothingAtStart() {
		ElevatorEngine elevator = new SimpleSmartElevator();
		assertCommands(elevator, NOTHING, NOTHING, NOTHING, NOTHING);
	}

	@Test
	public void shouldGoToCall() {
		ElevatorEngine elevator = new SimpleSmartElevator();
		elevator.call(4, Direction.UP);
		assertCommands(elevator, UP, UP, UP, UP, OPEN, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoToCallThenGoDown() {
		ElevatorEngine elevator = new SimpleSmartElevator();
		elevator.call(4, Direction.DOWN);
		assertCommands(elevator, UP, UP, UP, UP, OPEN);
		elevator.go(2);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoToGo() {
		ElevatorEngine elevator = new SimpleSmartElevator();
		elevator.go(2);
		assertCommands(elevator, UP, UP, OPEN, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoThroughTheWholeTargets() {
		ElevatorEngine elevator = new SimpleSmartElevator();
		elevator.call(4, Direction.UP);
		elevator.call(2, Direction.DOWN);
		elevator.call(3, Direction.DOWN);
		assertCommands(elevator, UP, UP, UP, UP, OPEN);
		elevator.go(5);
		assertCommands(elevator, CLOSE, UP, OPEN, CLOSE, DOWN, DOWN, OPEN);
		elevator.go(0);
		assertCommands(elevator, CLOSE, DOWN, OPEN);
		elevator.go(0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoThroughTheWholeTargetsWithState() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(4, Direction.UP);
		elevator.call(2, Direction.DOWN);
		elevator.call(3, Direction.DOWN);
		assertCommands(elevator, UP, UP, UP, UP, OPEN);
		elevator.go(5);
		assertCommands(elevator, CLOSE, UP, OPEN, CLOSE, DOWN, DOWN, OPEN);
		elevator.go(0);
		assertCommands(elevator, CLOSE, DOWN, OPEN);
		elevator.go(0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldSkipFirstReverse() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(0, Direction.UP);
		assertCommands(elevator, OPEN);
		elevator.go(4);
		assertCommands(elevator, CLOSE, UP, UP, UP);
		elevator.call(0, Direction.UP);
		elevator.call(2, Direction.UP);
		assertCommands(elevator, UP, OPEN, CLOSE, DOWN, DOWN, DOWN, DOWN, OPEN);
		elevator.go(4);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		elevator.go(4);
		assertCommands(elevator, CLOSE, UP, UP, OPEN, CLOSE, NOTHING);
	}

	@Test
	public void shouldNotKeep() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(0, Direction.UP);
		elevator.call(1, Direction.UP);
		assertCommands(elevator, OPEN);
		elevator.go(3);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.go(1);
		assertCommands(elevator, CLOSE, UP, UP, OPEN, CLOSE, NOTHING);
	}

	@Test
	public void shouldOpenOnce() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(0, Direction.UP);
		assertCommands(elevator, OPEN);
		elevator.go(3);
		elevator.call(0, Direction.UP);
		elevator.go(3);
		assertCommands(elevator, CLOSE, UP, UP, UP, OPEN, CLOSE, NOTHING);
	}
}