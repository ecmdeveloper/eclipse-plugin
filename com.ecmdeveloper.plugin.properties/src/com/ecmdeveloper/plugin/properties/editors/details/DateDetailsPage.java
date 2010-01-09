/**
 * Copyright 2009,2010, Ricardo Belfor
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class DateDetailsPage extends BaseDetailsPage {

	private DateTime calendar;
	private DateTime time;

	@Override
	protected void createClientContent(Composite client) {

		super.createClientContent(client);
		
		FormToolkit toolkit = form.getToolkit();

		toolkit.createLabel(client, "Date:");
		createCalendar(client);
		toolkit.createLabel(client, "Time:");
		createTime(client);
	}

	private void createTime(Composite client) {
		time = new DateTime (client, SWT.TIME);
		time.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				setDirty(true);
			}
		});
	}

	private void createCalendar(Composite client) {
		calendar = new DateTime (client, SWT.CALENDAR );
		calendar.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				setDirty(true);
			}
		});
	}

	protected void handleEmptyValueButton(boolean selected) {
		calendar.setEnabled( !selected );
		time.setEnabled( !selected );
	}

	@Override
	protected int getNumClientColumns() {
		return 2;
	}

	@Override
	protected void propertyChanged(Property property) {
		setValue( (Date) property.getValue() );
	}

	private void setValue(Date value) {
		if ( value != null ) {
			emptyValueButton.setSelection(false);
			Calendar calendarValue = Calendar.getInstance();
			calendarValue.setTime(value);
			System.out.println( calendarValue.get(Calendar.DAY_OF_MONTH) );
			calendar.setDate(calendarValue.get(Calendar.YEAR), calendarValue.get(Calendar.MONTH),
					calendarValue.get(Calendar.DAY_OF_MONTH));
			time.setTime(calendarValue.get(Calendar.HOUR), calendarValue.get(Calendar.MINUTE),
					calendarValue.get(Calendar.SECOND) );
		} else {
			emptyValueButton.setSelection(true);
		}
	}

	@Override
	protected Object getValue() {
	
		Calendar calendarValue = Calendar.getInstance();
	
		calendarValue.set(Calendar.YEAR, calendar.getYear() );
		calendarValue.set(Calendar.MONTH, calendar.getMonth() );
		calendarValue.set(Calendar.DAY_OF_MONTH, calendar.getDay() );
		calendarValue.set(Calendar.HOUR, time.getHours() );
		calendarValue.set(Calendar.MINUTE, time.getMinutes() );
		calendarValue.set(Calendar.SECOND, time.getSeconds() );
		
		return calendarValue.getTime();
	}

	@Override
	public void dispose() {
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
		calendar.setFocus();
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}
}
