package org.n10.codestory.s03e01.api;

import static java.lang.Math.*;
import static org.n10.codestory.s03e01.api.Direction.*;
import static org.n10.codestory.s03e01.api.ElevatorEngine.*;

public class User {

	private Integer initialFloor;
	private Integer floorToGo;
	private Integer tickToGo;
	private User.State state;
	private Integer tickToWait;

	public User() {
		this.tickToGo = 0;
		this.tickToWait = 0;
	}

	boolean waiting() {
		return state == State.WAITING;
	}

	Boolean traveling() {
		return state == State.TRAVELLING;
	}

	Boolean done() {
		return state == State.DONE;
	}

	private Integer randomFloor() {
		return new Double(random() * HIGHER_FLOOR).intValue();
	}

	private Direction randomDirection() {
		return randomBoolean() ? UP : DOWN;
	}

	private Boolean randomBoolean() {
		return random() > .5;
	}

	public Integer getTickToGo() {
		return tickToGo;
	}

	public Integer getInitialFloor() {
		return initialFloor;
	}

	public Integer getFloorToGo() {
		return floorToGo;
	}

	void tick() {
		if (traveling()) {
			tickToGo++;
		}
		if (waiting()) {
			tickToWait++;
		}
	}

	public Integer getTickToWait() {
		return tickToWait;
	}

	private enum State {

		WAITING, TRAVELLING, DONE,;
	}
}
