package com.ecmdeveloper.plugin.diagrams.model;

public class ClassDiagramAttribute {

	private String name;
	private String type;
	private String defaultValue;
	private String multiplicity;
	private boolean readOnly;
	private boolean ordered;
	private boolean unique;
	
	public ClassDiagramAttribute(String name, String type, String defaultValue,
			String multiplicity, boolean readOnly, boolean ordered,
			boolean unique) {
		super();
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.multiplicity = multiplicity;
		this.readOnly = readOnly;
		this.ordered = ordered;
		this.unique = unique;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getMultiplicity() {
		return multiplicity;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isOrdered() {
		return ordered;
	}

	public boolean isUnique() {
		return unique;
	}

	private String getModifiers() {
		StringBuffer modifiersText = new StringBuffer();
		String separator = "";

		if ( isReadOnly() ) {
			modifiersText.append( separator );
			modifiersText.append( "readOnly" );
			separator = ",";
		}
		
		if ( isOrdered() ) {
			modifiersText.append( separator );
			modifiersText.append( "ordered" );
			separator = ",";
		}
		
		if ( isUnique() ) {
			modifiersText.append( separator );
			modifiersText.append( "unique" );
			separator = ",";
		}
		
		return modifiersText.toString();
	}
	/**
	 * Attributes are formatted according to the UML standard:
	 * 
	 *  <property> ::= [<visibility>] [‘/’] <name> [‘:’ <prop-type>] [‘[‘ <multiplicity> ‘]’] [‘=’ <default>][‘{‘ <prop-modifier > [‘,’ <prop-modifier >]* ’}’]
	 */
	@Override
	public String toString() {
		StringBuffer attributeText = new StringBuffer();
		attributeText.append("+ ");
		attributeText.append( getName() );
		if ( type != null ) {
			attributeText.append( " : ");
			attributeText.append( type );
		}
		
		if (  multiplicity != null ) {
			attributeText.append( " [" );
			attributeText.append( multiplicity );
			attributeText.append( "]" );
		}
		
		if ( defaultValue != null ) {
			attributeText.append( " = ");
			attributeText.append( defaultValue );
		}
		
		String modifiers = getModifiers();
		if ( ! modifiers.isEmpty() ) {
			attributeText.append( " {" );
			attributeText.append( modifiers );
			attributeText.append( "}" );
		}
		
		return attributeText.toString();
	}
}
