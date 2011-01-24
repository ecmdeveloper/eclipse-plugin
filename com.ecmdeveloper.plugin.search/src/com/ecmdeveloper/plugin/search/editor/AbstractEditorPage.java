/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ecmdeveloper.plugin.search.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.ecmdeveloper.plugin.search.model.Query;


/**
 * An abstract base class for all editor pages used in the
 * <code>NetworkEditor</code>.
 * 
 * <p>It provides basic support for typical GEF handling like 
 * <code>CommandStack</code>s, <code>GraphicalViewer</code>s, a
 * Palette and so on.
 * 
 * @author Gunnar Wagenknecht
 */
public abstract class AbstractEditorPage extends EditorPart
{
//    private final SearchEditor parent;
//    private final EditDomain domain;
//
//    /**
//     * Creates a new AbstractEditorPage instance.
//     * @param parent the parent multi page editor
//     * @param domain the edit domain
//     */
//    public AbstractEditorPage(SearchEditor parent, EditDomain domain)
//    {
//        super();
//        this.parent = parent;
//        this.domain = domain;
//    }
//
//    public final void doSave(IProgressMonitor monitor)
//    {
//        getNetworkEditor().doSave(monitor);
//    }
//
//    public final void doSaveAs()
//    {
//        getNetworkEditor().doSaveAs();
//    }
//
//
//    /* (non-Javadoc)
//     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
//     */
//    public void init(IEditorSite site, IEditorInput input)
//        throws PartInitException
//    {
//        setSite(site);
//        setInput(input);
//        setTitle(input.getName() + ": " + getPageName());
//    }
//
//    public final boolean isDirty()
//    {
//        return getNetworkEditor().isDirty();
//    }
//
//    protected final CommandStack getCommandStack()
//    {
//        return getEditDomain().getCommandStack();
//    }
//
//    protected PaletteRoot getPaletteRoot()
//    {
//        return getNetworkEditor().getPaletteRoot();
//    }
//
//    public final boolean isSaveAsAllowed()
//    {
//        return getNetworkEditor().isSaveAsAllowed();
//    }
//
//    public void setFocus()
//    {
//        getGraphicalViewer().getControl().setFocus();
//    }
//
//    public final void createPartControl(Composite parent)
//    {
////        Composite composite = new Composite(parent, SWT.NONE);
////        GridLayout layout = new GridLayout();
////        layout.marginHeight = 10;
////        layout.marginWidth = 10;
////        layout.verticalSpacing = 5;
////        layout.horizontalSpacing = 5;
////        layout.numColumns = 1;
////        composite.setLayout(layout);
////        composite.setBackground(
////            parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
////        composite.setForeground(
////            parent.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
////
////        // label on top
////        Label label =
////            new Label(composite, SWT.HORIZONTAL | SWT.SHADOW_OUT | SWT.LEFT);
////        label.setText(getTitle());
////        label.setFont(
////            JFaceResources.getFontRegistry().get(JFaceResources.HEADER_FONT));
////        label.setBackground(
////            parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
////        label.setForeground(
////            parent.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
////        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
////
////        // now the main editor page
////        composite = new Composite(composite, SWT.NONE);
////        composite.setLayout(new FillLayout());
////        composite.setBackground(
////            parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
////        composite.setForeground(
////            parent.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
////        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
//        createPageControl(parent);
//    }
//
//    /**
//     * Returns the human readable name of this editor page.
//     * @return the human readable name of this editor page
//     */
//    protected abstract String getPageName();
//
//    /**
//     * Creates the cotrol of this editor page.
//     * @param parent
//     */
//    protected abstract void createPageControl(Composite parent);
//
//    protected final SearchEditor getNetworkEditor()
//    {
//        return parent;
//    }
//
//    /**
//     * Returns the edit domain this editor page uses.
//     * @return the edit domain this editor page uses
//     */
//    public final EditDomain getEditDomain()
//    {
//        return domain;
//    }
//
//    /**
//     * Hooks a <code>EditPartViewer</code> to the rest of the Editor.
//     * 
//     * <p>By default, the viewer is added to the SelectionSynchronizer, 
//     * which can be used to keep 2 or more EditPartViewers in sync.
//     * The viewer is also registered as the ISelectionProvider
//     * for the Editor's PartSite.
//     * 
//     * @param viewer the viewer to hook into the editor
//     */
//    protected void registerEditPartViewer(EditPartViewer viewer)
//    {
//        // register viewer to edit domain
//        getEditDomain().addViewer(viewer);
//
//        // the multi page network editor keeps track of synchronizing
//        getNetworkEditor().getSelectionSynchronizer().addViewer(viewer);
//
//        // add viewer as selection provider
//        getSite().setSelectionProvider(viewer);
//    }
//
//    /**
//     * Configures the specified <code>EditPartViewer</code>.
//     * 
//     * @param viewer
//     */
//    protected void configureEditPartViewer(EditPartViewer viewer)
//    {
//        // configure the shared key handler
//        if (viewer.getKeyHandler() != null)
//            viewer.getKeyHandler().setParent(
//                getNetworkEditor().getSharedKeyHandler());
//    }
//
//    /**
//     * Returns the workflow that is edited.
//     * @return the workflow that is edited
//     */
//    protected Query getNetwork()
//    {
//        return getNetworkEditor().getQuery();
//    }
//
//    /** the palette viewer */
//    private PaletteViewer paletteViewer;
//
//    /**
//     * Creates the createPaletteViewer on the specified <code>Composite</code>.
//     * @param parent the parent composite
//     */
//    protected void createPaletteViewer(Composite parent)
//    {
//        // create graphical viewer
//        paletteViewer = new PaletteViewer();
//        paletteViewer.createControl(parent);
//
//        // configure the viewer
//        paletteViewer.getControl().setBackground(parent.getBackground());
//
//        // hook the viewer into the EditDomain (only one palette per EditDomain)
//        getEditDomain().setPaletteViewer(paletteViewer);
//
//        // important: the palette is initialized via EditDomain
//        //fancy palette: paletteViewer.setEditPartFactory(new CustomizedPaletteEditPartFactory());
//        getEditDomain().setPaletteRoot(getPaletteRoot());
//    }
//
//    /**
//     * Returns the palette viewer.
//     * @return the palette viewer
//     */
//    protected PaletteViewer getPaletteViewer()
//    {
//        return paletteViewer;
//    }
//
//    /**
//     * Returns the graphical viewer of this page.
//     * 
//     * <p>This viewer is used for example for zoom support 
//     * and for the thumbnail in the overview of the outline page.
//     * 
//     * @return the viewer
//     */
//    protected abstract GraphicalViewer getGraphicalViewer();
}
