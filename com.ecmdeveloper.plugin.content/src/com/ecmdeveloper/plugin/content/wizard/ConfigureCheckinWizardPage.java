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

package com.ecmdeveloper.plugin.content.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Ricardo.Belfor
 *
 */
public class ConfigureCheckinWizardPage extends WizardPage {

	private Button selectContentButton;
	private Button useSavedContentButton;
	private Button CheckinMajorbutton;

	protected ConfigureCheckinWizardPage() {
		super("configureCheckin");
		
		setTitle("Configure Checkin");
		setDescription("Configure the checkin options.");
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		
		createLabel(container, "Select the source of the document content:" );
		createSelectContentButton(container);
		createUseSavedContentButton(container);
		createCheckinMajorButton(container);
	}

	private void createLabel(Composite container, String text) {
		final Label label = new Label(container, SWT.NONE);
		final GridData gridData = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData);
		label.setText(text);
	}

	private void createSelectContentButton(Composite container) {

		selectContentButton = new Button(container, SWT.RADIO);
		selectContentButton.setText("Select Content");
		selectContentButton.setLayoutData(getFullRowGridData());
		selectContentButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});
	}

	private GridData getFullRowGridData() {
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		return gd;
	}

	protected void updateButtons() {
		getWizard().getContainer().updateButtons();
	}

	private void createUseSavedContentButton(Composite container) {

		useSavedContentButton = new Button(container, SWT.RADIO);
		useSavedContentButton.setText("Use saved content");
		useSavedContentButton.setLayoutData(getFullRowGridData());
		useSavedContentButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});
	}

	private void createCheckinMajorButton(Composite container) {
		CheckinMajorbutton = new Button(container, SWT.CHECK);
		CheckinMajorbutton.setText("Checkin as major version");
		CheckinMajorbutton.setLayoutData(getFullRowGridData());
		CheckinMajorbutton.setSelection(true);
	}

	public boolean isCheckinMajor() {
		return CheckinMajorbutton.getSelection();
	}
	
	public boolean isSelectContent() {
		return selectContentButton.getSelection();
	}
}
