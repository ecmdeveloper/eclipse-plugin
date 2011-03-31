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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.views.ObjectStoreItemLabelProvider;
import com.ecmdeveloper.plugin.views.ObjectStoresViewContentProvider;


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
				selectValue();
			}} 
		);

		createButton(container, "Use ID value", true);
		createButton(container, "Use path name value", false);
	}
	
	private void selectValue() {
	
		ElementTreeSelectionDialog dialog = createSelectionDialog();
		
		int answer = dialog.open();
	
		if ( answer != ElementTreeSelectionDialog.OK ) {
			return;
		}
		
		IObjectStoreItem destination = (IObjectStoreItem) dialog.getFirstResult();
		if ( destination == null ) {
			return;
		}

		setText(destination.getId());
		setValue( destination.getId() );
		setDirty();
	}

	private ElementTreeSelectionDialog createSelectionDialog() {

		ITreeContentProvider contentProvider = new ObjectStoresViewContentProvider();
		ILabelProvider labelProvider = new ObjectStoreItemLabelProvider();
		Shell shell = getWizard().getContainer().getShell();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, labelProvider, contentProvider );
		dialog.setInput( ObjectStoresManager.getManager() );
		
		contentProvider.inputChanged( null, null, ObjectStoresManager.getManager() );
		dialog.setTitle(TITLE);
		dialog.setMessage( "Select an object" );
		return dialog;
	}

	private Button createButton(Composite container, String label, boolean selection) {

		Button button = new Button(container, SWT.RADIO);
		button.setText(label);
		button.setLayoutData(getFullRowGridData());
		button.setSelection(selection);

		return button;
	}
}
