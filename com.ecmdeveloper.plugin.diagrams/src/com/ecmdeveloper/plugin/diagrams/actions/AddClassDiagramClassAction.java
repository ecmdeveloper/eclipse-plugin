/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.actions;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.diagrams.editors.ClassDiagramClassFactory;
import com.ecmdeveloper.plugin.diagrams.editors.ClassDiagramEditor;
import com.ecmdeveloper.plugin.diagrams.parts.ClassDiagramEditPart;

/**
 * @author Ricardo.Belfor
 *
 */
public class AddClassDiagramClassAction extends SelectionAction {

	public static final String ID = "com.ecmdeveloper.plugin.diagrams.actions.addClassDiagramClassAction";
	public static final String REQUEST_TYPE = "addClassDiagramClass";

	private static final String ACTION_NAME = "Add Class";

	private ClassDescription classDescription;

	public AddClassDiagramClassAction(IWorkbenchPart part) {
		super(part);
		setId( ID );
		setText( ACTION_NAME );
	}

	public void setClassDescription(ClassDescription classDescription ) {
		this.classDescription = classDescription;
	}
	
	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	private Command createClassDiagramClassCreateCommand() {
		
		CreateRequest createRequest = new CreateRequest(REQUEST_TYPE);
		createRequest.setFactory( new ClassDiagramClassFactory( classDescription) );
		createRequest.setLocation( new Point(20,20) );
		GraphicalViewer graphicalViewer = ((ClassDiagramEditor)getWorkbenchPart()).getViewer();
		ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getRootEditPart();
		if ( ! rootEditPart.getChildren().isEmpty() && rootEditPart.getChildren().get(0) instanceof ClassDiagramEditPart ) {
			EditPart editPart = (EditPart) rootEditPart.getChildren().get(0);
			return editPart.getCommand(createRequest);
		} else {
			return UnexecutableCommand.INSTANCE;
		}
	}

	@Override
	public void run() {
		execute(createClassDiagramClassCreateCommand());
	}
}
