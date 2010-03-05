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

		IMemento notes = memento.getChild(PluginTagNames.NOTES);
		
		if ( notes != null ) {
			getClassDiagramNotes(classDiagram, notes);
		}		
		return classDiagram;
	}

	private void getClassDiagramNotes(ClassDiagram classDiagram, IMemento notes) {

		for (IMemento noteChild : notes.getChildren( PluginTagNames.NOTE ) ) {
			ClassDiagramNote classDiagramNote = getClassDiagramNote(noteChild);
			classDiagram.addClassDiagramNote(classDiagramNote);
		}
	}

	private ClassDiagramNote getClassDiagramNote(IMemento noteChild) {
		ClassDiagramNote classDiagramNote = new ClassDiagramNote();
		getLocation(noteChild, classDiagramNote);
		getSize(noteChild, classDiagramNote);
		String text = noteChild.getString( PluginTagNames.TEXT );
		classDiagramNote.setNoteText(text);
		
		return classDiagramNote;
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
		String parentClassId = clazz.getString( PluginTagNames.PARENT_CLASS_ID );
		ClassDiagramClass classDiagramClass = new ClassDiagramClass(name, displayName, abstractClass, id, parentClassId );
		
		getLocation(clazz, classDiagramClass);
		getSize(clazz, classDiagramClass);
		
		getClassDiagramAttributes(clazz, classDiagramClass);
		
		return classDiagramClass;
	}

	private void getSize(IMemento elementChild, ClassDiagramElement classDiagramElement) {
		Dimension size = new Dimension(elementChild.getInteger(PluginTagNames.WIDTH), elementChild
				.getInteger(PluginTagNames.HEIGHT));
		classDiagramElement.setSize(size);
	}

	private void getLocation(IMemento elementChild, ClassDiagramElement classDiagramElement) {
		Point location = new Point(elementChild.getInteger(PluginTagNames.XPOS), elementChild
				.getInteger(PluginTagNames.YPOS));
		classDiagramElement.setLocation(location);
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
		
		IMemento notesChild = memento.createChild(PluginTagNames.NOTES); 

		for ( ClassDiagramNote classDiagramNote : classDiagram.getClassDiagramNotes() ) {
			
			IMemento noteChild = notesChild.createChild(PluginTagNames.NOTE);
			initializeNoteChild(classDiagramNote, noteChild);
		}
		
		return memento;
	}

	private void initializeNoteChild(ClassDiagramNote classDiagramNote, IMemento noteChild) {
		noteChild.putString( PluginTagNames.TEXT, classDiagramNote.getNoteText() );
		addLocation(classDiagramNote, noteChild);
		addSize(classDiagramNote, noteChild);
	}

	private void initializeClassChild(ClassDiagramClass classDiagramClass, IMemento classChild) {

		classChild.putString( PluginTagNames.NAME, classDiagramClass.getName() );
		classChild.putString( PluginTagNames.DISPLAY_NAME, classDiagramClass.getDisplayName() );
		classChild.putBoolean( PluginTagNames.ABSTRACT_CLASS, classDiagramClass.isAbstractClass() );
		
		addLocation(classDiagramClass, classChild);
		addSize(classDiagramClass, classChild);

		classChild.putString( PluginTagNames.ID, classDiagramClass.getId() );
		classChild.putString( PluginTagNames.PARENT_CLASS_ID, classDiagramClass.getParentClassId() );
		
		IMemento attributesChild = classChild.createChild(PluginTagNames.ATTRIBUTES);
		initializeAttributesChild(classDiagramClass, attributesChild);
	}

	private void addSize(ClassDiagramElement classDiagramElement, IMemento elementChild) {
		Dimension size = classDiagramElement.getSize();
		elementChild.putInteger( PluginTagNames.HEIGHT, size.height );
		elementChild.putInteger( PluginTagNames.WIDTH, size.width );
	}

	private void addLocation(ClassDiagramElement classDiagramElement, IMemento elementChild) {
		Point location = classDiagramElement.getLocation();
		elementChild.putInteger( PluginTagNames.XPOS, location.x );
		elementChild.putInteger( PluginTagNames.YPOS, location.y );
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
