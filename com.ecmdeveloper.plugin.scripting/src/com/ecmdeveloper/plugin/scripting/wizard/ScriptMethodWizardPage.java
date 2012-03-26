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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public abstract class ScriptMethodWizardPage extends WizardPage {

	private static final String TITLE = "Configure Method";
	
	private static final String COMMENT = 	
		 "/**\r\n" + 
		 " * This method is called for every selected object. The signature of the\r\n" +
		 " * method should not be changed. The name of the method and the rest of the\r\n" +
		 " * code can be adjusted to your needs.\r\n" +
		 " * \r\n" +
		 " * @param object\r\n" +
		 " *            the selected object\r\n" +
		 " * @throws Exception\r\n" +
		 " *             the exception. Exceptions are displayed in the console and\r\n" +
		 " *             does not stop the execution of the rest of the selected\r\n" +
		 " *             objects.\r\n" +
		 " */\r\n";
 
	private String methodName = "run";
	private TargetTypeEditor targetTypeEditor;
	
	protected ScriptMethodWizardPage(boolean customObjectsSupported ) {
		super(TITLE );
		setTitle(TITLE);
		setDescription("Configure the main method of the console class." );
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = getContainer(parent);
		createUsernameEditor(container);
		targetTypeEditor = new TargetTypeEditor(container);
		targetTypeEditor.setValue(TargetType.ANY);
	}

	private Composite getContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

	private void createUsernameEditor(Composite container) {

		StringFieldEditor usernameEditor = new StringFieldEditor("", "&Method name:",container ) {
			@Override
			public int getNumberOfControls() {
				return 2;
			}
		};
		usernameEditor.setEmptyStringAllowed(false);
		usernameEditor.setStringValue( methodName );
		
		usernameEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					methodName = (String) event.getNewValue();
					updateControls();
				}
			}
		});
	}

	
	protected void updateControls() {
		
		if ( methodName.isEmpty() ) {
			setPageComplete(false);
			setErrorMessage("The method name cannot be empty" );
			return;
		}
		
		setPageComplete(true);
		setErrorMessage(null);
	}

	public String getMethodCode(boolean addComment ) {

		StringBuffer eventMethod = new StringBuffer();
				
		if ( addComment ) {
			eventMethod.append( COMMENT );
		}
		eventMethod.append("public void ");
		eventMethod.append( methodName );
		eventMethod.append("(Object object) throws Exception { \n" );
		eventMethod.append( getMethodBody( targetTypeEditor.getValue() ) );
		eventMethod.append( " \n\n// TODO Auto-generated method stub \n\n }" );
		return eventMethod.toString();
	}

	public abstract String getMethodBody(TargetType targetType);
	
	public Collection<String> getImports() 
	{
		ArrayList<String> imports = new ArrayList<String>();
		getImports( imports, targetTypeEditor.getValue() );
		return imports;
	}

	protected abstract void getImports(ArrayList<String> imports, TargetType value);

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public TargetType getTargetType() {
		return targetTypeEditor.getValue();
	}
}
