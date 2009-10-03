package com.ecmdeveloper.plugin.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredSelection;

public class NewDocumentClassifierTypePage extends NewClassTypePage {

	private static final String CLASSIFY_METHOD = 
		"public void classify(Document document) throws EngineRuntimeException" + METHOD_STUB;
	private static final String DOCUMENT_CLASSIFIER_INTERFACE_NAME = 
		"com.filenet.api.engine.DocumentClassifier";

	public NewDocumentClassifierTypePage() {
		super(true, "NewDocumentClassifierTypePage");
		setTitle( "New Document Classifier Class" );
		setDescription( "Create a new Document Classifier class." );
	}

	public void init(IStructuredSelection selection) {
		super.init(selection);
		addSuperInterface(DOCUMENT_CLASSIFIER_INTERFACE_NAME);
	}
	
	@Override
	protected void createTypeMembers(IType newType, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {

		if (createStubsButton.getSelection()) {
			
			String eventMethod = CLASSIFY_METHOD;
			newType.createMethod(eventMethod, null, false, monitor);
						
			imports.addImport( DOCUMENT_IMPORT );
			imports.addImport( ENGINE_RUNTIME_EXCEPTION_IMPORT );
		}
		
		imports.addImport( DOCUMENT_CLASSIFIER_INTERFACE_NAME );
	}
}
