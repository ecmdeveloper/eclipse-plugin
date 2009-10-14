package com.ecmdeveloper.plugin.codemodule.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManagerEvent;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManagerListener;

public class CodeModulesViewContentProvider implements IStructuredContentProvider, CodeModulesManagerListener {

	private TableViewer viewer;
	private CodeModulesManager manager;

	@Override
	public Object[] getElements(Object inputElement) {
		return manager.getCodeModuleFiles().toArray();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (manager != null)
			manager.removeCodeModulesManagerListener(this);
		manager = (CodeModulesManager) newInput;
		if (manager != null)
			manager.addCodeModuleManagerListener(this);
	}

	@Override
	public void codeModuleFilesItemsChanged(CodeModulesManagerEvent event) {

		if ( event.getItemsUpdated() != null ) {
			viewer.update(event.getItemsUpdated(), null );
		}

		if ( event.getItemsRemoved() != null ) {
			viewer.remove( event.getItemsRemoved() );
		}
		
		if ( event.getItemsAdded() != null ) {
			viewer.add( event.getItemsAdded() );
		}
	}
}
