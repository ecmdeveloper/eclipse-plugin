/**
 * 
 */
package com.ecmdeveloper.plugin.model.tasks;

import java.util.concurrent.Callable;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;

/**
 * @author Ricardo.Belfor
 *
 */
public class UpdateTask extends BaseTask implements Callable<String> {

	protected IObjectStoreItem[] objectStoreItems;
	protected boolean delete;
	
	public UpdateTask(ObjectStoresManager objectStoresManager,
			IObjectStoreItem[] objectStoreItems, boolean delete) {
		super(objectStoresManager);
		this.objectStoreItems = objectStoreItems;
		this.delete = delete;
	}

	@Override
	public String call() throws Exception {

		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			((ObjectStoreItem)objectStoreItem).save();
		}

		if ( delete ) {
			objectStoresManager.fireObjectStoreItemsChanged(null, objectStoreItems, null );
		} else {
			objectStoresManager.fireObjectStoreItemsChanged(null, null, objectStoreItems );
		}

		return null;
	}
}
