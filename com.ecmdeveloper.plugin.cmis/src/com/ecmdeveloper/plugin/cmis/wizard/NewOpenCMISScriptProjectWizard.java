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

package com.ecmdeveloper.plugin.cmis.wizard;

import java.io.IOException;

import com.ecmdeveloper.plugin.cmis.Activator;
import com.ecmdeveloper.plugin.cmis.scripting.ScriptingProjectNature;
import com.ecmdeveloper.plugin.scripting.wizard.NewScriptingProjectWizard;

/**
 * @author ricardo.belfor
 *
 */
public class NewOpenCMISScriptProjectWizard extends NewScriptingProjectWizard {

	@Override
	protected String[] getLibraries() throws IOException {
		String[] libraries = Activator.getDefault().getLibraries();
		return libraries;
	}

	@Override
	protected String getNatureId() {
		return ScriptingProjectNature.NATURE_ID;
	}

	@Override
	protected String getMethodRunnerLocation() throws Exception {
		return Activator.getDefault().getMethodRunnerLocation();
	}
}
