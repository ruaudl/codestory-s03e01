package org.n10.codestory.s03e01.api;

import java.util.LinkedList;
import java.util.Queue;
import static org.fest.assertions.Assertions.*;
import org.junit.Before;
import org.junit.Test;

public class ElevatorStateTest {

	private ElevatorState elevatorState;

	@Before
	public void setUp() {
		elevatorState = new ElevatorState();
	}

	@Test
	public void testHasWaitingTargetAhead() {
		Queue<User> queue = new LinkedList<>();
		queue.add(new User(Direction.UP));
		elevatorState.waitingTargets.put(4, queue);
		assertThat(elevatorState.hasTargetsAhead()).isTrue();
	}
	
	@Test
	public void testHasTravelingTargetAhead() {
		Queue<User> queue = new LinkedList<>();
		queue.add(new User(Direction.DOWN));
		elevatorState.travelingTargets.put(4, queue);
		assertThat(elevatorState.hasTargetsAhead()).isTrue();
	}

	@Test
	public void testHasntWaitingTargetAhead() {
		Queue<User> queue = new LinkedList<>();
		queue.add(new User(Direction.UP));
		elevatorState.waitingTargets.put(4, queue);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsAhead()).isFalse();
	}
	
	@Test
	public void testHasntTravelingTargetAhead() {
		Queue<User> queue = new LinkedList<>();
		queue.add(new User(Direction.DOWN));
		elevatorState.travelingTargets.put(4, queue);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsAhead()).isFalse();
	}

	@Test
	public void testHasTravelingTargetBehind() {
		Queue<User> queue = new LinkedList<>();
		queue.add(new User(Direction.DOWN));
		elevatorState.travelingTargets.put(4, queue);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsBehind()).isTrue();
	}
	
	@Test
	public void testHasWaitingTargetBehind() {
		Queue<User> queue = new LinkedList<>();
		queue.add(new User(Direction.UP));
		elevatorState.waitingTargets.put(4, queue);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsBehind()).isTrue();
	}

	@Test
	public void testHasWaitingTargetBehindSameDirection() {
		Queue<User> queue = new LinkedList<>();
		queue.add(new User(Direction.DOWN));
		elevatorState.waitingTargets.put(4, queue);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsBehind()).isTrue();
	}
	
	@Test
	public void testHasntWaitingTargetBehind() {
		Queue<User> queue = new LinkedList<>();
		queue.add(new User(Direction.UP));
		elevatorState.waitingTargets.put(4, queue);
		assertThat(elevatorState.hasTargetsBehind()).isFalse();
	}
	
	@Test
	public void testHasntTargetBehind() {
		Queue<User> queue = new LinkedList<>();
		queue.add(new User(Direction.UP));
		elevatorState.waitingTargets.put(4, queue);
		assertThat(elevatorState.hasTargetsBehind()).isFalse();
	}

	@Test
	public void limitIsOk() {
		ElevatorState elevator = new ElevatorState(0, 19, 42);
		assertThat(elevator.targetThreshold).isEqualTo(6);
	}
}