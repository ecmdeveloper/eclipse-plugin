/**
 * Copyright 2011, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.cmis.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;

/**
 * @author ricardo.belfor
 *
 */
public class Document extends ObjectStoreItem implements IDocument {

	protected org.apache.chemistry.opencmis.client.api.Document document;

	protected String versionSeriesId;
	protected String parentPath;
	protected String containmentName;
	private String mimeType;
	private boolean reserved;
	private String versionLabel;

	private Map<String,Object> properties;

	private Map<String,Property<?>> documentProperties;

	private String className;
	
	protected Document(Object document, IObjectStoreItem parent, ObjectStore objectStore, boolean saved ) {
		super(parent, objectStore);
	}

	protected Document(Object document, IObjectStoreItem parent, ObjectStore objectStore ) {
		this(document, parent, objectStore, true);
		this.document = (org.apache.chemistry.opencmis.client.api.Document) document;
		refresh();
		properties = new HashMap<String, Object>();
	}
	
	@Override
	public String getContainmentName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMimeType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath ) {
		this.parentPath = parentPath;
	}
	
	@Override
	public String getPathName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersionSeriesId() {
		return versionSeriesId;
	}

	@Override
	public String getVersionLabel() {
		return versionLabel;
	}

	@Override
	public boolean isCheckedOut() {
		return reserved;
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		return null;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public Object getValue(String propertyName) {
		
		if ( !saved ) {
			return null;
		}

		Property<?> property = documentProperties.get(propertyName);
		Object objectValue = null;
		if ( property != null ) {
			objectValue = property.getValue();
		}
//		if ( objectValue instanceof Collection ) {
//			ArrayList<?> values = new ArrayList( (Collection) objectValue );
//			return values.toArray();
//		} else if ( objectValue instanceof IndependentlyPersistableObject ) {
//			return "TODO";
//		}
		return objectValue;
		
	}

	@Override
	public boolean isSimilarObject(IObjectStoreItem otherItem) {
		if ( super.isSimilarObject(otherItem) ) {
			return true;
		}

		if ( versionSeriesId != null) {
			if ( isSameVersionSeries(this, otherItem) ) {
	//			System.out.println(otherItem.getDisplayName() + " found!");
				return true;
			}
		}
		return false;
	}
	
	private boolean isSameVersionSeries(IObjectStoreItem updatedItem, IObjectStoreItem otherItem) {
		return otherItem instanceof Document && updatedItem instanceof Document && 
				((Document)otherItem).getVersionSeriesId().equalsIgnoreCase( ((Document)updatedItem).getVersionSeriesId() );
	}

	@Override
	public void refresh() {
		if ( !saved ) {
			return;
		}
		
//		document.refresh(new String[] { PropertyNames.NAME, PropertyNames.ID,
//				PropertyNames.VERSION_SERIES, PropertyNames.IS_RESERVED, PropertyNames.MIME_TYPE });
		document.refresh();
		name = document.getName();
		id = document.getId();
		className = document.getType().getId();
		
//		mimeType = document.getMimeType();
		versionSeriesId = document.getVersionSeriesId();
		if ( versionSeriesId != null ) {
			reserved = document.isVersionSeriesCheckedOut();
		}
		
		versionLabel = document.getPropertyValue("cmis:versionLabel");
		documentProperties = new HashMap<String, Property<?>>();
		
		for ( Property<?> propery : document.getProperties() ) {
			documentProperties.put(propery.getId(), propery );
		}
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
		properties.put(PropertyIds.NAME, name );
	}

	@Override
	public void setValue(String propertyName, Object value) throws Exception {
		properties.put(PropertyIds.NAME, name );
	}

	@Override
	public void save() {
		if ( !properties.isEmpty() ) {
			document.updateProperties(properties);
		}
		properties.clear();
	}

	@Override
	public CmisObject getCmisObject() {
		return document;
	}

	@Override
	public boolean canCheckOut() {
		return versionSeriesId != null;
	}

	public org.apache.chemistry.opencmis.client.api.Document getInternalDocument() {
		return document;
	}

	public void refresh(org.apache.chemistry.opencmis.client.api.Document newDocument) {
		document = newDocument;
		refresh();
	}
}
