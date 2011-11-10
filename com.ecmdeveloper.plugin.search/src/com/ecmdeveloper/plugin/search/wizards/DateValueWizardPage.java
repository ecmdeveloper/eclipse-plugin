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

package com.ecmdeveloper.plugin.search.wizards;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

/**
 * @author ricardo.belfor
 *
 */
public class DateValueWizardPage extends ValueWizardPage {

	private static final String TITLE = "Date value";
	private static final String DESCRIPTION = "Enter a date value.";

	private DateTime calendar;
	private DateTime time;
	private Button omitTimeButton;
	private ComboViewer timeZonesCombo;

	protected DateValueWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}
	
	@Override
	protected void createInput(Composite container) {

		createLabel(container, "Date:" );
		createCalendar(container);
		createLabel(container, "Time:" );
		createTime(container);
		createLabel(container, "Time Zone:" );
		createTimeZones(container);
		createOmitTimeButton(container);
		
		if ( getValue() instanceof Calendar ) {
			setCalendarValue((Calendar) getValue());
		} else {
			setValue(getCalendarValue());
			setDirty();
		}
	}

	private void createOmitTimeButton(Composite container) {
		omitTimeButton = new Button(container, SWT.CHECK);
		omitTimeButton.setText("Omit time from query (00:00:00 is assumed)");
		omitTimeButton.setLayoutData( getFullRowGridData() );
		omitTimeButton.setSelection( isOmitTime() );
		omitTimeButton.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.getSource();
				boolean ommitTime = b.getSelection();
				time.setEnabled(!ommitTime);
				if (ommitTime) {
					time.setTime(0,0,0);
				}
				setValue( getCalendarValue() );
				setDirty();
			}} );
	}

	private void createLabel(Composite container, String text) {
		Label  label = new Label(container, SWT.NONE);
		label.setText( text );
	}

	private void createTime(Composite client) {
		time = new DateTime (client, SWT.TIME);
		time.setEnabled( !isOmitTime() );
		time.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				setValue( getCalendarValue() );
				setDirty();
			}
		});
	}

	private void createCalendar(Composite client) {
		calendar = new DateTime (client, SWT.CALENDAR );
		calendar.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				setValue( getCalendarValue() );
				setDirty();
			}
		});
	}

	private void setCalendarValue(Calendar calendarValue) {
		if ( calendarValue != null ) {
			calendar.setDate(calendarValue.get(Calendar.YEAR), calendarValue.get(Calendar.MONTH),
					calendarValue.get(Calendar.DAY_OF_MONTH));
			int hourOfDay = calendarValue.get(Calendar.HOUR_OF_DAY);
			int minutes = calendarValue.get(Calendar.MINUTE);
			int seconds = calendarValue.get(Calendar.SECOND);
			omitTimeButton.setSelection( hourOfDay == 0 && minutes == 0 && seconds == 0 );
			time.setTime(hourOfDay, minutes,seconds );
			setTimeZone( calendarValue.getTimeZone() );
		}
	}

	private boolean isOmitTime() {
		
		Object value = getValue();
		if ( value != null && value instanceof Calendar) {
			Calendar calendarValue = (Calendar) value;
			int hourOfDay = calendarValue.get(Calendar.HOUR_OF_DAY);
			int minutes = calendarValue.get(Calendar.MINUTE);
			int seconds = calendarValue.get(Calendar.SECOND);
	
			return hourOfDay == 0 && minutes == 0 && seconds == 0;
		}
		return false;
	}
	
	private Calendar getCalendarValue() {
	
		Calendar calendarValue = Calendar.getInstance();
	
		calendarValue.set(Calendar.YEAR, calendar.getYear() );
		calendarValue.set(Calendar.MONTH, calendar.getMonth() );
		calendarValue.set(Calendar.DAY_OF_MONTH, calendar.getDay() );
		calendarValue.set(Calendar.HOUR_OF_DAY, time.getHours() );
		calendarValue.set(Calendar.MINUTE, time.getMinutes() );
		calendarValue.set(Calendar.SECOND, time.getSeconds() );
		calendarValue.setTimeZone( getTimeZone() );
		return calendarValue;
	}

	private void createTimeZones(Composite parent) {
		
		timeZonesCombo = new ComboViewer(parent, SWT.VERTICAL
				| SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		timeZonesCombo.setContentProvider(new ArrayContentProvider());
		timeZonesCombo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				TimeZone timeZone = (TimeZone) element;
				return timeZone.getID();
			}} );

		timeZonesCombo.setInput( getTimeZones() );
		timeZonesCombo.addSelectionChangedListener( new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setValue( getCalendarValue() );
				setDirty();				
			}} );
		setTimeZone( TimeZone.getDefault() );
	}

	private void setTimeZone(TimeZone timeZone) {
		ISelection selection = new StructuredSelection( timeZone );
		timeZonesCombo.setSelection(selection );
	}

	private ArrayList<TimeZone> getTimeZones() {
		
		ArrayList<TimeZone> timeZoneList = new ArrayList<TimeZone>();
		for ( String id :  TimeZone.getAvailableIDs() ) {
			timeZoneList.add( TimeZone.getTimeZone(id) );
		}
		return timeZoneList;
	}

	private TimeZone getTimeZone() {
		IStructuredSelection selection = (IStructuredSelection) timeZonesCombo.getSelection();
		TimeZone timeZone = (TimeZone) selection.iterator().next();
		return timeZone;
	}
}
