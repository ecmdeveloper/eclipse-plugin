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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IGetParentTask;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetChildClassDescriptionsTask;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetClassDescriptionTask;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetRequiredClassDescriptionTask;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IRefreshClassDescriptionTask;
import com.ecmdeveloper.plugin.core.model.tasks.codemodule.ICreateCodeModuleTask;
import com.ecmdeveloper.plugin.core.model.tasks.codemodule.ICreateEventActionTask;
import com.ecmdeveloper.plugin.core.model.tasks.codemodule.IGetCodeModuleActionsTask;
import com.ecmdeveloper.plugin.core.model.tasks.codemodule.IGetCodeModulesTask;
import com.ecmdeveloper.plugin.core.model.tasks.codemodule.IUpdateCodeModuleTask;

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
	
	IRefreshTask getRefreshTask(IObjectStoreItem objectStoreItem);

	IUpdateTask getUpdateTask( IObjectStoreItem objectStoreItem );

	IUpdateTask getUpdateTask( IObjectStoreItem[] objectStoreItems );
	
	IMoveTask getMoveTask(IObjectStoreItem[] objectStoreItems, IObjectStoreItem destination );

	IFetchObjectTask getFetchObjectTask(IObjectStore objectStore, String id, String className, String objectType );

	IGetContentTask getGetContentTask(IDocument document, int contentIndex);

	IUpdateDocumentContentTask getUpdateDocumentContentTask(IDocument document, Collection<Object> content, String mimeType );
	
	ISaveTask getSaveTask(IDocument document, Collection<Object> content, String mimeType);

	IGetContentInfoTask getGetContentInfoTask(IDocument document);

	IGetReleasedVersionTask getGetReleasedVersionTask(IDocument document);

	IGetCurrentVersionTask getGetCurrentVersionTask(IDocument document);

	IGetContentAsFileTask getGetContentAsFileTask(IDocument document, String outputPath, int index);

	ICancelCheckoutTask getCancelCheckoutTask(IDocument document);

	ICheckoutTask getCheckoutTask(IDocument document);

	ICheckinTask getCheckinTask(IDocument document, boolean majorVersion, boolean autoClassify);
	
	// FIXME placeholder?
	IGetChildClassDescriptionsTask getGetChildClassDescriptionsTask(IClassDescription parent, Object placeholder);

	IGetClassDescriptionTask getGetClassDescriptionTask(String className, IObjectStore objectStore);

	IRefreshClassDescriptionTask getRefreshClassDescriptionTask(IClassDescription[] classDescriptions);

	IGetClassDescriptionTask getGetClassDescriptionTask(String className, IObjectStore objectStore, Object parent);

	ICreateFolderTask getCreateFolderTask(IObjectStoreItem parent, String className, Map<String,Object> propertiesMap);
	
	ICreateDocumentTask getCreateDocumentTask(IObjectStoreItem parent, String className, Map<String,Object> propertiesMap);

	ICreateCustomObjectTask getCreateCustomObjectTask(IObjectStoreItem parent, String className, Map<String, Object> propertiesMap);
	
	IExecuteSearchTask getExecuteSearchTask(String query, IObjectStore objectStore, Integer maxHits );

	IGetRequiredClassDescriptionTask getGetRequiredClassDescription(IPropertyDescription targetPropertyDescription, IObjectStore objectStore);

	IUpdateCodeModuleTask getUpdateCodeModuleTask(String id, String name, ArrayList<File> contentElementFiles, IObjectStore objectStore);

	IGetCodeModulesTask getGetCodeModulesTask(IObjectStore objectStore);

	ICreateCodeModuleTask getCreateCodeModuleTask(String name, ArrayList<File> contentElementFiles, IObjectStore objectStore);

	IGetCodeModuleActionsTask getGetCodeModuleActionsTask(String id, IObjectStore objectStore);

	ICreateEventActionTask getCreateEventActionTask(String id, String name, String className, boolean enabled, IObjectStore objectStore);
}