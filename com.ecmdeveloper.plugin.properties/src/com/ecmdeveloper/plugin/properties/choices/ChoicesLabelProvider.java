/**
 * Copyright 2010, Ricardo Belfor
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
package com.ecmdeveloper.plugin.properties.choices;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.core.model.ChoicePlaceholder;
import com.ecmdeveloper.plugin.core.model.IChoice;
import com.ecmdeveloper.plugin.properties.Activator;
import com.ecmdeveloper.plugin.properties.util.IconFiles;

public class ChoicesLabelProvider extends LabelProvider {

	private static final String CHOICE_VALUE_POSTFIX = ")";
	private static final String CHOICE_VALUE_PREFIX = " (";

	@Override
	public Image getImage(Object element) {
		IChoice choice = (IChoice) element;
		if ( choice instanceof ChoicePlaceholder ) {
			return Activator.getImage( IconFiles.CHOICES_PLACEHOLDER );
		}
		if ( ! choice.isSelectable() ) {
			return Activator.getImage( IconFiles.CHOICES_FOLDER );
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		IChoice choice = (IChoice) element;
		if ( choice.isSelectable() ) {
			return getSelectableChoiceText(choice);
		} else {
			return choice.getDisplayName();
		}
	}

	private String getSelectableChoiceText(IChoice choice) {
		if ( choice.getValue().toString().equals( choice.getDisplayName() ) ) {
			return choice.getDisplayName();
		} else {
			StringBuffer text = getDisplayNameAndValue(choice);
			return text.toString();
		}
	}

	private StringBuffer getDisplayNameAndValue(IChoice choice) {
		StringBuffer text = new StringBuffer();
		String displayName = choice.getDisplayName();
		if ( displayName != null && !displayName.isEmpty() ) {
			text.append( displayName );
			text.append( CHOICE_VALUE_PREFIX );
			text.append( choice.getValue().toString() );
			text.append( CHOICE_VALUE_POSTFIX );
		} else {
			if (choice.getValue() instanceof Object[] ) {
				getArrayValue(text, choice);
			} else {
				text.append( choice.getValue().toString() );
			}
		}
		return text;
	}

	private void getArrayValue(StringBuffer text, IChoice choice) {
		String concat = "";
		for ( Object v : (Object[]) choice.getValue() ) {
			text.append( concat );
			text.append(v.toString());
			concat = ", ";
		}
	}
}
