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

package com.ecmdeveloper.plugin.properties.editors.details.input;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.classes.model.Choice;
import com.ecmdeveloper.plugin.properties.choices.ChoicesContentProvider;
import com.ecmdeveloper.plugin.properties.choices.ChoicesLabelProvider;
import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class ChoiceFormInput {

	private CheckboxTreeViewer treeViewer;
	private Property property;

	public ChoiceFormInput(Composite client, FormToolkit toolkit) {

		Tree choicesTree = toolkit.createTree(client, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION/*| SWT.V_SCROLL */ | SWT.CHECK );
		choicesTree.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
		
		treeViewer = new CheckboxTreeViewer(choicesTree);
		treeViewer.setLabelProvider( new ChoicesLabelProvider() );
		treeViewer.setContentProvider( new ChoicesContentProvider() );
		treeViewer.addCheckStateListener( new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				makeSingleSelected(event);				
			}

			private void makeSingleSelected(CheckStateChangedEvent event) {
				Choice choice = (Choice) event.getElement();
				if ( choice.isSelectable() ) {
					treeViewer.setCheckedElements( new Object[] { choice } );
				} else {
					treeViewer.setCheckedElements( new Object[0] );
				}
			}
		});

		treeViewer.addSelectionChangedListener( new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectionModified();
			}
		} );
	}

	protected void selectionModified() {
	}

	public void setProperty( Property property ) {
		this.property = property;
		treeViewer.setInput(property);
	}
	
	public void setValue(Object value  ) {

		treeViewer.setCheckedElements( new Object[0] );

		if ( value != null ) {
			for ( Choice choice : property.getChoices() ) {
				if ( value.equals( choice.getValue() ) ) {
					treeViewer.setChecked( choice , true);
					break;
				}
			}
		}
	}
	
	public Object getValue() {
		Object[] checkedElements = treeViewer.getCheckedElements();
		if ( checkedElements == null || checkedElements.length == 0 ) {
			return null;
		}
		
		Choice choice = (Choice) checkedElements[0];
		return choice.getValue();
	}

	public void setEnabled(boolean enabled) {
		treeViewer.getTree().setEnabled(enabled);
	}

	public void setFocus() {
		treeViewer.getTree().setFocus();
	}
}
