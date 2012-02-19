package com.ecmdeveloper.plugin.codemodule.handlers.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.codemodule.util.PluginMessage;
import com.ecmdeveloper.plugin.core.model.IAction;

public class GetCodeModuleActionsJob extends Job
{
	private static final String HANDLER_NAME = "Code Module Actions";
	private static final String FAILED_MESSAGE = "Getting actions for \"{0}\" failed";
	private static final String MONITOR_MESSAGE = "Getting Actions";
	private static final String PROGRESS_MESSAGE = "Getting Action for \"{0}\"";

	private ArrayList<CodeModuleFile> codeModuleFiles;
	private Map<String,Collection<IAction>> actions;
	private CodeModulesManager codeModulesManager;
	private Shell shell;
	
	public GetCodeModuleActionsJob(ArrayList<CodeModuleFile> codeModuleFiles, Shell shell) {

		super(HANDLER_NAME);
		this.codeModuleFiles = codeModuleFiles;
		actions = new HashMap<String, Collection<IAction>>();
		codeModulesManager = CodeModulesManager.getManager();
		this.shell = shell;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		for ( CodeModuleFile codeModuleFile : codeModuleFiles ) {
			monitor.beginTask( MONITOR_MESSAGE, codeModuleFiles.size() );
			monitor.subTask( MessageFormat.format(PROGRESS_MESSAGE, codeModuleFile.getName() ) );
			if ( ! getCodeModuleActions(codeModuleFile) ) {
				monitor.done();
				return Status.CANCEL_STATUS;
			}
			monitor.worked(1);

			if ( monitor.isCanceled() ) {
				break;
			}
		}
		
		monitor.done();
		return Status.OK_STATUS;
	}

	private boolean getCodeModuleActions(final CodeModuleFile codeModuleFile )
	{
		try {
			actions.put(codeModuleFile.getId(), codeModulesManager
					.getCodeModuleActions(codeModuleFile));
			return true;
		} catch(final Exception e ) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					PluginMessage.openError(shell, HANDLER_NAME, 
							MessageFormat.format(FAILED_MESSAGE, codeModuleFile.getName() ), e );
				}
			} );
			return false;
		}
	}
	public Collection<IAction> getActions(CodeModuleFile codeModuleFile) {
		if ( actions.containsKey(codeModuleFile.getId()) ) {
			return actions.get(codeModuleFile.getId());
		}
		return null;
	}
}
