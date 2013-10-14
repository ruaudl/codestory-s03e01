package org.n10.codestory.s03e01;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import elevator.Command;
import elevator.Direction;
import elevator.User;
import elevator.engine.ElevatorEngine;
import elevator.exception.ElevatorIsBrokenException;

public class SimpleSmartElevator implements ElevatorEngine {

	private int floor;
	private Command nextCommand;
	private Direction direction;
	private Collection<Target> targets;

	private class HasHigherFloorPredicate implements Predicate<Target> {
		public boolean apply(Target value) {
			return value.getFloor() > floor;
		}
	}
	
	private class IsEqualFloorPredicate implements Predicate<Target> {
		public boolean apply(Target value) {
			return value.getFloor() == floor;
		}
	}
	
	private class IsMatchingDirectionPredicate implements Predicate<Target> {
		public boolean apply(Target value) {
			return value.getDirection() == null || value.getDirection() == direction;
		}
	}

	private class HasLowerFloorPredicate implements Predicate<Target> {
		public boolean apply(Target value) {
			return value.getFloor() < floor;
		}
	}

	public SimpleSmartElevator() {
		reset("Init");
	}

	@Override
	public Command nextCommand() throws ElevatorIsBrokenException {
		Command currentCommand = nextCommand;

		if (nextCommand == Command.OPEN) {
			nextCommand = Command.CLOSE;
		} else if (!targets.isEmpty()) {
			Optional<Target> upTarget = Iterables.tryFind(Ordering.natural().sortedCopy(targets), new HasHigherFloorPredicate());
			Optional<Target> downTarget = Iterables.tryFind(Ordering.natural().reverse().sortedCopy(targets), new HasLowerFloorPredicate());
			Map<Direction, Optional<Target>> mapDirectionToTarget = Maps.newHashMap();
			mapDirectionToTarget.put(Direction.UP, upTarget);
			mapDirectionToTarget.put(Direction.DOWN, downTarget);
			
			if (targetsContainFloor(mapDirectionToTarget)) {
				openTheDoor();
			} else {
				int target = floor;

				if (mapDirectionToTarget.get(direction).isPresent()) {
					target = mapDirectionToTarget.get(direction).get().getFloor();
				} else if (mapDirectionToTarget.get(inverse(direction)).isPresent()) {
					target = mapDirectionToTarget.get(inverse(direction)).get().getFloor();
				}
				
				int diff = target - floor;
				if (diff > 0) {
					nextCommand = Command.UP;
					direction = Direction.UP;
					floor++;
				} else if (diff < 0) {
					nextCommand = Command.DOWN;
					direction = Direction.DOWN;
					floor--;
				}
			}

			if (currentCommand == Command.NOTHING) {
				currentCommand = nextCommand();
			}
		} else {
			nextCommand = Command.NOTHING;
		}

		return currentCommand;
	}

	private void openTheDoor() {
		targets = Sets.newHashSet(Collections2.filter(targets, Predicates.not(new IsEqualFloorPredicate())));
		nextCommand = Command.OPEN;
	}

	private boolean targetsContainFloor(Map<Direction, Optional<Target>> mapDirectionToTarget) {
		Predicate<Target> predicate = new IsEqualFloorPredicate();
		if (mapDirectionToTarget.get(direction).isPresent()) {
			predicate = Predicates.and(predicate, new IsMatchingDirectionPredicate());
		}
		return Iterables.tryFind(targets, predicate).isPresent();
	}

	@Override
	public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException {
		targets.add(new Target(atFloor, to));
		return this;
	}

	@Override
	public ElevatorEngine go(Integer floorToGo) throws ElevatorIsBrokenException {
		targets.add(new Target(floorToGo, null));
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

	@Override
	public ElevatorEngine reset(String cause) throws ElevatorIsBrokenException {
		floor = 0;
		nextCommand = Command.NOTHING;
		direction = Direction.UP;
		targets = new HashSet<Target>();
		return this;
	}

}
