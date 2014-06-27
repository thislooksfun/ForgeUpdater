package com.tlf.forgeupdater.common;

import com.tlf.forgeupdater.checker.UpdateCheckManager;
import com.tlf.forgeupdater.checker.UpdateChecker;
import com.tlf.forgeupdater.event.EventHandlerCPW;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ForgeUpdater.MODID, name = ForgeUpdater.NAME, version = ForgeUpdater.VERSION)
public class ForgeUpdater
{
	public static final String MODID = "forgeupdater";
	public static final String NAME = "Forge Updater";
	public static final String VERSION = "0.0.1 pre-alpha";
	
	public static UpdateChecker checker;
	
	/** The public instance */
	@Instance(ForgeUpdater.MODID)
	public static ForgeUpdater instance;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {}
	@EventHandler
	public void onInit(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new EventHandlerCPW());
	}
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event) {
		UpdateCheckManager.INSTANCE.getUpdaters();
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