/**
 * Copyright 2013, Ricardo Belfor
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

package com.ecmdeveloper.plugin.java.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author ricardo.belfor
 *
 */
public class NewChangePreprocessorClassTypePage extends NewClassTypePage {

	private static final String CHANGE_PREPROCESSOR_INTERFACE_NAME = "com.filenet.api.engine.ChangePreprocessor";
	private static final String NAME = "New Change Preprocessor";

	protected static final String CHANGE_PREPROCESSORP_METHOD_STUB = 
		" { \n\t\t// TODO Auto-generated method stub \n\n\t\treturn false; }";
	
	private static final String ON_EVENT_METHOD = 
		"public boolean preprocessObjectChange(IndependentlyPersistableObject object) throws EngineRuntimeException" + 
		CHANGE_PREPROCESSORP_METHOD_STUB;
	
	public NewChangePreprocessorClassTypePage() {
		super(true, NAME);
		setTitle( NAME );
		setDescription( "Create a new Change Preprocessor class." );
	}

	public void init(IStructuredSelection selection) {
		super.init(selection);
		addSuperInterface(CHANGE_PREPROCESSOR_INTERFACE_NAME);
	}
	
	@Override
	protected void createTypeMembers(IType newType, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {

		if (createStubsButton.getSelection()) {
			String eventMethod = ON_EVENT_METHOD;
			newType.createMethod(eventMethod, null, false, monitor);
			
			imports.addImport( "com.filenet.api.core.IndependentlyPersistableObject" );
			imports.addImport( "com.filenet.api.exception.EngineRuntimeException" );
		}
		
		imports.addImport( CHANGE_PREPROCESSOR_INTERFACE_NAME );
	}
}
