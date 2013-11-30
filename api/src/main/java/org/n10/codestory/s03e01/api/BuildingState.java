package org.n10.codestory.s03e01.api;

public class BuildingState extends State {

	public Integer lowerFloor;
	public Integer higherFloor;

	public BuildingState() {
		lowerFloor = ElevatorEngine.LOWER_FLOOR;
		higherFloor = ElevatorEngine.HIGHER_FLOOR;
	}

	public BuildingState(Integer lowerFloor, Integer higherFloor) {
		this();
		this.lowerFloor = lowerFloor;
		this.higherFloor = higherFloor;
	}

	public void pushUser(User user) {
		pushUser(user, user.getInitialFloor());
	}

	public int getLimit() {
		return ((higherFloor - lowerFloor) + 1) / 3;
	}

}
