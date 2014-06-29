package com.tlf.forgeupdater.event;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import com.tlf.forgeupdater.checker.UpdateCheckManager;

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
		if (MinecraftServer.getServer().isSinglePlayer() || (!client && MinecraftServer.getServer().getConfigurationManager().getOps().contains(event.player.getCommandSenderName().toLowerCase())))
		{
			int updates = UpdateCheckManager.INSTANCE.getCheckersWithUpdate().size();
			if (updates > 0) {
				String msg = "There "+(updates == 1 ? "is " : "are ")+EnumChatFormatting.RED+updates+EnumChatFormatting.AQUA+" mod"+(updates == 1 ? "" : "s")+" with"+(updates == 1 ? " a" : "")+" new version"+(updates == 1 ? "" : "s")+".";
				event.player.addChatMessage(new ChatComponentText(msg).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)));
				
				IChatComponent chatComponentBody = new ChatComponentText("For more information, please type ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA));
				IChatComponent chatComponentLink = new ChatComponentText("/updates").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/updates")).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Run command").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)))));;
				event.player.addChatMessage(chatComponentBody.appendSibling(chatComponentLink));
			}
		}
	}
}