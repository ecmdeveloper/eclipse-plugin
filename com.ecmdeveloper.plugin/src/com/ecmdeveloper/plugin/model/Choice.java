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

package com.ecmdeveloper.plugin.model;

import java.util.ArrayList;
import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.ClassesManager;
import com.ecmdeveloper.plugin.core.model.IChoice;
import com.ecmdeveloper.plugin.model.tasks.GetChoiceValuesTask;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.filenet.api.constants.ChoiceType;

/**
 * @author Ricardo.Belfor
 *
 */
public class Choice implements IChoice {

	private com.filenet.api.admin.Choice internalChoice;
	private Choice parent;
	private ArrayList<IChoice> choices;
	private final ObjectStore objectStore;
	
	public Choice(Object internalChoice, Choice parent, ObjectStore objectStore) {
		this.objectStore = objectStore;
		if ( internalChoice != null ) {
			this.internalChoice = (com.filenet.api.admin.Choice) internalChoice;
			this.parent = parent;
		}
	}

	@Override
	public Collection<IChoice> getChildren() {
		if ( isMidNode() ) {
			
			if ( choices == null ) {
				GetChoiceValuesTask task = new GetChoiceValuesTask(this, objectStore);
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
		return ! isMidNode();
	}

	@Override
	public String getDisplayName() {
		return internalChoice.get_DisplayName();
	}
	
	@Override
	public Object getValue() {
		if ( isSelectable() ) {
			if ( internalChoice.get_ChoiceType().equals( ChoiceType.STRING) ) {
				return internalChoice.get_ChoiceStringValue();
			} else if ( internalChoice.get_ChoiceType().equals( ChoiceType.INTEGER) ) {
				return internalChoice.get_ChoiceIntegerValue();
			}
		}
		return null;
	}

	private boolean isMidNode() {
		return internalChoice.get_ChoiceType().equals( ChoiceType.MIDNODE_INTEGER) || 
			internalChoice.get_ChoiceType().equals( ChoiceType.MIDNODE_STRING );
	}

	@Override
	public Object getParent() {
		return parent;
	}

	public Object getObjectStoreObject() {
		return internalChoice;
	}
}
