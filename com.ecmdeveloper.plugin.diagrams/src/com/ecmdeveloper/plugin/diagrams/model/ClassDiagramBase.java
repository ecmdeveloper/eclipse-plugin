/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * The Class ClassDiagramBase.
 * 
 * @author Ricardo.Belfor
 */
public abstract class ClassDiagramBase implements IPropertySource {

	/** The Constant EMPTY_ARRAY. */
	private static final IPropertyDescriptor[] EMPTY_ARRAY = new IPropertyDescriptor[0];
	
	/** The pcs delegate. */
	private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);

	/**
	 * Attach a non-null PropertyChangeListener to this object.
	 * 
	 * @param listener a non-null PropertyChangeListener instance
	 * 
	 * @throws IllegalArgumentException if the parameter is null
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		pcsDelegate.addPropertyChangeListener(listener);
	}
	
	/**
	 * Remove a PropertyChangeListener from this component.
	 * 
	 * @param listener a PropertyChangeListener instance
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listener != null) {
			pcsDelegate.removePropertyChangeListener(listener);
		}
	}
	
	/**
	 * Report a property change to registered listeners (for example edit parts).
	 * 
	 * @param property the programmatic name of the property that changed
	 * @param oldValue the old value of this property
	 * @param newValue the new value of this property
	 */
	protected void firePropertyChange(String property, Object oldValue, Object newValue) {
		if (pcsDelegate.hasListeners(property)) {
			pcsDelegate.firePropertyChange(property, oldValue, newValue);
		}
	}

	/**
	 * Gets the editable value. Returns a value for this property source that
	 * can be edited in a property sheet.
	 * 
	 * @return the editable value
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	@Override
	public Object getEditableValue() {
		return this;
	}

	/**
	 * Gets the property descriptors. Children should override this. The default
	 * implementation returns an empty array.
	 * 
	 * @return the property descriptors
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return EMPTY_ARRAY;	
	}
}
