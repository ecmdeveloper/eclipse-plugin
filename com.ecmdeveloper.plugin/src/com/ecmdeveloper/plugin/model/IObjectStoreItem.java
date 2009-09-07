package com.ecmdeveloper.plugin.model;

import java.util.Collection;

import org.eclipse.core.runtime.IAdaptable;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public interface IObjectStoreItem extends IAdaptable {
	
	public String getName();
	
	public IObjectStoreItem getParent();
	
	public Collection<IObjectStoreItem> getChildren();
	
	public boolean hasChildren();
}
