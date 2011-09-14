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

package com.ecmdeveloper.plugin.core.model.tasks;

import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IGetParentTask;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;

/**
 * @author ricardo.belfor
 *
 */
public interface ITaskFactory {

	IDeleteTask getDeleteTask( IObjectStoreItem[] objectStoreItems, boolean deleteAllVersions);

	IGetParentTask getGetParentTask(IObjectStoreItem objectStoreItem);
	
	IGetDocumentVersionsTask getGetDocumentVersionsTask(IDocument document);
	
	IFetchPropertiesTask getFetchPropertiesTask(IObjectStoreItem objectStoreItem, String[] propertyNames);
	
	IRefreshTask getRefreshTask( IObjectStoreItem[] objectStoreItems, boolean notifyListeners);
	
	IUpdateTask getUpdateTask( IObjectStoreItem objectStoreItem );

	IUpdateTask getUpdateTask( IObjectStoreItem[] objectStoreItems );
	
	IMoveTask getMoveTask(IObjectStoreItem[] objectStoreItems, IObjectStoreItem destination );

	IFetchObjectTask getFetchObjectTask(IObjectStore objectStore, String id, String className, String objectType );

	IGetContentTask getGetContentTask(IDocument document, int contentIndex);

	ISaveTask getSaveTask(IDocument document, Collection<Object> content, String mimeType);

	IGetContentInfoTask getGetContentInfoTask(IDocument document);

	IGetReleasedVersionTask getGetReleasedVersionTask(IDocument document);

	IGetCurrentVersionTask getGetCurrentVersionTask(IDocument document);

	IGetContentAsFileTask getGetContentAsFileTask(IDocument document, String outputPath, int index);

	ICancelCheckoutTask getCancelCheckoutTask(IDocument document);

	ICheckoutTask getCheckoutTask(IDocument document);

	ICheckinTask getCheckinTask(IDocument document, boolean majorVersion, boolean autoClassify);
}