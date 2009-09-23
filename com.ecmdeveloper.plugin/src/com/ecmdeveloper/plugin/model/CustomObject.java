/**
 * 
 */
package com.ecmdeveloper.plugin.model;

import java.util.Collection;

import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * @author Ricardo Belfor
 *
 */
public class CustomObject extends ObjectStoreItem {

	protected com.filenet.api.core.CustomObject customObject;
	
	public CustomObject( Object customObject, IObjectStoreItem parent, ObjectStore objectStore ) {

		super(parent, objectStore );
		
		this.customObject = (com.filenet.api.core.CustomObject) customObject;
		refresh();
	}
	
	/**
	 * A custom object has no children.
	 * 
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getChildren()
	 */
	@Override
	public Collection<IObjectStoreItem> getChildren() {
		return null;
	}

	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return customObject;
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public void refresh() {

		this.customObject.fetchProperties( new String[] { PropertyNames.NAME, PropertyNames.ID } );
		this.name = this.customObject.get_Name();
		this.id = this.customObject.get_Id().toString();
	}
}
