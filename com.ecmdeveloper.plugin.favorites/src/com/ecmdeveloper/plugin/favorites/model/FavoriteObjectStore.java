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

package com.ecmdeveloper.plugin.favorites.model;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.ecmdeveloper.plugin.favorites.FetchFavoritesJob;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.Placeholder;

/**
 * @author ricardo.belfor
 *
 */
public class FavoriteObjectStore {

	private ObjectStore objectStore;
	private Collection<Object> children;

	public FavoriteObjectStore(ObjectStore objectStore) {
		this.objectStore = objectStore;
	}

	public ObjectStore getObjectStore() {
		return objectStore;
	}

	public Collection<Object> getChildren() 
	{
		if ( children == null )
		{
			children = new ArrayList<Object>();
			children.add( new Placeholder() );

			FavoritesManager.getInstance().fetchFavorites(this);
		}		
		return children;
	}

	public void refreshChildren(Collection<ObjectStoreItem> children) {
		this.children.clear();
		this.children.addAll( children );
	}

	boolean hasChildren() {
		if (children == null ) {
			return true;
		}
		return children.size() > 0;
	}
}