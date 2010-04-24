/**
 * Copyright 2009,2010, Ricardo Belfor
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
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	private static final String MISSING_JAAS_FILE_MESSAGE = "The file jaas.conf.WSI is not found in the folder {0}/config or the folder {0}/config/samples. Make sure it is located at one of these locations.";
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
				throw new RuntimeException( MessageFormat.format( MISSING_JAAS_FILE_MESSAGE, installLocation ) );
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
}
