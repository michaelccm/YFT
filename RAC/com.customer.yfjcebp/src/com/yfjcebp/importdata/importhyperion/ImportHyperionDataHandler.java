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
 #	2013-3-1	liqz  		Ini		导入Hyperion数据Handler									   
 #=============================================================================
 */
package com.yfjcebp.importdata.importhyperion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Utilities;
import com.yfjcebp.importdata.utils.OriginUtil;
import com.yfjcebp.importdata.AbstractImportDataHandler;

public class ImportHyperionDataHandler extends AbstractImportDataHandler {

	private HashMap<String,String> hmCostType = new HashMap<String,String>();
	private HashMap<String,String> hmMonthProperty = new HashMap<String,String>();
	private ArrayList<String> listSameCostType = new ArrayList<String>();
	private ArrayList<String> listPDxRowRange = new ArrayList<String>();
	private String strCostInfoDateColRange = "";
	private int iPDxDateRow ;
	private String strPdxProejctIdColName;
	private int iPdxProejctIdRow;
	private String strPdxCostPhaseTypeColName;
	private String strPdxCostTypeColName;
//	private Registry reg = Registry.getRegistry("com.yfjcebp.importdata.utils.utils");
	private static final String BURFLR_NAME = "com.yfjcebp.importdata.utils.utils_locale";
	private static final ResourceBundle reg = ResourceBundle.getBundle(BURFLR_NAME);
	private static Logger logger = Logger.getLogger(ImportHyperionDataHandler.class);
	
	/**
	 * m_PreferenceCostTpe::配置Excel临�用类型的名称与数捺�业�名称的�应关�
	 * */
	private String m_PreferenceCostTpe = "YFJC_CostType_Map"; 
	
	/**
	 * m_PreferenceMonthProperty::配置Excel东�份与数据库中费用信息的月份的对应关系
	 * */
	private String m_PreferenceMonthProperty = "YFJC_MonthProperty_Map";
	
	
	/**
	 * m_PreferenceSameCostType::配置Excel临�用类型不区分小类
	 * */
	private String m_PreferenceSameCostType = "YFJC_Hyperion_SameCostTypeInSmallGategory";
	
	private String m_PreferencePdxRowRange = "YFJC_Hyperion_Pdx_CostInfo_RowRange";
	
	/**
	 * m_PreferencePdxCostInfoDateRowColRange::配置Pdx表中�导入日期行�列范围
	 * */
	private String m_PreferencePdxCostInfoDateRowColRange = "YFJC_Hyperion_Pdx_CostInfo_Date_Row_ColNameRange";
	
	/**
	 * m_PreferencePdxProjectIdRowColName:: 配置PDx表中项目ID�在的行�列
	 * */
	private String m_PreferencePdxProjectIdRowColName = "YFJC_Hyperion_Pdx_ProjectId_Row_ColName";
	
	/**
	 * m_PreferencePdxCostPhaseTypeColName:: 配置PDx表中费用阶�类型�在列�
	 * */
	private String m_PreferencePdxCostPhaseTypeColName = "YFJC_Hyperion_Pdx_CostPhaseType_ColName";
	
	/**
	 * m_PreferencePdxCostTypeColName:: 配置PDx表中费用类型�在列�
	 * */
	private String m_PreferencePdxCostTypeColName = "YFJC_Hyperion_Pdx_CostType_ColName";
	
	private String m_CostTypeLovName = "Billing Types";
	
	/* 
	 * @see com.yfjcebp.importdata.AbstractImportDataHandler#checkImportFile(com.teamcenter.rac.kernel.TCComponent)
	 */
	@Override
	protected File checkImportFile(TCComponent selectedComponent) {
		ArrayList<String> listType = new ArrayList<String>();
		listType.add("MSExcel");
		listType.add("MSExcelX");
		File selFile = OriginUtil.checkImportFile(selectedComponent,listType);
		return selFile;
	}
	
