package org.n10.codestory.s03e01.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public abstract class State {

	public Map<Integer, Queue<User>> targets = new HashMap<>();

	public void pushUser(User user, Integer atFloor) {
		Queue<User> queue = targets.get(atFloor);
		if (queue == null) {
			queue = new LinkedList<>();
			targets.put(atFloor, queue);
		}
		queue.add(user);
	}

	public User popUser(int atFloor) {
		return targets.get(atFloor).remove();
	}

	public Queue<User> getFirstUsers(int count, int atFloor) {
		Queue<User> elements = targets.get(atFloor);
		if (isNotEmpty(elements)) {
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

	public void tickUsers() {
		Iterable<User> users = Iterables.concat(targets.values());
		for (User user : users) {
			user.tick();
		}
	}

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

	public static Function<Entry<Integer, Queue<User>>, Queue<User>> USER_EXTRACTION = new Function<Entry<Integer, Queue<User>>, Queue<User>>() {
		public Queue<User> apply(Entry<Integer, Queue<User>> input) {
			return input.getValue();
		}
	};

	public static boolean isNotEmpty(Queue<?> queue) {
		return queue != null && queue.size() > 0;
	}

}