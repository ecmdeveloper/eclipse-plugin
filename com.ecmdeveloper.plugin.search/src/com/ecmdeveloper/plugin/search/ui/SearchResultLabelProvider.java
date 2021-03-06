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

package com.ecmdeveloper.plugin.search.ui;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.ecmdeveloper.plugin.core.model.ICustomObject;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.ISearchResultRow;
import com.ecmdeveloper.plugin.ui.views.ObjectStoreItemLabelProvider;

/**
 * @author ricardo.belfor
 *
 */
public class SearchResultLabelProvider extends ObjectStoreItemLabelProvider implements ITableLabelProvider {

	protected IDecoratorManager decorator;
	
	private HashMap<Integer,String> indexToNameMap = new HashMap<Integer, String>();

	public SearchResultLabelProvider() {
		super();
		decorator = PlatformUI.getWorkbench().getDecoratorManager();
	}
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof ISearchResultRow) {
			ISearchResultRow searchResultRow = (ISearchResultRow)element;
			String name = indexToNameMap.get(Integer.valueOf(columnIndex) );
			if ( name != null ) {
				if ( name.equals("This") & searchResultRow.isHasObjectValue() ) {
					IObjectStoreItem obj = searchResultRow.getObjectValue();
					return getObjectStoreItemImage(obj, searchResultRow);					
				} 
			} 
		}
		return null;
	}

	private Image getObjectStoreItemImage(IObjectStoreItem obj, ISearchResultRow searchResultRow) {
		if (obj instanceof IFolder) {
			return getDecoratedImage( searchResultRow, getStandardImage(ISharedImages.IMG_OBJ_FOLDER ) );
		} else if (obj instanceof IDocument) {
			return getDecoratedImage(searchResultRow, getStandardImage(ISharedImages.IMG_OBJ_FILE ) );
		} else if (obj instanceof ICustomObject) {
			return getDecoratedImage(searchResultRow, getStandardImage( ISharedImages.IMG_OBJ_ELEMENT ) );
		}
		return null;
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

   public void connectIndexToName(Integer index, String name) {
		indexToNameMap.put(index, name);
	}
	
	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ISearchResultRow) {
			ISearchResultRow searchResultRow = (ISearchResultRow)element;
			String name = indexToNameMap.get(Integer.valueOf(columnIndex) );
			if ( name != null ) {
				if ( name.equals("This") && searchResultRow.isHasObjectValue() ) {
					IObjectStoreItem objectStoreItem = searchResultRow.getObjectValue();
					return super.getText(objectStoreItem);
				} else {
					Object value = searchResultRow.getValue(name);
					return 	getValueString(value);
				}
			} 
		}
		return "";
	}

	private String getValueString(Object value) {
		
		if ( value != null ) {
			if ( value instanceof List ) {
				StringBuffer valueString = new StringBuffer();
				String concat = "";
				for ( Object listValue : (List<?>) value ) {
					valueString.append(concat);
					valueString.append( listValue.toString() );
					concat = ",";
				}
				return valueString.toString();
			} else {
				return value.toString();
			}
		}
		return "";
	}

	private Image getDecoratedImage(Object obj, Image image) {
		Image decorated = decorator.decorateImage(image, obj);
		if (decorated != null)
			return decorated;
		return image;
	}

	private Image getStandardImage(String imageKey) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}
