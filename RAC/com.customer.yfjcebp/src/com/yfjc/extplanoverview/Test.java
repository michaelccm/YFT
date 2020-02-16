package com.yfjc.extplanoverview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Test {

	public static void main(String[] args) {
		Test test = new Test();
		Map<String, List<LccPlanInfo>> LccPlanInfoMap = test.getPlanInfos();
		File file = new File("c:/LCC Report.xlsx");
//		String outputPath = "c:/LCCReport2.xlsx";
		String outputPath = System.getenv("Temp") + "\\LCCExtPlanOverviewReport_" + test.getTimestamp();
		//System.out.println("outputPath========="+outputPath);
		test.writeReport(LccPlanInfoMap,file,outputPath);
	}

	private Map<String, List<LccPlanInfo>> getPlanInfos() {
		Map<String, List<LccPlanInfo>> LccPlanInfoMap = new HashMap<String, List<LccPlanInfo>>();
		List<LccPlanInfo> lccPlanInfos = new ArrayList<LccPlanInfo>();
		for (int i = 0; i < 5; i++) {
			LccPlanInfo lccPlanInfo = new LccPlanInfo();
			lccPlanInfo.setDivision("division" + i);
			lccPlanInfo.setExtSupportUserName("extSupportUserName" + i);
			lccPlanInfo.setLoginUser("loginUser" + i);
			lccPlanInfo.setSection("section" + i);
			lccPlanInfo.setNormalHours("normalHours" + i);
			lccPlanInfo.setProcessOwner("processOwner" + i);
			lccPlanInfo.setRate("rate" + i);
			lccPlanInfo.setRate(String.valueOf(i));
			lccPlanInfo.setLaborCost("laborCost" + i);
			lccPlanInfo.setCurrentProcess("currentProcess" + i);
			lccPlanInfo.setReleaseStatus("releaseStatus" + i);
			lccPlanInfo.setActualNormalHours("actualNormalHours" + i);
			lccPlanInfo.setNonbillableHours("nonbillableHours" + i);
			lccPlanInfo.setResourceUtilization("resourceUtilization" + i);
			
			lccPlanInfos.add(lccPlanInfo);
		}
		LccPlanInfoMap.put("Division 1", lccPlanInfos);
		LccPlanInfoMap.put("Division 2", lccPlanInfos);
		LccPlanInfoMap.put("Division 3", lccPlanInfos);
		return LccPlanInfoMap;
	}

	private void writeReport(Map<String, List<LccPlanInfo>> LccPlanInfoMap, File inputFile, String outputPath) {
		try {
			FileInputStream fileInputStream = new FileInputStream(inputFile);
			FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
			XSSFWorkbook inputWorkbook = new XSSFWorkbook(fileInputStream);
			XSSFSheet sheet1 = inputWorkbook.getSheetAt(0);
			Iterator<String> iterator = LccPlanInfoMap.keySet().iterator();
			while (iterator.hasNext()) {
				String division = (String) iterator.next();
				List<LccPlanInfo> lccPlanInfos = LccPlanInfoMap.get(division);
				writeLccPlanInfoToSheet(inputWorkbook, sheet1, division, lccPlanInfos);
			}
			inputWorkbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeLccPlanInfoToSheet(XSSFWorkbook inputWorkbook, XSSFSheet sheet1, String division, List<LccPlanInfo> lccPlanInfos) {
		int addRowCount = 0;
		int startCol = 0;
		int startWriteRow = 1;
		int lastRowNum = sheet1.getLastRowNum();
		for (int i = 0; i < lastRowNum + 1; i++) {
			String value = sheet1.getRow(i).getCell(0).getStringCellValue().trim();
			if (division.equals(value)) {
				startWriteRow = i;
				break;
			}
		}

		Row copyRow = sheet1.getRow(startWriteRow);
		for (int m = startCol; m < lccPlanInfos.size() + startCol; m++) {
			LccPlanInfo lccPlanInfo = lccPlanInfos.get(m);
			addRowCount++;
			sheet1.shiftRows(startWriteRow + addRowCount, sheet1.getLastRowNum(), 1, true, false);
			Row newRow = sheet1.createRow(startWriteRow + addRowCount);
			copyRow(inputWorkbook, copyRow, newRow, false);
			newRow.getCell(startCol).setCellValue("     "+lccPlanInfo.getExtSupportUserName());
			newRow.getCell(startCol + 1).setCellValue(lccPlanInfo.getLoginUser());
			newRow.getCell(startCol + 2).setCellValue(lccPlanInfo.getSection());
			newRow.getCell(startCol + 3).setCellValue(lccPlanInfo.getNormalHours());
			newRow.getCell(startCol + 4).setCellValue(lccPlanInfo.getProcessOwner());
			newRow.getCell(startCol + 5).setCellValue(lccPlanInfo.getRate());
			newRow.getCell(startCol + 6).setCellValue(lccPlanInfo.getLaborCost());
			newRow.getCell(startCol + 7).setCellValue(lccPlanInfo.getCurrentProcess());
			newRow.getCell(startCol + 8).setCellValue(lccPlanInfo.getReleaseStatus());
			newRow.getCell(startCol + 9).setCellValue(lccPlanInfo.getActualNormalHours());
			newRow.getCell(startCol + 10).setCellValue(lccPlanInfo.getNonbillableHours());
			newRow.getCell(startCol + 11).setCellValue(lccPlanInfo.getResourceUtilization());
		}
	}

	class LccPlanInfo {
		private String division = "";
		private String extSupportUserName = "";
		private String loginUser = "";
		private String section = "";
		private String normalHours = "";
		private String processOwner = "";
		private String rate = "";
		private String laborCost = "";
		private String currentProcess = "";
		private String releaseStatus = "";
		private String actualNormalHours = "";
		private String nonbillableHours = "";
		private String resourceUtilization = "";

		public String getDivision() {
			return division;
		}

		public void setDivision(String division) {
			this.division = division;
		}

		public String getExtSupportUserName() {
			return extSupportUserName;
		}

		public void setExtSupportUserName(String extSupportUserName) {
			this.extSupportUserName = extSupportUserName;
		}

		public String getLoginUser() {
			return loginUser;
		}

		public void setLoginUser(String loginUser) {
			this.loginUser = loginUser;
		}

		public String getSection() {
			return section;
		}

		public void setSection(String section) {
			this.section = section;
		}

		public String getNormalHours() {
			return normalHours;
		}

		public void setNormalHours(String normalHours) {
			this.normalHours = normalHours;
		}

		public String getProcessOwner() {
			return processOwner;
		}

		public void setProcessOwner(String processOwner) {
			this.processOwner = processOwner;
		}

		public String getRate() {
			return rate;
		}

		public void setRate(String rate) {
			this.rate = rate;
		}

		public String getLaborCost() {
			return laborCost;
		}

		public void setLaborCost(String laborCost) {
			this.laborCost = laborCost;
		}

		public String getCurrentProcess() {
			return currentProcess;
		}

		public void setCurrentProcess(String currentProcess) {
			this.currentProcess = currentProcess;
		}

		public String getReleaseStatus() {
			return releaseStatus;
		}

		public void setReleaseStatus(String releaseStatus) {
			this.releaseStatus = releaseStatus;
		}

		public String getActualNormalHours() {
			return actualNormalHours;
		}

		public void setActualNormalHours(String actualNormalHours) {
			this.actualNormalHours = actualNormalHours;
		}

		public String getNonbillableHours() {
			return nonbillableHours;
		}

		public void setNonbillableHours(String nonbillableHours) {
			this.nonbillableHours = nonbillableHours;
		}

		public String getResourceUtilization() {
			return resourceUtilization;
		}

		public void setResourceUtilization(String resourceUtilization) {
			this.resourceUtilization = resourceUtilization;
		}
	}

	public static void copyRow(XSSFWorkbook wb, Row fromRow, Row toRow, boolean copyValueFlag) {
		int cellNum = fromRow.getLastCellNum();
		for (int i = 0; i < cellNum; i++) {
			copyCell(wb, fromRow.getCell(i), toRow.createCell(i), copyValueFlag);
		}

	}

	public static void copyCell(XSSFWorkbook wb, Cell srcCell, Cell distCell, boolean copyValueFlag) {
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

	public static void copyCellStyle(CellStyle fromStyle, CellStyle toStyle) {
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
		// toStyle.setFillForegroundColor(HSSFColor.AUTOMATIC.index);

		toStyle.setDataFormat(fromStyle.getDataFormat());
		// toStyle.setFillPattern(fromStyle.getFillPattern());// 复制后颜色为黑色

		toStyle.setHidden(fromStyle.getHidden());
		toStyle.setIndention(fromStyle.getIndention());// 首行缩进
		toStyle.setLocked(fromStyle.getLocked());
		toStyle.setRotation(fromStyle.getRotation());// 旋转
		toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
		toStyle.setWrapText(fromStyle.getWrapText());

	}

	public static void writeDataToExcel(int row, int column, Sheet sheet, Object content, int cellType, CellStyle cellStyle) throws Exception {
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
	public String getTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}

}
