package com.teamcenter.custwork.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


import com.teamcenter.custwork.workmanager.WorkManagerCommand;
import com.teamcenter.rac.util.MessageBox;

public class WorkManagerHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent e) throws ExecutionException {
		
		try {
			WorkManagerCommand command = new WorkManagerCommand();
			command.executeModal();
		} catch (Exception e1) {
			MessageBox.post(e1);
			e1.printStackTrace();
		}
		return null;
	}

}
