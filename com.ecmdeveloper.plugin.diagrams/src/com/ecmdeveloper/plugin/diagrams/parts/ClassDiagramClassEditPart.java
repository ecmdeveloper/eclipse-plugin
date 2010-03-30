/**
 * Copyright 2009,2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import com.ecmdeveloper.plugin.diagrams.Activator;
import com.ecmdeveloper.plugin.diagrams.figures.UMLClassFigure;
import com.ecmdeveloper.plugin.diagrams.model.AttributeRelationship;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramAttribute;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramElement;
import com.ecmdeveloper.plugin.diagrams.model.InheritRelationship;
import com.ecmdeveloper.plugin.diagrams.util.IconFiles;

/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramClassEditPart extends ClassDiagramElementEditPart {

	private Label classLabel;
	private Map<String,Label> attributeLabelsMap = new HashMap<String, Label>(); 
	private UMLClassFigure classFigure;
	
	public ClassDiagramClassEditPart(ClassDiagramClass classDiagramClass) {
		setModel(classDiagramClass);
	}

	public ClassDiagramClass getClassDiagramClass() {
		return (ClassDiagramClass) getModel();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelSourceConnections() {
		List sourceList = new ArrayList();
		sourceList.addAll( getClassDiagramClass().getChildRelations() );
		
		//debugAttributeRelations( getClassDiagramClass().getSourceRelations(), "Source " );
		for ( AttributeRelationship sourceRelation : getClassDiagramClass().getSourceRelations() ) {
			if ( sourceRelation.isConnected() ) {
				sourceList.add(sourceRelation);
			}
		}

		return sourceList;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List getModelTargetConnections() {
		List targetList = new ArrayList();
		if (getClassDiagramClass().getParentRelation() != null) {
			targetList.add(getClassDiagramClass().getParentRelation());
		}
		// debugRelations(targetList, "Target: ");
		// debugAttributeRelations( getClassDiagramClass().getTargetRelations(), "Target " );
		targetList.addAll(getClassDiagramClass().getTargetRelations());
		return targetList;
	}

	@SuppressWarnings("unused")
	private void debugRelations(List<InheritRelationship> relations, String prefix) {
		for (InheritRelationship relation : relations) {
			System.out.println( prefix + relation.getParent().getName() + " is a parent of " + relation.getChild().getName());
		}
	}

	@SuppressWarnings("unused")
	private void debugAttributeRelations(List<AttributeRelationship> relations, String prefix) {
		for (AttributeRelationship relation : relations) {
			System.out.println( prefix + relation.toString());
		}
	}
	
	@Override
	protected IFigure createFigure() {
		ClassDiagramClass classDiagramClass = getClassDiagramClass();
		
		createClassLabel(classDiagramClass);
		updateClassLabelIcon(classDiagramClass);
		classFigure = new UMLClassFigure( classLabel );
		createAttributeLabels();
		return classFigure;
	}

	private void createClassLabel(ClassDiagramClass classDiagramClass) {
		Font classFont = new Font(null, "Arial", 12, SWT.BOLD
				+ (classDiagramClass.isAbstractClass() ? SWT.ITALIC : 0));
		classLabel = new Label( getClassLabel(classDiagramClass) );
		classLabel.setFont(classFont);
	}

	private String getClassLabel(ClassDiagramClass classDiagramClass) {
		boolean showDisplayNames = classDiagramClass.getParent().isShowDisplayNames();
		String name = showDisplayNames? classDiagramClass.getDisplayName() : classDiagramClass.getName();
		name = " " + name + " ";
		return name;
	}

	private void createAttributeLabels() {
		ClassDiagramClass classDiagramClass = getClassDiagramClass();
		attributeLabelsMap.clear();
		boolean showIcons = classDiagramClass.getParent().isShowIcons();
		boolean hasLabels = false;
		for ( ClassDiagramAttribute attribute : classDiagramClass.getAttributes() ) {
			if ( attribute.isVisible() && attribute.isActive() ) {
				Label attributeLabel = createAttributeLabel(attribute, showIcons);
				attributeLabelsMap.put(attribute.getName(), attributeLabel);
				hasLabels = true;
			}
		}
		
		classFigure.getAttributesCompartment().setVisible( hasLabels );
	}

	private Label createAttributeLabel(ClassDiagramAttribute attribute, boolean showIcons) {
		Label attributeLabel = new Label( attribute.toString().substring( showIcons ? 1 : 0) );
		classFigure.getAttributesCompartment().add( attributeLabel );
		if ( showIcons ) {
			attributeLabel.setIcon( Activator.getImage( IconFiles.PUBLIC_ATTRIBUTE_ICON ) );
		} else {
			attributeLabel.setIcon( null );
		}
		return attributeLabel;
	}

	private void updateClassLabelIcon(ClassDiagramClass classDiagramClass) {
		boolean showIcons = classDiagramClass.getParent().isShowIcons();
		if ( ! showIcons ) {
			classLabel.setIcon(null);
		} else {
			classLabel.setIcon( Activator.getImage( IconFiles.CLASS_ICON ) );
		}
	}

	private void updateAttributeLabels() {
		ClassDiagramClass classDiagramClass = getClassDiagramClass();
		boolean showIcons = classDiagramClass.getParent().isShowIcons();
		boolean showDisplayNames = classDiagramClass.getParent().isShowDisplayNames();
	
		for ( ClassDiagramAttribute attribute : classDiagramClass.getAttributes() ) {
			if ( attribute.isVisible() && attribute.isActive() ) {
				updateAttributeLabel(attribute, showIcons, showDisplayNames );
			}
		}
	}

	private void updateAttributeLabel(ClassDiagramAttribute attribute, boolean showIcons, boolean showDisplayName ) {
		Label attributeLabel = attributeLabelsMap.get( attribute.getName() );
		String text = attribute.getUMLString(showDisplayName, ! showIcons, true, true, true );
		attributeLabel.setText( text );
		if ( showIcons ) {
			attributeLabel.setIcon( Activator.getImage( IconFiles.PUBLIC_ATTRIBUTE_ICON ) );
		} else {
			attributeLabel.setIcon( null );
		}
	}

	@Override
	protected void refreshVisuals() {
		Point location = getClassDiagramClass().getLocation();
		Rectangle bounds = new Rectangle(location.x, location.y, -1, -1 );

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), bounds);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if ( ClassDiagramElement.CLASS_DIAGRAM_SETTINGS_CHANGED_PROP.equals(propertyName) ) {
			if ( evt.getNewValue().equals( ClassDiagram.SHOW_ICONS_PROP ) ) {
				refreshIcons();
			} else if ( evt.getNewValue().equals( ClassDiagram.SHOW_DISPLAY_NAMES_PROP ) ) {
				refreshLabels();
			}
		} else if ( ClassDiagramClass.VISIBLE_ATTRIBUTE_PROP.equals( propertyName) ) {
			classFigure.getAttributesCompartment().removeAll();
			createAttributeLabels();
		}
		super.propertyChange(evt);
	}

	private void refreshLabels() {
		ClassDiagramClass classDiagramClass = getClassDiagramClass();
		classLabel.setText( getClassLabel(classDiagramClass) );
		updateAttributeLabels();
		refreshVisuals();
	}

	private void refreshIcons() {
		ClassDiagramClass classDiagramClass = getClassDiagramClass();
		updateClassLabelIcon( classDiagramClass );
		updateAttributeLabels();
		refreshVisuals();
	}
}
