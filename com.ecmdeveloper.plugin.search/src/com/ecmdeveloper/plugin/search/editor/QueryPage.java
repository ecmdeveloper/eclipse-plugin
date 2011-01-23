/**
 * Copyright 2011, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.search.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryCondition;
import com.ecmdeveloper.plugin.search.model.QueryOperation;
import com.ecmdeveloper.plugin.search.model.QueryOperationType;
import com.ecmdeveloper.plugin.search.parts.QueryEditPartFactory;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class QueryPage extends EditorPart {

	private final SearchEditor parent;
	private TableViewer tableViewer;
	private ScrollingGraphicalViewer viewer;
	
	public QueryPage(SearchEditor parent) {
		super();
		this.parent = parent;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
	
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

 //		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
//		createTableViewer(sashForm);
//		createGraphicalViewer(sashForm);
//		createLabel(sashForm, "Hello, Editor 2!");
		createGraphicalViewer(composite);
	}

	private void createGraphicalViewer(Composite parent) {
        viewer = new ScrollingGraphicalViewer();
        viewer.createControl(parent);

        // configure the viewer
        org.eclipse.swt.graphics.Color background = parent.getBackground();
		viewer.getControl().setBackground(new org.eclipse.swt.graphics.Color(null, 200,255,255));
        viewer.setRootEditPart(new ScalableFreeformRootEditPart());
        viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

        // hook the viewer into the editor
        //registerEditPartViewer(viewer);

        // configure the viewer with drag and drop
        //configureEditPartViewer(viewer);

        viewer.setEditPartFactory( new QueryEditPartFactory() );
	    viewer.setRootEditPart(new ScalableFreeformRootEditPart());
        Query query = createMockModel();
		viewer.setContents( query );

        viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private Query createMockModel() {
		Query query = new Query();
        
		QueryOperation queryOperation = new QueryOperation(QueryOperationType.AND);
		query.setQueryOperation( queryOperation );
		queryOperation.add(new QueryCondition() ); 
		queryOperation.add(new QueryCondition() );

		QueryOperation orOperation = new QueryOperation(QueryOperationType.OR);
		orOperation.add(new QueryCondition() );
		orOperation.add(new QueryCondition() );
		queryOperation.add(orOperation );
		
		QueryOperation andOperation = new QueryOperation(QueryOperationType.AND);
		andOperation.add(new QueryCondition() );
		andOperation.add(new QueryCondition() );
		queryOperation.add(andOperation );

		return query;
	}

	private void createTableViewer(Composite parent) {

		tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		
		final Table table = tableViewer.getTable();
		TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);

		TableColumn typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setText("");
		layout.setColumnData(typeColumn, new ColumnPixelData(18) );

		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		layout.setColumnData(nameColumn, new ColumnWeightData(1) );

		TableColumn objectStoreColumn = new TableColumn(table, SWT.LEFT);
		objectStoreColumn.setText("Object Store");
		layout.setColumnData(objectStoreColumn, new ColumnWeightData(1) );

		table.setHeaderVisible(true);
		table.setLinesVisible(false);

//		viewer.setContentProvider( new CodeModulesViewContentProvider() );
//		viewer.setLabelProvider( new CodeModulesViewLabelProvider() );
//		viewer.setInput( CodeModulesManager.getManager() );

		getSite().setSelectionProvider(tableViewer);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	private void createLabel(Composite container, String text) {
		final Label label = new Label(container, SWT.BORDER);
		final GridData gridData = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData);
		label.setText(text);
	}

}
