package com.ecmdeveloper.plugin.properties.choices;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.classes.model.Choice;
import com.ecmdeveloper.plugin.properties.model.Property;

public class ChoicesContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private Property property;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		property = (Property) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if ( inputElement instanceof Choice ) {
			return ((Choice)inputElement).getChildren().toArray();
		} else {
			return property.getChoices().toArray();
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		if ( element instanceof Choice ) {
			return ! ((Choice)element).isSelectable();
		}
		return false;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}

	@Override
	public Object getParent(Object element) {
		if ( element instanceof Choice ) {
			return ((Choice)element).getParent();
		}
		return null;
	}

	@Override
	public void dispose() {
	}
}
