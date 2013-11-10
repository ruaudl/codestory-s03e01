package org.n10.codestory.s03e01.core;

import java.util.HashSet;
import java.util.Set;

import org.n10.codestory.s03e01.api.Command;
import org.n10.codestory.s03e01.api.Direction;
import org.n10.codestory.s03e01.api.ElevatorEngine;
import org.n10.codestory.s03e01.api.ElevatorIsBrokenException;
import org.n10.codestory.s03e01.api.User;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

public class TreeSetDirectElevator implements ElevatorEngine {

	private int floor;
	private Command nextCommand;
	private Direction direction;
	private Set<Integer> targets;

	private class HasHigherFloorPredicate implements Predicate<Integer> {
		public boolean apply(Integer value) {
			return value > floor;
		}
	}

	private class HasLowerFloorPredicate implements Predicate<Integer> {
		public boolean apply(Integer value) {
			return value < floor;
		}
	}

	public TreeSetDirectElevator() {
		reset(ElevatorEngine.LOWER_FLOOR, ElevatorEngine.HIGHER_FLOOR, "Init");
	}

	@Override
	public Command nextCommand() throws ElevatorIsBrokenException {
		Command currentCommand = nextCommand;

		if (nextCommand == Command.OPEN) {
			nextCommand = Command.CLOSE;
		} else if (!targets.isEmpty()) {
			if (targets.contains(floor)) {
				targets.remove(floor);
				nextCommand = Command.OPEN;
			} else {
				Integer target = floor;

				Optional<Integer> upTarget = Iterables.tryFind(Ordering.natural().sortedCopy(targets), new HasHigherFloorPredicate());
				Optional<Integer> downTarget = Iterables.tryFind(Ordering.natural().reverse().sortedCopy(targets), new HasLowerFloorPredicate());

				switch (direction) {
				case UP:
					if (upTarget.isPresent()) {
						target = upTarget.get();
					} else if (downTarget.isPresent()) {
						target = downTarget.get();
					}
					break;
				case DOWN:
					if (downTarget.isPresent()) {
						target = downTarget.get();
					} else if (upTarget.isPresent()) {
						target = upTarget.get();
					}
					break;
				}

				Integer diff = target - floor;
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

	@Override
	public ElevatorEngine call(Integer atFloor, Direction to) throws ElevatorIsBrokenException {
		targets.add(atFloor);
		return this;
	}

	@Override
	public ElevatorEngine go(Integer floorToGo) throws ElevatorIsBrokenException {
		targets.add(floorToGo);
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
		nextCommand = Command.NOTHING;
		direction = Direction.UP;
		targets = new HashSet<Integer>();
		return this;
	}

	@Override
	public ElevatorEngine limit(Integer i) {
		return this;
	}

}
