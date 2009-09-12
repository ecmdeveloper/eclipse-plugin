package com.ecmdeveloper.plugin.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.ecmdeveloper.plugin.util.PluginTagNames;

public class CodeModulesManager {

	private static final String CODEMODULES_FOLDER = "codemodules";
	private static final String CODEMODULE_FILE_EXTENSION = "codemodule";

	private static CodeModulesManager codeModulesManager;
	protected ArrayList<CodeModuleFile> codeModulefiles;
	private List<CodeModulesManagerListener> listeners = new ArrayList<CodeModulesManagerListener>();
	
	private CodeModulesManager() {}
	
	public static CodeModulesManager getManager()
	{
		if ( codeModulesManager == null )
		{
			codeModulesManager = new CodeModulesManager();
		}
		return codeModulesManager;
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

	public CodeModuleFile createCodeModuleFile(CodeModule codeModule, ObjectStore objectStore) {

		CodeModuleFile codeModuleFile = new CodeModuleFile(
				codeModule.getName(), codeModule.getId(), objectStore
						.getConnection().getName(), objectStore.getName());

		codeModuleFile.setFilename( getCodeModuleFile(codeModuleFile).getPath() );
		saveCodeModuleFile(codeModuleFile);
		
		fireCodeModuleFilesChanged( new CodeModuleFile[] {codeModuleFile} , null, null );

		return codeModuleFile;
	}

	public void saveCodeModuleFile(CodeModuleFile codeModuleFile ) {

		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.CODE_MODULE);
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
			fireCodeModuleFilesChanged(null, null, new CodeModuleFile[] { codeModuleFile} );
		}

	}

	private void saveCodeModuleFile(CodeModuleFile codeModuleFile, XMLMemento memento) 
	{
		memento.putString( PluginTagNames.NAME_TAG, codeModuleFile.getName() );
		memento.putString( PluginTagNames.ID_TAG, codeModuleFile.getId() );
		memento.putString( PluginTagNames.CONNECTION_NAME_TAG, codeModuleFile.getConnectionName() );
		memento.putString( PluginTagNames.OBJECT_STORE_NAME_TAG, codeModuleFile.getObjectStoreName() );
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
		String objectStoreName = memento.getString( PluginTagNames.OBJECT_STORE_NAME_TAG );
		
		CodeModuleFile codeModuleFile = new CodeModuleFile(name, id, connectionName, objectStoreName );
		
		IMemento filesChild = memento.getChild(PluginTagNames.FILES_TAG );
		if ( filesChild != null ) {
			for ( IMemento fileChild : filesChild.getChildren(PluginTagNames.FILE_TAG) ) {
				codeModuleFile.addFile( new File( fileChild.getString( PluginTagNames.NAME_TAG ) ) );
			}
		}
		
		codeModuleFile.setFilename( filename );
		
		return codeModuleFile;
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
		String filename = codeModuleFile.getId() + "." + CODEMODULE_FILE_EXTENSION;
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
	
}
