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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.favorites.Activator;
import com.ecmdeveloper.plugin.favorites.util.PluginLog;
import com.ecmdeveloper.plugin.favorites.util.PluginTagNames;

/**
 * @author ricardo.belfor
 *
 */
public class FavoritesStore {

	private static final String FAVORITES_FILENAME = "favorites.xml";
	private static final int CURRENT_FILE_VERSION = 1;
	
	public void saveFavorites(Collection<FavoriteObjectStoreItem> favorites ) {

		XMLMemento favoritesChild = XMLMemento.createWriteRoot(PluginTagNames.FAVORITES);
		favoritesChild.putInteger(PluginTagNames.VERSION, CURRENT_FILE_VERSION );
		favoritesChild.createChild(PluginTagNames.FOLDERS );
		favoritesChild.createChild(PluginTagNames.DOCUMENTS );
		
		for ( FavoriteObjectStoreItem favorite : favorites ) {
			saveFavorite(favoritesChild, favorite);
		}
		
		saveFavoritesFile(favoritesChild);
	}

	private void saveFavorite(XMLMemento favoritesChild, FavoriteObjectStoreItem favorite) {

		if ( favorite instanceof FavoriteFolder ) {
			IMemento foldersChild = favoritesChild.getChild( PluginTagNames.FOLDERS);
			saveFavoriteFolder(favorite, foldersChild .createChild(PluginTagNames.FOLDER ) );
		} else if ( favorite instanceof FavoriteDocument ) {
			IMemento documentsChild = favoritesChild.getChild( PluginTagNames.DOCUMENTS );
			saveFavoriteDocument(favorite, documentsChild .createChild(PluginTagNames.DOCUMENT));
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private void saveFavoriteDocument(FavoriteObjectStoreItem favorite, IMemento documentsChild) {
		saveFavoriteObjectStoreItem(favorite, documentsChild);
//		trackedFileChild.putString(FilesTrackerTagNames.VERSION_SERIES_ID, trackedFile.getVersionSeriesId() );
	}

	private void saveFavoriteFolder(FavoriteObjectStoreItem favorite, IMemento foldersChild) {
		saveFavoriteObjectStoreItem(favorite, foldersChild);
	}

	private void saveFavoriteObjectStoreItem(FavoriteObjectStoreItem favorite, IMemento trackedFileChild) {
		trackedFileChild.putString(PluginTagNames.ID, favorite.getId() );
		trackedFileChild.putString(PluginTagNames.CLASS_NAME, favorite.getClassName() );
		trackedFileChild.putString(PluginTagNames.CONNECTION_NAME, favorite.getConnectionName() );
		trackedFileChild.putString(PluginTagNames.OBJECT_STORE_NAME, favorite.getObjectStoreName() );
	}

	private void saveFavoritesFile(XMLMemento memento) {

		FileWriter writer = null;
		try {
			writer = new FileWriter( getFavoritesFile() );
			memento.save(writer);
		} catch (IOException e) {
			PluginLog.error(e);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				PluginLog.error(e);
			}
		}
	}

	public Collection<FavoriteObjectStoreItem> loadFavorites() {
		
		Collection<FavoriteObjectStoreItem> favorites = new HashSet<FavoriteObjectStoreItem>();
		
		FileReader reader = getFilesTrackerFileReader();
		if ( reader != null ) {
			readFavorites(favorites, reader);
		}
		return favorites;
	}

	private void readFavorites(Collection<FavoriteObjectStoreItem> favorites, FileReader reader) {
		try {
			XMLMemento favoritesChild = XMLMemento.createReadRoot( reader );
			loadFavoriteFolders(favorites, favoritesChild);
			loadFavoriteDocuments(favorites, favoritesChild);
		} catch (WorkbenchException e) {
			PluginLog.error(e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				PluginLog.error(e);
			}
		}
	}

	private void loadFavoriteFolders(Collection<FavoriteObjectStoreItem> favorites, XMLMemento favoritesChild) {
		IMemento foldersChild = favoritesChild.getChild( PluginTagNames.FOLDERS );
		for ( IMemento folderChild : foldersChild.getChildren( PluginTagNames.FOLDER ) ) {
			FavoriteFolder favoriteFolder = loadFavoriteFolder(folderChild);
			favorites.add( favoriteFolder );
		}
	}
	
	private void loadFavoriteDocuments(Collection<FavoriteObjectStoreItem> favorites, XMLMemento favoritesChild) {
		IMemento documentsChild = favoritesChild.getChild( PluginTagNames.DOCUMENTS );
		for ( IMemento documentChild : documentsChild.getChildren( PluginTagNames.DOCUMENT) ) {
			FavoriteDocument favoriteDocument = loadFavoriteDocument(documentChild);
			favorites.add( favoriteDocument );
		}
	}

	private FavoriteFolder loadFavoriteFolder(IMemento folderChild) {
		
		String objectStoreName = folderChild.getString( PluginTagNames.OBJECT_STORE_NAME );
		String connectionName = folderChild.getString( PluginTagNames.CONNECTION_NAME );
		String className = folderChild.getString( PluginTagNames.CLASS_NAME );
		String id = folderChild.getString( PluginTagNames.ID );
		
		return new FavoriteFolder(id, className, connectionName, objectStoreName );
	}

	private FavoriteDocument loadFavoriteDocument(IMemento documentChild) {
		
		String objectStoreName = documentChild.getString( PluginTagNames.OBJECT_STORE_NAME );
		String connectionName = documentChild.getString( PluginTagNames.CONNECTION_NAME );
		String className = documentChild.getString( PluginTagNames.CLASS_NAME );
		String id = documentChild.getString( PluginTagNames.ID );
		
		// TODO something with version series
		
		return new FavoriteDocument(id, className, connectionName, objectStoreName );
	}
	
	private FileReader getFilesTrackerFileReader() {
		FileReader reader = null;
		try {
			reader = new FileReader(getFavoritesFile());
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			PluginLog.error(e);
		}	
		return reader;
	}

	private File getFavoritesFile() {
		return Activator.getDefault().getStateLocation().append(FAVORITES_FILENAME).toFile();
	}
}
