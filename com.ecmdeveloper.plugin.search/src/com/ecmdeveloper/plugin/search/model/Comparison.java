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

import java.util.Date;

import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class Comparison extends QueryComponent {

	static final long serialVersionUID = 1;

	private IQueryField field;
	private ComparisonOperation comparisonOperation;
	private Object value;
	
	private static Image COMPARISON_ICON = createImage(Comparison.class, "icons/label16.gif"); //$NON-NLS-1$

	public Comparison() {
		super();
		value = new Date();
	}

	public Image getIconImage() {
		return COMPARISON_ICON;
	}

	public String toString() {
		if ( field != null && comparisonOperation != null) {
			return field + " " + comparisonOperation.getOperation() + " " + value;
		} else {
			return "";
		}
	}

	public void setField(IQueryField queryField) {
		this.field = queryField;
	}

	public IQueryField getField() {
		return field;
	}

	public ComparisonOperation getComparisonOperation() {
		return comparisonOperation;
	}

	public void setComparisonOperation(ComparisonOperation comparisonOperation) {
		this.comparisonOperation = comparisonOperation;
	}
}
