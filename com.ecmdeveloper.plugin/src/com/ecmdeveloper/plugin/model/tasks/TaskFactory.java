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

package com.ecmdeveloper.plugin.model.tasks;

import java.util.Collection;
import java.util.Map;

import com.ecmdeveloper.plugin.core.model.ClassesPlaceholder;
import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IGetParentTask;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.core.model.tasks.ICancelCheckoutTask;
import com.ecmdeveloper.plugin.core.model.tasks.ICheckinTask;
import com.ecmdeveloper.plugin.core.model.tasks.ICheckoutTask;
import com.ecmdeveloper.plugin.core.model.tasks.ICreateCustomObjectTask;
import com.ecmdeveloper.plugin.core.model.tasks.ICreateDocumentTask;
import com.ecmdeveloper.plugin.core.model.tasks.ICreateFolderTask;
import com.ecmdeveloper.plugin.core.model.tasks.IDeleteTask;
import com.ecmdeveloper.plugin.core.model.tasks.IExecuteSearchTask;
import com.ecmdeveloper.plugin.core.model.tasks.IFetchObjectTask;
import com.ecmdeveloper.plugin.core.model.tasks.IFetchPropertiesTask;
import com.ecmdeveloper.plugin.core.model.tasks.IGetContentAsFileTask;
import com.ecmdeveloper.plugin.core.model.tasks.IGetContentInfoTask;
import com.ecmdeveloper.plugin.core.model.tasks.IGetContentTask;
import com.ecmdeveloper.plugin.core.model.tasks.IGetCurrentVersionTask;
import com.ecmdeveloper.plugin.core.model.tasks.IGetDocumentVersionsTask;
import com.ecmdeveloper.plugin.core.model.tasks.IGetReleasedVersionTask;
import com.ecmdeveloper.plugin.core.model.tasks.IMoveTask;
import com.ecmdeveloper.plugin.core.model.tasks.IRefreshTask;
import com.ecmdeveloper.plugin.core.model.tasks.ISaveTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.IUpdateDocumentContentTask;
import com.ecmdeveloper.plugin.core.model.tasks.IUpdateTask;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetChildClassDescriptionsTask;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetClassDescriptionTask;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetRequiredClassDescriptionTask;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IRefreshClassDescriptionTask;
import com.ecmdeveloper.plugin.model.ClassDescription;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;

/**
 * @author ricardo.belfor
 *
 */
public class TaskFactory implements ITaskFactory {

	private static TaskFactory taskFactory;
	
	private TaskFactory() {}
	
	public static synchronized ITaskFactory getInstance() {
		if ( taskFactory == null ) {
			taskFactory = new TaskFactory();
		}
		return taskFactory;
	}
	
	@Override
	public IDeleteTask getDeleteTask(IObjectStoreItem[] objectStoreItems, boolean deleteAllVersions) {
		return new DeleteTask(objectStoreItems, deleteAllVersions );
	}

	@Override
	public IGetParentTask getGetParentTask(IObjectStoreItem objectStoreItem) {
		return new GetParentTask(objectStoreItem);
	}

	@Override
	public IFetchPropertiesTask getFetchPropertiesTask(IObjectStoreItem objectStoreItem,
			String[] propertyNames) {
		return new FetchPropertiesTask(objectStoreItem, propertyNames);
	}

	@Override
	public IGetDocumentVersionsTask getGetDocumentVersionsTask(IDocument document) {
		return new GetDocumentVersionsTask(document);
	}

	@Override
	public IMoveTask getMoveTask(IObjectStoreItem[] objectStoreItems, IObjectStoreItem destination) {
		return new MoveTask(objectStoreItems, destination);
	}

	@Override
	public IRefreshTask getRefreshTask(IObjectStoreItem[] objectStoreItems, boolean notifyListeners) {
		return new RefreshTask(objectStoreItems, notifyListeners);
	}

	@Override
	public IUpdateTask getUpdateTask(IObjectStoreItem objectStoreItem) {
		return new UpdateTask(objectStoreItem);
	}

	@Override
	public IUpdateTask getUpdateTask(IObjectStoreItem[] objectStoreItems) {
		return new UpdateTask(objectStoreItems);
	}

