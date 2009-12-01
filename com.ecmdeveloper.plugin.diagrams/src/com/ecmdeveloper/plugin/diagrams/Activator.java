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
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramAttributeAdapterFactory;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClassAdapterFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.diagrams";

	// The shared instance
	private static Activator plugin;

	private IAdapterFactory classDiagramClassAdapterFactory;
	private IAdapterFactory classDiagramAttributeAdapterFactory;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		classDiagramClassAdapterFactory = new ClassDiagramClassAdapterFactory();
		IAdapterManager adapterManager = Platform.getAdapterManager();
		adapterManager.registerAdapters( classDiagramClassAdapterFactory, ClassDescription.class );
		
		classDiagramAttributeAdapterFactory = new ClassDiagramAttributeAdapterFactory();
		adapterManager.registerAdapters( classDiagramAttributeAdapterFactory, PropertyDescription.class);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		
		IAdapterManager adapterManager = Platform.getAdapterManager();
		
		adapterManager.unregisterAdapters(classDiagramClassAdapterFactory);
		classDiagramClassAdapterFactory = null;
		
		adapterManager.unregisterAdapters(classDiagramAttributeAdapterFactory);
		classDiagramAttributeAdapterFactory = null;
		
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
