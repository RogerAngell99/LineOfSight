package com.lineofsight;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("lineofsight")
public interface LineOfSightPluginConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "lineOfSightTilesOverlayRange",
		name = "Tiles overlay range",
		description = "Range of the line of sight tiles overlay."
	)
	@Range(
		min = 1,
		max = 10
	)
	default int lineOfSightTilesOverlayRange()
	{
		return 10;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "lineOfSightTileBorderColor",
		name = "Border color",
		description = "Color of the tile border."
	)
	default Color lineOfSightTileBorderColor()
	{
		return Color.YELLOW;
	}

	@Range(
		min = 1
	)
	@ConfigItem(
		position = 2,
		keyName = "lineOfSightTileBorderWidth",
		name = "Border width",
		description = "Width of the tile border."
	)
	default int lineOfSightTileBorderWidth()
	{
		return 2;
	}
}
