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
package com.ecmdeveloper.plugin.core.model;

import java.util.Collection;

import org.eclipse.core.runtime.IAdaptable;

import com.ecmdeveloper.plugin.core.model.constants.Feature;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public interface IObjectStoreItem extends IAdaptable {
	
	public String getName();
	
	public void setName(String name);

	public String getDisplayName();

	public String getId();

	public boolean isSupportedFeature(Feature feature);
	
	public IObjectStoreItem getParent();
	
	public Collection<IObjectStoreItem> getChildren();
	
	public void setChildren( Collection<IObjectStoreItem> children );
	
	public boolean hasChildren();
	
	public IObjectStore getObjectStore();
	
	public void setParent(IObjectStoreItem parent);
	
	public void refresh();

	public void removeChild(IObjectStoreItem objectStoreItem);

	public void addChild(IObjectStoreItem objectStoreItem);
	
	public String getClassName();
	
	public Object getValue(String propertyName);

	public void setValue(String propertyName, Object value ) throws Exception;
	
	public boolean isSimilarObject(IObjectStoreItem otherItem);
	
	public ITaskFactory getTaskFactory();
}
