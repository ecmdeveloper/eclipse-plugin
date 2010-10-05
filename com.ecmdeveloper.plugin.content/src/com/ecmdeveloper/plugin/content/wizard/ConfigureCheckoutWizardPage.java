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
public class ConfigureCheckoutWizardPage extends WizardPage {

	private static final String TRACK_BUTTON_LABEL = "&Track Content";
	private static final String EDIT_BUTTON_LABEL = "Download and &Edit Content";
	private static final String DOWNLOAD_BUTTON_LABEL = "&Download Content";
	private static final String NOTHING_BUTTON_LABEL = "&Nothing";
	private Button downloadButton;
	private Button editButton;
	@SuppressWarnings("unused")
	private Button nothingButton;
	private Button trackButton;

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

	public boolean isTracked() {
		return trackButton.getSelection();
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
		createTrackButton(container);
	}

	private void createLabel(Composite container, String text) {
		final Label label = new Label(container, SWT.NONE);
		final GridData gridData = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData);
		label.setText(text);
	}
	
	private void createNothingButton(Composite container) {
		nothingButton = createButton(container, NOTHING_BUTTON_LABEL, false );
	}

	private void createDownloadButton(Composite container) {
		downloadButton = createButton(container, DOWNLOAD_BUTTON_LABEL, false );
	}

	private void createEditButton(Composite container) {
		editButton = createButton(container, EDIT_BUTTON_LABEL, false );
	}
	
	private Button createButton(Composite container, String text, boolean selection ) {
		
		Button button = new Button(container, SWT.RADIO);
		button.setText( text);
		button.setLayoutData(getFullRowGridData());
		button.setSelection(selection);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateTrackButton();
			}
		});
		
		return button;
	}

	protected void updateTrackButton() {
		trackButton.setEnabled( isDowload() );
		trackButton.setSelection( isDowload() );
	}

	private GridData getFullRowGridData() {
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		return gd;
	}

	private void createTrackButton(Composite container) {
		trackButton = new Button(container, SWT.CHECK );
		trackButton.setText(TRACK_BUTTON_LABEL);
		trackButton.setLayoutData(getFullRowGridData());
		trackButton.setSelection(false);
	}
}
