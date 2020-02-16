/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: POIReadExcel.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-5	wuh  		Ini		初�� 								   
 #=============================================================================
 */
package com.yfjcebp.skillmatrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

/**
 * 将excel的每�行数捽�为一维数组放�rrayList� Excel each row of data as a one-dimensional
 * array into ArrayList.
 * 
 * @author Administrator
 * 
 */
public class POIReadExcel {

//	private Registry reg = Registry.getRegistry(POIReadExcel.class);
	private static final String BURFLR_NAME = "com.yfjcebp.skillmatrix.skillmatrix_locale";
	private static final ResourceBundle reg = ResourceBundle.getBundle(BURFLR_NAME);
	private ArrayList<String[]> list = new ArrayList<String[]>();
	private String[] s;
	private String filePath;
	private InputStream is;
	private Sheet sheet;
	private Boolean excelIsNull = false;// excel昐�为空

	public Boolean getExcelIsNull() {
		return excelIsNull;
	}

	public void setExcelIsNull(Boolean excelIsNull) {
		this.excelIsNull = excelIsNull;
	}

	public POIReadExcel(String filePath) {
		this.filePath = filePath;
		// 创workbook对象
		Workbook workbook = paseExcel(filePath);
		// 得到sheet对象
		sheet = workbook.getSheetAt(0);
		// 得到�有的�
		Iterator<Row> iterator = sheet.iterator();
		Row row = null;
		if (iterator.hasNext()) {
			setExcelIsNull(false);
			while (iterator.hasNext()) {

				row = iterator.next();

				s = new String[row.getLastCellNum()];
				for (int i = 0; i < row.getLastCellNum(); i++) {
					// 将同�行的数据放到list�
					s[i] = getCellValue(row.getCell(i));
				}
				list.add(s);
			}
		} else {
			setExcelIsNull(true);
			MessageBox.post(reg.getString("nullexcelmessage"),
					reg.getString("errmessage"), MessageBox.ERROR);
		}
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<String[]> getList() {
		return list;
	}

	public void setList(ArrayList<String[]> list) {
		this.list = list;
	}

	/**
	 * 创建workbook对象
	 * 
	 * @param filePath
	 * @return
	 */
	public Workbook paseExcel(String filePath) {

		Workbook workbook = null;
		File file = new File(filePath);
		try {
			is = new FileInputStream(file);
			// 判断文件昻�么格�
			if (filePath.endsWith(".xls")) {
				workbook = new HSSFWorkbook(is);// Excel 2003
			} else if (filePath.endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(is);// Excel 2007
			} else if (filePath.endsWith(".xlsm")){
				workbook = new XSSFWorkbook(is);// Excel 2007
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

		}
		return workbook;
	}

	/**
	 * 通过sheet得到�有的非空�
	 * 
	 * @param sheet
	 * @return
	 */
	public ArrayList<Row> getRowList(Sheet sheet) {
		Row row = null;
		// 得到�有的Row
		Iterator<Row> iterator = sheet.iterator();
		ArrayList<Row> list = new ArrayList();
		// 取每个Row的��不�元格的�
		while (iterator.hasNext()) {
			row = iterator.next();
			if (row != null) {
				list.add(row);
			}
		}
		return list;
	}

	/**
	 * 获取单元格的�
	 * 
	 * @param cell
	 * @return
	 */
	public String getCellValue(Cell cell) {

		if (cell == null)
			return "";

		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

			return cell.getStringCellValue();

		} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {

			return String.valueOf(cell.getBooleanCellValue());

		} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

			return cell.getCellFormula();

		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {

			return (String.valueOf(cell.getNumericCellValue()));
		}

		return "";
	}

	/**
	 * 轍�成excelRange�
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	public String excelRange(int i, int j) {
		String str1, str2 = null;
		str1 = excelRange(j);
		str2 = str1 + String.valueOf(i + 1);
		return str2;
	}

	// �
	public String excelRange(int i) {
		String s = null;
		if (i == 0) {
			s = "A";
		}
		if (i == 1) {
			s = "B";
		}
		if (i == 2) {
			s = "C";
		}
		if (i == 3) {
			s = "D";
		}
		if (i == 4) {
			s = "E";
		}
		if (i == 5) {
			s = "F";
		}
		if (i == 6) {
			s = "G";
		}
		if (i == 7) {
			s = "H";
		}
		if (i == 8) {
			s = "I";
		}
		if (i == 9) {
			s = "J";
		}
		if (i == 10) {
			s = "K";
		}
		if (i == 11) {
			s = "L";
		}
		if (i == 12) {
			s = "M";
		}
		if (i == 13) {
			s = "N";
		}
		if (i == 14) {
			s = "O";
		}
		if (i == 15) {
			s = "P";
		}
		if (i == 16) {
			s = "Q";
		}
		if (i == 17) {
			s = "R";
		}
		if (i == 18) {
			s = "S";
		}
		if (i == 19) {
			s = "T";
		}
		if (i == 20) {
			s = "U";
		}
		if (i == 21) {
			s = "V";
		}
		if (i == 22) {
			s = "W";
		}
		if (i == 23) {
			s = "X";
		}
		if (i == 24) {
			s = "Y";
		}
		if (i == 25) {
			s = "Z";
		}
		return s;
	}

	// �
	public int columnNo(String s) {
		int i = 0;
		if (s.equals("A")) {
			i = 0;
		} else if (s.equals("B")) {
			i = 1;
		} else if (s.equals("C")) {
			i = 2;
		} else if (s.equals("D")) {
			i = 3;
		} else if (s.equals("E")) {
			i = 4;
		} else if (s.equals("F")) {
			i = 5;
		} else if (s.equals("G")) {
			i = 6;
		} else if (s.equals("H")) {
			i = 7;
		} else if (s.equals("I")) {
			i = 8;
		} else if (s.equals("J")) {
			i = 9;
		} else if (s.equals("K")) {
			i = 10;
		} else if (s.equals("L")) {
			i = 11;
		} else if (s.equals("M")) {
			i = 12;
		} else if (s.equals("N")) {
			i = 13;
		} else if (s.equals("O")) {
			i = 14;
		} else if (s.equals("P")) {
			i = 15;
		} else if (s.equals("Q")) {
			i = 16;
		} else if (s.equals("R")) {
			i = 17;
		} else if (s.equals("S")) {
			i = 18;
		} else if (s.equals("T")) {
			i = 19;
		} else if (s.equals("U")) {
			i = 20;
		} else if (s.equals("V")) {
			i = 21;
		} else if (s.equals("W")) {
			i = 22;
		} else if (s.equals("X")) {
			i = 23;
		} else if (s.equals("Y")) {
			i = 24;
		} else if (s.equals("Z")) {
			i = 25;
		} else {
			i = 26;
		}
		return i;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}
}
