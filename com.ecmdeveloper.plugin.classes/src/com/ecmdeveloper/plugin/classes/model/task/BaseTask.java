/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.classes.model.task;

import java.util.List;
import java.util.concurrent.Callable;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.ClassesManagerEvent;
import com.ecmdeveloper.plugin.classes.model.ClassesManagerListener;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public abstract class BaseTask implements Callable<Object>{

	protected List<ClassesManagerListener> listeners;

	public BaseTask() {
	}

	public void setListeners( List<ClassesManagerListener> listeners ) {
		this.listeners = listeners;
	}

	public void fireClassDescriptionChanged(ClassDescription[] itemsAdded,
			ClassDescription[] itemsRemoved, ClassDescription[] itemsUpdated) {
		
		if ( listeners == null || listeners.isEmpty() ) {
			return;
		}
		
		ClassesManagerEvent event = new ClassesManagerEvent(this,
				itemsAdded, itemsUpdated, itemsRemoved );
		for (ClassesManagerListener listener : listeners) {
			listener.classDescriptionsChanged(event);
		}
	}
}
