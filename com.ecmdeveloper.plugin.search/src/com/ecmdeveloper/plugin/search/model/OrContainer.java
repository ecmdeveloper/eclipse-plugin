/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.search.model;

/**
 * @author ricardo.belfor
 *
 */
public class OrContainer extends QueryContainer {

	private static final String OR_LABEL = "or";
	private static final long serialVersionUID = 1L;

	public OrContainer(Query query) {
		super(query);
	}
	
	@Override
	public String toString() {
		return OR_LABEL;
	}

	@Override
	protected String getConcatOperation() {
		return OR_LABEL;
	}

	@Override
	protected String getOperationPrefix() {
		return null;
	}
}
