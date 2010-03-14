/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.editors;

import java.io.IOException;
import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.ecmdeveloper.plugin.diagrams.actions.ExportDiagramAction;
import com.ecmdeveloper.plugin.diagrams.actions.ShowPropertiesAction;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramAttribute;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramFile;
import com.ecmdeveloper.plugin.diagrams.parts.ClassesEditPartFactory;
import com.ecmdeveloper.plugin.diagrams.util.PluginMessage;

/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramEditor extends GraphicalEditorWithFlyoutPalette {

	private static final String FILE_READ_MESSAGE = "Reading Class Diagram File failed.";

	private static final String CLASS_DIAGRAM_EDITOR_NAME = "Class Diagram Editor";

	private ClassDiagram model;
	
	public static final String ID = "com.ecmdeveloper.plugin.diagrams.classDiagramEditor";

	public ClassDiagramEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	public ClassDiagram getClassDiagram() {
		return model;
	}
	
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = configureViewer();
		configureContextMenu(viewer);
		
//		RootEditPart rootEditPart = viewer.getRootEditPart();
//		if ( rootEditPart instanceof LayerManager ) {
//			LayerManager layerManager = (LayerManager) rootEditPart;
//			ConnectionLayer layer = (ConnectionLayer) layerManager.getLayer( LayerConstants.CONNECTION_LAYER );
//			layer.setConnectionRouter( new ManhattanConnectionRouter() );
////			layer.setConnectionRouter( new ShortestPathConnectionRouter( null ) );
//		}
	}

	private GraphicalViewer configureViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
	    viewer.setEditPartFactory(new ClassesEditPartFactory() );
	    viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		return viewer;
	}

	
	private void configureContextMenu(GraphicalViewer viewer) {
		
		MenuManager menuManager = new ClassDiagramContextMenuManager( getActionRegistry() );
		viewer.setContextMenu( menuManager );
//		ContextMenuProvider contextMenuProvider = new ClassDiagramEditorContextMenuProvider(viewer,
//				getActionRegistry());
//		viewer.setContextMenu(contextMenuProvider);
//		getSite().registerContextMenu(contextMenuProvider, viewer);
	}

	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();

	      GraphicalViewer viewer = getGraphicalViewer();
	      viewer.setContents(model);

//	      ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) viewer.getRootEditPart();
//	      FavoritesManagerEditPart managerPart =
//	            (FavoritesManagerEditPart) rootEditPart.getChildren().get(0);
//	      ConnectionLayer connectionLayer = (ConnectionLayer) rootEditPart.getLayer(LayerConstants.CONNECTION_LAYER);
//	      connectionLayer.setConnectionRouter(new ShortestPathConnectionRouter(
//	            managerPart.getFigure()));

	      getGraphicalViewer().addDropTargetListener(
				(TransferDropTargetListener) new TemplateTransferDropTargetListener(
						getGraphicalViewer()));
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);

		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		ClassDiagramFile classDiagramFile = new ClassDiagramFile(file);
		try {
			model = classDiagramFile.read();
			setPartName( file.getName());
			setTitleToolTip( file.getLocation().toString() );
		} catch (Exception e) {
			PluginMessage.openError(getSite().getShell(), CLASS_DIAGRAM_EDITOR_NAME,
					FILE_READ_MESSAGE, e);
		}
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		return ClassDiagramEditorPaletteFactory.createPalette();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		ClassDiagramFile classDiagramFile = new ClassDiagramFile(file);
		try {
			classDiagramFile.save(model, monitor);
			getCommandStack().markSaveLocation();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	@SuppressWarnings("deprecation")
	public void deleteSelection() {
      getActionRegistry().getAction(DeleteAction.ID).run();
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class type) {
		if (type == ClassDiagram.class)
			return model;
		return super.getAdapter(type);
	}

	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void createActions() {
		super.createActions();
		IAction exportAction = new ExportDiagramAction(this);
		getActionRegistry().registerAction(exportAction);
		getSelectionActions().add( exportAction.getId() );
		
		IAction propertiesAction = new ShowPropertiesAction(this);
		getActionRegistry().registerAction(propertiesAction);
		getSelectionActions().add( propertiesAction.getId() );
	}
}
