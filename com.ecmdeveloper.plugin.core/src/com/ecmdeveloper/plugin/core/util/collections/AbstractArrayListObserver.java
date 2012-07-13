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

import java.util.Collection;

/**
 * @author ricardo.belfor
 *
 */
public abstract class AbstractArrayListObserver<E> implements ArrayListObserver<E> {
	 
    @Override
    public void onAdd( E element ) {
    }
 
    @Override
    public void onAdd( int index, E element ) {
    }
 
    @Override
    public void onAddAll( Collection<? extends E> elements ) {
    }
 
    @Override
    public void onAddAll( int index, Collection<? extends E> elements ) {
    }
 
    @Override
    public void onClear() {
    }
 
    @Override
    public void onRemove( int index ) {
    }
 
    @Override
    public void onRemove( Object obj ) {
    }
 
    @Override
    public void onRemoveAll( Collection<?> c ) {
    }
 
    @Override
    public void onRetainAll( Collection<?> c ) {
    }
 
    @Override
    public void onSet( int index, E element ) {
    }
 
    @Override
    public void onSubList( int fromIndex, int toIndex ) {
    }
}
