package org.opensourcedea.gui.utils;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


public class Images {
	
	public static ImageRegistry getHelpImageRegistry(Display display) {
		ImageRegistry imgReg = new ImageRegistry(display);
		imgReg.put("help", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/help.png"));
		return imgReg;
	}
	
	/**
	 * Set the help icon with some tooltip text. This method can ONLY be used on a composite which has a FormLayout.
	 * @param comp the composite
	 * @param tooltipText the tooltip text to use.
	 */
	public static void setHelpIcon(Composite comp, String tooltipText, int offsetTop, int offsetRight) {
		Canvas helpCanvas = new Canvas(comp, SWT.NONE);
		FormData fdata = new FormData();
		fdata.top = new FormAttachment(0, offsetTop);
		fdata.right = new FormAttachment(100, -offsetRight);
		fdata.height = 16;
		fdata.width = 16;
		helpCanvas.setLayoutData(fdata);
		Images.paintCanvas(helpCanvas, "help", getHelpImageRegistry(helpCanvas.getDisplay()));
		helpCanvas.setToolTipText(tooltipText);
	}
	
	public static ImageRegistry getFullImageRegistry(Display display) {
		ImageRegistry imgReg = new ImageRegistry(display);
		
		URL imgURL = Images.class.getResource("/org/opensourcedea/gui/images/page_white.png");
		imgReg.put("new", ImageDescriptor.createFromURL(imgURL));
		
//		imgReg.put("new", ImageDescriptor.createFromFile(Images.class, "../images/page_white.png"));
		imgReg.put("help", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/help.png"));
		imgReg.put("accept", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/accept.png"));
		imgReg.put("error", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/error.png"));
		imgReg.put("deaProblem", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/folder_page.png"));
		imgReg.put("rawData", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/table.png"));
		imgReg.put("variables", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/variable.png"));
		imgReg.put("modelDetails", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/widgets.png"));
		imgReg.put("solution", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/folder_star.png"));
		imgReg.put("objective", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/investment_menu_quality.png"));
		imgReg.put("projections", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/edit_path.png"));
		imgReg.put("lambdas", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/lambda-small.png"));
		imgReg.put("referenceSet", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/group.png"));
		imgReg.put("slacks", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/go-last-2.png"));
		imgReg.put("weights", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/balance.png"));
		imgReg.put("importWizard", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/document-import-2.png"));
		imgReg.put("exclamation", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/exclamation.png"));
		imgReg.put("open", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/folder.png"));
		imgReg.put("save", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/disk.png"));
		imgReg.put("saveAll",  ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/disk_multiple.png"));
		imgReg.put("exit", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/cross.png"));
		imgReg.put("import", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/page_white_put.png"));
		imgReg.put("export", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/page_white_get.png"));
		imgReg.put("close", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/box_closed.png"));
		imgReg.put("potIcon", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/pot2422-48x48x32.png"));
		imgReg.put("potIconSmall", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/potSmall.png"));
		imgReg.put("potIconLarge", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/pot_256.png"));
		imgReg.put("potIcon64", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/pot_64.png"));
		return imgReg;
	}
	
	public static ImageRegistry getMainGUIImageRegistry(Display display) {
		ImageRegistry imgReg = new ImageRegistry(display);
		imgReg.put("potIcon", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/pot2422-48x48x32.png"));
		imgReg.put("potIconSmall", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/potSmall.png"));
		imgReg.put("potIconLarge", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/pot_256.png"));
		imgReg.put("potIcon64", ImageDescriptor.createFromFile(Images.class, "/org/opensourcedea/gui/images/pot_64.png"));
		return imgReg;
	}
	
	
	
	public static void paintCanvas(final Canvas canvas, final String resource) {
		paintCanvas(canvas, resource, getFullImageRegistry(canvas.getDisplay()));
	}
	
	
	public static void paintCanvas(final Canvas canvas, final String resource, final ImageRegistry imgReg) {

		canvas.addPaintListener (new PaintListener () {
			public void paintControl (PaintEvent e) {
				//http://www.eclipse.org/forums/index.php/mv/tree/141414/#page_top Ryan
				GC gc2 = new GC(canvas);
				gc2.setBackground(canvas.getBackground());
				gc2.fillRectangle(0, 0, canvas.getSize().x, canvas.getSize().y);
				gc2.dispose();	
				e.gc.drawImage (imgReg.get(resource), 0, 0);

			}
		});
		
		canvas.redraw();
	}
	
	
	@SuppressWarnings("unused")
	private static Image scaleImage(Display display, Image originalImage, int factor) {

		double intFactor = 1d/factor;

		final int width = originalImage.getBounds().width; 
		final int height = originalImage.getBounds().height; 

		Image scaledImage = new Image(display, originalImage.getImageData().scaledTo((int)(width * intFactor),(int)(height * intFactor)));

		return scaledImage;
	}
	
}
