/**
 * Copyright 2009-2012, Ricardo Belfor
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

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class BaseDetailsPage implements IDetailsPage {

	private static final String PERIOD = ".";
	private static final String ACE_DESCRIPTION_2 = "'. This is ";
	private static final String ACE_DESCRIPTION_1 = "Access Control Entry for principal '";
	protected IManagedForm form;
	private Section section;
	protected boolean isDirty;
	private Label titleImage;
	private LabelProvider labelProvider = new SecurityLabelProvider();
	
	@Override
	public void initialize(IManagedForm form) {
		this.form = form;
	}
	
	@Override
	public void createContents(Composite parent) {
		setParentLayout(parent);
		Section section = createSection(parent);
		Composite client = createClient(section);
		createClientContent(client);
		section.setClient(client);
	}

	protected void createClientContent(Composite client) {
	}

	protected abstract int getNumClientColumns();

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	protected void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	@Override
	public void commit(boolean onSave) {
		setDirty(false);
	}


	private Section createSection(Composite parent) {
		section = form.getToolkit().createSection(parent,
				Section.DESCRIPTION | Section.TITLE_BAR );

		titleImage = new Label(section, SWT.NONE);
		section.setTextClient(titleImage);
		return section;
	}
	
	private Composite createClient(Section section) {
		Composite client = form.getToolkit().createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = getNumClientColumns();
		client.setLayout(layout);
		
		form.getToolkit().paintBordersFor(client);
		
		return client;
	}

	private void setParentLayout(Composite parent) {
		FillLayout fillLayout = new FillLayout( SWT.HORIZONTAL | SWT.VERTICAL );
		fillLayout.marginHeight = 5;
		fillLayout.marginWidth = 5;
		parent.setLayout( fillLayout );
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		Object object = getSelection(selection);
		
	    if ( object != null ) {
	    	
	    	ISecurityPrincipal securityPrincipal = getSelectedPrincipal(object);

	    	if ( securityPrincipal != null ) {
		    	setPrincipalInformation(securityPrincipal);
	    	}
	    }
	}

	protected void setPrincipalInformation(ISecurityPrincipal securityPrincipal) {
		setTitle( "Principal" );
		setDescription( labelProvider.getText(securityPrincipal) );
		setTitleImage(securityPrincipal);
	}

	protected void setAccessControlEntryInformation(IAccessControlEntry accessControlEntry ) {
		setTitle("Access Control Entry");
		ISecurityPrincipal securityPrincipal = accessControlEntry.getPrincipal();

		if ( securityPrincipal != null ) {
			String description = getAccessControlEntryDescription(securityPrincipal, accessControlEntry);
			setDescription( description);
			setTitleImage(accessControlEntry);
		}
	}
	
	protected String getAccessControlEntryDescription(ISecurityPrincipal securityPrincipal, IAccessControlEntry accessControlEntry) {
		StringBuilder description = new StringBuilder();
		
		description.append(ACE_DESCRIPTION_1);
		description.append(securityPrincipal.getDisplayName() );
		description.append(ACE_DESCRIPTION_2);
		description.append(accessControlEntry.getSource().toString());
		description.append(PERIOD);
		
		return description.toString();
	}
	
	protected void setTitleImage(Object object) {
		titleImage.setImage( labelProvider.getImage(object)  );
	}
	
	private ISecurityPrincipal getSelectedPrincipal(Object object) {
		ISecurityPrincipal securityPrincipal = null;
		if ( object instanceof ISecurityPrincipal ) {
			securityPrincipal = (ISecurityPrincipal) object;
		} else if ( object instanceof IAccessControlEntry ) {
			securityPrincipal = ((IAccessControlEntry) object).getPrincipal();
		}
		return securityPrincipal;
	}

	protected Object getSelection(ISelection selection) {

		if ( selection.isEmpty() ) {
	    	return null;
	    }
		
		if ( selection instanceof IStructuredSelection ) {
			Iterator<?> iterator = ((IStructuredSelection)selection).iterator();
			return iterator.next();
	    }
		return null;
	}

	protected void setTitle(String title ) {
		section.setText(title);
	}
	
	protected void setDescription(String description) {
		section.setDescription(description);
	}
}
