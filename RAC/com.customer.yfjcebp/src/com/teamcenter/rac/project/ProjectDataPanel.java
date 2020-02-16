/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.teamcenter.rac.project;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.kernel.AIFComponentChangeEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentCreateEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentDeleteEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentPropertyChangeEvent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.common.lov.LOVComboBox;
import com.teamcenter.rac.common.organization.OrgObject;
import com.teamcenter.rac.common.organization.OrgTreePanel;
import com.teamcenter.rac.common.organization.OrganizationPanel;
import com.teamcenter.rac.common.organization.OrganizationTree;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.SoaUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentAMTree;
import com.teamcenter.rac.kernel.TCComponentAMTreeType;
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
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleMember;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCExceptionPartial;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.project.dialogs.PrivilegedUserSelectionDialog;
import com.teamcenter.rac.project.dialogs.TeamAdminSelectionDialog;
import com.teamcenter.rac.stylesheet.PropertyArray;
import com.teamcenter.rac.stylesheet.PropertyLOVUIComponent;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.ConfirmationDialog;
import com.teamcenter.rac.util.DateButton;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.InterfaceSignalOnClose;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Painter;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.SplitPane;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.WildcardStringMatcher;
import com.teamcenter.rac.util.iPopupMenu;
import com.teamcenter.rac.util.iTextArea;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.combobox.iComboBox;
import com.teamcenter.rac.util.log.Debug;
import com.teamcenter.services.internal.rac.core.ProjectLevelSecurityService;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.apache.log4j.Logger;

// Referenced classes of package com.teamcenter.rac.project:
//            TeamRoleUserTreeNode, IProjectAdminApplicationPanel, ProjectTeamChildrenTreeNode, ProjectTeamLoadingTreeController
public class ProjectDataPanel extends JPanel implements InterfaceSignalOnClose,
		InterfaceAIFComponentEventListener, ActionListener {
	public static int UNPRIVILEGED = 0;
	public static int PRIVILEGED = 1;
	public static int TEAMADMIN = 2;
	public static final int LOAD_CHUNK_LIMIT = 5000;
	
	protected TCSession session;
	protected IProjectAdminApplicationPanel projAdminPanel;
	protected Registry reg;
	private JTextField nameField;
	private JTextField idField;
	private JTextArea descField;
	private JRadioButton activeStatusRB;
	private JRadioButton inactiveStatusRB;
	private JRadioButton inactiveInvisibleStatusRB;
	private JCheckBox usePrgmSecCB;
	private OrganizationPanel organizationPanel;
	private OrgTreePanel orgTreePanel;
	private OrganizationTree orgTree;
	private OrgTreeEventHandler orgTreeEventHandler;
	private TeamRoleUserTree teamRoleUserTree;
	private JButton privilegedUserButton;
	private JButton teamAdminButton;
	private JButton addMemberButton;
	private JButton removeMemberButton;
	private JTextField findTextField;
	private JButton findButton;
	private JButton createButton;
	private JButton modifyButton;
	private JButton copyButton;
	private JButton deleteButton;
	private JButton clearButton;
	private boolean isPA = false;
	private boolean isPTA = false;
	private static Hashtable<String, ArrayList<TCComponent>> hashTableData = null;
	private TCComponentProject project;
	private String projectName;
	private String projectID;
	private String projectDesc;
	private boolean projectStatus = true;
	private boolean projectVisibility = true;
	private boolean projectUseProgSec = false;
	private boolean startNewFind = true;
	private static boolean checked = false;
	private static boolean rulesPresent = false;
	private boolean shouldRemove = true;
	private static TCComponentUser currentPTA;
	private static final Logger logger = Logger
			.getLogger(ProjectDataPanel.class);

	public static TCComponentUser teamAdmin = null;
	public static TCComponentUser privilegedUsers[] = null;
	public static ArrayList allPrivUsers = null;

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
	private JButton helpButton;
	private LOVComboBox secLov;// 所属科室
	private LOVComboBox diviLov;// 所属部门
	private PropertyLOVUIComponent tlLov;// 项目负责人
	private LOVComboBox targetPosField;// 目标可能性百分比
	// add by wuh 2014-5-26
	private DateButton createDateButton;// 创建日期
	private DateButton closeDateButton;// 关闭日期
	// add by wuh 2014-6-5
	private JRadioButton eqmRadioButton;
	
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
	private boolean eqmStr;
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

	// 2014-5-20 add by wuh
	private StringBuffer proMemberSb = new StringBuffer();
	private Vector<TCComponentGroupMember> groupMemberVec = new Vector<TCComponentGroupMember>();
	private Vector<TCComponentScheduleMember> scheduleMemberVec = new Vector<TCComponentScheduleMember>();

	private class OrgTreeEventHandler extends MouseAdapter implements
			TreeSelectionListener {
		@Override
		public void mouseClicked(MouseEvent mouseevent) {
			if ((((ProjectDataPanel.this.isPA) || (ProjectDataPanel.this.isPTA)))
					&& (mouseevent.getClickCount() > 1))
				try {
					OrgObject localOrgObject = ProjectDataPanel.this.orgTreePanel
							.getSelectedObject();
					if (localOrgObject == null)
						return;
					int i = localOrgObject.getType();
					if ((i == 0) || (i == 4)) {
						this.selectedOrgObjects = new OrgObject[1];
						this.selectedOrgObjects[0] = localOrgObject;
						ProjectDataPanel.this.doAddMemberAction();
					} else {
						this.selectedOrgObjects = null;
						ProjectDataPanel.this.addMemberButton.setEnabled(false);
					}
				} catch (Exception localException) {
					MessageBox.post(localException);
				}
		}

		@Override
		public void valueChanged(TreeSelectionEvent treeselectionevent) {
			try {
				selectedOrgObjects = orgTreePanel.getSelectedObjects();
				if (selectedOrgObjects != null
						&& selectedOrgObjects.length >= 1) {
					int i = selectedOrgObjects.length;
					boolean flag = false;
					for (int j = 0; j < i; j++) {
						if (selectedOrgObjects[j] == null)
							continue;
						int k = selectedOrgObjects[j].getType();
						if (k != 0 && k != 4)
							continue;
						flag = true;
						break;
					}

					addMemberButton.setEnabled((isPA || isPTA) && flag);
					if (!flag)
						selectedOrgObjects = null;
				} else {
					addMemberButton.setEnabled(false);
					selectedOrgObjects = null;
				}
			} catch (Exception exception) {
				MessageBox.post(exception);
			}
		}

		public OrgObject[] getSelectedOrgObjects() {
			return selectedOrgObjects;
		}

		public void validateAddMemberButton() {
			addMemberButton.setEnabled((isPA || isPTA)
					&& selectedOrgObjects != null);
		}

		OrgObject selectedOrgObjects[];

		private OrgTreeEventHandler() {

			super();
			selectedOrgObjects = null;
		}

	}

	private class TeamRoleUserTree extends JTree {

		private TCComponentUser teamAdmin = null;
		private TCComponentUser[] privilegedUsers = null;
		private ArrayList<TCComponentUser> allPrivUsers = null;

		private class LoadNodesOperation extends AbstractAIFOperation {
			private DefaultTreeModel treeModel;
			private ProjectDataPanel.TeamRoleUserTreeNode parentNode;
			private TCComponent[] teamMembers;

			public LoadNodesOperation(
					DefaultTreeModel paramDefaultTreeModel,
					ProjectDataPanel.TeamRoleUserTreeNode paramTeamRoleUserTreeNode,
					TCComponent[] paramArrayOfTCComponent) {
				this.treeModel = paramDefaultTreeModel;
				this.parentNode = paramTeamRoleUserTreeNode;
				this.teamMembers = paramArrayOfTCComponent;
			}

			public void executeOperation() {
				Object localObject1 = null;
				ArrayList localArrayList1 = new ArrayList();
				ArrayList localArrayList2 = new ArrayList();
				int i = 0;
				ProjectDataPanel.TeamRoleUserTreeNode[] arrayOfTeamRoleUserTreeNode;
				for (int j = 0; j < this.teamMembers.length; ++j) {
					arrayOfTeamRoleUserTreeNode = null;
					if (this.teamMembers[j] instanceof TCComponentGroup) {
						if (Debug.isOn("project"))
							Debug.println("adding group= "
									+ this.teamMembers[j].toString());
						localArrayList2
								.add((TCComponentGroup) this.teamMembers[j]);
						if (j == 0)
							i = 1;
					} else if (this.teamMembers[j] instanceof TCComponentGroupMember) {
						if (Debug.isOn("project"))
							Debug.println("adding group member= "
									+ this.teamMembers[j].toString());
						localArrayList1.add(this.teamMembers[j]);
					} else if (Debug.isOn("project")) {
						Debug.println("WARNING: unsupported team members= "
								+ this.teamMembers[j].toString());
					}
					if (j != 0)
						continue;
					localObject1 = arrayOfTeamRoleUserTreeNode;
				}
				if (localArrayList1.size() > 0) {
					TCComponentGroupMember[] localObject2 = (TCComponentGroupMember[]) localArrayList1
							.toArray(new TCComponentGroupMember[localArrayList1
									.size()]);
					arrayOfTeamRoleUserTreeNode = ProjectDataPanel.TeamRoleUserTree.this
							.addGroupMembers(localObject2);

					localObject1 = arrayOfTeamRoleUserTreeNode[0];
				}
				if (localArrayList2.size() > 0) {
					TCComponentGroup[] localObject2 = (TCComponentGroup[]) localArrayList2
							.toArray(new TCComponentGroup[localArrayList2
									.size()]);
					arrayOfTeamRoleUserTreeNode = ProjectDataPanel.TeamRoleUserTree.this
							.addGroups(localObject2, this.treeModel,
									this.parentNode);
					if (i != 0)
						localObject1 = arrayOfTeamRoleUserTreeNode[0];
				}
				if (localObject1 == null)
					return;
				final ProjectDataPanel.TeamRoleUserTreeNode localObject2 = (ProjectDataPanel.TeamRoleUserTreeNode) localObject1;
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						if (localObject2 != null) {
							TreePath localTreePath = new TreePath(localObject2
									.getPath());
							ProjectDataPanel.TeamRoleUserTree.this
									.scrollPathToVisible(localTreePath);
						}
						ProjectDataPanel.this.validateButtons();
						ProjectDataPanel.TeamRoleUserTree.this.validate();
						ProjectDataPanel.TeamRoleUserTree.this.repaint();
					}
				});
			}
		}

		private ProjectDataPanel.TeamRoleUserTreeNode[] addGroups(
				TCComponentGroup[] paramArrayOfTCComponentGroup,
				DefaultTreeModel paramDefaultTreeModel,
				ProjectDataPanel.TeamRoleUserTreeNode paramTeamRoleUserTreeNode) {
			ProjectDataPanel.TeamRoleUserTreeNode[] arrayOfTeamRoleUserTreeNode = null;
			if (paramArrayOfTCComponentGroup != null) {
				int i = paramArrayOfTCComponentGroup.length;
				arrayOfTeamRoleUserTreeNode = new ProjectDataPanel.TeamRoleUserTreeNode[i];
				TCPreferenceService localTCPreferenceService = this.session
						.getPreferenceService();
				String str = "";
				try {
					str = localTCPreferenceService.getString(1,
							"TC_suppress_inactive_group_members");
					localTCPreferenceService.setString(1,
							"TC_suppress_inactive_group_members", "0");
				} catch (Exception localException1) {
					localException1.printStackTrace();
				}
				loadPropertiesHelper(paramArrayOfTCComponentGroup);
				for (int j = 0; j < i; ++j)
					arrayOfTeamRoleUserTreeNode[j] = addGroup(
							paramDefaultTreeModel, paramTeamRoleUserTreeNode,
							paramArrayOfTCComponentGroup[j]);
				try {
					localTCPreferenceService.setString(1,
							"TC_suppress_inactive_group_members", str);
				} catch (Exception localException2) {
					localException2.printStackTrace();
				}
			}
			return arrayOfTeamRoleUserTreeNode;
		}

		private class PrivUserPopupMenu extends iPopupMenu {
			@Override
			public void show(Component component, int i, int j) {
				super.show(component, i, j);
				validateMenuItems();
			}

			protected void validateMenuItems() {
				if (nodes != null && nodes.length == 1) {
					if (nodes[0].type == 3 || nodes[0].type == 4)
						if (nodes[0].status == ProjectDataPanel.UNPRIVILEGED)
							unsetMenuItem.setEnabled(false);
						else if (nodes[0].status == ProjectDataPanel.PRIVILEGED)
							setMenuItem.setEnabled(false);
						else if (nodes[0].status == ProjectDataPanel.TEAMADMIN) {
							unsetMenuItem.setEnabled(false);
							setMenuItem.setEnabled(false);
						}
					if (setProjMenuItem != null)
						if (project != null && projectUseProgSec)
							setProjMenuItem.setEnabled(true);
						else
							setProjMenuItem.setEnabled(false);
				}
			}

			private void setDefaultProjectForSelectedUsers(
					ProjectDataPanel.TeamRoleUserTreeNode ateamroleusertreenode[]) {
				Vector vector = new Vector();
				for (int i = 0; ateamroleusertreenode != null
						&& i < ateamroleusertreenode.length; i++) {
					ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = ateamroleusertreenode[i];
					if (teamroleusertreenode == null)
						continue;
					if (teamroleusertreenode.type == 0) {
						getGroupUsers(teamroleusertreenode, ALL_USERS, vector);
						continue;
					}
					if (teamroleusertreenode.type == 2) {
						getRoleUsers(teamroleusertreenode, ALL_USERS, vector);
						continue;
					}
					if (teamroleusertreenode.type != 3
							&& teamroleusertreenode.type != 4)
						continue;
					TCComponentUser tccomponentuser = teamroleusertreenode.user;
					if (tccomponentuser != null
							&& !vector.contains(tccomponentuser))
						vector.add(tccomponentuser);
				}

				if (vector.size() > 0 && project != null) {
					TCComponentUser atccomponentuser[] = new TCComponentUser[vector
							.size()];
					vector.toArray(atccomponentuser);
					com.teamcenter.services.internal.rac.core.DataManagementService datamanagementservice = com.teamcenter.services.internal.rac.core.DataManagementService
							.getService(session);
					ServiceData servicedata = datamanagementservice
							.setDefaultProjectForProjectMembers(project,
									atccomponentuser);
					if (servicedata != null
							&& servicedata.sizeOfPartialErrors() > 0) {
						com.teamcenter.rac.kernel.TCExceptionPartial tcexceptionpartial = SoaUtil
								.checkPartialErrorsNoThrow(servicedata);
						MessageBox.post(Utilities.getCurrentFrame(),
								tcexceptionpartial);
						return;
					}
				}
			}

			public TeamRoleUserTree tree;
			public ProjectDataPanel.TeamRoleUserTreeNode nodes[];
			protected JMenuItem setMenuItem;
			protected JMenuItem unsetMenuItem;
			protected JMenuItem setProjMenuItem;

			public PrivUserPopupMenu(
					TeamRoleUserTree teamroleusertree1,
					ProjectDataPanel.TeamRoleUserTreeNode ateamroleusertreenode[]) {

				super();
				tree = null;
				nodes = null;
				setMenuItem = null;
				unsetMenuItem = null;
				setProjMenuItem = null;
				tree = teamroleusertree1;
				nodes = ateamroleusertreenode;
				Registry registry = Registry.getRegistry(this);
				addPopupHeader(registry.getString("privUser.POPUP_TITLE",
						"Define Privileged Users"));
				setMenuItem = new JMenuItem();
				ImageIcon imageicon = registry.getImageIcon("yes.ICON");
				setMenuItem.setIcon(imageicon);
				setMenuItem.setText(registry.getString("setPrivUserMenu.NAME",
						"Set Privileged Users"));
				setMenuItem.setHorizontalTextPosition(4);
				setMenuItem.setVerticalTextPosition(0);
				setMenuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent actionevent) {
						tree.changePrivilegedUsers(nodes, true);
					}

					// JavaClassFileOutputException: Invalid index accessing
					// method local variables table of <init>
				});
				add(setMenuItem);
				unsetMenuItem = new JMenuItem();
				imageicon = registry.getImageIcon("no.ICON");
				unsetMenuItem.setIcon(imageicon);
				unsetMenuItem.setText(registry.getString(
						"unsetPrivUserMenu.NAME", "Unset Privileged Users"));
				unsetMenuItem.setHorizontalTextPosition(4);
				unsetMenuItem.setVerticalTextPosition(0);
				unsetMenuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent actionevent) {
						tree.changePrivilegedUsers(nodes, false);
					}

					// JavaClassFileOutputException: Invalid index accessing
					// method local variables table of <init>
				});
				add(unsetMenuItem);
				if (project != null && projectUseProgSec) {
					setProjMenuItem = new JMenuItem();
					Object aobj[] = { TCComponentProject
							.getDisplayName(session) };
					ImageIcon imageicon1 = registry
							.getImageIcon("projectDefinitionTab.ICON");
					setProjMenuItem.setIcon(imageicon1);
					setProjMenuItem.setText(MessageFormat.format(registry
							.getString("setDefaultProjectMenu.NAME",
									"Set Default Project"), aobj));
					setProjMenuItem.setHorizontalTextPosition(4);
					setProjMenuItem.setVerticalTextPosition(0);
					setProjMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent actionevent) {
							setDefaultProjectForSelectedUsers(nodes);
						}

						// JavaClassFileOutputException: Invalid index accessing
						// method local variables table of <init>
					});
					add(setProjMenuItem);
				}
			}
		}

		public void expandAll(JTree jtree, int i) {
			TreeModel treemodel = jtree.getModel();
			expandJTreeNode(jtree, treemodel, treemodel.getRoot(), 0, i);
		}

		public int expandJTreeNode(JTree jtree, TreeModel treemodel,
				Object obj, int i, int j) {
			if (obj != null && !treemodel.isLeaf(obj)) {
				jtree.expandRow(i);
				if (j != 0) {
					int k = 0;
					do {
						if (i + 1 >= jtree.getRowCount()
								|| k >= treemodel.getChildCount(obj))
							break;
						i++;
						Object obj1 = treemodel.getChild(obj, k);
						if (obj1 == null)
							break;
						TreePath treepath;
						while ((treepath = jtree.getPathForRow(i)) != null
								&& treepath.getLastPathComponent() != obj1)
							i++;
						if (treepath == null)
							break;
						i = expandJTreeNode(jtree, treemodel, obj1, i, j - 1);
						k++;
					} while (true);
				}
			}
			return i;
		}

		public void loadTeam(TCComponentProject paramTCComponentProject) {
			removeAllChildren();
			this.modified = false;
			this.validSelection = false;
			this.project = paramTCComponentProject;
			if (paramTCComponentProject != null) {
				try {
					List localObject = paramTCComponentProject.getTeam();
					this.teamMembers = ((TCComponent[]) ((List) localObject)
							.get(0));
					this.teamAdmin = ((TCComponentUser) ((List) localObject)
							.get(1));
					ProjectDataPanel.currentPTA = this.teamAdmin;
					this.privilegedUsers = ((TCComponentUser[]) ((List) localObject)
							.get(2));
					this.allPrivUsers = null;
					if (Debug.isOn("project")) {
						Debug.println("loading team members count="
								+ ((this.teamMembers != null) ? this.teamMembers.length
										: 0));
						Debug.println("loading priv users count="
								+ ((this.privilegedUsers != null) ? this.privilegedUsers.length
										: 0));
						Debug.println("loading team admin="
								+ ((this.teamAdmin != null) ? this.teamAdmin
										.toString() : "null"));
					}
				} catch (TCException localTCException) {
				}
				Object localObject = (DefaultTreeModel) getModel();
				ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode = (ProjectDataPanel.TeamRoleUserTreeNode) ((DefaultTreeModel) localObject)
						.getRoot();
				if (localTeamRoleUserTreeNode.comp != paramTCComponentProject) {
					localTeamRoleUserTreeNode.comp = paramTCComponentProject;
					localTeamRoleUserTreeNode.name = paramTCComponentProject
							.toString();
					((DefaultTreeModel) localObject)
							.nodeChanged(localTeamRoleUserTreeNode);
				}
				LoadNodesOperation localLoadNodesOperation = new LoadNodesOperation(
						(DefaultTreeModel) localObject,
						localTeamRoleUserTreeNode, this.teamMembers);
				try {
					this.session.queueOperation(localLoadNodesOperation);
				} catch (Exception localException) {
					MessageBox
							.post(Utilities.getCurrentFrame(), localException);
				}
			} else {
				this.teamMembers = null;
				this.privilegedUsers = null;
				this.teamAdmin = null;
				if (!(ProjectDataPanel.this.isPA))
					return;
				this.teamAdmin = this.session.getUser();
			}
		}

		public boolean isUserPTA() {
			boolean flag = false;
			if (ProjectDataPanel.teamAdmin == session.getUser())
				flag = true;
			return flag;
		}

		private void initializeTree() {
			// add by wuwei
			DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();

			// ProjectTeamLoadingTreeController projectteamloadingtreecontroller
			// = new ProjectTeamLoadingTreeController(
			// defaulttreemodel);
			// /addTreeWillExpandListener(projectteamloadingtreecontroller);
			DefaultMutableTreeNode projectteamchildrentreenode;
			if (project == null) {
				String s = reg.getString("projectTeam.TITLE", "Team");
				projectteamchildrentreenode = new DefaultMutableTreeNode(s);
			} else {
				projectteamchildrentreenode = new DefaultMutableTreeNode(
						project, true);
				defaulttreemodel.nodeChanged(projectteamchildrentreenode);
			}
			projectteamchildrentreenode.setAllowsChildren(true);
			setModel(new DefaultTreeModel(projectteamchildrentreenode));
			loadTeam(project);
			setVisibleRowCount(10);
			addTeamRoleUserTreeListeners();
		}

		public void removeAllChildren() {
			ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) getModel()
					.getRoot();
			DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
			if (teamroleusertreenode.getChildCount() <= 0) {
				return;
			} else {
				teamroleusertreenode.removeAllChildren();
				String s = reg.getString("projectTeam.TITLE", "Team");
				teamroleusertreenode.comp = null;
				teamroleusertreenode.name = s;
				defaulttreemodel.nodeChanged(teamroleusertreenode);
				defaulttreemodel.nodeStructureChanged(teamroleusertreenode);
				return;
			}
		}

		private void addTeamRoleUserTreeListeners() {
			getSelectionModel().setSelectionMode(4);
			addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent mouseevent) {
					if (!isPA && !isPTA)
						return;
					if (mouseevent.getClickCount() == 2) {
						TreePath treepath = ((TeamRoleUserTree) mouseevent
								.getSource()).getSelectionPath();
						if (treepath != null) {
							ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) treepath
									.getLastPathComponent();
							validSelection = validForDelete(teamroleusertreenode);
							removeMemberButton.setEnabled((isPA || isPTA)
									&& validSelection);
							if (teamroleusertreenode.comp != ProjectDataPanel.teamAdmin
									&& teamroleusertreenode.user != ProjectDataPanel.teamAdmin
									&& (teamroleusertreenode.type == 3 || teamroleusertreenode.type == 4)) {
								if (teamroleusertreenode.status == ProjectDataPanel.UNPRIVILEGED)
									teamroleusertreenode.status = ProjectDataPanel.PRIVILEGED;
								// .setStatus(ProjectDataPanel.PRIVILEGED);
								else
									teamroleusertreenode.status = ProjectDataPanel.UNPRIVILEGED;
								// .setStatus(ProjectDataPanel.UNPRIVILEGED);
								final DefaultTreeModel treeModel = (DefaultTreeModel) getModel();
								final ProjectDataPanel.TeamRoleUserTreeNode n = teamroleusertreenode;
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										treeModel.nodeChanged(n);
									}

								});
								modified = true;
								validateButtons();
							}
						}
					} else {
						showPrivUserPopup(mouseevent);
					}
				}

			});
			addTreeSelectionListener(new TreeSelectionListener() {
				@Override
				public void valueChanged(TreeSelectionEvent treeselectionevent) {
					TreePath atreepath[] = getSelectionPaths();
					for (int i = 0; atreepath != null && i < atreepath.length; i++) {
						TreePath treepath = atreepath[i];
						if (treepath == null)
							continue;
						ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) treepath
								.getLastPathComponent();
						validSelection = validForDelete(teamroleusertreenode);
						if (!validSelection)
							continue;
						removeMemberButton.setEnabled((isPA || isPTA)
								&& validSelection);
						break;
					}

				}

			});
		}

		private boolean validForDelete(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode) {
			int i = teamroleusertreenode.getLevel();
			if (i == 1)
				return true;
			if (i == 2) {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getParent();
				return teamroleusertreenode1.type == 2;
			} else {
				return false;
			}
		}

		public void validateRemoveMemberButton() {
			removeMemberButton.setEnabled((isPA || isPTA) && validSelection);
		}

		public void addNewGroupMember(
				TCComponentGroupMember tccomponentgroupmember) {
			TCComponentGroup tccomponentgroup = null;
			TCComponentRole tccomponentrole = null;
			try {
				tccomponentgroup = tccomponentgroupmember.getGroup();
				tccomponentrole = tccomponentgroupmember.getRole();
			} catch (TCException tcexception) {
			}
			if (tccomponentgroup != null && tccomponentrole != null) {
				DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();

				// modify by wuwei
				defaulttreemodel.insertNodeInto(new DefaultMutableTreeNode(
						defaulttreemodel), (MutableTreeNode) defaulttreemodel
						.getRoot(), defaulttreemodel
						.getChildCount(defaulttreemodel.getRoot()));
				// ProjectTeamLoadingTreeController
				// projectteamloadingtreecontroller = new
				// ProjectTeamLoadingTreeController(
				// defaulttreemodel);
				// addTreeWillExpandListener(projectteamloadingtreecontroller);
			}
		}

		// add by wuwei
		private void addNewGroupMemberInGroup(
				DefaultTreeModel paramDefaultTreeModel,
				ProjectDataPanel.TeamRoleUserTreeNode paramTeamRoleUserTreeNode,
				TCComponentGroupMember paramTCComponentGroupMember,
				TCComponentGroup paramTCComponentGroup,
				TCComponentRole paramTCComponentRole, boolean paramBoolean) {
			int i = paramTeamRoleUserTreeNode.getChildCount();
			for (int j = 0; j < i; ++j) {
				ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode1 = (ProjectDataPanel.TeamRoleUserTreeNode) paramTeamRoleUserTreeNode
						.getChildAt(j);
				if (paramBoolean) {
					if ((localTeamRoleUserTreeNode1.type != 2)
							|| (localTeamRoleUserTreeNode1.comp != paramTCComponentRole))
						continue;
					ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode2 = new ProjectDataPanel.TeamRoleUserTreeNode(
							paramTCComponentGroupMember,
							paramTCComponentGroupMember.toString(),
							this.teamAdmin);
					if ((localTeamRoleUserTreeNode2.user != this.teamAdmin)
							&& (localTeamRoleUserTreeNode2.user != null))
						if (isPrivilegedUser(localTeamRoleUserTreeNode2.user))
							localTeamRoleUserTreeNode2.status = ProjectDataPanel.this.PRIVILEGED;
						else
							localTeamRoleUserTreeNode2.status = ProjectDataPanel.this.UNPRIVILEGED;
					paramDefaultTreeModel.insertNodeInto(
							localTeamRoleUserTreeNode2,
							localTeamRoleUserTreeNode1,
							localTeamRoleUserTreeNode1.getChildCount());
					return;
				}
				if (localTeamRoleUserTreeNode1.type != 0)
					continue;
				addNewGroupMemberInGroup(
						paramDefaultTreeModel,
						localTeamRoleUserTreeNode1,
						paramTCComponentGroupMember,
						paramTCComponentGroup,
						paramTCComponentRole,
						localTeamRoleUserTreeNode1.comp == paramTCComponentGroup);
			}
		}

		public void removeGroupMember(
				TCComponentGroupMember tccomponentgroupmember) {
			DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
			ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) defaulttreemodel
					.getRoot();
			removeGroupMember(defaulttreemodel, teamroleusertreenode,
					tccomponentgroupmember);
		}

		private void removeGroupMember(DefaultTreeModel defaulttreemodel,
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				TCComponentGroupMember tccomponentgroupmember) {
			int i = teamroleusertreenode.getChildCount();
			for (int j = 0; j < i; j++) {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(j);
				if (teamroleusertreenode1.type == 0) {
					removeGroupMember(defaulttreemodel, teamroleusertreenode1,
							tccomponentgroupmember);
					continue;
				}
				if (teamroleusertreenode1.type == 2)
					removeGroupMemberInRole(defaulttreemodel,
							teamroleusertreenode1, tccomponentgroupmember);
			}

		}

		private void removeGroupMemberInRole(DefaultTreeModel defaulttreemodel,
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				TCComponentGroupMember tccomponentgroupmember) {
			int i = teamroleusertreenode.getChildCount();
			int j = 0;
			do {
				if (j >= i)
					break;
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(j);
				if (teamroleusertreenode1.type == 4
						&& teamroleusertreenode1.comp == tccomponentgroupmember) {
					defaulttreemodel
							.removeNodeFromParent(teamroleusertreenode1);
					if (i == 1) {
						ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode2 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
								.getParent();
						if (teamroleusertreenode2.isRoot())
							defaulttreemodel
									.removeNodeFromParent(teamroleusertreenode);
					}
					break;
				}
				j++;
			} while (true);
		}

		public void processGroupChange(TCComponentGroup tccomponentgroup) {
			DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
			ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) defaulttreemodel
					.getRoot();
			processGroupChange(defaulttreemodel, teamroleusertreenode,
					tccomponentgroup);
		}

		private void processGroupChange(
				DefaultTreeModel paramDefaultTreeModel,
				ProjectDataPanel.TeamRoleUserTreeNode paramTeamRoleUserTreeNode,
				TCComponentGroup paramTCComponentGroup) {
			int i = paramTeamRoleUserTreeNode.getChildCount();
			for (int j = 0; j < i; ++j) {
				ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode = (ProjectDataPanel.TeamRoleUserTreeNode) paramTeamRoleUserTreeNode
						.getChildAt(j);
				if (localTeamRoleUserTreeNode.type != 0)
					continue;
				if (localTeamRoleUserTreeNode.comp == paramTCComponentGroup) {
					paramDefaultTreeModel
							.removeNodeFromParent(localTeamRoleUserTreeNode);
					addGroup(paramDefaultTreeModel, paramTeamRoleUserTreeNode,
							paramTCComponentGroup);
					return;
				}
				processGroupChange(paramDefaultTreeModel,
						localTeamRoleUserTreeNode, paramTCComponentGroup);
			}
		}

		public void removeGroup(TCComponentGroup tccomponentgroup) {
			DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
			ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) defaulttreemodel
					.getRoot();
			removeGroup(defaulttreemodel, teamroleusertreenode,
					tccomponentgroup);
		}

		private void removeGroup(DefaultTreeModel defaulttreemodel,
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				TCComponentGroup tccomponentgroup) {
			int i = teamroleusertreenode.getChildCount();
			for (int j = 0; j < i; j++) {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(j);
				if (teamroleusertreenode1.type != 0)
					continue;
				if (teamroleusertreenode1.comp == tccomponentgroup) {
					defaulttreemodel
							.removeNodeFromParent(teamroleusertreenode1);
					break;
				}
				removeGroup(defaulttreemodel, teamroleusertreenode1,
						tccomponentgroup);
			}

		}

		//modify by wuwei
