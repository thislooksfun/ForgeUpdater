End-users:
===
##Commands:
`/updates` - lists all mods with new versions  
`/updates [page]` - the page of mods to view  
`/updates info [mod]` - view more info about one of the mods with a pending update  
`/updates refresh` - (not in 1.0.0) checks for new updates

Modders:
===

** NOTE:
The previous method of doing this, with the @Mod.Optional annotations, has been deprecated, and will be removed if/when I update to 1.8.

All you have to do is add the following code to your init (FMLInitializationEvent) method:
`FMLInterModComms.sendMessage("forgeupdater", "updaterInfo", "{id='[CurseForge ID]', minType='[Min Type]', formats=[Formats]}");`
For details regarding each of these pieces, see below:

---
####id
**Required:** The `CurseID` for your mod. Find it at `curse.com/mc-mods/minecraft/[curseID]`  
**Format:** A string ("'a'"); containing the CurseID of your mod.  
**Example:** This mod is at `curse/com/mc-mods/minecraft/forgeupdater`, therefore the curseID is `forgeupdater`  
**Note:** This is the same code as you would use for the [mod] tag on the [MinecraftForums](http://minecraftforum.net)

---
####formats
**Optional, but *strongly* reccomended:** The file name pattern(s) to use for this mod, where $mc = minecraft version; $v = mod version.  
**Format:** A string array ("['a', 'b']") containing the file name patterns to look for, in order.  
**Example:** This mod is `['Forge_Updater-$mc-$v.jar']`, and my mod (Hide Names)[http://minecraft.curseforge.com/mc-mods/62786-hide-names] returns `['Hide_Names-$mc-$v.jar', 'HideNames_v$v_MC_$mc.jar']`  
**Note 1:** All spaces in the resulting string will be replaced with underscores  
**Note 2:** This must be in the order they should be checked.  
**Note 3:** The `$mc` section is optional, but `$v` is required  
**Note 4:** If not found, the file pattern defaults to the pattern `[name]-$mc-$v.(jar|zip)` where `[name]` is your mod name with all spaces replaced with underscores (_)

---
####minType
**Optional:** The minimum release type to be checked for. 0 = alpha; 1 = beta; 2 = release.  
**Format:** A string ("'a'"); containing either 0, 1, or 2.  
**Example:** Returning 1 will allow beta and release builds, but not alpha builds.  
**Note:** By default this will be `2`


---
If you have any questions, bug reports, or feature requests, *please* create an issue. I would love to get feedback.
