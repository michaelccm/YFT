/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: SkillMatrixAction.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-10	wuh  		Ini		¡¯ 								   
 #=============================================================================
 */
package com.yfjcebp.skillmatrix;

import org.eclipse.swt.widgets.Display;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.kernel.TCSession;

public class SkillMatrixAction extends AbstractAIFAction {

	private String path;
	private Display display;
	private TCSession session;
	private AbstractAIFApplication app;

	public SkillMatrixAction(AbstractAIFUIApplication arg0, String arg1) {
		super(arg0, arg1);
		this.app = arg0;
		this.path = arg1;
		display = Display.getDefault();
		run();
	}

	@Override
	public void run() {
		session = (TCSession) app.getSession();
		SkillMatrixProgressBar progressBar = new SkillMatrixProgressBar(
				display, session, path);
	}
}
