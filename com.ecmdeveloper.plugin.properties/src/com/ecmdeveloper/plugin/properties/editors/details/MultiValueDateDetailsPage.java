/**
 * Copyright 2010, Ricardo Belfor
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

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ricardo.Belfor
 *
 */
public class MultiValueDateDetailsPage extends BaseMultiValueDetailsPage {

	private DateTime calendar;
	private DateTime time;

	@Override
	protected void createInput(Composite client, FormToolkit toolkit) {
		Composite dateTimeComposite = createParentComposite(client, toolkit);
		toolkit.createLabel(dateTimeComposite, "Date: " );
		calendar = new DateTime( dateTimeComposite, SWT.DATE);
		toolkit.createLabel(dateTimeComposite, "Time: " );
		time = new DateTime( dateTimeComposite, SWT.TIME);
	}

	private Composite createParentComposite(Composite client, FormToolkit toolkit) {
		Composite dateTimeComposite = toolkit.createComposite(client);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		dateTimeComposite.setLayout(layout);
		return dateTimeComposite;
	}

	@Override
	protected Object getInputValue() {
		Calendar calendarValue = Calendar.getInstance();
		
		calendarValue.set(Calendar.YEAR, calendar.getYear() );
		calendarValue.set(Calendar.MONTH, calendar.getMonth() );
		calendarValue.set(Calendar.DAY_OF_MONTH, calendar.getDay() );
		calendarValue.set(Calendar.HOUR, time.getHours() );
		calendarValue.set(Calendar.MINUTE, time.getMinutes() );
		calendarValue.set(Calendar.SECOND, time.getSeconds() );
		
		return calendarValue.getTime();
	}

	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.properties.editors.details.BaseMultiValueDetailsPage#setInputValue(java.lang.Object)
	 */
	@Override
	protected void setInputValue(Object value) {

		if ( value != null ) {
			Calendar calendarValue = Calendar.getInstance();
			calendarValue.setTime((Date) value);
	
			calendar.setDate(calendarValue.get(Calendar.YEAR), calendarValue.get(Calendar.MONTH),
					calendarValue.get(Calendar.DAY_OF_MONTH));
			time.setTime(calendarValue.get(Calendar.HOUR), calendarValue.get(Calendar.MINUTE),
					calendarValue.get(Calendar.SECOND) );
		}
	}

	@Override
	protected void handleEmptyValueButton(boolean selected) {
		super.handleEmptyValueButton(selected);
		calendar.setEnabled( !selected );
		time.setEnabled( !selected );
	}
}
