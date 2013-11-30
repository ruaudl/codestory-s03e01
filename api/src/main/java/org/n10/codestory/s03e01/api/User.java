package org.n10.codestory.s03e01.api;

public class User {

	private Integer initialFloor;
	private Integer floorToGo;
	private Integer tickToGo;
	private User.State state;
	private Integer tickToWait;
	private Direction directionToGo;

	public User(Direction directionToGo, Integer initialFloor) {
		this.tickToGo = 0;
		this.tickToWait = 0;
		this.directionToGo = directionToGo;
		this.initialFloor = initialFloor;
		this.floorToGo = initialFloor;
		this.state = State.WAITING;
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

	public void travels() {
		state = State.TRAVELLING;
	}

	public void waits() {
		state = State.WAITING;
	}

	public void arrived() {
		state = State.DONE;
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

	public Direction getDirectionToGo() {
		return directionToGo;
	}

	public void setFloorToGo(Integer floorToGo) {
		this.floorToGo = floorToGo;
	}

	public Integer getPoints(int waitTick, int goTick) {
		return 20 + (2 + Math.abs(initialFloor - floorToGo)) - (waitTick / 2 + goTick);
	}

	public Integer getRemainingPoints() {
		return getPoints(tickToWait, tickToGo);
	}

	public Integer getPotentialPoints(Integer actualFloor, boolean isOpen) {
		return getPoints(getTickToWaitToCome(actualFloor, isOpen) + tickToWait, getTickToGoToCome(actualFloor, isOpen) + tickToGo);
	}

	public boolean willGivePoints() {
		return getRemainingPoints() > 0;
	}

	public Integer getTickToWaitToCome(Integer actualFloor, boolean isOpen) {
		if (traveling()) {
			return 0;
		}
		return getTickToCome(actualFloor, isOpen, initialFloor);
	}

	public Integer getTickToGoToCome(Integer actualFloor, boolean isOpen) {
		if (waiting()) {
			return 2;
		}
		return getTickToCome(actualFloor, isOpen, floorToGo);
	}

	public Integer getTickToCome(Integer actualFloor, boolean isOpen, Integer floorToCompare) {
		if (actualFloor == floorToCompare) {
			if (isOpen) {
				return 0;
			} else {
				return 1;
			}
		}
		int nbMoves = Math.abs(actualFloor - floorToCompare) + 1;
		if (isOpen) {
			return nbMoves + 1;
		}
		return nbMoves;
	}

	public boolean willGivePointsFrom(Integer actualFloor, boolean isOpen) {
		return getPotentialPoints(actualFloor, isOpen) > 0;
	}

	private enum State {

		WAITING, TRAVELLING, DONE, ;
	}
}
