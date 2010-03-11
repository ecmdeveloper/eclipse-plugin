/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.editors;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramEditorContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;

	public ClassDiagramEditorContextMenuProvider(EditPartViewer viewer, ActionRegistry actionRegistry ) {
		super(viewer);
		this.actionRegistry = actionRegistry;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		
//		GEFActionConstants.addStandardActionGroups(menu);
		
		menu.add( getAction(ActionFactory.UNDO.getId() ) );
		menu.add( getAction(ActionFactory.REDO.getId() ) );
		menu.add( getAction(ActionFactory.DELETE.getId() ) );
//		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, getAction(ActionFactory.UNDO.getId()));
//		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, getAction(ActionFactory.REDO.getId()));
//		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, getAction(ActionFactory.DELETE.getId()));
	}

	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}

}
