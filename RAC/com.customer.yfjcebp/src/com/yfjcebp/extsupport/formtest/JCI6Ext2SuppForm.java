package com.yfjcebp.extsupport.formtest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.AbstractRendering;
import com.teamcenter.rac.util.combobox.iComboBox;
import com.teamcenter.soa.exceptions.NotLoadedException;
//import com.yfjcebp.extsupport.form.JCIUtil.LimitedDocument;
import com.yfjcebp.extsupport.formtest.JCIUtil.LimitedDocument;

public class JCI6Ext2SuppForm extends AbstractRendering{
	public final TCComponentForm form;
	public final TCSession tcsession;
	
	//首选项
	private final String ratePreferenceName = "YFJC_User_Rate_Mapping";
	private HashMap<String, String[]> componeyNameAndModifierMap;
	private final String holiday_preference = "YFJC_Holiday_Also_Weekend";
	private final String color_preference = "YFJC_Ext2SupportForm_Color";
	
	//时间及日期
	private String[] weekDays = { "Mon", "Tue", "Wed", "Thu", "Fri","Sat","Sun" };
	private final String[] month = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul","Aug", "Sep", "Oct", "Nov", "Dec" };
	private HashMap<String, Integer> weekDayToNumMap = new HashMap<String, Integer>();
	
	// 查询
	private final String QUERY_NAME = "__YFJC_LCC_ExtRateRevision";
	private HashMap<String, String> programinfoMap;
	
	//保存对象
	private ArrayList<TCComponentItemRevision> lastPassExtTSERevList;
//	private ArrayList<String[]> extTSEAttrMap = new ArrayList<String[]>();
	private HashMap<String, String[]> extTSEAttrMap = new HashMap<String, String[]>();
	private HashMap<String, TCComponentItemRevision> nameAndComMap = new HashMap<String, TCComponentItemRevision>();
	private int jciYear;
	private int jciMonth;
	private List<Date> weekOfMonth;
	private ArrayList<Integer> weekOfMonthType;
	
	
	
	//颜色配置
	private Boolean isColorConfiguration = false;
	private Color normalColor;
	private Color overtimeColor;
	private Color weekendColor;
	private Color holidayColor;
	private Color normalColorSelected;
	private Color normalColorNoSelectedIsNull;
	private Color normalColorNoSelectedIsZero;
	private Color normalColorNoSelectedIsEight;
	private Color normalColorNoSelectedIsNotEight;
	private Color overtimeColorSelected;
	private Color overtimeColorNoSelected;
	private Color weekendColorSelected;
	private Color weekendColorNoSelected;
	private Color holidayColorSelected;
	private Color holidayColorNoSelected;
	
	
	//table
	private Vector<String> fixColumnName;
	private Vector<String> moveColumnName;
	private Vector data1;
	private Vector data2;
	private JTabbedPane tabbedPanel;
	private JTable table0;
	private JTextField normalTField;
	private JTextField overTimeTField;
	private JTextField weekendTField;
	private JTextField holidayTField;
	private DefaultTableModel dmMove;
	private DefaultTableModel dmFix;
	private JTable fixTable;
	private JTable moveTable;

	public JCI6Ext2SuppForm(TCComponent arg0) throws Exception {
		super(arg0);
		form = (TCComponentForm) arg0;
		tcsession = arg0.getSession();
		loadRendering();
	}

	@Override
	public void loadRendering() throws TCException {
		/**
		 * 设置星期对应的数字
		 * Sun	:	1
		 * Mon	:	2
		 * Tue	:	3
		 * Wed	:	4
		 * Thu	:	5
		 * Fri	:	6
		 * Sat	:	7
		 */
		weekDayToNumMap.clear();
		for (int i = 0; i < weekDays.length; i++) {
			weekDayToNumMap.put(weekDays[i], i);
		}
		
		//查询需要的费率对象，返回的结果不会是null，可能数量为0
		ArrayList<TCComponentItemRevision> itemRevisionList = searchExtRateRevisionOperation();
		//分析查询结果，找到所有的最新已发布版本
		lastPassExtTSERevList = JCIUtil.getLastPassRevision(itemRevisionList);
		/**
		 * 解析结果，获得需要的属性值和对象结构
		 * 保存：1.ArrayList<String[]> extTSEAttrList		2.HashMap<String, TCComponentItemRevision> nameAndComMap
		 */
		getExtTSEAttrsFromExtRateRevisions(lastPassExtTSERevList);
		//获取费率首选项
		componeyNameAndModifierMap = JCIUtil.getModifier(ratePreferenceName, tcsession);
		/**
		 * 获取初始化的日期内容
		 * int jciYear,int jciMonth,List<Date> weekOfMonth,ArrayList<Integer>() weekOfMonthType
		 */
		getDateObejct();
		
		setRenderingReadWrite();
		/**
		 * 获取首选项中配置好的颜色
		 */
		setPreferenceColor();
		/**
		 * 数据库查询ProgramInfo
		 * HashMap<String, String> programinfoMap
		 */
		searchProgram();
		
		initData();
		
		initForm();
		
		
	}

