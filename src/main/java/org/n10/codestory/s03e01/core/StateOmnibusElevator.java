package org.n10.codestory.s03e01.core;

import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.api.ElevatorIsBrokenException;
import org.n10.codestory.s03e01.api.User;

public class StateOmnibusElevator implements ElevatorEngine {

	private int floor;
	private Command nextCommand;
	private Direction direction;

	public StateOmnibusElevator() {
		reset(ElevatorEngine.LOWER_FLOOR, ElevatorEngine.HIGHER_FLOOR, "Init");
	}

	@Override
	public Command nextCommand() throws ElevatorIsBrokenException {
		Command currentCommand = nextCommand;

		if (nextCommand == Command.OPEN) {
			nextCommand = Command.CLOSE;
		} else if (nextCommand == Command.CLOSE) {
			if (floor == HIGHER_FLOOR) {
				direction = Direction.DOWN;
			} else if (floor == LOWER_FLOOR) {
				direction = Direction.UP;
			}
			switch (direction) {
			case UP:
				nextCommand = Command.UP;
				floor++;
				break;
			case DOWN:
				nextCommand = Command.DOWN;
				floor--;
				break;
			}
		} else {
			nextCommand = Command.OPEN;
		}

		return currentCommand;
	}

	@Override
	public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException {
		return this;
	}

	@Override
	public ElevatorEngine go(Integer floorToGo) throws ElevatorIsBrokenException {
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
		direction = Direction.UP;
		nextCommand = Command.OPEN;
		return this;
	}

}
