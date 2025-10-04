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
		name = "Player",
		description = "Player configurations",
		position = 0
	)
	String playerSection = "Player";

	@ConfigSection(
		name = "Asymmetrical/PVP",
		description = "Asymmetrical/PVP configurations",
		position = 1,
		closedByDefault = true
	)
	String asymmetricalSection = "Asymmetrical/PVP";

	@ConfigSection(
		name = "NPC",
		description = "NPC configurations",
		position = 2,
		closedByDefault = true
	)
	String npcSection = "NPC";

	@ConfigItem(
		position = 0,
		keyName = "playerOverlayRange",
		name = "Player Range",
		description = "Maximum range of the line of sight overlay for the player.",
		section = playerSection
	)
	@Range(
		min = 1,
		max = 10
	)
	default int playerOverlayRange()
	{
		return 10;
	}

	@ConfigItem(
		position = 1,
		keyName = "showPlayerLos",
		name = "Show Player LOS",
		description = "Show line of sight for your player.",
		section = playerSection
	)
	default boolean showPlayerLos()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "playerOutlineOnly",
		name = "Player Outline only",
		description = "Only show the outer borders for the player.",
		section = playerSection
	)
	default boolean playerOutlineOnly()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "includePlayerTile",
		name = "Include player tile",
		description = "Include the current player's tile in the line of sight area. WARNING: You do not actually have line of sight in this tile.",
		section = playerSection
	)
	default boolean includePlayerTile()
	{
		return false;
	}

	@ConfigItem(
		position = 4,
		keyName = "borderColor",
		name = "Border color",
		description = "Color of the overlay's border.",
		section = playerSection
	)
	@Alpha
	default Color borderColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		position = 5,
		keyName = "borderWidth",
		name = "Border width",
		description = "Width of the overlay's border.",
		section = playerSection
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
		position = 6,
		keyName = "showFill",
		name = "Tile fill",
		description = "Add fill color to tiles. WARNING: This is a costly feature that might lower your game's performance.",
		section = playerSection
	)
	default boolean showFill()
	{
		return false;
	}

	@ConfigItem(
		position = 7,
		keyName = "fillColor",
		name = "Fill color",
		description = "Color of the overlay's tiles.",
		section = playerSection
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

	@ConfigItem(
		position = 0,
		keyName = "showNpcLos",
		name = "Show NPC LOS",
		description = "Show line of sight for NPCs.",
		section = npcSection
	)
	default boolean showNpcLos()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "npcNameFilter",
		name = "NPC Name Filter",
		description = "Only show LOS for NPCs with this name.",
		section = npcSection
	)
	default String npcNameFilter()
	{
		return "";
	}

	@ConfigItem(
		position = 2,
		keyName = "npcOverlayRange",
		name = "NPC Range",
		description = "Maximum range of the line of sight overlay for NPCs.",
		section = npcSection
	)
	@Range(
		min = 1,
		max = 10
	)
	default int npcOverlayRange()
	{
		return 10;
	}

	@ConfigItem(
		position = 3,
		keyName = "includeNpcTile",
		name = "Include NPC tile",
		description = "Include the current NPC's tile in the line of sight area. WARNING: The NPC does not actually have line of sight in this tile.",
		section = npcSection
	)
	default boolean includeNpcTile()
	{
		return false;
	}

	@ConfigItem(
		position = 4,
		keyName = "npcOutlineOnly",
		name = "NPC Outline only",
		description = "Only show the outer borders for NPCs.",
		section = npcSection
	)
	default boolean npcOutlineOnly()
	{
		return false;
	}

	@ConfigItem(
		position = 5,
		keyName = "npcBorderColor",
		name = "NPC Border color",
		description = "Color of the NPC overlay's border.",
		section = npcSection
	)
	@Alpha
	default Color npcBorderColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		position = 6,
		keyName = "npcBorderWidth",
		name = "NPC Border width",
		description = "Width of the NPC overlay's border.",
		section = npcSection
	)
	@Range(
		min = 1
	)
	default int npcBorderWidth()
	{
		return 2;
	}

	@ConfigItem(
		position = 7,
		keyName = "showNpcFill",
		name = "NPC Tile fill",
		description = "Add fill color to NPC tiles. WARNING: This is a costly feature that might lower your game's performance.",
		section = npcSection
	)
	default boolean showNpcFill()
	{
		return false;
	}

	@ConfigItem(
		position = 8,
		keyName = "npcFillColor",
		name = "NPC Fill color",
		description = "Color of the NPC overlay's tiles.",
		section = npcSection
	)
	@Alpha
	default Color npcFillColor()
	{
		return new Color(0, 255, 255, 45);
	}
}
