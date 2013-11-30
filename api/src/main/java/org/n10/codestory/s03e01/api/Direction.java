package org.n10.codestory.s03e01.api;

public enum Direction {

	UP, DOWN;

	public String toShortString() {
		return toString().substring(0, 1);
	};
}
