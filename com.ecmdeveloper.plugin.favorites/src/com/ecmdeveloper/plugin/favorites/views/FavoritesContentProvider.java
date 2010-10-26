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

package com.ecmdeveloper.plugin.favorites.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.favorites.model.FavoriteObjectStore;
import com.ecmdeveloper.plugin.favorites.model.FavoritesManager;
import com.ecmdeveloper.plugin.favorites.model.FavoritesManagerListener;
import com.ecmdeveloper.plugin.model.ObjectStores;
import com.ecmdeveloper.plugin.views.ObjectStoresViewContentProvider;

/**
 * @author ricardo.belfor
 *
 */
public class FavoritesContentProvider extends ObjectStoresViewContentProvider implements FavoritesManagerListener {

	private FavoritesManager manager;
	private TreeViewer viewer;

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		
		this.viewer = (TreeViewer) viewer;
		
		if (manager != null) {
			manager.removeFavoritesManagerListener(this);
		}
		
		manager = FavoritesManager.getInstance();
		
		if (manager != null) {
			manager.addFavoritesManagerListener(this);
		}
	}



	@Override
	public Object[] getChildren(Object parent) {
		
		if (parent instanceof ObjectStores ) {
			return FavoritesManager.getInstance().getFavoriteObjectStores().toArray();
		} else if ( parent instanceof FavoriteObjectStore ) {
			FavoriteObjectStore favoriteObjectStore = (FavoriteObjectStore) parent;
			return favoriteObjectStore.getChildren().toArray();
		} else {
			return super.getChildren(parent);
		}
	}

		

	@Override
	public boolean hasChildren(Object parent) {
		if ( parent instanceof FavoriteObjectStore ) {
			return ((FavoriteObjectStore) parent).hasChildren();
		}
		return super.hasChildren(parent);
	}

	@Override
	public void favoritesLoaded(final FavoriteObjectStore favoriteObjectStore) {
		refreshViewer(favoriteObjectStore);
	}

	@Override
	public void favoriteObjectStoreRemoved(FavoriteObjectStore favoriteObjectStore) {
		refreshViewer((FavoriteObjectStore)null);
	}

	private void refreshViewer(final FavoriteObjectStore favoriteObjectStore) {
		viewer.getTree().getDisplay().asyncExec( new Runnable() {
			
			@Override
			public void run() {
				viewer.refresh( favoriteObjectStore );
			}
		});
	}
}
