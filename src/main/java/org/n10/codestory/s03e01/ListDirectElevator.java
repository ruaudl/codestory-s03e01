package org.n10.codestory.s03e01;

import java.util.ArrayList;
import java.util.List;

import elevator.Command;
import elevator.Direction;
import elevator.User;
import elevator.engine.ElevatorEngine;
import elevator.exception.ElevatorIsBrokenException;

public class ListDirectElevator implements ElevatorEngine {

	private int floor;
	private Command nextCommand;
	private List<Integer> targets;

	public ListDirectElevator() {
		reset("Init");
	}

	@Override
	public Command nextCommand() throws ElevatorIsBrokenException {
		Command currentCommand = nextCommand;

		if (nextCommand == Command.OPEN) {
			nextCommand = Command.CLOSE;
		} else if (!targets.isEmpty()) {
			Integer diff = targets.get(0) - floor;
			if (diff > 0) {
				nextCommand = Command.UP;
				floor++;
			} else if (diff < 0) {
				nextCommand = Command.DOWN;
				floor--;
			} else {
				targets.remove(0);
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
		targets = new ArrayList<Integer>();
		return this;
	}

}
