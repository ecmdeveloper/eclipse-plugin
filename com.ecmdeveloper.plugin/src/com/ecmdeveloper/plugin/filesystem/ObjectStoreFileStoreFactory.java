/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.filesystem;

import java.text.MessageFormat;

import org.eclipse.core.filesystem.IFileStore;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;

/**
 * @author Ricardo.Belfor
 *
 */
public class ObjectStoreFileStoreFactory {

	private static final String UNSUPPORTED_CLASS_MESSAGE = "Cannot create FileStore for class {0}";

	public static IFileStore getFileStore(IObjectStoreItem objectStoreItem) {
		if ( objectStoreItem instanceof Document ) {
			return new ObjectStoreDocumentItem( (Document) objectStoreItem );
		} else if ( objectStoreItem instanceof Folder ) {
			return new ObjectStoreFolderItem( (Folder) objectStoreItem );
		} else {
			throw new UnsupportedOperationException(MessageFormat.format(
					UNSUPPORTED_CLASS_MESSAGE, objectStoreItem.getClass()
							.getName()));
		}
	}
}
