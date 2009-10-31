/**
 * 
 */
package com.ecmdeveloper.plugin.codemodule.handlers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.codemodule.handlers.util.CodeModuleActionLabelProvider;
import com.ecmdeveloper.plugin.codemodule.handlers.util.GetCodeModuleActionsJob;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.codemodule.util.PluginMessage;
import com.ecmdeveloper.plugin.model.Action;

/**
 * @author Ricardo.Belfor
 *
 */
public class ShowCodeModuleActionsHandler extends AbstractHandler implements
		IHandler {

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
				list.add( (CodeModuleFile) iterator.next() );
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
						Collection<Action> actions = job.getActions(codeModuleFile);
						if ( actions == null ) {
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
