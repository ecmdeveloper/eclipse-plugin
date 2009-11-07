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
public class Document extends ObjectStoreItem {

	protected com.filenet.api.core.Document document;
	protected String versionSeriesId;
	
	public Document(Object document, IObjectStoreItem parent, ObjectStore objectStore ) {
		super(parent, objectStore);
		
		this.document = (com.filenet.api.core.Document) document;
		refresh();
	}
	
	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return document;
	}

	@Override
	public void refresh() {
		this.document.fetchProperties( new String[] { PropertyNames.NAME, PropertyNames.ID, PropertyNames.VERSION_SERIES } );
		name = this.document.get_Name();
		id = this.document.get_Id().toString();
		versionSeriesId = this.document.get_VersionSeries().get_Id().toString(); 
	}

	/** 
	 * A document has no children.
	 * 
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getChildren()
	 */
	@Override
	public Collection<IObjectStoreItem> getChildren() 
	{
		// TODO: Maybe in the future browsing child document?
		return null;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		this.document.getProperties().putValue( "DocumentTitle", name);
	}

	public String getVersionSeriesId() {
		return versionSeriesId;
	}

	public void setVersionSeriesId(String versionSeriesId) {
		this.versionSeriesId = versionSeriesId;
	}
}
