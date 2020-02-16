package com.teamcenter.rac.schedule.commands.schedulemembership;

import com.teamcenter.rac.common.ActionAdapter;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDiscipline;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentResourcePool;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleMember;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentTCCalendar;
import com.teamcenter.rac.kernel.TCComponentTCCalendarEvent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.ScheduleViewApplication;
import com.teamcenter.rac.schedule.ScheduleViewApplicationPanel;
import com.teamcenter.rac.schedule.common.tree.ScheduleTreeNode;
import com.teamcenter.rac.schedule.common.util.ScheduleUtil;
import com.teamcenter.rac.schedule.common.util.TCComponentComparator;
import com.teamcenter.rac.schedule.componentutils.CalendarHelper;
import com.teamcenter.rac.schedule.project.scheduling.ModelFactory;
import com.teamcenter.rac.schedule.project.scheduling.ScheduleModel;
import com.teamcenter.rac.schedule.project.scheduling.SchedulingException;
import com.teamcenter.rac.schedule.project.server.RACInterface.RACInterface;
import com.teamcenter.rac.schedule.project.server.RACInterface.RACInterfaceFactory;
import com.teamcenter.rac.schedule.scheduler.componentutils.PreferenceHelper;
import com.teamcenter.rac.schedule.scheduler.componentutils.ScheduleHelper;
import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
import com.teamcenter.rac.util.CurrencyFormatter;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.SplitPane;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.projectmanagement.ScheduleManagementService;
import com.teamcenter.services.rac.projectmanagement._2007_01.ScheduleManagement.ScheduleObjDeleteContainer;
import com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.AddMembershipResponse;
import com.teamcenter.services.rac.projectmanagement._2008_06.ScheduleManagement.MembershipData;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.log4j.Logger;

public class ScheduleMemberTreePanel extends JPanel
{
  private String[] typeNames = null;
  private TCSession session = null;
  private TCComponent schedule = null;
  private ScheduleViewApplicationPanel schAppPanel = null;
  private static final Logger logger = Logger.getLogger(ScheduleMemberTreePanel.class);
  private String owningUser = null;
  private boolean editable = false;
  private ScheduleMemberTree tree = null;
  private ScheduleMemberPanel scheduleMemberPanel = null;
  private JPanel rightPanel = null;
  private JPanel leftPanel = null;
  public JScrollPane selectFromScrollPane;
  public JScrollPane selectedScrollPane;
  private JTable selectedTable = null;
  private Hashtable treeToTableHash = new Hashtable();
  private Hashtable tableNodes = new Hashtable();
  private Vector addVec = new Vector(2, 5);
  private Vector deleteVec = new Vector(2, 5);
  private Hashtable addHash = new Hashtable();
  private Hashtable deleteHash = new Hashtable();
  private Vector preExistingVec = null;
  private Vector modifyVec = null;
  private Hashtable preExistingHash = null;
  Registry registry = Registry.getRegistry(this);
  public String[] propNames = { this.registry.getString("newMember.name"), this.registry.getString("newMember.role"), this.registry.getString("newMember.rate") };
  private DataManagementService dmService;
  private Vector forbiddenVec = null;

  public ScheduleMemberTreePanel(TCSession paramTCSession)
  {
    this(null, paramTCSession);
  }

  public ScheduleMemberTreePanel(String[] paramArrayOfString, TCSession paramTCSession)
  {
    this(paramArrayOfString, paramTCSession, false);
    this.registry = Registry.getRegistry(this);
  }

  public ScheduleMemberTreePanel(String[] paramArrayOfString, TCSession paramTCSession, boolean paramBoolean)
  {
    this.session = paramTCSession;
    if ((ScheduleViewApplication.getApplication().getApplicationPanel() instanceof ScheduleViewApplicationPanel))
    {
      this.schAppPanel = ((ScheduleViewApplicationPanel)ScheduleViewApplication.getApplication().getApplicationPanel());
      TCComponent localTCComponent = this.schAppPanel.getSelectedComponent();
      if ((localTCComponent instanceof TCComponentSchedule))
        this.schedule = localTCComponent;
      else if ((localTCComponent instanceof TCComponentScheduleTask))
        try
        {
          this.schedule = TaskHelper.getScheduleForTask(localTCComponent);
        }
        catch (TCException localTCException1)
        {
          logger.error("Exception while retrieving schedule ", localTCException1);
        }
    }
    if (this.schedule == null)
      return;
    try
    {
      loadMembershipData(this.schAppPanel.getCurrentProject());
      this.owningUser = this.schedule.getTCProperty("owning_user").getReferenceValue().getUid();
    }
    catch (TCException localTCException2)
    {
      MessageBox.post(this.registry.getString("loadSchMemberInMemberDlg.MSG"), this.registry.getString("loadSchMemberInMemberDlg.TITLE"), 4);
    }
    catch (SchedulingException localSchedulingException)
    {
      logger.error("Exception while on demand loading of schedule members", localSchedulingException);
      MessageBox.post(this.registry.getString("loadSchMemberInMemberDlg.MSG"), this.registry.getString("loadSchMemberInMemberDlg.TITLE"), 4);
    }
    if(this.schedule.getSession().hasBypass())
    	this.editable=true;
    else
    	this.editable = ScheduleUtil.canChangeSchedulingData((TCComponentSchedule)this.schedule);
    if (paramArrayOfString == null)
      this.typeNames = this.registry.getStringArray("scheduleMember.TYPES");
    else
      this.typeNames = paramArrayOfString;
    this.dmService = DataManagementService.getService(paramTCSession);
    init();
    if (this.tree.getModel() != null)
      setPreExistingNodes();
    setSelectedTable();
  }

