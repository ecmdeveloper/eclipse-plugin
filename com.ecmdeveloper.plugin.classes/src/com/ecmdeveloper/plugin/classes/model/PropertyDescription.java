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

package com.ecmdeveloper.plugin.classes.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

import com.ecmdeveloper.plugin.classes.model.constants.PropertyType;
import com.ecmdeveloper.plugin.classes.model.task.GetChoiceValuesTask;
import com.ecmdeveloper.plugin.classes.util.PluginLog;
import com.filenet.api.admin.ChoiceList;
import com.filenet.api.constants.Cardinality;
import com.filenet.api.core.Factory;
import com.filenet.api.meta.PropertyDescriptionBoolean;
import com.filenet.api.meta.PropertyDescriptionInteger32;
import com.filenet.api.meta.PropertyDescriptionString;


/**
 * @author Ricardo.Belfor
 *
 */
public class PropertyDescription implements IAdaptable {
	
	private com.filenet.api.meta.PropertyDescription propertyDescription;
	private PropertyType propertyType;
	private String name;
	private ChoiceList choiceList;
	private ArrayList<Choice> choices;
	private boolean required;
	private boolean multivalue;
	
	public PropertyDescription(Object internalPropertyDescription) {
		this.propertyDescription = (com.filenet.api.meta.PropertyDescription) internalPropertyDescription;
		name = this.propertyDescription.get_SymbolicName();
		propertyType = PropertyType.fromTypeID(this.propertyDescription.get_DataType() );
		choiceList = propertyDescription.get_ChoiceList();
		required = propertyDescription.get_IsValueRequired();
		multivalue = !Cardinality.SINGLE.equals( propertyDescription.get_Cardinality() );
		
		StringBuffer description = new StringBuffer();
		description.append( propertyDescription.get_DescriptiveText() ); 
		description.append("This is " + (required? "": "not ") + "a required property.");

		if ( internalPropertyDescription instanceof PropertyDescriptionString ) {
			PropertyDescriptionString stringPropertyDescription = (PropertyDescriptionString) internalPropertyDescription;

			if ( stringPropertyDescription.get_MaximumLengthString() != null ) {
				description.append( " The maximum length is " + stringPropertyDescription.get_MaximumLengthString() + " characters." );
				description.append( " The maximum length is unspecified." );
			}
			if ( stringPropertyDescription.get_PropertyDefaultString() != null ) {
				description.append( " The default value is '" + stringPropertyDescription.get_PropertyDefaultString() + "'" );
			}
		} else if ( internalPropertyDescription instanceof PropertyDescriptionBoolean) {
			PropertyDescriptionBoolean booleanPropertyDescription =  (PropertyDescriptionBoolean) internalPropertyDescription;
		} else if ( internalPropertyDescription instanceof PropertyDescriptionInteger32 ) {
			PropertyDescriptionInteger32 booleanPropertyDescription = (PropertyDescriptionInteger32) internalPropertyDescription;
		}
	}

	public String getName() {
		return name;
	}
	
	public PropertyType getPropertyType() {
		return propertyType;
	}
	
	public boolean isRequired() {
		return required;
	}

	public boolean isMultivalue() {
		return multivalue;
	}

	protected void setMultivalue(boolean multivalue) {
		this.multivalue = multivalue;
	}

	public String getType() {
		// TODO look for the real type in case of an object type
		return propertyType.toString();
	}

	public boolean hasChoices() {
		return choiceList != null;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Choice> getChoices() {
		if ( choiceList == null ) {
			return null;
		}
		
		if ( choices == null ) {
			GetChoiceValuesTask task = new GetChoiceValuesTask(choiceList);
			try {
				choices = (ArrayList<Choice>) ClassesManager.getManager().executeTaskSync(task);
			} catch (Exception e) {
				PluginLog.error(e);
			}
		}
		return choices;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {

		if ( adapter.isInstance(propertyDescription) ) {
			return propertyDescription;
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