//		public void addComponents(TCComponent atccomponent[]) {
//			DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
//			ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) defaulttreemodel
//					.getRoot();
//			ArrayList arraylist = new ArrayList();
//			ArrayList arraylist1 = new ArrayList();
//			if (atccomponent != null) {
//				for (int i = 0; i < atccomponent.length; i++) {
//					boolean flag = false;
//					if (atccomponent[i] instanceof TCComponentGroup)
//						flag = true;
//					if (atccomponent[i] instanceof TCComponentGroupMember)
//						arraylist1.add(atccomponent[i]);
//					if (flag
//							&& !duplicatedGroup(teamroleusertreenode,
//									(TCComponentGroup) atccomponent[i]))
//						arraylist.add(atccomponent[i]);
//				}
//
//			}
//			if (!arraylist.isEmpty()) {
//				TCComponent tccomponent;
//				for (Iterator iterator = arraylist.iterator(); iterator
//						.hasNext(); defaulttreemodel.insertNodeInto(
//						new ProjectDataPanel.TeamRoleUserTreeNode(tccomponent,
//								tccomponent.toString()),
//						(MutableTreeNode) defaulttreemodel.getRoot(),
//						defaulttreemodel.getChildCount(defaulttreemodel
//								.getRoot())))
//
//					tccomponent = (TCComponent) iterator.next();
//				// ProjectTeamLoadingTreeController
//				// projectteamloadingtreecontroller = new
//				// ProjectTeamLoadingTreeController(
//				// defaulttreemodel);
//				// addTreeWillExpandListener(projectteamloadingtreecontroller);
//				modified = true;
//			}
//			if (!arraylist1.isEmpty()) {
//				ProjectDataPanel.TeamRoleUserTreeNode projectteamchildrentreenode = null;
//				boolean flag1 = false;
//				for (Iterator iterator1 = arraylist1.iterator(); iterator1
//						.hasNext(); defaulttreemodel
//						.nodeChanged(projectteamchildrentreenode)) {
//					TCComponent tccomponent1 = (TCComponent) iterator1.next();
//					TCComponentGroupMember tccomponentgroupmember = (TCComponentGroupMember) tccomponent1;
//					TCComponentRole tccomponentrole = null;
//					try {
//						tccomponentrole = tccomponentgroupmember.getRole();
//					} catch (TCException tcexception) {
//						Logger.getLogger(
//								com.teamcenter.rac.project.ProjectDataPanel.TeamRoleUserTree.class)
//								.error(tcexception.getLocalizedMessage(),
//										tcexception);
//					}
//					if (tccomponentrole != null)
//						projectteamchildrentreenode = findRoleNode(
//								teamroleusertreenode, tccomponentrole);
//					int j;
//					if (projectteamchildrentreenode == null) {
//						defaulttreemodel.insertNodeInto(
//								new ProjectDataPanel.TeamRoleUserTreeNode(
//										tccomponentrole, tccomponentrole
//												.getObjectString()),
//								(MutableTreeNode) defaulttreemodel.getRoot(),
//								defaulttreemodel.getChildCount(defaulttreemodel
//										.getRoot()));
//						projectteamchildrentreenode = findRoleNode(
//								teamroleusertreenode, tccomponentrole);
//						
//						//modify by wuwei
//						//j = projectteamchildrentreenode.addGroupMember(tccomponentgroupmember);
//							
//						addGroupMember(tccomponentgroupmember);
//					} else {
//						//modify by wuwei
//						//j = projectteamchildrentreenode.addGroupMember(tccomponentgroupmember);
//						addGroupMember(tccomponentgroupmember);		
//					}
//					if (defaulttreemodel
//							.getChildCount(projectteamchildrentreenode) != j)
//						defaulttreemodel
//								.insertNodeInto(
//										new ProjectDataPanel.TeamRoleUserTreeNode(
//												tccomponentgroupmember,
//												tccomponentgroupmember
//														.getObjectString()),
//										projectteamchildrentreenode,
//										defaulttreemodel
//												.getChildCount(projectteamchildrentreenode));
//
//					defaulttreemodel.reload();
//				}
//
//				// ProjectTeamLoadingTreeController
//				// projectteamloadingtreecontroller1 = new
//				// ProjectTeamLoadingTreeController(
//				// defaulttreemodel);
//				// addTreeWillExpandListener(projectteamloadingtreecontroller1);
//				modified = true;
//			}
//		}
		
		public void addComponents(
				TCComponentGroupMember[] paramArrayOfTCComponentGroupMember) {
			ProjectDataPanel.TeamRoleUserTreeNode[] arrayOfTeamRoleUserTreeNode = addGroupMembers(paramArrayOfTCComponentGroupMember);
			if (arrayOfTeamRoleUserTreeNode == null)
				return;
			final ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode = arrayOfTeamRoleUserTreeNode[(arrayOfTeamRoleUserTreeNode.length - 1)];
			if (localTeamRoleUserTreeNode == null)
				return;
			this.modified = true;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					TreePath localTreePath = new TreePath(localTeamRoleUserTreeNode
							.getPath());
					ProjectDataPanel.TeamRoleUserTree.this
							.scrollPathToVisible(localTreePath);
					ProjectDataPanel.this.validateButtons();
					ProjectDataPanel.TeamRoleUserTree.this.validate();
					ProjectDataPanel.TeamRoleUserTree.this.repaint();
				}
			});
		}
		
		public void addComponents(
				TCComponentGroup[] paramArrayOfTCComponentGroup) {
			DefaultTreeModel localDefaultTreeModel = (DefaultTreeModel) getModel();
			ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode = (ProjectDataPanel.TeamRoleUserTreeNode) localDefaultTreeModel
					.getRoot();
			ArrayList localArrayList = new ArrayList();
			if (paramArrayOfTCComponentGroup != null)
				for (int i = 0; i < paramArrayOfTCComponentGroup.length; ++i) {
					if (duplicatedGroup(localTeamRoleUserTreeNode,
							paramArrayOfTCComponentGroup[i]))
						continue;
					localArrayList.add(paramArrayOfTCComponentGroup[i]);
				}
			if (localArrayList.size() <= 0)
				return;
			TCComponent[] arrayOfTCComponent = (TCComponent[]) localArrayList
					.toArray(new TCComponent[localArrayList.size()]);
			LoadNodesOperation localLoadNodesOperation = new LoadNodesOperation(
					localDefaultTreeModel, localTeamRoleUserTreeNode,
					arrayOfTCComponent);
			try {
				this.session.performOperation(localLoadNodesOperation);
			} catch (Exception localException) {
				MessageBox.post(Utilities.getCurrentFrame(), localException);
			}
			this.modified = true;
		}

		private boolean duplicatedGroup(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				TCComponentGroup tccomponentgroup) {
			int i = teamroleusertreenode.getChildCount();
			for (int j = 0; j < i; j++) {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(j);
				if (teamroleusertreenode1.type != 0)
					continue;
				if (teamroleusertreenode1.comp == tccomponentgroup)
					return true;
				if (duplicatedGroup(teamroleusertreenode1, tccomponentgroup))
					return true;
			}

			return false;
		}

		private void loadPropertiesHelper(
				TCComponentGroup[] paramArrayOfTCComponentGroup) {
			ArrayList localArrayList1 = new ArrayList();
			ArrayList localArrayList2 = new ArrayList();
			for (int i = 0; i < paramArrayOfTCComponentGroup.length; ++i) {
				TCComponentGroup localTCComponentGroup = paramArrayOfTCComponentGroup[i];
				localArrayList2.add(localTCComponentGroup);
				try {
					TCComponent[] arrayOfTCComponent1 = localTCComponentGroup
							.getRolesAndGroups();
					for (TCComponent localTCComponent : arrayOfTCComponent1)
						if (localTCComponent instanceof TCComponentRole) {
							localArrayList1.add(localTCComponent);
						} else {
							if (!(localTCComponent instanceof TCComponentGroup))
								continue;
							localArrayList2.add(localTCComponent);
						}
				} catch (TCException localTCException) {
				}
			}
			ProjectDataPanel.loadPropertiesInBulk(localArrayList1,
					new String[] { "role_name" });
			ProjectDataPanel.loadPropertiesInBulk(localArrayList2,
					new String[] { "name", "parent" });
		}

		private ProjectDataPanel.TeamRoleUserTreeNode findRoleNode(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				TCComponentRole tccomponentrole) {
			int i = teamroleusertreenode.getChildCount();
			ProjectDataPanel.TeamRoleUserTreeNode projectteamchildrentreenode = null;
			for (int j = 0; j < i; j++) {
				projectteamchildrentreenode = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(j);

				if (projectteamchildrentreenode.type == 2
						&& projectteamchildrentreenode.comp == tccomponentrole)
					return projectteamchildrentreenode;
				projectteamchildrentreenode = null;
			}

			return projectteamchildrentreenode;
		}

		// private ProjectDataPanel.TeamRoleUserTreeNode addGroup(
		// DefaultTreeModel defaulttreemodel,
		// ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
		// TCComponentGroup tccomponentgroup) {
		// try {
		// DefaultMutableTreeNode projectteamchildrentreenode = new
		// DefaultMutableTreeNode(
		// teamroleusertreenode, true);
		// defaulttreemodel.insertNodeInto(projectteamchildrentreenode,
		// teamroleusertreenode,
		// teamroleusertreenode.getChildCount());
		// defaulttreemodel.nodeChanged(projectteamchildrentreenode);
		//
		// // ProjectTeamLoadingTreeController
		// // projectteamloadingtreecontroller = new
		// // ProjectTeamLoadingTreeController(
		// // defaulttreemodel);
		// // addTreeWillExpandListener(projectteamloadingtreecontroller);
		// return (ProjectDataPanel.TeamRoleUserTreeNode)
		// projectteamchildrentreenode;
		// } catch (Exception exception) {
		// return null;
		// }
		// }

		private ProjectDataPanel.TeamRoleUserTreeNode addGroup(
				DefaultTreeModel paramDefaultTreeModel,
				ProjectDataPanel.TeamRoleUserTreeNode paramTeamRoleUserTreeNode,
				TCComponentGroup paramTCComponentGroup) {
			try {
				ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode = new ProjectDataPanel.TeamRoleUserTreeNode(
						paramTCComponentGroup, paramTCComponentGroup.toString());
				paramDefaultTreeModel.insertNodeInto(localTeamRoleUserTreeNode,
						paramTeamRoleUserTreeNode,
						paramTeamRoleUserTreeNode.getChildCount());
				TCComponent[] arrayOfTCComponent1 = paramTCComponentGroup
						.getRolesAndGroups();
				for (TCComponent localTCComponent : arrayOfTCComponent1)
					if (localTCComponent instanceof TCComponentRole) {
						addRole(paramDefaultTreeModel,
								localTeamRoleUserTreeNode,
								(TCComponentRole) localTCComponent);
					} else {
						if (!(localTCComponent instanceof TCComponentGroup))
							continue;
						addGroup(paramDefaultTreeModel,
								localTeamRoleUserTreeNode,
								(TCComponentGroup) localTCComponent);
					}
				return localTeamRoleUserTreeNode;
			} catch (TCException localTCException) {
			}
			return null;
		}

		private ProjectDataPanel.TeamRoleUserTreeNode addRole(
				DefaultTreeModel paramDefaultTreeModel,
				ProjectDataPanel.TeamRoleUserTreeNode paramTeamRoleUserTreeNode,
				TCComponentRole paramTCComponentRole) {
			try {
				ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode1 = new ProjectDataPanel.TeamRoleUserTreeNode(
						paramTCComponentRole, paramTCComponentRole.toString());
				paramDefaultTreeModel.insertNodeInto(
						localTeamRoleUserTreeNode1, paramTeamRoleUserTreeNode,
						paramTeamRoleUserTreeNode.getChildCount());
				TCComponentGroup localTCComponentGroup = (TCComponentGroup) paramTeamRoleUserTreeNode.comp;
				TCComponent[] arrayOfTCComponent1 = getGroupMembersHelper(
						paramTCComponentRole, localTCComponentGroup);
				TCProperty[] arrayOfTCProperty = new TCProperty[arrayOfTCComponent1.length];
				for (int i = 0; i < arrayOfTCComponent1.length; ++i)
					arrayOfTCProperty[i] = arrayOfTCComponent1[i]
							.getTCProperty("the_user");
				if (arrayOfTCProperty.length > 0)
					TCProperty.getReferenceValues(arrayOfTCProperty);
				for (TCComponent localTCComponent : arrayOfTCComponent1) {
					ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode2 = new ProjectDataPanel.TeamRoleUserTreeNode(
							localTCComponent, localTCComponent.toString(),
							this.teamAdmin);
					if ((localTeamRoleUserTreeNode2.user != this.teamAdmin)
							&& (localTeamRoleUserTreeNode2.user != null))
						if (isPrivilegedUser(localTeamRoleUserTreeNode2.user))
							localTeamRoleUserTreeNode2.status = ProjectDataPanel.this.PRIVILEGED;
						else
							localTeamRoleUserTreeNode2.status = ProjectDataPanel.this.UNPRIVILEGED;
					paramDefaultTreeModel.insertNodeInto(
							localTeamRoleUserTreeNode2,
							localTeamRoleUserTreeNode1,
							localTeamRoleUserTreeNode1.getChildCount());
				}
				return localTeamRoleUserTreeNode1;
			} catch (TCException localTCException) {
			}
			return null;
		}

		private TCComponent[] getGroupMembersHelper(
				TCComponentRole tccomponentrole,
				TCComponentGroup tccomponentgroup) throws TCException {
			String s = (new StringBuilder(String.valueOf(tccomponentrole
					.getUid()))).append(tccomponentgroup.getUid()).toString();
			ArrayList arraylist = null;
			Object aobj[] = null;
			if (ProjectDataPanel.hashTableData == null)
				ProjectDataPanel.hashTableData = new Hashtable();
			if (ProjectDataPanel.hashTableData.containsKey(s))
				arraylist = (ArrayList) ProjectDataPanel.hashTableData.get(s);
			if (arraylist == null) {
				aobj = tccomponentrole.getGroupMembers(tccomponentgroup);
				arraylist = new ArrayList();
				TCComponentGroupMember atccomponentgroupmember[];
				int j = (atccomponentgroupmember = ((TCComponentGroupMember[]) (aobj))).length;
				for (int i = 0; i < j; i++) {
					TCComponentGroupMember tccomponentgroupmember = atccomponentgroupmember[i];
					arraylist.add(tccomponentgroupmember);
				}

				ProjectDataPanel.hashTableData.put(s, arraylist);
			} else {
				aobj = new TCComponent[arraylist.size()];
				aobj = (TCComponent[]) arraylist.toArray(aobj);
			}
			try {
				String as[] = { "the_user", "the_group", "the_role",
						"user_name", "object_name" };
				ArrayList arraylist1 = new ArrayList();
				for (int k = 0; k < aobj.length; k++)
					arraylist1.add(aobj[k]);

				ProjectDataPanel.loadPropertiesInBulk(arraylist1, as);
			} catch (Exception _ex) {
			}
			return ((TCComponent[]) (aobj));
		}

		private boolean isPrivilegedUser(TCComponentUser paramTCComponentUser) {
			boolean i = false;
			if (this.privilegedUsers != null)
				for (TCComponentUser user1 : this.privilegedUsers) {
					if (user1 != paramTCComponentUser)
						continue;
					i = true;
					break;
				}
			if (i == false) {
				if (this.allPrivUsers == null) {
					TCComponent[] localObject1 = getGroupMembersForProject(
							"Project Author",
							ProjectDataPanel.this.idField.getText());
					this.allPrivUsers = new ArrayList();
					for (Object localObject2 : localObject1)
						try {
							this.allPrivUsers
									.add(((TCComponentGroupMember) localObject2)
											.getUser());
						} catch (TCException localTCException) {
							Logger.getLogger(TeamRoleUserTree.class).error(
									localTCException.getLocalizedMessage(),
									localTCException);
						}
				}
				if (this.allPrivUsers.contains(paramTCComponentUser))
					i = true;
			}
			return i;
		}

		private TCComponent[] getGroupMembersForProject(String paramString1,
				String paramString2) {
			TCComponent[] arrayOfTCComponent = null;
			try {
				TCComponentQueryType localTCComponentQueryType = (TCComponentQueryType) this.session
						.getTypeComponent("ImanQuery");
				String str1 = "__EINT_group_members";
				TCComponentQuery localTCComponentQuery = (TCComponentQuery) localTCComponentQueryType
						.find(str1);
				String str2 = "*";
				String str3 = "Role";
				String str4 = "Group";
				String str5 = "User";
				try {
					TCQueryClause[] arrayOfTCQueryClause = localTCComponentQuery
							.describe();
					if ((arrayOfTCQueryClause != null)
							&& (arrayOfTCQueryClause.length == 3)) {
						str4 = arrayOfTCQueryClause[0]
								.getUserEntryNameDisplay();
						str3 = arrayOfTCQueryClause[1]
								.getUserEntryNameDisplay();
						str5 = arrayOfTCQueryClause[2]
								.getUserEntryNameDisplay();
					}
				} catch (Exception localException2) {
					ProjectDataPanel.logger.error(localException2.getClass()
							.getName(), localException2);
				}
				if (localTCComponentQuery != null) {
					String[] arrayOfString1 = { str3, str4, str5 };
					String[] arrayOfString2 = { paramString1, paramString2,
							str2 };
					arrayOfTCComponent = localTCComponentQuery.execute(
							arrayOfString1, arrayOfString2);
				}
			} catch (Exception localException1) {
				ProjectDataPanel.logger.error(localException1.getClass()
						.getName(), localException1);
			}
			return arrayOfTCComponent;
		}

		public TCComponentUser[] getUsers(boolean flag) {
			return getUsers(ALL_USERS, flag);
		}

		public TCComponentUser[] getPrivilegedUsers(boolean flag) {
			return getUsers(PRIV_USERS, flag);
		}

		public TCComponentUser[] getUnprivilegedUsers() {
			return getUsers(UNPRIV_USERS, false);
		}

		private void loadTheWholeTree(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				DefaultTreeModel defaulttreemodel, Vector vector, int i) {
			if (teamroleusertreenode == null)
				return;
			if (!teamroleusertreenode.isRoot())
				loadChildrenHelper(teamroleusertreenode);
			int j = teamroleusertreenode.getChildCount();
			for (int k = 0; k < j; k++) {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(k);
				if (teamroleusertreenode1.type == 3
						|| teamroleusertreenode1.type == 4) {
					TCComponentUser tccomponentuser = teamroleusertreenode1.user;
					if ((i == ALL_USERS
							|| i == PRIV_USERS
							&& teamroleusertreenode1.status != ProjectDataPanel.UNPRIVILEGED || i == UNPRIV_USERS
							&& teamroleusertreenode1.status == ProjectDataPanel.UNPRIVILEGED)
							&& tccomponentuser != null
							&& !vector.contains(tccomponentuser))
						vector.add(tccomponentuser);
				}
				if (!teamroleusertreenode1.isLeaf())
					loadTheWholeTree(teamroleusertreenode1, defaulttreemodel,
							vector, i);
			}

		}

		private TCComponentUser[] getUsers(int i, boolean flag) {
			if (i == PRIV_USERS && ProjectDataPanel.allPrivUsers != null)
				return (TCComponentUser[]) ProjectDataPanel.allPrivUsers
						.toArray(new TCComponentUser[ProjectDataPanel.allPrivUsers
								.size()]);
			DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
			ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) defaulttreemodel
					.getRoot();
			Vector vector = new Vector();
			loadTheWholeTree(teamroleusertreenode, defaulttreemodel, vector, i);
			int j = teamroleusertreenode.getChildCount();
			for (int k = 0; k < j; k++) {
				ProjectDataPanel.TeamRoleUserTreeNode projectteamchildrentreenode = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(k);
				if (projectteamchildrentreenode.type == 0) {
					loadChildrenHelper(projectteamchildrentreenode);
					getGroupUsers(projectteamchildrentreenode, i, vector);
					continue;
				}
				if (projectteamchildrentreenode.type == 2) {
					loadChildrenHelper(projectteamchildrentreenode);
					getRoleUsers(projectteamchildrentreenode, i, vector);
				}
			}

			if (flag && (i == ALL_USERS || i == PRIV_USERS)
					&& ProjectDataPanel.teamAdmin != null
					&& !vector.contains(ProjectDataPanel.teamAdmin))
				vector.add(ProjectDataPanel.teamAdmin);
			TCComponentUser atccomponentuser[] = new TCComponentUser[vector
					.size()];
			vector.toArray(atccomponentuser);
			if (Debug.isOn("project"))
				Debug.println((new StringBuilder())
						.append("geting priv users: useroption= ").append(i)
						.append(" add-current-user=").append(flag)
						.append(" user count= ")
						.append(atccomponentuser.length).toString());
			return atccomponentuser;
		}

		
		private void getGroupUsers(
				ProjectDataPanel.TeamRoleUserTreeNode paramTeamRoleUserTreeNode,
				int paramInt, Vector paramVector) {
			int i = paramTeamRoleUserTreeNode.getChildCount();
			for (int j = 0; j < i; ++j) {
				ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode = (ProjectDataPanel.TeamRoleUserTreeNode) paramTeamRoleUserTreeNode
						.getChildAt(j);
				if (localTeamRoleUserTreeNode.type == 2) {
					getRoleUsers(localTeamRoleUserTreeNode, paramInt,
							paramVector);
				} else {
					if (localTeamRoleUserTreeNode.type != 0)
						continue;
					getGroupUsers(localTeamRoleUserTreeNode, paramInt,
							paramVector);
				}
			}
		}
		
