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

package com.ecmdeveloper.plugin.scripting.handlers;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.scripting.Activator;
import com.ecmdeveloper.plugin.scripting.classloader.ProjectClassLoader;
import com.ecmdeveloper.plugin.scripting.dialogs.MethodSelectionDialog;
import com.ecmdeveloper.plugin.scripting.engine.MethodRunner;
import com.ecmdeveloper.plugin.scripting.engine.ScriptingContext;
import com.ecmdeveloper.plugin.scripting.jobs.ExecuteMethodJob;
import com.ecmdeveloper.plugin.scripting.util.PluginMessage;
import com.ecmdeveloper.plugin.scripting.wizard.LaunchScriptWizard;

/**
 * @author ricardo.belfor
 *
 */
public class ExecuteMethodHandler  extends AbstractHandler implements IHandler {

	private static final String HANDLER_NAME = "Execute Script";

	private static final String LOADING_CLASS_FAILED_MESSAGE = "Loading class \"{0}\" failed";

	private IMethod method;
	private String name;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		setName(event);

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		LaunchScriptWizard wizard = new LaunchScriptWizard();
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.create();
		if ( dialog.open() == Dialog.OK ) {
		}
		 
		MethodSelectionDialog methodSelectionDialog = createMethodSelectionDialog(window);
		method = selectMethod(methodSelectionDialog);
		if ( method == null ) {
			return null;
		}

		ScriptingContext scriptingContext = getScriptingContext(selection);
		if ( scriptingContext == null ) {
			return null;
		}
		
		scriptingContext.setScriptMethodName(method.getElementName());
		scriptingContext.setScriptClassName(method.getDeclaringType().getFullyQualifiedName());
		
		try {
			String filename = Activator.getDefault().getScriptingContextSerializer().serialize(scriptingContext);
			executeMethod(filename);
		} catch (Exception e) {
			PluginMessage.openError(window.getShell(), HANDLER_NAME, e.getLocalizedMessage(), e );
			return null;
		}

		return null;
	}

	private ScriptingContext getScriptingContext(ISelection selection) {
		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		ScriptingContext scriptingContext = null;
		while ( iterator.hasNext() ) {
			
			IObjectStoreItem objectStoreItem = (IObjectStoreItem) iterator.next();
			if ( scriptingContext == null ) {
				ObjectStore objectStore = objectStoreItem.getObjectStore();
				ContentEngineConnection connection = objectStore.getConnection();
				scriptingContext = new ScriptingContext(connection.getUsername(), connection
						.getPassword(), connection.getUrl(), objectStore.getName() );
			}
			scriptingContext.addObject(objectStoreItem.getId(), objectStoreItem.getClassName() );
		}
		return scriptingContext;
	}

	private void executeMethod(String filename) throws CoreException {

		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(ID_JAVA_APPLICATION);
		
		ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, HANDLER_NAME );
		workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, MethodRunner.class.getName() );
		workingCopy.setAttribute(ATTR_PROJECT_NAME, method.getJavaProject().getElementName() );
		workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS, "\"" + filename + "\"" );
		ILaunchConfiguration launchConfiguration = workingCopy.doSave();
		DebugUITools.launch(launchConfiguration,ILaunchManager.RUN_MODE);
	}

	private MethodSelectionDialog createMethodSelectionDialog(IWorkbenchWindow window) {
		MethodSelectionDialog methodSelectionDialog = new MethodSelectionDialog(window.getShell());
		methodSelectionDialog.setInitialPattern("**");
		return methodSelectionDialog;
	}

	private IMethod selectMethod(MethodSelectionDialog methodSelectionDialog) {
		IMethod method = null;
		if ( methodSelectionDialog.open() == Dialog.OK ) {
			method = (IMethod) methodSelectionDialog.getFirstResult();
		}
	
		return method;
	}

	private Class<?> loadMethodClass() {
		IType type = method.getDeclaringType();
		String typeName = type.getFullyQualifiedName();
		ClassLoader loader = new ProjectClassLoader(type.getJavaProject());
		try {
			return loader.loadClass(typeName);
		}
		catch (ClassNotFoundException e) {
			String message = MessageFormat.format( LOADING_CLASS_FAILED_MESSAGE, typeName);
			PluginMessage.openErrorFromThread(name, message, e);
		}
		return null;
	}

	private Method getClassMethod(Class<?> methodClass) {
		Method classMethod = null;
		try {
			classMethod = methodClass.getMethod(method.getElementName(), new Class[] {Object.class, Writer.class});
		} catch (Exception e) {
			String message = MessageFormat.format("Getting method \"{0}\" failed", method.getElementName() );
			PluginMessage.openErrorFromThread(name, message, e);
		}
		return classMethod;
	}

	private Writer getWriter() {
		MessageConsole console = findConsole("Script Console");
		console.clearConsole();
		OutputStream outputStream = console.newOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(outputStream);
		return writer;
	}

	private void setName(ExecutionEvent event) {
		try {
			name = event.getCommand().getName();
		} catch (NotDefinedException e) {
			name = "";
		}
	}
	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager consoleManager = plugin.getConsoleManager();
		MessageConsole console = findConsole(name, consoleManager);
		if ( console == null) {
			console = createConsole(name, consoleManager);
		}
		return console;
	}

	private MessageConsole createConsole(String name, IConsoleManager consoleManager) {
		MessageConsole myConsole = new MessageConsole(name, null);
		consoleManager.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	private MessageConsole findConsole( String name, IConsoleManager consoleManager ) {
		IConsole[] existing = consoleManager.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		}
		return null;
	}

	private void scheduleJob(ISelection selection, Class<?> methodClass, Method classMethod,
			Writer writer) {
		ObjectStoreItem[] objectStoreItems = getObjectStoreItems((IStructuredSelection) selection);
		ExecuteMethodJob job = new ExecuteMethodJob(methodClass, classMethod, objectStoreItems, writer );
		job.setUser(true);
		job.schedule();
	}

	private ObjectStoreItem[] getObjectStoreItems(IStructuredSelection selection) {
		
		ArrayList<ObjectStoreItem> itemsRenamed = new ArrayList<ObjectStoreItem>();
		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while (iterator.hasNext()) {
			Object elem = iterator.next();
	
			if (elem instanceof ObjectStoreItem) {
				itemsRenamed.add((ObjectStoreItem) elem);
			}
		}
		return itemsRenamed.toArray( new ObjectStoreItem[itemsRenamed.size()] );
	}
}
