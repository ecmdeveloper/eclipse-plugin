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

package com.ecmdeveloper.plugin.classes.wizard;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.ClassesManager;
import com.ecmdeveloper.plugin.classes.model.Placeholder;
import com.ecmdeveloper.plugin.classes.model.constants.ClassType;
import com.ecmdeveloper.plugin.classes.views.ClassesViewContentProvider;
import com.ecmdeveloper.plugin.classes.views.ClassesViewLabelProvider;
import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStore;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassSelectionWizardPage extends WizardPage {

	private static final String PAGE_NAME = "classSelectionPage";

	private TreeViewer viewer;
	private ClassType classType;

	public ClassSelectionWizardPage(ClassType classType) {
		super(PAGE_NAME);
		setTitle( "Select Class" );
		setDescription( "Select the class of the new object.");
		this.classType = classType;
	}

	public ClassDescription getClassDescription() {
		
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		if ( selection.isEmpty() ) {
			return null;
		}
		
		Object selectedObject = selection.iterator().next();
		if ( selectedObject instanceof ClassDescription && ! (selectedObject instanceof Placeholder) ) {
			return (ClassDescription) selectedObject;
		}
		return null;
	}
	
	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL );
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);

		viewer = new TreeViewer(container, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider( new ClassesViewContentProvider( classType ) );
		viewer.setLabelProvider(new ClassesViewLabelProvider() );
		viewer.setInput( ClassesManager.getManager() );
		viewer.addSelectionChangedListener( new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updatePageComplete((IStructuredSelection) viewer.getSelection());
			}} );
	}

	protected void updatePageComplete(IStructuredSelection selection) {
		setPageComplete( getClassDescription() != null );
	}

	public void setObjectStoreId(String objectStoreId) {
		viewer.setFilters( new ViewerFilter[] { new ObjectStoreFilter( objectStoreId) } );
	}
	
	class ObjectStoreFilter extends ViewerFilter
	{
		private String objectStoreId;

		public ObjectStoreFilter(String objectStoreId) {
			this.objectStoreId = objectStoreId;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if ( element instanceof ObjectStore ) {
				return objectStoreId.equalsIgnoreCase( ((ObjectStore) element).getId() );
			}
			return true;
		}
	};
}
