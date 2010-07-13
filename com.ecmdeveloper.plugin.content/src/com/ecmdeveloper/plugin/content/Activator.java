package com.ecmdeveloper.plugin.content;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ecmdeveloper.plugin.content.util.ContentCache;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.content";

	private static Activator plugin;
	private ContentCache contentCache;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		contentCache = new ContentCache( getStateLocation() );
		contentCache.clear();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		contentCache.clear();
		contentCache.stop();
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public ContentCache getContentCache() {
		return contentCache;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
