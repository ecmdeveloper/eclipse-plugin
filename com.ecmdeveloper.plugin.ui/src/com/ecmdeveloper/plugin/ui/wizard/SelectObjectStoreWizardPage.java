/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.ui.wizard;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.ui.views.ObjectStoreItemLabelProvider;

public class SelectObjectStoreWizardPage extends WizardPage 
{
	private CheckboxTableViewer objectStoresTable = null;
	private boolean inputSet;
	
	public SelectObjectStoreWizardPage() 
	{
		super( "selectObjectStore" );
		setTitle("Select Object Store");
		setDescription("Select from the list below the Object Stores to import" );
		inputSet = false;
	}

	@Override
	public void createControl(Composite parent) 
	{
		Composite container = new Composite(parent, SWT.NULL );
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);

		objectStoresTable = CheckboxTableViewer.newCheckList(container, SWT.BORDER);
		objectStoresTable.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		objectStoresTable.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				updatePageComplete();
			}
		});
	}

	protected void updatePageComplete() 
	{
		setPageComplete( objectStoresTable.getCheckedElements().length > 0 );
	}
	
	@Override
	public void setVisible(boolean visible) 
	{
		super.setVisible(visible);

		objectStoresTable.setContentProvider( new ArrayContentProvider() );
		objectStoresTable.setInput( ((AbstractImportObjectStoreWizard)getWizard()).getObjectStores() );
		objectStoresTable.setLabelProvider( new ObjectStoreItemLabelProvider() );
		inputSet = true;
		if ( objectStoresTable.getTable().getItemCount() == 0 )
		{
			setErrorMessage( "There are no more Object Stores left to import from this connection." );
		}
	}

	public IObjectStore[] getObjectStores() {

		if ( true ) {
			Object[] selectedObjects = objectStoresTable.getCheckedElements();
			IObjectStore[] objectStores = new IObjectStore[ objectStoresTable.getCheckedElements().length ];
			for (int i = 0; i < objectStores.length; i++) {
				objectStores[i] = (IObjectStore) selectedObjects[i];
			}
			return objectStores;
		} else {
			return new IObjectStore[0];
		}
	}
}
