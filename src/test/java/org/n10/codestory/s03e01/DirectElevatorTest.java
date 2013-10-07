package org.n10.codestory.s03e01;

import static elevator.Command.*;
import static org.n10.codestory.s03e01.ElevatorAssert.*;

import org.junit.Test;

import elevator.Direction;
import elevator.engine.ElevatorEngine;

public class DirectElevatorTest {

	@Test
	public void shouldDoNothingAtStart() {
		ElevatorEngine elevator = new ListDirectElevator();
		assertCommands(elevator, NOTHING, NOTHING, NOTHING, NOTHING);
	}
	
	@Test
	public void shouldGoToCall() {
		ElevatorEngine elevator = new ListDirectElevator();
		elevator.call(4, Direction.UP);
		assertCommands(elevator, UP, UP, UP, UP, OPEN, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoToGo() {
		ElevatorEngine elevator = new ListDirectElevator();
		elevator.go(2);
		assertCommands(elevator, UP, UP, OPEN, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoThroughTheWholeList() {
		ElevatorEngine elevator = new ListDirectElevator();
		elevator.call(4, Direction.UP);
		elevator.call(2, Direction.DOWN);
		elevator.call(3, Direction.DOWN);
		assertCommands(elevator, UP, UP, UP, UP, OPEN);
		elevator.go(5);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN);
		elevator.go(0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.go(0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		assertCommands(elevator, CLOSE, DOWN, DOWN, DOWN, DOWN, DOWN, OPEN);
		assertCommands(elevator, CLOSE, OPEN, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoThroughTheWholeSet() {
		ElevatorEngine elevator = new SetDirectElevator();
		elevator.call(4, Direction.UP);
		elevator.call(2, Direction.DOWN);
		elevator.call(3, Direction.DOWN);
		assertCommands(elevator, UP, UP, UP, UP, OPEN);
		elevator.go(5);
		assertCommands(elevator, CLOSE, DOWN, DOWN, OPEN);
		elevator.go(0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.go(0);
		assertCommands(elevator, CLOSE, UP, UP, OPEN);
		assertCommands(elevator, CLOSE, DOWN, DOWN, DOWN, DOWN, DOWN, OPEN);
		assertCommands(elevator, CLOSE, NOTHING);
	}

	@Test
	public void shouldGoThroughTheWholeTreeSet() {
		ElevatorEngine elevator = new TreeSetDirectElevator();
		elevator.call(4, Direction.UP);
		elevator.call(2, Direction.DOWN);
		elevator.call(3, Direction.DOWN);
		assertCommands(elevator, UP, UP, OPEN);
		elevator.go(0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.go(0);
		assertCommands(elevator, CLOSE, UP, OPEN);
		elevator.go(5);
		assertCommands(elevator, CLOSE, UP, OPEN);
		assertCommands(elevator, CLOSE, DOWN, DOWN, DOWN, DOWN, DOWN, OPEN);
		assertCommands(elevator, CLOSE, NOTHING);
	}
}