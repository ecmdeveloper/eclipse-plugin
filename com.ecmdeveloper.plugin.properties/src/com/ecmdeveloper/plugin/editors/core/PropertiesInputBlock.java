package com.ecmdeveloper.plugin.editors.core;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class PropertiesInputBlock extends MasterDetailsBlock {

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
//		FormToolkit toolkit = managedForm.getToolkit();
//		Section propertiesTableSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.DESCRIPTION );
//		propertiesTableSection.setText( "Properties" );
//		propertiesTableSection.setDescription("Select a property from the list" );
//		
//		Composite tableComposite = toolkit.createComposite(propertiesTableSection, SWT.None );
//		
//		Table t = toolkit.createTable(tableComposite, SWT.NULL );
//		
//		propertiesTableSection.setClient( tableComposite );
//		SectionPart sectionPart = new SectionPart(propertiesTableSection);
//		managedForm.addPart( sectionPart );
//		
//		TableViewer tableViewer = new TableViewer(t);
//		tableViewer.setContentProvider( new ArrayContentProvider() );
//		tableViewer.setInput( new String[] {"One", "Two", "Tree" } );

		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Section section = toolkit.createSection(parent, Section.DESCRIPTION);
		section.setText("Model Objects");
		section.setDescription("The list contains objects from the model whose details are editable on the right");
		section.marginWidth = 10;
		section.marginHeight = 5;
		toolkit.createCompositeSeparator(section);
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		client.setLayout(layout);
		Table t = toolkit.createTable(client, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		t.setLayoutData(gd);
		toolkit.paintBordersFor(client);
		section.setClient(client);
		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);
		TableViewer viewer = new TableViewer(t);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});
//		viewer.setContentProvider(new MasterContentProvider());
//		viewer.setLabelProvider(new MasterLabelProvider());
//		viewer.setInput(page.getEditor().getEditorInput());	}
		viewer.setContentProvider( new ArrayContentProvider() );
		viewer.setInput( new String[] {"One", "Two", "Tree" } );
	}
	
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(String.class, new StringDetailsPage() );
	}

}
