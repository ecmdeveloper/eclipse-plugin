package com.ecmdeveloper.plugin.model.tasks;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.ecmdeveloper.plugin.model.Action;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.filenet.api.collection.ActionSet;
import com.filenet.api.collection.VersionableSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;

public class GetCodeModuleActionsTask extends BaseTask {

	protected String codeModuleId;
	protected ObjectStore objectStore;

	public GetCodeModuleActionsTask(String codeModuleId, ObjectStore objectStore ) {
		super();
		this.codeModuleId = codeModuleId;
		this.objectStore = objectStore;
	}

	@Override
	public Object call() throws Exception {
		
		com.filenet.api.core.ObjectStore objectStoreObject = (com.filenet.api.core.ObjectStore) objectStore.getObjectStoreObject();
		VersionSeries versionSeries = Factory.VersionSeries.getInstance(objectStoreObject, new Id( codeModuleId ) ); 

		PropertyFilter propertyFilter = new PropertyFilter();
		propertyFilter.addIncludeProperty( new FilterElement(null, null, null, PropertyNames.VERSIONS, null ) );
		propertyFilter.addIncludeProperty( new FilterElement(null, null, null, PropertyNames.REFERENCING_ACTIONS, null ) );
		propertyFilter.addIncludeProperty( new FilterElement(1, null, null, PropertyNames.MAJOR_VERSION_NUMBER, null ) );
		propertyFilter.addIncludeProperty( new FilterElement(1, null, null, PropertyNames.MINOR_VERSION_NUMBER, null ) );

		versionSeries.fetchProperties(propertyFilter);
		
		VersionableSet versions = versionSeries.get_Versions();
		Iterator<?> versionsIterator = versions.iterator();
		Set<Action> actions = new HashSet<Action>();
		
		while ( versionsIterator.hasNext() ) {
			
			Document document = (Document) versionsIterator.next();
			ActionSet actionSet = ((com.filenet.api.admin.CodeModule)document).get_ReferencingActions();
			Iterator<?> iterator = actionSet.iterator();
	
			while (iterator.hasNext()) {
				Action action = new Action( iterator.next(), null, objectStore );
				String codeModuleVersion = document.get_MajorVersionNumber()
						+ "." + document.get_MinorVersionNumber();
				action.setCodeModuleVersion(codeModuleVersion);
				actions.add( action );
			}
		}
		
		return actions;
	}
}
