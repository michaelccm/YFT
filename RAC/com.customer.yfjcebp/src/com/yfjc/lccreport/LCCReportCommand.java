package com.yfjc.lccreport;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.poi.ss.usermodel.IndexedColors;
import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class LCCReportCommand extends AbstractAIFCommand {

	private Registry registry = null;
	private File file = null;
	private Vector<TCComponentItem> programinfoVec;
	private Vector<Boolean> projectActiveVec;
	private Vector<LinkedHashMap<String, Object>> dataVec;
	private String option1 = "YFJC_LaborType";
	private String option2 = "YFJC_LCC_Report_Devision_mapping";
	private String option3 = "YFJC_LCC_Report_UserName_mapping";
	private TCComponent[] prjs = null;
	private TCSession session = null;
	private String[] laborTypeArray = null;
	private String[] groupName = null;
	private String[] userName = null;
	private int startRow = 4;// ��ݴӵ�5�п�ʼ
	private LinkedHashMap groupMap;
	private LinkedHashMap newGroupMap;
	private String[] month = new String[] { "jci6_Jan", "jci6_Feb", "jci6_Mar",
			"jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug",
			"jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
	private String[] key = new String[] { "ProgramID", "ProgramName",
			"Category", "Beloging Devision", "Labor Total Cost_Actual",
			"Labor Total Cost_Forecast", "Labor Total Cost_Budget",
			"Labor Total Cost_EAC", "LCC Total Cost_Actual",
			"LCC Total Cost_Forecast", "LCC Total Cost_Budget",
			"LCC Total Cost_EAC", "Total Hours", "Total Cost" };
	private String year_month_option_name = "YFJC_LCC_Report_Year_Month";

	private int report_year;// ��Ҫ�޸�

	private int report_month;// ��Ҫ�޸�

	private String month_pro_name;// ��Ҫ�޸�

	private TCPreferenceService preService;

	private Vector<Boolean> labor_actual_big_budget;
	private Vector<Boolean> labor_eac_big_budget;
	private Vector<Boolean> lcc_actual_big_budget;
	private Vector<Boolean> lcc_eac_big_budget;
	private DecimalFormat df;

	private double actual_hours;
	private double actual_costs;

	public LCCReportCommand(TCComponent[] arg1, File arg2) {
		prjs = arg1;
		file = arg2;
		session = (TCSession) AIFUtility.getCurrentApplication().getSession();
		registry = Registry.getRegistry("com.yfjc.lccreport.lccreport");
	}

	@Override
	public void executeModal() throws Exception {

		programinfoVec = new Vector<TCComponentItem>();
		projectActiveVec = new Vector<Boolean>();
		for (int i = 0; i < prjs.length; i++) {
			TCComponent comp = getComp(prjs[i]);
			TCComponentItem item = (TCComponentItem) comp;
			if (item != null) {
				// �õ���Ŀ�Ƿ�
				projectActiveVec.add(prjs[i].getLogicalProperty("is_active"));
				programinfoVec.add(item);// programinfoVec ���е�programinfo����
			}
		}

		// ���л����Ŀ�µ���Ŀ��Ϣ�����Ƿ�Ϊ��
		if (programinfoVec.size() == 0) {
			MessageBox.post(registry.getString("messagebox.program"),
					"WARNING", MessageBox.WARNING);
		} else {
			groupName = session.getPreferenceService().getStringArray(4,
					option2);
			userName = session.getPreferenceService()
					.getStringArray(4, option3);

			new Thread() {
				@Override
				public void run() {
					ProgressBarThread progressbarthread = new ProgressBarThread(
							registry.getString("progress.title"),
							registry.getString("progress.info"));
					progressbarthread.start();
					preService = session.getPreferenceService();
					laborTypeArray = preService.getStringArray(4, option1);
					getYearMonthFormPre();
					month_pro_name = getMonthPro(report_month);
					getInfo();
					export(file, dataVec);
					progressbarthread.stopBar();
					// open(outpath);
				}
			}.start();
		}
	}

	/**
	 * get the target infomation from the CostInfo ���costInfo�����ϵ���Ϣ
	 */
	public void getInfo() {
		df = new DecimalFormat("#.00");
		groupMap = new LinkedHashMap();
		dataVec = new Vector<LinkedHashMap<String, Object>>();
		labor_actual_big_budget = new Vector<Boolean>();
		labor_eac_big_budget = new Vector<Boolean>();
		lcc_actual_big_budget = new Vector<Boolean>();
		lcc_eac_big_budget = new Vector<Boolean>();
		// �������е���Ŀ��Ϣ����
		for (int i = 0; i < programinfoVec.size(); i++) {
			TCComponentItem programinfo = programinfoVec.get(i);
			try {
				// �õ���Ŀ��Ϣ�µ�����costinfo����
				Vector<TCComponent> pro_costinfo_vec = getCostInfoOfPrograminfo(programinfo);
				// ������Ŀ��Ϣ����IMAN_reference�µ�����subcostinfo
				Vector<LinkedHashMap<String, Object>> projectInfoVec = new Vector<LinkedHashMap<String, Object>>();
				Map<String, String> actual_division_user = new HashMap<String, String>();
				// modify 2014-7-1 �������division��user��actual_hr
				getGroupActualInfo(pro_costinfo_vec, projectInfoVec,
						actual_division_user);
				// �õ���Ŀ��Ϣ��������°汾
				TCComponentItemRevision programinfoLastRev = programinfo
						.getLatestItemRevision();
				// �õ���Ŀ��Ϣ��������а汾
				TCComponent[] programinfoRevs = programinfo
						.getReferenceListProperty("revision_list");
				LinkedHashMap<String, Object> rowDataMap = new LinkedHashMap<String, Object>();// ÿ�����
				String rowNo = "" + (i + 1);
				rowDataMap.put("No.", rowNo);

				// ��Ŀ��Ϣ����item_id
				String program_id = programinfo.getProperty("item_id");
				rowDataMap.put("ProgramID", program_id);
				// object_name
				String program_name = programinfo.getProperty("object_name");
				rowDataMap.put("ProgramName", program_name);
				// �õ����°汾��jci6_Category
				String programRev_category = programinfoLastRev
						.getProperty("jci6_Category");
				rowDataMap.put("Category", programRev_category);
				// Belonging Devision
				TCComponentGroup group = (TCComponentGroup) programinfoLastRev
						.getReferenceProperty("jci6_ProgramDivi");
				String gname = group.getProperty("name").split("\\.")[0];
				rowDataMap.put("Beloging Devision", gname);
				// labor ֱ�Ӷ���Ŀ��Ϣȡ����ֵ
				// Labor Total Cost_Actual ֱ�Ӷ�����jci6_actual_labor_cost
				double cost_actual_labor = programinfo
						.getDoubleProperty("jci6_actual_labor_cost");
				rowDataMap.put("Labor Total Cost_Actual",
						df.format(cost_actual_labor));
				// Labor Total Cost_Forecast
				double cost_forecast_labor = programinfo
						.getDoubleProperty("jci6_forecast_labor_cost");
				rowDataMap.put("Labor Total Cost_Forecast",
						df.format(cost_forecast_labor));
				// Labor Total Cost_Budget
				double cost_budget_labor = programinfo
						.getDoubleProperty("jci6_budget_labor_cost");
				rowDataMap.put("Labor Total Cost_Budget",
						df.format(cost_budget_labor));
				// Labor Total Cost_EAC
				double cost_eac_labor = programinfo
						.getDoubleProperty("jci6_eac_labor_cost");
				rowDataMap.put("Labor Total Cost_EAC",
						df.format(cost_eac_labor));

				if (cost_actual_labor > cost_budget_labor) {
					labor_actual_big_budget.add(true);
				} else {
					labor_actual_big_budget.add(false);
				}

				if (cost_eac_labor > cost_budget_labor) {
					labor_eac_big_budget.add(true);
				} else {
					labor_eac_big_budget.add(false);
				}

				// LCC Total Cost_Actual ���Ƕ�costinfo������
				Vector<TCComponent> actual_Ext_Yuan_Lcc = new Vector<TCComponent>();
				// LCC Total Cost_Forecast
				Vector<TCComponent> forecast_Ext_Yuan_Lcc = new Vector<TCComponent>();

				// ������Ŀ��Ϣ�µ�����costinfo
				for (int j = 0, size = pro_costinfo_vec.size(); j < size; j++) {
					TCComponent costinfo = pro_costinfo_vec.get(j);// ��Ŀ��Ϣ��ĳ��costinfo
					TCComponent rateLevel = costinfo
							.getReferenceProperty("jci6_RateLevel");
					String rate_name = "";
					if (rateLevel != null)
						rate_name = getProRealValue(rateLevel,
								"discipline_name");
					if (getProRealValue(costinfo, "jci6_CPT").equals("Actual")
							&& rate_name.equals("ExtSupporter")
							&& getProRealValue(costinfo, "jci6_Unit").equals(
									"Yuan")) {
						if (isInLaborType(getProRealValue(costinfo,
								"jci6_CostType"))) {
							actual_Ext_Yuan_Lcc.add(costinfo);
						}
					}

					if (getProRealValue(costinfo, "jci6_CPT")
							.equals("Forecast")
							&& rate_name.equals("ExtSupporter")
							&& getProRealValue(costinfo, "jci6_Unit").equals(
									"Yuan")) {
						if (isInLaborType(getProRealValue(costinfo,
								"jci6_CostType"))) {
							forecast_Ext_Yuan_Lcc.add(costinfo);
						}
					}
				}

				// LCC Total Cost_Actual
				double cost_actual_lcc = getLCCTotalCost(actual_Ext_Yuan_Lcc);
				rowDataMap.put("LCC Total Cost_Actual",
						df.format(cost_actual_lcc));

				// LCC Total Cost_Forecast
				double cost_forecast_lcc = getLCCTotalCost(forecast_Ext_Yuan_Lcc);
				rowDataMap.put("LCC Total Cost_Forecast",
						df.format(cost_forecast_lcc));

				// LCC Total Cost_Budget
				Vector<TCComponent> budget_Ext_Yuan_Lcc = new Vector<TCComponent>();

				// LCC Total Cost_Budget
				double cost_budget_lcc = getLCCTotalCost(budget_Ext_Yuan_Lcc);
				rowDataMap.put("LCC Total Cost_Budget",
						df.format(cost_budget_lcc));

				// LCC Total Cost_EAC �����Ŀ��active,forecast+actual , inactive
				// Actual
				// �õ���Ŀ��Ϣ��Ӧ����Ŀ
				double cost_eac_lcc = 0;
				if (projectActiveVec.get(i)) {
					cost_eac_lcc = cost_actual_lcc + cost_forecast_lcc;
				} else {
					cost_eac_lcc = cost_actual_lcc;
				}
				rowDataMap.put("LCC Total Cost_EAC", df.format(cost_eac_lcc));

				if (cost_actual_lcc > cost_budget_lcc) {
					lcc_actual_big_budget.add(true);
				} else {
					lcc_actual_big_budget.add(false);
				}

				if (cost_eac_lcc > cost_budget_lcc) {
					lcc_eac_big_budget.add(true);
				} else {
					lcc_eac_big_budget.add(false);
				}

				// LCC_Budget
				Vector<TCComponent> division_all_LCCBudget = new Vector<TCComponent>();
				// ������Ŀ��Ϣ�����а汾
				for (int j = 0; j < programinfoRevs.length; j++) {
					if (programinfoRevs[j] instanceof TCComponentItemRevision) {
						// �õ�jci6_BudgetState��ֵ
						if (getProRealValue(programinfoRevs[j],
								"jci6_BudgetState").equals("State4")) {
							// �õ�ProgramInfoRevision��IMAN_external_object_link����ϵ����������
							// CostInfo
							TCComponent[] comps = programinfoRevs[j]
									.getRelatedComponents("IMAN_external_object_link");
							for (int m = 0; m < comps.length; m++) {
								if (comps[m].getType().equals("JCI6_CostInfo")) {
									TCComponent rateLevel = comps[m]
											.getReferenceProperty("jci6_RateLevel");
									String rate_name = "";
									if (rateLevel != null)
										rate_name = getProRealValue(rateLevel,
												"discipline_name");
									if (getProRealValue(comps[m], "jci6_CPT")
											.equals("Budget")
											&& rate_name.equals("ExtSupporter")
											&& getProRealValue(comps[m],
													"jci6_Unit").equals("Yuan")) {
										division_all_LCCBudget.add(comps[m]);
										if (isInLaborType(getProRealValue(
												comps[m], "jci6_CostType"))) {
											budget_Ext_Yuan_Lcc.add(comps[m]);
										}
									}
								}
							}
						}
					}
				}
				// LCC_Budget
				getLCC_BudgetInfo(division_all_LCCBudget, projectInfoVec);
				// �õ���Ŀ��Ϣ�µ�����subcostinfo����
				Vector<TCComponent> pro_subcostinfo_vec = getSubCostInfoOfPrograminfo(programinfo);
				// forecast_hr forecast_cost
				getGroupForecastInfo(pro_subcostinfo_vec, projectInfoVec);

				rowDataMap.put("InfoVec", projectInfoVec);
				rowDataMap.put("Total Hours", actual_hours);
				rowDataMap.put("Total Cost", actual_costs);

				if (actual_hours != 0 || actual_hours != 0) {
					dataVec.add(rowDataMap);
				}

			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * reorganize all the groups and users which appear ��������е����������Ա
	 */
	public void reorganize(String g, String u) {
		if (groupMap.containsKey(g)) {
			Vector v = (Vector) groupMap.get(g);
			if (!v.contains(u)) {
				v.add(u);
				groupMap.put(g, v);
			}
		} else {
			Vector v = new Vector();
			v.add(u);
			groupMap.put(g, v);
		}
	}
	
	

	/**
	 * get the JCI6_ProgramInfo object �����Ŀ��ϵ�µĶ���
	 */
	public TCComponent getComp(TCComponent project) {
		TCComponent comp = null;
		if (project != null) {
			TCComponent[] comps;
			try {
				comps = project
						.getRelatedComponents("TC_Program_Preferred_Items");
				if (comps != null) {
					for (int i = 0; i < comps.length; i++) {
						if (comps[i].getType().equals("JCI6_ProgramInfo")) {
							comp = comps[i];
							break;
						}
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return comp;
	}

	/**
	 * get the month before current month ��õ�ǰ�·ݵ�ǰһ����
	 */
	public String getMonthPro(int index) {
		String s = "";
		s = month[index - 1];
		return s;
	}

	/**
	 * get the summary cost from all the month from all the CostInfo
	 * ������д������������·ݿ����ܺ�
	 */
	public double getLCCTotalCost(Vector<TCComponent> v) {
		double total = 0;
		for (int i = 0; i < v.size(); i++) {
			TCComponent comp = v.get(i);
			try {
				String[] array = comp.getProperties(month);
				for (int j = 0; j < array.length; j++) {
					double d = Double.parseDouble(array[j]);
					total = total + d;
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return total;
	}

	/**
	 * to tell the string is in LaborType �жϴ����ַ��Ƿ���LaborType��ѡ����
	 */
	public boolean isInLaborType(String s) {
		if (s != null) {
			for (int i = 0; i < laborTypeArray.length; i++) {
				if (s.equals(laborTypeArray[i])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * get the name in option compare to the real group name ��ݴ�����ʵ��������ѡ���ж�Ӧ�����
	 */
	public String getLCCGroup(String s) {
		if (s != null) {
			for (int i = 0; i < groupName.length; i++) {
				if (groupName[i].contains(s)) {
					String[] array = groupName[i].split("=");
					return array[1];
				}
			}
		}
		return s;
	}

	/**
	 * get the name in option compare to the real user name
	 * ��ݴ�����ʵ�û�������ѡ���ж�Ӧ�����(excel�е����)
	 */
	public String getLCCUser(String s) {
		if (s != null) {
			for (int i = 0; i < userName.length; i++) {
				if (userName[i].contains(s)) {
					String[] array = userName[i].split("=");
					return array[1];
				}
			}
		}
		return s;
	}

	/**
	 * export the report ��������
	 */
	public void export(File f, Vector<LinkedHashMap<String, Object>> data) {
		String outpath = System.getenv("Temp") + "\\LCC_" + getTimestamp()
				+ ".xlsx";
		File file = new File(outpath);
		if (file.exists()) {
			file.delete();
		}
		ExcelUtil util = new ExcelUtil(f.getAbsolutePath(), outpath);
		util.open();
		util.paintProgramInfoRegion(report_year, month_pro_name);

		//����
		sortGroupMapByOption();
		int total_hours_column = util.paintGroup(newGroupMap);
		for (int i = 0; i < dataVec.size(); i++) {
			LinkedHashMap<String, Object> map = dataVec
					.get(i);
			util.fillValue(startRow + i, 0, String.valueOf(i + 1));
			// ΪActual��EAC��ɫ
			for (int j = 0; j < key.length; j++) {
				if (key[j].equals("Total Hours")) {
					util.fillValue(startRow + i, total_hours_column,
							map.get(key[j]).toString());
				} else if (key[j].equals("Total Cost")) {
					util.fillValue(startRow + i, total_hours_column + 1, map
							.get(key[j]).toString());
				} else {
					if (key[j].equals("Labor Total Cost_Actual")) {
						if (labor_actual_big_budget.get(i)) {
							util.fillValSetBackgroupColor(IndexedColors.YELLOW
									.getIndex(), startRow + i, j + 1,
									map.get(key[j]).toString());
						} else {
							util.fillValue(startRow + i, j + 1, map.get(key[j])
									.toString());
						}
					} else if (key[j].equals("Labor Total Cost_EAC")) {
						if (labor_eac_big_budget.get(i)) {
							util.fillValSetBackgroupColor(
									IndexedColors.RED.getIndex(), startRow + i,
									j + 1, map.get(key[j]).toString());
						} else {
							util.fillValue(startRow + i, j + 1, map.get(key[j])
									.toString());
						}
					} else if (key[j].equals("LCC Total Cost_Actual")) {
						if (lcc_actual_big_budget.get(i)) {
							util.fillValSetBackgroupColor(IndexedColors.YELLOW
									.getIndex(), startRow + i, j + 1,
									map.get(key[j]).toString());
						} else {
							util.fillValue(startRow + i, j + 1, map.get(key[j])
									.toString());
						}
					} else if (key[j].equals("LCC Total Cost_EAC")) {
						if (lcc_eac_big_budget.get(i)) {
							util.fillValSetBackgroupColor(
									IndexedColors.RED.getIndex(), startRow + i,
									j + 1, map.get(key[j]).toString());
						} else {
							util.fillValue(startRow + i, j + 1, map.get(key[j])
									.toString());
						}

					} else {
						util.fillValue(startRow + i, j + 1, map.get(key[j])
								.toString());
					}

				}
			}

			// for component
			Vector<HashMap<String, Object>> infoVec = (Vector<HashMap<String, Object>>) map
					.get("InfoVec");
			for (int j = 0; j < infoVec.size(); j++) {
				HashMap<String, Object> hm = infoVec
						.get(j);
				String group = hm.get("group").toString();
				String user = hm.get("user").toString();
				String type = hm.get("type").toString();
				String value = hm.get("value").toString();
				int col = util.getTargetCellCol(group, user, type);
				if (col != 0) {
					if (!value.equals(".00")) {
						util.fillValue(startRow + i, col, value);
					}
				}
			}
			util.setNotesBackGround(startRow + i);
		}
		
		
		// for the last row about Grand Total
		util.paintBottomRow(startRow + dataVec.size());
		

		util.autoSizeColumn();

		util.close();

		util.runWsf();

	}

	/**
	 * ����������ļ� open the file by command
	 * 
	 * @param filePath
	 */
	public void open(String filePath) {// ��excel
		Runtime r = Runtime.getRuntime();
		try {
			r.exec("cmd /c \"" + filePath + "\"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// get property the real value ������Ե���ʵֵ
	public String getProRealValue(TCComponent comp, String proName)
			throws TCException {
		String value = "";
		TCProperty property = comp.getTCProperty(proName);
		if (property != null) {
			int proType = property.getPropertyType();
			if (proType == TCProperty.PROP_string) {
				value = property.getStringValue();
			} else if (proType == TCProperty.PROP_double) {
				value = Double.toString(property.getDoubleValue());
			} else if (proType == TCProperty.PROP_int) {
				value = Integer.toString(property.getIntValue());
			} else if (proType == TCProperty.PROP_float) {
				value = Float.toString(property.getFloatValue());
			}
		} 
		return value;
	}

	/**
	 * �õ���Ŀ��Ϣ�µ�����costinfo����
	 * 
	 * @param programinfo
	 *            ��Ŀ��Ϣ����
	 * @return �ö��������е�costinfo
	 * @throws TCException
	 */
	private Vector<TCComponent> getCostInfoOfPrograminfo(
			TCComponentItem programinfo) throws TCException {
		Vector<TCComponent> pro_costinfo_vec = new Vector<TCComponent>();
		TCComponent[] comps = programinfo
				.getRelatedComponents("IMAN_external_object_link");
		for (int j = 0; j < comps.length; j++) {
			if (comps[j].getType().equals("JCI6_CostInfo")) {
				pro_costinfo_vec.add(comps[j]);// programinfo�µ����е�costinfo����
			}
		}
		return pro_costinfo_vec;
	}

	/**
	 * �õ���Ŀ��Ϣ�µ�����subcostinfo����
	 * 
	 * @param programinfo
	 *            ��Ŀ��Ϣ����
	 * @return �ö��������е�subcostinfo
	 * @throws TCException
	 */
	private Vector<TCComponent> getSubCostInfoOfPrograminfo(
			TCComponentItem programinfo) throws TCException {
		Vector<TCComponent> pro_costinfo_vec = new Vector<TCComponent>();
		TCComponent[] comps = programinfo
				.getRelatedComponents("IMAN_reference");
		for (int j = 0; j < comps.length; j++) {
			if (comps[j].getType().equals("JCI6_SubCostInfo")) {
				pro_costinfo_vec.add(comps[j]);// programinfo�µ����е�costinfo����
			}
		}
		return pro_costinfo_vec;
	}

	/**
	 * �ж�����û��Ƿ���projectInfoVec��
	 */
	private int isGUInVec(Vector<LinkedHashMap<String, Object>> projectInfoVec,
			String group_name, String user_name, String type) {
		for (int i = 0, size = projectInfoVec.size(); i < size; i++) {
			LinkedHashMap<String, Object> g_u_val = projectInfoVec.get(i);
			if (g_u_val.get("group").equals(group_name)
					&& g_u_val.get("user").equals(user_name)
					&& g_u_val.get("type").equals(type)) {
				return i;
			}
		}
		return -1;
	}

	// forecast_cost forecast_hr
	private void getGroupForecastInfo(Vector<TCComponent> pro_subcostinfo_vec,
			Vector<LinkedHashMap<String, Object>> projectInfoVec)
			throws TCException {
		for (int j = 0, subSize = pro_subcostinfo_vec.size(); j < subSize; j++) {
			TCComponent subcostinfo = pro_subcostinfo_vec.get(j);
			// �õ���ǰcostinfo�����
			if (subcostinfo.getIntProperty("jci6_Year") == report_year) {
				TCComponent rateLevel = subcostinfo
						.getReferenceProperty("jci6_RateLevel");
				String rate_name = "";
				if (rateLevel != null)
					rate_name = getProRealValue(rateLevel, "discipline_name");
				LinkedHashMap<String, Object> hm = null;
				if (getProRealValue(subcostinfo, "jci6_CPT").equals("Forecast")
						&& rate_name.equals("ExtSupporter")) {
					String unit_val = getProRealValue(subcostinfo, "jci6_Unit");
					if (unit_val.equals("Yuan")) {
						// �õ���ǰsubcostinfo��division,���division���Ҫ��
						TCComponent gcomp = subcostinfo
								.getReferenceProperty("jci6_Division");

						if (gcomp != null) {
							String group_name = getProRealValue(gcomp, "name")
									.split("\\.")[0];
							if (divisionInOption(group_name)) {// ��Ҫ�޸�
								// �ж��û��Ƿ�Ϊָ���û�
								TCComponent ucomp = subcostinfo
										.getReferenceProperty("jci6_User");

								if (ucomp != null) {
									// �õ��û�id
									if (ucomp instanceof TCComponentUser) {
										TCComponentUser user = (TCComponentUser) ucomp;
										String user_id = user.getUserId();
										if (userInOption(user_id)) {// ��Ҫ�޸�
											String username = user
													.getProperty("user_name");
											String user_id_name = username
													+ "(" + user_id + ")";
											reorganize(group_name, "");
											 reorganize(group_name,user_id_name);
											// �ж�����û��Ƿ���projectInfoVec��
											int vec_index = isGUInVec(
													projectInfoVec, group_name,
													user_id_name,
													"ForecastCost");
											// �õ��ö���ָ������·ݵ�ֵ
											double forecast_cost = subcostinfo
													.getDoubleProperty(month_pro_name);
											if (vec_index == -1) {
												hm = new LinkedHashMap<String, Object>();
												hm.put("group", group_name);
												hm.put("user", user_id_name);
												hm.put("type", "ForecastCost");
												hm.put("value", df
														.format(forecast_cost));
												projectInfoVec.add(hm);
											} else {
												hm = projectInfoVec
														.get(vec_index);
												forecast_cost = forecast_cost
														+ Double.parseDouble(hm
																.get("value")
																.toString());
												hm.put("value", df
														.format(forecast_cost));
												projectInfoVec
														.remove(vec_index);
												projectInfoVec.add(vec_index,
														hm);
											}
										}
									}
								}
							}
						}

					} else if (unit_val.equals("Hour")) {
						// �õ���ǰsubcostinfo��division,���division���Ҫ��
						TCComponent gcomp = subcostinfo
								.getReferenceProperty("jci6_Division");
						if (gcomp != null) {
							String group_name = getProRealValue(gcomp, "name")
									.split("\\.")[0];
							if (divisionInOption(group_name)) {// ��Ҫ�޸�
								TCComponent ucomp = subcostinfo
										.getReferenceProperty("jci6_User");
								// �ж��û��Ƿ�Ϊָ���û�
								if (ucomp != null) {
									// �õ��û�id
									if (ucomp instanceof TCComponentUser) {
										TCComponentUser user = (TCComponentUser) ucomp;
										String user_id = user.getUserId();
										if (userInOption(user_id)) {// ��Ҫ�޸�
											String username = user
													.getProperty("user_name");
											String user_id_name = username
													+ "(" + user_id + ")";
											reorganize(group_name, "");
											reorganize(group_name,user_id_name);
											// �ж�����û��Ƿ���projectInfoVec��
											int vec_index = isGUInVec(
													projectInfoVec, group_name,
													user_id_name, "ForecastHR");
											// �õ��ö���ָ������·ݵ�ֵ
											double forecast_hr = subcostinfo
													.getDoubleProperty(month_pro_name);
											if (vec_index == -1) {
												hm = new LinkedHashMap<String, Object>();
												hm.put("group", group_name);
												hm.put("user", user_id_name);
												hm.put("type", "ForecastHR");
												hm.put("value",
														df.format(forecast_hr));
												projectInfoVec.add(hm);
											} else {
												hm = projectInfoVec
														.get(vec_index);
												forecast_hr = forecast_hr
														+ Double.parseDouble(hm
																.get("value")
																.toString());
												//System.out.println("new forecast_hr -->"+ forecast_hr);
														
												hm.put("value",
														df.format(forecast_hr));
												projectInfoVec
														.remove(vec_index);
												projectInfoVec.add(vec_index,
														hm);
											}
										}
									}
								}
							}
						}
					}
				}
			} 
		}

	}

	// LCC_budget
	private void getLCC_BudgetInfo(Vector<TCComponent> division_all_LCCBudget,
			Vector<LinkedHashMap<String, Object>> projectInfoVec)
			throws TCException {
		for (int j = 0, subSize = division_all_LCCBudget.size(); j < subSize; j++) {
			TCComponent costinfo = division_all_LCCBudget.get(j);
			LinkedHashMap<String, Object> hm = null;
			// �ж��Ƿ�Ϊ��ǰ�� 2014-6-25
			if (costinfo.getIntProperty("jci6_Year") == report_year) {
				// �õ���ǰcostinfo��division,���division���Ҫ��
				TCComponent gcomp = costinfo
						.getReferenceProperty("jci6_Division");
				if (gcomp != null) {
					String group_name = getProRealValue(gcomp, "name").split(
							"\\.")[0];
					if (divisionInOption(group_name)) {// ��Ҫ�޸�
						// �ж��û��Ƿ�Ϊָ���û�{
						//reorganize(group_name, "");
						// �ж�����û��Ƿ���projectInfoVec��
						int vec_index = isGUInVec(projectInfoVec, group_name,
								"", "Budget");
						// �õ������µ�ֵ
						double budget = costinfo
								.getDoubleProperty(month_pro_name);
						if (vec_index == -1) {
							hm = new LinkedHashMap<String, Object>();
							hm.put("group", group_name);
							hm.put("user", "");
							hm.put("type", "Budget");
							hm.put("value", df.format(budget));
							projectInfoVec.add(hm);
						} else {
							hm = projectInfoVec.get(vec_index);
							budget = budget + Double.parseDouble(hm.get("value")
											.toString());
							hm.put("value", df.format(budget));
							projectInfoVec.remove(vec_index);
							projectInfoVec.add(vec_index, hm);
						}
					}
				}
			}
		}
	}

	/**
	 * actual_cost actual_hr
	 * 
	 * @param pro_costinfo_vec
	 * @param projectInfoVec
	 * @return ĳ��Ŀ��Ϣ �������user��actual_hr��actual_costȫΪ��
	 * @throws TCException
	 */
	private void getGroupActualInfo(Vector<TCComponent> pro_costinfo_vec,
			Vector<LinkedHashMap<String, Object>> projectInfoVec,
			Map<String, String> actual_division_user) throws TCException {
		actual_hours = 0;
		actual_costs = 0;
		for (int j = 0, subSize = pro_costinfo_vec.size(); j < subSize; j++) {
			TCComponent costinfo = pro_costinfo_vec.get(j);
			// �õ���ǰcostinfo�����
			if (costinfo.getIntProperty("jci6_Year") == report_year) {
				TCComponent rateLevel = costinfo
						.getReferenceProperty("jci6_RateLevel");
				String rate_name = "";
				if (rateLevel != null)
					rate_name = getProRealValue(rateLevel, "discipline_name");
				LinkedHashMap<String, Object> hm = null;
				if (getProRealValue(costinfo, "jci6_CPT").equals("Actual")
						&& rate_name.equals("ExtSupporter")) {
					String unit_val = getProRealValue(costinfo, "jci6_Unit");
					if (unit_val.equals("Yuan")) {
						// �õ���ǰsubcostinfo��division,���division���Ҫ��
						TCComponent gcomp = costinfo
								.getReferenceProperty("jci6_Division");
						if (gcomp != null) {
							String group_name = getProRealValue(gcomp, "name")
									.split("\\.")[0];
							if (divisionInOption(group_name)) {// ��Ҫ�޸�
								// �ж��û��Ƿ�Ϊָ���û�
								TCComponent ucomp = costinfo
										.getReferenceProperty("jci6_User");
								if (ucomp != null) {
									// �õ��û�id
									if (ucomp instanceof TCComponentUser) {
										TCComponentUser user = (TCComponentUser) ucomp;
										String user_id = user.getUserId();
										if (userInOption(user_id)) {// ��Ҫ�޸�
											String username = user
													.getProperty("user_name");
											String user_id_name = username
													+ "(" + user_id + ")";
											actual_division_user.put(
													group_name, user_id_name);
											reorganize(group_name, "");
											reorganize(group_name, user_id_name);
											// �ж�����û��Ƿ���projectInfoVec��
											int vec_index = isGUInVec(
													projectInfoVec, group_name,
													user_id_name, "ActualCost");
											// �õ��ö���ָ������·ݵ�ֵ
											double actual_cost = costinfo
													.getDoubleProperty(month_pro_name);
											actual_costs = actual_costs
													+ actual_cost;
											if (vec_index == -1) {
												hm = new LinkedHashMap<String, Object>();
												hm.put("group", group_name);
												hm.put("user", user_id_name);
												hm.put("type", "ActualCost");
												hm.put("value",
														df.format(actual_cost));

												projectInfoVec.add(hm);
											} else {
												hm = projectInfoVec
														.get(vec_index);
												actual_cost = actual_cost
														+ Double.parseDouble(hm
																.get("value")
																.toString());
												hm.put("value",
														df.format(actual_cost));
												projectInfoVec
														.remove(vec_index);
												projectInfoVec.add(vec_index,
														hm);
											}
										}
									}
								}
							}
						}

					} else if (unit_val.equals("Hour")) {
						// �õ���ǰsubcostinfo��division,���division���Ҫ��
						TCComponent gcomp = costinfo
								.getReferenceProperty("jci6_Division");
						if (gcomp != null) {
							String group_name = getProRealValue(gcomp, "name")
									.split("\\.")[0];
							if (divisionInOption(group_name)) {// ��Ҫ�޸�
								// �ж��û��Ƿ�Ϊָ���û�
								TCComponent ucomp = costinfo
										.getReferenceProperty("jci6_User");
								if (ucomp != null) {
									// �õ��û�id
									if (ucomp instanceof TCComponentUser) {
										TCComponentUser user = (TCComponentUser) ucomp;
										String user_id = user.getUserId();
										if (userInOption(user_id)) {// ��Ҫ�޸�
											String username = user
													.getProperty("user_name");
											String user_id_name = username
													+ "(" + user_id + ")";
											actual_division_user.put(
													group_name, user_id_name);
											reorganize(group_name, "");
											reorganize(group_name, user_id_name);
											// �ж�����û��Ƿ���projectInfoVec��
											int vec_index = isGUInVec(
													projectInfoVec, group_name,
													user_id_name, "ActualHR");
											// �õ��ö���ָ������·ݵ�ֵ
											double actual_hr = costinfo
													.getDoubleProperty(month_pro_name);
											actual_hours = actual_hours
													+ actual_hr;
											if (vec_index == -1) {
												hm = new LinkedHashMap<String, Object>();
												hm.put("group", group_name);
												hm.put("user", user_id_name);
												// hm.put("isnull",false);
												hm.put("type", "ActualHR");
												hm.put("value",
														df.format(actual_hr));
												projectInfoVec.add(hm);
											} else {
												hm = projectInfoVec
														.get(vec_index);
												actual_hr = actual_hr
														+ Double.parseDouble(hm
																.get("value")
																.toString());
												hm.put("value",
														df.format(actual_hr));
												projectInfoVec
														.remove(vec_index);
												projectInfoVec.add(vec_index,
														hm);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}



	}

	// �õ���Ҫ������������
	private void getYearMonthFormPre() {
		String year_month = preService.getString(4, year_month_option_name);
		if (year_month != null) {
			String str[] = year_month.split("\\-");
			if (str.length == 2) {
				report_year = Integer.valueOf(str[0]);
				report_month = Integer.valueOf(str[1]);
			}
		}

		if (report_year <= 0) {
			// �õ���ǰʱ��
			Date date = new Date();
			report_year = date.getYear() + 1900;
			report_month = date.getMonth() + 1;
		}
		
	}

	// �ж�cosinfo division�Ƿ�����ѡ����
	private boolean divisionInOption(String division) {
		for (int i = 0; i < groupName.length; i++) {
			if (division.equals(groupName[i])) {
				return true;
			}
		}
		return false;
	}

	// �ж�costinfo user_id �Ƿ�����ѡ����
	private boolean userInOption(String user_id) {
		for (int i = 0; i < userName.length; i++) {
			if (userName[i].equals(user_id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * �õ�ʱ���
	 * 
	 * @return
	 */
	public String getTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}
	
	//�����ѡ��ֵ��˳��    ����groupMap
	private void sortGroupMapByOption(){
		newGroupMap = new LinkedHashMap();
		for(int i=0;i<groupName.length;i++){
			if(groupMap.containsKey(groupName[i])){
				newGroupMap.put(groupName[i], groupMap.get(groupName[i]));
			}
		}
	}

}
