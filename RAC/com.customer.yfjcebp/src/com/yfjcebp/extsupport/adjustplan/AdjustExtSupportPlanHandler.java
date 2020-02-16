package com.yfjcebp.extsupport.adjustplan;

import javax.swing.SwingUtilities;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;

public class AdjustExtSupportPlanHandler extends AbstractHandler{

	private TCSession session;
	private AbstractAIFUIApplication app;
	
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) app.getSession();
		
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new AdjustExtSupportPlanDialog(session);
				
			}
			
		});
		return null;
	}

}
