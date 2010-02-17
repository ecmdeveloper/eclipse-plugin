package com.ecmdeveloper.plugin.classes.model;

import java.util.ArrayList;
import java.util.Collection;

public class ChoicePlaceholder extends Choice {

	public ChoicePlaceholder() {
		super(null,null);
	}

	@Override
	public Collection<Choice> getChildren() {
		return new ArrayList<Choice>();
	}

	@Override
	public String getDisplayName() {
		return "Loading choices...";
	}

	@Override
	public Object getObjectStoreObject() {
		return null;
	}

	@Override
	public Object getParent() {
		return null;
	}

	@Override
	public Object getValue() {
		return "Loading choices...";
	}

	@Override
	public boolean isSelectable() {
		return false;
	}
}
