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
package com.ecmdeveloper.plugin.classes.model;

import java.util.Collection;


/**
 * 
 * @author Ricardo Belfor
 *
 */
public class Placeholder extends ClassDescription {

	private static final String LOADING_MESSAGE = "Loading class descriptions...";
	private String message;

	public Placeholder() {
		super(null, null, null);
	}

	@Override
	public Collection<Object> getChildren() {
		return null;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public String toString() {
		return message == null ? LOADING_MESSAGE : message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