//		private void getGroupUsers2(
//				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
//				int i, Vector vector) {
//			int j = teamroleusertreenode.getChildCount();
//			for (int k = 0; k < j; k++) {
//				if (!(teamroleusertreenode.getChildAt(k) instanceof ProjectDataPanel.TeamRoleUserTreeNode)) {
//					loadChildrenHelper(teamroleusertreenode, k);
//					getGroupUsers(teamroleusertreenode, i, vector);
//					return;
//				}
//				ProjectDataPanel.TeamRoleUserTreeNode projectteamchildrentreenode = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
//						.getChildAt(k);
//				if (projectteamchildrentreenode.type == 2) {
//					getRoleUsers(projectteamchildrentreenode, i, vector);
//					continue;
//				}
//				if (projectteamchildrentreenode.type == 0)
//					getGroupUsers(((projectteamchildrentreenode)), i, vector);
//			}
//		}

		// modify by wuwei
		private void getRoleUsers(
				ProjectDataPanel.TeamRoleUserTreeNode paramTeamRoleUserTreeNode,
				int paramInt, Vector paramVector) {
			int i = paramTeamRoleUserTreeNode.getChildCount();
			for (int j = 0; j < i; ++j) {
				ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode = (ProjectDataPanel.TeamRoleUserTreeNode) paramTeamRoleUserTreeNode
						.getChildAt(j);
				TCComponentUser localTCComponentUser = localTeamRoleUserTreeNode.user;
				if (((paramInt != this.ALL_USERS)
						&& (((paramInt != this.PRIV_USERS) || (localTeamRoleUserTreeNode.status == ProjectDataPanel.this.UNPRIVILEGED))) && (((paramInt != this.UNPRIV_USERS) || (localTeamRoleUserTreeNode.status != ProjectDataPanel.this.UNPRIVILEGED))))
						|| (localTCComponentUser == null)
						|| (paramVector.contains(localTCComponentUser)))
					continue;
				paramVector.add(localTCComponentUser);
			}
		}

		// private void getRoleUsers(TeamRoleUserTreeNode teamroleusertreenode,
		// int i, Vector vector) {
		// int j = teamroleusertreenode.getChildCount();
		// if (j == 0) {
		// DefaultMutableTreeNode aprojectteamchildrentreenode[] =
		// teamroleusertreenode
		// .loadChildren((DefaultTreeModel) getModel());
		// boolean flag = aprojectteamchildrentreenode != null
		// && aprojectteamchildrentreenode.length > 0;
		// teamroleusertreenode.setAllowsChildren(flag);
		// teamroleusertreenode.setChildren(aprojectteamchildrentreenode);
		// j = teamroleusertreenode.getChildCount();
		// }
		// for (int k = 0; k < j; k++) {
		// if (!(teamroleusertreenode.getChildAt(k) instanceof
		// DefaultMutableTreeNode)) {
		// loadChildrenHelper(teamroleusertreenode, k);
		// getRoleUsers(teamroleusertreenode, i, vector);
		// return;
		// }
		// TeamRoleUserTreeNode projectteamchildrentreenode =
		// (TeamRoleUserTreeNode) teamroleusertreenode
		// .getChildAt(k);
		// TCComponentUser tccomponentuser = projectteamchildrentreenode.user;
		// if ((i == ALL_USERS
		// || i == PRIV_USERS
		// && projectteamchildrentreenode.status !=
		// ProjectDataPanel.UNPRIVILEGED || i == UNPRIV_USERS
		// && projectteamchildrentreenode.status ==
		// ProjectDataPanel.UNPRIVILEGED)
		// && tccomponentuser != null
		// && !vector.contains(tccomponentuser))
		// vector.add(tccomponentuser);
		// }
		//
		// }

//		private void loadChildrenHelper(
//				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
//				int i) {
//			boolean flag = teamroleusertreenode.getChildAt(i) instanceof DefaultMutableTreeNode;
//			while (!flag) {
//				ProjectDataPanel.TeamRoleUserTreeNode aprojectteamchildrentreenode[] = teamroleusertreenode
//						.getNextNode().loadChildren(
//								(DefaultTreeModel) getModel());
//				boolean flag1 = aprojectteamchildrentreenode != null
//						&& aprojectteamchildrentreenode.length > 0;
//				teamroleusertreenode.setAllowsChildren(flag1);
//				teamroleusertreenode.setChildren(aprojectteamchildrentreenode);
//				if (flag1)
//					flag = teamroleusertreenode.getChildAt(i) instanceof DefaultMutableTreeNode;
//				else
//					flag = true;
//			}
//		}

		private TeamRoleUserTreeNode[] addGroupMembers(
				TCComponentGroupMember atccomponentgroupmember[]) {
			ArrayList arraylist = new ArrayList();
			if (atccomponentgroupmember != null) {
				TCComponentGroupMember atccomponentgroupmember1[];
				int j = (atccomponentgroupmember1 = atccomponentgroupmember).length;
				for (int i = 0; i < j; i++) {
					TCComponentGroupMember tccomponentgroupmember = atccomponentgroupmember1[i];
					arraylist.add(tccomponentgroupmember);
				}

			}
			String as[] = { "the_user", "the_group", "the_role", "user_name",
					"object_name" };
			ProjectDataPanel.loadPropertiesInBulk(arraylist, as);
			TeamRoleUserTreeNode ateamroleusertreenode[] = null;
			if (atccomponentgroupmember != null
					&& atccomponentgroupmember.length > 0) {
				int k = atccomponentgroupmember.length;
				ateamroleusertreenode = new TeamRoleUserTreeNode[k];
				for (int l = 0; l < k; l++)
					ateamroleusertreenode[l] = addGroupMember(atccomponentgroupmember[l]);

			}
			return ateamroleusertreenode;
		}

		private TeamRoleUserTreeNode addGroupMember(
				TCComponentGroupMember paramTCComponentGroupMember) {
			DefaultTreeModel localDefaultTreeModel = (DefaultTreeModel) getModel();
			ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode1 = (ProjectDataPanel.TeamRoleUserTreeNode) localDefaultTreeModel
					.getRoot();
			if (duplicatedGroupMember(localTeamRoleUserTreeNode1,
					paramTCComponentGroupMember, 0)) {
				if (Debug.isOn("project"))
					Debug.println("WARNING: duplicated group member="
							+ paramTCComponentGroupMember.toString());
				return null;
			}
			TCComponentRole localTCComponentRole = null;
			TCComponentUser localTCComponentUser = null;
			
			try {
				localTCComponentRole = paramTCComponentGroupMember.getRole();
				localTCComponentUser = paramTCComponentGroupMember.getUser();
			} catch (TCException localTCException1) {
				localTCException1.printStackTrace();
			}
			
			Object localObject1;
			if ((localTCComponentRole == null)
					|| (localTCComponentUser == null)) {
				if (Debug.isOn("project")) {
					localObject1 = null;
					try {
						localObject1 = paramTCComponentGroupMember.getGroup();
					} catch (TCException localTCException2) {
						localTCException2.printStackTrace();
					}
					
					Debug.println("WARNING: group member has role  "
							+ ((localTCComponentRole != null) ? localTCComponentRole
									.toString() : "null")
							+ " and user "
							+ ((localTCComponentUser != null) ? localTCComponentUser
									.toString() : "null")
							+ " and group "
							+ ((localObject1 != null) ? ((TCComponentGroup) localObject1)
									.toString() : "null"));
				}
				return null;
			}
			int i = localTeamRoleUserTreeNode1.getChildCount();
			Object localObject2 = null;
			for (int j = 0; j < i; ++j) {
				ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode3 = (ProjectDataPanel.TeamRoleUserTreeNode) localTeamRoleUserTreeNode1
						.getChildAt(j);
				if ((localTeamRoleUserTreeNode3.type != 2)
						|| (localTeamRoleUserTreeNode3.comp != localTCComponentRole))
					continue;
				localObject2 = localTeamRoleUserTreeNode3;
				break;
			}
			if (localObject2 != null) {
				localObject1 = localObject2;
			} else {
				localObject1 = new ProjectDataPanel.TeamRoleUserTreeNode(
						localTCComponentRole, localTCComponentRole.toString());
				localDefaultTreeModel.insertNodeInto(
						(MutableTreeNode) localObject1,
						localTeamRoleUserTreeNode1,
						localTeamRoleUserTreeNode1.getChildCount());
			}
			ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode2 = new ProjectDataPanel.TeamRoleUserTreeNode(
					paramTCComponentGroupMember,
					paramTCComponentGroupMember.toString(), this.teamAdmin);
			if ((localTeamRoleUserTreeNode2.user != this.teamAdmin)
					&& (localTeamRoleUserTreeNode2.user != null))
				if (isPrivilegedUser(localTeamRoleUserTreeNode2.user))
					localTeamRoleUserTreeNode2.status = ProjectDataPanel.this.PRIVILEGED;
				else
					localTeamRoleUserTreeNode2.status = ProjectDataPanel.this.UNPRIVILEGED;
			localDefaultTreeModel.insertNodeInto(localTeamRoleUserTreeNode2,
					(MutableTreeNode) localObject1,
					((ProjectDataPanel.TeamRoleUserTreeNode) localObject1)
							.getChildCount());
			return ((ProjectDataPanel.TeamRoleUserTreeNode) localTeamRoleUserTreeNode2);
		}

		private boolean duplicatedGroupMember(
				ProjectDataPanel.TeamRoleUserTreeNode paramTeamRoleUserTreeNode,
				TCComponentGroupMember paramTCComponentGroupMember, int paramInt) {
			int i = paramTeamRoleUserTreeNode.getChildCount();
			for (int j = 0; j < i; ++j) {
				ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode1 = (ProjectDataPanel.TeamRoleUserTreeNode) paramTeamRoleUserTreeNode
						.getChildAt(j);
				if (localTeamRoleUserTreeNode1.type == 2) {
					int k = localTeamRoleUserTreeNode1.getChildCount();
					for (int l = 0; l < k; ++l) {
						ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode2 = (ProjectDataPanel.TeamRoleUserTreeNode) localTeamRoleUserTreeNode1
								.getChildAt(l);
						if ((localTeamRoleUserTreeNode2.comp == paramTCComponentGroupMember)
								|| ((paramInt == 0) && (identicalGroupMember(
										localTeamRoleUserTreeNode2.comp,
										paramTCComponentGroupMember))))
							return true;
					}
				} else if (duplicatedGroupMember(localTeamRoleUserTreeNode1,
						paramTCComponentGroupMember, paramInt + 1)) {
					return true;
				}
			}
			return false;
		}

		private boolean identicalGroupMember(TCComponent paramTCComponent1,
				TCComponent paramTCComponent2) {
			boolean i = false;
			if ((paramTCComponent1 instanceof TCComponentGroupMember)
					&& (paramTCComponent2 instanceof TCComponentGroupMember))
				try {
					TCComponentRole localTCComponentRole1 = ((TCComponentGroupMember) paramTCComponent1)
							.getRole();
					TCComponentRole localTCComponentRole2 = ((TCComponentGroupMember) paramTCComponent2)
							.getRole();
					TCComponentUser localTCComponentUser1 = ((TCComponentGroupMember) paramTCComponent1)
							.getUser();
					TCComponentUser localTCComponentUser2 = ((TCComponentGroupMember) paramTCComponent2)
							.getUser();
					TCComponentGroup localTCComponentGroup1 = ((TCComponentGroupMember) paramTCComponent1)
							.getGroup();
					TCComponentGroup localTCComponentGroup2 = ((TCComponentGroupMember) paramTCComponent2)
							.getGroup();
					if ((localTCComponentRole1 == localTCComponentRole2)
							&& (localTCComponentUser1 == localTCComponentUser2)
							&& (localTCComponentGroup1 == localTCComponentGroup2))
						i = true;
				} catch (TCException localTCException) {
				}
			return i;
		}

		private void loadChildrenHelper(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode) {

			// DefaultMutableTreeNode
			// ProjectTeamChildrenTreeNode aprojectteamchildrentreenode[] =
			// teamroleusertreenode
			// .loadChildren((DefaultTreeModel) getModel());
			// boolean flag = aprojectteamchildrentreenode != null
			// && aprojectteamchildrentreenode.length > 0;
			teamroleusertreenode.setAllowsChildren(true);
			// teamroleusertreenode.setChildren(aprojectteamchildrentreenode);
		}

		public TCComponentUser getTeamAdmin(boolean flag) {
			if (flag && ProjectDataPanel.teamAdmin == null)
				ProjectDataPanel.teamAdmin = session.getUser();
			if (Debug.isOn("project"))
				Debug.println((new StringBuilder())
						.append("geting team admin: ")
						.append(ProjectDataPanel.teamAdmin == null ? "null"
								: ProjectDataPanel.teamAdmin.toString())
						.append("addLoginUser= ")
						.append(flag ? "true" : "false").toString());
			return ProjectDataPanel.teamAdmin;
		}

		public TCComponent[] getTeamMembers() {
			DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
			ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) defaulttreemodel
					.getRoot();
			int i = teamroleusertreenode.getChildCount();
			Vector vector = new Vector();
			for (int j = 0; j < i; j++) {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(j);
				if (teamroleusertreenode1.type == 0) {
					vector.add(teamroleusertreenode1.comp);
					continue;
				}
				if (teamroleusertreenode1.type == 2) {
					loadChildrenHelper(teamroleusertreenode1);
					getRoleMembers(teamroleusertreenode1, vector);
				}
			}

			TCComponentUser tccomponentuser = getPAUser();
			if (tccomponentuser != null && !isTeamMember(tccomponentuser)
					&& !vector.contains(tccomponentuser)) {
				vector.add(tccomponentuser);
				if (Debug.isOn("project"))
					Debug.println((new StringBuilder())
							.append("adding PA User: ")
							.append(tccomponentuser.toString()).toString());
			}
			TCComponentUser tccomponentuser1 = getTeamAdmin(true);
			if (tccomponentuser1 != null && !isTeamMember(tccomponentuser1)
					&& !vector.contains(tccomponentuser1)) {
				vector.add(tccomponentuser1);
				if (Debug.isOn("project"))
					Debug.println((new StringBuilder())
							.append("adding PTA User: ")
							.append(tccomponentuser1.toString()).toString());
			}
			TCComponent atccomponent[] = new TCComponent[vector.size()];
			vector.toArray(atccomponent);
			if (Debug.isOn("project"))
				Debug.println((new StringBuilder())
						.append("team members count: ")
						.append(atccomponent.length).toString());
			return atccomponent;
		}

		private void getRoleMembers(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				Vector vector) {
			int i = teamroleusertreenode.getChildCount();
			for (int j = 0; j < i; j++) {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(j);
				vector.add(teamroleusertreenode1.comp);
			}

		}

		public void setPrivilegedUsers(TCComponentUser atccomponentuser[]) {
			ProjectDataPanel.privilegedUsers = atccomponentuser;
			ProjectDataPanel.allPrivUsers = new ArrayList();
			TCComponentUser atccomponentuser1[] = ProjectDataPanel.privilegedUsers;
			int i = atccomponentuser1.length;
			for (int j = 0; j < i; j++) {
				TCComponentUser tccomponentuser = atccomponentuser1[j];
				ProjectDataPanel.allPrivUsers.add(tccomponentuser);
			}

			modified = true;
		}
		
		
		private void setPrivilegedUsersForGroup(
				ProjectDataPanel.TeamRoleUserTreeNode paramTeamRoleUserTreeNode,
				int paramInt, TCComponentUser[] paramArrayOfTCComponentUser) {
			int i = paramTeamRoleUserTreeNode.getChildCount();
			for (int j = 0; j < i; ++j) {
				ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode = (ProjectDataPanel.TeamRoleUserTreeNode) paramTeamRoleUserTreeNode
						.getChildAt(j);
				if (localTeamRoleUserTreeNode.type == 2) {
					setPrivilegedUsersForRole(localTeamRoleUserTreeNode,
							paramInt, paramArrayOfTCComponentUser);
				} else {
					if (localTeamRoleUserTreeNode.type != 0)
						continue;
					setPrivilegedUsersForGroup(localTeamRoleUserTreeNode,
							paramInt, paramArrayOfTCComponentUser);
				}
			}
		}

