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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.editor.QueryContentProvider;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.util.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class ClassValueWizardPage extends WizardPage {

	private static final String TITLE = "Class value";
	private static final String DESCRIPTION = "Select the class.";
	private TreeViewer tablesTree;
	private String className;
	private final Query query;

	protected ClassValueWizardPage(Query query) {
		super(TITLE);
		this.query = query;
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = getContainer(parent);
		createTablesTree(container);
	}

	private Composite getContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}
	
	private void createTablesTree(Composite container) {

		tablesTree = new TreeViewer(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL );
		tablesTree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		tablesTree.setLabelProvider( getLabelProvider() );
		tablesTree.setContentProvider( new QueryContentProvider() );
		tablesTree.addFilter( getTablesFilter() );
		tablesTree.setInput(query);
		tablesTree.addSelectionChangedListener( new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				fieldSelectionChanged();
			}} 
		);
	}

	private LabelProvider getLabelProvider() {
		return new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				if (element instanceof IQueryTable ) {
					return Activator.getImage( IconFiles.CLASS );
				}
				return null;
			}
		};
	}

	private ViewerFilter getTablesFilter() {
		return new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IQueryTable ) {
					return true;
				}
				return false;
			} 
		};
	}
	
	protected void fieldSelectionChanged() {
		IStructuredSelection selection = (IStructuredSelection) tablesTree.getSelection();
		Iterator<?> iterator = selection.iterator();
		if (iterator.hasNext() ) {
			Object selectedObject = iterator.next();
			if ( selectedObject instanceof IQueryTable ) {
				className = ((IQueryTable) selectedObject).getName();
			} else {
				className = null;
			}
		}
		
		getWizard().getContainer().updateButtons();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String tableName) {
		this.className = tableName;
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if ( visible ) {
			if ( className != null ) {
				IQueryTable queryTable = query.getTable( className );
				if ( queryTable != null ) {
					tablesTree.expandToLevel(2);
					tablesTree.setSelection( new StructuredSelection(queryTable), true );
				}
			}
		}
	}
}
