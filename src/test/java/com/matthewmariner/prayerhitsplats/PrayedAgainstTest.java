package com.matthewmariner.prayerhitsplats;

import static com.matthewmariner.prayerhitsplats.PrayerHitsplatsPlugin.MAGIC;
import static com.matthewmariner.prayerhitsplats.PrayerHitsplatsPlugin.MELEE;
import static com.matthewmariner.prayerhitsplats.PrayerHitsplatsPlugin.MISSILES;
import static com.matthewmariner.prayerhitsplats.PrayerHitsplatsPlugin.prayedAgainst;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PrayedAgainstTest
{
	@Test
	public void meleeIsStoppedOnlyByProtectFromMelee()
	{
		assertTrue(prayedAgainst(true, MELEE));
		assertFalse("ranged prayer against a melee hit", prayedAgainst(true, MISSILES));
		assertFalse("magic prayer against a melee hit", prayedAgainst(true, MAGIC));
	}

	@Test
	public void aProjectileIsStoppedByEitherRangedOrMagicPrayer()
	{
		assertTrue(prayedAgainst(false, MISSILES));
		assertTrue(prayedAgainst(false, MAGIC));
		assertFalse("melee prayer against a projectile", prayedAgainst(false, MELEE));
		assertFalse("no prayer", prayedAgainst(false, 0));
	}
}
