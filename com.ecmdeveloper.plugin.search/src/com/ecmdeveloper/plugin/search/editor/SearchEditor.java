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

package com.ecmdeveloper.plugin.search.editor;

import java.util.EventObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public class SearchEditor extends MultiPageEditorPart {

	private QueryPage queryPage;
    private PaletteRoot paletteRoot;
    private SelectionSynchronizer synchronizer;
    private KeyHandler sharedKeyHandler;
    private ActionRegistry actionRegistry;
    private boolean isDirty = false;

    private DelegatingCommandStack delegatingCommandStack;
    private MultiPageCommandStackListener multiPageCommandStackListener;

    /**
     * The <code>CommandStackListener</code> that listens for
     * changes of the <code>DelegatingCommandStack</code>.
     */
    private CommandStackListener delegatingCommandStackListener =
        new CommandStackListener()
    {
        public void commandStackChanged(EventObject event)
        {
            //updateActions(stackActionIDs);
        }
    };
    
    private ISelectionListener selectionListener = new ISelectionListener()
    {
        public void selectionChanged(IWorkbenchPart part, ISelection selection)
        {
// TODO: use this and fix this        	
//            updateActions(editPartActionIDs);
        }
    };

    
	@Override
	protected void createPages() {
		
		queryPage = new QueryPage(this);
		try {
			addPage(queryPage, getEditorInput() );
			
            getMultiPageCommandStackListener().addCommandStack(
                    queryPage.getCommandStack());
                
                // activate delegating command stack
                getDelegatingCommandStack().setCurrentCommandStack(
                    queryPage.getCommandStack());
			
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public PaletteRoot getPaletteRoot() {
        if (null == paletteRoot)
        {
            paletteRoot = new QueryPaletteRoot();
        }
        return paletteRoot;
	}

	public SelectionSynchronizer getSelectionSynchronizer() {
        if (synchronizer == null)
            synchronizer = new SelectionSynchronizer();
        return synchronizer;
	}

	public Query getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	public KeyHandler getSharedKeyHandler() {
        if (sharedKeyHandler == null)
        {
            sharedKeyHandler = new KeyHandler();

            // configure common keys for all viewers
            sharedKeyHandler.put(
                KeyStroke.getPressed(SWT.DEL, 127, 0),
                getActionRegistry().getAction(GEFActionConstants.DELETE));
            sharedKeyHandler.put(
                KeyStroke.getPressed(SWT.F2, 0),
                getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT));
        }
        return sharedKeyHandler;
	}

	private ActionRegistry getActionRegistry() {
        if (actionRegistry == null)
            actionRegistry = new ActionRegistry();

        return actionRegistry;
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

    protected DelegatingCommandStack getDelegatingCommandStack()
    {
        if (null == delegatingCommandStack)
        {
            delegatingCommandStack = new DelegatingCommandStack();
            if (null != getCurrentPage())
                delegatingCommandStack.setCurrentCommandStack(
                    getCurrentPage().getCommandStack());
        }

        return delegatingCommandStack;
    }

    protected CommandStackListener getDelegatingCommandStackListener()
    {
        return delegatingCommandStackListener;
    }
    
    private AbstractEditorPage getCurrentPage()
    {
        if (getActivePage() == -1)
            return null;

        return (AbstractEditorPage) getEditor(getActivePage());
    }

    protected MultiPageCommandStackListener getMultiPageCommandStackListener()
    {
        if (null == multiPageCommandStackListener)
            multiPageCommandStackListener = new MultiPageCommandStackListener();
        return multiPageCommandStackListener;
    }

    public void dispose()
    {
        // dispose multi page command stack listener
        getMultiPageCommandStackListener().dispose();

        // remove delegating CommandStackListener
        getDelegatingCommandStack().removeCommandStackListener( getDelegatingCommandStackListener() );

//        // remove selection listener
//        getSite()
//            .getWorkbenchWindow()
//            .getSelectionService()
//            .removeSelectionListener(
//            getSelectionListener());
//
        // disposy the ActionRegistry (will dispose all actions)
        getActionRegistry().dispose();

        // important: always call super implementation of dispose
        super.dispose();
    }
}
