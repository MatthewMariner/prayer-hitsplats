package com.matthewmariner.prayerhitsplats;

import com.google.inject.Provides;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.Player;
import net.runelite.api.Projectile;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.GameTick;
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
	/** Bits of a protection-prayer mask. */
	static final int MELEE = 1, MISSILES = 2, MAGIC = 4;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PrayerHitsplatsOverlay overlay;

	private final SplatSlots slots = new SplatSlots();
	private final Landings landings = new Landings();

	/**
	 * The protection prayers the server had up after each of the last two ticks, so a projectile is
	 * judged by what was up when it was fired, not when it lands.
	 */
	private int previous, current;
	private int ticks;

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
	public void onGameTick(GameTick event)
	{
		ticks++;
		previous = current;
		current = protection();
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event)
	{
		Projectile projectile = event.getProjectile();
		if (projectile.getTargetActor() == client.getLocalPlayer())
		{
			// Either tick, until a test shows whether a projectile is first seen before or after its tick ends.
			landings.aimed(projectile, projectile.getEndCycle(), previous | current);
			log.debug("projectile {} tick={} startsIn={}", System.identityHashCode(projectile), ticks,
				projectile.getStartCycle() - client.getGameCycle());
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
		int fired = hitsplat.isMine() ? landings.take(now) : Landings.NONE;
		boolean melee = fired == Landings.NONE && hitsplat.isMine() && attackerInMeleeReach(player);
		// Melee is judged by the prayers up at the hit, a click on that same tick included.
		int prayers = fired == Landings.NONE ? protection() : fired;
		overlay.tint(slot, hitsplat.getHitsplatType() == HitsplatID.BLOCK_ME && prayedAgainst(melee, prayers) ? end : 0);
		log.debug("hitsplat type={} amount={} tick={} projectile={} melee={} judged={} previous={} current={} live={} slot={}",
			hitsplat.getHitsplatType(), hitsplat.getAmount(), ticks, fired != Landings.NONE, melee, prayers,
			previous, current, protection(), slot);
	}

	/**
	 * Melee is told apart from ranged and magic, but ranged and magic are not told apart, so either
	 * of their prayers counts against both.
	 */
	static boolean prayedAgainst(boolean melee, int prayers)
	{
		return (prayers & (melee ? MELEE : MISSILES | MAGIC)) != 0;
	}

	/** A hit with no projectile was melee only if something attacking you stands where melee reaches. */
	private boolean attackerInMeleeReach(Player player)
	{
		WorldArea area = player.getWorldArea();
		WorldView view = client.getTopLevelWorldView();
		return Stream.<Actor>concat(view.npcs().stream(), view.players().stream())
			.anyMatch(actor -> actor.getInteracting() == player && actor.getWorldArea().isInMeleeDistance(area));
	}

	/** The protection prayers the server has up. Never the client's values: it flips a prayer's varbit on click, before the server agrees. */
	private int protection()
	{
		return (up(VarbitID.PRAYER_PROTECTFROMMELEE) ? MELEE : 0)
			| (up(VarbitID.PRAYER_PROTECTFROMMISSILES) ? MISSILES : 0)
			| (up(VarbitID.PRAYER_PROTECTFROMMAGIC) ? MAGIC : 0);
	}

	private boolean up(int varbit)
	{
		return client.getServerVarbitValue(varbit) == 1;
	}

	@Provides
	PrayerHitsplatsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PrayerHitsplatsConfig.class);
	}
}
