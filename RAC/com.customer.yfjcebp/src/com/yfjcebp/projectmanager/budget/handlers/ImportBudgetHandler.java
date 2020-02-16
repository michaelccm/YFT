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
 #	2013-3-11	liuc  		Ini		TBLµº»Î‘§À„µƒhandler						   
 #=============================================================================
 */
package com.yfjcebp.projectmanager.budget.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentScheduleTaskType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.yfjcebp.projectmanager.budget.actions.ImportBudgetAction;


public class ImportBudgetHandler extends AbstractHandler {

	private File file;
	private boolean isFindItem = true;

	AbstractAIFApplication app = AIFUtility.getCurrentApplication();
	TCSession session = (TCSession) app.getSession();

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {

		// System.out.println("class===="+component.getClass()+"  TYpe===="+component.getType()+"  SRING==="+component.toString());
		// sumExpenses(session);
		
		
		ImportBudgetAction action = new ImportBudgetAction(app, null, null);
		//System.out.println("------11ImportBudgetHandler-------");
		new Thread(action).start();

		return null;
	}

	
}
