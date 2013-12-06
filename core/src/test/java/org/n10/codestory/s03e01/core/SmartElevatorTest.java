package org.n10.codestory.s03e01.core;

import static java.util.Arrays.*;
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
		assertCommands(elevator, UP, UP, UP, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(5, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_UP);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoToCallThenGoDown() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(4, Direction.DOWN);
		assertCommands(elevator, UP, UP, UP, UP, OPEN_DOWN);
		elevator.userHasEntered(null, 0);
		elevator.go(2, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN_DOWN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoToGo() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(0, Direction.UP);
		assertCommands(elevator, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(2, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN_UP);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoThroughTheWholeTargets() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(4, Direction.UP);
		elevator.call(2, Direction.DOWN);
		elevator.call(3, Direction.DOWN);
		assertCommands(elevator, UP, UP, UP, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(5, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_DOWN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN_DOWN);
		elevator.userHasEntered(null, 0);
		elevator.go(0, 0);
		assertCommands(elevator, CLOSE, DOWN, OPEN_DOWN);
		elevator.userHasEntered(null, 0);
		elevator.go(0, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN_DOWN);
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
		assertCommands(elevator, UP, UP, UP, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(5, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_DOWN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN_DOWN);
		elevator.userHasEntered(null, 0);
		elevator.go(0, 0);
		assertCommands(elevator, CLOSE, DOWN, OPEN_DOWN);
		elevator.userHasEntered(null, 0);
		elevator.go(0, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN_DOWN);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldSkipFirstReverse() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(0, Direction.UP);
		assertCommands(elevator, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(4, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP);
		elevator.call(0, Direction.UP);
		elevator.call(2, Direction.UP);
		assertCommands(elevator, UP, OPEN_DOWN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, DOWN, DOWN, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(4, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(4, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN_UP);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldNotKeep() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(0, Direction.UP);
		elevator.call(1, Direction.UP);
		assertCommands(elevator, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(1, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN_UP);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldOpenOnce() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.call(0, Direction.UP);
		assertCommands(elevator, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		elevator.call(0, Direction.UP);
		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP, OPEN_UP);
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

		assertCommands(elevator, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(7, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(8, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(8, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(9, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP, UP, OPEN_UP);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_UP);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_DOWN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, DOWN, DOWN, DOWN, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(6, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN_UP);
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

		assertCommands(elevator, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(7, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(8, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(9, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP, UP, OPEN_UP);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_UP);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_DOWN);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, OPEN_DOWN);
		elevator.userHasEntered(null, 0);
		elevator.go(1, 0);
		assertCommands(elevator, CLOSE, DOWN, OPEN_UP);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(6, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN_UP);
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
		assertCommands(elevator, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		elevator.go(3, 0);
		assertCommands(elevator, CLOSE, UP, UP, UP, OPEN_DOWN);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, DOWN, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(4, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN_UP);
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
		assertCommands(elevator, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		elevator.go(3, 0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN_UP);
		elevator.userHasEntered(null, 0);
		elevator.go(4, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_UP);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		assertCommands(elevator, CLOSE, UP, OPEN_UP);
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
		assertCommands(elevator, OPEN_UP);

		elevator.userHasEntered(null, 0);
		elevator.go(60, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 35, UP);

		elevator.call(50, Direction.UP);

		assertManyCommands(elevator, 15, UP);
		assertCommands(elevator, OPEN_DOWN);

		elevator.userHasExited(null, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 10, DOWN);
		assertCommands(elevator, OPEN_UP);

		elevator.userHasEntered(null, 0);
		elevator.go(60, 0);
		elevator.userHasEntered(null, 0);
		elevator.go(70, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 20, UP);
		assertCommands(elevator, OPEN_DOWN);

		elevator.userHasExited(null, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 5, DOWN);

		elevator.call(80, Direction.DOWN);

		assertManyCommands(elevator, 15, UP);
		assertCommands(elevator, OPEN_DOWN);

		elevator.userHasEntered(null, 0);
		elevator.go(30, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 50, DOWN);
		assertCommands(elevator, OPEN_UP);

		elevator.userHasExited(null, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 30, UP);
		assertCommands(elevator, OPEN_UP);

		elevator.userHasExited(null, 0);

		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldNotCareOfPointsWhenFull() {
		ElevatorEngine elevator = new StateSmartElevator();

		elevator.reset(0, 100, 2, 1, "");
		elevator.call(50, Direction.UP);
		elevator.call(50, Direction.UP);

		assertManyCommands(elevator, 50, UP);
		assertCommands(elevator, OPEN_UP);

		elevator.userHasEntered(null, 0);
		elevator.go(80, 0);
		elevator.userHasEntered(null, 0);
		elevator.go(80, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 20, UP);

		elevator.call(75, Direction.UP);

		assertManyCommands(elevator, 10, UP);
		assertCommands(elevator, OPEN_DOWN);

		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 5, DOWN);
		assertCommands(elevator, OPEN_UP);

		elevator.userHasEntered(null, 0);
		elevator.go(90, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 15, UP);
		assertCommands(elevator, OPEN_UP);

		elevator.userHasExited(null, 0);

		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldCareOfOpenDirection() {
		ElevatorEngine elevator = new StateSmartElevator();

		elevator.reset(0, 100, 3, 1, "");
		elevator.call(50, Direction.UP);

		assertManyCommands(elevator, 50, UP);
		assertCommands(elevator, OPEN_UP);

		elevator.userHasEntered(null, 0);
		elevator.go(80, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 20, UP);

		elevator.call(75, Direction.DOWN);
		elevator.call(75, Direction.DOWN);
		elevator.call(75, Direction.UP);
		elevator.call(75, Direction.UP);

		assertManyCommands(elevator, 5, UP);
		assertCommands(elevator, OPEN_UP);

		elevator.userHasEntered(null, 0);
		elevator.go(80, 0);
		elevator.userHasEntered(null, 0);
		elevator.go(80, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 5, UP);
		assertCommands(elevator, OPEN_DOWN);

		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 5, DOWN);
		assertCommands(elevator, OPEN_DOWN);

		elevator.userHasEntered(null, 0);
		elevator.go(70, 0);
		elevator.userHasEntered(null, 0);
		elevator.go(70, 0);

		assertCommands(elevator, CLOSE);
		assertManyCommands(elevator, 5, DOWN);
		assertCommands(elevator, OPEN_DOWN);

		elevator.userHasExited(null, 0);
		elevator.userHasExited(null, 0);

		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldMoveSecondCabinToCall() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.reset(-2, 8, 10, 2, null);

		elevator.call(3, Direction.UP);
		assertCommands(elevator, asList(NOTHING, UP), asList(NOTHING, UP), asList(NOTHING, UP), asList(NOTHING, OPEN_UP));

		elevator.userHasEntered(null, 1);
		elevator.go(5, 1);
		assertCommands(elevator, asList(NOTHING, CLOSE), asList(NOTHING, UP), asList(NOTHING, UP), asList(NOTHING, OPEN_UP));

		elevator.userHasExited(null, 1);
		assertCommands(elevator, asList(NOTHING, CLOSE), asList(NOTHING, NOTHING));
	}

	@Test
	public void shouldMoveCabinsToCalls() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.reset(-2, 8, 10, 2, null);

		elevator.call(3, Direction.UP);
		assertCommands(elevator, asList(NOTHING, UP), asList(NOTHING, UP));

		elevator.call(-1, Direction.UP);
		assertCommands(elevator, asList(DOWN, UP), asList(OPEN_UP, OPEN_UP));

		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		elevator.userHasEntered(null, 1);
		elevator.go(5, 1);
		assertCommands(elevator, asList(CLOSE, CLOSE), asList(UP, UP), asList(UP, UP), asList(UP, OPEN_UP));

		elevator.userHasExited(null, 1);
		assertCommands(elevator, asList(UP, CLOSE), asList(OPEN_UP, NOTHING));

		elevator.userHasExited(null, 0);
		assertCommands(elevator, asList(CLOSE, NOTHING), asList(NOTHING, NOTHING));
	}

	@Test
	public void shouldOpenSecondCabinsToCallerDirection() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.reset(-2, 8, 10, 2, null);

		elevator.call(3, Direction.DOWN);
		assertCommands(elevator, asList(NOTHING, UP), asList(NOTHING, UP), asList(NOTHING, UP), asList(NOTHING, OPEN_DOWN));

		elevator.userHasEntered(null, 1);
		elevator.go(-2, 1);
		assertCommands(elevator, asList(NOTHING, CLOSE));
		assertManyCommands(elevator, 4, asList(NOTHING, DOWN));

		elevator.call(-2, Direction.UP);
		elevator.call(8, Direction.DOWN);
		assertCommands(elevator, asList(DOWN, DOWN), asList(DOWN, OPEN_UP));

		elevator.userHasExited(null, 1);
		elevator.userHasEntered(null, 1);
		elevator.go(-1, 1);
		assertCommands(elevator, asList(NOTHING, CLOSE), asList(NOTHING, UP), asList(NOTHING, OPEN_UP));

		elevator.userHasExited(null, 1);
		assertCommands(elevator, asList(NOTHING, CLOSE));
		assertManyCommands(elevator, 9, asList(NOTHING, UP));
		assertCommands(elevator, asList(NOTHING, OPEN_DOWN));

		elevator.userHasEntered(null, 1);
		elevator.go(7, 1);
		assertCommands(elevator, asList(NOTHING, CLOSE), asList(NOTHING, DOWN), asList(NOTHING, OPEN_DOWN));

		elevator.userHasExited(null, 1);
		assertCommands(elevator, asList(NOTHING, CLOSE), asList(NOTHING, NOTHING));
	}

	@Test
	public void shouldOpenCabinsToCallerDirections() {
		ElevatorEngine elevator = new StateSmartElevator();
		elevator.reset(-2, 8, 10, 2, null);

		elevator.call(1, Direction.UP);
		elevator.call(4, Direction.DOWN);
		elevator.call(8, Direction.DOWN);
		assertCommands(elevator, asList(UP, UP), asList(OPEN_UP, UP));

		elevator.userHasEntered(null, 0);
		elevator.go(4, 0);
		assertCommands(elevator, asList(CLOSE, UP));
		assertManyCommands(elevator, 3, asList(UP, UP));
		assertCommands(elevator, asList(OPEN_DOWN, UP));

		elevator.userHasExited(null, 0);
		elevator.userHasEntered(null, 0);
		elevator.go(3, 0);
		assertCommands(elevator, asList(CLOSE, UP), asList(DOWN, OPEN_DOWN));

		elevator.userHasEntered(null, 1);
		elevator.go(7, 1);
		assertCommands(elevator, asList(OPEN_DOWN, CLOSE));

		elevator.userHasExited(null, 0);
		assertCommands(elevator, asList(CLOSE, DOWN), asList(NOTHING, OPEN_DOWN));

		elevator.userHasExited(null, 1);
		assertCommands(elevator, asList(NOTHING, CLOSE), asList(NOTHING, NOTHING));
	}

}