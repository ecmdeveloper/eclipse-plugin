/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.favorites.tester;

import org.eclipse.core.expressions.PropertyTester;

import com.ecmdeveloper.plugin.favorites.model.FavoritesManager;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;

/**
 * @author ricardo.belfor
 *
 */
public class FavoritesTester extends PropertyTester {

	private static final String IS_FAVORITE_PROPERTY = "isFavorite";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if ( ! ( receiver instanceof ObjectStoreItem ) )
		{
			return false;
		}

		if ( IS_FAVORITE_PROPERTY.equals(property) ) {
			ObjectStoreItem objectStoreItem = (ObjectStoreItem) receiver;
			return FavoritesManager.getInstance().isFavorite(objectStoreItem);
		}
		
		return false;
	}

}
