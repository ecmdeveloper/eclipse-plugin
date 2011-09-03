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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 * @deprecated
 */
public class SelectTableWizardPage extends WizardPage {

	private static final String TITLE = "Select Table";
	private static final String DESCRIPTION = "Select the query table.";

	private TableViewer contentTable;
	private final Query query;
	private final StructuredSelection selection;
	
	protected SelectTableWizardPage(Query query, StructuredSelection selection) {
		super(TITLE);
		this.query = query;
		this.selection = selection;
		setTitle(TITLE);
		setDescription(DESCRIPTION );
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);
		
		createContentTable(container);
	}

	private void createContentTable(Composite container) {

		contentTable = new TableViewer(container, SWT.BORDER | SWT.SINGLE );
		contentTable.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		contentTable.setLabelProvider( new LabelProvider() );
		contentTable.setContentProvider( new ArrayContentProvider() );
		contentTable.setInput( query.getQueryTables() );
		contentTable.addSelectionChangedListener( new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updatePageComplete();
			}} );

		if ( selection != null ) {
			contentTable.setSelection( selection );
		}
	}

	protected void updatePageComplete() {
		setPageComplete( getQueryTable() != null );
	}

	public IQueryTable getQueryTable() {
		IStructuredSelection selection = (IStructuredSelection) contentTable.getSelection();
		Iterator<?> iterator = selection.iterator();
		if ( iterator.hasNext() ) {
			return (IQueryTable) iterator.next();
		}
		return null;
	}
}
