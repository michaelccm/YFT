package com.yfjc.extplanoverview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjc.lccreport.ProgressBarThread;

public class ExtPlanOverviewReport {

	public String yearMonth;
	public TCSession session;
	private Registry registry = null;
	private String YFJC_SearchLCCForm_query = "YFJC_SearchLCCForm";
	private String JCI6_NormalHours_lov = "JCI6_NormalHours";
	private String YFJC_SearchExtDayHrReleased_query = "YFJC_SearchExtDayHrReleased";
	private Map<String, List<LccPlanInfo>> LccPlanInfoMap;
	private File templateFile;
	private XSSFUtil xSSFUtil;
	private ProgressBarThread progressbarthread;

	public ExtPlanOverviewReport(String yearMonth, File file) {
		this.yearMonth = yearMonth;
		this.templateFile = file;
		this.xSSFUtil = new XSSFUtil();
		registry = Registry.getRegistry(this);
		session = (TCSession) AIFUtility.getCurrentApplication().getSession();
		new Thread() {
			@Override
			public void run() {
				progressbarthread = new ProgressBarThread(registry.getString("progress.title"), registry.getString("progress.info"));
				progressbarthread.start();
				getReportData();
				//System.out.println("LccPlanInfoMap==========" + LccPlanInfoMap);
				if ((LccPlanInfoMap != null) && (LccPlanInfoMap.size() > 0)) {
					String outputPath = System.getenv("Temp") + "\\LCCExtPlanOverviewReport_" + getTimestamp() + ".xlsx";
					writeReport(LccPlanInfoMap, templateFile, outputPath);
					progressbarthread.stopBar();
					openOutputFile(outputPath);
				} else {
					progressbarthread.stopBar();
					MessageBox.post(registry.getString("messagebox.havaNoValidData"), "WARNING", MessageBox.WARNING);
					return;
				}
			}
		}.start();
	}

