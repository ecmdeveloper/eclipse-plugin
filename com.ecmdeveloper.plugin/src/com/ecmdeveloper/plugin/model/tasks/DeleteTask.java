/**
 * 
 */
package com.ecmdeveloper.plugin.model.tasks;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.VersionSeries;

/**
 * This class is used to perform the delete task.
 * 
 * @author Ricardo Belfor
 *
 */
public class DeleteTask extends BaseTask {

	protected IObjectStoreItem[] objectStoreItems;
	protected boolean deleteAllVerions;
	
	/**
	 * The constructor of this task is used to pass all the relevant input
	 * to perform the task.
	 * 
	 * @param objectStoresManager
	 * @param objectStoreItems
	 * @param deleteAllVerions
	 */
	public DeleteTask( IObjectStoreItem[] objectStoreItems, boolean deleteAllVerions) {
		this.objectStoreItems = objectStoreItems;
		this.deleteAllVerions = deleteAllVerions;
	}

	/**
	 * Performs the delete task. If the input item is an document there is a
	 * choice if all versions or just this version of the document should be
	 * deleted. The parent of the input item is updated to reflect the changes
	 * in the model. Also the ObjectStoresManager class is used to notify all
	 * listeners.
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Object call() throws Exception {

		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			
			IndependentlyPersistableObject persistableObject = ((ObjectStoreItem) objectStoreItem).getObjectStoreObject();

			if ( deleteAllVerions && objectStoreItem instanceof Document ) {
				com.filenet.api.core.Document documentObject = (com.filenet.api.core.Document) persistableObject;
				documentObject.fetchProperties( new String[] { PropertyNames.VERSION_SERIES} );
				VersionSeries versionSeries = documentObject.get_VersionSeries();
				versionSeries.delete();
				versionSeries.save(RefreshMode.NO_REFRESH);
			} else {
				persistableObject.delete();
				persistableObject.save(RefreshMode.REFRESH);
			}
			
			IObjectStoreItem parent = objectStoreItem.getParent();
			if ( parent != null ) {
				if ( parent instanceof Folder ) {
					((Folder)parent).removeChild(objectStoreItem);
				} else if ( parent instanceof ObjectStore ) {
					((ObjectStore)parent).removeChild(objectStoreItem);
				}
			}
		}
		
		fireObjectStoreItemsChanged(null, objectStoreItems, null );
		
		return null;
	}
}
