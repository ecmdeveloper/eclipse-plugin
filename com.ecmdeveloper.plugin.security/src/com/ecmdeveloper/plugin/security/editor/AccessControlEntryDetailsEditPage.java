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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntryType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IAccessRight;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;

/**
 * @author ricardo.belfor
 *
 */
public class AccessControlEntryDetailsEditPage extends BaseDetailsPage {

	private static final String DENY_LABEL = "Deny";
	private static final String ALLOW_LABEL = "Allow";
	
	private Button allowButton;
	private Button denyButton;
	private ComboViewer levelCombo;
	
	private IAccessControlEntry accessControlEntry;
	private AccessRightsTable accessRightsTable;
	
	protected void createClientContent(Composite client) {
		super.createClientContent(client);
		
		FormToolkit toolkit = form.getToolkit();
		
		createTypeControls(client, toolkit);
		createApplyToControls(client);
		createLevelControls(client);
		createAccessRightsTable(client);
	}

	private void createAccessRightsTable(Composite client) {

		accessRightsTable = new AccessRightsTable(client, form, getNumClientColumns() ) {
			@Override
			protected void accessRightChanged(IAccessRight accessRight, boolean checked ) {
				setDirty(true);
				accessRight.setGranted( checked );
			}
		};
	}

	private void createTypeControls(Composite client, FormToolkit toolkit) {
		toolkit.createLabel(client, "Type: ");
		createAllowButton(client, toolkit);
		createDenyButton(client, toolkit);
		form.getToolkit().createLabel(client, "");
	}

	private void createAllowButton(Composite client, FormToolkit toolkit) {
		allowButton = toolkit.createButton(client, ALLOW_LABEL, SWT.RADIO );
		allowButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				accessControlEntry.setType(AccessControlEntryType.ALLOW );
				setDirty(true);
			}
		} );
	}

	private void createDenyButton(Composite client, FormToolkit toolkit) {
		denyButton = toolkit.createButton(client, DENY_LABEL, SWT.RADIO );
		denyButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				accessControlEntry.setType(AccessControlEntryType.DENY );
				setDirty(true);
			}
		} );
	}

	private void createLevelControls(Composite container) {

		form.getToolkit().createLabel(container, "Level: ");
		
		levelCombo = new ComboViewer(container, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		levelCombo.setContentProvider(new ArrayContentProvider());
		levelCombo.setLabelProvider(new LabelProvider());
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		levelCombo.getCombo().setLayoutData(layoutData);
		levelCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
			}
		}
		);
		
	}

	private void createApplyToControls(Composite container) {

		form.getToolkit().createLabel(container, "Apply To: ");

		ComboViewer comboViewer = new ComboViewer(container, SWT.VERTICAL
				| SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
//		connectionsCombo.setInput(connections);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		comboViewer.getCombo().setLayoutData(layoutData);
//		connectionsCombo.getCombo().setEnabled(!connections.isEmpty());
//		connectionsCombo.setFilters( new ViewerFilter[] { getConnectionsFilter() } );
		comboViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
//						updateConnectButton();
//						updatePageComplete();					
					}
				});
		
	}
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		Object object = getSelection(selection);
		
	    if ( object != null ) {
	    	
	    	accessControlEntry = (IAccessControlEntry) object;
	    	setTitle("Access Control Entry");
	    	IPrincipal principal = accessControlEntry.getPrincipal();

	    	if ( principal != null ) {
	    		setDescription( "Access Control Entry for principal '" + principal.getName() + "'.");
		    	setTitleImage(accessControlEntry);
	    	}
	    	
			accessRightsTable.setInput( accessControlEntry.getAccessRights() );
    		for ( IAccessRight accessRight : accessControlEntry.getAccessRights() ) {
    			accessRightsTable.setChecked(accessRight, accessRight.isGranted() );
    		}
    		accessRightsTable.setEnabled( accessControlEntry.isEditable() );
    		levelCombo.setInput( accessControlEntry.getAllowedAccessLevels() );
//    		ISelection selection2 = new St;
//			levelCombo.setSelection(selection2  );

//	    	commitChanges = false;
//	    	propertyChanged( property );
//			setEmptyValueButtonState(property);
//			this.isDirty = false;
//			commitChanges = true;
//			form.getMessageManager().removeAllMessages();
	    }
	}
	
	
	@Override
	protected int getNumClientColumns() {
		return 4;
	}

	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.security.editor.BaseDetailsPage#getValue()
	 */
	@Override
	protected Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#isStale()
	 */
	@Override
	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#refresh()
	 */
	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
	 */
	@Override
	public boolean setFormInput(Object input) {
		// TODO Auto-generated method stub
		return false;
	}
}
