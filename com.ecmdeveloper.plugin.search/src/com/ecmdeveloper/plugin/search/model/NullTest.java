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

import org.eclipse.swt.graphics.Image;

/**
 * @author ricardo.belfor
 *
 */
public class NullTest extends QueryComponent {

	private static final long serialVersionUID = 1L;
	private static Image NULL_TEST_ICON = createImage(Comparison.class, "icons/ledicon16.gif"); //$NON-NLS-1$

	private boolean negated;
	
	public NullTest(Query query) {
		super(query);
	}
	
	@Override
	public Image getIconImage() {
		return NULL_TEST_ICON;
	}

	public void setNegated(boolean negated) {
		this.negated = negated;
	}

	public boolean isNegated() {
		return negated;
	}

	public String toString() {
		if ( getField() != null ) {
			StringBuffer result = new StringBuffer();
			result.append(getField());
			result.append( " IS ");
			if ( negated ) {
				result.append("NOT ");
			}
			result.append( "NULL");
			return result.toString();
		} else {
			return "";
		}
	}
}
