/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.codemodule.editors;

import java.text.MessageFormat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.ecmdeveloper.plugin.codemodule.Activator;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.util.IconFiles;
import com.ecmdeveloper.plugin.codemodule.util.Messages;

/**
 * This class wraps a Code Module in an IEditorInput object suitable as input
 * for an editor.
 * 
 * @author Ricardo Belfor
 * 
 */
public class CodeModuleEditorInput implements IEditorInput {

	private static final String CODE_MODULE_TOOLTIP_MESSAGE = 
		Messages.CodeModuleEditorInput_CodeModuleTooltipMessage;

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
		return Activator.getImageDescriptor( IconFiles.ICON_CODEMODULE );
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
		return MessageFormat.format( CODE_MODULE_TOOLTIP_MESSAGE, getName() );
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if ( adapter.equals( CodeModuleFile.class) ) {
			return codeModuleFile;
		}
		return null;
	}
}
