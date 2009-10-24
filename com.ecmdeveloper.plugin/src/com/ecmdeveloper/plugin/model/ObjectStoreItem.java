package com.ecmdeveloper.plugin.model;

import java.beans.DesignMode;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.Platform;

import com.filenet.api.collection.ReferentialContainmentRelationshipSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Containable;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;


public abstract class ObjectStoreItem implements IObjectStoreItem {

	protected IObjectStoreItem parent;
	protected String name;
	protected String id;
	protected ObjectStore objectStore;

	public ObjectStoreItem(IObjectStoreItem parent, ObjectStore objectStore ) {
		this.parent = parent;
		this.objectStore = objectStore;
	}
	
	public abstract IndependentlyPersistableObject getObjectStoreObject();
	
	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getParent()
	 */
	@Override
	public IObjectStoreItem getParent() {
		return parent;
	}
	
	@Override
	public void setParent(IObjectStoreItem parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	@Override
	public boolean hasChildren() 
	{
		return false;
	}

	@Override
	public ObjectStore getObjectStore() {
		return objectStore;
	}
	
	@Override
	public void delete() {
		getObjectStoreObject().delete();
	}

	public void save() {
		getObjectStoreObject().save(RefreshMode.REFRESH);
	}
	
	@Override
	public void move( IObjectStoreItem destination ) {

		if ( !(this instanceof Folder) ) { // TODO: add check for contained folder
			moveContainedObject(destination);
		} else {
			moveChildObject( destination );
		}
	}

	@Override
	public void setChildren(Collection<IObjectStoreItem> children) {
		// Stub for childless objects 
	}	
	
	private void moveChildObject(IObjectStoreItem destination) {

		com.filenet.api.core.Folder folder = (com.filenet.api.core.Folder) getObjectStoreObject();

		if ( destination instanceof Folder ) {
			folder.set_Parent( (com.filenet.api.core.Folder) ((Folder) destination).getObjectStoreObject() );
		} else if (destination instanceof ObjectStore ) {
			Folder rootFolder = ((ObjectStore)destination).getRootFolder();
			folder.set_Parent( (com.filenet.api.core.Folder) rootFolder.getObjectStoreObject() );
		}
		folder.save( RefreshMode.REFRESH );
	}

	@SuppressWarnings("unchecked")
	private void moveContainedObject(IObjectStoreItem destination) {

		IndependentlyPersistableObject object = getObjectStoreObject();

		if ( ! ( object instanceof Containable) ) {
			throw new UnsupportedOperationException( "An object of this type cannot be moved" );
		}

		object.fetchProperties(new String[] { PropertyNames.CONTAINERS } );
		PropertyFilter propertyFilter = new PropertyFilter();
		propertyFilter.addIncludeProperty( new FilterElement(null, null, null, PropertyNames.TAIL, null ) );
		propertyFilter.addIncludeProperty( new FilterElement(1, null, null, PropertyNames.ID, null ) );

		ReferentialContainmentRelationshipSet relations = ((Containable) object).get_Containers();

		Iterator<ReferentialContainmentRelationship> iterator = relations.iterator();
		
		while ( iterator.hasNext() ) {
			
			ReferentialContainmentRelationship relationship = iterator.next();
			relationship.fetchProperties( propertyFilter );
			com.filenet.api.core.Folder container = (com.filenet.api.core.Folder) relationship.get_Tail();
			
			if ( container.get_Id().toString().equals( this.getParent().getId() ) ) {
				relationship.set_Tail( ((ObjectStoreItem)destination).getObjectStoreObject() );
				relationship.save(RefreshMode.REFRESH);
				return;
			}
		}
	}
}
