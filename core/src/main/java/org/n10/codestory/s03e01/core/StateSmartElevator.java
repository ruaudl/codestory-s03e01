package org.n10.codestory.s03e01.core;

import org.n10.codestory.s03e01.api.BuildingState;
import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.api.ElevatorIsBrokenException;
import org.n10.codestory.s03e01.api.ElevatorState;
import org.n10.codestory.s03e01.api.User;

public class StateSmartElevator implements ElevatorEngine {

	private ElevatorState elevator;
	private BuildingState building;

	public StateSmartElevator() {
		reset(ElevatorEngine.LOWER_FLOOR, ElevatorEngine.HIGHER_FLOOR, ElevatorEngine.CABIN_SIZE, ElevatorEngine.CABIN_COUNT, "Init");
	}

	@Override
	public Command nextCommand() throws ElevatorIsBrokenException {
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
		building.tickUsers();
		// System.out.println(String.format("Command %s returned with states:\n%s",
		// command, this));
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
		User user = building.popUser(elevator.floor, elevator.direction);
		user.setFloorToGo(floorToGo);
		user.travels();
		System.out.println(String.format("User has entered: %s", user));
		elevator.pushUser(user);
		return this;
	}

	@Override
	public ElevatorEngine userHasEntered(User user, Integer cabin) throws ElevatorIsBrokenException {
		elevator.travelersCount++;
		return this;
	}

	@Override
	public ElevatorEngine userHasExited(User user, Integer cabin) throws ElevatorIsBrokenException {
		User traveler = elevator.popUser();
		traveler.arrived();
		System.out.println(String.format("User has exited: %s", traveler));
		elevator.travelersCount--;
		return this;
	}

	@Override
	public ElevatorEngine reset(Integer lowerFloor, Integer higherFloor, Integer cabinSize, Integer cabinCount, String cause) throws ElevatorIsBrokenException {
		building = new BuildingState(lowerFloor, higherFloor);
		ElevatorState newState = new ElevatorState(building, cabinSize);
		if (elevator != null) {
			newState.targetThreshold = elevator.targetThreshold;
		}
		elevator = newState;
		return this;
	}

	@Override
	public ElevatorEngine limit(Integer limit) {
		elevator.targetThreshold = limit;
		return this;
	}

	@Override
	public String getStatus() {
		return elevator.getStatus();
	}

	@Override
	public String toString() {
		return String.format("E=%s\nB=%s", elevator.toString(), building.toString());
	}
}
