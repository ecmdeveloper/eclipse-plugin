/**
 * Copyright 2010,2012, Ricardo Belfor
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

import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetRequiredClassDescriptionTask;
import com.ecmdeveloper.plugin.diagrams.Activator;

/**
 * @author Ricardo.Belfor
 *
 */
public class AttributeRelationship extends ClassDiagramBase {

	public static String VISIBLE_PROPERTY = "AttributeRelationship.Visible";

	private String name;
	private boolean visible;
	private boolean connected = false;
	private ClassConnector sourceConnector;
	private ClassConnector targetConnector;
	private boolean active;
	
	public AttributeRelationship(IPropertyDescription targetPropertyDescription, ClassDiagramClass parent, IObjectStore objectStore ) throws Exception {

		ITaskFactory taskFactory = objectStore.getTaskFactory();
		IGetRequiredClassDescriptionTask task = taskFactory.getGetRequiredClassDescription( targetPropertyDescription, objectStore );
		Activator.getDefault().getTaskManager().executeTaskSync( task );
		IClassDescription requiredClass = task.getRequiredClass(); 
		IPropertyDescription sourcePropertyDescription = task.getReflectivePropertyDescription();
		name = targetPropertyDescription.getDisplayName();
		initializeTargetConnector(targetPropertyDescription, sourcePropertyDescription, requiredClass);
		initalizeSourceConnector(targetPropertyDescription, sourcePropertyDescription, parent);
		visible = true;
		active = true;
	}

	public AttributeRelationship(String name, ClassConnector sourceConnector, ClassConnector targetConnector) {
		this.name = name;
		this.sourceConnector = sourceConnector;
		this.targetConnector = targetConnector;
		active = true;
	}

	private void initializeTargetConnector(IPropertyDescription targetPropertyDescription,
			IPropertyDescription sourcePropertyDescription,
			IClassDescription requiredClass) {
		
		targetConnector = new ClassConnector();
		targetConnector.setClassId( requiredClass.getId() );
		targetConnector.setClassName( requiredClass.getName() );
		targetConnector.setMultiplicity( MultiplicityFormatter.getMultiplicity( targetPropertyDescription ) );

		if ( sourcePropertyDescription != null ) {
			targetConnector.setPropertyId( sourcePropertyDescription.getId() );
			targetConnector.setPropertyName( sourcePropertyDescription.getName() );
		}
	}

	public ClassConnector getSourceConnector() {
		return sourceConnector;
	}

	public ClassConnector getTargetConnector() {
		return targetConnector;
	}

	private void initalizeSourceConnector(IPropertyDescription targetPropertyDescription,
			IPropertyDescription sourcePropertyDescription,
			ClassDiagramClass parent) {

		sourceConnector = new ClassConnector();
		sourceConnector.setClassId( parent.getId() );
		sourceConnector.setClassName( parent.getName() );
		sourceConnector.setPropertyId( targetPropertyDescription.getId() );
		sourceConnector.setPropertyName( targetPropertyDescription.getName() );
		if ( sourcePropertyDescription != null ) {
			sourceConnector.setMultiplicity( MultiplicityFormatter.getMultiplicity( sourcePropertyDescription ) );
		}

		boolean cascadeDelete = targetPropertyDescription.isCascadeDelete();
		sourceConnector.setAggregate( cascadeDelete );
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
		firePropertyChange(VISIBLE_PROPERTY, null, new Boolean( visible ) );
	}
	
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public boolean isLoop() {
		return getSourceConnector().getClassId().equals( getTargetConnector().getClassId() );
		
	}

	@Override
	public String toString() {
		return getSourceConnector().toString() + " --> " + getTargetConnector().toString();
	}
}
