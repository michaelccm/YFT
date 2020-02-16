package com.yfjcebp.extsupport;

import com.teamcenter.rac.kernel.TCAttachmentScope;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentGroupMemberType;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;
import com.teamcenter.services.rac.core._2008_06.DataManagement;
import com.teamcenter.soa.client.model.Property;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class SectionManagerPanel extends JPanel
  implements ActionListener
{
  private static final long serialVersionUID = 1L;
  private JTable table;
  private JButton addEastBtn;
  private JButton removeEastBtn;
  private JButton okBtn;
  protected JButton saveBtn;
  private JButton cancelBtn;
  private TCComponentUser loginUser;
  private String loginName;
  private TCComponentUser own_user;
  private boolean isNew;
  private TCComponentTaskTemplate taskTemplate;
  private TCSession session;
  private TCComponentQueryType queryType;
  private TCTextService textService;
  private TCComponentForm form;
  private Registry reg = Registry.getRegistry(this);
  private Vector<ExtSupportUtil> initExtSupportUtilVec = new Vector();
  private String[] extSupportProNames = { "jci6_Year", 
    "jci6_Month", "jci6_UserName", "jci6_Percent", "jci6_ownProxy", 
    "jci6_Section", "jci6_Division" };

  private StringBuffer sb = new StringBuffer();
  private DefaultTableModel model;
  private SectionManagerDialog dialog;
  private TCProperty tcproperty;
  private Vector<TCComponent> needRemoveExtVec = new Vector();
  private Vector<String> initColumNameVec;
  private String[] queryKeys = { "jci6_UserName", "jci6_Year", 
    "jci6_Month" };

  private String[] queryValues = new String[3];
  private boolean isInit;
  private TCUserService userservice;
  public boolean isRendering;
  public int needSaveSize;
  private Map<Integer, Integer> alsoNeedSaveMap = new HashMap();
  private TCComponent division_tag = null;
  private TCComponent section_tag = null;
  private DataManagementService dmService;
  private Vector<String> extSupportVec;
  private Map<String, TCComponentUser> ext_user_map;
  private String yearMonth;
  private String ext_year;
  private String ext_month;
  private String[] month_pro = { "Jan", "Feb", "Mar", "Apr", 
    "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
  private Vector<TCComponent> initUsagePercentVec;
  private String loginUser_division;
  private String queryTaskName = "YFJC_SearchExtPlanTask";
  private Map<String, String> ext_division;
  private Vector<String> ext_division_vec = new Vector();
  private TCComponentQuery taskQuery;
  private String[] task_attr;
  private String[] task_value;
  private String ext_daibiao;

  public SectionManagerPanel(TCComponentForm form, TCSession session, TCComponentUser own_user)
  {
    this.form = form;
    this.session = session;
    try {
      this.ext_division = new HashMap();
      this.ext_user_map = new HashMap();
      this.extSupportVec = new Vector();
      readyQuery();
      String object_name = form.getProperty("object_name");
      String[] str = object_name.split("_");
      this.yearMonth = str[1];
      this.loginUser = session.getUser();
      this.loginName = this.loginUser.getUserId();
      this.task_value = new String[] { this.loginName };
      this.own_user = own_user;
      if ((this.isNew) || ((this.loginUser != null) && (this.loginUser.equals(own_user)))) {
        TCComponent[] comps = JCI6YFJCUtil.query(this.textService, 
          this.queryType, "Dataset...", new String[] { "Name", 
          "DatasetType" }, new String[] { 
          str[0] + "_resourcepool*", "MSExcel" });
        if (comps != null)
          for (int i = 0; i < comps.length; i++) {
            TCComponentTcFile[] files = ((TCComponentDataset)comps[i])
              .getTcFiles();
            File file = null;
            if ((files != null) && (files.length > 0)) {
              file = files[0].getFmsFile();
            }
            if (file != null)
            {
              readExcel2003(file);
            }
          }
      }
    }
    catch (TCException e)
    {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public SectionManagerPanel(SectionManagerDialog dialog, Vector<String> extSupportVec, Map<String, TCComponentUser> ext_user_map, Map<String, String> ext_division, String yearMonth, String ext_daibiao, TCSession session)
  {
    this.dialog = dialog;
    this.extSupportVec = extSupportVec;
    this.ext_user_map = ext_user_map;
    this.yearMonth = yearMonth;
    this.ext_division = ext_division;
    this.ext_daibiao = ext_daibiao;
    this.session = session;
    try {
      this.loginUser = session.getUser();
      this.loginName = this.loginUser.getUserId();
      this.task_value = new String[] { this.loginName };
    }
    catch (TCException e) {
      e.printStackTrace();
    }
  }

  public void initDialog() throws Exception
  {
    //System.out.println("initDialog");
    if (this.initColumNameVec != null)
      this.initColumNameVec.removeAllElements();
    if (this.initExtSupportUtilVec != null)
      this.initExtSupportUtilVec.removeAllElements();
    if (this.alsoNeedSaveMap != null)
      this.alsoNeedSaveMap.clear();
    if (this.needRemoveExtVec != null)
      this.needRemoveExtVec.removeAllElements();
    if (this.model == null) {
      String[] str = this.yearMonth.split("\\.");
      this.ext_year = str[0];
      this.ext_month = str[1];
      if (this.ext_month.startsWith("0")) {
        this.ext_month = this.ext_month.substring(1, this.ext_month.length());
      }
      getSectionDivision();
      int i = 0; for (int size = this.extSupportVec.size(); i < size; i++) {
        String temp = (String)this.extSupportVec.get(i);
        if ((this.ext_division.containsKey(temp)) && 
          (((String)this.ext_division.get(temp)).equals(this.loginUser_division))) {
          this.ext_division_vec.add(temp);
        }
      }
      readyQuery();
      getQueryTask();
      this.isRendering = false;

      setLayout(new BorderLayout());
      Vector objs = new Vector();
      this.initColumNameVec = getColumnNames();
      this.model = new DefaultTableModel(objs, this.initColumNameVec);
      //System.out.println("table create before");
      this.table = new JTable(this.model)
      {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int i, int j)
        {
          if ((j == 0) || (j == 1)) {
            return false;
          }
          return true;
        }
      };
      getInitTableData(this.initColumNameVec);
      this.model.addTableModelListener(new TableModelListener()
      {
        public void tableChanged(TableModelEvent e) {
          int col = e.getColumn();
          int row = e.getFirstRow();
          if ((col >= 2) && (row >= 0) && (!SectionManagerPanel.this.isInit))
          {
            String value = (String)SectionManagerPanel.this.table.getValueAt(row, col);

            String username = (String)SectionManagerPanel.this.table.getValueAt(row, 1);
            String colName = SectionManagerPanel.this.table.getColumnName(col);
            SectionManagerPanel.this.queryValues[0] = username;
            SectionManagerPanel.this.queryValues[1] = SectionManagerPanel.this.ext_year;
            SectionManagerPanel.this.queryValues[2] = SectionManagerPanel.this.ext_month;
            try {
              double d = SectionManagerPanel.this.getUsagePercent(SectionManagerPanel.this.queryKeys, SectionManagerPanel.this.queryValues);

              if (Double.valueOf(value.substring(0, 
                value.length() - 1)).doubleValue() > 
                d) {
                SectionManagerPanel.this.table.setValueAt(TextFieldEditor.oldValue, row, 
                  col);
                MessageBox.post(username + " has been selected" + 
                  colName + (100.0D - d) + "%", 
                  "INFORMATION", 2);
              }
            }
            catch (TCException e1) {
              e1.printStackTrace();
            }
          }
        }
      });
      //System.out.println("setAutoResizeMode before");
      this.table.setAutoResizeMode(0);
      this.table.setRowHeight(30);
      setFixColumnWidth(this.table);
      JScrollPane scr = new JScrollPane(this.table);
      add(scr, "Center");
      if ((this.isNew) || ((this.loginUser != null) && (this.loginUser.equals(this.own_user)))) {
        JPanel eastPane = new JPanel(new VFlowLayout(1));
        this.addEastBtn = new JButton("+");
        this.addEastBtn.addActionListener(this);
        this.addEastBtn.setActionCommand("addEast");
        this.removeEastBtn = new JButton("-");
        this.removeEastBtn.addActionListener(this);
        this.removeEastBtn.setActionCommand("removeEast");
        eastPane.add(this.addEastBtn);
        eastPane.add(this.removeEastBtn);
        this.okBtn = new JButton("Save draft");
        this.okBtn.addActionListener(this);
        this.okBtn.setActionCommand("ok");
        this.saveBtn = new JButton("Save");
        this.saveBtn.addActionListener(this);
        this.saveBtn.setActionCommand("save");
        this.cancelBtn = new JButton("Cancel");
        this.cancelBtn.addActionListener(this);
        this.cancelBtn.setActionCommand("cancel");
        JPanel southPane = new JPanel();
        southPane.add(this.okBtn);

        southPane.add(this.cancelBtn);
        add(eastPane, "West");
        add(southPane, "South");
      }
      setSize(600, 350);
      DefaultTableCellRenderer render = new DefaultTableCellRenderer();
      render.setHorizontalAlignment(0);
      this.table.setDefaultRenderer(Object.class, render);

      TextFieldEditor editor = new TextFieldEditor();
      this.table.setDefaultEditor(Object.class, editor);

      this.table.addMouseMotionListener(new MouseMotionAdapter()
      {
        public void mouseMoved(MouseEvent mouseevent)
        {
          if ((SectionManagerPanel.this.isNew) || (
            (SectionManagerPanel.this.loginUser != null) && (SectionManagerPanel.this.loginUser.equals(SectionManagerPanel.this.own_user)))) {
            int selectCol = SectionManagerPanel.this.table.getSelectedColumn();
            if ((selectCol == 1) && (SectionManagerPanel.this.table.getSelectedRowCount() > 0))
              SectionManagerPanel.this.removeEastBtn.setEnabled(true);
            else
              SectionManagerPanel.this.removeEastBtn.setEnabled(false);
          }
          else {
            SectionManagerPanel.this.table.setEnabled(false);
          }
        }
      });
      if ((this.isNew) || ((this.loginUser != null) && (this.loginUser.equals(this.own_user))))
        this.table.getColumnModel().addColumnModelListener(
          new TableColumnModelListener()
        {
          public void columnSelectionChanged(ListSelectionEvent listselectionevent)
          {
            int selectCol = SectionManagerPanel.this.table.getSelectedColumn();
            if ((selectCol == 1) && 
              (SectionManagerPanel.this.table.getSelectedRowCount() > 0))
              SectionManagerPanel.this.removeEastBtn.setEnabled(true);
            else
              SectionManagerPanel.this.removeEastBtn.setEnabled(false);
          }

          public void columnRemoved(TableColumnModelEvent tablecolumnmodelevent)
          {
          }

          public void columnMoved(TableColumnModelEvent tablecolumnmodelevent)
          {
          }

          public void columnMarginChanged(ChangeEvent changeevent)
          {
          }

          public void columnAdded(TableColumnModelEvent tablecolumnmodelevent)
          {
          }
        });
    }
    else
    {
      this.model.setRowCount(0);
      this.initColumNameVec = getColumnNames();
      this.model.setColumnCount(this.initColumNameVec.size());
      this.model.setColumnIdentifiers(this.initColumNameVec);
      getInitTableData(this.initColumNameVec);
    }
  }

  public void actionPerformed(ActionEvent actionevent)
  {
    String s = actionevent.getActionCommand();
    if (s.equals("addEast"))
    {
      SelectUserDialog dialog = new SelectUserDialog(this.session, this.table, 
        this.ext_division_vec, this.ext_year, this.ext_month, this.initUsagePercentVec, 
        this.sb);
      dialog.initDialog();
    } else if (s.equals("removeEast")) {
      //System.out.println("---->" + this.table.getSelectedRow());
      int[] rows = this.table.getSelectedRows();
      int colCnt = this.table.getColumnCount();
      String[] year_colNames = null;
      int len = this.initExtSupportUtilVec.size();
      if (colCnt > 2) {
        year_colNames = new String[colCnt - 1];
        for (int j = 2; j < colCnt; j++) {
          year_colNames[(j - 1)] = this.table.getColumnName(j);
        }
      }
      //System.out.println(" rows.length------->" + rows.length);
      for (int i = rows.length - 1; i >= 0; i--) {
        if (year_colNames != null) {
          //System.out.println("year_colNames.length--->" +   year_colNames.length);
          
          String username = (String)this.table.getValueAt(i, 1);
          for (int j = 0; j < year_colNames.length; j++)
          {
            for (int h = 0; h < len; h++) {
              ExtSupportUtil ext = (ExtSupportUtil)this.initExtSupportUtilVec.get(h);
              if ((ext.getUserName().equals(username)) && 
                (ext.getYear().equals(this.ext_year)) && 
                (ext.getMonth().equals(this.ext_month))) {
                this.needRemoveExtVec.add(ext.getExtsupport());
                break;
              }
            }
          }
        }
        //System.out.println("needRemoveExtVec--->" +  this.needRemoveExtVec.size());
         
        this.model.removeRow(rows[i]);
      }

      int i = 0; for (int size = this.table.getRowCount(); i < size; i++)
        this.table.setValueAt(Integer.valueOf(i + 1), i, 0);
    }
    else if (s.equals("cancel")) {
      if (this.isNew) {
        this.dialog.dispose();
      } else {
        this.model.setRowCount(0);
        this.model.setColumnCount(this.initColumNameVec.size());
        this.model.setColumnIdentifiers(this.initColumNameVec);
        getInitTableData(this.initColumNameVec);
        setFixColumnWidth(this.table);
      }
    } else if (s.equals("save")) {
      try {
        save();
        this.form.save();
        //System.out.println("save finish");
        if (!this.isNew) return;
        this.dialog.dispose();
      }
      catch (TCException e)
      {
        e.printStackTrace();
      }
      catch (ServiceException e) {
        e.printStackTrace();
      }
    } else if (s.equals("ok")) {
      try {
        save();
        this.form.save();
        if (this.isNew)
        {
          TCComponent[] comps = this.taskQuery.execute(this.task_attr, 
            this.task_value);
          //System.out.println(this.task_attr[0] + "--->" + this.task_value[0]);
          if ((comps != null) && (comps.length > 0)) {
            //System.out.println("add Attachments");
            TCComponentTask task = (TCComponentTask)comps[0];
            task.addAttachments(TCAttachmentScope.GLOBAL, 
              new TCComponent[] { this.form }, 
              new int[] { 1 });
          } else {
            //System.out.println("create process");

            TCComponentProcess process = 
              JCI6YFJCUtil.createProcess(this.session, this.taskTemplate, 
              "ExtSupport Plan", 
              new TCComponent[] { this.form });

            TCComponent person = this.loginUser
              .getRelatedComponent("person");
            String pa5 = person.getProperty("PA5");
            readyQuery();
            String[] query_keys = { "userName" };
            String[] query_vals = { "*_" + pa5 };
            TCComponent[] results = JCI6YFJCUtil.query(this.textService, 
              this.queryType, "YFJC_SearchUser", query_keys, 
              query_vals);
            TCComponentGroupMember groupMember = null;
            if (results != null) {
              //System.out.println("serachUser cnt --->" + results.length);
              for (int i = 0; i < results.length; i++) {
                if ((results[i] instanceof TCComponentUser)) {
                  TCComponentUser user = (TCComponentUser)results[i];

                  TCComponentGroup defaultGroup = (TCComponentGroup)user.getTCProperty("default_group").getReferenceValue();
                  if (defaultGroup == null) {
                    //System.out.println("user[" + user.getUserId() + "] default_group not exist ");
                  } else {
                    //System.out.println("user[" + user.getUserId() + "] default_group exist ");
                    groupMember = getGroupMember(user, defaultGroup);
                  }

                }

                if (groupMember != null) {
                  break;
                }
              }
            }
            if (groupMember != null) {
              TCComponentTask rootTask = process.getRootTask();
              if (this.userservice == null)
                this.userservice = this.session.getUserService();
              JCI6YFJCUtil.callUserService(this.userservice, 
                "userservice_setSignOff", new Object[] { 
                rootTask, groupMember });
            }
            else {
              MessageBox.post("Cannot find Division Manager or Director with " + 
                this.loginName + 
                ",please go to your list to assign review task approval", 
                "WARNING", 4);
            }
          }
        }
        else {
          String templateName = this.reg.getString("ExtPlanReviewProcess");
          //System.out.println("templateName--->" + templateName);

          this.taskTemplate = JCI6YFJCUtil.findTaskTemplate(this.session, 
            templateName);
          if (this.taskTemplate == null) {
            String message = templateName + 
              " workflow template is not exist ";
            MessageBox.post(message, "INFORMATION", 
              2);
          } else {
            TCComponent[] comps = this.taskQuery.execute(this.task_attr, 
              this.task_value);
            //System.out.println(this.task_attr[0] + "--->" +  this.task_value[0]);
             
            if ((comps != null) && (comps.length > 0)) {
              //System.out.println("add Attachments");
              TCComponentTask task = (TCComponentTask)comps[0];
              TCComponent[] tccs = task.getAttachments(
                TCAttachmentScope.GLOBAL, 
                1);
              boolean isExist = false;
              if ((tccs != null) && (tccs.length > 0)) {
                for (int i = 0; i < tccs.length; i++) {
                  if (tccs[i].equals(this.form)) {
                    isExist = true;
                    break;
                  }
                }
              }
              if (!isExist) {
                task.addAttachments(TCAttachmentScope.GLOBAL, 
                  new TCComponent[] { this.form }, 
                  new int[] { 1 });
                MessageBox.post(
                  "Process Add Targets Successfully !", 
                  "INFORMATION", 2);
              }
              else
              {
                MessageBox.post(
                  "save  successfully !", 
                  "INFORMATION", 2);
              }
            } else {
              //System.out.println("create process");

              TCComponentProcess process = 
                JCI6YFJCUtil.createProcess(this.session, this.taskTemplate, 
                "ExtSupport Plan", 
                new TCComponent[] { this.form });

              TCComponent person = this.loginUser
                .getRelatedComponent("person");
              String pa5 = person.getProperty("PA5");
              readyQuery();
              String[] query_keys = { "userName" };
              String[] query_vals = { "*_" + pa5 };
              TCComponent[] results = JCI6YFJCUtil.query(
                this.textService, this.queryType, "YFJC_SearchUser", 
                query_keys, query_vals);
              TCComponentGroupMember groupMember = null;
              if (results != null) {
                //System.out.println("serachUser cnt --->" + results.length);
                for (int i = 0; i < results.length; i++) {
                  if ((results[i] instanceof TCComponentUser)) {
                    TCComponentUser user = (TCComponentUser)results[i];

                    TCComponentGroup defaultGroup = (TCComponentGroup)user.getTCProperty("default_group").getReferenceValue();
                    if (defaultGroup == null) {
                      //System.out.println("user[" + user.getUserId() + "] default_group not exist ");
                    } else {
                      //System.out.println("user[" + user.getUserId() + "] default_group exist ");
                      groupMember = getGroupMember(user, defaultGroup);
                    }

                  }

                  if (groupMember != null) {
                    break;
                  }
                }
              }
              if (groupMember != null) {
                TCComponentTask rootTask = process
                  .getRootTask();
                if (this.userservice == null)
                  this.userservice = this.session.getUserService();
                JCI6YFJCUtil.callUserService(this.userservice, 
                  "userservice_setSignOff", new Object[] { 
                  rootTask, groupMember });
                MessageBox.post("Create process Successfully !", 
                  "INFORMATION", 2);
              }
              else {
                MessageBox.post("Cannot find Division Manager or Director with " + 
                  this.loginName + 
                  ",please go to your worklist to assign review task approval", 
                  "WARNING", 4);
              }
            }
          }
        }
        if (this.isNew)
          this.dialog.dispose();
      }
      catch (TCException e)
      {
        e.printStackTrace();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected void save() throws TCException, ServiceException {
    createForm();
    if (this.table.isEditing()) {
      this.table.getCellEditor().stopCellEditing();
    }
    int initSize = this.initExtSupportUtilVec.size();
    Vector needSave = new Vector();
    int i = 0; for (int rowcnt = this.table.getRowCount(); i < rowcnt; i++) {
      String username = (String)this.table.getValueAt(i, 1);

      String value = (String)this.table.getValueAt(i, 2);

      String usagePerent = "";
      TCComponent comp = isExtSupportExist(initSize, username, 
        usagePerent);
      if (comp == null) {
        comp = createWSO(username, value);
      } else {
        String newUsagePerent = value.substring(0, value.length() - 1);
        if (!usagePerent.equals(newUsagePerent))
        {
          comp.setDoubleProperty("jci6_Percent", 
            Double.valueOf(newUsagePerent).doubleValue());
        }
      }
      needSave.add(comp);
    }

    this.needSaveSize = needSave.size();
    //System.out.println("needSave--->" + this.needSaveSize);
    if (this.needSaveSize > 0) {
      TCComponent[] comps = new TCComponent[this.needSaveSize];
      for ( i = 0; i < this.needSaveSize; i++) {
        comps[i] = ((TCComponent)needSave.get(i));
      }
      if (this.tcproperty == null) {
        this.tcproperty = this.form.getTCProperty("jci6_ExtSupportArray");
      }
      this.tcproperty.setReferenceValueArrayData(comps);
      this.form.setTCProperty(this.tcproperty);
    }

    if (this.needSaveSize == 0) {
      if (this.tcproperty == null) {
        try {
          this.tcproperty = this.form.getTCProperty("jci6_ExtSupportArray");
        }
        catch (TCException e) {
          e.printStackTrace();
        }
      }
      this.tcproperty.setNullVerdict(true);
      this.form.setTCProperty(this.tcproperty);
    }
    for ( i = 0; i < initSize; i++)
      if (!this.alsoNeedSaveMap.containsKey(Integer.valueOf(i)))
        ((ExtSupportUtil)this.initExtSupportUtilVec.get(i)).getExtsupport().delete();
  }

  private TCComponentForm createForm()
    throws TCException, ServiceException
  {
    if (this.isNew) {
      this.form = JCI6YFJCUtil.createForm(this.session, "JCI6_ExtSupPlan", 
        this.ext_daibiao + "_" + this.yearMonth + "_ExtSupport Plan");
      if (this.section_tag != null) {
        this.form.setReferenceProperty("jci6_Section", this.section_tag);
      }
      if (this.division_tag != null) {
        this.form.setReferenceProperty("jci6_Division", this.division_tag);
      }
      setFormRelate(this.form);
    }

    return this.form;
  }

  private TCComponent isExtSupportExist(int initSize, String username, String usagePerent)
  {
    TCComponent extsup = null;
    for (int j = 0; j < initSize; j++) {
      ExtSupportUtil ext = (ExtSupportUtil)this.initExtSupportUtilVec.get(j);
      //System.out.println(ext.getUserName() + "--->" + ext.getYear() +  "--->" + ext.getMonth());
       
      if ((ext.getUserName().equals(username)) && 
        (ext.getYear().equals(this.ext_year)) && 
        (ext.getMonth().equals(this.ext_month))) {
        //System.out.println("属性上原有此对象");
        extsup = ext.getExtsupport();
        usagePerent = ext.getUsagePercent();
        this.alsoNeedSaveMap.put(Integer.valueOf(j), Integer.valueOf(0));
        break;
      }
    }
    return extsup;
  }

  public int countStr(String sourceStr, String contentStr, int cnt)
  {
    if (sourceStr.indexOf(contentStr) == -1)
      return cnt;
    if (sourceStr.indexOf(contentStr) != -1) {
      cnt++;
      return countStr(
        sourceStr.substring(sourceStr.indexOf(contentStr) + 
        contentStr.length()), contentStr, cnt);
    }
    return cnt;
  }

  private void setFormRelate(TCComponentForm form)
    throws TCException
  {
    this.loginUser.getHomeFolder().add("contents", form);
  }

  private void getSectionDivision()
    throws TCException
  {
    TCComponentGroup currentGroup = this.session.getCurrentGroup();
    String groupFullName = currentGroup.getFullName();
    int cnt = countStr(groupFullName, ".", 0);
    if (cnt == 3) {
      this.section_tag = currentGroup;

      this.division_tag = currentGroup.getRelatedComponent("parent");
      this.loginUser_division = groupFullName.split("\\.")[1];
    } else if (cnt == 2)
    {
      this.division_tag = currentGroup;
      this.loginUser_division = groupFullName.split("\\.")[0];
    } else if (cnt == 1) {
      this.loginUser_division = groupFullName.split("\\.")[0];
    } else {
      this.loginUser_division = groupFullName;
    }
  }

  private TCComponent createWSO(String username, String value)
    throws ServiceException, TCException
  {
    if (this.dmService == null)
      this.dmService = DataManagementService.getService(this.session);
    DataManagement.CreateIn[] createIn = new DataManagement.CreateIn[1];
    createIn[0] = new DataManagement.CreateIn();
    createIn[0].data.boName = "JCI6_ExtSupport";
    this.sb.setLength(0);
    this.sb.append(this.loginName).append("_").append(username).append("_")
      .append(this.yearMonth);
    createIn[0].data.stringProps.put("object_name", this.sb.toString());
    DataManagement.CreateResponse responese = this.dmService.createObjects(createIn);
    TCComponent extSupport = responese.output[0].objects[0];
    if (extSupport != null)
    {
      TCProperty[] properties = extSupport
        .getTCProperties(this.extSupportProNames);
      properties[0].setIntValueData(Property.parseInt(this.ext_year));
      properties[1].setIntValueData(Property.parseInt(this.ext_month));
      properties[2].setStringValueData(username);
      properties[3].setDoubleValueData(Property.parseDouble(value
        .substring(0, value.length() - 1)));

      TCComponentUser user = (TCComponentUser)this.ext_user_map.get(username);

      if (user != null)
        properties[4].setReferenceValueData(user);
      if (this.section_tag != null)
        properties[5].setReferenceValueData(this.section_tag);
      if (this.division_tag != null)
        properties[6].setReferenceValueData(this.division_tag);
      extSupport.setTCProperties(properties);
    }
    return extSupport;
  }

  private double getUsagePercent(String[] queryKeys, String[] queryValues)
    throws TCException
  {
    readyQuery();
    TCComponent[] comps = JCI6YFJCUtil.query(this.textService, this.queryType, 
      "YFJC_SearchExtSupport", queryKeys, queryValues);
    double percent = 100.0D;
    if ((comps != null) && (comps.length > 0)) {
      int size = this.initExtSupportUtilVec.size();
      //System.out.println("getUsagePercent size--->" + size);
      for (int i = 0; i < comps.length; i++)
      {
        boolean isCurrent = false;
        for (int j = 0; j < size; j++)
        {
          if (((ExtSupportUtil)this.initExtSupportUtilVec.get(j)).getExtsupport()
            .equals(comps[i])) {
            isCurrent = true;
            break;
          }
          //System.out.println("不相等");
        }

        if (!isCurrent)
        {
          percent = percent - 
            comps[i].getDoubleProperty("jci6_Percent");
        }
      }
    }
    return percent;
  }

  private void readyQuery() throws TCException {
    if (this.queryType == null)
      this.queryType = 
        ((TCComponentQueryType)this.session
        .getTypeComponent("ImanQuery"));
    if (this.textService == null)
      this.textService = this.session.getTextService();
  }

  private Vector<String> getColumnNames()
    throws TCException
  {
    Vector cols = new Vector();
    cols.add("No.");
    cols.add("User Name");
    String colName = this.month_pro[(Integer.valueOf(this.ext_month).intValue() - 1)] + "-" + 
      this.ext_year;
    cols.add(colName);
    if (this.isNew)
    {
      return cols;
    }

    TCComponent[] comps = this.form.getRelatedComponents(this.tcproperty
      .getPropertyName());
    if ((comps == null) || (comps.length == 0)) {
      return cols;
    }
    this.initUsagePercentVec = new Vector();
    for (int i = 0; i < comps.length; i++) {
      TCComponent comp = comps[i];
      //System.out.println("comp.getType()--->" + comp.getType());
      if (comp.getType().equals("JCI6_ExtSupport")) {
        String[] extSupportProVals = comp
          .getProperties(this.extSupportProNames);
        ExtSupportUtil util = new ExtSupportUtil();
        util.setYear(extSupportProVals[0]);
        util.setMonth(extSupportProVals[1]);
        util.setUsagePercent(extSupportProVals[3]);
        util.setUserName(extSupportProVals[2]);
        util.setExtsupport(comp);
        this.initExtSupportUtilVec.add(util);
        this.initUsagePercentVec.add(comp);
      }
    }
    return cols;
  }

  private void getInitTableData(Vector<String> columNameVec)
  {
    //System.out.println("getInitTableData");
    int len = this.initExtSupportUtilVec.size();
    this.isInit = true;
    if (len > 0) {
      Map rowUserMap = new HashMap();
      int columnNameSize = columNameVec.size();
      int rowAt = 0;
      for (int i = 0; i < len; i++) {
        ExtSupportUtil ext = (ExtSupportUtil)this.initExtSupportUtilVec.get(i);
        String userName = ext.getUserName();
        if (i == 0) {
          rowUserMap.put(userName, Integer.valueOf(rowAt));
          int bb= i + 1;
          this.model.addRow(new String[] { bb + "", userName });
          String year = ext.getYear();
          String month = ext.getMonth();
          this.sb.setLength(0);
          this.sb.append(this.month_pro[(Integer.valueOf(month).intValue() - 1)])
            .append("-").append(year);
          for (int j = 2; j < columnNameSize; j++) {
            if (this.sb.toString().equals(columNameVec.get(j))) {
              //System.out.println("ext.getUsagePercent()--->" + ext.getUsagePercent()); 
               
              this.table.setValueAt(ext.getUsagePercent() + "%", 
                rowAt, j);
              break;
            }
          }
          rowAt++;
        }
        else if (rowUserMap.containsKey(userName)) {
          int row = ((Integer)rowUserMap.get(userName)).intValue();
          String year = ext.getYear();
          String month = ext.getMonth();
          this.sb.setLength(0);
          this.sb.append(this.month_pro[(Integer.valueOf(month).intValue() - 1)])
            .append("-").append(year);
          for (int j = 2; j < columnNameSize; j++)
            if (this.sb.toString().equals(columNameVec.get(j))) {
              //System.out.println("ext.getUsagePercent()--->" +  ext.getUsagePercent());
               
              this.table.setValueAt(ext.getUsagePercent() + "%", 
                row, j);
              break;
            }
        }
        else {
          rowUserMap.put(userName, Integer.valueOf(rowAt));
          int bb=i + 1;
          this.model.addRow(new String[] { bb + "", userName });
          String year = ext.getYear();
          String month = ext.getMonth();
          this.sb.setLength(0);
          this.sb.append(this.month_pro[(Integer.valueOf(month).intValue() - 1)])
            .append("-").append(year);
          for (int j = 2; j < columnNameSize; j++) {
            if (this.sb.toString().equals(columNameVec.get(j))) {
              this.table.setValueAt(ext.getUsagePercent() + "%", 
                rowAt, j);
              break;
            }
          }
          rowAt++;
        }
      }
    }

    this.isInit = false;
  }

  public boolean isInRemoveVec(TCComponent comp, Vector<TCComponent> needMoveVec)
  {
    int size = needMoveVec.size();
    for (int i = 0; i < size; i++) {
      if (((TCComponent)needMoveVec.get(i)).equals(comp)) {
        return true;
      }
    }
    return false;
  }

  public void readExcel2003(File file) throws IOException, TCException
  {
    FileInputStream is = new FileInputStream(file.getAbsolutePath());
    HSSFWorkbook wb = new HSSFWorkbook(is);
    HSSFFormulaEvaluator formula = new HSSFFormulaEvaluator(wb);
    HSSFSheet sheet = wb.getSheetAt(0);
    String[] key = { "userID" };
    String[] value = new String[1];
    TCComponentUser user = null;
    for (int i = 1; i < sheet.getLastRowNum(); i++) {
      HSSFRow row = sheet.getRow(i);
      String user_id = JCI6YFJCUtil.getStringCellValue2003(formula, 
        row.getCell(0)).trim();
      if ((!user_id.equals("")) && 
        (user_id.contains("("))) {
        String[] s = user_id.split("\\(");
        user_id = s[1].substring(0, s[1].length() - 1);
        value[0] = user_id;

        TCComponent[] comps = JCI6YFJCUtil.query(this.textService, 
          this.queryType, "YFJC_SearchUser", key, value);
        if ((comps != null) && (comps.length != 0)) {
          user = (TCComponentUser)comps[0];
          HSSFCell cell = row.getCell(3);
          String division_name = 
            JCI6YFJCUtil.getStringCellValue2003(formula, row.getCell(1))
            .trim();
          String extusername = 
            JCI6YFJCUtil.getStringCellValue2003(formula, cell).trim();
          this.extSupportVec.add(extusername);
          this.ext_user_map.put(extusername, user);
          this.ext_division.put(extusername, division_name);
        }
      }
    }

    is.close();
  }

  private void getQueryTask()
    throws TCException
  {
    if (this.taskQuery == null) {
      this.taskQuery = ((TCComponentQuery)this.queryType.find(this.queryTaskName));
      if (this.taskQuery != null) {
        String str = this.textService.getTextValue("user_id");
        if ((str == null) || (str.trim().equals("")))
          this.task_attr = new String[] { "user_id" };
        else
          this.task_attr = new String[] { str };
      }
    }
  }

  public void setFixColumnWidth(JTable table)
  {
    TableColumnModel tcm = table.getTableHeader().getColumnModel();
    for (int i = 0; i < tcm.getColumnCount(); i++) {
      TableColumn tc = tcm.getColumn(i);
      if (i == 0) {
        tc.setMaxWidth(60);
        tc.setMinWidth(60);
        tc.setPreferredWidth(60);
      } else if (i == 1) {
        tc.setMaxWidth(260);
        tc.setMinWidth(260);
        tc.setPreferredWidth(260);
      } else if (i == 2) {
        tc.setMaxWidth(130);
        tc.setMinWidth(130);
        tc.setPreferredWidth(130);
      }
    }
  }

  public void fitTableColumns(JTable myTable)
  {
    JTableHeader header = myTable.getTableHeader();
    int rowCount = myTable.getRowCount();
    Enumeration columns = myTable.getColumnModel()
      .getColumns();
    while (columns.hasMoreElements()) {
      TableColumn column = (TableColumn)columns.nextElement();
      int col = header.getColumnModel().getColumnIndex(
        column.getIdentifier());
      int width = 
        (int)header
        .getDefaultRenderer()
        .getTableCellRendererComponent(myTable, 
        column.getIdentifier(), false, false, -1, col)
        .getPreferredSize().getWidth();
      for (int row = 0; row < rowCount; row++) {
        int preferedWidth = 
          (int)myTable
          .getCellRenderer(row, col)
          .getTableCellRendererComponent(myTable, 
          myTable.getValueAt(row, col), false, false, 
          row, col).getPreferredSize().getWidth();
        width = Math.max(width, preferedWidth);
      }
      header.setResizingColumn(column);
      column.setWidth(width + myTable.getIntercellSpacing().width);
    }
  }

  public TCComponentGroupMember getGroupMember(TCComponentUser user, TCComponentGroup group)
    throws TCException
  {
    TCComponentGroupMemberType tccomponentgroupmembertype = (TCComponentGroupMemberType)this.session
      .getTypeComponent("GroupMember");
    TCComponentGroupMember[] atccomponentgroupmember = tccomponentgroupmembertype.find(user, group);
    TCComponentGroupMember member = null;
    //System.out.println("find groupMember cnt is " + atccomponentgroupmember.length);
    for (int m = 0; m < atccomponentgroupmember.length; m++) {
      member = atccomponentgroupmember[0];
    }
    return member;
  }

  public void setForm(TCComponentForm form) {
    this.form = form;
  }

  public void setNew(boolean isNew)
  {
    this.isNew = isNew;
  }

  public void setTaskTemplate(TCComponentTaskTemplate taskTemplate) {
    this.taskTemplate = taskTemplate;
  }

  public void setTcproperty(TCProperty tcproperty)
  {
    this.tcproperty = tcproperty;
  }

  public JTable getTable() {
    return this.table;
  }

  public Vector<ExtSupportUtil> getInitExtSupportUtilVec() {
    return this.initExtSupportUtilVec;
  }

  public Vector<TCComponent> getNeedRemoveExtVec() {
    return this.needRemoveExtVec;
  }

  public String[] getMonth_pro() {
    return this.month_pro;
  }
}