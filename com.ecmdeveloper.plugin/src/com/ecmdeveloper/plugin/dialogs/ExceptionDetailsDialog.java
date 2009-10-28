/**
 * Code taken from the book 'Eclipse Plug-ins, Third Edition' by Eric Clayberg and 
 * Dan Rubel. See www.qualityeclipse.com.
 */
package com.ecmdeveloper.plugin.dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Dictionary;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

/**
 * A dialog to display one or more errors to the user, as contained in an
 * <code>IStatus</code> object along with the plug-in identifier, name, version
 * and provider. If an error contains additional detailed information then a
 * Details button is automatically supplied, which shows or hides an error
 * details viewer when pressed by the user.
 * 
 * @see org.eclipse.core.runtime.IStatus
 */
public class ExceptionDetailsDialog extends AbstractDetailsDialog
{
   /**
    * The details to be shown ({@link Exception}, {@link IStatus}, or
    * <code>null</code> if no details).
    */
   private final Object details;

   /**
    * The bundle triggering this deatils dialog and whose information is to be
    * shown in the details area or <code>null</code> if no bundle details should
    * be shown.
    */
   private final Bundle bundle;

   /**
    * Construct a new instance with the specified elements. Note that the window
    * will have no visual representation (no widgets) until it is told to open.
    * By default, <code>open</code> blocks for dialogs.
    * 
    * @param parentShell
    *           the parent shell, or <code>null</code> to create a top-level
    *           shell
    * @param title
    *           the title for the dialog or <code>null</code> for none
    * @param image
    *           the image to be displayed
    * @param message
    *           the message to be displayed
    * @param details
    *           an object whose content is to be displayed in the details area,
    *           or <code>null</code> for none
    * @param plugin
    *           The plugin triggering this deatils dialog and whose information
    *           is to be shown in the details area or <code>null</code> if no
    *           plugin details should be shown.
    */
   public ExceptionDetailsDialog(Shell parentShell, String title, Image image,
         String message, Object details, Bundle bundle) {
      this(new SameShellProvider(parentShell), title, image, message, details, bundle);
   }

   /**
    * Construct a new instance with the specified elements. Note that the window
    * will have no visual representation (no widgets) until it is told to open.
    * By default, <code>open</code> blocks for dialogs.
    * 
    * @param parentShell
    *           the parent shell provider (not <code>null</code>)
    * @param title
    *           the title for the dialog or <code>null</code> for none
    * @param image
    *           the image to be displayed
    * @param message
    *           the message to be displayed
    * @param details
    *           an object whose content is to be displayed in the details area,
    *           or <code>null</code> for none
    * @param plugin
    *           The plugin triggering this deatils dialog and whose information
    *           is to be shown in the details area or <code>null</code> if no
    *           plugin details should be shown.
    */
   public ExceptionDetailsDialog(IShellProvider parentShell, String title, Image image,
         String message, Object details, Bundle bundle) {
      super(parentShell, getTitle(title, details), getImage(image, details), getMessage(
            message, details));

      this.details = details;
      this.bundle = bundle;
   }

   /**
    * Build content for the area of the dialog made visible when the Details
    * button is clicked.
    * 
    * @param parent
    *           the details area parent
    * @return the details area
    */
   protected Control createDetailsArea(Composite parent) {

      // Create the details area.
      Composite panel = new Composite(parent, SWT.NONE);
      panel.setLayoutData(new GridData(GridData.FILL_BOTH));
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      panel.setLayout(layout);

      // Create the details content.
      createProductInfoArea(panel);
      createDetailsViewer(panel);

      return panel;
   }

   /**
    * Create fields displaying the plugin information such as name, identifer,
    * version and vendor. Do nothing if the plugin is not specified.
    * 
    * @param parent
    *           the details area in which the fields are created
    * @return the product info composite or <code>null</code> if no plugin
    *         specified.
    */
   protected Composite createProductInfoArea(Composite parent) {

      // If no bundle specified, then nothing to display here
      if (bundle == null)
         return null;

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayoutData(new GridData());
      GridLayout layout = new GridLayout();
      layout.numColumns = 2;
      layout.marginWidth =
            convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
      composite.setLayout(layout);

      Dictionary<?, ?> bundleHeaders = bundle.getHeaders();
      String pluginId = bundle.getSymbolicName();
      String pluginVendor = (String) bundleHeaders.get("Bundle-Vendor");
      String pluginName = (String) bundleHeaders.get("Bundle-Name");
      String pluginVersion = (String) bundleHeaders.get("Bundle-Version");

      new Label(composite, SWT.NONE).setText("Provider:");
      new Label(composite, SWT.NONE).setText(pluginVendor);
      new Label(composite, SWT.NONE).setText("Plug-in Name:");
      new Label(composite, SWT.NONE).setText(pluginName);
      new Label(composite, SWT.NONE).setText("Plug-in ID:");
      new Label(composite, SWT.NONE).setText(pluginId);
      new Label(composite, SWT.NONE).setText("Version:");
      new Label(composite, SWT.NONE).setText(pluginVersion);

      return composite;
   }

