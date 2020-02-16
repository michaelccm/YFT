package com.yfjc.extplanoverview;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XSSFUtil {

	public void copyRow(XSSFWorkbook wb, Row fromRow, Row toRow, boolean copyValueFlag) {
		int cellNum = fromRow.getLastCellNum();
		for (int i = 0; i < cellNum; i++) {
			copyCell(wb, fromRow.getCell(i), toRow.createCell(i), copyValueFlag);
		}

	}

	public void copyCell(XSSFWorkbook wb, Cell srcCell, Cell distCell, boolean copyValueFlag) {
		CellStyle newstyle = wb.createCellStyle();
		// //System.out.println("srcCell======="+srcCell);
		copyCellStyle(srcCell.getCellStyle(), newstyle);
		distCell.setCellStyle(newstyle);
		if (srcCell.getCellComment() != null) {
			distCell.setCellComment(srcCell.getCellComment());
		}
		int srcCellType = srcCell.getCellType();
		distCell.setCellType(srcCellType);
		if (copyValueFlag) {
			if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {
				if (HSSFDateUtil.isCellDateFormatted(srcCell)) {
					distCell.setCellValue(srcCell.getDateCellValue());
				} else {
					distCell.setCellValue(srcCell.getNumericCellValue());
				}
			} else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {
				distCell.setCellValue(srcCell.getRichStringCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {

			} else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {
				distCell.setCellValue(srcCell.getBooleanCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {
				distCell.setCellErrorValue(srcCell.getErrorCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {
				distCell.setCellFormula(srcCell.getCellFormula());
			} else {
			}
		}
	}

	public void copyCellStyle(CellStyle fromStyle, CellStyle toStyle) {
		toStyle.setAlignment(fromStyle.getAlignment());
		// 边框和边框颜色
		toStyle.setBorderBottom(fromStyle.getBorderBottom());
		toStyle.setBorderLeft(fromStyle.getBorderLeft());
		toStyle.setBorderRight(fromStyle.getBorderRight());
		toStyle.setBorderTop(fromStyle.getBorderTop());
		toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
		toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
		toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
		toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());
		// 背景和前景
		toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
		toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

		toStyle.setDataFormat(fromStyle.getDataFormat());
		// toStyle.setFillPattern(fromStyle.getFillPattern());// 复制后颜色为黑色

		toStyle.setHidden(fromStyle.getHidden());
		toStyle.setIndention(fromStyle.getIndention());// 首行缩进
		toStyle.setLocked(fromStyle.getLocked());
		toStyle.setRotation(fromStyle.getRotation());// 旋转
		toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
		toStyle.setWrapText(fromStyle.getWrapText());

	}

	public void writeDataToExcel(int row, int column, Sheet sheet, Object content, int cellType, CellStyle cellStyle) throws Exception {
		Row r1 = sheet.getRow(row);
		Cell c1 = r1.getCell(column);
		if (null != cellStyle) {
			c1.setCellStyle(cellStyle);
		}
		switch (cellType) {
		case Cell.CELL_TYPE_NUMERIC:
			c1.setCellValue((Double) content);
			break;
		case Cell.CELL_TYPE_STRING:
			c1.setCellValue((String) content);
			break;
		case Cell.CELL_TYPE_FORMULA:
			c1.setCellFormula((String) content);
			break;
		default:
			c1.setCellValue((String) content);// 默认的先暂时全用这个
			//System.out.println("未匹配到东西!");
			break;
		}
	}

}
