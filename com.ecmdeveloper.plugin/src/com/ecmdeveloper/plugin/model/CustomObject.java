/**
 * 
 */
package com.ecmdeveloper.plugin.model;

import java.util.Collection;

import com.filenet.api.constants.PropertyNames;

/**
 * @author Ricardo Belfor
 *
 */
public class CustomObject extends ObjectStoreItem {

	protected com.filenet.api.core.CustomObject customObject;
	
	public CustomObject( Object customObject, IObjectStoreItem parent ) {

		super(parent);
		
		this.customObject = (com.filenet.api.core.CustomObject) customObject;
		this.customObject.fetchProperties( new String[] { PropertyNames.NAME, PropertyNames.ID } );
		this.name = this.customObject.get_Name();
		this.id = this.customObject.get_Id().toString();
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
}
