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

import com.ecmdeveloper.plugin.search.model.constants.QueryContainerType;

/**
 * @author ricardo.belfor
 *
 */
public class AndContainer extends QueryContainer {

	private static final String AND_LABEL = "AND";
	private static final long serialVersionUID = 1L;

	public AndContainer(Query query) {
		super(query);
	}

	@Override
	public String toString() {
		return AND_LABEL;
	}

	@Override
	protected String getConcatOperation() {
		return AND_LABEL;
	}

	@Override
	protected String getOperationPrefix() {
		return null;
	}

	@Override
	public QueryContainerType getType() {
		return QueryContainerType.AND_CONTAINER;
	}
}
