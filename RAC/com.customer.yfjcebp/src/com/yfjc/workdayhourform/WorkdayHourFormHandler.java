package com.yfjc.workdayhourform;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;



public class WorkdayHourFormHandler extends AbstractHandler {

	

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		TCSession session = (TCSession) AIFUtility.getCurrentApplication()
					.getSession();
		WorkdayHourFormCommand command = new WorkdayHourFormCommand(session);
		try {
			command.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


}