	/**
	 * 初始化表单的显示
	 */
	private void initForm() {
		setLayout(new BorderLayout());
		tabbedPanel = new JTabbedPane(JTabbedPane.TOP);
		tabbedPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		add(tabbedPanel, BorderLayout.CENTER);
		addFirstPanel(tabbedPanel); // 添加详细页
		addSecondPanel(tabbedPanel); // 添加汇总页
		if (isColorConfiguration) {
			addThirdPanel(tabbedPanel);
		}
		tabbedPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				 int index = tabbedPanel.getSelectedIndex();
				System.out.println("切换："+index);
				//刷新SecondPanel
				
			}
		});

		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(1000, 360));
	}
	
	/**
	 * 为JTabbedPane添加第一页
	 * 
	 * @param tabbedPanel
	 */
	private void addFirstPanel(JTabbedPane tabbedPanel) {
		JPanel firstPanel = new JPanel();
		firstPanel.setLayout(new BorderLayout(0, 0));
		tabbedPanel.addTab("Detail", null, firstPanel, null);
		
		//=====================paneltop==================================
		JPanel paneltop = new JPanel();
		firstPanel.add(paneltop, BorderLayout.NORTH);
		paneltop.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		table0 = new JTable();
		table0.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		table0.setModel(new DefaultTableModel(
				new Object[][] {
					{"", "Cost", "N", "O", "W", "H"},
					{"Approved Cost", "", "", "", "", ""},
					{"To be Approved", "", "", "", "", ""},
				},
				new String[] {
					"", "New column", "N", "O", "W", "H"
				}
			){
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		table0.getColumnModel().getColumn(0).setPreferredWidth(140);
		table0.getColumnModel().getColumn(1).setPreferredWidth(70);
		table0.getColumnModel().getColumn(2).setPreferredWidth(70);
		table0.getColumnModel().getColumn(3).setPreferredWidth(70);
		table0.getColumnModel().getColumn(4).setPreferredWidth(70);
		table0.getColumnModel().getColumn(5).setPreferredWidth(70);
		
		DefaultTableCellRenderer r = new DefaultTableCellRenderer(){
			@Override
			public void setHorizontalAlignment(int alignment) {
				// TODO Auto-generated method stub
				super.setHorizontalAlignment(JLabel.CENTER);
			}
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				// TODO Auto-generated method stub
				Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (!isSelected && row == 1) {
					if (column == 2) {
						com.setBackground(normalColor);
					}else if (column == 3) {
						com.setBackground(overtimeColor);
					}else if (column == 4) {
						com.setBackground(weekendColor);
					}else if (column == 5) {
						com.setBackground(holidayColor);
					}
				}
				return com;
			}
		};
		table0.getColumnModel().getColumn(2).setCellRenderer(r);
		table0.getColumnModel().getColumn(3).setCellRenderer(r);
		table0.getColumnModel().getColumn(4).setCellRenderer(r);
		table0.getColumnModel().getColumn(5).setCellRenderer(r);
		paneltop.add(table0);
		//******************************以上是table0********************************************
		JPanel billTypePanel = new JPanel();
		billTypePanel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		paneltop.add(billTypePanel);
		
		billTypePanel.add(new JLabel("Bill Type"));
		billTypePanel.add(new JLabel("  "));
		//Normal的输入
		{
			JPanel timePanel = new JPanel();
			billTypePanel.add(timePanel);
			timePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
			timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
			timePanel.add(new JLabel("Normal"));
			JPanel panel_2 = new JPanel();
			timePanel.add(panel_2);
			{
				JLabel lblNewLabel = new JLabel("  N  ");
				lblNewLabel.setBackground(normalColor);
				lblNewLabel.setOpaque(true);
				panel_2.add(lblNewLabel);
			}
			{
				normalTField = new JTextField();
				panel_2.add(normalTField);
				normalTField.setColumns(3);
				JCIUtil util = new JCIUtil();
				LimitedDocument limitedDocument= util.getLimitedDocument(3, "-.0123456789");
				normalTField.setDocument(limitedDocument);
			}
		}
		//Overtime的输入
		{
			JPanel timePanel = new JPanel();
			billTypePanel.add(timePanel);
			timePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
			timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
			timePanel.add(new JLabel("Overtime"));
			JPanel panel_2 = new JPanel();
			timePanel.add(panel_2);
			{
				JLabel lblNewLabel = new JLabel("  O  ");
				lblNewLabel.setBackground(overtimeColor);
				lblNewLabel.setOpaque(true);
				panel_2.add(lblNewLabel);
			}
			{
				overTimeTField = new JTextField();
				panel_2.add(overTimeTField);
				overTimeTField.setColumns(3);
				JCIUtil util = new JCIUtil();
				LimitedDocument limitedDocument= util.getLimitedDocument(3, "-.0123456789");
				overTimeTField.setDocument(limitedDocument);
			}
		}
		//Weekend的输入
		{
			JPanel timePanel = new JPanel();
			billTypePanel.add(timePanel);
			timePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
			timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
			timePanel.add(new JLabel("Weekend"));
			JPanel panel_2 = new JPanel();
			timePanel.add(panel_2);
			{
				JLabel lblNewLabel = new JLabel("  W  ");
				lblNewLabel.setBackground(weekendColor);
				lblNewLabel.setOpaque(true);
				panel_2.add(lblNewLabel);
			}
			{
				weekendTField = new JTextField();
				panel_2.add(weekendTField);
				weekendTField.setColumns(3);
				JCIUtil util = new JCIUtil();
				LimitedDocument limitedDocument= util.getLimitedDocument(3, "-.0123456789");
				weekendTField.setDocument(limitedDocument);
			}
		}
		//Holiday的输入
		{
			JPanel timePanel = new JPanel();
			billTypePanel.add(timePanel);
			timePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
			timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
			timePanel.add(new JLabel("Holiday"));
			JPanel panel_2 = new JPanel();
			timePanel.add(panel_2);
			{
				JLabel lblNewLabel = new JLabel("  H  ");
				lblNewLabel.setBackground(holidayColor);
				lblNewLabel.setOpaque(true);
				panel_2.add(lblNewLabel);
			}
			{
				holidayTField = new JTextField();
				panel_2.add(holidayTField);
				holidayTField.setColumns(3);
				JCIUtil util = new JCIUtil();
				LimitedDocument limitedDocument= util.getLimitedDocument(3, "-.0123456789");
				holidayTField.setDocument(limitedDocument);
			}
		}
		
		billTypePanel.add(new JLabel("   "));
		JButton assignButton = new JButton("Assign");
		billTypePanel.add(assignButton);
		assignButton.setFont(new Font("微软雅黑", Font.BOLD, 12));
		assignButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				assignButtonAction();
			}
		});
		JButton assignCleanButton = new JButton("CleanAssignValue");
		billTypePanel.add(assignCleanButton);
		assignCleanButton.setFont(new Font("微软雅黑", Font.BOLD, 12));
		assignCleanButton.setBackground(Color.RED);
		assignCleanButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				assignCleanButtonAction();
