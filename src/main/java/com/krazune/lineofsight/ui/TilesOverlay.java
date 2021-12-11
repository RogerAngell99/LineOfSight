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
import net.runelite.api.Point;
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
		WorldPoint[][] sightPoints = getSightWorldPoints();

		if (config.outlineOnly())
		{
			renderOutlineWorldPoints(graphics, sightPoints);

			return null;
		}

		renderOptimizedWorldPoints(graphics, sightPoints);

		return null;
	}

	private void renderOutlineWorldPoints(Graphics2D graphics, WorldPoint[][] sightPoints)
	{
		int areaLength = config.overlayRange() * 2 + 1;

		for (int x = 0; x < areaLength; ++x)
		{
			for (int y = 0; y < areaLength; ++y)
			{
				if (sightPoints[x][y] == null)
				{
					continue;
				}

				boolean topBorder = y == areaLength - 1 || sightPoints[x][y + 1] == null;
				boolean rightBorder = x == areaLength - 1 || sightPoints[x + 1][y] == null;
				boolean bottomBorder = y == 0 || sightPoints[x][y - 1] == null;
				boolean leftBorder = x == 0 || sightPoints[x - 1][y] == null;

				renderWorldPointBorders(graphics, sightPoints[x][y], topBorder, rightBorder, bottomBorder, leftBorder);
			}
		}
	}

	private void renderWorldPointBorders(Graphics2D graphics, WorldPoint worldPoint, boolean topBorder, boolean rightBorder, boolean bottomBorder, boolean leftBorder)
	{
		LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
		int plane = worldPoint.getPlane();

		graphics.setColor(config.borderColor());
		graphics.setStroke(new BasicStroke(config.borderWidth()));

		if (topBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() + 64), plane);

			int x1 = canvasPointA.getX();
			int y1 = canvasPointA.getY();

			Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() + 64), plane);

			int x2 = canvasPointB.getX();
			int y2 = canvasPointB.getY();

			graphics.drawLine(x1, y1, x2, y2);
		}

		if (rightBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() - 64), plane);

			int x1 = canvasPointA.getX();
			int y1 = canvasPointA.getY();

			Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() + 64), plane);

			int x2 = canvasPointB.getX();
			int y2 = canvasPointB.getY();

			graphics.drawLine(x1, y1, x2, y2);
		}

		if (bottomBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() - 64), plane);

			int x1 = canvasPointA.getX();
			int y1 = canvasPointA.getY();

			Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() - 64), plane);

			int x2 = canvasPointB.getX();
			int y2 = canvasPointB.getY();

			graphics.drawLine(x1, y1, x2, y2);
		}

		if (leftBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() - 64), plane);

			int x1 = canvasPointA.getX();
			int y1 = canvasPointA.getY();

			Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() + 64), plane);

			int x2 = canvasPointB.getX();
			int y2 = canvasPointB.getY();

			graphics.drawLine(x1, y1, x2, y2);
		}
	}

	public WorldPoint[][] getSightWorldPoints()
	{
		int areaLength = config.overlayRange() * 2 + 1;
		WorldPoint[][] worldPoints = new WorldPoint[areaLength][areaLength];

		Player player = client.getLocalPlayer();

		if (player == null)
		{
			return worldPoints;
		}

		WorldArea area = player.getWorldArea();

		if (area == null)
		{
			return worldPoints;
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

				WorldPoint newSightWorldPoint = new WorldPoint(x, y, area.getPlane());

				if (!area.hasLineOfSightTo(client, newSightWorldPoint))
				{
					continue;
				}

				worldPoints[i][j] = newSightWorldPoint;
			}
		}

		return worldPoints;
	}

	private void renderOptimizedWorldPoints(Graphics2D graphics, WorldPoint[][] sightPoints)
	{
		int areaLength = config.overlayRange() * 2 + 1;
		Color transparent = new Color(0, 0, 0, 0);
		Stroke stroke = new BasicStroke(config.borderWidth());

		for (int x = 0; x < areaLength; ++x)
		{
			for (int y = 0; y < areaLength; ++y)
			{
				if (sightPoints[x][y] == null)
				{
					continue;
				}

				Polygon polygon = generatePolygonFromWorldPoint(sightPoints[x][y]);

				if (polygon == null)
				{
					continue;
				}

				if (x == 0 || x == areaLength - 1 || y == 0 || y == areaLength - 1 || sightPoints[x + 1][y] == null || sightPoints[x - 1][y] == null || sightPoints[x][y + 1] == null || sightPoints[x][y - 1] == null)
				{
					OverlayUtil.renderPolygon(graphics, polygon, config.borderColor(), transparent, stroke);

					continue;
				}

				sightPoints[x][y] = null;
			}
		}
	}

	private Polygon generatePolygonFromWorldPoint(WorldPoint worldPoint)
	{
		return Perspective.getCanvasTilePoly(client, LocalPoint.fromWorld(client, worldPoint));
	}
}
