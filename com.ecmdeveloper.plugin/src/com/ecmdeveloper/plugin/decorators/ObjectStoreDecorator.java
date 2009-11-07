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
package com.ecmdeveloper.plugin.decorators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerListener;

/**
 * This class decorates the Object Store object. The decoration is changed based
 * on the fact if the Object Store is connected or not connected.
 * 
 * @author Ricardo Belfor
 */
public class ObjectStoreDecorator implements ILightweightLabelDecorator, ObjectStoresManagerListener {

	private static final String NOT_CONNECTED_DECORATOR_IMAGE = "icons/bullet_red.png"; //$NON-NLS-1$
	private static final String CONNECTED_DECORATOR_IMAGE = "icons/bullet_green.png"; //$NON-NLS-1$
	private static int quadrant = IDecoration.BOTTOM_RIGHT;

	private final ObjectStoresManager manager = ObjectStoresManager.getManager();
	private final List<ILabelProviderListener> listenerList = new ArrayList<ILabelProviderListener>();

	public ObjectStoreDecorator() {
		super();
		manager.addObjectStoresManagerListener(this);
	}

	/**
	 * Decorates the element.
	 * 
	 * @param element the element
	 * @param decoration the decoration
	 * 
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) {

		ObjectStore objectStore = (ObjectStore) element;

		ImageDescriptor descriptor = null;
		if ( objectStore.isConnected() ) {
			descriptor = Activator.getImageDescriptor( CONNECTED_DECORATOR_IMAGE );
		} else {
			descriptor = Activator.getImageDescriptor( NOT_CONNECTED_DECORATOR_IMAGE );
		}
		decoration.addOverlay(descriptor,quadrant);
	}

	/**
	 * Adds the listener.
	 * 
	 * @param listener the listener
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		if (!listenerList.contains(listener)) {
			listenerList.add(listener);
		}
	}

	/**
	 * Dispose.
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		manager.removeObjectStoresManagerListener(this);
	}

	/**
	 * Checks if this is a label property.
	 * 
	 * @param element the element
	 * @param property the property
	 * 
	 * @return true, if checks if is label property
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * Removes the listener.
	 * 
	 * @param listener the listener
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		listenerList.remove(listener);
	}

	/**
	 * Notification that the Object Store items is changed. This will change the 
	 * items registered as listeners accordingly.
	 * 
	 * @param event the event
	 * 
	 * @see com.ecmdeveloper.plugin.model.ObjectStoresManagerListener#objectStoreItemsChanged(com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent)
	 */
	@Override
	public void objectStoreItemsChanged(ObjectStoresManagerEvent event) {
		
		if ( event.getItemsUpdated() == null ) {
			return;
		}
		
		for ( IObjectStoreItem objectStoreItem : event.getItemsUpdated() ) {
			if ( objectStoreItem instanceof ObjectStore ) {
				
			      LabelProviderChangedEvent labelEvent =
			            new LabelProviderChangedEvent(this, objectStoreItem );

			      Iterator<ILabelProviderListener> iter = listenerList.iterator();
			      while (iter.hasNext())
			         iter.next().labelProviderChanged(labelEvent);
			}
		}
	}
}