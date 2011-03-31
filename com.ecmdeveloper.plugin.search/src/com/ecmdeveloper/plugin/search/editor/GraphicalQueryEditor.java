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

package com.ecmdeveloper.plugin.search.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.CopyTemplateAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.actions.AddTableAction;
import com.ecmdeveloper.plugin.search.actions.ConvertToTextAction;
import com.ecmdeveloper.plugin.search.actions.EditQueryComponentAction;
import com.ecmdeveloper.plugin.search.actions.ExecuteSearchAction;
import com.ecmdeveloper.plugin.search.actions.RemoveTableAction;
import com.ecmdeveloper.plugin.search.actions.SetMainQueryAction;
import com.ecmdeveloper.plugin.search.actions.ShowSqlAction;
import com.ecmdeveloper.plugin.search.actions.ToggleDistinctAction;
import com.ecmdeveloper.plugin.search.actions.ToggleIncludeSubclassesAction;
import com.ecmdeveloper.plugin.search.dnd.TextTransferDropTargetListener;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.parts.GraphicalPartFactory;
import com.ecmdeveloper.plugin.search.util.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class GraphicalQueryEditor extends GraphicalEditorWithFlyoutPalette implements PropertyChangeListener {

	private Query query = new Query();
	private PaletteRoot root;
	private QueryFieldsTable queryFieldsTable;
	private ToolItem removeTableItem;
	private IAction actions[] = { new AddTableAction(this), new RemoveTableAction(this),
			new EditQueryComponentAction(this), new ToggleIncludeSubclassesAction(this),
			new ToggleDistinctAction(this), new ShowSqlAction(this), new SetMainQueryAction(this),
			new ConvertToTextAction(this), new ExecuteSearchAction(this) };
	
	public GraphicalQueryEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		query.addPropertyChangeListener(this);
	}

	@Override
	public void dispose() {
		super.dispose();
		query.removePropertyChangeListener(this);
		queryFieldsTable.dispose();
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		
		if ( propertyChangeEvent.getPropertyName().equals( Query.TABLE_ADDED ) ||
				propertyChangeEvent.getPropertyName().equals( Query.TABLE_REMOVED ) ) {
			firePropertyChange(IEditorPart.PROP_DIRTY);
			removeTableItem.setEnabled( !query.getQueryTables().isEmpty() );
		}
	}

	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	public void createPartControl(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout());
		Composite c = new Composite(composite, SWT.NONE);
		c.setLayout(new FillLayout());

		createToolBar(c);
		SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL ));

		queryFieldsTable = new QueryFieldsTable(query, sashForm);	
		super.createPartControl(sashForm);
	}

	private Control createToolBar(Composite composite) {
		ToolBar toolBar = new ToolBar(composite, SWT.NONE);
		createExecuteButton(toolBar);
		createAddTableButton(toolBar);
		removeTableItem = createDeleteTableButton(toolBar);
		createIncludeSubClassesButton(toolBar);
		createDistinctButton(toolBar);
		createShowSQLButton(toolBar);
		
		new ToolItem(toolBar, SWT.SEPARATOR );

//		Label l = new Label( toolBar, SWT.BOTTOM );
//		l.setText("Hallo:");
//
//		ToolItem ti1 = new ToolItem(toolBar, SWT.SEPARATOR);
//		l.pack();
//		
//		ti1.setWidth(l.getBounds().width);
//		ti1.setControl(l);
		
		ToolBar tb = new ToolBar(composite, SWT.FLAT);
		ToolItem ti = new ToolItem(tb, SWT.NONE);
		ti.setText("Max Count:");	

		ToolItem ti2 = new ToolItem(tb, SWT.SEPARATOR);
		Text text = new Text(tb, SWT.BORDER );
		text.setText("200");
		text.pack();
		
		ti2.setWidth(text.getBounds().width);
		ti2.setControl(text);
		
		return toolBar;
	}

	private void createExecuteButton(ToolBar toolBar) {
		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage( Activator.getImage("icons/find.png") );
		toolItem.setToolTipText("Execute Search");
		toolItem.addSelectionListener( new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IAction action = getActionRegistry().getAction( ExecuteSearchAction.ID );
				action.run();
			}} 
		);
	}

	private void createAddTableButton(ToolBar toolBar) {
		ToolItem addTableItem = new ToolItem(toolBar, SWT.PUSH);
		addTableItem.setImage( Activator.getImage("icons/table_add.png") );
		addTableItem.setToolTipText("Add Table");
		addTableItem.addSelectionListener( new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IAction action = getActionRegistry().getAction( AddTableAction.ID );
				action.run();
			}} 
		);
	}

	private ToolItem createDeleteTableButton(ToolBar toolBar) {
		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage( Activator.getImage("icons/table_delete.png") );
		toolItem.setToolTipText("Remove Table");
		toolItem.setEnabled( !query.getQueryTables().isEmpty() );
		toolItem.addSelectionListener( new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IAction action = getActionRegistry().getAction( RemoveTableAction.ID );
				action.run();
			}} 
		);
		return toolItem;
	}
	
	private void createIncludeSubClassesButton(ToolBar toolBar) {
		ToolItem toolItem = new ToolItem(toolBar, SWT.CHECK);
		toolItem.setImage( Activator.getImage("icons/table_multiple.png") );
		toolItem.setToolTipText("Include Subclasses");
		toolItem.setSelection( query.isIncludeSubclasses() );
		toolItem.addSelectionListener( new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IAction action = getActionRegistry().getAction( ToggleIncludeSubclassesAction.ID );
				action.run();
			}} 
		);
	}

	private void createDistinctButton(ToolBar toolBar) {
		ToolItem toolItem = new ToolItem(toolBar, SWT.CHECK);
		toolItem.setImage( Activator.getImage( IconFiles.DISTINCT ) );
		toolItem.setToolTipText("Distinct");
		toolItem.setSelection( query.isDistinct() );
		toolItem.addSelectionListener( new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IAction action = getActionRegistry().getAction( ToggleDistinctAction.ID );
				action.run();
			}} 
		);
	}

	private void createShowSQLButton(ToolBar toolBar) {
		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH );
		toolItem.setImage( Activator.getImage( IconFiles.SHOW_SQL ) );
		toolItem.setToolTipText("Show SQL");
		toolItem.addSelectionListener( new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IAction action = getActionRegistry().getAction( ShowSqlAction.ID );
				action.run();
			}} 
		);
	}
	
	@Override
	protected PaletteRoot getPaletteRoot() {
		if( root == null ){
			root = QueryPaletteFactory.createPalette( query );
		}
		return root;
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = configureViewer();
		configureContextMenu(viewer);
	}
	
	private GraphicalViewer configureViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new GraphicalPartFactory());
	    viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer) );
		return viewer;
	}

	private void configureContextMenu(GraphicalViewer viewer) {
		MenuManager menuManager = new QueryContextMenuManager( getActionRegistry() );
		viewer.setContextMenu( menuManager );
	}

	@Override
	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents( getQueryDiagram() );

		getGraphicalViewer().addDropTargetListener((TransferDropTargetListener)
				new TemplateTransferDropTargetListener(getGraphicalViewer()));

		getGraphicalViewer().addDropTargetListener((TransferDropTargetListener)
				new TextTransferDropTargetListener(
						getGraphicalViewer(), TextTransfer.getInstance()));
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	protected PaletteViewerProvider createPaletteViewerProvider() {

		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
			protected void hookPaletteViewer(PaletteViewer viewer) {
				super.hookPaletteViewer(viewer);
				final CopyTemplateAction copy = (CopyTemplateAction)getActionRegistry()
						.getAction(ActionFactory.COPY.getId());
				viewer.addSelectionChangedListener(copy);
			}
		};
	}

	protected QueryDiagram getQueryDiagram() {
		return query.getQueryDiagram();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action = new CopyTemplateAction(this);
		registry.registerAction(action);

		for ( IAction action2 : actions ) {
			getActionRegistry().registerAction( action2 );
			getSelectionActions().add( action2.getId() );
		}
	}

	public Query getQuery() {
		return query;
	}
}