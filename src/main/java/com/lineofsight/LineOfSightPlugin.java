package com.lineofsight;

import com.google.inject.Provides;
import com.lineofsight.ui.TilesOverlay;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Line of Sight",
	description = "Adds line of sight information.",
	tags = {
		"line",
		"sight",
		"tiles",
		"markers",
		"range"
	}
)
public class LineOfSightPlugin extends Plugin
{
	@Inject
	private LineOfSightPluginConfig config;

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
	protected void startUp() throws Exception
	{
		overlayManager.add(lineOfSightTilesOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(lineOfSightTilesOverlay);
	}
}
