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

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.editor.QueryContentProvider;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.util.IconFiles;

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
	private final Query query;
	private ISelection selection;
	
	protected SelectFieldWizardPage(Query query, StructuredSelection selection) {
		super(TITLE);
		this.query = query;
		this.selection = selection;
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = getContainer(parent);
		createFieldsTree(container);
		createExtraControls(container);
	}
	
	protected void createExtraControls(Composite container ) {
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

		fieldsTree = new TreeViewer(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL );
		fieldsTree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		fieldsTree.setLabelProvider( new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				if (element instanceof IQueryTable ) {
					return Activator.getImage( IconFiles.TABLE_FOLDER );
				}
				return null;
			}
		});

		fieldsTree.setContentProvider( new QueryContentProvider() );
		fieldsTree.addSelectionChangedListener( new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				fieldSelectionChanged();
			}} 
		);
		
		if ( filter != null) {
			fieldsTree.addFilter(filter);
		}
		fieldsTree.setInput(query);
	}

	private void fieldSelectionChanged() {
		IStructuredSelection selection = (IStructuredSelection) fieldsTree.getSelection();
		Iterator<?> iterator = selection.iterator();
		if (iterator.hasNext() ) {
			Object selectedObject = iterator.next();
			if ( selectedObject instanceof IQueryField ) {
				field = (IQueryField) selectedObject;
			} else {
				field = null;
			}
		}
		
		getWizard().getContainer().updateButtons();
	}

	public IQueryField getField() {
		return field;
	}

	public void setField(IQueryField field) {
		this.field = field;
	}

	protected TreeViewer getFieldsTree() {
		return fieldsTree;
	}

	public void setFilter(QueryFieldFilter filter) {
		this.filter = filter;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if ( visible ) {
			if ( selection != null /*&& fieldsTree.getSelection().isEmpty()*/) {

				fieldsTree.expandToLevel(2);
//				fieldsTree.expandAll();
//				Iterator<?> iterator = ((IStructuredSelection)selection).iterator();
//				if (iterator.hasNext() ) {
//					Object selectedObject = iterator.next();
//					if ( selectedObject instanceof IQueryField ) {
//						fieldsTree.expandToLevel(((IQueryField) selectedObject).getQueryTable(), 4 );
//					}
//				}
				fieldsTree.setSelection(selection, true);			
			}
		}
	}
}
