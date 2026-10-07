package com.matthewmariner.prayerhitsplats;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SplatSlotsTest
{
	private final SplatSlots slots = new SplatSlots();

	@Test
	public void aLoneHitsplatTakesTheFirstSlot()
	{
		assertEquals(0, slots.place(100, 170));
		assertEquals(0, slots.place(200, 270));
	}

	@Test
	public void aSlotIsFreeOnTheCycleItsHitsplatDisappears()
	{
		slots.place(100, 170);

		assertEquals(0, slots.place(170, 240));
	}

	@Test
	public void overlappingHitsplatsFillTheSlotsInOrder()
	{
		assertEquals(0, slots.place(100, 170));
		assertEquals(1, slots.place(101, 171));
		assertEquals(2, slots.place(102, 172));
		assertEquals(3, slots.place(103, 173));
	}

	@Test
	public void aFreedSlotIsSkippedUntilTheRoundRobinComesBackToIt()
	{
		slots.place(100, 200);
		slots.place(101, 150);

		// Slot 1 is free again, but the client moves on to slot 2 while slot 0 still shows.
		assertEquals(2, slots.place(160, 230));
	}

	@Test
	public void theOrderRestartsOnceEverySlotIsFree()
	{
		slots.place(100, 170);
		slots.place(101, 171);

		assertEquals(0, slots.place(171, 241));
	}

	@Test
	public void whenAllFourShowTheOneClosestToDisappearingIsReplaced()
	{
		slots.place(100, 190);
		slots.place(101, 160);
		slots.place(102, 180);
		slots.place(103, 170);

		assertEquals(1, slots.place(104, 174));
	}
}
