/**
 * Copyright 2009,2010, Ricardo Belfor
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
package com.ecmdeveloper.plugin.properties.editors;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.properties.Activator;
import com.ecmdeveloper.plugin.properties.model.Property;
import com.ecmdeveloper.plugin.properties.util.IconFiles;

/**
 * @author Ricardo.Belfor
 *
 */
public class PropertyLabelProvider implements ILabelProvider {

	private static final String SEPARATOR = " - ";

	@Override
	public Image getImage(Object element) {

		if ( ! (element instanceof Property) ) {
			throw new UnsupportedOperationException( "This type is not supported for this label provider" );
		}
		
		Property property = (Property) element;
		if ( ! property.isSettableOnEdit() ) {
			return Activator.getImage( IconFiles.READ_ONLY_SMALL );
		}

		if ( property.isRequired() ) {
			return Activator.getImage( IconFiles.REQUIRED );
		}
		
		return Activator.getImage(IconFiles.EMPTY);
	}

	@Override
	public String getText(Object element) {
		
		if ( ! (element instanceof Property) ) {
			throw new UnsupportedOperationException( "This type is not supported for this label provider" );
		}
		
		Property property = (Property) element;
		return property.getDisplayName() + SEPARATOR  + property.getValueAsString();
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}
}
