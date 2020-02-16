package com.teamcenter.rac.project.common;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.aif.kernel.AIFComponentEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentPropertyChangeEvent;
import com.teamcenter.rac.aif.kernel.IOperationService;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.aifrcp.AifrcpPlugin;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.SoaUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentGroupMemberType;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentRoleType;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCExceptionPartial;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.project.Messages;
import com.teamcenter.rac.project.ProjectDataPanel;
import com.teamcenter.rac.project.filter.OrgTreeFilter;
import com.teamcenter.rac.project.filter.OrgTreeFindFilter;
import com.teamcenter.rac.project.filter.ProgramTreeFilter;
import com.teamcenter.rac.project.filter.ProjectTeamFilter;
import com.teamcenter.rac.project.filter.ProjectTeamFindFilter;
import com.teamcenter.rac.project.nodes.OrgTreeContentNode;
import com.teamcenter.rac.project.nodes.OrgTreeContentNode.IC_NodeCache;
import com.teamcenter.rac.project.nodes.ProjectTeamContentNode;
import com.teamcenter.rac.project.nodes.ProjectTeamGroupNode;
import com.teamcenter.rac.project.nodes.ProjectTeamNodeCache;
import com.teamcenter.rac.project.nodes.ProjectTeamRoleNode;
import com.teamcenter.rac.project.nodes.ProjectTeamUserNode;
import com.teamcenter.rac.project.providers.OrgNodeContentProvider;
import com.teamcenter.rac.project.providers.OrgNodeLabelProvider;
import com.teamcenter.rac.project.providers.ProjectTeamContentProvider;
import com.teamcenter.rac.project.providers.ProjectTeamLabelProvider;
import com.teamcenter.rac.project.views.AbstractTCAdminApplicationViewHelper;
import com.teamcenter.rac.project.views.ProjectDefinitionView;
import com.teamcenter.rac.services.ISessionService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.ui.views.providers.GroupRole;
import com.teamcenter.rac.util.ImageUtilities;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.OSGIUtil;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.SWTUIUtilities;
import com.teamcenter.services.internal.rac.core.DataManagementService;
import com.teamcenter.services.internal.rac.core.ProjectLevelSecurityService;
import com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity;
import com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.Filter;
import com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.GetFilteredProjectDataInputData;
import com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.GetFilteredProjectDataOutput;
import com.teamcenter.services.internal.rac.core._2007_06.ProjectLevelSecurity.GetFilteredProjectDataResponse;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectClientId;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectTeamData;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectTeamsResponse;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class ProjectTeamTreeManager extends Composite implements
		InterfaceAIFComponentEventListener {
	private Button addMemberButton;
	private Button removeMemberButton;
	private ProjectTeamFindFilter projectTeamFindFilter;
	private Text m_searchProjectTeamText = null;
	private Text m_searchOrgText = null;
	private TreeViewer m_availableTreeViewer;
	private TreeViewer projectTeamTreeViewer;
	private ProjectTeamFilter m_projectTeamFilter;
	protected ProjectTeamContentNode root = null;
	protected TCComponentGroupMemberType groupMemberType;
	private OrgTreeFindFilter orgFiliter;
	private OrgTreeFilter orgTreeFilter;
	private ProgramTreeFilter prgFiliter;
	protected TCComponentProject m_project = null;
	private ProjectDefinitionView m_projectDefinitionView = null;
	protected boolean isTeamModifed = false;
	public ProjectTeamData[] projectTeamMembers_preModify = null;
	private int _filterType = -1;
	private int _filterTypeProjectTeam = -1;
	private Combo combo = null;
	private StructuredSelection selection;
	private static final Logger logger = Logger
			.getLogger(ProjectTeamTreeManager.class);
	private TCComponentProject m_parentProject = null;

	public void setParentProject(TCComponentProject paramTCComponentProject) {
		this.m_parentProject = paramTCComponentProject;
	}

	@Deprecated
	public static TCComponentRole getProjectAdminRole() {
		TCComponentRole localTCComponentRole = null;
		TCComponentRoleType localTCComponentRoleType = null;
		try {
			localTCComponentRoleType = (TCComponentRoleType) RACUIUtil
					.getTCSession().getTypeComponent("Role");
		} catch (TCException localTCException1) {
			logger.error(localTCException1.getClass().getName(),
					localTCException1);
		}
		try {
			localTCComponentRole = localTCComponentRoleType
					.find("Project Administrator");
		} catch (TCException localTCException2) {
			logger.error(localTCException2.getClass().getName(),
					localTCException2);
		}
		return localTCComponentRole;
	}

	public ProjectTeamTreeManager(Composite paramComposite,
			ProjectDefinitionView paramProjectDefinitionView) {
		super(paramComposite, 0);
		setLayout(new FillLayout());
		SashForm localSashForm = new SashForm(this, 256);
		localSashForm.setLayout(new FillLayout());
		this.m_projectDefinitionView = paramProjectDefinitionView;
		getTCSession().addAIFComponentEventListener(this);
		Composite localComposite1 = new Composite(localSashForm, 4);
		Composite localComposite2 = new Composite(localSashForm, 4);
		constructOrganizationTreeSash(localComposite1);
		constructProjectTeamSash(localComposite2);
		localSashForm.setWeights(new int[] { 45, 55 });
		try {
			this.groupMemberType = ((TCComponentGroupMemberType) RACUIUtil
					.getTCSession().getTypeComponent("GroupMember"));
		} catch (TCException localTCException) {
			logger.error(localTCException.getClass().getName(),
					localTCException);
		}
	}

	//add by wuwei
	private void constructProjectTeamSash(Composite paramComposite) {
		GridLayout localGridLayout1 = new GridLayout(2, false);
		localGridLayout1.marginHeight = 0;
		localGridLayout1.marginWidth = 0;
		paramComposite.setLayout(localGridLayout1);
		createAvailableHeader(paramComposite);
		Composite localComposite = new Composite(paramComposite, 0);
		localComposite.setLayoutData(new GridData(5, 5));
		this.projectTeamTreeViewer = new TreeViewer(paramComposite, 2050);
		this.projectTeamTreeViewer.getTree().setLinesVisible(true);
		this.projectTeamTreeViewer.getTree().setHeaderVisible(true);
		this.m_projectTeamFilter = new ProjectTeamFilter();
		this.projectTeamTreeViewer.addFilter(this.m_projectTeamFilter);
		this.projectTeamTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(
							SelectionChangedEvent paramAnonymousSelectionChangedEvent) {
						if (ProjectTeamTreeManager.this.selection != null) {
							ProjectTeamLabelProvider localProjectTeamLabelProvider = new ProjectTeamLabelProvider();
							String str = localProjectTeamLabelProvider
									.getColumnText(
											ProjectTeamTreeManager.this.selection
													.getFirstElement(), 0);
							TreeItem[] arrayOfTreeItem1 = ProjectTeamTreeManager.this.projectTeamTreeViewer
									.getTree().getItems();
							Object localObject = null;
							if ((arrayOfTreeItem1 != null) && (str != null))
								for (TreeItem localTreeItem : arrayOfTreeItem1)
									if (localTreeItem.toString().contains(str)) {
										localObject = localTreeItem;
										break;
									}
							if (localObject != null)
								ProjectTeamTreeManager.this.projectTeamTreeViewer
										.getTree().setSelection(
												(TreeItem) localObject);
						}
					}
				});
		this.projectTeamTreeViewer.getTree().addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(
							SelectionEvent paramAnonymousSelectionEvent) {
						super.widgetSelected(paramAnonymousSelectionEvent);
						if ((ProjectTeamTreeManager.this.m_project != null)
								&& (ProjectTeamTreeManager.this.getRoot() != null)) {
							ProjectTeamStatus localProjectTeamStatus = (ProjectTeamStatus) ProjectTeamTreeManager.this
									.getRoot().getObject();
							TCComponentUser localTCComponentUser = ProjectTeamTreeManager.this.m_project
									.getSession().getUser();
							int i = 0;
							if ((localProjectTeamStatus
									.isAProjectTeamAdminMember(localTCComponentUser))
									|| (ProjectTeamTreeManager.this.m_projectDefinitionView
											.isPTA()))
								i = 1;
							ProjectTeamTreeManager.this
									.setButtonStatus(
											false,
											(i != 0)
													&& (ProjectTeamTreeManager.this.m_projectDefinitionView
															.isTeamInherited()));
							return;
						}
						ProjectTeamTreeManager.this
								.setButtonStatus(false, true);
					}
				});
		Image localImage1 = ImageUtilities.getImageDescriptor(
				ProjectConstants.projectReg
						.getImageIcon("projectDefinitionTab.ICON"))
				.createImage();
		Object[] arrayOfObject = { TCComponentProject
				.getDisplayName(getTCSession()) };
		String str1 = Messages.getFormattedString("setDefaultProjectMenu.NAME",
				arrayOfObject);
		String str2 = Messages.getString("setPrivUserMenu.NAME");
		String str3 = Messages.getString("unsetPrivUserMenu.NAME");
		//String str4 = Messages.getFormattedString("selectTeamAdminButton.TIP",
		//		arrayOfObject);
		String str4 ="Define Project Team Leader";
		
		Registry localRegistry = Registry
				.getRegistry("com.teamcenter.rac.project.project");
		Image localImage2 = ImageUtilities.getImageDescriptor(
				localRegistry.getImageIcon("PU.ICON")).createImage();
		Image localImage3 = ImageUtilities.getImageDescriptor(
				localRegistry.getImageIcon("NPU.ICON")).createImage();
		Image localImage4 = ImageUtilities.getImageDescriptor(
				localRegistry.getImageIcon("PTA.ICON")).createImage();
		final Menu localMenu = new Menu(paramComposite.getShell(), 8);
		MenuItem localMenuItem =null;
		/*
		MenuItem localMenuItem = new MenuItem(localMenu, 8);
		
		localMenuItem.setText(str1);
		localMenuItem.setImage(localImage1);
		localMenuItem.setEnabled(false);
		localMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				if (ProjectTeamTreeManager.this.getProject() != null) {
					TreeItem[] arrayOfTreeItem = ProjectTeamTreeManager.this.projectTeamTreeViewer
							.getTree().getSelection();
					setDefaultProjectForSelection(arrayOfTreeItem);
				}
			}

			private void setDefaultProjectForSelection(
					TreeItem[] paramAnonymousArrayOfTreeItem) {
				ArrayList localArrayList = new ArrayList();
				for (TreeItem arrayOfTCComponentUser : paramAnonymousArrayOfTreeItem)
					if ((arrayOfTCComponentUser.getData() instanceof ProjectTeamContentNode)) {
						ProjectTeamContentNode localProjectTeamContentNode = (ProjectTeamContentNode) arrayOfTCComponentUser
								.getData();
						if ((localProjectTeamContentNode.getObject() instanceof IC_UserProxy)) {
							IC_UserProxy localIC_UserProxy = (IC_UserProxy) localProjectTeamContentNode
									.getObject();
							localArrayList.add(localIC_UserProxy.getUser());
						}
					}
				TCComponentUser[] arrayOfTCComponentUser = new TCComponentUser[localArrayList
						.size()];
				arrayOfTCComponentUser = (TCComponentUser[]) localArrayList
						.toArray(arrayOfTCComponentUser);
				DataManagementService localDataManagementService = DataManagementService
						.getService(RACUIUtil.getTCSession());
				ServiceData localServiceData = localDataManagementService
						.setDefaultProjectForProjectMembers(
								ProjectTeamTreeManager.this.getProject(),
								arrayOfTCComponentUser);
				if ((localServiceData != null)
						&& (localServiceData.sizeOfPartialErrors() > 0)) {

					final TCExceptionPartial val$exception = SoaUtil
							.checkPartialErrorsNoThrow(localServiceData);
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageBox.post(val$exception);
						}
					});
				}
			}
		});
		*/
		final ProjectTeamTreeManager localProjectTeamTreeManager = this;
		
		localMenuItem = new MenuItem(localMenu, 8);
		localMenuItem.setText(str2);
		localMenuItem.setImage(localImage2);
		localMenuItem.setEnabled(false);
		localMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				TreeItem[] arrayOfTreeItem = ProjectTeamTreeManager.this.projectTeamTreeViewer
						.getTree().getSelection();
				ProjectTeamTreeManager.this.setPrivilegeOnSelection(
						arrayOfTreeItem, ProjectConstants.PRIVILEGED, true);
				ProjectTeamTreeManager.this.projectTeamTreeViewer.refresh();
			}
		});
		/*
		localMenuItem = new MenuItem(localMenu, 8);
		localMenuItem.setText(str3);
		localMenuItem.setImage(localImage3);
		localMenuItem.setEnabled(false);
		localMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				TreeItem[] arrayOfTreeItem = ProjectTeamTreeManager.this.projectTeamTreeViewer
						.getTree().getSelection();
				ProjectTeamTreeManager.this.setPrivilegeOnSelection(
						arrayOfTreeItem, ProjectConstants.UNPRIVILEGED, true);
				ProjectTeamTreeManager.this.projectTeamTreeViewer.refresh();
			}
		});
		*/
		
		localMenuItem = new MenuItem(localMenu, 8);
		localMenuItem.setText(str4);
		localMenuItem.setImage(localImage4);
		localMenuItem.setEnabled(false);
		localMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				TreeItem[] arrayOfTreeItem = ProjectTeamTreeManager.this.projectTeamTreeViewer
						.getTree().getSelection();
				ProjectTeamTreeManager.this.setPrivilegeOnSelection(
						arrayOfTreeItem, ProjectConstants.TEAMADMIN, true);
				ProjectTeamTreeManager.this.projectTeamTreeViewer.refresh();
			}
		});
		
		localMenu.addListener(22, new Listener() {
			public void handleEvent(Event paramAnonymousEvent) {
				MenuItem[] arrayOfMenuItem = localMenu.getItems();
				boolean bool1 = ProjectManager.getInstance().isLoggedInUserPTA(
						localProjectTeamTreeManager);
				if (((ProjectTeamTreeManager.this.getProject() != null) && (bool1))
						|| (ProjectTeamTreeManager.this.isProjectAdmin())) {
					TreeItem[] arrayOfTreeItem1 = ProjectTeamTreeManager.this.projectTeamTreeViewer
							.getTree().getSelection();
					if ((arrayOfTreeItem1 != null)
							&& (arrayOfTreeItem1.length > 0)) {
						if (ProjectTeamTreeManager.this.m_projectDefinitionView
								.getProgramSecurity()) {
							boolean bool2 = false;
							for (TreeItem localTreeItem : arrayOfTreeItem1)
								if ((localTreeItem.getData() instanceof ProjectTeamUserNode)) {
									ProjectTeamUserNode localProjectTeamUserNode2 = (ProjectTeamUserNode) localTreeItem
											.getData();
									if ((localProjectTeamUserNode2 != null)
											&& (localProjectTeamUserNode2
													.getPrivilege() == ProjectConstants.PRIVILEGED)) {
										bool2 = true;
										break;
									}
								}
							arrayOfMenuItem[0].setEnabled(bool2);
						} else {
							arrayOfMenuItem[0].setEnabled(false);
						}
						Object localObject = arrayOfTreeItem1[0].getData();
						if ((localObject instanceof ProjectTeamContentNode)) {
							if (!ProjectTeamTreeManager.this.m_projectDefinitionView
									.isTeamInherited())
								for (int i = 1; i < arrayOfMenuItem.length; i++)
									arrayOfMenuItem[i].setEnabled(true);
							if ((localObject instanceof ProjectTeamUserNode)) {
								ProjectTeamUserNode localProjectTeamUserNode1 = (ProjectTeamUserNode) localObject;
								if ((localProjectTeamUserNode1 != null) && (localProjectTeamUserNode1.getPrivilege() != ProjectConstants.TEAMADMIN + 1)) {
									if (localProjectTeamUserNode1.getPrivilege() == ProjectConstants.TEAMADMIN)
									{
										arrayOfMenuItem[0].setEnabled(true);
										arrayOfMenuItem[1].setEnabled(true);
									}
									//arrayOfMenuItem[3].setEnabled(false);
									if (localProjectTeamUserNode1.getPrivilege() == ProjectConstants.UNPRIVILEGED)
									{
										arrayOfMenuItem[0].setEnabled(true);
										arrayOfMenuItem[1].setEnabled(true);
									}
									//arrayOfMenuItem[2].setEnabled(false);
									if (localProjectTeamUserNode1.getPrivilege() == ProjectConstants.PRIVILEGED)
									{
										arrayOfMenuItem[0].setEnabled(true);
										arrayOfMenuItem[1].setEnabled(true);
									}
									//arrayOfMenuItem[1].setEnabled(false);
								} else {
									for (int aa = 1; aa < arrayOfMenuItem.length; aa++)
										arrayOfMenuItem[aa].setEnabled(false);
								}
							}
						} else if ((ProjectTeamTreeManager.this
								.isProjectAdmin())
								&& (ProjectTeamTreeManager.this.getProject() == null)
								&& (!ProjectTeamTreeManager.this.m_projectDefinitionView
										.isTeamInherited())) {
							for (int j = 1; j < arrayOfMenuItem.length; j++)
								arrayOfMenuItem[j].setEnabled(true);
						}
					}
				}
			}
		});
		
		this.projectTeamTreeViewer.getTree().setMenu(localMenu);
		String[] arrayOfString = { Messages.getString("projectName.LABEL"),
				Messages.getString("projectStatus.LABEL") };
		TreeViewerColumn localTreeViewerColumn = new TreeViewerColumn(
				this.projectTeamTreeViewer, 0, 0);
		localTreeViewerColumn.getColumn().setText(arrayOfString[0]);
		localTreeViewerColumn.getColumn().setWidth(495);
		localTreeViewerColumn = new TreeViewerColumn(
				this.projectTeamTreeViewer, 0, 1);
		localTreeViewerColumn.getColumn().setText(arrayOfString[1]);
		localTreeViewerColumn.getColumn().setWidth(5);//add by wuwei--隐藏列
	
		/*
		this.projectTeamTreeViewer
				.addDoubleClickListener(new IDoubleClickListener() {
					public void doubleClick(
							DoubleClickEvent paramAnonymousDoubleClickEvent) {
						TreeItem[] arrayOfTreeItem;
						if ((ProjectTeamTreeManager.this.projectTeamTreeViewer
								.getTree().getSelection() != null)
								&& (ProjectTeamTreeManager.this.projectTeamTreeViewer
										.getTree().getSelection().length > 1)) {
							arrayOfTreeItem = ProjectTeamTreeManager.this.projectTeamTreeViewer
									.getTree().getSelection();
							arrayOfTreeItem[0].setExpanded(true);
							ProjectTeamTreeManager.this.projectTeamTreeViewer
									.refresh();
							ProjectTeamTreeManager.this
									.setPrivilegeOnSelection(arrayOfTreeItem,
											-1, true);
							ProjectTeamTreeManager.this.projectTeamTreeViewer
									.refresh();
						} else if ((ProjectTeamTreeManager.this.projectTeamTreeViewer
								.getTree().getSelection() != null)
								&& (ProjectTeamTreeManager.this.projectTeamTreeViewer
										.getTree().getSelection().length == 1)) {
							arrayOfTreeItem = ProjectTeamTreeManager.this.projectTeamTreeViewer
									.getTree().getSelection();
							if ((arrayOfTreeItem[0].getData() instanceof ProjectTeamContentNode)) {
								ProjectTeamContentNode localProjectTeamContentNode = (ProjectTeamContentNode) arrayOfTreeItem[0]
										.getData();
								if (((localProjectTeamContentNode.getObject() instanceof IC_UserProxy))
										|| ((localProjectTeamContentNode
												.getObject() instanceof TCComponentUser))) {
									ProjectTeamTreeManager.this
											.setPrivilegeOnSelection(
													arrayOfTreeItem, -1, true);
									ProjectTeamTreeManager.this.projectTeamTreeViewer
											.refresh();
								}
							}
							ProjectTeamTreeManager.this.projectTeamTreeViewer
									.expandToLevel(arrayOfTreeItem[0], 5);
						}
					}
				});
		*/
		this.projectTeamTreeViewer
				.setLabelProvider(new ProjectTeamLabelProvider());
		this.projectTeamTreeViewer
				.setContentProvider(new ProjectTeamContentProvider(
						this.projectTeamTreeViewer));
		this.projectTeamFindFilter = new ProjectTeamFindFilter("*", -1);
		this.projectTeamTreeViewer.addFilter(this.projectTeamFindFilter);
		this.projectTeamTreeViewer
				.setInput(new ProjectTeamContentNode(null, 0));
		GridData localGridData = new GridData(4, 4, true, true);
		localGridData.heightHint = 0;
		localGridData.widthHint = 0;
		this.projectTeamTreeViewer.getTree().setLayoutData(localGridData);
		GridLayout localGridLayout2 = new GridLayout(1, false);
		localGridLayout2.marginHeight = 0;
		localGridLayout2.marginWidth = 0;
		this.projectTeamTreeViewer.getTree().setLayout(localGridLayout2);
		this.projectTeamTreeViewer.getTree().addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(
							SelectionEvent paramAnonymousSelectionEvent) {
						super.widgetSelected(paramAnonymousSelectionEvent);
						ProjectTeamTreeManager.this
								.setButtonStatus(
										false,
										!ProjectTeamTreeManager.this.m_projectDefinitionView
												.isTeamInherited());
					}
				});
		this.projectTeamTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(
							SelectionChangedEvent paramAnonymousSelectionChangedEvent) {
						if ((paramAnonymousSelectionChangedEvent.getSelection()
								.isEmpty())
								|| (ProjectTeamTreeManager.this.m_projectDefinitionView
										.isTeamInherited())) {
							ProjectTeamTreeManager.this.setButtonStatus(false,
									false);
							return;
						}
						ProjectTeamTreeManager.this
								.setButtonStatus(false, true);
					}
				});
		ProjectTeamNodeCache.projectManager = this;
	}

	private TCSession getTCSession() {
		try {
			return (TCSession) Activator.getDefault().getSessionService()
					.getSession(TCSession.class.getName(), false);
		} catch (Exception localException) {
			logger.error(localException.getLocalizedMessage(), localException);
		}
		return null;
	}

	private boolean isProjectAdmin() {
		return ProjectManager.getApp().isPAPrivileged();
	}

	private void constructOrganizationTreeSash(Composite paramComposite) {
		this.m_parentProject = null;
		GridLayout localGridLayout1 = new GridLayout(2, false);
		localGridLayout1.marginHeight = 0;
		localGridLayout1.marginWidth = 0;
		paramComposite.setLayout(localGridLayout1);
		createAvailableHeaderForOrganization(paramComposite);
		Composite localComposite = new Composite(paramComposite, 0);
		localComposite.setLayoutData(new GridData(5, 5));
		this.m_availableTreeViewer = new TreeViewer(paramComposite, 2818);
		this.m_availableTreeViewer
				.setContentProvider(new OrgNodeContentProvider(
						this.m_availableTreeViewer));
		this.m_availableTreeViewer.setLabelProvider(new OrgNodeLabelProvider());
		this.orgFiliter = new OrgTreeFindFilter("*", -1);
		this.orgTreeFilter = new OrgTreeFilter(getTCSession());
		this.m_availableTreeViewer.addFilter(this.orgFiliter);
		this.m_availableTreeViewer.addFilter(this.orgTreeFilter);
		TCComponentProject localTCComponentProject1 = getProject();
		TCComponentProject localTCComponentProject2 = null;
		if (localTCComponentProject1 != null)
			try {
				localTCComponentProject2 = (TCComponentProject) localTCComponentProject1
						.getReferenceProperty("fnd0Parent");
				if (localTCComponentProject2 != null) {
					this.prgFiliter = new ProgramTreeFilter(
							localTCComponentProject2, 3);
					this.m_availableTreeViewer.addFilter(this.prgFiliter);
				}
			} catch (TCException localTCException) {
				Logger.getLogger(ProjectTeamTreeManager.class).error(
						localTCException.getLocalizedMessage(),
						localTCException);
			}
		this.m_availableTreeViewer.setInput(OrgTreeContentNode
				.getRootNode(this));
		GridData localGridData = new GridData(4, 4, true, true);
		localGridData.heightHint = 0;
		localGridData.widthHint = 0;
		this.m_availableTreeViewer.getTree().setLayoutData(localGridData);
		GridLayout localGridLayout2 = new GridLayout(1, false);
		localGridLayout2.marginHeight = 0;
		localGridLayout2.marginWidth = 0;
		this.m_availableTreeViewer.getTree().setLayout(localGridLayout2);
		this.m_availableTreeViewer.getTree().addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(
							SelectionEvent paramAnonymousSelectionEvent) {
						super.widgetSelected(paramAnonymousSelectionEvent);
						if (!ProjectTeamTreeManager.this.m_projectDefinitionView
								.isTeamInherited()) {
							ProjectTeamTreeManager.this.addMemberButton
									.setEnabled(true);
							if ((ProjectTeamTreeManager.this.m_project != null)
									&& (ProjectTeamTreeManager.this.getRoot() != null)) {
								ProjectTeamStatus localProjectTeamStatus = (ProjectTeamStatus) ProjectTeamTreeManager.this
										.getRoot().getObject();
								TCComponentUser localTCComponentUser = ProjectTeamTreeManager.this.m_project
										.getSession().getUser();
								int i = 0;
								if ((localProjectTeamStatus
										.isAProjectTeamAdminMember(localTCComponentUser))
										|| (ProjectTeamTreeManager.this.m_projectDefinitionView
												.isPTA()))
									i = 1;
								ProjectTeamTreeManager.this
										.setButtonStatus(
												(i != 0)
														&& (!ProjectTeamTreeManager.this.m_projectDefinitionView
																.isTeamInherited()),
												false);
								return;
							}
							ProjectTeamTreeManager.this
									.setButtonStatus(
											!ProjectTeamTreeManager.this.m_projectDefinitionView
													.isTeamInherited(), false);
						}
					}
				});
		this.m_availableTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(
							SelectionChangedEvent paramAnonymousSelectionChangedEvent) {
						ProjectTeamTreeManager.this.projectTeamTreeViewer
								.getTree().deselectAll();
						if (paramAnonymousSelectionChangedEvent.getSelection()
								.isEmpty()) {
							ProjectTeamTreeManager.this.setButtonStatus(false,
									false);
							return;
						}
						if ((ProjectTeamTreeManager.this.m_project != null)
								&& (ProjectTeamTreeManager.this.getRoot() != null)) {
							ProjectTeamStatus localProjectTeamStatus = (ProjectTeamStatus) ProjectTeamTreeManager.this
									.getRoot().getObject();
							TCComponentUser localTCComponentUser = ProjectTeamTreeManager.this.m_project
									.getSession().getUser();
							int i = 0;
							if ((localProjectTeamStatus
									.isAProjectTeamAdminMember(localTCComponentUser))
									|| (ProjectTeamTreeManager.this.m_projectDefinitionView
											.isPTA()))
								i = 1;
							ProjectTeamTreeManager.this
									.setButtonStatus(
											(i != 0)
													&& (!ProjectTeamTreeManager.this.m_projectDefinitionView
															.isTeamInherited()),
											false);
							return;
						}
						ProjectTeamTreeManager.this
								.setButtonStatus(
										!ProjectTeamTreeManager.this.m_projectDefinitionView
												.isTeamInherited(), false);
					}
				});
		this.m_availableTreeViewer
				.addDoubleClickListener(new IDoubleClickListener() {
					public void doubleClick(
							DoubleClickEvent paramAnonymousDoubleClickEvent) {
						if ((ProjectTeamTreeManager.this.m_availableTreeViewer != null)
								&& (ProjectTeamTreeManager.this.m_availableTreeViewer
										.getTree().getSelection() != null)
								&& (ProjectTeamTreeManager.this.m_availableTreeViewer
										.getTree().getSelection().length > 0)
								&& (!ProjectTeamTreeManager.this.m_projectDefinitionView
										.isTeamInherited()))
							ProjectTeamTreeManager.this
									.addToSelectedMembersTree(ProjectTeamTreeManager.this.m_availableTreeViewer
											.getTree());
					}
				});
		addRemoveButtons(paramComposite);
	}

	private void createAvailableHeaderForOrganization(Composite paramComposite) {
		Composite localComposite = new Composite(paramComposite, 0);
		localComposite.setLayoutData(new GridData(4, 4, false, false));
		GridLayout localGridLayout = new GridLayout(3, false);
		localGridLayout.marginHeight = 0;
		localGridLayout.marginWidth = 0;
		localComposite.setLayout(localGridLayout);
		createSearchForOrganization(localComposite);
	}

	private void createAvailableHeader(Composite paramComposite) {
		Composite localComposite = new Composite(paramComposite, 0);
		localComposite.setLayoutData(new GridData(4, 4, false, false));
		GridLayout localGridLayout = new GridLayout(3, false);
		localGridLayout.marginHeight = 0;
		localGridLayout.marginWidth = 0;
		localComposite.setLayout(localGridLayout);
		createSearchCompForProjectTeam(localComposite);
	}

	private void createSearchForOrganization(Composite paramComposite) {
		Composite localComposite = new Composite(paramComposite, 0);
		localComposite.setLayoutData(SWTUIUtilities.defaultGridData(1, true,
				false));
		GridLayout localGridLayout = new GridLayout(6, false);
		localGridLayout.horizontalSpacing = 1;
		localGridLayout.marginHeight = 0;
		localGridLayout.marginWidth = 0;
		localComposite.setLayout(localGridLayout);
		createSearchTextField(localComposite, 0);
		Button localButton1 = createSearchCompButton(localComposite,
				ProjectConstants.USER_BUTTON_IMAGE,
				ProjectConstants.FIND_USER_BUTTON_TIP);
		localButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this._filterType = 3;
				ProjectTeamTreeManager.this.doSearchInOrgTree();
				ProjectTeamTreeManager.this.m_availableTreeViewer.expandAll();
			}
		});
		Button localButton2 = createSearchCompButton(localComposite,
				ProjectConstants.ROLE_BUTTON_IMAGE,
				ProjectConstants.FIND_ROLE_BUTTON_TIP);
		localButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this._filterType = 2;
				ProjectTeamTreeManager.this.doSearchInOrgTree();
				ProjectTeamTreeManager.this.m_availableTreeViewer.expandAll();
			}
		});
		Button localButton3 = createSearchCompButton(localComposite,
				ProjectConstants.GROUP_BUTTON_IMAGE,
				ProjectConstants.FIND_GROUP_BUTTON_TIP);
		localButton3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this._filterType = 0;
				ProjectTeamTreeManager.this.doSearchInOrgTree();
				ProjectTeamTreeManager.this.m_availableTreeViewer.expandAll();
			}
		});
		Button localButton4 = createSearchCompButton(localComposite,
				ProjectConstants.REFRESH_BUTTON_IMAGE,
				ProjectConstants.REFRESH_TEXT);
		localButton4.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				OrgTreeContentNode.IC_NodeCache.clearAndRefresh();
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						ProjectTeamTreeManager.this.doSearchInOrgTree();
						if (ProjectTeamTreeManager.this.orgFiliter
								.hasFilterCriteria())
							ProjectTeamTreeManager.this.m_availableTreeViewer
									.expandAll();
					}
				});
			}
		});
		Button localButton5 = createSearchCompButton(localComposite,
				ProjectConstants.CLEAR_BUTTON_IMAGE,
				ProjectConstants.CLEAR_BUTTON_TEXT);
		localButton5.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this.m_searchOrgText.setText("");
				ProjectTeamTreeManager.this.orgFiliter.setFilterString("");
				ProjectTeamTreeManager.this.orgFiliter.setFilterType(-1);
				ProjectTeamTreeManager.this.m_availableTreeViewer.refresh();
				ProjectTeamTreeManager.this.m_availableTreeViewer.collapseAll();
			}
		});
	}

	private void doSearchInOrgTree() {
		String str = this.m_searchOrgText.getText();
		if (!"".equals(str)) {
			this.orgFiliter.setFilterString(str);
			this.orgFiliter.setFilterType(this._filterType);
		}
		this.m_availableTreeViewer.refresh();
	}

	private void doSearchInProjectTeam() {
		String str = this.m_searchProjectTeamText.getText();
		this.projectTeamFindFilter.setFilterString(str);
		this.projectTeamFindFilter.setFilterType(this._filterTypeProjectTeam);
		this.projectTeamTreeViewer.refresh();
	}

	private void createSearchCompForProjectTeam(Composite paramComposite) {
		Composite localComposite = new Composite(paramComposite, 0);
		localComposite.setLayoutData(SWTUIUtilities.defaultGridData(1, true,
				false));
		GridLayout localGridLayout = new GridLayout(7, false);
		localGridLayout.horizontalSpacing = 1;
		localGridLayout.marginHeight = 0;
		localGridLayout.marginWidth = 0;
		localComposite.setLayout(localGridLayout);
		createSearchTextField(localComposite, 1);
		Button localButton1 = createSearchCompButton(localComposite,
				ProjectConstants.USER_BUTTON_IMAGE,
				ProjectConstants.FIND_USER_BUTTON_TIP);
		localButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this._filterTypeProjectTeam = 3;
				ProjectTeamTreeManager.this.doSearchInProjectTeam();
				ProjectTeamTreeManager.this.projectTeamTreeViewer.expandAll();
			}
		});
		Button localButton2 = createSearchCompButton(localComposite,
				ProjectConstants.ROLE_BUTTON_IMAGE,
				ProjectConstants.FIND_ROLE_BUTTON_TIP);
		localButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this._filterTypeProjectTeam = 2;
				ProjectTeamTreeManager.this.doSearchInProjectTeam();
				ProjectTeamTreeManager.this.projectTeamTreeViewer.expandAll();
			}
		});
		Button localButton3 = createSearchCompButton(localComposite,
				ProjectConstants.GROUP_BUTTON_IMAGE,
				ProjectConstants.FIND_GROUP_BUTTON_TIP);
		localButton3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this._filterTypeProjectTeam = 0;
				ProjectTeamTreeManager.this.doSearchInProjectTeam();
				ProjectTeamTreeManager.this.projectTeamTreeViewer.expandAll();
			}
		});
		Button localButton4 = createSearchCompButton(localComposite,
				ProjectConstants.REFRESH_BUTTON_IMAGE,
				ProjectConstants.REFRESH_TEXT);
		localButton4.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this.m_searchProjectTeamText.setText("");
				ProjectTeamTreeManager.this.projectTeamFindFilter
						.setFilterString("*");
				ProjectTeamTreeManager.this.projectTeamFindFilter
						.setFilterType(-1);
				ProjectTeamTreeManager.this.reloadProjectTeam();
				ProjectTeamTreeManager.this.projectTeamTreeViewer.collapseAll();
			}
		});
		Button localButton5 = createSearchCompButton(localComposite,
				ProjectConstants.CLEAR_BUTTON_IMAGE,
				ProjectConstants.CLEAR_BUTTON_TEXT);
		this.combo = new Combo(localComposite, 4);
		localButton5.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this.clearProjectTeamSearch(true);
			}
		});
		Registry localRegistry = Registry
				.getRegistry("com.teamcenter.rac.project.project");
		String[] arrayOfString1 = new String[3];
		arrayOfString1[0] = localRegistry.getString(
				"ProjectTeam.NonPrivileged", "Non-privileged");
		arrayOfString1[1] = localRegistry.getString("ProjectTeam.Privileged",
				"Privileged");
		arrayOfString1[2] = localRegistry.getString(
				"ProjectTeam.TeamAdministrator", "Team Administrator");
		String[] arrayOfString2 = arrayOfString1;
		if (arrayOfString2 != null)
			for (int i = 0; i < arrayOfString2.length; i++)
				this.combo.add(arrayOfString2[i]);
		this.combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this.m_searchProjectTeamText.setText("");
				String str = Integer.valueOf(
						ProjectTeamTreeManager.this.combo.getSelectionIndex())
						.toString();
				ProjectTeamTreeManager.this.projectTeamFindFilter
						.setFilterString(str);
				ProjectTeamTreeManager.this.projectTeamFindFilter
						.setFilterType(100);
				ProjectTeamTreeManager.this.projectTeamTreeViewer.refresh();
				ProjectTeamTreeManager.this.projectTeamTreeViewer.expandAll();
			}
		});
	}

	private void createSearchTextField(Composite paramComposite, int paramInt) {
		GridData localGridData = SWTUIUtilities.defaultGridData(1, true, false);
		localGridData.verticalAlignment = 16777216;
		Text localText = new Text(paramComposite, 2048);
		localText.setLayoutData(localGridData);
		localText.setTextLimit(256);
		localText.setToolTipText(ProjectConstants.FIND_TEXT_TIP);
		if (paramInt == 0)
			this.m_searchOrgText = localText;
		else
			this.m_searchProjectTeamText = localText;
	}

	private Button createSearchCompButton(Composite paramComposite,
			Image paramImage, String paramString) {
		Button localButton = new Button(paramComposite, 8);
		localButton.setImage(paramImage);
		localButton.setToolTipText(paramString);
		localButton.setLayoutData(SWTUIUtilities.defaultGridData(1, false,
				false));
		return localButton;
	}

	private void addRemoveButtons(Composite paramComposite) {
		Composite localComposite = new Composite(paramComposite, 0);
		localComposite.setLayoutData(SWTUIUtilities.defaultGridData(1, false,
				true));
		localComposite.setLayout(new GridLayout(1, false));
		this.addMemberButton = new Button(localComposite, 8);
		this.addMemberButton.setImage(ProjectConstants.ADD_TEAM_BUTTON_IMAGE);
		GridData localGridData = SWTUIUtilities.defaultGridData(1, true, true);
		localGridData.verticalAlignment = 1024;
		localGridData.heightHint = 20;
		localGridData.widthHint = 10;
		this.addMemberButton.setLayoutData(localGridData);
		String str = ProjectConstants.projectReg
				.getString("addMemberButton.TIP");
		this.addMemberButton.setToolTipText(str);
		this.addMemberButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this
						.addToSelectedMembersTree(ProjectTeamTreeManager.this.m_availableTreeViewer
								.getTree());
			}
		});
		this.addMemberButton.setEnabled(false);
		this.removeMemberButton = new Button(localComposite, 8);
		this.removeMemberButton
				.setImage(ProjectConstants.REMOVE_TEAM_BUTTON_IMAGE);
		localGridData = SWTUIUtilities.defaultGridData(1, true, true);
		localGridData.verticalAlignment = 128;
		localGridData.heightHint = 20;
		localGridData.widthHint = 30;
		this.removeMemberButton.setLayoutData(localGridData);
		str = ProjectConstants.projectReg.getString("removeMemberButton.TIP");
		this.removeMemberButton.setToolTipText(str);
		this.removeMemberButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(
					SelectionEvent paramAnonymousSelectionEvent) {
				ProjectTeamTreeManager.this
						.removeFromSelectedMembers(ProjectTeamTreeManager.this.projectTeamTreeViewer
								.getTree());
			}
		});
		this.removeMemberButton.setEnabled(false);
	}

	public void setRoot(ProjectTeamContentNode paramProjectTeamContentNode) {
		if (getRoot() == null)
			this.root = paramProjectTeamContentNode;
	}

	public ProjectTeamContentNode getRoot() {
		return this.root;
	}
	
	public Vector<ProjectTeamContentNode> getTeamAdmins(int paramInt,ProjectTeamStatus paramProjectTeamStatus){
		TreeSet<ProjectTeamContentNode> childNodes = root.getChildNodes();
		System.out.println("右边所有节点 childNodes.size()-->"+childNodes.size());
		Vector<ProjectTeamContentNode> teamadmins = new Vector<ProjectTeamContentNode>();
		Iterator iter = childNodes.iterator();
		while( iter.hasNext()) {
			System.out.println("next11");
		 	 ProjectTeamContentNode paramProjectTeamContentNode1=(ProjectTeamContentNode) iter.next();
			 if (paramProjectTeamContentNode1.getObject() != null){
//					 System.out.println("get object done");
				 TreeSet<ProjectTeamContentNode> childNodes2 = paramProjectTeamContentNode1.getChildNodes();
				 Iterator iter2 = childNodes2.iterator();
				 while(iter2.hasNext()){
					 System.out.println("next22");
					 ProjectTeamContentNode paramProjectTeamContentNode2=(ProjectTeamContentNode) iter2.next();
					 if (paramProjectTeamContentNode2.getObject() != null){
						 System.out.println("get object done");
						 if(paramProjectTeamContentNode2 instanceof ProjectTeamUserNode){
							 System.out.println("is ProjectTeamUserNode");
							 ProjectTeamUserNode paramProjectTeamUserNode= (ProjectTeamUserNode) paramProjectTeamContentNode2;
							 int privilege = paramProjectTeamUserNode.getPrivilege() ;
							 System.out.println("privilege is "+privilege);
							 if(privilege==ProjectConstants.TEAMADMIN){
								 teamadmins.add(paramProjectTeamContentNode2);
								 System.out.println("Get team adminsitarotor-->"+paramProjectTeamUserNode.getObject());
							 }
						 }
					 }
				 }
				 }else{
					
				 }
			 }
		return teamadmins;
					///if (paramProjectTeamContentNode1.getObject() != null)
					//	setNodeStatus((ProjectTeamUserNode) paramProjectTeamContentNode1,paramInt, paramProjectTeamStatus);
	}
	
	//add by wuw
	public void dorefreshTeamAdmin(TCComponentUser user,ProjectTeamStatus paramProjectTeamStatus){
		TreeSet<ProjectTeamContentNode> childNodes = root.getChildNodes();
		System.out.println("右边所有节点 childNodes.size()-->"+childNodes.size());
		//Vector<ProjectTeamContentNode> teamadmins = new Vector<ProjectTeamContentNode>();
		Iterator iter = childNodes.iterator();
		while( iter.hasNext()) {
			System.out.println("next11");
		 	 ProjectTeamContentNode paramProjectTeamContentNode1=(ProjectTeamContentNode) iter.next();
			 if (paramProjectTeamContentNode1.getObject() != null){
//					 System.out.println("get object done");
				 TreeSet<ProjectTeamContentNode> childNodes2 = paramProjectTeamContentNode1.getChildNodes();
				 Iterator iter2 = childNodes2.iterator();
				 while(iter2.hasNext()){
					 System.out.println("next22");
					 ProjectTeamContentNode paramProjectTeamContentNode2=(ProjectTeamContentNode) iter2.next();
					 if (paramProjectTeamContentNode2.getObject() != null){
						 System.out.println("get object done");
						 if(paramProjectTeamContentNode2 instanceof ProjectTeamUserNode){
							 System.out.println("is ProjectTeamUserNode");
							 ProjectTeamUserNode paramProjectTeamUserNode= (ProjectTeamUserNode) paramProjectTeamContentNode2;
							 
							 System.out.println("Get team adminsitarotor-->"+paramProjectTeamUserNode.getObject());
							 if(paramProjectTeamUserNode.getObject() instanceof IC_UserProxy){
								 System.out.println("Get IC_UserProxy getPrivilege-->"+paramProjectTeamUserNode.getPrivilege());
								// IC_UserProxy userProxy= (IC_UserProxy) paramProjectTeamUserNode.getObject();
//								if( userProxy.getUser()==user){
//									System.out.println("userProxy.getUser()==user");
//									setNodeStatus(paramProjectTeamUserNode,ProjectConstants.TEAMADMIN,paramProjectTeamStatus);
//								}
								if(paramProjectTeamUserNode.getPrivilege()==ProjectConstants.UNPRIVILEGED){
									setNodeStatus(paramProjectTeamUserNode,ProjectConstants.PRIVILEGED,paramProjectTeamStatus);
								}
							 }
						 }
					 }
				 }
				 }else{
					
				 }
			 }
		return ;
					///if (paramProjectTeamContentNode1.getObject() != null)
					//	setNodeStatus((ProjectTeamUserNode) paramProjectTeamContentNode1,paramInt, paramProjectTeamStatus);
	}
	
	

	//设置权限给节点
	private synchronized void setPrivilegeOnSelection(
			TreeItem[] paramArrayOfTreeItem, int paramInt, boolean paramBoolean) {
		if (this.m_projectDefinitionView.isTeamInherited())
			return;
		ProjectTeamStatus localProjectTeamStatus = null;
		if (paramArrayOfTreeItem == null)
			return;
		if ((this.root.getObject() != null)
				&& ((this.root.getObject() instanceof ProjectTeamStatus))) {
			localProjectTeamStatus = (ProjectTeamStatus) this.root.getObject();
		} else {
			localProjectTeamStatus = new ProjectTeamStatus(this.m_project);
			this.root.setObject(localProjectTeamStatus);
		}
		
		System.out.println("===========hhhh==========");
		//wuwei
		Vector<ProjectTeamContentNode> teamAdmins = null;
		if(paramInt == ProjectConstants.TEAMADMIN)
		{
			teamAdmins =  getTeamAdmins(ProjectConstants.PRIVILEGED,localProjectTeamStatus);
			System.out.println("teamAdmins.size()-->"+teamAdmins.size());
		}
		
		for (int i = 0; i < paramArrayOfTreeItem.length; i++) {
			Object localObject = paramArrayOfTreeItem[i].getData();
			ProjectTeamContentNode localProjectTeamContentNode = null;
			if ((localObject instanceof ProjectTeamContentNode))
				localProjectTeamContentNode = (ProjectTeamContentNode) localObject;
			setPrivilegeOnSelectionHelper(
					(ProjectTeamContentNode) paramArrayOfTreeItem[i].getData(),
					paramInt, localProjectTeamStatus,
					localProjectTeamContentNode, paramBoolean);
			this.isTeamModifed = true;
		}
		
		if(teamAdmins!= null && teamAdmins.size() > 0){
			for (int i = 0; i < teamAdmins.size(); i++) {
				setPrivilegeOnSelectionHelper(
						(ProjectTeamContentNode) teamAdmins.get(i),
						ProjectConstants.PRIVILEGED, localProjectTeamStatus,
						(ProjectTeamContentNode) teamAdmins.get(i), paramBoolean);
				this.isTeamModifed = true;
			}
		}
		
		this.projectTeamTreeViewer.refresh();
		if (this.isTeamModifed)
			this.m_projectDefinitionView.validateButtons();
	}

	private synchronized void setPrivilegeOnSelectionHelper(
			ProjectTeamContentNode paramProjectTeamContentNode1, int paramInt,
			ProjectTeamStatus paramProjectTeamStatus,
			ProjectTeamContentNode paramProjectTeamContentNode2,
			boolean paramBoolean) {
		if (paramProjectTeamContentNode1 != null)
			if ((paramProjectTeamContentNode1.getObject() instanceof IC_UserProxy)) {
				System.out.println("setPrivilegeOnSelectionHelper User");
				if (paramProjectTeamContentNode1.getObject() != null)
					setNodeStatus((ProjectTeamUserNode) paramProjectTeamContentNode1,paramInt, paramProjectTeamStatus);
				((ProjectTeamUserNode) paramProjectTeamContentNode1).setPrivilege(paramInt);
			} else {
				Object localObject1;

				if ((paramProjectTeamContentNode1.getObject() instanceof IC_GroupRoleHashable)) {
					System.out.println("setPrivilegeOnSelectionHelper GroupRole");
					localObject1 = (IC_GroupRoleHashable) paramProjectTeamContentNode1
							.getObject();
					try {
						int i = paramProjectTeamContentNode1.getChildNodes() != null ? 1
								: 0;

						if (i == 0) {
							TCComponentRole localObject2 = ((IC_GroupRoleHashable) localObject1)
									.getRole();
							TCComponentGroup localObject3 = ((IC_GroupRoleHashable) localObject1)
									.getGroup();
							TCComponentGroupMember[] localObject4 = ProjectManager
									.getInstance().getGroupMembers(
											(TCComponentGroup) localObject3,
											(TCComponentRole) localObject2);
							if ((localObject4 != null)
									&& (localObject4.length > 0))
								for (TCComponentGroupMember localTCComponentGroupMember : localObject4) {
									ProjectTeamUserNode localProjectTeamUserNode = ProjectTeamNodeCache
											.createOrFindNode(new IC_UserProxy(
													localTCComponentGroupMember));
									int n = setNodeStatus(
											localProjectTeamUserNode, paramInt,
											paramProjectTeamStatus);
									localProjectTeamUserNode.setPrivilege(n);
								}
						} else {
							Iterator localObject3 = paramProjectTeamContentNode1
									.getChildNodes().iterator();
							while (((Iterator) localObject3).hasNext()) {
								ProjectTeamContentNode localObject2 = (ProjectTeamContentNode) ((Iterator) localObject3)
										.next();
								ProjectTeamUserNode localObject4 = (ProjectTeamUserNode) localObject2;
								int j = setNodeStatus(
										(ProjectTeamUserNode) localObject4,
										paramInt, paramProjectTeamStatus);
								((ProjectTeamUserNode) localObject4)
										.setPrivilege(j);
							}
						}
					} catch (TCException localTCException) {
						logger.error(localTCException.getClass().getName(),
								localTCException);
					}
				}
				if ((paramProjectTeamContentNode1.getObject() instanceof TCComponentGroup)) {
					localObject1 = (TCComponentGroup) paramProjectTeamContentNode1
							.getObject();
					processchildrenNotExpanded((TCComponentGroup) localObject1,
							paramInt, paramProjectTeamStatus, paramBoolean);
				}
				if (paramProjectTeamContentNode1.getChildNodes() != null) {
					localObject1 = paramProjectTeamContentNode1.getChildNodes();
					Iterator localObject2 = ((TreeSet) localObject1).iterator();
					while (((Iterator) localObject2).hasNext()) {
						ProjectTeamContentNode localProjectTeamContentNode = (ProjectTeamContentNode) ((Iterator) localObject2)
								.next();
						setPrivilegeOnSelectionHelper(
								localProjectTeamContentNode, paramInt,
								paramProjectTeamStatus,
								paramProjectTeamContentNode2, paramBoolean);
					}
				}
			}
	}

	private int setNodeStatus(ProjectTeamUserNode paramProjectTeamUserNode,
			int paramInt, ProjectTeamStatus paramProjectTeamStatus) {
		int i = paramInt;
		
		System.out.println("lala setNodeStatus--->"+i);
		if (paramInt == -1)
			if ((paramProjectTeamUserNode.getPrivilege() == ProjectConstants.PRIVILEGED)
					|| (paramProjectTeamUserNode.getPrivilege() == ProjectConstants.TEAMADMIN))
				i = ProjectConstants.PRIVILEGED;//UNPRIVILEGED
			else
				i = ProjectConstants.PRIVILEGED;
		paramProjectTeamUserNode.setPrivilege(i);
		if (i == ProjectConstants.PRIVILEGED)
			paramProjectTeamStatus
					.setUserToPrivilegedUser((IC_UserProxy) paramProjectTeamUserNode
							.getObject());
		if (i == ProjectConstants.TEAMADMIN)
			paramProjectTeamStatus
					.setUserToPTA((IC_UserProxy) paramProjectTeamUserNode
							.getObject());
		if (i == ProjectConstants.UNPRIVILEGED){
			
			System.out.println("UNPRIVILEGED  paramProjectTeamUserNode-->"+paramProjectTeamUserNode);
			paramProjectTeamStatus
					.setUserToRegularMember((IC_UserProxy) paramProjectTeamUserNode
							.getObject());
		}
		return i;
	}

	private void processchildrenNotExpanded(
			TCComponentGroup paramTCComponentGroup, int paramInt,
			ProjectTeamStatus paramProjectTeamStatus, boolean paramBoolean) {
		try {
			TCComponent[] arrayOfTCComponent1 = paramTCComponentGroup
					.getRolesAndGroups();
			for (TCComponent localTCComponent : arrayOfTCComponent1) {
				if ((localTCComponent instanceof TCComponentGroup))
					processchildrenNotExpanded(
							(TCComponentGroup) localTCComponent, paramInt,
							paramProjectTeamStatus, paramBoolean);
				if ((localTCComponent instanceof TCComponentRole)) {
					TCComponentRole localTCComponentRole = (TCComponentRole) localTCComponent;
					TCComponentGroup[] arrayOfTCComponentGroup1 = localTCComponentRole
							.getAllGroups();
					for (TCComponentGroup localTCComponentGroup : arrayOfTCComponentGroup1)
						if (localTCComponentGroup.toString().equals(
								paramTCComponentGroup.toString())) {
							TCComponentGroupMember[] arrayOfTCComponentGroupMember1 = ProjectManager
									.getInstance().getGroupMembers(
											localTCComponentGroup,
											localTCComponentRole);
							if ((arrayOfTCComponentGroupMember1 != null)
									&& (arrayOfTCComponentGroupMember1.length > 0))
								for (TCComponentGroupMember localTCComponentGroupMember : arrayOfTCComponentGroupMember1) {
									IC_UserProxy localIC_UserProxy = new IC_UserProxy(
											localTCComponentGroupMember);
									ProjectTeamUserNode localProjectTeamUserNode = ProjectTeamNodeCache
											.createOrFindNode(localIC_UserProxy);
									localProjectTeamUserNode
											.setPrivilege(paramInt);
									if (paramInt == ProjectConstants.PRIVILEGED)
										paramProjectTeamStatus
												.setUserToPrivilegedUser(localIC_UserProxy);
									if (paramInt == ProjectConstants.TEAMADMIN)
										paramProjectTeamStatus
												.setUserToPTA(localIC_UserProxy);
									if (paramInt == ProjectConstants.UNPRIVILEGED)
										paramProjectTeamStatus
												.setUserToRegularMember(localIC_UserProxy);
								}
						}
				}
			}
		} catch (TCException localTCException) {
			logger.error(localTCException.getClass().getName(),
					localTCException);
		}
	}

	public void clearSelectedMembers() {
		this.m_project = null;
		this.projectTeamTreeViewer.getTree().removeAll();
		this.m_searchProjectTeamText.setText("");
		this.combo.deselectAll();
		this.projectTeamFindFilter.setFilterString("*");
		this.projectTeamFindFilter.setFilterType(-1);
		this.isTeamModifed = false;
		this.root = null;
		this.projectTeamMembers_preModify = null;
		ProjectTeamNodeCache.clearCache();
		this.projectTeamTreeViewer.refresh();
		ProjectTeamNodeCache.setRootNode(this.root);
		this.projectTeamTreeViewer.setInput(null);
		setButtonStatus(false, false);
	}

	public TreeViewer getProjectTeamMembersViewer() {
		return this.projectTeamTreeViewer;
	}

	public void setButtonStatus(boolean paramBoolean1, boolean paramBoolean2) {
		TreeItem[] arrayOfTreeItem = this.projectTeamTreeViewer.getTree()
				.getSelection();
		Object localObject1;
		Object localObject2;
		if ((arrayOfTreeItem != null)
				&& (arrayOfTreeItem.length > 0)
				&& ((arrayOfTreeItem[0].getData() instanceof ProjectTeamContentNode))) {
			localObject1 = (ProjectTeamContentNode) arrayOfTreeItem[0]
					.getData();
			if (((((ProjectTeamContentNode) localObject1).getObject() instanceof IC_UserProxy))
					|| ((((ProjectTeamContentNode) localObject1).getObject() instanceof TCComponentUser)))
				for (localObject2 = ((ProjectTeamContentNode) localObject1)
						.getParent(); ((ProjectTeamContentNode) localObject2)
						.getParent() != null; localObject2 = ((ProjectTeamContentNode) localObject2)
						.getParent())
					if ((((ProjectTeamContentNode) localObject2).getObject() instanceof TCComponentGroup)) {
						paramBoolean2 = false;
						break;
					}
		}
		if ((paramBoolean2) && (this.m_project != null) && (getRoot() != null)) {
			localObject1 = (ProjectTeamStatus) getRoot().getObject();
			localObject2 = this.m_project.getSession().getUser();
			int i = 0;
			if ((((ProjectTeamStatus) localObject1)
					.isAProjectTeamAdminMember((TCComponentUser) localObject2))
					|| (this.m_projectDefinitionView.isPTA()))
				i = 1;
			if (i == 0)
				paramBoolean2 = false;
		}
		this.addMemberButton.setEnabled(paramBoolean1);
		this.removeMemberButton.setEnabled(paramBoolean2);
	}

	private synchronized void removeFromSelectedMembers(Tree paramTree) {
		logger.info("removeFromSelectedMembers started");

		Tree tree = paramTree;
		boolean flag = true;
		boolean flag1 = true;
		Object aobj[] = projectTeamTreeViewer.getExpandedElements();
		org.eclipse.jface.viewers.TreePath atreepath[] = projectTeamTreeViewer
				.getExpandedTreePaths();
		m_projectDefinitionView.validateButtons();
		isTeamModifed = true;
		boolean flag2 = m_projectDefinitionView.getProgramSecurity();
		TreeItem atreeitem[] = tree.getSelection();
		Object aobj1[] = { TCComponentProject.getDisplayName(getTCSession()) };
		if (flag2 && atreeitem.length > 0)
			try {
				if (m_project != null
						&& m_project.getTCProperty("fnd0Children")
								.getModelObjectArrayValue().length > 0) {
					boolean flag3 = MessageDialog
							.openQuestion(
									getShell(),
									ProjectConstants.projectReg.getString(
											"removeUser.TITLE", "Remove User"),
									MessageFormat.format(
											ProjectConstants.projectReg
													.getString("confirmRemoveInheritedMember.MESSAGE"),
											aobj1));
					if (!flag3)
						return;
				}
			} catch (TCException tcexception) {
				logger.error(tcexception.getClass().getName(), tcexception);
			}
		TreeItem atreeitem1[];
		int j = (atreeitem1 = atreeitem).length;
		for (int i = 0; i < j; i++) {
			TreeItem treeitem = atreeitem1[i];
			ProjectTeamContentNode projectteamcontentnode = (ProjectTeamContentNode) treeitem
					.getData();
			((ProjectTeamContentNode) treeitem.getData()).getParent();
			if (flag2) {
				if (projectteamcontentnode.getObject() instanceof IC_UserProxy) {
					IC_UserProxy ic_userproxy = (IC_UserProxy) projectteamcontentnode
							.getObject();
					TCComponentUser tccomponentuser = ic_userproxy.getUser();
					if (tccomponentuser != null) {
						boolean flag5 = checkUserData(tccomponentuser);
						if (!flag5) {
							boolean flag7 = MessageDialog
									.openQuestion(
											getShell(),
											ProjectConstants.projectReg
													.getString(
															"removeUser.TITLE",
															"Remove User"),
											MessageFormat.format(
													ProjectConstants.projectReg
															.getString("confirmRemoveMember.MESSAGE"),
													aobj1));
							if (!flag7) {
								flag = flag7;
								return;
							}
						}
						if (flag)
							removeNode(projectteamcontentnode);
					}
				} else if (projectteamcontentnode.getObject() instanceof GroupRole) {
					for (Iterator iterator = projectteamcontentnode
							.getChildNodes().iterator(); iterator.hasNext();) {
						ProjectTeamContentNode projectteamcontentnode1 = (ProjectTeamContentNode) iterator
								.next();
						IC_UserProxy ic_userproxy1 = (IC_UserProxy) projectteamcontentnode1
								.getObject();
						TCComponentUser tccomponentuser1 = ic_userproxy1
								.getUser();
						if (tccomponentuser1 != null) {
							boolean flag8 = checkUserData(tccomponentuser1);
							if (!flag8) {
								boolean flag9 = MessageDialog
										.openQuestion(
												getShell(),
												ProjectConstants.projectReg
														.getString(
																"removeUser.TITLE",
																"Remove User"),
												MessageFormat
														.format(ProjectConstants.projectReg
																.getString("confirmRemoveMember.MESSAGE"),
																aobj1));
								if (flag9)
									flag = true;
							}
							if (flag)
								removeNode(projectteamcontentnode);
						}
					}

				} else if (projectteamcontentnode.getObject() instanceof TCComponentGroup) {
					TCComponentGroup tccomponentgroup = (TCComponentGroup) projectteamcontentnode
							.getObject();
					boolean flag4 = true;
					try {
						flag4 = checkGroupData(tccomponentgroup.getFullName());
					} catch (TCException tcexception1) {
						logger.error(tcexception1.getClass().getName(),
								tcexception1);
					}
					if (!flag4) {
						boolean flag6 = MessageDialog
								.openQuestion(
										getShell(),
										ProjectConstants.projectReg.getString(
												"removeGroup.TITLE",
												"Remove Group"),
										MessageFormat.format(
												ProjectConstants.projectReg
														.getString("confirmRemoveMember.MESSAGE"),
												aobj1));
						if (flag6)
							flag1 = true;
					}
					if (flag1)
						removeNode(projectteamcontentnode);
				}
			} else {
				removeNode(projectteamcontentnode);
			}
		}

		ProjectTeamNodeCache.refreshViewers(null, true);
		projectTeamTreeViewer.setInput(root);
		projectTeamTreeViewer.refresh();
		projectTeamTreeViewer.setExpandedElements(aobj);
		projectTeamTreeViewer.setExpandedTreePaths(atreepath);
		m_projectDefinitionView.validateButtons();
		logger.info("removeFromSelectedMembers ends");
	}

	private void removeNode(ProjectTeamContentNode paramProjectTeamContentNode) {
		ProjectTeamContentNode localProjectTeamContentNode = paramProjectTeamContentNode
				.getParent();
		if (localProjectTeamContentNode.getChildNodes() != null) {
			paramProjectTeamContentNode.removeFromParent();
			removeNodeHelper(paramProjectTeamContentNode);
			if (localProjectTeamContentNode.getChildNodes().isEmpty())
				this.root.getChildNodes().remove(localProjectTeamContentNode);
		}
	}

	private void removeNodeHelper(
			ProjectTeamContentNode paramProjectTeamContentNode) {
		ProjectTeamContentNode localProjectTeamContentNode1 = getRoot();
		if ((localProjectTeamContentNode1 != null)
				&& (localProjectTeamContentNode1.getObject() != null)
				&& ((localProjectTeamContentNode1.getObject() instanceof ProjectTeamStatus))) {
			ProjectTeamStatus localProjectTeamStatus = (ProjectTeamStatus) localProjectTeamContentNode1
					.getObject();
			if (localProjectTeamStatus != null) {
				Object localObject1 = paramProjectTeamContentNode.getObject();
				if ((localObject1 instanceof IC_UserProxy)) {
					localProjectTeamStatus
							.removeMembership((IC_UserProxy) localObject1);
				} else if ((localObject1 instanceof TCComponentGroup)) {
					localProjectTeamStatus
							.removeMember((TCComponentGroup) localObject1);
				} else if ((localObject1 instanceof GroupRole)) {
					Iterator localIterator = paramProjectTeamContentNode
							.getChildNodes().iterator();
					while (localIterator.hasNext()) {
						ProjectTeamContentNode localProjectTeamContentNode2 = (ProjectTeamContentNode) localIterator
								.next();
						Object localObject2 = localProjectTeamContentNode2
								.getObject();
						if ((localObject2 instanceof IC_UserProxy))
							localProjectTeamStatus
									.removeMembership((IC_UserProxy) localObject2);
					}
				}
			}
		}
	}

	public void addAsAdmin(TCComponentGroup paramTCComponentGroup,
			TCComponentRole paramTCComponentRole,
			TCComponentUser paramTCComponentUser) {
		TCComponentGroupMember localTCComponentGroupMember = ProjectManager
				.getInstance().getGroupMember(paramTCComponentGroup,
						paramTCComponentRole, paramTCComponentUser);
		if ((paramTCComponentGroup != null) && (paramTCComponentRole != null)
				&& (paramTCComponentUser != null)) {
			IC_GroupRoleHashable localIC_GroupRoleHashable = new IC_GroupRoleHashable(
					paramTCComponentGroup, paramTCComponentRole);
			Object localObject = null;
			ProjectTeamContentNode localProjectTeamContentNode = null;
			if (this.root != null)
				localProjectTeamContentNode = this.root
						.findOrCreateRoleNode(localIC_GroupRoleHashable);
			if (localProjectTeamContentNode != null)
				localObject = (ProjectTeamContentNode) localProjectTeamContentNode;
			else
				localObject = ProjectTeamNodeCache
						.createOrFindNode(localIC_GroupRoleHashable);
			ProjectTeamUserNode localProjectTeamUserNode = null;
			IC_UserProxy localIC_UserProxy = null;
			try {
				localIC_UserProxy = new IC_UserProxy(
						localTCComponentGroupMember);
				localProjectTeamUserNode = ProjectTeamNodeCache
						.createOrFindNode(localIC_UserProxy);
			} catch (TCException localTCException) {
				logger.error(localTCException.getClass().getName(),
						localTCException);
			}
			ProjectTeamStatus localProjectTeamStatus;
			if (localProjectTeamUserNode != null) {
				localProjectTeamUserNode
						.setPrivilege(ProjectConstants.TEAMADMIN);
				((ProjectTeamContentNode) localObject)
						.addChild(localProjectTeamUserNode);
				((ProjectTeamContentNode) localObject).setLoaded(true);
				if (this.root == null) {
					localProjectTeamStatus = new ProjectTeamStatus(
							this.m_project);
					this.root = new ProjectTeamContentNode(
							localProjectTeamStatus, 0);
				}
				this.root.addChild((ProjectTeamContentNode) localObject);
			}
			if ((this.root != null) && (this.root.getObject() != null)
					&& ((this.root.getObject() instanceof ProjectTeamStatus))) {
				localProjectTeamStatus = (ProjectTeamStatus) this.root
						.getObject();
				if (localProjectTeamStatus != null) {
					localProjectTeamStatus
							.addToProjectMembers(localTCComponentGroupMember);
					localProjectTeamStatus
							.addProjectTeamAdmin(localIC_UserProxy);
				}
			} else {
				localProjectTeamStatus = new ProjectTeamStatus(this.m_project);
				localProjectTeamStatus
						.addToProjectMembers(localTCComponentGroupMember);
				localProjectTeamStatus.addProjectTeamAdmin(localIC_UserProxy);
			}
		}
		this.projectTeamTreeViewer.setInput(this.root);
		this.projectTeamTreeViewer.refresh();
	}

	private void addToSelectedMembersTree(Tree paramTree) {
		Tree tree = paramTree;
		Object aobj[] = projectTeamTreeViewer.getExpandedElements();
		Object obj = null;
		selection = null;
		TreeItem atreeitem[] = tree.getSelection();
		if (root == null) {
			ProjectTeamStatus projectteamstatus = new ProjectTeamStatus(
					m_project);
			root = new ProjectTeamContentNode(projectteamstatus, 0);
		}
		int i = atreeitem != null ? atreeitem.length : 0;
		if (i == 0)
			return;
		int j = 0;
		TreeItem atreeitem1[];
		int l = (atreeitem1 = atreeitem).length;
		for (int k = 0; k < l; k++) {
			TreeItem treeitem = atreeitem1[k];
			j++;
			if (treeitem.getData() instanceof OrgTreeContentNode) {
				OrgTreeContentNode orgtreecontentnode = (OrgTreeContentNode) treeitem
						.getData();
				Object obj1 = orgtreecontentnode.getObject();
				if (obj1 instanceof IC_UserProxy) {
					OrgTreeContentNode orgtreecontentnode1 = (OrgTreeContentNode) ((OrgTreeContentNode) treeitem
							.getData()).getParent();
					ProjectTeamContentNode projectteamcontentnode = root
							.findOrCreateRoleNode(orgtreecontentnode1
									.getObject());
					ProjectTeamUserNode projectteamusernode = new ProjectTeamUserNode(
							(IC_UserProxy) obj1, 0);
					projectteamcontentnode.addChild(projectteamusernode);
					projectteamcontentnode.setLoaded(true);
					root.addChild(projectteamcontentnode);
					ProjectTeamStatus projectteamstatus2 = (ProjectTeamStatus) root
							.getObject();
					TCComponentGroupMember tccomponentgroupmember = ((IC_UserProxy) obj1)
							.getGroupMember();
					String s = m_project != null ? m_project.toDisplayString()
							: "";
					logger.info((new StringBuilder("Adding group member "))
							.append(tccomponentgroupmember.toDisplayString())
							.append(" to the project ").append(s).toString());
					projectteamstatus2
							.addToProjectMembers(tccomponentgroupmember);
					if (i == j)
						if (aobj != null && aobj.length > 0) {
							Object aobj2[] = new Object[aobj.length + 1];
							int j1 = 0;
							Object aobj4[];
							int j2 = (aobj4 = aobj).length;
							for (int i2 = 0; i2 < j2; i2++) {
								Object obj5 = aobj4[i2];
								aobj2[j1] = obj5;
								j1++;
							}

							aobj2[j1] = projectteamcontentnode;
							aobj = aobj2;
							obj = projectteamusernode;
						} else {
							aobj = (new Object[] { projectteamcontentnode });
							obj = projectteamcontentnode;
						}
				} else {
					boolean flag = true;
					if (m_parentProject != null) {
						List list = m_parentProject.getProjectGroups();
						if (obj1 instanceof IC_GroupRoleHashable) {
							TCComponentGroup tccomponentgroup = ((IC_GroupRoleHashable) obj1)
									.getGroup();
							if (list.contains(tccomponentgroup)) {
								flag = true;
							} else {
								flag = false;
								MessageBox.post(Messages
										.getString("RoleSelected"), Messages
										.getString("programNotSelected.TITLE"),
										1);
							}
						}
						if (obj1 instanceof TCComponentGroup)
							if (list.contains(obj1)) {
								flag = true;
							} else {
								flag = false;
								MessageBox.post(Messages
										.getString("GroupSelected"), Messages
										.getString("programNotSelected.TITLE"),
										1);
							}
					}
					if (flag) {
						ProjectTeamContentNode projectteamcontentnode1 = root
								.findOrCreateRoleNode(obj1);
						projectteamcontentnode1.setLoaded(false);
						projectteamcontentnode1.getChildrenInUI(
								projectTeamTreeViewer, true);
						projectteamcontentnode1.setLoaded(false);
						root.addChild(projectteamcontentnode1);
						ProjectTeamStatus projectteamstatus1 = (ProjectTeamStatus) root
								.getObject();
						Object obj2 = projectteamcontentnode1.getObject();
						if (obj2 instanceof TCComponent)
							projectteamstatus1
									.addToProjectMembers((TCComponent) obj2);
						else if (obj2 instanceof IC_GroupRoleHashable) {
							for (Iterator iterator = projectteamcontentnode1
									.getChildNodes().iterator(); iterator
									.hasNext();) {
								ProjectTeamContentNode projectteamcontentnode2 = (ProjectTeamContentNode) iterator
										.next();
								Object obj3 = projectteamcontentnode2
										.getObject();
								if (obj3 instanceof TCComponent) {
									logger.info((new StringBuilder(
											"Adding group member "))
											.append(obj3.toString())
											.append(" to the project ")
											.append(m_project != null ? m_project
													.toDisplayString()
													: "CONSTANT_NEW_PROJECT")
											.toString());
									projectteamstatus1
											.addToProjectMembers((TCComponent) obj3);
								} else if (obj3 instanceof IC_UserProxy) {
									TCComponentGroupMember tccomponentgroupmember1 = ((IC_UserProxy) obj3)
											.getGroupMember();
									logger.info((new StringBuilder(
											"Adding group member "))
											.append(tccomponentgroupmember1
													.toDisplayString())
											.append(" to the project ")
											.append(m_project != null ? m_project
													.toDisplayString()
													: "CONSTANT_NEW_PROJECT")
											.toString());
									projectteamstatus1
											.addToProjectMembers(tccomponentgroupmember1);
								}
							}

						}
						if (i == j)
							if (aobj != null && aobj.length > 0) {
								Object aobj1[] = new Object[aobj.length + 1];
								int i1 = 0;
								Object aobj3[];
								int l1 = (aobj3 = aobj).length;
								for (int k1 = 0; k1 < l1; k1++) {
									Object obj4 = aobj3[k1];
									aobj1[i1] = obj4;
									i1++;
								}

								aobj1[i1] = projectteamcontentnode1;
								aobj = aobj1;
								obj = projectteamcontentnode1;
							} else {
								obj = projectteamcontentnode1;
							}
					}
				}
			}
		}

		ProjectTeamNodeCache.refreshViewers(root, true);
		projectTeamTreeViewer.setInput(root);
		if (aobj != null && aobj.length > 0) {
			projectTeamTreeViewer.setExpandedElements(aobj);
			if (obj != null) {
				projectTeamTreeViewer.reveal(obj);
				selection = new StructuredSelection(new Object[] { obj });
				projectTeamTreeViewer.setSelection(selection, true);
			}
		}
		projectTeamTreeViewer.refresh();
		if (root != null) {
			isTeamModifed = true;
			m_projectDefinitionView.validateButtons();
		}
		selection = null;
	}

	public void loadProjectTeamData() {
		this.m_parentProject = null;
		this.isTeamModifed = false;
		if (this.projectTeamMembers_preModify == null)
			return;
		this.m_projectDefinitionView.setLoading(true);
		this.m_projectDefinitionView.disableButtons();
		ProjectTeamStatus localProjectTeamStatus = new ProjectTeamStatus(
				this.m_project);
		logger.info("reloading "
				+ (this.m_project == null ? "CONSTANT_NEW_PROJECT"
						: this.m_project.toDisplayString()));
		this.root = new ProjectTeamContentNode(localProjectTeamStatus, 0);
		ProjectTeamNodeCache.setRootNode(getRoot());
		final IOperationService localIOperationService = (IOperationService) OSGIUtil
				.getService(AifrcpPlugin.getDefault(), IOperationService.class);
		String[] arrayOfString = { TCComponentProject
				.getDisplayName(ProjectManager.getInstance().getTCSession()) };
		String str = ProjectConstants.projectReg.getStringWithSubstitution(
				"Loading.teamMessage", arrayOfString);
		final AbstractAIFOperation local27 = new AbstractAIFOperation(str,
				false) {
			public void executeOperation() {
				ProjectTeamTreeManager.this.addProjectOwnerNode();
			}
		};
		local27.addOperationListener(new InterfaceAIFOperationListener() {
			public void startOperation(String paramAnonymousString) {
			}

			public void endOperation() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						ProjectTeamTreeManager.this.m_projectDefinitionView
								.setLoading(false);
						ProjectTeamTreeManager.this.m_projectDefinitionView
								.enableButtons();
						ProjectTeamNodeCache.refreshViewers(
								ProjectTeamTreeManager.this.root, true);
						ProjectTeamTreeManager.this.projectTeamTreeViewer
								.setInput(ProjectTeamTreeManager.this.root);
						ProjectTeamTreeManager.this.projectTeamTreeViewer
								.refresh();
					}
				});
			}
		});
		AbstractAIFOperation local29 = new AbstractAIFOperation(str, false) {
			public void executeOperation() {
				try {
					ProjectTeamTreeManager.this.setTeamStatus();
					ProjectTeamTreeManager.this.m_projectTeamFilter
							.setProject(ProjectTeamTreeManager.this.m_project);
				} catch (Exception localException) {
				}
			}
		};
		local29.addOperationListener(new InterfaceAIFOperationListener() {
			public void startOperation(String paramAnonymousString) {
			}

			public void endOperation() {
				localIOperationService.queueOperation(local27);
				ProjectTeamTreeManager.this.m_projectDefinitionView
						.setLoading(false);
				ProjectTeamTreeManager.this.m_projectDefinitionView
						.enableButtons();
			}
		});
		localIOperationService.queueOperation(local29);
	}

	private void loadUserProperties() {
		ArrayList arraylist = new ArrayList();
		ArrayList arraylist1 = new ArrayList();
		ArrayList arraylist2 = new ArrayList();
		com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectTeamData aprojectteamdata[];
		int k = (aprojectteamdata = projectTeamMembers_preModify).length;
		for (int i = 0; i < k; i++) {
			com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectTeamData projectteamdata = aprojectteamdata[i];
			TCComponent atccomponent1[];
			int i1 = (atccomponent1 = projectteamdata.regularMembers).length;
			for (int l = 0; l < i1; l++) {
				TCComponent tccomponent = atccomponent1[l];
				if (tccomponent instanceof TCComponentGroupMember)
					arraylist.add(tccomponent);
				else if (tccomponent instanceof TCComponentUser) {
					TCComponentUser tccomponentuser1 = (TCComponentUser) tccomponent;
					arraylist1.add(tccomponentuser1);
				} else if (tccomponent instanceof TCComponentGroup)
					arraylist2.add(tccomponent);
			}

		}

		String as[] = { "the_group", "the_role", "the_user", "object_name",
				"status" };
		ProjectDataPanel.loadPropertiesInBulk(arraylist, as);
		for (int j = 0; j < arraylist.size(); j++) {
			TCComponentGroupMember tccomponentgroupmember = (TCComponentGroupMember) arraylist
					.get(j);
			try {
				TCComponentUser tccomponentuser = tccomponentgroupmember
						.getUser();
				arraylist1.add(tccomponentuser);
				TCComponentGroup tccomponentgroup = tccomponentgroupmember
						.getGroup();
				if (!arraylist2.contains(tccomponentgroup))
					arraylist2.add(tccomponentgroup);
				TCComponentRole tccomponentrole = tccomponentgroupmember
						.getRole();
				if (!arraylist2.contains(tccomponentrole))
					arraylist2.add(tccomponentrole);
			} catch (TCException tcexception) {
				logger.error(tcexception.getClass().getName(), tcexception);
			}
		}

		ProjectDataPanel.loadPropertiesInBulk(arraylist1,
				TCComponentProject.userPropertyNames);
		if (arraylist2.size() > 0) {
			TCComponent atccomponent[] = (TCComponent[]) arraylist2
					.toArray(new TCComponent[arraylist2.size()]);
			TCComponentType.cacheTCPropertiesSet(atccomponent,
					TCComponentProject.groupPropertyNames, true);
		}
	}

	private void addProjectOwnerNode() {
		if (this.m_project != null) {
			TCComponentGroup localTCComponentGroup = null;
			try {
				localTCComponentGroup = (TCComponentGroup) this.m_project
						.getReferenceProperty("project_team");
				if (localTCComponentGroup != null) {
					TCComponent[] arrayOfTCComponent1 = ProjectManager
							.getInstance().getGroupMembersProjectAdmin(
									localTCComponentGroup,
									getProjectAdminRole());
					if (arrayOfTCComponent1 != null)
						for (TCComponent localTCComponent : arrayOfTCComponent1) {
							IC_UserProxy localIC_UserProxy = null;
							if ((localTCComponent instanceof TCComponentUser))
								localIC_UserProxy = new IC_UserProxy(
										localTCComponentGroup,
										getProjectAdminRole(),
										(TCComponentUser) localTCComponent);
							else if ((localTCComponent instanceof TCComponentGroupMember))
								localIC_UserProxy = new IC_UserProxy(
										(TCComponentGroupMember) localTCComponent);
							if (localIC_UserProxy != null) {
								ProjectTeamUserNode localProjectTeamUserNode = new ProjectTeamUserNode(
										localIC_UserProxy, 3);
								IC_GroupRoleHashable localIC_GroupRoleHashable = new IC_GroupRoleHashable(
										localTCComponentGroup,
										getProjectAdminRole());
								ProjectTeamStatus localProjectTeamStatus = (ProjectTeamStatus) this.root
										.getObject();
								localProjectTeamStatus
										.setProjAdministrator(localIC_UserProxy
												.getUser());
								ProjectTeamRoleNode localProjectTeamRoleNode = ProjectTeamNodeCache
										.createOrFindNode(localIC_GroupRoleHashable);
								localProjectTeamRoleNode
										.addChild(localProjectTeamUserNode);
								localProjectTeamRoleNode.setLoaded(true);
								this.root.addChild(localProjectTeamRoleNode);
							}
						}
				}
			} catch (Exception localException) {
				logger.error(localException.getClass().getName(),
						localException);
			}
		}
	}

	private void setTeamStatus() {
		ProjectTeamStatus projectteamstatus = ProjectTeamStatus
				.getInstance(m_project);
		com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectTeamData aprojectteamdata[];
		int j = (aprojectteamdata = projectTeamMembers_preModify).length;
		for (int i = 0; i < j; i++) {
			com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectTeamData projectteamdata = aprojectteamdata[i];
			ArrayList arraylist = new ArrayList();
			TCComponent atccomponent[];
			int l = (atccomponent = projectteamdata.privMembers).length;
			for (int k = 0; k < l; k++) {
				TCComponent tccomponent = atccomponent[k];
				arraylist.add((TCComponentUser) tccomponent);
			}

			ArrayList arraylist1 = new ArrayList();
			TCComponent atccomponent1[];
			int i1 = (atccomponent1 = projectteamdata.projectTeamAdmins).length;
			for (l = 0; l < i1; l++) {
				TCComponent tccomponent1 = atccomponent1[l];
				arraylist1.add((TCComponentUser) tccomponent1);
			}

			String as[] = { "user_id", "user_name", "owning_site",
					"object_string" };
			ProjectDataPanel.loadPropertiesInBulk(arraylist, as);
			projectteamstatus.setPrivMembers(arraylist);
			projectteamstatus.setPTAs(arraylist1);
			l = 0;
			i1 = 0;
			TCComponent atccomponent2[];
			int k1 = (atccomponent2 = projectteamdata.regularMembers).length;
			for (int j1 = 0; j1 < k1; j1++) {
				TCComponent tccomponent2 = atccomponent2[j1];
				if (tccomponent2 instanceof TCComponentGroupMember) {
					TCComponentGroupMember tccomponentgroupmember = (TCComponentGroupMember) tccomponent2;
					projectteamstatus
							.addToProjectMembers(tccomponentgroupmember);
					l++;
					ProjectTeamContentNode projectteamcontentnode = null;
					try {
						IC_GroupRoleHashable ic_grouprolehashable = new IC_GroupRoleHashable(
								tccomponentgroupmember.getGroup(),
								tccomponentgroupmember.getRole());
						String s = tccomponentgroupmember.getGroup().getType();
						if (s.equals("Group")) {
							projectteamcontentnode = root
									.findOrCreateRoleNode(ic_grouprolehashable);
							TCComponentUser tccomponentuser = tccomponentgroupmember
									.getUser();
							IC_UserProxy ic_userproxy = new IC_UserProxy(
									tccomponentgroupmember);
							ProjectTeamUserNode projectteamusernode = ProjectTeamNodeCache
									.createOrFindNode(ic_userproxy);
							projectteamcontentnode
									.addChild(projectteamusernode);
							projectteamcontentnode.setLoaded(true);
							if (arraylist.contains(tccomponentuser)) {
								projectteamusernode
										.setPrivilege(ProjectConstants.PRIVILEGED);
								projectteamstatus
										.addPrivilegedMembers(ic_userproxy);
							}
							if (arraylist1.contains(tccomponentuser)) {
								projectteamusernode
										.setPrivilege(ProjectConstants.TEAMADMIN);
								projectteamstatus
										.addProjectTeamAdmin(ic_userproxy);
							}
						}
					} catch (TCException tcexception) {
						logger.error(tcexception.getClass().getName(),
								tcexception);
					}
					if (projectteamcontentnode != null)
						root.addChild(projectteamcontentnode);
				} else if (tccomponent2 instanceof TCComponentGroup) {
					TCComponentGroup tccomponentgroup = (TCComponentGroup) tccomponent2;
					projectteamstatus.addGroupToProjectTeam(tccomponentgroup);
					i1++;
					ProjectTeamGroupNode projectteamgroupnode = new ProjectTeamGroupNode(
							tccomponentgroup);
					root.addChild(projectteamgroupnode);
				}
			}

			logger.info((new StringBuilder(String.valueOf(l)))
					.append(" groupmembers and ")
					.append(i1)
					.append(" groups are loaded to the project ")
					.append(m_project != null ? m_project.toDisplayString()
							: "CONSTANT_NEW_PROJECT").toString());
		}

	}

	public TreeViewer getProjectTeamViewer() {
		return this.projectTeamTreeViewer;
	}

	public void setProject(TCComponentProject paramTCComponentProject) {
		this.m_project = paramTCComponentProject;
	}

	public TCComponentProject getProject() {
		return this.m_project;
	}

	public void refreshViewer(OrgTreeContentNode paramOrgTreeContentNode,
			boolean paramBoolean) {
		if (!this.m_availableTreeViewer.getControl().isDisposed()) {
			this.m_availableTreeViewer.refresh(paramOrgTreeContentNode);
			if (paramBoolean)
				this.m_availableTreeViewer.expandAll();
		}
	}

	public void reloadOrganizationTree() {
		this.m_availableTreeViewer.collapseAll();
		this.m_availableTreeViewer.setInput(OrgTreeContentNode
				.getRootNode(this));
		this.m_availableTreeViewer.refresh();
	}

	public void clearProjectTeamSearch(boolean paramBoolean) {
		this.m_searchProjectTeamText.setText("");
		this.combo.deselectAll();
		this.projectTeamFindFilter.setFilterString("*");
		this.projectTeamFindFilter.setFilterType(-1);
		this.projectTeamTreeViewer.collapseAll();
		if (paramBoolean)
			this.projectTeamTreeViewer.refresh();
	}

	public void refreshViewer(
			ProjectTeamContentNode paramProjectTeamContentNode,
			boolean paramBoolean) {
		if (!this.projectTeamTreeViewer.getControl().isDisposed()) {
			if (!this.projectTeamTreeViewer.isBusy())
				this.projectTeamTreeViewer.refresh(paramProjectTeamContentNode);
			if (paramBoolean) {
				int i = this.projectTeamTreeViewer.getAutoExpandLevel();
				this.projectTeamTreeViewer.expandToLevel(
						paramProjectTeamContentNode, i + 1);
			}
		}
	}

	public boolean teamModified() {
		return this.isTeamModifed;
	}

	public void resetOrgTree() {
		this.m_availableTreeViewer.setInput(OrgTreeContentNode
				.getRootNode(this));
		ViewerFilter[] arrayOfViewerFilter = this.m_availableTreeViewer
				.getFilters();
		for (int i = 0; i < arrayOfViewerFilter.length; i++)
			if ((arrayOfViewerFilter[i] instanceof ProgramTreeFilter))
				this.m_availableTreeViewer.removeFilter(arrayOfViewerFilter[i]);
	}

	public void reloadProjectTeam() {
		if (this.projectTeamMembers_preModify != null) {
			this.projectTeamMembers_preModify = null;
			if (this.m_project != null) {
				this.m_projectDefinitionView.setLoading(true);
				this.m_projectDefinitionView.disableButtons();
				String str = ProjectConstants.projectReg
						.getString("loadingProjects.MESSAGE");
				AbstractAIFOperation local31 = new AbstractAIFOperation(str,
						false) {
					public void executeOperation() {
						try {
							ProjectManager
									.setProjectTeamMembersPropertyPolicy();
							ProjectClientId[] arrayOfProjectClientId = new ProjectClientId[1];
							arrayOfProjectClientId[0] = new ProjectClientId();
							arrayOfProjectClientId[0].clientId = "PLS-RAC-SESSION";
							arrayOfProjectClientId[0].tcProject = ProjectTeamTreeManager.this.m_project;
							ProjectTeamsResponse localProjectTeamsResponse = ProjectManager
									.getInstance().getProjectTeamMembers(
											arrayOfProjectClientId);
							ProjectTeamTreeManager.this.projectTeamMembers_preModify = localProjectTeamsResponse.projectTeams;
						} catch (Exception localException) {
							ProjectTeamTreeManager.logger.error(localException
									.getClass().getName(), localException);
						}
					}
				};
				local31.addOperationListener(new InterfaceAIFOperationListener() {
					public void startOperation(String paramAnonymousString) {
					}

					public void endOperation() {
						ProjectTeamTreeManager.this.getProjectTeamJobPost();
						ProjectManager.restorePropertyPolicy();
					}
				});
				IOperationService localIOperationService = (IOperationService) OSGIUtil
						.getService(AifrcpPlugin.getDefault(),
								IOperationService.class);
				localIOperationService.queueOperation(local31);
			}
		}
	}

	private void getProjectTeamJobPost() {
		loadProjectTeamData();
	}

	public void setProjectTeamMembers(
			ProjectTeamData[] paramArrayOfProjectTeamData) {
		this.projectTeamMembers_preModify = paramArrayOfProjectTeamData;
	}

	public boolean checkUserData(TCComponentUser paramTCComponentUser) {
		boolean bool = true;
		if ((this.m_project == null) || (paramTCComponentUser == null))
			return true;
		try {
			bool = checkIfUserCreatedWSOInContextOfAProgramUsingSavedQueries(paramTCComponentUser);
		} catch (TCException localTCException) {
			logger.error(localTCException.getClass().getName(),
					localTCException);
			bool = checkIfUserCreatedWSOInContextOfAProgramUsingSmartFolderAPI(paramTCComponentUser);
		}
		return bool;
	}

	private boolean checkIfGroupCreatedWSOInContextOfAProgramUsingSavedQueries(
			String paramString) throws TCException {
		boolean bool = true;
		TCComponent[] arrayOfTCComponent = null;
		String str1 = this.m_project.getProperty("project_id");
		if (paramString == null)
			return true;
		String str2 = "__FindObjectsCreatedInContextOfAProgramByAGroup";
		TCComponentQueryType localTCComponentQueryType = (TCComponentQueryType) getTCSession()
				.getTypeComponent("ImanQuery");
		if (str2 != null) {
			TCComponentQuery localTCComponentQuery = (TCComponentQuery) localTCComponentQueryType
					.find(str2);
			TCQueryClause[] arrayOfTCQueryClause = localTCComponentQuery
					.describe();
			ArrayList localArrayList = new ArrayList();
			String[] arrayOfString1 = null;
			if (arrayOfTCQueryClause != null) {
				for (int i = 0; i < arrayOfTCQueryClause.length; i++) {
					if (arrayOfTCQueryClause[i].getAttributeName()
							.equalsIgnoreCase("owning_group.name"))
						localArrayList.add(0, arrayOfTCQueryClause[i]
								.getUserEntryNameDisplay());
					if (arrayOfTCQueryClause[i].getAttributeName()
							.equalsIgnoreCase("owning_project.project_id"))
						localArrayList.add(1, arrayOfTCQueryClause[i]
								.getUserEntryNameDisplay());
				}
				arrayOfString1 = new String[localArrayList.size()];
				arrayOfString1 = (String[]) localArrayList
						.toArray(arrayOfString1);
			}
			String[] arrayOfString2 = { paramString, str1 };
			arrayOfTCComponent = ProjectManager.getInstance().runQueryHelper(
					localTCComponentQuery, arrayOfString1, arrayOfString2);
			if ((arrayOfTCComponent != null) && (arrayOfTCComponent.length > 0)
					&& (arrayOfTCComponent[0] != null))
				bool = false;
		}
		return bool;
	}

	private boolean checkIfUserCreatedWSOInContextOfAProgramUsingSavedQueries(
			TCComponentUser paramTCComponentUser) throws TCException {
		boolean bool = true;
		TCComponent[] arrayOfTCComponent = null;
		String str1 = this.m_project.getProperty("project_id");
		if (paramTCComponentUser == null)
			return true;
		String str2 = "__FindObjectsCreatedInContextOfAProgramByAUser";
		TCComponentQueryType localTCComponentQueryType = (TCComponentQueryType) getTCSession()
				.getTypeComponent("ImanQuery");
		if (str2 != null) {
			TCComponentQuery localTCComponentQuery = (TCComponentQuery) localTCComponentQueryType
					.find(str2);
			if (localTCComponentQuery == null)
				throw new TCException(
						"Query __FindObjectsCreatedInContextOfAProgramByAUser was not found!");
			TCQueryClause[] arrayOfTCQueryClause = localTCComponentQuery
					.describe();
			ArrayList localArrayList = new ArrayList();
			String[] arrayOfString1 = null;
			if (arrayOfTCQueryClause != null) {
				for (int i = 0; i < arrayOfTCQueryClause.length; i++) {
					if (arrayOfTCQueryClause[i].getAttributeName()
							.equalsIgnoreCase("owning_user.user_id"))
						localArrayList.add(0, arrayOfTCQueryClause[i]
								.getUserEntryNameDisplay());
					if (arrayOfTCQueryClause[i].getAttributeName()
							.equalsIgnoreCase("owning_project.project_id"))
						localArrayList.add(1, arrayOfTCQueryClause[i]
								.getUserEntryNameDisplay());
				}
				arrayOfString1 = new String[localArrayList.size()];
				arrayOfString1 = (String[]) localArrayList
						.toArray(arrayOfString1);
			}
			String[] arrayOfString2 = { paramTCComponentUser.toString(), str1 };
			arrayOfTCComponent = ProjectManager.getInstance().runQueryHelper(
					localTCComponentQuery, arrayOfString1, arrayOfString2);
			if ((arrayOfTCComponent != null) && (arrayOfTCComponent.length > 0)
					&& (arrayOfTCComponent[0] != null))
				bool = false;
		}
		return bool;
	}

	private boolean checkIfUserCreatedWSOInContextOfAProgramUsingSmartFolderAPI(
			TCComponentUser paramTCComponentUser) {
		boolean bool = true;
		try {
			ProjectLevelSecurity.Filter[] arrayOfFilter = new ProjectLevelSecurity.Filter[2];
			arrayOfFilter[0] = new ProjectLevelSecurity.Filter();
			arrayOfFilter[0].sourceTypeName = "WorkspaceObject";
			arrayOfFilter[0].name = "owning_user";
			arrayOfFilter[0].value = paramTCComponentUser.toString();
			arrayOfFilter[1] = new ProjectLevelSecurity.Filter();
			arrayOfFilter[1].sourceTypeName = "WorkspaceObject";
			arrayOfFilter[1].name = "owning_project";
			arrayOfFilter[1].value = this.m_project.toString();
			ProjectLevelSecurity.GetFilteredProjectDataInputData[] arrayOfGetFilteredProjectDataInputData = new ProjectLevelSecurity.GetFilteredProjectDataInputData[1];
			arrayOfGetFilteredProjectDataInputData[0] = new ProjectLevelSecurity.GetFilteredProjectDataInputData();
			arrayOfGetFilteredProjectDataInputData[0].projectID = this.m_project
					.getProperty("project_id");
			arrayOfGetFilteredProjectDataInputData[0].filters = arrayOfFilter;
			ProjectLevelSecurityService localProjectLevelSecurityService = ProjectLevelSecurityService
					.getService(getTCSession());
			ProjectLevelSecurity.GetFilteredProjectDataResponse localGetFilteredProjectDataResponse = localProjectLevelSecurityService
					.getFilteredProjectData(arrayOfGetFilteredProjectDataInputData);
			TCComponent[] arrayOfTCComponent = localGetFilteredProjectDataResponse.output[0].filteredData;
			if ((arrayOfTCComponent != null) && (arrayOfTCComponent.length > 0))
				bool = false;
		} catch (Exception localException) {
			bool = true;
			logger.error(localException.getClass().getName(), localException);
		}
		return bool;
	}

	public boolean checkGroupData(String paramString) {
		boolean bool = true;
		if ((this.m_project == null) || (paramString == null))
			return bool;
		try {
			bool = checkIfGroupCreatedWSOInContextOfAProgramUsingSavedQueries(paramString);
		} catch (TCException localTCException) {
			logger.error(localTCException.getClass().getName(),
					localTCException);
			bool = checkIfGroupCreatedWSOInContextOfAProgramUsingSmartFolderAPI(paramString);
		}
		return bool;
	}

	private boolean checkIfGroupCreatedWSOInContextOfAProgramUsingSmartFolderAPI(
			String paramString) {
		boolean bool = true;
		try {
			ProjectLevelSecurity.Filter[] arrayOfFilter = new ProjectLevelSecurity.Filter[2];
			arrayOfFilter[0] = new ProjectLevelSecurity.Filter();
			arrayOfFilter[0].sourceTypeName = "WorkspaceObject";
			arrayOfFilter[0].name = "owning_group";
			arrayOfFilter[0].value = paramString.toString();
			arrayOfFilter[1] = new ProjectLevelSecurity.Filter();
			arrayOfFilter[1].sourceTypeName = "WorkspaceObject";
			arrayOfFilter[1].name = "owning_project";
			arrayOfFilter[1].value = this.m_project.toString();
			ProjectLevelSecurity.GetFilteredProjectDataInputData[] arrayOfGetFilteredProjectDataInputData = new ProjectLevelSecurity.GetFilteredProjectDataInputData[1];
			arrayOfGetFilteredProjectDataInputData[0] = new ProjectLevelSecurity.GetFilteredProjectDataInputData();
			arrayOfGetFilteredProjectDataInputData[0].projectID = this.m_project
					.getProperty("project_id");
			arrayOfGetFilteredProjectDataInputData[0].filters = arrayOfFilter;
			ProjectLevelSecurityService localProjectLevelSecurityService = ProjectLevelSecurityService
					.getService(getTCSession());
			ProjectLevelSecurity.GetFilteredProjectDataResponse localGetFilteredProjectDataResponse = localProjectLevelSecurityService
					.getFilteredProjectData(arrayOfGetFilteredProjectDataInputData);
			TCComponent[] arrayOfTCComponent = localGetFilteredProjectDataResponse.output[0].filteredData;
			if ((arrayOfTCComponent.length > 0)
					&& (arrayOfTCComponent[0] != null))
				bool = false;
		} catch (Exception localException) {
			bool = true;
			logger.error(localException.getClass().getName(), localException);
		}
		return bool;
	}

	public void processComponentEvents(
			AIFComponentEvent[] paramArrayOfAIFComponentEvent) {
		if (isOwnerChanged(paramArrayOfAIFComponentEvent))
			reloadProjectTeam();
	}

	private boolean isOwnerChanged(
			AIFComponentEvent[] paramArrayOfAIFComponentEvent) {
		for (AIFComponentEvent localAIFComponentEvent : paramArrayOfAIFComponentEvent) {
			InterfaceAIFComponent localInterfaceAIFComponent = localAIFComponentEvent
					.getComponent();
			if (((localInterfaceAIFComponent instanceof TCComponentProject))
					&& ((localAIFComponentEvent instanceof AIFComponentPropertyChangeEvent))) {
				AIFComponentPropertyChangeEvent localAIFComponentPropertyChangeEvent = (AIFComponentPropertyChangeEvent) localAIFComponentEvent;
				Object localObject1 = localAIFComponentPropertyChangeEvent
						.getFromValue("owning_user");
				Object localObject2 = localAIFComponentPropertyChangeEvent
						.getToValue("owning_user");
				Object localObject3 = localAIFComponentPropertyChangeEvent
						.getFromValue("owning_group");
				Object localObject4 = localAIFComponentPropertyChangeEvent
						.getToValue("owning_group");
				if (((localObject1 != null) && (!localObject1
						.equals(localObject2)))
						|| ((localObject3 != null) && (!localObject3
								.equals(localObject4))))
					return true;
			}
		}
		return false;
	}

	public ProjectTeamFilter getProjectTeamFilter() {
		return this.m_projectTeamFilter;
	}

	public TreeViewer getAvailableTreeViewer() {
		return this.m_availableTreeViewer;
	}
}