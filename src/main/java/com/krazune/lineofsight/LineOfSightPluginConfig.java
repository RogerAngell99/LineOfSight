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

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

import java.awt.Color;

@ConfigGroup("lineofsight")
public interface LineOfSightPluginConfig extends Config
{
	@ConfigSection(
		name = "General",
		description = "General configurations",
		position = 0
	)
	String generalSection = "General";

	@ConfigSection(
		name = "Asymmetrical/PVP",
		description = "Asymmetrical/PVP configurations",
		position = 1,
		closedByDefault = true
	)
	String asymmetricalSection = "Asymmetrical/PVP";

	@ConfigItem(
		position = 0,
		keyName = "overlayRange",
		name = "Range",
		description = "Maximum range of the line of sight overlay.",
		section = generalSection
	)
	@Range(
		min = 1,
		max = 10
	)
	default int overlayRange()
	{
		return 10;
	}

	@ConfigItem(
		position = 1,
		keyName = "outlineOnly",
		name = "Outline only",
		description = "Only show the outer borders.",
		section = generalSection
	)
	default boolean outlineOnly()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "includePlayerTile",
		name = "Include player tile",
		description = "Include the current player's tile in the line of sight area. WARNING: You do not actually have line of sight in this tile.",
		section = generalSection
	)
	default boolean includePlayerTile()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "borderColor",
		name = "Border color",
		description = "Color of the overlay's border.",
		section = generalSection
	)
	@Alpha
	default Color borderColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		position = 4,
		keyName = "borderWidth",
		name = "Border width",
		description = "Width of the overlay's border.",
		section = generalSection
	)
	@Range(
		min = 1
	)
	default int borderWidth()
	{
		return 2;
	}

	// This is unnecessary, can be toggled based on fill alpha
	// Should be removed but might cause breaking changes (configurations) for current installations
	@ConfigItem(
		position = 5,
		keyName = "showFill",
		name = "Tile fill",
		description = "Add fill color to tiles. WARNING: This is a costly feature that might lower your game's performance.",
		section = generalSection
	)
	default boolean showFill()
	{
		return false;
	}

	@ConfigItem(
		position = 6,
		keyName = "fillColor",
		name = "Fill color",
		description = "Color of the overlay's tiles.",
		section = generalSection
	)
	@Alpha
	default Color fillColor()
	{
		return new Color(255, 255, 0, 45);
	}

	@ConfigItem(
		position = 0,
		keyName = "includeAsymmetrical",
		name = "Include asymmetrical/PVP tiles",
		description = "Includes tiles that have line of sight to the player (this is how line of sight is calculated in PVP).",
		section = asymmetricalSection
	)
	default boolean includeAsymmetrical()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "asymmetricalBorderColor",
		name = "Border color",
		description = "Color of the overlay's border.",
		section = asymmetricalSection
	)
	@Alpha
	default Color asymmetricalBorderColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		position = 2,
		keyName = "asymmetricalBorderWidth",
		name = "Border width",
		description = "Color of the overlay's border.",
		section = asymmetricalSection
	)
	@Range(
			min = 1
	)
	default int asymmetricalBorderWidth()
	{
		return 2;
	}

	@ConfigItem(
		position = 3,
		keyName = "showAsymmetricalFill",
		name = "Tile fill",
		description = "Add fill color to tiles. WARNING: This is a costly feature that might lower your game's performance.",
		section = asymmetricalSection
	)
	default boolean showAsymmetricalFill()
	{
		return false;
	}

	@ConfigItem(
		position = 4,
		keyName = "asymmetricalFillColor",
		name = "Fill color",
			description = "Color of the overlay's tiles.",
		section = asymmetricalSection
	)
	@Alpha
	default Color asymmetricalFillColor()
	{
		return new Color(255, 0, 0, 45);
	}
}
