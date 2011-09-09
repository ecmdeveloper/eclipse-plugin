/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.commands;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.task.GetClassDescriptionTask;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoresManager;
import com.ecmdeveloper.plugin.diagrams.Activator;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;
import com.ecmdeveloper.plugin.diagrams.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramClassRefreshCommand extends Command {

	private static final String OBJECT_STORE_NOT_CONNECTED_MESSAGE = "Object Store \"{0}\" is not connected";
	private static final String COMMAND_NAME_FORMAT = "Refresh Class {0}";

	private ClassDiagram classDiagram;
    private ClassDiagramClass classDiagramClass;
    private ClassDiagramClass refreshedClassDiagramClass;

	public ClassDiagramClassRefreshCommand(ClassDiagramClass classDiagramClass) {
		this.classDiagram = classDiagramClass.getParent();
		this.classDiagramClass = classDiagramClass;
		setLabel(MessageFormat.format(COMMAND_NAME_FORMAT, classDiagramClass.getDisplayName()));
	}

	public void execute() {
		redo();
	}

	private IObjectStore getObjectStore(Shell activeShell) {
		String connectionName = classDiagramClass.getConnectionName();
		String objectStoreName = classDiagramClass.getObjectStoreName();
		final IObjectStoresManager objectStoresManager = Activator.getDefault().getObjectStoresManager();
		final IObjectStore objectStore = objectStoresManager.getObjectStore(connectionName, objectStoreName);
		
		if ( ! objectStore.isConnected() ) {
			String message = MessageFormat.format(OBJECT_STORE_NOT_CONNECTED_MESSAGE, objectStore
					.getDisplayName());
			MessageDialog.openError(activeShell, getLabel(), message );
		}
		return objectStore;
	}

	public void redo() {

		final Shell activeShell = Display.getCurrent().getActiveShell();
		final IObjectStore objectStore = getObjectStore(activeShell);
		if ( ! objectStore.isConnected() ) {
			return;
		}
		
		IRunnableContext context = new ProgressMonitorDialog(activeShell);
		try {
			context.run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					
						monitor.beginTask(getLabel(), IProgressMonitor.UNKNOWN);
						try {
							ClassDescription classDescription = getClassDescription(objectStore);
							if ( ! monitor.isCanceled() ) { 
								initializeRefreshedClassDiagramClass(classDescription);
							}
						} catch (Exception e) {
							PluginMessage.openErrorFromThread(activeShell, getLabel(), e.getMessage(), e );
						}
						
						monitor.done();
				}

				private void initializeRefreshedClassDiagramClass(ClassDescription classDescription) {
					refreshedClassDiagramClass = (ClassDiagramClass) classDescription.getAdapter(ClassDiagramClass.class);
					refreshedClassDiagramClass.setLocation( classDiagramClass.getLocation() );
					refreshedClassDiagramClass.setParentClassId( classDiagramClass.getParentClassId() );
				}

				private ClassDescription getClassDescription(final IObjectStore objectStore ) throws Exception {
					GetClassDescriptionTask task = new GetClassDescriptionTask( classDiagramClass.getName(), objectStore);
					Activator.getDefault().getTaskManager().executeTaskSync(task);
					ClassDescription classDescription = task.getClassDescription();
					return classDescription;
				}
				
			});
		} catch (Exception e) {
			PluginMessage.openError(activeShell, getLabel(), e.getMessage(), e );
		}

		replaceClassDiagramClass();
	}

	private void replaceClassDiagramClass() {
		classDiagram.deleteClassDiagramElement( classDiagramClass );
		classDiagram.addClassDiagramElement( refreshedClassDiagramClass );
	}

	public boolean canUndo() {
		return refreshedClassDiagramClass != null && classDiagramClass != null;
	}

	public void undo() {
		classDiagram.deleteClassDiagramElement( refreshedClassDiagramClass);
		classDiagram.addClassDiagramElement( classDiagramClass );
	}
}
