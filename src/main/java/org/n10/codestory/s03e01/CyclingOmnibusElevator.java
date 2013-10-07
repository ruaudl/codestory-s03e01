package org.n10.codestory.s03e01;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import elevator.Command;
import elevator.Direction;
import elevator.User;
import elevator.engine.ElevatorEngine;
import elevator.exception.ElevatorIsBrokenException;

public class CyclingOmnibusElevator implements ElevatorEngine {

	private Iterator<Command> cycle = Iterators.cycle(Command.UP, Command.OPEN, Command.CLOSE, Command.UP, Command.OPEN, Command.CLOSE, Command.UP, Command.OPEN, Command.CLOSE, Command.UP, Command.OPEN,
			Command.CLOSE, Command.UP, Command.OPEN, Command.CLOSE, Command.DOWN, Command.OPEN, Command.CLOSE, Command.DOWN, Command.OPEN, Command.CLOSE, Command.DOWN, Command.OPEN, Command.CLOSE,
			Command.DOWN, Command.OPEN, Command.CLOSE, Command.DOWN, Command.OPEN, Command.CLOSE);

	@Override
	public Command nextCommand() throws ElevatorIsBrokenException {
		return cycle.next();
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
	public ElevatorEngine reset(String cause) throws ElevatorIsBrokenException {
		return this;
	}

}
