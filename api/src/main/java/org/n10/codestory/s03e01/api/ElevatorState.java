package org.n10.codestory.s03e01.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ElevatorState extends State {

	public int floor;
	public int currentTravelersNb;
	public Direction direction;
	public Integer lowerFloor;
	public Integer higherFloor;
	public Integer targetThreshold;
	public Integer cabinSize;
	public boolean doorsOpened;
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
	private Function<Entry<Integer, Queue<User>>, Queue<User>> userExtraction = new Function<Entry<Integer, Queue<User>>, Queue<User>>() {
		public Queue<User> apply(Entry<Integer, Queue<User>> input) {
			return input.getValue();
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

	public ElevatorState() {
		doorsOpened = false;
		floor = 0;
		direction = Direction.UP;
		targets = new HashMap<>();
		lowerFloor = ElevatorEngine.LOWER_FLOOR;
		higherFloor = ElevatorEngine.HIGHER_FLOOR;
		cabinSize = ElevatorEngine.CABIN_SIZE;
		currentTravelersNb = 0;
		targetThreshold = getLimit();
		buildingState = new BuildingState();
	}

	public ElevatorState(Integer lowerFloor, Integer higherFloor, Integer cabinSize, BuildingState buildingState) {
		this();
		this.lowerFloor = lowerFloor;
		this.higherFloor = higherFloor;
		this.cabinSize = cabinSize;
		this.buildingState = buildingState;
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

		Iterable<Queue<User>> waitingUsers = Iterables.transform(waitings, userExtraction);
		Iterable<Queue<User>> travelingUsers = Iterables.transform(travelings, userExtraction);
		return willGivePoints(Iterables.concat(Iterables.concat(waitingUsers, travelingUsers)));
	}

	public boolean hasTargetsAhead() {
		return hasTargets(direction);
	}

	public boolean hasTargetsBehind() {
		return hasTargets(inverse(direction));
	}

	public boolean shouldOpen() {
		boolean willGivePoints = willGivePoints();
		if (isNotEmpty(targets.get(floor))) {
			if (Iterables.tryFind(targets.get(floor), hasPotentialPoints).isPresent() || !willGivePoints)
				return true;
		}

		if (!mayAddTargets()) {
			return false;
		}

		Queue<User> waitings = getFirstWaiting(floor);
		boolean waitingTargetPresent = isNotEmpty(waitings);
		if (!waitingTargetPresent) {
			return false;
		}

		boolean waitingTargetsSameDirection = !hasTargetsAhead() || Iterables.tryFind(waitings, hasSameDirection).isPresent();
		if (willGivePoints) {
			return waitingTargetsSameDirection && Iterables.tryFind(waitings, hasPotentialPoints).isPresent();
		}
		return waitingTargetsSameDirection;
	}

	public boolean thresholdNotReached() {
		return targetThreshold == null || targetThreshold <= 0 || getTargetsCount() < targetThreshold;
	}

	public boolean cabinSizeNotReached() {
		return cabinSize == null || cabinSize <= 0 || currentTravelersNb < cabinSize;
	}

	public boolean mayAddTargets() {
		return thresholdNotReached() && cabinSizeNotReached();
	}

	public User popWaiting() {
		return buildingState.targets.get(floor).remove();
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

	private int getLimit() {
		return ((higherFloor - lowerFloor) + 1) / 3;
	}

	public User popTraveling() {
		return targets.get(floor).remove();
	}

	private Queue<User> getFirstWaiting(int atFloor) {
		int count = cabinSize - currentTravelersNb;
		if (isNotEmpty(targets.get(atFloor))) {
			count += targets.get(atFloor).size();
		}
		return buildingState.getFirsts(count, atFloor);
	}

	public void tick() {
		Collection<Queue<User>> queues = Lists.newArrayList();
		queues.addAll(buildingState.targets.values());
		queues.addAll(targets.values());
		for (Queue<User> queue : queues) {
			if (isNotEmpty(queue)) {
				Iterator<User> iterator = queue.iterator();
				while (iterator.hasNext()) {
					iterator.next().tick();
				}
			}
		}
	}
}