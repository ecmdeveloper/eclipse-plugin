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
package com.ecmdeveloper.plugin.java.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class NewDocumentLifecycleActionHandlerTypePage extends NewClassTypePage {

	private static final String DOCUMENT_LIFECYCLE_POLICY_IMPORT = 
		"com.filenet.api.events.DocumentLifecyclePolicy";

	private static final String ON_DOCUMENT_SET_EXCEPTION_METHOD = 
		"public void onDocumentSetException(Document document, DocumentLifecyclePolicy documentLifecyclePolicy) throws EngineRuntimeException" + METHOD_STUB;
	private static final String ON_DOCUMENT_RESET_LIFECYCLE_DOCUMENT_METHOD = 
		"public void onDocumentResetLifecycle(Document document, DocumentLifecyclePolicy documentLifecyclePolicy) throws EngineRuntimeException" + METHOD_STUB;
	private static final String ON_DOCUMENT_PROMOTE_METHOD = 
		"public void onDocumentPromote(Document document, DocumentLifecyclePolicy documentLifecyclePolicy) throws EngineRuntimeException" + METHOD_STUB;
	private static final String DOCUMENT_DEMOTE_DOCUMENT_METHOD = 
		"public void onDocumentDemote(Document document, DocumentLifecyclePolicy documentLifecyclePolicy) throws EngineRuntimeException" + METHOD_STUB;
	private static final String ON_DOCUMENT_CLEAR_EXCEPTION_METHOD = 
		"public void onDocumentClearException(Document document, DocumentLifecyclePolicy documentLifecyclePolicy) throws EngineRuntimeException" + METHOD_STUB;
	
	private static final String DOCUMENT_LIFECYCLE_ACTION_HANDLER_INTERFACE_NAME = 
		"com.filenet.api.engine.DocumentLifecycleActionHandler";

	public NewDocumentLifecycleActionHandlerTypePage() {
		super(true, "NewDocumentLifecycleActionHandler");
		setTitle( "New Document Lifecycle Action Handler Class" );
		setDescription( "Create a new Document Lifecycle Action Handler class." );
	}

	public void init(IStructuredSelection selection) {
		super.init(selection);
		addSuperInterface(DOCUMENT_LIFECYCLE_ACTION_HANDLER_INTERFACE_NAME);
	}
	
	@Override
	protected void createTypeMembers(IType newType, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {

		if (createStubsButton.getSelection()) {
			
			String eventMethod = ON_DOCUMENT_CLEAR_EXCEPTION_METHOD;
			newType.createMethod(eventMethod, null, false, monitor);
			
			eventMethod = DOCUMENT_DEMOTE_DOCUMENT_METHOD;
			newType.createMethod(eventMethod, null, false, monitor);

			eventMethod = ON_DOCUMENT_PROMOTE_METHOD;
			newType.createMethod(eventMethod, null, false, monitor);

			eventMethod = ON_DOCUMENT_RESET_LIFECYCLE_DOCUMENT_METHOD;
			newType.createMethod(eventMethod, null, false, monitor);

			eventMethod = ON_DOCUMENT_SET_EXCEPTION_METHOD;
			newType.createMethod(eventMethod, null, false, monitor);
						
			imports.addImport( DOCUMENT_IMPORT );
			imports.addImport( DOCUMENT_LIFECYCLE_POLICY_IMPORT );
			imports.addImport( ENGINE_RUNTIME_EXCEPTION_IMPORT );
		}
		
		imports.addImport( DOCUMENT_LIFECYCLE_ACTION_HANDLER_INTERFACE_NAME );
	}
	
}
