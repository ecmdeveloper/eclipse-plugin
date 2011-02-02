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

import javax.management.Query;

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
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;

import com.ecmdeveloper.plugin.search.dnd.TextTransferDropTargetListener;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.parts.GraphicalPartFactory;

/**
 * @author ricardo.belfor
 *
 */
public class GraphicalQueryEditor extends GraphicalEditorWithFlyoutPalette {

	private QueryDiagram logicDiagram = new QueryDiagram();
	private PaletteRoot root;

	public GraphicalQueryEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

//	public void createPartControl(Composite parent) {
//		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
//		super.createPartControl(sashForm);
//		createLabel(sashForm, "Hello, Editor 2!");
//	}	

	private void createLabel(Composite container, String text) {
		final Label label = new Label(container, SWT.BORDER);
		final GridData gridData = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData);
		label.setText(text);
	}
	
	@Override
	protected PaletteRoot getPaletteRoot() {
		
//		PaletteRoot root = new PaletteRoot();
//		
//		PaletteGroup instGroup = new PaletteGroup("Creation d'elemnts"); 
//		root.add(instGroup);		
//		
//		instGroup.add(new CreationToolEntry("Condition", "Creation of a condition",
//				new SimpleFactory(QueryCondition.class)
//				/*new QueryComponentCreationFactory(QueryCondition.class)*/, null, null));
//		instGroup.add(new CreationToolEntry("Operation", "Creation of an operation",
//				new SimpleFactory(QueryOperation.class)
//				/*new QueryComponentCreationFactory(QueryOperation.class)*/, null, null));
//		return root;
		
		if( root == null ){
			root = QueryPaletteFactory.createPalette();
		}
		return root;
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = configureViewer();
		
		
//		configureContextMenu(viewer);
	}

	private GraphicalViewer configureViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
//	    viewer.setEditPartFactory(new QueryEditPartFactory() );
		
		viewer.setEditPartFactory(new GraphicalPartFactory());
	    viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		return viewer;
	}

	@Override
	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(/*createMockModel()*/ getLogicDiagram() );
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

	protected QueryDiagram getLogicDiagram() {
		return logicDiagram;
	}

	@Override
	protected void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action;
		
		action = new CopyTemplateAction(this);
		registry.registerAction(action);
	}
}