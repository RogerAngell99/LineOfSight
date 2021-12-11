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

	@ConfigItem(
		position = 1,
		keyName = "outlineOnly",
		name = "Outline only",
		description = "Only show the outer borders."
	)
	default boolean outlineOnly()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "borderColor",
		name = "Border color",
		description = "Color of the overlay's border."
	)
	@Alpha
	default Color borderColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		position = 3,
		keyName = "borderWidth",
		name = "Border width",
		description = "Width of the overlay's border."
	)
	@Range(
		min = 1
	)
	default int borderWidth()
	{
		return 2;
	}

	@ConfigItem(
		position = 4,
		keyName = "showFill",
		name = "Tile fill",
		description = "Add fill color to tiles. WARNING: This is a costly feature, and might lower your game's performance."
	)
	default boolean showFill()
	{
		return false;
	}

	@ConfigItem(
		position = 5,
		keyName = "fillColor",
		name = "Fill color",
		description = "Color of the overlay's tiles."
	)
	@Alpha
	default Color fillColor()
	{
		return new Color(0, 0, 0, 64);
	}
}
