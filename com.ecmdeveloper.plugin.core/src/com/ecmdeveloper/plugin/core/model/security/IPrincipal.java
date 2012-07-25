package com.ecmdeveloper.plugin.core.model.security;

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;

public interface IPrincipal {
	String getName();
	String getShortName();
	String getDisplayName();
	PrincipalType getType();
	boolean isGroup();
	boolean isSpecialAccount();
	boolean isUser();
}
