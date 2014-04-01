package de.maxgb.minecraft.second_screen.util;

import net.minecraft.item.Item;

/**
 * Util class to read private attributesa from Minecraft classes
 */
public class ReadPrivate {
	public static String getIconString(Item i) {
		try {
			Class<?> c = Item.class;
			java.lang.reflect.Field field = c.getDeclaredField("iconString");
			field.setAccessible(true);
			return (String) field.get(i);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i.getUnlocalizedName();
	}
}
