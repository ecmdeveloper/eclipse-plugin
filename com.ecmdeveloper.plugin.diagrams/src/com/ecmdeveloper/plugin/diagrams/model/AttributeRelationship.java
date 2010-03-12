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

package com.ecmdeveloper.plugin.diagrams.model;

import com.ecmdeveloper.plugin.classes.model.ClassesManager;
import com.ecmdeveloper.plugin.classes.model.task.GetRequiredClassDescription;
import com.filenet.api.meta.PropertyDescriptionObject;

/**
 * @author Ricardo.Belfor
 *
 */
public class AttributeRelationship {

	private String name;
	private boolean connected;
	private ClassConnector sourceConnector;
	private ClassConnector targetConnector;
	
	public AttributeRelationship(PropertyDescriptionObject objectPropertyDescription, ClassDiagramClass parent ) throws Exception {

		GetRequiredClassDescription task = new GetRequiredClassDescription( objectPropertyDescription );
		com.filenet.api.meta.ClassDescription requiredClass = (com.filenet.api.meta.ClassDescription) ClassesManager.getManager().executeTaskSync( task );

		name = objectPropertyDescription.get_DisplayName();
		initializeTargetConnector(objectPropertyDescription, requiredClass);
		initalizeSourceConnector(objectPropertyDescription, parent);
	}

	private void initializeTargetConnector(PropertyDescriptionObject objectPropertyDescription,
			com.filenet.api.meta.ClassDescription requiredClass) {
		
		targetConnector = new ClassConnector();
		targetConnector.setClassId( requiredClass.get_Id().toString() );
		targetConnector.setClassName( requiredClass.get_Name() );
		targetConnector.setMultiplicity( MultiplicityFormatter.getMultiplicity( objectPropertyDescription ) );

		if ( objectPropertyDescription.get_ReflectivePropertyId() != null ) {
			targetConnector.setPropertyId( objectPropertyDescription.get_ReflectivePropertyId().toString() );
		}
	}

	public ClassConnector getSourceConnector() {
		return sourceConnector;
	}

	public ClassConnector getTargetConnector() {
		return targetConnector;
	}

	private void initalizeSourceConnector(PropertyDescriptionObject objectPropertyDescription,
			ClassDiagramClass parent) {

		sourceConnector = new ClassConnector();
		sourceConnector.setClassId( parent.getId() );
		sourceConnector.setClassName( parent.getName() );
		sourceConnector.setPropertyId( objectPropertyDescription.get_Id().toString() );
		sourceConnector.setPropertyName( objectPropertyDescription.get_Name() );
		sourceConnector.setMultiplicity( "1" ); // TODO
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

//	private ClassDiagramClass source;
//	private ClassDiagramClass target;
//	
//	public AttributeRelationship(ClassDiagramClass source, ClassDiagramClass target) {
//		this.source = source;
//		this.target = target;
//	}
//
//	public ClassDiagramClass getSource() {
//		return source;
//	}
//
//	public ClassDiagramClass getTarget() {
//		return target;
//	}
	
	
}
