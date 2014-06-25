package com.tlf.forgeupdater.event;

import java.util.Iterator;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import com.tlf.forgeupdater.checker.UpdateCheckManager;
import com.tlf.forgeupdater.checker.UpdateChecker;
import com.tlf.forgeupdater.common.ForgeUpdater;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class EventHandlerCPW
{
	private final boolean client;
	
	public EventHandlerCPW()
	{
		this.client = FMLCommonHandler.instance().getEffectiveSide().isClient();
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (!client && (MinecraftServer.getServer().isSinglePlayer() || MinecraftServer.getServer().getConfigurationManager().getOps().contains(event.player.getCommandSenderName().toLowerCase())))
		{
			Iterator<UpdateChecker> iterator = UpdateCheckManager.INSTANCE.getCheckersWithUpdate().iterator();
			
			while (iterator.hasNext())
			{
				UpdateChecker checker = iterator.next();
				
				event.player.addChatMessage(new ChatComponentText("Version "+checker.updateVersion()+" of "+ForgeUpdater.checker.MODNAME+" available! (You are "+checker.versionsBehind()+" versions behind)"));
				event.player.addChatMessage(new ChatComponentText(checker.updateURL()));
			}
		}
	}
}