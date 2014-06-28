End-users:
===
##Commands:
`/updates` - lists all mods with new versions  
`/updates [page]` - the page of mods to view  
`/updates info [mod]` - view more info about one of the mods with a pending update  
`/updates refresh` - coming in 1.1

Modders:
===
I tried to make this as easy to implement as possible, and I think I did a pretty good job. All you have to do is add the following to your main mod class. (The one with @Mod). These can be in any order you want, just make sure they have the same name and return types otherwise it will not work! If you don't know what the `@Optional.Method` annotation does, Minalien has a great page about it [here](http://minalien.com/minecraft-forge-feature-spotlight-optional-annotation/).


**Required:** The `CurseID` for your mod. Find it at `curse.com/mc-mods/minecraft/[curseID]` For example: this mod is at 
`curse/com/mc-mods/minecraft/forgeupdater`, therefore the curseID is `forgeupdater`

```java
@Optional.Method(modid = "forgeupdater")
public String curseID() {
  return [curseID];
}
```

The file format to use for this mod, where $mc = minecraft version; $v = mod version. Example: this mod is "Forge_Updater-$mc-$v.jar"  
**Note:** all spaces in the resulting string will be replaced with underscores

```java
@Optional.Method(modid = "forgeupdater")
public String fileFormat() {
  return "Forge_Updater-$mc-$v.jar";
}
```

The file formats to use for this mod, if it has more than one. See the previous method.  
**Note 1:** This must be in the order they should be checked.  
**Note 2:** If this method is present, any result from the `fileFormat()` method will be ignored.

```java
@Optional.Method(modid = "forgeupdater")
public String[] fileFormats() {
  return new String[]{"Forge_Updater-$mc-$v.jar"};
}
```


The minimum release type to be checked for. 0 = release; 1 = beta; 2 = alpha. Example: 1 will allow beta and release builds, but not alpha.

```java
@Optional.Method(modid = "forgeupdater")
public int minType() {
  return 0;
}
```
