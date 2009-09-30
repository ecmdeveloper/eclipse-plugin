package com.ecmdeveloper.plugin.wizard;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;

public class ImportObjectStoreWizard extends Wizard implements IImportWizard {
	
	private SelectConnectionWizardPage selectConnectionWizardPage;
	private ConfigureConnectionWizardPage configureConnectionWizardPage;
	private SelectObjectStoreWizardPage selectObjectStoreWizardPage;

	private boolean connected = false;
	private ObjectStoresManager objectStoresManager;
	private String connectionName;
	
	@Override
	public void addPages() {
		
		Collection<ContentEngineConnection> connections = ObjectStoresManager
		.getManager().getConnections();

		if ( ! connections.isEmpty() ) {
			selectConnectionWizardPage = new SelectConnectionWizardPage( connections );
			addPage( selectConnectionWizardPage );
		}

		configureConnectionWizardPage = new ConfigureConnectionWizardPage();
		addPage(configureConnectionWizardPage);
		
		selectObjectStoreWizardPage = new SelectObjectStoreWizardPage();
		addPage(selectObjectStoreWizardPage);

		setWindowTitle( "Import Object Store" );
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		
		if ( page instanceof SelectConnectionWizardPage ) {
			if ( ! ( (SelectConnectionWizardPage) page).isNewConnection() ) {
				return selectObjectStoreWizardPage;
			}
		}

		return super.getNextPage(page);
	}

	@Override
	public boolean performFinish() {
		for (String objectStoreName : selectObjectStoreWizardPage.getObjectStores() )
		{
			objectStoresManager.addObjectStore(objectStoreName, connectionName);
		}
		return true;
	}

	@Override
	public boolean canFinish() {
		String[] objectStores = selectObjectStoreWizardPage.getObjectStores();
		if ( objectStores == null )
		{
			return false;
		}
		return selectObjectStoreWizardPage.getObjectStores().length > 0;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		objectStoresManager = ObjectStoresManager.getManager();
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected ) {
		this.connected = connected;
	}
	
	public void connect( ContentEngineConnection connection ) {
		
		if ( ! connection.isConnected() ) {
			objectStoresManager.connectConnection( connection.getName() );
			connectionName = connection.getName();
		}
		connected = true;
		getContainer().updateButtons();
	}
	
	public void connect() {
		
		String url = configureConnectionWizardPage.getURL();
		String username = configureConnectionWizardPage.getUsername();
		String password = configureConnectionWizardPage.getPassword();
		
		connectionName = objectStoresManager.createConnection( url, username, password );
		connected = true;

		getContainer().updateButtons();
	}
	
	public String[] getObjectStores() {
		return objectStoresManager.getNewObjectstoreNames(connectionName);
	}
}