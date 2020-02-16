/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: ImportHyperionDataHandler.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-11	liuc  		Ini		ÊÚÈ¨Ô¤ËãµÄcommand						   
 #=============================================================================
 */
package com.yfjcebp.projectmanager.budget.commands;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.yfjcebp.projectmanager.budget.dialogs.EQUDialog;


public class GrantEQUCommmand extends AbstractAIFCommand {
	private AbstractAIFApplication app;

	public GrantEQUCommmand(AbstractAIFApplication arg0) {
		app = arg0;
	}

	@Override
	public void executeModal() throws Exception {
		EQUDialog dialog = new EQUDialog(app);
		setRunnable(dialog);
	}
}
