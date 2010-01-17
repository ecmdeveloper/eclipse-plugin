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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ricardo.Belfor
 *
 */
public class MultiValueBooleanDetailsPage extends BaseMultiValueDetailsPage {

	private Button trueButton;
	private Button falseButton;

	@Override
	protected void createInput(Composite client, FormToolkit toolkit) {
		trueButton = toolkit.createButton(client, "True", SWT.RADIO );
		falseButton = toolkit.createButton(client, "False", SWT.RADIO );
	}

	@Override
	protected Object getInputValue() {
		Boolean value = new Boolean( trueButton.getSelection() );
		return value;
	}

	@Override
	protected void setInputValue(Object value) {
		Boolean booleanValue = (Boolean) value;
		if ( booleanValue == null ) {
			trueButton.setSelection(false);
			falseButton.setSelection(false);
		} else if ( booleanValue ) {
			trueButton.setSelection(true);
			falseButton.setSelection(false);
		} else {
			trueButton.setSelection(false);
			falseButton.setSelection(true);
		}
	}
}
