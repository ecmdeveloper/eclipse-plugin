/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.search.figures;

import org.eclipse.swt.graphics.Color;

/**
 * 
 * @author ricardo.belfor
 *
 */
public interface QueryColorConstants {

	public final static Color andContainer = new Color(null, 220, 70, 80);
	public final static Color orContainer = new Color(null, 0, 134, 255);
	public final static Color xorGate = new Color(null, 240, 240, 40);
	public final static Color logicGreen = new Color(null, 123, 174, 148);
	public final static Color logicHighlight = new Color(null, 66, 166, 115);
	public final static Color connectorGreen = new Color(null, 0, 69, 40);
	public final static Color logicBackgroundBlue = new Color(null, 200, 200, 240);
	public final static Color ghostFillColor = new Color(null, 31, 31, 31);
	public final static Color containerLabelColor = new Color(null, 255, 255, 255);

}
