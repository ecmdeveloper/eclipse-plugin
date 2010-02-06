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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.filenet.api.meta.PropertyDescriptionDateTime;
import com.filenet.api.meta.PropertyDescriptionFloat64;
import com.filenet.api.meta.PropertyDescriptionInteger32;
import com.filenet.api.meta.PropertyDescriptionString;


/**
 * @author Ricardo.Belfor
 *
 */
public class PropertyDescription implements IAdaptable {
	
	private static final String NOT_TEXT = "not ";
	private static final String PROPERTY_TYPE_TEXT = " This is a {0}required {1} property.";
	private static final String MAXIMUM_VALUE_TEXT = "the maximum value is ";
	private static final String MINIMUM_VALUE_TEXT = " The minimum value is ";
	private static final String AND_TEXT = " and ";
	private static final String UNSPECIFIED_TEXT = "unspecified";
	private static final String UNSPECIFIED_MINIMUM_AND_MAXIMUM_VALUES = " The minimum and maximum values are unspecified.";
	private static final String MAXIMUM_LENGTH_TEXT = " The maximum length is {0} characters.";
	private static final String UNSPECIFIED_MAXIMUM_LENGTH_TEXT = " The maximum length is unspecified.";
	private static final String DEFAULT_VALUE_TEXT = " The default value is \"{0}\".";
	
	private com.filenet.api.meta.PropertyDescription propertyDescription;
	private PropertyType propertyType;
	private String name;
	private String displayName;
	private ChoiceList choiceList;
	private ArrayList<Choice> choices;
	private boolean required;
	private boolean multivalue;
	private String descriptiveText;
	
