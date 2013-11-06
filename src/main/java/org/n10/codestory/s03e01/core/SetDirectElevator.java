package org.n10.codestory.s03e01.core;

import java.util.LinkedHashSet;

import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.api.ElevatorIsBrokenException;
import org.n10.codestory.s03e01.api.User;

public class SetDirectElevator implements ElevatorEngine {

	private int floor;
	private Command nextCommand;
	private LinkedHashSet<Integer> targets;

	public SetDirectElevator() {
		reset(ElevatorEngine.LOWER_FLOOR, ElevatorEngine.HIGHER_FLOOR, "Init");
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
	public ElevatorEngine reset(Integer lowerFloor, Integer higherFloor, String cause) throws ElevatorIsBrokenException {
		floor = 0;
		nextCommand = Command.NOTHING;
		targets = new LinkedHashSet<Integer>();
		return this;
	}

	@Override
	public ElevatorEngine limit(Integer i) {
		return this;
	}

}
