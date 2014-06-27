package com.tlf.forgeupdater.checker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.tlf.forgeupdater.checker.UpdateChecker.UpdateType;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class UpdateCheckManager
{
	public static final UpdateCheckManager INSTANCE = new UpdateCheckManager();
	
	private Set<UpdateChecker> checkers = new HashSet<UpdateChecker>();
	private Set<UpdateChecker> checkersWithUpdate = new HashSet<UpdateChecker>();
	
	public void getUpdaters()
	{
		Iterator<ModContainer> iterator = Loader.instance().getActiveModList().iterator();
		
		while (iterator.hasNext())
		{
			this.checkClass(iterator.next());
		}
	}
	
	private void checkClass(ModContainer mc)
	{
		String name = mc.getName();
		Object mod = mc.getMod();
		Class clazz = mod == null ? null : mod.getClass();
		
		if (!name.equals("Minecraft Coder Pack") && !name.equals("Forge Mod Loader") && !name.equals("Minecraft Forge"))
		{
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
						minType = UpdateType.getTypeForInt((int)updateTypes.invoke(mod));
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
				
				try {
					this.buildChecker(mc, id, minType, formats);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void buildChecker(ModContainer mc, String curseID, UpdateType minType, String[] fileFormats) throws IllegalArgumentException
	{
		if (curseID.equals("")) {
			throw new IllegalArgumentException("curseID can't be empty!");
		} else {
			System.out.println("====== Building checker for " + mc.getName());
			UpdateChecker checker = new UpdateChecker(mc, curseID, minType, fileFormats);
			this.checkers.add(checker);
			if (checker.hasUpdate) {
				this.checkersWithUpdate.add(checker);
			}
		}
	}
	
	public Set<UpdateChecker> getCheckers() { return this.checkers; }
	public Set<UpdateChecker> getCheckersWithUpdate() { return this.checkersWithUpdate; }
}