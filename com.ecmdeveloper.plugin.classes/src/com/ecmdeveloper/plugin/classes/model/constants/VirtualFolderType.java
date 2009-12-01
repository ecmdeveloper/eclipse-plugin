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
package com.ecmdeveloper.plugin.classes.model.constants;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public enum VirtualFolderType {

	DOCUMENT_CLASSES() { 		
		@Override
		public String toString() {
			return DOCUMENT_CLASSES_NAME;
		}
	},
	FOLDER_CLASSES() { 		
		@Override
		public String toString() {
			return FOLDER_CLASSES_NAME;
		}
	},
	CUSTOM_OBJECT_CLASSES() { 		
		@Override
		public String toString() {
			return CUSTOM_OBJECT_CLASSES_NAME;
		}
	};

	private static final String CUSTOM_OBJECT_CLASSES_NAME = "Custom Object Classes";
	private static final String FOLDER_CLASSES_NAME = "Folder Classes";
	private static final String DOCUMENT_CLASSES_NAME = "Document Classes";
	
	public static String getClassName(VirtualFolderType virtualFolderType)
	{
		switch (virtualFolderType) {
		case DOCUMENT_CLASSES: return "Document";
		case FOLDER_CLASSES: return "Folder";
		case CUSTOM_OBJECT_CLASSES: return "CustomObject";
		}
		return null;
	}
}
