package com.yfjcebp.projectmanager.budget.dialogs;

import java.io.File;
import java.io.IOException;

import com.jacob.com.Dispatch;

public class Test {

	public static boolean isData(String str) {
		// 是否为日期格式：1992-09-03
		if (str.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
			return true;
		} else {
			return false;
		}
		
	
	}
	
	public static void main(String[] args) {
		System.out.println(isData("1990-8-04"));
		System.out.println(isData("1990/08/04"));
		System.out.println(isData("1990-14-05"));
		System.out.println(isData("1990-12-34"));
		JacobEReportTool tool = new JacobEReportTool();
		
		//String TC_path = System.getenv("TPR");
		//try {
		//	tool.addDir(TC_path + "\\plugins");
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		// 得到系统的零时文件
		String getenv = System.getenv("TEMP");
		System.out.println("File.separator--->"+File.separator);
		// 得到所有的sheet
		System.out.println(getenv + File.separator
				+ "GEBT.xlsm");
		//Dispatch sheetsAll = tool.getSheets(getenv + File.separator
		//		+ "GEBT.xlsm");
		String[] str = "0|1".split("\\|");
		System.out.println(str.length);
		
		// 得到jci6_coc_feature的值
		String coc_feature = "jci6_coc_feature_V3.4.9=Content{.}[(C-330)~(C-347),(C-518)~(C-535),(C-127)~(C-158)]";
			if (coc_feature != null) {
				String[] p_sheet_col = coc_feature.split("=");
				if (p_sheet_col.length == 2) {
					String coc_feature_sheet = p_sheet_col[1].substring(0,p_sheet_col[1].indexOf("{"));
					String cols = p_sheet_col[1].substring(p_sheet_col[1].lastIndexOf("}") + 2,p_sheet_col[1].length()-1);
					System.out.println(cols);
				}
			}
		System.out.println("aaa-www".split("-").length);
		System.out.println("aaa-www".split("-")[0]);
		System.out.println("aaa-www".split("-")[1]);
		System.out.println("(C-8)".substring(1,"(C-8)".lastIndexOf(")")));
		String s = "(C-347)".substring(1,"(C-347)".lastIndexOf(")"));
		String[] row_col = s.split("\\-");
		
		
		
		//jci6_coc_feature_V3.4.9=Content{.}[(C-330)~(C-347),(C-518)~(C-535),(C-127)~(C-158)]
	}
	
	
	
	
}
