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

package com.ecmdeveloper.plugin.cmis.model;

import java.util.ArrayList;
import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.ClassesManager;
import com.ecmdeveloper.plugin.core.model.ClassesPlaceholder;
import com.ecmdeveloper.plugin.core.model.IClassDescriptionFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.constants.ClassDescriptionFolderType;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetClassDescriptionTask;

/**
 * This class represents a virtual folder in the Object Store classes
 * hierarchy.
 * 
 * @author Ricardo.Belfor
 *
 */
public class ClassDescriptionFolder implements IClassDescriptionFolder {

	protected IObjectStore objectStore;
	protected ClassDescriptionFolderType type;
	protected Collection<Object> children;
	
	public ClassDescriptionFolder(ClassDescriptionFolderType type , IObjectStore objectStore) {
		super();
		this.objectStore = objectStore;
		this.type = type;
	}

	public String getName() {
		return type.toString();
	}

	public ClassDescriptionFolderType getType() {
		return type;
	}
	
	public Collection<Object> getChildren() 
	{
		if ( children == null )
		{
			children = new ArrayList<Object>();
			children.add( new ClassesPlaceholder() );

			ITaskFactory taskFactory = objectStore.getTaskFactory();
			String className = getClassName(type);
			IGetClassDescriptionTask task = taskFactory.getGetClassDescriptionTask( className, objectStore, this );
			ClassesManager.getManager().executeTaskASync(task);
		}
		
		return children;
	}

	public void setChildren(ArrayList<Object> children ) {
		this.children = children;
	}

	public Object getParent() {
		return objectStore;
	}

	private String getClassName(ClassDescriptionFolderType classDescriptionFolderType)
	{
		switch (classDescriptionFolderType) {
		case DOCUMENT_CLASSES: return "cmis:document";
		case FOLDER_CLASSES: return "cmis:folder";
		}
		return null;
	}
}
