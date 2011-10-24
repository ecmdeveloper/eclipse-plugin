package com.ecmdeveloper.plugin.cmis;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;

import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.ecmdeveloper.plugin.core.model.IObjectStoresManager;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManager;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.cmis";

	private static Activator plugin;
	private SessionFactory sessionFactory;
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		sessionFactory = SessionFactoryImpl.newInstance();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public IObjectStoresManager getObjectStoresManager() {
		return (IObjectStoresManager) getWorkbench().getService(IObjectStoresManager.class);		
	}

	public ITaskManager getTaskManager() {
		return (ITaskManager) getWorkbench().getService(ITaskManager.class);		
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

	@SuppressWarnings("unchecked")
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

	public String getMethodRunnerLocation() throws IOException {
		Bundle bundle = getBundle();
		URL locationUrl = FileLocator.find(bundle, new Path("/dist/methodrunner.jar"), null);
		URL fileUrl = FileLocator.toFileURL(locationUrl);
		return fileUrl.getFile();
	}
}
