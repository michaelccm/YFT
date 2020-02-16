/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: ImportTimeLogOperation.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-6-24	liuc  	Ini		初始化										   
#=============================================================================
*/
package com.yfjcebp.importdata.importtimelog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjcebp.importdata.utils.MessageDialog;
import com.yfjcebp.importdata.utils.OriginUtil;
import com.yfjcebp.importdata.utils.POIExcelParser;

public class ImportTimeLogOperation extends AbstractAIFOperation{
	
	private SimpleDateFormat			dateFormat;

	private boolean						isFindItem		= true;
	private boolean						isFindItemRev	= true;
	private static Logger logger = Logger.getLogger(ImportTimeLogHandler.class);
	private  Registry reg = Registry.getRegistry(ImportTimeLogHandler.class);
	private HashMap costInfoDataMap ;
	
	private List sheet0ProgramInfoRev;
	
	private StringBuffer buff;
	
	private String []columns = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V",
			"W","X","Y","Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ"};
	
	private  TCSession  				session;
	private  AbstractAIFUIApplication	app;
	private  File 						file;
	public ImportTimeLogOperation(File file,TCSession session){
		this.app = AIFUtility.getCurrentApplication();
		this.session = session;
		this.file=file ;
	}
	/* 
	 * @see com.teamcenter.rac.aif.AbstractAIFOperation#executeOperation()
	 */
	@Override
	public void executeOperation() throws Exception {
		
		dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		costInfoDataMap=new HashMap();
		buff = new StringBuffer("");

		File logFile = OriginUtil.createLogFile("Timelog");
		PrintWriter pw = null;
		try {
			 
			pw = new PrintWriter(new FileOutputStream(logFile), false);
			POIExcelParser parser = new POIExcelParser(file);
			int sheetNum = parser.getSheetNum();
			parser.setInit(0, 0, 1);
			List<String> line = null;
			StringBuffer sb = new StringBuffer();
			StringBuffer sbName = new StringBuffer();
			List listProgramInfoRev = new ArrayList();
			String itemID = "";
			while ((line = parser.parseLine()) != null) {
				itemID = line.get(0);
				sb.delete(0, sb.length());
				String sheetName=line.get(4);
				if(!sheetName.equals("")&& parser.getSheet(sheetName)==null){
					pw.println(" do not find sheetName: "+sheetName);
				}
				if (checkExcelData1(parser.startRow, line, pw, sb)) {
					listProgramInfoRev.add(sb.toString());
				}
			}
			pw.flush();
			if (!isFindItem) {
				MessageBox.post(reg.getString("findItem.error"), "WARNING", 1);
				pw.close();
				return;
			}
			
			if(logger.isDebugEnabled())
				System.out.println("listdata program==" + listProgramInfoRev);

			for (int i = 1; i < sheetNum; i++) {
				HashMap servicedataMap=new HashMap();
				List listName = new ArrayList();
				List list = new ArrayList();
				String revID = "NULL";
				parser.setInit(i, 0, 1);
				String sheetName = parser.getSheetName();
				String jci6_CPT = "";
				if (sheetName.equals(reg.getString("timelogSheetName.labourForecast"))) {
					jci6_CPT = "Forecast";
				} else if (sheetName.equals(reg.getString("timelogSheetName.actual"))) {
					jci6_CPT = "Actual";
				} else {
					revID = sheetName;
					jci6_CPT = "Budget";
				}
				String programInfo = itemID + "|" + revID + "|" + jci6_CPT;
				if(logger.isDebugEnabled())
					System.out.println("programInfo==" + programInfo);

				list.clear();
				while ((line = parser.parseLine()) != null) {
					sb.delete(0, sb.length());
					sbName.delete(0, sbName.length());
					sbName.append(itemID);
					sbName.append("_");
					sbName.append(jci6_CPT);
					
					if (checkExcelData2(parser.startRow,sheetName, line, pw, sb,sbName)) {				
						listName.add(sbName.toString());
						list.add(sb.toString());
					}
				}
				pw.flush();
				//if(logger.isDebugEnabled())
				System.out.println("listdata cost==" + list);
				servicedataMap.put("programInfo", programInfo);
				servicedataMap.put("costInfoNames", listName);
				String dsName=createDs(session,list);
				servicedataMap.put("dsName", dsName);
				costInfoDataMap.put(i,servicedataMap);
			}
			pw.flush();
			pw.close();

			boolean isDoimport = true;
			if(logFile.length()>1){
				new MessageDialog(logFile);
			}else{
				if (!buff.toString().equals("")) {
					buff.append(reg.getString("importTimeLogDialog.text"));
//					ConfirmDialog confirmDialog = new ConfirmDialog(null, "Information", buff.toString());
//					confirmDialog.open();
//					if (!confirmDialog.isOkayClicked()) {
//						isDoimport = false;
//					}
					int n=JOptionPane.showConfirmDialog(null,buff.toString(),"Information",JOptionPane.YES_NO_OPTION);
					if(n==1)
						isDoimport = false;
					
				}
				if(isDoimport){
					String dsName1=createDs(session,listProgramInfoRev);
					Object obj[] = new Object[1];
					obj[0] = dsName1;
					TCUserService service = session.getUserService();
					service.call("createProgramInfoRev", obj);
					
					try {
						Thread.sleep(2*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} // 创建完项目信息版本，暂停2秒
					System.out.println("enter creat for call");
					for (int i = 1; i < sheetNum; i++) {
						
						HashMap servicedataMap= (HashMap) costInfoDataMap.get(i);
						String programInfo=(String) servicedataMap.get("programInfo");
						List listName=(List) servicedataMap.get("costInfoNames");
						String dsName=(String) servicedataMap.get("dsName");
						
						String objectNames[] = new String[listName.size()]; // 费用信息名称
						listName.toArray(objectNames);
						Object obj2[] = new Object[3];
						obj2[0] = programInfo;
						obj2[1] = objectNames;
						obj2[2] = dsName;
						
						service.call("importTimeLog", obj2);				
					}
				}
			}
		} catch (TCException e) {
			// TODO Auto-generated constructor stub
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated constructor stub
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated constructor stub
			e.printStackTrace();
		}
		pw.flush();
		pw.close();
		
	}
	
	/**
	 * 检验Excel sheet1 数据是否正确
	 * @return
	 */

	public boolean checkExcelData1(int row, List<String> line, PrintWriter writer, StringBuffer sb) {
		boolean flag = true;

		for (int i = 0; i < line.size(); i++) {
			String str = line.get(i);
			if (i == 0) {
				String key[] = { "ItemID" };
				String value[] = { str };
				System.out.println("ItemID= "+str);
				InterfaceAIFComponent[] aifcom = OriginUtil.searchComponentsCollection(session,reg.getString("itemQueryName"), key, value);
				System.out.println("aifcom.length= "+aifcom.length);
				if (aifcom.length < 1) {
					flag = false;
					isFindItem = false;
					writer.println("from row:"+row + "column :" + columns[i] + "do not find item");
				}
			}

			if (i == 3) {
				try {
					double b = Double.parseDouble(str);
				} catch (Exception e) {
					flag = false;
					writer.println("from row:"+row + "column :" + columns[i] + "is not double ");
				}
			}
			if (str.equals("")) {
				str = "NULL";
			}
			sb.append(str);
			if (i != line.size() - 1) {
				sb.append("|");
			}
		}
		return flag;
	}
	
	
	/**
	 * 检验Excel 其他sheet 数据是否正确
	 * @return
	 */

	public boolean checkExcelData2(int row, String sheetName ,List<String> line, PrintWriter writer, StringBuffer sb,StringBuffer sbName) {
		boolean flag = true;
		
		String division="";
		String year="";
		String manager="";
		String position []={"Manager","Lead Engineer","Senior Engineer","Engineer","Assistant Engineer","Engineer Assistant","ExtSupporter"};
		for (int i = 0; i < line.size(); i++) {
			String str = line.get(i);			
			if (i == 0) {
				division=str;
				if(str.contains("Out Source")){
					;
				}else{
					String key[] = { "Name" };
					String value[] = { str };
					InterfaceAIFComponent[] aifcom = OriginUtil.searchComponentsCollection(session,  reg.getString("groupQueryName"), key, value);
					if (aifcom.length < 1) {
						flag = false;
						writer.println(sheetName +"  from row:"+row + "column :" + columns[i] + "not found  group");
					}else{
						try {
							if(!aifcom[0].getProperty("object_name").equals(str)){
								flag = false;
								writer.println(sheetName +"  from row:"+row + "column :" + columns[i] + "not found  group");
							}
						} catch (Exception e) {
							// TODO Auto-generated constructor stub
							e.printStackTrace();
						}
					}		
				}
			}
			if (i ==1) {
				boolean isFind=false;
					for(int j=0;j<position.length;j++) {
						if(position[j].equals(str)){
							isFind=true;
							break;
						}
					}
					if(isFind==false){
						writer.println(sheetName +"  from row:"+row + "column :" + columns[i] + "not found  position");	
					}
			}
			
			if(i==1){
				manager=str;
			}
			
			if(sbName.toString().endsWith("Budget")){
				if(i==2){
					year=str;
				}
			}else{
				if(i==4){
					year=str;
				}
			}
//			if (i == 1) { //根据学科名字找不到学科 用查询构建器  
//					String key[] = { "discipline_name" };
//					String value[] = { str };
//					InterfaceAIFComponent[] aifcom = OriginUtil.searchComponentsCollection(session,  reg.getString("disciplineQueryName"), key, value);
//					if (aifcom.length < 1) {
//						flag = false;
//						writer.println(sheetName +"  from row:"+row + "column :" + i + "not found discipline");
//					}
//			}
			if(sheetName.equals(reg.getString("timelogSheetName.labourForecast")) 
				|| sheetName.equals(reg.getString("timelogSheetName.actual"))){
				if (i == 3) {
					if(division.contains("Out Source")){
						;
					}else{
						if(str.equals("")){
							buff.append(sheetName).append("  from row:").append(row).append("column :").append(columns[i]).append("\n");
						}
						else{
							String key[] = { "PA7" };
							String value[] = { str };
							InterfaceAIFComponent[] aifcom = OriginUtil.searchComponentsCollection(session,  reg.getString("userQueryName"), key, value);
							if (aifcom.length < 1) {
								flag = false;
								writer.println(sheetName +"  from row:"+row + "column :" + columns[i] + "not found  user");
							}
						}
					}
				}
			}
			
			if (str.equals("")) {
				str = "NULL";
			}
			sb.append(str);
			if (i != line.size() - 1) {
				sb.append("|");
			}
		}
		
		sbName.append("_");
		sbName.append(year);

		sbName.append("_");
		sbName.append(division);

		sbName.append("_");
		sbName.append(manager);
		
		return flag;
	}
	
	private static String createDs(TCSession session,List list) throws TCException, IOException{
		String fileName=UUID.randomUUID().toString();

		String fileStr = System.getenv("temp") +"/" + fileName+".txt";
		System.out.println("ds文件： fileStr = " + fileStr);
		File file = new File(fileStr);
		file.createNewFile();
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(file), false);
		
		for(int i=0;i<list.size();i++){
			
			pw.println(list.get(i).toString());
		}
		pw.flush();
		pw.close();
		
	
		TCTypeService service = session.getTypeService();
		TCComponentDatasetType datasetType = (TCComponentDatasetType) service.getTypeComponent("Dataset");
		TCComponentDataset tcd = datasetType.create(fileName, "description","Text");
		String[] arrTargetName = { fileStr };
		String[] type={"Text"}; //excel
		tcd.setFiles(arrTargetName,type);
				
		return fileName;
	}
	
//	private String createDs(TCSession session,List list) throws TCException, IOException{
//		String fileName=UUID.randomUUID().toString();
//
//		String fileStr = System.getenv("temp") +"/" + fileName+".txt";
//		System.out.println("ds文件： fileStr = " + fileStr);
//		File file = new File(fileStr);
//		file.createNewFile();
//		OutputStreamWriter writer =new OutputStreamWriter(new FileOutputStream(file),"utf-8");
//
//		for(int i=0;i<list.size();i++){
//			writer.write(list.get(i).toString());
//			writer.write("\r\n");
//		}
//		writer.flush();
//		writer.close();
//	
//		TCTypeService service = session.getTypeService();
//		TCComponentDatasetType datasetType = (TCComponentDatasetType) service.getTypeComponent("Dataset");
//		TCComponentDataset tcd = datasetType.create(fileName, "description","Text");
//		String[] arrTargetName = { fileStr };
//		String[] type={"Text"}; //excel
//		tcd.setFiles(arrTargetName,type);
//		
//		return fileName;
//	}

}
