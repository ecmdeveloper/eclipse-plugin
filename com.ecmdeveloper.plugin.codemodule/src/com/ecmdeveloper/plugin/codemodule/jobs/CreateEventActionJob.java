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

package com.ecmdeveloper.plugin.codemodule.jobs;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.codemodule.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class CreateEventActionJob extends Job {

	private static final String MONITOR_MESSAGE = "Creating Event Action";
	private static final String JOB_NAME = "Create Event Action";

	private CodeModuleFile codeModuleFile;
	private String name;
	private String className;
	private boolean enabled;
	private Shell shell;

	public CreateEventActionJob(CodeModuleFile codeModuleFile, String name, String className,
			boolean enabled, Shell shell) {
		super(JOB_NAME);
		this.codeModuleFile = codeModuleFile;
		this.name = name;
		this.className = className;
		this.enabled = enabled;
		this.shell = shell;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask( MONITOR_MESSAGE, 1 );
			CodeModulesManager codeModulesManager = CodeModulesManager.getManager();
			codeModulesManager.createEventAction(codeModuleFile, name, className, enabled);
			monitor.done();
			return Status.OK_STATUS;
		} catch(final Exception e ) {
			String message = MessageFormat.format("Creating Event Action \"{0}\" failed", name );
			PluginMessage.openErrorFromThread(shell, JOB_NAME, message, e);
			return Status.CANCEL_STATUS;
		}
	}
}
