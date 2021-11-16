package com.krazune.lineofsight;

import com.google.inject.Provides;
import com.krazune.lineofsight.ui.TilesOverlay;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Line of Sight",
	description = "Shows the player's line of sight.",
	tags = {
		"line",
		"sight",
		"tiles",
		"markers"
	}
)
public class LineOfSightPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TilesOverlay lineOfSightTilesOverlay;

	@Provides
	LineOfSightPluginConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LineOfSightPluginConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(lineOfSightTilesOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(lineOfSightTilesOverlay);
	}
}
