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

package com.ecmdeveloper.plugin.tracker.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.tracker.Activator;
import com.ecmdeveloper.plugin.tracker.util.PluginLog;


/**
 * @author Ricardo.Belfor
 *
 */
public class FilesTracker {

	private static final String FILES_TRACKER_FILENAME = "files_tracker.xml";
	private static final int CURRENT_FILE_VERSION = 1;
	private static FilesTracker filesTracker;
	private List<FilesTrackerListener> listeners = new ArrayList<FilesTrackerListener>();

	Map<String,TrackedFile> trackedFilesMap;
	
	public static FilesTracker getInstance()
	{
		if ( filesTracker == null )
		{
			filesTracker = new FilesTracker();
		}
		return filesTracker;
	}
	
	public void addTrackedFile(String filename, String id, String name, String className,
			String versionSeriesId, String connectionName, String connectionDisplayName,
			String objectStoreName, String objectStoreDisplayName, String mimeType)
	{
		initializeTrackedFiles();
		
		TrackedFile trackedFile = new TrackedFile();
		
		trackedFile.setFilename(filename);
		trackedFile.setId(id);
		trackedFile.setName(name);
		trackedFile.setVersionSeriesId(versionSeriesId);
		trackedFile.setConnectionName(connectionName);
		trackedFile.setConnectionDisplayName(connectionDisplayName);
		trackedFile.setObjectStoreName(objectStoreName);
		trackedFile.setObjectStoreDisplayName(objectStoreDisplayName);
		trackedFile.setClassName( className );
		trackedFile.setMimeType(mimeType);
		
		trackedFilesMap.put(filename, trackedFile);
		
		saveTrackedFiles();
		fireFilesChanged();
	}
	
	public boolean isFileTracked(String filename) {
		initializeTrackedFiles();
		return trackedFilesMap.containsKey(filename);
	}
	
	public boolean isVersionSeriesTracked(String versionSeriesId) {
		return getVersionSeriesTrackedFile(versionSeriesId) != null;
	}

	public TrackedFile getVersionSeriesTrackedFile(String versionSeriesId) {

		if ( versionSeriesId == null){
			return null;
		}
		
		initializeTrackedFiles();
		
		for ( TrackedFile trackedFile : trackedFilesMap.values() ) {
			if ( versionSeriesId.equalsIgnoreCase( trackedFile.getVersionSeriesId() ) ) {
				return trackedFile;
			}
		}
		return null;
	}

	public boolean removeTrackedVersionSeries(String versionSeriesId) {
		if ( versionSeriesId != null) {
			initializeTrackedFiles();
			for ( TrackedFile trackedFile : trackedFilesMap.values() ) {
				if ( versionSeriesId.equalsIgnoreCase( trackedFile.getVersionSeriesId() ) ) {
					trackedFilesMap.remove(trackedFile.getFilename());
					saveTrackedFiles();
					fireFilesChanged();
					return true;
				}
			}
		}
		return false;
	}

	public void removeTrackedFile(TrackedFile trackedFile) {
		initializeTrackedFiles();
		trackedFilesMap.remove( trackedFile.getFilename() );
		saveTrackedFiles();
		fireFilesChanged();
	}

	public Collection<TrackedFile> getTrackedFiles() {
		initializeTrackedFiles();
		return trackedFilesMap.values();
	}
	public TrackedFile getTrackedFile(String filename ) {
		initializeTrackedFiles();
		if ( trackedFilesMap.containsKey(filename) ) {
			return trackedFilesMap.get(filename);
		}
		return null;
	}
	
	private void initializeTrackedFiles() {
		if ( trackedFilesMap == null) {
			trackedFilesMap = new HashMap<String, TrackedFile>();
			loadTrackedFiles();
		}
	}
	
