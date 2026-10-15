package com.matthewmariner.prayerhitsplats;

import java.util.HashMap;
import java.util.Map;

/**
 * Projectiles aimed at you, kept until one lands with a hitsplat, together with the protection
 * prayers up on the tick each was fired: one of the two moments a hit is judged by, the other being
 * the tick it lands.
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
		private boolean taken;
	}

	/**
	 * Keyed by the projectile because the client reports one more than once; its landing cycle follows each report.
	 * A taken one stays until it is past {@link #TOLERANCE}, so a report of it in flight is not a new projectile.
	 */
	private final Map<Object, Landing> landings = new HashMap<>();

	void aimed(Object projectile, int endCycle)
	{
		landings.computeIfAbsent(projectile, p -> new Landing()).end = endCycle;
	}

	/**
	 * Gives every projectile first seen this tick the prayers the server had up by its end, and
	 * forgets any that landed without a hit, so a projectile that never brings one is not kept.
	 * A taken projectile keeps what it had.
	 */
	void settle(int prayers, int now)
	{
		forget(now);
		for (Landing landing : landings.values())
		{
			if (!landing.taken && landing.prayers == NONE)
			{
				landing.prayers = prayers;
			}
		}
	}

	/**
	 * Takes the projectile landing nearest {@code now}, within a tick of it; of two as near, the
	 * earlier. Each is taken once, so a pair landing together is one hit each.
	 *
	 * @return the prayers up on the tick it was fired, or {@link #NONE}
	 */
	int take(int now)
	{
		forget(now);
		Landing nearest = null;
		for (Landing landing : landings.values())
		{
			if (landing.taken || landing.end > now + TOLERANCE)
			{
				continue;
			}
			int away = Math.abs(landing.end - now);
			int nearestAway = nearest == null ? 0 : Math.abs(nearest.end - now);
			if (nearest == null || away < nearestAway || away == nearestAway && landing.end < nearest.end)
			{
				nearest = landing;
			}
		}
		if (nearest == null)
		{
			return NONE;
		}
		nearest.taken = true;
		return nearest.prayers;
	}

	private void forget(int now)
	{
		landings.values().removeIf(landing -> landing.end < now - TOLERANCE);
	}
}
