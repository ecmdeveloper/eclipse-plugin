package com.ecmdeveloper.plugin.search.editor;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryTable;

public class TableViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		IQueryField queryField = (IQueryField) element;
		if ( columnIndex == QueryFieldsTable.NAME_COLUMN_INDEX) {
			return queryField.getName(); 
		} else if ( columnIndex == QueryFieldsTable.TABLE_COLUMN_INDEX) {
				IQueryTable queryTable = queryField.getQueryTable();
				if ( queryTable != null ) {
					return queryTable.getName();
				} else {
					return "";
				}
		} else if ( columnIndex == QueryFieldsTable.TYPE_COLUMN_INDEX ) {
			return queryField.getType().toString();
		} else if ( columnIndex == QueryFieldsTable.SORT_TYPE_COLUMN_INDEX ) {
			return queryField.getSortType().toString();
		} else if ( columnIndex == QueryFieldsTable.SORT_ORDER_COLUMN_INDEX ) {
			if ( queryField.getSortOrder() != 0 ) {
				return Integer.toString( queryField.getSortOrder() );
			}
		}
		return "";
	}
}
