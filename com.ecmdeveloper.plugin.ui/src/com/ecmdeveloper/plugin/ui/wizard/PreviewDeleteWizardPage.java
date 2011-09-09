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

package com.ecmdeveloper.plugin.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.core.model.ICustomObject;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.ui.Activator;
import com.ecmdeveloper.plugin.ui.views.ObjectStoreItemLabelProvider;
import com.ecmdeveloper.plugin.ui.views.ObjectStoresViewContentProvider;

/**
 * @author ricardo.belfor
 *
 */
public class PreviewDeleteWizardPage extends WizardPage {

	private static final String CONTAINED_DELETE_MESSAGE = "It will also delete all the contained ";
	private static final String DELETE_MESSAGE = "The following items will be deleted. ";
	private static final String DELETE_MESSAGE_WITH_FOLDER = "The following items and subfolders will be deleted. ";
	private static final String PAGE_NAME = "Preview Delete";

	private TreeViewer viewer;
	private Collection<IObjectStoreItem> itemsDeleted;
	private ObjectStoresViewContentProvider contentProvider;
	
	protected PreviewDeleteWizardPage() {
		super(PAGE_NAME);
		setTitle( PAGE_NAME );
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = createContainer(parent);
		createViewer(container);
	}

	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

	private void createViewer(Composite container) {
		viewer = new TreeViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider( new ObjectStoreItemLabelProvider() );
		contentProvider = new ObjectStoresViewContentProvider();
		viewer.setContentProvider( contentProvider );
		viewer.setInput( Activator.getDefault().getTaskManager() );
	}

	@Override
	public void setVisible(boolean visible) {
		contentProvider.setRootElements( getItemsDeleted().toArray() );
		addTypeFilter();
		setDescription( getPageDescription() );
		super.setVisible(visible);
	}

	private void addTypeFilter() {
		ObjectStoreItemTypeFilter filter = createViewerFilter();
		viewer.resetFilters();
		viewer.addFilter( filter );
	}

	private String getPageDescription() {
		ArrayList<String> deletedItemTypes = ((DeleteWizard)getWizard()).getDeletedItemTypes();
		
		StringBuffer description = new StringBuffer();
		if ( ((DeleteWizard)getWizard()).isFolderDeleted() ) {
			description.append(DELETE_MESSAGE_WITH_FOLDER);
			if ( ! deletedItemTypes.isEmpty() ) {
				getContainedObjectsDescription(deletedItemTypes, description);
			}
		} else {
			description.append(DELETE_MESSAGE);
		}
		
		return description.toString();
	}

	private void getContainedObjectsDescription(ArrayList<String> deletedItemTypes, StringBuffer description) {
		description.append(CONTAINED_DELETE_MESSAGE);
		String concat = "";
		for (int i = 0; i < deletedItemTypes.size(); i++) {
			String itemType = deletedItemTypes.get(i);
			description.append(concat);
			description.append(itemType);
			concat = ( i == deletedItemTypes.size() - 2 ) ? " and " : ", ";
		}
		description.append(".");
	}

	private ObjectStoreItemTypeFilter createViewerFilter() {
		boolean filterDocuments = !((DeleteWizard)getWizard()).isDeleteContainedDocuments();
		boolean filterCustomObjects =  !((DeleteWizard)getWizard()).isDeleteContainedCustomObjects();
		boolean filterContainedFolders  =  !((DeleteWizard)getWizard()).isDeleteContainedFolders();
		ObjectStoreItemTypeFilter filter = new ObjectStoreItemTypeFilter();
		filter.setFilterDocuments(filterDocuments);
		filter.setFilterCustomObjects(filterCustomObjects);
		filter.setFilterContainedFolders(filterContainedFolders);
		return filter;
	}

	public void setItemsDeleted(Collection<IObjectStoreItem> itemsDeleted) {
		this.itemsDeleted = itemsDeleted;
	}

	public Collection<IObjectStoreItem> getItemsDeleted() {
		return itemsDeleted;
	}

	class ObjectStoreItemTypeFilter extends ViewerFilter {
		
		private boolean filterCustomObjects;
		private boolean filterDocuments;
		private boolean filterContainedFolders;
		
		public boolean isFilterCustomObjects() {
			return filterCustomObjects;
		}

		public void setFilterCustomObjects(boolean filterCustomObjects) {
			this.filterCustomObjects = filterCustomObjects;
		}

		public boolean isFilterDocuments() {
			return filterDocuments;
		}

		public void setFilterDocuments(boolean filterDocuments) {
			this.filterDocuments = filterDocuments;
		}

		public boolean isFilterContainedFolders() {
			return filterContainedFolders;
		}

		public void setFilterContainedFolders(boolean filterContainedFolders) {
			this.filterContainedFolders = filterContainedFolders;
		}
		
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if ( PreviewDeleteWizardPage.this.itemsDeleted.contains(element) ) {
				return true;
			}
			return !isFilteredType(element);
		}

		private boolean isFilteredType(Object element) {
			return (filterDocuments && element instanceof IDocument)
					|| (element instanceof ICustomObject && filterCustomObjects);
		}
	};
}
