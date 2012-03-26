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

package com.ecmdeveloper.plugin.scripting.wizard;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


/**
 * @author ricardo.belfor
 *
 */
public class NewScriptClassTypePage extends NewTypeWizardPage {

	private String methodCode;
	private Collection<String> codeImports;
	
	public NewScriptClassTypePage() {
		super(true, "NewConsoleProjectClassTypePage");
		setTitle( "New Console Project Class" );
		setDescription( "Create a new console project class." );
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite= new Composite(parent, SWT.NONE);
		
		int nColumns = 4;
		
		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;		
		composite.setLayout(layout);
		
		createContainerControls(composite, nColumns);	
		createPackageControls(composite, nColumns);	
		createSeparator(composite, nColumns);
		createTypeNameControls(composite, nColumns);
		createSuperClassControls(composite, nColumns);
		createCommentControls(composite, nColumns);
		setAddComments(true, true);
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

	protected void doStatusUpdate() {
		IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus, fTypeNameStatus };
		updateStatus(status);
	}

	@Override
	protected void createTypeMembers(IType newType, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		newType.createMethod(methodCode, null, false, monitor);
		for ( String codeImport : codeImports ) {
			imports.addImport(codeImport);
		}
	}

	public void setMethodCode(String methodCode) {
		this.methodCode = methodCode;
	}

	public void setImports(Collection<String> imports) {
		this.codeImports = imports;
	}
}
