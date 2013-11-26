package org.n10.codestory.s03e01.api;

public interface ElevatorEngine {

	public final static Integer LOWER_FLOOR = 0;
	public final static Integer HIGHER_FLOOR = 19;
	public final static Integer CABIN_SIZE = 42;
	public final static Integer CABIN_COUNT = 2;

	public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException;

	public ElevatorEngine go(Integer floorToGo, Integer cabin) throws ElevatorIsBrokenException;

	public Command nextCommand() throws ElevatorIsBrokenException;

	public ElevatorEngine userHasEntered(User user, Integer cabin) throws ElevatorIsBrokenException;

	public ElevatorEngine userHasExited(User user, Integer cabin) throws ElevatorIsBrokenException;

	public ElevatorEngine reset(Integer lowerFloor, Integer higherFloor, Integer cabinSize, Integer cabinCount, String cause) throws ElevatorIsBrokenException;

	public ElevatorEngine limit(Integer i);

	public String printState();

}
