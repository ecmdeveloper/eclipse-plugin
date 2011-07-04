/**
 * Copyright 2009,2010,2011, Ricardo Belfor
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
package com.ecmdeveloper.plugin.lib;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	private static final String INSTALLATION_ERROR_TITLE = "ECM Developer Installation Error";

	private static final String MISSING_JAAS_FILE_MESSAGE = "There was a problem starting the ECM Developer plugin.\n\n" +
			"The file jaas.conf.WSI is not found in the folder {0}config " +
			"or the folder {0}config/samples. Make sure it is located at one of these locations.\n\n" +
			"This could also mean that the Content Engine client libraries are not copied to the folder {0}."; 
			
	private static final String INSTALL_LINK_MESSAGE = "Visit <a href=\"http://www.ecmdeveloper.com/install\">http://www.ecmdeveloper.com/install</a> for the plug in installation instructions.";

	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.lib";
	private static Activator plugin;

	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		String installLocation = getInstallLocation();
		String jaasConfigFile = installLocation + "/config/jaas.conf.WSI";
		File file = new File( jaasConfigFile );
		if ( ! file.exists() )
		{
			jaasConfigFile = installLocation + "/config/samples/jaas.conf.WSI";
			if ( ! new File( jaasConfigFile ).exists() ) {
				showInvalidInstallationMessage(installLocation );
			}
		}
		
		String waspLocation = installLocation + "/wsi";
		System.setProperty("java.security.auth.login.config", jaasConfigFile );
		System.setProperty("wasp.location", waspLocation );
	}

	public String getInstallLocation() throws Exception {
		Bundle bundle = getBundle();
		URL locationUrl = FileLocator.find(bundle, new Path("/"), null);
		URL fileUrl = FileLocator.toFileURL(locationUrl);
		return fileUrl.getFile();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
	
	private void showInvalidInstallationMessage(String installLocation )
	{
		String message = MessageFormat.format( MISSING_JAAS_FILE_MESSAGE, installLocation );
		String title = INSTALLATION_ERROR_TITLE;
		
		Display display = Display.getDefault();
		 
		MessageDialog dialog = new MessageDialog(display.getActiveShell(), title, null,
				message, MessageDialog.ERROR, new String[] { IDialogConstants.OK_LABEL }, 0) {

			@Override
			protected Control createCustomArea(Composite parent) {

				Link link = new Link(parent, SWT.NONE);     
				String message = INSTALL_LINK_MESSAGE;     
				link.setText(message);    
				link.setSize(400, 100);
				link.addSelectionListener(new SelectionAdapter(){         
					public void widgetSelected(SelectionEvent event) 
					{                
						openLinkInBrowser(event);
					}

				}); 

				return link;

			}}; 
			dialog.open();
	}

	private void openLinkInBrowser(SelectionEvent event) {
		URL url = getURL(event);
		if ( url != null) {
			openURL(url, event.display );
		}
	}

	private URL getURL(SelectionEvent event) {
		URL url = null;
		try {
			url = new URL(event.text);
		} catch (MalformedURLException e) {
			MessageDialog.openError(event.display.getActiveShell(), INSTALLATION_ERROR_TITLE, e
					.getLocalizedMessage());
		}
		return url;
	}

	private void openURL(URL url, Display display) {
		try {                 
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
					.openURL(url);               
		}               
		catch (PartInitException e) {                 
			MessageDialog.openError(display.getActiveShell(), INSTALLATION_ERROR_TITLE, e
					.getLocalizedMessage());
		}              
	}     
}
