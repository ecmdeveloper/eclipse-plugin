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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.ecmdeveloper.plugin.search.model.AllQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public class QueryFieldsTable implements PropertyChangeListener  {

	public static final int NAME_COLUMN_INDEX = 0;
	public static final int TABLE_COLUMN_INDEX = 1;
	public static final int TYPE_COLUMN_INDEX = 2;
	public static final int SORT_TYPE_COLUMN_INDEX = 3;
	public static final int SORT_ORDER_COLUMN_INDEX = 4;
	
	private static final String NAME_COLUMN_NAME = "Name";
	private static final String TABLE_COLUMN_NAME = "Table";
	private static final String TYPE_COLUMN_NAME = "Field Type";
	private static final String SORT_TYPE_COLUMN_NAME = "Sort Type";
	private static final String SORT_ORDER_COLUMN_NAME = "Sort Order";
	
	private CheckboxTableViewer tableViewer;
	private final Query query;

	public QueryFieldsTable(Query query, Composite parent) {
		this.query = query;
		createTableViewer(parent);
		query.addPropertyChangeListener(this);
	}

	public TableViewer getTableViewer() {
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
		
/*
		tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER);

		final Table table = tableViewer.getTable();
		TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);

		TableColumn typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setText("");
		layout.setColumnData(typeColumn, new ColumnPixelData(18));

		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		layout.setColumnData(nameColumn, new ColumnWeightData(1));

		TableColumn objectStoreColumn = new TableColumn(table, SWT.LEFT);
		objectStoreColumn.setText("Object Store");
		layout.setColumnData(objectStoreColumn, new ColumnWeightData(1));

 */
		tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.FULL_SELECTION );
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		//				TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		//				nameColumn.setText("Name");
		//;		layout.setColumnData(nameColumn, new ColumnWeightData(1));

		createTableViewerColumn(NAME_COLUMN_NAME, 200, NAME_COLUMN_INDEX );
		createTableViewerColumn(TABLE_COLUMN_NAME, 100, TABLE_COLUMN_INDEX );
		createTableViewerColumn(TYPE_COLUMN_NAME, 100, TYPE_COLUMN_INDEX );
		TableViewerColumn column = createTableViewerColumn(SORT_TYPE_COLUMN_NAME, 100, SORT_TYPE_COLUMN_INDEX );
		column.setEditingSupport( new SortTypeEditingSupport( this) );

		column = createTableViewerColumn(SORT_ORDER_COLUMN_NAME, 100, SORT_ORDER_COLUMN_INDEX );
		column.setEditingSupport( new SortOrderEditingSupport( tableViewer ) );

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider( new ArrayContentProvider() );
		tableViewer.setLabelProvider( new TableViewLabelProvider() );
		tableViewer.setInput( query.getQueryFields() );

		tableViewer.addCheckStateListener( new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object element = event.getElement();

				if ( element instanceof AllQueryField) {
					if ( event.getChecked() ) {
						tableViewer.setAllChecked(true);
						tableViewer.setAllGrayed(true);
						tableViewer.setGrayed(element, false);
						query.setQueryFieldsSelection(true);
					} else {
						tableViewer.setAllGrayed(false);
					}
				} else {
					if ( tableViewer.getGrayed(element ) ) {
						tableViewer.setChecked(element, true);
					} else {
						((IQueryField) element).setSelected( event.getChecked() );
					}
				}
			}} );

//			getSite().setSelectionProvider(tableViewer);
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer,
				SWT.CHECK);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		if ( propertyChangeEvent.getPropertyName().equals( Query.TABLE_ADDED ) ||
				propertyChangeEvent.getPropertyName().equals( Query.TABLE_REMOVED ) )
		{
			tableViewer.setInput( query.getQueryFields() );
			tableViewer.refresh();
		}
	}
}
