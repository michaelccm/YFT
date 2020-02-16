/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: ExcelDialog.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-11	wuh  		Ini		初�� 								   
 #=============================================================================
 */
package com.yfjcebp.projectmanager.budget.dialogs;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.util.Registry;

/**
 * 使用swt组件做的界面 To use swt components to do the interface.
 * 
 * @author wu
 */
public class ImportDialogError {
	private Shell shell;
//	private Registry reg = Registry.getRegistry(this);
	private static final String BURFLR_NAME = "com.yfjcebp.projectmanager.budget.dialogs.dialogs_locale";
	private static final ResourceBundle reg = ResourceBundle.getBundle(BURFLR_NAME);
	private Label message;

	public ImportDialogError(final Display display,int type) {
		shell = new Shell(display);
		shell.setSize(260, 180);
		shell.setText(reg.getString("Shell_Error"));
		message = new Label(shell, SWT.CENTER);
		message.setBounds(30, 50, 200, 60);
		if(type==2)
			message.setText("Not found GEBTTemplate !!!");
		else
			message.setText("PDX has already existed !!!");
		shell.open();
		shell.getCursor();
	}

}
