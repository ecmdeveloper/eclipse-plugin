/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.search.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * 
 * @author ricardo.belfor
 *
 */
public abstract class QueryElement implements IPropertySource, Cloneable, Serializable {
	
	public static final String CHILDREN = "Children";

	transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	static final long serialVersionUID = 1;
	private final Query query;
	private QueryElement parent;
	private boolean rootDiagram = false;

	public boolean isRootDiagram() {
		return rootDiagram;
	}

	void setRootDiagram(boolean rootDiagram) {
		this.rootDiagram = rootDiagram;
	}
	
	public boolean isMainQuery() {
		return this.equals( query.getMainQuery() );
	}

	public boolean isMainQueryChild() {
		if ( isMainQuery() ) {
			return true;
		} else if (parent == null){
			return false;
		} else { 
			if ( !parent.isRootDiagram() ) {
				return parent.isMainQueryChild();
			}
			return false;
		}
	}
	
	public QueryElement(Query query) {
		this.query = query;
	}

	public Query getQuery() {
		return query;
	}
	
	public QueryElement getParent() {
		return parent;
	}

	protected void setParent(QueryElement parent) {
		this.parent = parent;
	}

	public void addPropertyChangeListener(PropertyChangeListener l){
		listeners.addPropertyChangeListener(l);
	}

	protected void firePropertyChange(String prop, Object old, Object newValue){
		listeners.firePropertyChange(prop, old, newValue);
	}

	protected void fireChildAdded(String prop, Object child, Object index) {
		listeners.firePropertyChange(prop, index, child);
	}

	protected void fireChildRemoved(String prop, Object child) {
		listeners.firePropertyChange(prop, child, null);
	}

	protected void fireStructureChange(String prop, Object child){
		listeners.firePropertyChange(prop, null, child);
	}

	public Object getEditableValue(){
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors(){
		return new IPropertyDescriptor[0];
	}

	public Object getPropertyValue(Object propName){
		return null;
	}

	final Object getPropertyValue(String propName){
		return null;
	}

	public boolean isPropertySet(Object propName){
		return isPropertySet((String)propName);
	}

	final boolean isPropertySet(String propName){
		return true;
	}

	private void readObject(ObjectInputStream in)throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		listeners = new PropertyChangeSupport(this);
	}

	public void removePropertyChangeListener(PropertyChangeListener l){
		listeners.removePropertyChangeListener(l);
	}

	public void resetPropertyValue(Object propName){
	}

	final void resetPropertyValue(String propName){}

	public void setPropertyValue(Object propName, Object val){}

	final void setPropertyValue(String propName, Object val){}

	public void update(){
	}
	
	public abstract String toSQL();

	public void refresh() {
		fireStructureChange(QuerySubpart.ID_ENABLEMENT, this);
	}
}
