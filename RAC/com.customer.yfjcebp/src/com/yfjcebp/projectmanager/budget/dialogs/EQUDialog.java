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
 #	2013-3-11	liuc  		Ini		授权预算的Dialog							   
 #=============================================================================
 */
package com.yfjcebp.projectmanager.budget.dialogs;

import java.io.File;

import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjcebp.projectmanager.budget.util.FileCopy;

public class EQUDialog extends AbstractAIFDialog {

	private static final long serialVersionUID = 1L;
	private AbstractAIFApplication app;
	private TCSession session;
	private TCComponent target;
	private static int isEQUDialog = 1;
	private static String ExcelNameMode = "GEBT.xlsm";
	private Registry reg = Registry.getRegistry(this);
	private TCComponentItem componentItem = null;
	private	TCComponentDataset dataSet_target =null;
	private TCComponent[] property=null;
	
	public EQUDialog(AbstractAIFApplication app) {
		this.app = app;
		session = (TCSession) this.app.getSession();

		

		target = (TCComponent) this.app.getTargetComponent();
		// target.whereReferencedByTypeRelation(arg0, arg1);

		if (!(target instanceof TCComponentDataset)) {
			MessageBox.post(reg.getString("dialog_warring"), "warning",
					MessageBox.WARNING);
			return;
		}

		try {
			// 得到数据集，保存到本地
			 dataSet_target = (TCComponentDataset) target;
			FileCopy fileCopy = new FileCopy();
			File file = getDataSetPathFile(dataSet_target);

			// 得到系统的零时文件
			String getenv = System.getenv("TEMP");
			File fileMode = new File(getenv + File.separator + ExcelNameMode);

			fileCopy.copyFile(file, fileMode);

			 property = target
					.getReferenceListProperty("project_list");

			// 通过getChildren()方法得到下面的子类
			TCComponent[] components = property[0]
					.getReferenceListProperty("project_data");

			// 得到Item
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof TCComponentItem) {
					TCComponentItem item = (TCComponentItem) components[i];
					if ("JCI6_ProgramInfo".equals(item.getType())) {
						componentItem = item;
					}
				}
			}

			// 创建对话框
			DeviceData deviceData = new DeviceData();
			Display display=new Display(deviceData);
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//Shell shell = AIFUtility.getCurrentApplication().getDesktop().getShell();
					try {
						Shell shell=new Shell();
						SWTutil t = new SWTutil(shell, session, componentItem,
								isEQUDialog, dataSet_target,property[0]);
					
						t.open();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 得到数据集的file
	private File getDataSetPathFile(TCComponentDataset tccomponentDataset) {
		File file = null;
		try {
			TCComponentTcFile tcFiles[] = tccomponentDataset.getTcFiles();
			file = tcFiles[0].getFmsFile();
		} catch (TCException e) {
			e.printStackTrace();
		}
		return file;
	}

}
