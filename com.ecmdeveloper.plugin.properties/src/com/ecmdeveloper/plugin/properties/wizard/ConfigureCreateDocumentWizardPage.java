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

package com.ecmdeveloper.plugin.properties.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class ConfigureCreateDocumentWizardPage extends WizardPage {

	private Button checkinMajorButton;
	private Button autoClassifyButton;
	
	protected ConfigureCreateDocumentWizardPage() {
		super("configureCreateDocument");
		
		setTitle("Configure Create Document");
		setDescription("Configure the create document options.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		
		checkinMajorButton = createCheckbox(container, "Checkin as major version", true );
		autoClassifyButton = createCheckbox(container, "Automatically classify", false );
	}

	private Button createCheckbox(Composite container, String text, boolean selected ) {
		Button checkbox = new Button(container, SWT.CHECK);
		checkbox.setText(text);
		checkbox.setLayoutData(getFullRowGridData());
		checkbox.setSelection( selected );
		
		return checkbox;
	}

	
	public boolean isCheckinMajor() {
		return checkinMajorButton.getSelection();
	}

	public boolean isAutoClassify() {
		return autoClassifyButton.getSelection();
	}

	private GridData getFullRowGridData() {
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		return gd;
	}
}
