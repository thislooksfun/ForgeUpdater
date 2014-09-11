package com.tlf.forgeupdater.common;

import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

import java.io.File;

import com.tlf.forgeupdater.checker.UpdateCheckManager;
import com.tlf.forgeupdater.checker.UpdateCheckThreadController;
import com.tlf.forgeupdater.command.CommandUpdates;
import com.tlf.forgeupdater.event.EventHandlerCPW;

@Mod(modid = ForgeUpdater.MODID, name = ForgeUpdater.NAME, version = ForgeUpdater.VERSION)
public class ForgeUpdater
{
	public static final String MODID = "forgeupdater";
	public static final String NAME = "Forge Updater";
	public static final String VERSION = "1.3.2";
	
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
	public void onInit(FMLInitializationEvent event)
	{
		FMLCommonHandler.instance().bus().register(new EventHandlerCPW());
		
		FMLInterModComms.sendMessage("forgeupdater", "updaterInfo", "{id='221832-forgeupdater', minType='0', formats=['Forge_Updater-$mc-$v.jar']}");
	}
	@EventHandler
	public void interModMessages(IMCEvent event)
	{
		for (IMCMessage message : event.getMessages())
		{
			if (message.key.equalsIgnoreCase("updaterInfo"))
			{
				if (message.isStringMessage())
				{
					System.out.println("The mod " + message.getSender() + " has sent the following message: " + message.getStringValue());
					try {
						MessageDecoder.decodeMessage(message.getSender(), message.getStringValue());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event) {
		UpdateCheckThreadController.instance.check();
	}
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandUpdates());
	}
	
	/** The CurseID for your mod. Find it at curse.com/mc-mods/minecraft/[curseID] For example: this mod is at curse/com/mc-mods/minecraft/221832-forgeupdater, therefore the curseID is 221832-forgeupdater */
	@Optional.Method(modid = "forgeupdater")
	public String curseID() { return "221832-forgeupdater"; }
	
	/** The file formats to use for this mod. See {@link #fileFormat()} */
	@Optional.Method(modid = "forgeupdater")
	public String[] fileFormats() { return new String[]{"Forge_Updater-$mc-$v.jar"}; }
	
	/** The minimum release type to be checked for. 0 = alpha; 1 = beta; 2 = release. Example: 1 will allow beta and release builds, but not alpha builds. */
	@Optional.Method(modid = "forgeupdater")
	public int minType() { return 0; }
}