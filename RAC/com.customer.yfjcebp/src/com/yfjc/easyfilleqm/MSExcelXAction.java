package com.yfjc.easyfilleqm;

import com.jacob.com.Dispatch;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.AEShell;
import com.teamcenter.rac.kernel.NamedReferenceContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinitionType;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.yfjc.lccreport.WSFUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class MSExcelXAction {
	private TCSession session;
	private String option1 = "YFJC_EQMExcel_fill_mapping";
	private String option2 = "YFJC_EQMForm_fill_mapping";
	private String wsfPath = System.getenv("temp") + "\\fillExcel.wsf";
	private String eqmInfoPath = System.getenv("temp") + "\\fillEQMInfo.txt";

	private String formInfoPath = System.getenv("temp")
			+ "\\fillEQMFormInfo.txt";
	private String datasetName = "EQM";
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat strDate;

	public void postProcess_EQM(TCComponentDataset ds, String arg1, int arg2) {
		//System.out.println("MSExcelXAction  fy postProcess");

		try {
			ds.refresh();
			session = ds.getSession();
			TCComponentForm form = null;
			Vector<LinkedHashMap<String,Object>> data = new Vector<LinkedHashMap<String,Object>>();
			String object_name = ds.getProperty("object_name");
			//System.out.println("dataset name -->" + object_name);

			if (object_name.startsWith(this.datasetName)) {
				TCComponentProject project = getProject(ds);
				TCComponent comp = getComp(project);
				if (comp != null) {
					TCComponent[] comps = comp
							.getRelatedComponents("IMAN_external_object_link");
					if ((comps != null) && (comps.length > 0)) {
						for (int i = 0; i < comps.length; i++) {
							if (comps[i].getType().equals("JCI6_EQMForm")) {
								form = (TCComponentForm) comps[i];
								break;
							}
						}
					}
				}
				if (form != null) {
					String[] strs = this.session.getPreferenceService()
							.getStringArray(4, this.option2);
					if ((strs != null) && (strs.length > 0))
						for (int i = 0; i < strs.length; i++) {
							LinkedHashMap<String,Object> lhm = new LinkedHashMap<String,Object>();
							String[] array1 = strs[i].split("=");
							String[] array2 = array1[1].split("\\{.}");
							String[] array3 = array2[1].split("-");
							if (array3.length == 2) {
								lhm.put("position", array3[0] + array3[1]);
							}
							lhm.put("sheetName", array2[0]);
							String[] array4 = array1[0].split("\\.");
							String proName = array4[1];
							lhm.put("proName", proName);
							data.add(lhm);
						}
					else{
						//System.out.println("'" + this.option2 + "'"+ "option not set,skip...");
								
					}
				}
			} else {
				//System.out.println("not EQM Report");
			}

			if (data.size() > 0) {
				String excelPath = "";

				String dsPath = ds.getCurrentWorkingDir();
				File dir = new File(dsPath);
				File[] tcfiles = dir.listFiles();
				if ((tcfiles != null) && (tcfiles.length > 0)) {
					File file1 = dir.listFiles()[0];
					excelPath = file1.getAbsolutePath();
				}

				File tempFile = new File(this.formInfoPath);
				if (tempFile.exists()) {
					tempFile.delete();
					tempFile = new File(this.formInfoPath);
				}
				try {
					PrintWriter txt = new PrintWriter(new FileWriter(
							this.formInfoPath, true), true);
					//System.out.println("excel path--->" + excelPath);
					if (!excelPath.trim().equals("")) {
						JacobExcelUtil jacobExcel = new JacobExcelUtil();
						String TC_path = System.getenv("TPR");
						jacobExcel.addDir(TC_path + "\\plugins");
						jacobExcel.openExcel(excelPath, false, false);
						for (int i = 0; i < data.size(); i++) {
							LinkedHashMap<String,Object> hm = data.get(i);
							String sheetName = (String) hm.get("sheetName");
							Dispatch sheet = jacobExcel
									.getSheetByName(sheetName);
							//System.out.println("单元格--->"+ (String) hm.get("position"));
									
							String value = jacobExcel.getValue(sheet,
									(String) hm.get("position"));
							//System.out.println("proName-->"+ (String) hm.get("proName")+ "-->value--->" + value);
									
									
							txt.print((String) hm.get("proName") + "=" + value);
							txt.print("\r\n");
						}
						jacobExcel.closeExcel(true);

						txt.close();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(new FileInputStream(
										this.formInfoPath)));
						String line = br.readLine();
						Vector v1 = new Vector();
						Vector v2 = new Vector();
						while (line != null) {
							String[] s = line.split("=");
							if (s.length == 2) {
								v1.add(s[0]);
								v2.add(s[1]);
							}
							line = br.readLine();
						}
						Object[] array1 = v1.toArray();
						Object[] array2 = v2.toArray();
						String[] pro = new String[array1.length];
						String[] value = new String[array1.length];
						for (int j = 0; j < array1.length; j++) {
							pro[j] = array1[j].toString();
							value[j] = array2[j].toString();
							//System.out.println("设值--->" + pro[j] + "--->"+ value[j]);
									
							setProVal(form, pro[j], value[j]);
						}

						form.save();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (TCException e1) {
			e1.printStackTrace();
		}
	}

	public String cutZero(String v) {
		if (v.indexOf(".") > -1) {
			while (v.lastIndexOf("0") == v.length() - 1) {
				v = v.substring(0, v.lastIndexOf("0"));
			}

			if (v.lastIndexOf(".") == v.length() - 1) {
				v = v.substring(0, v.lastIndexOf("."));
			}
		}
		return v;
	}

	public void preProcess_EQM(TCComponentDataset ds, AEShell arg1, int arg2) {
		//System.out.println("MSExcelXAction  fy preProcess");
		session = ds.getSession();
		String type = ds.getType();
		Vector<LinkedHashMap<String,Object>> data = new Vector<LinkedHashMap<String,Object>>();
		try {
			//System.out.println("dataset type -->" + type);
			if (type.equals("MSExcelX")) {
				String object_name = ds.getProperty("object_name");
				//System.out.println("dataset name -->" + object_name);
				if (object_name.startsWith(datasetName)) {
					String[] strs = session.getPreferenceService()
							.getStringArray(4, option1);
					if ((strs != null) && (strs.length > 0)) {
						TCComponentProject project = getProject(ds);
						TCComponent comp = getComp(project);
						if (comp != null) {
							TCComponentItem progarmItem = (TCComponentItem) comp;

							TCComponentItemRevision programRev = progarmItem
									.getLatestItemRevision();
							for (int i = 0; i < strs.length; i++) {
								LinkedHashMap<String,Object> lhm = new LinkedHashMap<String,Object>();
								String[] array1 = strs[i].split("=");
								String[] array2 = array1[1].split("\\{.}");
								String[] array3 = array2[1].split("-");
								lhm.put("sheetName", array2[0]);
								String[] array4 = array1[0].split("\\.");
								String proName = array4[1];
								String value = null;
								if (array4[0].equals("ProgramInfo")) {
									value = getProVal(progarmItem, proName);
								} else if (array4[0]
										.equals("ProgramInfoRevision")) {
									value = getProVal(programRev, proName);
								}
								String realValue = getRealValue(proName, value);
								lhm.put("value", realValue);
								lhm.put("row", array3[1]);
								lhm.put("col", String
										.valueOf(matchColIndex(array3[0]) + 1));
								data.add(lhm);
								//System.out.println("proName--->"+ proName+ "--->cell("+ String.valueOf(matchColIndex(array3[0]) + 1)+ array3[1] + ")-->value-->"+ realValue);
										
							}
						}
					} else {
						//System.out.println("'" + option1 + "'"+ "option not set,skip...");
								
					}
				} else {
					//System.out.println("not EQM Report");
				}

			}

			if (data.size() > 0) {
				File tempFile = new File(eqmInfoPath);
				if (tempFile.isFile()) {
					tempFile.delete();
					tempFile = new File(eqmInfoPath);
				}
				writeTextTemp(eqmInfoPath, data);
				String dsPath = ds.getCurrentWorkingDir();
				File dir = new File(dsPath);
				File file = dir.listFiles()[0];
				if (file != null) {
					String excelPath = file.getAbsolutePath();
					WSFUtil.getFileFromClass(getClass(), "fillExcel.wsf",
							System.getenv("temp"));
					callScript(wsfPath, eqmInfoPath, excelPath);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getRealValue(String proName, String value) {
		String realValue = value;
		String optionName = proName + "_LOV_mapping";
		String[] lov = session.getPreferenceService().getStringArray(4,
				optionName);
		if ((lov != null) && (lov.length > 0)) {
			for (int i = 0; i < lov.length; i++) {
				String[] array = lov[i].split("#");
				if (value.equals(array[0])) {
					realValue = array[1];
					break;
				}
			}
		}
		return realValue;
	}

	/**
	 * to write these datas from object to Text File 将信息写入临时文件
	 */
	private void writeTextTemp(String path, Vector<LinkedHashMap<String,Object>> data) {
		PrintWriter txt;
		try {
			txt = new PrintWriter(new FileWriter(path, true), true);
			for (int i = 0; i < data.size(); i++) {
				LinkedHashMap<String,Object> map = data.get(i);
				Iterator<Map.Entry<String,Object>> iterator = map.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String,Object> entry = iterator.next();
					txt.print(entry.getKey() + "="
							+ (String) entry.getValue() + "|");
				}
				txt.print("\r\n");
			}
			txt.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int matchColIndex(String str) {
		char[] chars = str.toCharArray();
		if (chars.length < 2) {
			return chars[0] - 'A';
		}
		return 26 * (chars[0] - 'A' + 1) + chars[1] - 65;
	}

	private TCComponentProject getProject(TCComponentDataset ds) {
		TCComponentProject project = null;
		try {
			String[] projectIds = ds.getProperty("project_ids").split(",");
			if (projectIds.length > 0)
				project = searchProject(projectIds[0].trim());
		} catch (TCException e) {
			e.printStackTrace();
		}
		return project;
	}

	public TCComponent getComp(TCComponentProject project) {
		TCComponent comp = null;
		if (project != null) {
			try {
				TCComponent[] comps = project
						.getRelatedComponents("TC_Program_Preferred_Items");
				if (comps != null)
					for (int i = 0; i < comps.length; i++)
						if (comps[i].getType().equals("JCI6_ProgramInfo")) {
							comp = comps[i];
							break;
						}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}

		return comp;
	}

	public String getProValue(TCComponent comp, String proName, String type) {
		String s = "";
		try {
			if (comp != null) {
				s = comp.getProperty(proName);
				if (type.equals("ProgramInfoRevision")) {
					TCComponentItem item = (TCComponentItem) comp;
					TCComponentItemRevision rev = item.getLatestItemRevision();
					s = rev.getProperty(proName);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return s;
	}

	private TCComponentProject searchProject(String projectId) {
		TCComponentProject project = null;
		this.session = ((TCSession) AIFUtility.getCurrentApplication()
				.getSession());
		try {
			TCTextService tcTextService = session.getTextService();
			String[] askKey = { tcTextService.getTextValue("ProjectID") };
			String[] askValue = { projectId };
			InterfaceAIFComponent[] objects = session.search(
					"Projects...", askKey, askValue);
			if ((objects != null) && (objects.length > 0))
				project = (TCComponentProject) objects[0];
		} catch (TCException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return project;
	}

	/**
	 * call VBScript to write excel sheet 调用vbs将临时文件信息写入excel
	 */
	public int callScript(String wsfPath, String txtPath, String excelPath) {
		File wsfFile = new File(wsfPath);// param2=文件夹名称
		Process script;
		int num = 1;
		try {
			script = Runtime.getRuntime().exec(
					new String[] { "wscript.exe", wsfFile.getAbsolutePath(),
							txtPath, excelPath });
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						script.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				num = Integer.parseInt(sb.toString());
				script.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return num;
	}

	private String guessDatasetReferenceNames(String ds, String file)
			throws TCException {
		TCComponentDatasetDefinitionType dsdefType = (TCComponentDatasetDefinitionType) this.session
				.getTypeService().getTypeComponent("DatasetType");
		TCComponentDatasetDefinition definition = dsdefType.find(ds);
		NamedReferenceContext[] contexts = definition
				.getNamedReferenceContexts();
		Hashtable hashtable = new Hashtable(contexts.length);
		String all = "";
		for (int i = 0; i < contexts.length; i++) {
			String template = contexts[i].getFileTemplate();
			int index = template.lastIndexOf(".");
			if (index != -1) {
				hashtable.put(template.substring(index + 1).toLowerCase(),
						contexts[i].getNamedReference());
			} else {
				all = template.toLowerCase();
				hashtable.put(all, contexts[i].getNamedReference());
			}
		}
		String refer = null;
		String name = new File(file).getName();
		refer = (String) hashtable.get(name
				.substring(name.lastIndexOf(".") + 1).toLowerCase());
		if (refer == null) {
			refer = (String) hashtable.get(all);
		}
		return refer;
	}

	private String getProVal(TCComponent comp, String proName)
			throws TCException {
		String value = "";
		TCProperty property = comp.getTCProperty(proName);
		if (property != null) {
			int proType = property.getPropertyType();
			if (proType == TCProperty.PROP_string) {
				value = comp.getProperty(proName);
			} else if (proType == TCProperty.PROP_double) {
				value = Double.toString(property.getDoubleValue());
			} else if (proType == TCProperty.PROP_int) {
				value = Integer.toString(property.getIntValue());
			} else if (proType == TCProperty.PROP_float) {
				value = Float.toString(property.getFloatValue());
			} else if (proType == TCProperty.PROP_date) {
				Date date = property.getDateValue();
				if (date != null) {
					if (dateFormat == null) {
						dateFormat = new SimpleDateFormat("dd-MMM-yy",
								Locale.ENGLISH);
					}
					value = dateFormat.format(date);
				}
			}else{
				value = comp.getProperty(proName);
			}
		} else {
			//System.out.println(proName + "not exist,skip...");
		}
		return value;
	}

	private void setProVal(TCComponent comp, String proName, String proValue)
			throws TCException {
		if ((proValue != null) && (!proValue.trim().equals(""))) {
			TCProperty property = comp.getTCProperty(proName);
			if (property.isModifiable()) {
				int proType = property.getPropertyType();
				if (proType == TCProperty.PROP_string) {
					if (strDate == null)
						strDate = new SimpleDateFormat(
								"E MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
					try {
						Date date = strDate.parse(proValue);
						if (dateFormat == null) {
							dateFormat = new SimpleDateFormat("dd-MMM-yy",
									Locale.ENGLISH);
						}
						proValue = dateFormat.format(date);
					} catch (ParseException localParseException1) {
					}finally{
						//System.out.println("proValue--->" + proValue);
						try{
							comp.setProperty(proName, proValue);
						}catch (TCException e){
							//System.out.println(proName + "-->set value ("+ proValue + ") defeat");
									
						}
					}
					
				} else if (proType == TCProperty.PROP_double) {
					try {
						Double doub = Double.valueOf(proValue);
						property.setDoubleValue(doub.doubleValue());
					} catch (NumberFormatException e) {
						//System.out.println("proValue-->" + proValue+ "--->is not double");
								
					} catch (TCException e) {
						//System.out.println(proName + "-->set value ("+ proValue + ") defeat");
								
					}
				} else if (proType == TCProperty.PROP_int) {
					try {
						Integer integer = Integer.valueOf(proValue);
						property.setIntValue(integer.intValue());
					} catch (NumberFormatException e) {
						//System.out.println("proValue-->" + proValue+ "--->is not int");
								
					} catch (TCException e) {
						//System.out.println(proName + "-->set value ("+ proValue + ") defeat");
								
					}
				} else if (proType == TCProperty.PROP_float) {
					try {
						Float f = Float.valueOf(proValue);
						property.setFloatValue(f.floatValue());
					} catch (NumberFormatException e) {
						//System.out.println("proValue-->" + proValue+ "--->is not Float");
								
					} catch (TCException e) {
						//System.out.println(proName + "-->set value ("+ proValue + ") defeat");
								
					}
				} else if (proType == TCProperty.PROP_date) {
					if (strDate == null)
						strDate = new SimpleDateFormat(
								"E MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
					try {
						property.setDateValue(this.strDate.parse(proValue));
					} catch (TCException e) {
						//System.out.println(proName + "-->set value ("+ proValue + ") defeat");
								
					} catch (ParseException e) {
						//System.out.println("proValue-->" + proValue+ "--->is not date");
								
					}
				}
			} else {
				
				//System.out.println(proName + " is not modifiable");
			}
		} else {
			
			//System.out.println(proName + " need set value is null,skip...");
		}
	}

	private void getLastModifyDate(File file) {
		long modifyTime = file.lastModified();
		Date date = new Date(modifyTime);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:MM");
		//System.out.println("最后修改时间为" + sd.format(date));
	}
}