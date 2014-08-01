package de.maxgb.minecraft.second_screen.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.util.Constants;

public class ModConfigGui extends GuiConfig {

	public ModConfigGui(GuiScreen guiScreen){
		super(guiScreen,getConfigElements(),Constants.MOD_ID,true,false,GuiConfig.getAbridgedConfigPath(Configs.config.toString()));
	}
	
	private static List<IConfigElement> getConfigElements(){
		List<IConfigElement> elements = new ArrayList<IConfigElement>();
		elements.addAll(new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_GENERAL)).getChildElements());
		elements.addAll(new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_CONNECTION_SETTINGS)).getChildElements());
		elements.addAll(new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_UPDATE_TIMES)).getChildElements());
		return elements;
	}

}
