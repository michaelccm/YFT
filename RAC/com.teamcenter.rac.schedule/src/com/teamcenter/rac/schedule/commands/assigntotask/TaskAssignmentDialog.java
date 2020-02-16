package com.teamcenter.rac.schedule.commands.assigntotask;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.wb.swt.SWTResourceManager;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentDiscipline;
import com.teamcenter.rac.kernel.TCComponentFnd0Qualification;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentResourcePool;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentTcRelation;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.providers.TCComponentLabelProvider;
import com.teamcenter.rac.qualifications.composites.QualificationChooser;
import com.teamcenter.rac.schedule.ScheduleViewApplication;
import com.teamcenter.rac.schedule.commands.deferred.ScheduleDeferredContext;
import com.teamcenter.rac.schedule.common.organization.DisciplineTreeLabelProvider;
import com.teamcenter.rac.schedule.common.organization.OrgTreeContentProvider;
import com.teamcenter.rac.schedule.common.organization.OrgTreeLabelProvider;
import com.teamcenter.rac.schedule.common.organization.ProjectTeamTreeContentProvider;
import com.teamcenter.rac.schedule.common.organization.ProjectTeamTreeLabelProvider;
import com.teamcenter.rac.schedule.common.organization.TaskActorInterface;
import com.teamcenter.rac.schedule.common.organization.TypeImageCache;
import com.teamcenter.rac.schedule.filteredTree.SMFilteredTree;
import com.teamcenter.rac.schedule.filteredTree.SMOrganizationTree;
import com.teamcenter.rac.schedule.project.resourceGraph.IGraphCommand;
import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.internal.rac.core.ICTService;
import com.teamcenter.services.internal.rac.core._2011_06.ICT.Arg;
import com.teamcenter.services.internal.rac.core._2011_06.ICT.InvokeICTMethodResponse;
import com.teamcenter.services.rac.projectmanagement.ScheduleManagementService;
import com.teamcenter.services.rac.projectmanagement._2014_06.ScheduleManagement;
import com.teamcenter.services.rac.projectmanagement._2014_06.ScheduleManagement.QualificationInfo;