	@Override
	public IFetchObjectTask getFetchObjectTask(IObjectStore objectStore, String id,
			String className, String objectType) {
		return new FetchObjectTask(objectStore, id, className, objectType);
	}

	@Override
	public ICancelCheckoutTask getCancelCheckoutTask(IDocument document) {
		return new CancelCheckoutTask((Document) document);
	}

	@Override
	public ICheckinTask getCheckinTask(IDocument document, boolean majorVersion, boolean autoClassify) {
		return new CheckinTask((Document) document, majorVersion, autoClassify);
	}

	@Override
	public ICheckoutTask getCheckoutTask(IDocument document) {
		return new CheckoutTask((Document) document);
	}

	@Override
	public IGetContentAsFileTask getGetContentAsFileTask(IDocument document, String outputPath,
			int index) {
		return new GetContentAsFileTask((Document) document, outputPath, index);
	}

	@Override
	public IGetContentInfoTask getGetContentInfoTask(IDocument document) {
		return new GetContentInfoTask((Document) document);
	}

	@Override
	public IGetContentTask getGetContentTask(IDocument document, int contentIndex) {
		return new GetContentTask((Document) document, contentIndex);
	}

	@Override
	public IGetCurrentVersionTask getGetCurrentVersionTask(IDocument document) {
		return new GetCurrentVersionTask((Document) document);
	}

	@Override
	public IGetReleasedVersionTask getGetReleasedVersionTask(IDocument document) {
		return new GetReleasedVersionTask((Document) document);
	}

	@Override
	public ISaveTask getSaveTask(IDocument document, Collection<Object> content, String mimeType) {
		return new SaveTask((Document) document, content, mimeType);
	}

	@Override
	public ICreateCustomObjectTask getCreateCustomObjectTask(IObjectStoreItem parent,
			String className, Map<String, Object> propertiesMap) {
		return new CreateCustomObjectTask((ObjectStoreItem) parent, className, propertiesMap);
	}

	@Override
	public ICreateDocumentTask getCreateDocumentTask(IObjectStoreItem parent, String className,
			Map<String, Object> propertiesMap) {
		return new CreateDocumentTask((ObjectStoreItem) parent, className, propertiesMap);
	}

	@Override
	public ICreateFolderTask getCreateFolderTask(IObjectStoreItem parent, String className,
			Map<String, Object> propertiesMap) {
		return new CreateFolderTask((ObjectStoreItem) parent, className, propertiesMap);
	}

	@Override
	public IGetChildClassDescriptionsTask getGetChildClassDescriptionsTask(
			IClassDescription parent, Object placeholder) {
		return new GetChildClassDescriptionsTask((ClassDescription)parent, (ClassesPlaceholder) placeholder);
	}

	@Override
	public IGetClassDescriptionTask getGetClassDescriptionTask(String className,
			IObjectStore objectStore) {
		return new GetClassDescriptionTask(className, objectStore);
	}

	@Override
	public IGetClassDescriptionTask getGetClassDescriptionTask(String className,
			IObjectStore objectStore, Object parent) {
		return new GetClassDescriptionTask(className, parent, objectStore);
	}

	@Override
	public IRefreshClassDescriptionTask getRefreshClassDescriptionTask(
			IClassDescription[] classDescriptions) {
		return new RefreshClassDescriptionTask((ClassDescription[]) classDescriptions);
	}

	@Override
	public IRefreshTask getRefreshTask(IObjectStoreItem objectStoreItem) {
		return new RefreshTask( objectStoreItem );
	}

	@Override
	public IExecuteSearchTask getExecuteSearchTask(String query, IObjectStore objectStore, Integer maxHits) {
		return new ExecuteSearchTask(query, objectStore);
	}

	@Override
	public IGetRequiredClassDescriptionTask getGetRequiredClassDescription(
			IPropertyDescription targetPropertyDescription, IObjectStore objectStore) {
		return new GetRequiredClassDescription(targetPropertyDescription, objectStore);
	}

	@Override
	public IUpdateDocumentContentTask getUpdateDocumentContentTask(IDocument document,
			Collection<Object> content, String mimeType) {
		throw new UnsupportedOperationException();
	}
}
