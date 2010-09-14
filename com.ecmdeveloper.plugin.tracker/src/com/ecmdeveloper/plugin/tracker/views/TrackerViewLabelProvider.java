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

package com.ecmdeveloper.plugin.tracker.views;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.ecmdeveloper.plugin.tracker.model.TrackedFile;

/**
 * @author ricardo.belfor
 *
 */
public class TrackerViewLabelProvider extends WorkbenchLabelProvider implements ITableLabelProvider {

	private static final int NAME_COLUMN = 0;
	private static final int PATH_COLUMN = 1;
	private static final int LOCATION_COLUMN = 2;

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if ( columnIndex == NAME_COLUMN ) {
			TrackedFile trackedFile = (TrackedFile) element;
			return super.getImage( trackedFile.getFile() );
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		
		TrackedFile trackedFile = (TrackedFile) element;

		if ( columnIndex == NAME_COLUMN ) {
			return super.getText(trackedFile.getFile());
		} else if ( columnIndex == LOCATION_COLUMN ) {
			return MessageFormat.format("{0}:{1}", trackedFile.getConnectionDisplayName(),
					trackedFile.getObjectStoreDisplayName());
		} else if ( columnIndex == PATH_COLUMN ) {
			return trackedFile.getFile().getParent().getFullPath().toString();
		}
		return "";
	}

	@Override
	protected String decorateText(String input, Object element) {
		return input;
	}
}
