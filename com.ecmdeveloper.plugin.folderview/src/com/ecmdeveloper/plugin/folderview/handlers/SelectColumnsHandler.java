/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.folderview.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.folderview.dialogs.ConfigureColumnsDialog;
import com.ecmdeveloper.plugin.folderview.views.FolderView;

/**
 * @author ricardo.belfor
 *
 */
public class SelectColumnsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		FolderView folderView = getActiveFolderView(event);
		
		ConfigureColumnsDialog dialog = new ConfigureColumnsDialog(folderView.getSite().getShell(),
				folderView.getVisibleColumns(), folderView.getHiddenColumns(), folderView.getGroupByColumns() );
		
		if ( dialog.open() == Dialog.OK ) {
			folderView.setVisibleColumns( dialog.getVisibleColumns() );
			folderView.setHiddenColumns( dialog.getHiddenColumns() );
			folderView.setGroupByColumns( dialog.getGroupByColumns() );
			
			folderView.updateColumns();
		}
		
		return null;
	}

	private FolderView getActiveFolderView(ExecutionEvent event) {
		FolderView folderView = null;
		final IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part != null && part instanceof FolderView ) {
			folderView = (FolderView) part;
		}
		return folderView;
	}
}
