//package com.yfjcebp.extsupport.form1;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.Font;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.GridLayout;
//import java.awt.Rectangle;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.PrintWriter;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.EventObject;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Locale;
//import java.util.Properties;
//import java.util.Set;
//import java.util.Vector;
//
//import javax.swing.AbstractButton;
//import javax.swing.ActionMap;
//import javax.swing.BoxLayout;
//import javax.swing.DefaultCellEditor;
//import javax.swing.DefaultListSelectionModel;
//import javax.swing.JButton;
//import javax.swing.JColorChooser;
//import javax.swing.JComboBox;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTabbedPane;
//import javax.swing.JTable;
//import javax.swing.JTextField;
//import javax.swing.JViewport;
//import javax.swing.ListSelectionModel;
//import javax.swing.SwingConstants;
//import javax.swing.border.EmptyBorder;
//import javax.swing.border.LineBorder;
//import javax.swing.border.TitledBorder;
//import javax.swing.event.CellEditorListener;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import javax.swing.event.EventListenerList;
//import javax.swing.event.TableModelEvent;
//import javax.swing.event.TableModelListener;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.JTableHeader;
//import javax.swing.table.TableCellEditor;
//import javax.swing.table.TableColumn;
//import javax.swing.table.TableColumnModel;
//
//import com.teamcenter.rac.aifrcp.AIFUtility;
//import com.teamcenter.rac.common.viewedit.ViewEditHelper;
//import com.teamcenter.rac.kernel.TCComponent;
//import com.teamcenter.rac.kernel.TCComponentContextList;
//import com.teamcenter.rac.kernel.TCComponentItem;
//import com.teamcenter.rac.kernel.TCComponentItemRevision;
//import com.teamcenter.rac.kernel.TCComponentItemType;
//import com.teamcenter.rac.kernel.TCComponentQuery;
//import com.teamcenter.rac.kernel.TCComponentQueryType;
//import com.teamcenter.rac.kernel.TCComponentTCCalendar;
//import com.teamcenter.rac.kernel.TCException;
//import com.teamcenter.rac.kernel.TCPreferenceService;
//import com.teamcenter.rac.kernel.TCProperty;
//import com.teamcenter.rac.kernel.TCSession;
//import com.teamcenter.rac.kernel.TCTextService;
//import com.teamcenter.rac.kernel.TCUserService;
//import com.teamcenter.rac.schedule.calendar.TCCalendar;
//import com.teamcenter.rac.schedule.calendar.TCCalendarEvent;
//import com.teamcenter.rac.schedule.componentutils.CalendarHelper;
//import com.teamcenter.rac.stylesheet.AbstractRendering;
//import com.teamcenter.rac.util.MessageBox;
//import com.teamcenter.rac.util.combobox.iComboBox;
//import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
//import com.teamcenter.services.rac.core.DataManagementService;
//import com.teamcenter.services.rac.core._2008_06.DataManagement;
//import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;
//import com.teamcenter.soa.exceptions.NotLoadedException;
//import com.yfjcebp.extsupport.form1.JCITableUtil.LimitedDocument;
//
//public class JCI6Ext2SuppForm extends AbstractRendering {
//
//	private TCComponent form;
//	private TCSession tcsession;
//
//	// 自定义假期
//	private String holiday_preference = "YFJC_Holiday_Also_Weekend";
//	private String[] weekDays = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri",
//			"Sat" };
//	private String[] mon = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
//			"Aug", "Sep", "Oct", "Nov", "Dec" };
//	private HashMap<String, Integer> weekDayList = new HashMap<String, Integer>();
//
//	/**
//	 * 费率:首选项YFJC_User_Rate_Mapping_ExtSupport key:Normal、Overtime、Holiday、Weekend value:
//	 */
//	HashMap<String, String> ModifierMap = new HashMap<String, String>();
//
//	private int jciYear; // 当前表单年
//	private int jciMonth; // 当前表单月
//	private List<Date> tcweeks; // 当月的每天的星期情况
//	private List<Integer> tcweeks_iswork; // 当月的每天的工作与否情况 1:正常 2：周末 3：假期
//
//	// 查询
//	private String QUERY_NAME = "__YFJC_LCC_ExtRateRevision";
//	private ArrayList<TCComponentItemRevision> lastPassRevList;
//	private ArrayList<Object[]> extTSEAttrList;
//
//	// Detail页
//	private JTextField normalTField;
//	private JTextField overTimeTField;
//	private JTextField weekendTField;
//	private JTextField holidayTField;
//
//	// ***********固定table的配置***************
//	private JTable fixTable;
//	private DefaultTableModel dmFix;
//	private Vector fixColumnName;
//	private Vector data1;
//	// ***********移动table的配置***************
//	private JTable moveTable;
//	private DefaultTableModel dmMove;
//	private Vector moveColumnName;
//	private Vector data2;
//	/**
//	 * form的属性jci6_Ext2Detail的值整理
//	 */
//	private Vector data;
//	private Vector data_form;
//
//	public final static String normalType = "Normal Hours";
//	public final static String overtimeType = "Overtime Hours";
//	public final static String weekendType = "Weekend Hours";
//	public final static String holidayType = "Holiday Hours";
//
//	private Boolean isColorConfiguration = false;
//
//	// public final static Color normalColor = Color.GREEN;
//	// public final static Color overtimeColor = Color.ORANGE;
//	// public final static Color weekendColor = Color.LIGHT_GRAY;
//	// public final static Color holidayColor = Color.PINK;
//	//
//	// public final static Color normalColorSelected = new Color(67, 143, 255);
//	// public final static Color normalColorNoSelectedIsNull = Color.WHITE;
//	// public final static Color normalColorNoSelectedIsZero = Color.RED;
//	// public final static Color normalColorNoSelectedIsEight = Color.GREEN;
//	// public final static Color normalColorNoSelectedIsNotEight = Color.YELLOW;
//	// public final static Color overtimeColorSelected = new Color(104, 255, 1);
//	// public final static Color overtimeColorNoSelected = Color.ORANGE;
//	// public final static Color weekendColorSelected = new Color(120, 120,
//	// 120);
//	// public final static Color weekendColorNoSelected = new Color(180, 180,
//	// 180);
//	// public final static Color holidayColorSelected = new Color(217, 148, 55);
//	// public final static Color holidayColorNoSelected = new Color(245, 227,
//	// 203);
//	public static Color normalColor;
//	public static Color overtimeColor;
//	public static Color weekendColor;
//	public static Color holidayColor;
//
//	public static Color normalColorSelected;
//	public static Color normalColorNoSelectedIsNull;
//	public static Color normalColorNoSelectedIsZero;
//	public static Color normalColorNoSelectedIsEight;
//	public static Color normalColorNoSelectedIsNotEight;
//	public static Color overtimeColorSelected;
//	public static Color overtimeColorNoSelected;
//	public static Color weekendColorSelected;
//	public static Color weekendColorNoSelected;
//	public static Color holidayColorSelected;
//	public static Color holidayColorNoSelected;
//	public static Color approvedCostColor;
//	public static Color toBeApprovedColor;
//
//	public static Color approvedHeadColor;
//	public static Color toBeApprovedHeadColor;
//	
//	private static Color assignButtonColor;
//	private static Color clearButtonColor;
//	private static Color addButtonColor;
//	private static Color deleteButtonColor;
//	private static Color reduceButtonColor;
//	
//	// 保存单元格的颜色
//	private HashMap<String, Color> rColor = new HashMap<String, Color>();
//	private HashMap<Integer, Color> programColor = new HashMap<Integer, Color>();
//
//	/**
//	 * 用于保存已经发布的行
//	 */
//	private HashMap<String, Boolean> passMap_fix = new HashMap<String, Boolean>();
//	private HashMap<String, Boolean> passMap_move = new HashMap<String, Boolean>();
//	
//	//保存删除的明细
//	private Vector deleteExts = new Vector();
//
//	/**
//	 * 用于保存从第8列开始的所有单元格的类型： Normal、Weekend、Overtime、Holiday cellTypeMap是保存当个cell
//	 * cellTypeMap_Column是保存一列的cell
//	 */
//	private HashMap<String, String> cellTypeMap_fix = new HashMap<String, String>();
//	private HashMap<String, String> cellTypeMap_move = new HashMap<String, String>();
//	private HashMap<Integer, String> cellTypeMap_Column = new HashMap<Integer, String>();
//	private JTable table0;
//
//	// 查询program后做添加用
//	private Vector programInfoList = new Vector();
//	private HashMap<String, String> programinfoMap = new HashMap<String, String>();
//
//	// second table value
//	private Vector secondData;
//
//	// 首选项YFJC_User_Rate_Mapping_ExtSupport
//	private HashMap<String, String[]> rateMap = new HashMap<String, String[]>();
//	private JTextField NField;
//	private JButton NButton;
//	private JTextField OField;
//	private JButton OButton;
//	private JTextField HField;
//	private JButton HButton;
//	private JTextField WField;
//	private JButton WButton;
//	private JTextField n1SelectedField;
//	private JButton n1SelectedButton;
//	private JTextField n1NoSelectedField;
//	private JButton n1NoSelectedButton;
//	private JTextField n2SelectedField;
//	private JButton n2SelectedButton;
//	private JTextField n2NoSelectedField;
//	private JButton n2NoSelectedButton;
//	private JTextField n3SelectedField;
//	private JButton n3SelectedButton;
//	private JTextField n3NoSelectedField;
//	private JButton n3NoSelectedButton;
//	private JTextField n4SelectedField;
//	private JButton n4SelectedButton;
//	private JTextField n4NoSelectedField;
//	private JButton n4NoSelectedButton;
//	private JTextField overtimeSelectedField;
//	private JButton overtimeSelectedButton;
//	private JTextField overtimeNoSelectedField;
//	private JButton overtimeNoSelectedButton;
//	private JTextField weekendSelectedField;
//	private AbstractButton weekendSelectedButton;
//	private JTextField weekendNoSelectedField;
//	private JButton weekendNoSelectedButton;
//	private JTextField holidaySelectedField;
//	private AbstractButton holidaySelectedButton;
//	private JTextField holidayNoSelectedField;
//	private JButton holidayNoSelectedButton;
//	private JTable summaryTabel;
//	private TCUserService userService;
//	private boolean isOKToModify;
//	private boolean isOKToCheckout;
//	private HashMap<Integer, Vector> overtimeMap;
//	private JTabbedPane tabbedPanel;
//	private JTextField approvedField;
//	private JButton approvedButton;
//	private JTextField noApprovedField;
//	private JButton noApprovedButton;
//	private JTextField approvedHeadField;
//	private JButton approvedHeadButton;
//	private JTextField noApprovedHeadField;
//	private JButton noApprovedHeadButton;
//	private JTextField assignField;
//	private JButton assignColorButton;
//	private JTextField clearField;
//	private JButton clearColorButton;
//	private JTextField addField;
//	private JButton addColorButton;
//	private JTextField deleteField;
//	private JButton deleteColorButton;
//	private JTextField reduceField;
//	private JButton reduceColorButton;
//	private ArrayList<Integer> passLines;
//	private JButton reduceButton;
//	private boolean ischeckoutAble;
//	private String roleName;
//	private JScrollPane scroll;
//	
//	public HashMap<Integer, String> col1Values = new HashMap<Integer, String>();
//	
//	private PrintWriter pw = null;
//	
//
//	public JCI6Ext2SuppForm(TCComponent arg0) throws Exception {
//		super(arg0);
//		form = arg0;
//		tcsession = arg0.getSession();
//		String tmpfolder=System.getProperty("java.io.tmpdir");
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//		tmpfolder = tmpfolder+formatter.format(new Date())+"JCI6Ext2SuppForm.log";
//		pw = new PrintWriter(tmpfolder);
//		
//		for (int i = 0; i < weekDays.length; i++) {
//			weekDayList.put(weekDays[i], i);
//		}
//		ArrayList<TCComponentItemRevision> itemRevisionList = searchOperation();
////		lastPassRevList = getLastPassRevision(itemRevisionList);
//		lastPassRevList = itemRevisionList;
//		pw.println("lastPassRevList:" + lastPassRevList.size());
//		pw.flush();
//		extTSEAttrList = getExtTSEAttrsFromExtRateRevisions(lastPassRevList);
//		prefLegal();
//		if (!prefLegal()) {
//			return;
//		}
//		jciYear = form.getIntProperty("jci6_Year");
//		jciMonth = form.getIntProperty("jci6_Month");
//		SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.ENGLISH);
//		tcweeks = dayWithWeekend();
//		// TCComponentTCCalendar cl = CalendarHelper
//		// .getDefaultBaseCalendar(tcsession);
//		// TCCalendar tcc = new TCCalendar(cl);
//		TCComponentTCCalendar standerdCanelder = null;
//		TCComponentTCCalendar[] allCalendars = CalendarHelper.getAllCalendars(tcsession, 1);
//		for (TCComponentTCCalendar tcComponentTCCalendar : allCalendars) {
//			try {
//				String calendarName = CalendarHelper.getCalendarName(tcComponentTCCalendar);
//				if (calendarName.contains("Standard" )||calendarName.contains("标准" ) ) {
//					standerdCanelder = tcComponentTCCalendar;
//					break;
//				}
//			} catch (TCException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		TCCalendar tccalendar = new TCCalendar(standerdCanelder);
//		tcweeks_iswork = new ArrayList<Integer>();
//		ArrayList<Date> holidayList = getPreferenceHoliday();
//		Calendar cal = Calendar.getInstance();
//		for (Date date : holidayList) {
//			SimpleDateFormat tmpsdf = new SimpleDateFormat("yyyyMMdd");
//			String str1 = tmpsdf.format(date);
//			pw.println("首选项："+str1);
//			pw.flush();
//		}
//		for (Date d : tcweeks) {
//			boolean isH = false;
//			boolean isW = false;
//			boolean isE = false;
//			SimpleDateFormat tmpsdf = new SimpleDateFormat("yyyyMMdd");
//			String str2 = tmpsdf.format(d);
//			pw.println("str2:"+str2);
//			pw.flush();
//			for (Date date : holidayList) {
//				String str1 = tmpsdf.format(date);
//				if (str1.equals(str2)) {
//					isH = true;
//					break;
//				}
//			}
////			isH = holidayList.contains(d);
//			if (sdf.format(d).contains("Sat") || sdf.format(d).contains("Sun")) {
//				isW = true;
//			}
//			cal.setTime(d);
//			TCCalendarEvent event1 = tccalendar.getEvent(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), false);
//			if (event1 != null && event1.getEventType() == 1) {
//				isE = true;
//			}
//			pw.println("=====rq:"+str2+"==="+isH+"==="+isW+"==="+isE);
//			pw.flush();
//			//判断weekend
//			if (isH == false) {
//				if (isW == false && isE == true) {
//					tcweeks_iswork.add(2);
//					continue;
//				}else if (isW == true && isE == false) {
//					tcweeks_iswork.add(2);
//					continue;
//				}
//			}
//			//判断H
//			if (isH == true) {
//				tcweeks_iswork.add(3);
//				continue;
//			}
//			tcweeks_iswork.add(1);
//			
////			if (isH && !isW) {
////				tcweeks_iswork.add(3);
////				continue;
////			}
////			if (isW) {
////				tcweeks_iswork.add(1);
////				continue;
////			}
////			tcweeks_iswork.add(2);
//
//		}
////		for (Date d : tcweeks) {
////			boolean isH = false;
////			boolean isW = false;
////			isH = holidayList.contains(d);
////			// isW = tcc.isWorkingDay(weekDayList.get(sdf.format(d)));
////			if (sdf.format(d).contains("Sat") || sdf.format(d).contains("Sun")) {
////				isW = false;
////			} else {
////				isW = true;
////			}
////			if (isH && !isW) {
////				tcweeks_iswork.add(3);
////				continue;
////			}
////			if (isW) {
////				tcweeks_iswork.add(1);
////				continue;
////			}
////			tcweeks_iswork.add(2);
////
////		}
//		setRenderingReadWrite();
//		// Registry registry = Registry.getRegistry(this);
//		// Registry.setValue("ok", "SaveAndExit");
//
//		setPreferenceColor();
//
//		searchProgram();
//		initData();
//		initForm();
//
//		allTableCount();
//		allCount();
//	}
//
//	
//
//	private void setPreferenceColor() {
//		String[] color = tcsession.getPreferenceService().getStringArray(4,
//				"YFJC_Ext2SupportForm_Color");
//		for (String str : color) {
//			String[] strs = str.split("=");
//			if (strs[0].equals("CanEditColor")) {
//				isColorConfiguration = Boolean.parseBoolean(strs[1]);
//			} else {
//				if (strs[0].equals("normalColor")) {
//					String[] rgbs = strs[1].split(",");
//					normalColor = new Color(Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("overtimeColor")) {
//					String[] rgbs = strs[1].split(",");
//					overtimeColor = new Color(Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("weekendColor")) {
//					String[] rgbs = strs[1].split(",");
//					weekendColor = new Color(Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("holidayColor")) {
//					String[] rgbs = strs[1].split(",");
//					holidayColor = new Color(Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("normalColorSelected")) {
//					String[] rgbs = strs[1].split(",");
//					normalColorSelected = new Color(Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("normalColorNoSelectedIsNull")) {
//					String[] rgbs = strs[1].split(",");
//					normalColorNoSelectedIsNull = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("normalColorNoSelectedIsZero")) {
//					String[] rgbs = strs[1].split(",");
//					normalColorNoSelectedIsZero = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("normalColorNoSelectedIsEight")) {
//					String[] rgbs = strs[1].split(",");
//					normalColorNoSelectedIsEight = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("normalColorNoSelectedIsNotEight")) {
//					String[] rgbs = strs[1].split(",");
//					normalColorNoSelectedIsNotEight = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("overtimeColorSelected")) {
//					String[] rgbs = strs[1].split(",");
//					overtimeColorSelected = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("overtimeColorNoSelected")) {
//					String[] rgbs = strs[1].split(",");
//					overtimeColorNoSelected = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("weekendColorSelected")) {
//					String[] rgbs = strs[1].split(",");
//					weekendColorSelected = new Color(Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("weekendColorNoSelected")) {
//					String[] rgbs = strs[1].split(",");
//					weekendColorNoSelected = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("holidayColorSelected")) {
//					String[] rgbs = strs[1].split(",");
//					holidayColorSelected = new Color(Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				} else if (strs[0].equals("holidayColorNoSelected")) {
//					String[] rgbs = strs[1].split(",");
//					holidayColorNoSelected = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				}else if (strs[0].equals("approvedCostColor")) {
//					pw.println("存在approvedCostColor");
//					pw.flush();
//					String[] rgbs = strs[1].split(",");
//					approvedCostColor = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				}else if (strs[0].equals("toBeApprovedColor")) {
//					pw.println("存在toBeApprovedColor");
//					pw.flush();
//					String[] rgbs = strs[1].split(",");
//					toBeApprovedColor = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				}else if (strs[0].equals("approvedHeadColor")) {
//					pw.println("存在approvedHeadColor");
//					pw.flush();
//					String[] rgbs = strs[1].split(",");
//					approvedHeadColor = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				}else if (strs[0].equals("toBeApprovedHeadColor")) {
//					pw.println("存在toBeApprovedHeadColor");
//					pw.flush();
//					String[] rgbs = strs[1].split(",");
//					toBeApprovedHeadColor = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				}else if (strs[0].equals("assignButtonColor")) {
//					pw.println("存在assignButtonColor");
//					pw.flush();
//					String[] rgbs = strs[1].split(",");
//					assignButtonColor = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				}else if (strs[0].equals("clearButtonColor")) {
//					pw.println("存在clearButtonColor");
//					pw.flush();
//					String[] rgbs = strs[1].split(",");
//					clearButtonColor = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				}else if (strs[0].equals("addButtonColor")) {
//					pw.println("存在addButtonColor");
//					pw.flush();
//					String[] rgbs = strs[1].split(",");
//					addButtonColor = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				}else if (strs[0].equals("deleteButtonColor")) {
//					pw.println("存在deleteButtonColor");
//					pw.flush();
//					String[] rgbs = strs[1].split(",");
//					deleteButtonColor = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				}else if (strs[0].equals("reduceButtonColor")) {
//					pw.println("存在reduceButtonColor");
//					pw.flush();
//					String[] rgbs = strs[1].split(",");
//					reduceButtonColor = new Color(
//							Integer.parseInt(rgbs[0]),
//							Integer.parseInt(rgbs[1]),
//							Integer.parseInt(rgbs[2]));
//				}
//				
//			}
//		}
//	}
//
//	// @SuppressWarnings("unchecked")
//	private void searchProgram() {
//		// try {
//		// TCComponentItemType aa =
//		// (TCComponentItemType)tcsession.getTypeComponent("JCI6_ProgramInfo");
//		// TCComponentItem[] bb = aa.findItems("*");
//		// pw.println("bb:"+bb.length);
//		// } catch (TCException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//
//		// select i.PITEM_ID,w.POBJECT_NAME,p.PUID from PITEM i,PWORKSPACEOBJECT
//		// w,PJCI6_PROGRAMINFO p where i.PUID=w.PUID and p.PUID = i.PUID and
//		// w.PUID = p.PUID;
//		InputStream inStream = this.getClass().getResourceAsStream(
//				"DB.properties");
//		Properties prop = new Properties();
//		try {
//			prop.load(inStream);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String USERNAMR = prop.getProperty("USERNAMR");
//		String PASSWORD = prop.getProperty("PASSWORD");
//		String DRVIER = prop.getProperty("DRVIER");
//		String URL = prop.getProperty("URL");
//		try {
//			inStream.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		Connection connection = null;
//		programInfoList.clear();
//		programinfoMap.clear();
//		try {
//			Class.forName(DRVIER);
//			connection = DriverManager.getConnection(URL, USERNAMR, PASSWORD);
//			pw.println("成功连接数据库");
//			pw.flush();
//		} catch (ClassNotFoundException e) {
//			throw new RuntimeException("class not find !", e);
//		} catch (SQLException e) {
//			throw new RuntimeException("get connection error!", e);
//		}
//
//		// String sql =
//		// "select i.PITEM_ID,w.POBJECT_NAME,p.PUID,p.PJCI6_CLOSEDATE " +
//		// "from PITEM i,PWORKSPACEOBJECT w,PJCI6_PROGRAMINFO p " +
//		// "where i.PUID=w.PUID and p.PUID = i.PUID and w.PUID = p.PUID and p.PJCI6_CLOSEDATE is null";
//		String sql = "SELECT i.PITEM_ID,w.POBJECT_NAME,p.PUID FROM PITEM i,PWORKSPACEOBJECT w,PJCI6_PROGRAMINFO p where p.puid in (select RSECONDARY_OBJECTU from pimanrelation where rrelation_typeu = 'QTSFOEzaopEFZA' and RPRIMARY_OBJECTU IN (select RPRIMARY_OBJECTU from pimanrelation where rrelation_typeu = 'QTSFOEzaopEFZA' and RSECONDARY_OBJECTU IN (select puid from pschedule))) and i.PUID=w.PUID and p.PUID = i.PUID and w.PUID = p.PUID and p.pjci6_closedate is null";
//		try {
//			Statement stmt = connection.createStatement();
//			ResultSet rs = stmt.executeQuery(sql);
//			while (rs.next()) {
//				// programInfoList
//				String programInfoID = rs.getString(1);
//				String programInfoName = rs.getString(2);
////				String programInfoUID = rs.getString(3);
//
//				String programInfoShowName = programInfoID + "_"
//						+ programInfoName;
//				programInfoList.add(programInfoShowName);
//				Collections.sort(programInfoList);  
//				programinfoMap.put(programInfoShowName, programInfoID);
//			}
//			rs.close();
//			stmt.close();
//			connection.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	private ArrayList<Date> getPreferenceHoliday() {
//		ArrayList<Date> holidayList = new ArrayList<Date>();
//		String[] holiday = tcsession.getPreferenceService().getStringArray(4,
//				this.holiday_preference);
//		SimpleDateFormat tmpsm = new SimpleDateFormat("yyyy-MM-dd");
//		for (String string : holiday) {
//			try {
//				holidayList.add(tmpsm.parse(string));
//			} catch (ParseException e) {
//				String er = e.getLocalizedMessage();
//				pw.println("er11:"+er);
//				pw.flush();
//			}
//		}
//		return holidayList;
//
//	}
//
//	/**
//	 * 获取当月的天，以星期的格式
//	 * 
//	 * @return
//	 */
//	private List<Date> dayWithWeekend() {
//		List<Date> list = new ArrayList<Date>();
//		Calendar cal = Calendar.getInstance();
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy_MM");
//		Date date = null;
//		try {
//			date = sdf1.parse(jciYear + "_" + jciMonth);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		cal.setTime(date);
//		cal.set(Calendar.DATE, 1);
//		int month = cal.get(Calendar.MONTH);
//		while (cal.get(Calendar.MONTH) == month) {
//			list.add(cal.getTime());
//			cal.add(Calendar.DATE, 1);
//		}
//
//		return list;
//	}
////	class SortByName implements Comparator {
////        public int compare(Object o1, Object o2) {
////        	TCComponentItemRevision s1 = (TCComponentItemRevision) o1;
////        	TCComponentItemRevision s2 = (TCComponentItemRevision) o2;
////         return s1.getName().compareTo(s2.getName());
////    }
//
//	/**
//	 * 通过查询获得已发布版本：__YFJC_LCC_ExtRateRevision 条件：用户、jci6_Division、jci6_Section
//	 * 返回值：获得已发布 测试用，暂时使用的用户ujoy1
//	 * 
//	 * @return
//	 * @throws NotLoadedException
//	 * @throws TCException
//	 */
//	private ArrayList<TCComponentItemRevision> searchOperation()
//			throws NotLoadedException, TCException {
//		ArrayList<TCComponentItemRevision> itemRevisionList = new ArrayList<TCComponentItemRevision>();
//		String jci6_Division = form
//				.getPropertyDisplayableValue("jci6_Division");
//		String jci6_Section = form.getPropertyDisplayableValue("jci6_Section");
//		String userID = tcsession.getUser().getUserId();
//		// ======================================================
//		// userID = "rosedale1";
//		if (jci6_Section.trim().isEmpty()) {
//			String[] searchAttrs = { "Division",  "name", "outsourceid","Description" };
//			String[] searchValues = { jci6_Division, "Pass", userID ,"1"};
//			TCComponent[] searchItemRevisions = query(tcsession, QUERY_NAME,
//					searchAttrs, searchValues);
//			if (searchItemRevisions != null && searchItemRevisions.length > 0) {
//				for (TCComponent tcComponent : searchItemRevisions) {
//					pw.println(tcComponent.getType() + "=="
//							+ tcComponent.getObjectString());
//					pw.flush();
//					itemRevisionList.add((TCComponentItemRevision) tcComponent);
//				}
//			}
//		} else {
//			String[] searchAttrs = { "Division", "Section", "name",
//					"outsourceid" ,"Description"};
//			String[] searchValues = { jci6_Division, jci6_Section, "Pass",
//					userID ,"1"};
//			TCComponent[] searchItemRevisions = query(tcsession, QUERY_NAME,
//					searchAttrs, searchValues);
//			if (searchItemRevisions != null && searchItemRevisions.length > 0) {
//				for (TCComponent tcComponent : searchItemRevisions) {
//					pw.println(tcComponent.getType() + "=="
//							+ tcComponent.getObjectString());
//					pw.flush();
//					itemRevisionList.add((TCComponentItemRevision) tcComponent);
//				}
//			}
//		}
//		if (itemRevisionList.size() > 0) {
//			Comparator c = new Comparator<TCComponentItemRevision>() {  
//	            @Override  
//	            public int compare(TCComponentItemRevision o1, TCComponentItemRevision o2) {  
//					try {
//						String s1_name = o1.getStringProperty("object_name");
//						String s2_name = o2.getStringProperty("object_name");
//						return s1_name.compareTo(s2_name);
//					} catch (TCException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					return 0;
//	            }  
//	        };        
////			 Collections.sort(itemRevisionList, new SortByName(){
////				 
////			 });
//	        Collections.sort(itemRevisionList,c);
//		}
//		return itemRevisionList;
//	}
//
////	/**
////	 * 用于查询
////	 * 
////	 * @param session
////	 * @param query_name
////	 * @param arg1
////	 * @param arg2
////	 * @return
////	 */
////	private TCComponent[] query(TCSession session, String query_name,
////			String[] arg1, String[] arg2) {
////		TCComponentContextList imancomponentcontextlist = null;
////		TCComponent[] component = null;
////		try {
////			TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session
////					.getTypeComponent("ImanQuery");
////			TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype
////					.find(query_name);
////			TCTextService imantextservice = session.getTextService();
////			String[] queryAttribute = new String[arg1.length];
////			for (int i = 0; i < arg1.length; ++i)
////				queryAttribute[i] = imantextservice.getTextValue(arg1[i]);
////
////			String[] queryValues = new String[arg2.length];
////			for (int i = 0; i < arg2.length; ++i)
////				queryValues[i] = arg2[i];
////			imancomponentcontextlist = imancomponentquery
////					.getExecuteResultsList(queryAttribute, queryValues);
////			component = imancomponentcontextlist.toTCComponentArray();
////		} catch (Exception ex) {
////			ex.printStackTrace();
////		}
////		return component;
////	}
//	/**
//	 * 用于查询
//	 * 
//	 * @param session
//	 * @param query_name
//	 * @param arg1
//	 * @param arg2
//	 * @return
//	 */
//	private TCComponent[] query(TCSession session, String query_name,
//			String[] arg1, String[] arg2) {
//		TCComponent[] resultComponents = null;
//		try {
//			TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
//			TCComponentQuery query = (TCComponentQuery) imancomponentquerytype.find(query_name);
//			TCTextService imantextservice = session.getTextService();
//			String[] queryAttribute = new String[arg1.length];
//			for (int i = 0; i < arg1.length; ++i){
//				queryAttribute[i] = imantextservice.getTextValue(arg1[i]);
//				pw.println("queryAttribute:"+queryAttribute[i]);
//				pw.flush();
//			}
//
//			String[] queryValues = new String[arg2.length];
//			for (int i = 0; i < arg2.length; ++i){
//				queryValues[i] = arg2[i];
//				pw.println("queryValues:"+queryValues[i]);
//				pw.flush();
//			}
//			resultComponents = query.execute(queryAttribute, queryValues);
//		} catch (TCException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return resultComponents;
//	}
//
//	/**
//	 * 通过所给的JCI6ExtRateRevision，找到其最新已发布版本
//	 * 
//	 * @param searchItemRevisions
//	 * @return
//	 * @throws TCException
//	 */
//	private ArrayList<TCComponentItemRevision> getLastPassRevision(
//			ArrayList<TCComponentItemRevision> itemRevisionList)
//			throws TCException {
//		ArrayList<TCComponentItemRevision> list = new ArrayList<TCComponentItemRevision>();
//		list.addAll(itemRevisionList);
//		HashMap<String, Date> listExistMap = new HashMap<String, Date>();
//		for (TCComponentItemRevision tcComponentItemRevision : itemRevisionList) {
//			String itemUID = tcComponentItemRevision.getItem().getUid();
//			if (listExistMap.containsKey(itemUID)) {
//				Date tmpRevDate = tcComponentItemRevision
//						.getDateProperty("creation_date");
//				Date mapDate = listExistMap.get(itemUID);
//				if (tmpRevDate.after(mapDate)) {
//					boolean ifail = list.remove(tcComponentItemRevision);
//					if (ifail) {
//						pw.println("移除队列中非最新已发布版本："
//								+ tcComponentItemRevision
//										.getStringProperty("object_string")
//								+ "_"
//								+ tcComponentItemRevision
//										.getStringProperty("item_id"));
//						pw.flush();
//					} else {
//						pw.println("移除队列中非最新已发布版本："
//								+ tcComponentItemRevision
//										.getStringProperty("object_string")
//								+ "_"
//								+ tcComponentItemRevision
//										.getStringProperty("item_id")
//								+ "  失败！！！");
//						pw.flush();
//					}
//				}
//			} else {
//				Date tmpRevDate = tcComponentItemRevision
//						.getDateProperty("creation_date");
//				listExistMap.put(itemUID, tmpRevDate);
//			}
//		}
//		return list;
//	}
//
//	/**
//	 * 通过得到的最新的已发布ExtRateRevision，获得ExtTSE需要的属性 JCI6ExtRateRevision JCI6_ExtTSE
//	 * jci6_Division jci6_Division TypeReference jci6_Section jci6_Section
//	 * TypeReference item.jci6_Company jci6_Company String object_name
//	 * jci6_ExtName String jci6_Onboard jci6_Onboard Date jci6_Rate jci6_Rate
//	 * Double jci6_GID jci6_GID String
//	 * 
//	 * @param lastPassRevList
//	 * @return
//	 */
//	private ArrayList<Object[]> getExtTSEAttrsFromExtRateRevisions(
//			ArrayList<TCComponentItemRevision> lastPassRevList) {
//		extTSEAttrList = new ArrayList<Object[]>();
//		if (lastPassRevList.size() == 0) {
//			return extTSEAttrList;
//		}
//		extTSEAttrList.clear();
//		for (TCComponentItemRevision ExtRateRevision : lastPassRevList) {
//			try {
//				Object[] vv = new Object[8];
//				TCComponent jci6_Division_v = ExtRateRevision
//						.getReferenceProperty("jci6_Division");
//				TCComponent jci6_Section_v = ExtRateRevision
//						.getReferenceProperty("jci6_Section");
//				TCComponent[] jci6_Company_1 = ExtRateRevision.getItem()
//						.getReferenceListProperty("IMAN_reference");
//				String jci6_Company_v = "";
//				if (jci6_Company_1 != null && jci6_Company_1.length == 1) {
//					// jci6_Company_v =
//					// jci6_Company_1[0].getStringProperty("object_name");
//					jci6_Company_v = jci6_Company_1[0].getProperty("user_name")
//							+ "(" + jci6_Company_1[0].getProperty("user_id")
//							+ ")";
//				}
//				String jci6_ExtName_v = ExtRateRevision
//						.getStringProperty("object_name");
//				Date jci6_Onboard_v = ExtRateRevision
//						.getDateProperty("jci6_Onboard");
//				Double jci6_Rate_v = ExtRateRevision
//						.getDoubleProperty("jci6_Rate");
//				String jci6_GID_v = ExtRateRevision
//						.getStringProperty("jci6_GID");
//				vv[0] = jci6_Division_v; // TCComponent
//				vv[1] = jci6_Section_v; // TCComponent
//				vv[2] = jci6_Company_v; // String
//				vv[3] = jci6_ExtName_v; // String
//				vv[4] = jci6_Onboard_v; // Date
//				vv[5] = jci6_Rate_v; // Double
//				vv[6] = jci6_GID_v; // String
//				vv[7] = ExtRateRevision;
//				extTSEAttrList.add(vv);
//			} catch (TCException e) {
//				e.printStackTrace();
//			}
//		}
//		return extTSEAttrList;
//	}
//
//	private Boolean prefLegal() {
//		StringBuilder errorBuff = new StringBuilder();
//		for (Object[] iterable_element : extTSEAttrList) {
//			String tmpComponyName = (String) iterable_element[2];
//			String value = getModifier(tmpComponyName, "ALL");
//			if (value == null || !value.equals("ALL")) {
//				errorBuff.append("首选项YFJC_User_Rate_Mapping_ExtSupport未配置此外服公司相关信息")
//						.append(tmpComponyName).append("\n");
//			}
//		}
//		if (errorBuff.length() > 2) {
//			MessageBox
//					.post(errorBuff.toString(), "WARNING", MessageBox.WARNING);
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * 初始化表单的显示
//	 */
//	private void initForm() {
//		setLayout(new BorderLayout());
//		tabbedPanel = new JTabbedPane(JTabbedPane.TOP);
//		tabbedPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
//		add(tabbedPanel, BorderLayout.CENTER);
//
//		addFirstPanel(tabbedPanel); // 添加详细页
//		addSecondPanel(tabbedPanel); // 添加汇总页
//		boolean ifail = false;
//		String roseStr = tcsession.getRole().toDisplayString();
//		try {
//			String groupStr = tcsession.getGroup().getFullName();
//			if (roseStr.contains("dba")||roseStr.contains("DBA")||groupStr.contains("dba")||groupStr.contains("DBA")) {
//				ifail = true;
//			}
//		} catch (TCException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		if (isColorConfiguration && ifail) {
//			addThirdPanel(tabbedPanel);
//		}
//
//		tabbedPanel.addChangeListener(new ChangeListener() {
//
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				int index = tabbedPanel.getSelectedIndex();
//				if (index == 1) {
//					Vector tmpSecondColumnName = initSecondColumnName();
//					Vector tmpSecondData = initSecondData();
//					((DefaultTableModel) summaryTabel.getModel())
//							.setDataVector(tmpSecondData, tmpSecondColumnName);
//					TableColumnModel cmsum = summaryTabel.getColumnModel();
//					GroupableTableHeader headersum = (GroupableTableHeader) summaryTabel
//							.getTableHeader();
//					// ====================0-1===========================
//					ColumnGroup g_name_0_2 = new ColumnGroup(jciYear + "-"
//							+ mon[jciMonth - 1]);
//					g_name_0_2.add(cmsum.getColumn(0));
//					g_name_0_2.add(cmsum.getColumn(1));
//					g_name_0_2.add(cmsum.getColumn(2));
//					headersum.addColumnGroup(g_name_0_2);
//					// ====================2============================
//					ColumnGroup g_name_3 = new ColumnGroup(" ");
//					g_name_3.add(cmsum.getColumn(3));
//					headersum.addColumnGroup(g_name_3);
//					// =====================3-8=========================
//					ColumnGroup g_name_4_9 = new ColumnGroup("Summary");
//					g_name_4_9.add(cmsum.getColumn(4));
//					for (int i = 4; i < 10; i++) {
//						g_name_4_9.add(cmsum.getColumn(i));
//					}
//					headersum.addColumnGroup(g_name_4_9);
//					// =====================9~=========================
////					ColumnGroup g_name_9 = new ColumnGroup(" ");
//					String[] week = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri",
//							"Sat" };
//					SimpleDateFormat sdfE = new SimpleDateFormat("E",
//							Locale.ENGLISH);
//					for (int i = 0; i < week.length; i++) {
//						Date tmpD = tcweeks.get(i);
//						week[i] = sdfE.format(tmpD);
//					}
//					int weekStart = 0;
//					for (int i = 10; i < tmpSecondColumnName.size(); i++) {
//						if (weekStart == 7) {
//							weekStart = 0;
//						}
//						String tmpWeek = week[weekStart];
//						weekStart++;
//						ColumnGroup g_name_tmp = new ColumnGroup(tmpWeek);
//						g_name_tmp.add(cmsum.getColumn(i));
//						headersum.addColumnGroup(g_name_tmp);
//					}
//					summaryTabel.setRowHeight(20);
//					// myCellRenderer2
//					myCellRenderer2 summaryCellRenderer = new myCellRenderer2();
//					summaryCellRenderer.setHorizontalAlignment(JLabel.CENTER);
//					Integer[] colSize = { 42, 148, 100, 178, 100, 75, 36, 36,
//							36, 36, 33 };
//					for (int i = 0; i < summaryTabel.getColumnCount(); i++) {
//						summaryTabel.getColumnModel().getColumn(i)
//								.setCellRenderer(summaryCellRenderer);
//						if (i < 10) {
//							summaryTabel.getColumnModel().getColumn(i)
//									.setPreferredWidth(colSize[i]);
//						} else {
//							summaryTabel.getColumnModel().getColumn(i)
//									.setPreferredWidth(colSize[10]);
//						}
//					}
//				}
//				/**
//				 * 
//				 // int colCount2 = summaryTabel.getColumnCount(); // for (int
//				 * i = 0; i < colCount2; i++) { //
//				 * pw.println("summaryTabel,col"
//				 * +i+":"+summaryTabel.getColumnModel
//				 * ().getColumn(i).getPreferredWidth()); // } Vector
//				 * tmpSecondColumnName = initSecondColumnName(); Vector
//				 * tmpSecondData = initSecondData();
//				 * ((DefaultTableModel)summaryTabel
//				 * .getModel()).setDataVector(tmpSecondData,
//				 * tmpSecondColumnName); TableColumnModel cmsum =
//				 * summaryTabel.getColumnModel(); GroupableTableHeader headersum
//				 * = (GroupableTableHeader)summaryTabel.getTableHeader();
//				 * //====================0-1===========================
//				 * ColumnGroup g_name_0_2 = new
//				 * ColumnGroup(jciYear+"-"+mon[jciMonth-1]);
//				 * g_name_0_2.add(cmsum.getColumn(0));
//				 * g_name_0_2.add(cmsum.getColumn(1));
//				 * g_name_0_2.add(cmsum.getColumn(2));
//				 * headersum.addColumnGroup(g_name_0_2);
//				 * //====================2============================
//				 * ColumnGroup g_name_3 = new ColumnGroup(" ");
//				 * g_name_3.add(cmsum.getColumn(3));
//				 * headersum.addColumnGroup(g_name_3);
//				 * //=====================3-8=========================
//				 * ColumnGroup g_name_4_9 = new ColumnGroup("Summary");
//				 * g_name_4_9.add(cmsum.getColumn(4)); for (int i = 4; i < 10;
//				 * i++) { g_name_4_9.add(cmsum.getColumn(i)); }
//				 * headersum.addColumnGroup(g_name_4_9);
//				 * //=====================9~=========================
//				 * ColumnGroup g_name_9 = new ColumnGroup(" "); String[] week =
//				 * {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"}; SimpleDateFormat
//				 * sdfE = new SimpleDateFormat("E", Locale.ENGLISH); for (int i
//				 * = 0; i < week.length; i++) { Date tmpD = tcweeks.get(i);
//				 * week[i] = sdfE.format(tmpD); } int weekStart = 0; for (int i
//				 * = 10; i < tmpSecondColumnName.size(); i++) { if (weekStart ==
//				 * 7) { weekStart = 0; } String tmpWeek = week[weekStart];
//				 * weekStart++; ColumnGroup g_name_tmp = new
//				 * ColumnGroup(tmpWeek); g_name_tmp.add(cmsum.getColumn(i));
//				 * headersum.addColumnGroup(g_name_tmp); }
//				 * summaryTabel.setRowHeight(20); //myCellRenderer2
//				 * myCellRenderer2 summaryCellRenderer = new myCellRenderer2();
//				 * summaryCellRenderer.setHorizontalAlignment(JLabel.CENTER);
//				 * Integer[] colSize = {42,148,100,178,94,75,36,36,36,36,33};
//				 * for (int i = 0; i < summaryTabel.getColumnCount(); i++) {
//				 * summaryTabel.getColumnModel().getColumn(i).setCellRenderer(
//				 * summaryCellRenderer); if (i < 10) {
//				 * summaryTabel.getColumnModel
//				 * ().getColumn(i).setPreferredWidth(colSize[i]); }else{
//				 * summaryTabel
//				 * .getColumnModel().getColumn(i).setPreferredWidth(colSize
//				 * [10]); } }
//				 */
//
//			}
//		});
//
//		// JPanel buttonPanel = new JPanel();
//		// add(buttonPanel,BorderLayout.SOUTH);
//		// JButton SaveTemplatebutton = new JButton("SaveTemplate");
//		// JButton submitbutton = new JButton("Submit");
//		// submitbutton.addActionListener(new ActionListener() {
//		//
//		// @Override
//		// public void actionPerformed(ActionEvent e) {
//		//
//		//
//		// }
//		// });
//		// buttonPanel.add(SaveTemplatebutton);
//		// buttonPanel.add(submitbutton);
//		// new NewProcessCommand(AIFUtility.getActiveDesktop(),
//		// AIFUtility.getCurrentApplication(),new
//		// TCComponent[]{(TCComponent)form.getPrimary()[0].getComponent()});
//
//		this.setBackground(Color.white);
//		this.setPreferredSize(new Dimension(1400, 160));
//	}
//
//	/**
//	 * 为JTabbedPane添加第一页
//	 * 
//	 * @param tabbedPanel
//	 */
//	private void addFirstPanel(JTabbedPane tabbedPanel) {
//		JPanel firstPanel = new JPanel();
//		firstPanel.setLayout(new BorderLayout(0, 0));
//		tabbedPanel.addTab("Detail", null, firstPanel, null);
//
//		// =====================paneltop==================================
//		JPanel paneltop = new JPanel();
//		firstPanel.add(paneltop, BorderLayout.NORTH);
//		paneltop.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
//
//		table0 = new JTable();
////		table0.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		table0.setBorder(new LineBorder(new Color(0, 0, 0)));
//
//		table0.setModel(new DefaultTableModel(new Object[][] {
//				{ "", "Cost", "N", "O", "W", "H" },
//				{ "Approved Cost", "", "", "", "", "" },
//				{ "To be Approved", "", "", "", "", "" }, }, new String[] { "",
//				"New column", "N", "O", "W", "H" }) {
//			@Override
//			public boolean isCellEditable(int row, int column) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
////		table0.getColumnModel().getColumn(0).setPreferredWidth(140);
////		table0.getColumnModel().getColumn(1).setPreferredWidth(70);
////		table0.getColumnModel().getColumn(2).setPreferredWidth(80);
////		table0.getColumnModel().getColumn(3).setPreferredWidth(80);
////		table0.getColumnModel().getColumn(4).setPreferredWidth(80);
////		table0.getColumnModel().getColumn(5).setPreferredWidth(80);
//
//		
//		
//		DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
//			@Override
//			public void setHorizontalAlignment(int alignment) {
//				super.setHorizontalAlignment(JLabel.CENTER);
//			}
//
//			@Override
//			public Component getTableCellRendererComponent(JTable table,
//					Object value, boolean isSelected, boolean hasFocus,
//					int row, int column) {
//				Component com = super.getTableCellRendererComponent(table,
//						value, isSelected, hasFocus, row, column);
//				if (!isSelected) {
//					if (row == 0 && column == 2) {
//						com.setBackground(normalColor);
//					}else if (row == 0 && column == 3) {
//						com.setBackground(overtimeColor);
//					}else if (row == 0 && column == 4) {
//						com.setBackground(weekendColor);
//					}else if (row == 0 && column == 5) {
//						com.setBackground(holidayColor);
//					}else if (row == 1 && column == 0) {
//						com.setBackground(approvedCostColor);
//					}else if (row == 2 && column == 0) {
//						com.setBackground(toBeApprovedColor);
//					}else{
//						com.setBackground(Color.WHITE);
//					}
//				}
//				
//				return com;
//			}
//		};
//		table0.getColumnModel().getColumn(0).setCellRenderer(r);
//		table0.getColumnModel().getColumn(1).setCellRenderer(r);
//		table0.getColumnModel().getColumn(2).setCellRenderer(r);
//		table0.getColumnModel().getColumn(3).setCellRenderer(r);
//		table0.getColumnModel().getColumn(4).setCellRenderer(r);
//		table0.getColumnModel().getColumn(5).setCellRenderer(r);
//		paneltop.add(table0);
//		JPanel billTypePanel = new JPanel();
//		billTypePanel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
//		paneltop.add(billTypePanel);
//
//		billTypePanel.add(new JLabel("Bill Type"));
//		billTypePanel.add(new JLabel("  "));
//		// Normal的输入
//		{
//			JPanel timePanel = new JPanel();
//			billTypePanel.add(timePanel);
//			timePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
//			timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
//			timePanel.add(new JLabel("Normal"));
//			JPanel panel_2 = new JPanel();
//			timePanel.add(panel_2);
//			{
//				JLabel lblNewLabel = new JLabel("  N  ");
//				lblNewLabel.setBackground(normalColor);
//				lblNewLabel.setOpaque(true);
//				panel_2.add(lblNewLabel);
//			}
//			{
//				normalTField = new JTextField();
//				panel_2.add(normalTField);
//				normalTField.setColumns(3);
//				JCITableUtil util = new JCITableUtil();
//				LimitedDocument limitedDocument = util.getLimitedDocument(4,
//						"-.0123456789");
//				normalTField.setDocument(limitedDocument);
//			}
//		}
//		// Overtime的输入
//		{
//			JPanel timePanel = new JPanel();
//			billTypePanel.add(timePanel);
//			timePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
//			timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
//			timePanel.add(new JLabel("Overtime"));
//			JPanel panel_2 = new JPanel();
//			timePanel.add(panel_2);
//			{
//				JLabel lblNewLabel = new JLabel("  O  ");
//				lblNewLabel.setBackground(overtimeColor);
//				lblNewLabel.setOpaque(true);
//				panel_2.add(lblNewLabel);
//			}
//			{
//				overTimeTField = new JTextField();
//				panel_2.add(overTimeTField);
//				overTimeTField.setColumns(3);
//				JCITableUtil util = new JCITableUtil();
//				LimitedDocument limitedDocument = util.getLimitedDocument(4,
//						"-.0123456789");
//				overTimeTField.setDocument(limitedDocument);
//			}
//		}
//		// Weekend的输入
//		{
//			JPanel timePanel = new JPanel();
//			billTypePanel.add(timePanel);
//			timePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
//			timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
//			timePanel.add(new JLabel("Weekend"));
//			JPanel panel_2 = new JPanel();
//			timePanel.add(panel_2);
//			{
//				JLabel lblNewLabel = new JLabel("  W  ");
//				lblNewLabel.setBackground(weekendColor);
//				lblNewLabel.setOpaque(true);
//				panel_2.add(lblNewLabel);
//			}
//			{
//				weekendTField = new JTextField();
//				panel_2.add(weekendTField);
//				weekendTField.setColumns(3);
//				JCITableUtil util = new JCITableUtil();
//				LimitedDocument limitedDocument = util.getLimitedDocument(4,
//						"-.0123456789");
//				weekendTField.setDocument(limitedDocument);
//			}
//		}
//		// Holiday的输入
//		{
//			JPanel timePanel = new JPanel();
//			billTypePanel.add(timePanel);
//			timePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
//			timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
//			timePanel.add(new JLabel("Holiday"));
//			JPanel panel_2 = new JPanel();
//			timePanel.add(panel_2);
//			{
//				JLabel lblNewLabel = new JLabel("  H  ");
//				lblNewLabel.setBackground(holidayColor);
//				lblNewLabel.setOpaque(true);
//				panel_2.add(lblNewLabel);
//			}
//			{
//				holidayTField = new JTextField();
//				panel_2.add(holidayTField);
//				holidayTField.setColumns(3);
//				JCITableUtil util = new JCITableUtil();
//				LimitedDocument limitedDocument = util.getLimitedDocument(4,
//						"-.0123456789");
//				holidayTField.setDocument(limitedDocument);
//			}
//		}
//
//		billTypePanel.add(new JLabel("   "));
//		JButton assignButton = new JButton("Assign");
//		billTypePanel.add(assignButton);
//		assignButton.setFont(new Font("微软雅黑", Font.BOLD, 12));
//		assignButton.setBackground(assignButtonColor);
//		assignButton.setOpaque(true);
//		assignButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				assignButtonAction();
//			}
//		});
//		JButton assignCleanButton = new JButton("Clear");
//		billTypePanel.add(assignCleanButton);
//		assignCleanButton.setFont(new Font("微软雅黑", Font.BOLD, 12));
//		assignCleanButton.setBackground(clearButtonColor);
//		assignCleanButton.setOpaque(true);
//		assignCleanButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				assignCleanButtonAction();
//				// int colCount1 = fixTable.getColumnCount();
//				// int colCount2 = moveTable.getColumnCount();
//				// pw.println("fixTable,col0:"+fixTable.getColumnModel().getColumn(0).getPreferredWidth());
//				// pw.println("fixTable,col0:"+fixTable.getColumnModel().getColumn(1).getPreferredWidth());
//				// for (int i = 0; i < colCount2; i++) {
//				// pw.println("moveTable,col"+i+":"+moveTable.getColumnModel().getColumn(i).getPreferredWidth());
//				// }
//			}
//		});
//
//		// =====================leftPanel==================================
//		ViewEditHelper viewHelp = new ViewEditHelper(tcsession);
//		ischeckoutAble = viewHelp.isCheckoutable(form);
//		JPanel leftPanel = new JPanel();
//		firstPanel.add(leftPanel, BorderLayout.WEST);
//		GridBagLayout gbl_leftPanel = new GridBagLayout();
//		leftPanel.setLayout(gbl_leftPanel);
//		{
//			JButton addButton = new JButton("Add");
//			GridBagConstraints gbc_addButton = new GridBagConstraints();
//			gbc_addButton.fill = GridBagConstraints.HORIZONTAL;
//			gbc_addButton.gridx = 0;
//			gbc_addButton.gridy = 0;
//			leftPanel.add(addButton, gbc_addButton);
////			String roleName = "";
//			try {
//				roleName = tcsession.getRole().getStringProperty("role_name");
//			} catch (TCException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			if (roleName.contains("ExtSupporter")||roleName.contains("工程师助理")) {
//				addButton.setBackground(addButtonColor);
//				addButton.setForeground(Color.BLACK);
//				addButton.addActionListener(new ActionListener() {
//					@Override
//					public void actionPerformed(ActionEvent e) {
//						addButtonAction();
//					}
//				});
//			}else{
//				addButton.setBackground(Color.GRAY);
//				addButton.setForeground(Color.LIGHT_GRAY);
//			}
//			addButton.setOpaque(true);
//		}
//		{
//			JButton minusButton = new JButton("Delete");
//			
//			GridBagConstraints gbc_minusButton = new GridBagConstraints();
//			gbc_minusButton.fill = GridBagConstraints.HORIZONTAL;
//			gbc_minusButton.gridx = 0;
//			gbc_minusButton.gridy = 1;
//			leftPanel.add(minusButton, gbc_minusButton);
//			
//			if (ischeckoutAble) {
//				minusButton.setBackground(deleteButtonColor);
//				minusButton.setForeground(Color.BLACK);
//				minusButton.addActionListener(new ActionListener() {
//					@Override
//					public void actionPerformed(ActionEvent e) {
//						minusButtonAction();
//					}
//				});
//			}else{
//				minusButton.setBackground(Color.GRAY);
//				minusButton.setForeground(Color.LIGHT_GRAY);
//			}
//			minusButton.setOpaque(true);
//		}
//		
//		{
//			
//			reduceButton = new JButton("Reduce");
//			reduceButton.setToolTipText("需要选中减免人员");
//			
//			GridBagConstraints gbc_reduceButton = new GridBagConstraints();
//			gbc_reduceButton.gridx = 0;
//			gbc_reduceButton.gridy = 2;
//			leftPanel.add(reduceButton, gbc_reduceButton);
//			
//			if (ischeckoutAble) {
//				reduceButton.setBackground(reduceButtonColor);
//				reduceButton.setForeground(Color.BLACK);
//				reduceButton.addActionListener(new ActionListener() {
//					@Override
//					public void actionPerformed(ActionEvent e) {
//						reduceButtonAction();
//					}
//				});
//			}else{
//				reduceButton.setBackground(Color.GRAY);
//				reduceButton.setForeground(Color.LIGHT_GRAY);
//			}
//			reduceButton.setOpaque(true);
//		}
//
//		// =====================panelCenter==================================
//		JPanel panelCenter = new JPanel();
//		firstPanel.add(panelCenter, BorderLayout.CENTER);
//		GridBagLayout gbl_panelCenter = new GridBagLayout();
//		gbl_panelCenter.rowWeights = new double[] { 1.0 };
//		gbl_panelCenter.columnWeights = new double[] { 1.0 };
//		panelCenter.setLayout(gbl_panelCenter);
//
//		String[] week = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
//		SimpleDateFormat sdfE = new SimpleDateFormat("E", Locale.ENGLISH);
//		for (int i = 0; i < week.length; i++) {
//			Date tmpD = tcweeks.get(i);
//			week[i] = sdfE.format(tmpD);
//		}
//		int weekStart = 0;
//		// ****************************table数据**************************************
//		dmMove = new DefaultTableModel(data2, moveColumnName) {
//			/**
//			 * 设置是否可编辑：（起始列：0） 一般只有第1列可编辑
//			 * 但是如果form存在对应的明细ExtTse，并且明细ExtTse已经发布，第1列也不能编辑
//			 */
//			public boolean isCellEditable(int row, int column) {
//				Boolean canEdit = true;
//				if (column == 1 &&data.size() > row) {
//					Set<String> passKey = passMap_move.keySet();
//					for (String str : passKey) {
//						if (str.contains(",")) {
//							String str_splits = str.split(",")[0];
//							
//							if (str_splits.trim().equals(Integer.toString(row))) {
//								boolean ifial = passMap_move.get(str);
//								if (ifial) {
//									canEdit = false;
//									break;
//								}
//							}
//						}
//					}
//				}else if (column == 1 &&data.size() <= row) {
//					canEdit = true;
//				}else{
//					canEdit = false;
//				}
//				
//				if (column == 1 && !ischeckoutAble) {
//					canEdit = false;
//				}
//				
//				if (roleName.contains("ExtSupporter")||roleName.contains("工程师助理")) {
//					
//				}else{
//					canEdit = false;
//				}
////				if (column == 1) {
////					if (data.size() <= row) {
////						canEdit = true;
////					} else if (((Vector) data.elementAt(row)).elementAt(0) != null
////							&& JCITableUtil.isPass((TCComponent) ((Vector) data
////									.elementAt(row)).elementAt(0))) {
////						canEdit = false;
////					} else {
////						canEdit = true;
////					}
////				}
//				return canEdit;
//			};
//		};
//
//		dmFix = new DefaultTableModel(data1, fixColumnName) {
//			/**
//			 * 设置是否可编辑：（起始列：0）（列数：2） 一般只有第0列可编辑 但是如果form存在对应的明细ExtTse，第0列也不可编辑
//			 */
//			public boolean isCellEditable(int row, int column) {
//				Boolean canEdit = true;
//
//				if (column == 1) {
//					canEdit = false;
//				} else if (column == 0 && data.size() > row) {
//					canEdit = false;
//				}
//				if (extTSEAttrList == null || extTSEAttrList.size() == 0) {
//					canEdit = false;
//				}
//				
//				
//				return canEdit;
//			};
//
//		};
//
//		// 添加监听
//		dmFix.addTableModelListener(new TableModelListener() {
//			@Override
//			public void tableChanged(TableModelEvent e) {
//				/**
//				 * 值出现变化 第0列修改 0：下拉修改 1.根据0修改
//				 */
//				if (e.getType() == e.DELETE) {
//					pw.println("dm移除行");
//					pw.flush();
//					return;
//				}
//				int row = e.getFirstRow();
//				int column = e.getColumn();
//				// boolean ifail1 = fixTable.getCellEditor(row,
//				// column)==null?true:fixTable.getCellEditor(row,
//				// column).stopCellEditing();
//				// boolean ifail2 =
//				// moveTable.getCellEditor()==null?true:fixTable.getCellEditor().stopCellEditing();
//				if (dmMove == null || fixTable == null || moveTable == null
//						|| dmMove.getDataVector().isEmpty()) {
//					return;
//				}
////				col1Value
//				if (column == 0) {
//					if (moveTable.getCellEditor() != null) {
//						moveTable.getCellEditor().stopCellEditing();
//					}
//					String nameValue = (String) dmFix.getValueAt(row, column);
//					if (!nameValue.trim().isEmpty()) {
//						//判断有工时，删除
//						if (e.getType() == e.UPDATE && col1Values.get(row) != null && !nameValue.equals(col1Values.get(row))) {
//							for (int i = 7; i < dmMove.getColumnCount(); i++) {
//								dmMove.setValueAt("", row, i);
//								cellTypeMap_move = removeCellTypeOvertimeRow(cellTypeMap_move, row);
//							}
//						}
//						/**
//						 * extTSEAttrList[i] = Object[] vv vv[0] =
//						 * jci6_Division_v; //TCComponent vv[1] =
//						 * jci6_Section_v; //TCComponent vv[2] = jci6_Company_v;
//						 * //String vv[3] = jci6_ExtName_v; //String vv[4] =
//						 * jci6_Onboard_v; //Date vv[5] = jci6_Rate_v; //Double
//						 * vv[6] = jci6_GID_v; //String
//						 */
//						for (Object[] obj : extTSEAttrList) {
//							if (obj[3].equals(nameValue.trim())) {
//								// dmFix，第0列，jci6_Rate_v
//								dmFix.setValueAt(obj[6], row, 1);
//								// dmMove，第0列，jci6_Company_v
//								String tmpCompany = obj[2].toString();
//								if (tmpCompany != null) {
//									dmMove.setValueAt(tmpCompany, row, 0);
//								}
//								Date tmpOnboard = (Date) obj[4];
//								SimpleDateFormat smf = new SimpleDateFormat(
//										"yyyy/MM/dd");
//								dmMove.setValueAt(smf.format(tmpOnboard), row,
//										2);
//								break;
//							}
//						}
//						
//					}
//					col1Values.put(row, nameValue);
//				}
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						if (fixTable != null && moveTable != null) {
//							try {
//								// fixTable.updateUI();
//								moveTable.updateUI();
//							} catch (NullPointerException e2) {
//								pw.println("疑似刷新失败，不影响正常工作");
//								pw.flush();
//							}
//						}
//					}
//				}).start();
//				// allCount();
//			}
//
//		});
//		// 添加监听
//		dmMove.addTableModelListener(new TableModelListener() {
//			@Override
//			public void tableChanged(TableModelEvent e) {
//				/**
//				 * 起始：第0列 监听第8列开始的值的变化
//				 */
//				if (e.getType() == e.DELETE) {
//					pw.println("dmMove移除行");
//					pw.flush();
//					allCount();
//					return;
//				}
//				int row = e.getFirstRow();
//				int column = e.getColumn();
//				if (column < 3) {
//					return;
//				}
//				if (dmFix == null || fixTable == null || moveTable == null
//						|| dmFix.getDataVector().isEmpty()) {
//					return;
//				}
//				if (column == 3) {
//					// Normal
//					return;
//				} else if (column == 4) {
//					// Overtime
//					return;
//				} else if (column == 5) {
//					// Weekend
//					return;
//				} else if (column == 6) {
//					// Holiday
//					return;
//				} else if (column > 6) {
//					/**
//					 * 以颜色判断对象类型
//					 * 
//					 */
//					Double NValue = 0.0d;
//					Double OValue = 0.0d;
//					Double WValue = 0.0d;
//					Double HValue = 0.0d;
//					String tmpOnBoardDate = dmMove.getValueAt(row, 2)
//							.toString();
//					if (tmpOnBoardDate.trim().length() > 2) {
//						for (int i = 0; i < tcweeks_iswork.size(); i++) {
//							String tmpV = dmMove.getValueAt(row, i + 7)
//									.toString();
//							if (tmpV == null || tmpV.isEmpty()) {
//								continue;
//							}
//							// Color backColor = rColor.get(row+","+(i+8));
//							// if (backColor == null) {
//							// pw.println("无颜色："+row+","+(i+8));
//							// continue;
//							// }
//							// if (backColor.equals(normalColorSelected) ||
//							// backColor.equals(normalColorNoSelectedIsNull) ||
//							// backColor.equals(normalColorNoSelectedIsZero) ||
//							// backColor.equals(normalColorNoSelectedIsEight)||
//							// backColor.equals(normalColorNoSelectedIsNotEight))
//							// {
//							// NValue += Double.parseDouble(tmpV);
//							// }else if (backColor.equals(overtimeColorSelected)
//							// || backColor.equals(overtimeColorNoSelected)) {
//							// OValue += Double.parseDouble(tmpV);
//							// }else if (backColor.equals(weekendColorSelected)
//							// || backColor.equals(weekendColorNoSelected)) {
//							// WValue += Double.parseDouble(tmpV);
//							// }else if (backColor.equals(holidayColorSelected)
//							// || backColor.equals(holidayColorNoSelected)) {
//							// HValue += Double.parseDouble(tmpV);
//							// }
//							String type = cellTypeMap_move.get(row + ","
//									+ (i + 7));
//							if (type == null || type.isEmpty()) {
//								pw
//										.println("无类型：" + row + "," + (i + 7));
//								pw.flush();
//								continue;
//							}
//							if (type.equals(normalType)) {
//								NValue += Double.parseDouble(tmpV);
//							} else if (type.equals(overtimeType)) {
//								OValue += Double.parseDouble(tmpV);
//							} else if (type.equals(weekendType)) {
//								WValue += Double.parseDouble(tmpV);
//							} else if (type.equals(holidayType)) {
//								HValue += Double.parseDouble(tmpV);
//							}
//						}
//					}
//					dmMove.setValueAt(NValue, row, 3);
//					dmMove.setValueAt(OValue, row, 4);
//					dmMove.setValueAt(WValue, row, 5);
//					dmMove.setValueAt(HValue, row, 6);
//					pw.println("ALLCOUNT....");
//					pw.flush();
//					allCount();
//				}
//			}
//
//		});
//
//		// ************************固定的table**********************************
//		fixTable = new JTable(dmFix) {
//			protected JTableHeader createDefaultTableHeader() {
//				GroupableTableHeader th = new GroupableTableHeader(columnModel);
//				th.setReorderingAllowed(false);
//				return th;
//			}
//		};
//
//		TableColumnModel cmfix = fixTable.getColumnModel();
//		ColumnGroup g_name_fix = new ColumnGroup(jciYear + "-"
//				+ mon[jciMonth - 1]);
//		g_name_fix.add(cmfix.getColumn(0));
//		g_name_fix.add(cmfix.getColumn(1));
//		GroupableTableHeader headerFix = (GroupableTableHeader) fixTable
//				.getTableHeader();
//		headerFix.addColumnGroup(g_name_fix);
//
//		Vector<String> usernamelist = new Vector<String>();
//		/**
//		 * jci6_Division jci6_Section jci6_Company jci6_ExtName jci6_Onboard
//		 * jci6_Rate jci6_GID
//		 */
////		usernamelist.add("");
//		for (Object[] rev : extTSEAttrList) {
//			usernamelist.add(rev[3].toString());
//		}
//		JComboBox comboBox = new JAutoCompleteComboBox(usernamelist);
//		comboBox.setSelectedIndex(-1);
//		DefaultCellEditor editor0 = new DefaultCellEditor(comboBox);
//		TableColumnModel tcm = fixTable.getColumnModel();
//		tcm.getColumn(0).setCellEditor(editor0);
//		myCellRenderer1 r1 = new myCellRenderer1();
//		myCellRenderer1 r2 = new myCellRenderer1();
////		r1.isFix = true;
////		r.setHorizontalAlignment(JLabel.CENTER);
//		tcm.getColumn(0).setCellRenderer(r1);
//		tcm.getColumn(1).setCellRenderer(r2);
//		// **************************移动的table********************************
//		moveTable = new JTable(dmMove) {
//			ListSelectionModel selectionModel = new DefaultListSelectionModel();
//
//			protected JTableHeader createDefaultTableHeader() {
//				return new GroupableTableHeader(columnModel);
//			}
//
//			@Override
//			public boolean isCellSelected(int row, int column) {
//				return selectionModel.isSelectedIndex(getIndex(row, column));
//			}
//
//			private int getIndex(int rowIndex, int colIndex) {
//				return getColumnCount() * rowIndex + colIndex;
//			}
//
//			@Override
//			public void changeSelection(int rowIndex, int columnIndex,
//					boolean toggle, boolean extend) {
//				boolean selected = isCellSelected(rowIndex, columnIndex);
//				changeSelectionModel(selectionModel, rowIndex, columnIndex,
//						toggle, extend, selected);
//				// Scroll after changing the selection as blit scrolling is
//				// immediate,
//				// so that if we cause the repaint after the scroll we end up
//				// painting
//				// everything!
//				if (getAutoscrolls()) {
//					Rectangle cellRect = getCellRect(rowIndex, columnIndex,
//							false);
//					if (cellRect != null) {
//						scrollRectToVisible(cellRect);
//					}
//				}
//			}
//
//			private void changeSelectionModel(ListSelectionModel sm,
//					int rowIndex, int colIndex, boolean toggle, boolean extend,
//					boolean selected) {
//				int index = getIndex(rowIndex, colIndex);
//
//				// use customed selection model to save selection status.
//				// at the same time, update the original selection model JTable
//				// used.
//				ListSelectionModel rsm = this.getSelectionModel();
//				ListSelectionModel csm = this.getColumnModel()
//						.getSelectionModel();
//				if (extend) {
//					if (toggle) {
//						sm.setAnchorSelectionIndex(index);
//						rsm.setAnchorSelectionIndex(rowIndex);
//						csm.setAnchorSelectionIndex(colIndex);
//					} else {
//						sm.setLeadSelectionIndex(index);
//						rsm.setLeadSelectionIndex(rowIndex);
//						csm.setLeadSelectionIndex(colIndex);
//					}
//				} else {
//					if (toggle) {
//						if (selected) {
//							sm.removeSelectionInterval(index, index);
//							rsm.removeSelectionInterval(rowIndex, rowIndex);
//							csm.removeSelectionInterval(colIndex, colIndex);
//						} else {
//							sm.addSelectionInterval(index, index);
//							rsm.addSelectionInterval(rowIndex, rowIndex);
//							csm.addSelectionInterval(colIndex, colIndex);
//						}
//					} else {
//						sm.setSelectionInterval(index, index);
//						rsm.setSelectionInterval(rowIndex, rowIndex);
//						csm.setSelectionInterval(colIndex, colIndex);
//						sm.setAnchorSelectionIndex(index);
//						rsm.setAnchorSelectionIndex(rowIndex);
//						csm.setAnchorSelectionIndex(colIndex);
//					}
//				}
//				repaint();
//			}
//		};
//		// 设置行高和列宽
//		fixTable.setRowHeight(20);
//		moveTable.setRowHeight(20);
//		Integer[] moveTableWidth = { 178, 147, 94, 36, 36, 36, 36, 33 };
//		fixTable.getColumnModel().getColumn(0).setPreferredWidth(148);
//		fixTable.getColumnModel().getColumn(1).setPreferredWidth(100);
//		for (int j = 0; j < moveTable.getColumnCount(); j++) {
//			int cell8 = moveTableWidth[7];
//			if (j < 7) {
//				moveTable.getColumnModel().getColumn(j)
//						.setPreferredWidth(moveTableWidth[j]);
//			} else {
//				moveTable.getColumnModel().getColumn(j)
//						.setPreferredWidth(cell8);
//			}
//		}
//
//		TableColumnModel cmmove = moveTable.getColumnModel();
//		GroupableTableHeader headermove = (GroupableTableHeader) moveTable
//				.getTableHeader();
//		ColumnGroup g_name_0 = new ColumnGroup(" ");
//		g_name_0.add(cmmove.getColumn(0));
//		headermove.addColumnGroup(g_name_0);
//		ColumnGroup g_name_1 = new ColumnGroup(" ");
//		g_name_1.add(cmmove.getColumn(1));
//		headermove.addColumnGroup(g_name_1);
//		ColumnGroup g_name_2_6 = new ColumnGroup("Summary");
//		for (int i = 2; i < 7; i++) {
//			g_name_2_6.add(cmmove.getColumn(i));
//		}
//		headermove.addColumnGroup(g_name_2_6);
//
//		for (int i = 7; i < moveColumnName.size(); i++) {
//			if (weekStart == 7) {
//				weekStart = 0;
//			}
//			String tmpWeek = week[weekStart];
//			weekStart++;
//			ColumnGroup g_name_tmp = new ColumnGroup(tmpWeek);
//			g_name_tmp.add(cmmove.getColumn(i));
//			headermove.addColumnGroup(g_name_tmp);
//		}
//
//		for (int i = 0; i < moveColumnName.size(); i++) {
//			if (i < 7) {
//				if (i == 1) {
//					// JComboBox combox = new
//					// JAutoCompleteComboBox(programInfoList);
//					// combox.setSelectedItem("");
//					// iComboBox combox = new iComboBox(programInfoList);
//					// combox.setAutoCompleteSuggestive(true);
//					// DefaultCellEditor cellEditor = new
//					// DefaultCellEditor(combox);
//					// DefaultCellEditor cellEditor = new DefaultCellEditor();
//					jciTableCelledit cellEditor = new jciTableCelledit(
//							programInfoList,ischeckoutAble);
//					cmmove.getColumn(i).setCellEditor(cellEditor);
//					cmmove.getColumn(i).setCellRenderer(new DefaultTableCellRenderer(){
//						@Override
//						public Component getTableCellRendererComponent(
//								JTable table, Object value, boolean isSelected,
//								boolean hasFocus, int row, int column) {
//							Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
//									row, column);
//							Color currentColor = programColor.get(row);
//							if (currentColor != null) {
//								com.setBackground(currentColor);
//							}
//							//去掉公司的颜色
////							if (com instanceof iComboBox) {
////								if (isSelected) {
////									((iComboBox)com).getTextField().setForeground(Color.GRAY);
////								}else{
////									((iComboBox)com).getTextField().setForeground(Color.BLACK);
////								}
////								
////							}else{
////								
////								if (isSelected) {
////									com.setForeground(Color.GRAY);
////								}else{
////									com.setForeground(Color.BLACK);
////								}
////							}
//							com.setForeground(Color.BLACK);
//							return com;
//						}
//					});
//				} else {
//					myCellRenderer1 cellRender = new myCellRenderer1();
//					cellRender.setHorizontalAlignment(JLabel.CENTER);
//					cmmove.getColumn(i).setCellRenderer(cellRender);
//				}
//			} else {
//				Integer intType = tcweeks_iswork.get(i - 7);
//				if (intType == 1) {
//					cellTypeMap_Column.put(i, normalType);
//				} else if (intType == 2) {
//					cellTypeMap_Column.put(i, weekendType);
//				} else if (intType == 3) {
//					cellTypeMap_Column.put(i, holidayType);
//				}
//				myCellRenderer1 cellRender = new myCellRenderer1();
//				cellRender.setHorizontalAlignment(JLabel.CENTER);
//				cmmove.getColumn(i).setCellRenderer(cellRender);
//			}
//		}
//
//		// fixTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		fixTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		moveTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		moveTable
//				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//		moveTable.setCellSelectionEnabled(true);
//
//		scroll = new JScrollPane(moveTable);
//		JViewport viewport = new JViewport();
//		viewport.setView(fixTable);
//		viewport.setPreferredSize(fixTable.getPreferredSize());
//		scroll.setRowHeaderView(viewport);
//		scroll.setCorner(JScrollPane.UPPER_LEFT_CORNER,
//				fixTable.getTableHeader());
//
//		GridBagConstraints gbc_scroll = new GridBagConstraints();
//		gbc_scroll.fill = GridBagConstraints.BOTH;
//		gbc_scroll.gridy = 0;
//		gbc_scroll.gridx = 0;
//		panelCenter.add(scroll, gbc_scroll);
//
//		// //////////////////////////////////////////
//
//	}
//
//	/**
//	 * 点击JButton("Assign")后的事件： 说明：如果 1.读取表中的选中项 2.判断 选中项 3.填值
//	 */
//	private void assignButtonAction() {
//		int tSize = 0;
//		String nStr = normalTField.getText();
//		String oStr = overTimeTField.getText();
//		String wStr = weekendTField.getText();
//		String hStr = holidayTField.getText();
//		// nStr = nStr.equals("0")||nStr.equals("0.0")?"":nStr;
//		// oStr = oStr.equals("0")||oStr.equals("0.0")?"":oStr;
//		// wStr = wStr.equals("0")||wStr.equals("0.0")?"":wStr;
//		// hStr = hStr.equals("0")||hStr.equals("0.0")?"":hStr;
//		if (nStr.trim().length() != 0) {
//			tSize++;
//			pw.println("normalTField:" + nStr.trim());
//			pw.flush();
//		}
//		if (oStr.trim().length() != 0) {
//			tSize++;
//			pw.println("overTimeTField:" + oStr.trim());
//			pw.flush();
//		}
//		if (wStr.trim().length() != 0) {
//			tSize++;
//			pw.println("weekendTField:" + wStr.trim());
//			pw.flush();
//		}
//		if (hStr.trim().length() != 0) {
//			tSize++;
//			pw.println("holidayTField:" + hStr.trim());
//			pw.flush();
//		}
//
//		if (tSize != 1) {
//			MessageBox.post("Assign的时候，同时只能输入一种类型的时间！", "WARNING",
//					MessageBox.WARNING);
//			return;
//		}
//
//		int colCount = moveTable.getColumnCount();
//		int rowCount = moveTable.getRowCount();
//		HashSet<String> errorSet = new HashSet<String>();
//		errorSet.clear();
//		DefaultTableModel moveTable_m = (DefaultTableModel) moveTable
//				.getModel();
//		for (int i = 7; i < colCount; i++) {
//			TableColumn col = moveTable.getColumnModel().getColumn(i);
//			for (int j = 0; j < rowCount; j++) {
//				if (moveTable.isCellSelected(j, i)) {
//					Color backColor = rColor.get(j + "," + i);
//					if (backColor == null) {
//						continue;
//					}
//					if (backColor.equals(normalColorSelected)
//							|| backColor.equals(normalColorNoSelectedIsNull)
//							|| backColor.equals(normalColorNoSelectedIsZero)
//							|| backColor.equals(normalColorNoSelectedIsEight)
//							|| backColor
//									.equals(normalColorNoSelectedIsNotEight)) {
//						if (nStr.trim().length() > 0) {
//							Double tmpD = Double.valueOf(nStr);
//							if (tmpD != 0.0d) {
//								boolean ifail = canInsertValue(tmpD, j, i,
//										rowCount, "normal", errorSet);
//								if (ifail) {
//									cellTypeMap_move.put(j + "," + i,
//											normalType);
//									moveTable_m.setValueAt(tmpD, j, i);
//								}
//							} else {
//								boolean ifail = canInsertValue(tmpD, j, i,
//										rowCount, "normal", errorSet);
//								if (ifail) {
//									cellTypeMap_move.put(j + "," + i,
//											normalType);
//									moveTable_m.setValueAt(tmpD, j, i);
//								}
//							}
//						} else if (oStr.trim().length() > 0) {
//							Double tmpD = Double.valueOf(oStr);
//							if (tmpD != 0.0d) {
//								boolean ifail = canInsertValue(tmpD, j, i,
//										rowCount, "overtime", errorSet);
//								if (ifail) {
//									cellTypeMap_move.put(j + "," + i,
//											overtimeType);
//									rColor.put(j + "," + i,
//											overtimeColorSelected);
//									moveTable_m.setValueAt(tmpD, j, i);
//								}
//							} else {
//								boolean ifail = canInsertValue(tmpD, j, i,
//										rowCount, "overtime", errorSet);
//								if (ifail) {
//									cellTypeMap_move.put(j + "," + i,
//											normalType);
//									rColor.put(j + "," + i, normalColorSelected);
//									moveTable_m.setValueAt(tmpD, j, i);
//								}
//							}
//						}
//					} else if (backColor.equals(overtimeColorSelected)
//							|| backColor.equals(overtimeColorNoSelected)) {
//						if (oStr.trim().length() > 0) {
//							Double tmpD = Double.valueOf(oStr);
//							if (tmpD != 0.0d) {
//								boolean ifail = canInsertValue(tmpD, j, i,
//										rowCount, "overtime", errorSet);
//								if (ifail) {
//									cellTypeMap_move.put(j + "," + i,
//											overtimeType);
//									moveTable_m.setValueAt(tmpD, j, i);
//								}
//							} else {
////								boolean ifail = canInsertValue(tmpD, j, i,
////										rowCount, false, errorSet);
////								if (ifail) {
//									cellTypeMap_move.put(j + "," + i,
//											normalType);
//									rColor.put(j + "," + i, normalColorSelected);
//									moveTable_m.setValueAt(tmpD, j, i);
////								}
//							}
//						}
//					} else if (backColor.equals(weekendColorSelected)
//							|| backColor.equals(weekendColorNoSelected)) {
//						if (wStr.trim().length() > 0) {
//							Double tmpD = Double.valueOf(wStr);
//							boolean ifail = canInsertValue(tmpD, j, i,
//									rowCount, "weekend", errorSet);
//							if (ifail) {
//								cellTypeMap_move.put(j + "," + i, weekendType);
//								moveTable_m.setValueAt(tmpD, j, i);
//							}
//						}
//					} else if (backColor.equals(holidayColorSelected)
//							|| backColor.equals(holidayColorNoSelected)) {
//						if (hStr.trim().length() > 0) {
//							Double tmpD = Double.valueOf(hStr);
//							boolean ifail = canInsertValue(tmpD, j, i,
//									rowCount, "holiday", errorSet);
//							if (ifail) {
//								cellTypeMap_move.put(j + "," + i, holidayType);
//								moveTable_m.setValueAt(tmpD, j, i);
//							}
//						}
//					}
//				}
//			}
//		}
//
//		if (errorSet.size() > 0) {
//			StringBuffer errorSB = new StringBuffer();
////			errorSB.append("Warning:\n");
//			for (String string : errorSet) {
//				errorSB.append(string).append("\n");
//			}
//			MessageBox
//					.post(errorSB.toString(), "WARNING", MessageBox.WARNING);
//		}
//	}
//
//	/**
//	 * 1.获取当前单元格对应的item，判断是否已经发布,只用于Normal hour 2.计算当前列的所有值的合计，同一个人,必须不大于8
//	 * 
//	 * @param tmpD
//	 *            当前值
//	 * @param row
//	 *            当前行
//	 * @param column
//	 *            当前列
//	 * @param lastRowCount
//	 *            moveTable的行数
//	 * @param errorBuff
//	 *            错误信息保存
//	 * @return
//	 */
//	private Boolean canInsertValue(Double tmpD, int row, int column,
//			int lastRowCount, String type, HashSet<String> errorSet) {
//		Boolean isPass = false; // 默认未发布
//		Double count = 0.0d;
//		Double count_all = 0.0d;	//相同日期相同人员的总工时
//		
//		if (row >= data.size()) {
//			isPass = false;
//		} else {
//			Set<String> passksy = passMap_move.keySet();
//			for (String str : passksy) {
//				if (str.contains(",")) {
//					String str_splits = str.split(",")[0];
//					if (str_splits.trim().equals(Integer.toString(row))) {
//						boolean ifail = passMap_move.get(str);
//						if (ifail) {
//							isPass = true;
//							break;
//						}
//					}
//				}
//			}
//		}
//		String currentName = fixTable.getValueAt(row, 0).toString().trim();
//		if (currentName == null || currentName.trim().isEmpty()) {
////			errorBuff.append("第").append(row).append("行不能填入值，没有选择相关人员");
//			errorSet.add("设置工时前，需明确人员");
//		}else if (isPass) {
//			errorSet.add("明细已发布，不能修改");
//		}else if (type.equals("normal")) {
//			if (!currentName.isEmpty()) {
//				for (int i = 0; i < lastRowCount; i++) {
//					String tmpname = fixTable.getValueAt(i, 0).toString();
//					if (i == row) {
//						count += tmpD;
//						count_all += tmpD;
//					} else if (tmpname.equals(currentName) && i != row) {
//						String tmpV = moveTable.getValueAt(i, column)
//								.toString().trim();
//						if (!tmpV.isEmpty()) {
//							count += Double.parseDouble(tmpV);
//							count_all += Double.parseDouble(tmpV);
//						}
//					}
//				}
//			}
//			if (count > 8) {
////				errorBuff.append(currentName).append("在").append(column - 6)
////						.append("号如果填入当前值，hour将大于8，不能填入当前值：").append(tmpD)
////						.append("\n");
//				errorSet.add("已超法定工时,工作日Normal hours不能大于8");
//			}
//			if (count_all < 0) {
////				errorSet.add("工作日Normal hours不能小于0");
//				errorSet.add("工时不在范围内");
//			}
//		}else if (type.equals("overtime")) {
//			if (!currentName.isEmpty()) {
//				for (int i = 0; i < lastRowCount; i++) {
//					String tmpname = fixTable.getValueAt(i, 0).toString();
//					if (i == row) {
//						count += tmpD;
//						count_all += tmpD;
//					} else if (tmpname.equals(currentName) && i != row) {
//						String tmpV = moveTable.getValueAt(i, column)
//								.toString().trim();
//						if (!tmpV.isEmpty()) {
//							count += Double.parseDouble(tmpV);
//							count_all += Double.parseDouble(tmpV);
//						}
//					}
//				}
//			}
//			if (count > 16) {
////				errorBuff.append(currentName).append("在").append(column - 6)
////						.append("号如果填入当前值，hour将大于8，不能填入当前值：").append(tmpD)
////						.append("\n");
//				errorSet.add("工时填写不规范，已超当天小时数限制");
//			}
//			if (count_all < 0) {
////				errorSet.add("工时填写不规范，小时数不能小于0");
//				errorSet.add("工时不在范围内");
//			}
//		}else if (type.equals("weekend")) {
//			if (!currentName.isEmpty()) {
//				for (int i = 0; i < lastRowCount; i++) {
//					String tmpname = fixTable.getValueAt(i, 0).toString();
//					if (i == row) {
//						count += tmpD;
//						count_all += tmpD;
//					} else if (tmpname.equals(currentName) && i != row) {
//						String tmpV = moveTable.getValueAt(i, column)
//								.toString().trim();
//						if (!tmpV.isEmpty()) {
//							count += Double.parseDouble(tmpV);
//							count_all += Double.parseDouble(tmpV);
//						}
//					}
//				}
//			}
//			if (count > 24) {
////				errorBuff.append(currentName).append("在").append(column - 6)
////						.append("号如果填入当前值，hour将大于8，不能填入当前值：").append(tmpD)
////						.append("\n");
//				errorSet.add("工时填写不规范，已超当天小时数限制");
//			}
//			if (count_all < 0) {
////				errorSet.add("工时填写不规范，小时数不能小于0");
//				errorSet.add("工时不在范围内");
//			}
//		}else if (type.equals("holiday")) {
//			if (!currentName.isEmpty()) {
//				for (int i = 0; i < lastRowCount; i++) {
//					String tmpname = fixTable.getValueAt(i, 0).toString();
//					if (i == row) {
//						count += tmpD;
//						count_all += tmpD;
//					} else if (tmpname.equals(currentName) && i != row) {
//						String tmpV = moveTable.getValueAt(i, column)
//								.toString().trim();
//						if (!tmpV.isEmpty()) {
//							count += Double.parseDouble(tmpV);
//							count_all += Double.parseDouble(tmpV);
//						}
//					}
//				}
//			}
//			if (count > 24) {
////				errorBuff.append(currentName).append("在").append(column - 6)
////						.append("号如果填入当前值，hour将大于8，不能填入当前值：").append(tmpD)
////						.append("\n");
//				errorSet.add("工时填写不规范，已超当天小时数限制");
//			}
//			if (count_all < 0) {
////				errorSet.add("工时填写不规范，小时数不能小于0");
//				errorSet.add("工时不在范围内");
//			}
//		}
//		
//		if (errorSet.size() > 0) {
//			return false;
//		} else {
//			return true;
//		}
//
//	}
//	
//	private boolean couldPop(){
//		ViewEditHelper viewHelp = new ViewEditHelper(tcsession);
//		boolean ischeckoutAble = viewHelp.isCheckoutable(form);
//		return ischeckoutAble;
//	}
//
//	/**
//	 * 清除填写框的值
//	 */
//	private void assignCleanButtonAction() {
//		normalTField.setText("");
//		overTimeTField.setText("");
//		weekendTField.setText("");
//		holidayTField.setText("");
//	}
//
//	/**
//	 * 减免按钮
//	 */
//	private void reduceButtonAction(){
//		int rowLine = fixTable.getSelectedRow();
//		if (rowLine == -1) {
//			MessageBox.post("需选中减免人员", "WARNING", MessageBox.WARNING);
//			return;
//		}else{
//			boolean ifail = fixTable.getCellEditor() == null ? true : fixTable
//					.getCellEditor(rowLine, 0).stopCellEditing();
//			boolean ifail1 = moveTable.getCellEditor() == null ? true
//					: moveTable.getCellEditor().stopCellEditing();
//			String name = fixTable.getValueAt(rowLine, 0).toString();
//			if (name.trim().isEmpty()) {
//				MessageBox.post("需选中减免人员", "WARNING", MessageBox.WARNING);
//				return;
//			}else{
//				String fixCell0 = fixTable.getValueAt(rowLine, 0).toString();
//				String fixCell1 = fixTable.getValueAt(rowLine, 1).toString();
//				String moveCell0 = moveTable.getValueAt(rowLine, 0).toString();
//				boolean ifail2 = moveTable.getCellEditor() == null ? true
//						: moveTable.getCellEditor(rowLine, 1).stopCellEditing();
//				String moveCell1 = moveTable.getValueAt(rowLine, 1).toString();
//				String moveCell2 = moveTable.getValueAt(rowLine, 2).toString();
//				Vector fixrowV = new Vector();
//				fixrowV.add(fixCell0);
//				fixrowV.add(fixCell1);
//				Vector moverowV = new Vector();
//				for (int i = 0; i < moveColumnName.size(); i++) {
//					if (i == 0) {
//						moverowV.add(moveCell0);
//					} else if (i == 1) {
//						moverowV.add(moveCell1);
//					} else if (i == 2) {
//						moverowV.add(moveCell2);
//					} else {
//						moverowV.add("");
//					}
//				}
//				int inserRow = -1;
//				for (int i = 0; i < fixTable.getRowCount(); i++) {
//					String tmpName = fixTable.getValueAt(i, 0).toString();
//					if (tmpName.trim().isEmpty()) {
//						inserRow = i;
//						break;
//					}
//				}
//				if (inserRow == -1) {
//					((DefaultTableModel) fixTable.getModel()).addRow(fixrowV);
//					((DefaultTableModel) moveTable.getModel()).addRow(moverowV);
//				}else{
//					((DefaultTableModel) fixTable.getModel()).insertRow(inserRow,
//							fixrowV);
//					((DefaultTableModel) moveTable.getModel()).insertRow(inserRow,
//							moverowV);
//				}
//			}
//		}
//	}
//	/**
//	 * 点击JButton("ADD")后的事件：
//	 */
//	private void addButtonAction() {
//		Vector fixrowV = new Vector();
//		fixrowV.add("");
//		fixrowV.add("");
//		((DefaultTableModel) fixTable.getModel()).insertRow(
//				fixTable.getRowCount(), fixrowV);
//		Vector moverowV = new Vector();
//		for (int i = 0; i < moveColumnName.size(); i++) {
//			moverowV.add("");
//		}
//		((DefaultTableModel) moveTable.getModel()).insertRow(
//				moveTable.getRowCount(), moverowV);
//		
////		int rowLine = fixTable.getSelectedRow();
////		if (rowLine == -1) {
////			// 没有选中项
////			Vector fixrowV = new Vector();
////			fixrowV.add("");
////			fixrowV.add("");
////			((DefaultTableModel) fixTable.getModel()).insertRow(
////					fixTable.getRowCount(), fixrowV);
////			Vector moverowV = new Vector();
////			for (int i = 0; i < moveColumnName.size(); i++) {
////				moverowV.add("");
////			}
////			((DefaultTableModel) moveTable.getModel()).insertRow(
////					moveTable.getRowCount(), moverowV);
////		} else {
////			boolean ifail = fixTable.getCellEditor() == null ? true : fixTable
////					.getCellEditor(rowLine, 0).stopCellEditing();
////			boolean ifail1 = moveTable.getCellEditor() == null ? true
////					: moveTable.getCellEditor().stopCellEditing();
////			String name = fixTable.getValueAt(rowLine, 0).toString();
////			// String name = "";
////			if (name.trim().isEmpty()) {
////				Vector fixrowV = new Vector();
////				fixrowV.add("");
////				fixrowV.add("");
////				((DefaultTableModel) fixTable.getModel()).insertRow(
////						fixTable.getRowCount(), fixrowV);
////				Vector moverowV = new Vector();
////				for (int i = 0; i < moveColumnName.size(); i++) {
////					moverowV.add("");
////				}
////				((DefaultTableModel) moveTable.getModel()).insertRow(
////						moveTable.getRowCount(), moverowV);
////			} else {
////				String fixCell0 = fixTable.getValueAt(rowLine, 0).toString();
////				String fixCell1 = fixTable.getValueAt(rowLine, 1).toString();
////				String moveCell0 = moveTable.getValueAt(rowLine, 0).toString();
////				boolean ifail2 = moveTable.getCellEditor() == null ? true
////						: moveTable.getCellEditor(rowLine, 1).stopCellEditing();
////				// String moveCell1 = moveTable.getValueAt(rowLine,
////				// 1).toString();
////				String moveCell2 = moveTable.getValueAt(rowLine, 2).toString();
////				Vector fixrowV = new Vector();
////				fixrowV.add(fixCell0);
////				fixrowV.add(fixCell1);
////				Vector moverowV = new Vector();
////				for (int i = 0; i < moveColumnName.size(); i++) {
////					if (i == 0) {
////						moverowV.add(moveCell0);
////					} else if (i == 1) {
////						moverowV.add("");
////					} else if (i == 2) {
////						moverowV.add(moveCell2);
////					} else {
////						moverowV.add("");
////					}
////				}
//////				int inserRow = fixTable.getRowCount();
////				int inserRow = -1;
////				for (int i = 0; i < fixTable.getRowCount(); i++) {
////					String tmpName = fixTable.getValueAt(i, 0).toString();
////					if (tmpName.trim().isEmpty()) {
////						inserRow = i;
////						break;
////					}
////				}
////				if (inserRow == -1) {
////					((DefaultTableModel) fixTable.getModel()).addRow(fixrowV);
////					((DefaultTableModel) moveTable.getModel()).addRow(moverowV);
////				}else{
////					((DefaultTableModel) fixTable.getModel()).insertRow(inserRow,
////							fixrowV);
////					((DefaultTableModel) moveTable.getModel()).insertRow(inserRow,
////							moverowV);
////				}
////			}
////		}
//	}
//
//	/**
//	 * 点击JButton("-")后的事件：
//	 */
//	private void minusButtonAction() {
//		int rowLine = fixTable.getSelectedRow();
//		if (rowLine != -1) {
//			if (data.size() < rowLine + 1) {
//				pw.println("不存在明细");
//				pw.flush();
//				// 修改，如果删除此行，需要对passMap、cellTypeMap做相应的删除工作
//				// passMap处理，一个是fix，另一个是move：将rowLine+1行的数据提前
//				passMap_fix = removePassRow(passMap_fix, rowLine);
//				passMap_move = removePassRow(passMap_move, rowLine);
//				// cellTypeMap处理，一个是fix，另一额是move
//				cellTypeMap_fix = removeCellTypeRow(cellTypeMap_fix, rowLine);
//				cellTypeMap_move = removeCellTypeRow(cellTypeMap_move, rowLine);
//				// fixTable.getCellEditor().stopCellEditing();
//				boolean ifail = fixTable.getCellEditor() == null ? true
//						: fixTable.getCellEditor().stopCellEditing();
//				((DefaultTableModel) fixTable.getModel()).removeRow(rowLine);
//				((DefaultTableModel) moveTable.getModel()).removeRow(rowLine);
//			}else{
//				//存在明细
//				pw.println("存在明细");
//				pw.flush();
//				boolean ifial = canRemovePassRow(passMap_fix, rowLine);
//				if (!ifial) {
//					return;
//				}
//				passMap_fix = removePassRow(passMap_fix, rowLine);
//				passMap_move = removePassRow(passMap_move, rowLine);
//				cellTypeMap_fix = removeCellTypeRow(cellTypeMap_fix, rowLine);
//				cellTypeMap_move = removeCellTypeRow(cellTypeMap_move, rowLine);
//				Vector rowobjs = (Vector) data.get(rowLine);
//				for (int i = 0; i < rowobjs.size(); i++) {
//					if (i>0 && i < 9) {
//						continue;
//					}
//					Object obj = rowobjs.get(i);
//					pw.println("删除项："+obj);
//					pw.flush();
//					if (obj != null && obj instanceof TCComponent) {
//						deleteExts.add(obj);
//					}
//				}
//				data.remove(rowLine);
//				boolean ifail = fixTable.getCellEditor() == null ? true
//						: fixTable.getCellEditor().stopCellEditing();
//				((DefaultTableModel) fixTable.getModel()).removeRow(rowLine);
//				((DefaultTableModel) moveTable.getModel()).removeRow(rowLine);
//			}
//		}
//	}
//	
//	private boolean canRemovePassRow(HashMap<String, Boolean> passMap, int removeRowLine){
//		boolean ifial = true;
//		Set<String> keys = passMap.keySet();
//		for (String string : keys) {
//			Boolean tmpValue = passMap.get(string);
//			String[] strs = string.split(",");
//			Integer tmpRowCount = Integer.parseInt(strs[0]);
//			if (tmpRowCount == removeRowLine) {
//				if (tmpValue.booleanValue() == true) {
//					ifial = false;
//				}
//			}
//		}
//		return ifial;
//	}
//
//	/**
//	 * 去除一行数据的操作
//	 * 
//	 * @param passMap
//	 * @param removeRowLine
//	 * @return
//	 */
//	private HashMap<String, Boolean> removePassRow(
//			HashMap<String, Boolean> passMap, int removeRowLine) {
//		HashMap<String, Boolean> tmpHashmap = new HashMap<String, Boolean>();
//		Set<String> keys = passMap.keySet();
//		for (String string : keys) {
//			Boolean tmpValue = passMap.get(string);
//			String[] strs = string.split(",");
//			Integer tmpRowCount = Integer.parseInt(strs[0]);
//			if (tmpRowCount < removeRowLine) {
//				tmpHashmap.put(strs[0] + "," + strs[1], tmpValue);
//			} else if (tmpRowCount > removeRowLine) {
//				tmpRowCount = tmpRowCount - 1;
//				tmpHashmap.put(tmpRowCount + "," + strs[1], tmpValue);
//			}
//		}
//		return tmpHashmap;
//	}
//
//	/**
//	 * 去除一行数据的操作
//	 * 
//	 * @param cellTypeMap
//	 * @param removeRowLine
//	 * @return
//	 */
//	private HashMap<String, String> removeCellTypeRow(
//			HashMap<String, String> cellTypeMap, int removeRowLine) {
//		HashMap<String, String> tmpHashmap = new HashMap<String, String>();
//		Set<String> keys = cellTypeMap.keySet();
//		for (String string : keys) {
//			String tmpValue = cellTypeMap.get(string);
//			String[] strs = string.split(",");
//			Integer tmpRowCount = Integer.parseInt(strs[0]);
//			if (tmpRowCount < removeRowLine) {
//				tmpHashmap.put(strs[0] + "," + strs[1], tmpValue);
//			} else if (tmpRowCount > removeRowLine) {
//				tmpRowCount = tmpRowCount - 1;
//				tmpHashmap.put(tmpRowCount + "," + strs[1], tmpValue);
//			}
//		}
//		return tmpHashmap;
//	}
//	
//	/**
//	 * 去除一行的Overtime标记
//	 * 
//	 * @param cellTypeMap
//	 * @param removeRowLine
//	 * @return
//	 */
//	private HashMap<String, String> removeCellTypeOvertimeRow(HashMap<String, String> cellTypeMap, int removeRowLine){
//		HashMap<String, String> tmpHashmap = new HashMap<String, String>();
//		Set<String> keys = cellTypeMap.keySet();
//		for (String string : keys) {
//			String tmpValue = cellTypeMap.get(string);
//			String[] strs = string.split(",");
//			Integer tmpRowCount = Integer.parseInt(strs[0]);
//			if (tmpRowCount == removeRowLine && tmpValue != null && tmpValue.contains("Overtime")) {
//				cellTypeMap.put(string, normalType);
//			}
//		}
//		return cellTypeMap;
//	}
//			
//			
//
//	private void initData() throws TCException {
//		byPass(true);
//		TCComponent[] extComs = form
//				.getReferenceListProperty("jci6_Ext2Detail");
//		byPass(false);
//		ArrayList<String> moveColumnName_list = new ArrayList<String>();
//		String[] moveColumnName_first = { "Company", "Program", "Onboard Date",
//				 "N", "O", "W", "H" };
//		for (String string : moveColumnName_first) {
//			moveColumnName_list.add(string);
//		}
//		for (int i = 0; i < tcweeks.size(); i++) {
//			String day_count = "" + (i + 1);
//			moveColumnName_list.add(day_count);
//		}
//
//		moveColumnName = new Vector();
//		for (String string : moveColumnName_list) {
//			moveColumnName.add(string);
//		}
//
//		fixColumnName = new Vector();
//		fixColumnName.add("Name");
//		fixColumnName.add("Global ID");
//
//		data1 = new Vector();
//		data2 = new Vector();
//		data = new Vector();
//
//		if (extComs == null || extComs.length == 0) {
//
//		} else {
//			Vector<Integer> rowCount_native_V = new Vector<Integer>();
//			for (int i = 0; i < extComs.length; i++) {
//				extComs[i].refresh();
//				String tmpName = extComs[i].getStringProperty("object_name");
//				if (tmpName.startsWith("Head")) {
//					rowCount_native_V.add(i);
//				}
//			}
//			int rowCount_last = extComs.length;
//			for (int i = 0; i < rowCount_native_V.size(); i++) {
//				int tmprowCount = rowCount_native_V.get(i);
//				if (i != rowCount_native_V.size() - 1) {
//					int tmpnextrowCount = rowCount_native_V.get(i + 1);
//					Vector data0_1 = new Vector();
//					// 固定列
//					Vector data1_1 = new Vector();
//					String tmpExtName = extComs[tmprowCount]
//							.getStringProperty("jci6_ExtName");
//					String tmpGID = extComs[tmprowCount]
//							.getStringProperty("jci6_GID");
//					data1_1.add(tmpExtName);
//					data1_1.add(tmpGID);
//					data1.add(data1_1);
//					data0_1.add(extComs[tmprowCount]);
//					data0_1.add(extComs[tmprowCount]);
//					// 移动列
//					Vector data2_1 = new Vector();
//					for (int i1 = 0; i1 < moveColumnName.size(); i1++) {
//						if (i1 == 0) {
//							String tmpCompany = extComs[tmprowCount]
//									.getStringProperty("jci6_Company");
//							data2_1.add(tmpCompany);
//							data0_1.add(extComs[tmprowCount]);
//						} else if (i1 == 1) {
//							byPass(true);
//							TCComponent tmpProgram = extComs[tmprowCount]
//									.getReferenceProperty("jci6_Program");
//							byPass(false);
//							if (tmpProgram == null) {
//								data2_1.add("");
//							} else {
//								String tmpProgramID = tmpProgram
//										.getStringProperty("item_id");
//								String tmpProgramName = tmpProgram
//										.getStringProperty("object_name");
//								data2_1.add(tmpProgramID + "_" + tmpProgramName);
//							}
//							data0_1.add(extComs[tmprowCount]);
//						} else if (i1 == 2) {
//							Date tmpOnboard = extComs[tmprowCount]
//									.getDateProperty("jci6_Onboard");
//							SimpleDateFormat smf = new SimpleDateFormat(
//									"yyyy/MM/dd");
//							if (tmpOnboard != null) {
//								data2_1.add(smf.format(tmpOnboard));
//							} else {
//								data2_1.add("");
//							}
//							data0_1.add(extComs[tmprowCount]);
//						} else if (i1 > 2 && i1 < 7) {
//							data2_1.add("");
//							data0_1.add("");
//						} else if (i1 >= 7) {
//							boolean hasItem = false;
//							for (int j = tmprowCount + 1; j < tmpnextrowCount; j++) {
//								TCComponent tmoCom = extComs[j];
//								String tmoCom_name = tmoCom
//										.getStringProperty("object_name");
//								
////								int tmpDay = Integer.parseInt(tmoCom_name
////										.split("_")[5]);
//								String[] tmoCom_name_splists = tmoCom_name.split("_");
//								int tmpDay = Integer.parseInt(tmoCom_name_splists[tmoCom_name_splists.length-1]);
//								if (i1 - 6 == tmpDay) {
//									data0_1.add(tmoCom);
//									Double tmpHours = tmoCom
//											.getDoubleProperty("jci6_Hours");
//									data2_1.add(tmpHours.toString());
//									hasItem = true;
//									break;
//								}
//							}
//							if (!hasItem) {
//								data2_1.add("");
//								data0_1.add("");
//							}
//						}
//					}
//					data2.add(data2_1);
//					data.add(data0_1);
//				} else {
//					if (tmprowCount < rowCount_last) {
//						int tmpnextrowCount = rowCount_last;
//						Vector data0_1 = new Vector();
//						// 固定列
//						Vector data1_1 = new Vector();
//						String tmpExtName = extComs[tmprowCount]
//								.getStringProperty("jci6_ExtName");
//						String tmpGID = extComs[tmprowCount]
//								.getStringProperty("jci6_GID");
//						data1_1.add(tmpExtName);
//						data1_1.add(tmpGID);
//						data1.add(data1_1);
//						data0_1.add(extComs[tmprowCount]);
//						data0_1.add(extComs[tmprowCount]);
//						// 移动列
//						Vector data2_1 = new Vector();
//						for (int i1 = 0; i1 < moveColumnName.size(); i1++) {
//							if (i1 == 0) {
//								String tmpCompany = extComs[tmprowCount]
//										.getStringProperty("jci6_Company");
//								data2_1.add(tmpCompany);
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 == 1) {
//								byPass(true);
//								TCComponent tmpProgram = extComs[tmprowCount]
//										.getReferenceProperty("jci6_Program");
//								byPass(false);
//								if (tmpProgram == null) {
//									data2_1.add("");
//								} else {
//									String tmpProgramID = tmpProgram
//											.getStringProperty("item_id");
//									String tmpProgramName = tmpProgram
//											.getStringProperty("object_name");
//									data2_1.add(tmpProgramID + "_"
//											+ tmpProgramName);
//								}
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 == 2) {
//								Date tmpOnboard = extComs[tmprowCount]
//										.getDateProperty("jci6_Onboard");
//								SimpleDateFormat smf = new SimpleDateFormat(
//										"yyyy/MM/dd");
//								if (tmpOnboard != null) {
//									data2_1.add(smf.format(tmpOnboard));
//								} else {
//									data2_1.add("");
//								}
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 > 2 && i1 < 7) {
//								data2_1.add("");
//								data0_1.add("");
//							} else if (i1 >= 7) {
//								boolean hasItem = false;
//								for (int j = tmprowCount + 1; j < tmpnextrowCount; j++) {
//									TCComponent tmoCom = extComs[j];
//									String tmoCom_name = tmoCom
//											.getStringProperty("object_name");
////									int tmpDay = Integer.parseInt(tmoCom_name
////											.split("_")[5]);
//									String[] tmoCom_name_splits = tmoCom_name.split("_");
//									int tmpDay = Integer.parseInt(tmoCom_name_splits[tmoCom_name_splits.length-1]);
//									if (i1 - 6 == tmpDay) {
//										Double tmpHours = tmoCom
//												.getDoubleProperty("jci6_Hours");
//										data0_1.add(tmoCom);
//										data2_1.add(tmpHours.toString());
//										hasItem = true;
//										break;
//									}
//								}
//								if (!hasItem) {
//									data2_1.add("");
//									data0_1.add("");
//								}
//							}
//						}
//						data2.add(data2_1);
//						data.add(data0_1);
//					} else if (tmprowCount == rowCount_last) {
//						int tmpnextrowCount = rowCount_last;
//						Vector data0_1 = new Vector();
//						// 固定列
//						Vector data1_1 = new Vector();
//						String tmpExtName = extComs[tmprowCount]
//								.getStringProperty("jci6_ExtName");
//						String tmpGID = extComs[tmprowCount]
//								.getStringProperty("jci6_GID");
//						data1_1.add(tmpExtName);
//						data1_1.add(tmpGID);
//						data1.add(data1_1);
//						data0_1.add(extComs[tmprowCount]);
//						data0_1.add(extComs[tmprowCount]);
//						// 移动列
//						Vector data2_1 = new Vector();
//						for (int i1 = 0; i1 < moveColumnName.size(); i1++) {
//							if (i1 == 0) {
//								String tmpCompany = extComs[tmprowCount]
//										.getStringProperty("jci6_Company");
//								data2_1.add(tmpCompany);
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 == 1) {
//								byPass(true);
//								TCComponent tmpProgram = extComs[tmprowCount]
//										.getReferenceProperty("jci6_Program");
//								byPass(false);
//								if (tmpProgram == null) {
//									data2_1.add("");
//								} else {
//									String tmpProgramID = tmpProgram
//											.getStringProperty("item_id");
//									String tmpProgramName = tmpProgram
//											.getStringProperty("object_name");
//									data2_1.add(tmpProgramID + "_"
//											+ tmpProgramName);
//								}
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 == 2) {
//								Date tmpOnboard = extComs[tmprowCount]
//										.getDateProperty("jci6_Onboard");
//								SimpleDateFormat smf = new SimpleDateFormat(
//										"yyyy/MM/dd");
//								if (tmpOnboard != null) {
//									data2_1.add(smf.format(tmpOnboard));
//								} else {
//									data2_1.add("");
//								}
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 > 2 && i1 < 7) {
//								data2_1.add("");
//								data0_1.add("");
//							} else if (i1 >= 7) {
//								data2_1.add("");
//								data0_1.add("");
//							}
//						}
//						data2.add(data2_1);
//						data.add(data0_1);
//					}
//				}
//			}
//		}
//		if (data1.size() == 0) {
//			// 添加空白行
//			for (int i = 0; i < 1; i++) {
//				Vector date1_1 = new Vector();
//				date1_1.add("");
//				date1_1.add("");
//				data1.add(date1_1);
//				Vector date2_1 = new Vector();
//				for (int j = 0; j < moveColumnName.size(); j++) {
//					if (i == 0) {
//						date2_1.add("");
//					} else if (i == 1) {
//						date2_1.add("");
//					} else if (i == 2) {
//						date2_1.add("");
//					} else if (i > 2 && i < 7) {
//						date2_1.add("");
//					} else if (i >= 7) {
//						date2_1.add("");
//					}
//				}
//				data2.add(date2_1);
//			}
//		}
//		/*
//		 * 处理data，为passMap、cellTypeMap_Column赋值
//		 */
//		if (!data.isEmpty()) {
//			for (int rowCount = 0; rowCount < data.size(); rowCount++) {
//				Vector ColumnV = (Vector) data.get(rowCount);
//				for (int columnCount = 0; columnCount < ColumnV.size(); columnCount++) {
//					Object obj = ColumnV.get(columnCount);
//					if (obj instanceof TCComponent) {
//						// 为cellTypeMap、passMap赋值
//						Boolean ispass = JCITableUtil.isPass((TCComponent) obj,form);
//						String tmpType = ((TCComponent) obj)
//								.getStringProperty("jci6_BillType");
//						if (columnCount < 2) {
//							if (tmpType.equals(normalType)
//									|| tmpType.equals(overtimeType)
//									|| tmpType.equals(holidayType)
//									|| tmpType.equals(weekendType)) {
//
//								cellTypeMap_fix.put(rowCount + ","
//										+ columnCount, tmpType);
//							}
//							passMap_fix.put(rowCount + "," + columnCount,
//									ispass);
//						} else {
//							if (tmpType.equals(normalType)
//									|| tmpType.equals(overtimeType)
//									|| tmpType.equals(holidayType)
//									|| tmpType.equals(weekendType)) {
//
//								cellTypeMap_move.put(rowCount + ","
//										+ (columnCount - 2), tmpType);
//							}
//							passMap_move.put(
//									rowCount + "," + (columnCount - 2), ispass);
//						}
//					}
//				}
//			}
//		}
//
//	}
//
//	private void reloadModule() {
//		for (int i = 0; i < data1.size(); i++) {
//			((DefaultTableModel) moveTable.getModel()).addRow((Vector) data2
//					.elementAt(i));
//			((DefaultTableModel) fixTable.getModel()).addRow((Vector) data1
//					.elementAt(i));
//		}
//	}
//
//	/**
//	 * 为JTabbedPane添加第二页:汇总页
//	 * 
//	 * @param tabbedPanel
//	 */
//	private void addSecondPanel(JTabbedPane tabbedPanel) {
//		JPanel secondPanel = new JPanel();
//		secondPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
//		tabbedPanel.addTab("Person Summary", null, secondPanel, null);
//		secondPanel.setLayout(new BorderLayout(0, 0));
//		// ***********************************Module*****************************
//		Vector secondColumnName = initSecondColumnName();
//		secondData = initSecondData();
//		DefaultTableModel summaryTableModule = new DefaultTableModel(
//				secondData, secondColumnName) {
//			@Override
//			public boolean isCellEditable(int row, int column) {
//				return false;
//			}
//		};
//		summaryTabel = new JTable(summaryTableModule) {
//			protected JTableHeader createDefaultTableHeader() {
//				return new GroupableTableHeader(columnModel);
//			}
//		};
//		TableColumnModel cmsum = summaryTabel.getColumnModel();
//		GroupableTableHeader headersum = (GroupableTableHeader) summaryTabel
//				.getTableHeader();
//		// ====================0-1===========================
//		ColumnGroup g_name_0_2 = new ColumnGroup(jciYear + "-"
//				+ mon[jciMonth - 1]);
//		g_name_0_2.add(cmsum.getColumn(0));
//		g_name_0_2.add(cmsum.getColumn(1));
//		g_name_0_2.add(cmsum.getColumn(2));
//		headersum.addColumnGroup(g_name_0_2);
//		// ====================2============================
//		ColumnGroup g_name_3 = new ColumnGroup(" ");
//		g_name_3.add(cmsum.getColumn(3));
//		headersum.addColumnGroup(g_name_3);
//		// =====================3-8=========================
//		ColumnGroup g_name_4_9 = new ColumnGroup("Summary");
//		g_name_4_9.add(cmsum.getColumn(4));
//		for (int i = 4; i < 10; i++) {
//			g_name_4_9.add(cmsum.getColumn(i));
//		}
//		headersum.addColumnGroup(g_name_4_9);
//		// =====================9~=========================
////		ColumnGroup g_name_9 = new ColumnGroup(" ");
//		String[] week = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
//		SimpleDateFormat sdfE = new SimpleDateFormat("E", Locale.ENGLISH);
//		for (int i = 0; i < week.length; i++) {
//			Date tmpD = tcweeks.get(i);
//			week[i] = sdfE.format(tmpD);
//		}
//		int weekStart = 0;
//		for (int i = 10; i < secondColumnName.size(); i++) {
//			if (weekStart == 7) {
//				weekStart = 0;
//			}
//			String tmpWeek = week[weekStart];
//			weekStart++;
//			ColumnGroup g_name_tmp = new ColumnGroup(tmpWeek);
//			g_name_tmp.add(cmsum.getColumn(i));
//			headersum.addColumnGroup(g_name_tmp);
//		}
//		summaryTabel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		summaryTabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		summaryTabel.setCellSelectionEnabled(true);
//		summaryTabel.setRowHeight(20);
//		// myCellRenderer2
//		myCellRenderer2 summaryCellRenderer = new myCellRenderer2();
//		summaryCellRenderer.setHorizontalAlignment(JLabel.CENTER);
//		Integer[] colSize = { 42, 148, 100, 178, 100, 75, 36, 36, 36, 36, 33 };
//		for (int i = 0; i < secondColumnName.size(); i++) {
//			summaryTabel.getColumnModel().getColumn(i)
//					.setCellRenderer(summaryCellRenderer);
//			if (i < 10) {
//				summaryTabel.getColumnModel().getColumn(i)
//						.setPreferredWidth(colSize[i]);
//			} else {
//				summaryTabel.getColumnModel().getColumn(i)
//						.setPreferredWidth(colSize[10]);
//			}
//		}
//		JScrollPane scroll = new JScrollPane(summaryTabel);
//		secondPanel.add(scroll, BorderLayout.CENTER);
//	}
//
//	private Vector initSecondColumnName() {
//		Vector secondColumnName = new Vector();
//		secondColumnName.add("NO.");
//		secondColumnName.add("Name");
//		secondColumnName.add("Global ID");
//		String[] moveColumnName_first = { "Company", "Onboard Date", "Cost",
//				"N", "O", "W", "H" };
//		for (String string : moveColumnName_first) {
//			secondColumnName.add(string);
//		}
//		for (int i = 0; i < tcweeks.size(); i++) {
//			String day_count = "" + (i + 1);
//			secondColumnName.add(day_count);
//		}
//		return secondColumnName;
//	}
//
////	/**
////	 * 获取fix和move的值
////	 * 
////	 * @return
////	 */
////	private Vector initSecondData() {
////		Vector returnv = new Vector();
////		overtimeMap = new HashMap<Integer, Vector>();
////		/**
////		 * 合并相同人员下当天的工时
////		 */
////		int rowCount = fixTable.getRowCount();
////		int moveColumn = moveTable.getColumnCount();
////		// 1.获取相同人员的行号
////		HashMap<String, Vector> nameLineNos = new HashMap<String, Vector>();
////		for (int i = 0; i < rowCount; i++) {
////			String cell0 = fixTable.getValueAt(i, 0).toString();
////			String cell1 = fixTable.getValueAt(i, 1).toString();
////			String currentName = cell0 + cell1;
////			if (currentName.trim().length() > 0) {
////				Vector lineNos = nameLineNos.get(currentName);
////				if (lineNos == null) {
////					lineNos = new Vector<Integer>();
////				}
////				lineNos.add(i);
////				nameLineNos.put(currentName, lineNos);
////			}
////		}
////
////		Set<String> names = nameLineNos.keySet();
////		Integer rowNO = 1;
////		for (String currentName : names) {
////			pw.println("合并人员：" + currentName);
////			Vector<Integer> lineNos = nameLineNos.get(currentName);
////			Vector<Integer> overtimeColV = new Vector<Integer>();
////			Vector sameNameValues = new Vector();
////			sameNameValues.setSize(moveColumn + 3);
////
////			for (Integer lineNo : lineNos) {
////				String fixCell0 = fixTable.getValueAt(lineNo, 0).toString();
////				String fixCell1 = fixTable.getValueAt(lineNo, 1).toString();
////				sameNameValues.setElementAt(rowNO, 0);
////				sameNameValues.setElementAt(fixCell0, 1);
////				sameNameValues.setElementAt(fixCell1, 2);
////				for (int i = 0; i < moveColumn; i++) {
////					if (i == 1) {
////						// 不需要ProgramInfo
////						continue;
////					} else {
////						String tmpCell = moveTable.getValueAt(lineNo, i)
////								.toString().trim();
////						// 0~3:字符串，4~7：NOHW的值，8~：工时
////						if (i < 3) {
////							if (i == 0) {
////								sameNameValues.setElementAt(tmpCell, 3);
////							} else {
////								sameNameValues.setElementAt(tmpCell, i + 2);
////								sameNameValues.setElementAt("", i + 3);
////							}
////						} else if (i >= 3) {
////							tmpCell = tmpCell.isEmpty() ? "0" : tmpCell;
////							double currentCellDouble = Double
////									.parseDouble(tmpCell);
////							Object oldCell = sameNameValues.get(i + 3);
////							String oldCellValue = oldCell == null
////									|| oldCell.toString().trim().isEmpty() ? "0"
////									: oldCell.toString().trim();
////							double oldCellValueDouble = Double
////									.parseDouble(oldCellValue);
////							sameNameValues.setElementAt(
////									(currentCellDouble + oldCellValueDouble),
////									i + 3);
////							String tmpCellType = cellTypeMap_move.get(lineNo
////									+ "," + i);
////							if (tmpCellType != null
////									&& tmpCellType.equals(overtimeType)) {
////								overtimeColV.add(i + 3);
////							}
////						}
////					}
////				}
////			}
////			overtimeMap.put(rowNO, overtimeColV);
////			// 填入
////			returnv.add(sameNameValues);
////			rowNO++;
////		}
////
////		// 获取汇总信息
////		Vector tmpV1_table0 = new Vector();
////		Vector tmpV2_table0 = new Vector();
////		for (int i = 0; i < moveColumn + 3; i++) {
////			if (i > 3 && i < 10) {
////				String tmpa = table0.getValueAt(1, i - 4).toString();
////				String tmpb = table0.getValueAt(2, i - 4).toString();
////				tmpV1_table0.add(tmpa);
////				tmpV2_table0.add(tmpb);
////			} else {
////				tmpV1_table0.add("");
////				tmpV2_table0.add("");
////			}
////		}
////		returnv.add(tmpV1_table0);
////		returnv.add(tmpV2_table0);
////		secondData = returnv;
////		return returnv;
////	}
//	/**
//	 * 获取fix和move的值
//	 * 
//	 * @return
//	 */
//	private Vector initSecondData() {
//		Vector returnv = new Vector();
//		overtimeMap = new HashMap<Integer, Vector>();
//		passLines = new ArrayList<Integer>();
//		/**
//		 * 合并相同人员下当天的工时
//		 */
//		int rowCount = fixTable.getRowCount();
//		int moveColumn = moveTable.getColumnCount();
//		// 1.获取相同人员的行号
//		HashMap<String, Vector> nameLineNos = new HashMap<String, Vector>();
//		for (int i = 0; i < rowCount; i++) {
//			boolean isPass = false;
//			Set<String> passkey = passMap_move.keySet();
//			for (String str : passkey) {
//				if (str.contains(",")) {
//					String str_splits = str.split(",")[0];
//					if (str_splits.trim().equals(Integer.toString(i))) {
//						boolean ifail = passMap_move.get(str);
//						if (ifail) {
//							isPass = true;
//							break;
//						}
//					}
//				}
//			}
//			String cell0 = fixTable.getValueAt(i, 0).toString();
//			String cell1 = fixTable.getValueAt(i, 1).toString();
//			String currentName = cell0 + cell1;
//			if (isPass) {
//				currentName += currentName+"AAA";
//			}else{
//				currentName += currentName+"BBB";
//			}
//			if (currentName.trim().length() > 3) {
//				Vector lineNos = nameLineNos.get(currentName);
//				if (lineNos == null) {
//					lineNos = new Vector<Integer>();
//				}
//				lineNos.add(i);
//				nameLineNos.put(currentName, lineNos);
//			}
//		}
//
//		Set<String> names = nameLineNos.keySet();
//		Vector<String> names_value = new Vector<String>(names);
//		Collections.sort(names_value);
//		
//		Integer rowNO = 1;
//		for (String currentName : names_value) {
//			pw.println("合并人员：" + currentName);
//			pw.flush();
//			Vector<Integer> lineNos = nameLineNos.get(currentName);
//			Vector<Integer> overtimeColV = new Vector<Integer>();
//			Vector sameNameValues = new Vector();
//			sameNameValues.setSize(moveColumn + 3);
//
//			for (Integer lineNo : lineNos) {
//				String fixCell0 = fixTable.getValueAt(lineNo, 0).toString();
//				String fixCell1 = fixTable.getValueAt(lineNo, 1).toString();
//				sameNameValues.setElementAt(rowNO, 0);
//				sameNameValues.setElementAt(fixCell0, 1);
//				sameNameValues.setElementAt(fixCell1, 2);
//				for (int i = 0; i < moveColumn; i++) {
//					if (i == 1) {
//						// 不需要ProgramInfo
//						continue;
//					} else {
//						String tmpCell = moveTable.getValueAt(lineNo, i)
//								.toString().trim();
//						// 0~3:字符串，4~7：NOHW的值，8~：工时
//						if (i < 3) {
//							if (i == 0) {
//								sameNameValues.setElementAt(tmpCell, 3);
//							} else {
//								sameNameValues.setElementAt(tmpCell, i + 2);
//								sameNameValues.setElementAt("", i + 3);
//							}
//						} else if (i >= 3) {
//							tmpCell = tmpCell.isEmpty() ? "0" : tmpCell;
//							double currentCellDouble = Double
//									.parseDouble(tmpCell);
//							Object oldCell = sameNameValues.get(i + 3);
//							String oldCellValue = oldCell == null
//									|| oldCell.toString().trim().isEmpty() ? "0"
//									: oldCell.toString().trim();
//							double oldCellValueDouble = Double
//									.parseDouble(oldCellValue);
//							sameNameValues.setElementAt(
//									(currentCellDouble + oldCellValueDouble),
//									i + 3);
//							String tmpCellType = cellTypeMap_move.get(lineNo
//									+ "," + i);
//							if (tmpCellType != null
//									&& tmpCellType.equals(overtimeType)) {
//								overtimeColV.add(i + 3);
//							}
//						}
//					}
//				}
//			}
//			overtimeMap.put(rowNO, overtimeColV);
//			// 填入
//			returnv.add(sameNameValues);
//			if (currentName.endsWith("BBB")) {
//				passLines.add(rowNO-1);
//			}
//			rowNO++;
//		}
//
//		// 获取汇总信息
//		Vector tmpV1_table0 = new Vector();
//		Vector tmpV2_table0 = new Vector();
//		for (int i = 0; i < moveColumn + 3; i++) {
//			if (i > 3 && i < 10) {
//				String tmpa = table0.getValueAt(1, i - 4).toString();
//				String tmpb = table0.getValueAt(2, i - 4).toString();
//				tmpV1_table0.add(tmpa);
//				tmpV2_table0.add(tmpb);
//			} else {
//				tmpV1_table0.add("");
//				tmpV2_table0.add("");
//			}
//		}
//		returnv.add(tmpV1_table0);
//		returnv.add(tmpV2_table0);
//		secondData = returnv;
//		return returnv;
//	}
//
//
//	/**
//	 * 获取fix和move的值 废弃
//	 * 
//	 * @return
//	 */
//	private Vector initSecondData1() {
//		Vector returnv = new Vector();
//		int rowCount = fixTable.getRowCount();
//		int moveColumn = moveTable.getColumnCount();
//		Integer rowNO = 1;
//		for (int i = 0; i < rowCount; i++) {
//			Vector tmpV = new Vector();
//			String fixCell0 = fixTable.getValueAt(i, 0).toString().trim();
//			if (fixCell0.isEmpty()) {
//				// continue;
//				break;
//			}
//			String fixCell1 = fixTable.getValueAt(i, 1).toString().trim();
//			tmpV.add(rowNO.toString());
//			tmpV.add(fixCell0);
//			tmpV.add(fixCell1);
//			for (int j = 0; j < moveColumn; j++) {
//				if (j == 1) {
//					continue;
//				}
//				String moveCell = moveTable.getValueAt(i, j).toString();
//				tmpV.add(moveCell);
//			}
//			returnv.add(tmpV);
//			rowNO++;
//		}
//		// 获取汇总信息
//		Vector tmpV1_table0 = new Vector();
//		Vector tmpV2_table0 = new Vector();
//		for (int i = 0; i < moveColumn + 1; i++) {
//			if (i > 3 && i < 10) {
//				String tmpa = table0.getValueAt(1, i - 4).toString();
//				String tmpb = table0.getValueAt(2, i - 4).toString();
//				tmpV1_table0.add(tmpa);
//				tmpV2_table0.add(tmpb);
//			} else {
//				tmpV1_table0.add("");
//				tmpV2_table0.add("");
//			}
//		}
//		returnv.add(tmpV1_table0);
//		returnv.add(tmpV2_table0);
//		// table0.getValueAt(1, column)
//		secondData = returnv;
//		return returnv;
//	}
//
//	/**
//	 * 为JTabbedPane添加第三页：颜色配置也
//	 * 
//	 * @param tabbedPanel
//	 */
//	private void addThirdPanel(JTabbedPane tabbedPanel) {
//
//		JPanel thirdPanel = new JPanel();
//		tabbedPanel.addTab("Color Configuration", thirdPanel);
//		thirdPanel.setLayout(new BorderLayout(0, 0));
//
//		JPanel typeColorPanel = new JPanel();
//		typeColorPanel.setBorder(new TitledBorder(new LineBorder(new Color(0,
//				0, 0), 2), "N,O,H,W COLOR", TitledBorder.LEADING,
//				TitledBorder.TOP, null, null));
//		thirdPanel.add(typeColorPanel, BorderLayout.NORTH);
//		typeColorPanel.setLayout(new GridLayout(2, 2, 0, 0));
//
//		JPanel NPanel = new JPanel();
//		NPanel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
//		typeColorPanel.add(NPanel);
//
//		JLabel NLabel = new JLabel("  N  ");
//		NLabel.setBackground(Color.LIGHT_GRAY);
//		NLabel.setOpaque(true);
//		NPanel.add(NLabel);
//
//		NField = new JTextField();
//		NField.setHorizontalAlignment(SwingConstants.CENTER);
//		NField.setEditable(false);
//		NPanel.add(NField);
//		NField.setColumns(20);
//		NField.setText(normalColor.getRed() + "," + normalColor.getGreen()
//				+ "," + normalColor.getBlue());
//		NField.setBackground(normalColor);
//
//		NButton = new JButton("<<");
//		NButton.setHorizontalAlignment(SwingConstants.LEFT);
//		NPanel.add(NButton);
//		NButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(NButton,
//						"N Color Chooser", normalColor);
//				NField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				NField.setBackground(currentColor);
//				normalColor = currentColor;
//			}
//		});
//
//		JPanel OPanel = new JPanel();
//		OPanel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
//		typeColorPanel.add(OPanel);
//
//		JLabel OLabel = new JLabel("  O  ");
//		OLabel.setBackground(Color.LIGHT_GRAY);
//		OLabel.setOpaque(true);
//		OPanel.add(OLabel);
//
//		OField = new JTextField();
//		OField.setHorizontalAlignment(SwingConstants.CENTER);
//		OField.setEditable(false);
//		OPanel.add(OField);
//		OField.setColumns(20);
//		OField.setText(overtimeColor.getRed() + "," + overtimeColor.getGreen()
//				+ "," + overtimeColor.getBlue());
//		OField.setBackground(overtimeColor);
//
//		OButton = new JButton("<<");
//		OPanel.add(OButton);
//		OButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(OButton,
//						"O Color Chooser", overtimeColor);
//				OField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				OField.setBackground(currentColor);
//				overtimeColor = currentColor;
//			}
//		});
//
//		JPanel HPanel = new JPanel();
//		HPanel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
//		typeColorPanel.add(HPanel);
//
//		JLabel HLabel = new JLabel("  H  ");
//		HLabel.setOpaque(true);
//		HLabel.setBackground(Color.LIGHT_GRAY);
//		HPanel.add(HLabel);
//
//		HField = new JTextField();
//		HField.setHorizontalAlignment(SwingConstants.CENTER);
//		HField.setEditable(false);
//		HField.setColumns(20);
//		HPanel.add(HField);
//		HField.setText(holidayColor.getRed() + "," + holidayColor.getGreen()
//				+ "," + holidayColor.getBlue());
//		HField.setBackground(holidayColor);
//
//		HButton = new JButton("<<");
//		HPanel.add(HButton);
//		HButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(HButton,
//						"H Color Chooser", holidayColor);
//				HField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				HField.setBackground(currentColor);
//				holidayColor = currentColor;
//			}
//		});
//
//		JPanel WPanel = new JPanel();
//		WPanel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
//		typeColorPanel.add(WPanel);
//
//		JLabel WLabel = new JLabel("  W  ");
//		WLabel.setOpaque(true);
//		WLabel.setBackground(Color.LIGHT_GRAY);
//		WPanel.add(WLabel);
//
//		WField = new JTextField();
//		WField.setHorizontalAlignment(SwingConstants.CENTER);
//		WField.setEditable(false);
//		WField.setColumns(20);
//		WPanel.add(WField);
//		WField.setText(weekendColor.getRed() + "," + weekendColor.getGreen()
//				+ "," + weekendColor.getBlue());
//		WField.setBackground(weekendColor);
//
//		WButton = new JButton("<<");
//		WPanel.add(WButton);
//		WButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(WButton,
//						"W Color Chooser", weekendColor);
//				WField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				WField.setBackground(currentColor);
//				weekendColor = currentColor;
//			}
//		});
//
//		JPanel dayColorPanel = new JPanel();
//		dayColorPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0,
//				0), 2), "DAY COLOR", TitledBorder.LEADING, TitledBorder.TOP,
//				null, null));
//		thirdPanel.add(dayColorPanel, BorderLayout.CENTER);
//		dayColorPanel.setLayout(new GridLayout(10, 1, 0, 0));
//
//		JPanel panel = new JPanel();
//		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
//		dayColorPanel.add(panel);
//		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 3));
//
//		JLabel normal1Label = new JLabel("DAY_NORMAL_HOUR = NULL");
//		normal1Label.setBackground(Color.LIGHT_GRAY);
//		normal1Label.setFont(new Font("黑体", Font.BOLD, 21));
//		normal1Label.setOpaque(true);
//		panel.add(normal1Label);
//
//		JPanel n1SelectedPanel = new JPanel();
//		panel.add(n1SelectedPanel);
//		FlowLayout flowLayout = (FlowLayout) n1SelectedPanel.getLayout();
//		flowLayout.setAlignment(FlowLayout.LEFT);
//
//		JLabel n1SelectedLabel = new JLabel(" Selected  ");
//		n1SelectedLabel.setBackground(Color.LIGHT_GRAY);
//		n1SelectedLabel.setOpaque(true);
//		n1SelectedPanel.add(n1SelectedLabel);
//
//		n1SelectedField = new JTextField();
//		n1SelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		n1SelectedField.setEditable(false);
//		n1SelectedPanel.add(n1SelectedField);
//		n1SelectedField.setColumns(20);
//		n1SelectedField.setText(normalColorSelected.getRed() + ","
//				+ normalColorSelected.getGreen() + ","
//				+ normalColorSelected.getBlue());
//		n1SelectedField.setBackground(normalColorSelected);
//
//		n1SelectedButton = new JButton("<<");
//		n1SelectedPanel.add(n1SelectedButton);
//		n1SelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(n1SelectedButton,
//						"N=NULL Color Chooser", normalColorSelected);
//				n1SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n1SelectedField.setBackground(currentColor);
//				n2SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n2SelectedField.setBackground(currentColor);
//				n3SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n3SelectedField.setBackground(currentColor);
//				n4SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n4SelectedField.setBackground(currentColor);
//				normalColorSelected = currentColor;
//			}
//		});
//
//		JPanel n1NoSelectedPanel = new JPanel();
//		panel.add(n1NoSelectedPanel);
//
//		JLabel n1NoSelectedLabel = new JLabel("NoSelected");
//		n1NoSelectedLabel.setBackground(Color.LIGHT_GRAY);
//		n1NoSelectedLabel.setOpaque(true);
//		n1NoSelectedPanel.add(n1NoSelectedLabel);
//
//		n1NoSelectedField = new JTextField();
//		n1NoSelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		n1NoSelectedField.setEditable(false);
//		n1NoSelectedPanel.add(n1NoSelectedField);
//		n1NoSelectedField.setColumns(20);
//		n1NoSelectedField.setText(normalColorNoSelectedIsNull.getRed() + ","
//				+ normalColorNoSelectedIsNull.getGreen() + ","
//				+ normalColorNoSelectedIsNull.getBlue());
//		n1NoSelectedField.setBackground(normalColorNoSelectedIsNull);
//
//		n1NoSelectedButton = new JButton("<<");
//		n1NoSelectedPanel.add(n1NoSelectedButton);
//		n1NoSelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(
//						n1NoSelectedButton, "N=NULL Color Chooser",
//						normalColorNoSelectedIsNull);
//				n1NoSelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n1NoSelectedField.setBackground(currentColor);
//				normalColorNoSelectedIsNull = currentColor;
//			}
//		});
//
//		JPanel panel_1 = new JPanel();
//		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
//		flowLayout_1.setVgap(3);
//		flowLayout_1.setHgap(10);
//		flowLayout_1.setAlignment(FlowLayout.LEFT);
//		panel_1.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
//		dayColorPanel.add(panel_1);
//
//		JLabel normal2Label = new JLabel("DAY_NORMAL_HOUR = 0  ");
//		normal2Label.setBackground(new Color(192, 192, 192));
//		normal2Label.setFont(new Font("黑体", Font.BOLD, 21));
//		normal2Label.setOpaque(true);
//		panel_1.add(normal2Label);
//
//		JPanel n2SelectedPanel = new JPanel();
//		panel_1.add(n2SelectedPanel);
//
//		JLabel n2SelectedLabel = new JLabel(" Selected  ");
//		n2SelectedLabel.setBackground(Color.LIGHT_GRAY);
//		n2SelectedLabel.setOpaque(true);
//		n2SelectedPanel.add(n2SelectedLabel);
//
//		n2SelectedField = new JTextField();
//		n2SelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		n2SelectedField.setEditable(false);
//		n2SelectedPanel.add(n2SelectedField);
//		n2SelectedField.setColumns(20);
//		n2SelectedField.setText(normalColorSelected.getRed() + ","
//				+ normalColorSelected.getGreen() + ","
//				+ normalColorSelected.getBlue());
//		n2SelectedField.setBackground(normalColorSelected);
//
//		n2SelectedButton = new JButton("<<");
//		n2SelectedPanel.add(n2SelectedButton);
//		n2SelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(n2SelectedButton,
//						"N=0 Color Chooser", normalColorSelected);
//				n1SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n1SelectedField.setBackground(currentColor);
//				n2SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n2SelectedField.setBackground(currentColor);
//				n3SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n3SelectedField.setBackground(currentColor);
//				n4SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n4SelectedField.setBackground(currentColor);
//				normalColorSelected = currentColor;
//			}
//		});
//
//		JPanel n2NoSelectedPanel = new JPanel();
//		panel_1.add(n2NoSelectedPanel);
//
//		JLabel n2NoSelectedLabel = new JLabel("NoSelected");
//		n2NoSelectedLabel.setBackground(Color.LIGHT_GRAY);
//		n2NoSelectedLabel.setOpaque(true);
//		n2NoSelectedPanel.add(n2NoSelectedLabel);
//
//		n2NoSelectedField = new JTextField();
//		n2NoSelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		n2NoSelectedField.setEditable(false);
//		n2NoSelectedPanel.add(n2NoSelectedField);
//		n2NoSelectedField.setColumns(20);
//		n2NoSelectedField.setText(normalColorNoSelectedIsZero.getRed() + ","
//				+ normalColorNoSelectedIsZero.getGreen() + ","
//				+ normalColorNoSelectedIsZero.getBlue());
//		n2NoSelectedField.setBackground(normalColorNoSelectedIsZero);
//
//		n2NoSelectedButton = new JButton("<<");
//		n2NoSelectedPanel.add(n2NoSelectedButton);
//		n2NoSelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(
//						n2NoSelectedButton, "N=0 Color Chooser",
//						normalColorNoSelectedIsZero);
//				n2NoSelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n2NoSelectedField.setBackground(currentColor);
//				normalColorNoSelectedIsZero = currentColor;
//			}
//		});
//
//		JPanel panel_2 = new JPanel();
//		FlowLayout flowLayout_2 = (FlowLayout) panel_2.getLayout();
//		flowLayout_2.setVgap(3);
//		flowLayout_2.setHgap(10);
//		flowLayout_2.setAlignment(FlowLayout.LEFT);
//		panel_2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
//		dayColorPanel.add(panel_2);
//
//		JLabel normal3Label = new JLabel("DAY_NORMAL_HOUR = 8  ");
//		normal3Label.setBackground(Color.LIGHT_GRAY);
//		normal3Label.setFont(new Font("黑体", Font.BOLD, 21));
//		normal3Label.setOpaque(true);
//		panel_2.add(normal3Label);
//
//		JPanel n3SelectedPanel = new JPanel();
//		panel_2.add(n3SelectedPanel);
//
//		JLabel n3SelectedLabel = new JLabel(" Selected  ");
//		n3SelectedLabel.setBackground(Color.LIGHT_GRAY);
//		n3SelectedLabel.setOpaque(true);
//		n3SelectedPanel.add(n3SelectedLabel);
//
//		n3SelectedField = new JTextField();
//		n3SelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		n3SelectedField.setEditable(false);
//		n3SelectedField.setColumns(20);
//		n3SelectedPanel.add(n3SelectedField);
//		n3SelectedField.setText(normalColorSelected.getRed() + ","
//				+ normalColorSelected.getGreen() + ","
//				+ normalColorSelected.getBlue());
//		n3SelectedField.setBackground(normalColorSelected);
//
//		n3SelectedButton = new JButton("<<");
//		n3SelectedPanel.add(n3SelectedButton);
//		n3SelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(n3SelectedButton,
//						"N=8 Color Chooser", normalColorSelected);
//				n1SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n1SelectedField.setBackground(currentColor);
//				n2SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n2SelectedField.setBackground(currentColor);
//				n3SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n3SelectedField.setBackground(currentColor);
//				n4SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n4SelectedField.setBackground(currentColor);
//				normalColorSelected = currentColor;
//			}
//		});
//
//		JPanel n3NoSelectedPanel = new JPanel();
//		panel_2.add(n3NoSelectedPanel);
//
//		JLabel n3NoSelectedLabel = new JLabel("NoSelected");
//		n3NoSelectedLabel.setBackground(Color.LIGHT_GRAY);
//		n3NoSelectedLabel.setOpaque(true);
//		n3NoSelectedPanel.add(n3NoSelectedLabel);
//
//		n3NoSelectedField = new JTextField();
//		n3NoSelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		n3NoSelectedField.setEditable(false);
//		n3NoSelectedField.setColumns(20);
//		n3NoSelectedPanel.add(n3NoSelectedField);
//		n3NoSelectedField.setText(normalColorNoSelectedIsEight.getRed() + ","
//				+ normalColorNoSelectedIsEight.getGreen() + ","
//				+ normalColorNoSelectedIsEight.getBlue());
//		n3NoSelectedField.setBackground(normalColorNoSelectedIsEight);
//
//		n3NoSelectedButton = new JButton("<<");
//		n3NoSelectedPanel.add(n3NoSelectedButton);
//		n3NoSelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(
//						n3NoSelectedButton, "N=8 Color Chooser",
//						normalColorNoSelectedIsEight);
//				n3NoSelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n3NoSelectedField.setBackground(currentColor);
//				normalColorNoSelectedIsEight = currentColor;
//			}
//		});
//
//		JPanel panel_3 = new JPanel();
//		FlowLayout flowLayout_3 = (FlowLayout) panel_3.getLayout();
//		flowLayout_3.setAlignment(FlowLayout.LEFT);
//		panel_3.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
//		dayColorPanel.add(panel_3);
//
//		JLabel normal4Label = new JLabel("DAY_NORMAL_HOUR > 0 AND <8");
//		normal4Label.setBackground(Color.LIGHT_GRAY);
//		normal4Label.setFont(new Font("黑体", Font.BOLD, 21));
//		normal4Label.setOpaque(true);
//		panel_3.add(normal4Label);
//
//		JPanel n4SelectedPanel = new JPanel();
//		panel_3.add(n4SelectedPanel);
//
//		JLabel n4SelectedLabel = new JLabel(" Selected  ");
//		n4SelectedLabel.setBackground(Color.LIGHT_GRAY);
//		n4SelectedLabel.setOpaque(true);
//		n4SelectedPanel.add(n4SelectedLabel);
//
//		n4SelectedField = new JTextField();
//		n4SelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		n4SelectedField.setEditable(false);
//		n4SelectedField.setColumns(20);
//		n4SelectedPanel.add(n4SelectedField);
//		n4SelectedField.setText(normalColorSelected.getRed() + ","
//				+ normalColorSelected.getGreen() + ","
//				+ normalColorSelected.getBlue());
//		n4SelectedField.setBackground(normalColorSelected);
//
//		n4SelectedButton = new JButton("<<");
//		n4SelectedPanel.add(n4SelectedButton);
//		n4SelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(n4SelectedButton,
//						"N>0 AND <8 Color Chooser", normalColorSelected);
//				n1SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n1SelectedField.setBackground(currentColor);
//				n2SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n2SelectedField.setBackground(currentColor);
//				n3SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n3SelectedField.setBackground(currentColor);
//				n4SelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n4SelectedField.setBackground(currentColor);
//				normalColorSelected = currentColor;
//			}
//		});
//
//		JPanel n4NoSelectedPanel = new JPanel();
//		panel_3.add(n4NoSelectedPanel);
//
//		JLabel n4NoSelectedLabel = new JLabel("NoSelected");
//		n4NoSelectedLabel.setBackground(Color.LIGHT_GRAY);
//		n4NoSelectedLabel.setOpaque(true);
//		n4NoSelectedPanel.add(n4NoSelectedLabel);
//
//		n4NoSelectedField = new JTextField();
//		n4NoSelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		n4NoSelectedField.setEditable(false);
//		n4NoSelectedField.setColumns(20);
//		n4NoSelectedPanel.add(n4NoSelectedField);
//		n4NoSelectedField.setText(normalColorNoSelectedIsNotEight.getRed()
//				+ "," + normalColorNoSelectedIsNotEight.getGreen() + ","
//				+ normalColorNoSelectedIsNotEight.getBlue());
//		n4NoSelectedField.setBackground(normalColorNoSelectedIsNotEight);
//
//		n4NoSelectedButton = new JButton("<<");
//		n4NoSelectedPanel.add(n4NoSelectedButton);
//		n4NoSelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(
//						n4NoSelectedButton, "N>0 AND <8 Color Chooser",
//						normalColorNoSelectedIsNotEight);
//				n4NoSelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				n4NoSelectedField.setBackground(currentColor);
//				normalColorNoSelectedIsNotEight = currentColor;
//			}
//		});
//
//		JPanel panel_4 = new JPanel();
//		FlowLayout flowLayout_4 = (FlowLayout) panel_4.getLayout();
//		flowLayout_4.setAlignment(FlowLayout.LEFT);
//		panel_4.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
//		dayColorPanel.add(panel_4);
//
//		JLabel overtimeLabel = new JLabel("DAY_OVERTIME_HOUR");
//		overtimeLabel.setOpaque(true);
//		overtimeLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		overtimeLabel.setBackground(Color.LIGHT_GRAY);
//		panel_4.add(overtimeLabel);
//
//		JPanel overtimeSelectedPanel = new JPanel();
//		panel_4.add(overtimeSelectedPanel);
//
//		JLabel ovetimeSelectedLabel = new JLabel(" Selected  ");
//		ovetimeSelectedLabel.setOpaque(true);
//		ovetimeSelectedLabel.setBackground(Color.LIGHT_GRAY);
//		overtimeSelectedPanel.add(ovetimeSelectedLabel);
//
//		overtimeSelectedField = new JTextField();
//		overtimeSelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		overtimeSelectedField.setEditable(false);
//		overtimeSelectedField.setColumns(20);
//		overtimeSelectedPanel.add(overtimeSelectedField);
//		overtimeSelectedField.setText(overtimeColorSelected.getRed() + ","
//				+ overtimeColorSelected.getGreen() + ","
//				+ overtimeColorSelected.getBlue());
//		overtimeSelectedField.setBackground(overtimeColorSelected);
//
//		overtimeSelectedButton = new JButton("<<");
//		overtimeSelectedPanel.add(overtimeSelectedButton);
//		overtimeSelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(
//						overtimeSelectedButton, "Overtime Color Chooser",
//						overtimeColorSelected);
//				overtimeSelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				overtimeSelectedField.setBackground(currentColor);
//				overtimeColorSelected = currentColor;
//			}
//		});
//
//		JPanel overtimeNoSelectedPanel = new JPanel();
//		panel_4.add(overtimeNoSelectedPanel);
//
//		JLabel ovetimeNoSelectedLabel = new JLabel("NoSelected");
//		ovetimeNoSelectedLabel.setOpaque(true);
//		ovetimeNoSelectedLabel.setBackground(Color.LIGHT_GRAY);
//		overtimeNoSelectedPanel.add(ovetimeNoSelectedLabel);
//
//		overtimeNoSelectedField = new JTextField();
//		overtimeNoSelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		overtimeNoSelectedField.setEditable(false);
//		overtimeNoSelectedField.setColumns(20);
//		overtimeNoSelectedPanel.add(overtimeNoSelectedField);
//		overtimeNoSelectedField.setText(overtimeColorNoSelected.getRed() + ","
//				+ overtimeColorNoSelected.getGreen() + ","
//				+ overtimeColorNoSelected.getBlue());
//		overtimeNoSelectedField.setBackground(overtimeColorNoSelected);
//
//		overtimeNoSelectedButton = new JButton("<<");
//		overtimeNoSelectedPanel.add(overtimeNoSelectedButton);
//		overtimeNoSelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(
//						overtimeNoSelectedButton, "Overtime Color Chooser",
//						overtimeColorNoSelected);
//				overtimeNoSelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				overtimeNoSelectedField.setBackground(currentColor);
//				overtimeColorNoSelected = currentColor;
//			}
//		});
//
//		JPanel panel_5 = new JPanel();
//		FlowLayout flowLayout_5 = (FlowLayout) panel_5.getLayout();
//		flowLayout_5.setAlignment(FlowLayout.LEFT);
//		panel_5.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
//		dayColorPanel.add(panel_5);
//
//		JLabel weekendLabel = new JLabel("DAY_WEEKEND_HOUR");
//		weekendLabel.setOpaque(true);
//		weekendLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		weekendLabel.setBackground(Color.LIGHT_GRAY);
//		panel_5.add(weekendLabel);
//
//		JPanel weekendSelectedPanel = new JPanel();
//		panel_5.add(weekendSelectedPanel);
//
//		JLabel weekendSelectedLabel = new JLabel(" Selected  ");
//		weekendSelectedLabel.setOpaque(true);
//		weekendSelectedLabel.setBackground(Color.LIGHT_GRAY);
//		weekendSelectedPanel.add(weekendSelectedLabel);
//
//		weekendSelectedField = new JTextField();
//		weekendSelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		weekendSelectedField.setEditable(false);
//		weekendSelectedField.setColumns(20);
//		weekendSelectedPanel.add(weekendSelectedField);
//		weekendSelectedField.setText(weekendColorSelected.getRed() + ","
//				+ weekendColorSelected.getGreen() + ","
//				+ weekendColorSelected.getBlue());
//		weekendSelectedField.setBackground(weekendColorSelected);
//
//		weekendSelectedButton = new JButton("<<");
//		weekendSelectedPanel.add(weekendSelectedButton);
//		weekendSelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(
//						weekendSelectedButton, "Weekend Color Chooser",
//						weekendColorSelected);
//				weekendSelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				weekendSelectedField.setBackground(currentColor);
//				weekendColorSelected = currentColor;
//			}
//		});
//
//		JPanel weekendNoSelectedPanel = new JPanel();
//		panel_5.add(weekendNoSelectedPanel);
//
//		JLabel weekendNoSelectedLabel = new JLabel("NoSelected");
//		weekendNoSelectedLabel.setOpaque(true);
//		weekendNoSelectedLabel.setBackground(Color.LIGHT_GRAY);
//		weekendNoSelectedPanel.add(weekendNoSelectedLabel);
//
//		weekendNoSelectedField = new JTextField();
//		weekendNoSelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		weekendNoSelectedField.setEditable(false);
//		weekendNoSelectedField.setColumns(20);
//		weekendNoSelectedPanel.add(weekendNoSelectedField);
//		weekendNoSelectedField.setText(weekendColorNoSelected.getRed() + ","
//				+ weekendColorNoSelected.getGreen() + ","
//				+ weekendColorNoSelected.getBlue());
//		weekendNoSelectedField.setBackground(weekendColorNoSelected);
//
//		weekendNoSelectedButton = new JButton("<<");
//		weekendNoSelectedPanel.add(weekendNoSelectedButton);
//		weekendNoSelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(
//						weekendNoSelectedButton, "Weekend Color Chooser",
//						weekendColorNoSelected);
//				weekendNoSelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				weekendNoSelectedField.setBackground(currentColor);
//				weekendColorNoSelected = currentColor;
//			}
//		});
//
//		JPanel panel_6 = new JPanel();
//		FlowLayout flowLayout_6 = (FlowLayout) panel_6.getLayout();
//		flowLayout_6.setAlignment(FlowLayout.LEFT);
//		panel_6.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
//		dayColorPanel.add(panel_6);
//
//		JLabel holidayLabel = new JLabel("DAY_HOLIDAY_HOUR");
//		holidayLabel.setOpaque(true);
//		holidayLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		holidayLabel.setBackground(Color.LIGHT_GRAY);
//		panel_6.add(holidayLabel);
//
//		JPanel holidaySelectedPanel = new JPanel();
//		panel_6.add(holidaySelectedPanel);
//
//		JLabel holidaySelectedLabel = new JLabel(" Selected  ");
//		holidaySelectedLabel.setOpaque(true);
//		holidaySelectedLabel.setBackground(Color.LIGHT_GRAY);
//		holidaySelectedPanel.add(holidaySelectedLabel);
//
//		holidaySelectedField = new JTextField();
//		holidaySelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		holidaySelectedField.setEditable(false);
//		holidaySelectedField.setColumns(20);
//		holidaySelectedPanel.add(holidaySelectedField);
//		holidaySelectedField.setText(holidayColorSelected.getRed() + ","
//				+ holidayColorSelected.getGreen() + ","
//				+ holidayColorSelected.getBlue());
//		holidaySelectedField.setBackground(holidayColorSelected);
//
//		holidaySelectedButton = new JButton("<<");
//		holidaySelectedPanel.add(holidaySelectedButton);
//		holidaySelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(
//						holidaySelectedButton, "Holiday Color Chooser",
//						holidayColorSelected);
//				holidaySelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				holidaySelectedField.setBackground(currentColor);
//				holidayColorSelected = currentColor;
//			}
//		});
//
//		JPanel holidayNoSelectedPanel = new JPanel();
//		panel_6.add(holidayNoSelectedPanel);
//
//		JLabel holidayNoSelectedLabel = new JLabel("NoSelected");
//		holidayNoSelectedLabel.setOpaque(true);
//		holidayNoSelectedLabel.setBackground(Color.LIGHT_GRAY);
//		holidayNoSelectedPanel.add(holidayNoSelectedLabel);
//
//		holidayNoSelectedField = new JTextField();
//		holidayNoSelectedField.setHorizontalAlignment(SwingConstants.CENTER);
//		holidayNoSelectedField.setEditable(false);
//		holidayNoSelectedField.setColumns(20);
//		holidayNoSelectedPanel.add(holidayNoSelectedField);
//		holidayNoSelectedField.setText(holidayColorNoSelected.getRed() + ","
//				+ holidayColorNoSelected.getGreen() + ","
//				+ holidayColorNoSelected.getBlue());
//		holidayNoSelectedField.setBackground(holidayColorNoSelected);
//
//		holidayNoSelectedButton = new JButton("<<");
//		holidayNoSelectedPanel.add(holidayNoSelectedButton);
//		holidayNoSelectedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(
//						holidayNoSelectedButton, "Holiday Color Chooser",
//						holidayColorNoSelected);
//				holidayColorNoSelected = currentColor;
//				holidayNoSelectedField.setText(currentColor.getRed() + ","
//						+ currentColor.getGreen() + ","
//						+ currentColor.getBlue());
//				holidayNoSelectedField.setBackground(currentColor);
//			}
//		});
//		
//		JPanel panel_7 = new JPanel();
//		FlowLayout flowLayout_8 = (FlowLayout) panel_7.getLayout();
//		flowLayout_8.setAlignment(FlowLayout.LEFT);
//		dayColorPanel.add(panel_7);
//		
//		JLabel approvedLabel = new JLabel("APPROVED_COST");
//		approvedLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		approvedLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		approvedLabel.setBackground(Color.LIGHT_GRAY);
//		approvedLabel.setOpaque(true);
//		panel_7.add(approvedLabel);
//		
//		JPanel approvedPanel = new JPanel();
//		panel_7.add(approvedPanel);
//		
//		JLabel approvedColor = new JLabel("All");
//		approvedColor.setOpaque(true);
//		approvedColor.setBackground(Color.LIGHT_GRAY);
//		approvedPanel.add(approvedColor);
//		
//		approvedField = new JTextField();
//		approvedField.setHorizontalAlignment(SwingConstants.CENTER);
//		approvedField.setEditable(false);
//		approvedPanel.add(approvedField);
//		approvedField.setColumns(20);
//		approvedField.setText(approvedCostColor.getRed() + ","
//				+ approvedCostColor.getGreen() + ","
//				+ approvedCostColor.getBlue());
//		approvedField.setBackground(approvedCostColor);
//		
//		approvedButton = new JButton("<<");
//		approvedPanel.add(approvedButton);
//		approvedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(approvedButton, "Approved Color Chooser", approvedCostColor);
//				approvedField.setText(currentColor.getRed()+","+currentColor.getGreen()+","+currentColor.getBlue());
//				approvedField.setBackground(currentColor);
//				approvedCostColor = currentColor;
//			}
//		});
//		
//		JLabel lblNewLabel_1 = new JLabel("         ");
//		panel_7.add(lblNewLabel_1);
//		
//		JLabel noApprovedLabel = new JLabel("TO_BE_APPROVED");
//		noApprovedLabel.setOpaque(true);
//		noApprovedLabel.setBackground(Color.LIGHT_GRAY);
//		noApprovedLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		panel_7.add(noApprovedLabel);
//		
//		JPanel noApprovedPanel = new JPanel();
//		panel_7.add(noApprovedPanel);
//		
//		JLabel noApprovedColor = new JLabel("All");
//		noApprovedColor.setOpaque(true);
//		noApprovedColor.setBackground(Color.LIGHT_GRAY);
//		noApprovedPanel.add(noApprovedColor);
//		
//		noApprovedField = new JTextField();
//		noApprovedField.setHorizontalAlignment(SwingConstants.CENTER);
//		noApprovedField.setEditable(false);
//		noApprovedPanel.add(noApprovedField);
//		noApprovedField.setColumns(20);
//		noApprovedField.setText(toBeApprovedColor.getRed() + ","
//				+ toBeApprovedColor.getGreen() + ","
//				+ toBeApprovedColor.getBlue());
//		noApprovedField.setBackground(toBeApprovedColor);
//		
//		noApprovedButton = new JButton("<<");
//		noApprovedPanel.add(noApprovedButton);
//		noApprovedButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(noApprovedButton, "NoApproved Color Chooser", toBeApprovedColor);
//				noApprovedField.setText(currentColor.getRed()+","+currentColor.getGreen()+","+currentColor.getBlue());
//				noApprovedField.setBackground(currentColor);
//				toBeApprovedColor = currentColor;
//			}
//		});
//		
//		JPanel panel_8 = new JPanel();
//		FlowLayout flowLayout_9 = (FlowLayout) panel_8.getLayout();
//		flowLayout_9.setAlignment(FlowLayout.LEFT);
//		dayColorPanel.add(panel_8);
//		
//		JLabel approvedHeadLabel = new JLabel("APPROVED_HEAD");
//		approvedHeadLabel.setOpaque(true);
//		approvedHeadLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		approvedHeadLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		approvedHeadLabel.setBackground(Color.LIGHT_GRAY);
//		panel_8.add(approvedHeadLabel);
//		
//		JPanel approvedHeadPanel = new JPanel();
//		panel_8.add(approvedHeadPanel);
//		
//		JLabel approved_Head_Color = new JLabel("All");
//		approved_Head_Color.setOpaque(true);
//		approved_Head_Color.setBackground(Color.LIGHT_GRAY);
//		approvedHeadPanel.add(approved_Head_Color);
//		
//		approvedHeadField = new JTextField();
//		approvedHeadField.setHorizontalAlignment(SwingConstants.CENTER);
//		approvedHeadField.setEditable(false);
//		approvedHeadPanel.add(approvedHeadField);
//		approvedHeadField.setColumns(20);
//		approvedHeadField.setText(approvedHeadColor.getRed() + ","
//				+ approvedHeadColor.getGreen() + ","
//				+ approvedHeadColor.getBlue());
//		approvedHeadField.setBackground(approvedHeadColor);
//		
//		approvedHeadButton = new JButton("<<");
//		approvedHeadPanel.add(approvedHeadButton);
//		approvedHeadButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(approvedHeadButton, "Approved Head Color Chooser", approvedHeadColor);
//				approvedHeadField.setText(currentColor.getRed()+","+currentColor.getGreen()+","+currentColor.getBlue());
//				approvedHeadField.setBackground(currentColor);
//				approvedHeadColor = currentColor;
//			}
//		});
//		
//		JLabel lblNewLabel_2 = new JLabel("         ");
//		panel_8.add(lblNewLabel_2);
//		
//		JLabel noApprovedHeadLabel = new JLabel("TO_BE_APPROVED_HEAD");
//		noApprovedHeadLabel.setOpaque(true);
//		noApprovedHeadLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		noApprovedHeadLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		noApprovedHeadLabel.setBackground(Color.LIGHT_GRAY);
//		panel_8.add(noApprovedHeadLabel);
//		
//		JPanel noApprovedHeadPanel = new JPanel();
//		panel_8.add(noApprovedHeadPanel);
//		
//		JLabel noApproved_Head_Color = new JLabel("All");
//		noApproved_Head_Color.setOpaque(true);
//		noApproved_Head_Color.setBackground(Color.LIGHT_GRAY);
//		noApprovedHeadPanel.add(noApproved_Head_Color);
//		
//		noApprovedHeadField = new JTextField();
//		noApprovedHeadField.setHorizontalAlignment(SwingConstants.CENTER);
//		noApprovedHeadField.setEditable(false);
//		noApprovedHeadPanel.add(noApprovedHeadField);
//		noApprovedHeadField.setColumns(20);
//		noApprovedHeadField.setText(toBeApprovedHeadColor.getRed() + ","
//				+ toBeApprovedHeadColor.getGreen() + ","
//				+ toBeApprovedHeadColor.getBlue());
//		noApprovedHeadField.setBackground(toBeApprovedHeadColor);
//		
//		noApprovedHeadButton = new JButton("<<");
//		noApprovedHeadPanel.add(noApprovedHeadButton);
//		noApprovedHeadButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(noApprovedHeadButton, "NoApproved Head Color Chooser", toBeApprovedHeadColor);
//				noApprovedHeadField.setText(currentColor.getRed()+","+currentColor.getGreen()+","+currentColor.getBlue());
//				noApprovedHeadField.setBackground(currentColor);
//				toBeApprovedHeadColor = currentColor;
//			}
//		});
//		
//		JPanel panel_9 = new JPanel();
//		FlowLayout flowLayout_10 = (FlowLayout) panel_9.getLayout();
//		flowLayout_10.setAlignment(FlowLayout.LEFT);
//		dayColorPanel.add(panel_9);
//		
//		JPanel assignPanel = new JPanel();
//		panel_9.add(assignPanel);
//		
//		JLabel assignLabel = new JLabel("Assign");
//		assignLabel.setOpaque(true);
//		assignLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		assignLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		assignLabel.setBackground(Color.LIGHT_GRAY);
//		assignPanel.add(assignLabel);
//		
//		assignField = new JTextField();
//		assignField.setHorizontalAlignment(SwingConstants.CENTER);
//		assignField.setEditable(false);
//		assignPanel.add(assignField);
//		assignField.setColumns(12);
//		assignField.setText(assignButtonColor.getRed() + ","
//				+ assignButtonColor.getGreen() + ","
//				+ assignButtonColor.getBlue());
//		assignField.setBackground(assignButtonColor);
//		
//		assignColorButton = new JButton("<<");
//		assignPanel.add(assignColorButton);
//		assignColorButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(assignColorButton, "AssignButton Color Chooser", assignButtonColor);
//				assignField.setText(currentColor.getRed()+","+currentColor.getGreen()+","+currentColor.getBlue());
//				assignField.setBackground(currentColor);
//				assignButtonColor = currentColor;
//			}
//		});
//		
//		JPanel clearPanel = new JPanel();
//		panel_9.add(clearPanel);
//		
//		JLabel clearLabel = new JLabel("Clear");
//		clearLabel.setOpaque(true);
//		clearLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		clearLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		clearLabel.setBackground(Color.LIGHT_GRAY);
//		clearPanel.add(clearLabel);
//		
//		clearField = new JTextField();
//		clearField.setHorizontalAlignment(SwingConstants.CENTER);
//		clearField.setEditable(false);
//		clearPanel.add(clearField);
//		clearField.setColumns(12);
//		clearField.setText(clearButtonColor.getRed() + ","
//				+ clearButtonColor.getGreen() + ","
//				+ clearButtonColor.getBlue());
//		clearField.setBackground(clearButtonColor);
//		
//		clearColorButton = new JButton("<<");
//		clearPanel.add(clearColorButton);
//		clearColorButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(clearColorButton, "ClearButton Color Chooser", clearButtonColor);
//				clearField.setText(currentColor.getRed()+","+currentColor.getGreen()+","+currentColor.getBlue());
//				clearField.setBackground(currentColor);
//				clearButtonColor = currentColor;
//			}
//		});
//		
//		JPanel addPanel = new JPanel();
//		panel_9.add(addPanel);
//		
//		JLabel addLabel = new JLabel("Add");
//		addLabel.setOpaque(true);
//		addLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		addLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		addLabel.setBackground(Color.LIGHT_GRAY);
//		addPanel.add(addLabel);
//		
//		addField = new JTextField();
//		addField.setHorizontalAlignment(SwingConstants.CENTER);
//		addField.setEditable(false);
//		addPanel.add(addField);
//		addField.setColumns(12);
//		addField.setText(addButtonColor.getRed() + ","
//				+ addButtonColor.getGreen() + ","
//				+ addButtonColor.getBlue());
//		addField.setBackground(addButtonColor);
//		
//		addColorButton = new JButton("<<");
//		addPanel.add(addColorButton);
//		addColorButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(addColorButton, "AddButton Color Chooser", addButtonColor);
//				addField.setText(currentColor.getRed()+","+currentColor.getGreen()+","+currentColor.getBlue());
//				addField.setBackground(currentColor);
//				addButtonColor = currentColor;
//			}
//		});
//		
//		JPanel deletePanel = new JPanel();
//		panel_9.add(deletePanel);
//		
//		JLabel deleteLabel = new JLabel("Delete");
//		deleteLabel.setOpaque(true);
//		deleteLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		deleteLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		deleteLabel.setBackground(Color.LIGHT_GRAY);
//		deletePanel.add(deleteLabel);
//		
//		deleteField = new JTextField();
//		deleteField.setHorizontalAlignment(SwingConstants.CENTER);
//		deleteField.setEditable(false);
//		deletePanel.add(deleteField);
//		deleteField.setColumns(12);
//		deleteField.setText(deleteButtonColor.getRed() + ","
//				+ deleteButtonColor.getGreen() + ","
//				+ deleteButtonColor.getBlue());
//		deleteField.setBackground(deleteButtonColor);
//		
//		deleteColorButton = new JButton("<<");
//		deletePanel.add(deleteColorButton);
//		deleteColorButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(deleteColorButton, "DeleteButton Color Chooser", deleteButtonColor);
//				deleteField.setText(currentColor.getRed()+","+currentColor.getGreen()+","+currentColor.getBlue());
//				deleteField.setBackground(currentColor);
//				deleteButtonColor = currentColor;
//			}
//		});
//		
//		JPanel reducePanel = new JPanel();
//		panel_9.add(reducePanel);
//		
//		JLabel reduceLabel = new JLabel("Reduce");
//		reduceLabel.setOpaque(true);
//		reduceLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		reduceLabel.setFont(new Font("黑体", Font.BOLD, 21));
//		reduceLabel.setBackground(Color.LIGHT_GRAY);
//		reducePanel.add(reduceLabel);
//		
//		reduceField = new JTextField();
//		reduceField.setHorizontalAlignment(SwingConstants.CENTER);
//		reduceField.setEditable(false);
//		reducePanel.add(reduceField);
//		reduceField.setColumns(12);
//		reduceField.setText(reduceButtonColor.getRed() + ","
//				+ reduceButtonColor.getGreen() + ","
//				+ reduceButtonColor.getBlue());
//		reduceField.setBackground(reduceButtonColor);
//		
//		reduceColorButton = new JButton("<<");
//		reducePanel.add(reduceColorButton);
//		reduceColorButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color currentColor = JColorChooser.showDialog(reduceColorButton, "ReduceButton Color Chooser", reduceButtonColor);
//				reduceField.setText(currentColor.getRed()+","+currentColor.getGreen()+","+currentColor.getBlue());
//				reduceField.setBackground(currentColor);
//				reduceButtonColor = currentColor;
//			}
//		});
//
//		JPanel buttonPanel = new JPanel();
//		FlowLayout flowLayout_7 = (FlowLayout) buttonPanel.getLayout();
//		flowLayout_7.setAlignment(FlowLayout.LEFT);
//		thirdPanel.add(buttonPanel, BorderLayout.SOUTH);
//
//		JButton updateButton = new JButton("UpdateColor");
//		buttonPanel.add(updateButton);
//		updateButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				try {
//					String[] insert = {
//							"CanEditColor=true",
//							"normalColor=" + normalColor.getRed() + ","
//									+ normalColor.getGreen() + ","
//									+ normalColor.getBlue(),
//							"overtimeColor=" + overtimeColor.getRed() + ","
//									+ overtimeColor.getGreen() + ","
//									+ overtimeColor.getBlue(),
//							"weekendColor=" + weekendColor.getRed() + ","
//									+ weekendColor.getGreen() + ","
//									+ weekendColor.getBlue(),
//							"holidayColor=" + holidayColor.getRed() + ","
//									+ holidayColor.getGreen() + ","
//									+ holidayColor.getBlue(),
//							"normalColorSelected="
//									+ normalColorSelected.getRed() + ","
//									+ normalColorSelected.getGreen() + ","
//									+ normalColorSelected.getBlue(),
//							"normalColorNoSelectedIsNull="
//									+ normalColorNoSelectedIsNull.getRed()
//									+ ","
//									+ normalColorNoSelectedIsNull.getGreen()
//									+ ","
//									+ normalColorNoSelectedIsNull.getBlue(),
//							"normalColorNoSelectedIsZero="
//									+ normalColorNoSelectedIsZero.getRed()
//									+ ","
//									+ normalColorNoSelectedIsZero.getGreen()
//									+ ","
//									+ normalColorNoSelectedIsZero.getBlue(),
//							"normalColorNoSelectedIsEight="
//									+ normalColorNoSelectedIsEight.getRed()
//									+ ","
//									+ normalColorNoSelectedIsEight.getGreen()
//									+ ","
//									+ normalColorNoSelectedIsEight.getBlue(),
//							"normalColorNoSelectedIsNotEight="
//									+ normalColorNoSelectedIsNotEight.getRed()
//									+ ","
//									+ normalColorNoSelectedIsNotEight
//											.getGreen() + ","
//									+ normalColorNoSelectedIsNotEight.getBlue(),
//							"overtimeColorSelected="
//									+ overtimeColorSelected.getRed() + ","
//									+ overtimeColorSelected.getGreen() + ","
//									+ overtimeColorSelected.getBlue(),
//							"overtimeColorNoSelected="
//									+ overtimeColorNoSelected.getRed() + ","
//									+ overtimeColorNoSelected.getGreen() + ","
//									+ overtimeColorNoSelected.getBlue(),
//							"weekendColorSelected="
//									+ weekendColorSelected.getRed() + ","
//									+ weekendColorSelected.getGreen() + ","
//									+ weekendColorSelected.getBlue(),
//							"weekendColorNoSelected="
//									+ weekendColorNoSelected.getRed() + ","
//									+ weekendColorNoSelected.getGreen() + ","
//									+ weekendColorNoSelected.getBlue(),
//							"holidayColorSelected="
//									+ holidayColorSelected.getRed() + ","
//									+ holidayColorSelected.getGreen() + ","
//									+ holidayColorSelected.getBlue(),
//							"holidayColorNoSelected="
//									+ holidayColorNoSelected.getRed() + ","
//									+ holidayColorNoSelected.getGreen() + ","
//									+ holidayColorNoSelected.getBlue(),
//							"approvedCostColor="
//									+ approvedCostColor.getRed() + ","
//									+ approvedCostColor.getGreen() + ","
//									+ approvedCostColor.getBlue(),
//							"toBeApprovedColor="
//									+ toBeApprovedColor.getRed() + ","
//									+ toBeApprovedColor.getGreen() + ","
//									+ toBeApprovedColor.getBlue(),
//							"approvedHeadColor="
//									+ approvedHeadColor.getRed() + ","
//									+ approvedHeadColor.getGreen() + ","
//									+ approvedHeadColor.getBlue(),
//							"toBeApprovedHeadColor="
//									+ toBeApprovedHeadColor.getRed() + ","
//									+ toBeApprovedHeadColor.getGreen() + ","
//									+ toBeApprovedHeadColor.getBlue(),
//							"assignButtonColor="
//									+ assignButtonColor.getRed() + ","
//									+ assignButtonColor.getGreen() + ","
//									+ assignButtonColor.getBlue(),
//							"clearButtonColor="
//									+ clearButtonColor.getRed() + ","
//									+ clearButtonColor.getGreen() + ","
//									+ clearButtonColor.getBlue(),
//							"addButtonColor="
//									+ addButtonColor.getRed() + ","
//									+ addButtonColor.getGreen() + ","
//									+ addButtonColor.getBlue(),
//							"deleteButtonColor="
//									+ deleteButtonColor.getRed() + ","
//									+ deleteButtonColor.getGreen() + ","
//									+ deleteButtonColor.getBlue(),
//							"reduceButtonColor="
//									+ reduceButtonColor.getRed() + ","
//									+ reduceButtonColor.getGreen() + ","
//									+ reduceButtonColor.getBlue()};
//					tcsession.getPreferenceService().setStringArray(4,
//							"YFJC_Ext2SupportForm_Color", insert);
//				} catch (TCException e1) {
//					e1.printStackTrace();
//				}
//
//			}
//		});
//
//		JButton reloadButton = new JButton("ReloadColor");
//		buttonPanel.add(reloadButton);
//		reloadButton.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				setPreferenceColor();
//
//			}
//		});
//
//		buttonPanel.add(new JLabel("       "));
//		JTextField noteField = new JTextField(
//				"The Preference name is 'YFJC_Ext2SupportForm_Color'");
//		noteField.setEditable(false);
//		buttonPanel.add(noteField);
//
//	}
//
//	@Override
//	public void loadRendering() throws TCException {
//		pw
//				.println("****************************loadRendering********************************");
//		pw.flush();
//		byPass(true);
//		TCComponent[] extComs = form
//				.getReferenceListProperty("jci6_Ext2Detail");
//		byPass(false);
//		data1.clear();
//		data2.clear();
//		data.clear();
//
//		if (extComs == null || extComs.length == 0) {
//
//		} else {
//			Vector<Integer> rowCount_native_V = new Vector<Integer>();
//			for (int i = 0; i < extComs.length; i++) {
//				String tmpName = extComs[i].getStringProperty("object_name");
//				if (tmpName.startsWith("Head")) {
//					rowCount_native_V.add(i);
//				}
//			}
//			int rowCount_last = extComs.length;
//			for (int i = 0; i < rowCount_native_V.size(); i++) {
//				int tmprowCount = rowCount_native_V.get(i);
//				if (i != rowCount_native_V.size() - 1) {
//					int tmpnextrowCount = rowCount_native_V.get(i + 1);
//					Vector data0_1 = new Vector();
//					// 固定列
//					Vector data1_1 = new Vector();
//					String tmpExtName = extComs[tmprowCount]
//							.getStringProperty("jci6_ExtName");
//					String tmpGID = extComs[tmprowCount]
//							.getStringProperty("jci6_GID");
//					data1_1.add(tmpExtName);
//					data1_1.add(tmpGID);
//					data1.add(data1_1);
//					data0_1.add(extComs[tmprowCount]);
//					data0_1.add(extComs[tmprowCount]);
//					// 移动列
//					Vector data2_1 = new Vector();
//					for (int i1 = 0; i1 < moveColumnName.size(); i1++) {
//						if (i1 == 0) {
//							String tmpCompany = extComs[tmprowCount]
//									.getStringProperty("jci6_Company");
//							data2_1.add(tmpCompany);
//							data0_1.add(extComs[tmprowCount]);
//						} else if (i1 == 1) {
//							byPass(true);
//							TCComponent tmpProgram = extComs[tmprowCount]
//									.getReferenceProperty("jci6_Program");
//							byPass(false);
//							if (tmpProgram == null) {
//								data2_1.add("");
//							} else {
//								String tmpProgramID = tmpProgram
//										.getStringProperty("item_id");
//								String tmpProgramName = tmpProgram
//										.getStringProperty("object_name");
//								data2_1.add(tmpProgramID + "_" + tmpProgramName);
//							}
//							data0_1.add(extComs[tmprowCount]);
//						} else if (i1 == 2) {
//							Date tmpOnboard = extComs[tmprowCount]
//									.getDateProperty("jci6_Onboard");
//							SimpleDateFormat smf = new SimpleDateFormat(
//									"yyyy/MM/dd");
//							if (tmpOnboard != null) {
//								data2_1.add(smf.format(tmpOnboard));
//							} else {
//								data2_1.add("");
//							}
//							data0_1.add(extComs[tmprowCount]);
//						} else if (i1 > 2 && i1 < 7) {
//							data2_1.add("");
//							data0_1.add("");
//						} else if (i1 >= 7) {
//							boolean hasItem = false;
//							for (int j = tmprowCount + 1; j < tmpnextrowCount; j++) {
//								TCComponent tmoCom = extComs[j];
//								String tmoCom_name = tmoCom
//										.getStringProperty("object_name");
////								int tmpDay = Integer.parseInt(tmoCom_name
////										.split("_")[5]);
//								String[] tmoCom_name_splits = tmoCom_name.split("_");
//								int tmpDay = Integer.parseInt(tmoCom_name_splits[tmoCom_name_splits.length-1]);								
//								if (i1 - 6 == tmpDay) {
//									data0_1.add(tmoCom);
//									Double tmpHours = tmoCom
//											.getDoubleProperty("jci6_Hours");
//									data2_1.add(tmpHours.toString());
//									hasItem = true;
//									break;
//								}
//							}
//							if (!hasItem) {
//								data2_1.add("");
//								data0_1.add("");
//							}
//						}
//					}
//					data2.add(data2_1);
//					data.add(data0_1);
//				} else {
//					if (tmprowCount < rowCount_last) {
//						int tmpnextrowCount = rowCount_last;
//						Vector data0_1 = new Vector();
//						// 固定列
//						Vector data1_1 = new Vector();
//						String tmpExtName = extComs[tmprowCount]
//								.getStringProperty("jci6_ExtName");
//						String tmpGID = extComs[tmprowCount]
//								.getStringProperty("jci6_GID");
//						data1_1.add(tmpExtName);
//						data1_1.add(tmpGID);
//						data1.add(data1_1);
//						data0_1.add(extComs[tmprowCount]);
//						data0_1.add(extComs[tmprowCount]);
//						// 移动列
//						Vector data2_1 = new Vector();
//						for (int i1 = 0; i1 < moveColumnName.size(); i1++) {
//							if (i1 == 0) {
//								String tmpCompany = extComs[tmprowCount]
//										.getStringProperty("jci6_Company");
//								data2_1.add(tmpCompany);
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 == 1) {
//								byPass(true);
//								TCComponent tmpProgram = extComs[tmprowCount]
//										.getReferenceProperty("jci6_Program");
//								byPass(false);
//								if (tmpProgram == null) {
//									data2_1.add("");
//								} else {
//									String tmpProgramID = tmpProgram
//											.getStringProperty("item_id");
//									String tmpProgramName = tmpProgram
//											.getStringProperty("object_name");
//									data2_1.add(tmpProgramID + "_"
//											+ tmpProgramName);
//								}
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 == 2) {
//								Date tmpOnboard = extComs[tmprowCount]
//										.getDateProperty("jci6_Onboard");
//								SimpleDateFormat smf = new SimpleDateFormat(
//										"yyyy/MM/dd");
//								if (tmpOnboard != null) {
//									data2_1.add(smf.format(tmpOnboard));
//								} else {
//									data2_1.add("");
//								}
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 > 2 && i1 < 7) {
//								data2_1.add("");
//								data0_1.add("");
//							} else if (i1 >= 7) {
//								boolean hasItem = false;
//								for (int j = tmprowCount + 1; j < tmpnextrowCount; j++) {
//									TCComponent tmoCom = extComs[j];
//									String tmoCom_name = tmoCom
//											.getStringProperty("object_name");
////									int tmpDay = Integer.parseInt(tmoCom_name
////											.split("_")[5]);
//									String[] tmoCom_name_splits = tmoCom_name.split("_");
//									int tmpDay = Integer.parseInt(tmoCom_name_splits[tmoCom_name_splits.length-1]);									
//									if (i1 - 6 == tmpDay) {
//										Double tmpHours = tmoCom
//												.getDoubleProperty("jci6_Hours");
//										data0_1.add(tmoCom);
//										data2_1.add(tmpHours.toString());
//										hasItem = true;
//										break;
//									}
//								}
//								if (!hasItem) {
//									data2_1.add("");
//									data0_1.add("");
//								}
//							}
//						}
//						data2.add(data2_1);
//						data.add(data0_1);
//					} else if (tmprowCount == rowCount_last) {
//						int tmpnextrowCount = rowCount_last;
//						Vector data0_1 = new Vector();
//						// 固定列
//						Vector data1_1 = new Vector();
//						String tmpExtName = extComs[tmprowCount]
//								.getStringProperty("jci6_ExtName");
//						String tmpGID = extComs[tmprowCount]
//								.getStringProperty("jci6_GID");
//						data1_1.add(tmpExtName);
//						data1_1.add(tmpGID);
//						data1.add(data1_1);
//						data0_1.add(extComs[tmprowCount]);
//						data0_1.add(extComs[tmprowCount]);
//						// 移动列
//						Vector data2_1 = new Vector();
//						for (int i1 = 0; i1 < moveColumnName.size(); i1++) {
//							if (i1 == 0) {
//								String tmpCompany = extComs[tmprowCount]
//										.getStringProperty("jci6_Company");
//								data2_1.add(tmpCompany);
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 == 1) {
//								byPass(true);
//								TCComponent tmpProgram = extComs[tmprowCount]
//										.getReferenceProperty("jci6_Program");
//								byPass(false);
//								if (tmpProgram == null) {
//									data2_1.add("");
//								} else {
//									String tmpProgramID = tmpProgram
//											.getStringProperty("item_id");
//									String tmpProgramName = tmpProgram
//											.getStringProperty("object_name");
//									data2_1.add(tmpProgramID + "_"
//											+ tmpProgramName);
//								}
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 == 2) {
//								Date tmpOnboard = extComs[tmprowCount]
//										.getDateProperty("jci6_Onboard");
//								SimpleDateFormat smf = new SimpleDateFormat(
//										"yyyy/MM/dd");
//								if (tmpOnboard != null) {
//									data2_1.add(smf.format(tmpOnboard));
//								} else {
//									data2_1.add("");
//								}
//								data0_1.add(extComs[tmprowCount]);
//							} else if (i1 > 2 && i1 < 7) {
//								data2_1.add("");
//								data0_1.add("");
//							} else if (i1 >= 7) {
//								data2_1.add("");
//								data0_1.add("");
//							}
//						}
//						data2.add(data2_1);
//						data.add(data0_1);
//					}
//				}
//			}
//		}
//		if (data1.size() == 0) {
//			// 添加空白行
//			for (int i = 0; i < 1; i++) {
//				Vector date1_1 = new Vector();
//				date1_1.add("");
//				date1_1.add("");
//				data1.add(date1_1);
//				Vector date2_1 = new Vector();
//				for (int j = 0; j < moveColumnName.size(); j++) {
//					if (i == 0) {
//						date2_1.add("");
//					} else if (i == 1) {
//						date2_1.add("");
//					} else if (i == 2) {
//						date2_1.add("");
//					} else if (i > 2 && i < 7) {
//						date2_1.add("");
//					} else if (i >= 7) {
//						date2_1.add("");
//					}
//				}
//				data2.add(date2_1);
//			}
//		}
//		/*
//		 * 处理data，为passMap、cellTypeMap_Column赋值
//		 */
//		if (!data.isEmpty()) {
//			for (int rowCount = 0; rowCount < data.size(); rowCount++) {
//				Vector ColumnV = (Vector) data.get(rowCount);
//				for (int columnCount = 0; columnCount < ColumnV.size(); columnCount++) {
//					Object obj = ColumnV.get(columnCount);
//					if (obj instanceof TCComponent) {
//						// 为cellTypeMap、passMap赋值
//						Boolean ispass = JCITableUtil.isPass((TCComponent) obj,form);
//						String tmpType = ((TCComponent) obj)
//								.getStringProperty("jci6_BillType");
//						// pw.println(rowCount+","+columnCount+"=="+tmpType);
//						if (columnCount < 2) {
//							if (tmpType.equals(normalType)
//									|| tmpType.equals(overtimeType)
//									|| tmpType.equals(holidayType)
//									|| tmpType.equals(weekendType)) {
//
//								cellTypeMap_fix.put(rowCount + ","
//										+ columnCount, tmpType);
//							}
//							passMap_fix.put(rowCount + "," + columnCount,
//									ispass);
//						} else {
//							if (tmpType.equals(normalType)
//									|| tmpType.equals(overtimeType)
//									|| tmpType.equals(holidayType)
//									|| tmpType.equals(weekendType)) {
//
//								cellTypeMap_move.put(rowCount + ","
//										+ (columnCount - 2), tmpType);
//							}
//							passMap_move.put(
//									rowCount + "," + (columnCount - 2), ispass);
//						}
//					}
//				}
//			}
//		}
//		reloadModule();
//		allTableCount();
//		allCount();
//	}
//
//	/**
//	 * 计算每行N、O、W、H的合计 打算通过触发器来进行计算，方法是先对某单元格赋值，后还原值
//	 */
//	private void allTableCount() {
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				{
//					Vector moveData = ((DefaultTableModel) moveTable.getModel())
//							.getDataVector();
//					for (int rowCount = 0; rowCount < moveData.size(); rowCount++) {
//						Vector currentRowValues = (Vector) moveData
//								.get(rowCount);
//						// 获取入职时间来作为是具体人员工时的判断
//						String cell2 = currentRowValues.get(2).toString()
//								.trim();
//						if (cell2.length() > 3) {
//							// 此行是具体人员的工时，可以进行工时的合计
//							String cell9 = currentRowValues.get(9).toString()
//									.trim();
//							// 赋值：
//							((DefaultTableModel) moveTable.getModel())
//									.setValueAt("1", rowCount, 9);
//							((DefaultTableModel) moveTable.getModel())
//									.setValueAt(cell9, rowCount, 9);
//						}
//					}
//				}
//			}
//		}).start();
//
//	}
//
//	/**
//	 * 合计所有的N、O、W、H的合计和费用
//	 */
//	private void allCount() {
//		Double approvedNormal = 0.0d;
//		Double approvedOvertime = 0.0d;
//		Double approvedHoliday = 0.0d;
//		Double approvedWeekend = 0.0d;
//		Double noApprovedNormal = 0.0d;
//		Double noApprovedOvertime = 0.0d;
//		Double noApprovedHoliday = 0.0d;
//		Double noApprovedWeekend = 0.0d;
//		Double approvedCost = 0.0d;
//		Double noApprovedCost = 0.0d;
//		Vector dataMove = ((DefaultTableModel) moveTable.getModel())
//				.getDataVector();
//		for (int i = 0; i < dataMove.size(); i++) {
//			Vector rowVector = (Vector) dataMove.get(i);
//			String tmpName = fixTable.getValueAt(i, 0).toString();
//			String currentConmponyName = rowVector.get(0).toString().trim(); // 外服公司
//			pw.println("tmpName:" + tmpName
//					+ "&&& currentConmponyName:" + currentConmponyName);
//			pw.flush();
//			if (tmpName.length() < 2 || currentConmponyName.length() < 2) {
//				continue;
//			}
//			for (int j = 7; j < rowVector.size(); j++) {
//				Double currentValue = 0.0d;
//				String tmpV = rowVector.get(j).toString().trim();
//				if (!tmpV.isEmpty()) {
//					currentValue = Double.parseDouble(tmpV);
//				}
//				Boolean ispass = passMap_move.get(i + "," + j);
//				if (ispass == null) {
//					ispass = false;
//				}
//				String currentType = cellTypeMap_move.get(i + "," + j);
//				if (currentType == null) {
//					continue;
//				}
//				if (ispass) {
//					if (currentType.contains("Normal")) {
//						approvedNormal += currentValue;
//					} else if (currentType.contains("Over")) {
//						approvedOvertime += currentValue;
//					} else if (currentType.contains("Holiday")) {
//						approvedHoliday += currentValue;
//					} else if (currentType.contains("Weekend")) {
//						approvedWeekend += currentValue;
//					}
//					String currentModifier = getModifier(currentConmponyName,
//							currentType);
////					pw.println(i + "," + j + "=====" + currentModifier);
//					if (data.size() > i) {
//						TCComponent com = (TCComponent) ((Vector) data
//								.elementAt(i)).elementAt(0);
//						approvedCost += getCurrentCount(currentValue,
//								currentModifier, tmpName, com);
//
//					} else {
//						approvedCost += getCurrentCount(currentValue,
//								currentModifier, tmpName, null);
//					}
//				} else {
//					if (currentType.contains("Normal")) {
//						noApprovedNormal += currentValue;
//					} else if (currentType.contains("Over")) {
//						noApprovedOvertime += currentValue;
//					} else if (currentType.contains("Holiday")) {
//						noApprovedHoliday += currentValue;
//					} else if (currentType.contains("Weekend")) {
//						noApprovedWeekend += currentValue;
//					}
//					String currentModifier = getModifier(currentConmponyName,
//							currentType);
////					pw.println(i + "," + j + "=====" + currentModifier);
//					if (data.size() > i) {
//						TCComponent com = (TCComponent) ((Vector) data
//								.elementAt(i)).elementAt(0);
//						noApprovedCost += getCurrentCount(currentValue,
//								currentModifier, tmpName, com);
//
//					} else {
//						if (extTSEAttrList == null || extTSEAttrList.size() == 0) {
//							TCComponent com = null;
//							for (int k = 0; k < data.size(); k++) {
//								String tmpqq = fixTable.getValueAt(k, 0).toString();
//								if (tmpqq.trim().equals(tmpName.trim())) {
//									com = (TCComponent) ((Vector) data
//											.elementAt(k)).elementAt(0);
//								}
//							}
//							noApprovedCost += getCurrentCount(currentValue,
//									currentModifier, tmpName, com);
//						}else{
//							noApprovedCost += getCurrentCount(currentValue,
//									currentModifier, tmpName, null);
//						}
//					}
//				}
//			}
//		}
//
//		// table0.setModel(new DefaultTableModel(
//		// new Object[][] {
//		// {"", "Cost", "N", "O", "W", "H"},
//		// {"Approved Cost", approvedCost, approvedNormal, approvedOvertime,
//		// approvedWeekend,approvedHoliday},
//		// {"To be Approved", noApprovedCost, noApprovedNormal,
//		// noApprovedOvertime, noApprovedWeekend, noApprovedHoliday},
//		// },
//		// new String[] {
//		// "", "", "N", "O", "W", "H"
//		// }
//		// ){
//		// @Override
//		// public boolean isCellEditable(int row, int column) {
//		// return false;
//		// }
//		// });
//		Object[][] dataVector = {
//				{ "", "Cost", "N", "O", "W", "H" },
//				{ "Approved Cost", approvedCost, approvedNormal,
//						approvedOvertime, approvedWeekend, approvedHoliday },
//				{ "To be Approved", noApprovedCost, noApprovedNormal,
//						noApprovedOvertime, noApprovedWeekend,
//						noApprovedHoliday }, };
//		String[] columnIdentifiers = { "", "", "N", "O", "W", "H" };
//		((DefaultTableModel) table0.getModel()).setDataVector(dataVector,
//				columnIdentifiers);
//		DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
//			@Override
//			public void setHorizontalAlignment(int alignment) {
//				// TODO Auto-generated method stub
//				super.setHorizontalAlignment(JLabel.CENTER);
//			}
//
//			@Override
//			public Component getTableCellRendererComponent(JTable table,
//					Object value, boolean isSelected, boolean hasFocus,
//					int row, int column) {
//				// TODO Auto-generated method stub
//				Component com = super.getTableCellRendererComponent(table,
//						value, isSelected, hasFocus, row, column);
//				if (!isSelected) {
//					if (row == 0 && column == 2) {
//						com.setBackground(normalColor);
//					}else if (row == 0 && column == 3) {
//						com.setBackground(overtimeColor);
//					}else if (row == 0 && column == 4) {
//						com.setBackground(weekendColor);
//					}else if (row == 0 && column == 5) {
//						com.setBackground(holidayColor);
//					}else if (row == 1 && column == 0) {
//						com.setBackground(approvedCostColor);
//					}else if (row == 2 && column == 0) {
//						com.setBackground(toBeApprovedColor);
//					}else{
//						com.setBackground(Color.WHITE);
//					}
//				}
//				return com;
//			}
//		};
//		table0.getColumnModel().getColumn(0).setCellRenderer(r);
//		table0.getColumnModel().getColumn(1).setCellRenderer(r);
//		table0.getColumnModel().getColumn(2).setCellRenderer(r);
//		table0.getColumnModel().getColumn(3).setCellRenderer(r);
//		table0.getColumnModel().getColumn(4).setCellRenderer(r);
//		table0.getColumnModel().getColumn(5).setCellRenderer(r);
//		// {42,148,100,178,94,75,36,36,36,36,33};
//		table0.getColumnModel().getColumn(0).setPreferredWidth(130);
//		table0.getColumnModel().getColumn(1).setPreferredWidth(94);
//		table0.getColumnModel().getColumn(2).setPreferredWidth(55);
//		table0.getColumnModel().getColumn(3).setPreferredWidth(55);
//		table0.getColumnModel().getColumn(4).setPreferredWidth(55);
//		table0.getColumnModel().getColumn(5).setPreferredWidth(55);
//	}
//
//	/**
//	 * 计算当前值的Cost
//	 * 
//	 * @param currentValue
//	 * @param currentModifier
//	 * @param tmpName
//	 * @return
//	 */
//	private Double getCurrentCount(Double currentValue, String currentModifier,
//			String tmpName, TCComponent com) {
//		Double modifier = 0.0d;
//		{
//			String currentModifierD = currentModifier.replace("x", "").trim();
//			currentModifierD = currentModifierD.replace("X", "").trim();
//			modifier = Double.parseDouble(currentModifierD);
//		}
//		for (Object[] objs : extTSEAttrList) {
//			if (objs[3].equals(tmpName)) {
//				Double currentRate = (Double) objs[5];
//				return currentValue * currentRate * modifier;
//			}
//		}
//
//		if (com != null) {
//			try {
//				Double currentRate = com.getDoubleProperty("jci6_Rate");
//				return currentValue * currentRate * modifier;
//			} catch (TCException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return 0.0d;
//	}
//	
//	
//	@Override
//	public boolean isObjectSavable() {
//		boolean ifail = fixTable.getCellEditor() == null ? true : fixTable
//				.getCellEditor().stopCellEditing();
//		boolean ifail1 = moveTable.getCellEditor() == null ? true : moveTable
//				.getCellEditor().stopCellEditing();
//		int rowCount = moveTable.getRowCount();
//		for (int i = 0; i < rowCount; i++) {
//			String name = fixTable.getValueAt(i, 0).toString();
//			String programInfo = moveTable.getValueAt(i, 1).toString();
//			if (!name.trim().isEmpty() && programInfo.trim().isEmpty()) {
//				MessageBox.post("检查：Program不能为空", "WARNING",
//						MessageBox.WARNING);
//				return false;
//			}
//		}
//		return super.isObjectSavable();
//	}
//	@Override
//	public boolean isObjectSavable(boolean arg0) {
//		boolean ifail = fixTable.getCellEditor() == null ? true : fixTable
//				.getCellEditor().stopCellEditing();
//		boolean ifail1 = moveTable.getCellEditor() == null ? true : moveTable
//				.getCellEditor().stopCellEditing();
//		int rowCount = moveTable.getRowCount();
//		for (int i = 0; i < rowCount; i++) {
//			String name = fixTable.getValueAt(i, 0).toString();
//			String programInfo = moveTable.getValueAt(i, 1).toString();
//			if (!name.trim().isEmpty() && programInfo.trim().isEmpty()) {
//				MessageBox.post("检查：Program不能为空", "WARNING",
//						MessageBox.WARNING);
//				return false;
//			}
//		}
//		return super.isObjectSavable(arg0);
//	}
//	@Override
//	public boolean isObjectSavable(boolean arg0, boolean arg1) {
//		boolean ifail = fixTable.getCellEditor() == null ? true : fixTable
//				.getCellEditor().stopCellEditing();
//		boolean ifail1 = moveTable.getCellEditor() == null ? true : moveTable
//				.getCellEditor().stopCellEditing();
//		int rowCount = moveTable.getRowCount();
//		for (int i = 0; i < rowCount; i++) {
//			String name = fixTable.getValueAt(i, 0).toString();
//			String programInfo = moveTable.getValueAt(i, 1).toString();
//			if (!name.trim().isEmpty() && programInfo.trim().isEmpty()) {
//				MessageBox.post("检查：Program不能为空", "WARNING",
//						MessageBox.WARNING);
//				return false;
//			}
//		}
//		return super.isObjectSavable(arg0, arg1);
//	}
//
//	@Override
//	public void saveRendering() {
//		boolean ifail = fixTable.getCellEditor() == null ? true : fixTable
//				.getCellEditor().stopCellEditing();
//		boolean ifail1 = moveTable.getCellEditor() == null ? true : moveTable
//				.getCellEditor().stopCellEditing();
//		
//		int rowCount = moveTable.getRowCount();
//		for (int i = 0; i < rowCount; i++) {
//			String name = fixTable.getValueAt(i, 0).toString();
//			String programInfo = moveTable.getValueAt(i, 1).toString();
//			if (!name.trim().isEmpty() && programInfo.trim().isEmpty()) {
//				MessageBox.post("检查：Program不能为空", "WARNING",
//						MessageBox.WARNING);
//				return ;
//			}
//		}
//		
//		
//		try {
//			byPass(true);
//			pw.println("111111111111:"+form.getReferenceListProperty("jci6_Ext2Detail").length);
//			pw.flush();
//			TCProperty bb = form.getTCProperty("jci6_Ext2Detail");
//			TCComponent[] formexts = bb.getReferenceValueArray();
//			data_form = new Vector();
//			for (TCComponent tcComponent : formexts) {
//				data_form.add(tcComponent);
//			}
//			bb.setReferenceValueArray(null);
//			form.save();
//			form.refresh();
//			pw.println("2222222222222:"+form.getReferenceListProperty("jci6_Ext2Detail").length);
//			pw.flush();
//			byPass(false);
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		
//		{
//			//删除需要删除的明细
////			deleteExts
//			if (deleteExts != null && deleteExts.size() > 0) {
//				pw.println("deleteExts存在值");
//				pw.flush();
////				TCReservationService service = tcsession.getReservationService();
////				try {
////					service.reserve(form);
////				} catch (TCException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}// 签出对象
//				
//					try {
//						byPass(true);
//						for (Object iterable_element : deleteExts) {
//							TCComponent aa = (TCComponent) iterable_element;
//							aa.delete();
//						}
//						byPass(false);
//					} catch (TCException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
////				try {
////					service.unreserve(form);
////				} catch (TCException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}// 签入对象
//			}
//		}
////		moveTable.p
//		int movecount = moveTable.getColumnCount();
//		moveTable.getSelectionModel().setSelectionInterval(rowCount - 1, rowCount - 1);
//	    Rectangle rect = moveTable.getCellRect(rowCount - 1, movecount-1, true);
//	    moveTable.updateUI();
//	    moveTable.scrollRectToVisible(rect);
////		((DefaultTableModel) fixTable.getModel()).fireTableDataChanged();
////		((DefaultTableModel) moveTable.getModel()).fireTableDataChanged();
//		data1 = ((DefaultTableModel) fixTable.getModel()).getDataVector();
//		data2 = ((DefaultTableModel) moveTable.getModel()).getDataVector();
//		Vector finalVector = new Vector();
//		for (int i = 0; i < data1.size(); i++) {
//			if (i < data.size()) {
//				// Fix
//				TCComponent com = (TCComponent) ((Vector) data.elementAt(i))
//						.elementAt(0);
//				// Mov
//				String tmpProgram = ((Vector) data2.elementAt(i)).elementAt(1)
//						.toString();
//				try {
//					TCComponent programinfo = getProgramInfo(tmpProgram);
//					if (programinfo != null) {
//						com.setReferenceProperty("jci6_Program", programinfo);
//					}
//					finalVector.add(com);
//				} catch (TCException e) {
//					e.printStackTrace();
//				}
//				// 8~
//				for (int j = 7; j < moveColumnName.size(); j++) {
//					String currentValue = ((Vector) data2.elementAt(i))
//							.elementAt(j).toString();
//					// Color backColor = rColor.get(i+","+(j+2));
//					Color backColor = rColor.get(i + "," + j);
//					if (backColor != null) {
//						if (backColor.equals(normalColorSelected)
//								|| backColor
//										.equals(normalColorNoSelectedIsNull)
//								|| backColor
//										.equals(normalColorNoSelectedIsZero)
//								|| backColor
//										.equals(normalColorNoSelectedIsEight)
//								|| backColor
//										.equals(normalColorNoSelectedIsNotEight)) {
//							Object com1_obj = ((Vector) data.elementAt(i))
//									.elementAt(j + 2);
//							if (com1_obj != null
//									&& com1_obj instanceof TCComponent) {
//								TCComponent com1 = (TCComponent) com1_obj;
//								TCComponent programinfo = getProgramInfo(tmpProgram);
//								if (programinfo != null) {
//									try {
//										com1.setReferenceProperty(
//												"jci6_Program", programinfo);
//									} catch (TCException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								}
//								finalVector.add(com1);
//							} else {
//								TCComponent com1 = createFollowCom(com,
//										currentValue, normalType, j - 6);
//								if (com1 != null) {
//									TCComponent programinfo = getProgramInfo(tmpProgram);
//									if (programinfo != null) {
//										try {
//											com1.setReferenceProperty(
//													"jci6_Program", programinfo);
//										} catch (TCException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
//									}
//									finalVector.add(com1);
//								}
//							}
//							// TCComponent com1 = (TCComponent)
//							// ((Vector)data.elementAt(i)).elementAt(j+2);
//
//						} else if (backColor.equals(overtimeColorSelected)
//								|| backColor.equals(overtimeColorNoSelected)) {
//							// TCComponent com1 = (TCComponent)
//							// ((Vector)data.elementAt(i)).elementAt(j+2);
//							// if (com1 == null) {
//							// com1 = createFollowCom(com, currentValue,
//							// overtimeType, j-7);
//							//
//							// }
//							// if (com1 != null) {
//							// finalVector.add(com1);
//							// }
//							Object com1_obj = ((Vector) data.elementAt(i))
//									.elementAt(j + 2);
//							if (com1_obj != null
//									&& com1_obj instanceof TCComponent) {
//								TCComponent com1 = (TCComponent) com1_obj;
//								TCComponent programinfo = getProgramInfo(tmpProgram);
//								if (programinfo != null) {
//									try {
//										com1.setReferenceProperty(
//												"jci6_Program", programinfo);
//									} catch (TCException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								}
//								finalVector.add(com1);
//							} else {
//								TCComponent com1 = createFollowCom(com,
//										currentValue, overtimeType, j - 6);
//								if (com1 != null) {
//									TCComponent programinfo = getProgramInfo(tmpProgram);
//									if (programinfo != null) {
//										try {
//											com1.setReferenceProperty(
//													"jci6_Program", programinfo);
//										} catch (TCException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
//									}
//									finalVector.add(com1);
//								}
//							}
//						} else if (backColor.equals(weekendColorSelected)
//								|| backColor.equals(weekendColorNoSelected)) {
//							// TCComponent com1 = (TCComponent)
//							// ((Vector)data.elementAt(i)).elementAt(j+2);
//							// if (com1 == null) {
//							// com1 = createFollowCom(com, currentValue,
//							// weekendType, j-7);
//							// }
//							// if (com1 != null) {
//							// finalVector.add(com1);
//							// }
//							Object com1_obj = ((Vector) data.elementAt(i))
//									.elementAt(j + 2);
//							if (com1_obj != null
//									&& com1_obj instanceof TCComponent) {
//								TCComponent com1 = (TCComponent) com1_obj;
//								TCComponent programinfo = getProgramInfo(tmpProgram);
//								if (programinfo != null) {
//									try {
//										com1.setReferenceProperty(
//												"jci6_Program", programinfo);
//									} catch (TCException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								}
//								finalVector.add(com1);
//							} else {
//								TCComponent com1 = createFollowCom(com,
//										currentValue, weekendType, j - 6);
//								if (com1 != null) {
//									TCComponent programinfo = getProgramInfo(tmpProgram);
//									if (programinfo != null) {
//										try {
//											com1.setReferenceProperty(
//													"jci6_Program", programinfo);
//										} catch (TCException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
//									}
//									finalVector.add(com1);
//								}
//							}
//						} else if (backColor.equals(holidayColorSelected)
//								|| backColor.equals(holidayColorNoSelected)) {
//							// TCComponent com1 = (TCComponent)
//							// ((Vector)data.elementAt(i)).elementAt(j+2);
//							// if (com1 == null) {
//							// com1 = createFollowCom(com, currentValue,
//							// holidayType, j-7);
//							// }
//							// if (com1 != null) {
//							// finalVector.add(com1);
//							// }
//							Object com1_obj = ((Vector) data.elementAt(i))
//									.elementAt(j + 2);
//							if (com1_obj != null
//									&& com1_obj instanceof TCComponent) {
//								TCComponent com1 = (TCComponent) com1_obj;
//								TCComponent programinfo = getProgramInfo(tmpProgram);
//								if (programinfo != null) {
//									try {
//										com1.setReferenceProperty(
//												"jci6_Program", programinfo);
//									} catch (TCException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								}
//								finalVector.add(com1);
//							} else {
//								TCComponent com1 = createFollowCom(com,
//										currentValue, holidayType, j - 6);
//								if (com1 != null) {
//									TCComponent programinfo = getProgramInfo(tmpProgram);
//									if (programinfo != null) {
//										try {
//											com1.setReferenceProperty(
//													"jci6_Program", programinfo);
//										} catch (TCException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
//									}
//									finalVector.add(com1);
//								}
//							}
//						}
//					}
//				}
//
//			} else {
//				// 创建头和follow
//				// Fix
//				String name = ((Vector) data1.elementAt(i)).elementAt(0)
//						.toString();
//				if (name == null || name.trim().isEmpty()) {
//					continue;
//				}
//				// Mov
//				String tmpProgram = ((Vector) data2.elementAt(i)).elementAt(1)
//						.toString();
//				if (tmpProgram.trim().equals("")) {
//					MessageBox.post("Program不能为空", "WARNING",
//							MessageBox.WARNING);
//					return;
//				}
//				TCComponent comHead = null;
//				try {
//					comHead = createHeadCom(name, tmpProgram);
//					if (comHead != null) {
//						finalVector.add(comHead);
//					}
//				} catch (ServiceException e) {
//					e.printStackTrace();
//				} catch (TCException e) {
//					e.printStackTrace();
//				}
//				if (comHead == null) {
//					continue;
//				}
//				// 8~
//				for (int j = 7; j < moveColumnName.size(); j++) {
//
//					String currentValue = ((Vector) data2.elementAt(i))
//							.elementAt(j).toString().trim();
//					Color backColor = rColor.get(i + "," + j);
//					if (backColor != null) {
//						if (backColor.equals(normalColorSelected)
//								|| backColor
//										.equals(normalColorNoSelectedIsNull)
//								|| backColor
//										.equals(normalColorNoSelectedIsZero)
//								|| backColor
//										.equals(normalColorNoSelectedIsEight)
//								|| backColor
//										.equals(normalColorNoSelectedIsNotEight)) {
//							TCComponent com1 = null;
//							if (com1 == null) {
//								com1 = createFollowCom(comHead, currentValue,
//										normalType, j - 6);
//							}
//							if (com1 != null) {
//								finalVector.add(com1);
//							}
//						} else if (backColor.equals(overtimeColorSelected)
//								|| backColor.equals(overtimeColorNoSelected)) {
//							TCComponent com1 = null;
//							if (com1 == null) {
//								com1 = createFollowCom(comHead, currentValue,
//										overtimeType, j - 6);
//							}
//							if (com1 != null) {
//								finalVector.add(com1);
//							}
//						} else if (backColor.equals(weekendColorSelected)
//								|| backColor.equals(weekendColorNoSelected)) {
//							TCComponent com1 = null;
//							if (com1 == null) {
//								com1 = createFollowCom(comHead, currentValue,
//										weekendType, j - 6);
//							}
//							if (com1 != null) {
//								finalVector.add(com1);
//							}
//						} else if (backColor.equals(holidayColorSelected)
//								|| backColor.equals(holidayColorNoSelected)) {
//							TCComponent com1 = null;
//							if (com1 == null) {
//								com1 = createFollowCom(comHead, currentValue,
//										holidayType, j - 6);
//							}
//							if (com1 != null) {
//								finalVector.add(com1);
//							}
//						}
//					}
//
//				}
//			}
//		}
//		try {
//			byPass(true);
//			if (finalVector != null && finalVector.size() > 0) {
//				pw.println("准备写入的EXTtse数量："+finalVector.size());
//				pw.flush();
//				form.getTCProperty("jci6_Ext2Detail").setReferenceValueArray(
//						(TCComponent[]) finalVector
//								.toArray(new TCComponent[finalVector.size()]));
//			}
//			form.refresh();
//			byPass(false);
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			byPass(false);
//		} catch (TCException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//	}
//	
//	/**
//	 * 获取相关的汇率
//	 * 
//	 * @param currentComponeyName
//	 * @return
//	 */
//	private String getModifier(String currentComponeyName, String type) {
//		if (rateMap == null || rateMap.size() == 0) {
//			TCPreferenceService prefSvc = tcsession.getPreferenceService();
//			String[] strs = prefSvc.getStringArray(
//					TCPreferenceService.TC_preference_all,
//					"YFJC_User_Rate_Mapping_ExtSupport");
//			// 解析，例如：ExtSupporter{.}tata1=Normal Hours,x1{;}Normal
//			// Hours,x1{;}Normal Hours,x1{;}Normal Hours,x1
//			for (String string : strs) {
//				if (string.startsWith("ExtSupporter")) {
//					if (string.startsWith("ExtSupporter{.}")) {
//						String componyName = "";
//						String[] propValue = string.split("=");
//						if (propValue.length != 2) {
//							continue;
//						}
//						// 获取公司 propValue[0]=ExtSupporter{.}tata1
//						pw.println(string);
//						pw.flush();
//						String[] props = propValue[0].split("\\{.\\}");
//						if (props.length == 2) {
//							componyName = props[1].trim();
//						}
//						// 获取费率 propValue[1]=Normal Hours,x1{;}Normal
//						// Hours,x1{;}Normal Hours,x1{;}Normal Hours,x1
//						if (componyName != null && !componyName.isEmpty()) {
//							String[] rs = propValue[1].split("\\{;\\}");
//							if (rs.length == 4) {
//								String[] s = new String[4];
//								for (String string2 : rs) {
//									String[] string2s = string2.split(",");
//									if (string2.contains("Normal")) {
//										s[0] = string2s[1];
//									} else if (string2.contains("Overtime")) {
//										s[1] = string2s[1];
//									} else if (string2.contains("Holiday")) {
//										s[2] = string2s[1];
//									} else if (string2.contains("Weekend")) {
//										s[3] = string2s[1];
//									}
//								}
//								pw.println("tianti-------------"
//										+ componyName);
//								pw.flush();
//								rateMap.put(componyName, s);
//							}
//						}
//					}else{
//						String[] propValue = string.split("=");
//						if (propValue.length != 2) {
//							continue;
//						}
//						if (!propValue[0].equals("ExtSupporter")) {
//							continue;
//						}
//						pw.println("string:"+string);
//						pw.flush();
//						String[] rs = propValue[1].split("\\{;\\}");
//						if (rs.length == 4) {
//							String[] s = new String[4];
//							for (String string2 : rs) {
//								String[] string2s = string2.split(",");
//								if (string2.contains("Normal")) {
//									s[0] = string2s[1];
//								} else if (string2.contains("Overtime")) {
//									s[1] = string2s[1];
//								} else if (string2.contains("Holiday")) {
//									s[2] = string2s[1];
//								} else if (string2.contains("Weekend")) {
//									s[3] = string2s[1];
//								}
//							}
//							pw.println("tianti-------------"
//									+ "ExtSupporter");
//							pw.flush();
//							rateMap.put("ExtSupporter", s);
//						}
//					}
//				}
//			}
//		}
//		// 开始获取相关汇率
//		String returnV = "";
//		Set<String> componys = rateMap.keySet();
//		for (String string : componys) {
//			if (currentComponeyName.contains(string)) {
//				String[] vs = rateMap.get(string);
//				if (type.contains("Normal")) {
//					returnV = vs[0];
//				} else if (type.contains("Over")) {
//					returnV = vs[1];
//				} else if (type.contains("Holiday")) {
//					returnV = vs[2];
//				} else if (type.contains("Weekend")) {
//					returnV = vs[3];
//				} else if (type.equals("ALL")) {
//					returnV = "ALL";
//				}
//				break;
//			}
//		}
//		
//		if (returnV.isEmpty()) {
//			for (String string : componys) {
//				if ("ExtSupporter".contains(string.trim())) {
//					String[] vs = rateMap.get(string);
//					if (type.contains("Normal")) {
//						returnV = vs[0];
//					} else if (type.contains("Over")) {
//						returnV = vs[1];
//					} else if (type.contains("Holiday")) {
//						returnV = vs[2];
//					} else if (type.contains("Weekend")) {
//						returnV = vs[3];
//					} else if (type.equals("ALL")) {
//						returnV = "ALL";
//					}
//					break;
//				}
//			}
//		}
////		pw.println("currentComponeyName:"+currentComponeyName+"========="+returnV);
//		return returnV;
//	}
//
//	/**
//	 * 获取相关的汇率
//	 * 
//	 * @param currentComponeyName
//	 * @return
//	 */
//	private String getModifier1(String currentComponeyName, String type) {
//		if (rateMap == null || rateMap.size() == 0) {
//			TCPreferenceService prefSvc = tcsession.getPreferenceService();
//			String[] strs = prefSvc.getStringArray(
//					TCPreferenceService.TC_preference_all,
//					"YFJC_User_Rate_Mapping_ExtSupport");
//			// 解析，例如：ExtSupporter{.}tata1=Normal Hours,x1{;}Normal
//			// Hours,x1{;}Normal Hours,x1{;}Normal Hours,x1
//			for (String string : strs) {
//				if (string.startsWith("ExtSupporter{.}")) {
//					String componyName = "";
//					String[] propValue = string.split("=");
//					if (propValue.length != 2) {
//						continue;
//					}
//					// 获取公司 propValue[0]=ExtSupporter{.}tata1
//					pw.println(string);
//					pw.flush();
//					String[] props = propValue[0].split("\\{.\\}");
//					if (props.length == 2) {
//						componyName = props[1].trim();
//					}
//					// 获取费率 propValue[1]=Normal Hours,x1{;}Normal
//					// Hours,x1{;}Normal Hours,x1{;}Normal Hours,x1
//					if (componyName != null && !componyName.isEmpty()) {
//						String[] rs = propValue[1].split("\\{;\\}");
//						if (rs.length == 4) {
//							String[] s = new String[4];
//							for (String string2 : rs) {
//								String[] string2s = string2.split(",");
//								if (string2.contains("Normal")) {
//									s[0] = string2s[1];
//								} else if (string2.contains("Overtime")) {
//									s[1] = string2s[1];
//								} else if (string2.contains("Holiday")) {
//									s[2] = string2s[1];
//								} else if (string2.contains("Weekend")) {
//									s[3] = string2s[1];
//								}
//							}
//							pw.println("tianti-------------"
//									+ componyName);
//							pw.flush();
//							rateMap.put(componyName, s);
//						}
//
//					}
//				}
//			}
//			
//		}
//
//		// 开始获取相关汇率
//		Set<String> componys = rateMap.keySet();
//		for (String string : componys) {
//			if (currentComponeyName.contains(string)) {
//				String[] vs = rateMap.get(string);
//				if (type.contains("Normal")) {
//					return vs[0];
//				} else if (type.contains("Over")) {
//					return vs[1];
//				} else if (type.contains("Holiday")) {
//					return vs[2];
//				} else if (type.contains("Weekend")) {
//					return vs[3];
//				} else if (type.equals("ALL")) {
//					return "ALL";
//				}
//			}
//		}
//		return "";
//	}
//
//	private TCComponent createHeadCom(String name, String program)
//			throws ServiceException, TCException {
//		Object[] currentObj = null;
//		if (extTSEAttrList == null || extTSEAttrList.size() == 0) {
//			TCComponent component = createHeadComWithoutextTSEAttrList(name,
//					program);
//			return component;
//		}
//		for (Object[] objects : extTSEAttrList) {
//			if (objects[3].equals(name)) {
//				currentObj = objects;
//				break;
//			}
//		}
//		name = "Head_" + name;
//		TCSession tcsession = (TCSession) form.getSession();
//		DataManagementService dmService = DataManagementService
//				.getService(tcsession);
//		com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[] createIn = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];
//		createIn[0] = new DataManagement.CreateIn();
//		createIn[0].data.boName = "JCI6_ExtTSE";
//		createIn[0].data.stringProps.put("object_name", name);
//		CreateResponse responese = dmService.createObjects(createIn);
//		TCComponent component = responese.output[0].objects[0];
//		TCProperty[] property = component.getTCProperties(new String[] {
//				"jci6_Division", "jci6_Section", "jci6_Company",
//				"jci6_ExtName", "jci6_Onboard", "jci6_Rate", "jci6_GID",
//				"jci6_Program" });
//		property[0].setReferenceValue((TCComponent) currentObj[0]);
//		property[1].setReferenceValue((TCComponent) currentObj[1]);
//		property[2].setStringValue((String) currentObj[2]);
//		property[3].setStringValue((String) currentObj[3]);
//		property[4].setDateValue((Date) currentObj[4]);
//		property[5].setDoubleValue((Double) currentObj[5]);
//		property[6].setStringValue((String) currentObj[6]);
//		TCComponent programInfoItem = getProgramInfo(program);
//		if (programInfoItem != null) {
//			pw.println(program + "找到item");
//			pw.flush();
//			property[7].setReferenceValue(programInfoItem);
//		} else {
//			pw.println(program + "没有找到item");
//			pw.flush();
//		}
//		component.setTCProperties(property);
//		return component;
//	}
//
//	private TCComponent createHeadComWithoutextTSEAttrList(String name,
//			String program) throws ServiceException, TCException {
//		int rowCount = fixTable.getRowCount();
//		TCComponent tmpCom = null;
//		for (int i = 0; i < data_form.size(); i++) {
//			Object tcComponentObj = data_form.get(i);
//			if (tcComponentObj != null && tcComponentObj instanceof TCComponent) {
//				TCComponent tcComponent = (TCComponent) tcComponentObj;
//				//修改 2017-09-28
////				if (tcComponent.isValid()) {
//					String tmpname = tcComponent.getStringProperty("object_name");
//					if (tmpname.startsWith("Head") && tmpname.endsWith(name)) {
//						tmpCom = tcComponent;
//						break;
//					}
////				}
//			}
//		}
////		for (int i = 0; i < rowCount; i++) {
////			String tmpName = fixTable.getValueAt(i, 0).toString();
////			if (tmpName.trim().equals(name.trim()) && data.size() > i) {
////				Object comObj = data.get(i);
////				if (comObj != null && comObj instanceof TCComponent) {
////					tmpCom = (TCComponent) data.get(i);
////					break;
////				}
////			}
////		}
//		pw.println(name+"==createHeadComWithoutextTSEAttrList:"+tmpCom);
//		pw.flush();
//		if (tmpCom == null) {
//			return null;
//		}
//		name = "Head_" + name;
//		TCSession tcsession = (TCSession) form.getSession();
//		DataManagementService dmService = DataManagementService
//				.getService(tcsession);
//		com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[] createIn = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];
//		createIn[0] = new DataManagement.CreateIn();
//		createIn[0].data.boName = "JCI6_ExtTSE";
//		createIn[0].data.stringProps.put("object_name", name);
//		CreateResponse responese = dmService.createObjects(createIn);
//		TCComponent component = responese.output[0].objects[0];
//		TCProperty[] property = component.getTCProperties(new String[] {
//				"jci6_Division", "jci6_Section", "jci6_Company",
//				"jci6_ExtName", "jci6_Onboard", "jci6_Rate", "jci6_GID",
//				"jci6_Program" });
//		TCProperty[] property1 = tmpCom.getTCProperties(new String[] {
//				"jci6_Division", "jci6_Section", "jci6_Company",
//				"jci6_ExtName", "jci6_Onboard", "jci6_Rate", "jci6_GID",
//				"jci6_Program" });
//		// property[0].setReferenceValue((TCComponent) currentObj[0]);
//		// property[1].setReferenceValue((TCComponent) currentObj[1]);
//		// property[2].setStringValue((String) currentObj[2]);
//		// property[3].setStringValue((String) currentObj[3]);
//		// property[4].setDateValue((Date) currentObj[4]);
//		// property[5].setDoubleValue((Double) currentObj[5]);
//		// property[6].setStringValue((String) currentObj[6]);
//		property[0].setReferenceValue(property1[0].getReferenceValue());
//		property[1].setReferenceValue(property1[1].getReferenceValue());
//		property[2].setStringValue(property1[2].getStringValue());
//		property[3].setStringValue(property1[3].getStringValue());
//		property[4].setDateValue(property1[4].getDateValue());
//		property[5].setDoubleValue(property1[5].getDoubleValue());
//		property[6].setStringValue(property1[6].getStringValue());
//		TCComponent programInfoItem = getProgramInfo(program);
//		if (programInfoItem != null) {
//			pw.println(program + "找到item");
//			pw.flush();
//			property[7].setReferenceValue(programInfoItem);
//		} else {
//			pw.println(program + "没有找到item");
//			pw.flush();
//		}
//		component.setTCProperties(property);
//		return component;
//	}
//
//	private TCComponent getProgramInfo(String itemIDName) {
//		try {
//			byPass(true);
//		} catch (TCException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		String itemID = programinfoMap.get(itemIDName);
//		if (itemID == null) {
//			return null;
//		}
//		try {
//			
//			TCComponentItemType type = (TCComponentItemType) tcsession
//					.getTypeComponent("Item");
//			TCComponentItem[] items = type.findItems(itemID);
//			if (items != null && items.length > 0) {
//				return items[0];
//			} else {
//				return null;
//			}
//		} catch (TCException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	/**
//	 * 开关旁路
//	 * 
//	 * @param open
//	 * @throws TCException
//	 */
//	private void byPass(boolean open) throws TCException {
//		if (userService == null)
//			userService = tcsession.getUserService();
//		Object[] obj = new Object[1];
//		if (obj == null)
//			obj = new Object[1];
//		if (open) {
//			obj[0] = 1;
//			userService.call("open_or_close_pass", obj);
//		} else {
//			obj[0] = 0;
//			userService.call("open_or_close_pass", obj);
//		}
//	}
//
//	private TCComponent createFollowCom(TCComponent com, String currentValue,
//			String cellType, Integer jciDay) {
//		if (currentValue.isEmpty()) {
//			return null;
//		}
//		Double jci6_Hours = Double.valueOf(currentValue);
//		String billType_0 = cellType.split("\\s+")[0];
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy_MM_dd");
//		TCComponent jci6_Division = null;
//		try {
//			jci6_Division = com.getReferenceProperty("jci6_Division");
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		TCComponent jci6_Section = null;
//		try {
//			jci6_Section = com.getReferenceProperty("jci6_Section");
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		String jci6_Company = null;
//		try {
//			jci6_Company = com.getStringProperty("jci6_Company");
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		String jci6_ExtName = null;
//		try {
//			jci6_ExtName = com.getStringProperty("jci6_ExtName");
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		Date jci6_Onboard = null;
//		try {
//			jci6_Onboard = com.getDateProperty("jci6_Onboard");
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		Double jci6_Rate = null;
//		try {
//			jci6_Rate = com.getDoubleProperty("jci6_Rate");
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		String jci6_GID = null;
//		try {
//			jci6_GID = com.getStringProperty("jci6_GID");
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		TCComponent jci6_Program = null;
//		try {
//			byPass(true);
//			jci6_Program = com.getReferenceProperty("jci6_Program");
//			byPass(false);
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		Date jci6_Date = null;
//		try {
//			jci6_Date = sdf1.parse(jciYear + "_" + jciMonth + "_" + jciDay);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		String jci6_BillType = cellType;
//		String name = "Follow_" + jci6_ExtName + "_" + billType_0 + "_"
//				+ jciYear + "_" + jciMonth + "_" + jciDay;
//		TCSession tcsession = (TCSession) AIFUtility.getCurrentApplication()
//				.getSession();
//		DataManagementService dmService = DataManagementService
//				.getService(tcsession);
//		com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[] createIn = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];
//		createIn[0] = new DataManagement.CreateIn();
//		createIn[0].data.boName = "JCI6_ExtTSE";
//		createIn[0].data.stringProps.put("object_name", name);
//		CreateResponse responese = null;
//		try {
//			responese = dmService.createObjects(createIn);
//		} catch (ServiceException e) {
//			e.printStackTrace();
//		}
//		TCComponent component = responese.output[0].objects[0];
//		try {
//			component.setReferenceProperty("jci6_Division", jci6_Division);
//			component.setReferenceProperty("jci6_Section", jci6_Section);
//			component.setStringProperty("jci6_Company", jci6_Company);
//			component.setStringProperty("jci6_ExtName", jci6_ExtName);
//			component.setDateProperty("jci6_Onboard", jci6_Onboard);
//			component.setDoubleProperty("jci6_Rate", jci6_Rate);
//			component.setStringProperty("jci6_GID", jci6_GID);
//			component.setDateProperty("jci6_Date", jci6_Date);
//			component.setStringProperty("jci6_BillType", jci6_BillType);
//			component.setDoubleProperty("jci6_Hours", jci6_Hours);
//			String tmpRate = getModifier(jci6_Company, jci6_BillType);
//			component.setStringProperty("jci6_Modifier", tmpRate);
//			component.setReferenceProperty("jci6_Program", jci6_Program);
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		return component;
//	}
//
//	/**
//	 * 自定义DefaultTableCellRenderer，用于Summary table
//	 */
//	class myCellRenderer2 extends DefaultTableCellRenderer {
//		
//		
//		@Override
//		public Component getTableCellRendererComponent(JTable table,
//				Object value, boolean isSelected, boolean hasFocus, int row,
//				int column) {
//			if (value == null) {
//				value = "";
//			}else{
//				value = value.toString().replace(".0", "");
//			}
//			int rowCount = table.getRowCount();
//			// 设置Overtime的day cell 的颜色
//			
//			if (row < rowCount - 2) {
//				if (isSelected == false) {
//
//					// 设置N、O、W、H颜色
//					switch (column) {
//					case 0:
//						if(!passLines.contains(row)){
//							super.setBackground(approvedCostColor);
//						}else{
//							super.setBackground(toBeApprovedColor);
//						}
//						break;
//					case 1:
//						if(!passLines.contains(row)){
//							super.setBackground(approvedCostColor);
//						}else{
//							super.setBackground(toBeApprovedColor);
//						}
//						break;
//					case 2:
//						if(!passLines.contains(row)){
//							super.setBackground(approvedCostColor);
//						}else{
//							super.setBackground(toBeApprovedColor);
//						}
//						break;
//					case 3:
//						if(!passLines.contains(row)){
//							super.setBackground(approvedCostColor);
//						}else{
//							super.setBackground(toBeApprovedColor);
//						}
//						break;
//					case 4:
//						if(!passLines.contains(row)){
//							super.setBackground(approvedCostColor);
//						}else{
//							super.setBackground(toBeApprovedColor);
//						}
//						break;
//					case 5:
//						if(!passLines.contains(row)){
//							super.setBackground(approvedCostColor);
//						}else{
//							super.setBackground(toBeApprovedColor);
//						}
//						break;
//					case 6:
//						super.setBackground(normalColor);
//						break;
//					case 7:
//						super.setBackground(overtimeColor);
//						break;
//					case 8:
//						super.setBackground(weekendColor);
//						break;
//					case 9:
//						super.setBackground(holidayColor);
//						break;
//					}
//					setFont(table.getFont());
//					setValue(value); 
//				}else{
//					super.setForeground(Color.BLACK);
//					super.setBackground(table.getSelectionBackground());
//					setValue(value); 
//				}
//				
//				
//				
//				
//				String tmpType = cellTypeMap_move.get(row + "," + (column - 3));
//				tmpType = ((tmpType != null && tmpType.length() > 3) ? tmpType
//						: cellTypeMap_Column.get(column - 3));
//				if (tmpType == null) {
//					return this;
//				}
//				Vector overtimeV = overtimeMap.get(row + 1);
//				if (overtimeV != null && overtimeV.size() > 0
//						&& overtimeV.contains(column)) {
//					if (isSelected) {
//						super.setForeground(Color.BLACK);
//						super.setBackground(overtimeColorSelected);
//					} else {
//						super.setForeground(Color.BLACK);
//						super.setBackground(overtimeColorNoSelected);
//					}
//				} else if (tmpType.equals(weekendType)) {
//					if (isSelected) {
//						super.setForeground(Color.BLACK);
//						super.setBackground(weekendColorSelected);
//					} else {
//						super.setForeground(Color.BLACK);
//						super.setBackground(weekendColorNoSelected);
//					}
//				} else if (tmpType.equals(holidayType)) {
//					if (isSelected) {
//						super.setForeground(Color.BLACK);
//						super.setBackground(holidayColorSelected);
//					} else {
//						super.setForeground(Color.BLACK);
//						super.setBackground(holidayColorNoSelected);
//					}
//				}else{
//					if (isSelected) {
//						super.setForeground(Color.BLACK);
//						super.setBackground(table.getSelectionBackground());
//					} else {
//						super.setForeground(Color.BLACK);
//						super.setBackground(table.getBackground());
//					}
//				}
//				setFont(table.getFont());
//				setValue(value); 
//			} else {
//				super.setForeground(Color.BLACK);
//				if (!isSelected&&column != 4) {
//					super.setForeground(Color.BLACK);
//					super.setBackground(Color.WHITE);
//				}else if (!isSelected&&column == 4) {
//					if (row == rowCount - 1) {
//						super.setBackground(toBeApprovedColor);
//					}else if (row == rowCount - 2) {
//						super.setBackground(approvedCostColor);
//					}
//				}else if (isSelected) {
//					super.setBackground(table.getSelectionBackground());
//				}
//				setFont(table.getFont());
//				setValue(value); 
//			}
//			
//			
//			return this;
//		}
//	}
////	class myCellRenderer2 extends DefaultTableCellRenderer {
////		
////		
////		@Override
////		public Component getTableCellRendererComponent(JTable table,
////				Object value, boolean isSelected, boolean hasFocus, int row,
////				int column) {
////			if (value == null) {
////				value = "";
////			}else{
////				value = value.toString().replace(".0", "");
////			}
////			Component comp = super.getTableCellRendererComponent(table, value,
////					isSelected, hasFocus, row, column);
////			int rowCount = table.getRowCount();
////			
//////			if (isSelected == false && hasFocus == false && row < rowCount - 2) {
//////				// 设置间隔颜色
////////				switch (row % 2) {
////////				case 0:
////////					comp.setBackground(new Color(222, 222, 222));
////////					break;
////////
////////				default:
////////					comp.setBackground(new Color(250, 250, 250));
////////					break;
////////				}
//////				//设置未发布的行的颜色
////////					if (passLines.isEmpty()) {
////////						comp.setBackground(Color.white);
////////					}else if(passLines.contains(row)){
////////						if (column >= 0 && column < 6) {
////////							comp.setBackground(toBeApprovedColor);
////////						}else{
////////							comp.setBackground(Color.white);
////////						}
////////					}
//////				// 设置N、O、W、H颜色
//////				switch (column) {
//////				case 0:
//////					if(passLines.contains(row)){
//////						comp.setBackground(approvedCostColor);
//////					}else{
//////						comp.setBackground(toBeApprovedColor);
//////					}
//////					break;
//////				case 1:
//////					if(passLines.contains(row)){
//////						comp.setBackground(approvedCostColor);
//////					}else{
//////						comp.setBackground(toBeApprovedColor);
//////					}
//////					break;
//////				case 2:
//////					if(passLines.contains(row)){
//////						comp.setBackground(approvedCostColor);
//////					}else{
//////						comp.setBackground(toBeApprovedColor);
//////					}
//////					break;
//////				case 3:
//////					if(passLines.contains(row)){
//////						comp.setBackground(approvedCostColor);
//////					}else{
//////						comp.setBackground(toBeApprovedColor);
//////					}
//////					break;
//////				case 4:
//////					if(passLines.contains(row)){
//////						comp.setBackground(approvedCostColor);
//////					}else{
//////						comp.setBackground(toBeApprovedColor);
//////					}
//////					break;
//////				case 5:
//////					if(passLines.contains(row)){
//////						comp.setBackground(approvedCostColor);
//////					}else{
//////						comp.setBackground(toBeApprovedColor);
//////					}
//////					break;
//////				case 6:
//////					comp.setBackground(normalColor);
//////					break;
//////				case 7:
//////					comp.setBackground(overtimeColor);
//////					break;
//////				case 8:
//////					comp.setBackground(weekendColor);
//////					break;
//////				case 9:
//////					comp.setBackground(holidayColor);
//////					break;
//////				}
//////			}
////			// 设置Overtime的day cell 的颜色
////			
////			if (row < rowCount - 2) {
////				String tmpType = cellTypeMap_move.get(row + "," + (column - 3));
////				tmpType = ((tmpType != null && tmpType.length() > 3) ? tmpType
////						: cellTypeMap_Column.get(column - 3));
////				if (tmpType == null) {
////					return comp;
////				}
////				if (!this.isPaintingForPrint()) {
////					return comp;
////				}
////				// if (tmpType.equals(overtimeType)) {
////				// if (isSelected) {
////				// comp.setForeground(Color.BLACK);
////				// comp.setBackground(overtimeColorSelected);
////				// } else {
////				// comp.setForeground(Color.BLACK);
////				// comp.setBackground(overtimeColorNoSelected);
////				// }
////				// } else
////				Vector overtimeV = overtimeMap.get(row + 1);
////				if (overtimeV != null && overtimeV.size() > 0
////						&& overtimeV.contains(column)) {
////					if (isSelected) {
////						comp.setForeground(Color.BLACK);
////						comp.setBackground(overtimeColorSelected);
////					} else {
////						comp.setForeground(Color.BLACK);
////						comp.setBackground(overtimeColorNoSelected);
////					}
////				} else if (tmpType.equals(weekendType)) {
////					if (isSelected) {
////						comp.setForeground(Color.BLACK);
////						comp.setBackground(weekendColorSelected);
////					} else {
////						comp.setForeground(Color.BLACK);
////						comp.setBackground(weekendColorNoSelected);
////					}
////				} else if (tmpType.equals(holidayType)) {
////					if (isSelected) {
////						comp.setForeground(Color.BLACK);
////						comp.setBackground(holidayColorSelected);
////					} else {
////						comp.setForeground(Color.BLACK);
////						comp.setBackground(holidayColorNoSelected);
////					}
////				}else{
////					if (isSelected) {
////						comp.setForeground(Color.BLACK);
////						comp.setBackground(table.getSelectionBackground());
////					} else {
////						comp.setForeground(Color.BLACK);
////						comp.setBackground(table.getBackground());
////					}
////				}
////				
////			} else {
////				if (!isSelected&&column != 4) {
////					comp.setForeground(Color.BLACK);
////					comp.setBackground(Color.WHITE);
////				}else if (!isSelected&&column == 4) {
////					if (row == rowCount - 1) {
////						comp.setBackground(toBeApprovedColor);
////					}else if (row == rowCount - 2) {
////						comp.setBackground(approvedCostColor);
////					}
////				}
////			}
////			
////			if (isSelected == false && row < rowCount - 2) {
////				// 设置N、O、W、H颜色
////				switch (column) {
////				case 0:
////					if(passLines.contains(row)){
////						comp.setBackground(approvedCostColor);
////						this.setBackground(approvedCostColor);
////					}else{
////						comp.setBackground(toBeApprovedColor);
////						this.setBackground(toBeApprovedColor);
////					}
////					break;
////				case 1:
////					if(passLines.contains(row)){
////						comp.setBackground(approvedCostColor);
////						this.setBackground(approvedCostColor);
////					}else{
////						comp.setBackground(toBeApprovedColor);
////						this.setBackground(toBeApprovedColor);
////					}
////					break;
////				case 2:
////					if(passLines.contains(row)){
////						comp.setBackground(approvedCostColor);
////						this.setBackground(approvedCostColor);
////					}else{
////						comp.setBackground(toBeApprovedColor);
////						this.setBackground(toBeApprovedColor);
////					}
////					break;
////				case 3:
////					if(passLines.contains(row)){
////						comp.setBackground(approvedCostColor);
////						this.setBackground(approvedCostColor);
////					}else{
////						comp.setBackground(toBeApprovedColor);
////						this.setBackground(toBeApprovedColor);
////					}
////					break;
////				case 4:
////					if(passLines.contains(row)){
////						comp.setBackground(approvedCostColor);
////						this.setBackground(approvedCostColor);
////					}else{
////						comp.setBackground(toBeApprovedColor);
////						this.setBackground(toBeApprovedColor);
////					}
////					break;
////				case 5:
////					if(passLines.contains(row)){
////						comp.setBackground(approvedCostColor);
////						this.setBackground(approvedCostColor);
////					}else{
////						comp.setBackground(toBeApprovedColor);
////						this.setBackground(toBeApprovedColor);
////					}
////					break;
////				case 6:
////					comp.setBackground(normalColor);
////					break;
////				case 7:
////					comp.setBackground(overtimeColor);
////					break;
////				case 8:
////					comp.setBackground(weekendColor);
////					break;
////				case 9:
////					comp.setBackground(holidayColor);
////					break;
////				}
////			}
////			
////			return comp;
////		}
////	}
//
//	class jciTableCelledit extends iComboBox implements TableCellEditor {
//
//		// EventListenerList:保存EventListener 列表的类。
//		private EventListenerList listenerList = new EventListenerList();
//		// ChangeEvent用于通知感兴趣的参与者事件源中的状态已发生更改。
//		private ChangeEvent changeEvent = new ChangeEvent(this);
//
//		public jciTableCelledit(Vector list,boolean isenable) {
//			if (list == null || list.size() == 0) {
//				addItem("");
//			} else {
//				setItems(list);
//			}
//			setAutoCompleteSuggestive(true);
////			if (extTSEAttrList == null || extTSEAttrList.size() == 0) {
////				setEnabled(false);
////			}else {
////				setEnabled(true);
////			}
//			
//		}
//		
//		
//
//		@Override
//		public Object getCellEditorValue() {
//			// TODO Auto-generated method stub
//			return getSelectedItem().toString();
//		}
//
//		@Override
//		public boolean isCellEditable(EventObject anEvent) {
//			// TODO Auto-generated method stub
//			return true;
//		}
//
//		@Override
//		public boolean shouldSelectCell(EventObject anEvent) {
//			// TODO Auto-generated method stub
//			return true;
//		}
//
//		private void fireEditingStopped() {
//			CellEditorListener listener;
//			Object[] listeners = listenerList.getListenerList();
//			for (int i = 0; i < listeners.length; i++) {
//				if (listeners[i] == CellEditorListener.class) {
//					// 之所以是i+1，是因为一个为CellEditorListener.class（Class对象），
//					// 接着的是一个CellEditorListener的实例
//					listener = (CellEditorListener) listeners[i + 1];
//					// 让changeEvent去通知编辑器已经结束编辑
//					// 在editingStopped方法中，JTable调用getCellEditorValue()取回单元格的值，
//					// 并且把这个值传递给TableValues(TableModel)的setValueAt()
//					listener.editingStopped(changeEvent);
//				}
//			}
//		}
//
//		@Override
//		public boolean stopCellEditing() {
//			fireEditingStopped();// 请求终止编辑操作从JTable获得
//			return true;
//		}
//
//		@Override
//		public void cancelCellEditing() {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void addCellEditorListener(CellEditorListener l) {
//			listenerList.add(CellEditorListener.class, l);
//
//		}
//
//		@Override
//		public void removeCellEditorListener(CellEditorListener l) {
//			listenerList.remove(CellEditorListener.class, l);
//
//		}
//
//		@Override
//		public Component getTableCellEditorComponent(JTable table,
//				Object value, boolean isSelected, int row, int column) {
//			setSelectedItem(value);
////			boolean isPass = false;
////			Set<String> passkey = passMap_move.keySet();
////			for (String str : passkey) {
////				if (str.contains(",")) {
////					String str_splits = str.split(",")[0];
////					if (str_splits.trim().equals(Integer.toString(row))) {
////						boolean ifail = passMap_move.get(str);
////						if (ifail) {
////							isPass = true;
////							break;
////						}
////					}
////				}
////			}
//			
//			Color currentColor = programColor.get(row);
//			if (currentColor.equals(approvedHeadColor)) {
//				this.setEnabled(false);
//				this.getTextField().setBackground(approvedHeadColor);
//			}else if (currentColor.equals(toBeApprovedHeadColor)) {
//				this.setEnabled(true);
//				this.getTextField().setBackground(toBeApprovedHeadColor);
//			}else{
//				this.setEnabled(true);
//				this.getTextField().setBackground(currentColor);
//			}
//			//字体颜色
////			if (isSelected) {
////				
////				this.getTextField().setForeground(Color.GRAY);
////			}else{
////				
////				this.getTextField().setForeground(Color.BLACK);
////			}
////			
//			this.setForeground(Color.BLACK);
//			return this;
//		}
//
//	}
//
//	/**
//	 * 自定义DefaultTableCellRenderer，用于颜色显示
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//	class myCellRenderer1 extends DefaultTableCellRenderer {
//
//
//		@Override
//		public void setHorizontalAlignment(int alignment) {
//			super.setHorizontalAlignment(JLabel.CENTER);
//		}
//
//		@Override
//		public Component getTableCellRendererComponent(JTable table,
//				Object value, boolean isSelected, boolean hasFocus, int row,
//				int column) {
//			boolean isPass = false;
//			Set<String> passkey = passMap_move.keySet();
//			for (String str : passkey) {
//				if (str.contains(",")) {
//					String str_splits = str.split(",")[0];
//					if (str_splits.trim().equals(Integer.toString(row))) {
//						boolean ifail = passMap_move.get(str);
//						if (ifail) {
//							isPass = true;
//							break;
//						}
//					}
//				}
//			}
//			String cell0 = table.getValueAt(row, 0).toString();
//			if (column == 0 || column == 1 || column == 2) {
//				if (isSelected) {
//					super.setBackground(table.getSelectionBackground());
//					super.setForeground(table.getForeground());
//					super.setFont(table.getFont());
//				} else {
//					if (isPass) {
//						super.setBackground(approvedHeadColor);
//						super.setForeground(Color.GRAY);
//						super.setFont(table.getFont().deriveFont(Font.BOLD));
//						programColor.put(row, approvedHeadColor);
//					}else if(!cell0.trim().isEmpty()){
//						super.setForeground(table.getForeground());
//						super.setFont(table.getFont());
//						super.setBackground(toBeApprovedHeadColor);
//						programColor.put(row, toBeApprovedHeadColor);
//					}else{
//						super.setForeground(table.getForeground());
//						super.setFont(table.getFont());
//						super.setBackground(table.getBackground());
//						programColor.put(row, table.getBackground());
//					}
//				}
////				if (ispassFix) {
////					super.setForeground(Color.GRAY);
////					super.setFont(table.getFont().deriveFont(Font.BOLD));
////				}
//			} else {
//				// 是mov的
//				if (column == 3) {
//					// Normal
//					if (isSelected) {
//						super.setForeground(table.getSelectionForeground());
//						super.setBackground(table.getSelectionBackground());
//					} else {
//						super.setForeground(Color.BLACK);
//						// super.setBackground(Color.GREEN);
//						super.setBackground(normalColor);
//					}
//				} else if (column == 4) {
//					// Overtime
//					if (isSelected) {
//						super.setForeground(table.getSelectionForeground());
//						super.setBackground(table.getSelectionBackground());
//					} else {
//						super.setForeground(Color.BLACK);
//						// super.setBackground(Color.ORANGE);
//						super.setBackground(overtimeColor);
//					}
//				} else if (column == 5) {
//					// Weekend
//					if (isSelected) {
//						super.setForeground(table.getSelectionForeground());
//						super.setBackground(table.getSelectionBackground());
//					} else {
//						super.setForeground(Color.BLACK);
//						// super.setBackground(Color.LIGHT_GRAY);
//						super.setBackground(weekendColor);
//					}
//				} else if (column == 6) {
//					// Holiday
//					if (isSelected) {
//						super.setForeground(table.getSelectionForeground());
//						super.setBackground(table.getSelectionBackground());
//					} else {
//						super.setForeground(Color.BLACK);
//						// super.setBackground(Color.PINK);
//						super.setBackground(holidayColor);
//					}
//				} else if (column > 6) {
//					String tmpType = cellTypeMap_move.get(row + "," + column);
//					tmpType = ((tmpType != null && tmpType.length() > 3) ? tmpType
//							: cellTypeMap_Column.get(column));
//					if (tmpType.equals(normalType)) {
//						if (isSelected) {
//							super.setForeground(Color.BLACK);
//							super.setBackground(normalColorSelected);
//						} else {
//							String cell2 = table.getValueAt(row, 2).toString();
//							if (data1.size() > row) {
//								cell2 = ((Vector) data2.elementAt(row))
//										.elementAt(2).toString();
//								;
//							}
//							{
//								// 获取当前行的fix的name
//								String currentName = fixTable
//										.getValueAt(row, 0).toString().trim();
//								if (currentName.isEmpty()) {
//									super.setForeground(Color.BLACK);
//									super.setBackground(normalColorNoSelectedIsNull);
//								} else {
//									Double currentColumnCount = 0.0d;
//									int rowCount = fixTable.getRowCount();
//									for (int i = 0; i < rowCount; i++) {
//										String tmpName = fixTable
//												.getValueAt(i, 0).toString()
//												.trim();
//										if (!tmpName.isEmpty()
//												&& currentName
//														.endsWith(tmpName)) {
//											// 计算合计
//											String tmpType1 = cellTypeMap_move
//													.get(i + "," + column);
//											String currentColumnStr = table
//													.getValueAt(i, column)
//													.toString().trim();
//											if (!currentColumnStr.isEmpty()
//													&& (tmpType1 != null && tmpType1
//															.equals(normalType))) {
//												currentColumnCount += Double
//														.valueOf(currentColumnStr);
//											}
//										}
//									}
//									if (currentColumnCount == 0.0d) {
//										super.setForeground(Color.BLACK);
//										super.setBackground(normalColorNoSelectedIsZero);
//									} else if (currentColumnCount == 8.0d) {
//										super.setForeground(Color.BLACK);
//										super.setBackground(normalColorNoSelectedIsEight);
//									} else {
//										super.setForeground(Color.BLACK);
//										super.setBackground(normalColorNoSelectedIsNotEight);
//									}
//								}
//							}
//						}
//					} else if (tmpType.equals(overtimeType)) {
//						if (isSelected) {
//							super.setForeground(Color.BLACK);
//							super.setBackground(overtimeColorSelected);
//						} else {
//							super.setForeground(Color.BLACK);
//							super.setBackground(overtimeColorNoSelected);
//						}
//					} else if (tmpType.equals(weekendType)) {
//						if (isSelected) {
//							super.setForeground(Color.BLACK);
//							super.setBackground(weekendColorSelected);
//						} else {
//							super.setForeground(Color.BLACK);
//							super.setBackground(weekendColorNoSelected);
//						}
//					} else if (tmpType.equals(holidayType)) {
//						if (isSelected) {
//							super.setForeground(Color.BLACK);
//							super.setBackground(holidayColorSelected);
//						} else {
//							super.setForeground(Color.BLACK);
//							super.setBackground(holidayColorNoSelected);
//						}
//					}
//				} else {
//					super.getTableCellRendererComponent(table, value,
//							isSelected, hasFocus, row, column);
//				}
//				rColor.put(row + "," + column, super.getBackground());
//				if (isPass) {
//					super.setForeground(Color.GRAY);
//					super.setFont(table.getFont().deriveFont(Font.BOLD));
//				}else{
//					super.setForeground(table.getForeground());
//					super.setFont(table.getFont());
//				}
//			}
//			if (value != null) {
//				value = value.toString().replace(".0", "");
//			}
//			setValue(value);
//			return this;
//		}
//
//	}
//
//}
