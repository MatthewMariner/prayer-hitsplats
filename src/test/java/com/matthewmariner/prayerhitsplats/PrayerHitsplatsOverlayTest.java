package com.matthewmariner.prayerhitsplats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.awt.Color;
import java.awt.image.BufferedImage;
import org.junit.Test;

public class PrayerHitsplatsOverlayTest
{
	private static final Color AMBER = new Color(255, 176, 0);

	@Test
	public void tintKeepsShapeAndShadingInTheNewHue()
	{
		BufferedImage sprite = new BufferedImage(3, 1, BufferedImage.TYPE_INT_ARGB);
		sprite.setRGB(1, 0, 0xFF0040C8);
		sprite.setRGB(2, 0, 0xFF001464);

		BufferedImage tinted = PrayerHitsplatsOverlay.tint(sprite, AMBER);

		assertEquals("transparent stays transparent", 0, tinted.getRGB(0, 0) >>> 24);
		assertEquals(0xFF, tinted.getRGB(1, 0) >>> 24);
		assertEquals(hue(AMBER), hue(new Color(tinted.getRGB(1, 0))), 0.01f);
		assertTrue("the darker pixel stays darker", brightness(tinted.getRGB(2, 0)) < brightness(tinted.getRGB(1, 0)));
	}

	private static float hue(Color colour)
	{
		return Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null)[0];
	}

	private static float brightness(int rgb)
	{
		Color colour = new Color(rgb);
		return Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null)[2];
	}
}
