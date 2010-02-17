/**
 * Copyright 2010, Ricardo Belfor
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

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.classes.model.Choice;
import com.ecmdeveloper.plugin.properties.choices.ChoicesContentProvider;
import com.ecmdeveloper.plugin.properties.choices.ChoicesLabelProvider;
import com.ecmdeveloper.plugin.properties.editors.details.input.ChoiceFormInput;
import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class SingleChoiceDetailsPage extends BaseDetailsPage implements PropertyChangeListener {

	private ChoiceFormInput choiceFormInput;

	@Override
	protected void createClientContent(Composite client) {
		super.createClientContent(client);
		
		FormToolkit toolkit = form.getToolkit();
		choiceFormInput = new ChoiceFormInput(client,toolkit) {
			@Override
			protected void selectionModified() {
				setDirty(true);
			}
		};
	}


	@Override
	protected int getNumClientColumns() {
		return 1;
	}

	@Override
	protected Object getValue() {
		return choiceFormInput.getValue();
	}

	@Override
	protected void handleEmptyValueButton(boolean selected) {
		choiceFormInput.setEnabled( !selected );
	}

	@Override
	protected void propertyChanged(Property property) {
		
		choiceFormInput.setProperty(property);
		if ( property.getValue() != null ) {
			choiceFormInput.setValue( property.getValue() );
			property.getPropertyDescription().addPropertyChangeListener(this);
		}
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
		choiceFormInput.setFocus();
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}


	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		if ( propertyChangeEvent.getPropertyName().equals("Choices") ) {
			if ( property.getValue() != null ) {
				choiceFormInput.setValueASync( property.getValue() );
			}
		}		
	}
}
