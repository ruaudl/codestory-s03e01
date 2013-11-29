package org.n10.codestory.s03e01.core;

import static org.n10.codestory.s03e01.api.Command.*;
import static org.n10.codestory.s03e01.core.ElevatorAssert.*;

import org.junit.Test;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;

public class SmartElevatorTest {

	@Test
	public void shouldDoNothingAtStart() {
		ElevatorEngine elevator = new StateSmartElevator();
		assertCommands(elevator, NOTHING, NOTHING, NOTHING, NOTHING);
	}

	@Test
	public void shouldGoToCall() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(4, Direction.UP);
		assertCommands(elevator, UP, UP, UP, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(5, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoToCallThenGoDown() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(4, Direction.DOWN);
		assertCommands(elevator, UP, UP, UP, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(2, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoToGo() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(0, Direction.UP);
		assertCommands(elevator, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(2, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoThroughTheWholeTargets() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(4, Direction.UP);
		elevator.call(2, Direction.DOWN);
		elevator.call(3, Direction.DOWN);
		assertCommands(elevator, UP, UP, UP, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(5, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(0, 0);
		assertCommands(elevator, CLOSE, DOWN, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(0, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoThroughTheWholeTargetsWithState() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(4, Direction.UP);
		elevator.call(2, Direction.DOWN);
		elevator.call(3, Direction.DOWN);
		assertCommands(elevator, UP, UP, UP, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(5, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(0, 0);
		assertCommands(elevator, CLOSE, DOWN, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(0, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldSkipFirstReverse() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(0, Direction.UP);
		assertCommands(elevator, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(4, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP);
		elevator.call(0, Direction.UP);
		elevator.call(2, Direction.UP);
		assertCommands(elevator, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, DOWN, DOWN, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(4, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(4, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldNotKeep() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(0, Direction.UP);
		elevator.call(1, Direction.UP);
		assertCommands(elevator, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(1, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldOpenOnce() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(0, Direction.UP);
		assertCommands(elevator, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		elevator.call(0, Direction.UP);
		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP, OPEN);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldSkipOverThreshold() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.limit(3);

		elevator.call(0, Direction.UP);
		elevator.call(1, Direction.UP);
		elevator.call(2, Direction.UP);
		elevator.call(3, Direction.UP);
		elevator.call(4, Direction.UP);

		assertCommands(elevator, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(7, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(8, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(8, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(9, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, DOWN, DOWN, DOWN, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(6, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldSkipOverThresholdAndFirstReverse() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.limit(3);

		elevator.call(0, Direction.UP);
		elevator.call(1, Direction.UP);
		elevator.call(2, Direction.DOWN);
		elevator.call(3, Direction.UP);
		elevator.call(4, Direction.UP);

		assertCommands(elevator, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(7, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(8, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(9, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(1, 0);
		assertCommands(elevator, CLOSE, DOWN, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(6, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldSkipFloorWhenCabinIsFull() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.reset(0, 6, 2, 1, "");
		elevator.call(0, Direction.UP);
		elevator.call(0, Direction.UP);
		elevator.call(2, Direction.UP);
		assertCommands(elevator, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		elevator.go(3, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP, OPEN);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(4, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldNotSkipFloorWhenCabinIsNotFull() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.reset(0, 6, 3, 1, "");
		elevator.call(0, Direction.UP);
		elevator.call(0, Direction.UP);
		elevator.call(2, Direction.UP);
		assertCommands(elevator, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		elevator.go(3, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		elevator.userHasEntered(null, 0);
		elevator.go(4, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldCareOfPoints() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.reset(0, 100, 2, 1, "");
		elevator.call(10, Direction.UP);
		elevator.call(50, Direction.UP);
		assertManyCommands(elevator, 10, UP);
		assertCommands(elevator, OPEN);

		elevator.userHasEntered(null, 0);
		elevator.go(60, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 35, UP);

		elevator.call(50, Direction.UP);

		assertManyCommands(elevator, 15, UP);
		assertCommands(elevator, OPEN);
		elevator.userHasExited(null, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 10, DOWN);
		assertCommands(elevator, OPEN);

		elevator.userHasEntered(null, 0);
		elevator.go(60, 0);
		elevator.userHasEntered(null, 0);
		elevator.go(70, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 20, UP);
		assertCommands(elevator, OPEN);

		elevator.userHasExited(null, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 10, DOWN);
		assertCommands(elevator, OPEN);

		elevator.userHasExited(null, 0);

		assertCommands(elevator, CLOSE, NOTHING);
	}
}