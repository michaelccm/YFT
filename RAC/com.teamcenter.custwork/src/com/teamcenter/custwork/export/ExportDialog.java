package com.teamcenter.custwork.export;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.eclipse.ui.part.ViewPart;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class ExportDialog extends AbstractAIFDialog {

	private String title = "";

	private ExcelOperation excelOperation = null;

	public ExportDialog(ExcelOperation excelOperation,String title,boolean bool) {
		super(bool);
		this.title = title;
		this.excelOperation = excelOperation;
		initUI();
	}

	public void initUI(){
		setPersistentDisplay(true);
		setOptimalDisplay(true);
		setTitle(title);
		final JFileChooser jfilechooser = new JFileChooser();
		jfilechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jfilechooser.setApproveButtonText("µ¼³ö");
		jfilechooser.setApproveButtonMnemonic("exportFileButton.MNEMONIC".charAt(0));
		centerToScreen(1.0D, 0.5D, 0.0D, 0.5D);
		getContentPane().add(jfilechooser);
		jfilechooser.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String string = e.getActionCommand();
				System.out.println("string:::" + string);
				if("ApproveSelection".equals(string)){
					try {
						File file = jfilechooser.getSelectedFile();
						if(!file.getName().endsWith(".xlsx")){
							file = new File(file.getPath()+".xlsx");
						}
						new Thread(new ExportProgressDialog(excelOperation,title,
								(TCSession)AIFUtility.getDefaultSession(),
								null,file)).start();
						dispose();
					} catch (TCException e1) {
						MessageBox.post(e1);
						e1.printStackTrace();
					} 
				}else if("CancelSelection".equals(string)){
					dispose();
				}
			}			
		});	
	}

}
