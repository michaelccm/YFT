/*
#=============================================================================
#																			   
#			copyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: ImportNonLaborActualDataHandler.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-3-8	liqz  		Ini		Handler					
#	2013-6-27	liqz		Modify	QAD					   
#=============================================================================
*/
package com.yfjcebp.importdata.importnonlabor.actual;

import java.util.ResourceBundle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Utilities;

/**
 * @author liqz
 */
public class ImportNonLaborActualDataHandler extends AbstractHandler {
	private AbstractAIFUIApplication app;

	/**
	 * iImportType::ActualActual_PRQAD
	 * 1:Actual  data
	 * 2:Actual_PR data
	 * 3:QAD data
	 * */
	private int iImportType = 0;
//	private Registry reg=null;
	private static final String BURFLR_NAME = "com.yfjcebp.importdata.utils.utils_locale";
	private static final ResourceBundle reg = ResourceBundle.getBundle(BURFLR_NAME);
	
	
	/**
	 * checkImportFile::
	 * @param TCComponent 
	 * @return TCComponentTcFile
	 * **/
	private TCComponentTcFile checkImportFile(TCComponent selectedCompoent) {
		
//		reg = Registry.getRegistry("com.yfjcebp.importdata.utils.utils");
		String strType = selectedCompoent.getType();
		//System.out.println(reg.getString("SelectCorrectFileToImport"));
		if((!strType.equalsIgnoreCase("MSExcelX")) &&(!strType.equalsIgnoreCase("MSExcel")) && (!strType.equalsIgnoreCase("Text"))){
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("SelectCorrectFileToImport"),"Information",MessageBox.WARNING);
			return null;
		}
		TCComponentDataset dataset = (TCComponentDataset) selectedCompoent;
		TCComponentTcFile tcfile[] = null;
		try {
			tcfile = dataset.getTcFiles();
			if(tcfile == null || tcfile.length ==0){
				MessageBox.post(Utilities.getCurrentFrame(),reg.getString("NoNameReferenceFile"),"Information",MessageBox.WARNING);
				return null;
			}
			String strDatasetName = dataset.getProperty("object_name");
			if(strType.equalsIgnoreCase("MSExcelX") || strType.equalsIgnoreCase("MSExcel")){
				/*<zhouxi> remove the function of import testing data*
			    /*if(strDatasetName.toUpperCase().startsWith("TEST_")){
					iImportType = 1;
				}else */if(strDatasetName.toUpperCase().startsWith("PR_")){
					iImportType = 2;
				}
			}else{
//				if(strDatasetName.toUpperCase().startsWith("QAD_")){
				iImportType = 3;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return tcfile[0];
	}

	/* 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		app = AIFUtility.getCurrentApplication();
		InterfaceAIFComponent component = app.getTargetComponent();
		
		TCComponentTcFile tcfile = checkImportFile((TCComponent)component);
		if(tcfile != null){
			new ImportNonLaborActualDataDialog(app, tcfile,iImportType);
		}
		return null;
	}
}
