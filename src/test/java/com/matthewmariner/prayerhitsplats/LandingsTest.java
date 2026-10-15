package com.matthewmariner.prayerhitsplats;

import static com.matthewmariner.prayerhitsplats.PrayerHitsplatsPlugin.MAGIC;
import static com.matthewmariner.prayerhitsplats.PrayerHitsplatsPlugin.MELEE;
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
	public void aProjectileLandingAFullToleranceAfterTheHitIsTaken()
	{
		landings.aimed("a", 100 + Landings.TOLERANCE);
		landings.settle(MELEE, 100);

		assertEquals(MELEE, landings.take(100));
	}

	@Test
	public void aProjectileThatLandedTicksAgoIsForgotten()
	{
		landings.aimed("spell", 100);
		landings.settle(MAGIC, 100);

		assertEquals(Landings.NONE, landings.take(100 + Landings.TOLERANCE + 1));
	}

	@Test
	public void aProjectileThatLandedAFullToleranceBeforeTheHitIsStillTaken()
	{
		landings.aimed("a", 100);
		landings.settle(MELEE, 90);

		assertEquals(MELEE, landings.take(100 + Landings.TOLERANCE));
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
	public void aProjectileThatNeverBringsAHitIsForgottenATickAfterItLands()
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

	@Test
	public void aPairLandingAFullToleranceApartIsTakenNearestFirst()
	{
		// "a" sorts ahead of "b" in the map, so a first-match take reaches the later orb first.
		landings.aimed("b", 130);
		landings.settle(MISSILES, 100);
		landings.aimed("a", 130 + Landings.TOLERANCE);
		landings.settle(MAGIC, 101);

		// The first hit is in reach of both orbs but belongs to the one landing beside it.
		assertEquals(MISSILES, landings.take(135));
		assertEquals(MAGIC, landings.take(135 + Landings.TOLERANCE));
		assertEquals(Landings.NONE, landings.take(135 + Landings.TOLERANCE));
	}

	@Test
	public void theNearestProjectileIsTakenWhicheverWasAimedFirst()
	{
		// "a" sorts ahead of "b" in the map, so the far one is what a first-match take would reach.
		landings.aimed("a", 150);
		landings.settle(MAGIC, 100);
		landings.aimed("b", 130);
		landings.settle(MISSILES, 101);

		assertEquals(MISSILES, landings.take(128));
		assertEquals(MAGIC, landings.take(128));
	}

	@Test
	public void theNearerLandingWinsEvenWhenTheFartherOneLandedFirst()
	{
		// The far one landed before the hit and the near one lands after it, so only the distance
		// picks, not which side of the hit it is on.
		landings.aimed("a", 120);
		landings.settle(MAGIC, 100);
		landings.aimed("b", 135);
		landings.settle(MISSILES, 101);

		assertEquals(MISSILES, landings.take(130));
		assertEquals(MAGIC, landings.take(130));
	}

	@Test
	public void aTieGoesToTheEarlierLanding()
	{
		landings.aimed("a", 140);
		landings.settle(MAGIC, 100);
		landings.aimed("b", 120);
		landings.settle(MISSILES, 101);

		assertEquals(MISSILES, landings.take(130));
		assertEquals(MAGIC, landings.take(130));
	}

	@Test
	public void aTieGoesToTheEarlierLandingEvenWhenItSortsFirst()
	{
		// The mirror of the tie above: here the earlier landing is the one the map reaches first.
		landings.aimed("a", 120);
		landings.settle(MISSILES, 100);
		landings.aimed("b", 140);
		landings.settle(MAGIC, 101);

		assertEquals(MISSILES, landings.take(130));
		assertEquals(MAGIC, landings.take(130));
	}

	@Test
	public void aTakenProjectileStillInFlightIsNotTakenAgainWhenReported()
	{
		Object orb = new Object();
		landings.aimed(orb, 130);
		landings.settle(MISSILES, 100);
		assertEquals(MISSILES, landings.take(130));

		// The client keeps reporting it until it is gone, after the prayers have changed.
		landings.aimed(orb, 135);
		landings.settle(0, 101);

		assertEquals(Landings.NONE, landings.take(135));
	}

	@Test
	public void aReportedTakenProjectileLeavesTheOtherOneForTheNextHit()
	{
		Object taken = new Object();
		Object other = new Object();
		landings.aimed(taken, 130);
		landings.settle(MISSILES, 100);
		landings.aimed(other, 150);
		landings.settle(MAGIC, 101);
		assertEquals(MISSILES, landings.take(130));

		landings.aimed(taken, 135);
		landings.settle(0, 102);

		assertEquals(MAGIC, landings.take(150));
		assertEquals(Landings.NONE, landings.take(150));
	}

	@Test
	public void aTakenProjectileIsForgottenOncePastTolerance()
	{
		Object orb = new Object();
		landings.aimed(orb, 100);
		landings.settle(MISSILES, 90);
		assertEquals(MISSILES, landings.take(100));

		landings.settle(0, 100 + Landings.TOLERANCE + 1);
		// Forgotten, so a report of it is a new projectile again.
		landings.aimed(orb, 400);
		landings.settle(MAGIC, 380);

		assertEquals(MAGIC, landings.take(400));
	}
}
