package com.tlf.forgeupdater.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.Map;

import com.tlf.forgeupdater.checker.UpdateCheckThreadController;
import com.tlf.forgeupdater.checker.UpdateChecker;

public class CommandUpdates extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "updates";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}
	
	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender)
	{
		return "/updates <page|info|refresh>";
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return MinecraftServer.getServer().isSinglePlayer() ? true : MinecraftServer.getServer().getConfigurationManager().getOps().contains(sender.getCommandSenderName().toLowerCase());
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		Map<String, UpdateChecker> map = UpdateCheckThreadController.instance.MANAGER.getCheckersWithUpdate();
		UpdateChecker[] checkers = map.values().toArray(new UpdateChecker[0]);
		
		int pages = (int)Math.ceil((double)map.size()/4.0D);
		int page = 0;
		String mod = null;
		
		if (args.length == 1) {
			if (args[0].matches("\\d+")) {
				page = parseIntBounded(sender, args[0], 1, pages)-1;
			} else if (args[0].equalsIgnoreCase("info")) {
				throw new WrongUsageException("/updates info [modid]", new Object[0]);
			} else if (args[0].equalsIgnoreCase("refresh")) {
				UpdateCheckThreadController.instance.refresh(sender);
				return;
			} else {
				throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
			}
		} else if (args.length > 1 && args[0].equalsIgnoreCase("info")) {
			if (args.length > 2) {
				throw new WrongUsageException("/updates info [modid]", new Object[0]);
			} else {
				mod = args[1];
			}
		}
		
		if (mod != null) {
			UpdateChecker checker = map.get(mod);
			
			ChatComponentText chatComponentBody = new ChatComponentText("Version "+EnumChatFormatting.RED+checker.updateVersion()+EnumChatFormatting.AQUA+" of "+EnumChatFormatting.RED+checker.MODNAME+EnumChatFormatting.AQUA+" available! (You are "+EnumChatFormatting.RED+checker.versionsBehind()+EnumChatFormatting.AQUA+" version"+(checker.versionsBehind() == 1 ? "" : "s")+" behind) URL: ");
			ChatComponentText chatComponentLink = new ChatComponentText(checker.updateURL());
			chatComponentLink.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED).setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, checker.updateURL())).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Download version "+EnumChatFormatting.RED+checker.updateVersion()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)))));
			chatComponentBody.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)).appendSibling(chatComponentLink);
			sender.addChatMessage(chatComponentBody);
		} else if (pages == 0) {
			if (UpdateCheckThreadController.instance.checking()) {
				int percent = UpdateCheckThreadController.instance.MANAGER.percentDone();
				sender.addChatMessage(new ChatComponentText("There are no known updates, but there is currently a check "+percent+"% done!").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
			} else {
				IChatComponent left = new ChatComponentText("There are no updates at this time. Use ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN));
				IChatComponent middle = new ChatComponentText("/updates refresh").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/updates refresh")).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Run command").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)))));
				IChatComponent right = new ChatComponentText(" to refresh").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN).setChatClickEvent(new ClickEvent(null, null)).setChatHoverEvent(new HoverEvent(null, null)));
				sender.addChatMessage(left.appendSibling(middle).appendSibling(right));
			}
		} else {
			int pageStart = page*7;
			int pageEnd = ((page*7)+7 > map.size()) ? map.size() : (page*7)+7;
			
			IChatComponent left = new ChatComponentText(page > 0 ? EnumChatFormatting.GOLD+"<--" : EnumChatFormatting.DARK_GREEN+"---").setChatStyle(new ChatStyle().setChatClickEvent(page > 0 ? new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/updates "+page) : null).setChatHoverEvent(page > 0 ? new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Previous page").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD))) : null));
			IChatComponent message = new ChatComponentText(" Showing updates page "+EnumChatFormatting.RED+(page+1)+EnumChatFormatting.DARK_GREEN+" of "+EnumChatFormatting.RED+pages+EnumChatFormatting.DARK_GREEN+" (/updates <page>) ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN).setChatClickEvent(new ClickEvent(null, null)).setChatHoverEvent(new HoverEvent(null, null)));
			IChatComponent right = new ChatComponentText((page+1) < pages ? EnumChatFormatting.GOLD+"-->" : EnumChatFormatting.DARK_GREEN+"---").setChatStyle(new ChatStyle().setChatClickEvent((page+1) < pages ? new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/updates "+page+1) : null).setChatHoverEvent((page+1) < pages ? new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Next page").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD))) : null));
			
			sender.addChatMessage(left.appendSibling(message).appendSibling(right));
			for (int i = pageStart; i < pageEnd; i++)
			{
				UpdateChecker checker = checkers[i];
				
				ChatComponentText chatComponent = new ChatComponentText("- "+EnumChatFormatting.RED+checker.MODID+EnumChatFormatting.AQUA+" is "+EnumChatFormatting.RED+checker.versionsBehind()+EnumChatFormatting.AQUA+" version"+(checker.versionsBehind() == 1 ? "" : "s")+" behind");
				chatComponent.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/updates info "+checker.MODID)).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Show information").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)))));
				sender.addChatMessage(chatComponent);
			}
		}
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
	{
		if (par2ArrayOfStr.length == 1) {
			int pages = (int)Math.ceil((double)UpdateCheckThreadController.instance.getUpdates()/4.0D);
			String[] matches = new String[pages+2];
			for (int i = 0; i < pages; i++) {
				matches[i] = ""+(i+1);
			}
			matches[pages] = "info";
			matches[pages+1] = "refresh";
			
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, matches);
		} else if (par2ArrayOfStr.length == 2 && par2ArrayOfStr[0].equalsIgnoreCase("info")) {
			String[] mods = UpdateCheckThreadController.instance.MANAGER.getCheckersWithUpdate().keySet().toArray(new String[0]);
			
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, mods);
		}
		
		return null;
	}
}