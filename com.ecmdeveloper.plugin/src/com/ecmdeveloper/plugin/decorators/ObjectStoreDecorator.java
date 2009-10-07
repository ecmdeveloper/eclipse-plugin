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
 * 
 * @author Ricardo Belfor
 *
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

	/* (non-Javadoc)
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