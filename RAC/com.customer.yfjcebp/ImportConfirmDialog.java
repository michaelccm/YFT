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
 #	2013-3-11	liuc  		Ini		锟斤拷锟角碉拷锟斤拷锟絊WT锟皆伙拷锟斤拷						   
 #=============================================================================
 */
package com.yfjcebp.projectmanager.budget.dialogs;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.jacob.com.Dispatch;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCAttachmentScope;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupType;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentReleaseStatusType;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2008_06.DataManagement;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;

public class ImportConfirmDialog extends AbstractAIFDialog {

	private static final long serialVersionUID = 1L;
	private Button btn_ok;
	private Button btn_cancel;
	// private JacobEReportTool tool;
	private Registry reg = Registry.getRegistry(this);
	private TCSession session;
	private Shell shell;
	private int isF;
	private Object obj[] = new Object[1];
	private TCComponentDataset dataset;
	private TCComponentItemRevision revision;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	// 锟斤拷选锟斤拷锟斤拷
	// 4.18---锟睫改凤拷锟剿癸拷锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟窖★拷睿拷锟轿拷锟斤拷锟斤拷值为CostType锟斤拷值锟斤拷锟斤拷同锟酵革拷锟铰讹拷锟斤拷锟铰斤拷
	private static String YFJC_NonLabor_CostType = "YFJC_NonLabor_CostType";
	private static String YFJC_NonLabor_Position = "YFJC_NonLabor_Position";
	private static String YFJC_HumanLabor_Position = "YFJC_HumanLabor_Position";
	private static String YFJC_HumanLabor_Group = "YFJC_HumanLabor_Group";
	// 5.4--锟睫革拷锟剿癸拷锟斤拷锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟窖★拷锟斤拷锟斤拷锟�--YFJC_HumanLabor_RateLevel
	private static String YFJC_HumanLabor_Section = "YFJC_HumanLabor_RateLevel";
	// 5.6--锟睫革拷锟斤拷锟斤拷锟斤拷一锟斤拷锟斤拷选锟斤拷---锟揭碉拷锟剿癸拷锟斤拷锟矫碉拷SMTE锟斤拷锟斤拷
	private static String YFJC_HumanLabor_SMTE_GROUP = "YFJC_HumanLabor_SMTE_GROUP";
	private String YFJC_LaborCostYuan_Position = "YFJC_LabourCost_Position";
	// excel锟侥硷拷锟斤拷
	private static String ExcelNameMode = "GEBT.xlsm";

	// 锟斤拷锟斤拷锟斤拷斯锟斤拷锟絚ostInfo锟斤拷锟斤拷锟斤拷息
	private HashMap<String, TCComponent> maps_CostInfo_Non;
	// 5.6锟睫革拷----锟斤拷锟斤拷锟剿癸拷锟斤拷costInfo锟斤拷锟斤拷锟斤拷息
	HashMap<String, HashMap<String, TCComponent>> maps_HunmanLabor;
	HashMap<String, HashMap<String, TCComponent>> maps_HunmanLabor_yuan;
	// 锟斤拷锟斤拷锟斤拷选锟斤拷锟斤拷锟斤拷锟斤拷
	private HashMap<String, String> maps_Prefer_values = new HashMap<String, String>();

	// 锟斤拷锟斤拷锟剿癸拷锟斤拷锟矫碉拷锟斤拷锟斤拷锟较�
	private HashMap<String, String> map_group = new HashMap<String, String>();

	// 4.1锟斤拷锟斤拷
	// 锟斤拷锟斤拷error锟侥对伙拷锟斤拷锟斤拷要锟斤拷值
	private ArrayList<HashMap<String, String>> error_list = null;

	// 4.10锟斤拷锟斤拷
	private String ProjectID;

	// JacobEReportTool tool = new JacobEReportTool();

	// 6.5锟斤拷锟斤拷----锟芥储Group,学锟狡ｏ拷锟皆硷拷CostType
	private HashMap<String, String> nameOfPreference = new HashMap<String, String>();

	// 5.31锟睫革拷----锟斤拷锟斤拷为锟秸就诧拷锟斤拷锟斤拷锟剿硷拷锟斤拷执锟斤拷
	int endposition_labor = 0;
	int endposition_nonlabor = 0;
	private TCComponent projectComponent;
	// 7.2锟睫革拷--锟斤拷锟斤拷 锟剿癸拷锟斤拷锟斤拷锟斤拷息锟侥讹拷取---元锟斤拷锟劫硷拷锟斤拷锟斤拷嵌锟饺≈革拷锟揭筹拷锟斤拷锟斤拷锟斤拷
	int TYPE_HumanLaborYuan = 0;

