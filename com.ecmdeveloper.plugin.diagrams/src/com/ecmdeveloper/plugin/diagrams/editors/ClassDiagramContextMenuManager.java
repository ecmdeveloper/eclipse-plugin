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

package com.ecmdeveloper.plugin.diagrams.editors;

import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

import com.ecmdeveloper.plugin.diagrams.actions.ExportDiagramAction;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramContextMenuManager extends MenuManager {

	private ActionRegistry actionRegistry;

	public ClassDiagramContextMenuManager(ActionRegistry actionRegistry) {
		this.actionRegistry = actionRegistry;
		
		add( getAction(ActionFactory.UNDO.getId() ) );
		add( getAction(ActionFactory.REDO.getId() ) );
		add( getAction(ActionFactory.DELETE.getId() ) );
		add(new Separator());
		add( getAction( ExportDiagramAction.ID) );
	}

	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}
	
}
