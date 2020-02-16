/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.teamcenter.rac.project.views;

//反编译界面
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.aif.kernel.IOperationService;
import com.teamcenter.rac.aifrcp.AifrcpPlugin;
import com.teamcenter.rac.common.lov.LOVComboBox;
import com.teamcenter.rac.common.lov.view.controls.LOVDisplayer;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.SessionChangedEvent;
import com.teamcenter.rac.kernel.SessionChangedListener;
import com.teamcenter.rac.kernel.SoaUtil;
import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentGroupType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.project.Messages;
import com.teamcenter.rac.project.ProjectDataPanel;
import com.teamcenter.rac.project.ProjectUIPostActions;
import com.teamcenter.rac.project.common.ProjectConstants;
import com.teamcenter.rac.project.common.ProjectManager;
import com.teamcenter.rac.project.common.ProjectTeamStatus;
import com.teamcenter.rac.project.common.ProjectTeamTreeManager;
import com.teamcenter.rac.project.filter.ProgramTreeFilter;
import com.teamcenter.rac.project.nodes.ProjectRootNode;
import com.teamcenter.rac.project.nodes.ProjectTeamContentNode;
import com.teamcenter.rac.stylesheet.PropertyArray;
import com.teamcenter.rac.stylesheet.PropertyLOVDisplayer;
import com.teamcenter.rac.stylesheet.PropertyLOVUIComponent;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.ui.views.AbstractContentViewPart;
import com.teamcenter.rac.util.AdapterUtil;
import com.teamcenter.rac.util.ConfirmationDialog;
import com.teamcenter.rac.util.DateButton;
import com.teamcenter.rac.util.EmbeddedComposite;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.ImageUtilities;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.OSGIUtil;
import com.teamcenter.rac.util.Painter;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.SplitPane;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.iTextArea;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.combobox.iComboBox;
import com.teamcenter.rac.util.controls.DateControl;
import com.teamcenter.rac.util.log.Debug;
import com.teamcenter.rac.vns.model.IContentView;
import com.teamcenter.services.rac.core.ProjectLevelSecurityService;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectClientId;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectOpsOutput;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectOpsResponse;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectTeamData;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectTeamsResponse;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.TeamMemberInfo;
import com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.CopyProjectsInfo2;
import com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ModifyProjectsInfo2;
import com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ProjectInformation2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.omg.PortableInterceptor.INACTIVE;

import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity;
import com.teamcenter.soa.client.model.ErrorValue;

