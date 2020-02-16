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
 #	2013-3-1	liuc  		Ini		��������								   
 #=============================================================================
 */

package com.yfjcebp.importdata.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Utilities;

public class OriginUtil {
	
//	private static Registry reg = Registry.getRegistry(OriginUtil.class);
	private static final String BURFLR_NAME = "com.yfjcebp.importdata.utils.utils_locale";
	private static final ResourceBundle reg = ResourceBundle.getBundle(BURFLR_NAME);
	/**
	 * ��ѯ������
	 * 
	 * @return
	 */
	public static InterfaceAIFComponent[] searchComponentsCollection(TCSession session, String searchName, String[] keys, String[] values) {
		// ��Ϣ���
		InterfaceAIFComponent[] result = new InterfaceAIFComponent[0];
		try {
			TCTextService textService = session.getTextService();
			TCComponentQueryType querytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
			TCComponentQuery query = (TCComponentQuery) querytype.find(searchName);
			if (query == null) {				
				MessageBox.post(reg.getString("searchCreate")  + searchName + "!", "INFO", MessageBox.INFORMATION);			
				return null;
			}
			querytype.clearCache();
			String[] as = new String[keys.length];
			for (int i = 0; i < keys.length; i++) {
				//as[i] = textService.getTextValue(keys[i]);
				as[i] =keys[i];
				if(as[i].startsWith("!!!")){
					as[i]=keys[i];
				}
			}

			String[] as1 = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				//as1[i] = textService.getTextValue(values[i]);
				as1[i] =values[i];
				
				if(as1[i].startsWith("!!!")){
					as1[i]=values[i];
				}
			}
			//query.clearCache();
			TCComponentContextList list = query.getExecuteResultsList(as, as1);
			if (list != null) {
				int count = list.getListCount();
				result = new InterfaceAIFComponent[count];
				for (int i = 0; i < count; i++) {
					result[i] = list.get(i).getComponent();
				}
			}
		} catch (TCException e) {
			MessageBox.post(reg.getString("searchError"), "INFO", MessageBox.ERROR);
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * ������־�ļ�����������ʱĿ¼��
	 * 
	 * @return
	 */
	public static File createLogFile(String strFileName) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String dirStr = System.getenv("temp")+"/SiemensLog";
		File dirFile = new File(dirStr);
		if(!dirFile.exists()){
			dirFile.mkdir();
		}
		Date date = new Date();
		String time = format.format(date);
		String fileStr = dirStr +"/" + strFileName + time+".txt";
		//System.out.println("log�ļ��� fileStr = " + fileStr);
		File file = new File(fileStr);
		if(file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
			return file;
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * StringBufferDemo:: д��־��Ϣ
	 * @param String
	 * @param File
	 * */
	public static void StringBufferDemo(String strContext, File logFile) {
		try{
			FileOutputStream out = new FileOutputStream(logFile, true);
			StringBuffer sb = new StringBuffer(strContext);
			sb.append("\r\n");
			out.write(sb.toString().getBytes("gb2312"));
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * checkFile::���ѡ�����ݼ��ļ��Ƿ���ȷ
	 * @param InterfaceAIFComponent
	 * @return File
	 */
	public static File checkImportFile(InterfaceAIFComponent target) {
		File file = null;
		if(!(target.getType().equals("MSExcelX"))){
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("SelectCorrectFileToImport"),"Information",MessageBox.WARNING);
			return null;
		}
		TCComponentDataset dataset = (TCComponentDataset) target;
		try {
			TCComponentTcFile tcfile[] = dataset.getTcFiles();
			if(tcfile == null || tcfile.length ==0){
				MessageBox.post(Utilities.getCurrentFrame(),reg.getString("NoNameReferenceFile"),"Information",MessageBox.WARNING);
				return null;
			}
			String strPath = System.getenv("TEMP");
			file = tcfile[0].getFile(strPath);
		} catch (TCException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * checkFile::���ѡ�����ݼ��ļ��Ƿ���ȷ
	 * @param InterfaceAIFComponent
	 * @param ArrayList<String>
	 * @return File
	 */
	public static File checkImportFile(InterfaceAIFComponent target,ArrayList<String> listCheckType) {
		File file = null;
		if(!listCheckType.contains(target.getType())){
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("SelectCorrectFileToImport"),"Information",MessageBox.WARNING);
			return null;
		}
		TCComponentDataset dataset = (TCComponentDataset) target;
		try {
			TCComponentTcFile tcfile[] = dataset.getTcFiles();
			if(tcfile == null || tcfile.length ==0){
				MessageBox.post(Utilities.getCurrentFrame(),reg.getString("NoNameReferenceFile"),"Information",MessageBox.WARNING);
				return null;
			}
			String strPath = System.getenv("TEMP");
			file = tcfile[0].getFile(strPath);
		} catch (TCException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	
	/**
	 * getPreferenceMap::�����ѡ����ƻ�ȡ��ѡ���е�ֵ
	 * @param Registry
	 * @param TCPreferenceService
	 * @param String
	 * @return HashMap<String,String>
	 * */
	public static HashMap<String,String> getPreferenceMap(TCPreferenceService preference,String pPreferenceName){
		HashMap<String,String> hmData = new HashMap<String,String>();
		String[] arrOptions = preference.getStringArray(4,pPreferenceName);
		if(arrOptions != null && arrOptions.length > 0){
			int len = arrOptions.length;
			String[] arrValue = null;
			for(int i = 0; i < len; i++){
				if(arrOptions[i].contains("=")){
					arrValue = arrOptions[i].split("=");
					hmData.put(arrValue[0],arrValue[1]);
				}
			}
		}else{
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + pPreferenceName + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
		}
		return hmData;
	}
	
	/**
	 * getPreferenceList::�����ѡ����ƻ�ȡ��ѡ���е�ֵ
	 * @param TCPreferenceService
	 * @param String
	 * @return ArrayList<String>
	 * */
	public static ArrayList<String> getPreferenceList(TCPreferenceService preference,String pPreferenceName){
		ArrayList<String> lstData = new ArrayList<String>();
		String[] arrOptions = preference.getStringArray(4,pPreferenceName);
		if(arrOptions != null && arrOptions.length > 0){
			int len = arrOptions.length;
			for(int i = 0; i < len; i++){
				lstData.add(arrOptions[i]);
			}
		}else{
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + pPreferenceName + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
		}
		return lstData;
	}
	
	/**
	 * getPreferenceListWithLov::�����ѡ����ƻ�ȡ��ѡ���е�ֵ
	 * @param TCSession session
	 * @param TCPreferenceService
	 * @param String
	 * @return ArrayList<String>
	 * */
	public static ArrayList<String> getPreferenceListWithLov(TCSession session,TCPreferenceService preference,String pPreferenceName,String pLovName){
		String[] arrLovValue = null;
		try {
			TCComponentListOfValuesType dtype = (TCComponentListOfValuesType) session.getTypeComponent("ListOfValues");
			TCComponentListOfValues[] tmpcom = dtype.find(pLovName);
			arrLovValue = tmpcom[0].getListOfValues().getStringListOfValues();
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lstData = new ArrayList<String>();
		String[] arrOptions = preference.getStringArray(4,pPreferenceName);
		if(arrOptions != null && arrOptions.length > 0){
			int len = arrOptions.length;
			for(int i = 0; i < len; i++){
				for(int j = 0; j < arrLovValue.length; j++){
					if(arrLovValue[j].equals(arrOptions[i])){
						lstData.add(arrLovValue[j]);
					}
				}
			}
		}else{
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + pPreferenceName + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
		}
		return lstData;
	}
	
	/**
	 * getMapWithLOV::����ѡ��ֵ���LOV��ʾ���ȡMap
	 * @param TCSession 
	 * @param TCPreferenceService 
	 * @param String  preference name
	 * @param String lov name
	 * @return HashMap<String,String> result
	 * */
	public static HashMap<String,String> getMapWithLOV(TCSession session,TCPreferenceService preference,String pPreferenceName,String pLovName){
		String[] arrLovValue = null;
		try {
			TCComponentListOfValuesType dtype = (TCComponentListOfValuesType) session.getTypeComponent("ListOfValues");
			TCComponentListOfValues[] tmpcom = dtype.find(pLovName);
			arrLovValue = tmpcom[0].getListOfValues().getStringListOfValues();
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		HashMap<String,String> hmData = new HashMap<String,String>();
		String[] arrCostType = preference.getStringArray(4,pPreferenceName);
		if(arrCostType != null && arrCostType.length > 0){
			int len = arrCostType.length;
			String[] arrValue;
			for(int i = 0; i < len; i++){
				if(arrCostType[i].contains("=")){
					//DV Testing=DV_TEST;DV Test
					arrValue = arrCostType[i].split("=");
					for(int j = 0; j < arrLovValue.length; j++){
						if(arrLovValue[j].equals(arrValue[0])){
							hmData.put(arrLovValue[j],arrValue[1]);		
						}
					}
				}
			}
		}
		if(hmData.size() == 0){
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + pPreferenceName + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
		}
		return hmData;
	}
	
	
	
	/**
	 * getPreferenceValue::��ȡ������ѡ��ֵ
	 * @param TCSession 
	 * @param Registry
	 * @param TCPreferenceService 
	 * @param String  preference name
	 * @return String
	 * */
	public static String getPreferenceValue(TCPreferenceService preference,String pPreferenceName){
		String strValue = preference.getString(4, pPreferenceName);
		if(strValue == null || strValue.length() == 0){
			MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + pPreferenceName + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
			return "";
		}
		return strValue;
	}
	
}
