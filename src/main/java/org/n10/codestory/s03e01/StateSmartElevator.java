package org.n10.codestory.s03e01;

import elevator.Command;
import elevator.Direction;
import elevator.User;
import elevator.engine.ElevatorEngine;
import elevator.exception.ElevatorIsBrokenException;

public class StateSmartElevator implements ElevatorEngine {

	private ElevatorState state;


	public StateSmartElevator() {
		reset("Init");
	}

	@Override
	public Command nextCommand() throws ElevatorIsBrokenException {
		Command currentCommand = state.nextCommand;

		if (state.willOpen()) {
			state.doClose();
		} else if (state.shouldOpen()) {
			state.doOpen();
		} else if (state.hasTargetsAhead()){
			state.doContinue();
		} else if (state.hasTargetsBehind()) {
			state.doReverse();
		} else {
			state.doNothing();
		}
		
		if (currentCommand == Command.NOTHING && state.doSomething()) {
			currentCommand = nextCommand();
		}
		return currentCommand;
	}

	@Override
	public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException {
		state.targets.add(new Target(atFloor, to));
		return this;
	}

	@Override
	public ElevatorEngine go(Integer floorToGo) throws ElevatorIsBrokenException {
		state.targets.add(new Target(floorToGo, null));
		return this;
	}

	@Override
	public ElevatorEngine userHasEntered(User user) throws ElevatorIsBrokenException {
		return this;
	}

	@Override
	public ElevatorEngine userHasExited(User user) throws ElevatorIsBrokenException {
		return this;
	}
	
	@Override
	public ElevatorEngine reset(String cause) throws ElevatorIsBrokenException {
		state = new ElevatorState();
		return this;
	}

}
