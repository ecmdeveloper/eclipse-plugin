/**
 * Copyright 2011, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.handlers;

import org.eclipse.jface.preference.IPreferenceStore;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.scripting.MethodRunner;
import com.ecmdeveloper.plugin.scripting.ScriptingProjectNature;
import com.ecmdeveloper.plugin.scripting.handlers.AbstractExecuteMethodHandler;

/**
 * @author ricardo.belfor
 *
 */
public class ExecuteMethodHandler extends AbstractExecuteMethodHandler {

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected String getProjectNatureId() {
		return ScriptingProjectNature.NATURE_ID;
	}

	@Override
	protected String getRunnerClassName() {
		return MethodRunner.class.getName();
	}
}
