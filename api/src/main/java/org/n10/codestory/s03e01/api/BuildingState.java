package org.n10.codestory.s03e01.api;

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

	public void pushUser(User user) {
		pushUser(user, user.getInitialFloor());
	}

	public int getLimit() {
		return ((higherFloor - lowerFloor) + 1) / 3;
	}

	public boolean floorInRange(int floor, int cabinId) {
		int floorsNb = higherFloor - lowerFloor + 1;
		int rangePerCabin = floorsNb / cabinCount;
		int higherFloorByCabin = higherFloor + 1;
		if (cabinId != (cabinCount - 1)) {
			higherFloorByCabin = (rangePerCabin * (cabinId + 1)) + lowerFloor;
		}
		int lowerFloorByCabin = (rangePerCabin * cabinId) + lowerFloor;
		return floor >= lowerFloorByCabin && floor < higherFloorByCabin;
	}
}
