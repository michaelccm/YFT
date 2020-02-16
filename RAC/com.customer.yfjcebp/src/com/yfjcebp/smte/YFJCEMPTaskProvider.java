package com.yfjcebp.smte;


import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;


@SuppressWarnings("restriction")
public class YFJCEMPTaskProvider extends EPMTaskLabelProviderDelegate
{    
    @Override
    public String getText(Object element)
    {
    	System.out.println("getText");
        if(element instanceof AIFComponentContext && ((AIFComponentContext)element).getComponent() instanceof TCComponentTask)
        {
            TCComponentTask task = (TCComponentTask)((AIFComponentContext)element).getComponent();
            try
            {
                TCComponent[] schtasks = task.getReferenceListProperty("project_task_attachments");
                for(int i=0; schtasks!= null && i<schtasks.length; i++)
                {
                	System.out.println("process project_task_attachments");
                    if(schtasks[i] instanceof TCComponentScheduleTask)
                    {
                        TCComponent schedule = schtasks[i].getReferenceProperty("schedule_tag");
                        String projecName = schedule.getProperty("projects_list");
                        if(projecName != null && projecName.trim().length() > 0)
                        {
                            return projecName + " - " + super.getText(element);
                        }
                    }
                }
                TCComponent[] targets = task.getReferenceListProperty("root_target_attachments");
                for(int i=0; targets!= null && i<targets.length; i++)
                {
                	System.out.println("process root_target_attachments");
                    if(targets[i] instanceof TCComponent)
                    {
                        String projecName = targets[i].getProperty("projects_list");
                        if(projecName != null && projecName.trim().length() > 0)
                        {
                            return projecName + " - " + super.getText(element);
                        }
                    }
                }
                TCComponent[] refs = task.getReferenceListProperty("root_reference_attachments");
                for(int i=0; refs!= null && i<refs.length; i++)
                {
                	System.out.println("process root_reference_attachments");
                    if(refs[i] instanceof TCComponent)
                    {
                        String projecName = refs[i].getProperty("projects_list");
                        if(projecName != null && projecName.trim().length() > 0)
                        {
                            return projecName + " - " + super.getText(element);
                        }
                    }
                }
            }catch(TCException ex)
            {
                ex.printStackTrace();
            }
        }
        return super.getText( element );
    }

}
