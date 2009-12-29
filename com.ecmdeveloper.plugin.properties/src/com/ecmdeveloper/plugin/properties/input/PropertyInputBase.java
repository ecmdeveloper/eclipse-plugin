package com.ecmdeveloper.plugin.properties.input;

import com.filenet.api.meta.PropertyDescription;

public abstract class PropertyInputBase implements IPropertyInput {

	protected PropertyDescription propertyDescription;
	
	public PropertyInputBase( PropertyDescription propertyDescription ) {
		this.propertyDescription = propertyDescription;
	}
}
