/**
 * Copyright 2013, Ricardo Belfor
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

package com.ecmdeveloper.plugin.folderview.views;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.ecmdeveloper.plugin.core.model.ICustomObject;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.Placeholder;
import com.ecmdeveloper.plugin.folderview.Activator;
import com.ecmdeveloper.plugin.folderview.model.Category;
import com.ecmdeveloper.plugin.folderview.utils.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class FolderViewTreeLabelProvider extends CellLabelProvider {

	public FolderViewTreeLabelProvider() {
	}

	@Override
	public void update(ViewerCell cell) {

		if ( cell.getElement() instanceof IObjectStoreItem) {
			IObjectStoreItem item = (IObjectStoreItem) cell.getElement();
			cell.setText( item.getDisplayName() );
			cell.setImage( getImage(cell.getElement() ) );
		}
		
		if ( cell.getElement() instanceof Category) {
			Category category = (Category) cell.getElement();
			String text = MessageFormat.format("{0}: {1}", category.getName(), category.getValueString() );
			cell.setText( text );
			int level = category.getLevel();
			int imageIndex = level % IconFiles.LABELS.length;
			cell.setImage( Activator.getImage( IconFiles.LABELS[imageIndex] ) );
		}
	}

	private Image getStandardImage(String imageKey) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	public Image getImage(Object obj) {

		if ( obj instanceof Placeholder ){
	         return ((Placeholder)obj).getImage();
		} else if (obj instanceof IFolder) {
		   return getDecoratedImage( obj, getStandardImage(ISharedImages.IMG_OBJ_FOLDER ) );
		} else if (obj instanceof IDocument) {
	        return getDecoratedImage(obj, getStandardImage(ISharedImages.IMG_OBJ_FILE ) );
		} else if (obj instanceof ICustomObject) {
		   return getDecoratedImage(obj, getStandardImage( ISharedImages.IMG_OBJ_ELEMENT ) );
		}
		
		return getStandardImage(ISharedImages.IMG_OBJS_INFO_TSK);
	}

	private Image getDecoratedImage(Object obj, Image standardImage) {
		return standardImage;
	}
}
