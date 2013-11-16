package org.n10.codestory.s03e01.core;

import java.util.LinkedList;
import java.util.Queue;
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
        reset(ElevatorEngine.LOWER_FLOOR, ElevatorEngine.HIGHER_FLOOR, ElevatorEngine.CABIN_SIZE, "Init");
    }

    @Override
    public Command nextCommand() throws ElevatorIsBrokenException {
        System.out.println(state.printState());

        Command currentCommand = state.nextCommand;

        if (currentCommand == Command.CLOSE) {
            state.clearTraveling();
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

        if (currentCommand == Command.NOTHING && state.willDoSomething()) {
            currentCommand = nextCommand();
        }
        return currentCommand;
    }

    @Override
    public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException {
        Queue<Direction> queue = state.waitingTargets.get(atFloor);
        if (queue == null) {
            queue = new LinkedList<>();
            state.waitingTargets.put(atFloor, queue);
        }
        queue.add(to);
        return this;
    }

    @Override
    public ElevatorEngine go(Integer floorToGo) throws ElevatorIsBrokenException {
        state.travelingTargets.add(new Target(floorToGo, null));
        return this;
    }

    @Override
    public ElevatorEngine userHasEntered(User user) throws ElevatorIsBrokenException {
        state.popWaiting();
        return this;
    }

    @Override
    public ElevatorEngine userHasExited(User user) throws ElevatorIsBrokenException {
        return this;
    }

    @Override
    public ElevatorEngine reset(Integer lowerFloor, Integer higherFloor, Integer cabinSize, String cause) throws ElevatorIsBrokenException {
        ElevatorState newState = new ElevatorState(lowerFloor, higherFloor, cabinSize);
        if (state != null) {
            newState.targetThreshold = state.targetThreshold;
        }
        state = newState;
        return this;
    }

    @Override
    public ElevatorEngine limit(Integer limit) {
        state.targetThreshold = limit;
        return this;
    }
}
