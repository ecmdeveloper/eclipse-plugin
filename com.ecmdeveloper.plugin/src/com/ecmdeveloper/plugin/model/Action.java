/**
 * 
 */
package com.ecmdeveloper.plugin.model;

import java.util.Collection;

import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * @author Ricardo.Belfor
 *
 */
public class Action extends ObjectStoreItem {

	com.filenet.api.events.EventAction eventAction;
	protected String codeModuleVersion;
	
	public Action(Object eventAction, IObjectStoreItem parent, ObjectStore objectStore ) {
		super(parent, objectStore);

		this.eventAction = (com.filenet.api.events.EventAction) eventAction;
		refresh();
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		return null;
	}

	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return eventAction;
	}

	@Override
	public void setName(String name) {
		eventAction.set_DisplayName(name);
	}

	@Override
	public void refresh() {
		this.eventAction.fetchProperties( new String[] { PropertyNames.NAME, PropertyNames.ID } );
		this.name = this.eventAction.get_Name();
		this.id = this.eventAction.get_Id().toString();
	}

	public void setCodeModule(CodeModule codeModule) {
		eventAction.set_CodeModule((com.filenet.api.admin.CodeModule) codeModule
						.getObjectStoreObject());
	}
	
	public void setCodeModuleVersion(String codeModuleVersion ) {
		this.codeModuleVersion = codeModuleVersion;
	}
	
	public String getCodeModuleVersion() {
		return codeModuleVersion;
	}
}
