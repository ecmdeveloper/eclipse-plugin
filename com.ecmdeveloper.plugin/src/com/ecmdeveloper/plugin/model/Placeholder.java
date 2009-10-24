package com.ecmdeveloper.plugin.model;

import java.util.Collection;

import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class Placeholder extends ObjectStoreItem implements IObjectStoreItem{

	public Placeholder() {
		super(null, null);
		this.name = "Loading...";
		// TODO Auto-generated constructor stub
	}

	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}
}
