package org.n10.codestory.s03e01.api;

import static org.fest.assertions.Assertions.*;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;

public class ElevatorStateTest {

	private ElevatorState elevatorState;

	@Before
	public void setUp() {
		elevatorState = new ElevatorState();
	}

	@Test
	public void testHasTargetAhead() {
		Queue<Direction> queue = new LinkedList<>();
		queue.add(Direction.UP);
		elevatorState.waitingTargets.put(4, queue);
		assertThat(elevatorState.hasTargetsAhead()).isTrue();
	}

	@Test
	public void testHasntTargetAhead() {
		Queue<Direction> queue = new LinkedList<>();
		queue.add(Direction.UP);
		elevatorState.waitingTargets.put(4, queue);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsAhead()).isFalse();
	}

	@Test
	public void testHasTargetBehind() {
		Queue<Direction> queue = new LinkedList<>();
		queue.add(Direction.UP);
		elevatorState.waitingTargets.put(4, queue);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsBehind()).isTrue();
	}

	@Test
	public void testHasTargetBehindSameDirection() {
		Queue<Direction> queue = new LinkedList<>();
		queue.add(Direction.DOWN);
		elevatorState.waitingTargets.put(4, queue);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsBehind()).isTrue();
	}

	@Test
	public void testHasntTargetBehind() {
		Queue<Direction> queue = new LinkedList<>();
		queue.add(Direction.UP);
		elevatorState.waitingTargets.put(4, queue);
		assertThat(elevatorState.hasTargetsBehind()).isFalse();
	}
}