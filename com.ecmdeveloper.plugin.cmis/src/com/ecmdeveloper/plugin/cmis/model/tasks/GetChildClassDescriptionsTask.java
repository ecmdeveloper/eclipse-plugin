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
package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.Tree;

import com.ecmdeveloper.plugin.core.model.ClassesPlaceholder;
import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetChildClassDescriptionsTask;
import com.ecmdeveloper.plugin.cmis.model.ClassDescription;
import com.ecmdeveloper.plugin.cmis.model.ObjectStore;
import com.ecmdeveloper.plugin.cmis.util.PluginLog;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetChildClassDescriptionsTask extends AbstractTask implements IGetChildClassDescriptionsTask {

	private ClassDescription parent;
	private ClassesPlaceholder placeholder;
	private Collection<Object> oldChildren;
	
	public GetChildClassDescriptionsTask(ClassDescription parent) {
		this(parent,null);
	}

	public GetChildClassDescriptionsTask(ClassDescription parent, ClassesPlaceholder placeholder) {
		this.parent = parent;
		this.placeholder = placeholder;
	}

	@Override
	public IClassDescription getParent() {
		return parent;
	}

	@Override
	public Collection<Object> getOldChildren() {
		return oldChildren;
	}

	@Override
	public Object call() throws Exception {
		getImmediateSubclassDescriptions();
		fireTaskCompleteEvent(TaskResult.COMPLETED);
		return null;
	}

	private void getImmediateSubclassDescriptions() {
		
		ObjectType objectType = parent.getTypeDefinition();
		Session session = ((ObjectStore)parent.getObjectStore()).getSession();
		//
		// WORKARROUND: for IBM FileNet P8 gave an exception when the children of the children were fetched, so
		//				now the whole tree is fetched.
		//
		// ItemIterable<ObjectType> typeChildren = session.getTypeChildren(objectType.getId(), true);
		List<Tree<ObjectType>> descendants = session.getTypeDescendants(objectType.getId(), -1, true );
		
		ArrayList<IClassDescription> children = new ArrayList<IClassDescription>();
		oldChildren = parent.getChildren();
		
		try
		{
            for (Tree<ObjectType> treeItem : descendants) {
                addLevel(parent, treeItem);
            }
			
		} catch (Exception e) {
			ClassesPlaceholder classesPlaceholder = new ClassesPlaceholder(e);
			classesPlaceholder.setParent(parent);
			children.add( classesPlaceholder );
			PluginLog.error(e);
		}
	}

    private void addLevel(ClassDescription parent, Tree<ObjectType> tree) {

    	ObjectType childObjectType = tree.getItem();
		ClassDescription classDescription = new ClassDescription( childObjectType, parent, parent.getObjectStore());
        parent.addChild(classDescription);
        classDescription.setHasChildren(false);
        
        if ( tree.getChildren() != null) {
            for (Tree<ObjectType> treeItem : tree.getChildren()) {
                addLevel(classDescription, treeItem);
            }
        }
    }
}
