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

import java.util.Collection;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

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

	private ContentStream contentStream;

	private Boolean versionable;
	
	protected Document(Object document, IObjectStoreItem parent, ObjectStore objectStore, boolean saved ) {
		super(parent, objectStore, saved);
		this.document = (org.apache.chemistry.opencmis.client.api.Document) document;
		refresh();
	}

	protected Document(Object document, IObjectStoreItem parent, ObjectStore objectStore ) {
		this(document, parent, objectStore, true);
	}
	
	@Override
	public String getContainmentName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMimeType() {
		return mimeType;
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
		mimeType = document.getContentStreamMimeType();
		versionable = ((DocumentType) document.getType()).isVersionable();
		versionSeriesId = document.getVersionSeriesId();
		if ( versionSeriesId != null ) {
			reserved = document.isVersionSeriesCheckedOut();
		}
		
		initalizeProperties();
		
		versionLabel = document.getPropertyValue("cmis:versionLabel");
		
	}
	
	@Override
	public void save() {
		super.save();
		this.name = document.getName();
	}

	public boolean isSaved() {
		return saved;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
		setValue(PropertyIds.NAME, name );
	}

	@Override
	public CmisObject getCmisObject() {
		return document;
	}

	@Override
	public boolean canCheckOut() {
		return versionable != null && versionable;
	}

	public org.apache.chemistry.opencmis.client.api.Document getInternalDocument() {
		return document;
	}

	public void refresh(org.apache.chemistry.opencmis.client.api.Document newDocument) {
		document = newDocument;
		refresh();
	}

	public void setContentStream(ContentStream contentStream) {
		this.contentStream = contentStream;
		if ( contentStream != null ) {
			setValue(PropertyIds.CONTENT_STREAM_FILE_NAME, contentStream.getFileName() );
		}
	}
	
	public void saveNew(VersioningState versioningState) {

		if ( saved ) {
			throw new IllegalStateException("Document is already saved" );
		}
		
		org.apache.chemistry.opencmis.client.api.Folder internalFolder = ((Folder)parent).getInternalFolder();
		document = internalFolder.createDocument(changedProperties, contentStream, versioningState );
		saved = true;
		changedProperties.clear();
		refresh();
	}
}
