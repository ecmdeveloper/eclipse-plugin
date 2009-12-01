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
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramAttribute;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramFile;
import com.ecmdeveloper.plugin.diagrams.parts.ClassesEditPartFactory;

/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramEditor extends GraphicalEditorWithFlyoutPalette {

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
		GraphicalViewer viewer = getGraphicalViewer();
	    viewer.setEditPartFactory(new ClassesEditPartFactory() );
	    viewer.setRootEditPart(new ScalableFreeformRootEditPart());
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
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
//		ClassDiagram classDiagram = new ClassDiagram();
//		classDiagram.addClassDiagramClass( new ClassDiagramClass("My Class") );
		//classDiagram.addClassDiagramClass( new ClassDiagramClass("My Second Class") );
//		model = classDiagram;
		
		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		ClassDiagramFile classDiagramFile = new ClassDiagramFile(file);
		try {
			model = classDiagramFile.read(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		ClassDiagramClass classDiagramClass = model.getClassDiagramClasses().get(0);
//		classDiagramClass.setAbstractClass(true);
//		classDiagramClass.addAttribute( new ClassDiagramAttribute("name", "String", null, null, false, false, false ) );
//		classDiagramClass.addAttribute( new ClassDiagramAttribute("id", "Id", null, null, true, false, false ) );
//		classDiagramClass.addAttribute( new ClassDiagramAttribute("color", "Color", null, "*", false, false, true ) );
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		// TODO Auto-generated method stub
		return null;
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
}
