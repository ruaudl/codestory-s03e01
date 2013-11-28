package org.n10.codestory.s03e01.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ElevatorState {

	public int floor;
	public int currentTravelersNb;
	public Command nextCommand;
	public Direction direction;
	public Map<Integer, Queue<User>> waitingTargets = new HashMap<>();
	public Map<Integer, Queue<User>> travelingTargets = new HashMap<>();
	public Integer lowerFloor;
	public Integer higherFloor;
	public Integer targetThreshold;
	public Integer cabinSize;
	public boolean doorsOpened;

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

	public ElevatorState() {
		doorsOpened = false;
		floor = 0;
		nextCommand = Command.NOTHING;
		direction = Direction.UP;
		waitingTargets = new HashMap<>();
		travelingTargets = new HashMap<>();
		this.lowerFloor = ElevatorEngine.LOWER_FLOOR;
		this.higherFloor = ElevatorEngine.HIGHER_FLOOR;
		this.cabinSize = ElevatorEngine.CABIN_SIZE;
		this.currentTravelersNb = 0;
		targetThreshold = getLimit();
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

	public boolean willGivePoints() {
		Iterable<User> values = Iterables.concat(Iterables.concat(travelingTargets.values(), waitingTargets.values()));
		return Iterables.tryFind(values, hasPotentialPoints).isPresent();
	}

	private boolean hasTargets(final Direction direction) {
		boolean hasWaiting = Iterables.tryFind(waitingTargets.entrySet(), isAhead.get(direction)).isPresent();
		boolean hasTravelling = Iterables.tryFind(travelingTargets.entrySet(), isAhead.get(direction)).isPresent();
		return hasWaiting || hasTravelling;
	}

	private boolean isNotEmpty(Queue<?> queue) {
		return queue != null && queue.size() > 0;
	}

	public boolean hasTargetsAhead() {
		return hasTargets(direction);
	}

	public boolean hasTargetsBehind() {
		return hasTargets(inverse(direction));
	}

	public boolean shouldOpen() {
		if (isNotEmpty(travelingTargets.get(floor))) {
			return true;
		}

		if (mayAddTargets()) {
			Queue<User> waitings = getFirstWaiting(floor);
			boolean waitingTargetPresent = isNotEmpty(waitings);
			if (!waitingTargetPresent) {
				return false;
			}

			boolean waitingTargetsSameDirection = !hasTargetsAhead() || Iterables.tryFind(waitings, hasSameDirection).isPresent();
			if (willGivePoints()) {
				return waitingTargetsSameDirection && Iterables.tryFind(waitings, hasPotentialPoints).isPresent();
			}
			return waitingTargetsSameDirection;
		}
		return false;
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
		return waitingTargets.get(floor).remove();
	}

	public void doOpen() {
		doorsOpened = true;
		nextCommand = Command.OPEN;
	}

	public void doClose() {
		doorsOpened = false;
		nextCommand = Command.CLOSE;
	}

	public void doNothing() {
		nextCommand = Command.NOTHING;
	}

	public void doContinue() {
		doMove(direction);
	}

	public void doReverse() {
		doMove(inverse(direction));
	}

	public int getTargetsCount() {
		return Maps.filterValues(travelingTargets, new Predicate<Queue<User>>() {
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

		if (isNotEmpty(travelingTargets.get(floor))) {
			builder.append("<");
		} else {
			builder.append(" ");
		}
		Queue<User> users = waitingTargets.get(floor);
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

	private int getLimit() {
		return ((higherFloor - lowerFloor) + 1) / 3;
	}

	public User popTraveling() {
		return travelingTargets.get(floor).remove();
	}

	private Queue<User> getFirstWaiting(int atFloor) {
		Queue<User> elements = waitingTargets.get(atFloor);
		if (isNotEmpty(elements)) {
			int count = cabinSize - currentTravelersNb;
			if (isNotEmpty(travelingTargets.get(atFloor))) {
				count += travelingTargets.get(atFloor).size();
			}
			if (elements.size() > count) {
				Queue<User> firsts = new LinkedList<>();
				for (int i = 0; i < count; i++) {
					firsts.add(elements.peek());
				}
				return firsts;
			}
		}
		return elements;
	}

	public void tick() {
		Collection<Queue<User>> queues = Lists.newArrayList();
		queues.addAll(waitingTargets.values());
		queues.addAll(travelingTargets.values());
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