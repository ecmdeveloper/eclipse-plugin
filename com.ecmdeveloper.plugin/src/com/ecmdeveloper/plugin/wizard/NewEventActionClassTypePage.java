package com.ecmdeveloper.plugin.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;
import org.eclipse.jface.viewers.IStructuredSelection;

public class NewEventActionClassTypePage extends NewTypeWizardPage {

	private static final String EVENT_ACTION_HANDLER_INTERFACE_NAME = "com.filenet.api.engine.EventActionHandler";

	private Button fCreateStubs;
	
	public NewEventActionClassTypePage() {
		super(true, "New Event Action");
		setTitle( "New Event Action Class" );
		setDescription( "Create a new Event Action class." );
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
//		composite.setFont(parent.getFont());
		
		int nColumns= 4;
		
		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;		
		composite.setLayout(layout);
		
		createContainerControls(composite, nColumns);	
		createPackageControls(composite, nColumns);	
				
		createSeparator(composite, nColumns);
		
		createTypeNameControls(composite, nColumns);
		createSuperInterfacesControls(composite, nColumns);
		
		// Create the checkbox controlling whether we want stubs
		createEmptySpace(composite, 1);
        fCreateStubs= new Button(composite, SWT.CHECK);
        fCreateStubs.setText("Create inherited abstract methods");
        GridData gd= new GridData();
        gd.horizontalSpan= nColumns;
        fCreateStubs.setLayoutData(gd);
		
		createCommentControls(composite, nColumns);
		enableCommentControl(true);
		
		setControl(composite);
	}

	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		doStatusUpdate();
	}
	
	public void init(IStructuredSelection selection) {
		
		IJavaElement javaElement = getInitialJavaElement(selection);
		initContainerPage(javaElement);
		initTypePage(javaElement);
		doStatusUpdate();
		
		addSuperInterface(EVENT_ACTION_HANDLER_INTERFACE_NAME);
	}

	/**
	 *  Define the components for which a status is desired
	 */
	 private void doStatusUpdate() {
		IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus,
				fTypeNameStatus };
		updateStatus(status);
	}
	
	@Override
	protected void createTypeMembers(IType newType, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {

		if (fCreateStubs.getSelection()) {
			String eventMethod = "public void onEvent(ObjectChangeEvent event, Id subscriptionId) throws EngineRuntimeException {}";
			newType.createMethod(eventMethod, null, false, monitor);
			
			imports.addImport( "com.filenet.api.events.ObjectChangeEvent" );
			imports.addImport( "com.filenet.api.exception.EngineRuntimeException" );
			imports.addImport( "com.filenet.api.util.Id" );
		}
		
		imports.addImport( "com.filenet.api.engine.EventActionHandler" );
	}

	/**
	 * Creates a spacer control with the given span.
	 * The composite is assumed to have <code>MGridLayout</code> as
	 * layout.
	 * @param parent The parent composite
	 */			
	public static Control createEmptySpace(Composite parent, int span) {
		Label label= new Label(parent, SWT.LEFT);
		GridData gd= new GridData();
		gd.horizontalAlignment= GridData.BEGINNING;
		gd.grabExcessHorizontalSpace= false;
		gd.horizontalSpan= span;
		gd.horizontalIndent= 0;
		gd.widthHint= 0;
		gd.heightHint= 0;
		label.setLayoutData(gd);
		return label;
	}
}
