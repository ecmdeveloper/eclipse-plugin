/**
 * 
 */
package com.ecmdeveloper.plugin.model.tasks;

import java.util.ArrayList;
import java.util.Iterator;

import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ReferentialContainmentRelationship;

/**
 * @author Ricardo Belfor
 *
 */
public class LoadChildrenTask extends BaseTask {

	final protected ObjectStoreItem objectStoreItem;
	
	public LoadChildrenTask( ObjectStoreItem objectStoreItem ) {
		this.objectStoreItem = objectStoreItem;
	}

	@Override
	public String call() throws Exception {

		ArrayList<IObjectStoreItem> children = new ArrayList<IObjectStoreItem>();
		
		com.filenet.api.core.Folder folder;

		if ( objectStoreItem instanceof ObjectStore ) {
			com.filenet.api.core.ObjectStore objectStore = (com.filenet.api.core.ObjectStore) objectStoreItem.getObjectStoreObject();
			objectStore.fetchProperties( new String[] { PropertyNames.ROOT_FOLDER } );
			folder = objectStore.get_RootFolder();
			folder.fetchProperties( new String[] { PropertyNames.CONTAINEES, PropertyNames.SUB_FOLDERS } );
		} else if ( objectStoreItem instanceof Folder ) {
			folder = (com.filenet.api.core.Folder) objectStoreItem.getObjectStoreObject();
		} else {
			return null;
		}
		
		Iterator<?> iterator = folder.get_SubFolders().iterator();
		ObjectStore objectStore = objectStoreItem.getObjectStore();
		while (iterator.hasNext()) {
			children.add( new Folder( iterator.next(), objectStoreItem, objectStore ) );
		}
		
		iterator = folder.get_Containees().iterator();
		
		while (iterator.hasNext() ) {
			
			ReferentialContainmentRelationship relation = (ReferentialContainmentRelationship) iterator.next();
			relation.fetchProperties( new String[]{ PropertyNames.HEAD } );

			IndependentObject object = relation.get_Head();

			if ( object instanceof com.filenet.api.core.Document )
			{
				children.add( new Document( object, objectStoreItem, objectStore ) );
			}
			else if ( object instanceof com.filenet.api.core.Folder )
			{
				// TODO: mark this folder as a link instead of a regular child?
				children.add( new Folder( object, objectStoreItem, objectStore ) );
			}
			else if ( object instanceof com.filenet.api.core.CustomObject )
			{
				children.add( new CustomObject( object, objectStoreItem, objectStore ) );
			}
		}
		
		objectStoreItem.setChildren(children);
		fireObjectStoreItemsChanged(null, null, new ObjectStoreItem[] { objectStoreItem} );
		
		return null;
	}

}
