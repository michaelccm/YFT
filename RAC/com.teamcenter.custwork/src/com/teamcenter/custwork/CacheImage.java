package com.teamcenter.custwork;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ImageIcon;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import com.teamcenter.rac.common.TCTypeRenderer;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.Registry.ConvertibaleImageIcon;

public class CacheImage {
	private final Map<String, Image> imageMap = new HashMap<String, Image>();

	private static CacheImage INSTANCE;

	private CacheImage() {
	}

	public static CacheImage getINSTANCE() {
		if (INSTANCE == null)
			INSTANCE = new CacheImage();
		return INSTANCE;
	}


	
	public Image getImage(String applicationID,String imageName) {
		if (imageName == null)
			return null;
		Image image = (Image) imageMap.get(imageName);
		if (image == null) {
			image =AbstractUIPlugin.imageDescriptorFromPlugin(
					applicationID,imageName).createImage();
			imageMap.put(imageName, image);
		}

		return image;
	}

	public Image getImageIco(URL url,String imageName) {
		if (imageName == null)
			return null;
		Image image = (Image) imageMap.get(imageName);
		if (image == null) {
			ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
			image =imageDescriptor.createImage();
			imageMap.put(imageName, image);
		}

		return image;
	}

	public void dispose() {
		Iterator iterator = imageMap.values().iterator();
		while (iterator.hasNext())
			((Image) iterator.next()).dispose();
		imageMap.clear();
	}

	public Image convertibaleImage(TCComponent tccomonent) throws TCException{
		ImageIcon icon = TCTypeRenderer.getIconForComponent(tccomonent, false, null);
		ImageDescriptor imagedes = ((ConvertibaleImageIcon) icon).getImageDescriptor();
		return imagedes.createImage();
	}

	public ImageIcon convertibaleImageIcon(TCComponent tccomonent) throws TCException{
		return TCTypeRenderer.getIconForComponent(tccomonent, false, null);
	}

	public ImageIcon convertibaleImageIcon(String imageName) throws TCException{
		String imgLocation = "images/" + imageName;
		URL imageURL = CacheImage.class.getResource(imgLocation);
		if (imageURL != null)
			return new ImageIcon(imageURL);
		else
			return null;
	}
	
	
}