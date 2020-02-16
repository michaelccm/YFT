package com.teamcenter.custwork.toolkit;

import java.awt.Frame;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import com.teamcenter.custwork.CacheImage;
import com.teamcenter.rac.kernel.TCException;

public class NumTopMessageBox {
	public static void post(Frame frame, String s, String s1, int i){
		Object[] options = {"确定"};
		ImageIcon image = null;
		try {
			image = CacheImage.getINSTANCE().convertibaleImageIcon("ProcessVariable_32.png");
		} catch (TCException e) {
			e.printStackTrace();
		}		
	//	super.setIconImage(image.getImage());
		JOptionPane optionPanel = new JOptionPane();
		optionPanel.setIcon(image);
		optionPanel.showOptionDialog(frame, s,s1,JOptionPane.DEFAULT_OPTION,i,
				null, options, options[0]);
		
	}

	public static void post(Frame frame,
			Exception e1) {
		Object[] options = {"确定"};
		ImageIcon image = null;
		try {
			image = CacheImage.getINSTANCE().convertibaleImageIcon("ProcessVariable_32.png");
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	//	super.setIconImage(image.getImage());
		JOptionPane optionPanel = new JOptionPane();
		optionPanel.setIcon(image);
		optionPanel.showOptionDialog(frame, e1.getLocalizedMessage(),"Error",
				JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,
				null, options, options[0]);
		
	}

}
