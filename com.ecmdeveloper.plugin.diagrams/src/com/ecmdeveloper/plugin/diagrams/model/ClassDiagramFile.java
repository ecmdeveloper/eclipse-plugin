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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.diagrams.util.PluginTagNames;

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

		dataOut.write( writer.toString().getBytes("UTF-8") );
		
		file.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, monitor);
	}

	public ClassDiagram read() throws IOException, WorkbenchException {

//		InputStream contents = file.getContents();
//		file.get
//		byte[] buffer = new byte[100];
//		int bytesRead;
//		
//		StringBuffer stringBuffer = new StringBuffer();
//		while ( ( bytesRead = contents.read(buffer) ) > 0 ) {
//			stringBuffer.append(buffer, );
//		}
//		
//	    DataInputStream in = new DataInputStream(new ByteArrayInputStream(stringBuffer.toString().getBytes())); 
//
//	    String xmlString = in.readUTF();
//	    if (xmlString == null || xmlString.length() == 0) {
//			return null;
//		}
//
//		StringReader reader = new StringReader(xmlString);
		
//		Reader reader = InputStreamReader) file.getContents() );
//		XMLMemento memento = XMLMemento.createReadRoot(reader);

		File file2 = file.getLocation().toFile();
		FileReader fileReader = new FileReader( file2 );
		
		XMLMemento memento = XMLMemento.createReadRoot( fileReader );
		
		return getClassDiagram(memento);
	}
	
	public ClassDiagram getClassDiagram(XMLMemento memento ) {
	
		ClassDiagram classDiagram = new ClassDiagram();

		IMemento classes = memento.getChild(PluginTagNames.CLASSES);
		
		if ( classes != null ) {
			
			for (IMemento clazz : classes.getChildren( PluginTagNames.CLASS ) ) {
				
				String name = clazz.getString( PluginTagNames.NAME );
				ClassDiagramClass classDiagramClass = new ClassDiagramClass(name);
				
				Point location = new Point(clazz.getInteger(PluginTagNames.XPOS),
						clazz.getInteger(PluginTagNames.YPOS));
				classDiagramClass.setLocation(location);
				
				Dimension size = new Dimension(
						clazz.getInteger(PluginTagNames.WIDTH), 
						clazz.getInteger(PluginTagNames.HEIGHT));
				classDiagramClass.setSize(size);
				
				classDiagram.addClassDiagramClass(classDiagramClass);
			}
		}		
		return classDiagram;
	}
	
	public XMLMemento getXMLMemento(ClassDiagram classDiagram ) {
		
		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.CLASSDIAGRAM);
		IMemento classesChild = memento.createChild(PluginTagNames.CLASSES); 
		
		for ( ClassDiagramClass classDiagramClass : classDiagram.getClassDiagramClasses() ) {
			
			IMemento classChild = classesChild.createChild(PluginTagNames.CLASS);
			classChild.putString( PluginTagNames.NAME, classDiagramClass.getName() );
			
			Point location = classDiagramClass.getLocation();
			classChild.putInteger( PluginTagNames.XPOS, location.x );
			classChild.putInteger( PluginTagNames.YPOS, location.y );
			
			Dimension size = classDiagramClass.getSize();
			classChild.putInteger( PluginTagNames.HEIGHT, size.height );
			classChild.putInteger( PluginTagNames.WIDTH, size.width );
		}
		
		return memento;
	}
}
