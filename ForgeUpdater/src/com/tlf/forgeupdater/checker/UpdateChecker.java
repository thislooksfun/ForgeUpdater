package com.tlf.forgeupdater.checker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import com.tlf.forgeupdater.JSON.JSONException;
import com.tlf.forgeupdater.JSON.JSONObject;

import cpw.mods.fml.common.ModContainer;

public class UpdateChecker
{
	protected boolean hasUpdate;
	protected String updateURL;
	protected String updateVersion;
	protected int versionsBehind;
	
	public final String MODVERSION;
	public final String MODNAME;
	public final String CURSEID;
	
	
	
	public UpdateChecker(ModContainer mc, String curseID)
	{
		this.MODVERSION = mc.getVersion();
		this.MODNAME = mc.getName();
		this.CURSEID = curseID;
		
		try {
			this.check();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		this.hasUpdate = true;
	}
	
	public void check() throws IOException, JSONException
	{
		JSONObject json;
		json = readJsonFromUrl("widget.mcf.li/mc-mods/minecraft/"+CURSEID+".json");
		
		System.out.println(json.toString());
		System.out.println(json.get("id"));
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
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	
	public boolean hasUpdate()    { return this.hasUpdate;      }
	public String updateURL()     { return this.updateURL;      }
	public String updateVersion() { return this.updateVersion;  }
	public int versionsBehind()   { return this.versionsBehind; }
}