/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: ReadDataFromExcelOperation.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-3-1	liqz  		Ini		’										   
#=============================================================================
 */
package com.yfjcebp.importdata.importhyperion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jacob.com.Dispatch;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjcebp.importdata.utils.MessageDialog;
import com.yfjcebp.importdata.utils.OriginUtil;
import com.yfjcebp.importdata.utils.ReadDataUtil;
import com.yfjcebp.projectmanager.budget.dialogs.JacobEReportTool;

/**
 * @author liqz
 */
public class ReadDataFromFileOperation extends AbstractAIFOperation {

	private TCUserService			service;
	private static SimpleDateFormat	dateformat					= new SimpleDateFormat("yyyy-M");
	private File					dataFile;
	private File errorLogFile;
	private TCSession				session;
	private HashMap<String, String>	hmCostType					= new HashMap<String, String>();
	private HashMap<String, String>	hmDateProperty				= new HashMap<String, String>();
	private ArrayList<String>		listSameCostType				= new ArrayList<String>();
	private ArrayList<String>		listPDxRowRange				= new ArrayList<String>();
	private ArrayList<String>		listActualColNameRange		= new ArrayList<String>();
	/*======variable for import Actual And Actual_PR file ======*/
	private String strActualCostPhaseTypeColName = "";
	private int iActualRowCostPhaseType = 0;
	private int iActualRowDate = 0;
	private String strActualCellDateColName = "";
	private int iRowActualCostType = 0;
	private int iRowActualStartWith = 0;
	/*======variable for import Actual And Actual_PR file ======*/
	
	/*======variable for import Hyperion file ======*/
	private int iRowPdxDate = 0;
	private String strActualProjectIdColName = "";
	private int iPdxProejctIdRow = 0;
	private String strPdxProejctIdColName = "";
	private String strPdxCostPhaseTypeColName = "";
	private String strPdxCostTypeColName = "";
	private String strCostInfoDateColRange = "";
	/*======variable for import Hyperion file ======*/
	
	/*======variable for import QAD file ======*/
	private int iStartRow = 0;
	/*======variable for import QAD file ======*/
	
	private ReadDataUtil dataUtil;
	
	private String strCostInfoSearchName = "YFJC_SearchCostInfoByProgramInfo";
	private String strProjectSearchName = "YFJC_SearchProjectByProjectID";
	
	/**
	 * conditions in YFJC_SearchCostInfo
	 * */
	private String[] arrCostInfokeys = new String[] { "project_id", "jci6_CPT", "jci6_CostType" ,"jci6_Year"};
	/**
	 * conditions in YFJC_SearchCostInfoByProgramInfo
	 * */
	private String[] arrCostInfoProgramkeys = new String[] { "project_id","item_revision_id", "jci6_CPT", "jci6_CostType" ,"jci6_Year"};
	
	private String[] arrProjectKeys = new String[]{"project_id"};
	
	private static Logger logger = Logger.getLogger(ReadDataFromFileOperation.class);
	private Registry reg = Registry.getRegistry(this);
	private boolean isHyperionImport = false;
	private int iImportType = 0;
	
	/**
	 * ReadDataFromExcelOperation:: 
	 * @param int lActualActual_PRQADl
	 * @param File l
	 * @param TCSession
	 * @param HashMap<String, String> Map
	 * @param HashMap<String, String> ·Map
	 * @param ArrayList<String>  ActualActual_PR SheetезΧ
	 * @param String 'y
	 * @param int 'y
	 * @param int 
	 * @param int ActualActual_PR
	 * @param String ActualActual_PR
	 * @param int ActualActual_PRп’
	 * @param String ActualActualPRLID
	 * */
	public ReadDataFromFileOperation(int pImportType,File pdataFile, TCSession pSession, HashMap<String, String> phmCostType,
		HashMap<String, String> phmDateProperty,ArrayList<String> plistActualColNameRange, 
			String pstrActualCostPhaseTypeColName,int prowActualCostPhaseType,int prowActualCostType,int pActualRowDate,
			String pActualCellDateName,int pActualStartRow,String pstrActualProjectIdColName) {
		iImportType = pImportType;
		session = pSession;
		service = session.getUserService();
		hmCostType = phmCostType;
		hmDateProperty = phmDateProperty;
		dataFile = pdataFile;
		listActualColNameRange = plistActualColNameRange;
		strActualCostPhaseTypeColName = pstrActualCostPhaseTypeColName;
		iActualRowCostPhaseType = prowActualCostPhaseType;
		iActualRowDate = pActualRowDate;
		strActualCellDateColName = pActualCellDateName;
		iRowActualCostType= prowActualCostType;
		iRowActualStartWith = pActualStartRow;
		strActualProjectIdColName = pstrActualProjectIdColName;
		dataUtil = new ReadDataUtil(session);
	}

