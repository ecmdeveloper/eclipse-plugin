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
import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class SingleChoiceDetailsPage extends BaseDetailsPage {

	private CheckboxTreeViewer treeViewer;

	@Override
	protected void createClientContent(Composite client) {
		super.createClientContent(client);
		
		FormToolkit toolkit = form.getToolkit();
		createChoicesTree(client, toolkit);
	}

	private void createChoicesTree(Composite client, FormToolkit toolkit) {

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
				setDirty(true);
			}
		} );
	}

	@Override
	protected int getNumClientColumns() {
		return 1;
	}

	@Override
	protected Object getValue() {
		
		Object[] checkedElements = treeViewer.getCheckedElements();
		if ( checkedElements == null || checkedElements.length == 0 ) {
			return null;
		}
		
		Choice choice = (Choice) checkedElements[0];
		return choice.getValue();
	}

	@Override
	protected void handleEmptyValueButton(boolean selected) {
		treeViewer.getTree().setEnabled( !selected );
	}

	@Override
	protected void propertyChanged(Property property) {
		treeViewer.setInput(property);
		if ( property.getValue() != null ) {
			selectPropertyValue(property);
		}
	}

	private void selectPropertyValue(Property property) {
		Object value = property.getValue();
		for ( Choice choice : property.getChoices() ) {
			if ( value.equals( choice.getValue() ) ) {
				treeViewer.setChecked( choice , true);
				break;
			}
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
		treeViewer.getTree().setFocus();
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}
}