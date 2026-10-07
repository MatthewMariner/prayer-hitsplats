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
		landings.aimed("arrow", 130, MISSILES);

		assertEquals(MISSILES, landings.take(120));
		assertEquals(Landings.NONE, landings.take(120));
	}

	@Test
	public void aProjectileStillInFlightIsNotTaken()
	{
		landings.aimed("bolt", 200, MISSILES);

		assertEquals(Landings.NONE, landings.take(200 - Landings.TOLERANCE - 1));
		assertEquals(MISSILES, landings.take(200));
	}

	@Test
	public void aProjectileThatLandedTicksAgoIsForgotten()
	{
		landings.aimed("spell", 100, MAGIC);

		assertEquals(Landings.NONE, landings.take(100 + Landings.TOLERANCE + 1));
	}

	@Test
	public void aProjectileKeepsThePrayersFromWhenItWasFired()
	{
		Object spell = new Object();
		landings.aimed(spell, 140, MAGIC);
		// Reported again as you move, after the prayer was flicked off.
		landings.aimed(spell, 150, 0);

		assertEquals(MAGIC, landings.take(150));
		assertEquals(Landings.NONE, landings.take(150));
	}
}
