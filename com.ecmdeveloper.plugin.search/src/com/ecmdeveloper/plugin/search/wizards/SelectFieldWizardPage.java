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

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.search.editor.QueryContentProvider;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public class SelectFieldWizardPage extends WizardPage {

	private static final String TITLE = "Select Field";
	private static final String DESCRIPTION = "Select the desired query field.";

	private TreeViewer fieldsTree;
	private IQueryField field;
	private QueryFieldFilter filter;
	private Query query;
	
	protected SelectFieldWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = getContainer(parent);
		createFieldsTree(container);
	}

	private Composite getContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

	private void createFieldsTree(Composite container) {

		fieldsTree = new TreeViewer(container, SWT.BORDER | SWT.SINGLE );
		fieldsTree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		fieldsTree.setLabelProvider( new LabelProvider() );
		fieldsTree.setContentProvider( new QueryContentProvider() );
		fieldsTree.addSelectionChangedListener( new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				fieldSelectionChanged();
			}} );
		fieldsTree.addFilter(filter);
	}

	private void fieldSelectionChanged() {
		IStructuredSelection selection = (IStructuredSelection) fieldsTree.getSelection();
		Iterator<?> iterator = selection.iterator();
		if (iterator.hasNext() ) {
			field = (IQueryField) iterator.next();
		}
		
		getWizard().getContainer().updateButtons();
	}

	public IQueryField getField() {
		return field;
	}

	public void setField(IQueryField field) {
		this.field = field;
	}

	public void setFilter(QueryFieldFilter filter) {
		this.filter = filter;
	}

	public void setInput(Query query) {
		this.query = query;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if ( visible ) {
			fieldsTree.setInput( query );
//			if ( selection != null && contentTable.getSelection().isEmpty()) {
//				contentTable.setSelection(selection);
//			}
		}
	}
}
