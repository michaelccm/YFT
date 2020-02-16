package com.yfjcebp.change.user.info;
import java.util.Calendar;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;


public class ChangeUserInfoHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		AbstractAIFUIApplication app =  AIFUtility.getCurrentApplication();
		ChangeUserInfoAction action = new ChangeUserInfoAction(app, "changeUserInfo");
		action.run();
		/*AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		TCSession session = (TCSession)app .getSession();
		InterfaceAIFComponent[] aifs = app.getTargetComponents();
		TCComponent[] comps = new TCComponent[aifs.length];
		for(int i=0;i<aifs.length;i++){
			comps[i] = (TCComponent) aifs[i];
		}
		
		try {
			session.getUserService().call("D4CreateSnapshots", comps);
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return null;
	}

	
	
	
	
}
