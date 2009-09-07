package com.ecmdeveloper.plugin.model;

import java.util.EventObject;

public class CodeModulesManagerEvent extends EventObject {

	private static final long serialVersionUID = 7349782413953807032L;

	private final CodeModuleFile[] itemsAdded;
	private final CodeModuleFile[] itemsRemoved;
	private final CodeModuleFile[] itemsUpdated;
	
	public CodeModulesManagerEvent(CodeModulesManager source,
			CodeModuleFile[] itemsAdded, CodeModuleFile[] itemsRemoved, CodeModuleFile[] itemsUpdated) {
		super(source);

		this.itemsAdded = itemsAdded;
		this.itemsRemoved = itemsRemoved;
		this.itemsUpdated = itemsUpdated;
	}

	public CodeModuleFile[] getItemsAdded() {
		return itemsAdded;
	}

	public CodeModuleFile[] getItemsRemoved() {
		return itemsRemoved;
	}

	public CodeModuleFile[] getItemsUpdated() {
		return itemsUpdated;
	}

}
