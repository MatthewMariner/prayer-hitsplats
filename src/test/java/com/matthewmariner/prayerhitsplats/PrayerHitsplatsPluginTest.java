package com.matthewmariner.prayerhitsplats;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PrayerHitsplatsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(PrayerHitsplatsPlugin.class);
		RuneLite.main(args);
	}
}
