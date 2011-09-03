/**
 * Copyright 2009,2010, Ricardo Belfor
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
package com.ecmdeveloper.plugin.classes.model.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.Placeholder;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.filenet.api.collection.ClassDescriptionSet;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetChildClassDescriptionsTask extends BaseTask {

	private ClassDescription parent;
	private Placeholder placeholder;
	private Collection<Object> oldChildren;
	
	public GetChildClassDescriptionsTask(ClassDescription parent) {
		this(parent,null);
	}

	public GetChildClassDescriptionsTask(ClassDescription parent, Placeholder placeholder) {
		this.parent = parent;
		this.placeholder = placeholder;
	}

	public ClassDescription getParent() {
		return parent;
	}

	public Collection<Object> getOldChildren() {
		return oldChildren;
	}

	@Override
	protected Object execute() throws Exception {
		getImmediateSubclassDescriptions();
		fireTaskCompleteEvent(TaskResult.COMPLETED);
		return null;
	}

	@SuppressWarnings("unchecked")
	private void getImmediateSubclassDescriptions() {
		com.filenet.api.meta.ClassDescription classDescriptionObject = 
			(com.filenet.api.meta.ClassDescription) parent.getObjectStoreObject();

		ClassDescriptionSet subclassDescriptions = classDescriptionObject.get_ImmediateSubclassDescriptions();
		
		ArrayList<ClassDescription> children = new ArrayList<ClassDescription>();

		for (Iterator iterator = subclassDescriptions.iterator(); iterator.hasNext();) 
		{
			com.filenet.api.meta.ClassDescription childClassDescriptionObject = 
				(com.filenet.api.meta.ClassDescription) iterator.next();
			ClassDescription classDescription = new ClassDescription(
					childClassDescriptionObject, parent, parent.getObjectStore());
			children.add(classDescription);
			
//			if ( placeholder != null) {
//				placeholder.setMessage( MessageFormat.format("Loading \"{0}\"...", childClassDescriptionObject.get_DisplayName() ) );
//				fireClassDescriptionChanged( null, null, new ClassDescription[] { parent } );
//			}
		}	

		oldChildren = parent.getChildren();
		parent.setChildren( children );

	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return parent.getObjectStore().getConnection();
	}
}
