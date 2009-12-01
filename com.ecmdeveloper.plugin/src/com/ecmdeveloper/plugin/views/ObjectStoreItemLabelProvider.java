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
package com.ecmdeveloper.plugin.views;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.model.Action;
import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.Placeholder;
import com.ecmdeveloper.plugin.util.IconFiles;

public class ObjectStoreItemLabelProvider extends LabelProvider {

	final IDecoratorManager decorator;

	
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
			if ( object instanceof ObjectStore ) {
				ObjectStore objectStore = (ObjectStore) object;
				return objectStore.getConnection().toString() + ":" + objectStore.getDisplayName();
			} else {
				return ((IObjectStoreItem) object).getName();
			}
		}
		return object.toString();
	}
	
	public Image getImage(Object obj) {

		String imageKey = ISharedImages.IMG_OBJS_INFO_TSK;
		
		if ( obj instanceof Placeholder ){
	         return Activator.getImage( IconFiles.HOURGLASS );
		} else if (obj instanceof ObjectStore) {
			
	         Image image = Activator.getImage( IconFiles.ICON_OBJECTSTORE );
	         Image decorated = decorator.decorateImage(image, obj);
	         if (decorated != null)
	            return decorated;
	         return image;

		} else if ( obj instanceof Action ) {
			return Activator.getImage( IconFiles.ICON_ACTION );
		} else if (obj instanceof Folder) {
		   imageKey = ISharedImages.IMG_OBJ_FOLDER;
		} else if (obj instanceof Document) {
		   imageKey = ISharedImages.IMG_OBJ_FILE;
		} else if (obj instanceof CustomObject) {
		   imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		}
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}
