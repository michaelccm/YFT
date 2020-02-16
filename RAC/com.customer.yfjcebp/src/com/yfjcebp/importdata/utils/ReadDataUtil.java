/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: ReadExcelUtil.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-3-4	liqz  		Ini		��										   
#	2013-7-12	liqz		Modify	uQADl
#=============================================================================
*/
package com.yfjcebp.importdata.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;

import com.jacob.com.Dispatch;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.Registry;
import com.yfjcebp.projectmanager.budget.dialogs.JacobEReportTool;
/**
 * @author liqz
 * @modigy zhanggl remove judge itemRevision_id getCostInfoByYear function
 */
public class ReadDataUtil {
	private TCUserService			service;
	private TCSession session;
	private String[] arrCostInfoKeys = null;
	private String[] arrCostInfoKeysByProgramInfo = null;
	private String[] arrProjectKeys = null;
	private static Logger logger = Logger.getLogger(ReadDataUtil.class);
	private Registry reg = Registry.getRegistry(this);
	private String strSearchNameForCostInfo = "";
	private String strSearchNameForProject = "";
	private TCComponentItemType itemType ;
	private String strTimeStamp = "";
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
	private DecimalFormat numformat = new DecimalFormat("#.##");
	
	private String []columns = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V",
			"W","X","Y","Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ"};
	
	public ReadDataUtil(TCSession pSession){
		session = pSession;
		try {
			itemType = (TCComponentItemType) session.getTypeComponent("Item");
			strTimeStamp = format.format(new Date());
		} catch (TCException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * getActualDataByMonth::��excel�н������
     * @param Sheet �����sheet
     * @param HashMap<String, String> ��������Map
     * @param ArrayList<String> ��Ҫ�´����ķ�����ϢList
     * @param HashMap<TCComponent,String> �Ѵ��ڵķ�����ϢֵMap,
     * @param HashMap<String, String> �·�����Map
     * @param String ����ķ������ʹ����п�ʼ
     * @param String ����ķ������͵����н���
     * @param int �����п�ʼ�������
     * @param int �����������ڵ���
     * @param int ���ý׶��������ڵ���
     * @param String ���ý׶��������ڵ�����
     * @param int �������ڵ���
     * @param int �������ڵ�����
     * @param String ��ĿID���ڵ�����
     * @param File errorLogFile
     * @param SimpleDateFormat
     */

public void getActualDataByMonth(JacobEReportTool tool, String sheetName, Dispatch sheet,
        ArrayList<String> listCostInfoValue,
        HashMap<TCComponent, String> hmOldCostTypeValue,
        HashMap<String, String> hmCostType,
        HashMap<String, String> hmDateProperty,
        String strCostTypeColNameStart, String strCostTypeColNameEnd,
        int startRow, int rowCostType, int rowCostPhaseType,
        String strCostPhaseTypeColName, int rowDate, String strDateColName,
        String strProjectIdColName, File errorLogFile,
        SimpleDateFormat dateformat) {

        String strCostPhaseType = ""; // ���ý׶�����
        String strYear = ""; // ��
        String strMonthPropName = ""; // ��

        //rowCostPhaseType;  //���ý׶�����������
        int cellCostPhaseType = colNameToNum(strCostPhaseTypeColName); //���ý׶�����������
        
        //rowDate;      //�������ڵ���
        int cellDate = colNameToNum(strDateColName); //�������ڵ���
        
        int cellProjectId = colNameToNum(strProjectIdColName); //��ĿID���ڵ���
        
        int startCol = colNameToNum(strCostTypeColNameStart); //�����п�ʼ����
        int endCol = colNameToNum(strCostTypeColNameEnd); //���뵽���н���
        
        if(logger.isDebugEnabled()){
           // logger.debug("getActualDataByMonth:: ����������͵��дӣ�" + startCol + "�� " + endCol);
        }
        
        if(logger.isDebugEnabled()){
           // logger.debug("getActualDataByMonth:: ���ڵ���:" + rowDate);
          //  logger.debug("getActualDataByMonth:: ���ڵ���:" + cellDate);
        }
        
        strCostPhaseType = tool.getDataFromExcel(NumToString(cellCostPhaseType), rowCostPhaseType, sheet);//1��2
        try {
           // logger.debug("getActualDataByMonth:: rowDate:" + rowDate);
           // logger.debug("getActualDataByMonth:: cellDate:" + cellDate);
            String strTempValue = dateformat.format(new Date(tool.getDataFromExcel(NumToString(cellDate), rowDate, sheet)));
            //logger.debug("getActualDataByMonth:: strTempValue:" + strTempValue);
            strYear = strTempValue.substring(0, 4);
            String strTempMonth = strTempValue.substring(5, strTempValue.length());
            strMonthPropName = hmDateProperty.get(strTempMonth);
        } catch (Exception ex) {
            ex.printStackTrace();
            String strError = reg.getString("ErrorDate") + sheetName +  ex.getMessage();
            OriginUtil.StringBufferDemo(strError, errorLogFile);
        }

        String[] values = new String[4];
        values[1] = strCostPhaseType;
        values[3] = strYear;

        int ifail = startRow;
        Dispatch userRange=Dispatch.call(sheet, "UsedRange").toDispatch();//��ȡExcelʹ�õ�sheet 
        Dispatch row=Dispatch.call(userRange, "Rows").toDispatch(); 
        int rowCnt=Dispatch.get(row,"Count").getInt();//excel��ʹ�õ�����
        try {
            service = session.getUserService();
            for (int i = startRow; i < rowCnt; i++) {
                ifail = i;
                // ��ȡ��Ŀid
                values[0] = tool.getDataFromExcel(NumToString(cellProjectId), i, sheet);
                logger.debug("project id ==========" + values[0]);
                if(values[0] == null || values[0].equals("")){
                    continue;
                }
                
                service.call("open_or_close_pass", new Object[]{1});
                TCComponentItem programInfoItem = itemType.find(values[0]);
                if(programInfoItem == null){
                    OriginUtil.StringBufferDemo(values[0] + ":" + reg.getString("ProgramInfoNotExist") + reg.getString("InSheet") + sheetName, errorLogFile);
                }
                
                //�����Ŀ�Ƿ����ϵͳ��
                InterfaceAIFComponent[] projects = OriginUtil.searchComponentsCollection(session, strSearchNameForProject, arrProjectKeys, new String[]{values[0]});
                if(projects == null || projects.length == 0){
                    OriginUtil.StringBufferDemo(values[0] + ":" + reg.getString("ProjectNotExist") + " " + reg.getString("Row") + i + reg.getString("InSheet") + sheetName, errorLogFile);
                }
                service.call("open_or_close_pass", new Object[]{0});
                
                for (int col = startCol; col <= endCol; col++) {
                    // ��ȡÿ�ַ������͵�ֵ
                    double dCostValue = 0;
                    String data_key_1 = tool.getDataFromExcel(NumToString(col), i, sheet);
                    if (data_key_1.equals("0.0")){
                        continue;
                    }else {
                        try {
                            dCostValue = Double.parseDouble(data_key_1.trim());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            OriginUtil.StringBufferDemo(values[0] + "," + reg.getString("ErrorOccur") + " " + reg.getString("Row") + i + reg.getString("Column")  + columns[col-1] + " "+ reg.getString("InSheet") + sheetName, errorLogFile);
                            continue;
                        }
                    }

                    if (dCostValue == 0) {
                        continue;
                    }

                    if(logger.isDebugEnabled()){
                        logger.debug("getActualDataByMonth:: ���������Ϣ�� �����������ڵ��� = " + rowCostType + ";��=" + col);
                    }
                    
                    // ��������
                    String strExcelCostType = tool.getDataFromExcel(NumToString(col), rowCostType, sheet);
                    String strCostType = getCostType(hmCostType,strExcelCostType);
                    values[2] = strCostType;
                    
                    if(logger.isDebugEnabled()){
                        logger.debug("getActualDataByMonth:: ���������Ϣ�� Excel�������� = " +strExcelCostType );
                        logger.debug("getActualDataByMonth:: ���������Ϣ�� ϵͳ�������� = " +strCostType );
                    }
                    
                    if(strCostType.equals("")){
                        OriginUtil.StringBufferDemo(values[0] + "," + reg.getString("ErrorOccur") + " " + reg.getString("Row") + i + reg.getString("Column") + columns[col-1] + " "+ reg.getString("InSheet") + sheetName + reg.getString("CostTypeNotFound"), errorLogFile);
                        continue;
                    }
                    
                    // {"project_id","jci6_CPT","jci6_CostType","jci6_Year"};
                    InterfaceAIFComponent[] aifcomp = OriginUtil.searchComponentsCollection(session, strSearchNameForCostInfo, arrCostInfoKeys, values);
                    if (aifcomp == null || aifcomp.length == 0) {
                        StringBuffer strPropertyValue = new StringBuffer(100);
                        strPropertyValue.append(values[0]).append("_").append(values[1]).append("_").append(values[2]).append("_").append(values[3]).append("_").append(strTimeStamp); //�������
                        strPropertyValue.append("|");
                        strPropertyValue.append(values[0]); // project_id
                        strPropertyValue.append("|");
                        strPropertyValue.append("NULL"); // project �汾
                        strPropertyValue.append("|");
                        strPropertyValue.append(values[1]); // ���ý׶�
                        strPropertyValue.append("|");
                        strPropertyValue.append(values[3]); // ��
                        strPropertyValue.append("|");
                        strPropertyValue.append(values[2]); // ��������
                        strPropertyValue.append("|");
                        strPropertyValue.append(strMonthPropName).append("=").append(dCostValue);
                        listCostInfoValue.add(strPropertyValue.toString());
                    } else {
                        // ����ϵͳ���еķ�����Ϣ����
                        StringBuffer bf = new StringBuffer(100);
                        TCComponent component = (TCComponent)aifcomp[0];
                        bf.append(strMonthPropName).append("=").append(dCostValue);
                        hmOldCostTypeValue.put(component, bf.toString());
                    }
                }
                if(logger.isDebugEnabled()){
                    logger.debug(values[0] + "��Success import data from the row:" + i);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            OriginUtil.StringBufferDemo(reg.getString("ErrorOccur") + reg.getString("Row") + ifail + reg.getString("InSheet")+ sheetName, errorLogFile);
        }
    }   

	public void getActualDataByMonth(String xlsFilePath, Sheet sheet_poi,ArrayList<String> listCostInfoValue,HashMap<TCComponent,String> hmOldCostTypeValue, HashMap<String, String> hmCostType, HashMap<String, String> hmDateProperty,
		String strCostTypeColNameStart, String strCostTypeColNameEnd, int startRow, int rowCostType,int rowCostPhaseType,
		String strCostPhaseTypeColName,int rowDate,String strDateColName,String strProjectIdColName,
		File errorLogFile,SimpleDateFormat dateformat) {

		String strCostPhaseType = ""; // 'y
		String strYear = ""; // 
		String strMonthPropName = ""; // 

		startRow = startRow - 1;
		rowCostType = rowCostType - 1;
		
		rowCostPhaseType = rowCostPhaseType - 1;  //'y
		int cellCostPhaseType = colNameToNum(strCostPhaseTypeColName) - 1; //'y
		
		rowDate = rowDate - 1;		//
		int cellDate = colNameToNum(strDateColName) - 1; //
		
		int cellProjectId = colNameToNum(strProjectIdColName) - 1; //LID
		
		int startCol = colNameToNum(strCostTypeColNameStart) - 1; //�ᡯ
		int endCol = colNameToNum(strCostTypeColNameEnd) - 1; //��
		
		if(logger.isDebugEnabled()){
			logger.debug("getActualDataByMonth:: ��" + startCol + " " + endCol);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("getActualDataByMonth:: :" + cellDate);
		}
		
		int ifail = startRow;
		JacobEReportTool tool = new JacobEReportTool();
		String TC_path = System.getenv("TPR");
		try {
            tool.addDir(TC_path + "\\plugins");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		Dispatch sheetsAll = tool.getSheets(xlsFilePath);
		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_poi.getSheetName());
		
		strCostPhaseType = sheet_poi.getRow(rowCostPhaseType).getCell(cellCostPhaseType).getStringCellValue().trim();  //12
		try {
			Cell cell = sheet_poi.getRow(rowDate).getCell(cellDate);  //22
			if (DateUtil.isCellDateFormatted(cell)) {
				String strTempValue = dateformat.format(cell.getDateCellValue());
				strYear = strTempValue.substring(0, 4);
				String strTempMonth = strTempValue.substring(5, strTempValue.length());
				strMonthPropName = hmDateProperty.get(strTempMonth);
			} else {
				Date dtTemp = dateformat.parse(cell.getStringCellValue().trim());
				String strTempValue = dateformat.format(dtTemp);
				strYear = strTempValue.substring(0, 4);
				String strTempMonth = strTempValue.substring(5, strTempValue.length());
				strMonthPropName = hmDateProperty.get(strTempMonth);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			String strError = reg.getString("ErrorDate") + sheet_poi.getSheetName() +  ex.getMessage();
			OriginUtil.StringBufferDemo(strError, errorLogFile);
		}

		String[] values = new String[4];
		values[1] = strCostPhaseType;
		values[3] = strYear;

		int rowCnt = sheet_poi.getPhysicalNumberOfRows();
		try {
			
			for (int i = startRow; i < rowCnt; i++) {
				ifail = i;
				// Lid
				values[0] = tool.getDataFromExcel(NumToString(cellProjectId+1), i, sheet);
				//values[0] = sheet.getRow(i).getCell(cellProjectId).getStringCellValue().trim();
				if(values[0] == null || values[0].equals("")){
					continue;
				}
				
				TCComponentItem programInfoItem = itemType.find(values[0]);
				if(programInfoItem == null){
					OriginUtil.StringBufferDemo(values[0] + ":" + reg.getString("ProgramInfoNotExist") + reg.getString("InSheet") + sheet_poi.getSheetName(), errorLogFile);
				}
				
				//L��
				InterfaceAIFComponent[] projects = OriginUtil.searchComponentsCollection(session, strSearchNameForProject, arrProjectKeys, new String[]{values[0]});
				if(projects == null || projects.length == 0){
					OriginUtil.StringBufferDemo(values[0] + ":" + reg.getString("ProjectNotExist") + " " + reg.getString("Row") + (i + 1) + reg.getString("InSheet") + sheet_poi.getSheetName(), errorLogFile);
				}
				
				for (int col = startCol; col <= endCol; col++) {
					// "y
					double dCostValue = 0;
					String data_key_1 = tool.getDataFromExcel("NumToString(col+1)", i, sheet);
					logger.debug("data_key_1 = " + data_key_1);
					if (data_key_1.equals("null")||data_key_1.equals("[None]")||data_key_1.equals("-")){
						continue;
					}else {
						try {
							dCostValue = Double.parseDouble(data_key_1.trim());
						} catch (Exception ex) {
							ex.printStackTrace();
							OriginUtil.StringBufferDemo(values[0] + "," + reg.getString("ErrorOccur") + " " + reg.getString("Row") + (i + 1) + reg.getString("Column")  + columns[col] + " "+ reg.getString("InSheet") + sheet_poi.getSheetName(), errorLogFile);
							continue;
						}
					}

					if (dCostValue == 0) {
						continue;
					}

					if(logger.isDebugEnabled()){
						logger.debug("getActualDataByMonth::   = " + (rowCostType + 1) + ";=" + (col + 1));
					}
					
					// 
					String strExcelCostType = sheet_poi.getRow(rowCostType).getCell(col).getStringCellValue().trim();
					String strCostType = getCostType(hmCostType,strExcelCostType);
					values[2] = strCostType;
					
					if(logger.isDebugEnabled()){
						logger.debug("getActualDataByMonth::  Excel = " +strExcelCostType );
						logger.debug("getActualDataByMonth::  �� = " +strCostType );
					}
					
					if(strCostType.equals("")){
						OriginUtil.StringBufferDemo(values[0] + "," + reg.getString("ErrorOccur") + " " + reg.getString("Row") + (i + 1) + reg.getString("Column") + columns[col] + " "+ reg.getString("InSheet") + sheet_poi.getSheetName() + reg.getString("CostTypeNotFound"), errorLogFile);
						continue;
					}
					
					// {"project_id","jci6_CPT","jci6_CostType","jci6_Year"};
					InterfaceAIFComponent[] aifcomp = OriginUtil.searchComponentsCollection(session, strSearchNameForCostInfo, arrCostInfoKeys, values);
					if (aifcomp == null || aifcomp.length == 0) {
						StringBuffer strPropertyValue = new StringBuffer(100);
						strPropertyValue.append(values[0]).append("_").append(values[1]).append("_").append(values[2]).append("_").append(values[3]).append("_").append(strTimeStamp); //
						strPropertyValue.append("|");
						strPropertyValue.append(values[0]); // project_id
						strPropertyValue.append("|");
						strPropertyValue.append("NULL"); // project ��
						strPropertyValue.append("|");
						strPropertyValue.append(values[1]); // 'y
						strPropertyValue.append("|");
						strPropertyValue.append(values[3]); // 
						strPropertyValue.append("|");
						strPropertyValue.append(values[2]); // 
						strPropertyValue.append("|");
						strPropertyValue.append(strMonthPropName).append("=").append(dCostValue);
						listCostInfoValue.add(strPropertyValue.toString());
					} else {
						// �ŧ�k
						StringBuffer bf = new StringBuffer(100);
						TCComponent component = (TCComponent)aifcomp[0];
						bf.append(strMonthPropName).append("=").append(dCostValue);
						hmOldCostTypeValue.put(component, bf.toString());
					}
				}
				if(logger.isDebugEnabled()){
					logger.debug(values[0] + "Success import data from the row:" + (i + 1));
				}
			}
		}catch (Exception ex) {
			ex.printStackTrace();
			OriginUtil.StringBufferDemo(reg.getString("ErrorOccur") + reg.getString("Row") + (ifail + 1) + reg.getString("InSheet")+ sheet_poi.getSheetName(), errorLogFile);
		}
	}

	/**
	 * getCostInfoByYear:: 
	 * @param ArrayList<String>  1/2 klist
	 * @param HashMap<TCComponent,String> kMap
	 * @param Sheet sheet
	 * @param HashMap<String, String> Map
	 * @param HashMap<String, String> ��Map
	 * @param String �ᡯ
	 * @param String ��
	 * @param int 
	 * @param ArrayList<String> ��k��
	 * @param ArrayList<String> ����
	 * @param int LID
	 * @param String LID
	 * @param String 'y
	 * @param String 
	 * @param SimpleDateFormat
	 * @param File errorLogFile
	 */
	@SuppressWarnings("deprecation")
	public void getCostInfoByYear(ArrayList<String> listCostInfoValue,HashMap<TCComponent,String> hmOldCostTypeValue,Sheet sheet, HashMap<String, String> hmCostType, HashMap<String, String> hmDateProperty,
		String strYearMonthColNameStart, String strYearMonthColNameEnd, int rowDate, ArrayList<String> listRowRange, 
		ArrayList<String> listSameCostType,int iPdxProejctIdRow,String strPdxProejctIdColName,String strCostPhaseTypeColName,
		String strCostTypeColName,SimpleDateFormat dateformat,File errorLogFile) {
		
		int iProjectIdRow = iPdxProejctIdRow - 1;
		int iProjectIdCol = colNameToNum(strPdxProejctIdColName) - 1;
		
		if(logger.isDebugEnabled()){
			logger.debug("iProjectIdRow = " + iProjectIdRow);
			logger.debug("iProjectIdCol = " + iProjectIdCol);
		}
		
		String strProgramIdRev = sheet.getRow(iProjectIdRow).getCell(iProjectIdCol).getStringCellValue().trim();
		int iRevIndex = strProgramIdRev.indexOf("_");
		String strProgramId = strProgramIdRev.substring(0,iRevIndex);
		String strProgramRev = strProgramIdRev.substring(iRevIndex + 1);
		strProgramId = strProgramId.toUpperCase();
		strProgramRev = strProgramRev.toUpperCase();
		
		int iCostPhaseTypeCol = colNameToNum(strCostPhaseTypeColName) -1 ;
		int iCostTypeCol =  colNameToNum(strCostTypeColName) -1 ;
		
		int startDateCol = colNameToNum(strYearMonthColNameStart) - 1;
		int endDateCol = colNameToNum(strYearMonthColNameEnd) - 1;
		rowDate = rowDate - 1; // 

		int size = listRowRange.size();
		int startRow;
		int endRow;

		try{
			service = session.getUserService();
			service.call("open_or_close_pass", new Object[]{1});
			TCComponentItem programInfoItem = itemType.find(strProgramId);
			if(programInfoItem == null){
				OriginUtil.StringBufferDemo(strProgramId + ":" + reg.getString("ProgramInfoNotExist")+"," + reg.getString("InSheet") + sheet.getSheetName(), errorLogFile);
			}
			service.call("open_or_close_pass", new Object[]{0});
//			TCComponentItemRevision programInfoItemRev = itemRevType.findRevision(strProgramId, strProgramRev);
//			if(programInfoItemRev == null){
//				OriginUtil.StringBufferDemo(strProgramIdRev + ":" + reg.getString("ProgramInfoRevNotExist")+"," + reg.getString("InSheet") + sheet.getSheetName(), errorLogFile);
//			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		InterfaceAIFComponent[] projects = OriginUtil.searchComponentsCollection(session, strSearchNameForProject, arrProjectKeys, new String[]{strProgramId});
		if(projects == null || projects.length == 0){
			OriginUtil.StringBufferDemo(strProgramId + ":" + reg.getString("ProjectNotExist")+ "," + reg.getString("InSheet") + sheet.getSheetName(), errorLogFile);
		}
		
		HashMap<String,Double> hmSameCostTypeValue = new HashMap<String,Double>();
		HashMap<String,String> hmCostTypeValue = new HashMap<String,String>();
		
		for (int i = 0; i < size; i++) {
			// "o�֦�
			String strRowRange = listRowRange.get(i);
			if (!strRowRange.contains("-")) {
				startRow = Integer.parseInt(strRowRange) - 1;
				endRow = startRow;
			} else {
				String[] arrRowRange = strRowRange.split("-");
				startRow = Integer.parseInt(arrRowRange[0]) - 1;
				endRow = Integer.parseInt(arrRowRange[1]) - 1;
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("Begin to import data from row:" + (startRow + 1) + "to row:"+ (endRow + 1));
				logger.debug("Begin to import data from column from:" + startDateCol + "to column :" + endDateCol);
			}
			
			boolean isOk = getDataByRowRange(hmSameCostTypeValue,hmCostTypeValue,hmOldCostTypeValue,sheet, startRow, endRow, 
				startDateCol, endDateCol, rowDate, hmCostType, hmDateProperty, strProgramId, strProgramRev, 
				listSameCostType,iCostPhaseTypeCol,iCostTypeCol,dateformat,errorLogFile);
			if(isOk){
				if(logger.isDebugEnabled()){
					logger.debug("success to import data from row:" + (startRow + 1) + "to row:"+ (endRow + 1));
				}
			}
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("hmSameCostTypeValue  = " + hmSameCostTypeValue);
			logger.debug("hmCostTypeValue  = " + hmCostTypeValue);
		}
		
		InterfaceAIFComponent[] aifcomp = null;
		Iterator it = hmSameCostTypeValue.keySet().iterator();
		//CSA1304~PD2~Actual~Tool~2013~jci6_Jan
		HashMap<String,TCComponent> hmKeyComp = new HashMap<String,TCComponent>();
		while(it.hasNext()){
			String strKey = it.next().toString();
			int lastIndex = strKey.lastIndexOf("~");
			
			String strCostValueKey = strKey.substring(0,lastIndex);
			String strMonthProp = strKey.substring(lastIndex + 1);
			
			//
			aifcomp = null;
			if(!hmKeyComp.containsKey(strCostValueKey)){
				//CSA1304~PD2~Actual~Tool~2013
				String[] arrCostKey = strCostValueKey.split("~");
				if(arrCostKey[1].equals("NULL")){
					String[] arrValue = new String[4];
					arrValue[0] = arrCostKey[0];
					arrValue[1] = arrCostKey[2];
					arrValue[2] = arrCostKey[3];
					arrValue[3] = arrCostKey[4];
					aifcomp = OriginUtil.searchComponentsCollection(session, strSearchNameForCostInfo, arrCostInfoKeys, arrValue);
				}else{
					// "L ID","��", "jci6_CPT", "jci6_CostType" ,"jci6_Year"};
					aifcomp = OriginUtil.searchComponentsCollection(session, strSearchNameForCostInfo, arrCostInfoKeysByProgramInfo, arrCostKey);
				}
				if(aifcomp != null && aifcomp.length > 0){
					hmKeyComp.put(strCostValueKey, (TCComponent)aifcomp[0]);
				}
			}
			if(hmKeyComp.containsKey(strCostValueKey)){
				StringBuffer bf = new StringBuffer(100);
				TCComponent component = hmKeyComp.get(strCostValueKey);
				if(hmOldCostTypeValue.containsKey(component)){
					bf.append(hmOldCostTypeValue.get(component));
					bf.append("|");
					bf.append(strMonthProp).append("=").append(numformat.format(hmSameCostTypeValue.get(strKey)));
				}else{
					bf.append(strMonthProp).append("=").append(numformat.format(hmSameCostTypeValue.get(strKey)));
				}
				hmOldCostTypeValue.put(component, bf.toString());
			}else{
				if(hmCostTypeValue.containsKey(strCostValueKey)){
					StringBuffer bf = new StringBuffer(100);
					bf.append(hmCostTypeValue.get(strCostValueKey));
					bf.append("|");
					bf.append(strMonthProp).append("=").append(numformat.format(hmSameCostTypeValue.get(strKey)));
					hmCostTypeValue.put(strCostValueKey,bf.toString());
				}else{
					hmCostTypeValue.put(strCostValueKey,strMonthProp + "=" + numformat.format(hmSameCostTypeValue.get(strKey)));
				}
			}
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("22 hmCostTypeValue  = " + hmCostTypeValue);
			logger.debug(" hmKeyComp  = " + hmKeyComp);
		}
		
		Iterator itCost = hmCostTypeValue.keySet().iterator();
		while(itCost.hasNext()){
			String strKey = itCost.next().toString();
			String[] arrProp = strKey.split("~");
			StringBuffer strPropertyValue = new StringBuffer(100);
			
			//cost name 
			strPropertyValue.append(arrProp[0]).append("_").append(arrProp[2]).append("_").append(arrProp[3]).append("_").append(arrProp[4]).append("_").append(strTimeStamp);
			strPropertyValue.append("|");
			strPropertyValue.append(arrProp[0]).append("|"); //Programid
			strPropertyValue.append(arrProp[1]).append("|"); //programrev
			strPropertyValue.append(arrProp[2]).append("|"); //cost phase type
			strPropertyValue.append(arrProp[4]).append("|"); //year
			strPropertyValue.append(arrProp[3]).append("|"); //cost type
			strPropertyValue.append(hmCostTypeValue.get(strKey));	//monthproperty and cost value
			listCostInfoValue.add(strPropertyValue.toString());
		}
	}


	/**
	 * getDataByRowRange::~o��
	 * @param HashMap<String,Double> k��Map
	 * @param HashMap<String,String> ukMap
	 * @param HashMap<TCComponent,String> kMap
	 * @param Sheet sheet
	 * @param int �ᡯ
	 * @param int ��
	 * @param int �ᡯ
	 * @param int ��
	 * @param int 
	 * @param HashMap<String, String> Map
	 * @param HashMap<String, String> ��Map
	 * @param String LID
	 * @param String L�� 
	 * @param Vector<String> ����
	 * @param int 'y
	 * @param int 
	 * @param SimpleDateFormat
	 * @param File errorLogFile
	 * @return boolean
	 */
	public boolean getDataByRowRange(HashMap<String,Double> hmSameCostTypeValue,HashMap<String,String> hmCostTypeValue,HashMap<TCComponent,String> hmOldCostTypeValue,
		Sheet sheet,int startRow, int endRow, int startDateCol, int endDateCol, int rowDate, HashMap<String, String> hmCostType,
		HashMap<String,String> hmDateProperty, String strProgramId,String strProgramRev, ArrayList<String> listSameCostType,
		int iCostPhaseTypeCol,int iCostTypeCol,SimpleDateFormat dateformat,File errorLogFile) {
		
		String strYear = "";
		String strMonthPropName = "";
		String[] values = new String[4];
		
		// ~o�§�
		String strCurRowCostType = "";
		int ifail = startRow;
		try {
			for (int row = startRow; row <= endRow; row++) {
				ifail = row;
				String strExcelCostType = sheet.getRow(row).getCell(iCostTypeCol).getStringCellValue().trim();
				if (strCurRowCostType.trim().length() == 0) {
					// ^j��
					strCurRowCostType = strExcelCostType;
				} else if (strExcelCostType.trim().length() != 0) {
					// ��
					strCurRowCostType = strExcelCostType;
				}
				String strCostType = getCostType(hmCostType,strCurRowCostType);
				if(strCostType.equals("")){
					OriginUtil.StringBufferDemo(values[0] + "," + reg.getString("ErrorOccur") + " " + reg.getString("Row") + (row + 1) + " " + reg.getString("InSheet") + sheet.getSheetName() + reg.getString("CostTypeNotFound"), errorLogFile);
					continue;
				}
				
				String strTempPhaseType = sheet.getRow(row).getCell(iCostPhaseTypeCol).getStringCellValue().trim().trim();
				String strPhaseType = getCostPhaseType(strTempPhaseType);
				
				values[0] = strProgramId;
				values[1] = strPhaseType;
				values[2] = strCostType;

				for (int col = startDateCol; col <= endDateCol; col++) {
					Cell cellDate = sheet.getRow(rowDate).getCell(col);
					String[] arrYearMonth = getYearMonth(hmDateProperty,dateformat,cellDate);
					if(arrYearMonth != null){
						strYear = arrYearMonth[0];
						strMonthPropName = arrYearMonth[1];	
					}
					if(arrYearMonth == null ||  strYear.equals("") || strMonthPropName.equals("")){
						String strError = reg.getString("ErrorDate") + sheet.getSheetName();
						OriginUtil.StringBufferDemo(strError , errorLogFile);
						continue;
					}
					values[3] = strYear;

					double dCostValue = 0;
					Cell celldata = sheet.getRow(row).getCell(col);
					int celltype = celldata.getCellType();
					if (celltype == Cell.CELL_TYPE_BLANK || celltype == Cell.CELL_TYPE_ERROR) {
						continue;
					} else if (celltype == Cell.CELL_TYPE_NUMERIC) {
						dCostValue = celldata.getNumericCellValue();
					} else if (celltype == Cell.CELL_TYPE_STRING) {
						try {
							dCostValue = Double.parseDouble(celldata.getStringCellValue().trim());
						} catch (Exception ex1) {
							String strError = reg.getString("ErrorOccur");
							OriginUtil.StringBufferDemo(strError+ reg.getString("Row") + (row + 1) + reg.getString("Column") + columns[col] + " "+ reg.getString("InSheet")+ sheet.getSheetName() +":data in it is not correct" , errorLogFile);
							continue;
						}
					}
					if (dCostValue == 0) {
						continue;
					}

					boolean isBudget = false;
					if (strPhaseType.equalsIgnoreCase("Budget")){
						isBudget = true;
					}
					
					StringBuffer bufKey = new StringBuffer();
					bufKey.append(strProgramId);
					bufKey.append("~");
					if (isBudget) {
						bufKey.append(strProgramRev); // project ��
					} else {
						bufKey.append("NULL"); 						
					}
					bufKey.append("~");
					bufKey.append(strPhaseType);
					bufKey.append("~");
					bufKey.append(strCostType);
					bufKey.append("~");
					bufKey.append(strYear);
					
					if (listSameCostType.contains(strCostType)) {
						bufKey.append("~").append(strMonthPropName);
						if(hmSameCostTypeValue.containsKey(bufKey.toString())){
							dCostValue = dCostValue + hmSameCostTypeValue.get(bufKey.toString());
						}
						hmSameCostTypeValue.put(bufKey.toString(), dCostValue);
					}else{
						String strCostValue = numformat.format(dCostValue); //����
						
						// ��'yCostInfo^o�� 1/2 CostInfo
						InterfaceAIFComponent[] aifcomp = null;
						if(!isBudget){
							aifcomp = OriginUtil.searchComponentsCollection(session, strSearchNameForCostInfo, arrCostInfoKeys, values);
						}else{
							String[] arrProgram = new String[5];
							arrProgram[0] = strProgramId;
							arrProgram[1] = strProgramRev;
							arrProgram[2] = strPhaseType;
							arrProgram[3] = strCostType;
							arrProgram[4] = strYear;
							aifcomp = OriginUtil.searchComponentsCollection(session, strSearchNameForCostInfo, arrCostInfoKeysByProgramInfo, arrProgram);
						}
						if(aifcomp == null || aifcomp.length == 0){
							StringBuffer strNewMonthProp = new StringBuffer(100);
							if(hmCostTypeValue.containsKey(bufKey.toString())){
								strNewMonthProp.append(hmCostTypeValue.get(bufKey.toString())).append("|").append(strMonthPropName).append("=").append(strCostValue);
							}else{
								strNewMonthProp.append(strMonthPropName).append("=").append(strCostValue);
							}
							hmCostTypeValue.put(bufKey.toString(), strNewMonthProp.toString());
						}else{
							// k
							StringBuffer strMonthProp = new StringBuffer(100);
							TCComponent component = (TCComponent)aifcomp[0];
							if(hmOldCostTypeValue.containsKey(component)){
								strMonthProp.append(hmOldCostTypeValue.get(component));
								strMonthProp.append("|");
								strMonthProp.append(strMonthPropName).append("=").append(strCostValue);
							}else{
								strMonthProp.append(strMonthPropName).append("=").append(strCostValue);
							}
							hmOldCostTypeValue.put(component, strMonthProp.toString());
						}
					}
				}
				if(logger.isDebugEnabled()){
					logger.debug("Success import data in row:" + (row + 1));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			String strError = reg.getString("ErrorOccur");
			OriginUtil.StringBufferDemo(strError + reg.getString("Row") + (ifail + 1)  + reg.getString("InSheet") + sheet.getSheetName() + ex.getMessage(), errorLogFile);
			return false;
		}
		return true;
	}
	
	/**
	 * getQADData::QADl
	 * @param File file  
	 * @param ArrayList<String> listCostInfoValue
	 * @param HashMap<TCComponent,String> hmOldCostTypeValue
	 * @param HashMap<String, String> hmCostType
	 * @param HashMap<String,String>hmDateProperty
	 * @param SimpleDateFormat dateFormat
	 * @param int �ᡯ
	 * @param File errorLogFile
	 * */
	public ArrayList<String> getQADData(File file,ArrayList<String> listCostInfoValue,HashMap<TCComponent,String> hmOldCostTypeValue,HashMap<String, String> hmCostType,HashMap<String,String>hmDateProperty,
		SimpleDateFormat dateFormat,int iStartRow,File errorLogFile){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			String strCostPhaseType = "Actual";
			String[] arrValue = null;
			
			reader = new BufferedReader(new FileReader(file));
			iStartRow = iStartRow - 1;
			int row = 0;
			while((line = reader.readLine())!= null){
//				if(row == 1){
//					/*========Added by liqz on 20130627 Begin========*/
//					int data = Integer.parseInt(line);
//					int iYearNum = 0;
//					int iMonthNum = 0;
//					iYearNum = data/12;
//					iMonthNum = data%12;
//					if(iMonthNum == 0){
//						iYearNum = iYearNum - 1;
//						iMonthNum = 12;
//					}
//					logger.info("iYearNum = " + iYearNum);
//					logger.info("iMonthNum = " + iMonthNum);
//					
//					strYear = (2006 + iYearNum) +"";
//					strMonthPropName = hmDateProperty.get((iMonthNum + ""));
//					/*========Added by liqz on 20130627 End========*/
//				}else
				if(row >= iStartRow){
					if(line.contains(";")){
						/*=======Modified by liqz on 20130712 Begin======*/
						arrValue = line.split(";");
						//Actual;2013;Jun            ;Periodic;CSA0246E_PD2   ;RMB;DOM_TRA        ;[icp none];[NONE];[NONE];[NONE];[NONE];           1074.00
						if(arrValue != null && arrValue.length == 13){
							String strYear = arrValue[1].trim();
							String strMonthPropName = "jci6_" + arrValue[2].trim();
							String strProgramInfo = arrValue[4].trim();
							String strCostTypeValue = arrValue[6].trim();
							String strCostValue = arrValue[12].trim();
							
							/*=======Modified by liqz on 20130712 End======*/
							
							String strProgramInfoId = "";
							if(strProgramInfo.contains("_")){
								int index = strProgramInfo.indexOf("_");
								strProgramInfoId = strProgramInfo.substring(0,index);
							}else{
								strProgramInfoId = strProgramInfo; 
							}
							strProgramInfoId = strProgramInfoId.toUpperCase();
							double dCostValue = Double.parseDouble(strCostValue);
							if(dCostValue == 0){
								row ++;
								continue;
							}
							service = session.getUserService();
							service.call("open_or_close_pass", new Object[]{1});
							TCComponentItem programInfoItem = itemType.find(strProgramInfoId);
							if(programInfoItem == null){
								OriginUtil.StringBufferDemo(strProgramInfoId + ":" + reg.getString("ProgramInfoNotExist") + " " + reg.getString("Row") + (row + 1), errorLogFile);
								row ++;
							}
							InterfaceAIFComponent[] projects = OriginUtil.searchComponentsCollection(session, strSearchNameForProject, arrProjectKeys, new String[]{strProgramInfoId});
							if(projects == null || projects.length == 0){
								OriginUtil.StringBufferDemo(strProgramInfoId + ":" + reg.getString("ProjectNotExist") + " " + reg.getString("Row") + (row + 1), errorLogFile);
								row ++;
							}
							
							String strCostType = getCostType(hmCostType,strCostTypeValue.trim());
							if(strCostType.equals("")){
								OriginUtil.StringBufferDemo(strProgramInfoId + "," + reg.getString("ErrorOccur") + " " + reg.getString("Row") + (row + 1) + " " + reg.getString("CostTypeNotFound"), errorLogFile);
								row ++;
							}
							
							service.call("open_or_close_pass", new Object[]{0});
							
							InterfaceAIFComponent[] aifcomp = null;
							aifcomp = OriginUtil.searchComponentsCollection(session, strSearchNameForCostInfo, arrCostInfoKeys,
								new String[]{strProgramInfoId,strCostPhaseType,strCostType,strYear});
							if(aifcomp == null || aifcomp.length == 0){
								StringBuffer strPropertyValue = new StringBuffer(100);
								strPropertyValue.append(strProgramInfoId).append("_").append(strCostPhaseType).append("_").append(strCostType).append("_").append(strYear).append("_").append(strTimeStamp); //
								strPropertyValue.append("|");
								strPropertyValue.append(strProgramInfoId); // project_id
								strPropertyValue.append("|");
								strPropertyValue.append("NULL"); // project ��
								strPropertyValue.append("|");
								strPropertyValue.append(strCostPhaseType); // 'y
								strPropertyValue.append("|");
								strPropertyValue.append(strYear); // 
								strPropertyValue.append("|");
								strPropertyValue.append(strCostType); // 
								strPropertyValue.append("|");
								strPropertyValue.append(strMonthPropName).append("=").append(dCostValue);
								listCostInfoValue.add(strPropertyValue.toString());
							}else{
								StringBuffer strMonthProp = new StringBuffer(100);
								TCComponent component = (TCComponent)aifcomp[0];
								strMonthProp.append(strMonthPropName).append("=").append(dCostValue);
								hmOldCostTypeValue.put(component, strMonthProp.toString());
							}
						}else{
							OriginUtil.StringBufferDemo(reg.getString("DataFormatIsNotCorrect") + " " + reg.getString("Row") + (row + 1), errorLogFile);
						}
					}else{
						OriginUtil.StringBufferDemo(reg.getString("DataFormatIsNotCorrect") + " " + reg.getString("Row")+ (row + 1), errorLogFile);
					}
				}
				row ++;
			}
			reader.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return listCostInfoValue;
	}
	
	
	/**
	 * getYearMonth::P
	 * @param HashMap<String,String> hmDateProperty
	 * @param SimpleDateFormat dateformat
	 * @param Cell cellDate
	 * @return String[]
	 */
	private String[] getYearMonth(HashMap<String, String> hmDateProperty, SimpleDateFormat dateformat, Cell cellDate) {
		String[] arrYearMonth = new String[2];
		String strYear = "";
		String strMonthPropName = "";
		if (Cell.CELL_TYPE_NUMERIC == cellDate.getCellType()) {
			if (DateUtil.isCellDateFormatted(cellDate)) {
				String strTempValue = dateformat.format(cellDate.getDateCellValue());
				strYear = strTempValue.substring(0, 4);
				String strTempMonth = strTempValue.substring(5, strTempValue.length());
				strMonthPropName = hmDateProperty.get(strTempMonth);
			} else {
				Date dtTemp;
				try {
					dtTemp = dateformat.parse(cellDate.getNumericCellValue() + "");
					String strTempValue = dateformat.format(dtTemp);
					strYear = strTempValue.substring(0, 4);
					String strTempMonth = strTempValue.substring(5, strTempValue.length());
					strMonthPropName = hmDateProperty.get(strTempMonth);
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			}
		} else if (Cell.CELL_TYPE_STRING == cellDate.getCellType()) {
			Date dtTemp;
			try {
				dtTemp = dateformat.parse(cellDate.getStringCellValue().trim());
				String strTempValue = dateformat.format(dtTemp);
				strYear = strTempValue.substring(0, 4);
				String strTempMonth = strTempValue.substring(5, strTempValue.length());
				strMonthPropName = hmDateProperty.get(strTempMonth);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		arrYearMonth[0] = strYear;
		arrYearMonth[1] = strMonthPropName;
		return arrYearMonth;
	}

	/**
	 * getCostType:: excel��k�ŧ�k
	 * @param HashMap<String,String>
	 * @param String
	 * @return String
	 */
	public String getCostType(HashMap<String, String> hmCostType, String strExcelCostType) {
		Iterator it = hmCostType.keySet().iterator();
		while (it.hasNext()) {
			String strRealCostType = it.next().toString();
			String strOptionCostType = hmCostType.get(strRealCostType);
			if (strOptionCostType.contains(";")) {
				String[] arrType = strOptionCostType.split(";");
				for (int j = 0; j < arrType.length; j++) {
					if (arrType[j].equalsIgnoreCase(strExcelCostType)) {
						return strRealCostType;
					}
				}
			} else if (strOptionCostType.equalsIgnoreCase(strExcelCostType)) {
				return strRealCostType;
			}
		}
		return "";
	}

	/**
	 * getCostPhaseType::'y
	 * @param String excel��L
	 * @return String L
	 * */
	private String getCostPhaseType(String pCostPhaseType){
		String strCostPhaseType = pCostPhaseType;
		if(pCostPhaseType.equalsIgnoreCase("Forecast")){
			strCostPhaseType = "Budget";
		}
		return strCostPhaseType;
	}

	/**
	 * colNameToNum::
	 * @param String AB
	 * @return int 
	 */
	public int colNameToNum(String colName) {
		int result = 0;
		for (int i = 0; i < colName.length(); i++) {
			result = result * 26 + colName.charAt(i) - 65 + 1;
		}
		return result;
	}

	/**
	 * setSearchNameForCostInfo:: ��IJ
	 * @param String 
	 * @param String[] 1
	 * @param String[] 2
	 */
	public void setSearchNameKeysForCostInfo(String pSearchNameForCostInfo, String[] pCostInfoKeys,String[] pCostInfoKeysByProgramInfo) {
		strSearchNameForCostInfo = pSearchNameForCostInfo;
		arrCostInfoKeys = pCostInfoKeys;
		arrCostInfoKeysByProgramInfo = pCostInfoKeysByProgramInfo;
	}

	/**
	 * setSearchCostInfoName:: ��TC_ProjectIJ
	 * @param String 
	 * @param String[] 
	 */
	public void setSearchNameKeysForTCProject(String pSearchNameForTCProject, String[] pProjectKeys) {
		strSearchNameForProject = pSearchNameForTCProject;
		arrProjectKeys = pProjectKeys;
	}
	
	// ,excel�֧�
	private String NumToString(int Num) {
		String str = "";
		String strReturn = "";
		if (Num == 0)
			return "";

		float f = (Num % 26 == 0) ? 26 : Num % 26;
		switch ((int) f) {
		case 1:
			str = "A";
			break;
		case 2:
			str = "B";
			break;
		case 3:
			str = "C";
			break;
		case 4:
			str = "D";
			break;
		case 5:
			str = "E";
			break;
		case 6:
			str = "F";
			break;
		case 7:
			str = "G";
			break;
		case 8:
			str = "H";
			break;
		case 9:
			str = "I";
			break;
		case 10:
			str = "J";
			break;
		case 11:
			str = "K";
			break;
		case 12:
			str = "L";
			break;
		case 13:
			str = "M";
			break;
		case 14:
			str = "N";
			break;
		case 15:
			str = "O";
			break;
		case 16:
			str = "P";
			break;
		case 17:
			str = "Q";
			break;
		case 18:
			str = "R";
			break;
		case 19:
			str = "S";
			break;
		case 20:
			str = "T";
			break;
		case 21:
			str = "U";
			break;
		case 22:
			str = "V";
			break;
		case 23:
			str = "W";
			break;
		case 24:
			str = "X";
			break;
		case 25:
			str = "Y";
			break;
		case 26:
			str = "Z";
			break;
		}

		if (Num >= 26) {
			str = NumToString((Num / 26) - ((f == 26) ? 1 : 0)) + str;
		}

		strReturn = str;
		return strReturn;
	}
	
	public static void main(String[] args){
		String strValue = "Actual;2013;Jun            ;Periodic;CSA0246E_PD2   ;RMB;DOM_TRA        ;[icp none];[NONE];[NONE];[NONE];[NONE];           1074.00";
		String[] arrValue = strValue.split(";");
		//System.out.println("arrValue.length = " + arrValue.length);
		String strYear = arrValue[1].trim();
		String strMonthPropName = "jci6_" + arrValue[2].trim();
		String strProgramInfo = arrValue[4].trim();
		String strCostTypeValue = arrValue[6].trim();
		String strCostValue = arrValue[12].trim();
		
		//System.out.println("strYear = " + strYear);
		//System.out.println("strMonthPropName = " + strMonthPropName);
		//System.out.println("strProgramInfo = " + strProgramInfo);
		//System.out.println("strCostTypeValue = " + strCostTypeValue);
		//System.out.println("strCostValue = " + strCostValue);
		
	}
}
