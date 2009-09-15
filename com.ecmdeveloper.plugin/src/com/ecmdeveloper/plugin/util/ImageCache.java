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
