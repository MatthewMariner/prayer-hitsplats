package com.matthewmariner.prayerhitsplats;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Projectile;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * A 0 means the attack missed or a protection prayer stopped it, and the game draws both the same.
 * This recolours the second kind, so a blue 0 on you is a real miss.
 */
@Slf4j
@PluginDescriptor(
	name = "Prayer Hitsplats",
	description = "Recolours the 0 you take while praying against the attack, so it no longer looks like a plain miss",
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
	private final Landings landings = new Landings();

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
	public void onProjectileMoved(ProjectileMoved event)
	{
		Projectile projectile = event.getProjectile();
		if (projectile.getTargetActor() == client.getLocalPlayer())
		{
			landings.aimed(projectile, projectile.getEndCycle());
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		Player player = client.getLocalPlayer();
		if (event.getActor() != player)
		{
			return;
		}

		Hitsplat hitsplat = event.getHitsplat();
		int now = client.getGameCycle();
		int end = hitsplat.getDisappearsOnGameCycle();
		// Every hitsplat on you is placed, tinted or not, so the slots stay in step with the client's.
		int slot = slots.place(now, end);
		// isMine() is an attack's hit or block on you, never poison and the like, so only it takes a projectile.
		boolean melee = hitsplat.isMine() && !landings.take(now) && attackerInMeleeReach(player);
		boolean protectMelee = isOn(VarbitID.PRAYER_PROTECTFROMMELEE);
		boolean protectMissiles = isOn(VarbitID.PRAYER_PROTECTFROMMISSILES);
		boolean protectMagic = isOn(VarbitID.PRAYER_PROTECTFROMMAGIC);
		boolean prayed = prayedAgainst(melee, protectMelee, protectMissiles, protectMagic);
		overlay.tint(slot, hitsplat.getHitsplatType() == HitsplatID.BLOCK_ME && prayed ? end : 0);
		log.debug("hitsplat type={} amount={} melee={} protect melee/missiles/magic={}/{}/{} slot={}",
			hitsplat.getHitsplatType(), hitsplat.getAmount(), melee, protectMelee, protectMissiles, protectMagic, slot);
	}

	/**
	 * Melee is told apart from ranged and magic, but ranged and magic are not told apart, so either
	 * of their prayers counts against both.
	 */
	static boolean prayedAgainst(boolean melee, boolean protectMelee, boolean protectMissiles, boolean protectMagic)
	{
		return melee ? protectMelee : protectMissiles || protectMagic;
	}

	/** A hit with no projectile was melee only if something attacking you stands where melee reaches. */
	private boolean attackerInMeleeReach(Player player)
	{
		WorldArea area = player.getWorldArea();
		for (NPC npc : client.getTopLevelWorldView().npcs())
		{
			if (npc.getInteracting() == player && npc.getWorldArea().isInMeleeDistance(area))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isOn(int varbit)
	{
		return client.getVarbitValue(varbit) == 1;
	}

	@Provides
	PrayerHitsplatsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PrayerHitsplatsConfig.class);
	}
}
