package com.ecmdeveloper.plugin.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ecmdeveloper.plugin.core.model.impl.ObjectStoresManager;
import com.ecmdeveloper.plugin.core.util.ImageCache;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.core";

	private static Activator plugin;
	private static final ImageCache imageCache = new ImageCache();
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		ObjectStoresManager.getManager().saveObjectStores();
		plugin = null;
		imageCache.dispose();
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static Image getImage( String path ) {
		return imageCache.getImage( getImageDescriptor( path ) );		
	}
}
