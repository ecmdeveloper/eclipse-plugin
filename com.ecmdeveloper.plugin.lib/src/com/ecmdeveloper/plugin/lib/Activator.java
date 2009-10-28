package com.ecmdeveloper.plugin.lib;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.lib";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		String installLocation = getInstallLocation();
		String jaasConfigFile = installLocation + "/config/jaas.conf.WSI";
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
