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

package com.ecmdeveloper.plugin.properties.input;

import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.properties.renderer.StringInputRenderer;
import com.filenet.api.meta.PropertyDescriptionString;

/**
 * @author Ricardo.Belfor
 *
 */
public class StringPropertyInput extends PropertyInputBase {

	private StringInputRenderer inputRenderer;
	
	public StringPropertyInput(PropertyDescriptionString propertyDescription) {
		super(propertyDescription);
	
		String displayName = propertyDescription.get_DisplayName();
		String descriptiveText = propertyDescription.get_DescriptiveText();
		if ( propertyDescription.get_ChoiceList() != null ) {
			descriptiveText += " (selectable)";
		}
		Integer maximumLengthString = propertyDescription.get_MaximumLengthString();
		inputRenderer = new StringInputRenderer(displayName, descriptiveText, maximumLengthString );
		
	}

	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.properties.input.IPropertyInput#renderControls(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	public void renderControls(Composite container, int numColumns) {
		inputRenderer.renderControls(container, numColumns);
	}

	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.properties.input.IPropertyInput#getValue()
	 */
	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(Object value) {
		String valueAsString = PropertyValueConversion.getValueAsString(value);
		inputRenderer.setValue( valueAsString );
	}
}
