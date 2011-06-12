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
import java.util.Collection;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.ecmdeveloper.plugin.model.SearchResultRow;
import com.ecmdeveloper.plugin.search.ui.wizard.ExportFileWizardPage.Format;
import com.ecmdeveloper.plugin.search.util.CSVSearchResultExport;
import com.ecmdeveloper.plugin.search.util.SearchResultExport;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class ExportSearchResultWizard extends Wizard {

	private static final String TITLE = "Export Search Result";
	private final Collection<SearchResultRow> searchResult;
	private ExportFileWizardPage exportFileWizardPage;
	private CSVExportWizardPage csvExportWizardPage;

	public ExportSearchResultWizard(Collection<SearchResultRow> searchResult) {
		this.searchResult = searchResult;
		setWindowTitle(TITLE);
	}

	@Override
	public void addPages() {
		exportFileWizardPage = new ExportFileWizardPage();
		addPage(exportFileWizardPage);
		
		csvExportWizardPage = new CSVExportWizardPage();
		addPage(csvExportWizardPage);
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
		case XML:
			return false;
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
			case XML:
				return null;
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
				return true;
			} catch (IOException e) {
				PluginMessage.openError(getShell(), TITLE, e.getLocalizedMessage(), e );
				return false;
			}
		}
		// TODO Auto-generated method stub
		return false;
	}

	private SearchResultExport getSearchResultExporter() {

		Format format = exportFileWizardPage.getFormat();
		String filename = exportFileWizardPage.getExportPath() + File.pathSeparator + exportFileWizardPage.getFilename();

		switch (format ) {
		case CSV:
			return createCSVSearchResultExport(filename);
		case HTML:
		case XML:
			break;
		}
		return null;
	}

	private SearchResultExport createCSVSearchResultExport(String filename) {
		boolean writeHeader = csvExportWizardPage.isWriteHeader();
		char delimiter = csvExportWizardPage.getDelimiter();
		return new CSVSearchResultExport(searchResult, filename, writeHeader, delimiter );
	}
}
