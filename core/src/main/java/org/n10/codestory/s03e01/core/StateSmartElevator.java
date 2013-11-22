package org.n10.codestory.s03e01.core;

import java.util.LinkedList;
import java.util.Queue;
import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.api.ElevatorIsBrokenException;
import org.n10.codestory.s03e01.api.ElevatorState;
import org.n10.codestory.s03e01.api.Target;
import org.n10.codestory.s03e01.api.User;

public class StateSmartElevator implements ElevatorEngine {

	private ElevatorState state;

	public StateSmartElevator() {
		reset(ElevatorEngine.LOWER_FLOOR, ElevatorEngine.HIGHER_FLOOR, ElevatorEngine.CABIN_SIZE, "Init");
	}

	@Override
	public Command nextCommand() throws ElevatorIsBrokenException {
		Command currentCommand = state.nextCommand;

//		if (currentCommand == Command.CLOSE) {
//			state.clearTraveling();
//		}

		if (state.willOpen()) {
			state.doClose();
		} else if (state.shouldOpen()) {
			state.doOpen();
		} else if (state.hasTargetsAhead()) {
			state.doContinue();
		} else if (state.hasTargetsBehind()) {
			state.doReverse();
		} else {
			state.doNothing();
		}

		if (currentCommand == Command.NOTHING && state.willDoSomething()) {
			currentCommand = nextCommand();
		}
		return currentCommand;
	}

	@Override
	public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException {
		Queue<User> queue = state.waitingTargets.get(atFloor);
		if (queue == null) {
			queue = new LinkedList<>();
			state.waitingTargets.put(atFloor, queue);
		}
		queue.add(new User(to));
		return this;
	}

	@Override
	public ElevatorEngine go(Integer floorToGo) throws ElevatorIsBrokenException {
		Queue<User> queue = state.travelingTargets.get(floorToGo);
		if (queue == null) {
			queue = new LinkedList<>();
			state.travelingTargets.put(floorToGo, queue);
		}
		
		Direction direction = Direction.UP;
		if (floorToGo < state.floor) {
			direction = Direction.DOWN;
		}
		queue.add(new User(direction));
		return this;
	}

	@Override
	public ElevatorEngine userHasEntered(User user) throws ElevatorIsBrokenException {
		state.popWaiting();
		state.currentTravelersNb++;
		return this;
	}

	@Override
	public ElevatorEngine userHasExited(User user) throws ElevatorIsBrokenException {
		state.popTraveling();
		state.currentTravelersNb--;
		return this;
	}

	@Override
	public ElevatorEngine reset(Integer lowerFloor, Integer higherFloor, Integer cabinSize, String cause) throws ElevatorIsBrokenException {
		ElevatorState newState = new ElevatorState(lowerFloor, higherFloor, cabinSize);
		if (state != null) {
			newState.targetThreshold = state.targetThreshold;
		}
		state = newState;
		return this;
	}

	@Override
	public ElevatorEngine limit(Integer limit) {
		state.targetThreshold = limit;
		return this;
	}

	@Override
	public String printState() {
		return state.printState();
	}
}
