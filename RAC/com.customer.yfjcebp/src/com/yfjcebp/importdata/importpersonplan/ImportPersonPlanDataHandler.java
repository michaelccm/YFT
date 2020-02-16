/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: ImportPersonPlanDataHandler.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-17	zhangyn  		Ini				
 #	2013-4-08	zhangyn  		add		excel	
 #	2013-4-15	zhangyn  		add						   
 #=============================================================================
 */
package com.yfjcebp.importdata.importpersonplan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Utilities;
import com.yfjcebp.importdata.AbstractImportDataHandler;
import com.yfjcebp.importdata.utils.OriginUtil;

public class ImportPersonPlanDataHandler  extends AbstractImportDataHandler {
	
	protected TCComponentFolder tcfolder = null;
	
	protected String filename = "";
	
//	private static Registry reg = Registry.getRegistry(OriginUtil.class);
	private static final String BURFLR_NAME = "com.yfjcebp.importdata.utils.utils_locale";
	private static final ResourceBundle reg = ResourceBundle.getBundle(BURFLR_NAME);
	
	protected String importPersonPlanPreference = "YFJC_Import_PersonPlan_RowStartEnd";
	
	protected String SearchImportPersonPlan =  "YFJC_SearchCostInfoByImportPersonPlan";
	
	private List<String> importList = new ArrayList<String>();
	
	private String insideSheet = "";
	private String outsideSheet = "";
	
	private int insideRowStart = 0;
	private int insideRowEnd = 0;
	private int outsideRowStart = 0;
	private int outsideRowEnd = 0;

	public List<String> getImportList() {
		return importList;
	}

	public void setImportList(List<String> importList) {
		this.importList = importList;
	}

	@Override
	protected File checkImportFile(TCComponent selectedComponent) {
		try {
			filename = selectedComponent.getProperty("object_name");
			AIFComponentContext[] reference = selectedComponent.whereReferenced();
			for(int i = 0;i <reference.length;i++)
			{
				if(reference[i].getComponent() instanceof TCComponentFolder)
				{
					tcfolder = (TCComponentFolder) reference[i].getComponent();
					break;
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		file = OriginUtil.checkImportFile(selectedComponent);
		return file;
	}
	
	@Override
	protected void doImport() {
		TCPreferenceService ps = session.getPreferenceService();
		ArrayList<String> preferenceList = OriginUtil.getPreferenceList(ps,importPersonPlanPreference);
		if(preferenceList.size()==0)
		{
			return ;
		}
		if(preferenceList.size()!=2)
		{
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + importPersonPlanPreference + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
			return ;
		}
		if(preferenceList.get(0).split(":").length==2)
		{
			insideSheet = preferenceList.get(0).split(":")[0];
			if(preferenceList.get(0).split(":")[1].split(",").length==2)
			{
				insideRowStart = Integer.parseInt(preferenceList.get(0).split(":")[1].split(",")[0]);
				insideRowEnd = Integer.parseInt(preferenceList.get(0).split(":")[1].split(",")[1]);
			}else
			{
				MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + importPersonPlanPreference + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
				return ;
			}
		}else
		{
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + importPersonPlanPreference + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
			return ;
		}
		if(preferenceList.get(1).split(":").length==2)
		{
			outsideSheet = preferenceList.get(1).split(":")[0];
			if(preferenceList.get(1).split(":")[1].split(",").length==2)
			{
				outsideRowStart = Integer.parseInt(preferenceList.get(1).split(":")[1].split(",")[0]);
				outsideRowEnd = Integer.parseInt(preferenceList.get(1).split(":")[1].split(",")[1]);
			}else
			{
				MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + importPersonPlanPreference + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
				return ;
			}
		}else
		{
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + importPersonPlanPreference + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
			return ;
		}
		
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				new ImportPersonPlanDataDialog(ImportPersonPlanDataHandler.this,session,file,tcfolder,filename,insideSheet,outsideSheet,insideRowStart,insideRowEnd,outsideRowStart,outsideRowEnd);
			}
		});
	}

}
