package org.n10.codestory.s03e01.api;

import org.fest.assertions.Assertions;
import org.junit.Test;

/**
 * 
 * @author Guillaume
 */
public class UserTest {

	@Test
	public void testGetTickToGoToCome() {
		User user = new User(Direction.UP, 0);
		user.setFloorToGo(10);
		user.travels();
		Assertions.assertThat(user.getTickToGoToCome(0, true)).isEqualTo(10 + 1 + 1);
	}

	@Test
	public void testGetTickToGoToComeInTravels() {
		User user = new User(Direction.UP, 0);
		user.setFloorToGo(10);
		user.travels();
		Assertions.assertThat(user.getTickToGoToCome(4, false)).isEqualTo(6 + 1);
	}

	@Test
	public void testGetPotentialPointsGiveMaxAtStart() {
		User user = new User(Direction.UP, 0);
		user.tick();
		user.setFloorToGo(10);
		user.travels();
		user.tick();
		for (int i = 0; i < 10; i++) {
			user.tick();
		}
		user.tick();
		Assertions.assertThat(user.getRemainingPoints()).isEqualTo(20);
	}

	@Test
	public void testPointsWhenWaiting() {
		User user = new User(Direction.UP, 2);

		Assertions.assertThat(user.getTickToWaitToCome(0, true)).isEqualTo(4);
		Assertions.assertThat(user.getPotentialPoints(0, true)).isEqualTo(18);
	}
}
