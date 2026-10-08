package com.matthewmariner.prayerhitsplats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Projectiles aimed at you, kept until one lands with a hitsplat, together with the protection
 * prayers up on the tick each was fired: those are the ones the game judges the hit by.
 */
final class Landings
{
	/** A projectile lands on the tick its hitsplat does: within 30 client cycles, either side. */
	static final int TOLERANCE = 30;

	/** What {@link #take} returns when no projectile landed with the hit. */
	static final int NONE = -1;

	private static final class Landing
	{
		private int prayers = NONE;
		private int end;
	}

	/** Keyed by the projectile because the client reports one more than once; its landing cycle follows each report. */
	private final Map<Object, Landing> landings = new HashMap<>();

	void aimed(Object projectile, int endCycle)
	{
		landings.computeIfAbsent(projectile, p -> new Landing()).end = endCycle;
	}

	/**
	 * Gives every projectile first seen this tick the prayers the server had up by its end, and
	 * forgets any that landed without a hit, so a projectile that never brings one is not kept.
	 */
	void settle(int prayers, int now)
	{
		forget(now);
		for (Landing landing : landings.values())
		{
			if (landing.prayers == NONE)
			{
				landing.prayers = prayers;
			}
		}
	}

	/**
	 * Takes the projectile landing within a tick of {@code now}.
	 *
	 * @return the prayers up on the tick it was fired, or {@link #NONE}
	 */
	int take(int now)
	{
		forget(now);
		for (Iterator<Landing> it = landings.values().iterator(); it.hasNext(); )
		{
			Landing landing = it.next();
			if (landing.end <= now + TOLERANCE)
			{
				it.remove();
				return landing.prayers;
			}
		}
		return NONE;
	}

	private void forget(int now)
	{
		landings.values().removeIf(landing -> landing.end < now - TOLERANCE);
	}
}
