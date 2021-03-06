/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.properties.editors.details;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.properties.editors.details.input.ChoiceFormInput;
import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class MultiChoiceDetailsPage extends BaseMultiValueDetailsPage {

	private ChoiceFormInput choiceFormInput;

	@Override
	protected void createInput(Composite client, FormToolkit toolkit) {
		choiceFormInput = new ChoiceFormInput(client,toolkit);
	}

	@Override
	protected Object getInputValue() {
		return choiceFormInput.getValue();
	}

	@Override
	protected void setInputValue(Object value) {
		choiceFormInput.setValue(value);
	}

	@Override
	protected void propertyChanged(Property property) {
		super.propertyChanged(property);
		choiceFormInput.setProperty(property);
	}
}
