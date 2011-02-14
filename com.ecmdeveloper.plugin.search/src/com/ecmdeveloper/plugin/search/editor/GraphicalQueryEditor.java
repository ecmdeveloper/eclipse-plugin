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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Label;
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
import com.ecmdeveloper.plugin.search.actions.EditQueryComponentAction;
import com.ecmdeveloper.plugin.search.actions.RemoveTableAction;
import com.ecmdeveloper.plugin.search.dnd.TextTransferDropTargetListener;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.parts.GraphicalPartFactory;

/**
 * @author ricardo.belfor
 *
 */
public class GraphicalQueryEditor extends GraphicalEditorWithFlyoutPalette implements PropertyChangeListener {

	private Query query = new Query();
	private PaletteRoot root;
	private QueryFieldsTable queryFieldsTable;
	private ToolItem removeTableItem;
	
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

	private void createCoolbar(Composite composite) {
		
		
		CoolBar coolBar = new CoolBar(composite, SWT.NONE);
		coolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(createToolBar(coolBar));
		calcSize(item);
		
		CoolItem textItem = new CoolItem(coolBar, SWT.NONE);

		Composite c = new Composite(coolBar, SWT.NONE);
		c.setLayout(new FillLayout());

		Label l = new Label(c, SWT.NONE);
		l.setText("Max Count:");
//		ToolBar tb = new ToolBar(textItem, SWT.FLAT);
//		ToolItem ti = new ToolItem(tb, SWT.NONE);
//		ti.setText("Max Count:");	

		Text text = new Text(c, SWT.BORDER | SWT.DROP_DOWN);
		text.setText("200");
		text.pack();
//		Point size = text.getSize();
//		textItem.setControl(text);
//		textItem.setSize(textItem.computeSize(size.x, size.y));
		textItem.setControl(c);	
		calcSize(textItem);
	}	
	private void calcSize(CoolItem item) {
		Control control = item.getControl();
		Point pt = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		pt = item.computeSize(pt.x, pt.y);
		item.setSize(pt);
	}
	
	private Control createToolBar(Composite composite) {
		ToolBar toolBar = new ToolBar(composite, SWT.NONE);
		createExecuteButton(toolBar);
		createAddTableButton(toolBar);
		removeTableItem = createDeleteTableButton(toolBar);
		createIncludeSubClassesButton(toolBar);
		
		new ToolItem(toolBar, SWT.SEPARATOR );

		Label l = new Label( toolBar, SWT.BOTTOM );
		l.setText("Hallo:");

		ToolItem ti1 = new ToolItem(toolBar, SWT.SEPARATOR);
		l.pack();
		
		ti1.setWidth(l.getBounds().width);
		ti1.setControl(l);
		
		ToolBar tb = new ToolBar(composite, SWT.FLAT);
		ToolItem ti = new ToolItem(tb, SWT.NONE);
		ti.setText("Max Count:");	

		ToolItem ti2 = new ToolItem(tb, SWT.SEPARATOR);
		Text text = new Text(tb, SWT.BORDER );
		text.setText("200");
		text.pack();
		
		ti2.setWidth(text.getBounds().width);
		ti2.setControl(text);
		
//		ToolItem ti = new ToolItem(toolBar, SWT.NONE);
//		ti.setText("Max Count:");	

		return toolBar;
	}

	private void createExecuteButton(ToolBar toolBar) {
		ToolItem executeItem = new ToolItem(toolBar, SWT.PUSH);
		executeItem.setImage( Activator.getImage("icons/find.png") );
		executeItem.setToolTipText("Execute Search");
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
		ToolItem includeSubClassesItem = new ToolItem(toolBar, SWT.CHECK);
		includeSubClassesItem.setImage( Activator.getImage("icons/table_multiple.png") );
		includeSubClassesItem.setToolTipText("Include Subclasses");
	}

	private void createLabel(Composite container, String text) {
		final Label label = new Label(container, SWT.BORDER);
		final GridData gridData = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData);
		label.setText(text);
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
		return viewer;
	}

	private void configureContextMenu(GraphicalViewer viewer) {
		MenuManager menuManager = new QueryContextMenuManager( getActionRegistry() );
		viewer.setContextMenu( menuManager );
	}

	@Override
	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(/*createMockModel()*/ getQueryDiagram() );
//		viewer.addDropTargetListener(createTransferDropTargetListener());
//		viewer.addDropTargetListener((TransferDropTargetListener)
//				new TemplateTransferDropTargetListener(getGraphicalViewer()));

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
//		return new PaletteViewerProvider(getEditDomain()) {
//			protected void configurePaletteViewer(PaletteViewer viewer) {
//				super.configurePaletteViewer(viewer);
//				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
//			}
//		};
		
		return new PaletteViewerProvider(getEditDomain()) {
			private IMenuListener menuListener;
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
//				viewer.setCustomizer(new LogicPaletteCustomizer());
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
			protected void hookPaletteViewer(PaletteViewer viewer) {
				super.hookPaletteViewer(viewer);
				final CopyTemplateAction copy = (CopyTemplateAction)getActionRegistry()
						.getAction(ActionFactory.COPY.getId());
				viewer.addSelectionChangedListener(copy);
//				if (menuListener == null)
//					menuListener = new IMenuListener() {
//						public void menuAboutToShow(IMenuManager manager) {
//							manager.appendToGroup(GEFActionConstants.GROUP_COPY, copy);
//						}
//					};
//				viewer.getContextMenu().addMenuListener(menuListener);
			}
		};
	}

//	private TransferDropTargetListener createTransferDropTargetListener() {
//		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
//			protected CreationFactory getFactory(Object template) {
//				return new SimpleFactory((Class) template);
//			}
//		};
//	}

	protected QueryDiagram getQueryDiagram() {
		return query.getQueryDiagram();
	}

	@Override
	protected void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action = new CopyTemplateAction(this);
		registry.registerAction(action);
		
		registerAddTableAction();
		registerRemoveTableAction();
		registerEditQueryComponentAction();
	}

	@SuppressWarnings("unchecked")
	private void registerAddTableAction() {
		IAction addTableAction = new AddTableAction(this);
		getActionRegistry().registerAction(addTableAction);
		getSelectionActions().add( addTableAction.getId() );
	}

	@SuppressWarnings("unchecked")
	private void registerRemoveTableAction() {
		IAction removeTableAction = new RemoveTableAction(this);
		getActionRegistry().registerAction(removeTableAction);
		getSelectionActions().add( removeTableAction.getId() );
	}

	@SuppressWarnings("unchecked")
	private void registerEditQueryComponentAction() {
		IAction action = new EditQueryComponentAction(this);
		getActionRegistry().registerAction(action);
		getSelectionActions().add( action.getId() );
	}
	
	public Query getQuery() {
		return query;
	}
}