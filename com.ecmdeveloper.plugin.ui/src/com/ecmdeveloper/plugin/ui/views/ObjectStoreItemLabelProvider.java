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
package com.ecmdeveloper.plugin.ui.views;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.ecmdeveloper.plugin.ui.Activator;
import com.ecmdeveloper.plugin.core.model.IAction;
import com.ecmdeveloper.plugin.core.model.ICustomObject;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.Placeholder;
import com.ecmdeveloper.plugin.ui.util.IconFiles;

public class ObjectStoreItemLabelProvider extends LabelProvider {

	protected IDecoratorManager decorator;

	
	public ObjectStoreItemLabelProvider() {
		super();
		decorator = PlatformUI.getWorkbench().getDecoratorManager();
	}
	
	public void addListener(ILabelProviderListener listener) {
		decorator.addListener(listener);
		super.addListener(listener);
	}

	public void removeListener(ILabelProviderListener listener) {
		decorator.removeListener(listener);
		super.removeListener(listener);
	}
	
   @Override
	public void dispose() {
		super.dispose();
	}

	public String getText(Object object) {

		if ( object instanceof IObjectStoreItem )
		{
			if ( object instanceof IObjectStore ) {
				IObjectStore objectStore = (IObjectStore) object;
				return objectStore.getConnection().toString() + ":" + objectStore.getDisplayName();
			} else {
				return ((IObjectStoreItem) object).getName();
			}
		}
		return object.toString();
	}
	
	public Image getImage(Object obj) {

		if ( obj instanceof Placeholder ){
	         return ((Placeholder)obj).getImage();
		} else if (obj instanceof IObjectStore) {
	         return getDecoratedImage(obj, IconFiles.ICON_OBJECTSTORE );
		} else if ( obj instanceof IAction ) {
			return Activator.getImage( IconFiles.ICON_ACTION );
		} else if (obj instanceof IFolder) {
		   return getDecoratedImage( obj, getStandardImage(ISharedImages.IMG_OBJ_FOLDER ) );
		} else if (obj instanceof IDocument) {
	        return getDecoratedImage(obj, getStandardImage(ISharedImages.IMG_OBJ_FILE ) );
		} else if (obj instanceof ICustomObject) {
		   return getDecoratedImage(obj, getStandardImage( ISharedImages.IMG_OBJ_ELEMENT ) );
		}
		
		return getStandardImage(ISharedImages.IMG_OBJS_INFO_TSK);
	}

	private Image getStandardImage(String imageKey) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
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