   /**
    * Create the details field based upon the details object. Do nothing if the
    * details object is not specified.
    * 
    * @param parent
    *           the details area in which the fields are created
    * @return the details field
    */
   protected Control createDetailsViewer(Composite parent) {
      if (details == null)
         return null;

      Text text =
            new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL
                  | SWT.V_SCROLL);
      text.setLayoutData(new GridData(GridData.FILL_BOTH));

      // Create the content.
      StringWriter writer = new StringWriter(1000);
      if (details instanceof Throwable)
         appendException(new PrintWriter(writer), (Throwable) details);
      else if (details instanceof IStatus)
         appendStatus(new PrintWriter(writer), (IStatus) details, 0);
      text.setText(writer.toString());

      return text;
   }

   // //////////////////////////////////////////////////////////////////////////
   //
   // Utility methods for building content
   //
   // //////////////////////////////////////////////////////////////////////////

   /**
    * Answer the title based on the provided title and details object.
    */
   public static String getTitle(String title, Object details) {
      if (title != null)
         return title;
      if (details instanceof Throwable) {
         Throwable e = (Throwable) details;
         while (e instanceof InvocationTargetException)
            e = ((InvocationTargetException) e).getTargetException();
         String name = e.getClass().getName();
         return name.substring(name.lastIndexOf('.') + 1);
      }
      return "Exception";
   }

   /**
    * Answer the image based on the provided image and details object.
    */
   public static Image getImage(Image image, Object details) {
      if (image != null)
         return image;
      Display display = Display.getCurrent();
      if ( display == null ) return null;
      if (details instanceof IStatus) {
         switch (((IStatus) details).getSeverity()) {
         case IStatus.ERROR:
            return display.getSystemImage(SWT.ICON_ERROR);
         case IStatus.WARNING:
            return display.getSystemImage(SWT.ICON_WARNING);
         case IStatus.INFO:
            return display.getSystemImage(SWT.ICON_INFORMATION);
         case IStatus.OK:
            return null;
         }
      }
      return display.getSystemImage(SWT.ICON_ERROR);
   }

   /**
    * Answer the message based on the provided message and details object.
    */
   public static String getMessage(String message, Object details) {
      if (details instanceof Throwable) {
         Throwable e = (Throwable) details;
         while (e instanceof InvocationTargetException)
            e = ((InvocationTargetException) e).getTargetException();
         if (message == null)
            return e.toString();
         return MessageFormat.format(message, new Object[] { e.toString() });
      }
      if (details instanceof IStatus) {
         String statusMessage = ((IStatus) details).getMessage();
         if (message == null)
            return statusMessage;
         return MessageFormat.format(message, new Object[] { statusMessage });
      }
      if (message != null)
         return message;
      return "An Exception occurred.";
   }

   public static void appendException(PrintWriter writer, Throwable ex) {
      if (ex instanceof CoreException) {
         appendStatus(writer, ((CoreException) ex).getStatus(), 0);
         writer.println();
      }
      appendStackTrace(writer, ex);
      if (ex instanceof InvocationTargetException)
         appendException(writer, ((InvocationTargetException) ex).getTargetException());
   }

   public static void appendStatus(PrintWriter writer, IStatus status, int nesting) {
      for (int i = 0; i < nesting; i++)
         writer.print("  ");
      writer.println(status.getMessage());
      IStatus[] children = status.getChildren();
      for (int i = 0; i < children.length; i++)
         appendStatus(writer, children[i], nesting + 1);
   }

   public static void appendStackTrace(PrintWriter writer, Throwable ex) {
      ex.printStackTrace(writer);
   }
}
