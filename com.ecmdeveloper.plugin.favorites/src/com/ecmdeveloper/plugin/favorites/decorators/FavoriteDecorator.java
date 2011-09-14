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
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import com.ecmdeveloper.plugin.favorites.Activator;
import com.ecmdeveloper.plugin.favorites.model.FavoritesManager;
import com.ecmdeveloper.plugin.favorites.util.IconFiles;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;

/**
 * @author ricardo.belfor
 *
 */
public class FavoriteDecorator implements ILightweightLabelDecorator {

	@Override
	public void decorate(Object element, IDecoration decoration) {

		IObjectStoreItem objectStoreItem = (IObjectStoreItem) element;

		if ( FavoritesManager.getInstance().isFavorite(objectStoreItem) ) {
			ImageDescriptor descriptor = Activator.getImageDescriptor(IconFiles.FAVORITE_DECORATOR_IMAGE);
			decoration.addOverlay(descriptor, IDecoration.TOP_LEFT);
		}
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

}
