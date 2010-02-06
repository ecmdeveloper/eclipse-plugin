/**
 * Copyright 2009,2010, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.editors.folder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.properties.input.IPropertyInput;
import com.ecmdeveloper.plugin.properties.input.PropertyInputFactory;
import com.filenet.api.admin.PropertyDefinitionString;
import com.filenet.api.constants.Cardinality;
import com.filenet.api.meta.PropertyDescriptionString;

/**
 * 
 * @author Ricardo Belfor
 * @deprecated Editor is now implement differently
 */
public class FolderEditorForm extends FormPage {

	private IMessageManager messageManager;
	private ObjectStoreItem objectStoreItem;
	private ClassDescription classDescription;
	private Map<String,IPropertyInput> propertyInputMap;
	
	public FolderEditorForm(FormEditor editor, ClassDescription classDescription) {
		super(editor, "folderEditorPage", "Main");
		this.classDescription = classDescription;
		propertyInputMap = new HashMap<String, IPropertyInput>();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		final FormToolkit toolkit = managedForm.getToolkit();
		toolkit.getHyperlinkGroup().setHyperlinkUnderlineMode(
                HyperlinkSettings.UNDERLINE_ALWAYS);
		
		toolkit.decorateFormHeading(form.getForm());
		form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter());
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		form.getBody().setLayout(layout);
		
		createPropertiesSection( form, toolkit );
		createActionsSection( form, toolkit );
		
		messageManager = managedForm.getMessageManager();
		form.reflow(true);
	}

	public void refreshFormContent(ObjectStoreItem objectStoreItem ) {
		
		this.objectStoreItem = objectStoreItem;
	
		for (PropertyDescription propertyDescription : classDescription.getPropertyDescriptions() ) {
			IPropertyInput propertyInput = getPropertyInput(propertyDescription);
			propertyInput.setValue( objectStoreItem.getValue( propertyDescription.getName() ) );
		}
		
	}

	private void createActionsSection(ScrolledForm form, FormToolkit toolkit) {
		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR );
		
		section.setText("Action");
		section.setDescription("The actions for this item" );

		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);

		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		client.setLayout(layout);

		
		section.setClient(client);
	}

	private void createPropertiesSection(ScrolledForm form, FormToolkit toolkit) {

		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR );
		
		section.setText("Properties");
		section.setDescription("The properties of this folder" );

		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);

		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		client.setLayout(layout);

		for (PropertyDescription propertyDescription : classDescription.getPropertyDescriptions() ) {
			addProperty( client, propertyDescription );
		}
		section.setClient(client);
	}

	private void addProperty(Composite client, PropertyDescription propertyDescription) {

		IPropertyInput propertyInput = PropertyInputFactory.getPropertyInput(propertyDescription, true );
		propertyInput.renderControls(client, 2);
		addPropertyInput(propertyDescription, propertyInput);
	}

	private void addPropertyInput(PropertyDescription propertyDescription,
			IPropertyInput propertyInput) {
		propertyInputMap.put( propertyDescription.getName(), propertyInput );
	}

	private IPropertyInput getPropertyInput(PropertyDescription propertyDescription) {
		IPropertyInput propertyInput = propertyInputMap.get( propertyDescription.getName() );
		return propertyInput;
	}
}
