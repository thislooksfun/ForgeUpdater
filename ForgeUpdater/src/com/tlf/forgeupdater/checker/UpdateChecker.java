package com.tlf.forgeupdater.checker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import net.minecraftforge.common.MinecraftForge;

import com.tlf.forgeupdater.JSON.JSONArray;
import com.tlf.forgeupdater.JSON.JSONException;
import com.tlf.forgeupdater.JSON.JSONObject;

import cpw.mods.fml.common.ModContainer;

public class UpdateChecker
{
	protected boolean hasUpdate = false;
	protected String updateURL;
	protected String updateVersion;
	protected int versionsBehind = 0;
	
	public final String MODVERSION;
	public final String MODNAME;
	public final String CURSEID;
	public final String MODID;
	public final UpdateType MINTYPE;
	
	public UpdateChecker(ModContainer mc, String curseID, UpdateType minType, String[] fileFormats)
	{
		this.MODVERSION = mc.getVersion();
		this.MODNAME = mc.getName();
		this.MODID = mc.getModId();
		this.CURSEID = curseID;
		this.MINTYPE = (minType == null ? UpdateType.RELEASE : minType);
		
		try {
			this.check(fileFormats);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		/*
		this.hasUpdate = true;
		this.updateURL = "http://google.com";
		this.updateVersion = "1.2.3";
		 */
	}
	
	private void check(String[] fileFormats) throws IOException, JSONException, NullPointerException
	{
		JSONObject json;
		json = readJsonFromUrl("http://widget.mcf.li/mc-mods/minecraft/"+CURSEID+".json");
		
		JSONObject files = json.getJSONObject("files");
		JSONArray names = files.names();
		
		for (int i = 0; i < names.length(); i++)
		{
			JSONObject jobj = files.getJSONObject((String)names.get(i));
			
			String fileName = jobj.getString("name");
			String url = jobj.getString("url");
			String mcVersion = jobj.getString("version");
			String type = jobj.getString("type");
			
			int index = fileName.lastIndexOf("-");
			
			if (index > -1) {
				int match = getRegexMatch(fileName, getRegexFromFileFormats(fileFormats));
				if (match > -1) {
					String version = getVersionFromFileName(fileFormats[match], fileName);
					if (this.checkVersion(MinecraftForge.MC_VERSION, mcVersion, false)) {
						if (version.matches("(\\.*\\d+)+")) {							
							if (isAllowedType(type) && this.checkVersion(this.MODVERSION, version, true)) {
								this.hasUpdate = true;
								this.versionsBehind++;
								if (this.checkVersion(this.updateVersion, version, true)) {
									this.updateURL = url;
									this.updateVersion = version.replaceAll("[^\\.0-9]", "");
								}
							}
						}
					}
				}
			}
		}
	}
	
	private boolean isAllowedType(String inp) {
		return UpdateType.getTypeForStr(inp).toInt() <= MINTYPE.toInt();
	}
	
	private String getVersionFromFileName(String pattern, String filename)
	{
		boolean hasMCVer = pattern.contains("$mc");
		
		if (hasMCVer)
		{
			int mcVerIndex = pattern.indexOf("$mc");
			
			if (mcVerIndex > -1) {
				boolean mcVerLeft = pattern.matches(".*\\$mc.*\\$v.*");
				
				String[] leftParts = pattern.substring(0, mcVerIndex).split("\\$v");
				String[] rightParts = pattern.substring(mcVerIndex+3).split("\\$v");
				
				String left = leftParts[0];
				String middle = (leftParts.length == 2 ? leftParts[1] : rightParts[0]);
				String right = (rightParts.length == 2 ? rightParts[1] : rightParts[0]);
				
				String updateVersion = (mcVerLeft ? filename.substring(filename.indexOf(middle, left.length()) + middle.length(), filename.length() - right.length()) : filename.substring(left.length(), filename.indexOf(middle, left.length())));
								
				return updateVersion;
			}
		}
		else
		{
			int verIndex = pattern.indexOf("$v");
			
			if (verIndex > -1) {				
				String left = pattern.substring(0, verIndex);
				String right = pattern.substring(verIndex+2);
				
				String updateVersion = filename.substring(left.length(), filename.indexOf(right, left.length()));
								
				return updateVersion;
			}
		}
		return "";
	}
	
	private int getRegexMatch(String filename, String[] regex)
	{
		for (int i = 0; i < regex.length; i++) {
			if (filename.matches(regex[i])) { return i; }
		}
		return -1;
	}
	
	private String[] getRegexFromFileFormats(String[] fileFormats)
	{
		String[] output = new String[fileFormats.length];
		for (int i = 0; i < fileFormats.length; i++) {
			output[i] = fileFormats[i].replaceAll("\\$n", ".*").replaceAll("\\$mc", "(\\\\d+\\\\.\\\\d\\\\.\\\\d)").replaceAll("\\$v", "(\\\\d+(\\\\.{1}\\\\d+)*)");
		}
		
		return output;
	}
	
	private boolean checkVersion(String current, String check, boolean strict)
	{
		if (current == null || current.equals("")) {
			return true;
		} else if (check == null || check.equals("")) {
			return false;
		}
		
		String[] currentVersion = current.replaceAll("[^\\.0-9]", "").split("\\.");
		String[] newVersion = check.replaceAll("[^\\.0-9]", "").split("\\.");
		
		while (currentVersion.length < newVersion.length) {
			currentVersion = append(currentVersion, "0");
		}
		while (newVersion.length < currentVersion.length) {
			newVersion = append(newVersion, "0");
		}
		
		for (int i = 0; i < currentVersion.length; i++) {
			while (currentVersion[i].length() < newVersion[i].length()) {
				currentVersion[i] += "0";
			}
			while (newVersion[i].length() < currentVersion[i].length()) {
				newVersion[i] += "0";
			}
		}
		
		int currentVer = Integer.parseInt(concat(currentVersion).replaceAll("\\D", ""));
		int newVer = Integer.parseInt(concat(newVersion).replaceAll("\\D", ""));
		
		return strict ? (currentVer < newVer) : (currentVer <= newVer);
	}
	
	private String readAll(Reader rd) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	private JSONObject readJsonFromUrl(String url) throws IOException, JSONException
	{
		InputStream is = null;
		
		try {
			is = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} catch (MalformedURLException e) {
			System.err.println("ERROR: " + e.getMessage());
		} finally {
			if (is != null) {
				is.close();
			}
		}
		
		return null;
	}
	
	public boolean hasUpdate()    { return this.hasUpdate;      }
	public String updateURL()     { return this.updateURL;      }
	public String updateVersion() { return this.updateVersion;  }
	public int versionsBehind()   { return this.versionsBehind; }
	
	public enum UpdateType {
		RELEASE("release"),
		BETA("beta"),
		ALPHA("release");
		
		private final String type;
		
		UpdateType(String releasetype) {
			this.type = releasetype;
		}
		
		public static UpdateType getTypeForInt(int i) {
			switch (i) {
			case 0:
				return ALPHA;
			case 1:
				return BETA;
			default:
				return RELEASE;
			}
		}
		public static UpdateType getTypeForStr(String str) {
			if (str.equalsIgnoreCase("beta")) {
				return BETA;
			} else if (str.equalsIgnoreCase("alpha")) {
				return ALPHA;
			} else {
				return RELEASE;
			}
		}
		
		public int toInt() {
			if (this.type.equalsIgnoreCase("beta")) {
				return 0;
			} else if (this.type.equalsIgnoreCase("alpha")) {
				return 1;
			} else {
				return 2;
			}
		}
		public String toString() {
			return this.type;
		}
	}
	
	public static <T, S extends T> T[] append(T[] appendTo, S append)
	{
		if (append == null) {
			throw new IllegalArgumentException("Can't append null!");
		}
		
		T[] appended = (T[])Array.newInstance(appendTo.getClass().getComponentType(), appendTo.length+1);
		
		for (int i = 0; i < appendTo.length; i++) {
			appended[i] = appendTo[i];
		}
		
		appended[appendTo.length] = (T)append;
		return appended;
	}
	public static String concat(String[] input) { return concat(input, ""); }
	public static String concat(String[] input, String seperator)
	{
		String output = "";
		for (String str : input) {
			if (output.equals("")) {
				output += str;
			} else {
				output += seperator + str;
			}
		}
		return output;
	}
}