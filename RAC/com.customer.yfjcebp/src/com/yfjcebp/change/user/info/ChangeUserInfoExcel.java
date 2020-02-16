package com.yfjcebp.change.user.info;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.aif.kernel.IPreferenceService;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.yfjcebp.extsupport.JCI6YFJCUtil;


public class ChangeUserInfoExcel {

	private XSSFWorkbook wb;
	private FileInputStream is;
	private String excelPath;
	private XSSFSheet sheet;
	private Vector<Map<Integer, String>> dataVec = new Vector<Map<Integer, String>>();
	private TCSession session;
	private Map<String,Integer> personProColMap = new HashMap<String, Integer>();
	private  Map<String,Integer> disciplineColMap = new HashMap<String, Integer>();
	private String user_id_col_name;
	private String rateLevel_col_name;
	private String tc_all_col_name;
	private String discipline_col_name;
	
	public ChangeUserInfoExcel(TCSession session,String excelPath) {
		this.session = session;
		this.excelPath = excelPath;
	}

	public void readExcel() throws IOException {
		openExcel();
		getPreColMapping();
		getUserInfo();
		closeExcel();
	}

	/**
	 * 打开excel
	 * 
	 * @throws IOException
	 */
	private void openExcel() throws IOException {
		is = new FileInputStream(excelPath);
		wb = new XSSFWorkbook(is);
		sheet = wb.getSheetAt(0);
	}

	/**
	 * 关闭excel
	 * 
	 * @throws IOException
	 */
	private void closeExcel() throws IOException {
		is.close();
	}


	
	//获得首选项YFJC_PostionFlow_PropMapping
	private void getPreColMapping(){
		user_id_col_name = "A";
		TCPreferenceService preService = session.getPreferenceService();
		String[] preValue = preService.getStringArray(TCPreferenceService.TC_preference_site, "YFJC_PositionFlow_PropMapping");
		if(preValue != null && preValue.length >0){
			for(int i=0;i<preValue.length;i++){
				String[] str = preValue[i].trim().split("=");
				if(str.length == 2){
					String[] tmp = str[1].trim().split("\\.");
					if(tmp.length == 2){
						if(tmp[0].equals("Person")){
							personProColMap.put(tmp[1], CellReference.convertColStringToIndex( str[0]));
						}else if(tmp[0].equals("Discipline")){
							if(tmp[1].equals("TC_all")){
								tc_all_col_name = str[0];
								disciplineColMap.put("TC_all", CellReference.convertColStringToIndex(str[0]));
							}else if(tmp[1].equals("RateLevel")){
								rateLevel_col_name = str[0];
								disciplineColMap.put("jci6_RateLevel", CellReference.convertColStringToIndex(str[0]));
							}else{
								discipline_col_name = str[0];
								disciplineColMap.put("Discipline", CellReference.convertColStringToIndex(str[0]));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 获得需转岗用户的相关信息
	 */
	private void getUserInfo() {
		// 真正数据从第三行开始
		XSSFFormulaEvaluator formula = new XSSFFormulaEvaluator(wb);
		for (int i = 2, rowcnt = sheet.getLastRowNum()+1; i < rowcnt; i++) {
			XSSFRow row = sheet.getRow(i);
			boolean isRowNull = true;
			if (row != null) {
				Map<Integer, String> map = new HashMap<Integer, String>();
				for (int j = 0, colcnt = row.getLastCellNum(); j < colcnt; j++) {
					String s = JCI6YFJCUtil.getStringCellValue(formula,
							row.getCell(j));
					if(s != null && !s.equals("")){
						isRowNull = false;
					}
					map.put(j, s);
				}
				if(!isRowNull){
					dataVec.add(map);
				}	
			}
		}
	}
	

	public Map<String, Integer> getPersonProColMap() {
		return personProColMap;
	}

	public Map<String, Integer> getDisciplineColMap() {
		return disciplineColMap;
	}



	public Vector<Map<Integer, String>> getDataVec() {
		return dataVec;
	}

	public String getUser_id_col_name() {
		return user_id_col_name;
	}

	public String getRateLevel_col_name() {
		return rateLevel_col_name;
	}


	public String getTc_all_col_name() {
		return tc_all_col_name;
	}



	public String getDiscipline_col_name() {
		return discipline_col_name;
	}


	
	
	

}
