package com.matthewmariner.prayerhitsplats;

import java.util.Arrays;

/**
 * Which of an actor's four hitsplat slots the client draws each new hitsplat in. The API says when
 * a hitsplat disappears but not where it is drawn, so the slot is replayed from every hitsplat the
 * actor receives: round-robin over the free slots, restarting at the first once all are free.
 */
final class SplatSlots
{
	static final int COUNT = 4;

	/** What {@link #place} returns when the slot cannot be known. */
	static final int UNKNOWN = -1;

	private final int[] ends = new int[COUNT];
	private int next;

	/**
	 * @param now game cycle the hitsplat was applied on
	 * @param end game cycle it disappears on
	 * @return the slot it is drawn in, or {@link #UNKNOWN}
	 */
	int place(int now, int end)
	{
		if (allFree(now))
		{
			next = 0;
		}

		for (int i = 0; i < COUNT; i++)
		{
			int slot = next;
			next = (next + 1) % COUNT;
			if (ends[slot] <= now)
			{
				ends[slot] = end;
				return slot;
			}
		}

		// All four show, and whether the client drops this one or replaces another is not in the
		// API, so every slot counts as unknown until all of them have cleared.
		Arrays.fill(ends, Math.max(end, Arrays.stream(ends).max().getAsInt()));
		return UNKNOWN;
	}

	private boolean allFree(int now)
	{
		for (int end : ends)
		{
			if (end > now)
			{
				return false;
			}
		}
		return true;
	}
}
