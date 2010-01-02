/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.editors.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

import com.ecmdeveloper.plugin.classes.model.constants.PropertyType;
import com.ecmdeveloper.plugin.properties.editors.details.DateDetailsPage;
import com.ecmdeveloper.plugin.properties.editors.details.IntegerDetailsPage;
import com.ecmdeveloper.plugin.properties.editors.details.StringDetailsPage;
import com.ecmdeveloper.plugin.properties.editors.details.UnknownDetailsPage;

/**
 * @author Ricardo.Belfor
 *
 */
public class PropertiesInputDetailsPageProvider implements IDetailsPageProvider {

	private Map<PropertyType, IDetailsPage> propertyTypeDetailsPageMap = new HashMap<PropertyType, IDetailsPage>();
	
	@Override
	public Object getPageKey(Object object) {
		Property property = (Property) object;
		return property.getPropertyDescription().getPropertyType();
	}
	
	@Override
	public IDetailsPage getPage(Object key) {

		PropertyType propertyType = (PropertyType) key;
		
		if ( propertyTypeDetailsPageMap.containsKey(propertyType) ) {
			return propertyTypeDetailsPageMap.get(propertyType);
		} 

		if ( propertyType.equals( PropertyType.STRING ) ) {
			return getStringDetailsPage();
		}
		if ( propertyType.equals( PropertyType.LONG ) ) {
			return getIntegerDetailsPage();
		}
		else if ( propertyType.equals( PropertyType.DATE ) ) {
			return getDateDetailsPage();
		}
		
		return getUnknownDetailsPage();
	}
	
	private IDetailsPage getIntegerDetailsPage() {
		IDetailsPage detailsPage = new IntegerDetailsPage();
		propertyTypeDetailsPageMap.put( PropertyType.LONG, detailsPage );
		return detailsPage;
	}

	private IDetailsPage getDateDetailsPage() {
		IDetailsPage detailsPage = new DateDetailsPage();
		propertyTypeDetailsPageMap.put( PropertyType.STRING, detailsPage );
		return detailsPage;
	}

	private IDetailsPage getUnknownDetailsPage() {
		IDetailsPage detailsPage = new UnknownDetailsPage();
		propertyTypeDetailsPageMap.put( PropertyType.STRING, detailsPage );
		return detailsPage;
	}
	
	private IDetailsPage getStringDetailsPage() {
		IDetailsPage detailsPage = new StringDetailsPage();
		propertyTypeDetailsPageMap.put( PropertyType.STRING, detailsPage );
		return detailsPage;
	}
}