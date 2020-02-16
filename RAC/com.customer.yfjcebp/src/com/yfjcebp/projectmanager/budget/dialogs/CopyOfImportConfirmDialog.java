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
 #	2013-3-11	liuc  		Ini		ï¿½ï¿½ï¿½Çµï¿½ï¿½ï¿½ï¿½SWTï¿½Ô»ï¿½ï¿½ï¿½						   
 #=============================================================================
 */
package com.yfjcebp.projectmanager.budget.dialogs;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

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
import com.teamcenter.rac.kernel.TCDateFormat;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCAttachmentScope;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentReleaseStatusType;
import com.teamcenter.rac.kernel.TCComponentTask;
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

public class CopyOfImportConfirmDialog extends AbstractAIFDialog {

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
	private SimpleDateFormat sdf_show = new SimpleDateFormat(
			reg.getString("TimeFormat"));

	// ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½ï¿½
	// 4.18---ï¿½Þ¸Ä·ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½î£¬ï¿½ï¿½Îªï¿½ï¿½ï¿½ï¿½ï¿½ÖµÎªCostTypeï¿½ï¿½Öµï¿½ï¿½ï¿½ï¿½Í¬ï¿½Í¸ï¿½ï¿½Â¶ï¿½ï¿½ï¿½ï¿½Â½ï¿½
	private static String YFJC_NonLabor_CostType = "YFJC_NonLabor_CostType";
	private static String YFJC_NonLabor_Position = "YFJC_NonLabor_Position";
	private static String YFJC_HumanLabor_Position = "YFJC_HumanLabor_Position";
	private static String YFJC_HumanLabor_Group = "YFJC_HumanLabor_Group";
	// 5.4--ï¿½Þ¸ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?--YFJC_HumanLabor_RateLevel
	private static String YFJC_HumanLabor_Section = "YFJC_HumanLabor_RateLevel";
	// 5.6--ï¿½Þ¸ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ò»ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½---ï¿½Òµï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½Ãµï¿½SMTEï¿½ï¿½ï¿½ï¿½
	private static String YFJC_HumanLabor_SMTE_GROUP = "YFJC_HumanLabor_SMTE_GROUP";
	private String YFJC_LaborCostYuan_Position = "YFJC_LabourCost_Position";
	// excelï¿½Ä¼ï¿½ï¿½ï¿½
	private static String ExcelNameMode = "GEBT.xlsm";

	// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½costInfoï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
	private HashMap<String, TCComponent> maps_CostInfo_Non;
	// 5.6ï¿½Þ¸ï¿½----ï¿½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½costInfoï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
	HashMap<String, HashMap<String, TCComponent>> maps_HunmanLabor;
	HashMap<String, HashMap<String, TCComponent>> maps_HunmanLabor_yuan;
	// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
	private HashMap<String, String> maps_Prefer_values = new HashMap<String, String>();

	// ï¿½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½Ãµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï?
	private HashMap<String, String> map_group = new HashMap<String, String>();

	// 4.1ï¿½ï¿½ï¿½ï¿½
	// ï¿½ï¿½ï¿½ï¿½errorï¿½Ä¶Ô»ï¿½ï¿½ï¿½ï¿½ï¿½Òªï¿½ï¿½Öµ
	private ArrayList<HashMap<String, String>> error_list = null;

	// 4.10ï¿½ï¿½ï¿½ï¿½
	private String ProjectID;

	// JacobEReportTool tool = new JacobEReportTool();

	// 6.5ï¿½ï¿½ï¿½ï¿½----ï¿½æ´¢Group,Ñ§ï¿½Æ£ï¿½ï¿½Ô¼ï¿½CostType
	private HashMap<String, String> nameOfPreference = new HashMap<String, String>();

	// 5.31ï¿½Þ¸ï¿½----ï¿½ï¿½ï¿½ï¿½Îªï¿½Õ¾Í²ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ë¼ï¿½ï¿½ï¿½Ö´ï¿½ï¿½
	int endposition_labor = 0;
	int endposition_nonlabor = 0;
	private TCComponent projectComponent;
	// 7.2ï¿½Þ¸ï¿½--ï¿½ï¿½ï¿½ï¿½ ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½Ä¶ï¿½È¡---Ôªï¿½ï¿½ï¿½Ù¼ï¿½ï¿½ï¿½ï¿½ï¿½Ç¶ï¿½È¡Ö¸ï¿½ï¿½Ò³ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
	int TYPE_HumanLaborYuan = 0;

