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

package com.ecmdeveloper.plugin.search.handlers;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.model.SearchResultRow;
import com.ecmdeveloper.plugin.search.ui.QuerySearchResult;
import com.ecmdeveloper.plugin.search.ui.SearchResultPage;
import com.ecmdeveloper.plugin.search.ui.wizard.ExportSearchResultWizard;

/**
 * @author ricardo.belfor
 *
 */
public class ExportHandler extends AbstractHandler implements IHandler {

	private static final String EMPTY_SEARCH_RESULT_MESSAGE = "The search result contains no information that can be exported.";
	private static final String HANDLER_NAME = "Export Search Result";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		Collection<SearchResultRow> result = getSearchResult();
		if ( result == null || result.isEmpty() ) {
			MessageDialog.openError(window.getShell(), HANDLER_NAME, EMPTY_SEARCH_RESULT_MESSAGE );
		}

		ExportSearchResultWizard wizard = new ExportSearchResultWizard(result);
		WizardDialog dialog = new WizardDialog( window.getShell(), wizard);
		dialog.create();
		dialog.open();
		
		return null;
	}

	private Collection<SearchResultRow> getSearchResult() {
		ISearchResultViewPart searchResultView = NewSearchUI.getSearchResultView();
		ISearchResultPage activePage = searchResultView.getActivePage();

		Collection<SearchResultRow> result = null;
		if ( activePage instanceof SearchResultPage ) {
			SearchResultPage searchResultPage = (SearchResultPage)activePage;
			QuerySearchResult searchResult = (QuerySearchResult) searchResultPage.getSearchResult();
			
			result = searchResult.getResult();
		}
		return result;
	}

}
