/**
 * Copyright 2009,2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.cmis.model;

import java.util.ArrayList;
import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.ClassesManager;
import com.ecmdeveloper.plugin.core.model.IChoice;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetChoiceValuesTask;
import com.ecmdeveloper.plugin.cmis.model.tasks.GetChoiceValuesTask;
import com.ecmdeveloper.plugin.cmis.util.PluginLog;

/**
 * @author Ricardo.Belfor
 *
 */
public class Choice implements IChoice {

	private org.apache.chemistry.opencmis.commons.definitions.Choice internalChoice;
	private Choice parent;
	private ArrayList<IChoice> choices;
	private final ObjectStore objectStore;
	
	public Choice(Object internalChoice, Choice parent, ObjectStore objectStore) {
		this.objectStore = objectStore;
		if ( internalChoice != null ) {
			this.internalChoice = (org.apache.chemistry.opencmis.commons.definitions.Choice) internalChoice;
			this.parent = parent;
		}
	}

	@Override
	public Collection<IChoice> getChildren() {
		if ( !internalChoice.getChoice().isEmpty() ) {
			
			if ( choices == null ) {
				IGetChoiceValuesTask task = new GetChoiceValuesTask(this, objectStore);
				try {
					choices = (ArrayList<IChoice>) ClassesManager.getManager().executeTaskSync(task);
				} catch (Exception e) {
					PluginLog.error(e);
				}
			}
			return choices;
		}
		return null;
	}
	
	@Override
	public boolean isSelectable() {
		return !internalChoice.getValue().isEmpty();
	}

	@Override
	public String getDisplayName() {
		return internalChoice.getDisplayName();
	}
	
	@Override
	public Object getValue() {
		return internalChoice.getValue();
	}


	@Override
	public Object getParent() {
		return parent;
	}

	public org.apache.chemistry.opencmis.commons.definitions.Choice getObjectStoreObject() {
		return internalChoice;
	}

	public ArrayList<IChoice> getChoices() {
		return choices;
	}
}
