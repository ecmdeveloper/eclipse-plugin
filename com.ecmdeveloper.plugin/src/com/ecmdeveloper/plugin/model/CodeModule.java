package com.ecmdeveloper.plugin.model;


public class CodeModule {
//public class CodeModule extends Document {

	public CodeModule(String name, String id) {
		this.name = name;
		this.id = id;
	}

	protected String name;
	protected String id;
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
//	public CodeModule(Object document, IObjectStoreItem parent) {
//		super(document, parent);
//	}
}
