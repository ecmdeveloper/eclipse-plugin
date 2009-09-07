package com.ecmdeveloper.plugin.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.ecmdeveloper.plugin.model.ObjectStoresManager;

public class ImportObjectStoreWizard extends Wizard implements IImportWizard {
	private ConfigureConnectionWizardPage configureConnectionWizardPage;
	private SelectObjectStoreWizardPage selectObjectStoreWizardPage;

	private boolean connected = false;
	private ObjectStoresManager objectStoresManager;
	private String connectionName;
	
	@Override
	public void addPages() 
	{
		configureConnectionWizardPage = new ConfigureConnectionWizardPage();
		addPage(configureConnectionWizardPage);
		
		selectObjectStoreWizardPage = new SelectObjectStoreWizardPage();
		addPage(selectObjectStoreWizardPage);
	}

	@Override
	public boolean performFinish() 
	{
		for (String objectStoreName : selectObjectStoreWizardPage.getObjectStores() )
		{
			objectStoresManager.addObjectStore(objectStoreName, connectionName);
		}
		return true;
	}

	@Override
	public boolean canFinish() 
	{
		String[] objectStores = selectObjectStoreWizardPage.getObjectStores();
		if ( objectStores == null )
		{
			return false;
		}
		return selectObjectStoreWizardPage.getObjectStores().length > 0;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) 
	{
		objectStoresManager = ObjectStoresManager.getManager();
	}

	public boolean isConnected()
	{
		return connected;
	}
	
	public void connect()
	{
		String url = configureConnectionWizardPage.getURL();
		String username = configureConnectionWizardPage.getUsername();
		String password = configureConnectionWizardPage.getPassword();
		
		connectionName = objectStoresManager.createConnection( url, username, password );
		connected = true;
		
		getContainer().updateButtons();
	}
	
	public String[] getObjectStores()
	{
		return objectStoresManager.getObjectStores(connectionName);
	}
}
