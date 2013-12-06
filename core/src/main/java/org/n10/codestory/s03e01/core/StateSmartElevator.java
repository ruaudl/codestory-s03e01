package org.n10.codestory.s03e01.core;

import java.util.ArrayList;
import java.util.List;

import org.n10.codestory.s03e01.api.BuildingState;
import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.api.ElevatorIsBrokenException;
import org.n10.codestory.s03e01.api.ElevatorState;
import org.n10.codestory.s03e01.api.User;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class StateSmartElevator implements ElevatorEngine {

	private List<ElevatorState> elevators;
	private BuildingState building;

	public StateSmartElevator() {
		reset(ElevatorEngine.LOWER_FLOOR, ElevatorEngine.HIGHER_FLOOR, ElevatorEngine.CABIN_SIZE, ElevatorEngine.CABIN_COUNT, "Init");
	}

	@Override
	public List<Command> nextCommand() throws ElevatorIsBrokenException {
		List<Command> commands = Lists.transform(elevators, new Function<ElevatorState, Command>() {
			@Override
			public Command apply(ElevatorState input) {
				return nextCommand(input);
			}
		});
		building.tickUsers();
		// System.out.println(String.format("Command %s returned with states:\n%s",
		// command, this));
		return commands;
	}

	private Command nextCommand(ElevatorState elevator) {
		Command command = Command.NOTHING;

		if (elevator.isOpen()) {
			command = elevator.doClose();
		} else if (elevator.shouldOpen()) {
			command = elevator.doOpen();
		} else if (elevator.hasTargetsAhead()) {
			command = elevator.doContinue();
		} else if (elevator.hasTargetsBehind()) {
			command = elevator.doReverse();
		}

		elevator.tickUsers();
		return command;
	}

	@Override
	public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException {
		User user = new User(to, atFloor);
		building.pushUser(user);
		System.out.println(String.format("User has called: %s", user));
		return this;
	}

	@Override
	public ElevatorEngine go(Integer floorToGo, Integer cabin) throws ElevatorIsBrokenException {
		ElevatorState elevator = elevators.get(cabin);
		User user = building.popUser(elevator.floor, elevator.direction);
		user.setFloorToGo(floorToGo);
		user.travels();
		System.out.println(String.format("User has entered: %s", user));
		elevator.pushUser(user);
		return this;
	}

	@Override
	public ElevatorEngine userHasEntered(User user, Integer cabin) throws ElevatorIsBrokenException {
		elevators.get(cabin).travelersCount++;
		return this;
	}

	@Override
	public ElevatorEngine userHasExited(User user, Integer cabin) throws ElevatorIsBrokenException {
		ElevatorState elevator = elevators.get(cabin);
		User traveler = elevator.popUser();
		traveler.arrived();
		System.out.println(String.format("User has exited: %s", traveler));
		elevator.travelersCount--;
		return this;
	}

	@Override
	public ElevatorEngine reset(Integer lowerFloor, Integer higherFloor, Integer cabinSize, Integer cabinCount, String cause) throws ElevatorIsBrokenException {
		building = new BuildingState(lowerFloor, higherFloor, cabinCount);
		List<ElevatorState> newElevators = new ArrayList<>(cabinCount);
		for (int i = 0; i < cabinCount; i++) {
			ElevatorState newState = new ElevatorState(building, cabinSize, i);
			if (elevators != null && i < elevators.size() && elevators.get(i) != null) {
				newState.targetThreshold = elevators.get(i).targetThreshold;
			}
			newElevators.add(newState);
		}
		elevators = newElevators;
		return this;
	}

	@Override
	public ElevatorEngine limit(Integer limit) {
		for (ElevatorState elevator : elevators) {
			elevator.targetThreshold = limit;
		}
		return this;
	}

	@Override
	public String getStatus() {
		StringBuilder builder = new StringBuilder();
		for (ElevatorState elevator : elevators) {
			builder.append(elevator.getStatus());
			builder.append(" | ");
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (ElevatorState elevator : elevators) {
			builder.append(String.format("E=%s\n", elevator.toString()));
		}
		return builder.append(String.format("B=%s", building.toString())).toString();
	}
}
