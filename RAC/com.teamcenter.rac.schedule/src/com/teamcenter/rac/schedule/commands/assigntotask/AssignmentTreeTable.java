package com.teamcenter.rac.schedule.commands.assigntotask;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.common.AIFTreeLine;
import com.teamcenter.rac.aif.common.AIFTreeTable;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.aifrcp.AppThemeHelper;
import com.teamcenter.rac.charts.resourcegraph.ResourceGraphBasePanel;
import com.teamcenter.rac.charts.resourcegraph.ResourceGraphContainerPanel;
import com.teamcenter.rac.common.TCTreeCellRenderer;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDiscipline;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.ScheduleViewApplicationPanel;
import com.teamcenter.rac.schedule.commands.deferred.ScheduleDeferredContext;
import com.teamcenter.rac.schedule.common.tree.ScheduleTreeLine;
import com.teamcenter.rac.schedule.project.resourceGraph.AdapterJFreeChart;
import com.teamcenter.rac.schedule.project.resourceGraph.GraphView;
import com.teamcenter.rac.schedule.project.resourceGraph.IGraphCommand;
import com.teamcenter.rac.schedule.project.resourceGraph.IGraphCommandListener;
import com.teamcenter.rac.schedule.project.resourceGraph.ResourceContainer;
import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;

