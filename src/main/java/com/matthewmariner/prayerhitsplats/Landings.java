package com.matthewmariner.prayerhitsplats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Projectiles aimed at you, kept until one lands with a hitsplat, together with the protection
 * prayers that were up when each was fired: those are the ones the game judges the hit by.
 */
final class Landings
{
	/** A projectile lands on the tick its hitsplat does: within 30 client cycles, either side. */
	static final int TOLERANCE = 30;

	/** What {@link #take} returns when no projectile landed with the hit. */
	static final int NONE = -1;

	private static final class Landing
	{
		private final int prayers;
		private int end;

		private Landing(int prayers)
		{
			this.prayers = prayers;
		}
	}

	/**
	 * Keyed by the projectile because the client reports one aimed at an actor again each time that
	 * actor moves. The landing cycle follows each report; the prayers stay as first seen.
	 */
	private final Map<Object, Landing> landings = new HashMap<>();

	void aimed(Object projectile, int endCycle, int prayers)
	{
		landings.computeIfAbsent(projectile, p -> new Landing(prayers)).end = endCycle;
	}

	/**
	 * Takes the projectile landing within a tick of {@code now}.
	 *
	 * @return the prayers that were up when it was fired, or {@link #NONE}
	 */
	int take(int now)
	{
		landings.values().removeIf(landing -> landing.end < now - TOLERANCE);
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
}
