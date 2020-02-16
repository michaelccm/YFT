package com.yfjcebp.projectmanager.budget.dialogs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.jacob.com.Dispatch;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;

public class COCOperation extends AbstractAIFOperation {

	private TCSession session;
	private TCComponentItemRevision itemRev;
	private String gebt_version;
	private String coc_all;
	private String coc_feature;
	private double coc_feature_val;
	private Dispatch sheetsAll = null;
	private JacobEReportTool tool;
	private double val;
	private Map<String, Double> propNameValueMap;
	private String coc_select_str;
	private String coc_feature_select_str;
	private Vector<String> proExitName;
	private TCUserService userService;
	private Object[] obj;

	// private Vect
	public COCOperation(TCSession session, TCComponentItemRevision itemRev,
			String gebt_version) {
		this.session = session;
		this.itemRev = itemRev;
		this.gebt_version = gebt_version;
	}

	@Override
	public void executeOperation() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("**************************开始进入填写COC**************************");
		
		try {
			getPreCoc();
			if (coc_all != null) {
				// 分割字符串
				tool = new JacobEReportTool();
				// 得到系统的零时文件
				String getenv = System.getenv("TEMP");
				String TC_path = System.getenv("TPR");
				tool.addDir(TC_path + "\\plugins");
				// 得到所有的sheet
				sheetsAll = tool.getSheets(getenv + File.separator
						+ "GEBT.xlsm");
				getCocFeature();
				splitString(coc_all);
				setRevProVal();
			} else {
				System.out.println(coc_select_str
						+ " not set in YFJC_GEBT_Attribute_Location");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("**************************填写COC完成**************************");
			if(tool != null)
				tool.closeExcelFile(false);
		}
	}

	/**
	 * 读首选项
	 */
	private void getPreCoc() {
		TCPreferenceService preService = session.getPreferenceService();
		String[] preVals = preService.getStringArray(
				TCPreferenceService.TC_preference_site, "YFJC_GEBT_Attribute_Location");
		coc_select_str = "jci6_COC_" + gebt_version;
		coc_feature_select_str = "jci6_coc_feature_" + gebt_version;
		
		if (preVals != null && preVals.length > 0) {
			for (int i = 0; i < preVals.length; i++) {
				System.out.println("getPreCoc:: coc_select_str-->"+coc_select_str);
				if (preVals[i].startsWith(coc_select_str)) {
					coc_all = preVals[i];
				}
				System.out.println("getPreCoc:: coc_all-->"+coc_all);
				
				System.out.println("========================================");
				
				System.out.println("getPreCoc:: coc_feature_select_str-->"+coc_feature_select_str);
				if (preVals[i].startsWith(coc_feature_select_str)) {
					coc_feature = preVals[i];
				}
				System.out.println("getPreCoc:: coc_feature-->"+coc_feature);
			}
		}
	}

	/**
	 * 分割字符串
	 * 
	 * @param coc
	 */
	private void splitString(String coc) {
		String[] s = coc.split(":",-1);
		if (s.length == 2) {
			String[] p_sheet_cols = s[1].split("\\|",-1);
			for (int i = 0; i < p_sheet_cols.length; i++) {
				String[] p_sheet = p_sheet_cols[i].split("=",-1);
				if (p_sheet.length == 2) {
					if (itemRev.isValidPropertyName(p_sheet[0])) {
						// 得到属性对应的sheet页
						String coc_sheet = p_sheet[1].substring(0,
								p_sheet[1].indexOf("{"));
						String cells = p_sheet[1].substring(
								p_sheet[1].lastIndexOf("}") + 2,
								p_sheet[1].length() - 1);
						// System.out.println("--->" + coc_sheet + "----->");
						Dispatch sheet = tool.openExcelFile(sheetsAll,
								coc_sheet);
						val = 0;
						getMData(tool, sheet, cells, true);
						System.out.println(p_sheet[0] + "==========>" + val);
						if (propNameValueMap == null) {
							propNameValueMap = new HashMap<String, Double>();
						}
						if (proExitName == null) {
							proExitName = new Vector<String>();
						}
						proExitName.add(p_sheet[0]);
						propNameValueMap.put(p_sheet[0], val);
					} else {
						System.out.println("项目信息版本没有'" + p_sheet[0] + "'属性");
					}
				}
			}
		}
	}

	/**
	 * 得到jci6_coc_feature的值
	 */
	private void getCocFeature() {
		System.out.println("coc_feature--->"+coc_feature);
		if (coc_feature != null) {
			String[] p_sheet_col = coc_feature.split("=",-1);
			if (p_sheet_col.length == 2) {
				if (itemRev.isValidPropertyName("jci6_coc_feature")) {
					String coc_feature_sheet = p_sheet_col[1].substring(0,
							p_sheet_col[1].indexOf("{"));
					String cells = p_sheet_col[1].substring(
							p_sheet_col[1].lastIndexOf("}") + 2,
							p_sheet_col[1].length() - 1);
					Dispatch sheet = tool.openExcelFile(sheetsAll,
							coc_feature_sheet);
					System.out.println("jci6_coc_feature" + cells);
					val = 0;
					getMData(tool, sheet, cells, true);
					coc_feature_val = val;
					if (propNameValueMap == null) {
						propNameValueMap = new HashMap<String, Double>();
					}
					if (proExitName == null) {
						proExitName = new Vector<String>();
					}
					proExitName.add("jci6_coc_feature");
					propNameValueMap.put("jci6_coc_feature", coc_feature_val);
					System.out.println("coc_feature--->" + coc_feature_val);
				} else {
					System.out.println("项目信息版本没有'jci6_coc_feature'属性");
				}

			} else {
				System.out.println(coc_feature_select_str
						+ " set error in YFJC_GEBT_Attribute_Location");
			}
		} else {
			System.out.println(coc_feature_select_str
					+ " not set in YFJC_GEBT_Attribute_Location");
		}
	}

