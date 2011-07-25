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

package com.ecmdeveloper.plugin.scripting.wizard;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ecmdeveloper.plugin.scripting.dialogs.MethodSelectionDialog;

/**
 * @author ricardo.belfor
 *
 */
public class ConfigureScriptWizardPage extends WizardPage {

	private static final String TITLE = "Configure Script";
	private CLabel scriptMethod;
	private CLabel scriptClass;
	private IMethod method;
	private JavaElementLabelProvider javaElementLabelProvider;
	
	protected ConfigureScriptWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription("Select the script class and method and configure the launching options");
		javaElementLabelProvider = new JavaElementLabelProvider();
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite container = createContainer(parent);
		
		try {
			

			Group group = new Group(container, SWT.SHADOW_IN  );
			GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false );
			group.setLayoutData( gd );
			
			final GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			group.setLayout(gridLayout);
			group.setText("Script");
			scriptMethod = createJavaElementRow(group, "Method:" );
			scriptClass = createJavaElementRow(group, "Class:" );
			createSelectButton(group);
			

//			GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false );
//			group.setLayoutData( gd );
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}

	private void createSelectButton(Composite container) {
		Button button = new Button(container, SWT.PUSH );
		button.setText( "Select Method" );
		button.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectMethod();
			}} 
		);
	}

	private CLabel createJavaElementRow(Composite container, String name) {

		Label namelabel = new Label(container, SWT.NONE);
		namelabel.setLayoutData( new GridData(GridData.BEGINNING) );
		namelabel.setText( name );
		
		CLabel label = new CLabel(container, SWT.FLAT);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        label.setLayoutData(gd);
        label.setText("<not selected>");
        return label;
	}

	protected void selectMethod() {
		MethodSelectionDialog methodSelectionDialog = createMethodSelectionDialog();
		if ( methodSelectionDialog.open() == Dialog.OK ) {
			method = (IMethod) methodSelectionDialog.getFirstResult();
			updateScriptMethodLabel();
			updateScriptClassLabel();
		}
	}

	private void updateScriptMethodLabel() {
		scriptMethod.setImage( javaElementLabelProvider.getImage(method) );
		scriptMethod.setText( javaElementLabelProvider.getText(method) );
	}

	private void updateScriptClassLabel() {
		IJavaElement compilationUnit = method.getParent();
		scriptClass.setImage( javaElementLabelProvider.getImage(compilationUnit) );
		scriptClass.setText( javaElementLabelProvider.getText(compilationUnit) );
	}

	private MethodSelectionDialog createMethodSelectionDialog() {
		MethodSelectionDialog methodSelectionDialog = new MethodSelectionDialog(getShell());
		methodSelectionDialog.setInitialPattern("**");
		return methodSelectionDialog;
	}

	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

}
