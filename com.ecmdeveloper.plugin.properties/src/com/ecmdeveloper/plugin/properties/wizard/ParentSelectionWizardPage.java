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

package com.ecmdeveloper.plugin.properties.wizard;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.properties.Activator;
import com.ecmdeveloper.plugin.properties.util.IconFiles;
import com.ecmdeveloper.plugin.views.ObjectStoreItemLabelProvider;
import com.ecmdeveloper.plugin.views.ObjectStoresViewContentProvider;

/**
 * @author Ricardo.Belfor
 *
 */
public class ParentSelectionWizardPage extends WizardPage {

	private static final String NO_PARENT_MESSAGE = "<no parent selected>";
	private static final String DESCRIPTION = "Select the parent folder";
	private static final String TITLE = "Select Parent";
	private static final String BROWSE_BUTTON_LABEL = "Br&owse...";
	private static final String PAGE_NAME = "selectParentPage";

	private TreeViewer viewer;
	private ObjectStoreItem selection;
	
	public ObjectStoreItem getSelection() {
		return selection;
	}

	public void setSelection(ObjectStoreItem folder) {
		this.selection = folder;
	}

	protected ParentSelectionWizardPage() {
		super(PAGE_NAME);
		setTitle( TITLE);
		setDescription( DESCRIPTION );
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL );
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);

		addLabel(container, "Current Parent Path:", true);
		createTreeViewer(container);
		createBrowseButton(container);
	}

	private void createTreeViewer(Composite container) {
		viewer = new TreeViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider( new PathContentProvider() );
		viewer.setLabelProvider( new PathLabelProvider() );
		setViewerInput();
	}

	private PathElement[] getPathElements( String objectStoreName, String folderPath ) {
		ArrayList<PathElement> pathElements = new ArrayList<PathElement>();
		pathElements.add( new PathElement(objectStoreName, 0 ) );
		
		if (folderPath.length() > 0 ) {
			String[] pathParts = folderPath.substring(1).split("/");
			for ( String pathPart : pathParts ) {
				pathElements.add( new PathElement( pathPart, 1) );
			}
		}
		return pathElements.toArray( new PathElement[0] );
	}
	private void addLabel(Composite container, String text, boolean enabled) {
		final Label label = new Label(container, SWT.NONE);
		final GridData gridData = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData);
		label.setText(text);
		label.setEnabled(enabled);
	}

	private void createBrowseButton(Composite container) {
		Button browseButton = new Button(container, SWT.NONE);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseForParent();
			}
		});
		browseButton.setText(BROWSE_BUTTON_LABEL);
	}
	
	private void browseForParent() {

		ElementTreeSelectionDialog dialog = createBrowseForParentDialog();

		if ( dialog.open() == ElementTreeSelectionDialog.OK ) {
			Object result = dialog.getFirstResult();
			if ( result instanceof Folder || result instanceof ObjectStore ) {
				selection = (ObjectStoreItem) result;
				setViewerInput();
			}
			return;
		}
	}

	private ElementTreeSelectionDialog createBrowseForParentDialog() {

		ITreeContentProvider contentProvider = new ObjectStoresViewContentProvider();
		ILabelProvider labelProvider = new ObjectStoreItemLabelProvider();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), labelProvider, contentProvider );
		dialog.setInput( Activator.getDefault().getObjectStoresManager() );
		contentProvider.inputChanged( null, null, Activator.getDefault().getObjectStoresManager() );
		dialog.setTitle( getTitle() );
		dialog.setMessage( getDescription() );
		dialog.addFilter( new ParentTargetFilter() );
		return dialog;
	}

	private void setViewerInput() {
		if ( selection != null ) {
			String pathName = getSelectionPathName();
			String objectStoreName = selection.getObjectStore().getDisplayName();
			viewer.setInput( getPathElements( objectStoreName, pathName ) );
		} else {
			PathElement dummyPathElement = new PathElement(NO_PARENT_MESSAGE, 2);
			viewer.setInput( new PathElement[] { dummyPathElement } );
		}
		viewer.refresh();
		viewer.expandAll();
		
		setPageComplete( selection != null );
	}

	private String getSelectionPathName() {
		String pathName;
		if ( selection instanceof Folder ) {
			pathName = ((Folder) selection).getPathName();
		} else {
			pathName = "";
		}
		return pathName;
	}
	
	class ParentTargetFilter extends ViewerFilter
	{
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if ( ( element instanceof Document || element instanceof CustomObject  ) ) {
				return false;
			}
			return true;
		}
	};
	
	class PathElement {
		public String name;
		public int type;
		
		public PathElement(String name, int type) {
			this.name = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	
	class PathLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object element) {
			PathElement pathElement = (PathElement) element;
			if ( pathElement.type == 0 ) {
				return Activator.getImage( IconFiles.OBJECT_STORE );
			} else if ( pathElement.type == 1 ) {
				return Activator.getImage( IconFiles.FOLDER );
			} else {
				return Activator.getImage( IconFiles.FOLDER_ERROR );
			}
		}
	}

	class PathContentProvider implements IStructuredContentProvider, ITreeContentProvider {

		private PathElement[] pathParts;

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			pathParts = null;
			if ( newInput != null && newInput instanceof PathElement[] ) {
				pathParts = (PathElement[]) newInput;
			}
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof PathElement[] ) {
				return new Object[] { pathParts[0] };
			} else if (!isInputEmpty()) {
				return getChildren(inputElement);
			}
			return null;
		}

		private boolean isInputEmpty() {
			return pathParts == null || pathParts.length == 0;  
		}

		
		@Override
		public void dispose() {
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if ( isInputEmpty() ) {
				return null;
			}
			
			int index = getElementIndex(parentElement);
			if ( index < 0 || index >= pathParts.length - 1) {
				return null;
			}
			
			return new Object[] { pathParts[index+1] };
		}

		private int getElementIndex(Object inputElement) {
			if ( ! isInputEmpty() ) {
				for ( int i = 0; i < pathParts.length; ++i ) {
					if ( pathParts[i].equals(inputElement) ) {
						return i;
					}
				}
			}
			return -1;
		}

		@Override
		public Object getParent(Object element) {
			if ( ! isInputEmpty() ) {
				int index = getElementIndex(element);
				if ( index >= 1 ) {
					return new Object[] { pathParts[index-1] };
				}
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if ( ! isInputEmpty() ) {
				int index = getElementIndex(element);
				return index >= 0 && index < pathParts.length - 1;
			}
			return false;
		}
	}
}
