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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
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
public class CSVExportWizardPage extends WizardPage {

	private static final String TITLE = "CSV Options";
	private char delimiter;
	private boolean writeHeader;

	protected CSVExportWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription("Configure the CSV file.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = createContainer(parent);
		createWriteHeaderEditor(container);
		createDelimitersEditor(container);
		setPageComplete(false);
	}

	private void createWriteHeaderEditor(Composite container) {
		BooleanFieldEditor editor = new BooleanFieldEditor("","Write Column Name Header", container );
		writeHeader = editor.getBooleanValue();
		editor.setPropertyChangeListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				writeHeader = (Boolean) event.getNewValue();
				
			}
		});
	}

	private void createDelimitersEditor(Composite container) {

		String[][] labelsAndValues = { { "Comma", "," }, { "Tab", "\t" }, { "Semicolon", ";" } };  
		RadioGroupFieldEditor editor = new RadioGroupFieldEditor("", "Column Delimiters:", 1,
				labelsAndValues, container);

		editor.setPropertyChangeListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				delimiter = ((String) event.getNewValue()).charAt(0);
				updateControls();
			}
		});
	}

	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

	protected void updateControls() {
		setPageComplete( delimiter != '\0' );
	}
	
	public char getDelimiter() {
		return delimiter;
	}

	public boolean isWriteHeader() {
		return writeHeader;
	}
}
