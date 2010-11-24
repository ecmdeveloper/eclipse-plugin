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

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.codemodule.Activator;
import com.ecmdeveloper.plugin.codemodule.util.PluginLog;
import com.ecmdeveloper.plugin.codemodule.util.PluginTagNames;

/**
 * @author ricardo.belfor
 *
 */
public class CodeModuleFileStore {

	private static final int CURRENT_FILE_VERSION = 1;

	private static final String CODE_MODULE_FILE_FORMAT = "{0}_{1}_{2}.{3}";
	private static final String CODEMODULE_FILE_EXTENSION = "codemodule";
	private static final String CODEMODULES_FOLDER = "codemodules";

	public boolean save(CodeModuleFile codeModuleFile) {
		
		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.CODE_MODULE);
		memento.putInteger(PluginTagNames.VERSION, CURRENT_FILE_VERSION );
		
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
		return saved;
	}

	private void saveCodeModuleFile(CodeModuleFile codeModuleFile, XMLMemento memento) 
	{
		memento.putString( PluginTagNames.NAME, codeModuleFile.getName() );
		memento.putString( PluginTagNames.ID, codeModuleFile.getId() );
		memento.putString( PluginTagNames.CONNECTION_NAME, codeModuleFile.getConnectionName() );
		memento.putString( PluginTagNames.CONNECTION_DISPLAY_NAME, codeModuleFile.getConnectionDisplayName() );
		memento.putString( PluginTagNames.OBJECT_STORE_NAME, codeModuleFile.getObjectStoreName() );
		memento.putString( PluginTagNames.OBJECT_STORE_DISPLAY_NAME, codeModuleFile.getObjectStoreDisplayName() );
		
		saveFiles(memento, codeModuleFile);
		saveJavaElements(memento, codeModuleFile);
	}

	private void saveFiles(XMLMemento memento, CodeModuleFile codeModuleFile) {
		IMemento filesChild = memento.createChild(PluginTagNames.FILES); 
		
		for ( File file : codeModuleFile.getFiles() ) {
			IMemento fileChild = filesChild.createChild(PluginTagNames.FILE);
			fileChild.putString( PluginTagNames.NAME, file.getAbsolutePath() );
		}
	}

	private void saveJavaElements(XMLMemento memento, CodeModuleFile codeModuleFile) {
		IMemento filesChild = memento.createChild(PluginTagNames.JAVA_ELEMENTS); 
		
		for ( IJavaElement javaElement : codeModuleFile.getJavaElements()) {
			IMemento fileChild = filesChild.createChild(PluginTagNames.JAVE_ELEMENT);
			fileChild.putString( PluginTagNames.NAME, javaElement.getHandleIdentifier() );
		}
	}
	
	public File getCodeModuleFile(CodeModuleFile codeModuleFile) {
		
		String filename = MessageFormat.format( CODE_MODULE_FILE_FORMAT, 
				codeModuleFile.getConnectionName(),
				codeModuleFile.getObjectStoreName(),
				codeModuleFile.getId(),
				CODEMODULE_FILE_EXTENSION );
		
		return getCodeModulesPath().append( filename ).toFile();
	}
	
	public Collection<CodeModuleFile> getCodeModuleFiles() {

			File codeModulesPath = getCodeModulesPath().toFile();
	
		    FilenameFilter fileFilter = new FilenameFilter() {
				@Override
				public boolean accept(File parent, String name) {
					return name.endsWith( "." + CODEMODULE_FILE_EXTENSION );
				}
		    };
	
		    Collection<CodeModuleFile> codeModulefiles = new ArrayList<CodeModuleFile>();
		    
		    for ( File file : codeModulesPath.listFiles(fileFilter) ) {
		    	CodeModuleFile codeModuleFile = loadCodeModuleFile(file.getPath() );
		    	if ( codeModuleFile != null) {
		    		codeModulefiles.add( codeModuleFile );
		    	}
		    }
		
	    return codeModulefiles;
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
	
	private CodeModuleFile loadCodeModuleFile(XMLMemento memento, String filename) {
		CodeModuleFile codeModuleFile = createCodeModuleFile(memento);
		loadFiles(memento, codeModuleFile);
		loadJavaElements(memento, codeModuleFile);
		codeModuleFile.setFilename( filename );
		
		return codeModuleFile;
	}

	private CodeModuleFile createCodeModuleFile(XMLMemento memento) {

		String name = memento.getString( PluginTagNames.NAME );
		String id = memento.getString( PluginTagNames.ID );
		String connectionName = memento.getString( PluginTagNames.CONNECTION_NAME );
		String connectionDisplayName = memento.getString( PluginTagNames.CONNECTION_DISPLAY_NAME );
		String objectStoreName = memento.getString( PluginTagNames.OBJECT_STORE_NAME );
		String objectStoreDisplayName = memento.getString( PluginTagNames.OBJECT_STORE_DISPLAY_NAME );
		
		CodeModuleFile codeModuleFile = new CodeModuleFile(name, id, connectionName, connectionDisplayName, 
				objectStoreName, objectStoreDisplayName );
		return codeModuleFile;
	}

	private void loadFiles(XMLMemento memento, CodeModuleFile codeModuleFile) {
		
		IMemento filesChild = memento.getChild(PluginTagNames.FILES );
		if ( filesChild != null ) {
			for ( IMemento fileChild : filesChild.getChildren(PluginTagNames.FILE) ) {
				codeModuleFile.addFile( new File( fileChild.getString( PluginTagNames.NAME ) ) );
			}
		}
	}

	private void loadJavaElements(XMLMemento memento, CodeModuleFile codeModuleFile) {
		IMemento javaElementsChild = memento.getChild(PluginTagNames.JAVA_ELEMENTS );
		if ( javaElementsChild != null ) {
			for ( IMemento javaElementChild : javaElementsChild.getChildren(PluginTagNames.JAVE_ELEMENT) ) {
				String handleIdentifier = javaElementChild.getString( PluginTagNames.NAME );
				codeModuleFile.addJavaElement( JavaCore.create(handleIdentifier) );
			}
		}
	}
	
	public IPath getCodeModulesPath() {
		IPath parentFolder = Activator.getDefault().getStateLocation().append(CODEMODULES_FOLDER);
		
		if ( ! parentFolder.toFile().exists() )
		{
			parentFolder.toFile().mkdir();
		}
		return parentFolder;
	}
}
