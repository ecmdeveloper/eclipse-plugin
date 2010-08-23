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

import java.util.ArrayList;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.model.Folder;

/**
 * @author ricardo.belfor
 *
 */
public class NewDocumentEditorInput extends NewObjectStoreItemEditorInput {

	private ArrayList<Object> content;
	private String mimeType;
	private boolean checkinMajor;
	private boolean autoClassify;
	
	public NewDocumentEditorInput(ClassDescription classDescription, Folder parent) {
		super(classDescription, parent);
	}

	public ArrayList<Object> getContent() {
		return content;
	}

	public void setContent(ArrayList<Object> content) {
		this.content = content;
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
