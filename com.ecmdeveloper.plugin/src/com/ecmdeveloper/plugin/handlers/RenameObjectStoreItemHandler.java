package com.ecmdeveloper.plugin.handlers;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressService;

import com.ecmdeveloper.plugin.handlers.DeleteObjectStoreItemHandler.DeleteJob;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.DeleteTask;
import com.ecmdeveloper.plugin.model.tasks.UpdateTask;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class RenameObjectStoreItemHandler extends AbstractHandler implements IHandler {

	private static final String PROGRESS_MESSAGE = Messages.RenameObjectStoreItemHandler_ProgressMessage;
	private static final String MONITOR_MESSAGE = Messages.RenameObjectStoreItemHandler_MonitorMessage;
	private static final String NEW_NAME_MESSAGE = Messages.NewNameMessage;
	private static final String HANDLER_NAME = Messages.HandlerName;
	private static final String INVALID_CHARS_MESSAGE = Messages.RenameObjectStoreItemHandler_InvalidCharsMessage;
	private static final String INVALID_LENGTH_MESSAGE = Messages.RenameObjectStoreItemHandler_InvalidLengthMessage;
	private static final String FAILED_MESSAGE = Messages.RenameObjectStoreItemHandler_FailedMessage;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		final ArrayList<IObjectStoreItem> itemsRenamed = new ArrayList<IObjectStoreItem>();
		
		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {

			Object elem = iterator.next();

			if (!(elem instanceof IObjectStoreItem)) {
				continue;
			}

			IObjectStoreItem objectStoreItem = (IObjectStoreItem)elem; 
			String oldName = objectStoreItem.getName();
			
			// Create the name validator
			
			IInputValidator inputValidator = null;
			if ( objectStoreItem instanceof Folder ) {
				inputValidator = new FolderNameValidator();	
			} else if ( objectStoreItem instanceof Document ) {
				inputValidator = new DocumentNameValidator();
			}
			
			InputDialog inputDialog = new InputDialog( window.getShell(), HANDLER_NAME, NEW_NAME_MESSAGE, oldName, inputValidator );
			int open = inputDialog.open();
			
			if ( open == InputDialog.OK ) {
				objectStoreItem.setName( inputDialog.getValue() );
				itemsRenamed.add(objectStoreItem);
			}
		}

		if ( itemsRenamed.isEmpty() ) {
			return null;
		}
		
		Job renameJob = new RenameJob(itemsRenamed, window.getShell() );
		renameJob.setUser(true);
		renameJob.schedule();
		
		return null;
	}

	class RenameJob extends Job
	{
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
				int worked = 0;
				for ( IObjectStoreItem objectStoreItem : itemsRenamed ) {
					
					monitor.beginTask( MONITOR_MESSAGE, itemsRenamed.size() );
					monitor.subTask( MessageFormat.format(PROGRESS_MESSAGE, objectStoreItem.getName() ) );
					updateItem( objectStoreItem );
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

	class FolderNameValidator implements IInputValidator
	{
//		private static final String NAME_INVALID_CHARS = "\\/*:\"<>|?"; //$NON-NLS-1$
		private static final String NAME_INVALID_CHARS = ""; //$NON-NLS-1$
		private static final int MAX_NAME_LENGTH = 64;
		
		@Override
		public String isValid(String newText) {

			if ( newText.length() > MAX_NAME_LENGTH ) {
				return MessageFormat.format(INVALID_LENGTH_MESSAGE, MAX_NAME_LENGTH );
			}
			for ( byte invalidChar : NAME_INVALID_CHARS.getBytes() ) {
				if ( newText.indexOf(invalidChar) > 0 ) {
					return MessageFormat.format( INVALID_CHARS_MESSAGE,  NAME_INVALID_CHARS );
				}
			}
			return null;
		}
	}

	class DocumentNameValidator implements IInputValidator
	{
		private static final int MAX_NAME_LENGTH = 64;
		
		@Override
		public String isValid(String newText) {

			if ( newText.length() > MAX_NAME_LENGTH ) {
				return MessageFormat.format(INVALID_LENGTH_MESSAGE, MAX_NAME_LENGTH );
			}
			return null;
		}
	}
}