	public ImportConfirmDialog(final Display display,
			final TCComponentItemRevision revision,
			final HashMap<String, String> maps, final TCSession session,
			final int isF, final TCComponentDataset dataset,
			TCComponent projectComponent) {

		obj[0] = "open bypass";// 锟斤拷路锟斤拷锟斤拷要锟侥憋拷锟斤拷锟斤拷锟斤拷锟街�

		shell = new Shell(display);
		shell.setSize(500, 465);
		shell.setText(reg.getString("dialog_Confirm"));
		this.session = session;
		this.isF = isF;
		this.dataset = dataset;
		this.revision = revision;
		this.projectComponent = projectComponent;
		// 注锟斤拷锟斤拷锟斤拷,SWT.NULTI锟斤拷锟斤拷锟斤拷选锟斤拷锟斤拷锟�SWT.FULL_SELECTION锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷选锟斤拷
		// ,SWT.BORDER锟竭匡拷SWT.V_SCROLL
		// ,SWT.H_SCROLL锟斤拷锟斤拷锟斤拷
		TableViewer tableViewer = new TableViewer(shell, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(30, 20, 420, 330);

		// 锟斤拷锟斤拷button
		btn_ok = new Button(shell, SWT.NONE);
		btn_ok.setBounds(90, 360, 100, 30);
		btn_ok.setText(reg.getString("btn_import"));

		btn_cancel = new Button(shell, SWT.NONE);
		btn_cancel.setBounds(300, 360, 100, 30);
		btn_cancel.setText(reg.getString("btn_cancel"));

		// 锟斤拷锟斤拷锟斤拷
		TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(150);
		newColumnTableColumn.setText(reg.getString("Name_property"));

		TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(140);
		newColumnTableColumn_1.setText(reg.getString("Name_original_value"));

		TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(140);
		newColumnTableColumn_2.setText(reg.getString("Name_budget_value"));

		TableItem item1 = new TableItem(table, SWT.CENTER);
		TableItem item2 = new TableItem(table, SWT.CENTER);
		TableItem item3 = new TableItem(table, SWT.CENTER);
		TableItem item4 = new TableItem(table, SWT.CENTER);
		TableItem item5 = new TableItem(table, SWT.CENTER);
		TableItem item6 = new TableItem(table, SWT.CENTER);
		TableItem item7 = new TableItem(table, SWT.CENTER);
		TableItem item8 = new TableItem(table, SWT.CENTER);
		TableItem item9 = new TableItem(table, SWT.CENTER);

		// 锟斤拷锟矫碉拷一锟叫碉拷值
		item1.setText(0, getNameOfTC(revision, "jci6_PDxSeq"));
		item2.setText(0, getNameOfTC(revision, "jci6_GEBTTemplate"));
		item3.setText(0, getNameOfTC(revision, "jci6_EQU"));
		item4.setText(0, getNameOfTC(revision, "jci6_EQUSignDate"));
		item5.setText(0, getNameOfTC(revision, "jci6_CalcDate"));
		item6.setText(0, getNameOfTC(revision, "jci6_Responsibility"));
		item7.setText(0, getNameOfTC(revision, "jci6_SignedMoney"));
		item8.setText(0, getNameOfTC(revision, "jci6_PDxSignDate"));
		item9.setText(0, getNameOfTC(revision, "jci6_Remark"));

		try {
			// 锟斤拷锟矫达拷锟捷癸拷锟斤拷锟斤拷值锟斤拷一锟斤拷写锟斤拷指锟斤拷锟斤拷元锟斤拷
			// 原值
			String jci6_PDxSeq = revision.getProperty("jci6_PDxSeq");
			String jci6_GEBTTemplate = revision
					.getProperty("jci6_GEBTTemplate");
			String jci6_EQU = revision.getProperty("jci6_EQU");
			String jci6_EQUSignDate = revision.getProperty("jci6_EQUSignDate");
			String jci6_CalcDate = revision.getProperty("jci6_CalcDate");
			String jci6_Responsibility = revision
					.getProperty("jci6_Responsibility");
			String jci6_PDxSignDate = revision.getProperty("jci6_PDxSignDate");

			item1.setText(1, jci6_PDxSeq);
			item2.setText(1, jci6_GEBTTemplate);

			// 锟斤拷示去锟斤拷锟斤拷锟斤拷摹锟�锟斤拷
			if (!jci6_EQU.equals("")) {
				item3.setText(1, cutString(jci6_EQU, 3));
			} else {
				item3.setText(1, jci6_EQU);
			}

			if (!jci6_EQUSignDate.equals("")) {
				item4.setText(
						1,
						jci6_EQUSignDate.substring(0,
								jci6_EQUSignDate.indexOf(" ")));
			} else {
				item4.setText(1, jci6_EQUSignDate);
			}

			if (!jci6_CalcDate.equals("")) {
				item5.setText(1,
						jci6_CalcDate.substring(0, jci6_CalcDate.indexOf(" ")));
			} else {
				item5.setText(1, jci6_CalcDate);
			}

			item6.setText(1, jci6_Responsibility);
			item7.setText(1, revision.getProperty("jci6_SignedMoney"));

			if (!jci6_PDxSignDate.equals("")) {
				item8.setText(
						1,
						jci6_PDxSignDate.substring(0,
								jci6_PDxSignDate.indexOf(" ")));
			} else {
				item8.setText(1, jci6_PDxSignDate);
			}

			item9.setText(1, revision.getProperty("jci6_Remark"));

			if (maps != null) {
				item1.setText(2, maps.get("PDx_Squence"));
				item2.setText(2, maps.get("GEBT_Version"));
				item3.setText(2, maps.get("EQU"));
				item4.setText(2, maps.get("EQU_Signdate"));
				item5.setText(2, maps.get("Calc_Date"));
				item6.setText(2, maps.get("calculated_by"));
				item7.setText(2, maps.get("approved_amount"));
				item8.setText(2, maps.get("PDx_Sign_Date"));
				item9.setText(2, maps.get("remark"));
			}

			shell.open();

			table.addListener(SWT.MeasureItem, new Listener() {
				public void handleEvent(Event event) {
					event.height = 30;
				}
			});

			btn_cancel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {

					// 锟截闭碉拷前锟斤拷锟斤拷
					shell.dispose();
				}
			});

			btn_ok.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					JacobEReportTool tool = new JacobEReportTool();
					int isClose = 0;
					try {
						// 锟斤拷锟芥本锟窖撅拷锟斤拷锟斤拷锟斤拷锟斤拷直锟斤拷锟睫改ｏ拷要锟斤拷锟斤拷路
						// if
						// (revision.getProperty("release_status_list").equals(
						// reg.getString("TCM_release"))) {
						//
						// // 锟斤拷锟斤拷预锟斤拷说锟斤拷锟街达拷锟�TBL
						// if (isF == 2) {
						// try {
						// // 锟矫碉拷系统锟斤拷锟斤拷时锟侥硷拷
						// String getenv = System.getenv("TEMP");
						// String TC_path = System.getenv("TPR");
						// tool.addDir(TC_path + "\\plugins");
						// // 锟矫碉拷锟斤拷锟叫碉拷sheet
						// Dispatch sheetsAll = tool.getSheets(getenv
						// + File.separator + ExcelNameMode);
						//
						// // 锟饺硷拷锟揭伙拷锟揭拷锟饺★拷锟街�
						// checkExcel(maps, tool, sheetsAll, revision);
						//
						// if (error_list.size() != 0) {
						// isClose = 1;
						//
						// tool.closeExcelFile(false);
						//
						// display.dispose();
						// Excel_ErrorDialog dialog_error = new
						// Excel_ErrorDialog(
						// error_list);
						// return;
						// }
						//
						// // 锟斤拷删锟斤拷姹撅拷碌摹锟斤拷锟斤拷锟斤拷锟较拷锟斤拷锟�
						// deleteCostInfo(revision);
						// // 锟斤拷锟铰斤拷锟斤拷锟斤拷锟斤拷息
						// // 锟斤拷取excel锟斤拷锟斤拷锟斤拷荩锟斤拷锟叫达拷锟斤拷锟斤拷锟斤拷锟较拷锟�
						//
						// // 锟矫碉拷锟斤拷选锟斤拷锟街�--YFJC_HumanLabor_Position
						// getValueOfExcelByPreference(session,
						// YFJC_HumanLabor_Position,
						// maps.get("GEBT_Version"));
						// // 锟劫达拷锟斤拷锟剿癸拷锟斤拷息
						// setValuefromExcel(revision,
						// maps_Prefer_values.get("Sheet"),
						// Integer.parseInt(maps_Prefer_values
						// .get("StartRow")),
						// Integer.parseInt(maps_Prefer_values
						// .get("EndRow")),
						// Integer.parseInt(maps_Prefer_values
						// .get("StartCol")),
						// endposition_labor,
						// Integer.parseInt(maps_Prefer_values
						// .get("Position")), tool,
						// sheetsAll);
						//
						// // 锟矫碉拷锟斤拷选锟斤拷锟街�--YFJC_NonLabor_Position
						// getValueOfExcelByPreference(session,
						// YFJC_NonLabor_Position,
						// maps.get("GEBT_Version"));
						//
						// System.out
						// .println("-----------锟斤拷始锟斤拷锟斤拷锟斤拷锟剿癸拷锟斤拷锟斤拷目锟斤拷息锟斤拷锟斤拷锟斤拷--------");
						// // 锟劫达拷锟斤拷锟斤拷锟剿癸拷锟斤拷息
						// setValuefromNonLabel(revision,
						// maps_Prefer_values.get("Sheet"),
						// Integer.parseInt(maps_Prefer_values
						// .get("StartRow")),
						// Integer.parseInt(maps_Prefer_values
						// .get("EndRow")),
						// Integer.parseInt(maps_Prefer_values
						// .get("StartCol")),
						// endposition_nonlabor,
						// Integer.parseInt(maps_Prefer_values
						// .get("Position")), tool,
						// sheetsAll);
						//
						// System.out
						// .println("------------------------------删锟斤拷锟斤拷值锟侥凤拷锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷------------------------");
						// deleteCostInfo_noValue(revision);
						// } catch (TCException e1) {
						// // TODO Auto-generated catch block
						// e1.printStackTrace();
						// } catch (Exception e2) {
						// // TODO Auto-generated catch block
						// e2.printStackTrace();
						// } finally {
						// if (isClose == 0) {
						// tool.closeExcelFile(false);
						// }
						// }
						//
						// }
						//
						// try {
						// setDataOfRev(revision, maps);
						//
						// } catch (Exception e3) {
						// // TODO: handle exception
						// }
						//
						// } else {
						// 执锟斤拷TBL锟斤拷锟斤拷
						if (isF == 2) {
							try {
								// 锟斤拷锟铰斤拷锟斤拷锟斤拷锟斤拷息
								// 锟斤拷取excel锟斤拷锟斤拷锟斤拷荩锟斤拷锟叫达拷锟斤拷锟斤拷锟斤拷锟较拷锟�
								// 锟矫碉拷系统锟斤拷锟斤拷时锟侥硷拷
								String getenv = System.getenv("TEMP");
								String TC_path = System.getenv("TPR");
								tool.addDir(TC_path + "\\plugins");
								// 锟矫碉拷锟斤拷锟叫碉拷sheet
								Dispatch sheetsAll = tool.getSheets(getenv
										+ File.separator + ExcelNameMode);

								// 锟饺硷拷锟揭伙拷锟揭拷锟饺★拷锟街�
								checkExcel(maps, tool, sheetsAll, revision);

								if (error_list.size() != 0) {

									isClose = 1;
									// 锟截憋拷EXCEL锟斤拷锟�
									tool.closeExcelFile(false);

									display.dispose();
									Excel_ErrorDialog dialog_error = new Excel_ErrorDialog(
											error_list);
									return;
								}

								// 删锟斤拷锟斤拷锟斤拷锟较拷锟斤拷锟�
								deleteCostInfo(revision);

								// 锟矫碉拷锟斤拷选锟斤拷锟街�--YFJC_HumanLabor_Position
								getValueOfExcelByPreference(session,
										YFJC_HumanLabor_Position,
										maps.get("GEBT_Version"));

								TYPE_HumanLaborYuan = 0;

								// 锟劫达拷锟斤拷锟剿癸拷锟斤拷息
								setValuefromExcel(revision, maps_Prefer_values
										.get("Sheet"), Integer
										.parseInt(maps_Prefer_values
												.get("StartRow")), Integer
										.parseInt(maps_Prefer_values
												.get("EndRow")), Integer
										.parseInt(maps_Prefer_values
												.get("StartCol")),
										endposition_labor, Integer
												.parseInt(maps_Prefer_values
														.get("Position")),
										tool, sheetsAll);

								// 锟矫碉拷锟斤拷选锟斤拷锟街�--YFJC_HumanLabor_Position
								getValueOfExcelByPreference(session,
										YFJC_LaborCostYuan_Position,
										maps.get("GEBT_Version"));

								TYPE_HumanLaborYuan = 1;

								// 7.2锟睫革拷 锟劫达拷锟斤拷锟剿癸拷锟斤拷息---yuan
								setValuefromExcel(revision, maps_Prefer_values
										.get("Sheet"), Integer
										.parseInt(maps_Prefer_values
												.get("StartRow")), Integer
										.parseInt(maps_Prefer_values
												.get("EndRow")), Integer
										.parseInt(maps_Prefer_values
												.get("StartCol")),
										endposition_labor, Integer
												.parseInt(maps_Prefer_values
														.get("Position")),
										tool, sheetsAll);

								// 锟矫碉拷锟斤拷选锟斤拷锟街�
								getValueOfExcelByPreference(session,
										YFJC_NonLabor_Position,
										maps.get("GEBT_Version"));
								// 锟劫达拷锟斤拷锟斤拷锟剿癸拷锟斤拷息
								setValuefromNonLabel(revision,
										maps_Prefer_values.get("Sheet"),
										Integer.parseInt(maps_Prefer_values
												.get("StartRow")), Integer
												.parseInt(maps_Prefer_values
														.get("EndRow")),
										Integer.parseInt(maps_Prefer_values
												.get("StartCol")),
										endposition_nonlabor, Integer
												.parseInt(maps_Prefer_values
														.get("Position")),
										tool, sheetsAll);

								System.out
										.println("------------------------------删锟斤拷锟斤拷值锟侥凤拷锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷------------------------");

								// 5.27----删锟斤拷锟斤拷值锟矫凤拷锟斤拷锟斤拷息锟斤拷锟斤拷
								deleteCostInfo_noValue(revision);

							} catch (TCException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (Exception e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							} finally {
								if (isClose == 0) {
									tool.closeExcelFile(false);
								}
							}
						}

						// 锟睫革拷指锟斤拷锟芥本锟斤拷锟斤拷锟斤拷
						setDataOfRev(revision, maps);

						if (isDatasetRelease(dataset)) {
							// 锟斤拷锟斤拷锟斤拷锟斤拷
							releaseRev(revision);
						}
						// }

						System.out
								.println("=================删锟斤拷姹撅拷碌锟斤拷锟斤拷械锟絛ataset===========");
						TCComponent[] relatedComponents = revision
								.getRelatedComponents("IMAN_specification");
						if (relatedComponents.length > 0) {
							for (int i = 0; i < relatedComponents.length; i++) {
								if (relatedComponents[i] instanceof TCComponentDataset) {
									openByPass();
									revision.remove("IMAN_specification",
											relatedComponents[i]);
									closeByPass();
								}
							}
						}

						System.out
								.println("=================锟斤拷dataset锟斤拷拥锟斤拷姹撅拷掳锟絛ataset===========");
						openByPass();
						revision.add("IMAN_specification", dataset);
						closeByPass();

						// 锟斤拷锟紾EBT锟侥硷拷锟角凤拷锟斤拷锟斤拷锟斤拷锟叫ｏ拷锟酵帮拷锟斤拷锟斤拷姹疽诧拷锟接碉拷锟斤拷锟斤拷锟斤拷
						TCComponentTask rootTask = null;

						// 5.17锟睫革拷---dataset锟斤拷菁锟斤拷欠锟斤拷锟�
						if (isDatasetINProcesssList(dataset)) {
							if (dataset.getCurrentJob() != null) {
								openByPass();
								rootTask = dataset.getCurrentJob()
										.getRootTask();
								closeByPass();

							}
						}

						if (rootTask != null) {
							TCComponent[] attachments = rootTask
									.getAttachments(TCAttachmentScope.LOCAL, 1);

							int findFlag = 0;
							int findDateSet = 0;
							for (int i = 0; i < attachments.length; i++) {
								if (attachments[i] instanceof TCComponentDataset) {
									if (attachments[i] == dataset) {
										findDateSet = 1;
									}
								}
								if (attachments[i] instanceof TCComponentItemRevision) {
									if (attachments[i] == revision) {
										findFlag = 1;
									}
								}
							}

							if (findFlag != 1 && findDateSet == 1) {
								openByPass();
								rootTask.addAttachments(
										TCAttachmentScope.GLOBAL,
										new TCComponent[] { revision },
										new int[] { 1 });
								closeByPass();
							}

						}

					} catch (TCException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} finally {
						System.out.println("锟截憋拷锟斤拷锟叫对伙拷锟斤拷锟斤拷锟�!!");
						// 锟截憋拷锟斤拷锟叫对伙拷锟斤拷锟斤拷锟�
						display.dispose();
					}
				}
			});

		} catch (TCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// 锟睫改碉拷原锟斤拷锟斤拷锟斤拷锟斤拷
	private void setDataOfRev(TCComponentItemRevision revision,
			HashMap<String, String> maps) throws TCException {
		try {
			System.out.println("----锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷薷目锟绞�----");

			// 锟斤拷锟斤拷路
			// combo锟斤拷锟斤拷锟斤拷锟斤拷锟矫碉拷锟斤拷锟斤拷锟斤拷
			openByPass();
			revision.setStringProperty("jci6_PDxSeq", maps.get("PDx_Squence"));
			closeByPass();

			openByPass();
			revision.setStringProperty("jci6_GEBTTemplate",
					maps.get("GEBT_Version"));
			closeByPass();

			// maps.get("EQU")
			openByPass();
			revision.setProperty("jci6_EQU", maps.get("EQU"));
			closeByPass();
			System.out.println("------锟睫革拷jci6_EQU--------" + maps.get("EQU"));

			openByPass();
			revision.setProperty("jci6_SignedMoney",
					maps.get("approved_amount"));
			closeByPass();

			// 4.18锟睫革拷--锟芥本锟斤拷current_revision_id锟斤拷锟斤拷锟斤拷锟斤拷PDx_Squence锟斤拷值锟斤拷锟斤拷锟斤拷
			System.out.println("------锟睫革拷item_revision_id锟斤拷锟斤拷---------"
					+ maps.get("PDx_Squence"));
			openByPass();
			revision.setProperty("item_revision_id", maps.get("PDx_Squence"));
			closeByPass();

			openByPass();
			revision.setProperty("jci6_Remark", maps.get("remark"));
			closeByPass();

			// 锟斤拷锟斤拷calculated_by锟斤拷值
			/**
			if (!maps.get("calculated_by").equals("")) {

				TCComponentUserType userType = (TCComponentUserType) session
						.getTypeComponent("User");
				TCComponentUser user = userType.find(maps.get("calculated_by"));

				if (user != null) {
					openByPass();
					revision.setReferenceProperty("jci6_Responsibility", user);
					closeByPass();
				}
				System.out.println("========锟睫革拷jci6_Responsibility锟斤拷锟�====="
						+ user.toString());
			}**/
			
			if (!maps.get("calculated_by").equals("")) {
			    /*william zhou <2013-07-31>*/
			    TCComponentListOfValues lov = revision.getTCProperty("jci6_Responsibility").getLOV();
		        TCComponent[] components = lov.getRelatedComponents();
		        ArrayList<String> userLists = new ArrayList<String>();
		        TCComponentUser user = null;
		        for (int j = 0; j < components.length; j++) {
		            if (components[j] instanceof TCComponentUser) 
		            {
		                String userName = ((TCComponentUser)components[j]).getProperty("user_name");
		                if(userName.equals(maps.get("calculated_by")))
		                {
		                    user = (TCComponentUser)components[j];
		                    break;
		                }
		            }
		        }
			}

			setDataByPass(revision, "jci6_EQUSignDate",
					maps.get("EQU_Signdate"));
			 System.out.println("========锟睫革拷锟斤拷锟斤拷EQUSignDate锟斤拷锟�=====");
			setDataByPass(revision, "jci6_CalcDate", maps.get("Calc_Date"));
			 System.out.println("========锟睫革拷锟斤拷锟斤拷Calc_Date锟斤拷锟�=====");

			setDataByPass(revision, "jci6_PDxSignDate",
					maps.get("PDx_Sign_Date"));
			 System.out.println("========锟睫革拷锟斤拷锟斤拷PDx_Sign_Date锟斤拷锟�=====");

			System.out.println("------锟睫革拷锟斤拷锟斤拷锟斤拷锟�----");

			/*
			 * else {
			 * 
			 * System.out.println("-------锟芥本锟斤拷锟斤拷没锟叫凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟睫革拷-----");
			 * 
			 * // combo锟斤拷锟斤拷锟斤拷锟斤拷锟矫碉拷锟斤拷锟斤拷锟斤拷 revision.setStringProperty("jci6_PDxSeq",
			 * maps.get("PDx_Squence"));
			 * revision.setStringProperty("jci6_GEBTTemplate",
			 * maps.get("GEBT_Version"));
			 * 
			 * System.out.println("-------锟睫革拷PDx_Squence:"+maps.get("PDx_Squence")
			 * +"      锟斤拷GEBT_Version------"+maps.get("GEBT_Version"));
			 * 
			 * // maps.get("EQU") revision.setProperty("jci6_EQU",
			 * maps.get("EQU"));
			 * System.out.println("------锟睫革拷jci6_EQU--------"+maps.get("EQU"));
			 * 
			 * 
			 * String Calc_Date = maps.get("Calc_Date"); if
			 * (!Calc_Date.equals("")) {
			 * revision.setDateProperty("jci6_CalcDate", sdf.parse(Calc_Date));
			 * System.out.println("----------jci6_CalcDate锟斤拷锟斤拷为锟斤拷"+Calc_Date); }
			 * else { revision.setDateProperty("jci6_CalcDate", null);
			 * System.out.println("----------jci6_CalcDate锟斤拷锟斤拷为NULL锟斤拷锟斤拷锟斤拷"); }
			 * 
			 * String EQU_Signdate = maps.get("EQU_Signdate"); if
			 * (!EQU_Signdate.equals("")) {
			 * revision.setDateProperty("jci6_EQUSignDate",
			 * sdf.parse(EQU_Signdate));
			 * System.out.println("----------jci6_EQUSignDate锟斤拷锟斤拷为锟斤拷"
			 * +EQU_Signdate); } else {
			 * revision.setDateProperty("jci6_EQUSignDate", null);
			 * System.out.println("---------jci6_CalcDate锟斤拷锟斤拷为NULL锟斤拷锟斤拷锟斤拷"); }
			 * 
			 * // 锟斤拷锟斤拷calculated_by锟斤拷值 TCComponentUserType userType =
			 * (TCComponentUserType) session .getTypeComponent("User");
			 * TCComponentUser user = userType.find(maps.get("calculated_by"));
			 * System.out.println("---------锟揭碉拷calculated_by锟斤拷USER锟斤拷锟斤拷锟斤拷-----");
			 * revision.setReferenceProperty("jci6_Responsibility", user);
			 * System
			 * .out.println("---------锟睫革拷jci6_Responsibility锟斤拷USER锟斤拷锟斤拷锟斤拷-----");
			 * 
			 * // maps.get("approved_amount")
			 * revision.setProperty("jci6_SignedMoney",
			 * maps.get("approved_amount"));
			 * System.out.println("---------锟睫革拷jci6_SignedMoney锟斤拷值------"
			 * +maps.get("approved_amount"));
			 * 
			 * String PDx_Sign_Date = maps.get("PDx_Sign_Date"); if
			 * (!PDx_Sign_Date.equals("")) {
			 * revision.setDateProperty("jci6_PDxSignDate",
			 * sdf.parse(PDx_Sign_Date));
			 * System.out.println("----------jci6_PDxSignDate锟斤拷锟斤拷为锟斤拷"
			 * +PDx_Sign_Date); } else {
			 * revision.setDateProperty("jci6_PDxSignDate", null);
			 * System.out.println("---------jci6_PDxSignDate锟斤拷锟斤拷为NULL锟斤拷锟斤拷锟斤拷"); }
			 * 
			 * // 4.18锟睫革拷--锟芥本锟斤拷current_revision_id锟斤拷锟斤拷锟斤拷锟斤拷PDx_Squence锟斤拷值锟斤拷锟斤拷锟斤拷
			 * revision.setProperty("item_revision_id",
			 * maps.get("PDx_Squence"));
			 * System.out.println("---------item_revision_id锟斤拷锟斤拷为-----"
			 * +maps.get("PDx_Squence"));
			 * 
			 * System.out.println("----锟睫革拷锟斤拷锟斤拷锟斤拷锟�----"); }
			 */
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 锟斤拷锟斤拷路
	private void openByPass() {
		try {
			session.getUserService().call("open_or_close_pass",
					new Object[] { 1 });

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 锟截憋拷锟斤拷路
	private void closeByPass() {
		try {
			session.getUserService().call("open_or_close_pass",
					new Object[] { 0 });
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 锟斤拷锟斤拷锟芥本锟斤拷锟斤拷锟斤拷
	private void releaseRev(TCComponent ir) throws TCException {
		// 锟饺得碉拷ReleaseStatusType
		TCComponentReleaseStatusType rlaType = (TCComponentReleaseStatusType) session
				.getTypeComponent("ReleaseStatus");

		// 锟斤拷锟斤拷系统锟侥匡拷锟劫凤拷锟斤拷锟斤拷锟教★拷锟斤拷锟斤拷锟斤拷"TCM 锟窖凤拷锟斤拷"
		openByPass();
		TCComponent tcp = rlaType.create(reg.getString("TCM_release"));
		closeByPass();

		// 锟斤拷锟斤拷TCComponentReleaseStatusType锟斤拷锟斤拷锟�
		openByPass();
		tcp.save();
		closeByPass();

		// 锟斤拷拥锟街革拷锟斤拷陌姹撅拷锟斤拷锟斤拷校锟斤拷蠖ü锟较�
		openByPass();
		ir.add("release_status_list", tcp);
		closeByPass();
	}

	// 锟睫革拷TC锟斤拷锟窖凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
	private void setDataByPass(TCComponentItemRevision revision, String name,
			String value) throws TCException, Exception {

		if (name.equals("jci6_EQUSignDate") || name.equals("jci6_CalcDate")
				|| name.equals("jci6_PDxSignDate")) {
			if (!value.equals("")&&value.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])")) {
				openByPass();
				revision.setDateProperty(name, sdf.parse(value));
				closeByPass();
			}
		} else {
			openByPass();
			revision.setProperty(name, value);
			closeByPass();
		}

	}

	// 锟揭碉拷TC系统锟斤拷锟接︼拷锟斤拷锟斤拷锟�
	// 锟剿癸拷锟斤拷息锟斤拷锟斤拷锟斤拷学锟斤拷
	private boolean isSearchvalueofPreference(String value, String properityName) {

		boolean flag = false;
		try {
			// 锟斤拷锟斤拷锟斤拷锟轿拷锟斤拷椤�
			if (properityName.equals("Group")) {
				TCComponentGroupType groupType = (TCComponentGroupType) session
						.getTypeComponent(properityName);

				TCComponentGroup componentGroup = groupType.find(value);
				if (componentGroup != null) {
					flag = true;
				}
			} else if (properityName.equals("Section")) {
				// 锟斤拷询学锟斤拷
				TCComponent[] tcp = query(session, "YFJC_Search_Discipline",
						new String[] { "discipline_name" },
						new String[] { value });
				if (tcp[0] != null) {
					flag = true;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return flag;
	}

	// 锟斤拷锟斤拷 ---锟剿癸拷锟斤拷锟斤拷锟斤拷息锟酵凤拷锟剿癸拷锟斤拷锟斤拷锟斤拷息
	private TCComponent createCostInfo(TCSession session,
			TCComponentItemRevision revision, String object_name, int type,
			String GroupName, String SelectionName, int year, String costType)
			throws TCException, ServiceException {
		// 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷息
		DataManagementService dmService = DataManagementService
				.getService(session);

		com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[] createIn = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];

		createIn[0] = new DataManagement.CreateIn();

		createIn[0].data.boName = "JCI6_CostInfo";

		ProjectID = revision.getProperty("item_id");
		// 锟矫碉拷时锟斤拷锟�
		String timestamp = getSystemTime();
		String name = "";

		String name_Unit = "";
		if (object_name.equals("锟斤拷锟斤拷")) {
			name_Unit = "ManMonth";
		} else if (object_name.equals("小时")) {
			name_Unit = "Hour";
		} else if (object_name.equals("元")) {
			name_Unit = "Yuan";
		}

		// 锟饺得碉拷group锟侥讹拷锟斤拷,锟斤拷荻锟斤拷锟矫碉拷Group锟侥讹拷写锟斤拷锟斤拷
		TCComponentGroup componentGroup = null;
		if (type != 1) {
			TCComponentGroupType groupType = (TCComponentGroupType) session
					.getTypeComponent("Group");
			componentGroup = groupType.find(GroupName);
		}

		if (type == 1) {
			name = ProjectID + "_Budget_" + costType + "_" + year + "_"
					+ timestamp;
			createIn[0].data.stringProps.put("object_name", name);
		} else {
			name = ProjectID + "_Budget_" + year + "_"
					+ componentGroup.getProperty("name") + "_" + SelectionName
					+ "_" + name_Unit + "_" + timestamp;
			createIn[0].data.stringProps.put("object_name", name);

		}

		CreateResponse responese = dmService.createObjects(createIn);
		TCComponent component = responese.output[0].objects[0];

		TCProperty[] property = null;
		if (type == 1) {
			// 锟斤拷锟剿癸拷锟斤拷锟斤拷锟斤拷息
			// 锟斤拷TCProperty[]锟斤拷锟芥储一系锟斤拷要锟睫改碉拷TC锟叫碉拷锟斤拷锟斤拷锟斤拷锟�
			System.out.println("---------锟斤拷锟斤拷锟斤拷锟剿癸拷锟斤拷息---------");

			property = component.getTCProperties(new String[] { "jci6_CPT",
					"jci6_CostType", "jci6_Unit", "jci6_Year" });

			property[0].setStringValueData("Budget");
			property[1].setStringValueData(costType);
			property[2].setStringValueData(name_Unit);

			// 锟斤拷锟斤拷int锟斤拷
			property[3].setIntValueData(year);
			component.setTCProperties(property);

		} else {
			// 锟剿癸拷锟斤拷锟斤拷锟斤拷息
			System.out.println("---------锟斤拷锟斤拷锟剿癸拷锟斤拷息---------");

			// 锟斤拷TCProperty[]锟斤拷锟芥储一系锟斤拷要锟睫改碉拷TC锟叫碉拷锟斤拷锟斤拷锟斤拷锟�
			property = component.getTCProperties(new String[] { "jci6_CPT",
					"jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Division",
					"jci6_RateLevel" });

			property[0].setStringValueData("Budget");
			property[1].setStringValueData("Normal Hours");
			property[2].setStringValueData(name_Unit);

			// 锟斤拷锟斤拷int锟斤拷,
			property[3].setIntValueData(year);

			// 锟斤拷询指锟斤拷锟斤拷锟斤拷
			property[4].setReferenceValueData(componentGroup);

			// System.out.println("---学锟斤拷锟斤拷锟�----" + SelectioName);
			// 锟斤拷询学锟斤拷
			TCComponent[] tcp = query(session, "YFJC_Search_Discipline",
					new String[] { "discipline_name" },
					new String[] { SelectionName });
			if (tcp != null)
				property[5].setReferenceValueData(tcp[0]);
			else
				System.out.println("-----锟斤拷询学锟斤拷锟斤拷锟矫伙拷业锟斤拷锟斤拷锟斤拷锟�----");

			// 5.4锟睫革拷----锟剿癸拷锟斤拷锟斤拷
			component.setTCProperties(property);

		}

		// 锟铰挂碉拷指锟斤拷锟侥版本锟斤拷
		openByPass();
		revision.add("IMAN_external_object_link", component);
		closeByPass();

		// 7.2锟睫革拷---锟斤拷锟斤拷菁锟斤拷锟斤拷锟侥匡拷锟街革拷筛锟斤拷麓锟斤拷锟斤拷姆锟斤拷锟斤拷锟较�
		openByPass();
		projectComponent.add("project_data", component);
		closeByPass();

		return component;
	}

	// 删锟斤拷锟斤拷锟斤拷锟较拷锟斤拷锟�
	private void deleteCostInfo(TCComponentItemRevision revision)
			throws TCException {
		TCComponent[] tcComponents = revision
				.getReferenceListProperty("IMAN_external_object_link");
		for (int i = 0; i < tcComponents.length; i++) {
			openByPass();
			revision.remove("IMAN_external_object_link", tcComponents[i]);
			closeByPass();

			openByPass();
			tcComponents[i].delete();
			closeByPass();
		}
	}

	// 5.27----删锟斤拷锟斤拷值锟矫凤拷锟斤拷锟斤拷息锟斤拷锟斤拷
	private void deleteCostInfo_noValue(TCComponentItemRevision revision)
			throws TCException {

		TCComponent[] tcComponents = revision
				.getReferenceListProperty("IMAN_external_object_link");
		for (int i = 0; i < tcComponents.length; i++) {
			if (isCostInfoNoValue(tcComponents[i])) {
				openByPass();
				revision.remove("IMAN_external_object_link", tcComponents[i]);
				closeByPass();

				openByPass();
				tcComponents[i].delete();
				closeByPass();
			}else{
				tcComponents[i].lock();
				releaseRev(tcComponents[i]);
				tcComponents[i].save();
				tcComponents[i].unlock();
				
			}
		}

	}

	// 5.27----删锟斤拷锟斤拷值锟矫凤拷锟斤拷锟斤拷息锟斤拷锟斤拷
	private boolean isCostInfoNoValue(TCComponent component) throws TCException {
		String jci6_Jan_str = component.getProperty("jci6_Jan");
		String jci6_Feb_str = component.getProperty("jci6_Feb");
		String jci6_Mar_str = component.getProperty("jci6_Mar");
		String jci6_Apr_str = component.getProperty("jci6_Apr");
		String jci6_May_str = component.getProperty("jci6_May");
		String jci6_Jun_str = component.getProperty("jci6_Jun");
		String jci6_Jul_str = component.getProperty("jci6_Jul");
		String jci6_Aug_str = component.getProperty("jci6_Aug");
		String jci6_Sep_str = component.getProperty("jci6_Sep");
		String jci6_Oct_str = component.getProperty("jci6_Oct");
		String jci6_Nov_str = component.getProperty("jci6_Nov");
		String jci6_Dec_str = component.getProperty("jci6_Dec");

		double jci6_Jan = Double.parseDouble(jci6_Jan_str);
		double jci6_Feb = Double.parseDouble(jci6_Feb_str);
		double jci6_Mar = Double.parseDouble(jci6_Mar_str);
		double jci6_Apr = Double.parseDouble(jci6_Apr_str);
		double jci6_May = Double.parseDouble(jci6_May_str);
		double jci6_Jun = Double.parseDouble(jci6_Jun_str);
		double jci6_Jul = Double.parseDouble(jci6_Jul_str);
		double jci6_Aug = Double.parseDouble(jci6_Aug_str);
		double jci6_Sep = Double.parseDouble(jci6_Sep_str);
		double jci6_Oct = Double.parseDouble(jci6_Oct_str);
		double jci6_Nov = Double.parseDouble(jci6_Nov_str);
		double jci6_Dec = Double.parseDouble(jci6_Dec_str);

		if (jci6_Jan == 0 && jci6_Feb == 0 && jci6_Mar == 0 && jci6_Apr == 0
				&& jci6_May == 0 && jci6_Jun == 0 && jci6_Jul == 0
				&& jci6_Aug == 0 && jci6_Sep == 0 && jci6_Oct == 0
				&& jci6_Nov == 0 && jci6_Dec == 0) {
			return true;
		} else {
			return false;
		}

	}

	// 锟叫讹拷锟矫伙拷选锟斤拷锟斤拷锟捷硷拷锟角否发诧拷
	private boolean isDatasetRelease(TCComponentDataset dataset)
			throws TCException {
		String release = dataset.getProperty("release_status_list");
		if (!release.equals(reg.getString("TCM_release")))
			return false;
		else
			return true;
	}

	// 5.17---- 锟叫讹拷锟矫伙拷选锟斤拷锟斤拷锟捷硷拷锟角凤拷锟斤拷锟斤拷锟斤拷锟斤拷
	private boolean isDatasetINProcesssList(TCComponentDataset dataset)
			throws TCException {
		TCComponent[] tcComponents = dataset
				.getReferenceListProperty("process_stage_list");

		if (tcComponents == null || tcComponents.length == 0)
			return false;
		else
			return true;
	}

	// 锟饺硷拷锟揭伙拷锟揭拷锟饺★拷锟街�
	private void checkExcel(HashMap<String, String> maps,
			JacobEReportTool tool, Dispatch sheetsAll,
			TCComponentItemRevision revision) {

		error_list = new ArrayList<HashMap<String, String>>();

		// 锟节硷拷锟斤拷时锟斤拷锟饺★拷锟揭拷锟斤拷械锟斤拷锟街刮伙拷锟�
		try {
			// 锟矫碉拷锟斤拷选锟斤拷锟街�--YFJC_HumanLabor_Position
			boolean b = getValueOfExcelByPreference(session,
					YFJC_HumanLabor_Position, maps.get("GEBT_Version"));

			if (b) {
				checkValueOfHumanLaborfromExcel(
						maps_Prefer_values.get("Sheet"),
						Integer.parseInt(maps_Prefer_values.get("StartRow")),
						Integer.parseInt(maps_Prefer_values.get("EndRow")),
						Integer.parseInt(maps_Prefer_values.get("StartCol")),
						Integer.parseInt(maps_Prefer_values.get("EndCol")),
						Integer.parseInt(maps_Prefer_values.get("Position")),
						tool, sheetsAll, revision, maps.get("GEBT_Version"));
			}

			System.out.println("------锟斤拷锟斤拷斯锟斤拷锟斤拷煤锟斤拷------锟斤拷锟斤拷list锟斤拷锟斤拷锟斤拷---"
					+ error_list.size());

			// 7.2锟睫革拷---- 锟矫碉拷锟斤拷选锟斤拷锟街�--YFJC_HumanLabor_Position
			boolean d = getValueOfExcelByPreference(session,
					YFJC_LaborCostYuan_Position, maps.get("GEBT_Version"));

			if (d) {
				checkValueOfHumanLaborfromExcel(
						maps_Prefer_values.get("Sheet"),
						Integer.parseInt(maps_Prefer_values.get("StartRow")),
						Integer.parseInt(maps_Prefer_values.get("EndRow")),
						Integer.parseInt(maps_Prefer_values.get("StartCol")),
						Integer.parseInt(maps_Prefer_values.get("EndCol")),
						Integer.parseInt(maps_Prefer_values.get("Position")),
						tool, sheetsAll, revision, maps.get("GEBT_Version"));
			}

			System.out.println("------锟斤拷锟斤拷取锟剿癸拷锟斤拷锟斤拷元锟斤拷excel锟斤拷锟�-----锟斤拷锟斤拷list锟斤拷锟斤拷锟斤拷---"
					+ error_list.size());

			// 锟矫碉拷锟斤拷选锟斤拷锟街�--YFJC_NonLabor_Position
			boolean c = getValueOfExcelByPreference(session,
					YFJC_NonLabor_Position, maps.get("GEBT_Version"));

			if (c) {
				checkValueOfNon_LaborfromExcel(maps_Prefer_values.get("Sheet"),
						Integer.parseInt(maps_Prefer_values.get("StartRow")),
						Integer.parseInt(maps_Prefer_values.get("EndRow")),
						Integer.parseInt(maps_Prefer_values.get("StartCol")),
						Integer.parseInt(maps_Prefer_values.get("EndCol")),
						Integer.parseInt(maps_Prefer_values.get("Position")),
						tool, sheetsAll, maps.get("GEBT_Version"));
			}
			System.out.println("----锟斤拷锟斤拷list锟斤拷锟斤拷锟斤拷---" + error_list.size());
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 锟斤拷锟斤拷斯锟斤拷锟斤拷锟街革拷锟絜xcel锟斤拷锟�
	 */
	private void checkValueOfHumanLaborfromExcel(String sheet_name,
			int startRow, int endRow, int starDate, int endDate,
			int datePosition, JacobEReportTool tool, Dispatch sheetsAll,
			TCComponentItemRevision revision, String GEBTVersion)
			throws TCException, IOException {

		System.out.println("-----锟斤拷锟斤拷斯锟斤拷锟斤拷锟�------锟矫碉拷锟斤拷选锟斤拷姹厩帮拷锟街�---------");

		// System.out.println("===================================="
		// + YFJC_HumanLabor_Section + "_" + GEBTVersion
		// + "=======================");
		// System.out.println("===================================="
		// + YFJC_HumanLabor_Group + "_" + GEBTVersion
		// + "=======================");

		String Section_name = searchNameOfReference(session,
				YFJC_HumanLabor_Section + "_" + GEBTVersion);
		String Group_name = searchNameOfReference(session,
				YFJC_HumanLabor_Group + "_" + GEBTVersion);

		// System.out
		// .println("----------------------锟矫碉拷锟斤拷选锟斤拷姹撅拷院锟斤拷值-----------------");
		//
		// System.out
		// .println("=============Section_name=========================="
		// + Section_name);
		// System.out.println("=============Group_name=========================="
		// + Group_name);

		if (Section_name.equals("")) {
			String error_S = YFJC_HumanLabor_Section + GEBTVersion
					+ " not exists!!!";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("error_Section", error_S);
			error_list.add(map);
		} else {
			nameOfPreference.put("Section", Section_name);
		}

		if (Group_name.equals("")) {
			String error_G = YFJC_HumanLabor_Group + GEBTVersion
					+ " not exists!!!";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("error_Group", error_G);
			error_list.add(map);
		} else {
			nameOfPreference.put("Group", Group_name);
		}

		// 锟斤拷excel锟叫碉拷sheet页
		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		endposition_labor = endDate;

		// 取锟斤拷锟斤拷锟斤拷
		for (int col = starDate; col <= endDate; col++) {
			// 锟斤拷锟铰的凤拷锟斤拷锟斤拷取excel,转锟斤拷锟缴憋拷准锟斤拷锟斤拷
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);
			String data;

			if (!data_string.equals("")) {
				try {
					data = sdf.format(new Date(data_string));
				} catch (Exception e) {
					// 锟斤拷锟斤拷锟斤拷息
					String error_p = sheet_name + ":" + column + datePosition;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_Date", error_p);
					error_list.add(map);
					data = "1900-01-01";
				}

				if (!data
						.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
					// 锟斤拷锟斤拷锟斤拷息
					String error_p = sheet + ":" + "C" + datePosition;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_Date", error_p);
					error_list.add(map);
				}
			} else {
				endposition_labor = col - 1;
				break;
			}

		}

		// System.out.println("==6.3锟睫革拷========================="
		// + endposition_labor);

		// 取锟斤拷指锟斤拷锟斤拷锟斤拷锟街�
		for (int i = startRow; i <= endRow; i++) {
			checkHumanLaborByline(tool, sheet_name, i, starDate,
					endposition_labor, sheet, revision,
					nameOfPreference.get("Section"),
					nameOfPreference.get("Group"));
		}

	}

	/*
	 * 锟斤拷锟揭伙拷锟斤拷斯锟斤拷锟斤拷锟絪heet
	 */
	private void checkHumanLaborByline(JacobEReportTool tool,
			String sheet_name, int row, int starDate, int endDate,
			Dispatch sheet, TCComponentItemRevision revision,
			String Section_name, String Group_name) throws ServiceException,
			TCException {

		// 锟斤拷取每一锟叫的第讹拷锟斤拷,锟斤拷锟斤拷锟叫碉拷位锟矫ｏ拷锟斤拷锟斤拷锟叫讹拷锟斤拷锟斤拷值锟斤拷tc锟斤拷锟角凤拷锟斤拷锟�
		// 1.锟斤拷锟叫讹拷锟斤拷锟斤拷锟斤拷锟斤拷知锟斤拷锟斤拷锟�
		// 2.锟斤拷锟斤拷锟斤拷锟斤拷tc锟斤拷锟角凤拷锟斤拷值
		// 3.锟斤拷锟矫伙拷校锟絩eturn锟斤拷锟斤拷
		String data_key_1 = tool.getDataFromExcel("B", row, sheet);
		String data_key_2 = tool.getDataFromExcel("C", row, sheet);

		// System.out.println("-------B锟叫碉拷值======" + data_key_1);

		// 5.8锟睫革拷--锟斤拷锟絜xcel锟斤拷锟叫碉拷锟斤拷锟斤拷为0.锟斤拷锟斤拷锟斤拷一锟叫撅拷锟斤拷锟�
		String height_str = tool.getHeight("B" + row, sheet);
		if (height_str.equals("0.0")) {
			return;
		}

		// System.out.println("data_key_2=====" + data_key_2);
		if (data_key_2.equals("null")) {

			// System.out.println("锟斤拷锟揭伙拷校锟斤拷锟斤拷锟�====" + sheet + ": " + row);
		} else if (data_key_1.equals("null")
				&& data_key_2.equals("Total FTE's")) {

			// System.out.println("锟斤拷锟揭伙拷校锟斤拷锟斤拷锟�====" + sheet + ": " + row);

		} else if (data_key_1.equals("null") && data_key_2.contains("Total")) {
			// System.out.println("锟斤拷锟揭伙拷校锟斤拷锟斤拷锟�====" + sheet + ": " + row);

		} else {
			// System.out.println("data_key_2=====" + data_key_2);
			String findValue_section = null;
			String Group_value = null;

			findValue_section = isFindValue(data_key_2, session, Section_name);
			if (findValue_section.equals("")) {
				// 锟斤拷锟斤拷
				String error_p = sheet_name + ":" + "C" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Section", error_p);
				error_list.add(map);
			}

			// 5.6锟睫革拷-----锟斤拷锟絛ata_key_2为"SMTP"锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟介，锟斤拷要锟斤拷锟斤拷选锟斤拷锟斤拷锟斤拷锟矫ｏ拷锟揭碉拷锟斤拷锟斤拷锟窖★拷锟�
			if (data_key_2.equals("SMTE")) {
				// 5.6-锟睫革拷
				Group_value = isFindValue(data_key_2, session,
						YFJC_HumanLabor_SMTE_GROUP);
				// System.out.println("SMTE=======" + Group_value);
			} else {
				if (!data_key_1.equals("null")) {
					// 5.8---锟睫改憋拷锟斤拷
					String findValue_group = isFindValue(data_key_1, session,
							Group_name);
					if (!findValue_group.equals("")) {
						// 锟揭碉拷锟斤拷选锟斤拷锟接︼拷锟街�

						// 锟斤拷锟斤拷锟睫改★拷锟斤拷group
						if (findValue_group.equals("jci6_ProgramDivi")) {
							TCComponentGroup tcGroup = (TCComponentGroup) revision
									.getReferenceProperty("jci6_ProgramDivi");
							Group_value = tcGroup.getFullName();
							// System.out.println("-------------Group_Name======"
							// + Group_value);
						} else {
							Group_value = findValue_group;
						}

						map_group.put("Group", Group_value.trim());
					} else {
						// 锟斤拷锟斤拷
						String error_p = sheet_name + ":" + "B" + row;
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("error_Group", error_p);
						error_list.add(map);
					}
				} else {

					Group_value = map_group.get("Group");
				}
			}

			boolean b_gruop = isSearchvalueofPreference(Group_value, "Group");
			if (!b_gruop) {
				// 系统锟斤拷没锟叫讹拷应锟斤拷值
				// 锟斤拷锟斤拷
				String error_p = sheet_name + ":" + "B" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Group", error_p);
				error_list.add(map);
			}

			boolean b_section = isSearchvalueofPreference(findValue_section,
					"Section");
			if (!b_section && !findValue_section.equals("")) {
				// 系统锟斤拷没锟叫讹拷应锟斤拷值
				// 锟斤拷锟斤拷
				String error_p = sheet_name + ":" + "C" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Section", error_p);
				error_list.add(map);
			}

			// 取每锟斤拷锟铰碉拷值
			for (int col = starDate; col <= endDate; col++) {

				// 锟斤拷取每锟街凤拷锟斤拷锟斤拷锟酵碉拷值
				// 锟斤拷ExcelRW锟斤拷取excel
				String data = tool.getDataFromExcel(NumToString(col), row,
						sheet);

				if (data.equals("null") || data.equals("")) {
					data = "0.0";
				}

				double dCostValue;

				// if (data.matches("^(-?\\d+)(\\.\\d+)?$")) {
				try {
					dCostValue = Double.parseDouble(data);
					// System.out.println("-----锟斤拷锟斤拷锟斤拷----" + dCostValue);
				} catch (Exception e) {
					// 锟斤拷锟斤拷锟斤拷息
					System.out.println("------锟斤拷锟斤拷转锟斤拷锟斤拷锟斤拷-----锟斤拷锟斤拷锟斤拷===");
					String error_p = sheet_name + ":" + NumToString(col) + row;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_num", error_p);
					error_list.add(map);
					return;
				}
				// } else {
				// // 锟斤拷锟斤拷锟斤拷息
				// System.out.println("----------锟斤拷锟斤拷锟斤拷锟街诧拷锟皆ｏ拷锟斤拷--锟斤拷----");
				// String error_p = sheet_name + ":" + NumToString(col) + row;
				// HashMap<String, String> map = new HashMap<String, String>();
				// map.put("error_num", error_p);
				// error_list.add(map);
				// return;
				// }
			}
		}
	}

	/*
	 * 锟斤拷锟揭伙拷蟹锟斤拷斯锟斤拷锟较�
	 */
	private void checkNonLaborByline(JacobEReportTool tool, String sheet_name,
			int row, int starDate, int endDate, String preferenceName,
			Dispatch sheet) throws ServiceException, TCException {

		System.out.println("-------锟斤拷锟揭伙拷蟹锟斤拷斯锟斤拷锟较�!!--------");
		// 取锟矫碉拷一锟叫碉拷值,
		String cellValue = tool.getDataFromExcel("A", row, sheet);

		// 5.8锟睫革拷--锟斤拷锟絜xcel锟斤拷锟叫碉拷锟斤拷锟斤拷为0.锟斤拷锟斤拷锟斤拷一锟叫撅拷锟斤拷锟�
		String height_str = tool.getHeight("A" + row, sheet);
		if (height_str.equals("0.0")) {
			// System.out.println("-------锟斤拷锟剿癸拷锟斤拷息锟斤拷锟�!!--------");
			return;
		}

		// 什么时锟津创斤拷锟斤拷锟剿癸拷锟斤拷锟叫斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷keyOfreference为锟斤拷取一锟斤拷excel锟斤拷锟街碉拷锟斤拷锟斤拷锟窖★拷锟斤拷锟斤拷锟斤拷欠锟斤拷锟斤拷锟斤拷应锟斤拷锟斤拷锟斤拷
		String findValue = isFindValue(cellValue, session, preferenceName);

		if (findValue.equals("")) {
			return;
		}

		// 锟矫碉拷锟斤拷锟斤拷锟斤拷锟斤拷--只锟叫凤拷锟剿癸拷锟斤拷息锟斤拷锟斤拷
		// String costType = isFindValue(cellValue, session,
		// preferenceName);

		// if (costType.equals("")) {
		// // 锟斤拷锟斤拷
		// String error_p = sheet_name + ":" + "A" + row;
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("error_CostType", error_p);
		// error_list.add(map);
		// }

		// 取每锟斤拷锟铰碉拷值
		for (int col = starDate; col <= endDate; col++) {

			// 锟斤拷取每锟街凤拷锟斤拷锟斤拷锟酵碉拷值
			double dCostValue = 0;

			// 锟斤拷ExcelRW锟斤拷锟竭讹拷取excel
			String cellCost = tool.getDataFromExcel(NumToString(col), row,
					sheet);

			if (cellCost.equals("null") || cellCost.equals("")) {
				cellCost = "0.0";
			}

			if (cellCost.matches("^(-?\\d+)(\\.\\d+)?$")) {
				try {
					dCostValue = Double.parseDouble(cellCost);
					// System.out.println("-----锟斤拷锟斤拷锟斤拷----" + dCostValue);
				} catch (Exception e) {
					// 锟斤拷锟斤拷锟斤拷息
					String error_p = sheet_name + ":" + NumToString(col) + row;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_num", error_p);
					error_list.add(map);
					return;
				}
			} else {
				// 锟斤拷锟斤拷锟斤拷息
				System.out.println("---锟斤拷锟街诧拷锟斤拷---");
				String error_p = sheet_name + ":" + NumToString(col) + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_num", error_p);
				error_list.add(map);
				return;
			}
		}
	}

	/*
	 * 锟斤拷锟斤拷锟剿癸拷锟斤拷锟斤拷de指锟斤拷excel锟斤拷锟�
	 */
	private void checkValueOfNon_LaborfromExcel(String sheet_name,
			int startRow, int endRow, int starDate, int endDate,
			int datePosition, JacobEReportTool tool, Dispatch sheetsAll,
			String GEBTVersion) throws ServiceException, TCException {

		System.out.println("--------锟斤拷锟斤拷锟剿癸拷锟斤拷锟斤拷-------锟矫碉拷锟斤拷选锟斤拷姹撅拷院锟斤拷值锟斤拷---------------");
		String CostType_name = searchNameOfReference(session,
				YFJC_NonLabor_CostType + "_" + GEBTVersion);
		System.out.println("=============CostType_name============="
				+ CostType_name);

		if (CostType_name.equals("")) {
			String error_cost = YFJC_NonLabor_CostType + GEBTVersion
					+ " not exists!!!";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("error_CostType", error_cost);
			error_list.add(map);
		} else {
			nameOfPreference.put("CostType", CostType_name);
		}

		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		endposition_nonlabor = endDate;

		// 取锟斤拷锟斤拷锟斤拷
		for (int col = starDate; col <= endDate; col++) {
			// 锟斤拷锟铰的凤拷锟斤拷锟斤拷取excel
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);
			String data;

			if (!data_string.equals("")) {
				try {
					data = sdf.format(new Date(data_string));
				} catch (Exception e) {
					// 锟斤拷锟斤拷锟斤拷息
					String error_p = sheet_name + ":" + column + datePosition;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_Date", error_p);
					error_list.add(map);
					data = "1900-01-01";
				}

				if (!data
						.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
					// 锟斤拷锟斤拷锟斤拷息
					String error_p = sheet_name + ":" + column + datePosition;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_Date", error_p);
					error_list.add(map);
					data = "1900-01-01";
				}
			} else {
				endposition_nonlabor = col - 1;
				break;
			}

		}

		// System.out.println("=========6.3锟睫革拷==endposition_nonlabor==========="
		// + endposition_nonlabor);

		for (int i = startRow; i <= endRow; i++) {
			checkNonLaborByline(tool, sheet_name, i, starDate,
					endposition_nonlabor, nameOfPreference.get("CostType"),
					sheet);
		}

	}

	// 锟剿癸拷锟斤拷息
	// 锟斤拷取指锟斤拷锟斤拷一锟叫凤拷锟斤拷锟斤拷息锟斤拷值,锟斤拷锟斤拷锟斤拷锟矫斤拷去
	private void setCostInfoValuesByRow(TCComponentItemRevision revision,
			JacobEReportTool tool, int row, int startDate, int endDate,
			HashMap<String, String> month_maps,
			HashMap<String, String> year_maps, Dispatch sheet)
			throws ServiceException, TCException {

		// 锟斤拷取每一锟叫的第讹拷锟斤拷,锟斤拷锟斤拷锟叫碉拷位锟矫ｏ拷锟斤拷锟斤拷锟叫讹拷锟斤拷锟斤拷值锟斤拷tc锟斤拷锟角凤拷锟斤拷锟�
		// 1.锟斤拷锟叫讹拷锟斤拷锟斤拷锟斤拷锟斤拷知锟斤拷锟斤拷锟�
		// 2.锟斤拷锟斤拷锟斤拷锟斤拷tc锟斤拷锟角凤拷锟斤拷值
		// 3.锟斤拷锟矫伙拷校锟絩eturn锟斤拷锟斤拷
		String data_key_1 = tool.getDataFromExcel("B", row, sheet);
		String data_key_2 = tool.getDataFromExcel("C", row, sheet);

		// 5.8锟睫革拷--锟斤拷锟絜xcel锟斤拷锟叫碉拷锟斤拷锟斤拷为0.锟斤拷锟斤拷锟斤拷一锟叫撅拷锟斤拷锟�
		String height_str = tool.getHeight("B" + row, sheet);
		if (height_str.equals("0.0")) {
			return;
		}

		if (data_key_2.equals("null")) {
			return;
		}

		// YFJC_HumanLabor_Section
		String findValue_section = isFindValue(data_key_2, session,
				nameOfPreference.get("Section"));
		if (findValue_section.equals("")) {
			return;
		}

		String Group_value = null;

		if (data_key_2.equals("SMTE")) {
			// 5.6-锟睫革拷--YFJC_HumanLabor_SMTE_GROUP
			Group_value = isFindValue(data_key_2, session,
					YFJC_HumanLabor_SMTE_GROUP);
			// System.out.println("SMTE---锟斤拷应锟斤拷Group锟斤拷锟斤拷锟斤拷");
		} else {

			if (!data_key_1.equals("null")) {
				// 5.8---锟睫改憋拷锟斤拷==YFJC_HumanLabor_Group
				String findValue_group = isFindValue(data_key_1, session,
						nameOfPreference.get("Group"));
				if (!findValue_group.equals("")) {
					// 锟揭碉拷锟斤拷选锟斤拷锟接︼拷锟街�

					// 锟斤拷锟斤拷锟睫改★拷锟斤拷group
					if (findValue_group.equals("jci6_ProgramDivi")) {
						TCComponentGroup tcGroup = (TCComponentGroup) revision
								.getReferenceProperty("jci6_ProgramDivi");
						Group_value = tcGroup.getFullName();
						// System.out.println("-------------Group_Name======"
						// + Group_value);
					} else {
						Group_value = findValue_group;
					}

					map_group.put("Group", Group_value);
				} else {
					// 锟斤拷锟斤拷

				}
			} else {
				Group_value = map_group.get("Group");
			}
		}

		String year = year_maps.get(startDate + "");

		// 5.6锟睫革拷
		String index = Group_value + findValue_section;

		// 取锟矫凤拷锟斤拷锟斤拷息锟斤拷锟斤拷要锟斤拷锟斤拷锟斤拷
		TCComponent labor_CostInfo = null;
		TCComponent labor_CostInfo_hour = null;
		TCComponent labor_CostInfo_money = null;

		HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();

		// maps_CostInfo.put("锟斤拷锟斤拷", labor_CostInfo);
		// maps_CostInfo.put("小时", labor_CostInfo_hour);
		// maps_CostInfo.put("元", labor_CostInfo_money);

		// 锟矫碉拷锟斤拷锟斤拷BY学锟狡★拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷jci6_RateLevel
		// String rate_string = "";

		// 取每锟斤拷锟铰碉拷值
		for (int col = startDate; col <= endDate; col++) {

			if (TYPE_HumanLaborYuan == 0) {
				if (maps_HunmanLabor == null) {
					int YEAR = Integer.parseInt(year);
					maps_HunmanLabor = new HashMap<String, HashMap<String, TCComponent>>();

					// 锟斤拷锟斤拷锟斤拷锟斤拷锟铰★拷锟斤拷锟斤拷锟斤拷息
					labor_CostInfo = createCostInfo(session, revision, "锟斤拷锟斤拷", 2,
							Group_value, findValue_section, YEAR, null);
					labor_CostInfo_hour = createCostInfo(session, revision,
							"小时", 2, Group_value, findValue_section, YEAR, null);
					maps_CostInfo.put("锟斤拷锟斤拷", labor_CostInfo);
					maps_CostInfo.put("小时", labor_CostInfo_hour);
					maps_HunmanLabor.put(index + year, maps_CostInfo);
				}
				if (!year.equals(year_maps.get(col + ""))) {
					// 锟斤拷锟斤拷要锟斤拷锟斤拷锟铰的凤拷锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷司筒锟斤拷锟揭拷锟斤拷锟�
					year = year_maps.get(col + "");
					boolean flag = false;

					// 5.6锟睫革拷
					for (String s : maps_HunmanLabor.keySet()) {
						if ((index + year).equals(s)) {
							flag = true;
						}
					}

					if (!flag) {
						int YEAR = Integer.parseInt(year);

						// 锟斤拷锟斤拷锟斤拷锟斤拷锟铰★拷锟斤拷锟斤拷锟斤拷息
						labor_CostInfo = createCostInfo(session, revision,
								"锟斤拷锟斤拷", 2, Group_value, findValue_section, YEAR,
								null);
						labor_CostInfo_hour = createCostInfo(session, revision,
								"小时", 2, Group_value, findValue_section, YEAR,
								null);
						maps_CostInfo.put("锟斤拷锟斤拷", labor_CostInfo);
						maps_CostInfo.put("小时", labor_CostInfo_hour);
						maps_HunmanLabor.put(index + year, maps_CostInfo);

					} else {

						labor_CostInfo = maps_HunmanLabor.get(index + year)
								.get("锟斤拷锟斤拷");
						labor_CostInfo_hour = maps_HunmanLabor
								.get(index + year).get("小时");

					}
				} else {

					boolean flag = false;
					// 5.6锟睫革拷
					for (String s : maps_HunmanLabor.keySet()) {
						if ((index + year).equals(s)) {
							flag = true;
						}
					}

					if (!flag) {
						int YEAR = Integer.parseInt(year);
						// 锟斤拷锟斤拷锟斤拷锟斤拷锟铰★拷锟斤拷锟斤拷锟斤拷息
						labor_CostInfo = createCostInfo(session, revision,
								"锟斤拷锟斤拷", 2, Group_value, findValue_section, YEAR,
								null);
						labor_CostInfo_hour = createCostInfo(session, revision,
								"小时", 2, Group_value, findValue_section, YEAR,
								null);
						maps_CostInfo.put("锟斤拷锟斤拷", labor_CostInfo);
						maps_CostInfo.put("小时", labor_CostInfo_hour);
						maps_HunmanLabor.put(index + year, maps_CostInfo);

					} else {

						labor_CostInfo = maps_HunmanLabor.get(index + year)
								.get("锟斤拷锟斤拷");
						labor_CostInfo_hour = maps_HunmanLabor
								.get(index + year).get("小时");
					}
				}
			} else {
				if (maps_HunmanLabor_yuan == null) {
					int YEAR = Integer.parseInt(year);
					maps_HunmanLabor_yuan = new HashMap<String, HashMap<String, TCComponent>>();
					labor_CostInfo_money = createCostInfo(session, revision,
							"元", 2, Group_value, findValue_section, YEAR, null);
					maps_CostInfo.put("元", labor_CostInfo_money);
					maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);
				}

				if (!year.equals(year_maps.get(col + ""))) {
					// 锟斤拷锟斤拷要锟斤拷锟斤拷锟铰的凤拷锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷司筒锟斤拷锟揭拷锟斤拷锟�
					year = year_maps.get(col + "");
					boolean flag = false;

					// 5.6锟睫革拷
					for (String s : maps_HunmanLabor_yuan.keySet()) {
						if ((index + year).equals(s)) {
							flag = true;
						}
					}
					if (!flag) {// 锟斤拷锟斤拷锟斤拷元锟斤拷锟斤拷锟斤拷锟斤拷息
						int YEAR = Integer.parseInt(year);
						labor_CostInfo_money = createCostInfo(session,
								revision, "元", 2, Group_value,
								findValue_section, YEAR, null);
						maps_CostInfo.put("元", labor_CostInfo_money);
						maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);

					} else {
						labor_CostInfo_money = maps_HunmanLabor_yuan.get(
								index + year).get("元");

					}
				} else {

					boolean flag = false;
					// 5.6锟睫革拷
					for (String s : maps_HunmanLabor_yuan.keySet()) {
						if ((index + year).equals(s)) {
							flag = true;
						}
					}
					if (!flag) {// 锟斤拷锟斤拷锟斤拷元锟斤拷锟斤拷锟斤拷锟斤拷息
						int YEAR = Integer.parseInt(year);

						labor_CostInfo_money = createCostInfo(session,
								revision, "元", 2, Group_value,
								findValue_section, YEAR, null);
						maps_CostInfo.put("元", labor_CostInfo_money);
						maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);
					} else {

						labor_CostInfo_money = maps_HunmanLabor_yuan.get(
								index + year).get("元");

					}
				}
			}
			// 锟矫碉拷锟斤拷锟斤拷BY学锟狡★拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷jci6_RateLevel
			// if (TYPE_HumanLaborYuan == 0) {
			// TCComponent property = labor_CostInfo
			// .getReferenceProperty("jci6_RateLevel");
			// rate_string = property.getProperty("default_rate");
			// } else {
			// TCComponent property = labor_CostInfo_money
			// .getReferenceProperty("jci6_RateLevel");
			// rate_string = property.getProperty("default_rate");
			// }

			// 锟斤拷取每锟街凤拷锟斤拷锟斤拷锟酵碉拷值
			// 锟斤拷ExcelRW锟斤拷取excel
			String data = tool.getDataFromExcel(NumToString(col), row, sheet);

			if (data.equals("null") || data.equals("")) {
				data = "0.0";
			}

			// 锟斤拷锟斤拷锟斤拷值
			double dCostValue = 0;
			// 原锟斤拷锟斤拷锟皆碉拷值
			double val = 0;

			if (data.matches("^(-?\\d+)(\\.\\d+)?$")) {
				try {
					dCostValue = Double.parseDouble(data);
					// System.out.println("-----锟斤拷锟斤拷锟斤拷----" + dCostValue);
				} catch (Exception e) {
					// 锟斤拷锟斤拷锟斤拷息
					dCostValue = 0;
				}
			} else {
				continue;
			}
			// 锟剿癸拷锟斤拷锟矫的凤拷锟斤拷
			// Double rate = Double.parseDouble(rate_string);

			// 4.27锟斤拷锟斤拷小锟斤拷锟斤拷锟斤拷锟轿�
			// (dCostValue+"").substring(0,(dCostValue+"").indexOf(".")+3)
			String stringValue = "";

			if (month_maps.get(col + "").equals("01")) {
				// 锟叫讹拷锟角凤拷为0锟斤拷锟斤拷为0锟斤拷取5位锟斤拷锟铰ｏ拷4位小时锟斤拷3位元
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Jan");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						// stringValue = (dCostValue + "").substring(0,
						// (dCostValue + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Jan",
								cutString(stringValue, 5));

						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Jan");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_Jan",
								cutString(stringValue, 4));

					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_Jan");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_Jan",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			} else if (month_maps.get(col + "").equals("02")) {
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Feb");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Feb",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Feb");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_Feb",
								cutString(stringValue, 4));
					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_Feb");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_Feb",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue + "").substring(0,
					// (dCostValue + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			} else if (month_maps.get(col + "").equals("03")) {
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Mar");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Mar",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Mar");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_Mar",
								cutString(stringValue, 4));
					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_Mar");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_Mar",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue + "").substring(0,
					// (dCostValue + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			} else if (month_maps.get(col + "").equals("04")) {
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Apr");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Apr",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Apr");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_Apr",
								cutString(stringValue, 4));
					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_Apr");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_Apr",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue + "").substring(0,
					// (dCostValue + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			} else if (month_maps.get(col + "").equals("05")) {
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_May");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_May",
								cutString(stringValue, 5));

						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_May");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_May",
								cutString(stringValue, 4));
					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_May");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_May",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue + "").substring(0,
					// (dCostValue + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			} else if (month_maps.get(col + "").equals("06")) {
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Jun");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Jun",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Jun");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_Jun",
								cutString(stringValue, 4));
					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_Jun");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_Jun",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue + "").substring(0,
					// (dCostValue + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			} else if (month_maps.get(col + "").equals("07")) {
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Jul");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Jul",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Jul");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_Jul",
								cutString(stringValue, 4));
					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_Jul");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_Jul",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue + "").substring(0,
					// (dCostValue + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			} else if (month_maps.get(col + "").equals("08")) {
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Aug");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Aug",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Aug");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_Aug",
								cutString(stringValue, 4));
					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_Aug");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_Aug",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue + "").substring(0,
					// (dCostValue + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			} else if (month_maps.get(col + "").equals("09")) {
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Sep");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Sep",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Sep");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_Sep",
								cutString(stringValue, 4));
					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_Sep");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_Sep",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue + "").substring(0,
					// (dCostValue + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			} else if (month_maps.get(col + "").equals("10")) {
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Oct");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Oct",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Oct");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_Oct",
								cutString(stringValue, 4));
					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_Oct");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_Oct",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue + "").substring(0,
					// (dCostValue + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			} else if (month_maps.get(col + "").equals("11")) {
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Nov");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Nov",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Nov");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_Nov",
								cutString(stringValue, 4));
					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_Nov");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_Nov",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue + "").substring(0,
					// (dCostValue + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			} else if (month_maps.get(col + "").equals("12")) {
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Dec");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Dec",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Dec");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
						}
						stringValue = dCostValue * 170 + val + "" + "00";
						labor_CostInfo_hour.setProperty("jci6_Dec",
								cutString(stringValue, 4));
					} else {
						String property_value_money = labor_CostInfo_money
								.getProperty("jci6_Dec");
						if (property_value_money != null
								&& !property_value_money.equals("")) {
							val = Double.parseDouble(property_value_money);
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo_money.setProperty("jci6_Dec",
								cutString(stringValue, 3));
					}
					// stringValue = (dCostValue + "").substring(0,
					// (dCostValue + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 + "").substring(0,
					// (dCostValue * 170 + "").indexOf(".") + 3);

					// stringValue = (dCostValue * 170 * rate + "").substring(0,
					// (dCostValue * 170 * rate + "").indexOf(".") + 3);

				}
			}

		}
	}

	// 锟斤拷取锟剿癸拷锟斤拷锟斤拷指锟斤拷锟斤拷锟叫碉拷excel锟斤拷锟�
	private void setValuefromExcel(TCComponentItemRevision revision,
			String sheet_name, int startRow, int endRow, int starDate,
			int endDate, int datePosition, JacobEReportTool tool,
			Dispatch sheetsAll) throws TCException, IOException {

		System.out.println("-----锟斤拷取锟剿癸拷锟斤拷锟斤拷-------");

		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		HashMap<String, String> month_maps = new HashMap<String, String>();
		HashMap<String, String> year_maps = new HashMap<String, String>();

		// 取锟斤拷锟斤拷锟斤拷
		for (int col = starDate; col <= endDate; col++) {
			// 锟斤拷锟铰的凤拷锟斤拷锟斤拷取excel
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);
			String data;
			try {
				data = sdf.format(new Date(data_string));
			} catch (Exception e) {
				// 锟斤拷锟斤拷锟斤拷息
				String error_p = sheet_name + ":" + column + datePosition;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Date", error_p);
				error_list.add(map);
				data = "1900-01-01";
			}

			String year = data.substring(0, data.indexOf("-"));
			year_maps.put(col + "", year);
			String month = data.substring(data.indexOf("-") + 1,
					data.lastIndexOf("-"));
			month_maps.put(col + "", month);
		}

		// 取锟斤拷指锟斤拷锟斤拷锟斤拷锟街�
		for (int i = startRow; i <= endRow; i++) {
			setCostInfoValuesByRow(revision, tool, i, starDate, endDate,
					month_maps, year_maps, sheet);
		}

	}

	// 锟斤拷取锟斤拷锟剿癸拷锟斤拷锟矫碉拷excel锟斤拷锟�
	private void setValuefromNonLabel(TCComponentItemRevision revision,
			String sheet_name, int startRow, int endRow, int starDate,
			int endDate, int datePosition, JacobEReportTool tool,
			Dispatch sheetsAll) throws ServiceException, TCException {

		System.out.println("-----锟斤拷取锟斤拷锟剿癸拷锟斤拷锟斤拷-------");

		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		HashMap<String, String> month_maps_non = new HashMap<String, String>();
		HashMap<String, String> year_maps_non = new HashMap<String, String>();

		// 取锟斤拷锟斤拷锟斤拷
		for (int col = starDate; col <= endDate; col++) {
			// 锟斤拷锟铰的凤拷锟斤拷锟斤拷取excel
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);

			String data = sdf.format(new Date(data_string));

			// 锟斤拷媳锟阶硷拷锟斤拷诟锟绞�
			if (data.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
				String year = data.substring(0, data.indexOf("-"));
				year_maps_non.put(col + "", year);
				String month = data.substring(data.indexOf("-") + 1,
						data.lastIndexOf("-"));
				month_maps_non.put(col + "", month);
			}

		}

		for (int i = startRow; i <= endRow; i++) {
			readNonLabor(revision, tool, i, starDate, endDate,
					YFJC_NonLabor_CostType, month_maps_non, year_maps_non,
					sheet);
		}

	}

	// 锟斤拷锟剿癸拷锟斤拷息
	// 锟斤拷取一锟斤拷锟斤拷息锟侥凤拷锟斤拷
	private void readNonLabor(TCComponentItemRevision revision,
			JacobEReportTool tool, int row, int startDate, int endDate,
			String preferenceName, HashMap<String, String> month_maps_non,
			HashMap<String, String> year_maps_non, Dispatch sheet)
			throws ServiceException, TCException {

		// 取锟矫碉拷一锟叫碉拷值,锟斤拷ExcelRW
		String cellValue = tool.getDataFromExcel("A", row, sheet);

		// 5.8锟睫革拷--锟斤拷锟絜xcel锟斤拷锟叫碉拷锟斤拷锟斤拷为0.锟斤拷锟斤拷锟斤拷一锟叫撅拷锟斤拷锟�
		String height_str = tool.getHeight("A" + row, sheet);
		if (height_str.equals("0.0")) {
			return;
		}

		// 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟街�
		String year = year_maps_non.get(startDate + "");

		// 什么时锟津创斤拷锟斤拷锟剿癸拷锟斤拷锟叫斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷keyOfreference为锟斤拷取一锟斤拷excel锟斤拷锟街碉拷锟斤拷锟斤拷锟窖★拷锟斤拷锟斤拷锟斤拷欠锟斤拷锟斤拷锟斤拷应锟斤拷锟斤拷锟斤拷
		// String findValue = isFindValue(cellValue, session, preferenceName);

		// 锟矫碉拷锟斤拷锟斤拷锟斤拷锟斤拷--只锟叫凤拷锟剿癸拷锟斤拷息锟斤拷锟斤拷----YFJC_NonLabor_CostType
		String findValue = isFindValue(cellValue, session,
				nameOfPreference.get("CostType"));

		String costType = findValue;

		if (findValue.equals("")) {
			return;
		}

		// 锟斤拷要锟斤拷锟矫的凤拷锟斤拷锟斤拷息
		TCComponent component_costInfo;

		// 取每锟斤拷锟铰碉拷值
		for (int col = startDate; col <= endDate; col++) {

			if (!year.equals(year_maps_non.get(col + ""))) {
				// 锟斤拷锟斤拷要锟斤拷锟斤拷锟铰的凤拷锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷司筒锟斤拷锟揭拷锟斤拷锟�
				year = year_maps_non.get(col + "");
				boolean flag = false;

				for (String s : maps_CostInfo_Non.keySet()) {
					if ((findValue + year).equals(s)) {
						flag = true;
					}
				}

				if (!flag) {
					int Year = Integer.parseInt(year);
					TCComponent createCostInfo = createCostInfo(session,
							revision, "元", 1, null, null, Year, costType);
					maps_CostInfo_Non.put(findValue + year, createCostInfo);
					component_costInfo = createCostInfo;
				} else {
					component_costInfo = maps_CostInfo_Non
							.get(findValue + year);
				}
			}

			if (!findValue.equals("")) {
				if (maps_CostInfo_Non == null) {

					int Year = Integer.parseInt(year);
					maps_CostInfo_Non = new HashMap<String, TCComponent>();
					TCComponent createCostInfo = createCostInfo(session,
							revision, "元", 1, null, null, Year, costType);
					maps_CostInfo_Non.put(findValue + year, createCostInfo);
					component_costInfo = createCostInfo;

				} else {
					boolean flag = false;
					for (String s : maps_CostInfo_Non.keySet()) {
						if ((findValue + year).equals(s)) {
							flag = true;
						}
					}
					// 锟斤拷锟絟ashmap锟斤拷没锟斤拷
					if (!flag) {

						int Year = Integer.parseInt(year);
						// 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷息
						TCComponent createCostInfo = createCostInfo(session,
								revision, "元", 1, null, null, Year, costType);
						maps_CostInfo_Non.put(findValue + year, createCostInfo);
						component_costInfo = createCostInfo;
					} else {
						component_costInfo = maps_CostInfo_Non.get(findValue
								+ year);
					}
				}

				// 锟斤拷取每锟街凤拷锟斤拷锟斤拷锟酵碉拷值
				double dCostValue = 0;
				double val = 0;

				// 锟斤拷ExcelRW锟斤拷锟竭讹拷取excel
				String cellCost = tool.getDataFromExcel(NumToString(col), row,
						sheet);

				if (cellCost.equals("null") || cellCost.equals("")) {
					cellCost = "0.0";
				}

				cellCost = cellCost + "00";

				if (cellCost.matches("^(-?\\d+)(\\.\\d+)?$")) {
					try {
						dCostValue = Double.parseDouble(cellCost);
						// System.out.println("-----锟斤拷锟斤拷锟斤拷----" + dCostValue);
					} catch (Exception e) {
						// 锟斤拷写锟斤拷锟斤拷锟斤拷息
						dCostValue = -1;
					}
				} else {
					continue;
				}

				String stringValue = "";

				// 4.27锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷息锟斤拷元锟斤拷小锟斤拷锟斤拷锟�位 ((dCostValue+
				// val)+"").substring(0,((dCostValue+ val)+"").indexOf(".")+3)

				if (month_maps_non.get(col + "").equals("01")) {
					String property = component_costInfo
							.getProperty("jci6_Jan");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_Jan",
								cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("02")) {
					String property = component_costInfo
							.getProperty("jci6_Feb");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_Feb",
								cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("03")) {
					String property = component_costInfo
							.getProperty("jci6_Mar");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_Mar",
								cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("04")) {
					String property = component_costInfo
							.getProperty("jci6_Apr");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_Apr",
								cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("05")) {
					String property = component_costInfo
							.getProperty("jci6_May");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_May",
								cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("06")) {
					String property = component_costInfo
							.getProperty("jci6_Jun");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_Jun",
								cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("07")) {
					String property = component_costInfo
							.getProperty("jci6_Jul");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_Jul",
								cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("08")) {
					String property = component_costInfo
							.getProperty("jci6_Aug");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_Aug",
								cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("09")) {
					String property = component_costInfo
							.getProperty("jci6_Sep");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_Sep",
								cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("10")) {
					String property = component_costInfo
							.getProperty("jci6_Oct");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_Oct",
								cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("11")) {
					String property = component_costInfo
							.getProperty("jci6_Nov");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_Nov",
								cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("12")) {
					String property = component_costInfo
							.getProperty("jci6_Dec");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = dCostValue + val + "" + "00";
						component_costInfo.setProperty("jci6_Dec",
								cutString(stringValue, 3));
					}
				}
			}
		}

	}

	// 锟斤拷锟斤拷选锟斤拷锟斤拷去锟斤拷锟斤拷锟皆碉拷值
	private String isFindValue(String key, TCSession tcsession,
			String preferenceName) {
		String value = "";

		HashMap<String, String> hashMap = getTCPreferenceArray(tcsession,
				preferenceName);

		for (String keyValue : hashMap.keySet()) {
			// 锟揭碉拷锟斤拷应锟侥版本
			String[] strings = keyValue.split(" ");
			if (strings.length > 1) {
				boolean flag = false;
				for (int i = 0; i < strings.length; i++) {
					if (key.contains(strings[i])) {
						flag = true;
					} else {
						flag = false;
						break;
					}
				}
				// 锟斤拷同
				if (flag) {
					value = hashMap.get(keyValue);
				}

			} else {
				if (keyValue.equals(key)) {
					value = hashMap.get(keyValue);
				}

				// System.out.println("锟揭碉拷锟剿讹拷应锟斤拷选锟斤拷锟街�---锟斤拷锟街ｏ拷" + key);
			}
		}
		return value;
	}

	// 锟斤拷锟斤拷锟窖★拷锟斤拷值锟皆★拷=锟斤拷锟街革拷锟斤拷锟斤拷锟絟ashmap
	private HashMap<String, String> getTCPreferenceArray(TCSession tcsession,
			String preferenceName) {

		TCPreferenceService tcpreservice = tcsession.getPreferenceService();
		String[] preString = tcpreservice.getStringArray(
				TCPreferenceService.TC_preference_site, preferenceName);
		HashMap<String, String> map_refer = new HashMap<String, String>();

		for (int i = 0; i < preString.length; i++) {
			String[] split = preString[i].split("=");
			// GEBT锟芥本
			String ver = split[0].trim();
			// 5.8锟睫革拷--锟斤拷锟诫“UTF-8锟斤拷
			map_refer.put(ver, split[1].trim());
		}
		return map_refer;
	}

	// 锟铰斤拷锟斤拷询 "学锟斤拷锟斤拷锟�锟侥癸拷锟斤拷---锟皆讹拷锟斤拷锟窖拷锟斤拷锟�
	public TCComponent[] query(TCSession session, String query_name,
			String[] arg1, String[] arg2) {
		TCComponentContextList imancomponentcontextlist = null;
		TCComponent[] component = (TCComponent[]) null;
		try {
			TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session
					.getTypeComponent("ImanQuery");
			TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype
					.find(query_name);
			TCTextService imantextservice = session.getTextService();
			String[] queryAttribute = new String[arg1.length];
			for (int i = 0; i < arg1.length; ++i)
				queryAttribute[i] = imantextservice.getTextValue(arg1[i]);

			String[] queryValues = new String[arg2.length];
			for (int i = 0; i < arg2.length; ++i)
				queryValues[i] = arg2[i];

			imancomponentcontextlist = imancomponentquery
					.getExecuteResultsList(queryAttribute, queryValues);
			component = imancomponentcontextlist.toTCComponentArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return component;
	}

	/**
	 * colNameToNum::锟斤拷锟斤拷锟斤拷锟斤拷业锟斤拷锟斤拷锟斤拷锟�
	 * 
	 * @param String
	 *            锟斤拷锟斤拷A锟斤拷B锟饺ｏ拷
	 * @return int 锟斤拷锟斤拷锟斤拷
	 */
	public int colNameToNum(String colName) {
		int result = 0;
		for (int i = 0; i < colName.length(); i++) {
			result = result * 26 + colName.charAt(i) - 65 + 1;
		}
		return result;
	}

	// 锟斤拷取锟斤拷应锟斤拷锟斤拷选锟筋，锟矫碉拷excel锟斤拷位锟矫碉拷值
	private boolean getValueOfExcelByPreference(TCSession tcsession,
			String preferenceName, String lov_values) {
		// lov_values值为锟斤拷V1锟斤拷锟斤拷锟斤拷V2锟斤拷

		String index = null;

		// 锟斤拷取锟斤拷锟斤拷锟剿癸拷锟酵凤拷锟剿癸拷锟斤拷锟斤拷选锟斤拷
		TCPreferenceService tcpreservice = tcsession.getPreferenceService();

		String[] preString = tcpreservice.getStringArray(
				TCPreferenceService.TC_preference_site, preferenceName);

		HashMap<String, String> map = new HashMap<String, String>();

		for (int i = 0; i < preString.length; i++) {
			String[] split = preString[i].split("=");
			// GEBT锟芥本
			String ver = split[0].trim();
			String[] split2 = ver.split("_");
			if (split2.length == 2) {
				// 锟揭碉拷锟斤拷应锟侥版本
				if (split2[1].equals(lov_values)) {
					index = ver;
					map.put(ver, split[1].trim());
					System.out.println("---锟揭碉拷锟斤拷应锟侥版本----" + ver);
				}
			}
		}

		// 锟斤拷锟矫伙拷业锟斤拷锟斤拷头沤锟絜rror_list锟斤拷锟斤拷
		if (index == null) {
			// combo_version.setEnabled(true);

			// 锟斤拷锟斤拷锟斤拷息
			String error_msg = "Not found related version from "
					+ preferenceName;
			HashMap<String, String> map_msg = new HashMap<String, String>();
			map_msg.put("error_msg", error_msg);
			error_list.add(map_msg);
			return false;

		} else {

			// 锟斤拷取锟斤拷应锟斤拷值锟斤拷锟揭碉拷excel锟叫碉拷cell
			String sheet = map.get(index).substring(0,
					map.get(index).indexOf("{"));
			// 页锟斤拷锟斤拷锟斤拷
			maps_Prefer_values.put("Sheet", sheet.trim());

			String row = map.get(index).substring(
					map.get(index).indexOf("}") + 1,
					map.get(index).indexOf("||"));
			String[] split_row = row.split("-");
			// 锟斤拷始锟叫猴拷锟斤拷止锟斤拷
			maps_Prefer_values.put("StartRow", split_row[0]);
			maps_Prefer_values.put("EndRow", split_row[1]);

			// 锟斤拷锟节碉拷锟叫碉拷位锟斤拷
			String pos = map.get(index).substring(
					map.get(index).lastIndexOf("|") + 1,
					map.get(index).indexOf("("));
			maps_Prefer_values.put("Position", pos);

			// 锟矫碉拷锟斤拷锟节碉拷锟斤拷锟斤拷锟斤拷
			String Date = map.get(index).substring(
					map.get(index).indexOf("(") + 1,
					map.get(index).indexOf(")"));
			String[] split_date = Date.split("-");

			maps_Prefer_values
					.put("StartCol", colNameToNum(split_date[0]) + "");
			maps_Prefer_values.put("EndCol", colNameToNum(split_date[1]) + "");
			return true;
		}

	}

	/**
	 * 去锟斤拷小锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
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

	// 锟斤拷锟斤拷转锟斤拷英锟斤拷锟斤拷母,锟斤拷取excel锟斤拷锟叫碉拷锟叫碉拷锟斤拷值转锟斤拷英锟斤拷锟斤拷母
	private String NumToString(int Num) {
		String str = "";
		String strReturn = "";
		if (Num == 0)
			return "";

		float f = (Num % 26 == 0) ? 26 : Num % 26;
		switch ((int) f) {
		case 1:
			str = "A";
			break;
		case 2:
			str = "B";
			break;
		case 3:
			str = "C";
			break;
		case 4:
			str = "D";
			break;
		case 5:
			str = "E";
			break;
		case 6:
			str = "F";
			break;
		case 7:
			str = "G";
			break;
		case 8:
			str = "H";
			break;
		case 9:
			str = "I";
			break;
		case 10:
			str = "J";
			break;
		case 11:
			str = "K";
			break;
		case 12:
			str = "L";
			break;
		case 13:
			str = "M";
			break;
		case 14:
			str = "N";
			break;
		case 15:
			str = "O";
			break;
		case 16:
			str = "P";
			break;
		case 17:
			str = "Q";
			break;
		case 18:
			str = "R";
			break;
		case 19:
			str = "S";
			break;
		case 20:
			str = "T";
			break;
		case 21:
			str = "U";
			break;
		case 22:
			str = "V";
			break;
		case 23:
			str = "W";
			break;
		case 24:
			str = "X";
			break;
		case 25:
			str = "Y";
			break;
		case 26:
			str = "Z";
			break;
		}

		if (Num >= 26) {
			str = NumToString(((int) Num / 26) - ((f == 26) ? 1 : 0)) + str;
		}

		strReturn = str;
		return strReturn;
	}

	// 去锟斤拷锟铰凤拷前锟侥★拷0锟斤拷
	private String changeDate(String str) {
		if (!str.equals("")) {
			String month = str.substring(str.indexOf("-") + 1,
					str.lastIndexOf("-"));
			if (month.charAt(0) == '0') {
				char charAt = month.charAt(1);
				String s = charAt + "";
				return s;
			} else
				return str;
		} else
			return "";
	}

	// 锟斤拷取i位小锟斤拷
	private String cutString(String str, int i) {
		String stringValue = "";
		if (str.contains(".")) {
			stringValue = (str + "0000").substring(0,
					(str + "0000").indexOf(".") + (i + 1));
		} else {
			stringValue = str + ".00";
		}
		return stringValue;
	}

	// 锟矫碉拷TC锟叫碉拷锟斤拷锟斤拷锟斤拷锟斤拷系统锟皆讹拷锟戒化锟斤拷锟斤拷英锟斤拷
	private String getNameOfTC(TCComponentItemRevision revision, String name) {
		String displayName = "";
		try {
			TCComponentItemRevisionType tcComponentItemRevisionType = (TCComponentItemRevisionType) revision
					.getTypeComponent();

			TCPropertyDescriptor activeStatusTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor(name);
			displayName = activeStatusTCProperty.getDisplayName();

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return displayName;
	}

	// 锟矫碉拷系统锟斤拷时锟斤拷
	private String getSystemTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		Date currentTime = new Date();
		String dateString = format.format(currentTime);
		return dateString;
	}

	// 6.5---锟睫革拷=锟斤拷弄锟斤拷锟斤拷锟斤拷---锟斤拷锟斤拷锟斤拷选锟斤拷锟斤拷
	private String searchNameOfReference(TCSession tcsession,
			String pereferenceName) {
		TCPreferenceService tcpreservice = tcsession.getPreferenceService();
		String[] preString = tcpreservice.getStringArray(
				TCPreferenceService.TC_preference_site, pereferenceName);
		String name = "";
		if (preString == null || preString.length == 0) {
			name = pereferenceName.substring(0,
					pereferenceName.lastIndexOf("_"));

			String[] preString2 = tcpreservice.getStringArray(
					TCPreferenceService.TC_preference_site, name);
			if (preString2 == null || preString2.length == 0) {
				return "";
			} else {
				return name;
			}
		} else {
			name = pereferenceName;
		}
		return name;
	}
}
