package com.matthewmariner.prayerhitsplats;

/**
 * Which of an actor's four hitsplat slots the client draws each new hitsplat in. The API says when
 * a hitsplat disappears but not where it is drawn, so the slot is replayed from every hitsplat the
 * actor receives: round-robin over the free slots, restarting at the first once all are free.
 */
final class SplatSlots
{
	static final int COUNT = 4;

	private final int[] ends = new int[COUNT];
	private int next;

	/**
	 * @param now game cycle the hitsplat was applied on
	 * @param end game cycle it disappears on
	 * @return the slot it is drawn in
	 */
	int place(int now, int end)
	{
		if (allFree(now))
		{
			next = 0;
		}

		int slot = -1;
		for (int i = 0; i < COUNT && slot < 0; i++)
		{
			if (ends[next] <= now)
			{
				slot = next;
			}
			next = (next + 1) % COUNT;
		}

		if (slot < 0)
		{
			// All four are showing: the one closest to disappearing makes way.
			slot = 0;
			for (int i = 1; i < COUNT; i++)
			{
				if (ends[i] < ends[slot])
				{
					slot = i;
				}
			}
		}

		ends[slot] = end;
		return slot;
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
