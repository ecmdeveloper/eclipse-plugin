package com.ecmdeveloper.plugin.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ecmdeveloper.plugin.core.model.IObjectStoresStore;
import com.ecmdeveloper.plugin.core.model.impl.ObjectStoresManager;
import com.ecmdeveloper.plugin.core.util.ImageCache;
import com.ecmdeveloper.plugin.core.util.PluginLog;
import com.ecmdeveloper.plugin.core.util.PluginMessage;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.core";
	public static final String OBJECT_STORES_STORE_EXTENSION_ID = "com.ecmdeveloper.plugin.objectstorestores";
	
	private static Activator plugin;
	private static final ImageCache imageCache = new ImageCache();
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		getObjectStoreStores();
	}

	private void getObjectStoreStores() {
		
		ObjectStoresManager objectStoresManager = ObjectStoresManager.getManager();
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(OBJECT_STORES_STORE_EXTENSION_ID);
		
		try {
			for ( IConfigurationElement configurationElement : configurationElements ) {
				final Object object = configurationElement.createExecutableExtension("class");
				if ( object instanceof IObjectStoresStore) {
					objectStoresManager.registerObjectStoresStore((IObjectStoresStore) object);
				}
			}
		} catch (CoreException e) {
			PluginLog.error(e);
		}
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
