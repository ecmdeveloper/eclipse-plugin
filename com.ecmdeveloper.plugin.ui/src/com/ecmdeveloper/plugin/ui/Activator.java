package com.ecmdeveloper.plugin.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ecmdeveloper.plugin.core.model.IObjectStoresManager;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManager;
import com.ecmdeveloper.plugin.core.util.ImageCache;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.ui";

	private static Activator plugin;

	private static final ImageCache imageCache = new ImageCache();
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		Platform.getPlugin("com.ecmdeveloper.plugin.cmis");
		Platform.getPlugin("com.ecmdeveloper.plugin");
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		imageCache.dispose();
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}


	public IObjectStoresManager getObjectStoresManager() {
		return (IObjectStoresManager) getWorkbench().getService(IObjectStoresManager.class);		
	}

	public ITaskManager getTaskManager() {
		return (ITaskManager) getWorkbench().getService(ITaskManager.class);		
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static Image getImage( String path ) {
		return imageCache.getImage( getImageDescriptor( path ) );		
	}
}
