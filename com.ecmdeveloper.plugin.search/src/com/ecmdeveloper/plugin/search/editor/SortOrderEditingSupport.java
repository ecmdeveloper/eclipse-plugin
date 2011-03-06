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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.SortOrderUtils;
import com.ecmdeveloper.plugin.search.model.SortType;

/**
 * @author ricardo.belfor
 *
 */
public class SortOrderEditingSupport extends EditingSupport {

	private final TreeViewer viewer;
	
	public SortOrderEditingSupport(TreeViewer tableViewer) {
		super(tableViewer);
		this.viewer = tableViewer;
	}

	@Override
	protected boolean canEdit(Object element) {
		if ( element instanceof IQueryField) {
			IQueryField queryField = (IQueryField) element;
			return !SortType.NONE.equals( queryField.getSortType() );
		}
		return false;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		
		Collection<IQueryField> fields = getQuery().getQueryFields();
		ArrayList<String> valuesList = new ArrayList<String>(); 
		for ( IQueryField field : fields) {
			if ( !SortType.NONE.equals( field.getSortType() ) ) {
				valuesList.add( Integer.toString( field.getSortOrder() ) );
			}
		}

		Collections.sort(valuesList);
		String values[] = valuesList.toArray( new String[ valuesList.size()] );
		return new ComboBoxCellEditor( viewer.getTree(), values );
	}

	private Query getQuery() {
		return (Query) viewer.getInput();
	}

	@Override
	protected Object getValue(Object element) {
		IQueryField queryField = (IQueryField) element;
		if ( queryField.getSortOrder() != 0 ) {
			return queryField.getSortOrder() + 1;
		}

		return -1;
	}

	@Override
	protected void setValue(Object element, Object value) {

		int intValue = ((Integer)value).intValue();
		if ( intValue < 0 ) {
			return;
		}
		
		IQueryField queryField = (IQueryField) element;
		SortOrderUtils.swap( getQuery().getQueryFields(), intValue + 1,
				queryField.getSortOrder());
		viewer.refresh();
	}
}
