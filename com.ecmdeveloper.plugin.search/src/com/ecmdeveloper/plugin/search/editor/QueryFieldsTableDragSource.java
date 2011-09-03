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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

import com.ecmdeveloper.plugin.search.model.IQueryField;

/**
 * @author ricardo.belfor
 *
 */
public class QueryFieldsTableDragSource extends DragSourceAdapter{

	private TreeViewer viewer;
	
	public QueryFieldsTableDragSource(TreeViewer viewer) {
		this.viewer = viewer;
	}

	public void dragSetData(DragSourceEvent event) {
		event.data = ((StructuredSelection) viewer.getSelection()).getFirstElement();
	}

	public void dragStart(DragSourceEvent event) {
		if ( isSingleSelection() && isQueryFieldSelected() ) {
			event.doit = true;
			event.data = ((StructuredSelection) viewer.getSelection())
					.getFirstElement();
		} else {
			event.doit = false;
		}
	}

	private boolean isQueryFieldSelected() {
		return ((StructuredSelection) viewer
				.getSelection()).getFirstElement() instanceof IQueryField;
	}

	private boolean isSingleSelection() {
		return ((StructuredSelection)viewer.getSelection()).size() == 1;
	}
}
