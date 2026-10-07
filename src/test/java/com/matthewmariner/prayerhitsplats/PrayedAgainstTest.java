package com.matthewmariner.prayerhitsplats;

import static com.matthewmariner.prayerhitsplats.PrayerHitsplatsPlugin.prayedAgainst;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PrayedAgainstTest
{
	@Test
	public void meleeIsStoppedOnlyByProtectFromMelee()
	{
		assertTrue(prayedAgainst(true, true, false, false));
		assertFalse("ranged prayer against a melee hit", prayedAgainst(true, false, true, false));
		assertFalse("magic prayer against a melee hit", prayedAgainst(true, false, false, true));
	}

	@Test
	public void aProjectileIsStoppedByEitherRangedOrMagicPrayer()
	{
		assertTrue(prayedAgainst(false, false, true, false));
		assertTrue(prayedAgainst(false, false, false, true));
		assertFalse("melee prayer against a projectile", prayedAgainst(false, true, false, false));
	}
}
