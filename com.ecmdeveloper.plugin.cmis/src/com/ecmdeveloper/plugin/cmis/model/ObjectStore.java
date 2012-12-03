/**
 * Copyright 2011, Ricardo Belfor
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

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.RepositoryCapabilities;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.CapabilityAcl;

import com.ecmdeveloper.plugin.cmis.Activator;
import com.ecmdeveloper.plugin.cmis.model.tasks.LoadChildrenTask;
import com.ecmdeveloper.plugin.core.model.IClassDescriptionFolder;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.ObjectStoreItemsModel;
import com.ecmdeveloper.plugin.core.model.Placeholder;
import com.ecmdeveloper.plugin.core.model.constants.ClassDescriptionFolderType;
import com.ecmdeveloper.plugin.core.model.constants.ClassType;
import com.ecmdeveloper.plugin.core.model.constants.Feature;
import com.ecmdeveloper.plugin.core.model.constants.PlaceholderType;

/**
 * @author ricardo.belfor
 *
 */
public class ObjectStore extends ObjectStoreItem implements IObjectStore {

	private static final String NOT_CONNECTED_MESSAGE = "Object Store \"{0}:{1}\" is not connected.\nConnect to the Object Store before trying this action";

	private Session session;
	private final Connection connection;
	private final String id;
	private String displayName;
	private Collection<IObjectStoreItem> children;
	private boolean readACL;
	private boolean editACL;
	
	protected ObjectStore(String id, Connection connection ) {
		super(null, null);
		
		this.id = id;
		this.name = id;
		this.displayName = id;
		this.connection = connection;
	}
	
	@Override
	public void connect() {
		if ( session == null ) {
			Map<String, String> parameters = connection.getParameters();
			parameters.put(SessionParameter.REPOSITORY_ID, id );
			SessionFactory sessionFactory = Activator.getDefault().getSessionFactory();
			session = sessionFactory.createSession(parameters);
			displayName = session.getRepositoryInfo().getName();

			CapabilityAcl aclCapability = getAclCapability();
			readACL = aclCapability != null && !aclCapability.equals( CapabilityAcl.NONE );
			editACL = aclCapability != null && aclCapability.equals(CapabilityAcl.MANAGE );
			 
			ObjectStoreItemsModel.getInstance().add(this);	
		}
	}

	private CapabilityAcl getAclCapability() {
		RepositoryCapabilities cap = session.getRepositoryInfo().getCapabilities();
		if ( cap != null ) {
			return cap.getAclCapability();
		}
		return null;
	}

	@Override
	public void disconnect() {
		session = null;
		children = null;		
	}

	public Session getSession() {
		return session;
	}

	@Override
	public IConnection getConnection() {
		return connection;
	}

	@Override
	public boolean isConnected() {
		return session != null;
	}

	@Override
	public String getClassName() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return id;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	@Override
	public ObjectStore getObjectStore() {
		return this;
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		
		if ( !isConnected() ) {
			return null;
		}
		
		if ( children == null )
		{
			children = new ArrayList<IObjectStoreItem>();
			children.add( new Placeholder( PlaceholderType.LOADING ) );
			
			LoadChildrenTask loadChildrenTask = new LoadChildrenTask( this );
			Activator.getDefault().getTaskManager().executeTaskASync(loadChildrenTask);
		}
		return children;
	}

	@Override
	public void setChildren(Collection<IObjectStoreItem> children) {
		this.children = children;
	}	

	public void removeChild(IObjectStoreItem childItem ) {
		if ( children.contains( childItem ) ) {
			children.remove(childItem);
		}
	}

	@Override
	public void addChild(IObjectStoreItem childItem) {

		if ( children != null ) {
			if ( ! children.contains( childItem ) ) {
				children.add(childItem);
			}
		}
	}
	
	@Override
	public IObjectStoreItem getParent() {
		return null;
	}

