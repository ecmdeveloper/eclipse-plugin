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

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.folderview.Activator;
import com.ecmdeveloper.plugin.folderview.model.Category;
import com.ecmdeveloper.plugin.folderview.utils.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class FolderViewColumnLabelProvider extends CellLabelProvider {

	private final String columnName;
	private final int columnIndex;
	
	public FolderViewColumnLabelProvider(String columnName, int columnIndex) {
		this.columnName = columnName;
		this.columnIndex = columnIndex;
	}

	@Override
	public void update(ViewerCell cell) {
		if ( cell.getElement() instanceof IObjectStoreItem) {
			IObjectStoreItem item = (IObjectStoreItem) cell.getElement();
			if ( columnIndex == 0) {
				cell.setText( item.getDisplayName() );
			} else{
				Object value = item.getSafeValue(columnName);
				cell.setText( value != null ? value.toString() : "" );
			}
		}
		
		if ( cell.getElement() instanceof Category) {
			if ( columnIndex == 0) {
				Category category = (Category) cell.getElement();
				String text = MessageFormat.format("{0}: {1}", category.getName(), category.getValueString() );
				cell.setText( text );
				int level = category.getLevel();
				int imageIndex = level % IconFiles.LABELS.length;
				cell.setImage( Activator.getImage( IconFiles.LABELS[imageIndex] ) );
			}
		}
	}
}
