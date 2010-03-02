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
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import com.ecmdeveloper.plugin.diagrams.figures.UMLClassFigure;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramAttribute;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;
import com.ecmdeveloper.plugin.diagrams.model.InheritRelationship;
import com.ecmdeveloper.plugin.diagrams.policies.ClassDiagramComponentEditPolicy;

/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramClassEditPart extends AbstractClassesGraphicalEditPart
		implements PropertyChangeListener {

	public ClassDiagramClassEditPart(ClassDiagramClass classDiagramClass) {
		setModel(classDiagramClass);
	}

	public ClassDiagramClass getClassDiagramClass() {
		return (ClassDiagramClass) getModel();
	}
	
	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			getClassDiagramClass().addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			getClassDiagramClass().removePropertyChangeListener(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List getModelSourceConnections() {
		List<InheritRelationship> relations = getClassDiagramClass().getChildRelations();
		debugRelations(relations, "Source: ");
		return relations;
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
		return targetList;
	}

	@Override
	protected IFigure createFigure() {
		ClassDiagramClass classDiagramClass = getClassDiagramClass();
		Font classFont = new Font(null, "Arial", 12, SWT.BOLD
				+ (classDiagramClass.isAbstractClass() ? SWT.ITALIC : 0));
		Label classLabel1 = new Label( classDiagramClass.getName() );
		classLabel1.setFont(classFont);
		
		UMLClassFigure classFigure = new UMLClassFigure( classLabel1 );
	
		for ( ClassDiagramAttribute attribute : classDiagramClass.getAttributes() ) {
			Label attributeLabel = new Label( attribute.toString() );
			classFigure.getAttributesCompartment().add( attributeLabel );
		}
		return classFigure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new ClassDiagramComponentEditPolicy() );
	}

	@Override
	protected void refreshVisuals() {
//		Rectangle bounds = new Rectangle(getClassDiagramClass().getLocation(),
//				getClassDiagramClass().getSize());
		Point location = getClassDiagramClass().getLocation();
		Rectangle bounds = new Rectangle(location.x, location.y, -1, -1 );

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), bounds);
		
		System.out.println( "refreshVisuals called " + bounds.toString() );
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (ClassDiagramClass.SIZE_PROP.equals(propertyName)
				|| ClassDiagramClass.LOCATION_PROP.equals(propertyName)) {
			refreshVisuals();
		} else if ( ClassDiagramClass.SOURCE_CONNECTIONS_PROP.equals( propertyName) ) {
			refreshSourceConnections();
			refreshVisuals();
		} else if ( ClassDiagramClass.TARGET_CONNECTIONS_PROP.equals( propertyName) ) {
			refreshTargetConnections();
			refreshVisuals();
		}
	}	
}
