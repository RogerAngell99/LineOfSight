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
package com.krazune.lineofsight;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import javax.inject.Inject;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashSet;

import net.runelite.api.Client;
import net.runelite.api.CollisionDataFlag;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

// One day I will refactor this mess but today is not that day
public class TilesOverlay extends Overlay
{
	Client client;

	LineOfSightPluginConfig config;

	@Inject
	public TilesOverlay(Client client, LineOfSightPluginConfig config)
	{
		this.client = client;
		this.config = config;

		setOverlayConfigurations();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.showPlayerLos())
		{
			if (config.includeAsymmetrical())
			{
				// It's not efficient to render these separately from the regular LOS, but will do for now
				renderAsymmetricalLineOfSight(graphics);
			}

			renderLineOfSight(graphics);
		}

		if (config.showNpcLos())
		{
			renderNpcLineOfSight(graphics);
		}

		return null;
	}

	private void setOverlayConfigurations()
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	private void renderAsymmetricalLineOfSight(Graphics2D graphics)
	{
		HashSet<WorldPoint> asymmetricalSightPoints = new HashSet<>();
		WorldPoint[][] points = getAsymmetricalSightWorldPoints();
		for (WorldPoint[] row : points) {
			for (WorldPoint point : row) {
				if (point != null) {
					asymmetricalSightPoints.add(point);
				}
			}
		}

		renderWorldPoints(
			graphics,
			asymmetricalSightPoints,
			config.playerOutlineOnly(),
			config.showAsymmetricalFill(),
			config.asymmetricalFillColor(),
			config.asymmetricalBorderColor(),
			config.asymmetricalBorderWidth(),
			config.playerOverlayRange() * 2 + 1
		);
	}

	private void renderLineOfSight(Graphics2D graphics)
	{
		HashSet<WorldPoint> sightPoints = new HashSet<>();
		WorldPoint[][] points = getSightWorldPoints(config.includePlayerTile());
		for (WorldPoint[] row : points) {
			for (WorldPoint point : row) {
				if (point != null) {
					sightPoints.add(point);
				}
			}
		}

		renderWorldPoints(
			graphics,
			sightPoints,
			config.playerOutlineOnly(),
			config.showFill(),
			config.fillColor(),
			config.borderColor(),
			config.borderWidth(),
			config.playerOverlayRange() * 2 + 1
		);
	}

	private void renderNpcLineOfSight(Graphics2D graphics)
	{
		List<NPC> npcs = client.getNpcs();
		String npcNameFilter = config.npcNameFilter();
		List<String> npcNameFilters = Arrays.stream(npcNameFilter.split(",")).map(String::trim).collect(Collectors.toList());
		HashSet<WorldPoint> mergedSightPoints = new HashSet<>();

		for (NPC npc : npcs)
		{
			if (npc == null || npc.getName() == null)
			{
				continue;
			}

			if (!npcNameFilter.isEmpty() && !npcNameFilters.stream().anyMatch(name -> npc.getName().equalsIgnoreCase(name)))
			{
				continue;
			}

			mergedSightPoints.addAll(Arrays.stream(getNpcSightWorldPoints(npc)).flatMap(Arrays::stream).filter(java.util.Objects::nonNull).collect(Collectors.toSet()));
		}

		renderWorldPoints(
			graphics,
			mergedSightPoints,
			config.npcOutlineOnly(),
			config.showNpcFill(),
			config.npcFillColor(),
			config.npcBorderColor(),
			config.npcBorderWidth(),
			config.npcOverlayRange() * 2 + 1
		);
	}

	private WorldPoint[][] getNpcSightWorldPoints(NPC npc)
	{
		int areaLength = config.npcOverlayRange() * 2 + 1;
		WorldPoint[][] worldPoints = new WorldPoint[areaLength][areaLength];
		WorldArea area = npc.getWorldArea();

		if (area == null)
		{
			return worldPoints;
		}

		int initialX = area.getX() - config.npcOverlayRange();
		int initialY = area.getY() - config.npcOverlayRange();
		int maxX = area.getX() + config.npcOverlayRange();
		int maxY = area.getY() + config.npcOverlayRange();
		WorldView worldView = client.getTopLevelWorldView();

		for (int x = initialX, i = 0; x <= maxX; ++x, ++i)
		{
			for (int y = initialY, j = 0; y <= maxY; ++y, ++j)
			{
				WorldPoint newSightWorldPoint = new WorldPoint(x, y, area.getPlane());

				if (area.hasLineOfSightTo(worldView, newSightWorldPoint) || (x == area.getX() && y == area.getY() && config.includeNpcTile()))
				{
					worldPoints[i][j] = newSightWorldPoint;
				}
			}
		}

		return worldPoints;
	}

	private void renderWorldPoints(Graphics2D graphics, HashSet<WorldPoint> sightPoints, boolean outlineOnly, boolean showFill, Color fillColor, Color borderColor, int borderWidth, int areaLength)
	{
		if (outlineOnly)
		{
			renderOutlineWorldPoints(graphics, sightPoints, showFill, fillColor, borderColor, borderWidth, areaLength);
			return;
		}

		renderWorldPointsFullGrid(graphics, sightPoints, showFill, fillColor, borderColor, borderWidth, areaLength);
	}

	private WorldPoint[][] getSightWorldPoints(boolean includePlayerTile)
	{
		int areaLength = config.playerOverlayRange() * 2 + 1;
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

		int initialX = area.getX() - config.playerOverlayRange();
		int initialY = area.getY() - config.playerOverlayRange();
		int maxX = area.getX() + config.playerOverlayRange();
		int maxY = area.getY() + config.playerOverlayRange();
		WorldView worldView = client.getTopLevelWorldView();

		for (int x = initialX, i = 0; x <= maxX; ++x, ++i)
		{
			for (int y = initialY, j = 0; y <= maxY; ++y, ++j)
			{
				WorldPoint newSightWorldPoint = new WorldPoint(x, y, area.getPlane());

				if (area.hasLineOfSightTo(worldView, newSightWorldPoint) || (x == area.getX() && y == area.getY() && includePlayerTile))
				{
					worldPoints[i][j] = newSightWorldPoint;
				}
			}
		}

		return worldPoints;
	}

	private WorldPoint[][] getAsymmetricalSightWorldPoints()
	{
		int areaLength = config.playerOverlayRange() * 2 + 1;
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

		int initialX = area.getX() - config.playerOverlayRange();
		int initialY = area.getY() - config.playerOverlayRange();
		int maxX = area.getX() + config.playerOverlayRange();
		int maxY = area.getY() + config.playerOverlayRange();
		WorldView worldView = client.getTopLevelWorldView();
		int[][] collisionFlags = worldView.getCollisionMaps()[area.getPlane()].getFlags();

		for (int x = initialX, i = 0; x <= maxX; ++x, ++i)
		{
			for (int y = initialY, j = 0; y <= maxY; ++y, ++j)
			{
				if ((x == area.getX() && y == area.getY()))
				{
					continue;
				}

				WorldPoint newSightWorldPoint = new WorldPoint(x, y, area.getPlane());
				boolean hasSight = area.hasLineOfSightTo(worldView, newSightWorldPoint);
				boolean hasSightFromTarget = newSightWorldPoint.toWorldArea().hasLineOfSightTo(worldView, area.toWorldPoint());

				if (!hasSight && hasSightFromTarget)
				{
					LocalPoint localPoint = LocalPoint.fromWorld(worldView, newSightWorldPoint);
					int collisionFlag = collisionFlags[localPoint.getSceneX()][localPoint.getSceneY()];

					if ((collisionFlag & CollisionDataFlag.BLOCK_LINE_OF_SIGHT_FULL) == 0)
					{
						worldPoints[i][j] = newSightWorldPoint;
					}
				}
			}
		}

		return worldPoints;
	}

	private void renderOutlineWorldPoints(Graphics2D graphics, HashSet<WorldPoint> sightPoints, boolean showFill, Color fillColor, Color borderColor, int borderWidth, int areaLength)
	{
		for (WorldPoint point : sightPoints)
		{
			if (showFill)
			{
				Polygon tilePolygon = generatePolygonFromWorldPoint(point);

				if (tilePolygon != null)
				{
					graphics.setColor(fillColor);
					graphics.fill(tilePolygon);
				}
			}

			boolean topBorder = !sightPoints.contains(new WorldPoint(point.getX(), point.getY() + 1, point.getPlane()));
			boolean rightBorder = !sightPoints.contains(new WorldPoint(point.getX() + 1, point.getY(), point.getPlane()));
			boolean bottomBorder = !sightPoints.contains(new WorldPoint(point.getX(), point.getY() - 1, point.getPlane()));
			boolean leftBorder = !sightPoints.contains(new WorldPoint(point.getX() - 1, point.getY(), point.getPlane()));

			renderWorldPointBorders(graphics, point, topBorder, rightBorder, bottomBorder, leftBorder, borderColor, borderWidth);
		}
	}

	private Polygon generatePolygonFromWorldPoint(WorldPoint worldPoint)
	{
		LocalPoint localPoint = LocalPoint.fromWorld(client.getTopLevelWorldView(), worldPoint);

		if (localPoint == null)
		{
			return null;
		}

		return Perspective.getCanvasTilePoly(client, localPoint);
	}

	private void renderWorldPointBorders(Graphics2D graphics, WorldPoint worldPoint, boolean topBorder, boolean rightBorder, boolean bottomBorder, boolean leftBorder, Color borderColor, int borderWidth)
	{
		WorldView worldView = client.getTopLevelWorldView();
		LocalPoint localPoint = LocalPoint.fromWorld(worldView, worldPoint);

		if (localPoint == null)
		{
			return;
		}

		int plane = worldPoint.getPlane();

		graphics.setColor(borderColor);
		graphics.setStroke(new BasicStroke(borderWidth));

		if (topBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() + 64, worldView), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() + 64, worldView), plane);

				if (canvasPointB != null)
				{
					int x2 = canvasPointB.getX();
					int y2 = canvasPointB.getY();

					graphics.drawLine(x1, y1, x2, y2);
				}
			}
		}

		if (rightBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() - 64, worldView), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() + 64, worldView), plane);

				if (canvasPointB != null)
				{
					int x2 = canvasPointB.getX();
					int y2 = canvasPointB.getY();

					graphics.drawLine(x1, y1, x2, y2);
				}
			}
		}

		if (bottomBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() - 64, worldView), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() - 64, worldView), plane);

				if (canvasPointB != null)
				{
					int x2 = canvasPointB.getX();
					int y2 = canvasPointB.getY();

					graphics.drawLine(x1, y1, x2, y2);
				}
			}
		}

		if (leftBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() - 64, worldView), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() + 64, worldView), plane);

				if (canvasPointB != null)
				{
					int x2 = canvasPointB.getX();
					int y2 = canvasPointB.getY();

					graphics.drawLine(x1, y1, x2, y2);
				}
			}
		}
	}

	private void renderWorldPointsFullGrid(Graphics2D graphics, HashSet<WorldPoint> sightPoints, boolean showFill, Color fillColor, Color borderColor, int borderWidth, int areaLength)
	{
		Color transparent = new Color(0, 0, 0, 0);
		Stroke stroke = new BasicStroke(borderWidth);

		for (WorldPoint point : sightPoints)
		{
			Polygon polygon = generatePolygonFromWorldPoint(point);

			if (polygon == null)
			{
				continue;
			}

			if (showFill)
			{
				OverlayUtil.renderPolygon(graphics, polygon, borderColor, fillColor, stroke);
			}
			else
			{
				OverlayUtil.renderPolygon(graphics, polygon, borderColor, transparent, stroke);
			}
		}
	}
}