//				int colCount1 = fixTable.getColumnCount();
//				int colCount2 = moveTable.getColumnCount();
//				System.out.println("fixTable,col0:"+fixTable.getColumnModel().getColumn(0).getPreferredWidth());
//				System.out.println("fixTable,col0:"+fixTable.getColumnModel().getColumn(1).getPreferredWidth());
//				for (int i = 0; i < colCount2; i++) {
//					System.out.println("moveTable,col"+i+":"+moveTable.getColumnModel().getColumn(i).getPreferredWidth());
//				}
			}
		});
		
		//**********************************以上是填值**************************************
		JPanel leftPanel = new JPanel();
		firstPanel.add(leftPanel, BorderLayout.WEST);
		GridBagLayout gbl_leftPanel = new GridBagLayout();
		leftPanel.setLayout(gbl_leftPanel);
		{
			JButton addButton = new JButton("+");
			GridBagConstraints gbc_addButton = new GridBagConstraints();
			gbc_addButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_addButton.gridx = 0;
			gbc_addButton.gridy = 0;
			leftPanel.add(addButton, gbc_addButton);
			addButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
//					addButtonAction();
				}
			});
		}
		{
			JButton minusButton = new JButton("-");
			GridBagConstraints gbc_minusButton = new GridBagConstraints();
			gbc_minusButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_minusButton.gridx = 0;
			gbc_minusButton.gridy = 1;
			leftPanel.add(minusButton, gbc_minusButton);
			minusButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
//					minusButtonAction();
				}
			});
		}
		//**********************************以上是leftPanel**************************************
		JPanel panelCenter = new JPanel();
		firstPanel.add(panelCenter, BorderLayout.CENTER);
		GridBagLayout gbl_panelCenter = new GridBagLayout();
		gbl_panelCenter.rowWeights = new double[]{1.0};
		gbl_panelCenter.columnWeights = new double[]{1.0};
		panelCenter.setLayout(gbl_panelCenter);
		
		dmMove = new DefaultTableModel(data2,moveColumnName){
			/**
			 * 设置是否可编辑：（起始列：0）
			 * 一般只有第1列可编辑
			 * 但是如果form存在对应的明细ExtTse，并且明细ExtTse已经发布，第1列也不能编辑
			 */
			public boolean isCellEditable(int row, int column) {
				Boolean canEdit = false;
				if (column == 1 ) {
					CellBean cellbean = (CellBean) dmMove.getValueAt(row, column);
					if (!cellbean.isPass()) {
						canEdit = true;
					}
				}
				return canEdit;
			}
		};
		
		dmFix = new DefaultTableModel(data1,fixColumnName){
			/**
			 * 设置是否可编辑：（起始列：0）（列数：2）
			 * 一般只有第0列可编辑
			 * 但是如果form存在对应的明细ExtTse，第0列也不可编辑
			 */
			public boolean isCellEditable(int row, int column) {
				Boolean canEdit = false;
				if (column == 0) {
					CellBean cellbean = (CellBean) dmMove.getValueAt(row, column);
					if (!cellbean.isPass()) {
						canEdit = true;
					}
				}
				return canEdit;
			}
		};
		
		//添加监听
		dmFix.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == e.DELETE) {
					System.out.println("dm移除行");
					return;
				}else if (e.getType() == e.UPDATE) {
					int row = e.getFirstRow();  
					int column = e.getColumn(); 
					if (column == 0 && row > 0 && !fixTable.isEditing()) {
						CellBean fix0 = (CellBean) dmFix.getValueAt(row, 0);
						String currentValue = fix0.getShowedValue();
//						String currentValue = dmFix.getValueAt(row, 0).toString();
						String[] extTSEAttrValue = extTSEAttrMap.get(currentValue);
						if (extTSEAttrValue != null) {
							CellBean fix1 = (CellBean) dmFix.getValueAt(row, 1);
							CellBean move0 = (CellBean) dmMove.getValueAt(row, 0);
							CellBean move2 = (CellBean) dmMove.getValueAt(row, 2);
							fix1.setShowedValue(extTSEAttrValue[1]);
							move0.setShowedValue(extTSEAttrValue[2]);
							move2.setShowedValue(extTSEAttrValue[3]);
						}else{
							System.out.println(row+","+column+"的值是空的");
						}
					}
				}
				
			}
		});
		
		//添加监听
		dmMove.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				
				
			}
		});
		//**********************************以上是module和其监听************************************
		fixTable = new JTable(dmFix){
			protected JTableHeader createDefaultTableHeader() {
				GroupableTableHeader th = new GroupableTableHeader(columnModel);
				th.setReorderingAllowed(false);
		          return th;
		      }
		};
		
		TableColumnModel cmfix = fixTable.getColumnModel();
	    ColumnGroup g_name_fix = new ColumnGroup(jciYear+"-"+month[jciMonth-1]);
	    g_name_fix.add(cmfix.getColumn(0));
	    g_name_fix.add(cmfix.getColumn(1));
	    GroupableTableHeader headerFix = (GroupableTableHeader)fixTable.getTableHeader();
	    headerFix.addColumnGroup(g_name_fix);
	    
	    //programinfoMap
	    Vector<String> usernamelist = new Vector<String>();
	    usernamelist.add("");
	    Set<String> extTSEAttrkeys = extTSEAttrMap.keySet();
	    for (String str : extTSEAttrkeys) {
	    	usernamelist.add(str);
		}