//		private void setPrivilegedUsersForGroup2(
//				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
//				int i, TCComponentUser atccomponentuser[]) {
//			int j = teamroleusertreenode.getChildCount();
//			for (int k = 0; k < j; k++) {
//				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
//						.getChildAt(k);
//
//				//
//				if (!(teamroleusertreenode.getChildAt(k) instanceof DefaultMutableTreeNode)) {
//					loadChildrenHelper(teamroleusertreenode, k);
//					setPrivilegedUsersForGroup(teamroleusertreenode, i,
//							atccomponentuser);
//				}
//				if (teamroleusertreenode1.type == 2) {
//					setPrivilegedUsersForRole(teamroleusertreenode1, i,
//							atccomponentuser);
//					continue;
//				}
//				if (teamroleusertreenode1.type == 0)
//					setPrivilegedUsersForGroup(teamroleusertreenode1, i,
//							atccomponentuser);
//			}
//
//		}

		private void setPrivilegedUsersForRole(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				int i, TCComponentUser atccomponentuser[]) {
			int j = teamroleusertreenode.getChildCount();
			for (int k = 0; k < j; k++) {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(k);
				boolean flag = false;
				if (atccomponentuser != null) {
					TCComponentUser atccomponentuser1[] = atccomponentuser;
					int l = atccomponentuser1.length;
					int i1 = 0;
					do {
						if (i1 >= l)
							break;
						TCComponentUser tccomponentuser = atccomponentuser1[i1];
						if (teamroleusertreenode1.user == tccomponentuser) {
							flag = true;
							break;
						}
						i1++;
					} while (true);
				}
				if ((i == RESET_ALL || i == SET_SELECTED)
						&& flag
						&& teamroleusertreenode1.status == ProjectDataPanel.UNPRIVILEGED) {
					teamroleusertreenode1.status = ProjectDataPanel.PRIVILEGED;
					// .setStatus(ProjectDataPanel.PRIVILEGED);
					modified = true;
					continue;
				}
				if ((i == RESET_ALL && !flag || i == UNSET_SELECTED && flag)
						&& teamroleusertreenode1.status == ProjectDataPanel.PRIVILEGED) {
					teamroleusertreenode1.status = ProjectDataPanel.UNPRIVILEGED;
					// .setStatus(ProjectDataPanel.UNPRIVILEGED);
					modified = true;
				}
			}

		}

		public void setTeamAdmin(TCComponentUser tccomponentuser) {
			if (ProjectDataPanel.teamAdmin != tccomponentuser) {
				modified = true;
				ProjectDataPanel.teamAdmin = tccomponentuser;
				DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) defaulttreemodel
						.getRoot();
				int i = teamroleusertreenode.getChildCount();
				for (int j = 0; j < i; j++) {
					ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
							.getChildAt(j);
					if (teamroleusertreenode1.type == 0) {
						setTeamAdminForGroup(teamroleusertreenode1,
								tccomponentuser);
						continue;
					}
					if (teamroleusertreenode1.type == 2)
						setTeamAdminForRole(teamroleusertreenode1,
								tccomponentuser);
				}

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						invalidate();
						repaint();
					}

				});
			}
		}

		private void setTeamAdminForGroup(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				TCComponentUser tccomponentuser) {
			int i = teamroleusertreenode.getChildCount();
			for (int j = 0; j < i; j++) {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(j);
				if (teamroleusertreenode1.type == 2) {
					setTeamAdminForRole(teamroleusertreenode1, tccomponentuser);
					continue;
				}
				if (teamroleusertreenode1.type == 0)
					setTeamAdminForGroup(teamroleusertreenode1, tccomponentuser);
			}

		}

		private void setTeamAdminForRole(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				TCComponentUser tccomponentuser) {
			int i = teamroleusertreenode.getChildCount();
			for (int j = 0; j < i; j++) {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
						.getChildAt(j);
				if (teamroleusertreenode1.user == tccomponentuser
						&& teamroleusertreenode1.status != ProjectDataPanel.TEAMADMIN) {
					teamroleusertreenode1.status = ProjectDataPanel.TEAMADMIN;
					modified = true;
					continue;
				}
				if (teamroleusertreenode1.user != tccomponentuser
						&& teamroleusertreenode1.status == ProjectDataPanel.TEAMADMIN) {
					teamroleusertreenode1.status = ProjectDataPanel.PRIVILEGED;
					// .setStatus(ProjectDataPanel.PRIVILEGED);
					modified = true;
				}
			}

		}

		public boolean teamDefined() {
			DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
			ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) defaulttreemodel
					.getRoot();
			int i = teamroleusertreenode.getChildCount();
			boolean flag = false;
			if (i > 0)
				flag = true;
			return flag;
		}

		public boolean teamModified() {
			return modified;
		}

		public void teamModified(boolean flag) {
			modified = flag;
		}

		public void removeSelectedMembers() {
			TreePath atreepath[] = getSelectionPaths();
			for (int i = 0; atreepath != null && i < atreepath.length; i++) {
				TreePath treepath = atreepath[i];
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) treepath
						.getLastPathComponent();
				if (validForDelete(teamroleusertreenode))
					removeSelected(teamroleusertreenode);
			}

			if (ProjectDataPanel.teamAdmin != null
					&& !isTeamMember(ProjectDataPanel.teamAdmin)) {
				if (Debug.isOn("project"))
					Debug.println((new StringBuilder())
							.append("removing PTA User: ")
							.append(ProjectDataPanel.teamAdmin.toString())
							.toString());
				ProjectDataPanel.teamAdmin = null;
			}
		}

		public void removeSelected(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode) {
			try {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = teamroleusertreenode;
				// ProjectTeamChildrenTreeNode projectteamchildrentreenode =
				// (ProjectTeamChildrenTreeNode) teamroleusertreenode;

				DefaultMutableTreeNode projectteamchildrentreenode = (DefaultMutableTreeNode) teamroleusertreenode;
				if (teamroleusertreenode.type == 4) {
					TCComponentGroupMember tccomponentgroupmember = (TCComponentGroupMember) teamroleusertreenode.comp;
					// projectteamchildrentreenode
					// .removeGroupMember(tccomponentgroupmember);

					// add by wuwei
					projectteamchildrentreenode.removeAllChildren();

					if (ProjectDataPanel.allPrivUsers != null)
						ProjectDataPanel.allPrivUsers
								.remove(tccomponentgroupmember.getUser());
				}
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode2 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode1
						.getParent();
				int i = teamroleusertreenode2.getLevel();
				if (i == 1 && teamroleusertreenode2.type == 2
						&& teamroleusertreenode2.getChildCount() == 1) {
					teamroleusertreenode1 = teamroleusertreenode2;
					teamroleusertreenode2 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode1
							.getParent();
				}
				if (Debug.isOn("project"))
					Debug.println((new StringBuilder())
							.append("deleting node: ")
							.append(teamroleusertreenode1.toString())
							.append(" from node: ")
							.append(teamroleusertreenode2.toString())
							.toString());
				int j = teamroleusertreenode2.getIndex(teamroleusertreenode1);
				if (j > 0)
					j--;
				DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
				defaulttreemodel.removeNodeFromParent(teamroleusertreenode1);
				TreePath treepath;
				if (teamroleusertreenode2.getChildCount() > 0) {
					DefaultMutableTreeNode defaultmutabletreenode = (DefaultMutableTreeNode) teamroleusertreenode2
							.getChildAt(j);
					treepath = new TreePath(defaultmutabletreenode.getPath());
				} else {
					treepath = new TreePath(teamroleusertreenode2.getPath());
				}
				setSelectionPath(treepath);
				modified = true;
			} catch (Exception exception) {
				MessageBox.post(exception);
			}
		}

		public void startSearchOperation(final String s) {
			System.out.println("startSearchOperation");
			if (s != null && s.trim().length() > 0)
				session.queueOperation(new AbstractAIFOperation(s) {

					@Override
					public void executeOperation() throws Exception {
						DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
						ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) defaulttreemodel
								.getRoot();
						setCursor(Cursor.getPredefinedCursor(3));
						if (matchedNodes == null)
							matchedNodes = new Vector();
						else
							matchedNodes.removeAllElements();
						findMatchedNodes(teamroleusertreenode, s.trim()
								.toLowerCase());
						matchedNodes.addElement(Integer.valueOf(matchedNodes
								.size()));
						setCursor(Cursor.getPredefinedCursor(0));
						selectMatchedNode();
					}

				});
			else if (matchedNodes != null)
				matchedNodes.removeAllElements();
		}

		public void selectMatchedNode() {
			if (matchedNodes.size() <= 1) {
				setNoFoundMessage();
				session.setReadyStatus();
				return;
			} else {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) matchedNodes
						.firstElement();
				TreePath treepath = new TreePath(teamroleusertreenode.getPath());
				expandPath(treepath);
				setSelectionPath(treepath);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						TreePath treepath1 = getSelectionPath();
						scrollPathToVisible(treepath1);
					}

				});
				matchedNodes.removeElementAt(0);
				Integer integer = (Integer) matchedNodes.lastElement();
				int i = (integer.intValue() - matchedNodes.size()) + 1;
				setFoundMessage(integer, i);
				session.setReadyStatus();
				return;
			}
		}

		private void findMatchedNodes(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				String s) {
			System.out.println("findMatchedNodes ----->teamroleusertreenode-->"
					+ teamroleusertreenode + "<--");
			System.out.println("teamroleusertreenode.name--->"
					+ teamroleusertreenode.name + "<--");
			System.out.println("teamroleusertreenode.toString()--->"
					+ teamroleusertreenode.toString());
			if (teamroleusertreenode.name != null) {
				String s1 = teamroleusertreenode.name.toLowerCase();
				WildcardStringMatcher wildcardstringmatcher = new WildcardStringMatcher(
						s);
				if (s1.indexOf(s) >= 0 || wildcardstringmatcher.match(s1))
					matchedNodes.addElement(teamroleusertreenode);
			}
			ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1;
			for (Enumeration enumeration = teamroleusertreenode.children(); enumeration
					.hasMoreElements(); findMatchedNodes(teamroleusertreenode1,
					s))
				teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) enumeration
						.nextElement();

		}

		private void setFoundMessage(Integer integer, int i) {
			session.setStatus((new StringBuilder()).append(i).append("/")
					.append(integer).append(" ")
					.append(reg.getString("found.MSG", "found")).toString());
		}

		private void setNoFoundMessage() {
			session.setStatus((new StringBuilder()).append("0 ")
					.append(reg.getString("found.MSG", "found")).toString());
			MessageBox.post(Utilities.getCurrentFrame(), reg.getString(
					"notFound.MSG",
					"No objects were located using the specified criteria."),
					reg.getString("search.TITLE"), 2);
		}

		public void projectModified() {
			final DefaultTreeModel treeModel = (DefaultTreeModel) getModel();
			final ProjectDataPanel.TeamRoleUserTreeNode root = (ProjectDataPanel.TeamRoleUserTreeNode) treeModel
					.getRoot();
			root.name = root.comp.toString();
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					treeModel.nodeChanged(root);
				}

			});
		}

		public boolean isTeamMember(TCComponentUser tccomponentuser) {
			DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
			ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) defaulttreemodel
					.getRoot();
			try {
				TCPreferenceService tcpreferenceservice = session
						.getPreferenceService();
				String s = "";
				try {
					s = tcpreferenceservice.getString(1,
							"TC_suppress_inactive_group_members");
					tcpreferenceservice.setString(1,
							"TC_suppress_inactive_group_members", "0");
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				TCComponentGroupMember atccomponentgroupmember[] = tccomponentuser
						.getGroupMembers();
				try {
					tcpreferenceservice.setString(1,
							"TC_suppress_inactive_group_members", s);
				} catch (Exception exception1) {
					exception1.printStackTrace();
				}
				if (atccomponentgroupmember != null
						&& atccomponentgroupmember.length > 0)
					return isTeamMember(teamroleusertreenode,
							atccomponentgroupmember);
			} catch (TCException tcexception) {
			}
			return false;
		}

		private boolean isTeamMember(
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode,
				TCComponentGroupMember atccomponentgroupmember[]) {
			ProjectDataPanel.TeamRoleUserTreeNode localTeamRoleUserTreeNode = null;
			Enumeration localEnumeration = teamroleusertreenode.children();
			do {
				TCComponentGroupMember[] arrayOfTCComponentGroupMember;
				int j;
				while (true) {
					if (!(localEnumeration.hasMoreElements()))
						return false;
					localTeamRoleUserTreeNode = (ProjectDataPanel.TeamRoleUserTreeNode) localEnumeration
							.nextElement();
					if (localTeamRoleUserTreeNode.type != 4)
						break;
					arrayOfTCComponentGroupMember = atccomponentgroupmember;
					int i = arrayOfTCComponentGroupMember.length;
					for (j = 0; j < i; ++j) {
						TCComponentGroupMember localTCComponentGroupMember = arrayOfTCComponentGroupMember[j];
						if (localTCComponentGroupMember == localTeamRoleUserTreeNode.comp)
							return true;
					}
				}
			} while (!(isTeamMember(localTeamRoleUserTreeNode,
					atccomponentgroupmember)));
			return true;
		}

		private TCComponentUser getPAUser() {
			TCComponentUser tccomponentuser = null;
			try {
				if (project == null)
					tccomponentuser = session.getUser();
				else
					tccomponentuser = project.getPAUser();
			} catch (TCException tcexception) {
			}
			return tccomponentuser;
		}

		private void showPrivUserPopup(MouseEvent mouseevent) {
			int i = mouseevent.getModifiers();
			TeamRoleUserTree teamroleusertree = (TeamRoleUserTree) mouseevent
					.getSource();
			if (i <= 6) {
				TreePath atreepath[] = getSelectionPaths();
				if (atreepath == null || atreepath.length == 1) {
					int j = teamroleusertree.getRowForLocation(
							mouseevent.getX(), mouseevent.getY());
					if (j != -1) {
						if (mouseevent.getClickCount() == 1) {
							teamroleusertree.clearSelection();
							teamroleusertree.addSelectionRow(j);
						}
					} else {
						teamroleusertree.clearSelection();
					}
				}
				atreepath = getSelectionPaths();
				if (atreepath == null || atreepath.length == 0)
					return;
				ProjectDataPanel.TeamRoleUserTreeNode ateamroleusertreenode[] = new ProjectDataPanel.TeamRoleUserTreeNode[atreepath.length];
				for (int k = 0; k < atreepath.length; k++) {
					TreePath treepath = atreepath[k];
					if (treepath != null)
						ateamroleusertreenode[k] = (ProjectDataPanel.TeamRoleUserTreeNode) treepath
								.getLastPathComponent();
					else
						ateamroleusertreenode[k] = null;
				}

				PrivUserPopupMenu privuserpopupmenu = new PrivUserPopupMenu(
						teamroleusertree, ateamroleusertreenode);
				privuserpopupmenu.show((Component) mouseevent.getSource(),
						mouseevent.getX(), mouseevent.getY());
			}
		}

		public void changePrivilegedUsers(
				ProjectDataPanel.TeamRoleUserTreeNode ateamroleusertreenode[],
				boolean flag) {
			Vector vector = new Vector();
			for (int i = 0; ateamroleusertreenode != null
					&& i < ateamroleusertreenode.length; i++) {
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = ateamroleusertreenode[i];
				if (teamroleusertreenode == null)
					continue;
				if (teamroleusertreenode.type == 0) {
					loadChildrenHelper(teamroleusertreenode);
					getGroupUsers(teamroleusertreenode, ALL_USERS, vector);
					continue;
				}
				if (teamroleusertreenode.type == 2) {
					loadChildrenHelper(teamroleusertreenode);
					getRoleUsers(teamroleusertreenode, ALL_USERS, vector);
					continue;
				}
				if (teamroleusertreenode.type != 3
						&& teamroleusertreenode.type != 4)
					continue;
				TCComponentUser tccomponentuser = teamroleusertreenode.user;
				if (tccomponentuser != null
						&& !vector.contains(tccomponentuser))
					vector.add(tccomponentuser);
			}

			if (vector.size() > 0) {
				TCComponentUser atccomponentuser[] = new TCComponentUser[vector
						.size()];
				vector.toArray(atccomponentuser);
				DefaultTreeModel defaulttreemodel = (DefaultTreeModel) getModel();
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) defaulttreemodel
						.getRoot();
				int j = teamroleusertreenode1.getChildCount();
				int k = flag ? SET_SELECTED : UNSET_SELECTED;
				for (int l = 0; l < j; l++) {
					ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode2 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode1
							.getChildAt(l);
					if (teamroleusertreenode2.type == 0) {
						setPrivilegedUsersForGroup(teamroleusertreenode2, k,
								atccomponentuser);
						continue;
					}
					if (teamroleusertreenode2.type == 2)
						setPrivilegedUsersForRole(teamroleusertreenode2, k,
								atccomponentuser);
				}

				if (ProjectDataPanel.privilegedUsers == null) {
					ProjectDataPanel.privilegedUsers = getPrivilegedUsers(false);
				} else {
					vector.clear();
					TCComponentUser atccomponentuser1[] = ProjectDataPanel.privilegedUsers;
					int i1 = atccomponentuser1.length;
					for (int j1 = 0; j1 < i1; j1++) {
						TCComponentUser tccomponentuser1 = atccomponentuser1[j1];
						vector.add(tccomponentuser1);
					}

					atccomponentuser1 = atccomponentuser;
					i1 = atccomponentuser1.length;
					for (int k1 = 0; k1 < i1; k1++) {
						TCComponentUser tccomponentuser2 = atccomponentuser1[k1];
						if (flag && !vector.contains(tccomponentuser2)) {
							vector.add(tccomponentuser2);
							continue;
						}
						if (!flag && vector.contains(tccomponentuser2))
							vector.remove(tccomponentuser2);
					}

					ProjectDataPanel.privilegedUsers = new TCComponentUser[vector
							.size()];
					vector.toArray(ProjectDataPanel.privilegedUsers);
				}
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						invalidate();
						repaint();
					}

				});
				validateButtons();
			}
		}

		private int PRIV_USERS;
		private int UNPRIV_USERS;
		private int ALL_USERS;
		private int RESET_ALL;
		private int SET_SELECTED;
		private int UNSET_SELECTED;
		private ImageIcon teamIcon;
		private ImageIcon groupIcon;
		private ImageIcon roleIcon;
		private ImageIcon adminIcon;
		private ImageIcon yesIcon;
		private ImageIcon noIcon;
		private TCSession session;
		private Registry reg;
		private TCComponentProject project;
		private TCComponent teamMembers[];
		private boolean modified;
		private Vector matchedNodes;
		private boolean validSelection;

		public TeamRoleUserTree(TCSession tcsession, Registry registry) {

			super();
			PRIV_USERS = 1;
			UNPRIV_USERS = 2;
			ALL_USERS = 3;
			RESET_ALL = 1;
			SET_SELECTED = 2;
			UNSET_SELECTED = 3;
			project = null;
			teamMembers = null;
			modified = false;
			matchedNodes = null;
			validSelection = false;
			session = tcsession;
			reg = registry;
			Registry registry1 = Registry
					.getRegistry("com.teamcenter.rac.common.organization.organization");
			groupIcon = registry1.getImageIcon("group.ICON");
			roleIcon = registry1.getImageIcon("role.ICON");
			adminIcon = registry.getImageIcon("groupMember.ICON");
			yesIcon = registry.getImageIcon("yes.ICON");
			noIcon = registry.getImageIcon("no.ICON");
			teamIcon = registry.getImageIcon("projectTeam.ICON");
			setCellRenderer(new DefaultTreeCellRenderer() {

				@Override
				public Component getTreeCellRendererComponent(JTree jtree,
						Object obj, boolean flag, boolean flag1, boolean flag2,
						int i, boolean flag3) {
					super.getTreeCellRendererComponent(jtree, obj, flag, flag1,
							flag2, i, flag3);
					if (obj instanceof ProjectDataPanel.TeamRoleUserTreeNode) {
						ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) obj;
						if (teamroleusertreenode.type == 0) {
							if (teamroleusertreenode.isRoot())
								setIcon(teamIcon);
							else
								setIcon(groupIcon);
						} else if (teamroleusertreenode.type == 2)
							setIcon(roleIcon);
						else if (teamroleusertreenode.type == 4
								|| teamroleusertreenode.type == 3) {
							if (teamroleusertreenode.status == ProjectDataPanel.TEAMADMIN)
								setIcon(adminIcon);
							else if (teamroleusertreenode.status == ProjectDataPanel.PRIVILEGED)
								setIcon(yesIcon);
							else
								setIcon(noIcon);
							if (teamroleusertreenode.user != null) {
								String s = teamroleusertreenode.toString();
								if (s != null) {
									int j = s.lastIndexOf("/") + 1;
									if (j < s.length()) {
										s = s.substring(0, j);
										s = (new StringBuilder())
												.append(s)
												.append(teamroleusertreenode.user
														.toDisplayString())
												.toString();
									}
								}
								obj = s;
							}
						}
						setText(obj.toString());
					}
					return this;
				}

				// JavaClassFileOutputException: Invalid index accessing method
				// local variables table of <init>
			});
			initializeTree();
		}
	}

	public static void loadPropertiesInBulk(List<? extends TCComponent> list,
			String[] as) {
		if ((list == null) || (list.isEmpty()))
			return;

		int i = 0;
		if (list != null && as != null)
			i = list.size();
		int j = 0;
		while (i > 0) {
			int k = 0;
			try {
				ArrayList arraylist = new ArrayList();
				for (; j < list.size() && k < 5000; j++) {
					arraylist.add(list.get(j));
					k++;
				}

				i -= k;
				TCComponentType.getTCPropertiesSet(arraylist, as);
			} catch (Exception exception) {
				logger.error(exception.getClass().getName(), exception);
			}
		}
	}

	public static void loadObjectsInBulk(String as[], TCSession tcsession) {
		com.teamcenter.services.rac.core.DataManagementService datamanagementservice = com.teamcenter.services.rac.core.DataManagementService
				.getService(tcsession);
		int i = 0;
		if (as == null)
			return;
		i = as.length;
		int j = 0;
		while (i > 0)
			try {
				int k = as.length - j;
				if (k > 5000)
					k = 5000;
				String as1[] = new String[k];
				int l = j <= 0 ? j : j - 1;
				as1 = Arrays.copyOfRange(as, l, l + k);
				j += as1.length;
				i -= k;
				datamanagementservice.loadObjects(as1);
			} catch (Exception exception) {
				logger.error(exception.getClass().getName(), exception);
			}
	}

	public ProjectDataPanel(TCSession tcsession,
			IProjectAdminApplicationPanel iprojectadminapplicationpanel) {
		super(true);
		isPA = false;
		isPTA = false;
		projectStatus = true;
		projectVisibility = true;
		projectUseProgSec = false;
		startNewFind = true;
		shouldRemove = true;
		session = tcsession;
		projAdminPanel = iprojectadminapplicationpanel;
		userService = session.getUserService();
		initializePanel();
		session.addAIFComponentEventListener(this);
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				validateButtons();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				validateButtons();
			}
		});

	}

	private void initializePanel() {
		reg = Registry.getRegistry(this);
		setLayout(new VerticalLayout(10, 5, 5, 5, 5));
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
		buildDefiningProjAttrPanel();
		projInfoTabs.addTab(reg.getString("projectXMJC.TITLE"),
				new JScrollPane(buildDefiningXMJCAttrPanel()));
		projInfoTabs.addTab(reg.getString("projectXMLX.TITLE"),
				new JScrollPane(buildDefiningXMLXAttrPanel()));
		projInfoTabs.addTab(reg.getString("projectXMRQ.TITLE"),
				new JScrollPane(buildDefiningXMRQAttrPanel()));
		projInfoTabs.addTab(reg.getString("projectFunction.TITLE"),
				new JScrollPane(buildDefiningGNPZAttrPanel()));
		projInfoTabs.addTab(reg.getString("projectPDX.TITLE"), new JScrollPane(
				buildDefiningPDXAttrPanel()));
		setXMJCPanelEnabled(false);
		setXMLXPanelEnabled(false);
		setXMRQPanelEnabled(false);
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

		JPanel jpanel1 = buildDefiningMemberPanel();
		SplitPane splitpane = new SplitPane(1);
		splitpane.setTopComponent(projInfoTabs);
		splitpane.setBottomComponent(jpanel1);
		splitpane.setDividerLocation(0.40000000000000002D);
		add("unbound.bind.center.center", splitpane);
		JPanel jpanel2 = buildButtonPanel();
		add("bottom.nobind.center.center", jpanel2);
		add("bottom", new Separator());
	}

	private JPanel buildDefiningProjAttrPanel() {
		JLabel jlabel = new JLabel(reg.getString("projectName.LABEL", "Name"));
		nameField = new iTextField(18, 32, true);
		nameField.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent mouseevent) {
				projAdminPanel.clearProjectsTreeSelection();
			}

		});
		nameField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent keyevent) {
				enableCopyPaste(keyevent, nameField);
				validateButtons();
			}

		});
		JLabel jlabel1 = new JLabel(reg.getString("projectID.LABEL", "ID"));
		idField = new iTextField(18, 64, true);
		idField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent keyevent) {
				enableCopyPaste(keyevent, idField);
				validateButtons();
			}

		});
		idField.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent mouseevent) {
				projAdminPanel.clearProjectsTreeSelection();
			}

		});
		JLabel jlabel2 = new JLabel(reg.getString("projectDesc.LABEL",
				"Description"));
		descField = new iTextArea(3, 28, 240, false);
		descField.setLineWrap(true);
		descField.setWrapStyleWord(true);
		descField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent keyevent) {
				enableCopyPaste(keyevent, descField);
				validateButtons();
			}

		});
		descField.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent mouseevent) {
				projAdminPanel.clearProjectsTreeSelection();
			}

		});
		JScrollPane jscrollpane = new JScrollPane();
		jscrollpane.getViewport().add(descField);
		String s = reg.getString("activeStatusButton.TITLE", "Active");
		activeStatusRB = new JRadioButton(s, true);
		Object aobj[] = { TCComponentProject.getDisplayName(session) };
		activeStatusRB.setToolTipText(MessageFormat.format(
				reg.getString("activeStatusButton.TIP"), aobj));
		String s1 = reg.getString("inactiveStatusButton.TITLE", "Inactive");
		inactiveStatusRB = new JRadioButton(s1, false);
		inactiveStatusRB.setToolTipText(MessageFormat.format(
				reg.getString("inactiveStatusButton.TIP"), aobj));
		String s2 = reg.getString("inactiveInvisibleStatusButton.TITLE",
				"Inactive And Invisble");
		inactiveInvisibleStatusRB = new JRadioButton(s2, false);
		inactiveInvisibleStatusRB.setToolTipText(MessageFormat.format(
				reg.getString("inactiveInvisibleStatusButton.TIP"), aobj));
		ButtonGroup buttongroup = new ButtonGroup();
		buttongroup.add(activeStatusRB);
		buttongroup.add(inactiveStatusRB);
		buttongroup.add(inactiveInvisibleStatusRB);
		activeStatusRB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				JRadioButton jradiobutton = (JRadioButton) actionevent
						.getSource();
				jradiobutton.setSelected(true);
				validateButtons();
			}

		});
		inactiveStatusRB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				JRadioButton jradiobutton = (JRadioButton) actionevent
						.getSource();
				jradiobutton.setSelected(true);
				validateButtons();
			}

		});
		inactiveInvisibleStatusRB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				JRadioButton jradiobutton = (JRadioButton) actionevent
						.getSource();
				jradiobutton.setSelected(true);
				validateButtons();
			}

		});
		usePrgmSecCB = new JCheckBox("", true);
		usePrgmSecCB.setToolTipText(reg
				.getString("useProgramSecurityCheckBox.TIP"));
		usePrgmSecCB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (usePrgmSecCB.isSelected()) {
					if (!rulesArePresent()) {
						MessageBox.post(
								reg.getString("programAMRulesAbsent.MSG",
										"This option can't be enabled since Program AM Rules are not present."),
								reg.getString("programAMRulesAbsent.TITLE",
										"Program AM Rules Not Present"), 2);
						usePrgmSecCB.setSelected(false);
					}
				} else {
					int i = ConfirmationDialog.post(
							reg.getString("disablePrgSecWarning.TITLE",
									"Program Level Security"),
							reg.getString(
									"disablePrgSecWarning.MSG",
									"If you disable this option data will no longer be secured using Program Level Access Rules.\nDo you want to continue?"));
					if (i == 1)
						usePrgmSecCB.setSelected(true);
				}
				validateButtons();
			}

		});
		JLabel jlabel3 = new JLabel(reg.getString("projectStatus.LABEL",
				"Status"));
		JLabel jlabel4 = new JLabel(reg.getString(
				"useProgramSecurityCheckBox.TITLE", "Use Program Security"));
		JPanel jpanel = new JPanel(new HorizontalLayout());
		jpanel.add("left.nobind", activeStatusRB);
		jpanel.add("left.nobind", inactiveStatusRB);
		jpanel.add("left.nobind", inactiveInvisibleStatusRB);
		JPanel jpanel1 = new JPanel(new PropertyLayout());
		jpanel1.add("1.1.right.center", jlabel1);
		jpanel1.add("1.2.left.center", idField);
		jpanel1.add("2.1.right.center", jlabel);
		jpanel1.add("2.2.left.center", nameField);
		jpanel1.add("3.1.right.center", jlabel2);
		jpanel1.add("3.2.center.center", jscrollpane);
		jpanel1.add("4.1.right.center", jlabel3);
		jpanel1.add("4.2.left.center", jpanel);
		jpanel1.add("5.1.left.center", jlabel4);
		jpanel1.add("5.2.right.center", usePrgmSecCB);
		return jpanel1;
	}

	private void enableCopyPaste(KeyEvent keyevent,
			JTextComponent jtextcomponent) {
		int i = keyevent.getKeyCode();
		Object obj = null;
		boolean flag = i == 67 || i == 90;
		if (keyevent.isControlDown() && flag) {
			Clipboard clipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			String s = jtextcomponent.getText();
			StringSelection stringselection = new StringSelection(s);
			clipboard.setContents(stringselection, null);
		}
		if (keyevent.isControlDown() && i == 86) {
			Clipboard clipboard1 = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			Transferable transferable = clipboard1.getContents(this);
			try {
				String s1 = (String) transferable
						.getTransferData(DataFlavor.stringFlavor);
				String s2 = jtextcomponent.getText();
				if (s2 != null) {
					s2.concat(s1);
					jtextcomponent.setText(s2);
				} else {
					jtextcomponent.setText(s1);
				}
			} catch (Throwable throwable) {
				System.err.println(keyevent);
			}
		}
	}

	private JPanel buildDefiningMemberPanel() {
		JPanel jpanel = new JPanel(new HorizontalLayout(3, 3, 3, 3, 3));
		organizationPanel = new OrganizationPanel(Utilities.getCurrentFrame(),
				session);
		organizationPanel.hideDetailsInfoPanel();
		orgTreePanel = organizationPanel.getOrgTreePanel();
		orgTreePanel.getOrgTree().setProjectOption(true);
		orgTree = orgTreePanel.getOrgTree();
		orgTreeEventHandler = new OrgTreeEventHandler();
		orgTree.addTreeSelectionListener(orgTreeEventHandler);
		orgTree.addMouseListener(orgTreeEventHandler);
		orgTreePanel.getSearchTextField().addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent mouseevent) {
				projAdminPanel.clearProjectsTreeSelection();
			}

		});
		JPanel jpanel1 = new JPanel(new BorderLayout());
		jpanel1.setBorder(new TitledBorder(
				BorderFactory.createEtchedBorder(),
				reg.getString("organizationGroups.LABEL", "Organization Groups")));
		jpanel1.add(organizationPanel, "Center");
		jpanel.add("unbound.bind", jpanel1);
		jpanel.add("right.bind.center.center", buildAddandRemoveButtonPanel());
		JPanel jpanel2 = buildSelectedMemberPanel(session, reg);
		SplitPane splitpane = new SplitPane(0);
		splitpane.setLeftComponent(jpanel);
		splitpane.setRightComponent(jpanel2);
		splitpane.setDividerLocation(0.5D);
		JPanel jpanel3 = new JPanel(new BorderLayout());
		jpanel3.setBorder(BorderFactory.createTitledBorder(reg.getString(
				"memberPanel.TITLE", "Member Selection")));
		jpanel3.add(splitpane, "Center");
		return jpanel3;
	}

	private JPanel buildAddandRemoveButtonPanel() {
		JPanel jpanel = new JPanel();
		jpanel.setLayout(new ButtonLayout(2));
		addMemberButton = createButton(
				reg.getImageIcon("addMemberButton.ICON"),
				reg.getString("addMemberButton.TIP"));
		addMemberButton.setEnabled(false);
		addMemberButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				doAddMemberAction();
			}

		});
		jpanel.add(addMemberButton);
		removeMemberButton = createButton(
				reg.getImageIcon("removeMemberButton.ICON"),
				reg.getString("removeMemberButton.TIP"));
		removeMemberButton.setEnabled(false);
		removeMemberButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				// add by wuh 2014-5-20
				System.out.println("come in add by wuh ");
				try {
					proMembersMaintainParity();
					String proMemberStr = proMemberSb.toString();
					if (!proMemberStr.equals(reg
							.getString("assign_scheduletask"))) {
						MessageBox.post(proMemberStr,
								reg.getString("info_title"),
								MessageBox.INFORMATION);
					} else {
						if (project != null) {
							// 通过当前项目的“首选零组件”关系找到对应的 Schedule
							TCComponent[] program_items = project
									.getRelatedComponents("TC_Program_Preferred_Items");
							scheduleMemberVec.clear();
							TCComponentSchedule schedule = null;
							if (program_items != null) {
								System.out
										.println("TC_Program_Preferred_Items count --->"
												+ program_items.length);
								for (int i = 0; i < program_items.length; i++) {
									if (program_items[i] instanceof TCComponentSchedule) {
										schedule = (TCComponentSchedule) program_items[i];
										TCComponent[] schedulemembers = schedule
												.getRelatedComponents("schedulemember_taglist");
										if (schedulemembers != null) {
											System.out
													.println("schedulemember count--->"
															+ schedulemembers.length);
											for (int j = 0; j < schedulemembers.length; j++) {
												if (schedulemembers[j] instanceof TCComponentScheduleMember) {
													System.out
															.println(j
																	+ " is TCComponentScheduleMember");
													TCComponentScheduleMember scheduleMember = (TCComponentScheduleMember) schedulemembers[j];
													scheduleMemberVec
															.add(scheduleMember);
												} else {
													System.out
															.println(j
																	+ " is not TCComponentScheduleMember");
												}
											}
										}
									}
								}
								StringBuffer schedule_member_error = new StringBuffer(
										reg.getString("is_schedulemember"));
								for (int i = 0, groupSize = groupMemberVec
										.size(); i < groupSize; i++) {
									TCComponentUser group_mem_user = groupMemberVec
											.get(i).getUser();
									System.out
											.println("TCComponentGroupMember user_id --->"
													+ group_mem_user
															.getUserId());
									for (int j = 0, scheduleSize = scheduleMemberVec
											.size(); j < scheduleSize; j++) {
										TCComponent schedule_mem_resouce = scheduleMemberVec
												.get(j).getRelatedComponent(
														"resource_tag");
										System.out
												.println("schedule_mem_resouce  type ---->"
														+ schedule_mem_resouce
																.getType());
										if (schedule_mem_resouce instanceof TCComponentUser) {
											TCComponentUser schedule_mem_user = (TCComponentUser) schedule_mem_resouce;
											System.out.println("schedule_mem_resouce user_id --->"
													+ schedule_mem_user
															.getUserId());
											if (group_mem_user
													.equals(schedule_mem_user)) {
												System.out.println("equal");
												String user_name = schedule_mem_user
														.getProperty("user_name");
												schedule_member_error.append(
														"\r\n").append(
														user_name);
												// TCComponentScheduleMember s;
												// 移除该用户
												// schedule.remove("schedulemember_taglist",
												// scheduleMemberVec.get(j));
											} else {
												System.out
														.println("not equal,skip。。。。。");
											}
										}
										// else if(schedule_mem_resouce
										// instanceof TCComponentDiscipline){
										// TCComponentDiscipline discipline =
										// (TCComponentDiscipline)
										// schedule_mem_resouce;
										// TCComponent[] members =
										// discipline.getRelatedComponents("TC_discipline_member");
										// if(members != null){
										// System.out.println("TCComponentDiscipline member count --- >"+members.length);
										// for(int k=0;k<members.length;k++){
										// if(members[k] instanceof
										// TCComponentUser){
										// TCComponentUser discipline_user =
										// (TCComponentUser) members[k];
										// System.out
										// .println("TCComponentDiscipline user_id--->"+discipline_user.getUserId());
										// if(group_mem_user.equals(discipline_user)){
										// //移除该用户
										// discipline.remove("TC_discipline_member",
										// members[k]);
										// System.out.println("TCComponentDiscipline相等");
										// }else{
										// System.out.println("TCComponentDiscipline不相等");
										// }
										// }
										// }
										// }else{
										// System.out.println("TCComponentDiscipline member count --- >0");
										// }
										// }

									}
								}
								if (!schedule_member_error.toString().equals(
										reg.getString("is_schedulemember"))) {
									MessageBox.post(
											schedule_member_error.toString(),
											reg.getString("info_title"),
											MessageBox.INFORMATION);
									return;
								}
							}
						}
						doRemoveMemberAction();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
		jpanel.add(removeMemberButton);
		return jpanel;
	}

	private JPanel buildButtonPanel() {
		JPanel jpanel = new JPanel();
		jpanel.setLayout(new ButtonLayout(1, 1, 20));
		createButton = new JButton(reg.getString("createProjectButton.LABEL",
				"Create"), reg.getImageIcon("create.ICON"));
		modifyButton = new JButton(reg.getString("modifyProjectButton.LABEL",
				"Modify"), reg.getImageIcon("modify.ICON"));
		copyButton = new JButton(reg.getString("copyProjectButton.LABEL",
				"Copy"), reg.getImageIcon("copy.ICON"));
		deleteButton = new JButton(reg.getString("deleteProjectButton.LABEL",
				"Delete"), reg.getImageIcon("delete.ICON"));
		clearButton = new JButton(reg.getString("clearProjectButton.LABEL",
				"Clear"), reg.getImageIcon("clear.ICON"));
		jpanel.add(createButton);
		jpanel.add(modifyButton);
		jpanel.add(copyButton);
		jpanel.add(deleteButton);
		jpanel.add(clearButton);
		createButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				try {
					createProjectOperation();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		modifyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				modifyProjectOperation();
			}

		});
		copyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				copyProjectOperation();
			}

		});
		deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				deleteProjectOperation();
			}

		});
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				clearOperation();
				resetSecitionLov(null);// 设置section lov值变化
			}

		});
		return jpanel;
	}

	private boolean checkTeamAdminMembership() {
		boolean flag = true;
		TCComponentUser tccomponentuser = teamRoleUserTree.getTeamAdmin(true);
		if (!teamRoleUserTree.isTeamMember(tccomponentuser)) {
			int i = 0;
			Object aobj[] = { TCComponentProject.getDisplayName(session) };
			i = ConfirmationDialog.post(
					MessageFormat.format(reg.getString("confirmPTA.TITLE",
							"Add Project Team Admin"), aobj), MessageFormat
							.format(reg.getString("confirmPTA.MESSAGE"), aobj));
			if (i == 1)
				flag = false;
		}
		return flag;
	}

	private boolean isProjectOwnerOrPTA() {
		TCComponentUser tccomponentuser = session.getUser();
		TCComponentUser tccomponentuser1 = null;
		try {
			tccomponentuser1 = project.getPAUser();
		} catch (TCException tcexception) {
		}
		return tccomponentuser == tccomponentuser1
				|| tccomponentuser == currentPTA;
	}

	private void createProjectOperation() throws TCException {
		System.out.println("=====ispa=========" + isPA);
		System.out.println("=====ispta=========" + isPTA);
		System.out.println("=====checkTeamAdminMembership========="
				+ checkTeamAdminMembership());
		TCComponentGroup currentGroup = session.getCurrentGroup();
		System.out.println("-------得到当前用户的组-------");

		// System.out.println("currentGroup的FullName====="
		// + currentGroup.getFullName());
		// System.out.println("currentGroup的GroupName====="
		// + currentGroup.getGroupName());

		// 5.7-修改
		/*
		 * if (currentGroup.getGroupName().equals("dba")) {
		 * MessageBox.post("dba can't create project !", "Warrring",
		 * MessageBox.WARNING); // initializePanel(); return; } else
		 */if (!checkTeamAdminMembership()) {
			return;
		} else if (!currentGroup.getGroupName().equals("dba")) {
			MessageBox.post("non-dba can't create project !", "Warning",
					MessageBox.WARNING);
			// initializePanel();
			return;
		} else {
			Object aobj[] = { TCComponentProject.getDisplayName(session) };
			String s = MessageFormat.format(
					reg.getString("createProject.MESSAGE", "Creating Project"),
					aobj);
			session.queueOperation(new AbstractAIFOperation(s) {

				@Override
				public void executeOperation() {
					try {
						TCComponentProjectType tccomponentprojecttype = (TCComponentProjectType) session
								.getTypeComponent("TC_Project");
						TCComponentProject tccomponentproject = tccomponentprojecttype
								.create(getProjectID(), getProjectName(),
										getProjectDesc(), teamRoleUserTree
												.getTeamMembers(),
										teamRoleUserTree.getTeamAdmin(true),
										teamRoleUserTree
												.getPrivilegedUsers(true));
						/*
						 * String as[] = new String[3]; as[0] = "is_visible";
						 * as[1] = "use_program_security"; as[2] = "is_active";
						 * projectVisibility = getProgramVisibility();
						 * projectUseProgSec = getProgramSecurity();
						 * projectStatus = getProjectStatus(); TCProperty
						 * atcproperty[] =
						 * tccomponentproject.getTCProperties(as);
						 * atcproperty[0].setLogicalValue(projectVisibility);
						 * atcproperty[1].setLogicalValue(projectUseProgSec);
						 * atcproperty[2].setLogicalValue(projectStatus);
						 * tccomponentproject.lock();
						 * tccomponentproject.setTCProperties(atcproperty);
						 * tccomponentproject.save();
						 * tccomponentproject.unlock();
						 */

						projAdminPanel.projectCreated(tccomponentproject);

						// 创建项目信息Item
						TCComponentItem newProjectItem = createItemForItk();

						if (newProjectItem != null) {

							tccomponentproject.add(
									"TC_Program_Preferred_Items",
									newProjectItem);
							tccomponentproject
									.assignToProject(new TCComponent[] { newProjectItem });
							/* williamzhou <2013-08-02> */
							tccomponentproject.setLogicalProperty(
									"use_program_security", true);
							// ************************* 根据模板自动创建项目资料库 (zhanggl)
							/*
							 * Vector vtUser = tccomponentproject.getTeam();
							 * TCComponentUser teamAdmin = null; teamAdmin =
							 * (TCComponentUser) vtUser.elementAt(1); if(
							 * teamAdmin == null ) teamAdmin =
							 * ((TCSession)getSession()).getUser();
							 * 
							 * Object objInput[] = new Object[3]; objInput[0] =
							 * tccomponentproject; objInput[1] = newProjectItem;
							 * objInput[2] = teamAdmin; try { TCUserService
							 * service =
							 * ((TCSession)getSession()).getUserService();
							 * service.call("JCI6postActionsForNewProj",
							 * objInput); } catch (TCException e) {
							 * MessageBox.post( Utilities.getCurrentFrame(),
							 * e.getMessage(), "Error", 4); e.printStackTrace();
							 * }
							 */
							// ************************* 根据模板自动创建项目资料库 (zhanggl)
						}

						validateButtons();
					} catch (TCException tcexception) {
						MessageBox.post(Utilities.getCurrentFrame(),
								tcexception);
					}
				}

			});
			return;
		}
	}

	private void deleteProjectOperation() {
		final Object args[] = { TCComponentProject.getDisplayName(session) };
		int i = ConfirmationDialog.post(
				reg.getString("confirmDelete.TITLE", "Confirm Delete"),
				MessageFormat.format(
						reg.getString("confirmDeleteProject.MESSAGE"), args));
		if (i == 1) {
			return;
		} else {
			session.queueOperation(new AbstractAIFOperation() {

				@Override
				public void executeOperation() {
					String s = MessageFormat.format(reg.getString(
							"deleteProject.MESSAGE", "Deleting Project"), args);
					session.setStatus(s);
					try {
						if (project != null) {
							String s1 = project.getStringProperty("project_id");
							if (Debug.isOn("project"))
								Debug.println((new StringBuilder())
										.append("deleteProjectOperation: deleting... ")
										.append(s1).toString());
							// 添加删除项目信息，先去掉关系再删除
							TCComponent[] childs = project
									.getRelatedComponents("TC_Program_Preferred_Items");
							if (childs != null && childs.length > 0) {
								project.remove("TC_Program_Preferred_Items",
										childs);
								TCComponentItem jci6ProgramInfo = null;
								for (int i = 0; i < childs.length; i++) {
									String type = childs[i].getType();
									if ("JCI6_ProgramInfo".equals(type)) {
										jci6ProgramInfo = (TCComponentItem) childs[i];
										continue;
									}
								}
								if (jci6ProgramInfo != null) {
									jci6ProgramInfo.delete();
								}
							}

							project.delete();
							projAdminPanel.projectDeleted(project, s1);
							if (Debug.isOn("project"))
								Debug.println((new StringBuilder())
										.append("deleteProjectOperation: done... ")
										.append(s1).toString());
						}
					} catch (TCException tcexception) {
						MessageBox.post(Utilities.getCurrentFrame(),
								tcexception);
					}
					session.setReadyStatus();
				}

			});
			clearOperation();
			return;
		}
	}

	// 点击修改项目button操作
	private void modifyProjectOperation() {
		if (teamRoleUserTree.teamModified() && !checkTeamAdminMembership())
			return;

		if (!isProjectOwnerOrPTA()) {
			Object aobj[] = { session.getUser(), project.toString() };
			String s = MessageFormat.format(
					reg.getString("PADoNotOwnTheProject.MSG"), aobj);
			MessageBox.post(s, reg.getString("PADoNotOwnTheProject.TITLE",
					"Project modify error..."), 1);
			teamRoleUserTree.loadTeam(project);
			return;

		} else {
			Object aobj1[] = { TCComponentProject.getDisplayName(session) };
			String s1 = MessageFormat
					.format(reg.getString("modifyProject.MESSAGE",
							"Modifying Project"), aobj1);
			session.queueOperation(new AbstractAIFOperation(s1) {

				@Override
				public void executeOperation() {
					if (project != null) {
						String s2 = getProjectID();
						String s3 = null;
						try {
							s3 = project.getStringProperty("project_id");
						} catch (TCException tcexception) {
						}

						// 5.15修改----user添加删除
						if (modifyProjectTeam(project)
								& modifyProjectProps(project))
							projAdminPanel.projectModified(project, s3, s2);
						validateButtons();
						ProjectDataPanel.currentPTA = teamRoleUserTree
								.getTeamAdmin(true);
					}
				}

			});
			return;
		}
	}

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
			if (projectStatus != flag2) {
				vector.add(atcproperty[3]);
				atcproperty[3].setLogicalValueData(flag2);
			}
			boolean flag3 = getProgramSecurity();
			if (projectUseProgSec != flag3) {
				vector.add(atcproperty[4]);
				atcproperty[4].setLogicalValueData(flag3);
			}
			boolean flag4 = getProgramVisibility();
			if (projectVisibility != flag4) {
				vector.add(atcproperty[5]);
				atcproperty[5].setLogicalValueData(flag4);
			}
			int i = vector.size();
			if (i > 0) {
				TCProperty atcproperty2[] = new TCProperty[i];
				for (int j = 0; j < i; j++)
					atcproperty2[j] = (TCProperty) vector.elementAt(j);

				try {
					tccomponentproject.setTCProperties(atcproperty2);
					TCProperty atcproperty1[] = tccomponentproject
							.getTCProperties(as);
					projectID = atcproperty1[0].getStringValue();
					projectName = atcproperty1[1].getStringValue();
					projectDesc = atcproperty1[2].getStringValue();
					projectStatus = atcproperty1[3].getLogicalValue();
					projectUseProgSec = atcproperty1[4].getLogicalValue();
					projectVisibility = atcproperty1[5].getLogicalValue();
					if (flag1) {
						tccomponentproject.clearCache("object_string");
						teamRoleUserTree.projectModified();
					}
				} catch (TCException tcexception1) {
					tccomponentproject.clearCache(as);
					MessageBox.post(Utilities.getCurrentFrame(), tcexception1);
					flag = false;
				}
			}

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
						userService.call("open_or_close_pass",
								new Object[] { 0 });

						userService.call("open_or_close_pass",
								new Object[] { 1 });
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

	private boolean modifyProjectTeam(TCComponentProject tccomponentproject) {
		boolean flag = true;

		System.out.println("---------------进入修改用户的程序------");
		try {
			if (teamRoleUserTree.teamModified()) {
				System.out.println("----------开始修改了用户-------------");
				TCComponent atccomponent[] = teamRoleUserTree.getTeamMembers();
				TCComponentUser tccomponentuser = teamAdmin;
				TCComponentUser atccomponentuser[] = (TCComponentUser[]) allPrivUsers
						.toArray(new TCComponentUser[allPrivUsers.size()]);
				//tccomponentproject.modifyTeam(atccomponent, tccomponentuser,
				//		atccomponentuser);
				tccomponentproject.modifyTeam(atccomponent,new TCComponentUser[]{tccomponentuser}, atccomponentuser);

				System.out.println("----------修改完成！！！-------------");

				teamRoleUserTree.teamModified(false);
				teamRoleUserTree.loadTeam(tccomponentproject);
				System.out.println("---------------LOAD用户!!!!-----------");

			}
		} catch (TCException tcexception) {
			MessageBox.post(Utilities.getCurrentFrame(), tcexception);
			flag = false;
		}
		return flag;
	}

	private void copyProjectOperation() {
		if (!checkTeamAdminMembership()) {
			return;
		} else {
			Object aobj[] = { TCComponentProject.getDisplayName(session) };
			String s = MessageFormat.format(
					reg.getString("copyProject.MESSAGE", "Copying Project"),
					aobj);
			session.queueOperation(new AbstractAIFOperation(s) {

				@Override
				public void executeOperation() {
					try {
						if (project != null) {
							TCComponentProject tccomponentproject = project;
							TCComponentProjectType tccomponentprojecttype = (TCComponentProjectType) session
									.getTypeComponent("TC_Project");
							TCComponentProject tccomponentproject1 = tccomponentprojecttype
									.copy(tccomponentproject,
											getProjectID(),
											getProjectName(),
											getProjectDesc(),
											teamRoleUserTree.getTeamMembers(),
											teamRoleUserTree.getTeamAdmin(true),
											teamRoleUserTree
													.getPrivilegedUsers(true));
							projAdminPanel.projectCopied(project,
									tccomponentproject1);
							// copy逻辑
							TCComponent[] tcComponetSub = tccomponentproject
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
							validateButtons();
						}
					} catch (TCException tcexception) {
						MessageBox.post(Utilities.getCurrentFrame(),
								tcexception);
					}
				}

			});
			return;
		}
	}

	public void clearOperation() {
		usePrgmSecCB.setSelected(false);
		projAdminPanel.clearOperation();
		teamAdmin = null;
		privilegedUsers = null;
		allPrivUsers = null;
	}
	
	private void doAddMemberAction() {
		OrgObject[] arrayOfOrgObject = this.orgTreeEventHandler
				.getSelectedOrgObjects();
		ArrayList localArrayList1 = new ArrayList();
		ArrayList localArrayList2 = new ArrayList();
		if ((arrayOfOrgObject == null) || (arrayOfOrgObject.length <= 0))
			return;
		int i = arrayOfOrgObject.length;
		for (int j = 0; j < i; ++j) {
			int k = arrayOfOrgObject[j].getType();
			if (k == 0) {
				localArrayList1.add(arrayOfOrgObject[j].getGroup());
			} else {
				if (k != 4)
					continue;
				localArrayList2.add(arrayOfOrgObject[j].getComponent());
			}
		}
		Object localObject;
		if (localArrayList1.size() > 0) {
			TCComponentGroup[] localObject22 = (TCComponentGroup[]) localArrayList1
					.toArray(new TCComponentGroup[localArrayList1.size()]);
			this.teamRoleUserTree.addComponents(localObject22);
		}
		if (localArrayList2.size() > 0) {
			TCComponentGroupMember[] localObject22 = (TCComponentGroupMember[]) localArrayList2
					.toArray(new TCComponentGroupMember[localArrayList2.size()]);
			this.teamRoleUserTree.addComponents(localObject22);
		}
		if (this.teamRoleUserTree.getTeamAdmin(false) == null) {
			localObject = this.session.getUser();
			if (this.teamRoleUserTree
					.isTeamMember((TCComponentUser) localObject))
				this.teamRoleUserTree
						.setTeamAdmin((TCComponentUser) localObject);
		}
		validateButtons();
	}

	//modify by wuwei
