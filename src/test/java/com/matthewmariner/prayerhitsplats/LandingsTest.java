package com.matthewmariner.prayerhitsplats;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class LandingsTest
{
	private final Landings landings = new Landings();

	@Test
	public void aProjectileLandingWithTheHitIsTakenOnce()
	{
		landings.aimed("arrow", 130);

		assertTrue(landings.take(120));
		assertFalse(landings.take(120));
	}

	@Test
	public void aProjectileStillInFlightIsNotTaken()
	{
		landings.aimed("bolt", 200);

		assertFalse(landings.take(200 - Landings.TOLERANCE - 1));
		assertTrue(landings.take(200));
	}

	@Test
	public void aProjectileThatLandedTicksAgoIsForgotten()
	{
		landings.aimed("spell", 100);

		assertFalse(landings.take(100 + Landings.TOLERANCE + 1));
	}

	@Test
	public void aProjectileReportedAgainAsYouMoveCountsOnce()
	{
		Object spell = new Object();
		landings.aimed(spell, 140);
		landings.aimed(spell, 150);

		assertTrue(landings.take(150));
		assertFalse(landings.take(150));
	}
}
