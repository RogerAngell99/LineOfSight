package com.krazune.lineofsight;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class LineOfSightPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(LineOfSightPlugin.class);
		RuneLite.main(args);
	}
}
