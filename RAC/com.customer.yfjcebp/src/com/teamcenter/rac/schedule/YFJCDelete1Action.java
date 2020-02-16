package com.teamcenter.rac.schedule;

import java.util.Vector;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.common.actions.Delete1Action;
import com.teamcenter.rac.util.MessageBox;

public class YFJCDelete1Action extends Delete1Action {

	private TCSession tcsession = null;
	private TCComponent[] scheduleTask = null;
	private  AbstractAIFCommand localObject = null;
	public YFJCDelete1Action(
			AbstractAIFUIApplication paramAbstractAIFUIApplication,
			String paramString) {
		super(paramAbstractAIFUIApplication, paramString);
		//System.out.println("--------------------YFJCDelete1Action-----------------------");
		tcsession =  (TCSession) paramAbstractAIFUIApplication.getSession();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run(){
		//System.out.println("------------------YFJCDelete1Action.run()  start--------------------------");
		try {
			InterfaceAIFComponent components[] = this.application.getTargetComponents();
			Vector<TCComponent> scheduleTaskVec = new Vector<TCComponent>();
			for(int i = 0; i < components.length; i ++)
			{
				TCComponent tmpScheduleTask = (TCComponent)components[i];
				if(tmpScheduleTask instanceof TCComponentScheduleTask)
				{
					getAllChildScheduleTask( (TCComponentScheduleTask)tmpScheduleTask , scheduleTaskVec );
				}
				
			}
			
			scheduleTask = new TCComponent[scheduleTaskVec.size()];
			for(int i = 0; i < scheduleTaskVec.size(); i ++)
			{
				//System.out.println("--------->		scheduleTaskVec["+i+"].object_name = "+scheduleTaskVec.get(i).getProperty("object_name"));
				scheduleTask[i] = scheduleTaskVec.get(i);
			}
			Object[] input = {scheduleTask};
			Object rtnObj = tcsession.getUserService().call("deleteScheduleTaskCheck", input);
			if(rtnObj.toString().length()>0)
			{
				//System.out.println("---->	"+rtnObj.toString());
				MessageBox.post(rtnObj.toString(),super.actionName, MessageBox.WARNING);
			}else{
				super.run();
			}
			
			

			
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    //System.out.println("------------------YFJCDelete1Action.run()  end--------------------------");
	}
	void getAllChildScheduleTask(TCComponentScheduleTask scheduleTask , Vector<TCComponent> scheduleTaskVec)
	{
		try {
			scheduleTaskVec.add(scheduleTask);
			AIFComponentContext[] childContexts = scheduleTask.getChildren("child_task_taglist");
			////System.out.println("----->			parent Schedule Task = "+scheduleTask.getProperty("object_name"));
			for(int i = 0; i < childContexts.length; i ++)
			{
				TCComponentScheduleTask tmpChildScheduleTask = (TCComponentScheduleTask) childContexts[i].getComponent();
				////System.out.println("----->			child "+i+" = "+tmpChildScheduleTask.getProperty("object_name"));
				getAllChildScheduleTask( tmpChildScheduleTask , scheduleTaskVec );
			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
