package org.n10.codestory.s03e01.core;

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
		reset(ElevatorEngine.LOWER_FLOOR, ElevatorEngine.HIGHER_FLOOR, "Init");
	}

	@Override
	public Command nextCommand() throws ElevatorIsBrokenException {
		Command currentCommand = state.nextCommand;

		if (currentCommand == Command.CLOSE) {
			state.clearFloor();
		}

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
	public ElevatorEngine reset(Integer lowerFloor, Integer higherFloor, String cause) throws ElevatorIsBrokenException {
		state = new ElevatorState(lowerFloor, higherFloor);
		return this;
	}

}
