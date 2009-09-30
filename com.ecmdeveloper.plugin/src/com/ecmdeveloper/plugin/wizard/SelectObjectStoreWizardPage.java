package com.ecmdeveloper.plugin.wizard;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class SelectObjectStoreWizardPage extends WizardPage 
{
	private CheckboxTableViewer objectStoresTable = null;
	
	public SelectObjectStoreWizardPage() 
	{
		super( "selectObjectStore" );
		setTitle("Select Object Store");
		setDescription("Select from the list below the Object Stores to import" );
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
		objectStoresTable.setInput( ((ImportObjectStoreWizard)getWizard()).getObjectStores() );
		objectStoresTable.setLabelProvider( new LabelProvider() );
		if ( objectStoresTable.getTable().getItemCount() == 0 )
		{
			setErrorMessage( "There are no more Object Stores left to import from this connection." );
		}
	}

	public String[] getObjectStores() {
		
		Object[] selectedObjects = objectStoresTable.getCheckedElements();
		String[] objectStores = new String[ objectStoresTable.getCheckedElements().length ];
		for (int i = 0; i < objectStores.length; i++) {
			objectStores[i] = (String) selectedObjects[i];
		}
		return objectStores;
	}
}
