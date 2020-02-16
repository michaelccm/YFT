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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

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
import com.teamcenter.rac.kernel.TCComponentDatasetType;
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
import com.teamcenter.rac.kernel.TCTypeService;
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
	private SimpleDateFormat sdf_show = new SimpleDateFormat(
			reg.getString("TimeFormat"));

	// 锟斤拷选锟斤拷锟斤拷
	// 4.18---锟睫改凤拷锟剿癸拷锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟窖★拷睿拷锟轿拷锟斤拷锟斤拷值为CostType锟斤拷值锟斤拷锟斤拷同锟酵革拷锟铰讹拷锟斤拷锟铰斤拷
	private static String YFJC_NonLabor_CostType = "YFJC_NonLabor_CostType";
	private static String YFJC_NonLabor_Position = "YFJC_NonLabor_Position";
	private static String YFJC_HumanLabor_Position = "YFJC_HumanLabor_Position";
	private static String YFJC_HumanLabor_Group = "YFJC_HumanLabor_Group";
	// 5.4--锟睫革拷锟剿癸拷锟斤拷锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟窖★拷锟斤拷锟斤拷锟?--YFJC_HumanLabor_RateLevel
	private static String YFJC_HumanLabor_Section = "YFJC_HumanLabor_RateLevel";
	// 5.6--锟睫革拷锟斤拷锟斤拷锟斤拷一锟斤拷锟斤拷选锟斤拷---锟揭碉拷锟剿癸拷锟斤拷锟矫碉拷SMTE锟斤拷锟斤拷
	private static String YFJC_HumanLabor_SMTE_GROUP = "YFJC_HumanLabor_SMTE_GROUP";
	private String YFJC_LaborCostYuan_Position = "YFJC_LabourCost_Position";
	// excel锟侥硷拷锟斤拷
	private static String ExcelNameMode = "GEBT.xlsm";

	// 锟斤拷锟斤拷锟斤拷斯锟斤拷锟絚ostInfo锟斤拷锟斤拷锟斤拷息
	private HashMap<String, MyCostInfoBean> maps_CostInfo_Non;
	// 5.6锟睫革拷----锟斤拷锟斤拷锟剿癸拷锟斤拷costInfo锟斤拷锟斤拷锟斤拷息
	HashMap<String, HashMap<String, MyCostInfoBean>> maps_HunmanLabor;
	HashMap<String, HashMap<String, MyCostInfoBean>> maps_HunmanLabor_yuan;
	// 锟斤拷锟斤拷锟斤拷选锟斤拷锟斤拷锟斤拷锟斤拷
	private HashMap<String, String> maps_Prefer_values = new HashMap<String, String>();

	// 锟斤拷锟斤拷锟剿癸拷锟斤拷锟矫碉拷锟斤拷锟斤拷锟较?
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
	
	//add by wuwei
	private static String YFJC_Budget_DateCheck = "YFJC_Budget_DateCheck";
			
	

	public ImportConfirmDialog(final Display display,
			final TCComponentItemRevision revision,
			final HashMap<String, String> maps, final TCSession session,
			final int isF, final TCComponentDataset dataset,
			TCComponent projectComponent) {

		obj[0] = "open bypass";// 锟斤拷路锟斤拷锟斤拷要锟侥憋拷锟斤拷锟斤拷锟斤拷锟街?

		shell = new Shell(display);
		shell.setSize(500, 465);
		shell.setText(reg.getString("dialog_Confirm"));
		this.session = session;
		this.isF = isF;
		this.dataset = dataset;
		this.revision = revision;
		this.projectComponent = projectComponent;
		// 注锟斤拷锟斤拷锟斤拷,SWT.NULTI锟斤拷锟斤拷锟斤拷选锟斤拷锟斤拷锟?SWT.FULL_SELECTION锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷选锟斤拷
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
		// TableItem item7 = new TableItem(table, SWT.CENTER);
		// TableItem item8 = new TableItem(table, SWT.CENTER);
		// TableItem item9 = new TableItem(table, SWT.CENTER);

		// 锟斤拷锟矫碉拷一锟叫碉拷值
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
			// 锟斤拷锟矫达拷锟捷癸拷锟斤拷锟斤拷值锟斤拷一锟斤拷写锟斤拷指锟斤拷锟斤拷元锟斤拷
			// 原值
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

			// 锟斤拷示去锟斤拷锟斤拷锟斤拷摹锟?锟斤拷
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

					// 锟截闭碉拷前锟斤拷锟斤拷
					shell.dispose();
				}
			});

			btn_ok.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					/**
					 * 2014-9-25 add by wuh  增加COC的相关操作
					 */
					COCOperation coc  = new COCOperation(session,revision,maps.get("GEBT_Version"));
					try {
						coc.executeOperation();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					/**
					 * 2014-9-25 add by wuh  增加COC的相关操作
					 */
					
					JacobEReportTool tool = new JacobEReportTool();
					int isClose = 0;
					int newRes=0;
					try {
						// 锟斤拷锟芥本锟窖撅拷锟斤拷锟斤拷锟斤拷锟斤拷直锟斤拷锟睫改ｏ拷要锟斤拷锟斤拷路
						// if
						// (revision.getProperty("release_status_list").equals(
						// reg.getString("TCM_release"))) {
						//
						// // 锟斤拷锟斤拷预锟斤拷说锟斤拷锟街达拷锟?TBL
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
						// // 锟饺硷拷锟揭伙拷锟揭拷锟饺★拷锟街?
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
						// // 锟斤拷删锟斤拷姹撅拷碌摹锟斤拷锟斤拷锟斤拷锟较拷锟斤拷锟?
						// deleteCostInfo(revision);
						// // 锟斤拷锟铰斤拷锟斤拷锟斤拷锟斤拷息
						// // 锟斤拷取excel锟斤拷锟斤拷锟斤拷荩锟斤拷锟叫达拷锟斤拷锟斤拷锟斤拷锟较拷锟?
						//
						// // 锟矫碉拷锟斤拷选锟斤拷锟街?--YFJC_HumanLabor_Position
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
						// // 锟矫碉拷锟斤拷选锟斤拷锟街?--YFJC_NonLabor_Position
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
						TCComponentTask rootTask = null;

						// 5.17修改---dataset数据集是否有
						if (isDatasetINProcesssList(dataset)) {
							//System.out.println("WW job--->"+target.getCurrentJob());
							if (dataset.getCurrentJob() != null) {
								// openByPass();
								rootTask = dataset.getCurrentJob().getRootTask();
								// closeByPass();
							}
						}
						
						System.out.println("lala isF-->"+isF);
						
						
						if (isF == 2) {
							try {
								// 锟斤拷锟铰斤拷锟斤拷锟斤拷锟斤拷息
								// 锟斤拷取excel锟斤拷锟斤拷锟斤拷荩锟斤拷锟叫达拷锟斤拷锟斤拷锟斤拷锟较拷锟?
								// 锟矫碉拷系统锟斤拷锟斤拷时锟侥硷拷
								String getenv = System.getenv("TEMP");
								String TC_path = System.getenv("TPR");
								tool.addDir(TC_path + "\\plugins");
								// 锟矫碉拷锟斤拷锟叫碉拷sheet
								Dispatch sheetsAll = tool.getSheets(getenv
										+ File.separator + ExcelNameMode);

								System.out.println("====checkExcel   start====");
								// 锟饺硷拷锟揭伙拷锟揭拷锟饺★拷锟街?
								checkExcel(maps, tool, sheetsAll, revision);
								System.out.println("====checkExcel   end===="+error_list.size());
								
								if (error_list.size() != 0) {
									newRes++;
									isClose = 1;
									// 锟截憋拷EXCEL锟斤拷锟?
									tool.closeExcelFile(false);

									//add by wuwei --2019.1.11
									StringBuilder sb_warr = new StringBuilder();
									StringBuilder sb_err = new StringBuilder();
									for (int i = 0; i < error_list.size(); i++) {
										HashMap<String, String> hashMap = error_list.get(i);
										if (hashMap.containsKey("error_num")) {

											sb_err.append(reg.getString("Error_data") + " "
													+ hashMap.get("error_num") + "\r\n");
										} else if (hashMap.containsKey("error_Date")) {

											sb_err.append(reg.getString("Error_data") + " "
													+ hashMap.get("error_Date") + "\r\n");
										} else if (hashMap.containsKey("error_msg")) {
											
											sb_err.append(hashMap.get("error_msg") + "\r\n");
										} else if (hashMap.containsKey("error_Group")) {

											sb_warr.append(reg.getString("Error_Group") + " "
													+ hashMap.get("error_Group") + "\r\n");
										} else if (hashMap.containsKey("error_Section")) {

											sb_warr.append(reg.getString("Error_Section") + " "
													+ hashMap.get("error_Section") + "\r\n");
										} else if (hashMap.containsKey("error_Cost")) {

											sb_warr.append(reg.getString("Error_CostType") + " "
													+ hashMap.get("error_CostType") + "\r\n");
										}
									}
									
									String errmessage = sb_warr.toString();
									String warnmessage = sb_err.toString();
									String warningMessage = "Warning Message";
									String errorMessage = "\r\n" + "Error Message";
									
									TCComponentItem myProgramItem = revision.getItem();
									String jci6_PDxSeq=maps.get("PDx_Squence");
									String datasetname1=myProgramItem.getProperty("item_id")+"_"+jci6_PDxSeq+"_errorMessage";
									
									String logPath1=System.getenv("TEMP")+File.separator+datasetname1+".txt";
									contentToTxt(logPath1,errmessage,warnmessage,warningMessage,errorMessage);
							
									
									//add by wuwei --2019.1.11
									//增加txt数据集，把报错信息挂到流程下
									TCComponentDataset txt_dataset=null;
									
									TCTypeService service = session.getTypeService();
									TCComponentDatasetType datasetType = (TCComponentDatasetType) service.getTypeComponent("Dataset");
									txt_dataset = datasetType.create(datasetname1, "description","Text");
									String[] arrTargetName = { logPath1 };
									String[] type={"Text"}; //excel
									txt_dataset.setFiles(arrTargetName,type);
									
									//System.out.println("WW txt_dataset--->"+txt_dataset);
									//System.out.println("WW rootTask--->"+rootTask);
									
									if(rootTask!=null){
										rootTask.addAttachments(
												TCAttachmentScope.GLOBAL,
												new TCComponent[] { txt_dataset },
												new int[] { 1 });
										
									}
									
									//Display.getDefault().syncExec(new Runnable() {
									//	@Override
									//	public void run() {
											// TODO Auto-generated method stub
											Excel_ErrorDialog dialog_error = new Excel_ErrorDialog( display,
													error_list);
										//}
									//});
								

									return;
								}

								
								openByPass();
								// 删锟斤拷锟斤拷锟斤拷锟较拷锟斤拷锟?
								deleteCostInfo(revision);
								closeByPass();

								// 锟矫碉拷锟斤拷选锟斤拷锟街?--YFJC_HumanLabor_Position
								getValueOfExcelByPreference(session,
										YFJC_HumanLabor_Position,
										maps.get("GEBT_Version"));

								TYPE_HumanLaborYuan = 0;
								
								System.out.println("HumanLabor endposition_labor-->"+endposition_labor+" StartCol:"+Integer
										.parseInt(maps_Prefer_values.get
										("StartCol")));
								int newStartPos=Integer.parseInt(maps_Prefer_values.get("StartCol"));
								
								if(newStartPos>endposition_labor){
									endposition_labor=Integer.parseInt(maps_Prefer_values.get("EndCol"));
								}
								
								System.out.println("endposition_labor-->"+endposition_labor);


								
								// 锟劫达拷锟斤拷锟剿癸拷锟斤拷息
								// 再创建人工信息
								setValuefromExcel(revision, maps_Prefer_values
										.get("Sheet"), Integer
										.parseInt(maps_Prefer_values
												.get("StartRow")),
										Integer.parseInt(maps_Prefer_values
												.get("EndRow")), Integer
												.parseInt(maps_Prefer_values.get

												("StartCol")), endposition_labor,
										Integer.parseInt(maps_Prefer_values.get

										("Position")), tool, sheetsAll);

								// 锟矫碉拷锟斤拷选锟斤拷锟街?--YFJC_HumanLabor_Position
								getValueOfExcelByPreference(session,
										YFJC_LaborCostYuan_Position,
										maps.get("GEBT_Version"));

								TYPE_HumanLaborYuan = 1;
								
								
								System.out.println("ww endposition_nonlabor-->"+endposition_nonlabor+" StartCol:"+Integer
										.parseInt(maps_Prefer_values.get
										("StartCol")));
								
								newStartPos=Integer.parseInt(maps_Prefer_values.get("StartCol"));
								
								if(newStartPos>endposition_nonlabor){
									endposition_nonlabor=Integer.parseInt(maps_Prefer_values.get("EndCol"));
								}
								

								// 7.2修改 再创建人工信息---yuan
								setValuefromExcel(revision, maps_Prefer_values
										.get("Sheet"), Integer
										.parseInt(maps_Prefer_values
												.get("StartRow")),
										Integer.parseInt(maps_Prefer_values
												.get("EndRow")), Integer
												.parseInt(maps_Prefer_values.get

												("StartCol")), endposition_labor,
										Integer.parseInt(maps_Prefer_values.get

										("Position")), tool, sheetsAll);

								// 得到首选项的值
								getValueOfExcelByPreference(session,
										YFJC_NonLabor_Position,
										maps.get("GEBT_Version"));
								// 再创建非人工信息
								setValuefromNonLabel(revision, maps_Prefer_values
										.get("Sheet"), Integer
										.parseInt(maps_Prefer_values
												.get("StartRow")),
										Integer.parseInt(maps_Prefer_values
												.get("EndRow")), Integer
												.parseInt(maps_Prefer_values.get

												("StartCol")),
										endposition_nonlabor, Integer
												.parseInt(maps_Prefer_values.get

												("Position")), tool, sheetsAll);

								tool.closeExcelFile(false);

								System.out
										.println("------------------------------删锟斤拷锟斤拷值锟侥凤拷锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷------------------------");

								// System.out.println("Before======delete no value CostInfo=====");

								// ShowCostInfos(revision);

								// 5.27----删锟斤拷锟斤拷值锟矫凤拷锟斤拷锟斤拷息锟斤拷锟斤拷
								//deleteCostInfo_noValue(revision);

								// System.out
								// .println("After======delete no value CostInfo=====");
								// ShowCostInfos(revision);
								
								System.out.println("====create hunman costinfo====");
								System.out.println("====maps_HunmanLabor:"+maps_HunmanLabor);
								System.out.println("====maps_HunmanLabor_yuan:"+maps_HunmanLabor_yuan);
								
								//add by wuwei--2019-06-13
								processHunamCostInfo(maps_HunmanLabor);
								processHunamCostInfo(maps_HunmanLabor_yuan);
								
								System.out.println("lala  create nonlabel costinfo.......");
								//非人工--maps_CostInfo_Non
								for(Entry<String, MyCostInfoBean> entry : maps_CostInfo_Non.entrySet()){
									MyCostInfoBean bean = entry.getValue();
									HashMap<String, String> costBeanMap = hasCostInfoNoValueMap(bean);
									if (costBeanMap.size()>0){
										TCComponent createCostInfo = createCostInfo(session, bean.revision, bean.object_name, bean.type, bean.GroupName, bean.SelectionName, bean.year,bean.costType);
										String array[]=new String[costBeanMap.size()];
										String values[]=new String[costBeanMap.size()];
										int a=0;
										for(Entry<String, String> valueEntry :costBeanMap.entrySet()){
											array[a]=valueEntry.getKey();
											values[a]=valueEntry.getValue();
											a++;
										}
										
										createCostInfo.setProperties(array, values);
										releaseRev(createCostInfo);
									}
								}
								

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
						
						//add by wuwei-- 2019-11-25
						TCComponentItem myProgramItem = revision.getItem();
						String itemPropertArray[]={"jci6_EQU" };
						String values[]={maps.get("EQU")};
						System.out.println("设置ProgramINfo值---jci6_EQU: "+maps.get("EQU")+"  jci6_PDxSeq:"+maps.get("PDx_Squence")+"  jci6_Remark:"+maps.get("remark"));
						
						//new modify by wuwei
						//openByPass();
						//myProgramItem.lock();
						//myProgramItem.setProperties(itemPropertArray, values);
						//myProgramItem.save();
						//closeByPass();
						

						if (isDatasetRelease(dataset)) {
							// 锟斤拷锟斤拷锟斤拷锟斤拷
							releaseRev(revision);
						}
						// }

						System.out
								.println("=================删锟斤拷姹撅拷碌锟斤拷锟斤拷械锟絛ataset===========");
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
								.println("=================锟斤拷dataset锟斤拷拥锟斤拷姹撅拷掳锟絛ataset===========");
						if (isFindDataset == 0) {
							// //openByPass();
							revision.add("IMAN_specification", dataset);
							// closeByPass();
						}
						// 锟斤拷锟紾EBT锟侥硷拷锟角凤拷锟斤拷锟斤拷锟斤拷锟叫ｏ拷锟酵帮拷锟斤拷锟斤拷姹疽诧拷锟接碉拷锟斤拷锟斤拷锟斤拷
						

						// 5.17锟睫革拷---dataset锟斤拷菁锟斤拷欠锟斤拷锟?
						if (isDatasetINProcesssList(dataset)) {
							if (dataset.getCurrentJob() != null) {
								// //openByPass();
								rootTask = dataset.getCurrentJob()
										.getRootTask();
								// closeByPass();

							}
						}
						
						System.out.println("ImportConfirmDialog:: rootTask:"+rootTask);
						
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
						System.out.println("EXCEL IMPORT FINISH");
						// 锟截憋拷锟斤拷锟叫对伙拷锟斤拷锟斤拷锟?
						System.out.println("newRes-->"+newRes);
						if(newRes==0)
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
			 * System.out.println("-------锟芥本锟斤拷锟斤拷没锟叫凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟睫革拷-----");
			 * 
			 * // combo锟斤拷锟斤拷锟斤拷锟斤拷锟矫碉拷锟斤拷锟斤拷锟斤拷
			 * revision.setStringProperty("jci6_PDxSeq",
			 * maps.get("PDx_Squence"));
			 * revision.setStringProperty("jci6_GEBTTemplate",
			 * maps.get("GEBT_Version"));
			 * 
			 * System.out.println("-------锟睫革拷PDx_Squence:"+maps.get("PDx_Squence"
			 * ) +"      锟斤拷GEBT_Version------"+maps.get("GEBT_Version"));
			 * 
			 * // maps.get("EQU") revision.setProperty("jci6_EQU",
			 * maps.get("EQU"));
			 * System.out.println("------锟睫革拷jci6_EQU--------"+maps.get("EQU"));
			 * 
			 * 
			 * String Calc_Date = maps.get("Calc_Date"); if
			 * (!Calc_Date.equals("")) {
			 * revision.setDateProperty("jci6_CalcDate", sdf.parse(Calc_Date));
			 * System
			 * .out.println("----------jci6_CalcDate锟斤拷锟斤拷为锟斤拷"+Calc_Date); }
			 * else { revision.setDateProperty("jci6_CalcDate", null);
			 * System.out
			 * .println("----------jci6_CalcDate锟斤拷锟斤拷为NULL锟斤拷锟斤拷锟斤拷"); }
			 * 
			 * String EQU_Signdate = maps.get("EQU_Signdate"); if
			 * (!EQU_Signdate.equals("")) {
			 * revision.setDateProperty("jci6_EQUSignDate",
			 * sdf.parse(EQU_Signdate));
			 * System.out.println("----------jci6_EQUSignDate锟斤拷锟斤拷为锟斤拷"
			 * +EQU_Signdate); } else {
			 * revision.setDateProperty("jci6_EQUSignDate", null);
			 * System.out.println("---------jci6_CalcDate锟斤拷锟斤拷为NULL锟斤拷锟斤拷锟斤拷");
			 * }
			 * 
			 * // 锟斤拷锟斤拷calculated_by锟斤拷值 TCComponentUserType userType =
			 * (TCComponentUserType) session .getTypeComponent("User");
			 * TCComponentUser user = userType.find(maps.get("calculated_by"));
			 * System
			 * .out.println("---------锟揭碉拷calculated_by锟斤拷USER锟斤拷锟斤拷锟斤拷-----");
			 * revision.setReferenceProperty("jci6_Responsibility", user);
			 * System
			 * .out.println("---------锟睫革拷jci6_Responsibility锟斤拷USER锟斤拷锟斤拷锟斤拷-----"
			 * );
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
			 * System.out.println
			 * ("---------jci6_PDxSignDate锟斤拷锟斤拷为NULL锟斤拷锟斤拷锟斤拷"); }
			 * 
			 * // 4.18锟睫革拷--
			 * 锟芥本锟斤拷current_revision_id锟斤拷锟斤拷锟斤拷锟斤拷PDx_Squence锟斤拷值锟斤拷锟斤拷锟斤拷
			 * revision.setProperty("item_revision_id",
			 * maps.get("PDx_Squence"));
			 * System.out.println("---------item_revision_id锟斤拷锟斤拷为-----"
			 * +maps.get("PDx_Squence"));
			 * 
			 * System.out.println("----锟睫革拷锟斤拷锟斤拷锟斤拷锟?----"); }
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
		// //openByPass();
		TCComponent tcp = rlaType.create(reg.getString("TCM_release"));
		// closeByPass();

		// 锟斤拷锟斤拷TCComponentReleaseStatusType锟斤拷锟斤拷锟?
		// //openByPass();
		tcp.save();
		// closeByPass();

		// 锟斤拷拥锟街革拷锟斤拷陌姹撅拷锟斤拷锟斤拷校锟斤拷蠖ü锟较?
		// //openByPass();
		ir.add("release_status_list", tcp);
		// closeByPass();
	}

	// 锟睫革拷TC锟斤拷锟窖凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟?
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

	// 锟揭碉拷TC系统锟斤拷锟接︼拷锟斤拷锟斤拷锟?
	// 锟剿癸拷锟斤拷息锟斤拷锟斤拷锟斤拷学锟斤拷
	private boolean isSearchvalueofPreference(String value, String properityName) {

		boolean flag = false;
		try {
			// 锟斤拷锟斤拷锟斤拷锟轿拷锟斤拷椤?
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
						new String[]  { "discipline_name" }, //discipline_name
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

	// 创建 ---人工费用信息和非人工费用信息
		private TCComponent createCostInfo(TCSession session,
				TCComponentItemRevision revision, String object_name, int type,
				String GroupName, String SelectionName, int year, String costType)
				throws TCException, ServiceException {
			
			// 创建费用信息
			DataManagementService dmService = DataManagementService
					.getService(session);
			if(year==1900){
				year=2020;
			}
			
			System.out.println("costType-->"+costType);

			// modify by wuwei
			com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[] createIn = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];

			createIn[0] = new DataManagement.CreateIn();

			createIn[0].data.boName = "JCI6_CostInfo";
			// createIn[0].data.stringProps=new HashMap<String, String>();

			ProjectID = revision.getProperty("item_id");
			// 得到时间戳
			String timestamp = getSystemTime();
			String name = "";

			String name_Unit = "";
			if (object_name.equals("人月")) {
				name_Unit = "ManMonth";
			} else if (object_name.equals("小时")) {
				name_Unit = "Hour";
			} else if (object_name.equals("元")) {
				name_Unit = "Yuan";
			}

			// 先得到group的对象,根据对象得到Group的短写名字
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

			// modify by wuwei
			if (type == 1) {
				createIn[0].data.stringProps.put("jci6_CPT", "Budget");
				if(costType==null)
					createIn[0].data.stringProps.put("jci6_CostType", "");
				else
				createIn[0].data.stringProps.put("jci6_CostType", costType.trim());
				createIn[0].data.stringProps.put("jci6_Unit", name_Unit);
				createIn[0].data.intProps.put("jci6_Year", new BigInteger(year+""));
			} else {
				createIn[0].data.stringProps.put("jci6_CPT", "Budget");
				createIn[0].data.stringProps.put("jci6_CostType", "Normal Hours");
				createIn[0].data.stringProps.put("jci6_Unit", name_Unit);
				createIn[0].data.intProps.put("jci6_Year", new BigInteger(year+""));
				//createIn[0].data.stringProps.put("jci6_Division", componentGroup);
			}

			System.out.println("create type:"+type+"  name_Unit-->"+name_Unit+" jci6_Year:"+year+" jci6_CostType:"+costType);
			TCComponent component = null;
			CreateResponse responese = dmService.createObjects(createIn);
			int create_count = responese.serviceData.sizeOfCreatedObjects();
			int create_error_count = responese.serviceData.sizeOfPartialErrors();
			if(create_error_count==0){
				 component = responese.output[0].objects[0];
			}
			else{
				String message = responese.serviceData.getPartialError(0).getMessages()[0];
				System.out.println("error message is "+message);
			}
				

			TCProperty[] property = null;
			if (type == 1) {
				// 非人工费用信息
				// 用TCProperty[]来存储一系列要修改的TC中的属性名称
				/*
				 * //System.out.println("---------创建非人工信息---------");
				 * 
				 * property = component.getTCProperties(new String[] { "jci6_CPT",
				 * "jci6_CostType", "jci6_Unit", "jci6_Year" });
				 * 
				 * property[0].setStringValueData("Budget");
				 * property[1].setStringValueData(costType);
				 * property[2].setStringValueData(name_Unit);
				 * 
				 * // 年是int型 property[3].setIntValueData(year);
				 * component.setTCProperties(property);
				 */

			} else {
				// 人工费用信息
				//System.out.println("---------创建人工信息---------");

				// 用TCProperty[]来存储一系列要修改的TC中的属性名称
//				property = component.getTCProperties(new String[] { "jci6_CPT",
//						"jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Division",
//						"jci6_RateLevel" });
				if(component!=null){
					property = component.getTCProperties(new String[] { "jci6_Division",
					"jci6_RateLevel" });

		//			property[0].setStringValueData("Budget");
		//			property[1].setStringValueData("Normal Hours");
		//			property[2].setStringValueData(name_Unit);
		//
		//			// 年是int型,
		//			property[3].setIntValueData(year);
		
					// 查询指定的组
					property[0].setReferenceValueData(componentGroup);
		
					// //System.out.println("---学科名称-----" + SelectioName);
		
					// 三期ebp修改---by wuwei
					boolean flag_new = false;
					if (SelectionName.equals("Resident Engineer")) {
						//System.out.println("---2014/6/11  Set RateLevel NULL---"+ SelectionName);
								
						flag_new = true;
						property[1].setReferenceValueData((TCComponent) null);
					} else {
						// 查询学科
						TCComponent[] tcp = query(session, "YFJC_Search_Discipline",
								new String[] { "discipline_name" }, //discipline_name
								new String[] { SelectionName });
						if (tcp != null)
							property[1].setReferenceValueData(tcp[0]);
						else{
							//System.out.println("-----查询学科名称没找到！！！-----");
						}
					}
		
					// 5.4修改----人工费用
					component.setTCProperties(property);
					if (flag_new) {
		
						//System.out.println("---2014/6/11  SelectioName---"+ SelectionName);
								
						component.setProperty("jci6_TaskType", "tasktype26");
					}
				}
				

			}

			if(component!=null){
				// 下挂到指定的版本下
				// openByPass();
				revision.add("IMAN_external_object_link", component);
				// closeByPass();

				// 7.2修改---把数据集的项目，指派给新创建的费用信息
				// openByPass();
				// projectComponent.add("project_data", component);
				// closeByPass();

				// component.save();
				component.refresh();
			}
		
			

			return component;
		}
		

	// 删锟斤拷锟斤拷锟斤拷锟较拷锟斤拷锟?
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

	// 5.27----删锟斤拷锟斤拷值锟矫凤拷锟斤拷锟斤拷息锟斤拷锟斤拷
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

	//add by wuwei
	String human_check_row="";
	String non_check_row="";
		
	
	
	// 先检查一遍要读取的值--modify by wuwei
	private void checkExcel(HashMap<String, String> maps,
				JacobEReportTool tool, Dispatch sheetsAll,
				TCComponentItemRevision revision) {

			error_list = new ArrayList<HashMap<String, String>>();
			

			// 在检查的时候读取需要的列的终止位置
			try {
				// 得到首选项的值---YFJC_HumanLabor_Position
				boolean b = getValueOfExcelByPreference(session,
						YFJC_HumanLabor_Position, maps.get("GEBT_Version"));

				if (b) {
					/**
					 * 增加日期为空 ，非人工和人工总数居行的检查
					 */
					//add by wuwei
					
					// 读取配置人工和非人工的首选项
					TCPreferenceService tcpreservice = session.getPreferenceService();
					String[] preString = tcpreservice.getStringArray(
							TCPreferenceService.TC_preference_site, YFJC_Budget_DateCheck);

					for (int i = 0; i < preString.length; i++) {
						String[] split = preString[i].split("=");
						// GEBT版本
						String ver = split[0].trim();
						String[] split2 = ver.split("_");
						if (split2.length == 2) {
							// 找到对应的版本
							// //System.out.println(lov_values + "=====" + split2[1]);
							if (split2[1].equals(maps.get("GEBT_Version"))) {
								//System.out.println("------YFJC_HumanLabor_DateCheck----找到对应的Buget版本 For location："+ ver);
										
								
								if(split.length>1){
									String[] split3 = split[1].split("\\|", -1);
									human_check_row=split3[0];
									if(split3.length>1){
										non_check_row=split3[1];
									}
								}
								break;
							}
							
						}
					}

					//System.out.println("YFJC_Budget_DateCheck------human_check_row:"+ human_check_row+"  non_check_row:"+non_check_row);
							
					
					String SheetName1 = maps_Prefer_values.get("Sheet");
					int StartCol = Integer.parseInt(maps_Prefer_values.get("StartCol"));
					int EndCol = Integer.parseInt(maps_Prefer_values.get("EndCol"));
					int datePosition = Integer.parseInt(maps_Prefer_values.get("Position"));
					
					// 打开excel中的sheet页
					Dispatch sheet = tool.openExcelFile(sheetsAll, SheetName1);
					
					for (int col = StartCol; col <= EndCol; col++) {

						// 用新的方法读取excel,转化成标准日期
						String column = NumToString(col);
						String data_string = tool.getDataFromExcel(column, datePosition,
								sheet);
						
						
						if (data_string.equals("")&&!"".equals(human_check_row)) {
							int row0 = Integer.parseInt(human_check_row);
							String value1 = tool.getDataFromExcel(column, row0,sheet);
							if("".equals(value1)){
								String error_p = SheetName1 + ":" + column + datePosition;
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("error_Date", error_p);
								error_list.add(map);
							}
						} 

					}
					
					
					

					
					
					checkValueOfHumanLaborfromExcel(
							maps_Prefer_values.get("Sheet"),
							Integer.parseInt(maps_Prefer_values.get("StartRow")),
							Integer.parseInt(maps_Prefer_values.get("EndRow")),
							Integer.parseInt(maps_Prefer_values.get("StartCol")),
							Integer.parseInt(maps_Prefer_values.get("EndCol")),
							Integer.parseInt(maps_Prefer_values.get("Position")),
							tool, sheetsAll, revision, maps.get("GEBT_Version"));
				}

				System.out.println("------检查人工费用后的------错误list的数量---"+ error_list.size());
						

				// 7.2修改---- 得到首选项的值---YFJC_HumanLabor_Position
				boolean d = getValueOfExcelByPreference(session,
						YFJC_LaborCostYuan_Position, maps.get("GEBT_Version"));

				if (d) {
					//modify by wuwei
					String SheetName1 = maps_Prefer_values.get("Sheet");
					int StartCol = Integer.parseInt(maps_Prefer_values.get("StartCol"));
					int EndCol = Integer.parseInt(maps_Prefer_values.get("EndCol"));
					int datePosition = Integer.parseInt(maps_Prefer_values.get("Position"));
					
					// 打开excel中的sheet页
					Dispatch sheet = tool.openExcelFile(sheetsAll, SheetName1);
					
					for (int col = StartCol; col <= EndCol; col++) {

						// 用新的方法读取excel,转化成标准日期
						String column = NumToString(col);
						String data_string = tool.getDataFromExcel(column, datePosition,
								sheet);
						
						
						if (data_string.equals("")&&!"".equals(non_check_row)) {
							int row0 = Integer.parseInt(non_check_row);
							String value1 = tool.getDataFromExcel(column, row0,sheet);
							if("".equals(value1)){
								String error_p = SheetName1 + ":" + column + datePosition;
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("error_Date", error_p);
								error_list.add(map);
							}
						} 

					}
					
					
					checkValueOfHumanLaborfromExcel(
							maps_Prefer_values.get("Sheet"),
							Integer.parseInt(maps_Prefer_values.get("StartRow")),
							Integer.parseInt(maps_Prefer_values.get("EndRow")),
							Integer.parseInt(maps_Prefer_values.get("StartCol")),
							Integer.parseInt(maps_Prefer_values.get("EndCol")),
							Integer.parseInt(maps_Prefer_values.get("Position")),
							tool, sheetsAll, revision, maps.get("GEBT_Version"));
				}

				System.out.println("------检查读取人工费用元的excel后的------错误list的数量---"+ error_list.size());
						

				// 得到首选项的值---YFJC_NonLabor_Position
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
				System.out.println("----错误list的数量---" + error_list.size());
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	
	// 锟饺硷拷锟揭伙拷锟揭拷锟饺★拷锟街?
	private void checkExcel_old(HashMap<String, String> maps,
			JacobEReportTool tool, Dispatch sheetsAll,
			TCComponentItemRevision revision) {

		error_list = new ArrayList<HashMap<String, String>>();

		// 在检查的时候读取需要的列的终止位置
		try {
			// 得到首选项的值---YFJC_HumanLabor_Position
			boolean b = getValueOfExcelByPreference(session,
					YFJC_HumanLabor_Position, maps.get("GEBT_Version"));

			if (b) {
				/**
				 * 增加日期为空 ，非人工和人工总数居行的检查
				 */
				//add by wuwei
				
				// 读取配置人工和非人工的首选项
				TCPreferenceService tcpreservice = session.getPreferenceService();
				String[] preString = tcpreservice.getStringArray(
						TCPreferenceService.TC_preference_site, YFJC_Budget_DateCheck);

				for (int i = 0; i < preString.length; i++) {
					String[] split = preString[i].split("=");
					// GEBT版本
					String ver = split[0].trim();
					String[] split2 = ver.split("_");
					if (split2.length == 2) {
						// 找到对应的版本
						// //System.out.println(lov_values + "=====" + split2[1]);
						if (split2[1].equals(maps.get("GEBT_Version"))) {
							//System.out.println("------YFJC_HumanLabor_DateCheck----找到对应的Buget版本 For location："+ ver);
									
							
							if(split.length>1){
								String[] split3 = split[1].split("\\|", -1);
								human_check_row=split3[0];
								if(split3.length>1){
									non_check_row=split3[1];
								}
							}
							break;
						}
						
					}
				}

				//System.out.println("YFJC_Budget_DateCheck------human_check_row:"+ human_check_row+"  non_check_row:"+non_check_row);
						
				
				String SheetName1 = maps_Prefer_values.get("Sheet");
				int StartCol = Integer.parseInt(maps_Prefer_values.get("StartCol"));
				int EndCol = Integer.parseInt(maps_Prefer_values.get("EndCol"));
				int datePosition = Integer.parseInt(maps_Prefer_values.get("Position"));
				
				// 打开excel中的sheet页
				Dispatch sheet = tool.openExcelFile(sheetsAll, SheetName1);
				
				for (int col = StartCol; col <= EndCol; col++) {

					// 用新的方法读取excel,转化成标准日期
					String column = NumToString(col);
					String data_string = tool.getDataFromExcel(column, datePosition,
							sheet);
					
					
					if (data_string.equals("")&&!"".equals(human_check_row)) {
						int row0 = Integer.parseInt(human_check_row);
						String value1 = tool.getDataFromExcel(column, row0,sheet);
						if("".equals(value1)){
							String error_p = SheetName1 + ":" + column + datePosition;
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("error_Date", error_p);
							error_list.add(map);
						}
					} 

				}
				
				
				

				
				
				checkValueOfHumanLaborfromExcel(
						maps_Prefer_values.get("Sheet"),
						Integer.parseInt(maps_Prefer_values.get("StartRow")),
						Integer.parseInt(maps_Prefer_values.get("EndRow")),
						Integer.parseInt(maps_Prefer_values.get("StartCol")),
						Integer.parseInt(maps_Prefer_values.get("EndCol")),
						Integer.parseInt(maps_Prefer_values.get("Position")),
						tool, sheetsAll, revision, maps.get("GEBT_Version"));
			}

			System.out.println("------检查人工费用后的------错误list的数量---"+ error_list.size());
					

			// 7.2修改---- 得到首选项的值---YFJC_HumanLabor_Position
			boolean d = getValueOfExcelByPreference(session,
					YFJC_LaborCostYuan_Position, maps.get("GEBT_Version"));

			if (d) {
				//modify by wuwei
				String SheetName1 = maps_Prefer_values.get("Sheet");
				int StartCol = Integer.parseInt(maps_Prefer_values.get("StartCol"));
				int EndCol = Integer.parseInt(maps_Prefer_values.get("EndCol"));
				int datePosition = Integer.parseInt(maps_Prefer_values.get("Position"));
				
				// 打开excel中的sheet页
				Dispatch sheet = tool.openExcelFile(sheetsAll, SheetName1);
				
				for (int col = StartCol; col <= EndCol; col++) {

					// 用新的方法读取excel,转化成标准日期
					String column = NumToString(col);
					String data_string = tool.getDataFromExcel(column, datePosition,
							sheet);
					
					
					if (data_string.equals("")&&!"".equals(non_check_row)) {
						int row0 = Integer.parseInt(non_check_row);
						String value1 = tool.getDataFromExcel(column, row0,sheet);
						if("".equals(value1)){
							String error_p = SheetName1 + ":" + column + datePosition;
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("error_Date", error_p);
							error_list.add(map);
						}
					} 

				}
				
				
				checkValueOfHumanLaborfromExcel(
						maps_Prefer_values.get("Sheet"),
						Integer.parseInt(maps_Prefer_values.get("StartRow")),
						Integer.parseInt(maps_Prefer_values.get("EndRow")),
						Integer.parseInt(maps_Prefer_values.get("StartCol")),
						Integer.parseInt(maps_Prefer_values.get("EndCol")),
						Integer.parseInt(maps_Prefer_values.get("Position")),
						tool, sheetsAll, revision, maps.get("GEBT_Version"));
			}

			System.out.println("------检查读取人工费用元的excel后的------错误list的数量---"+ error_list.size());
					

			// 得到首选项的值---YFJC_NonLabor_Position
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
			System.out.println("----错误list的数量---" + error_list.size());
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 锟斤拷锟斤拷斯锟斤拷锟斤拷锟街革拷锟絜xcel锟斤拷锟?
	 */
	private void checkValueOfHumanLaborfromExcel(String sheet_name,
			int startRow, int endRow, int starDate, int endDate,
			int datePosition, JacobEReportTool tool, Dispatch sheetsAll,
			TCComponentItemRevision revision, String GEBTVersion)
			throws TCException, IOException {

		System.out
				.println("-----锟斤拷锟斤拷斯锟斤拷锟斤拷锟?------锟矫碉拷锟斤拷选锟斤拷姹厩帮拷锟街?---------");

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
		//System.out.println("ww endposition_labor-->"+endposition_labor);

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

		// 取锟斤拷指锟斤拷锟斤拷锟斤拷锟街?
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

		// 锟斤拷取每一锟叫的第讹拷锟斤拷,锟斤拷锟斤拷锟叫碉拷位锟矫ｏ拷锟斤拷锟斤拷锟叫讹拷锟斤拷锟斤拷值锟斤拷tc锟斤拷锟角凤拷锟斤拷锟?
		// 1.锟斤拷锟叫讹拷锟斤拷锟斤拷锟斤拷锟斤拷知锟斤拷锟斤拷锟?
		// 2.锟斤拷锟斤拷锟斤拷锟斤拷tc锟斤拷锟角凤拷锟斤拷值
		// 3.锟斤拷锟矫伙拷校锟絩eturn锟斤拷锟斤拷
		String data_key_1 = tool.getDataFromExcel("B", row, sheet);
		String data_key_2 = tool.getDataFromExcel("C", row, sheet);

		// System.out.println("-------B锟叫碉拷值======" + data_key_1);

		// 5.8锟睫革拷--锟斤拷锟絜xcel锟斤拷锟叫碉拷锟斤拷锟斤拷为0.锟斤拷锟斤拷锟斤拷一锟叫撅拷锟斤拷锟?
		String height_str = tool.getHeight("B" + row, sheet);
		if (height_str.equals("0.0")) {
			return;
		}

		// System.out.println("data_key_2=====" + data_key_2);
		if (data_key_2.equals("null")) {

			// System.out.println("锟斤拷锟揭伙拷校锟斤拷锟斤拷锟?====" + sheet + ": " + row);
		} else if (data_key_1.equals("null")
				&& data_key_2.equals("Total FTE's")) {

			// System.out.println("锟斤拷锟揭伙拷校锟斤拷锟斤拷锟?====" + sheet + ": " + row);

		} else if (data_key_1.equals("null") && data_key_2.contains("Total")) {
			// System.out.println("锟斤拷锟揭伙拷校锟斤拷锟斤拷锟?====" + sheet + ": " + row);

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

			// 5.6锟睫革拷-----锟斤拷锟絛ata_key_2为"SMTP"锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟介，锟斤拷要锟斤拷锟斤拷选锟斤拷锟斤拷锟斤拷锟矫ｏ拷锟揭碉拷锟斤拷锟斤拷锟窖★拷锟?
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
						// 锟揭碉拷锟斤拷选锟斤拷锟接︼拷锟街?

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

			//修改最新代码---by wuwei
			if(findValue_section.contains("Resident Engineer")){
				b_section =true;
			}


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
					System.out
							.println("------锟斤拷锟斤拷转锟斤拷锟斤拷锟斤拷-----锟斤拷锟斤拷锟斤拷===");
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
	 * 锟斤拷锟揭伙拷蟹锟斤拷斯锟斤拷锟较?
	 */
	private void checkNonLaborByline(JacobEReportTool tool, String sheet_name,
			int row, int starDate, int endDate, String preferenceName,
			Dispatch sheet) throws ServiceException, TCException {

		//System.out.println("-------检查一行非人工信息!!!--------");
		// 取得第一列的值,
		String cellValue = tool.getDataFromExcel("A", row, sheet);

		// 5.8修改--如果excel这行的行数为0.即隐藏一行就跳过。
		String height_str = tool.getHeight("A" + row, sheet);
		if (height_str.equals("0.0")) {
			// //System.out.println("-------非人工信息跳过!!!--------");
			return;
		}

		// 什么时候创建非人工，有讲究————keyOfreference为读取一行excel里的值，到首选项里找是否存在相应的属性
		String findValue = isFindValue(cellValue, session, preferenceName);

		if (findValue.equals("")) {
			return;
		}

		// 得到费用类型--只有非人工信息才有
		// String costType = isFindValue(cellValue, session,
		// preferenceName);

		// if (costType.equals("")) {
		// // 报错
		// String error_p = sheet_name + ":" + "A" + row;
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("error_CostType", error_p);
		// error_list.add(map);
		// }

		// 取每个月的值
		for (int col = starDate; col <= endDate; col++) {

			// 获取每种费用类型的值
			double dCostValue = 0;

			// 用ExcelRW工具读取excel
			String cellCost = tool.getDataFromExcel(NumToString(col), row,
					sheet);

			if (cellCost.equals("null") || cellCost.equals("")) {
				cellCost = "0.0";
			}
			try {
				BigDecimal bd = new BigDecimal(cellCost);
				cellCost = bd.stripTrailingZeros().toPlainString();
			} catch (Exception e) {
				// 报错信息
				//String error_p = sheet_name + ":" + NumToString(col) + row;
				//HashMap<String, String> map = new HashMap<String, String>();
				//map.put("error_num", error_p);
				//error_list.add(map);
				//return;
				cellCost="0.0";
			}

			if (cellCost.matches("^(-?\\d+)(\\.\\d+)?$")) {
				try {
					dCostValue = Double.parseDouble(cellCost);
					// //System.out.println("-----有数字----" + dCostValue);
				} catch (Exception e) {
					// 报错信息
					String error_p = sheet_name + ":" + NumToString(col) + row;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_num", error_p);
					error_list.add(map);
					return;
				}
			} else {
				// 报错信息
				//System.out.println("---数字不对---");
				String error_p = sheet_name + ":" + NumToString(col) + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_num", error_p);
				error_list.add(map);
				return;
			}

		}

	}

	/*
	 * 锟斤拷锟斤拷锟剿癸拷锟斤拷锟斤拷de指锟斤拷excel锟斤拷锟?
	 */
	private void checkValueOfNon_LaborfromExcel(String sheet_name,
			int startRow, int endRow, int starDate, int endDate,
			int datePosition, JacobEReportTool tool, Dispatch sheetsAll,
			String GEBTVersion) throws ServiceException, TCException {

		//System.out.println("----------检查非人工费用-------得到首选项版本以后的值是--------------");
				
		String CostType_name = searchNameOfReference(session,
				YFJC_NonLabor_CostType + "_" + GEBTVersion);
		// //System.out.println("=============CostType_name============="
		// + CostType_name);

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

		// 取得日期
		for (int col = starDate; col <= endDate; col++) {
			// 用新的方法读取excel
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);
			String data;

			if (!data_string.equals("")) {
				try {
					data = sdf.format(new Date(data_string));
				} catch (Exception e) {
					// 报错信息
					String error_p = sheet_name + ":" + column + datePosition;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_Date", error_p);
					error_list.add(map);
					data = "1900-01-01";
				}

				if (!data
						.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")&& data.indexOf("E")==0) {
					// 报错信息
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

		System.out.println("=========6.3修改==endposition_nonlabor==========="
		 + endposition_nonlabor);

		for (int i = startRow; i <= endRow; i++) {
			checkNonLaborByline(tool, sheet_name, i, starDate,
					endposition_nonlabor, nameOfPreference.get("CostType"),
					sheet);
		}

	}

	// 人工信息
	// 读取指定的一行费用信息的值,并且设置进去
		private void setCostInfoValuesByRow(TCComponentItemRevision revision,
				JacobEReportTool tool, int row, int startDate, int endDate,
				HashMap<String, String> month_maps,
				HashMap<String, String> year_maps, Dispatch sheet)
				throws ServiceException, TCException {

			// 读取每一行的第二列,第三列的位置，并且判断属性值在tc中是否存在
			// 1.先判断首先项里知否存在
			// 2.存在再找tc中是否有值
			// 3.如果没有，return结束
			String data_key_1 = tool.getDataFromExcel("B", row, sheet);
			String data_key_2 = tool.getDataFromExcel("C", row, sheet);

			// 5.8修改--如果excel这行的行数为0.即隐藏一行就跳过。
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
				// 5.6-修改--YFJC_HumanLabor_SMTE_GROUP
				Group_value = isFindValue(data_key_2, session,
						YFJC_HumanLabor_SMTE_GROUP);
				// //System.out.println("SMTE---对应得Group！！！");
			} else {

				if (!data_key_1.equals("null")) {
					// 5.8---修改编码==YFJC_HumanLabor_Group
					String findValue_group = isFindValue(data_key_1, session,
							nameOfPreference.get("Group"));
					if (!findValue_group.equals("")) {
						// 找到首选项对应的值

						// 最新修改——group
						if (findValue_group.equals("jci6_ProgramDivi")) {
							TCComponentGroup tcGroup = (TCComponentGroup) revision
									.getReferenceProperty("jci6_ProgramDivi");
							Group_value = tcGroup.getFullName();
							// //System.out.println("-------------Group_Name======"
							// + Group_value);
						} else {
							Group_value = findValue_group;
						}

						map_group.put("Group", Group_value);
					} else {
						// 报错

					}
				} else {
					Group_value = map_group.get("Group");
				}
			}

			String year = year_maps.get(startDate + "");

			// 5.6修改
			String index = Group_value + findValue_section;

			// 取得费用信息里需要的属性
			MyCostInfoBean labor_CostInfo = null;
			MyCostInfoBean labor_CostInfo_hour = null;
			MyCostInfoBean labor_CostInfo_money = null;

			// HashMap<String, TCComponent> maps_CostInfo = new HashMap<String,
			// TCComponent>();

			// maps_CostInfo.put("人月", labor_CostInfo);
			// maps_CostInfo.put("小时", labor_CostInfo_hour);
			// maps_CostInfo.put("元", labor_CostInfo_money);

			// 得到费率BY学科——————jci6_RateLevel
			// String rate_string = "";

			// 7.2修改--第二次创建“元”费用信息
			if (TYPE_HumanLaborYuan == 1) {

			}

			System.out.println("startDate:"+startDate+"  endDate:"+endDate);
			// 取每个月的值
			for (int col = startDate; col <= endDate; col++) {
				if (TYPE_HumanLaborYuan == 0) {
					if (maps_HunmanLabor == null) {
						int YEAR = Integer.parseInt(year);
						maps_HunmanLabor = new HashMap<String, HashMap<String, MyCostInfoBean>>();

						// 创建“人月”费用信息
//						labor_CostInfo = createCostInfo(session, revision, "人月", 2,
//								Group_value, findValue_section, YEAR, null);
//						labor_CostInfo_hour = createCostInfo(session, revision,
//								"小时", 2, Group_value, findValue_section, YEAR, null);

						labor_CostInfo =new MyCostInfoBean( revision, "人月", 2,
								Group_value, findValue_section, YEAR, null);
						labor_CostInfo_hour =new MyCostInfoBean( revision,
								"小时", 2, Group_value, findValue_section, YEAR, null);

						
//						HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
//						maps_CostInfo.put("人月", labor_CostInfo);
//						maps_CostInfo.put("小时", labor_CostInfo_hour);
//						maps_HunmanLabor.put(index + year, maps_CostInfo);
						
						HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
						maps_CostInfo.put("人月", labor_CostInfo);
						maps_CostInfo.put("小时", labor_CostInfo_hour);
						maps_HunmanLabor.put(index + year, maps_CostInfo);
					}
					if (!year.equals(year_maps.get(col + ""))) {
						// 本来要创建新的费用信息，但是如果有了就不需要创建
						year = year_maps.get(col + "");
						boolean flag = false;

						// 5.6修改
						for (String s : maps_HunmanLabor.keySet()) {
							if ((index + year).equals(s)) {
								flag = true;
								break;
							}
						}

						if (!flag) {
							int YEAR = Integer.parseInt(year);
							// 创建“人月”费用信息
							//labor_CostInfo = createCostInfo(session, revision,
							//		"人月", 2, Group_value, findValue_section, YEAR,
							//		null);
							//labor_CostInfo_hour = createCostInfo(session, revision,
							//		"小时", 2, Group_value, findValue_section, YEAR,
							//		null);
							
							labor_CostInfo =new MyCostInfoBean( revision,
									"人月", 2, Group_value, findValue_section, YEAR,
									null);
							
							labor_CostInfo_hour=new MyCostInfoBean( revision,
											"小时", 2, Group_value, findValue_section, YEAR,
											null);

							//HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
							HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
							maps_CostInfo.put("人月", labor_CostInfo);
							maps_CostInfo.put("小时", labor_CostInfo_hour);
							maps_HunmanLabor.put(index + year, maps_CostInfo);
							
							
						} else {
							//System.out.println("====存在costinfo对象===year===" + year+ "   " + index);
									
							labor_CostInfo = maps_HunmanLabor.get(index + year)
									.get("人月");
							labor_CostInfo_hour = maps_HunmanLabor
									.get(index + year).get("小时");

						}
					} else {

						boolean flag = false;
						// 5.6修改
						for (String s : maps_HunmanLabor.keySet()) {
							if ((index + year).equals(s)) {
								flag = true;
								break;
							}
						}

						if (!flag) {
							int YEAR = Integer.parseInt(year);
							// 创建“人月”费用信息
//							labor_CostInfo = createCostInfo(session, revision,
//									"人月", 2, Group_value, findValue_section, YEAR,
//									null);
//							labor_CostInfo_hour = createCostInfo(session, revision,
//									"小时", 2, Group_value, findValue_section, YEAR,
//									null);
							
							labor_CostInfo = new MyCostInfoBean( revision,
									"人月", 2, Group_value, findValue_section, YEAR,
									null);
							labor_CostInfo_hour = new MyCostInfoBean( revision,
									"小时", 2, Group_value, findValue_section, YEAR,
									null);

							//HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
							//maps_CostInfo.put("人月", labor_CostInfo);
							//maps_CostInfo.put("小时", labor_CostInfo_hour);
							//maps_HunmanLabor.put(index + year, maps_CostInfo);
							
							HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
							maps_CostInfo.put("人月", labor_CostInfo);
							maps_CostInfo.put("小时", labor_CostInfo_hour);
							maps_HunmanLabor.put(index + year, maps_CostInfo);
						} else {

							labor_CostInfo = maps_HunmanLabor.get(index + year)
									.get("人月");
							labor_CostInfo_hour = maps_HunmanLabor
									.get(index + year).get("小时");
						}
					}
				} else {
					if (maps_HunmanLabor_yuan == null) {
						int YEAR = Integer.parseInt(year);
						maps_HunmanLabor_yuan = new HashMap<String, HashMap<String, MyCostInfoBean>>();
//						labor_CostInfo_money = createCostInfo(session, revision,
//								"元", 2, Group_value, findValue_section, YEAR, null);
						labor_CostInfo_money = new MyCostInfoBean( revision,
								"元", 2, Group_value, findValue_section, YEAR, null);
						
						
						//HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
						//maps_CostInfo.put("元", labor_CostInfo_money);
						//maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);
						
						HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
						maps_CostInfo.put("元", labor_CostInfo_money);
						maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);
					}

					if (!year.equals(year_maps.get(col + ""))) {
						// 本来要创建新的费用信息，但是如果有了就不需要创建
						year = year_maps.get(col + "");
						boolean flag = false;

						// 5.6修改
						for (String s : maps_HunmanLabor_yuan.keySet()) {
							if ((index + year).equals(s)) {
								flag = true;
								break;
							}
						}
						if (!flag) {// 创建“元”费用信息
							int YEAR = Integer.parseInt(year);
//							labor_CostInfo_money = createCostInfo(session,
//									revision, "元", 2, Group_value,
//									findValue_section, YEAR, null);
							
							labor_CostInfo_money = new MyCostInfoBean(
									revision, "元", 2, Group_value,
									findValue_section, YEAR, null);

							//HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
							HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
							maps_CostInfo.put("元", labor_CostInfo_money);
							maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);

						} else {
							labor_CostInfo_money = maps_HunmanLabor_yuan.get(
									index + year).get("元");

						}
					} else {

						boolean flag = false;
						// 5.6修改
						for (String s : maps_HunmanLabor_yuan.keySet()) {
							if ((index + year).equals(s)) {
								flag = true;
								break;
							}
						}
						if (!flag) {// 创建“元”费用信息
							int YEAR = Integer.parseInt(year);

							//labor_CostInfo_money = createCostInfo(session,
							//		revision, "元", 2, Group_value,
							//		findValue_section, YEAR, null);
							labor_CostInfo_money = new MyCostInfoBean(
									revision, "元", 2, Group_value,
									findValue_section, YEAR, null);

							//HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
							HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
							maps_CostInfo.put("元", labor_CostInfo_money);
							maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);
						} else {

							labor_CostInfo_money = maps_HunmanLabor_yuan.get(
									index + year).get("元");

						}
					}
				}

				// 得到费率BY学科——————jci6_RateLevel
				// if (TYPE_HumanLaborYuan == 0) {
				// TCComponent property = labor_CostInfo
				// .getReferenceProperty("jci6_RateLevel");
				// rate_string = property.getProperty("default_rate");
				// } else {
				// TCComponent property = labor_CostInfo_money
				// .getReferenceProperty("jci6_RateLevel");
				// rate_string = property.getProperty("default_rate");
				// }

				// 获取每种费用类型的值
				// 用ExcelRW读取excel
				String data = tool.getDataFromExcel(NumToString(col), row, sheet);

				if (data.equals("null") || data.equals("")) {
					data = "0.0";
				}
			
				// 增量的值
				double dCostValue = 0;
				try {
					BigDecimal bd = new BigDecimal(data);
					data = bd.stripTrailingZeros().toPlainString();
					
					if(!data.contains(".")){
						data=data+".00";
					}
				} catch (Exception e) {
					// 报错信息
					dCostValue = 0;
				}
				
				// 原来属性的值
				double val = 0;

				if (data.matches("^(-?\\d+)(\\.\\d+)?$")) {
					try {
						dCostValue = Double.parseDouble(data);
						// //System.out.println("-----有数字----" + dCostValue);
					} catch (Exception e) {
						// 报错信息
						dCostValue = 0;
					}
				} else {
					continue;
				}
				// 人工费用的费率
				// Double rate = Double.parseDouble(rate_string);

				// 4.27保留小数点后的两位
				// (dCostValue+"").substring(0,(dCostValue+"").indexOf(".")+3)
				String stringValue = "";
				DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");//格式化设置  

				if (month_maps.get(col + "").equals("01")) {
					// 判断是否为0，不为0截取5位人月，4位小时数，3位元
					if (dCostValue != 0) {
						if (TYPE_HumanLaborYuan == 0) {
							String property_value = labor_CostInfo.jci6_Jan;
									//.getProperty("jci6_Jan");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  jci6_Jan的值不为0===="+ big2( val) + "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
							}
							// stringValue = (dCostValue + "").substring(0,
							// (dCostValue + "").indexOf(".") + 3);
							stringValue = big2(dCostValue + val) + "" + "00";
							//labor_CostInfo.setProperty("jci6_Jan",
							//		cutString(stringValue, 5));
							labor_CostInfo.jci6_Jan=cutString(stringValue, 5);

							String property_value_hour = labor_CostInfo_hour.jci6_Jan;
									//.getProperty("jci6_Jan");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  jci6_Jan的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
												
							}
							stringValue = big2(dCostValue * 170 + val) + "" + "00";
							//labor_CostInfo_hour.setProperty("jci6_Jan",
							//		cutString(stringValue, 4));
							labor_CostInfo_hour.jci6_Jan=cutString(stringValue, 4);

						} else {
							String property_value_money = labor_CostInfo_money.jci6_Jan;
									//.getProperty("jci6_Jan");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);

								//System.out.println("元人工费用信息  jci6_Jan的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
			
												
							}
							stringValue = big2(dCostValue + val )+ "" + "00";
							//labor_CostInfo_money.setProperty("jci6_Jan",
							//		cutString(stringValue, 3));
							labor_CostInfo_money.jci6_Jan=cutString(stringValue, 3);
						}
						

					}
				} else if (month_maps.get(col + "").equals("02")) {
					if (dCostValue != 0) {
						if (TYPE_HumanLaborYuan == 0) {
							String property_value = labor_CostInfo.jci6_Feb;
									//.getProperty("jci6_Feb");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  jci6_Feb的值不为0===="+ val + "+ labor_CostInfo.getProperty("jci6_Year"));  "
										
										
							}
							stringValue = big2( dCostValue + val )+ "" + "00";
							//labor_CostInfo.setProperty("jci6_Feb",
							//		cutString(stringValue, 5));
							labor_CostInfo.jci6_Feb=cutString(stringValue, 5);
							
							
							String property_value_hour = labor_CostInfo_hour.jci6_Feb;
									//.getProperty("jci6_Feb");
							
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  jci6_Feb的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2( dCostValue * 170 + val )+ "" + "00";
							labor_CostInfo_hour.jci6_Feb=cutString(stringValue, 4);
							//labor_CostInfo_hour.setProperty("jci6_Feb",
							//		cutString(stringValue, 4));
						} else {
							String property_value_money = labor_CostInfo_money.jci6_Feb;
									//.getProperty("jci6_Feb");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);
								//System.out.println("元人工费用信息  jci6_Feb的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue =big2( dCostValue + val )+ "" + "00";
							labor_CostInfo_money.jci6_Feb=cutString(stringValue, 3);
							//labor_CostInfo_money.setProperty("jci6_Feb",
							//		cutString(stringValue, 3));
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
							String property_value = labor_CostInfo.jci6_Mar;
									//.getProperty("jci6_Mar");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  jci6_Mar的值不为0===="+ val + "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
							}
							stringValue =big2( dCostValue + val )+ "" + "00";
							//labor_CostInfo.setProperty("jci6_Mar",
							//		cutString(stringValue, 5));
							labor_CostInfo.jci6_Mar=cutString(stringValue, 5);
							
							String property_value_hour = labor_CostInfo_hour.jci6_Mar;
									//.getProperty("jci6_Mar");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  jci6_Mar的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue =big2( dCostValue * 170 + val )+ "" + "00";
							labor_CostInfo_hour.jci6_Mar=cutString(stringValue, 4);
							//labor_CostInfo_hour.setProperty("jci6_Mar",
							//		cutString(stringValue, 4));
						} else {
							String property_value_money = labor_CostInfo_money.jci6_Mar;
									//.getProperty("jci6_Mar");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);
								//System.out.println("元人工费用信息  jci6_Mar的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2( dCostValue + val)+ "" + "00";
							labor_CostInfo_money.jci6_Mar=cutString(stringValue, 3);
							//labor_CostInfo_money.setProperty("jci6_Mar",
							//		cutString(stringValue, 3));
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
							String property_value = labor_CostInfo.jci6_Apr;
									//.getProperty("jci6_Apr");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  jci6_Apr的值不为0===="+ val + "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
							}
							stringValue = big2( dCostValue + val )+ "" + "00";
							labor_CostInfo.jci6_Apr=cutString(stringValue, 5);
							//labor_CostInfo.setProperty("jci6_Apr",
							//		cutString(stringValue, 5));
							
							String property_value_hour = labor_CostInfo_hour.jci6_Apr;
									//.getProperty("jci6_Apr");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  jci6_Apr的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue =big2(  dCostValue * 170 + val )+ "" + "00";
							labor_CostInfo_hour.jci6_Apr=cutString(stringValue, 4);
							//labor_CostInfo_hour.setProperty("jci6_Apr",
							//		cutString(stringValue, 4));
						} else {
							String property_value_money = labor_CostInfo_money.jci6_Apr;
									//.getProperty("jci6_Apr");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);
								//System.out.println("元人工费用信息  jci6_Apr的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
												
							}
							stringValue = big2(dCostValue + val)+ "" + "00";
							labor_CostInfo_money.jci6_Apr=cutString(stringValue, 3);
							//labor_CostInfo_money.setProperty("jci6_Apr",
							//		cutString(stringValue, 3));
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
							String property_value = labor_CostInfo.jci6_May;
									//.getProperty("jci6_May");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  jci6_May的值不为0===="+ val + "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
							}
							stringValue = big2(dCostValue + val )+ "" + "00";
							labor_CostInfo.jci6_May=cutString(stringValue, 5);
							//labor_CostInfo.setProperty("jci6_May",
							//		cutString(stringValue, 5));

							String property_value_hour = labor_CostInfo_hour.jci6_May;
									//.getProperty("jci6_May");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  jci6_May的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
												
							}
							stringValue = big2(dCostValue * 170 + val )+ "" + "00";
							labor_CostInfo_hour.jci6_May=cutString(stringValue, 4);
							//labor_CostInfo_hour.setProperty("jci6_May",
							//		cutString(stringValue, 4));
						} else {
							String property_value_money = labor_CostInfo_money.jci6_May;
									//.getProperty("jci6_May");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);
								//System.out.println("元人工费用信息  jci6_May的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue + val )+ "" + "00";
							labor_CostInfo_money.jci6_May=cutString(stringValue, 3);
							//labor_CostInfo_money.setProperty("jci6_May",
							//		cutString(stringValue, 3));
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
							String property_value = labor_CostInfo.jci6_Jun;
									//.getProperty("jci6_Jun");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  6月的值不为0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
							}
							stringValue = big2(dCostValue + val) + "" + "00";
							labor_CostInfo.jci6_Jun=cutString(stringValue, 5);
							//labor_CostInfo.setProperty("jci6_Jun",
							//		cutString(stringValue, 5));
							
							String property_value_hour = labor_CostInfo_hour.jci6_Jun;
									//.getProperty("jci6_Jun");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  6月的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue * 170 + val) + "" + "00";
							labor_CostInfo_hour.jci6_Jun=cutString(stringValue, 4);
							//labor_CostInfo_hour.setProperty("jci6_Jun",
							//		cutString(stringValue, 4));
						} else {
							String property_value_money = labor_CostInfo_money.jci6_Jun;
									//.getProperty("jci6_Jun");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);
								//System.out.println("元人工费用信息  6月的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue + val )+ "" + "00";
							labor_CostInfo_money.jci6_Jun=cutString(stringValue, 3);
							//labor_CostInfo_money.setProperty("jci6_Jun",
							//		cutString(stringValue, 3));
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
							String property_value = labor_CostInfo.jci6_Jul;
									//.getProperty("jci6_Jul");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  7月的值不为0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
							}
							stringValue = big2(dCostValue + val )+ "" + "00";
							labor_CostInfo.jci6_Jul=cutString(stringValue, 5);
							//labor_CostInfo.setProperty("jci6_Jul",
							//		cutString(stringValue, 5));
							
							String property_value_hour = labor_CostInfo_hour.jci6_Jul;
									//.getProperty("jci6_Jul");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  7月的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue * 170 + val )+ "" + "00";
							labor_CostInfo_hour.jci6_Jul=cutString(stringValue, 4);
							//labor_CostInfo_hour.setProperty("jci6_Jul",
							//		cutString(stringValue, 4));
						} else {
							String property_value_money = labor_CostInfo_money.jci6_Jul;
									//.getProperty("jci6_Jul");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);
								//System.out.println("元人工费用信息  7月的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue + val )+ "" + "00";
							labor_CostInfo_money.jci6_Jul=cutString(stringValue, 3);
							//labor_CostInfo_money.setProperty("jci6_Jul",
							//		cutString(stringValue, 3));
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
							String property_value = labor_CostInfo.jci6_Aug;
									//.getProperty("jci6_Aug");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  8月的值不为0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
							}
							stringValue = big2(dCostValue + val) + "" + "00";
							labor_CostInfo.jci6_Aug=cutString(stringValue, 5);
							//labor_CostInfo.setProperty("jci6_Aug",
							//		cutString(stringValue, 5));
							
							String property_value_hour = labor_CostInfo_hour.jci6_Aug;
									//.getProperty("jci6_Aug");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  8月的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue =big2( dCostValue * 170 + val )+ "" + "00";
							labor_CostInfo_hour.jci6_Aug=cutString(stringValue, 4);
							//labor_CostInfo_hour.setProperty("jci6_Aug",
							//		cutString(stringValue, 4));
						} else {
							String property_value_money = labor_CostInfo_money.jci6_Aug;
									//.getProperty("jci6_Aug");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);
								//System.out.println("元人工费用信息  8月的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue + val )+ "" + "00";
							labor_CostInfo_money.jci6_Aug=cutString(stringValue, 3);
							//labor_CostInfo_money.setProperty("jci6_Aug",
							//		cutString(stringValue, 3));
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
							String property_value = labor_CostInfo.jci6_Sep;
									//.getProperty("jci6_Sep");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  9月的值不为0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
							}
							stringValue =big2( dCostValue + val )+ "" + "00";
							labor_CostInfo.jci6_Sep=cutString(stringValue, 5);
							//labor_CostInfo.setProperty("jci6_Sep",
							//		cutString(stringValue, 5));
							String property_value_hour = labor_CostInfo_hour.jci6_Sep;
									//.getProperty("jci6_Sep");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  9月的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue * 170 + val) + "" + "00";
							labor_CostInfo_hour.jci6_Sep=cutString(stringValue, 4);
							//labor_CostInfo_hour.setProperty("jci6_Sep",
							//		cutString(stringValue, 4));
						} else {
							String property_value_money = labor_CostInfo_money.jci6_Sep;
									//.getProperty("jci6_Sep");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);
								//System.out.println("元人工费用信息  9月的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
												
							}
							stringValue = big2(dCostValue + val) + "" + "00";
							labor_CostInfo_money.jci6_Sep=cutString(stringValue, 3);
							//labor_CostInfo_money.setProperty("jci6_Sep",
							//		cutString(stringValue, 3));
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
							String property_value = labor_CostInfo.jci6_Oct;
									//.getProperty("jci6_Oct");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  10月的值不为0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
							}
							stringValue =big2( dCostValue + val )+ "" + "00";
							labor_CostInfo.jci6_Oct=cutString(stringValue, 5);
							//labor_CostInfo.setProperty("jci6_Oct",
							//		cutString(stringValue, 5));
							
							String property_value_hour = labor_CostInfo_hour.jci6_Oct;
									//.getProperty("jci6_Oct");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  10月的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue * 170 + val )+ "" + "00";
							labor_CostInfo_hour.jci6_Oct=cutString(stringValue, 4);
							//labor_CostInfo_hour.setProperty("jci6_Oct",
							//		cutString(stringValue, 4));
						} else {
							String property_value_money = labor_CostInfo_money.jci6_Oct;
									//.getProperty("jci6_Oct");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);
								//System.out.println("元人工费用信息  10月的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue + val )+ "" + "00";
							labor_CostInfo_money.jci6_Oct=cutString(stringValue, 3);
							//labor_CostInfo_money.setProperty("jci6_Oct",
							//		cutString(stringValue, 3));
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
							String property_value = labor_CostInfo.jci6_Nov;
									//.getProperty("jci6_Nov");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  11月的值不为0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
							}
							stringValue = big2(dCostValue + val )+ "" + "00";
							labor_CostInfo.jci6_Nov=cutString(stringValue, 5);
							//labor_CostInfo.setProperty("jci6_Nov",
							//		cutString(stringValue, 5));
							
							String property_value_hour = labor_CostInfo_hour.jci6_Nov;
									//.getProperty("jci6_Nov");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  11月的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue =big2( dCostValue * 170 + val ) + "" + "00";
							labor_CostInfo_hour.jci6_Nov=cutString(stringValue, 4);
							//labor_CostInfo_hour.setProperty("jci6_Nov",
							//		cutString(stringValue, 4));
						} else {
							String property_value_money = labor_CostInfo_money.jci6_Nov;
									//.getProperty("jci6_Nov");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);
								//System.out.println("元人工费用信息  11月的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue + val) + "" + "00";
							labor_CostInfo_money.jci6_Nov=cutString(stringValue, 3);
							//labor_CostInfo_money.setProperty("jci6_Nov",
							//		cutString(stringValue, 3));
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
							String property_value = labor_CostInfo.jci6_Dec;
									//.getProperty("jci6_Dec");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("人月人工费用信息  12月的值不为0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
							}
							stringValue = big2(dCostValue + val) + "" + "00";
							labor_CostInfo.jci6_Dec=cutString(stringValue, 5);
							//labor_CostInfo.setProperty("jci6_Dec",
							//		cutString(stringValue, 5));
							
							String property_value_hour = labor_CostInfo_hour.jci6_Dec;
									//.getProperty("jci6_Dec");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value_hour);
								//System.out.println("小时人工费用信息  12月的值不为0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue * 170 + val) + "" + "00";
							labor_CostInfo_hour.jci6_Dec=cutString(stringValue, 4);
							//labor_CostInfo_hour.setProperty("jci6_Dec",
							//		cutString(stringValue, 4));
						} else {
							String property_value_money = labor_CostInfo_money.jci6_Dec;
									//.getProperty("jci6_Dec");
							if (property_value_money != null
									&& !property_value_money.equals("")) {
								val = Double.parseDouble(property_value_money);
								//System.out.println("元人工费用信息  12月的值不为0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
							}
							stringValue = big2(dCostValue + val) + "" + "00";
							labor_CostInfo_money.jci6_Dec=cutString(stringValue, 3);
							//labor_CostInfo_money.setProperty("jci6_Dec",
							//		cutString(stringValue, 3));
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
	
		// 读取人工费用指定的行的excel表格
		private void setValuefromExcel(TCComponentItemRevision revision,
				String sheet_name, int startRow, int endRow, int starDate,
				int endDate, int datePosition, JacobEReportTool tool,
				Dispatch sheetsAll) throws TCException, IOException {

			//System.out.println("-----读取人工费用-------");

			Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

			HashMap<String, String> month_maps = new HashMap<String, String>();
			HashMap<String, String> year_maps = new HashMap<String, String>();

			// 取得日期
			for (int col = starDate; col <= endDate; col++) {
				// 用新的方法读取excel
				String column = NumToString(col);
				String data_string = tool.getDataFromExcel(column, datePosition,
						sheet);
				String data;
				try {
					data = sdf.format(new Date(data_string));
				} catch (Exception e) {
					// 报错信息
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

			// 取得指定行数的值
			for (int i = startRow; i <= endRow; i++) {
				setCostInfoValuesByRow(revision, tool, i, starDate, endDate,
						month_maps, year_maps, sheet);
			}

		}

	// 锟斤拷取锟斤拷锟剿癸拷锟斤拷锟矫碉拷excel锟斤拷锟?
	private void setValuefromNonLabel(TCComponentItemRevision revision,
			String sheet_name, int startRow, int endRow, int startDate,
			int endDate, int datePosition, JacobEReportTool tool,
			Dispatch sheetsAll) throws ServiceException, TCException {
		//System.out.println("-----读取非人工费用-------");

		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		HashMap<String, String> month_maps_non = new HashMap<String, String>();
		HashMap<String, String> year_maps_non = new HashMap<String, String>();

		// 取得日期
		for (int col = startDate; col <= endDate; col++) {
			// 用新的方法读取excel
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);
			String data="";
			try{
				 data = sdf.format(new Date(data_string));
			} catch (Exception e) {
				data = "1900-01-01";
			}

			// 是否为标准日期
			if (data.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
				String year = data.substring(0, data.indexOf("-"));
				year_maps_non.put(col + "", year);
				// //System.out.println("date------" + year_maps_non.get(col +
				// ""));
				String month = data.substring(data.indexOf("-") + 1,
						data.lastIndexOf("-"));
				month_maps_non.put(col + "", month);

			}

		}

		for (int i = startRow; i <= endRow; i++) {
			readNonLabor(revision, tool, i, startDate, endDate,
					YFJC_NonLabor_CostType, month_maps_non, year_maps_non,
					sheet);
		}

	}
	
	private String isFindValue2(String key, TCSession tcsession,
			String preferenceName) {
		String value = "";

		HashMap<String, String> hashMap = getTCPreferenceArray2(tcsession,
				preferenceName);

		for (String keyValue : hashMap.keySet()) {
			// 找到对应的版本

			// 相同
			if (keyValue.trim().equals(key.trim())) {
				value = hashMap.get(keyValue);
				//System.out.println("2014/6/11===找到了对应首选项的值----名字：" + keyValue+ "  value的值====" + value);
				break;	

			} else {
				if (keyValue.equals(key)) {
					value = hashMap.get(keyValue);
					//System.out.println("2014/6/11===找到了对应首选项的值----名字：" + key+ "  value的值====" + value);
					break;

				}

			}
		}
		return value;
	}

	// 锟斤拷锟剿癸拷锟斤拷息
	// 锟斤拷取一锟斤拷锟斤拷息锟侥凤拷锟斤拷
	private void readNonLabor(TCComponentItemRevision revision,
			JacobEReportTool tool, int row, int startDate, int endDate,
			String preferenceName, HashMap<String, String> month_maps_non,
			HashMap<String, String> year_maps_non, Dispatch sheet)
			throws ServiceException, TCException {

		// 取得第一列的值,用ExcelRW
		String cellValue = tool.getDataFromExcel("A", row, sheet);

		// 5.8修改--如果excel这行的行数为0.即隐藏一行就跳过。
		String height_str = tool.getHeight("A" + row, sheet);
		if (height_str.equals("0.0")) {
			return;
		}

		// 设置日期年初值
		String year = year_maps_non.get(startDate + "");

		// 什么时候创建非人工，有讲究————keyOfreference为读取一行excel里的值，到首选项里找是否存在相应的属性
		// String findValue = isFindValue(cellValue, session, preferenceName);

		// 得到费用类型--只有非人工信息才有----YFJC_NonLabor_CostType
		String findValue = isFindValue2(cellValue, session,
				nameOfPreference.get("CostType"));

		String costType = findValue;
		if (findValue.equals("")) {
			return;
		}

		//System.out.println("\n读取非人工单元格----第一列的值---->" + cellValue+ "   findValue---->" + findValue + "   costType---->"+ costType);
				
				

		// 需要设置的费用信息
		MyCostInfoBean component_costInfo;

		// 取每个月的值
		for (int col = startDate; col <= endDate; col++) {

			if (!year.equals(year_maps_non.get(col + ""))) {
				// 本来要创建新的费用信息，但是如果有了就不需要创建
				year = year_maps_non.get(col + "");
				boolean flag = false;

				for (String s : maps_CostInfo_Non.keySet()) {
					if ((findValue + year).equals(s)) {
						flag = true;
						break;
					}
				}

				if (!flag) {
					int Year = Integer.parseInt(year);
					//TCComponent createCostInfo = createCostInfo(session,
					//		revision, "元", 1, null, null, Year, costType);
					MyCostInfoBean createCostInfo=new MyCostInfoBean(
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
					maps_CostInfo_Non = new HashMap<String, MyCostInfoBean>();
					//TCComponent createCostInfo = createCostInfo(session,
					//		revision, "元", 1, null, null, Year, costType);
					MyCostInfoBean createCostInfo=new MyCostInfoBean(
							revision, "元", 1, null, null, Year, costType);
					
					maps_CostInfo_Non.put(findValue + year, createCostInfo);
					component_costInfo = createCostInfo;

				} else {
					boolean flag = false;
					for (String s : maps_CostInfo_Non.keySet()) {
						if ((findValue + year).equals(s)) {
							flag = true;
							break;
						}
					}
					// 如果hashmap里没有
					if (!flag) {

						int Year = Integer.parseInt(year);
						// 创建费用信息
						//TCComponent createCostInfo = createCostInfo(session,
						//		revision, "元", 1, null, null, Year, costType);
						MyCostInfoBean createCostInfo=new MyCostInfoBean(
										revision, "元", 1, null, null, Year, costType);
						
						maps_CostInfo_Non.put(findValue + year, createCostInfo);
						component_costInfo = createCostInfo;
					} else {
						component_costInfo = maps_CostInfo_Non.get(findValue
								+ year);
					}
				}

				// 获取每种费用类型的值
				double dCostValue = 0;
				double val = 0;

				// 用ExcelRW工具读取excel
				String cellCost = tool.getDataFromExcel(NumToString(col), row,
						sheet);

				if (cellCost.equals("null") || cellCost.equals("")) {
					cellCost = "0.0";
				}

				try {
					// modify by wuwei- 2018/12/4
					BigDecimal bd = new BigDecimal(cellCost);
					cellCost = bd.stripTrailingZeros().toPlainString();
					
				} catch (Exception e) {
					// 填写报错信息
					dCostValue = 0;
				}
				
				//modify by wuwei--非人工00
				if(cellCost.contains(".")){
					cellCost = cellCost + "00";
				}else{
					cellCost = cellCost + ".000";
				}

				
				
				//System.out.println("--readNonLabor---有数字cellCost--->" + cellCost);
				//DecimalFormat decimalFormat = new DecimalFormat("#,##0.000");//格式化设置  
				
				if (cellCost.matches("^(-?\\d+)(\\.\\d+)?$")) {
					try {
						dCostValue = Double.parseDouble(cellCost);
						//System.out.println("--readNonLabor--有数字----" + big2(dCostValue));
					} catch (Exception e) {
						e.printStackTrace();
						// 填写报错信息
						dCostValue = 0;
					}
				} else {
					continue;
				}

				String stringValue = "";

				// 4.27保留费用信息“元”小数点后的3位 ((dCostValue+
				// val)+"").substring(0,((dCostValue+ val)+"").indexOf(".")+3)

				if (month_maps_non.get(col + "").equals("01")) {
					String property = component_costInfo.jci6_Jan;
							//.getProperty("jci6_Jan");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue =big2(dCostValue + val)  + "" + "00";
						component_costInfo.jci6_Jan=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_Jan",
						//		cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("02")) {
					String property = component_costInfo.jci6_Feb;
							//.getProperty("jci6_Feb");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = big2(dCostValue + val) + "" + "00";
						// component_costInfo.setProperty("jci6_Feb",
						// cutString(stringValue, 3));
						
						component_costInfo.jci6_Feb=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_Feb",
						//		cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("03")) {
					String property = component_costInfo.jci6_Mar;
							//.getProperty("jci6_Mar");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = big2(dCostValue + val) + "" + "00";
						component_costInfo.jci6_Mar=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_Mar",
						//		cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("04")) {
					String property = component_costInfo.jci6_Apr;
							//.getProperty("jci6_Apr");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = big2(dCostValue + val) + "" + "00";
						component_costInfo.jci6_Apr=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_Apr",
						//		cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("05")) {
					String property = component_costInfo.jci6_May;
							//.getProperty("jci6_May");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = big2(dCostValue + val) + "" + "00";
						component_costInfo.jci6_May=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_May",
						//		cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("06")) {
					String property = component_costInfo.jci6_Jun;
							//.getProperty("jci6_Jun");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = big2(dCostValue + val) + "" + "00";
						component_costInfo.jci6_Jun=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_Jun",
						//		cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("07")) {
					String property = component_costInfo.jci6_Jul;
							//.getProperty("jci6_Jul");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = big2(dCostValue + val) + "" + "00";
						component_costInfo.jci6_Jul=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_Jul",
						//		cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("08")) {
					String property = component_costInfo.jci6_Aug;
							//.getProperty("jci6_Aug");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = big2(dCostValue + val) + "" + "00";
						component_costInfo.jci6_Aug=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_Aug",
						//		cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("09")) {
					String property = component_costInfo.jci6_Sep;
							//.getProperty("jci6_Sep");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = big2(dCostValue + val) + "" + "00";
						component_costInfo.jci6_Sep=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_Sep",
						//		cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("10")) {
					String property = component_costInfo.jci6_Oct;
							//.getProperty("jci6_Oct");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = big2(dCostValue + val) + "" + "00";
						component_costInfo.jci6_Oct=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_Oct",
						//		cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("11")) {
					String property = component_costInfo.jci6_Nov;
							//.getProperty("jci6_Nov");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = big2(dCostValue + val) + "" + "00";
						component_costInfo.jci6_Nov=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_Nov",
						//		cutString(stringValue, 3));
					}
				} else if (month_maps_non.get(col + "").equals("12")) {
					String property = component_costInfo.jci6_Dec;
							//.getProperty("jci6_Dec");
					if (property == null || property.equals("")) {

					} else {
						val = Double.parseDouble(property);
					}

					if ((dCostValue + val) != 0) {
						// stringValue = ((dCostValue + val) + "").substring(0,
						// ((dCostValue + val) + "").indexOf(".") + 3);
						stringValue = big2(dCostValue + val) + "" + "00";
						component_costInfo.jci6_Dec=cutString(stringValue, 3);
						//component_costInfo.setProperty("jci6_Dec",
						//		cutString(stringValue, 3));
					}
				}

			}
		}

	}
	
	// 获得首选项的值以“=”分隔，组成hashmap
		private HashMap<String, String> getTCPreferenceArray2(TCSession tcsession,
				String preferenceName) {

			TCPreferenceService tcpreservice = tcsession.getPreferenceService();
			String[] preString = tcpreservice.getStringArray(
					TCPreferenceService.TC_preference_site, preferenceName);
			HashMap<String, String> map_refer = new HashMap<String, String>();

			for (int i = 0; i < preString.length; i++) {
				String[] split = preString[i].split("=");
				// GEBT版本
				String ver = split[0].trim();
				map_refer.put(ver, split[1].trim());
			}
			return map_refer;
		}

	// 锟斤拷锟斤拷选锟斤拷锟斤拷去锟斤拷锟斤拷锟皆碉拷值
	private String isFindValue(String key, TCSession tcsession,
			String preferenceName) {
		String value = "";

		HashMap<String, String> hashMap = getTCPreferenceArray2(tcsession,
				preferenceName);

		for (String keyValue : hashMap.keySet()) {
			// 找到对应的版本
			String[] strings = keyValue.split(" ",-1);
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
				// 相同
				if (flag) {
					value = hashMap.get(keyValue);
					//System.out.println("2014/6/11===找到了对应首选项的值----名字："+ keyValue + "  value的值====" + value);
							

				}

			} else {
				if (keyValue.equals(key)) {
					value = hashMap.get(keyValue);
					//System.out.println("2014/6/11===找到了对应首选项的值----名字：" + key+ "  value的值====" + value);
							

				}

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

	// 锟铰斤拷锟斤拷询 "学锟斤拷锟斤拷锟?锟侥癸拷锟斤拷---锟皆讹拷锟斤拷锟窖拷锟斤拷锟?
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
	 * colNameToNum::锟斤拷锟斤拷锟斤拷锟斤拷业锟斤拷锟斤拷锟斤拷锟?
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
		// lov_values值为“V1”，“V2”

		String index = null;

		// 读取配置人工和非人工的首选项
		TCPreferenceService tcpreservice = tcsession.getPreferenceService();

		String[] preString = tcpreservice.getStringArray(
				TCPreferenceService.TC_preference_site, preferenceName);

		HashMap<String, String> map = new HashMap<String, String>();

		System.out.println("preString.length:"+preString.length);
		for (int i = 0; i < preString.length; i++) {
			String[] split = preString[i].split("=");
			// GEBT版本
			String ver = split[0].trim();
			String[] split2 = ver.split("_");
			if (split2.length == 2) {
				// 找到对应的版本
				// //System.out.println(lov_values + "=====" + split2[1]);
				if (split2[1].equals(lov_values)) {
					index = ver;
					map.put(ver, split[1].trim());
					//System.out.println("----------找到对应的GEBT版本 For location："+ ver);
							
				}
			}
		}

		// 如果没找到，就放进error_list里面
		if (index == null) {
			// combo_version.setEnabled(true);
			index = "EQU";
			// 报错信息
			String error_msg = "Not found related version from preference_setting!!!";
			HashMap<String, String> map_msg = new HashMap<String, String>();
			map_msg.put("error_msg", error_msg);
			error_list.add(map_msg);
			return false;
		} else {

			// 读取相应的值，找到excel中的cell
			String sheet = map.get(index).substring(0,
					map.get(index).indexOf("{"));
			// 页的名字
			maps_Prefer_values.put("Sheet", sheet.trim());

			String row = map.get(index).substring(
					map.get(index).indexOf("}") + 1,
					map.get(index).indexOf("||"));
			String[] split_row = row.split("-");
			// 起始行和终止行
			maps_Prefer_values.put("StartRow", split_row[0]);
			maps_Prefer_values.put("EndRow", split_row[1]);

			// 日期的行的位置
			String pos = map.get(index).substring(
					map.get(index).lastIndexOf("|") + 1,
					map.get(index).indexOf("("));
			maps_Prefer_values.put("Position", pos);

			// 得到日期的所在列
			String Date = map.get(index).substring(
					map.get(index).indexOf("(") + 1,
					map.get(index).indexOf(")"));
			String[] split_date = Date.split("-");

			maps_Prefer_values
					.put("StartCol", colNameToNum(split_date[0]) + "");
			maps_Prefer_values.put("EndCol", colNameToNum(split_date[1]) + "");
			//endposition_labor=colNameToNum(split_date[1]);
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
			str = NumToString((Num / 26) - ((f == 26) ? 1 : 0)) + str;
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
	
	
	public  void contentToTxt(String filePath, String errmessage,String warnmessage, String warningMessage,String errorMessage) {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath),true));
            if (!errmessage.equals("")) {
            	writer.append(errorMessage + "\r\n");
    			writer.append(errmessage + "\r\n");
    		}
            
            if (!warnmessage.equals("")) {
            	writer.append(warningMessage + "\r\n");
				writer.append(warnmessage);
    		}
            
            writer.flush();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
	
	private  String big2(double d) {
		NumberFormat nf = NumberFormat.getInstance(); 
														
		// 默认true以逗号隔开,如[123,456,789.128]
		nf.setGroupingUsed(false);
		String formatValue = nf.format(d);
		if(formatValue.contains(".")){
			formatValue=formatValue+"00";
		}else{
			formatValue=formatValue+".00";
		}
														
		return formatValue;
	}
	
	private void processHunamCostInfo(HashMap<String, HashMap<String, MyCostInfoBean>> maps_HunmanLabor) throws TCException, ServiceException {
		// TODO Auto-generated method stub
		for (Entry<String, HashMap<String, MyCostInfoBean>> allEntry : maps_HunmanLabor.entrySet()) {
			HashMap<String, MyCostInfoBean> hashMap = allEntry.getValue();
			for (Entry<String, MyCostInfoBean> entry : hashMap.entrySet()) {
				MyCostInfoBean bean = entry.getValue();
				HashMap<String, String> costBeanMap = hasCostInfoNoValueMap(bean);
				if (costBeanMap.size()>0){
					TCComponent createCostInfo = createCostInfo(session, bean.revision, bean.object_name, bean.type, bean.GroupName, bean.SelectionName, bean.year,bean.costType);
					String array[]=new String[costBeanMap.size()];
					String values[]=new String[costBeanMap.size()];
					int a=0;
					for(Entry<String, String> valueEntry :costBeanMap.entrySet()){
						array[a]=valueEntry.getKey();
						values[a]=valueEntry.getValue();
						a++;
					}
					
					createCostInfo.setProperties(array, values);
					releaseRev(createCostInfo);
				}
			}
		}
	}
	
	private HashMap<String,String> hasCostInfoNoValueMap(MyCostInfoBean component) throws TCException {
		HashMap<String,String> hashmap=new HashMap<String,String>();
		String jci6_Jan_str = component.jci6_Jan;
		String jci6_Feb_str = component.jci6_Feb;
		String jci6_Mar_str = component.jci6_Mar;
		String jci6_Apr_str = component.jci6_Apr;
		String jci6_May_str = component.jci6_May;
		String jci6_Jun_str = component.jci6_Jun;
		String jci6_Jul_str = component.jci6_Jul;
		String jci6_Aug_str = component.jci6_Aug;
		String jci6_Sep_str = component.jci6_Sep;
		String jci6_Oct_str = component.jci6_Oct;
		String jci6_Nov_str = component.jci6_Nov;
		String jci6_Dec_str = component.jci6_Dec;

		
		if (!"".equals(jci6_Jan_str.trim())){
			hashmap.put("jci6_Jan", jci6_Jan_str);
		}
		
		if (!"".equals(jci6_Feb_str.trim())){
			hashmap.put("jci6_Feb", jci6_Feb_str);
		}
		
		if (!"".equals(jci6_Mar_str.trim())){
			hashmap.put("jci6_Mar", jci6_Mar_str);
		}
		
		if (!"".equals(jci6_Apr_str.trim())){
			hashmap.put("jci6_Apr", jci6_Apr_str);
		}
		
		if (!"".equals(jci6_May_str.trim())){
			hashmap.put("jci6_May", jci6_May_str);
		}
		
		if (!"".equals(jci6_Jun_str.trim())){
			hashmap.put("jci6_Jun", jci6_Jun_str);
		}
		
		if (!"".equals(jci6_Jul_str.trim())){
			hashmap.put("jci6_Jul", jci6_Jul_str);
		}
		
		if (!"".equals(jci6_Aug_str.trim())){
			hashmap.put("jci6_Aug", jci6_Aug_str);
		}
		
		if (!"".equals(jci6_Sep_str.trim())){
			hashmap.put("jci6_Sep", jci6_Sep_str);
		}
		
		if (!"".equals(jci6_Oct_str.trim())){
			hashmap.put("jci6_Oct", jci6_Oct_str);
		}
		
		if (!"".equals(jci6_Nov_str.trim())){
			hashmap.put("jci6_Nov", jci6_Nov_str);
		}
		
		if (!"".equals(jci6_Dec_str.trim())){
			hashmap.put("jci6_Dec", jci6_Dec_str);
		}
		
		
		return hashmap;

	}
}
