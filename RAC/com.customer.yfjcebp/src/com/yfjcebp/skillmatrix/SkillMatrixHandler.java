/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: SkillMatrixHandler.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-9	wuh  		Ini		閸掓繂顫愰崠锟� 								   
 #=============================================================================
 */
package com.yfjcebp.skillmatrix;

import java.io.File;
import java.util.ResourceBundle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;


/**
 * 缂佸瓨濮㈤悽銊﹀煕閹讹拷閼崇晨�慼andler Maintenance of user skills handler
 * 
 * @author wuhui
 * 
 */

public class SkillMatrixHandler extends AbstractHandler {

	private String path;
	private TCSession session;
//	private  Registry reg = Registry.getRegistry(SkillMatrixHandler.class);
	private static final String BURFLR_NAME = "com.yfjcebp.skillmatrix.skillmatrix_locale";
	private static final ResourceBundle reg = ResourceBundle.getBundle(BURFLR_NAME);
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
		session = (TCSession) app.getSession();
		//if (session.getUserName().equals("infodba")) {
			// 瀵版鍩岄悽銊﹀煕闁褰囬惃鍕�鐠烇�
			TCComponentDataset dataset = getDataset(app);
			if (dataset == null) {
				MessageBox.post(reg.getString("selectNotDatasetMessage"), reg.getString("notDatasetMessage"), MessageBox.ERROR);
			} else {
				// 瀵版鍩宒ataset閸涜棄鎮曞鏇犳暏閻ㄥ�鏋冩禒锟�
				File file = getDataSetPathFile(dataset);
				if(file != null){
				   // 瀵版鍩屽鏇犳暏閺傚洣娆㈤惃鍕卜鐎电鐭惧锟�
				   path = file.getAbsolutePath();
				   TCComponentFolder folder = null;  
				   SkillMatrixAction skillMatrixAction = new SkillMatrixAction((AbstractAIFUIApplication) app, path);
				}  
			}
//		} else {
//			MessageBox.post(reg.getString("userNotAdminMessage"), reg.getString("warmessage"), MessageBox.ERROR);
//		}
		return null;
	}


	/**
	 * 瀵版鍩岄弫鐗堝祦闂嗗�娈戝鏇犳暏閻ㄥ嫏�冩�锟�
	 * 
	 * @param dataset
	 * @return
	 */
	public File getDataSetPathFile(TCComponentDataset dataset) {
		TCComponentTcFile[] tcFiles;
		File file = null;

		try {
			tcFiles = dataset.getTcFiles();
			if(tcFiles.length >0){
		     	// 瀵版鍩屽鏇犳暏閻ㄥ�鏋冩禒锟�
				/*TCComponentDataset logFile = dataset;
				String path = System.getenv("Temp") + File.separator + "template_info.xlsm";
						File log = new File(path);
						if(log.exists()){
							log.delete();
						}
						file = logFile.getFile("excel", log.getName(), log.getParent());*/
				String property = System.getProperty("java.io.tmpdir");
				File dir = new File(property+tcFiles[0]);
				dir.delete();
				file = tcFiles[0].getFile(property);
			}else{
				MessageBox.post(reg.getString("datasetrefMessage"), reg.getString("errmessage"), MessageBox.WARNING);
			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 閸掋倖鏌囂�銊﹀煕闁褰囬惃鍕Ц閸氾缚璐熼弐�堝祦闂嗭�
	 */
	private TCComponentDataset getDataset(AbstractAIFApplication app) {
		TCComponentDataset dataset = null;
		// 瀵版鍩岄悽銊﹀煕闁褰囬惃鍕�鐠烇�
		InterfaceAIFComponent targets[] = app.getTargetComponents();
		for (int i = 0; i < targets.length; i++) {
			if ( targets[i] instanceof TCComponentDataset ) {
				dataset = (TCComponentDataset) targets[i];
			}
		}

		return dataset;
	}
}
