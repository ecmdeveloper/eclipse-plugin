/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.search.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.search.model.IQueryField;

/**
 * @author ricardo.belfor
 *
 */
public class BooleanMultiValueWizardPage extends MultiValueWizardPage {

	private Button trueButton;
	private Button falseButton;

	public BooleanMultiValueWizardPage(IQueryField queryField) {
		super(queryField);
		setDescription("Enter Boolean Values" );
	}

	@Override
	protected void createValuesControls(Composite parent) {
		createTrueButton(parent);
		createFalseButton(parent);
	}
	
	private void createTrueButton(Composite client) {
		trueButton = new Button(client, SWT.RADIO);
		trueButton.setText("True");
	}

	private void createFalseButton(Composite client) {
		falseButton = new Button(client, SWT.RADIO);
		falseButton.setText("False");
	}

	@Override
	protected Object getEditorValue() {
		if ( trueButton.getSelection() ) {
			return true;
		} else if ( falseButton.getSelection() ) {
			return false;
		}
		return null;
	}

	@Override
	protected void setEditorValue(Object value) {
		trueButton.setSelection( (Boolean)value );
		falseButton.setSelection( !(Boolean)value );
	}

	@Override
	protected void clearEditorValue() {
		trueButton.setSelection(false);
		falseButton.setSelection(false);
	}
}
