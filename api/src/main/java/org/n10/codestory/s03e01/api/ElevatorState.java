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

public class ElevatorState implements Cloneable {

	public int floor;
	public Command nextCommand;
	public Direction direction;
	public Set<Target> waitingTargets;
	public Set<Target> travelingTargets;
	public Integer lowerFloor;
	public Integer higherFloor;
	public Integer targetThreshold;

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
		waitingTargets = new HashSet<Target>();
		travelingTargets = new HashSet<Target>();
		targetThreshold = -1;
	}

	public ElevatorState(Integer lowerFloor, Integer higherFloor) {
		this();
		this.lowerFloor = lowerFloor;
		this.higherFloor = higherFloor;
	}

	public boolean willOpen() {
		return nextCommand == Command.OPEN;
	}

	public boolean willDoSomething() {
		return nextCommand != Command.NOTHING;
	}

	private boolean hasTargets(Direction direction) {
		return Iterables.tryFind(Ordering.natural().sortedCopy(Sets.union(waitingTargets, travelingTargets)), isAhead.get(direction)).isPresent();
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

		Predicate<Target> predicate = equalsFloor;
		if (hasTargetsAhead()) {
			predicate = Predicates.and(predicate, equalsDirection);
		}
		return Iterables.tryFind(waitingTargets, predicate).isPresent() && mayAddTargets();
	}

	public boolean mayAddTargets() {
		return targetThreshold == null || targetThreshold <= 0 || travelingTargets.size() < targetThreshold;
	}

	public void clearFloor() {
		travelingTargets = Sets.newHashSet(Collections2.filter(travelingTargets, Predicates.not(equalsFloor)));
		waitingTargets = Sets.newHashSet(Collections2.filter(waitingTargets, Predicates.not(equalsFloor)));
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
		
		if (direction == Direction.UP)
			builder.append("∧");
		if (direction == Direction.DOWN)
			builder.append("∨");
		
		builder.append(":");
		
		builder.append(String.format("%02d/%02d", travelingTargets.size(), targetThreshold));
		
		builder.append(":");

		if (Iterables.tryFind(travelingTargets, equalsFloor).isPresent())
			builder.append("←");
		else
			builder.append(" ");

		if (Iterables.tryFind(waitingTargets, Predicates.and(equalsFloor, equalsDirection)).isPresent())
			builder.append("↗");
		else if (Iterables.tryFind(waitingTargets, equalsFloor).isPresent())
			builder.append("→");
		else
			builder.append(" ");
		
		builder.append(":");

		if (hasTargetsAhead())
			builder.append("↑");
		else if (hasTargetsBehind())
			builder.append("↺");
		else
			builder.append(" ");
		
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