	/**
	 * ReadDataFromExcelOperation:: 
	 * @param int lActualActual_PRQADl
	 * @param File l
	 * @param TCSession
	 * @param HashMap<String, String> Map
	 * @param HashMap<String, String> ·Map
	 * @param ArrayList<String>  ActualActual_PR SheetезΧ
	 * @param String 'y
	 * @param int 'y
	 * @param int 
	 * @param int ActualActual_PR
	 * @param String ActualActual_PR
	 * @param int ActualActual_PRп’
	 * @param String ActualActualPRLID
	 * */
	public ReadDataFromFileOperation(int pImportType,File pdataFile, TCSession pSession, HashMap<String, String> phmCostType,
		HashMap<String, String> phmDateProperty,int piStartRow) {
		iImportType = pImportType;
		session = pSession;
		service = session.getUserService();
		hmCostType = phmCostType;
		hmDateProperty = phmDateProperty;
		dataFile = pdataFile;
		iStartRow = piStartRow;
		dataUtil = new ReadDataUtil(session);
	}
	
	/**
	 * ReadDataFromExcelOperation:: Hyperion
	 * @param File l
	 * @param TCSession
	 * @param HashMap<String, String> Map
	 * @param HashMap<String, String> ·Map
	 * @param Vector<String> СMap
	 * @param ArrayList<String> SheetSheetзΧ
	 * @param String SheetSheetзΧ
	 * @param int PDx汾
	 * @param int PDxProjectId
	 * @param String PDxProjectId
	 * @param String PDxз'y
	 * @param String PDxз
	 * */
	public ReadDataFromFileOperation(File pdataFile, TCSession pSession, HashMap<String, String> phmCostType,
		HashMap<String, String> phmDateProperty,ArrayList<String> plistSameCostType, ArrayList<String> pPDxlistRowRange, 
		String pstrCostInfoDateColRange,int pPdxRowDate,int piPdxProejctIdRow,String pstrPdxProejctIdColName,
		String pstrPdxCostPhaseTypeColName,String pstrPdxCostTypeColName) {
		dataFile = pdataFile;
		session = pSession;
		hmCostType = phmCostType;
		hmDateProperty = phmDateProperty;
		listSameCostType = plistSameCostType;
		listPDxRowRange = pPDxlistRowRange;
		strCostInfoDateColRange = pstrCostInfoDateColRange;
		iRowPdxDate =  pPdxRowDate;
		iPdxProejctIdRow = piPdxProejctIdRow;
		strPdxProejctIdColName = pstrPdxProejctIdColName;	
		strPdxCostPhaseTypeColName = pstrPdxCostPhaseTypeColName;
		strPdxCostTypeColName = pstrPdxCostTypeColName;
		service = session.getUserService();
		isHyperionImport = true;
		dataUtil = new ReadDataUtil(session);
	}
	
	
	/*
	 * @see com.teamcenter.rac.aif.AbstractAIFOperation#executeOperation()
	 */
	@Override
	public void executeOperation() throws Exception {
		dataUtil.setSearchNameKeysForCostInfo(strCostInfoSearchName,arrCostInfokeys,arrCostInfoProgramkeys);
		dataUtil.setSearchNameKeysForTCProject(strProjectSearchName,arrProjectKeys);
		
		ArrayList<String> listCostInfoValue = new ArrayList<String>();
		HashMap<TCComponent,String> hmOldCostTypeValue = new HashMap<TCComponent,String>();
		
		if(isHyperionImport){
			ArrayList<File> listFile = splitFiles(dataFile);
			getExcelForHyperion(listFile,listCostInfoValue,hmOldCostTypeValue);
		}else{
			if(iImportType == 1 || iImportType == 2 ){
				getExcelNonLaborActualData(dataFile,listCostInfoValue,hmOldCostTypeValue);
			}else if(iImportType == 3){
				getQADDataFromFile(dataFile,listCostInfoValue,hmOldCostTypeValue);
			}
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("listCostInfoValue = " + listCostInfoValue);
			logger.debug("hmOldCostTypeValue = " + hmOldCostTypeValue);
		}
		
		if(errorLogFile.length() > 0){
			new MessageDialog(AIFUtility.getCurrentApplication(), errorLogFile);
		}else{
			service.call("open_or_close_pass", new Object[]{1});
			if(hmOldCostTypeValue.size() > 0){
				updateCostInfo(hmOldCostTypeValue);
			}
			if(listCostInfoValue.size() > 0) {
				String arrData[] = new String[listCostInfoValue.size()];
				listCostInfoValue.toArray(arrData);
				Object[] obj = new Object[1];
				obj[0] = arrData;
				try {
					service.call("hyperionimport", obj);
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
			try {
				service.call("open_or_close_pass", new Object[]{0});
			} catch (TCException e) {
				e.printStackTrace();
			}
			MessageBox.post(reg.getString("FinishImport"), "Information", MessageBox.INFORMATION);	
		}
		if(logger.isDebugEnabled()){
			logger.debug("finish importing all data");
		}
	}

	/**
	 * splitFiles::excel"ysheetl
	 * @param File
	 * @return ArrayList<File>
	 * */
	private ArrayList<File> splitFiles(File paramFile){
		ArrayList<File> listFile = new ArrayList<File>();
		
		String[] cmds = new String[3];
		cmds[0] = getTCPortal() + "plugins\\CopyExcelSheet.exe";
		cmds[1] = paramFile.getParent();
		cmds[2] = paramFile.getName();
		Runtime rt = Runtime.getRuntime();
		Process proc;
		try {
			proc = rt.exec(cmds);
			InputStream in = proc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = br.readLine()) != null) {
				listFile.add(new File(line));
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		if(logger.isDebugEnabled()){
			logger.debug("listFile = " + listFile);
		}
		return listFile;
	}
	
	/**
	 * getTCPortal::portal·
	 * */
	public String getTCPortal() {
		String portal_path = System.getenv("TPR"); 
		////System.out.println("portal_path:" + portal_path);
		if (portal_path != null && !portal_path.endsWith("\\"))
			portal_path = portal_path + "\\";
		return portal_path;
	}
	
	
	/**
	 * getExcelForHyperion::Hyperion
	 * @param ArrayList<File>
	 * @param ArrayList<String>  1/2 klist
	 * @param HashMap<TCComponent,String> kMap
	 * */
	private void getExcelForHyperion(ArrayList<File> paramListFile,ArrayList<String> listCostInfoValue,HashMap<TCComponent,String> hmOldCostTypeValue){
		errorLogFile = OriginUtil.createLogFile("Hyperion");
		try{
		//programinfo PDx?
			if(logger.isDebugEnabled()){
				logger.debug("prepare to import data from PDx sheet");
			}
			String strYearMonthColNameStart = "";
			String strYearMonthColNameEnd = "";
			int rowDate = iRowPdxDate;
			if (strCostInfoDateColRange.contains("-")) {
				strYearMonthColNameStart = strCostInfoDateColRange.split("-")[0];
				strYearMonthColNameEnd = strCostInfoDateColRange.split("-")[1];
			} else {
				strYearMonthColNameStart = strCostInfoDateColRange;
				strYearMonthColNameEnd = strCostInfoDateColRange;
			}
			
			int size = paramListFile.size();
			for(int i = 0; i < size; i++){
				File file = paramListFile.get(i);
				Workbook workbook = null;
				try {
					workbook = new XSSFWorkbook(new FileInputStream(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					try {
						workbook = new HSSFWorkbook(new FileInputStream(file));
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				if(workbook != null){
					int isheetcount = workbook.getNumberOfSheets();
					for(int index = 0; index < isheetcount; index++){
						Sheet pdxsheet = workbook.getSheetAt(index);
	
						if(logger.isDebugEnabled()){
							logger.debug("begin to import data from sheet " + pdxsheet.getSheetName());
						}
						
						dataUtil.getCostInfoByYear(listCostInfoValue,hmOldCostTypeValue,pdxsheet, hmCostType, hmDateProperty, strYearMonthColNameStart, strYearMonthColNameEnd,
							rowDate, listPDxRowRange,listSameCostType,iPdxProejctIdRow,strPdxProejctIdColName,strPdxCostPhaseTypeColName,
							strPdxCostTypeColName,dateformat,errorLogFile);	
						
						if(logger.isDebugEnabled()){
							logger.debug("finish importing data from sheet:" + pdxsheet.getSheetName());
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBox.post(reg.getString("Error"), "Information", MessageBox.INFORMATION);
			return;
		}
	}
	
    /**
     * getExcelNonLaborActualData导入非人工实际费用的Actual或Actual_PR数据
     * @param File
     * @param ArrayList<String> 需要新建的费用类型list
     * @param HashMap<TCComponent,String> 已存在的费用类型值Map
     * */
    private void getExcelNonLaborActualData(File file,ArrayList<String> listCostInfoValue,HashMap<TCComponent,String> hmOldCostTypeValue){
        try {
            if(iImportType == 1){
                errorLogFile = OriginUtil.createLogFile("导入非人工实际费用信息错误日志"); 
            }else if(iImportType == 2){
                errorLogFile = OriginUtil.createLogFile("导入非人工实际PR费用信息错误日志");
            }
            
            JacobEReportTool tool = new JacobEReportTool();
            String TC_path = System.getenv("TPR");
            try {
                tool.addDir(TC_path + "\\plugins");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Dispatch sheetsAll = tool.getSheets(file.getAbsolutePath());
            
            Dispatch sheet = Dispatch.invoke(sheetsAll, 
                    "Item", 
                    Dispatch.Get, 
                    new Object[] { new Integer(1) }, 
                    new int[1]).toDispatch(); 
            String sheetName=Dispatch.get(sheet,"Name").toString();//获得sheet的名字 
            sheet = tool.openExcelFile(sheetsAll, sheetName);
                    
            String strCostTypeColNameStart = ""; // 费用类型开始导入的列名
            String strCostTypeColNameEnd = ""; // 费用类型结束导入的列名

            String strProjectIdColName = strActualProjectIdColName; //Actual的项目ID所在的列名
            
            // 获取Actual导入表的数据
            int  startRow = iRowActualStartWith; // 开始导入费用信息的行
            int rowCostType =  iRowActualCostType; // 费用类型名称所在的行
            
            int iRangeSize = listActualColNameRange.size();
            
            if(logger.isDebugEnabled()){
                logger.debug("begin to import data from sheet " + sheetName);
            }
            
            for (int i = 0; i < iRangeSize; i++) {
                String strRange = listActualColNameRange.get(i);
                if (strRange.contains("-")) {
                    strCostTypeColNameStart = strRange.split("-")[0];
                    strCostTypeColNameEnd = strRange.split("-")[1];
                } else {
                    strCostTypeColNameStart = strRange;
                    strCostTypeColNameEnd = strRange;
                }
                if(logger.isDebugEnabled()){
                    logger.debug("prepare to import data from row: " + startRow);
                    logger.debug("prepare to import data from column: " + strCostTypeColNameStart + "to column:" + strCostTypeColNameEnd);
                }
                dataUtil.getActualDataByMonth(tool, sheetName, sheet,listCostInfoValue,hmOldCostTypeValue, hmCostType, hmDateProperty, strCostTypeColNameStart, strCostTypeColNameEnd, 
                    startRow, rowCostType,iActualRowCostPhaseType,strActualCostPhaseTypeColName,iActualRowDate,strActualCellDateColName,strProjectIdColName,
                    errorLogFile,dateformat);
            }
            if(logger.isDebugEnabled()){
                logger.debug("finish importing data from sheet:" + sheetName);
            }   
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
	
	/**
	 * getQADData~oQAD
	 * @param File
	 * @param ArrayList<String>  1/2 klist
	 * @param HashMap<TCComponent,String> kMap
	 * */
	private void getQADDataFromFile(File file,ArrayList<String> listCostInfoValue,HashMap<TCComponent,String> hmOldCostTypeValue){
		try {
			errorLogFile = OriginUtil.createLogFile("QAD");	
			// QAD
			dataUtil.getQADData(file, listCostInfoValue,hmOldCostTypeValue,hmCostType, hmDateProperty, dateformat, iStartRow, errorLogFile);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * updateCostInfo::update costinfo component
	 * @param HashMap<TCComponent,String>
	 */
	private void updateCostInfo(HashMap<TCComponent,String> hmOldCostInfo) {
		try {
			Iterator it = hmOldCostInfo.keySet().iterator();
			while(it.hasNext()){
				TCComponent costInfoComp = (TCComponent) it.next();
				costInfoComp.lock();
				String strMonthPropValue = hmOldCostInfo.get(costInfoComp);
				if(logger.isDebugEnabled()){
					logger.debug("strMonthPropValue = " + strMonthPropValue);
				}
				if(strMonthPropValue.contains("|")){
					String[] arrValue = strMonthPropValue.split("\\|");
					for(int i = 0 ; i < arrValue.length; i++){
						String[] arrPropValue = arrValue[i].split("=");
						costInfoComp.setDoubleProperty(arrPropValue[0], Double.parseDouble(arrPropValue[1]));
					}
				}else{
					String[] arrPropValue = strMonthPropValue.split("=");
					costInfoComp.setDoubleProperty(arrPropValue[0], Double.parseDouble(arrPropValue[1]));
				}
				costInfoComp.save();
				costInfoComp.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
