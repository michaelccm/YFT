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
 #	2013-3-11	liuc  		Ini		导入预算和EQU的SWT对话框					

	   
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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Button;
import com.jacob.com.Dispatch;
import com.teamcenter.rac.kernel.TCDateFormat;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
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
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2008_06.DataManagement;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;

public class NewSWTutil {

	private Display display;
	private Shell shell;
	private int isF;

	private static String ExcelNameMode = "GEBT.xlsm";

	// swt界面需要的组件
	private Text text_EQU;
	// private DatePickerCombo text_EQU_Signdate;
	// private DatePickerCombo text_Calc_Date;
	// private Combo text_calculated_by;
	private Text text_approved_amount;
	private DatePickerCombo text_PDx_Sign_Date;
	private Combo combo_version;
	private Combo combo_pdx;
	private Registry reg = Registry.getRegistry(this);
	private Button btn_import;
	private Button btn_cancel;
	private StyledText remarkText;

	// 存放从excel中得到的数据
	private HashMap<String, String> maps;
	private TCComponentItem componentItem;
	private TCSession session;
	private Object data;
	private TCComponentDataset target;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	// 需要的变量
	// 是覆盖还是新建版本,false为覆盖
	private boolean flag = false;

	// private Object obj[] = new Object[1];

	// 4.8修改
	// JacobEReportTool tool = new JacobEReportTool();
	// 4.8更新
	// 首选项名
	private static String YFJC_GEBT_Attribute_Location = "YFJC_GEBT_Attribute_Location";
	// 4.18---修改非人工的信息对象的首选项，因为后面的值为CostType的值，相同就更新而不是新建
	private static String YFJC_NonLabor_CostType = "YFJC_NonLabor_CostType";
	private static String YFJC_NonLabor_Position = "YFJC_NonLabor_Position";
	private static String YFJC_HumanLabor_Position = "YFJC_HumanLabor_Position";
	private static String YFJC_HumanLabor_Group = "YFJC_HumanLabor_Group";
	// 5.4--修改人工费用信息对象的首选项名字---YFJC_HumanLabor_RateLevel
	private static String YFJC_HumanLabor_Section = "YFJC_HumanLabor_RateLevel";
	// 5.6--修改新增加一个首选项---找到人工费用的SMTE的组SMTE的组
	private static String YFJC_HumanLabor_SMTE_GROUP = "YFJC_HumanLabor_SMTE_GROUP";

	// 7.2修改-----新增加一个首选项---找到人工费用的元的excel所对应的位置
	private String YFJC_LaborCostYuan_Position = "YFJC_LabourCost_Position";
	private HashMap<String, TCComponent> maps_CostInfo_Non;// 保存非人工的costInfo费用信息
	// 5.6修改----保存人工的costInfo费用信息
	HashMap<String, HashMap<String, TCComponent>> maps_HunmanLabor;
	HashMap<String, HashMap<String, TCComponent>> maps_HunmanLabor_yuan;
	// 保存人工费用的组的信息
	private HashMap<String, String> map_group = new HashMap<String, String>();

	// 保存首选项里的数据
	private HashMap<String, String> maps_Prefer_values = new HashMap<String, String>();

	// 保存error的对话框需要的值
	private ArrayList<HashMap<String, String>> error_list = null;

	// 4.10更新
	private String ProjectID;

	// 6.5更新----存储Group,学科，以及CostType
	private HashMap<String, String> nameOfPreference = new HashMap<String, String>();

	// 5.31更新-----------能够得到每个itemrevision的string
	Vector<String> treeNames = new Vector<String>();
	int endposition_labor = 0;
	int endposition_nonlabor = 0;
	private TCComponent projectComponent;

	// 7.2修改--更新 人工费用信息的读取---元不再计算而是读取指定页面的内容
	int TYPE_HumanLaborYuan = 0;

	// 构造函数
	public NewSWTutil(DeviceData deviceData, TCSession session,
			TCComponentItem componentItem, int isF, TCComponentDataset target,
			TCComponent projectComponent) {
		// this.shell = shell;
		// this.display = display;

		display = new Display(deviceData);
		shell = new Shell();

		// obj[0] = "open bypass";// 旁路中需要的变量参数的值

		this.componentItem = componentItem;
		this.session = session;
		this.isF = isF;
		this.target = target;
		this.projectComponent = projectComponent;
		maps = new HashMap<String, String>();
	}

