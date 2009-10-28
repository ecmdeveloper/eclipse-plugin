package com.ecmdeveloper.plugin.handlers;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressService;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.DeleteTask;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class DeleteObjectStoreItemHandler extends AbstractHandler implements IHandler {

	private static final String MONITOR_MESSAGE = Messages.DeleteObjectStoreItemHandler_MonitorMessage;
	private static final String PROGRESS_MESSAGE = Messages.DeleteObjectStoreItemHandler_ProgressMessage;
	private static final String FAILED_MESSAGE = Messages.DeleteObjectStoreItemHandler_FailedMessage;
	private static final String HANDLER_NAME = Messages.DeleteObjectStoreItemHandler_HandlerName;
	private static final String DELETE_MESSAGE = Messages.DeleteObjectStoreItemHandler_DeleteMessage;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		final ArrayList<IObjectStoreItem> itemsDeleted = new ArrayList<IObjectStoreItem>();
		
		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {

			Object elem = iterator.next();

			if ( !( elem instanceof IObjectStoreItem ) ) {
				continue;
			}

			IObjectStoreItem objectStoreItem = (IObjectStoreItem)elem;
			
			if ( ! objectStoreItem.hasChildren() ) {

				String name = objectStoreItem.getName();
				boolean answerTrue = MessageDialog.openQuestion(window
						.getShell(), HANDLER_NAME, MessageFormat.format(
						DELETE_MESSAGE, name) );
				if (answerTrue) {
					itemsDeleted.add(objectStoreItem);
				}
			} else {
				// TODO: show a new confirmation dialog
				MessageDialog.openInformation( window.getShell(), HANDLER_NAME, "Deleting objects with children is not yet supported" ); //$NON-NLS-1$
			}
		}

		Job deleteJob = new DeleteJob(itemsDeleted, window.getShell() );
		deleteJob.setUser(true);
		deleteJob.schedule();
		
		return null;
	}
	
	class DeleteJob extends Job
	{
		private ArrayList<IObjectStoreItem> itemsDeleted;
		private Shell shell;
		public DeleteJob(ArrayList<IObjectStoreItem> itemsDeleted, Shell shell) {
			super(HANDLER_NAME);
			this.itemsDeleted = itemsDeleted;
			this.shell = shell;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			try {

				int worked = 0;
				for ( IObjectStoreItem objectStoreItem : itemsDeleted ) {
					
					monitor.beginTask( MONITOR_MESSAGE, itemsDeleted.size() );
					monitor.subTask( MessageFormat.format(PROGRESS_MESSAGE, objectStoreItem.getName() ) );
					deleteItem( objectStoreItem );
					monitor.worked(++worked);

					if ( monitor.isCanceled() ) {
						break;
					}
				}
				
				return Status.OK_STATUS;
			} finally {
				monitor.done();
			}
		}

		private void deleteItem(final IObjectStoreItem objectStoreItem ) {

			try {
				DeleteTask deleteTask = new DeleteTask( new IObjectStoreItem[] { objectStoreItem }, true);
				ObjectStoresManager.getManager().executeTaskSync( deleteTask);
			} catch(final Exception e ) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						PluginMessage.openError(shell, HANDLER_NAME, 
								MessageFormat.format(FAILED_MESSAGE, objectStoreItem.getName() ), e );
					}
				} );
			}
		}
	}
}
