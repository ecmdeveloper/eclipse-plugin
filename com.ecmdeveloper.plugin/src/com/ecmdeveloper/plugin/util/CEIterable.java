package com.ecmdeveloper.plugin.util;

import java.util.Iterator;

import com.filenet.api.collection.EngineCollection;

public class CEIterable<T> implements Iterable<T> {
	
	private final EngineCollection collection;

	public CEIterable( final EngineCollection collection ) {
		this.collection = collection;
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		return collection.iterator();
	}
}
