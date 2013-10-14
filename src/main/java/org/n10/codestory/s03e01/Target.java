package org.n10.codestory.s03e01;

import elevator.Direction;

public class Target implements Comparable<Target>{

	private int floor;
	
	private Direction direction;

	public Target(int floor, Direction direction) {
		super();
		this.floor = floor;
		this.direction = direction;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floorToGo) {
		this.floor = floorToGo;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction directionToGo) {
		this.direction = directionToGo;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (obj == null){
			return false;
		}
		
		if(!(obj instanceof Target)){
			return false;
		}
		Target objTarget = (Target) obj;
		if (floor != objTarget.floor) {
			return false;
		}
		
		return (direction == objTarget.direction);
	}
	
	@Override
	public int hashCode() {
		int hash = 17 * floor;
		if (direction != null){
			hash += 31 * direction.hashCode();
		}
		return hash; 
	}

	@Override
	public int compareTo(Target o) {
		return floor - o.floor;
	}
	
	@Override
	public String toString() {
		return "" + floor + direction;
	}
	
	
}
