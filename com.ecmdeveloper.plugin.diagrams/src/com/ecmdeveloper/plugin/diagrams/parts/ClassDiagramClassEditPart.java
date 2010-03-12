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

package com.ecmdeveloper.plugin.diagrams.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

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
	private ArrayList<Label> attributeLabels = new ArrayList<Label>();
	private UMLClassFigure classFigure;
	
	public ClassDiagramClassEditPart(ClassDiagramClass classDiagramClass) {
		setModel(classDiagramClass);
	}

	public Rectangle getBounds() {
		return classFigure.getBounds();
	}
	public ClassDiagramClass getClassDiagramClass() {
		return (ClassDiagramClass) getModel();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelSourceConnections() {
		List sourceList = new ArrayList();
		sourceList.addAll( getClassDiagramClass().getChildRelations() );
		
		for ( AttributeRelationship sourceRelation : getClassDiagramClass().getSourceRelations() ) {
			if ( sourceRelation.isConnected() ) {
				sourceList.add(sourceRelation);
			}
		}

		return sourceList;
	}

	private void debugRelations(List<InheritRelationship> relations, String prefix) {
		for (InheritRelationship relation : relations) {
			System.out.println( prefix + relation.getParent().getName() + " is a parent of " + relation.getChild().getName());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List getModelTargetConnections() {
		List targetList = new ArrayList();
		if ( getClassDiagramClass().getParentRelation() != null ) {
			targetList.add( getClassDiagramClass().getParentRelation() );
		}
		debugRelations(targetList, "Target: ");
		targetList.addAll( getClassDiagramClass().getTargetRelations() );
		return targetList;
	}

	@Override
	protected IFigure createFigure() {
		ClassDiagramClass classDiagramClass = getClassDiagramClass();
		
		createClassLabel(classDiagramClass);
		updateClassLabelIcon(classDiagramClass);
		classFigure = new UMLClassFigure( classLabel );
		createAttributeLabels(classDiagramClass, classFigure);
		updateAttributeLabelIcons(classDiagramClass);

		return classFigure;
	}

	private void createAttributeLabels(ClassDiagramClass classDiagramClass,
			UMLClassFigure classFigure) {
		attributeLabels.clear();
		boolean showIcons = classDiagramClass.getParent().isShowIcons();
		for ( ClassDiagramAttribute attribute : classDiagramClass.getAttributes() ) {
			Label attributeLabel = new Label( attribute.toString().substring( showIcons ? 1 : 0) );
			attributeLabels.add(attributeLabel);
			classFigure.getAttributesCompartment().add( attributeLabel );
		}
	}

	private void createClassLabel(ClassDiagramClass classDiagramClass) {
		Font classFont = new Font(null, "Arial", 12, SWT.BOLD
				+ (classDiagramClass.isAbstractClass() ? SWT.ITALIC : 0));
		classLabel = new Label( classDiagramClass.getName() );
		classLabel.setFont(classFont);
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
				ClassDiagramClass classDiagramClass = getClassDiagramClass();
				updateClassLabelIcon( classDiagramClass );
				updateAttributeLabelIcons(classDiagramClass);
				refreshVisuals();
			}
		}
		super.propertyChange(evt);
	}

	private void updateClassLabelIcon(ClassDiagramClass classDiagramClass) {
		boolean showIcons = classDiagramClass.getParent().isShowIcons();
		if ( ! showIcons ) {
			classLabel.setIcon(null);
		} else {
			classLabel.setIcon( Activator.getImage( IconFiles.CLASS_ICON ) );
		}
	}
	
	private void updateAttributeLabelIcons(ClassDiagramClass classDiagramClass) {
		boolean showIcons = classDiagramClass.getParent().isShowIcons();
		for (Label attributeLabel : attributeLabels ) {
			if ( showIcons ) {
				attributeLabel.setIcon( Activator.getImage( IconFiles.PUBLIC_ATTRIBUTE_ICON ) );
			} else {
				attributeLabel.setIcon( null );
			}
		}
	}
}
