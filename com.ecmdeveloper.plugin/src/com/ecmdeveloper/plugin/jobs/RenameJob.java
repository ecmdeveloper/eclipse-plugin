package com.ecmdeveloper.plugin.jobs;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.UpdateTask;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.util.PluginMessage;

public class RenameJob extends Job {

	private static final String HANDLER_NAME = Messages.HandlerName;
	private static final String MONITOR_MESSAGE = Messages.RenameObjectStoreItemHandler_MonitorMessage;
	private static final String PROGRESS_MESSAGE = Messages.RenameObjectStoreItemHandler_ProgressMessage;
	private static final String FAILED_MESSAGE = Messages.RenameObjectStoreItemHandler_FailedMessage;

	private ArrayList<IObjectStoreItem> itemsRenamed;
	private Shell shell;

	public RenameJob(ArrayList<IObjectStoreItem> itemsRenamed, Shell shell) {
		super(HANDLER_NAME);
		this.itemsRenamed = itemsRenamed;
		this.shell = shell;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {
			monitor.beginTask( MONITOR_MESSAGE, itemsRenamed.size() );
			for ( IObjectStoreItem objectStoreItem : itemsRenamed ) {
				monitor.subTask( MessageFormat.format(PROGRESS_MESSAGE, objectStoreItem.getName() ) );
				updateItem( objectStoreItem );
				monitor.worked(1);

				if ( monitor.isCanceled() ) {
					break;
				}
			}
			
			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}
	}

	private void updateItem(final IObjectStoreItem objectStoreItem ) {

		try {
			UpdateTask updateTask = new UpdateTask( itemsRenamed.toArray( new ObjectStoreItem[ itemsRenamed.size() ] ) );
			ObjectStoresManager.getManager().executeTaskSync( updateTask );
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
