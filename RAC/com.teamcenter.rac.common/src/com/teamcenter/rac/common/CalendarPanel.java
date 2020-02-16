package com.teamcenter.rac.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.ToolTipManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicToggleButtonUI;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.PreferenceObject;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentTCCalendar;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.schedule.calendar.TCCalendar;
import com.teamcenter.rac.schedule.calendar.TCCalendarEvent;
import com.teamcenter.rac.schedule.calendar.TCDate;
import com.teamcenter.rac.schedule.componentutils.CalendarHelper;
import com.teamcenter.rac.util.AdapterUtil;
import com.teamcenter.rac.util.FilterDocument;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.InterfaceSignalOnClose;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.date.DateTextFieldViewer;
import com.teamcenter.rac.util.date.IDateProvider;

public class CalendarPanel extends JPanel
  implements PropertyChangeListener, InterfaceSignalOnClose
{
 
  private List<Calendar> selecteds;
  private GregorianCalendar calendar;
  private GregorianCalendar selected;
  public DateButton[] dates;
  private JComboLabel month;
  private iTextField year;
  private iTextField hour;
  private iTextField minute;
  private iTextField second;
  private JButton next;
  private JButton prev;
  private JToggleButton clearSelection;
  private JPanel datesPanel;
  private JPanel monthYearPanel = null;
  private JPanel timePanel = null;
  private SimpleDateFormat userFormat = null;
  private TextFieldUpdater tfu;
  private Font smallFont;
  private DailyCalendarHoursPanel dailyCalendarHoursPanel = null;
  private WorkCalendarPanel workCalendarPanel = null;
  private JLabel[] display = new JLabel[7];
  private String[] strWeekDays = new String[7];
  private int[][] monthDates = new int[6][7];
  private int indexWeekDay = 0;
  public static int setWeekDay = 0;
  private boolean oneOrZeroInFirstRowFirstCol = true;
  public static boolean prefValueSet = false;
  private Registry r = null;
  private List<TCCalendarEvent> calEvents = null;
  private List<TCCalendarEvent> baseCalEvents = null;
  private TCCalendar tcCalendar = null;
  private Icon lineIcon = null;
  private Map<String, TCCalendarEvent> buttonEvents = null;
  private int startOfWeek = 1;
  private boolean isCtrlDown = false;
  private DateTextFieldViewer m_DateControl;
  private JLabel m_errorMessageLabel;
  private IDateProvider m_dateProvider;
  //added by YFJC
  private boolean checkRole;
  private JPanel colorRemarkPane;
  private JLabel colorRemarklbl;
  private String DATECOLOROPTION = "YFJC_Calendar_DateColor";
  private String HOLIDAYOPTION = "YFJC_Holiday_Also_Weekend";
  
  private boolean isStandar = false;
  private Vector colorLabel;
  private Vector colorVec;
  private String[] holiday;
  private String opDesc;
  private Color mySelectColor;
  //end by YFJC
  public CalendarPanel()
  {
	System.out.println("11");
	TCSession mysession = (TCSession)AIFUtility.getCurrentApplication().getSession();
    try {
      if (mysession.getRole().getProperty("role_name").equals("ExtSupporter"))
        this.checkRole = true;
      else {
        this.checkRole = false;
      }

      this.mySelectColor = new Color(Integer.decode("#99CCFF").intValue());
    } catch (TCException e) {
      e.printStackTrace();
    }
    this.monthYearPanel = null;
    this.timePanel = null;
    this.userFormat = null;
    this.dailyCalendarHoursPanel = null;
    this.workCalendarPanel = null;
    this.display = new JLabel[7];
    this.strWeekDays = new String[7];
    this.monthDates = new int[6][7];
    this.indexWeekDay = 0;
    this.oneOrZeroInFirstRowFirstCol = true;
    this.r = null;
    this.calEvents = null;
    this.baseCalEvents = null;
    this.tcCalendar = null;
    this.lineIcon = null;
    this.buttonEvents = null;
    this.startOfWeek = 1;
    this.isCtrlDown = false;
    Date localDate = new Date();
    constructPanel(localDate);
    setHourMinSec(0, 0, 0);
    setTime(localDate);
  }

  public CalendarPanel(Date paramDate)
  {
	//System.out.println("CalendarPanel  paramDate");
	TCSession mysession = (TCSession)AIFUtility.getCurrentApplication().getSession();
    try {
      if (mysession.getRole().getProperty("role_name").equals("ExtSupporter"))
        this.checkRole = true;
      else {
        this.checkRole = false;
      }
      this.mySelectColor = new Color(Integer.decode("#99CCFF").intValue());
    } catch (TCException e) {
      e.printStackTrace();
    }
    this.monthYearPanel = null;
    this.timePanel = null;
    this.userFormat = null;
    this.dailyCalendarHoursPanel = null;
    this.workCalendarPanel = null;
    this.display = new JLabel[7];
    this.strWeekDays = new String[7];
    this.monthDates = new int[6][7];
    this.indexWeekDay = 0;
    this.oneOrZeroInFirstRowFirstCol = true;
    this.r = null;
    this.calEvents = null;
    this.baseCalEvents = null;
    this.tcCalendar = null;
    this.lineIcon = null;
    this.buttonEvents = null;
    this.startOfWeek = 1;
    this.isCtrlDown = false;
    if (paramDate == null)
      paramDate = new Date();
    constructPanel(paramDate);
  }

  public CalendarPanel(Date paramDate, SimpleDateFormat paramSimpleDateFormat)
  {
	//System.out.println("CalendarPanel  paramDate,paramSimpleDateFormat");
	TCSession mysession = (TCSession)AIFUtility.getCurrentApplication().getSession();
    try {
      if (mysession.getRole().getProperty("role_name").equals("ExtSupporter"))
        this.checkRole = true;
      else {
        this.checkRole = false;
      }
      this.mySelectColor = new Color(Integer.decode("#99CCFF").intValue());
    } catch (TCException e) {
      e.printStackTrace();
    }
    this.monthYearPanel = null;
    this.timePanel = null;
    this.userFormat = null;
    this.dailyCalendarHoursPanel = null;
    this.workCalendarPanel = null;
    this.display = new JLabel[7];
    this.strWeekDays = new String[7];
    this.monthDates = new int[6][7];
    this.indexWeekDay = 0;
    this.oneOrZeroInFirstRowFirstCol = true;
    this.r = null;
    this.calEvents = null;
    this.baseCalEvents = null;
    this.tcCalendar = null;
    this.lineIcon = null;
    this.buttonEvents = null;
    this.startOfWeek = 1;
    this.isCtrlDown = false;
    if (paramDate == null)
      paramDate = new Date();
    this.userFormat = paramSimpleDateFormat;
    constructPanel(paramDate);
  }

  public void setTCCalendar(TCCalendar paramTCCalendar)
  {
	//System.out.println("setTCCalendar  paramTCCalendar");
    this.tcCalendar = paramTCCalendar;
    this.baseCalEvents = null;
    if (this.tcCalendar != null)
    {
      this.calEvents = this.tcCalendar.getEvents();
      if (paramTCCalendar.getCalendarType() != 1)
        this.baseCalEvents = this.tcCalendar.getBaseEvents();
    }
    else
    {
      this.calEvents = null;
      this.baseCalEvents = null;
    }
    clear();
    updateDatePanel();
    if (!this.checkRole){
    	try {
            if (paramTCCalendar != null)
            {
              if ((paramTCCalendar.getCalendarComponent().getProperty("tccal_name").equals("标准")) || (paramTCCalendar.getCalendarComponent().getProperty("tccal_name").equals("Standard"))) {
                if (this.colorRemarkPane == null) {
                  this.colorRemarkPane = new JPanel();
                  if ((this.colorLabel != null) && (this.colorLabel.size() > 0)) {
                    this.colorRemarkPane.setBorder(new TitledBorder(null, "颜色说明", 0,  0, null, Color.BLUE));
                    String lblstr = "";
                    if ((this.opDesc == null) || (this.opDesc.equals("")))
                      lblstr = "正常工时=0:" + this.colorLabel.elementAt(0).toString() + ";<0正常工时<8:" + 
                        this.colorLabel.elementAt(1).toString() + ";正常工时=8:" + this.colorLabel.elementAt(2).toString();
                    else {
                      lblstr = this.opDesc;
                    }
                    this.colorRemarklbl = new JLabel(lblstr);
                    this.colorRemarkPane.add(this.colorRemarklbl);
                  }

                }
                this.monthYearPanel.add("left", this.colorRemarkPane);
              }
              else if ((!paramTCCalendar.getCalendarComponent().getProperty("tccal_name").equals("标准")) && (!paramTCCalendar.getCalendarComponent().getProperty("tccal_name").equals("Standard")) && (this.colorRemarkPane != null)) {
                this.monthYearPanel.remove(this.colorRemarkPane);
              }
            }
            else if (this.colorRemarkPane != null) {
              this.monthYearPanel.remove(this.colorRemarkPane);
            }
       }
      catch (TCException e)
      {
        e.printStackTrace();
      }
    }
  }

  public void setDailyCalendarHoursPanel(DailyCalendarHoursPanel paramDailyCalendarHoursPanel)
  {
	//System.out.println("setDailyCalendarHoursPanel  paramDailyCalendarHoursPanel");
    this.dailyCalendarHoursPanel = paramDailyCalendarHoursPanel;
  }

  public void setWorkCalendarPanel(WorkCalendarPanel paramWorkCalendarPanel)
  {
	//System.out.println("setWorkCalendarPanel  paramWorkCalendarPanel");
    this.workCalendarPanel = paramWorkCalendarPanel;
  }

  public void setTime(Date paramDate)
  {
	//.out.println("setTime  paramDate");
    if (paramDate == null)
      paramDate = new Date();
    this.calendar.setTime(paramDate);
    this.selected.setTime(paramDate);
    updateText();
    updateDateProvider();
    updateDatePanel();
  }

  public Date getTime()
  {
	//System.out.println("getTime");
    return this.calendar.getTime();
  }

  public TCDate getSelectedDate()
  {
	//System.out.println("getSelectedDate");
    return new TCDate(this.selected);
  }

  public List<Calendar> getSelectedDates()
  {
	//System.out.println("getSelectedDates");
    return this.selecteds;
  }

  public void setEnabled(boolean paramBoolean)
  {
	//System.out.println("setEnabled");
    super.setEnabled(paramBoolean);
    for (DateButton localDateButton : this.dates)
      localDateButton.setEnabled(paramBoolean);
    this.year.setEnabled(paramBoolean);
    this.minute.setEnabled(paramBoolean);
    this.hour.setEnabled(paramBoolean);
    this.second.setEnabled(paramBoolean);
    this.prev.setEnabled(paramBoolean);
    this.next.setEnabled(paramBoolean);
    this.clearSelection.setEnabled(paramBoolean);
  }

  private void constructPanel(Date paramDate)
  {
	//System.out.println("constructPanel paramDate");
	//YCJC ADD
	getAllCusColor();
    getCusHoliday();
  //YCJC End
    this.r = Registry.getRegistry(this);
    DateFormatSymbols localDateFormatSymbols;
    if (this.userFormat != null)
      localDateFormatSymbols = this.userFormat.getDateFormatSymbols();
    else
      localDateFormatSymbols = new DateFormatSymbols();
    this.strWeekDays[0] = localDateFormatSymbols.getShortWeekdays()[1];
    this.strWeekDays[1] = localDateFormatSymbols.getShortWeekdays()[2];
    this.strWeekDays[2] = localDateFormatSymbols.getShortWeekdays()[3];
    this.strWeekDays[3] = localDateFormatSymbols.getShortWeekdays()[4];
    this.strWeekDays[4] = localDateFormatSymbols.getShortWeekdays()[5];
    this.strWeekDays[5] = localDateFormatSymbols.getShortWeekdays()[6];
    this.strWeekDays[6] = localDateFormatSymbols.getShortWeekdays()[7];
    this.lineIcon = this.r.getImageIcon("line.ICON");
    this.buttonEvents = new HashMap();
    this.calendar = new GregorianCalendar();
    this.selected = new GregorianCalendar();
    this.selecteds = new ArrayList();
    setLayout(new VerticalLayout(2, 2, 2, 2, 2));
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    localGregorianCalendar.setTime(paramDate);
    this.m_dateProvider = ((IDateProvider)AdapterUtil.getAdapter(localGregorianCalendar, IDateProvider.class));
    this.m_DateControl = new DateTextFieldViewer(1);
    this.m_DateControl.setDateProvider(this.m_dateProvider);
    add("top", this.m_DateControl);
    addPropertyChangeListener(this.m_dateProvider);
    this.m_errorMessageLabel = new JLabel(" ");
    this.m_errorMessageLabel.setForeground(Color.RED);
    add("top", this.m_errorMessageLabel);
    this.m_DateControl.getTextControl().addFocusListener(new FocusAdapter()
    {
      public void focusLost(FocusEvent paramAnonymousFocusEvent)
      {
        CalendarPanel.this.handleDateControlEvent();
      }
    });
    this.m_DateControl.getTextControl().addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        CalendarPanel.this.handleDateControlEvent();
      }
    });
    Font localFont = new JLabel("foo").getFont();
    this.smallFont = new Font(localFont.getName(), 0, localFont.getSize());
    this.monthYearPanel = new JPanel(new HorizontalLayout(15, 0, 0, 0, 0));
    this.prev = new JButton(this.r.getImageIcon("left.ICON"))
    {
      public Dimension getPreferredSize()
      {
        return new Dimension(16, 16);
      }
    };
    this.prev.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        CalendarPanel.this.decMonth();
        CalendarPanel.this.updateText();
        CalendarPanel.this.workCalendarPanel.fillEventsToUpdate();
        if (CalendarPanel.this.tcCalendar != null)
          CalendarPanel.this.tcCalendar.setDailyDefaultTimeRanges(CalendarPanel.this.dailyCalendarHoursPanel.getDailyDefaultTimeRanges());
        CalendarPanel.this.workCalendarPanel.clearSelection();
        CalendarPanel.this.selecteds.clear();
        CalendarPanel.this.clear();
        CalendarPanel.this.workCalendarPanel.enablePrefValueSettings();
        CalendarPanel.this.updateDatePanel();
        if (CalendarPanel.this.month.isEnabled())
          CalendarPanel.this.dailyCalendarHoursPanel.setEnabled(true);
        else
          CalendarPanel.this.dailyCalendarHoursPanel.setEnabled(false);
      }
    });
    FilterDocument localFilterDocument = new FilterDocument("0123456789", 4);
    localFilterDocument.setNegativeAccepted(false);
    this.month = new JComboLabel(localDateFormatSymbols.getMonths());
    this.month.setPreferredSize(new Dimension(55, 20));
    this.year = new iTextField(localFilterDocument, "", 4, 4, true, null);
    this.tfu = new TextFieldUpdater();
    this.year.addActionListener(this.tfu);
    this.year.addFocusListener(this.tfu);
    this.next = new JButton(this.r.getImageIcon("right.ICON"))
    {
      public Dimension getPreferredSize()
      {
        return new Dimension(16, 16);
      }
    };
    this.next.setMargin(new Insets(0, 0, 0, 0));
    this.next.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        CalendarPanel.this.incMonth();
        CalendarPanel.this.updateText();
        CalendarPanel.this.workCalendarPanel.fillEventsToUpdate();
        if (CalendarPanel.this.tcCalendar != null)
          CalendarPanel.this.tcCalendar.setDailyDefaultTimeRanges(CalendarPanel.this.dailyCalendarHoursPanel.getDailyDefaultTimeRanges());
        CalendarPanel.this.workCalendarPanel.clearSelection();
        CalendarPanel.this.selecteds.clear();
        CalendarPanel.this.clear();
        CalendarPanel.this.updateDatePanel();
        CalendarPanel.this.workCalendarPanel.enablePrefValueSettings();
        if (CalendarPanel.this.month.isEnabled())
          CalendarPanel.this.dailyCalendarHoursPanel.setEnabled(true);
        else
          CalendarPanel.this.dailyCalendarHoursPanel.setEnabled(false);
      }
    });
    this.monthYearPanel.add("left", this.prev);
    this.monthYearPanel.add("left", this.month);
    this.monthYearPanel.add("left", this.next);
    this.monthYearPanel.add("right", this.year);
    add("top", this.monthYearPanel);
    this.datesPanel = new JPanel(new GridLayout(7, 7));
    String[] arrayOfString = localDateFormatSymbols.getShortWeekdays();
    for (int i = 1; i <= 7; i++)
    {
      JLabel localJLabel = new JLabel(arrayOfString[i]);
      localJLabel.setHorizontalAlignment(0);
      localJLabel.setFont(this.smallFont);
      this.datesPanel.add(localJLabel);
      this.display[(i - 1)] = localJLabel;
    }
    this.dates = new DateButton[42];
    ButtonGroup localButtonGroup = new ButtonGroup();
    for (int j = 0; j < 42; j++)
    {
      DateButton localDateButton = new DateButton();
      localDateButton.setNumber(j);
      localDateButton.setBorderPainted(true);
      localDateButton.setMargin(new Insets(1, 1, 1, 1));
      localDateButton.setFont(this.smallFont);
      localDateButton.setOpaque(true);
      localDateButton.setFocusPainted(false);
      localDateButton.setContentAreaFilled(true);
      ToolTipManager.sharedInstance().registerComponent(localDateButton);
      //YCJC ADDED
      ToolTipManager.sharedInstance().setDismissDelay(600000);
      //YCJC END
      this.dates[j] = localDateButton;
      this.datesPanel.add(localDateButton);
      localButtonGroup.add(localDateButton);
      localDateButton.addKeyListener(new MyKeyAdapter());
      addChangeListener(localDateButton);
    }
    this.clearSelection = new JToggleButton("clear");
    localButtonGroup.add(this.clearSelection);
    this.datesPanel.setBorder(BorderFactory.createEtchedBorder());
  //YCJC ADDED
    this.monthYearPanel.add("right", this.year);
    add("top", this.monthYearPanel);
    //YCJC END
    add("top", this.datesPanel);
    addTimePanel(paramDate, true);
  }

  public void addTimePanel(Date paramDate, boolean paramBoolean)
  {
	//System.out.println("addTimePanel paramDate paramBoolean");
    if (paramBoolean)
    {
      this.timePanel = new JPanel(new GridLayout(1, 6));
      FilterDocument localFilterDocument = new FilterDocument("0123456789", 2);
      localFilterDocument.setNumberLimits(0.0D, 23.0D);
      this.hour = new iTextField(localFilterDocument, "", 2);
      localFilterDocument = new FilterDocument("0123456789", 2);
      localFilterDocument.setNumberLimits(0.0D, 59.0D);
      this.minute = new iTextField(localFilterDocument, "", 2);
      localFilterDocument = new FilterDocument("0123456789", 2);
      localFilterDocument.setNumberLimits(0.0D, 59.0D);
      this.second = new iTextField(localFilterDocument, "", 2);
      this.hour.setFont(this.smallFont);
      this.minute.setFont(this.smallFont);
      this.second.setFont(this.smallFont);
      this.hour.addActionListener(this.tfu);
      this.hour.addFocusListener(this.tfu);
      this.minute.addActionListener(this.tfu);
      this.minute.addFocusListener(this.tfu);
      this.second.addActionListener(this.tfu);
      this.second.addFocusListener(this.tfu);
      this.timePanel.add(this.hour);
      this.timePanel.add(new JLabel("h :", 0));
      this.timePanel.add(this.minute);
      this.timePanel.add(new JLabel("m :", 0));
      this.timePanel.add(this.second);
      this.timePanel.add(new JLabel("s  ", 0));
      this.timePanel.setBorder(BorderFactory.createEtchedBorder());
      add("top.unbound.center.center", this.timePanel);
      setTime(paramDate);
      updateText();
      updateDatePanel();
    }
    else if (this.timePanel != null)
    {
      this.timePanel.setVisible(false);
      updateDatePanel();
    }
  }

  private void decMonth()
  {
	//System.out.println("decMonth");
    if ((this.calendar.get(1) == 1) && (this.calendar.get(2) == 0))
      return;
    this.calendar.add(2, -1);
    this.selected.add(2, -1);
    updateDateProvider();
    updateInvalidDateErrorMessage(" ");
  }

  private void incMonth()
  {
	//System.out.println("incMonth");
    if ((this.calendar.get(1) == 9999) && (this.calendar.get(2) == 11))
      return;
    this.calendar.add(2, 1);
    this.selected.add(2, 1);
    updateDateProvider();
    updateInvalidDateErrorMessage(" ");
  }

  private void getAllText()
  {
	//System.out.println("getAllText");
    try
    {
      int i = Integer.parseInt(this.year.getText());
      if (i == 0)
        i = 1;
      this.calendar.set(1, i);
      convertTo4DigitYear(this.calendar);
      String str = this.hour.getText();
      i = Integer.parseInt(str);
      if ((str.charAt(0) == '0') && (str.length() != 1))
        i = Integer.parseInt(str.substring(1, 2));
      this.calendar.set(11, i);
      str = this.minute.getText();
      i = Integer.parseInt(str);
      if ((str.charAt(0) == '0') && (str.length() != 1))
        i = Integer.parseInt(str.substring(1, 2));
      this.calendar.set(12, i);
      str = this.second.getText();
      i = Integer.parseInt(str);
      if ((str.charAt(0) == '0') && (str.length() != 1))
        i = Integer.parseInt(str.substring(1, 2));
      this.calendar.set(13, i);
      updateDateProvider();
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    updateText();
  }

  private void convertTo4DigitYear(GregorianCalendar paramGregorianCalendar)
  {
	//System.out.println("convertTo4DigitYear");
    int i = paramGregorianCalendar.get(1);
    if (i <= 99)
    {
      DateFormat localDateFormat = DateFormat.getDateInstance(3, Locale.US);
      try
      {
        Date localDate = localDateFormat.parse(localDateFormat.format(paramGregorianCalendar.getTime()));
        paramGregorianCalendar.setTime(localDate);
      }
      catch (ParseException localParseException)
      {
      }
    }
  }

  private void updateText()
  {
	//System.out.println("updateText");
    this.month.setSelectedIndex(this.calendar.get(2));
    this.year.setText(String.valueOf(this.calendar.get(1)));
    int i = this.calendar.get(11);
    String str1 = String.valueOf(i);
    if (i < 10)
      str1 = "0" + str1;
    int j = this.calendar.get(12);
    String str2 = String.valueOf(j);
    if (j < 10)
      str2 = "0" + str2;
    int k = this.calendar.get(13);
    String str3 = String.valueOf(k);
    if (k < 10)
      str3 = "0" + str3;
    this.hour.setText(str1);
    this.minute.setText(str2);
    this.second.setText(str3);
  }

  private JToggleButton updateDatePanel()
  {
	//System.out.println("updateDatePanel");
    int i = this.calendar.get(5);
    this.calendar.set(5, 1);
    int j = this.calendar.get(7);
    int k = (j - this.startOfWeek) % 7;
    if (k < 0)
      k += 7;
    this.calendar.set(5, i);
    for (int m = 0; m < k; m++)
      this.dates[m].setNumber(0);
    HashMap localHashMap = new HashMap(7);
    int n = this.calendar.getActualMaximum(5);
    int i1 = 0;
    int i3;
    for (int i2 = k; i1 < n; i2++)
    {
      i3 = i1 + 1;
      this.dates[i2].setNumber(i3);
      if (this.tcCalendar != null)
      {
        this.calendar.set(5, i3);
        int i4 = this.calendar.get(7);
        boolean bool = this.tcCalendar.isWorkingDay(i4);
        if (!bool)
          this.dates[i2].setNonWorking(true);
        else
          this.dates[i2].setNonWorking(false);
        if (!localHashMap.containsKey(Integer.valueOf(i4)))
          localHashMap.put(Integer.valueOf(i4), Boolean.valueOf(bool));
        this.calendar.set(5, i);
      }
      i1++;
    }
    for (i1 = k + n; i1 < 42; i1++)
      this.dates[i1].setNumber(0);
    setColorForNonDates(localHashMap);
    JToggleButton localObject;
    if (this.calendar.get(2) == this.selected.get(2))
    {
      int i2 = k + this.selected.get(5) - 1;
      this.dates[i2].setSelected(true);
      localObject = this.dates[i2];
    }
    else
    {
      this.clearSelection.setSelected(true);
      localObject = this.clearSelection;
    }
    if (this.isCtrlDown)
      for (int i2 = 0; i2 < this.selecteds.size(); i2++)
      {
        i3 = k + ((Calendar)this.selecteds.get(i2)).get(5) - 1;
        this.dates[i3].setSelected(true);
        localObject = this.dates[i3];
      }
    processCalendarEvents();
    return localObject;
  }

  private void updateInvalidDateErrorMessage(String paramString)
  {
	//System.out.println("updateInvalidDateErrorMessage paramString");
    this.m_errorMessageLabel.setText(paramString);
  }

  private void updateDateProvider()
  {
	//System.out.println("updateDateProvide");
    this.m_dateProvider.setDateField(this.calendar.get(1), this.calendar.get(2), this.calendar.get(5));
  }

  public void processCalendarEvents()
  {
	//System.out.println("processCalendarEvents");
    if ((this.baseCalEvents != null) && (this.baseCalEvents.size() > 0))
    {
     Iterator localObject2 = this.baseCalEvents.iterator();
      while (localObject2.hasNext())
      {
    	TCCalendarEvent localObject1 = (TCCalendarEvent) localObject2.next();
        processCalendarEvent((TCCalendarEvent)localObject1);
      }
    }
    if ((this.calEvents != null) && (this.calEvents.size() > 0))
    {
      Iterator localObject2 = this.calEvents.iterator();
      while (localObject2.hasNext())
      {
    	TCCalendarEvent localObject1 = (TCCalendarEvent)localObject2.next();
        processCalendarEvent((TCCalendarEvent)localObject1);
      }
    }
    Object localObject1 = null;
    Object localObject2 = null;
    List localList = null;
    if (this.tcCalendar != null)
    {
      localObject1 = this.tcCalendar.getNewEventsVector();
      localObject2 = this.tcCalendar.getUpdatedEventsVector();
      localList = this.tcCalendar.getDeletedEventsVector();
    }
    Iterator localIterator;
    TCCalendarEvent localTCCalendarEvent1;
    if ((localObject1 != null) && (((List)localObject1).size() > 0))
    {
      localIterator = ((List)localObject1).iterator();
      while (localIterator.hasNext())
      {
        localTCCalendarEvent1 = (TCCalendarEvent)localIterator.next();
        processCalendarEvent(localTCCalendarEvent1);
      }
    }
    if ((localObject2 != null) && (((List)localObject2).size() > 0))
    {
      localIterator = ((List)localObject2).iterator();
      while (localIterator.hasNext())
      {
        localTCCalendarEvent1 = (TCCalendarEvent)localIterator.next();
        processCalendarEvent(localTCCalendarEvent1);
      }
    }
    if ((localList != null) && (localList.size() > 0))
    {
      localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        localTCCalendarEvent1 = (TCCalendarEvent)localIterator.next();
        TCCalendarEvent localTCCalendarEvent2 = new TCCalendarEvent();
        localTCCalendarEvent2.setEventDate(localTCCalendarEvent1.getDay(), localTCCalendarEvent1.getMonth(), localTCCalendarEvent1.getYear());
        localTCCalendarEvent2.setEventType(3);
        processCalendarEvent(localTCCalendarEvent2);
      }
    }
  }

  private void clear()
  {
	//System.out.println("clear");
    for (int i = 0; i < 42; i++)
      this.dates[i].unset();
    this.buttonEvents.clear();
    this.dailyCalendarHoursPanel.setDefaults();
    this.calendar.set(5, 1);
  }

  public void addChangeListener(ChangeListener paramChangeListener)
  {
	//System.out.println("addChangeListener paramChangeListener");
    this.listenerList.add(ChangeListener.class, paramChangeListener);
  }

  public void removeChangeListener(ChangeListener paramChangeListener)
  {
	//System.out.println("removeChangeListener paramChangeListener");
    this.listenerList.remove(ChangeListener.class, paramChangeListener);
  }

  public void setHourMinSec(int paramInt1, int paramInt2, int paramInt3)
  {
	//System.out.println("setHourMinSec paramInt1 paramInt2 paramInt3");
    this.calendar.set(11, paramInt1);
    this.calendar.set(12, paramInt2);
    this.calendar.set(13, paramInt3);
    this.selected.set(11, paramInt1);
    this.selected.set(12, paramInt2);
    this.selected.set(13, paramInt3);
    updateText();
  }

  public void fireButtonSelected(Object paramObject)
  {
	//System.out.println("fireButtonSelected paramObject");
    getAllText();
    ChangeEvent localChangeEvent = new ChangeEvent(paramObject);
    Object[] arrayOfObject1 = this.listenerList.getListenerList();
    for (Object localObject : arrayOfObject1)
      if ((localObject instanceof DateButton))
        ((ChangeListener)localObject).stateChanged(localChangeEvent);
  }

  public iTextField getYearTextField()
  {
	//System.out.println("getYearTextField");
    return this.year;
  }

  public DateTextFieldViewer getDateControl()
  {
	//System.out.println("getDateControl");
    return this.m_DateControl;
  }

  private TCCalendarEvent getMatchingEvent(List<TCCalendarEvent> paramList, int paramInt1, int paramInt2, int paramInt3)
  {
	//System.out.println("getMatchingEvent paramList paramInt1");
	TCCalendarEvent localObject = null;
    if ((paramList != null) && (paramList.size() > 0))
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        TCCalendarEvent localTCCalendarEvent = (TCCalendarEvent)localIterator.next();
        int i = localTCCalendarEvent.getDay();
        int j = localTCCalendarEvent.getMonth();
        int k = localTCCalendarEvent.getYear();
        if ((k == paramInt1) && (j == paramInt2) && (i == paramInt3))
        {
          localObject = localTCCalendarEvent;
          //YFCJ ADDED
          //break;
        //YFCJ END
        }
      }
    }
    return localObject;
  }

  public void setDayOfWeekDetails(int paramInt, boolean paramBoolean)
  {
	//System.out.println("setDayOfWeekDetails paramInt paramBoolean");
    int i = (paramInt - this.startOfWeek) % 7;
    if (i < 0)
      i += 7;
    for (int j = 0; j < 6; j++)
    {
      int k = i + j * 7;
      if ((!this.dates[k].isException()) && (!this.dates[k].isInheritedException()))
        this.dates[k].setNonWorking(paramBoolean);
    }
  }

  public void setStartOfWeek(int paramInt, int[] paramArrayOfInt)
  {
	//System.out.println("setStartOfWeek paramInt paramArrayOfInt");
    this.startOfWeek = paramInt;
    int i = paramInt - 1;
    this.dailyCalendarHoursPanel.refreshOrderedDays(this.startOfWeek);
    if (this.startOfWeek == 1)
    {
      for (int j = 0; j < 7; j++)
        this.display[j].setText(this.strWeekDays[j]);
    }
    else
    {
      int  j = i;
      for (int k = 0; j < 7; k++)
      {
        this.display[k].setText(this.strWeekDays[j]);
        j++;
      }
      j = 7 - i;
      for (int k = 0; j < 7; k++)
      {
        this.display[j].setText(this.strWeekDays[k]);
        j++;
      }
    }
    int j = 0;
    for (int k = 0; k < 6; k++)
      for (int m = 0; m < 7; m++)
      {
        this.monthDates[k][m] = this.dates[j].getNumber();
        j++;
      }
    if (paramArrayOfInt[0] == 0)
    {
      this.indexWeekDay = i;
    }
    else
    {
      this.indexWeekDay = (paramArrayOfInt[1] - paramArrayOfInt[0]);
      if (this.indexWeekDay < 0)
        this.indexWeekDay += 7;
    }
    int k = 0;
    for (int m = 0; m < 7; m++)
    {
      for (int n = 0; n < 6; n++)
        if (this.indexWeekDay < 7)
          this.dates[(m + n * 7)].setNumber(this.monthDates[n][this.indexWeekDay]);
      this.indexWeekDay += 1;
      if (this.indexWeekDay == 7)
      {
        this.indexWeekDay = 0;
        k = m;
        break;
      }
    }
    for (int m = k; m < 7; m++)
    {
      for (int n = 0; n < 6; n++)
        if (m + 1 != 7)
          this.dates[(m + 1 + n * 7)].setNumber(this.monthDates[n][this.indexWeekDay]);
      this.indexWeekDay += 1;
    }
    adjustingDates(paramArrayOfInt);
    int m = this.calendar.get(5);
    for (int n = 0; n < 42; n++)
    {
      String str = String.valueOf(this.dates[n].getNumber());
      if (((this.dates[n].getNumber() == 0) && (this.dates[n].getEvent() != null)) || ((this.dates[n].getEvent() != null) && (!this.buttonEvents.containsKey(str))))
        this.dates[n].setEvent(null);
      else if (this.buttonEvents.containsKey(str))
        this.dates[n].setEvent((TCCalendarEvent)this.buttonEvents.get(str));
      if (this.dates[n].getNumber() == m)
        this.dates[n].setSelected(true);
    }
    updateDatePanel();
  }

  private void setColorForNonDates(Map<Integer, Boolean> paramMap)
  {
	//System.out.println("setColorForNonDates paramMap");
    if ((paramMap == null) || (paramMap.size() <= 0))
      return;
    int i = 0;
    int j = 42;
    for (int k = 0; k < 42; k++)
      if (this.dates[k].getNumber() != 0)
      {
        i = k;
        break;
      }
    for (int k = i + 1; k < 42; k++)
      if ((this.dates[k].getNumber() != 0) && (k + 1 < 42) && (this.dates[(k + 1)].getNumber() == 0))
      {
        j = k + 1;
        break;
      }
    int m;
    boolean bool;
    for (int k = 0; k < i; k++)
    {
      m = this.startOfWeek + k - 1;
      m %= 7;
      m++;
      bool = ((Boolean)paramMap.get(Integer.valueOf(m))).booleanValue();
      if (!bool)
        this.dates[k].setNonWorking(true);
    }
    for (int k = j; k < 42; k++)
    {
      m = this.startOfWeek + k % 7 - 1;
      m %= 7;
      m++;
      bool = ((Boolean)paramMap.get(Integer.valueOf(m))).booleanValue();
      if (!bool)
        this.dates[k].setNonWorking(true);
    }
  }

  public void processCalendarEvent(TCCalendarEvent paramTCCalendarEvent)
  {
	//System.out.println("processCalendarEvent paramTCCalendarEvent");
    try
    {
      int i = paramTCCalendarEvent.getYear();
      int j = paramTCCalendarEvent.getMonth();
      if ((i != this.calendar.get(1)) || (j != this.calendar.get(2)))
        return;
      int k = paramTCCalendarEvent.getDay();
      DateButton localDateButton = null;
      for (int m = 0; m < 42; m++)
        if (this.dates[m].getNumber() == k)
        {
          localDateButton = this.dates[m];
          break;
        }
      if (localDateButton != null)
      {
        localDateButton.setEvent(paramTCCalendarEvent);
        this.buttonEvents.put(String.valueOf(localDateButton.getNumber()), paramTCCalendarEvent);
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  private void adjustingDates(int[] paramArrayOfInt)
  {
	//System.out.println("adjustingDates paramArrayOfInt");
    int[] arrayOfInt = new int[7];
    for (int i = 0; i < 7; i++)
      arrayOfInt[i] = this.dates[i].getNumber();
    if (this.dates[0].getNumber() > 1)
      this.oneOrZeroInFirstRowFirstCol = false;
    int i = 0;
    int j = 0;
    i = arrayOfInt[j];
    for (int k = 0; k < 7; k++)
      if (arrayOfInt[k] > i)
        i = arrayOfInt[k];
    int k = 0;
    for (int m = 0; m < 7; m++)
      if (arrayOfInt[m] == i)
      {
        k = m;
        break;
      }
    int n;
    for (int m = k + 1; m < 7; m++)
      for (n = 0; n < 6; n++)
        if (n + 1 != 6)
        {
          int i1 = this.dates[(m + (n + 1) * 7)].getNumber();
          if (i1 > 0)
            this.dates[(m + n * 7)].setNumber(i1);
        }
    for (int m = k + 1; m < 7; m++)
      for (n = 0; n < 6; n++)
        if ((n + 1 != 6) && (this.dates[(m + (n + 1) * 7)].getNumber() > 0) && (this.dates[(m + n * 7)].getNumber() == this.dates[(m + (n + 1) * 7)].getNumber()))
          this.dates[(m + (n + 1) * 7)].setNumber(0);
    if (!this.oneOrZeroInFirstRowFirstCol)
      adjustingDatesDown();
  }

  public void adjustingDatesDown()
  {
	//System.out.println("adjustingDatesDown");
    for (int i = 0; i < 7; i++)
      for (int j = 5; j > 0; j--)
        if ((j - 1 != -1) && (this.dates[(i + (j - 1) * 7)].getNumber() > 0))
          this.dates[(i + j * 7)].setNumber(this.dates[(i + (j - 1) * 7)].getNumber());
    this.oneOrZeroInFirstRowFirstCol = true;
    int i = 0;
    if (this.dates[0].getNumber() > 0)
      i = this.dates[0].getNumber();
    int j = 0;
    for (int k = 6; k >= 0; k--)
    {
      i--;
      int m = i;
      if (m != 0)
      {
        this.dates[k].setNumber(m);
      }
      else
      {
        j = k;
        break;
      }
    }
    for (int k = j; k >= 0; k--)
      this.dates[k].setNumber(0);
  }

  private void handleDateControlEvent()
  {
	//System.out.println("handleDateControlEvent");
    Date localDate1 = this.m_DateControl.parseDateString();
    if (localDate1 == null)
    {
      updateInvalidDateErrorMessage(Registry.getRegistry(this).getString("dateInvalid.TITLE"));
    }
    else
    {
      Date localDate2 = (Date)AdapterUtil.getAdapter(this.m_dateProvider, Date.class);
      this.calendar.setTime(localDate2);
      this.selected.setTime(localDate2);
      updateInvalidDateErrorMessage(" ");
      updateText();
      updateDatePanel();
    }
  }

  public IDateProvider getDateProvider()
  {
	//System.out.println("getDateProvider");
    return this.m_dateProvider;
  }

  private boolean isCalendarSelected(Calendar paramCalendar)
  {
	//System.out.println("isCalendarSelected paramCalendar");
    boolean bool = false;
    int i = this.selecteds.size();
    if (i >= 1)
    {
      int j = paramCalendar.get(1);
      int k = paramCalendar.get(2);
      int m = paramCalendar.get(5);
      for (int n = 0; n < i; n++)
      {
        Calendar localCalendar = (Calendar)this.selecteds.get(n);
        if ((localCalendar.get(1) == j) && (localCalendar.get(2) == k) && (localCalendar.get(5) == m))
        {
          bool = true;
          break;
        }
      }
    }
    return bool;
  }

  private void removeSelectedCal(Calendar paramCalendar)
  {
	//System.out.println("removeSelectedCal paramCalendar");
    int i = this.selecteds.size();
    if (i >= 1)
    {
      int j = paramCalendar.get(1);
      int k = paramCalendar.get(2);
      int m = paramCalendar.get(5);
      for (int n = 0; n < i; n++)
      {
        Calendar localCalendar = (Calendar)this.selecteds.get(n);
        if ((localCalendar.get(1) == j) && (localCalendar.get(2) == k) && (localCalendar.get(5) == m))
        {
          this.selecteds.remove(n);
          break;
        }
      }
    }
  }

  //YFJC_ADDED
  private Vector calHours(boolean isWorkingDay, int year, int month, int day)
  {
    Vector v = new Vector();
    TCSession session = (TCSession)AIFUtility.getCurrentApplication().getSession();
    try
    {
      String userID = session.getUser().getUserId();
      String datestr = String.valueOf(day) + "-" + transfer(month) + "-" + String.valueOf(year);
      if (Locale.getDefault().toString().equals("zh_CN"))
      {
        datestr = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
      }
      //System.out.println(" in calHours datestr " + datestr);
      String[] s1 = { "BillType", "Owning User", "CreateAfter", "CreateBefore"};
      String[] s2 = { "*", userID, datestr + " 0:00", datestr + " 23:59" };
      TCComponent[] comps = qry_search("SearchTimeSheetEntry", s1, s2);
      
      int normalh = 0;
      int unnormalh = 0;
      int hours = 0;
      StringBuffer sbf = new StringBuffer();
      if ((comps != null) && (comps.length > 0))
      {
    	//System.out.println("search result is "+comps.length);
        sbf.append("<html>");
        for (int i = 0; i < comps.length; i++) {
          if (i == 0) {
            sbf.append("汇总自：");
          }
          if (isWorkingDay) {
            //System.out.println("  in calHour the minutes === " + comps[i].getProperty("minutes"));
            if (comps[i].getProperty("bill_type").equals("Normal Hours"))
              normalh += Integer.parseInt(comps[i].getProperty("minutes"));
            else
              unnormalh += Integer.parseInt(comps[i].getProperty("minutes"));
          }
          else {
            hours += Integer.parseInt(comps[i].getProperty("minutes"));
          }
          String str2 = comps[i].getProperty("object_name").split(":TSE")[0];
          String str1 = comps[i].getProperty("jci6_Schedule").split(":TSE")[0];
          String str3 = String.valueOf(Integer.parseInt(comps[i].getProperty("minutes").toString()) / 60);
          sbf.append("<p>").append(str1).append(":").append(str2).append(":").append(str3).append("</p>");
        }
      }
      else{
    	  //System.out.println("search no value");
      }
      if (!sbf.toString().equals("")) {
        sbf.append("</html>");
      }
      if (isWorkingDay) {
        //System.out.println(" in  calHour the normalh ==" + normalh);
        //System.out.println(" in  calHour the unnormalh ==" + unnormalh);
        normalh /= 60;
        unnormalh /= 60;
        v.addElement(String.valueOf(normalh));
        v.addElement(String.valueOf(unnormalh));
        v.addElement(String.valueOf(sbf.toString()));
      } else {
        hours /= 60;
        v.addElement(String.valueOf(hours));
        v.addElement(String.valueOf(sbf.toString()));
      }
    }
    catch (TCException e) {
      e.printStackTrace();
    }
    return v;
  }
  public String transfer(int month) {
	    String s = "";
	    switch (month) { case 1:
	      s = "Jan"; break;
	    case 2:
	      s = "Feb"; break;
	    case 3:
	      s = "Mar"; break;
	    case 4:
	      s = "Apr"; break;
	    case 5:
	      s = "May"; break;
	    case 6:
	      s = "Jun"; break;
	    case 7:
	      s = "Jul"; break;
	    case 8:
	      s = "Aug"; break;
	    case 9:
	      s = "Sep"; break;
	    case 10:
	      s = "Oct"; break;
	    case 11:
	      s = "Nov"; break;
	    case 12:
	      s = "Dec";
	    }
	    return s;
  }
  public boolean isToday(int y, int m, int d)
  {
    boolean flag = false;
    Calendar cal = Calendar.getInstance();
    int y1 = cal.get(1);
    int m1 = cal.get(2) + 1;
    int d1 = cal.get(5);
    if ((y == y1) && (m == m1) && (d == d1)) {
      flag = true;
    }
    return flag;
  }
  public void getAllCusColor() {
    TCSession session = (TCSession)AIFUtility.getCurrentApplication().getSession();
    String[] option = session.getPreferenceService().getStringArray(4, this.DATECOLOROPTION);
    this.opDesc = session.getPreferenceService().getPreferenceDescription(this.DATECOLOROPTION);
    //System.out.println("opDesc is "+this.opDesc);
    /* TCPreferenceService tcpreservice = session.getPreferenceService();
    try {
      PreferenceObject[] obs = tcpreservice.askAllPreferences();
      for (int i = 0; i < obs.length; i++)
        if (obs[i].getPreferenceName().equals(this.DATECOLOROPTION))
          this.opDesc = obs[i].getPreferenceDescription();
    }
    catch (TCException e)
    {
      e.printStackTrace();
    }*/
    this.colorLabel = new Vector();
    this.colorVec = new Vector();
    if ((option != null) && (option.length > 0)){
    	for (int i = 0; i < option.length; i++) {
	        String[] temp = option[i].split("=");
	        this.colorVec.addElement(temp[1]);
	        this.colorLabel.addElement(temp[0]);
	    }
    }
  }
  public Color getSomeColor(int h)
  {
    Color c = null;
    if (this.colorVec.size() > 0) {
      if (h == 0)
      {
        c = new Color(Integer.decode(this.colorVec.elementAt(0).toString()).intValue());
      } else if ((h > 0) && (h < 8))
        c = new Color(Integer.decode(this.colorVec.elementAt(1).toString()).intValue());
      else if (h == 8) {
        c = new Color(Integer.decode(this.colorVec.elementAt(2).toString()).intValue());
      }
    }
    return c;
  }

  public void getCusHoliday() {
    TCSession session = (TCSession)AIFUtility.getCurrentApplication().getSession();
    this.holiday = session.getPreferenceService().getStringArray(4, this.HOLIDAYOPTION);
  }

  public boolean isDayBold(int year, int month, int day) {
    boolean flag = false;
    if (this.holiday != null) {
      for (int i = 0; i < this.holiday.length; i++) {
        String[] str = this.holiday[i].split("-");
        if ((year == Integer.parseInt(str[0])) && (month == Integer.parseInt(str[1])) && (day == Integer.parseInt(str[2]))) {
          flag = true;
        }
      }
    }
    return flag;
  }
  /**
	 * @brief get query object
	 * @param queryName
	 * @return
	 */
	public TCComponentQuery qry_get_query(String queryName) {
		TCSession session = (TCSession)AIFUtility.getCurrentApplication().getSession();
		TCComponentQuery query = null;
		try {
			TCComponentQueryType queryType = ((TCComponentQueryType) session.getTypeComponent("ImanQuery"));
			query = ((TCComponentQuery) queryType.find(queryName));
			if ((query == null) || (!query.isValid())) {
				MessageBox.post("Option failed,not find Query object", "Message", MessageBox.INFORMATION);
			}
		} catch (TCException imane) {
			imane.printStackTrace();
			imane.dump();
		}
		return query;
	}
  /**
	 * 
	 * @brief search
	 * @param qryName
	 * @param entryAttributes
	 * @param queryCriteriaValue
	 * @return
	 */
	public TCComponent[] qry_search(String qryName,String[] entryNameDisplay,String[] queryCriteriaValue){
		TCComponent[] results = null;
		if(qryName.isEmpty() || (entryNameDisplay == null) || (queryCriteriaValue == null) || (queryCriteriaValue.length == 0) || (entryNameDisplay.length == 0)){
			return null;
		}
		TCComponentQuery query = qry_get_query(qryName);
		if(query != null){
			try {
				TCQueryClause[] clause = query.describe();
				if( (clause != null) && (clause.length > 0) ){
					List<String> entry_names = new ArrayList<String>();
					
					for (int i = 0; i < entryNameDisplay.length; i++) {
						String oneEntryNameDisplay = entryNameDisplay[i];
						boolean existAttName = false;
						System.out.println("start find"+oneEntryNameDisplay);
						for(int j = 0; j < clause.length; ++j){
							String clauseAttName = clause[j].getAttributeName();
							String userEntryNameDisplay = clause[j].getUserEntryNameDisplay();
							//System.out.println("attName:"+clauseAttName+",entyNameDisplay:"+userEntryNameDisplay);
							if(userEntryNameDisplay.equals(oneEntryNameDisplay)){
								entry_names.add(userEntryNameDisplay);
								existAttName = true;
								break;
							}
						}
						if(!existAttName){
							//System.out.println("not find AttributeName:"+oneEntryNameDisplay);
							return null;
						}
					}
					String[] queryCriteriaName = new String[entry_names.size()];
					queryCriteriaName = entry_names.toArray(queryCriteriaName);
					results = query.execute(queryCriteriaName, queryCriteriaValue);
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
  public TCComponent[] query(String query_name, String[] arg1, String[] arg2)
  {
    TCComponentContextList imancomponentcontextlist = null;
    TCComponent[] component = (TCComponent[])null;
    try
    {
      TCSession session = (TCSession)AIFUtility.getCurrentApplication().getSession();
      TCComponentQueryType imancomponentquerytype = (TCComponentQueryType)session.getTypeComponent("ImanQuery");
      TCComponentQuery imancomponentquery = (TCComponentQuery)imancomponentquerytype.find(query_name);
      TCTextService imantextservice = session.getTextService();
      String[] queryAttribute = new String[arg1.length];
      for (int i = 0; i < arg1.length; i++) {
        queryAttribute[i] = imantextservice.getTextValue(arg1[i]);
      }
      String[] queryValues = new String[arg2.length];
      for (int i = 0; i < arg2.length; i++) {
        queryValues[i] = arg2[i];
      }
      imancomponentcontextlist = imancomponentquery.getExecuteResultsList(queryAttribute, queryValues);
      component = imancomponentcontextlist.toTCComponentArray();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return component;
  }
  //YFJC_ADDED END
  private void addPropertyChangeListener(IDateProvider paramIDateProvider)
  {
	//System.out.println("addPropertyChangeListener paramIDateProvider");
    PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AdapterUtil.getAdapter(paramIDateProvider, PropertyChangeSupport.class);
    if (localPropertyChangeSupport != null)
      localPropertyChangeSupport.addPropertyChangeListener(this);
  }

  private void removePropertyChangeListener(IDateProvider paramIDateProvider)
  {
	//System.out.println("removePropertyChangeListener paramIDateProvider");
    PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AdapterUtil.getAdapter(paramIDateProvider, PropertyChangeSupport.class);
    if (localPropertyChangeSupport != null)
      localPropertyChangeSupport.removePropertyChangeListener(this);
  }

  private void removeListener()
  {
	//System.out.println("removeListener");
    removePropertyChangeListener(this.m_dateProvider);
  }

  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
	//System.out.println("propertyChange paramPropertyChangeEvent");
    updateInvalidDateErrorMessage(" ");
  }

  public void closeSignaled()
  {
	//System.out.println("closeSignaled");
    removeListener();
  }

  public class DateButton extends JToggleButton
    implements ChangeListener
  {
    int number;
    int eventType = 3;
    boolean isSelected = false;
    boolean daySelected = false;
    private Color currentColor = Color.white;
    private TCCalendarEvent evt = null;
    private boolean nonWorkingDay = false;
    //YFJC_ADDED
    private String curToolTipMsg;
    //YFJC_END

    public DateButton()
    {
      System.out.println("DateButton");
      //YFJC_ADDED
      this.eventType = 3;
      this.isSelected = false;
      this.daySelected = false;
      this.currentColor = Color.white;
      this.evt = null;
      this.nonWorkingDay = false;
      //YFJC_END
      setNumber(0);
      //setPreferredSize(new Dimension(30, 30));
      setPreferredSize(new Dimension(30, 40));
      setFocusPainted(false);
      setContentAreaFilled(false);
      setBorder(BorderFactory.createLineBorder(Color.black, 1));
      addMouseListener(new MouseAdapter()
      {
        public void mouseClicked(MouseEvent paramAnonymousMouseEvent)
        {
          CalendarPanel.this.isCtrlDown = false;
          if (paramAnonymousMouseEvent.isControlDown())
            CalendarPanel.this.isCtrlDown = true;
          if ((CalendarPanel.DateButton.this.isSelected) && (CalendarPanel.this.isCtrlDown))
            CalendarPanel.DateButton.this.clearSelection();
          else if (CalendarPanel.DateButton.this.getNumber() > 0)
            CalendarPanel.this.fireButtonSelected(CalendarPanel.DateButton.this);
        }
      });
    }

    public void updateUI()
    {
      //System.out.println("updateUI");
      BasicToggleButtonUI local2 = new BasicToggleButtonUI()
      {
        protected void paintButtonPressed(Graphics paramAnonymousGraphics, AbstractButton paramAnonymousAbstractButton)
        {
          if (paramAnonymousAbstractButton.isContentAreaFilled())
          {
            Dimension localDimension = paramAnonymousAbstractButton.getSize();
            paramAnonymousGraphics.setColor(CalendarPanel.DateButton.this.currentColor);
            paramAnonymousGraphics.fillRect(0, 0, localDimension.width, localDimension.height);
          }
        }
      };
      setUI(local2);
    }

    public void setNumber(int paramInt)
    {
      //System.out.println("setNumber paramInt");
      this.number = paramInt;
      this.isSelected = isSelected();
      setVerticalAlignment(1);
      setHorizontalAlignment(0);
      setVerticalTextPosition(1);
      setHorizontalTextPosition(0);
      if (paramInt == 0)
      {
        setText("");
        //YFJC_ADDED
        if (isSelected()) {
            setSelected(false);
        }
        this.currentColor = getButtonColor(getEventType());
      //YFJC_END
        setEnabled(false);
      }
      else
      {
    	  String text = "";
          if (CalendarPanel.this.checkRole) {
            text = Integer.toString(this.number);
          }
          else if (CalendarPanel.this.tcCalendar != null)
            try
            {
              if ((CalendarPanel.this.tcCalendar.getCalendarComponent().getProperty(
                "tccal_name").equals("标准")) || 
                (CalendarPanel.this.tcCalendar.getCalendarComponent()
                .getProperty("tccal_name").equals(
                "Standard"))) {
                CalendarPanel.this.isStandar = true;
                StringBuffer sbf = new StringBuffer();

                if (isSelected()) {
                  setSelected(false);
                  this.currentColor = getButtonColor(getEventType());
                }

                if (!isNonWorking())
                {
                  //System.out.println("YFJC is workday");
                  String s = "";
                  String msg = "";

                  if (isException()) {
                	//System.out.println("YFJC is isException");
                    Vector v = CalendarPanel.this.calHours(false, CalendarPanel.this.calendar
                      .get(1), CalendarPanel.this.calendar
                      .get(2) + 1, this.number);
                    if ((v != null) && (v.size() > 0)) {
                      s = v.elementAt(0).toString();
                      //System.out.println(" in 313 s======= " + s);
                      int h = Integer.parseInt(v.elementAt(0).toString());
                      msg = v.elementAt(1).toString();
                      this.currentColor = CalendarHelper.NON_WORKING_COLOR;
                    }
                    if (CalendarPanel.this.isDayBold(CalendarPanel.this.calendar
                      .get(1), CalendarPanel.this.calendar
                      .get(2) + 1, this.number)) {
                      setFont(new Font(getFont()
                        .getFontName(), 1, 
                        getFont().getSize()));
                      this.currentColor = CalendarHelper.EXCEPTION_COLOR;
                    } else {
                      setFont(CalendarPanel.this.smallFont);
                    }
                  }
                  else
                  {
                	//System.out.println("YFJC is notException");
                    Vector v = CalendarPanel.this.calHours(true, CalendarPanel.this.calendar.get(1), CalendarPanel.this.calendar.get(2) + 1, this.number);
                    if ((v != null) && (v.size() > 0)) {
                      s = v.elementAt(0) + ";" + v.elementAt(1);
                      int h = Integer.parseInt(v.elementAt(0).toString());
                      this.currentColor = CalendarPanel.this.getSomeColor(h);
                      msg = v.elementAt(2).toString();
                      if (CalendarPanel.this.isDayBold(CalendarPanel.this.calendar
                        .get(1), CalendarPanel.this.calendar
                        .get(2) + 1, 
                        this.number))
                      {
                        v = CalendarPanel.this.calHours(
                          false, 
                          CalendarPanel.this.calendar.get(1), 
                          CalendarPanel.this.calendar
                          .get(2) + 1, 
                          this.number);
                        s = v.elementAt(0).toString();
                        msg = v.elementAt(1).toString();
                        setFont(new Font(getFont()
                          .getFontName(), 1, 
                          getFont().getSize()));
                        this.currentColor = CalendarHelper.EXCEPTION_COLOR;
                      } else {
                        setFont(CalendarPanel.this.smallFont);
                      }
                    }
                  }
                  sbf.append("<html><div align=\"center\">").append(Integer.toString(this.number)).append("</div><div align=\"center\">" + s + "</div>" + "</html>");
                  //System.out.println("tooltip msg is "+msg);
                  setTooTipMsg(msg);
                } else {
                  //System.out.println("YFJC is noworkingday");
                  String s = "";
                  if (isException()) {
                	//System.out.println("YFJC is isException");
                    Vector v = CalendarPanel.this.calHours(true, CalendarPanel.this.calendar.get(1), CalendarPanel.this.calendar.get(2) + 1, this.number);
                    if (v.size() == 3) {
                      this.currentColor = CalendarPanel.this.getSomeColor(Integer.parseInt((String)v.elementAt(0)));
                      s = v.elementAt(0) + ";" + v.elementAt(1);
                      if (!v.elementAt(0).toString().equals("0"))
                      {
                        if (!v.elementAt(1).toString().equals("0")){
                        	//System.out.println("tooltip msg is "+v.elementAt(2).toString());
                        	setTooTipMsg(v.elementAt(2).toString());
                        }
                      }
                    }
                  }
                  else {
                	//System.out.println("YFJC is noworkingday no exception");
                    Vector v = CalendarPanel.this.calHours(false, CalendarPanel.this.calendar.get(1), CalendarPanel.this.calendar.get(2) + 1, this.number);
                    s = (String)v.elementAt(0);
                    if (!v.elementAt(0).toString().equals("0")) {
                      //System.out.println("tooltip msg is "+v.elementAt(1).toString());
                      setTooTipMsg(v.elementAt(1).toString());
                    }
                    this.currentColor = CalendarHelper.NON_WORKING_COLOR;
                  }
                  sbf.append("<html><div align=\"center\">").append(Integer.toString(this.number)).append("</div><div align=\"center\">" + s + "</div>" + "</html>");
                }
                text = sbf.toString();
              }
              else {
                CalendarPanel.this.isStandar = false;
                text = Integer.toString(this.number);
                setFont(CalendarPanel.this.smallFont);
              }
            } catch (TCException e) {
              e.printStackTrace();
            }
          else {
            text = Integer.toString(this.number);
          }
          if (isSelected()) {
            setSelected(false);
          }
          setText(text);
          setEnabled(true);
      }
      setBackground(this.currentColor);
      //setForeground(this.isSelected ? Color.white : Color.black);
    }
    //YFJC_ADDED
    public String getToolTipMsg()

    {
      return this.curToolTipMsg;
    }

    public void setTooTipMsg(String s) {
      this.curToolTipMsg = s;
    }
    //YFJC_ADDED
    public int getNumber()
    {
      //System.out.println("getNumber");
      return this.number;
    }

    public void setSelected(boolean paramBoolean)
    {
      //System.out.println("setSelected paramBoolean");
      super.setSelected(paramBoolean);
      if (paramBoolean)
        CalendarPanel.this.fireButtonSelected(this);
    }

    public void clearSelection()
    {
      //System.out.println("clearSelection");
      this.isSelected = false;
      GregorianCalendar localGregorianCalendar = new GregorianCalendar();
      localGregorianCalendar.setTime(CalendarPanel.this.calendar.getTime());
      localGregorianCalendar.set(5, this.number);
      CalendarPanel.this.removeSelectedCal(localGregorianCalendar);
      updateButtonDisplay();
    }

    public void unset()
    {
      //System.out.println("unset");    	
      this.currentColor = Color.white;
      this.evt = null;
      this.eventType = 3;
      this.nonWorkingDay = false;
      setIcon(null);
      updateButtonDisplay();
    }

    private void selectDate()
    {
      //System.out.println("selectDate");
      if (CalendarPanel.this.workCalendarPanel == null)
        return;
      this.isSelected = true;
      updateButtonDisplay();
      CalendarPanel.this.calendar.set(5, this.number);
      CalendarPanel.this.selected.setTime(CalendarPanel.this.calendar.getTime());
      GregorianCalendar localGregorianCalendar = new GregorianCalendar();
      localGregorianCalendar.setTime(CalendarPanel.this.calendar.getTime());
      if (!CalendarPanel.this.isCalendarSelected(localGregorianCalendar))
        CalendarPanel.this.selecteds.add(localGregorianCalendar);
      int i = CalendarPanel.this.calendar.get(7);
      int j = CalendarPanel.this.calendar.get(1);
      int k = CalendarPanel.this.calendar.get(2);
      TCCalendarEvent localTCCalendarEvent = CalendarPanel.this.workCalendarPanel.getCurrentEventForDate(new TCDate(CalendarPanel.this.calendar));
      if (localTCCalendarEvent == null)
        localTCCalendarEvent = CalendarPanel.this.getMatchingEvent(CalendarPanel.this.calEvents, j, k, this.number);
      if ((localTCCalendarEvent == null) || (localTCCalendarEvent.getEventType() == 4))
        localTCCalendarEvent = CalendarPanel.this.getMatchingEvent(CalendarPanel.this.baseCalEvents, j, k, this.number);
      boolean bool = false;
      if (localTCCalendarEvent == null)
        if (CalendarPanel.this.tcCalendar != null)
          bool = CalendarPanel.this.tcCalendar.isWorkingDay(i);
        else
          bool = true;
      CalendarPanel.this.workCalendarPanel.updateState(localTCCalendarEvent, bool);
      CalendarPanel.this.updateDateProvider();
    }

    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      //System.out.println("stateChanged paramChangeEvent");
      if ((paramChangeEvent.getSource() instanceof DateButton))
      {
        DateButton localDateButton = (DateButton)paramChangeEvent.getSource();
        if (localDateButton.getNumber() == getNumber())
          selectDate();
        else if (!CalendarPanel.this.isCtrlDown)
          clearSelection();
      }
    }

    public Color getButtonColor(int paramInt)
    {
      //System.out.println("getButtonColor paramInt");
      Color localColor = CalendarHelper.WORKING_COLOR;
      switch (paramInt)
      {
      case 1:
        localColor = CalendarHelper.EXCEPTION_COLOR;
        break;
      case 2:
        localColor = CalendarHelper.INHERITED_EXCEPTION_COLOR;
        break;
      case 0:
        localColor = CalendarHelper.NON_WORKING_COLOR;
        break;
      }
      //YFJC_ADDED
      //localColor = isSelected() ? CalendarHelper.SELECTED_COLOR : localColor;
      if ((!CalendarPanel.this.checkRole) && (CalendarPanel.this.isStandar)) {
          if (this.number != 0)
        	 localColor = isSelected() ? CalendarPanel.this.mySelectColor : this.currentColor;
      }
      else {
        localColor = isSelected() ? CalendarHelper.SELECTED_COLOR : localColor;
      }
      ////YFJC_REMOVED
      return localColor;
    }

    public void setEventType(int paramInt)
    {
      //System.out.println("setEventType paramInt");
      this.eventType = paramInt;
    }

    public int getEventType()
    {
      //System.out.println("getEventType");
      return this.eventType;
    }

    public boolean isException()
    {
     // System.out.println("isException");
      return this.eventType == 1;
    }

    public boolean isInheritedException()
    {
      //System.out.println("isInheritedException");
      return this.eventType == 2;
    }

    public void setEvent(TCCalendarEvent paramTCCalendarEvent)
    {
     // System.out.println("setEvent paramTCCalendarEvent");
      this.evt = paramTCCalendarEvent;
      this.eventType = (this.evt == null ? 3 : this.evt.getEventType());
      updateButtonDisplay();
    }

    public TCCalendarEvent getEvent()
    {
      //System.out.println("getEvent");
      return this.evt;
    }

    public void setNonWorking(boolean paramBoolean)
    {
      //System.out.println("setNonWorking paramBoolean");
      this.nonWorkingDay = paramBoolean;
      this.eventType = (paramBoolean ? 0 : 3);
      updateButtonDisplay();
    }

    public boolean isNonWorking()
    {
      //System.out.println("isNonWorking ");
      return this.nonWorkingDay;
    }

    public String getToolTipText(MouseEvent paramMouseEvent)
    {
      //System.out.println("getToolTipText paramMouseEvent");
      return getToolTipMessage();
    }

    private String getToolTipMessage()
    {
     // System.out.println("getToolTipMessage");
      String str1 = null;
      if ((!CalendarPanel.this.checkRole) && (CalendarPanel.this.isStandar)) {
    	  str1 = getToolTipMsg();
        }
      else if ((getNumber() != 0) && ((getEvent() != null) || (isNonWorking())))
      {
        String str2 = CalendarPanel.this.r.getString("cal.TIP");
        String str3 = "";
        String str4 = "";
        String str5 = "00:00";
        StringBuilder localStringBuilder = new StringBuilder();
        if (getEvent() != null)
        {
          int i = getEvent().getTotalMinutesForEvent();
          str3 = i <= 0 ? CalendarPanel.this.r.getString("NON_WORKING") : CalendarPanel.this.r.getString("WORKING");
          str4 = getEventType() == 2 ? CalendarPanel.this.r.getString("INH_EXCEPTION") : CalendarPanel.this.r.getString("EXCEPTION");
          if (i < 0)
          {
            str5 = "00:00";
          }
          else
          {
        	StringBuilder localObject = new StringBuilder();
            ((StringBuilder)localObject).append(String.valueOf(i / 60));
            ((StringBuilder)localObject).append(":");
            ((StringBuilder)localObject).append(String.valueOf(i % 60));
            str5 = ((StringBuilder)localObject).toString();
          }
          Object localObject = getEvent().askEventRanges();
          if (!getEvent().isNonWorking())
            for (int j = 0; j < ((List)localObject).size(); j++)
            {
              localStringBuilder.append("<p>");
              localStringBuilder.append("<b>");
              localStringBuilder.append(j + 1);
              localStringBuilder.append("</b>");
              localStringBuilder.append(".  ");
              localStringBuilder.append(DailyCalendarHoursPanel.setHourTextValue(((TCComponentTCCalendar.TimeRange)((List)localObject).get(j)).start));
              localStringBuilder.append(" : ");
              localStringBuilder.append(DailyCalendarHoursPanel.setHourTextValue(((TCComponentTCCalendar.TimeRange)((List)localObject).get(j)).end));
              localStringBuilder.append("</p>");
            }
        }
        else if (isNonWorking())
        {
          str3 = CalendarPanel.this.r.getString("NON_WORKING");
          str4 = CalendarPanel.this.r.getString("EXCEPTION");
        }
        str1 = MessageFormat.format(str2, new Object[] { str3, str4, str5, localStringBuilder.toString() });
      }
      return str1;
    }

    private void updateButtonDisplay()
    {
      //System.out.println("updateButtonDisplay");
      Icon localIcon = null;
      if ((this.eventType == 1) || (this.eventType == 2))
        localIcon = CalendarPanel.this.lineIcon;
      setIcon(localIcon);
      this.currentColor = getButtonColor(this.eventType);
      
      //YFJC_ADDED
      /*setBackground(this.currentColor);
      if ((this.evt != null) && (this.evt.getEventType() == 1) && (this.evt.isNonWorking()))
        setBackground(CalendarHelper.NON_WORKING_COLOR);
      setForeground(isSelected() ? Color.white : Color.black);
      */
      if ((!CalendarPanel.this.checkRole) && (!CalendarPanel.this.isStandar)) {
          setBackground(this.currentColor);
          if ((this.evt != null) && (this.evt.getEventType() == 1) && (this.evt.isNonWorking())) {
            setBackground(CalendarHelper.NON_WORKING_COLOR);
          }
      }
      repaint();
    //YFJC_END
    }
  }

  private static class JComboLabel extends JLabel
  {
	  
    private String[] texts;

    public JComboLabel(String[] paramArrayOfString)
    {
      super();
      //System.out.println("JComboLabel paramArrayOfString");
      this.texts = paramArrayOfString;
    }

    public void setSelectedIndex(int paramInt)
    {
      //System.out.println("setSelectedIndex paramInt");
      setText(this.texts[paramInt]);
    }
  }

  private class MyKeyAdapter extends KeyAdapter
  {
    private MyKeyAdapter()
    {
    }

    public void keyPressed(KeyEvent paramKeyEvent)
    {
      //System.out.println("keyPressed paramKeyEvent");
      int i = paramKeyEvent.getKeyCode();
      if (i == 37)
      {
        CalendarPanel.this.calendar.add(5, -1);
        CalendarPanel.this.selected.add(5, -1);
      }
      else if (i == 39)
      {
        CalendarPanel.this.calendar.add(5, 1);
        CalendarPanel.this.selected.add(5, 1);
      }
      else if (i == 38)
      {
        CalendarPanel.this.calendar.add(5, -7);
        CalendarPanel.this.selected.add(5, -7);
      }
      else if (i == 40)
      {
        CalendarPanel.this.calendar.add(5, 7);
        CalendarPanel.this.selected.add(5, 7);
      }
      else if (i == 33)
      {
        if (paramKeyEvent.isControlDown())
          CalendarPanel.this.calendar.add(1, -1);
        else
          CalendarPanel.this.decMonth();
      }
      else if (i == 34)
      {
        if (paramKeyEvent.isControlDown())
          CalendarPanel.this.calendar.add(1, 1);
        else
          CalendarPanel.this.incMonth();
      }
      else if (i == 36)
      {
        CalendarPanel.this.calendar.set(5, 1);
        CalendarPanel.this.selected.set(5, 1);
      }
      else if (i == 35)
      {
        CalendarPanel.this.calendar.set(5, CalendarPanel.this.calendar.getActualMaximum(5));
        CalendarPanel.this.selected.set(5, CalendarPanel.this.calendar.getActualMaximum(5));
      }
      else if (i == 10)
      {
        CalendarPanel.this.calendar.set(5, 1);
        int j = CalendarPanel.this.calendar.get(7);
        for (int k = 0; k < CalendarPanel.this.dates.length; k++)
          if (CalendarPanel.this.dates[k].hasFocus())
          {
            CalendarPanel.this.dates[k].setSelected(true);
            CalendarPanel.this.selected.set(CalendarPanel.this.selected.get(1), CalendarPanel.this.selected.get(2), k - j);
            CalendarPanel.this.calendar.set(CalendarPanel.this.selected.get(1), CalendarPanel.this.selected.get(2), k - j);
            break;
          }
      }
      CalendarPanel.this.updateText();
      JToggleButton localJToggleButton = CalendarPanel.this.updateDatePanel();
      localJToggleButton.requestFocusInWindow();
    }
  }

  private class TextFieldUpdater extends FocusAdapter
    implements ActionListener
  {
    private TextFieldUpdater()
    {
    }

    public void actionPerformed(ActionEvent paramActionEvent)
    {
      //System.out.println("TextFieldUpdater actionPerformed paramActionEvent");
      CalendarPanel.this.getAllText();
      CalendarPanel.this.clear();
      CalendarPanel.this.updateDatePanel();
      CalendarPanel.this.workCalendarPanel.enablePrefValueSettings();
      if (CalendarPanel.this.month.isEnabled())
        CalendarPanel.this.dailyCalendarHoursPanel.setEnabled(true);
      else
        CalendarPanel.this.dailyCalendarHoursPanel.setEnabled(false);
    }

    public void focusLost(FocusEvent paramFocusEvent)
    {
      //System.out.println("focusLost paramFocusEvent");
      CalendarPanel.this.getAllText();
      CalendarPanel.this.clear();
      CalendarPanel.this.updateDatePanel();
      CalendarPanel.this.workCalendarPanel.enablePrefValueSettings();
      if (CalendarPanel.this.month.isEnabled())
        CalendarPanel.this.dailyCalendarHoursPanel.setEnabled(true);
      else
        CalendarPanel.this.dailyCalendarHoursPanel.setEnabled(false);
    }
  }
}