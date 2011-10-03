package com.ecmdeveloper.plugin.properties.input;

import com.ecmdeveloper.plugin.core.model.IPropertyDescription;

public abstract class PropertyInputBase implements IPropertyInput {

	protected IPropertyDescription propertyDescription;
	
	public PropertyInputBase( IPropertyDescription propertyDescription ) {
		this.propertyDescription = propertyDescription;
	}
}
