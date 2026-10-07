package com.matthewmariner.prayerhitsplats;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("prayerhitsplats")
public interface PrayerHitsplatsConfig extends Config
{
	@ConfigItem(
		keyName = "colour",
		name = "Colour",
		description = "Colour of a 0 taken while a protection prayer is up"
	)
	default Color colour()
	{
		return new Color(255, 176, 0);
	}
}
