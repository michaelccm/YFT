package com.teamcenter.rac.schedule.wizardpagegenerator;

import org.eclipse.jface.wizard.IWizardPage;

import com.teamcenter.rac.schedule.commands.newschedule.MyNewBOScheduleOperation;
import com.teamcenter.rac.schedule.commands.newschedule.NewScheduleHandler;
import com.teamcenter.rac.schedule.wizardpages.SchedulePageModel;
import com.teamcenter.rac.ui.commands.create.bo.NewBOWizard;
import com.teamcenter.rac.util.SWTUIUtilities;

public class NewScheduleBOWizard extends NewBOWizard
{
  public boolean has_scheule= false;
  public NewScheduleBOWizard(String paramString)
  {
    super(paramString);
  }

  public boolean performFinish()
  {
	  SchedulePageModel localSchedulePageModel = NewScheduleHandler.getScheduleModel();
	  boolean bool = localSchedulePageModel.validatePage();
	  if (bool)
	      return super.performFinish();
	  return bool;
  }
  public boolean canFinish()
  {
	if(NewScheduleHandler.commandName.contains("EBP")){
		System.out.println("is EBP");
		if(getWizardDialog()!=null){
			IWizardPage wizardpage = getWizardDialog().getCurrentPage();
			if(wizardpage != null){
				if(wizardpage.getName().equals("CreatePropsPage-Schedule")){
					return false;
				}
			}
		}
	}
    return super.canFinish();
  }
  //创建完成后，
  protected void postSuccessfulFinish(){
	  if(getOperationClass() instanceof MyNewBOScheduleOperation ){
		  MyNewBOScheduleOperation opera= (MyNewBOScheduleOperation) getOperationClass();
		  if(opera.has_schedule == false){
			  System.out.println("postSuccessfulFinish and flag is success");
			  SWTUIUtilities.asyncExec(new Runnable() {
				public void run() {
					 getWizardDialog().close();
				}
			  });
		  }
		  else{
			  System.out.println("postSuccessfulFinish and flag is failed");
		  }
	  }
  }
}