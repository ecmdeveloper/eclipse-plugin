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

package com.ecmdeveloper.plugin.cmis.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.cmis.Activator;

/**
 * @author ricardo.belfor
 *
 */
public class RepositoryViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {

	private List<Repository> repositories;
	private Session session;

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		if ( repositories == null ) {
			SessionFactory sessionFactory = Activator.getDefault().getSessionFactory();
			
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put(SessionParameter.USER, "admin");
			parameters.put(SessionParameter.PASSWORD, "admin");
			parameters.put(SessionParameter.ATOMPUB_URL,"http://192.168.220.129/chemistry-opencmis-server-inmemory-0.4.0/atom");
			parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			
			repositories = sessionFactory.getRepositories(parameters);
			if ( ! repositories.isEmpty() ) {
				parameters.put(SessionParameter.REPOSITORY_ID, repositories.get(0).getId() );
				session = sessionFactory.createSession(parameters);
			}
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if ( inputElement instanceof String ) {
			return repositories.toArray();
		} 
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if ( parentElement instanceof Repository) {
			Folder rootFolder = session.getRootFolder();
			ItemIterable<CmisObject> children = rootFolder.getChildren();
			return toArray(children);
		} else if ( parentElement instanceof Folder) {
			Folder folder = (Folder) parentElement;
			return toArray(folder.getChildren());
		}
		return null;
	}

	private Object[] toArray(ItemIterable<CmisObject> children) {
		ArrayList<CmisObject> childFolders = new ArrayList<CmisObject>();
		for (CmisObject cmisObject : children ) {
			childFolders.add(cmisObject);
		}
		return childFolders.toArray();
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof Repository || element instanceof Folder;
	}

}
