package com.ecmdeveloper.plugin.search.editor;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.search.model.IQueryField;

public class TableViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		IQueryField queryField = (IQueryField) element;
		if ( columnIndex == 0) {
			return queryField.getName(); 
		} else if ( columnIndex == 1 ) {
			return queryField.getType().toString();
		} else if ( columnIndex == 2 ) {
			return queryField.getSortType().toString();
		} else if ( columnIndex == 3 ) {
			if ( queryField.getSortOrder() != 0 ) {
				return Integer.toString( queryField.getSortOrder() );
			}
		}
		return "";
	}
}
