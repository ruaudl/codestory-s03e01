package org.n10.codestory.s03e01.api;

import java.util.List;

public interface ElevatorEngine {

	final static Integer LOWER_FLOOR = 0;
	final static Integer HIGHER_FLOOR = 19;
	final static Integer CABIN_SIZE = 42;
	final static Integer CABIN_COUNT = 2;

	ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException;

	ElevatorEngine go(Integer floorToGo, Integer cabin) throws ElevatorIsBrokenException;

	List<Command> nextCommand() throws ElevatorIsBrokenException;

	ElevatorEngine userHasEntered(User user, Integer cabin) throws ElevatorIsBrokenException;

	ElevatorEngine userHasExited(User user, Integer cabin) throws ElevatorIsBrokenException;

	ElevatorEngine reset(Integer lowerFloor, Integer higherFloor, Integer cabinSize, Integer cabinCount, String cause) throws ElevatorIsBrokenException;

	ElevatorEngine limit(Integer i);

	String getStatus();

}
