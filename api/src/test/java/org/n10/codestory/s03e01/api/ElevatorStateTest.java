package org.n10.codestory.s03e01.api;

import static org.fest.assertions.Assertions.*;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;

public class ElevatorStateTest {

	private ElevatorState elevatorState;
	private BuildingState buildingState;

	@Before
	public void setUp() {
		buildingState = new BuildingState();
		elevatorState = new ElevatorState(buildingState);
	}

	@Test
	public void testHasWaitingTargetAhead() {
		buildingState.pushUser(new User(Direction.UP, 4), 4);
		assertThat(elevatorState.hasTargetsAhead()).isTrue();
	}

	@Test
	public void testHasTravelingTargetAhead() {
		elevatorState.pushUser(new User(Direction.DOWN, 5), 4);
		assertThat(elevatorState.hasTargetsAhead()).isTrue();
	}

	@Test
	public void testHasntWaitingTargetAhead() {
		buildingState.pushUser(new User(Direction.UP, 4), 4);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsAhead()).isFalse();
	}

	@Test
	public void testHasntTravelingTargetAhead() {
		Queue<User> queue = new LinkedList<>();
		queue.add(new User(Direction.DOWN, 6));
		elevatorState.targets.put(4, queue);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsAhead()).isFalse();
	}

	@Test
	public void testHasTravelingTargetBehind() {
		Queue<User> queue = new LinkedList<>();
		queue.add(new User(Direction.DOWN, 6));
		elevatorState.targets.put(4, queue);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsBehind()).isTrue();
	}

	@Test
	public void testHasWaitingTargetBehind() {
		buildingState.pushUser(new User(Direction.UP, 4), 4);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsBehind()).isTrue();
	}

	@Test
	public void testHasWaitingTargetBehindSameDirection() {
		buildingState.pushUser(new User(Direction.DOWN, 4), 4);
		elevatorState.floor = 5;
		assertThat(elevatorState.hasTargetsBehind()).isTrue();
	}

	@Test
	public void testHasntWaitingTargetBehind() {
		buildingState.pushUser(new User(Direction.UP, 4), 4);
		assertThat(elevatorState.hasTargetsBehind()).isFalse();
	}

	@Test
	public void testHasntTargetBehind() {
		buildingState.pushUser(new User(Direction.UP, 4), 4);
		assertThat(elevatorState.hasTargetsBehind()).isFalse();
	}

	@Test
	public void testSecondShouldOpen() {
		elevatorState.cabinSize = 2;
		elevatorState.travelersCount = 1;
		buildingState.pushUser(new User(Direction.DOWN, 0), 0);
		buildingState.pushUser(new User(Direction.UP, 0), 0);
		buildingState.pushUser(new User(Direction.UP, 0), 1);
		assertThat(elevatorState.shouldOpen()).isTrue();
	}

	@Test
	public void testOnlyFirstShouldOpen() {
		elevatorState.cabinSize = 2;
		elevatorState.travelersCount = 1;
		buildingState.pushUser(new User(Direction.UP, 0), 0);
		buildingState.pushUser(new User(Direction.DOWN, 0), 0);
		buildingState.pushUser(new User(Direction.UP, 0), 1);
		assertThat(elevatorState.shouldOpen()).isTrue();
	}

	@Test
	public void limitIsOk() {
		ElevatorState elevator = new ElevatorState(new BuildingState(0, 19), 42);
		assertThat(elevator.targetThreshold).isEqualTo(6);
	}
}