package com.teamcenter.rac.schedule.commands.newschedule;

import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.plugin.Activator;
import com.teamcenter.rac.schedule.project.sharedUtils.TimeZoneCalUtil;
import com.teamcenter.rac.schedule.wizardpages.SchedulePageModel;
import com.teamcenter.rac.services.ISessionService;
import com.teamcenter.rac.ui.commands.handlers.NewBOHandler;
import com.teamcenter.rac.util.Registry;
import java.text.MessageFormat;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class NewScheduleHandler extends NewBOHandler
{
  public static String commandName="";
  private static SchedulePageModel scheduleModel = null;
  private final Registry textRegistry = Registry.getRegistry("com.teamcenter.rac.schedule.schedule");

  public static SchedulePageModel getScheduleModel()
  {
    if (scheduleModel == null)
      scheduleModel = new SchedulePageModel();
    return scheduleModel;
  }

  public Object execute(ExecutionEvent paramExecutionEvent)
    throws ExecutionException
  {
	try {
		commandName = paramExecutionEvent.getCommand().getName();
		System.out.println("commandName:"+commandName);
	} catch (NotDefinedException e) {
		e.printStackTrace();  
	}
    scheduleModel = null;
    this.wizardId = "com.teamcenter.rac.schedule.commands.newschedule.GenericScheduleWizard";
    TCSession localTCSession = (TCSession)Activator.getDefault().getSessionService().getDefaultSession();
    TCPreferenceService localTCPreferenceService = localTCSession.getPreferenceService();
    String str1 = localTCPreferenceService.getString(4, "SiteTimeZone", "null");
    IWorkbench localIWorkbench = PlatformUI.getWorkbench();
    IWorkbenchWindow localIWorkbenchWindow = localIWorkbench.getActiveWorkbenchWindow();
    Shell localShell = localIWorkbenchWindow.getShell();
    if (("null".equals(str1)) || (str1 == null))
    {
      MessageDialog.openInformation(localShell, this.textRegistry.getString("tzNotSet.TITLE"), this.textRegistry.getString("tzNotSet.MSG"));
      return Boolean.valueOf(false);
    }
    if (!TimeZoneCalUtil.testTimeZoneID(str1))
    {
      String str2 = this.textRegistry.getString("tzInvalid.MSG");
      str2 = MessageFormat.format(str2, new Object[] { str1 });
      MessageDialog.openInformation(localShell, this.textRegistry.getString("tzInvalid.TITLE"), str2);
      return Boolean.valueOf(false);
    }
    return super.execute(paramExecutionEvent);
  }

  public String getWizardTitle()
  {
    Registry localRegistry = Registry.getRegistry(this);
    return localRegistry.getString("dialog.TITLE");
  }
}