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

package com.ecmdeveloper.plugin.search.policies;

import java.util.ArrayList;

import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.commands.CreateClassTestCommand;
import com.ecmdeveloper.plugin.search.commands.CreateCommand;
import com.ecmdeveloper.plugin.search.commands.CreateComparisonCommand;
import com.ecmdeveloper.plugin.search.commands.CreateFreeTextCommand;
import com.ecmdeveloper.plugin.search.commands.CreateFullTextQueryCommand;
import com.ecmdeveloper.plugin.search.commands.CreateInFolderTestCommand;
import com.ecmdeveloper.plugin.search.commands.CreateInSubFolderTestCommand;
import com.ecmdeveloper.plugin.search.commands.CreateInTestCommand;
import com.ecmdeveloper.plugin.search.commands.CreateNullTestCommand;
import com.ecmdeveloper.plugin.search.commands.CreateThisInFolderTestCommand;
import com.ecmdeveloper.plugin.search.commands.CreateThisInTreeTestCommand;
import com.ecmdeveloper.plugin.search.commands.CreateWildcardTestCommand;
import com.ecmdeveloper.plugin.search.editor.QueryCreationFactory;
import com.ecmdeveloper.plugin.search.model.ClassTest;
import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.FreeText;
import com.ecmdeveloper.plugin.search.model.FullTextQuery;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.InFolderTest;
import com.ecmdeveloper.plugin.search.model.InSubFolderTest;
import com.ecmdeveloper.plugin.search.model.InTest;
import com.ecmdeveloper.plugin.search.model.NullTest;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryElementDescription;
import com.ecmdeveloper.plugin.search.model.QuerySubpart;
import com.ecmdeveloper.plugin.search.model.ThisInFolderTest;
import com.ecmdeveloper.plugin.search.model.ThisInTreeTest;
import com.ecmdeveloper.plugin.search.model.WildcardTest;

/**
 * @author ricardo.belfor
 *
 */
public class QueryCommandFactory {

	public CreateCommand getCreateCommand(CreateRequest request) {
		Object objectType = request.getNewObjectType();
		return getCreateCommand(objectType);
	}

	public CreateCommand getCreateCommand(Object objectType) {
		
		CreateCommand command;
		if ( objectType == Comparison.class ) {
			command = new CreateComparisonCommand();
		} else if ( objectType == NullTest.class ) {
			command = new CreateNullTestCommand();
		} else if ( objectType == InTest.class ) {
			command = new CreateInTestCommand();
		} else if ( objectType == WildcardTest.class ) {
			command = new CreateWildcardTestCommand();
		} else if ( objectType == InFolderTest.class ) {
			command = new CreateInFolderTestCommand();
		} else if ( objectType == ThisInFolderTest.class ) {
			command = new CreateThisInFolderTestCommand();
		} else if ( objectType == ThisInTreeTest.class ) {
			command = new CreateThisInTreeTestCommand();
		} else if ( objectType == InSubFolderTest.class ) {
			command = new CreateInSubFolderTestCommand();
		} else if ( objectType == FreeText.class ) {
			command = new CreateFreeTextCommand();
		} else if ( objectType == ClassTest.class ) {
			command = new CreateClassTestCommand();
		} else if ( objectType == FullTextQuery.class ) {
			command = new CreateFullTextQueryCommand();
		} else {			
			command = new CreateCommand();
		}
				
		return command;
	}

	public CreateCommand getCreateCommand(IQueryField queryField, Query query) {
		
		CreateCommand createCommand;
		Shell shell = Display.getCurrent().getActiveShell();
		ElementListSelectionDialog dialog = createSelectionDialog(shell, queryField);
		if ( dialog.open() == Dialog.OK ) {
			QueryElementDescription description = (QueryElementDescription) dialog.getFirstResult();
			QueryCreationFactory q = new QueryCreationFactory(query, description.getObjectType() );
			createCommand = getCreateCommand(description.getObjectType() );
			createCommand.setQueryField( queryField );
			QuerySubpart newPart = (QuerySubpart) q.getNewObject();
			createCommand.setChild(newPart);
		} else {
			return null;
		}
		return createCommand;
	}

	private ElementListSelectionDialog createSelectionDialog(Shell shell, IQueryField queryField) {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell,new LabelProvider() {
	
			@Override
			public Image getImage(Object element) {
				return Activator.getImage( ((QueryElementDescription)element).getIcon() );
			}
			
		});
	
		dialog.setMultipleSelection(false);
		dialog.setTitle("Query component selection");
		dialog.setMessage("Select the query component for this field.");
		ArrayList<Object> operations = new ArrayList<Object>();
		
		if ( Comparison.DESCRIPTION.isValidFor(queryField)) {
			operations.add(Comparison.DESCRIPTION);
		}
		
		if ( NullTest.DESCRIPTION.isValidFor(queryField)) {
			operations.add(NullTest.DESCRIPTION);
		}

		if ( InTest.DESCRIPTION.isValidFor(queryField)) {
			operations.add(InTest.DESCRIPTION);
		}

		if ( WildcardTest.DESCRIPTION.isValidFor(queryField)) {
			operations.add(WildcardTest.DESCRIPTION);
		}

		if ( InFolderTest.DESCRIPTION.isValidFor(queryField)) {
			operations.add(InFolderTest.DESCRIPTION);
		}

		if ( ThisInFolderTest.DESCRIPTION.isValidFor(queryField)) {
			operations.add(ThisInFolderTest.DESCRIPTION);
		}

		if ( ThisInTreeTest.DESCRIPTION.isValidFor(queryField)) {
			operations.add(ThisInTreeTest.DESCRIPTION);
		}

		if ( InSubFolderTest.DESCRIPTION.isValidFor(queryField)) {
			operations.add(InSubFolderTest.DESCRIPTION);
		}

		if ( FreeText.DESCRIPTION.isValidFor(queryField)) {
			operations.add(FreeText.DESCRIPTION);
		}

		if ( ClassTest.DESCRIPTION.isValidFor(queryField)) {
			operations.add(ClassTest.DESCRIPTION);
		}
		
		if ( FullTextQuery.DESCRIPTION.isValidFor(queryField)) {
			operations.add(FullTextQuery.DESCRIPTION);
		}

		dialog.setElements(operations.toArray());
		return dialog;
	}
}
