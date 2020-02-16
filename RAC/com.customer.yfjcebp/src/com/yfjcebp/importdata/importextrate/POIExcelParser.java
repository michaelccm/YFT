package com.yfjcebp.importdata.importextrate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.util.MessageBox;

/** */
/**
 * <ul>
 * <li>Title:[POI基础上的Excel数据读取工具]</li>
 * <li>Description: [支持Excell2003,Excell2007,自动格式化数值型数据,自动格式化日期型数据]</li>
 * <li>Copyright 2010 RoadWay Co., Ltd.</li>
 * <li>All right reserved.</li>
 * <li>Created by [刘剑鸣] [Dec 5, 2010]</li>
 * <li>Midified by [modifier] [modified time]</li>
 * <li>所需Jar包列表</li>
 * <li>poi-3.7-20101029.jar</li>
 * <li>poi-examples-3.7-20101029.jar</li>
 * <li>poi-ooxml-3.7-2010102.jar</li>
 * <li>poi-ooxml-schemas-3.7-2010102.jar</li>
 * <li>xmlbeans-2.3.0.jar</li>
 * <ul>
 * @version 1.0
 */
public class POIExcelParser {
	private XSSFWorkbook			workbook;

	private Sheet				sheet;

	public int					rows;

	public int					startRow;

	public int					endRow;

	private int					columns;

	private Row					rowyear;

	private Row					rowmonth;

	private SimpleDateFormat	sdf;	
	

	public POIExcelParser(File input) throws FileNotFoundException, IOException {

		sdf = new SimpleDateFormat("yyyy/M/dd");
		workbook = new XSSFWorkbook(new FileInputStream(input));

	}

	public int getSheetNum() {
		return workbook.getNumberOfSheets();
	}

	public String getSheetName() {
		return sheet.getSheetName();
	}
	
	public void setInit(int sheetIndex) {
		sheet = workbook.getSheetAt(sheetIndex);
		//System.out.println("excel文件一共要读取" + rows + "行数据");
	}
	
	public void setInit( int cols, int startRow) {
		this.startRow = startRow;
		columns = cols;
		rows = sheet.getPhysicalNumberOfRows();
		//System.out.println("excel文件一共要读取" + rows + "行数据");
	}

	public void setInit(int sheetIndex, int cols, int startRow) {
		this.startRow = startRow;
		columns = cols;
		sheet = workbook.getSheetAt(sheetIndex);
		rows = sheet.getPhysicalNumberOfRows();
		//System.out.println("excel文件一共要读取" + rows + "行数据");
	}

	public void setInit(int sheetIndex, int cols, int startRow, int endRow) {
		this.startRow = startRow;
		this.endRow = endRow;
		columns = cols;
		sheet = workbook.getSheetAt(sheetIndex);
		rows = sheet.getPhysicalNumberOfRows();
		rowyear = sheet.getRow(startRow - 2);
		rowmonth = sheet.getRow(startRow - 1);
		//System.out.println("excel文件一共要读取" + rows + "行数据");
	}
	