public class AssignmentTreeTable extends AIFTreeTable
  implements IGraphCommandListener
{
  private static final String rgRoleName = "Resource Graph Viewers";
  private AssignmentsTreeTableCellRenderer columnRenderer = null;
  private ResourceGraphButtonTblCellRndr iconColumnRenderer = null;
  private AssignedPrivilegedCellRenderer assignedPrivilegedCellRenderer = null;
  private ScheduleViewApplicationPanel schedulePanel = null;
  private Vector<String> memList = null;
  private String privilegedUser = null;
  private boolean privilegedUserNeeded = false;
  private HashMap<String, ScheduleTreeLine> privilegedCandidates = null;
  private Registry r = Registry.getRegistry(this);
  private TCComponent selectedResource = null;
  private TCComponent selectedTask = null;
  private ViewResourceGraphFromAssignToTaskDialog dlg = null;
  private AdapterJFreeChart adapter = null;
  private String currentRole = null;
  private Calendar taskRangeBegin = null;
  private Calendar taskRangeEnd = null;
  private Calendar startPlusTwelveMonth = null;
  private boolean forNewTaskFlag = false;
  private TCSession session;
  private static final Logger logger = Logger.getLogger(AssignmentTreeTable.class);
  private static MouseListener refreshBtnMouseListener = null;
  private ResourceContainer resourceContainer = null;

  public AssignmentTreeTable(AssignmentTreeTableModel paramAssignmentTreeTableModel, ScheduleViewApplicationPanel paramScheduleViewApplicationPanel, Vector paramVector)
  {
    super(paramAssignmentTreeTableModel);
    //System.out.println("AssignmentTreeTable AssignmentTreeTableModel ScheduleViewApplicationPanel Vector");
    this.schedulePanel = paramScheduleViewApplicationPanel;
    this.session = this.schedulePanel.getApplicationSession();
    this.memList = paramVector;
    this.selectedTask = this.schedulePanel.getSelectedComponent();
    setResourceGraphDatesFromSelectedTask();
    TCComponentRole localTCComponentRole = this.session.getCurrentRole();
    try
    {
      this.currentRole = localTCComponentRole.getTCProperty("role_name").getStringValue();
    }
    catch (TCException localTCException)
    {
      logger.error(localTCException.getClass().getName(), localTCException);
    }
    addMouseListerner();
    addEventListeners();
    setBackground(Color.WHITE);
    setAutoResizeMode(4);
    setSelectionMode(0);
    setRowHeight(AppThemeHelper.getOptimalRowHeight(getFont()));
    getTree().setCellRenderer(new TCTreeCellRenderer());
    setColumnRenderers();
    this.privilegedCandidates = new HashMap();
  }

  public AssignmentTreeTable(AssignmentTreeTableModel paramAssignmentTreeTableModel, ScheduleViewApplicationPanel paramScheduleViewApplicationPanel, Vector paramVector, boolean paramBoolean)
  {
    this(paramAssignmentTreeTableModel, paramScheduleViewApplicationPanel, paramVector);
    //System.out.println("AssignmentTreeTable AssignmentTreeTableModel ScheduleViewApplicationPanel Vector paramBoolean");
    this.forNewTaskFlag = paramBoolean;
  }

  protected void addMouseListerner()
  {
	//System.out.println("addMouseListerner");
    addMouseListener(new MouseEventHandler());
  }

  protected Vector getMemberList()
  {
	//System.out.println("getMemberList");
    return this.memList;
  }

  public String getPrivilegedUser()
  {
	//System.out.println("getPrivilegedUser");
    return this.privilegedUserNeeded ? this.privilegedUser : null;
  }

  public void setPrivilegedUser(String paramString)
  {
	//System.out.println("setPrivilegedUser paramString");
    if (this.privilegedUserNeeded)
    {
      this.privilegedUser = paramString;
      repaint();
    }
  }

  private boolean isPrivilegedCandidate(ScheduleTreeLine paramScheduleTreeLine)
  {
	//System.out.println("isPrivilegedCandidate ScheduleTreeLine");
    if ((!this.privilegedUserNeeded) || (paramScheduleTreeLine == null) || (!paramScheduleTreeLine.isSelected()))
      return false;
    Object localObject1 = paramScheduleTreeLine.getUserObject();
    if (!(localObject1 instanceof TCComponentUser))
      return false;
    TreeNode localTreeNode = paramScheduleTreeLine.getParent();
    if ((localTreeNode != null) && ((localTreeNode instanceof ScheduleTreeLine)))
    {
      ScheduleTreeLine localScheduleTreeLine1 = (ScheduleTreeLine)localTreeNode;
      Object localObject2 = localScheduleTreeLine1.getUserObject();
      if ((localObject2 instanceof TCComponentDiscipline))
        return false;
      String str = ((TCComponentUser)localObject1).getUid();
      ScheduleTreeLine localScheduleTreeLine2 = (ScheduleTreeLine)this.privilegedCandidates.get(str);
      if (localScheduleTreeLine2 == paramScheduleTreeLine)
        return true;
      if ((localScheduleTreeLine2 == null) || (!localScheduleTreeLine2.isSelected()))
      {
        this.privilegedCandidates.put(str, paramScheduleTreeLine);
        return true;
      }
      if ((localTreeNode.getParent() == null) && (localScheduleTreeLine2.getParent().getParent() != null))
      {
        this.privilegedCandidates.put(str, paramScheduleTreeLine);
        return true;
      }
    }
    return false;
  }

  private boolean isPrivilegedUser(ScheduleTreeLine paramScheduleTreeLine)
  {
	//System.out.println("isPrivilegedUser ScheduleTreeLine");
    if (!isPrivilegedCandidate(paramScheduleTreeLine))
      return false;
    Object localObject = paramScheduleTreeLine.getUserObject();
    String str = ((TCComponentUser)localObject).getUid();
    if (this.privilegedUser == null)
    {
      this.privilegedUser = str;
      return true;
    }
    return this.privilegedUser.equals(str);
  }

  private void clearPrivilegedUser(ScheduleTreeLine paramScheduleTreeLine)
  {
	//System.out.println("clearPrivilegedUser ScheduleTreeLine");
    Object localObject = paramScheduleTreeLine.getUserObject();
    String str = ((TCComponent)localObject).getUid();
    if (this.privilegedCandidates.get(str) == paramScheduleTreeLine)
    {
      this.privilegedCandidates.remove(str);
      if (this.privilegedUser.equals(str))
        this.privilegedUser = null;
    }
  }

  public void setPrivilegedUserNeeded(boolean paramBoolean)
  {
	//System.out.println("setPrivilegedUserNeeded paramBoolean");
    if (this.privilegedUserNeeded != paramBoolean)
    {
      this.privilegedUserNeeded = paramBoolean;
      repaint();
    }
  }

  private boolean isAssignedCandidate(ScheduleTreeLine paramScheduleTreeLine)
  {
	//System.out.println("isAssignedCandidate ScheduleTreeLine");
    if (paramScheduleTreeLine == null)
      return false;
    Object localObject = paramScheduleTreeLine.getUserObject();
    return ((localObject instanceof TCComponentUser)) || ((localObject instanceof TCComponentDiscipline));
  }

  protected boolean isAssigned(AssignmentTreeLine paramAssignmentTreeLine)
  {
	//System.out.println("isAssigned AssignmentTreeLine");
    if (!isAssignedCandidate(paramAssignmentTreeLine))
      return false;
    boolean bool = paramAssignmentTreeLine.isSelected();
    String str1 = paramAssignmentTreeLine.getKey();
    if ((bool) && (!this.memList.contains(str1)))
    {
      this.memList.add(str1);
    }
    else if ((!bool) && (this.memList.contains(str1)))
    {
      this.memList.remove(str1);
    }
    else if (!paramAssignmentTreeLine.isActiveUser())
    {
      str1 = paramAssignmentTreeLine.getResUID();
      Vector localVector = new Vector(this.memList.size());
      for (int i = 0; i < this.memList.size(); i++)
      {
        String str2 = (String)this.memList.get(i);
        String[] arrayOfString = AssignmentTreeLine.parseKey(str2);
        String str3 = arrayOfString[0];
        localVector.add(str3);
      }
      if ((bool) && (!this.memList.contains(str1)))
      {
        this.memList.add(str1);
      }
      else if ((!bool) && (localVector.contains(str1)))
      {
        int i = localVector.indexOf(str1);
        this.memList.remove(i);
      }
    }
    return bool;
  }

  private void setResourceGraphDatesFromSelectedTask()
  {
	//System.out.println("setResourceGraphDatesFromSelectedTask");
    if ((this.selectedTask != null) && ((this.selectedTask instanceof TCComponentScheduleTask)))
    {
      TCComponentScheduleTask localTCComponentScheduleTask = (TCComponentScheduleTask)this.selectedTask;
      try
      {
        this.taskRangeBegin = TaskHelper.getDateCalendar(TaskHelper.getStartDate(localTCComponentScheduleTask));
        this.taskRangeEnd = TaskHelper.getDateCalendar(TaskHelper.getEndDate(localTCComponentScheduleTask));
        this.startPlusTwelveMonth = ((Calendar)this.taskRangeBegin.clone());
        this.startPlusTwelveMonth.add(2, 12);
        if (this.startPlusTwelveMonth.before(this.taskRangeEnd))
          this.taskRangeEnd = this.startPlusTwelveMonth;
      }
      catch (TCException localTCException)
      {
        MessageBox.post(AIFUtility.getActiveDesktop().getFrame(), localTCException);
        return;
      }
    }
  }

  private void setColumnRenderer(String paramString, TableCellRenderer paramTableCellRenderer)
  {
	//System.out.println("setColumnRenderer paramString paramTableCellRenderer");
    try
    {
      getColumn(this.m_reg.getString(paramString)).setCellRenderer(paramTableCellRenderer);
    }
    catch (Exception localException)
    {
    }
  }

  public void setColumnRenderers()
  {
	//System.out.println("setColumnRenderers");
    ToolTipManager.sharedInstance().unregisterComponent(this);
    if (this.columnRenderer == null)
      this.columnRenderer = new AssignmentsTreeTableCellRenderer();
    if (this.iconColumnRenderer == null)
      this.iconColumnRenderer = new ResourceGraphButtonTblCellRndr();
    setColumnRenderer("resourcegraph", this.iconColumnRenderer);
    if (this.assignedPrivilegedCellRenderer == null)
      this.assignedPrivilegedCellRenderer = new AssignedPrivilegedCellRenderer();
    setColumnRenderer("assigned/privileged", this.assignedPrivilegedCellRenderer);
    setColumnRenderer("isassigned", this.assignedPrivilegedCellRenderer);
    ToolTipManager.sharedInstance().registerComponent(this);
  }

  public void setTaskRangeBegin(Calendar paramCalendar)
  {
	//System.out.println("setTaskRangeBegin paramCalendar");
    this.taskRangeBegin = paramCalendar;
  }

  public void setTaskRangeEnd(Calendar paramCalendar)
  {
	//System.out.println("setTaskRangeEnd paramCalendar");
    if (paramCalendar == null)
      this.taskRangeEnd = Calendar.getInstance();
    else
      this.taskRangeEnd = paramCalendar;
    if (this.taskRangeBegin == null)
      this.taskRangeBegin = this.taskRangeEnd;
    this.startPlusTwelveMonth = ((Calendar)this.taskRangeBegin.clone());
    this.startPlusTwelveMonth.add(2, 12);
    if (this.startPlusTwelveMonth.before(this.taskRangeEnd))
      this.taskRangeEnd = this.startPlusTwelveMonth;
  }

  public boolean isForNewTask()
  {
	//System.out.println("isForNewTask");
    return this.forNewTaskFlag;
  }

  public String getToolTipText(MouseEvent paramMouseEvent)
  {
	  
	//System.out.println("getToolTipText paramMouseEvent");
    StringBuffer localStringBuffer = new StringBuffer();
    Point localPoint = paramMouseEvent.getPoint();
    int i = columnAtPoint(localPoint);
    int j = rowAtPoint(localPoint);
    if (j == 0)
      return null;
    TableCellRenderer localTableCellRenderer = getCellRenderer(j, i);
    ScheduleTreeLine localScheduleTreeLine = (ScheduleTreeLine)getRow(j);
    Object localObject1 = localScheduleTreeLine.getUserObject();
    if (getColumnName(i).equals(this.m_reg.getString("member/disciplinename")))
    {
      while ((localObject1 instanceof TCComponent))
      {
    	TCComponent localObject2 = (TCComponent)localObject1;
        if (localStringBuffer.toString().equals(""))
          localStringBuffer.append(((TCComponent)localObject2).getType()).append(": ").append(((TCComponent)localObject2).toString());
        else
          localStringBuffer.append(", ").append(((TCComponent)localObject2).getType()).append(": ").append(((TCComponent)localObject2).toString());
        localScheduleTreeLine = (ScheduleTreeLine)localScheduleTreeLine.getParent();
        if (localScheduleTreeLine == null)
          break;
        localObject1 = localScheduleTreeLine.getUserObject();
      }
    }
    else if (getColumnName(i).equalsIgnoreCase(this.m_reg.getString("resourceload")))
    {
      if (((localObject1 instanceof TCComponentUser)) || ((localObject1 instanceof TCComponentDiscipline)))
        localStringBuffer.append(new StringBuffer(this.m_reg.getString("resourceLoadToolTip")));
    }
    else if ((localTableCellRenderer instanceof AssignedPrivilegedCellRenderer))
    {
     HitStatus localObject2 = ((AssignedPrivilegedCellRenderer)localTableCellRenderer).hitTest(this, j, i, localPoint);
      if (localObject2 == HitStatus.ASSIGNED_CANDIDATE)
        localStringBuffer.append(new StringBuffer(this.m_reg.getString("assignedToolTip")));
      else if (localObject2 == HitStatus.PRIVILEGED_CANDIDATE)
        localStringBuffer.append(new StringBuffer(this.m_reg.getString("privilegedToolTip")));
      else if (localObject2 == HitStatus.PRIVILEGED_USER)
        localStringBuffer.append(new StringBuffer(this.m_reg.getString("isPrivilegedToolTip")));
      String str;
      if (((localScheduleTreeLine instanceof AssignmentTreeLine)) && (!((AssignmentTreeLine)localScheduleTreeLine).isActiveUser()))
      {
        str = this.m_reg.getString("nonActiveUserToolTip");
        localStringBuffer.append(". ").append(str);
      }
      if (((localScheduleTreeLine instanceof AssignmentTreeLine)) && (!((AssignmentTreeLine)localScheduleTreeLine).isScheduleMember()))
      {
        str = this.m_reg.getString("nonmemberToolTip");
        localStringBuffer.append(". ").append(str);
      }
    }
    else
    {
      String localObject2 = super.getToolTipText(paramMouseEvent);
      localStringBuffer = new StringBuffer(localObject2 == null ? "" : (String)localObject2);
    }
    String localObject2 = localStringBuffer == null ? "" : localStringBuffer.toString();
    return localObject2;
  }

  public void execute(IGraphCommand paramIGraphCommand)
  {
	//System.out.println("execute paramIGraphCommand");
    try
    {
      switch (paramIGraphCommand.getCommandId())
      {
      case 103:
      case 107:
        if (this.dlg == null)
        {
          this.adapter = new AdapterJFreeChart(paramIGraphCommand);
          this.dlg = new ViewResourceGraphFromAssignToTaskDialog(AIFUtility.getActiveDesktop().getFrame(), this.selectedResource, paramIGraphCommand.getStartDate().getTime(), paramIGraphCommand.getEndDate().getTime(), this.adapter);
          refreshBtnAddMouseListener();
          this.dlg.addWindowListener(new WindowAdapter()
          {
            public void windowClosing(WindowEvent paramAnonymousWindowEvent)
            {
              AssignmentTreeTable.this.dlg.setVisible(false);
              AssignmentTreeTable.this.dlg.disposeDialog();
              AssignmentTreeTable.this.validate();
              AssignmentTreeTable.this.repaint();
              AssignmentTreeTable.this.dlg = null;
            }
          });
        }
        else
        {
          try
          {
            ResourceGraphBasePanel localResourceGraphBasePanel1 = this.dlg.getGraphContainerPanel().getBasePanel();
            localResourceGraphBasePanel1.refreshView(this.adapter, paramIGraphCommand.getStartDate().getTime(), paramIGraphCommand.getEndDate().getTime());
          }
          catch (Exception localException1)
          {
            logger.error(localException1.getClass().getName(), localException1);
          }
        }
        this.dlg.setSize(this.dlg.getWidth(), this.dlg.getHeight());
        this.dlg.setVisible(true);
        break;
      case 104:
        setCursor(Cursor.getPredefinedCursor(0));
        AdapterJFreeChart localAdapterJFreeChart = new AdapterJFreeChart(paramIGraphCommand);
        try
        {
          ResourceGraphBasePanel localResourceGraphBasePanel2 = this.dlg.getGraphContainerPanel().getBasePanel();
          localResourceGraphBasePanel2.refreshView(localAdapterJFreeChart, paramIGraphCommand.getStartDate().getTime(), paramIGraphCommand.getEndDate().getTime());
          validate();
          repaint();
        }
        catch (Exception localException2)
        {
          logger.error(localException2.getClass().getName(), localException2);
        }
      case 150:
        MessageBox.post("Resource Graph exception", "", 150);
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      MessageBox.post(this.m_reg.getString("assignToTask.INVALID_DATE_FOR_RG"), "", 150);
      logger.error(localIllegalArgumentException.getClass().getName(), localIllegalArgumentException);
    }
  }

  public void actionPerformed(ActionEvent paramActionEvent)
  {
	//System.out.println("actionPerformed ActionEvent");
  }

  private void refreshBtnAddMouseListener()
  {
	//System.out.println("refreshBtnAddMouseListener");
    refreshBtnMouseListener = new MouseListener()
    {
      public void mouseClicked(MouseEvent paramAnonymousMouseEvent)
      {
        AssignmentTreeTable.this.refreshDataModel(104);
      }

      public void mousePressed(MouseEvent paramAnonymousMouseEvent)
      {
      }

      public void mouseReleased(MouseEvent paramAnonymousMouseEvent)
      {
      }

      public void mouseEntered(MouseEvent paramAnonymousMouseEvent)
      {
      }

      public void mouseExited(MouseEvent paramAnonymousMouseEvent)
      {
      }
    };
    try
    {
      this.dlg.getGraphContainerPanel().getBasePanel().getRefreshBtn().addMouseListener(refreshBtnMouseListener);
    }
    catch (Exception localException)
    {
      logger.error(localException.getClass().getName(), localException);
    }
  }

  private void refreshDataModel(int paramInt)
  {
	//System.out.println("refreshDataModel paramInt");
    try
    {
      this.resourceContainer.setCommandId(paramInt);
      GraphView.singleton().controller(this.resourceContainer, false);
    }
    catch (Exception localException)
    {
      setCursor(Cursor.getPredefinedCursor(0));
      MessageBox.post(this.m_reg.getString("resourceGraph.data.exception_refresh"), "", 150);
      localException.printStackTrace();
    }
  }

  public void expandSelected()
  {
	//System.out.println("expandSelected");
    Set localSet = findLinesToExpand(getRoot());
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      AIFTreeLine localAIFTreeLine = (AIFTreeLine)localIterator.next();
      expandPath(new TreePath(localAIFTreeLine.getPath()));
    }
  }

  private Set<AIFTreeLine> findLinesToExpand(AIFTreeLine paramAIFTreeLine)
  {
	//System.out.println("findLinesToExpand AIFTreeLine");
    HashSet localHashSet = new HashSet();
    if (((paramAIFTreeLine instanceof AssignmentTreeLine)) && (((AssignmentTreeLine)paramAIFTreeLine).isSelected()))
      localHashSet.add((AIFTreeLine)paramAIFTreeLine.getParent());
    if (paramAIFTreeLine.getChildCount() > 0)
    {
      AIFTreeLine[] arrayOfAIFTreeLine1 = (AIFTreeLine[])paramAIFTreeLine.getChildNodes();
      for (AIFTreeLine localAIFTreeLine : arrayOfAIFTreeLine1)
        localHashSet.addAll(findLinesToExpand(localAIFTreeLine));
    }
    return localHashSet;
  }

  class AssignedPrivilegedCellRenderer extends JPanel
    implements TableCellRenderer
  {
    private JCheckBox checkBox;
    private JRadioButton radioButton;
    private JLabel label;
    private JLabel nonmemberLabel;
    private int strutWidth = 16;
    private int compWidth = 20;

    public AssignedPrivilegedCellRenderer()
    {
      //System.out.println("AssignedPrivilegedCellRenderer");
      setLayout(new BoxLayout(this, 0));
      setOpaque(true);
      this.radioButton = new JRadioButton();
      this.radioButton.setOpaque(false);
      this.radioButton.setEnabled(true);
      this.checkBox = new JCheckBox();
      this.checkBox.setOpaque(false);
      this.checkBox.setEnabled(true);
      this.label = new JLabel(AssignmentTreeTable.this.r.getImageIcon("privilegedMember.ICON"));
      this.label.setOpaque(false);
      this.label.setEnabled(true);
      String str1 = AssignmentTreeTable.this.r.getString("privilegedToolTip");
      this.label.setToolTipText(str1);
      Registry localRegistry = Registry.getRegistry("com.teamcenter.rac.util.util");
      this.nonmemberLabel = new JLabel(localRegistry.getImageIcon("error.ICON"));
      this.nonmemberLabel.setOpaque(false);
      this.nonmemberLabel.setEnabled(true);
      String str2 = AssignmentTreeTable.this.r.getString("nonmemberToolTip");
      this.nonmemberLabel.setToolTipText(str2);
      add(Box.createHorizontalStrut(this.strutWidth));
      add(this.checkBox);
      add(this.radioButton);
      add(this.label);
      add(this.nonmemberLabel);
      this.compWidth = ((int)this.checkBox.getPreferredSize().getWidth());
    }

    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      //System.out.println("getTableCellRendererComponent paramJTable paramObject paramBoolean1 paramBoolean2 paramInt1 paramInt2");
      setBackground(paramBoolean1 ? paramJTable.getSelectionBackground() : Color.white);
      AssignmentTreeLine localAssignmentTreeLine = (AssignmentTreeLine)((AIFTreeTable)paramJTable).getRow(paramInt1);
      this.checkBox.setVisible(AssignmentTreeTable.this.isAssignedCandidate(localAssignmentTreeLine));
      this.checkBox.setSelected(AssignmentTreeTable.this.isAssigned(localAssignmentTreeLine));
      this.radioButton.setVisible(AssignmentTreeTable.this.isPrivilegedCandidate(localAssignmentTreeLine));
      this.radioButton.setSelected(AssignmentTreeTable.this.isPrivilegedUser(localAssignmentTreeLine));
      this.label.setVisible(AssignmentTreeTable.this.isPrivilegedUser(localAssignmentTreeLine));
      if (!localAssignmentTreeLine.isActiveUser())
      {
        this.nonmemberLabel.setVisible(!localAssignmentTreeLine.isActiveUser());
        return this;
      }
      this.nonmemberLabel.setVisible(!localAssignmentTreeLine.isScheduleMember());
      return this;
    }

    public AssignmentTreeTable.HitStatus hitTest(JTable paramJTable, int paramInt1, int paramInt2, Point paramPoint)
    {
      //System.out.println("hitTest paramJTable paramInt1 paramInt2 paramPoint");
      ScheduleTreeLine localScheduleTreeLine = (ScheduleTreeLine)((AIFTreeTable)paramJTable).getRow(paramInt1);
      Rectangle localRectangle = paramJTable.getCellRect(paramInt1, paramInt2, false);
      localRectangle.x += this.strutWidth;
      localRectangle.width = this.compWidth;
      if ((localRectangle.contains(paramPoint)) && (AssignmentTreeTable.this.isAssignedCandidate(localScheduleTreeLine)))
        return AssignmentTreeTable.HitStatus.ASSIGNED_CANDIDATE;
      localRectangle.x += localRectangle.width;
      if ((localRectangle.contains(paramPoint)) && (AssignmentTreeTable.this.isPrivilegedCandidate(localScheduleTreeLine)))
        return AssignmentTreeTable.HitStatus.PRIVILEGED_CANDIDATE;
      localRectangle.x += localRectangle.width;
      if ((localRectangle.contains(paramPoint)) && (AssignmentTreeTable.this.isPrivilegedUser(localScheduleTreeLine)))
        return AssignmentTreeTable.HitStatus.PRIVILEGED_USER;
      return AssignmentTreeTable.HitStatus.NONE;
    }
  }

  private class AssignmentsTreeTableCellRenderer extends JPanel
    implements TableCellRenderer
  {
    JCheckBox check = new JCheckBox();
    AssignmentTreeLine node = null;

    public AssignmentsTreeTableCellRenderer()
    {
      super();
      //System.out.println("AssignmentsTreeTableCellRenderer");
      setOpaque(true);
      this.check.setOpaque(false);
      this.check.setEnabled(true);
      add(this.check, "Center");
    }

    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      //System.out.println("getTableCellRendererComponent paramJTable paramObject paramBoolean1 paramBoolean2 paramInt1 paramInt2");
      if (paramBoolean1)
        setBackground(paramJTable.getSelectionBackground());
      else
        setBackground(Color.white);
      if (paramInt1 <= 0)
      {
        this.check.setSelected(false);
        return this;
      }
      this.node = ((AssignmentTreeLine)((AIFTreeTable)paramJTable).getRow(paramInt1));
      if (this.node != null)
        if (this.node.getSelectStatus() == 1)
        {
          this.check.setSelected(true);
          if (!AssignmentTreeTable.this.memList.contains(this.node.getKey()))
            AssignmentTreeTable.this.memList.add(this.node.getKey());
        }
        else
        {
          this.check.setSelected(false);
          if (((TCComponent)this.node.getUserObject() != null) && (AssignmentTreeTable.this.memList.contains(this.node.getKey())))
            AssignmentTreeTable.this.memList.remove(this.node.getKey());
        }
      return this;
    }
  }

  private static enum HitStatus
  {
    NONE, ASSIGNED_CANDIDATE, PRIVILEGED_CANDIDATE, PRIVILEGED_USER;
  }

  private class MouseEventHandler extends MouseAdapter
  {
    private MouseEventHandler()
    {
    }

    public void mouseClicked(MouseEvent paramMouseEvent)
    {
    }

    public void mousePressed(MouseEvent paramMouseEvent)
    {
      //System.out.println("mousePressed MouseEvent");
      Point localPoint = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
      int i = AssignmentTreeTable.this.columnAtPoint(localPoint);
      int j = AssignmentTreeTable.this.rowAtPoint(localPoint);
      ScheduleTreeLine localScheduleTreeLine = (ScheduleTreeLine)AssignmentTreeTable.this.getRow(j);
      if (j == 0)
        return;
      TCComponent localTCComponent = (TCComponent)localScheduleTreeLine.getUserObject();
      AssignmentTreeTable.this.selectedResource = localTCComponent;
      if ((localTCComponent != null) && ((localTCComponent.getClass() == TCComponentGroup.class) || (localTCComponent.getClass() == TCComponentRole.class)))
        return;
      TableCellRenderer localTableCellRenderer = AssignmentTreeTable.this.getCellRenderer(j, i);
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if ((localTableCellRenderer instanceof AssignmentTreeTable.AssignmentsTreeTableCellRenderer))
      {
        localObject1 = AssignmentTreeTable.this.getCellRect(j, i, false);
        localObject2 = ((AssignmentTreeTable.AssignmentsTreeTableCellRenderer)localTableCellRenderer).getPreferredSize();
        localObject3 = new Rectangle(((Rectangle)localObject1).getLocation(), (Dimension)localObject2);
        if (((Rectangle)localObject3).contains(localPoint))
        {
          localScheduleTreeLine.setSelectStatus(localScheduleTreeLine.getSelectStatus() + 1);
          AssignmentTreeTable.this.repaint();
        }
      }
      else if ((localTableCellRenderer instanceof AssignmentTreeTable.ResourceGraphButtonTblCellRndr))
      {
        if ((localTCComponent != null) && ((localTCComponent instanceof TCComponentDiscipline)))
          return;
        if ((ScheduleDeferredContext.inDeferredSession()) || (AssignmentTreeTable.this.currentRole == null) || (!"Resource Graph Viewers".equals(AssignmentTreeTable.this.currentRole)))
          return;
        if ((!AssignmentTreeTable.this.isForNewTask()) && ((AssignmentTreeTable.this.selectedTask == null) || (!(AssignmentTreeTable.this.selectedTask instanceof TCComponentScheduleTask))))
        {
          MessageBox.post(AssignmentTreeTable.this.m_reg.getString("assignToTask.NO_TASK_SELECT_FOR_RG"), AssignmentTreeTable.this.m_reg.getString("epmtyAssigneeTree.TITLE"), 1);
        }
        else
        {
          AssignmentTreeTable.this.setResourceGraphDatesFromSelectedTask();
          try
          {
            localObject1 = "";
            localObject2 = new String[] { "non-templete-published + " + ((TCComponentUser)AssignmentTreeTable.this.selectedResource).getUserId() };
            localObject3 = new TCComponent[] { AssignmentTreeTable.this.selectedResource };
            TCComponentSchedule[] arrayOfTCComponentSchedule = new TCComponentSchedule[1];
            String str = ResourceGraphBasePanel.getPreference(AssignmentTreeTable.this.selectedResource.getSession(), "scheduling_graph_dataSource", "teamCenterDB");
            int k = str.equalsIgnoreCase("randomSample") ? 107 : 103;
            AssignmentTreeTable.this.resourceContainer = new ResourceContainer(AssignmentTreeTable.class, AssignmentTreeTable.this, AssignmentTreeTable.this.session, (String)localObject1, k, (TCComponent[])localObject3, (String[])localObject2, arrayOfTCComponentSchedule, AssignmentTreeTable.this.taskRangeBegin, AssignmentTreeTable.this.taskRangeEnd);
            GraphView.singleton().controller(AssignmentTreeTable.this.resourceContainer, false);
          }
          catch (Exception localException)
          {
            MessageBox.post(AIFUtility.getActiveDesktop().getFrame(), localException);
            return;
          }
        }
      }
      else if ((localTableCellRenderer instanceof AssignmentTreeTable.AssignedPrivilegedCellRenderer))
      {
        AssignmentTreeTable.HitStatus localHitStatus = ((AssignmentTreeTable.AssignedPrivilegedCellRenderer)localTableCellRenderer).hitTest(AssignmentTreeTable.this, j, i, localPoint);
        if (localHitStatus == AssignmentTreeTable.HitStatus.ASSIGNED_CANDIDATE)
        {
          localScheduleTreeLine.setSelectStatus(localScheduleTreeLine.getSelectStatus() + 1);
          if (!localScheduleTreeLine.isSelected())
            AssignmentTreeTable.this.clearPrivilegedUser(localScheduleTreeLine);
          AssignmentTreeTable.this.repaint();
        }
        else if (localHitStatus == AssignmentTreeTable.HitStatus.PRIVILEGED_CANDIDATE)
        {
          localObject2 = localScheduleTreeLine.getUserObject();
          localObject3 = ((TCComponentUser)localObject2).getUid();
          if (!((String)localObject3).equals(AssignmentTreeTable.this.privilegedUser))
          {
            AssignmentTreeTable.this.privilegedUser = ((String)localObject3);
            AssignmentTreeTable.this.repaint();
          }
        }
      }
    }
  }

  class ResourceGraphButtonTblCellRndr extends JPanel
    implements TableCellRenderer
  {
    JButton iconButton = new JButton(AssignmentTreeTable.this.r.getImageIcon("resourceGraphBT.ICON"));
    ScheduleTreeLine node = null;

    public ResourceGraphButtonTblCellRndr()
    {
      super();
      //System.out.println("ResourceGraphButtonTblCellRndr");
      setOpaque(true);
      this.iconButton.setOpaque(false);
      this.iconButton.setEnabled(true);
      this.iconButton.setMargin(new Insets(0, 0, 0, 0));
      add(this.iconButton, "Center");
    }

    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      //System.out.println("getTableCellRendererComponent paramJTable paramObject paramBoolean2 paramInt1 paramInt2");
      if (paramBoolean1)
        setBackground(paramJTable.getSelectionBackground());
      else
        setBackground(Color.white);
      this.node = ((ScheduleTreeLine)((AIFTreeTable)paramJTable).getRow(paramInt1));
      if (this.node != null)
      {
        Object localObject = this.node.getUserObject();
        if ((ScheduleDeferredContext.inDeferredSession()) || (paramInt1 <= 0) || (AssignmentTreeTable.this.currentRole == null) || (!"Resource Graph Viewers".equals(AssignmentTreeTable.this.currentRole)) || (localObject.getClass() == TCComponentGroup.class) || (localObject.getClass() == TCComponentRole.class) || (localObject.getClass() == TCComponentDiscipline.class))
          this.iconButton.setEnabled(false);
        else
          this.iconButton.setEnabled(true);
      }
      return this;
    }
  }
}