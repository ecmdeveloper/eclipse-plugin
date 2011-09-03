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

package com.ecmdeveloper.plugin.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.SearchResultRow;
import com.ecmdeveloper.plugin.util.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class SearchResultRowDecorator extends ObjectStoreItemDecorator {

	private static int quadrant = IDecoration.BOTTOM_RIGHT;

	@Override
	public void decorate(Object element, IDecoration decoration) {

		if ( isCheckedOutDocument(element) ) { 
			ImageDescriptor descriptor = Activator.getImageDescriptor(IconFiles.CHECKED_OUT_DECORATOR_IMAGE);
			decoration.addOverlay(descriptor, quadrant);
		}
	}

	private boolean isCheckedOutDocument(Object element) {
		if ( element instanceof SearchResultRow ) {
			IObjectStoreItem objectStoreItem = ((SearchResultRow)element).getObjectValue();
			if ( objectStoreItem instanceof Document ) {
				if ( ((Document)objectStoreItem).isCheckedOut() ) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	protected boolean isDecoratedType(IObjectStoreItem objectStoreItem) {
		return objectStoreItem instanceof Document;
	}
}
