package com.teamcenter.rac.schedule.commands.assigntotask;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.common.AbstractTCCommandDialog;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.schedule.ScheduleViewApplication;
import com.teamcenter.rac.schedule.ScheduleViewApplicationPanel;
import com.teamcenter.rac.schedule.project.scheduling.ModelFactory;
import com.teamcenter.rac.schedule.project.scheduling.ScheduleModel;
import com.teamcenter.rac.schedule.project.scheduling.SchedulingException;
import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
import com.teamcenter.rac.util.ConfirmationDialog;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.VerticalLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.table.TableCellEditor;

public class AssignToTaskDialog extends AbstractTCCommandDialog
{
  public JPanel assignTaskPanel;
  public JPanel topPanel;
  private JButton updateBT;
  private JButton applyBT;
  private JRadioButton addAssignmentsRadioBtn;
  private JRadioButton overwriteAssignmentsRadioBtn;
  private GridBagLayout gridBag;
  private ScheduleViewApplicationPanel schPanel = null;
  private AbstractAIFApplication theApp;
  private Frame frame;
  private AbstractAIFCommand cmd;
  private String[] typeNames = null;
  private Registry r = Registry.getRegistry(this);
  private boolean newTaskAssignment = false;
  private TreeTablePanel treeTablePanel = null;
  private static Registry reg = Registry.getRegistry("com.teamcenter.rac.schedule.commands.assigntotask.assigntotask");
  public ArrayList<TCComponent> tasks = new ArrayList();
  public static boolean updateNonCompTask = false;
  public static boolean containCompleteTask = false;
  public static boolean toViewAssingment = false;
  public static boolean containsOnlyCompTask = true;

  public AssignToTaskDialog(Frame paramFrame, AbstractAIFCommand paramAbstractAIFCommand, AbstractAIFApplication paramAbstractAIFApplication, boolean paramBoolean)
  {
	
    super(paramFrame, paramAbstractAIFCommand);
    //System.out.println("AssignToTaskDialog  paramFrame paramAbstractAIFCommand paramAbstractAIFApplication paramBoolean");
    this.theApp = paramAbstractAIFApplication;
    this.cmd = paramAbstractAIFCommand;
    setTitle(this.r.getString("assignTaskDialog.TITLE"));
    setResizable(false);
    this.newTaskAssignment = paramBoolean;
    initUI();
    if (!((ScheduleViewApplicationPanel)((ScheduleViewApplication)paramAbstractAIFApplication).getApplicationPanel()).isScheduleModifiable())
    {
      this.updateBT.setEnabled(false);
      this.applyBT.setEnabled(false);
    }
    centerToScreen();
  }

  public AbstractAIFSession getSession()
  {
    return ((ScheduleViewApplication)this.theApp).getSession();
  }

  public AbstractAIFApplication getApplication()
  {
    return this.theApp;
  }

  public AbstractAIFCommand getAbstractAIFCmd()
  {
    return this.cmd;
  }

  public boolean getIsNewTaskAssignment()
  {
    return this.newTaskAssignment;
  }

