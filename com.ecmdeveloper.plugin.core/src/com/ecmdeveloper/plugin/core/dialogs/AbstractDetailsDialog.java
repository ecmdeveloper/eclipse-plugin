/**
 * Code taken from the book 'Eclipse Plug-ins, Third Edition' by Eric Clayberg and 
 * Dan Rubel. See www.qualityeclipse.com.
 */
package com.ecmdeveloper.plugin.core.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * An abstract dialog with a details section that can be shown or hidden by the
 * user. Subclasses are responsible for providing the content of the details
 * section.
 */
public abstract class AbstractDetailsDialog extends Dialog
{
   private final String title;
   private final String message;
   private final Image image;

   private Button detailsButton;
   private Control detailsArea;
   private Point cachedWindowSize;

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
    */
   public AbstractDetailsDialog(Shell parentShell, String title, Image image,
         String message) {
      this(new SameShellProvider(parentShell), title, image, message);
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
    */
   public AbstractDetailsDialog(IShellProvider parentShell, String title, Image image,
         String message) {
      super(parentShell);

      this.title = title;
      this.image = image;
      this.message = message;

      setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
   }

   /**
    * Configures the given shell in preparation for opening this window in it.
    * In our case, we set the title if one was provided.
    */
   protected void configureShell(Shell shell) {
      super.configureShell(shell);
      if (title != null)
         shell.setText(title);
   }

   /**
    * Creates and returns the contents of the upper part of this dialog (above
    * the button bar). This includes an image, if specified, and a message.
    * 
    * @param parent
    *           the parent composite to contain the dialog area
    * @return the dialog area control
    */
   protected Control createDialogArea(Composite parent) {
      Composite composite = (Composite) super.createDialogArea(parent);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      if (image != null) {
         ((GridLayout) composite.getLayout()).numColumns = 2;
         Label label = new Label(composite, 0);
         image.setBackground(label.getBackground());
         label.setImage(image);
         label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
               | GridData.VERTICAL_ALIGN_BEGINNING));
      }

      Label label = new Label(composite, SWT.WRAP);
      if (message != null)
         label.setText(message);
      GridData data =
            new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
      data.widthHint =
            convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
      label.setLayoutData(data);
      label.setFont(parent.getFont());

      return composite;
   }

   /**
    * Adds OK and Details buttons to this dialog's button bar.
    * 
    * @param parent
    *           the button bar composite
    */
   protected void createButtonsForButtonBar(Composite parent) {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
      detailsButton =
            createButton(parent, IDialogConstants.DETAILS_ID,
                  IDialogConstants.SHOW_DETAILS_LABEL, false);
   }

   /**
    * The buttonPressed() method is called when either the OK or Details buttons
    * is pressed. We override this method to alternately show or hide the
    * details area if the Details button is pressed.
    */
   protected void buttonPressed(int id) {
      if (id == IDialogConstants.DETAILS_ID)
         toggleDetailsArea();
      else
         super.buttonPressed(id);
   }

   /**
    * Toggles the unfolding of the details area. This is triggered by the user
    * pressing the Details button.
    */
   protected void toggleDetailsArea() {
      Point oldWindowSize = getShell().getSize();
      Point newWindowSize = cachedWindowSize;
      cachedWindowSize = oldWindowSize;

      // Show the details area.
      if (detailsArea == null) {
         detailsArea = createDetailsArea((Composite) getContents());
         detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
      }

      // Hide the details area.
      else {
         detailsArea.dispose();
         detailsArea = null;
         detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
      }

      /*
       * Must be sure to call getContents().computeSize(SWT.DEFAULT,
       * SWT.DEFAULT) before calling getShell().setSize(newWindowSize) since
       * controls have been added or removed.
       */

      // Compute the new window size.
      Point oldSize = getContents().getSize();
      Point newSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
      if (newWindowSize == null)
         newWindowSize =
               new Point(oldWindowSize.x, oldWindowSize.y + (newSize.y - oldSize.y));

      // Crop new window size to screen.
      Point windowLoc = getShell().getLocation();
      Rectangle screenArea = getContents().getDisplay().getClientArea();
      if (newWindowSize.y > screenArea.height - (windowLoc.y - screenArea.y))
         newWindowSize.y = screenArea.height - (windowLoc.y - screenArea.y);

      getShell().setSize(newWindowSize);
      ((Composite) getContents()).layout();
   }

   /**
    * subclasses must implement createDetailsArea to provide content for the
    * area of the dialog made visible when the Details button is clicked.
    * 
    * @param parent
    *           the details area parent
    * @return the details area
    */
   protected abstract Control createDetailsArea(Composite parent);
}
