package com.tlf.forgeupdater.common;

import com.tlf.forgeupdater.checker.UpdateCheckThreadController;
import com.tlf.forgeupdater.checker.UpdateChecker.UpdateType;

public class MessageDecoder
{
	public static void decodeMessage(String modID, String message)
	{
		String id = "";
		String[] templates = new String[0];
		UpdateType type = null;
		
		
		String name = "";
		boolean inString = false;
		String temp = "";
		int lastComma = 1;
		int inSection = -1;
		boolean inArray = false;
		
		int length = message.length();
		for (int i = 0; i < length; i++)
		{
			char ch = message.charAt(i);
			if (i == 0 && ch != '{') { //Opening bracket
				throwIAE(message);
			} else if (i == length-1 && ch != '}') { //Closing bracket
				throwIAE(message);
			} else if (ch == '\'') { //Opening/closing strings
				if (message.charAt(i-1) != '\\') {
					if (inString) {
						switch (inSection) {
						case 0:
							id = temp;
							break;
						case 1:
							templates = append(templates, temp);
							break;
						case 2:
							type = UpdateType.getTypeForInt(Integer.parseInt(temp));
							break;
						default:
							throwIAE(message);
						}
					}
					temp = "";
					inString = !inString;
				}
			} else if (ch == '[') {
				inArray = true;
			} else if (ch == '[') {
				if (inArray) {
					inArray = false;
				} else {
					throwIAE(message);
				}
			} else if (ch == ',') {
				if (!inString) {
					lastComma = i+1;
				}
			} else if (ch == '=') {
				if (!inString) {
					name = message.substring(lastComma, i).trim();
					inSection = name.equalsIgnoreCase("id") ? 0 : (name.equals("formats") ? 1 : (name.equalsIgnoreCase("minType") ? 2 : -1));
				}
			} else {
				if (inString) {
					temp += ch;
				}
			}
		}
		
		if (id.equals("")) {
			throwIAE(message);
		}
		
		UpdaterMessage up = new UpdaterMessage(modID, id, (templates == null ? new String[] {modID+""} : templates), (type == null ? UpdateType.RELEASE : type));
		
		UpdateCheckThreadController.instance.MANAGER.addUpdaterMessage(up);
	}
	
	private static String[] append(String[] array, String input)
	{
		String[] end = new String[array.length+1];
		for (int i = 0; i < array.length; i++)
		{
			end[i] = array[i];
		}
		end[array.length] = input;
		
		return end;
	}
	
	private static void throwIAE(String message) {
		throw new IllegalArgumentException(message+" is not a valid format!");
	}
	
	public static class UpdaterMessage
	{
		public final String modID;
		public final String curseID;
		public final String[] fileTemplates;
		public final UpdateType minType;
		
		public UpdaterMessage(String modid, String ID, String[] templates, UpdateType type)
		{
			this.modID = modid;
			this.curseID = ID;
			this.fileTemplates = templates;
			this.minType = type;
		}
	}
}