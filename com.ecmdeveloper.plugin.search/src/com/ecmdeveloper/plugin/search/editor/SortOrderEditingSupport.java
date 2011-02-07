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
import org.eclipse.jface.viewers.TableViewer;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.SortOrderUtils;
import com.ecmdeveloper.plugin.search.model.SortType;

/**
 * @author ricardo.belfor
 *
 */
public class SortOrderEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	
	public SortOrderEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected boolean canEdit(Object element) {
		IQueryField queryField = (IQueryField) element;
		return !SortType.NONE.equals( queryField.getSortType() );
	}

	@SuppressWarnings("unchecked")
	@Override
	protected CellEditor getCellEditor(Object element) {
		
		Collection<IQueryField> fields = (Collection<IQueryField>) viewer.getInput();
		ArrayList<String> valuesList = new ArrayList<String>(); 
		for ( IQueryField field : fields) {
			if ( !SortType.NONE.equals( field.getSortType() ) ) {
				valuesList.add( Integer.toString( field.getSortOrder() ) );
			}
		}

		Collections.sort(valuesList);
		String values[] = valuesList.toArray( new String[ valuesList.size()] );
		return new ComboBoxCellEditor( viewer.getTable(), values );
	}

	@Override
	protected Object getValue(Object element) {
		IQueryField queryField = (IQueryField) element;
		if ( queryField.getSortOrder() != 0 ) {
			return queryField.getSortOrder() + 1;
		}

		return -1;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setValue(Object element, Object value) {

		int intValue = ((Integer)value).intValue();
		if ( intValue < 0 ) {
			return;
		}
		
		IQueryField queryField = (IQueryField) element;
		SortOrderUtils.swap((Collection<IQueryField>) viewer.getInput(), intValue + 1,
				queryField.getSortOrder());
		viewer.refresh();
	}
}
