/**
 * Copyright 2009,2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.properties.editors.details;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.classes.model.constants.PropertyType;
import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class PropertiesInputDetailsPageProvider implements IDetailsPageProvider {

	private static Map<String,Class<? extends IDetailsPage>> keyToClassMap = new HashMap<String, Class<? extends IDetailsPage>>();
	
	static
	{
		keyToClassMap.put( getPageKey(PropertyType.STRING, false, false ), StringDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.STRING, true, false ), SingleChoiceDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.STRING, false, true ), MultiValueStringDetailsPage.class );

		keyToClassMap.put( getPageKey(PropertyType.LONG, false, false ), IntegerDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.LONG, true, false ), SingleChoiceDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.LONG, false, true ), MultiValueIntegerDetailsPage.class );
		
		keyToClassMap.put( getPageKey(PropertyType.DATE, false, false ), DateDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.DATE, false, true ), MultiValueDateDetailsPage.class );
		
		keyToClassMap.put( getPageKey(PropertyType.BOOLEAN, false, false ), BooleanDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.BOOLEAN, false, true ), MultiValueBooleanDetailsPage.class );

		keyToClassMap.put( getPageKey(PropertyType.DOUBLE, false, false ), DoubleDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.DOUBLE, false, true ), MultiValueDoubleDetailsPage.class );
	}
	
	private IDetailsPage unknownDetailsPage;

	@Override
	public Object getPageKey(Object object) {
		Property property = (Property) object;
		PropertyDescription propertyDescription = property.getPropertyDescription();
		return PropertiesInputDetailsPageProvider.getPageKey(propertyDescription.getPropertyType(),
				propertyDescription.hasChoices(), property.isMultivalue() );
	}
	
	@Override
	public IDetailsPage getPage(Object key) {

		if ( keyToClassMap.containsKey( key ) ) {
			return createDetailsPage(key);
		}
		
		return getUnknownDetailsPage();
	}

	private IDetailsPage createDetailsPage(Object key) {
		Class<?> detailsClass = keyToClassMap.get( key);
		try {
			IDetailsPage detailsPage = (IDetailsPage) detailsClass.newInstance();
			return detailsPage;
		} catch (Exception e) {
			return getUnknownDetailsPage();
		}
	}
	
	private IDetailsPage getUnknownDetailsPage() {
		if ( unknownDetailsPage != null ) {
			unknownDetailsPage = new UnknownDetailsPage();
		}
		return unknownDetailsPage;
	}
	
	private static String getPageKey( PropertyType propertyType, boolean selectable, boolean multivalue ) {

		StringBuffer pageKey = new StringBuffer();

		if ( multivalue ) {
			pageKey.append( "Multivalue" );
		}
		
		pageKey.append( propertyType.toString() );

		if ( selectable ) {
			pageKey.append("WithChoices" );
		}
		
		return pageKey.toString();
	}
}