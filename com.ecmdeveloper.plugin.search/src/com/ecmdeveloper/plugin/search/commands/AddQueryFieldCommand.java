/**
 * Copyright 2011, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.search.commands;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.FreeText;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.InFolderTest;
import com.ecmdeveloper.plugin.search.model.InSubFolderTest;
import com.ecmdeveloper.plugin.search.model.NullTest;
import com.ecmdeveloper.plugin.search.model.QueryElementDescription;
import com.ecmdeveloper.plugin.search.model.WildcardTest;

/**
 * @author ricardo.belfor
 *
 */
public class AddQueryFieldCommand extends CreateCommand {

	private IQueryField queryField;

	public AddQueryFieldCommand(IQueryField queryField) {
		this.queryField = queryField;
	}

	@Override
	public void execute() {

		Shell shell = Display.getCurrent().getActiveShell();

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell,new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				return Activator.getImage( ((QueryElementDescription)element).getIcon() );
			}
			
		});

		dialog.setMultipleSelection(false);
		dialog.setTitle("Query component selection");
		dialog.setMessage("Select the query component for this field.");
		Object[] elements = new Object[] { Comparison.DESCRIPTION, NullTest.DESCRIPTION,
				WildcardTest.DESCRIPTION, InFolderTest.DESCRIPTION, InSubFolderTest.DESCRIPTION,
				FreeText.DESCRIPTION };
		dialog.setElements(elements);
		dialog.open();

		super.execute();
	}
}
