/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2021, Miguel Sousa
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
