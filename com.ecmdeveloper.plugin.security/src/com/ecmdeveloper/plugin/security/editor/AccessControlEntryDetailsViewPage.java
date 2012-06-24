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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IAccessRight;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;

/**
 * @author ricardo.belfor
 *
 */
public class AccessControlEntryDetailsViewPage extends BaseDetailsPage {

	private IAccessControlEntry accessControlEntry;
	private AccessRightsTable accessRightsTable;
	private Label levelLabel;
	private Label applyToLabel;
	private Label typeLabel;
	
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
			}
		};
	}

	private void createTypeControls(Composite client, FormToolkit toolkit) {
		toolkit.createLabel(client, "Type: ");
		typeLabel = toolkit.createLabel(client, "");
	}

	private void createLevelControls(Composite container) {
		form.getToolkit().createLabel(container, "Level: ");
		levelLabel = form.getToolkit().createLabel(container, "");
	}

	private void createApplyToControls(Composite container) {
		form.getToolkit().createLabel(container, "Apply To: ");
		applyToLabel = form.getToolkit().createLabel(container, "");
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
    		
    		levelLabel.setText( accessControlEntry.getAccessLevel().getName() );
    		accessRightsTable.setEnabled( false );
	    }
	}
	
	
	@Override
	protected int getNumClientColumns() {
		return 2;
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
