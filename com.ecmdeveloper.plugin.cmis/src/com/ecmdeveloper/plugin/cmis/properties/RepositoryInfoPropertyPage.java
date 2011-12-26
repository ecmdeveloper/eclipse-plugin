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

package com.ecmdeveloper.plugin.cmis.properties;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.ecmdeveloper.plugin.cmis.model.ObjectStore;

/**
 * @author ricardo.belfor
 *
 */
public class RepositoryInfoPropertyPage  extends PropertyPage implements IWorkbenchPropertyPage {

	private TreeViewer infoTreeViewer;
	
	@Override
	protected Control createContents(Composite parent) {
		
		ObjectStore objectStore = (ObjectStore) getElement();

		Composite container = createContainer(parent);
		if ( objectStore.isConnected() ) {
			createFileTree(container);
			
			List<Entry<String,Object>> repositoryInfo = objectStore.getRepositoryInfo();
			infoTreeViewer.setInput( repositoryInfo );
			infoTreeViewer.expandToLevel(2);
		} else {
			Label label = new Label(container, SWT.None);
			label.setText("Repository information is available only when the Object Store is connected.");
		}
		return container;
	}
	
	private Composite createContainer(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		panel.setLayout(layout);
		return panel;
	}

	private void createFileTree(Composite client) {

		infoTreeViewer = new TreeViewer(client, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		GridData gd = new GridData(GridData.FILL_BOTH);
		
		infoTreeViewer.getTree().setLayoutData(gd);
		infoTreeViewer.setContentProvider(new InfoContentProvider() );
		infoTreeViewer.setLabelProvider(new InfoLabelProvider());
		infoTreeViewer.getTree().setHeaderVisible(true);
		
		TreeColumn treeColumn1 = new TreeColumn(infoTreeViewer.getTree(), SWT.LEFT );
		treeColumn1.setWidth(200);
		treeColumn1.setText("Name");
		treeColumn1.setResizable(true);

		TreeColumn treeColumn2 = new TreeColumn(infoTreeViewer.getTree(), SWT.LEFT );
		treeColumn2.setWidth(300);
		treeColumn2.setText("Value");
		treeColumn2.setResizable(true);
	}
	
	class InfoContentProvider implements IStructuredContentProvider, ITreeContentProvider {

		List<Entry<String,Object>> infoList;
		
		@SuppressWarnings("unchecked")
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if ( newInput != null ) {
				infoList = (List<Entry<String, Object>>) newInput;
			} else {
				infoList = null;
			}
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public void dispose() {
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object[] getChildren(Object parentElement) {
			
			if ( parentElement instanceof List) {
				return ((List) parentElement).toArray();
			} else if ( parentElement instanceof Entry ) {
				Object value = ((Entry) parentElement).getValue();
				if ( value != null && value instanceof List ) {
					return ((List) value).toArray();
				}
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean hasChildren(Object element) {
			if ( element instanceof List) {
				return !((List) element).isEmpty();
			} else if ( element instanceof Entry ) {
				Object value = ((Entry) element).getValue();
				if ( value != null && value instanceof List ) {
					return !((List) value).isEmpty();
				}
			}
			return false; 
		}
	}

	class InfoLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int NAME_COLUMN_INDEX = 0;
		private static final int VALUE_COLUMN_INDEX = 1;
		private static final String EMPTY_STRING = ""; 
		
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if ( element instanceof Entry ) {
				if ( columnIndex == NAME_COLUMN_INDEX ) {
					return ((Entry) element).getKey().toString();
				} else if ( columnIndex == VALUE_COLUMN_INDEX ) {
					return getValue(element);
				}
			}

			return EMPTY_STRING;
		}

		private String getValue(Object element) {
			Object value = ((Entry) element).getValue();
			if ( value != null ) {
				if (value instanceof List) {
					return EMPTY_STRING;
				}
				return value.toString();
			}
			return EMPTY_STRING;
		}
	}
}
