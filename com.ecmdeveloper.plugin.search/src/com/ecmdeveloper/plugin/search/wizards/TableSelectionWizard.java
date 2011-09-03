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

package com.ecmdeveloper.plugin.search.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.constants.ClassType;
import com.ecmdeveloper.plugin.classes.wizard.ClassSelectionWizardPage;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.QueryTable2;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class TableSelectionWizard extends Wizard {

	private static final String TITLE = "Select Table";
	
	private ClassSelectionWizardPage classSelectionPage;

	private IQueryTable queryTable;
	
	public TableSelectionWizard() {
		setWindowTitle( TITLE );
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		classSelectionPage = new ClassSelectionWizardPage(ClassType.ALL_CLASSES);
		addPage( classSelectionPage );
	}

	@Override
	public boolean performFinish() {
		
		final ClassDescription classDescription = classSelectionPage.getClassDescription();
		if ( classDescription != null) {
			getShell().getDisplay().syncExec( new Runnable() {
				@Override
				public void run() {
					try {
						getContainer().run(true, false, new IRunnableWithProgress() {
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
								monitor.beginTask("Initializing table " + classDescription.getDisplayName(), IProgressMonitor.UNKNOWN );
								queryTable = new QueryTable2( classDescription );
								try {
									Collection<Object> children = classDescription.getChildren(true);
									for ( Object childClassDescription : children ) {
										
										ClassDescription childClassDescription2 = (ClassDescription) childClassDescription;
										monitor.subTask( "Initializing table " + childClassDescription2.getDisplayName() );
										IQueryTable childTable = new QueryTable2( childClassDescription2 );
										queryTable.addChildQueryTable( childTable );
									}
								} catch (Exception e) {
									PluginMessage.openErrorFromThread(getShell(), TITLE, e.getLocalizedMessage(), e);
								}
								monitor.done();
							}
						});
					} catch (InvocationTargetException e) {
						PluginMessage.openErrorFromThread(getShell(), TITLE, e.getLocalizedMessage(), e);
					} catch (InterruptedException e) {
						// User canceled, so stop but don’t close wizard.
					}

				} });
			
			return true;
		}
		return false;
	}

	public IQueryTable getQueryTable() {
		return queryTable;
	}
}
