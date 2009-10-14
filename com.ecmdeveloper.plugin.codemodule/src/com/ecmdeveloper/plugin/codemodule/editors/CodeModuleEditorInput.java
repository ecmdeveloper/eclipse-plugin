package com.ecmdeveloper.plugin.codemodule.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class CodeModuleEditorInput implements IEditorInput {

	private CodeModuleFile codeModuleFile;
	
	public CodeModuleEditorInput(CodeModuleFile codeModuleFile)
	{
		this.codeModuleFile = codeModuleFile;
	}
	
	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return codeModuleFile.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Code Module: " + getName();
	}

	@Override
	public Object getAdapter(Class adapter) {
		if ( adapter.equals( CodeModuleFile.class) ) {
			return codeModuleFile;
		}
		return null;
	}
}
