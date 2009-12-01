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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;


/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramClass extends ClassDiagramElement {

	private String name;
	private String displayName;
	private boolean abstractClass;
	private String id;
	
	private ArrayList<ClassDiagramAttribute> attributes = new ArrayList<ClassDiagramAttribute>();

	public ClassDiagramClass(String name, String displayName,
			boolean abstractClass, String id) {
		super();
		this.name = name;
		this.displayName = displayName;
		this.abstractClass = abstractClass;
		this.id = id;
	}

	public ClassDiagramClass(IAdaptable adaptableObject) {
		
		com.filenet.api.meta.ClassDescription internalClassDescription = (com.filenet.api.meta.ClassDescription) adaptableObject
				.getAdapter(com.filenet.api.meta.ClassDescription.class);
		
		abstractClass = ! internalClassDescription.get_AllowsInstances();
		id = internalClassDescription.get_Id().toString();
		displayName = internalClassDescription.get_DisplayName();
		name = internalClassDescription.get_Name();
		
		ClassDescription classDescription = (ClassDescription) adaptableObject.getAdapter(ClassDescription.class);
		for ( PropertyDescription propertyDescription : classDescription.getPropertyDescriptions() ) {
			 ClassDiagramAttribute attribute = (ClassDiagramAttribute) propertyDescription.getAdapter(ClassDiagramAttribute.class);
			 addAttribute(attribute);
		 }
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getId() {
		return id;
	}

	public boolean isAbstractClass() {
		return abstractClass;
	}
	
	public void addAttribute(ClassDiagramAttribute attribute ) {
		attributes.add(attribute);
	}
	
	public List<ClassDiagramAttribute> getAttributes() {
		return attributes;
	}
}
