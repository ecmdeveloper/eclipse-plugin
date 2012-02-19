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
import com.ecmdeveloper.plugin.core.model.AbstractConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStores;

/**
 * @author ricardo.belfor
 *
 */
public class Connection extends AbstractConnection {

	private List<Repository> repositories;
	private Map<String, String> parameters;
	private Authentication authentication;
	private BindingType bindingType;
	private boolean useCompression;
	private boolean useClientCompression;
	private boolean useCookies;
	
	@Override
	public void connect() {

		SessionFactory sessionFactory = Activator.getDefault().getSessionFactory();
		
		parameters = new HashMap<String, String>();
		parameters.put(SessionParameter.USER, getUsername() );
		parameters.put(SessionParameter.PASSWORD, getPassword() );
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
            parameters.put(SessionParameter.USER, getUsername() );
            parameters.put(SessionParameter.PASSWORD, getPassword() );
        	break;
        case NTLM:
            parameters.put(SessionParameter.USER, getUsername() );
            parameters.put(SessionParameter.PASSWORD, getPassword() );
            parameters.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS, CmisBindingFactory.NTLM_AUTHENTICATION_PROVIDER);
        	break;
        }
	}

	private void setUrlParameters() {

		switch (bindingType) {
		case ATOMPUB:
			parameters.put(SessionParameter.ATOMPUB_URL, getUrl() );
			break;
		case WEBSERVICES:
            parameters.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, getUrl() );
            parameters.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, getUrl() );
            parameters.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE, getUrl() );
            parameters.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE, getUrl() );
            parameters.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, getUrl() );
            parameters.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, getUrl() );
            parameters.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, getUrl() );
            parameters.put(SessionParameter.WEBSERVICES_ACL_SERVICE, getUrl() );
            parameters.put(SessionParameter.WEBSERVICES_POLICY_SERVICE, getUrl() );
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

	public Map<String, String> getParameters() {
		Map<String,String> parametersCopy = new HashMap<String, String>();
		parametersCopy.putAll(parameters);
		return parametersCopy;
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
