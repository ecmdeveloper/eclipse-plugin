/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.properties.editors.details;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class MultiValueStringDetailsPage extends BaseDetailsPage {

	private static final String VALUES_PROPERTY = "Values";
	private ArrayList<Value> modelValues;
	private PropertyChangeSupport modelChanges;
	private TableViewer valuesTableViewer;
	private Text text;
	private Button updateButton;
	private Button deleteButton;
	private Button addButton;

	@Override
	protected void createClientContent(Composite client) {
		super.createClientContent(client);
		
		FormToolkit toolkit = form.getToolkit();
		
		createText(client, toolkit);
		createUpdateButton(client, toolkit);
		createValuesTableViewer(client, toolkit);
		Composite buttons = createButtonsComposite(client);
		createAddButton(toolkit, buttons);
		createDeleteButton(toolkit, buttons);
	}

	private Composite createButtonsComposite(Composite client) {
		Composite buttons = new Composite(client, SWT.NONE );
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		buttons.setLayout(fillLayout);
		return buttons;
	}

	private void createUpdateButton(Composite client, FormToolkit toolkit) {
		updateButton = toolkit.createButton(client, "Update", SWT.None );
		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) valuesTableViewer.getSelection();
				if ( selection.isEmpty() ) {
					return;
				}
				
				Value value = (Value) selection.getFirstElement();
				value.setValue( text.getText() );
				setDirty(true);
			}
		});
	}

	private void createAddButton(FormToolkit toolkit, Composite buttons) {
		
		addButton = toolkit.createButton(buttons, "Add", SWT.None );
		addButton.addSelectionListener(new SelectionAdapter() {
	
			@Override
			public void widgetSelected(SelectionEvent e) {
				String newValue = text.getText();
				if( !newValue.isEmpty() ) {
					Value value = new Value(newValue);
					modelValues.add(value);
					modelChanges.firePropertyChange(VALUES_PROPERTY, null, null );
					setDirty(true);
				}
			}
		} );
	}

	private void createDeleteButton(FormToolkit toolkit, Composite buttons) {
		deleteButton = toolkit.createButton(buttons, "Remove", SWT.None );
		deleteButton.addSelectionListener(new SelectionAdapter() {
	
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) valuesTableViewer.getSelection();
				if ( selection.isEmpty() ) {
					return;
				}
				
				Value value = (Value) selection.getFirstElement();
				if ( modelValues.remove(value) ) {
					modelChanges.firePropertyChange(VALUES_PROPERTY, null, null );
					setDirty(true);
				}
			}
		} );
	}
	
	private void createText(Composite client, FormToolkit toolkit) {
		text = toolkit.createText(client, "", SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP ); 
		text.setLayoutData( new GridData(GridData.FILL_HORIZONTAL ) );
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});
	}

	private void createValuesTableViewer(Composite client, FormToolkit toolkit) {

		Table table = toolkit.createTable(client, SWT.BORDER | SWT.FULL_SELECTION );
		table.setLayoutData( new GridData(GridData.FILL_BOTH) );
		valuesTableViewer = new TableViewer(table);
		valuesTableViewer.setLabelProvider( new LabelProvider() );
		valuesTableViewer.setContentProvider( new ValuesContentProvider() );
		
		valuesTableViewer.addSelectionChangedListener( new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if ( selection.isEmpty() ) {
					text.setText("");
				} else {
					Object value = selection.getFirstElement();
					text.setText( value.toString() );
				}
			}
		} );
	}

	@Override
	protected int getNumClientColumns() {
		return 2;
	}

	@Override
	protected Object getValue() {
		IStructuredSelection selection = (IStructuredSelection) valuesTableViewer.getSelection();
		if ( selection.isEmpty() ) {
			return null;
		}
		
		return null;
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
		
		modelValues = new ArrayList<Value>();
		modelChanges = new PropertyChangeSupport(modelValues);		
		if ( value != null ) {
			for ( Object objectValue : (Object[])value ) {
				Value modelValue = new Value(objectValue);
				modelValues.add( modelValue );
			}
		}
		valuesTableViewer.setInput(modelValues);
		modelChanges.addPropertyChangeListener( (ValuesContentProvider) valuesTableViewer.getContentProvider() );
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean setFormInput(Object input) {
		// TODO Auto-generated method stub
		return false;
	}

	class Value {
		public Object objectValue;
		
		public Value(Object objectValue) {
			this.objectValue = objectValue;
		}

		public void setValue(Object objectValue) {
			this.objectValue = objectValue;
			modelChanges.firePropertyChange("Value", null, this );
		}

		@Override
		public String toString() {
			return objectValue.toString();
		}
	}

	class ValuesContentProvider extends ArrayContentProvider implements PropertyChangeListener {

		private TableViewer viewer;
		
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			super.inputChanged(viewer, oldInput, newInput);
			this.viewer = (TableViewer) viewer;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ( VALUES_PROPERTY.equals( evt.getPropertyName() ) ) {
				viewer.refresh();
			} else {
				viewer.update( evt.getNewValue(), null );
			}
		}
	}
}
