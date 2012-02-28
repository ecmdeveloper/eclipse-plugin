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
package com.ecmdeveloper.plugin.model;

import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.IAction;
import com.ecmdeveloper.plugin.core.model.ICodeModule;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * This class represents an Action object. Examples of this type of object are
 * Event Actions and Document Lifecyle Actions.
 * 
 * @author Ricardo.Belfor
 * 
 */
public class Action extends ObjectStoreItem implements IAction {

	com.filenet.api.events.Action action;
	protected String codeModuleVersion;
	
	public Action(Object eventAction, IObjectStoreItem parent, ObjectStore objectStore ) {
		super(parent, objectStore);

		this.action = (com.filenet.api.events.Action) eventAction;
		refresh();
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		return null;
	}

	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return action;
	}

	@Override
	public void setName(String name) {
		action.set_DisplayName(name);
	}

	@Override
	public void refresh() {
		this.action.refresh( new String[] { PropertyNames.NAME, PropertyNames.ID } );
		this.name = this.action.get_Name();
		this.id = this.action.get_Id().toString();
	}

	public void setCodeModule(ICodeModule codeModule) {
		action.set_CodeModule((com.filenet.api.admin.CodeModule) ((ObjectStoreItem) codeModule)
						.getObjectStoreObject());
	}
	
	public void setCodeModuleVersion(String codeModuleVersion ) {
		this.codeModuleVersion = codeModuleVersion;
	}
	
	public String getCodeModuleVersion() {
		return codeModuleVersion;
	}

	@Override
	public String getClassName() {
		if ( action != null ) {
			return action.getClassName();
		}
		return null;
	}
}
