package com.ecmdeveloper.plugin.core.model.security;

import java.util.Collection;

public interface IPrincipal {

	String getName();
	Boolean isGroup();
	Collection<IAccessControlEntry> getAccessControlEntries(); 
}
