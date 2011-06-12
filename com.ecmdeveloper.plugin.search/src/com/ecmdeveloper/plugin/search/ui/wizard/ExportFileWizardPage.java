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

package com.ecmdeveloper.plugin.search.ui.wizard;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class ExportFileWizardPage extends WizardPage {

	private static final String DESCRIPTION = "Select the format and the destination of the export file.";
	private static final String TITLE = "Export File";

	public enum Format {
		CSV { 		
			@Override
			public String toString() {
				return "CSV";
			}
		},
		XML{ 		
			@Override
			public String toString() {
				return "XML";
			}
		},
		HTML { 		
			@Override
			public String toString() {
				return "HTML";
			}
		}
	}

	private Format format = Format.CSV;
	private String filename;
	private String exportPath;
	private boolean openFileInEditor;
	
	protected ExportFileWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION );
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = createContainer(parent);
//		createFormatButtons(container);
//		createFileSelectionControls(container);
		
		createFormatEditor(container);
		createDirectoryEditor(container);
		createFilenameEditor(container);
		createOpenFileInEditorEditor(container);
	}

	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

	private void createFormatEditor(Composite container) {
	
		String[][] labelsAndValues = getFormatLabelsAndValues();
	
		RadioGroupFieldEditor formatEditor = new RadioGroupFieldEditor("", "Export file &format:", 1,
				labelsAndValues, container) {
			@Override
			public int getNumberOfControls() {
				return 3;
			}
		};
	
		formatEditor.setPropertyChangeListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				format = Format.valueOf((String) event.getNewValue());
				updateControls();
			}
		});
	}

	private String[][] getFormatLabelsAndValues() {
		String labelsAndValues[][] = new String[Format.values().length][2];
		
		int i = 0;
		for (Format format : Format.values()) {
			labelsAndValues[i++] = new String[] { format.toString(), format.name() };
		}
		return labelsAndValues;
	}

	@Override
	public void setVisible(boolean visible) {
		if ( visible ) {
			updateControls();
		}
		super.setVisible(visible);
	}

	private void createDirectoryEditor(Composite container) {
		
		DirectoryFieldEditor exportFileEditor = new DirectoryFieldEditor("", "Export path:", container);
		exportFileEditor.setEmptyStringAllowed(false);
		exportFileEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				checkValidInput(event);
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					exportPath = (String) event.getNewValue();
					System.out.println( event.getNewValue().toString() );
					updateControls();
				}
			}} 
		);
		
		exportFileEditor.getErrorMessage();
	}

	private void createFilenameEditor(Composite container) {
		StringFieldEditor editor = new StringFieldEditor("", "Export Filename:",container ) {
			@Override
			public int getNumberOfControls() {
				return 3;
			}
		};

		editor.setEmptyStringAllowed(false);
		editor.setPropertyChangeListener( new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				checkValidInput(event);
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					filename = (String) event.getNewValue();
					updateControls();
				}
			}
		});
	}

	protected void updateControls() {
		setPageComplete( format != null && filename != null  && exportPath != null);
	}

	private void checkValidInput(PropertyChangeEvent event) {
		if ( event.getProperty().equals( FieldEditor.IS_VALID ) ) {
			if ( (Boolean) event.getNewValue() ) {
				setErrorMessage(null);
			} else {
				setErrorMessage( ((StringFieldEditor) event.getSource()).getErrorMessage() );
			}					
			updateControls();
		}
	}

	private void createOpenFileInEditorEditor(Composite container) {
		
		BooleanFieldEditor editor = new BooleanFieldEditor("","Open File in Editor", container ) {
			@Override
			public int getNumberOfControls() {
				return 3;
			}
		};
		openFileInEditor = editor.getBooleanValue();
		editor.setPropertyChangeListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				openFileInEditor = (Boolean) event.getNewValue();
			}
		});
	}

	public Format getFormat() {
		return format;
	}

	public String getFilename() {
		return filename;
	}

	public String getExportPath() {
		return exportPath;
	}

	public boolean isOpenFileInEditor() {
		return openFileInEditor;
	}

}
