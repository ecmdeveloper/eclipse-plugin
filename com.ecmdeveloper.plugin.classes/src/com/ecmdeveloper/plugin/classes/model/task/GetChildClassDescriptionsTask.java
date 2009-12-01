/**
 * Copyright 2009, Ricardo Belfor
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
import com.filenet.api.collection.ClassDescriptionSet;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetChildClassDescriptionsTask extends BaseTask {

	private ClassDescription parent;
	
	public GetChildClassDescriptionsTask(ClassDescription parent) {
		super();
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object call() throws Exception {

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
		}	

		Collection<Object> oldChildren = parent.getChildren();
		
		parent.setChildren( children );

		fireClassDescriptionChanged( children.toArray( new ClassDescription[0] ), 
				oldChildren != null ? oldChildren.toArray( new ClassDescription[0] ) : null, new ClassDescription[] { parent } );

		return null;
	}
}
