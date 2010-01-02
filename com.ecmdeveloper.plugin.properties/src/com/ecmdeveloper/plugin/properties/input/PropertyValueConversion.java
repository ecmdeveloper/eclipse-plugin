package com.ecmdeveloper.plugin.properties.input;

public class PropertyValueConversion {

	public static String getValueAsString(Object value) {
		if ( value == null ) {
			return "(empty)";
		} else if ( value instanceof Object[] ) {
			String arrayValue = getArrayValue(value);
			return arrayValue;
		} else {
			return value.toString();
		}
	}
	
	private static String getArrayValue(Object value) {
		Object[] arrayValue = (Object[]) value;
		StringBuffer stringBuffer = new StringBuffer();
		String appendPrefix = "";
		for ( Object arrayItem : arrayValue ) {
			stringBuffer.append( appendPrefix );
			stringBuffer.append( arrayItem.toString() );
			appendPrefix = ";";
		}
		String string = stringBuffer.toString();
		return string;
	}
}
