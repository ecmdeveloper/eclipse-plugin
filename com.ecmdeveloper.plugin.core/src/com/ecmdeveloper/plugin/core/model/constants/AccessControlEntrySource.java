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

package com.ecmdeveloper.plugin.core.model.constants;

/**
 * @author ricardo.belfor
 *
 */
public enum AccessControlEntrySource {
	
	DIRECT { 		
		@Override
		public String toString() {
			return DIRECT_STRING;
		}
	},
	DEFAULT { 		
		@Override
		public String toString() {
			return DEFAULT_STRING;
		}
	},
	INHERITED { 		
		@Override
		public String toString() {
			return INHERITED_STRING;
		}
	};

	private static final String INHERITED_STRING = "inherited entry";
	private static final String DIRECT_STRING = "direct entry";
	private static final String DEFAULT_STRING = "direct entry from the default instance security";
}
