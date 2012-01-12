package com.ecmdeveloper.plugin.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IObjectStoresManager;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManager;
import com.ecmdeveloper.plugin.core.util.ImageCache;
import com.ecmdeveloper.plugin.ui.model.ObjectStoreItemAdapterFactory;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.ui";

	private static Activator plugin;

	private static final ImageCache imageCache = new ImageCache();

	private IAdapterFactory objectStoreItemAdapterFactory;
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		objectStoreItemAdapterFactory = new ObjectStoreItemAdapterFactory();
		IAdapterManager adapterManager = Platform.getAdapterManager();
		adapterManager.registerAdapters( objectStoreItemAdapterFactory, IObjectStoreItem.class );
		
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		imageCache.dispose();
		
		IAdapterManager adapterManager = Platform.getAdapterManager();
		adapterManager.unregisterAdapters(objectStoreItemAdapterFactory);
		objectStoreItemAdapterFactory = null;
		
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
