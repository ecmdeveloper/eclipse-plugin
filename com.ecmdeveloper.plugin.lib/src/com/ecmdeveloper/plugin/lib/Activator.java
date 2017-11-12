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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;

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

	private static final String MISSING_JARS_MESSAGE = "There was a problem starting the ECM Developer plugin.\n\n" +
			"The following jar files were not found in the folder {0}: {1}\n\n" +
			"Make sure that the jar-files are copied to this location."; 
	
	private static final String INSTALL_LINK_MESSAGE = "Visit <a href=\"http://www.ecmdeveloper.com/plugin/getting-started\">http://www.ecmdeveloper.com/plugin/getting-started/</a> for the plug in installation instructions.";
	
	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.lib";
	private static Activator plugin;

	public void start(BundleContext context) throws Exception {
		
		super.start(context);
		plugin = this;
		
		String installLocation = getInstallLocation();
		Set<String> missingJars = getMissingJars(installLocation);
		if ( !missingJars.isEmpty() ) {
			showMissingJarsMessage(installLocation, getMissingJarsList(missingJars) );
		}

	}

	private Set<String> getMissingJars(String installLocation) {
		File installFolder = new File(installLocation + "/lib");
		Set<String> apiFiles = new HashSet<String>();
		apiFiles.addAll( Arrays.asList("Jace.jar", "stax-api.jar", "xlxpScanner.jar", "xlxpScanner.jar", "log4j.jar") );
		for (File file : installFolder.listFiles() ) {
			if ( file.getName().startsWith("log4j-") ) {
				apiFiles.remove( "log4j.jar" );
			} else { 
				apiFiles.remove( file.getName() );
			}
		}
		return apiFiles;
	}

	private String getMissingJarsList(Set<String> apiFiles) {
		String concat = "";
		StringBuilder missingJars = new StringBuilder();
		
		for ( String missingJar : apiFiles ) {
			missingJars.append(concat);
			missingJars.append(missingJar);
			concat = ",";
		}
		return missingJars.toString();
	}

	public String getInstallLocation() throws Exception {
		Bundle bundle = getBundle();
		URL locationUrl = FileLocator.find(bundle, new Path("/"), null);
		URL fileUrl = FileLocator.toFileURL(locationUrl);
		return fileUrl.getFile();
	}

	public String[] getLibraries() throws IOException {
		
		String[] pathParts = getPathParts();
		ArrayList<String> pathPartsList = new ArrayList<String>(); 
		for ( String pathPart : pathParts ) {
			if ( !pathPart.equals(".") )
			{
				pathPartsList.add( getPathPartPath(pathPart) );
			}
		}

		return pathPartsList.toArray( new String[0] );
	}

	@SuppressWarnings({ "rawtypes" })
	private String[] getPathParts() {
		Dictionary headers = getBundle().getHeaders();
		String bundleClassPath = (String) headers.get("Bundle-ClassPath");
		System.out.println( bundleClassPath.toString() );
		String[] pathParts = bundleClassPath.split(",");
		return pathParts;
	}

	private String getPathPartPath(String pathPart) throws IOException {
		URL locationUrl = FileLocator.find(getBundle(), new Path(pathPart), null);
		URL fileUrl = FileLocator.toFileURL(locationUrl);
		String file = fileUrl.getFile();
		return file;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
	
	private void showMissingJarsMessage(String installLocation, String missingJars )
	{
		final String message = MessageFormat.format( MISSING_JARS_MESSAGE, installLocation, missingJars );
		final String title = INSTALLATION_ERROR_TITLE;
		
		final Display display = Display.getDefault();
		
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
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
		});
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
