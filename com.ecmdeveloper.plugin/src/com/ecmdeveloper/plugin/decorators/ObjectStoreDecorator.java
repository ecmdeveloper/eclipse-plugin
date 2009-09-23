/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
 * An example showing how to control when an element is decorated. This example
 * decorates only elements that are instances of IResource and whose attribute
 * is 'Read-only'.
 * 
 * @see ILightweightLabelDecorator
 */
public class ObjectStoreDecorator implements ILightweightLabelDecorator, ObjectStoresManagerListener {

	/** The integer value representing the placement options */
	private static int quadrant = IDecoration.BOTTOM_RIGHT;

	/** The icon image location in the project folder */
	private String iconPath = "icons/read_only.gif"; //NON-NLS-1

	private final ObjectStoresManager manager = ObjectStoresManager.getManager();
	
	private final List<ILabelProviderListener> listenerList = new ArrayList<ILabelProviderListener>();
	
	/**
	 * The image description used in
	 * <code>addOverlay(ImageDescriptor, int)</code>
	 */
	private ImageDescriptor descriptor;

	public ObjectStoreDecorator() {
		super();
		manager.addObjectStoresManagerListener(this);
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) {
//		/**
//		 * Checks that the element is an IResource with the 'Read-only' attribute
//		 * and adds the decorator based on the specified image description and the
//		 * integer representation of the placement option.
//		 */
//		IResource resource = (IResource) element;
//		ResourceAttributes attrs = resource.getResourceAttributes();
//		if (attrs.isReadOnly()){
//			URL url = Platform.find(
//					Platform.getBundle("com.ecmdeveloper.plugin"), new Path(iconPath)); //NON-NLS-1
//
//			if (url == null)
//				return;
//			descriptor = ImageDescriptor.createFromURL(url);			
//			decoration.addOverlay(descriptor,quadrant);
//		}

		ObjectStore objectStore = (ObjectStore) element;

		if ( objectStore.isConnected() ) {
			descriptor = Activator.getImageDescriptor( "icons/bullet_green.png" );
		} else {
			descriptor = Activator.getImageDescriptor( "icons/bullet_red.png" );
		}
		decoration.addOverlay(descriptor,quadrant);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		if (!listenerList.contains(listener)) {
			listenerList.add(listener);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		manager.removeObjectStoresManagerListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		listenerList.remove(listener);
	}

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