  public void initUI()
  {
	//System.out.println("initUI ");
    this.schPanel = ((ScheduleViewApplicationPanel)((ScheduleViewApplication)this.theApp).getApplicationPanel());
    final ArrayList localArrayList = this.schPanel.getSelectedComponents();
    try
    {
      loadSchMemberDataInAssignToTaskDlg(this.schPanel.getCurrentProject().getUid());
    }
    catch (SchedulingException localSchedulingException)
    {
      MessageBox.post(this.r.getString("loadSchMemberInAssignToTaskDlg.MSG"), this.r.getString("loadSchMemberInAssignToTaskDlg.TITLE"), 4);
    }
    setUpdateNonCompTask(false);
    setToViewAssingment(false);
    setContainCompleteTask(false);
    setContainsOnlyCompTask(true);
    if ((!this.newTaskAssignment) && (localArrayList != null) && (localArrayList.size() > 0))
      if (localArrayList.size() == 1)
      {
        try
        {
          TCComponent localTCComponent = (TCComponent)localArrayList.get(0);
          if (((TaskHelper.isTaskTriggered(localTCComponent)) && (!TaskHelper.isTaskComplete(localTCComponent))) || ((TaskHelper.isMsIntegLink(localTCComponent)) && (!TaskHelper.isTaskComplete(localTCComponent))))
            setUpdateNonCompTask(true);
          else
            updateOneSelectedComp(localArrayList, reg);
        }
        catch (TCException localTCException)
        {
          localTCException.printStackTrace();
        }
      }
      else
      {
        updateSelectedComp(localArrayList, reg);
        if ((!getContainCompleteTask()) && (!getContainsOnlyCompTask()))
          setUpdateNonCompTask(true);
        if (getContainsOnlyCompTask())
          return;
      }
    super.initUI();
    this.gridBag = new GridBagLayout();
    this.assignTaskPanel = new JPanel();
    this.topPanel = new JPanel(this.gridBag);
    String str = null;
    if (localArrayList != null)
      str = localArrayList.toString();
    if ((str != null) && (str.length() > 0))
      this.topPanel.setBorder(BorderFactory.createTitledBorder(this.r.getString("assignMember") + ": " + str));
    else
      this.topPanel.setBorder(BorderFactory.createTitledBorder(this.r.getString("assignMember")));
    boolean bool = true;
    if (localArrayList.size() > 1)
    {
      bool = false;
      this.treeTablePanel = new TreeTablePanel(this.schPanel, this.newTaskAssignment, bool);
    }
    else
    {
      this.treeTablePanel = new TreeTablePanel(this.schPanel, this.newTaskAssignment, bool);
    }
    JScrollPane localJScrollPane = new JScrollPane();
    localJScrollPane.setPreferredSize(new Dimension(450, 300));
    this.gridBag.setConstraints(localJScrollPane, new GridBagConstraints(0, 0, 2, 1, 0.0D, 0.0D, 10, 0, new Insets(10, 0, 0, 5), 0, 0));
    localJScrollPane.setBackground(Color.WHITE);
    localJScrollPane.setViewportView(this.treeTablePanel.getTreeTable());
    localJScrollPane.setSize(this.topPanel.getSize());
    this.topPanel.add(localJScrollPane);
    this.assignTaskPanel.add(this.topPanel);
    ButtonGroup localButtonGroup = new ButtonGroup();
    this.addAssignmentsRadioBtn = new JRadioButton();
    this.addAssignmentsRadioBtn.setText(this.r.getString("addAssignmentsRadioBtn"));
    this.addAssignmentsRadioBtn.setSelected(true);
    this.addAssignmentsRadioBtn.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent paramAnonymousItemEvent)
      {
        AssignToTaskDialog.this.treeTablePanel.setPrivilegedUserNeeded(paramAnonymousItemEvent.getStateChange() == 2);
      }
    });
    this.overwriteAssignmentsRadioBtn = new JRadioButton();
    this.overwriteAssignmentsRadioBtn.setText(this.r.getString("overwriteAssignmentsRadioBtn"));
    this.overwriteAssignmentsRadioBtn.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent paramAnonymousItemEvent)
      {
        AssignToTaskDialog.this.treeTablePanel.setPrivilegedUserNeeded(paramAnonymousItemEvent.getStateChange() == 1);
      }
    });
    localButtonGroup.add(this.addAssignmentsRadioBtn);
    localButtonGroup.add(this.overwriteAssignmentsRadioBtn);
    JPanel localJPanel = new JPanel(new VerticalLayout());
    localJPanel.setBorder(BorderFactory.createTitledBorder(this.r.getString("BtPanelText")));
    localJPanel.add("top", this.addAssignmentsRadioBtn);
    localJPanel.add("bottom", this.overwriteAssignmentsRadioBtn);
    if (localArrayList.size() > 1)
    {
      this.topPanel.add(localJPanel, new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 21, 2, new Insets(1, 1, 1, 1), 0, 0));
      this.treeTablePanel.setPrivilegedUserNeeded(true);
    }
    this.mainPanel.add(this.assignTaskPanel);
    this.buttonPanel.remove(this.okButton);
    this.buttonPanel.remove(this.applyButton);
    this.buttonPanel.remove(this.cancelButton);
    this.applyBT = new JButton(this.applyButton.getText());
    this.applyBT.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
    	//System.out.println("applyBT  actionPerformed");
        boolean bool = false;
        if (localArrayList.size() == 1)
        {
          AssignToTaskDialog.this.treeTablePanel.saveAssignments((TCComponent)localArrayList.get(0));
        }
        else
        {
          if (AssignToTaskDialog.this.addAssignmentsRadioBtn.isSelected())
            AssignToTaskDialog.this.treeTablePanel.saveMultiTaskAssignments(localArrayList, bool);
          if (AssignToTaskDialog.this.overwriteAssignmentsRadioBtn.isSelected())
          {
            bool = true;
            AssignToTaskDialog.this.treeTablePanel.saveMultiTaskAssignments(localArrayList, bool);
          }
        }
        AbstractAIFSession localAbstractAIFSession = AssignToTaskDialog.this.getSession();
        AssignToTaskDialog.this.disposeDialog();
        AssignToTaskCommand localAssignToTaskCommand = new AssignToTaskCommand(localAbstractAIFSession);
        localAssignToTaskCommand.run();
      }
    });
    this.applyBT.addFocusListener(new FocusListener()
    {
      public void focusGained(FocusEvent paramAnonymousFocusEvent)
      {
        AssignmentTreeTable localAssignmentTreeTable = AssignToTaskDialog.this.getTreeTablePanel().getTreeTable();
        int i = localAssignmentTreeTable.getEditingRow();
        int j = localAssignmentTreeTable.getEditingColumn();
        TableCellEditor localTableCellEditor;
        if ((i > -1) && (j > -1) && ((localTableCellEditor = localAssignmentTreeTable.getCellEditor(i, j)) != null))
          localTableCellEditor.stopCellEditing();
      }

      public void focusLost(FocusEvent paramAnonymousFocusEvent)
      {
      }
    });
    this.applyBT.addMouseListener(new MouseListener()
    {
      public void mouseClicked(MouseEvent paramAnonymousMouseEvent)
      {
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
    });
    if (localArrayList.size() > 1)
      this.applyBT.setEnabled(false);
    this.updateBT = new JButton(this.okButton.getText());
    if (getToViewAssingment())
    {
      this.applyBT.setEnabled(false);
      this.updateBT.setEnabled(false);
    }
    this.updateBT.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        boolean bool = false;
        if (localArrayList.size() == 1)
        {
          AssignToTaskDialog.this.treeTablePanel.saveAssignments((TCComponent)localArrayList.get(0));
        }
        else
        {
          if (AssignToTaskDialog.this.addAssignmentsRadioBtn.isSelected())
            AssignToTaskDialog.this.treeTablePanel.saveMultiTaskAssignments(localArrayList, bool);
          if (AssignToTaskDialog.this.overwriteAssignmentsRadioBtn.isSelected())
          {
            bool = true;
            AssignToTaskDialog.this.treeTablePanel.saveMultiTaskAssignments(localArrayList, bool);
          }
        }
        AssignToTaskDialog.this.setVisible(false);
        AssignToTaskDialog.this.dispose();
      }
    });
    this.updateBT.addFocusListener(new FocusListener()
    {
      public void focusGained(FocusEvent paramAnonymousFocusEvent)
      {
        AssignmentTreeTable localAssignmentTreeTable = AssignToTaskDialog.this.getTreeTablePanel().getTreeTable();
        int i = localAssignmentTreeTable.getEditingRow();
        int j = localAssignmentTreeTable.getEditingColumn();
        TableCellEditor localTableCellEditor;
        if ((i > -1) && (j > -1) && ((localTableCellEditor = localAssignmentTreeTable.getCellEditor(i, j)) != null))
          localTableCellEditor.stopCellEditing();
      }

      public void focusLost(FocusEvent paramAnonymousFocusEvent)
      {
      }
    });
    this.updateBT.addMouseListener(new MouseListener()
    {
      public void mouseClicked(MouseEvent paramAnonymousMouseEvent)
      {
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
    });
    this.cancelButton = new JButton(this.cancelButton.getText());
    this.cancelButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        AssignToTaskDialog.this.validateTimer.stop();
        AssignToTaskDialog.this.setVisible(false);
        AssignToTaskDialog.this.dispose();
      }
    });
    this.buttonPanel.add(this.updateBT);
    this.buttonPanel.add(this.applyBT);
    this.buttonPanel.add(this.cancelButton);
    pack();
    this.cancelButton.requestFocusInWindow();
  }

  private void loadSchMemberDataInAssignToTaskDlg(String paramString)
    throws SchedulingException
  {
	  //System.out.println("loadSchMemberDataInAssignToTaskDlg paramString");
    try
    {
      ModelFactory.singleton(this.schPanel.getApplicationSession()).getScheduleModel().loadMemberDataEntries(paramString);
    }
    catch (SchedulingException localSchedulingException)
    {
      throw localSchedulingException;
    }
  }

  public String[] getTypeNames()
  {
	//System.out.println("getTypeNames");
    if (this.typeNames == null)
      this.typeNames = Registry.getRegistry(this).getStringArray("scheduleMember.TYPES");
    return this.typeNames;
  }

  public void startCommandOperation()
  {
  }

  public boolean showCloseButton()
  {
    return false;
  }

  public void stopPressed()
  {
  }

  public boolean isPerformable()
  {
    return true;
  }

  public boolean isNewTaskAssignment()
  {
    return this.newTaskAssignment;
  }

  public TreeTablePanel getTreeTablePanel()
  {
    return this.treeTablePanel;
  }

  public void setTopPanelBorder(String paramString)
  {
    this.topPanel.setBorder(BorderFactory.createTitledBorder(this.r.getString("assignMember") + ": " + paramString));
  }

  private static boolean isSelectionContainsCompTask(ArrayList<TCComponent> paramArrayList)
  {
	//System.out.println("isSelectionContainsCompTask paramArrayList");
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      TCComponent localTCComponent = (TCComponent)localIterator.next();
      try
      {
        if ((TaskHelper.isTaskComplete(localTCComponent)) || (TaskHelper.isTaskTriggered(localTCComponent)) || (TaskHelper.isMsIntegLink(localTCComponent)))
        {
          setContainCompleteTask(true);
          return getContainCompleteTask();
        }
      }
      catch (TCException localTCException)
      {
        localTCException.printStackTrace();
      }
    }
    return getContainCompleteTask();
  }

  private static boolean isSelectionContainsOnlyCompTask(ArrayList<TCComponent> paramArrayList)
  {
	//System.out.println("isSelectionContainsOnlyCompTask paramArrayList");
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      TCComponent localTCComponent = (TCComponent)localIterator.next();
      try
      {
        if ((!TaskHelper.isTaskComplete(localTCComponent)) && (!TaskHelper.isTaskTriggered(localTCComponent)) && (!TaskHelper.isMsIntegLink(localTCComponent)))
        {
          setContainsOnlyCompTask(false);
          return getContainsOnlyCompTask();
        }
      }
      catch (TCException localTCException)
      {
        localTCException.printStackTrace();
      }
    }
    return getContainsOnlyCompTask();
  }

  public static ArrayList<TCComponent> removeCompletedTask(ArrayList<TCComponent> paramArrayList)
  {
	//System.out.println("removeCompletedTask paramArrayList");
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      TCComponent localTCComponent = (TCComponent)localIterator.next();
      try
      {
        if ((TaskHelper.isTaskComplete(localTCComponent)) || (TaskHelper.isTaskTriggered(localTCComponent)) || (TaskHelper.isMsIntegLink(localTCComponent)))
          localArrayList.add(localTCComponent);
      }
      catch (TCException localTCException)
      {
        localTCException.printStackTrace();
      }
    }
    paramArrayList.removeAll(localArrayList);
    return paramArrayList;
  }

  public static void updateSelectedComp(ArrayList<TCComponent> paramArrayList, Registry paramRegistry)
  {
	//System.out.println("updateSelectedComp paramArrayList paramRegistry");
    if (isSelectionContainsOnlyCompTask(paramArrayList))
    {
      msgTaskCompleted(paramRegistry);
      return;
    }
    if (isSelectionContainsCompTask(paramArrayList))
    {
      removeCompletedTask(paramArrayList);
      String str1 = paramRegistry.getString("tskNameInfo");
      String str2 = str1 + paramArrayList.toString();
      updateNonCompTask = ConfirmationDialog.post(null, paramRegistry.getString("statusCompletedInfo.TITLE"), MessageFormat.format(paramRegistry.getString("statusCompletedMultitask.MSG"), new Object[] { "" }), str2, null) == 2;
      if (!updateNonCompTask);
    }
  }

  public static void updateOneSelectedComp(ArrayList<TCComponent> paramArrayList, Registry paramRegistry)
  {
	//System.out.println("updateOneSelectedComp paramArrayList paramRegistry");
    setUpdateNonCompTask(true);
    if (isSelectionContainsCompTask(paramArrayList))
    {
      toViewAssingment = ConfirmationDialog.post(paramRegistry.getString("statusCompletedInfo.TITLE"), MessageFormat.format(paramRegistry.getString("statusCompletedSingletaskToviewAss.MSG"), new Object[] { "" })) == 2;
      if (!toViewAssingment)
        setUpdateNonCompTask(false);
      return;
    }
  }

  public static void msgTaskCompleted(Registry paramRegistry)
  {
	//System.out.println("msgTaskCompleted paramRegistry");
    MessageBox localMessageBox = null;
    localMessageBox = new MessageBox(paramRegistry.getString("statusCompletedSingletask.MSG"), paramRegistry.getString("statusCompletedInfo.TITLE"), 2);
    localMessageBox.setModal(true);
    localMessageBox.setVisible(true);
  }

  public static void setUpdateNonCompTask(boolean paramBoolean)
  {
    updateNonCompTask = paramBoolean;
  }

  public static boolean getUpdateNonCompTask()
  {
    return updateNonCompTask;
  }

  public static void setToViewAssingment(boolean paramBoolean)
  {
    toViewAssingment = paramBoolean;
  }

  public static boolean getToViewAssingment()
  {
    return toViewAssingment;
  }

  public static boolean getContainsOnlyCompTask()
  {
    return containsOnlyCompTask;
  }

  public static void setContainsOnlyCompTask(boolean paramBoolean)
  {
    containsOnlyCompTask = paramBoolean;
  }

  public static boolean getContainCompleteTask()
  {
    return containCompleteTask;
  }

  public static void setContainCompleteTask(boolean paramBoolean)
  {
    containCompleteTask = paramBoolean;
  }

  public void setVisible(boolean paramBoolean)
  {
	//System.out.println("setVisible paramBoolean");
    if (paramBoolean)
      this.treeTablePanel.expandSelected();
    super.setVisible(paramBoolean);
  }
}