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

package com.ecmdeveloper.plugin.cmis.views;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author ricardo.belfor
 *
 */
public class CmisObjectLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof CmisObject ) {
			return ((CmisObject) element).getName();
		} else if ( element instanceof Repository ) {
			return ((Repository) element).getName();
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if ( element instanceof Folder ) {
			return getStandardImage(ISharedImages.IMG_OBJ_FOLDER );			
		} else if ( element instanceof Document ) {
			return getStandardImage(ISharedImages.IMG_OBJ_FILE );
		}

		return super.getImage(element);
	}
	
	private Image getStandardImage(String imageKey) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}
