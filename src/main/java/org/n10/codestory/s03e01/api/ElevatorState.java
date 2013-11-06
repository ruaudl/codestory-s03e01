package org.n10.codestory.s03e01.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


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
	public Collection<Target> targets;
	public Integer lowerFloor;
	public Integer higherFloor;
	

	public ElevatorState(Integer lowerFloor, Integer higherFloor) {
		super();
		this.lowerFloor = lowerFloor;
		this.higherFloor = higherFloor;
	}

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
		targets = new HashSet<Target>();
	}

	public boolean willOpen() {
		return nextCommand == Command.OPEN;
	}

	private boolean hasTargets(Direction direction) {
		return Iterables.tryFind(Ordering.natural().sortedCopy(targets), isAhead.get(direction)).isPresent();
	}
	
	public boolean hasTargetsAhead() {
		return hasTargets(direction);
	}

	public boolean hasTargetsBehind() {
		return hasTargets(inverse(direction));
	}

	public boolean shouldOpen() {
		Predicate<Target> predicate = equalsFloor;
		if (hasTargetsAhead()) {
			predicate = Predicates.and(predicate, equalsDirection);
		}
		return Iterables.tryFind(targets, predicate).isPresent();
	}
	
	public void clearFloor() {
		targets = Sets.newHashSet(Collections2.filter(targets, Predicates.not(equalsFloor)));
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

	public boolean doSomething() {
		return nextCommand != Command.NOTHING;
	}

}