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

import java.text.MessageFormat;

import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.properties.model.UnsavedPropertiesObject;

/**
 * @author ricardo.belfor
 *
 */
public class NewFolderEditorInput extends NewObjectStoreItemEditorInput {

	private static final String DEFAULT_FOLDER_NAME = "Folder {0}";
	private static final String FOLDER_TITLE_PROPERTY_NAME = "FolderName";

	private static int newFolderIndex = 0;
	
	public NewFolderEditorInput(IClassDescription classDescription, IObjectStoreItem parent) {
		super(classDescription, parent);
		
		try {
			String unsavedTitle = MessageFormat.format(DEFAULT_FOLDER_NAME, ++newFolderIndex);
			propertiesObject.setValue(FOLDER_TITLE_PROPERTY_NAME, unsavedTitle);
			((UnsavedPropertiesObject) propertiesObject).setName(unsavedTitle);
		} catch (Exception e) {
		}
	}
}
