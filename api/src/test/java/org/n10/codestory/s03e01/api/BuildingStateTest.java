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
		Assertions.assertThat(buildingState.floorInRange(-2, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(10, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(4, 0)).isTrue();
	}
	
	@Test
	public void floorInRangeForTwoCabinsAndImpairFloorsNumber() {
		BuildingState buildingState = new BuildingState(-2, 10, 2);
		Assertions.assertThat(buildingState.floorInRange(-2, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(-2, 1)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(0, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(3, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(4, 0)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(4, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(10, 1)).isTrue();
	}
	
	@Test
	public void floorInRangeForTwoCabinsAndPairFloorsNumber() {
		BuildingState buildingState = new BuildingState(-2, 9, 2);
		Assertions.assertThat(buildingState.floorInRange(-2, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(-2, 1)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(0, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(3, 0)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(4, 0)).isFalse();
		Assertions.assertThat(buildingState.floorInRange(4, 1)).isTrue();
		Assertions.assertThat(buildingState.floorInRange(9, 1)).isTrue();
	}
	
	
}
