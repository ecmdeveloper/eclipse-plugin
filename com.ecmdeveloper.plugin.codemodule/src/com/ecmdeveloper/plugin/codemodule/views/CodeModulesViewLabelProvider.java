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

package com.ecmdeveloper.plugin.codemodule.views;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.codemodule.Activator;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.util.IconFiles;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoresManager;
import com.ecmdeveloper.plugin.ui.views.ObjectStoreItemLabelProvider;

/**
 * @author Ricardo Belfor
 *
 */
public class CodeModulesViewLabelProvider extends ObjectStoreItemLabelProvider implements ITableLabelProvider {

	private static final int TYPE_COLUMN = 0;
	private static final int NAME_COLUMN = 1;
	private static final int LOCATION_COLUMN = 2;
//	private ObjectStoreItemLabelProvider objectStoreItemLabelProvider;
	private IObjectStoresManager objectStoresManager;
	
	public CodeModulesViewLabelProvider() {
		super();
//		objectStoreItemLabelProvider = new ObjectStoreItemLabelProvider();
		objectStoresManager = Activator.getDefault().getObjectStoresManager();
	}

//	public void addListener(ILabelProviderListener listener) {
//		objectStoreItemLabelProvider.addListener(listener);
//		super.addListener(listener);
//	}
//
//	public void removeListener(ILabelProviderListener listener) {
//		objectStoreItemLabelProvider.removeListener(listener);
//		super.removeListener(listener);
//	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		
		switch ( columnIndex ) {
		case TYPE_COLUMN:
			return Activator.getImage( IconFiles.ICON_CODEMODULE );
		case LOCATION_COLUMN:
			IObjectStore objectStore = getObjectStore(element);
			if ( objectStore == null ) {
				return Activator.getImage( IconFiles.ICON_DATABASE_ERROR );
			}
			return super.getImage( objectStore );
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		
		switch (columnIndex) {
		case TYPE_COLUMN:
			return "";
		case NAME_COLUMN:
			return ((CodeModuleFile) element).getName();
		case LOCATION_COLUMN:
			IObjectStore objectStore = getObjectStore(element);
			if ( objectStore == null ) {
				return MessageFormat.format("Object Store \"{0}:{1}\" is not configured", 
						((CodeModuleFile) element).getConnectionName(),
						((CodeModuleFile) element).getObjectStoreName() );
			}
			return super.getText( getObjectStore(element) );
		default:
			return "";
		}
	}

	private IObjectStore getObjectStore(Object element) {
		CodeModuleFile codeModuleFile = (CodeModuleFile) element; 
		IObjectStore objectStore = objectStoresManager.getObjectStore(codeModuleFile.getConnectionName(), codeModuleFile.getObjectStoreName() );
		return objectStore;
	}
}
