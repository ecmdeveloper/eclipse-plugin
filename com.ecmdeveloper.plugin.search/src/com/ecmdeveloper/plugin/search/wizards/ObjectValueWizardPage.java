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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;


/**
 * @author ricardo.belfor
 *
 */
public class ObjectValueWizardPage extends SimpleValueWizardPage {

	private static final String TITLE = "Object value";
	private static final String DESCRIPTION = "Enter an id or path or select an object.";

	protected ObjectValueWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	protected void textModified(String textValue) {
		setValue( textValue );
		setDirty();
	}

	@Override
	protected void createInput(Composite container) {
		super.createInput(container);
		Button button = new Button(container, SWT.PUSH );
		button.setText( "Browse" );
		button.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO add real browse dialog
				Shell shell = getWizard().getContainer().getShell();
				MessageDialog.openQuestion(shell, "Select Object", "TODO: select object dialog");
			}} 
		);
	}
}
