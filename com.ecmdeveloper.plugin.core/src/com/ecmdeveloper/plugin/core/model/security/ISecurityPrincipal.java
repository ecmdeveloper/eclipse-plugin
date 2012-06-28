package com.ecmdeveloper.plugin.core.model.security;

import java.util.Collection;

public interface ISecurityPrincipal extends IPrincipal {

	Collection<IAccessControlEntry> getAccessControlEntries(); 
}
