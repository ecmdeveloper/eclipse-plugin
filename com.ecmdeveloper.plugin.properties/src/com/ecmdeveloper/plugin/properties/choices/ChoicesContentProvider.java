package com.ecmdeveloper.plugin.properties.choices;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.core.model.IChoice;
import com.ecmdeveloper.plugin.properties.model.Property;

public class ChoicesContentProvider implements IStructuredContentProvider, ITreeContentProvider, PropertyChangeListener {

	private Property property;
	private TreeViewer treeViewer;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	
		treeViewer = (TreeViewer) viewer;
		
		if ( property != null) {
			property.getPropertyDescription().removePropertyChangeListener(this);
		}
		
		property = (Property) newInput;
		
		if ( property != null ) {
			property.getPropertyDescription().addPropertyChangeListener(this);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if ( inputElement instanceof IChoice ) {
			return ((IChoice)inputElement).getChildren().toArray();
		} else {
			return property.getChoices().toArray();
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		if ( element instanceof IChoice ) {
			return ! ((IChoice)element).isSelectable();
		}
		return false;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}

	@Override
	public Object getParent(Object element) {
		if ( element instanceof IChoice ) {
			return ((IChoice)element).getParent();
		}
		return null;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		if ( propertyChangeEvent.getPropertyName().equals("Choices") ) {
			treeViewer.getTree().getDisplay().asyncExec( new Runnable() {
				@Override
				public void run() {
					treeViewer.refresh();
				}
			} );
		}
	}
}
