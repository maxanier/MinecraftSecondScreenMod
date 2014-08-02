package de.maxgb.minecraft.second_screen.util;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * Event which forces an update of the listener corrosponding to the referred class
 * @author Max
 *
 */
@SuppressWarnings("rawtypes")
public class ForceUpdateEvent extends Event {
	public final Class listener;

	public ForceUpdateEvent(Class updateListener) {
		listener = updateListener;
	}
}
