/**
 * 
 */
package com.ecmdeveloper.plugin.model;

import java.util.Collection;

import com.filenet.api.constants.PropertyNames;

/**
 * @author Ricardo.Belfor
 *
 */
public class Action extends ObjectStoreItem {

	com.filenet.api.events.EventAction eventAction;
	
	public Action(Object eventAction, IObjectStoreItem parent, ObjectStore objectStore ) {
		super(parent, objectStore);

		this.eventAction = (com.filenet.api.events.EventAction) eventAction;
		this.eventAction.fetchProperties( new String[] { PropertyNames.NAME, PropertyNames.ID } );
		this.name = this.eventAction.get_Name();
		this.id = this.eventAction.get_Id().toString();
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		return null;
	}
}
