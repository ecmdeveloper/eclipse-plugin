/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramFile {

	private IFile file;

	public ClassDiagramFile() {
	}
	
	public ClassDiagramFile( IFile file ) {
		this.file = file;
	}

	public void save( ClassDiagram classDiagram, IProgressMonitor monitor ) throws IOException, CoreException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(out);
		
		XMLMemento memento = getXMLMemento(classDiagram);
		StringWriter writer = new StringWriter(); 
		memento.save(writer); 
		writer.close(); 

		dataOut.writeUTF( writer.toString() );
		
		file.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, monitor);
	}
	
	public XMLMemento getXMLMemento(ClassDiagram classDiagram ) {
		
		XMLMemento memento = XMLMemento.createWriteRoot("classdiagram");
		IMemento classesChild = memento.createChild("classes"); 
		
		for ( ClassDiagramClass classDiagramClass : classDiagram.getClassDiagramClasses() ) {
			
			IMemento classChild = classesChild.createChild("class");
			classChild.putString( "name", classDiagramClass.getName() );
			
			Point location = classDiagramClass.getLocation();
			classChild.putInteger( "xPos", location.x );
			classChild.putInteger( "yPos", location.y );
			
			Dimension size = classDiagramClass.getSize();
			classChild.putInteger( "height", size.height );
			classChild.putInteger( "width", size.width );
		}
		
		return memento;
	}
	
//	private void saveToFile(CodeModuleFile codeModuleFile, boolean saveNew ) {
//
//		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.CODE_MODULE);
//		boolean saved = false;
//		saveCodeModuleFile(codeModuleFile, memento);
//		FileWriter writer = null;
//		try {
//			File outputFile;
//			if ( codeModuleFile.getFilename() != null) {
//				outputFile = new File( codeModuleFile.getFilename() );
//			}
//			else
//			{
//				outputFile = getCodeModuleFile(codeModuleFile);
//			}
//			writer = new FileWriter( outputFile );
//			memento.save(writer);
//			saved = true;
//		} catch (IOException e) {
//			PluginLog.error(e);
//		} finally {
//			try {
//				if (writer != null)
//					writer.close();
//			} catch (IOException e) {
//				PluginLog.error(e);
//			}
//		}
//		
//		if ( saved) {
//			if ( saveNew ) {
//				fireCodeModuleFilesChanged(new CodeModuleFile[] { codeModuleFile}, null, null );
//			} else {
//				fireCodeModuleFilesChanged(null, null, new CodeModuleFile[] { codeModuleFile} );
//			}
//		}
//	}
	
}
