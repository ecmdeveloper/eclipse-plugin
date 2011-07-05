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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

/**
 * @author ricardo.belfor
 *
 */
public class QueryFieldValueFormatter {

	public static String format(IQueryField queryField, Object value ) {
		
		if ( value == null ) {
			throw new IllegalArgumentException();
		}
		
		switch (queryField.getType()) {
		case BOOLEAN:
		case GUID:
		case LONG:
			return value.toString(); 
		case DATE:
			return formatDate(value);
		case DOUBLE:
			return formatDouble(value);
		case OBJECT:
			return MessageFormat.format("OBJECT(''{0}'')", value.toString());
		case STRING:
			return formatString(value);
		}
		return null;
	}

	private static String formatDate(Object value) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String dateValue = dateFormatter.format(value);
		if ( dateValue.endsWith("T00:00:00") ) {
			dateValue = dateValue.replaceAll("T00:00:00", "");
		} else {
			dateValue += getISOTimeZone(value);
		}
		return dateValue;
	}

	private static String getISOTimeZone(Object value) {
		SimpleDateFormat timeZoneFormatter = new SimpleDateFormat("Z");
		String timeZone = timeZoneFormatter.format(value);
		if ( timeZone.equals("Z") ) {
			timeZone = "+00:00";
		} else if ( timeZone.length() == 5) {
			timeZone = timeZone.substring(0,3) + ":" + timeZone.substring(3);
		} else if ( timeZone.length() == 3 ) {
			timeZone = timeZone + ":00";
		}
		return timeZone;
	}

	private static String formatString(Object value) {
		String stringValue = value.toString();
		stringValue = stringValue.replaceAll("'", "''");
		return MessageFormat.format("''{0}''", stringValue);
	}

	private static String formatDouble(Object value) {
		String doubleValue = value.toString();
		if ( doubleValue.indexOf(".") < 0 ) {
			doubleValue += ".0";
		}
		return doubleValue;
	}
}
