package com.tlf.forgeupdater.event;

import net.minecraft.server.MinecraftServer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import com.tlf.forgeupdater.checker.UpdateCheckThreadController;
import com.tlf.forgeupdater.common.TLFUtils;

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
		if (MinecraftServer.getServer().isSinglePlayer() || (!client && TLFUtils.isPlayerOp(event.player.getCommandSenderName())))
		{
			UpdateCheckThreadController.instance.onPlayerConnect(event.player);
		}
	}
}