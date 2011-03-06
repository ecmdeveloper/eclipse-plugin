/**
 * Copyright 2011, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.search.editor;

import java.util.Collection;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.SortOrderUtils;
import com.ecmdeveloper.plugin.search.model.SortType;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class SortTypeEditingSupport extends EditingSupport {

	private final TreeViewer viewer;
	private final QueryFieldsTable queryFieldsTable;
	
	public SortTypeEditingSupport(QueryFieldsTable queryFieldsTable ) {
		super( queryFieldsTable.getTableViewer() );
		this.queryFieldsTable = queryFieldsTable;
		this.viewer = queryFieldsTable.getTableViewer() ;
	}
	
	@Override
	protected boolean canEdit(Object element) {
		if ( element instanceof IQueryField ) {
			IQueryField queryField = (IQueryField) element;
			return queryField.isOrderable();
		}
		return false;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new ComboBoxCellEditor( viewer.getTree(), SortType.getNames() );	
	}

	@Override
	protected Object getValue(Object element) {
		IQueryField queryField = (IQueryField) element;
		return queryField.getSortType().ordinal();
	}

	@Override
	protected void setValue(Object element, Object value) {
		IQueryField queryField = (IQueryField) element;
		Integer integerValue = (Integer) value;
		SortType sortType = SortType.valueOf(integerValue);
		
		if ( integerValue.intValue() < 0 ) {
			return;
		}
		
		if ( !SortType.NONE.equals( sortType ) ) {
			if ( queryField.getSortType().equals( SortType.NONE ) ) {
				int maxQueryField = SortOrderUtils.getMax( getQueryFields() );
				queryField.setSortOrder(maxQueryField+1);
			}
			queryField.setSortType( sortType );
			queryFieldsTable.selectField(queryField);
		} else {
			if ( !queryField.getSortType().equals( SortType.NONE ) ) {
				SortOrderUtils.remove( getQueryFields(), queryField.getSortOrder() );
				queryField.setSortOrder(0);
			} 				
			queryField.setSortType( sortType );
			
		}
		
		viewer.refresh(queryField);
	}

	private Collection<IQueryField> getQueryFields() {
		return ((Query) viewer.getInput()).getQueryFields();
	}
}
