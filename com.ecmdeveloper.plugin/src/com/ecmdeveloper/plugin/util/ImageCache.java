/**
 * Copyright 2009, Ricardo Belfor
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
package com.ecmdeveloper.plugin.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class ImageCache
{
   private final Map<ImageDescriptor, Image> imageMap =
         new HashMap<ImageDescriptor, Image>();

   public Image getImage(ImageDescriptor imageDescriptor) {
      if (imageDescriptor == null)
         return null;
      Image image = (Image) imageMap.get(imageDescriptor);
      if (image == null) {
         image = imageDescriptor.createImage();
         imageMap.put(imageDescriptor, image);
      }
      return image;
   }

   public void dispose() {
      Iterator<Image> iter = imageMap.values().iterator();
      while (iter.hasNext())
         iter.next().dispose();
      imageMap.clear();
   }
}
