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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public class SelectFullTextQueryFieldWizardPage extends SelectFieldWizardPage {

	private Button allFieldsButton;
	private boolean allFields;
	
	protected SelectFullTextQueryFieldWizardPage(Query query, StructuredSelection selection) {
		super(query, selection);
	}

	@Override
	protected void createExtraControls(Composite container) {
		
		allFieldsButton = new Button(container, SWT.CHECK );
		allFieldsButton.setText( "Search all CBR-enabled properties, including content.");
		allFieldsButton.setSelection( allFields );
		allFieldsButton.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				allFields = allFieldsButton.getSelection();
				getFieldsTree().getTree().setEnabled( !allFields  );
				getWizard().getContainer().updateButtons();
			}} );
		
		getFieldsTree().getTree().setEnabled( !allFields  );
		getFieldsTree().addFilter( new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IQueryTable ) {
					return ((IQueryTable)element).isCBREnabled();
				}
				return true;
			} 
		});
	}

	protected boolean isAllFields() {
		return allFields;
	}

	protected void setAllFields(boolean allFields) {
		this.allFields = allFields;
	}
}
