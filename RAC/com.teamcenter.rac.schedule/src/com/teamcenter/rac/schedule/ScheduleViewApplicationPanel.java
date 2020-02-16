package com.teamcenter.rac.schedule;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.AIFTreeLine;
import com.teamcenter.rac.aif.common.AIFTreeTableModelAdapter;
import com.teamcenter.rac.aif.kernel.AIFComponentChangeEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.AIFComponentDeleteEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentEvent;
import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.aif.kernel.IOperationService;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.commands.namedreferences.ImportFilesOperation;
import com.teamcenter.rac.common.MRUButton;
import com.teamcenter.rac.common.lov.LOVComboBox;
import com.teamcenter.rac.kernel.InterfaceComponentLine;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentChangeItemRevision;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFnd0ProxyTask;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentManager;
import com.teamcenter.rac.kernel.TCComponentResourceAssignment;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleDeliverable;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentTaskDeliverable;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.commands.deferred.DeferredSessionPanel;
import com.teamcenter.rac.schedule.commands.deferred.ReleaseDeferred;
import com.teamcenter.rac.schedule.commands.deferred.ScheduleDeferredContext;
import com.teamcenter.rac.schedule.commands.deferred.ScheduleLockedContext;
import com.teamcenter.rac.schedule.common.actions.OpenSubScheduleAction;
import com.teamcenter.rac.schedule.common.gantt.JGanttChart;
import com.teamcenter.rac.schedule.common.tree.ColumnIdentifier;
import com.teamcenter.rac.schedule.common.tree.RowHeader;
import com.teamcenter.rac.schedule.common.tree.ScheduleTreeLine;
import com.teamcenter.rac.schedule.common.tree.ScheduleTreeTable;
import com.teamcenter.rac.schedule.common.tree.ScheduleTreeTableModel;
import com.teamcenter.rac.schedule.common.tree.TaskRowNumberManager;
import com.teamcenter.rac.schedule.common.util.ProgramUtil;
import com.teamcenter.rac.schedule.common.util.ProxyTaskComparator;
import com.teamcenter.rac.schedule.common.util.ScheduleUtil;
import com.teamcenter.rac.schedule.common.util.SchedulingExceptionHandler;
import com.teamcenter.rac.schedule.model.helpers.TaskModelHelper;
import com.teamcenter.rac.schedule.plugin.Activator;
import com.teamcenter.rac.schedule.project.common.gui.HoursFormatter;
import com.teamcenter.rac.schedule.project.scheduling.ModelFactory;
import com.teamcenter.rac.schedule.project.scheduling.ProgramMetaDataContainer;
import com.teamcenter.rac.schedule.project.scheduling.ProgramPreferencesUtil;
import com.teamcenter.rac.schedule.project.scheduling.ReferenceIDContainer;
import com.teamcenter.rac.schedule.project.scheduling.SchedulingEnginePrefs;
import com.teamcenter.rac.schedule.project.scheduling.SchedulingException;
import com.teamcenter.rac.schedule.project.server.RACInterface.RACInterface;
import com.teamcenter.rac.schedule.project.server.RACInterface.RACInterfaceFactory;
import com.teamcenter.rac.schedule.project.server.dataInterface.LoadScheduleOptions;
import com.teamcenter.rac.schedule.project.sharedUtils.TimeZoneCalUtil;
import com.teamcenter.rac.schedule.scheduler.componentutils.ScheduleHelper;
import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
import com.teamcenter.rac.schedule.soainterface.CreateScheduleTaskRunner;
import com.teamcenter.rac.schedule.soainterface.RecalculateScheduleNonInteractiveRunner;
import com.teamcenter.rac.schedule.svc.NewTaskRequest;
import com.teamcenter.rac.services.IAspectService;
import com.teamcenter.rac.services.ISessionService;
import com.teamcenter.rac.services.ITogglePropertiesService;
import com.teamcenter.rac.util.ConfirmationDialog;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.InterfaceSignalOnClose;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.OSGIUtil;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.event.ClientEventDispatcher;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.services.internal.rac.projectmanagement._2007_06.ScheduleManagement.LoadProgramViewResponse;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2007_06.DataManagement.RelationAndTypesFilter;
import com.teamcenter.services.rac.core._2007_09.DataManagement.ExpandGRMRelationsData2;
import com.teamcenter.services.rac.core._2007_09.DataManagement.ExpandGRMRelationsOutput2;
import com.teamcenter.services.rac.core._2007_09.DataManagement.ExpandGRMRelationsPref2;
import com.teamcenter.services.rac.core._2007_09.DataManagement.ExpandGRMRelationsResponse2;
import com.teamcenter.services.rac.core._2007_09.DataManagement.ExpandGRMRelationship;
import com.teamcenter.services.rac.projectmanagement._2012_02.ScheduleManagement.AttributeUpdateContainer;
import com.teamcenter.services.rac.projectmanagement._2012_02.ScheduleManagement.CreatedObjectsContainer;
import com.teamcenter.services.rac.projectmanagement._2012_02.ScheduleManagement.TaskCreateContainer;
import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;


