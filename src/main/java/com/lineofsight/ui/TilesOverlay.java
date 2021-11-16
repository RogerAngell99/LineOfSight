package com.lineofsight.ui;

import com.lineofsight.LineOfSightPluginConfig;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
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
		renderLineOfSightPoints(graphics);

		return null;
	}

	private void renderLineOfSightPoints(Graphics2D graphics)
	{
		WorldArea area = client.getLocalPlayer().getWorldArea();

		if (area == null)
		{
			return;
		}

		int initialX = area.getX() - config.lineOfSightTilesOverlayRange();
		int initialY = area.getY() - config.lineOfSightTilesOverlayRange();
		int maxX = area.getX() + config.lineOfSightTilesOverlayRange();
		int maxY = area.getY() + config.lineOfSightTilesOverlayRange();

		for (int x = initialX; x <= maxX; ++x)
		{
			for (int y = initialY; y <= maxY; ++y)
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

					Polygon polygon = Perspective.getCanvasTilePoly(client, localPoint);

					if (polygon == null)
					{
						continue;
					}

					OverlayUtil.renderPolygon(graphics, polygon, config.lineOfSightTileBorderColor(), new BasicStroke(config.lineOfSightTileBorderWidth()));
				}
			}
		}
	}
}