//	private void doAddMemberAction() {
//		OrgObject aorgobject[] = orgTreeEventHandler.getSelectedOrgObjects();
//		ArrayList arraylist = new ArrayList();
//		ArrayList arraylist1 = new ArrayList();
//		if (aorgobject != null && aorgobject.length > 0) {
//			int i = aorgobject.length;
//			for (int j = 0; j < i; j++) {
//				int k = aorgobject[j].getType();
//				if (k == 0) {
//					arraylist.add(aorgobject[j].getGroup());
//					continue;
//				}
//				if (k == 4)
//					arraylist1.add(aorgobject[j].getComponent());
//			}
//
//			if (arraylist.size() > 0) {
//				TCComponentGroup atccomponentgroup[] = (TCComponentGroup[]) arraylist
//						.toArray(new TCComponentGroup[arraylist.size()]);
//				teamRoleUserTree.addComponents(atccomponentgroup);
//			}
//		}
//		if (arraylist1.size() > 0) {
//			TCComponentGroupMember atccomponentgroupmember[] = (TCComponentGroupMember[]) arraylist1
//					.toArray(new TCComponentGroupMember[arraylist.size()]);
//			teamRoleUserTree.addComponents(atccomponentgroupmember);
//		}
//		if (teamRoleUserTree.getTeamAdmin(false) == null) {
//			TCComponentUser tccomponentuser = session.getUser();
//			if (teamRoleUserTree.isTeamMember(tccomponentuser))
//				teamRoleUserTree.setTeamAdmin(tccomponentuser);
//		}
//		validateButtons();
//	}

	private void doRemoveMemberAction() {
		boolean flag = true;
		boolean flag1 = true;
		boolean flag2 = true;
		TreePath atreepath[] = teamRoleUserTree.getSelectionPaths();
		boolean flag3 = getProgramSecurity();
		if (project == null) {
			teamRoleUserTree.removeSelectedMembers();
			validateButtons();
		} else {
			Object aobj[] = { TCComponentProject.getDisplayName(session) };
			label0: for (int i = 0; atreepath != null && i < atreepath.length
					&& flag3; i++) {
				TreePath treepath = atreepath[i];
				ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) treepath
						.getLastPathComponent();
				if (teamroleusertreenode.type == 4) {
					checkUserData(teamroleusertreenode.user);
					flag = shouldRemove;
					if (shouldRemove)
						continue;
					int j = ConfirmationDialog.post(reg.getString(
							"removeUser.TITLE", "Remove User"), MessageFormat
							.format(reg
									.getString("confirmRemoveMember.MESSAGE"),
									aobj));
					if (j == 2)
						flag = true;
					continue;
				}
				if (teamroleusertreenode.type == 2) {
					int k = teamroleusertreenode.getChildCount();
					int i1 = 0;
					do {
						if (i1 >= k)
							continue label0;
						ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode1 = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
								.getChildAt(i1);
						checkUserData(teamroleusertreenode1.user);
						flag1 = shouldRemove;
						if (!shouldRemove) {
							int j1 = ConfirmationDialog
									.post(reg.getString("removeRole.TITLE",
											"Remove Role"),
											MessageFormat.format(
													reg.getString("confirmRemoveMember.MESSAGE"),
													aobj));
							if (j1 == 2)
								flag1 = true;
							continue label0;
						}
						i1++;
					} while (true);
				}
				if (teamroleusertreenode.type != 0)
					continue;
				checkGroupData(teamroleusertreenode.name);
				flag2 = shouldRemove;
				if (shouldRemove)
					continue;
				int l = ConfirmationDialog.post(reg.getString(
						"removeGroup.TITLE", "Remove Group"), MessageFormat
						.format(reg.getString("confirmRemoveMember.MESSAGE"),
								aobj));
				if (l == 2)
					flag2 = true;
			}

			if (flag && flag1 && flag2) {
				teamRoleUserTree.removeSelectedMembers();
				validateButtons();
			}
		}
	}

	private JPanel buildSelectedMemberPanel(TCSession tcsession,
			Registry registry) {
		teamRoleUserTree = new TeamRoleUserTree(session, registry);
		JScrollPane jscrollpane = new JScrollPane(teamRoleUserTree);
		privilegedUserButton = createButton(
				registry.getImageIcon("selectPrivilegedUserButton.ICON"),
				registry.getString("selectPrivilegedUserButton.TIP"));
		privilegedUserButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				startPrivilegedUserSelectOperation();
			}

		});
		privilegedUserButton.setEnabled(false);
		Object aobj[] = { TCComponentProject.getDisplayName(session) };
		teamAdminButton = createButton(
				registry.getImageIcon("selectTeamAdminButton.ICON"),
				MessageFormat.format(
						registry.getString("selectTeamAdminButton.TIP"), aobj));
		teamAdminButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				startTeamAdminSelectOperation();
			}

		});
		teamAdminButton.setEnabled(false);
		findTextField = new JTextField(10);
		findTextField.setToolTipText(registry.getString("findText.TIP",
				"Enter a name to do Find in Display"));
		findTextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent keyevent) {
				char c = keyevent.getKeyChar();
				if (c != '\n')
					startNewFind = true;
			}

		});
		findTextField.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent mouseevent) {
				projAdminPanel.clearProjectsTreeSelection();
			}

		});
		findTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				String s = findTextField.getText().toLowerCase();
				System.out.println("search key---->" + s + "<---");
				if (s != null && s.trim().length() > 0) {
					System.out.println("startNewFind--->" + startNewFind);
					if (startNewFind) {
						teamRoleUserTree.startSearchOperation(s);
						startNewFind = false;
					} else {
						teamRoleUserTree.selectMatchedNode();
					}
				}
			}

		});
		findButton = new JButton(registry.getImageIcon("findButton.ICON"));
		findButton.setToolTipText(registry.getString("findButton.TIP",
				"Perform Find in Display"));
		findButton.setMargin(new Insets(0, 0, 0, 0));
		findButton.setFocusPainted(false);
		findButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				String s = findTextField.getText().toLowerCase();
				if (s != null && s.trim().length() > 0)
					if (startNewFind) {
						teamRoleUserTree.startSearchOperation(s);
						startNewFind = false;
					} else {
						teamRoleUserTree.selectMatchedNode();
					}
			}

		});
		JPanel jpanel = new JPanel(new HorizontalLayout(1));
		jpanel.add("left.nobind", findTextField);
		jpanel.add("left.nobind", findButton);
		jpanel.add("right", privilegedUserButton);
		jpanel.add("right", teamAdminButton);
		JPanel jpanel1 = new JPanel(new VerticalLayout(3, 3, 3, 3, 3));
		jpanel1.add("unbound", jscrollpane);
		jpanel1.add("bottom", jpanel);
		JPanel jpanel2 = new JPanel(new BorderLayout());
		jpanel2.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
				registry.getString("selectedMember.LABEL", "Selected Members")));
		jpanel2.add(jpanel1, "Center");
		JPanel jpanel3 = new JPanel(new HorizontalLayout(3, 3, 3, 3, 3));
		jpanel3.add("unbound.bind", jpanel2);
		return jpanel3;
	}

	public void setPrivilegedUsers(TCComponentUser atccomponentuser[]) {
		teamRoleUserTree.setPrivilegedUsers(atccomponentuser);
		validateButtons();
	}

	public void setTeamAdmin(TCComponentUser tccomponentuser) {
		teamRoleUserTree.setTeamAdmin(tccomponentuser);
		validateButtons();
	}

	private void startPrivilegedUserSelectOperation() {
		if (teamRoleUserTree != null) {
			teamRoleUserTree.expandAll(teamRoleUserTree, -1);
			TCComponentUser atccomponentuser[] = teamRoleUserTree
					.getPrivilegedUsers(false);
			TCComponentUser atccomponentuser1[] = teamRoleUserTree
					.getUnprivilegedUsers();
			if (atccomponentuser != null && atccomponentuser.length > 0
					|| atccomponentuser1 != null
					&& atccomponentuser1.length > 0) {
				PrivilegedUserSelectionDialog privilegeduserselectiondialog = new PrivilegedUserSelectionDialog(
						Utilities.getCurrentFrame(), this, atccomponentuser,
						atccomponentuser1, isPA || isPTA);
				privilegeduserselectiondialog.setVisible(true);
			}
		}
	}

	private void startTeamAdminSelectOperation() {
		if (teamRoleUserTree != null) {
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					teamRoleUserTree.expandAll(teamRoleUserTree, -1);
				}

			};
			if (EventQueue.isDispatchThread())
				runnable.run();
			TCComponentUser atccomponentuser[] = teamRoleUserTree
					.getUsers(false);
			if (atccomponentuser == null || atccomponentuser.length <= 0)
				return;
			TCComponentUser tccomponentuser = teamRoleUserTree
					.getTeamAdmin(false);
			TeamAdminSelectionDialog teamadminselectiondialog = new TeamAdminSelectionDialog(
					Utilities.getCurrentFrame(), this, tccomponentuser,
					atccomponentuser, isPA || isPTA);
			teamadminselectiondialog.setVisible(true);
		}
	}

	// 清空控件上显示
	public void loadData(TCComponentProject tccomponentproject) {
		try {
			System.out.println("========5.20====调用LoadData=======");
			// 清空控件上显示
			clearComponentValue();

			addMemberButton.setEnabled(false);
			removeMemberButton.setEnabled(false);
			project = tccomponentproject;
			if (project != null) {
				String as[] = new String[6];
				as[0] = "project_id";
				as[1] = "project_name";
				as[2] = "project_desc";
				as[3] = "is_active";
				as[4] = "use_program_security";
				as[5] = "is_visible";
				TCProperty atcproperty[] = tccomponentproject
						.getTCProperties(as);
				projectID = atcproperty[0].getStringValue();
				projectName = atcproperty[1].getStringValue();
				projectDesc = atcproperty[2].getStringValue();
				projectStatus = atcproperty[3].getLogicalValue();
				projectUseProgSec = atcproperty[4].getLogicalValue();
				projectVisibility = atcproperty[5].getLogicalValue();
				idField.setText(projectID);
				nameField.setText(projectName);
				descField.setText(projectDesc);
				activeStatusRB.setSelected(projectStatus);
				inactiveStatusRB.setSelected(!projectStatus
						&& projectVisibility);
				inactiveInvisibleStatusRB.setSelected(!projectStatus
						&& !projectVisibility);
				usePrgmSecCB.setSelected(projectUseProgSec);
				teamRoleUserTree.loadTeam(project);

				// 加载项目信息属性值到定义面板
				clearComponentValue();

				TCComponent[] childs = project
						.getRelatedComponents("TC_Program_Preferred_Items");
				if (childs != null && childs.length > 0) {
					System.out.println("------加载项目信息属性值到定义面板-----");

					TCComponentItem jci6ProgramInfo = null;
					for (int i = 0; i < childs.length; i++) {
						String type = childs[i].getType();
						if ("JCI6_ProgramInfo".equals(type)) {
							jci6ProgramInfo = (TCComponentItem) childs[i];
							continue;
						}
					}

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
						oemNameStr = jci6ProgramInfoRev
								.getProperty("jci6_OEMName");

						System.out.println("----显示jci6_OEMName---："
								+ oemNameStr);

						modeNameStr = jci6ProgramInfoRev
								.getProperty("jci6_OEMModelName");
						System.out.println("----显示OEMModelName---："
								+ modeNameStr);

						secStr = jci6ProgramInfoRev
								.getProperty("jci6_ProgramSec");
						System.out.println("----显示ProgramSec---：" + secStr);

						diviStr = jci6ProgramInfoRev
								.getProperty("jci6_ProgramDivi");
						System.out.println("----显示ProjectTL---：" + diviStr);

						TCComponentUser tlUser = (TCComponentUser) jci6ProgramInfoRev
								.getReferenceProperty("jci6_ProjectTL");
						if (tlUser != null) {
							tlStr = "" + tlUser.getProperty("person");
							System.out.println("----显示tlUser---：" + tlStr);

						}

						// add by wuh 2014-5-26
						createDateStr = jci6ProgramInfo
								.getDateProperty("creation_date");
						closeDateStr = jci6ProgramInfo
								.getDateProperty("jci6_CloseDate");
						// add 2014-6-5
						eqmStr = jci6ProgramInfo.getTCProperty("jci6_IsRunEQM")
								.getBoolValue();

						targetPosStr = jci6ProgramInfoRev
								.getProperty("jci6_TargetPos");
						System.out
								.println("----显示targetPos---：" + targetPosStr);

						boolean statusVal = jci6ProgramInfoRev
								.getLogicalProperty("jci6_ActiveStatus");
						// .getProperty("jci6_ActiveStatus");

						// 5.17修改---Active Status正常显示
						System.out
								.println("jci6_ActiveStatus的属性值为：--------------"
										+ statusVal);
						// if ("Y".equalsIgnoreCase(statusVal))
						if (statusVal)
							activeStatusStr = "Active";
						else
							activeStatusStr = "Inactive";
						System.out.println("----显示activeStatus-----："
								+ activeStatusStr);

						projectStatusStr = jci6ProgramInfoRev
								.getProperty("jci6_ProgramState");
						System.out.println("----显示ProgramState-----："
								+ projectStatusStr);

						categoryStr = jci6ProgramInfoRev
								.getProperty("jci6_Category");
						System.out
								.println("----显示Category-----：" + categoryStr);

						productStr = jci6ProgramInfoRev
								.getProperty("jci6_Product");
						System.out.println("----显示Product-----: " + productStr);

						typeStr = jci6ProgramInfoRev.getProperty("jci6_Type");
						System.out.println("----显示jci6_Type-----: " + typeStr);

						pStartStr = jci6ProgramInfoRev
								.getDateProperty("jci6_StartDate");
						System.out.println("----显示jci6_StartDate-----: "
								+ pStartStr);

						pEndStr = jci6ProgramInfoRev
								.getDateProperty("jci6_EndDate");
						System.out.println("----显示jci6_EndDate-----: "
								+ pEndStr);

						lcStr = jci6ProgramInfoRev.getDateProperty("jci6_SOP");
						System.out.println("----显示jci6_SOP-----: " + lcStr);

						sopfyStr = ""
								+ jci6ProgramInfoRev
										.getIntProperty("jci6_SOPFY");
						System.out
								.println("----显示jci6_SOPFY-----: " + sopfyStr);

						modelYearStr = ""
								+ jci6ProgramInfoRev
										.getIntProperty("jci6_ModelYear");
						System.out.println("----显示jci6_ModelYear-----: "
								+ modelYearStr);

						functionStr = jci6ProgramInfoRev
								.getProperty("jci6_Function");
						System.out.println("----显示jci6_Function-----: "
								+ functionStr);

						frontSSStr = jci6ProgramInfoRev
								.getProperty("jci6_FrontSS");
						System.out.println("----显示jci6_FrontSS-----: "
								+ frontSSStr);

						rearSSStr = jci6ProgramInfoRev
								.getProperty("jci6_RearSS");
						System.out.println("----显示jci6_RearSS-----: "
								+ rearSSStr);

						trackStr = jci6ProgramInfoRev.getProperty("jci6_Track");
						System.out
								.println("----显示jci6_Track-----: " + trackStr);

						reclinerStr = jci6ProgramInfoRev
								.getProperty("jci6_Recliner");
						System.out.println("----显示jci6_Recliner-----: "
								+ reclinerStr);

						pumpStr = jci6ProgramInfoRev.getProperty("jci6_PUMP");
						System.out.println("----显示jci6_PUMP-----: " + pumpStr);

						vtaStr = jci6ProgramInfoRev.getProperty("jci6_VTA");
						System.out.println("----显示jci6_VTA-----: " + vtaStr);

						latchStr = jci6ProgramInfoRev.getProperty("jci6_Latch");
						System.out
								.println("----显示jci6_Latch-----: " + latchStr);

						// PDX信息只读特殊处理,属性不全
						pxdStr = jci6ProgramInfoRev.getProperty("jci6_PDxSeq");
						System.out.println("----显示jci6_PDxSeq-----: " + pxdStr);

						gebtStr = jci6ProgramInfoRev
								.getProperty("jci6_GEBTTemplate");
						System.out.println("----显示jci6_GEBTTemplate-----: "
								+ gebtStr);

						jpanel1.remove(SMTE_lov);
						SMTE_lov = new PropertyArray();
						SMTE_lov.addMouseMotionListener(new MouseMotionListener() {
							@Override
							public void mouseMoved(MouseEvent e) {
								System.out
										.println("================mouseMoved!!!!===============");
								validateButtons();

							}

							@Override
							public void mouseDragged(MouseEvent e) {
								// TODO Auto-generated method stub

							}
						});

						// 5.12--SMTE
						TCProperty tcProperty = jci6ProgramInfoRev
								.getTCProperty("jci6_SMTE_Variables");

						SMTE_lov.load(tcProperty);
						// SMTE_lov.save(tcProperty);

						// 5.12--修改
						jpanel1.add("10.2.center.center", SMTE_lov);

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
						eqmRadioButton.setSelected(eqmStr);

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

						System.out.println("----添加值到控件上显示----");

						setTargetPossibilityVisiiable(targetPosStr);
						System.out.println("----设置targetPossibility状态及值----");

						resetSecitionLov(secStr);// 设置section lov值变化
					}

				}

				if (!isPA) {
					isPTA = teamRoleUserTree.isUserPTA();
					projAdminPanel.setIsPTA(isPTA);
					nameField.setEnabled(isPA || isPTA);
					idField.setEnabled(isPA || isPTA);
					descField.setEnabled(isPA || isPTA);
					activeStatusRB.setEnabled(isPA || isPTA);
					inactiveStatusRB.setEnabled(isPA || isPTA);
					inactiveInvisibleStatusRB.setEnabled(isPA || isPTA);
					usePrgmSecCB.setEnabled(isPA || isPTA);
					setXMJCPanelEnabled(false);
					setXMLXPanelEnabled(false);
					// setXMRQPanelEnabled(false);
				} else {
					setXMJCPanelEnabled(true);
					setXMLXPanelEnabled(true);
					// setXMRQPanelEnabled(true);
				}

				// add by wuh 2015-5-27
				System.out.println("setXMRQPanelEnabled-->isPA--->" + isPA
						+ "--->teamRoleUserTree.isUserPTA()--->"
						+ teamRoleUserTree.isUserPTA());
				if (isPA || teamRoleUserTree.isUserPTA()) {
					setXMRQPanelEnabled(true);
				} else {
					setXMRQPanelEnabled(false);
				}

				if (teamRoleUserTree.isUserPTA()) {
					setGNPZPanelEnabled(true);
				} else {
					setGNPZPanelEnabled(false);
				}
				TCComponentUser userProject = (TCComponentUser) project
						.getRelatedComponent("owning_user");

				if (session.getUser().getUserId()
						.equals(userProject.getUserId())) {
					setXMJCPanelEnabled(true);
					setXMLXPanelEnabled(true);
					// setXMRQPanelEnabled(true);
				} else {
					setXMJCPanelEnabled(false);
					setXMLXPanelEnabled(false);
					// setXMRQPanelEnabled(false);
				}
				System.out.println("isPA-->" + isPA + "-->isPTA--->" + isPTA);
				closeDateButton.setEnabled(isPA || isPTA);// add by wuh
															// 2014-5-27
				eqmRadioButton.setEnabled(isPA);// add 2014-6-5
			} else {
				idField.setText(null);
				nameField.setText(null);
				descField.setText(null);
				activeStatusRB.setSelected(true);
				usePrgmSecCB.setSelected(false);
				teamRoleUserTree.loadTeam(null);

				setXMJCPanelEnabled(true);
				setXMLXPanelEnabled(true);
				// setXMRQPanelEnabled(true);
				if (!isPA) {
					isPTA = false;
					projAdminPanel.setIsPTA(false);
					nameField.setEnabled(isPA || isPTA);
					idField.setEnabled(isPA || isPTA);
					descField.setEnabled(isPA || isPTA);
					activeStatusRB.setEnabled(isPA || isPTA);
					inactiveStatusRB.setEnabled(isPA || isPTA);
					inactiveInvisibleStatusRB.setEnabled(isPA || isPTA);
					usePrgmSecCB.setEnabled(isPA || isPTA);
				}
				if (isPA) {
					setXMRQPanelEnabled(true);// add wuh
					setGNPZPanelEnabled(true);
				}

				closeDateButton.setEnabled(isPA || isPTA);// add by wuh
															// 2014-5-27
				eqmRadioButton.setEnabled(isPA);// add 2014-6-5
			}

			validateButtons();
			invalidate();
			repaint();
		} catch (Exception exception) {
			MessageBox.post(Utilities.getCurrentFrame(), exception);
		}
	}

	public void setIsPA(boolean flag) {
		isPA = flag;
		nameField.setEnabled(isPA || isPTA);
		idField.setEnabled(isPA || isPTA);
		descField.setEnabled(isPA || isPTA);
		activeStatusRB.setEnabled(isPA || isPTA);
		inactiveStatusRB.setEnabled(isPA || isPTA);
		inactiveInvisibleStatusRB.setEnabled(isPA || isPTA);
		usePrgmSecCB.setEnabled(isPA || isPTA);
		validateButtons();
	}

	public String getProjectID() {
		return idField.getText();
	}

	public String getProjectName() {
		return nameField.getText();
	}

	public String getProjectDesc() {
		return descField.getText();
	}

	public boolean getProjectStatus() {
		return activeStatusRB.isSelected();
	}

	public boolean getProgramVisibility() {
		return !inactiveInvisibleStatusRB.isSelected();
	}

	public boolean getProgramSecurity() {
		return usePrgmSecCB.isSelected();
	}

	private JButton createButton(ImageIcon imageicon, String s) {
		JButton jbutton = new JButton(imageicon);
		jbutton.setMargin(new Insets(0, 0, 0, 0));
		jbutton.setFocusPainted(false);
		jbutton.setEnabled(true);
		jbutton.setToolTipText(s);
		return jbutton;
	}

	private void validateButtons() {
		boolean flag = teamRoleUserTree.teamDefined();
		String s = getProjectName();
		String s1 = getProjectID();
		String s2 = getProjectDesc();
		boolean flag1 = getProgramVisibility() != projectVisibility;
		boolean flag2 = getProgramSecurity() != projectUseProgSec;
		s = s == null ? null : s.trim();
		s1 = s1 == null ? null : s1.trim();
		s2 = s2 == null ? null : s2.trim();
		boolean flag3 = false;
		if (s1 != null && projectID != null && !s1.equals(projectID))
			flag3 = true;
		boolean flag4 = false;
		if (s != null && projectName != null && !s.equals(projectName))
			flag4 = true;
		boolean flag5 = false;
		if (s2 != null && projectDesc != null && !s2.equals(projectDesc))
			flag5 = true;

		// 新增所有必填项判断创建按钮状态
		// 项目基础
		boolean oemNameFlag = false;// 客户名称
		boolean modeNameFlag = false;
		boolean secFlag = false;// 所属科室
		boolean diviFlag = false;// 所属部门
		boolean tlFlag = false;// 项目负责人
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

		if (tlValue != null && tlStr != null && !tlValue.equals(tlStr))
			tlFlag = true;

		// add by wuh 2014-5-26
		Date closeDateValue = closeDateButton.getDate();
		if (closeDateValue != null || closeDateStr != null) {
			if (closeDateValue != null
					&& closeDateStr != null
					&& closeDateValue.toString()
							.equals(closeDateStr.toString())) {
				closeFlag = false;
			} else {
				closeFlag = true;
			}
		}

		System.out.println("jci6_CloseDate---->" + closeFlag);
		// add by wuh 2014-6-5
		boolean eqmVal = eqmRadioButton.isSelected();
		if (eqmVal == eqmStr) {
			eqmFlag = false;
		} else {
			eqmFlag = true;
		}

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
		if (targetPosField.getSelectedItem() != null)
			targetPosValue = targetPosField.getSelectedItem().toString();// getComponentValue(targetPosField);

		if (targetPosValue != null
				&& targetPosStr != null
				&& !"".equals(targetPosValue)
				&& !"".equals(targetPosStr)
				&& Double.parseDouble(targetPosValue) != Double
						.parseDouble(targetPosStr))
			targetPosFlag = true;
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

		if (typePanel2.isVisible()) {
			newCreateFlag = newCreateFlag && !"".equals(targetPosValue)
					&& targetPosValue.length() > 0;
		}

		// 新增判断所有可编辑属性是否变化，判断修改按钮状态，不包括PDX只读页，因为此页只读没有修改可能

		Date pStartValue = pStartDate.getDate();
		if (pStartValue != null || pStartStr != null) {

			if (pStartValue != null && pStartStr != null
					&& pStartValue.toString().equals(pStartStr.toString())) {
				pStartFlag = false;
			} else {
				pStartFlag = true;
			}
		}

		Date pEndValue = pEndDate.getDate();
		if (pEndValue != null || pEndStr != null) {

			if (pEndValue != null && pEndStr != null
					&& pEndValue.toString().equals(pEndStr.toString())) {
				pEndFlag = false;
			} else {
				pEndFlag = true;
			}
		}

		Date lcValue = lcDate.getDate();
		if (lcValue != null || lcStr != null) {
			if (lcValue != null && lcStr != null
					&& lcValue.toString().equals(lcStr.toString())) {
				lcFlag = false;
			} else {
				lcFlag = true;
			}
		}
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

		// 5.12-SMTE
		SMTEFlag = SMTE_lov.isValueModified();
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

		createButton.setEnabled(isPA && s != null && s.length() > 0
				&& s1 != null && s1.length() > 0 && flag
				&& (project == null || flag3 && flag4) && newCreateFlag);
		if (project != null) {
			boolean flag6 = activeStatusRB.isSelected() != projectStatus;
			boolean flag7 = teamRoleUserTree.teamModified();
			modifyButton.setEnabled((isPA || isPTA)
					&& flag
					&& (flag4 || flag3 || flag6 || flag5 || flag7 || flag1
							|| flag2 || newModifyFlag));
		} else {
			modifyButton.setEnabled(false);
		}
		if (project != null)
			copyButton.setEnabled(isPA && s != null && s.length() > 0
					&& s1 != null && s1.length() > 0 && flag && flag3 && flag4);
		else
			copyButton.setEnabled(false);
		if (deleteButton != null)
			deleteButton.setEnabled(isPA && project != null);
		privilegedUserButton.setEnabled(flag);
		teamAdminButton.setEnabled(flag);
		orgTreeEventHandler.validateAddMemberButton();
		teamRoleUserTree.validateRemoveMemberButton();
	}

	private boolean rulesArePresent() {
		if (!checked)
			try {
				TCTextService tctextservice = session.getTextService();
				TCComponentAMTreeType tccomponentamtreetype = (TCComponentAMTreeType) session
						.getTypeComponent("AM_tree");
				com.teamcenter.rac.kernel.TCComponentAMTree atccomponentamtree[][] = {
						tccomponentamtreetype.findNodes(null, tctextservice
								.getTextValue("k_am_rule_in_inactive_program"),
								null),
						tccomponentamtreetype.findNodes(null, tctextservice
								.getTextValue("k_am_rule_is_program_member"),
								null),
						tccomponentamtreetype.findNodes(null, tctextservice
								.getTextValue("k_am_rule_in_current_program"),
								null) };
				if (atccomponentamtree[0].length == 0
						|| atccomponentamtree[1].length == 0
						|| atccomponentamtree[2].length == 0)
					rulesPresent = false;
				else
					rulesPresent = true;
				checked = true;
			} catch (TCException tcexception) {
				MessageBox.post(tcexception);
			}
		return rulesPresent;
	}

	@Override
	public void closeSignaled() {
		session.removeAIFComponentEventListener(this);
	}

	@Override
	public void processComponentEvents(AIFComponentEvent aaifcomponentevent[]) {
		AIFComponentEvent aaifcomponentevent1[] = aaifcomponentevent;
		int i = aaifcomponentevent1.length;
		for (int j = 0; j < i; j++) {
			AIFComponentEvent aifcomponentevent = aaifcomponentevent1[j];
			InterfaceAIFComponent interfaceaifcomponent = aifcomponentevent
					.getComponent();
			if (interfaceaifcomponent instanceof TCComponentGroupMember) {
				if (aifcomponentevent instanceof AIFComponentCreateEvent) {
					teamRoleUserTree
							.addNewGroupMember((TCComponentGroupMember) interfaceaifcomponent);
					continue;
				}
				if (aifcomponentevent instanceof AIFComponentDeleteEvent)
					teamRoleUserTree
							.removeGroupMember((TCComponentGroupMember) interfaceaifcomponent);
				continue;
			}
			if (interfaceaifcomponent instanceof TCComponentGroup) {
				if (aifcomponentevent instanceof AIFComponentChangeEvent) {
					teamRoleUserTree
							.processGroupChange((TCComponentGroup) interfaceaifcomponent);
					continue;
				}
				if (aifcomponentevent instanceof AIFComponentCreateEvent) {
					try {
						TCComponent tccomponent = ((TCComponentGroup) interfaceaifcomponent)
								.getReferenceProperty("parent");
						if (tccomponent != null)
							teamRoleUserTree
									.processGroupChange((TCComponentGroup) tccomponent);
					} catch (TCException tcexception) {
					}
					continue;
				}
				if (aifcomponentevent instanceof AIFComponentDeleteEvent)
					teamRoleUserTree
							.removeGroup((TCComponentGroup) interfaceaifcomponent);
				continue;
			}
			if (project != null
					&& (interfaceaifcomponent instanceof TCComponentProject)
					&& (aifcomponentevent instanceof AIFComponentPropertyChangeEvent)
					&& ((AIFComponentPropertyChangeEvent) aifcomponentevent)
							.isChangedProperty("owning_user")
					&& project.equals(interfaceaifcomponent)
					&& teamRoleUserTree != null)
				teamRoleUserTree.loadTeam(project);
		}

	}

	public void checkUserData(TCComponentUser tccomponentuser) {
		if (tccomponentuser == null)
			return;
		try {
			com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.Filter afilter[] = new com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.Filter[2];
			afilter[0] = new com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.Filter();
			afilter[0].sourceTypeName = "WorkspaceObject";
			afilter[0].name = "owning_user";
			afilter[0].value = tccomponentuser.toString();
			afilter[1] = new com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.Filter();
			afilter[1].sourceTypeName = "WorkspaceObject";
			afilter[1].name = "owning_project";
			afilter[1].value = project.toString();
			com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.GetFilteredProjectDataInputData agetfilteredprojectdatainputdata[] = new com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.GetFilteredProjectDataInputData[1];
			agetfilteredprojectdatainputdata[0] = new com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.GetFilteredProjectDataInputData();
			agetfilteredprojectdatainputdata[0].projectID = project
					.getProperty("project_id");
			agetfilteredprojectdatainputdata[0].filters = afilter;
			ProjectLevelSecurityService projectlevelsecurityservice = ProjectLevelSecurityService
					.getService(session);
			com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.GetFilteredProjectDataResponse getfilteredprojectdataresponse = projectlevelsecurityservice
					.getFilteredProjectData(agetfilteredprojectdatainputdata);
			TCComponent atccomponent[] = getfilteredprojectdataresponse.output[0].filteredData;
			if (atccomponent.length == 0)
				shouldRemove = true;
			else
				shouldRemove = false;
		} catch (Exception exception) {
			shouldRemove = false;
			MessageBox.post(exception);
		}
	}

	public void checkGroupData(String s) {
		if (s == null)
			return;
		try {
			com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.Filter afilter[] = new com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.Filter[2];
			afilter[0] = new com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.Filter();
			afilter[0].sourceTypeName = "WorkspaceObject";
			afilter[0].name = "owning_group";
			afilter[0].value = s.toString();
			afilter[1] = new com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.Filter();
			afilter[1].sourceTypeName = "WorkspaceObject";
			afilter[1].name = "owning_project";
			afilter[1].value = project.toString();
			com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.GetFilteredProjectDataInputData agetfilteredprojectdatainputdata[] = new com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.GetFilteredProjectDataInputData[1];
			agetfilteredprojectdatainputdata[0] = new com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.GetFilteredProjectDataInputData();
			agetfilteredprojectdatainputdata[0].projectID = project
					.getProperty("project_id");
			agetfilteredprojectdatainputdata[0].filters = afilter;
			ProjectLevelSecurityService projectlevelsecurityservice = ProjectLevelSecurityService
					.getService(session);
			com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.GetFilteredProjectDataResponse getfilteredprojectdataresponse = projectlevelsecurityservice
					.getFilteredProjectData(agetfilteredprojectdatainputdata);
			TCComponent atccomponent[] = getfilteredprojectdataresponse.output[0].filteredData;
			if (atccomponent.length == 0)
				shouldRemove = true;
			else
				shouldRemove = false;
		} catch (Exception exception) {
			shouldRemove = false;
			MessageBox.post(exception);
		}
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
	private JPanel buildDefiningXMJCAttrPanel() {
		JPanel jpanel1 = new JPanel(new PropertyLayout());
		try {
			// 项目ID
			JLabel idJlabel = new JLabel(reg.getString("projectID.LABEL", "ID"));
			idField = new iTextField(18, 64, true);
			helpButton = new JButton(reg.getImageIcon("help_jci6.ICON"));
			helpButton.setPreferredSize(new Dimension(25, 20));
			helpButton.setToolTipText(reg.getString("help_tip"));
			helpButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					MessageBox.post(reg.getString("help_text"), "提示！",
							MessageBox.INFORMATION);
				}

			});
			idField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent keyevent) {
					enableCopyPaste(keyevent, idField);
					validateButtons();
				}

			});
			idField.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent mouseevent) {
					projAdminPanel.clearProjectsTreeSelection();
				}

			});
			// 项目名称
			JLabel nameJlabel = new JLabel(reg.getString("projectName.LABEL",
					"Name"));
			nameField = new iTextField(18, 32, true);
			nameField.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent mouseevent) {
					projAdminPanel.clearProjectsTreeSelection();
				}

			});
			nameField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent keyevent) {
					enableCopyPaste(keyevent, nameField);
					validateButtons();
				}

			});
			// 项目描述
			JLabel descJlabel = new JLabel(reg.getString("projectDesc.LABEL",
					"Description"));
			descField = new iTextArea(3, 28, 240, false);
			descField.setLineWrap(true);
			descField.setWrapStyleWord(true);
			descField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent keyevent) {
					enableCopyPaste(keyevent, descField);
					validateButtons();
				}

			});
			descField.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent mouseevent) {
					projAdminPanel.clearProjectsTreeSelection();
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

				@Override
				public void mousePressed(MouseEvent mouseevent) {

					validateButtons();
				}

			});
			modeNameField.addKeyListener(new KeyAdapter() {

				@Override
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

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated me1111thod stub

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
			eqmRadioButton = new JRadioButton();
			eqmRadioButton.setEnabled(false);

			eqmRadioButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					// TODO Auto-generated method stub
					validateButtons();
				}
			});

			jpanel1.add("1.1.right.center", idJlabel);
			jpanel1.add("1.2.left.center", idField);
			jpanel1.add("1.3.left.center", helpButton);
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
			jpanel1.add("11.1.right.center", eqmJlabel);
			jpanel1.add("11.2.center.center", eqmRadioButton);
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jpanel1;

	}

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
			activeStatusBox = new iComboBox(
					reg.getStringArray("ActiveStatus_jci6"));
			// activeStatusBox.addItem(reg.getStringArray("ActiveStatus_jci6")[0]);
			activeStatusBox.setMandatory(true);
			activeStatusBox.setPreferredSize(new Dimension(200, 20));
			activeStatusBox.setMinimumSize(new Dimension(200, 20));
			activeStatusBox.addActionListener(this);

			// 项目状态
			TCPropertyDescriptor programStatusTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_ProgramState");
			JLabel programStatusJlabel = new JLabel(
					programStatusTCProperty.getDisplayName());
			projectStatusBox = new iComboBox();
			projectStatusBox.setMandatory(true);
			projectStatusBox.setPreferredSize(new Dimension(200, 20));
			projectStatusBox.setMinimumSize(new Dimension(200, 20));
			projectStatusBox.addActionListener(this);
			// 种类
			TCPropertyDescriptor categoryTCProperty = tcComponentItemRevisionType
					.getPropertyDescriptor("jci6_Category");
			JLabel categoryJlabel = new JLabel(
					categoryTCProperty.getDisplayName());
			categoryBox = new iComboBox();
			categoryBox.setMandatory(true);
			categoryBox.setPreferredSize(new Dimension(200, 20));
			categoryBox.setMinimumSize(new Dimension(200, 20));
			categoryBox.addActionListener(this);
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

				@Override
				public void mousePressed(MouseEvent mouseevent) {

					validateButtons();
				}

			});
			
			
			
			epicTextField.addKeyListener(new KeyAdapter() {

				@Override
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

				@Override
				public void mousePressed(MouseEvent mouseevent) {
					validateButtons();
				}

			});
			functionTextField.addKeyListener(new KeyAdapter() {

				@Override
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
	 * 激活状态、项目状态、目录三者联动事件处理
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if (e.getSource() == activeStatusBox) {
			loadProjectStatusBoxData();

			validateButtons();

		} else if (e.getSource() == projectStatusBox) {// categoryBox
			loadCategoryBox();

			setTargetPossibilityVisiiable("");
			validateButtons();
		} else if (e.getSource() == categoryBox) {//

			validateButtons();
		}
		// }else if(e.getSource() == SMTE_lov){
		// validateButtons();
		// }
	}

	/*
	 * 
	 * 设置targetPossibility状态及值
	 */
	private void setTargetPossibilityVisiiable(String targetPosStr) {
		if (projectStatusBox.getSelectedItem() != null
				&& !"".equals(projectStatusBox.getSelectedItem().toString()
						.trim())) {
			String strSelect = projectStatusBox.getSelectedItem().toString()
					.trim();
			String values[] = reg.getStringArray("TargetPossibility_jci6");
			Vector<String> tpVec = new Vector<String>();
			if (values != null && values.length > 0) {
				for (int i = 0; i < values.length; i++) {
					tpVec.add(values[i]);
				}
			}
			if (tpVec.contains(strSelect)) {
				sepl.setVisible(true);
				typePanel2.setVisible(true);
				targetPosField.setText(targetPosStr);
			} else {
				sepl.setVisible(false);
				typePanel2.setVisible(false);
				targetPosField.setText("");
			}

		} else {
			sepl.setVisible(false);
			typePanel2.setVisible(false);
			targetPosField.setText("");
		}

	}

	/*
	 * 
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
			String values[] = reg.getStringArray(strSelect + "_jci6");
			projectStatusBox.addItems(values);
		}
		projectStatusBox.getTextField().setText(text_projectStatus);
		categoryBox.getTextField().setText(text_category);
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
			String values[] = reg.getStringArray(strSelect + "_jci6");
			categoryBox.addItems(values);
		}
		categoryBox.getTextField().setText(text_category);
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
		boolean eqmflag = eqmRadioButton.isSelected();

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
				userService.call("open_or_close_pass", new Object[] { 1 });
				newItem.setProperty("jci6_EPIC_PN", epic);
				userService.call("open_or_close_pass", new Object[] { 0 });

				// 7.10修改--项目资料库权限
				TCProperty[] tcProperty = newIr
						.getTCProperties(propProInfoNames);
				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[0].setStringValueData(oemName);
				userService.call("open_or_close_pass", new Object[] { 0 });
				TCComponentGroupType groupType = (TCComponentGroupType) session
						.getTypeComponent("Group");

				System.out.println("--------设置完OEM的name------");

				// jci6_OEMModelName
				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[24].setStringValueData(modeName);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完modeNameLOV------");

				if (sec != null && !"".equals(sec)) {

					TCComponentGroup secGroup = groupType.find(sec);
					userService.call("open_or_close_pass", new Object[] { 1 });
					tcProperty[1].setReferenceValueData(secGroup);
					userService.call("open_or_close_pass", new Object[] { 0 });

					System.out.println("--------设置完OsecGroup------");
				}
				if (divi != null && !"".equals(divi)) {

					TCComponentGroup diviGroup = groupType.find(divi);
					userService.call("open_or_close_pass", new Object[] { 1 });
					tcProperty[2].setReferenceValueData(diviGroup);
					userService.call("open_or_close_pass", new Object[] { 0 });

					System.out.println("--------设置完diviGroup------");
				}

				// add by wuh 2014-5-26
				if (closeDate != null) {
					userService.call("open_or_close_pass", new Object[] { 1 });
					newItem.setDateProperty("jci6_CloseDate", closeDate);
					;
					userService.call("open_or_close_pass", new Object[] { 0 });
				}

				// add by wuh 2014-6-5
				if (eqmflag) {
					userService.call("open_or_close_pass", new Object[] { 1 });
					newItem.setLogicalProperty("jci6_IsRunEQM", eqmflag);
					userService.call("open_or_close_pass", new Object[] { 0 });
				}

				if (!"".equals(targetPos)) {

					userService.call("open_or_close_pass", new Object[] { 1 });
					tcProperty[4].setDoubleValueData(Double
							.parseDouble(targetPos));
					userService.call("open_or_close_pass", new Object[] { 0 });

					System.out.println("--------设置完TargetPos------");

				}

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[5].setLogicalValueData(activeStatusLogic);
				userService.call("open_or_close_pass", new Object[] { 0 });

				System.out.println("--------设置完activeStatus------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[6].setStringValueData(projectStatus);
				userService.call("open_or_close_pass", new Object[] { 0 });

				System.out.println("--------设置完programnetatus------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[7].setStringValueData(category);
				userService.call("open_or_close_pass", new Object[] { 0 });

				System.out.println("--------设置完category------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[8].setStringValueData(product);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完product------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[9].setStringValueData(type);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完TYPE------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[10].setDateValueData(pStart);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完StartDate------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[11].setDateValueData(pEnd);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完EndDate------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[12].setDateValueData(lc);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完SOP------");

				if (!"".equals(sopfy)) {

					userService.call("open_or_close_pass", new Object[] { 1 });
					tcProperty[13].setIntValueData(Integer.parseInt(sopfy));
					userService.call("open_or_close_pass", new Object[] { 0 });
					System.out.println("--------设置完SOPFY------");
				}
				if (!"".equals(modelYear)) {

					userService.call("open_or_close_pass", new Object[] { 1 });
					tcProperty[14].setIntValueData(Integer.parseInt(modelYear));
					userService.call("open_or_close_pass", new Object[] { 0 });
					System.out.println("--------设置完modelYear------");
				}

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[15].setStringValueData(function);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完function------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[16].setStringValueData(frontSS);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完frontSS------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[17].setStringValueData(rearSS);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完rearSS------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[18].setStringValueData(track);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完track------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[19].setStringValueData(recliner);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完recliner------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[20].setStringValueData(pump);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完pump------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[21].setStringValueData(vta);
				userService.call("open_or_close_pass", new Object[] { 0 });
				System.out.println("--------设置完VTA------");

				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[22].setStringValueData(latch);
				userService.call("open_or_close_pass", new Object[] { 0 });
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
				userService.call("open_or_close_pass", new Object[] { 1 });
				tcProperty[23].setDoubleValueData(Double.parseDouble(equ));
				userService.call("open_or_close_pass", new Object[] { 0 });
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
								userService.call("open_or_close_pass",
										new Object[] { 1 });
								tcProperty[3].setReferenceValueData(user);
								userService.call("open_or_close_pass",
										new Object[] { 0 });
								// System.out.println("--------设置完tlLov的user------");
							} else {
								TCComponent[] users = query(session,
										QUERY_NAME,
										new String[] { "PersonName" },
										new String[] { tl });
								userService.call("open_or_close_pass",
										new Object[] { 1 });
								tcProperty[3].setReferenceValueData(users[0]);
								userService.call("open_or_close_pass",
										new Object[] { 0 });
								System.out
										.println("--------------设置完tlLov的user-过查询找到！！！------");
							}
						}
					} else {
						TCComponentUser tlUser = (TCComponentUser) tlLov
								.getSelectedObject();
						if (tlUser != null) {
							userService.call("open_or_close_pass",
									new Object[] { 1 });
							tcProperty[3].setReferenceValueData(tlUser);
							userService.call("open_or_close_pass",
									new Object[] { 0 });

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
				newIr.setTCProperties(tcProperty);
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
	 * 设置特殊情况lov必填项
	 */
	public void setRequire(LOVComboBox lov, TCPropertyDescriptor des) {
		if (des.isRequired())
			lov.setMandatory(true);
	}

	/*
	 * 
	 * 修改项目信息属性
	 */
	public void setProjectInfoProperties(TCComponentItem projectInfoItem) {

		System.out.println("----按了Modify按钮后修改项目信息开始！！！----");

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
			String targetPosValue = targetPosField.getTextField().getText();// getComponentValue(targetPosField);

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

			// 8.22--修改
			tcProperty[3].setReferenceValueData(teamAdmin);
			vector.add(tcProperty[3]);

			System.out.println(teamAdmin
					+ "--------修改 ---Project Team Leader  完成！！========="
					+ tlValue);

			// add　by wuh 2014-5-26
			boolean close_flag = false;
			Date closeDateValue = closeDateButton.getDate();
			TCProperty closeDatePro = null;
			if (closeDateStr == null && closeDateValue != null) {
				System.out.println("null---->not null");
				userService.call("open_or_close_pass", new Object[] { 1 });
				projectInfoItem.setDateProperty("jci6_CloseDate",
						closeDateValue);
				userService.call("open_or_close_pass", new Object[] { 0 });
				close_flag = true;
			} else if (closeDateStr != null && closeDateValue != null
					&& !closeDateStr.equals(closeDateValue)) {
				System.out.println("not null--->not equal");
				userService.call("open_or_close_pass", new Object[] { 1 });
				projectInfoItem.setDateProperty("jci6_CloseDate",
						closeDateValue);
				userService.call("open_or_close_pass", new Object[] { 0 });
				close_flag = true;
			} else if (closeDateStr != null && closeDateValue == null) {
				userService.call("open_or_close_pass", new Object[] { 1 });
				projectInfoItem.setDateProperty("jci6_CloseDate",
						closeDateValue);
				userService.call("open_or_close_pass", new Object[] { 0 });
				close_flag = true;
			}

			// add by wuh 2014-6-5
			boolean eqm_flag = false;
			boolean eqmVal = eqmRadioButton.isSelected();
			if (eqmStr == eqmVal) {
				System.out.println("EQM not change");
			} else {
				System.out.println("EQM changed");
				userService.call("open_or_close_pass", new Object[] { 1 });
				projectInfoItem.setLogicalProperty("jci6_IsRunEQM", eqmVal);
				userService.call("open_or_close_pass", new Object[] { 0 });
				eqm_flag = true;
			}

			if (close_flag && eqm_flag) {
				// 刷新
				projectInfoItem.refresh();
				closeDateStr = projectInfoItem.getTCProperty("jci6_CloseDate")
						.getDateValue();
				// System.out.println();
				eqmStr = projectInfoItem.getTCProperty("jci6_IsRunEQM")
						.getLogicalValue();
			} else if (close_flag && !eqm_flag) {
				// 刷新
				projectInfoItem.refresh();
				closeDateStr = projectInfoItem.getTCProperty("jci6_CloseDate")
						.getDateValue();
			} else if (eqm_flag && !close_flag) {
				projectInfoItem.refresh();
				eqmStr = projectInfoItem.getTCProperty("jci6_IsRunEQM")
						.getLogicalValue();
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

			if (pStartValueDate != null && pStartStr != null
					&& !pStartValueDate.toString().equals(pStartStr.toString())) {
				vector.add(tcProperty[10]);
				tcProperty[10].setDateValueData(pStartValueDate);
			} else if (pStartValueDate == null && pStartStr != null) {
				vector.add(tcProperty[10]);
				tcProperty[10].setDateValueData(pStartValueDate);
			} else if (pStartValueDate != null && pStartStr == null) {
				vector.add(tcProperty[10]);
				tcProperty[10].setDateValueData(pStartValueDate);
			}

			if (pEndValueDate != null && pEndStr != null
					&& !pEndValueDate.toString().equals(pEndStr.toString())) {
				vector.add(tcProperty[11]);
				tcProperty[11].setDateValueData(pEndValueDate);
			} else if (pEndValueDate == null && pEndStr != null) {
				vector.add(tcProperty[11]);
				tcProperty[11].setDateValueData(pEndValueDate);
			} else if (pEndValueDate != null && pEndStr == null) {
				vector.add(tcProperty[11]);
				tcProperty[11].setDateValueData(pEndValueDate);
			}

			if (lcValueDate != null && lcStr != null
					&& !lcValueDate.toString().equals(lcStr.toString())) {
				vector.add(tcProperty[12]);
				tcProperty[12].setDateValueData(lcValueDate);
			} else if (lcValueDate == null && lcStr != null) {
				vector.add(tcProperty[12]);
				tcProperty[12].setDateValueData(lcValueDate);
			} else if (lcValueDate != null && lcStr == null) {
				vector.add(tcProperty[12]);
				tcProperty[12].setDateValueData(lcValueDate);
			}

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

			// 5.12-SMTE
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
					if ("Active".equals(activeStatusValue))
						activeStatusStr = "Active";
					else
						activeStatusStr = "Inactive";

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
		eqmRadioButton.setSelected(false);

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
	 * 设置项目基础页是否可编辑
	 */
	public void setXMJCPanelEnabled(boolean flag) {
		idField.setEnabled(flag);
		helpButton.setEnabled(flag);
		nameField.setEnabled(flag);
		descField.setEnabled(flag);
		oemNameLov.setEnabled(flag);
		modeNameField.setEnabled(flag);
		secLov.setEnabled(flag);
		diviLov.setEnabled(flag);
		tlLov.setEnabled(flag);
		// add by wuh 2014-5-26
		closeDateButton.setEnabled(flag);
		eqmRadioButton.setEnabled(flag);
		targetPosField.setEnabled(flag);
	}

	/*
	 * 
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

	public void setSelectItemRevision(TCComponentItemRevision revision) {
		selectItemRevision = revision;
	}

	public TCComponentItemRevision getSelectItemRevision() {
		return selectItemRevision;
	}

	// add by wuh
	/**
	 * 判断用户有没有指派到时间表任务上
	 * 
	 * @throws Exception
	 */
	private void proMembersMaintainParity() throws Exception {
		proMemberSb.setLength(0);
		groupMemberVec.clear();
		proMemberSb.append(reg.getString("assign_scheduletask"));
		TreePath atreepath[] = teamRoleUserTree.getSelectionPaths();
		for (int i = 0; atreepath != null && i < atreepath.length; i++) {
			TreePath treepath = atreepath[i];
			ProjectDataPanel.TeamRoleUserTreeNode teamroleusertreenode = (ProjectDataPanel.TeamRoleUserTreeNode) treepath
					.getLastPathComponent();

			System.out
					.println("proMembersMaintainParity  teamroleusertreenode-->"
							+ teamroleusertreenode.toString());
			// if (teamroleusertreenode instanceof ProjectTeamChildrenTreeNode)
			// {
			// System.out.println("is ProjectTeamChildrenTreeNode");
			// ProjectTeamChildrenTreeNode projectteamchildrentreenode =
			// (ProjectTeamChildrenTreeNode) teamroleusertreenode;
			//
			// } else {
			// System.out.println("is not ProjectTeamChildrenTreeNode");
			// }
			System.out.println("teamroleusertreenode.type--->"
					+ teamroleusertreenode.type);
			System.out.println("selected object type is--->"
					+ teamroleusertreenode.comp.getType());
			// type=4 选中的就是TCComponentGroupMember,type=2 选中的就是TCComponentRole
			if (teamroleusertreenode.type == 4) {
				TCComponentGroupMember tccomponentgroupmember = (TCComponentGroupMember) teamroleusertreenode.comp;
				groupMemberYZ(tccomponentgroupmember);
				groupMemberVec.add(tccomponentgroupmember);
			} else if (teamroleusertreenode.type == 2) {
				int node_child_cnt = teamroleusertreenode.getChildCount();
				System.out.println("child node count is--->" + node_child_cnt);
				// 得到第一个子
				for (int j = 0; j < node_child_cnt; j++) {
					ProjectDataPanel.TeamRoleUserTreeNode child_node = (ProjectDataPanel.TeamRoleUserTreeNode) teamroleusertreenode
							.getChildAt(j);
					TCComponentGroupMember child_group_member = (TCComponentGroupMember) child_node.comp;
					System.out.println("child node group member name is---->"
							+ child_group_member.getProperty("object_name"));
					groupMemberYZ(child_group_member);
					groupMemberVec.add(child_group_member);
				}
			}
		}
	}

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
					proMemberSb.append("\r\n").append(user_name);
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

	private class TeamRoleUserTreeNode extends DefaultMutableTreeNode {
		public int type;
		public TCComponent comp;
		public TCComponentUser user;
		public String name;
		public int status;

		public TeamRoleUserTreeNode(TCComponent paramTCComponent,
				String paramString) {
			super(paramString);
			this.type = -1;
			this.comp = null;
			this.user = null;
			this.status = ProjectDataPanel.this.PRIVILEGED;
			this.comp = paramTCComponent;
			this.name = paramString;
			if (this.comp instanceof TCComponentGroup) {
				this.type = 0;
				setAllowsChildren(true);
			} else if (this.comp instanceof TCComponentRole) {
				this.type = 2;
				setAllowsChildren(true);
			} else if (this.comp instanceof TCComponentGroupMember) {
				this.type = 4;
				setAllowsChildren(false);
				try {
					this.user = ((TCComponentUser) this.comp
							.getReferenceProperty("the_user"));
				} catch (TCException localTCException) {
					this.user = null;
				}
			} else {
				if (!(this.comp instanceof TCComponentUser))
					return;
				this.type = 3;
				setAllowsChildren(false);
			}
		}

		public TeamRoleUserTreeNode(TCComponent paramTCComponent,
				String paramString, TCComponentUser paramTCComponentUser) {
			this(paramTCComponent, paramString);
			if (this.user != paramTCComponentUser)
				return;
			this.status = ProjectDataPanel.this.TEAMADMIN;
		}

		public TeamRoleUserTreeNode(String paramString, int paramInt) {
			super(paramString);
			this.type = -1;
			this.comp = null;
			this.user = null;
			this.status = ProjectDataPanel.this.PRIVILEGED;
			this.comp = null;
			this.name = paramString;
			this.type = paramInt;
			if ((this.type == 0) || (this.type == 2))
				setAllowsChildren(true);
			else
				setAllowsChildren(false);
		}

		public String toString() {
			return this.name;
		}
	}

}

/*
 * DECOMPILATION REPORT
 * 
 * Decompiled from:
 * E:\workspace_jci6\com.teamcenter.rac.tcapps\com.teamcenter.rac
 * .tcapps_9000.1.0.1.1.1.jar Total time: 656 ms Jad reported messages/errors:
 * Couldn't resolve all exception handlers in method mouseClicked Exit status: 0
 * Caught exceptions:
 */