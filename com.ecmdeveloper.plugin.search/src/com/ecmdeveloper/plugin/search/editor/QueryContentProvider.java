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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public class QueryContentProvider implements IStructuredContentProvider,
	ITreeContentProvider{

	private Viewer viewer;
	private Query query;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		
//		if (query != null) {
//			codeModuleFile.removePropertyFileListener(this);
//		}
		
		if ( newInput instanceof Query ) {
			query = (Query) newInput;
			
//			if (codeModuleFile != null) {
//				codeModuleFile.addPropertyFileListener(this);
//			}
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if ( parentElement instanceof IQueryTable ) {
			return getQueryTableChildren(parentElement);
		} else if ( parentElement instanceof IQueryField ) {
			return null;
		} else {
			return query.getQueryTables().toArray();
		}
	}

	private Object[] getQueryTableChildren(Object parentElement) {
		
		IQueryTable queryTable = (IQueryTable)parentElement;
		Collection<Object> children = new ArrayList<Object>();
		if ( query.isIncludeSubclasses() ) {
			children.addAll( queryTable.getChildQueryTables() );
		}
		children.addAll( queryTable.getQueryFields() );
		return children.toArray();
	}

	@Override
	public Object getParent(Object element) {
		if ( element instanceof IQueryTable ) {
			return query;
		} else if ( element instanceof IQueryField ) {
			return ((IQueryField)element).getQueryTable();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if ( element instanceof IQueryTable ) {
			return !((IQueryTable)element).getQueryFields().isEmpty();
		} else if ( element instanceof IQueryField ) {
			return false;
		}
		return false;
	}

}
