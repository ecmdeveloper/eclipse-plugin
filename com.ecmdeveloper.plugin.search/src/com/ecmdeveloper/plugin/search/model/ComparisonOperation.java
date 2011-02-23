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
public enum ComparisonOperation {

	EQUAL("=", false) { 		
		@Override
		public String toString() {
			return "Equals";
		}
		
	},
	NOT_EQUAL("<>", false) { 		
		@Override
		public String toString() {
			return "Not Equals";
		}
	},
	LESS("<", true) { 		
		@Override
		public String toString() {
			return "Less";
		}
	},
	LESS_OR_EQUAL("<=", true) { 		
		@Override
		public String toString() {
			return "Less or Equal";
		}
	},
	GREATER(">", true) { 		
		@Override
		public String toString() {
			return "Greater";
		}
	},
	GREATER_OR_EQUAL(">=", true) { 		
		@Override
		public String toString() {
			return "Greater or Equal";
		}
	};

	private final String operation;
	private final boolean requiresOrderable;
	
	private ComparisonOperation(String operation, boolean requiresOrderable) {
		this.operation = operation;
		this.requiresOrderable = requiresOrderable;
	}
	
	public String getOperation() {
		return operation;
	}

	public boolean isRequiresOrderable() {
		return requiresOrderable;
	}
}
