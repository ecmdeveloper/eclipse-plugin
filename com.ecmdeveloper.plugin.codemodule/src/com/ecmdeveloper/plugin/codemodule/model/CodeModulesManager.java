/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.codemodule.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.codemodule.Activator;
import com.ecmdeveloper.plugin.codemodule.util.PluginLog;
import com.ecmdeveloper.plugin.codemodule.util.PluginTagNames;
import com.ecmdeveloper.plugin.model.Action;
import com.ecmdeveloper.plugin.model.CodeModule;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerListener;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerRefreshEvent;
import com.ecmdeveloper.plugin.model.tasks.CreateCodeModuleTask;
import com.ecmdeveloper.plugin.model.tasks.GetCodeModuleActionsTask;
import com.ecmdeveloper.plugin.model.tasks.GetCodeModulesTask;
import com.ecmdeveloper.plugin.model.tasks.UpdateCodeModuleTask;
import com.ecmdeveloper.plugin.model.tasks.UpdateTask;

/**
 * This class is the manager class for Code Module Files. It is implemented as a
 * singleton class.
 * 
 * @author Ricardo.Belfor
 * 
 */
public class CodeModulesManager implements ObjectStoresManagerListener {

	private static final int CURRENT_FILE_VERSION = 1;

	private static final String CODE_MODULE_FILE_FORMAT = "{0}_{1}_{2}.{3}";
	private static final String CODEMODULES_FOLDER = "codemodules";
	private static final String CODEMODULE_FILE_EXTENSION = "codemodule";

	private static CodeModulesManager codeModulesManager;
	protected ArrayList<CodeModuleFile> codeModulefiles;
	protected ObjectStoresManager objectStoresManager;
	
	private List<CodeModulesManagerListener> listeners = new ArrayList<CodeModulesManagerListener>();
	
	private CodeModulesManager() {
		getObjectStoresManager();
		getCodeModuleFiles();	
	}
	
	public static CodeModulesManager getManager()
	{
		if ( codeModulesManager == null ) {
			codeModulesManager = new CodeModulesManager();
		}
		return codeModulesManager;
	}

	private ObjectStoresManager getObjectStoresManager() {

		if ( objectStoresManager == null ) {
			objectStoresManager = ObjectStoresManager.getManager();
			objectStoresManager.addObjectStoresManagerListener(this);
		}
		return objectStoresManager;
	}
	
	public static void shutdown() {
      if ( codeModulesManager != null) {
      }
	}
	
	@SuppressWarnings("unchecked")
	public Collection<CodeModule> getNewCodeModules(ObjectStore objectStore ) throws ExecutionException {

		GetCodeModulesTask task = new GetCodeModulesTask( objectStore );
		Collection<CodeModule> codeModules = 
			(Collection<CodeModule>) getObjectStoresManager().executeTaskSync(task);

		ArrayList<CodeModule> newCodeModules = new ArrayList<CodeModule>();
		
		for (CodeModule codeModule : codeModules) {
			
			boolean found = false;
			
			if ( codeModulefiles != null ) {
				for ( CodeModuleFile codeModuleFile : codeModulefiles ) {
					if ( isSame(codeModule, codeModuleFile) ) {
						found = true;
						break;
					}
				}
			}
			
			if ( ! found ) {
				newCodeModules.add( codeModule );
			}
		}
		
		return newCodeModules;
	}

	/**
	 * Checks if the Code Module is the same as the Code Module managed
	 * by the Code Module file. It is not enough to check the Id as Id's
	 * may be the same for different Object Stores in case of an export/import.
	 * 
	 * @param codeModule
	 * @param codeModuleFile
	 * @return
	 */
	private boolean isSame(CodeModule codeModule, CodeModuleFile codeModuleFile) {
		return codeModule.getId().equalsIgnoreCase( codeModuleFile.getId() ) && 
				codeModuleFile.getObjectStoreName().equals( codeModule.getObjectStore().getName() ) &&
				codeModuleFile.getConnectionName().equals(codeModule.getObjectStore().getConnection().getName() );
	}

