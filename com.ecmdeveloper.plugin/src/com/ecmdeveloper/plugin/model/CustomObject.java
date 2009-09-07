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
		this.customObject.fetchProperties( new String[] { PropertyNames.NAME } );
		this.name = this.customObject.get_Name();
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
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
}
