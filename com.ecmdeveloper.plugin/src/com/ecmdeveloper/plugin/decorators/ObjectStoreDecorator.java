/**
 * Copyright 2009, Ricardo Belfor
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
package com.ecmdeveloper.plugin.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;

/**
 * This class decorates the Object Store object. The decoration is changed based
 * on the fact if the Object Store is connected or not connected.
 * 
 * @author Ricardo Belfor
 */
public class ObjectStoreDecorator extends ObjectStoreItemDecorator {

	private static final String NOT_CONNECTED_DECORATOR_IMAGE = "icons/bullet_red.png"; //$NON-NLS-1$
	private static final String CONNECTED_DECORATOR_IMAGE = "icons/bullet_green.png"; //$NON-NLS-1$
	private static int quadrant = IDecoration.BOTTOM_RIGHT;

	/**
	 * Decorates the element.
	 * 
	 * @param element the element
	 * @param decoration the decoration
	 * 
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) {

		ObjectStore objectStore = (ObjectStore) element;

		ImageDescriptor descriptor = null;
		if ( objectStore.isConnected() ) {
			descriptor = Activator.getImageDescriptor( CONNECTED_DECORATOR_IMAGE );
		} else {
			descriptor = Activator.getImageDescriptor( NOT_CONNECTED_DECORATOR_IMAGE );
		}
		decoration.addOverlay(descriptor,quadrant);
	}

	/**
	 * Checks if this is a label property.
	 * 
	 * @param element the element
	 * @param property the property
	 * 
	 * @return true, if checks if is label property
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
	
	protected boolean isDecoratedType(IObjectStoreItem objectStoreItem) {
		return objectStoreItem instanceof ObjectStore;
	}
}