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
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.scripting.classloader.ProjectClassLoader;
import com.ecmdeveloper.plugin.scripting.dialogs.MethodSelectionDialog;
import com.ecmdeveloper.plugin.scripting.jobs.ExecuteMethodJob;
import com.ecmdeveloper.plugin.scripting.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class ExecuteMethodHandler  extends AbstractHandler implements IHandler {

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
		
		MethodSelectionDialog methodSelectionDialog = createMethodSelectionDialog(window);
		method = selectMethod(methodSelectionDialog);
		if ( method == null ) {
			return null;
		}

		Class<?> methodClass = loadMethodClass();
		if ( methodClass != null ) {
			Method classMethod = getClassMethod(methodClass);
			if ( classMethod != null ) {
				Writer writer = getWriter();
				scheduleJob(selection, methodClass, classMethod, writer);
			}
		}
		
		return null;
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
