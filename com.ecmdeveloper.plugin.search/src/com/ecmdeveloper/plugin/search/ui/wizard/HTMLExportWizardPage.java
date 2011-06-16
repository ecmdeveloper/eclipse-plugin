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
public class HTMLExportWizardPage extends WizardPage {

	private static final String TITLE = "HTML Options";
	private boolean writeHeader;

	protected HTMLExportWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription("Configure the HTML file.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = createContainer(parent);
		createWriteHeaderEditor(container);
		setPageComplete(true);
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

	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

	public boolean isWriteHeader() {
		return writeHeader;
	}
}
