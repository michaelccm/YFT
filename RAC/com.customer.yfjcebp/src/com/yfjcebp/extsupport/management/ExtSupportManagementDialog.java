package com.yfjcebp.extsupport.management;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.commands.open.OpenFormDialog;
import com.teamcenter.rac.common.lov.LOVComboBox;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentFormType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.combobox.iComboBox;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ExtSupportManagementDialog extends AbstractAIFDialog
{
  private String title = "ExtSupport Management";
  private TCSession tcsession;
  private TCComponentFolder homeFolder;
  private iComboBox divisionCombox;
  private iComboBox sectionCombox;
  private LOVComboBox yearCombobox;
  private LOVComboBox monthCombobox;
  private Object[] lovSecValues;
  private String userID;
  private String userName;
  private Vector abandonValue;
  private HashMap<String, String> divisionMap;
  private HashMap<String, String> sectionMap;
  private boolean isControlMonth;

  public ExtSupportManagementDialog(TCSession session, TCComponentFolder homeFolder)
  {
    super(true);
    this.tcsession = session;
    this.homeFolder = homeFolder;
    try {
      this.userID = session.getUser().getUserId();
      this.userName = session.getUserName();
    }
    catch (TCException e) {
      e.printStackTrace();
    }
    init();
  }

  private void init()
  {
    setTitle(this.title);

    Dimension a = new Dimension(420, 240);
    setPreferredSize(a);

    this.abandonValue = getPreference();
    this.isControlMonth = controlMonth();
    getDivisions();
    Set divisionSet = this.divisionMap.keySet();
    Vector divisionVec = new Vector();
    divisionVec.addAll(divisionSet);
    Collections.sort(divisionVec);
    this.sectionCombox = new iComboBox();
    this.sectionCombox.setAutoCompleteSuggestive(true);
    this.sectionCombox.setMaximumRowCount(10);
    this.sectionCombox.setPreferredSize(new Dimension(330, 20));
    this.sectionCombox.setMinimumSize(new Dimension(330, 20));

    this.divisionCombox = new iComboBox(divisionVec);
    this.divisionCombox.setAutoCompleteSuggestive(true);
    this.divisionCombox.setMaximumRowCount(10);
    this.divisionCombox.setPreferredSize(new Dimension(330, 20));
    this.divisionCombox.setMinimumSize(new Dimension(330, 20));
    this.divisionCombox.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        if (ExtSupportManagementDialog.this.divisionCombox.getSelectedIndex() != -1) {
          String divisionName = ExtSupportManagementDialog.this.divisionCombox.getSelectedObject().toString();
          String divisionPUID = (String)ExtSupportManagementDialog.this.divisionMap.get(divisionName);
          Vector sectionVec = ExtSupportManagementDialog.this.getSections(divisionPUID);
          Collections.sort(sectionVec);
          ExtSupportManagementDialog.this.sectionCombox.removeAllItems();
          for (Iterator localIterator = sectionVec.iterator(); localIterator.hasNext(); ) { Object object = localIterator.next();
            ExtSupportManagementDialog.this.sectionCombox.addItem(object);
          }
          ExtSupportManagementDialog.this.sectionCombox.setSelectedIndex(-1);
        }
      }
    });
    this.divisionCombox.setSelectedIndex(-1);

    this.monthCombobox = new LOVComboBox( "JCI6_Month");
    this.monthCombobox.loadSelectionItems();
    if (this.isControlMonth) {
      int currentMonth = Calendar.getInstance().get(2) + 1;
      this.monthCombobox.removeAllItems();
      for (int i = currentMonth; i < 13; i++) {
        this.monthCombobox.addItem(Integer.valueOf(i));
      }
    }
    this.monthCombobox.setEditable(false);
    this.monthCombobox.setSelectedIndex(-1);
    this.monthCombobox.setMaximumRowCount(5);
    this.monthCombobox.setPreferredSize(new Dimension(100, 20));
    this.monthCombobox.setMinimumSize(new Dimension(100, 20));

    int currentYear = Calendar.getInstance().get(1);

    this.yearCombobox = new LOVComboBox( "JCI6_Year");
    this.yearCombobox.loadSelectionItems();
    this.yearCombobox.setEditable(false);
    this.yearCombobox.setMaximumRowCount(5);
    this.yearCombobox.setPreferredSize(new Dimension(100, 20));
    this.yearCombobox.setMinimumSize(new Dimension(100, 20));
    this.yearCombobox.setSelectedString(Integer.toString(currentYear));
    this.yearCombobox.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        String tmpyear = ExtSupportManagementDialog.this.yearCombobox.getSelectedDisplayString().trim();
        if ((!tmpyear.isEmpty()) && (ExtSupportManagementDialog.this.isControlMonth)) {
          int currentYear = Calendar.getInstance().get(1);
          int tmpYear_i = Integer.parseInt(tmpyear);
          if (tmpYear_i == currentYear) {
            ExtSupportManagementDialog.this.monthCombobox.setEnabled(true);
            ExtSupportManagementDialog.this.monthCombobox.setSelectedIndex(-1);
            int currentMonth = Calendar.getInstance().get(2) + 1;
            ExtSupportManagementDialog.this.monthCombobox.removeAllItems();
            for (int i = currentMonth; i < 13; i++)
              ExtSupportManagementDialog.this.monthCombobox.addItem(Integer.valueOf(i));
          }
          else if (tmpYear_i > currentYear) {
            ExtSupportManagementDialog.this.monthCombobox.setSelectedIndex(-1);
            ExtSupportManagementDialog.this.monthCombobox.removeAllItems();
            for (int i = 1; i < 13; i++) {
              ExtSupportManagementDialog.this.monthCombobox.addItem(Integer.valueOf(i));
            }
            ExtSupportManagementDialog.this.monthCombobox.setEnabled(true);
          } else if (tmpYear_i < currentYear) {
            ExtSupportManagementDialog.this.monthCombobox.setSelectedIndex(-1);
            ExtSupportManagementDialog.this.monthCombobox.setEnabled(false);
          }
        }
      }
    });
    JLabel divisionLabel = new JLabel("Division");
    JLabel sectionLabel = new JLabel("Section");
    JLabel yearLabel = new JLabel("Year");
    JLabel monthLabel = new JLabel("Month");

    JPanel parentPanel = new JPanel(new VerticalLayout(5, 2, 2, 2, 2));
    JPanel searchInfoPanel = new JPanel(new PropertyLayout());
    searchInfoPanel.add("1.1.left.center", divisionLabel);
    searchInfoPanel.add("1.2.left.center", this.divisionCombox);
    searchInfoPanel.add("2.1.left.center", sectionLabel);
    searchInfoPanel.add("2.2.left.center", this.sectionCombox);
    searchInfoPanel.add("3.1.left.center", yearLabel);
    searchInfoPanel.add("3.2.left.center", this.yearCombobox);
    searchInfoPanel.add("4.1.left.center", monthLabel);
    searchInfoPanel.add("4.2.left.center", this.monthCombobox);

    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        boolean ifail = ExtSupportManagementDialog.this.doOKButton();
        if (ifail)
          ExtSupportManagementDialog.this.dispose();
      }
    });
    JButton Cancel = new JButton("Cancel");
    Cancel.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        ExtSupportManagementDialog.this.dispose();
      }
    });
    buttonPanel.add(okButton);
    buttonPanel.add(Cancel);

    parentPanel.add("top.bin", new Separator());
    parentPanel.add("top.bind.left.top", searchInfoPanel);
    parentPanel.add("bottom.nobind.center.top", buttonPanel);

    getContentPane().add(parentPanel);
    pack();
    Dimension screen = getToolkit().getScreenSize();
    setLocation((screen.width - getSize().width) / 2, (screen.height - getSize().height) / 2);
    setVisible(true);
  }

  private boolean doOKButton()
  {
	  this.divisionCombox.revalidate();
	    Object divisionObj = this.divisionCombox.getSelectedObject();
	    Object sectionObj = this.sectionCombox.getSelectedObject();
	    String sectionStr = "";
	    String divisionStr = "";
	    if ((sectionObj == null) || (sectionObj.toString().isEmpty())) {
	      if (this.sectionCombox.getItemCount() > 0) {
	        MessageBox.post(this, "Section存在选项，不能为空！", "ERROR", 1);
	        return false;
	      }
	    }
	    else sectionStr = sectionObj.toString();

	    if ((divisionObj == null) || (divisionObj.toString().isEmpty())) {
	      MessageBox.post(this, "Devision不能为空！", "ERROR", 1);
	      return false;
	    }
	    divisionStr = divisionObj.toString();

	    String yearStr = this.yearCombobox.getSelectedDisplayString().trim();
	    String monthStr = this.monthCombobox.getSelectedDisplayString().trim();
	    if (monthStr.isEmpty()) {
	      MessageBox.post(this, "Month不能为空！", "ERROR", 1);
	      return false;
	    }
	    if (monthStr.length() == 1) {
	      monthStr = "0" + monthStr;
	    }

	    String name = "";
	    if (sectionStr.trim().isEmpty()) {
	      name = this.userID + "_" + divisionStr + "_" + yearStr + "." + monthStr;
	      //System.out.println("name:" + name);
	      String divisionPUID = (String)this.divisionMap.get(divisionStr);
	      TCComponent divisionCom = getComFromName(divisionPUID);
	      boolean ifail = hasSameName(name);
	      if (ifail)
	      {
	        MessageBox.post(this, "外服用人计划表单不能重复创建\n" + name + "已存在", "WARNING", 4);
	        return false;
	      }
	      createForm(name, divisionCom, null, Integer.valueOf(Integer.parseInt(yearStr)), Integer.valueOf(Integer.parseInt(monthStr)));
	    }
	    else {
	      name = this.userID + "_" + sectionStr + "_" + divisionStr + "_" + yearStr + "." + monthStr;
	      //System.out.println("name:" + name);
	      String divisionPUID = (String)this.divisionMap.get(divisionStr);
	      TCComponent divisionCom = getComFromName(divisionPUID);
	      String sectionPUID = (String)this.sectionMap.get(sectionStr);
	      TCComponent sectionCom = getComFromName(sectionPUID);
	      boolean ifail = hasSameName(name);
	      if (ifail) {
	        MessageBox.post(this, "外服用人计划表单不能重复创建\n" + name + "已存在", "WARNING", 4);
	        return false;
	      }
	      createForm(name, divisionCom, sectionCom, Integer.valueOf(Integer.parseInt(yearStr)), Integer.valueOf(Integer.parseInt(monthStr)));
	    }

	    return true;
  }

  private void getDivisions()
  {
    this.divisionMap = new HashMap();
    InputStream inStream = getClass().getResourceAsStream("DB.properties");
    Properties prop = new Properties();
    try {
      prop.load(inStream);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    String USERNAMR = prop.getProperty("USERNAMR");
    String PASSWORD = prop.getProperty("PASSWORD");
    String DRVIER = prop.getProperty("DRVIER");
    String URL = prop.getProperty("URL");
    try {
      inStream.close();
    }
    catch (IOException e1) {
      e1.printStackTrace();
    }
    Connection connection = null;
    try {
      Class.forName(DRVIER);
      connection = DriverManager.getConnection(URL, USERNAMR, PASSWORD);
      //System.out.println("成功连接数据库");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("class not find !", e);
    } catch (SQLException e) {
      throw new RuntimeException("get connection error!", e);
    }
    String sql = "select ppom_group.pname,ppom_group.puid from ppom_group where rparentu in (select puid from ppom_group where rparentu=(select puid from ppom_group where pname='Technical Center'))";
    try {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next())
      {
        String divisionName = rs.getString(1);
        String divisionPUID = rs.getString(2);
        if (!this.abandonValue.contains(divisionName)) {
          this.divisionMap.put(divisionName, divisionPUID);
        }
      }
      rs.close();
      stmt.close();
      connection.close();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private Vector getSections(String divisionPUID)
  {
    Vector sectionVec = new Vector();
    this.sectionMap = new HashMap();
    InputStream inStream = getClass().getResourceAsStream("DB.properties");
    Properties prop = new Properties();
    try {
      prop.load(inStream);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    String USERNAMR = prop.getProperty("USERNAMR");
    String PASSWORD = prop.getProperty("PASSWORD");
    String DRVIER = prop.getProperty("DRVIER");
    String URL = prop.getProperty("URL");
    try {
      inStream.close();
    }
    catch (IOException e1) {
      e1.printStackTrace();
    }
    Connection connection = null;
    try {
      Class.forName(DRVIER);
      connection = DriverManager.getConnection(URL, USERNAMR, PASSWORD);
      //System.out.println("成功连接数据库");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("class not find !", e);
    } catch (SQLException e) {
      throw new RuntimeException("get connection error!", e);
    }
    String sql = "select pname,puid from ppom_group where rparentu = '" + divisionPUID + "'";
    try {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next())
      {
        String sectionName = rs.getString(1);
        String sectionPUID = rs.getString(2);
        if (!this.abandonValue.contains(sectionName)) {
          sectionVec.add(sectionName);
          this.sectionMap.put(sectionName, sectionPUID);
        }
      }

      rs.close();
      stmt.close();
      connection.close();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    return sectionVec;
  }

  private TCComponent getComFromName(String groupPUID)
  {
    TCComponent groupCom = null;
    try {
      groupCom = this.tcsession.stringToComponent(groupPUID);
    }
    catch (TCException e)
    {
      e.printStackTrace();
    }

    return groupCom;
  }

  private boolean hasSameName(String currentName) {
    String[] searchAttrs = { "name" };
    String[] searchValues = { currentName };
    TCComponent[] forms = query(this.tcsession, "General...", searchAttrs, searchValues);
    if ((forms == null) || (forms.length == 0)) {
      return false;
    }
    return true;
  }

  private TCComponent[] query(TCSession session, String query_name, String[] arg1, String[] arg2)
  {
    TCComponentContextList imancomponentcontextlist = null;
    TCComponent[] component = (TCComponent[])null;
    try {
      TCComponentQueryType imancomponentquerytype = (TCComponentQueryType)session
        .getTypeComponent("ImanQuery");
      TCComponentQuery imancomponentquery = (TCComponentQuery)imancomponentquerytype
        .find(query_name);
      TCTextService imantextservice = session.getTextService();
      String[] queryAttribute = new String[arg1.length];
      for (int i = 0; i < arg1.length; i++) {
        queryAttribute[i] = imantextservice.getTextValue(arg1[i]);
      }
      String[] queryValues = new String[arg2.length];
      for (int i = 0; i < arg2.length; i++)
        queryValues[i] = arg2[i];
      imancomponentcontextlist = imancomponentquery
        .getExecuteResultsList(queryAttribute, queryValues);
      component = imancomponentcontextlist.toTCComponentArray();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return component;
  }


//  public static void main(String[] args) throws ParseException {
//    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM");
//    Date a1 = s.parse("2015-03");
//    Date a2 = s.parse("2016-011");
//    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
//    String startDateStr = format.format(a1);
//    String endDateStr = format.format(a2);
//    String[] startDateStrs = startDateStr.split("-");
//    String[] endDateStrs = endDateStr.split("-");
//    int sYear = Integer.parseInt(startDateStrs[0]);
//    int sMonth = Integer.parseInt(startDateStrs[1]);
//    int eYear = Integer.parseInt(endDateStrs[0]);
//    int eMonth = Integer.parseInt(endDateStrs[1]);
//    if (sYear == eYear)
//      for (int i = sMonth; i <= eMonth; i++) {
//        String m = "";
//        if (i < 10)
//          m = "0" + i;
//        else {
//          m = i;
//        }
//        String tmp = sYear + "." + m;
//        //System.out.println(tmp);
//      }
//    else if (sYear < eYear)
//      for (int i = sYear; i <= eYear; i++)
//        if (i != eYear)
//          for (int j = sMonth; j <= 12; j++) {
//            String m = "";
//            if (j < 10)
//              m = "0" + j;
//            else {
//              m = j;
//            }
//            String tmp = i + "." + m;
//            //System.out.println(tmp);
//          }
//        else
//          for (int j = 1; j <= eMonth; j++) {
//            String m = "";
//            if (j < 10)
//              m = "0" + j;
//            else {
//              m = j;
//            }
//            String tmp = i + "." + m;
//            //System.out.println(tmp);
//          }
//  }

  private Vector getPreference()
  {
    Vector returnV = new Vector();
    TCPreferenceService prefSvc = this.tcsession.getPreferenceService();
    String[] strs = prefSvc.getStringArray(0, "YFJC_abandoned_division_section");
    for (String string : strs) {
      returnV.add(string);
    }
    return returnV;
  }

  private boolean controlMonth() {
    TCPreferenceService prefSvc = this.tcsession.getPreferenceService();

    String str = prefSvc.getString(0, "YFJC_EXTSupportmanagement_Control_Month");
    if ((str.contains("t")) || (str.contains("T")))
      return true;
    if ((str.contains("f")) || (str.contains("F"))) {
      return false;
    }
    return false;
  }

  private void createForm(String formName, TCComponent divisionCom, TCComponent sectionCom, Integer year, Integer month)
  {
    try
    {
      TCComponentFormType newFormType = (TCComponentFormType)this.tcsession.getTypeComponent("Form");
      TCComponentForm newForm = newFormType.create(formName, "", "JCI6_Ext2Supp");
      newForm.setReferenceProperty("jci6_Division", divisionCom);
      if (sectionCom != null) {
        newForm.setReferenceProperty("jci6_Section", sectionCom);
      }
      newForm.setIntProperty("jci6_Year", year.intValue());
      newForm.setIntProperty("jci6_Month", month.intValue());
      newForm.setStringProperty("jci6_Company", this.userName);

      this.homeFolder.add("contents", newForm);

      OpenFormDialog of = new OpenFormDialog(newForm);
      of.setPreferredSize(new Dimension(1400, 600));
      of.showDialog();
    }
    catch (TCException e) {
      e.printStackTrace();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}