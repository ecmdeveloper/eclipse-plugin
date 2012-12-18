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

package com.ecmdeveloper.plugin.folderview.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author ricardo.belfor
 *
 */
public class Category {

	final private String name;
	final private Object value;
	private final int level;
	
	private Collection<Object> children = new ArrayList<Object>();
	
	public Category(String name, Object value, int level) {
		this.name = name;
		this.value = value;
		this.level = level;
	}

	public Category(String name, Object value, int level, Collection<?> children) {
		this.name = name;
		this.value = value;
		this.level = level;
		this.children.addAll(children);
	}
	
	public String getName() {
		return name;
	}

	public Collection<Object> getChildren() {
		return children;
	}

	public void setChildren(Collection<?> children) {
		this.children.clear();
		this.children.addAll(children);
	}

	public void add(Object categoryOrItem) {
		children.add(categoryOrItem);
	}

	public Object getValue() {
		return value;
	}

	public String getValueString() {
		return value != null? value.toString() : "(null)";
	}
	
	public int getLevel() {
		return level;
	}
}
