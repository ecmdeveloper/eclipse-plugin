/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.properties.editors.details;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;

import com.ecmdeveloper.plugin.editors.core.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class UnknownDetailsPage extends BaseDetailsPage {

	@Override
	protected void createClientContent(Composite client) {
		form.getToolkit().createLabel(client, "No details page for this property type yet", SWT.CENTER | SWT.WRAP );
	}

	@Override
	protected int getNumClientColumns() {
		return 1;
	}

	@Override
	protected void handleEmptyValueButton(boolean selected) {
	}

	@Override
	public void commit(boolean onSave) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	protected void propertyChanged(Property property) {
	}

	@Override
	protected Object getValue() {
		return null;
	}
}
