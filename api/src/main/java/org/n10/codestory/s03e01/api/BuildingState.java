package org.n10.codestory.s03e01.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public class BuildingState extends State {

	public Integer lowerFloor;
	public Integer higherFloor;
	public int cabinCount;

	public BuildingState() {
		lowerFloor = ElevatorEngine.LOWER_FLOOR;
		higherFloor = ElevatorEngine.HIGHER_FLOOR;
		cabinCount = 1;
	}

	public BuildingState(Integer lowerFloor, Integer higherFloor, int cabinCount) {
		this();
		this.lowerFloor = lowerFloor;
		this.higherFloor = higherFloor;
		this.cabinCount = cabinCount;
	}

	public Predicate<? super Integer> isInRange(final int cabinId) {
		return new Predicate<Integer>() {
			@Override
			public boolean apply(Integer floor) {
				return floorInRange(floor, cabinId);
			}
		};
	}

	public Collection<Queue<User>> getUsers(int cabinId) {
		return Maps.filterKeys(targets, isInRange(cabinId)).values();
	}

	public Set<Entry<Integer, Queue<User>>> getUsersByFloor(int cabinId) {
		return Maps.filterKeys(targets, isInRange(cabinId)).entrySet();
	}

	public Queue<User> getUsersAtFloor(int atFloor, int cabinId) {
		Queue<User> queue = new LinkedList<>();
		if (floorInRange(atFloor, cabinId)) {
			queue = targets.get(atFloor);
			if (queue == null) {
				queue = new LinkedList<>();
				targets.put(atFloor, queue);
			}
		}
		return queue;
	}

	public Iterable<User> getFirstUsers(int count, int atFloor, final Direction direction, int cabinId) {
		return Iterables.limit(Iterables.filter(getUsersAtFloor(atFloor, cabinId), hasSameDirection(direction)), count);
	}

	public void pushUser(User user) {
		pushUser(user, user.getInitialFloor());
	}

	public int getLimit() {
		return ((higherFloor - lowerFloor) + 1) / 3;
	}

	public boolean floorInRange(int floor, int cabinId) {
		Map<Integer, Queue<User>> effectiveTargets = Maps.filterEntries(targets, new Predicate<Entry<Integer, Queue<User>>>() {
			public boolean apply(Entry<Integer, Queue<User>> input) {
				return isNotEmpty(input.getValue());
			}
		});
		if (!effectiveTargets.containsKey(floor)) {
			return false;
		}

		ArrayList<Integer> indexes = Lists.newArrayList(Ordering.natural().sortedCopy(effectiveTargets.keySet()));
		int floorIndex = indexes.indexOf(Integer.valueOf(floor));

		double indexesNb = indexes.size();
		double rangePerCabin = indexesNb / cabinCount;
		int higherIndexByCabin = (int) ((rangePerCabin * (cabinId + 1)));
		int lowerIndexByCabin = (int) ((rangePerCabin * cabinId));
		return floorIndex >= lowerIndexByCabin && floorIndex < higherIndexByCabin;
	}
}
