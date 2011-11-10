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
import java.util.Calendar;

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
			return formatDate(value, queryField );
		case DOUBLE:
			return formatDouble(value);
		case OBJECT:
			return MessageFormat.format("OBJECT(''{0}'')", value.toString());
		case STRING:
			return formatString(value);
		}
		return null;
	}

	private static String formatDate(Object value, IQueryField queryField) {
		
		if ( queryField.getQueryTable().isContentEngineTable() ) { 
			return formatContentEngineDate(value);
		} else {
			return formatCmisDate(value);
		}
	}

	private static String formatCmisDate(Object value) {

		StringBuffer output = new StringBuffer();
		Calendar calendar = (Calendar)value;
		output.append( getDateTimeInTimeZone(calendar) );
		output.append(".000");
		output.append( getTimeZoneOffset(calendar) );
		return output.toString();
	}

	private static String getTimeZoneOffset(Calendar calendar) {
		int rawOffset = calendar.getTimeZone().getRawOffset();
		int hour = rawOffset / (60*60*1000);
		String timeZoneOffset;
		int minute = Math.abs(rawOffset/(60*1000)) % 60;
		if ( hour == 0 && minute == 0 ) {
			timeZoneOffset = "Z";
		} else if ( hour < 0 ) {
			timeZoneOffset = String.format("-%02d:%02d", -hour, minute);
		} else {
			timeZoneOffset = String.format("+%02d:%02d", hour, minute);
		}
		return timeZoneOffset;
	}

	private static String getDateTimeInTimeZone(Calendar calendar) {
		String dateTimeInTimeZone = String.format("%04d-%02d-%02dT%02d:%02d:%02d", calendar
				.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar
				.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar
				.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		return dateTimeInTimeZone;
	}
	
	private static String formatContentEngineDate(Object value) {

		StringBuffer output = new StringBuffer();
		Calendar calendar = (Calendar)value;
		String dateValue = getDateTimeInTimeZone(calendar);
		if ( dateValue.endsWith("T00:00:00") ) {
			dateValue = dateValue.replaceAll("T00:00:00", "");
		}
		output.append( dateValue );
		output.append( getTimeZoneOffset(calendar) );
		return output.toString();
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