	/**
	 * Open the window
	 * 
	 * @throws Exception
	 * @throws TCException
	 */
	public void open() throws Exception {
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * 
	 * @throws Exception
	 */
	protected void createContents() throws Exception {

		shell.setSize(562, 680);
		if (isF == 1) {
			shell.setText(reg.getString("Shell_Tittle_EQU"));
		} else {
			shell.setText(reg.getString("Shell_Tittle_TBL"));
		}

		// EQU=Content{.}C536
		// PDx_Squence的LOV的值
		String[] PDxSeq_Lov = getLovStrings(componentItem, "jci6_PDxSeq");

		// GEBT_Version的LOV值 没配置
		String[] GEBT_versions = getLovStrings(componentItem,
				"jci6_GEBTTemplate");

		Label label_PDx = new Label(shell, SWT.RIGHT);
		label_PDx.setBounds(65, 10, 93, 17);
		label_PDx.setText(getNameOfTC(componentItem, "jci6_PDxSeq") + ":");

		// 创建Combo组件，为下拉列表样式
		combo_pdx = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo_pdx.setBounds(168, 7, 240, 20);
		combo_pdx.setItems(PDxSeq_Lov);
		// 给个默认的值--4.18修改
		// combo_pdx.select(0);

		Label label_version = new Label(shell, SWT.RIGHT);
		label_version.setBounds(49, 33, 113, 17);
		label_version.setText(getNameOfTC(componentItem, "jci6_GEBTTemplate")
				+ ":");

		// 创建Combo组件，为下拉列表样式
		combo_version = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo_version.setBounds(168, 33, 240, 20);
		combo_version.setItems(GEBT_versions);
		// combo_version.select(0);

		Label label_EQU = new Label(shell, SWT.RIGHT);
		label_EQU.setBounds(65, 70, 93, 17);
		label_EQU.setText(getNameOfTC(componentItem, "jci6_EQU") + ":");

		text_EQU = new Text(shell, SWT.BORDER);
		text_EQU.setBounds(168, 70, 240, 20);

		// Label label_sign_date = new Label(shell, SWT.RIGHT);
		// label_sign_date.setBounds(49, 105, 113, 17);
		// label_sign_date.setText(getNameOfTC(componentItem,
		// "jci6_EQUSignDate")
		// + ":");

		text_approved_amount = new Text(shell, SWT.BORDER);
		text_approved_amount.setBounds(168, 105, 240, 20);
		// text_EQU_Signdate.setBackground(new Color(Display.getCurrent(), 195,
		// 195, 195));

		Label label_PDxSignDate = new Label(shell, SWT.RIGHT);
		label_PDxSignDate.setBounds(49, 136, 113, 17);
		label_PDxSignDate
				.setText(getNameOfTC(componentItem, "jci6_PDxSignDate") + ":");

		// text_Calc_Date = new DatePickerCombo(shell, SWT.BORDER);
		// text_Calc_Date.setBounds(168, 133, 240, 20);

		// Label calculatedBy = new Label(shell, SWT.RIGHT);
		// calculatedBy.setBounds(40, 167, 122, 17);
		// calculatedBy.setText(getNameOfTC(componentItem,
		// "jci6_Responsibility")
		// + ":");

		// jci6_Responsibility的LOV值
//		String[] personLists = getLovStrings(componentItem,
//				"jci6_Responsibility");

		// text_calculated_by = new Combo(shell, SWT.DROP_DOWN);
		// text_calculated_by.setBounds(168, 166, 240, 20);
		// text_calculated_by.setItems(personLists);

		Label label_approved_amount = new Label(shell, SWT.NONE);
		label_approved_amount.setBounds(49, 105, 113, 17);
		label_approved_amount.setText(getNameOfTC(componentItem,
				"jci6_SignedMoney") + ":");

		// text_approved_amount.setBackground(new Color(Display.getCurrent(),
		// 195,
		// 195, 195));

		// Label label_PDx_Sign_Date = new Label(shell, SWT.RIGHT);
		// label_PDx_Sign_Date.setBounds(49, 240, 113, 17);
		// label_PDx_Sign_Date.setText(getNameOfTC(componentItem,
		// "jci6_PDxSignDate") + ":");

		text_PDx_Sign_Date = new DatePickerCombo(shell, SWT.BORDER);
		text_PDx_Sign_Date.setBounds(168, 133, 240, 20);
		// text_PDx_Sign_Date.setBackground(new Color(Display.getCurrent(), 195,
		// 195, 195));

		if (isF == 1) {
			label_approved_amount.setVisible(false);
			text_approved_amount.setVisible(false);
			// label_PDx_Sign_Date.setVisible(false);
			// text_PDx_Sign_Date.setVisible(false);
		}

		// 5.31修改
		Label label_Remark = new Label(shell, SWT.RIGHT);
		label_Remark.setBounds(40, 167, 122, 17);
		label_Remark.setText(getNameOfTC(componentItem, "jci6_Remark") + ":");

		remarkText = new StyledText(shell, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		remarkText.setBounds(168, 166, 240, 110);

		// 新建树
		Tree tree = new Tree(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setBounds(103, 425, 273, 139);

		// 创建树的一个根节点
		final TreeItem root = new TreeItem(tree, SWT.NULL);

		String root_name = componentItem.getStringProperty("object_string");
		// 创建一个树对象
		root.setText(root_name);
		root.setData(componentItem);

		AIFComponentContext[] contexts = componentItem.getChildren();

		for (int i = 0; i < contexts.length; i++) {
			if (contexts[i].getComponent() instanceof TCComponentItemRevision) {
				TreeItem item = new TreeItem(root, SWT.NONE);
				item.setText(contexts[i].toString());
				treeNames.add(contexts[i].toString());
				TCComponentItemRevision revision = (TCComponentItemRevision) contexts[i]
						.getComponent();
				item.setData(revision);
				// 递归调用，因为无法得知要创建几个子目录
				listFiles(contexts[i], item);
			}
		}

		// --------------虚线框--------------------------------
		Group group = new Group(shell, SWT.NONE);
		group.setBounds(80, 400, 320, 180);
		group.setText(reg.getString("Group_Tittle"));

		// ----------监听button------------
		btn_import = new Button(shell, SWT.NONE);
		btn_import.setBounds(108, 590, 72, 22);
		btn_import.setText(reg.getString("btn_import"));
		btn_import.setEnabled(false);

		btn_cancel = new Button(shell, SWT.NONE);
		btn_cancel.setBounds(304, 590, 72, 22);
		btn_cancel.setText(reg.getString("btn_cancel"));

		btn_cancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// 释放资源
				display.dispose();
			}
		});

		btn_import.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

				// 判断是覆盖还是新建版本
				if (flag) {
					JacobEReportTool tool = new JacobEReportTool();
					int isClose = 0;
					try {
						Dispatch sheetsAll = null;

						// 得到八个属性的值
						getAllValues(maps);

						// 选择的是根目录，新建版本以"IMAN_specification"关系挂接到这个版本下
						// 再自动发布冻结这个版本对象

						// 新建版本(升版)
						TCComponentItem item = (TCComponentItem) data;

						String item_version = maps.get("PDx_Squence");

						// openByPass();

						TCComponent[] relatedComponents = item
								.getRelatedComponents("revision_list");

						// 改成修订最新版本的而不是“A”版本的
						TCComponentItemRevision rev_tag = item
								.getLatestItemRevision();
						// for (int i = 0; i < relatedComponents.length; i++) {
						// if (relatedComponents[i] instanceof
						// TCComponentItemRevision) {
						// TCComponentItemRevision rev_tmp =
						// (TCComponentItemRevision) relatedComponents[i];
						//
						// if (rev_tmp.getProperty("item_revision_id")
						// .endsWith("A")) {
						// rev_tag = rev_tmp;
						// }
						// }
						// }

						// 新建版本的方法
						TCComponentItemRevision saveAsRev = rev_tag
								.saveAs(item_version);
						System.out.println("新版本是否有权限修改："
								+ saveAsRev.okToModify() + " 版本Name=========="
								+ saveAsRev);

						// closeByPass();

						// openByPass();
						TCComponent tcComponent = saveAsRev
								.getRelatedComponent("IMAN_specification");
						// closeByPass();

						// 执行的是TBL
						if (isF == 2) {
							// 得到系统的零时文件
							String getenv = System.getenv("TEMP");
							String TC_path = System.getenv("TPR");
							tool.addDir(TC_path + "\\plugins");
							// 得到所有的sheet
							sheetsAll = tool.getSheets(getenv + File.separator
									+ ExcelNameMode);

							// 先检查一遍要读取的值
							checkExcel(maps, tool, sheetsAll, saveAsRev);

							if (error_list.size() != 0) {
								isClose = 1;
								tool.closeExcelFile(false);
								display.dispose();
								Excel_ErrorDialog dialog_error = new Excel_ErrorDialog(
										error_list);

								return;
							}
						}

						if (tcComponent != null) {
							// 解除他们之间的关系属性
							// openByPass();
							saveAsRev.remove("IMAN_specification", tcComponent);
							// closeByPass();
						}

						deleteCostInfo(saveAsRev, flag);

						// 先清空copy过来的属性,再设置值
						setDataOfRev(saveAsRev, maps);

						// openByPass();
						saveAsRev.add("IMAN_specification", target);
						// closeByPass();

						if (isDatasetRelease(target)) {
							// 发布冻结掉这个版本
							releaseRev(saveAsRev);
						}

						// 根据GEBT文件是否在流程中，就把这个版本也添加到流程中
						TCComponentTask rootTask = null;

						// 5.17修改---dataset数据集是否有
						if (isDatasetINProcesssList(target)) {
							if (target.getCurrentJob() != null) {
								// openByPass();
								rootTask = target.getCurrentJob().getRootTask();
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
									if (attachments[i] == target) {
										findDateSet = 1;
									}
								}
								if (attachments[i] instanceof TCComponentItemRevision)

								{
									if (attachments[i] == saveAsRev) {
										findFlag = 1;
									}
								}
							}

							if (findFlag != 1 && findDateSet == 1) {
								// openByPass();
								rootTask.addAttachments(
										TCAttachmentScope.GLOBAL,
										new TCComponent[] { saveAsRev },
										new int[] { 1 });
								// closeByPass();
							}
						}

						// 执行的是TBL
						if (isF == 2) {
							// 再新建费用信息
							// 读取excel里的内容，填写到费用信息中

							// 得到首选项的值---YFJC_HumanLabor_Position
							getValueOfExcelByPreference(session,
									YFJC_HumanLabor_Position,
									maps.get("GEBT_Version"));

							TYPE_HumanLaborYuan = 0;

							// 再创建人工信息
							setValuefromExcel(saveAsRev, maps_Prefer_values
									.get("Sheet"), Integer
									.parseInt(maps_Prefer_values
											.get("StartRow")),
									Integer.parseInt(maps_Prefer_values
											.get("EndRow")), Integer
											.parseInt(maps_Prefer_values.get

											("StartCol")), endposition_labor,
									Integer.parseInt(maps_Prefer_values.get

									("Position")), tool, sheetsAll);

							// 得到首选项的值---YFJC_HumanLabor_Position
							getValueOfExcelByPreference(session,
									YFJC_LaborCostYuan_Position,
									maps.get("GEBT_Version"));

							TYPE_HumanLaborYuan = 1;

							// 7.2修改 再创建人工信息---yuan
							setValuefromExcel(saveAsRev, maps_Prefer_values
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
							setValuefromNonLabel(saveAsRev, maps_Prefer_values
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

							// System.out.println("Before======delete no value CostInfo=====");

							// ShowCostInfos(saveAsRev);

							// 删除无值的costInfo---费用信息
							deleteCostInfo_noValue(saveAsRev);

						}

					} catch (TCException e2) {
						e2.printStackTrace();
					} catch (Exception e3) {
						e3.printStackTrace();
					} finally {
						/*
						 * if (isClose == 0) {
						 * System.out.println("---EXCEL 进程guanbi----"); //
						 * tool.closeExcelFile(false); }
						 */
						// 完成后关闭
						display.dispose();

						System.out.println("EXCEL CLOSE!!!");
					}

				} else {
					// 选择的是版本，覆盖版本
					// 再自动发布冻结这个版本对象

					// 得到八个属性的值
					getAllValues(maps);

					TCComponentItemRevision revision = (TCComponentItemRevision) data;

					// revision.getReferenceListProperty("");
					// 新建覆盖确认对话
					ImportConfirmDialog confirmDialog = new ImportConfirmDialog(
							display, revision, maps, session, isF, target,
							projectComponent);

				}
			}
		});

		// 当鼠标点击节点时使节点可编辑
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 获得触发事件的TreeItem
				TreeItem item = (TreeItem) e.item;
				data = item.getData();
				if (data instanceof TCComponentItem) {

					// 导入EQU的对话框
					if (text_EQU.getText() == null
							|| text_EQU.getText().equals("0")
							|| text_EQU.getText().equals("")
							|| text_EQU.getText().equals("null")) {
						btn_import.setEnabled(false);
					} else {
						if (combo_pdx.getText().equals("")) {
							btn_import.setEnabled(false);
						} else {
							if (isPDXNameExists(treeNames, combo_pdx.getText())) {
								btn_import.setEnabled(false);

								ImportDialogError dialogError = new ImportDialogError(
										display, 1);

							} else {
								btn_import.setEnabled(true);
							}
						}

					}

					flag = true;

				} else if (data instanceof TCComponentItemRevision) {
					if (text_EQU.getText() == null
							|| text_EQU.getText().equals("0")
							|| text_EQU.getText().equals("")
							|| text_EQU.getText().equals("null")) {
						btn_import.setEnabled(false);
					} else {

						if (combo_pdx.getText().equals("")) {
							btn_import.setEnabled(false);
						} else {
							TCComponentItemRevision rev = (TCComponentItemRevision) data;
							// System.out.println("6.1修改======================="
							// + rev.toString());
							if (!rev.toString().contains(combo_pdx.getText())) {

								if (isPDXNameExists(treeNames,
										combo_pdx.getText())) {
									btn_import.setEnabled(false);

									ImportDialogError dialogError = new

									ImportDialogError(display, 1);

								} else {
									btn_import.setEnabled(true);
								}
							} else {
								btn_import.setEnabled(true);
							}

						}

					}

					// 选择的是版本，覆盖版本以"IMAN_specification"关系挂接到这个版本下
					// 再自动发布冻结这个版本对象
					flag = false;

				} else {
					btn_import.setEnabled(false);
				}
			}
		});

		// ----------------------TEXT事件监听----------------------------//

		// combo_version: text的事件监听
		combo_version.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				JacobEReportTool tool = new JacobEReportTool();
				try {
					String TC_path = System.getenv("TPR");
					tool.addDir(TC_path + "\\plugins");
					// 得到系统的零时文件
					String getenv = System.getenv("TEMP");
					System.out.println("File.separator--->"+File.separator);
					// 得到所有的sheet
					System.out.println(getenv + File.separator
							+ ExcelNameMode);
					Dispatch sheetsAll = tool.getSheets(getenv + File.separator
							+ ExcelNameMode);

					// 得到combo_version下拉的选择的值
					String version = combo_version.getText();

					HashMap<String, String> map_value = new HashMap<String, String>();

					// 在首选项中查找,取得相关的属性
					String EQU_VALUE_str = searchGEBTVersion_value(version,
							map_value, tool, sheetsAll);
					
					//修改bywuwei 2014-8-28
					if(EQU_VALUE_str==null)
						EQU_VALUE_str="0";
					else if(EQU_VALUE_str.equals(""))
						EQU_VALUE_str="0";
					double doubleEQU = Double.parseDouble(EQU_VALUE_str);
					double r=	(double)Math.round(doubleEQU*1000)/1000;
					String EQU_VALUE = r+"";
					
					//String EQU_VALUE = cutString(EQU_VALUE_str, 3);

					// 先让text可编辑
					text_EQU.setEditable(true);

					// 设置EQU值
					text_EQU.setText(EQU_VALUE);

					// 让text不可编辑
					text_EQU.setEditable(false);
					if (EQU_VALUE.equals("null")
							|| Double.parseDouble(EQU_VALUE) == 0) {
						text_EQU.setEditable(true);
						btn_import.setEnabled(false);
					}

					// text_Calc_Date.setEnabled(true);

					// 4.18修改---jci6_CalcDate的值是由数据集的属性"creation_date"来决定
					Date date_CalcDate = target
							.getDateProperty("creation_date");
					// String jci6_CalcDate = date_CalcDate.toString();
					// if (!jci6_CalcDate.equals("")) {
					// setTextValueFromExcel(jci6_CalcDate, "jci6_CalcDate");
					// } else {
					// text_Calc_Date.setText("");
					// }
					// text_Calc_Date.setDate(date_CalcDate);
					// if (date_CalcDate == null) {
					// text_Calc_Date.setText("");
					// } else {
					// text_Calc_Date.setText(sdf.format(date_CalcDate));
					// text_Calc_Date.setEnabled(false);
					// }

					String jci6_PDxSeq = getValue(map_value, "jci6_PDxSeq",
							tool, sheetsAll);
					setTextValueFromExcel(jci6_PDxSeq, "jci6_PDxSeq");

					text_PDx_Sign_Date.setEnabled(true);
					String jci6_PDxSignDate = getValue(map_value,
							"jci6_PDxSignDate", tool, sheetsAll);
					if (!jci6_PDxSignDate.equals("")) {
						setTextValueFromExcel(jci6_PDxSignDate,
								"jci6_PDxSignDate");
					} else {
						text_PDx_Sign_Date.setText("");
					}

					String jci6_Responsibility = getValue(map_value,
							"jci6_Responsibility", tool, sheetsAll);
					setTextValueFromExcel(jci6_Responsibility,
							"jci6_Responsibility");

					// text_EQU_Signdate.setEnabled(true);
					// String jci6_EQUSignDate = getValue(map_value,
					// "jci6_EQUSignDate", tool, sheetsAll);
					// if (!jci6_EQUSignDate.equals("")) {
					// setTextValueFromExcel(jci6_EQUSignDate,
					// "jci6_EQUSignDate");
					// } else {
					// text_EQU_Signdate.setText("");
					// }

					text_approved_amount.setEnabled(true);
					String jci6_SignedMoney_str = getValue(map_value,
							"jci6_SignedMoney", tool, sheetsAll);

					System.out.println("读取EXCEL中Signed Money==============="
							+ jci6_SignedMoney_str);
					
					//修改bywuwei 2014-8-28
					if(jci6_SignedMoney_str==null)
						EQU_VALUE_str="0";
					else if(jci6_SignedMoney_str.equals(""))
						EQU_VALUE_str="0";
					double doublejci6_SignedMoney = Double.parseDouble(jci6_SignedMoney_str);
					double rr=	(double)Math.round(doublejci6_SignedMoney*100)/100;
					String jci6_SignedMoney =rr+"";
					
					System.out.println("处理后的Signed Money==============="
							+ jci6_SignedMoney);

					// 处理读取EXCEL中Signed
					// Money===============1.3611513297324609E700
					
//					if (jci6_SignedMoney_str.contains("E")) {
//						jci6_SignedMoney = jci6_SignedMoney_str;
//
//					} else {
//						jci6_SignedMoney_str = jci6_SignedMoney_str + "00";
//						jci6_SignedMoney = cutString(jci6_SignedMoney_str, 2);
//					}

					if (!jci6_SignedMoney.equals("")) {
						// && jci6_SignedMoney.matches("^(-?\\d+)(\\.\\d+)?$"))
						// {
						setTextValueFromExcel(jci6_SignedMoney,
								"jci6_SignedMoney");
						text_approved_amount.setEnabled(false);
					} else {
						text_approved_amount.setText("");
					}

					if (combo_pdx.getText().equals("") || EQU_VALUE.equals("0")
							|| EQU_VALUE.equals("null") || data == null) {
						btn_import.setEnabled(false);
					}
				} catch (Exception e2) {
					// TODO: handle exception
				} finally {
					// 关闭
					System.out.println("combo_version 关闭");
					tool.closeExcelFile(false);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// text_EQU事件监
		text_EQU.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {

				if ((!isNumeric(e.text) || e.text.equals("0"))) {
					btn_import.setEnabled(false);
				} else {
					btn_import.setEnabled(true);
				}
			}
		});

		// text_EQU_Signdate.setText("");

		// text_Calc_Date.setText("");

		// text_calculated_by.setText("");

		text_approved_amount.setText("");

		text_PDx_Sign_Date.setText("");
	}

	/*
	 * 
	 */
	private boolean isPDXNameExists(Vector<String> arrays, String target) {
		boolean flag = false;
		for (int i = 0; i < arrays.size(); i++) {
			String substring = arrays.get(i).substring(
					arrays.get(i).indexOf("/") + 1, arrays.get(i).indexOf(";"));

			if (substring.equals(target)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/*
	 * 获得首选项 需要TCSession和首选项的名字
	 */
	private String[] getTCPreferenceArray(TCSession tcsession,
			String preferenceName) {
		String[] preString = null;
		TCPreferenceService tcpreservice = tcsession.getPreferenceService();
		preString = tcpreservice.getStringArray(
				TCPreferenceService.TC_preference_site, preferenceName);
		return preString;
	}

	// 根据首选项的字符串截取,在解析excel中的数据，读取excel中指定的数据
	private String getDate(JacobEReportTool tool, String sheet, String col,
			int row, Dispatch sheetsAll) {

		String value = "";
		try {
			Dispatch sheet1 = tool.openExcelFile(sheetsAll, sheet);
			value = tool.getDataFromExcel(col, row, sheet1);
			// tool.closeExcelFile(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	// 递归方法创建子目录
	public void listFiles(AIFComponentContext context, TreeItem item)
			throws Exception {
		AIFComponentContext[] contexts = context.getComponent().getChildren();
		if (contexts != null && contexts.length > 0) {
			for (int i = 0; i < contexts.length; i++) {
				if (contexts[i].getComponent() instanceof TCComponentDataset) {
					TreeItem subItem = new TreeItem(item, SWT.NONE);
					TCComponentDataset dataset = (TCComponentDataset) contexts[i]
							.getComponent();
					subItem.setText(contexts[i].toString());
					subItem.setData(dataset);
					listFiles(contexts[i], subItem);
				}
			}
		}
	}

	// 得到八个属性的值
	private void getAllValues(HashMap<String, String> hashmap) {
		String string = null;
		String text_PDx = combo_pdx.getText();
		if (text_PDx != null) {
			if (text_PDx.indexOf("/") > 0) {
				string = text_PDx.substring(0, text_PDx.indexOf("/"));
			} else {
				string = combo_pdx.getText();
			}
		} else {
			String sel = combo_pdx.getItem(0);
			if (sel.indexOf("/") > 0) {
				string = sel.substring(0, sel.indexOf("/"));
			} else {
				string = sel;
			}
		}

		hashmap.put("PDx_Squence", string.trim());
		hashmap.put("GEBT_Version", combo_version.getText());
		String equ = text_EQU.getText();
		hashmap.put("EQU", cutString(equ, 3));
		// hashmap.put("calculated_by", text_calculated_by.getText());
		String amount = text_approved_amount.getText();
		hashmap.put("approved_amount", amount);
		hashmap.put("remark", remarkText.getText());

		/*
		 * if (text_EQU_Signdate.getText() != null ||
		 * !text_EQU_Signdate.getText().equals("")) {
		 * 
		 * 
		 * hashmap.put("EQU_Signdate", text_EQU_Signdate.getText()); if
		 * (text_EQU_Signdate.getDate() != null) { hashmap.put("EQU_Signdate",
		 * sdf.format(text_EQU_Signdate.getDate())); } } else {
		 * hashmap.put("EQU_Signdate", ""); }
		 */

		/*
		 * if (text_Calc_Date.getText() != null ||
		 * !text_Calc_Date.getText().equals("")) {
		 * 
		 * hashmap.put("PDx_Sign_Date", text_Calc_Date.getText());
		 * 
		 * if (text_Calc_Date.getDate() != null) { hashmap.put("PDx_Sign_Date",
		 * sdf.format(text_Calc_Date.getDate())); }
		 * 
		 * } else { // System.out.println("没有找到Calc_Date");
		 * hashmap.put("PDx_Sign_Date", ""); }
		 */

		if (text_PDx_Sign_Date.getText() != null
				|| !text_PDx_Sign_Date.getText().equals("")) {

			System.out.println("--text_PDx_Sign_Date----"
					+ text_PDx_Sign_Date.getText());
			hashmap.put("PDx_Sign_Date", text_PDx_Sign_Date.getText());

			if (text_PDx_Sign_Date.getDate() != null) {
				hashmap.put("PDx_Sign_Date",
						sdf.format(text_PDx_Sign_Date.getDate()));
			}

		} else { // System.out.println("没有找到PDx_Sign_Date");
			hashmap.put("PDx_Sign_Date", "");
		}

	}

	// 修改掉原来的属性
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

	// 开旁路
	private void openByPass() {
		try {
			session.getUserService().call("open_or_close_pass",
					new Object[] { 1 });

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 关闭旁路
	private void closeByPass() {
		try {
			session.getUserService().call("open_or_close_pass",
					new Object[] { 0 });
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 修改TC中已发布对象的属性
	private void setDataByPass(TCComponentItemRevision revision, String name,
			String value) throws TCException, Exception {
		if (name.equals("jci6_EQUSignDate") || name.equals("jci6_CalcDate")
				|| name.equals("jci6_PDxSignDate")) {

			TCDateFormat format_temp = new TCDateFormat(session);
			SimpleDateFormat sdf_Temp = format_temp.askDefaultDateFormat();
			if (!value.equals("")
					&& value.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])")) {
				if (name.equals("jci6_CalcDate")) {

					Date date_CalcDate = target
							.getDateProperty("creation_date");
					revision.setDateProperty("jci6_CalcDate", date_CalcDate);
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

	// PDx_Squence的LOV的值
	private String[] getLovStrings(TCComponentItem componentItem,
			String property_string) {
		String[] fullNames = null;
		try {

			if (componentItem == null) {
				System.out.println("item is NULL!");
			}
			fullNames = componentItem.getLatestItemRevision()
					.getTCProperty(property_string).getLOV().getListOfValues()
					.getValuesFullNames();
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fullNames;
	}

	// 在SWT上显示首选项里配置的excel数据集的位置的指定属性
	private void setTextValueFromExcel(String value, String nameOfProperty) {
		// 更新4.3，不是找版本下的值，而是在数据集里找到相应的属性，显示到SWT的组件上去

		if (nameOfProperty.equals("jci6_GEBTTemplate")) {
			String[] items = combo_version.getItems();
			for (int i = 0; i < items.length; i++) {
				if (combo_version.getItem(i).equals(value)) {
					combo_version.select(i);
				}
			}
		} else if (nameOfProperty.equals("jci6_PDxSeq")) {
			String[] items_PDx = combo_pdx.getItems();
			for (int i = 0; i < items_PDx.length; i++) {
				if (combo_pdx.getItem(i).equals(value)) {
					combo_pdx.select(i);
				}
			}
		} /*
		 * else if (nameOfProperty.equals("jci6_EQUSignDate")) { Date date; try
		 * { date = new Date(value); } catch (Exception e) { date = null; }
		 * text_EQU_Signdate.setDate(date); if (date == null) {
		 * text_EQU_Signdate.setText(""); } else {
		 * text_EQU_Signdate.setText(sdf.format(date));
		 * text_EQU_Signdate.setEnabled(false); }
		 * 
		 * } else if (nameOfProperty.equals("jci6_CalcDate")) { Date date; try {
		 * date = new Date(value); } catch (Exception e) { date = null; }
		 * text_Calc_Date.setDate(date); if (date == null) {
		 * text_Calc_Date.setText(""); } else {
		 * text_Calc_Date.setText(sdf.format(date));
		 * text_Calc_Date.setEnabled(false); } }
		 */

		/*
		 * // 能够显示jci6_Responsibility的属性值 else if
		 * (nameOfProperty.equals("jci6_Responsibility")) { String[]
		 * items_calculated_by = text_calculated_by.getItems(); for (int i = 0;
		 * i < items_calculated_by.length; i++) { if
		 * (text_calculated_by.getItem(i).equals(value)) {
		 * text_calculated_by.setEnabled(true); text_calculated_by.select(i);
		 * text_calculated_by.setEnabled(false); } } }
		 */else if (nameOfProperty.equals("jci6_SignedMoney")) {
			text_approved_amount.setText(value);
		} else if (nameOfProperty.equals("jci6_PDxSignDate")) {
			Date date;
			try {
				date = new Date(value);
			} catch (Exception e) {
				date = null;
			}
			text_PDx_Sign_Date.setDate(date);
			if (date == null) {
				text_PDx_Sign_Date.setText("");
			} else {
				text_PDx_Sign_Date.setText(sdf.format(date));
				text_PDx_Sign_Date.setEnabled(false);
			}
		}

	}

	// 查找GBET的版本
	private String searchGEBTVersion_value(String lov_values,
			HashMap<String, String> maps_value, JacobEReportTool tool,
			Dispatch sheetsAll) {
		String index = null;
		// 得到"YFJC_GEBT_Attribute_Location"首选项
		String[] strings_Preference = getTCPreferenceArray(session,
				YFJC_GEBT_Attribute_Location);
		// ArrayList<String> EQU_lists = new ArrayList<String>();
		// System.out.println("---------" + strings_Preference[0]);

		HashMap<String, String> map = new HashMap<String, String>();
		int position = -1;

		for (int i = 0; i < strings_Preference.length; i++) {
			// 以“|”分隔
			String[] strings = strings_Preference[i].split("\\|");
			String equ_value = null;

			for (int j = 0; j < strings.length; j++) {
				if (strings[j].contains("jci6_EQU")) {
					equ_value = strings[j];
					break;
				}
			}

			if (equ_value == null) {
				continue;
			}

			String[] split = equ_value.split("=");
			// GEBT版本
			String ver = split[0].trim();//jci6_EQU_V3.1
			map.put(ver, split[1].trim());
			// 得到版本“V1”，“V2”
			String[] split2 = ver.split("_");

			if (split2.length == 3) {
				// 找到对应的版本
				if (lov_values.contains(split2[2])) {
					// combo_version.setEnabled(false);
					index = ver;
					position = i;
					System.out.println("-----------找到对应的GEBT版本 :  " + position);
				}
			}
		}

		// 如果没找到，就用默认的"EQU"版本
		if (index == null) {
			btn_import.setEnabled(false);
			// index = "jci6_EQU";
			// for (int i = 0; i < strings_Preference.length; i++) {
			// // 规定了EQU必须设置在最前
			// if (strings_Preference[i].startsWith("jci6_EQU=")) {
			// position = i;
			// }
			// }

			// 5.3报错，弹出告警对话框
			ImportDialogError error = new ImportDialogError(display, 2);
		}

		// 以“|”分割
		String[] split_value = strings_Preference[position].split("\\|");

		for (int i = 0; i < split_value.length; i++) {
			// System.out.println("------" + split_value[i]);
			// 得到相应的jci6的属性名和值，然后读取exccel
			String[] split_property = split_value[i].split("=");

			// "jci6_CalcDate"是创建的时间---excel文件的时间
			/*
			 * if (split_property[0].contains("jci6_CalcDate")) {
			 * maps_value.put("jci6_CalcDate", split_property[1]); } else
			 */

			if (split_property[0].contains("jci6_PDxSignDate")) {
				maps_value.put("jci6_PDxSignDate", split_property[1]);
			} else if (split_property[0].contains("jci6_PDxSeq")) {
				maps_value.put("jci6_PDxSeq", split_property[1]);
			} else if (split_property[0].contains("jci6_EQUSignDate")) {
				maps_value.put("jci6_EQUSignDate", split_property[1]);
			} else if (split_property[0].contains("jci6_Responsibility")) {
				maps_value.put("jci6_Responsibility", split_property[1]);
			} else if (split_property[0].contains("jci6_GEBTTemplate")) {
				maps_value.put("jci6_GEBTTemplate", split_property[1]);
			} else if (split_property[0].contains("jci6_SignedMoney")) {
				maps_value.put("jci6_SignedMoney", split_property[1]);
			}
		}

		// EQU=Content{.}C536
		// 读取相应的值，找到excel中的cell
		String sheet = map.get(index).substring(0, map.get(index).indexOf("{"));
		String postion = map.get(index).substring(
				map.get(index).lastIndexOf("}") + 1);

		// 4.18---更新加一个“-”分隔，可以找到列的字母和行的位置的数字
		String[] split_Cell = postion.split("-");

		int row = Integer.parseInt(split_Cell[1]);

		String col = split_Cell[0];

		// 得到excel中指定的值
		String value = getDate(tool, sheet, col, row, sheetsAll);

		String str = "";
		// value 是否为浮点数
		if (!value.matches("^(-?\\d+)(\\.\\d+)?$")) {
			str = "0";
			// 告警
			// MessageBox.post("EQU" + reg.getString("error"), "warning",
			// MessageBox.WARNING);
		} else {
			// 去掉多余的0
			str = cutZero(value);
		}

		return str;
	}

	/**
	 * 去掉小数点后面多余的零
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

	// 读取字符串，在excel中找到数据
	private String getValue(HashMap<String, String> map, String index,
			JacobEReportTool tool, Dispatch sheetsAll) {

		// 读取相应的值，找到excel中的cell
		if (map.get(index) != null) {

			String sheet = map.get(index).substring(0,
					map.get(index).indexOf("{"));

			// 测试
			String postion = map.get(index).substring(
					map.get(index).lastIndexOf("}") + 1);

			String[] split_Cell = postion.split("-");

			int row = Integer.parseInt(split_Cell[1]);

			String col = split_Cell[0];

			// 得到excel中指定的值
			String value = getDate(tool, sheet, col, row, sheetsAll);

			return value;
		} else {
			return "";
		}
	}

	// 发布版本，冻结
	private void releaseRev(TCComponent ir) throws TCException {
		// 先得到ReleaseStatusType
		TCComponentReleaseStatusType rlaType = (TCComponentReleaseStatusType) session
				.getTypeComponent("ReleaseStatus");
		// 创建系统的快速发布流程create(属性名)————"TCM 已发放"
		// openByPass();
		TCComponent tcp = rlaType.create(reg.getString("TCM_release"));
		// closeByPass();

		// 保存TCComponentReleaseStatusType类对象
		// openByPass();
		tcp.save();
		// closeByPass();

		// 添加到指定的版本对象中，绑定关系
		// openByPass();
		ir.add("release_status_list", tcp);
		// closeByPass();

	}

	public boolean isNumeric(String str) {
		// 是否为浮点数
		if (str.matches("^(-?\\d+)(\\.\\d+)?$")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isData(String str) {
		// 是否为日期格式：1992-09-03
		if (str.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
			return true;
		} else {
			return false;
		}
	}

	// 判断用户选择的数据集是否发布
	private boolean isDatasetRelease(TCComponentDataset dataset)
			throws TCException {
		String release = dataset.getProperty("release_status_list");
		if (!release.equals(reg.getString("TCM_release")))
			return false;
		else
			return true;
	}

	// 5.17---- 判断用户选择的数据集是否在流程中
	private boolean isDatasetINProcesssList(TCComponentDataset dataset)
			throws TCException {
		TCComponent[] tcComponents = dataset
				.getReferenceListProperty("process_stage_list");

		if (tcComponents == null || tcComponents.length <= 0)
			return false;
		else
			return true;
	}

	// 删除费用信息对象
	private void deleteCostInfo(TCComponentItemRevision revision, boolean isf)
			throws TCException {
		TCComponent[] tcComponents = revision
				.getReferenceListProperty("IMAN_external_object_link");
		for (int i = 0; i < tcComponents.length; i++) {
			// openByPass();
			revision.remove("IMAN_external_object_link", tcComponents[i]);
			// closeByPass();

			// 选择的是版本
			if (!isf) {
				// openByPass();
				tcComponents[i].delete();
				// closeByPass();
			}
		}
	}

	// 5.27----删除无值得费用信息对象
	private void deleteCostInfo_noValue(TCComponentItemRevision revision)
			throws TCException {

		TCComponent[] tcComponents = revision
				.getReferenceListProperty("IMAN_external_object_link");
		for (int i = 0; i < tcComponents.length; i++) {
			if (isCostInfoNoValue(tcComponents[i])) {
				// openByPass();
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

	// 5.27----删除无值得费用信息对象
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

	// 得到TC中的属性名，根据系统自动变化成中英文
	private String getNameOfTC(TCComponentItem item, String name) {
		String displayName = "";
		try {
			TCComponentItemRevision revision = item.getLatestItemRevision();
			TCComponentItemRevisionType tcComponentItemRevisionType = (TCComponentItemRevisionType)

			revision.getTypeComponent();

			TCPropertyDescriptor activeStatusTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor(name);
			displayName = activeStatusTCProperty.getDisplayName();

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return displayName;
	}

	// 先检查一遍要读取的值
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
				checkValueOfHumanLaborfromExcel(
						maps_Prefer_values.get("Sheet"),
						Integer.parseInt(maps_Prefer_values.get("StartRow")),
						Integer.parseInt(maps_Prefer_values.get("EndRow")),
						Integer.parseInt(maps_Prefer_values.get("StartCol")),
						Integer.parseInt(maps_Prefer_values.get("EndCol")),
						Integer.parseInt(maps_Prefer_values.get("Position")),
						tool, sheetsAll, revision, maps.get("GEBT_Version"));
			}

			System.out.println("------检查人工费用后的------错误list的数量---"
					+ error_list.size());

			// 7.2修改---- 得到首选项的值---YFJC_HumanLabor_Position
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

			System.out.println("------检查读取人工费用元的excel后的------错误list的数量---"
					+ error_list.size());

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
	 * 检查人工费用指定excel表格
	 */
	private void checkValueOfHumanLaborfromExcel(String sheet_name,
			int startRow, int endRow, int starDate, int endDate,
			int datePosition, JacobEReportTool tool, Dispatch sheetsAll,
			TCComponentItemRevision revision, String GEBTVersion)
			throws TCException, IOException {

		// System.out.println("-----检查人工费用-------得到首选项版本前的值-----------");
		//
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

		// System.out.println("----------------------得到首选项版本以后的值---------------");
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

		// 打开excel中的sheet页
		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		endposition_labor = endDate;

		// 取得日期
		for (int col = starDate; col <= endDate; col++) {
			// 用新的方法读取excel,转化成标准日期
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
						.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
					// 报错信息
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

		// System.out.println("==6.3修改========================="
		// + endposition_labor);

		// 取得指定行数的值
		for (int i = startRow; i <= endRow; i++) {
			checkHumanLaborByline(tool, sheet_name, i, starDate,
					endposition_labor, sheet, revision,
					nameOfPreference.get("Section"),
					nameOfPreference.get("Group"));
		}

	}

	/*
	 * 检查一行人工费用sheet
	 */
	private void checkHumanLaborByline(JacobEReportTool tool,
			String sheet_name, int row, int starDate, int endDate,
			Dispatch sheet, TCComponentItemRevision revision,
			String Section_name, String Group_name) throws ServiceException,
			TCException {

		// 读取每一行的第二列,第三列的位置，并且判断属性值在tc中是否存在
		// 1.先判断首先项里知否存在
		// 2.存在再找tc中是否有值
		// 3.如果没有，return结束
		String data_key_1 = tool.getDataFromExcel("B", row, sheet);
		String data_key_2 = tool.getDataFromExcel("C", row, sheet);

		// System.out.println("-------B列的值======" + data_key_1);

		// 5.8修改--如果excel这行的行数为0.即隐藏一行就跳过。
		String height_str = tool.getHeight("B" + row, sheet);
		if (height_str.equals("0.0")) {
			return;
		}

		// System.out.println("data_key_2=====" + data_key_2);
		if (data_key_2.equals("null")) {

			// System.out.println("跳过一行！！！=====" + sheet + ": " + row);
		} else if (data_key_1.equals("null")
				&& data_key_2.equals("Total FTE's")) {

			// System.out.println("跳过一行！！！=====" + sheet + ": " + row);

		} else if (data_key_1.equals("null") && data_key_2.contains("Total")) {
			// System.out.println("跳过一行！！！=====" + sheet + ": " + row);

		} else {
			// System.out.println("data_key_2=====" + data_key_2);
			String findValue_section = null;
			String Group_value = null;

			findValue_section = isFindValue(data_key_2, session, Section_name);
			if (findValue_section.equals("")) {
				// 报错
				String error_p = sheet_name + ":" + "C" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Section", error_p);
				error_list.add(map);
			}

			// 5.6修改-----如果data_key_2为"SMTP"，它就是用特殊的组，需要在首选项里配置，找到这个首选项
			if (data_key_2.equals("SMTE")) {
				// 5.6-修改
				Group_value = isFindValue(data_key_2, session,
						YFJC_HumanLabor_SMTE_GROUP);
				// System.out.println("SMTE=======" + Group_value);
			} else {
				if (!data_key_1.equals("null")) {
					// 5.8---修改编码
					String findValue_group = isFindValue(data_key_1, session,
							Group_name);
					if (!findValue_group.equals("")) {
						// 找到首选项对应的值

						// 最新修改——group
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
						// 报错
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
				// 系统中没有对应的值
				// 报错
				String error_p = sheet_name + ":" + "B" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Group", error_p);
				error_list.add(map);
			}

			boolean b_section = isSearchvalueofPreference(findValue_section,
					"Section");

			// 修改源码---by wuwei
			if (findValue_section.contains("Resident Engineer")) {
				b_section = true;
			}

			if (!b_section && !findValue_section.equals("")) {
				// 系统中没有对应的值
				// 报错
				String error_p = sheet_name + ":" + "C" + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_Section", error_p);
				error_list.add(map);
			}

			// 取每个月的值
			for (int col = starDate; col <= endDate; col++) {

				// 获取每种费用类型的值
				// 用ExcelRW读取excel
				String data = tool.getDataFromExcel(NumToString(col), row,
						sheet);

				if (data.equals("null") || data.equals("")) {
					data = "0.0";
				}

				double dCostValue;

				// if (data.matches("^(-?\\d+)(\\.\\d+)?$")) {
				try {
					dCostValue = Double.parseDouble(data);
					// System.out.println("-----有数字----" + dCostValue);
				} catch (Exception e) {
					// 报错信息
					System.out.println("------数字转换出错-----！！！===");
					String error_p = sheet_name + ":" + NumToString(col) + row;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("error_num", error_p);
					error_list.add(map);
					return;
				}

			}

		}
	}

	/*
	 * 检查非人工费用de指定excel表格
	 */
	private void checkValueOfNon_LaborfromExcel(String sheet_name,
			int startRow, int endRow, int starDate, int endDate,
			int datePosition, JacobEReportTool tool, Dispatch sheetsAll,
			String GEBTVersion) throws ServiceException, TCException {

		System.out
				.println("----------检查非人工费用-------得到首选项版本以后的值是--------------");
		String CostType_name = searchNameOfReference(session,
				YFJC_NonLabor_CostType + "_" + GEBTVersion);
		// System.out.println("=============CostType_name============="
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
						.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
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

		// System.out.println("=========6.3修改==endposition_nonlabor==========="
		// + endposition_nonlabor);

		for (int i = startRow; i <= endRow; i++) {
			checkNonLaborByline(tool, sheet_name, i, starDate,
					endposition_nonlabor, nameOfPreference.get("CostType"),
					sheet);
		}

	}

	/*
	 * 检查一行非人工信息
	 */
	private void checkNonLaborByline(JacobEReportTool tool, String sheet_name,
			int row, int starDate, int endDate, String preferenceName,
			Dispatch sheet) throws ServiceException, TCException {

		System.out.println("-------检查一行非人工信息!!!--------");
		// 取得第一列的值,
		String cellValue = tool.getDataFromExcel("A", row, sheet);

		// 5.8修改--如果excel这行的行数为0.即隐藏一行就跳过。
		String height_str = tool.getHeight("A" + row, sheet);
		if (height_str.equals("0.0")) {
			// System.out.println("-------非人工信息跳过!!!--------");
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

			if (cellCost.matches("^(-?\\d+)(\\.\\d+)?$")) {
				try {
					dCostValue = Double.parseDouble(cellCost);
					// System.out.println("-----有数字----" + dCostValue);
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
				System.out.println("---数字不对---");
				String error_p = sheet_name + ":" + NumToString(col) + row;
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("error_num", error_p);
				error_list.add(map);
				return;
			}

		}

	}

	// 读取相应的首选项，得到excel的位置的值
	private boolean getValueOfExcelByPreference(TCSession tcsession,
			String preferenceName, String lov_values) {
		// lov_values值为“V1”，“V2”

		String index = null;

		// 读取配置人工和非人工的首选项
		TCPreferenceService tcpreservice = tcsession.getPreferenceService();

		String[] preString = tcpreservice.getStringArray(
				TCPreferenceService.TC_preference_site, preferenceName);

		HashMap<String, String> map = new HashMap<String, String>();

		for (int i = 0; i < preString.length; i++) {
			String[] split = preString[i].split("=");
			// GEBT版本
			String ver = split[0].trim();
			String[] split2 = ver.split("_");
			if (split2.length == 2) {
				// 找到对应的版本
				// System.out.println(lov_values + "=====" + split2[1]);
				if (split2[1].equals(lov_values)) {
					index = ver;
					map.put(ver, split[1].trim());
					System.out.println("----------找到对应的GEBT版本 For location："
							+ ver);
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
			return true;
		}
	}

	/**
	 * colNameToNum::根据列名找到列索引
	 * 
	 * @param String
	 *            列名（A、B等）
	 * @return int 列索引
	 */
	public int colNameToNum(String colName) {
		int result = 0;
		for (int i = 0; i < colName.length(); i++) {
			result = result * 26 + colName.charAt(i) - 65 + 1;
		}
		return result;
	}

	// 数字转成英文字母,读取excel表中的列的数值转成英文字母
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

	// 到首选项里去找属性的值
	private String isFindValue(String key, TCSession tcsession,
			String preferenceName) {
		String value = "";

		HashMap<String, String> hashMap = getTCPreferenceArray2(tcsession,
				preferenceName);

		for (String keyValue : hashMap.keySet()) {
			// 找到对应的版本
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
				// 相同
				if (flag) {
					value = hashMap.get(keyValue);
					System.out.println("2014/6/11===找到了对应首选项的值----名字："
							+ keyValue + "  value的值====" + value);

				}

			} else {
				if (keyValue.equals(key)) {
					value = hashMap.get(keyValue);
					System.out.println("2014/6/11===找到了对应首选项的值----名字：" + key
							+ "  value的值====" + value);

				}

			}
		}
		return value;
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
				System.out.println("2014/6/11===找到了对应首选项的值----名字：" + keyValue
						+ "  value的值====" + value);

			} else {
				if (keyValue.equals(key)) {
					value = hashMap.get(keyValue);
					System.out.println("2014/6/11===找到了对应首选项的值----名字：" + key
							+ "  value的值====" + value);

				}

			}
		}
		return value;
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

	// 找到TC系统里对应的属性
	// 人工信息里的组和学科
	private boolean isSearchvalueofPreference(String value, String properityName) {

		boolean flag = false;
		try {
			// 如果属性为“组”
			if (properityName.equals("Group")) {
				TCComponentGroupType groupType = (TCComponentGroupType) session
						.getTypeComponent(properityName);

				TCComponentGroup componentGroup = groupType.find(value);
				if (componentGroup != null) {
					flag = true;
				}
			} else if (properityName.equals("Section")) {
				// 查询学科
				TCComponent[] tcp = query(session, "YFJC_Search_Discipline",
						new String[] { "discipline_name" },
						new String[] { value });
				if (tcp[0] != null) {
					// System.out.println("找到了Section!!!");
					flag = true;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return flag;
	}

	// 新建查询 "学科名称"的工具---自定义查询工具
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

	// 读取人工费用指定的行的excel表格
	private void setValuefromExcel(TCComponentItemRevision revision,
			String sheet_name, int startRow, int endRow, int starDate,
			int endDate, int datePosition, JacobEReportTool tool,
			Dispatch sheetsAll) throws TCException, IOException {

		System.out.println("-----读取人工费用-------");

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
			// System.out.println("SMTE---对应得Group！！！");
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
						// System.out.println("-------------Group_Name======"
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
		TCComponent labor_CostInfo = null;
		TCComponent labor_CostInfo_hour = null;
		TCComponent labor_CostInfo_money = null;

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

		// 取每个月的值
		for (int col = startDate; col <= endDate; col++) {
			if (TYPE_HumanLaborYuan == 0) {
				if (maps_HunmanLabor == null) {
					int YEAR = Integer.parseInt(year);
					maps_HunmanLabor = new HashMap<String, HashMap<String, TCComponent>>();

					// 创建“人月”费用信息
					labor_CostInfo = createCostInfo(session, revision, "人月", 2,
							Group_value, findValue_section, YEAR, null);
					labor_CostInfo_hour = createCostInfo(session, revision,
							"小时", 2, Group_value, findValue_section, YEAR, null);

					HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
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
						}
					}

					if (!flag) {
						int YEAR = Integer.parseInt(year);

						// 创建“人月”费用信息
						labor_CostInfo = createCostInfo(session, revision,
								"人月", 2, Group_value, findValue_section, YEAR,
								null);
						labor_CostInfo_hour = createCostInfo(session, revision,
								"小时", 2, Group_value, findValue_section, YEAR,
								null);

						HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
						maps_CostInfo.put("人月", labor_CostInfo);
						maps_CostInfo.put("小时", labor_CostInfo_hour);
						maps_HunmanLabor.put(index + year, maps_CostInfo);

					} else {
						System.out.println("====存在costinfo对象===year===" + year
								+ "   " + index);
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
						}
					}

					if (!flag) {
						int YEAR = Integer.parseInt(year);
						// 创建“人月”费用信息
						labor_CostInfo = createCostInfo(session, revision,
								"人月", 2, Group_value, findValue_section, YEAR,
								null);
						labor_CostInfo_hour = createCostInfo(session, revision,
								"小时", 2, Group_value, findValue_section, YEAR,
								null);

						HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
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
					maps_HunmanLabor_yuan = new HashMap<String, HashMap<String, TCComponent>>();
					labor_CostInfo_money = createCostInfo(session, revision,
							"元", 2, Group_value, findValue_section, YEAR, null);

					HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
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
						}
					}
					if (!flag) {// 创建“元”费用信息
						int YEAR = Integer.parseInt(year);
						labor_CostInfo_money = createCostInfo(session,
								revision, "元", 2, Group_value,
								findValue_section, YEAR, null);

						HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
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
						}
					}
					if (!flag) {// 创建“元”费用信息
						int YEAR = Integer.parseInt(year);

						labor_CostInfo_money = createCostInfo(session,
								revision, "元", 2, Group_value,
								findValue_section, YEAR, null);

						HashMap<String, TCComponent> maps_CostInfo = new HashMap<String, TCComponent>();
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
			// 原来属性的值
			double val = 0;

			if (data.matches("^(-?\\d+)(\\.\\d+)?$")) {
				try {
					dCostValue = Double.parseDouble(data);
					// System.out.println("-----有数字----" + dCostValue);
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

			if (month_maps.get(col + "").equals("01")) {
				// 判断是否为0，不为0截取5位人月，4位小时数，3位元
				if (dCostValue != 0) {
					if (TYPE_HumanLaborYuan == 0) {
						String property_value = labor_CostInfo
								.getProperty("jci6_Jan");
						if (property_value != null
								&& !property_value.equals("")) {
							val = Double.parseDouble(property_value);
							System.out.println("人月人工费用信息  jci6_Jan的值不为0===="
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
							System.out.println("小时人工费用信息  jci6_Jan的值不为0===="
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

							System.out.println("元人工费用信息  jci6_Jan的值不为0===="
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
							System.out.println("人月人工费用信息  jci6_Feb的值不为0===="
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
							System.out.println("小时人工费用信息  jci6_Feb的值不为0===="
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
							System.out.println("元人工费用信息  jci6_Feb的值不为0===="
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
							System.out.println("人月人工费用信息  jci6_Mar的值不为0===="
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
							System.out.println("小时人工费用信息  jci6_Mar的值不为0===="
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
							System.out.println("元人工费用信息  jci6_Mar的值不为0===="
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
							System.out.println("人月人工费用信息  jci6_Apr的值不为0===="
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
							System.out.println("小时人工费用信息  jci6_Apr的值不为0===="
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
							System.out.println("元人工费用信息  jci6_Apr的值不为0===="
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
							System.out.println("人月人工费用信息  jci6_May的值不为0===="
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
							System.out.println("小时人工费用信息  jci6_May的值不为0===="
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
							System.out.println("元人工费用信息  jci6_May的值不为0===="
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
							System.out.println("人月人工费用信息  6月的值不为0====" + val
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
							System.out.println("小时人工费用信息  6月的值不为0===="
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
							System.out.println("元人工费用信息  6月的值不为0===="
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
							System.out.println("人月人工费用信息  7月的值不为0====" + val
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
							System.out.println("小时人工费用信息  7月的值不为0===="
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
							System.out.println("元人工费用信息  7月的值不为0===="
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
							System.out.println("人月人工费用信息  8月的值不为0====" + val
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
							System.out.println("小时人工费用信息  8月的值不为0===="
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
							System.out.println("元人工费用信息  8月的值不为0===="
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
							System.out.println("人月人工费用信息  9月的值不为0====" + val
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
							System.out.println("小时人工费用信息  9月的值不为0===="
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
							System.out.println("元人工费用信息  9月的值不为0===="
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
							System.out.println("人月人工费用信息  10月的值不为0====" + val
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
							System.out.println("小时人工费用信息  10月的值不为0===="
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
							System.out.println("元人工费用信息  10月的值不为0===="
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
							System.out.println("人月人工费用信息  11月的值不为0====" + val
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
							System.out.println("小时人工费用信息  11月的值不为0===="
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
							System.out.println("元人工费用信息  11月的值不为0===="
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
							System.out.println("人月人工费用信息  12月的值不为0====" + val
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
							System.out.println("小时人工费用信息  12月的值不为0===="
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
							System.out.println("元人工费用信息  12月的值不为0===="
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

	// 创建 ---人工费用信息和非人工费用信息
	private TCComponent createCostInfo(TCSession session,
			TCComponentItemRevision revision, String object_name, int type,
			String GroupName, String SelectionName, int year, String costType)
			throws TCException, ServiceException {
		// 创建费用信息
		DataManagementService dmService = DataManagementService
				.getService(session);

		com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[] createIn = new

		com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];

		createIn[0] = new DataManagement.CreateIn();

		createIn[0].data.boName = "JCI6_CostInfo";

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

		CreateResponse responese = dmService.createObjects(createIn);
		TCComponent component = responese.output[0].objects[0];

		TCProperty[] property = null;
		if (type == 1) {
			// 非人工费用信息
			// 用TCProperty[]来存储一系列要修改的TC中的属性名称
			System.out.println("---------创建非人工信息---------");

			property = component.getTCProperties(new String[] { "jci6_CPT",
					"jci6_CostType", "jci6_Unit", "jci6_Year" });

			property[0].setStringValueData("Budget");
			property[1].setStringValueData(costType);
			property[2].setStringValueData(name_Unit);

			// 年是int型
			property[3].setIntValueData(year);
			component.setTCProperties(property);

		} else {
			// 人工费用信息
			System.out.println("---------创建人工信息---------");

			// 用TCProperty[]来存储一系列要修改的TC中的属性名称
			property = component.getTCProperties(new String[] { "jci6_CPT",
					"jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Division",
					"jci6_RateLevel" });

			property[0].setStringValueData("Budget");
			property[1].setStringValueData("Normal Hours");
			property[2].setStringValueData(name_Unit);

			// 年是int型,
			property[3].setIntValueData(year);

			// 查询指定的组
			property[4].setReferenceValueData(componentGroup);

			// System.out.println("---学科名称-----" + SelectioName);

			// 三期ebp修改---by wuwei
			boolean flag_new = false;
			if (SelectionName.equals("Resident Engineer")) {
				System.out.println("---2014/6/11  Set RateLevel NULL---"
						+ SelectionName);
				flag_new = true;
				property[5].setReferenceValueData((TCComponent) null);
			} else {
				// 查询学科
				TCComponent[] tcp = query(session, "YFJC_Search_Discipline",
						new String[] { "discipline_name" },
						new String[] { SelectionName });
				if (tcp != null)
					property[5].setReferenceValueData(tcp[0]);
				else
					System.out.println("-----查询学科名称没找到！！！-----");
			}

			// 5.4修改----人工费用
			component.setTCProperties(property);
			if (flag_new) {

				System.out.println("---2014/6/11  SelectioName---"
						+ SelectionName);
				component.setProperty("jci6_TaskType", "tasktype26");
			}

		}

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

		return component;
	}

	// 读取非人工费用的excel表格
	private void setValuefromNonLabel(TCComponentItemRevision revision,
			String sheet_name, int startRow, int endRow, int startDate,
			int endDate, int datePosition, JacobEReportTool tool,
			Dispatch sheetsAll) throws ServiceException, TCException {
		System.out.println("-----读取非人工费用-------");

		Dispatch sheet = tool.openExcelFile(sheetsAll, sheet_name);

		HashMap<String, String> month_maps_non = new HashMap<String, String>();
		HashMap<String, String> year_maps_non = new HashMap<String, String>();

		// 取得日期
		for (int col = startDate; col <= endDate; col++) {
			// 用新的方法读取excel
			String column = NumToString(col);
			String data_string = tool.getDataFromExcel(column, datePosition,
					sheet);

			String data = sdf.format(new Date(data_string));

			// 是否为标准日期
			if (data.matches("(\\d{2}|\\d{4})-((0?[1-9])|(1[012]))-(0?[1-9]|[12][0-9]|3[01])\\s*((0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]))?")) {
				String year = data.substring(0, data.indexOf("-"));
				year_maps_non.put(col + "", year);
				// System.out.println("date------" + year_maps_non.get(col +
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

	// 非人工信息
	// 读取一条信息的方法
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

		System.out.println("\n读取非人工单元格----第一列的值---->" + cellValue
				+ "   findValue---->" + findValue + "   costType---->"
				+ costType);

		// 需要设置的费用信息
		TCComponent component_costInfo;

		// 取每个月的值
		for (int col = startDate; col <= endDate; col++) {

			if (!year.equals(year_maps_non.get(col + ""))) {
				// 本来要创建新的费用信息，但是如果有了就不需要创建
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
					// 如果hashmap里没有
					if (!flag) {

						int Year = Integer.parseInt(year);
						// 创建费用信息
						TCComponent createCostInfo = createCostInfo(session,
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

				cellCost = cellCost + "00";

				if (cellCost.matches("^(-?\\d+)(\\.\\d+)?$")) {
					try {
						dCostValue = Double.parseDouble(cellCost);
						// System.out.println("-----有数字----" + dCostValue);
					} catch (Exception e) {
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
						// component_costInfo.setProperty("jci6_Feb",
						// cutString(stringValue, 3));
						component_costInfo.setDoubleProperty("jci6_Feb",
								Double.parseDouble(cutString(stringValue, 3)));
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

	// 得到系统的时间
	private String getSystemTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		Date currentTime = new Date();
		String dateString = format.format(currentTime);
		return dateString;
	}

	// 截取i位小数
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

	// 6.5---修改=新弄个方法---查找首选项名
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
