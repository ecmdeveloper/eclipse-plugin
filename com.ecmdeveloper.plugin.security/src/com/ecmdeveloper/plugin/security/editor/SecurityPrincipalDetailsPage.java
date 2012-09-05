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

package com.ecmdeveloper.plugin.security.editor;

import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;
import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.jobs.GetMembersJob;
import com.ecmdeveloper.plugin.security.jobs.GetMembershipsJob;
import com.ecmdeveloper.plugin.security.util.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityPrincipalDetailsPage extends BaseDetailsPage {

	private static final String MEMBERSHIPS_MESSAGE = "Memberships of {1} {0}.";
	private static final String MEMBERS_MESSAGE = "Members of group {0}.";
	private static final String SHOW_MEMBERSHIPS = "Show Memberships";
	private static final String SHOW_MEMBERS = "Show Members";
	private static final String EMPTY_STRING = "";
	private Label nameLabel;
	private ImageHyperlink showMembersLink;
	private ImageHyperlink showMembershipsLink;
	private Label displayNameLabel;
	private final Shell shell;
	private ISecurityPrincipal securityPrincipal;

	public SecurityPrincipalDetailsPage(Shell shell) {
		this.shell = shell;
	}

	@Override
	protected int getNumClientColumns() {
		return 2;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean setFormInput(Object input) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void createClientContent(Composite client) {

//		Color color1 = new Color(Display.getCurrent(), 0, 128, 0 );
//		client.setBackground(color1);
		
		Composite linksClient = createLinksClient(client);
		createShowMembershipsLink(linksClient);
		createShowMembersLink(linksClient);
		
		form.getToolkit().createLabel(client, "Name: ");
		nameLabel = form.getToolkit().createLabel(client, EMPTY_STRING, SWT.WRAP );
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);

		form.getToolkit().createLabel(client, "Display Name: ");
		displayNameLabel = form.getToolkit().createLabel(client, EMPTY_STRING, SWT.WRAP );
		displayNameLabel.setLayoutData(layoutData);
	}

	private void createShowMembershipsLink(Composite linksClient) {
		showMembershipsLink = form.getToolkit().createImageHyperlink(linksClient, SWT.WRAP);
		showMembershipsLink.setText(SHOW_MEMBERSHIPS);
		showMembershipsLink.setImage( Activator.getImage(IconFiles.MEMBERSHIPS) );
		showMembershipsLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {

				GetMembershipsJob job = new GetMembershipsJob(securityPrincipal, shell);
				job.setUser(true);
				job.addJobChangeListener( new JobChangeAdapter() {

					@Override
					public void done(IJobChangeEvent event) {
						GetMembershipsJob job2 = (GetMembershipsJob) event.getJob();
						if ( job2.getResult().isOK() ) {
							String message = MessageFormat.format(MEMBERSHIPS_MESSAGE, securityPrincipal.getDisplayName(), securityPrincipal.isGroup() ? "group" : "user" );
							showPrincipalsList( job2.getMemberships(), "Show Memberships",  message );
						}
					}

				} );
				job.schedule();
			}
	    });
	}

	private void showPrincipalsList(final Collection<IPrincipal> members, final String title, final String message) {
		
		shell.getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new SecurityLabelProvider() );
				dialog.setTitle(title);
				dialog.setMessage(message);
				dialog.setElements( members.toArray() );
				dialog.open();
			}
		} );
	}

	private void createShowMembersLink(Composite linksClient) {
		showMembersLink = form.getToolkit().createImageHyperlink(linksClient, SWT.WRAP);
		showMembersLink.setText(SHOW_MEMBERS);
		showMembersLink.setImage( Activator.getImage(IconFiles.MEMBERS) );
		showMembersLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {

				GetMembersJob job = new GetMembersJob(securityPrincipal, shell);
				job.setUser(true);
				job.addJobChangeListener( new JobChangeAdapter() {

					@Override
					public void done(IJobChangeEvent event) {
						GetMembersJob job2 = (GetMembersJob) event.getJob();
						if ( job2.getResult().isOK() ) {
							String message = MessageFormat.format(MEMBERS_MESSAGE, securityPrincipal.getDisplayName());
							showPrincipalsList( job2.getMembers(), "Show Members", message );
						}
					}

				} );
				job.schedule();
			}
	    });
	}

	private Composite createLinksClient(Composite client) {
		Composite buttons = new Composite(client, SWT.NONE );
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.HORIZONTAL;
		fillLayout.spacing = 10;
		buttons.setLayout(fillLayout);
		
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = getNumClientColumns();
		buttons.setLayoutData(layoutData);
		
		return buttons;
	}
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		
		super.selectionChanged(part, selection);
		
		Object object = getSelection(selection);
		
	    if ( object != null ) {
	    	
			if ( object instanceof ISecurityPrincipal ) {
				securityPrincipal = (ISecurityPrincipal) object;
				nameLabel.setText( securityPrincipal.getName() );
				nameLabel.pack(true);

				displayNameLabel.setText( securityPrincipal.getDisplayName() );
				displayNameLabel.pack(true);

				showMembersLink.setVisible(securityPrincipal.isGroup() );
				showMembershipsLink.setVisible(securityPrincipal.isGroup() || securityPrincipal.isUser() );
			}
	    }
	}

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);
	}
}
