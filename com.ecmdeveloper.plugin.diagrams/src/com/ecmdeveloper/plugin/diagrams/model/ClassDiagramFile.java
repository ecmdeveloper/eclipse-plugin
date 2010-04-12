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

	public static final int CURRENT_FILE_VERSION = 1;

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

		dataOut.write( writer.toString().getBytes() );
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
		
		boolean showIcons = memento.getBoolean( PluginTagNames.SHOW_ICONS );
		classDiagram.setShowIcons(showIcons);
		
		boolean showDisplayNames = memento.getBoolean( PluginTagNames.SHOW_DISPLAY_NAMES );
		classDiagram.setShowDisplayNames(showDisplayNames );
		
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
		
		String id = noteChild.getString( PluginTagNames.ID );
		classDiagramNote.setId(id);
		
		return classDiagramNote;
	}

	private void getClassDiagramClasses(ClassDiagram classDiagram, IMemento classes) {
		
		for (IMemento clazz : classes.getChildren( PluginTagNames.CLASS ) ) {
			ClassDiagramClass classDiagramClass = getClassDiagramClass(clazz);
			classDiagram.addClassDiagramClass(classDiagramClass);
		}
	}

	private ClassDiagramClass getClassDiagramClass(IMemento classChild) {

		String name = classChild.getString( PluginTagNames.NAME );
		String displayName = classChild.getString( PluginTagNames.DISPLAY_NAME );
		boolean abstractClass = classChild.getBoolean( PluginTagNames.ABSTRACT_CLASS );
		String id = classChild.getString( PluginTagNames.ID );
		String parentClassId = classChild.getString( PluginTagNames.PARENT_CLASS_ID );
		String connectionName = classChild.getString( PluginTagNames.CONNECTION_NAME );
		String connectionDisplayName = classChild.getString( PluginTagNames.CONNECTION_DISPLAY_NAME );
		String objectStoreName = classChild.getString( PluginTagNames.OBJECT_STORE_NAME );
		String objectStoreDisplayName = classChild.getString( PluginTagNames.OBJECT_STORE_DISPLAY_NAME );

		ClassDiagramClass classDiagramClass = new ClassDiagramClass(name, displayName,
				abstractClass, id, parentClassId, connectionName, connectionDisplayName,
				objectStoreName, objectStoreDisplayName);
		
		getLocation(classChild, classDiagramClass);
		
		getClassDiagramAttributes(classChild, classDiagramClass);
		getClassDiagramAttributesRelationships(classChild, classDiagramClass);
		
		boolean parentVisible = classChild.getBoolean( PluginTagNames.PARENT_VISIBLE );
		classDiagramClass.setParentVisible(parentVisible);
		
		return classDiagramClass;
	}

	private void getClassDiagramAttributesRelationships(IMemento clazz,
			ClassDiagramClass classDiagramClass) {
		
		IMemento attributeRelationshipsChild = clazz.getChild(PluginTagNames.ATTRIBUTE_RELATIONSHIPS);
		
		if ( attributeRelationshipsChild == null ) {
			return;
		}
		for ( IMemento attributeRelationshipChild : attributeRelationshipsChild.getChildren( PluginTagNames.ATTRIBUTE_RELATIONSHIP ) ) {

			AttributeRelationship attributeRelationship = getClassDiagramAttributeRelationship(attributeRelationshipChild);
			classDiagramClass.addSource( attributeRelationship );
		}
		
	}

	private AttributeRelationship getClassDiagramAttributeRelationship( IMemento attributeRelationshipChild) {
		
		String name = attributeRelationshipChild.getString( PluginTagNames.NAME );
		ClassConnector sourceConnector = getClassConnector(attributeRelationshipChild.getChild( PluginTagNames.SOURCE_CONNECTOR) );
		ClassConnector targetConnector = getClassConnector(attributeRelationshipChild.getChild( PluginTagNames.TARGET_CONNECTOR) );

		AttributeRelationship attributeRelationship = new AttributeRelationship(name, sourceConnector, targetConnector );
		
		boolean visible = attributeRelationshipChild.getBoolean( PluginTagNames.VISIBLE );
		attributeRelationship.setVisible(visible);

		return attributeRelationship;
	}

	private ClassConnector getClassConnector(IMemento classConnectorChild) {
		String classId = classConnectorChild.getString(PluginTagNames.CLASS_ID );
		String className = classConnectorChild.getString(PluginTagNames.CLASS_NAME );
		String multiplicity = classConnectorChild.getString(PluginTagNames.MULTIPLICITY);
		String propertyId = classConnectorChild.getString(PluginTagNames.PROPERTY_ID );
		String propertyName = classConnectorChild.getString(PluginTagNames.PROPERTY_NAME  );
		ClassConnector classConnector = new ClassConnector(classId, className, propertyId, propertyName, multiplicity );

		boolean aggregate = classConnectorChild.getBoolean( PluginTagNames.AGGREGATE );
		classConnector.setAggregate(aggregate);
		
		return classConnector;
	}

	private void getSize(IMemento elementChild, ClassDiagramElementWithResize classDiagramElement) {
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

		boolean visible = attributeChild.getBoolean( PluginTagNames.VISIBLE );
		classDiagramAttribute.setVisible(visible);
		
		return classDiagramAttribute;
	}
	
	public XMLMemento getXMLMemento(ClassDiagram classDiagram ) {
		
		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.CLASSDIAGRAM);
		memento.putInteger(PluginTagNames.VERSION_TAG, CURRENT_FILE_VERSION );
		memento.putBoolean(PluginTagNames.SHOW_ICONS , classDiagram.isShowIcons() );
		memento.putBoolean(PluginTagNames.SHOW_DISPLAY_NAMES, classDiagram.isShowDisplayNames() );
		
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
		noteChild.putString( PluginTagNames.ID, classDiagramNote.getId() );
		addLocation(classDiagramNote, noteChild);
		addSize(classDiagramNote, noteChild);
	}

	private void initializeClassChild(ClassDiagramClass classDiagramClass, IMemento classChild) {

		classChild.putString( PluginTagNames.NAME, classDiagramClass.getName() );
		classChild.putString( PluginTagNames.DISPLAY_NAME, classDiagramClass.getDisplayName() );
		classChild.putBoolean( PluginTagNames.ABSTRACT_CLASS, classDiagramClass.isAbstractClass() );
		classChild.putString( PluginTagNames.CONNECTION_NAME, classDiagramClass.getConnectionName() );
		classChild.putString( PluginTagNames.CONNECTION_DISPLAY_NAME, classDiagramClass.getConnectionDisplayName() );
		classChild.putString( PluginTagNames.OBJECT_STORE_NAME, classDiagramClass.getObjectStoreName() );
		classChild.putString( PluginTagNames.OBJECT_STORE_DISPLAY_NAME, classDiagramClass.getObjectStoreDisplayName() );
		
		addLocation(classDiagramClass, classChild);

		classChild.putString( PluginTagNames.ID, classDiagramClass.getId() );
		classChild.putString( PluginTagNames.PARENT_CLASS_ID, classDiagramClass.getParentClassId() );
		classChild.putBoolean( PluginTagNames.PARENT_VISIBLE, classDiagramClass.isParentVisible() );
		
		IMemento attributesChild = classChild.createChild(PluginTagNames.ATTRIBUTES);
		initializeAttributesChild(classDiagramClass, attributesChild);
		
		IMemento attributeRelationshipsChild = classChild.createChild(PluginTagNames.ATTRIBUTE_RELATIONSHIPS );
		initializeAttributeRelationshipsChild(classDiagramClass, attributeRelationshipsChild);
	}

	private void initializeAttributeRelationshipsChild(ClassDiagramClass classDiagramClass,
			IMemento attributeRelationshipsChild) {

		for ( AttributeRelationship attributeRelationship : classDiagramClass.getSourceRelations() ) {
			IMemento attributeRelationshipChild = attributeRelationshipsChild.createChild(PluginTagNames.ATTRIBUTE_RELATIONSHIP );
			initializeAttributeRelationshipChild(attributeRelationshipChild, attributeRelationship);
		}
	}

	private void initializeAttributeRelationshipChild(IMemento attributeRelationshipChild,
			AttributeRelationship attributeRelationship) {

		attributeRelationshipChild.putString( PluginTagNames.NAME, attributeRelationship.getName() );
		attributeRelationshipChild.putBoolean(PluginTagNames.VISIBLE, attributeRelationship.isVisible() );

		IMemento sourceConnectorChild = attributeRelationshipChild.createChild( PluginTagNames.SOURCE_CONNECTOR );
		initializeClassConnectorChild(sourceConnectorChild, attributeRelationship.getSourceConnector() );
		
		IMemento targetConnectorChild  = attributeRelationshipChild.createChild( PluginTagNames.TARGET_CONNECTOR );
		initializeClassConnectorChild(targetConnectorChild, attributeRelationship.getTargetConnector() );
	}

	private void initializeClassConnectorChild(IMemento classConnectorChild,
			ClassConnector classConnector) {
		classConnectorChild.putString(PluginTagNames.CLASS_ID, classConnector.getClassId() );
		classConnectorChild.putString(PluginTagNames.CLASS_NAME, classConnector.getClassName() );
		classConnectorChild.putString(PluginTagNames.MULTIPLICITY, classConnector.getMultiplicity() );
		classConnectorChild.putString(PluginTagNames.PROPERTY_ID, classConnector.getPropertyId() );
		classConnectorChild.putString(PluginTagNames.PROPERTY_NAME, classConnector.getPropertyName() );
		classConnectorChild.putBoolean(PluginTagNames.AGGREGATE, classConnector.isAggregate() );
	}

	private void addSize(ClassDiagramElementWithResize classDiagramElement, IMemento elementChild) {
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
		attributeChild.putBoolean(PluginTagNames.VISIBLE, attribute.isVisible() );
	}
}
