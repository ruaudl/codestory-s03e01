package org.n10.codestory.s03e01.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class State {

	public static boolean isNotEmpty(Queue<?> queue) {
		return queue != null && queue.size() > 0;
	}

	public Map<Integer, Queue<User>> targets = new HashMap<>();

	public Queue<User> getFirsts(int count, int atFloor) {
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

}