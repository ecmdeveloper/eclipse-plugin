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
package com.ecmdeveloper.plugin.classes.views;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.ecmdeveloper.plugin.classes.Activator;
import com.ecmdeveloper.plugin.core.model.ClassesPlaceholder;
import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IClassDescriptionFolder;
import com.ecmdeveloper.plugin.ui.views.ObjectStoreItemLabelProvider;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassesViewLabelProvider extends ObjectStoreItemLabelProvider {

	@Override
	public Image getImage(Object object) {
		if ( object instanceof IClassDescriptionFolder ) {
			String imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		} else if ( object instanceof ClassesPlaceholder ) {
			return ((ClassesPlaceholder) object).getImage();
		} else if ( object instanceof IClassDescription ) {
			return Activator.getImage("icons/table.png" );
		}
		return super.getImage(object);
	}

	@Override
	public String getText(Object object) {
		if ( object instanceof IClassDescriptionFolder ) {
			return ((IClassDescriptionFolder) object).getName();
		} else if ( object instanceof ClassesPlaceholder ) {
			return object.toString();
		} else if ( object instanceof IClassDescription ) {
			return object.toString();
		}
		return super.getText(object);
	}

}
