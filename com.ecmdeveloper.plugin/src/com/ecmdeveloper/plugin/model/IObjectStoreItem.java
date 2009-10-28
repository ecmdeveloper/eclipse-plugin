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
	
	public void setName(String name);
	
	public String getId();
	
	public IObjectStoreItem getParent();
	
	public Collection<IObjectStoreItem> getChildren();
	
	public void setChildren( Collection<IObjectStoreItem> children );
	
	public boolean hasChildren();
	
	public ObjectStore getObjectStore();
	
	void move(IObjectStoreItem destination);

	public void setParent(IObjectStoreItem parent);
	
	public void refresh();
}
