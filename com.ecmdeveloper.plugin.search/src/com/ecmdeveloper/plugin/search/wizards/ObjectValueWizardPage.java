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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.ui.views.ObjectStoreItemLabelProvider;
import com.ecmdeveloper.plugin.ui.views.ObjectStoresViewContentProvider;


/**
 * @author ricardo.belfor
 *
 */
public class ObjectValueWizardPage extends SimpleValueWizardPage {

	private static final String TITLE = "Object value";
	private static final String DESCRIPTION = "Enter an id or path or select an object.";
	
	private Button useIdButton;
	private Button usePathButton;
	private String selectedPath;
	private String selectedId;
	private boolean showOnlyFolders = false;
	
	protected ObjectValueWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setMultiLine(true);
	}

	public boolean isShowOnlyFolders() {
		return showOnlyFolders;
	}

	public void setShowOnlyFolders(boolean showOnlyFolders) {
		this.showOnlyFolders = showOnlyFolders;
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

		useIdButton = createButton(container, "Use Id value", true);
		usePathButton = createButton(container, "Use Path value", false);
		updateButtonState();
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

		setSelectedValues(destination);
		updateButtonState();
		updateValue();
	}

	private void setSelectedValues(IObjectStoreItem destination) {
		if ( destination instanceof IFolder ) {
			selectedPath = ((IFolder)destination).getPathName();
		} else {
			selectedPath = null;
			useIdButton.setSelection(true);
			usePathButton.setSelection(false);
		}
		selectedId = destination.getId();
	}

	private void updateValue() {
		if (useIdButton.getSelection() ) {
			setText( selectedId );
			setValue( selectedId );
		} else {
			setText( selectedPath );
			setValue( selectedPath );
		}
		setDirty();
	}

	private ElementTreeSelectionDialog createSelectionDialog() {

		ITreeContentProvider contentProvider = new ObjectStoresViewContentProvider();
		ILabelProvider labelProvider = new ObjectStoreItemLabelProvider();
		Shell shell = getWizard().getContainer().getShell();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, labelProvider, contentProvider );
		dialog.setValidator( new ISelectionStatusValidator() {

			private Status errorStatus = new Status( IStatus.ERROR, Activator.PLUGIN_ID, "");

			@Override
			public IStatus validate(Object[] selection) {
				if ( selection != null && selection.length == 1 && !(selection[0] instanceof IObjectStore ) ) {
					return Status.OK_STATUS;
				}
				return errorStatus;
			}
		});
		dialog.setInput( Activator.getDefault().getObjectStoresManager() );
		
		contentProvider.inputChanged( null, null, Activator.getDefault().getObjectStoresManager() );
		dialog.setTitle(TITLE);
		dialog.setMessage( "Select an object" );
		addFolderFilter(dialog);
		return dialog;
	}

	private void addFolderFilter(ElementTreeSelectionDialog dialog) {
		if ( showOnlyFolders ) {
			dialog.addFilter( new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if ( element instanceof IObjectStore || element instanceof IFolder ) {
						return true;
					}
					return false;
				} } );
		}
	}

	private Button createButton(Composite container, String label, boolean selection) {

		Button button = new Button(container, SWT.RADIO);
		button.setText(label);
		button.setLayoutData(getFullRowGridData());
		button.setSelection(selection);
		button.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateValue();
			}}
		);
		return button;
	}
	
	private void updateButtonState() {
		useIdButton.setEnabled( selectedId != null );
		usePathButton.setEnabled( selectedPath != null );
	
	}
}
