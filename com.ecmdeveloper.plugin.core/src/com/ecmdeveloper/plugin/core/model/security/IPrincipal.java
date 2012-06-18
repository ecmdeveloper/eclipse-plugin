package com.ecmdeveloper.plugin.core.model.security;

import java.util.List;

public interface IPrincipal {

	String getName();
	Boolean isGroup();
	List<IAccessControlEntry> getAccessControlEntries(); 
}