	/* 
	 * @see com.yfjcebp.importdata.AbstractImportDataHandler#initPreferenceData()
	 */
	@Override
	protected boolean initImportPublicPreferenceData() {
		TCPreferenceService preference = session.getPreferenceService();
		//获取费用类型的�应关�
		hmCostType = OriginUtil.getMapWithLOV(session, preference, m_PreferenceCostTpe,m_CostTypeLovName);
		if(hmCostType.size() == 0){
			return false;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("hmCostType = " + hmCostType);
		}
		
		//获取月份属�的对应关系
		hmMonthProperty = OriginUtil.getPreferenceMap(preference, m_PreferenceMonthProperty);
		if(hmMonthProperty.size() == 0){
			return false;
		}
		return true;
	}

	/* 
	 * @see com.yfjcebp.importdata.AbstractImportDataHandler#initImportPdxPreferenceData()
	 */
	@Override
	protected boolean initImportPreferenceData() {
		TCPreferenceService preference = session.getPreferenceService();
		//获取Pdx表中不区分小类的费用类型
		listSameCostType = OriginUtil.getPreferenceListWithLov(session,preference, m_PreferenceSameCostType,m_CostTypeLovName);
		if(listSameCostType.size() == 0){
			return false;
		}
		
		//获取Pdx表中�要�入的行的范围
		listPDxRowRange = OriginUtil.getPreferenceList(preference, m_PreferencePdxRowRange);
		if(listPDxRowRange.size() == 0){
			return false;
		}
		
		//获取Pdx表中日期�在的行及列范�(6:D-CD)
		String strRowColDate = OriginUtil.getPreferenceValue(preference,m_PreferencePdxCostInfoDateRowColRange);
		strRowColDate = strRowColDate.replace("�",":");
		if(!strRowColDate.contains(":")){
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + m_PreferencePdxCostInfoDateRowColRange + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
			return false;
		}
		iPDxDateRow =  Integer.parseInt(strRowColDate.split(":")[0]);
		strCostInfoDateColRange = strRowColDate.split(":")[1];
		
		//获取Pdx表中ProejctId�在的行�列(2:B)
		String strPdxProejctIdRowColName = OriginUtil.getPreferenceValue(preference,m_PreferencePdxProjectIdRowColName);
		if(strPdxProejctIdRowColName.length() == 0){
			return false;
		}
		if((!strPdxProejctIdRowColName.contains(":"))){
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + m_PreferencePdxProjectIdRowColName + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
			return false;
		}
		strPdxProejctIdRowColName = strPdxProejctIdRowColName.replace("�",":");
		iPdxProejctIdRow = Integer.parseInt(strPdxProejctIdRowColName.split(":")[0]);
		strPdxProejctIdColName = strPdxProejctIdRowColName.split(":")[1];
		
		//获取PDx表中费用阶�类型列名
		strPdxCostPhaseTypeColName = OriginUtil.getPreferenceValue(preference,m_PreferencePdxCostPhaseTypeColName);
		if(strPdxCostPhaseTypeColName.length() == 0){
			return false;
		}
		
		//获取PDx表中费用类型列名
		strPdxCostTypeColName = OriginUtil.getPreferenceValue(preference,m_PreferencePdxCostTypeColName);
		if(strPdxCostTypeColName.length() == 0){
			return false;
		}
		return true;
	}

	/* 
	 * @see com.yfjcebp.importdata.AbstractImportDataHandler#doImport()
	 */
	@Override
	protected void doImport() {
		session.setStatus("正在进�Hyperion数据导入，�稍后...");
		session.queueOperation(new ReadDataFromFileOperation(file,session,hmCostType,hmMonthProperty,listSameCostType,
			listPDxRowRange,strCostInfoDateColRange,iPDxDateRow,iPdxProejctIdRow,strPdxProejctIdColName,strPdxCostPhaseTypeColName,strPdxCostTypeColName));
		session.setReadyStatus();
	}
}
