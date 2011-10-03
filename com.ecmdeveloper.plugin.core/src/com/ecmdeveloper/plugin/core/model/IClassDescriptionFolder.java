package com.ecmdeveloper.plugin.core.model;

import java.util.ArrayList;
import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.constants.ClassDescriptionFolderType;

public interface IClassDescriptionFolder {

	public String getName();

	public ClassDescriptionFolderType getType();

	public Collection<Object> getChildren();

	public Object getParent();

	public void setChildren(ArrayList<Object> children);

}