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

package com.ecmdeveloper.plugin.security.jobs;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.IRealm;
import com.ecmdeveloper.plugin.core.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class GetMembershipsJob extends Job {

	private static final String TITLE = "Show Memberships";
	private static final String NO_MEMBERSHIPS_MESSAGE = "This princial has no memberships";
	private final IPrincipal principal;
	private final Shell shell;
	private Collection<IPrincipal> memberships;
	
	public GetMembershipsJob(IPrincipal principal, Shell shell) {
		super(TITLE);
		this.principal = principal;
		this.shell = shell;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
	
		IRealm realm = principal.getRealm();
		if ( realm == null ) {
			showNoMembershipsMessage();
			return Status.OK_STATUS;
		}
	
		try {
			monitor.beginTask("Fetching Memberships", IProgressMonitor.UNKNOWN);
			memberships = realm.getMemberships(principal);
			monitor.done();
		} catch (Exception e) {
			PluginMessage.openErrorFromThread(TITLE, e.getLocalizedMessage(), e);
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}

	private void showNoMembershipsMessage() {
		shell.getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(shell, TITLE, NO_MEMBERSHIPS_MESSAGE );
			}
		} );
	}

	public Collection<IPrincipal> getMemberships() {
		return memberships;
	}
}
