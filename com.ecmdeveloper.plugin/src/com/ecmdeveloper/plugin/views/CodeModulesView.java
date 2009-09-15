package com.ecmdeveloper.plugin.views;

import java.util.Iterator;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import com.ecmdeveloper.plugin.editors.CodeModuleEditor;
import com.ecmdeveloper.plugin.editors.CodeModuleEditorInput;
import com.ecmdeveloper.plugin.handlers.EditCodeModuleHandler;
import com.ecmdeveloper.plugin.model.CodeModuleFile;
import com.ecmdeveloper.plugin.model.CodeModulesManager;
import com.ecmdeveloper.plugin.util.PluginLog;

public class CodeModulesView extends ViewPart {

	private TableViewer viewer;
	private TableColumn typeColumn;
	private TableColumn nameColumn;
	private TableColumn objectStoreColumn;
	
	public CodeModulesView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		createTableViewer(parent);
		hookMouse();
		hookContextMenu();
	}

	private void createTableViewer(Composite parent) {

		viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI | SWT.FULL_SELECTION);
		
		final Table table = viewer.getTable();
		TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);

		typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setText("");
		layout.setColumnData(typeColumn, new ColumnPixelData(18) );

		nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		layout.setColumnData(nameColumn, new ColumnWeightData(1) );

		objectStoreColumn = new TableColumn(table, SWT.LEFT);
		objectStoreColumn.setText("Object Store");
		layout.setColumnData(objectStoreColumn, new ColumnWeightData(1) );

		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		viewer.setContentProvider( new CodeModulesViewContentProvider() );
		viewer.setLabelProvider( new CodeModulesViewLabelProvider() );
		viewer.setInput( CodeModulesManager.getManager() );

		getSite().setSelectionProvider(viewer);
	}
	
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

   private void hookMouse() {
		viewer.getTable().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				openEditor(viewer.getSelection());
			}
		});
	}

	private void hookContextMenu() {
		
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CodeModulesView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
   
	private void fillContextMenu(IMenuManager manager) {
		manager.add(new Separator("edit") );
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator("other"));		
	}
	
   	public void openEditor(ISelection selection) {

		// Get the first element.

		if (!(selection instanceof IStructuredSelection))
			return;
		Iterator<?> iter = ((IStructuredSelection) selection).iterator();
		if (!iter.hasNext())
			return;

		Object elem = iter.next();

		try {
			
			if ( ! EditCodeModuleHandler.isEditorActive( getSite().getPage(), (CodeModuleFile) elem ) ) {
				IEditorInput input = new CodeModuleEditorInput( (CodeModuleFile) elem );
				String editorId = CodeModuleEditor.CODE_MODULE_EDITOR_ID;
				IDE.openEditor(getSite().getPage(), input, editorId);
			}
			
		} catch (PartInitException e) {
			PluginLog.error("Open editor failed" , e);
		}
   	}
}
