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

package com.ecmdeveloper.plugin.cmis.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.chemistry.opencmis.commons.definitions.Choice;
import org.apache.chemistry.opencmis.commons.definitions.PropertyBooleanDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDateTimeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDecimalDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyIntegerDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyStringDefinition;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

import com.ecmdeveloper.plugin.cmis.model.tasks.GetChoiceValuesTask;
import com.ecmdeveloper.plugin.core.model.ChoicePlaceholder;
import com.ecmdeveloper.plugin.core.model.ClassesManager;
import com.ecmdeveloper.plugin.core.model.IChoice;
import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.core.model.constants.PropertyType;
import com.ecmdeveloper.plugin.core.model.tasks.TaskCompleteEvent;
import com.ecmdeveloper.plugin.core.model.tasks.TaskListener;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetChoiceValuesTask;


/**
 * @author Ricardo.Belfor
 *
 */
public class PropertyDescription implements IAdaptable, TaskListener, IPropertyDescription {
	
	private static final String READ_ONLY_TEXT = " The value is read only.";
	private static final String SETTABLE_ON_CHECKIN_TEXT = " The value is only settable on checkin.";
	private static final String SETTABLE_ON_CREATE_TEXT = " The value is only settable on create.";
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
	
	private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);
	private PropertyDefinition<?> propertyDescription;
	private PropertyType propertyType;
	private final String name;
	private final String displayName;
	private List<?> choiceList;
	private ArrayList<IChoice> choices;
	private final boolean required;
	private final boolean multivalue;
	private String descriptiveText;
	private boolean readOnly;
	private boolean settableOnCreate;
	private boolean settableOnCheckIn;
	private boolean settableOnEdit;
	private final boolean orderable;
	private Boolean containable;
	private final Boolean systemOwned;
	private final ObjectStore objectStore;
	private final boolean cbrEnabled;
	private final boolean hidden;
	private final boolean searchable;
	private final boolean selectable;
	
	public PropertyDescription(PropertyDefinition<?> internalPropertyDescription, ObjectStore objectStore ) {
		
		this.objectStore = objectStore;
		this.propertyDescription = internalPropertyDescription;
		name = propertyDescription.getQueryName();
		displayName = propertyDescription.getDisplayName() != null ? propertyDescription
				.getDisplayName() : name;
		propertyType = fromTypeID(propertyDescription.getPropertyType() );
		choiceList = propertyDescription.getChoices();
		required = propertyDescription.isRequired();
		multivalue = !propertyDescription.getCardinality().equals( org.apache.chemistry.opencmis.commons.enums.Cardinality.SINGLE );
		systemOwned = Updatability.READONLY.equals( propertyDescription.getUpdatability() );
		orderable = propertyDescription.isOrderable();
		cbrEnabled = initializeCBREnabled();
		hidden = false;
		searchable = propertyDescription.isQueryable();
		selectable = true; // TODO propertyDescription.get_IsSelectable();
		
		containable = null;
			
		initializeSettability();
		initializeDescriptiveText();
	}

	public static PropertyType fromTypeID(org.apache.chemistry.opencmis.commons.enums.PropertyType typeId )
	{
		switch ( typeId ) {
		case STRING: return PropertyType.STRING;
		case INTEGER: return PropertyType.LONG;
		case BOOLEAN: return PropertyType.BOOLEAN;
		case DATETIME: return PropertyType.DATE;
		case HTML: return PropertyType.UNKNOWN;
		case DECIMAL: return PropertyType.DOUBLE;
		case ID: return PropertyType.GUID;
		case URI: return PropertyType.UNKNOWN;
		default: return PropertyType.UNKNOWN;
		}
	}
	
	private Boolean initializeCBREnabled() {
		// TODO
		return false;
	}

	private void initializeSettability() {
		Updatability settability = propertyDescription.getUpdatability();
		readOnly = settability.equals( Updatability.READONLY );
		settableOnEdit = settability.equals( Updatability.READWRITE );
		settableOnCheckIn = settability.equals( Updatability.WHENCHECKEDOUT );
		settableOnCreate = settability.equals( Updatability.ONCREATE );
	}

	public ObjectStore getObjectStore() {
		return objectStore;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDescriptiveText() {
		return descriptiveText;
	}

	@Override
	public PropertyType getPropertyType() {
		return propertyType;
	}
	
	@Override
	public boolean isRequired() {
		return required;
	}

	@Override
	public boolean isMultivalue() {
		return multivalue;
	}

	@Override
	public String getType() {
		// TODO look for the real type in case of an object type
		return propertyType.toString();
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public boolean isSettableOnCreate() {
		return settableOnCreate;
	}

	@Override
	public boolean isSettableOnCheckIn() {
		return settableOnCheckIn;
	}

	@Override
	public boolean isSettableOnEdit() {
		return settableOnEdit;
	}

	@Override
	public Boolean getSystemOwned() {
		return systemOwned;
	}

	@Override
	public boolean isOrderable() {
		return orderable;
	}

	@Override
	public boolean isContainable() {
		if (containable == null) {
			containable = initializeContainable();
		}
		return containable;
	}

	@Override
	public boolean isCBREnabled() {
		return cbrEnabled;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public boolean isSelectable() {
		return selectable;
	}

	@Override
	public boolean isSearchable() {
		return searchable;
	}

	private boolean initializeContainable() {
//		if ( propertyType.equals(PropertyType.OBJECT) ) {
//			try {
//				// TODO: make this asynchronous?
//				PropertyDescriptionObject objectPropertyDescription = (PropertyDescriptionObject) propertyDescription;
//				CheckContainableClass task = new CheckContainableClass(objectPropertyDescription,
//						objectStore);
//				ClassesManager.getManager().executeTaskSync( task );
//				return task.isContainable();
//			} catch (Exception e) {
//				PluginLog.error(e);
//				return false;
//			}
//		}
		return false;
	}

//	private com.filenet.api.meta.ClassDescription getRequiredClassDescription(
//		Object internalPropertyDescription, ObjectStore objectStore) {
//	com.filenet.api.meta.ClassDescription requiredClass = null;
//	PropertyDescriptionObject objectPropertyDescription = (PropertyDescriptionObject) internalPropertyDescription;
//	return requiredClass;
//}

	@Override
	public boolean hasChoices() {
		return choiceList != null && !choiceList.isEmpty();
	}
	
	@Override
	public Collection<IChoice> getChoices() {
		if ( choiceList == null || choiceList.isEmpty() ) {
			return null;
		}
		
		if ( choices == null ) {

			choices = new ArrayList<IChoice>();
			choices.add( new ChoicePlaceholder() );

			IGetChoiceValuesTask task = new GetChoiceValuesTask(choiceList, objectStore);
			task.addTaskListener(this);
			ClassesManager.getManager().executeTaskASync(task);
		}
		return choices;
	}

	@Override
	public void onTaskComplete(TaskCompleteEvent taskCompleteEvent) {
		if ( taskCompleteEvent.getSource().getClass().equals(GetChoiceValuesTask.class) ) {
			GetChoiceValuesTask task = (GetChoiceValuesTask) taskCompleteEvent.getSource();
			choices = task.getChoices();
			firePropertyChange("Choices", null, null);
		}
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
		
		if ( propertyDescription instanceof PropertyStringDefinition ) {
			PropertyStringDefinition stringPropertyDescription = (PropertyStringDefinition) propertyDescription;
			description.append( getStringPropertyDescription(stringPropertyDescription) );
		} else if ( propertyDescription instanceof PropertyBooleanDefinition) {
			PropertyBooleanDefinition booleanPropertyDescription =  (PropertyBooleanDefinition) propertyDescription;
			description.append( getBooleanPropertyDescription(booleanPropertyDescription) );
		} else if ( propertyDescription instanceof PropertyIntegerDefinition ) {
			PropertyIntegerDefinition integerPropertyDescription = (PropertyIntegerDefinition) propertyDescription;
			description.append( getIntegerPropertyDescription(integerPropertyDescription) );
		} else if ( propertyDescription instanceof PropertyDecimalDefinition ) {
			PropertyDecimalDefinition floatPropertyDescription = (PropertyDecimalDefinition) propertyDescription;
			description.append( getFloatPropertyDescription(floatPropertyDescription) );
		} else if ( propertyDescription instanceof PropertyDateTimeDefinition ) {
			PropertyDateTimeDefinition dateTimePropertyDescription = (PropertyDateTimeDefinition) propertyDescription;
			description.append( getDateTimePropertyDescription(dateTimePropertyDescription) );
		}
		
		descriptiveText =  description.toString();
	}

	private StringBuffer getGeneralPropertyDescription() {
		
		StringBuffer description = new StringBuffer();
		String description2 = propertyDescription.getDescription();
		if ( description2 != null) {
			description.append( description2 );
			if ( propertyDescription.getDescription().trim().length() != 0 && 
					! propertyDescription.getDescription().trim().endsWith(".") ) {
				description.append(".");
			}
		}
		
		description.append(MessageFormat.format(PROPERTY_TYPE_TEXT,
				(required ? "" : NOT_TEXT), propertyType.toString() ));

		if ( isSettableOnCreate() ) {
			description.append( SETTABLE_ON_CREATE_TEXT );
		} else if ( isSettableOnCheckIn() ) {
			description.append( SETTABLE_ON_CHECKIN_TEXT );
		} else if ( isReadOnly() ) {
			description.append( READ_ONLY_TEXT );
		}
		return description;
	}

	private String getDateTimePropertyDescription( PropertyDateTimeDefinition dateTimePropertyDescription) {
		StringBuffer description = new StringBuffer();
		
		if ( dateTimePropertyDescription.getDefaultValue() != null ) {
			description.append(MessageFormat.format(DEFAULT_VALUE_TEXT,
					dateTimePropertyDescription.getDefaultValue() ));
		}
	
		// TODO dateTimePropertyDescription.getDateTimeResolution();
		
		return description.toString();
	}

	private String getIntegerPropertyDescription(PropertyIntegerDefinition integerPropertyDescription) {
		
		StringBuffer description = new StringBuffer();
		
		if ( integerPropertyDescription.getDefaultValue() != null ) {
			description.append(MessageFormat.format(DEFAULT_VALUE_TEXT,
					integerPropertyDescription.getDefaultValue() ));
		}
	
		BigInteger minimumValue = integerPropertyDescription.getMinValue();
		BigInteger maximumValue = integerPropertyDescription.getMaxValue();
		description.append( getMinimumAndMaximumDescription( minimumValue, maximumValue) );
		
		return description.toString();
	}

	private String getFloatPropertyDescription(PropertyDecimalDefinition floatPropertyDescription) {
		
		StringBuffer description = new StringBuffer();
		
		if ( floatPropertyDescription.getDefaultValue() != null ) {
			description.append(MessageFormat.format(DEFAULT_VALUE_TEXT,
					floatPropertyDescription.getDefaultValue() ));
		}
	
		BigDecimal minimumValue = floatPropertyDescription.getMinValue();
		BigDecimal maximumValue = floatPropertyDescription.getMaxValue();
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

	private String getBooleanPropertyDescription(PropertyBooleanDefinition booleanPropertyDescription) {
		StringBuffer description = new StringBuffer();
		if ( booleanPropertyDescription.getDefaultValue() != null ) {
			description.append(MessageFormat.format(DEFAULT_VALUE_TEXT,
					booleanPropertyDescription.getDefaultValue() ));
		}
		return description.toString();
	}

	private String getStringPropertyDescription(PropertyStringDefinition stringPropertyDescription) {
		
		StringBuffer description = new StringBuffer();
		
		if ( stringPropertyDescription.getMaxLength() != null ) {
			description.append(MessageFormat.format(MAXIMUM_LENGTH_TEXT,
					stringPropertyDescription.getMaxLength( ) ));
		} else {
			description.append( UNSPECIFIED_MAXIMUM_LENGTH_TEXT );
		}
		
		if ( stringPropertyDescription.getDefaultValue() != null ) {
			description.append(MessageFormat.format(DEFAULT_VALUE_TEXT,
					stringPropertyDescription.getDefaultValue()));
		}
		
		return description.toString();
	}

	@Override
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		pcsDelegate.addPropertyChangeListener(listener);
	}

	@Override
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listener != null) {
			pcsDelegate.removePropertyChangeListener(listener);
		}
	}

	protected void firePropertyChange(String property, Object oldValue, Object newValue) {
		if (pcsDelegate.hasListeners(property)) {
			pcsDelegate.firePropertyChange(property, oldValue, newValue);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getList() {
		
		switch ( propertyType ) {
		case BOOLEAN:
			return new ArrayList<Boolean>();
		case DATE:
			return new ArrayList<Date>();
		case DOUBLE:
			return new ArrayList<Double>();
		case LONG:
			return new ArrayList<Integer>();
		case STRING:
			return new ArrayList<String>();
		case GUID:
			return new ArrayList<String>();
//		case OBJECT:
//			// TODO: fix this!!!
//			return null;
//		case BINARY:
//			return Factory.BinaryList.createList();
		}
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCascadeDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnum() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isList() {
		// TODO Auto-generated method stub
		return false;
	}
}
