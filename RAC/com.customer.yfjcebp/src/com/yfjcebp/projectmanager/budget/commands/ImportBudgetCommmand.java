/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: ImportHyperionDataHandler.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-11	liuc  		Ini		TBL����Ԥ���command						   
 #=============================================================================
 */
package com.yfjcebp.projectmanager.budget.commands;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.util.MessageBox;
import com.yfjcebp.projectmanager.budget.dialogs.TBLDialog;

public class ImportBudgetCommmand extends AbstractAIFCommand {
	private AbstractAIFApplication app;

	public ImportBudgetCommmand(AbstractAIFApplication arg0) {
		app = arg0;
	}

	@Override
	public void executeModal() throws Exception {
		// tyl 2015/01/12 GEBT导入工具改进(当GEBT文件的EXCEL 数据集�指派到�両�盗�，�求报错，终�导入)
		InterfaceAIFComponent[] components = this.app.getTargetComponents();
		//System.out.println("------------ww ImportBudgetCommmand---------------:"+components);
		if ((components == null) || (components.length > 1)) {
			MessageBox.post("Please choose GEBT xlsm dataset !", "Warning", MessageBox.WARNING);
			return;
		}
		if (!(components[0] instanceof TCComponentDataset)) {
			MessageBox.post("Please choose GEBT  !", "Warning", MessageBox.WARNING);
			return;
		}
		TCComponent[] project_list = ((TCComponent) components[0]).getReferenceListProperty("project_list");
		if ((project_list == null)||(project_list.length==0)) {
			MessageBox.post("GEBT xlsm dataset need to be assigned by one project!", "Warning", MessageBox.WARNING);
			return;
		}
		if (project_list.length > 1) {
			MessageBox.post("GEBT xlsm dataset assigned by more than one project,please check it!", "Warning", MessageBox.WARNING);
			return;
		}
		// tyl 2015/01/12 GEBT导入工具改进 (当GEBT文件的EXCEL 数据集�指派到�両�盗�，�求报错，终�导入)]
		//System.out.println("new TBLDialog1");
		//System.out.println("---------ok---------");
		new TBLDialog(app);
		//System.out.println("s");
	}
}
