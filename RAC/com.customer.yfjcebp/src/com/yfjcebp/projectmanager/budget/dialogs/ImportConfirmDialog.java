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
 #	2013-3-11	liuc  		Ini		���ǵ����SWT�Ի���						   
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

	// ��ѡ����
	// 4.18---�޸ķ��˹�����Ϣ�������ѡ���Ϊ�����ֵΪCostType��ֵ����ͬ�͸��¶����½�
	private static String YFJC_NonLabor_CostType = "YFJC_NonLabor_CostType";
	private static String YFJC_NonLabor_Position = "YFJC_NonLabor_Position";
	private static String YFJC_HumanLabor_Position = "YFJC_HumanLabor_Position";
	private static String YFJC_HumanLabor_Group = "YFJC_HumanLabor_Group";
	// 5.4--�޸��˹�������Ϣ�������ѡ������?--YFJC_HumanLabor_RateLevel
	private static String YFJC_HumanLabor_Section = "YFJC_HumanLabor_RateLevel";
	// 5.6--�޸�������һ����ѡ��---�ҵ��˹����õ�SMTE����
	private static String YFJC_HumanLabor_SMTE_GROUP = "YFJC_HumanLabor_SMTE_GROUP";
	private String YFJC_LaborCostYuan_Position = "YFJC_LabourCost_Position";
	// excel�ļ���
	private static String ExcelNameMode = "GEBT.xlsm";

	// ������˹���costInfo������Ϣ
	private HashMap<String, MyCostInfoBean> maps_CostInfo_Non;
	// 5.6�޸�----�����˹���costInfo������Ϣ
	HashMap<String, HashMap<String, MyCostInfoBean>> maps_HunmanLabor;
	HashMap<String, HashMap<String, MyCostInfoBean>> maps_HunmanLabor_yuan;
	// ������ѡ��������
	private HashMap<String, String> maps_Prefer_values = new HashMap<String, String>();

	// �����˹����õ�������?
	private HashMap<String, String> map_group = new HashMap<String, String>();

	// 4.1����
	// ����error�ĶԻ�����Ҫ��ֵ
	private ArrayList<HashMap<String, String>> error_list = null;

	// 4.10����
	private String ProjectID;

	// JacobEReportTool tool = new JacobEReportTool();

	// 6.5����----�洢Group,ѧ�ƣ��Լ�CostType
	private HashMap<String, String> nameOfPreference = new HashMap<String, String>();

	// 5.31�޸�----����Ϊ�վͲ������˼���ִ��
	int endposition_labor = 0;
	int endposition_nonlabor = 0;
	private TCComponent projectComponent;
	// 7.2�޸�--���� �˹�������Ϣ�Ķ�ȡ---Ԫ���ټ�����Ƕ�ȡָ��ҳ�������
	int TYPE_HumanLaborYuan = 0;
	
	//add by wuwei
	private static String YFJC_Budget_DateCheck = "YFJC_Budget_DateCheck";
			
	

	public ImportConfirmDialog(final Display display,
			final TCComponentItemRevision revision,
			final HashMap<String, String> maps, final TCSession session,
			final int isF, final TCComponentDataset dataset,
			TCComponent projectComponent) {

		obj[0] = "open bypass";// ��·����Ҫ�ı���������?

		shell = new Shell(display);
		shell.setSize(500, 465);
		shell.setText(reg.getString("dialog_Confirm"));
		this.session = session;
		this.isF = isF;
		this.dataset = dataset;
		this.revision = revision;
		this.projectComponent = projectComponent;
		// ע������,SWT.NULTI������ѡ�����?SWT.FULL_SELECTION����������ѡ��
		// ,SWT.BORDER�߿�SWT.V_SCROLL
		// ,SWT.H_SCROLL������
		TableViewer tableViewer = new TableViewer(shell, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(30, 20, 420, 330);

		// ����button
		btn_ok = new Button(shell, SWT.NONE);
		btn_ok.setBounds(90, 360, 100, 30);
		btn_ok.setText(reg.getString("btn_import"));

		btn_cancel = new Button(shell, SWT.NONE);
		btn_cancel.setBounds(300, 360, 100, 30);
		btn_cancel.setText(reg.getString("btn_cancel"));

		// ������
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

		// ���õ�һ�е�ֵ
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
			// ���ô��ݹ�����ֵ��һ��д��ָ����Ԫ��
			// ԭֵ
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

			// ��ʾȥ������ġ�?��
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

					// �رյ�ǰ����
					shell.dispose();
				}
			});

			btn_ok.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					/**
					 * 2014-9-25 add by wuh  ����COC����ز���
					 */
					COCOperation coc  = new COCOperation(session,revision,maps.get("GEBT_Version"));
					try {
						coc.executeOperation();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					/**
					 * 2014-9-25 add by wuh  ����COC����ز���
					 */
					
					JacobEReportTool tool = new JacobEReportTool();
					int isClose = 0;
					int newRes=0;
					try {
						// ���汾�Ѿ���������ֱ���޸ģ�Ҫ����·
						// if
						// (revision.getProperty("release_status_list").equals(
						// reg.getString("TCM_release"))) {
						//
						// // ����Ԥ��˵���ִ��?TBL
						// if (isF == 2) {
						// try {
						// // �õ�ϵͳ����ʱ�ļ�
						// String getenv = System.getenv("TEMP");
						// String TC_path = System.getenv("TPR");
						// tool.addDir(TC_path + "\\plugins");
						// // �õ����е�sheet
						// Dispatch sheetsAll = tool.getSheets(getenv
						// + File.separator + ExcelNameMode);
						//
						// // �ȼ��һ��Ҫ��ȡ���?
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
						// // ��ɾ��汾�µġ�������ϧn�����?
						// deleteCostInfo(revision);
						// // ���½�������Ϣ
						// // ��ȡexcel������ݣ���д��������ϧn���?
						//
						// // �õ���ѡ����?--YFJC_HumanLabor_Position
						// getValueOfExcelByPreference(session,
						// YFJC_HumanLabor_Position,
						// maps.get("GEBT_Version"));
						// // �ٴ����˹���Ϣ
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
						// // �õ���ѡ����?--YFJC_NonLabor_Position
						// getValueOfExcelByPreference(session,
						// YFJC_NonLabor_Position,
						// maps.get("GEBT_Version"));
						//
						// System.out
						// .println("-----------��ʼ�������˹�����Ŀ��Ϣ������--------");
						// // �ٴ������˹���Ϣ
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
						// .println("------------------------------ɾ����ֵ�ķ�����Ϣ������------------------------");
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
						// ִ��TBL����
						TCComponentTask rootTask = null;

						// 5.17�޸�---dataset���ݼ��Ƿ���
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
								// ���½�������Ϣ
								// ��ȡexcel������ݣ���д��������ϧn���?
								// �õ�ϵͳ����ʱ�ļ�
								String getenv = System.getenv("TEMP");
								String TC_path = System.getenv("TPR");
								tool.addDir(TC_path + "\\plugins");
								// �õ����е�sheet
								Dispatch sheetsAll = tool.getSheets(getenv
										+ File.separator + ExcelNameMode);

								System.out.println("====checkExcel   start====");
								// �ȼ��һ��Ҫ��ȡ���?
								checkExcel(maps, tool, sheetsAll, revision);
								System.out.println("====checkExcel   end===="+error_list.size());
								
								if (error_list.size() != 0) {
									newRes++;
									isClose = 1;
									// �ر�EXCEL���?
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
									//����txt���ݼ����ѱ�����Ϣ�ҵ�������
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
								// ɾ�������ϧn�����?
								deleteCostInfo(revision);
								closeByPass();

								// �õ���ѡ����?--YFJC_HumanLabor_Position
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


								
								// �ٴ����˹���Ϣ
								// �ٴ����˹���Ϣ
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

								// �õ���ѡ����?--YFJC_HumanLabor_Position
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
								

								// 7.2�޸� �ٴ����˹���Ϣ---yuan
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

								// �õ���ѡ���ֵ
								getValueOfExcelByPreference(session,
										YFJC_NonLabor_Position,
										maps.get("GEBT_Version"));
								// �ٴ������˹���Ϣ
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
										.println("------------------------------ɾ����ֵ�ķ�����Ϣ������------------------------");

								// System.out.println("Before======delete no value CostInfo=====");

								// ShowCostInfos(revision);

								// 5.27----ɾ����ֵ�÷�����Ϣ����
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
								//���˹�--maps_CostInfo_Non
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

						// �޸�ָ���汾������
						setDataOfRev(revision, maps);
						
						//add by wuwei-- 2019-11-25
						TCComponentItem myProgramItem = revision.getItem();
						String itemPropertArray[]={"jci6_EQU" };
						String values[]={maps.get("EQU")};
						System.out.println("����ProgramINfoֵ---jci6_EQU: "+maps.get("EQU")+"  jci6_PDxSeq:"+maps.get("PDx_Squence")+"  jci6_Remark:"+maps.get("remark"));
						
						//new modify by wuwei
						//openByPass();
						//myProgramItem.lock();
						//myProgramItem.setProperties(itemPropertArray, values);
						//myProgramItem.save();
						//closeByPass();
						

						if (isDatasetRelease(dataset)) {
							// ��������
							releaseRev(revision);
						}
						// }

						System.out
								.println("=================ɾ��汾�µ����е�dataset===========");
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
								.println("=================��dataset��ӵ��汾�°�dataset===========");
						if (isFindDataset == 0) {
							// //openByPass();
							revision.add("IMAN_specification", dataset);
							// closeByPass();
						}
						// ���GEBT�ļ��Ƿ��������У��Ͱ�����汾Ҳ��ӵ�������
						

						// 5.17�޸�---dataset��ݼ��Ƿ���?
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
						// �ر����жԻ������?
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

	// �޸ĵ�ԭ��������
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
			 * System.out.println("-------�汾����û�з����������޸�-----");
			 * 
			 * // combo���������õ�������
			 * revision.setStringProperty("jci6_PDxSeq",
			 * maps.get("PDx_Squence"));
			 * revision.setStringProperty("jci6_GEBTTemplate",
			 * maps.get("GEBT_Version"));
			 * 
			 * System.out.println("-------�޸�PDx_Squence:"+maps.get("PDx_Squence"
			 * ) +"      ��GEBT_Version------"+maps.get("GEBT_Version"));
			 * 
			 * // maps.get("EQU") revision.setProperty("jci6_EQU",
			 * maps.get("EQU"));
			 * System.out.println("------�޸�jci6_EQU--------"+maps.get("EQU"));
			 * 
			 * 
			 * String Calc_Date = maps.get("Calc_Date"); if
			 * (!Calc_Date.equals("")) {
			 * revision.setDateProperty("jci6_CalcDate", sdf.parse(Calc_Date));
			 * System
			 * .out.println("----------jci6_CalcDate����Ϊ��"+Calc_Date); }
			 * else { revision.setDateProperty("jci6_CalcDate", null);
			 * System.out
			 * .println("----------jci6_CalcDate����ΪNULL������"); }
			 * 
			 * String EQU_Signdate = maps.get("EQU_Signdate"); if
			 * (!EQU_Signdate.equals("")) {
			 * revision.setDateProperty("jci6_EQUSignDate",
			 * sdf.parse(EQU_Signdate));
			 * System.out.println("----------jci6_EQUSignDate����Ϊ��"
			 * +EQU_Signdate); } else {
			 * revision.setDateProperty("jci6_EQUSignDate", null);
			 * System.out.println("---------jci6_CalcDate����ΪNULL������");
			 * }
			 * 
			 * // ����calculated_by��ֵ TCComponentUserType userType =
			 * (TCComponentUserType) session .getTypeComponent("User");
			 * TCComponentUser user = userType.find(maps.get("calculated_by"));
			 * System
			 * .out.println("---------�ҵ�calculated_by��USER������-----");
			 * revision.setReferenceProperty("jci6_Responsibility", user);
			 * System
			 * .out.println("---------�޸�jci6_Responsibility��USER������-----"
			 * );
			 * 
			 * // maps.get("approved_amount")
			 * revision.setProperty("jci6_SignedMoney",
			 * maps.get("approved_amount"));
			 * System.out.println("---------�޸�jci6_SignedMoney��ֵ------"
			 * +maps.get("approved_amount"));
			 * 
			 * String PDx_Sign_Date = maps.get("PDx_Sign_Date"); if
			 * (!PDx_Sign_Date.equals("")) {
			 * revision.setDateProperty("jci6_PDxSignDate",
			 * sdf.parse(PDx_Sign_Date));
			 * System.out.println("----------jci6_PDxSignDate����Ϊ��"
			 * +PDx_Sign_Date); } else {
			 * revision.setDateProperty("jci6_PDxSignDate", null);
			 * System.out.println
			 * ("---------jci6_PDxSignDate����ΪNULL������"); }
			 * 
			 * // 4.18�޸�--
			 * �汾��current_revision_id��������PDx_Squence��ֵ������
			 * revision.setProperty("item_revision_id",
			 * maps.get("PDx_Squence"));
			 * System.out.println("---------item_revision_id����Ϊ-----"
			 * +maps.get("PDx_Squence"));
			 * 
			 * System.out.println("----�޸��������?----"); }
			 */
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// ����·
	private void openByPass() {
		try {
			session.getUserService().call("open_or_close_pass",
					new Object[] { 1 });

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// �ر���·
	private void closeByPass() {
		try {
			session.getUserService().call("open_or_close_pass",
					new Object[] { 0 });
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// �����汾������
	private void releaseRev(TCComponent ir) throws TCException {
		// �ȵõ�ReleaseStatusType
		TCComponentReleaseStatusType rlaType = (TCComponentReleaseStatusType) session
				.getTypeComponent("ReleaseStatus");

		// ����ϵͳ�Ŀ��ٷ������̡�������"TCM �ѷ���"
		// //openByPass();
		TCComponent tcp = rlaType.create(reg.getString("TCM_release"));
		// closeByPass();

		// ����TCComponentReleaseStatusType�����?
		// //openByPass();
		tcp.save();
		// closeByPass();

		// ��ӵ�ָ���İ汾�����У��󶨹��?
		// //openByPass();
		ir.add("release_status_list", tcp);
		// closeByPass();
	}

	// �޸�TC���ѷ������������?
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

	// �ҵ�TCϵͳ���Ӧ������?
	// �˹���Ϣ������ѧ��
	private boolean isSearchvalueofPreference(String value, String properityName) {

		boolean flag = false;
		try {
			// �������Ϊ����?
			if (properityName.equals("Group")) {
				TCComponentGroupType groupType = (TCComponentGroupType) session
						.getTypeComponent(properityName);

				TCComponentGroup componentGroup = groupType.find(value);
				if (componentGroup != null) {
					flag = true;
				}
			} else if (properityName.equals("Section")) {
				// ��ѯѧ��
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

	// ���� ---�˹�������Ϣ�ͷ��˹�������Ϣ
		private TCComponent createCostInfo(TCSession session,
				TCComponentItemRevision revision, String object_name, int type,
				String GroupName, String SelectionName, int year, String costType)
				throws TCException, ServiceException {
			
			// ����������Ϣ
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
			// �õ�ʱ���
			String timestamp = getSystemTime();
			String name = "";

			String name_Unit = "";
			if (object_name.equals("����")) {
				name_Unit = "ManMonth";
			} else if (object_name.equals("Сʱ")) {
				name_Unit = "Hour";
			} else if (object_name.equals("Ԫ")) {
				name_Unit = "Yuan";
			}

			// �ȵõ�group�Ķ���,���ݶ���õ�Group�Ķ�д����
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
				// ���˹�������Ϣ
				// ��TCProperty[]���洢һϵ��Ҫ�޸ĵ�TC�е���������
				/*
				 * //System.out.println("---------�������˹���Ϣ---------");
				 * 
				 * property = component.getTCProperties(new String[] { "jci6_CPT",
				 * "jci6_CostType", "jci6_Unit", "jci6_Year" });
				 * 
				 * property[0].setStringValueData("Budget");
				 * property[1].setStringValueData(costType);
				 * property[2].setStringValueData(name_Unit);
				 * 
				 * // ����int�� property[3].setIntValueData(year);
				 * component.setTCProperties(property);
				 */

			} else {
				// �˹�������Ϣ
				//System.out.println("---------�����˹���Ϣ---------");

				// ��TCProperty[]���洢һϵ��Ҫ�޸ĵ�TC�е���������
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
		//			// ����int��,
		//			property[3].setIntValueData(year);
		
					// ��ѯָ������
					property[0].setReferenceValueData(componentGroup);
		
					// //System.out.println("---ѧ������-----" + SelectioName);
		
					// ����ebp�޸�---by wuwei
					boolean flag_new = false;
					if (SelectionName.equals("Resident Engineer")) {
						//System.out.println("---2014/6/11  Set RateLevel NULL---"+ SelectionName);
								
						flag_new = true;
						property[1].setReferenceValueData((TCComponent) null);
					} else {
						// ��ѯѧ��
						TCComponent[] tcp = query(session, "YFJC_Search_Discipline",
								new String[] { "discipline_name" }, //discipline_name
								new String[] { SelectionName });
						if (tcp != null)
							property[1].setReferenceValueData(tcp[0]);
						else{
							//System.out.println("-----��ѯѧ������û�ҵ�������-----");
						}
					}
		
					// 5.4�޸�----�˹�����
					component.setTCProperties(property);
					if (flag_new) {
		
						//System.out.println("---2014/6/11  SelectioName---"+ SelectionName);
								
						component.setProperty("jci6_TaskType", "tasktype26");
					}
				}
				

			}

			if(component!=null){
				// �¹ҵ�ָ���İ汾��
				// openByPass();
				revision.add("IMAN_external_object_link", component);
				// closeByPass();

				// 7.2�޸�---�����ݼ�����Ŀ��ָ�ɸ��´����ķ�����Ϣ
				// openByPass();
				// projectComponent.add("project_data", component);
				// closeByPass();

				// component.save();
				component.refresh();
			}
		
			

			return component;
		}
		

	// ɾ�������ϧn�����?
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

	// 5.27----ɾ����ֵ�÷�����Ϣ����
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

	// 5.27----ɾ����ֵ�÷�����Ϣ����
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

	// �ж��û�ѡ�����ݼ��Ƿ񷢲�
	private boolean isDatasetRelease(TCComponentDataset dataset)
			throws TCException {
		String release = dataset.getProperty("release_status_list");
		if (!release.equals(reg.getString("TCM_release")))
			return false;
		else
			return true;
	}

	// 5.17---- �ж��û�ѡ�����ݼ��Ƿ���������
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
		
	
	
	// �ȼ��һ��Ҫ��ȡ��ֵ--modify by wuwei
	private void checkExcel(HashMap<String, String> maps,
				JacobEReportTool tool, Dispatch sheetsAll,
				TCComponentItemRevision revision) {

			error_list = new ArrayList<HashMap<String, String>>();
			

			// �ڼ���ʱ���ȡ��Ҫ���е���ֹλ��
			try {
				// �õ���ѡ���ֵ---YFJC_HumanLabor_Position
				boolean b = getValueOfExcelByPreference(session,
						YFJC_HumanLabor_Position, maps.get("GEBT_Version"));

				if (b) {
					/**
					 * ��������Ϊ�� �����˹����˹��������еļ��
					 */
					//add by wuwei
					
					// ��ȡ�����˹��ͷ��˹�����ѡ��
					TCPreferenceService tcpreservice = session.getPreferenceService();
					String[] preString = tcpreservice.getStringArray(
							TCPreferenceService.TC_preference_site, YFJC_Budget_DateCheck);

					for (int i = 0; i < preString.length; i++) {
						String[] split = preString[i].split("=");
						// GEBT�汾
						String ver = split[0].trim();
						String[] split2 = ver.split("_");
						if (split2.length == 2) {
							// �ҵ���Ӧ�İ汾
							// //System.out.println(lov_values + "=====" + split2[1]);
							if (split2[1].equals(maps.get("GEBT_Version"))) {
								//System.out.println("------YFJC_HumanLabor_DateCheck----�ҵ���Ӧ��Buget�汾 For location��"+ ver);
										
								
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
					
					// ��excel�е�sheetҳ
					Dispatch sheet = tool.openExcelFile(sheetsAll, SheetName1);
					
					for (int col = StartCol; col <= EndCol; col++) {

						// ���µķ�����ȡexcel,ת���ɱ�׼����
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

				System.out.println("------����˹����ú��------����list������---"+ error_list.size());
						

				// 7.2�޸�---- �õ���ѡ���ֵ---YFJC_HumanLabor_Position
				boolean d = getValueOfExcelByPreference(session,
						YFJC_LaborCostYuan_Position, maps.get("GEBT_Version"));

				if (d) {
					//modify by wuwei
					String SheetName1 = maps_Prefer_values.get("Sheet");
					int StartCol = Integer.parseInt(maps_Prefer_values.get("StartCol"));
					int EndCol = Integer.parseInt(maps_Prefer_values.get("EndCol"));
					int datePosition = Integer.parseInt(maps_Prefer_values.get("Position"));
					
					// ��excel�е�sheetҳ
					Dispatch sheet = tool.openExcelFile(sheetsAll, SheetName1);
					
					for (int col = StartCol; col <= EndCol; col++) {

						// ���µķ�����ȡexcel,ת���ɱ�׼����
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

				System.out.println("------����ȡ�˹�����Ԫ��excel���------����list������---"+ error_list.size());
						

				// �õ���ѡ���ֵ---YFJC_NonLabor_Position
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
				System.out.println("----����list������---" + error_list.size());
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	
	// �ȼ��һ��Ҫ��ȡ���?
	private void checkExcel_old(HashMap<String, String> maps,
			JacobEReportTool tool, Dispatch sheetsAll,
			TCComponentItemRevision revision) {

		error_list = new ArrayList<HashMap<String, String>>();

		// �ڼ���ʱ���ȡ��Ҫ���е���ֹλ��
		try {
			// �õ���ѡ���ֵ---YFJC_HumanLabor_Position
			boolean b = getValueOfExcelByPreference(session,
					YFJC_HumanLabor_Position, maps.get("GEBT_Version"));

			if (b) {
				/**
				 * ��������Ϊ�� �����˹����˹��������еļ��
				 */
				//add by wuwei
				
				// ��ȡ�����˹��ͷ��˹�����ѡ��
				TCPreferenceService tcpreservice = session.getPreferenceService();
				String[] preString = tcpreservice.getStringArray(
						TCPreferenceService.TC_preference_site, YFJC_Budget_DateCheck);

				for (int i = 0; i < preString.length; i++) {
					String[] split = preString[i].split("=");
					// GEBT�汾
					String ver = split[0].trim();
					String[] split2 = ver.split("_");
					if (split2.length == 2) {
						// �ҵ���Ӧ�İ汾
						// //System.out.println(lov_values + "=====" + split2[1]);
						if (split2[1].equals(maps.get("GEBT_Version"))) {
							//System.out.println("------YFJC_HumanLabor_DateCheck----�ҵ���Ӧ��Buget�汾 For location��"+ ver);
									
							
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
				
				// ��excel�е�sheetҳ
				Dispatch sheet = tool.openExcelFile(sheetsAll, SheetName1);
				
				for (int col = StartCol; col <= EndCol; col++) {

					// ���µķ�����ȡexcel,ת���ɱ�׼����
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

			System.out.println("------����˹����ú��------����list������---"+ error_list.size());
					

			// 7.2�޸�---- �õ���ѡ���ֵ---YFJC_HumanLabor_Position
			boolean d = getValueOfExcelByPreference(session,
					YFJC_LaborCostYuan_Position, maps.get("GEBT_Version"));

			if (d) {
				//modify by wuwei
				String SheetName1 = maps_Prefer_values.get("Sheet");
				int StartCol = Integer.parseInt(maps_Prefer_values.get("StartCol"));
				int EndCol = Integer.parseInt(maps_Prefer_values.get("EndCol"));
				int datePosition = Integer.parseInt(maps_Prefer_values.get("Position"));
				
				// ��excel�е�sheetҳ
				Dispatch sheet = tool.openExcelFile(sheetsAll, SheetName1);
				
				for (int col = StartCol; col <= EndCol; col++) {

					// ���µķ�����ȡexcel,ת���ɱ�׼����
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

			System.out.println("------����ȡ�˹�����Ԫ��excel���------����list������---"+ error_list.size());
					

			// �õ���ѡ���ֵ---YFJC_NonLabor_Position
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
			System.out.println("----����list������---" + error_list.size());
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * ����˹�����ָ��excel���?
	 */
	private void checkValueOfHumanLaborfromExcel(String sheet_name,
			int startRow, int endRow, int starDate, int endDate,
			int datePosition, JacobEReportTool tool, Dispatch sheetsAll,
			TCComponentItemRevision revision, String GEBTVersion)
			throws TCException, IOException {

		System.out
				.println("-----����˹�����?------�õ���ѡ��汾ǰ���?---------");

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
		// .println("----------------------�õ���ѡ��汾�Ժ��ֵ-----------------");
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

		// ��excel�е�sheetҳ
		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		endposition_labor = endDate;
		//System.out.println("ww endposition_labor-->"+endposition_labor);

		// ȡ������
		for (int col = starDate; col <= endDate; col++) {
			// ���µķ�����ȡexcel,ת���ɱ�׼����
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);
			String data;

			if (!data_string.equals("")) {
				try {
					data = sdf.format(new Date(data_string));
				} catch (Exception e) {
					// ������Ϣ
					String error_p = sheet_name + ":" + column + datePosition;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_Date", error_p);
					error_list.add(map);
					data = "1900-01-01";
				}

				if (!data
						.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
					// ������Ϣ
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

		// System.out.println("==6.3�޸�========================="
		// + endposition_labor);

		// ȡ��ָ��������?
		for (int i = startRow; i <= endRow; i++) {
			checkHumanLaborByline(tool, sheet_name, i, starDate,
					endposition_labor, sheet, revision,
					nameOfPreference.get("Section"),
					nameOfPreference.get("Group"));
		}

	}

	/*
	 * ���һ���˹�����sheet
	 */
	private void checkHumanLaborByline(JacobEReportTool tool,
			String sheet_name, int row, int starDate, int endDate,
			Dispatch sheet, TCComponentItemRevision revision,
			String Section_name, String Group_name) throws ServiceException,
			TCException {

		// ��ȡÿһ�еĵڶ���,�����е�λ�ã������ж�����ֵ��tc���Ƿ����?
		// 1.���ж���������֪�����?
		// 2.��������tc���Ƿ���ֵ
		// 3.���û�У�return����
		String data_key_1 = tool.getDataFromExcel("B", row, sheet);
		String data_key_2 = tool.getDataFromExcel("C", row, sheet);

		// System.out.println("-------B�е�ֵ======" + data_key_1);

		// 5.8�޸�--���excel���е�����Ϊ0.������һ�о����?
		String height_str = tool.getHeight("B" + row, sheet);
		if (height_str.equals("0.0")) {
			return;
		}

		// System.out.println("data_key_2=====" + data_key_2);
		if (data_key_2.equals("null")) {

			// System.out.println("���һ�У�����?====" + sheet + ": " + row);
		} else if (data_key_1.equals("null")
				&& data_key_2.equals("Total FTE's")) {

			// System.out.println("���һ�У�����?====" + sheet + ": " + row);

		} else if (data_key_1.equals("null") && data_key_2.contains("Total")) {
			// System.out.println("���һ�У�����?====" + sheet + ": " + row);

		} else {
			// System.out.println("data_key_2=====" + data_key_2);
			String findValue_section = null;
			String Group_value = null;

			findValue_section = isFindValue(data_key_2, session, Section_name);
			if (findValue_section.equals("")) {
				// ����
				String error_p = sheet_name + ":" + "C" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Section", error_p);
				error_list.add(map);
			}

			// 5.6�޸�-----���data_key_2Ϊ"SMTP"���������������飬��Ҫ����ѡ�������ã��ҵ������ѡ��?
			if (data_key_2.equals("SMTE")) {
				// 5.6-�޸�
				Group_value = isFindValue(data_key_2, session,
						YFJC_HumanLabor_SMTE_GROUP);
				// System.out.println("SMTE=======" + Group_value);
			} else {
				if (!data_key_1.equals("null")) {
					// 5.8---�޸ı���
					String findValue_group = isFindValue(data_key_1, session,
							Group_name);
					if (!findValue_group.equals("")) {
						// �ҵ���ѡ���Ӧ���?

						// �����޸ġ���group
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
						// ����
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
				// ϵͳ��û�ж�Ӧ��ֵ
				// ����
				String error_p = sheet_name + ":" + "B" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Group", error_p);
				error_list.add(map);
			}

			boolean b_section = isSearchvalueofPreference(findValue_section,
					"Section");

			//�޸����´���---by wuwei
			if(findValue_section.contains("Resident Engineer")){
				b_section =true;
			}


			if (!b_section && !findValue_section.equals("")) {
				// ϵͳ��û�ж�Ӧ��ֵ
				// ����
				String error_p = sheet_name + ":" + "C" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Section", error_p);
				error_list.add(map);
			}

			// ȡÿ���µ�ֵ
			for (int col = starDate; col <= endDate; col++) {

				// ��ȡÿ�ַ������͵�ֵ
				// ��ExcelRW��ȡexcel
				String data = tool.getDataFromExcel(NumToString(col), row,
						sheet);

				if (data.equals("null") || data.equals("")) {
					data = "0.0";
				}

				double dCostValue;

				// if (data.matches("^(-?\\d+)(\\.\\d+)?$")) {
				try {
					dCostValue = Double.parseDouble(data);
					// System.out.println("-----������----" + dCostValue);
				} catch (Exception e) {
					// ������Ϣ
					System.out
							.println("------����ת������-----������===");
					String error_p = sheet_name + ":" + NumToString(col) + row;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_num", error_p);
					error_list.add(map);
					return;
				}
				// } else {
				// // ������Ϣ
				// System.out.println("----------�������ֲ��ԣ���--��----");
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
	 * ���һ�з��˹����?
	 */
	private void checkNonLaborByline(JacobEReportTool tool, String sheet_name,
			int row, int starDate, int endDate, String preferenceName,
			Dispatch sheet) throws ServiceException, TCException {

		//System.out.println("-------���һ�з��˹���Ϣ!!!--------");
		// ȡ�õ�һ�е�ֵ,
		String cellValue = tool.getDataFromExcel("A", row, sheet);

		// 5.8�޸�--���excel���е�����Ϊ0.������һ�о�������
		String height_str = tool.getHeight("A" + row, sheet);
		if (height_str.equals("0.0")) {
			// //System.out.println("-------���˹���Ϣ����!!!--------");
			return;
		}

		// ʲôʱ�򴴽����˹����н�����������keyOfreferenceΪ��ȡһ��excel���ֵ������ѡ�������Ƿ������Ӧ������
		String findValue = isFindValue(cellValue, session, preferenceName);

		if (findValue.equals("")) {
			return;
		}

		// �õ���������--ֻ�з��˹���Ϣ����
		// String costType = isFindValue(cellValue, session,
		// preferenceName);

		// if (costType.equals("")) {
		// // ����
		// String error_p = sheet_name + ":" + "A" + row;
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("error_CostType", error_p);
		// error_list.add(map);
		// }

		// ȡÿ���µ�ֵ
		for (int col = starDate; col <= endDate; col++) {

			// ��ȡÿ�ַ������͵�ֵ
			double dCostValue = 0;

			// ��ExcelRW���߶�ȡexcel
			String cellCost = tool.getDataFromExcel(NumToString(col), row,
					sheet);

			if (cellCost.equals("null") || cellCost.equals("")) {
				cellCost = "0.0";
			}
			try {
				BigDecimal bd = new BigDecimal(cellCost);
				cellCost = bd.stripTrailingZeros().toPlainString();
			} catch (Exception e) {
				// ������Ϣ
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
					// //System.out.println("-----������----" + dCostValue);
				} catch (Exception e) {
					// ������Ϣ
					String error_p = sheet_name + ":" + NumToString(col) + row;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_num", error_p);
					error_list.add(map);
					return;
				}
			} else {
				// ������Ϣ
				//System.out.println("---���ֲ���---");
				String error_p = sheet_name + ":" + NumToString(col) + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_num", error_p);
				error_list.add(map);
				return;
			}

		}

	}

	/*
	 * �����˹�����deָ��excel���?
	 */
	private void checkValueOfNon_LaborfromExcel(String sheet_name,
			int startRow, int endRow, int starDate, int endDate,
			int datePosition, JacobEReportTool tool, Dispatch sheetsAll,
			String GEBTVersion) throws ServiceException, TCException {

		//System.out.println("----------�����˹�����-------�õ���ѡ��汾�Ժ��ֵ��--------------");
				
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

		// ȡ������
		for (int col = starDate; col <= endDate; col++) {
			// ���µķ�����ȡexcel
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);
			String data;

			if (!data_string.equals("")) {
				try {
					data = sdf.format(new Date(data_string));
				} catch (Exception e) {
					// ������Ϣ
					String error_p = sheet_name + ":" + column + datePosition;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_Date", error_p);
					error_list.add(map);
					data = "1900-01-01";
				}

				if (!data
						.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")&& data.indexOf("E")==0) {
					// ������Ϣ
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

		System.out.println("=========6.3�޸�==endposition_nonlabor==========="
		 + endposition_nonlabor);

		for (int i = startRow; i <= endRow; i++) {
			checkNonLaborByline(tool, sheet_name, i, starDate,
					endposition_nonlabor, nameOfPreference.get("CostType"),
					sheet);
		}

	}

	// �˹���Ϣ
	// ��ȡָ����һ�з�����Ϣ��ֵ,�������ý�ȥ
		private void setCostInfoValuesByRow(TCComponentItemRevision revision,
				JacobEReportTool tool, int row, int startDate, int endDate,
				HashMap<String, String> month_maps,
				HashMap<String, String> year_maps, Dispatch sheet)
				throws ServiceException, TCException {

			// ��ȡÿһ�еĵڶ���,�����е�λ�ã������ж�����ֵ��tc���Ƿ����
			// 1.���ж���������֪�����
			// 2.��������tc���Ƿ���ֵ
			// 3.���û�У�return����
			String data_key_1 = tool.getDataFromExcel("B", row, sheet);
			String data_key_2 = tool.getDataFromExcel("C", row, sheet);

			// 5.8�޸�--���excel���е�����Ϊ0.������һ�о�������
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
				// 5.6-�޸�--YFJC_HumanLabor_SMTE_GROUP
				Group_value = isFindValue(data_key_2, session,
						YFJC_HumanLabor_SMTE_GROUP);
				// //System.out.println("SMTE---��Ӧ��Group������");
			} else {

				if (!data_key_1.equals("null")) {
					// 5.8---�޸ı���==YFJC_HumanLabor_Group
					String findValue_group = isFindValue(data_key_1, session,
							nameOfPreference.get("Group"));
					if (!findValue_group.equals("")) {
						// �ҵ���ѡ���Ӧ��ֵ

						// �����޸ġ���group
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
						// ����

					}
				} else {
					Group_value = map_group.get("Group");
				}
			}

			String year = year_maps.get(startDate + "");

			// 5.6�޸�
			String index = Group_value + findValue_section;

			// ȡ�÷�����Ϣ����Ҫ������
			MyCostInfoBean labor_CostInfo = null;
			MyCostInfoBean labor_CostInfo_hour = null;
			MyCostInfoBean labor_CostInfo_money = null;

			// HashMap<String, TCComponent> maps_CostInfo = new HashMap<String,
			// TCComponent>();

			// maps_CostInfo.put("����", labor_CostInfo);
			// maps_CostInfo.put("Сʱ", labor_CostInfo_hour);
			// maps_CostInfo.put("Ԫ", labor_CostInfo_money);

			// �õ�����BYѧ�ơ�����������jci6_RateLevel
			// String rate_string = "";

			// 7.2�޸�--�ڶ��δ�����Ԫ��������Ϣ
			if (TYPE_HumanLaborYuan == 1) {

			}

			System.out.println("startDate:"+startDate+"  endDate:"+endDate);
			// ȡÿ���µ�ֵ
			for (int col = startDate; col <= endDate; col++) {
				if (TYPE_HumanLaborYuan == 0) {
					if (maps_HunmanLabor == null) {
						int YEAR = Integer.parseInt(year);
						maps_HunmanLabor = new HashMap<String, HashMap<String, MyCostInfoBean>>();

						// ���������¡�������Ϣ
//						labor_CostInfo = createCostInfo(session, revision, "����", 2,
//								Group_value, findValue_section, YEAR, null);
//						labor_CostInfo_hour = createCostInfo(session, revision,
//								"Сʱ", 2, Group_value, findValue_section, YEAR, null);

						labor_CostInfo =new MyCostInfoBean( revision, "����", 2,
								Group_value, findValue_section, YEAR, null);
						labor_CostInfo_hour =new MyCostInfoBean( revision,
								"Сʱ", 2, Group_value, findValue_section, YEAR, null);

						
//						HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
//						maps_CostInfo.put("����", labor_CostInfo);
//						maps_CostInfo.put("Сʱ", labor_CostInfo_hour);
//						maps_HunmanLabor.put(index + year, maps_CostInfo);
						
						HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
						maps_CostInfo.put("����", labor_CostInfo);
						maps_CostInfo.put("Сʱ", labor_CostInfo_hour);
						maps_HunmanLabor.put(index + year, maps_CostInfo);
					}
					if (!year.equals(year_maps.get(col + ""))) {
						// ����Ҫ�����µķ�����Ϣ������������˾Ͳ���Ҫ����
						year = year_maps.get(col + "");
						boolean flag = false;

						// 5.6�޸�
						for (String s : maps_HunmanLabor.keySet()) {
							if ((index + year).equals(s)) {
								flag = true;
								break;
							}
						}

						if (!flag) {
							int YEAR = Integer.parseInt(year);
							// ���������¡�������Ϣ
							//labor_CostInfo = createCostInfo(session, revision,
							//		"����", 2, Group_value, findValue_section, YEAR,
							//		null);
							//labor_CostInfo_hour = createCostInfo(session, revision,
							//		"Сʱ", 2, Group_value, findValue_section, YEAR,
							//		null);
							
							labor_CostInfo =new MyCostInfoBean( revision,
									"����", 2, Group_value, findValue_section, YEAR,
									null);
							
							labor_CostInfo_hour=new MyCostInfoBean( revision,
											"Сʱ", 2, Group_value, findValue_section, YEAR,
											null);

							//HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
							HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
							maps_CostInfo.put("����", labor_CostInfo);
							maps_CostInfo.put("Сʱ", labor_CostInfo_hour);
							maps_HunmanLabor.put(index + year, maps_CostInfo);
							
							
						} else {
							//System.out.println("====����costinfo����===year===" + year+ "   " + index);
									
							labor_CostInfo = maps_HunmanLabor.get(index + year)
									.get("����");
							labor_CostInfo_hour = maps_HunmanLabor
									.get(index + year).get("Сʱ");

						}
					} else {

						boolean flag = false;
						// 5.6�޸�
						for (String s : maps_HunmanLabor.keySet()) {
							if ((index + year).equals(s)) {
								flag = true;
								break;
							}
						}

						if (!flag) {
							int YEAR = Integer.parseInt(year);
							// ���������¡�������Ϣ
//							labor_CostInfo = createCostInfo(session, revision,
//									"����", 2, Group_value, findValue_section, YEAR,
//									null);
//							labor_CostInfo_hour = createCostInfo(session, revision,
//									"Сʱ", 2, Group_value, findValue_section, YEAR,
//									null);
							
							labor_CostInfo = new MyCostInfoBean( revision,
									"����", 2, Group_value, findValue_section, YEAR,
									null);
							labor_CostInfo_hour = new MyCostInfoBean( revision,
									"Сʱ", 2, Group_value, findValue_section, YEAR,
									null);

							//HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
							//maps_CostInfo.put("����", labor_CostInfo);
							//maps_CostInfo.put("Сʱ", labor_CostInfo_hour);
							//maps_HunmanLabor.put(index + year, maps_CostInfo);
							
							HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
							maps_CostInfo.put("����", labor_CostInfo);
							maps_CostInfo.put("Сʱ", labor_CostInfo_hour);
							maps_HunmanLabor.put(index + year, maps_CostInfo);
						} else {

							labor_CostInfo = maps_HunmanLabor.get(index + year)
									.get("����");
							labor_CostInfo_hour = maps_HunmanLabor
									.get(index + year).get("Сʱ");
						}
					}
				} else {
					if (maps_HunmanLabor_yuan == null) {
						int YEAR = Integer.parseInt(year);
						maps_HunmanLabor_yuan = new HashMap<String, HashMap<String, MyCostInfoBean>>();
//						labor_CostInfo_money = createCostInfo(session, revision,
//								"Ԫ", 2, Group_value, findValue_section, YEAR, null);
						labor_CostInfo_money = new MyCostInfoBean( revision,
								"Ԫ", 2, Group_value, findValue_section, YEAR, null);
						
						
						//HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
						//maps_CostInfo.put("Ԫ", labor_CostInfo_money);
						//maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);
						
						HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
						maps_CostInfo.put("Ԫ", labor_CostInfo_money);
						maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);
					}

					if (!year.equals(year_maps.get(col + ""))) {
						// ����Ҫ�����µķ�����Ϣ������������˾Ͳ���Ҫ����
						year = year_maps.get(col + "");
						boolean flag = false;

						// 5.6�޸�
						for (String s : maps_HunmanLabor_yuan.keySet()) {
							if ((index + year).equals(s)) {
								flag = true;
								break;
							}
						}
						if (!flag) {// ������Ԫ��������Ϣ
							int YEAR = Integer.parseInt(year);
//							labor_CostInfo_money = createCostInfo(session,
//									revision, "Ԫ", 2, Group_value,
//									findValue_section, YEAR, null);
							
							labor_CostInfo_money = new MyCostInfoBean(
									revision, "Ԫ", 2, Group_value,
									findValue_section, YEAR, null);

							//HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
							HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
							maps_CostInfo.put("Ԫ", labor_CostInfo_money);
							maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);

						} else {
							labor_CostInfo_money = maps_HunmanLabor_yuan.get(
									index + year).get("Ԫ");

						}
					} else {

						boolean flag = false;
						// 5.6�޸�
						for (String s : maps_HunmanLabor_yuan.keySet()) {
							if ((index + year).equals(s)) {
								flag = true;
								break;
							}
						}
						if (!flag) {// ������Ԫ��������Ϣ
							int YEAR = Integer.parseInt(year);

							//labor_CostInfo_money = createCostInfo(session,
							//		revision, "Ԫ", 2, Group_value,
							//		findValue_section, YEAR, null);
							labor_CostInfo_money = new MyCostInfoBean(
									revision, "Ԫ", 2, Group_value,
									findValue_section, YEAR, null);

							//HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
							HashMap<String, MyCostInfoBean> maps_CostInfo = new HashMap<String, MyCostInfoBean>();
							maps_CostInfo.put("Ԫ", labor_CostInfo_money);
							maps_HunmanLabor_yuan.put(index + year, maps_CostInfo);
						} else {

							labor_CostInfo_money = maps_HunmanLabor_yuan.get(
									index + year).get("Ԫ");

						}
					}
				}

				// �õ�����BYѧ�ơ�����������jci6_RateLevel
				// if (TYPE_HumanLaborYuan == 0) {
				// TCComponent property = labor_CostInfo
				// .getReferenceProperty("jci6_RateLevel");
				// rate_string = property.getProperty("default_rate");
				// } else {
				// TCComponent property = labor_CostInfo_money
				// .getReferenceProperty("jci6_RateLevel");
				// rate_string = property.getProperty("default_rate");
				// }

				// ��ȡÿ�ַ������͵�ֵ
				// ��ExcelRW��ȡexcel
				String data = tool.getDataFromExcel(NumToString(col), row, sheet);

				if (data.equals("null") || data.equals("")) {
					data = "0.0";
				}
			
				// ������ֵ
				double dCostValue = 0;
				try {
					BigDecimal bd = new BigDecimal(data);
					data = bd.stripTrailingZeros().toPlainString();
					
					if(!data.contains(".")){
						data=data+".00";
					}
				} catch (Exception e) {
					// ������Ϣ
					dCostValue = 0;
				}
				
				// ԭ�����Ե�ֵ
				double val = 0;

				if (data.matches("^(-?\\d+)(\\.\\d+)?$")) {
					try {
						dCostValue = Double.parseDouble(data);
						// //System.out.println("-----������----" + dCostValue);
					} catch (Exception e) {
						// ������Ϣ
						dCostValue = 0;
					}
				} else {
					continue;
				}
				// �˹����õķ���
				// Double rate = Double.parseDouble(rate_string);

				// 4.27����С��������λ
				// (dCostValue+"").substring(0,(dCostValue+"").indexOf(".")+3)
				String stringValue = "";
				DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");//��ʽ������  

				if (month_maps.get(col + "").equals("01")) {
					// �ж��Ƿ�Ϊ0����Ϊ0��ȡ5λ���£�4λСʱ����3λԪ
					if (dCostValue != 0) {
						if (TYPE_HumanLaborYuan == 0) {
							String property_value = labor_CostInfo.jci6_Jan;
									//.getProperty("jci6_Jan");
							if (property_value != null
									&& !property_value.equals("")) {
								val = Double.parseDouble(property_value);
								//System.out.println("�����˹�������Ϣ  jci6_Jan��ֵ��Ϊ0===="+ big2( val) + "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  jci6_Jan��ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
												
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

								//System.out.println("Ԫ�˹�������Ϣ  jci6_Jan��ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
			
												
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
								//System.out.println("�����˹�������Ϣ  jci6_Feb��ֵ��Ϊ0===="+ val + "+ labor_CostInfo.getProperty("jci6_Year"));  "
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  jci6_Feb��ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("Ԫ�˹�������Ϣ  jci6_Feb��ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("�����˹�������Ϣ  jci6_Mar��ֵ��Ϊ0===="+ val + "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  jci6_Mar��ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("Ԫ�˹�������Ϣ  jci6_Mar��ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("�����˹�������Ϣ  jci6_Apr��ֵ��Ϊ0===="+ val + "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  jci6_Apr��ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("Ԫ�˹�������Ϣ  jci6_Apr��ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
												
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
								//System.out.println("�����˹�������Ϣ  jci6_May��ֵ��Ϊ0===="+ val + "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  jci6_May��ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
												
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
								//System.out.println("Ԫ�˹�������Ϣ  jci6_May��ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("�����˹�������Ϣ  6�µ�ֵ��Ϊ0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  6�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("Ԫ�˹�������Ϣ  6�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("�����˹�������Ϣ  7�µ�ֵ��Ϊ0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  7�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("Ԫ�˹�������Ϣ  7�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("�����˹�������Ϣ  8�µ�ֵ��Ϊ0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  8�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("Ԫ�˹�������Ϣ  8�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("�����˹�������Ϣ  9�µ�ֵ��Ϊ0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  9�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("Ԫ�˹�������Ϣ  9�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
												
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
								//System.out.println("�����˹�������Ϣ  10�µ�ֵ��Ϊ0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  10�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("Ԫ�˹�������Ϣ  10�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("�����˹�������Ϣ  11�µ�ֵ��Ϊ0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  11�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("Ԫ�˹�������Ϣ  11�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("�����˹�������Ϣ  12�µ�ֵ��Ϊ0====" + val+ "  "+ labor_CostInfo.getProperty("jci6_Year"));
										
										
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
								//System.out.println("Сʱ�˹�������Ϣ  12�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_hour.getProperty("jci6_Year"));
										
										
										
												
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
								//System.out.println("Ԫ�˹�������Ϣ  12�µ�ֵ��Ϊ0===="+ val+ "  "+ labor_CostInfo_money.getProperty("jci6_Year"));
										
										
										
												
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
	
		// ��ȡ�˹�����ָ�����е�excel���
		private void setValuefromExcel(TCComponentItemRevision revision,
				String sheet_name, int startRow, int endRow, int starDate,
				int endDate, int datePosition, JacobEReportTool tool,
				Dispatch sheetsAll) throws TCException, IOException {

			//System.out.println("-----��ȡ�˹�����-------");

			Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

			HashMap<String, String> month_maps = new HashMap<String, String>();
			HashMap<String, String> year_maps = new HashMap<String, String>();

			// ȡ������
			for (int col = starDate; col <= endDate; col++) {
				// ���µķ�����ȡexcel
				String column = NumToString(col);
				String data_string = tool.getDataFromExcel(column, datePosition,
						sheet);
				String data;
				try {
					data = sdf.format(new Date(data_string));
				} catch (Exception e) {
					// ������Ϣ
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

			// ȡ��ָ��������ֵ
			for (int i = startRow; i <= endRow; i++) {
				setCostInfoValuesByRow(revision, tool, i, starDate, endDate,
						month_maps, year_maps, sheet);
			}

		}

	// ��ȡ���˹����õ�excel���?
	private void setValuefromNonLabel(TCComponentItemRevision revision,
			String sheet_name, int startRow, int endRow, int startDate,
			int endDate, int datePosition, JacobEReportTool tool,
			Dispatch sheetsAll) throws ServiceException, TCException {
		//System.out.println("-----��ȡ���˹�����-------");

		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		HashMap<String, String> month_maps_non = new HashMap<String, String>();
		HashMap<String, String> year_maps_non = new HashMap<String, String>();

		// ȡ������
		for (int col = startDate; col <= endDate; col++) {
			// ���µķ�����ȡexcel
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);
			String data="";
			try{
				 data = sdf.format(new Date(data_string));
			} catch (Exception e) {
				data = "1900-01-01";
			}

			// �Ƿ�Ϊ��׼����
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
			// �ҵ���Ӧ�İ汾

			// ��ͬ
			if (keyValue.trim().equals(key.trim())) {
				value = hashMap.get(keyValue);
				//System.out.println("2014/6/11===�ҵ��˶�Ӧ��ѡ���ֵ----���֣�" + keyValue+ "  value��ֵ====" + value);
				break;	

			} else {
				if (keyValue.equals(key)) {
					value = hashMap.get(keyValue);
					//System.out.println("2014/6/11===�ҵ��˶�Ӧ��ѡ���ֵ----���֣�" + key+ "  value��ֵ====" + value);
					break;

				}

			}
		}
		return value;
	}

	// ���˹���Ϣ
	// ��ȡһ����Ϣ�ķ���
	private void readNonLabor(TCComponentItemRevision revision,
			JacobEReportTool tool, int row, int startDate, int endDate,
			String preferenceName, HashMap<String, String> month_maps_non,
			HashMap<String, String> year_maps_non, Dispatch sheet)
			throws ServiceException, TCException {

		// ȡ�õ�һ�е�ֵ,��ExcelRW
		String cellValue = tool.getDataFromExcel("A", row, sheet);

		// 5.8�޸�--���excel���е�����Ϊ0.������һ�о�������
		String height_str = tool.getHeight("A" + row, sheet);
		if (height_str.equals("0.0")) {
			return;
		}

		// �����������ֵ
		String year = year_maps_non.get(startDate + "");

		// ʲôʱ�򴴽����˹����н�����������keyOfreferenceΪ��ȡһ��excel���ֵ������ѡ�������Ƿ������Ӧ������
		// String findValue = isFindValue(cellValue, session, preferenceName);

		// �õ���������--ֻ�з��˹���Ϣ����----YFJC_NonLabor_CostType
		String findValue = isFindValue2(cellValue, session,
				nameOfPreference.get("CostType"));

		String costType = findValue;
		if (findValue.equals("")) {
			return;
		}

		//System.out.println("\n��ȡ���˹���Ԫ��----��һ�е�ֵ---->" + cellValue+ "   findValue---->" + findValue + "   costType---->"+ costType);
				
				

		// ��Ҫ���õķ�����Ϣ
		MyCostInfoBean component_costInfo;

		// ȡÿ���µ�ֵ
		for (int col = startDate; col <= endDate; col++) {

			if (!year.equals(year_maps_non.get(col + ""))) {
				// ����Ҫ�����µķ�����Ϣ������������˾Ͳ���Ҫ����
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
					//		revision, "Ԫ", 1, null, null, Year, costType);
					MyCostInfoBean createCostInfo=new MyCostInfoBean(
								revision, "Ԫ", 1, null, null, Year, costType);
					
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
					//		revision, "Ԫ", 1, null, null, Year, costType);
					MyCostInfoBean createCostInfo=new MyCostInfoBean(
							revision, "Ԫ", 1, null, null, Year, costType);
					
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
					// ���hashmap��û��
					if (!flag) {

						int Year = Integer.parseInt(year);
						// ����������Ϣ
						//TCComponent createCostInfo = createCostInfo(session,
						//		revision, "Ԫ", 1, null, null, Year, costType);
						MyCostInfoBean createCostInfo=new MyCostInfoBean(
										revision, "Ԫ", 1, null, null, Year, costType);
						
						maps_CostInfo_Non.put(findValue + year, createCostInfo);
						component_costInfo = createCostInfo;
					} else {
						component_costInfo = maps_CostInfo_Non.get(findValue
								+ year);
					}
				}

				// ��ȡÿ�ַ������͵�ֵ
				double dCostValue = 0;
				double val = 0;

				// ��ExcelRW���߶�ȡexcel
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
					// ��д������Ϣ
					dCostValue = 0;
				}
				
				//modify by wuwei--���˹�00
				if(cellCost.contains(".")){
					cellCost = cellCost + "00";
				}else{
					cellCost = cellCost + ".000";
				}

				
				
				//System.out.println("--readNonLabor---������cellCost--->" + cellCost);
				//DecimalFormat decimalFormat = new DecimalFormat("#,##0.000");//��ʽ������  
				
				if (cellCost.matches("^(-?\\d+)(\\.\\d+)?$")) {
					try {
						dCostValue = Double.parseDouble(cellCost);
						//System.out.println("--readNonLabor--������----" + big2(dCostValue));
					} catch (Exception e) {
						e.printStackTrace();
						// ��д������Ϣ
						dCostValue = 0;
					}
				} else {
					continue;
				}

				String stringValue = "";

				// 4.27����������Ϣ��Ԫ��С������3λ ((dCostValue+
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
	
	// �����ѡ���ֵ�ԡ�=���ָ������hashmap
		private HashMap<String, String> getTCPreferenceArray2(TCSession tcsession,
				String preferenceName) {

			TCPreferenceService tcpreservice = tcsession.getPreferenceService();
			String[] preString = tcpreservice.getStringArray(
					TCPreferenceService.TC_preference_site, preferenceName);
			HashMap<String, String> map_refer = new HashMap<String, String>();

			for (int i = 0; i < preString.length; i++) {
				String[] split = preString[i].split("=");
				// GEBT�汾
				String ver = split[0].trim();
				map_refer.put(ver, split[1].trim());
			}
			return map_refer;
		}

	// ����ѡ����ȥ�����Ե�ֵ
	private String isFindValue(String key, TCSession tcsession,
			String preferenceName) {
		String value = "";

		HashMap<String, String> hashMap = getTCPreferenceArray2(tcsession,
				preferenceName);

		for (String keyValue : hashMap.keySet()) {
			// �ҵ���Ӧ�İ汾
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
				// ��ͬ
				if (flag) {
					value = hashMap.get(keyValue);
					//System.out.println("2014/6/11===�ҵ��˶�Ӧ��ѡ���ֵ----���֣�"+ keyValue + "  value��ֵ====" + value);
							

				}

			} else {
				if (keyValue.equals(key)) {
					value = hashMap.get(keyValue);
					//System.out.println("2014/6/11===�ҵ��˶�Ӧ��ѡ���ֵ----���֣�" + key+ "  value��ֵ====" + value);
							

				}

			}
		}
		return value;
	}

	// �����ѡ���ֵ�ԡ�=���ָ������hashmap
	private HashMap<String, String> getTCPreferenceArray(TCSession tcsession,
			String preferenceName) {

		TCPreferenceService tcpreservice = tcsession.getPreferenceService();
		String[] preString = tcpreservice.getStringArray(
				TCPreferenceService.TC_preference_site, preferenceName);
		HashMap<String, String> map_refer = new HashMap<String, String>();

		for (int i = 0; i < preString.length; i++) {
			String[] split = preString[i].split("=");
			// GEBT�汾
			String ver = split[0].trim();
			// 5.8�޸�--���롰UTF-8��
			map_refer.put(ver, split[1].trim());
		}
		return map_refer;
	}

	// �½���ѯ "ѧ�����?�Ĺ���---�Զ����ѯ����?
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
	 * colNameToNum::��������ҵ�������?
	 * 
	 * @param String
	 *            ����A��B�ȣ�
	 * @return int ������
	 */
	public int colNameToNum(String colName) {
		int result = 0;
		for (int i = 0; i < colName.length(); i++) {
			result = result * 26 + colName.charAt(i) - 65 + 1;
		}
		return result;
	}

	// ��ȡ��Ӧ����ѡ��õ�excel��λ�õ�ֵ
	private boolean getValueOfExcelByPreference(TCSession tcsession,
			String preferenceName, String lov_values) {
		// lov_valuesֵΪ��V1������V2��

		String index = null;

		// ��ȡ�����˹��ͷ��˹�����ѡ��
		TCPreferenceService tcpreservice = tcsession.getPreferenceService();

		String[] preString = tcpreservice.getStringArray(
				TCPreferenceService.TC_preference_site, preferenceName);

		HashMap<String, String> map = new HashMap<String, String>();

		System.out.println("preString.length:"+preString.length);
		for (int i = 0; i < preString.length; i++) {
			String[] split = preString[i].split("=");
			// GEBT�汾
			String ver = split[0].trim();
			String[] split2 = ver.split("_");
			if (split2.length == 2) {
				// �ҵ���Ӧ�İ汾
				// //System.out.println(lov_values + "=====" + split2[1]);
				if (split2[1].equals(lov_values)) {
					index = ver;
					map.put(ver, split[1].trim());
					//System.out.println("----------�ҵ���Ӧ��GEBT�汾 For location��"+ ver);
							
				}
			}
		}

		// ���û�ҵ����ͷŽ�error_list����
		if (index == null) {
			// combo_version.setEnabled(true);
			index = "EQU";
			// ������Ϣ
			String error_msg = "Not found related version from preference_setting!!!";
			HashMap<String, String> map_msg = new HashMap<String, String>();
			map_msg.put("error_msg", error_msg);
			error_list.add(map_msg);
			return false;
		} else {

			// ��ȡ��Ӧ��ֵ���ҵ�excel�е�cell
			String sheet = map.get(index).substring(0,
					map.get(index).indexOf("{"));
			// ҳ������
			maps_Prefer_values.put("Sheet", sheet.trim());

			String row = map.get(index).substring(
					map.get(index).indexOf("}") + 1,
					map.get(index).indexOf("||"));
			String[] split_row = row.split("-");
			// ��ʼ�к���ֹ��
			maps_Prefer_values.put("StartRow", split_row[0]);
			maps_Prefer_values.put("EndRow", split_row[1]);

			// ���ڵ��е�λ��
			String pos = map.get(index).substring(
					map.get(index).lastIndexOf("|") + 1,
					map.get(index).indexOf("("));
			maps_Prefer_values.put("Position", pos);

			// �õ����ڵ�������
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
	 * ȥ��С������������
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

	// ����ת��Ӣ����ĸ,��ȡexcel���е��е���ֵת��Ӣ����ĸ
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

	// ȥ���·�ǰ�ġ�0��
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

	// ��ȡiλС��
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

	// �õ�TC�е���������ϵͳ�Զ��仯����Ӣ��
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

	// �õ�ϵͳ��ʱ��
	private String getSystemTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		Date currentTime = new Date();
		String dateString = format.format(currentTime);
		return dateString;
	}

	// 6.5---�޸�=��Ū������---������ѡ����
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
														
		// Ĭ��true�Զ��Ÿ���,��[123,456,789.128]
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
