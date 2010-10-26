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

package com.ecmdeveloper.plugin.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class ConfigureDeleteWizardPage extends WizardPage {

	private static final String KEEP_CONTAINED_OBJECTS_LABEL = "&Keep contained objects";
	private static final String DELETE_CONTAINED_OBJECTS_LABEL = "&Delete the following contained objects:";
	
	private static final String DELETE_CONTAINED_FOLDERS_LABEL = "&Folders";
	private static final String DELETE_CONTAINED_CUSTOM_OBJECTS_LABEL = "&Custom Objects";
	private static final String DELETE_CONTAINED_DOCUMENTS_LABEL = "D&ocuments";
	
	private static final String PAGE_NAME = "Configure Delete";
	private static final String DESCRIPTION = "Configure the items to be deleted.";

	private static final int NUM_COLUMNS = 1;
	private static final int CHECKBOX_INDENT = 20;

	private Button deleteContainedDocumentsButton;
	private Button deleteContainedCustomObjectsButton;
	private Button deleteContainedFoldersButton;
	private Button deleteContainedObjectsButton;

	protected ConfigureDeleteWizardPage() {
		super(PAGE_NAME);
		
		setTitle(PAGE_NAME);
		setDescription( DESCRIPTION);
	}

	public boolean isDeleteContainedDocuments() {
		return deleteContainedObjectsButton.getSelection()
				&& deleteContainedDocumentsButton.getSelection();
	}

	public boolean isDeleteContainedCustomObjects() {
		return deleteContainedObjectsButton.getSelection()
				&& deleteContainedCustomObjectsButton.getSelection();
	}

	public boolean isDeleteContainedFolders() {
		return deleteContainedObjectsButton.getSelection()
				&& deleteContainedFoldersButton.getSelection();
	}
	
	@Override
	public void createControl(Composite parent) {

		Composite container = createContainer(parent);
		createButton(container, KEEP_CONTAINED_OBJECTS_LABEL, true);
		deleteContainedObjectsButton = createButton(container, DELETE_CONTAINED_OBJECTS_LABEL, false);
		
		deleteContainedDocumentsButton = createCheckbox(container, DELETE_CONTAINED_DOCUMENTS_LABEL);
		deleteContainedCustomObjectsButton = createCheckbox(container, DELETE_CONTAINED_CUSTOM_OBJECTS_LABEL);
		deleteContainedFoldersButton = createCheckbox(container, DELETE_CONTAINED_FOLDERS_LABEL);
		updateButtons();
	}

	private Button createButton(Composite container, String text, boolean selection ) {
		
		Button button = new Button(container, SWT.RADIO);
		button.setText( text);
		button.setLayoutData(getFullRowGridData());
		button.setSelection(selection);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});
		
		return button;
	}
	
	protected void updateButtons() {
		boolean deleteContainedObjects = deleteContainedObjectsButton.getSelection();
		deleteContainedDocumentsButton.setEnabled( deleteContainedObjects );
		deleteContainedCustomObjectsButton.setEnabled( deleteContainedObjects );
		deleteContainedFoldersButton.setEnabled( deleteContainedObjects );
	}

	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = NUM_COLUMNS;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

	private Button createCheckbox(Composite container, String text) {
		Button checkbox = new Button(container, SWT.CHECK);
		checkbox.setText(text);
		checkbox.setLayoutData(getFullRowIndentGridData());
		checkbox.setSelection(false);
		return checkbox;
	}
	
	private GridData getFullRowGridData() {
		GridData gd = new GridData();
		gd.horizontalSpan = NUM_COLUMNS;
		return gd;
	}

	private GridData getFullRowIndentGridData() {
		GridData gridData = getFullRowGridData();
		gridData.horizontalIndent = CHECKBOX_INDENT;
		return gridData;
	}
}