	public CopyOfImportConfirmDialog(final Display display,
			final TCComponentItemRevision revision,
			final HashMap<String, String> maps, final TCSession session,
			final int isF, final TCComponentDataset dataset,
			TCComponent projectComponent) {

		obj[0] = "open bypass";// ï¿½ï¿½Â·ï¿½ï¿½ï¿½ï¿½Òªï¿½Ä±ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ö?

		shell = new Shell(display);
		shell.setSize(500, 465);
		shell.setText(reg.getString("dialog_Confirm"));
		this.session = session;
		this.isF = isF;
		this.dataset = dataset;
		this.revision = revision;
		this.projectComponent = projectComponent;
		// ×¢ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½,SWT.NULTIï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½ï¿½ï¿?SWT.FULL_SELECTIONï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½
		// ,SWT.BORDERï¿½ß¿ï¿½SWT.V_SCROLL
		// ,SWT.H_SCROLLï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
		TableViewer tableViewer = new TableViewer(shell, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(30, 20, 420, 330);

		// ï¿½ï¿½ï¿½ï¿½button
		btn_ok = new Button(shell, SWT.NONE);
		btn_ok.setBounds(90, 360, 100, 30);
		btn_ok.setText(reg.getString("btn_import"));

		btn_cancel = new Button(shell, SWT.NONE);
		btn_cancel.setBounds(300, 360, 100, 30);
		btn_cancel.setText(reg.getString("btn_cancel"));

		// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
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
		// TableItem item7 = new TableItem(table, SWT.CENTER);
		// TableItem item8 = new TableItem(table, SWT.CENTER);
		// TableItem item9 = new TableItem(table, SWT.CENTER);

		// ï¿½ï¿½ï¿½Ãµï¿½Ò»ï¿½Ðµï¿½Öµ
		item1.setText(0, getNameOfTC(revision, "jci6_PDxSeq"));
		item2.setText(0, getNameOfTC(revision, "jci6_GEBTTemplate"));
		item3.setText(0, getNameOfTC(revision, "jci6_EQU"));
		item4.setText(0, getNameOfTC(revision, "jci6_SignedMoney"));
		item5.setText(0, getNameOfTC(revision, "jci6_PDxSignDate"));
		item6.setText(0, getNameOfTC(revision, "jci6_Remark"));
		// item7.setText(0, getNameOfTC(revision, "jci6_SignedMoney"));
		// item8.setText(0, getNameOfTC(revision, "jci6_PDxSignDate"));
		// item7.setText(0, getNameOfTC(revision, "jci6_Remark"));

		try {
			// ï¿½ï¿½ï¿½Ã´ï¿½ï¿½Ý¹ï¿½ï¿½ï¿½ï¿½ï¿½Öµï¿½ï¿½Ò»ï¿½ï¿½Ð´ï¿½ï¿½Ö¸ï¿½ï¿½ï¿½ï¿½Ôªï¿½ï¿½
			// Ô­Öµ
			String jci6_PDxSeq = revision.getProperty("jci6_PDxSeq");
			String jci6_GEBTTemplate = revision
					.getProperty("jci6_GEBTTemplate");
			String jci6_EQU = revision.getProperty("jci6_EQU");
			// String jci6_EQUSignDate =
			// revision.getProperty("jci6_EQUSignDate");
			// String jci6_CalcDate = revision.getProperty("jci6_CalcDate");
			// String jci6_Responsibility = revision
			// .getProperty("jci6_Responsibility");
			String jci6_PDxSignDate = revision.getProperty("jci6_PDxSignDate");

			item1.setText(1, jci6_PDxSeq);
			item2.setText(1, jci6_GEBTTemplate);

			// ï¿½ï¿½Ê¾È¥ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ä¡ï¿?ï¿½ï¿½
			if (!jci6_EQU.equals("")) {
				item3.setText(1, cutString(jci6_EQU, 3));
			} else {
				item3.setText(1, jci6_EQU);
			}

			/*
			 * if (!jci6_EQUSignDate.equals("")) { item4.setText( 1,
			 * jci6_EQUSignDate.substring(0, jci6_EQUSignDate.indexOf(" "))); }
			 * else { item4.setText(1, jci6_EQUSignDate); }
			 */

			// if (!jci6_CalcDate.equals("")) {
			// item5.setText(1,
			// jci6_CalcDate.substring(0, jci6_CalcDate.indexOf(" ")));
			// } else {
			// item5.setText(1, jci6_CalcDate);
			// }

			// item6.setText(1, jci6_Responsibility);
			item4.setText(1, revision.getProperty("jci6_SignedMoney"));

			if (!jci6_PDxSignDate.equals("")) {
				item5.setText(
						1,
						jci6_PDxSignDate.substring(0,
								jci6_PDxSignDate.indexOf(" ")));
			} else {
				item5.setText(1, jci6_PDxSignDate);
			}

			item6.setText(1, revision.getProperty("jci6_Remark"));

			if (maps != null) {
				item1.setText(2, maps.get("PDx_Squence"));
				item2.setText(2, maps.get("GEBT_Version"));
				item3.setText(2, maps.get("EQU"));
				item4.setText(2, maps.get("approved_amount"));
				item5.setText(2, maps.get("PDx_Sign_Date"));
				item6.setText(2, maps.get("remark"));
				// item7.setText(2, maps.get("remark"));
				// item8.setText(2, maps.get("PDx_Sign_Date"));
				// item9.setText(2, maps.get("remark"));
			}

			shell.open();

			table.addListener(SWT.MeasureItem, new Listener() {
				@Override
				public void handleEvent(Event event) {
					event.height = 30;
				}
			});

			btn_cancel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {

					// ï¿½Ø±Õµï¿½Ç°ï¿½ï¿½ï¿½ï¿½
					shell.dispose();
				}
			});

			btn_ok.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					/**
					 * 2014-9-25 add by wuh  Ôö¼ÓCOCµÄÏà¹Ø²Ù×÷
					 */
					COCOperation coc  = new COCOperation(session,revision,maps.get("GEBT_Version"));
					try {
						coc.executeOperation();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					/**
					 * 2014-9-25 add by wuh  Ôö¼ÓCOCµÄÏà¹Ø²Ù×÷
					 */
					
					JacobEReportTool tool = new JacobEReportTool();
					int isClose = 0;
					try {
						// ï¿½ï¿½ï¿½æ±¾ï¿½Ñ¾ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ö±ï¿½ï¿½ï¿½Þ¸Ä£ï¿½Òªï¿½ï¿½ï¿½ï¿½Â·
						// if
						// (revision.getProperty("release_status_list").equals(
						// reg.getString("TCM_release"))) {
						//
						// // ï¿½ï¿½ï¿½ï¿½Ô¤ï¿½ï¿½Ëµï¿½ï¿½ï¿½Ö´ï¿½ï¿?TBL
						// if (isF == 2) {
						// try {
						// // ï¿½Ãµï¿½ÏµÍ³ï¿½ï¿½ï¿½ï¿½Ê±ï¿½Ä¼ï¿½
						// String getenv = System.getenv("TEMP");
						// String TC_path = System.getenv("TPR");
						// tool.addDir(TC_path + "\\plugins");
						// // ï¿½Ãµï¿½ï¿½ï¿½ï¿½Ðµï¿½sheet
						// Dispatch sheetsAll = tool.getSheets(getenv
						// + File.separator + ExcelNameMode);
						//
						// // ï¿½È¼ï¿½ï¿½Ò»ï¿½ï¿½Òªï¿½ï¿½È¡ï¿½ï¿½Ö?
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
						// // ï¿½ï¿½É¾ï¿½ï¿½æ±¾ï¿½ÂµÄ¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï§n¿½ï¿½ï¿½ï¿?
						// deleteCostInfo(revision);
						// // ï¿½ï¿½ï¿½Â½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
						// // ï¿½ï¿½È¡excelï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ý£ï¿½ï¿½ï¿½Ð´ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï§n¿½ï¿?
						//
						// // ï¿½Ãµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½Ö?--YFJC_HumanLabor_Position
						// getValueOfExcelByPreference(session,
						// YFJC_HumanLabor_Position,
						// maps.get("GEBT_Version"));
						// // ï¿½Ù´ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢
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
						// // ï¿½Ãµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½Ö?--YFJC_NonLabor_Position
						// getValueOfExcelByPreference(session,
						// YFJC_NonLabor_Position,
						// maps.get("GEBT_Version"));
						//
						// System.out
						// .println("-----------ï¿½ï¿½Ê¼ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½Ä¿ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½--------");
						// // ï¿½Ù´ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢
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
						// .println("------------------------------É¾ï¿½ï¿½ï¿½ï¿½Öµï¿½Ä·ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½------------------------");
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
						// Ö´ï¿½ï¿½TBLï¿½ï¿½ï¿½ï¿½
						if (isF == 2) {
							try {
								// ï¿½ï¿½ï¿½Â½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
								// ï¿½ï¿½È¡excelï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ý£ï¿½ï¿½ï¿½Ð´ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï§n¿½ï¿?
								// ï¿½Ãµï¿½ÏµÍ³ï¿½ï¿½ï¿½ï¿½Ê±ï¿½Ä¼ï¿½
								String getenv = System.getenv("TEMP");
								String TC_path = System.getenv("TPR");
								tool.addDir(TC_path + "\\plugins");
								// ï¿½Ãµï¿½ï¿½ï¿½ï¿½Ðµï¿½sheet
								Dispatch sheetsAll = tool.getSheets(getenv
										+ File.separator + ExcelNameMode);

								// ï¿½È¼ï¿½ï¿½Ò»ï¿½ï¿½Òªï¿½ï¿½È¡ï¿½ï¿½Ö?
								checkExcel(maps, tool, sheetsAll, revision);

								if (error_list.size() != 0) {

									isClose = 1;
									// ï¿½Ø±ï¿½EXCELï¿½ï¿½ï¿?
									tool.closeExcelFile(false);

									display.dispose();
									Excel_ErrorDialog dialog_error = new Excel_ErrorDialog(
											error_list);
									return;
								}

								// É¾ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï§n¿½ï¿½ï¿½ï¿?
								deleteCostInfo(revision);

								// ï¿½Ãµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½Ö?--YFJC_HumanLabor_Position
								getValueOfExcelByPreference(session,
										YFJC_HumanLabor_Position,
										maps.get("GEBT_Version"));

								TYPE_HumanLaborYuan = 0;

								// ï¿½Ù´ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢
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

								// ï¿½Ãµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½Ö?--YFJC_HumanLabor_Position
								getValueOfExcelByPreference(session,
										YFJC_LaborCostYuan_Position,
										maps.get("GEBT_Version"));

								TYPE_HumanLaborYuan = 1;

								// 7.2ï¿½Þ¸ï¿½ ï¿½Ù´ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢---yuan
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

								// ï¿½Ãµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½Ö?
								getValueOfExcelByPreference(session,
										YFJC_NonLabor_Position,
										maps.get("GEBT_Version"));
								// ï¿½Ù´ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢
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
										.println("------------------------------É¾ï¿½ï¿½ï¿½ï¿½Öµï¿½Ä·ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½------------------------");

								// System.out.println("Before======delete no value CostInfo=====");

								// ShowCostInfos(revision);

								// 5.27----É¾ï¿½ï¿½ï¿½ï¿½Öµï¿½Ã·ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½
								deleteCostInfo_noValue(revision);

								// System.out
								// .println("After======delete no value CostInfo=====");
								// ShowCostInfos(revision);

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

						// ï¿½Þ¸ï¿½Ö¸ï¿½ï¿½ï¿½æ±¾ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
						setDataOfRev(revision, maps);
						
						//add by wuwei
						TCComponentItem myProgramItem = revision.getItem();
						String itemPropertArray[]={"jci6_EQU","jci6_PDxSeq","jci6_Remark" };
						String values[]={maps.get("EQU"),maps.get("PDx_Squence"),maps.get("remark")};
						System.out.println("ÉèÖÃProgramINfoÖµ---jci6_EQU: "+maps.get("EQU")+"  jci6_PDxSeq:"+maps.get("PDx_Squence")+"  jci6_Remark:"+maps.get("remark"));
						
						openByPass();
						myProgramItem.lock();
						myProgramItem.setProperties(itemPropertArray, values);
						myProgramItem.save();
						closeByPass();
						

						if (isDatasetRelease(dataset)) {
							// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
							releaseRev(revision);
						}
						// }

						System.out
								.println("=================É¾ï¿½ï¿½æ±¾ï¿½Âµï¿½ï¿½ï¿½ï¿½Ðµï¿½dataset===========");
						int isFindDataset = 0;
						TCComponent[] relatedComponents = revision
								.getRelatedComponents("IMAN_specification");
						if (relatedComponents.length > 0) {
							for (int i = 0; i < relatedComponents.length; i++) {
								if (relatedComponents[i] instanceof TCComponentDataset) {
									if (dataset == relatedComponents[i]) {
										isFindDataset = 1;
										break;
									}
								}
							}
						}

						System.out
								.println("=================ï¿½ï¿½datasetï¿½ï¿½Óµï¿½ï¿½æ±¾ï¿½Â°ï¿½dataset===========");
						if (isFindDataset == 0) {
							// //openByPass();
							revision.add("IMAN_specification", dataset);
							// closeByPass();
						}
						// ï¿½ï¿½ï¿½GEBTï¿½Ä¼ï¿½ï¿½Ç·ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ð£ï¿½ï¿½Í°ï¿½ï¿½ï¿½ï¿½ï¿½æ±¾Ò²ï¿½ï¿½Óµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
						TCComponentTask rootTask = null;

						// 5.17ï¿½Þ¸ï¿½---datasetï¿½ï¿½Ý¼ï¿½ï¿½Ç·ï¿½ï¿½ï¿?
						if (isDatasetINProcesssList(dataset)) {
							if (dataset.getCurrentJob() != null) {
								// //openByPass();
								rootTask = dataset.getCurrentJob()
										.getRootTask();
								// closeByPass();

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
								// //openByPass();
								rootTask.addAttachments(
										TCAttachmentScope.GLOBAL,
										new TCComponent[] { revision },
										new int[] { 1 });
								// closeByPass();
							}

						}

					} catch (TCException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} finally {
						System.out.println("ï¿½Ø±ï¿½ï¿½ï¿½ï¿½Ð¶Ô»ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?!!");
						// ï¿½Ø±ï¿½ï¿½ï¿½ï¿½Ð¶Ô»ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?
						display.dispose();
					}
				}
			});

		} catch (TCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// ï¿½Þ¸Äµï¿½Ô­ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
	private void setDataOfRev(TCComponentItemRevision revision,
			HashMap<String, String> maps) throws TCException {

		try {
			revision.lock();

			System.out.println("----setDataOfRev!!!-----");

			System.out.println("======Start revise data of Revision=======");

			String value = maps.get("PDx_Sign_Date");

			System.out.println("PDx_Sign_Date=============" + value);

			if (!value.equals("")
					&& value.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])")) {

				String as[] = { "jci6_Remark", "jci6_EQU", "jci6_SignedMoney",
						"jci6_GEBTTemplate", "jci6_PDxSeq", "item_revision_id",
						"jci6_PDxSignDate" };

				TCProperty[] tcProperties = revision.getTCProperties(as);
				Vector vector = new Vector();

				tcProperties[0].setStringValueData(maps.get("remark"));
				vector.add(tcProperties[0]);
				System.out.println("remark=============" + maps.get("remark"));

				System.out.println("------jci6_EQU****-------START=========");
				// openByPass();Double.parseDouble(maps.get("EQU")) != 0
				if (maps.get("EQU") != null
						&& Double.parseDouble(maps.get("EQU")) != 0) {
					System.out.println("------jci6_EQU--------"
							+ maps.get("EQU"));
					tcProperties[1].setDoubleValueData(Double.parseDouble(maps
							.get("EQU")));
					vector.add(tcProperties[1]);
					System.out
							.println("------jci6_EQU--------Finished!!!==========="
									+ maps.get("EQU"));
					// closeByPass();
				} else {
					tcProperties[1].setDoubleValueData(0.00);
					vector.add(tcProperties[1]);
					System.out
							.println("------jci6_EQU==0------Finished!!!==========="
									+ maps.get("EQU"));
				}

				System.out
						.println("------approved_amount****-------START=========");

				if (maps.get("approved_amount") != null
						&& Double.parseDouble(maps.get("approved_amount")) != 0) {
					System.out.println("------jci6_SignedMoney---------"
							+ maps.get("approved_amount"));
					tcProperties[2].setDoubleValueData(Double.parseDouble(maps
							.get("approved_amount")));
					vector.add(tcProperties[2]);

					System.out
							.println("------jci6_SignedMoney--------Finished!!!==========="
									+ maps.get("approved_amount"));
					// closeByPass();
				} else {
					tcProperties[2].setDoubleValueData(0);
					vector.add(tcProperties[2]);
					System.out
							.println("------jci6_SignedMoney==0------Finished!!!==========="
									+ maps.get("approved_amount"));
				}

				// System.out.println("========start upset jci6_EQUSignDate:======"
				// + maps.get("EQU_Signdate"));
				// setDataByPass(tcProperties[3], "jci6_EQUSignDate",
				// maps.get("EQU_Signdate"));
				// System.out.println("========set EQUSignDate successfully !!!=====");

				// System.out.println("========start upset jci6_PDxSignDate:======"
				// + maps.get("PDx_Sign_Date"));
				// setDataByPass(tcProperties[3], "jci6_PDxSignDate",
				// maps.get("PDx_Sign_Date"));
				// System.out.println("========set  jci6_PDxSignDate  successfully !!!=====");

				// System.out.println("========start upset jci6_PDxSignDate:======"
				// + maps.get("PDx_Sign_Date"));
				// setDataByPass(tcProperties[5], "jci6_PDxSignDate",
				// maps.get("PDx_Sign_Date"));

				System.out.println("------Begin set jci6_GEBTTemplate----");
				tcProperties[3].setStringValueData(maps.get("GEBT_Version"));
				vector.add(tcProperties[3]);
				System.out
						.println("----- set jci6_GEBTTemplate  successfully !!! ----");

				tcProperties[4].setStringValueData(maps.get("PDx_Squence"));
				vector.add(tcProperties[4]);
				System.out
						.println("----- set jci6_PDxSeq  successfully !!! ----"
								+ maps.get("PDx_Squence"));

				// openByPass();
				tcProperties[5].setStringValueData(maps.get("PDx_Squence"));
				// closeByPass();
				vector.add(tcProperties[5]);
				System.out.println("After revise::PDx_Squence============="
						+ maps.get("PDx_Squence"));

				vector.add(tcProperties[6]);

				System.out.println("jci6_PDxSignDate==========" + value);

				TCDateFormat format_temp = new TCDateFormat(session);
				SimpleDateFormat sdf_Temp = format_temp.askDefaultDateFormat();
				System.out.println("------set data begining !!!-----");
				tcProperties[6].setDateValueData((sdf_Temp
						.parse(changeDateString(value) + " 00:00")));

				System.out
						.println("------jci6_PDxSignDate--set  Finished !!!--");

				int i = vector.size();
				if (i > 0) {
					TCProperty atcproperty2[] = new TCProperty[i];
					for (int j = 0; j < i; j++)
						atcproperty2[j] = (TCProperty) vector.elementAt(j);

					openByPass();
					revision.setTCProperties(atcproperty2);
					closeByPass();

				}

				// revision.refresh();

				System.out.println("========set data end !!!=========");

			} else {

				String as[] = { "jci6_Remark", "jci6_EQU", "jci6_SignedMoney",
						"jci6_GEBTTemplate", "jci6_PDxSeq", "item_revision_id" };

				TCProperty[] tcProperties = revision.getTCProperties(as);
				Vector vector = new Vector();

				tcProperties[0].setStringValueData(maps.get("remark"));
				vector.add(tcProperties[0]);
				System.out.println("remark=============" + maps.get("remark"));

				System.out.println("------jci6_EQU****-------START=========");
				// openByPass();Double.parseDouble(maps.get("EQU")) != 0
				if (maps.get("EQU") != null
						&& Double.parseDouble(maps.get("EQU")) != 0) {
					System.out.println("------jci6_EQU--------"
							+ maps.get("EQU"));
					tcProperties[1].setDoubleValueData(Double.parseDouble(maps
							.get("EQU")));
					vector.add(tcProperties[1]);
					System.out
							.println("------jci6_EQU--------Finished!!!==========="
									+ maps.get("EQU"));
					// closeByPass();
				} else {
					tcProperties[1].setDoubleValueData(0.00);
					vector.add(tcProperties[1]);
					System.out
							.println("------jci6_EQU==0------Finished!!!==========="
									+ maps.get("EQU"));
				}

				System.out
						.println("------approved_amount****-------START=========");

				if (maps.get("approved_amount") != null
						&& Double.parseDouble(maps.get("approved_amount")) != 0) {
					System.out.println("------jci6_SignedMoney---------"
							+ maps.get("approved_amount"));
					tcProperties[2].setDoubleValueData(Double.parseDouble(maps
							.get("approved_amount")));
					vector.add(tcProperties[2]);

					System.out
							.println("------jci6_SignedMoney--------Finished!!!==========="
									+ maps.get("approved_amount"));
					// closeByPass();
				} else {
					tcProperties[2].setDoubleValueData(0);
					vector.add(tcProperties[2]);
					System.out
							.println("------jci6_SignedMoney==0------Finished!!!==========="
									+ maps.get("approved_amount"));
				}

				// System.out.println("========start upset jci6_EQUSignDate:======"
				// + maps.get("EQU_Signdate"));
				// setDataByPass(tcProperties[3], "jci6_EQUSignDate",
				// maps.get("EQU_Signdate"));
				// System.out.println("========set EQUSignDate successfully !!!=====");

				// System.out.println("========start upset jci6_PDxSignDate:======"
				// + maps.get("PDx_Sign_Date"));
				// setDataByPass(tcProperties[3], "jci6_PDxSignDate",
				// maps.get("PDx_Sign_Date"));
				// System.out.println("========set  jci6_PDxSignDate  successfully !!!=====");

				// System.out.println("========start upset jci6_PDxSignDate:======"
				// + maps.get("PDx_Sign_Date"));
				// setDataByPass(tcProperties[5], "jci6_PDxSignDate",
				// maps.get("PDx_Sign_Date"));

				System.out.println("------Begin set jci6_GEBTTemplate----");
				tcProperties[3].setStringValueData(maps.get("GEBT_Version"));
				vector.add(tcProperties[3]);
				System.out
						.println("----- set jci6_GEBTTemplate  successfully !!! ----");

				tcProperties[4].setStringValueData(maps.get("PDx_Squence"));
				vector.add(tcProperties[4]);
				System.out
						.println("----- set jci6_PDxSeq  successfully !!! ----"
								+ maps.get("PDx_Squence"));

				// openByPass();
				tcProperties[5].setStringValueData(maps.get("PDx_Squence"));
				// closeByPass();
				vector.add(tcProperties[5]);
				System.out.println("After revise::PDx_Squence============="
						+ maps.get("PDx_Squence"));

				int i = vector.size();
				if (i > 0) {
					TCProperty atcproperty2[] = new TCProperty[i];
					for (int j = 0; j < i; j++)
						atcproperty2[j] = (TCProperty) vector.elementAt(j);

					openByPass();
					revision.setTCProperties(atcproperty2);
					closeByPass();

				}

				// revision.refresh();

			}

			revision.save();
			revision.unlock();

			/*
			 * else {
			 * 
			 * System.out.println("-------ï¿½æ±¾ï¿½ï¿½ï¿½ï¿½Ã»ï¿½Ð·ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Þ¸ï¿½-----");
			 * 
			 * // comboï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ãµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
			 * revision.setStringProperty("jci6_PDxSeq",
			 * maps.get("PDx_Squence"));
			 * revision.setStringProperty("jci6_GEBTTemplate",
			 * maps.get("GEBT_Version"));
			 * 
			 * System.out.println("-------ï¿½Þ¸ï¿½PDx_Squence:"+maps.get("PDx_Squence"
			 * ) +"      ï¿½ï¿½GEBT_Version------"+maps.get("GEBT_Version"));
			 * 
			 * // maps.get("EQU") revision.setProperty("jci6_EQU",
			 * maps.get("EQU"));
			 * System.out.println("------ï¿½Þ¸ï¿½jci6_EQU--------"+maps.get("EQU"));
			 * 
			 * 
			 * String Calc_Date = maps.get("Calc_Date"); if
			 * (!Calc_Date.equals("")) {
			 * revision.setDateProperty("jci6_CalcDate", sdf.parse(Calc_Date));
			 * System
			 * .out.println("----------jci6_CalcDateï¿½ï¿½ï¿½ï¿½Îªï¿½ï¿½"+Calc_Date); }
			 * else { revision.setDateProperty("jci6_CalcDate", null);
			 * System.out
			 * .println("----------jci6_CalcDateï¿½ï¿½ï¿½ï¿½ÎªNULLï¿½ï¿½ï¿½ï¿½ï¿½ï¿½"); }
			 * 
			 * String EQU_Signdate = maps.get("EQU_Signdate"); if
			 * (!EQU_Signdate.equals("")) {
			 * revision.setDateProperty("jci6_EQUSignDate",
			 * sdf.parse(EQU_Signdate));
			 * System.out.println("----------jci6_EQUSignDateï¿½ï¿½ï¿½ï¿½Îªï¿½ï¿½"
			 * +EQU_Signdate); } else {
			 * revision.setDateProperty("jci6_EQUSignDate", null);
			 * System.out.println("---------jci6_CalcDateï¿½ï¿½ï¿½ï¿½ÎªNULLï¿½ï¿½ï¿½ï¿½ï¿½ï¿½");
			 * }
			 * 
			 * // ï¿½ï¿½ï¿½ï¿½calculated_byï¿½ï¿½Öµ TCComponentUserType userType =
			 * (TCComponentUserType) session .getTypeComponent("User");
			 * TCComponentUser user = userType.find(maps.get("calculated_by"));
			 * System
			 * .out.println("---------ï¿½Òµï¿½calculated_byï¿½ï¿½USERï¿½ï¿½ï¿½ï¿½ï¿½ï¿½-----");
			 * revision.setReferenceProperty("jci6_Responsibility", user);
			 * System
			 * .out.println("---------ï¿½Þ¸ï¿½jci6_Responsibilityï¿½ï¿½USERï¿½ï¿½ï¿½ï¿½ï¿½ï¿½-----"
			 * );
			 * 
			 * // maps.get("approved_amount")
			 * revision.setProperty("jci6_SignedMoney",
			 * maps.get("approved_amount"));
			 * System.out.println("---------ï¿½Þ¸ï¿½jci6_SignedMoneyï¿½ï¿½Öµ------"
			 * +maps.get("approved_amount"));
			 * 
			 * String PDx_Sign_Date = maps.get("PDx_Sign_Date"); if
			 * (!PDx_Sign_Date.equals("")) {
			 * revision.setDateProperty("jci6_PDxSignDate",
			 * sdf.parse(PDx_Sign_Date));
			 * System.out.println("----------jci6_PDxSignDateï¿½ï¿½ï¿½ï¿½Îªï¿½ï¿½"
			 * +PDx_Sign_Date); } else {
			 * revision.setDateProperty("jci6_PDxSignDate", null);
			 * System.out.println
			 * ("---------jci6_PDxSignDateï¿½ï¿½ï¿½ï¿½ÎªNULLï¿½ï¿½ï¿½ï¿½ï¿½ï¿½"); }
			 * 
			 * // 4.18ï¿½Þ¸ï¿½--
			 * ï¿½æ±¾ï¿½ï¿½current_revision_idï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½PDx_Squenceï¿½ï¿½Öµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
			 * revision.setProperty("item_revision_id",
			 * maps.get("PDx_Squence"));
			 * System.out.println("---------item_revision_idï¿½ï¿½ï¿½ï¿½Îª-----"
			 * +maps.get("PDx_Squence"));
			 * 
			 * System.out.println("----ï¿½Þ¸ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?----"); }
			 */
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// ï¿½ï¿½ï¿½ï¿½Â·
	private void openByPass() {
		try {
			session.getUserService().call("open_or_close_pass",
					new Object[] { 1 });

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ï¿½Ø±ï¿½ï¿½ï¿½Â·
	private void closeByPass() {
		try {
			session.getUserService().call("open_or_close_pass",
					new Object[] { 0 });
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ï¿½ï¿½ï¿½ï¿½ï¿½æ±¾ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
	private void releaseRev(TCComponent ir) throws TCException {
		// ï¿½ÈµÃµï¿½ReleaseStatusType
		TCComponentReleaseStatusType rlaType = (TCComponentReleaseStatusType) session
				.getTypeComponent("ReleaseStatus");

		// ï¿½ï¿½ï¿½ï¿½ÏµÍ³ï¿½Ä¿ï¿½ï¿½Ù·ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ì¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½"TCM ï¿½Ñ·ï¿½ï¿½ï¿½"
		// //openByPass();
		TCComponent tcp = rlaType.create(reg.getString("TCM_release"));
		// closeByPass();

		// ï¿½ï¿½ï¿½ï¿½TCComponentReleaseStatusTypeï¿½ï¿½ï¿½ï¿½ï¿?
		// //openByPass();
		tcp.save();
		// closeByPass();

		// ï¿½ï¿½Óµï¿½Ö¸ï¿½ï¿½ï¿½Ä°æ±¾ï¿½ï¿½ï¿½ï¿½ï¿½Ð£ï¿½ï¿½ó¶¨¹ï¿½Ï?
		// //openByPass();
		ir.add("release_status_list", tcp);
		// closeByPass();
	}

	// ï¿½Þ¸ï¿½TCï¿½ï¿½ï¿½Ñ·ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?
	private void setDataByPass(TCComponentItemRevision revision, String name,
			String value) throws TCException, Exception {
		if (name.equals("jci6_EQUSignDate") || name.equals("jci6_CalcDate")
				|| name.equals("jci6_PDxSignDate")) {
			TCDateFormat format_temp = new TCDateFormat(session);
			SimpleDateFormat sdf_Temp = format_temp.askDefaultDateFormat();

			if (!value.equals("")
					&& value.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])")) {
				if (name.equals("jci6_CalcDate")) {

					Date date_CalcDate = dataset
							.getDateProperty("creation_date");
					revision.setDateProperty(name, date_CalcDate);
				} else {
					System.out.println("------set data begining !!!-----");
					revision.setDateProperty(
							name,
							(sdf_Temp.parse(changeDateString(value) + " 00:00")));
					// closeByPass();
				}
			}
		}
	}

	// ï¿½Òµï¿½TCÏµÍ³ï¿½ï¿½ï¿½Ó¦ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?
	// ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ñ§ï¿½ï¿½
	private boolean isSearchvalueofPreference(String value, String properityName) {

		boolean flag = false;
		try {
			// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Îªï¿½ï¿½ï¿½é¡?
			if (properityName.equals("Group")) {
				TCComponentGroupType groupType = (TCComponentGroupType) session
						.getTypeComponent(properityName);

				TCComponentGroup componentGroup = groupType.find(value);
				if (componentGroup != null) {
					flag = true;
				}
			} else if (properityName.equals("Section")) {
				// ï¿½ï¿½Ñ¯Ñ§ï¿½ï¿½
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

	// ï¿½ï¿½ï¿½ï¿½ ---ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½Í·ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
	private TCComponent createCostInfo(TCSession session,
			TCComponentItemRevision revision, String object_name, int type,
			String GroupName, String SelectionName, int year, String costType)
			throws TCException, ServiceException {
		// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
		DataManagementService dmService = DataManagementService
				.getService(session);

		com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[] createIn = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];

		createIn[0] = new DataManagement.CreateIn();

		createIn[0].data.boName = "JCI6_CostInfo";

		ProjectID = revision.getProperty("item_id");
		// ï¿½Ãµï¿½Ê±ï¿½ï¿½ï¿?
		String timestamp = getSystemTime();
		String name = "";

		String name_Unit = "";
		if (object_name.equals("ÈËÔÂ")) {
			name_Unit = "ManMonth";
		} else if (object_name.equals("Ð¡Ê±")) {
			name_Unit = "Hour";
		} else if (object_name.equals("Ôª")) {
			name_Unit = "Yuan";
		}

		// ï¿½ÈµÃµï¿½groupï¿½Ä¶ï¿½ï¿½ï¿½,ï¿½ï¿½Ý¶ï¿½ï¿½ï¿½Ãµï¿½Groupï¿½Ä¶ï¿½Ð´ï¿½ï¿½ï¿½ï¿½
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
			// ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
			// ï¿½ï¿½TCProperty[]ï¿½ï¿½ï¿½æ´¢Ò»Ïµï¿½ï¿½Òªï¿½Þ¸Äµï¿½TCï¿½Ðµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?
			System.out.println("---------ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢---------");

			property = component.getTCProperties(new String[] { "jci6_CPT",
					"jci6_CostType", "jci6_Unit", "jci6_Year" });

			property[0].setStringValueData("Budget");
			property[1].setStringValueData(costType);
			property[2].setStringValueData(name_Unit);

			// ï¿½ï¿½ï¿½ï¿½intï¿½ï¿½
			property[3].setIntValueData(year);
			component.setTCProperties(property);

		} else {
			// ÈË¹¤·ÑÓÃÐÅÏ¢
			System.out.println("---------ï¿½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢---------");

			// ï¿½ï¿½TCProperty[]ï¿½ï¿½ï¿½æ´¢Ò»Ïµï¿½ï¿½Òªï¿½Þ¸Äµï¿½TCï¿½Ðµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?
			property = component.getTCProperties(new String[] { "jci6_CPT",
					"jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Division",
					"jci6_RateLevel" });

			property[0].setStringValueData("Budget");
			property[1].setStringValueData("Normal Hours");
			property[2].setStringValueData(name_Unit);

			// ï¿½ï¿½ï¿½ï¿½intï¿½ï¿½,
			property[3].setIntValueData(year);

			// ï¿½ï¿½Ñ¯Ö¸ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
			property[4].setReferenceValueData(componentGroup);

			// System.out.println("---Ñ§¿ÆÃû³Æ---" + SelectioName);
			

			//ÈýÆÚebpÐÞ¸Ä---by wuwei
			boolean flag_new=false;
			if (SelectionName.equals("Resident Engineer")){
				System.out.println("---2014/6/11  Set RateLevel NULL---" + SelectionName);
				flag_new=true;				
				property[5].setReferenceValueData((TCComponent)null);
			}else{

				// ²éÑ¯Ñ§¿Æ
				TCComponent[] tcp = query(session, "YFJC_Search_Discipline",
					new String[] { "discipline_name" },
					new String[] { SelectionName });
				if (tcp != null)
					property[5].setReferenceValueData(tcp[0]);
				else
					System.out.println("-----²éÑ¯Ñ§¿ÆÃû³ÆÃ»ÕÒµ½£¡£¡£¡---");
			}
				
				
			// 5.4ÐÞ¸Ä----ÈË¹¤·ÑÓÃ
			component.setTCProperties(property);
			if(flag_new){
			
				System.out.println("---2014/6/11  SelectioName---" + SelectionName);
				component.setProperty("jci6_TaskType", "tasktype26");		
			}

		}

		// ï¿½Â¹Òµï¿½Ö¸ï¿½ï¿½ï¿½Ä°æ±¾ï¿½ï¿½
		// ////openByPass();
		revision.add("IMAN_external_object_link", component);
		// //closeByPass();

		// 7.2ä¿®æ”¹---æŠŠæ•°æ®é›†çš„é¡¹ç›®ï¼ŒæŒ‡æ´¾ç»™æ–°åˆ›å»ºçš„è´¹ç”¨ä¿¡æ?
		// openByPass();
		// projectComponent.add("project_data", component);
		// closeByPass();

		// component.save();
		component.refresh();

		return component;
	}

	// É¾ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï§n¿½ï¿½ï¿½ï¿?
	private void deleteCostInfo(TCComponentItemRevision revision)
			throws TCException {
		TCComponent[] tcComponents = revision
				.getReferenceListProperty("IMAN_external_object_link");
		for (int i = 0; i < tcComponents.length; i++) {
			// //openByPass();
			revision.remove("IMAN_external_object_link", tcComponents[i]);
			// closeByPass();

			// //openByPass();
			tcComponents[i].delete();
			// closeByPass();
		}
	}

	// 5.27----É¾ï¿½ï¿½ï¿½ï¿½Öµï¿½Ã·ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½
	private void deleteCostInfo_noValue(TCComponentItemRevision revision)
			throws TCException {

		TCComponent[] tcComponents = revision
				.getReferenceListProperty("IMAN_external_object_link");
		for (int i = 0; i < tcComponents.length; i++) {
			if (isCostInfoNoValue(tcComponents[i])) {
				// //openByPass();
				revision.remove("IMAN_external_object_link", tcComponents[i]);
				// closeByPass();

				// openByPass();
				tcComponents[i].delete();
				// closeByPass();
			} else {
				releaseRev(tcComponents[i]);
			}
		}

	}

	// 5.27----É¾ï¿½ï¿½ï¿½ï¿½Öµï¿½Ã·ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½
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

	// ï¿½Ð¶ï¿½ï¿½Ã»ï¿½Ñ¡ï¿½ï¿½ï¿½ï¿½ï¿½Ý¼ï¿½ï¿½Ç·ñ·¢²ï¿½
	private boolean isDatasetRelease(TCComponentDataset dataset)
			throws TCException {
		String release = dataset.getProperty("release_status_list");
		if (!release.equals(reg.getString("TCM_release")))
			return false;
		else
			return true;
	}

	// 5.17---- ï¿½Ð¶ï¿½ï¿½Ã»ï¿½Ñ¡ï¿½ï¿½ï¿½ï¿½ï¿½Ý¼ï¿½ï¿½Ç·ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
	private boolean isDatasetINProcesssList(TCComponentDataset dataset)
			throws TCException {
		TCComponent[] tcComponents = dataset
				.getReferenceListProperty("process_stage_list");

		if (tcComponents == null || tcComponents.length == 0)
			return false;
		else
			return true;
	}

	// ï¿½È¼ï¿½ï¿½Ò»ï¿½ï¿½Òªï¿½ï¿½È¡ï¿½ï¿½Ö?
	private void checkExcel(HashMap<String, String> maps,
			JacobEReportTool tool, Dispatch sheetsAll,
			TCComponentItemRevision revision) {

		error_list = new ArrayList<HashMap<String, String>>();

		// ï¿½Ú¼ï¿½ï¿½ï¿½Ê±ï¿½ï¿½ï¿½È¡ï¿½ï¿½Òªï¿½ï¿½ï¿½Ðµï¿½ï¿½ï¿½Ö¹Î»ï¿½ï¿?
		try {
			// ï¿½Ãµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½Ö?--YFJC_HumanLabor_Position
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

			System.out
					.println("------ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½Ãºï¿½ï¿½------ï¿½ï¿½ï¿½ï¿½listï¿½ï¿½ï¿½ï¿½ï¿½ï¿½---"
							+ error_list.size());

			// 7.2ï¿½Þ¸ï¿½---- ï¿½Ãµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½Ö?--YFJC_HumanLabor_Position
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

			System.out
					.println("------ï¿½ï¿½ï¿½ï¿½È¡ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½Ôªï¿½ï¿½excelï¿½ï¿½ï¿?-----ï¿½ï¿½ï¿½ï¿½listï¿½ï¿½ï¿½ï¿½ï¿½ï¿½---"
							+ error_list.size());

			// ï¿½Ãµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½Ö?--YFJC_NonLabor_Position
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
			System.out
					.println("----ï¿½ï¿½ï¿½ï¿½listï¿½ï¿½ï¿½ï¿½ï¿½ï¿½---" + error_list.size());
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½Ö¸ï¿½ï¿½excelï¿½ï¿½ï¿?
	 */
	private void checkValueOfHumanLaborfromExcel(String sheet_name,
			int startRow, int endRow, int starDate, int endDate,
			int datePosition, JacobEReportTool tool, Dispatch sheetsAll,
			TCComponentItemRevision revision, String GEBTVersion)
			throws TCException, IOException {

		System.out
				.println("-----ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿?------ï¿½Ãµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½æ±¾Ç°ï¿½ï¿½Ö?---------");

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
		// .println("----------------------ï¿½Ãµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½æ±¾ï¿½Ôºï¿½ï¿½Öµ-----------------");
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

		// ï¿½ï¿½excelï¿½Ðµï¿½sheetÒ³
		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		endposition_labor = endDate;

		// È¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
		for (int col = starDate; col <= endDate; col++) {
			// ï¿½ï¿½ï¿½ÂµÄ·ï¿½ï¿½ï¿½ï¿½ï¿½È¡excel,×ªï¿½ï¿½ï¿½É±ï¿½×¼ï¿½ï¿½ï¿½ï¿½
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);
			String data;

			if (!data_string.equals("")) {
				try {
					data = sdf.format(new Date(data_string));
				} catch (Exception e) {
					// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
					String error_p = sheet_name + ":" + column + datePosition;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_Date", error_p);
					error_list.add(map);
					data = "1900-01-01";
				}

				if (!data
						.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
					// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
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

		// System.out.println("==6.3ï¿½Þ¸ï¿½========================="
		// + endposition_labor);

		// È¡ï¿½ï¿½Ö¸ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ö?
		for (int i = startRow; i <= endRow; i++) {
			checkHumanLaborByline(tool, sheet_name, i, starDate,
					endposition_labor, sheet, revision,
					nameOfPreference.get("Section"),
					nameOfPreference.get("Group"));
		}

	}

	/*
	 * ï¿½ï¿½ï¿½Ò»ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½sheet
	 */
	private void checkHumanLaborByline(JacobEReportTool tool,
			String sheet_name, int row, int starDate, int endDate,
			Dispatch sheet, TCComponentItemRevision revision,
			String Section_name, String Group_name) throws ServiceException,
			TCException {

		// ï¿½ï¿½È¡Ã¿Ò»ï¿½ÐµÄµÚ¶ï¿½ï¿½ï¿½,ï¿½ï¿½ï¿½ï¿½ï¿½Ðµï¿½Î»ï¿½Ã£ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ð¶ï¿½ï¿½ï¿½ï¿½ï¿½Öµï¿½ï¿½tcï¿½ï¿½ï¿½Ç·ï¿½ï¿½ï¿½ï¿?
		// 1.ï¿½ï¿½ï¿½Ð¶ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Öªï¿½ï¿½ï¿½ï¿½ï¿?
		// 2.ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½tcï¿½ï¿½ï¿½Ç·ï¿½ï¿½ï¿½Öµ
		// 3.ï¿½ï¿½ï¿½Ã»ï¿½Ð£ï¿½returnï¿½ï¿½ï¿½ï¿½
		String data_key_1 = tool.getDataFromExcel("B", row, sheet);
		String data_key_2 = tool.getDataFromExcel("C", row, sheet);

		// System.out.println("-------Bï¿½Ðµï¿½Öµ======" + data_key_1);

		// 5.8ï¿½Þ¸ï¿½--ï¿½ï¿½ï¿½excelï¿½ï¿½ï¿½Ðµï¿½ï¿½ï¿½ï¿½ï¿½Îª0.ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ò»ï¿½Ð¾ï¿½ï¿½ï¿½ï¿?
		String height_str = tool.getHeight("B" + row, sheet);
		if (height_str.equals("0.0")) {
			return;
		}

		// System.out.println("data_key_2=====" + data_key_2);
		if (data_key_2.equals("null")) {

			// System.out.println("ï¿½ï¿½ï¿½Ò»ï¿½Ð£ï¿½ï¿½ï¿½ï¿½ï¿?====" + sheet + ": " + row);
		} else if (data_key_1.equals("null")
				&& data_key_2.equals("Total FTE's")) {

			// System.out.println("ï¿½ï¿½ï¿½Ò»ï¿½Ð£ï¿½ï¿½ï¿½ï¿½ï¿?====" + sheet + ": " + row);

		} else if (data_key_1.equals("null") && data_key_2.contains("Total")) {
			// System.out.println("ï¿½ï¿½ï¿½Ò»ï¿½Ð£ï¿½ï¿½ï¿½ï¿½ï¿?====" + sheet + ": " + row);

		} else {
			// System.out.println("data_key_2=====" + data_key_2);
			String findValue_section = null;
			String Group_value = null;

			findValue_section = isFindValue(data_key_2, session, Section_name);
			if (findValue_section.equals("")) {
				// ï¿½ï¿½ï¿½ï¿½
				String error_p = sheet_name + ":" + "C" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Section", error_p);
				error_list.add(map);
			}

			// 5.6ï¿½Þ¸ï¿½-----ï¿½ï¿½ï¿½data_key_2Îª"SMTP"ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½é£¬ï¿½ï¿½Òªï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ã£ï¿½ï¿½Òµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿?
			if (data_key_2.equals("SMTE")) {
				// 5.6-ï¿½Þ¸ï¿½
				Group_value = isFindValue(data_key_2, session,
						YFJC_HumanLabor_SMTE_GROUP);
				// System.out.println("SMTE=======" + Group_value);
			} else {
				if (!data_key_1.equals("null")) {
					// 5.8---ï¿½Þ¸Ä±ï¿½ï¿½ï¿½
					String findValue_group = isFindValue(data_key_1, session,
							Group_name);
					if (!findValue_group.equals("")) {
						// ï¿½Òµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½Ó¦ï¿½ï¿½Ö?

						// ï¿½ï¿½ï¿½ï¿½ï¿½Þ¸Ä¡ï¿½ï¿½ï¿½group
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
						// ï¿½ï¿½ï¿½ï¿½
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
				// ÏµÍ³ï¿½ï¿½Ã»ï¿½Ð¶ï¿½Ó¦ï¿½ï¿½Öµ
				// ï¿½ï¿½ï¿½ï¿½
				String error_p = sheet_name + ":" + "B" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Group", error_p);
				error_list.add(map);
			}

			boolean b_section = isSearchvalueofPreference(findValue_section,
					"Section");

			//ÐÞ¸Ä×îÐÂ´úÂë---by wuwei
			if(findValue_section.contains("Resident Engineer")){
				b_section =true;
			}


			if (!b_section && !findValue_section.equals("")) {
				// ÏµÍ³ï¿½ï¿½Ã»ï¿½Ð¶ï¿½Ó¦ï¿½ï¿½Öµ
				// ï¿½ï¿½ï¿½ï¿½
				String error_p = sheet_name + ":" + "C" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Section", error_p);
				error_list.add(map);
			}

			// È¡Ã¿ï¿½ï¿½ï¿½Âµï¿½Öµ
			for (int col = starDate; col <= endDate; col++) {

				// ï¿½ï¿½È¡Ã¿ï¿½Ö·ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Íµï¿½Öµ
				// ï¿½ï¿½ExcelRWï¿½ï¿½È¡excel
				String data = tool.getDataFromExcel(NumToString(col), row,
						sheet);

				if (data.equals("null") || data.equals("")) {
					data = "0.0";
				}

				double dCostValue;

				// if (data.matches("^(-?\\d+)(\\.\\d+)?$")) {
				try {
					dCostValue = Double.parseDouble(data);
					// System.out.println("-----ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½----" + dCostValue);
				} catch (Exception e) {
					// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
					System.out
							.println("------ï¿½ï¿½ï¿½ï¿½×ªï¿½ï¿½ï¿½ï¿½ï¿½ï¿½-----ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½===");
					String error_p = sheet_name + ":" + NumToString(col) + row;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_num", error_p);
					error_list.add(map);
					return;
				}
				// } else {
				// // ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
				// System.out.println("----------ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ö²ï¿½ï¿½Ô£ï¿½ï¿½ï¿½--ï¿½ï¿½----");
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
	 * ï¿½ï¿½ï¿½Ò»ï¿½Ð·ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï?
	 */
	private void checkNonLaborByline(JacobEReportTool tool, String sheet_name,
			int row, int starDate, int endDate, String preferenceName,
			Dispatch sheet) throws ServiceException, TCException {

		System.out.println("-------ï¿½ï¿½ï¿½Ò»ï¿½Ð·ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï?!!--------");
		// È¡ï¿½Ãµï¿½Ò»ï¿½Ðµï¿½Öµ,
		String cellValue = tool.getDataFromExcel("A", row, sheet);

		// 5.8ï¿½Þ¸ï¿½--ï¿½ï¿½ï¿½excelï¿½ï¿½ï¿½Ðµï¿½ï¿½ï¿½ï¿½ï¿½Îª0.ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ò»ï¿½Ð¾ï¿½ï¿½ï¿½ï¿?
		String height_str = tool.getHeight("A" + row, sheet);
		if (height_str.equals("0.0")) {
			// System.out.println("-------ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿?!!--------");
			return;
		}

		// Ê²Ã´Ê±ï¿½ò´´½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½Ð½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½keyOfreferenceÎªï¿½ï¿½È¡Ò»ï¿½ï¿½excelï¿½ï¿½ï¿½Öµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ç·ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ó¦ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
		String findValue = isFindValue(cellValue, session, preferenceName);

		if (findValue.equals("")) {
			return;
		}

		// ï¿½Ãµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½--Ö»ï¿½Ð·ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½
		// String costType = isFindValue(cellValue, session,
		// preferenceName);

		// if (costType.equals("")) {
		// // ï¿½ï¿½ï¿½ï¿½
		// String error_p = sheet_name + ":" + "A" + row;
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("error_CostType", error_p);
		// error_list.add(map);
		// }

		// È¡Ã¿ï¿½ï¿½ï¿½Âµï¿½Öµ
		for (int col = starDate; col <= endDate; col++) {

			// ï¿½ï¿½È¡Ã¿ï¿½Ö·ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Íµï¿½Öµ
			double dCostValue = 0;

			// ï¿½ï¿½ExcelRWï¿½ï¿½ï¿½ß¶ï¿½È¡excel
			String cellCost = tool.getDataFromExcel(NumToString(col), row,
					sheet);

			if (cellCost.equals("null") || cellCost.equals("")) {
				cellCost = "0.0";
			}

			if (cellCost.matches("^(-?\\d+)(\\.\\d+)?$")) {
				try {
					dCostValue = Double.parseDouble(cellCost);
					// System.out.println("-----ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½----" + dCostValue);
				} catch (Exception e) {
					// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
					String error_p = sheet_name + ":" + NumToString(col) + row;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_num", error_p);
					error_list.add(map);
					return;
				}
			} else {
				// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
				System.out.println("---ï¿½ï¿½ï¿½Ö²ï¿½ï¿½ï¿½---");
				String error_p = sheet_name + ":" + NumToString(col) + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_num", error_p);
				error_list.add(map);
				return;
			}
		}
	}

	/*
	 * ï¿½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½deÖ¸ï¿½ï¿½excelï¿½ï¿½ï¿?
	 */
	private void checkValueOfNon_LaborfromExcel(String sheet_name,
			int startRow, int endRow, int starDate, int endDate,
			int datePosition, JacobEReportTool tool, Dispatch sheetsAll,
			String GEBTVersion) throws ServiceException, TCException {

		System.out
				.println("--------ï¿½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½-------ï¿½Ãµï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½æ±¾ï¿½Ôºï¿½ï¿½Öµï¿½ï¿½---------------");
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

		// È¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
		for (int col = starDate; col <= endDate; col++) {
			// ï¿½ï¿½ï¿½ÂµÄ·ï¿½ï¿½ï¿½ï¿½ï¿½È¡excel
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);
			String data;

			if (!data_string.equals("")) {
				try {
					data = sdf.format(new Date(data_string));
				} catch (Exception e) {
					// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
					String error_p = sheet_name + ":" + column + datePosition;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_Date", error_p);
					error_list.add(map);
					data = "1900-01-01";
				}

				if (!data
						.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
					// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
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

		// System.out.println("=========6.3ï¿½Þ¸ï¿½==endposition_nonlabor==========="
		// + endposition_nonlabor);

		for (int i = startRow; i <= endRow; i++) {
			checkNonLaborByline(tool, sheet_name, i, starDate,
					endposition_nonlabor, nameOfPreference.get("CostType"),
					sheet);
		}

	}

	// ÈË¹¤ÐÅÏ¢
	// ¶ÁÈ¡Ö¸¶¨µÄÒ»ÐÐ·ÑÓÃÐÅÏ¢µÄÖµ,²¢ÇÒÉèÖÃ½øÈ¥
	private void setCostInfoValuesByRow(TCComponentItemRevision revision,
			JacobEReportTool tool, int row, int startDate, int endDate,
			HashMap<String, String> month_maps,
			HashMap<String, String> year_maps, Dispatch sheet)
			throws ServiceException, TCException {

		// ¶ÁÈ¡Ã¿Ò»ÐÐµÄµÚ¶þÁÐ,µÚÈýÁÐµÄÎ»ÖÃ£¬²¢ÇÒÅÐ¶ÏÊôÐÔÖµÔÚtcÖÐÊÇ·ñ´æÔÚ
		// 1.ÏÈÅÐ¶ÏÊ×ÏÈÏîÀïÖª·ñ´æÔÚ
		// 2.´æÔÚÔÙÕÒtcÖÐÊÇ·ñÓÐÖµ
		// 3.Èç¹ûÃ»ÓÐ£¬return½áÊø
		String data_key_1 = tool.getDataFromExcel("B", row, sheet);
		String data_key_2 = tool.getDataFromExcel("C", row, sheet);

		// 5.8ÐÞ¸Ä--Èç¹ûexcelÕâÐÐµÄÐÐÊýÎª0.¼´Òþ²ØÒ»ÐÐ¾ÍÌø¹ý¡£
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
			// 5.6-ÐÞ¸Ä--YFJC_HumanLabor_SMTE_GROUP
			Group_value = isFindValue(data_key_2, session,
					YFJC_HumanLabor_SMTE_GROUP);
			// System.out.println("SMTE---¶ÔÓ¦µÃGroup£¡£¡£¡");
		} else {

			if (!data_key_1.equals("null")) {
				// 5.8---ÐÞ¸Ä±àÂë==YFJC_HumanLabor_Group
				String findValue_group = isFindValue(data_key_1, session,
						nameOfPreference.get("Group"));
				if (!findValue_group.equals("")) {
					// ÕÒµ½Ê×Ñ¡Ïî¶ÔÓ¦µÄÖµ

					// ×îÐÂÐÞ¸Ä¡ª¡ªgroup
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
					// ±¨´í

				}
			} else {
				Group_value = map_group.get("Group");
			}
		}

		String year = year_maps.get(startDate + "");

		// 5.6ÐÞ¸Ä
		String index = Group_value + findValue_section;

		// È¡µÃ·ÑÓÃÐÅÏ¢ÀïÐèÒªµÄÊôÐÔ
		TCComponent labor_CostInfo = null;
		TCComponent labor_CostInfo_hour = null;
		TCComponent labor_CostInfo_money = null;

		// HashMap<String, TCComponent> maps_CostInfo = new HashMap<String,
		// TCComponent>();

		// maps_CostInfo.put("ÈËÔÂ", labor_CostInfo);
		// maps_CostInfo.put("Ð¡Ê±", labor_CostInfo_hour);
		// maps_CostInfo.put("Ôª", labor_CostInfo_money);

		// µÃµ½·ÑÂÊBYÑ§¿Æ¡ª¡ª¡ª¡ª¡ª¡ªjci6_RateLevel
		// String rate_string = "";

		// 7.2ÐÞ¸Ä--µÚ¶þ´Î´´½¨¡°Ôª¡±·ÑÓÃÐÅÏ¢
		if (TYPE_HumanLaborYuan == 1) {

		}

		// È¡Ã¿¸öÔÂµÄÖµ
		for (int col = startDate; col <= endDate; col++) {
			if (TYPE_HumanLaborYuan == 0) {
				if (maps_HunmanLabor == null) {
					int YEAR = Integer.parseInt(year);
					maps_HunmanLabor = new HashMap<String, HashMap<String, TCComponent>>();

					// ´´½¨¡°ÈËÔÂ¡±·ÑÓÃÐÅÏ¢
					labor_CostInfo = createCostInfo(session, revision, "ÈËÔÂ", 2,
							Group_value, findValue_section, YEAR, null);
					labor_CostInfo_hour = createCostInfo(session, revision,
							"Ð¡Ê±", 2, Group_value, findValue_section, YEAR, null);

					HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
					maps_CostInfo.put("ÈËÔÂ", labor_CostInfo);
					maps_CostInfo.put("Ð¡Ê±", labor_CostInfo_hour);
					maps_HunmanLabor.put(index + year, maps_CostInfo);
				}
				if (!year.equals(year_maps.get(col + ""))) {
					// ±¾À´Òª´´½¨ÐÂµÄ·ÑÓÃÐÅÏ¢£¬µ«ÊÇÈç¹ûÓÐÁË¾Í²»ÐèÒª´´½¨
					year = year_maps.get(col + "");
					boolean flag = false;

					// 5.6ÐÞ¸Ä
					for (String s : maps_HunmanLabor.keySet()) {
						if ((index + year).equals(s)) {
							flag = true;
						}
					}

					if (!flag) {
						int YEAR = Integer.parseInt(year);

						// ´´½¨¡°ÈËÔÂ¡±·ÑÓÃÐÅÏ¢
						labor_CostInfo = createCostInfo(session, revision,
								"ÈËÔÂ", 2, Group_value, findValue_section, YEAR,
								null);
						labor_CostInfo_hour = createCostInfo(session, revision,
								"Ð¡Ê±", 2, Group_value, findValue_section, YEAR,
								null);

						HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
						maps_CostInfo.put("ÈËÔÂ", labor_CostInfo);
						maps_CostInfo.put("Ð¡Ê±", labor_CostInfo_hour);
						maps_HunmanLabor.put(index + year, maps_CostInfo);

					} else {
						System.out.println("====´æÔÚcostinfo¶ÔÏó===year===" + year
								+ "   " + index);
						labor_CostInfo = maps_HunmanLabor.get(index + year)
								.get("ÈËÔÂ");
						labor_CostInfo_hour = maps_HunmanLabor
								.get(index + year).get("Ð¡Ê±");

					}
				} else {

					boolean flag = false;
					// 5.6ÐÞ¸Ä
					for (String s : maps_HunmanLabor.keySet()) {
						if ((index + year).equals(s)) {
							flag = true;
						}
					}

					if (!flag) {
						int YEAR = Integer.parseInt(year);
						// ´´½¨¡°ÈËÔÂ¡±·ÑÓÃÐÅÏ¢
						labor_CostInfo = createCostInfo(session, revision,
								"ÈËÔÂ", 2, Group_value, findValue_section, YEAR,
								null);
						labor_CostInfo_hour = createCostInfo(session, revision,
								"Ð¡Ê±", 2, Group_value, findValue_section, YEAR,
								null);

						HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
						maps_CostInfo.put("ÈËÔÂ", labor_CostInfo);
						maps_CostInfo.put("Ð¡Ê±", labor_CostInfo_hour);
						maps_HunmanLabor.put(index + year, maps_CostInfo);

					} else {

						labor_CostInfo = maps_HunmanLabor.get(index + year)
								.get("ÈËÔÂ");
						labor_CostInfo_hour = maps_HunmanLabor
								.get(index + year).get("Ð¡Ê±");
					}
				}
			} else {
				if (maps_HunmanLabor_yuan == null) {
					int YEAR = Integer.parseInt(year);
					maps_HunmanLabor_yuan = new HashMap<String, HashMap<String, TCComponent>>();
					labor_CostInfo_money = createCostInfo(session, revision,
							"Ôª", 2, Group_value, findValue_section, YEAR, null);

					HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
					maps_CostInfo.put("Ôª", labor_CostInfo_money);
					maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);
				}

				if (!year.equals(year_maps.get(col + ""))) {
					// ±¾À´Òª´´½¨ÐÂµÄ·ÑÓÃÐÅÏ¢£¬µ«ÊÇÈç¹ûÓÐÁË¾Í²»ÐèÒª´´½¨
					year = year_maps.get(col + "");
					boolean flag = false;

					// 5.6ÐÞ¸Ä
					for (String s : maps_HunmanLabor_yuan.keySet()) {
						if ((index + year).equals(s)) {
							flag = true;
						}
					}
					if (!flag) {// ´´½¨¡°Ôª¡±·ÑÓÃÐÅÏ¢
						int YEAR = Integer.parseInt(year);
						labor_CostInfo_money = createCostInfo(session,
								revision, "Ôª", 2, Group_value,
								findValue_section, YEAR, null);

						HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
						maps_CostInfo.put("Ôª", labor_CostInfo_money);
						maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);

					} else {
						labor_CostInfo_money = maps_HunmanLabor_yuan.get(
								index + year).get("Ôª");

					}
				} else {

					boolean flag = false;
					// 5.6ÐÞ¸Ä
					for (String s : maps_HunmanLabor_yuan.keySet()) {
						if ((index + year).equals(s)) {
							flag = true;
						}
					}
					if (!flag) {// ´´½¨¡°Ôª¡±·ÑÓÃÐÅÏ¢
						int YEAR = Integer.parseInt(year);

						labor_CostInfo_money = createCostInfo(session,
								revision, "Ôª", 2, Group_value,
								findValue_section, YEAR, null);

						HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
						maps_CostInfo.put("Ôª", labor_CostInfo_money);
						maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);
					} else {

						labor_CostInfo_money = maps_HunmanLabor_yuan.get(
								index + year).get("Ôª");

					}
				}
			}

			// µÃµ½·ÑÂÊBYÑ§¿Æ¡ª¡ª¡ª¡ª¡ª¡ªjci6_RateLevel
			// if (TYPE_HumanLaborYuan == 0) {
			// TCComponent property = labor_CostInfo
			// .getReferenceProperty("jci6_RateLevel");
			// rate_string = property.getProperty("default_rate");
			// } else {
			// TCComponent property = labor_CostInfo_money
			// .getReferenceProperty("jci6_RateLevel");
			// rate_string = property.getProperty("default_rate");
			// }

			// »ñÈ¡Ã¿ÖÖ·ÑÓÃÀàÐÍµÄÖµ
			// ÓÃExcelRW¶ÁÈ¡excel
			String data = tool.getDataFromExcel(NumToString(col), row, sheet);

			if (data.equals("null") || data.equals("")) {
				data = "0.0";
			}

			// ÔöÁ¿µÄÖµ
			double dCostValue = 0;
			// Ô­À´ÊôÐÔµÄÖµ
			double val = 0;

			if (data.matches("^(-?\\d+)(\\.\\d+)?$")) {
				try {
					dCostValue = Double.parseDouble(data);
					// System.out.println("-----ÓÐÊý×Ö----" + dCostValue);
				} catch (Exception e) {
					// ±¨´íÐÅÏ¢
					dCostValue = 0;
				}
			} else {
				continue;
			}
			// ÈË¹¤·ÑÓÃµÄ·ÑÂÊ
			// Double rate = Double.parseDouble(rate_string);

			// 4.27±£ÁôÐ¡ÊýµãºóµÄÁ½Î»
			// (dCostValue+"").substring(0,(dCostValue+"").indexOf(".")+3)
			String stringValue = "";

			if (month_maps.get(col + "").equals("01")) {
				// ÅÐ¶ÏÊÇ·ñÎª0£¬²»Îª0½ØÈ¡5Î»ÈËÔÂ£¬4Î»Ð¡Ê±Êý£¬3Î»Ôª
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Jan");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  jci6_JanµÄÖµ²»Îª0===="
									+ val + "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
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
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  jci6_JanµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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

							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  jci6_JanµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  jci6_FebµÄÖµ²»Îª0===="
									+ val + "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Feb",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Feb");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  jci6_FebµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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
							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  jci6_FebµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  jci6_MarµÄÖµ²»Îª0===="
									+ val + "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Mar",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Mar");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  jci6_MarµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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
							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  jci6_MarµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  jci6_AprµÄÖµ²»Îª0===="
									+ val + "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Apr",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Apr");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  jci6_AprµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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
							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  jci6_AprµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  jci6_MayµÄÖµ²»Îª0===="
									+ val + "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_May",
								cutString(stringValue, 5));

						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_May");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  jci6_MayµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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
							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  jci6_MayµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  6ÔÂµÄÖµ²»Îª0====" + val
									+ "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Jun",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Jun");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  6ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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
							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  6ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  7ÔÂµÄÖµ²»Îª0====" + val
									+ "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Jul",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Jul");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  7ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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
							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  7ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  8ÔÂµÄÖµ²»Îª0====" + val
									+ "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Aug",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Aug");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  8ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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
							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  8ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  9ÔÂµÄÖµ²»Îª0====" + val
									+ "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Sep",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Sep");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  9ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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
							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  9ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  10ÔÂµÄÖµ²»Îª0====" + val
									+ "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Oct",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Oct");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  10ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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
							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  10ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  11ÔÂµÄÖµ²»Îª0====" + val
									+ "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Nov",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Nov");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  11ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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
							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  11ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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
							System.out.println("ÈËÔÂÈË¹¤·ÑÓÃÐÅÏ¢  12ÔÂµÄÖµ²»Îª0====" + val
									+ "  "
									+ labor_CostInfo.getProperty("jci6_Year"));
						}
						stringValue = dCostValue + val + "" + "00";
						labor_CostInfo.setProperty("jci6_Dec",
								cutString(stringValue, 5));
						String property_value_hour = labor_CostInfo_hour
								.getProperty("jci6_Dec");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value_hour);
							System.out.println("Ð¡Ê±ÈË¹¤·ÑÓÃÐÅÏ¢  12ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_hour
											.getProperty("jci6_Year"));
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
							System.out.println("ÔªÈË¹¤·ÑÓÃÐÅÏ¢  12ÔÂµÄÖµ²»Îª0===="
									+ val
									+ "  "
									+ labor_CostInfo_money
											.getProperty("jci6_Year"));
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

	// ï¿½ï¿½È¡ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½Ö¸ï¿½ï¿½ï¿½ï¿½ï¿½Ðµï¿½excelï¿½ï¿½ï¿?
	private void setValuefromExcel(TCComponentItemRevision revision,
			String sheet_name, int startRow, int endRow, int starDate,
			int endDate, int datePosition, JacobEReportTool tool,
			Dispatch sheetsAll) throws TCException, IOException {

		System.out.println("-----ï¿½ï¿½È¡ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½-------");

		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		HashMap<String, String> month_maps = new HashMap<String, String>();
		HashMap<String, String> year_maps = new HashMap<String, String>();

		// È¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
		for (int col = starDate; col <= endDate; col++) {
			// ï¿½ï¿½ï¿½ÂµÄ·ï¿½ï¿½ï¿½ï¿½ï¿½È¡excel
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);
			String data;
			try {
				data = sdf.format(new Date(data_string));
			} catch (Exception e) {
				// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
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

		// È¡ï¿½ï¿½Ö¸ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ö?
		for (int i = startRow; i <= endRow; i++) {
			setCostInfoValuesByRow(revision, tool, i, starDate, endDate,
					month_maps, year_maps, sheet);
		}

	}

	// ï¿½ï¿½È¡ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½Ãµï¿½excelï¿½ï¿½ï¿?
	private void setValuefromNonLabel(TCComponentItemRevision revision,
			String sheet_name, int startRow, int endRow, int starDate,
			int endDate, int datePosition, JacobEReportTool tool,
			Dispatch sheetsAll) throws ServiceException, TCException {

		System.out.println("-----ï¿½ï¿½È¡ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½-------");

		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		HashMap<String, String> month_maps_non = new HashMap<String, String>();
		HashMap<String, String> year_maps_non = new HashMap<String, String>();

		// È¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
		for (int col = starDate; col <= endDate; col++) {
			// ï¿½ï¿½ï¿½ÂµÄ·ï¿½ï¿½ï¿½ï¿½ï¿½È¡excel
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);

			String data = sdf.format(new Date(data_string));

			// ï¿½ï¿½Ï±ï¿½×¼ï¿½ï¿½ï¿½Ú¸ï¿½Ê?
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

	// ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢
	// ï¿½ï¿½È¡Ò»ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½Ä·ï¿½ï¿½ï¿½
	private void readNonLabor(TCComponentItemRevision revision,
			JacobEReportTool tool, int row, int startDate, int endDate,
			String preferenceName, HashMap<String, String> month_maps_non,
			HashMap<String, String> year_maps_non, Dispatch sheet)
			throws ServiceException, TCException {

		// È¡ï¿½Ãµï¿½Ò»ï¿½Ðµï¿½Öµ,ï¿½ï¿½ExcelRW
		String cellValue = tool.getDataFromExcel("A", row, sheet);

		// 5.8ï¿½Þ¸ï¿½--ï¿½ï¿½ï¿½excelï¿½ï¿½ï¿½Ðµï¿½ï¿½ï¿½ï¿½ï¿½Îª0.ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ò»ï¿½Ð¾ï¿½ï¿½ï¿½ï¿?
		String height_str = tool.getHeight("A" + row, sheet);
		if (height_str.equals("0.0")) {
			return;
		}

		// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ö?
		String year = year_maps_non.get(startDate + "");

		// Ê²Ã´Ê±ï¿½ò´´½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½Ð½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½keyOfreferenceÎªï¿½ï¿½È¡Ò»ï¿½ï¿½excelï¿½ï¿½ï¿½Öµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ç·ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ó¦ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
		// String findValue = isFindValue(cellValue, session, preferenceName);

		// ï¿½Ãµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½--Ö»ï¿½Ð·ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½----YFJC_NonLabor_CostType
		String findValue = isFindValue(cellValue, session,
				nameOfPreference.get("CostType"));

		String costType = findValue;

		if (findValue.equals("")) {
			return;
		}

		// ï¿½ï¿½Òªï¿½ï¿½ï¿½ÃµÄ·ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
		TCComponent component_costInfo;

		// È¡Ã¿ï¿½ï¿½ï¿½Âµï¿½Öµ
		for (int col = startDate; col <= endDate; col++) {

			if (!year.equals(year_maps_non.get(col + ""))) {
				// ï¿½ï¿½ï¿½ï¿½Òªï¿½ï¿½ï¿½ï¿½ï¿½ÂµÄ·ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ë¾Í²ï¿½ï¿½ï¿½Òªï¿½ï¿½ï¿½ï¿?
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
							revision, "Ôª", 1, null, null, Year, costType);
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
							revision, "Ôª", 1, null, null, Year, costType);
					maps_CostInfo_Non.put(findValue + year, createCostInfo);
					component_costInfo = createCostInfo;

				} else {
					boolean flag = false;
					for (String s : maps_CostInfo_Non.keySet()) {
						if ((findValue + year).equals(s)) {
							flag = true;
						}
					}
					// ï¿½ï¿½ï¿½hashmapï¿½ï¿½Ã»ï¿½ï¿½
					if (!flag) {

						int Year = Integer.parseInt(year);
						// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
						TCComponent createCostInfo = createCostInfo(session,
								revision, "Ôª", 1, null, null, Year, costType);
						maps_CostInfo_Non.put(findValue + year, createCostInfo);
						component_costInfo = createCostInfo;
					} else {
						component_costInfo = maps_CostInfo_Non.get(findValue
								+ year);
					}
				}

				// ï¿½ï¿½È¡Ã¿ï¿½Ö·ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Íµï¿½Öµ
				double dCostValue = 0;
				double val = 0;

				// ï¿½ï¿½ExcelRWï¿½ï¿½ï¿½ß¶ï¿½È¡excel
				String cellCost = tool.getDataFromExcel(NumToString(col), row,
						sheet);

				if (cellCost.equals("null") || cellCost.equals("")) {
					cellCost = "0.0";
				}

				cellCost = cellCost + "00";

				if (cellCost.matches("^(-?\\d+)(\\.\\d+)?$")) {
					try {
						dCostValue = Double.parseDouble(cellCost);
						// System.out.println("-----ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½----" +
						// dCostValue);
					} catch (Exception e) {
						// ï¿½ï¿½Ð´ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
						dCostValue = -1;
					}
				} else {
					continue;
				}

				String stringValue = "";

				// 4.27ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢ï¿½ï¿½Ôªï¿½ï¿½Ð¡ï¿½ï¿½ï¿½ï¿½ï¿?Î» ((dCostValue+
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

	// ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½ï¿½È¥ï¿½ï¿½ï¿½ï¿½ï¿½Ôµï¿½Öµ
	private String isFindValue(String key, TCSession tcsession,
			String preferenceName) {
		String value = "";

		HashMap<String, String> hashMap = getTCPreferenceArray(tcsession,
				preferenceName);

		for (String keyValue : hashMap.keySet()) {
			// ï¿½Òµï¿½ï¿½ï¿½Ó¦ï¿½Ä°æ±¾
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
				// ï¿½ï¿½Í¬
				if (flag) {
					value = hashMap.get(keyValue);
					System.out.println("2014/6/11===find reference key£º" +keyValue+"     Value: "+ value);
				}

			} else {
				if (keyValue.equals(key)) {
					value = hashMap.get(keyValue);
					System.out.println("2014/6/11===find reference key£º" +key+"     Value: "+ value);
				}

				//System.out.println("2014/6/11===find reference key-Value----name£º" + key);
			}
		}
		return value;
	}

	// ï¿½ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½Öµï¿½Ô¡ï¿½=ï¿½ï¿½ï¿½Ö¸ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½hashmap
	private HashMap<String, String> getTCPreferenceArray(TCSession tcsession,
			String preferenceName) {

		TCPreferenceService tcpreservice = tcsession.getPreferenceService();
		String[] preString = tcpreservice.getStringArray(
				TCPreferenceService.TC_preference_site, preferenceName);
		HashMap<String, String> map_refer = new HashMap<String, String>();

		for (int i = 0; i < preString.length; i++) {
			String[] split = preString[i].split("=");
			// GEBTï¿½æ±¾
			String ver = split[0].trim();
			// 5.8ï¿½Þ¸ï¿½--ï¿½ï¿½ï¿½ë¡°UTF-8ï¿½ï¿½
			map_refer.put(ver, split[1].trim());
		}
		return map_refer;
	}

	// ï¿½Â½ï¿½ï¿½ï¿½Ñ¯ "Ñ§ï¿½ï¿½ï¿½ï¿½ï¿?ï¿½Ä¹ï¿½ï¿½ï¿½---ï¿½Ô¶ï¿½ï¿½ï¿½ï¿½Ñ¯ï¿½ï¿½ï¿½ï¿?
	public TCComponent[] query(TCSession session, String query_name,
			String[] arg1, String[] arg2) {
		TCComponentContextList imancomponentcontextlist = null;
		TCComponent[] component = null;
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
	 * colNameToNum::ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Òµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?
	 * 
	 * @param String
	 *            ï¿½ï¿½ï¿½ï¿½Aï¿½ï¿½Bï¿½È£ï¿½
	 * @return int ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
	 */
	public int colNameToNum(String colName) {
		int result = 0;
		for (int i = 0; i < colName.length(); i++) {
			result = result * 26 + colName.charAt(i) - 65 + 1;
		}
		return result;
	}

	// ï¿½ï¿½È¡ï¿½ï¿½Ó¦ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½î£¬ï¿½Ãµï¿½excelï¿½ï¿½Î»ï¿½Ãµï¿½Öµ
	private boolean getValueOfExcelByPreference(TCSession tcsession,
			String preferenceName, String lov_values) {
		// lov_valuesÖµÎªï¿½ï¿½V1ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½V2ï¿½ï¿½

		String index = null;

		// ï¿½ï¿½È¡ï¿½ï¿½ï¿½ï¿½ï¿½Ë¹ï¿½ï¿½Í·ï¿½ï¿½Ë¹ï¿½ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½
		TCPreferenceService tcpreservice = tcsession.getPreferenceService();

		String[] preString = tcpreservice.getStringArray(
				TCPreferenceService.TC_preference_site, preferenceName);

		HashMap<String, String> map = new HashMap<String, String>();

		for (int i = 0; i < preString.length; i++) {
			String[] split = preString[i].split("=");
			// GEBTï¿½æ±¾
			String ver = split[0].trim();
			String[] split2 = ver.split("_");
			if (split2.length == 2) {
				// ï¿½Òµï¿½ï¿½ï¿½Ó¦ï¿½Ä°æ±¾
				if (split2[1].equals(lov_values)) {
					index = ver;
					map.put(ver, split[1].trim());
					System.out.println("---ï¿½Òµï¿½ï¿½ï¿½Ó¦ï¿½Ä°æ±¾----" + ver);
				}
			}
		}

		// ï¿½ï¿½ï¿½Ã»ï¿½Òµï¿½ï¿½ï¿½ï¿½Í·Å½ï¿½error_listï¿½ï¿½ï¿½ï¿½
		if (index == null) {
			// combo_version.setEnabled(true);

			// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï¢
			String error_msg = "Not found related version from "
					+ preferenceName;
			HashMap<String, String> map_msg = new HashMap<String, String>();
			map_msg.put("error_msg", error_msg);
			error_list.add(map_msg);
			return false;

		} else {

			// ï¿½ï¿½È¡ï¿½ï¿½Ó¦ï¿½ï¿½Öµï¿½ï¿½ï¿½Òµï¿½excelï¿½Ðµï¿½cell
			String sheet = map.get(index).substring(0,
					map.get(index).indexOf("{"));
			// Ò³ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
			maps_Prefer_values.put("Sheet", sheet.trim());

			String row = map.get(index).substring(
					map.get(index).indexOf("}") + 1,
					map.get(index).indexOf("||"));
			String[] split_row = row.split("-");
			// ï¿½ï¿½Ê¼ï¿½Ðºï¿½ï¿½ï¿½Ö¹ï¿½ï¿½
			maps_Prefer_values.put("StartRow", split_row[0]);
			maps_Prefer_values.put("EndRow", split_row[1]);

			// ï¿½ï¿½ï¿½Úµï¿½ï¿½Ðµï¿½Î»ï¿½ï¿½
			String pos = map.get(index).substring(
					map.get(index).lastIndexOf("|") + 1,
					map.get(index).indexOf("("));
			maps_Prefer_values.put("Position", pos);

			// ï¿½Ãµï¿½ï¿½ï¿½ï¿½Úµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
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
	 * È¥ï¿½ï¿½Ð¡ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
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

	// ï¿½ï¿½ï¿½ï¿½×ªï¿½ï¿½Ó¢ï¿½ï¿½ï¿½ï¿½Ä¸,ï¿½ï¿½È¡excelï¿½ï¿½ï¿½Ðµï¿½ï¿½Ðµï¿½ï¿½ï¿½Öµ×ªï¿½ï¿½Ó¢ï¿½ï¿½ï¿½ï¿½Ä¸
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
			str = NumToString((Num / 26) - ((f == 26) ? 1 : 0)) + str;
		}

		strReturn = str;
		return strReturn;
	}

	// È¥ï¿½ï¿½ï¿½Â·ï¿½Ç°ï¿½Ä¡ï¿½0ï¿½ï¿½
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

	// ï¿½ï¿½È¡iÎ»Ð¡ï¿½ï¿½
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

	// ï¿½Ãµï¿½TCï¿½Ðµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ÏµÍ³ï¿½Ô¶ï¿½ï¿½ä»¯ï¿½ï¿½ï¿½ï¿½Ó¢ï¿½ï¿½
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

	// ï¿½Ãµï¿½ÏµÍ³ï¿½ï¿½Ê±ï¿½ï¿½
	private String getSystemTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		Date currentTime = new Date();
		String dateString = format.format(currentTime);
		return dateString;
	}

	// 6.5---ï¿½Þ¸ï¿½=ï¿½ï¿½Åªï¿½ï¿½ï¿½ï¿½ï¿½ï¿½---ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ñ¡ï¿½ï¿½ï¿½ï¿½
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

	private void showINfo(TCComponent component) throws TCException {
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
		System.out.println("==================CostInfo Infomation=========");
		System.out.println("jci6_Jan=============" + jci6_Jan_str);
		System.out.println("jci6_Feb=============" + jci6_Feb_str);
		System.out.println("jci6_Mar=============" + jci6_Mar_str);
		System.out.println("jci6_Apr=============" + jci6_Apr_str);

		System.out.println("jci6_May=============" + jci6_Mar_str);
		System.out.println("jci6_Jun=============" + jci6_Jun_str);
		System.out.println("jci6_Jul=============" + jci6_Jul_str);
		System.out.println("jci6_Aug=============" + jci6_Aug_str);

		System.out.println("jci6_Sep=============" + jci6_Sep_str);
		System.out.println("jci6_Oct=============" + jci6_Oct_str);
		System.out.println("jci6_Nov=============" + jci6_Nov_str);
		System.out.println("jci6_Dec=============" + jci6_Dec_str);

		System.out.println("==============Show End=====================");
	}

	private void ShowCostInfos(TCComponentItemRevision revision)
			throws TCException {
		TCComponent[] tcComponents = revision
				.getReferenceListProperty("IMAN_external_object_link");
		for (int i = 0; i < tcComponents.length; i++) {
			showINfo(tcComponents[i]);
		}
	}

	private String changeDateString(String str) {
		String target = "";
		if (str.contains("-")) {
			String[] splits = str.split("-");
			// Jan,Feb,Mar,Apr,May,Jun,Jul
			// Aug,Sep,Oct,Nov,Dec
			String month = "";
			if (splits[1].equals("01") || splits[1].equals("1")) {
				month = "Jan";
			} else if (splits[1].equals("02") || splits[1].equals("2")) {
				month = "Feb";
			} else if (splits[1].equals("03") || splits[1].equals("3")) {
				month = "Mar";
			} else if (splits[1].equals("04") || splits[1].equals("4")) {
				month = "Apr";
			} else if (splits[1].equals("05") || splits[1].equals("5")) {
				month = "May";
			} else if (splits[1].equals("06") || splits[1].equals("6")) {
				month = "Jun";
			} else if (splits[1].equals("07") || splits[1].equals("7")) {
				month = "Jul";
			} else if (splits[1].equals("08") || splits[1].equals("8")) {
				month = "Aug";
			} else if (splits[1].equals("09") || splits[1].equals("9")) {
				month = "Sep";
			} else if (splits[1].equals("10") || splits[1].equals("10")) {
				month = "Oct";
			} else if (splits[1].equals("11") || splits[1].equals("11")) {
				month = "Nov";
			} else if (splits[1].equals("12") || splits[1].equals("12")) {
				month = "Dec";
			}
			String day = splits[2];
			if (splits[2].contains(" ")) {
				day = splits[2].substring(0, splits[2].indexOf(" "));
			}

			target = day + "-" + month + "-" + splits[0];
		}
		return target;
	}
}
