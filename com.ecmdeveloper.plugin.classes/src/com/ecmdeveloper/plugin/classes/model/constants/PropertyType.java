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
package com.ecmdeveloper.plugin.classes.model.constants;

import com.filenet.api.constants.TypeID;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public enum PropertyType {

	BINARY { 		
		@Override
		public String toString() {
			return BINARY_NAME;
		}
	},
	BOOLEAN{ 		
		@Override
		public String toString() {
			return BOOLEAN_NAME;
		}
	},
	DATE{ 		
		@Override
		public String toString() {
			return DATE_NAME;
		}
	},
	DOUBLE{ 		
		@Override
		public String toString() {
			return DOUBLE_NAME;
		}
	},
	GUID{ 		
		@Override
		public String toString() {
			return GUID_NAME;
		}
	},
	LONG{ 		
		@Override
		public String toString() {
			return LONG_NAME;
		}
	},
	OBJECT{ 		
		@Override
		public String toString() {
			return OBJECT_NAME;
		}
	},
	STRING { 		
		@Override
		public String toString() {
			return STRING_NAME;
		}
	},
	UNKNOWN { 		
		@Override
		public String toString() {
			return UNKNOWN_NAME;
		}
	};
	
	private static final String UNKNOWN_NAME = "Unknown";
	private static final String STRING_NAME = "String";
	private static final String OBJECT_NAME = "Object";
	private static final String LONG_NAME = "Long";
	private static final String GUID_NAME = "Id";
	private static final String DOUBLE_NAME = "Double";
	private static final String DATE_NAME = "Date";
	private static final String BOOLEAN_NAME = "Boolean";
	private static final String BINARY_NAME = "Binary";

	public static PropertyType fromTypeID(TypeID typeId )
	{
		switch ( typeId.getValue() ) {
			case TypeID.BINARY_AS_INT: return BINARY;
			case TypeID.BOOLEAN_AS_INT: return BOOLEAN;
			case TypeID.DATE_AS_INT: return DATE;
			case TypeID.DOUBLE_AS_INT: return DOUBLE;
			case TypeID.GUID_AS_INT: return GUID;
			case TypeID.LONG_AS_INT: return LONG;
			case TypeID.OBJECT_AS_INT: return OBJECT;
			case TypeID.STRING_AS_INT: return STRING;
			default: return UNKNOWN;
		}
	}
}
