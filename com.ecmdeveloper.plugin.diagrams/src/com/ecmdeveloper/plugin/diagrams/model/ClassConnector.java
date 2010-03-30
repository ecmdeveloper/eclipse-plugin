/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.model;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassConnector {

	private String classId;
	private String className;
	private String multiplicity;
	private String propertyId;
	private String propertyName;
	private boolean aggregate;

	public ClassConnector() {}
	
	public ClassConnector(String classId, String className, String propertyId, String propertyName,
			String multiplicity) {
		this.classId = classId;
		this.className = className;
		this.propertyId = propertyId;
		this.propertyName = propertyName;
		this.multiplicity = multiplicity;
	}

	public String getClassId() {
		return classId;
	}
	
	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}
	
	public String getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public void setAggregate(boolean aggregate) {
		this.aggregate = aggregate;
	}

	public boolean isAggregate() {
		return aggregate;
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append( className );
		if ( propertyName != null ) {
			stringBuffer.append(", " );
			stringBuffer.append( propertyName );
		}
		return stringBuffer.toString();
	}
	
	
}
