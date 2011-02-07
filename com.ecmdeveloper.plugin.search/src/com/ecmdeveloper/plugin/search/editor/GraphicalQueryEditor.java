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
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;

import com.ecmdeveloper.plugin.search.dnd.TextTransferDropTargetListener;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.parts.GraphicalPartFactory;

/**
 * @author ricardo.belfor
 *
 */
public class GraphicalQueryEditor extends GraphicalEditorWithFlyoutPalette {

	private Query query = new Query();
	private CheckboxTableViewer tableViewer;
	private PaletteRoot root;

	public GraphicalQueryEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	public void createPartControl(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout());
		
		CoolBar coolBar = new CoolBar(composite, SWT.NONE);
		coolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		CoolItem textItem = new CoolItem(coolBar, SWT.NONE);
		Text text = new Text(coolBar, SWT.BORDER | SWT.DROP_DOWN);
		text.setText("TEXT");
		text.pack();
		Point size = text.getSize();
		textItem.setControl(text);
		textItem.setSize(textItem.computeSize(size.x, size.y));
		
		SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL ));
//		Composite composite = new Composite(sashForm, SWT.NONE); 

		
		createTableViewer(sashForm);
		super.createPartControl(sashForm);
//		createLabel(sashForm, "Hello, Editor 2!");
	}	

	private void createLabel(Composite container, String text) {
		final Label label = new Label(container, SWT.BORDER);
		final GridData gridData = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData);
		label.setText(text);
	}

	private void createTableViewer(Composite parent) {
/*
		tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER);

		final Table table = tableViewer.getTable();
		TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);

		TableColumn typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setText("");
		layout.setColumnData(typeColumn, new ColumnPixelData(18));

		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		layout.setColumnData(nameColumn, new ColumnWeightData(1));

		TableColumn objectStoreColumn = new TableColumn(table, SWT.LEFT);
		objectStoreColumn.setText("Object Store");
		layout.setColumnData(objectStoreColumn, new ColumnWeightData(1));

*/
		tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.FULL_SELECTION );
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

//		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
//		nameColumn.setText("Name");
//;		layout.setColumnData(nameColumn, new ColumnWeightData(1));
		
		createTableViewerColumn("Bla", 100, 1 );
		createTableViewerColumn("Field Type", 100, 2 );
		TableViewerColumn column = createTableViewerColumn("Sort Type", 100, 3 );
		column.setEditingSupport( new SortTypeEditingSupport( tableViewer) );

		column = createTableViewerColumn("Sort Order", 100, 4 );
		column.setEditingSupport( new SortOrderEditingSupport( tableViewer ) );
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider( new ArrayContentProvider() );
		tableViewer.setLabelProvider( new TableViewLabelProvider() );
		tableViewer.setInput( query.getQueryFields() );

		
		//getSite().setSelectionProvider(tableViewer);
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
			final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer,
			SWT.CHECK);
			final TableColumn column = viewerColumn.getColumn();
			column.setText(title);
			column.setWidth(bound);
			column.setResizable(true);
			column.setMoveable(true);
			return viewerColumn;
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
	}
}