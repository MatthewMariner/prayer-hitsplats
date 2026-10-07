package com.matthewmariner.prayerhitsplats;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * A 0 means the attack missed or a protection prayer stopped it, and the game draws both the same.
 * This recolours the second kind, so a blue 0 on you is always a real miss.
 */
@Slf4j
@PluginDescriptor(
	name = "Prayer Hitsplats",
	description = "Recolours the 0 you take while a protection prayer is up, so it no longer looks like a plain miss",
	tags = {"prayer", "protection", "hitsplat", "miss", "block", "combat"}
)
public class PrayerHitsplatsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PrayerHitsplatsOverlay overlay;

	private final SplatSlots slots = new SplatSlots();

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlay.clear();
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (event.getActor() != client.getLocalPlayer())
		{
			return;
		}

		// Every hitsplat on you is placed, tinted or not, so the slots stay in step with the client's.
		Hitsplat hitsplat = event.getHitsplat();
		int end = hitsplat.getDisappearsOnGameCycle();
		int slot = slots.place(client.getGameCycle(), end);
		boolean protecting = isProtecting();
		overlay.tint(slot, hitsplat.getHitsplatType() == HitsplatID.BLOCK_ME && protecting ? end : 0);
		log.debug("hitsplat type={} amount={} protecting={} slot={}", hitsplat.getHitsplatType(), hitsplat.getAmount(), protecting, slot);
	}

	private boolean isProtecting()
	{
		return client.getVarbitValue(VarbitID.PRAYER_PROTECTFROMMELEE) == 1
			|| client.getVarbitValue(VarbitID.PRAYER_PROTECTFROMMISSILES) == 1
			|| client.getVarbitValue(VarbitID.PRAYER_PROTECTFROMMAGIC) == 1;
	}

	@Provides
	PrayerHitsplatsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PrayerHitsplatsConfig.class);
	}
}