	public Collection<CodeModuleFile> getCodeModuleFiles() {

		if ( codeModulefiles == null )
		{
			File codeModulesPath = getCodeModulesPath().toFile();
	
		    FilenameFilter fileFilter = new FilenameFilter() {
				@Override
				public boolean accept(File parent, String name) {
					return name.endsWith( "." + CODEMODULE_FILE_EXTENSION );
				}
		    };
	
		    codeModulefiles = new ArrayList<CodeModuleFile>();
		    
		    for ( File file : codeModulesPath.listFiles(fileFilter) ) {
		    	CodeModuleFile codeModuleFile = loadCodeModuleFile(file.getPath() );
		    	if ( codeModuleFile != null) {
		    		codeModulefiles.add( codeModuleFile );
		    	}
		    }
		}
		
	    return codeModulefiles;
	}

	public CodeModuleFile createNewCodeModuleFile(ObjectStore objectStore, String name) {

		CodeModuleFile codeModuleFile = new CodeModuleFile(name, null,
				objectStore.getConnection().getName(), objectStore.getConnection().getDisplayName(), 
				objectStore.getName(), objectStore.getDisplayName() );
		
		return codeModuleFile;
	}

	public CodeModuleFile createCodeModuleFile(CodeModule codeModule, ObjectStore objectStore) {

		CodeModuleFile codeModuleFile = new CodeModuleFile( codeModule, objectStore );
		codeModuleFile.setFilename( getCodeModuleFile(codeModuleFile).getPath() );
		saveCodeModuleFile(codeModuleFile, true);
		codeModulefiles.add(codeModuleFile);

		return codeModuleFile;
	}

	public void saveCodeModuleFile(CodeModuleFile codeModuleFile ) {
		saveCodeModuleFile(codeModuleFile, false);
	}

	private void saveCodeModuleFile(CodeModuleFile codeModuleFile, boolean saveNew ) {

		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.CODE_MODULE);
		memento.putInteger(PluginTagNames.VERSION_TAG, CURRENT_FILE_VERSION );
		
		boolean saved = false;
		saveCodeModuleFile(codeModuleFile, memento);
		FileWriter writer = null;
		try {
			File outputFile;
			if ( codeModuleFile.getFilename() != null) {
				outputFile = new File( codeModuleFile.getFilename() );
			}
			else
			{
				outputFile = getCodeModuleFile(codeModuleFile);
			}
			writer = new FileWriter( outputFile );
			memento.save(writer);
			saved = true;
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
		
		if ( saved) {
			if ( saveNew ) {
				fireCodeModuleFilesChanged(new CodeModuleFile[] { codeModuleFile}, null, null );
			} else {
				fireCodeModuleFilesChanged(null, null, new CodeModuleFile[] { codeModuleFile} );
			}
		}
	}

	public void saveNewCodeModuleFile(CodeModuleFile codeModuleFile) throws ExecutionException {

		ObjectStore objectStore = getObjectStore(codeModuleFile);
		ObjectStore.assertConnected(objectStore);
		
		CreateCodeModuleTask task = new CreateCodeModuleTask(codeModuleFile
				.getName(), codeModuleFile.getFiles(), objectStore );
		
		CodeModule codeModule = (CodeModule) getObjectStoresManager().executeTaskSync(task);
		codeModuleFile.setId( codeModule.getId() );
		
		saveCodeModuleFile(codeModuleFile, true);

		codeModuleFile.setFilename( getCodeModuleFile(codeModuleFile).getPath() );
		codeModulefiles.add(codeModuleFile);
	}

	private ObjectStore getObjectStore(CodeModuleFile codeModuleFile) {

		ObjectStoresManager objectStoresManager = ObjectStoresManager.getManager();
		ObjectStore objectStore = objectStoresManager.getObjectStore(
				codeModuleFile.getConnectionName(), codeModuleFile
						.getObjectStoreName());
		
		if ( objectStore == null ) {
			throw new NullPointerException( "Object Store not found:" + codeModuleFile.getConnectionName() + ": " + codeModuleFile
					.getObjectStoreName() );
		}
		return objectStore;
	}

