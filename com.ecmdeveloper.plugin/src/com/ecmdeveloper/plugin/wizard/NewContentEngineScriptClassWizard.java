/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.wizard;

import java.util.ArrayList;

import com.ecmdeveloper.plugin.scripting.wizard.NewScriptClassWizard;
import com.ecmdeveloper.plugin.scripting.wizard.ScriptMethodWizardPage;
import com.ecmdeveloper.plugin.scripting.wizard.TargetType;

/**
 * @author ricardo.belfor
 *
 */
public class NewContentEngineScriptClassWizard extends NewScriptClassWizard {

	protected ScriptMethodWizardPage getScriptMethodWizardPage() {
		
		ScriptMethodWizardPage scriptMethodWizardPage = new ScriptMethodWizardPage(true) {

			@Override
			protected void getImports(ArrayList<String> imports, TargetType targetType) {
				switch ( targetType ) {
				case ANY:
					imports.add( "com.filenet.api.core.IndependentlyPersistableObject" );
					break;
				case DOCUMENT:
					imports.add( "com.filenet.api.core.Document" );
					break;
				case FOLDER:
					imports.add( "com.filenet.api.core.Folder" );
					break;
				case CUSTOM_OBJECT:
					imports.add( "com.filenet.api.core.CustomObject" );
					break;
				}
			}

			@Override
			public String getMethodBody(TargetType targetType) {
				switch ( targetType ) {
				case ANY:
					return "IndependentlyPersistableObject independentlyPersistableObject = (IndependentlyPersistableObject) object;";
				case DOCUMENT:
					return "Document document = (Document) object;";
				case FOLDER:
					return "Folder folder = (Folder) object;";
				case CUSTOM_OBJECT:
					return "CustomObject customObject = (CustomObject) object;";
				}
				return "";
			}
		};
		return scriptMethodWizardPage;
	}
}
