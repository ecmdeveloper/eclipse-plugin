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

import org.eclipse.core.runtime.IAdaptable;

import com.ecmdeveloper.plugin.classes.model.ClassesManager;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.classes.model.constants.PropertyType;
import com.ecmdeveloper.plugin.classes.model.task.GetRequiredClassDescription;
import com.ecmdeveloper.plugin.diagrams.util.PluginLog;
import com.filenet.api.constants.Cardinality;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescriptionObject;

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

	public ClassDiagramAttribute(IAdaptable adaptableObject) {

		PropertyDescription propertyDescription = (PropertyDescription) adaptableObject
				.getAdapter(PropertyDescription.class);
		com.filenet.api.meta.PropertyDescription internalPropertyDescription = (com.filenet.api.meta.PropertyDescription) adaptableObject
				.getAdapter(com.filenet.api.meta.PropertyDescription.class );
		
		this.multiplicity = MultiplicityFormatter.getMultiplicity( internalPropertyDescription );
		this.modifiers = getModifiers( internalPropertyDescription );
		this.name = internalPropertyDescription.get_SymbolicName();
		this.displayName = internalPropertyDescription.get_DisplayName();
		this.type = propertyDescription.getPropertyType().toString();
		this.defaultValue = getDefaultValue();
		
		if ( propertyDescription.getPropertyType().equals( PropertyType.OBJECT ) ) {
			PropertyDescriptionObject objectPropertyDescription = (PropertyDescriptionObject) internalPropertyDescription;
			try {
				// TODO: make this asynchronous?
				GetRequiredClassDescription task = new GetRequiredClassDescription( objectPropertyDescription );
				ClassesManager.getManager().executeTaskSync( task );
				com.filenet.api.meta.ClassDescription requiredClass = task.getRequiredClass();
				this.type = requiredClass.get_DisplayName();
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

	private String getModifiers(com.filenet.api.meta.PropertyDescription internalPropertyDescription) {
	
		StringBuffer modifiersText = new StringBuffer();
		String separator = "";

		if ( internalPropertyDescription.get_IsReadOnly() ) {
			modifiersText.append( separator );
			modifiersText.append( "readOnly" );
			separator = ",";
		}
		
		if ( Cardinality.LIST.equals( internalPropertyDescription.get_Cardinality() ) ) {
			modifiersText.append( separator );
			modifiersText.append( "ordered" );
			separator = ",";
		}
		
		if ( Cardinality.ENUM.equals( internalPropertyDescription.get_Cardinality() ) ) {
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