public class ProjectDefinitionView extends AbstractContentViewPart implements
		SessionChangedListener, IContentView {

	private Composite m_headerComposite = null;
	private Button m_createButton = null;
	private Button m_modifyButton = null;
	private Button m_copyButton = null;
	private Button m_deleteButton = null;
	private Button m_clearButton = null;
	private Text m_idText = null;
	private Text m_nameText = null;
	private Text m_descriptionText = null;
	private LOVDisplayer m_categoryCombo;
	private Button m_activeStatusBtn = null;
	private Button m_inactiveStatusBtn = null;
	private Button m_inactiveandInviisibleStatusBtn = null;
	private static ProjectTeamTreeManager m_projectTeamManager;
	private TCComponentProject project = null;
	private TCSession session = RACUIUtil.getTCSession();

	// modify by wuwei
	private final Registry reg = ProjectConstants.projectReg;

	private static final String BURFLR_NAME = "com.teamcenter.rac.project.views.views_locale";
	private static final ResourceBundle registry1 = ResourceBundle
			.getBundle(BURFLR_NAME);

	private String projectID;
	private String projectName;
	private String projectDesc;
	private String projectCollaborationCategories;
	private boolean useProgramSec = false;
	// private boolean projectStatus = false;
	private boolean projectActiveStatus = false;

	private boolean projectVisibility = false;
	private ProjectLevelSecurity.ProjectTeamData[] projectTeamMembers = null;
	private ProjectLevelSecurity.ProjectOpsResponse result = null;
	private boolean isPA = false;
	private boolean isPTA = false;
	private ArrayList<ProjectLevelSecurity.TeamMemberInfo> infos = null;
	private boolean isModified = false;
	private boolean isNewlyCreated = false;
	private boolean isDirty = false;
	private boolean isLoading = false;
	private ProjectUIPostActions projectUIPostActions = null;
	private static final Logger logger = Logger
			.getLogger(ProjectDefinitionView.class);

	private Text m_parentText;
	private String m_parentProject = null;
	private Button m_ProgramBtn = null;
	private Button m_ProjectBtn = null;
	private Button m_teamInheritance = null;
	private int teamInheritanceValue;
	private static ProjectDefinitionView pDefView;

	// 反编译添加
	private TCComponentItemRevisionType tcComponentItemRevisionType;
	private TCComponentItemType tcComponentItemType;
	private StringBuffer buff;
	private String QUERY_NAME = "YFJC_Search_UserByName";
	private TCUserService userService;
	private Separator sepl;
	private JPanel typePanel2;
	private String[] lovSecValues;
	private TCComponentItemRevision selectItemRevision;

	// 项目基础
	private LOVComboBox oemNameLov;// 客户名称
	private iTextField modeNameField;
	// private JButton helpButton;
	private LOVComboBox secLov;// 所属科室
	private LOVComboBox diviLov;// 所属部门
	public PropertyLOVUIComponent tlLov;// 项目负责人
	private LOVComboBox targetPosField;// 目标可能性百分比
	// add by wuh 2014-5-26
	private DateButton createDateButton;// 创建日期
	private DateButton closeDateButton;// 关闭日期
	// add by wuh 2014-6-5
	// private JRadioButton eqmRadioButton;
	// 项目类型
	private iComboBox activeStatusBox;// 激活状态
	private iComboBox projectStatusBox;// 项目状态
	private iComboBox categoryBox;// 种类
	private LOVComboBox productLov;// 产品
	private LOVComboBox typeLov;// 类型
	// 项目日期
	private DateButton pStartDate;// 项目开始日期
	private DateButton pEndDate;// 项目结束日期
	private DateButton lcDate;// 量产日期
	private LOVComboBox sopfyLov;// 量产年
	private LOVComboBox modelYearLov;// 车型年

	// 功能配置
	private iTextField epicTextField;// EPIC号
	private iTextField functionTextField;// 功能配置
	private LOVComboBox frontSSLov;// 前排骨架
	private LOVComboBox rearSSLov;// 后排骨架
	private LOVComboBox trackLov;// 滑道
	private LOVComboBox reclinerLov;// 调角器
	private LOVComboBox pumpLov;// 手动高调
	private LOVComboBox vtaLov;// 电动高调
	private LOVComboBox latchLov;// 锁
	// 5.12--新增jci6_SMTE_Variables的LOV
	private PropertyArray SMTE_lov;

	// PDX信息
	private LOVComboBox pxdLov;// pdx版本

	// 5.6修改
	// private PropertyObjectLink latestLink;
	private LOVComboBox latestComboLov;// GEBT的BudgetState
	private LOVComboBox gebtLov;// GEBT版本版本
	private iTextField equTextField;// EQU
	// private DateButton equSignDate;// EQU批准日期---不需要
	// private DateButton calcDate;// Calc日期---不需要
	// private LOVComboBox calcByLov;// Valc人员---不需要
	private iTextField approvedAmountTextField;// Approved Amount
	private DateButton pdxSignDate;// PDX版本批准日期日期

	// 5.31修改
	private JTextArea remarkField;

	/*
	 * 定义所有属性加载时的值，以方便判断修改后变化来决定修改按钮状态，所有属性加载值都是在loadData中赋值的，遵循系统原先逻辑
	 */
	// 项目基础
	private String oemNameStr;// 客户名称
	private String modeNameStr;
	private String secStr;// 所属科室
	private String diviStr;// 所属部门
	private String tlStr;// 项目负责人

	// add by wuh 2014-5-26
	private Date createDateStr;// 创建日期
	private Date closeDateStr;// 关闭日期
	// add 2014-6-5
	// private boolean eqmStr;
	private String targetPosStr;// 目标可能性百分比
	// 项目类型
	private String activeStatusStr;// 激活状态
	private String projectStatusStr;// 项目状态
	private String categoryStr;// 种类
	private String productStr;// 产品
	private String typeStr;// 类型
	// 项目日期
	private Date pStartStr;// 项目开始日期
	private Date pEndStr;// 项目结束日期
	private Date lcStr;// 量产日期
	private String sopfyStr;// 量产年
	private String modelYearStr;// 车型年
	// 功能配置
	private String epicStr;// EPIC号
	private String functionStr;// 功能配置
	private String frontSSStr;// 前排骨架
	private String rearSSStr;// 后排骨架
	private String trackStr;// 滑道
	private String reclinerStr;// 调角器
	private String pumpStr;// 手动高调
	private String vtaStr;// 电动高调
	private String latchStr;// 锁
	// 5.12-SMTE
	// private String SMTEStr;

	// PDX信息

	private String pxdStr;// pdx版本
	private String gebtStr;// GEBT版本版本
	private String equStr;// EQU
	// private Date equSignStr;// EQU批准日期-- 不需要
	// private Date calcStr;// Calc日期--不需要
	// private String calcByStr;// Valc人员--buxuyao
	private String approvedAmountStr;// Approved Amount
	private Date pdxSignStr;// PDX版本批准日期日期
	// 5.6修改
	private String budgetStateStr;// BudgetState的lov值

	// 5.12-SMTE
	// private String SMTEStr;

	// PDX信息

	private java.awt.Frame myframe = null;

	public ProjectDefinitionView() {
		System.out.println("===wuwei modify ProjectDefinitionView======");
		try {
			isPA = false;
			isPTA = false;

			tcComponentItemType = (TCComponentItemType) session
					.getTypeComponent("JCI6_ProgramInfo");
			tcComponentItemRevisionType = (TCComponentItemRevisionType) session
					.getTypeComponent("JCI6_ProgramInfoRevision");

			userService = session.getUserService();
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setShowHeader(true);
		setMultiSelectionSupported(false);
		setProcessNullSelectionsEnabled(true);
		setComponentEventListeningEnabled(true);
		AbstractTCAdminApplicationViewHelper localAbstractTCAdminApplicationViewHelper = ProjectManager
				.getApp();

		try {
			localAbstractTCAdminApplicationViewHelper
					.checkApplicationPriviledgeWithMessage();
		} catch (Exception localException) {
			logger.error(localException.getClass().getName(), localException);
		}
		this.isPA = localAbstractTCAdminApplicationViewHelper.isPAPrivileged();
		this.isPTA = localAbstractTCAdminApplicationViewHelper
				.isPTAPrivileged();

		loadPostActionExtension();
	}

	protected void createContent(Composite paramComposite) {
		paramComposite.setLayout(new FillLayout(512));
		SashForm localSashForm = new SashForm(paramComposite, 512);
		localSashForm.setSashWidth(5);
		localSashForm.setLayout(new FillLayout());
		getToolkit().adapt(localSashForm, true, true);
		ViewForm localViewForm1 = new ViewForm(localSashForm, 16777216);
		getToolkit().adapt(localViewForm1, true, true);
		ViewForm localViewForm2 = new ViewForm(localSashForm, 16777216);
		getToolkit().adapt(localViewForm2, true, true);

		System.out.println("ProjectDefinitionView:: createContent");

		// add by wuewi
		ViewForm localViewForm3 = new ViewForm(localSashForm, 16777216);
		getToolkit().adapt(localViewForm3, true, true);
		Composite topFormComposite = createTopForm(localViewForm3);

		// localViewForm1.setContent(createTopForm(localViewForm1));

		// add by wuwei
		// /Composite buttonPanel = createButtons(localViewForm3);
		// localViewForm3.setContent(buttonPanel);
		// localViewForm3.setContent(buttonPanel);
		// localViewForm3.setBorderVisible(true);
		localViewForm2.setContent(createBottomForm(localViewForm2));
		localViewForm2.setBorderVisible(true);

		localSashForm.setWeights(new int[] { 50, 50, 2 });

		System.out.println("---ProjectDefinitionView -SWT_AWT.new_Frame---");
		Composite localComposite1 = new EmbeddedComposite(localViewForm1, 0);

		localComposite1.setLayout(new FillLayout());
		myframe = SWT_AWT.new_Frame(localComposite1);
		myframe.setLayout(new BorderLayout());

		System.out
				.println("----ProjectDefinitionView initializePanel(frame)---");
		initializePanel(myframe);

		localViewForm1.setContent(localComposite1);

		getTCSession().addSessionChangeListener(this);
	}

	// modify by wuwei
	protected Control createHeadControl(Composite paramComposite) {
		Composite localComposite = createHeaderComposite(paramComposite);
		createButtons(localComposite);
		getScrolledForm().getForm().setHeadClient(this.m_headerComposite);
		getScrolledForm().getForm().getHead().update();

		return this.m_headerComposite;
	}

	public boolean isDirtyStill() {
		boolean bool = m_projectTeamManager.teamModified();
		return ((this.isDirty) || (bool));
	}

	public boolean isLoading() {
		return this.isLoading;
	}

	public void setLoading(boolean paramBoolean) {
		this.isLoading = paramBoolean;
	}

	public void setXMJCPanelEnabled(boolean flag) {
		if (m_idText != null)
			m_idText.setEnabled(flag);

		if (m_nameText != null)
			m_nameText.setEnabled(flag);

		if (m_descriptionText != null)
			m_descriptionText.setEnabled(flag);

		if (idField != null)
			idField.setEnabled(flag);

		if (nameField != null)
			nameField.setEnabled(flag);

		if (descField != null)
			descField.setEnabled(flag);

		// if(helpButton!=null)
		// helpButton.setEnabled(flag);

		if (oemNameLov != null)
			oemNameLov.setEnabled(flag);

		if (modeNameField != null)
			modeNameField.setEnabled(flag);

		if (secLov != null)
			secLov.setEnabled(flag);

		if (diviLov != null)
			diviLov.setEnabled(flag);

		if (tlLov != null)
			tlLov.setEnabled(flag);
		// add by wuh 2014-5-26
		if (closeDateButton != null)
			closeDateButton.setEnabled(flag);

		// if(eqmRadioButton!=null)
		// eqmRadioButton.setEnabled(flag);

		if (targetPosField != null)
			targetPosField.setEnabled(flag);
	}

	/*
	 * 设置项目类型页是否可编辑
	 */
	public void setXMLXPanelEnabled(boolean flag) {
		activeStatusBox.setEnabled(flag);
		projectStatusBox.setEnabled(flag);
		categoryBox.setEnabled(flag);
		productLov.setEnabled(flag);
		typeLov.setEnabled(flag);
	}

	/*
	 * 
	 * 设置项目日期页是否可编辑
	 */
	public void setXMRQPanelEnabled(boolean flag) {
		pStartDate.setEnabled(flag);
		pEndDate.setEnabled(flag);
		lcDate.setEnabled(flag);
		sopfyLov.setEnabled(flag);
		modelYearLov.setEnabled(flag);
	}

	/*
	 * 
	 * 设置功能配置页是否可编辑
	 */
	public void setGNPZPanelEnabled(boolean flag) {
		epicTextField.setEnabled(flag);
		functionTextField.setEnabled(flag);
		frontSSLov.setEnabled(flag);
		rearSSLov.setEnabled(flag);
		trackLov.setEnabled(flag);
		reclinerLov.setEnabled(flag);
		pumpLov.setEnabled(flag);
		vtaLov.setEnabled(flag);
		latchLov.setEnabled(flag);
		// 5.12--新增加一个SMTE属性
		SMTE_lov.setEnabled(flag);
	}

	/*
	 * 
	 * 设置PDX信息页是否可编辑
	 */
	public void setPDXPanelEnabled(boolean flag) {
		pxdLov.setEnabled(flag);
		// 5.6修改
		// latestLink.setEnabled(flag);
		latestComboLov.setEnabled(flag);
		gebtLov.setEnabled(flag);
		equTextField.setEnabled(flag);
		// equSignDate.setEnabled(flag);
		// calcDate.setEnabled(flag);
		// calcByLov.setEnabled(flag);
		approvedAmountTextField.setEnabled(flag);
		pdxSignDate.setEnabled(flag);
		remarkField.setEnabled(flag);
	}

	public void disableButtons() {
		if (Display.getCurrent() != null) {
			this.m_createButton.setEnabled(false);
			this.m_modifyButton.setEnabled(false);
			this.m_copyButton.setEnabled(false);
			this.m_deleteButton.setEnabled(false);
			this.m_clearButton.setEnabled(false);
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ProjectDefinitionView.this.m_createButton.setEnabled(false);
					ProjectDefinitionView.this.m_modifyButton.setEnabled(false);
					ProjectDefinitionView.this.m_copyButton.setEnabled(false);
					ProjectDefinitionView.this.m_deleteButton.setEnabled(false);
					ProjectDefinitionView.this.m_clearButton.setEnabled(false);
				}
			});
		}
	}

	public void enableButtons() {
		if (Display.getCurrent() != null) {
			validateButtons();
			this.m_clearButton.setEnabled(true);
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ProjectDefinitionView.this.validateButtons();
					ProjectDefinitionView.this.m_clearButton.setEnabled(true);
				}
			});
		}
	}

	/*
	 * 
	 * 创建项目信息方法
	 */
	public TCComponentItem createItemForItk() {
		buff = new StringBuffer();
		String projectId = getProjectID();
		String projectName = getProjectName();
		String epic = epicTextField.getText();
		buff.append(projectId).append(";").append(projectName).append(";")
				.append("JCI6_ProgramInfo");

		// 项目基础
		String oemName = getComponentValue(oemNameLov);
		String modeName = modeNameField.getText();
		String sec = getComponentValue(secLov);
		String divi = getComponentValue(diviLov);
		String tl = getComponentValue(tlLov);
		String targetPos = targetPosField.getTextField().getText();// getComponentValue(targetPosField);
		// add by wuh 2014-5-26
		Date closeDate = closeDateButton.getDate();
		// add 2014-6-5
		// boolean eqmflag = eqmRadioButton.isSelected();

		// 项目类型
		boolean activeStatusLogic = false;
		String activeStatus = getComponentValue(activeStatusBox);
		if ("Active".equals(activeStatus)) {
			activeStatusLogic = true;
			projectActiveStatus = true;
		} else {
			projectActiveStatus = false;
		}
		String projectStatus = getComponentValue(projectStatusBox);
		String category = getComponentValue(categoryBox);
		String product = getComponentValue(productLov);
		String type = getComponentValue(typeLov);

		// 项目日期
		Date pStart = pStartDate.getDate();
		Date pEnd = pEndDate.getDate();
		Date lc = lcDate.getDate();
		String sopfy = getComponentValue(sopfyLov);
		String modelYear = getComponentValue(modelYearLov);

		// 功能配置
		String function = functionTextField.getText();
		String frontSS = getComponentValue(frontSSLov);
		String rearSS = getComponentValue(rearSSLov);
		String track = getComponentValue(trackLov);
		String recliner = getComponentValue(reclinerLov);
		String pump = getComponentValue(pumpLov);
		String vta = getComponentValue(vtaLov);
		String latch = getComponentValue(latchLov);
		// 5.12-SMTE
		// String SMTEs = getComponentValue(SMTE_lov);

		// PDX信息默认只读
		String equ = equTextField.getText();
		if ("".equals(equ))
			equ = "0";

		TCComponentItem newItem = null;
		try {
			Object obj[] = new Object[1];
			obj[0] = buff.toString();
			Object ret = session.getUserService().call("jc6_create_item", obj);
			TCComponentItemType tcComponentItemType = (TCComponentItemType) session
					.getTypeComponent("JCI6_ProgramInfo");
			newItem = tcComponentItemType.find(getProjectID());
			if (newItem != null) {

				System.out.println("---创建项目信息开始----");

				String propProInfoNames[] = { "jci6_OEMName",
						"jci6_ProgramSec", "jci6_ProgramDivi",
						"jci6_ProjectTL", "jci6_TargetPos",
						"jci6_ActiveStatus", "jci6_ProgramState",
						"jci6_Category", "jci6_Product", "jci6_Type",
						"jci6_StartDate", "jci6_EndDate",
						"jci6_SOP",
						"jci6_SOPFY",
						"jci6_ModelYear",// "jci6_EPIC_PN",
						"jci6_Function", "jci6_FrontSS", "jci6_RearSS",
						"jci6_Track", "jci6_Recliner", "jci6_PUMP", "jci6_VTA",
						"jci6_Latch", "jci6_EQU", "jci6_OEMModelName" };
				// "use_program_security" };
				TCComponentItemRevision newIr = newItem.getLatestItemRevision();

				newItem.setProperty("jci6_EPIC_PN", epic);

				// 7.10修改--项目资料库权限
				TCProperty[] tcProperty = newIr
						.getTCProperties(propProInfoNames);

				tcProperty[0].setStringValueData(oemName);

				TCComponentGroupType groupType = (TCComponentGroupType) session
						.getTypeComponent("Group");

				System.out.println("--------设置完OEM的name------");

				// jci6_OEMModelName

				tcProperty[24].setStringValueData(modeName);

				System.out.println("--------设置完modeNameLOV------");

				if (sec != null && !"".equals(sec)) {

					TCComponentGroup secGroup = groupType.find(sec);

					tcProperty[1].setReferenceValueData(secGroup);

					System.out.println("--------设置完OsecGroup------");
				}
				if (divi != null && !"".equals(divi)) {

					TCComponentGroup diviGroup = groupType.find(divi);

					tcProperty[2].setReferenceValueData(diviGroup);

					System.out.println("--------设置完diviGroup------");
				}

				// add by wuh 2014-5-26
				if (closeDate != null) {
					newItem.setDateProperty("jci6_CloseDate", closeDate);
				}

				// add by wuh 2014-6-5
				// if (eqmflag) {
				// newItem.setLogicalProperty("jci6_IsRunEQM", eqmflag);
				// }

				if (!"".equals(targetPos)) {

					tcProperty[4].setDoubleValueData(Double
							.parseDouble(targetPos));

					System.out.println("--------设置完TargetPos------");

				}

				tcProperty[5].setLogicalValueData(activeStatusLogic);

				System.out.println("--------设置完activeStatus------");

				tcProperty[6].setStringValueData(projectStatus);

				System.out.println("--------设置完programnetatus------");

				tcProperty[7].setStringValueData(category);

				System.out.println("--------设置完category------");

				tcProperty[8].setStringValueData(product);

				System.out.println("--------设置完product------");

				tcProperty[9].setStringValueData(type);

				System.out.println("--------设置完TYPE------");

				tcProperty[10].setDateValueData(pStart);

				System.out.println("--------设置完StartDate------");

				tcProperty[11].setDateValueData(pEnd);

				System.out.println("--------设置完EndDate------");

				tcProperty[12].setDateValueData(lc);

				System.out.println("--------设置完SOP------");

				if (!"".equals(sopfy)) {

					tcProperty[13].setIntValueData(Integer.parseInt(sopfy));

					System.out.println("--------设置完SOPFY------");
				}
				if (!"".equals(modelYear)) {

					tcProperty[14].setIntValueData(Integer.parseInt(modelYear));

					System.out.println("--------设置完modelYear------");
				}

				tcProperty[15].setStringValueData(function);

				System.out.println("--------设置完function------");

				tcProperty[16].setStringValueData(frontSS);

				System.out.println("--------设置完frontSS------");

				tcProperty[17].setStringValueData(rearSS);

				System.out.println("--------设置完rearSS------");

				tcProperty[18].setStringValueData(track);

				System.out.println("--------设置完track------");

				tcProperty[19].setStringValueData(recliner);

				System.out.println("--------设置完recliner------");

				tcProperty[20].setStringValueData(pump);

				System.out.println("--------设置完pump------");

				tcProperty[21].setStringValueData(vta);

				System.out.println("--------设置完VTA------");

				tcProperty[22].setStringValueData(latch);

				System.out.println("--------设置完LATCH------");

				// 5.12--SMTE
				// tcProperty[25].setStringValueData(SMTEs);
				TCPropertyDescriptor SMTETCProperty = tcComponentItemRevisionType
						.getPropertyDescriptor("jci6_SMTE_Variables");

				SMTE_lov.load(SMTETCProperty);
				userService.call("open_or_close_pass", new Object[] { 1 });
				SMTE_lov.save(newIr);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完SMTE_Variables------");

				// equ
				tcProperty[23].setDoubleValueData(Double.parseDouble(equ));

				System.out.println("--------设置完EQU------");

				System.out.println("-----------Project Team Leader========="
						+ tl);
				if (tlLov.getSelectedObject() != null) {
					System.out.println("----------------tlLov有值！！！======="
							+ tlLov.getSelectedObject().getClass().toString());

					if (tlLov.getSelectedObject().getClass().toString()
							.indexOf("String") > 0) {

						System.out.println("----------------tlLov的值为======="
								+ tlLov.getSelectedObject().getClass()
										.toString());
						TCComponentUserType userTL = (TCComponentUserType) session
								.getTypeComponent("User");
						if (tl != null && !"".equals(tl)) {
							if (tl.indexOf("(") > 0) {
								String strUser = tl.replaceAll("\\(", ";")
										.replaceAll("\\)", "").split(";")[1]
										.trim();

								System.out.println("---------strUser==="
										+ strUser);
								TCComponentUser user = userTL.find(strUser);

								System.out.println("-------------user===="
										+ user.getOSUserName());

								tcProperty[3].setReferenceValueData(user);

								// System.out.println("--------设置完tlLov的user------");
							} else {
								TCComponent[] users = query(session,
										QUERY_NAME,
										new String[] { "Person Name" },
										new String[] { tl });

								tcProperty[3].setReferenceValueData(users[0]);

								System.out
										.println("--------------设置完tlLov的user-过查询找到！！！------");
							}
						}
					} else {
						TCComponentUser tlUser = (TCComponentUser) tlLov
								.getSelectedObject();
						if (tlUser != null) {

							tcProperty[3].setReferenceValueData(tlUser);
						}

						System.out
								.println("--------------直接设置完tlLov的值！！！-----");
					}
				}

				// 7.10修改--项目资料库权限
				// userService.call("open_or_close_pass", new Object[] { 1 });
				// tcProperty[25].setLogicalValue(true);
				// userService.call("open_or_close_pass", new Object[] { 0 });

				System.out.println("---创建项目信息结束----");
				userService.call("open_or_close_pass", new Object[] { 1 });
				newIr.lock();
				newIr.setTCProperties(tcProperty);
				newIr.save();
				newIr.unlock();
				userService.call("open_or_close_pass", new Object[] { 0 });
				newIr.refresh();
			} else {
				System.out.println("-----newItem is NULL-----！！！");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newItem;
	}

	private void initializePanel(Frame frame) {
		frame.setLayout(new VerticalLayout(10, 5, 5, 5, 5));
		System.out.println("=====add by yangh   =initializePanel=====");
		// =======================add by yangh
		try {
			tcComponentItemType = (TCComponentItemType) session
					.getTypeComponent("JCI6_ProgramInfo");
			tcComponentItemRevisionType = (TCComponentItemRevisionType) session
					.getTypeComponent("JCI6_ProgramInfoRevision");

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JTabbedPane projInfoTabs = new JTabbedPane(1);
		projInfoTabs.setTabPlacement(SwingConstants.BOTTOM);
		// buildDefiningProjAttrPanel();
		projInfoTabs.addTab(registry1.getString("projectXMJC.TITLE"),
				new JScrollPane(buildDefiningXMJCAttrPanel()));
		projInfoTabs.addTab(registry1.getString("projectXMLX.TITLE"),
				new JScrollPane(buildDefiningXMLXAttrPanel()));
		projInfoTabs.addTab(registry1.getString("projectXMRQ.TITLE"),
				new JScrollPane(buildDefiningXMRQAttrPanel()));
		projInfoTabs.addTab(registry1.getString("projectFunction.TITLE"),
				new JScrollPane(buildDefiningGNPZAttrPanel()));
		projInfoTabs.addTab(registry1.getString("projectPDX.TITLE"),
				new JScrollPane(buildDefiningPDXAttrPanel()));

		System.out.println("===setXMJCPanelEnabled(true)===");
		// modify by wuwei
		setXMJCPanelEnabled(true);
		setXMLXPanelEnabled(true);
		setXMRQPanelEnabled(true);
		setGNPZPanelEnabled(false);
		setPDXPanelEnabled(false);

		/*
		 * <William> 9-Jun-2013 modify code to make the textfield align to left
		 * side
		 */
		System.out
				.println("-------------------------William-----------------------------");
		oemNameLov.setOptimalWidth();
		// modeNameField.setOptimalWidth();
		diviLov.setOptimalWidth();
		secLov.setOptimalWidth();
		// tlLov
		targetPosField.setOptimalWidth();
		// activeStatusBox.setsetOptimalWidth();
		// projectStatusBox.setOptimalWidth();
		// categoryBox.setOptimalWidth();
		productLov.setOptimalWidth();
		typeLov.setOptimalWidth();
		sopfyLov.setOptimalWidth();
		modelYearLov.setOptimalWidth();
		// epicTextField.setHorizontalAlignment(JTextField.LEFT);
		// functionTextField.setHorizontalAlignment(JTextField.LEFT);
		frontSSLov.setOptimalWidth();
		rearSSLov.setOptimalWidth();
		trackLov.setOptimalWidth();
		reclinerLov.setOptimalWidth();
		pumpLov.setOptimalWidth();
		vtaLov.setOptimalWidth();
		latchLov.setOptimalWidth();
		pxdLov.setOptimalWidth();
		gebtLov.setOptimalWidth();
		latestComboLov.setOptimalWidth();
		// equTextField.setHorizontalAlignment(JTextField.LEFT);
		// calcByLov.setOptimalWidth();
		// approvedAmountTextField.setHorizontalAlignment(JTextField.LEFT);

		// =======================add by yangh
		frame.add("unbound.bind.center.center", projInfoTabs);

	}

	// private JPanel buildDefiningProjAttrPanel() {}

	// modify by wuwei
	protected synchronized Composite createTopForm(Composite localComposite) {
		Composite localComposite1 = getToolkit().createComposite(
				localComposite, 0);
		// Composite localComposite1 =
		// getToolkit().createScrolledForm(paramComposite);
		// ScrolledComposite scrolledComposite=new
		// ScrolledComposite(paramComposite, SWT.H_SCROLL|SWT.V_SCROLL);
		GridLayout localGridLayout = new GridLayout(3, false);

		localComposite1.setLayout(localGridLayout);
		Label localLabel1 = getToolkit().createLabel(localComposite1,
				Messages.getString("projectID.LABEL"), 64);
		GridData localGridData1 = new GridData(16384, 4, false, false);
		localLabel1.setLayoutData(localGridData1);
		Label localLabel2 = getToolkit().createLabel(localComposite1, "*");
		GridData localGridData2 = new GridData(4, 4, false, false);
		localLabel2.setLayoutData(localGridData2);
		localLabel2.setForeground(localComposite1.getDisplay()
				.getSystemColor(3));
		GridData localGridData3 = new GridData(4, 128, false, false);
		this.m_idText = getToolkit().createText(localComposite1, "", 2048);
		localGridData3 = new GridData(16384, 16777216, true, false);
		localGridData3.widthHint = 152;
		this.m_idText.setLayoutData(localGridData3);
		this.m_idText.setTextLimit(128);
		m_idText.setVisible(false);
		this.m_idText.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent paramKeyEvent) {
				if ((ProjectDefinitionView.this.isPA)
						|| (ProjectDefinitionView.this.isPTA)
						|| (ProjectDefinitionView.this.isPTA()))
					ProjectDefinitionView.this.validateButtons();
			}

			public void keyPressed(KeyEvent paramKeyEvent) {
				ProjectDefinitionView.this.isDirty = true;
			}
		});
		this.m_idText.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent paramMouseEvent) {
				if (ProjectDefinitionView.this.m_idText.getCharCount() > 50)
					ProjectDefinitionView.this.m_idText
							.setToolTipText(ProjectDefinitionView.this.m_idText
									.getText());
				else
					ProjectDefinitionView.this.m_idText.setToolTipText("");
			}
		});
		Label localLabel3 = getToolkit().createLabel(localComposite1,
				Messages.getString("projectName.LABEL"), 64);
		localGridData3 = new GridData(4, 128, false, false, 1, 1);
		localLabel3.setLayoutData(localGridData3);
		Label localLabel4 = getToolkit().createLabel(localComposite1, "*");
		GridData localGridData4 = new GridData(4, 4, false, false);
		localLabel4.setLayoutData(localGridData4);
		localLabel4.setForeground(localComposite1.getDisplay()
				.getSystemColor(3));

		this.m_nameText = getToolkit().createText(localComposite1, "", 2048);
		m_nameText.setVisible(false);
		getToolkit().adapt(this.m_nameText, true, true);
		localGridData3 = new GridData(16384, 16777216, true, false);
		localGridData3.widthHint = 160;
		this.m_nameText.setLayoutData(localGridData3);
		this.m_nameText.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent paramKeyEvent) {
				if ((ProjectDefinitionView.this.isPA)
						|| (ProjectDefinitionView.this.isPTA)
						|| (ProjectDefinitionView.this.isPTA()))
					ProjectDefinitionView.this.validateButtons();
			}

			public void keyPressed(KeyEvent paramKeyEvent) {
				ProjectDefinitionView.this.isDirty = true;
			}
		});
		this.m_nameText.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent paramMouseEvent) {
				if (ProjectDefinitionView.this.m_nameText.getCharCount() > 50)
					ProjectDefinitionView.this.m_nameText
							.setToolTipText(ProjectDefinitionView.this.m_idText
									.getText());
				else
					ProjectDefinitionView.this.m_nameText.setToolTipText("");
			}
		});
		Label localLabel5 = getToolkit().createLabel(localComposite1,
				Messages.getString("projectDesc.LABEL"), 64);
		localGridData3 = new GridData(4, 128, false, false, 2, 1);
		localLabel5.setLayoutData(localGridData3);
		this.m_descriptionText = getToolkit().createText(localComposite1, "",
				2626);
		localGridData3 = new GridData(16384, 16777216, true, false);
		localGridData3.widthHint = 250;
		localGridData3.heightHint = 50;
		this.m_descriptionText.setLayoutData(localGridData3);
		this.m_descriptionText.setTextLimit(256);
		m_descriptionText.setVisible(false);
		this.m_descriptionText.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent paramKeyEvent) {
				if ((ProjectDefinitionView.this.isPA)
						|| (ProjectDefinitionView.this.isPTA)
						|| (ProjectDefinitionView.this.isPTA()))
					ProjectDefinitionView.this.validateButtons();
			}

			public void keyPressed(KeyEvent paramKeyEvent) {
				ProjectDefinitionView.this.isDirty = true;
			}
		});

		this.m_descriptionText.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent paramMouseEvent) {
				if (ProjectDefinitionView.this.m_descriptionText.getCharCount() > 50)
					ProjectDefinitionView.this.m_descriptionText
							.setToolTipText(ProjectDefinitionView.this.m_idText
									.getText());
				else
					ProjectDefinitionView.this.m_descriptionText
							.setToolTipText("");
			}
		});

		Label localLabel6 = getToolkit().createLabel(localComposite1,
				Messages.getString("projectCollaborationCategories.LABEL"), 64);
		localGridData3 = new GridData(4, 128, false, false, 2, 1);
		localLabel6.setLayoutData(localGridData3);
		TCPropertyDescriptor localTCPropertyDescriptor = null;
		try {
			TCComponentType localTCComponentType = getTCSession()
					.getTypeComponent("TC_Project");
			localTCPropertyDescriptor = localTCComponentType
					.getPropertyDescriptor("fnd0CollaborationCategories");
		} catch (TCException localTCException) {
			logger.error(localTCException.getLocalizedMessage(),
					localTCException);
		}

		this.m_categoryCombo = new LOVDisplayer(localComposite1, 2);
		this.m_categoryCombo.initialize(this.project,
				localTCPropertyDescriptor, null);
		getToolkit().adapt(this.m_categoryCombo, true, true);
		localGridData3 = new GridData(16384, 16777216, true, false);
		localGridData3.widthHint = 250;
		localGridData3.heightHint = 25;
		this.m_categoryCombo.setLayoutData(localGridData3);
		m_categoryCombo.setVisible(false);

		this.m_categoryCombo
				.addPropertyChangeListener(new IPropertyChangeListener() {
					public void propertyChange(
							PropertyChangeEvent paramPropertyChangeEvent) {
						if ((ProjectDefinitionView.this.isPA)
								|| (ProjectDefinitionView.this.isPTA)
								|| (ProjectDefinitionView.this.isPTA())) {
							ProjectDefinitionView.this.validateButtons();
							ProjectDefinitionView.this.isDirty = true;
						}
					}
				});

		Label localLabel7 = getToolkit().createLabel(localComposite1,
				Messages.getString("projectParent.LABEL"), 1);
		localGridData3 = new GridData(4, 128, false, false, 2, 1);
		localLabel7.setLayoutData(localGridData3);

		this.m_parentText = getToolkit().createText(localComposite1, "", 2048);
		getToolkit().adapt(this.m_parentText, true, true);
		m_parentText.setVisible(false);

		localGridData3 = new GridData(16384, 16777216, true, false);
		localGridData3.widthHint = 160;
		this.m_parentText.setLayoutData(localGridData3);
		this.m_parentText.setEnabled(false);
		Label localLabel8 = getToolkit().createLabel(localComposite1,
				Messages.getString("projectStatus.LABEL"), 1);
		localGridData3 = new GridData(4, 128, false, false, 2, 1);
		localLabel8.setLayoutData(localGridData3);
		Composite localComposite2 = new Composite(localComposite1, 0);
		localComposite2.setLayout(new RowLayout());
		SelectionAdapter local10 = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				if ((ProjectDefinitionView.this.isPA)
						|| (ProjectDefinitionView.this.isPTA)
						|| (ProjectDefinitionView.this.isPTA())) {
					ProjectDefinitionView.this.validateButtons();
					ProjectDefinitionView.this.isDirty = true;
				}
			}
		};

		this.m_activeStatusBtn = new Button(localComposite2, 16);
		m_activeStatusBtn.setVisible(false);
		this.m_activeStatusBtn.setText(Messages
				.getString("activeStatusButton.TITLE"));
		this.m_activeStatusBtn.addSelectionListener(local10);
		this.m_activeStatusBtn.setSelection(true);

		this.m_inactiveStatusBtn = new Button(localComposite2, 16);
		m_inactiveStatusBtn.setVisible(false);
		this.m_inactiveStatusBtn.setText(Messages
				.getString("inactiveStatusButton.TITLE"));
		this.m_inactiveStatusBtn.addSelectionListener(local10);

		this.m_inactiveandInviisibleStatusBtn = new Button(localComposite2, 16);
		m_inactiveandInviisibleStatusBtn.setVisible(false);
		this.m_inactiveandInviisibleStatusBtn.setText(Messages
				.getString("inactiveInvisibleStatusButton.TITLE"));
		this.m_inactiveandInviisibleStatusBtn.addSelectionListener(local10);

		Label localLabel9 = getToolkit().createLabel(localComposite1, "", 1);
		localLabel9.setVisible(false);
		localGridData3 = new GridData(4, 128, false, false, 2, 1);
		localLabel9.setLayoutData(localGridData3);
		Composite localComposite3 = new Composite(localComposite1, 0);
		localComposite3.setLayout(new RowLayout());
		this.m_ProgramBtn = new Button(localComposite3, 16);
		m_ProgramBtn.setVisible(false);
		this.m_ProgramBtn.setText(Messages.getString("program.LABEL"));
		this.m_ProgramBtn.addSelectionListener(local10);
		this.m_ProgramBtn.setSelection(false);
		this.m_ProjectBtn = new Button(localComposite3, 16);

		this.m_ProjectBtn.setText(Messages.getString("project.LABEL"));
		this.m_ProjectBtn.addSelectionListener(local10);
		this.m_ProjectBtn.setSelection(true);
		Label localLabel10 = getToolkit().createLabel(localComposite1, "", 1);
		localGridData3 = new GridData(4, 128, false, false, 2, 1);
		localLabel10.setLayoutData(localGridData3);
		this.m_teamInheritance = new Button(localComposite1, 32);

		String inheritance_label = Messages.getString("inheritance.LABEL");
		System.out.println("inheritance.LABEL-->" + inheritance_label);

		this.m_teamInheritance.setText(inheritance_label);
		localGridData3 = new GridData(16384, 16777216, true, false);
		this.m_teamInheritance.setLayoutData(localGridData3);
		this.m_teamInheritance.setSelection(false);
		SelectionAdapter local11 = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				if ((ProjectDefinitionView.this.isPA)
						|| (ProjectDefinitionView.this.isPTA)
						|| (ProjectDefinitionView.this.isPTA())) {
					String str1 = Messages
							.getString("inheritanceWarning.TITLE");
					String str2;
					if (ProjectDefinitionView.this.m_teamInheritance
							.getSelection()) {
						str2 = Messages.getString("inheritanceTrue.WARNING");
						MessageBox.post(str2, str1, 4);
					} else {
						str2 = Messages.getString("inheritanceFalse.WARNING");
						MessageBox.post(str2, str1, 4);
					}
					ProjectDefinitionView.this.validateButtons();
					ProjectDefinitionView.this.isDirty = true;

				}

			}
		};
		this.m_teamInheritance.addSelectionListener(local11);

		// scrolledComposite.setContent(localComposite1);
		return localComposite1;
	}

	public void resetSecitionLov(String secStr) {
		if (diviLov.getSelectedItem() != null
				&& !"".equals(diviLov.getTextField().getText())) {
			// 添加联动过滤
			String diviValue = diviLov.getSelectedItem().toString();
			diviValue = diviValue.replace(".", ";").split(";")[0];

			secLov.removeAllItems();
			if (lovSecValues != null && lovSecValues.length > 0) {

				for (int i = 0; i < lovSecValues.length; i++) {

					if (lovSecValues[i].toString().indexOf(diviValue) >= 0) {
						secLov.addItem(lovSecValues[i]);
					}
				}
			}

		} else if (diviLov.getSelectedObject() != null
				&& !"".equals(diviLov.getSelectedObject().toString())) {
			// 添加联动过滤
			String diviValue = diviLov.getSelectedObject().toString();
			diviValue = diviValue.replace(".", ";").split(";")[0];
			secLov.removeAllItems();
			if (lovSecValues != null && lovSecValues.length > 0) {

				for (int i = 0; i < lovSecValues.length; i++) {

					if (lovSecValues[i].toString().indexOf(diviValue) >= 0) {
						secLov.addItem(lovSecValues[i]);
					}
				}
			}
		} else if (!"".equals(diviLov.getTextField().getText())) {
			// 添加联动过滤
			String diviValue = diviLov.getTextField().getText();
			diviValue = diviValue.replace(".", ";").split(";")[0];
			secLov.removeAllItems();
			if (lovSecValues != null && lovSecValues.length > 0) {

				for (int i = 0; i < lovSecValues.length; i++) {

					if (lovSecValues[i].toString().indexOf(diviValue) >= 0) {
						secLov.addItem(lovSecValues[i]);
					}
				}
			}
		} else {
			secLov.removeAllItems();
			if (lovSecValues != null && lovSecValues.length > 0) {

				for (int i = 0; i < lovSecValues.length; i++) {

					secLov.addItem(lovSecValues[i]);

				}
			}
		}
		if (secStr != null && !"".equals(secStr)) {
			secLov.setText(secStr);
		} else {
			secLov.removeAllItems();
			if (lovSecValues != null && lovSecValues.length > 0) {

				for (int i = 0; i < lovSecValues.length; i++) {

					secLov.addItem(lovSecValues[i]);

				}
			}
		}
	}

	/*
	 * 
	 * 建立项目基础Panel
	 */
	// private JPanel buildDefiningXMJCAttrPanel() {}

	/*
	 * 
	 * 建立项目类型Panel
	 */
	private JPanel buildDefiningXMLXAttrPanel() {
		JPanel typePanel = new JPanel(new PropertyLayout());
		JPanel typePanel1 = new JPanel(new PropertyLayout());
		typePanel2 = new JPanel(new PropertyLayout());
		try {
			// 激活状态
			TCPropertyDescriptor activeStatusTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_ActiveStatus");
			JLabel activeStatusJlabel = new JLabel(
					activeStatusTCProperty.getDisplayName());

			String ss = registry1.getString("ActiveStatus_jci6");
			activeStatusBox = new iComboBox(ss.split(",", -1));
			// activeStatusBox = new iComboBox(
			// reg.getStringArray("ActiveStatus_jci6"));
			// activeStatusBox.addItem(reg.getStringArray("ActiveStatus_jci6")[0]);
			activeStatusBox.setMandatory(true);
			activeStatusBox.setPreferredSize(new Dimension(200, 20));
			activeStatusBox.setMinimumSize(new Dimension(200, 20));

			// modify by wuwei
			activeStatusBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					loadProjectStatusBoxData();
					validateButtons();
				}
			});

			// 项目状态
			TCPropertyDescriptor programStatusTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_ProgramState");
			JLabel programStatusJlabel = new JLabel(
					programStatusTCProperty.getDisplayName());
			projectStatusBox = new iComboBox();
			projectStatusBox.setMandatory(true);
			projectStatusBox.setPreferredSize(new Dimension(200, 20));
			projectStatusBox.setMinimumSize(new Dimension(200, 20));
			// modify by wuwei
			projectStatusBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub
					loadCategoryBox();

					// setTargetPossibilityVisiiable("");
					validateButtons();
				}

			});

			// 种类
			TCPropertyDescriptor categoryTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_Category");
			JLabel categoryJlabel = new JLabel(
					categoryTCProperty.getDisplayName());

			categoryBox = new iComboBox();
			categoryBox.setMandatory(true);
			categoryBox.setPreferredSize(new Dimension(200, 20));
			categoryBox.setMinimumSize(new Dimension(200, 20));

			// modify by wuwei
			categoryBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub
					validateButtons();
				}

			});

			// 产品
			TCPropertyDescriptor productTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_Product");
			JLabel productJlabel = new JLabel(
					productTCProperty.getDisplayName());
			productLov = new LOVComboBox(productTCProperty.getLOV());
			productLov.setMaximumRowCount(24);
			setRequire(productLov, productTCProperty);
			productLov.setPreferredSize(new Dimension(200, 20));
			productLov.setMinimumSize(new Dimension(200, 20));
			productLov.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub
					validateButtons();
				}

			});

			// 类型
			TCPropertyDescriptor typeTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_Type");
			JLabel typeJlabel = new JLabel(typeTCProperty.getDisplayName());
			typeLov = new LOVComboBox(typeTCProperty.getLOV());
			typeLov.setMaximumRowCount(24);
			setRequire(typeLov, typeTCProperty);
			typeLov.setMaximumSize(new Dimension(200, 20));
			typeLov.setMinimumSize(new Dimension(200, 20));
			typeLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});

			typePanel.add("1.1.right.center", activeStatusJlabel);
			typePanel.add("1.2.left.center", activeStatusBox);
			typePanel.add("2.1.right.center", programStatusJlabel);
			typePanel.add("2.2.left.center", projectStatusBox);
			typePanel.add("3.1.right.center", categoryJlabel);
			typePanel.add("3.2.center.center", categoryBox);
			typePanel.add("4.1.right.center", productJlabel);
			typePanel.add("4.2.center.center", productLov);
			typePanel.add("5.1.right.center", typeJlabel);
			typePanel.add("5.2.center.center", typeLov);

			// 目标可能性百分比
			TCPropertyDescriptor targetPosTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_TargetPos");
			JLabel targetPosJlabel = new JLabel(
					targetPosTCProperty.getDisplayName());
			// targetPosJlabel.setVisible(false);
			targetPosField = new LOVComboBox(targetPosTCProperty.getLOV()) {
				@Override
				public void paint(Graphics g) {
					super.paint(g);
					Painter.paintIsRequired(this, g);
				}
			};
			// targetPosField.setVisible(false);
			targetPosField.setMaximumRowCount(24);
			setRequire(targetPosField, targetPosTCProperty);
			targetPosField.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub
					validateButtons();
				}

			});

			typePanel2.add("1.1.left", targetPosJlabel);
			typePanel2.add("1.2.left", targetPosField);
			typePanel2.setVisible(false);
			sepl = new Separator();
			sepl.setVisible(false);
			typePanel1.add("1.1.left", typePanel);
			typePanel1.add("2.1.right.center", sepl);
			typePanel1.add("3.1.center.center", typePanel2);

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return typePanel1;

	}

	/*
	 * 
	 * 建立项目日期Panel
	 */
	private JPanel buildDefiningXMRQAttrPanel() {
		JPanel jpanel1 = new JPanel(new PropertyLayout());
		try {
			// 项目开始日期
			TCPropertyDescriptor startDateTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_StartDate");
			JLabel startDateJlabel = new JLabel(
					startDateTCProperty.getDisplayName());
			pStartDate = new DateButton(new Date(), "yyyy-MM-dd", true, false,
					false);
			// pStartDate.set//20

			pStartDate.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});

			// 项目结束日期
			TCPropertyDescriptor endDateTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_EndDate");
			JLabel endDateJlabel = new JLabel(
					endDateTCProperty.getDisplayName());
			pEndDate = new DateButton(new Date(), "yyyy-MM-dd", true, false,
					false);
			pEndDate.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});

			// 量产日期
			TCPropertyDescriptor sopTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_SOP");
			JLabel sopJlabel = new JLabel(sopTCProperty.getDisplayName());
			lcDate = new DateButton(new Date(), "yyyy-MM-dd", true, false,
					false);
			lcDate.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});

			// 量产年
			TCPropertyDescriptor sopfyTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_SOPFY");
			JLabel sopfyJlabel = new JLabel(sopfyTCProperty.getDisplayName());
			sopfyLov = new LOVComboBox(sopfyTCProperty.getLOV());
			sopfyLov.setMaximumRowCount(24);
			setRequire(sopfyLov, sopfyTCProperty);
			sopfyLov.setPreferredSize(new Dimension(200, 20));
			sopfyLov.setMinimumSize(new Dimension(200, 20));
			sopfyLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});

			// 车型年
			TCPropertyDescriptor modelYearTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_ModelYear");
			JLabel modelYearJlabel = new JLabel(
					modelYearTCProperty.getDisplayName());
			modelYearLov = new LOVComboBox(modelYearTCProperty.getLOV());
			modelYearLov.setMaximumRowCount(24);
			setRequire(modelYearLov, modelYearTCProperty);
			modelYearLov.setPreferredSize(new Dimension(200, 20));
			modelYearLov.setMinimumSize(new Dimension(200, 20));
			modelYearLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});
			jpanel1.add("1.1.right.center", startDateJlabel);
			jpanel1.add("1.2.left.center", pStartDate);
			jpanel1.add("2.1.right.center", endDateJlabel);
			jpanel1.add("2.2.left.center", pEndDate);
			jpanel1.add("3.1.right.center", sopJlabel);
			jpanel1.add("3.2.center.center", lcDate);
			jpanel1.add("4.1.right.center", sopfyJlabel);
			jpanel1.add("4.2.center.center", sopfyLov);
			jpanel1.add("5.1.right.center", modelYearJlabel);
			jpanel1.add("5.2.center.center", modelYearLov);
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jpanel1;
	}

	private JPanel jpanel1;

	/*
	 * 
	 * 建立功能配置Panel
	 */
	private JPanel buildDefiningGNPZAttrPanel() {
		jpanel1 = new JPanel(new PropertyLayout());
		try {
			// EPIC号
			TCPropertyDescriptor epicTCProperty = tcComponentItemType
					.getPropertyDescriptor("jci6_EPIC_PN");
			JLabel epicJlabel = new JLabel(epicTCProperty.getDisplayName());
			epicTextField = new iTextField(18, 64, epicTCProperty.isRequired());
			epicTextField.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent mouseevent) {
					validateButtons();
				}

			});

			epicTextField.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent keyevent) {
					validateButtons();
				}

			});

			// 功能配置
			TCPropertyDescriptor functionTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_Function");
			JLabel functionJlabel = new JLabel(
					functionTCProperty.getDisplayName());
			functionTextField = new iTextField(18, 64, false);
			functionTextField.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent mouseevent) {
					validateButtons();
				}

			});
			functionTextField.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent keyevent) {
					validateButtons();
				}

			});

			// 前排骨架
			TCPropertyDescriptor frontTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_FrontSS");
			JLabel frontJlabel = new JLabel(frontTCProperty.getDisplayName());
			frontSSLov = new LOVComboBox(frontTCProperty.getLOV());
			frontSSLov.setMaximumRowCount(24);
			setRequire(frontSSLov, frontTCProperty);
			frontSSLov.setPreferredSize(new Dimension(200, 20));
			frontSSLov.setMinimumSize(new Dimension(200, 20));
			frontSSLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});

			// 后排骨架
			TCPropertyDescriptor rearSSTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_RearSS");
			JLabel rearSSJlabel = new JLabel(rearSSTCProperty.getDisplayName());
			rearSSLov = new LOVComboBox(rearSSTCProperty.getLOV());
			rearSSLov.setMaximumRowCount(24);
			setRequire(rearSSLov, rearSSTCProperty);
			// rearSSLov.setPreferredSize(new Dimension(200,20));
			// rearSSLov.setMinimumSize(new Dimension(200,20));
			rearSSLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});

			// 5.12修改----新增加一个属性jci6_SMTE_Variables
			TCPropertyDescriptor SMTETCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_SMTE_Variables");
			JLabel SMTEJlabel = new JLabel(SMTETCProperty.getDisplayName());

			if (SMTE_lov == null) {
				SMTE_lov = new PropertyArray();
				try {
					SMTE_lov.load(SMTETCProperty);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				/*
				 * JScrollPane listScrollPane = new JScrollPane(list);
				 * listScrollPane.getCorner(key)
				 * SMTE_lov.get.addInputMethodListener
				 * (l).addMouseMotionListener(new MouseMotionListener() {
				 * 
				 * @Override public void mouseMoved(MouseEvent e) { System.out
				 * .println("================mouseMoved!!!!===============");
				 * validateButtons();
				 * 
				 * }
				 * 
				 * @Override public void mouseDragged(MouseEvent e) { // TODO
				 * Auto-generated method stub
				 * 
				 * } });
				 */
			}

			// 滑道
			TCPropertyDescriptor trackTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_Track");
			JLabel trackJlabel = new JLabel(trackTCProperty.getDisplayName());
			trackLov = new LOVComboBox(trackTCProperty.getLOV());
			trackLov.setMaximumRowCount(24);
			setRequire(trackLov, trackTCProperty);
			trackLov.setPreferredSize(new Dimension(200, 20));
			trackLov.setMinimumSize(new Dimension(200, 20));
			trackLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});

			// 调角器
			TCPropertyDescriptor reclinerTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_Recliner");
			JLabel reclinerJlabel = new JLabel(
					reclinerTCProperty.getDisplayName());
			reclinerLov = new LOVComboBox(reclinerTCProperty.getLOV());
			reclinerLov.setMaximumRowCount(24);
			setRequire(reclinerLov, reclinerTCProperty);
			reclinerLov.setPreferredSize(new Dimension(200, 20));
			reclinerLov.setMinimumSize(new Dimension(200, 20));
			reclinerLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});

			// 手动高调
			TCPropertyDescriptor pumpTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_PUMP");
			JLabel pumpJlabel = new JLabel(pumpTCProperty.getDisplayName());
			pumpLov = new LOVComboBox(pumpTCProperty.getLOV());
			pumpLov.setMaximumRowCount(24);
			setRequire(pumpLov, pumpTCProperty);
			pumpLov.setPreferredSize(new Dimension(200, 20));
			pumpLov.setMinimumSize(new Dimension(200, 20));
			pumpLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});
			// 电动高调
			TCPropertyDescriptor vtaTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_VTA");
			JLabel vtaJlabel = new JLabel(vtaTCProperty.getDisplayName());
			vtaLov = new LOVComboBox(vtaTCProperty.getLOV());
			vtaLov.setMaximumRowCount(24);
			setRequire(vtaLov, vtaTCProperty);
			vtaLov.setPreferredSize(new Dimension(200, 20));
			vtaLov.setMinimumSize(new Dimension(200, 20));
			vtaLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});
			// 锁
			TCPropertyDescriptor latchTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_Latch");
			JLabel latchJlabel = new JLabel(latchTCProperty.getDisplayName());
			latchLov = new LOVComboBox(latchTCProperty.getLOV());
			latchLov.setMaximumRowCount(24);
			setRequire(latchLov, latchTCProperty);
			latchLov.setPreferredSize(new Dimension(200, 20));
			latchLov.setMinimumSize(new Dimension(200, 20));
			latchLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});

			jpanel1.add("1.1.right.center", epicJlabel);
			jpanel1.add("1.2.left.center", epicTextField);
			jpanel1.add("2.1.right.center", functionJlabel);
			jpanel1.add("2.2.left.center", functionTextField);
			jpanel1.add("3.1.right.center", frontJlabel);
			jpanel1.add("3.2.center.center", frontSSLov);
			jpanel1.add("4.1.right.center", rearSSJlabel);
			jpanel1.add("4.2.center.center", rearSSLov);
			jpanel1.add("5.1.right.center", trackJlabel);
			jpanel1.add("5.2.center.center", trackLov);
			jpanel1.add("6.1.right.center", reclinerJlabel);
			jpanel1.add("6.2.left.center", reclinerLov);
			jpanel1.add("7.1.right.center", pumpJlabel);
			jpanel1.add("7.2.center.center", pumpLov);
			jpanel1.add("8.1.right.center", vtaJlabel);
			jpanel1.add("8.2.center.center", vtaLov);
			jpanel1.add("9.1.right.center", latchJlabel);
			jpanel1.add("9.2.center.center", latchLov);
			// 5.12--修改
			jpanel1.add("10.1.right.center", SMTEJlabel);
			jpanel1.add("10.2.center.center", SMTE_lov);

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jpanel1;
	}

	/*
	 * 
	 * PDX Panel
	 */
	private JPanel buildDefiningPDXAttrPanel() {
		JPanel jpanel1 = new JPanel(new PropertyLayout());
		jpanel1.setEnabled(false);
		try {
			// pdx版本
			TCPropertyDescriptor pdxTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_PDxSeq");
			JLabel pdxJlabel = new JLabel(pdxTCProperty.getDisplayName());
			pxdLov = new LOVComboBox(pdxTCProperty.getLOV());
			pxdLov.setMaximumRowCount(24);
			pxdLov.setPreferredSize(new Dimension(200, 20));
			pxdLov.setMinimumSize(new Dimension(200, 20));

			// PropertyObjectLink,, jci6_LatestStatus
			// 5.6修改
			// TCPropertyDescriptor latestStatusTCProperty = tcComponentItemType
			// .getPropertyDescriptor("jci6_LatestStatus");
			// JLabel latestJlabel = new JLabel(
			// latestStatusTCProperty.getDisplayName());
			// latestLink = new PropertyObjectLink();
			// try {
			// latestLink.load(latestStatusTCProperty);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			TCPropertyDescriptor latestStatusTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_BudgetState");
			JLabel latestJlabel = new JLabel(
					latestStatusTCProperty.getDisplayName());

			latestComboLov = new LOVComboBox(latestStatusTCProperty.getLOV());
			latestComboLov.setMaximumRowCount(24);
			latestComboLov.setPreferredSize(new Dimension(200, 20));
			latestComboLov.setMinimumSize(new Dimension(200, 20));

			remarkField = new iTextArea(3, 28, 240, false);
			remarkField.setLineWrap(true);
			remarkField.setWrapStyleWord(true);
			JScrollPane jscrollpaneRemark = new JScrollPane();
			jscrollpaneRemark.getViewport().add(remarkField);

			TCPropertyDescriptor jci6RemarkDescriptor = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_Remark");

			JLabel remarkLabel = new JLabel(
					jci6RemarkDescriptor.getDisplayName());

			// GEBT版本版本
			TCPropertyDescriptor gebtTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_GEBTTemplate");
			JLabel gebtJlabel = new JLabel(gebtTCProperty.getDisplayName());
			gebtLov = new LOVComboBox(gebtTCProperty.getLOV());
			gebtLov.setMaximumRowCount(24);
			gebtLov.setPreferredSize(new Dimension(200, 20));
			gebtLov.setMinimumSize(new Dimension(200, 20));

			// EQU
			TCPropertyDescriptor equTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_EQU");
			JLabel equJlabel = new JLabel(equTCProperty.getDisplayName());
			equTextField = new iTextField(18, 64, true);
			equTextField.setPreferredSize(new Dimension(200, 20));
			equTextField.setText("0");
			// EQU批准日期

			// Calc日期

			// Calc人

			// Approved Amount
			TCPropertyDescriptor approvedTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_SignedMoney");
			JLabel approvedAmountJlabel = new JLabel(
					approvedTCProperty.getDisplayName());
			approvedAmountTextField = new iTextField(18, 64, false);

			// PDX版本批准日期日期
			TCPropertyDescriptor pdxSignDateTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_PDxSignDate");
			JLabel pdxSignDateJlabel = new JLabel(
					pdxSignDateTCProperty.getDisplayName());
			pdxSignDate = new DateButton(new Date(), "yyyy-MM-dd", true, false,
					false);

			jpanel1.add("1.1.right.center", pdxJlabel);
			jpanel1.add("1.2.left.center", pxdLov);

			jpanel1.add("2.1.right.center", latestJlabel);

			// 5.6修改
			// jpanel1.add("2.2.left.center", latestLink);
			jpanel1.add("2.2.left.center", latestComboLov);

			jpanel1.add("3.1.right.center", gebtJlabel);
			jpanel1.add("3.2.left.center", gebtLov);
			jpanel1.add("4.1.right.center", equJlabel);
			jpanel1.add("4.2.center.center", equTextField);
			// jpanel1.add("5.1.right.center", equSignDateJlabel);
			// jpanel1.add("5.2.center.center", equSignDate);
			// jpanel1.add("6.1.right.center", calcDateJlabel);
			// jpanel1.add("6.2.center.center", calcDate);
			// jpanel1.add("7.1.right.center", calcByJlabel);
			// jpanel1.add("7.2.center.center", calcByLov);
			jpanel1.add("5.1.right.center", approvedAmountJlabel);
			jpanel1.add("5.2.center.center", approvedAmountTextField);
			jpanel1.add("6.1.right.center", pdxSignDateJlabel);
			jpanel1.add("6.2.center.center", pdxSignDate);
			// 5.31修改
			jpanel1.add("7.1.right.center", remarkLabel);
			jpanel1.add("7.2.center.center", jscrollpaneRemark);

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jpanel1;
	}

	/*
	 * 
	 * 设置特殊情况lov必填项
	 */
	public void setRequireLovComposite(Composite paramComposite,
			TCPropertyDescriptor productTCProperty) {
		if (productTCProperty.isRequired()) {
			Label localLabel6 = getToolkit().createLabel(paramComposite,
					productTCProperty.getDisplayName(), 64);

			GridData localGridData3 = new GridData(4, 128, false, false, 1, 1);
			localLabel6.setLayoutData(localGridData3);

			Label localLabel7 = getToolkit().createLabel(paramComposite, "*",
					64);
			localLabel7.setForeground(paramComposite.getDisplay()
					.getSystemColor(3));
			GridData localGridData0 = new GridData(4, 4, false, false);
			localLabel7.setLayoutData(localGridData0);

		} else {
			Label localLabel6 = getToolkit().createLabel(paramComposite,
					productTCProperty.getDisplayName(), 64);

			GridData localGridData3 = new GridData(4, 128, false, false, 2, 1);
			localLabel6.setLayoutData(localGridData3);
		}

	}

	protected synchronized Composite createBottomForm(Composite paramComposite) {
		CTabFolder localCTabFolder = new CTabFolder(paramComposite, 1);
		getToolkit().adapt(localCTabFolder, true, true);
		CTabItem localCTabItem = new CTabItem(localCTabFolder, 0);
		localCTabItem.setText(Messages.USERS);
		m_projectTeamManager = new ProjectTeamTreeManager(localCTabFolder, this);
		localCTabItem.setControl(m_projectTeamManager);
		localCTabFolder.setSelection(localCTabItem);
		return localCTabFolder;
	}

	protected Composite createHeaderComposite(Composite paramComposite) {
		if ((this.m_headerComposite != null)
				&& (!(this.m_headerComposite.isDisposed()))) {
			this.m_headerComposite.dispose();
			this.m_headerComposite = null;
		}
		this.m_headerComposite = new Composite(paramComposite, 64);
		this.m_headerComposite.setLayoutData(new GridData(4, 4, true, true));
		FillLayout localFillLayout = new FillLayout();
		this.m_headerComposite.setLayout(localFillLayout);
		Group localGroup = new Group(this.m_headerComposite, 64);
		localGroup.setText(ProjectConstants.OPTIONS_TEXT);
		return localGroup;
	}

	protected void createButtons(Composite paramComposite) {
		RowLayout localRowLayout = new RowLayout(256);
		localRowLayout.spacing = 5;
		localRowLayout.marginLeft = 5;
		localRowLayout.marginRight = 5;
		paramComposite.setLayout(localRowLayout);
		this.m_createButton = getToolkit().createButton(paramComposite,
				ProjectConstants.CREATE_TEXT, 8);
		this.m_createButton.setEnabled(false);
		this.m_createButton.setImage(ProjectConstants.CREATE_ICON);
		this.m_createButton.setToolTipText(ProjectConstants.CREATE_TEXT);
		this.m_createButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				ProjectManager.getInstance().doRefresh();
				ProjectDefinitionView.this.createProjectOperation();
				ProjectDefinitionView.this.isDirty = false;

				// add by wuwei

			}

			public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
			}
		});
		this.m_modifyButton = getToolkit().createButton(paramComposite,
				ProjectConstants.MODIFY_TEXT, 8);
		this.m_modifyButton.setEnabled(false);
		this.m_modifyButton.setImage(ProjectConstants.MODIFY_ICON);
		this.m_modifyButton.setToolTipText(ProjectConstants.MODIFY_TEXT);
		this.m_modifyButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				System.out.println("====m_modifyButton===");
				ProjectDefinitionView.this.modifyProjectOperation();
				ProjectDefinitionView.this.isDirty = false;

				// add by wuwei
			}

			public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
			}
		});
		this.m_copyButton = getToolkit().createButton(paramComposite,
				ProjectConstants.COPY_TEXT, 8);
		this.m_copyButton.setEnabled(false);
		this.m_copyButton.setImage(ProjectConstants.COPY_ICON);
		this.m_copyButton.setToolTipText(ProjectConstants.COPY_TEXT);
		this.m_copyButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				ProjectDefinitionView.this.copyProjectOperation();
				ProjectManager.getInstance().doRefresh();
				ProjectDefinitionView.this.isDirty = false;
			}

			public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
			}
		});
		this.m_deleteButton = getToolkit().createButton(paramComposite,
				ProjectConstants.DELETE_TEXT, 8);
		this.m_deleteButton.setEnabled(false);
		this.m_deleteButton.setImage(ProjectConstants.DELETE_ICON);
		this.m_deleteButton.setToolTipText(ProjectConstants.DELETE_TEXT);
		this.m_deleteButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				ProjectDefinitionView.this.deleteProjectOperation();
				ProjectManager.getInstance().doRefresh();
				ProjectDefinitionView.this.clearOperation();
				ProjectDefinitionView.this.isDirty = false;
				clearComponentValue();
			}

			public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
			}
		});
		this.m_clearButton = getToolkit().createButton(paramComposite,
				ProjectConstants.CLEAR_BUTTON_TEXT, 8);
		this.m_clearButton.setEnabled(true);
		this.m_clearButton.setImage(ProjectConstants.CLEAR_BUTTON_IMAGE);
		this.m_clearButton.setToolTipText(ProjectConstants.CLEAR_BUTTON_TEXT);
		this.m_clearButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				ProjectDefinitionView.this.isDirty = false;
				ProjectDefinitionView.this.clearOperation();
				clearComponentValue();
			}

			public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
			}
		});
	}

	private void createProjectOperation() {
		collectProjectMetaData();
		TCComponentProjectType tccomponentprojecttype = null;
		try {
			tccomponentprojecttype = (TCComponentProjectType) session
					.getTypeComponent("TC_Project");
		} catch (TCException tcexception) {
			logger.error(tcexception.getClass().getName(), tcexception);
		}
		Object aobj[] = { TCComponentProject.getDisplayName(session) };
		Object obj = null;
		if (tccomponentprojecttype != null) {
			try {
				TCComponentProject tccomponentproject = tccomponentprojecttype
						.find(projectID);
				if (tccomponentproject != null) {
					String s = MessageFormat.format(reg.getString(
							"duplicateProject.TITLE", "Duplicate Project"),
							aobj);
					String s1 = MessageFormat.format(reg.getString(
							"duplicateProjectID.MESSAGE",
							"Duplicate Project ID"), aobj);
					MessageBox.post(s1, s, 1);
					return;
				}
			} catch (TCException tcexception1) {
				logger.error(tcexception1.getClass().getName(), tcexception1);
			}
			TCComponent atccomponent[] = ProjectManager.getInstance()
					.findProjectsByNameOrID(projectName, false);
			if (atccomponent != null && atccomponent.length > 0) {
				String s2 = reg.getString("duplicateProject.TITLE",
						"Duplicate Project ");
				String s3 = MessageFormat.format(reg.getString(
						"duplicateProjectName.MESSAGE",
						"Duplicate Project Name"), aobj);
				MessageBox.post(s3, s2, 1);
				return;
			}
		}
		
		//modify by wuw
		if (!checkTeamAdminMembership2())
			return;
		infos = getSelectedMembersHelper(null,null);
		int i = getTeamInheritance();
		final int val$teamInheritance = i;
		final String final_s = MessageFormat.format(
				reg.getString("createProject.MESSAGE", "Creating Project"),
				aobj);
		AbstractAIFOperation abstractaifoperation = new AbstractAIFOperation(
				false) {
			public void executeOperation() {
				try {
					com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ProjectInformation2 aprojectinformation2[] = new com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ProjectInformation2[1];
					aprojectinformation2[0] = new com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ProjectInformation2();
					aprojectinformation2[0].clientId = "PLS-RAC-SESSION";
					aprojectinformation2[0].projectId = projectID;
					aprojectinformation2[0].projectName = projectName;
					aprojectinformation2[0].projectDescription = projectDesc;
					aprojectinformation2[0].useProgramContext = useProgramSec;
					aprojectinformation2[0].active = projectActiveStatus;
					aprojectinformation2[0].visible = projectVisibility;
					HashMap hashmap = new HashMap();
					if (!projectCollaborationCategories.isEmpty()) {
						String s4 = projectCollaborationCategories;
						int j = s4.indexOf(",");
						ArrayList arraylist = new ArrayList();
						for (; j != -1; j = s4.indexOf(",")) {
							String s5 = s4.substring(0, j);
							arraylist.add(s5);
							s4 = s4.substring(j + 1);
						}

						arraylist.add(s4);
						String as[] = new String[arraylist.size()];
						arraylist.toArray(as);
						hashmap.put("fnd0CollaborationCategories", as);
					}
					if (!m_parentProject.isEmpty())
						hashmap.put("fnd0Parent",
								new String[] { m_parentProject });
					hashmap.put("fnd0InheritTeamFromParent",
							new String[] { Integer
									.toString(val$teamInheritance) });
					aprojectinformation2[0].propertyMap = hashmap;
					aprojectinformation2[0].teamMembers = new com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.TeamMemberInfo[infos
							.size()];
					aprojectinformation2[0].teamMembers = (com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.TeamMemberInfo[]) infos
							.toArray(aprojectinformation2[0].teamMembers);
					com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectOpsResponse projectopsresponse = ProjectManager
							.getInstance().createProjects(aprojectinformation2);
					if (projectopsresponse != null
							&& projectopsresponse.serviceData != null) {
						if (projectopsresponse.serviceData
								.sizeOfPartialErrors() > 0) {
							final com.teamcenter.rac.kernel.TCExceptionPartial exception = SoaUtil
									.checkPartialErrorsNoThrow(projectopsresponse.serviceData);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									MessageBox.post(exception);
								}

							});
						}
						if (projectopsresponse.serviceData
								.sizeOfCreatedObjects() > 0) {
							TCComponentProject tccomponentproject1 = projectopsresponse.projectOpsOutputs[0].project;
							ProjectManager.getInstance().addCreatedProjects(
									tccomponentproject1);
							isNewlyCreated = true;
							ProjectDefinitionView.m_projectTeamManager
									.setProject(tccomponentproject1);
							if (projectUIPostActions != null)
								projectUIPostActions
										.projectCreated(tccomponentproject1);

							// 创建项目信息Item
							TCComponentItem newProjectItem = createItemForItk();

							if (newProjectItem != null) {

								tccomponentproject1.add(
										"TC_Program_Preferred_Items",
										newProjectItem);
								tccomponentproject1
										.assignToProject(new TCComponent[] { newProjectItem });
								/* williamzhou <2013-08-02> */
								tccomponentproject1.setLogicalProperty(
										"use_program_security", true);

							}
						}
					}
				} catch (Exception exception1) {
					ProjectDefinitionView.logger.error(exception1.getClass(),
							exception1);
				}
			}

		};
		IOperationService ioperationservice = (IOperationService) OSGIUtil
				.getService(AifrcpPlugin.getDefault(),
						com.teamcenter.rac.aif.kernel.IOperationService.class);

		ioperationservice.queueOperation(abstractaifoperation);
		if (isNewlyCreated && m_projectTeamManager != null) {
			isDirty = false;
			m_projectTeamManager.reloadProjectTeam();
			isNewlyCreated = false;
		}
	}

	// modify by wuwei
	private boolean modifyProjectProps(TCComponentProject tccomponentproject) {
		boolean flag = true;
		String as[] = new String[6];

		// 是否需要改版本的名字
		boolean isRename = false;
		String name1 = "";

		as[0] = "project_id";
		as[1] = "project_name";
		as[2] = "project_desc";
		as[3] = "is_active";
		as[4] = "use_program_security";
		as[5] = "is_visible";
		try {
			boolean flag1 = false;
			Vector vector = new Vector();
			TCProperty atcproperty[] = tccomponentproject.getTCProperties(as);
			String s = getProjectID();
			if (!projectID.equals(s)) {
				vector.add(atcproperty[0]);
				atcproperty[0].setStringValueData(s);
				flag1 = true;
			}
			s = getProjectName();
			if (!projectName.equals(s)) {
				vector.add(atcproperty[1]);
				atcproperty[1].setStringValueData(s);
				flag1 = true;
				isRename = true;
				name1 = s;
			}
			s = getProjectDesc();
			if (!projectDesc.equals(s)) {
				vector.add(atcproperty[2]);
				atcproperty[2].setStringValueData(s);
			}
			boolean flag2 = getProjectStatus();
			if (projectActiveStatus != flag2) {
				vector.add(atcproperty[3]);
				atcproperty[3].setLogicalValueData(flag2);
			}
			boolean flag3 = getProgramSecurity();
			if (useProgramSec != flag3) {
				vector.add(atcproperty[4]);
				atcproperty[4].setLogicalValueData(flag3);
			}
			boolean flag4 = getProgramVisibility();
			if (projectVisibility != flag4) {
				vector.add(atcproperty[5]);
				atcproperty[5].setLogicalValueData(flag4);
			}
			int i = vector.size();

			/*
			 * if (i > 0) { TCProperty atcproperty2[] = new TCProperty[i]; for
			 * (int j = 0; j < i; j++) atcproperty2[j] = (TCProperty)
			 * vector.elementAt(j);
			 * 
			 * try { tccomponentproject.setTCProperties(atcproperty2);
			 * TCProperty atcproperty1[] = tccomponentproject
			 * .getTCProperties(as); projectID =
			 * atcproperty1[0].getStringValue(); projectName =
			 * atcproperty1[1].getStringValue(); projectDesc =
			 * atcproperty1[2].getStringValue(); projectStatus =
			 * atcproperty1[3].getLogicalValue(); useProgramSec =
			 * atcproperty1[4].getLogicalValue(); projectVisibility =
			 * atcproperty1[5].getLogicalValue(); if (flag1) {
			 * tccomponentproject.clearCache("object_string");
			 * ProjectManager.getInstance() .projectModified(
			 * tccomponentproject); //teamRoleUserTree.projectModified(); } }
			 * catch (TCException tcexception1) {
			 * tccomponentproject.clearCache(as);
			 * MessageBox.post(Utilities.getCurrentFrame(), tcexception1); flag
			 * = false; } }
			 */

			// =========添加保存项目信息逻辑
			TCComponent[] childs = tccomponentproject
					.getRelatedComponents("TC_Program_Preferred_Items");
			if (childs != null && childs.length > 0) {
				TCComponentItem jci6ProgramInfo = null;
				for (int k = 0; k < childs.length; k++) {
					String type = childs[k].getType();
					if ("JCI6_ProgramInfo".equals(type)) {
						jci6ProgramInfo = (TCComponentItem) childs[k];
						continue;
					}
				}
				if (jci6ProgramInfo != null) {

					if (isRename) {
						System.out
								.println("=======最新修改1  修改object_name的项目信息和其最新版本=============");
						userService.call("open_or_close_pass",
								new Object[] { 1 });
						jci6ProgramInfo.setProperty("object_name", name1);

						jci6ProgramInfo.getLatestItemRevision().setProperty(
								"object_name", name1);
						userService.call("open_or_close_pass",
								new Object[] { 0 });
					}

					setProjectInfoProperties(jci6ProgramInfo);

				}
			}
			// ========================

		} catch (TCException tcexception) {
			MessageBox.post(Utilities.getCurrentFrame(), tcexception);
			flag = false;
		}
		return flag;
	}

	int isMODIFYagian=0;
	Vector<TCComponent> teamMembers=new Vector<TCComponent>();
	Vector<TCComponentUser> teamAdmins=new Vector<TCComponentUser>();
	Vector<TCComponentUser> allPrivUsers=new Vector<TCComponentUser>();
	private void modifyProjectOperation() {
		setLoading(true);
		disableButtons();
		boolean checkmembership = checkTeamAdminMembership();
		System.out.println("modifyProjectOperation checkmembership-->"
				+ checkmembership);
		if (!checkmembership) {
			setLoading(false);
			return;
		} else {
			
			String activeStatus = getComponentValue(activeStatusBox);
			if ("Active".equals(activeStatus)) {
				projectActiveStatus = true;
			} else {
				projectActiveStatus = false;
			}
			
			System.out.println("modifyProjectOperation projectActiveStatus-->"+projectActiveStatus);
			
			collectProjectMetaData();
			result = null;
			Object aobj[] = { TCComponentProject.getDisplayName(session) };
			final String final_s = MessageFormat
					.format(reg.getString("modifyProject.MESSAGE",
							"Modifying Project"), aobj);
			int i = getTeamInheritance();
			
			TCComponentUser userProject=null;
			try {
				userProject = (TCComponentUser) project
						.getRelatedComponent("owning_user");
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 isMODIFYagian=0;
			TCComponentUser user1 = session.getUser();
			if(user1==userProject){
				infos = getSelectedMembersHelper(this.project,null);
			}else{
				infos = getSelectedMembersHelper(this.project,user1);
				isMODIFYagian=1;
			}
			
			final int val$teamInheritance = i;
			AbstractAIFOperation abstractaifoperation = new AbstractAIFOperation(
					false) {
				public void executeOperation() {
					try {
						com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ModifyProjectsInfo2 amodifyprojectsinfo2[] = new com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ModifyProjectsInfo2[1];
						amodifyprojectsinfo2[0] = new com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ModifyProjectsInfo2();
						amodifyprojectsinfo2[0].sourceProject = project;
						amodifyprojectsinfo2[0].clientId = "PLS-RAC-SESSION";
						amodifyprojectsinfo2[0].projectInfo = new com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ProjectInformation2();
						amodifyprojectsinfo2[0].projectInfo.projectId = projectID;
						amodifyprojectsinfo2[0].projectInfo.projectName = projectName;
						amodifyprojectsinfo2[0].projectInfo.projectDescription = projectDesc;
						amodifyprojectsinfo2[0].projectInfo.useProgramContext = useProgramSec;
						amodifyprojectsinfo2[0].projectInfo.active = projectActiveStatus;
						amodifyprojectsinfo2[0].projectInfo.visible = projectVisibility;
						HashMap hashmap = new HashMap();
						ArrayList arraylist = new ArrayList();
						String s = projectCollaborationCategories;
						System.out
								.println("lala executeOperation====projectCollaborationCategories==="
										+ projectCollaborationCategories);

						for (int j = s.indexOf(","); j != -1; j = s
								.indexOf(",")) {
							String s1 = s.substring(0, j);
							arraylist.add(s1);
							s = s.substring(j + 1);
						}

						arraylist.add(s);
						String as[] = new String[arraylist.size()];
						arraylist.toArray(as);
						hashmap.put("fnd0CollaborationCategories", as);
						hashmap.put("fnd0InheritTeamFromParent",
								new String[] { Integer
										.toString(val$teamInheritance) });
						amodifyprojectsinfo2[0].projectInfo.propertyMap = hashmap;
						amodifyprojectsinfo2[0].projectInfo.teamMembers = new com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.TeamMemberInfo[infos
								.size()];
						amodifyprojectsinfo2[0].projectInfo.teamMembers = (com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.TeamMemberInfo[]) infos
								.toArray(amodifyprojectsinfo2[0].projectInfo.teamMembers);
						ProjectDefinitionView.logger.info((new StringBuilder(
								"Modify ")).append(projectName)
								.append(" server call starts").toString());
						// open bypass
						userService.call("open_or_close_pass",
								new Object[] { 1 });
						
						//TCComponentUser relatedUser = (TCComponentUser) project.getRelatedComponent("owning_user");
						//TCComponentGroup owningGroup = (TCComponentGroup) project.getRelatedComponent("owning_group");
						//System.out.println("relatedUser-->"+relatedUser+"  owningGroup:"+owningGroup);
						//project.changeOwner(session.getUser(), session.getGroup());
						
						result = ProjectManager.getInstance().modifyProjects(
								amodifyprojectsinfo2);
						
						if(isMODIFYagian==1){
							TCComponent[] paramArrayOfTCComponent=new TCComponent[teamMembers.size()];
							paramArrayOfTCComponent=teamMembers.toArray(paramArrayOfTCComponent);
							TCComponentUser[] paramArrayOfTCComponentUser1=new TCComponentUser[teamAdmins.size()];
							paramArrayOfTCComponentUser1=teamAdmins.toArray(paramArrayOfTCComponentUser1);
						
							TCComponentUser[] paramArrayOfTCComponentUser2=new TCComponentUser[allPrivUsers.size()];
							paramArrayOfTCComponentUser2=allPrivUsers.toArray(paramArrayOfTCComponentUser2);
							project.modifyTeam(paramArrayOfTCComponent,paramArrayOfTCComponentUser1,paramArrayOfTCComponentUser2);
						
						}
						
						//project.changeOwner(relatedUser, owningGroup);
						userService.call("open_or_close_pass",
								new Object[] { 0 });

						System.out.println("result-->" + result);
						
						int errorcnt=result.serviceData.sizeOfPartialErrors();
						for(int a =0;a<errorcnt;a++){
							ErrorValue[] errorValues = result.serviceData.getPartialError(a).getErrorValues();
							if(errorValues!=null&&errorValues.length>0){
								for(int b=0;b<errorValues.length;b++){
									System.out.println("errorValue-->" + errorValues[b].getMessage());
								}
							}
						}
						
						// modifyProjectProps(project);
						setProgramInfoForItk();

					} catch (Exception exception) {
						ProjectDefinitionView.logger.error(exception.getClass()
								.getName(), exception);
					}
				}

			};

			abstractaifoperation
					.addOperationListener(new InterfaceAIFOperationListener() {
						public void startOperation(String s) {
						}

						public void endOperation() {
							try {
								String s = project.getProperty("project_id");
								if (result != null) {
									if (result.serviceData
											.sizeOfPartialErrors() > 0) {
										final com.teamcenter.rac.kernel.TCExceptionPartial exception = SoaUtil
												.checkPartialErrorsNoThrow(result.serviceData);
										Display.getDefault().asyncExec(
												new Runnable() {
													public void run() {
														MessageBox
																.post(exception);
													}

												});
									}
									if (result.projectOpsOutputs != null
											&& result.projectOpsOutputs.length > 0) {
										System.out
												.println("lala====projectModified==="
														+ projectID);

										TCComponentProject tccomponentproject = result.projectOpsOutputs[0].project;
										
										userService.call("open_or_close_pass",
												new Object[] { 1 });
										ProjectManager.getInstance()
												.projectModified(
														tccomponentproject);
										
										userService.call("open_or_close_pass",
												new Object[] { 0 });

										System.out
												.println("lala====tccomponentproject==="
														+ tccomponentproject);
										System.out
												.println("lala====projectUIPostActions==="
														+ projectUIPostActions);
										// modifyProjectProps(tccomponentproject);

										if (projectUIPostActions != null) {

											System.out
													.println("projectUIPostActions.projectModified====argument s:"
															+ s);
											projectUIPostActions
													.projectModified(
															tccomponentproject,
															s, projectID);

										}
										setLoading(false);
										enableButtons();
									}
								}
							} catch (Exception exception1) {
								ProjectDefinitionView.logger.error(exception1
										.getClass().getName(), exception1);
							}
						}

					});
			IOperationService ioperationservice = (IOperationService) OSGIUtil
					.getService(
							AifrcpPlugin.getDefault(),
							com.teamcenter.rac.aif.kernel.IOperationService.class);
			ioperationservice.queueOperation(abstractaifoperation);
			isModified = true;
			return;
		}
	}

	private boolean doesUserHaveWriteAccessToThisObject(
			TCComponentProject tccomponentproject) {
		boolean flag = true;
		TCSession tcsession = getTCSession();
		TCAccessControlService tcaccesscontrolservice = tcsession
				.getTCAccessControlService();
		try {
			boolean aflag[] = tcaccesscontrolservice.checkPrivileges(
					tccomponentproject, new String[] { "WRITE" });
			flag = aflag[0];
		} catch (Exception exception) {
			flag = false;
			logger.error(exception.getClass().getName(), exception);
		}
		return flag;
	}

	private void copyProjectOperation() {
		if (!checkTeamAdminMembership())
			return;
		collectProjectMetaData();
		TCComponentProjectType tccomponentprojecttype = null;
		try {
			tccomponentprojecttype = (TCComponentProjectType) session
					.getTypeComponent("TC_Project");
		} catch (TCException tcexception) {
			logger.error(tcexception.getClass().getName(), tcexception);
		}
		Object aobj[] = { TCComponentProject.getDisplayName(session) };
		Object obj = null;
		if (tccomponentprojecttype != null) {
			try {
				TCComponentProject tccomponentproject = tccomponentprojecttype
						.find(projectID);
				if (tccomponentproject != null) {
					String s = MessageFormat.format(reg.getString(
							"duplicateProject.TITLE", "Duplicate Project"),
							aobj);
					String s1 = MessageFormat.format(reg.getString(
							"duplicateProjectID.MESSAGE",
							"Duplicate Project ID"), aobj);
					MessageBox.post(s1, s, 1);
					return;
				}
			} catch (TCException tcexception1) {
				logger.error(tcexception1.getClass().getName(), tcexception1);
			}
			TCComponent atccomponent[] = ProjectManager.getInstance()
					.findProjectsByNameOrID(projectName, false);
			if (atccomponent != null && atccomponent.length > 0) {
				String s2 = reg.getString("duplicateProject.TITLE",
						"Duplicate Project ");
				String s3 = MessageFormat.format(reg.getString(
						"duplicateProjectName.MESSAGE",
						"Duplicate Project Name"), aobj);
				MessageBox.post(s3, s2, 1);
				return;
			}
		}
		infos = getSelectedMembersHelper(project,null);
		int i = getTeamInheritance();
		final int teamInheritance = i;
		final String final_s = MessageFormat.format(
				reg.getString("copyProject.MESSAGE", "Copying Project"), aobj);
		Job job = new Job(final_s) {
			protected IStatus run(IProgressMonitor iprogressmonitor) {
				iprogressmonitor.beginTask(final_s, -1);
				try {
					if (project != null) {
						com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ProjectInformation2 projectinformation2 = new com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ProjectInformation2();
						projectinformation2.projectId = projectID;
						projectinformation2.projectName = projectName;
						projectinformation2.projectDescription = projectDesc;
						projectinformation2.projectDescription = projectDesc;
						projectinformation2.active = projectActiveStatus;
						projectinformation2.visible = projectVisibility;
						projectinformation2.useProgramContext = useProgramSec;
						HashMap hashmap = new HashMap();
						if (!projectCollaborationCategories.isEmpty()) {
							String s4 = projectCollaborationCategories;
							int j = s4.indexOf(",");
							ArrayList arraylist = new ArrayList();
							for (; j != -1; j = s4.indexOf(",")) {
								String s5 = s4.substring(0, j);
								arraylist.add(s5);
								s4 = s4.substring(j + 1);
							}

							arraylist.add(s4);
							String as[] = new String[arraylist.size()];
							arraylist.toArray(as);
							hashmap.put("fnd0CollaborationCategories", as);
						}
						hashmap.put("fnd0InheritTeamFromParent",
								new String[] { Integer
										.toString(teamInheritance) });
						projectinformation2.propertyMap = hashmap;
						projectinformation2.teamMembers = new com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.TeamMemberInfo[infos
								.size()];
						projectinformation2.teamMembers = (com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.TeamMemberInfo[]) infos
								.toArray(projectinformation2.teamMembers);
						com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.CopyProjectsInfo2 acopyprojectsinfo2[] = new com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.CopyProjectsInfo2[1];
						acopyprojectsinfo2[0] = new com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.CopyProjectsInfo2();
						acopyprojectsinfo2[0].projectInfo = projectinformation2;
						acopyprojectsinfo2[0].sourceProject = project;
						acopyprojectsinfo2[0].clientId = projectinformation2.projectId;
						ProjectLevelSecurityService projectlevelsecurityservice = ProjectLevelSecurityService
								.getService(session);
						com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectOpsResponse projectopsresponse = projectlevelsecurityservice
								.copyProjects2(acopyprojectsinfo2);
						if (projectopsresponse.serviceData
								.sizeOfPartialErrors() > 0) {
							final com.teamcenter.rac.kernel.TCExceptionPartial exception = SoaUtil
									.checkPartialErrorsNoThrow(projectopsresponse.serviceData);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									MessageBox.post(exception);
								}

							});
						}
						if (projectopsresponse.serviceData
								.sizeOfCreatedObjects() > 0) {
							TCComponentProject tccomponentproject1 = (TCComponentProject) projectopsresponse.serviceData
									.getCreatedObject(0);
							ProjectManager.getInstance().addCreatedProjects(
									tccomponentproject1);

							// copy逻辑
							TCComponent[] tcComponetSub = tccomponentproject1
									.getRelatedComponents("TC_Program_Preferred_Items");
							TCComponentItem newProjectItem = createItemForItk();
							if (newProjectItem != null) {
								tccomponentproject1.add(
										"TC_Program_Preferred_Items",
										newProjectItem);
								if (tcComponetSub != null
										&& tcComponetSub.length > 0) {
									for (int i = 0; i < tcComponetSub.length; i++) {
										String type = tcComponetSub[i]
												.getType();
										if (!"JCI6_ProgramInfo".equals(type)) {
											tccomponentproject1
													.add("TC_Program_Preferred_Items",
															tcComponetSub[i]);
										}
									}

								}
							}
						}
					}

				} catch (Exception exception1) {
					ProjectDefinitionView.logger.error(exception1.getClass()
							.getName(), exception1);
				} finally {
					iprogressmonitor.done();
				}

				return Status.OK_STATUS;
			}

		};
		job.setPriority(10);
		job.setRule(getTCSession().getOperationJobRule());
		job.schedule();
		ProjectManager.getInstance().projectModified(project);
		validateButtons();
	}

	private synchronized void deleteProjectOperation() {
		Object[] arrayOfObject = { TCComponentProject
				.getDisplayName(this.session) };
		// ceshi m_idText
		boolean bool = MessageDialog.openConfirm(this.m_headerComposite
				.getShell(), this.reg.getString("confirmDelete.TITLE",
				"Confirm Delete"), MessageFormat.format(
				this.reg.getString("confirmDeleteProject.MESSAGE"),
				arrayOfObject));
		if (!(bool))
			return;
		String str1 = getProjectID();
		final String val$id = str1;
		String str2 = MessageFormat
				.format(this.reg.getString("deleteProject.MESSAGE",
						"Deleting Project"), arrayOfObject);
		this.session.setStatus(str2);

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (ProjectDefinitionView.this.project == null)
					return;
				// TCComponentProject localTCComponentProject =
				// ProjectDefinitionView.this.session
				// .getCurrentProject();

				TCComponentProject localTCComponentProject = ProjectDefinitionView.this.project;
				System.out.println("deleteProjectOperation project-->"
						+ localTCComponentProject);

				try {
					if (Debug.isOn("project"))
						Debug.println("deleteProjectOperation: deleting... "
								+ val$id);

					userService.call("open_or_close_pass", new Object[] { 1 });
					if ((localTCComponentProject != null)
							&& (localTCComponentProject
									.equals(ProjectDefinitionView.this.project))) {

						// 添加删除项目信息，先去掉关系再删除
						TCComponent[] childs = ProjectDefinitionView.this.project
								.getRelatedComponents("TC_Program_Preferred_Items");
						if (childs != null && childs.length > 0) {
							ProjectDefinitionView.this.project.remove(
									"TC_Program_Preferred_Items", childs);
							TCComponentItem jci6ProgramInfo = null;
							for (int i = 0; i < childs.length; i++) {
								String type = childs[i].getType();
								if ("JCI6_ProgramInfo".equals(type)) {
									jci6ProgramInfo = (TCComponentItem) childs[i];
									break;
								}
							}
							if (jci6ProgramInfo != null) {
								ProjectDefinitionView.this.project.remove(
										"TC_Program_Preferred_Items",
										jci6ProgramInfo);
								ProjectDefinitionView.this.project.refresh();
								jci6ProgramInfo.delete();
							}
						}
						ProjectDefinitionView.this.session
								.setCurrentProject(null);
					}

					System.out
							.println("lala deleteProject localTCComponentProject-->"
									+ localTCComponentProject);

					ProjectManager.getInstance().deleteProject(
							localTCComponentProject);

					// add by wuwei
					ProjectDefinitionView.this.project.delete();

					userService.call("open_or_close_pass", new Object[] { 0 });
				} catch (Exception localException) {
					MessageBox.post(localException);
				}
				ProjectDefinitionView.this.session.setReadyStatus();
			}
		});
	}

	public void sessionChanged(SessionChangedEvent paramSessionChangedEvent) {
		AbstractTCAdminApplicationViewHelper localAbstractTCAdminApplicationViewHelper = ProjectManager
				.getApp();
		try {
			localAbstractTCAdminApplicationViewHelper
					.initializeApplicationPriviledge();
		} catch (Exception localException) {
			logger.error(localException.getClass().getName(), localException);
		}
		this.isPA = localAbstractTCAdminApplicationViewHelper.isPAPrivileged();
		if (localAbstractTCAdminApplicationViewHelper.isPTAPrivileged()) {
			boolean bool = isTeamDefined();
			if ((bool) && (this.project != null)
					&& (m_projectTeamManager.getRoot() != null)
					&& (m_projectTeamManager.getRoot().getObject() != null)) {
				ProjectTeamStatus localProjectTeamStatus = (ProjectTeamStatus) m_projectTeamManager
						.getRoot().getObject();
				this.isPTA = localProjectTeamStatus
						.isAProjectTeamAdminMember(getTCSession().getUser());
			}

		} else {
			this.isPTA = localAbstractTCAdminApplicationViewHelper
					.isPTAPrivileged();
		}
	}

	// modify by wuwei--调用LoadData
	protected synchronized void processSetInput(
			IWorkbenchPart paramIWorkbenchPart, List<Object> paramList) {
		// 加载项目信息属性值到定义面板
		// clearComponentValue();
		System.out.println("========5.20====调用LoadData=====isPTA:" + isPTA
				+ "  isPA:" + isPA);

		Object localObject1 = (paramList.isEmpty()) ? null : paramList.get(0);

		System.out.println("localObject1=======" + localObject1);
		if (localObject1 == null)
			return;

		if (localObject1 instanceof ProjectRootNode) {
			System.out.println("++++clearOperation=======");
			clearOperation();
			clearComponentValue();
		}
		m_projectTeamManager.clearProjectTeamSearch(true);
		// add by wuwei
		m_projectTeamManager.setButtonStatus(true, true);

		// 这里找到programinfo或者指定版本
		TCComponentProject localTCComponentProject = (TCComponentProject) AdapterUtil
				.getAdapter(localObject1, TCComponentProject.class, true);

		this.project = localTCComponentProject;

		int i = ((localTCComponentProject != null) && (this.project != null) && (this.project
				.getUid().equals(localTCComponentProject.getUid()))) ? 1 : 0;

		if (i == 1) {
			this.isModified = true;
		} else {
			this.isModified = false;
		}

		int j = ((i != 0) && (!(this.isModified))) ? 0 : 1;

		System.out.println("processSetInput方法  localTCComponentProject:"
				+ localTCComponentProject + "  this.project-->" + this.project
				+ " this.isModified:" + this.isModified);

		setInputObject(paramIWorkbenchPart, localTCComponentProject);
		if ((localTCComponentProject == null) && (i == 0)) {
			// this.project = localTCComponentProject;
			// m_projectTeamManager.setButtonStatus(false, false);
			if (!(isDirtyStill())) {
				clearOperation();
				clearComponentValue();
			} else {
				validateButtons();
			}

			setXMJCPanelEnabled(true);
			setXMLXPanelEnabled(true);
			setGNPZPanelEnabled(true);
			setXMRQPanelEnabled(true);

			System.out.println("@@ return");
			return;
		}

		System.out.println("ww localTCComponentProject-->"
				+ localTCComponentProject + "  j:" + j);
		if ((localTCComponentProject == null) || (j == 0)) {
			String propties[] = { "project_id", "project_name", "project_desc",
					"is_active", "use_program_security" };
			TCProperty[] arrayOfTCProperty = null;
			try {
				arrayOfTCProperty = localTCComponentProject
						.getTCProperties(propties);
			} catch (TCException localTCException1) {
				logger.error(localTCException1.getClass().getName(),
						localTCException1);
			}

			idField.setText(arrayOfTCProperty[0].getStringValue());
			nameField.setText(arrayOfTCProperty[1].getStringValue());
			descField.setText(arrayOfTCProperty[2].getStringValue());
			// activeStatusRB.setSelected(
			// arrayOfTCProperty[3].getLogicalValue());
			// usePrgmSecCB.setSelected(
			// arrayOfTCProperty[4].getLogicalValue());
			// teamRoleUserTree.loadTeam(null);

			System.out.println("===setXMJCPanelEnabled(true)===");
			setXMJCPanelEnabled(true);
			setXMLXPanelEnabled(true);
			setGNPZPanelEnabled(true);
			// setXMRQPanelEnabled(true);
			{
				isPTA = false;
				// projAdminPanel.setIsPTA(false);
				nameField.setEnabled(isPA || isPTA);
				idField.setEnabled(isPA || isPTA);
				descField.setEnabled(isPA || isPTA);
				// activeStatusRB.setEnabled(isPA || isPTA);
				// inactiveStatusRB.setEnabled(isPA || isPTA);
				// inactiveInvisibleStatusRB.setEnabled(isPA || isPTA);
				// usePrgmSecCB.setEnabled(isPA || isPTA);
			}

			System.out.println("===isPTA:" + isPTA);
			m_projectTeamManager.setButtonStatus(true, true);
			if (isPA) {
				setXMRQPanelEnabled(true);// add wuh
				setGNPZPanelEnabled(true);

			}

			validateButtons();

			closeDateButton.setEnabled(isPA || isPTA);// add by wuh
														// 2014-5-27
			// eqmRadioButton.setEnabled(isPA);// add 2014-6-5
			System.out.println("@@ return");
			return;
		}
		setLoading(true);
		disableButtons();

		this.projectTeamMembers = null;
		String[] arrayOfString2 = { TCComponentProject
				.getDisplayName(ProjectManager.getInstance().getTCSession()) };
		Object localObject2 = ProjectConstants.projectReg
				.getStringWithSubstitution("loadingProjects.MESSAGE",
						arrayOfString2);

		AbstractAIFOperation localObject3 = new AbstractAIFOperation(
				(String) localObject2, false) {
			public void executeOperation() {
				try {
					userService.call("open_or_close_pass",
							new Object[] { 1 });				
					ProjectManager.setProjectTeamMembersPropertyPolicy();
					ProjectLevelSecurity.ProjectClientId[] arrayOfProjectClientId = new ProjectLevelSecurity.ProjectClientId[1];
					arrayOfProjectClientId[0] = new ProjectLevelSecurity.ProjectClientId();
					arrayOfProjectClientId[0].clientId = "PLS-RAC-SESSION";
					arrayOfProjectClientId[0].tcProject = ProjectDefinitionView.this.project;
					
					ProjectLevelSecurity.ProjectTeamsResponse localProjectTeamsResponse = ProjectManager
							.getInstance().getProjectTeamMembers(
									arrayOfProjectClientId);
					
					userService.call("open_or_close_pass",
							new Object[] { 0 });
					ProjectDefinitionView.this.projectTeamMembers = localProjectTeamsResponse.projectTeams;

					
					
					int cnt=projectTeamMembers.length;
					System.out.println("prjLevelSecurityService::getProjectTeamMembers cnt-->"+cnt);
					for(int i=0;i<cnt;i++){
						TCComponent[] projectTeamAdminsArray = projectTeamMembers[i].projectTeamAdmins;
						TCComponent[] privMembersArray= projectTeamMembers[i].privMembers;
						
						System.out.println("LoadData projectTeamAdminsArray:"+projectTeamAdminsArray+"  privMembersArray:"+privMembersArray);
						
						if(projectTeamAdminsArray!=null){
							System.out.println("LoadData projectTeamAdminsArray.length:"+projectTeamAdminsArray.length);
							for(int a=0;a<projectTeamAdminsArray.length;a++){
								System.out.println("projectTeamAdmins:"+projectTeamAdminsArray[a]);
							}
						}
						
						if(privMembersArray!=null){
							System.out.println("LoadData privMembersArray.length:"+privMembersArray.length);
							for(int a=0;a<privMembersArray.length;a++){
								System.out.println("LoadData privMembers:"+privMembersArray[a]);
							}
						}
					}
					
				} catch (Exception localException) {
					localException.printStackTrace();
				}
				ProjectDefinitionView.logger
						.info("AIFOpertaion getProjectTeamJob completed for project "
								+ project.toString());
			}
		};

		((AbstractAIFOperation) localObject3)
				.addOperationListener(new InterfaceAIFOperationListener() {
					public void startOperation(String paramString) {
					}

					public void endOperation() {
						ProjectDefinitionView.this.getProjectTeamJobPost();
						ProjectManager.restorePropertyPolicy();
					}
				});

		IOperationService localIOperationService = (IOperationService) OSGIUtil
				.getService(AifrcpPlugin.getDefault(), IOperationService.class);
		localIOperationService.queueOperation((Job) localObject3);
		logger.info("getProjectTeamJob queued for project "
				+ localTCComponentProject.toString());

		this.project = localTCComponentProject;
		String[] arrayOfString1 = new String[11];
		arrayOfString1[0] = "project_id";
		arrayOfString1[1] = "project_name";
		arrayOfString1[2] = "project_desc";
		arrayOfString1[3] = "is_active";
		arrayOfString1[4] = "use_program_security";
		arrayOfString1[5] = "is_visible";
		arrayOfString1[6] = "owning_user";
		arrayOfString1[7] = "object_string";
		arrayOfString1[8] = "fnd0CollaborationCategories";
		arrayOfString1[9] = "fnd0Parent";
		arrayOfString1[10] = "fnd0InheritTeamFromParent";
		TCProperty[] arrayOfTCProperty = null;
		try {
			arrayOfTCProperty = localTCComponentProject
					.getTCProperties(arrayOfString1);
		} catch (TCException localTCException1) {
			logger.error(localTCException1.getClass().getName(),
					localTCException1);
		}

		try {
			this.projectID = arrayOfTCProperty[0].getStringValue();
			this.projectName = arrayOfTCProperty[1].getStringValue();
			this.projectDesc = arrayOfTCProperty[2].getStringValue();
			this.projectActiveStatus = arrayOfTCProperty[3].getLogicalValue();
			this.projectVisibility = arrayOfTCProperty[5].getLogicalValue();
			this.useProgramSec = arrayOfTCProperty[4].getLogicalValue();

			System.out.println("调用LoadData  projectActiveStatus:"
					+ projectActiveStatus + "  projectVisibility:"
					+ projectVisibility);

			boolean bool = (!(this.projectActiveStatus))
					&& (!(this.projectVisibility));

			/**
			 * modify by wuwei this.m_idText.setText(this.projectID);
			 * this.m_nameText.setText(this.projectName);
			 * this.m_descriptionText.setText(this.projectDesc);
			 */
			this.idField.setText(this.projectID);
			this.nameField.setText(this.projectName);
			this.descField.setText(this.projectDesc);

			this.projectCollaborationCategories = Utilities.getStringForArray(
					arrayOfTCProperty[8].getStringArrayValue(), ",");
			if (m_categoryCombo != null)
				this.m_categoryCombo.setSelectedValue(arrayOfTCProperty[8]);

			if (this.m_parentText != null)
				this.m_parentText.setText(arrayOfTCProperty[9]
						.getDisplayValue());

			if (this.m_activeStatusBtn != null)
				this.m_activeStatusBtn.setSelection(this.projectActiveStatus);
			if (this.m_inactiveStatusBtn != null)
				this.m_inactiveStatusBtn
						.setSelection((!(this.projectActiveStatus))
								&& (!(bool)));

			if (this.useProgramSec) {
				this.m_ProgramBtn.setSelection(true);
				this.m_ProjectBtn.setSelection(false);
				this.m_ProgramBtn.setEnabled(true);
				this.m_teamInheritance.setEnabled(false);
			} else {
				this.m_ProgramBtn.setSelection(false);
				this.m_ProjectBtn.setSelection(true);
				if ((this.m_parentText != null)
						&& (!(this.m_parentText.getText().isEmpty()))) {
					this.m_ProgramBtn.setEnabled(false);
					this.m_teamInheritance.setEnabled(true);

					m_projectTeamManager.setButtonStatus(false, false);
				} else {
					this.m_ProgramBtn.setEnabled(true);
					this.m_teamInheritance.setEnabled(false);

					m_projectTeamManager.setButtonStatus(true, true);
				}
			}
			
			

			// modify by wuwei
			if (this.m_activeStatusBtn != null)
				m_activeStatusBtn.setSelection(projectActiveStatus);
			if (this.m_inactiveandInviisibleStatusBtn != null)
				this.m_inactiveandInviisibleStatusBtn.setSelection(bool);

			m_projectTeamManager.setProject(this.project);
			if ((this.useProgramSec)
					|| (arrayOfTCProperty[9].getReferenceValue() == null))
				m_projectTeamManager.resetOrgTree();
			if (arrayOfTCProperty[9].getReferenceValue() != null) {
				TCComponentProject proj2 = (TCComponentProject) arrayOfTCProperty[9]
						.getReferenceValue();
				ProgramTreeFilter localObject6 = new ProgramTreeFilter(proj2, 3);
				m_projectTeamManager.getAvailableTreeViewer().addFilter(
						localObject6);
				
			}
			this.teamInheritanceValue = arrayOfTCProperty[10].getIntValue();
			this.m_teamInheritance.setSelection(this.teamInheritanceValue == 1);

			
			
			
			// add by wuwei
			TCComponent[] childs = project
					.getRelatedComponents("TC_Program_Preferred_Items");
			if (childs != null && childs.length > 0) {
				System.out.println("------加载项目信息属性值到定义面板-----");

				TCComponentItem jci6ProgramInfo = null;
				for (int a = 0; a < childs.length; a++) {
					String type = childs[a].getType();
					System.out.println("lala type------>" + type);
					if ("JCI6_ProgramInfo".equals(type)) {
						jci6ProgramInfo = (TCComponentItem) childs[a];
						break;
					}
				}

				TCComponentUser tlUser=null;
				if (jci6ProgramInfo != null) {

					TCComponentItemRevision jci6ProgramInfoRev = null;

					if (this.getSelectItemRevision() == null) {
						jci6ProgramInfoRev = jci6ProgramInfo
								.getLatestItemRevision();
					} else {

						System.out
								.println("========5.20====调用this.getSelectItemRevision=======--------------");
						jci6ProgramInfoRev = this.getSelectItemRevision();
						System.out
								.println("==========5.20选择的是当前的版本对象！！！============"
										+ jci6ProgramInfoRev
												.getProperty("object_string"));
					}
					// 5.28修改
					jci6ProgramInfoRev.refresh();

					epicStr = jci6ProgramInfo.getProperty("jci6_EPIC_PN");
					if (epicStr == null || "".equals(epicStr))
						epicStr = "TBD";
					oemNameStr = jci6ProgramInfoRev.getProperty("jci6_OEMName");

					System.out.println("----显示jci6_OEMName---：" + oemNameStr);

					modeNameStr = jci6ProgramInfoRev
							.getProperty("jci6_OEMModelName");
					System.out.println("----显示OEMModelName---：" + modeNameStr);

					secStr = jci6ProgramInfoRev.getProperty("jci6_ProgramSec");
					System.out.println("----显示ProgramSec---：" + secStr);

					diviStr = jci6ProgramInfoRev
							.getProperty("jci6_ProgramDivi");
					System.out.println("----显示ProjectTL---：" + diviStr);

					tlUser = (TCComponentUser) jci6ProgramInfoRev
							.getReferenceProperty("jci6_ProjectTL");
					if (tlUser != null) {
						tlStr = "" + tlUser.getProperty("person");
						System.out.println("----new 显示tlUser---：" + tlStr);

					}

					// add by wuh 2014-5-26
					createDateStr = jci6ProgramInfo
							.getDateProperty("creation_date");
					closeDateStr = jci6ProgramInfo
							.getDateProperty("jci6_CloseDate");
					// add 2014-6-5
					// eqmStr = jci6ProgramInfo.getTCProperty("jci6_IsRunEQM")
					// .getBoolValue();

					targetPosStr = jci6ProgramInfoRev
							.getProperty("jci6_TargetPos");
					System.out.println("----显示targetPos---：" + targetPosStr);

					boolean statusVal = jci6ProgramInfoRev
							.getLogicalProperty("jci6_ActiveStatus");
					// .getProperty("jci6_ActiveStatus");

					// 5.17修改---Active Status正常显示
					System.out.println("jci6_ActiveStatus的属性值为：--------------"
							+ statusVal);

					// if ("Y".equalsIgnoreCase(statusVal))
					if (statusVal) {
						activeStatusStr = "Active";
						this.m_activeStatusBtn.setSelection(true);
						this.m_inactiveStatusBtn.setSelection(false);
					} else {
						activeStatusStr = "Inactive";
						this.m_activeStatusBtn.setSelection(false);
						this.m_inactiveStatusBtn.setSelection(true);
					}

					System.out.println("----显示activeStatus-----："
							+ activeStatusStr);

					projectStatusStr = jci6ProgramInfoRev
							.getProperty("jci6_ProgramState");
					System.out.println("----显示ProgramState-----："
							+ projectStatusStr);

					categoryStr = jci6ProgramInfoRev
							.getProperty("jci6_Category");
					System.out.println("----显示Category-----：" + categoryStr);

					productStr = jci6ProgramInfoRev.getProperty("jci6_Product");
					System.out.println("----显示Product-----: " + productStr);

					typeStr = jci6ProgramInfoRev.getProperty("jci6_Type");
					System.out.println("----显示jci6_Type-----: " + typeStr);

					pStartStr = jci6ProgramInfoRev
							.getDateProperty("jci6_StartDate");
					System.out.println("----显示jci6_StartDate-----: "
							+ pStartStr);

					pEndStr = jci6ProgramInfoRev
							.getDateProperty("jci6_EndDate");
					System.out.println("----显示jci6_EndDate-----: " + pEndStr);

					lcStr = jci6ProgramInfoRev.getDateProperty("jci6_SOP");
					System.out.println("----显示jci6_SOP-----: " + lcStr);

					sopfyStr = ""
							+ jci6ProgramInfoRev.getIntProperty("jci6_SOPFY");
					if ("0".equals(sopfyStr)) {
						sopfyStr = "";
					}
					System.out.println("----显示jci6_SOPFY-----: " + sopfyStr);

					modelYearStr = ""
							+ jci6ProgramInfoRev
									.getIntProperty("jci6_ModelYear");
					System.out.println("----显示jci6_ModelYear-----: "
							+ modelYearStr);
					if ("0".equals(modelYearStr)) {
						modelYearStr = "";
					}

					functionStr = jci6ProgramInfoRev
							.getProperty("jci6_Function");
					System.out.println("----显示jci6_Function-----: "
							+ functionStr);

					frontSSStr = jci6ProgramInfoRev.getProperty("jci6_FrontSS");
					System.out
							.println("----显示jci6_FrontSS-----: " + frontSSStr);

					rearSSStr = jci6ProgramInfoRev.getProperty("jci6_RearSS");
					System.out.println("----显示jci6_RearSS-----: " + rearSSStr);

					trackStr = jci6ProgramInfoRev.getProperty("jci6_Track");
					System.out.println("----显示jci6_Track-----: " + trackStr);

					reclinerStr = jci6ProgramInfoRev
							.getProperty("jci6_Recliner");
					System.out.println("----显示jci6_Recliner-----: "
							+ reclinerStr);

					pumpStr = jci6ProgramInfoRev.getProperty("jci6_PUMP");
					System.out.println("----显示jci6_PUMP-----: " + pumpStr);

					vtaStr = jci6ProgramInfoRev.getProperty("jci6_VTA");
					System.out.println("----显示jci6_VTA-----: " + vtaStr);

					latchStr = jci6ProgramInfoRev.getProperty("jci6_Latch");
					System.out.println("----显示jci6_Latch-----: " + latchStr);

					// PDX信息只读特殊处理,属性不全
					pxdStr = jci6ProgramInfoRev.getProperty("jci6_PDxSeq");
					System.out.println("----显示jci6_PDxSeq-----: " + pxdStr);

					gebtStr = jci6ProgramInfoRev
							.getProperty("jci6_GEBTTemplate");
					System.out.println("----显示jci6_GEBTTemplate-----: "
							+ gebtStr);

					// SMTE_lov = new PropertyArray();

					// 5.12--SMTE
					TCProperty tcProperty = jci6ProgramInfoRev
							.getTCProperty("jci6_SMTE_Variables");

					// SMTE_lov.load(tcProperty);
					// SMTE_lov.save(tcProperty);

					System.out.println("-------显示jci6_SMTE_lov-----: ");

					// 5.6修改
					budgetStateStr = jci6ProgramInfoRev
							.getProperty("jci6_BudgetState");
					System.out.println("----显示jci6_BudgetState-----: "
							+ budgetStateStr);

					String remarkStr = jci6ProgramInfoRev
							.getProperty("jci6_Remark");

					equStr = jci6ProgramInfoRev.getProperty("jci6_EQU");
					System.out.println("----显示jci6_EQU-----: " + equStr);

					if (equStr == null || "".equals(equStr))
						equStr = "0";

					approvedAmountStr = jci6ProgramInfoRev
							.getProperty("jci6_SignedMoney");
					pdxSignStr = jci6ProgramInfoRev
							.getDateProperty("jci6_PDxSignDate");

					// 添加值到控件上显示
					epicTextField.setText(epicStr);
					oemNameLov.setSelectedItem(oemNameStr);
					modeNameField.setText(modeNameStr);
					secLov.setText(secStr);
					diviLov.setText(diviStr);
					tlLov.setSelectedValue(tlStr);
					// add by wuh 2014-5-26
					createDateButton.setDate(createDateStr);
					closeDateButton.setDate(closeDateStr);
					// add by wuh 2014-6-5
					// eqmRadioButton.setSelected(eqmStr);

					targetPosField.setText(targetPosStr);
					activeStatusBox.setSelectedItem(activeStatusStr);
					loadProjectStatusBoxData();
					projectStatusBox.setSelectedItem(projectStatusStr);
					loadCategoryBox();
					categoryBox.setSelectedItem(categoryStr);
					productLov.setSelectedItem(productStr);
					typeLov.setSelectedItem(typeStr);
					pStartDate.setDate(pStartStr);
					pEndDate.setDate(pEndStr);
					lcDate.setDate(lcStr);
					sopfyLov.setSelectedItem(sopfyStr);
					modelYearLov.setSelectedItem(modelYearStr);
					functionTextField.setText(functionStr);
					frontSSLov.setSelectedItem(frontSSStr);
					rearSSLov.setSelectedItem(rearSSStr);
					trackLov.setSelectedItem(trackStr);
					reclinerLov.setSelectedItem(reclinerStr);
					pumpLov.setSelectedItem(pumpStr);
					vtaLov.setSelectedItem(vtaStr);
					latchLov.setSelectedItem(latchStr);
					pxdLov.setSelectedItem(pxdStr);

					// 5.6修改
					latestComboLov.setSelectedItem(budgetStateStr);
					// 5.31修改
					remarkField.setText(remarkStr);

					gebtLov.setSelectedItem(gebtStr);
					equTextField.setText(equStr);
					// equSignDate.setDate(equSignStr);
					// calcDate.setDate(calcStr);
					// calcByLov.setSelectedItems(calcByStr);
					approvedAmountTextField.setText(approvedAmountStr);
					pdxSignDate.setDate(pdxSignStr);

					System.out.println("----ww 添加值到控件上显示----");

					// setTargetPossibilityVisiiable(targetPosStr);
					System.out.println("----设置targetPossibility状态及值----");

					resetSecitionLov(secStr);// 设置section lov值变化
				}

				isPTA = false;
				isPA = false;
				
				ProjectTeamStatus projectteamstatus = (ProjectTeamStatus) m_projectTeamManager
						.getRoot().getObject();
				
				//add by wuwei
				m_projectTeamManager.dorefreshTeamAdmin(tlUser, projectteamstatus);
				
				TCComponentUser tccomponentuser = getTCSession().getUser();

				CopyOnWriteArrayList<TCComponentUser> localObject4 = projectteamstatus
						.getProjectTeamAdmins();
				if (!(localObject4).isEmpty()) {
					System.out.println("====new abcd localObject4.size()=="
							+ localObject4.size());
					Iterator<TCComponentUser> iterator4 = localObject4
							.iterator();
					while (iterator4.hasNext()) {
						TCComponentUser localObject6 = (TCComponentUser) iterator4
								.next();
						// ProjectLevelSecurity.TeamMemberInfo
						// localTeamMemberInfo = new
						// ProjectLevelSecurity.TeamMemberInfo();
						// localTeamMemberInfo.teamMember = ((TCComponent)
						// localObject3);
						// localTeamMemberInfo.teamMemberType =
						// ProjectConstants.TEAMADMIN;
						if (tccomponentuser == localObject6) {
							System.out
									.println("==== lala tccomponentuser==localObject3==");
							isPTA = true;
							break;
						}
					}
				}
				// isPTA=projectteamstatus.isAProjectTeamAdminMember(tccomponentuser);

				TCComponentUser userProject = (TCComponentUser) project
						.getRelatedComponent("owning_user");

				TCComponentUser projAdministrator = projectteamstatus
						.getProjAdministrator();
				System.out.println("==== lala projAdministrator:"
						+ projAdministrator);
				if (tccomponentuser == projAdministrator
						|| tccomponentuser == userProject) {
					isPA = true;
				}

				// isPA =
				// projectteamstatus.isAProjectAdministrator(tccomponentuser);

				System.out.println("load data isPA:" + isPA + "  isPTA:"
						+ isPTA);

				if (!isPA) {
					// isPTA = teamRoleUserTree.isUserPTA();

					// projAdminPanel.setIsPTA(isPTA);
					nameField.setEnabled(isPA || isPTA);
					idField.setEnabled(isPA || isPTA);
					descField.setEnabled(isPA || isPTA);
					// activeStatusRB.setEnabled(isPA || isPTA);
					// inactiveStatusRB.setEnabled(isPA || isPTA);
					// inactiveInvisibleStatusRB.setEnabled(isPA || isPTA);
					// usePrgmSecCB.setEnabled(isPA || isPTA);

					setXMJCPanelEnabled(false);
					setXMLXPanelEnabled(false);

					System.out
							.println("=ww ==setXMJCPanelEnabled(false)===isPTA:"
									+ isPTA);
					// setXMRQPanelEnabled(false);
				}

				if (isPA) {
					System.out.println("===setXMJCPanelEnabled(true)===");
					setXMJCPanelEnabled(true);
					setXMLXPanelEnabled(true);
					// setXMRQPanelEnabled(true);
				}

				// add by wuh 2015-5-27
				if (isPA) {
					setXMRQPanelEnabled(true);
				} else {
					TCComponentUser tccomponentuser2 = getTCSession().getUser();
					String user_name = tccomponentuser2
							.getProperty("user_name");

					System.out.println("dada activeStatusStr:"
							+ activeStatusStr + "  tlStr:" + tlStr
							+ "  user_name:" + user_name);
					if ("Active".equals(activeStatusStr)
							&& user_name.equals(tlStr)) {
						setXMRQPanelEnabled(true);
					} else {
						setXMRQPanelEnabled(false);
					}

				}

				// if (teamRoleUserTree.isUserPTA()) {
				// setGNPZPanelEnabled(true);
				// } else {
				// setGNPZPanelEnabled(false);
				// }
				if (isPA) {
					setGNPZPanelEnabled(true);
					setPDXPanelEnabled(true);
				} else {
					setGNPZPanelEnabled(false);
				}

				System.out.println("isPA-->" + isPA + "-->isPTA--->" + isPTA);

				if (session.getUser().getUserId()
						.equals(userProject.getUserId())
						&& (project != null && project.okToModify())) {
					System.out.println("===setXMJCPanelEnabled(true)===");
					setXMJCPanelEnabled(true);
					setXMLXPanelEnabled(true);
					// setXMRQPanelEnabled(true);
				} else {
					if (isPA) {// || isPTA
						setXMJCPanelEnabled(true);
						setXMLXPanelEnabled(true);
					} else {
						System.out.println("===setXMJCPanelEnabled(false)===");
						setXMJCPanelEnabled(false);
						setXMLXPanelEnabled(false);
					}
					// setXMRQPanelEnabled(false);
				}

				if (isPTA) {
					setGNPZPanelEnabled(true);
					setXMRQPanelEnabled(true);
				}

				closeDateButton.setEnabled(isPA || isPTA);// add by wuh
															// 2014-5-27
				// eqmRadioButton.setEnabled(isPA);// add 2014-6-5

				myframe.repaint();
				System.out.println("===LOAD data repaint===");
				
//				int res1=m_projectTeamManager.doRefersh1();
//				System.out.println("doRefersh1():: res1-->"+res1);
//				if(res1>0){
//					doWorkRefeshView();
//				}
			}

		} catch (TCException localTCException2) {
			logger.error(localTCException2.getClass().getName(),
					localTCException2);
		}
		
		

	}

	/*
	 * 
	 * 加载更新目录lov
	 */
	public void loadCategoryBox() {
		String text_category = categoryBox.getTextField().getText();
		categoryBox.removeAllItems();
		if (projectStatusBox.getSelectedItem() != null
				&& !"".equals(projectStatusBox.getSelectedItem().toString()
						.trim())) {
			String strSelect = projectStatusBox.getSelectedItem().toString()
					.trim();
			// String values[] = reg.getStringArray(strSelect + "_jci6");
			String ss = registry1.getString(strSelect + "_jci6");
			System.out.println("loadCategoryBox::  " + strSelect + "_jci6-->"
					+ ss);
			String values[] = ss.split(",", -1);
			categoryBox.addItems(values);
		}
		categoryBox.getTextField().setText(text_category);
	}

	/*
	 * 加载更新项目状态lov
	 */
	public void loadProjectStatusBoxData() {
		String text_projectStatus = projectStatusBox.getTextField().getText();
		String text_category = categoryBox.getTextField().getText();

		projectStatusBox.removeAllItems();
		categoryBox.removeAllItems();
		if (activeStatusBox.getSelectedItem() != null
				&& !"".equals(activeStatusBox.getSelectedItem().toString()
						.trim())) {
			String strSelect = activeStatusBox.getSelectedItem().toString()
					.trim();

			// String values[] = reg.getStringArray(strSelect + "_jci6");
			String ss = registry1.getString(strSelect + "_jci6");
			String values[] = ss.split(",", -1);
			System.out.println("loadProjectStatusBoxData::  " + strSelect
					+ "_jci6-->" + ss);

			projectStatusBox.addItems(values);
		}
		projectStatusBox.getTextField().setText(text_projectStatus);
		categoryBox.getTextField().setText(text_category);
	}

	private void getProjectTeamJobPost() {
		System.out.println("=====ww getProjectTeamJobPost()=====");
		m_projectTeamManager.setProject(this.project);
		int cnt=projectTeamMembers.length;
		for(int i=0;i<cnt;i++){
			TCComponent[] projectTeamAdminsArray = projectTeamMembers[i].projectTeamAdmins;
			TCComponent[] privMembersArray= projectTeamMembers[i].privMembers;
			
			System.out.println("projectTeamAdminsArray:"+projectTeamAdminsArray+"  privMembersArray:"+privMembersArray);
			
			if(projectTeamAdminsArray!=null){
				System.out.println("projectTeamAdminsArray.length:"+projectTeamAdminsArray.length);
				for(int a=0;a<projectTeamAdminsArray.length;a++){
					System.out.println("projectTeamAdmins:"+projectTeamAdminsArray[a]);
				}
			}
			
			if(privMembersArray!=null){
				System.out.println("privMembersArray.length:"+privMembersArray.length);
				for(int a=0;a<privMembersArray.length;a++){
					System.out.println("privMembers:"+privMembersArray[a]);
				}
			}
		}
		m_projectTeamManager.setProjectTeamMembers(this.projectTeamMembers);
		m_projectTeamManager.loadProjectTeamData();
		System.out.println("=====ww loadProjectTeamData()=====");

	}

	// modify by wuwei
	private void collectProjectMetaData() {
		/**
		 * this.projectID = this.m_idText.getText().trim(); this.projectName =
		 * this.m_nameText.getText().trim(); this.projectDesc =
		 * this.m_descriptionText.getText().trim();
		 **/
		this.projectID = this.idField.getText().trim();
		this.projectName = this.nameField.getText().trim();
		this.projectDesc = this.descField.getText().trim();

		this.useProgramSec = this.m_ProgramBtn.getSelection();
		System.out.println("m_ProgramBtn.getSelection()-->" + useProgramSec);
		// this.projectStatus = ((this.m_activeStatusBtn.getSelection()) &&
		// (!(this.m_inactiveandInviisibleStatusBtn
		// .getSelection())));

		this.projectActiveStatus = ((this.m_activeStatusBtn.getSelection()) && (!(this.m_inactiveandInviisibleStatusBtn
				.getSelection())));

		System.out.println("m_activeStatusBtn.getSelection()-->"
				+ projectActiveStatus);

		this.projectVisibility = (!(this.m_inactiveandInviisibleStatusBtn
				.getSelection()));
		System.out.println("m_inactiveandInviisibleStatusBtn.getSelection()-->"
				+ projectVisibility);

		this.projectCollaborationCategories = Utilities.getStringForArray(
				Utilities.getArray(this.m_categoryCombo.getSelectedValue()),
				",");
		this.m_parentProject = this.m_parentText.getText().trim();
		System.out.println("m_parentText.getText()-->" + m_parentProject);
	}

	public void clearOperation() {
		if (this.project != null) {
			processSetOriginalInput(this, new ArrayList());
			this.project = null;
			makeContentViewBlank(this, null, true);
		}
		if (m_projectTeamManager != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ProjectDefinitionView.m_projectTeamManager
							.clearProjectTeamSearch(true);
					ProjectDefinitionView.m_projectTeamManager
							.clearSelectedMembers();
					if (ProjectDefinitionView.this.m_activeStatusBtn == null)
						return;
					ProjectDefinitionView.this.m_activeStatusBtn
							.setSelection(true);
				}
			});
			m_projectTeamManager.resetOrgTree();
		}
		this.projectID = "";
		this.projectName = "";
		this.projectDesc = "";
		this.useProgramSec = false;
		this.projectActiveStatus = true;
		this.projectVisibility = true;
		this.project = null;

		/**
		 * if (this.m_idText != null) this.m_idText.setText(""); if
		 * (this.m_nameText != null) this.m_nameText.setText(""); if
		 * (this.m_descriptionText != null) this.m_descriptionText.setText("");
		 */
		if (this.idField != null)
			this.idField.setText("");
		if (this.nameField != null)
			this.nameField.setText("");
		if (this.descField != null)
			this.descField.setText("");

		if (m_categoryCombo != null
				&& this.m_categoryCombo.getSelectedValue() != null)
			this.m_categoryCombo.setSelectedValue("");

		if (this.m_parentText != null)
			this.m_parentText.setText("");
		if (this.m_activeStatusBtn != null)
			this.m_activeStatusBtn.setSelection(true);
		if (this.m_inactiveStatusBtn != null)
			this.m_inactiveStatusBtn.setSelection(false);
		if (this.m_ProgramBtn != null) {
			this.m_ProgramBtn.setEnabled(true);
			this.m_ProgramBtn.setSelection(false);
		}
		if (this.m_ProjectBtn != null)
			this.m_ProjectBtn.setSelection(true);
		if (this.m_inactiveandInviisibleStatusBtn != null)
			this.m_inactiveandInviisibleStatusBtn.setSelection(false);
		if (this.m_teamInheritance != null) {
			this.m_teamInheritance.setSelection(false);
			this.m_teamInheritance.setEnabled(false);
		}
		if (m_projectTeamManager != null)
			m_projectTeamManager.setRoot(null);
		ProjectManager.getInstance().clearAll();
		validateButtons();
	}

	//保留创建的功能检查
	private boolean checkTeamAdminMembership2() {
		 boolean bool1 = true;
		    if (this.m_teamInheritance.getSelection())
		      return bool1;
		    ProjectTeamStatus localProjectTeamStatus = ProjectTeamStatus.getInstance(this.project);
		    if (((localProjectTeamStatus == null) || ((localProjectTeamStatus != null) && (localProjectTeamStatus.getProject() != this.project))) && (m_projectTeamManager.getRoot().getObject() != null) && ((m_projectTeamManager.getRoot().getObject() instanceof ProjectTeamStatus)))
		      localProjectTeamStatus = (ProjectTeamStatus)m_projectTeamManager.getRoot().getObject();
		    TCComponentUser localTCComponentUser = this.session.getUser();
		    TCComponentRole localTCComponentRole = this.session.getRole();
		    TCComponentGroup localTCComponentGroup = this.session.getGroup();
		    if ((m_projectTeamManager.getRoot() != null) && (m_projectTeamManager.getRoot().getObject() != null) && (localProjectTeamStatus != null) && (localProjectTeamStatus.getProjectTeamAdmins() != null) && (!localProjectTeamStatus.getProjectTeamAdmins().isEmpty()))
		      return bool1;
		    if ((localProjectTeamStatus == null) || (localProjectTeamStatus.getProjectTeamAdmins().isEmpty()))
		    {
		      Object[] arrayOfObject = { TCComponentProject.getDisplayName(this.session) };
		      boolean bool2 = MessageDialog.openConfirm(this.m_idText.getShell(), MessageFormat.format(this.reg.getString("confirmPTA.TITLE", "Add Project Team Admin"), arrayOfObject), MessageFormat.format(this.reg.getString("confirmPTA.MESSAGE"), arrayOfObject));
		      if (!bool2)
		        bool1 = false;
		      else
		        try
		        {
		          m_projectTeamManager.addAsAdmin(localTCComponentGroup, localTCComponentRole, localTCComponentUser);
		        }
		        catch (Exception localException)
		        {
		          logger.error(localException.getClass().getName(), localException);
		        }
		    }
		    return bool1;
	}
	
	
	private boolean checkTeamAdminMembership() {
		boolean flag = true;
		if (m_teamInheritance.getSelection())
			return flag;
		ProjectTeamStatus projectteamstatus = ProjectTeamStatus
				.getInstance(project);
		if ((projectteamstatus == null || projectteamstatus != null
				&& projectteamstatus.getProject() != project)
				&& m_projectTeamManager.getRoot().getObject() != null
				&& (m_projectTeamManager.getRoot().getObject() instanceof ProjectTeamStatus))
			projectteamstatus = (ProjectTeamStatus) m_projectTeamManager
					.getRoot().getObject();
		TCComponentUser tccomponentuser = session.getUser();
		TCComponentRole tccomponentrole = session.getRole();
		TCComponentGroup tccomponentgroup = session.getGroup();
		if (m_projectTeamManager.getRoot() != null
				&& m_projectTeamManager.getRoot().getObject() != null
				&& projectteamstatus != null
				&& projectteamstatus.getProjectTeamAdmins() != null
				&& !projectteamstatus.getProjectTeamAdmins().isEmpty())
			return flag;
		
		
		
		//add by wuwei          localProjectTeamStatus.getProjectTeamAdmins()
		Object localObject2 = projectteamstatus.getProjectTeamAdmins();
		//System.out.println("checkTeamAdminMembership projectTeamAdmins.size()--->"+projectTeamAdmins.size());
		
		TCComponentUser teamAdmin = null;
		if (!(((Collection) localObject2).isEmpty())) {
			Iterator localObject4 = ((Collection) localObject2).iterator();
			while (((Iterator) localObject4).hasNext()) {
				TCComponentUser	localObject3 = (TCComponentUser) ((Iterator) localObject4)
						.next();
				
				System.out.println("checkTeamAdminMembership TEAMADMIN teamMember:"
						+ ((TCComponentUser) localObject3));

				teamAdmin=localObject3;
			}
		}
		
		
//		if(projectTeamAdmins.size()==0){
//			List localObject=null;
//			try {
//				localObject = this.project.getTeam();
//			} catch (TCException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			if(localObject!=null){
//				Object object = localObject.get(1);
//				System.out.println("Object-->" + object
//						+ "   Object.getClass():" + object.getClass());
//
//				
//				if (object instanceof TCComponentUser[]) {
//					TCComponentUser[] array = (TCComponentUser[]) object;
//					if (array != null && array.length > 0) {
//						for (int bb = 0; bb < array.length; bb++) {
//							System.out.println("checkTeamAdminMembership array[" + bb + "]-->"
//									+ array[bb]);
//						}
//						teamAdmin = array[0];
//					}
//				}
//				
//				System.out.println("lala checkTeamAdminMembership teamAdmin-->" + teamAdmin);
//				
//			}
//			
//		}
		
		
		
		if (teamAdmin==null && (projectteamstatus == null
				|| projectteamstatus.getProjectTeamAdmins().isEmpty())) {
			Object aobj[] = { TCComponentProject.getDisplayName(session) };
			// ceshi m_idText
			boolean flag1 = MessageDialog.openConfirm(m_headerComposite
					.getShell(),
					MessageFormat.format(reg.getString("confirmPTA.TITLE",
							"Add Project Team Admin"), aobj), MessageFormat
							.format(reg.getString("confirmPTA.MESSAGE"), aobj));
			if (!flag1)
				flag = false;
			else
				try {
					m_projectTeamManager.addAsAdmin(tccomponentgroup,
							tccomponentrole, tccomponentuser);
				} catch (Exception exception) {
					logger.error(exception.getClass().getName(), exception);
				}
		}
		return flag;
	}

	public boolean isValidSelectedObjectForViewPart(
			IWorkbenchPart paramIWorkbenchPart, Object paramObject) {
		return ((AdapterUtil.getAdapter(paramObject, ProjectRootNode.class) != null) || (AdapterUtil
				.getAdapter(paramObject, TCComponentProject.class) != null));
	}

	protected void makeViewBlank(IWorkbenchPart paramIWorkbenchPart,
			List<Object> paramList, boolean paramBoolean) {
		super.makeViewBlank(paramIWorkbenchPart, paramList, paramBoolean);
		validateButtons();
	}

	public String getProjectID() {
		// if(m_idText!=null)
		// return this.m_idText.getText();
		if (idField != null)
			return this.idField.getText();

		return "";
	}

	public String getProjectName() {
		// if(m_nameText!=null)
		// return this.m_nameText.getText();
		if (nameField != null)
			return this.nameField.getText();

		return "";
	}

	public String getProjectDesc() {
		// if(m_descriptionText!=null)
		// return this.m_descriptionText.getText();
		if (descField != null)
			return this.descField.getText();

		return "";
	}

	// modify by wuwei
	public String getProjectCollaborationCategories() {
		String str = "";
		// str = Utilities.getStringForArray(
		// Utilities.getArray(this.m_categoryCombo.getSelectedValue()),
		// ",");
		return str;
	}

	public boolean getProjectStatus() {
		// return this.m_activeStatusBtn.getSelection();
		boolean activeStatusFlag = false;// 激活状态
		String activeStatusValue = getComponentValue(activeStatusBox);
		if (activeStatusValue != null && activeStatusStr != null
				&& !activeStatusValue.equals(activeStatusStr))
			activeStatusFlag = true;

		this.m_activeStatusBtn.setSelection(activeStatusFlag);
		return activeStatusFlag;
	}

	// modify by wuwei
	public boolean getProgramVisibility() {
		// return (!(this.m_inactiveandInviisibleStatusBtn.getSelection()));
		return true;
	}

	public synchronized boolean getProgramSecurity() {
		// return (!(this.m_ProjectBtn.getSelection()));
		return true;
	}

	public String getParentProject() {
		// return this.m_parentText.getText();
		return "";
	}

	private boolean isTeamDefined() {
		return ((m_projectTeamManager != null) && (m_projectTeamManager
				.getRoot() != null));
	}

	boolean tlFlag = false;// 项目负责人
	public synchronized void validateButtons() {
		if (isLoading())
			return;
		tlFlag = false;
		boolean flag = isTeamDefined();
		if (flag && project != null && m_projectTeamManager.getRoot() != null
				&& m_projectTeamManager.getRoot().getObject() != null) {
			ProjectTeamStatus projectteamstatus = (ProjectTeamStatus) m_projectTeamManager
					.getRoot().getObject();
			TCComponentUser tccomponentuser = getTCSession().getUser();

			isPTA = projectteamstatus
					.isAProjectTeamAdminMember(tccomponentuser);

			System.out.println("validateButtons  projectteamstatus-->"
					+ projectteamstatus + "  isPTA:" + isPTA);
		}

		String s = getProjectName();
		String s1 = getProjectID();
		String s2 = getProjectDesc();
		final String s3 = getProjectCollaborationCategories();
		boolean flag1 = getProgramVisibility() ^ projectVisibility;
		boolean flag2 = false;
		if (m_ProjectBtn != null)
			flag2 = getProgramSecurity() ^ useProgramSec;
		boolean flag3 = false;

		System.out.println("validateButtons  m_teamInheritance-->"
				+ m_teamInheritance);
		if (m_teamInheritance != null)
			flag3 = getTeamInheritance() != teamInheritanceValue;

		s = s == null ? null : s.trim();
		s1 = s1 == null ? null : s1.trim();
		s2 = s2 == null ? null : s2.trim();
		boolean flag4 = false;
		if (s1 != null && projectID != null && !s1.equals(projectID))
			flag4 = true;
		boolean flag5 = false;
		if (s != null && projectName != null && !s.equals(projectName))
			flag5 = true;
		boolean flag6 = false;
		if (s2 != null && projectDesc != null && !s2.equals(projectDesc))
			flag6 = true;

		// 新增所有必填项判断创建按钮状态
		// 项目基础
		boolean oemNameFlag = false;// 客户名称
		boolean modeNameFlag = false;
		boolean secFlag = false;// 所属科室
		boolean diviFlag = false;// 所属部门
		
		// add by wuh 2014-5-26
		boolean closeFlag = false;// 关闭日期
		boolean eqmFlag = false;// 是否运行EQM
		boolean targetPosFlag = false;// 目标可能性百分比
		// 项目类型
		boolean activeStatusFlag = false;// 激活状态
		boolean projectStatusFlag = false;// 项目状态
		boolean categoryFlag = false;// 种类
		boolean productFlag = false;// 产品
		boolean typeFlag = false;// 类型
		// 项目日期
		boolean pStartFlag = false;// 项目开始日期
		boolean pEndFlag = false;// 项目结束日期
		boolean lcFlag = false;// 量产日期
		boolean sopfyFlag = false;// 量产年
		boolean modelYearFlag = false;// 车型年
		// 功能配置
		boolean epicFlag = false;// EPIC号
		boolean functionFlag = false;// 功能配置
		boolean frontSSFlag = false;// 前排骨架
		boolean rearSSFlag = false;// 后排骨架
		boolean trackFlag = false;// 滑道
		boolean reclinerFlag = false;// 调角器
		boolean pumpFlag = false;// 手动高调
		boolean vtaFlag = false;// 电动高调
		boolean latchFlag = false;// 锁
		// 5.12-SMTE
		boolean SMTEFlag = false;// SMTE
		// final HashMap<String, String> map=new HashMap<String, String>();

		boolean equFlag = false;// 锁

		String oemNameValue = getComponentValue(oemNameLov);
		if (oemNameValue != null && oemNameStr != null
				&& !oemNameValue.equals(oemNameStr))
			oemNameFlag = true;

		String secValue = getComponentValue(secLov);
		// System.out.println("=========secValue======"+secValue);
		// System.out.println("==========secStr====="+secStr);
		if (secValue != null && secStr != null && !secValue.equals(secStr))
			secFlag = true;
		String diviValue = getComponentValue(diviLov);
		if (diviValue != null && diviStr != null && !diviValue.equals(diviStr))
			diviFlag = true;

		String tlValue = "";
		
		if (tlLov.getSelectedObject() != null
				&& tlLov.getSelectedObject().getClass().toString()
						.indexOf("String") < 0) {

			TCComponentUser tlUser = (TCComponentUser) tlLov
					.getSelectedObject();
			try {
				tlValue = "" + tlUser.getProperty("person");
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("tlLov.getSelectedObject()================"
					+ tlLov.getSelectedObject());
			tlValue = null;
		}
		
		

		if (tlLov.getSelectedObject() != null ){
			TCComponentUser tccomponentuser1 = getTCSession().getUser();
			String currentPerson="";
			try {
				currentPerson = tccomponentuser1.getProperty("person");
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("currentPerson-->"+currentPerson+"  tlLov.getSelectedObject():"+tlLov.getSelectedObject().toString());
			if(currentPerson.equals(tlLov.getSelectedObject().toString())){
				tlFlag = true;
			}
		}
			
		System.out.println("new123 tlFlag-->"+tlFlag);

		// add by wuh 2014-5-26
		// Date closeDateValue = closeDateButton.getDate();
		// if (closeDateValue != null || closeDateStr != null) {
		// if (closeDateValue != null
		// && closeDateStr != null
		// && closeDateValue.toString()
		// .equals(closeDateStr.toString())) {
		// closeFlag = false;
		// } else {
		// closeFlag = true;
		// }
		// }
		String equStr = "";
		TCComponentItem jci6ProgramInfo = null;
		try {
			if (project != null) {
				TCComponent[] childs = this.project
						.getRelatedComponents("TC_Program_Preferred_Items");
				if (childs != null && childs.length > 0) {
					for (int k = 0; k < childs.length; k++) {
						String type = childs[k].getType();
						if ("JCI6_ProgramInfo".equals(type)) {
							jci6ProgramInfo = (TCComponentItem) childs[k];
							continue;
						}
					}
				}
			}

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TCComponentItemRevision projectInfoIR = null;
		if (jci6ProgramInfo != null) {
			try {
				projectInfoIR = jci6ProgramInfo.getLatestItemRevision();
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("jci6_CloseDate---->" + closeFlag);
		// add by wuh 2014-6-5
		// boolean eqmVal = eqmRadioButton.isSelected();
		// if (eqmVal == eqmStr) {
		// eqmFlag = false;
		// } else {
		// eqmFlag = true;
		// }

		String activeStatusValue = getComponentValue(activeStatusBox);
		if (activeStatusValue != null && activeStatusStr != null
				&& !activeStatusValue.equals(activeStatusStr))
			activeStatusFlag = true;

		String projectStatusValue = getComponentValue(projectStatusBox);
		if (projectStatusValue != null && projectStatusStr != null
				&& !projectStatusValue.equals(projectStatusStr))
			projectStatusFlag = true;

		String categoryValue = getComponentValue(categoryBox);
		if (categoryValue != null && categoryStr != null
				&& !categoryValue.equals(categoryStr))
			categoryFlag = true;

		String productValue = getComponentValue(productLov);
		if (productValue != null && productStr != null
				&& !productValue.equals(productStr))
			productFlag = true;

		String typeValue = getComponentValue(typeLov);
		if (typeValue != null && typeStr != null && !typeValue.equals(typeStr))
			typeFlag = true;

		String epicValue = epicTextField.getText();
		if (epicValue != null && epicStr != null && !epicValue.equals(epicStr))
			epicFlag = true;

		String equValue = equTextField.getText();
		if (equValue != null && equStr != null && !equValue.equals(equStr))
			equFlag = true;

		String targetPosValue = "";
		if (targetPosField.getSelectedObject() != null)
			targetPosValue = targetPosField.getSelectedObject().toString();// getComponentValue(targetPosField);

		if (targetPosValue != null
				&& targetPosStr != null
				&& !"".equals(targetPosValue)
				&& !"".equals(targetPosStr)
				&& Double.parseDouble(targetPosValue) != Double
						.parseDouble(targetPosStr)) {
			targetPosFlag = true;
		}

		boolean newCreateFlag = !"".equals(oemNameValue)
				&& oemNameValue.length() > 0
				// && !"".equals(secValue) && secValue.length() > 0
				&& !"".equals(diviValue) && diviValue.length() > 0
				&& !"".equals(activeStatusValue)
				&& activeStatusValue.length() > 0
				&& !"".equals(projectStatusValue)
				&& projectStatusValue.length() > 0 && !"".equals(categoryValue)
				&& categoryValue.length() > 0 && !"".equals(productValue)
				&& productValue.length() > 0 && !"".equals(typeValue)
				&& typeValue.length() > 0 && epicValue != null
				&& epicValue.length() > 0 && equValue != null
				&& equValue.length() > 0;

		if (typePanel2 != null && typePanel2.isVisible()) {
			newCreateFlag = newCreateFlag && !"".equals(targetPosValue)
					&& targetPosValue.length() > 0;
		}

		// 新增判断所有可编辑属性是否变化，判断修改按钮状态，不包括PDX只读页，因为此页只读没有修改可能
		// Date pStartValue = pStartDate.getDate();
		// if (pStartValue != null || pStartStr != null) {
		//
		// if (pStartValue != null && pStartStr != null
		// && pStartValue.toString().equals(pStartStr.toString())) {
		// pStartFlag = false;
		// } else {
		// pStartFlag = true;
		// }
		// }

		// Date pEndValue = pEndDate.getDate();
		// if (pEndValue != null || pEndStr != null) {
		//
		// if (pEndValue != null && pEndStr != null
		// && pEndValue.toString().equals(pEndStr.toString())) {
		// pEndFlag = false;
		// } else {
		// pEndFlag = true;
		// }
		// }

		// Date lcValue = lcDate.getDate();
		// if (lcValue != null || lcStr != null) {
		// if (lcValue != null && lcStr != null
		// && lcValue.toString().equals(lcStr.toString())) {
		// lcFlag = false;
		// } else {
		// lcFlag = true;
		// }
		// }
		String sopfyValue = getComponentValue(sopfyLov);

		if ("".equals(sopfyValue))
			sopfyValue = "0";
		if (sopfyValue != null && sopfyStr != null
				&& !sopfyValue.equals(sopfyStr))
			sopfyFlag = true;
		String modelYearValue = getComponentValue(modelYearLov);
		if ("".equals(modelYearValue))
			modelYearValue = "0";
		if (modelYearValue != null && modelYearStr != null
				&& !modelYearValue.equals(modelYearStr))
			modelYearFlag = true;
		String functionValue = functionTextField.getText();
		if (functionValue != null && functionStr != null
				&& !functionValue.equals(functionStr))
			functionFlag = true;
		String frontSSValue = getComponentValue(frontSSLov);
		if (frontSSValue != null && frontSSStr != null
				&& !frontSSValue.equals(frontSSStr))
			frontSSFlag = true;
		String rearSSValue = getComponentValue(rearSSLov);
		if (rearSSValue != null && rearSSStr != null
				&& !rearSSValue.equals(rearSSStr))
			rearSSFlag = true;

		TCProperty SMTE_Property2 = null;
		if (projectInfoIR != null) {
			try {
				SMTE_Property2 = projectInfoIR
						.getTCProperty("jci6_SMTE_Variables");
				// 8.22--修改
				// 5.12-SMTE
				SMTEFlag = SMTE_lov.isPropertyModified(SMTE_Property2);
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.out.println("===============SMTEFlag=================="
				+ SMTEFlag);

		String trackValue = getComponentValue(trackLov);
		if (trackValue != null && trackStr != null
				&& !trackValue.equals(trackStr))
			trackFlag = true;
		String reclinerValue = getComponentValue(reclinerLov);
		if (reclinerValue != null && reclinerStr != null
				&& !reclinerValue.equals(reclinerStr))
			reclinerFlag = true;
		String pumpValue = getComponentValue(pumpLov);
		if (pumpValue != null && pumpStr != null && !pumpValue.equals(pumpStr))
			pumpFlag = true;
		String vtaValue = getComponentValue(vtaLov);
		if (vtaValue != null && vtaStr != null && !vtaValue.equals(vtaStr))
			vtaFlag = true;
		String latchValue = getComponentValue(latchLov);
		if (latchValue != null && latchStr != null
				&& !latchValue.equals(latchStr))
			latchFlag = true;

		String modeNameValue = modeNameField.getText();
		if (modeNameValue != null && modeNameStr != null
				&& !modeNameValue.equals(modeNameStr))
			modeNameFlag = true;

		boolean flag7 = false;
		if (!s3.equalsIgnoreCase(projectCollaborationCategories))
			flag7 = true;

		// modify by wuh
		boolean newModifyFlag = oemNameFlag || secFlag || diviFlag || tlFlag
				|| closeFlag || eqmFlag || targetPosFlag || activeStatusFlag
				|| projectStatusFlag || categoryFlag || productFlag || typeFlag
				|| pStartFlag || pEndFlag || lcFlag || sopfyFlag
				|| modelYearFlag || epicFlag || functionFlag || frontSSFlag
				|| rearSSFlag || trackFlag || reclinerFlag || pumpFlag
				|| vtaFlag || latchFlag || equFlag || modeNameFlag || SMTEFlag;

		// System.out.println("====newModefyFlag:"+newModifyFlag);
		// System.out.println("====newModefyFlag:"+oemNameFlag +"="+ secFlag
		// +"="+ diviFlag +"="+ tlFlag +"="+ targetPosFlag +"="+
		// activeStatusFlag +"="+
		// projectStatusFlag +"="+ categoryFlag +"="+ productFlag +"="+ typeFlag
		// +"="+ pStartFlag +"="+ pEndFlag +"="+ lcFlag +"="+ sopfyFlag +"="+
		// modelYearFlag +"="+ epicFlag +"="+ functionFlag +"="+ frontSSFlag
		// +"="+ rearSSFlag +"="+ trackFlag +"="+ reclinerFlag +"="+ pumpFlag
		// +"="+
		// vtaFlag +"="+ latchFlag +"="+ equFlag+"="+modeNameFlag);

		System.out.println("isPA-->" + isPA);
		System.out.println("s---->" + s + "  s1:" + s1 + "  flag:" + flag
				+ "  flag3:" + flag3);
		System.out.println("project---->" + project + "  flag4:" + flag4
				+ "  flag5:" + flag5);

		final boolean flag123 = (isPA && s != null && s.length() > 0
				&& s1 != null && s1.length() > 0 && (flag ) && (project == null || flag4
				&& flag5));

		System.out.println("flag123---->" + flag123);

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				// m_createButton.setEnabled(isPA && s != null && s.length() > 0
				// && s1 != null && s1.length() > 0 && (flag || flag3)
				// && (project == null || flag4 && flag5));

				System.out.println("m_createButton.setEnabled---->" + flag123);
				m_createButton.setEnabled(flag123);

			}
		});

		if (project != null) {
			// modify by wuwei
			// boolean flag8 = m_activeStatusBtn.getSelection() ^ projectStatus;
			// boolean flag8 = m_activeStatusBtn.getSelection()
			// ^projectActiveStatus;
			boolean flag8 = projectActiveStatus;
			System.out.println("projectActiveStatus:" + projectActiveStatus);

			boolean flag10 = m_projectTeamManager.teamModified();
			if (teamInheritanceValue == 1 && !flag3)
				flag10 = false;
			boolean flag11 = isPA && isPTA();
			if (isPTA() && !isPA) {
				if (flag10)
					flag11 = true;
				if (flag5 || flag4 || flag8 || flag6 || flag1 || flag2 || flag7)
					flag11 = false;
			}
			if (isPTA() && !isPA) {
				if (flag10)
					flag11 = true;
				if (flag5 || flag4 || flag8 || flag6 || flag1 || flag2)
					flag11 = false;
			}

			final boolean flagB = flag11
					&& flag
					&& (flag5 || flag4 || flag8 || flag6 || flag10 || flag1
							|| flag2 || flag7) || flag3 || newModifyFlag;
			Display.getDefault().syncExec(new Runnable() {
				public void run() {

					System.out
							.println("m_modifyButton.setEnabled---->" + flagB+"  ,tlFlag-->"+tlFlag);
					if(flagB==true&&flag123==false){
						TCComponentUser tccomponentuser1 = getTCSession().getUser();
						try {
							String s4 = project.getProperty("owning_user");
							if (s4 != null && tccomponentuser1 != null
									&& tccomponentuser1.toString().contains(s4)){
								m_modifyButton.setEnabled(true);
							}else{
								if(tlFlag){
									m_modifyButton.setEnabled(true);
								}else{
									m_modifyButton.setEnabled(false);
								}
									
							}
						} catch (TCException tcexception) {
							logger.error(tcexception.getClass().getName(), tcexception);
						}
					}else{
						m_modifyButton.setEnabled(flagB);
					}
					
				}
			});

		} else {
			// m_modifyButton.setEnabled(false);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					System.out
							.println("m_modifyButton.setEnabled---->" + false);
					m_modifyButton.setEnabled(false);
				}
			});
		}

		if (project != null) {
			final boolean flagC = isPA && s != null && s.length() > 0
					&& s1 != null && s1.length() > 0 && flag && flag4 && flag5;

			// m_copyButton.setEnabled(isPA && s != null && s.length() > 0
			// && s1 != null && s1.length() > 0 && flag && flag4 && flag5);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					System.out.println("m_copyButton.setEnabled---->" + flagC);
					m_copyButton.setEnabled(flagC);
				}
			});

		} else {
			// m_copyButton.setEnabled(false);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					System.out.println("m_copyButton.setEnabled---->" + false);
					m_copyButton.setEnabled(false);
				}
			});
		}
		
		if (m_deleteButton != null) {
			boolean flag9 = false;
			if (project != null) {
				TCComponentUser tccomponentuser1 = getTCSession().getUser();
				try {
					String s4 = project.getProperty("owning_user");
					if (s4 != null && tccomponentuser1 != null
							&& tccomponentuser1.toString().contains(s4))
						flag9 = true;
				} catch (TCException tcexception) {
					logger.error(tcexception.getClass().getName(), tcexception);
				}
			}

			final boolean flagD = isPA && project != null && flag9;
			// m_deleteButton.setEnabled(isPA && project != null && flag9);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					System.out
							.println("m_deleteButton.setEnabled---->" + flagD);
					m_deleteButton.setEnabled(flagD);
				}
			});
		}
	}

	private int getTeamInheritance() {
		// return ((this.m_teamInheritance.getSelection()) ? 1 : 0);
		return 0;
	}

	public boolean isTeamInherited() {
		// return this.m_teamInheritance.getSelection();
		// 可能造成按钮变灰
		return false;
	}

	public boolean isPTA() {
		System.out.println("check start isPTA()-->" + isPTA);
		if (this.isPTA)
			return true;
		TCComponentUser localTCComponentUser = getTCSession().getUser();
		if (this.project != null) {
			try {
				String str = this.project.getProperty("owning_user");
				if ((str != null)
						&& (localTCComponentUser != null)
						&& (localTCComponentUser.toString()
								.equalsIgnoreCase(str))) {
					System.out.println("check end isPTA()-->true");
					return true;
				}

			} catch (TCException localTCException) {
				logger.error(localTCException.getClass().getName(),
						localTCException);
			}
		}
		try {
			if ((m_projectTeamManager.projectTeamMembers_preModify != null)
					&& (m_projectTeamManager.projectTeamMembers_preModify[0] != null))
				for (Object localObject : m_projectTeamManager.projectTeamMembers_preModify[0].projectTeamAdmins) {
					if (localObject == localTCComponentUser) {
						System.out.println("check end isPTA()-->true");
						return true;
					}

				}

		} catch (Exception localException) {
			logger.error(localException.getClass().getName(), localException);
		}

		System.out.println("check end isPTA()-->false");
		return false;
	}

	private TCComponentUser TeamUser=null;
	private synchronized ArrayList<ProjectLevelSecurity.TeamMemberInfo> getSelectedMembersHelper(TCComponentProject localTCComponentProject,TCComponentUser sessionUser) {
		ProjectTeamStatus localProjectTeamStatus = null;
		if(localTCComponentProject==null){
			localProjectTeamStatus = getPTM(localProjectTeamStatus);
		}else{
			localProjectTeamStatus = ProjectTeamStatus
					.getInstance(localTCComponentProject);
		}
		teamMembers.clear();
		teamAdmins.clear();
		allPrivUsers.clear();
		TeamUser=null;
		
		System.out
				.println("getSelectedMembersHelper::localProjectTeamStatus-->"
						+ localProjectTeamStatus);

		ArrayList localArrayList = new ArrayList();
		if (localProjectTeamStatus != null) {
			//TCComponentProject localTCComponentProject = localProjectTeamStatus
			//		.getProject();
			String str = (localTCComponentProject == null) ? "CONSTANT_NEW_PROJECT"
					: localTCComponentProject.toDisplayString();
			System.out.println("getSelectedMembersHelper:: modifying project: "
					+ str);
			CopyOnWriteArrayList localCopyOnWriteArrayList = localProjectTeamStatus
					.getProjectMembers();
			Object localObject3;
			if (!(localCopyOnWriteArrayList.isEmpty())) {
				Iterator localObject2 = localCopyOnWriteArrayList.iterator();
				while (((Iterator) localObject2).hasNext()) {
					TCComponent localObject1 = (TCComponent) ((Iterator) localObject2)
							.next();
					localObject3 = new ProjectLevelSecurity.TeamMemberInfo();
					((ProjectLevelSecurity.TeamMemberInfo) localObject3).teamMember = ((TCComponent) localObject1);
					((ProjectLevelSecurity.TeamMemberInfo) localObject3).teamMemberType = ProjectConstants.UNPRIVILEGED;//PRIVILEGED
					System.out.println("UNPRIVILEGED teamMember:"
							+ ((TCComponent) localObject1));
					teamMembers.add(localObject1);
					localArrayList.add(localObject3);
				}
			}
			logger.info(localCopyOnWriteArrayList.size()
					+ " project members are collected");
			Object localObject1 = localProjectTeamStatus.getPrivilegedMembers();
			Object localObject4;
			if (!(((Collection) localObject1).isEmpty())) {
				localObject3 = ((Collection) localObject1).iterator();
				while (((Iterator) localObject3).hasNext()) {
					TCComponentUser localObject2 = (TCComponentUser) ((Iterator) localObject3)
							.next();
					localObject4 = new ProjectLevelSecurity.TeamMemberInfo();
					((ProjectLevelSecurity.TeamMemberInfo) localObject4).teamMember = ((TCComponent) localObject2);
					((ProjectLevelSecurity.TeamMemberInfo) localObject4).teamMemberType = ProjectConstants.PRIVILEGED;
					System.out.println("PRIVILEGED teamMember:"
							+ ((TCComponent) localObject2));
					allPrivUsers.add(localObject2);
					localArrayList.add(localObject4);
				}
			}

			logger.info(((Collection) localObject1).size()
					+ " privileged users are collected");
			Object localObject2 = localProjectTeamStatus.getProjectTeamAdmins();
			if (!(((Collection) localObject2).isEmpty())) {
				localObject4 = ((Collection) localObject2).iterator();
				while (((Iterator) localObject4).hasNext()) {
					localObject3 = (TCComponentUser) ((Iterator) localObject4)
							.next();
					ProjectLevelSecurity.TeamMemberInfo localTeamMemberInfo = new ProjectLevelSecurity.TeamMemberInfo();
					localTeamMemberInfo.teamMember = ((TCComponent) localObject3);
					localTeamMemberInfo.teamMemberType = ProjectConstants.TEAMADMIN;
					System.out.println("getSelectedMembersHelper:: TEAMADMIN teamMember:"
							+ ((TCComponent) localObject3));
					TeamUser=(TCComponentUser)localObject3;
					teamAdmins.add((TCComponentUser)localObject3);
					localArrayList.add(localTeamMemberInfo);
				}
			}
			logger.info(((Collection) localObject2).size()
					+ " PTA users are collected");
			
			if(sessionUser!=null){
				ProjectLevelSecurity.TeamMemberInfo localTeamMemberInfo = new ProjectLevelSecurity.TeamMemberInfo();
				localTeamMemberInfo.teamMember = ((TCComponent) sessionUser);
				localTeamMemberInfo.teamMemberType = ProjectConstants.TEAMADMIN;
				System.out.println("getSelectedMembersHelper:: TEAMADMIN teamMember:"
						+ ((TCComponent) sessionUser));
				localArrayList.add(localTeamMemberInfo);
			}
			
		}
		localArrayList.trimToSize();
		return ((ArrayList<ProjectLevelSecurity.TeamMemberInfo>) (ArrayList<ProjectLevelSecurity.TeamMemberInfo>) (ArrayList<ProjectLevelSecurity.TeamMemberInfo>) (ArrayList<ProjectLevelSecurity.TeamMemberInfo>) localArrayList);
	}

	private ProjectTeamStatus getPTM(ProjectTeamStatus paramProjectTeamStatus) {
		if ((m_projectTeamManager.getRoot() != null)
				&& (m_projectTeamManager.getRoot().getObject() != null)
				&& (m_projectTeamManager.getRoot().getObject() instanceof ProjectTeamStatus))
			paramProjectTeamStatus = (ProjectTeamStatus) m_projectTeamManager
					.getRoot().getObject();
		return paramProjectTeamStatus;
	}

	private TreeSet<ProjectTeamContentNode> getPTMChildren() {
		TreeSet localTreeSet;
		if ((m_projectTeamManager.getRoot() != null)
				&& (m_projectTeamManager.getRoot().getChildNodes() != null))
			localTreeSet = m_projectTeamManager.getRoot().getChildNodes();
		else
			localTreeSet = null;
		return localTreeSet;
	}

	@Deprecated
	public Text getIdText() {
		return this.m_idText;
	}

	@Deprecated
	public Text getNameText() {
		return this.m_nameText;
	}

	@Deprecated
	public Text getDescriptionText() {
		return this.m_descriptionText;
	}

	@Deprecated
	public Button getActiveStatusBtn() {
		return this.m_activeStatusBtn;
	}

	@Deprecated
	public Button getCreateButton() {
		return this.m_createButton;
	}

	public ProjectTeamTreeManager getProjectTeamManager() {
		return m_projectTeamManager;
	}

	public TCComponentProject getProject() {
		return this.project;
	}

	private void loadPostActionExtension() {
		IExtensionPoint localIExtensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(
						"com.teamcenter.rac.project.ProjectUIPostActions");
		System.out.println("localIExtensionPoint:" + localIExtensionPoint);
		System.out.println("Label:" + localIExtensionPoint.getLabel()
				+ "  Identifier:"
				+ localIExtensionPoint.getNamespaceIdentifier()
				+ "  UniqueIdenti:"
				+ localIExtensionPoint.getUniqueIdentifier());
		if (localIExtensionPoint != null) {
			IExtension[] arrayOfIExtension = localIExtensionPoint
					.getExtensions();
			System.out.println("arrayOfIExtension:" + arrayOfIExtension);
			System.out.println("arrayOfIExtension.length-->"
					+ arrayOfIExtension.length);
			if ((arrayOfIExtension != null) && (arrayOfIExtension.length > 0)) {
				System.out.println("localIExtensionPoint.length:"
						+ arrayOfIExtension.length);
				IConfigurationElement[] arrayOfIConfigurationElement = arrayOfIExtension[0]
						.getConfigurationElements();
				System.out.println("lala arrayOfIConfigurationElement-->"
						+ arrayOfIConfigurationElement);
				if (arrayOfIConfigurationElement != null) {
					try {
						this.projectUIPostActions = ((ProjectUIPostActions) arrayOfIConfigurationElement[0]
								.createExecutableExtension("class"));
					} catch (CoreException localCoreException) {
						Logger.getLogger(ProjectDefinitionView.class).error(
								localCoreException.getLocalizedMessage(),
								localCoreException);
					}
				} else {
					System.out
							.println("loadPostActionExtension() projectUIPostActions-->"
									+ projectUIPostActions);
				}
			}
		}
	}

	protected void postCreateContent() {
		pDefView = this;
	}

	public static ProjectDefinitionView getProjectDefViewInstance() {
		return pDefView;
	}

	public void setParentProgram(String paramString) {
		this.m_parentText.setText(paramString);
	}

	public void setProjectButton() {
		this.m_ProjectBtn.setSelection(true);
		this.m_ProgramBtn.setSelection(false);
		this.m_ProgramBtn.setEnabled(false);
		this.m_teamInheritance.setEnabled(true);
	}

	public void setSelectItemRevision(TCComponentItemRevision revision) {
		selectItemRevision = revision;
	}

	public TCComponentItemRevision getSelectItemRevision() {
		return selectItemRevision;
	}

	/*
	 * 
	 * 获得下拉类控件值方法
	 */
	public String getComponentValue(Component obj) {
		if (obj.getClass().toString().indexOf("PropertyLOVUIComponent") > 0) {
			PropertyLOVUIComponent lov = (PropertyLOVUIComponent) obj;
			if (lov.getSelectedObject() != null) {
				return lov.getSelectedObject().toString().trim();
			}
		} else if (obj.getClass().toString().indexOf("iComboBox") > 0) {
			iComboBox box = (iComboBox) obj;
			if (box.getSelectedItem() != null)
				return box.getSelectedItem().toString().trim();
			else
				return box.getTextField().getText();
		} else if (obj.getClass().toString().indexOf("LOVComboBox") > 0) {
			LOVComboBox box = (LOVComboBox) obj;
			if (box.getSelectedItem() != null) {
				return box.getSelectedItem().toString().trim();
			} else {
				return box.getTextField().getText();
			}
		}

		return "";
	}

	/*
	 * 
	 * 清除所有控件值
	 */
	public void clearComponentValue() {

		epicTextField.setText("TBD");
		oemNameLov.setSelectedItem(null);
		modeNameField.setText(null);
		secLov.setSelectedItem(null);
		diviLov.setSelectedItem(null);
		tlLov.setSelectedValue(null);
		// add by wuh 2014-5-26
		createDateButton.setDate((Date) null);
		closeDateButton.setDate((Date) null);
		// eqmRadioButton.setSelected(false);

		targetPosField.setText(null);
		activeStatusBox.setSelectedItem(null);
		projectStatusBox.setSelectedItem(null);
		categoryBox.setSelectedItem(null);
		productLov.setSelectedItem(null);
		typeLov.setSelectedItem(null);
		pStartDate.setDate((Date) null);
		pEndDate.setDate((Date) null);
		lcDate.setDate((Date) null);
		sopfyLov.setSelectedItem(null);
		modelYearLov.setSelectedItem(null);
		functionTextField.setText(null);
		frontSSLov.setSelectedItem(null);
		rearSSLov.setSelectedItem(null);
		trackLov.setSelectedItem(null);
		reclinerLov.setSelectedItem(null);
		pumpLov.setSelectedItem(null);
		vtaLov.setSelectedItem(null);
		latchLov.setSelectedItem(null);
		pxdLov.setSelectedItem(null);
		gebtLov.setSelectedItem(null);
		equTextField.setText("0");
		// equSignDate.setDate((Date) null);
		// calcDate.setDate((Date) null);
		pdxSignDate.setDate((Date) null);

		// 5.6修改
		latestComboLov.setSelectedItem(null);
		remarkField.setText("");
	}

	/*
	 * 
	 * 修改项目信息属性
	 */
	public void setProjectInfoProperties(TCComponentItem projectInfoItem) {

		System.out.println("new----按了Modify按钮后修改项目信息开始！！！----");

		TCComponentItemRevision projectInfoIR = null;
		try {
			if (this.getSelectItemRevision() == null) {
				projectInfoIR = projectInfoItem.getLatestItemRevision();
			} else {
				projectInfoIR = this.getSelectItemRevision();
				System.out.println("修改当前版本============================！！！");
			}

			String propProInfoNames[] = { "jci6_OEMName", "jci6_ProgramSec",
					"jci6_ProgramDivi", "jci6_ProjectTL", "jci6_TargetPos",
					"jci6_ActiveStatus", "jci6_ProgramState", "jci6_Category",
					"jci6_Product", "jci6_Type", "jci6_StartDate",
					"jci6_EndDate", "jci6_SOP",
					"jci6_SOPFY",
					"jci6_ModelYear",// "jci6_EPIC_PN",
					"jci6_Function", "jci6_FrontSS", "jci6_RearSS",
					"jci6_Track", "jci6_Recliner", "jci6_PUMP", "jci6_VTA",
					"jci6_Latch", "jci6_EQU", "jci6_OEMModelName"
			// "jci6_SMTE_Variables"
			};
			// TCComponentItemRevisionType tcComponentItemRevisionType =
			// (TCComponentItemRevisionType)
			// session.getTypeComponent("JCI6_ProgramInfoRevision");
			TCProperty[] tcProperty = projectInfoIR
					.getTCProperties(propProInfoNames);
			Vector vector = new Vector();
			String oemNameValue = getComponentValue(oemNameLov);
			String modeNameValue = modeNameField.getText();
			String secValue = getComponentValue(secLov);
			String diviValue = getComponentValue(diviLov);
			String tlValue = getComponentValue(tlLov);
			String targetPosValue = getComponentValue(targetPosField);
			// targetPosField.getSelectedValue().toString();
			// getComponentValue(targetPosField);

			// PDX信息默认只读
			String equ = equTextField.getText();
			if ("".equals(equ))
				equ = "0";

			if (!oemNameStr.equals(oemNameValue)) {
				vector.add(tcProperty[0]);
				tcProperty[0].setStringValueData(oemNameValue);
			}
			if (!secStr.equals(secValue)) {
				if (secValue != null && !"".equals(secValue)) {

					TCComponentGroupType groupType = (TCComponentGroupType) session
							.getTypeComponent("Group");
					TCComponentGroup secGroup = groupType.find(secValue);
					vector.add(tcProperty[1]);
					tcProperty[1].setReferenceValueData(secGroup);
				}
			}
			if (!diviStr.equals(diviValue)) {
				if (diviValue != null && !"".equals(diviValue)) {
					TCComponentGroupType groupType = (TCComponentGroupType) session
							.getTypeComponent("Group");
					TCComponentGroup diciGroup = groupType.find(diviValue);
					vector.add(tcProperty[2]);
					tcProperty[2].setReferenceValueData(diciGroup);
				}
			}

			// if (tlLov.getSelectedObject() != null) {
			// System.out.println("----------------tlLov有值！！！======="
			// + tlLov.getSelectedObject().getClass().toString());
			//
			// if (tlLov.getSelectedObject().getClass().toString()
			// .indexOf("String") > 0) {
			//
			// System.out.println("----------------tlLov的值为======="
			// + tlLov.getSelectedObject().getClass().toString());
			//
			// TCComponentUserType userTL = (TCComponentUserType) session
			// .getTypeComponent("User");
			//
			// System.out.println("---tlValue---------------------"
			// + tlValue);
			//
			// if (tlValue != null && !"".equals(tlValue)) {
			// if (tlValue.indexOf("\\(") > 0) {
			// String strUser = tlValue.replaceAll("\\(", ";")
			// .replaceAll("\\)", "").split(";")[1].trim();
			//
			// System.out.println("---------strUser===" + strUser);
			//
			// TCComponentUser user = userTL.find(strUser);
			//
			// System.out.println("-------------user===="
			// + user.getOSUserName());
			//
			// tcProperty[3].setReferenceValueData(user);
			// } else {
			// TCComponent[] users = query(session, QUERY_NAME,
			// new String[] { "PersonName" },
			// new String[] { tlValue });
			// tcProperty[3].setReferenceValueData(users[0]);
			//
			// System.out
			// .println("--------------设置完tlLov的user-通过查询找到！！！-----");
			// }
			//
			// }
			// } else {
			// TCComponentUser tlUser = (TCComponentUser) tlLov
			// .getSelectedObject();
			// if (tlUser != null) {
			// tcProperty[3].setReferenceValueData(tlUser);
			// }
			//
			// System.out.println("--------------直接设置完tlLov的值！！！-----");
			// }
			// vector.add(tcProperty[3]);
			// }

			// modify by wuwei
			List localObject = this.project.getTeam();
			Object object = localObject.get(1);
			System.out.println("Object-->" + object + "   Object.getClass():"
					+ object.getClass());

			TCComponentUser teamAdmin = null;
			if (object instanceof TCComponentUser[]) {
				TCComponentUser[] array = (TCComponentUser[]) object;
				if (array != null && array.length > 0) {
					for (int bb = 0; bb < array.length; bb++) {
						System.out.println("array[" + bb + "]-->" + array[bb]);
					}
					teamAdmin = array[0];
				}
			}
			System.out.println("lala teamAdmin-->" + teamAdmin);

			// modify by wuwei
			// 8.22--修改
			tcProperty[3].setReferenceValueData((TCComponent) teamAdmin);
			vector.add(tcProperty[3]);

			System.out.println(teamAdmin
					+ "--------修改 ---Project Team Leader  完成！！========="
					+ tlValue);

			// add　by wuh 2014-5-26
			boolean close_flag = false;

			// modify by wuwei
			// Date closeDateValue = closeDateButton.getDate();
			// TCProperty closeDatePro = null;
			// if (closeDateStr == null && closeDateValue != null) {
			// System.out.println("null---->not null");
			// userService.call("open_or_close_pass", new Object[] { 1 });
			// projectInfoItem.setDateProperty("jci6_CloseDate",
			// closeDateValue);
			// userService.call("open_or_close_pass", new Object[] { 0 });
			// close_flag = true;
			// } else if (closeDateStr != null && closeDateValue != null
			// && !closeDateStr.equals(closeDateValue)) {
			// System.out.println("not null--->not equal");
			// userService.call("open_or_close_pass", new Object[] { 1 });
			// projectInfoItem.setDateProperty("jci6_CloseDate",
			// closeDateValue);
			// userService.call("open_or_close_pass", new Object[] { 0 });
			// close_flag = true;
			// } else if (closeDateStr != null && closeDateValue == null) {
			// userService.call("open_or_close_pass", new Object[] { 1 });
			// projectInfoItem.setDateProperty("jci6_CloseDate",
			// closeDateValue);
			// userService.call("open_or_close_pass", new Object[] { 0 });
			// close_flag = true;
			// }

			// add by wuh 2014-6-5
			boolean eqm_flag = false;
			// boolean eqmVal = eqmRadioButton.isSelected();
			// if (eqmStr == eqmVal) {
			// System.out.println("EQM not change");
			// } else {
			// System.out.println("EQM changed");
			// userService.call("open_or_close_pass", new Object[] { 1 });
			// projectInfoItem.setLogicalProperty("jci6_IsRunEQM", eqmVal);
			// userService.call("open_or_close_pass", new Object[] { 0 });
			// eqm_flag = true;
			// }

			if (close_flag && eqm_flag) {
				// 刷新
				projectInfoItem.refresh();
				closeDateStr = projectInfoItem.getTCProperty("jci6_CloseDate")
						.getDateValue();
				// System.out.println();
				// eqmStr = projectInfoItem.getTCProperty("jci6_IsRunEQM")
				// .getLogicalValue();
			} else if (close_flag && !eqm_flag) {
				// 刷新
				projectInfoItem.refresh();
				closeDateStr = projectInfoItem.getTCProperty("jci6_CloseDate")
						.getDateValue();
			} else if (eqm_flag && !close_flag) {
				// projectInfoItem.refresh();
				// eqmStr = projectInfoItem.getTCProperty("jci6_IsRunEQM")
				// .getLogicalValue();
			}

			if (!targetPosStr.equals(targetPosValue)
					&& !"".equals(targetPosValue)) {
				vector.add(tcProperty[4]);
				tcProperty[4].setDoubleValueData(Double
						.parseDouble(targetPosValue));
			}
			String activeStatusValue = getComponentValue(activeStatusBox);
			String projectStatusValue = getComponentValue(projectStatusBox);
			String categoryValue = getComponentValue(categoryBox);
			String productValue = getComponentValue(productLov);
			String typeValue = getComponentValue(typeLov);
			if (!activeStatusStr.equals(activeStatusValue)) {
				vector.add(tcProperty[5]);
				if ("Active".equals(activeStatusValue)) {
					userService.call("open_or_close_pass", new Object[] { 1 });
					tcProperty[5].setLogicalValueData(true);
					userService.call("open_or_close_pass", new Object[] { 0 });
					System.out
							.println("--------------直接设置完Active Status的值为-----TRUE！！！！");
				} else {
					userService.call("open_or_close_pass", new Object[] { 1 });
					tcProperty[5].setLogicalValueData(false);
					userService.call("open_or_close_pass", new Object[] { 0 });
					System.out
							.println("--------------直接设置完Active Status的值为-----false！！！！");
				}
			}

			System.out.println("old projectStatusStr:" + projectStatusStr
					+ "  projectStatusValue:" + projectStatusValue);
			if (!projectStatusStr.equals(projectStatusValue)) {
				vector.add(tcProperty[6]);
				tcProperty[6].setStringValueData(projectStatusValue);
			}
			if (!categoryStr.equals(categoryValue)) {
				vector.add(tcProperty[7]);
				tcProperty[7].setStringValueData(categoryValue);
			}
			if (!productStr.equals(productValue)) {
				vector.add(tcProperty[8]);
				tcProperty[8].setStringValueData(productValue);
			}
			if (!typeStr.equals(typeValue)) {
				vector.add(tcProperty[9]);
				tcProperty[9].setStringValueData(typeValue);
			}

			Date pStartValueDate = pStartDate.getDate();
			Date pEndValueDate = pEndDate.getDate();
			Date lcValueDate = lcDate.getDate();
			String sopfyValue = getComponentValue(sopfyLov);
			if (sopfyValue.equals(""))
				sopfyValue = "0";

			String modelYearValue = getComponentValue(modelYearLov);
			if (modelYearValue.equals(""))
				modelYearValue = "0";

			// modify by wuwei
			// if (pStartValueDate != null && pStartStr != null
			// && !pStartValueDate.toString().equals(pStartStr.toString())) {
			// vector.add(tcProperty[10]);
			// tcProperty[10].setDateValueData(pStartValueDate);
			// } else if (pStartValueDate == null && pStartStr != null) {
			// vector.add(tcProperty[10]);
			// tcProperty[10].setDateValueData(pStartValueDate);
			// } else if (pStartValueDate != null && pStartStr == null) {
			// vector.add(tcProperty[10]);
			// tcProperty[10].setDateValueData(pStartValueDate);
			// }

			vector.add(tcProperty[10]);
			tcProperty[10].setDateValueData(pStartValueDate);

			// if (pEndValueDate != null && pEndStr != null
			// && !pEndValueDate.toString().equals(pEndStr.toString())) {
			// vector.add(tcProperty[11]);
			// tcProperty[11].setDateValueData(pEndValueDate);
			// } else if (pEndValueDate == null && pEndStr != null) {
			// vector.add(tcProperty[11]);
			// tcProperty[11].setDateValueData(pEndValueDate);
			// } else if (pEndValueDate != null && pEndStr == null) {
			// vector.add(tcProperty[11]);
			// tcProperty[11].setDateValueData(pEndValueDate);
			// }

			vector.add(tcProperty[11]);
			tcProperty[11].setDateValueData(pEndValueDate);

			// if (lcValueDate != null && lcStr != null
			// && !lcValueDate.toString().equals(lcStr.toString())) {
			// vector.add(tcProperty[12]);
			// tcProperty[12].setDateValueData(lcValueDate);
			// } else if (lcValueDate == null && lcStr != null) {
			// vector.add(tcProperty[12]);
			// tcProperty[12].setDateValueData(lcValueDate);
			// } else if (lcValueDate != null && lcStr == null) {
			// vector.add(tcProperty[12]);
			// tcProperty[12].setDateValueData(lcValueDate);
			// }
			vector.add(tcProperty[12]);
			tcProperty[12].setDateValueData(lcValueDate);

			if (!sopfyStr.equals(sopfyValue)) {
				vector.add(tcProperty[13]);
				tcProperty[13].setIntValueData(Integer.parseInt(sopfyValue));
			}
			if (!modelYearStr.equals(modelYearValue)) {
				vector.add(tcProperty[14]);
				tcProperty[14]
						.setIntValueData(Integer.parseInt(modelYearValue));
			}

			String epicValue = epicTextField.getText();
			String functionValue = functionTextField.getText();
			String frontSSValue = getComponentValue(frontSSLov);
			String rearSSValue = getComponentValue(rearSSLov);

			// 5.12-新增加一个属性jci6_SMTE_Variables
			// String SMTEValue = getComponentValue(SMTE_lov);

			String trackValue = getComponentValue(trackLov);
			String reclinerValue = getComponentValue(reclinerLov);
			String pumpValue = getComponentValue(pumpLov);
			String vtaValue = getComponentValue(vtaLov);
			String latchValue = getComponentValue(latchLov);
			if (!epicStr.equals(epicValue)) {
				userService.call("open_or_close_pass", new Object[] { 1 });
				projectInfoItem.setProperty("jci6_EPIC_PN", epicValue);
				userService.call("open_or_close_pass", new Object[] { 0 });
			}
			if (!functionStr.equals(functionValue)) {
				vector.add(tcProperty[15]);
				tcProperty[15].setStringValueData(functionValue);
			}
			if (!frontSSStr.equals(frontSSValue)) {
				vector.add(tcProperty[16]);
				tcProperty[16].setStringValueData(frontSSValue);
			}
			if (!rearSSStr.equals(rearSSValue)) {
				vector.add(tcProperty[17]);
				tcProperty[17].setStringValueData(rearSSValue);
			}

			if (!trackStr.equals(trackValue)) {
				vector.add(tcProperty[18]);
				tcProperty[18].setStringValueData(trackValue);
			}
			if (!reclinerStr.equals(reclinerValue)) {
				vector.add(tcProperty[19]);
				tcProperty[19].setStringValueData(reclinerValue);
			}
			if (!pumpStr.equals(pumpValue)) {
				vector.add(tcProperty[20]);
				tcProperty[20].setStringValueData(pumpValue);
			}
			if (!vtaStr.equals(vtaValue)) {
				vector.add(tcProperty[21]);
				tcProperty[21].setStringValueData(vtaValue);
			}
			if (!latchStr.equals(latchValue)) {
				vector.add(tcProperty[22]);
				tcProperty[22].setStringValueData(latchValue);
			}

			// equ
			vector.add(tcProperty[23]);
			tcProperty[23].setDoubleValueData(Double.parseDouble(equ));

			if (!modeNameStr.equals(modeNameValue)) {
				vector.add(tcProperty[24]);
				tcProperty[24].setStringValueData(modeNameValue);
			}

			TCPropertyDescriptor SMTETCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_SMTE_Variables");
			TCProperty SMTE_Property = projectInfoIR
					.getTCProperty("jci6_SMTE_Variables");
			// 5.12-SMTE--wuwei
			if (SMTE_lov.isValueModified()) {
				System.out
						.println("=========修改SMTELOV-----SUCCESS！！！=============");
				try {
					userService.call("open_or_close_pass", new Object[] { 1 });
					SMTE_lov.save(projectInfoIR
							.getTCProperty("jci6_SMTE_Variables"));
					userService.call("open_or_close_pass", new Object[] { 0 });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			int i = vector.size();
			if (i > 0) {
				TCProperty atcproperty2[] = new TCProperty[i];
				for (int j = 0; j < i; j++)
					atcproperty2[j] = (TCProperty) vector.elementAt(j);

				try {
					userService.call("open_or_close_pass", new Object[] { 1 });
					projectInfoIR.setTCProperties(atcproperty2);
					userService.call("open_or_close_pass", new Object[] { 0 });
					projectInfoIR.lock();
					projectInfoIR.save();
					projectInfoIR.unlock();
					// modify修改后，刷新一下
					projectInfoIR.refresh();

					TCProperty atcproperty1[] = projectInfoIR
							.getTCProperties(propProInfoNames);
					oemNameStr = atcproperty1[0].getStringValue();
					if (atcproperty1[1].getReferenceValue() != null)
						secStr = atcproperty1[1].getReferenceValue().toString();
					else
						secStr = "";
					if (atcproperty1[2].getReferenceValue() != null)
						diviStr = atcproperty1[2].getReferenceValue()
								.toString();
					else
						diviStr = "";

					if (atcproperty1[3].getReferenceValue() != null)
						tlStr = atcproperty1[3].getReferenceValue().toString();
					else
						tlStr = "";

					targetPosStr = atcproperty1[4].getDoubleValue() + "";

					boolean statusVal = atcproperty1[5].getLogicalValue();

					System.out.println("=======5.28=====statusVal============="
							+ statusVal);

					// 5.28修改
					// if (statusVal)
					if ("Active".equals(activeStatusValue)) {
						activeStatusStr = "Active";
						this.m_activeStatusBtn.setSelection(true);
						this.m_inactiveStatusBtn.setSelection(false);
					} else {
						activeStatusStr = "Inactive";
						this.m_activeStatusBtn.setSelection(false);
						this.m_inactiveStatusBtn.setSelection(true);
					}

					projectStatusStr = atcproperty1[6].getStringValue();
					categoryStr = atcproperty1[7].getStringValue();
					productStr = atcproperty1[8].getStringValue();
					typeStr = atcproperty1[9].getStringValue();

					pStartStr = atcproperty1[10].getDateValue();
					pEndStr = atcproperty1[11].getDateValue();
					lcStr = atcproperty1[12].getDateValue();
					sopfyStr = atcproperty1[13].getIntValue() + "";
					modelYearStr = atcproperty1[14].getIntValue() + "";

					epicStr = projectInfoItem.getProperty("jci6_EPIC_PN");
					functionStr = atcproperty1[15].getStringValue();
					frontSSStr = atcproperty1[16].getStringValue();
					rearSSStr = atcproperty1[17].getStringValue();
					trackStr = atcproperty1[18].getStringValue();
					reclinerStr = atcproperty1[19].getStringValue();
					pumpStr = atcproperty1[20].getStringValue();
					vtaStr = atcproperty1[21].getStringValue();
					latchStr = atcproperty1[22].getStringValue();
					// 5.12-SMTE
					// SMTEStr = atcproperty1[25].getStringValue();

					modeNameStr = atcproperty1[24].getStringValue();

					System.out.println("----修改项目信息结束！！！----");

				} catch (TCException tcexception1) {
					projectInfoIR.clearCache(propProInfoNames);
					MessageBox.post(Utilities.getCurrentFrame(), tcexception1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// add by wuh
	/**
	 * 判断用户有没有指派到时间表任务上
	 * 
	 * @throws Exception
	 */
	// private void proMembersMaintainParity() throws Exception {
	// proMemberSb.setLength(0);
	// groupMemberVec.clear();
	// proMemberSb.append(reg.getString("assign_scheduletask"));
	// TreePath atreepath[] = teamRoleUserTree.getSelectionPaths();
	// for (int i = 0; atreepath != null && i < atreepath.length; i++) {
	// TreePath treepath = atreepath[i];
	// ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode =
	// (ProjectDataPanel.TeamRoleUserTreeNode) treepath
	// .getLastPathComponent();
	//
	// System.out
	// .println("proMembersMaintainParity  teamroleusertreenode-->"
	// + teamroleusertreenode.toString());
	// // if (teamroleusertreenode instanceof ProjectTeamChildrenTreeNode)
	// // {
	// // System.out.println("is ProjectTeamChildrenTreeNode");
	// // ProjectTeamChildrenTreeNode projectteamchildrentreenode =
	// // (ProjectTeamChildrenTreeNode) teamroleusertreenode;
	// //
	// // } else {
	// // System.out.println("is not ProjectTeamChildrenTreeNode");
	// // }
	// System.out.println("teamroleusertreenode.type--->"
	// + teamroleusertreenode.type);
	// System.out.println("selected object type is--->"
	// + teamroleusertreenode.comp.getType());
	// // type=4 选中的就是TCComponentGroupMember,type=2 选中的就是TCComponentRole
	// if (teamroleusertreenode.type == 4) {
	// TCComponentGroupMember tccomponentgroupmember = (TCComponentGroupMember)
	// teamroleusertreenode.comp;
	// groupMemberYZ(tccomponentgroupmember);
	// groupMemberVec.add(tccomponentgroupmember);
	// } else if (teamroleusertreenode.type == 2) {
	// int node_child_cnt = teamroleusertreenode.getChildCount();
	// System.out.println("child node count is--->" + node_child_cnt);
	// // 得到第一个子
	// for (int j = 0; j < node_child_cnt; j++) {
	// ProjectDataPanel.TeamRoleUserTreeNode child_node =
	// (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
	// .getChildAt(j);
	// TCComponentGroupMember child_group_member = (TCComponentGroupMember)
	// child_node.comp;
	// System.out.println("child node group member name is---->"
	// + child_group_member.getProperty("object_name"));
	// groupMemberYZ(child_group_member);
	// groupMemberVec.add(child_group_member);
	// }
	// }
	// }
	// }

	/**
	 * 根据项目id和user_id查找该成员是否被指派任务
	 * 
	 * @param groupmember
	 *            指定groupmember
	 * @throws Exception
	 */
	private void groupMemberYZ(TCComponentGroupMember groupmember)
			throws Exception {
		// 得到userid
		String user_id = groupmember.getUserId();
		String user_name = groupmember.getUser().getProperty("user_name");
		System.out.println("groupmember user_id is---->" + user_id);
		TCComponentQuery query = getQuery("YFJC_Search ResourceAssign");
		if (query == null) {
			System.out.println("not define 'YFJC_Search ResourceAssign' query");
		} else {
			String[] textName = new String[] { "project_id", "user_id" };
			String[] textVals = new String[] { projectID, user_id };
			TCComponent[] comps = query(query, textName, textVals);
			if (comps != null) {
				System.out.println("query count is" + comps.length);
				for (int i = 0; i < comps.length; i++) {
					System.out.println("query object type --->"
							+ comps[i].getType());
				}
				if (comps.length > 0) {
					// modify by wuwei
					// proMemberSb.append("\r\n").append(user_name);
				}
			} else {
				System.out.println("query count is 0");
			}
		}
	}

	/**
	 * 得到系统查询
	 * 
	 * @param queryName
	 *            查询器名称
	 * @return
	 * @throws TCException
	 */
	private TCComponentQuery getQuery(String queryName) throws TCException {
		TCComponentQueryType queryType = (TCComponentQueryType) session
				.getTypeComponent("ImanQuery");
		return (TCComponentQuery) queryType.find(queryName);
	}

	/**
	 * 调用系统查询
	 * 
	 * @param query
	 *            查询器对象
	 * @param textName
	 *            查询关键字
	 * @param textVals
	 *            查询关键字对应的值
	 * @return 返回查找的对象
	 * @throws TCException
	 */
	private TCComponent[] query(TCComponentQuery query, String[] textName,
			String[] textVals) throws TCException {
		TCTextService textService = session.getTextService();
		String[] searchAttributes = new String[textName.length];
		for (int i = 0; i < textName.length; i++) {
			searchAttributes[i] = textService.getTextValue(textName[i]);
		}
		return query.execute(searchAttributes, textVals);
	}

	/*
	 * 
	 * 调用系统查询
	 */
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

	/*
	 * 
	 * 设置特殊情况lov必填项
	 */
	public void setRequire(LOVComboBox lov, TCPropertyDescriptor des) {
		if (des.isRequired())
			lov.setMandatory(true);
	}

	private JPanel buildDefiningXMJCAttrPanel() {
		JPanel jpanel1 = new JPanel(new PropertyLayout());
		try {
			// 项目ID
			JLabel idJlabel = new JLabel(reg.getString("projectID.LABEL", "ID"));
			idField = new iTextField(18, 64, true);
			// helpButton = new JButton(reg.getImageIcon("help_jci6.ICON"));
			// helpButton.setPreferredSize(new Dimension(25, 20));
			// helpButton.setToolTipText(reg.getString("help_tip"));
			// helpButton.addActionListener(new ActionListener() {
			// @Override
			// public void actionPerformed(ActionEvent arg0) {
			// // TODO Auto-generated method stub
			// MessageBox.post(reg.getString("help_text"), "提示！",
			// MessageBox.INFORMATION);
			// }
			// });

			idField.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent keyevent) {
					if ((!(ProjectDefinitionView.this.isPA))
							&& (!(ProjectDefinitionView.this.isPTA))
							&& (!(ProjectDefinitionView.this.isPTA())))
						return;
					ProjectDefinitionView.this.validateButtons();
				}

			});
			idField.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent mouseevent) {
					ProjectDefinitionView.this.isDirty = true;
				}

			});
			// 项目名称
			JLabel nameJlabel = new JLabel(reg.getString("projectName.LABEL",
					"Name"));
			nameField = new iTextField(18, 32, true);
			nameField.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent mouseevent) {
					if ((!(ProjectDefinitionView.this.isPA))
							&& (!(ProjectDefinitionView.this.isPTA))
							&& (!(ProjectDefinitionView.this.isPTA())))
						return;
					ProjectDefinitionView.this.validateButtons();
				}

			});
			nameField.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent keyevent) {
					ProjectDefinitionView.this.isDirty = true;
				}

			});
			// 项目描述
			JLabel descJlabel = new JLabel(reg.getString("projectDesc.LABEL",
					"Description"));
			descField = new iTextArea(3, 28, 240, false);
			descField.setLineWrap(true);
			descField.setWrapStyleWord(true);
			descField.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent keyevent) {
					if ((!(ProjectDefinitionView.this.isPA))
							&& (!(ProjectDefinitionView.this.isPTA))
							&& (!(ProjectDefinitionView.this.isPTA())))
						return;
					ProjectDefinitionView.this.validateButtons();
				}

			});
			descField.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent mouseevent) {
					ProjectDefinitionView.this.isDirty = true;
				}

			});
			JScrollPane jscrollpane = new JScrollPane();
			jscrollpane.getViewport().add(descField);

			TCComponentItemRevisionType tcComponentItemRevisionType = (TCComponentItemRevisionType) session
					.getTypeComponent("JCI6_ProgramInfoRevision");
			// 客户名称
			TCPropertyDescriptor oemNameTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_OEMName");
			JLabel omeNameJlabel = new JLabel(
					oemNameTCProperty.getDisplayName());
			oemNameLov = new LOVComboBox(oemNameTCProperty.getLOV());
			oemNameLov.setMaximumRowCount(20);
			setRequire(oemNameLov, oemNameTCProperty);
			oemNameLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

					validateButtons();
				}

			});
			oemNameLov.setPreferredSize(new Dimension(200, 20));
			oemNameLov.setMinimumSize(new Dimension(200, 20));
			// jci6_OEMModelName
			TCPropertyDescriptor modeNameTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_OEMModelName");

			JLabel modeNameJlabel = new JLabel(
					modeNameTCProperty.getDisplayName());
			modeNameField = new iTextField(18, 32, false);
			modeNameField.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent mouseevent) {
					validateButtons();
				}

			});
			modeNameField.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent keyevent) {
					validateButtons();
				}

			});

			// 所属科室
			TCPropertyDescriptor secTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_ProgramSec");
			JLabel secJlabel = new JLabel(secTCProperty.getDisplayName());
			secLov = new LOVComboBox(secTCProperty.getLOV());
			setRequire(secLov, secTCProperty);
			TCComponentListOfValues listlov = secTCProperty.getLOV();
			lovSecValues = listlov.getListOfValues().getLOVDisplayValues();
			secLov.setMaximumRowCount(20);
			secLov.setPreferredSize(new Dimension(200, 20));
			secLov.setMinimumSize(new Dimension(200, 20));
			secLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub

					if (secLov.getSelectedItem() != null
							&& !"".equals(secLov.getTextField().getText())) {
						String secValue = secLov.getSelectedItem().toString();
						secValue = secValue.substring(secValue.indexOf(".") + 1);
						diviLov.setText(secValue);
					} else if (secLov.getSelectedObject() != null) {
						String secValue = secLov.getSelectedObject().toString();
						secValue = secValue.substring(secValue.indexOf(".") + 1);
						diviLov.setText(secValue);
					}
					validateButtons();

				}

			});

			// 所属部门
			TCPropertyDescriptor diviTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_ProgramDivi");
			JLabel diviJlabel = new JLabel(diviTCProperty.getDisplayName());

			diviLov = new LOVComboBox(diviTCProperty.getLOV());
			setRequire(diviLov, diviTCProperty);
			diviLov.setMaximumRowCount(24);
			diviLov.setPreferredSize(new Dimension(200, 20));
			diviLov.setMinimumSize(new Dimension(200, 20));

			diviLov.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub

					if (diviLov.getSelectedItem() != null
							&& !"".equals(diviLov.getTextField().getText())) {

						// 添加联动过滤
						String diviValue = diviLov.getSelectedItem().toString();
						diviValue = diviValue.replace(".", ";").split(";")[0];

						secLov.removeAllItems();
						if (lovSecValues != null && lovSecValues.length > 0) {

							for (int i = 0; i < lovSecValues.length; i++) {

								if (lovSecValues[i].toString().indexOf(
										diviValue) >= 0) {
									secLov.addItem(lovSecValues[i]);
								}
							}
						}
					} else if (diviLov.getSelectedObject() != null
							&& !"".equals(diviLov.getSelectedObject()
									.toString())) {
						// 添加联动过滤
						String diviValue = diviLov.getSelectedObject()
								.toString();
						diviValue = diviValue.replace(".", ";").split(";")[0];
						secLov.removeAllItems();
						if (lovSecValues != null && lovSecValues.length > 0) {

							for (int i = 0; i < lovSecValues.length; i++) {

								if (lovSecValues[i].toString().indexOf(
										diviValue) >= 0) {
									secLov.addItem(lovSecValues[i]);
								}
							}
						}
					}
					validateButtons();

				}

			});
			// 项目负责人
			TCPropertyDescriptor tlTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_ProjectTL");
			JLabel tlJlabel = new JLabel(tlTCProperty.getDisplayName());
			tlLov = new PropertyLOVUIComponent();
			tlLov.setMaximumRowCount(24);
			try {
				tlLov.load(tlTCProperty);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tlLov.setPreferredSize(new Dimension(200, 20));
			tlLov.setMinimumSize(new Dimension(200, 20));

			tlLov.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					validateButtons();
				}

			});
			/*
			 * //目标可能性百分比 TCPropertyDescriptor
			 * targetPosTCProperty=tcComponentItemRevisionType
			 * .getPropertyDescriptor("jci6_TargetPos"); JLabel targetPosJlabel
			 * = new JLabel(targetPosTCProperty.getDisplayName());
			 * targetPosField = new LOVComboBox(targetPosTCProperty.getLOV());
			 * targetPosField.setMaximumRowCount(24);
			 * setRequire(targetPosField,targetPosTCProperty);
			 * targetPosField.addActionListener(new ActionListener(){
			 * 
			 * @Override public void actionPerformed(ActionEvent arg0) { // TODO
			 * Auto-generated me1111thod stub
			 * projAdminPanel.clearProjectsTreeSelection(); validateButtons(); }
			 * 
			 * });
			 */

			// add by wuh 2014-5-26
			TCComponentItemType tcComponentItemType = (TCComponentItemType) session
					.getTypeComponent("JCI6_ProgramInfo");
			TCPropertyDescriptor creDateProperty = tcComponentItemType
					.getPropertyDescriptor("creation_date");
			JLabel creDateJlabel = new JLabel(creDateProperty.getDisplayName());
			createDateButton = new DateButton(new Date(), "yyyy-MM-dd HH:mm",
					true, false, false);
			createDateButton.setEnabled(false);
			TCPropertyDescriptor cloDateProperty = tcComponentItemType
					.getPropertyDescriptor("jci6_CloseDate");
			JLabel cloDateJlabel = new JLabel(cloDateProperty.getDisplayName());
			closeDateButton = new DateButton(new Date(), "yyyy-MM-dd HH:mm",
					true, false, false);
			closeDateButton.setEnabled(false);
			closeDateButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub
					validateButtons();
				}
			});

			// add by wuh
			TCPropertyDescriptor eqmProperty = tcComponentItemType
					.getPropertyDescriptor("jci6_IsRunEQM");
			JLabel eqmJlabel = new JLabel(eqmProperty.getDisplayName());
			// eqmRadioButton = new JRadioButton();
			// eqmRadioButton.setEnabled(false);
			// eqmRadioButton.addActionListener(new ActionListener() {
			// @Override
			// public void actionPerformed(ActionEvent actionevent) {
			// // TODO Auto-generated method stub
			// validateButtons();
			// }
			// });

			jpanel1.add("1.1.right.center", idJlabel);
			jpanel1.add("1.2.left.center", idField);
			// jpanel1.add("1.3.left.center", helpButton);
			jpanel1.add("2.1.right.center", nameJlabel);
			jpanel1.add("2.2.left.center", nameField);
			jpanel1.add("3.1.right.center", descJlabel);
			jpanel1.add("3.2.center.center", jscrollpane);
			jpanel1.add("4.1.right.center", omeNameJlabel);
			jpanel1.add("4.2.center.center", oemNameLov);

			jpanel1.add("5.1.right.center", modeNameJlabel);
			jpanel1.add("5.2.center.center", modeNameField);

			jpanel1.add("6.1.right.center", diviJlabel);
			jpanel1.add("6.2.center.center", diviLov);
			jpanel1.add("7.1.right.center", secJlabel);
			jpanel1.add("7.2.center.center", secLov);
			jpanel1.add("8.1.right.center", tlJlabel);
			jpanel1.add("8.2.center.center", tlLov);
			// jpanel1.add("8.1.right.center", targetPosJlabel);
			// jpanel1.add("8.2.center.center", targetPosField);

			// add by wuh 2014-5-26
			jpanel1.add("9.1.right.center", creDateJlabel);
			jpanel1.add("9.2.center.center", createDateButton);
			jpanel1.add("10.1.right.center", cloDateJlabel);
			jpanel1.add("10.2.center.center", closeDateButton);
			// jpanel1.add("11.1.right.center", eqmJlabel);
			// jpanel1.add("11.2.center.center", eqmRadioButton);
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jpanel1;
	}

	private JTextField nameField;
	private JTextField idField;
	private iTextArea descField;

	// 设置属性
	public TCComponentItem setProgramInfoForItk() {
		buff = new StringBuffer();
		String projectId = getProjectID();
		String projectName = getProjectName();
		String epic = epicTextField.getText();
		buff.append(projectId).append(";").append(projectName).append(";")
				.append("JCI6_ProgramInfo");

		// 项目基础
		String oemName = getComponentValue(oemNameLov);
		String modeName = modeNameField.getText();
		String sec = getComponentValue(secLov);
		String divi = getComponentValue(diviLov);
		String tl = getComponentValue(tlLov);
		String targetPos = targetPosField.getTextField().getText();// getComponentValue(targetPosField);
		// add by wuh 2014-5-26
		Date closeDate = closeDateButton.getDate();
		// add 2014-6-5
		// boolean eqmflag = eqmRadioButton.isSelected();

		// 项目类型
		boolean activeStatusLogic = false;
		String activeStatus = getComponentValue(activeStatusBox);
		if ("Active".equals(activeStatus))
			activeStatusLogic = true;
		String projectStatus = getComponentValue(projectStatusBox);
		String category = getComponentValue(categoryBox);
		String product = getComponentValue(productLov);
		String type = getComponentValue(typeLov);

		// 项目日期
		Date pStart = pStartDate.getDate();
		Date pEnd = pEndDate.getDate();
		Date lc = lcDate.getDate();
		String sopfy = getComponentValue(sopfyLov);
		String modelYear = getComponentValue(modelYearLov);

		// 功能配置
		String function = functionTextField.getText();
		String frontSS = getComponentValue(frontSSLov);
		String rearSS = getComponentValue(rearSSLov);
		String track = getComponentValue(trackLov);
		String recliner = getComponentValue(reclinerLov);
		String pump = getComponentValue(pumpLov);
		String vta = getComponentValue(vtaLov);
		String latch = getComponentValue(latchLov);
		// 5.12-SMTE
		// String SMTEs = getComponentValue(SMTE_lov);

		// PDX信息默认只读
		String equ = equTextField.getText();
		if ("".equals(equ))
			equ = "0";

		TCComponentItem newItem = null;
		try {
			Object obj[] = new Object[1];
			obj[0] = buff.toString();
			Object ret = session.getUserService().call("jc6_create_item", obj);
			TCComponentItemType tcComponentItemType = (TCComponentItemType) session
					.getTypeComponent("JCI6_ProgramInfo");
			newItem = tcComponentItemType.find(getProjectID());
			if (newItem != null) {

				System.out.println("---设置programinfo信息开始----projectId:"
						+ projectId);

				String propProInfoNames[] = { "jci6_OEMName",
						"jci6_ProgramSec", "jci6_ProgramDivi",
						"jci6_ProjectTL", "jci6_TargetPos",
						"jci6_ActiveStatus", "jci6_ProgramState",
						"jci6_Category", "jci6_Product", "jci6_Type",
						"jci6_StartDate", "jci6_EndDate",
						"jci6_SOP",
						"jci6_SOPFY",
						"jci6_ModelYear",// "jci6_EPIC_PN",
						"jci6_Function", "jci6_FrontSS", "jci6_RearSS",
						"jci6_Track", "jci6_Recliner", "jci6_PUMP", "jci6_VTA",
						"jci6_Latch", "jci6_EQU", "jci6_OEMModelName" };
				// "use_program_security" };
				TCComponentItemRevision newIr = newItem.getLatestItemRevision();

				newItem.setProperty("jci6_EPIC_PN", epic);

				// 7.10修改--项目资料库权限
				TCProperty[] tcProperty = newIr
						.getTCProperties(propProInfoNames);

				tcProperty[0].setStringValueData(oemName);

				TCComponentGroupType groupType = (TCComponentGroupType) session
						.getTypeComponent("Group");

				System.out.println("--------设置完OEM的name------");

				// jci6_OEMModelName

				tcProperty[24].setStringValueData(modeName);

				System.out.println("--------设置完modeNameLOV------");

				if (sec != null && !"".equals(sec)) {

					TCComponentGroup secGroup = groupType.find(sec);

					tcProperty[1].setReferenceValueData(secGroup);

					System.out.println("--------设置完OsecGroup------");
				}
				if (divi != null && !"".equals(divi)) {

					TCComponentGroup diviGroup = groupType.find(divi);

					tcProperty[2].setReferenceValueData(diviGroup);

					System.out.println("--------设置完diviGroup------");
				}

				// add by wuh 2014-5-26
				if (closeDate != null) {
					newItem.setDateProperty("jci6_CloseDate", closeDate);
				}

				// add by wuh 2014-6-5
				// if (eqmflag) {
				// newItem.setLogicalProperty("jci6_IsRunEQM", eqmflag);
				// }

				if (!"".equals(targetPos)) {

					tcProperty[4].setDoubleValueData(Double
							.parseDouble(targetPos));

					System.out.println("--------设置完TargetPos------");

				}

				tcProperty[5].setLogicalValueData(activeStatusLogic);

				System.out.println("--------设置完activeStatus------");

				tcProperty[6].setStringValueData(projectStatus);

				System.out.println("--------设置完programnetatus------");

				tcProperty[7].setStringValueData(category);

				System.out.println("--------设置完category------");

				tcProperty[8].setStringValueData(product);

				System.out.println("--------设置完product------");

				tcProperty[9].setStringValueData(type);

				System.out.println("--------设置完TYPE------");

				tcProperty[10].setDateValueData(pStart);

				System.out.println("--------设置完StartDate------");

				tcProperty[11].setDateValueData(pEnd);

				System.out.println("--------设置完EndDate------");

				tcProperty[12].setDateValueData(lc);

				System.out.println("--------设置完SOP------");

				if (!"".equals(sopfy)) {

					tcProperty[13].setIntValueData(Integer.parseInt(sopfy));

					System.out.println("--------设置完SOPFY------");
				} else {
					tcProperty[13].setIntValueData(2019);
				}
				if (!"".equals(modelYear)) {

					tcProperty[14].setIntValueData(Integer.parseInt(modelYear));

					System.out.println("--------设置完modelYear------");
				} else {
					tcProperty[14].setIntValueData(2019);
				}

				tcProperty[15].setStringValueData(function);

				System.out.println("--------设置完function------");

				tcProperty[16].setStringValueData(frontSS);

				System.out.println("--------设置完frontSS------");

				tcProperty[17].setStringValueData(rearSS);

				System.out.println("--------设置完rearSS------");

				tcProperty[18].setStringValueData(track);

				System.out.println("--------设置完track------");

				tcProperty[19].setStringValueData(recliner);

				System.out.println("--------设置完recliner------");

				tcProperty[20].setStringValueData(pump);

				System.out.println("--------设置完pump------");

				tcProperty[21].setStringValueData(vta);

				System.out.println("--------设置完VTA------");

				tcProperty[22].setStringValueData(latch);

				System.out.println("--------设置完LATCH------");

				// 5.12--SMTE
				// tcProperty[25].setStringValueData(SMTEs);
				TCPropertyDescriptor SMTETCProperty = tcComponentItemRevisionType
						.getPropertyDescriptor("jci6_SMTE_Variables");

				SMTE_lov.load(SMTETCProperty);
				userService.call("open_or_close_pass", new Object[] { 1 });
				SMTE_lov.save(newIr);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完SMTE_Variables------");

				// equ
				tcProperty[23].setDoubleValueData(Double.parseDouble(equ));

				System.out.println("--------设置完EQU------");

				System.out.println("-----------Project Team Leader========="
						+ tl);

				// modify by wuwei
//				List localObject = this.project.getTeam();
//				Object object = localObject.get(1);
//				System.out.println("new Object-->" + object
//						+ "   Object.getClass():" + object.getClass());

				TCComponentUser teamAdmin = TeamUser;
//				if (object instanceof TCComponentUser[]) {
//					TCComponentUser[] array = (TCComponentUser[]) object;
//					if (array != null && array.length > 0) {
//						for (int bb = 0; bb < array.length; bb++) {
//							System.out.println("array[" + bb + "]-->"
//									+ array[bb]);
//							
//							teamAdmin = array[bb];
//						}
//						//teamAdmin = array[0];
//					}
//				}
				System.out.println("lala teamAdmin-->" + teamAdmin);

				// modify by wuwei
				// 8.22--修改
				tcProperty[3].setReferenceValueData((TCComponent) teamAdmin);

				System.out.println(teamAdmin
						+ "--------修改 ---Project Team Leader  完成！！========="
						+ tl);

				// if (tlLov.getSelectedObject() != null) {
				// System.out.println("----------------tlLov有值！！！======="
				// + tlLov.getSelectedObject().getClass().toString());
				//
				// if (tlLov.getSelectedObject().getClass().toString()
				// .indexOf("String") > 0) {
				//
				// System.out.println("----------------tlLov的值为======="
				// + tlLov.getSelectedObject().getClass()
				// .toString());
				// TCComponentUserType userTL = (TCComponentUserType) session
				// .getTypeComponent("User");
				// if (tl != null && !"".equals(tl)) {
				// if (tl.indexOf("(") > 0) {
				// String strUser = tl.replaceAll("\\(", ";")
				// .replaceAll("\\)", "").split(";")[1]
				// .trim();
				//
				// System.out.println("---------strUser==="
				// + strUser);
				// TCComponentUser user = userTL.find(strUser);
				//
				// System.out.println("-------------user===="
				// + user.getOSUserName());
				//
				// tcProperty[3].setReferenceValueData(user);
				//
				// // System.out.println("--------设置完tlLov的user------");
				// } else {
				// TCComponent[] users = query(session,
				// QUERY_NAME,
				// new String[] { "PersonName" },
				// new String[] { tl });
				//
				// tcProperty[3].setReferenceValueData(users[0]);
				//
				// System.out
				// .println("--------------设置完tlLov的user-过查询找到！！！------");
				// }
				// }
				// } else {
				// TCComponentUser tlUser = (TCComponentUser) tlLov
				// .getSelectedObject();
				// if (tlUser != null) {
				//
				// tcProperty[3].setReferenceValueData(tlUser);
				// }
				//
				// System.out
				// .println("--------------直接设置完tlLov的值！！！-----");
				// }
				// }

				// 7.10修改--项目资料库权限
				// userService.call("open_or_close_pass", new Object[] { 1 });
				// tcProperty[25].setLogicalValue(true);
				// userService.call("open_or_close_pass", new Object[] { 0 });

				System.out.println("---设置项目信息结束----");
				userService.call("open_or_close_pass", new Object[] { 1 });
				newIr.lock();
				newIr.setTCProperties(tcProperty);
				newIr.save();
				newIr.unlock();
				userService.call("open_or_close_pass", new Object[] { 0 });
				newIr.refresh();
			} else {
				System.out.println("-----newItem is NULL-----ProjectID:"
						+ getProjectID());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newItem;
	}

//	public void doWorkRefeshView() {
//		AbstractAIFOperation localObject3 = new AbstractAIFOperation("", false) {
//			public void executeOperation() {
//				try {
//					infos = getSelectedMembersHelper();
//					com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ModifyProjectsInfo2 amodifyprojectsinfo2[] = new com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ModifyProjectsInfo2[1];
//					amodifyprojectsinfo2[0] = new com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ModifyProjectsInfo2();
//					amodifyprojectsinfo2[0].sourceProject = project;
//					amodifyprojectsinfo2[0].clientId = "PLS-RAC-SESSION";
//					amodifyprojectsinfo2[0].projectInfo = new com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ProjectInformation2();
//					amodifyprojectsinfo2[0].projectInfo.projectId = projectID;
//					amodifyprojectsinfo2[0].projectInfo.projectName = projectName;
//					amodifyprojectsinfo2[0].projectInfo.projectDescription = projectDesc;
//					amodifyprojectsinfo2[0].projectInfo.useProgramContext = useProgramSec;
//					amodifyprojectsinfo2[0].projectInfo.active = projectActiveStatus;
//					amodifyprojectsinfo2[0].projectInfo.visible = projectVisibility;
//					HashMap hashmap = new HashMap();
//					ArrayList arraylist = new ArrayList();
//					String s = projectCollaborationCategories;
//					System.out
//							.println("lala executeOperation====projectCollaborationCategories==="
//									+ projectCollaborationCategories);
//
//					for (int j = s.indexOf(","); j != -1; j = s.indexOf(",")) {
//						String s1 = s.substring(0, j);
//						arraylist.add(s1);
//						s = s.substring(j + 1);
//					}
//
//					arraylist.add(s);
//					String as[] = new String[arraylist.size()];
//					arraylist.toArray(as);
//					hashmap.put("fnd0CollaborationCategories", as);
//					hashmap.put("fnd0InheritTeamFromParent",
//							new String[] { Integer.toString(0) });
//					amodifyprojectsinfo2[0].projectInfo.propertyMap = hashmap;
//					amodifyprojectsinfo2[0].projectInfo.teamMembers = new com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.TeamMemberInfo[infos
//							.size()];
//					amodifyprojectsinfo2[0].projectInfo.teamMembers = (com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.TeamMemberInfo[]) infos
//							.toArray(amodifyprojectsinfo2[0].projectInfo.teamMembers);
//					ProjectDefinitionView.logger.info((new StringBuilder("Modify "))
//							.append(projectName).append(" server call starts")
//							.toString());
//					// open bypass
//					userService.call("open_or_close_pass", new Object[] { 1 });
//					result = ProjectManager.getInstance().modifyProjects(
//							amodifyprojectsinfo2);
//					userService.call("open_or_close_pass", new Object[] { 0 });
//
//					System.out.println("result-->" + result);
//
//				} catch (Exception exception) {
//					ProjectDefinitionView.logger.error(exception.getClass().getName(),
//							exception);
//				}
//			}
//		};
//
//		((AbstractAIFOperation) localObject3)
//				.addOperationListener(new InterfaceAIFOperationListener() {
//					public void startOperation(String paramString) {
//					
//					}
//
//					public void endOperation() {
//
//					}
//				});
//		IOperationService localIOperationService = (IOperationService) OSGIUtil
//				.getService(AifrcpPlugin.getDefault(), IOperationService.class);
//		localIOperationService.queueOperation((Job) localObject3);
//		
//	}

}