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

package com.ecmdeveloper.plugin.favorites.views;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;

import com.ecmdeveloper.plugin.favorites.Activator;
import com.ecmdeveloper.plugin.favorites.model.FavoriteObjectStore;
import com.ecmdeveloper.plugin.views.ObjectStoreItemLabelProvider;

/**
 * @author ricardo.belfor
 *
 */
public class FavoritesLabelProvider extends ObjectStoreItemLabelProvider {

//	private IDecoratorManager decorator;
//	
//	public FavoritesLabelProvider() {
//		super();
//		decorator = PlatformUI.getWorkbench().getDecoratorManager();
//	}
//
//	public void addListener(ILabelProviderListener listener) {
//		decorator.addListener(listener);
//		super.addListener(listener);
//	}
//
//	public void removeListener(ILabelProviderListener listener) {
//		decorator.removeListener(listener);
//		super.removeListener(listener);
//	}

	@Override
	public Image getImage(Object object) {
		if ( object instanceof FavoriteObjectStore ) {
			
			return getDecoratedImage(object, "icons/database.png");
			
//			return super.getImage(favoriteObjectStore.getObjectStore() );
		}
		return super.getImage(object);
	}

	@Override
	public String getText(Object object) {
		if ( object instanceof FavoriteObjectStore ) {
			FavoriteObjectStore favoriteObjectStore = (FavoriteObjectStore) object;
			return super.getText(favoriteObjectStore.getObjectStore() );
		}
		return super.getText(object);
	}

	private Image getDecoratedImage(Object obj, String iconFile) {
		Image image = Activator.getImage(iconFile);
		return getDecoratedImage(obj, image);
	}

	private Image getDecoratedImage(Object obj, Image image) {
		Image decorated = decorator.decorateImage(image, obj);
		if (decorated != null)
			return decorated;
		return image;
	}
}
