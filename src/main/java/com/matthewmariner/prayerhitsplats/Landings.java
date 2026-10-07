package com.matthewmariner.prayerhitsplats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Projectiles aimed at you, kept until one lands with a hitsplat. A hit that arrives with one was
 * ranged or magic.
 */
final class Landings
{
	/** A projectile lands on the tick its hitsplat does: within 30 client cycles, either side. */
	static final int TOLERANCE = 30;

	/**
	 * Landing cycle per projectile, keyed by the projectile because the client reports one aimed at
	 * an actor again each time that actor moves.
	 */
	private final Map<Object, Integer> ends = new HashMap<>();

	void aimed(Object projectile, int endCycle)
	{
		ends.put(projectile, endCycle);
	}

	/** Takes the projectile landing within a tick of {@code now}, if there is one. */
	boolean take(int now)
	{
		ends.values().removeIf(end -> end < now - TOLERANCE);
		for (Iterator<Integer> it = ends.values().iterator(); it.hasNext(); )
		{
			if (it.next() <= now + TOLERANCE)
			{
				it.remove();
				return true;
			}
		}
		return false;
	}
}
