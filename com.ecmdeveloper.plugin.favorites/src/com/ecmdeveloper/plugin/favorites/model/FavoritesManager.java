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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.ecmdeveloper.plugin.favorites.jobs.FetchFavoritesJob;
import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStores;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerListener;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerRefreshEvent;
import com.ecmdeveloper.plugin.model.tasks.DeleteTask;


/**
 * @author ricardo.belfor
 *
 */
public class FavoritesManager implements ObjectStoresManagerListener {

	private static FavoritesManager favoritesManager;
	private List<FavoritesManagerListener> listeners = new ArrayList<FavoritesManagerListener>();
	private Collection<FavoriteObjectStore> favoriteObjectStores;
	private FavoritesStore favoritesStore = new FavoritesStore();
	Collection<FavoriteObjectStoreItem> favorites;
	private ObjectStoresManager objectStoresManager;
	
	public static FavoritesManager getInstance()
	{
		if ( favoritesManager == null )
		{
			favoritesManager = new FavoritesManager();
		}
		return favoritesManager;
	}

	public FavoritesManager() {
		objectStoresManager = ObjectStoresManager.getManager();
		objectStoresManager.addObjectStoresManagerListener(this);
		// TODO find a place to remove this object as listener
	}

	private void initializeFavorites() {
		if ( favorites == null) {
			favorites = favoritesStore.loadFavorites();
		}
	}
	public Collection<FavoriteObjectStoreItem> getFavoriteFolders(FavoriteObjectStore favoriteObjectStore) {
	
		initializeFavorites();
		
		ArrayList<FavoriteObjectStoreItem> objectStoreFavorites = new ArrayList<FavoriteObjectStoreItem>();

		for ( FavoriteObjectStoreItem favorite : favorites ) {
			if ( isFavoriteObjectStoreFavorite(favoriteObjectStore, favorite) ) {
				objectStoreFavorites.add(favorite);
			}
		}
		return objectStoreFavorites;
	}

	private boolean isFavoriteObjectStoreFavorite(FavoriteObjectStore favoriteObjectStore, FavoriteObjectStoreItem favorite) {

		ObjectStore objectStore = favoriteObjectStore.getObjectStore();
		String objectStoreName = objectStore.getName();
		String connectionName = objectStore.getConnection().getName();
		return favorite.getObjectStoreName().equals( objectStoreName ) && 
				favorite.getConnectionName().equals( connectionName );
	}
	
