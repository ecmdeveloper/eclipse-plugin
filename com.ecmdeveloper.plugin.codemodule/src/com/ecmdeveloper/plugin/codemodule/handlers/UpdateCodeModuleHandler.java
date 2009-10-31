/**
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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.codemodule.handlers.util.CodeModuleActionLabelProvider;
import com.ecmdeveloper.plugin.codemodule.handlers.util.GetCodeModuleActionsJob;
import com.ecmdeveloper.plugin.codemodule.handlers.util.UpdateCodeModuleJob;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.util.Messages;
import com.ecmdeveloper.plugin.model.Action;

/**
 * @author Ricardo Belfor
 *
 */
public class UpdateCodeModuleHandler extends AbstractHandler implements IHandler {

	private static final String SELECT_ACTIONS_MESSAGE = Messages.UpdateCodeModuleHandler_SelectActionsMessage;
	private static final String UPDATE_MESSAGE = Messages.UpdateCodeModuleHandler_UpdateMessage;
	private static final String HANDLER_NAME = Messages.UpdateCodeModuleHandler_HandlerName;
	private static final String ACTION_SELECTION_DIALOG_TITLE = Messages.UpdateCodeModuleHandler_ActionSelectionDialogTitle;

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
			list.add( (CodeModuleFile) iterator.next() );
		}
		
		GetCodeModuleActionsJob job = new GetCodeModuleActionsJob( list, window.getShell() );
		job.addJobChangeListener( new GetCodeModuleActionsJobListener( list, window.getShell() ) );
		job.setUser(true);
		job.schedule();

		return null;
	}
	
	class GetCodeModuleActionsJobListener extends JobChangeAdapter {
		
		public GetCodeModuleActionsJobListener(ArrayList<CodeModuleFile> list, Shell shell) {
			super();
			this.list = list;
			this.shell = shell;
		}

		ArrayList<CodeModuleFile> list;
		Shell shell;
		
		@Override
		public void done(IJobChangeEvent event) {

			if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
				return;
			}

			GetCodeModuleActionsJob job = (GetCodeModuleActionsJob) event.getJob();

			for ( final CodeModuleFile codeModuleFile : list) {
				Collection<Action> actions = job.getActions(codeModuleFile);
				if ( actions == null ) {
					continue;
				}
				
				String message = MessageFormat.format( SELECT_ACTIONS_MESSAGE, codeModuleFile.getName() );
				LabelProvider labelProvider = new CodeModuleActionLabelProvider();
				final ListSelectionDialog dialog = new ListSelectionDialog(shell, actions, new ArrayContentProvider(), labelProvider, message );
				dialog.setInitialSelections( actions.toArray() );
				dialog.setTitle( ACTION_SELECTION_DIALOG_TITLE );

				shell.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						if ( dialog.open() == Dialog.OK ) {
							UpdateCodeModuleJob updateCodeModuleJob = new UpdateCodeModuleJob(
									codeModuleFile, dialog.getResult(),
									shell );
							updateCodeModuleJob.addJobChangeListener( new UpdateCodeModuleJobListener() );
							updateCodeModuleJob.setUser(true);
							updateCodeModuleJob.schedule();
						}
					}
				} );
			}
		}
	}
	
	class UpdateCodeModuleJobListener extends JobChangeAdapter {

		@Override
		public void done(IJobChangeEvent event) {

			if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
				return;
			}

			final UpdateCodeModuleJob job = (UpdateCodeModuleJob) event.getJob();
			
			job.getShell().getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					MessageDialog.openInformation(job.getShell(),
							HANDLER_NAME, MessageFormat.format(
									UPDATE_MESSAGE,
									job.getCodeModuleFile().getName()).toString() );			
				}
			} );
		}
	}
}
