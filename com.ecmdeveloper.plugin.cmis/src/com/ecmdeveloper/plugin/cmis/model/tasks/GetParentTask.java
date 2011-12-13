/**
 * Copyright 2011, Ricardo Belfor
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
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Session;

import com.ecmdeveloper.plugin.cmis.model.Folder;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItemFactory;
import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IGetParentTask;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;

/**
 * @author ricardo.belfor
 *
 */
public class GetParentTask extends AbstractTask implements IGetParentTask {

	private final ObjectStoreItem child;
	private final Collection<IFolder> parents;
	
	public GetParentTask(IObjectStoreItem child) {
		this.child = (ObjectStoreItem) child;
		parents = new ArrayList<IFolder>();
	}

	@Override
	public Collection<IFolder> getParents() {
		return parents;
	}

	@Override
	public Object call() throws Exception {
		
		List<org.apache.chemistry.opencmis.client.api.Folder> containers = getContainers();

		if (containers != null && !containers.isEmpty() ) {
			for ( org.apache.chemistry.opencmis.client.api.Folder internalFolder : containers) {
				Folder folder = ObjectStoreItemFactory.createFolder(internalFolder, null, child.getObjectStore() );
				parents.add( folder );
			}
		}

		if ( childIsFolder() ) {
			Folder folder = getParentFolder();
			parents.add( folder );
		}			

		return null;
	}

	private List<org.apache.chemistry.opencmis.client.api.Folder> getContainers() {
		
		CmisObject cmisObject = child.getCmisObject();
		if ( cmisObject instanceof FileableCmisObject) {
			FileableCmisObject fileableCmisObject = (FileableCmisObject) cmisObject;
			return fileableCmisObject.getParents();
		}
		return null;
	}

	private boolean childIsFolder() {
		return child instanceof Folder;
	}

	private Folder getParentFolder() {
		org.apache.chemistry.opencmis.client.api.Folder internalChildFolder = (org.apache.chemistry.opencmis.client.api.Folder) child.getCmisObject();
		String parentId = internalChildFolder.getParentId();
		Session session = child.getObjectStore().getSession();
		Folder folder = ObjectStoreItemFactory.createFolder(session.getObject(parentId), null, child.getObjectStore() );
		return folder;
	}
}
