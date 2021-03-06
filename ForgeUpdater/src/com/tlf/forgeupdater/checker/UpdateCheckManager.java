package com.tlf.forgeupdater.checker;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.tlf.forgeupdater.checker.UpdateChecker.UpdateType;
import com.tlf.forgeupdater.common.ForgeUpdater;
import com.tlf.forgeupdater.common.MessageDecoder.UpdaterMessage;

public class UpdateCheckManager implements Runnable
{
	public static boolean allowAll = true;
	
	private Map<String, UpdateChecker> checkers = new HashMap<String, UpdateChecker>();
	private Map<String, UpdateChecker> checkersWithUpdate = new HashMap<String, UpdateChecker>();
	private Set<UpdaterMessage> messages = new HashSet<UpdaterMessage>();
	private Set<String> ignoreMethods = new HashSet<String>();
	
	private static boolean initialized = false;
	
	private int percentDone = 0;
	
	@Override
	public void run()
	{
		this.getUpdaters();
		
		UpdateCheckThreadController.instance.checking = false;
		UpdateCheckThreadController.instance.finishCheck(this.checkersWithUpdate.size());
	}
	
	public synchronized void addUpdaterMessage(UpdaterMessage message)
	{
		this.ignoreMethods.add(message.modID);
		this.messages.add(message);
	}
	
	private void getUpdaters()
	{
		if (initialized) {
			this.checkers = new HashMap<String, UpdateChecker>();
			this.checkersWithUpdate = new HashMap<String, UpdateChecker>();
		}
		
		if (allowAll || initialized) {
			Iterator<ModContainer> iterator = Loader.instance().getActiveModList().iterator();
			Iterator<UpdaterMessage> iterator2 = this.messages.iterator();
			int max = Loader.instance().getActiveModList().size() + this.messages.size();
			int current = 0;
			
			while (iterator.hasNext())
			{
				this.checkClass(iterator.next());
				this.percentDone = (int)(((float)++current/(float)max)*100);
			}
			while (iterator2.hasNext())
			{
				this.buildChecker(iterator2.next());
				this.percentDone = (int)(((float)++current/(float)max)*100);
			}
		}
	}
	
	private void checkClass(ModContainer mc)
	{
		String name = mc.getName();
		Object mod = mc.getMod();
		Class clazz = (mod == null ? null : mod.getClass());
		if (ignoreMethods.contains(mc.getModId())) {
			return;
		}
		
		if (!name.equals("Minecraft Coder Pack") && !name.equals("Forge Mod Loader") && !name.equals("Minecraft Forge"))
		{
			if (clazz == null) {
				System.err.println("Class of "+name+" is null!");
				return;
			}
			
			Method[] methods = clazz.getMethods();
			
			Method curseID = null;
			Method fileFormat = null;
			Method fileFormats = null;
			Method updateTypes = null;
			
			for (Method method : methods) {
				if (method.getName().equals("curseID") && method.getParameterTypes().length == 0 && method.getReturnType() == String.class) {
					if (curseID == null) {
						curseID = method;
					}
				} else if (method.getName().equals("fileFormat") && method.getParameterTypes().length == 0 && method.getReturnType() == String.class) {
					if (fileFormat == null) {
						fileFormat = method;
					}
				} else if (method.getName().equals("fileFormats") && method.getParameterTypes().length == 0 && method.getReturnType() == String[].class) {
					if (fileFormats == null) {
						fileFormats = method;
					}
				} else if (method.getName().equals("minType") && method.getParameterTypes().length == 0 && method.getReturnType() == int.class) {
					if (updateTypes == null) {
						updateTypes = method;
					}
				}
			}
			
			if (curseID != null)
			{
				String id;
				String[] formats = new String[]{mc.getName().replaceAll(" ", "_")+"-$mc-$v.(jar|zip)"};
				UpdateType minType = UpdateType.RELEASE;
				
				try {
					id = (String)curseID.invoke(mod);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					return;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					return;
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					return;
				}
				
				if (fileFormats != null) {
					try {
						formats = (String[])fileFormats.invoke(mod);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						return;
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						return;
					} catch (InvocationTargetException e) {
						e.printStackTrace();
						return;
					}
				} else if (fileFormat != null) {
					try {
						formats = new String[]{(String)fileFormat.invoke(mod)};
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						return;
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						return;
					} catch (InvocationTargetException e) {
						e.printStackTrace();
						return;
					}
				}
				
				if (updateTypes != null) {
					try {
						minType = UpdateType.getTypeForInt((Integer)updateTypes.invoke(mod));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						return;
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						return;
					} catch (InvocationTargetException e) {
						e.printStackTrace();
						return;
					}
				}
				
				boolean allowed = ForgeUpdater.instance.config.get("mods", mc.getModId(), true).getBoolean(true);
				ForgeUpdater.instance.config.save();
				
				for (int i = 0; i < formats.length; i++) {
					formats[i] = formats[i].replace(" ", "_");
				}
				
				if (allowed) {
					try {
						this.buildChecker(mc, id, minType, formats);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void buildChecker(UpdaterMessage message) throws IllegalArgumentException
	{
		ModContainer mc = Loader.instance().getIndexedModList().get(message.modID);
		if (mc != null) {
			buildChecker(mc, message.curseID, message.minType, message.fileTemplates);
		} else {
			System.err.println("There is no ModContainer for " + message.modID);
		}
	}
	private void buildChecker(ModContainer mc, String curseID, UpdateType minType, String[] fileFormats) throws IllegalArgumentException
	{
		if (curseID.equals("")) {
			throw new IllegalArgumentException("curseID can't be empty!");
		} else {
			UpdateChecker checker = new UpdateChecker(mc, curseID, minType, fileFormats);
			this.checkers.put(mc.getModId(), checker);
			if (checker.hasUpdate) {
				this.checkersWithUpdate.put(mc.getModId(), checker);
			}
		}
	}
	
	public synchronized Map<String, UpdateChecker> getCheckers() { return this.checkers; }
	public synchronized Map<String, UpdateChecker> getCheckersWithUpdate() { return this.checkersWithUpdate; }
	
	public synchronized int percentDone() {
		return this.percentDone;
	}
}