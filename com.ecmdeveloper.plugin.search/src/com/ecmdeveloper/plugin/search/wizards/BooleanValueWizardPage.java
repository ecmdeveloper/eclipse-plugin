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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class BooleanValueWizardPage extends ValueWizardPage {

	private static final String TITLE = "Boolean value";
	private static final String DESCRIPTION = "Select a boolean value.";
	
	private Button trueButton;
	private Button falseButton;
	
	protected BooleanValueWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	protected void createInput(Composite container) {
		createTrueButton(container);
		createFalseButton(container);
	}

	private void createTrueButton(Composite client) {
		trueButton = new Button(client, SWT.RADIO);
		trueButton.setLayoutData( getFullRowGridData() );
		trueButton.setText("True");
		Object value = getValue();
		if ( value != null && value instanceof Boolean ) {
			trueButton.setSelection( (Boolean) value );
		}
		trueButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setValue( Boolean.TRUE );
				setDirty();
			}
		} );
	}

	private void createFalseButton(Composite client) {
		
		falseButton = new Button(client, SWT.RADIO);
		falseButton.setText("False");
		falseButton.setLayoutData( getFullRowGridData() );
		Object value = getValue();
		if ( value != null && value instanceof Boolean ) {
			falseButton.setSelection( !(Boolean) value );
		}
		falseButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setValue( Boolean.FALSE );
				setDirty();
			}
		} );
	}
}
