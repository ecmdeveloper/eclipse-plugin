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

import java.util.HashMap;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

import com.ecmdeveloper.plugin.diagrams.Activator;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;
import com.ecmdeveloper.plugin.diagrams.parts.ClassDiagramClassEditPart;
import com.ecmdeveloper.plugin.diagrams.util.IconFiles;

/**
 * @author Ricardo.Belfor
 *
 */
public class RefreshDiagramClassAction  extends SelectionAction {

	public static final String CLASS_DIAGRAM_CLASS_KEY = "classDiagramClass";
	public static final String ID = "com.ecmdeveloper.plugin.diagrams.actions.refreshDiagramClassAction";
	public static final String REQUEST_TYPE = "refreshClassDiagramClass";
	
	private static final String ACTION_NAME = "Refresh Class Diagram Class";

	public RefreshDiagramClassAction(IWorkbenchPart part) {
		super(part);
		setId( ID );
		setText( ACTION_NAME );
	}

	@Override
	protected boolean calculateEnabled() {
		if ( !isSingleItemSelected() || !isClassDiagramClassSelected()) {
			return false;
		}
		return true;
	}

	private boolean isSingleItemSelected() {
		return getSelectedObjects().size() == 1;
	}

	private boolean isClassDiagramClassSelected() {
		return (getSelectedObjects().get(0) instanceof ClassDiagramClassEditPart);
	}

	private Command createRefreshDiagramClassCommand() {
		if ( ! isSingleItemSelected() || !isClassDiagramClassSelected() ) {
			throw new UnsupportedOperationException();
		}
		
		Request request = new Request( REQUEST_TYPE );
		ClassDiagramClassEditPart part = (ClassDiagramClassEditPart) getSelectedObjects().get(0);
		ClassDiagramClass classDiagramClass = part.getClassDiagramClass();
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put(CLASS_DIAGRAM_CLASS_KEY, classDiagramClass );
		request.setExtendedData(map );
		return part.getCommand(request);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptor( IconFiles.CLASS_REFRESH_ICON );
	}

	@Override
	public void run() {
		execute( createRefreshDiagramClassCommand() );
	}
}
