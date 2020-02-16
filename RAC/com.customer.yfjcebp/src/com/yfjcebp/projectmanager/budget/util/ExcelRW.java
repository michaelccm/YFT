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
 #	2013-3-11	liuc  		Ini		读取excel的工具类					   
 #=============================================================================
 */
package com.yfjcebp.projectmanager.budget.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelRW {

	public Workbook readExcel(String filename) throws Exception {

		Workbook wb = null;

		try {

			if (filename.endsWith(".xls")) {
				wb = new HSSFWorkbook(new FileInputStream(filename));// Excel
																		// 2003
			} else {
				wb = new XSSFWorkbook(new FileInputStream(filename));// Excel
																		// 2007
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

		}
		return wb;
	}

	public ArrayList<String> parseExcel(Workbook wb, String sheet, int rowNum)
			throws Exception {
		Sheet sheet1 = wb.getSheet(sheet);
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 1; i < rowNum; i++) {
			Row row = sheet1.getRow(i) != null ? sheet1.getRow(i) : sheet1
					.createRow(i);
			String string = row.getCell(0).toString();
			if (string.equals(null) || string == "" || string.length() == 0)
				string = "0";
			list.add(string);
		}

		return list;
	}

	public String getDate(Workbook wb, String sheet, int rowNum, int colNum) {
		// 格式化日期——标准日期格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Sheet sheet1 = wb.getSheet(sheet);

		Row row = sheet1.getRow(rowNum) != null ? sheet1.getRow(rowNum)
				: sheet1.createRow(rowNum);
		Cell cell = row.getCell(colNum) != null ? row.getCell(colNum) : row
				.createCell(colNum);

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
					// 去掉多余的0
					cellValue = cutZero(cell.getNumericCellValue() + "");
				}
			}

			/** 处理字符串型 */
			else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
				cellValue = cell.getStringCellValue();
			}

			/** 处理布尔型 */
			else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
				cellValue = cell.getBooleanCellValue() + "";
			}

			/** 其它的,非以上几种数据类型 */
			else {
				cellValue = cell.toString() + "";
			}

		}

		return cellValue;
	}

	/**
	 * 去掉小数点后面多余的零
	 * 
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

	public void clearExcel(Workbook wb, String sheet, int rowNum, int colNum)
			throws Exception {
		Sheet sheet1 = wb.getSheet(sheet);
		Row row1 = sheet1.getRow(rowNum) != null ? sheet1.getRow(rowNum)
				: sheet1.createRow(rowNum);
		Cell cell = row1.getCell(colNum) != null ? row1.getCell(colNum) : row1
				.createCell(colNum);
		cell.setCellType(3);
	}

	public void wirteExcel(Workbook wb, String sheet, int row, int col,
			String value) throws Exception {
		Sheet sheet1 = wb.getSheet(sheet);
		Row row1 = sheet1.getRow(row) != null ? sheet1.getRow(row) : sheet1
				.createRow(row);
		Cell c3 = row1.getCell(col) != null ? row1.getCell(col) : row1
				.createCell(col);
		c3.setCellValue(value);
	}

	public void closeAndSave(String filename, Workbook wb, String sheet, int row)
			throws IOException {
		setExcelFormat(row, wb, sheet);
		FileOutputStream out = new FileOutputStream(filename);
		wb.write(out);
		out.close();
	}

	public CellStyle setNullStyle(Workbook wb) {
		CellStyle setBorder = wb.createCellStyle();
		setBorder.setBorderBottom((short) 0);
		setBorder.setBorderLeft((short) 0);
		setBorder.setBorderTop((short) 0);
		setBorder.setBorderRight((short) 0);
		return setBorder;
	}

	public CellStyle setStyle(Workbook wb) {
		CellStyle setBorder = wb.createCellStyle();
		setBorder.setBorderBottom((short) 1);
		setBorder.setBorderLeft((short) 1);
		setBorder.setBorderTop((short) 1);
		setBorder.setBorderRight((short) 1);
		setBorder.setAlignment((short) 2);
		Font font = wb.createFont();
		// font.setFontName("宋体");
		font.setFontHeightInPoints((short) 9);
		setBorder.setFont(font);
		setBorder.setWrapText(true);
		return setBorder;
	}

	public void setExcelFormat(int row, Workbook wb, String sheet) {
		Sheet sheet1 = wb.getSheet(sheet);
		CellStyle style_blank = setNullStyle(wb);
		for (int i = row; i < row + 51; i++) {
			Row rowdata = sheet1.getRow(i + 5) != null ? sheet1.getRow(i + 5)
					: sheet1.createRow(i + 5);
			for (int j = 0; j < 17; j++) {
				Cell cell = rowdata.getCell(j + 1) != null ? rowdata
						.getCell(j + 1) : rowdata.createCell(j + 1);
				if (i == row && row != 0) {
					CellStyle setBorder = wb.createCellStyle();
					setBorder.setBorderBottom((short) 0);
					setBorder.setBorderLeft((short) 0);
					setBorder.setBorderRight((short) 0);
					cell.setCellStyle(setBorder);
				} else {
					cell.setCellStyle(style_blank);
				}
			}

		}

		if (row == 0) {
			Row rowdata = sheet1.getRow(5) != null ? sheet1.getRow(5) : sheet1
					.createRow(5);
			for (int j = 0; j < 17; j++) {
				Cell cell = rowdata.getCell(j + 1) != null ? rowdata
						.getCell(j + 1) : rowdata.createCell(j + 1);
				if (j == 0) {
					CellStyle style = setStyle(wb);
					style.setBorderLeft((short) 5);
					style.setBorderBottom((short) 5);
					cell.setCellStyle(style);
				} else if (j == 16) {
					CellStyle style = setStyle(wb);
					style.setBorderRight((short) 5);
					style.setBorderBottom((short) 5);
					cell.setCellStyle(style);
				} else {
					CellStyle style = setStyle(wb);
					style.setBorderBottom((short) 5);
					cell.setCellStyle(style);
				}
			}

		}
		for (int i = 0; i < row; i++) {
			Row rowdata = sheet1.getRow(i + 5) != null ? sheet1.getRow(i + 5)
					: sheet1.createRow(i + 5);
			if (i != row - 1) {
				for (int j = 0; j < 17; j++) {
					Cell cell = rowdata.getCell(j + 1) != null ? rowdata
							.getCell(j + 1) : rowdata.createCell(j + 1);
					if (j == 0) {
						CellStyle style = setStyle(wb);
						style.setBorderLeft((short) 5);
						cell.setCellStyle(style);
					} else if (j == 16) {
						CellStyle style = setStyle(wb);
						style.setBorderRight((short) 5);
						cell.setCellStyle(style);
					} else {
						CellStyle style = setStyle(wb);
						cell.setCellStyle(style);
					}
				}

			} else {
				for (int j = 0; j < 17; j++) {
					Cell cell = rowdata.getCell(j + 1) != null ? rowdata
							.getCell(j + 1) : rowdata.createCell(j + 1);
					if (j == 0) {
						CellStyle style = setStyle(wb);
						style.setBorderLeft((short) 5);
						style.setBorderBottom((short) 5);
						cell.setCellStyle(style);
					} else if (j == 16) {
						CellStyle style = setStyle(wb);
						style.setBorderRight((short) 5);
						style.setBorderBottom((short) 5);
						cell.setCellStyle(style);
					} else {
						CellStyle style = setStyle(wb);
						style.setBorderBottom((short) 5);
						cell.setCellStyle(style);
					}
				}

			}
		}

	}
}