	private void saveCodeModuleFile(CodeModuleFile codeModuleFile, XMLMemento memento) 
	{
		memento.putString( PluginTagNames.NAME_TAG, codeModuleFile.getName() );
		memento.putString( PluginTagNames.ID_TAG, codeModuleFile.getId() );
		memento.putString( PluginTagNames.CONNECTION_NAME_TAG, codeModuleFile.getConnectionName() );
		memento.putString( PluginTagNames.CONNECTION_DISPLAY_NAME_TAG, codeModuleFile.getConnectionDisplayName() );
		memento.putString( PluginTagNames.OBJECT_STORE_NAME_TAG, codeModuleFile.getObjectStoreName() );
		memento.putString( PluginTagNames.OBJECT_STORE_DISPLAY_NAME_TAG, codeModuleFile.getObjectStoreDisplayName() );
		
		IMemento filesChild = memento.createChild(PluginTagNames.FILES_TAG); 
		
		for ( File file : codeModuleFile.getFiles() ) {
			
			IMemento fileChild = filesChild.createChild(PluginTagNames.FILE_TAG);
			fileChild.putString( PluginTagNames.NAME_TAG, file.getAbsolutePath() );
		}
	}

	public CodeModuleFile loadCodeModuleFile(String filename )
	{
		FileReader reader = null;
		try {
			reader = new FileReader( filename );
			return loadCodeModuleFile(XMLMemento.createReadRoot(reader), filename);
		} catch (FileNotFoundException e) {
			// Ignored... no object store items exist yet.
		} catch (Exception e) {
			// Log the exception and move on.
			PluginLog.error(e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				PluginLog.error(e);
			}
		}
		return null;
	}
	
	private CodeModuleFile loadCodeModuleFile(XMLMemento memento, String filename) 
	{
		String name = memento.getString( PluginTagNames.NAME_TAG );
		String id = memento.getString( PluginTagNames.ID_TAG );
		String connectionName = memento.getString( PluginTagNames.CONNECTION_NAME_TAG );
		String connectionDisplayName = memento.getString( PluginTagNames.CONNECTION_DISPLAY_NAME_TAG );
		String objectStoreName = memento.getString( PluginTagNames.OBJECT_STORE_NAME_TAG );
		String objectStoreDisplayName = memento.getString( PluginTagNames.OBJECT_STORE_DISPLAY_NAME_TAG );
		
		CodeModuleFile codeModuleFile = new CodeModuleFile(name, id, connectionName, connectionDisplayName, 
				objectStoreName, objectStoreDisplayName );
		
		IMemento filesChild = memento.getChild(PluginTagNames.FILES_TAG );
		if ( filesChild != null ) {
			for ( IMemento fileChild : filesChild.getChildren(PluginTagNames.FILE_TAG) ) {
				codeModuleFile.addFile( new File( fileChild.getString( PluginTagNames.NAME_TAG ) ) );
			}
		}
		
		codeModuleFile.setFilename( filename );
		
		return codeModuleFile;
	}

	@SuppressWarnings("unchecked")
	public Collection<Action> getCodeModuleActions( CodeModuleFile codeModuleFile ) throws ExecutionException {
		
		ObjectStore objectStore = getObjectStore(codeModuleFile);
		ObjectStore.assertConnected(objectStore);
		GetCodeModuleActionsTask task = new GetCodeModuleActionsTask(codeModuleFile.getId(), objectStore );
		return (Collection<Action>) getObjectStoresManager().executeTaskSync(task);
	}
	
	public void updateCodeModule(CodeModuleFile codeModuleFile, Object[] selectedActions ) throws ExecutionException {
		
		ObjectStore objectStore = getObjectStore(codeModuleFile);
		ObjectStore.assertConnected(objectStore);
		UpdateCodeModuleTask task = new UpdateCodeModuleTask(codeModuleFile
				.getId(), codeModuleFile.getName(), codeModuleFile.getFiles(),
				objectStore);
		
		CodeModule codeModule = (CodeModule) getObjectStoresManager().executeTaskSync(task);

		for ( Object objectStoreItem : selectedActions ) {
			if (objectStoreItem instanceof Action ) {
				((Action) objectStoreItem).setCodeModule( codeModule );
				UpdateTask updateTask = new UpdateTask((IObjectStoreItem) objectStoreItem );
				getObjectStoresManager().executeTaskSync(updateTask);
			}
		}
		
	}

