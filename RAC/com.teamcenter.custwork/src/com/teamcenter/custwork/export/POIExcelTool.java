package com.teamcenter.custwork.export;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class POIExcelTool {
	public static void insertRow(int starRow,int rows,Sheet sheet){	
		//sheet.shiftRows(starRow+1, sheet.getLastRowNum(), rows,true,false);
		sheet.shiftRows(starRow+1, sheet.getLastRowNum(), rows,true,true);
		sheet.createRow(starRow+1); 
		/*for (int i = 0; i < rows; i++) {
			HSSFRow sourceRow = null;
			HSSFRow targetRow = null;
			HSSFCell soureCell = null;
			HSSFCell targetCell = null;
			short m;
			starRow = starRow + 1;
			sourceRow = (HSSFRow) sheet.getRow(starRow);
			targetRow = (HSSFRow) sheet.createRow(starRow + 1);
			targetRow.setHeight(sourceRow.getHeight());
			for (m = sourceRow.getFirstCellNum(); m < sourceRow.getLastCellNum(); m++) {
				 soureCell = sourceRow.getCell(m);
				targetCell = targetRow.createCell(m);
				// targetCell.setEncoding(sourceCell.getEncoding());
				targetCell.setCellStyle(soureCell.getCellStyle());
				targetCell.setCellType(soureCell.getCellType());
				//targetCell.setCellValue(i);//�O��ֵ
			}
		}*/
	}

	public static void insertRow(HSSFSheet sheet, int startRow, int rows) {  
		sheet.shiftRows(startRow, sheet.getLastRowNum(), rows, true, false);  
		for (int i = 0; i < rows; i++) {  
			HSSFRow sourceRow = null;//ԭʼλ��  
			HSSFRow targetRow = null;//�ƶ���λ��  
			HSSFCell sourceCell = null;  
			HSSFCell targetCell = null;  
			sourceRow = sheet.createRow(startRow);  
			targetRow = sheet.getRow(startRow + rows);  
			sourceRow.setHeight(targetRow.getHeight());  

			for (int m = targetRow.getFirstCellNum(); m < targetRow.getPhysicalNumberOfCells(); m++) {  
				sourceCell = sourceRow.createCell(m);  
				targetCell = targetRow.getCell(m);  
				sourceCell.setCellStyle(targetCell.getCellStyle());  
				sourceCell.setCellType(targetCell.getCellType());  
			}  
			startRow++;  
		}  

	}

	public static void insertRow(XSSFSheet sheet, int startRow,int rows) {
		sheet.shiftRows(startRow, sheet.getLastRowNum(), rows, true, false);  
		for (int i = 0; i < rows; i++) {  
			XSSFRow sourceRow = null;//ԭʼλ��  
			XSSFRow targetRow = null;//�ƶ���λ��  
			XSSFCell sourceCell = null;  
			XSSFCell targetCell = null;  
			sourceRow = sheet.createRow(startRow);  
			targetRow = sheet.getRow(startRow + rows);  
			sourceRow.setHeight(targetRow.getHeight());  
			for (int m = targetRow.getFirstCellNum(); m < targetRow.getPhysicalNumberOfCells(); m++) {  
				sourceCell = sourceRow.createCell(m);  
				targetCell = targetRow.getCell(m);  
				sourceCell.setCellStyle(targetCell.getCellStyle());  
				sourceCell.setCellType(targetCell.getCellType());  
			//	System.out.println(sourceCell.getRichStringCellValue());
			//	System.out.println(targetCell.getRichStringCellValue());
			}  
			startRow++;  
		}  

	}  

	/**
	 * ��EXCEL��A,B,C,D,E��ӳ���0,1,2,3
	 * @param col
	 * @return
	 */
	public static int getExcelCol(String col){
		col = col.toUpperCase();
		//��-1��ʼ����,��ĸ��1��ʼ���㡣����������������������ͬ��
		int count = -1;
		char[] cs = col.toCharArray();
		for(int i=0;i<cs.length;i++)
		{
			count += (cs[i]-64 ) * Math.pow(26, cs.length-1-i);
		}
		return count;
	}

	public static float getExcelCellAutoHeight(String str, float fontCountInline) {
		float defaultRowHeight = 12.00f;
		float defaultCount = 0.00f;
		for (int i = 0; i < str.length(); i++) {
			float ff = getregex(str.substring(i, i + 1));
			defaultCount = defaultCount + ff;
		}
		return ((int) (defaultCount / fontCountInline) + 1) * defaultRowHeight;
	}

	public static float getregex(String charStr) {

		if(charStr==" ")
		{
			return 0.5f;
		}
		// �ж��Ƿ�Ϊ��ĸ���ַ�
		if (Pattern.compile("^[A-Za-z0-9]+$").matcher(charStr).matches()) {
			return 0.5f;
		}
		// �ж��Ƿ�Ϊȫ��

		if (Pattern.compile("[\u4e00-\u9fa5]+$").matcher(charStr).matches()) {
			return 1.00f;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("��", "#");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		map.put("��", "��");
		if (map.containsKey(charStr)) {
			return 1.00f;
		}
		if (Pattern.compile("[^x00-xff]").matcher(charStr).matches()) {
			return 1.00f;
		}
		return 0.5f;

	}
	
	public static void openExcelReport(String file) throws IOException{
		String as1[] = { "cmd.exe", "/c", "start", file };	
		Runtime.getRuntime().exec(as1);	
	}


}
