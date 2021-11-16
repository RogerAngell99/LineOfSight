package com.krazune.lineofsight;

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
		keyName = "overlayRange",
		name = "Range",
		description = "Range of the line of sight overlay."
	)
	@Range(
		min = 1,
		max = 10
	)
	default int overlayRange()
	{
		return 10;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "borderColor",
		name = "Border color",
		description = "Color of the overlay's border."
	)
	default Color borderColor()
	{
		return Color.YELLOW;
	}

	@Range(
		min = 1
	)
	@ConfigItem(
		position = 2,
		keyName = "borderWidth",
		name = "Border width",
		description = "Width of the overlay's border."
	)
	default int borderWidth()
	{
		return 2;
	}
}
