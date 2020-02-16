package com.yfjcebp.reorientation.user.privileges;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class ReoUserPrivilegesHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		// TODO Auto-generated method stub
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		ReoUserPrivilegesAction action = new ReoUserPrivilegesAction(app,"");
		action.run();
//		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
//		TCSession session = (TCSession)app .getSession();
//		InterfaceAIFComponent[] aifs = app.getTargetComponents();
//		TCComponent[] comps = new TCComponent[aifs.length];
//		for(int i=0;i<aifs.length;i++){
//			comps[i] = (TCComponent) aifs[i];
//		}
//		
//		try {
//			session.getUserService().call("D4CreateSnapshot", aifs);
//		} catch (TCException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return null;
	}

}
