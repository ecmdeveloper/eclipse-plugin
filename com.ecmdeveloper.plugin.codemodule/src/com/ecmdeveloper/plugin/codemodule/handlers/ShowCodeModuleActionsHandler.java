/**
 * Copyright 2009, Ricardo Belfor
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
package com.ecmdeveloper.plugin.codemodule.handlers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.codemodule.handlers.util.CodeModuleActionLabelProvider;
import com.ecmdeveloper.plugin.codemodule.handlers.util.GetCodeModuleActionsJob;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.core.model.IAction;
import com.ecmdeveloper.plugin.core.util.PluginMessage;

/**
 * This handler handles the
 * <code>com.ecmdeveloper.plugin.showCodeModuleActions</code> command. A dialog
 * showing the actions related to the Code Module is shown.
 * 
 * @author Ricardo.Belfor
 * 
 */
public class ShowCodeModuleActionsHandler extends AbstractHandler implements
		IHandler {

	private static final String NOT_SAVED_MESSAGE = "The Code Module ''{0}'' is not saved yet. Please save before trying to perform this action.";
	private static final String NO_ACTIONS_MESSAGE = "There are no Actions related to the Code Module \"{0}\"";
	private static final String SHOW_ACTIONS_MESSAGE = "The actions related to the Code Module \"{0}\"\n:";
	private static final String SHOW_ACTIONS_DIALOG_TITLE = "Actions";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

			final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			if (window == null)	return null;

			ISelection selection = HandlerUtil.getCurrentSelection(event);

			if (!(selection instanceof IStructuredSelection) || selection.isEmpty() ) {
				return null;
			}
			
			Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
			
			final ArrayList<CodeModuleFile> list = new ArrayList<CodeModuleFile>();
			while ( iterator.hasNext() ) {
				CodeModuleFile codeModuleFile = (CodeModuleFile) iterator.next();
				if ( codeModuleFile.getId() == null ) {
					PluginMessage.openError(window.getShell(), "Show Actions", MessageFormat.format(
							NOT_SAVED_MESSAGE, codeModuleFile.getName()), null);
					return null;
				}

				list.add( codeModuleFile );
			}
			
			GetCodeModuleActionsJob job = new GetCodeModuleActionsJob(list, window.getShell() );
			job.addJobChangeListener( new JobChangeAdapter() {
				
				@Override
				public void done(IJobChangeEvent event) {

					if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
						return;
					}

					GetCodeModuleActionsJob job = (GetCodeModuleActionsJob) event.getJob();
					for ( CodeModuleFile codeModuleFile : list) {
						Collection<IAction> actions = job.getActions(codeModuleFile);
						if ( actions == null ) {
							continue;
						}
						
						if ( actions.isEmpty() ) {
							final String message = MessageFormat.format(
								NO_ACTIONS_MESSAGE, codeModuleFile.getName());

							window.getShell().getDisplay().syncExec(new Runnable() {
								@Override
								public void run() {
									MessageDialog.openInformation( window.getShell(), SHOW_ACTIONS_DIALOG_TITLE, message );
								}
							} );
							continue;
						}

						String message = MessageFormat.format( SHOW_ACTIONS_MESSAGE, codeModuleFile.getName() );
						LabelProvider labelProvider = new CodeModuleActionLabelProvider();
						final ListDialog dialog = new ListDialog( window.getShell() );
						dialog.setContentProvider(new ArrayContentProvider());
						dialog.setLabelProvider( labelProvider );
						dialog.setInput( actions.toArray() );
						dialog.setTitle( SHOW_ACTIONS_DIALOG_TITLE );
						dialog.setMessage(message);

						window.getShell().getDisplay().syncExec(new Runnable() {
							@Override
							public void run() {
								dialog.open();
							}
						} );
					}
				}
			} );

			job.setUser(true);
			job.schedule();

			return null;
	}
}
