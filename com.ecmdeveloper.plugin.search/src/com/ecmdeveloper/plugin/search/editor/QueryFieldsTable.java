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

import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.util.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class QueryFieldsTable implements PropertyChangeListener  {

	public static final int NAME_COLUMN_INDEX = 0;
	public static final int TYPE_COLUMN_INDEX = 1;
	public static final int ALIAS_COLUMN_INDEX = 2;
	public static final int SORT_TYPE_COLUMN_INDEX = 3;
	public static final int SORT_ORDER_COLUMN_INDEX = 4;
	
	private static final String NAME_COLUMN_NAME = "Name";
	private static final String TYPE_COLUMN_NAME = "Field Type";
	private static final String SORT_TYPE_COLUMN_NAME = "Sort Type";
	private static final String SORT_ORDER_COLUMN_NAME = "Sort Order";
	private static final String ALIAS_COLUMN_NAME = "Alias";
	
	private CheckboxTreeViewer tableViewer;
	private final Query query;

	class BaseColumnLabelProvider extends ColumnLabelProvider {
		
		@Override
		public Color getForeground(Object element) {
			if ( element instanceof IQueryField ) {
				if ( !((IQueryField) element).isSelectable() ) {
					return tableViewer.getTree().getDisplay().getSystemColor(SWT.COLOR_GRAY );
				}
			}
			return super.getForeground(element);
		}
	}
	
	public QueryFieldsTable(Query query, Composite parent) {
		this.query = query;
		createTableViewer(parent);
		addDragSupport();
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

		createNameColumn();
		createTypeColumn();
		createAliasColumn();
		createSortTypeColumn();
		createSortOrderColumn();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider( new QueryContentProvider() );
		tableViewer.addCheckStateListener( getCheckStateListener() );
		tableViewer.setInput( query );
		refreshTableViewer();
	}

	private void createNameColumn() {
		TreeViewerColumn nameColumn = createTableViewerColumn(NAME_COLUMN_NAME, 200, NAME_COLUMN_INDEX );
		nameColumn.setLabelProvider( new ColumnLabelProvider() {

			@Override
			public Image getImage(Object element) {
				if ( element instanceof IQueryTable ) {
					return Activator.getImage( IconFiles.TABLE_FOLDER );
				} else if ( element instanceof IQueryField ) {
					if ( ((IQueryField) element).isQueryField() ) {
						return Activator.getImage( IconFiles.SEARCHABLE_QUERY_FIELD );
					}
				}				
				return super.getImage(element);
			}} );
	}

	private void createAliasColumn() {
		TreeViewerColumn column = createTableViewerColumn(ALIAS_COLUMN_NAME, 100, ALIAS_COLUMN_INDEX );
		column.setEditingSupport(new AliasEditingSupport(tableViewer) );
		column.setLabelProvider( new BaseColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if ( element instanceof IQueryField ) {
					return ((IQueryField) element).getAlias();
				} else if (element instanceof IQueryTable ) {
					return ((IQueryTable) element).getAlias();
				}
				return "";
			}});
	}

	private void createSortOrderColumn() {
		TreeViewerColumn column;
		column = createTableViewerColumn(SORT_ORDER_COLUMN_NAME, 100, SORT_ORDER_COLUMN_INDEX );
		column.setEditingSupport( new SortOrderEditingSupport( tableViewer ) );
		column.setLabelProvider( new BaseColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if ( element instanceof IQueryField ) {
					IQueryField queryField = (IQueryField) element;
					if ( queryField.getSortOrder() != 0 ) {
						return Integer.toString( queryField.getSortOrder() );
					}
				}
				return "";
			}} );
	}

	private void createSortTypeColumn() {
		TreeViewerColumn column;
		column = createTableViewerColumn(SORT_TYPE_COLUMN_NAME, 100, SORT_TYPE_COLUMN_INDEX );
		column.setEditingSupport( new SortTypeEditingSupport( this) );
		column.setLabelProvider( new BaseColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if ( element instanceof IQueryField ) {
					return ((IQueryField) element).getSortType().toString();
				}
				return "";
			}} );
	}

	private void createTypeColumn() {
		TreeViewerColumn typeColumn = createTableViewerColumn(TYPE_COLUMN_NAME, 100, TYPE_COLUMN_INDEX );
		typeColumn.setLabelProvider( new BaseColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if ( element instanceof IQueryField ) {
					return ((IQueryField) element).getType().toString();
				}
				return "";
			} } );
	}

	private ICheckStateListener getCheckStateListener() {
		return new ICheckStateListener() {
	
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object element = event.getElement();
				if ( element instanceof IQueryTable ) {
					IQueryTable queryTable = (IQueryTable) element;
					for (IQueryField queryField: queryTable.getQueryFields() ) {
						queryField.setSelected( event.getChecked() );
						tableViewer.setChecked(queryField, queryField.isSelected() );
					}
				} else if ( element instanceof IQueryField ) {
					((IQueryField) element).setSelected( event.getChecked() );
				}
			}};
	}

	private TreeViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		
		final TreeViewerColumn viewerColumn = new TreeViewerColumn(tableViewer, SWT.CHECK);
		final TreeColumn column = viewerColumn.getColumn();
		
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);

		return viewerColumn;
	}

	private void addDragSupport() {
		tableViewer.addDragSupport(DND.DROP_COPY, new Transfer[] { TemplateTransfer.getInstance() },
				new QueryFieldsTableDragSource(tableViewer));
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		if ( propertyChangeEvent.getPropertyName().equals( Query.TABLE_ADDED ) ||
				propertyChangeEvent.getPropertyName().equals( Query.TABLE_REMOVED ) ) {
			refreshTableViewer();
		}
		else if ( propertyChangeEvent.getPropertyName().equals( Query.TOGGLE_INCLUDE_SUBCLASSES ) ) {
			refreshTableViewer();
		}
	}

	private void refreshTableViewer() {
		tableViewer.refresh();
		tableViewer.expandToLevel(2);
		tableViewer.setCheckedElements( query.getSelectedQueryFields().toArray() );
	}
}
