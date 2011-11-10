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

package com.ecmdeveloper.plugin.search.wizards;

import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.QueryFieldValueFormatter;

/**
 * @author ricardo.belfor
 *
 */
public abstract class MultiValueWizardPage extends WizardPage {

	private TableViewer valuesTable;
	private ArrayList<Object> values;
	private final IQueryField queryField;
	
	protected MultiValueWizardPage(IQueryField queryField) {
		super("Multi Value Wizard Page");
		this.queryField = queryField;
		setTitle( "Multiple Values" );
		values = new ArrayList<Object>();
	}

	public void setValue(ArrayList<Object> values) {
		if ( values != null) {
			this.values.addAll(values);
		}
	}
	
	public ArrayList<Object> getValue() {
		return values;
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite container = createContainer(parent);
		
		createValuesTable(container);
		createValuesEditor(container);
		createButtons(container);
	}

	private void createValuesEditor(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL );
		composite.setLayoutData( new GridData(GridData.FILL_BOTH) );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);
		createValuesControls(composite);
	}

	protected abstract void createValuesControls(Composite parent);
	
	private void createValuesTable(Composite container) {
		
		valuesTable = new TableViewer(container, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION ); 
		valuesTable.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		valuesTable.setContentProvider( new ArrayContentProvider() );
		valuesTable.setLabelProvider( new LabelProvider() {

			@Override
			public String getText(Object element) {
				if ( element instanceof Calendar) {
					return QueryFieldValueFormatter.format(queryField, element);
//					return ((Calendar) element).getTime().toString();
				}
				return super.getText(element);
			}} );
		
		valuesTable.setInput( values );
		valuesTable.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if ( !selection.isEmpty() ) {
					setEditorValue( selection.getFirstElement() );
				}
			}} );
	}

	protected abstract void setEditorValue(Object value);

	private void createButtons(Composite container) {
		Composite buttonBar = createButtonBar(container);
		createAddValueButton(buttonBar);
		createRemoveValueButton(buttonBar);
		createUpdateValueButton(buttonBar);
	}

	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL );
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

	private Composite createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL );
		composite.setLayoutData( new GridData( GridData.BEGINNING ) );
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 3;
		composite.setLayout(gridLayout2);
		return composite;
	}

	private void createAddValueButton(Composite container) {
		
		Button addExternalFileButton = new Button(container, SWT.PUSH);
		addExternalFileButton.setText("Add");
		addExternalFileButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object editorValue = getEditorValue();
				if (editorValue != null) {
					values.add( editorValue );
					valuesTable.refresh();
				}
			}
		});
	}

	protected abstract Object getEditorValue();

	private void createRemoveValueButton(Composite container) {
		
		Button addExternalFileButton = new Button(container, SWT.PUSH);
		addExternalFileButton.setText("Remove");
		addExternalFileButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) valuesTable.getSelection();
				if ( !selection.isEmpty() ) {
					values.remove( selection.getFirstElement() );
					valuesTable.refresh();
					clearEditorValue();
				}
			}
		});
	}

	protected abstract void clearEditorValue();

	private void createUpdateValueButton(Composite container) {
		
		Button addExternalFileButton = new Button(container, SWT.PUSH);
		addExternalFileButton.setText("Update");
		addExternalFileButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object editorValue = getEditorValue();
				if ( editorValue == null ) {
					return;
				}
				
				IStructuredSelection selection = (IStructuredSelection) valuesTable.getSelection();
				if ( !selection.isEmpty() ) {
					int index = values.indexOf( selection.getFirstElement() );
					if ( index >= 0 ) {
						values.set(index, editorValue );
						valuesTable.replace(values.get(index), index);
					}
				}				
			}
		});
	}
}
