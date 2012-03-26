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

package com.ecmdeveloper.plugin.cmis.wizard;

import java.util.ArrayList;

import com.ecmdeveloper.plugin.scripting.wizard.NewScriptClassWizard;
import com.ecmdeveloper.plugin.scripting.wizard.ScriptMethodWizardPage;
import com.ecmdeveloper.plugin.scripting.wizard.TargetType;

/**
 * @author ricardo.belfor
 *
 */
public class NewCMISScriptClassWizard extends NewScriptClassWizard {

	protected ScriptMethodWizardPage getScriptMethodWizardPage() {
		
		ScriptMethodWizardPage scriptMethodWizardPage = new ScriptMethodWizardPage(false) {

			@Override
			protected void getImports(ArrayList<String> imports, TargetType targetType) {
				
				imports.add("org.apache.chemistry.opencmis.client.api.Session");
				imports.add("com.ecmdeveloper.plugin.cmis.scripting.MethodRunner");
				
				switch ( targetType ) {
				case ANY:
					imports.add( "org.apache.chemistry.opencmis.client.api.FileableCmisObject" );
					break;
				case DOCUMENT:
					imports.add( "org.apache.chemistry.opencmis.client.api.Document" );
					break;
				case FOLDER:
					imports.add( "org.apache.chemistry.opencmis.client.api.Folder" );
					break;
				}
			}

			@Override
			public String getMethodBody(TargetType targetType) {
				
				StringBuffer methodBody = new StringBuffer();
				methodBody.append( "Session session = MethodRunner.getSession();\r\n" );
				switch ( targetType ) {
				case ANY:
					methodBody.append( "FileableCmisObject fileableCmisObject = (FileableCmisObject) object;" );
					break;
				case DOCUMENT:
					methodBody.append( "Document document = (Document) object;" );
					break;
				case FOLDER:
					methodBody.append( "Folder folder = (Folder) object;" );
					break;
				}
				return methodBody.toString();
			}
		};
		return scriptMethodWizardPage;
	}
}
