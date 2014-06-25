package com.tlf.forgeupdater.common;

import java.lang.annotation.Annotation;
import java.util.Iterator;

import com.tlf.forgeupdater.annotation.UpdateCheck;
import com.tlf.forgeupdater.checker.UpdateChecker;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ForgeUpdater.MODID, name = ForgeUpdater.NAME, version = ForgeUpdater.VERSION)
@UpdateCheck(curseID = "")
public class ForgeUpdater
{
	public static final String MODID = "forgeupdater";
	public static final String NAME = "Forge Updater";
	public static final String VERSION = "0.0.1 pre-alpha";
	
	//** 
	public static UpdateChecker checker;
	
	/** The public instance */
	@Instance(ForgeUpdater.MODID)
	public static ForgeUpdater instance;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {}
	@EventHandler
	public void onInit(FMLInitializationEvent event) {}
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		Iterator<ModContainer> iterator = Loader.instance().getActiveModList().iterator();
		
		System.out.println("\n===========");
		System.out.println("Checking mods...");
		while (iterator.hasNext())
		{
			ModContainer mc = iterator.next();
			String name = mc.getName();
			Object mod = mc.getMod();
			Class clazz = mod == null ? null : mod.getClass();
			
			if (!name.equals("Minecraft Coder Pack") && !name.equals("Forge Mod Loader") && !name.equals("Minecraft Forge"))
			{
				System.out.println("Mod name: " + name);
				System.out.println("Mod class: " + (clazz == null ? "null" : clazz.getName()));
				
				if (clazz.isAnnotationPresent(UpdateCheck.class)) {
					Annotation[] annotations = clazz.getAnnotations();
					
					System.out.println("\n-----------");
					System.out.println("Checking annotations for " + name);
					for (Annotation ann : annotations) {
						if (ann instanceof UpdateCheck) {
							UpdateCheck checker = (UpdateCheck)ann;
							
							checker.curseID();
						}
					}
					System.out.println("-----------\n");
					//System.out.println("Updating!);
				}
			}
		}
		System.out.println("\n===========");
	}
}