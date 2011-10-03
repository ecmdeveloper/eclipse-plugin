package com.ecmdeveloper.plugin.core.model;

import java.util.ArrayList;
import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.IChoice;

public class ChoicePlaceholder implements IChoice {

	public ChoicePlaceholder() {
	}

	@Override
	public Collection<IChoice> getChildren() {
		return new ArrayList<IChoice>();
	}

	@Override
	public String getDisplayName() {
		return "Loading choices...";
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
