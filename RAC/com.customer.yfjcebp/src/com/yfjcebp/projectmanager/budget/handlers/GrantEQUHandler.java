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
 #	2013-3-11	liuc  		Ini		授权EQU的handler						   
 #=============================================================================
 */

package com.yfjcebp.projectmanager.budget.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.yfjcebp.projectmanager.budget.actions.GrantEQUAction;

public class GrantEQUHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
		TCSession session = (TCSession) app.getSession();
		
		//得到tcccomponentusertype用户登录名
		try {
			TCComponentUserType tcccomponentusertype = (TCComponentUserType) session.getTypeComponent("User");
			GrantEQUAction action=new GrantEQUAction(app,null,null);
			new Thread(action).start();
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
