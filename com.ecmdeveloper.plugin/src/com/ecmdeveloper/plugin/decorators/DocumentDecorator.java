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

package com.ecmdeveloper.plugin.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.model.Document;

/**
 * @author Ricardo.Belfor
 *
 */
public class DocumentDecorator extends ObjectStoreItemDecorator {

	private static final String CHECKED_OUT_DECORATOR_IMAGE = "icons/check.png"; //$NON-NLS-1$
	private static int quadrant = IDecoration.BOTTOM_RIGHT;

	@Override
	public void decorate(Object element, IDecoration decoration) {

		Document document = (Document) element;

		if (document.isCheckedOut()) {
			ImageDescriptor descriptor = Activator.getImageDescriptor(CHECKED_OUT_DECORATOR_IMAGE);
			decoration.addOverlay(descriptor, quadrant);
		}
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
}
