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

package com.ecmdeveloper.plugin.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Ricardo.Belfor
 *
 */
public class ConfigureCheckoutWizardPage extends WizardPage {

	private Button downloadButton;
	private Button editButton;
	private Button nothingButton;

	protected ConfigureCheckoutWizardPage() {
		super("configureCheckout");

		setTitle("Configure Checkout");
		setDescription("Configure the checkout options.");
	}

	public boolean isDowload() {
		return downloadButton.getSelection() || editButton.getSelection();
	}
	
	public boolean isEdit() {
		return editButton.getSelection();
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
//		container.setLayout( new StackLayout() );
		setControl(container);
		
		createLabel(container, "Select the actions to perform with the content:" );
		createNothingButton(container);
		createDownloadButton(container);
		createEditButton(container);
	}

	private void createLabel(Composite container, String text) {
		final Label label = new Label(container, SWT.NONE);
		final GridData gridData = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData);
		label.setText(text);
	}
	
	private void createNothingButton(Composite container) {
		nothingButton = new Button(container, SWT.RADIO);
		nothingButton.setText("Nothing");
		nothingButton.setLayoutData(getFullRowGridData());
	}
	
	private void createDownloadButton(Composite container) {
		downloadButton = new Button(container, SWT.RADIO);
		downloadButton.setText("Download Content");
		downloadButton.setLayoutData(getFullRowGridData());
	}

	private void createEditButton(Composite container) {
		editButton = new Button(container, SWT.RADIO);
		editButton.setText("Download and Edit Content");
		editButton.setLayoutData(getFullRowGridData());
		editButton.setSelection(true);
	}
	
	private GridData getFullRowGridData() {
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		return gd;
	}
}
