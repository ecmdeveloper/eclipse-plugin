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

package com.ecmdeveloper.plugin.search.model.constants;

/**
 * @author ricardo.belfor
 *
 */
public enum FullTextQueryType {

	CONTAINS{		
		@Override
		public String toString() {
			return super.toString() + ", search for precise or fuzzy matches.";
		}
	},
	FREETEXT{		
		@Override
		public String toString() {
			return super.toString() + ", search the text of document content for values that match the meaning.";
		}
	};
}