	public static void main(String[] args){
		File file = new File("D:/Rosedale Resource+Rate.xlsx");
		try {
			
			POIExcelParser parser = new POIExcelParser(file);
			int sheetNum = parser.getSheetNum();
			parser.setInit(0, 0, 1);
			List<String> line = null;
			StringBuffer sb = new StringBuffer();
			StringBuffer sbName = new StringBuffer();
			List listProgramInfoRev = new ArrayList();
			String itemID = "";
			while ((line = parser.parseLine()) != null) {
				//System.out.println(line);
				
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated constructor stub
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

public boolean setInit(String sheetname,int cols, int startRow , int endRow){
		this.startRow = startRow;
		this.endRow = endRow;
		columns = cols;
		sheet = workbook.getSheet(sheetname);
		if(sheet==null){
			MessageBox.post("没有找到<"+sheetname+">sheet页","Information",MessageBox.WARNING);
			return false;
		}
		rows = sheet.getPhysicalNumberOfRows();
		rowyear = sheet.getRow(startRow-2);
		rowmonth = sheet.getRow(startRow-1);
		//System.out.println("excel文件一共要读取" + rows + "行数据");
		return true;
	}

	public List<String> parseLine() {
		List<String> l = getLine();
		while (isEmpty(l)) {
			l = getLine();
		}

		return l;
	}
	
	public Sheet getSheet(String sheetName) {
		return workbook.getSheet(sheetName);
	}


	private boolean isEmpty(List<String> l) {
		if (l == null) {
			return false;
		}

		for (String string : l) {
			if (!string.trim().equals("")) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 增加公式的处理
	 * @return
	 */
	public List<String> getLine() {
		//System.out.println("startRow:"+startRow+" rows:"+rows);
		if (startRow > rows - 1) {
			//System.out.println("startRow > rows - 1   return！！！" );
			return null;
		}

		List<String> line = new ArrayList<String>();

		Row rows = sheet.getRow(startRow++);
		if (rows != null) {
			// int cos = Math.min(columns, rows.getPhysicalNumberOfCells());
			int cos = sheet.getRow(0).getPhysicalNumberOfCells();
			for (int j = 0; j < cos; j++) {
				Cell cell = rows.getCell(j);
				String cellValue = "";
				if (cell != null) {
					/** */
					/** 处理数字型的,自动去零 */
					if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
						/** */
						/** 在excel里,日期也是数字,在此要进行判断 */
						if (DateUtil.isCellDateFormatted(cell)) {
							cellValue = sdf.format(cell.getDateCellValue());
						} else {
							DecimalFormat df = new DecimalFormat("0.000");  
							cellValue = cutZero(df.format(cell.getNumericCellValue()));
						}
					}
					/** */
					/** 处理字符串型 */
					else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
						cellValue = cell.getStringCellValue();
					}
					/** */
					/** 处理布尔型 */
					else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
						cellValue = cell.getBooleanCellValue() + "";
					}
					/** */
					/** 处理公式 */
					else if (Cell.CELL_TYPE_FORMULA == cell.getCellType()) {
						DecimalFormat df = new DecimalFormat("0.0");  
						cellValue = cutZero(df.format(cell.getNumericCellValue()));
					}
					/** */
					/** 其它的,非以上几种数据类型 */
					else {
						cellValue = cell.toString() + "";
					}
					line.add(cellValue);
				} else {
					line.add(cellValue);
				}
			}

			for (int j = 0; j < columns - cos + 1; j++) {
				line.add("");
			}
		}

		return line;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}

	/** */
	/**
	 * <ul>
	 * <li>Description:[正确地处理整数后自动加零的情况]</li>
	 * <li>Created by [Huyvanpull] [Jan 20, 2010]</li>
	 * <li>Midified by [modifier] [modified time]</li>
	 * <ul>
	 * @param sNum
	 * @return
	 */
	private String getRightStr(String sNum) {
		DecimalFormat decimalFormat = new DecimalFormat("#.000000");
		String resultStr = decimalFormat.format(new Double(sNum));
		if (resultStr.matches("^[-+]?\\d+\\.[0]+$")) {
			resultStr = resultStr.substring(0, resultStr.indexOf("."));
		}
		return resultStr;
	}

	/**
	 * 去掉小数点后面多余的零
	 * @param v
	 * @return
	 */
	public String cutZero(String v) {
		if (v.indexOf(".") > -1) {
			while (true) {
				if (v.lastIndexOf("0") == (v.length() - 1)) {
					v = v.substring(0, v.lastIndexOf("0"));
				} else {
					break;
				}
			}
			if (v.lastIndexOf(".") == (v.length() - 1)) {
				v = v.substring(0, v.lastIndexOf("."));
			}
		}
		return v;
	}

	public static boolean isDateFormat(String dataStr) {
		if (dataStr.contains("-")) {
			String year = dataStr.split("-")[0];
			String month = dataStr.split("-")[1];
			try {
				if (year.length() == 4 && Integer.parseInt(year) > 1900) {
					if (month.length() == 2) {
						if (Integer.parseInt(month) > 0 && Integer.parseInt(month) < 13) {
							return true;
						} else {
							//System.out.println("月份不在1月和12月之间");
						}
					} else {
						//System.out.println("月份不在1月和12月之间");
					}
				} else {
					//System.out.println("年中字符不等于4位");
				}
			} catch (NumberFormatException e) {
				//System.out.println("年或者月中含有非数字的字符");
				e.printStackTrace();
			}
		}
		return false;
	}

	public List<String> parsePersonPlanLine() {
		List<String> l = getPersonPlanLine();
		while (isEmpty(l)) {
			l = getPersonPlanLine();
		}

		return l;
	}

	public List<String> getPersonPlanLine() {
		if (startRow > endRow - 1) {
			return null;
		}

		List<String> line = new ArrayList<String>();

		Row rows = sheet.getRow(startRow++);
		if (rows != null) {
			int cos = sheet.getRow(startRow - 1).getPhysicalNumberOfCells();
			for (int j = 0; j < cos; j++) {
				Cell cell = rows.getCell(j);
				Cell cellyear = rowyear.getCell(j);
				Cell cellmonth = rowmonth.getCell(j);
				String cellyearValue = getPersonPlanLineCellValue(cellyear);
				String cellmonthValue = getPersonPlanLineCellValue(cellmonth);
				String cellValue = getPersonPlanLineCellValue(cell);

				if (j == 0) {
					line.add(cellValue);

				} else {
					line.add(cellyearValue + "|" + cellmonthValue + "|" + cellValue + "|");
				}
			}
		}
		return line;
	}

	/**
	 * 增加公式的处理
	 * @return
	 */
	private String getPersonPlanLineCellValue(Cell cell) {
		String cellValue = "";
		if (cell != null) {
			/** */
			/** 处理数字型的,自动去零 */
			if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
				/** */
				/** 在excel里,日期也是数字,在此要进行判断 */
				if (DateUtil.isCellDateFormatted(cell)) {
					cellValue = sdf.format(cell.getDateCellValue());
				} else {
					cellValue = cutZero(cell.getNumericCellValue() + "");
				}
			}
			/** */
			/** 处理字符串型 */
			else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
				cellValue = cell.getStringCellValue();
			}
			/** */
			/** 处理布尔型 */
			else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
				cellValue = cell.getBooleanCellValue() + "";
			}
			/** */
			/** 处理公式 */
			else if (Cell.CELL_TYPE_FORMULA == cell.getCellType()) {
				DecimalFormat df = new DecimalFormat("0.0");  
				cellValue = cutZero(df.format(cell.getNumericCellValue()));
			}
			/** */
			/** 其它的,非以上几种数据类型 */
			else {
				cellValue = cell.toString() + "";
			}
		}

		if (cellValue.equals("")) {
			cellValue = " ";
		}
		return cellValue;
	}

}
