package org.n10.codestory.s03e01.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public class ElevatorState extends State {

	public int floor;
	public int travelersCount;
	public boolean doorsOpened;
	public Direction direction;
	public Integer targetThreshold;
	public Integer cabinSize;
	public BuildingState buildingState;
	private final int cabinId;

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

	public ElevatorState(BuildingState buildingState, int cabinId) {
		floor = 0;
		travelersCount = 0;
		doorsOpened = false;
		direction = Direction.UP;
		this.buildingState = buildingState;
		targetThreshold = buildingState.getLimit();
		this.cabinId = cabinId;
	}

	public ElevatorState(BuildingState buildingState, Integer cabinSize, int cabinId) {
		this(buildingState, cabinId);
		this.cabinSize = cabinSize;
	}

	public boolean isOpen() {
		return doorsOpened;
	}

	public boolean willGivePoints(Iterable<User> users) {
		return Iterables.tryFind(users, hasPotentialPoints).isPresent();
	}

	public boolean willGivePoints() {
		return willGivePoints(Iterables.concat(Iterables.concat(getUsers(), buildingState.getUsers(cabinId))));
	}

	private boolean hasTargets(final Direction direction) {
		Iterable<Entry<Integer, Queue<User>>> waitings = Iterables.filter(buildingState.getUsersByFloor(cabinId), isAhead.get(direction));
		Iterable<Entry<Integer, Queue<User>>> travelings = Iterables.filter(targets.entrySet(), isAhead.get(direction));

		if (Iterables.isEmpty(waitings) && Iterables.isEmpty(travelings)) {
			return false;
		}

		if (!willGivePoints() || !mayAddTargets()) {
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

	public boolean hasTravelersToGetOut() {
		return isNotEmpty(targets.get(floor));
	}

	public boolean hasTravelersToGetOutWithPoints() {
		return hasTravelersToGetOut() && (Iterables.tryFind(getUsersAtFloor(floor), hasPotentialPoints).isPresent() || !willGivePoints());
	}

	public boolean hasTravelersWithPoints() {
		return travelersCount > 0 && (Iterables.tryFind(Iterables.concat(getUsers()), hasPotentialPoints).isPresent());
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
		if (hasTravelersToGetOutWithPoints()) {
			return true;
		}

		if (!mayAddTargets()) {
			return hasTravelersToGetOut() && !hasTravelersWithPoints();
		}

		if (!buildingState.floorInRange(floor, cabinId)) {
			return false;
		}

		Iterable<User> waitings = getFirstWaiting(floor);
		if (isEmpty(waitings)) {
			return false;
		}

		Predicate<User> predicate = Predicates.alwaysTrue();
		if (hasTargetsAhead()) {
			predicate = Predicates.and(predicate, hasSameDirection(direction));
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
		if (!hasTargetsAhead() && !Iterables.tryFind(getFirstWaitingFollowing(floor), hasPotentialPoints).isPresent()) {
			if (hasTargetsBehind() || Iterables.tryFind(getFirstWaitingReversing(floor), hasPotentialPoints).isPresent()) {
				direction = inverse(direction);
			}
		}
		doorsOpened = true;
		return openTo(direction);
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

	public String getStatus() {
		String getOut = hasTravelersToGetOut() ? "<" : " ";
		Queue<User> waitings = buildingState.getUsersAtFloor(floor, cabinId);
		String getIn = Iterables.tryFind(waitings, hasSameDirection(direction)).isPresent() ? "≥" : isNotEmpty(waitings) ? ">" : " ";
		String aheadOrBehind = hasTargetsAhead() ? "→" : hasTargetsBehind() ? "↺" : " ";
		String state = String.format("(%02d:%s:%02d/%02d:%s%s:%s)", floor, direction.toShortString(), getTargetsCount(), targetThreshold, getOut, getIn,
				aheadOrBehind);
		return state;
	}

	@Override
	public String toString() {
		return String.format("%s\n\t%s", super.toString(), getStatus());
	}

	private int getRoom(int atFloor) {
		return cabinSize - travelersCount + getUsersAtFloor(atFloor).size();
	}

	private Iterable<User> getFirstWaitingFollowing(int atFloor) {
		return buildingState.getFirstUsers(getRoom(atFloor), atFloor, direction, cabinId);
	}

	private Iterable<User> getFirstWaitingReversing(int atFloor) {
		return buildingState.getFirstUsers(getRoom(atFloor), atFloor, inverse(direction), cabinId);
	}

	private Iterable<User> getFirstWaiting(int atFloor) {
		Iterable<User> waitings = getFirstWaitingFollowing(atFloor);
		if (!hasTargetsAhead()) {
			waitings = Iterables.concat(waitings, getFirstWaitingReversing(atFloor));
		}
		return waitings;
	}
}