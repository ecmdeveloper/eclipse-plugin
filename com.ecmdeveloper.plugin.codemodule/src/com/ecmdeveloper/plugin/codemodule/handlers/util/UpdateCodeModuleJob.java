/**
 * 
 */
package com.ecmdeveloper.plugin.codemodule.handlers.util;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.codemodule.util.Messages;
import com.ecmdeveloper.plugin.codemodule.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class UpdateCodeModuleJob extends Job {

	private static final String MONITOR_MESSAGE = "Updating Code Module";
	private static final String PROGRESS_MESSAGE = "Updating Code Module \"{0}\"";
	private static final String HANDLER_NAME = Messages.UpdateCodeModuleHandler_HandlerName;

	private CodeModuleFile codeModuleFile;
	private Object[] actions;
	private CodeModulesManager codeModulesManager;
	private Shell shell;

	public UpdateCodeModuleJob(CodeModuleFile codeModuleFile, Object[] actions, Shell shell) {
		super(HANDLER_NAME);

		this.codeModuleFile = codeModuleFile;
		this.actions = actions;
		codeModulesManager = CodeModulesManager.getManager();
		this.shell = shell;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		monitor.beginTask( MONITOR_MESSAGE, 1 );
		monitor.subTask( MessageFormat.format(PROGRESS_MESSAGE, codeModuleFile.getName() ) );
		if ( ! updateCodeModule() ) {
			monitor.done();
			return Status.CANCEL_STATUS;
		}
		monitor.worked(1);
		monitor.done();
		return Status.OK_STATUS;
	}

	private boolean updateCodeModule()
	{
		try {
			codeModulesManager.updateCodeModule(codeModuleFile, actions );
			return true;
		} catch(final Exception e ) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					PluginMessage.openError(shell, HANDLER_NAME, 
							MessageFormat.format("Updating Code Module \"{0}\" failed", codeModuleFile.getName() ), e );
				}
			} );
			return false;
		}
	}

	public CodeModuleFile getCodeModuleFile() {
		return codeModuleFile;
	}

	public Shell getShell() {
		return shell;
	}
}
