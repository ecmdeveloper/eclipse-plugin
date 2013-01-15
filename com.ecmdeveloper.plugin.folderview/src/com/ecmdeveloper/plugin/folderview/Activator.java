package com.ecmdeveloper.plugin.folderview;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ecmdeveloper.plugin.core.model.tasks.ITaskManager;
import com.ecmdeveloper.plugin.core.util.ImageCache;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.folderview";
	private static final ImageCache imageCache = new ImageCache();
	private static Activator plugin;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
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

	public ITaskManager getTaskManager() {
		return (ITaskManager) getWorkbench().getService(ITaskManager.class);		
	}
}
