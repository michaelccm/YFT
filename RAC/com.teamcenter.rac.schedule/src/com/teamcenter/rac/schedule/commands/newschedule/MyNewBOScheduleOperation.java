/**  
 * Project Name:com.yfjc.calendar  
 * File Name:MyNewBOScheduleOperation.java  
 * Package Name:com.teamcenter.rac.schedule.commands.newschedule  
 * Date:2019年11月26日下午1:44:19  
 * Copyright (c) 2019, Real All Rights Reserved.  
 *  
*/  
  
package com.teamcenter.rac.schedule.commands.newschedule;

import java.util.Locale;

import javax.swing.JOptionPane;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.wizard.IWizardPage;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.project.AssignProjectPage;
import com.teamcenter.rac.util.AdapterUtil;

/**  
 * ClassName:MyNewBOScheduleOperation <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2019年11月26日 下午1:44:19 <br/>  
 * @author   moni  
 * @version    
 * @since    JDK 1.7  
 * @see        
 */
public class MyNewBOScheduleOperation extends NewBOScheduleOperation {
	public static boolean has_schedule = false;
	public void executeOperation()throws Exception{
		has_schedule = false;
		String project_name="";
		IWizardPage[] pages = this.wizard.getPages();
		for (int i = 0; i < pages.length; i++) {
			if(pages[i] instanceof AssignProjectPage){
				TCComponentProject seleproj = ((AssignProjectPage)pages[i]).getMySelectProject();
				if(seleproj!=null){
					TCComponent[] childs = seleproj.getRelatedComponents("TC_Program_Preferred_Items");
					if(childs != null && childs.length > 0 ){
						for (int k = 0; k < childs.length; k++) {
							if(childs[k] instanceof TCComponentSchedule){
								project_name = childs[k].getStringProperty("object_string");
								has_schedule = true;
								break;
							}
						}
					}
				}
			}
		}
		if(has_schedule == true){
			if(Locale.getDefault().toString().equals("zh_CN")){
				JOptionPane.showMessageDialog(null,"项目："+project_name+"已关联时间表","错误",JOptionPane.ERROR_MESSAGE);
			}
			else{
				JOptionPane.showMessageDialog(null,"Project:"+project_name+" already assign schedule","错误",JOptionPane.ERROR_MESSAGE);
			}
		}
		else{
			super.executeOperation();
		}
	}
	protected void postCreate()
	{
		super.postCreate();
	}
}
  
