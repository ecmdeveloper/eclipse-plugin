package com.ecmdeveloper.plugin.wizard;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.ecmdeveloper.plugin.util.PluginMessage;

public class ImportObjectStoreWizard extends Wizard implements IImportWizard {
	
	private static final String CONNECT_MESSAGE = "Connecting to \"{0}\"";
	private static final String CONNECT_TITLE = "Connect";
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
		setNeedsProgressMonitor(true);
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

	public void setConnection( ContentEngineConnection connection ) {

		connected = connection.isConnected();
		connectionName = connection.getName();
		getContainer().updateButtons();
	}
	
	public void connect( final ContentEngineConnection connection ) {
		
		if ( ! connection.isConnected() ) {
			
	      try {
	    	  getContainer().run(true, false, new IRunnableWithProgress() {
	    		  public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
	    			  performConnect(connection, monitor);
	    		  }
	    	  });
			} catch (InvocationTargetException e) {
				PluginMessage.openError(getShell(), CONNECT_TITLE, e.getLocalizedMessage(), e );
			} catch (InterruptedException e) {
				// User canceled, so stop but don’t close wizard.
			}
			
		} else {
			connected = true;
			connectionName = connection.getName();
			getContainer().updateButtons();
		}
	}
	
	public void connect() {
		
		final String url = configureConnectionWizardPage.getURL();
		final String username = configureConnectionWizardPage.getUsername();
		final String password = configureConnectionWizardPage.getPassword();
		
	      // Perform the operation in a separate thread
	      // so that the operation can be canceled.
		try {
	         getContainer().run(true, false, new IRunnableWithProgress() {
	            public void run(IProgressMonitor monitor) throws InvocationTargetException,
	                  InterruptedException {
	        		connectionName = objectStoresManager.createConnection( url, username, password, monitor );
	        		connected = true;
	        		getContainer().updateButtons();
	            }
	         });
	      }
	      catch (InvocationTargetException e) {
				PluginMessage.openError(getShell(), CONNECT_TITLE, e.getLocalizedMessage(), e );
	      }
	      catch (InterruptedException e) {
	    	  // Should not happen
	      }
	}
	
	public String[] getObjectStores() {
		return objectStoresManager.getNewObjectstoreNames(connectionName);
	}

	private void performConnect(final ContentEngineConnection connection, IProgressMonitor monitor) {
		objectStoresManager.connectConnection(connection.getName(), monitor );
		connected = true;
		connectionName = connection.getName();
		getContainer().updateButtons();
	}
}