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

package com.ecmdeveloper.plugin.search.actions;

import java.util.HashMap;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.ecmdeveloper.plugin.search.model.QueryElement;
import com.ecmdeveloper.plugin.search.parts.QueryEditPart;

/**
 * @author ricardo.belfor
 *
 */
public class SetMainQueryAction extends SelectionAction {

	public static final String ID = "com.ecmdeveloper.plugin.search.actions.setMainQueryAction";
	public static final String REQUEST_TYPE = "setMainQuery";
	public static final String QUERY_COMPONENT_KEY = "queryComponent";

	private static final String ACTION_NAME = "Set Main Query";

	public SetMainQueryAction(IWorkbenchPart part) {
		super(part);
		setId( ID );
		setText( ACTION_NAME );
	}

	@Override
	protected boolean calculateEnabled() {

		if ( !isSingleItemSelected() ) {
			return false;
		}
		
		Object firstObject = getSelectedObjects().get(0);
		if ( firstObject instanceof QueryEditPart ) {
			QueryEditPart part = (QueryEditPart) firstObject;
			QueryElement queryElement = (QueryElement) part.getModel();
			if ( queryElement.isMainQuery() ) {
				return false;
			}
			
			QueryElement parent = queryElement.getParent();
			if ( parent != null && parent.isRootDiagram() ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void run() {
		execute( createEditCommand() );
	}

	private Command createEditCommand() {
		if ( ! isSingleItemSelected() ) {
			throw new UnsupportedOperationException();
		}
		
		Request request = new Request( REQUEST_TYPE );
		QueryEditPart part = (QueryEditPart) getSelectedObjects().get(0);
		QueryElement queryComponent = (QueryElement) part.getModel();
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put(QUERY_COMPONENT_KEY, queryComponent );
		request.setExtendedData(map );
		return part.getCommand(request);
	}

	private boolean isSingleItemSelected() {
		return getSelectedObjects().size() == 1;
	}
}
