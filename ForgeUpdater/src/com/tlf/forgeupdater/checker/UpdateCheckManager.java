package com.tlf.forgeupdater.checker;

import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.common.ModContainer;

public class UpdateCheckManager
{
	public static final UpdateCheckManager INSTANCE = new UpdateCheckManager();
	
	private Set<UpdateChecker> checkers = new HashSet<UpdateChecker>();
	private Set<UpdateChecker> checkersWithUpdate = new HashSet<UpdateChecker>();
	
	public void buildChecker(ModContainer mc, String curseID) {
		UpdateChecker checker = new UpdateChecker(mc, curseID);
		this.checkers.add(checker);
		if (checker.hasUpdate) {
			this.checkersWithUpdate.add(checker);
		}
	}
	
	public Set<UpdateChecker> getCheckers() { return this.checkers; }
	public Set<UpdateChecker> getCheckersWithUpdate() { return this.checkersWithUpdate; }
}