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

import com.tlf.forgeupdater.JSON.JSONArray;
import com.tlf.forgeupdater.JSON.JSONException;
import com.tlf.forgeupdater.JSON.JSONObject;

import cpw.mods.fml.common.ModContainer;

public class UpdateChecker
{
	protected boolean hasUpdate = false;
	protected String updateURL = "";
	protected String updateVersion = "";
	protected int versionsBehind = 0;
	
	public final String MODVERSION;
	public final String MODNAME;
	public final String CURSEID;
	public final UpdateType MINTYPE;
	
	public UpdateChecker(ModContainer mc, String curseID, UpdateType minType, String[] fileFormats)
	{
		this.MODVERSION = mc.getVersion();
		this.MODNAME = mc.getName();
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
		
		this.hasUpdate = true;
	}
	
	public void check(String[] fileFormats) throws IOException, JSONException, NullPointerException
	{
		JSONObject json;
		json = readJsonFromUrl("http://widget.mcf.li/mc-mods/minecraft/"+CURSEID+".json");
		
		System.out.println(json.toString());
		JSONObject files = json.getJSONObject("files");
		JSONArray names = files.names();
		
		for (int i = 0; i < names.length(); i++)
		{
			JSONObject jobj = files.getJSONObject((String)names.get(i));
			
			String fileName = jobj.getString("name");
			String url = jobj.getString("url");
			String type = jobj.getString("type");
			
			System.out.println(String.format("Filename: %s; Type: %s; URL: %s", fileName, type, url));
			System.out.println("Matches: "+fileName.matches(".*-.*-.*\\.jar$"));
			
			int index = fileName.lastIndexOf("-");
			
			if (index > -1) {
				String version = getVersionFromFileName(getRegexFromFileFormats(fileFormats), fileName);
				if (version.matches("(\\.*\\d+)+")) {
					System.out.println("Version: "+version);
					
					if (this.checkVersion(version)) {
						System.out.println("New version!");
						this.versionsBehind++;
						this.updateURL = url;
						this.updateVersion = version.replaceAll("[^\\.0-9]", "");
					}
				}
			}
		}
	}
	
	public String getVersionFromFileName(String[] regex, String filename)
	{
		for (int i = 0; i < regex.length; i++) {
			System.out.println("Regex["+i+"]: " + regex[i]);
		}
		System.out.println("[^\\.0-9]");
		return "";
	}
	
	public String[] getRegexFromFileFormats(String[] fileFormats)
	{
		for (int i = 0; i < fileFormats.length; i++) {
			fileFormats[i] = fileFormats[i].replaceAll("\\$n", ".*").replaceAll("\\$mc", "(\\\\d+\\\\.\\\\d\\\\.\\\\d)").replaceAll("\\$v", "(\\d+(\\\\.{1}\\\\d+)*)");
		}
		
		return fileFormats;
	}
	
	public boolean checkVersion(String checkVer)
	{
		String[] currentVersion = this.MODVERSION.replaceAll("[^\\.0-9]", "").split("\\.");
		System.out.println("v0.0.1".replaceAll("[^\\.0-9]", ""));
		String[] newVersion = checkVer.replaceAll("[^\\.0-9]", "").split("\\.");
		
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
		
		System.out.println(concat(currentVersion, "."));
		System.out.println(concat(newVersion, "."));
		System.out.println();
		
		int currentVer = Integer.parseInt(concat(currentVersion).replaceAll("\\D", ""));
		int newVer = Integer.parseInt(concat(newVersion).replaceAll("\\D", ""));
		
		return currentVer < newVer;
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
	
	public JSONObject readJsonFromUrl(String url) throws IOException, JSONException
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
		
		//EnumChatFormatting //Reference
		
		private final String type;
		
		UpdateType(String releasetype) {
			this.type = releasetype;
		}
		
		public static UpdateType getTypeForInt(int i) {
			switch (i) {
			case 1:
				return BETA;
			case 2:
				return ALPHA;
			default:
				return RELEASE;
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