End-users:
===
##Commands:
`/updates` - lists all mods with new versions  
`/updates [page]` - the page of mods to view  
`/updates info [mod]` - view more info about one of the mods with a pending update  
`/updates refresh` - (not in 1.0.0) checks for new updates

Modders:
===

All you have to do is add the following code to your init (FMLInitializationEvent) method:
    FMLInterModComms.sendMessage("forgeupdater", "updaterInfo", "{id='221832-forgeupdater', minType='0', formats=['Forge_Updater-$mc-$v.jar']}");
**The breakdown:**  
The message you send is parsed with JSON. The required elements to have are "id", "minType" and "formats".  
---
**Required:** The `CurseID` for your mod. Find it at `curse.com/mc-mods/minecraft/[curseID]`  
**Example:** This mod is at `curse/com/mc-mods/minecraft/221832-forgeupdater`, therefore the curseID is `221832-forgeupdater`
---
**Optional:** The minimum release type to be checked for. 0 = alpha; 1 = beta; 2 = release.  
**Example:** Returning 1 will allow beta and release builds, but not alpha builds.
**Note 1:** By default this will be `2`

{id='221832-forgeupdater', minType='0', formats=['Forge_Updater-$mc-$v.jar']}


== OLD INFORMATION
This system has been left in for legacy purposes, but you should not use it, as it will go away if/when I eventually update to 1.8

I tried to make this as easy to implement as possible, and I think I did a pretty good job. You don't have to add any dependencies to your mod(s), and you don't have to worry about anything crashing if this isn't installed. All you have to do is add the following methods to your main mod class (the one with @Mod). That's it. These can be in any order you want, just make sure they have the same name and return types otherwise it will not work!  
If you don't know what the `@Optional.Method` annotation does, Minalien has a great page about it [here](http://minalien.com/minecraft-forge-feature-spotlight-optional-annotation/).

The methods
---
---
**Required:** The `CurseID` for your mod. Find it at `curse.com/mc-mods/minecraft/[curseID]`  
**Example:** This mod is at `curse/com/mc-mods/minecraft/forgeupdater`, therefore the curseID is `forgeupdater`  
**Note:** This is the same code as you would use for the [mod] tag on the [MinecraftForums](http://minecraftforum.net)

```java
@Optional.Method(modid = "forgeupdater")
public String curseID() {
  return [curseID];
}
```

---
**Optional, but *strongly* reccomended:** The file format to use for this mod, where $mc = minecraft version; $v = mod version.  
**Example:** This mod is `Forge_Updater-$mc-$v.jar`  
**Note 1:** All spaces in the resulting string will be replaced with underscores  
**Note 2:** The `$mc` section is optional, but `$v` is required  
**Note 3:** If not found, the file pattern defaults to the pattern `[name]-$mc-$v.(jar|zip)` where `[name]` is your mod name with all spaces replaced with underscores (_)

```java
@Optional.Method(modid = "forgeupdater")
public String fileFormat() {
  return [filepattern];
}
```

---
**Optional, but *strongly* reccomended:** The file formats to use for this mod, if it has more than one. See the previous method.  
**Example:** My mod (Hide Names)[http://minecraft.curseforge.com/mc-mods/62786-hide-names] returns `new String[]{"Hide_Names-$mc-$v.jar", "HideNames_v$v_MC_$mc.jar"}`  
**Note 1:** *If this method is present, any result from the `fileFormat()` method will be ignored.*  
**Note 2:** This must be in the order they should be checked.  
**Note 3:** If not found, it will use the result from `fileFormat()` instead. If neither are found, it defaults to the pattern `[name]-$mc-$v.(jar|zip)` where `[name]` is your mod name with all spaces replaced with underscores (_)  

```java
@Optional.Method(modid = "forgeupdater")
public String[] fileFormats() {
  return new String[]{[filepattern 1], [filepattern 2] ... [filepattern X]};
}
```

---
**Optional:** The minimum release type to be checked for. 0 = alpha; 1 = beta; 2 = release.  
**Example:** Returning 1 will allow beta and release builds, but not alpha builds.
**Note 1:** By default this will be `2`

```java
@Optional.Method(modid = "forgeupdater")
public int minType() {
  return 2;
}
```

---
If you have any questions, bug reports, or feature requests, *please* create an issue. I would love to get feedback.
