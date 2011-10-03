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

package com.ecmdeveloper.plugin.classes.handlers;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.core.model.ClassesManager;
import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IRefreshClassDescriptionTask;

/**
 * @author Ricardo.Belfor
 *
 */
public class RefreshClassDescriptionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if ( !isValidSelection(selection) ) {
			return null;
		}

		ArrayList<IClassDescription> elementsRefreshed = getSelectedClassDescriptions(selection);

		if ( elementsRefreshed.size() > 0) {
			ITaskFactory taskFactory = elementsRefreshed.get(0).getObjectStore().getTaskFactory();
			IRefreshClassDescriptionTask refreshTask = taskFactory.getRefreshClassDescriptionTask( elementsRefreshed.toArray(new IClassDescription[0] ) );
			ClassesManager.getManager().executeTaskASync(refreshTask);
		}
		
		return null;
	}

	private ArrayList<IClassDescription> getSelectedClassDescriptions(ISelection selection) {
		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		
		ArrayList<IClassDescription> selectedClassDescriptions = new ArrayList<IClassDescription>();
		
		while (iterator.hasNext()) {
			IClassDescription selectedObject = (IClassDescription) iterator.next();
			selectedClassDescriptions.add( selectedObject );
		}
		return selectedClassDescriptions;
	}

	private boolean isValidSelection(ISelection selection) {
		if ( selection == null || selection.isEmpty() ) {
			return false;
		}
		
		if ( !(selection instanceof IStructuredSelection) ) {
			return false;
		}
		
		return true;
	}

}
