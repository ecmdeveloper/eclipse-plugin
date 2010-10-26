/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.properties.editors.input;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.properties.model.UnsavedPropertiesObject;

/**
 * @author ricardo.belfor
 *
 */
public class NewDocumentEditorInput extends NewObjectStoreItemEditorInput {

	private static final String DEFAULT_DOCUMENT_NAME = "Document {0}";
	private static final String DOCUMENT_TITLE_PROPERTY_NAME = "DocumentTitle";

	private static int newDocumentIndex = 0;
	
	private ArrayList<Object> content;
	private String mimeType;
	private boolean checkinMajor;
	private boolean autoClassify;
	
	public NewDocumentEditorInput(ClassDescription classDescription, ObjectStoreItem parent) {
		super(classDescription, parent);
		String unsavedTitle = MessageFormat.format(DEFAULT_DOCUMENT_NAME, ++newDocumentIndex);
		setName(unsavedTitle);
	}

	private void setName(String unsavedTitle) {	
		try {
			propertiesObject.setValue(DOCUMENT_TITLE_PROPERTY_NAME, unsavedTitle);
			((UnsavedPropertiesObject) propertiesObject).setName(unsavedTitle);
		} catch (Exception e) {
		}
	}

	public ArrayList<Object> getContent() {
		return content;
	}

	public void setContent(ArrayList<Object> content) {
		this.content = content;
		String contentName = getContentName();
		if (contentName != null) {
			setName(contentName);
		}
	}

	private String getContentName() {
		if ( this.content != null && this.content.size() > 0 ) {
			Object object = this.content.get(0);
			
			if (object instanceof IFile) {
				IFile file = (IFile) object;
				return file.getName();
			}
			
			if (object instanceof File) {
				File file = (File) object;
				return file.getName();
			}
		}
		return null;
	}

	public String getMimeType() {
		return mimeType;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setCheckinMajor(boolean checkinMajor) {
		this.checkinMajor = checkinMajor;
	}

	public boolean isCheckinMajor() {
		return checkinMajor;
	}

	public void setAutoClassify(boolean autoClassify) {
		this.autoClassify = autoClassify;
	}

	public boolean isAutoClassify() {
		return autoClassify;
	}
}
