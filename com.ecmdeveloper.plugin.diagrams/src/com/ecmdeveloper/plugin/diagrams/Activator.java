/**
 * Copyright 2009, Ricardo Belfor
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
package com.ecmdeveloper.plugin.diagrams;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramAttributeAdapterFactory;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClassAdapterFactory;
import com.ecmdeveloper.plugin.diagrams.util.ImageCache;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.diagrams";
	
	private static final ImageCache imageCache = new ImageCache();
	private static Activator plugin;

	private IAdapterFactory classDiagramClassAdapterFactory;
	private IAdapterFactory classDiagramAttributeAdapterFactory;
	
	public Activator() {}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		classDiagramClassAdapterFactory = new ClassDiagramClassAdapterFactory();
		IAdapterManager adapterManager = Platform.getAdapterManager();
		adapterManager.registerAdapters( classDiagramClassAdapterFactory, ClassDescription.class );
		
		classDiagramAttributeAdapterFactory = new ClassDiagramAttributeAdapterFactory();
		adapterManager.registerAdapters( classDiagramAttributeAdapterFactory, PropertyDescription.class);		
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		
		IAdapterManager adapterManager = Platform.getAdapterManager();
		
		adapterManager.unregisterAdapters(classDiagramClassAdapterFactory);
		classDiagramClassAdapterFactory = null;
		
		adapterManager.unregisterAdapters(classDiagramAttributeAdapterFactory);
		classDiagramAttributeAdapterFactory = null;
		
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
