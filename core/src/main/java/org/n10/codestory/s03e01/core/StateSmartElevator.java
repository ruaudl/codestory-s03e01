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
		return command;
	}

	@Override
	public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException {
		building.pushUser(new User(to, atFloor));
		return this;
	}

	@Override
	public ElevatorEngine go(Integer floorToGo, Integer cabin) throws ElevatorIsBrokenException {
		User user = building.popUser(elevator.floor);
		user.setFloorToGo(floorToGo);
		user.travels();
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
		System.out.println(String.format("User entered at %s, exited at %s, gave %s points", traveler.getInitialFloor(), traveler.getFloorToGo(),
				traveler.getRemainingPoints()));
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
	public String printState() {
		return elevator.printState();
	}
}
