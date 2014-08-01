package de.maxgb.minecraft.second_screen.util;

import cpw.mods.fml.common.eventhandler.Event;

@SuppressWarnings("rawtypes")
public class ForceUpdateEvent extends Event {
	public final Class listener;

	public ForceUpdateEvent(Class updateListener) {
		listener = updateListener;
	}
}