	public PropertyDescription(Object internalPropertyDescription) {
		this.propertyDescription = (com.filenet.api.meta.PropertyDescription) internalPropertyDescription;
		name = propertyDescription.get_SymbolicName();
		displayName = propertyDescription.get_DisplayName();
		propertyType = PropertyType.fromTypeID(propertyDescription.get_DataType() );
		choiceList = propertyDescription.get_ChoiceList();
		required = propertyDescription.get_IsValueRequired();
		multivalue = !Cardinality.SINGLE.equals( propertyDescription.get_Cardinality() );
		initializeDescriptiveText();
	}

	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public String getDescriptiveText() {
		return descriptiveText;
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

	private void initializeDescriptiveText() {
		
		StringBuffer description = getGeneralPropertyDescription();
		
		if ( propertyDescription instanceof PropertyDescriptionString ) {
			PropertyDescriptionString stringPropertyDescription = (PropertyDescriptionString) propertyDescription;
			description.append( getStringPropertyDescription(stringPropertyDescription) );
		} else if ( propertyDescription instanceof PropertyDescriptionBoolean) {
			PropertyDescriptionBoolean booleanPropertyDescription =  (PropertyDescriptionBoolean) propertyDescription;
			description.append( getBooleanPropertyDescription(booleanPropertyDescription) );
		} else if ( propertyDescription instanceof PropertyDescriptionInteger32 ) {
			PropertyDescriptionInteger32 integerPropertyDescription = (PropertyDescriptionInteger32) propertyDescription;
			description.append( getIntegerPropertyDescription(integerPropertyDescription) );
		} else if ( propertyDescription instanceof PropertyDescriptionFloat64 ) {
			PropertyDescriptionFloat64 floatPropertyDescription = (PropertyDescriptionFloat64) propertyDescription;
			description.append( getFloatPropertyDescription(floatPropertyDescription) );
		} else if ( propertyDescription instanceof PropertyDescriptionDateTime ) {
			PropertyDescriptionDateTime dateTimePropertyDescription = (PropertyDescriptionDateTime) propertyDescription;
			description.append( getDateTimePropertyDescription(dateTimePropertyDescription) );
		}
		
		descriptiveText =  description.toString();
	}

	private StringBuffer getGeneralPropertyDescription() {
		
		StringBuffer description = new StringBuffer();
		description.append( propertyDescription.get_DescriptiveText() );
		if ( propertyDescription.get_DescriptiveText().trim().length() != 0 && 
				! propertyDescription.get_DescriptiveText().trim().endsWith(".") ) {
			description.append(".");
		}

		description.append(MessageFormat.format(PROPERTY_TYPE_TEXT,
				(required ? "" : NOT_TEXT), propertyType.toString() ));
		
		return description;
	}

	private String getDateTimePropertyDescription( PropertyDescriptionDateTime dateTimePropertyDescription) {
		StringBuffer description = new StringBuffer();
		
		if ( dateTimePropertyDescription.get_PropertyDefaultDateTime() != null ) {
			description.append(MessageFormat.format(DEFAULT_VALUE_TEXT,
					dateTimePropertyDescription.get_PropertyDefaultDateTime() ));
		}
	
		Date minimumValue = dateTimePropertyDescription.get_PropertyMinimumDateTime();
		Date maximumValue = dateTimePropertyDescription.get_PropertyMaximumDateTime();
		description.append( getMinimumAndMaximumDescription( minimumValue, maximumValue) );
		
		return description.toString();
	}

	private String getIntegerPropertyDescription(PropertyDescriptionInteger32 integerPropertyDescription) {
		
		StringBuffer description = new StringBuffer();
		
		if ( integerPropertyDescription.get_PropertyDefaultInteger32() != null ) {
			description.append(MessageFormat.format(DEFAULT_VALUE_TEXT,
					integerPropertyDescription.get_PropertyDefaultInteger32() ));
		}
	
		Integer minimumValue = integerPropertyDescription.get_PropertyMinimumInteger32();
		Integer maximumValue = integerPropertyDescription.get_PropertyMaximumInteger32();
		description.append( getMinimumAndMaximumDescription( minimumValue, maximumValue) );
		
		return description.toString();
	}

	private String getFloatPropertyDescription(PropertyDescriptionFloat64 floatPropertyDescription) {
		
		StringBuffer description = new StringBuffer();
		
		if ( floatPropertyDescription.get_PropertyDefaultFloat64() != null ) {
			description.append(MessageFormat.format(DEFAULT_VALUE_TEXT,
					floatPropertyDescription.get_PropertyDefaultFloat64() ));
		}
	
		Double minimumValue = floatPropertyDescription.get_PropertyMinimumFloat64();
		Double maximumValue = floatPropertyDescription.get_PropertyMaximumFloat64();
		description.append( getMinimumAndMaximumDescription( minimumValue, maximumValue) );
		
		return description.toString();
	}

	private String getMinimumAndMaximumDescription( Object minimumValue, Object maximumValue) {
		
		StringBuffer description = new StringBuffer();
		
		if (minimumValue == null
				&& maximumValue == null) {
			description.append( UNSPECIFIED_MINIMUM_AND_MAXIMUM_VALUES );
		}
		else {
			
			description.append( MINIMUM_VALUE_TEXT );
			
			if ( minimumValue != null ) {
				description.append( minimumValue );
			} else {
				description.append( UNSPECIFIED_TEXT );
			}
	
			description.append( AND_TEXT );
			description.append( MAXIMUM_VALUE_TEXT );
			if ( maximumValue != null ) {
				description.append( maximumValue );
			} else {
				description.append( UNSPECIFIED_TEXT );
			}
		}
		
		return description.toString();
	}

	private String getBooleanPropertyDescription(PropertyDescriptionBoolean booleanPropertyDescription) {
		StringBuffer description = new StringBuffer();
		if ( booleanPropertyDescription.get_PropertyDefaultBoolean() != null ) {
			description.append(MessageFormat.format(DEFAULT_VALUE_TEXT,
					booleanPropertyDescription.get_PropertyDefaultBoolean() ));
		}
		return description.toString();
	}

	private String getStringPropertyDescription(PropertyDescriptionString stringPropertyDescription) {
		
		StringBuffer description = new StringBuffer();
		
		if ( stringPropertyDescription.get_MaximumLengthString() != null ) {
			description.append(MessageFormat.format(MAXIMUM_LENGTH_TEXT,
					stringPropertyDescription.get_MaximumLengthString()));
		} else {
			description.append( UNSPECIFIED_MAXIMUM_LENGTH_TEXT );
		}
		
		if ( stringPropertyDescription.get_PropertyDefaultString() != null ) {
			description.append(MessageFormat.format(DEFAULT_VALUE_TEXT,
					stringPropertyDescription.get_PropertyDefaultString()));
		}
		
		return description.toString();
	}
}
