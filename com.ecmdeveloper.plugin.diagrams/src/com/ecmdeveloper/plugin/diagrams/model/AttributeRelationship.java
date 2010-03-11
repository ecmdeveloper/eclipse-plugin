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

	private String targetClassId;
	private String targetClassName;
	private String name;
	private String targetMultiplicity;
	private boolean connected;
	private String targetPropertyId;

	public AttributeRelationship(PropertyDescriptionObject objectPropertyDescription) throws Exception {

		GetRequiredClassDescription task = new GetRequiredClassDescription( objectPropertyDescription );
		com.filenet.api.meta.ClassDescription requiredClass = (com.filenet.api.meta.ClassDescription) ClassesManager.getManager().executeTaskSync( task );
		
		targetClassId = requiredClass.get_Id().toString();
		targetClassName = requiredClass.get_Name();
		name = objectPropertyDescription.get_DisplayName();
		targetMultiplicity = MultiplicityFormatter.getMultiplicity( objectPropertyDescription );
		if ( objectPropertyDescription.get_ReflectivePropertyId() != null ) {
			targetPropertyId = objectPropertyDescription.get_ReflectivePropertyId().toString();
		} else {
			targetPropertyId = null; 
		}
	}

	public String getRequiredClassId() {
		return targetClassId;
	}

	public void setRequiredClassId(String requiredClassId) {
		this.targetClassId = requiredClassId;
	}

	public String getRequiredClassName() {
		return targetClassName;
	}

	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTargetMultiplicity() {
		return targetMultiplicity;
	}

	public void setTargetMultiplicity(String targetMultiplicity) {
		this.targetMultiplicity = targetMultiplicity;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public String getReflectivePropertyId() {
		return targetPropertyId;
	}

	public void setReflectivePropertyId(String reflectivePropertyId) {
		this.targetPropertyId = reflectivePropertyId;
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
