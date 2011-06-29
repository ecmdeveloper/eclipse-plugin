package com.ecmdeveloper.plugin.search.editor;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.util.IconFiles;

public class TableViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if ( columnIndex == QueryFieldsTable.NAME_COLUMN_INDEX ) {
			if ( element instanceof IQueryTable ) {
				return Activator.getImage( IconFiles.TABLE_FOLDER );
			} else if ( element instanceof IQueryField ) {
				if ( ((IQueryField) element).isQueryField() ) {
					return Activator.getImage( IconFiles.SEARCHABLE_QUERY_FIELD );
				}
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		
		if (element instanceof IQueryTable ) {
			return getQueryTableColumnText( (IQueryTable) element, columnIndex);
		} else if ( element instanceof IQueryField ) {
			return getQueryFieldColumnText( (IQueryField) element, columnIndex);
		}
		return "";
	}

	private String getQueryTableColumnText(IQueryTable queryTable, int columnIndex) {
		if ( columnIndex == QueryFieldsTable.NAME_COLUMN_INDEX) {
			return queryTable.getName(); 
		} else if ( columnIndex == QueryFieldsTable.ALIAS_COLUMN_INDEX ) {
			return queryTable.getAlias();
		}
		return "";
	}

	private String getQueryFieldColumnText(IQueryField queryField, int columnIndex) {
		
		if ( columnIndex == QueryFieldsTable.NAME_COLUMN_INDEX) {
			return queryField.getName(); 
		} else if ( columnIndex == QueryFieldsTable.TYPE_COLUMN_INDEX ) {
			return queryField.getType().toString();
		} else if ( columnIndex == QueryFieldsTable.ALIAS_COLUMN_INDEX ) {
			return queryField.getAlias();
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
