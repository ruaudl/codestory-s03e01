package org.n10.codestory.s03e01.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class ElevatorState extends State {

	public int floor;
	public int travelersCount;
	public boolean doorsOpened;
	public Direction direction;
	public Integer targetThreshold;
	public Integer cabinSize;
	public BuildingState buildingState;

	private Predicate<User> hasSameDirection = new Predicate<User>() {
		@Override
		public boolean apply(User user) {
			return user.getDirectionToGo() == direction;
		}
	};
	private Predicate<User> hasPotentialPoints = new Predicate<User>() {
		@Override
		public boolean apply(User user) {
			return user.willGivePointsFrom(floor, doorsOpened);
		}
	};
	private Map<Direction, Predicate<Entry<Integer, Queue<User>>>> isAhead = new HashMap<>();
	{
		isAhead.put(Direction.UP, new Predicate<Entry<Integer, Queue<User>>>() {
			@Override
			public boolean apply(Entry<Integer, Queue<User>> value) {
				return value.getKey() > floor && isNotEmpty(value.getValue());
			}
		});
		isAhead.put(Direction.DOWN, new Predicate<Entry<Integer, Queue<User>>>() {
			@Override
			public boolean apply(Entry<Integer, Queue<User>> value) {
				return value.getKey() < floor && isNotEmpty(value.getValue());
			}
		});
	}

	public ElevatorState(BuildingState buildingState) {
		floor = 0;
		travelersCount = 0;
		doorsOpened = false;
		direction = Direction.UP;
		this.buildingState = buildingState;
		targetThreshold = buildingState.getLimit();
	}

	public ElevatorState(BuildingState buildingState, Integer cabinSize) {
		this(buildingState);
		this.cabinSize = cabinSize;
	}

	public boolean isOpen() {
		return doorsOpened;
	}

	public boolean willGivePoints(Iterable<User> users) {
		return Iterables.tryFind(users, hasPotentialPoints).isPresent();
	}

	public boolean willGivePoints() {
		return willGivePoints(Iterables.concat(Iterables.concat(targets.values(), buildingState.targets.values())));
	}

	private boolean hasTargets(final Direction direction) {
		Iterable<Entry<Integer, Queue<User>>> waitings = Iterables.filter(buildingState.targets.entrySet(), isAhead.get(direction));
		Iterable<Entry<Integer, Queue<User>>> travelings = Iterables.filter(targets.entrySet(), isAhead.get(direction));

		if (Iterables.isEmpty(waitings) && Iterables.isEmpty(travelings)) {
			return false;
		}

		if (!willGivePoints()) {
			return true;
		}

		Iterable<Queue<User>> waitingUsers = Iterables.transform(waitings, USER_EXTRACTION);
		Iterable<Queue<User>> travelingUsers = Iterables.transform(travelings, USER_EXTRACTION);
		return willGivePoints(Iterables.concat(Iterables.concat(waitingUsers, travelingUsers)));
	}

	public boolean hasTargetsAhead() {
		return hasTargets(direction);
	}

	public boolean hasTargetsBehind() {
		return hasTargets(inverse(direction));
	}

	public boolean hasTravellersWithPoints() {
		return isNotEmpty(targets.get(floor)) && (Iterables.tryFind(targets.get(floor), hasPotentialPoints).isPresent() || !willGivePoints());
	}

	public boolean thresholdNotReached() {
		return targetThreshold == null || targetThreshold <= 0 || getTargetsCount() < targetThreshold;
	}

	public boolean cabinSizeNotReached() {
		return cabinSize == null || cabinSize <= 0 || travelersCount < cabinSize;
	}

	public boolean mayAddTargets() {
		return thresholdNotReached() && cabinSizeNotReached();
	}

	public boolean shouldOpen() {
		if (hasTravellersWithPoints()) {
			return true;
		}

		if (!mayAddTargets()) {
			return false;
		}

		Queue<User> waitings = getFirstWaiting(floor);
		if (!isNotEmpty(waitings)) {
			return false;
		}

		Predicate<User> predicate = Predicates.alwaysTrue();
		if (hasTargetsAhead()) {
			predicate = Predicates.and(predicate, hasSameDirection);
		}
		if (willGivePoints()) {
			predicate = Predicates.and(predicate, hasPotentialPoints);
		}
		return Iterables.tryFind(waitings, predicate).isPresent();
	}

	public void pushUser(User user) {
		pushUser(user, user.getFloorToGo());
	}

	public User popUser() {
		return popUser(floor);
	}

	public Command doOpen() {
		doorsOpened = true;
		return Command.OPEN;
	}

	public Command doClose() {
		doorsOpened = false;
		return Command.CLOSE;
	}

	public Command doContinue() {
		return doMove(direction);
	}

	public Command doReverse() {
		return doMove(inverse(direction));
	}

	private Command doMove(Direction direction) {
		switch (direction) {
		case UP:
			floor++;
			this.direction = Direction.UP;
			return Command.UP;
		case DOWN:
			floor--;
			this.direction = Direction.DOWN;
			return Command.DOWN;
		default:
			return Command.NOTHING;
		}
	}

	public int getTargetsCount() {
		return Maps.filterValues(targets, new Predicate<Queue<User>>() {
			@Override
			public boolean apply(Queue<User> t) {
				return isNotEmpty(t);
			}
		}).size();

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

		builder.append(String.format("%02d/%02d", getTargetsCount(), targetThreshold));

		builder.append(":");

		if (isNotEmpty(targets.get(floor))) {
			builder.append("<");
		} else {
			builder.append(" ");
		}
		Queue<User> users = buildingState.targets.get(floor);
		if (isNotEmpty(users) && Iterables.tryFind(users, new Predicate<User>() {
			@Override
			public boolean apply(User t) {
				return direction == t.getDirectionToGo();
			}
		}).isPresent()) {
			builder.append("≥");
		} else if (isNotEmpty(users)) {
			builder.append(">");
		} else {
			builder.append(" ");
		}

		builder.append(":");

		if (hasTargetsAhead()) {
			builder.append("→");
		} else if (hasTargetsBehind()) {
			builder.append("↺");
		} else {
			builder.append(" ");
		}

		builder.append("]");
		return builder.toString();
	}

	private Queue<User> getFirstWaiting(int atFloor) {
		int count = cabinSize - travelersCount;
		if (isNotEmpty(targets.get(atFloor))) {
			count += targets.get(atFloor).size();
		}
		return buildingState.getFirstUsers(count, atFloor);
	}
}