	private void loadTrackedFiles() {
	
		FileReader reader = getFilesTrackerFileReader();
		if ( reader == null ) {
			return;
		}
		
		try {
			XMLMemento memento = XMLMemento.createReadRoot( reader );
			//IMemento trackedFilesChild = memento.getChild( FilesTrackerTagNames.TRACKED_FILES );
			for ( IMemento trackedFileChild : memento.getChildren( FilesTrackerTagNames.TRACKED_FILE ) ) {
				TrackedFile trackedFile = loadTrackedFile(trackedFileChild);
				trackedFilesMap.put(trackedFile.getFilename(), trackedFile );
			}
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

	private TrackedFile loadTrackedFile(IMemento trackedFileChild) {
		TrackedFile trackedFile = new TrackedFile();
		
		trackedFile.setFilename( trackedFileChild.getString( FilesTrackerTagNames.FILENAME) );
		trackedFile.setId(trackedFileChild.getString( FilesTrackerTagNames.ID) );
		trackedFile.setName(trackedFileChild.getString( FilesTrackerTagNames.NAME) );
		trackedFile.setClassName(trackedFileChild.getString( FilesTrackerTagNames.CLASS_NAME) );
		trackedFile.setVersionSeriesId( trackedFileChild.getString( FilesTrackerTagNames.VERSION_SERIES_ID) );
		trackedFile.setConnectionName(trackedFileChild.getString( FilesTrackerTagNames.CONNECTION_NAME ) );
		trackedFile.setConnectionDisplayName(trackedFileChild.getString( FilesTrackerTagNames.CONNECTION_DISPLAY_NAME ) );
		trackedFile.setObjectStoreName(trackedFileChild.getString( FilesTrackerTagNames.OBJECT_STORE_NAME ) );
		trackedFile.setObjectStoreDisplayName(trackedFileChild.getString( FilesTrackerTagNames.OBJECT_STORE_DISPLAY_NAME ) );
		trackedFile.setMimeType(trackedFileChild.getString( FilesTrackerTagNames.MIME_TYPE ) );
		
		return trackedFile;
	}
	
	private void saveTrackedFiles() {

		initializeTrackedFiles();
		
		XMLMemento memento = XMLMemento.createWriteRoot(FilesTrackerTagNames.TRACKED_FILES);
		memento.putInteger(FilesTrackerTagNames.VERSION, CURRENT_FILE_VERSION );
	
		for ( TrackedFile trackedFile : trackedFilesMap.values() ) {
			saveTrackedFile(memento, trackedFile);
		}
		
		saveFilesTrackerFile( memento);
	}

	private void saveTrackedFile(XMLMemento memento, TrackedFile trackedFile) {

		IMemento trackedFileChild = memento.createChild(FilesTrackerTagNames.TRACKED_FILE );

		trackedFileChild.putString(FilesTrackerTagNames.FILENAME, trackedFile.getFilename() );
		trackedFileChild.putString(FilesTrackerTagNames.ID, trackedFile.getId() );
		trackedFileChild.putString(FilesTrackerTagNames.NAME, trackedFile.getName() );
		trackedFileChild.putString(FilesTrackerTagNames.CLASS_NAME, trackedFile.getClassName() );
		trackedFileChild.putString(FilesTrackerTagNames.VERSION_SERIES_ID, trackedFile.getVersionSeriesId() );
		trackedFileChild.putString(FilesTrackerTagNames.CONNECTION_NAME, trackedFile.getConnectionName() );
		trackedFileChild.putString(FilesTrackerTagNames.CONNECTION_DISPLAY_NAME, trackedFile.getConnectionDisplayName() );
		trackedFileChild.putString(FilesTrackerTagNames.OBJECT_STORE_NAME, trackedFile.getObjectStoreName() );
		trackedFileChild.putString(FilesTrackerTagNames.OBJECT_STORE_DISPLAY_NAME, trackedFile.getObjectStoreDisplayName() );
		trackedFileChild.putString(FilesTrackerTagNames.MIME_TYPE, trackedFile.getMimeType() );
	}

	private File getFilesTrackerFile()
	{
		return Activator.getDefault().getStateLocation().append(FILES_TRACKER_FILENAME).toFile();
	}

	private void saveFilesTrackerFile(XMLMemento memento) {

		FileWriter writer = null;
		try {
			writer = new FileWriter( getFilesTrackerFile() );
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
	
	private FileReader getFilesTrackerFileReader() {
		FileReader reader = null;
		try {
			reader = new FileReader(getFilesTrackerFile());
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			PluginLog.error(e);
		}	
		return reader;
	}

	public void addCodeModuleManagerListener( FilesTrackerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeCodeModulesManagerListener( FilesTrackerListener listener) {
		listeners.remove(listener);
	}
	
	private void fireFilesChanged() {
		for (FilesTrackerListener listener : listeners) {
			listener.filesChanged();
		}
	}
}
