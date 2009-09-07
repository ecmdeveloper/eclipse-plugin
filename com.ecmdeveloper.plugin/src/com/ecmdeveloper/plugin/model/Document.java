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
public class Document extends ObjectStoreItem {

	protected com.filenet.api.core.Document document;
	
	public Document(Object document, IObjectStoreItem parent ) {
		super(parent);
		
		this.document = (com.filenet.api.core.Document) document;
		this.document.fetchProperties( new String[] { PropertyNames.NAME } );
		this.name = this.document.get_Name();
	}
	
	/** 
	 * A document has no children.
	 * 
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getChildren()
	 */
	@Override
	public Collection<IObjectStoreItem> getChildren() 
	{
		return null;
	}
}
