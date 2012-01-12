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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.bindings.CmisBindingFactory;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

import com.ecmdeveloper.plugin.cmis.Activator;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStores;

/**
 * @author ricardo.belfor
 *
 */
public class Connection implements IConnection {

	private String url;
	private String username;
	private String password;
	private List<Repository> repositories;
	private Map<String, String> parameters;
	private String name;
	private String displayName	;
	private Authentication authentication;
	private BindingType bindingType;
	private boolean useCompression;
	private boolean useClientCompression;
	private boolean useCookies;
	
	@Override
	public void connect() {

		SessionFactory sessionFactory = Activator.getDefault().getSessionFactory();
		
		parameters = new HashMap<String, String>();
		parameters.put(SessionParameter.USER, username );
		parameters.put(SessionParameter.PASSWORD, password );
		parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		
		setUrlParameters();
        setAuthenticationParameters();
		
        if ( useCompression ) {
            parameters.put(SessionParameter.COMPRESSION, "true");
        }

        if ( useClientCompression ) {
            parameters.put(SessionParameter.CLIENT_COMPRESSION, "true");
        }

        if ( useCookies ) {
            parameters.put(SessionParameter.COOKIES, "true");
        }
        
		repositories = sessionFactory.getRepositories(parameters);
	}

	private void setAuthenticationParameters() {
		switch (authentication) {
        case STANDARD:
            parameters.put(SessionParameter.USER, username);
            parameters.put(SessionParameter.PASSWORD, password);
        	break;
        case NTLM:
            parameters.put(SessionParameter.USER, username);
            parameters.put(SessionParameter.PASSWORD, password);
            parameters.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS, CmisBindingFactory.NTLM_AUTHENTICATION_PROVIDER);
        	break;
        }
	}

	private void setUrlParameters() {

		switch (bindingType) {
		case ATOMPUB:
			parameters.put(SessionParameter.ATOMPUB_URL, url );
			break;
		case WEBSERVICES:
            parameters.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_ACL_SERVICE, url);
            parameters.put(SessionParameter.WEBSERVICES_POLICY_SERVICE, url);
			break;
		default:
			throw new IllegalArgumentException( "Invalid binding type " + bindingType );
		}
	}

	@Override
	public void disconnect() {
		parameters = null;
		repositories = null;
	}

	@Override
	public boolean isConnected() {
		return repositories != null;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Map<String, String> getParameters() {
		Map<String,String> parametersCopy = new HashMap<String, String>();
		parametersCopy.putAll(parameters);
		return parametersCopy;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public IObjectStore[] getObjectStores(IObjectStores parent) {
		ArrayList<IObjectStore> objectStoreList = new ArrayList<IObjectStore>();
		for ( Repository repository : repositories ) {
			IObjectStore objectStore = new ObjectStore(repository.getId(), this );
			objectStoreList.add(objectStore);
		}
		return objectStoreList.toArray( new IObjectStore[0] );
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	public BindingType getBindingType() {
		return bindingType;
	}

	public void setBindingType(BindingType bindingType) {
		this.bindingType = bindingType;
	}

	public boolean isUseCompression() {
		return useCompression;
	}

	public void setUseCompression(boolean useCompression) {
		this.useCompression = useCompression;
	}

	public boolean isUseClientCompression() {
		return useClientCompression;
	}

	public void setUseClientCompression(boolean useClientCompression) {
		this.useClientCompression = useClientCompression;
	}

	public boolean isUseCookies() {
		return useCookies;
	}

	public void setUseCookies(boolean useCookies) {
		this.useCookies = useCookies;
	}
}
