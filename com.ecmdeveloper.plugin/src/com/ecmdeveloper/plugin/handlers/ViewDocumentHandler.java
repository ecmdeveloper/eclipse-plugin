/**
 * Copyright 2009,2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.jobs.ViewDocumentJob;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;

/**
 * @author Ricardo.Belfor
 *
 */
public class ViewDocumentHandler extends AbstractHandler implements IHandler {

	public static final String ID = "com.ecmdeveloper.plugin.viewDocument";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {
			
			IObjectStoreItem objectStoreItem = (IObjectStoreItem) iterator.next();
			ViewDocumentJob job = new ViewDocumentJob((Document) objectStoreItem, window );
			job.setUser(true);
			job.schedule();
		}

		Activator.getDefault().getContentCache().registerAsListener(window);

		return null;
	}
	
	class DeleteMe implements IPartListener2
	{

		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partHidden(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partOpened(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
