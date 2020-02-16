package com.yfjcebp.extsupport.management;

import javax.swing.SwingUtilities;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class ExtSupportManagementHandler extends AbstractHandler{

	private AbstractAIFUIApplication app;
	private TCSession session;
	private TCComponentFolder homeFolder;

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) app.getSession();
		try {
			InterfaceAIFComponent com = app.getTargetComponent();
			if (com != null && com instanceof TCComponentFolder) {
				homeFolder = (TCComponentFolder) com;
			}else{
				homeFolder = session.getUser().getHomeFolder();
			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				new ExtSupportManagementDialog(session, homeFolder);
			}
		});
		return null;
	}

}
