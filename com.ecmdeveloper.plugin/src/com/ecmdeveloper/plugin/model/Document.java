/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
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
