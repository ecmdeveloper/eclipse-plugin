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
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.ecmdeveloper.plugin.favorites.FetchFavoritesJob;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStores;


/**
 * @author ricardo.belfor
 *
 */
public class FavoritesManager {

	private static FavoritesManager favoritesManager;
	private List<FavoritesManagerListener> listeners = new ArrayList<FavoritesManagerListener>();
	private Collection<Object> favoriteObjectStores;
	private FavoritesStore favoritesStore = new FavoritesStore();
	Collection<FavoriteObjectStoreItem> favorites;
	
	public static FavoritesManager getInstance()
	{
		if ( favoritesManager == null )
		{
			favoritesManager = new FavoritesManager();
		}
		return favoritesManager;
	}

	private void initializeFavorites() {
		if ( favorites == null) {
			favorites = favoritesStore.loadFavorites();
		}
	}
	public Collection<FavoriteObjectStoreItem> getFavoriteFolders(FavoriteObjectStore favoriteObjectStore) {
	
		initializeFavorites();
		
		ArrayList<FavoriteObjectStoreItem> objectStoreFavorites = new ArrayList<FavoriteObjectStoreItem>();

		ObjectStore objectStore = favoriteObjectStore.getObjectStore();
		String objectStoreName = objectStore.getName();
		String connectionName = objectStore.getConnection().getName();

		for ( FavoriteObjectStoreItem favorite : favorites ) {
			if ( favorite.getObjectStoreName().equals( objectStoreName ) && 
					favorite.getConnectionName().equals( connectionName ) ) {
				objectStoreFavorites.add(favorite);
			}
		}
		return objectStoreFavorites;
	}
	
	public Object[] getFavoriteObjectStores(ObjectStores objectStores) {
		if ( favoriteObjectStores == null) {
			favoriteObjectStores = new ArrayList<Object>();
			for ( IObjectStoreItem objectStore : objectStores.getChildren() ) {
				favoriteObjectStores.add( new FavoriteObjectStore((ObjectStore) objectStore) );
			}
		}
		return favoriteObjectStores.toArray();
	}
	
	public void fetchFavorites(final FavoriteObjectStore favoriteObjectStore) {
		
		Collection<FavoriteObjectStoreItem> favoriteFolders = getFavoriteFolders(favoriteObjectStore);
		Job job = new FetchFavoritesJob(favoriteFolders, favoriteObjectStore.getObjectStore() );
		job.setUser(true);
		job.addJobChangeListener( new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {

				if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
					return;
				}

				FetchFavoritesJob job = (FetchFavoritesJob) event.getJob();
				favoriteObjectStore.refreshChildren(job.getObjectStoreItems());
				
				for ( FavoritesManagerListener listener : listeners ) {
					listener.favoritesLoaded(favoriteObjectStore);
				}
			}} );
		job.schedule();		
	}

	public void addFavoritesManagerListener(FavoritesManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeFavoritesManagerListener(FavoritesManagerListener listener) {
		listeners.remove(listener);
	}
}
