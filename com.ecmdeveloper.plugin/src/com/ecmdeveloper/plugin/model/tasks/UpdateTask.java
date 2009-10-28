/**
 * 
 */
package com.ecmdeveloper.plugin.model.tasks;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;

/**
 * @author Ricardo.Belfor
 *
 */
public class UpdateTask extends BaseTask {

	protected IObjectStoreItem[] objectStoreItems;
	
	public UpdateTask( IObjectStoreItem objectStoreItem ) {
		this.objectStoreItems = new IObjectStoreItem[] { objectStoreItem }; 
	}
	
	public UpdateTask( IObjectStoreItem[] objectStoreItems ) {
		this.objectStoreItems = objectStoreItems;
	}

	@Override
	public String call() throws Exception {

		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			((ObjectStoreItem)objectStoreItem).save();
		}

		fireObjectStoreItemsChanged(null, null, objectStoreItems );
		return null;
	}
}
