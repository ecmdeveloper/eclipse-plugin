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

package com.ecmdeveloper.plugin.favorites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ecmdeveloper.plugin.favorites.model.FavoriteDocument;
import com.ecmdeveloper.plugin.favorites.model.FavoriteObjectStoreItem;
import com.ecmdeveloper.plugin.favorites.util.PluginMessage;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.FetchObjectTask;

/**
 * @author ricardo.belfor
 *
 */
public class FetchFavoritesJob extends Job {

	private Collection<FavoriteObjectStoreItem> favorites;
	private ObjectStore objectStore;
	private Collection<ObjectStoreItem> objectStoreItems;
	
	
	public FetchFavoritesJob(Collection<FavoriteObjectStoreItem> favoriteFolders, ObjectStore objectStore) {
		super("Fetch Favorites");
		this.favorites = favoriteFolders;
		this.objectStore = objectStore;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		objectStoreItems = new ArrayList<ObjectStoreItem>();
		ObjectStoresManager objectStoresManager = ObjectStoresManager.getManager(); 
		for (FavoriteObjectStoreItem favorite : favorites ) {
			//String objectType ,= null;
			if ( favorite instanceof FavoriteDocument ) {
				
			}
			// TODO fix this
			FetchObjectTask task = new FetchObjectTask(objectStore, favorite.getId(), favorite.getClassName(), "Folder" );
			try {
				Object result = objectStoresManager.executeTaskSync(task);
				objectStoreItems.add((ObjectStoreItem) result);
			} catch (ExecutionException e) {
				PluginMessage.openErrorFromThread(null, getName(), "Fething favorites failed", e);
			}
		}

		return Status.OK_STATUS;
	}

	public Collection<ObjectStoreItem> getObjectStoreItems() {
		return objectStoreItems;
	}
}
