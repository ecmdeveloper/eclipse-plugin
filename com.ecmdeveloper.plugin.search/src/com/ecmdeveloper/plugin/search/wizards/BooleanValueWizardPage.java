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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class BooleanValueWizardPage extends ValueWizardPage {

	private static final String TITLE = "Double value";
	private static final String DESCRIPTION = "Enter a double value.";
	
	private Button trueButton;
	private Button falseButton;
	
	protected BooleanValueWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	protected void createInput(Composite container) {
		
		// TODO Auto-generated method stub
		
	}

//	private void createTrueButton(Composite client, FormToolkit toolkit) {
//		trueButton = toolkit.createButton(client, "True", SWT.RADIO );
//		trueButton.addSelectionListener( new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				setDirty(true);
//			}
//		} );
//	}
//
//	private void createFalseButton(Composite client, FormToolkit toolkit) {
//		falseButton = toolkit.createButton(client, "False", SWT.RADIO );
//		falseButton.addSelectionListener( new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				setDirty(true);
//			}
//		} );
//	}
//
//	private Button createButton(Composite container, String value, boolean selection) {
//
//		Button button = new Button(container, SWT.RADIO);
//		button.setText(value);
////		button.setLayoutData(getFullRowGridData());
//		button.setSelection(selection);
//		button.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				setValue( text.getText() );
//				setDirty();
//				updateSelection(e.getSource());
//			}
//
//		});
//
//		return button;
//	}
}
