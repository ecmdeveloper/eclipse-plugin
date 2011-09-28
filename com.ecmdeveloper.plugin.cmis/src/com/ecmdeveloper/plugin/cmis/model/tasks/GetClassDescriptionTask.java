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
package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Session;

import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IClassDescriptionFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetClassDescriptionTask;
import com.ecmdeveloper.plugin.cmis.model.ClassDescription;
import com.ecmdeveloper.plugin.cmis.model.ClassDescriptionFolder;
import com.ecmdeveloper.plugin.cmis.model.ObjectStore;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class GetClassDescriptionTask extends AbstractTask implements IGetClassDescriptionTask {

	private String name;
	private ObjectStore objectStore;
	private Object parent;
	private ClassDescription classDescription;
	private Collection<Object> oldChildren;
	private ArrayList<IClassDescription> children;

	public GetClassDescriptionTask(String name, IObjectStore objectStore) {
		this(name, null, objectStore);
	}
	
	public GetClassDescriptionTask(String name, Object parent, IObjectStore objectStore) {
		super();
		this.name = name;
		this.objectStore = (ObjectStore) objectStore;
		this.parent = parent;
	}

	@Override
	public IClassDescription getClassDescription() {
		return classDescription;
	}

	@Override
	public Collection<Object> getOldChildren() {
		return oldChildren;
	}

	@Override
	public ArrayList<IClassDescription> getChildren() {
		return children;
	}

	@Override
	public Object call() throws Exception {

		Session session = objectStore.getSession();
		ObjectType typeDefinition = session.getTypeDefinition(name);

		classDescription = new ClassDescription( typeDefinition, parent, objectStore );
		
		addToParent(classDescription);
		fireTaskCompleteEvent(TaskResult.COMPLETED);
		return classDescription;
	}

	private void addToParent(ClassDescription classDescription) {

		if ( parent == null ) {
			return;
		}
		
		children = new ArrayList<IClassDescription>();
		children.add(classDescription);

		oldChildren = null;
		if ( parent instanceof IClassDescriptionFolder) {
			oldChildren = addToVirtualFolder(children);
		}
	}

	private Collection<Object> addToVirtualFolder(ArrayList<IClassDescription> children) {
		Collection<Object> oldChildren;
		oldChildren = ((IClassDescriptionFolder) parent).getChildren();
		ArrayList<Object> childObjects = new ArrayList<Object>();
		childObjects.addAll(children);
		((ClassDescriptionFolder)parent).setChildren( ( childObjects ) );
		return oldChildren;
	}
}
