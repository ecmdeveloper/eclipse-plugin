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

/**
 * @author ricardo.belfor
 *
 */
public class NewCustomObjectEditorInput extends NewObjectStoreItemEditorInput {

	private static final String DEFAULT_CUSTOM_OBJECT_NAME = "Custom Object {0}";

	private static int newCustomObjectIndex = 0;
	
	public NewCustomObjectEditorInput(IClassDescription classDescription, IObjectStoreItem parent) {
		super(classDescription, parent);

		String unsavedTitle = MessageFormat.format(DEFAULT_CUSTOM_OBJECT_NAME, ++newCustomObjectIndex);
		setUnsavedName(unsavedTitle);
	}
}
