package com.ecmdeveloper.plugin.favorites.tester;

import org.eclipse.core.expressions.PropertyTester;

import com.ecmdeveloper.plugin.favorites.model.FavoriteObjectStore;

public class FavoriteObjectStoreTester extends PropertyTester {

	private static final String IS_CONNECTED_PROPERTY = "isConnected";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if ( ! ( receiver instanceof FavoriteObjectStore ) )
		{
			return false;
		}

		if ( IS_CONNECTED_PROPERTY.equals(property) ) {
			FavoriteObjectStore favoriteObjectStore = (FavoriteObjectStore) receiver;
			return favoriteObjectStore.getObjectStore().isConnected();
		}
		
		return false;
	}


}
