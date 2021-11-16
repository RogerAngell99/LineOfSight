package com.krazune.lineofsight.ui;

import com.krazune.lineofsight.LineOfSightPluginConfig;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class TilesOverlay extends Overlay
{
	Client client;

	LineOfSightPluginConfig config;

	@Inject
	public TilesOverlay(Client client, LineOfSightPluginConfig config)
	{
		this.client = client;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		renderPolygons(graphics, generatePolygons());

		return null;
	}

	private Polygon[][] generatePolygons()
	{
		int areaLength = config.overlayRange() * 2 + 1;
		Polygon[][] polygons = new Polygon[areaLength][areaLength];

		Player player = client.getLocalPlayer();

		if (player == null)
		{
			return polygons;
		}

		WorldArea area = player.getWorldArea();

		if (area == null)
		{
			return polygons;
		}

		int initialX = area.getX() - config.overlayRange();
		int initialY = area.getY() - config.overlayRange();
		int maxX = area.getX() + config.overlayRange();
		int maxY = area.getY() + config.overlayRange();

		for (int x = initialX, i = 0; x <= maxX; ++x, ++i)
		{
			for (int y = initialY, j = 0; y <= maxY; ++y, ++j)
			{
				if (x == area.getX() && y == area.getY())
				{
					continue;
				}

				WorldPoint point = new WorldPoint(x, y, area.getPlane());

				if (area.hasLineOfSightTo(client, point))
				{
					LocalPoint localPoint = LocalPoint.fromWorld(client, point);

					if (localPoint == null)
					{
						continue;
					}

					polygons[i][j] = Perspective.getCanvasTilePoly(client, localPoint);
				}
			}
		}

		return polygons;
	}

	private void renderPolygons(Graphics2D graphics, Polygon[][] polygons)
	{
		int areaLength = config.overlayRange() * 2 + 1;
		Color transparent = new Color(0, 0, 0, 0);
		Stroke stroke = new BasicStroke(config.borderWidth());

		for (int i = 0; i < areaLength; ++i)
		{
			for (int j = 0; j < areaLength; ++j)
			{
				if (polygons[i][j] == null)
				{
					continue;
				}

				if (i == 0 || i == areaLength - 1 || j == 0 || j == areaLength - 1 || polygons[i + 1][j] == null || polygons[i - 1][j] == null || polygons[i][j + 1] == null || polygons[i][j - 1] == null)
				{
					OverlayUtil.renderPolygon(graphics, polygons[i][j], config.borderColor(), transparent, stroke);

					continue;
				}

				polygons[i][j] = null;
			}
		}
	}
}