	// jci6_coc_feature_V3.4.9=Content{.}[(C-330)~(C-347),(C-518)~(C-535),(C-127)~(C-158)]

	/**
	 * 得到指定多个单元格的值
	 * 
	 * @param tool
	 * @param sheet
	 * @param cells
	 * @param isadd
	 */
	private void getMData(JacobEReportTool tool, Dispatch sheet, String cells,
			boolean isadd) {
		// System.out.println("current data before---->"+val);
		if (cells.contains(",")) {
			String[] qjstr = cells.split(",",-1);
			for (int i = 0; i < qjstr.length; i++) {
				getMData(tool, sheet, qjstr[i], true);
			}
		} else if (cells.contains("+")) {
			String[] qjstr = cells.split("\\+",-1);
			for (int i = 0; i < qjstr.length; i++) {
				getMData(tool, sheet, qjstr[i], true);
			}
		} else if (cells.contains(")-")) {
			int index = cells.indexOf(")-");
			System.out.println(cells.indexOf(")-"));
			String[] cell = cells.substring(1, index).split("-",-1);
			System.out.println(cell[0] + Integer.valueOf(cell[1]));
			String cellVal = getData(tool, sheet, cell[0],Integer.valueOf(cell[1]));
			
			if (isadd) {
				if(!cellVal.trim().equals("")){
					val = val + Double.valueOf(cellVal);
				}
			} else {
				if(!cellVal.trim().equals("")){
					val = val
						- Double.valueOf(cellVal);
				}
			}
			getMData(tool, sheet, cells.substring(index + 2), false);
		} else if (cells.contains("~")) {
			String[] qjstr = cells.split("~",-1);
			String end_str = qjstr[1];
			String tmp_str = qjstr[0];
			while (!tmp_str.equals(end_str)) {
				getMData(tool, sheet, tmp_str, true);
				String[] s = tmp_str.split("-",-1);
				tmp_str = s[0]
						+ "-"
						+ (Integer
								.valueOf(s[1].substring(0, s[1].length() - 1)) + 1)
						+ ")";
			}
		} else if (cells.equals("(jci6_coc_feature)")) {
			if (isadd) {
				val = val + coc_feature_val;
			} else {
				val = val - coc_feature_val;
			}
		} else {
			System.out.println("cells---->" + cells);
			String s = cells.substring(1, cells.lastIndexOf(")"));
			String[] row_col = s.split("\\-",-1);
			String cellVal = getData(tool, sheet, row_col[0],
					Integer.valueOf(row_col[1]));
			System.out.println("值----》"
					+ cellVal);
			if (isadd) {
				if(!cellVal.trim().equals("")){
					val = val
						+ Double.valueOf(cellVal);
				}
			} else {
				val = val- Double.valueOf(cellVal);
			}

		}

		// System.out.println("current data after---->"+val);
	}

	/**
	 * 根据首选项的字符串截取,在解析excel中的数据，读取excel中指定的数据
	 * 
	 * @param tool
	 * @param sheet
	 * @param col
	 * @param row
	 * @return
	 */
	private String getData(JacobEReportTool tool, Dispatch sheet, String col,
			int row) {
		String value = "";
		try {
			value = tool.getDataFromExcel(col, row, sheet);
			// tool.closeExcelFile(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 设置版本属性
	 * 
	 * @throws TCException
	 */
	private void setRevProVal() throws TCException {
		if (propNameValueMap != null) {
			int len = proExitName.size();
			String[] proNames = new String[len];
			for (int i = 0; i < len; i++) {
				proNames[i] = proExitName.get(i);
			}
			TCProperty[] propertys = itemRev.getTCProperties(proNames);
			double d = 0;
			for (int i = 0; i < propertys.length; i++) {
				d = (double)Math.round(propNameValueMap.get(proNames[i])*1000)/1000;
				System.out.println("设置属性："+proNames[i]+"----->"+d);
				propertys[i].setDoubleValueData(d);	
			}

			byPass(true);
			
			itemRev.lock();
			itemRev.setTCProperties(propertys);
			itemRev.save();
			itemRev.unlock();
			
			itemRev.refresh();
			byPass(false);
		}
	}

	/**
	 *  开关旁路
	 * @param open
	 * @throws TCException
	 */
	private void byPass(boolean open) throws TCException {
		if (userService == null)
			userService = session.getUserService();
		if(obj == null)
			obj = new Object[1];
		if (open) {
			obj[0] = 1;
			userService.call("open_or_close_pass", obj);
		} else {
			obj[0] = 0;
			userService.call("open_or_close_pass", obj);
		}
	}

}
