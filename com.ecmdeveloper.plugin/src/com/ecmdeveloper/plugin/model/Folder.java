/**
 * 
 */
package com.ecmdeveloper.plugin.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.ecmdeveloper.plugin.model.tasks.LoadChildrenTask;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.ReferentialContainmentRelationship;

/**
 * @author Ricardo Belfor
 *
 */
public class Folder extends ObjectStoreItem {

	private Collection<IObjectStoreItem> children;
	protected com.filenet.api.core.Folder folder;
	protected Boolean hasChildren;
	
	public Folder(Object folder, IObjectStoreItem parent, ObjectStore objectStore ) {
		super(parent, objectStore );
		
		this.folder = (com.filenet.api.core.Folder) folder;
		refresh();
		System.out.println( "Loaded '" + this.name + "'" );
	}
	
	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return folder;
	}

	@Override
	public void refresh() {
		
		folder.fetchProperties( new String[] { PropertyNames.FOLDER_NAME, PropertyNames.ID, PropertyNames.CONTAINEES, PropertyNames.SUB_FOLDERS } );
		name = this.folder.get_FolderName();
		id = this.folder.get_Id().toString();
		children = null;
	
		hasChildren = ! folder.get_SubFolders().isEmpty() || ! folder.get_Containees().isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getChildren()
	 */
	@Override
	public Collection<IObjectStoreItem> getChildren() 
	{
		if ( children == null )
		{
			children = new ArrayList<IObjectStoreItem>();
			children.add( new Placeholder() );

			LoadChildrenTask loadChildrenTask = new LoadChildrenTask( this );
			ObjectStoresManager.getManager().executeTaskASync(loadChildrenTask);
		}
		
		return children;
	}
	
	public void removeChild(IObjectStoreItem childItem ) {
		if ( children.contains( childItem ) ) {
			children.remove(childItem);
			hasChildren = ! children.isEmpty();
		}
	}

	@Override
	public boolean hasChildren() 
	{
		if ( hasChildren == null ) {
			return false;
		} else {
			return hasChildren;
		}
	}

	@Override
	public void setChildren(Collection<IObjectStoreItem> children) {
		this.children = children;
	}	
	
	@Override
	public void setName(String name) {
		this.name = name;
		folder.set_FolderName( name );
	}
}
