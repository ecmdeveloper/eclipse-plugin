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

package com.ecmdeveloper.plugin.content.compare;

import java.text.MessageFormat;

import com.ecmdeveloper.plugin.content.constants.PropertyNames;
import com.ecmdeveloper.plugin.core.model.IDocument;

/**
 * @author Ricardo.Belfor
 *
 */
public class DocumentVersionCompareItem extends DocumentCompareItem {

	private static final String VERSION_NAME_FORMAT = "Version {1}.{2} ({0})";

	public DocumentVersionCompareItem(IDocument document, int contentIndex ) {
		super(document, contentIndex);
	}

	@Override
	public String getName() {
		String name = document.getName();
		Object majorVersionNumber = document.getValue( PropertyNames.MAJOR_VERSION_NUMBER );
		Object minorVersionNumber = document.getValue( PropertyNames.MINOR_VERSION_NUMBER );
		return MessageFormat.format( VERSION_NAME_FORMAT, name, majorVersionNumber, minorVersionNumber );
	}

	@Override
	public long getModificationDate() {
		Integer majorVersionNumber = (Integer) document.getValue( PropertyNames.MAJOR_VERSION_NUMBER );
		Integer minorVersionNumber = (Integer) document.getValue( PropertyNames.MINOR_VERSION_NUMBER);
		return majorVersionNumber.longValue() * 10 + minorVersionNumber.longValue();
	}
}
