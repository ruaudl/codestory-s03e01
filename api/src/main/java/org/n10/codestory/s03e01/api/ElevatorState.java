package org.n10.codestory.s03e01.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.util.Map.Entry;
import java.util.Queue;

public class ElevatorState implements Cloneable {

    public int floor;
    public Command nextCommand;
    public Direction direction;
//	public Set<Target> waitingTargets;
    public Set<Target> travelingTargets;
    public Integer lowerFloor;
    public Integer higherFloor;
    public Integer targetThreshold;
    public Integer cabinSize;
    public Map<Integer, Queue<Direction>> waitingTargets = new HashMap<>();
    private Predicate<Target> equalsFloor = new Predicate<Target>() {
        public boolean apply(Target value) {
            return value.getFloor() == floor;
        }
    };
    private Predicate<Target> equalsDirection = new Predicate<Target>() {
        public boolean apply(Target value) {
            return value.getDirection() == null || value.getDirection() == direction;
        }
    };
    private Map<Direction, Predicate<Target>> isAhead = new HashMap<Direction, Predicate<Target>>();

    {
        isAhead.put(Direction.UP, new Predicate<Target>() {
            public boolean apply(Target value) {
                return value.getFloor() > floor;
            }
        });
        isAhead.put(Direction.DOWN, new Predicate<Target>() {
            public boolean apply(Target value) {
                return value.getFloor() < floor;
            }
        });
    }

    public ElevatorState() {
        floor = 0;
        nextCommand = Command.NOTHING;
        direction = Direction.UP;
        waitingTargets = new HashMap<>();
        travelingTargets = new HashSet<>();
        targetThreshold = -1;
        this.lowerFloor = ElevatorEngine.LOWER_FLOOR;
        this.higherFloor = ElevatorEngine.HIGHER_FLOOR;
        this.cabinSize = ElevatorEngine.CABIN_SIZE;
    }

    public ElevatorState(Integer lowerFloor, Integer higherFloor, Integer cabinSize) {
        this();
        this.lowerFloor = lowerFloor;
        this.higherFloor = higherFloor;
        this.cabinSize = cabinSize;
    }

    public boolean willOpen() {
        return nextCommand == Command.OPEN;
    }

    public boolean willDoSomething() {
        return nextCommand != Command.NOTHING;
    }

    private boolean hasTargets(final Direction direction) {
        boolean hasWaiting = Iterables.tryFind(waitingTargets.entrySet(), new Predicate<Entry<Integer, Queue<Direction>>>(){
            @Override
            public boolean apply(Entry<Integer, Queue<Direction>> entry) {
                boolean hasAhead = true;
                if(direction.equals(Direction.UP)) {
                    hasAhead = hasAhead && entry.getKey() > floor;
                } else if(direction.equals(Direction.DOWN)) {
                    hasAhead = hasAhead && entry.getKey() < floor;
                }
                return hasAhead && entry.getValue()!= null && !entry.getValue().isEmpty();
            }
        }).isPresent();
        return hasWaiting || Iterables.tryFind(Ordering.natural().sortedCopy(travelingTargets), isAhead.get(direction)).isPresent();
    }

    public boolean hasTargetsAhead() {
        return hasTargets(direction);
    }

    public boolean hasTargetsBehind() {
        return hasTargets(inverse(direction));
    }

    public boolean shouldOpen() {
        if (Iterables.tryFind(travelingTargets, equalsFloor).isPresent()) {
            return true;
        }
        Queue<Direction> directions = waitingTargets.get(floor);
        boolean waitingTargetPresent = directions != null && !directions.isEmpty();

        if (hasTargetsAhead()) {
            waitingTargetPresent = waitingTargetPresent && Iterables.tryFind(directions, Predicates.equalTo(direction)).isPresent();
        }

        return waitingTargetPresent && mayAddTargets();
    }

    public boolean mayAddTargets() {
        return targetThreshold == null || targetThreshold <= 0 || travelingTargets.size() < targetThreshold;
    }

    public void clearTraveling() {
        travelingTargets = Sets.newHashSet(Collections2.filter(travelingTargets, Predicates.not(equalsFloor)));
    }

    public void popWaiting() {
        waitingTargets.get(floor).remove();
    }

    public void doOpen() {
        nextCommand = Command.OPEN;
    }

    public void doClose() {
        nextCommand = Command.CLOSE;
    }

    public void doNothing() {
        nextCommand = Command.NOTHING;
    }

    @Override
    public ElevatorState clone() {
        try {
            return (ElevatorState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void doContinue() {
        doMove(direction);
    }

    public void doReverse() {
        doMove(inverse(direction));
    }

    public String printState() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");

        builder.append(String.format("%02d", floor));

        builder.append(":");

        if (direction == Direction.UP) {
            builder.append("∧");
        }
        if (direction == Direction.DOWN) {
            builder.append("∨");
        }

        builder.append(":");

        builder.append(String.format("%02d/%02d", travelingTargets.size(), targetThreshold));

        builder.append(":");

        if (Iterables.tryFind(travelingTargets, equalsFloor).isPresent()) {
            builder.append("�?");
        } else {
            builder.append(" ");
        }
        Queue<Direction> directions = waitingTargets.get(floor);
        if (directions !=null && !directions.isEmpty() && Iterables.tryFind(directions, Predicates.equalTo(direction)).isPresent()) {
            builder.append("↗");
        } else if (directions !=null && !directions.isEmpty()) {
            builder.append("→");
        } else {
            builder.append(" ");
        }

        builder.append(":");

        if (hasTargetsAhead()) {
            builder.append("↑");
        } else if (hasTargetsBehind()) {
            builder.append("↺");
        } else {
            builder.append(" ");
        }

        builder.append("]");
        return builder.toString();
    }

    private void doMove(Direction direction) {
        switch (direction) {
            case UP:
                floor++;
                nextCommand = Command.UP;
                this.direction = Direction.UP;
                break;
            case DOWN:
                floor--;
                nextCommand = Command.DOWN;
                this.direction = Direction.DOWN;
            default:
                break;
        }
    }

    private Direction inverse(Direction direction) {
        switch (direction) {
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            default:
                break;
        }
        return null;
    }
}