public class TaskAssignmentDialog extends Dialog
  implements SelectionListener, Listener, FocusListener
{
  protected Object result;
  protected Registry registry = Registry.getRegistry("com.teamcenter.rac.schedule.commands.assigntotask.properties.properties");
  private static final Logger logger = Logger.getLogger(TaskAssignmentDialog.class);
  protected Shell shlTaskAssignment;
  protected Text orgTreeSearchText;
  protected boolean canModify = false;
  protected ScheduleViewApplication theApp;
  protected TCComponentSchedule theSchedule = null;
  protected static final String SEGOEFONT = "Segoe UI";
  protected static final String CONSOLASFONT = "Consolas";
  public static final String ROOT = "Root";
  protected TCSession tcSession = null;
  protected TreeViewer orgTreeViewer = null;
  protected TreeViewer scheduleTaskTreeViewer = null;
  protected TreeViewer teamTreeViewer = null;
  protected StructuredViewer selectedViewer = null;
  protected TreeViewer disciplineTreeViewer = null;
  protected List<TCComponent> selectedTasks = null;
  protected TabFolder tabFolder = null;
  protected Group grpAssignAllTasks = null;
  protected Button btnOk = null;
  protected Button btnCancel = null;
  protected Button btnAdd = null;
  protected Button btnRemove = null;
  protected SMFilteredTree filteredTaskTree = null;
  protected SMOrganizationTree filteredOrgTree = null;
  protected SMFilteredTree filteredDisciplineTree = null;
  protected SMFilteredTree filteredTeamTree = null;
  protected Group orgResourceGroup = null;
  protected Group disciplinegroup = null;
  protected Group teamGroup = null;
  protected Button btnPlaceHolderAssignment = null;
  protected Button buttonTeamPlaceHolder = null;
  protected Button btnAnyMember = null;
  protected Button btnAllMembers = null;
  protected Button buttonTeamAnyMember = null;
  protected Button btnSpecificGroup = null;
  protected Button btnAnyGroup = null;
  protected Button buttonTeamAllMember = null;
  protected Button buttonTeamSpecificGroup = null;
  protected Button buttonTeamAnyGroup = null;
  protected Group grpResourcePoolOptions = null;
  protected Group groupTeamResourcePool = null;
  protected Combo comboProjects = null;
  protected int previousSelectionIndex = -1;
  protected Display display = null;
  protected boolean isOrgGrpRoleInSelection = false;
  protected boolean isOrgGrpUserInSelection = false;
  protected boolean isTeamGrpRoleInSelection = false;
  protected boolean isTeamMemberInSelection = false;
  protected ScheduleTaskTreeModel scheduleRoot = null;
  protected TreeColumn assignmentNote = null;
  protected TreeColumn taskQual = null;
  protected TreeColumn discColumn = null;
  protected TreeColumn placeholderColumn = null;
  protected TreeColumn resourceGraphC = null;
  protected TreeColumn resourceLevelC = null;
  protected TreeColumn privilegeUserC = null;
  protected TreeColumn tasks = null;
  protected Group groupRightPane = null;
  protected Group groupLeftPane = null;
  protected UIAssignmentCache assignmentCache = null;
  protected OrgTreeContentProvider orgTreeContentProvider = null;
  protected ProjectTeamTreeContentProvider teamTreeContentProvider = null;
  //protected DisciplineTreeContentProvider disciplineTreeContentProvider = null;
  protected MyDisciplineTreeContentProvider disciplineTreeContentProvider = null;
  private Listener closeListener = null;
  protected CheckboxTableViewer filteringTableviewViewer_dis = null;
  protected CheckboxTableViewer filteringTableviewViewer_grprole = null;
  protected CheckboxTableViewer filteringTableviewViewer_qual = null;
  protected TableViewer filteringResults_viewer = null;
  protected TableViewer assignQualificationTableViewer = null;
  protected Button assignQualBtn;
  protected Button removeQualBtn;
  private Button alternateButton;
  protected QualificationChooser qualificationChooser;
  private String changeLockedRulePref = "SM_PREVENT_UPDATE_STATES";
  private String PRESERVATION_ERROR = "PRESERVATION_ERROR_TITLE";
  protected static final String RGROLENAME = "Resource Graph Viewers";
  private String currentRole = null;
  private boolean showResourceGraph = false;
  protected ResourceGraphUtil resourceGraphUtil;
  private Text orgSearchText;
  private Button btnOrganizationSearch;
  private Button btnOrganizationSearchClr;
  protected TCComponent[] memebers = null;
  protected List<TCComponentUser> member_users=new ArrayList<TCComponentUser>();
  protected List<TCComponentDiscipline> member_displices=new ArrayList<TCComponentDiscipline>();
  public TaskAssignmentDialog(Shell paramShell, int paramInt, ScheduleViewApplication paramScheduleViewApplication, boolean paramBoolean, UIAssignmentCache paramUIAssignmentCache)
  {
    super(paramShell, paramInt);
    System.out.println("TaskAssignmentDialog");
    this.theApp = paramScheduleViewApplication;
    this.canModify = paramBoolean;
    this.tcSession = this.theApp.getSession();
    this.selectedTasks = this.theApp.getSelectedComponents();
    this.assignmentCache = paramUIAssignmentCache;
    TCComponentRole localTCComponentRole = this.theApp.getSession().getCurrentRole();
    try
    {
      this.currentRole = localTCComponentRole.getTCProperty("role_name").getStringValue();
      if ((this.currentRole != null) && (!ScheduleDeferredContext.inDeferredSession()) && ("Resource Graph Viewers".equals(this.currentRole)))
        this.showResourceGraph = true;
    }
    catch (TCException localTCException)
    {
      logger.error("Exception:getCurrentRole", localTCException);
    }
    ResourceGraphUtil.clearUserCache();
  }

  public TCSession getTcSession()
  {
    return this.tcSession;
  }

  public Object open()
  {
	System.out.println("open");
    createContents();
    TypeImageCache.registerDialog(this);
    Monitor localMonitor = getParent().getDisplay().getPrimaryMonitor();
    Rectangle localRectangle1 = localMonitor.getBounds();
    Rectangle localRectangle2 = this.shlTaskAssignment.getBounds();
    int i = localRectangle1.x + (localRectangle1.width - localRectangle2.width) / 2;
    int j = localRectangle1.y + (localRectangle1.height - localRectangle2.height) / 2;
    this.shlTaskAssignment.setLocation(i, j);
    this.shlTaskAssignment.open();
    this.shlTaskAssignment.layout();
    this.display = getParent().getDisplay();
    addCloseListener();
    while (!this.shlTaskAssignment.isDisposed())
      if (!this.display.readAndDispatch())
        this.display.sleep();
    return this.result;
  }

  protected void addCloseListener()
  {
    this.closeListener = new Listener()
    {
      public void handleEvent(Event paramAnonymousEvent)
      {
        WidgetState.disposeAll();
        TypeImageCache.disposeImageMap(TaskAssignmentDialog.this);
      }
    };
    this.shlTaskAssignment.addListener(21, this.closeListener);
  }

  public Display getDisplay()
  {
    return this.display;
  }

  private void createContents()
  {
	System.out.println("createContents");
    this.shlTaskAssignment = new Shell(getParent(), 67680);
    this.shlTaskAssignment.setSize(1186, 722);
    this.shlTaskAssignment.setLayout(new FormLayout());
    this.shlTaskAssignment.setText(this.registry.getString("TaskAssignmentDialog.TITLE"));
    this.shlTaskAssignment.setImage(Registry.getRegistry(AbstractAIFDialog.class).getImage("aifDesktop.ICON"));
    createLeftPanel();
    createRightPanel();
    createBottomPanel();
    setReadOnlyMode();
    createContentsPost();
    this.scheduleTaskTreeViewer.getControl().forceFocus();
  }

  private void createMidPanel()
  {
	System.out.println("createMidPanel");
    this.btnRemove = new Button(this.grpAssignAllTasks, 0);
    this.btnRemove.setBounds(559, 355, 66, 25);
    this.btnRemove.setToolTipText(this.registry.getString("TaskAssignmentDialog.Remove.ToolTip.TEXT"));
    this.btnRemove.setFont(SWTResourceManager.getFont("Segoe UI", 8, 0));
    this.btnRemove.setEnabled(false);
    this.btnRemove.setText(this.registry.getString("TaskAssignmentDialog.Remove.TITLE"));
    this.btnAdd = new Button(this.grpAssignAllTasks, 0);
    this.btnAdd.setBounds(559, 286, 66, 25);
    this.btnAdd.setToolTipText(this.registry.getString("TaskAssignmentDialog.Add.ToolTip.TEXT"));
    this.btnAdd.setFont(SWTResourceManager.getFont("Segoe UI", 8, 0));
    this.btnAdd.setText(this.registry.getString("TaskAssignmentDialog.Add.TITLE"));
    this.btnAdd.setEnabled(false);
  }

  protected void createContentsPost()
  {
  }

  protected void createTabPanel(Group paramGroup)
  {
	System.out.println("createTabPanel");
    paramGroup.setLayout(null);
    this.tabFolder = new TabFolder(paramGroup, 0);
    this.tabFolder.setBounds(10, 20, 500, 528);
    this.tabFolder.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    this.tabFolder.addSelectionListener(this);
  }

  protected void createRightPanel()
  {
	System.out.println("createRightPanel");
    this.groupRightPane = new Group(this.grpAssignAllTasks, 0);
    this.groupRightPane.setBounds(631, 34, 513, 561);
    createTabPanel(this.groupRightPane);
    createAddPanel(this.groupRightPane);
    createScheduleTaskTree();
    //createOrgTeamGroup();
    //createProjectTeamGroup();
    try {
		getMemberShip2();//获取人员和学科
	} catch (TCException e) {
		e.printStackTrace();  
	} catch (ServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    createDisciplineGroup();
    //createQualificationsGroup();
    //createSearchGroup();
  }
  
//  protected void getMemberShip() throws TCException{
//	System.out.println("getMemberShip");
//	memebers =getSchedule().getReferenceListProperty("fnd0Schedulemember_taglist");
//  	if(memebers!=null && memebers.length>0){
//  		for (int i = 0; i < memebers.length; i++) {
//  			if(memebers[i] instanceof TCComponentScheduleMember){
//  				 System.out.println("is TCComponentScheduleMember ");
//  				 TCComponent resource_tag = memebers[i].getReferenceProperty("resource_tag");
//  				 if(resource_tag instanceof TCComponentUser){
//  					System.out.println("resource_tag is TCComponentUser ");
//  					 member_users.add((TCComponentUser) resource_tag);
//  				 }
//  				 else if(resource_tag instanceof TCComponentDiscipline){
//  					System.out.println("resource_tag is TCComponentDiscipline ");
//  					member_displices.add((TCComponentDiscipline) resource_tag);
//  				 }
//  			}
//  		}
//  	}
//  }
  
  protected void getProjectMember() throws TCException{
      TCComponent[] projects = getSchedule().getReferenceListProperty("project_list");
      if ((projects != null) && (projects.length > 0))
      {
        TCComponentProject project = (TCComponentProject)projects[0];
        TCComponent projectTeam = project.getReferenceProperty("project_team");
        if (projectTeam != null)
        {
          TCComponent[] projectMemmbers = projectTeam.getReferenceListProperty("project_members");
          if(projectMemmbers!=null && projectMemmbers.length > 0){
        	  System.out.println("获取到projectMembers 为"+projectMemmbers.length);
        	  for (int j = 0; j < projectMemmbers.length; j++) {
        		  if ((projectMemmbers[j] instanceof TCComponentGroupMember))
                  {
        			TCComponentGroupMember groupmember = (TCComponentGroupMember)projectMemmbers[j];
        			TCComponentUser user = groupmember.getUser();
                    member_users.add((TCComponentUser) user);
                  }
        	  }
          }
        }
      }
  }
  protected boolean getUsers(TCComponentDiscipline discipline) throws TCException{
	  TCComponentContextList childs = discipline.getRelatedList();
	  if(childs!=null ){
		  int count  =  childs.getListCount();
		  if(member_users.size()>0)
		  {
			  for (int j = 0; j < count; j++) {
				  AIFComponentContext aifComponentContext = childs.get(j);
				  String user_id = "";
				  if(aifComponentContext.getComponent() instanceof TCComponentUser){
					  TCComponentUser user = (TCComponentUser) aifComponentContext.getComponent();
					  user_id = user.getUserId();
				  }
				  for (int i = 0; i < member_users.size(); i++) {
					  if(user_id.equals(member_users.get(i).getUserId())){
						  return true;
					  }
				  }
			  }
		  }
	  }
	  return false;
  }
  protected void getMemberShip2() throws TCException, ServiceException{
	  //获取项目组成员
	  getProjectMember();
	  ICTService service = ICTService.getService(getSchedule().getSession());
	  Arg[] args = new Arg[3];
	  args[0]=new Arg();
	  args[1]=new Arg();
	  args[2]=new Arg();
	  args[0].val = "Discipline";
	  args[1].val = "TYPE::Discipline::Discipline::POM_system_class";
	  args[2].val = "false";
	  InvokeICTMethodResponse response = service.invokeICTMethod("ICCT", "extent", args);
	  ServiceData servicedata = response.serviceData;
	  int plain_lengh = servicedata.sizeOfPlainObjects();
	  for (int i = 0; i < plain_lengh; i++) {
		  TCComponent comp  = servicedata.getPlainObject(i);
		  if(comp instanceof TCComponentDiscipline){
			  TCComponentDiscipline discipline = (TCComponentDiscipline) comp;
			  String name =  comp.getStringProperty("discipline_name");
			  if(name.startsWith("SDT")){
				  if(getUsers(discipline)){
					  member_displices.add(discipline);
				  }
			  }
		  }
	  }
  }
  
  protected void createAddPanel(Group paramGroup)
  {
	System.out.println("createAddPanel");
    createMidPanel();
  }

  protected void createDisciplineGroup()
  {
	System.out.println("createDisciplineGroup");
    this.disciplinegroup = new Group(this.tabFolder, 0);
    this.disciplinegroup.setBounds(0, 0, 480, 531);
    ///this.orgResourceGroup.setLayout(null);
    PatternFilter localPatternFilter = new PatternFilter();
    this.disciplinegroup.setLayout(new FormLayout());
    this.filteredDisciplineTree = new SMFilteredTree(this.disciplinegroup, 68098, localPatternFilter, true);
    FormData localFormData = new FormData();
    localFormData.bottom = new FormAttachment(0, 460);
    localFormData.right = new FormAttachment(0, 463);
    localFormData.top = new FormAttachment(0, 2);
    localFormData.left = new FormAttachment(0, 8);
    this.filteredDisciplineTree.setLayoutData(localFormData);
    Tree localTree = this.filteredDisciplineTree.getViewer().getTree();
    localTree.setFont(SWTResourceManager.getFont("Segoe UI", 8, 0));
    this.filteredDisciplineTree.getFilterControl().setFont(SWTResourceManager.getFont("Segoe UI", 8, 0));
    this.filteredDisciplineTree.setInitialText(this.registry.getString("TaskAssignmentDialog.DisciplineTreeViewer.InitialText"));
    this.filteredDisciplineTree.addFocusListener(this);
    this.disciplineTreeViewer = this.filteredDisciplineTree.getViewer();
    this.disciplineTreeViewer.setAutoExpandLevel(2);
    TabItem localTabItem = new TabItem(this.tabFolder, 0);
    localTabItem.setToolTipText(this.registry.getString("TaskAssignmentDialog.Discipline.ToolTip.TEXT"));
    localTabItem.setText(this.registry.getString("TaskAssignmentDialog.Discipline.Tab.TITLE"));
    localTabItem.setControl(this.disciplinegroup);
  }

  protected void createProjectTeamGroup()
  {
	System.out.println("createProjectTeamGroup");
    this.teamGroup = new Group(this.tabFolder, 0);
    PatternFilter localPatternFilter = new PatternFilter();
    this.teamGroup.setLayout(new FormLayout());
    this.filteredTeamTree = new SMFilteredTree(this.teamGroup, 68098, localPatternFilter, true);
    FormData localFormData1 = new FormData();
    localFormData1.bottom = new FormAttachment(0, 315);
    localFormData1.right = new FormAttachment(0, 463);
    localFormData1.top = new FormAttachment(0, 50);
    localFormData1.left = new FormAttachment(0, 8);
    this.filteredTeamTree.setLayoutData(localFormData1);
    Tree localTree = this.filteredTeamTree.getViewer().getTree();
    localTree.setFont(SWTResourceManager.getFont("Segoe UI", 8, 0));
    this.filteredTeamTree.getFilterControl().setFont(SWTResourceManager.getFont("Segoe UI", 8, 0));
    this.filteredTeamTree.setInitialText(this.registry.getString("TaskAssignmentDialog.ProjectTeamTreeViewer.InitialText"));
    Group localGroup1 = new Group(this.teamGroup, 0);
    FormData localFormData2 = new FormData();
    localFormData2.right = new FormAttachment(0, 463);
    localFormData2.top = new FormAttachment(0, -1);
    localFormData2.left = new FormAttachment(0, 8);
    localGroup1.setLayoutData(localFormData2);
    localGroup1.setLayout(new FormLayout());
    this.comboProjects = new Combo(localGroup1, 12);
    this.comboProjects.setToolTipText(this.registry.getString("TaskAssignmentDialog.ProjectTeam.Combo.ToolTip.TEXT"));
    this.comboProjects.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    FormData localFormData3 = new FormData();
    localFormData3.right = new FormAttachment(0, 443);
    localFormData3.top = new FormAttachment(0, -6);
    localFormData3.left = new FormAttachment(0, 90);
    this.comboProjects.setLayoutData(localFormData3);
    this.comboProjects.addSelectionListener(this);
    Label localLabel = new Label(localGroup1, 0);
    localLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    FormData localFormData4 = new FormData();
    localFormData4.bottom = new FormAttachment(0, 22);
    localFormData4.right = new FormAttachment(0, 84);
    localFormData4.top = new FormAttachment(0, -3);
    localFormData4.left = new FormAttachment(0, 8);
    localLabel.setLayoutData(localFormData4);
    localLabel.setText(this.registry.getString("TaskAssignmentDialog.ProjectTeam.Label.TITLE"));
    this.filteredTeamTree.addFocusListener(this);
    this.teamTreeViewer = this.filteredTeamTree.getViewer();
    this.teamTreeViewer.setAutoExpandLevel(2);
    this.groupTeamResourcePool = new Group(this.teamGroup, 8388608);
    FormData localFormData5 = new FormData();
    localFormData5.bottom = new FormAttachment(0, 466);
    localFormData5.right = new FormAttachment(0, 463);
    localFormData5.top = new FormAttachment(0, 347);
    localFormData5.left = new FormAttachment(0, 8);
    this.groupTeamResourcePool.setLayoutData(localFormData5);
    this.groupTeamResourcePool.setLayout(new FormLayout());
    this.groupTeamResourcePool.setText(this.registry.getString("TaskAssignmentDialog.ResourcePool.TITLE"));
    this.groupTeamResourcePool.setFont(SWTResourceManager.getFont("Consolas", 9, 1));
    Group localGroup2 = new Group(this.groupTeamResourcePool, 0);
    FormData localFormData6 = new FormData();
    localFormData6.right = new FormAttachment(0, 443);
    localFormData6.top = new FormAttachment(0, 1);
    localFormData6.left = new FormAttachment(0, 8);
    localGroup2.setLayoutData(localFormData6);
    localGroup2.setFont(SWTResourceManager.getFont("Consolas", 10, 1));
    localGroup2.setLayout(new FormLayout());
    this.buttonTeamAnyMember = new Button(localGroup2, 8388624);
    FormData localFormData7 = new FormData();
    localFormData7.top = new FormAttachment(0, -2);
    localFormData7.left = new FormAttachment(0, 8);
    this.buttonTeamAnyMember.setLayoutData(localFormData7);
    this.buttonTeamAnyMember.setText(this.registry.getString("TaskAssignmentDialog.ResourcePool.AnyMember.TITLE"));
    this.buttonTeamAnyMember.setSelection(true);
    this.buttonTeamAnyMember.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    this.buttonTeamAllMember = new Button(localGroup2, 8388624);
    FormData localFormData8 = new FormData();
    localFormData8.left = new FormAttachment(this.buttonTeamAnyMember, 114);
    localFormData8.top = new FormAttachment(0, -2);
    this.buttonTeamAllMember.setLayoutData(localFormData8);
    this.buttonTeamAllMember.setText(this.registry.getString("TaskAssignmentDialog.ResourcePool.AllMember.TITLE"));
    this.buttonTeamAllMember.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    Group localGroup3 = new Group(this.groupTeamResourcePool, 0);
    FormData localFormData9 = new FormData();
    localFormData9.right = new FormAttachment(0, 443);
    localFormData9.top = new FormAttachment(0, 48);
    localFormData9.left = new FormAttachment(0, 8);
    localGroup3.setLayoutData(localFormData9);
    localGroup3.setLayout(new FormLayout());
    this.buttonTeamSpecificGroup = new Button(localGroup3, 16);
    FormData localFormData10 = new FormData();
    localFormData10.top = new FormAttachment(0, -2);
    localFormData10.left = new FormAttachment(0, 8);
    this.buttonTeamSpecificGroup.setLayoutData(localFormData10);
    this.buttonTeamSpecificGroup.setText(this.registry.getString("TaskAssignmentDialog.ResourcePool.SpecificGroup.TITLE"));
    this.buttonTeamSpecificGroup.setSelection(true);
    this.buttonTeamSpecificGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    this.buttonTeamAnyGroup = new Button(localGroup3, 8388624);
    FormData localFormData11 = new FormData();
    localFormData11.left = new FormAttachment(this.buttonTeamSpecificGroup, 103);
    localFormData11.top = new FormAttachment(0, -2);
    this.buttonTeamAnyGroup.setLayoutData(localFormData11);
    this.buttonTeamAnyGroup.setText(this.registry.getString("TaskAssignmentDialog.ResourcePool.AnyGroup.TITLE"));
    this.buttonTeamAnyGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    TabItem localTabItem = new TabItem(this.tabFolder, 0);
    localTabItem.setToolTipText(this.registry.getString("TaskAssignmentDialog.ProjectTeam.ToolTip.TEXT"));
    localTabItem.setText(this.registry.getString("TaskAssignmentDialog.ProjectTeam.Tab.TITLE"));
    localTabItem.setControl(this.teamGroup);
    this.buttonTeamPlaceHolder = new Button(this.teamGroup, 8388640);
    FormData localFormData12 = new FormData();
    localFormData12.top = new FormAttachment(0, 321);
    localFormData12.left = new FormAttachment(0, 8);
    this.buttonTeamPlaceHolder.setLayoutData(localFormData12);
    this.buttonTeamPlaceHolder.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    this.buttonTeamPlaceHolder.setText(this.registry.getString("TaskAssignmentDialog.PlaceHolder.TITLE"));
    this.buttonTeamPlaceHolder.addSelectionListener(this);
  }

  public void createOrgTeamGroup()
  {
	System.out.println("createOrgTeamGroup");
    this.orgResourceGroup = new Group(this.tabFolder, 0);
    this.orgResourceGroup.setBounds(445, 100, 514, 500);
    PatternFilter localPatternFilter = new PatternFilter();
    this.orgResourceGroup.setLayout(new FormLayout());
    this.filteredOrgTree = new SMOrganizationTree(this.orgResourceGroup, 68098, localPatternFilter, true, this.tcSession);
    FormData localFormData1 = new FormData();
    localFormData1.bottom = new FormAttachment(0, 305);
    localFormData1.right = new FormAttachment(0, 463);
    localFormData1.top = new FormAttachment(0, 65);
    localFormData1.left = new FormAttachment(0, 8);
    this.filteredOrgTree.setLayoutData(localFormData1);
    Tree localTree = this.filteredOrgTree.getViewer().getTree();
    localTree.setFont(SWTResourceManager.getFont("Segoe UI", 8, 0));
    this.filteredOrgTree.getFilterControl().setFont(SWTResourceManager.getFont("Segoe UI", 8, 0));
    this.filteredOrgTree.setInitialText(this.registry.getString("TaskAssignmentDialog.OrganizationTreeViewer.InitialText"));
    this.filteredOrgTree.addFocusListener(this);
    this.orgTreeViewer = this.filteredOrgTree.getViewer();
    this.orgTreeViewer.setUseHashlookup(true);
    this.orgTreeViewer.setAutoExpandLevel(2);
    Group localGroup1 = new Group(this.orgResourceGroup, 0);
    FormData localFormData2 = new FormData();
    localGroup1.setLayoutData(localFormData2);
    localGroup1.setBounds(11, 14, 455, 100);
    localGroup1.setLayout(null);
    this.orgSearchText = new Text(localGroup1, 2176);
    this.orgSearchText.setBounds(10, 25, 251, 21);
    this.orgSearchText.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    this.orgSearchText.setText(this.registry.getString("TaskAssignmentDialog.OrganizationTreeViewer.SearchInitialText"));
    this.orgSearchText.addFocusListener(this);
    this.btnOrganizationSearch = new Button(localGroup1, 0);
    this.btnOrganizationSearch.setBounds(267, 23, 75, 25);
    this.btnOrganizationSearch.setText(this.registry.getString("TaskAssignmentDialog.Assign.Tab.TITLE"));
    this.btnOrganizationSearch.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        String str = TaskAssignmentDialog.this.orgSearchText.getText();
        TaskAssignmentDialog.this.filteredOrgTree.searchOrganization(str);
      }
    });
    getShlTaskAssignment().setDefaultButton(this.btnOrganizationSearch);
    this.btnOrganizationSearchClr = new Button(localGroup1, 0);
    Registry localRegistry = Registry.getRegistry("com.teamcenter.rac.schedule.common.tree.tree");
    this.btnOrganizationSearchClr.setText(localRegistry.getString("FilterDialog.Clear"));
    this.btnOrganizationSearchClr.setBounds(348, 23, 75, 25);
    this.btnOrganizationSearchClr.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        TaskAssignmentDialog.this.orgSearchText.setText(TaskAssignmentDialog.this.registry.getString("TaskAssignmentDialog.OrganizationTreeViewer.SearchInitialText"));
        TaskAssignmentDialog.this.orgTreeContentProvider.restoreOrganizationRoot(TaskAssignmentDialog.this.orgTreeViewer);
      }
    });
    this.btnPlaceHolderAssignment = new Button(this.orgResourceGroup, 8388640);
    localFormData2.left = new FormAttachment(this.btnPlaceHolderAssignment, 0, 16384);
    FormData localFormData3 = new FormData();
    localFormData3.top = new FormAttachment(0, 311);
    localFormData3.left = new FormAttachment(0, 8);
    this.btnPlaceHolderAssignment.setLayoutData(localFormData3);
    this.btnPlaceHolderAssignment.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    this.btnPlaceHolderAssignment.setText(this.registry.getString("TaskAssignmentDialog.PlaceHolder.TITLE"));
    this.btnPlaceHolderAssignment.addSelectionListener(this);
    this.grpResourcePoolOptions = new Group(this.orgResourceGroup, 0);
    localFormData2.right = new FormAttachment(0, 460);
    FormData localFormData4 = new FormData();
    localFormData4.bottom = new FormAttachment(0, 466);
    localFormData4.right = new FormAttachment(0, 463);
    localFormData4.top = new FormAttachment(0, 344);
    localFormData4.left = new FormAttachment(0, 8);
    this.grpResourcePoolOptions.setLayoutData(localFormData4);
    this.grpResourcePoolOptions.setText(this.registry.getString("TaskAssignmentDialog.ResourcePool.TITLE"));
    this.grpResourcePoolOptions.setFont(SWTResourceManager.getFont("Consolas", 9, 1));
    this.grpResourcePoolOptions.setLayout(new FormLayout());
    Group localGroup2 = new Group(this.grpResourcePoolOptions, 0);
    FormData localFormData5 = new FormData();
    localFormData5.right = new FormAttachment(0, 437);
    localFormData5.top = new FormAttachment(0);
    localFormData5.left = new FormAttachment(0, 8);
    localGroup2.setLayoutData(localFormData5);
    localGroup2.setFont(new Font(getParent().getDisplay(), "Consolas", 10, 1));
    Group localGroup3 = new Group(this.grpResourcePoolOptions, 0);
    FormData localFormData6 = new FormData();
    localFormData6.bottom = new FormAttachment(0, 82);
    localFormData6.right = new FormAttachment(0, 437);
    localFormData6.top = new FormAttachment(0, 42);
    localFormData6.left = new FormAttachment(0, 8);
    localGroup3.setLayoutData(localFormData6);
    localGroup3.setLayout(new FormLayout());
    localGroup2.setLayout(new FormLayout());
    this.btnAnyMember = new Button(localGroup2, 8388624);
    this.btnAnyMember.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    FormData localFormData7 = new FormData();
    localFormData7.top = new FormAttachment(0, -2);
    localFormData7.left = new FormAttachment(0, 8);
    this.btnAnyMember.setLayoutData(localFormData7);
    this.btnAnyMember.setSelection(true);
    this.btnAnyMember.setText(this.registry.getString("TaskAssignmentDialog.ResourcePool.AnyMember.TITLE"));
    this.btnAllMembers = new Button(localGroup2, 8388624);
    this.btnAllMembers.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    FormData localFormData8 = new FormData();
    localFormData8.left = new FormAttachment(this.btnAnyMember, 115);
    localFormData8.top = new FormAttachment(0, -2);
    this.btnAllMembers.setLayoutData(localFormData8);
    this.btnAllMembers.setText(this.registry.getString("TaskAssignmentDialog.ResourcePool.AllMember.TITLE"));
    this.btnSpecificGroup = new Button(localGroup3, 16);
    this.btnSpecificGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    FormData localFormData9 = new FormData();
    localFormData9.top = new FormAttachment(0, -2);
    localFormData9.left = new FormAttachment(0, 8);
    this.btnSpecificGroup.setLayoutData(localFormData9);
    this.btnSpecificGroup.setSelection(true);
    this.btnSpecificGroup.setText(this.registry.getString("TaskAssignmentDialog.ResourcePool.SpecificGroup.TITLE"));
    this.btnAnyGroup = new Button(localGroup3, 8388624);
    this.btnAnyGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    FormData localFormData10 = new FormData();
    localFormData10.left = new FormAttachment(this.btnSpecificGroup, 103);
    localFormData10.top = new FormAttachment(0, -2);
    this.btnAnyGroup.setLayoutData(localFormData10);
    this.btnAnyGroup.setText(this.registry.getString("TaskAssignmentDialog.ResourcePool.AnyGroup.TITLE"));
    TabItem localTabItem = new TabItem(this.tabFolder, 0);
    localTabItem.setToolTipText(this.registry.getString("TaskAssignmentDialog.Organization.ToolTip.TEXT"));
    localTabItem.setText(this.registry.getString("TaskAssignmentDialog.Organization.Tab.TITLE"));
    localTabItem.setControl(this.orgResourceGroup);
  }

  protected List<TaskQualificationModel> getQualificationsOfSelectedTask()
  {
	System.out.println("getQualificationsOfSelectedTask");
	List<TaskQualificationModel> localObject = new ArrayList();
    InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = Activator.getDefault().getSelectionMediatorService().getTargetComponents();
    if ((arrayOfInterfaceAIFComponent != null) && (arrayOfInterfaceAIFComponent.length == 1))
    {
      TCComponent localTCComponent = (TCComponent)arrayOfInterfaceAIFComponent[0];
      if ((localTCComponent instanceof TCComponentScheduleTask))
      {
        ScheduleTaskTreeModel localScheduleTaskTreeModel = new ScheduleTaskTreeModel((TCComponentScheduleTask)localTCComponent, false, this.tcSession);
        localObject = localScheduleTaskTreeModel.getQualifications();
      }
    }
    return localObject;
  }

  protected void createQualificationsGroup()
  {
	System.out.println("createQualificationsGroup");
    Group localGroup1 = new Group(this.tabFolder, 0);
    localGroup1.setLayout(new FormLayout());
    Group localGroup2 = new Group(localGroup1, 0);
    FormData localFormData1 = new FormData();
    localFormData1.top = new FormAttachment(0, 8);
    localFormData1.left = new FormAttachment(0, 8);
    localGroup2.setLayoutData(localFormData1);
    localGroup2.setText(this.registry.getString("TaskAssignmentDialog.QualTab.ExtQual.TITLE"));
    this.assignQualificationTableViewer = new TableViewer(localGroup2, 68354);
    createColumns(this.assignQualificationTableViewer);
    this.assignQualificationTableViewer.setContentProvider(new ArrayContentProvider());
    this.assignQualificationTableViewer.setLabelProvider(new TableLabelProvider());
    final Table localTable = this.assignQualificationTableViewer.getTable();
    localTable.setHeaderVisible(true);
    localTable.setLinesVisible(true);
    localTable.setBounds(4, 22, 445, 150);
    List localList = getQualificationsOfSelectedTask();
    if ((localList != null) && (!localList.isEmpty()))
      this.assignQualificationTableViewer.setInput(localList);
    this.removeQualBtn = new Button(localGroup2, 0);
    FormData localFormData2 = new FormData();
    this.removeQualBtn.setLayoutData(localFormData2);
    this.removeQualBtn.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        TaskAssignmentDialog.this.removeQualificationObject();
      }
    });
    localTable.addSelectionListener(new SelectionListener()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        doHandleSelection();
      }

      public void widgetDefaultSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        doHandleSelection();
      }

      private void doHandleSelection()
      {
        TaskAssignmentDialog.this.removeQualBtn.setEnabled(localTable.getSelectionIndex() >= 0);
      }
    });
    this.removeQualBtn.setBounds(10, 197, 96, 31);
    this.removeQualBtn.setText(this.registry.getString("TaskAssignmentDialog.QualTab.RemoveBtn.TEXT"));
    Group localGroup3 = new Group(localGroup1, 0);
    FormData localFormData3 = new FormData();
    localFormData3.top = new FormAttachment(localGroup2, 26);
    localFormData3.left = new FormAttachment(0, 8);
    localGroup3.setLayoutData(localFormData3);
    localGroup3.setText(this.registry.getString("TaskAssignmentDialog.QualTab.AssignBtn.TITLE"));
    localGroup3.setLayout(new FormLayout());
    this.qualificationChooser = new QualificationChooser(localGroup3, 0);
    this.assignQualBtn = new Button(localGroup3, 8);
    FormData localFormData4 = new FormData();
    localFormData4.top = new FormAttachment(0, 110);
    localFormData4.left = new FormAttachment(0, 10);
    localFormData4.width = 96;
    localFormData4.height = 30;
    this.assignQualBtn.setLayoutData(localFormData4);
    this.assignQualBtn.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        TaskAssignmentDialog.this.assignQualificationObject();
      }
    });
    this.assignQualBtn.setText(this.registry.getString("TaskAssignmentDialog.QualTab.AssignBtn.TEXT"));
    this.qualificationChooser.addSelectionListener(new SelectionListener()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        handleSelectionChange();
      }

      public void widgetDefaultSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        handleSelectionChange();
      }

      private void handleSelectionChange()
      {
        TaskAssignmentDialog.this.validateAssignQualBtn();
      }
    });
    enableQualificationButtons(false);
    TabItem localTabItem = new TabItem(this.tabFolder, 0);
    localTabItem.setToolTipText(this.registry.getString("TaskAssignmentDialog.Qualification.ToolTip.TEXT"));
    localTabItem.setText(this.registry.getString("TaskAssignmentDialog.Qualification.Tab.TITLE"));
    localTabItem.setControl(localGroup1);
  }

  protected void createSearchGroup()
  {
	System.out.println("createSearchGroup");
    TCPreferenceService localTCPreferenceService = this.tcSession.getPreferenceService();
    Boolean localBoolean = localTCPreferenceService.getLogicalValue("SM_ENFORCE_ASSIGNMENT_CRITERIA");
    if (localBoolean == null)
      localBoolean = Boolean.valueOf(false);
    Group localGroup1 = new Group(this.tabFolder, 768);
    localGroup1.setLayout(new GridLayout());
    Composite localComposite1 = new Composite(localGroup1, 4);
    GridLayout localGridLayout1 = new GridLayout(1, false);
    localGridLayout1.marginWidth = 0;
    localGridLayout1.marginHeight = 0;
    localGridLayout1.verticalSpacing = 5;
    localGridLayout1.horizontalSpacing = 5;
    localComposite1.setLayout(localGridLayout1);
    GridData localGridData1 = new GridData(768);
    localComposite1.setLayoutData(localGridData1);
    Group localGroup2 = new Group(localComposite1, 32);
    localGroup2.setText(this.registry.getString("TaskAssignmentDialog.Filtering.Search.TITLE"));
    localGroup2.setLayout(new GridLayout(1, false));
    localGroup2.setLayoutData(new GridData(4, 4, true, false));
    Composite localComposite2 = new Composite(localGroup2, 0);
    GridLayout localGridLayout2 = new GridLayout(2, true);
    localGridLayout2.marginWidth = 0;
    localGridLayout2.marginHeight = 0;
    localGridLayout2.verticalSpacing = 10;
    localGridLayout2.horizontalSpacing = 10;
    localComposite2.setLayout(localGridLayout2);
    GridData localGridData2 = new GridData(4, 4, false, false);
    localComposite2.setLayoutData(localGridData2);
    int i = 210;
    int j = 50;
    int k = 30;
    String[] arrayOfString1 = { "Discipline" };
    String[] arrayOfString2 = { this.registry.getString("TaskAssignmentDialog.Filtering.DiscColumn.TEXT") };
    this.filteringTableviewViewer_dis = createAllTableViewer(localComposite2, arrayOfString1, arrayOfString2, i, j, k);
    this.filteringTableviewViewer_dis.setLabelProvider(new TCComponentLabelProvider());
    this.filteringTableviewViewer_dis.addCheckStateListener(new FilteringCheckStateListener(localBoolean.booleanValue()));
    String[] arrayOfString3 = { "Group/Role" };
    String[] arrayOfString4 = { this.registry.getString("TaskAssignmentDialog.Filtering.GrpRoleColumn.TEXT") };
    this.filteringTableviewViewer_grprole = createAllTableViewer(localComposite2, arrayOfString3, arrayOfString4, i, j, k);
    this.filteringTableviewViewer_grprole.setLabelProvider(new TCComponentLabelProvider());
    this.filteringTableviewViewer_grprole.addCheckStateListener(new FilteringCheckStateListener(localBoolean.booleanValue()));
    Composite localComposite3 = new Composite(localGroup2, 0);
    GridLayout localGridLayout3 = new GridLayout(1, true);
    localGridLayout3.marginWidth = 0;
    localGridLayout3.marginHeight = 0;
    localGridLayout3.verticalSpacing = 10;
    localGridLayout3.horizontalSpacing = 10;
    localComposite3.setLayout(localGridLayout3);
    GridData localGridData3 = new GridData(4, 4, true, false);
    localComposite2.setLayoutData(localGridData3);
    i = 200;
    j = 430;
    k = 80;
    String[] arrayOfString5 = { "secondary_object", "fnd0LevelReqd" };
    String[] arrayOfString6 = { this.registry.getString("TaskAssignmentDialog.QualTab.QualificationColumn.TEXT"), this.registry.getString("TaskAssignmentDialog.QualTab.LevelColumn.TEXT") };
    this.filteringTableviewViewer_qual = createAllTableViewer(localComposite3, arrayOfString5, arrayOfString6, i, j, k);
    this.filteringTableviewViewer_qual.addCheckStateListener(new FilteringCheckStateListener(localBoolean.booleanValue()));
    setQualificationsInSearchTab();
    Composite localComposite4 = new Composite(localGroup2, 0);
    GridLayout localGridLayout4 = new GridLayout(2, true);
    localGridLayout4.marginWidth = 0;
    localGridLayout4.marginHeight = 0;
    localGridLayout4.verticalSpacing = 10;
    localGridLayout4.horizontalSpacing = 10;
    localComposite4.setLayout(localGridLayout4);
    GridData localGridData4 = new GridData(4, 4, true, true);
    localComposite4.setLayoutData(localGridData4);
    Button localButton1 = new Button(localComposite4, 8);
    localButton1.setText(this.registry.getString("TaskAssignmentDialog.Filtering.FindRes.TEXT"));
    localButton1.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        TaskAssignmentDialog.this.filteringResults_viewer.getTable().removeAll();
        WidgetState.addState(TaskAssignmentDialog.this.btnAdd, false);
        WidgetState.showCorrectCascadedStates();
        ScheduleTaskTreeModel localScheduleTaskTreeModel = null;
        ISelection localISelection = TaskAssignmentDialog.this.getScheduleTaskTreeViewer().getSelection();
        if ((localISelection instanceof IStructuredSelection))
        {
          IStructuredSelection localObject1 = (IStructuredSelection)localISelection;
          List localObject2 = ((IStructuredSelection)localObject1).toList();
          Iterator localIterator = localObject2.iterator();
          while (localIterator.hasNext())
          {
        	ResourceTreeModel localObject3 = (ResourceTreeModel)localIterator.next();
            if ((localObject3 instanceof ScheduleTaskTreeModel))
              localScheduleTaskTreeModel = (ScheduleTaskTreeModel)localObject3;
            if (((localObject3 instanceof AssignmentTreeModel)) || ((localObject3 instanceof PlaceHolderAssignmentTreeModel)))
              localScheduleTaskTreeModel = (ScheduleTaskTreeModel)((ResourceTreeModel)localObject3).getParent();
          }
        }
        Object localObject1 = new ScheduleManagement.FilterCriteria();
        Object[] localObject2 = TaskAssignmentDialog.this.filteringTableviewViewer_dis.getCheckedElements();
        Object[] localObject3 = new TCComponentDiscipline[localObject2.length];
        for (int i = 0; i < localObject2.length; i++)
        	localObject3[i] = ((TCComponentDiscipline)localObject2[i]);
        ((ScheduleManagement.FilterCriteria)localObject1).disciplines = ((TCComponentDiscipline[])localObject3);
        Object[] arrayOfObject1 = TaskAssignmentDialog.this.filteringTableviewViewer_grprole.getCheckedElements();
        TCComponentResourcePool[] arrayOfTCComponentResourcePool = new TCComponentResourcePool[arrayOfObject1.length];
        for (int j = 0; j < arrayOfObject1.length; j++)
          arrayOfTCComponentResourcePool[j] = ((TCComponentResourcePool)arrayOfObject1[j]);
        ((ScheduleManagement.FilterCriteria)localObject1).grouprole = arrayOfTCComponentResourcePool;
        Object[] arrayOfObject2 = TaskAssignmentDialog.this.filteringTableviewViewer_qual.getCheckedElements();
        ArrayList localArrayList1 = new ArrayList();
        for (int k = 0; k < arrayOfObject2.length; k++)
        {
          TaskQualificationModel localObject4 = (TaskQualificationModel)arrayOfObject2[k];
          QualificationInfo localObject5 = new ScheduleManagement.QualificationInfo();
          ((ScheduleManagement.QualificationInfo)localObject5).qualification = ((TCComponentFnd0Qualification)((TaskQualificationModel)localObject4).getQualificationObject());
          ((ScheduleManagement.QualificationInfo)localObject5).level = ((TaskQualificationModel)localObject4).getQualLevel();
          ((ScheduleManagement.QualificationInfo)localObject5).isfindAlternates = TaskAssignmentDialog.this.alternateButton.getSelection();
          localArrayList1.add(localObject5);
        }
        ScheduleManagement.QualificationInfo[] arrayOfQualificationInfo = (ScheduleManagement.QualificationInfo[])localArrayList1.toArray(new ScheduleManagement.QualificationInfo[localArrayList1.size()]);
        ((ScheduleManagement.FilterCriteria)localObject1).qualifications = arrayOfQualificationInfo;
        Object localObject4 = ScheduleManagementService.getService(TaskAssignmentDialog.this.tcSession);
        Object localObject5 = ((ScheduleManagement)localObject4).filterUsers((ScheduleManagement.FilterCriteria)localObject1);
        ArrayList localArrayList2 = new ArrayList();
        FilteredInternalUser[] arrayOfFilteredInternalUser = null;
        //Object localObject6;
        if ((localObject5 != null) && (((ScheduleManagement.FilteredUsersInfo)localObject5).filtereduserinfo != null) && (((ScheduleManagement.FilteredUsersInfo)localObject5).filtereduserinfo.length > 0))
        {
          arrayOfFilteredInternalUser = new FilteredInternalUser[((ScheduleManagement.FilteredUsersInfo)localObject5).filtereduserinfo.length];
          int m = 0;
          for (ScheduleManagement.FilteredUser localObje6 : ((ScheduleManagement.FilteredUsersInfo)localObject5).filtereduserinfo)
          {
            arrayOfFilteredInternalUser[m] = new FilteredInternalUser();
            arrayOfFilteredInternalUser[m].user = ((ScheduleManagement.FilteredUser)localObje6);
            arrayOfFilteredInternalUser[m].setCheckedUser(false);
            arrayOfFilteredInternalUser[m].setSelectedTask(localScheduleTaskTreeModel != null ? localScheduleTaskTreeModel.getUnderlyingObject() : null);
            if (((ScheduleManagement.FilteredUser)localObje6).qualificationRel != null)
              localArrayList2.add(((ScheduleManagement.FilteredUser)localObje6).qualificationRel);
            m++;
          }
        }
        try
        {
          if ((localArrayList2 != null) && (!localArrayList2.isEmpty()) && (localArrayList2.size() > 0))
            TCComponentType.refreshThese((TCComponent[])localArrayList2.toArray(new TCComponentTcRelation[localArrayList2.size()]));
        }
        catch (TCException localTCException)
        {
          TaskAssignmentDialog.logger.error("TaskAssignmentDialog::refreshQualRelation", localTCException);
        }
        if ((localObject5 != null) && (((ScheduleManagement.FilteredUsersInfo)localObject5).filtereduserinfo != null) && (((ScheduleManagement.FilteredUsersInfo)localObject5).filtereduserinfo.length > 0))
        {
          if (arrayOfFilteredInternalUser != null)
          {
            HashMap localHashMap = new HashMap();
            Object localObject8;
            for (FilteredInternalUser localObject6 : arrayOfFilteredInternalUser)
              if (localHashMap.containsKey(((FilteredInternalUser)localObject6).user.user))
              {
                ((FilteredUserGroup)localHashMap.get(((FilteredInternalUser)localObject6).user.user)).add((FilteredInternalUser)localObject6);
              }
              else
              {
                localObject8 = new FilteredUserGroup();
                ((FilteredUserGroup)localObject8).add((FilteredInternalUser)localObject6);
                localHashMap.put(((FilteredInternalUser)localObject6).user.user, localObject8);
              }
            ArrayList localObject6 = new ArrayList(localHashMap.values());
            Collections.sort((List)localObject6);
            Collections.reverse((List)localObject6);
            ArrayList localArrayList3 = new ArrayList();
            Iterator itr = ((List)localObject6).iterator();
            while (itr.hasNext())
            {
              FilteredUserGroup localFilteredUserGroup = (FilteredUserGroup)itr.next();
              localObject8 = localFilteredUserGroup.getFilteredUsers();
              localArrayList3.addAll((Collection)localObject8);
            }
            arrayOfFilteredInternalUser = (FilteredInternalUser[])localArrayList3.toArray(new FilteredInternalUser[0]);
          }
          TaskAssignmentDialog.this.filteringResults_viewer.setInput(arrayOfFilteredInternalUser);
        }
      }
    });
    this.alternateButton = new Button(localComposite4, 32);
    this.alternateButton.setText(this.registry.getString("TaskAssignmentDialog.Filtering.Alternates.TEXT"));
    Group localGroup3 = new Group(localComposite1, 0);
    localGroup3.setText(this.registry.getString("TaskAssignmentDialog.Filtering.Res.TEXT"));
    localGroup3.setLayout(new GridLayout(1, true));
    GridData localGridData5 = new GridData();
    localGridData5.verticalAlignment = 4;
    localGroup2.setLayout(new GridLayout(1, true));
    String[] arrayOfString7 = { "User", "Qualification", "Level", "Expiry Date", "Discipline", "Group/Role", "Resource Graph" };
    String[] arrayOfString8 = { this.registry.getString("TaskAssignmentDialog.Filtering.Res.UserClm.TEXT"), this.registry.getString("TaskAssignmentDialog.Filtering.Res.QualificationClm.TEXT"), this.registry.getString("TaskAssignmentDialog.Filtering.Res.LevelClm.TEXT"), this.registry.getString("TaskAssignmentDialog.Filtering.Res.ExpiryClm.TEXT"), this.registry.getString("TaskAssignmentDialog.Filtering.Res.DisciplineClm.TEXT"), this.registry.getString("TaskAssignmentDialog.Filtering.Res.GrpRoleColumnClm.TEXT"), this.registry.getString("TaskAssignmentDialog.Filtering.Res.ResGraphClm.TEXT") };
    i = 100;
    j = 430;
    k = 170;
    this.filteringResults_viewer = createResultsTableViewer(localGroup3, arrayOfString7, arrayOfString8, i, j, k);
    this.filteringResults_viewer.setLabelProvider(new FilterTableLabelProvider(this.filteringResults_viewer, this.tcSession, this));
    this.filteringResults_viewer.setContentProvider(new FilterTableContentProvider());
    Button localButton2 = new Button(localGroup3, 8);
    localButton2.setText(this.registry.getString("TaskAssignmentDialog.Filtering.ResGraphBtn.TEXT"));
    localButton2.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        ArrayList localArrayList = new ArrayList();
        TableItem[] arrayOfTableItem = TaskAssignmentDialog.this.filteringResults_viewer.getTable().getItems();
        ScheduleTaskTreeModel localScheduleTaskTreeModel;
        Object localObject3;
        for (TableItem localScheduleTaskTreeModeltableItem : arrayOfTableItem)
        {
          localObject3 = (FilteredInternalUser)localScheduleTaskTreeModeltableItem.getData();
          if (((FilteredInternalUser)localObject3).isCheckedUser())
            localArrayList.add(((FilteredInternalUser)localObject3).user);
        }
        if ((localArrayList != null) && (localArrayList.size() > 0))
        {
          localScheduleTaskTreeModel = null;
          ISelection localISelection = TaskAssignmentDialog.this.getScheduleTaskTreeViewer().getSelection();
          if ((localISelection instanceof IStructuredSelection))
          {
            IStructuredSelection localObject1 = (IStructuredSelection)localISelection;
            List lst = ((IStructuredSelection)localObject1).toList();
            Iterator localIterator = lst.iterator();
            while (localIterator.hasNext())
            {
              localObject3 = (ResourceTreeModel)localIterator.next();
              if ((localObject3 instanceof ScheduleTaskTreeModel))
                localScheduleTaskTreeModel = (ScheduleTaskTreeModel)localObject3;
              if (((localObject3 instanceof AssignmentTreeModel)) || ((localObject3 instanceof PlaceHolderAssignmentTreeModel)))
                localScheduleTaskTreeModel = (ScheduleTaskTreeModel)((ResourceTreeModel)localObject3).getParent();
            }
          }
          TCComponentUser[] localObject1 = new TCComponentUser[localArrayList.size()];
          for (int k = 0; k < localArrayList.size(); k++)
          {
            localObject3 = (ScheduleManagement.FilteredUser)localArrayList.get(k);
            localObject1[k] = ((ScheduleManagement.FilteredUser)localObject3).user;
          }
          Shell localShell = TaskAssignmentDialog.this.getShlTaskAssignment();
          if (localScheduleTaskTreeModel != null)
            TaskAssignmentDialog.this.resourceGraphUtil = ResourceGraphUtil.getSingleton((TCComponentUser[])localObject1, localScheduleTaskTreeModel.getUnderlyingObject(), TaskAssignmentDialog.this.getTcSession(), localShell);
          localShell.addFocusListener(new FocusAdapter()
          {
            public void focusGained(FocusEvent paramAnonymous2FocusEvent)
            {
              if ((TaskAssignmentDialog.this.resourceGraphUtil.getGraphDialog() != null) && (TaskAssignmentDialog.this.resourceGraphUtil.getGraphDialog().isShowing()))
              {
                TaskAssignmentDialog.this.resourceGraphUtil.getGraphDialog().requestFocus();
                TaskAssignmentDialog.this.resourceGraphUtil.getGraphDialog().toFront();
              }
            }
          });
          TaskAssignmentDialog.this.resourceGraphUtil.getGraphDialog();
          localObject3 = TaskAssignmentDialog.this.resourceGraphUtil.getGraphCommandQualification((TCComponentUser[])localObject1);
          if (localObject3 != null)
            TaskAssignmentDialog.this.resourceGraphUtil.showUIQ((IGraphCommand)localObject3);
        }
      }
    });
    if (!this.showResourceGraph)
      localButton2.setEnabled(false);
    TabItem localTabItem = new TabItem(this.tabFolder, 0);
    localTabItem.setToolTipText(this.registry.getString("TaskAssignmentDialog.Assign.ToolTip.TEXT"));
    localTabItem.setText(this.registry.getString("TaskAssignmentDialog.Assign.Tab.TITLE"));
    localTabItem.setControl(localGroup1);
  }

  public void setQualificationsInSearchTab()
  {
	System.out.println("setQualificationsInSearchTab");
    TableItem[] arrayOfTableItem1 = this.assignQualificationTableViewer.getTable().getItems();
    ArrayList localArrayList = new ArrayList();
    for (TableItem localTableItem : arrayOfTableItem1)
    {
      TaskQualificationModel localTaskQualificationModel = (TaskQualificationModel)localTableItem.getData();
      localArrayList.add(localTaskQualificationModel);
    }
    this.filteringTableviewViewer_qual.setInput(localArrayList.toArray(new TaskQualificationModel[localArrayList.size()]));
    this.filteringTableviewViewer_qual.setCheckedElements(localArrayList.toArray(new TaskQualificationModel[localArrayList.size()]));
  }

  public TableViewer createResultsTableViewer(Composite paramComposite, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt1, int paramInt2, int paramInt3)
  {
	System.out.println("createResultsTableViewer");
    Table localTable = new Table(paramComposite, 68352);
    localTable.setLinesVisible(true);
    localTable.setHeaderVisible(true);
    TableViewer localTableViewer = new TableViewer(localTable);
    for (int i = 0; i < paramArrayOfString1.length; i++)
    {
      TableColumn localTableColumn = new TableColumn(localTable, 0);
      localTableColumn.setText(paramArrayOfString2[i]);
      localTableColumn.setWidth(paramInt1);
      TableViewerColumn localTableViewerColumn = new TableViewerColumn(localTableViewer, localTableColumn);
      localTableViewerColumn.setLabelProvider(new TCComponentLabelProvider(paramArrayOfString1[i]));
    }
    localTableViewer.setContentProvider(new ArrayContentProvider());
    localTable.getVerticalBar().setVisible(true);
    GridData localGridData = new GridData(1808);
    localGridData.grabExcessVerticalSpace = true;
    localGridData.grabExcessHorizontalSpace = true;
    localGridData.widthHint = paramInt2;
    localGridData.heightHint = paramInt3;
    localTable.setLayoutData(localGridData);
    localTable.setHeaderVisible(true);
    localTable.setVisible(true);
    localTable.setLinesVisible(false);
    localTable.pack();
    return localTableViewer;
  }

  public void enableQualificationButtons(boolean paramBoolean)
  {
    this.assignQualBtn.setEnabled(paramBoolean);
    this.removeQualBtn.setEnabled(paramBoolean);
  }

  private void createColumns(TableViewer paramTableViewer)
  {
	System.out.println("createColumns paramTableViewer");
    String[] arrayOfString = { this.registry.getString("TaskAssignmentDialog.QualTab.QualificationColumn.TEXT"), this.registry.getString("TaskAssignmentDialog.QualTab.LevelColumn.TEXT") };
    int[] arrayOfInt = { 200, 250, 10, 10 };
    for (int i = 0; i < arrayOfString.length; i++)
      createTableViewerColumn(paramTableViewer, arrayOfString[i], arrayOfInt[i]);
  }

  private void createTableViewerColumn(TableViewer paramTableViewer, String paramString, int paramInt)
  {
	System.out.println("createTableViewerColumn");
    TableViewerColumn localTableViewerColumn = new TableViewerColumn(paramTableViewer, 0);
    TableColumn localTableColumn = localTableViewerColumn.getColumn();
    localTableColumn.setText(paramString);
    localTableColumn.setWidth(paramInt);
    localTableColumn.setResizable(false);
    localTableColumn.setResizable(true);
    localTableColumn.setMoveable(true);
  }

  private void validateAssignQualBtn()
  {
    this.assignQualBtn.setEnabled((isSingleTaskInSelection()) && (this.qualificationChooser.getSelectedQualification() != null) && (this.qualificationChooser.getSelectedQualificationLevel() != null));
  }

  private boolean isSingleTaskInSelection()
  {
	System.out.println("isSingleTaskInSelection");
    ISelection localISelection = this.scheduleTaskTreeViewer.getSelection();
    if (!(localISelection instanceof IStructuredSelection))
      return false;
    IStructuredSelection localIStructuredSelection = (IStructuredSelection)localISelection;
    if (localIStructuredSelection.size() > 1)
      return false;
    ResourceTreeModel localResourceTreeModel = (ResourceTreeModel)localIStructuredSelection.getFirstElement();
    return ((localResourceTreeModel instanceof ScheduleTaskTreeModel)) && (this.scheduleRoot != localResourceTreeModel);
  }

  public void assignQualificationObject()
  {
	System.out.println("assignQualificationObject");
    if ((this.qualificationChooser.getSelectedQualification() == null) || (this.qualificationChooser.getSelectedQualificationLevel() == null) || (!isSingleTaskInSelection()))
      return;
    TCPreferenceService localTCPreferenceService = this.tcSession.getPreferenceService();
    String[] arrayOfString = localTCPreferenceService.getStringValues(this.changeLockedRulePref);
    ArrayList localArrayList = new ArrayList();
    if (arrayOfString != null)
      for (String localObject1 : arrayOfString)
        localArrayList.add(localObject1);
    Object localObject1 = getScheduleTaskTreeViewer().getSelection();
    ScheduleTaskTreeModel localScheduleTaskTreeModel = (ScheduleTaskTreeModel)((IStructuredSelection)localObject1).getFirstElement();
    if ((localScheduleTaskTreeModel == null) || (localScheduleTaskTreeModel.getUnderlyingObject() == null))
      return;
    TCComponentFnd0Qualification localTCComponentFnd0Qualification = (TCComponentFnd0Qualification)this.qualificationChooser.getSelectedQualification().getComponent();
    String temp = localTCComponentFnd0Qualification.toString();
    try
    {
      TCComponentSchedule localTCComponentSchedule = getSchedule();
      String localObject3 = localTCComponentSchedule.getStringProperty("fnd0state");
      if (localArrayList.contains(localObject3))
      {
        String localObject4 = this.registry.getString("TaskAssignmentDialog.LockedState.Schedule.AddQualification.TEXT");
        String localObject5 = MessageFormat.format((String)localObject4, new Object[] { temp, localTCComponentSchedule.toString(), localObject3 });
        String localObject6 = this.registry.getString("TaskAssignmentDialog.Qualification.Tab.TITLE");
        Shell localObject7 = getShlTaskAssignment();
        MessageDialog.openError((Shell)localObject7, (String)localObject6, (String)localObject5);
        return;
      }
      TCComponentScheduleTask localObject4 = localScheduleTaskTreeModel.getUnderlyingObject();
      String localObject5 = TaskHelper.getFnd0State((TCComponent)localObject4);
      if (localArrayList.contains(localObject5))
      {
        String localObject6 = localScheduleTaskTreeModel.getUnderlyingObject().toString();
        String localObject7 = this.registry.getString("TaskAssignmentDialog.LockedState.Task.AddQualification.TEXT");
        String str = MessageFormat.format((String)localObject7, new Object[] { temp, localObject6, localObject5 });
        String localObject8 = this.registry.getString("TaskAssignmentDialog.Qualification.Tab.TITLE");
        Shell localObject9 = getShlTaskAssignment();
        MessageDialog.openError((Shell)localObject9, (String)localObject8, str);
        return;
      }
    }
    catch (TCException localTCException)
    {
      Object localObject7;
      String str;
      Object localObject8;
      Object localObject9;
      logger.error("TaskAssignmentDialog::assignQualificationObject", localTCException);
      List localList = localScheduleTaskTreeModel.getAssignments();
      Object localObject3 = localScheduleTaskTreeModel.getUnderlyingObject();
      Object localObject5 = localList.iterator();
      while (((Iterator)localObject5).hasNext())
      {
    	 AssignmentTreeModel  localObject4 = (AssignmentTreeModel)((Iterator)localObject5).next();
        if ((((AssignmentTreeModel)localObject4).getResource() instanceof TCComponentUser))
        {
          String localObject6 = this.registry.getString("TaskAssignmentDialog.UsersAssigned.Task.AddQualification.TEXT");
          localObject7 = MessageFormat.format((String)localObject6, new Object[] { ((TCComponentScheduleTask)localObject3).toString() });
          str = this.registry.getString("TaskAssignmentDialog.Qualification.Tab.TITLE");
          localObject8 = getShlTaskAssignment();
          MessageDialog.openError((Shell)localObject8, str, (String)localObject7);
          return;
        }
      }
      Object localObject4 = localScheduleTaskTreeModel.getQualifications();
      Object localObject6 = ((List)localObject4).iterator();
      while (((Iterator)localObject6).hasNext())
      {
        localObject5 = (TaskQualificationModel)((Iterator)localObject6).next();
        if (((TaskQualificationModel)localObject5).getQualificationObject().equals(localTCComponentFnd0Qualification))
        {
          localObject7 = this.registry.getString("AssignmentAddButtonListener.Qualification.Warning.TEXT");
          str = MessageFormat.format((String)localObject7, new Object[] { temp, ((TCComponentScheduleTask)localObject3).toString() });
          localObject8 = this.registry.getString("AssignmentAddButtonListener.AssignmentExists.Warning.Dialog.TITLE");
          localObject9 = getShlTaskAssignment();
          MessageDialog.openWarning((Shell)localObject9, (String)localObject8, str);
          return;
        }
      }
      localObject5 = this.qualificationChooser.getSelectedQualificationLevel().getDisplayName();
      for (int k = 0; k < this.assignmentCache.getRemovequallist().size(); k++)
      {
    	TaskQualificationModel localObject71 = (TaskQualificationModel)this.assignmentCache.getRemovequallist().get(k);
        if ((((TaskQualificationModel)localObject71).getScheduletask().equals(localObject3)) && (((TaskQualificationModel)localObject71).getQualName().equals(temp)))
        {
          ((TaskQualificationModel)localObject71).setQualLevel((String)localObject5);
          this.assignmentCache.getAssignQualList().add(localObject71);
          this.assignmentCache.getRemovequallist().remove(localObject71);
          this.assignQualificationTableViewer.add(localObject71);
          if (localScheduleTaskTreeModel != null)
          {
            localScheduleTaskTreeModel.addQualifications((TaskQualificationModel)localObject71);
            setQualificationsInSearchTab();
          }
          getScheduleTaskTreeViewer().refresh(true);
          return;
        }
      }
      for (int k = 0; k < this.assignmentCache.getAssignQualList().size(); k++)
      {
        localObject7 = (TaskQualificationModel)this.assignmentCache.getAssignQualList().get(k);
        if ((((TaskQualificationModel)localObject7).getScheduletask().equals(localObject3)) && (((TaskQualificationModel)localObject7).getQualName().equals(temp)))
        {
          str = this.registry.getString("AssignmentAddButtonListener.Qualification.Warning.TEXT");
          localObject8 = MessageFormat.format(str, new Object[] { temp, ((TCComponentScheduleTask)localObject3).toString() });
          localObject9 = this.registry.getString("AssignmentAddButtonListener.AssignmentExists.Warning.Dialog.TITLE");
          Shell localShell = getShlTaskAssignment();
          MessageDialog.openWarning(localShell, (String)localObject9, (String)localObject8);
          return;
        }
      }
      TaskQualificationModel localTaskQualificationModel = new TaskQualificationModel((TCComponentScheduleTask)localObject3, this.tcSession);
      localTaskQualificationModel.setQualName((String)temp);
      localTaskQualificationModel.setQualificationObject(localTCComponentFnd0Qualification);
      localTaskQualificationModel.setQualLevel((String)localObject5);
      this.assignmentCache.getAssignQualList().add(localTaskQualificationModel);
      this.assignQualificationTableViewer.add(localTaskQualificationModel);
      if (localScheduleTaskTreeModel != null)
      {
        localScheduleTaskTreeModel.addQualifications(localTaskQualificationModel);
        setQualificationsInSearchTab();
      }
      getScheduleTaskTreeViewer().refresh(true);
    }
  }

  public void removeQualificationObject()
  {
	System.out.println("removeQualificationObject");
    if (!isSingleTaskInSelection())
      return;
    TCPreferenceService localTCPreferenceService = this.tcSession.getPreferenceService();
    String[] arrayOfString = localTCPreferenceService.getStringValues(this.changeLockedRulePref);
    ArrayList localArrayList = new ArrayList();
    if (arrayOfString != null)
      for (String localObject1 : arrayOfString)
        localArrayList.add(localObject1);
    Object localObject1 = getScheduleTaskTreeViewer().getSelection();
    ScheduleTaskTreeModel localScheduleTaskTreeModel = (ScheduleTaskTreeModel)((IStructuredSelection)localObject1).getFirstElement();
    if (localScheduleTaskTreeModel == null)
      return;
    ISelection localISelection = this.assignQualificationTableViewer.getSelection();
    if (!(localISelection instanceof IStructuredSelection))
      return;
    IStructuredSelection istrt = (IStructuredSelection)localISelection;
    List localList = istrt.toList();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      TaskQualificationModel localTaskQualificationModel = (TaskQualificationModel)localIterator.next();
      try
      {
        TCComponentSchedule localTCComponentSchedule = getSchedule();
        String str1 = localTCComponentSchedule.getStringProperty("fnd0state");
        String str3;
        String str4;
        Object localObject4;
        if (localArrayList.contains(str1))
        {
          String localObject3 = localTaskQualificationModel.getQualificationObject().toDisplayString();
          String str2 = this.registry.getString("TaskAssignmentDialog.LockedState.Schedule.RemoveQualification.TEXT");
          str3 = MessageFormat.format(str2, new Object[] { localObject3, localTCComponentSchedule.toString(), str1 });
          str4 = this.registry.getString("TaskAssignmentDialog.Qualification.Tab.TITLE");
          localObject4 = getShlTaskAssignment();
          MessageDialog.openError((Shell)localObject4, str4, str3);
          return;
        }
        Object localObject3 = localScheduleTaskTreeModel.getUnderlyingObject();
        String str2 = TaskHelper.getFnd0State((TCComponent)localObject3);
        if (localArrayList.contains(str2))
        {
          str3 = localTaskQualificationModel.getQualificationObject().toDisplayString();
          str4 = localScheduleTaskTreeModel.getUnderlyingObject().toString();
          localObject4 = this.registry.getString("TaskAssignmentDialog.LockedState.Task.RemoveQualification.TEXT");
          String str5 = MessageFormat.format((String)localObject4, new Object[] { str3, str4, str2 });
          String str6 = this.registry.getString("TaskAssignmentDialog.Qualification.Tab.TITLE");
          Shell localShell = getShlTaskAssignment();
          MessageDialog.openError(localShell, str6, str5);
          return;
        }
      }
      catch (TCException localTCException)
      {
        logger.error("TaskAssignmentDialog::assignQualificationObject", localTCException);
        if (localTaskQualificationModel.getRelation() != null)
          this.assignmentCache.getRemovequallist().add(localTaskQualificationModel);
        this.assignmentCache.getAssignQualList().remove(localTaskQualificationModel);
        this.assignQualificationTableViewer.remove(localTaskQualificationModel);
        if (localScheduleTaskTreeModel != null)
        {
          localScheduleTaskTreeModel.removeQualifications(localTaskQualificationModel);
          setQualificationsInSearchTab();
        }
      }
    }
    getScheduleTaskTreeViewer().refresh(true);
    this.removeQualBtn.setEnabled(false);
  }

  private CheckboxTableViewer createAllTableViewer(Composite paramComposite, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt1, int paramInt2, int paramInt3)
  {
	System.out.println("createAllTableViewer");
    Table localTable = new Table(paramComposite, 68384);
    localTable.setLinesVisible(true);
    localTable.setHeaderVisible(true);
    CheckboxTableViewer localCheckboxTableViewer = new CheckboxTableViewer(localTable);
    for (int i = 0; i < paramArrayOfString1.length; i++)
    {
      TableColumn localTableColumn = new TableColumn(localTable, 0);
      localTableColumn.setText(paramArrayOfString2[i]);
      localTableColumn.setWidth(paramInt1);
      TableViewerColumn localTableViewerColumn = new TableViewerColumn(localCheckboxTableViewer, localTableColumn);
      localTableViewerColumn.setLabelProvider(new TCComponentLabelProvider(paramArrayOfString1[i]));
    }
    localCheckboxTableViewer.setContentProvider(new ArrayContentProvider());
    localCheckboxTableViewer.setLabelProvider(new TableLabelProvider());
    localTable.getVerticalBar().setVisible(true);
    GridData localGridData = new GridData(1808);
    localGridData.grabExcessVerticalSpace = true;
    localGridData.grabExcessHorizontalSpace = true;
    localGridData.widthHint = paramInt2;
    localGridData.heightHint = paramInt3;
    localTable.setLayoutData(localGridData);
    localTable.setHeaderVisible(true);
    localTable.setVisible(true);
    localTable.setLinesVisible(false);
    localTable.pack();
    return localCheckboxTableViewer;
  }

  private void createLeftPanel()
  {
	System.out.println("createLeftPanel");
    this.grpAssignAllTasks = new Group(this.shlTaskAssignment, 0);
    this.grpAssignAllTasks.setLayout(null);
    FormData localFormData = new FormData();
    localFormData.bottom = new FormAttachment(0, 679);
    localFormData.right = new FormAttachment(0, 1170);
    localFormData.top = new FormAttachment(0, 10);
    localFormData.left = new FormAttachment(0, 10);
    this.grpAssignAllTasks.setLayoutData(localFormData);
    this.grpAssignAllTasks.setText(this.registry.getString("TaskAssignmentDialog.Group.AssignAllTask"));
    this.grpAssignAllTasks.setFont(SWTResourceManager.getFont("Consolas", 9, 1));
    this.groupLeftPane = new Group(this.grpAssignAllTasks, 0);
    this.groupLeftPane.setBounds(10, 34, 543, 561);
    PatternFilter localPatternFilter = new PatternFilter();
    this.groupLeftPane.setLayout(null);
    this.filteredTaskTree = new SMFilteredTree(this.groupLeftPane, 68098, localPatternFilter, true);
    this.filteredTaskTree.setBounds(10, 25, 525, 515);
    Tree localTree = this.filteredTaskTree.getViewer().getTree();
    localTree.setFont(SWTResourceManager.getFont("Segoe UI", 8, 0));
    this.filteredTaskTree.getFilterControl().setFont(SWTResourceManager.getFont("Segoe UI", 8, 0));
    this.filteredTaskTree.setInitialText(this.registry.getString("TaskAssignmentDialog.ScheduleTaskTreeViewer.InitialText"));
    this.filteredTaskTree.addFocusListener(this);
    this.scheduleTaskTreeViewer = this.filteredTaskTree.getViewer();
    this.scheduleTaskTreeViewer.setAutoExpandLevel(-1);
    this.scheduleTaskTreeViewer.getControl().forceFocus();
    createRemovePanel(this.groupLeftPane);
  }

  protected void createRemovePanel(Group paramGroup)
  {
  }

  protected void createBottomPanel()
  {
	System.out.println("createBottomPanel");
    Group localGroup = new Group(this.grpAssignAllTasks, 0);
    localGroup.setBounds(10, 604, 1134, 55);
    localGroup.setLayout(null);
    this.btnCancel = new Button(localGroup, 0);
    this.btnCancel.setBounds(903, 18, 80, 27);
    this.btnCancel.setToolTipText(this.registry.getString("TaskAssignmentDialog.Cancel.ToolTip.TEXT"));
    this.btnCancel.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    this.btnCancel.setText(this.registry.getString("TaskAssignmentDialog.Cancel.TITLE"));
    this.btnOk = new Button(localGroup, 0);
    this.btnOk.setBounds(775, 18, 80, 27);
    this.btnOk.setToolTipText(this.registry.getString("TaskAssignmentDialog.OK.ToolTip.TEXT"));
    this.btnOk.setFont(SWTResourceManager.getFont("Segoe UI", 9, 0));
    this.btnOk.setText(this.registry.getString("TaskAssignmentDialog.OK.TITLE"));
    this.btnOk.addListener(13, this);
    this.btnCancel.addListener(13, this);
  }

  protected void setReadOnlyMode()
  {
	System.out.println("setReadOnlyMode");
    if (!this.canModify)
    {
      this.btnAdd.setVisible(false);
      this.btnRemove.setVisible(false);
      String str = this.shlTaskAssignment.getShell().getText() + this.registry.getString("TaskAssignmentDialog.Schedule.ReadOnly.TITLE");
      this.shlTaskAssignment.getShell().setText(str);
    }
  }

  protected void createScheduleTaskTree()
  {
	System.out.println("createScheduleTaskTree");
    if (this.scheduleRoot == null)
    {
      this.btnAdd.addListener(13, new AssignmentAddButtonListener(this));
      Tree localTree = this.scheduleTaskTreeViewer.getTree();
      localTree.setHeaderVisible(true);
      localTree.setLinesVisible(true);
      addColumns(localTree);
      AssignmentTreeContentProvider localAssignmentTreeContentProvider = new AssignmentTreeContentProvider(this);
      this.scheduleTaskTreeViewer.setContentProvider(localAssignmentTreeContentProvider);
      this.scheduleTaskTreeViewer.setLabelProvider(new AssignmentTreeLabelProvider(this));
      this.scheduleTaskTreeViewer.addSelectionChangedListener(localAssignmentTreeContentProvider);
      this.scheduleTaskTreeViewer.setInput("Root");
      localAssignmentTreeContentProvider.setInitalSelectedTreeItems();
    }
  }

  protected void addColumns(Tree paramTree)
  {
	System.out.println("addColumns");
    this.tasks = new TreeColumn(paramTree, 0);
    this.tasks.setText(this.registry.getString("TaskAssignmentDialog.ScheduleTaskTreeViewer.ScheduleTaskColumn.TEXT"));
    this.tasks.setResizable(true);
    this.tasks.setWidth(180);
    this.resourceLevelC = new TreeColumn(paramTree, 0);
    this.resourceLevelC.setText(this.registry.getString("TaskAssignmentDialog.ScheduleTaskTreeViewer.ResourceLevelColumn.TEXT"));
    this.resourceLevelC.setResizable(true);
    this.resourceLevelC.setWidth(60);
    this.assignmentNote = new TreeColumn(paramTree, 0);
    this.assignmentNote.setText(this.registry.getString("TaskAssignmentDialog.ScheduleTaskTreeViewer.AssignmentNoteColumn.TEXT"));
    this.assignmentNote.setResizable(true);
    this.assignmentNote.setWidth(60);
    this.discColumn = new TreeColumn(paramTree, 0);
    this.discColumn.setText(this.registry.getString("TaskAssignmentDialog.ScheduleTaskTreeViewer.Discipline.TEXT"));
    this.discColumn.setResizable(true);
    this.discColumn.setWidth(60);
    this.placeholderColumn = new TreeColumn(paramTree, 0);
    this.placeholderColumn.setText(this.registry.getString("TaskAssignmentDialog.ScheduleTaskTreeViewer.PlaceHolder.TEXT"));
    this.placeholderColumn.setResizable(true);
    this.placeholderColumn.setWidth(60);
    this.privilegeUserC = new TreeColumn(paramTree, 0);
    this.privilegeUserC.setText(this.registry.getString("TaskAssignmentDialog.ScheduleTaskTreeViewer.PrivilegeUserColumn.TEXT"));
    this.privilegeUserC.setResizable(true);
    this.privilegeUserC.setWidth(80);
    this.taskQual = new TreeColumn(paramTree, 0);
    this.taskQual.setText(this.registry.getString("TaskAssignmentDialog.ScheduleTaskTreeViewer.TaskQualificationColumn.TEXT"));
    this.taskQual.setResizable(true);
    this.taskQual.setWidth(60);
    this.resourceGraphC = new TreeColumn(paramTree, 0);
    this.resourceGraphC.setText(this.registry.getString("TaskAssignmentDialog.ScheduleTaskTreeViewer.ResourceGraphColumn.TEXT"));
    this.resourceGraphC.setResizable(true);
    this.resourceGraphC.setWidth(60);
  }

  protected void createOrgTree()
  {
	System.out.println("createOrgTree");
    this.selectedViewer = this.orgTreeViewer;
    if (this.orgTreeContentProvider == null)
    {
      this.orgTreeContentProvider = new OrgTreeContentProvider(this.tcSession);
      this.orgTreeViewer.setContentProvider(this.orgTreeContentProvider);
      this.orgTreeViewer.setLabelProvider(new OrgTreeLabelProvider(this.tcSession, this));
      this.orgTreeViewer.setInput("Root");
      this.filteredOrgTree.setOrgTreeContentProvider(this.orgTreeContentProvider);
      this.orgTreeViewer.addSelectionChangedListener(new ISelectionChangedListener()
      {
        public void selectionChanged(SelectionChangedEvent paramAnonymousSelectionChangedEvent)
        {
          TaskAssignmentDialog.this.validateOrgAssignmentControls();
        }
      });
      this.orgTreeViewer.addDoubleClickListener(new IDoubleClickListener()
      {
        public void doubleClick(DoubleClickEvent paramAnonymousDoubleClickEvent)
        {
          TaskAssignmentDialog.this.doAddAssignment();
        }
      });
    }
    validateOrgAssignmentControls();
  }

  protected void createProjectTeamTree()
  {
	System.out.println("createProjectTeamTree");
    this.selectedViewer = this.teamTreeViewer;
    if (this.teamTreeContentProvider == null)
    {
      this.teamTreeContentProvider = new ProjectTeamTreeContentProvider(this.tcSession, this.comboProjects);
      this.teamTreeViewer.setContentProvider(this.teamTreeContentProvider);
      this.teamTreeViewer.setLabelProvider(new ProjectTeamTreeLabelProvider(this.tcSession, this));
      TCComponentSchedule localTCComponentSchedule = getSchedule();
      this.teamTreeContentProvider.populateComboAndSetDefault(localTCComponentSchedule);
      this.teamTreeViewer.setInput("Root");
      this.teamTreeViewer.addSelectionChangedListener(new ISelectionChangedListener()
      {
        public void selectionChanged(SelectionChangedEvent paramAnonymousSelectionChangedEvent)
        {
          TaskAssignmentDialog.this.validateTeamAssignmentControls();
        }
      });
      this.teamTreeViewer.addDoubleClickListener(new IDoubleClickListener()
      {
        public void doubleClick(DoubleClickEvent paramAnonymousDoubleClickEvent)
        {
          TaskAssignmentDialog.this.doAddAssignment();
        }
      });
    }
    validateTeamAssignmentControls();
  }

  protected void createDisciplinesTree()
  {
	System.out.println("createDisciplinesTree");
    this.selectedViewer = this.disciplineTreeViewer;
    if (this.disciplineTreeContentProvider == null)
    {
      //this.disciplineTreeContentProvider = new DisciplineTreeContentProvider(this.tcSession);
      System.out.println("new MyDisciplineTreeContentProvider and member_users size 22is "+member_users.size());
      this.disciplineTreeContentProvider = new MyDisciplineTreeContentProvider(this.tcSession,this.member_users,this.member_displices);
      this.disciplineTreeViewer.setContentProvider(this.disciplineTreeContentProvider);
      this.disciplineTreeViewer.setLabelProvider(new DisciplineTreeLabelProvider(this.tcSession, this));
      this.disciplineTreeViewer.setInput("Root");
      this.disciplineTreeViewer.addSelectionChangedListener(new ISelectionChangedListener()
      {
        public void selectionChanged(SelectionChangedEvent paramAnonymousSelectionChangedEvent)
        {
          TaskAssignmentDialog.this.validateDisciplineAssignmentControls();
        }
      });
      this.disciplineTreeViewer.addDoubleClickListener(new IDoubleClickListener()
      {
        public void doubleClick(DoubleClickEvent paramAnonymousDoubleClickEvent)
        {
          TaskAssignmentDialog.this.doAddAssignment();
        }
      });
      this.disciplineTreeViewer.expandAll();//全部展开
    }
    validateDisciplineAssignmentControls();
  }

  protected void createFilterQualTree()
  {
    this.selectedViewer = this.filteringResults_viewer;
    validateAddButtonForFilterQualTree();
  }

  public void validateAddButtonForFilterQualTree()
  {
	System.out.println("validateAddButtonForFilterQualTree");
    validateAddButton();
    TableItem[] arrayOfTableItem1 = this.filteringResults_viewer.getTable().getItems();
    boolean bool = false;
    for (TableItem localTableItem : arrayOfTableItem1)
    {
      FilteredInternalUser localFilteredInternalUser = (FilteredInternalUser)localTableItem.getData();
      if (localFilteredInternalUser.isCheckedUser())
      {
        bool = true;
        break;
      }
    }
    WidgetState.addState(this.btnAdd, bool);
    WidgetState.showCorrectCascadedStates();
  }

  protected boolean doAddAssignment()
  {
	System.out.println("doAddAssignment");
    boolean bool = false;
    if ((this.btnAdd.isVisible()) && (this.btnAdd.isEnabled()))
    {
      Event localEvent = new Event();
      localEvent.type = 13;
      localEvent.widget = this.btnAdd;
      this.btnAdd.notifyListeners(13, localEvent);
      bool = true;
    }
    return bool;
  }

  public void validateOrgAssignmentControls()
  {
	System.out.println("validateOrgAssignmentControls");
    this.btnAdd.setEnabled(false);
    this.btnPlaceHolderAssignment.setEnabled(false);
    this.isOrgGrpUserInSelection = false;
    this.isOrgGrpRoleInSelection = false;
    cascadeResourcePoolOption(this.btnPlaceHolderAssignment);
    List localList = null;
    ISelection localISelection = this.orgTreeViewer.getSelection();
    if ((localISelection instanceof IStructuredSelection))
    {
      IStructuredSelection localIStructuredSelection = (IStructuredSelection)localISelection;
      localList = localIStructuredSelection.toList();
      if (localList != null)
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          TaskActorInterface localTaskActorInterface = (TaskActorInterface)localIterator.next();
          if (localTaskActorInterface.getType() != null)
          {
            if ((localTaskActorInterface.isRole()) || (localTaskActorInterface.isGroup()))
            {
              this.isOrgGrpRoleInSelection = true;
              WidgetState.addState(this.btnPlaceHolderAssignment, true);
            }
            else
            {
              WidgetState.addState(this.btnPlaceHolderAssignment, false);
            }
            enableAddButton(localTaskActorInterface);
            if (localTaskActorInterface.isUser())
              this.isOrgGrpUserInSelection = true;
          }
          else
          {
            WidgetState.addState(this.btnAdd, false);
            WidgetState.addState(this.btnPlaceHolderAssignment, false);
          }
        }
        if ((this.isOrgGrpUserInSelection) && (this.isOrgGrpRoleInSelection))
          WidgetState.addState(this.btnAdd, false);
      }
    }
    WidgetState.showCorrectCascadedStates();
  }

  public void validateAddButton()
  {
	System.out.println("validateAddButton");
    this.btnAdd.setEnabled(false);
    List localList = null;
    ISelection localISelection = this.orgTreeViewer.getSelection();
    if ((localISelection instanceof IStructuredSelection))
    {
      IStructuredSelection localIStructuredSelection = (IStructuredSelection)localISelection;
      localList = localIStructuredSelection.toList();
      if (localList != null)
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          TaskActorInterface localTaskActorInterface = (TaskActorInterface)localIterator.next();
          if (localTaskActorInterface.getType() != null)
          {
            enableAddButton(localTaskActorInterface);
          }
          else
          {
            WidgetState.addState(this.btnAdd, false);
            WidgetState.addState(this.btnPlaceHolderAssignment, false);
          }
        }
      }
    }
    WidgetState.showCorrectCascadedStates();
  }

  protected void enableAddButton(TaskActorInterface paramTaskActorInterface)
  {
	System.out.println("enableAddButton");
    ISelection localISelection = this.scheduleTaskTreeViewer.getSelection();
    int i = 0;
    int j = 0;
    int k = 0;
    if ((localISelection instanceof IStructuredSelection))
    {
      IStructuredSelection localIStructuredSelection = (IStructuredSelection)localISelection;
      List localList = localIStructuredSelection.toList();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        ResourceTreeModel localResourceTreeModel = (ResourceTreeModel)localIterator.next();
        if ((localResourceTreeModel instanceof PlaceHolderAssignmentTreeModel))
        {
          TCComponent localTCComponent = ((AssignmentTreeModel)localResourceTreeModel).getResource();
          boolean bool = ((AssignmentTreeModel)localResourceTreeModel).getIsPlaceHolderAssignment();
          if (((localTCComponent instanceof TCComponentResourcePool)) && (bool))
          {
            if (paramTaskActorInterface.isDiscipline())
            {
              k = 1;
              break;
            }
            if (((paramTaskActorInterface.isGroup()) || (paramTaskActorInterface.isRole())) && (this.btnPlaceHolderAssignment.getSelection()))
            {
              k = 1;
              break;
            }
          }
          if ((localTCComponent instanceof TCComponentDiscipline))
          {
            if (((paramTaskActorInterface.isGroup()) || (paramTaskActorInterface.isRole())) && (this.btnPlaceHolderAssignment.getSelection()))
            {
              k = 1;
              break;
            }
            if (paramTaskActorInterface.isDiscipline())
            {
              k = 1;
              break;
            }
          }
          j = 1;
        }
        else if ((localResourceTreeModel instanceof ScheduleTaskTreeModel))
        {
          if (this.scheduleRoot != localResourceTreeModel)
          {
            i = 1;
          }
          else
          {
            i = 0;
            break;
          }
        }
      }
      if (((i != 0) && (j == 0)) || (k != 0))
        WidgetState.addState(this.btnAdd, true);
      else
        WidgetState.addState(this.btnAdd, false);
    }
  }

  public void validateTeamAssignmentControls()
  {
	System.out.println("validateTeamAssignmentControls");
    this.btnAdd.setEnabled(false);
    this.buttonTeamPlaceHolder.setEnabled(false);
    this.isTeamMemberInSelection = false;
    this.isTeamGrpRoleInSelection = false;
    cascadeResourcePoolOption(this.buttonTeamPlaceHolder);
    List localList = null;
    ISelection localISelection = this.teamTreeViewer.getSelection();
    if ((localISelection instanceof IStructuredSelection))
    {
      IStructuredSelection localIStructuredSelection = (IStructuredSelection)localISelection;
      localList = localIStructuredSelection.toList();
      if (localISelection != null)
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          TaskActorInterface localTaskActorInterface = (TaskActorInterface)localIterator.next();
          if (localTaskActorInterface.getType() != null)
          {
            if ((localTaskActorInterface.isRole()) || (localTaskActorInterface.isGroup()))
            {
              this.isTeamGrpRoleInSelection = true;
              WidgetState.addState(this.buttonTeamPlaceHolder, true);
            }
            else
            {
              WidgetState.addState(this.buttonTeamPlaceHolder, false);
            }
            enableAddButton(localTaskActorInterface);
            if ((localTaskActorInterface.isUser()) || (localTaskActorInterface.isGroupMember()))
              this.isTeamMemberInSelection = true;
          }
          else
          {
            WidgetState.addState(this.btnAdd, false);
            WidgetState.addState(this.buttonTeamPlaceHolder, false);
          }
        }
        if ((this.isTeamMemberInSelection) && (this.isTeamGrpRoleInSelection))
          WidgetState.addState(this.btnAdd, false);
      }
    }
    WidgetState.showCorrectCascadedStates();
  }

  public void cascadeResourcePoolOption(Control paramControl)
  {
	System.out.println("cascadeResourcePoolOption");
    paramControl.setEnabled(false);
    Event localEvent = new Event();
    localEvent.type = 13;
    localEvent.widget = paramControl;
    paramControl.notifyListeners(13, localEvent);
  }

  public void validateDisciplineAssignmentControls()
  {
	System.out.println("validateDisciplineAssignmentControls");
    this.btnAdd.setEnabled(false);
    List localList = null;
    ISelection localISelection = this.disciplineTreeViewer.getSelection();
    if ((localISelection instanceof IStructuredSelection))
    {
      IStructuredSelection localIStructuredSelection = (IStructuredSelection)localISelection;
      localList = localIStructuredSelection.toList();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        TaskActorInterface localTaskActorInterface = (TaskActorInterface)localIterator.next();
        if (localTaskActorInterface.getType() != null)
          enableAddButton(localTaskActorInterface);
        else
          WidgetState.addState(this.btnAdd, false);
      }
    }
    WidgetState.showCorrectCascadedStates();
    System.out.println("validateDisciplineAssignmentControls done");
  }

  public void validateQualificationsTabControls()
  {
	System.out.println("validateQualificationsTabControls");
    validateAssignQualBtn();
    WidgetState.addState(this.btnAdd, false);
    WidgetState.showCorrectCascadedStates();
  }

  public void widgetSelected(SelectionEvent paramSelectionEvent)
  {
	System.out.println("widgetSelected");
    Object localObject = paramSelectionEvent.getSource();
    if ((localObject instanceof TabFolder))
    {
      TabFolder localTabFolder = (TabFolder)localObject;
      switch (localTabFolder.getSelectionIndex())
      {
      case 0:
    	  createDisciplinesTree();
          break;
      /*case 0:
        createOrgTree();
        break;
      case 1:
        createProjectTeamTree();
        break;
      case 2:
        createDisciplinesTree();
        break;
      case 3:
        validateQualificationsTabControls();
        break;
      case 4:
        createFilterQualTree();*/
      default:
        break;
      }
    }
    else
    {
      boolean bool;
      if (paramSelectionEvent.widget == this.btnPlaceHolderAssignment)
      {
        bool = this.btnPlaceHolderAssignment.getEnabled();
        cascadeSelection(this.grpResourcePoolOptions, bool);
        setResourcePoolGroupRoleOptions(this.btnPlaceHolderAssignment);
        setResourcePoolRoleOptions(this.btnPlaceHolderAssignment);
        validateAddButton();
      }
      else if (paramSelectionEvent.widget == this.buttonTeamPlaceHolder)
      {
        bool = this.buttonTeamPlaceHolder.getEnabled();
        cascadeSelection(this.groupTeamResourcePool, bool);
        setResourcePoolGroupRoleOptions(this.buttonTeamPlaceHolder);
        setResourcePoolRoleOptions(this.buttonTeamPlaceHolder);
      }
      else if (paramSelectionEvent.widget == this.comboProjects)
      {
        int i = this.comboProjects.getSelectionIndex();
        if ((this.previousSelectionIndex != i) && (i != -1))
        {
          this.teamTreeViewer.setContentProvider(new ProjectTeamTreeContentProvider(this.tcSession, this.comboProjects));
          this.teamTreeViewer.setLabelProvider(new ProjectTeamTreeLabelProvider(this.tcSession, this));
          this.teamTreeViewer.setInput("Root");
          this.previousSelectionIndex = i;
          cascadeSelection(this.groupTeamResourcePool, false);
        }
      }
    }
  }

  protected void setResourcePoolGroupRoleOptions(Button paramButton)
  {
	System.out.println("setResourcePoolGroupRoleOptions");
    List localList = null;
    TreeViewer localTreeViewer = null;
    boolean bool = paramButton.getSelection();
    if (paramButton == this.btnPlaceHolderAssignment)
      localTreeViewer = this.orgTreeViewer;
    if (paramButton == this.buttonTeamPlaceHolder)
      localTreeViewer = this.teamTreeViewer;
    if (localTreeViewer != null)
    {
      ISelection localISelection = localTreeViewer.getSelection();
      if ((localISelection instanceof IStructuredSelection))
      {
        IStructuredSelection localIStructuredSelection = (IStructuredSelection)localISelection;
        localList = localIStructuredSelection.toList();
        int i = this.tabFolder.getSelectionIndex();
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          TaskActorInterface localTaskActorInterface = (TaskActorInterface)localIterator.next();
          if (localTaskActorInterface.getType() != null)
          {
            if ((localTaskActorInterface.isRole()) || (localTaskActorInterface.isGroup()))
            {
              if (i == 0)
              {
                WidgetState.addState(this.btnAnyMember, !bool);
                WidgetState.addState(this.btnAllMembers, !bool);
                WidgetState.addState(this.btnSpecificGroup, bool);
                WidgetState.addState(this.btnAnyGroup, bool);
              }
              if (i == 1)
              {
                WidgetState.addState(this.buttonTeamAnyMember, !bool);
                WidgetState.addState(this.buttonTeamAllMember, !bool);
                WidgetState.addState(this.buttonTeamSpecificGroup, bool);
                WidgetState.addState(this.buttonTeamAnyGroup, bool);
              }
            }
            else
            {
              if (i == 0)
              {
                WidgetState.addState(this.btnAnyMember, false);
                WidgetState.addState(this.btnAllMembers, false);
                WidgetState.addState(this.btnSpecificGroup, false);
                WidgetState.addState(this.btnAnyGroup, false);
              }
              if (i == 1)
              {
                WidgetState.addState(this.buttonTeamAnyMember, false);
                WidgetState.addState(this.buttonTeamAllMember, false);
                WidgetState.addState(this.buttonTeamSpecificGroup, false);
                WidgetState.addState(this.buttonTeamAnyGroup, false);
              }
            }
          }
          else
          {
            WidgetState.addState(this.buttonTeamAnyMember, false);
            WidgetState.addState(this.buttonTeamAllMember, false);
            WidgetState.addState(this.buttonTeamSpecificGroup, false);
            WidgetState.addState(this.buttonTeamAnyGroup, false);
            WidgetState.addState(this.btnAnyMember, false);
            WidgetState.addState(this.btnAllMembers, false);
            WidgetState.addState(this.btnSpecificGroup, false);
            WidgetState.addState(this.btnAnyGroup, false);
          }
        }
      }
    }
    WidgetState.showCorrectStates();
  }

  protected void setResourcePoolRoleOptions(Button paramButton)
  {
	System.out.println("setResourcePoolRoleOptions");
    List localList = null;
    TreeViewer localTreeViewer = null;
    int i = 0;
    if (paramButton == this.btnPlaceHolderAssignment)
      localTreeViewer = this.orgTreeViewer;
    if (paramButton == this.buttonTeamPlaceHolder)
      localTreeViewer = this.teamTreeViewer;
    if (localTreeViewer != null)
    {
      ISelection localISelection = localTreeViewer.getSelection();
      if ((localISelection instanceof IStructuredSelection))
      {
        IStructuredSelection localIStructuredSelection = (IStructuredSelection)localISelection;
        localList = localIStructuredSelection.toList();
        if (localList != null)
        {
          int j = this.tabFolder.getSelectionIndex();
          Iterator localIterator = localList.iterator();
          while (localIterator.hasNext())
          {
            TaskActorInterface localTaskActorInterface = (TaskActorInterface)localIterator.next();
            if (localTaskActorInterface.getType() != null)
            {
              if (localTaskActorInterface.isRole())
              {
                if (j == 0)
                {
                  WidgetState.addState(this.btnSpecificGroup, i == 0);
                  WidgetState.addState(this.btnAnyGroup, i == 0);
                }
                if (j == 1)
                {
                  WidgetState.addState(this.buttonTeamSpecificGroup, i == 0);
                  WidgetState.addState(this.buttonTeamAnyGroup, i == 0);
                }
              }
              else
              {
                if (j == 0)
                {
                  WidgetState.addState(this.btnSpecificGroup, false);
                  WidgetState.addState(this.btnAnyGroup, false);
                }
                if (j == 1)
                {
                  WidgetState.addState(this.buttonTeamSpecificGroup, false);
                  WidgetState.addState(this.buttonTeamAnyGroup, false);
                }
              }
            }
            else
            {
              WidgetState.addState(this.buttonTeamAnyMember, false);
              WidgetState.addState(this.buttonTeamAllMember, false);
              WidgetState.addState(this.buttonTeamSpecificGroup, false);
              WidgetState.addState(this.buttonTeamAnyGroup, false);
              WidgetState.addState(this.btnAnyMember, false);
              WidgetState.addState(this.btnAllMembers, false);
              WidgetState.addState(this.btnSpecificGroup, false);
              WidgetState.addState(this.btnAnyGroup, false);
            }
          }
        }
      }
    }
    WidgetState.showCorrectStates();
  }

  protected void cascadeSelection(Control paramControl, boolean paramBoolean)
  {
	System.out.println("cascadeSelection");
    if ((paramControl instanceof Group))
    {
      Group localGroup = (Group)paramControl;
      Control[] arrayOfControl1 = localGroup.getChildren();
      for (Control localControl : arrayOfControl1)
        cascadeSelection(localControl, paramBoolean);
    }
    else
    {
      paramControl.setEnabled(paramBoolean);
    }
  }

  public void widgetDefaultSelected(SelectionEvent paramSelectionEvent)
  {
  }

  public void handleEvent(Event paramEvent)
  {
    if (paramEvent.widget == this.btnCancel)
    {
      this.result = this.btnCancel;
      this.shlTaskAssignment.close();
    }
    else if (paramEvent.widget == this.btnOk)
    {
      this.result = this.btnOk;
      this.shlTaskAssignment.close();
    }
  }

  public TCComponentSchedule getSchedule()
  {
	System.out.println("getSchedule");
    if (this.theSchedule == null)
    {
      TCComponent localTCComponent1 = this.theApp.getScheduleViewApplicationPanel().getSelectedComponent();
      if ((localTCComponent1 instanceof TCComponentScheduleTask))
      {
        TCComponentScheduleTask localTCComponentScheduleTask = (TCComponentScheduleTask)localTCComponent1;
        try
        {
          TCComponent localTCComponent2 = TaskHelper.getScheduleForTask(localTCComponentScheduleTask);
          if ((localTCComponent2 instanceof TCComponentSchedule))
            this.theSchedule = ((TCComponentSchedule)localTCComponent2);
        }
        catch (TCException localTCException)
        {
          logger.error("TaskAssignmentDialog::getSchedule", localTCException);
        }
      }
    }
    return this.theSchedule;
  }

  public void focusGained(FocusEvent paramFocusEvent)
  {
    if (paramFocusEvent.widget == this.filteredTaskTree)
      this.filteredTaskTree.setInitialText("");
    if ((paramFocusEvent.widget == this.orgSearchText) && (this.orgSearchText.getText().equals(this.registry.getString("TaskAssignmentDialog.OrganizationTreeViewer.SearchInitialText"))))
      this.orgSearchText.setText("");
  }

  public void focusLost(FocusEvent paramFocusEvent)
  {
    if (paramFocusEvent.widget == this.filteredTaskTree)
      this.filteredTaskTree.setInitialText(this.registry.getString("TaskAssignmentDialog.ScheduleTaskTreeViewer.InitialText"));
    if ((paramFocusEvent.widget == this.orgSearchText) && (this.orgSearchText.getText().equals("")))
      this.orgSearchText.setText(this.registry.getString("TaskAssignmentDialog.OrganizationTreeViewer.SearchInitialText"));
  }

  public UIAssignmentCache getAssignmentCache()
  {
    return this.assignmentCache;
  }

  public TreeViewer getScheduleTaskTreeViewer()
  {
    return this.scheduleTaskTreeViewer;
  }

  public TabFolder getTabFolder()
  {
    return this.tabFolder;
  }

  public Button getBtnRemove()
  {
    return this.btnRemove;
  }

  public Shell getShlTaskAssignment()
  {
    return this.shlTaskAssignment;
  }

  public StructuredViewer getSelectedViewer()
  {
    return this.selectedViewer;
  }

  public Combo getComboProjects()
  {
    return this.comboProjects;
  }

  protected boolean isInResourcePoolOption()
  {
    /*boolean bool = false;
    int i = this.tabFolder.getSelectionIndex();
    if ((i == 0) && (!this.btnPlaceHolderAssignment.getSelection()) && (!this.isOrgGrpUserInSelection))
      bool = true;
    else if ((i == 1) && (!this.buttonTeamPlaceHolder.getSelection()) && (!this.isTeamMemberInSelection))
      bool = true;
    return bool;*/
	return false;
  }

  protected boolean isPlaceHolderAssignment()
  {
    /*boolean bool = false;
    int i = this.tabFolder.getSelectionIndex();
    if ((i == 0) && (this.btnPlaceHolderAssignment.getSelection()) && (!this.isOrgGrpUserInSelection))
      bool = true;
    else if ((i == 1) && (this.buttonTeamPlaceHolder.getSelection()) && (!this.isTeamMemberInSelection))
      bool = true;
    return bool;*/
	  return false;
  }

  public BitSet getResourcePoolOptions()
  {
    BitSet localBitSet = null;
    if (isInResourcePoolOption())
    {
      int i = this.tabFolder.getSelectionIndex();
      System.out.println("tabFolder selection index is "+i);
      if (i == 0)
      {
        localBitSet = new BitSet(4);
        if (this.btnAnyMember.getSelection())
        {
          localBitSet.set(0);
          localBitSet.clear(1);
        }
        else
        {
          localBitSet.clear(0);
          localBitSet.set(1);
        }
        if (!this.btnSpecificGroup.isEnabled())
        {
          localBitSet.clear(2);
          localBitSet.clear(3);
        }
        else if (this.btnSpecificGroup.getSelection())
        {
          localBitSet.set(2);
          localBitSet.clear(3);
        }
        else
        {
          localBitSet.clear(2);
          localBitSet.set(3);
        }
      }
      else if (i == 1)
      {
        localBitSet = new BitSet(4);
        if (this.buttonTeamAnyMember.getSelection())
        {
          localBitSet.set(0);
          localBitSet.set(0);
        }
        else
        {
          localBitSet.clear(0);
          localBitSet.set(1);
        }
        if (!this.buttonTeamSpecificGroup.isEnabled())
        {
          localBitSet.clear(2);
          localBitSet.clear(3);
        }
        else if (this.buttonTeamSpecificGroup.getSelection())
        {
          localBitSet.set(2);
          localBitSet.clear(3);
        }
        else
        {
          localBitSet.clear(2);
          localBitSet.set(3);
        }
      }
    }
    return localBitSet;
  }

  public BitSet getPlaceHolderOptions()
  {
    BitSet localBitSet = null;
    if (isPlaceHolderAssignment())
    {
      int i = this.tabFolder.getSelectionIndex();
      if (i == 0)
      {
        localBitSet = new BitSet(4);
        if (!this.btnSpecificGroup.isEnabled())
        {
          localBitSet.clear(2);
          localBitSet.clear(3);
        }
        else if (this.btnSpecificGroup.getSelection())
        {
          localBitSet.set(2);
          localBitSet.clear(3);
        }
        else
        {
          localBitSet.clear(2);
          localBitSet.set(3);
        }
      }
      else if (i == 1)
      {
        localBitSet = new BitSet(4);
        if (!this.buttonTeamSpecificGroup.isEnabled())
        {
          localBitSet.clear(2);
          localBitSet.clear(3);
        }
        else if (this.buttonTeamSpecificGroup.getSelection())
        {
          localBitSet.set(2);
          localBitSet.clear(3);
        }
        else
        {
          localBitSet.clear(2);
          localBitSet.set(3);
        }
      }
    }
    return localBitSet;
  }

  public List<TCComponent> getSelectedTasks()
  {
    return this.selectedTasks;
  }

  public Button getBtnOk()
  {
    return this.btnOk;
  }

  protected InterfaceAIFComponent getSelectedTask()
  {
    this.orgTreeViewer.getSelection();
    InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = Activator.getDefault().getSelectionMediatorService().getTargetComponents();
    InterfaceAIFComponent localInterfaceAIFComponent = arrayOfInterfaceAIFComponent[0];
    return localInterfaceAIFComponent;
  }

  public TCComponent getSelectedComp()
  {
    InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = Activator.getDefault().getSelectionMediatorService().getTargetComponents();
    TCComponent localTCComponent = null;
    if ((arrayOfInterfaceAIFComponent != null) && (arrayOfInterfaceAIFComponent.length > 0))
      localTCComponent = (TCComponent)arrayOfInterfaceAIFComponent[0];
    return localTCComponent;
  }

  public boolean getAssignQualBtn()
  {
    return this.assignQualBtn.getEnabled();
  }

  public void setAssignQualBtn(boolean paramBoolean)
  {
    this.assignQualBtn.setEnabled(paramBoolean);
  }

  public boolean getRemoveQualBtn()
  {
    return this.removeQualBtn.getEnabled();
  }

  public void setRemoveQualBtn(boolean paramBoolean)
  {
    this.removeQualBtn.setEnabled(paramBoolean);
  }

  private class TableLabelProvider extends LabelProvider
    implements ITableLabelProvider
  {
    private TableLabelProvider()
    {
    }

    public String getColumnText(Object paramObject, int paramInt)
    {
      TaskQualificationModel localTaskQualificationModel = (TaskQualificationModel)paramObject;
      String str = "";
      switch (paramInt)
      {
      case 0:
        str = localTaskQualificationModel.getQualName();
        break;
      case 1:
        str = localTaskQualificationModel.getQualLevel();
        break;
      default:
        str = "";
      }
      return str;
    }

    public Image getColumnImage(Object paramObject, int paramInt)
    {
      return null;
    }
  }
}