	public static IPath getCodeModulesPath() {
		IPath parentFolder = Activator.getDefault().getStateLocation().append(CODEMODULES_FOLDER);
		
		if ( ! parentFolder.toFile().exists() )
		{
			parentFolder.toFile().mkdir();
		}
		return parentFolder;
	}
	
	public File getCodeModuleFile(CodeModuleFile codeModuleFile) {
		
		String filename = MessageFormat.format( CODE_MODULE_FILE_FORMAT, 
				codeModuleFile.getConnectionName(),
				codeModuleFile.getObjectStoreName(),
				codeModuleFile.getId(),
				CODEMODULE_FILE_EXTENSION );
		
		return CodeModulesManager.getCodeModulesPath().append( filename ).toFile();
	}

	public void removeCodeModuleFile(CodeModuleFile selectedObject) 
	{
		if ( selectedObject.getFilename() != null ) {
			(new java.io.File( selectedObject.getFilename() )).delete();
			codeModulefiles.remove( selectedObject );
			
			fireCodeModuleFilesChanged(null, new CodeModuleFile[] { selectedObject }, null );
		}
	}

	public void addCodeModuleManagerListener( CodeModulesManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeCodeModulesManagerListener( CodeModulesManagerListener listener) {
		listeners.remove(listener);
	}
	
	private void fireCodeModuleFilesChanged(CodeModuleFile[] itemsAdded,
			CodeModuleFile[] itemsRemoved, CodeModuleFile[] itemsUpdated ) {
		CodeModulesManagerEvent event = new CodeModulesManagerEvent(this,
				itemsAdded, itemsRemoved, itemsUpdated );
		for (CodeModulesManagerListener listener : listeners) {
			listener.codeModuleFilesItemsChanged( event );
		}
	}

	@Override
	public void objectStoreItemsChanged(ObjectStoresManagerEvent event) {
		
		if ( event.getItemsRemoved() != null ) {
			for ( IObjectStoreItem objectStoreItem : event.getItemsRemoved() ) {
				if ( objectStoreItem instanceof Document ) {
					removeDocumentCodeModuleFile(objectStoreItem);
				} else if ( objectStoreItem instanceof ObjectStore ) {
					updateObjectStoreCodeModuleFiles(objectStoreItem);
				}
			}
		}
		
		if ( event.getItemsUpdated() != null ) {
			for ( IObjectStoreItem objectStoreItem : event.getItemsUpdated() ) {
				if ( objectStoreItem instanceof Document ) {
					// TODO check if this is a Code Module
				} else if ( objectStoreItem instanceof ObjectStore ) {
					updateObjectStoreCodeModuleFiles(objectStoreItem);
				}
			}
		}
	}

	private void removeDocumentCodeModuleFile(IObjectStoreItem objectStoreItem) {

		String id = ((Document)objectStoreItem).getVersionSeriesId(); 
		
		for ( CodeModuleFile codeModuleFile: codeModulefiles ) {
			if ( id.equalsIgnoreCase( codeModuleFile.getId() ) ) {
				removeCodeModuleFile(codeModuleFile);
				break;
			}
		}
	}

	private void updateObjectStoreCodeModuleFiles( IObjectStoreItem objectStoreItem) {
		
		String objectStoreName = objectStoreItem.getName();
		String connectionName = ((ObjectStore) objectStoreItem).getConnection().getName();
		Set<CodeModuleFile> removedItems = new HashSet<CodeModuleFile>();

		for ( CodeModuleFile codeModuleFile: codeModulefiles ) {
			if ( objectStoreName.equalsIgnoreCase( codeModuleFile.getObjectStoreName() ) &&
				 connectionName.equalsIgnoreCase( codeModuleFile.getConnectionName() ) ) {
				removedItems.add( codeModuleFile );
			}
		}

		if ( ! removedItems.isEmpty() ) {
			fireCodeModuleFilesChanged(null, null, removedItems.toArray( new CodeModuleFile[ removedItems.size() ] ) );
		}
	}

	@Override
	public void objectStoreItemsRefreshed(ObjectStoresManagerRefreshEvent event) {
	}
}
