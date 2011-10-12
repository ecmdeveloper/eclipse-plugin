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

import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.core.model.constants.PropertyType;
import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class PropertiesInputDetailsPageProvider implements IDetailsPageProvider {

	private static Map<String,Class<? extends IDetailsPage>> keyToClassMap = new HashMap<String, Class<? extends IDetailsPage>>();
	
	static
	{
		// Flags: selectable, multivalue, readOnly 
		keyToClassMap.put( getPageKey(PropertyType.STRING, false, false, false ), StringDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.STRING, true, false, false ), SingleChoiceDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.STRING, false, true, false ), MultiValueStringDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.STRING, true, true, false ), MultiChoiceDetailsPage.class );

		keyToClassMap.put( getPageKey(PropertyType.STRING, false, false, true ), SingleValueReadOnlyDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.STRING, true, false, true ), SingleValueReadOnlyDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.STRING, false, true, true ), MultiValueReadOnlyDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.STRING, true, true, true ), MultiValueReadOnlyDetailsPage.class );
		
		keyToClassMap.put( getPageKey(PropertyType.LONG, false, false, false ), IntegerDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.LONG, true, false, false ), SingleChoiceDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.LONG, false, true, false ), MultiValueIntegerDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.LONG, true, true, false ), MultiChoiceDetailsPage.class );

		keyToClassMap.put( getPageKey(PropertyType.LONG, false, false, true ), SingleValueReadOnlyDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.LONG, true, false, true ), SingleValueReadOnlyDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.LONG, false, true, true ), MultiValueReadOnlyDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.LONG, true, true, true ), MultiValueReadOnlyDetailsPage.class );
		
		keyToClassMap.put( getPageKey(PropertyType.DATE, false, false, false ), DateDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.DATE, false, true, false ), MultiValueDateDetailsPage.class );

		keyToClassMap.put( getPageKey(PropertyType.DATE, false, false, true ), SingleValueReadOnlyDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.DATE, false, true, true ), MultiValueReadOnlyDetailsPage.class );
		
		keyToClassMap.put( getPageKey(PropertyType.BOOLEAN, false, false, false ), BooleanDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.BOOLEAN, false, true, false ), MultiValueBooleanDetailsPage.class );

		keyToClassMap.put( getPageKey(PropertyType.BOOLEAN, false, false, true ), SingleValueReadOnlyDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.BOOLEAN, false, true, true ), MultiValueReadOnlyDetailsPage.class );

		keyToClassMap.put( getPageKey(PropertyType.DOUBLE, false, false, false ), DoubleDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.DOUBLE, false, true, false ), MultiValueDoubleDetailsPage.class );

		keyToClassMap.put( getPageKey(PropertyType.DOUBLE, false, false, true ), SingleValueReadOnlyDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.DOUBLE, false, true, true ), MultiValueReadOnlyDetailsPage.class );

		keyToClassMap.put( getPageKey(PropertyType.GUID, false, false, true ), SingleValueReadOnlyDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.GUID, false, true, true ), MultiValueReadOnlyDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.GUID, false, false, false ), StringDetailsPage.class );
		keyToClassMap.put( getPageKey(PropertyType.GUID, false, true, false ), MultiValueStringDetailsPage.class );
	}
	
	private IDetailsPage unknownDetailsPage;

	@Override
	public Object getPageKey(Object object) {
		Property property = (Property) object;
		IPropertyDescription propertyDescription = property.getPropertyDescription();
		return PropertiesInputDetailsPageProvider.getPageKey(propertyDescription.getPropertyType(),
				propertyDescription.hasChoices(), property.isMultivalue(), !property.isSettableOnEdit() );
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
		if ( unknownDetailsPage == null ) {
			unknownDetailsPage = new UnknownDetailsPage();
		}
		return unknownDetailsPage;
	}
	
	private static String getPageKey( PropertyType propertyType, boolean selectable, boolean multivalue, boolean readOnly ) {

		StringBuffer pageKey = new StringBuffer();

		if ( multivalue ) {
			pageKey.append( "Multivalue" );
		}
		
		pageKey.append( propertyType.toString() );

		if ( selectable ) {
			pageKey.append("WithChoices" );
		}
		
		if ( readOnly ) {
			pageKey.append( "ReadOnly" );
		}
		return pageKey.toString();
	}
}