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

package com.ecmdeveloper.plugin.search.ui.wizard;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class XMLExportWizardPage extends WizardPage {

	private static final String TITLE = "XML Options";
	private String rowsTag = "rows";
	private String rowTag = "row";

	protected XMLExportWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription("Configure the XML file.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = createContainer(parent);
		
		createRowsTagEditor(container);
		createRowTagEditor(container);
		setPageComplete(true);
	}

	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

	private void createRowsTagEditor(Composite container) {
		StringFieldEditor filenameEditor = new StringFieldEditor("", "Rows tag:",container );
		filenameEditor.setEmptyStringAllowed(false);
		filenameEditor.setStringValue( rowsTag );
		filenameEditor.setPropertyChangeListener( new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				checkValidInput(event);
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					rowsTag = (String) event.getNewValue();
					updateControls();
				}
			}
		});
	}

	private void createRowTagEditor(Composite container) {
		StringFieldEditor filenameEditor = new StringFieldEditor("", "Row tag:",container );
		filenameEditor.setEmptyStringAllowed(false);
		filenameEditor.setStringValue( rowTag );
		filenameEditor.setPropertyChangeListener( new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				checkValidInput(event);
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					rowTag = (String) event.getNewValue();
					updateControls();
				}
			}
		});
	}
	
	private void checkValidInput(PropertyChangeEvent event) {
		if ( event.getProperty().equals( FieldEditor.IS_VALID ) ) {
			if ( (Boolean) event.getNewValue() ) {
				setErrorMessage(null);
			} else {
				setErrorMessage( ((StringFieldEditor) event.getSource()).getErrorMessage() );
			}					
			updateControls();
		}
	}

	protected void updateControls() {
		setPageComplete( rowsTag != null && !rowsTag.isEmpty() && rowTag != null && !rowTag.isEmpty() );
	}

	public String getRowsTag() {
		return rowsTag;
	}

	public String getRowTag() {
		return rowTag;
	}
}
