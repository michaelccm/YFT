/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: ImportProgramOperation.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-6-24	liuc  	Ini		初始化										   
#=============================================================================
*/
package com.yfjcebp.importdata.importprogram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjcebp.importdata.utils.MessageDialog;
import com.yfjcebp.importdata.utils.OriginUtil;
import com.yfjcebp.importdata.utils.POIExcelParser;


public class ImportProgramOperation extends AbstractAIFOperation{
	
	private SimpleDateFormat			dateFormat;
	private static Logger				logger	= Logger.getLogger(ImportProgramHandler.class);
	private Registry					reg		= Registry.getRegistry(ImportProgramHandler.class);

	private TCComponentListOfValuesType	lovType	= null;
	private List						JCI6_CustomerName;
	private List						JCI6_ProgramState;
	private List						JCI6_Category;

	private List						JCI6_ProgramInfoType;
	private List						JCI6_Product;
	private StringBuffer				buff;

	private Map<String, String>			map;

	private Map<Integer, String>		colmap;
	
	private TCSession    				session ;
	private File						file;
	
	
	public ImportProgramOperation(File file,TCSession session){
		this.session=session;
		this.file=file;
	}
	
	
	@Override
	public void executeOperation() throws Exception {

		map = getMapOfProNameAndColName();
		if (map == null) {
			MessageBox.post("Preference YFJC_ImportProgramInfo_Properties_Mapping is not exist or has not value!", "Warning", MessageBox.WARNING);
			return;
		}
		try {
			lovType = (TCComponentListOfValuesType) session.getTypeComponent("ListOfValues");
			TCComponentListOfValues[] tmpcom = lovType.find("JCI6_CustomerName");
			String values[] = tmpcom[0].getListOfValues().getStringListOfValues();
			JCI6_CustomerName = Arrays.asList(values);

			tmpcom = lovType.find("JCI6_ProgramState");
			values = tmpcom[0].getListOfValues().getStringListOfValues();
			JCI6_ProgramState = Arrays.asList(values);

			tmpcom = lovType.find("JCI6_Category");
			values = tmpcom[0].getListOfValues().getStringListOfValues();
			JCI6_Category = Arrays.asList(values);

			tmpcom = lovType.find("JCI6_ProgramInfoType");
			values = tmpcom[0].getListOfValues().getStringListOfValues();
			JCI6_ProgramInfoType = Arrays.asList(values);

			tmpcom = lovType.find("JCI6_Product");
			values = tmpcom[0].getListOfValues().getStringListOfValues();
			JCI6_Product = Arrays.asList(values);

			// pattern = Pattern.compile("[0-9]*");
			//
			// tmpcom = lovType.find("JCI6_ProgramIDType");
			// values = tmpcom[0].getListOfValues().getStringListOfValues();
			// JCI6_ProgramIDType = Arrays.asList(values);
			//
			// tmpcom = lovType.find("JCI6_Name");
			// values = tmpcom[0].getListOfValues().getStringListOfValues();
			// JCI6_Name = Arrays.asList(values);
			//
			// tmpcom = lovType.find("JCI6_ESTMIC");
			// values = tmpcom[0].getListOfValues().getStringListOfValues();
			// JCI6_ESTMIC = Arrays.asList(values);

		} catch (TCException e1) {
			// TODO Auto-generated constructor stub
			e1.printStackTrace();
		}

		dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		File logFile = OriginUtil.createLogFile("programInfo");
		PrintWriter pw = null;
		colmap = new HashMap<Integer, String>();
		colmap.put(0, "A");
		colmap.put(1, "B");
		colmap.put(2, "C");
		colmap.put(3, "D");
		colmap.put(4, "E");
		colmap.put(5, "F");
		colmap.put(6, "G");
		colmap.put(7, "H");
		colmap.put(8, "I");
		colmap.put(9, "J");
		colmap.put(10, "K");
		colmap.put(11, "L");
		colmap.put(12, "M");
		colmap.put(13, "N");
		colmap.put(14, "O");
		colmap.put(15, "P");
		colmap.put(16, "Q");
		colmap.put(17, "R");
		colmap.put(18, "S");
		colmap.put(19, "T");
		colmap.put(20, "U");
		colmap.put(21, "V");
		colmap.put(22, "W");
		colmap.put(23, "X");
		colmap.put(24, "Y");
		colmap.put(25, "Z");

		System.out.println("===read excel ==");
		try {
			pw = new PrintWriter(new FileOutputStream(logFile), false);
			POIExcelParser parser = new POIExcelParser(file);
			
			parser.setInit(0);
			parser.setInit(parser.getSheet().getRow(1).getPhysicalNumberOfCells(), 2);
			List<String> line = null;
			List<String> list = new ArrayList<String>();
			StringBuffer sb = new StringBuffer("\n\t");
			buff = new StringBuffer("");
			while ((line = parser.parseLine()) != null) {
				//System.out.println("175 line=="+line);
				sb.delete(0, sb.length());
				if (checkExcelData(parser, parser.startRow, line, pw, sb)) {
					list.add(sb.toString());
				}
			}

			pw.flush();
			pw.close();
			System.out.println("listdata321==" + list);
 
			boolean isDoimport = true;
			if (logFile.length() > 1) {
				new MessageDialog(logFile);
			} else {
				if (!buff.toString().equals("")) {
					buff.append(reg.getString("confirmDialog.text"));
//					ConfirmDialog confirmDialog = new ConfirmDialog(AIFUtility.getCurrentApplication().getDesktop().getShell(), "Information", buff.toString());
//					confirmDialog.open();
//					if (!confirmDialog.isOkayClicked()) {
//						isDoimport = false;
//					}
					int n=JOptionPane.showConfirmDialog(null,buff.toString(),"Information",JOptionPane.YES_NO_OPTION);
					if(n==1)
						isDoimport = false;
				}
				if(isDoimport){
					System.out.println("begin import==");
					String dsName1 = createDs(session, list);
					Object obj[] = new Object[1];
					obj[0] = dsName1;
					TCUserService service = session.getUserService();
					
					TCComponentDataset ds=(TCComponentDataset) service.call("importProgramInfo", obj);
					System.out.println("begin end==");
					ds.open();
					
					System.out.println("Import programInfo success!");
					MessageBox.post("Import programInfo success!", "Information", MessageBox.INFORMATION);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			pw.flush();
			pw.close();
		}
		
	}
	
	/**
	 * 检验Excel 数据是否正确
	 * @return
	 * @throws TCException
	 */

	public boolean checkExcelData(POIExcelParser parser, int row, List<String> line, PrintWriter writer, StringBuffer sb) throws TCException {
		boolean flag = true;
		String programState = "";
		for (int i = 0; i < line.size(); i++) {
			String str = line.get(i);
			programState = "";
			String colCoordinate = getColumnNum(i);
			if(parser.getSheet().getRow(1).getCell(i)==null){
				continue;
			}
			String colFullName = parser.getSheet().getRow(1).getCell(i).getStringCellValue().replace("（", "(").replace("）", ")").trim();
			String colName = "";
			if (colFullName.indexOf("(") > -1) {
				colName = colFullName.substring(colFullName.indexOf("("));
			} else {
				colName = colFullName;
			}
			String proName = "";
			if (map.get(colName) != null) {
				proName = map.get(colName);
			} else {
				for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
					String key = iter.next();
					if (colName.contains(key)) {
						proName = map.get(key);
						break;
					}
				}
			}
			if (proName.equals("")) {
				continue;
			}
			if (colName.contains("(Project ID)")) {
				String key[] = { "Project ID" };
				String value[] = { str };
				InterfaceAIFComponent[] aifcom = OriginUtil.searchComponentsCollection(session, reg.getString("projectQueryName"), key, value);
				key[0] = "Project Name";
				value[0] = line.get(1);
				InterfaceAIFComponent[] aifcom2 = OriginUtil.searchComponentsCollection(session, reg.getString("projectQueryName"), key, value);
				if (aifcom.length < 1 && aifcom2.length > 0) {
					flag = false;
					writer.println("from row:" + row + "column :" + colCoordinate + " projectName is already exists ");
				}
				TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent("JCI6_ProgramInfo");
				TCComponentItem itemProgromInfo = itemType.find(str);
				if (itemProgromInfo != null) {
					int length = itemProgromInfo.getReferenceListProperty("revision_list").length;
					//System.out.println("length = =====" + length);
					if (length > 1) {
						writer.println("from row:" + row + "column :" + colCoordinate + "The project exists, override?");
					} else if (length == 1) {
						buff.append(str).append("\n");
					}
				}
			}
			if (colName.contains("Target Possibility") && programState.equals("Phase 0")) {
				try {
					Double.parseDouble(str);
				} catch (Exception e) {
					flag = false;
					writer.println("from row:" + row + "column :" + colCoordinate + " is not double ");
				}
			}

			if (colName.contains("(Start Date)") || colName.contains("(End Date)") || colName.contains("(SOP)")) {
				if (!str.equals("")) {
					try {
						dateFormat.parse(str);
					} catch (ParseException e) {
						flag = false;
						writer.println("from row:" + row + "column :" + colCoordinate + " is not righ dateFomat 'yyyy/MM/dd'");
					}
				}
			}

			if (colName.contains("(OEM name)") || colName.contains("(Belonging Division)") || 
					colName.contains("(Active Status)") || colName.contains("(Project State)") || 
					colName.contains("(Category)") || colName.contains("(Product)") ||
					colName.contains("(Type)") || colName.contains("Target Possibility")||
					colName.toLowerCase().contains("(project team leader)")) {// 去掉第四列的判断，即Section的判断
				if (str.equals("")) {
					flag = false;
					writer.println("from row:" + row + "column :" + colCoordinate + " can not null");
					continue;
				}

				if (colName.contains("(OEM name)")) {
					if (!JCI6_CustomerName.contains(str)) {
						writer.println("from row:" + row + "column :" + colCoordinate + " is not in lov JCI6_CustomerName");
					}
				}
				if (colName.contains("(Program State)")) {
					if (!JCI6_ProgramState.contains(str)) {
						programState = str;
						writer.println("from row:" + row + "column :" + colCoordinate + " is not in lov JCI6_ProgramState");
					}
				}
				if (colName.contains("(Category)")) {
					if (!JCI6_Category.contains(str)) {
						writer.println("from row:" + row + "column :" + colCoordinate + " is not in lov JCI6_Category");
					}
				}
				if (colName.contains("(Product)")) {
					if (!JCI6_Product.contains(str)) {
						writer.println("from row:" + row + "column :" + colCoordinate + " is not in lov JCI6_Product");
					}
				}
				if (colName.contains("(Type)")) {
					if (!JCI6_ProgramInfoType.contains(str)) {
						writer.println("from row:" + row + "column :" + colCoordinate + " is not in lov JCI6_ProgramInfoType");
					}
				}
			}

			if (colName.contains("(SOPFY)") || colName.contains("(Model Year)")) {
				if (!str.equals("")) {
					try {
						Integer.parseInt(str);
					} catch (NumberFormatException e) {
						flag = false;
						writer.println("from row:" + row + "column :" + colCoordinate + " is not int type");
					}
				}
			}
			if (colName.contains("(Program Section)") || colName.contains("(Belonging Division)")) {
				if (!str.equals("")) {
					String key[] = { "Name" };
					String value[] = { str+".*" };
					InterfaceAIFComponent[] aifcom = OriginUtil.searchComponentsCollection(session, reg.getString("groupQueryName"), key, value);
					if (aifcom.length < 1) {
						flag = false;
						writer.println("from row:" + row + "column :" + colCoordinate + " not found  group");
					}
				}
			}
			if (colName.toLowerCase().contains("(project team leader)")) {
				if (!str.equals("")) {
					// String key[] = { "userName" };
					String key[] = { "userID" };
					String value[] = { str };
					InterfaceAIFComponent[] aifcom = OriginUtil.searchComponentsCollection(session, reg.getString("userQueryName"), key, value);
					if (aifcom.length < 1) {
						flag = false;
						writer.println("from row:" + row + "column :" + colCoordinate + " not found  user");
					}
				}
			}
			if (colName.contains("(Active Status)")) {
				if (!str.equals("")) {
					str = str.replace("否", "false").replace("非", "false");
				}
			}

			if (str.equals("")) {
				str = "";//NULL
			}
			sb.append(proName).append("=").append(str);
			if (i != line.size() - 1) {
				sb.append("|");
			}
		}
		return flag;
	}

	public Map<String, String> getMapOfProNameAndColName() {
		TCPreferenceService service = session.getPreferenceService();
		String[] mapping = service.getStringArray(TCPreferenceService.TC_preference_site, "YFJC_ImportProgramInfo_Properties_Mapping");
		if (mapping != null && mapping.length > 0) {
			Map<String, String> colNameToProName = new HashMap<String, String>();
			int len = mapping.length;
			for (int i = 0; i < len; i++) {
				String[] arr = mapping[i].replace("（", "(").replace("）", ")").trim().split("=");
				colNameToProName.put(arr[1], arr[0]);
			}
			return colNameToProName;
		}
		return null;
	}

	/**
	 * 根据excel的列数字得到excel的列坐标，比如第0列的坐标是A，第1列的坐标是B，以此类推
	 * @param columnCount
	 * @return
	 */
	public String getColumnNum(int columnCount) {
		if (columnCount < 26) {
			return colmap.get(columnCount);
		} else {
			int firstNum = columnCount / 26 - 1;
			int secondNum = columnCount % 26;
			return new StringBuffer(colmap.get(firstNum)).append(colmap.get(secondNum)).toString();
		}
	}
	
	private static String createDs(TCSession session, List<String> list) throws TCException, IOException {
		String fileName = UUID.randomUUID().toString();

		String fileStr = System.getenv("temp") + "/" + fileName + ".txt";
		//System.out.println("ds文件： fileStr = " + fileStr);
		File file = new File(fileStr);
		file.createNewFile();

		PrintWriter pw = new PrintWriter(new FileOutputStream(file), false);

		for (int i = 0; i < list.size(); i++) {
			pw.println(list.get(i).toString());
		}
		pw.flush();
		pw.close();

		TCTypeService service = session.getTypeService();
		TCComponentDatasetType datasetType = (TCComponentDatasetType) service.getTypeComponent("Dataset");
		TCComponentDataset tcd = datasetType.create(fileName, "description", "Text");
		String[] arrTargetName = { fileStr };
		String[] type = { "Text" }; // excel
		tcd.setFiles(arrTargetName, type);

		return fileName;
	}

}
