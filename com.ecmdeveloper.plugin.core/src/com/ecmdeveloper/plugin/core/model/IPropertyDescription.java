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

package com.ecmdeveloper.plugin.core.model;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

import com.ecmdeveloper.plugin.core.model.constants.PropertyType;

/**
 * @author ricardo.belfor
 *
 */
public interface IPropertyDescription {

	String getName();

	String getDisplayName();

	String getDescriptiveText();

	PropertyType getPropertyType();

	boolean isRequired();

	boolean isMultivalue();

	String getType();

	boolean isReadOnly();

	boolean isSettableOnCreate();

	boolean isSettableOnCheckIn();

	boolean isSettableOnEdit();

	Boolean getSystemOwned();

	boolean isOrderable();

	boolean isContainable();

	boolean isCBREnabled();

	boolean isHidden();

	boolean isSelectable();

	boolean isSearchable();

	boolean hasChoices();

	Collection<IChoice> getChoices();

	void addPropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);
	
	List getList();

	String getId();

	boolean isCascadeDelete();

	boolean isList();

	boolean isEnum();
}
