package com.teamcenter.custwork.toolkit;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import com.teamcenter.custwork.CacheImage;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AifrcpPlugin;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.services.IAspectUIService;
import com.teamcenter.rac.util.OSGIUtil;

public class BuildComponentFactory {

	public static JButton makeNavigationButton(String imageName,String toolTipText, String altText) 
	throws TCException {
		String imgLocation = imageName;
		JButton button = new JButton();
		button.setToolTipText(toolTipText);
		button.setActionCommand(altText);
		button.setText(toolTipText);
		button.setEnabled(true);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		if (imgLocation != null){
			button.setIcon(CacheImage.getINSTANCE().convertibaleImageIcon(imgLocation));
		}else{ 
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}
		return button;
	}
	
	public static JButton makeNavigationToolButton(String imageName,String toolTipText) 
	throws TCException {
		String imgLocation = imageName;
		JButton button = new JButton();
		button.setToolTipText(toolTipText);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	//	button.setActionCommand(altText);
	//	button.setText(toolTipText);
		button.setEnabled(true);
		if (imgLocation != null){
			button.setIcon(CacheImage.getINSTANCE().convertibaleImageIcon(imgLocation));
		}else{ 
			button.setText(toolTipText);
			System.err.println("Resource not found: " + imgLocation);
		}
		return button;
	}
	
	
	public static JButton makeNavigationButton(String imageName,String toolTipText, String altText,
			Object obj) throws TCException {
		String imgLocation = imageName;
		JButton button = new JButton();
		button.setToolTipText(toolTipText);
		button.setActionCommand(altText);
		button.setText(toolTipText);
		button.setEnabled(true);
		button.addActionListener((ActionListener) obj);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		if (imgLocation != null){
			button.setIcon(CacheImage.getINSTANCE().convertibaleImageIcon(imgLocation));
		}else{ 
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}
		return button;
	}
	
	public static JButton makeNavigationToolButton(String imageName,String toolTipText, String altText,
			Object obj) throws TCException {
		String imgLocation = imageName;
		JButton button = new JButton();
		button.setToolTipText(toolTipText);
		button.setActionCommand(altText);
		button.addActionListener((ActionListener) obj);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setEnabled(true);
		if (imgLocation != null) {
			button.setIcon(CacheImage.getINSTANCE().convertibaleImageIcon(imgLocation));
		} else { 
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}
		return button;
	}
	


	
	
}