//	    JComboBox comboBox = new JAutoCompleteComboBox(usernamelist);
//	    JComboBox comboBox = new JAutoCompleteComboBox(usernamelist);
//	    comboBox.setSelectedItem("");
//	    DefaultCellEditor editor0 = new DefaultCellEditor(comboBox);
	    jciTableCelledit editor0 = new jciTableCelledit(usernamelist);
	    TableColumnModel tcm = fixTable.getColumnModel();
	    tcm.getColumn(0).setCellEditor(editor0);
	    myCellRenderer1 r1 = new myCellRenderer1();
	    r.setHorizontalAlignment(JLabel.CENTER);
	    tcm.getColumn(0).setCellRenderer(r1);
	    tcm.getColumn(1).setCellRenderer(r1);
	    
	    //**********************************以上是fixtable************************************
	    moveTable = new JTable(dmMove){
	    	//设置单元格多选
			ListSelectionModel selectionModel = new DefaultListSelectionModel();
			protected JTableHeader createDefaultTableHeader() {
		          return new GroupableTableHeader(columnModel);
		      }
			@Override
			public boolean isCellSelected(int row, int column) {
				return selectionModel.isSelectedIndex(getIndex(row, column));
			}
			private int getIndex(int rowIndex, int colIndex) {
			      return getColumnCount() * rowIndex + colIndex;
		     }
			@Override
			public void changeSelection(int rowIndex, int columnIndex,
					boolean toggle, boolean extend) {
				boolean selected = isCellSelected(rowIndex, columnIndex);
				   changeSelectionModel(selectionModel, rowIndex, columnIndex, toggle, extend, selected);
				   // Scroll after changing the selection as blit scrolling is immediate,
				   // so that if we cause the repaint after the scroll we end up painting
				   // everything!
				   if (getAutoscrolls()) {
				    Rectangle cellRect = getCellRect(rowIndex, columnIndex, false);
				    if (cellRect != null) {
				     scrollRectToVisible(cellRect);
				    }
				   }
			}
			private void changeSelectionModel(ListSelectionModel sm, int rowIndex, int colIndex, boolean toggle, boolean extend, boolean selected) {
			      int index = getIndex(rowIndex, colIndex);
			      
			      // use customed selection model to save selection status.
			      // at the same time, update the original selection model JTable used.
			      ListSelectionModel rsm = this.getSelectionModel();
			      ListSelectionModel csm = this.getColumnModel().getSelectionModel();
			      if (extend) {
			    if (toggle) {
			     sm.setAnchorSelectionIndex(index);
			     rsm.setAnchorSelectionIndex(rowIndex);
			     csm.setAnchorSelectionIndex(colIndex);
			    } else {
			     sm.setLeadSelectionIndex(index);
			     rsm.setLeadSelectionIndex(rowIndex);
			     csm.setLeadSelectionIndex(colIndex);
			    }
			   } else {
			    if (toggle) {
			     if (selected) {
			      sm.removeSelectionInterval(index, index);
			      rsm.removeSelectionInterval(rowIndex, rowIndex);
			      csm.removeSelectionInterval(colIndex, colIndex);
			     } else {
			      sm.addSelectionInterval(index, index);
			      rsm.addSelectionInterval(rowIndex, rowIndex);
			      csm.addSelectionInterval(colIndex, colIndex);
			     }
			    } else {
			     sm.setSelectionInterval(index, index);
			     rsm.setSelectionInterval(rowIndex, rowIndex);
			     csm.setSelectionInterval(colIndex, colIndex);
			     sm.setAnchorSelectionIndex(index);
			     rsm.setAnchorSelectionIndex(rowIndex);
			     csm.setAnchorSelectionIndex(colIndex);
			    }
			   }
			      repaint();
			  }
		};
		 //**********************************以上是movetable************************************
		 //设置行高和列宽
	    fixTable.setRowHeight(20);
	    moveTable.setRowHeight(20);
	    Integer[] moveTableWidth = {178,147,94,75,36,36,36,36,33};
	    fixTable.getColumnModel().getColumn(0).setPreferredWidth(148);
	    fixTable.getColumnModel().getColumn(1).setPreferredWidth(100);
	    for (int j = 0; j < moveTable.getColumnCount(); j++) {
	    	int cell8 = moveTableWidth[8];
			if (j < 8) {
				moveTable.getColumnModel().getColumn(j).setPreferredWidth(moveTableWidth[j]);
			}else{
				moveTable.getColumnModel().getColumn(j).setPreferredWidth(cell8);
			}
		}
	    
	    String[] week = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
		SimpleDateFormat sdfE = new SimpleDateFormat("E", Locale.ENGLISH);
		for (int i = 0; i < week.length; i++) {
			Date tmpD = weekOfMonth.get(i);
			week[i] = sdfE.format(tmpD);
		}
		 
	    TableColumnModel cmmove = moveTable.getColumnModel();
		GroupableTableHeader headermove = (GroupableTableHeader)moveTable.getTableHeader();
	    ColumnGroup g_name_0 = new ColumnGroup(" ");
	    g_name_0.add(cmmove.getColumn(0));
	    headermove.addColumnGroup(g_name_0);
	    ColumnGroup g_name_1 = new ColumnGroup(" ");
	    g_name_1.add(cmmove.getColumn(1));
	    headermove.addColumnGroup(g_name_1);
	    ColumnGroup g_name_2_7 = new ColumnGroup("Summary");
	    for (int i = 2; i < 8; i++) {
	    	g_name_2_7.add(cmmove.getColumn(i));
		}
	    headermove.addColumnGroup(g_name_2_7);
	    int weekStart=0;
	    for (int i = 8; i < moveColumnName.size(); i++) {
			if (weekStart == 7) {
				weekStart = 0;
			}
			String tmpWeek = week[weekStart];
			weekStart++;
			ColumnGroup g_name_tmp = new ColumnGroup(tmpWeek);
			g_name_tmp.add(cmmove.getColumn(i));
			headermove.addColumnGroup(g_name_tmp);
		}
	    Set<String> programinfoKeyset = programinfoMap.keySet();
	    Vector programinfoV = new Vector();
	    for (String str : programinfoKeyset) {
	    	programinfoV.add(str);
		}
	    for (int i = 0; i < moveColumnName.size(); i++) {
	    	if (i == 1) {
	    		jciTableCelledit cellEditor = new jciTableCelledit(programinfoV);
				cmmove.getColumn(i).setCellEditor(cellEditor);
			}else{
				myCellRenderer1 cellRender = new myCellRenderer1();
				cmmove.getColumn(i).setCellRenderer(cellRender);
			}
	    }
	    
	    fixTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		moveTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		moveTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		moveTable.setCellSelectionEnabled(true);
		
		JScrollPane scroll = new JScrollPane(moveTable);
	    JViewport viewport = new JViewport();
	    viewport.setView(fixTable);
	    viewport.setPreferredSize(fixTable.getPreferredSize());
	    scroll.setRowHeaderView(viewport);
	    scroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixTable
	        .getTableHeader());
	    
	    GridBagConstraints gbc_scroll = new GridBagConstraints();
	    gbc_scroll.fill = GridBagConstraints.BOTH;
	    gbc_scroll.gridy = 0;
	    gbc_scroll.gridx = 0;
	    panelCenter.add(scroll, gbc_scroll);
	    
	   
		//**********************************以上是panelCenter************************************
		
		
		//************************addFirstPanel end******************************
	}
	
	/**
	 * 为JTabbedPane添加第二页:汇总页
	 * 
	 * @param tabbedPanel
	 */
	private void addSecondPanel(JTabbedPane tabbedPanel) {
		JPanel secondPanel = new JPanel();
		secondPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		tabbedPanel.addTab("Person Summary", null, secondPanel, null);
		secondPanel.setLayout(new BorderLayout(0, 0));
	}
	

	/**
	 * 为JTabbedPane添加第三页：颜色配置也
	 * @param tabbedPanel
	 */
	private void addThirdPanel(JTabbedPane tabbedPanel){
		
	}
	
	private void initData() throws TCException{
		//设置fixTable和moveTable的handler
		fixColumnName = new Vector<String>();
		fixColumnName.add("Name");
		fixColumnName.add("Global ID");
		String[] moveColumnName_first = { "Company", "Program", "Onboard Date","Cost", "N", "O", "W", "H" };
		moveColumnName = new Vector<String>();
		for (String string : moveColumnName_first) {
			moveColumnName.add(string);
		}
		for (int i = 0; i < weekOfMonth.size(); i++) {
			String day_count = "" + (i + 1);
			moveColumnName.add(day_count);
		}
		
		data1 = new Vector();
		data2 = new Vector();
		TCComponent[] extComs = form.getReferenceListProperty("jci6_Ext2Detail");
		if (extComs == null || extComs.length == 0) {

		} else {
			int lasteExtComsCount = extComs.length;
			int dataCount = 0;
			for (int i = 0; i < lasteExtComsCount; i++) {
				TCComponent currentExtCom = extComs[i];
				String currentExtComName = currentExtCom.getStringProperty("object_name");
				if (currentExtComName.startsWith("Head")) {
					dataCount++;
					Vector data1_1 = new Vector();
					Vector data2_1 = new Vector();
					data2_1.setSize(moveColumnName.size());
					CellBean fix0 = new CellBean(currentExtCom, CellBean.columnType_fix0_Name, CellBean.cellType_Null);
					CellBean fix1 = new CellBean(currentExtCom, CellBean.columnType_fix1_GlobalID, CellBean.cellType_Null);
					CellBean move0 = new CellBean(currentExtCom, CellBean.columnType_move0_Company, CellBean.cellType_Null);
					CellBean move1 = new CellBean(currentExtCom, CellBean.columnType_move1_Program, CellBean.cellType_Null);
					CellBean move2 = new CellBean(currentExtCom, CellBean.columnType_move2_OnboardDate, CellBean.cellType_Null);
					CellBean move3 = new CellBean(currentExtCom, CellBean.columnType_move3_Cost, CellBean.cellType_Null);
					move3.setVisible(false);
					CellBean move4 = new CellBean(currentExtCom, CellBean.columnType_move4_N, CellBean.cellType_Null);
					CellBean move5 = new CellBean(currentExtCom, CellBean.columnType_move5_O, CellBean.cellType_Null);
					CellBean move6 = new CellBean(currentExtCom, CellBean.columnType_move6_W, CellBean.cellType_Null);
					CellBean move7 = new CellBean(currentExtCom, CellBean.columnType_move7_H, CellBean.cellType_Null);
					data1_1.add(fix0);
					data1_1.add(fix1);
					data2_1.setElementAt(move0, 0);
					data2_1.setElementAt(move1, 1);
					data2_1.setElementAt(move2, 2);
					data2_1.setElementAt(move3, 3);
					data2_1.setElementAt(move4, 4);
					data2_1.setElementAt(move5, 5);
					data2_1.setElementAt(move6, 6);
					data2_1.setElementAt(move7, 7);
					for (int j = 8; j < moveColumnName.size(); j++) {
						int tmptype = weekOfMonthType.get(j-8);
						CellBean move8 = null;
						switch (tmptype) {
						case 1:
							move8 = new CellBean(CellBean.columnType_move8_Day, CellBean.cellType_N);
							break;
						case 2:
							move8 = new CellBean(CellBean.columnType_move8_Day, CellBean.cellType_W);
							break;
						case 3:
							move8 = new CellBean(CellBean.columnType_move8_Day, CellBean.cellType_H);
							break;
						}
						data2_1.setElementAt(move8, j);
					}
					data1.add(data1_1);
					data2.add(data2_1);
				}else if (currentExtComName.startsWith("Follow")) {
					Vector data2_1 = (Vector) data2.get(dataCount-1);
					CellBean move8 = null;
					for (int j = 0; j < weekOfMonthType.size(); j++) {
						int tmptype = weekOfMonthType.get(j);
						int tmpDay = Integer.parseInt(currentExtComName.split("_")[5]);
						if (j+1 == tmpDay) {
							switch (tmptype) {
							case 1:
								move8 = new CellBean(currentExtCom, CellBean.columnType_move8_Day, CellBean.cellType_N);
								break;
							case 2:
								move8 = new CellBean(currentExtCom, CellBean.columnType_move8_Day, CellBean.cellType_W);
								break;
							case 3:
								move8 = new CellBean(currentExtCom, CellBean.columnType_move8_Day, CellBean.cellType_H);
								break;
							}
							data2_1.setElementAt(move8, j+8);
						}
					}
					data2.setElementAt(data2_1, dataCount-1);
				}
				
			}
		}
		//填补
		for (int i = 0; i < data2.size(); i++) {
			Vector data2_1 = (Vector) data2.get(i);
			Double sumary_N_A = 0d;
			Double sumary_N_noA = 0d;
			Double sumary_O_A = 0d;
			Double sumary_O_nA = 0d;
			Double sumary_W_A = 0d;
			Double sumary_W_noA = 0d;
			Double sumary_H_A = 0d;
			Double sumary_H_noA = 0d;
			for (int j = 8; j < moveColumnName.size(); j++) {
				CellBean moveCell = (CellBean) data2_1.get(j);
				if (!moveCell.hasExtTSE()) {
					continue;
				}
				String showValue = moveCell.getShowedValue();
				Double showValue_double = Double.parseDouble(showValue);
				if (moveCell.getCellType().equals(CellBean.cellType_N)) {
					if (moveCell.isPass()) {
						sumary_N_A += showValue_double;
					}else{
						sumary_N_noA += showValue_double;
					}
				}else if (moveCell.getCellType().equals(CellBean.cellType_O)) {
					if (moveCell.isPass()) {
						sumary_O_A += showValue_double;
					}else{
						sumary_O_nA += showValue_double;
					}
				}else if (moveCell.getCellType().equals(CellBean.cellType_W)) {
					if (moveCell.isPass()) {
						sumary_W_A += showValue_double;
					}else{
						sumary_W_noA += showValue_double;
					}
				}else if (moveCell.getCellType().equals(CellBean.cellType_H)) {
					if (moveCell.isPass()) {
						sumary_H_A += showValue_double;
					}else{
						sumary_H_noA += showValue_double;
					}
				}
			}
			CellBean moveN = (CellBean) data2_1.get(4);
			CellBean moveO = (CellBean) data2_1.get(5);
			CellBean moveW = (CellBean) data2_1.get(6);
			CellBean moveH = (CellBean) data2_1.get(7);
			moveN.setValue(sumary_N_A, sumary_N_noA);
			moveO.setValue(sumary_O_A, sumary_O_nA);
			moveW.setValue(sumary_W_A, sumary_W_noA);
			moveH.setValue(sumary_H_A, sumary_H_noA);
			data2_1.setElementAt(moveN, 4);
			data2_1.setElementAt(moveO, 5);
			data2_1.setElementAt(moveW, 6);
			data2_1.setElementAt(moveH, 7);
			data2.setElementAt(data2_1, i);
		}
		
		//添加空白行
		for (int i = 0; i < 4; i++) {
			Vector date1_1 = getNewDateX_1("fix");
			Vector date2_1 = getNewDateX_1("move");
			data1.add(date1_1);
			data2.add(date2_1);
			
		}
		
		
	}
	
	
	/**
	 * 创建新行：
	 * @param type	fix/move
	 * @return
	 */
	private Vector getNewDateX_1(String type){
		Vector dateX_1 = new Vector();
		if (type.equals("fix")) {
			CellBean fix0 = new CellBean(CellBean.columnType_fix0_Name, CellBean.cellType_Null);
			CellBean fix1 = new CellBean(CellBean.columnType_fix1_GlobalID, CellBean.cellType_Null);
			dateX_1.add(fix0);
			dateX_1.add(fix1);
		}else if (type.equals("move")) {
			for (int i = 0; i < moveColumnName.size(); i++) {
				CellBean moveCell = null;
				if (i == 0) {
					moveCell = new CellBean(CellBean.columnType_move0_Company, CellBean.cellType_Null);
				}else if (i == 1) {
					moveCell = new CellBean(CellBean.columnType_move1_Program, CellBean.cellType_Null);
				}else if (i == 2) {
					moveCell = new CellBean(CellBean.columnType_move2_OnboardDate, CellBean.cellType_Null);
				}else if (i == 3) {
					moveCell = new CellBean(CellBean.columnType_move3_Cost, CellBean.cellType_Null);
					moveCell.setVisible(false);
				}else if (i == 4) {
					moveCell = new CellBean(CellBean.columnType_move4_N, CellBean.cellType_Null);
				}else if (i == 5) {
					moveCell = new CellBean(CellBean.columnType_move5_O, CellBean.cellType_Null);
				}else if (i == 6) {
					moveCell = new CellBean(CellBean.columnType_move6_W, CellBean.cellType_Null);
				}else if (i == 7) {
					moveCell = new CellBean(CellBean.columnType_move7_H, CellBean.cellType_Null);
				}else if (i >= 8) {
					int tmptype = weekOfMonthType.get(i-8);
					switch (tmptype) {
					case 1:
						moveCell = new CellBean(CellBean.columnType_move8_Day, CellBean.cellType_N);
						break;
					case 2:
						moveCell = new CellBean( CellBean.columnType_move8_Day, CellBean.cellType_W);
						break;
					case 3:
						moveCell = new CellBean( CellBean.columnType_move8_Day, CellBean.cellType_H);
						break;
					}
				}
				dateX_1.add(moveCell);
			}
		}
		return dateX_1;
	}
	
	private void searchProgram() {
		String USERNAMR = "infodba";
		String PASSWORD = "infodba";
		String DRVIER = "oracle.jdbc.OracleDriver";
		String URL = "jdbc:oracle:thin:@10.178.188.244:1521:tc";
		Connection connection = null;
		programinfoMap = new HashMap<String, String>();
		try {
			Class.forName(DRVIER);
			connection = DriverManager.getConnection(URL, USERNAMR, PASSWORD);
			System.out.println("成功连接数据库");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("class not find !", e);
		} catch (SQLException e) {
			throw new RuntimeException("get connection error!", e);
		}

		String sql = "select " +
				"i.PITEM_ID,w.POBJECT_NAME,p.PUID " +
				"from PITEM i,PWORKSPACEOBJECT w,PJCI6_PROGRAMINFO p " +
				"where i.PUID=w.PUID and p.PUID = i.PUID and w.PUID = p.PUID";
		try {
			Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(sql);
			 while (rs.next()){
				 //programInfoList
				 String programInfoID = rs.getString(1);
				 String programInfoName = rs.getString(2);
				 
				 String programInfoShowName = programInfoID+"-"+programInfoName;
				 programinfoMap.put(programInfoShowName, programInfoID);
             }
			 rs.close();
             stmt.close();
             connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}  
	}
	
	private void setPreferenceColor(){
		String[] color  = tcsession.getPreferenceService().getStringArray(4, color_preference);
		for (String str : color) {
			String[] strs = str.split("=");
			if (strs[0].equals("CanEditColor")) {
				isColorConfiguration = Boolean.parseBoolean(strs[1]);
			}else{
				if (strs[0].equals("normalColor")) {
					String[] rgbs = strs[1].split(",");
					normalColor = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("overtimeColor")) {
					String[] rgbs = strs[1].split(",");
					overtimeColor = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("weekendColor")) {
					String[] rgbs = strs[1].split(",");
					weekendColor = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("holidayColor")) {
					String[] rgbs = strs[1].split(",");
					holidayColor = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("normalColorSelected")) {
					String[] rgbs = strs[1].split(",");
					normalColorSelected = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("normalColorNoSelectedIsNull")) {
					String[] rgbs = strs[1].split(",");
					normalColorNoSelectedIsNull = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("normalColorNoSelectedIsZero")) {
					String[] rgbs = strs[1].split(",");
					normalColorNoSelectedIsZero = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("normalColorNoSelectedIsEight")) {
					String[] rgbs = strs[1].split(",");
					normalColorNoSelectedIsEight = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("normalColorNoSelectedIsNotEight")) {
					String[] rgbs = strs[1].split(",");
					normalColorNoSelectedIsNotEight = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("overtimeColorSelected")) {
					String[] rgbs = strs[1].split(",");
					overtimeColorSelected = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("overtimeColorNoSelected")) {
					String[] rgbs = strs[1].split(",");
					overtimeColorNoSelected = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("weekendColorSelected")) {
					String[] rgbs = strs[1].split(",");
					weekendColorSelected = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("weekendColorNoSelected")) {
					String[] rgbs = strs[1].split(",");
					weekendColorNoSelected = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("holidayColorSelected")) {
					String[] rgbs = strs[1].split(",");
					holidayColorSelected = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}else if (strs[0].equals("holidayColorNoSelected")) {
					String[] rgbs = strs[1].split(",");
					holidayColorNoSelected = new Color(Integer.parseInt(rgbs[0]), Integer.parseInt(rgbs[1]), Integer.parseInt(rgbs[2]));
				}
			}
		}
	}
	
	/**
	 * 获取初始化的日期内容
	 * @throws TCException 
	 */
	private void getDateObejct() throws TCException{
		jciYear = form.getIntProperty("jci6_Year");
		jciMonth = form.getIntProperty("jci6_Month");
		weekOfMonth = JCIUtil.dayWithWeekend(jciYear,jciMonth);
		/**
		 * normal:1	weekend:2	holiday:3
		 */
		weekOfMonthType = new ArrayList<Integer>();
		ArrayList<Date> holidayList = JCIUtil.getPreferenceHoliday(tcsession,holiday_preference);
		for (Date d : weekOfMonth) {
			if (holidayList.contains(d)) {
				weekOfMonthType.add(3);
			}else if (JCIUtil.dateToWeek(d).contains("Sat") || JCIUtil.dateToWeek(d).contains("Sun")) {
				weekOfMonthType.add(2);
			}else{
				weekOfMonthType.add(1);
			}
		}
	}
	
	/**
	 * 通过查询获得已发布版本：__YFJC_LCC_ExtRateRevision 条件：用户、jci6_Division、jci6_Section
	 * 返回值：获得已发布 测试用，暂时使用的用户ujoy1
	 * 
	 * @return
	 * @throws NotLoadedException
	 * @throws TCException
	 */
	private ArrayList<TCComponentItemRevision> searchExtRateRevisionOperation()throws TCException {
		ArrayList<TCComponentItemRevision> itemRevisionList = new ArrayList<TCComponentItemRevision>();
		String jci6_Division = "";
		String jci6_Section = "";
		try {
			jci6_Division = form.getPropertyDisplayableValue("jci6_Division");
			jci6_Section = form.getPropertyDisplayableValue("jci6_Section");
		} catch (NotLoadedException e) {
			e.printStackTrace();
		}
		String userID = tcsession.getUser().getUserId();
		//userID = "rosedale1";
		String[] searchAttrs = { "Division", "Section", "name", "outsourceid" };
		String[] searchValues = { jci6_Division, jci6_Section, "Pass", userID };
		TCComponent[] searchItemRevisions = JCIUtil.query(tcsession, QUERY_NAME,
				searchAttrs, searchValues);
		if (searchItemRevisions != null && searchItemRevisions.length > 0) {
			for (TCComponent tcComponent : searchItemRevisions) {
				itemRevisionList.add((TCComponentItemRevision) tcComponent);
			}
		}
		
		System.out.println(QUERY_NAME+"查询到的对象的数量是："+itemRevisionList.size());
		
		return itemRevisionList;
	}
	
	/**
	 * 通过得到的最新的已发布ExtRateRevision，获得ExtTSE需要的属性 JCI6ExtRateRevision JCI6_ExtTSE
	 * jci6_Division jci6_Division TypeReference jci6_Section jci6_Section
	 * TypeReference item.jci6_Company jci6_Company String object_name
	 * jci6_ExtName String jci6_Onboard jci6_Onboard Date jci6_Rate jci6_Rate
	 * Double jci6_GID jci6_GID String
	 * 
	 * @param lastPassRevList
	 * @return
	 * @throws TCException 
	 */
	private void getExtTSEAttrsFromExtRateRevisions(ArrayList<TCComponentItemRevision> lastPassRevList) throws TCException {
		extTSEAttrMap.clear();
		if (lastPassRevList.size() == 0) {
			return;
		}
		for (TCComponentItemRevision ExtRateRevision : lastPassRevList) {
			String[] attrs = new String[4];
			String jci6_ExtName = ExtRateRevision.getStringProperty("object_name");
			String jci6_GID = ExtRateRevision.getStringProperty("jci6_GID");
			TCComponent[] jci6_Company_1 = ExtRateRevision.getItem().getReferenceListProperty("IMAN_reference");
			String jci6_Company_v = "";
			if (jci6_Company_1 != null && jci6_Company_1.length == 1) {
				jci6_Company_v = jci6_Company_1[0].getProperty("user_name")
						+ "(" + jci6_Company_1[0].getProperty("user_id")
						+ ")";
			}
			Date jci6_Onboard_1 = ExtRateRevision.getDateProperty("jci6_Onboard");
			String jci6_Onboard_v = JCIUtil.formatDate(jci6_Onboard_1);
			if (jci6_Onboard_v.isEmpty()) {
				continue;
			}else{
				attrs[0] = jci6_ExtName;
				attrs[1] = jci6_GID;
				attrs[2] = jci6_Company_v;
				attrs[3] = jci6_Onboard_v;
				extTSEAttrMap.put(jci6_ExtName,attrs);
				nameAndComMap.put(jci6_ExtName, ExtRateRevision);
				
			}
		}
	}

	@Override
	public void saveRendering() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 自定义DefaultTableCellRenderer，用于颜色显示
	 * @author Administrator
	 *
	 */
	class myCellRenderer1 extends DefaultTableCellRenderer{
		@Override
		public void setHorizontalAlignment(int alignment) {
			super.setHorizontalAlignment(JLabel.CENTER);
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			CellBean bean = (CellBean) value;
			boolean isPass = bean.isPass();
			String showValue = bean.getShowedValue();
			boolean visible = bean.isVisible();
			int columnType = bean.getColumnType();
			//一般颜色处理
			if (columnType == CellBean.columnType_fix0_Name) {
				if (isSelected) {
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setBackground(table.getBackground());
				}
			}else if (columnType == CellBean.columnType_fix1_GlobalID) {
				if (isSelected) {
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setBackground(table.getBackground());
				}
			}else if (columnType == CellBean.columnType_move0_Company) {
				if (isSelected) {
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setBackground(table.getBackground());
				}
			}else if (columnType == CellBean.columnType_move1_Program) {
				if (isSelected) {
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setBackground(table.getBackground());
				}
			}else if (columnType == CellBean.columnType_move2_OnboardDate) {
				if (isSelected) {
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setBackground(table.getBackground());
				}
			}else if (columnType == CellBean.columnType_move3_Cost) {
				if (isSelected) {
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setBackground(table.getBackground());
				}
			}else if (columnType == CellBean.columnType_move4_N) {
				if (isSelected) {
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setBackground(normalColor);
				}
			}else if (columnType == CellBean.columnType_move5_O) {
				if (isSelected) {
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setBackground(overtimeColor);
				}
			}else if (columnType == CellBean.columnType_move6_W) {
				if (isSelected) {
//					super.setForeground(table.getSelectionForeground());
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setBackground(weekendColor);
				}
			}else if (columnType == CellBean.columnType_move7_H) {
				if (isSelected) {
					super.setBackground(table.getSelectionBackground());
				}else{
					super.setBackground(holidayColor);
				}
			}else if (columnType == CellBean.columnType_move8_Day) {
				String cellType = bean.getCellType();
				if (cellType.equals(CellBean.cellType_N)) {
					//TODO 特殊处理，分颜色
					if (isSelected) {
						super.setBackground(table.getSelectionBackground());
					}else{
						super.setBackground(table.getBackground());
					}
				}else if (cellType.equals(CellBean.cellType_O)) {
					if (isSelected) {
						super.setBackground(overtimeColorSelected);
					}else{
						super.setBackground(overtimeColorNoSelected);
					}
				}else if (cellType.equals(CellBean.cellType_W)) {
					if (isSelected) {
						super.setBackground(weekendColorSelected);
					}else{
						super.setBackground(weekendColorNoSelected);
					}
				}else if (cellType.equals(CellBean.cellType_H)) {
					if (isSelected) {
						super.setBackground(holidayColorSelected);
					}else{
						
						super.setBackground(holidayColorNoSelected);
					}
				}
			}
			
			//发布后灰色显示
			if (isPass) {
				this.setForeground(Color.GRAY);
				this.setFont(table.getFont().deriveFont(Font.BOLD));
			}else{
				setForeground(Color.BLACK);
			}
			if (visible) {
				this.setValue(showValue);
			}else{
				this.setValue("");
			}
			return this;
		}
		
	}
	
	class jciTableCelledit extends iComboBox implements TableCellEditor{
		
		
		 //EventListenerList:保存EventListener 列表的类。
       private EventListenerList listenerList = new EventListenerList();
       //ChangeEvent用于通知感兴趣的参与者事件源中的状态已发生更改。
       private ChangeEvent changeEvent = new ChangeEvent(this);
       

		public jciTableCelledit(Vector list) {
			super();
			if (list == null || list.size() == 0) {
				addItem("");
			}else{
				setItems(list);
			}
			setAutoCompleteSuggestive(true);
		}
		
		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return getSelectedItem().toString();
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			// TODO Auto-generated method stub
			return true;
		}

		private void fireEditingStopped(){
           CellEditorListener listener;
           Object[]listeners = listenerList.getListenerList();
           for(int i = 0; i < listeners.length; i++){
                    if(listeners[i]== CellEditorListener.class){
                             //之所以是i+1，是因为一个为CellEditorListener.class（Class对象），
                             //接着的是一个CellEditorListener的实例
                             listener= (CellEditorListener)listeners[i+1];
                             //让changeEvent去通知编辑器已经结束编辑
                //在editingStopped方法中，JTable调用getCellEditorValue()取回单元格的值，
                             //并且把这个值传递给TableValues(TableModel)的setValueAt()
                             listener.editingStopped(changeEvent);
                    }
           }
 }
		
		@Override
		public boolean stopCellEditing() {
			fireEditingStopped();//请求终止编辑操作从JTable获得
           return true;
		}

		@Override
		public void cancelCellEditing() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {
			listenerList.add(CellEditorListener.class,l);
			
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
			listenerList.remove(CellEditorListener.class,l);
			
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			setSelectedItem(value);
			table.getValueAt(row, column);
			//String value1 = bean.getShowedValue();
			return this;
		}
		
	}

}
