//package com.yfjc.workdayhourform;
//
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Vector;
//
//import javax.swing.JButton;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTable;
//import javax.swing.ListSelectionModel;
//import javax.swing.ScrollPaneConstants;
//import javax.swing.SwingConstants;
//import javax.swing.event.TableModelEvent;
//import javax.swing.event.TableModelListener;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.JTableHeader;
//import javax.swing.table.TableColumn;
//
//import com.teamcenter.rac.aif.kernel.AIFComponentContext;
//import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
//import com.teamcenter.rac.common.create.CreateInstanceInput;
//import com.teamcenter.rac.common.create.IBOCreateDefinition;
//import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
//import com.teamcenter.rac.kernel.ListOfValuesInfo;
//import com.teamcenter.rac.kernel.TCComponent;
//import com.teamcenter.rac.kernel.TCComponentForm;
//import com.teamcenter.rac.kernel.TCComponentGroup;
//import com.teamcenter.rac.kernel.TCComponentGroupMember;
//import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
//import com.teamcenter.rac.kernel.TCComponentProcess;
//import com.teamcenter.rac.kernel.TCComponentProject;
//import com.teamcenter.rac.kernel.TCComponentQuery;
//import com.teamcenter.rac.kernel.TCComponentQueryType;
//import com.teamcenter.rac.kernel.TCComponentRole;
//import com.teamcenter.rac.kernel.TCComponentSchedule;
//import com.teamcenter.rac.kernel.TCComponentScheduleTask;
//import com.teamcenter.rac.kernel.TCComponentTask;
//import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
//import com.teamcenter.rac.kernel.TCComponentUser;
//import com.teamcenter.rac.kernel.TCException;
//import com.teamcenter.rac.kernel.TCProperty;
//import com.teamcenter.rac.kernel.TCSession;
//import com.teamcenter.rac.kernel.TCTextService;
//import com.teamcenter.rac.util.ButtonLayout;
//import com.teamcenter.rac.util.MessageBox;
//import com.teamcenter.rac.util.Registry;
//import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
//import com.yfjcebp.extsupport.JCI6YFJCUtil;
//
//public class WorkdayHourPanel_20140903 extends JPanel implements ActionListener {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	public WDHTable table;
//	public JButton saveBtn;
//	public JButton cancelBtn;
//	public JButton addBtn;
//	public JButton delBtn;
//	public JButton surestartBtn;
//	public DefaultTableModel dtm;
//	private boolean canEdit;
//	private Vector<String> extNames;// 外包人员姓名
//	private Vector<String> compareUserV;//
//	private Vector<String> projectV;// 当前登录用户所参与的项目名
//	private Vector<String> taskV;// 时间表任务名
//	private Vector<TCComponent> oldCompVec;// 操作前的对象
//	// private Vector<TCComponent> newCompVec;// 操作后需要保存的对象
//	private Vector<Vector<String>> data;// 初始化的data
//	private TCSession session;
//	private TCComponentForm form;
//	private TCProperty tcProperty;
//	private String[] lovValues = null;
//	private ListOfValuesInfo loi;
//	private String[] lovValueDiscrip = null;
//	private Map<String, TCComponentScheduleTask> taskMap;// 时间表任务名，时间表任务
//	private Map<String, TCComponentProject> projectMap;// 项目名，项目
//	private TCComponentTaskTemplate template = null;// 流程模板
//	private TCComponentUser loginUser;// 当前登录用户
//	private WorkdayHourDialog dialog;
//	private Vector<String> hV;
//	private String[] wdhProNames = new String[] { "jci6_UserName",
//			"jci6_Project", "jci6_Task", "jci6_Division", "jci6_Hour",
//			"jci6_ExtraHour", "jci6_Year", "jci6_Month", "jci6_ownProxy" };
//	private int lastYear;
//	private int lastMonth;
//	private boolean isprocess;
//	private String defaultNorHours;
//	private double defaultHour;
//
//	// add 8-26
//	private TCComponentGroup divisionGroup;
//	private String divisionName;
//	private Map<String, String> ext_division;
//	private Registry reg = Registry.getRegistry(this);
//	private String extTaskName = reg.getString("ExtHourTaskName");
//	private 	Vector<TCComponentGroupMember> signoffVec = new Vector<TCComponentGroupMember>();
//
//	public WorkdayHourPanel_20140903(WorkdayHourDialog dialog, TCSession session,
//			TCComponentTaskTemplate template, Map<String, String> ext_division,
//			String divisionName, TCComponentGroup divisionGroup) {
//		this.dialog = dialog;
//		canEdit = true;
//		this.session = session;
//		this.ext_division = ext_division;
//		this.template = template;
//		this.divisionName = divisionName;
//		this.divisionGroup = divisionGroup;
//		taskMap = new HashMap<String, TCComponentScheduleTask>();
//		projectMap = new HashMap<String, TCComponentProject>();
//		data = new Vector<Vector<String>>();
//		projectV = new Vector<String>();
//		taskV = new Vector<String>();
//		compareUserV = new Vector<String>();
//		try {
//			loginUser = session.getUser();
//			loi = TCComponentListOfValuesType.findLOVByName(session,
//					"JCI6_NormalHours").getListOfValues();
//			lovValueDiscrip = loi.getDescriptions();
//			lovValues = loi.getStringListOfValues();
//			defaultNorHours  = getDefaultNormalHour();
//			defaultHour = Double.valueOf(defaultNorHours);
//			extNames = new Vector<String>();
//			getDivisionGroup();
//			getNewExt();
//			compareUserV.addAll(extNames);
//			getReviewUser();
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		getYearAndLastMonth();
//		initTableDataVector(null);
//		initUI();
//	}
//
//	/**
//	 * 
//	 * @param flag
//	 *            是否能编辑
//	 * @param flag1
//	 * @param v
//	 * @param f
//	 * @param p
//	 * @param t
//	 */
//	public WorkdayHourPanel_20140903(boolean isprocess, boolean isEdit,
//			Map<String, String> ext_division, TCComponentForm f, TCProperty p,
//			TCComponentTaskTemplate t) {
//		System.out.println("WorkdayHourPanel form");
//		this.isprocess = isprocess;
//		canEdit = isEdit;
//		this.ext_division = ext_division;
//		form = f;
//		tcProperty = p;
//		template = t;
//		taskMap = new HashMap<String, TCComponentScheduleTask>();
//		projectMap = new HashMap<String, TCComponentProject>();
//		data = new Vector<Vector<String>>();
//		projectV = new Vector<String>();
//		taskV = new Vector<String>();
//		compareUserV = new Vector<String>();
//		try {
//			session = form.getSession();
//			loginUser = session.getUser();
//			loi = TCComponentListOfValuesType.findLOVByName(session,
//					"JCI6_NormalHours").getListOfValues();
//			lovValueDiscrip = loi.getDescriptions();
//			lovValues = loi.getStringListOfValues();
//			defaultNorHours  = getDefaultNormalHour();
//			defaultHour = Integer.valueOf(defaultNorHours);
//			extNames = new Vector<String>();
//			getDivisionGroup();
//			getNewExt();
//			compareUserV.addAll(extNames);
//			getReviewUser();
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//
//		initTableDataVector(tcProperty.getReferenceValueArray());
//		initUI();
//	}
//
//	public void initUI() {
//		
//		String[] header = new String[] { "User", "Program", "Task",
//				"Normal Hours", "Overtime Hours", "Division" };
//		hV = new Vector<String>();
//		for (int i = 0; i < header.length; i++) {
//			hV.add(header[i]);
//		}
//		dtm = new DefaultTableModel(data, hV);
//		table = new WDHTable(dtm,extNames) {
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public boolean isCellEditable(int row, int column) {
//				if (column == 5) {
//					return false;
//				}
//				return canEdit;
//			}
//		};
//		table.getTableHeader().setReorderingAllowed(false);
//		dtm.addTableModelListener(new TableModelListener() {
//			@Override
//			public void tableChanged(TableModelEvent e) {
//				int col = e.getColumn();
//				int row = e.getLastRow();
//				if (col == 1) {
//					// 载入任务
//					if (dtm.getValueAt(row, col) == null) {
//						setTaskVector("");
//					} else {
//						setTaskVector(dtm.getValueAt(row, col).toString());
//					}
//					int size = taskV.size();
//					if (size == 0) {
//						table.setValueAt(null, row, col + 1);
//					} else {
//						String s = (String) table.getValueAt(row, col + 1);
//						boolean hasVal = false;
//						if (s != null && !s.trim().equals("")) {
//							for (int i = 0; i < size; i++) {
//								if (taskV.get(i).equals(s)) {
//									hasVal = true;
//									break;
//								}
//							}
//						}
//						if (!hasVal) {
//							table.setValueAt(null, row, col + 1);
//						} else {
//							System.out.println("有值");
//						}
//					}
//					TableColumn tableColumn = table.getColumnModel().getColumn(
//							2);
//					ComboxCellEditor editor = new ComboxCellEditor(taskV, 2);
//					tableColumn.setCellEditor(editor);
//				}else if(col == 0){
//					//人名
//					String currentUserName = (String) table.getValueAt(row, col);
//					double hourCnt = defaultHour;
//					for(int i=0,size=table.getRowCount();i<size;i++){
//						String name = (String) table.getValueAt(i, col);
//						if(currentUserName.equals(name) && i!= row){
//							hourCnt = hourCnt - Double.valueOf((String)table.getValueAt(i, 3));
//						}
//					}
//					table.setValueAt(String.valueOf(hourCnt), row, 3);
//				}else if(col==3){
//					//Normal Hours
//					String currentUserName = (String) table.getValueAt(row, 0);
//					double hourCnt = defaultHour;
//					for(int i=0,size=table.getRowCount();i<size;i++){
//						String name = (String) table.getValueAt(i, 0);
//						if(currentUserName.equals(name) && i!= row){
//							hourCnt = hourCnt - Double.valueOf((String)table.getValueAt(i, 3));
//						}
//					}
//					Double current = Double.valueOf((String) table.getValueAt(row, 3));
//					if(current > hourCnt){
//						MessageBox.post("超过当月法定工时", "INFORMATION",MessageBox.INFORMATION);
//						table.setValueAt(String.valueOf(hourCnt), row, col);
//					}
//				}
//			}
//		});
//		//fitTableColumns(table);
//		table.setRowHeight(30);
//		table.setRowSelectionAllowed(true);
//		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//		table.addMouseListener(new MouseListener() {
//
//			@Override
//			public void mouseReleased(MouseEvent arg0) {
//
//			}
//
//			@Override
//			public void mousePressed(MouseEvent arg0) {
//				if (table.getSelectedRow() >= 0) {
//					delBtn.setEnabled(true);
//				} else {
//					delBtn.setEnabled(false);
//				}
//				int column = table.getSelectedColumn();
//				int row = table.getSelectedRow();
//				if (column > 0) {
//					Object ob = table.getValueAt(row, column - 1);
//					if (ob != null) {
//						dtm.setValueAt(ob.toString(), row, column - 1);
//						if (column == 2) {
//							setTaskVector(ob.toString());
//							TableColumn tableColumn = table.getColumnModel()
//									.getColumn(column);
//							// 8-18
//							ComboxCellEditor editor = new ComboxCellEditor(
//									taskV, column);
//							tableColumn.setCellEditor(editor);
//						}
//
//					}
//				}
//
//			}
//
//			@Override
//			public void mouseExited(MouseEvent arg0) {
//
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent arg0) {
//
//			}
//
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//			}
//		});
//
//		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
//		render.setHorizontalAlignment(SwingConstants.CENTER);
//		for (int i = 0; i < table.getColumnCount(); i++) {
//			TableColumn tableColumn = table.getColumnModel().getColumn(i);
//			tableColumn.setCellRenderer(render);
//			if (i == 1) {
//				ComboxCellEditor editor = new ComboxCellEditor(projectV, i);
//				tableColumn.setCellEditor(editor);
//			} else if (i == 2) {
//				ComboxCellEditor editor = new ComboxCellEditor(taskV, i);
//				tableColumn.setCellEditor(editor);
//			}
//		}
//		addBtn = new JButton("+");
//		addBtn.addActionListener(this);
//		addBtn.setVisible(canEdit);
//		delBtn = new JButton(" - ");
//		delBtn.addActionListener(this);
//		delBtn.setVisible(canEdit);
//		delBtn.setEnabled(false);
//
//		cancelBtn = new JButton("Cancel");
//		cancelBtn.addActionListener(this);
//		cancelBtn.setVisible(canEdit);
//		saveBtn = new JButton("Save");
//		saveBtn.addActionListener(this);
//		saveBtn.setVisible(canEdit);
//		if (canEdit == true) {
//			saveBtn.setVisible(canEdit);
//		}
//		surestartBtn = new JButton("Start Workflow");
//		surestartBtn.addActionListener(this);
//		surestartBtn.setVisible(canEdit);
//		JScrollPane jsp = new JScrollPane(table,
//				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
//				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		jsp.setPreferredSize(new Dimension(600, 500));
//		ButtonLayout btnlayout = new ButtonLayout(ButtonLayout.VERTICAL,
//				ButtonLayout.TOP, 10);
//
//		JPanel adddelpane = new JPanel();
//		adddelpane.setLayout(btnlayout);
//		adddelpane.add(addBtn);
//		adddelpane.add(delBtn);
//		JPanel surestartPane = new JPanel();
//		surestartPane.add(surestartBtn);
//		JPanel savecancelPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//		savecancelPane.add(saveBtn);
//		savecancelPane.add(cancelBtn);
//
//		GridBagLayout layout = new GridBagLayout();
//		this.setLayout(layout);
//		this.add(adddelpane);
//		this.add(jsp);
//		this.add(surestartPane);
//		this.add(savecancelPane);
//		GridBagConstraints s = new GridBagConstraints();
//		// 是用来控制添加进的组件的显示位置
//		s.fill = GridBagConstraints.BOTH;
//		// 该方法是为了设置如果组件所在的区域比组件本身要大时的显示情况
//		// NONE：不调整组件大小。
//		// HORIZONTAL：加宽组件，使它在水平方向上填满其显示区域，但是不改变高度。
//		// VERTICAL：加高组件，使它在垂直方向上填满其显示区域，但是不改变宽度。
//		// BOTH：使组件完全填满其显示区域。
//		s.gridwidth = 8;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
//		s.weightx = 1;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
//		s.weighty = 1;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
//		layout.setConstraints(jsp, s);// 设置组件
//		s.gridwidth = 0;
//		s.weightx = 0;
//		s.weighty = 0;
//		layout.setConstraints(adddelpane, s);
//		s.gridwidth = 6;
//		s.weightx = 0;
//		s.weighty = 0;
//		layout.setConstraints(surestartPane, s);
//		s.gridwidth = 2;
//		s.weightx = 0;
//		s.weighty = 0;
//		layout.setConstraints(savecancelPane, s);
//		System.out.println("初始化--->" + data.size());
//	}
//
//	/**
//	 * 根据数据内容自动调整列宽。 resize column width automatically
//	 */
//	public void fitTableColumns(JTable myTable) {
//		myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		JTableHeader header = myTable.getTableHeader();
//		int rowCount = myTable.getRowCount();
//		Enumeration<TableColumn> columns = myTable.getColumnModel()
//				.getColumns();
//		while (columns.hasMoreElements()) {
//			TableColumn column = columns.nextElement();
//			int col = header.getColumnModel().getColumnIndex(
//					column.getIdentifier());
//			int width = (int) header
//					.getDefaultRenderer()
//					.getTableCellRendererComponent(myTable,
//							column.getIdentifier(), false, false, -1, col)
//					.getPreferredSize().getWidth();
//			for (int row = 0; row < rowCount; row++) {
//				int preferedWidth = (int) myTable
//						.getCellRenderer(row, col)
//						.getTableCellRendererComponent(myTable,
//								myTable.getValueAt(row, col), false, false,
//								row, col).getPreferredSize().getWidth();
//				width = Math.max(width, preferedWidth);
//			}
//			header.setResizingColumn(column); // 此行很重要
//			column.setWidth(width + myTable.getIntercellSpacing().width);
//		}
//	}
//
//	@Override
//	public void actionPerformed(ActionEvent a) {
//		if (a.getSource() == addBtn) {
//			if (!defaultNorHours.equals("")) {
//				dtm.addRow(new String[] { "", "", "", defaultNorHours, "0", divisionName }); // 表格最下边加
//			} else {
//				dtm.addRow(new String[] { "", "", "", "0", "0", divisionName });
//			}
//		} else if (a.getSource() == delBtn) {
//			if (table.isEditing()) {
//				table.getCellEditor().stopCellEditing();
//			}
//			int[] rows = table.getSelectedRows();
//			if (rows != null) {
//				for (int i = 0; i < rows.length; i++) {
//					dtm.removeRow(rows[i]);
//				}
//			}
//		} else if (a.getSource() == saveBtn) {
//			// saveDatatoProperty
//			if (checkTable() == false) {
//				MessageBox.post("请检查表格所填值是否符合要求！", "WARNING",
//						MessageBox.WARNING);
//			} else {
//				if (dialog != null) {
//					try {
//						form = createForm();
//					} catch (ServiceException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (TCException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				saveData();
//				if (dialog == null) {
//					// 删除原来的object
//					for (int i = 0; i < oldCompVec.size(); i++) {
//						TCComponent comp = oldCompVec.get(i);
//						try {
//							comp.delete();
//							System.out.println("after save DATA  I delete ");
//						} catch (TCException e) {
//							e.printStackTrace();
//						}
//					}
//					oldCompVec.clear();
//				} else {
//					dialog.dispose();
//				}
//
//			}
//		} else if (a.getSource() == cancelBtn) {
//			try {
//				if (dialog != null) {
//					dialog.dispose();
//				} else {
//					// 初始化
//					dtm.setRowCount(0);
//					oldCompVec.removeAllElements();
//					TCComponent[] objects = tcProperty.getReferenceValueArray();
//					if (objects != null) {
//						for (int i = 0; i < objects.length; i++) {
//							oldCompVec.add(objects[i]);
//						}
//					}
//					for (int i = 0, size = oldCompVec.size(); i < size; i++) {
//						TCComponent comp = oldCompVec.get(i);
//						TCProperty[] propertys = comp
//								.getTCProperties(wdhProNames);
//						Vector<String> vec = new Vector<String>();
//						vec.add(propertys[0].getStringValue());
//						dtm.addRow(vec);
//						TCComponentProject prj = (TCComponentProject) propertys[1]
//								.getReferenceValue();
//						TCComponentScheduleTask task = (TCComponentScheduleTask) propertys[2]
//								.getReferenceValue();
//						TCComponentGroup group = (TCComponentGroup) propertys[3]
//								.getReferenceValue();
//						String normalHour = comp.getProperty("jci6_Hour");
//						String extractHour = comp.getProperty("jci6_ExtraHour");
//						String project_string = prj.getProperty("object_string");
//						table.setValueAt(project_string, i, 1);
//						String name = "";
//						name = task.getProperty("object_name");
//						table.setValueAt(name, i, 2);
//						table.setValueAt(normalHour, i, 3);
//						table.setValueAt(extractHour, i, 4);
//						table.setValueAt(group.getGroupName(), i, 5);
//					}
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else if (a.getSource() == surestartBtn) {
//			// saveDatatoProperty
//			if (checkTable() == false) {
//				MessageBox.post("请检查表格所填值是否符合要求！", "WARNING",
//						MessageBox.WARNING);
//			} else {
//				try {
//					if (dialog != null) {
//
//						form = createForm();
//
//					}
//					saveData();
//					// 删除原来的object
//					for (int i = 0; i < oldCompVec.size(); i++) {
//						TCComponent comp = oldCompVec.get(i);
//
//						comp.delete();
//
//					}
//					oldCompVec.clear();
//					if (isprocess || dialog != null) {
//						// 发起流程
//
//						TCComponentProcess process = JCI6YFJCUtil
//								.createProcess(session, template,
//										template.getName(),
//										new TCComponent[] { form });
//						isprocess = false;
//						int size = signoffVec.size();
//						if (size > 0) {
//							TCComponentTask rootTask = process.getRootTask();
//							for (int i = 0; i < size; i++) {
//								// 多次调用UserService
//								JCI6YFJCUtil.callUserService(
//										session.getUserService(),
//										"userservice_setSignOff", new Object[] {
//												rootTask, signoffVec.get(i) });
//							}
//							if (dialog != null) {
//								dialog.dispose();
//							}
//							MessageBox.post("Create Process Success!",
//									"INFORMATION", MessageBox.INFORMATION);
//						} else {
//							if (dialog != null) {
//								dialog.dispose();
//							}
//							MessageBox.post("Cannot find Division Manager or Section Manager,please go to your worklist to assign review task approval",
//									"WARNING", MessageBox.WARNING);
//						}
//
//					} else {
//						MessageBox.post(
//								"The form has been in template process !",
//								"ERROR", MessageBox.ERROR);
//					}
//				} catch (Exception e) {
//					MessageBox
//							.post(e.toString(), "Exception", MessageBox.ERROR);
//				}
//			}
//
//		}
//	}
//
//	public JTable getTable() {
//		return table;
//	}
//
//	public String getDefaultNormalHour() {
//		Calendar cal = Calendar.getInstance();
//		int year = cal.get(Calendar.YEAR);
//		int month = cal.get(Calendar.MONTH) + 1;// 月份是从0开始的需要加1
//		for (int i = 0; i < lovValues.length; i++) {
//			String[] array = lovValues[i].split("\\.");
//			if (array[0].equals(String.valueOf(year))) {
//				if (Integer.parseInt(array[1]) == month) {
//					return lovValueDiscrip[i];
//				}
//			}
//		}
//		return "";
//	}
//
//
//	public void setProjectVector(String userName) {
//		projectV.clear();
//		TCComponent[] comp = query(session, "YFJC_SearchUser",
//				new String[] { "userName" }, new String[] { userName });
//		if (comp != null && comp.length > 0) {
//			TCComponentUser user = (TCComponentUser) comp[0];
//			try {
//				if (user != null) {
//					String[] projects = user.getProjectNames();
//					System.out.println(" user == " + userName
//							+ "    prj length== " + projects.length);
//					for (int i = 0; i < projects.length; i++) {
//						projectV.add(projects[i]);
//						System.out.println(" project name === " + projects[i]);
//						TCComponent[] theComp = query(session, "Projects...",
//								new String[] { "ProjectName" },
//								new String[] { projects[i] });
//						if (theComp != null && theComp.length > 0) {
//							TCComponentProject project = (TCComponentProject) theComp[0];
//							projectMap.put(projects[i], project);
//						}
//					}
//				} else {
//					System.out.println(" user " + userName + " is null");
//				}
//			} catch (TCException e) {
//				e.printStackTrace();
//			}
//		} else {
//			System.out.println(" user " + userName + " is null");
//		}
//	}
//
//	public void setProjectVector(TCComponentUser user) throws TCException {
//		projectV.clear();
//		if (user != null) {
//			String[] key = new String[] { "Id" };
//			String[] value = new String[] { loginUser.getUserId() };
//			TCComponent[] theComp = query(session, "UserBasedProjects", key,
//					value);
//			if (theComp != null && theComp.length > 0) {
//				for (int i = 0; i < theComp.length; i++) {
//					TCComponentProject project = (TCComponentProject) theComp[i];
//					String projectName = project.getProperty("object_string");
//					projectV.add(projectName);
//					projectMap.put(projectName, project);
//				}
//			} else {
//				System.out.println("查询到空");
//			}
//
//		}
//
//	}
//
//	// 得到项目的任务
//	public void setTaskVector(String prjName) {
//		TCComponentSchedule schedule = null;
//		TCComponentScheduleTask scheduleTask = null;
//		taskV.clear();
//		TCComponentProject project = projectMap
//				.get(prjName);
//		try {
//			if (project != null) {
//				TCComponent[] comps = project
//						.getRelatedComponents("TC_Program_Preferred_Items");
//				for (int i = 0; i < comps.length; i++) {
//					System.out.println(comps[i].getType());
//					// 测试系统项目下挂的是时间表任务类型
//					if (comps[i] instanceof TCComponentSchedule) {
//						schedule = (TCComponentSchedule) comps[i];
//						break;
//					}
//				}
//				if (schedule != null) {
//					scheduleTask = (TCComponentScheduleTask) schedule
//							.getRelatedComponent("sch_summary_task");
//					AIFComponentContext[] context = scheduleTask.getChildren();
//					for (int j = 0; j < context.length; j++) {
//						TCComponent comp1 = (TCComponent) context[j]
//								.getComponent();
//						if (comp1 instanceof TCComponentScheduleTask) {
//							TCComponentScheduleTask task = (TCComponentScheduleTask) comp1;
//							String taskName = task.getProperty("object_name");
//							System.out.println("taskName---->" + taskName);
//							if (taskName.equals(extTaskName)) { // 汇总任务
//								// 获得子任务
//								TCComponent[] childComp = task
//										.getRelatedComponents("child_task_taglist");
//								for (int i = 0; i < childComp.length; i++) {
//									String s = childComp[i]
//											.getProperty("object_name");
//									taskV.add(s);
//									taskMap.put(
//											s,
//											(TCComponentScheduleTask) childComp[i]);
//								}
//							}
//						}
//					}
//				} else {
//					System.out.println("schedule is null");
//				}
//			} else {
//				System.out.println(" project === null");
//			}
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public boolean checkTable() {
//		if (table.isEditing()) {
//			table.getCellEditor().stopCellEditing();
//		}
//		System.out.println("i  am  in  checkTable() ");
//		for (int i = 0; i < table.getRowCount(); i++) {
//			for (int j = 0; j < table.getColumnCount(); j++) {
//				if (table.getValueAt(i, j) == null) {
//					return false;
//				}
//				if (table.getValueAt(i, j).toString().equals("")) {
//					return false;
//				}
//			}
//		}
//		return true;
//	}
//
//	public TCComponent createObject(String userName,
//			TCComponentProject project, TCComponentScheduleTask task,
//			TCComponentGroup division, double normalHour, double extractHour) {
//		TCComponent tccomponent = null;
//		IBOCreateDefinition ibocreatedefinition = BOCreateDefinitionFactory
//				.getInstance()
//				.getCreateDefinition(session, "JCI6_ExtWorkDayHr");
//		com.teamcenter.rac.common.create.CreateInstanceInput cii = new CreateInstanceInput(
//				ibocreatedefinition);
//		cii.add("object_name", "ExtWorkDayHr");
//		List list = new ArrayList();
//		list.add(cii);
//		ArrayList arraylist = new ArrayList(0);
//		arraylist.addAll(list);
//		List list1;
//		try {
//			list1 = SOAGenericCreateHelper.create(session, ibocreatedefinition,
//					arraylist);
//			if (list1 != null && list1.size() > 0)
//				tccomponent = (TCComponent) list1.get(0);
//			TCProperty[] propertys = tccomponent.getTCProperties(wdhProNames);
//			propertys[0].setStringValueData(userName);
//			propertys[1].setReferenceValueData(project);
//			propertys[2].setReferenceValueData(task);
//			propertys[3].setReferenceValueData(division);
//			propertys[4].setDoubleValueData(normalHour);
//			propertys[5].setDoubleValueData(extractHour);
//			propertys[6].setIntValueData(lastYear);
//			propertys[7].setIntValueData(lastMonth);
//			propertys[8].setReferenceValueData(loginUser);
//			tccomponent.setTCProperties(propertys);
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//
//		return tccomponent;
//	}
//
//	public void saveData() {
//		// newCompVec = new Vector();
//		// 创建 JCI6_ExtWorkDayHr对象组
//		TCComponent[] objects = new TCComponent[table.getRowCount()];
//		for (int i = 0; i < table.getRowCount(); i++) {
//			String userName = table.getValueAt(i, 0).toString();
//			TCComponentProject project = (projectMap
//					.get(table.getValueAt(i, 1).toString()));
//			TCComponentScheduleTask task = (taskMap
//					.get(table.getValueAt(i, 2).toString()));
//			double normalHour = Double.parseDouble(table.getValueAt(i, 3)
//					.toString());
//			double extractHour = Double.parseDouble(table.getValueAt(i, 4)
//					.toString());
//			objects[i] = createObject(userName, project, task, divisionGroup,
//					normalHour, extractHour);
//			// newCompVec.add(objects[i]); // 存放 以备这次操作后再次保存
//		}
//		try {
//			if (tcProperty == null)
//				tcProperty = form.getTCProperty("jci6_ExtHourArray");
//			tcProperty.setReferenceValueArray(objects);
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static TCComponent[] query(TCSession session, String query_name,
//			String[] arg1, String[] arg2) {
//		TCComponent[] component = null;
//		try {
//			TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session
//					.getTypeComponent("ImanQuery");
//			TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype
//					.find(query_name);
//			TCTextService imantextservice = session.getTextService();
//			String[] queryAttribute = new String[arg1.length];
//			for (int i = 0; i < arg1.length; ++i) {
//				queryAttribute[i] = imantextservice.getTextValue(arg1[i]);
//			}
//			String[] queryValues = new String[arg2.length];
//			for (int i = 0; i < arg2.length; ++i) {
//				queryValues[i] = arg2[i];
//			}
//			component = imancomponentquery.execute(queryAttribute, queryValues);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return component;
//	}
//
//
//	public void initTableDataVector(TCComponent[] objects) {
//		oldCompVec = new Vector<TCComponent>(); // 记录表单属性原有对象以备本次保存时删除
//		try {
//			if (objects != null) {
//				for (int i = 0; i < objects.length; i++) {
//					oldCompVec.add(objects[i]);
//					Vector<String> temp = new Vector<String>();
//					TCProperty[] propertys = objects[i]
//							.getTCProperties(wdhProNames);
//					temp.add(propertys[0].getStringValue());
//					TCComponentProject prj = (TCComponentProject) propertys[1]
//							.getReferenceValue();
//					TCComponentScheduleTask task = (TCComponentScheduleTask) propertys[2]
//							.getReferenceValue();
//					TCComponentGroup group = (TCComponentGroup) propertys[3]
//							.getReferenceValue();
//					String normalHour = objects[i].getProperty("jci6_Hour");
//					String extractHour = objects[i]
//							.getProperty("jci6_ExtraHour");
//					String project_string = prj.getProperty("object_string");
//					temp.add(project_string);
//					projectMap.put(project_string, prj);
//					String name = "";
//					name = task.getProperty("object_name");
//					taskMap.put(name, task);
//					temp.add(name);
//					temp.add(normalHour);
//					temp.add(extractHour);
//					temp.add(group.getGroupName());
//					data.add(temp);
//				}
//			}
//			// 2014-8-12 modify by wuh 载入项目
//			setProjectVector(loginUser);
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public DefaultTableModel getDataModel() {
//		return dtm;
//	}
//
//	/**
//	 * 创建ExtWorkDayHourForm
//	 * 
//	 * @return
//	 * @throws TCException
//	 * @throws ServiceException
//	 */
//	private TCComponentForm createForm() throws TCException, ServiceException {
//		if (dialog != null) {
//			String m = null;
//			if (lastMonth < 10) {
//				m = "0" + String.valueOf(lastMonth);
//			} else {
//				m = String.valueOf(lastMonth);
//			}
//			String formname = divisionName + "_" + lastYear + "." + m
//					+ "_ExtSupport Actual Hours";
//			form = JCI6YFJCUtil.createForm(session, "JCI6_ExtWDHrForm",
//					formname);
//			setFormRelate(form);
//		}
//		return form;
//	}
//
//	// 得到当前日期的上一月
//	private void getYearAndLastMonth() {
//		Date date = new Date();
//		int year = date.getYear() + 1900;
//		int month = date.getMonth() + 1;
//		if (month == 1) {
//			lastYear = year - 1;
//			lastMonth = 12;
//		} else {
//			lastYear = year;
//			lastMonth = month - 1;
//		}
//
//	}
//
//	/**
//	 * form关联
//	 * 
//	 * @param form
//	 * @throws TCException
//	 */
//	private void setFormRelate(TCComponentForm form) throws TCException {
////		InterfaceAIFComponent aif = AIFUtility.getCurrentApplication()
////				.getTargetComponent();
////		TCComponentFolder folder = null;
////		if (aif instanceof TCComponentFolder) {
////			folder = (TCComponentFolder) aif;
////		} else {
////			folder = loginUser.getNewStuffFolder();
////		}		
//		loginUser.getHomeFolder().add("contents", form);
//	}
//
//	// 获得JCI6_Division lov
//	private void getDivisionValues() throws TCException {
//		System.out.println("getDivisionValues in");
//		ListOfValuesInfo info = TCComponentListOfValuesType.findLOVByName(
//				session, "JCI6_Division").getListOfValues();
//		Object[] object = info.getListOfValues();
//		for (int i = 0; i < object.length; i++) {
//			if (object[i] instanceof TCComponentGroup) {
//				TCComponentGroup group = (TCComponentGroup) object[i];
//				String gname = group.getGroupName();
//				if (gname.equals(divisionName)) {
//					divisionGroup = group;
//					break;
//				}
//			}
//		}
//	}
//
//	private void getDivisionGroup() throws TCException {
//		if (form != null) {
//			String object_name = form.getProperty("object_name");
//			String[] str = object_name.split("_");
//			divisionName = str[0];
//			String[] yearMonth = str[1].split("\\.");
//			lastYear = Integer.valueOf(yearMonth[0]);
//			lastMonth = Integer.valueOf(yearMonth[1]);
//			getDivisionValues();
//		}
//	}
//	
//	
//	/**
//	 * 得到审核人
//	 * @throws TCException
//	 */
//	private void getReviewUser() throws TCException{
//		TCComponentRole[] roles = divisionGroup.getRoles();
//		//获得部门经理
//		for (int j = 0; j < roles.length; j++) {
//			if (roles[j].getProperty("role_name").equals(
//					"Division Manager")) {
//				TCComponentGroupMember[] member = roles[j]
//						.getGroupMembers(divisionGroup);
//				if (member != null && member.length > 0) {
//					for (int m = 0; m < member.length; m++) {
//						signoffVec.add(member[m]);
//					}
//				}
//			}
//		}
//		int size = signoffVec.size();
//		if(size == 0){
//			System.out.println("Division Manager not exist,get section manager。。。");
//			//获得同级的section manager
//			for (int j = 0; j < roles.length; j++) {
//				if (roles[j].getProperty("role_name").equals(
//						"Section Manager")) {
//					TCComponentGroupMember[] member = roles[j]
//							.getGroupMembers(divisionGroup);
//					if (member != null && member.length > 0) {
//						for (int m = 0; m < member.length; m++) {
//							System.out.println("current group section manager");
//							signoffVec.add(member[m]);
//						}
//					}
//				}
//			}
//			size = signoffVec.size();
//			//获得子组的section manager
//			if(size == 0){
//				System.out.println("current group section manager not exist,get child group section manager");
//				TCComponentGroup[] childGroups = divisionGroup.getGroups();
//				if(childGroups != null && childGroups.length > 0){
//					for(int j=0;j<childGroups.length;j++){
//						TCComponentRole[] childRoles = childGroups[j].getRoles();
//						for (int h = 0; h < childRoles.length; h++) {
//							if (childRoles[h].getProperty("role_name").equals(
//									"Section Manager")) {
//								TCComponentGroupMember[] member = childRoles[h]
//										.getGroupMembers(childGroups[j]);
//								if (member != null && member.length > 0) {
//									for (int m = 0; m < member.length; m++) {
//										System.out.println("child group section manager");
//										signoffVec.add(member[m]);
//									}
//								}
//							}
//						}
//					}
//				}else{
//					System.out.println("child group null");
//				}
//			}
//			
//		}
//	}
//
//	private void getNewExt() {
//		for (Map.Entry<String, String> entry : ext_division.entrySet()) {
//			if (entry.getValue().equals(divisionName)) {
//				extNames.add(entry.getKey());
//			}
//		}
//	}
//
//	public Vector<TCComponent> getOldCompVec() {
//		return oldCompVec;
//	}
//
//}
