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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;
import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.util.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityPrincipalDetailsPage extends BaseDetailsPage {

	private static final String EMPTY_STRING = "";
	private Label nameLabel;
	private ImageHyperlink showMembersLink;

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
//		super.createClientContent(client);
		Composite linksClient = createLinksClient(client);
		
		showMembersLink = form.getToolkit().createImageHyperlink(linksClient, SWT.WRAP);
		showMembersLink.setText("Show Members");
		showMembersLink.setImage( Activator.getImage(IconFiles.GROUP) );
		showMembersLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
			}
	    });
		
		ImageHyperlink showMembershipsLink = form.getToolkit().createImageHyperlink(linksClient, SWT.WRAP);
		showMembershipsLink.setText("Show Members");
		showMembershipsLink.setImage( Activator.getImage(IconFiles.MEMBERSHIPS) );
		showMembershipsLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
			}
	    });
		
		FormToolkit toolkit = form.getToolkit();


		form.getToolkit().createLabel(client, "Name: ");
		nameLabel = form.getToolkit().createLabel(client, EMPTY_STRING);

		
//		GridData layoutData = new GridData();
//		layoutData.horizontalSpan = getNumClientColumns();
//		showMembersLink.setLayoutData(layoutData);
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
				ISecurityPrincipal securityPrincipal = (ISecurityPrincipal) object;
				nameLabel.setText( securityPrincipal.getName() );
				
				showMembersLink.setVisible(securityPrincipal.isGroup() );
			}
	    }
	}

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);
	}
}
