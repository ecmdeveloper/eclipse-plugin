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
package com.ecmdeveloper.plugin.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.util.PluginMessage;

public class ImportObjectStoreWizard extends Wizard implements IImportWizard {
	
	private static final String CONNECT_FAILED_MESSAGE = "Connect failed";
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
		
		Collection<IConnection> connections = ObjectStoresManager
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

	/**
	 * Gets the next wizard page. If on the select connection page a existing
	 * connection is choosen then the configure connection page is skipped.
	 * 
	 * @param page the page
	 * 
	 * @return the next page
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
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
		for (ObjectStore objectStore : selectObjectStoreWizardPage.getObjectStores() )
		{
			objectStoresManager.addObjectStore(objectStore);
		}
		return true;
	}

	@Override
	public boolean canFinish() {
		ObjectStore[] objectStores = selectObjectStoreWizardPage.getObjectStores();
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
			
//	    	  getShell().getDisplay().syncExec( new Runnable() {
//
//				@Override
//				public void run() {
//
//			      try {
//			    	  getContainer().run(true, false, new IRunnableWithProgress() {
//			    		  public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			    			  performConnect(connection, new NullProgressMonitor() );
//			    		  }
//			    	  });
//					} catch (InvocationTargetException e) {
//						PluginMessage.openError(getShell(), CONNECT_TITLE, e.getLocalizedMessage(), e );
//					} catch (InterruptedException e) {
//						// User canceled, so stop but don’t close wizard.
//					}
//					
//				} });
	    	  
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
		
		try {
	         getContainer().run(true, false, new IRunnableWithProgress() {
	            public void run(IProgressMonitor monitor) throws InvocationTargetException,
	                  InterruptedException {
	        		try {
						connectionName = objectStoresManager.createConnection( url, username, password, monitor );
		        		connected = true;
		    			getShell().getDisplay().syncExec( new Runnable() {
	
		    				@Override
		    				public void run() {
		    					getContainer().updateButtons();
		    				}
		    			});
					} catch (ExecutionException e) {
						PluginMessage.openErrorFromThread(getShell(), CONNECT_TITLE, CONNECT_FAILED_MESSAGE, e );
					}
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
	
	public IObjectStore[] getObjectStores() {
		
		IObjectStore[] objectStores = new IObjectStore[0];

		try {
			NewObjectstoreNamesRunnable runnable = new NewObjectstoreNamesRunnable(
					connectionName);
			getContainer().run(true, false, runnable);
			return runnable.getObjectStores();
		} catch (Exception e ) {
			PluginMessage.openError(getShell(), CONNECT_TITLE, e.getLocalizedMessage(), e );
		}
		
		return objectStores; 
	}

	private void performConnect(final ContentEngineConnection connection, IProgressMonitor monitor) {
		try {
			objectStoresManager.connectConnection(connection, monitor );
			connected = true;
			connectionName = connection.getName();
			
			getShell().getDisplay().syncExec( new Runnable() {

				@Override
				public void run() {
					getContainer().updateButtons();
				}});
		} catch (ExecutionException e) {
			PluginMessage.openErrorFromThread(getShell(), CONNECT_TITLE, e.getLocalizedMessage(), e );
		}
	}
	
	class NewObjectstoreNamesRunnable implements IRunnableWithProgress {

		private String connectionName;
		private IObjectStore[] objectStores = new ObjectStore[0];
		
		public NewObjectstoreNamesRunnable(String connectionName ) {
			this.connectionName = connectionName;
		}
		
		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {

			monitor.beginTask("Getting Object Store names", IProgressMonitor.UNKNOWN );
			try {
				objectStores = objectStoresManager.getNewObjectstores(connectionName);
			} catch (final Exception e) {
				PluginMessage.openErrorFromThread(getShell(), CONNECT_TITLE, e.getLocalizedMessage(), e );
			}
			monitor.done();
		}

		public IObjectStore[] getObjectStores() {
			return objectStores;
		}
	}
}