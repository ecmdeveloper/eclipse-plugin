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

import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.properties.renderer.BooleanInputRenderer;

/**
 * @author Ricardo.Belfor
 *
 */
public class BooleanPropertyInput extends PropertyInputBase {

	private BooleanInputRenderer inputRenderer;
	
	public BooleanPropertyInput(IPropertyDescription propertyDescription) {
		super(propertyDescription);

		String displayName = propertyDescription.getDisplayName();
		String descriptiveText = propertyDescription.getDescriptiveText();
//		propertyDescription.
		inputRenderer = new BooleanInputRenderer(displayName, descriptiveText );
	}

	@Override
	public void renderControls(Composite container, int numColumns) {
		inputRenderer.renderControls(container, numColumns);
	}

	@Override
	public void setValue(Object value) {

		if ( value == null || value instanceof Boolean ) {
			inputRenderer.setValue((Boolean) value);
		} else {
			throw new UnsupportedOperationException("Class '" + value.getClass().getName()
					+ "' is not a Boolean");
		}
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}
}
