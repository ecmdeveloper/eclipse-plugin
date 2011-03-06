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

package com.ecmdeveloper.plugin.search.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.ecmdeveloper.plugin.search.model.AllQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public class QueryFieldsTable implements PropertyChangeListener  {

	public static final int NAME_COLUMN_INDEX = 0;
	public static final int TYPE_COLUMN_INDEX = 1;
	public static final int SORT_TYPE_COLUMN_INDEX = 2;
	public static final int SORT_ORDER_COLUMN_INDEX = 3;
	
	private static final String NAME_COLUMN_NAME = "Name";
	private static final String TYPE_COLUMN_NAME = "Field Type";
	private static final String SORT_TYPE_COLUMN_NAME = "Sort Type";
	private static final String SORT_ORDER_COLUMN_NAME = "Sort Order";
	
	private CheckboxTreeViewer tableViewer;
	private final Query query;

	public QueryFieldsTable(Query query, Composite parent) {
		this.query = query;
		createTableViewer(parent);
		query.addPropertyChangeListener(this);
	}

	public TreeViewer getTableViewer() {
		return tableViewer;
	}
	
	public void dispose() {
		query.removePropertyChangeListener(this);
	}
	
	public void selectField(IQueryField queryField) {
		queryField.setSelected(true);
		tableViewer.setChecked(queryField, true);
	}
	
	private void createTableViewer(Composite parent) {
		
		tableViewer = new CheckboxTreeViewer( parent, SWT.BORDER | SWT.FULL_SELECTION );
		Tree table = tableViewer.getTree();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTableViewerColumn(NAME_COLUMN_NAME, 200, NAME_COLUMN_INDEX );
		createTableViewerColumn(TYPE_COLUMN_NAME, 100, TYPE_COLUMN_INDEX );
		TreeViewerColumn column = createTableViewerColumn(SORT_TYPE_COLUMN_NAME, 100, SORT_TYPE_COLUMN_INDEX );
		column.setEditingSupport( new SortTypeEditingSupport( this) );

		column = createTableViewerColumn(SORT_ORDER_COLUMN_NAME, 100, SORT_ORDER_COLUMN_INDEX );
		column.setEditingSupport( new SortOrderEditingSupport( tableViewer ) );

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider( new QueryContentProvider() );
		tableViewer.setLabelProvider( new TableViewLabelProvider() );

		tableViewer.addCheckStateListener( new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object element = event.getElement();

				if ( element instanceof AllQueryField) {
					if ( event.getChecked() ) {
						tableViewer.setAllChecked(true);
//						tableViewer.setAllGrayed(true);
						tableViewer.setGrayed(element, false);
						query.setQueryFieldsSelection(true);
					} else {
//						tableViewer.setAllGrayed(false);
					}
				} else {
					if ( tableViewer.getGrayed(element ) ) {
						tableViewer.setChecked(element, true);
					} else {
						if ( element instanceof IQueryField ) {
							((IQueryField) element).setSelected( event.getChecked() );
						} else if ( element instanceof IQueryTable ) {
							// TODO
						}
					}
				}
			}} );

		tableViewer.setInput( query );
		tableViewer.expandToLevel(2);
//			getSite().setSelectionProvider(tableViewer);
	}

	private TreeViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TreeViewerColumn viewerColumn = new TreeViewerColumn(tableViewer,
				SWT.CHECK);
		final TreeColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		if ( propertyChangeEvent.getPropertyName().equals( Query.TABLE_ADDED ) ||
				propertyChangeEvent.getPropertyName().equals( Query.TABLE_REMOVED ) ) {
			tableViewer.refresh();
		}
		else if ( propertyChangeEvent.getPropertyName().equals( Query.TOGGLE_INCLUDE_SUBCLASSES ) ) {
			tableViewer.refresh();
			tableViewer.expandToLevel(2);
		}
	}
}