public class ScheduleViewApplicationPanel extends JPanel
  implements InterfaceAIFComponentEventListener, InterfaceSignalOnClose, PropertyChangeListener, MouseListener
{
  final Registry r = Registry.getRegistry("com.teamcenter.rac.schedule.schedule");
  private Frame frame;
  private AbstractAIFUIApplication app;
  protected TCSession session;
  private JSplitPane splitPane;
  private ScheduleTreeTableModel treetableModel = null;
  private ScheduleTreeLine rootSchTreeLine = null;
  private JScrollPane treeScrollPane;
  private JScrollBar treeScrollPaneVertScrollBar;
  private MouseAdapter treeScrollPaneVertScrollBarMouseAdapter;
  private KeyListener treeTableKeyListener;
  private JPanel schPanel;
  private JPanel tskPanel;
  private JPanel ganttPanel;
  private JScrollPane ganttChartScrollPane;
  private JGanttChart ganttChart;
  private ScheduleTreeTable treeTable = null;
  private VIEWTYPES currentViewType = VIEWTYPES.PROGRAMAUTHOR;
  private String taskName = null;
  protected int workTask = 0;
  private JTextField durationField = null;
  private int workEstimateMinutes = 480;
  private ProgramMetaDataContainer programContainer;
  private ProgramMetaDataContainer transientProgramContainer;
  private JPanel shortcutPanel;
  private Point selectedPoint;
  private HashMap<TCComponent, List<TCComponent>> proxyMap = null;
  private iTextField taskField = null;
  LOVComboBox gateTypeLOVComboBox;
  protected AIFDesktop desktop = null;
  private MRUButton mruButton;
  private JButton createTaskBT;
  private TCComponentDataset currentProgramView = null;
  LoadProgramViewResponse programViewResponse = null;
  private String pgmViewXMLData = null;
  private String xmlContent = null;
  private boolean supressExpansionEvent = false;
  public List<String> expandedTreeObjects;
  private List<InterfaceAIFComponent> openedComponents;
  private static final String taskObjectTypePreference = "ScheduleTaskClassNameToCreate";
  private static final Logger logger = Logger.getLogger(ScheduleViewApplicationPanel.class);
  public static final int STRUCTURE_PARTIAL_CONTEXT_UNDEFINED = -1;
  public static final int STRUCTURE_PARTIAL_CONTEXT_DEFAULT = 0;
  public static final int STRUCTURE_PARTIAL_CONTEXT_SCHSUMMARY = 1;
  public static final int STRUCTURE_PARTIAL_CONTEXT_SCHSUMMARYVIEW = 2;
  public static final int STRUCTURE_PARTIAL_CONTEXT_ALL = 3;
  public static final int STRUCTURE_PARTIAL_CONTEXT_MAX = 1;
  private boolean structureAddingSiblings = false;
  public static final int STRUCTURE_LOAD_CONTEXT_DEFAULT = 0;
  public static final int STRUCTURE_LOAD_CONTEXT_SUBSCHEDULE = 1;
  public static final int STRUCTURE_LOAD_CONTEXT_SUMMARYTASK = 2;
  public static final int STRUCTURE_LOAD_CONTEXT_ALL_SUMMARIES = 3;
  public static final int STRUCTURE_LOAD_CONTEXT_INSERT_REFERENCE = 4;
  public static final int STRUCTURE_LOAD_CONTEXT_INSERT_COPY = 5;
  public static final int STRUCTURE_LOAD_CONTEXT_REFRESH = 6;
  public static final int STRUCTURE_LOAD_CONTEXT_SYMMETRIC_DIFERENCE = 7;
  public static final int STRUCTURE_LOAD_CONTEXT_RELOAD = 8;
  public static final int STRUCTURE_LOAD_CONTEXT_BASELINE = 9;
  public static final int STRUCTURE_UNLOAD_CONTEXT_NONE = 0;
  public static final int STRUCTURE_UNLOAD_CONTEXT_SCHSUMMARY = 1;
  public static final int STRUCTURE_UNLOAD_CONTEXT_SUMMARY = 2;
  public static final int STRUCTURE_UNLOAD_CONTEXT_PHASEGATESUMMARY = 3;
  public static final int STRUCTURE_CLIENT_CONTEXT_RAC = 0;
  public static final int STRUCTURE_CLIENT_CONTEXT_API = 1;
  public static final int STRUCTURE_CLIENT_CONTEXT_MSP_PLUGIN = 2;
  public static final String pref_partial_context = "SM_Structure_Partial_Context";
  public static final String pref_load_context = "SM_Structure_Load_Context";
  public static final String pref_client_context = "SM_Structure_Client_Context";
  public static final String pref_unload_context = "SM_Structure_Unload_Context";
  private int instance_pref_partial_context = -1;
  private static HashMap<String, Integer> loadScheduleoptions;
  private TCComponentSchedule currentSchedule = null;
  private TCComponentSchedule previouslyOpenedSchedule = null;
  private TCComponentSchedule subSchedule = null;
  private boolean isSubSchedule = false;
  private String viewID = null;
  private Date lastDateModified;
  private boolean startDeferredMode = false;
  private boolean doRecalc = false;
  public static final String start_deferred_mode = "SM_START_DEFERRED_MODE";
  public static final String pref_run_data_inconsistency_check = "SM_Run_Data_Inconsistency_Checks";
  private final int EVENT_TYPE_KEY = 0;
  private final int EVENT_TYPE_MOUSE = 1;
  private boolean isTaskBarForSS = false;
  private static String ITEM_SUB_TYPE_PROP = "item_sub_type";
  private static String ITEM_SUB_TYPE_SSS0JOBCARD = "SSS0JobTask";
  private static String SS_ACTIVITY_EXECUTION_TYPE_PROP = "fnd0activityExecutionType";
  private static String SS_ACTIVITY_EXECUTION_TYPE_VALUE = "Smr0Perform";
  private static String SS_JOB_ACTIVITY_SCHEDULE_REL = "CMHasWorkBreakdown";
  private static String SCHEDULE_TASK_REVISION_TYPE = "ScheduleTaskRevisionType";
  public ReferenceIDContainer newTaskRefIdCont = null;
  private static volatile int ganttViewRectNumberOfRows = 12;
  protected boolean durationInEditMode = false;
  private static volatile int ganttRowCounter = 0;
  protected boolean taskInEditMode = false;
  private static volatile boolean scrollBarWasAdjusting = false;
  protected boolean systemMsgIsShowing = true;
  public Memento memento;
  private volatile int splilPaneDividerLocation;
  private  int DIVIDER_SET_BY_WEIGHTS = 0;
  private  int DIVIDER_DRAGGED = 1;
  private volatile int splitPanePropertyChangeContext;
  private JPanel mainPanel;
  private TCComponentDynamicTaskLine taskLine;
  private int loadSchedulePending;
  boolean canDrag;
  boolean determineState;
  public static final String OPEN_OBJ_PROP = "OpenSchedulingObject";
  public static final String OPEN_PROGRAM_VIEW = "ProgramView";
  public static final String OPEN_SCHEDULE = "Schedule";
  public static final String OPEN_GENERIC = "Generic";
  public Job jobRedraw;
  private Runnable runExpand;

  public ScheduleViewApplicationPanel(AbstractAIFUIApplication paramAbstractAIFUIApplication)
  {
    super(new BorderLayout());
    getClass();
    this.memento = new Memento();
    this.splilPaneDividerLocation = 0;
    this.DIVIDER_SET_BY_WEIGHTS = 0;
    this.DIVIDER_DRAGGED = 1;
    this.splitPanePropertyChangeContext = 1;
    this.mainPanel = null;
    this.taskLine = null;
    this.loadSchedulePending = 0;
    this.canDrag = false;
    this.determineState = false;
    this.jobRedraw = new Job("thread.job.name.redraw")
    {
      protected IStatus run(IProgressMonitor paramAnonymousIProgressMonitor)
      {
        AIFTreeLine[] arrayOfAIFTreeLine = null;
        if (ScheduleViewApplicationPanel.this.treeTable != null)
          arrayOfAIFTreeLine = ScheduleViewApplicationPanel.this.treeTable.getSelectedLines();
        SwingUtilities.invokeLater(ScheduleViewApplicationPanel.this.runExpand);
        if ((ScheduleViewApplicationPanel.this.treeTable != null) && (arrayOfAIFTreeLine != null))
          ScheduleViewApplicationPanel.this.treeTable.setSelected(arrayOfAIFTreeLine);
        return Status.OK_STATUS;
      }
    };
    this.runExpand = new Runnable()
    {
      public void run()
      {
        if (ScheduleViewApplicationPanel.this.newTaskRefIdCont == null)
          return;
        boolean bool = ScheduleViewApplicationPanel.this.getSupressExpansionEvent();
        ScheduleViewApplicationPanel.this.setSupressExpansionEvent(false);
        if (ScheduleViewApplicationPanel.this.runExpand() == null)
        {
          ScheduleViewApplicationPanel.this.setSupressExpansionEvent(bool);
          ScheduleViewApplicationPanel.this.jobRedraw.setPriority(50);
          ScheduleViewApplicationPanel.this.jobRedraw.schedule(50L);
        }
        ScheduleViewApplicationPanel.this.newTaskRefIdCont = null;
      }
    };
    this.app = ScheduleViewApplication.getApplication();
    this.proxyMap = new HashMap();
    this.session = ((TCSession)this.app.getSession());
    Activator.getDefault().addTogglePropertyChangeListener(this);
    loadScheduleoptions = new HashMap();
    this.treeScrollPane = new JScrollPane();
    this.treeScrollPane.getVerticalScrollBar().addAdjustmentListener(new TreeAdjustmentListener());
    this.treeScrollPaneVertScrollBar = this.treeScrollPane.getVerticalScrollBar();
    this.treeScrollPaneVertScrollBarMouseAdapter = createTreeScrollPaneVerticalScrollBarMouseAdapter();
    this.treeScrollPaneVertScrollBar.addMouseListener(this);
    this.shortcutPanel = new JPanel(new FlowLayout(0, 10, 5));
    this.schPanel = new JPanel(new BorderLayout());
    this.schPanel.setMinimumSize(new Dimension(50, 50));
    this.schPanel.setBackground(Color.GRAY);
    this.schPanel.addMouseListener(this);
    this.tskPanel = new JPanel(new FlowLayout(0, 10, 5));
    JLabel localJLabel1 = new JLabel("           ");
    localJLabel1.setOpaque(true);
    localJLabel1.setPreferredSize(new Dimension(100, 15));
    JLabel localJLabel2 = new JLabel(this.r.getString("TaskName"));
    this.taskField = new iTextField(this.r.getString("TaskFieldText"), 20);
    this.taskField.setLengthLimit(128);
    this.taskField.setForeground(new Color(181, 166, 180));
    this.taskField.setToolTipText(this.r.getString("TaskFieldToolTip"));
    JLabel localJLabel3 = new JLabel(this.r.getString("workLabelName"));
    this.taskField.setEditable(false);
    localJLabel3.setOpaque(true);
    this.durationField = new JTextField(8);
    this.durationField.setText(HoursFormatter.format(this.workEstimateMinutes));
    this.durationField.setEditable(false);
    this.durationField.setToolTipText(this.r.getString("durationFieldToolTip"));
    this.createTaskBT = new JButton(this.r.getString("createTaskBT"));
    this.createTaskBT.setEnabled(false);
    JLabel gateTypelabel = new JLabel("Gate/Task Type");
    
    TCComponentListOfValues lovs_taskType = TCComponentListOfValuesType.findLOVByName(session, "JCI6_TaskType");
    this.gateTypeLOVComboBox =  new LOVComboBox(lovs_taskType);
    this.gateTypeLOVComboBox.setMandatory(true);
    this.tskPanel.add(localJLabel2);
    this.tskPanel.add(this.taskField);
    this.tskPanel.add(localJLabel3);
    this.tskPanel.add(this.durationField);
    this.tskPanel.add(gateTypelabel);
    this.tskPanel.add(this.gateTypeLOVComboBox);
    this.tskPanel.add(this.createTaskBT);
    this.taskField.addMouseListener(new MouseAdapter()
    {
      public void mouseReleased(MouseEvent paramAnonymousMouseEvent)
      {
        if ((ScheduleViewApplicationPanel.this.taskField.isEditable()) && (ScheduleViewApplicationPanel.this.taskField.getText().compareTo(ScheduleViewApplicationPanel.this.r.getString("TaskFieldText")) == 0))
        {
          ScheduleViewApplicationPanel.this.taskField.setText("");
          ScheduleViewApplicationPanel.this.taskField.setForeground(new Color(0, 0, 0));
          ScheduleViewApplicationPanel.this.taskField.requestFocus();
        }
      }
    });
    this.taskField.addFocusListener(new FocusAdapter()
    {
      public void focusLost(FocusEvent paramAnonymousFocusEvent)
      {
        ScheduleViewApplicationPanel.this.taskInEditMode = false;
        if ((ScheduleViewApplicationPanel.this.taskField.getText().trim().length() == 0) || (ScheduleViewApplicationPanel.this.systemMsgIsShowing))
        {
          ScheduleViewApplicationPanel.this.taskField.setText(ScheduleViewApplicationPanel.this.r.getString("TaskFieldText"));
          ScheduleViewApplicationPanel.this.taskField.setForeground(new Color(192, 180, 191));
          ScheduleViewApplicationPanel.this.createTaskBT.setEnabled(false);
        }
      }

      public void focusGained(FocusEvent paramAnonymousFocusEvent)
      {
        ScheduleViewApplicationPanel.this.taskInEditMode = true;
        ScheduleViewApplicationPanel.this.taskField.setEnabled(true);
        ScheduleViewApplicationPanel.this.taskField.setForeground(new Color(0, 0, 0));
      }
    });
    this.durationField.addKeyListener(new KeyAdapter()
    {
      public void keyReleased(KeyEvent paramAnonymousKeyEvent)
      {
        if (paramAnonymousKeyEvent.getKeyCode() == 127)
        {
          Action localAction = (Action)ScheduleViewApplicationPanel.this.durationField.getActionForKeyStroke(KeyStroke.getKeyStroke(127, 0));
          if (localAction != null)
          {
            String str = localAction.getValue("Name").toString();
            localAction.actionPerformed(new ActionEvent(ScheduleViewApplicationPanel.this.durationField, 1001, str, paramAnonymousKeyEvent.getWhen(), 0));
          }
        }
        if ((ScheduleViewApplicationPanel.this.taskField.getText().trim().equals("")) || (ScheduleViewApplicationPanel.this.taskField.getText().compareTo(ScheduleViewApplicationPanel.this.r.getString("TaskFieldText")) == 0) || (ScheduleViewApplicationPanel.this.durationField.getText().trim().equals("")))
        {
          ScheduleViewApplicationPanel.this.createTaskBT.setEnabled(false);
          return;
        }
        if (!ScheduleViewApplicationPanel.this.systemMsgIsShowing)
        {
          TaskHelper.checkStringSize(ScheduleViewApplicationPanel.this.taskField, ScheduleViewApplicationPanel.this.session, null);
          if (paramAnonymousKeyEvent.getKeyCode() == 10)
            ScheduleViewApplicationPanel.this.taskCreation(true);
          else
            ScheduleViewApplicationPanel.this.createTaskBT.setEnabled(true);
        }
      }
    });
    this.durationField.addFocusListener(new FocusAdapter()
    {
      public void focusLost(FocusEvent paramAnonymousFocusEvent)
      {
        ScheduleViewApplicationPanel.this.durationInEditMode = false;
        try
        {
          ScheduleViewApplicationPanel.this.workEstimateMinutes = HoursFormatter.getMinutes(ScheduleViewApplicationPanel.this.durationField.getText());
        }
        catch (ParseException localParseException)
        {
        }
        ScheduleViewApplicationPanel.this.durationField.setText(HoursFormatter.format(ScheduleViewApplicationPanel.this.workEstimateMinutes));
      }

      public void focusGained(FocusEvent paramAnonymousFocusEvent)
      {
        ScheduleViewApplicationPanel.this.durationInEditMode = true;
      }
    });
    this.schPanel.add(localJLabel1, "North");
    this.schPanel.add(this.treeScrollPane, "Center");
    this.taskField.addKeyListener(new KeyAdapter()
    {
      public void keyReleased(KeyEvent paramAnonymousKeyEvent)
      {
        if (paramAnonymousKeyEvent.getKeyCode() == 127)
        {
          Action localAction = (Action)ScheduleViewApplicationPanel.this.taskField.getActionForKeyStroke(KeyStroke.getKeyStroke(127, 0));
          if (localAction != null)
          {
            String str = localAction.getValue("Name").toString();
            localAction.actionPerformed(new ActionEvent(ScheduleViewApplicationPanel.this.taskField, 1001, str, paramAnonymousKeyEvent.getWhen(), 0));
          }
        }
        if ((ScheduleViewApplicationPanel.this.taskField.getText().trim().equals("")) || (ScheduleViewApplicationPanel.this.taskField.getText().compareTo(ScheduleViewApplicationPanel.this.r.getString("TaskFieldText")) == 0) || (ScheduleViewApplicationPanel.this.durationField.getText().trim().equals("")))
        {
          ScheduleViewApplicationPanel.this.createTaskBT.setEnabled(false);
          return;
        }
        if (ScheduleViewApplicationPanel.this.taskField.getText().trim().length() > 0)
          TaskHelper.checkStringSize(ScheduleViewApplicationPanel.this.taskField, ScheduleViewApplicationPanel.this.session, null);
        if (paramAnonymousKeyEvent.getKeyCode() == 10)
        {
        	String textValue = (String)ScheduleViewApplicationPanel.this.gateTypeLOVComboBox.getSelectedItem();
            if ((textValue == null) || (textValue.trim().length() == 0))
            {
              MessageBox.post("±ÿ–ÎÃÓ–¥Gate/Task Type£°", "", 1);
              return;
            }
            
          if (ScheduleViewApplicationPanel.this.taskField.getText().trim().length() > 0)
            try
            {
              ScheduleViewApplicationPanel.this.taskField.setFocusable(false);
              ScheduleViewApplicationPanel.this.setCursor(Cursor.getPredefinedCursor(3));
              ScheduleViewApplicationPanel.this.taskCreation(true);
            }
            finally
            {
              ScheduleViewApplicationPanel.this.taskField.setFocusable(true);
              ScheduleViewApplicationPanel.this.taskField.requestFocusInWindow();
              ScheduleViewApplicationPanel.this.setCursor(Cursor.getPredefinedCursor(0));
            }
          else
            ScheduleViewApplicationPanel.this.createTaskBT.setEnabled(false);
        }
        else if (ScheduleViewApplicationPanel.this.taskField.getText().trim().equals(""))
        {
          ScheduleViewApplicationPanel.this.createTaskBT.setEnabled(false);
        }
        else
        {
          ScheduleViewApplicationPanel.this.systemMsgIsShowing = false;
          ScheduleViewApplicationPanel.this.createTaskBT.setEnabled(true);
        }
      }
    });
    this.createTaskBT.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
    	  String textValue = (String)ScheduleViewApplicationPanel.this.gateTypeLOVComboBox.getSelectedItem();
          if ((textValue == null) || (textValue.trim().length() == 0))
          {
            MessageBox.post("±ÿ–ÎÃÓ–¥Gate/Task Type£°", "", 1);
            return;
          }

        TCComponent localTCComponent = null;
        localTCComponent = ScheduleViewApplicationPanel.this.getSelectedComponent();
        boolean bool = true;
        Object localObject;
        if ((localTCComponent instanceof TCComponentScheduleTask))
        {
          localObject = (TCComponentScheduleTask)localTCComponent;
          bool = ((TCComponentScheduleTask)localObject).validateConditions();
        }
        if (bool)
        {
          ScheduleViewApplicationPanel.this.taskCreation(false);
          localObject = new Runnable()
          {
            public void run()
            {
              ScheduleViewApplicationPanel.this.taskField.requestFocusInWindow();
            }
          };
          SwingUtilities.invokeLater((Runnable)localObject);
        }
      }
    });
    this.ganttPanel = new JPanel(new BorderLayout());
    this.ganttPanel.addAncestorListener(new AncestorListener()
    {
      public void ancestorAdded(AncestorEvent paramAnonymousAncestorEvent)
      {
      }

      public void ancestorRemoved(AncestorEvent paramAnonymousAncestorEvent)
      {
      }

      public void ancestorMoved(AncestorEvent paramAnonymousAncestorEvent)
      {
        ScheduleViewApplicationPanel.this.splitPanePropertyChangeContext = 1;
      }
    });
    this.ganttPanel.setMinimumSize(new Dimension(50, 50));
    this.ganttPanel.setBackground(Color.GRAY);
    this.ganttPanel.addMouseListener(this);
    this.splitPane = new JSplitPane(1, this.schPanel, this.ganttPanel);
    this.splitPane.addPropertyChangeListener("dividerLocation", new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        if (ScheduleViewApplicationPanel.this.splitPanePropertyChangeContext != 0)
        {
          ScheduleViewApplicationPanel.this.splilPaneDividerLocation = ((Integer)paramAnonymousPropertyChangeEvent.getNewValue()).intValue();
          ScheduleViewApplicationPanel.this.splitPanePropertyChangeContext = 0;
        }
      }
    });
    this.splitPane.setOneTouchExpandable(true);
    this.splitPane.setDividerLocation(400);
    JPanel localJPanel1 = new JPanel(new BorderLayout());
    localJPanel1.setOpaque(false);
    JPanel localJPanel2 = new JPanel(new HorizontalLayout(2, 2, 2, 2, 2));
    localJPanel2.setOpaque(false);
    localJPanel2.setMinimumSize(new Dimension(0, 0));
    this.mruButton = new MRUButton((IAspectService)this.app, this.session);
    this.mruButton.setSuggestedVerticalAlignment(1);
    localJPanel2.add("left.nobind", this.mruButton);
    DeferredSessionPanel localDeferredSessionPanel = new DeferredSessionPanel(this.session.getPreferenceService());
    JComponent[] arrayOfJComponent = localDeferredSessionPanel.getComps();
    int i = arrayOfJComponent.length;
    for (int j = 0; j < i; j++)
      this.tskPanel.add(arrayOfJComponent[j], j + 5);
    this.tskPanel.revalidate();
    this.shortcutPanel.add(this.tskPanel);
    localJPanel1.add("North", this.shortcutPanel);
    localJPanel1.add("Center", this.splitPane);
    localJPanel1.add("South", localJPanel2);
    add("Center", localJPanel1);
    this.session.addAIFComponentEventListener(this);
    ITogglePropertiesService localITogglePropertiesService = (ITogglePropertiesService)OSGIUtil.getService(Activator.getDefault(), ITogglePropertiesService.class);
    localITogglePropertiesService.setToggleProperty("OpenSchedulingObject", isProgramView() ? "ProgramView" : "Schedule", this);
    this.openedComponents = new ArrayList();
  }

  private void taskCreation(boolean paramBoolean)
  {
    if (this.systemMsgIsShowing)
    {
      String str = this.r.getString("TaskName.MSG");
      MessageBox localMessageBox = new MessageBox(this.desktop, str, this.r.getString("error.TITLE"), 1);
      localMessageBox.setModal(true);
      localMessageBox.setVisible(true);
      this.taskField.setFocusable(true);
    }
    else
    {
      createNewTaskRequest();
      this.durationField.setText(HoursFormatter.format(this.workEstimateMinutes));
      this.taskField.setText("");
      this.createTaskBT.setEnabled(false);
    }
    if (paramBoolean)
      this.taskField.requestFocusInWindow();
  }

  public static ExpandGRMRelationsResponse2 expandGRMRelations(TCComponent[] paramArrayOfTCComponent, String paramString, boolean paramBoolean)
  {
    ExpandGRMRelationsResponse2 localExpandGRMRelationsResponse2 = null;
    if ((paramArrayOfTCComponent != null) && (paramString.length() > 0))
    {
      TCSession localTCSession = (TCSession)Activator.getDefault().getSessionService().getDefaultSession();
      DataManagementService localDataManagementService = DataManagementService.getService(localTCSession);
      RelationAndTypesFilter[] arrayOfRelationAndTypesFilter = new RelationAndTypesFilter[1];
      arrayOfRelationAndTypesFilter[0] = new RelationAndTypesFilter();
      arrayOfRelationAndTypesFilter[0].relationTypeName = paramString;
      ExpandGRMRelationsPref2 localExpandGRMRelationsPref2 = new ExpandGRMRelationsPref2();
      localExpandGRMRelationsPref2.returnRelations = true;
      localExpandGRMRelationsPref2.info = arrayOfRelationAndTypesFilter;
      if (paramBoolean)
        localExpandGRMRelationsResponse2 = localDataManagementService.expandGRMRelationsForPrimary(paramArrayOfTCComponent, localExpandGRMRelationsPref2);
      else
        localExpandGRMRelationsResponse2 = localDataManagementService.expandGRMRelationsForSecondary(paramArrayOfTCComponent, localExpandGRMRelationsPref2);
    }
    return localExpandGRMRelationsResponse2;
  }

  public static ArrayList<TCComponent> getOtherSideObjects(ExpandGRMRelationsResponse2 paramExpandGRMRelationsResponse2)
  {
    if (paramExpandGRMRelationsResponse2 == null)
      return null;
    ArrayList localArrayList = new ArrayList();
    for (ExpandGRMRelationsOutput2 localExpandGRMRelationsOutput2 : paramExpandGRMRelationsResponse2.output)
      for (ExpandGRMRelationsData2 localExpandGRMRelationsData2 : localExpandGRMRelationsOutput2.relationshipData)
        for (ExpandGRMRelationship localExpandGRMRelationship : localExpandGRMRelationsData2.relationshipObjects)
          if (!localArrayList.contains(localExpandGRMRelationship.otherSideObject))
            localArrayList.add(localExpandGRMRelationship.otherSideObject);
    return localArrayList;
  }

  public boolean checkScheduleForWorkOrderRelation(TCComponent paramTCComponent)
  {
    int i = 0;
    try
    {
      ArrayList localArrayList = null;
      TCComponent[] arrayOfTCComponent = new TCComponent[1];
      if ((paramTCComponent instanceof TCComponentScheduleTask))
      {
        arrayOfTCComponent[0] = ((TCComponentScheduleTask)paramTCComponent).getReferenceProperty("schedule_tag");
        i = 1;
      }
      else if ((paramTCComponent instanceof TCComponentSchedule))
      {
        arrayOfTCComponent[0] = paramTCComponent;
        i = 1;
      }
      if (i != 0)
      {
        ExpandGRMRelationsResponse2 localExpandGRMRelationsResponse2 = expandGRMRelations(arrayOfTCComponent, SS_JOB_ACTIVITY_SCHEDULE_REL, false);
        if (localExpandGRMRelationsResponse2 == null)
          return false;
        localArrayList = getOtherSideObjects(localExpandGRMRelationsResponse2);
        if ((localArrayList != null) && (localArrayList.size() != 0))
        {
          TCComponent localTCComponent = (TCComponent)localArrayList.get(0);
          if (localTCComponent != null)
            try
            {
              if (localTCComponent.isTypeOf("SSS0SvcWorkOrderRevision"))
                return true;
            }
            catch (TCException localTCException2)
            {
              logger.error("TCException", localTCException2);
              return false;
            }
        }
      }
    }
    catch (TCException localTCException1)
    {
      logger.error(localTCException1.getClass().getName(), localTCException1);
      return false;
    }
    return false;
  }

  private boolean createNewTaskRequest()
  {
    boolean bool = false;
    try
    {
      this.workEstimateMinutes = HoursFormatter.getMinutes(this.durationField.getText());
    }
    catch (ParseException localParseException)
    {
    }
    try
    {
      this.taskName = this.taskField.getText();
      NewTaskRequest localNewTaskRequest = new NewTaskRequest(this.taskName, this.workEstimateMinutes);
      doCreateTask(localNewTaskRequest);
    }
    catch (IllegalStateException localIllegalStateException)
    {
      logger.error("createNewTaskRequest::IllegalStateException", localIllegalStateException);
    }
    return bool;
  }

  public void doCreateTask(NewTaskRequest paramNewTaskRequest)
  {
    Registry localRegistry = Registry.getRegistry(this);
    TCComponent localTCComponent1 = null;
    TCComponentSchedule localTCComponentSchedule = null;
    Object localObject1 = null;
    TCComponent localTCComponent2 = null;
    ScheduleViewApplicationPanel localScheduleViewApplicationPanel = this;
    localTCComponentSchedule = localScheduleViewApplicationPanel.getCurrentProject();
    localTCComponent1 = localScheduleViewApplicationPanel.getSelectedComponent();
    int i = 0;
    if (localTCComponent1 != null)
      i = (localTCComponent1.getClass().getName().compareToIgnoreCase("com.teamcenter.rac.kernel.TCComponentSSS0JobCard") != 0) && (localTCComponent1.getClass().getName().compareToIgnoreCase("com.teamcenter.rac.kernel.TCComponentSSS0JobTask") != 0) ? 0 : 1;
    if ((i != 0) && (checkScheduleForWorkOrderRelation(localTCComponentSchedule)))
      this.isTaskBarForSS = true;
    if ((this.isTaskBarForSS) && (i == 0))
    {
      MessageBox.post(this.desktop, localRegistry.getString("notJobActivity.ERROR"), localRegistry.getString("error.TITLE"), 1);
      return;
    }
    if ((localTCComponent1 instanceof TCComponentScheduleTask))
    {
      try
      {
        localObject1 = TaskHelper.getParentForTask(localTCComponent1);
      }
      catch (TCException localTCException1)
      {
        localTCException1.printStackTrace();
      }
      localTCComponent2 = localTCComponent1;
    }
    if (localObject1 == null)
    {
      localTCComponent2 = null;
      try
      {
        localObject1 = ScheduleHelper.getScheduleSummaryTask(localTCComponentSchedule);
      }
      catch (TCException localTCException2)
      {
        localTCException2.printStackTrace();
      }
    }
    if (localTCComponentSchedule == null)
    {
      String localObject2 = localRegistry.getString("failToCreate");
      if (this.desktop != null)
        MessageBox.post(this.desktop, (String)localObject2, localRegistry.getString("error.TITLE"), 1);
    }
    this.session.setStatus(localRegistry.getString("creatingNewTask.MSG") + " " + this.taskName + "  ...");
    ModelFactory.singleton(this.session);
    TaskCreateContainer[] localObject2 = new TaskCreateContainer[1];
    localObject2[0] = new TaskCreateContainer();
    localObject2[0].objectType = "ScheduleTask";
    localObject2[0].otherAttributes = null;
    
    TCPreferenceService localTCPreferenceService = this.session.getPreferenceService();
    String str = localTCPreferenceService.getStringValue("ScheduleTaskClassNameToCreate");
    if ((str != null) && (!str.isEmpty()))
      localObject2[0].objectType = str;
    localObject2[0].name = paramNewTaskRequest.getTaskName();
    localObject2[0].workEstimate = paramNewTaskRequest.getDurationMin();
    Object localObject3 = new GregorianCalendar();
    Object localObject4 = new GregorianCalendar();
    TCComponent localTCComponent3 = null;
    Calendar localCalendar1 = Calendar.getInstance();
    Calendar localCalendar2 = Calendar.getInstance();
    try
    {
      if ((localTCComponent2 != null) && (!TaskHelper.getScheduleUidForTask(localTCComponent2).equalsIgnoreCase(TaskHelper.getMasterScheduleUid())))
      {
        if (TaskHelper.isScheduleSummaryTask(localTCComponent2))
        {
          localTCComponent3 = TaskHelper.getScheduleForTask(TaskHelper.getParentForTask(localTCComponent2));
          if (localTCComponent3 != null)
            if (TaskHelper.isFinishDateScheduling(localTCComponent3))
            {
              localObject4 = RACInterfaceFactory.singleton().getRACInterface(this.session, localTCComponentSchedule).setToEndOfDay(new GregorianCalendar());
              localCalendar1.setTimeInMillis(localTCComponentSchedule.getDateProperty("start_date").getTime());
              localCalendar2.setTimeInMillis(localTCComponentSchedule.getDateProperty("finish_date").getTime());
              if ((((Calendar)localObject4).before(localCalendar1)) || (((Calendar)localObject4).after(localCalendar2)))
                ((Calendar)localObject4).setTimeInMillis(localCalendar2.getTimeInMillis());
              localObject2[0].finish = ((Calendar)localObject4);
            }
            else if (localTCComponentSchedule != null)
            {
              localObject3 = RACInterfaceFactory.singleton().getRACInterface(this.session, localTCComponentSchedule).setToStartOfDay(new GregorianCalendar());
              localCalendar1.setTimeInMillis(localTCComponentSchedule.getDateProperty("start_date").getTime());
              localCalendar2.setTimeInMillis(localTCComponentSchedule.getDateProperty("finish_date").getTime());
              if ((((Calendar)localObject3).before(localCalendar1)) || (((Calendar)localObject3).after(localCalendar2)))
                ((Calendar)localObject3).setTimeInMillis(localCalendar1.getTimeInMillis());
              localObject2[0].start = ((Calendar)localObject3);
            }
        }
        else
        {
          localTCComponent3 = TaskHelper.getScheduleForTask(localTCComponent2);
          if (localTCComponent3 != null)
          {
            localCalendar1.setTimeInMillis(localTCComponent3.getDateProperty("start_date").getTime());
            localCalendar2.setTimeInMillis(localTCComponent3.getDateProperty("finish_date").getTime());
            Calendar localCalendar3;
            if (TaskHelper.isFinishDateScheduling(localTCComponent2))
            {
              localCalendar3 = Calendar.getInstance();
              localCalendar3.setTime(ScheduleHelper.getEndDate(localTCComponent3));
              localObject4 = localCalendar3;
              if ((((Calendar)localObject4).before(localCalendar1)) || (((Calendar)localObject4).after(localCalendar2)))
                ((Calendar)localObject4).setTimeInMillis(localCalendar2.getTimeInMillis());
              localObject2[0].finish = ((Calendar)localObject4);
            }
            else
            {
              localCalendar3 = Calendar.getInstance();
              localCalendar3.setTime(ScheduleHelper.getStartDate(localTCComponent3));
              localObject3 = localCalendar3;
              if ((((Calendar)localObject3).before(localCalendar1)) || (((Calendar)localObject3).after(localCalendar2)))
                ((Calendar)localObject3).setTimeInMillis(localCalendar1.getTimeInMillis());
              localObject2[0].start = ((Calendar)localObject3);
            }
          }
        }
      }
      else if (TaskHelper.isFinishDateScheduling(localTCComponentSchedule))
      {
        localObject4 = RACInterfaceFactory.singleton().getRACInterface(this.session, localTCComponentSchedule).setToEndOfDay(new GregorianCalendar());
        localCalendar1.setTimeInMillis(localTCComponentSchedule.getDateProperty("start_date").getTime());
        localCalendar2.setTimeInMillis(localTCComponentSchedule.getDateProperty("finish_date").getTime());
        if ((((Calendar)localObject4).before(localCalendar1)) || (((Calendar)localObject4).after(localCalendar2)))
          ((Calendar)localObject4).setTimeInMillis(localCalendar2.getTimeInMillis());
        localObject2[0].finish = ((Calendar)localObject4);
      }
      else if (localTCComponentSchedule != null)
      {
        localObject3 = RACInterfaceFactory.singleton().getRACInterface(this.session, localTCComponentSchedule).setToStartOfDay(new GregorianCalendar());
        localCalendar1.setTimeInMillis(localTCComponentSchedule.getDateProperty("start_date").getTime());
        localCalendar2.setTimeInMillis(localTCComponentSchedule.getDateProperty("finish_date").getTime());
        if ((((Calendar)localObject3).before(localCalendar1)) || (((Calendar)localObject3).after(localCalendar2)))
          ((Calendar)localObject3).setTimeInMillis(localCalendar1.getTimeInMillis());
        localObject2[0].start = ((Calendar)localObject3);
      }
    }
    catch (TCException localTCException3)
    {
      logger.error("TCException", localTCException3);
    }
    ArrayList localArrayList = new ArrayList();
    AttributeUpdateContainer localAttributeUpdateContainer1 = new AttributeUpdateContainer();
    localAttributeUpdateContainer1.attrName = "auto_complete";
    localAttributeUpdateContainer1.attrValue = String.valueOf(false);
    localAttributeUpdateContainer1.attrType = 6;
    localArrayList.add(localAttributeUpdateContainer1);
    AttributeUpdateContainer localAttributeUpdateContainer2 = new AttributeUpdateContainer();
    localAttributeUpdateContainer2.attrName = "fixed_type";
    localAttributeUpdateContainer2.attrValue = String.valueOf(0);
    localAttributeUpdateContainer2.attrType = 2;
    localArrayList.add(localAttributeUpdateContainer2);
    if (paramNewTaskRequest.getDurationMin() != 0)
    {
    	AttributeUpdateContainer localAttributeUpdateContainer3 = new AttributeUpdateContainer();
      localAttributeUpdateContainer3.attrName = "duration";
      localAttributeUpdateContainer3.attrValue = String.valueOf(paramNewTaskRequest.getDurationMin());
      localAttributeUpdateContainer3.attrType = 2;
      localArrayList.add(localAttributeUpdateContainer3);
    }
    else
    {
    	AttributeUpdateContainer localAttributeUpdateContainer3 = new AttributeUpdateContainer();
      localAttributeUpdateContainer3.attrName = "duration";
      localAttributeUpdateContainer3.attrValue = String.valueOf(0);
      localAttributeUpdateContainer3.attrType = 2;
      localArrayList.add(localAttributeUpdateContainer3);
    }
    if ((localObject1 != null) && ((localObject1 instanceof TCComponentScheduleTask)))
      localObject2[0].parent = ((TCComponentScheduleTask)localObject1);
    if ((localTCComponent2 != null) && ((localTCComponent2 instanceof TCComponentScheduleTask)))
      localObject2[0].prevSibling = ((TCComponentScheduleTask)localTCComponent2);
    localObject2[0].desc = "";
    AttributeUpdateContainer localAttributeUpdateContainer3 = new AttributeUpdateContainer();
    localAttributeUpdateContainer3.attrName = "priority";
    localAttributeUpdateContainer3.attrValue = String.valueOf(3);
    localAttributeUpdateContainer3.attrType = 2;
    localArrayList.add(localAttributeUpdateContainer3);
    if ((this.isTaskBarForSS) && (i != 0))
    {
      localObject2[0].objectType = "SSS0JobTask";
      AttributeUpdateContainer localAttributeUpdateContainer4 = new AttributeUpdateContainer();
      localAttributeUpdateContainer4.attrName = SS_ACTIVITY_EXECUTION_TYPE_PROP;
      localAttributeUpdateContainer4.attrValue = SS_ACTIVITY_EXECUTION_TYPE_VALUE;
      localAttributeUpdateContainer4.attrType = 1;
      localArrayList.add(localAttributeUpdateContainer4);
      if ((localTCComponent1 != null) && ((localTCComponent1 instanceof TCComponentScheduleTask)))
      {
        localObject2[0].parent = ((TCComponentScheduleTask)localTCComponent1);
        localObject2[0].prevSibling = null;
      }
    }
    AttributeUpdateContainer localAttributeUpdateContainer4 = new AttributeUpdateContainer();
    localAttributeUpdateContainer4.attrName = "constraint";
    localAttributeUpdateContainer4.attrValue = String.valueOf(0);
    localAttributeUpdateContainer4.attrType = 2;
    localArrayList.add(localAttributeUpdateContainer4);
    AttributeUpdateContainer localAttributeUpdateContainer5 = new AttributeUpdateContainer();
    localAttributeUpdateContainer5.attrName = "task_type";
    localAttributeUpdateContainer5.attrType = 2;
    if (paramNewTaskRequest.getDurationMin() != 0)
      localAttributeUpdateContainer5.attrValue = String.valueOf(0);
    else
      localAttributeUpdateContainer5.attrValue = String.valueOf(1);
    localArrayList.add(localAttributeUpdateContainer5);
    String textValue2 = ScheduleViewApplicationPanel.this.gateTypeLOVComboBox.getSelectedString();
    if(textValue2!=null && textValue2.length()>0){
    	AttributeUpdateContainer localAttributeUpdateContainercust = new AttributeUpdateContainer();
    	localAttributeUpdateContainercust.attrName = "jci6_TaskType";
    	localAttributeUpdateContainercust.attrValue = textValue2;
    	localAttributeUpdateContainercust.attrType = 1;
        localArrayList.add(localAttributeUpdateContainercust);
    }
//    Object localObject5;
    AttributeUpdateContainer[] localObject5;
    if ((localArrayList != null) && (localArrayList.size() > 0))
    {
      localObject5 = new AttributeUpdateContainer[localArrayList.size()];
      localObject5 = (AttributeUpdateContainer[])localArrayList.toArray((Object[])localObject5);
      localObject2[0].otherAttributes = ((AttributeUpdateContainer[])localObject5);
    }
    try
    {
//      localObject8 = localObject2;
      final TaskCreateContainer[] localObject8 = localObject2;
      if (localTCComponentSchedule != null)
      {
        Job local11 = new Job("")
        {
          protected IStatus run(IProgressMonitor paramAnonymousIProgressMonitor)
          {
            IStatus localIStatus = Status.OK_STATUS;
            CreateScheduleTaskRunner localCreateScheduleTaskRunner = new CreateScheduleTaskRunner(localObject8);
            int i = localCreateScheduleTaskRunner.execute();
            if (i != 0)
              localIStatus = Status.CANCEL_STATUS;
           //follow code add by 2019.11.14
            /*else{
            	
            	System.out.println("---> start set jci6_TaskType attribute!");
            	com.teamcenter.services.rac.projectmanagement._2012_02.ScheduleManagement.CreatedObjectsContainer results = (CreatedObjectsContainer) localCreateScheduleTaskRunner.getResults();
            	if( (results != null) && (results.createdObjects!=null)){
            		System.out.println("--->can start set jci6_TaskType attribute!");
            		TCComponent[] createdObjects = results.createdObjects;
            		System.out.println("createdObjects length is "+createdObjects.length);
            		for (int k = 0; k < createdObjects.length; k++) {
            			TCComponent tmpTask = createdObjects[k];
            			String uid = tmpTask.getUid();
            			 String textValue2 = ScheduleViewApplicationPanel.this.gateTypeLOVComboBox.getSelectedString();
                        System.out.println(uid+" set jci6_TaskType val:"+textValue2);
                        try {
                   			tmpTask.lock();
							tmpTask.setProperty("jci6_TaskType", textValue2);
							tmpTask.save();
							tmpTask.unlock();
						} catch (TCException e) {
							// TODO Auto-generated catch block
							System.out.println("jci6_TaskType msg:"+e.getMessage());
							e.printStackTrace();
						}
					}
            		
            	}else{
            		System.out.println("--->can't start set jci6_TaskType attribute!results.createdObjects == null!");
            	}
            }*/
            return localIStatus;
          }
        };
        local11.addJobChangeListener(new JobChangeAdapter()
        {
          public void done(IJobChangeEvent paramAnonymousIJobChangeEvent)
          {
            if (paramAnonymousIJobChangeEvent.getResult().isOK())
            {
              ScheduleViewApplicationPanel.this.jobRedraw.setPriority(50);
              ScheduleViewApplicationPanel.this.jobRedraw.schedule(50L);
            }
          }
        });
        local11.setPriority(10);
        local11.schedule(0L);
      }
    }
    catch (Exception localException)
    {
      logger.error("processTaskCreate::Exception", localException);
    }
  }

  public boolean open(TCComponent paramTCComponent, boolean paramBoolean)
  {
    return open(paramTCComponent, paramBoolean, false);
  }

  public void setTaskBarSS(boolean paramBoolean)
  {
    this.isTaskBarForSS = paramBoolean;
  }

  public boolean getTaskBarSS()
  {
    return this.isTaskBarForSS;
  }

  public boolean open(final TCComponentDynamicTaskLine paramTCComponentDynamicTaskLine, boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      setLoadScheduleOption("SM_Structure_Client_Context", 0);
      this.lastDateModified = Calendar.getInstance().getTime();
      this.isSubSchedule = false;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      return false;
    }
    this.currentViewType = VIEWTYPES.GENERICVIEW;
    this.previouslyOpenedSchedule = this.currentSchedule;
    this.currentSchedule = null;
    this.currentProgramView = null;
    this.taskLine = paramTCComponentDynamicTaskLine;
    Job local13 = new Job(this.r.getString("thread.job.name"))
    {
      protected IStatus run(IProgressMonitor paramAnonymousIProgressMonitor)
      {
        int i = 1;
        try
        {
          if (SwingUtilities.isEventDispatchThread())
          {
            ScheduleViewApplicationPanel.this.createTreeTableModel(paramTCComponentDynamicTaskLine);
            ScheduleViewApplicationPanel.this.createTreeTableAndGantt(paramTCComponentDynamicTaskLine);
          }
          else
          {
            SwingUtilities.invokeLater(new Runnable()
            {
              public void run()
              {
                ScheduleViewApplicationPanel.this.createTreeTableModel(paramTCComponentDynamicTaskLine);
                ScheduleViewApplicationPanel.this.createTreeTableAndGantt(paramTCComponentDynamicTaskLine);
              }
            });
          }
        }
        catch (Exception localException)
        {
        }
        return i != 0 ? Status.OK_STATUS : Status.CANCEL_STATUS;
      }
    };
    local13.setPriority(10);
    local13.schedule();
    return true;
  }

  private void createTreeTableModel(TCComponentDynamicTaskLine paramTCComponentDynamicTaskLine)
  {
    setEnableOptions(false);
    setProgramOptions(false);
    this.rootSchTreeLine = new ScheduleTreeLine(paramTCComponentDynamicTaskLine);
    this.treetableModel = new ScheduleTreeTableModel(this.rootSchTreeLine, this.session, VIEWTYPES.GENERICVIEW, this);
    this.rootSchTreeLine.setStructureIsVisited(true);
    String str = "ScheduleColumnsShownPref";
    ColumnIdentifier[] arrayOfColumnIdentifier = getPreferenceList(str, 0);
    String[] arrayOfString = new String[arrayOfColumnIdentifier.length];
    for (int i = 0; i < arrayOfColumnIdentifier.length; i++)
      arrayOfString[i] = ((String)arrayOfColumnIdentifier[i].getColumnId());
    try
    {
      populateScheduleTasks(paramTCComponentDynamicTaskLine, arrayOfString);
    }
    catch (TCException localTCException)
    {
      logger.error("Exception::createTreeTableModel", localTCException);
    }
  }

  private void createTreeTableAndGantt(TCComponentDynamicTaskLine paramTCComponentDynamicTaskLine)
  {
    if (this.treeTable != null)
    {
      this.session.removeAIFComponentEventListener(this.treeTable);
      this.treeTable = null;
    }
    this.treeTable = new ScheduleTreeTable(this.treetableModel, this, this.currentViewType);
    this.session.addAIFComponentEventListener(this.treeTable);
    addTreeTableMouseListeners();
    this.treeTableKeyListener = createTreeTableKeylistener();
    this.treeTable.addKeyListener(this.treeTableKeyListener);
    boolean bool;
    if ((this.app != null) && (this.rootSchTreeLine != null))
    {
      bool = false;
      Iterator localIterator = this.openedComponents.iterator();
      while (localIterator.hasNext())
      {
        InterfaceAIFComponent localInterfaceAIFComponent = (InterfaceAIFComponent)localIterator.next();
        if (localInterfaceAIFComponent.equals(this.rootSchTreeLine.getComponent()))
          bool = true;
        else
          ClientEventDispatcher.fireEventLater(this, "com/teamcenter/rac/aifrcp/component/Close", new Object[] { InterfaceAIFComponent.class, localInterfaceAIFComponent, "perspectiveId", "com.teamcenter.rac.schedule.ScheduleManagerPerspective" });
      }
      if (!bool)
        ClientEventDispatcher.fireEventLater(this, "com/teamcenter/rac/aifrcp/component/Open", new Object[] { InterfaceAIFComponent.class, this.rootSchTreeLine.getComponent(), AbstractAIFUIApplication.class, this.app });
      this.openedComponents.clear();
      this.openedComponents.add(this.rootSchTreeLine.getComponent());
    }
    this.treeScrollPane.setViewportView(this.treeTable);
    RowHeader.setRowHeader(this.treeTable, new TaskRowNumberManager(this.treeTable.getTree().getModel(), isProgramView()), true);
    if (this.ganttChart != null)
    {
      this.treeScrollPaneVertScrollBar.removeMouseListener(this.treeScrollPaneVertScrollBarMouseAdapter);
      bool = this.ganttPanel.isVisible();
      this.ganttPanel.setVisible(false);
      this.ganttPanel.removeAll();
      this.ganttPanel.setVisible(bool);
    }
    this.ganttPanel.add("Center", createGanttScrollPane());
    this.treeScrollPaneVertScrollBar.addMouseListener(this.treeScrollPaneVertScrollBarMouseAdapter);
    enableEvents(256L);
    this.ganttChart.addMouseListener(this);
    this.splitPane.updateUI();
    this.treeTable.setSelected(new AIFTreeLine[] { this.treeTable.getNodeForRow(0) });
    this.treeTable.addMouseListener(this);
    this.treetableModel.loadColumnPreferenceValue();
    this.treeTable.modelAdapter.fireTableStructureChanged();
    this.treeTable.setColumnRenderers();
    createEventListeners();
    ITogglePropertiesService localITogglePropertiesService = (ITogglePropertiesService)OSGIUtil.getService(Activator.getDefault(), ITogglePropertiesService.class);
    localITogglePropertiesService.setToggleProperty("OpenSchedulingObject", "Generic", this);
    this.expandedTreeObjects = new ArrayList();
    ganttViewRectNumberOfRows = 12;
    ganttRowCounter = 0;
    scrollBarWasAdjusting = false;
    this.ganttChart.refresh();
  }

  public boolean open(TCComponent paramTCComponent, boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      setLoadScheduleOption("SM_Structure_Client_Context", 0);
      this.lastDateModified = Calendar.getInstance().getTime();
      this.isSubSchedule = false;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      return false;
    }
    initMenuToggles();
    if (paramTCComponent == null)
    {
      MessageBox localMessageBox1 = new MessageBox(this.r.getString("errorSchedule.MSG"), this.r.getString("detailSchedule.MSG"), this.r.getString("errorMB.TITLE"), 1);
      localMessageBox1.setVisible(true);
      return false;
    }
    int i = savePrevious(paramTCComponent);
    if (i == 3)
      return true;
    if ((!(paramTCComponent instanceof TCComponentSchedule)) && (!(paramTCComponent instanceof TCComponentScheduleTask)) && (!(paramTCComponent instanceof TCComponentScheduleDeliverable)) && (!(paramTCComponent instanceof TCComponentTaskDeliverable)) && (!(paramTCComponent instanceof TCComponentFnd0ProxyTask)) && (!(paramTCComponent instanceof TCComponentChangeItemRevision)) && (!paramTCComponent.getType().equals("ScheduleTaskRevision")) && (!paramTCComponent.getType().equals("ProgramView")) && (!(paramTCComponent instanceof TCComponentResourceAssignment)))
    {
      AIFComponentContext[] arrayOfAIFComponentContext = null;
      int j = 0;
      try
      {
        arrayOfAIFComponentContext = paramTCComponent.whereReferenced();
        for (int k = 0; k < arrayOfAIFComponentContext.length; k++)
          if (((arrayOfAIFComponentContext[k].getComponent() instanceof TCComponentTaskDeliverable)) || ((arrayOfAIFComponentContext[k].getComponent() instanceof TCComponentScheduleDeliverable)))
          {
            paramTCComponent = (TCComponent)arrayOfAIFComponentContext[k].getComponent();
            j = 1;
            break;
          }
        if ((j == 0) && ((paramTCComponent instanceof InterfaceComponentLine)))
        {
          paramTCComponent = paramTCComponent.getUnderlyingComponent();
          j = 1;
        }
      }
      catch (TCException localTCException2)
      {
        localTCException2.printStackTrace();
      }
      if (j == 0)
      {
        MessageBox localMessageBox2 = new MessageBox(this.r.getString("errorSchedule.MSG"), this.r.getString("detailSchedule.MSG"), this.r.getString("errorMB.TITLE"), 1);
        localMessageBox2.setVisible(true);
        return false;
      }
    }
    try
    {
      if ((paramTCComponent instanceof TCComponentScheduleDeliverable))
        paramTCComponent = paramTCComponent.getReferenceProperty("schedule");
      else if ((paramTCComponent instanceof TCComponentTaskDeliverable))
        paramTCComponent = paramTCComponent.getReferenceProperty("schedule_task");
      else if ((paramTCComponent instanceof TCComponentResourceAssignment))
        paramTCComponent = paramTCComponent.getReferenceProperty("primary_object");
      if (paramTCComponent.getType().equals("ProgramView"))
      {
        this.currentViewType = VIEWTYPES.PROGRAMVIEW;
        this.previouslyOpenedSchedule = this.currentSchedule;
        this.currentSchedule = null;
      }
      else if ((paramTCComponent instanceof TCComponentSchedule))
      {
        this.currentViewType = VIEWTYPES.PROGRAMAUTHOR;
      }
    }
    catch (TCException localTCException1)
    {
      localTCException1.printStackTrace();
      return false;
    }
    TCComponent localTCComponent1 = null;
    int j = 0;
    if (((paramTCComponent instanceof TCComponentScheduleTask)) || ((paramTCComponent instanceof TCComponentFnd0ProxyTask)))
    {
      j = 1;
      try
      {
        if ((paramTCComponent instanceof TCComponentScheduleTask))
          localTCComponent1 = TaskHelper.getScheduleForTask(paramTCComponent);
        else if ((paramTCComponent instanceof TCComponentFnd0ProxyTask))
          localTCComponent1 = TaskHelper.getScheduleForProxyTask(paramTCComponent);
        if (!(localTCComponent1 instanceof TCComponentSchedule))
          return false;
      }
      catch (TCException localTCException3)
      {
        localTCComponent1 = null;
        return false;
      }
      this.currentViewType = VIEWTYPES.PROGRAMAUTHOR;
    }
    else
    {
      localTCComponent1 = paramTCComponent;
    }
    try
    {
      if (localTCComponent1.getLogicalProperty("is_baseline"))
      {
        int m = ConfirmationDialog.post(this.r.getString("warning.TITLE"), this.r.getString("scheduleBaselineOpen.MSG"), false);
        if (m == 2)
          localTCComponent1 = paramTCComponent.getReferenceProperty("activeschbaseline_tag");
        else
          return false;
      }
    }
    catch (TCException localTCException4)
    {
      localTCComponent1 = null;
      return false;
    }
    if ((this.currentSchedule != null) && (((TCComponentSchedule)localTCComponent1).getUid().equals(getCurrentProject().getUid())) && (this.treeTable != null))
      if ((j != 0) && ((paramTCComponent.getClass() == TCComponentScheduleTask.class) || (paramTCComponent.getClass() == TCComponentFnd0ProxyTask.class)) && (this.treeTable.findNode(paramTCComponent) != null))
      {
        if (SwingUtilities.isEventDispatchThread())
        {
          this.treeTable.setSelected(new AIFTreeLine[] { this.treeTable.findNode(paramTCComponent) });
          setScrollIntoView();
        }
        else
        {
          final TCComponent localTCComponent2 = paramTCComponent;
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              ScheduleViewApplicationPanel.this.treeTable.setSelected(new AIFTreeLine[] { ScheduleViewApplicationPanel.this.treeTable.findNode(localTCComponent2) });
              ScheduleViewApplicationPanel.this.setScrollIntoView();
            }
          });
        }
      }
      else if (SwingUtilities.isEventDispatchThread())
        this.treeTable.setSelected(new AIFTreeLine[] { this.treeTable.getNodeForRow(0) });
      else
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            ScheduleViewApplicationPanel.this.treeTable.setSelected(new AIFTreeLine[] { ScheduleViewApplicationPanel.this.treeTable.getNodeForRow(0) });
          }
        });
    if (!isProgramView())
      setCurrentProject((TCComponentSchedule)localTCComponent1);
    load(localTCComponent1, paramTCComponent, paramBoolean2);
    return true;
  }

  protected boolean checkExistingDeferred()
  {
    if (ScheduleDeferredContext.inDeferredSession())
    {
      if (Display.getCurrent() != null)
      {
        String[] arrayOfString = { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL };
        MessageDialog localMessageDialog = new MessageDialog(ScheduleViewApplication.getApplication().getApplicationView().getSite().getShell(), this.r.getString("deferredSession.TITLE"), null, this.r.getString("cancelDeferred.MSG"), 3, arrayOfString, 0);
        int j = localMessageDialog.open();
        if (j != 0)
          return true;
      }
      else
      {
        int i = ConfirmationDialog.post(this.r.getString("deferredSession.TITLE"), this.r.getString("cancelDeferred.MSG"), false);
        if (i == 1)
          return true;
      }
      try
      {
        new ReleaseDeferred(false).execute(null);
        ScheduleDeferredContext.setContext(false);
      }
      catch (ExecutionException localExecutionException)
      {
        localExecutionException.printStackTrace();
      }
    }
    return false;
  }

  public boolean load(final TCComponent paramTCComponent1, final TCComponent paramTCComponent2, boolean paramBoolean)
  {
    if (paramTCComponent1 == null)
      setEnableOptions(false);
    try
    {
      TCPreferenceService localTCPreferenceService = this.session.getPreferenceService();
      String str = localTCPreferenceService.getString(4, "SiteTimeZone", "null");
      if (("null".equals(str)) || (str == null))
      {
        JOptionPane.showMessageDialog(this, this.r.getString("tzNotSet.MSG"), this.r.getString("tzNotSet.TITLE"), 0);
        this.rootSchTreeLine = null;
        return false;
      }
      if (!TimeZoneCalUtil.testTimeZoneID(str))
      {
        String localObject = this.r.getString("tzInvalid.MSG");
        localObject = MessageFormat.format((String)localObject, new Object[] { str });
        JOptionPane.showMessageDialog(this, localObject, this.r.getString("tzInvalid.TITLE"), 0);
        this.rootSchTreeLine = null;
        return false;
      }
      if (((paramTCComponent2 instanceof TCComponentSchedule)) || (isProgramView()))
      {
    	  Runnable localObject = new Runnable()
        {
          public void run()
          {
            ScheduleViewApplicationPanel.this.mruButton.insertMRUComponent(paramTCComponent2);
          }
        };
        if (SwingUtilities.isEventDispatchThread())
          ((Runnable)localObject).run();
        else
          SwingUtilities.invokeLater((Runnable)localObject);
      }
      Object localObject = new Job(this.r.getString("thread.job.name"))
      {
        protected IStatus run(IProgressMonitor paramAnonymousIProgressMonitor)
        {
          boolean bool1 = true;
          try
          {
            Object localObject1;
            Object localObject2;
            Object localObject3;
            //$SWITCH_TABLE$com$teamcenter$rac$schedule$VIEWTYPES()[ScheduleViewApplicationPanel.this.currentViewType.ordinal()]
            System.out.println("load->currentViewType.ordinal():"+currentViewType.ordinal());
            switch (currentViewType.ordinal())
            {
            case 0:
            case 1:
              localObject1 = new LoadScheduleOptions();
              ((LoadScheduleOptions)localObject1).setIntOptions(ScheduleViewApplicationPanel.loadScheduleoptions);
              ScheduleViewApplicationPanel.getPartialContexOptiontFromTCPreference(ScheduleViewApplicationPanel.this.session);
              ScheduleViewApplicationPanel.setLoadContextOptionToDefault();
              bool1 = RACInterfaceFactory.singleton().getRACInterface(ScheduleViewApplicationPanel.this.session, paramTCComponent1).loadSchedule((LoadScheduleOptions)localObject1);
              if (bool1)
              {
                localObject2 = paramTCComponent1.getTCProperty("fnd0Schmgt_Lock").getReferenceValue();
                if ((localObject2 instanceof TCComponentUser))
                {
                  boolean bool2 = ((TCComponent)localObject2).equals(((TCComponent)localObject2).getSession().getUser());
                  if (bool2)
                  {
                    ScheduleDeferredContext.setContext(true);
                    DeferredSessionPanel.setDeferredSession(true);
                  }
                  else
                  {
                    DeferredSessionPanel.setLockedByUser(((TCComponent)localObject2).toDisplayString());
                  }
                  ScheduleLockedContext.setContext(!bool2);
                }
                if (paramTCComponent1.getTCProperty("recalc_type").getIntValue() != 0)
                {
                  localObject3 = new ConfirmationDialog(ScheduleViewApplicationPanel.this.frame, ScheduleViewApplicationPanel.this.r.getString("recalcSchedule.TITLE"), ScheduleViewApplicationPanel.this.r.getString("recalcSchedule.MSG"), false, null, null, null);
                  ((ConfirmationDialog)localObject3).setAlwaysOnTop(true);
                  ((ConfirmationDialog)localObject3).setVisible(true);
                  if (((ConfirmationDialog)localObject3).getAnswer() == 2)
                    ScheduleViewApplicationPanel.this.doRecalc = true;
                }
              }
              break;
            case 2:
	            {
	              ScheduleViewApplicationPanel.this.rootSchTreeLine = new ScheduleTreeLine(paramTCComponent1);
	              ScheduleViewApplicationPanel.this.currentProgramView = ((TCComponentDataset)ScheduleViewApplicationPanel.this.rootSchTreeLine.getComponent());
	              ScheduleViewApplicationPanel.this.programContainer = new ProgramMetaDataContainer();
	              TCComponent[] localObjectNamedRefs = ScheduleViewApplicationPanel.this.getCurrentProgramView().getNamedReferences();
	              if (localObjectNamedRefs.length > 0)
	              {
	                localObject2 = "";
	                localObject3 = (TCComponentTcFile)localObjectNamedRefs[0];
	                localObject2 = ScheduleViewApplicationPanel.this.loadProgramViewMetaDataFromVolume((TCComponentTcFile)localObject3);
	                ScheduleViewApplicationPanel.this.programContainer = ProgramPreferencesUtil.createProgramMetaData((String)localObject2);
	                ScheduleViewApplicationPanel.this.pgmViewXMLData = ((String)localObject2);
	                if (ScheduleViewApplicationPanel.this.programContainer.getProgramFilter() == null)
	                  ScheduleViewApplicationPanel.this.programContainer.setProgramFilter(new ArrayList());
	                ScheduleViewApplicationPanel.this.cloneProgramContainer();
	              }
	              break;
	            }
            }
          }
          catch (TCException localTCException)
          {
            localTCException.printStackTrace();
          }
          if (((paramTCComponent2 instanceof TCComponentSchedule)) || (isProgramView()))
            ScheduleViewApplicationPanel.this.mruButton.saveMRUEntries();
          return bool1 ? Status.OK_STATUS : Status.CANCEL_STATUS;
        }
      };
      ((Job)localObject).addJobChangeListener(new JobChangeAdapter()
      {
        public void done(IJobChangeEvent paramAnonymousIJobChangeEvent)
        {
          Job local1 = new Job(ScheduleViewApplicationPanel.this.r.getString("thread.job.name"))
          {
            protected IStatus run(IProgressMonitor paramAnonymous2IProgressMonitor)
            {
              return Status.OK_STATUS;
            }
          };
          local1.addJobChangeListener(new JobChangeAdapter()
          {
            public void done(IJobChangeEvent paramAnonymous2IJobChangeEvent)
            {
              if (!isProgramView()){
            	  //paramTCComponent1 (TCComponentSchedule)this.val$schedule
                ScheduleRoleContext.setupMenuContext((TCComponentSchedule)paramTCComponent1);
              }
              else
                ScheduleRoleContext.setupMenuContext(null);
            }
          });
          local1.schedule();
          if (paramAnonymousIJobChangeEvent.getResult().isOK())
          {
            if (SwingUtilities.isEventDispatchThread())
            {
              if (ScheduleViewApplicationPanel.this.createTreeTableModel(paramTCComponent1))
                ScheduleViewApplicationPanel.this.createTreeTableAndGantt(paramTCComponent1, paramTCComponent2);
              ScheduleViewApplicationPanel.this.setCursor(Cursor.getPredefinedCursor(0));
              if (!ScheduleViewApplicationPanel.this.isProgramView())
                ScheduleViewApplicationPanel.this.memento.restoreState();
            }
            else
            {
              SwingUtilities.invokeLater(new Runnable()
              {
                public void run()
                {
                  if (ScheduleViewApplicationPanel.this.createTreeTableModel(paramTCComponent1))
                    ScheduleViewApplicationPanel.this.createTreeTableAndGantt(paramTCComponent1, paramTCComponent2);
                  ScheduleViewApplicationPanel.this.setCursor(Cursor.getPredefinedCursor(0));
                  if (!ScheduleViewApplicationPanel.this.isProgramView())
                    ScheduleViewApplicationPanel.this.memento.restoreState();
                }
              });
            }
          }
          else
            ScheduleViewApplicationPanel.this.setCursor(Cursor.getPredefinedCursor(0));
        }
      });
      ((Job)localObject).setPriority(10);
      ((Job)localObject).schedule();
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return true;
  }

  public boolean createTreeTableModel(final TCComponent paramTCComponent)
  {
    try
    {
//      Object localObject1;
      Object localObject2;
      //$SWITCH_TABLE$com$teamcenter$rac$schedule$VIEWTYPES()[this.currentViewType.ordinal()]
      System.out.println("createTreeTableModel currentViewType.ordinal():"+currentViewType.ordinal());
      switch (currentViewType.ordinal())
      {
      case 0:
      case 1:
    	 String[] localObject1 = null;
        localObject2 = TaskHelper.getSummaryTaskForSchedule(paramTCComponent);
        Object localObject4;
        int i;
        if ((localObject2 != null) && ((localObject2 instanceof TCComponentScheduleTask)))
        {
          String localObject3 = "ScheduleColumnsShownPref";
          ColumnIdentifier[] preferenceListlocalObject4 = getPreferenceList((String)localObject3, 0);
          localObject1 = new String[preferenceListlocalObject4.length];
          for (i = 0; i < preferenceListlocalObject4.length; i++)
            localObject1[i] = ((String)preferenceListlocalObject4[i].getColumnId());
        }
        this.rootSchTreeLine = new ScheduleTreeLine(TaskHelper.getSummaryTaskForSchedule(paramTCComponent), (String[])localObject1);
        Object localObject3 = (TCComponent)this.rootSchTreeLine.getContextComponent().getComponent();
        this.rootSchTreeLine.setStructureIsVisited(true);
        if (localObject3.getClass() == TCComponentScheduleTask.class)
        {
        	TCComponent[] task_taglistlocalObject4 = ((TCComponent)localObject3).getReferenceListProperty("child_task_taglist");
          i = getLoadScheduleOption("SM_Structure_Partial_Context");
          this.rootSchTreeLine.setStructureIsVisited(true);
          populateProxyAndScheduleTasks(paramTCComponent, (TCComponent)localObject3, (TCComponent[])task_taglistlocalObject4, (String[])localObject1, i, null);
        }
        break;
      case 2:
      }
      if (((paramTCComponent instanceof TCComponentSchedule)) && (!isScheduleModifiable()))
      {
        if (!isScheduleModifiable())
        {
          setEnableOptions(false);
          setProgramOptions(false);
        }
        else
        {
          setEnableOptions(true);
          setProgramOptions(false);
        }
      }
      else if (isProgramView())
      {
        setEnableOptions(false);
        setProgramOptions(true);
      }
      else
      {
        setEnableOptions(true);
        setProgramOptions(false);
        if (this.doRecalc)
        {
        	Job localObject1 = new Job(this.r.getString("recalcSchedule.TITLE"))
          {
            protected IStatus run(IProgressMonitor paramAnonymousIProgressMonitor)
            {
              ScheduleViewApplicationPanel.this.doRecalc = false;
              RecalculateScheduleNonInteractiveRunner localRecalculateScheduleNonInteractiveRunner = new RecalculateScheduleNonInteractiveRunner((TCComponentSchedule)paramTCComponent, -1, false);
              localRecalculateScheduleNonInteractiveRunner.execute();
              SwingUtilities.invokeLater(new Runnable()
              {
                public void run()
                {
                  if (ScheduleViewApplicationPanel.this.rootSchTreeLine != null)
                    try
                    {
                      Object localObject = ScheduleViewApplicationPanel.this.rootSchTreeLine.getUserObject();
                      if (localObject != null)
                        if ((localObject instanceof TCComponentSchedule))
                          ScheduleViewApplicationPanel.this.rootSchTreeLine.updateScheduleIcon();
                        else if ((localObject instanceof TCComponentScheduleTask))
                          ScheduleViewApplicationPanel.this.rootSchTreeLine.updateScheduleSummaryIcon();
                    }
                    catch (Exception localException)
                    {
                      localException.printStackTrace();
                    }
                }
              });
              return Status.OK_STATUS;
            }
          };
          localObject2 = (IOperationService)OSGIUtil.getService(Activator.getDefault(), IOperationService.class);
          ((IOperationService)localObject2).queueOperation((Job)localObject1);
        }
      }
    }
    catch (SchedulingException localSchedulingException)
    {
      SchedulingExceptionHandler.handleScheduleException(localSchedulingException);
      return false;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    if (this.rootSchTreeLine != null)
      this.treetableModel = new ScheduleTreeTableModel(this.rootSchTreeLine, this.session, this.currentViewType, this);
    return true;
  }

  public VIEWTYPES getCurrentViewType()
  {
    return this.currentViewType;
  }

  public void populateProxyAndScheduleTasks(TCComponent paramTCComponent1, TCComponent paramTCComponent2, TCComponent[] paramArrayOfTCComponent, String[] paramArrayOfString, int paramInt, Vector<ScheduleTreeLine> paramVector)
    throws TCException
  {
    populateProxyMap(paramTCComponent1, true);
    List localList = getReferencedProxyTasks(paramTCComponent2);
    Object localObject;
    if (localList != null)
    {
      localObject = localList.iterator();
      while (((Iterator)localObject).hasNext())
      {
        TCComponent localTCComponent = (TCComponent)((Iterator)localObject).next();
        ScheduleTreeLine localScheduleTreeLine = new ScheduleTreeLine(localTCComponent, paramArrayOfString);
        this.rootSchTreeLine.add(localScheduleTreeLine);
        localScheduleTreeLine.setStructureIsVisited(true);
      }
    }
    for (int i = 0; i < paramArrayOfTCComponent.length; i++)
    {
      localObject = populateTask(this.rootSchTreeLine, paramArrayOfTCComponent[i], paramArrayOfString, paramInt);
      if (paramVector != null)
        paramVector.addAll((Collection)localObject);
    }
  }

  public void populateScheduleTasks(TCComponentDynamicTaskLine paramTCComponentDynamicTaskLine, String[] paramArrayOfString)
    throws TCException
  {
    List localList = paramTCComponentDynamicTaskLine.getTaskUids();
    ArrayList localArrayList1 = new ArrayList();
    if (localList != null)
    {
      ArrayList localArrayList2 = new ArrayList();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        TCComponent localTCComponent = this.session.getComponentManager().getTCComponent(str);
        localArrayList2.add(localTCComponent);
        ScheduleTreeLine localScheduleTreeLine = new ScheduleTreeLine(localTCComponent, paramArrayOfString);
        this.rootSchTreeLine.add(localScheduleTreeLine);
        localScheduleTreeLine.setStructureIsVisited(true);
        localArrayList1.add(localTCComponent);
      }
      paramTCComponentDynamicTaskLine.setTasks(localArrayList1);
    }
  }

  public boolean createTreeTableAndGantt(TCComponent paramTCComponent1, TCComponent paramTCComponent2)
  {
    try
    {
      if (this.treeTable != null)
      {
        this.session.removeAIFComponentEventListener(this.treeTable);
        this.treeTable = null;
      }
      long l = 0L;
//      $SWITCH_TABLE$com$teamcenter$rac$schedule$VIEWTYPES()[getCurrentViewType().ordinal()]
      System.out.println("createTreeTableAndGantt getCurrentViewType().ordinal():"+getCurrentViewType().ordinal());
      switch (getCurrentViewType().ordinal())
      {
      case 0:
      case 1:
        l = ScheduleHelper.getStartDateAsLong(paramTCComponent1);
        break;
      case 2:
      case 3:
      }
      this.treeTable = new ScheduleTreeTable(this.treetableModel, this, this.currentViewType);
      if ((paramTCComponent2 != null) && (this.treeTable.findNode(paramTCComponent2) != null))
        this.treeTable.setSelected(new AIFTreeLine[] { this.treeTable.findNode(paramTCComponent2) });
      this.session.addAIFComponentEventListener(this.treeTable);
      addTreeTableMouseListeners();
      this.treeTableKeyListener = createTreeTableKeylistener();
      this.treeTable.addKeyListener(this.treeTableKeyListener);
      boolean bool1;
      Object localObject2;
      if ((this.app != null) && (this.rootSchTreeLine != null))
      {
        bool1 = false;
        Iterator localIterator = this.openedComponents.iterator();
        while (localIterator.hasNext())
        {
          localObject2 = (InterfaceAIFComponent)localIterator.next();
          if (localObject2.equals(this.rootSchTreeLine.getComponent()))
            bool1 = true;
          else
            ClientEventDispatcher.fireEventLater(this, "com/teamcenter/rac/aifrcp/component/Close", new Object[] { InterfaceAIFComponent.class, localObject2, "perspectiveId", "com.teamcenter.rac.schedule.ScheduleManagerPerspective" });
        }
        if (!bool1)
          ClientEventDispatcher.fireEventLater(this, "com/teamcenter/rac/aifrcp/component/Open", new Object[] { InterfaceAIFComponent.class, this.rootSchTreeLine.getComponent(), AbstractAIFUIApplication.class, this.app });
        this.openedComponents.clear();
        this.openedComponents.add(this.rootSchTreeLine.getComponent());
      }
      this.treeScrollPane.setViewportView(this.treeTable);
      RowHeader.setRowHeader(this.treeTable, new TaskRowNumberManager(this.treeTable.getTree().getModel(), isProgramView()), true);
      if (this.ganttChart != null)
      {
        this.treeScrollPaneVertScrollBar.removeMouseListener(this.treeScrollPaneVertScrollBarMouseAdapter);
        bool1 = this.ganttPanel.isVisible();
        this.ganttPanel.setVisible(false);
        this.ganttPanel.removeAll();
        this.ganttPanel.setVisible(bool1);
      }
      this.ganttPanel.add("Center", createGanttScrollPane());
      this.treeScrollPaneVertScrollBar.addMouseListener(this.treeScrollPaneVertScrollBarMouseAdapter);
      enableEvents(256L);
      if (getCurrentProject() != null)
      {
        this.ganttChart.setAllowLayout(false);
        this.ganttChart.setCurrentProject(paramTCComponent1);
        JGanttChart.refreshState = 0;
        JGanttChart.refreshCount = 0;
        this.ganttChart.putRefreshJobToSleep(100L);
        if (paramTCComponent2 != null)
        {
          try
          {
            if ((paramTCComponent2 instanceof TCComponentScheduleTask))
              l = TaskHelper.getStartDate(paramTCComponent2);
          }
          catch (TCException localTCException2)
          {
            localTCException2.printStackTrace();
          }
          Calendar localObject1 = Calendar.getInstance();
          ((Calendar)localObject1).setTimeInMillis(l);
          this.ganttChart.scrollToDate((Calendar)localObject1, true);
        }
      }
      else if ((getProgramContainer() != null) && (getProgramContainer().getScheduleIDs() != null) && (getProgramContainer().getScheduleIDs().size() > 0))
      {
        this.ganttChart.setProgramViewGanttCalendarTimeSpan(getProgramContainer(), null);
      }
      this.ganttChart.addMouseListener(this);
      this.splitPane.updateUI();
      AIFTreeLine localObject1 = null;
      if (((paramTCComponent2 instanceof TCComponentFnd0ProxyTask)) || ((paramTCComponent2 instanceof TCComponentScheduleTask)))
      {
        if ((localObject1 = this.treeTable.findNode(paramTCComponent2)) != null)
        {
          this.treeTable.setSelected(new AIFTreeLine[] { localObject1 });
          setScrollIntoView();
        }
      }
      else
        this.treeTable.setSelected(new AIFTreeLine[] { this.treeTable.getNodeForRow(0) });
      this.treeTable.addMouseListener(this);
      try
      {
        if ((paramTCComponent1 instanceof TCComponentSchedule))
        {
          localObject2 = paramTCComponent1.getTCProperty("fnd0Schmgt_Lock").getReferenceValue();
          boolean bool2 = false;
          if ((localObject2 instanceof TCComponentUser))
          {
            if (bool2)
              DeferredSessionPanel.setLockedByUser(((TCComponent)localObject2).toDisplayString());
            ScheduleLockedContext.setContext(bool2);
          }
          else
          {
            DeferredSessionPanel.setDeferredSession(false);
            ScheduleLockedContext.setContext(false);
          }
          if (isScheduleModifiable())
          {
            this.taskField.setEditable(!bool2);
            this.taskField.setEnabled(!bool2);
            this.taskField.setText(this.r.getString("TaskFieldText"));
            this.taskField.setForeground(new Color(181, 166, 180));
            this.durationField.setEditable(!bool2);
            this.durationField.setEnabled(!bool2);
            setEnableOptions(!bool2);
            this.createTaskBT.setEnabled(false);
          }
        }
      }
      catch (TCException localTCException3)
      {
        localTCException3.printStackTrace();
      }
      if ((isProgramView()) && (this.rootSchTreeLine != null))
      {
        this.currentProgramView = ((TCComponentDataset)this.rootSchTreeLine.getComponent());
        if (this.currentProgramView != null)
          loadProgramView();
        this.treetableModel.loadColumnPreferenceValue();
        this.treeTable.setColumnRenderers();
      }
      createEventListeners();
      ITogglePropertiesService localITogglePropertiesService = (ITogglePropertiesService)OSGIUtil.getService(Activator.getDefault(), ITogglePropertiesService.class);
      localITogglePropertiesService.setToggleProperty("OpenSchedulingObject", isProgramView() ? "ProgramView" : "Schedule", this);
      this.expandedTreeObjects = new ArrayList();
      System.out.println("getCurrentViewType().ordinal():"+getCurrentViewType().ordinal());
      switch (getCurrentViewType().ordinal())
      {
      case 1:
        TCComponentSchedule localTCComponentSchedule = getCurrentProject();
        String str = "";
        if (localTCComponentSchedule != null)
          try
          {
            str = ScheduleHelper.getName(localTCComponentSchedule);
            ScheduleViewApplication.getApplication().setLabel(str);
          }
          catch (TCException localTCException4)
          {
            logger.error("TCException::createTreeTableAndGantt", localTCException4);
          }
        break;
      case 2:
        TCComponentDataset localTCComponentDataset = getCurrentProgramView();
        if (localTCComponentDataset != null)
          ScheduleViewApplication.getApplication().setLabel(localTCComponentDataset.toDisplayString());
        break;
      case 3:
        TCComponentDynamicTaskLine localTCComponentDynamicTaskLine = getTaskLine();
        if (localTCComponentDynamicTaskLine != null)
          ScheduleViewApplication.getApplication().setLabel(localTCComponentDynamicTaskLine.getName());
        break;
      }
      ganttViewRectNumberOfRows = 12;
      ganttRowCounter = 0;
      scrollBarWasAdjusting = false;
    }
    catch (TCException localTCException1)
    {
      logger.error("TCException::createTreeTableAndGantt", localTCException1);
    }
    return true;
  }

  private int savePrevious(TCComponent paramTCComponent)
  {
    if (!isDirty())
      return 1;
    if (this.rootSchTreeLine == null)
      return 1;
    InterfaceAIFComponent localInterfaceAIFComponent = this.rootSchTreeLine.getComponent();
    if (((localInterfaceAIFComponent instanceof TCComponentScheduleTask)) || ((localInterfaceAIFComponent instanceof TCComponentSchedule)))
      return 1;
    int i = 1;
    if ((localInterfaceAIFComponent instanceof TCComponentDataset))
    {
      TCComponentDataset localTCComponentDataset = (TCComponentDataset)localInterfaceAIFComponent;
      if (paramTCComponent.equals(localTCComponentDataset))
        return 3;
      i = ConfirmationDialog.post(this.r.getString("programViewSave.TITLE"), this.r.getString("programViewSave.MSG"), true);
      if (i == 2)
        saveProgramView();
    }
    return i;
  }

  protected void updateMenuAndToolBarChanges()
  {
    if (isProgramView())
    {
      setEnableOptions(false);
      setProgramOptions(true);
    }
    else
    {
      setEnableOptions(true);
      setProgramOptions(false);
    }
  }

  private void createEventListeners()
  {
    getTree().addTreeExpansionListener(new TreeExpansionListener()
    {
      public void treeExpanded(TreeExpansionEvent paramAnonymousTreeExpansionEvent)
      {
        ScheduleViewApplicationPanel.this.processTreeExpansion(paramAnonymousTreeExpansionEvent);
      }

      public void treeCollapsed(TreeExpansionEvent paramAnonymousTreeExpansionEvent)
      {
        ScheduleViewApplicationPanel.this.processTreeCollapse(paramAnonymousTreeExpansionEvent);
      }
    });
  }

  public void processTreeExpansion(TreeExpansionEvent paramTreeExpansionEvent)
  {
    if (!this.supressExpansionEvent)
    {
      TreePath localTreePath = paramTreeExpansionEvent.getPath();
      Object[] arrayOfObject = localTreePath.getPath();
      if ((arrayOfObject[(arrayOfObject.length - 1)] instanceof ScheduleTreeLine))
      {
        ScheduleTreeLine localScheduleTreeLine1 = (ScheduleTreeLine)arrayOfObject[(arrayOfObject.length - 1)];
        String str = localScheduleTreeLine1.generateKey();
        if (!this.expandedTreeObjects.contains(str))
          this.expandedTreeObjects.add(str);
        try
        {
          setLoadContextOptionToDefault();
          int i = getLoadScheduleOption("SM_Structure_Partial_Context");
          switch (i)
          {
          case 0:
            break;
          case 1:
          case 3:
            ScheduleTreeLine localScheduleTreeLine2 = (ScheduleTreeLine)arrayOfObject[(arrayOfObject.length - 1)];
            if (!localScheduleTreeLine2.getStructureIsVisited())
            {
              TCComponent localTCComponent = (TCComponent)localScheduleTreeLine2.getUserObject();
              int j = localTCComponent.getIntProperty("task_type");
              switch (j)
              {
              case 6:
                if (i == 2)
                {
                  OpenSubScheduleAction localOpenSubScheduleAction = new OpenSubScheduleAction(this.app, "openSubScheduleCommand");
                  localOpenSubScheduleAction.run();
                }
                break;
              case 2:
              case 3:
              case 4:
              case 5:
              }
            }
            break;
          case 2:
          }
        }
        catch (TCException localTCException)
        {
          logger.error("Exception while handling structure processTreeExpansion", localTCException);
        }
        catch (Exception localException)
        {
          logger.error("Exception while handling structure processTreeExpansion", localException);
        }
      }
    }
  }

  private void processTreeCollapse(TreeExpansionEvent paramTreeExpansionEvent)
  {
    TreePath localTreePath = paramTreeExpansionEvent.getPath();
    Object[] arrayOfObject = localTreePath.getPath();
    if ((arrayOfObject[(arrayOfObject.length - 1)] instanceof ScheduleTreeLine))
    {
      ScheduleTreeLine localScheduleTreeLine = (ScheduleTreeLine)arrayOfObject[(arrayOfObject.length - 1)];
      String str = localScheduleTreeLine.generateKey();
      this.expandedTreeObjects.remove(str);
    }
  }

  public ColumnIdentifier[] getPreferenceList(String paramString, int paramInt)
  {
    String[] arrayOfString = null;
    String[][] arrayOfString1 = null;
    try
    {
      TCPreferenceService localTCPreferenceService = this.session.getPreferenceService();
      arrayOfString = localTCPreferenceService.getStringArray(paramInt, paramString);
      arrayOfString1 = ScheduleTreeTableModel.getDisplayableHeaderValue(this.session, arrayOfString);
      ColumnIdentifier[] arrayOfColumnIdentifier = new ColumnIdentifier[arrayOfString1.length];
      for (int i = 0; i < arrayOfString1.length; i++)
        arrayOfColumnIdentifier[i] = new ColumnIdentifier(arrayOfString1[i][0], arrayOfString1[i][1], arrayOfString1[i][2]);
      return arrayOfColumnIdentifier;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return new ColumnIdentifier[0];
  }

  public Vector<ScheduleTreeLine> populateTask(ScheduleTreeLine paramScheduleTreeLine, TCComponent paramTCComponent, String[] paramArrayOfString, int paramInt)
  {
    Vector localVector = new Vector();
    if (paramTCComponent != null)
    {
      TCComponent[] arrayOfTCComponent1 = null;
      try
      {
        if ((paramInt > 1) || (paramInt < 0))
          paramInt = 0;
        Object localObject1;
        Object localObject2;
        int i;
        int j;
        switch (paramInt)
        {
        case 0:
          if ((paramTCComponent instanceof TCComponentScheduleTask))
          {
            localObject1 = (TCComponentScheduleTask)paramTCComponent;
            localObject2 = new ScheduleTreeLine(localObject1, paramArrayOfString);
            ((ScheduleTreeLine)localObject2).setStructureIsVisited(true);
            processAddWithProxyNodes(paramScheduleTreeLine, (TCComponent)localObject1, paramArrayOfString, (ScheduleTreeLine)localObject2);
           
            if (TaskHelper.isSummaryStructure((TCComponent)localObject1))
            {
              arrayOfTCComponent1 = TaskHelper.getChildTasks((TCComponentScheduleTask)localObject1);
              for (j = 0; j < arrayOfTCComponent1.length; j++)
                populateTask((ScheduleTreeLine)localObject2, arrayOfTCComponent1[j], paramArrayOfString, paramInt);
            }
          }
          break;
        case 1:
        case 2:
        case 3:
          localObject1 = new ScheduleTreeLine(paramTCComponent, paramArrayOfString);
          processAddWithProxyNodes(paramScheduleTreeLine, paramTCComponent, paramArrayOfString, (ScheduleTreeLine)localObject1);
          if (TaskHelper.isSummaryStructure(paramTCComponent))
          {
            if ((TaskHelper.isSummaryTask(paramTCComponent)) || (TaskHelper.isPhaseTask(paramTCComponent)) || (TaskHelper.isScheduleSummaryTask(paramTCComponent)))
            {
              ((ScheduleTreeLine)localObject1).setStructureIsVisited(true);
              arrayOfTCComponent1 = TaskHelper.getChildTasks((TCComponentScheduleTask)paramTCComponent);
              if ((paramInt == 1) || (paramInt == 2))
              {
                for (TCComponent eachlocalObject2 : arrayOfTCComponent1)
                  populateTask((ScheduleTreeLine)localObject1, (TCComponent)eachlocalObject2, paramArrayOfString, paramInt);
              }
              else
              {
                localObject2 = null;
                ScheduleTreeLine localObject3 = null;
                for (TCComponent localObject4 : arrayOfTCComponent1)
                {
                  ScheduleTreeLine localScheduleTreeLine = new ScheduleTreeLine(localObject4, paramArrayOfString);
                  processAddWithProxyNodes(paramScheduleTreeLine, (TCComponent) localObject4, paramArrayOfString, localScheduleTreeLine);
                  if (localObject4.getIntProperty("task_type") == 2)
                  {
                    localObject2 = localObject4;
                    localObject3 = localScheduleTreeLine;
                  }
                }
                populateSubTask(localObject3, (TCComponent)localObject2, paramArrayOfString);
              }
            }
            else
            {
              ((ScheduleTreeLine)localObject1).setStructureIsVisited(false);
              localVector.add(localObject1);
            }
          }
          else
            ((ScheduleTreeLine)localObject1).setStructureIsVisited(true);
          break;
        }
      }
      catch (TCException localTCException)
      {
        localTCException.printStackTrace();
      }
    }
    return localVector;
  }

  public Vector<ScheduleTreeLine> processAddWithProxyNodes(ScheduleTreeLine paramScheduleTreeLine1, TCComponent paramTCComponent, String[] paramArrayOfString, ScheduleTreeLine paramScheduleTreeLine2)
  {
    Vector localVector = new Vector();
    if ((TaskHelper.isScheduleSummaryTask(paramTCComponent)) && (paramScheduleTreeLine2.getStructureIsVisited()))
      try
      {
        populateProxyMap(TaskHelper.getScheduleForTask(paramTCComponent), false);
      }
      catch (TCException localTCException1)
      {
        Logger.getLogger(getClass()).error("Error getting proxy tasks", localTCException1);
      }
    List localList = getReferencedProxyTasks(paramTCComponent);
    if (localList != null)
    {
      ArrayList localArrayList1 = new ArrayList();
      ArrayList localArrayList2 = new ArrayList();
      long l = 9223372036854775807L;
      try
      {
        l = TaskHelper.getStartDate(paramTCComponent);
      }
      catch (TCException localTCException2)
      {
        Logger.getLogger(getClass()).error("Unable to read start date(1)", localTCException2);
      }
      Iterator localIterator = localList.iterator();
      TCComponent localTCComponent;
      while (localIterator.hasNext())
      {
        localTCComponent = (TCComponent)localIterator.next();
        try
        {
          Date localDate = localTCComponent.getDateProperty("fnd0start_date");
          if (localDate != null)
          {
            if ((l < localDate.getTime()) || (TaskHelper.isScheduleSummaryTask(paramTCComponent)))
              localArrayList2.add(localTCComponent);
            else
              localArrayList1.add(localTCComponent);
          }
          else
            localArrayList2.add(localTCComponent);
        }
        catch (TCException localTCException3)
        {
          Logger.getLogger(getClass()).error("Unable to read start date(2)", localTCException3);
          localArrayList2.add(localTCComponent);
        }
      }
      localIterator = localArrayList1.iterator();
      ScheduleTreeLine localScheduleTreeLine;
      while (localIterator.hasNext())
      {
        localTCComponent = (TCComponent)localIterator.next();
        localScheduleTreeLine = new ScheduleTreeLine(localTCComponent, paramArrayOfString);
        paramScheduleTreeLine1.add(localScheduleTreeLine);
        localVector.add(localScheduleTreeLine);
        localScheduleTreeLine.setStructureIsVisited(true);
      }
      paramScheduleTreeLine1.add(paramScheduleTreeLine2);
      localVector.add(paramScheduleTreeLine2);
      localIterator = localArrayList2.iterator();
      while (localIterator.hasNext())
      {
        localTCComponent = (TCComponent)localIterator.next();
        localScheduleTreeLine = new ScheduleTreeLine(localTCComponent, paramArrayOfString);
        if (TaskHelper.isScheduleSummaryTask(paramTCComponent))
        {
          paramScheduleTreeLine2.add(localScheduleTreeLine);
        }
        else
        {
          paramScheduleTreeLine1.add(localScheduleTreeLine);
          localVector.add(localScheduleTreeLine);
        }
        localScheduleTreeLine.setStructureIsVisited(true);
      }
    }
    else
    {
      paramScheduleTreeLine1.add(paramScheduleTreeLine2);
      localVector.add(paramScheduleTreeLine2);
    }
    return localVector;
  }

  public void populateSubTask(ScheduleTreeLine paramScheduleTreeLine, TCComponent paramTCComponent, String[] paramArrayOfString)
  {
    if ((paramScheduleTreeLine != null) && (paramTCComponent != null))
    {
      ScheduleTreeLine localScheduleTreeLine = null;
      TCComponent[] arrayOfTCComponent1 = null;
      try
      {
        if (((TCComponent)paramScheduleTreeLine.getUserObject()).getUid().compareToIgnoreCase(paramTCComponent.getUid()) != 0)
        {
          localScheduleTreeLine = new ScheduleTreeLine(paramTCComponent, paramArrayOfString);
          processAddWithProxyNodes(paramScheduleTreeLine, paramTCComponent, paramArrayOfString, localScheduleTreeLine);
        }
        arrayOfTCComponent1 = TaskHelper.getChildTasks((TCComponentScheduleTask)paramTCComponent);
        for (TCComponent localTCComponent : arrayOfTCComponent1)
          populateSubTask(localScheduleTreeLine, localTCComponent, paramArrayOfString);
      }
      catch (TCException localTCException)
      {
        localTCException.printStackTrace();
      }
    }
  }

  public ArrayList<TCComponent> getAllLevelTasks(TCComponent paramTCComponent)
  {
    ArrayList localArrayList1 = new ArrayList();
    if (paramTCComponent != null)
    {
      if (!localArrayList1.contains(paramTCComponent))
        localArrayList1.add(paramTCComponent);
      TCComponent[] arrayOfTCComponent = null;
      try
      {
        arrayOfTCComponent = TaskHelper.getChildTasks((TCComponentScheduleTask)paramTCComponent);
        for (int i = 0; i < arrayOfTCComponent.length; i++)
        {
          ArrayList localArrayList2 = getAllLevelTasks(arrayOfTCComponent[i]);
          Iterator localIterator = localArrayList2.iterator();
          while (localIterator.hasNext())
          {
            TCComponent localTCComponent = (TCComponent)localIterator.next();
            localArrayList1.add(localTCComponent);
          }
        }
      }
      catch (TCException localTCException)
      {
        localTCException.printStackTrace();
      }
    }
    return localArrayList1;
  }

  private void addTreeTableMouseListeners()
  {
    JTree localJTree = this.treeTable.getTree();
    this.treeTable.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent paramAnonymousMouseEvent)
      {
        if (paramAnonymousMouseEvent.getClickCount() == 2)
        {
          Point localPoint = new Point(paramAnonymousMouseEvent.getX(), paramAnonymousMouseEvent.getY());
          int i = ScheduleViewApplicationPanel.this.treeTable.rowAtPoint(localPoint);
          ScheduleTreeLine localScheduleTreeLine = ScheduleViewApplicationPanel.this.treeTable.getNodeForRow(i);
          localScheduleTreeLine.setExpandedStatus(!localScheduleTreeLine.isExpanded());
          try
          {
            IHandlerService localIHandlerService = (IHandlerService)PlatformUI.getWorkbench().getService(IHandlerService.class);
            System.out.println("currentViewType.ordinal():"+currentViewType.ordinal());
            switch (currentViewType.ordinal())
            {
            case 0:
            case 1:
              localIHandlerService.executeCommand("com.teamcenter.rac.properties", null);
              break;
            case 3:
              if (!localScheduleTreeLine.isRoot())
                localIHandlerService.executeCommand("com.teamcenter.rac.properties", null);
              break;
            case 2:
            }
          }
          catch (Exception localException)
          {
            MessageBox localMessageBox = new MessageBox(ScheduleViewApplicationPanel.this.app.getDesktop(), localException);
            localMessageBox.setModal(true);
            localMessageBox.setVisible(true);
          }
        }
      }

      public void mouseReleased(MouseEvent paramAnonymousMouseEvent)
      {
        if (SwingUtilities.isRightMouseButton(paramAnonymousMouseEvent))
        {
          Point localPoint = new Point(paramAnonymousMouseEvent.getX(), paramAnonymousMouseEvent.getY());
          int i = ScheduleViewApplicationPanel.this.treeTable.rowAtPoint(localPoint);
          int[] arrayOfInt = ScheduleViewApplicationPanel.this.treeTable.getSelectedRows();
          int j = 0;
          for (int k = 0; k < arrayOfInt.length; k++)
            if (arrayOfInt[k] == i)
              j = 1;
          if (j == 0)
            ScheduleViewApplicationPanel.this.treeTable.getTree().setSelectionRow(i);
        }
      }
    });
    localJTree.addTreeSelectionListener(new TreeSelectionListener()
    {
      public void valueChanged(TreeSelectionEvent paramAnonymousTreeSelectionEvent)
      {
        if (ScheduleViewApplicationPanel.this.ganttChart != null)
        {
          if (ScheduleViewApplicationPanel.this.currentViewType != null)
            ScheduleViewApplicationPanel.this.currentViewType.equals(VIEWTYPES.PROGRAMAUTHOR);
          if (ScheduleViewApplicationPanel.this.treeTable.getTree().getSelectionPaths() != null)
            ScheduleViewApplicationPanel.this.ganttChart.setMultiSelectedTasks(ScheduleViewApplicationPanel.this.treeTable.getSelectedComponents(), ScheduleViewApplicationPanel.this.treeTable.getTree().getSelectionPaths());
        }
        if ((ScheduleViewApplicationPanel.this.treeTable != null) && (ScheduleViewApplicationPanel.this.treeTable.getTree().getSelectionRows() != null) && (ScheduleViewApplicationPanel.this.treeTable.getTree().getSelectionRows().length > 0))
        {
          int[] arrayOfInt = ScheduleViewApplicationPanel.this.treeTable.getTree().getSelectionRows();
          Rectangle localRectangle = ScheduleViewApplicationPanel.this.treeTable.getCellRect(arrayOfInt[(arrayOfInt.length - 1)], 0, true);
          localRectangle.x += ScheduleViewApplicationPanel.this.treeScrollPane.getHorizontalScrollBar().getValue();
          localRectangle.width = 1;
          ScheduleViewApplicationPanel.this.treeTable.scrollRectToVisible(localRectangle);
        }
      }
    });
  }

  public void initialize(InterfaceAIFComponent paramInterfaceAIFComponent)
  {
    open((TCComponent)paramInterfaceAIFComponent, false, true);
  }

  public ScheduleTreeTable getTreeTable()
  {
    return this.treeTable;
  }

  public JTree getTree()
  {
    return this.treeTable == null ? null : this.treeTable.getTree();
  }

  public JScrollPane getTreeTableScrollpane()
  {
    return this.treeScrollPane;
  }

  public void expandAll()
  {
    if (this.ganttChart != null)
      this.ganttChart.setAllowLayout(false);
    if (this.treeTable != null)
      this.treeTable.expandAll();
    if (this.session != null)
    {
      this.session.queueOperation(createNewRowNumOp(this.session));
      this.session.queueOperation(new PostExpandCollapseOperation());
    }
  }

  public void collapseAll()
  {
    if (this.ganttChart != null)
      this.ganttChart.setAllowLayout(false);
    if (this.treeTable != null)
    {
      this.treeTable.collapseAll();
      invalidate();
      validate();
      repaint();
    }
    if (this.session != null)
    {
      this.session.queueOperation(createNewRowNumOp(this.session));
      this.session.queueOperation(new PostExpandCollapseOperation());
    }
  }

  public JScrollPane createGanttScrollPane()
  {
    this.ganttChart = null;
    this.ganttChartScrollPane = new JScrollPane(21, 32);
    this.ganttChartScrollPane.getViewport().setBackground(Color.white);
    this.ganttChart = new JGanttChart(this, this.ganttChartScrollPane);
    this.ganttChartScrollPane.getViewport().setView(this.ganttChart);
    this.ganttChartScrollPane.doLayout();
    this.ganttChartScrollPane.repaint();
    return this.ganttChartScrollPane;
  }

  public JGanttChart ganttChartComponent()
  {
    return this.ganttChart;
  }

  public JPanel getGanttPanelComponent()
  {
    return this.ganttPanel;
  }

  public JScrollBar getTreeScrollPaneVertScrollBar()
  {
    return this.treeScrollPaneVertScrollBar;
  }

  public MouseAdapter getTreeScrollPaneVertScrollBarMouseAdapter()
  {
    return this.treeScrollPaneVertScrollBarMouseAdapter;
  }

  public JScrollPane getGanttChartScrollPane()
  {
    return this.ganttChartScrollPane;
  }

  public void setScrollIntoView()
  {
    Runnable local23 = new Runnable()
    {
      public void run()
      {
        TCComponent localTCComponent = ScheduleViewApplicationPanel.this.getSelectedComponent();
        if (localTCComponent != null)
        {
          long l = 0L;
          try
          {
            l = TaskHelper.getStartDate(localTCComponent);
          }
          catch (TCException localTCException)
          {
            localTCException.printStackTrace();
          }
          Calendar localCalendar = Calendar.getInstance();
          localCalendar.setTimeInMillis(l);
          ScheduleViewApplicationPanel.this.ganttChart.scrollToDate(localCalendar, true);
        }
      }
    };
    if (!SwingUtilities.isEventDispatchThread())
      SwingUtilities.invokeLater(local23);
    else
      local23.run();
  }

  public void validateTree()
  {
    this.splitPanePropertyChangeContext = 0;
    super.validateTree();
    setDividerLocation();
  }

  public void processComponentEvents(AIFComponentEvent[] paramArrayOfAIFComponentEvent)
  {
    for (int i = 0; i < paramArrayOfAIFComponentEvent.length; i++)
    {
      AIFComponentEvent localAIFComponentEvent = paramArrayOfAIFComponentEvent[i];
      TCComponent localTCComponent = (TCComponent)localAIFComponentEvent.getComponent();
      if ((localTCComponent != null) && (localTCComponent.equals(this.currentSchedule)))
        if ((localAIFComponentEvent instanceof AIFComponentDeleteEvent))
        {
          if (SwingUtilities.isEventDispatchThread())
          {
            this.treeScrollPane.getViewport().removeAll();
            this.ganttPanel.removeAll();
            ScheduleViewApplication.getApplication().setLabel(null);
            this.currentSchedule = null;
            ScheduleRoleContext.setupMenuContext(null);
            this.taskField.setEnabled(false);
            this.taskField.setEditable(false);
            this.durationField.setEditable(false);
            this.durationField.setEnabled(false);
            this.createTaskBT.setEnabled(false);
          }
          else
          {
            SwingUtilities.invokeLater(new Runnable()
            {
              public void run()
              {
                ScheduleViewApplicationPanel.this.treeScrollPane.getViewport().removeAll();
                ScheduleViewApplicationPanel.this.ganttPanel.removeAll();
                ScheduleViewApplication.getApplication().setLabel(null);
                ScheduleViewApplicationPanel.this.currentSchedule = null;
                ScheduleRoleContext.setupMenuContext(null);
                ScheduleViewApplicationPanel.this.taskField.setEnabled(false);
                ScheduleViewApplicationPanel.this.taskField.setEditable(false);
                ScheduleViewApplicationPanel.this.durationField.setEditable(false);
                ScheduleViewApplicationPanel.this.durationField.setEnabled(false);
                ScheduleViewApplicationPanel.this.createTaskBT.setEnabled(false);
              }
            });
          }
        }
        else if (((localAIFComponentEvent instanceof AIFComponentChangeEvent)) && (this.currentSchedule != null))
          ScheduleViewApplication.getApplication().setLabel(this.currentSchedule.toDisplayString());
      if ((localTCComponent != null) && (this.currentProgramView != null) && (localTCComponent.equals(this.currentProgramView)) && ((localAIFComponentEvent instanceof AIFComponentChangeEvent)) && (this.currentProgramView != null))
        ScheduleViewApplication.getApplication().setLabel(this.currentProgramView.toDisplayString());
    }
  }

  public TCComponent getNextNode()
  {
    int i = this.treeTable.rowAtPoint(this.selectedPoint);
    return (TCComponent)this.treeTable.getNodeForRow(i + 1).getUserObject();
  }

  public TCComponent getPreviousNode()
  {
    int i = this.treeTable.rowAtPoint(this.selectedPoint);
    return (TCComponent)this.treeTable.getNodeForRow(i - 1).getUserObject();
  }

  public TCSession getApplicationSession()
  {
    return this.session;
  }

  public TCComponentSchedule getCurrentProject()
  {
    if (this.isSubSchedule)
      return this.subSchedule;
    return this.currentSchedule;
  }

  private void setCurrentProject(TCComponentSchedule paramTCComponentSchedule)
  {
    this.viewID = ScheduleViewApplication.generateViewID(paramTCComponentSchedule);
    ScheduleViewApplication.getApplication().setViewID(this.viewID, null);
    this.previouslyOpenedSchedule = this.currentSchedule;
    this.currentSchedule = paramTCComponentSchedule;
  }

  public TCComponentSchedule getPreviouslyOpenedSchedule()
  {
    return this.previouslyOpenedSchedule;
  }

  public ProgramMetaDataContainer getProgramContainer()
  {
    return this.programContainer;
  }

  public void setProgramContainer(ProgramMetaDataContainer paramProgramMetaDataContainer)
  {
    this.programContainer = paramProgramMetaDataContainer;
  }

  public void cloneProgramContainer()
  {
    if (this.programContainer != null)
      this.transientProgramContainer = this.programContainer.copy();
  }

  public ProgramMetaDataContainer getTransientProgramContainer()
  {
    return this.transientProgramContainer;
  }

  public void setTransientProgramContainer(ProgramMetaDataContainer paramProgramMetaDataContainer)
  {
    this.transientProgramContainer = paramProgramMetaDataContainer;
  }

  public TCComponentDataset getCurrentProgramView()
  {
    return this.currentProgramView;
  }

  public String getProgramViewXMLData()
  {
    return this.pgmViewXMLData;
  }

  public void setPgmViewXMLData(String paramString)
  {
    this.pgmViewXMLData = paramString;
  }

  public boolean isDraggable(Point paramPoint)
  {
    if (!this.canDrag)
      return false;
    Object localObject = getCurrentSelectionArray();
    if ((localObject != null) && ((localObject instanceof Object[])) && (((Object[])localObject).length > 1))
      return false;
    TreePath localTreePath = this.treeTable.getTree().getPathForLocation(paramPoint.x, paramPoint.y);
    return (localTreePath != null) && (localTreePath.getParentPath() != null);
  }

  public Object getCurrentSelectionArray()
  {
    TreePath[] arrayOfTreePath = this.treeTable.getTree().getSelectionPaths();
    if (arrayOfTreePath == null)
      return null;
    Object[] arrayOfObject = new Object[arrayOfTreePath.length];
    for (int i = 0; i < arrayOfTreePath.length; i++)
      arrayOfObject[i] = ((DefaultMutableTreeNode)arrayOfTreePath[i].getLastPathComponent()).getUserObject();
    return arrayOfObject;
  }

  public TCComponent getSelectedComponent()
  {
    if (this.treeTable != null)
    {
      int i = this.treeTable.getSelectedRow();
      if (this.treeTable.getNodeForRow(i) == null)
        return null;
      return (TCComponent)this.treeTable.getNodeForRow(i).getUserObject();
    }
    return null;
  }

  public ScheduleTreeLine getSelectedTreeLine()
  {
    ScheduleTreeLine localScheduleTreeLine = null;
    if (this.treeTable != null)
    {
      int i = this.treeTable.getSelectedRow();
      localScheduleTreeLine = this.treeTable.getNodeForRow(i);
    }
    return localScheduleTreeLine;
  }

  public ArrayList<TCComponent> getSelectedComponents()
  {
    ArrayList localArrayList = new ArrayList();
    if (this.treeTable != null)
    {
      int[] arrayOfInt = this.treeTable.getSelectedRows();
      int i = arrayOfInt.length;
      for (int j = 0; j < i; j++)
      {
        ScheduleTreeLine localScheduleTreeLine = this.treeTable.getNodeForRow(arrayOfInt[j]);
        if ((localScheduleTreeLine != null) && (localScheduleTreeLine.getUserObject() != null))
          localArrayList.add((TCComponent)localScheduleTreeLine.getUserObject());
      }
    }
    return localArrayList;
  }

  public void setEnableOptions(boolean paramBoolean)
  {
    final boolean bool = paramBoolean;
    Runnable local25 = new Runnable()
    {
      public void run()
      {
        ScheduleViewApplicationPanel.this.taskField.setEditable(bool);
        ScheduleViewApplicationPanel.this.taskField.setEnabled(bool);
        ScheduleViewApplicationPanel.this.durationField.setEditable(bool);
        ScheduleViewApplicationPanel.this.durationField.setEnabled(bool);
      }
    };
    if (SwingUtilities.isEventDispatchThread())
      local25.run();
    else
      SwingUtilities.invokeLater(local25);
  }

  public void setProgramOptions(boolean paramBoolean)
  {
  }

  public boolean isProgramView()
  {
    return this.currentViewType.equals(VIEWTYPES.PROGRAMVIEW);
  }

  public void setAsProgramview()
  {
    this.currentViewType = VIEWTYPES.PROGRAMVIEW;
  }

  public boolean isScheduleModifiable()
  {
    boolean bool = false;
    TCComponentSchedule localTCComponentSchedule = getCurrentProject();
    if (localTCComponentSchedule != null)
      bool = ScheduleUtil.canChangeSchedulingData(localTCComponentSchedule);
    return bool;
  }

  public void save()
  {
    if ((this.app != null) && (this.rootSchTreeLine != null) && (isProgramView()))
      saveProgramViewOnClose();
    if ((this.session != null) && (this.treeTable != null))
      this.session.removeAIFComponentEventListener(this.treeTable);
  }

  public RowNumUpdateOperation createNewRowNumOp(AbstractAIFSession paramAbstractAIFSession)
  {
    RowNumUpdateOperation localRowNumUpdateOperation = new RowNumUpdateOperation(this.session);
    return localRowNumUpdateOperation;
  }

  private String getDefaultExportDirectory()
  {
    TCPreferenceService localTCPreferenceService = this.session.getPreferenceService();
    String str = localTCPreferenceService.getString(0, "defaultExportDirectory");
    if (str != null)
    {
      str = str.trim();
      if (str.length() == 0)
        str = null;
    }
    return str;
  }

  public void loadProgramView()
  {
    try
    {
      TCComponent[] arrayOfTCComponent = getCurrentProgramView().getNamedReferences();
      Object localObject;
      if ((arrayOfTCComponent.length > 0) && ((arrayOfTCComponent[0] instanceof TCComponentTcFile)))
      {
        if (this.programContainer != null)
          if (this.programViewResponse == null)
          {
            localObject = (TCComponentTcFile)arrayOfTCComponent[0];
            String str = loadProgramViewMetaDataFromVolume((TCComponentTcFile)localObject);
            this.programContainer = ProgramPreferencesUtil.createProgramMetaData(str);
            if (this.programContainer.getProgramFilter() == null)
              this.programContainer.setProgramFilter(new ArrayList());
            ProgramUtil.loadData(this.session, (ScheduleViewApplication)this.app);
            this.pgmViewXMLData = str;
          }
          else
          {
            ProgramUtil.loadGantt(this.session, (ScheduleViewApplication)this.app, this.programViewResponse);
            this.programViewResponse = null;
          }
      }
      else
      {
        if (getProgramContainer() == null)
          this.programContainer = new ProgramMetaDataContainer();
        if ((getProgramContainer().getDataSetUID() == null) && (this.rootSchTreeLine != null) && ((this.rootSchTreeLine.getComponent() instanceof TCComponentDataset)))
        {
          localObject = (TCComponentDataset)this.rootSchTreeLine.getComponent();
          getProgramContainer().setDataSetUID(((TCComponentDataset)localObject).getUid());
        }
        this.pgmViewXMLData = ProgramPreferencesUtil.transformToXML(getProgramContainer());
      }
    }
    catch (TCException localTCException)
    {
      localTCException.printStackTrace();
    }
  }

  public String loadProgramViewMetaDataFromVolume(TCComponentTcFile paramTCComponentTcFile)
  {
    StringBuffer localStringBuffer = null;
    try
    {
      File localFile = paramTCComponentTcFile.getFile(getDefaultExportDirectory());
      if (localFile != null)
      {
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(localFile.toString()), Charset.defaultCharset()));
        try
        {
          localStringBuffer = new StringBuffer("");
          for (String str = localBufferedReader.readLine(); str != null; str = localBufferedReader.readLine())
            localStringBuffer.append(str);
        }
        catch (IOException localIOException)
        {
          logger.error("IOException in loadProgramViewMetaDataFromVolume() ", localIOException);
        }
        localBufferedReader.close();
      }
    }
    catch (Exception localException)
    {
      logger.error("Exception in loadProgramViewMetaDataFromVolume() ", localException);
    }
    if (localStringBuffer == null)
      return null;
    return localStringBuffer.toString();
  }

  public boolean isDirty()
  {
    boolean bool = true;
    if (ScheduleDeferredContext.inDeferredSession())
    {
    	//$SWITCH_TABLE$com$teamcenter$rac$schedule$commands$deferred$ScheduleDeferredContext$STATUS()[ScheduleDeferredContext.context.ordinal()]
     System.out.println("isDirty ScheduleDeferredContext.context.ordinal():"+ScheduleDeferredContext.context.ordinal());
    	switch (ScheduleDeferredContext.context.ordinal())
      {
    	case 0:
      case 1:
        bool = false;
        break;
      case 2:
        bool = true;
      default:
        break;
      }
    }
    else if (!isProgramView())
    {
      bool = false;
    }
    else if ((this.rootSchTreeLine == null) || (!(this.rootSchTreeLine.getComponent() instanceof TCComponentDataset)) || (getProgramContainer() == null))
    {
      bool = false;
    }
    else
    {
      TCComponentDataset localTCComponentDataset = (TCComponentDataset)this.rootSchTreeLine.getComponent();
      if (getProgramContainer().getDataSetUID() == null)
        getProgramContainer().setDataSetUID(localTCComponentDataset.getUid());
      this.xmlContent = ProgramPreferencesUtil.transformToXML(getProgramContainer());
      if ((this.pgmViewXMLData != null) && (this.pgmViewXMLData.equals(this.xmlContent)))
        bool = false;
    }
    return bool;
  }

  protected String getProgramViewSaveTitle()
  {
    return this.r.getString("programViewSave.TITLE");
  }

  protected String getProgramViewStringForSave()
  {
    return this.r.getString("programViewSave.MSG");
  }

  private void getXmlContent()
  {
    if (this.xmlContent == null)
      this.xmlContent = ProgramPreferencesUtil.transformToXML(getProgramContainer());
  }

  public void setXMLContent(String paramString)
  {
    this.xmlContent = paramString;
  }

  private String convertDatasetDisplayNameToValidFileName(String paramString)
  {
    String str1 = "/";
    String str2 = "";
    if (paramString.contains(str1))
      str2 = paramString.replace('/', '_');
    str1 = "\\";
    if (str2.contains(str1))
      str2 = str2.replace('\\', '_');
    str1 = ":";
    if (str2.contains(str1))
      str2 = str2.replace(':', '_');
    str1 = "*";
    if (str2.contains(str1))
      str2 = str2.replace('*', '_');
    str1 = "?";
    if (str2.contains(str1))
      str2 = str2.replace('?', '_');
    str1 = "<";
    if (str2.contains(str1))
      str2 = str2.replace('<', '_');
    str1 = ">";
    if (str2.contains(str1))
      str2 = str2.replace('>', '_');
    str1 = "|";
    if (str2.contains(str1))
      str2 = str2.replace('|', '_');
    str1 = "\"";
    if (str2.contains(str1))
      str2 = str2.replace('"', '_');
    return str2;
  }

  public void writeToXml(TCComponent[] paramArrayOfTCComponent, TCComponentDataset paramTCComponentDataset, boolean paramBoolean)
    throws IOException, TCException
  {
    File localFile = null;
    if ((paramArrayOfTCComponent == null) || ((paramArrayOfTCComponent != null) && (paramArrayOfTCComponent.length == 0)))
    {
    	String localObject1 = paramTCComponentDataset.toString();
    	String localObject2 = convertDatasetDisplayNameToValidFileName((String)localObject1);
      if (((String)localObject2).length() < 3)
        localObject2 = localObject2 + "_PREFERENCE";
      localFile = File.createTempFile((String)localObject2, ".xml");
      paramBoolean = false;
    }
    else
    {
      localFile = ((TCComponentTcFile)paramArrayOfTCComponent[0]).getFile(getDefaultExportDirectory());
      paramBoolean = true;
    }
    Object localObject4;
    Object localObject3;
    if (localFile.canWrite())
    {
      FileOutputStream localObject1 = null;
      FileChannel localObject2 = null;
      localObject4 = TCSession.getServerEncodingName(this.session);
      try
      {
        localObject1 = new FileOutputStream(localFile);
        localObject2 = ((FileOutputStream)localObject1).getChannel();
        localObject3 = ByteBuffer.allocateDirect(this.xmlContent.getBytes((String)localObject4).length);
        ((ByteBuffer)localObject3).put(this.xmlContent.getBytes((String)localObject4));
        ((ByteBuffer)localObject3).rewind();
        ((FileChannel)localObject2).write((ByteBuffer)localObject3);
        ((FileOutputStream)localObject1).close();
      }
      finally
      {
        if (localObject1 != null)
          ((FileOutputStream)localObject1).close();
        if ((localObject2 != null) && (((FileChannel)localObject2).isOpen()))
          ((FileChannel)localObject2).close();
      }
    }
    Object localObject1 = paramTCComponentDataset.getSession();
    Object localObject2 = Registry.getRegistry(this);
    ((AbstractAIFSession)localObject1).setStatus(((Registry)localObject2).getString("importingFiles") + " " + localFile.toString());
    try
    {
      paramTCComponentDataset.lock();
      if (paramBoolean)
      {
        localObject3 = new String[] { localFile.getName() };
        localObject4 = new String[] { "XML" };
        paramTCComponentDataset.removeFiles((String[])localObject3, (String[])localObject4);
      }
      localObject3 = new ImportFilesOperation(paramTCComponentDataset, localFile, "Text", null, "XML", Utilities.getCurrentFrame());
      ((AbstractAIFSession)localObject1).queueOperation((Job)localObject3);
      paramTCComponentDataset.unlock();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      paramTCComponentDataset.unlock();
    }
    ((AbstractAIFSession)localObject1).setReadyStatus();
  }

  public void saveProgramView()
  {
    try
    {
      boolean bool = false;
      TCComponentDataset localTCComponentDataset = (TCComponentDataset)this.rootSchTreeLine.getComponent();
      TCComponent[] arrayOfTCComponent = localTCComponentDataset.getNamedReferences();
      getXmlContent();
      if (!isDirty())
        return;
      try
      {
        writeToXml(arrayOfTCComponent, localTCComponentDataset, bool);
        this.pgmViewXMLData = this.xmlContent;
        cloneProgramContainer();
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
    }
    catch (TCException localTCException)
    {
      localTCException.printStackTrace();
    }
  }

  public void saveProgramViewOnClose()
  {
    try
    {
      TCComponentDataset localTCComponentDataset = getCurrentProgramView();
      if ((localTCComponentDataset != null) && (localTCComponentDataset.getLogicalProperty("is_modifiable")))
        saveProgramView();
      else
        MessageBox.post(this.r.getString("ProgramViewNoAccess.MSG"), this.r.getString("programViewSave.TITLE"), 4);
    }
    catch (Exception localException)
    {
      MessageBox localMessageBox = new MessageBox(localException);
      localMessageBox.setModal(true);
      localMessageBox.setVisible(true);
    }
  }

  public void restoreTree()
  {
    restoreTreeNode(getTree(), new TreePath(getTree().getModel().getRoot()), null);
  }

  private void restoreTreeNode(JTree paramJTree, TreePath paramTreePath, DefaultMutableTreeNode paramDefaultMutableTreeNode)
  {
    TreeNode localTreeNode = (TreeNode)paramTreePath.getLastPathComponent();
    if (localTreeNode.getChildCount() >= 0)
    {
      Enumeration localEnumeration = localTreeNode.children();
      while (localEnumeration.hasMoreElements())
      {
        DefaultMutableTreeNode localDefaultMutableTreeNode = (DefaultMutableTreeNode)localEnumeration.nextElement();
        TreePath localTreePath = paramTreePath.pathByAddingChild(localDefaultMutableTreeNode);
        restoreTreeNode(paramJTree, localTreePath, localDefaultMutableTreeNode);
      }
    }
    if ((paramDefaultMutableTreeNode != null) && ((paramDefaultMutableTreeNode instanceof ScheduleTreeLine)))
      if (this.expandedTreeObjects.contains(((ScheduleTreeLine)paramDefaultMutableTreeNode).generateKey()))
        paramJTree.expandPath(paramTreePath);
      else
        paramJTree.collapsePath(paramTreePath);
  }

  public List<String> getExpandedTreeObjects()
  {
    return this.expandedTreeObjects;
  }

  public void closeSignaled()
  {
    if ((this.app != null) && (this.rootSchTreeLine != null))
    {
      Iterator localIterator = this.openedComponents.iterator();
      while (localIterator.hasNext())
      {
        InterfaceAIFComponent localInterfaceAIFComponent = (InterfaceAIFComponent)localIterator.next();
        ClientEventDispatcher.fireEventLater(this, "com/teamcenter/rac/aifrcp/component/Close", new Object[] { InterfaceAIFComponent.class, localInterfaceAIFComponent, "perspectiveId", "com.teamcenter.rac.schedule.ScheduleManagerPerspective" });
      }
      this.openedComponents.clear();
    }
    if (ScheduleDeferredContext.inDeferredSession())
      try
      {
        new ReleaseDeferred(false).execute(null);
        ScheduleDeferredContext.setContext(false);
      }
      catch (ExecutionException localExecutionException)
      {
        localExecutionException.printStackTrace();
      }
    if (this.session != null)
      this.session.removeAIFComponentEventListener(this);
  }

  public void populateProxyMap(TCComponent paramTCComponent, boolean paramBoolean)
    throws TCException
  {
    if (paramBoolean)
      this.proxyMap.clear();
    TCComponent localTCComponent1 = paramTCComponent.getReferenceProperty("fnd0SummaryTask");
    TCComponent[] arrayOfTCComponent = paramTCComponent.getReferenceListProperty("fnd0ProxyTasks");
    if ((arrayOfTCComponent != null) && (arrayOfTCComponent.length > 0))
    {
      ArrayList localArrayList = new ArrayList();
      TCComponent localTCComponent2;
      for (TCComponent eachlocalTCComponent2 : arrayOfTCComponent)
        localArrayList.add((TCComponentFnd0ProxyTask)eachlocalTCComponent2);
      Collections.sort(localArrayList, new ProxyTaskComparator());
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        localTCComponent2 = (TCComponent)localIterator.next();
        if (localTCComponent2 != null)
        {
          TCComponent localTCComponent3 = localTCComponent2.getReferenceProperty("fnd0ref");
          if ((localTCComponent3 == null) || (TaskHelper.isOrphaned(localTCComponent3)))
            localTCComponent3 = localTCComponent1;
//          ??? = RACInterfaceFactory.singleton().getRACInterface(this.session, this.currentSchedule);
          RACInterface racInterface = RACInterfaceFactory.singleton().getRACInterface(this.session, this.currentSchedule);
          if (((RACInterface)racInterface).getHomeTask(localTCComponent2, this.currentSchedule, true) == null)
          {
            addToProxyMap(localTCComponent3, localTCComponent2);
          }
          else
          {
            Collection localCollection = ((RACInterface)racInterface).getPredecessorDependencies(localTCComponent2);
            if ((localCollection == null) || (localCollection.size() == 0))
            {
              localCollection = ((RACInterface)racInterface).getSuccessorDependencies(localTCComponent2);
              if ((localCollection == null) || (localCollection.size() == 0))
                addToProxyMap(localTCComponent3, localTCComponent2);
            }
          }
        }
      }
    }
  }

  private void addToProxyMap(TCComponent paramTCComponent1, TCComponent paramTCComponent2)
  {
    try
    {
      if (TaskHelper.isLinkedTask(paramTCComponent1))
      {
        for (TCComponent localTCComponent1 = paramTCComponent1.getReferenceProperty("fnd0ref"); 
        		(localTCComponent1 != null) && (TaskHelper.isLinkedTask(localTCComponent1)); 
        		localTCComponent1 = localTCComponent1.getReferenceProperty("fnd0ref"))
		        {
		        	if (localTCComponent1 == null)
		            {
		              TCComponent localTCComponent2 = TaskHelper.getScheduleForProxyTask(paramTCComponent2);
		              localTCComponent1 = TaskHelper.getSummaryTaskForSchedule(localTCComponent2);
		            }
		            if (localTCComponent1 != null)
		              paramTCComponent1 = localTCComponent1;
		            else
		              logger.info("Was not able to find reference task for proxy " + paramTCComponent2);
		        }
      }
    }
    catch (TCException localTCException)
    {
      logger.error("TCException", localTCException);
    }
    List localObject = (List)this.proxyMap.get(paramTCComponent1);
    if (localObject == null)
    {
      localObject = new ArrayList();
      this.proxyMap.put(paramTCComponent1, localObject);
    }
    if (!((List)localObject).contains(paramTCComponent2))
      ((List)localObject).add(paramTCComponent2);
  }

  public List<TCComponent> getReferencedProxyTasks(TCComponent paramTCComponent)
  {
    List localList = null;
    if (this.proxyMap.containsKey(paramTCComponent))
      localList = (List)this.proxyMap.get(paramTCComponent);
    return localList;
  }

  public void addReferencedProxyTasks(TCComponent paramTCComponent1, TCComponent paramTCComponent2)
  {
	List localObject = this.proxyMap.get(paramTCComponent1);
    if (localObject == null)
    {
      localObject = new ArrayList();
      this.proxyMap.put(paramTCComponent1, localObject);
    }
    localObject.add(paramTCComponent2);
  }

  public void removeReferencedProxyTasks(TCComponent paramTCComponent)
  {
    Iterator localIterator = this.proxyMap.values().iterator();
    while (localIterator.hasNext())
    {
      List localList = (List)localIterator.next();
      if (localList.contains(paramTCComponent))
        localList.remove(paramTCComponent);
    }
  }

  public AbstractAIFUIApplication getAbstractAIFUIApplication()
  {
    return this.app;
  }

  public TCComponent getSubScheduleToView()
  {
    return this.subSchedule;
  }

  public void setSubScheduleToView(TCComponent paramTCComponent)
  {
    this.isSubSchedule = true;
    this.viewID = ScheduleViewApplication.generateViewID(paramTCComponent);
    this.subSchedule = ((TCComponentSchedule)paramTCComponent);
  }

  public static int getPartialContexOptiontFromTCPreference(TCSession paramTCSession)
  {
    int i;
    try
    {
      TCPreferenceService localTCPreferenceService = paramTCSession.getPreferenceService();
      i = localTCPreferenceService.getInt(1, "SM_Structure_Partial_Context", 0);
      if ((i > 1) || (i < 0))
        i = 0;
      setLoadScheduleOption("SM_Structure_Partial_Context", i);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      i = 0;
    }
    return i;
  }

  public static void setLoadContextOptionToDefault()
  {
    try
    {
      setLoadContextOption(0);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public static void setLoadContextOption(int paramInt)
  {
    try
    {
      setLoadScheduleOption("SM_Structure_Load_Context", paramInt);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public static void setLoadScheduleOption(String paramString, int paramInt)
  {
    loadScheduleoptions.put(paramString, Integer.valueOf(paramInt));
  }

  public static int getLoadScheduleOption(String paramString)
    throws Exception
  {
    Integer localInteger;
    if ((localInteger = (Integer)loadScheduleoptions.get(paramString)) == null)
      localInteger = Integer.valueOf(paramString.equalsIgnoreCase("SM_Structure_Client_Context") ? 0 : paramString.equalsIgnoreCase("SM_Structure_Load_Context") ? 0 : paramString.equalsIgnoreCase("SM_Structure_Partial_Context") ? 0 : -1);
    int i = -1;
    if ((paramString.equalsIgnoreCase("SM_Structure_Partial_Context")) && ((i = ((ScheduleViewApplicationPanel)ScheduleViewApplication.getApplication().getApplicationPanel()).getInstancePartialContext()) != -1))
      localInteger = Integer.valueOf(i);
    return localInteger.intValue();
  }

  public static HashMap<String, Integer> getIntLoadScheduleOptions()
  {
    return loadScheduleoptions;
  }

  public static LoadScheduleOptions getLoadScheduleOptions()
  {
    LoadScheduleOptions localLoadScheduleOptions = new LoadScheduleOptions();
    localLoadScheduleOptions.setIntOptions(loadScheduleoptions);
    return localLoadScheduleOptions;
  }

  public void setInstancePartialContext(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 1))
    {
      NumberFormatException localNumberFormatException = new NumberFormatException("Partial Context: the value specified for the context is unsupported");
      localNumberFormatException.printStackTrace();
      return;
    }
    this.instance_pref_partial_context = paramInt;
  }

  public int getInstancePartialContext()
  {
    return this.instance_pref_partial_context;
  }

  public void setSupressExpansionEvent(boolean paramBoolean)
  {
    this.supressExpansionEvent = paramBoolean;
  }

  public boolean getSupressExpansionEvent()
  {
    return this.supressExpansionEvent;
  }

  public boolean getStructureAddingSiblings()
  {
    return this.structureAddingSiblings;
  }

  public void setStructureAddingSiblings(boolean paramBoolean)
  {
    this.structureAddingSiblings = paramBoolean;
  }

  public ScheduleTreeLine getRootScheduleTreeLine()
  {
    return this.rootSchTreeLine;
  }

  public String getViewID()
  {
    return this.viewID;
  }

  public void setLastDateModified(Date paramDate)
  {
    this.lastDateModified = paramDate;
  }

  public Date getLastDateModified()
  {
    return this.lastDateModified;
  }

  private MouseAdapter createTreeScrollPaneVerticalScrollBarMouseAdapter()
  {
    MouseAdapter local26 = new MouseAdapter()
    {
      public void mouseReleased(MouseEvent paramAnonymousMouseEvent)
      {
      }
    };
    return local26;
  }

  private KeyListener createTreeTableKeylistener()
  {
    KeyListener local27 = new KeyListener()
    {
      public void keyTyped(KeyEvent paramAnonymousKeyEvent)
      {
      }

      public void keyPressed(KeyEvent paramAnonymousKeyEvent)
      {
      }

      public void keyReleased(KeyEvent paramAnonymousKeyEvent)
      {
        switch (paramAnonymousKeyEvent.getKeyCode())
        {
        case 33:
        case 34:
        case 38:
        case 40:
          ScheduleViewApplicationPanel.this.fastRefresh(0);
        case 35:
        case 36:
        case 37:
        case 39:
        }
      }
    };
    return local27;
  }

  private void fastRefresh(int paramInt)
  {
    if (this.ganttChart != null)
      try
      {
        setCursor(Cursor.getPredefinedCursor(3));
        this.treeTable.removeKeyListener(this.treeTableKeyListener);
        this.treeScrollPaneVertScrollBar.removeMouseListener(this.treeScrollPaneVertScrollBarMouseAdapter);
        this.treeScrollPaneVertScrollBar.setEnabled(false);
        if (SwingUtilities.isEventDispatchThread())
          runFastRefresh();
        else
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              ScheduleViewApplicationPanel.this.runFastRefresh();
            }
          });
      }
      catch (Exception localException)
      {
        this.treeTable.addKeyListener(this.treeTableKeyListener);
        this.treeScrollPaneVertScrollBar.addMouseListener(this.treeScrollPaneVertScrollBarMouseAdapter);
        this.treeScrollPaneVertScrollBar.setEnabled(true);
        ganttViewRectNumberOfRows = getGanttViewRectNumberOfRows();
        ganttRowCounter = 0;
        scrollBarWasAdjusting = false;
        setCursor(Cursor.getPredefinedCursor(0));
        logger.error("Exception in fastRefresh() ", localException);
      }
  }

  private void runFastRefresh()
  {
    Job local29;
    try
    {
      this.ganttChart.fastRefresh();
    }
    finally
    {
      local29 = new Job(this.r.getString("thread.job.name.refresh"))
      {
        protected IStatus run(IProgressMonitor paramAnonymousIProgressMonitor)
        {
          ScheduleViewApplicationPanel.this.treeTable.addKeyListener(ScheduleViewApplicationPanel.this.treeTableKeyListener);
          ScheduleViewApplicationPanel.this.treeScrollPaneVertScrollBar.addMouseListener(ScheduleViewApplicationPanel.this.treeScrollPaneVertScrollBarMouseAdapter);
          ScheduleViewApplicationPanel.this.treeScrollPaneVertScrollBar.setEnabled(true);
          ScheduleViewApplicationPanel.ganttViewRectNumberOfRows = ScheduleViewApplicationPanel.this.getGanttViewRectNumberOfRows();
          ScheduleViewApplicationPanel.ganttRowCounter = 0;
          ScheduleViewApplicationPanel.scrollBarWasAdjusting = false;
          return Status.OK_STATUS;
        }
      };
      local29.schedule(300L);
      local29.setPriority(10);
      setCursor(Cursor.getPredefinedCursor(0));
    }
  }

  private TreePath runExpand()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode = (DefaultMutableTreeNode)getTree().getModel().getRoot();
    HashSet localHashSet = new HashSet();
    Enumeration localEnumeration = localDefaultMutableTreeNode.preorderEnumeration();
    Object localObject2;
    while (localEnumeration.hasMoreElements())
    {
    	DefaultMutableTreeNode localObject1 = (DefaultMutableTreeNode)localEnumeration.nextElement();
      localObject2 = this.newTaskRefIdCont.getObjectIDs();
      Iterator localIterator = ((Collection)localObject2).iterator();
      while (localIterator.hasNext())
      {
    	  String localObject3 = (String)localIterator.next();
        Object localObject4 = ((DefaultMutableTreeNode)localObject1).getUserObject();
        if ((localObject4 instanceof TCComponent))
        {
          TCComponent localTCComponent = (TCComponent)localObject4;
          if (((String)localObject3).equals(localTCComponent.getUid()))
            localHashSet.add(localObject1);
        }
      }
      if (localHashSet.size() >= this.newTaskRefIdCont.getObjectIDs().size())
        break;
    }
    TreePath localObject1 = null;
    Object localObject3 = localHashSet.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject2 = (DefaultMutableTreeNode)((Iterator)localObject3).next();
      try
      {
        localObject1 = new TreePath(((DefaultMutableTreeNode)localObject2).getPath());
        if (getTree().isExpanded((TreePath)localObject1))
        {
          getTree().collapsePath((TreePath)localObject1);
          getTree().expandPath((TreePath)localObject1);
        }
        else
        {
          getTree().expandPath((TreePath)localObject1);
          getTree().collapsePath((TreePath)localObject1);
        }
        getTree().setSelectionPath((TreePath)localObject1);
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    }
    return localObject1;
  }

  private void initMenuToggles()
  {
    if (this.session != null)
    {
      TCPreferenceService localTCPreferenceService = this.session.getPreferenceService();
      boolean bool = localTCPreferenceService.isTrue(1, TaskHelper.VIEW_CRITICALPATH_PREF);
      Activator.getDefault().setToggleProperty("SM_View_CriticalPath", bool, this);
      SchedulingEnginePrefs localSchedulingEnginePrefs = ModelFactory.singleton(this.session).getSchedulingEnginePrefs();
      localSchedulingEnginePrefs.setViewCriticalPath(bool);
    }
  }

  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if ((paramPropertyChangeEvent != null) && ("SM_View_CriticalPath".equals(paramPropertyChangeEvent.getPropertyName())) && (paramPropertyChangeEvent.getNewValue() != null))
    {
      boolean bool1 = Boolean.parseBoolean(paramPropertyChangeEvent.getNewValue().toString());
      if ((ganttChartComponent() != null) && (getTreeTable() != null) && (getCurrentProject() != null))
      {
        TCComponentSchedule localTCComponentSchedule = getCurrentProject();
        TCSession localTCSession = ganttChartComponent().getSession();
        TCPreferenceService localTCPreferenceService = localTCSession.getPreferenceService();
        SchedulingEnginePrefs localSchedulingEnginePrefs = ModelFactory.singleton(localTCSession).getSchedulingEnginePrefs();
        try
        {
          boolean bool2 = localTCPreferenceService.isTrue(1, TaskHelper.VIEW_CRITICALPATH_PREF);
          if (bool2 != bool1)
            localTCPreferenceService.setString(1, TaskHelper.VIEW_CRITICALPATH_PREF, bool1 ? "true" : "false");
        }
        catch (TCException localTCException1)
        {
          Logger.getLogger(getClass()).error("Unable to set CriticalPath Pref", localTCException1);
        }
        ganttChartComponent().setViewCrticalPath(bool1);
        getTreeTable().setViewCrticalPath(bool1);
        localSchedulingEnginePrefs.setViewCriticalPath(bool1);
        if (bool1)
        {
        	RACInterface localObject = RACInterfaceFactory.singleton().getRACInterface(localTCComponentSchedule.getSession(), localTCComponentSchedule);
          if (localObject != null)
            ((RACInterface)localObject).clearCriticalPathTasks();
          Collection localCollection = null;
          try
          {
            localCollection = ScheduleHelper.getSubSchedules(localTCComponentSchedule, true);
            Iterator localIterator = localCollection.iterator();
            while (localIterator.hasNext())
            {
              TCComponent localTCComponent = (TCComponent)localIterator.next();
              RACInterface localRACInterface = RACInterfaceFactory.singleton().getRACInterface(localTCSession, localTCComponent);
              if (localRACInterface != null)
                localRACInterface.clearCriticalPathTasks();
            }
          }
          catch (TCException localTCException2)
          {
            localTCException2.printStackTrace();
          }
        }
        Object localObject = new Runnable()
        {
          public void run()
          {
            ScheduleViewApplicationPanel.this.repaint();
          }
        };
        if (SwingUtilities.isEventDispatchThread())
          ((Runnable)localObject).run();
        else
          SwingUtilities.invokeLater((Runnable)localObject);
      }
    }
  }

  private int getGanttViewRectNumberOfRows()
  {
    int i = this.ganttChart.getRowHeight();
    return getGanttViewRectHalfHeight() / i;
  }

  private int getGanttViewRectHalfHeight()
  {
    Rectangle localRectangle = this.ganttChart.getViewRect();
    int i = localRectangle.height / 2;
    return i;
  }

  public boolean isDurationInEditMode()
  {
    return this.durationInEditMode;
  }

  public boolean isTaskInEditMode()
  {
    return this.taskInEditMode;
  }

  public void mouseClicked(MouseEvent paramMouseEvent)
  {
    Job local31 = new Job("")
    {
      protected IStatus run(IProgressMonitor paramAnonymousIProgressMonitor)
      {
        Display.getDefault().syncExec(new Runnable()
        {
          public void run()
          {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(ScheduleViewApplication.getScheduleManagerView());
          }
        });
        return Status.OK_STATUS;
      }
    };
    local31.schedule();
  }

  public void mousePressed(MouseEvent paramMouseEvent)
  {
  }

  public void mouseReleased(MouseEvent paramMouseEvent)
  {
  }

  public void mouseEntered(MouseEvent paramMouseEvent)
  {
  }

  public void mouseExited(MouseEvent paramMouseEvent)
  {
  }

  public void setDividerLocation()
  {
    this.splitPanePropertyChangeContext = 1;
    this.splitPane.setDividerLocation(this.splilPaneDividerLocation);
  }

  public TCComponentDynamicTaskLine getTaskLine()
  {
    return this.taskLine;
  }

  public int getLoadSchedulePending()
  {
    return this.loadSchedulePending;
  }

  public ScheduleTreeLine getRootSchTreeLine()
  {
    return this.rootSchTreeLine;
  }

  public ScheduleTreeTable getScheduleTreeTable()
  {
    return this.treeTable;
  }

  public class Memento
  {
    private HashSet<String> state = new HashSet();
    private String ScheduleUid = "";

    public Memento()
    {
    }

    public void SaveState()
    {
      try
      {
        this.state.clear();
        this.ScheduleUid = ScheduleViewApplicationPanel.this.getCurrentProject().getUid();
        if ((ScheduleViewApplicationPanel.this.treeTable != null) && (ScheduleViewApplicationPanel.this.treeTable.getRoot() != null) && (ScheduleViewApplicationPanel.this.treeTable.getRoot().getChildCount() > 0))
        {
          Vector localVector = new Vector();
          ScheduleViewApplicationPanel.this.treeTable.getAllTreeNodes(ScheduleViewApplicationPanel.this.treeTable.getRoot(), localVector);
          Iterator localIterator = localVector.iterator();
          while (localIterator.hasNext())
          {
            ScheduleTreeLine localScheduleTreeLine = (ScheduleTreeLine)localIterator.next();
            TCComponent localTCComponent = (TCComponent)localScheduleTreeLine.getUserObject();
            if ((localScheduleTreeLine.getChildCount() > 0) && (TaskHelper.isSummaryStructure(localTCComponent)))
            {
              TreePath localTreePath = new TreePath(localScheduleTreeLine.getPath());
              if (ScheduleViewApplicationPanel.this.getTree().isExpanded(localTreePath))
                this.state.add(localTCComponent.getUid());
            }
          }
        }
      }
      catch (Exception localException)
      {
        ScheduleViewApplicationPanel.logger.error("Exception while handling memento SaveState", localException);
        localException.printStackTrace();
      }
    }

    public HashSet<String> getState()
    {
      return this.state;
    }

    public String getOwner()
    {
      return this.ScheduleUid;
    }

    public void restoreState()
    {
      try
      {
        if (ScheduleViewApplicationPanel.this.getCurrentProject() == null)
          return;
        if (!this.ScheduleUid.equals(ScheduleViewApplicationPanel.this.getCurrentProject().getUid()))
          return;
        if ((ScheduleViewApplicationPanel.this.treeTable != null) && (ScheduleViewApplicationPanel.this.treeTable.getRoot().getChildCount() > 0))
        {
          TreePath localTreePath = null;
          Vector localVector = new Vector();
          ScheduleViewApplicationPanel.this.treeTable.getAllTreeNodes(ScheduleViewApplicationPanel.this.treeTable.getRoot(), localVector);
          Iterator localIterator = localVector.iterator();
          while (localIterator.hasNext())
          {
            ScheduleTreeLine localScheduleTreeLine = (ScheduleTreeLine)localIterator.next();
            TCComponent localTCComponent = (TCComponent)localScheduleTreeLine.getUserObject();
            if ((localScheduleTreeLine.getChildCount() > 0) && (TaskHelper.isSummaryStructure(localTCComponent)) && (this.state.contains(localTCComponent.getUid())))
            {
              localTreePath = new TreePath(localScheduleTreeLine.getPath());
              if (!ScheduleViewApplicationPanel.this.getTree().isExpanded(localTreePath))
                ScheduleViewApplicationPanel.this.getTree().expandPath(localTreePath);
            }
          }
          if (localTreePath != null)
          {
            boolean bool = ScheduleViewApplicationPanel.this.getTree().getExpandsSelectedPaths();
            ScheduleViewApplicationPanel.this.getTree().setExpandsSelectedPaths(true);
            ScheduleViewApplicationPanel.this.getTree().setSelectionPath(localTreePath);
            ScheduleViewApplicationPanel.this.getTree().setExpandsSelectedPaths(bool);
          }
        }
      }
      catch (Exception localException)
      {
        ScheduleViewApplicationPanel.logger.error("Exception while handling memento restoreState", localException);
        localException.printStackTrace();
      }
    }

    public void restoreStateob()
    {
      Job local1 = new Job("restoreState()")
      {
        protected IStatus run(IProgressMonitor paramAnonymousIProgressMonitor)
        {
          Display.getDefault().syncExec(new Runnable()
          {
            public void run()
            {
              if (SwingUtilities.isEventDispatchThread())
                ScheduleViewApplicationPanel.Memento.this.doMementoWork();
              else
                SwingUtilities.invokeLater(new Runnable()
                {
                  public void run()
                  {
                    ScheduleViewApplicationPanel.Memento.this.doMementoWork();
                  }
                });
            }
          });
          return Status.OK_STATUS;
        }
      };
      local1.setPriority(50);
      local1.schedule(500L);
    }

    private void doMementoWork()
    {
      try
      {
        if (ScheduleViewApplicationPanel.this.getCurrentProject() == null)
          return;
        if (!this.ScheduleUid.equals(ScheduleViewApplicationPanel.this.getCurrentProject().getUid()))
          return;
        if ((ScheduleViewApplicationPanel.this.treeTable != null) && (ScheduleViewApplicationPanel.this.treeTable.getRoot().getChildCount() > 0))
        {
          AIFTreeLine[] arrayOfAIFTreeLine = ScheduleViewApplicationPanel.this.treeTable.getSelectedLines();
          TreePath localTreePath = null;
          ScheduleTreeLine[] arrayOfScheduleTreeLine1 = (ScheduleTreeLine[])ScheduleViewApplicationPanel.this.treeTable.getRoot().getChildNodes();
          for (ScheduleTreeLine localScheduleTreeLine : arrayOfScheduleTreeLine1)
          {
            TCComponent localTCComponent = (TCComponent)localScheduleTreeLine.getUserObject();
            if ((localScheduleTreeLine.getChildCount() > 0) && (TaskHelper.isSummaryStructure(localTCComponent)) && (this.state.contains(localTCComponent.getUid())))
            {
              localTreePath = new TreePath(localScheduleTreeLine.getPath());
              ScheduleViewApplicationPanel.this.getTree().expandPath(localTreePath);
            }
          }
          if (localTreePath != null)
          {
            boolean bool = ScheduleViewApplicationPanel.this.getTree().getExpandsSelectedPaths();
            ScheduleViewApplicationPanel.this.getTree().setExpandsSelectedPaths(true);
            ScheduleViewApplicationPanel.this.getTree().setSelectionPath(localTreePath);
            ScheduleViewApplicationPanel.this.getTree().setExpandsSelectedPaths(bool);
          }
          ScheduleViewApplicationPanel.this.treeTable.setSelected(arrayOfAIFTreeLine);
        }
      }
      catch (Exception localException)
      {
        ScheduleViewApplicationPanel.logger.error("Exception while handling memento restoreState", localException);
        localException.printStackTrace();
      }
    }
  }

  private class PostExpandCollapseOperation extends AbstractAIFOperation
  {
    private PostExpandCollapseOperation()
    {
    }

    public void executeOperation()
      throws Exception
    {
      if (ScheduleViewApplicationPanel.this.ganttChart != null)
      {
        ScheduleViewApplicationPanel.this.ganttChart.setAllowLayout(true);
        ScheduleViewApplicationPanel.this.ganttChart.refresh();
      }
    }
  }

  private class RowNumUpdateOperation extends AbstractAIFOperation
  {
    public RowNumUpdateOperation(AbstractAIFSession arg2)
    {
    }

    public void executeOperation()
      throws Exception
    {
      Container localContainer1 = ScheduleViewApplicationPanel.this.treeTable.getParent();
      if ((localContainer1 instanceof JViewport))
      {
        Container localContainer2 = localContainer1.getParent();
        if ((localContainer2 instanceof JScrollPane))
        {
          JScrollPane localObject = (JScrollPane)localContainer2;
          ((JScrollPane)localObject).getRowHeader().invalidate();
          ((JScrollPane)localObject).getRowHeader().validate();
          ((JScrollPane)localObject).getRowHeader().repaint();
        }
        Object localObject = new Rectangle(ScheduleViewApplicationPanel.this.getVisibleRect().width - ScheduleViewApplicationPanel.this.treeTable.getTree().getVisibleRect().height, ScheduleViewApplicationPanel.this.getVisibleRect().width - ScheduleViewApplicationPanel.this.treeTable.getTree().getVisibleRect().width);
        ScheduleViewApplicationPanel.this.repaint((Rectangle)localObject);
        localContainer1.repaint();
      }
    }
  }

  class TreeAdjustmentListener
    implements AdjustmentListener
  {
    TreeAdjustmentListener()
    {
    }

    public void adjustmentValueChanged(AdjustmentEvent paramAdjustmentEvent)
    {
      JScrollBar localJScrollBar = (JScrollBar)paramAdjustmentEvent.getSource();
      int i = localJScrollBar.getValue() * -1;
      if (ScheduleViewApplicationPanel.this.ganttChart != null)
      {
        ScheduleViewApplicationPanel.this.ganttChart.setLocation(ScheduleViewApplicationPanel.this.ganttChart.getLocation().x, i);
        if (ScheduleViewApplicationPanel.this.isProgramView())
          return;
        int j = 0;
        if (paramAdjustmentEvent.getValueIsAdjusting())
        {
          j = paramAdjustmentEvent.getAdjustable().getBlockIncrement();
          ScheduleViewApplicationPanel.scrollBarWasAdjusting = true;
        }
        else if (!ScheduleViewApplicationPanel.scrollBarWasAdjusting)
        {
          j = paramAdjustmentEvent.getAdjustable().getUnitIncrement();
          ScheduleViewApplicationPanel.scrollBarWasAdjusting = false;
        }
        switch (paramAdjustmentEvent.getAdjustmentType())
        {
        case 1:
        case 2:
          j = paramAdjustmentEvent.getAdjustable().getUnitIncrement();
          break;
        case 3:
        case 4:
          j = paramAdjustmentEvent.getAdjustable().getBlockIncrement();
          break;
        case 5:
          ScheduleViewApplicationPanel.ganttRowCounter += j;
          ScheduleViewApplicationPanel.scrollBarWasAdjusting = false;
        }
      }
    }
  }
}