	@Override
	public Object getValue(String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() {
		return isConnected();
	}

	@Override
	public boolean isSimilarObject(IObjectStoreItem otherItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh() {
		children = null;
		
		if ( isConnected() ) {
//			objectStore.refresh( new String[] { PropertyNames.DISPLAY_NAME } );
//			displayName = objectStore.get_DisplayName();
		}
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setParent(IObjectStoreItem parent) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save() {
	}

	@Override
	public CmisObject getCmisObject() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<IClassDescriptionFolder> getClassDescriptionFolders() {

		ArrayList<IClassDescriptionFolder> virtualFolders = new ArrayList<IClassDescriptionFolder>();
		for (ClassDescriptionFolderType classDescriptionFolderType : ClassDescriptionFolderType.values() ) {
			if ( !classDescriptionFolderType.equals( ClassDescriptionFolderType.CUSTOM_OBJECT_CLASSES ) ) {
				virtualFolders.add( new ClassDescriptionFolder( classDescriptionFolderType, this )  );
			}
		}
		
		return virtualFolders;
	}

	@Override
	public IClassDescriptionFolder getClassDescriptionFolder( ClassDescriptionFolderType classDescriptionFolderType) {
		return new ClassDescriptionFolder( classDescriptionFolderType, this );
	}

	@Override
	public String getDefaultClassName(ClassType classType) {
		if ( classType.equals( ClassType.DOCUMENT_CLASSES ) ) {
			return "cmis:document";
		} else if ( classType.equals( ClassType.FOLDER_CLASSES ) ) {
			return "cmis:folder";
		}
		return null;
	}

	@Override
	public void assertConnected() {
		if (!isConnected()) {
			
			throw new RuntimeException(MessageFormat.format(NOT_CONNECTED_MESSAGE, getConnection()
					.toString(), getName()));
		}
	}
	
	public List<Entry<String, Object>> getRepositoryInfo() {

		List<Entry<String,Object>> repositoryInfoMap = new ArrayList< Entry<String,Object> >();
		
		repositoryInfoMap.add( getEntry( "General", getGeneralInfo() ) );
		repositoryInfoMap.add( getEntry( "Capabilities", getCapabilitiesInfo() ) );

		return repositoryInfoMap;
	}

	private List<Entry<String,Object>> getGeneralInfo() {
		
		List< Entry<String,Object> > generalInfoMap = new ArrayList< Entry<String,Object> >();

		RepositoryInfo repInfo = session.getRepositoryInfo();
		
		generalInfoMap.add( getEntry( "Name", repInfo.getName() ) );
		generalInfoMap.add( getEntry( "Id", repInfo.getId() ) );
		generalInfoMap.add( getEntry( "Description", repInfo.getDescription() ) );
		generalInfoMap.add( getEntry( "Vendor", repInfo.getVendorName() ) );
		generalInfoMap.add( getEntry( "Product", repInfo.getProductName() + " " + repInfo.getProductVersion() ) );
		generalInfoMap.add( getEntry( "CMIS Version", repInfo.getCmisVersionSupported() ) );
		
		if ( repInfo.getPrincipalIdAnonymous() != null ) {
			generalInfoMap.add( getEntry("Principal id Anonymous" , repInfo.getPrincipalIdAnonymous() ) );
		}

		if ( repInfo.getPrincipalIdAnyone() != null ) {
			generalInfoMap.add( getEntry("Principal id Anyone" , repInfo.getPrincipalIdAnyone() ) );
		}

		return generalInfoMap;
	}

	private Entry<String, Object> getEntry(String key, Object value) {
		Entry<String, Object> entry = new AbstractMap.SimpleEntry<String, Object>(key, value);
		return entry;
	}

	private List<Entry<String,Object>> getCapabilitiesInfo() {

		RepositoryCapabilities cap = session.getRepositoryInfo().getCapabilities();
		List< Entry<String,Object> > capabilitiesMap = new ArrayList< Entry<String,Object> >();
		
		if ( cap != null ) {
			capabilitiesMap.add( getEntry( "Get descendants supported", cap.isGetDescendantsSupported() ) );
			capabilitiesMap.add( getEntry( "Get folder tree supported", cap.isGetFolderTreeSupported() ) );
			capabilitiesMap.add( getEntry( "Unfiling supported", cap.isUnfilingSupported() ) );
			capabilitiesMap.add( getEntry( "Multifiling supported", cap.isMultifilingSupported() ) );
			capabilitiesMap.add( getEntry( "Version specific filing supported", cap.isVersionSpecificFilingSupported() ) );
			capabilitiesMap.add( getEntry( "Query", cap.getQueryCapability() ) );
			capabilitiesMap.add( getEntry( "Joins", cap.getJoinCapability() ) );
			capabilitiesMap.add( getEntry( "All versions searchable", cap.isAllVersionsSearchableSupported() ) );
			capabilitiesMap.add( getEntry( "PWC searchable", cap.isPwcSearchableSupported() ) );
			capabilitiesMap.add( getEntry( "PWC updatable", cap.isPwcUpdatableSupported() ) );
			capabilitiesMap.add( getEntry( "Content stream updates", cap.getContentStreamUpdatesCapability() ) );
			capabilitiesMap.add( getEntry( "Renditions", cap.getRenditionsCapability() ) );
			capabilitiesMap.add( getEntry( "Changes", cap.getChangesCapability() ) );
			capabilitiesMap.add( getEntry( "ACLs", cap.getAclCapability() ) );
		}
		return capabilitiesMap;
	}

	@Override
	public boolean isSupportedFeature(Feature feature) {

		if ( Feature.READ_ACL.equals(feature) ) {
			return readACL;
		}
		
		if (Feature.EDIT_ACL.equals(feature) ) {
			return editACL;
		}

		return super.isSupportedFeature(feature);
	}
	
	
}
