package org.n10.codestory.s03e01;

import java.util.LinkedHashSet;

import elevator.Command;
import elevator.Direction;
import elevator.User;
import elevator.engine.ElevatorEngine;
import elevator.exception.ElevatorIsBrokenException;

public class SetDirectElevator implements ElevatorEngine {

	private int floor;
	private Command nextCommand;
	private LinkedHashSet<Integer> targets;

	public SetDirectElevator() {
		reset("Init");
	}

	@Override
	public Command nextCommand() throws ElevatorIsBrokenException {
		Command currentCommand = nextCommand;

		if (nextCommand == Command.OPEN) {
			nextCommand = Command.CLOSE;
		} else if (!targets.isEmpty()) {
			Integer diff = targets.iterator().next() - floor;
			if (diff > 0) {
				nextCommand = Command.UP;
				floor++;
			} else if (diff < 0) {
				nextCommand = Command.DOWN;
				floor--;
			} else {
				targets.remove(targets.iterator().next());
				nextCommand = Command.OPEN;
			}
			if (currentCommand == Command.NOTHING) {
				currentCommand = nextCommand();
			}
		} else {
			nextCommand = Command.NOTHING;
		}

		return currentCommand;
	}

	@Override
	public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException {
		targets.add(atFloor);
		return this;
	}

	@Override
	public ElevatorEngine go(Integer floorToGo) throws ElevatorIsBrokenException {
		targets.add(floorToGo);
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
		floor = 0;
		nextCommand = Command.NOTHING;
		targets = new LinkedHashSet<Integer>();
		return this;
	}

}
