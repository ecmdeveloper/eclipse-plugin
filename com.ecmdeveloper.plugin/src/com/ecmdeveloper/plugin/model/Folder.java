/**
 * 
 */
package com.ecmdeveloper.plugin.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ReferentialContainmentRelationship;

/**
 * @author Ricardo Belfor
 *
 */
public class Folder extends ObjectStoreItem {

	private ArrayList<IObjectStoreItem> children;
	protected com.filenet.api.core.Folder folder;
	protected Boolean hasChildren;
	
	public Folder(Object folder, IObjectStoreItem parent ) {
		super(parent);
		
		this.folder = (com.filenet.api.core.Folder) folder;
		this.folder.fetchProperties( new String[] { PropertyNames.FOLDER_NAME } );
		this.name = this.folder.get_FolderName();
		System.out.println( "Loaded '" + this.name + "'" );
	}
	
	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getChildren()
	 */
	@Override
	public Collection<IObjectStoreItem> getChildren() 
	{
		if ( children == null )
		{
			initializeChildren();
		}
		
		return children;
	}

	@SuppressWarnings("unchecked")
	private void initializeChildren()
	{
		children = new ArrayList<IObjectStoreItem>();

		if ( ! hasChildren() ) {
			return;
		}

		Iterator iterator = folder.get_SubFolders().iterator();

		while (iterator.hasNext()) {
			children.add( new Folder( iterator.next(), this ) );
		}
		
		iterator = folder.get_Containees().iterator();
		
		while (iterator.hasNext() ) {
			
			ReferentialContainmentRelationship relation = (ReferentialContainmentRelationship) iterator.next();
			relation.fetchProperties( new String[]{ PropertyNames.HEAD } );

			IndependentObject object = relation.get_Head();

			if ( object instanceof com.filenet.api.core.Document )
			{
				children.add( new Document( object, this ) );
			}
			else if ( object instanceof com.filenet.api.core.Folder )
			{
				// TODO: mark this folder as a link instead of a regular child?
				children.add( new Folder( object, this ) );
			}
			else if ( object instanceof com.filenet.api.core.CustomObject )
			{
				children.add( new CustomObject( object, this ) );
			}
		}
		
	}

	@Override
	public boolean hasChildren() 
	{
		if ( hasChildren == null )
		{
			folder.fetchProperties( new String[] { PropertyNames.CONTAINEES, PropertyNames.SUB_FOLDERS } );
			if ( ! folder.get_SubFolders().isEmpty() ) {
				hasChildren = true;
				return hasChildren;
			}
			
			if ( ! folder.get_Containees().isEmpty() ) {
				hasChildren = true;
				return hasChildren;
			}

			hasChildren = false;
			return hasChildren;
		}
		
		return hasChildren;
	}
}
