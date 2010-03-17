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

package com.ecmdeveloper.plugin.diagrams.actions;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class ExportAction extends SelectionAction {

	private static final String PNG_EXTENSION = "png";
	private static final String BMP_EXTENSION = "bmp";
	private static final String JPG_EXTENSION = "jpg";
	private static final String JPEG_EXTENSION = "jpeg";

	public ExportAction(IWorkbenchPart part) {
		super(part);
	}

	protected int getFormatFromFilename(String filename ) {
		
		String lowerCaseFilename = filename.toLowerCase();
		if (lowerCaseFilename.toLowerCase().endsWith(JPEG_EXTENSION)
				|| lowerCaseFilename.toLowerCase().endsWith(JPG_EXTENSION)) {
			return SWT.IMAGE_JPEG;
		} else if ( lowerCaseFilename.toLowerCase().endsWith( BMP_EXTENSION) ) {
			return SWT.IMAGE_BMP;
		} else if ( lowerCaseFilename.toLowerCase().endsWith( PNG_EXTENSION) ) {
			return SWT.IMAGE_PNG;
		}
		
		return SWT.IMAGE_JPEG;
	}
	
	protected String getOutputFilename() {
		Shell shell = getWorkbenchPart().getSite().getShell();
		
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		String[] filterExtensions = new String[] { "*." + JPEG_EXTENSION, "*." + JPG_EXTENSION,
				"*." + BMP_EXTENSION, "*." + PNG_EXTENSION };
		fileDialog.setFilterExtensions(filterExtensions);		
		
		return fileDialog.open();
	}

	@Override
	public void run() {

		Shell shell = getWorkbenchPart().getSite().getShell();

		try {
			String outputFilename = getOutputFilename();
			if ( outputFilename == null ) {
				return;
			}
	
			exportDiagram(outputFilename);
			MessageDialog.openInformation( shell, getText(), "Image exported to '" + outputFilename + "'" );
		} catch ( Exception e ) {
			MessageDialog.openError(shell, getText(), e.getLocalizedMessage() );
		}
	}

	protected void saveImage(String outputFilename, Image img) {
		ImageData[] imgData = new ImageData[1];
		imgData[0] = img.getImageData();
		ImageLoader imgLoader = new ImageLoader();
		imgLoader.data = imgData;
		imgLoader.save(outputFilename, getFormatFromFilename( outputFilename) );
	}

	protected void exportDiagram(String outputFilename) {

		IFigure figure = getFigure();
		Rectangle figureBounds = figure.getBounds();
		
		Image image = null;
		GC imageGC = null;

		try {
			image = new Image(null, figureBounds.width, figureBounds.height);
			imageGC = new GC(image);
			Graphics imgGraphics = new SWTGraphics(imageGC);
			imgGraphics.translate( -figureBounds.x, -figureBounds.y );
			figure.paint(imgGraphics);
			
			saveImage(outputFilename, image);
		} finally {
			if ( imageGC != null) {
				imageGC.dispose();
			}
		
			if ( image != null ) {
				image.dispose();
			}
		}
	}

	protected abstract IFigure getFigure();
}
