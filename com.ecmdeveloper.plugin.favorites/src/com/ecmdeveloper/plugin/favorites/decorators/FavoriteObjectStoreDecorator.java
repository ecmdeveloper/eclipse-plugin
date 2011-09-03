/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.favorites.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.decorators.ObjectStoreItemDecorator;
import com.ecmdeveloper.plugin.favorites.Activator;
import com.ecmdeveloper.plugin.favorites.model.FavoriteObjectStore;
import com.ecmdeveloper.plugin.favorites.util.IconFiles;
import com.ecmdeveloper.plugin.model.ObjectStore;

/**
 * @author ricardo.belfor
 *
 */
public class FavoriteObjectStoreDecorator extends ObjectStoreItemDecorator {

	@Override
	public void decorate(Object element, IDecoration decoration) {
		FavoriteObjectStore favoriteObjectStore = (FavoriteObjectStore) element;
		ObjectStore objectStore = favoriteObjectStore.getObjectStore();

		ImageDescriptor descriptor = null;
		if ( objectStore.isConnected() ) {
			descriptor = Activator.getImageDescriptor( IconFiles.CONNECTED_DECORATOR_IMAGE );
		} else {
			descriptor = Activator.getImageDescriptor( IconFiles.NOT_CONNECTED_DECORATOR_IMAGE );
		}
		
		decoration.addOverlay(descriptor,IDecoration.BOTTOM_RIGHT);

		ImageDescriptor favoriteDescriptor = Activator.getImageDescriptor( IconFiles.FAVORITE_DECORATOR_IMAGE );
		decoration.addOverlay(favoriteDescriptor, IDecoration.TOP_LEFT );
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	protected boolean isDecoratedType(IObjectStoreItem objectStoreItem) {
		return true;
	}
}
