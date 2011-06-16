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

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.model.SearchResultRow;
import com.ecmdeveloper.plugin.search.ui.wizard.ExportFileWizardPage.Format;
import com.ecmdeveloper.plugin.search.util.CSVSearchResultExport;
import com.ecmdeveloper.plugin.search.util.HTMLSearchResultExport;
import com.ecmdeveloper.plugin.search.util.SearchResultExport;
import com.ecmdeveloper.plugin.search.util.XMLSearchResultExport;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class ExportSearchResultWizard extends Wizard {

	private static final String TITLE = "Export Search Result";
	private final Collection<SearchResultRow> searchResult;
	private final String label;
	private final IWorkbench workbench;

	private ExportFileWizardPage exportFileWizardPage;
	private CSVExportWizardPage csvExportWizardPage;
	private HTMLExportWizardPage htmlExportWizardPage;
	private XMLExportWizardPage xmlExportWizardPage;

	public ExportSearchResultWizard(Collection<SearchResultRow> searchResult, String label, IWorkbench workbench) {
		this.searchResult = searchResult;
		this.label = label;
		this.workbench = workbench;
		setWindowTitle(TITLE);
	}

	@Override
	public void addPages() {
		exportFileWizardPage = new ExportFileWizardPage(label);
		addPage(exportFileWizardPage);
		
		csvExportWizardPage = new CSVExportWizardPage();
		addPage(csvExportWizardPage);
		
		htmlExportWizardPage = new HTMLExportWizardPage();
		addPage(htmlExportWizardPage);
		
		xmlExportWizardPage = new XMLExportWizardPage();
		addPage(xmlExportWizardPage);
	}

	@Override
	public boolean canFinish() {
		if ( !exportFileWizardPage.isPageComplete() ) {
			return false;
		}

		Format format = exportFileWizardPage.getFormat();

		switch (format ) {
		case CSV:
			return csvExportWizardPage.isPageComplete();
		case HTML:
			return htmlExportWizardPage.isPageComplete();
		case XML:
			return xmlExportWizardPage.isPageComplete();
		}
		return false;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {

		if ( page instanceof ExportFileWizardPage ) {
			
			Format format = exportFileWizardPage.getFormat();
			switch (format ) {
			case CSV:
				return csvExportWizardPage;
			case HTML:
				return htmlExportWizardPage;
			case XML:
				return xmlExportWizardPage;
			}
		}
		return null;
	}

	@Override
	public boolean performFinish() {

		SearchResultExport searchResultExport = getSearchResultExporter();
		
		if ( searchResultExport != null) {
			try {
				searchResultExport.export();
				if ( exportFileWizardPage.isOpenFileInEditor() ) {
					openFileInEditor( searchResultExport.getFilename() );
				}
				return true;
			} catch (IOException e) {
				PluginMessage.openError(getShell(), TITLE, e.getLocalizedMessage(), e );
				return false;
			}
		}
		return false;
	}

	private void openFileInEditor(String filename) {
		try {
			IFileStore store = EFS.getLocalFileSystem().getStore( new Path( filename ) );
			IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
			IDE.openEditorOnFileStore( activePage, store );
		} catch (PartInitException e) {
			PluginMessage.openError( getShell(), TITLE, e.getLocalizedMessage(), e);
		}
	}

	private SearchResultExport getSearchResultExporter() {

		Format format = exportFileWizardPage.getFormat();
		String filename = exportFileWizardPage.getExportPath() + File.separator + exportFileWizardPage.getFilename();

		switch (format ) {
		case CSV:
			return createCSVSearchResultExport(filename);
		case HTML:
			return createHTMLSearchResultExport(filename);
		case XML:
			return createXMLSearchResultExport(filename);
		}
		return null;
	}

	private SearchResultExport createCSVSearchResultExport(String filename) {
		boolean writeHeader = csvExportWizardPage.isWriteHeader();
		char delimiter = csvExportWizardPage.getDelimiter();
		return new CSVSearchResultExport(searchResult, filename, writeHeader, delimiter );
	}

	private SearchResultExport createHTMLSearchResultExport(String filename) {
		boolean writeHeader = htmlExportWizardPage.isWriteHeader();
		return new HTMLSearchResultExport(searchResult, filename, writeHeader );
	}

	private SearchResultExport createXMLSearchResultExport(String filename) {
		String rowsTag = xmlExportWizardPage.getRowsTag();
		String rowTag = xmlExportWizardPage.getRowTag();
		return new XMLSearchResultExport(searchResult, filename, rowsTag, rowTag );
	}
}
