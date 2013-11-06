package org.n10.codestory.s03e01.api;


public interface ElevatorEngine {

    public final static Integer LOWER_FLOOR = 0;
    public final static Integer HIGHER_FLOOR = 19;

    public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException;

    public ElevatorEngine go(Integer floorToGo) throws ElevatorIsBrokenException;

    public Command nextCommand() throws ElevatorIsBrokenException;

    public ElevatorEngine userHasEntered(User user) throws ElevatorIsBrokenException;

    public ElevatorEngine userHasExited(User user) throws ElevatorIsBrokenException;

    public ElevatorEngine reset(Integer lowerFloor, Integer higherFloor, String cause) throws ElevatorIsBrokenException;

	public ElevatorEngine limit(Integer i);

}
