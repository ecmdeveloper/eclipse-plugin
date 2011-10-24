/**
 * Copyright 2011, Ricardo Belfor
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
package com.ecmdeveloper.plugin.scripting;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.ecmdeveloper.plugin.scripting.engine.ScriptingContextSerializer;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.ecmdeveloper.plugin.scripting";

	private static Activator plugin;
	private ScriptingContextSerializer scriptingContextSerializer;
	
	public ScriptingContextSerializer getScriptingContextSerializer() {
		return scriptingContextSerializer;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		scriptingContextSerializer = new ScriptingContextSerializer( getStateLocation() );
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public String getBaseMethodRunnerLocation() throws Exception {
		Bundle bundle = getBundle();
		URL locationUrl = FileLocator.find(bundle, new Path("/dist/com.ecmdeveloper.scripting_1.0.0.jar"), null);
		URL fileUrl = FileLocator.toFileURL(locationUrl);
		return fileUrl.getFile();
	}

	public String getLog4JLocation() throws Exception {
		Bundle bundle = getBundle();
		URL locationUrl = FileLocator.find(bundle, new Path("/dist/log4j.properties"), null);
		URL fileUrl = FileLocator.toFileURL(locationUrl);
		return fileUrl.getFile();
	}
}
