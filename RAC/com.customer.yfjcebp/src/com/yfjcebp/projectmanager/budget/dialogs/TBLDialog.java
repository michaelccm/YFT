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
 #	2013-3-11	liuc  		Ini		TBL导入预算的Dialog					   
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

public class TBLDialog extends AbstractAIFDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractAIFApplication app;
	private TCSession session;
	private TCComponent target;
	private Registry reg = Registry.getRegistry(this);
	private static int isTBL = 2;
	private static String ExcelNameMode = "GEBT.xlsm";
	TCComponentItem componentItem = null;
	TCComponentDataset dataSet_target =null;
	TCComponent[] property=null;
	
	// private boolean isFindItem = true;

	// 需要注册信息
	// private Registry reg = Registry.getRegistry(ImportTimeLogHandler.class);

	public TBLDialog(AbstractAIFApplication app) {
		System.out.println("123");
		this.app = app;
		session = (TCSession) this.app.getSession();

		target = (TCComponent) this.app.getTargetComponent();

		// target.whereReferencedByTypeRelation(arg0, arg1);
		System.out.println("11");
		if (!(target instanceof TCComponentDataset)) {
			MessageBox.post(reg.getString("dialog_warring"), "warning",
					MessageBox.WARNING);
			return;
		}

		try {
			System.out.println("22");
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
			System.out.println("33");
			// 得到Item
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof TCComponentItem) {
					TCComponentItem item = (TCComponentItem) components[i];
					if ("JCI6_ProgramInfo".equals(item.getType())) {
						componentItem = item;
					}
				}
			}
			System.out.println("44");
			// 创建对话框
			//DeviceData deviceData = new DeviceData();
			//System.out.println("deviceData:"+deviceData);//org.eclipse.swt.graphics.DeviceData@610e9216
			System.out.println("session:"+session);//infodba
			System.out.println("componentItem:"+componentItem);//CSA0302S-Yancheng support
			System.out.println("isTBL:"+isTBL);//2
			System.out.println("dataSet_target:"+dataSet_target);//GEBT v3.2.11 Rev 3_DYK Support CSA0302S.xlsm
			System.out.println("property[0]:"+property[0]);//CSA0302S-Yancheng support
			
			DeviceData deviceData = new DeviceData();
			Display display=new Display(deviceData);
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//Shell shell = AIFUtility.getCurrentApplication().getDesktop().getShell();
					try {
						Shell shell =new Shell();
						SWTutil t = new SWTutil(shell, session, componentItem, isTBL,
								dataSet_target,property[0]);
					
						t.open();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			})	;			
			
			
			

		} catch (TCException e) {
			// TODO Auto-generated catch block
			System.out.println("1");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("2");
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
