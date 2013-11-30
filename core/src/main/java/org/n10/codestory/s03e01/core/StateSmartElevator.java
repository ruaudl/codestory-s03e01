package org.n10.codestory.s03e01.core;

import java.util.LinkedList;
import java.util.Queue;

import org.n10.codestory.s03e01.api.BuildingState;
import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.api.ElevatorIsBrokenException;
import org.n10.codestory.s03e01.api.ElevatorState;
import org.n10.codestory.s03e01.api.User;

public class StateSmartElevator implements ElevatorEngine {

	private ElevatorState state;

	public StateSmartElevator() {
		reset(ElevatorEngine.LOWER_FLOOR, ElevatorEngine.HIGHER_FLOOR, ElevatorEngine.CABIN_SIZE, ElevatorEngine.CABIN_COUNT, "Init");
	}

	@Override
	public Command nextCommand() throws ElevatorIsBrokenException {
		Command command = Command.NOTHING;

		if (state.isOpen()) {
			command = state.doClose();
		} else if (state.shouldOpen()) {
			command = state.doOpen();
		} else if (state.hasTargetsAhead()) {
			command = state.doContinue();
		} else if (state.hasTargetsBehind()) {
			command = state.doReverse();
		}

		state.tick();
		return command;
	}

	@Override
	public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException {
		Queue<User> queue = state.buildingState.targets.get(atFloor);
		if (queue == null) {
			queue = new LinkedList<>();
			state.buildingState.targets.put(atFloor, queue);
		}
		queue.add(new User(to, atFloor));
		return this;
	}

	@Override
	public ElevatorEngine go(Integer floorToGo, Integer cabin) throws ElevatorIsBrokenException {
		User user = state.popWaiting();
		user.setFloorToGo(floorToGo);
		user.travels();
		Queue<User> queue = state.targets.get(floorToGo);
		if (queue == null) {
			queue = new LinkedList<>();
			state.targets.put(floorToGo, queue);
		}

		queue.add(user);
		return this;
	}

	@Override
	public ElevatorEngine userHasEntered(User user, Integer cabin) throws ElevatorIsBrokenException {

		state.currentTravelersNb++;
		return this;
	}

	@Override
	public ElevatorEngine userHasExited(User user, Integer cabin) throws ElevatorIsBrokenException {
		User traveler = state.popTraveling();
		traveler.arrived();
		System.out.println(String.format("User entered at %s, exited at %s, earned %s points", traveler.getInitialFloor(), traveler.getFloorToGo(),
				traveler.getRemainingPoints()));
		state.currentTravelersNb--;
		return this;
	}

	@Override
	public ElevatorEngine reset(Integer lowerFloor, Integer higherFloor, Integer cabinSize, Integer cabinCount, String cause) throws ElevatorIsBrokenException {
		ElevatorState newState = new ElevatorState(lowerFloor, higherFloor, cabinSize, new BuildingState());
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
