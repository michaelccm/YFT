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
 #	2013-3-11	liuc  		Ini		��ȨԤ���ACTION							   
 #=============================================================================
 */

package com.yfjcebp.projectmanager.budget.actions;

import java.awt.Frame;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.yfjcebp.projectmanager.budget.commands.GrantEQUCommmand;

public class GrantEQUAction extends AbstractAIFAction {

	private AbstractAIFApplication app;
	private AbstractAIFCommand commmand;

	public GrantEQUAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {
		super(arg0, arg1, arg2);
		app = arg0;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		commmand = new GrantEQUCommmand(app);
		try {
			commmand.executeModal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}