package com.tlf.forgeupdater.common;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.tlf.forgeupdater.checker.UpdateCheckManager;
import com.tlf.forgeupdater.command.CommandUpdates;
import com.tlf.forgeupdater.event.EventHandlerCPW;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ForgeUpdater.MODID, name = ForgeUpdater.NAME, version = ForgeUpdater.VERSION)
public class ForgeUpdater
{
	public static final String MODID = "forgeupdater";
	public static final String NAME = "Forge Updater";
	public static final String VERSION = "0.0.1 pre-alpha";
	
	/** The {@link Configuration} for Hide Names */
	public Configuration config;
	
	/** The public instance */
	@Instance(ForgeUpdater.MODID)
	public static ForgeUpdater instance;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		config = new Configuration(new File(event.getModConfigurationDirectory(), "/Updater.cfg"), false);
		
		config.load();
		
		UpdateCheckManager.allowAll = config.get(Configuration.CATEGORY_GENERAL, "allowAll", true, "If false, disallows all update checking. If true, allows activated checkers").getBoolean(true);
		config.addCustomCategoryComment("Mods", "Individual control of mods\nset to false to disable version checking for that mod.");
		
		config.save();
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new EventHandlerCPW());
	}
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event) {
		UpdateCheckManager.INSTANCE.getUpdaters();
	}
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandUpdates());
	}
	
	/** The CurseID for your mod. Find it at curse.com/mc-mods/minecraft/[curseID ]*/
	@Optional.Method(modid = "forgeupdater")
	public String curseID() { return ""; }
	
	/** The file formats to use for this mod, where $mc = minecraft version; $v = mod version. Example: "Hide_Names-$mc-$v.jar" */
	@Optional.Method(modid = "forgeupdater")
	public String fileFormat() { return ""; }
	
	/** The file formats to use for this mod. See {@link #fileFormat()} */
	@Optional.Method(modid = "forgeupdater")
	public String[] fileFormats() { return new String[]{""}; }
	
	/** The minimum release type to be checked for. 0 = release; 1 = beta; 2 = alpha. Example: 1 will allow beta and release builds, but not alpha. */
	@Optional.Method(modid = "forgeupdater")
	public int minType() { return 0; }
}