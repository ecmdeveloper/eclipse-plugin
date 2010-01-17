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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class BaseDetailsPage implements IDetailsPage {

	protected IManagedForm form;
	private Section section;
	protected Button emptyValueButton;
	private boolean isDirty;
	private Property property;
	
	@Override
	public void initialize(IManagedForm form) {
		this.form = form;
	}
	
	@Override
	public void createContents(Composite parent) {
		setParentLayout(parent);
		Section section = createSection(parent);
		Composite client = createClient(section);
		createClientContent(client);
		section.setClient(client);
	}

	protected void createClientContent(Composite client) {
		createEmptyValueButton(client, form.getToolkit() );
	}
	
	protected abstract int getNumClientColumns();

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	protected void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	@Override
	public void commit(boolean onSave) {
		if (property != null ) {
			commitPropertyValue();
		}
	}

	private void commitPropertyValue() {
		try {
			if ( emptyValueButton != null && emptyValueButton.getSelection() ) {
				property.getObjectStoreItem().setValue( property.getName(), null );
			} else {
				commitNotNullPropertyValue();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void commitNotNullPropertyValue() throws Exception {
		Object value = getValue();
		if ( value instanceof Object[] ) {
			List valuesList = property.getList();
			for ( Object valueElement : (Object[]) value ) {
				valuesList.add( valueElement );
			}
			property.getObjectStoreItem().setValue( property.getName(), valuesList );
		} else {
			property.getObjectStoreItem().setValue( property.getName(), getValue() );
		}
	}
	
	protected abstract Object getValue();
	
	protected void createEmptyValueButton(Composite client, FormToolkit toolkit) {
		
		emptyValueButton = toolkit.createButton(client, "Use Empty Value", SWT.CHECK );
		emptyValueButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selected = ((Button)e.getSource()).getSelection();
				handleEmptyValueButton(selected);
				setDirty(true);
			}
		} );

		GridData gridData = new GridData();
		gridData.horizontalSpan = getNumClientColumns();
		emptyValueButton.setLayoutData( gridData );
	}
	
	protected abstract void handleEmptyValueButton(boolean selected);

	private Section createSection(Composite parent) {
		section = form.getToolkit().createSection(parent,
				Section.DESCRIPTION | Section.TITLE_BAR );
		return section;
	}
	
	private Composite createClient(Section section) {
		Composite client = form.getToolkit().createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = getNumClientColumns();
		client.setLayout(layout);
		form.getToolkit().paintBordersFor(client);
		
		return client;
	}

	private void setParentLayout(Composite parent) {
		FillLayout fillLayout = new FillLayout( SWT.HORIZONTAL | SWT.VERTICAL );
		fillLayout.marginHeight = 5;
		fillLayout.marginWidth = 5;
		parent.setLayout( fillLayout );
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
	    property = getPropertyFromSelection(selection);
	    if ( property != null ) {
	    	setTitle( property.getName() );
	    	setDescription("Enter a new value for the string value. " +
					"This is not a required property and the maximum length is 64.");
	    	propertyChanged( property );
			setEmptyValueButtonState(property);
			setDirty(false);
			form.getMessageManager().removeAllMessages();
	    }
	}

	protected abstract void propertyChanged( Property property );

	private void setEmptyValueButtonState(Property property) {
		
		if ( emptyValueButton != null ) { 
			if ( ! property.isRequired() ) {
				Object value = property.getValue();
				emptyValueButton.setSelection( value == null );
				handleEmptyValueButton( value == null );
			} else {
				emptyValueButton.setEnabled(false);
				handleEmptyValueButton(false);
			}
		}
	}

	protected Property getPropertyFromSelection(ISelection selection) {

		if ( selection.isEmpty() ) {
	    	return null;
	    }
		
		if ( selection instanceof IStructuredSelection ) {
			Iterator<?> iterator = ((IStructuredSelection)selection).iterator();
			return (Property) iterator.next();
	    }
		return null;
	}

	protected void setTitle(String title ) {
		section.setText(title);
	}
	
	protected void setDescription(String description) {
		section.setDescription(description);
	}
}