	public void openOutputFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return;
		}
		Runtime r = Runtime.getRuntime();
		try {
			r.exec("cmd /c \"" + filePath + "\"");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(e.getLocalizedMessage(), "Error", MessageBox.ERROR);
		}
	}

	private void getReportData() {
		try {
			LccPlanInfoMap = new HashMap<String, List<LccPlanInfo>>();
			TCComponentUser user = session.getUser();
			String userid = user.getUserId();
			String object_name = userid + "_" + yearMonth + "*";
			// String rosedale1_2015.01*
			// //System.out.println("object_name========" + object_name);
			String entries[] = { "object_name" };
			String values[] = { object_name };
			TCComponent[] components = query(session, YFJC_SearchLCCForm_query, entries, values);
			// //System.out.println("components========" + components);
			if (components != null) {
				// //System.out.println(" components.length========" +
				// components.length);
				for (int i = 0; i < components.length; i++) {
					getLccFormInfo(components[i]);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
			progressbarthread.stopBar();
			MessageBox.post(e.getLocalizedMessage(), "Error", MessageBox.ERROR);
		}
	}

	private void getLccFormInfo(TCComponent lccForm) {
		try {
			String lccFormPropertyNames[] = { "jci6_Division", "jci6_ExtSupportArray" };
			String extSupportPropertyNames[] = { "jci6_ownProxy", "jci6_Section", "jci6_Percent", "owning_user", "jci6_ownProxy", "jci6_UserName" };
			String extWorkDayHrPropertyNames[] = { "jci6_Hour", "jci6_ExtraHour" };
			NumberFormat percentNumberFormat = NumberFormat.getPercentInstance();
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
			// String currentYearMonth = sdf.format(new Date());
			ListOfValuesInfo normalHoursListOfValuesInfo = TCComponentListOfValuesType.findLOVByName(session, JCI6_NormalHours_lov).getListOfValues();
			String[] normalHoursDescriptions = normalHoursListOfValuesInfo.getDescriptions();
			String[] normalHoursListOfValue = normalHoursListOfValuesInfo.getStringListOfValues();
			int currentMonthNormalHours = 0;
			for (int i = 0; i < normalHoursListOfValue.length; i++) {
				if (yearMonth.equals(normalHoursListOfValue[i])) {
					currentMonthNormalHours = Integer.valueOf(normalHoursDescriptions[i]);
				}
			}
			String division = "";
			String loginUser = session.getUser().toString();
			TCProperty[] lccFormProperties = lccForm.getTCProperties(lccFormPropertyNames);
			TCComponent jci6_Division = lccFormProperties[0].getReferenceValue();
			if (jci6_Division != null) {
				division = jci6_Division.getProperty("object_name").trim();
			}
			if (division.length() == 0) {
				return;
			}

			String currentProcess = "";
			String releaseStatus = getProcessStatus(lccForm);
			if (releaseStatus.equals("InProgress")) {
				// process_stage Test Review/122730/A;1-ccc,新建 “审核”任务
				// 1/122730/A;1-ccc
				// process_stage_list [Test Review, 新建 “审核”任务 1]
				TCComponent[] stage_list = lccForm.getReferenceListProperty("process_stage_list");
				int currentTaskIndex = stage_list.length - 1;
				currentProcess = stage_list[currentTaskIndex].toDisplayString();
			}
			List<LccPlanInfo> LccPlanInfos = new ArrayList<LccPlanInfo>();
			if (LccPlanInfoMap.containsKey(division)) {
				LccPlanInfos = LccPlanInfoMap.get(division);
				LccPlanInfoMap.remove(division);
			}
			TCComponent[] extSupports = lccFormProperties[1].getReferenceValueArray();
			for (int i = 0; i < extSupports.length; i++) {
				TCProperty[] extSupportProperties = extSupports[i].getTCProperties(extSupportPropertyNames);
				String section = "";
				if (extSupportProperties[1].getReferenceValue() != null) {
					section = extSupportProperties[1].getReferenceValue().getProperty("object_name");
				}
				double normalHours = currentMonthNormalHours * extSupportProperties[2].getDoubleValue() / 100;
				String processOwner = extSupportProperties[3].getDisplayValue();
				double rate = 0;
				double actualNormalHours = 0;
				double nonbillableHours = 0;

				if (extSupportProperties[4].getReferenceValue() != null) {
					AIFComponentContext[] secondaryComponents = extSupportProperties[4].getReferenceValue().getSecondary();
					if (secondaryComponents != null) {
						for (int m = 0; m < secondaryComponents.length; m++) {
							if ("CostValue".equals(secondaryComponents[m].getComponent().getType())) {
								String cost = secondaryComponents[m].getComponent().getProperty("cost");
								if (cost.trim().length() > 0) {
									rate = Double.valueOf(cost);
								}
							}
						}
					}
				}
				String extSupportUserName = extSupportProperties[5].getStringValue().trim();
				String entries[] = { "Year", "Month", "UserName" };
				// //System.out.println("yearMonth==" + yearMonth);

				String[] yearMonths = yearMonth.split("\\.");
				String values[] = { yearMonths[0], yearMonths[1], extSupportUserName };
				TCComponent[] extWorkDayHrs = query(session, YFJC_SearchExtDayHrReleased_query, entries, values);
				if ((extWorkDayHrs != null) && (extWorkDayHrs.length > 0)) {
					for (int m = 0; m < extWorkDayHrs.length; m++) {
						TCProperty[] extWorkDayHrProperties = extWorkDayHrs[m].getTCProperties(extWorkDayHrPropertyNames);
						double actualNormalHours1 = extWorkDayHrProperties[0].getDoubleValue();
						double nonbillableHours1 = extWorkDayHrProperties[1].getDoubleValue();
						actualNormalHours = actualNormalHours + actualNormalHours1;
						nonbillableHours = nonbillableHours + nonbillableHours1;
					}
				}

				double laborCost = normalHours * rate;
				double resourceUtilization = 0;
				if (normalHours != 0) {
					resourceUtilization = actualNormalHours / normalHours;
				}
				LccPlanInfo lccPlanInfo = new LccPlanInfo();
				lccPlanInfo.setDivision(division);
				lccPlanInfo.setExtSupportUserName(extSupportUserName);
				lccPlanInfo.setLoginUser(loginUser);
				lccPlanInfo.setSection(section);
				lccPlanInfo.setNormalHours(String.valueOf(normalHours));
				lccPlanInfo.setProcessOwner(processOwner);
				lccPlanInfo.setRate(String.valueOf(rate));
				lccPlanInfo.setLaborCost(String.valueOf(laborCost));
				lccPlanInfo.setCurrentProcess(currentProcess);
				lccPlanInfo.setReleaseStatus(releaseStatus);
				lccPlanInfo.setActualNormalHours(String.valueOf(actualNormalHours));
				lccPlanInfo.setNonbillableHours(String.valueOf(nonbillableHours));
				lccPlanInfo.setResourceUtilization(String.valueOf(percentNumberFormat.format(resourceUtilization)));
				LccPlanInfos.add(lccPlanInfo);
				// lccPlanInfo.printfPlanInfo(lccPlanInfo);
			}
			LccPlanInfoMap.put(division, LccPlanInfos);
		} catch (TCException e) {
			e.printStackTrace();
			progressbarthread.stopBar();
			MessageBox.post(e.getLocalizedMessage(), "Error", MessageBox.ERROR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getProcessStatus(TCComponent component) {
		String status = "Draft";
		try {
			TCComponent[] release_list = component.getTCProperty("release_status_list").getReferenceValueArray();
			TCComponent[] process_list = component.getTCProperty("process_stage_list").getReferenceValueArray();
			if ((release_list != null) && (release_list.length > 0)) {
				status = "Approved";
			} else {
				if ((process_list != null) && (process_list.length > 0)) {
					status = "InProgress";
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
			progressbarthread.stopBar();
			MessageBox.post(e.getLocalizedMessage(), "Error", MessageBox.ERROR);
		}
		return status;
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
			progressbarthread.stopBar();
			MessageBox.post(e.getLocalizedMessage(), "Error", MessageBox.ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			progressbarthread.stopBar();
			MessageBox.post(e.getLocalizedMessage(), "Error", MessageBox.ERROR);
		}
	}

	private void writeLccPlanInfoToSheet(XSSFWorkbook inputWorkbook, XSSFSheet sheet1, String division, List<LccPlanInfo> lccPlanInfos) {
		try {
			int addRowCount = 0;
			int startCol = 0;
			int startWriteRow = 1;
			int lastRowNum = sheet1.getLastRowNum();
			for (int i = 0; i < lastRowNum + 1; i++) {
				if (sheet1.getRow(i) != null) {
					if (sheet1.getRow(i).getCell(0) != null) {
						if (sheet1.getRow(i).getCell(0).getStringCellValue() != null) {
							String value = sheet1.getRow(i).getCell(0).getStringCellValue().trim();
							if ((i == 1)) {
								if (value.equals("Created by:")) {
									sheet1.getRow(i).getCell(1).setCellValue(session.getUser().getProperty("user_name"));
								} else if (value.equals("Created date:")) {
									sheet1.getRow(i).getCell(1).setCellValue(new Date().toLocaleString());
								}
							}
							if (division.equals(value)) {
								startWriteRow = i;
								break;
							}
						}
					}
				}
			}

			Row copyRow = sheet1.getRow(startWriteRow);
			for (int m = startCol; m < lccPlanInfos.size() + startCol; m++) {
				LccPlanInfo lccPlanInfo = lccPlanInfos.get(m);
				addRowCount++;
				sheet1.shiftRows(startWriteRow + addRowCount, lastRowNum + addRowCount, 1, true, false);
				Row newRow = sheet1.createRow(startWriteRow + addRowCount);
				xSSFUtil.copyRow(inputWorkbook, copyRow, newRow, false);
				newRow.getCell(startCol).setCellValue(lccPlanInfo.getExtSupportUserName());
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
		} catch (TCException e) {
			e.printStackTrace();
			progressbarthread.stopBar();
			MessageBox.post(e.getLocalizedMessage(), "Error", MessageBox.ERROR);
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

		public void printfPlanInfo(LccPlanInfo lccPlanInfo) {
			//System.out.println("division=====" + lccPlanInfo.getDivision());
			//System.out.println("extSupportUserName=====" + lccPlanInfo.getExtSupportUserName());
			//System.out.println("loginUser=====" + lccPlanInfo.getLoginUser());
			//System.out.println("section=====" + lccPlanInfo.getSection());
			//System.out.println("normalHours=====" + lccPlanInfo.getNormalHours());
			//System.out.println("processOwner=====" + lccPlanInfo.getProcessOwner());
			//System.out.println("rate=====" + lccPlanInfo.getRate());
			//System.out.println("laborCost=====" + lccPlanInfo.getLaborCost());
			//System.out.println("currentProcess=====" + lccPlanInfo.getCurrentProcess());
			//System.out.println("releaseStatus=====" + lccPlanInfo.getReleaseStatus());
			//System.out.println("actualNormalHours=====" + lccPlanInfo.getActualNormalHours());
			//System.out.println("nonbillableHours=====" + lccPlanInfo.getNonbillableHours());
			//System.out.println("resourceUtilization=====" + lccPlanInfo.getResourceUtilization());
		}

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

	public TCComponent[] query(TCSession session, String query_name, String[] arg1, String[] arg2) {
		TCComponentContextList imancomponentcontextlist = null;
		TCComponent[] component = null;
		try {
			TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
			TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype.find(query_name);
			TCTextService imantextservice = session.getTextService();
			String[] queryAttribute = new String[arg1.length];
			for (int i = 0; i < arg1.length; ++i)
				queryAttribute[i] = imantextservice.getTextValue(arg1[i]);

			String[] queryValues = new String[arg2.length];
			for (int i = 0; i < arg2.length; ++i)
				queryValues[i] = arg2[i];

			imancomponentcontextlist = imancomponentquery.getExecuteResultsList(queryAttribute, queryValues);
			component = imancomponentcontextlist.toTCComponentArray();
		} catch (Exception ex) {
			ex.printStackTrace();
			progressbarthread.stopBar();
			MessageBox.post(ex.getLocalizedMessage(), "Error", MessageBox.ERROR);
		}
		return component;
	}

	public String getTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}
}
