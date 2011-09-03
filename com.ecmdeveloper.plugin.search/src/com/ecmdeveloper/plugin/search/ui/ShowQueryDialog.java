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

package com.ecmdeveloper.plugin.search.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author ricardo.belfor
 *
 */
public class ShowQueryDialog extends Dialog {

	private static final String[] KEYWORDS = { "SELECT", "OPTIONS", "BATCHSIZE", "TIMELIMIT",
			"FULLTEXTROWLIMIT", "DISTINCT", "ALL", "TOP", "FROM", "WHERE", "ORDER BY", "AS",
			"INNER", "LEFT", "OUTER", "RIGHT", "FULL", "JOIN", "ON", "WITH", "INCLUDESUBCLASSES",
			"EXCLUDESUBCLASSES", "AND", "OR", "NOT", "LIKE", "IS", "NOT", "NULL", "IN", "EXISTS", "ASC", "DESC" };
	private static final String[] FUNCTIONS = { "INFOLDER", "INSUBFOLDER", "ISCLASS", "CONTAINS",
			"FREETEXT", "UPPER", "LOWER", "ABS" };

	private final String sql;

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Query SQL");
	}

	public ShowQueryDialog(Shell parentShell, String sql) {
		super(parentShell);
		this.sql = sql;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Control control = super.createDialogArea(parent);

		StyledText text = createStyledText(control);
		text.setText( sql );
		
		setKeywordStyleRange(text, sql, KEYWORDS, getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE ) );	
		setKeywordStyleRange(text, sql, FUNCTIONS, getShell().getDisplay().getSystemColor(SWT.COLOR_MAGENTA ) );	
		setStringStyleRange(text, sql);

		return control;
	}

	private StyledText createStyledText(Control control) {
		StyledText text = new StyledText((Composite) control, SWT.BORDER | SWT.V_SCROLL);
	    text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.setWordWrap(true);
		text.setEditable(false);
		return text;
	}

	private void setKeywordStyleRange(StyledText text, String sql, String[] keywords, Color color) {
		
		for (String keyword : keywords ) {
			int index = sql.indexOf(keyword, 0);
			do {
			  if ( index >= 0 ) {
					StyleRange keywordStyle = new StyleRange();
					keywordStyle.start = index;
					keywordStyle.length = keyword.length();
					keywordStyle.foreground = color;
					text.setStyleRange(keywordStyle);
			  }
			  index = sql.indexOf(keyword, index + keyword.length() );
			} while (index >= 0 );
		}
	}

	private void setStringStyleRange(StyledText text, String sql) {
		
		int firstIndex = sql.indexOf("'" );
		int secondIndex = -1;
		if ( firstIndex >= 0 ) {
			secondIndex = sql.indexOf("'", firstIndex + 1);
		}

		do {
			if ( secondIndex > 0 ) {
					StyleRange stringStyle = new StyleRange();
					stringStyle.start = firstIndex;
					stringStyle.length = secondIndex - firstIndex + 1;
					stringStyle.foreground = getShell().getDisplay().getSystemColor(SWT.COLOR_RED );
					text.setStyleRange(stringStyle);
			  }
			firstIndex = sql.indexOf("'", secondIndex + 1);
			if ( firstIndex > 0 ) {
				secondIndex = sql.indexOf("'", firstIndex + 1);
			}
			
		} while ( firstIndex >  0 && secondIndex > 0 );
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}
}
