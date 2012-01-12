/**
 * Copyright 2009,2010, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
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
import com.ecmdeveloper.plugin.core.model.constants.PropertyType;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetRequiredClassDescriptionTask;
import com.ecmdeveloper.plugin.diagrams.Activator;
import com.ecmdeveloper.plugin.diagrams.util.PluginLog;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramAttribute {
	
	private String name;
	private String type;
	private Object defaultValue;
	private String multiplicity;
	private String modifiers;
	private String displayName;
	private boolean visible;
	private boolean active;
	
	public ClassDiagramAttribute(String name, String displayName, String type,
			String defaultValue, String multiplicity, String modifiers) {

		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.defaultValue = defaultValue;
		this.multiplicity = multiplicity;
		this.modifiers = modifiers;
		visible = true;
		active = true;
	}

	public ClassDiagramAttribute(IPropertyDescription propertyDescription, IObjectStore objectStore) {

		this.multiplicity = MultiplicityFormatter.getMultiplicity( propertyDescription );
		this.modifiers = getModifiers( propertyDescription );
		this.name = propertyDescription.getName();
		this.displayName = propertyDescription.getDisplayName();
		this.type = propertyDescription.getPropertyType().toString();
		this.defaultValue = getDefaultValue();
		
		if ( propertyDescription.getPropertyType().equals( PropertyType.OBJECT ) ) {
			try {
				// TODO: make this asynchronous?
				ITaskFactory taskFactory = objectStore.getTaskFactory();
				IGetRequiredClassDescriptionTask task = taskFactory.getGetRequiredClassDescription(
						propertyDescription, objectStore );
				Activator.getDefault().getTaskManager().executeTaskSync(task);
				IClassDescription requiredClass = task.getRequiredClass();
				this.type = requiredClass.getName();
			} catch (Exception e) {
				PluginLog.error(e);
			}
		}
		
		visible = true;
		active = true;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getMultiplicity() {
		return multiplicity;
	}

	public String getModifiers() {
		return modifiers;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDefaultValue() {
		if ( defaultValue != null ) {
			return defaultValue.toString();
		}
		return "";
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return getUMLString(false, true, true, true, true );
	}

	private String getModifiers(IPropertyDescription propertyDescription) {
	
		StringBuffer modifiersText = new StringBuffer();
		String separator = "";

		if ( propertyDescription.isReadOnly() ) {
			modifiersText.append( separator );
			modifiersText.append( "readOnly" );
			separator = ",";
		}
		
		if ( propertyDescription.isList() ) {
			modifiersText.append( separator );
			modifiersText.append( "ordered" );
			separator = ",";
		}
		
		if ( propertyDescription.isEnum() ) {
			modifiersText.append( separator );
			modifiersText.append( "unique" );
			separator = ",";
		}
		
		return modifiersText.toString();
	}

	/**
	 * Returns the property description as an UML attribute. Attributes are
	 * formatted according to the UML standard:
	 * 
	 * <property> ::= [<visibility>] [‘/’] <name> [‘:’ <prop-type>] [‘[‘
	 * <multiplicity> ‘]’] [‘=’ <default>][‘{‘ <prop-modifier > [‘,’
	 * <prop-modifier >]* ’}’]
	 */
	public String getUMLString(boolean showDisplayName, boolean showVisibility, boolean showType, boolean showDefaultValue, boolean showModifiers ) {

		StringBuffer attributeText = new StringBuffer();
		
		if ( showVisibility ) {
			attributeText.append("+ ");
		}
		
		if ( showDisplayName ) {
			attributeText.append(displayName );
		} else {
			attributeText.append( name );
		}
		
		if ( showType ) {
		
			attributeText.append( " : ");
			attributeText.append( type );
			if ( ! multiplicity.isEmpty() ) {
				attributeText.append( '[' );
				attributeText.append( multiplicity );
				attributeText.append( ']' );
			}
			if ( showDefaultValue ) {
				if ( defaultValue != null && defaultValue.toString().length() != 0 ) {
					attributeText.append( " = ");
					attributeText.append( defaultValue );
				}
			}
		}		

		if ( showModifiers ) {
			if ( ! modifiers.isEmpty() ) {
				attributeText.append( " {" );
				attributeText.append( modifiers );
				attributeText.append( "}" );
			}
		}
		return attributeText.toString();
	}
}
