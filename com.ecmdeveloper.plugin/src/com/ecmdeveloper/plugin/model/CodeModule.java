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

import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Document;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.VersionSeries;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class CodeModule extends ObjectStoreItem{

	protected VersionSeries versionSeries;
	protected Document document;
	
	public CodeModule(Object versionSeries, IObjectStoreItem parent, ObjectStore objectStore ) {
		
		super(parent, objectStore);
		this.versionSeries = (VersionSeries) versionSeries;
		refresh();
	}

	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return document;
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		return null;
	}

	@Override
	public void refresh() {
		versionSeries.refresh( new String[] { PropertyNames.NAME, PropertyNames.ID, PropertyNames.CURRENT_VERSION } );
		name = ((Document) versionSeries.get_CurrentVersion()).get_Name();
		id = versionSeries.get_Id().toString();
		versionSeries.fetchProperties( new String[]  { PropertyNames.CURRENT_VERSION } );
		document = (Document) versionSeries.get_CurrentVersion();
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		
		if ( adapter.equals( Document.class ) ) {
			versionSeries.fetchProperties( new String[]  { PropertyNames.CURRENT_VERSION } );
			return versionSeries.get_CurrentVersion();
		}
		return super.getAdapter(adapter);
	}

	@Override
	public String getClassName() {
		if ( document != null ) {
			return document.getClassName();
		}
		return null;
	}
}
