package com.matthewmariner.prayerhitsplats;

import static com.matthewmariner.prayerhitsplats.PrayerHitsplatsPlugin.MAGIC;
import static com.matthewmariner.prayerhitsplats.PrayerHitsplatsPlugin.MISSILES;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LandingsTest
{
	private final Landings landings = new Landings();

	@Test
	public void aProjectileLandingWithTheHitIsTakenOnce()
	{
		landings.aimed("arrow", 130);
		landings.settle(MISSILES, 100);

		assertEquals(MISSILES, landings.take(120));
		assertEquals(Landings.NONE, landings.take(120));
	}

	@Test
	public void aProjectileStillInFlightIsNotTaken()
	{
		landings.aimed("bolt", 200);
		landings.settle(MISSILES, 100);

		assertEquals(Landings.NONE, landings.take(200 - Landings.TOLERANCE - 1));
		assertEquals(MISSILES, landings.take(200));
	}

	@Test
	public void aProjectileThatLandedTicksAgoIsForgotten()
	{
		landings.aimed("spell", 100);
		landings.settle(MAGIC, 100);

		assertEquals(Landings.NONE, landings.take(100 + Landings.TOLERANCE + 1));
	}

	@Test
	public void aProjectileKeepsThePrayersFromTheTickItWasFired()
	{
		Object spell = new Object();
		landings.aimed(spell, 140);
		landings.settle(MAGIC, 100);
		// Reported again a tick later, after the prayer was flicked off.
		landings.aimed(spell, 150);
		landings.settle(0, 100);

		assertEquals(MAGIC, landings.take(150));
		assertEquals(Landings.NONE, landings.take(150));
	}

	@Test
	public void aProjectileThatNeverBringsAHitIsForgottenAtTheNextTick()
	{
		landings.aimed("cosmetic", 100);
		landings.settle(MAGIC, 100 + Landings.TOLERANCE + 1);

		// Asked about its own landing cycle, it would still match if it had been kept.
		assertEquals(Landings.NONE, landings.take(100));
	}

	@Test
	public void twoProjectilesLandingTogetherAreTakenOneHitEach()
	{
		landings.aimed("first arrow", 130);
		landings.aimed("second arrow", 131);
		landings.settle(MISSILES, 100);

		assertEquals(MISSILES, landings.take(130));
		assertEquals(MISSILES, landings.take(130));
		assertEquals(Landings.NONE, landings.take(130));
	}
}
