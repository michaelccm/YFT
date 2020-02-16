package com.teamcenter.custwork.workmanager;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentFolderType;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class WorkManagerCommand extends AbstractAIFCommand {

	public WorkManagerCommand() throws TCException {
		WorkManagerDialog workManager = new WorkManagerDialog(
				AIFUtility.getActiveDesktop().getFrame(), false);
		setRunnable(workManager);
	}
	
}
