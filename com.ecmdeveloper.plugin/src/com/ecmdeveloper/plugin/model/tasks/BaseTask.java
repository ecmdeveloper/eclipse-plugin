package com.ecmdeveloper.plugin.model.tasks;

import com.ecmdeveloper.plugin.model.ObjectStoresManager;


public abstract class BaseTask {

	protected ObjectStoresManager objectStoresManager;

	public BaseTask(ObjectStoresManager objectStoresManager) {
		this.objectStoresManager = objectStoresManager;
	}	
}
