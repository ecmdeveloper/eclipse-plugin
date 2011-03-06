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

import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author ricardo.belfor
 * 
 */
public class Comparison extends QueryComponent {

	static final long serialVersionUID = 1;

	private ComparisonOperation comparisonOperation;
	private Object value;

	private static Image COMPARISON_ICON = createImage(Comparison.class, "icons/label16.gif"); //$NON-NLS-1$

	public Comparison(Query query) {
		super(query);
	}

	public Image getIconImage() {
		return COMPARISON_ICON;
	}

	public String toString() {
		if (getField() != null && comparisonOperation != null) {
			return getField() + " " + comparisonOperation.getOperation() + " "
					+ QueryFieldValueFormatter.format(getField(), value);
		} else {
			return "";
		}
	}

	public ComparisonOperation getComparisonOperation() {
		return comparisonOperation;
	}

	public void setComparisonOperation(ComparisonOperation comparisonOperation) {
		ComparisonOperation oldField = this.comparisonOperation;
		this.comparisonOperation = comparisonOperation;
		firePropertyChange(PROPERTY_CHANGED, oldField, comparisonOperation);
	}

	public void setValue(Object value) {
		Object oldField = this.value;
		this.value = value;
		firePropertyChange(PROPERTY_CHANGED, oldField, value);
	}

	public Object getValue() {
		return value;
	}
}