	public Collection<FavoriteObjectStore> getFavoriteObjectStores() {
		if ( favoriteObjectStores == null) {
			favoriteObjectStores = new ArrayList<FavoriteObjectStore>();
			ObjectStores objectStores = ObjectStoresManager.getManager().getObjectStores();
			for ( IObjectStoreItem objectStore : objectStores.getChildren() ) {
				favoriteObjectStores.add( new FavoriteObjectStore((ObjectStore) objectStore) );
			}
		}
		return favoriteObjectStores;
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
				fireFavoriteObjectStoreChanged(favoriteObjectStore);
				favoritesStore.saveFavorites(favorites);
			}} );
		job.schedule();
	}

	public void addFavorite(ObjectStoreItem objectStoreItem) {

		initializeFavorites();
		
		FavoriteObjectStoreItem favorite = createFavoriteObjectStoreItem(objectStoreItem);
		favorites.add(favorite);
		favoritesStore.saveFavorites(favorites);
		addFavoriteToFavoriteObjectStore(favorite, objectStoreItem );
	}

	private void addFavoriteToFavoriteObjectStore(FavoriteObjectStoreItem favorite, ObjectStoreItem objectStoreItem) {
		
		FavoriteObjectStore favoriteObjectStore = getFavoriteObjectStore(favorite);
		if ( favoriteObjectStore != null ) {
			favoriteObjectStore.addChild(objectStoreItem);
			fireFavoriteObjectStoreChanged(favoriteObjectStore);
		}
		return;
	}

	private void fireFavoriteObjectStoreChanged(FavoriteObjectStore favoriteObjectStore) {
		for ( FavoritesManagerListener listener : listeners ) {
			listener.favoritesLoaded(favoriteObjectStore);
		}
	}

	private FavoriteObjectStore getFavoriteObjectStore(FavoriteObjectStoreItem favorite) {
		for ( FavoriteObjectStore favoriteObjectStore : favoriteObjectStores ) {
			if ( isFavoriteObjectStoreFavorite(favoriteObjectStore, favorite) ) {
				return favoriteObjectStore;
			}
		}
		return null;
	}

	private FavoriteObjectStoreItem createFavoriteObjectStoreItem(ObjectStoreItem objectStoreItem) {
		ObjectStore objectStore = objectStoreItem.getObjectStore();
		String objectStoreName = objectStore.getName();
		String connectionName = objectStore.getConnection().getName();
		String className = objectStoreItem.getClassName();
		String id = objectStoreItem.getId();
		String name = objectStoreItem.getDisplayName();
		
		FavoriteObjectStoreItem favorite;
		
		if ( objectStoreItem instanceof Folder ) {
			favorite = new FavoriteFolder(id, name, className, connectionName, objectStoreName );
		} else if ( objectStoreItem instanceof Document ) {
			favorite = new FavoriteDocument(id, name, className, connectionName, objectStoreName );
		} else if ( objectStoreItem instanceof CustomObject ) {
			favorite = new FavoriteCustomObject(id, name, className, connectionName, objectStoreName );
		} else {
			throw new UnsupportedOperationException();
		}
		return favorite;
	}

	public void removeFavorite(ObjectStoreItem objectStoreItem) {
		removeFavorite(objectStoreItem, true );
	}

	private void removeFavorite(ObjectStoreItem objectStoreItem, boolean notify) {
	
		FavoriteObjectStoreItem favorite = getObjectStoreItemFavorite(objectStoreItem);
		if ( favorite == null ) {
			return;
		}
		favorites.remove(favorite);
		favoritesStore.saveFavorites(favorites);
	
		if ( notify ) {
			FavoriteObjectStore favoriteObjectStore = getFavoriteObjectStore(favorite);
			if ( favoriteObjectStore != null ) {
				favoriteObjectStore.removeChild(objectStoreItem);
				fireFavoriteObjectStoreChanged(favoriteObjectStore);
			}
		}
	}

	public void removeFavorite(FavoriteObjectStoreItem favorite) {
		favorites.remove(favorite);
		favoritesStore.saveFavorites(favorites);
	}

	private FavoriteObjectStoreItem getObjectStoreItemFavorite(ObjectStoreItem objectStoreItem) {
		for (FavoriteObjectStoreItem favorite  : favorites) {
			if ( favorite.isFavoriteOf(objectStoreItem)) {
				return favorite;
			}
		}
		return null;
	}

	public void addFavoritesManagerListener(FavoritesManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeFavoritesManagerListener(FavoritesManagerListener listener) {
		listeners.remove(listener);
	}

	public boolean isFavorite(ObjectStoreItem objectStoreItem) {

		initializeFavorites();
		
		for ( FavoriteObjectStoreItem  favorite : favorites ) {
			if ( favorite.isFavoriteOf(objectStoreItem) ) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void objectStoreItemsChanged(ObjectStoresManagerEvent event) {
		if ( event.getItemsRemoved() != null) {
			for ( IObjectStoreItem objectStoreItem : event.getItemsRemoved() ) {
				if ( isFavorite((ObjectStoreItem) objectStoreItem)) {
					removeFavorite((ObjectStoreItem) objectStoreItem, false );
				}
			}
		}
		
		notifyUpdatedFavoriteObjectStores(event);		
	}

	private void notifyUpdatedFavoriteObjectStores(ObjectStoresManagerEvent event) {
		Collection<ObjectStore> objectStoreItems = getObjectStoreItems(event.getItemsUpdated());
		for ( ObjectStore objectStore : objectStoreItems) {
			for (FavoriteObjectStore  favoriteObjectStore : getFavoriteObjectStores() ) {
				if ( favoriteObjectStore.isObjectStoreOf(objectStore) ) {
					fireFavoriteObjectStoreChanged(favoriteObjectStore);
					break;
				}
			}
		}
	}

	private Collection<ObjectStore> getObjectStoreItems(IObjectStoreItem[] objectStoreItems ) {
		Set<ObjectStore> objectStores = new HashSet<ObjectStore>();

		if ( objectStoreItems != null ) {
			for (IObjectStoreItem objectStoreItem : objectStoreItems ) {
				if (objectStoreItem instanceof ObjectStore) {
					objectStores.add((ObjectStore) objectStoreItem);
				}
			}
		}
		return objectStores;
	}
	
	@Override
	public void objectStoreItemsRefreshed(ObjectStoresManagerRefreshEvent event) {
		// TODO Auto-generated method stub
		
	}
}
