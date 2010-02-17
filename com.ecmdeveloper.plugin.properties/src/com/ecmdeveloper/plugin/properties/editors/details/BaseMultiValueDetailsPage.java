/**
 * Copyright 2010, Ricardo Belfor
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
package com.ecmdeveloper.plugin.properties.editors.details;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.classes.model.Choice;
import com.ecmdeveloper.plugin.properties.model.Property;

public abstract class BaseMultiValueDetailsPage extends BaseDetailsPage {

	private List<Value> modelValues;
	private TableViewer valuesTableViewer;
	private Button updateButton;
	private Button deleteButton;
	private Button addButton;
	private Button upButton;
	private Button downButton;
	
	@Override
	protected void createClientContent(Composite client) {
		
		if ( ! isReadOnly() ) {
			super.createClientContent(client);
		}
		
		FormToolkit toolkit = form.getToolkit();
		
		createValuesTableViewer(client, toolkit);

		if ( ! isReadOnly() ) {
			Composite buttons = createButtonsComposite(client);
			
			createAddButton(buttons, toolkit);
			createDeleteButton(buttons, toolkit);
			createUpButton(buttons, toolkit);
			createDownButton(buttons, toolkit);
	
			createInput(client, toolkit);
			createUpdateButton(client, toolkit);
		}
	}

	protected abstract void createInput(Composite client, FormToolkit toolkit);

	protected boolean isReadOnly() {
		return false;
	}
	
	private void createValuesTableViewer(Composite client, FormToolkit toolkit) {
	
		Table table = toolkit.createTable(client, SWT.BORDER | SWT.FULL_SELECTION );
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.grabExcessVerticalSpace = true;
		table.setLayoutData( layoutData );
		valuesTableViewer = new TableViewer(table);
		valuesTableViewer.setLabelProvider( new LabelProvider() );
		valuesTableViewer.setContentProvider( new ArrayContentProvider() );
		
		valuesTableViewer.addSelectionChangedListener( new ISelectionChangedListener() {
	
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if ( selection.isEmpty() ) {
					setInputValue(null);
				} else {
					Object value = selection.getFirstElement();
					setInputValue( ((Value)value).getValue() );
				}
			}
		} );
	}

	protected abstract void setInputValue(Object value);
	
	private Composite createButtonsComposite(Composite client) {
		Composite buttons = new Composite(client, SWT.NONE );
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		buttons.setLayout(fillLayout);
		return buttons;
	}

	private void createUpdateButton(Composite client, FormToolkit toolkit) {
		updateButton = toolkit.createButton(client, "Update", SWT.None );
		updateButton.addSelectionListener(new ValueSelectionAdapter() {

			@Override
			protected void valueSelected(Value value) {
				value.setValue( getInputValue() );
				setDirty(true);
			}
		});
		
		GridData gridData = new GridData( SWT.LEFT, SWT.TOP, true, false );
		updateButton.setLayoutData( gridData );
	}

	protected abstract Object getInputValue();
	
	private void createAddButton(Composite buttons, FormToolkit toolkit) {
		
		addButton = toolkit.createButton(buttons, "Add", SWT.None );
		addButton.addSelectionListener(new SelectionAdapter() {
	
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object newValue = getInputValue();
				if( newValue != null ) {
					Value value = new Value(newValue);
					modelValues.add(value);
					modelChanged();
				}
			}
		} );
	}

	private void createDeleteButton(Composite buttons, FormToolkit toolkit) {
		deleteButton = toolkit.createButton(buttons, "Remove", SWT.None );
		deleteButton.addSelectionListener(new ValueSelectionAdapter() {

			@Override
			protected void valueSelected(Value value) {
				if ( modelValues.remove(value) ) {
					modelChanged();
				}
			}
		} );
	}
	
	private void createUpButton(Composite client, FormToolkit toolkit) {
		upButton = toolkit.createButton(client, "Move Up", SWT.None );
		upButton.addSelectionListener(new ValueSelectionAdapter() {
	
			@Override
			protected void valueSelected(Value value) {
				int valueIndex = modelValues.indexOf(value);
				if ( valueIndex > 0 ) {
					modelValues.remove( valueIndex );
					modelValues.add( valueIndex-1, value );
					modelChanged();
				}
			}
		});
	}

	private void createDownButton(Composite client, FormToolkit toolkit) {
	
		downButton = toolkit.createButton(client, "Move Down", SWT.None );
		downButton.addSelectionListener(new ValueSelectionAdapter() {

			@Override
			protected void valueSelected(Value value) {
				int valueIndex = modelValues.indexOf(value);
				if ( valueIndex >= 0 && valueIndex < (modelValues.size()-1) ) {
					modelValues.remove( valueIndex );
					modelValues.add( valueIndex+1, value );
					modelChanged();
				}
			}
		});
	}

	private void modelChanged() {
		valuesTableViewer.refresh();
		upButton.setEnabled( !modelValues.isEmpty() );
		downButton.setEnabled( !modelValues.isEmpty() );
		deleteButton.setEnabled( !modelValues.isEmpty() );
		setDirty(true);
	}

	@Override
	protected int getNumClientColumns() {
		return 2;
	}

	@Override
	protected Object getValue() {
		if ( modelValues.isEmpty() ) {
			return null;
		}
		
		Object[] valuesArray = new Object[ modelValues.size() ];
		int valuesArrayIndex = 0;
		for ( Value modelValue : modelValues ) {
			valuesArray[valuesArrayIndex++] = modelValue.getValue();
		}
		
		return valuesArray;
	}

	@Override
	protected void handleEmptyValueButton(boolean selected) {
		valuesTableViewer.getTable().setEnabled( !selected );
	}

	@Override
	protected void propertyChanged(Property property) {
		Object value = property.getValue();
		setValue( value );
	}

	private void setValue(Object value) {
		
		modelValues = new LinkedList<Value>();
		if ( value != null ) {
			for ( Object objectValue : (Object[])value ) {
				Value modelValue = new Value(objectValue);
				modelValues.add( modelValue );
			}
		}
		valuesTableViewer.setInput(modelValues);
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	class Value {
		public Object objectValue;
		
		public Value(Object objectValue) {
			initializeValue(objectValue);
		}

		public Object getValue() {
			return objectValue;
		}

		public void setValue(Object objectValue) {
			initializeValue(objectValue);
			modelChanged();
		}

		private void initializeValue(Object objectValue) {
			this.objectValue = objectValue;
		}

		@Override
		public String toString() {
			return objectValue.toString();
		}
	}

	abstract class ValueSelectionAdapter extends SelectionAdapter
	{
		@Override
		public void widgetSelected(SelectionEvent e) {
			IStructuredSelection selection = (IStructuredSelection) valuesTableViewer.getSelection();
			if ( selection.isEmpty() ) {
				return;
			}

			Value value = (Value) selection.getFirstElement();
			valueSelected( value );
		}

		protected abstract void valueSelected(Value value);
	}
}
