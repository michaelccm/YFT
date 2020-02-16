package com.yfjc.lccreport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	private String filePath;
	private String outPath;
	private XSSFWorkbook wb = null;
	private XSSFSheet sheetTemplate = null;
	private XSSFSheet sheet = null;
	private FileInputStream is;
	private FileOutputStream fileOut;
	private HashMap colMap;
	private Vector csVec;
	private Vector<Integer> sumUserCostInColVec;
	private Vector<Integer> sumTotalCostInColVec;
	private Vector<Integer> notesInColVec;
	private String title;
	private int titleStartRow = 1;
	private int dataStartRow = 4;
	private int[] index = new int[] { 0, 1, 2, 3 };
	private String tmplateSheetName = "template";
	private String sheetName = "Program-Division-ExtSupport";
	private XSSFCellStyle valueStyle;
	private XSSFCellStyle valueStyle2;
	private XSSFCellStyle valueStyle3;
	private StringBuffer col_sb = new StringBuffer();
	private String[] str = new String[] { "ForecastHR", "ForecastCost",
			"ActualHR", "ActualCost" };
	private int formularInRow = 10;
	private Vector<DivisionUserCol> actual_cost_cols = new Vector<DivisionUserCol>();// Acutal_Cost������
	private Map<String, Integer> lcc_buget_cols = new HashMap<String, Integer>();
	private Map<String, Integer> notes_cols = new HashMap<String, Integer>();
	private Map<Integer, Integer> need_delete_cols = new HashMap<Integer, Integer>();

	public ExcelUtil(String filePath, String outPath) {
		this.filePath = filePath;
		this.outPath = outPath;
	}

	/**
	 * �򿪹�����
	 */
	public void open() {
		try {
			is = new FileInputStream(filePath);
			fileOut = new FileOutputStream(outPath);
			try {
				wb = new XSSFWorkbook(is);
				sheet = wb.createSheet(sheetName);
				sheetTemplate = wb.getSheet(tmplateSheetName);
				csVec = getCellStylesVec();
				getValueStyle();
				getValueStyle2();
				getValueStyle3();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �ر�������
	 */
	public void close() {
		try {
			wb.removeSheetAt(0);
			wb.write(fileOut);
			fileOut.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ָ�����ƵĹ�����
	 */
	public XSSFSheet getSheet(String sheetName) {
		return wb.getSheet(sheetName);
	}

	/**
	 * �õ����һ��
	 */
	private int getEndCol(XSSFSheet sheet) {
		XSSFRow row = sheet.getRow(titleStartRow);
		return row.getLastCellNum();
	}

	/**
	 * ���ݴ����ַ��������Ӧ��������
	 */
	public int getColIndex(String str) {
		char[] chars = str.toCharArray();
		if (chars.length < 2) {
			return (chars[0] - 65);
		} else {
			return ((26 * (chars[0] - 65 + 1)) + chars[1] - 65);
		}
	}

	/**
	 * ���ָ������û��Լ������µĵ�Ԫ���������
	 */
	public int getTargetCellCol(String g, String u, String type) {
		int col = 0;
		HashMap map = (HashMap) colMap.get(g);
		if (type.equals("Budget")) {
			if (map != null) {
				col = Integer.parseInt(map.get("Budget").toString());
			}
		} else {

			if (map != null) {
				Object obj = map.get(u);
				int[] temp = (int[]) obj;
				for (int i = 0; i < str.length; i++) {
					if (str[i].equals(type)) {
						col = temp[i];
						break;
					}
				}
			}
		}
		return col;
	}

	/**
	 * ���ָ����Ԫ��ķ��
	 * 
	 * @return
	 */
	public Vector getCellStylesVec() {
		Vector vec = new Vector();
		// ����Ϊ
		// Group��LCC_Budget��User��fCost��aCost��notes��totalHourse��totalCost��title��
		// GrandTotal��cost��NO.��ProgramID��ProgramName����ע�����ž���yellow��red��F5
		int[] row = new int[] { 1, 2, 2, 3, 3, 1, 1, 1, 0, 10, 10, 4, 4, 4, 12,
				12, 4, 4, 4, 10 };
		String[] col = new String[] { "N", "N", "O", "O", "Q", "W", "EB", "EC",
				"B", "C", "F", "A", "B", "C", "B", "M", "J", "M", "F", "Q" };
		for (int i = 0; i < row.length; i++) {
			XSSFCell cell = sheetTemplate.getRow(row[i]).getCell(
					getColIndex(col[i]));
			vec.add(cell.getCellStyle());
		}
		return vec;
	}

	/**
	 * �ϲ���Ԫ��
	 */
	public void mergeRegion(CellStyle cellStyle, int firstRow, int lastRow,
			int firstCol, int lastCol) {
		CellRangeAddress region = new CellRangeAddress(firstRow, lastRow,
				firstCol, lastCol);
		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
			XSSFRow row = (XSSFRow) CellUtil.getRow(i, sheet);
			for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
				XSSFCell cell = (XSSFCell) CellUtil.getCell(row, j);
				cell.setCellStyle(cellStyle);
			}
		}
		sheet.addMergedRegion(region);
	}

	/**
	 * ����������
	 */
	public int paintGroup(LinkedHashMap map) {
		int total_hours_column = 12;
		colMap = new HashMap();
		sumUserCostInColVec = new Vector<Integer>();
		sumTotalCostInColVec = new Vector<Integer>();
		notesInColVec = new Vector<Integer>();
		int titleInRow = 0;
		int titleInCol = 0;
		int groupInRow = 1;// �����ڵ��к�
		int budgetInRow = 2;// buget�����к�
		int userInRow = 2;// user������
		int costInRow = 3;// cost������
		int noteInRow = 1;// note������
		int groupInCol = getColIndex("N");// ���Ǵ�"N"��ʼ
		int budgetInCol = groupInCol;// buget��"N"��ʼ
		int userStartedCol = groupInCol + 1;// user��"O"��ʼ
		int budgetMergerRowNum = 2;// LCC_buget�ϲ�����
		int noteMergerRowNum = 3;// note�ϲ�����
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			HashMap hm = new HashMap();
			budgetInCol = groupInCol;
			userStartedCol = groupInCol + 1;// �û��Ǵ�"O"��ʼ
			// costStartedCol = userStartedCol;

			Entry entry = (Entry) iterator.next();
			String g = (String) entry.getKey();// Division
			Vector v = (Vector) entry.getValue();// ĳ��Division�е��û�����
			int userNum = v.size();
			if (userNum > 1) {
				// paint Budget
				mergeRegion((CellStyle) csVec.get(1), budgetInRow, budgetInRow
						+ budgetMergerRowNum - 1, budgetInCol, budgetInCol);
				sheet.getRow(budgetInRow).getCell(budgetInCol)
						.setCellValue("LCC_Budget");
				hm.put("Budget", budgetInCol);
				lcc_buget_cols.put(g, budgetInCol);
				int divisionIndex = isDivisionExist(g);
				DivisionUserCol diUserCol = null;
				Vector<String> userNameVec = null;
				Vector<Integer> colsVec = null;
				if (divisionIndex != -1) {
					diUserCol = actual_cost_cols.get(divisionIndex);
					userNameVec = diUserCol.getUserNamesVec();
					colsVec = diUserCol.getColsVec();
				} else {
					userNameVec = new Vector<String>();
					colsVec = new Vector<Integer>();

					// diUserCol = new DivisionUserCol(g,userNameVec,colsVec);
				}
				// paint User
				for (int i = 0; i < userNum; i++) {
					String u = v.get(i).toString();
					if (!u.equals("")) {
						mergeRegion((CellStyle) csVec.get(2), userInRow,
								userInRow, userStartedCol, userStartedCol + 3);
						XSSFCellStyle cs = (XSSFCellStyle) csVec.get(2);
						cs.setBorderBottom(CellStyle.BORDER_THIN); // �±߿�
						cs.setBorderLeft(CellStyle.BORDER_THIN);// ��߿�
						cs.setBorderTop(CellStyle.BORDER_THIN);// �ϱ߿�
						cs.setBorderRight(CellStyle.BORDER_THIN);// �ұ߿�
						cs.setAlignment(CellStyle.ALIGN_RIGHT);
						XSSFCell uCell = sheet.getRow(userInRow).getCell(
								userStartedCol);
						uCell.setCellValue(u);
						uCell.setCellStyle(cs);
						// paint cost
						XSSFRow row = sheet.getRow(costInRow);
						int[] cols = new int[4];

						for (int j = 0; j < 4; j++) {
							XSSFCell cell = (XSSFCell) CellUtil.getCell(row,
									userStartedCol + j);
							cols[j] = userStartedCol + j;
							if (j == 0 || j == 1) {
								if (j == 0) {
									cell.setCellValue("Forecast_hr");
								} else {
									cell.setCellValue("Forecast_Cost");
								}
								cell.setCellStyle((CellStyle) csVec.get(3));
							} else {
								sumUserCostInColVec.add(cols[j]);
								if (j == 2) {
									cell.setCellValue("Acutal_hr");
								} else {
									userNameVec.add(u);
									colsVec.add(userStartedCol + j);
									cell.setCellValue("Acutal_Cost");
								}
								cell.setCellStyle((CellStyle) csVec.get(4));
							}
						}
						userStartedCol = userStartedCol + 4;
						hm.put(u, cols);
					}
					colMap.put(g, hm);
				}

				if (divisionIndex == -1) {
					diUserCol = new DivisionUserCol(g, userNameVec, colsVec);
					actual_cost_cols.add(diUserCol);
				}

				// paint group
				mergeRegion((CellStyle) csVec.get(0), groupInRow, groupInRow,
						groupInCol, groupInCol + (userNum - 1) * 4);
				XSSFRow row = sheet.getRow(groupInRow);
				XSSFCell cell = (XSSFCell) CellUtil.getCell(row, groupInCol);
				cell.setCellValue(g);
				int noteInCol = groupInCol + (userNum - 1) * 4 + 1;
				groupInCol = noteInCol + 1;// need add the "note" column

				// paint note
				mergeRegion((CellStyle) csVec.get(5), 1, 3, noteInCol, noteInCol);
				row = sheet.getRow(noteInRow);
				cell = (XSSFCell) CellUtil.getCell(row, noteInCol);
				cell.setCellValue("Notes");
				notesInColVec.add(noteInCol);
				notes_cols.put(g, noteInCol);
			}
		}
		// paint total

		mergeRegion((CellStyle) csVec.get(6), groupInRow, groupInRow + 2,
				getEndCol(sheet), getEndCol(sheet));
		int total_hours = getEndCol(sheet) - 1;
		sumTotalCostInColVec.add(total_hours);
		total_hours_column = total_hours;
		XSSFCell cell = (XSSFCell) CellUtil.getCell(sheet.getRow(groupInRow),
				total_hours);
		cell.setCellValue("Total Hours");
		mergeRegion((CellStyle) csVec.get(7), groupInRow, groupInRow + 2,
				getEndCol(sheet), getEndCol(sheet));
		cell = (XSSFCell) CellUtil.getCell(sheet.getRow(groupInRow),
				(getEndCol(sheet) - 1));
		sumTotalCostInColVec.add(getEndCol(sheet) - 1);
		cell.setCellValue("Total Cost");

		// paint title
		mergeRegion((CellStyle) csVec.get(8), titleInRow, titleInRow,
				titleInCol, getEndCol(sheet) - 1);
		sheet.getRow(titleInRow).getCell(titleInCol).setCellValue(title);
		return total_hours_column;
	}

	/**
	 * ������֮ǰ������
	 */
	public void paintProgramInfoRegion(int report_year, String month_pro_name) {
		// copy cell style
		int startRow = 1;
		int startCol = 0;
		HashMap hm = new HashMap();
		String[] str = new String[] { "A", "B", "C", "D", "E", "F", "G", "H",
				"I", "J", "K", "L", "M" };
		XSSFRow row = sheetTemplate.getRow(startRow);
		String[] array = new String[str.length];
		for (int i = 0; i < str.length; i++) {
			XSSFCell cell = (XSSFCell) CellUtil.getCell(row,
					getColIndex(str[i]));
			hm.put(str[i], cell.getCellStyle());
			array[i] = cell.getStringCellValue();

		}
		// get title
		row = sheetTemplate.getRow(0);

		// �ⲿ֧���¶���Ŀ��ʱ���˱���Jan,2014
		// title = ((XSSFCell) CellUtil.getCell(row, 1)).getStringCellValue();

		title = "�ⲿ֧���¶���Ŀ��ʱ���˱���" + month_pro_name.split("_")[1] + ","
				+ report_year;

		// merge cell
		for (int i = 0; i < str.length; i++) {
			mergeRegion((CellStyle) hm.get(str[i]), startRow, startRow + 2,
					startCol + i, startCol + i);
		}

		XSSFRow targetRow = sheet.getRow(startRow);
		for (int i = 0; i < str.length; i++) {
			XSSFCell cell = (XSSFCell) CellUtil
					.getCell(targetRow, startCol + i);
			cell.setCellValue(array[i]);
		}
	}

	/**
	 * ����sheet�ײ�����
	 */
	public void paintBottomRow(int rowNum) {
		// ����ΪLabor Total Cost_Actual��Labor Total Cost_Forecast��Labor Total
		// Cost_Budget
		// Labor Total Cost_EAC��LCC Cost_Budget��LCC Cost_EAC��LCC Cost_Actual��
		// LCC Cost_Forecast��LCC Cost_Budget��LCC Cost_EAC,Acutal_hr��Total Hours
		String[] col = new String[] { "F", "G", "H", "I", "J", "K", "L", "M",
				"Q", "EB" };
		XSSFRow row = sheetTemplate.getRow(formularInRow);
		// GrandTotal in C
		XSSFRow lastRow = sheet.createRow(rowNum);
		XSSFCell cell1 = (XSSFCell) CellUtil.getCell(lastRow, 0);
		XSSFCell cell2 = (XSSFCell) CellUtil.getCell(lastRow, 1);
		cell1.setCellStyle((CellStyle) csVec.get(13));
		cell2.setCellStyle((CellStyle) csVec.get(13));

		XSSFCell cell = (XSSFCell) CellUtil.getCell(lastRow, 2);
		cell.setCellStyle((CellStyle) csVec.get(9));
		cell.setCellValue("Grand Total");
		// blanket1 in D
		cell = (XSSFCell) CellUtil.getCell(lastRow, 3);
		cell.setCellStyle((CellStyle) csVec.get(10));
		// blanket2 int E
		cell = (XSSFCell) CellUtil.getCell(lastRow, 4);
		cell.setCellStyle((CellStyle) csVec.get(10));

		// paint the lastRow
		row = sheet.createRow(rowNum + 2);
		cell = row.createCell(1);
		cell.setCellStyle((CellStyle) csVec.get(14));
		cell.setCellValue("��ע:");
		cell = row.createCell(2);
		cell.setCellStyle((CellStyle) csVec.get(14));
		cell.setCellValue("��ɫ������ʾActual>Budget");
		cell = row.createCell(getColIndex("M"));
		cell.setCellStyle((CellStyle) csVec.get(15));
		cell.setCellValue("���ž���Division");

		row = sheet.createRow(rowNum + 3);
		cell = row.createCell(2);
		cell.setCellStyle((CellStyle) csVec.get(14));
		cell.setCellValue("��ɫ������ʾEAC>Budget");
		cell = row.createCell(getColIndex("M"));
		cell.setCellStyle((CellStyle) csVec.get(15));
		cell.setCellValue("����Date");

		// modify 2014-6-23
		setSumFormular(lastRow);

		// modify 2014-7-2
		getColSb(lastRow);

	}

	public void fillValue(int rownum, int colNum, String value) {
		if (value != null && !value.equals("")) {
			XSSFRow row = sheet.getRow(rownum);
			if (row == null) {
				row = sheet.createRow(rownum);
			}
			XSSFCell cell = row.createCell(colNum);
			if (colNum >= 5) {
				cell.setCellStyle((CellStyle) csVec.get(18));
			} else {
				cell.setCellStyle((CellStyle) csVec.get(13));
			}
			if (colNum >= 5) {
				cell.setCellValue(Double.parseDouble(value));
			} else {
				cell.setCellValue(value);
			}
		} else {
			XSSFRow row = sheet.getRow(rownum);
			if (row == null) {
				row = sheet.createRow(rownum);
			}
			XSSFCell cell = row.createCell(colNum);
			if (colNum >= 5) {
				cell.setCellStyle((CellStyle) csVec.get(18));
			} else {
				cell.setCellStyle((CellStyle) csVec.get(13));
			}
			cell.setCellValue("");
		}
	}

	/**
	 * �õ���ʽ�ַ���
	 */
	public String dealWithFormular(String s, int row) {
		String[] array = s.split("\\(");
		String name = array[0];
		String col = array[1].substring(0, 1);
		StringBuffer sbf = new StringBuffer();
		sbf.append(name).append("(").append(col).append(dataStartRow + 1)
				.append(":").append(col).append(row).append(")");
		return sbf.toString();
	}

	/**
	 * ��note����ɫ
	 */
	public void setNotesBackGround(int rowNum) {
		XSSFRow row = sheet.getRow(rowNum);
		for (int i = 0, len = notesInColVec.size(); i < len; i++) {
			XSSFCell cell = (XSSFCell) CellUtil.getCell(row,
					Integer.parseInt(notesInColVec.get(i).toString()));
			cell.setCellStyle((CellStyle) csVec.get(5));
		}

	}

	/**
	 * ��ָ����Ԫ����ɫ
	 */
	public void setCellColor(int startRow, int endRow) {
		String[] cols = new String[] { "H", "I", "J", "L", "M" };
		for (int i = startRow; i <= endRow; i++) {
			XSSFRow row = sheet.getRow(i);
			XSSFCell cell1 = row.getCell(getColIndex(cols[0]));
			String budget1 = cell1.getStringCellValue();
			XSSFCell cell2 = row.getCell(getColIndex(cols[1]));
			String actual1 = cell2.getStringCellValue();
			XSSFCell cell3 = row.getCell(getColIndex(cols[2]));
			String eac1 = cell3.getStringCellValue();

			XSSFCell cell4 = row.getCell(getColIndex(cols[3]));
			String budget2 = cell4.getStringCellValue();
			XSSFCell cell5 = row.getCell(getColIndex(cols[4]));
			String eac2 = cell5.getStringCellValue();

			if (Double.parseDouble(actual1) < Double.parseDouble(budget1)) {
				cell2.setCellStyle((CellStyle) csVec.get(16));
			}
			if (Double.parseDouble(eac1) < Double.parseDouble(budget1)) {
				cell3.setCellStyle((CellStyle) csVec.get(17));
			}
			if (Double.parseDouble(eac2) < Double.parseDouble(budget2)) {
				cell5.setCellStyle((CellStyle) csVec.get(17));
			}
		}
	}

	/**
	 * ����λ�ú�ֽ��д��Ԫ��
	 */
	public void fillValSetBackgroupColor(short color, int rownum, int colNum,
			String value) {
		if (value != null && !value.equals("")) {
			XSSFRow row = sheet.getRow(rownum);
			if (row == null) {
				row = sheet.createRow(rownum);
			}
			XSSFCell cell = row.createCell(colNum);
			XSSFCellStyle style = wb.createCellStyle();// ������ʽ����
			// ��������
			XSSFFont font = wb.createFont();// �����������
			font.setFontHeightInPoints((short) 12);// ���������С
			font.setFontName("arail");// ����Ϊ������
			style.setFont(font);// ��������뵽��ʽ����
			// ���ö��뷽ʽ
			style.setAlignment(CellStyle.ALIGN_RIGHT);// ˮƽ�Ҷ���
			style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// ��ֱ����
			// ���ñ߿�
			// style.setBorderTop(XSSFCellStyle.BORDER_THIN);// �ϱ߿�
			// style.setBorderBottom(XSSFCellStyle.BORDER_THIN);// �±߿�
			// style.setBorderLeft(XSSFCellStyle.BORDER_THIN);// ��߱߿�
			// style.setBorderRight(XSSFCellStyle.BORDER_THIN);// �ұ߱߿�
			style.setFillForegroundColor(color);
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);

			style.setDataFormat((short) 4);

			cell.setCellStyle(style);
			// cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(Double.parseDouble(value));
		}
	}

	// �Զ������п�
	public void autoSizeColumn() {
		int colcnt = getEndCol(sheet);
		for (int i = 0; i < colcnt; i++) {
			sheet.autoSizeColumn(i, true);
		}
	}

	public void getValueStyle() {
		valueStyle = wb.createCellStyle();// ������ʽ����
		// ��������
		XSSFFont font = wb.createFont();// �����������
		font.setFontHeightInPoints((short) 12);// ���������С
		font.setFontName("arail");// ����Ϊ������
		valueStyle.setFont(font);// ��������뵽��ʽ����
		// ���ñ߿�
		valueStyle.setBorderTop(CellStyle.BORDER_THIN);// �ϱ߿�
		valueStyle.setBorderBottom(CellStyle.BORDER_THIN);// �±߿�
		valueStyle.setBorderLeft(CellStyle.BORDER_THIN);// ��߱߿�
		valueStyle.setBorderRight(CellStyle.BORDER_THIN);// �ұ߱߿�
		valueStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT
				.getIndex());
		valueStyle.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		valueStyle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		valueStyle
				.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		// ���ö��뷽ʽ
		valueStyle.setAlignment(CellStyle.ALIGN_RIGHT);// ˮƽ�Ҷ���
		valueStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// ��ֱ����
		valueStyle.setDataFormat((short) 4);

	}

	// ���ù�ʽ
	private void setSumFormular(XSSFRow row) {
		StringBuffer sb = new StringBuffer();
		int rowNo = row.getRowNum() + 1;
		int col = getEndCol(sheet);
		if (col > 15) {
			// �ж��������Ƿ�Ϊ��
			Vector<Integer> col_has_val = new Vector<Integer>();
			for (int i = 4; i < rowNo - 1; i++) {
				XSSFRow row1 = sheet.getRow(i);
				// �ж��������Ƿ�Ϊ��
				for (int j = 13; j < col - 2; j++) {
					XSSFCell cell = row1.getCell(j);
					if (cell == null) {
						cell = row1.createCell(j);
					}
					int cellType = cell.getCellType();
					String cellValue1 = sheet.getRow(1).getCell(j)
							.getStringCellValue();
					if (cellType == Cell.CELL_TYPE_BLANK) {// ��ֵ
						if (!cellValue1.equals("Notes")) {
							cell.setCellStyle(valueStyle);
						}
					} else {
						col_has_val.add(j);
					}
				}
			}

			// ��Ҫɾ������
			int end_col = col - 3;
			int start_col = end_col;
			for (int j = col - 3; j >= 13; j--) {
				String cellValue1 = sheet.getRow(1).getCell(j)
						.getStringCellValue();
				if (cellValue1.equals("Notes")) {
					if (start_col != end_col) {
						start_col++;
						mergeRegion(valueStyle2, rowNo + 1, rowNo + 1,
								start_col, end_col);
						mergeRegion(valueStyle3, rowNo + 2, rowNo + 2,
								start_col, end_col);
					}
					// ������
					end_col = j - 1;
					start_col = end_col;

				} else {
					if (j == 13) {
						// �ϲ���Ԫ��
						mergeRegion(valueStyle2, rowNo + 1, rowNo + 1,
								start_col, end_col);
						mergeRegion(valueStyle3, rowNo + 2, rowNo + 2,
								start_col, end_col);
					}
					start_col--;
				}

				// modify 2014-6-25
				// ��ûֵ
				if (!isInVec(j, col_has_val)) {
					if (!cellValue1.equals("Notes")) {
						need_delete_cols.put(j, 0);
					}
				}
			}
		}

		col = getEndCol(sheet);
		CellStyle grandStyle = (CellStyle) csVec.get(9);
		grandStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		for (int i = 5; i < col; i++) {
			XSSFCell cell = row.getCell(i);
			if (cell == null) {
				cell = row.createCell(i);
			}
			cell.setCellStyle(grandStyle);
			if (!isNotesColumn(i)) {
				// ����ת�ɶ�Ӧ���к�
				String colName = org.apache.poi.ss.util.CellReference.convertNumToColString(i);
				// SUM(A10:B10)
				sb.setLength(0);
				sb.append("SUM(").append(colName).append(5).append(":")
						.append(colName).append(rowNo - 1).append(")");
				cell.setCellType(Cell.CELL_TYPE_FORMULA);
				cell.setCellFormula(sb.toString());
			}
		}

	}

	// �ж��Ƿ�Ϊnotes��
	private boolean isNotesColumn(int col) {
		for (int i = 0, size = notesInColVec.size(); i < size; i++) {
			if (col == notesInColVec.get(i)) {
				return true;
			}
		}
		return false;
	}

	// �ж�ָ��ֵ�Ƿ���vector��
	private boolean isInVec(int val, Vector<Integer> col_val) {
		for (int i = 0, size = col_val.size(); i < size; i++) {
			if (col_val.get(i) == val) {
				return true;
			}
		}
		return false;
	}

	public void runWsf() {
		//System.out.println("col_sb---->"+col_sb.toString());
		if (!col_sb.toString().equals("")) {
			// ����wsfɾ���հ���
			try {
				String wsf_path = WSFUtil.getFileFromClass(getClass(),
						"deleteExcelCol.wsf", System.getenv("Temp"));
				String[] paras = new String[] { col_sb.toString(), outPath };
				WSFUtil.runWsf(wsf_path, paras);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void getValueStyle2() {
		valueStyle2 = wb.createCellStyle();// ������ʽ����
		// ��������
		XSSFFont font = wb.createFont();// �����������
		font.setFontHeightInPoints((short) 11);// ���������С
		font.setFontName("����");// ����Ϊ������
		valueStyle2.setFont(font);// ��������뵽��ʽ����

		// ���ö��뷽ʽ
		valueStyle2.setAlignment(CellStyle.ALIGN_RIGHT);// ˮƽ�Ҷ���
		valueStyle2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// ��ֱ����

		// ���ñ߿�
		valueStyle2.setBorderTop(CellStyle.BORDER_NONE);
		valueStyle2.setBorderBottom(CellStyle.BORDER_THIN);// �±߿�
		valueStyle2.setBorderLeft(CellStyle.BORDER_NONE);
		valueStyle2.setBorderRight(CellStyle.BORDER_NONE);

	}

	public void getValueStyle3() {
		valueStyle3 = wb.createCellStyle();// ������ʽ����
		// ��������
		XSSFFont font = wb.createFont();// �����������
		font.setFontHeightInPoints((short) 11);// ���������С
		font.setFontName("����");// ����Ϊ������
		valueStyle3.setFont(font);// ��������뵽��ʽ����

		// ���ö��뷽ʽ
		valueStyle3.setAlignment(CellStyle.ALIGN_RIGHT);// ˮƽ�Ҷ���
		valueStyle3.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// ��ֱ����
		// ���ñ߿�
		valueStyle3.setBorderTop(CellStyle.BORDER_THIN);
		valueStyle3.setBorderBottom(CellStyle.BORDER_THIN);// �±߿�
		valueStyle3.setBorderLeft(CellStyle.BORDER_NONE);
		valueStyle3.setBorderRight(CellStyle.BORDER_NONE);

	}

	// add 2014-7-2
	private int isDivisionExist(String divisionName) {
		for (int i = 0, size = actual_cost_cols.size(); i < size; i++) {
			if (actual_cost_cols.get(i).getDivisionName().equals(divisionName)) {
				return i;
			}
		}
		return -1;
	}

	private void getColSb(XSSFRow row) {
		XSSFFormulaEvaluator formula = new XSSFFormulaEvaluator(wb);
		for (int i = 0, size = actual_cost_cols.size(); i < size; i++) {
			boolean division_null = true;
			DivisionUserCol diUserCol = actual_cost_cols.get(i);
			String divisionName = diUserCol.getDivisionName();
			Vector<String> userNames = diUserCol.getUserNamesVec();
			Vector<Integer> cols = diUserCol.getColsVec();
			for (int j = 0, len = userNames.size(); j < len; j++) {
				int actual_cost_col = cols.get(j);
				CellValue cellVal = formula.evaluate(row
						.getCell(actual_cost_col));
				CellValue cellVal2 = formula.evaluate(row
						.getCell(actual_cost_col - 1));
				if (cellVal.getNumberValue() == 0
						&& cellVal2.getNumberValue() == 0) {
					// ��Ҫɾ������
					need_delete_cols.put(actual_cost_col, 0);
					need_delete_cols.put(actual_cost_col - 1, 0);
					need_delete_cols.put(actual_cost_col - 2, 0);
					need_delete_cols.put(actual_cost_col - 3, 0);
				} else {
					division_null = false;
				}
			}
			if (division_null) {
				// ��Ҫɾ��LCC_Budget �� Notes
				need_delete_cols.put(lcc_buget_cols.get(divisionName), 0);
				need_delete_cols.put(notes_cols.get(divisionName), 0);
			}
		}

		List<Map.Entry<Integer, Integer>> list = sortMapByKey(need_delete_cols);
		for (int i = 0; i < list.size(); i++) {
			Entry<Integer, Integer> e = list.get(i);
			Integer keyVal = e.getKey();
			if (col_sb.toString().equals("")) {
				col_sb.append(keyVal + 1);
			} else {
				col_sb.append("|").append(keyVal + 1);
			}
		}

	}

	/**
	 * map����
	 * 
	 * @param map
	 * @return
	 */
	private List<Map.Entry<Integer, Integer>> sortMapByKey(
			Map<Integer, Integer> map) {
		List<Map.Entry<Integer, Integer>> charList = new ArrayList<Map.Entry<Integer, Integer>>(
				map.entrySet());
		Collections.sort(charList,
				new Comparator<Map.Entry<Integer, Integer>>() {
					@Override
					public int compare(Entry<Integer, Integer> o1,
							Entry<Integer, Integer> o2) {
						// TODO Auto-generated method stub
						if (o2.getKey() > o1.getKey()) {
							return 1;
						} else if (o1.getKey() > o2.getKey()) {
							return -1;
						} else {
							return 0;
						}
					}

				});

		return charList;
	}

	private class DivisionUserCol {
		private String divisionName;
		private Vector<String> userNamesVec;
		private Vector<Integer> colsVec;

		public DivisionUserCol(String divisionName,
				Vector<String> userNamesVec, Vector<Integer> colsVec) {
			this.divisionName = divisionName;
			this.userNamesVec = userNamesVec;
			this.colsVec = colsVec;
		}

		public Vector<Integer> getColsVec() {
			return colsVec;
		}

		public String getDivisionName() {
			return divisionName;
		}

		public Vector<String> getUserNamesVec() {
			return userNamesVec;
		}

	}
}
