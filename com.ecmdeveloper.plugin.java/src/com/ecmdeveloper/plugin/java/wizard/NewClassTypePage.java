package com.ecmdeveloper.plugin.java.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public abstract class NewClassTypePage extends NewTypeWizardPage {

	protected static final String ENGINE_RUNTIME_EXCEPTION_IMPORT = 
		"com.filenet.api.exception.EngineRuntimeException";
	
	protected static final String DOCUMENT_IMPORT = 
		"com.filenet.api.core.Document";

	protected static final String METHOD_STUB = 
		" { \n// TODO Auto-generated method stub \n\n }";
	
	protected Button createStubsButton;
	
	public NewClassTypePage(boolean isClass, String pageName) {
		super(isClass, pageName);
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
		createStubsButton = new Button(composite, SWT.CHECK);
		createStubsButton.setText("&Create inherited abstract methods");
		GridData gd = new GridData();
		gd.horizontalSpan = nColumns - 1;
		createStubsButton.setLayoutData(gd);
		createStubsButton.setSelection(true);
		
		createCommentControls(composite, nColumns);
		enableCommentControl(true);
		
		setControl(composite);
	}

	public void init(IStructuredSelection selection) {
		IJavaElement javaElement = getInitialJavaElement(selection);
		initContainerPage(javaElement);
		initTypePage(javaElement);
		doStatusUpdate();
	}
	
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		doStatusUpdate();
	}
	
	/**
	 *  Define the components for which a status is desired
	 */
	 protected void doStatusUpdate() {
		IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus,
				fTypeNameStatus };
		updateStatus(status);
	}

	/**
	 * Creates a spacer control with the given span.
	 * The composite is assumed to have <code>MGridLayout</code> as
	 * layout.
	 * @param parent The parent composite
	 */			
	public static Control createEmptySpace(Composite parent, int span) {

		Label label = new Label(parent, SWT.LEFT);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = span;
		gd.horizontalIndent = 0;
		gd.widthHint = 0;
		gd.heightHint = 0;
		label.setLayoutData(gd);
		return label;
	}
}
