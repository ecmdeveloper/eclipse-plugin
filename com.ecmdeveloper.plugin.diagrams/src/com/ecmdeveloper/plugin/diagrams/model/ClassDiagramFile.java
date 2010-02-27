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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

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

	private IFile classDiagramFile;

	public ClassDiagramFile() {
	}
	
	public ClassDiagramFile( IFile classDiagramFile ) {
		this.classDiagramFile = classDiagramFile;
	}

	public void save( ClassDiagram classDiagram, IProgressMonitor monitor ) throws IOException, CoreException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(out);
		
		XMLMemento memento = getXMLMemento(classDiagram);
		StringWriter writer = new StringWriter(); 
		memento.save(writer); 
		writer.close(); 

		dataOut.write( writer.toString().getBytes("UTF-8") );
		System.out.println( writer.toString() );
		classDiagramFile.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, monitor);
	}

	public ClassDiagram read() throws IOException, WorkbenchException {

		File file = classDiagramFile.getLocation().toFile();
		FileReader fileReader = new FileReader( file );
		
		XMLMemento memento = XMLMemento.createReadRoot( fileReader );
		
		return getClassDiagram(memento);
	}
	
	public ClassDiagram getClassDiagram(XMLMemento memento ) {
	
		ClassDiagram classDiagram = new ClassDiagram();

		IMemento classes = memento.getChild(PluginTagNames.CLASSES);
		
		if ( classes != null ) {
			getClassDiagramClasses(classDiagram, classes);
		}		
		return classDiagram;
	}

	private void getClassDiagramClasses(ClassDiagram classDiagram, IMemento classes) {
		
		for (IMemento clazz : classes.getChildren( PluginTagNames.CLASS ) ) {
			ClassDiagramClass classDiagramClass = getClassDiagramClass(clazz);
			classDiagram.addClassDiagramClass(classDiagramClass);
		}
	}

	private ClassDiagramClass getClassDiagramClass(IMemento clazz) {

		String name = clazz.getString( PluginTagNames.NAME );
		String displayName = clazz.getString( PluginTagNames.DISPLAY_NAME );
		boolean abstractClass = clazz.getBoolean( PluginTagNames.ABSTRACT_CLASS );
		String id = clazz.getString( PluginTagNames.ID );
		
		ClassDiagramClass classDiagramClass = new ClassDiagramClass(name, displayName, abstractClass, id );
		
		Point location = new Point(clazz.getInteger(PluginTagNames.XPOS), clazz
				.getInteger(PluginTagNames.YPOS));
		classDiagramClass.setLocation(location);

		Dimension size = new Dimension(
				clazz.getInteger(PluginTagNames.WIDTH), 
				clazz.getInteger(PluginTagNames.HEIGHT));
		classDiagramClass.setSize(size);

		
		getClassDiagramAttributes(clazz, classDiagramClass);
		
		return classDiagramClass;
	}

	private void getClassDiagramAttributes(IMemento clazz, ClassDiagramClass classDiagramClass) {

		IMemento attributesChild = clazz.getChild(PluginTagNames.ATTRIBUTES);
		
		for ( IMemento attributeChild : attributesChild.getChildren( PluginTagNames.ATTRIBUTE ) ) {
			ClassDiagramAttribute classDiagramAttribute = getClassDiagramAttribute(attributeChild);
			classDiagramClass.addAttribute(classDiagramAttribute);
		}
	}

	private ClassDiagramAttribute getClassDiagramAttribute(IMemento attributeChild) {
		
		String name = attributeChild.getString( PluginTagNames.NAME );
		String displayName = attributeChild.getString( PluginTagNames.DISPLAY_NAME );
		String type = attributeChild.getString( PluginTagNames.TYPE );
		String modifiers = attributeChild.getString( PluginTagNames.MODIFIERS );
		String multiplicity = attributeChild.getString( PluginTagNames.MULTIPLICITY );
		String defaultValue = attributeChild.getString( PluginTagNames.DEFAULT_VALUE );
		
		ClassDiagramAttribute classDiagramAttribute = new ClassDiagramAttribute(name, displayName, type, defaultValue, multiplicity,  modifiers );

		return classDiagramAttribute;
	}
	
	public XMLMemento getXMLMemento(ClassDiagram classDiagram ) {
		
		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.CLASSDIAGRAM);
		IMemento classesChild = memento.createChild(PluginTagNames.CLASSES); 
		
		for ( ClassDiagramClass classDiagramClass : classDiagram.getClassDiagramClasses() ) {
			
			IMemento classChild = classesChild.createChild(PluginTagNames.CLASS);
			initializeClassChild(classDiagramClass, classChild);
		}
		
		return memento;
	}

	private void initializeClassChild(ClassDiagramClass classDiagramClass, IMemento classChild) {

		classChild.putString( PluginTagNames.NAME, classDiagramClass.getName() );
		classChild.putString( PluginTagNames.DISPLAY_NAME, classDiagramClass.getDisplayName() );
		classChild.putBoolean( PluginTagNames.ABSTRACT_CLASS, classDiagramClass.isAbstractClass() );
		
		Point location = classDiagramClass.getLocation();
		classChild.putInteger( PluginTagNames.XPOS, location.x );
		classChild.putInteger( PluginTagNames.YPOS, location.y );
		
		Dimension size = classDiagramClass.getSize();
		classChild.putInteger( PluginTagNames.HEIGHT, size.height );
		classChild.putInteger( PluginTagNames.WIDTH, size.width );
		classChild.putString( PluginTagNames.ID, classDiagramClass.getId() );
		
		IMemento attributesChild = classChild.createChild(PluginTagNames.ATTRIBUTES);
		initializeAttributesChild(classDiagramClass, attributesChild);
	}

	private void initializeAttributesChild(ClassDiagramClass classDiagramClass,
			IMemento attributesChild) {

		for ( ClassDiagramAttribute attribute : classDiagramClass.getAttributes() )
		{
			IMemento attributeChild = attributesChild.createChild(PluginTagNames.ATTRIBUTE);
			initializeAttributeChild(attribute, attributeChild);
		}
	}

	private void initializeAttributeChild(ClassDiagramAttribute attribute, IMemento attributeChild) {
		attributeChild.putString(PluginTagNames.NAME, attribute.getName() );
		attributeChild.putString(PluginTagNames.DISPLAY_NAME, attribute.getDisplayName() );
		attributeChild.putString(PluginTagNames.TYPE, attribute.getType());
		attributeChild.putString(PluginTagNames.MODIFIERS, attribute.getModifiers() );
		attributeChild.putString(PluginTagNames.MULTIPLICITY, attribute.getMultiplicity() );
		attributeChild.putString(PluginTagNames.DEFAULT_VALUE, attribute.getDefaultValue() );
	}
}
