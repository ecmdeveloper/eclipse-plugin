/**
 * 
 */
package com.ecmdeveloper.plugin.model.tasks;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.util.PluginLog;

/**
 * @author Ricardo.Belfor
 *
 */
public class RefreshTask extends BaseTask {

	protected IObjectStoreItem[] objectStoreItems;

	public RefreshTask( IObjectStoreItem[] objectStoreItems ) {
		this.objectStoreItems = objectStoreItems;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Object call() throws Exception {

		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			try {
				objectStoreItem.refresh();
			} catch (Exception e) {
				PluginLog.error(e);
			}
		}

		fireObjectStoreItemsChanged(null, null, objectStoreItems );

		return null;
	}
}
