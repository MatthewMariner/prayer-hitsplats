package com.matthewmariner.prayerhitsplats;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageUtil;

/**
 * Draws a recoloured 0 over the client's own blue one, in the same slot, for as long as it shows.
 */
class PrayerHitsplatsOverlay extends Overlay
{
	/** Where the client draws each hitsplat slot, relative to the first. */
	private static final int[][] SLOT_OFFSETS = {{0, 0}, {0, -20}, {-15, -10}, {15, -10}};

	private final Client client;
	private final PrayerHitsplatsConfig config;
	private final SpriteManager spriteManager;

	/** Game cycle each slot's tint lasts until; 0 when the slot holds nothing to tint. */
	private final int[] tintedUntil = new int[SplatSlots.COUNT];
	private BufferedImage cover;
	private Color coverColour;

	@Inject
	PrayerHitsplatsOverlay(Client client, PrayerHitsplatsConfig config, SpriteManager spriteManager)
	{
		this.client = client;
		this.config = config;
		this.spriteManager = spriteManager;
		setPosition(OverlayPosition.DYNAMIC);
		// Above the client's own hitsplats ("above overheads"), beneath the interfaces.
		setLayer(OverlayLayer.UNDER_WIDGETS);
	}

	void tint(int slot, int until)
	{
		tintedUntil[slot] = until;
	}

	void clear()
	{
		Arrays.fill(tintedUntil, 0);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		int now = client.getGameCycle();
		if (Arrays.stream(tintedUntil).noneMatch(until -> until > now))
		{
			return null;
		}

		Player player = client.getLocalPlayer();
		BufferedImage image = cover();
		if (player == null || image == null)
		{
			return null;
		}

		Point anchor = player.getCanvasImageLocation(image, player.getLogicalHeight() / 2);
		if (anchor == null)
		{
			return null;
		}

		for (int slot = 0; slot < SplatSlots.COUNT; slot++)
		{
			if (tintedUntil[slot] > now)
			{
				graphics.drawImage(image, anchor.getX() + SLOT_OFFSETS[slot][0], anchor.getY() + SLOT_OFFSETS[slot][1], null);
			}
		}
		return null;
	}

	private BufferedImage cover()
	{
		Color colour = config.colour();
		if (cover == null || !colour.equals(coverColour))
		{
			BufferedImage sprite = spriteManager.getSprite(SpriteID.Hitmark.HITSPLAT_BLUE_MISS, 0);
			if (sprite == null)
			{
				return null;
			}
			cover = cover(sprite, colour);
			coverColour = colour;
		}
		return cover;
	}

	/**
	 * The miss splat in {@code colour} with its 0, and a 1px rim so a pixel of offset from the
	 * splat underneath cannot show through.
	 */
	private static BufferedImage cover(BufferedImage sprite, Color colour)
	{
		BufferedImage padded = new BufferedImage(sprite.getWidth() + 2, sprite.getHeight() + 2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = padded.createGraphics();
		g.drawImage(tint(sprite, colour), 1, 1, null);
		g.dispose();

		BufferedImage image = ImageUtil.outlineImage(padded, colour.darker());
		g = image.createGraphics();
		g.setFont(FontManager.getRunescapeSmallFont());
		FontMetrics metrics = g.getFontMetrics();
		int x = (image.getWidth() - metrics.stringWidth("0")) / 2;
		int y = (image.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
		g.setColor(Color.BLACK);
		g.drawString("0", x + 1, y + 1);
		g.setColor(Color.WHITE);
		g.drawString("0", x, y);
		g.dispose();
		return image;
	}

	/** {@code sprite} in the hue of {@code colour}, each pixel keeping its own brightness and alpha. */
	static BufferedImage tint(BufferedImage sprite, Color colour)
	{
		float[] hsb = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
		BufferedImage tinted = new BufferedImage(sprite.getWidth(), sprite.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < sprite.getWidth(); x++)
		{
			for (int y = 0; y < sprite.getHeight(); y++)
			{
				int argb = sprite.getRGB(x, y);
				float brightness = Math.max(argb >> 16 & 0xFF, Math.max(argb >> 8 & 0xFF, argb & 0xFF)) / 255f;
				int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * brightness) & 0xFFFFFF;
				tinted.setRGB(x, y, argb & 0xFF000000 | rgb);
			}
		}
		return tinted;
	}
}
