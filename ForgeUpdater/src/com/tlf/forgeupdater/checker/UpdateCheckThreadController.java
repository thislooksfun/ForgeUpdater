package com.tlf.forgeupdater.checker;

import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.HashSet;
import java.util.Set;

public class UpdateCheckThreadController
{
	public static UpdateCheckThreadController instance = new UpdateCheckThreadController();
	
	public final UpdateCheckManager MANAGER = new UpdateCheckManager();
	protected Thread thread;
	
	protected boolean checking = false;
	
	private int updates = 0;
	
	private Set<ICommandSender> senders = new HashSet<ICommandSender>();
	
	private UpdateCheckThreadController() {}
	
	public void check() {
		this.thread = new Thread(MANAGER, "tlf Update Check Manager");
		thread.start();
		this.checking = true;
	}
	
	protected void finishCheck(int newUpdates)
	{
		this.updates = newUpdates;
		
		for (ICommandSender sender : senders) {
			String msg = "There "+(updates == 1 ? "is " : "are ")+EnumChatFormatting.RED+updates+EnumChatFormatting.AQUA+" mod"+(updates == 1 ? "" : "s")+" with"+(updates == 1 ? " a" : "")+" new version"+(updates == 1 ? "" : "s")+".";
			sender.addChatMessage(new ChatComponentText(msg).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)));
			if (updates > 0) {
				IChatComponent chatComponentBody = new ChatComponentText("For more information, please type ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA));
				IChatComponent chatComponentLink = new ChatComponentText("/updates").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/updates")).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Run command").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)))));;
				sender.addChatMessage(chatComponentBody.appendSibling(chatComponentLink));
			}
		}
	}
	
	public void onPlayerConnect(ICommandSender sender)
	{
		if (checking) {
			this.senders.add(sender);
		} else {
			int updates = UpdateCheckThreadController.instance.getUpdates();
			if (updates > 0) {
				String msg = "There "+(updates == 1 ? "is " : "are ")+EnumChatFormatting.RED+updates+EnumChatFormatting.AQUA+" mod"+(updates == 1 ? "" : "s")+" with"+(updates == 1 ? " a" : "")+" new version"+(updates == 1 ? "" : "s")+".";
				sender.addChatMessage(new ChatComponentText(msg).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)));
				
				IChatComponent chatComponentBody = new ChatComponentText("For more information, please type ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA));
				IChatComponent chatComponentLink = new ChatComponentText("/updates").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/updates")).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Run command").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)))));;
				sender.addChatMessage(chatComponentBody.appendSibling(chatComponentLink));
			}
		}
	}
	
	public void refresh(ICommandSender sender) {
		if (!this.checking) {
			sender.addChatMessage(new ChatComponentText("Checking!").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)));
			this.thread = new Thread(MANAGER, "tlf Update Check Manager");
			this.thread.start();
			this.checking = true;
		} else {
			int percent = this.MANAGER.percentDone();
			sender.addChatMessage(new ChatComponentText("Check is "+percent+"% done.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
		}
		
		this.senders.add(sender);
	}
	
	public int getUpdates() {
		return this.updates;
	}
	
	public boolean checking() {
		return this.checking;
	}
}