/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.core.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author ricardo.belfor
 *
 */
public class ObservableArrayList<E> extends ArrayList<E> {

	private List<ArrayListObserver<E>> observers = null;
	private static final long serialVersionUID = 1L;

	public ObservableArrayList() {
	}
	
	public ObservableArrayList( ArrayListObserver<E> observer ) {
		registerObserver( observer );
	}

	@Override
	public boolean add( E e ) {
		boolean result = super.add( e );
		if ( result ) {
			for ( ArrayListObserver<E> o : getObservers() ) {
				o.onAdd( e );
			}
		}
		return result;
	}

	@Override
	public void add( int index, E element ) {
		super.add( index, element );
		for ( ArrayListObserver<E> o : getObservers() ) {
			o.onAdd( index, element );
		}
	}

	@Override
	public boolean addAll( Collection<? extends E> c ) {
		boolean result = super.addAll( c );
		if ( result ) {
			for ( ArrayListObserver<E> o : getObservers() ) {
				o.onAddAll( c );
			}
		}
		return result;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean result = super.addAll(index, c);
		for (ArrayListObserver<E> o : getObservers()) {
			o.onAddAll(index, c);
		}
		return result;

	}

	@Override
	public void clear() {
		super.clear();
		for ( ArrayListObserver<E> o : getObservers() ) {
			o.onClear();
		}
	}

	public List<ArrayListObserver<E>> getObservers() {
		if ( observers == null ) {
			observers = new ArrayList<ArrayListObserver<E>>();
		}
		return observers;
	}

	public void registerObserver( ArrayListObserver<E> observer ) {
		getObservers().add( observer );
	}

	@Override
	public E remove( int index ) {
		E toRet = super.remove( index );
		for ( ArrayListObserver<E> o : getObservers() ) {
			o.onRemove( index );
		}
		return toRet;
	}

	@Override
	public boolean remove( Object obj ) {
		boolean result = super.remove( obj );
		if ( result ) {
			for ( ArrayListObserver<E> o : getObservers() ) {
				o.onRemove( obj );
			}
		}
		return result;
	}

	@Override
	public boolean removeAll( Collection<?> c ) {
		boolean result = super.removeAll( c );
		for ( ArrayListObserver<E> o : getObservers() ) {
			o.onRemoveAll( c );
		}
		return result;
	}

	@Override
	public boolean retainAll( Collection<?> c ) {
		boolean result = super.retainAll( c );
		for ( ArrayListObserver<E> o : getObservers() ) {
			o.onRetainAll( c );
		}
		return result;
	}

	@Override
	public E set( int index, E element ) {
		E toRet = super.set( index, element );
		for ( ArrayListObserver<E> o : getObservers() ) {
			o.onSet( index, element );
		}
		return toRet;
	}

	@Override
	public List<E> subList( int fromIndex, int toIndex ) {
		List<E> toRet = super.subList( fromIndex, toIndex );
		for ( ArrayListObserver<E> o : getObservers() ) {
			o.onSubList( fromIndex, toIndex );
		}
		return toRet;
	}

	public void unregisterObserver( ArrayListObserver<E> observer ) {
		getObservers().remove( observer );
	}
}