  private void loadMembershipData(TCComponent paramTCComponent)
    throws SchedulingException
  {
    ModelFactory.singleton(this.session).getScheduleModel().loadMemberDataEntries(paramTCComponent.getUid());
  }

  boolean isEditable()
  {
    return this.editable;
  }

  protected void init()
  {
    setPreExistingVec();
    this.scheduleMemberPanel = new ScheduleMemberPanel(this.session, getSchedule(), this.editable);
    this.selectedTable = this.scheduleMemberPanel.memberTable;
    this.tree = new ScheduleMemberTree(this.session, getTypeNames(), getSchedule());
    setLayout(new VerticalLayout(2, 2, 2, 2, 2));
    this.leftPanel = new JPanel(new VerticalLayout(2, 2, 2, 2, 2));
    JScrollPane localJScrollPane = new JScrollPane(this.tree);
    this.tree.getSelectionModel().setSelectionMode(1);
    CheckCellEditor localCheckCellEditor = new CheckCellEditor();
    localCheckCellEditor.addCheckListener(new ActionAdapter()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        ScheduleMemberTreePanel.this.handleDoubleClickSelectFrom(paramAnonymousActionEvent.getSource());
      }
    });
    if (this.editable)
      this.tree.setCellEditor(localCheckCellEditor);
    MouseAdapter local2 = new MouseAdapter()
    {
      public void mouseClicked(MouseEvent paramAnonymousMouseEvent)
      {
        if (paramAnonymousMouseEvent.getClickCount() > 0)
        {
          TreePath localTreePath = ScheduleMemberTreePanel.this.tree.getPathForLocation(paramAnonymousMouseEvent.getX(), paramAnonymousMouseEvent.getY());
          if (localTreePath == null)
            return;
          ScheduleMemberTreePanel.this.tree.setSelectionPath(localTreePath);
          ScheduleTreeNode localScheduleTreeNode = (ScheduleTreeNode)localTreePath.getLastPathComponent();
          if ((localScheduleTreeNode != null) && (localScheduleTreeNode.isLeaf()) && (paramAnonymousMouseEvent.getClickCount() == 1))
            ScheduleMemberTreePanel.this.handleSingleClickSelectFrom(localScheduleTreeNode);
        }
      }
    };
    if (this.editable)
      this.tree.addMouseListener(local2);
    this.leftPanel.add("unbound.bind", localJScrollPane);
    this.rightPanel = new JPanel(new VerticalLayout(0, 0, 0, 0, 0));
    this.rightPanel.add("unbound.bind", this.scheduleMemberPanel);
    SplitPane localSplitPane = new SplitPane(0);
    localSplitPane.setDividerLocation(0.4D);
    localSplitPane.setLeftComponent(this.leftPanel);
    localSplitPane.setRightComponent(this.rightPanel);
    add("unbound.bind", localSplitPane);
    setPreferredSize(new Dimension(800, 350));
  }

  public ScheduleMemberPanel getScheduleMemberPanel()
  {
    return this.scheduleMemberPanel;
  }

  private void setPreExistingVec()
  {
    try
    {
      TCProperty localTCProperty = this.schedule.getTCProperty("fnd0Schedulemember_taglist");
      TCComponent[] arrayOfTCComponent1 = localTCProperty.getReferenceValueArray();
      if (arrayOfTCComponent1.length > 0)
      {
        ServiceData localServiceData = this.dmService.getProperties(arrayOfTCComponent1, new String[] { "resource_tag" });
        TCComponent[] arrayOfTCComponent2 = new TCComponent[localServiceData.sizeOfPlainObjects()];
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;
        int i1 = 0;
        TCComponent localTCComponent;
        for (int i2 = 0; i2 < localServiceData.sizeOfPlainObjects(); i2++)
        {
          arrayOfTCComponent2[i2] = localServiceData.getPlainObject(i2);
          if ((arrayOfTCComponent2[i2] instanceof TCComponentScheduleMember))
          {
            localTCComponent = arrayOfTCComponent2[i2].getReferenceProperty("resource_tag");
            if (localTCComponent == null)
              localTCComponent = arrayOfTCComponent2[i2].getReferenceProperty("schedule_as_member");
            localArrayList1.add(localTCComponent);
            if ((i1 == 0) && ((localTCComponent instanceof TCComponentSchedule)))
            {
              localArrayList2.add("object_name");
              i1 = 1;
            }
            if ((i == 0) && ((localTCComponent instanceof TCComponentUser)))
            {
              for (int i3 = 0; i3 < TCComponentUser.USER_TOSTRING_PROPERTIES.length; i3++)
                localArrayList2.add(TCComponentUser.USER_TOSTRING_PROPERTIES[i3]);
              i = 1;
            }
            if ((k == 0) && ((localTCComponent instanceof TCComponentDiscipline)))
            {
              localArrayList2.add(localTCComponent.getToStringProperty());
              k = 1;
            }
            if ((j == 0) && ((localTCComponent instanceof TCComponentGroup)))
            {
              localArrayList2.add(localTCComponent.getToStringProperty());
              j = 1;
            }
            if ((m == 0) && ((localTCComponent instanceof TCComponentRole)))
            {
              localArrayList2.add(localTCComponent.getToStringProperty());
              m = 1;
            }
            if ((n == 0) && ((localTCComponent instanceof TCComponentResourcePool)))
            {
              localArrayList2.add(localTCComponent.getToStringProperty());
              n = 1;
            }
          }
        }
        TCComponentType.getPropertiesSet(localArrayList1, (String[])localArrayList2.toArray(new String[0]));
        this.preExistingVec = new Vector(2);
        this.preExistingHash = new Hashtable();
        for (int i2 = 0; i2 < arrayOfTCComponent1.length; i2++)
          if ((arrayOfTCComponent1[i2] instanceof TCComponentScheduleMember))
          {
            localTCComponent = null;
            localTCComponent = arrayOfTCComponent2[i2].getReferenceProperty("resource_tag");
            if ((localTCComponent != null) && (!this.preExistingVec.contains(localTCComponent)))
            {
              this.preExistingVec.add(localTCComponent);
              this.preExistingHash.put(localTCComponent, arrayOfTCComponent1[i2]);
            }
          }
      }
    }
    catch (TCException localTCException)
    {
      localTCException.printStackTrace();
    }
  }

  private void setPreExistingNodes()
  {
    this.tableNodes.clear();
    if ((this.preExistingVec != null) && (this.preExistingVec.size() > 0))
    {
      if (this.preExistingVec.size() > 1)
      {
        Collections.sort(this.preExistingVec, new TCComponentComparator());
        try
        {
          if (this.preExistingVec.contains(this.schedule.getTCProperty("owning_user").getReferenceValue()))
            this.preExistingVec.remove(this.schedule.getTCProperty("owning_user").getReferenceValue());
          this.preExistingVec.add(0, this.schedule.getTCProperty("owning_user").getReferenceValue());
        }
        catch (TCException localTCException)
        {
          logger.error("Exception while retrieving schedule member", localTCException);
        }
      }
      for (int i = 0; i < this.preExistingVec.size(); i++)
      {
        TCComponent localTCComponent1 = (TCComponent)this.preExistingVec.elementAt(i);
        String str = getKey(localTCComponent1);
        ScheduleTreeNode localScheduleTreeNode = null;
        if ((localScheduleTreeNode = this.tree.findNodeByNameAndType(localTCComponent1)) != null)
        {
          localScheduleTreeNode.setChecked(true);
          TCComponent localTCComponent2 = localScheduleTreeNode.getUnderlyingUserObject();
          if (localTCComponent2 != null)
          {
            this.addVec.add(localTCComponent2);
            this.addHash.put(getKey(localTCComponent1), localTCComponent1);
            this.tableNodes.put(str, localScheduleTreeNode);
            TCComponentScheduleMember localTCComponentScheduleMember = (TCComponentScheduleMember)this.preExistingHash.get(localTCComponent1);
            if (localTCComponentScheduleMember != null)
            {
              MembershipEntry localMembershipEntry = new MembershipEntry(localTCComponentScheduleMember, PreferenceHelper.getDefCurrency(this.session));
              this.scheduleMemberPanel.setSchedule(localMembershipEntry);
              this.treeToTableHash.put(localTCComponent1, localMembershipEntry);
            }
          }
        }
      }
    }
  }

  public void setForbiddenVec(Vector paramVector, String paramString)
  {
    this.forbiddenVec = paramVector;
  }

  public boolean forbidden(Object paramObject)
  {
    return (this.forbiddenVec != null) && (this.forbiddenVec.contains(paramObject));
  }

  public void setSchedule(TCComponent paramTCComponent)
  {
    this.schedule = paramTCComponent;
  }

  public TCComponent getSchedule()
  {
    return this.schedule;
  }

  public String[] getTypeNames()
  {
    return this.typeNames;
  }

  public void setTypeNames(String[] paramArrayOfString)
  {
    this.typeNames = paramArrayOfString;
  }

  public void handleDoubleClickSelectFrom(Object paramObject)
  {
    ScheduleTreeNode localScheduleTreeNode1 = null;
    if ((paramObject instanceof ScheduleTreeNode))
      localScheduleTreeNode1 = (ScheduleTreeNode)paramObject;
    if (localScheduleTreeNode1 == null)
      return;
    if (!localScheduleTreeNode1.isFolder())
    {
      if (localScheduleTreeNode1.isChecked())
      {
        TCComponent localTCComponent = localScheduleTreeNode1.getUnderlyingUserObject();
        if (this.owningUser.equals(localTCComponent.getUid()))
        {
          MessageBox.post(this.registry.getString("scheduleOwner.errMsg"), this.registry.getString("scheduleMemberCalendar.ERROR"), 1);
          return;
        }
        if (this.tableNodes.size() < 2)
        {
          MessageBox.post(this.registry.getString("scheduleMemberLastRemoval.ERRORMSG"), this.registry.getString("scheduleMemberCalendar.ERROR"), 1);
          return;
        }
        removeSelected(localScheduleTreeNode1);
        localScheduleTreeNode1.setChecked(!localScheduleTreeNode1.isChecked());
      }
      else if (localScheduleTreeNode1.getType().equals(this.registry.getString("user.TYPE_COMPONENT")))
      {
        if (checkUserStatus(localScheduleTreeNode1))
        {
          addSelected(localScheduleTreeNode1);
          localScheduleTreeNode1.setChecked(!localScheduleTreeNode1.isChecked());
        }
        else
        {
          MessageBox.post(this.registry.getString("UserStatus.MSG"), this.registry.getString("UserStatus.TITLE"), 1);
          localScheduleTreeNode1.setChecked(localScheduleTreeNode1.isChecked());
        }
      }
      else
      {
        addSelected(localScheduleTreeNode1);
        localScheduleTreeNode1.setChecked(!localScheduleTreeNode1.isChecked());
      }
    }
    else
    {
      int i;
      ScheduleTreeNode localScheduleTreeNode2;
      if (localScheduleTreeNode1.isChecked())
      {
        i = localScheduleTreeNode1.getChildCount();
        do
        {
          localScheduleTreeNode2 = (ScheduleTreeNode)localScheduleTreeNode1.getChildAt(i);
          if (!localScheduleTreeNode2.isFolder())
          {
            removeSelected(localScheduleTreeNode2);
            localScheduleTreeNode2.setChecked(false);
          }
          i--;
        }
        while (i >= 0);
      }
      else
      {
        i = localScheduleTreeNode1.getChildCount();
        do
        {
          localScheduleTreeNode2 = (ScheduleTreeNode)localScheduleTreeNode1.getChildAt(i);
          if (!localScheduleTreeNode2.isFolder())
          {
            addSelected(localScheduleTreeNode2);
            localScheduleTreeNode2.setChecked(true);
          }
          i--;
        }
        while (i >= 0);
      }
      localScheduleTreeNode1.setChecked(!localScheduleTreeNode1.isChecked());
    }
  }

  public void checkTaskAssigneesForDiscipline(ScheduleTreeNode paramScheduleTreeNode)
    throws TCException
  {
    TCComponent localTCComponent = paramScheduleTreeNode.getUnderlyingUserObject();
    Vector localVector1 = getTasksAssignments(localTCComponent);
    if (localVector1 != null)
    {
      Vector localVector2 = (Vector)localVector1.get(0);
      if ((localVector2 != null) && (localVector2.contains(localTCComponent.getUid())))
      {
        Object[] arrayOfObject = { ((TCComponentDiscipline)localTCComponent).getStringProperty("discipline_name") };
        MessageFormat localMessageFormat = new MessageFormat(this.registry.getString("scheduleDisciplineMemberAssigment.INFOMSG"));
        StringBuffer localStringBuffer = new StringBuffer(localMessageFormat.format(arrayOfObject));
        int i = localVector2.size();
        for (int j = 0; j < i; j++)
        {
          String str1 = (String)((Vector)localVector1.get(2)).get(j);
          String str2 = (String)((Vector)localVector1.get(1)).get(j);
          localStringBuffer.append("\n").append(str1).append(" (").append(str2).append(this.registry.getString("percent.sign")).append(")");
        }
        MessageBox.post(this.registry.getString("scheduleMemberRemoveAssigment.INFOMSG"), localStringBuffer.toString(), this.registry.getString("scheduleDisciplineMemberAssigment.MSGTITLE"), 2);
      }
      else
      {
        checkScheduleMembershipNode(paramScheduleTreeNode);
      }
    }
  }

  public void checkTaskAssigneesForRoles(ScheduleTreeNode paramScheduleTreeNode)
    throws TCException
  {
    TCComponent localTCComponent = paramScheduleTreeNode.getUnderlyingUserObject();
    Vector localVector1 = getTasksAssignments(localTCComponent);
    if (localVector1 != null)
    {
      Vector localVector2 = (Vector)localVector1.get(0);
      if ((localVector2 != null) && (localVector2.contains(localTCComponent.getUid())))
      {
        Object[] arrayOfObject = { ((TCComponentRole)localTCComponent).getStringProperty("role_name") };
        MessageFormat localMessageFormat = new MessageFormat(this.registry.getString("scheduleRoleMemberAssigment.INFOMSG"));
        StringBuffer localStringBuffer = new StringBuffer(localMessageFormat.format(arrayOfObject));
        int i = localVector2.size();
        for (int j = 0; j < i; j++)
        {
          String str1 = (String)((Vector)localVector1.get(2)).get(j);
          String str2 = (String)((Vector)localVector1.get(1)).get(j);
          localStringBuffer.append("\n").append(str1).append(" (").append(str2).append(this.registry.getString("percent.sign")).append(")");
        }
        MessageBox.post(this.registry.getString("scheduleMemberRemoveAssigment.INFOMSG"), localStringBuffer.toString(), this.registry.getString("scheduleRoleMemberAssigment.MSGTITLE"), 2);
      }
      else
      {
        checkScheduleMembershipNode(paramScheduleTreeNode);
      }
    }
  }

  public void checkTaskAssigneesForGroup(ScheduleTreeNode paramScheduleTreeNode)
    throws TCException
  {
    TCComponent localTCComponent = paramScheduleTreeNode.getUnderlyingUserObject();
    Vector localVector1 = getTasksAssignments(localTCComponent);
    if (localVector1 != null)
    {
      Vector localVector2 = (Vector)localVector1.get(0);
      if ((localVector2 != null) && (localVector2.contains(localTCComponent.getUid())))
      {
        Object[] arrayOfObject = { ((TCComponentGroup)localTCComponent).getStringProperty("name") };
        MessageFormat localMessageFormat = new MessageFormat(this.registry.getString("scheduleGroupMemberAssigment.INFOMSG"));
        StringBuffer localStringBuffer = new StringBuffer(localMessageFormat.format(arrayOfObject));
        int i = localVector2.size();
        for (int j = 0; j < i; j++)
        {
          String str1 = (String)((Vector)localVector1.get(2)).get(j);
          String str2 = (String)((Vector)localVector1.get(1)).get(j);
          localStringBuffer.append("\n").append(str1).append(" (").append(str2).append(this.registry.getString("percent.sign")).append(")");
        }
        MessageBox.post(this.registry.getString("scheduleMemberRemoveAssigment.INFOMSG"), localStringBuffer.toString(), this.registry.getString("scheduleGroupMemberAssigment.MSGTITLE"), 2);
      }
      else
      {
        checkScheduleMembershipNode(paramScheduleTreeNode);
      }
    }
  }

  public void checkTaskAssigneesForResourcePool(ScheduleTreeNode paramScheduleTreeNode)
    throws TCException
  {
    TCComponent localTCComponent = paramScheduleTreeNode.getUnderlyingUserObject();
    Vector localVector1 = getTasksAssignments(localTCComponent);
    if (localVector1 != null)
    {
      Vector localVector2 = (Vector)localVector1.get(0);
      if ((localVector2 != null) && (localVector2.contains(localTCComponent.getUid())))
      {
        Object[] arrayOfObject = { ((TCComponentResourcePool)localTCComponent).toDisplayString() };
        MessageFormat localMessageFormat = new MessageFormat(this.registry.getString("scheduleResourcePoolMemberAssigment.INFOMSG"));
        StringBuffer localStringBuffer = new StringBuffer(localMessageFormat.format(arrayOfObject));
        int i = localVector2.size();
        for (int j = 0; j < i; j++)
        {
          String str1 = (String)((Vector)localVector1.get(2)).get(j);
          String str2 = (String)((Vector)localVector1.get(1)).get(j);
          localStringBuffer.append("\n").append(str1).append(" (").append(str2).append(this.registry.getString("percent.sign")).append(")");
        }
        MessageBox.post(this.registry.getString("scheduleMemberRemoveAssigment.INFOMSG"), localStringBuffer.toString(), this.registry.getString("scheduleResourcePoolMemberAssigment.MSGTITLE"), 2);
      }
      else
      {
        checkScheduleMembershipNode(paramScheduleTreeNode);
      }
    }
  }

  public void handleSingleClickSelectFrom(Object paramObject)
  {
    ScheduleTreeNode localScheduleTreeNode = null;
    if ((paramObject instanceof ScheduleTreeNode))
      localScheduleTreeNode = (ScheduleTreeNode)paramObject;
    else
      return;
    if (localScheduleTreeNode.isChecked())
    {
      TCComponent localTCComponent = localScheduleTreeNode.getUnderlyingUserObject();
      if (this.owningUser.equals(localTCComponent.getUid()))
      {
        MessageBox.post(this.registry.getString("scheduleOwner.errMsg"), this.registry.getString("scheduleMemberCalendar.ERROR"), 1);
        return;
      }
      if (this.tableNodes.size() < 2)
      {
        MessageBox.post(this.registry.getString("scheduleMemberLastRemoval.ERRORMSG"), this.registry.getString("scheduleMemberCalendar.ERROR"), 1);
        return;
      }
      try
      {
        if (localTCComponent.getClass() == TCComponentGroup.class)
        {
          checkTaskAssigneesForGroup(localScheduleTreeNode);
        }
        else if (localTCComponent.getClass() == TCComponentRole.class)
        {
          checkTaskAssigneesForRoles(localScheduleTreeNode);
        }
        else if (localTCComponent.getClass() == TCComponentResourcePool.class)
        {
          checkTaskAssigneesForResourcePool(localScheduleTreeNode);
        }
        else if (localTCComponent.getClass() == TCComponentDiscipline.class)
        {
          checkTaskAssigneesForDiscipline(localScheduleTreeNode);
        }
        else
        {
          Vector localVector1 = getTasksAssignments(localTCComponent);
          if (localVector1 != null)
          {
            Vector localVector2 = (Vector)localVector1.get(0);
            if ((localVector2 != null) && (localVector2.contains(localTCComponent.getUid())))
            {
              Object[] arrayOfObject = { ((TCComponentUser)localTCComponent).getOSUserName() };
              MessageFormat localMessageFormat = new MessageFormat(this.registry.getString("scheduleMemberAssigment.INFOMSG"));
              StringBuffer localStringBuffer = new StringBuffer(localMessageFormat.format(arrayOfObject));
              int i = localVector2.size();
              for (int j = 0; j < i; j++)
              {
                String str1 = (String)((Vector)localVector1.get(2)).get(j);
                String str2 = (String)((Vector)localVector1.get(1)).get(j);
                localStringBuffer.append("\n").append(str1).append(" (").append(str2).append(this.registry.getString("percent.sign")).append(")");
              }
              MessageBox.post(this.registry.getString("scheduleMemberRemoveAssigment.INFOMSG"), localStringBuffer.toString(), this.registry.getString("scheduleMemberAssigment.MSGTITLE"), 2);
            }
            else
            {
              checkScheduleMembershipNode(localScheduleTreeNode);
            }
          }
        }
        return;
      }
      catch (TCException localTCException)
      {
        logger.error("TCException", localTCException);
      }
    }
    checkScheduleMembershipNode(localScheduleTreeNode);
  }

  private boolean checkUserStatus(ScheduleTreeNode paramScheduleTreeNode)
  {
    int i = 0;
    try
    {
      TCComponent localTCComponent = paramScheduleTreeNode.getUnderlyingUserObject();
      i = localTCComponent.getIntProperty("status");
      if (i != 0)
        return false;
    }
    catch (TCException localTCException)
    {
      logger.error("TCException :" + localTCException);
    }
    return true;
  }

  private void checkScheduleMembershipNode(ScheduleTreeNode paramScheduleTreeNode)
  {
    if (paramScheduleTreeNode.isChecked())
    {
      removeSelected(paramScheduleTreeNode);
      paramScheduleTreeNode.setChecked(!paramScheduleTreeNode.isChecked());
    }
    else if (paramScheduleTreeNode.getType().equals(this.registry.getString("user.TYPE_COMPONENT")))
    {
      if (checkUserStatus(paramScheduleTreeNode))
      {
        addSelected(paramScheduleTreeNode);
        paramScheduleTreeNode.setChecked(!paramScheduleTreeNode.isChecked());
      }
      else
      {
        MessageBox.post(this.registry.getString("UserStatus.MSG"), this.registry.getString("UserStatus.TITLE"), 1);
        paramScheduleTreeNode.setChecked(paramScheduleTreeNode.isChecked());
      }
    }
    else
    {
      addSelected(paramScheduleTreeNode);
      paramScheduleTreeNode.setChecked(!paramScheduleTreeNode.isChecked());
    }
  }

  public Vector<Object> getTasksAssignments(TCComponent paramTCComponent)
    throws TCException
  {
    RACInterface localRACInterface = RACInterfaceFactory.singleton().getRACInterface(this.session, TaskHelper.getMasterSchedule());
    TCComponentScheduleTask localTCComponentScheduleTask = ScheduleHelper.getScheduleSummaryTask(this.schedule);
    List localList = TaskHelper.getAllTasksInSch(localTCComponentScheduleTask);
    if (localList == null)
      return null;
    Vector localVector1 = new Vector(5, 5);
    Vector localVector2 = new Vector(5, 5);
    Vector localVector3 = new Vector(5, 5);
    Iterator localIterator1 = localList.iterator();
    while (localIterator1.hasNext())
    {
      TCComponent localObject = (TCComponent)localIterator1.next();
      if ((localObject != null) && (TaskHelper.getScheduleForTask(localObject).equals(this.schedule)))
      {
        Collection localCollection = localRACInterface.getInternalAssignments((TCComponent)localObject);
        if (localCollection != null)
        {
          Iterator localIterator2 = localCollection.iterator();
          while (localIterator2.hasNext())
          {
            TCComponent localTCComponent1 = (TCComponent)localIterator2.next();
            if (localTCComponent1 != null)
            {
              TCComponent localTCComponent2 = TaskHelper.getResourceFromResourceAssignment(localTCComponent1);
              if (localTCComponent2.getUid().compareTo(paramTCComponent.getUid()) == 0)
              {
                localVector1.add(localTCComponent2.getUid());
                double d = localRACInterface.getResourceLevel(localTCComponent1);
                localVector2.add(d);
                localVector3.add(((TCComponent)localObject).toString());
              }
            }
          }
        }
      }
    }
    Vector<Object> localObject = new Vector(3);
    ((Vector)localObject).add(localVector1);
    ((Vector)localObject).add(localVector2);
    ((Vector)localObject).add(localVector3);
    return localObject;
  }

  public void addSelected(ScheduleTreeNode paramScheduleTreeNode)
  {
    TCComponent localTCComponent = paramScheduleTreeNode.getUnderlyingUserObject();
    int i;
    if ((i = this.deleteVec.lastIndexOf(localTCComponent)) >= 0)
    {
      this.deleteVec.removeElementAt(i);
      if (this.deleteHash.containsKey(getKey(localTCComponent)))
        this.deleteHash.remove(getKey(localTCComponent));
    }
    this.addVec.addElement(localTCComponent);
    this.addHash.put(getKey(localTCComponent), localTCComponent);
    MembershipEntry localMembershipEntry = null;
    if ((this.preExistingVec != null) && (this.preExistingVec.contains(localTCComponent)))
      localMembershipEntry = new MembershipEntry((TCComponentScheduleMember)this.preExistingHash.get(localTCComponent), PreferenceHelper.getDefCurrency(this.session));
    else
      localMembershipEntry = new MembershipEntry(localTCComponent, getSchedule(), PreferenceHelper.getDefCurrency(this.session));
    TCComponentTCCalendar localTCComponentTCCalendar1 = CalendarHelper.getResourceCalendar(localTCComponent, this.session);
    TCComponentTCCalendar localTCComponentTCCalendar2 = null;
    if (localTCComponentTCCalendar1 != null)
      localTCComponentTCCalendar2 = (TCComponentTCCalendar)localTCComponentTCCalendar1.getBaseCalendar();
    TCComponentTCCalendarEvent[] arrayOfTCComponentTCCalendarEvent;
    RACInterface localRACInterface;
    if (localTCComponentTCCalendar2 != null)
    {
      arrayOfTCComponentTCCalendarEvent = CalendarHelper.getEventsForCalendar(localTCComponentTCCalendar2, false);
      localRACInterface = RACInterfaceFactory.singleton().getRACInterface(this.session, this.schedule);
      localRACInterface.reCacheCalendar(localTCComponentTCCalendar2, arrayOfTCComponentTCCalendarEvent);
    }
    if (localTCComponentTCCalendar1 != null)
    {
      arrayOfTCComponentTCCalendarEvent = CalendarHelper.getEventsForCalendar(localTCComponentTCCalendar1, false);
      localRACInterface = RACInterfaceFactory.singleton().getRACInterface(this.session, this.schedule);
      localRACInterface.reCacheCalendar(localTCComponentTCCalendar1, arrayOfTCComponentTCCalendarEvent);
    }
    this.scheduleMemberPanel.setSchedule(localMembershipEntry);
    this.tableNodes.put(getKey(localTCComponent), localMembershipEntry);
    this.treeToTableHash.put(localTCComponent, localMembershipEntry);
    setSelectedTable();
  }

  public void removeSelected(ScheduleTreeNode paramScheduleTreeNode)
  {
    stopEditting();
    TCComponent localTCComponent = paramScheduleTreeNode.getUnderlyingUserObject();
    int i;
    if ((i = this.addVec.lastIndexOf(localTCComponent)) >= 0)
    {
      this.addVec.removeElementAt(i);
      if (this.addHash.containsKey(getKey(localTCComponent)))
        this.addHash.remove(getKey(localTCComponent));
    }
    this.deleteVec.addElement(localTCComponent);
    this.deleteHash.put(getKey(localTCComponent), localTCComponent);
    if (this.tableNodes.containsKey(getKey(localTCComponent)))
      this.tableNodes.remove(getKey(localTCComponent));
    if (this.treeToTableHash.containsKey(localTCComponent))
    {
      MembershipEntry localObject = (MembershipEntry)this.treeToTableHash.get(localTCComponent);
      int j = ((MemberTableModel)this.selectedTable.getModel()).getRowIndex(localObject);
      ((MemberTableModel)this.selectedTable.getModel()).removeRow(j);
      this.treeToTableHash.remove(localTCComponent);
    }
    setSelectedTable();
    Object localObject = this.tree.getSelectionPath();
    this.tree.stopEditing();
    this.tree.setSelectionPath((TreePath)localObject);
  }

  public String getKey(TCComponent paramTCComponent)
  {
    return paramTCComponent.getUid();
  }

  public void performAdds()
    throws SchedulingException
  {
    stopEditting();
    if ((this.addVec == null) || (this.addVec.size() == 0))
      return;
    RACInterface localRACInterface = RACInterfaceFactory.singleton().getRACInterface(this.session, TaskHelper.getMasterSchedule());
    ScheduleManagementService localScheduleManagementService = ScheduleManagementService.getService(this.session);
    Vector localVector = new Vector(2);
    this.modifyVec = new Vector(2);
    for (int i = 0; i < this.addVec.size(); i++)
    {
      TCComponent localTCComponent = (TCComponent)this.addVec.get(i);
      if ((((MembershipEntry)this.treeToTableHash.get(this.addVec.get(i))).getNewMembershipData() == null) || ((this.preExistingVec != null) && (this.preExistingVec.contains(this.addVec.get(i)))))
        this.modifyVec.add(localTCComponent);
      else
        localVector.add(localTCComponent);
    }
    if (localVector.size() > 0)
    {
      MembershipData[] arrayOfMembershipData = new MembershipData[localVector.size()];
      Object localObject;
      for (int j = 0; j < localVector.size(); j++)
      {
        localObject = (MembershipEntry)this.treeToTableHash.get(localVector.get(j));
        arrayOfMembershipData[j] = ((MembershipEntry)localObject).getNewMembershipData();
        localRACInterface.addScheduleMember(((MembershipEntry)localObject).getMember());
        localRACInterface.updateResourceRate(((MembershipEntry)localObject).getMember(), this.schedule, ((MembershipEntry)localObject).getMemberRate(), ((MembershipEntry)localObject).getCurrency());
      }
      try
      {
        AddMembershipResponse localAddMembershipResponse = localScheduleManagementService.addMemberships(arrayOfMembershipData);
        if (localAddMembershipResponse.data.sizeOfPartialErrors() > 0)
        {
         com.teamcenter.soa.client.model.ErrorStack localObject2 = localAddMembershipResponse.data.getPartialError(0);
          throw new SchedulingException(localObject2.getMessages()[0]);
        }
        if (localAddMembershipResponse.data.sizeOfCreatedObjects() == 0)
          throw new SchedulingException("SERVER_INTERFACE_ERR_GET_RESPONSE");
      }
      catch (SchedulingException localSchedulingException)
      {
        throw localSchedulingException;
      }
    }
  }

  public void performDeletes()
    throws SchedulingException
  {
    if ((this.preExistingHash == null) || (this.deleteVec == null) || (this.deleteVec.size() == 0))
      return;
    Vector localVector = new Vector();
    Object localObject1;
    ScheduleObjDeleteContainer localObject2;
    for (int i = 0; i < this.deleteVec.size(); i++)
    {
      localObject1 = (TCComponent)this.deleteVec.get(i);
      if (!this.preExistingHash.containsKey(localObject1))
      {
        i--;
        this.deleteVec.removeElement(localObject1);
      }
      else
      {
        localObject2 = new ScheduleObjDeleteContainer();
        localObject2.object = ((TCComponent)this.preExistingHash.get(localObject1));
        localVector.addElement(localObject2);
      }
    }
    if (localVector.isEmpty())
      return;
    try
    {
      ScheduleManagementService localScheduleManagementService = ScheduleManagementService.getService(this.session);
      localObject1 = localScheduleManagementService.deleteSchedulingObjects((ScheduleObjDeleteContainer[])localVector.toArray(new ScheduleObjDeleteContainer[0]));
      if (((ServiceData)localObject1).sizeOfDeletedObjects() == 0)
      {
        if (((ServiceData)localObject1).sizeOfPartialErrors() > 0)
        {
          com.teamcenter.soa.client.model.ErrorStack localObject3 = ((ServiceData)localObject1).getPartialError(0);
          throw new SchedulingException(localObject3.getMessages()[0]);
        }
        throw new SchedulingException("SERVER_INTERFACE_ERR_GET_RESPONSE");
      }
    }
    catch (SchedulingException localSchedulingException)
    {
      throw localSchedulingException;
    }
  }

  private void stopEditting()
  {
    if (this.selectedTable.isEditing())
    {
      TableCellEditor localTableCellEditor = this.selectedTable.getCellEditor();
      if (localTableCellEditor != null)
        localTableCellEditor.stopCellEditing();
    }
  }

  public void performUpdates()
    throws SchedulingException
  {
    stopEditting();
    if ((this.modifyVec == null) || (this.modifyVec.size() == 0))
      return;
    RACInterface localRACInterface = RACInterfaceFactory.singleton().getRACInterface(this.session, TaskHelper.getMasterSchedule());
    if (this.modifyVec.size() > 0)
    {
      TCComponentScheduleMember[] arrayOfTCComponentScheduleMember = new TCComponentScheduleMember[this.modifyVec.size()];
      for (int i = 0; i < this.modifyVec.size(); i++)
      {
        MembershipEntry localMembershipEntry = (MembershipEntry)this.treeToTableHash.get(this.modifyVec.get(i));
        if (localMembershipEntry.getOrignalComp() != null)
        {
          arrayOfTCComponentScheduleMember[i] = localMembershipEntry.getOrignalComp();
          int j = localMembershipEntry.getPrivilege();
          BigDecimal localBigDecimal = localMembershipEntry.getMemberRate();
          String str1 = localMembershipEntry.getCurrency();
          localRACInterface.updateResourceRate(localMembershipEntry.getMember(), this.schedule, localBigDecimal, str1);
          try
          {
            TCProperty localTCProperty1 = arrayOfTCComponentScheduleMember[i].getTCProperty(this.propNames[1]);
            TCProperty[] arrayOfTCProperty = { localTCProperty1 };
            if (localTCProperty1.getIntValue() != j)
              try
              {
                arrayOfTCProperty[0].setIntValue(j);
              }
              catch (Exception localException1)
              {
                MessageBox.post(localException1);
              }
            TCComponent localTCComponent = arrayOfTCComponentScheduleMember[i].getReferenceProperty("costvalue_form_tag");
            TCProperty localTCProperty2 = null;
            TCProperty localTCProperty3 = null;
            if(localTCComponent!=null){
            	localTCProperty2 = localTCComponent.getTCProperty("cost");
            	localTCProperty3 = localTCComponent.getTCProperty("currency");
            }
            String str2 = CurrencyFormatter.formatForPersistence(localBigDecimal);
            if (!localMembershipEntry.isOverridden())
            {
              str2 = "";
              str1 = "";
            }
            if (localTCProperty2!=null && !localTCProperty2.getStringValue().equals(localBigDecimal.toPlainString()))
              try
              {
                localTCProperty2.setStringValue(str2);
              }
              catch (Exception localException2)
              {
                MessageBox.post(localException2);
              }
            if (localTCProperty3!=null && !localTCProperty3.getStringValue().equals(str1))
              try
              {
                localTCProperty3.setStringValue(str1);
              }
              catch (Exception localException3)
              {
                MessageBox.post(localException3);
              }
          }
          catch (TCException localTCException)
          {
            throw new SchedulingException(localTCException.toString());
          }
        }
      }
    }
  }

  public void reset(boolean paramBoolean)
  {
    if ((paramBoolean) && (this.session != null))
    {
      DataManagementService localDataManagementService = DataManagementService.getService(this.session);
      if ((localDataManagementService != null) && (TaskHelper.getMasterSchedule() != null))
        localDataManagementService.getProperties(new TCComponent[] { TaskHelper.getMasterSchedule() }, new String[] { "fnd0Schedulemember_taglist" });
    }
    this.addVec = new Vector(2, 5);
    this.deleteVec = new Vector(2, 5);
    this.preExistingVec = null;
    this.preExistingHash = null;
    this.treeToTableHash = new Hashtable();
    this.tableNodes = new Hashtable();
    this.addHash = new Hashtable();
    this.deleteHash = new Hashtable();
  }

  public ScheduleMemberTree getMemberTree()
  {
    return this.tree;
  }

  public void setSelectedTable()
  {
    this.selectedTable.invalidate();
    this.selectedTable.clearSelection();
    this.selectedTable.validate();
    this.selectedTable.repaint();
    this.tree.invalidate();
    this.tree.validate();
    this.tree.repaint();
  }

  public boolean isUpdated()
  {
    return true;
  }

  public class CheckCellEditor
    implements TreeCellEditor
  {
    private Vector listenerList = new Vector();

    public CheckCellEditor()
    {
    }

    public Component getTableCellEditorComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
    {
      return paramJTree;
    }

    public boolean isCellEditable(EventObject paramEventObject)
    {
      if (((paramEventObject instanceof MouseEvent)) && (this.listenerList.size() > 0))
      {
        int i = ScheduleMemberTreePanel.this.tree.getRowForLocation(((MouseEvent)paramEventObject).getX(), ((MouseEvent)paramEventObject).getY());
        if (i >= 0)
        {
          Rectangle localRectangle = ScheduleMemberTreePanel.this.tree.getRowBounds(i);
          localRectangle.setBounds(localRectangle.x + CheckCellRenderer.CHECKBOXOFFSET, localRectangle.y, CheckCellRenderer.CHECKBOXWIDTH, localRectangle.height);
          boolean bool = localRectangle.contains(((MouseEvent)paramEventObject).getPoint());
          if (bool)
            notifyListeners(ScheduleMemberTreePanel.this.tree.getPathForRow(i).getLastPathComponent());
        }
      }
      return false;
    }

    public void addCheckListener(ActionListener paramActionListener)
    {
      this.listenerList.addElement(paramActionListener);
    }

    private void notifyListeners(Object paramObject)
    {
      int i = this.listenerList.size();
      do
      {
        ((ActionListener)this.listenerList.elementAt(i)).actionPerformed(new ActionEvent(paramObject, 0, ""));
        i--;
      }
      while (i >= 0);
    }

    public void addCellEditorListener(CellEditorListener paramCellEditorListener)
    {
    }

    public void cancelCellEditing()
    {
    }

    public Object getCellEditorValue()
    {
      return null;
    }

    public void removeCellEditorListener(CellEditorListener paramCellEditorListener)
    {
    }

    public boolean shouldSelectCell(EventObject paramEventObject)
    {
      return false;
    }

    public boolean stopCellEditing()
    {
      return true;
    }

    public Component getTreeCellEditorComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
    {
      return null;
    }
  }
}