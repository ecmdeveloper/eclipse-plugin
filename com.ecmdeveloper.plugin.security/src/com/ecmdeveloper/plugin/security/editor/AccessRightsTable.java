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

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IManagedForm;

import com.ecmdeveloper.plugin.core.model.security.IAccessRight;

/**
 * @author ricardo.belfor
 *
 */
public abstract class AccessRightsTable {

	private static final String PERMISSIONS_LABEL = "Permissions:";

	private CheckboxTableViewer accessRightsTable;
	
	public AccessRightsTable(Composite client, IManagedForm form, int columns ) {
		createAccessRightsTable( client, form, columns );
	}
	
	private void createAccessRightsTable(Composite client, IManagedForm form, int columns) {
		form.getToolkit().createLabel(client, PERMISSIONS_LABEL);
		Table table = form.getToolkit().createTable(client, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.CHECK );
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL );
		layoutData.horizontalSpan = columns;
		table.setLayoutData( layoutData );
		createTableViewer(table);
	}
	
	private void createTableViewer(Table table) {
		accessRightsTable = new CheckboxTableViewer(table);
		accessRightsTable.setLabelProvider(new LabelProvider() );
		accessRightsTable.setContentProvider(new ArrayContentProvider() );
		accessRightsTable.addCheckStateListener( getCheckStateListener());
	}

	private ICheckStateListener getCheckStateListener() {
		return new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				IAccessRight accessRight = (IAccessRight) event.getElement();
				accessRightChanged(accessRight, event.getChecked() );
			}
		};
	}

	protected abstract void accessRightChanged(IAccessRight accessRight, boolean checked);

	public void setInput(List<IAccessRight> accessRights) {
		accessRightsTable.setInput( accessRights );
	}

	public void setChecked(IAccessRight accessRight, boolean granted) {
		accessRightsTable.setChecked(accessRight, accessRight.isGranted() );
	}
	
	public void setEnabled(boolean enabled) {
		accessRightsTable.getTable().setEnabled( enabled );
	}
	
	public void refresh() {
		accessRightsTable.refresh();
	}
}
