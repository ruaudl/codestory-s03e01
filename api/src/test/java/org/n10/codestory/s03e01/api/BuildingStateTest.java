/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.n10.codestory.s03e01.api;

import org.fest.assertions.Assertions;
import org.junit.Test;

public class BuildingStateTest {

	@Test
	public void floorInRangeAlwaysTrueForOneCabin() {
		BuildingState buildingState = new BuildingState(-2, 10, 1);
		Assertions.assertThat(buildingState.floorInRange(-3, 0)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(-2, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(4, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(10, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(11, 0)).isFalse();
	}

	@Test
	public void floorInRangeForTwoCabinsAndImpairFloorsNumber() {
		BuildingState buildingState = new BuildingState(-2, 10, 2);
		Assertions.assertThat(buildingState.floorInRange(-3, 0)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(-2, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(0, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(3, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(4, 0)).isFalse();

		Assertions.assertThat(buildingState.floorInRange(-2, 1)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(3, 1)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(4, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(6, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(10, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(11, 1)).isFalse();
	}

	@Test
	public void floorInRangeForTwoCabinsAndPairFloorsNumber() {
		BuildingState buildingState = new BuildingState(-2, 9, 2);
		Assertions.assertThat(buildingState.floorInRange(-3, 0)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(-2, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(0, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(3, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(4, 0)).isFalse();

		Assertions.assertThat(buildingState.floorInRange(-2, 1)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(3, 1)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(4, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(6, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(9, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(10, 1)).isFalse();
	}

	@Test
	public void floorInRangeForThreeCabinsAndTwentyFloors() {
		BuildingState buildingState = new BuildingState(-2, 17, 3);
		Assertions.assertThat(buildingState.floorInRange(-3, 0)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(-2, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(0, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(3, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(4, 0)).isFalse();

		Assertions.assertThat(buildingState.floorInRange(3, 1)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(4, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(6, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(10, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(11, 1)).isFalse();

		Assertions.assertThat(buildingState.floorInRange(10, 2)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(11, 2)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(14, 2)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(17, 2)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(18, 2)).isFalse();
	}

	@Test
	public void floorInRangeForThreeCabinsAndTenFloors() {
		BuildingState buildingState = new BuildingState(-2, 7, 3);
		Assertions.assertThat(buildingState.floorInRange(-3, 0)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(-2, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(0, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(1, 0)).isFalse();

		Assertions.assertThat(buildingState.floorInRange(0, 1)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(1, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(3, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(4, 1)).isFalse();

		Assertions.assertThat(buildingState.floorInRange(3, 2)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(4, 2)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(7, 2)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(8, 2)).isFalse();
	}

}
