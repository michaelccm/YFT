package com.yfjc.newschedule;

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.project.sharedUtils.TimeZoneCalUtil;
import com.teamcenter.rac.schedule.wizardpages.SchedulePageModel;
import com.teamcenter.rac.ui.commands.handlers.NewBOHandler;
import com.teamcenter.rac.util.Registry;

public class YfjsNewScheduleHandler extends NewBOHandler{
	private static SchedulePageModel scheduleModel = null;
	private final Registry textRegistry = Registry
			.getRegistry("com.teamcenter.rac.schedule.schedule");
	
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		scheduleModel = null;
		this.wizardId = "com.teamcenter.rac.schedule.commands.newschedule.GenericScheduleWizard";
		AbstractAIFUIApplication application = AIFUtility.getCurrentApplication();
		TCSession localTCSession = (TCSession)application.getSession();
		TCPreferenceService localTCPreferenceService = localTCSession
				.getPreferenceService();
		String str1 = localTCPreferenceService.getString(4, "SiteTimeZone",
				"null");
		IWorkbench localIWorkbench = PlatformUI.getWorkbench();
		IWorkbenchWindow localIWorkbenchWindow = localIWorkbench
				.getActiveWorkbenchWindow();
		Shell localShell = localIWorkbenchWindow.getShell();
		if (("null".equals(str1)) || (str1 == null)) {
			MessageDialog.openInformation(localShell,
					this.textRegistry.getString("tzNotSet.TITLE"),
					this.textRegistry.getString("tzNotSet.MSG"));
			return Boolean.valueOf(false);
		}
		if (!(TimeZoneCalUtil.testTimeZoneID(str1))) {
			String str2 = this.textRegistry.getString("tzInvalid.MSG");
			str2 = MessageFormat.format(str2, new Object[] { str1 });
			MessageDialog.openInformation(localShell,
					this.textRegistry.getString("tzInvalid.TITLE"), str2);
			return Boolean.valueOf(false);
		}
		return super.execute(arg0);
	}
	
	@Override
	public String getWizardTitle() {
		// TODO Auto-generated method stub
		return super.getWizardTitle();
	}
}
