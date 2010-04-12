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
package com.ecmdeveloper.plugin.classes.model.task;

import java.util.ArrayList;
import java.util.Collection;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.VirtualFolder;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.ecmdeveloper.plugin.model.tasks.TaskResult;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class GetClassDescriptionTask extends BaseTask {

	private String name;
	private ObjectStore objectStore;
	private Object parent;
	private ClassDescription classDescription;
	private Collection<Object> oldChildren;
	private ArrayList<ClassDescription> children;

	public GetClassDescriptionTask(String name, ObjectStore objectStore) {
		this(name, null, objectStore);
	}
	
	public GetClassDescriptionTask(String name, Object parent, ObjectStore objectStore) {
		super();
		this.name = name;
		this.objectStore = objectStore;
		this.parent = parent;
	}

	public ClassDescription getClassDescription() {
		return classDescription;
	}

	public Collection<Object> getOldChildren() {
		return oldChildren;
	}

	public ArrayList<ClassDescription> getChildren() {
		return children;
	}

	@Override
	public Object call() throws Exception {

		com.filenet.api.core.ObjectStore objectStoreObject = 
			(com.filenet.api.core.ObjectStore) objectStore.getObjectStoreObject();

		com.filenet.api.meta.ClassDescription classDescriptionObject = 
			(com.filenet.api.meta.ClassDescription) objectStoreObject.fetchObject("ClassDescription", name, null);

		classDescription = new ClassDescription(classDescriptionObject, parent, objectStore );
		
		addToParent(classDescription);
		fireTaskCompleteEvent(TaskResult.COMPLETED);
		return classDescription;
	}

	private void addToParent(ClassDescription classDescription) {

		if ( parent == null ) {
			return;
		}
		
		children = new ArrayList<ClassDescription>();
		children.add(classDescription);

		oldChildren = null;
		if ( parent instanceof VirtualFolder) {
			oldChildren = addToVirtualFolder(children);
		}
	}

	private Collection<Object> addToVirtualFolder(ArrayList<ClassDescription> children) {
		Collection<Object> oldChildren;
		oldChildren = ((VirtualFolder) parent).getChildren();
		ArrayList<Object> childObjects = new ArrayList<Object>();
		childObjects.addAll(children);
		((VirtualFolder)parent).setChildren( ( childObjects ) );
		return oldChildren;
	}
}
