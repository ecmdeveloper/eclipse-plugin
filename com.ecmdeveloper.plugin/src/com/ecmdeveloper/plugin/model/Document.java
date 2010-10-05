/**
 * Copyright 2009,2010, Ricardo Belfor
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
import com.filenet.api.core.VersionSeries;

/**
 * @author Ricardo Belfor
 *
 */
public class Document extends ObjectStoreItem {

	protected com.filenet.api.core.Document document;
	protected String versionSeriesId;
	protected String parentPath;
	protected String containmentName;
	private String mimeType;
	private boolean reserved;

	protected Document(Object document, IObjectStoreItem parent, ObjectStore objectStore) {
		this(document, parent,objectStore, true );
	}
	
	protected Document(Object document, IObjectStoreItem parent, ObjectStore objectStore, boolean saved ) {
		super(parent, objectStore, saved);
		
		this.document = (com.filenet.api.core.Document) document;
		refresh();
	}
	
	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return document;
	}

	@Override
	public void refresh() {
		
		if ( !saved ) {
			return;
		}
		
		document.refresh(new String[] { PropertyNames.NAME, PropertyNames.ID,
				PropertyNames.VERSION_SERIES, PropertyNames.IS_RESERVED, PropertyNames.MIME_TYPE });
		name = document.get_Name();
		id = document.get_Id().toString();
		mimeType = document.get_MimeType();
		VersionSeries versionSeries = document.get_VersionSeries();
		versionSeriesId = versionSeries.get_Id().toString();
		reserved = versionSeries.get_IsReserved();
	}

	public void refresh( com.filenet.api.core.Document newDocument ) {
		document = newDocument;
		refresh();
	}
	
	@Override
	public void save() {
		super.save();
		name = document.get_Name();
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

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath ) {
		this.parentPath = parentPath;
	}

	public String getContainmentName() {
		return containmentName;
	}

	public void setContainmentName(String containmentName) {
		this.containmentName = containmentName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getPathName() {
		return getParentPath() + "/" + getContainmentName();
	}
	
	@Override
	public String getClassName() {
		if ( document != null ) {
			return document.getClassName();
		}
		return null;
	}

	public boolean isCheckedOut() {
		return reserved;
	}

	@Override
	public boolean isSimilarObject(IObjectStoreItem otherItem) {
		if ( super.isSimilarObject(otherItem) ) {
			return true;
		}
		
		if ( isSameVersionSeries(this, otherItem) ) {
			System.out.println(otherItem.getDisplayName() + " found!");
			return true;
		}
		return false;
	}
	
	private boolean isSameVersionSeries(IObjectStoreItem updatedItem, IObjectStoreItem otherItem) {
		return otherItem instanceof Document && updatedItem instanceof Document && 
				((Document)otherItem).getVersionSeriesId().equalsIgnoreCase( ((Document)updatedItem).getVersionSeriesId() );
	}
}
