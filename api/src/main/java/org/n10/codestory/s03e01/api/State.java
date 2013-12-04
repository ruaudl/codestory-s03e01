package org.n10.codestory.s03e01.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public abstract class State {

	public Map<Integer, Queue<User>> targets = new HashMap<>();

	public Collection<Queue<User>> getUsers() {
		return targets.values();
	}

	public Set<Entry<Integer,Queue<User>>> getUsersByFloor() {
		return targets.entrySet();
	}

	public Queue<User> getUsersAtFloor(int atFloor) {
		Queue<User> queue = targets.get(atFloor);
		if (queue == null) {
			queue = new LinkedList<>();
			targets.put(atFloor, queue);
		}
		return queue;
	}

	public Iterable<User> getFirstUsers(int count, int atFloor, final Direction direction) {
		return Iterables.limit(Iterables.filter(getUsersAtFloor(atFloor), hasSameDirection(direction)), count);
	}

	public void pushUser(User user, Integer atFloor) {
		getUsersAtFloor(atFloor).add(user);
	}

	public User popUser(int atFloor) {
		return getUsersAtFloor(atFloor).remove();
	}

	public User popUser(int atFloor, final Direction direction) {
		User userToPop = Iterables.tryFind(getUsersAtFloor(atFloor), hasSameDirection(direction)).get();
		getUsersAtFloor(atFloor).remove(userToPop);
		return userToPop;
	}

	public int getTargetsCount() {
		return Maps.filterValues(targets, new Predicate<Queue<User>>() {
			@Override
			public boolean apply(Queue<User> queue) {
				return isNotEmpty(queue);
			}
		}).size();
	}

	public void tickUsers() {
		Iterable<User> users = Iterables.concat(targets.values());
		for (User user : users) {
			user.tick();
		}
	}

	@Override
	public String toString() {
		int points = 0;
		for (User user : Iterables.concat(targets.values())) {
			points += Math.max(0, user.getRemainingPoints());
		}
		return String.format("(%d=%s)", points, targets.toString());
	}

	public static Function<Entry<Integer, Queue<User>>, Queue<User>> USER_EXTRACTION = new Function<Entry<Integer, Queue<User>>, Queue<User>>() {
		public Queue<User> apply(Entry<Integer, Queue<User>> input) {
			return input.getValue();
		}
	};

	public static Direction inverse(Direction direction) {
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

	public static Command openTo(Direction direction) {
		switch (direction) {
		case UP:
			return Command.OPEN_UP;
		case DOWN:
			return Command.OPEN_DOWN;
		}
		return Command.OPEN;
	}

	public static boolean isNotEmpty(Iterable<?> elements) {
		return elements != null && elements.iterator().hasNext();
	}

	public static boolean isEmpty(Iterable<?> elements) {
		return elements == null || !elements.iterator().hasNext();
	}

	public static final Predicate<User> hasSameDirection(final Direction direction) {
		return new Predicate<User>() {
			@Override
			public boolean apply(User user) {
				return user.getDirectionToGo() == direction;
			}
		};
	}
}