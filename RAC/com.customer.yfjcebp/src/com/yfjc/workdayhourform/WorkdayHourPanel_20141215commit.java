package com.yfjc.workdayhourform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.yfjcebp.extsupport.JCI6YFJCUtil;

public class WorkdayHourPanel_20141215commit extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public WDHTable table;
	public JButton saveBtn;
	public JButton cancelBtn;
	public JButton addBtn;
	public JButton delBtn;
	public JButton surestartBtn;
	public DefaultTableModel dtm;
	private boolean canEdit;
	private Vector<String> extNames;// 外包人员姓名
	private Vector<String> compareUserV;//
	private Vector<String> projectV;// 当前登录用户所参与的项目名
	private Vector<String> taskV;// 时间表任务名
	private Vector<TCComponent> oldCompVec;// 操作前的对象
	// private Vector<TCComponent> newCompVec;// 操作后需要保存的对象
	private Vector<Vector<String>> data;// 初始化的data
	private TCSession session;
	private TCComponentForm form;
	private TCProperty tcProperty;
	private String[] lovValues = null;
	private ListOfValuesInfo loi;
	private String[] lovValueDiscrip = null;
	private Map<TCComponentProject,Map<String, TCComponentScheduleTask>> proTaskMap;
	//private Map<String, TCComponentScheduleTask> taskMap;// 时间表任务名，时间表任务
	private Map<String, TCComponentProject> projectMap;// 项目名，项目
	private TCComponentTaskTemplate template = null;// 流程模板
	private TCComponentUser loginUser;// 当前登录用户
	private WorkdayHourDialog dialog;
	private Vector<String> hV;
	private String[] wdhProNames = new String[] { "jci6_UserName",
			"jci6_Project", "jci6_Task", "jci6_Division", "jci6_Hour",
			"jci6_ExtraHour", "jci6_Year", "jci6_Month", "jci6_ownProxy" };
	private int lastYear;
	private int lastMonth;
	private boolean isprocess;
	private String defaultNorHours;
	private double defaultHour;
	// add 8-26
	private TCComponentGroup divisionGroup;
	private String divisionName;
	private Registry reg = Registry.getRegistry(this);
	private String extTaskName = reg.getString("ExtHourTaskName");
	private Vector<TCComponentGroupMember> signoffVec = new Vector<TCComponentGroupMember>();
	private TCComponentQueryType imancomponentquerytype;
	private TCTextService imantextservice;

	public WorkdayHourPanel_20141215commit(WorkdayHourDialog dialog, TCSession session,
			TCComponentTaskTemplate template, String divisionName,
			TCComponentGroup divisionGroup) {
		this.dialog = dialog;
		canEdit = true;
		this.session = session;
		this.template = template;
		this.divisionName = divisionName;
		this.divisionGroup = divisionGroup;
		proTaskMap = new HashMap<TCComponentProject, Map<String,TCComponentScheduleTask>>();
		//taskMap = new HashMap<String, TCComponentScheduleTask>();
		projectMap = new HashMap<String, TCComponentProject>();
		data = new Vector<Vector<String>>();
		projectV = new Vector<String>();
		taskV = new Vector<String>();
		compareUserV = new Vector<String>();
		try {
			loginUser = session.getUser();
			loi = TCComponentListOfValuesType.findLOVByName(session,
					"JCI6_NormalHours").getListOfValues();
			lovValueDiscrip = loi.getDescriptions();
			lovValues = loi.getStringListOfValues();
//			defaultNorHours = getDefaultNormalHour();
//			defaultHour = Double.valueOf(defaultNorHours);
			extNames = new Vector<String>();
			getDivisionGroup();
			getYearAndLastMonth();
			//modify 2014-12-4
			defaultNorHours = getDefaultNormalHour();
			defaultHour = Double.valueOf(defaultNorHours);
			
			getExtName(session, divisionGroup.getFullName(),
					String.valueOf(lastYear), String.valueOf(lastMonth),
					loginUser.getUserId());
			compareUserV.addAll(extNames);
			getReviewUser();
		} catch (TCException e) {
			e.printStackTrace();
		}
		initTableDataVector(null);
		initUI();
	}

	/**
	 * 
	 * @param flag
	 *            是否能编辑
	 * @param flag1
	 * @param v
	 * @param f
	 * @param p
	 * @param t
	 */
	public WorkdayHourPanel_20141215commit(boolean isprocess, boolean isEdit,
			TCComponentForm f, TCProperty p, TCComponentTaskTemplate t) {
		System.out.println("WorkdayHourPanel form");
		this.isprocess = isprocess;
		canEdit = isEdit;
		form = f;
		tcProperty = p;
		template = t;
		proTaskMap = new HashMap<TCComponentProject, Map<String,TCComponentScheduleTask>>();
		//taskMap = new HashMap<String, TCComponentScheduleTask>();
		projectMap = new HashMap<String, TCComponentProject>();
		data = new Vector<Vector<String>>();
		projectV = new Vector<String>();
		taskV = new Vector<String>();
		compareUserV = new Vector<String>();
		try {
			session = form.getSession();
			//modify by wuh 2014-12-16
			loginUser = (TCComponentUser) form.getRelatedComponent("owning_user");
			//loginUser = session.getUser();
			loi = TCComponentListOfValuesType.findLOVByName(session,
					"JCI6_NormalHours").getListOfValues();
			lovValueDiscrip = loi.getDescriptions();
			lovValues = loi.getStringListOfValues();
//			defaultNorHours = getDefaultNormalHour();
//			defaultHour = Integer.valueOf(defaultNorHours);
			extNames = new Vector<String>();
			getDivisionGroup();
			//modify 2014-12-4
			defaultNorHours = getDefaultNormalHour();
			defaultHour = Double.valueOf(defaultNorHours);
			
			getExtName(session, divisionGroup.getFullName(),
					String.valueOf(lastYear), String.valueOf(lastMonth),
					loginUser.getUserId());
			compareUserV.addAll(extNames);
			getReviewUser();
		} catch (TCException e) {
			e.printStackTrace();
		}

		initTableDataVector(tcProperty.getReferenceValueArray());
		initUI();
	}

	public void initUI() {

		String[] header = new String[] { "User", "Program", "Task",
				"Normal Hours", "Overtime Hours", "Division" };
		hV = new Vector<String>();
		for (int i = 0; i < header.length; i++) {
			hV.add(header[i]);
		}
		dtm = new DefaultTableModel(data, hV);
		table = new WDHTable(dtm, extNames) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 5) {
					return false;
				}
				return canEdit;
			}
		};
		table.getTableHeader().setReorderingAllowed(false);
		dtm.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int col = e.getColumn();
				int row = e.getLastRow();
				if (col == 1) {
					// 载入任务
					if (dtm.getValueAt(row, col) == null) {
						setTaskVector("");
					} else {
						setTaskVector(dtm.getValueAt(row, col).toString());
					}
					int size = taskV.size();
					if (size == 0) {
						table.setValueAt(null, row, col + 1);
					} else {
						String s = (String) table.getValueAt(row, col + 1);
						boolean hasVal = false;
						if (s != null && !s.trim().equals("")) {
							for (int i = 0; i < size; i++) {
								if (taskV.get(i).equals(s)) {
									hasVal = true;
									break;
								}
							}
						}
						if (!hasVal) {
							table.setValueAt(null, row, col + 1);
						} else {
							System.out.println("有值");
						}
					}
					TableColumn tableColumn = table.getColumnModel().getColumn(
							2);
					ComboxCellEditor editor = new ComboxCellEditor(taskV, 2);
					tableColumn.setCellEditor(editor);
				} else if (col == 0) {
					// 人名
					String currentUserName = (String) table
							.getValueAt(row, col);
					double hourCnt = defaultHour;
					for (int i = 0, size = table.getRowCount(); i < size; i++) {
						String name = (String) table.getValueAt(i, col);
						if (currentUserName.equals(name) && i != row) {
							hourCnt = hourCnt
									- Double.valueOf((String) table.getValueAt(
											i, 3));
						}
					}
					table.setValueAt(String.valueOf(hourCnt), row, 3);
				} else if (col == 3) {
					// Normal Hours
					String currentUserName = (String) table.getValueAt(row, 0);
					double hourCnt = defaultHour;
					for (int i = 0, size = table.getRowCount(); i < size; i++) {
						String name = (String) table.getValueAt(i, 0);
						if (currentUserName.equals(name) && i != row) {
							hourCnt = hourCnt
									- Double.valueOf((String) table.getValueAt(
											i, 3));
						}
					}
					Double current = Double.valueOf((String) table.getValueAt(
							row, 3));
					if (current > hourCnt) {
						MessageBox.post("以下人员的法定工时已超额填写,系统将自动减去多余法定工时:\n"+currentUserName, "INFORMATION",
								MessageBox.INFORMATION);
						table.setValueAt(String.valueOf(hourCnt), row, col);
					}
				}
			}
		});
		// fitTableColumns(table);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setFixColumnWidth(table);
		table.setRowHeight(30);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (table.getSelectedRow() >= 0) {
					delBtn.setEnabled(true);
				} else {
					delBtn.setEnabled(false);
				}
				int column = table.getSelectedColumn();
				int row = table.getSelectedRow();
				if (column > 0) {
					Object ob = table.getValueAt(row, column - 1);
					if (ob != null) {
						dtm.setValueAt(ob.toString(), row, column - 1);
						if (column == 2) {
							setTaskVector(ob.toString());
							TableColumn tableColumn = table.getColumnModel()
									.getColumn(column);
							// 8-18
							ComboxCellEditor editor = new ComboxCellEditor(
									taskV, column);
							tableColumn.setCellEditor(editor);
						}

					}
				}

			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});

		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.LEFT);
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn tableColumn = table.getColumnModel().getColumn(i);
			tableColumn.setCellRenderer(render);
			if (i == 1) {
				ComboxCellEditor editor = new ComboxCellEditor(projectV, i);
				tableColumn.setCellEditor(editor);
			} else if (i == 2) {
				ComboxCellEditor editor = new ComboxCellEditor(taskV, i);
				tableColumn.setCellEditor(editor);
			}
		}
		addBtn = new JButton("+");
		addBtn.addActionListener(this);
		addBtn.setVisible(canEdit);
		delBtn = new JButton(" - ");
		delBtn.addActionListener(this);
		delBtn.setVisible(canEdit);
		delBtn.setEnabled(false);

		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(this);
		cancelBtn.setVisible(canEdit);
		saveBtn = new JButton("Save");
		saveBtn.addActionListener(this);
		saveBtn.setVisible(canEdit);
		if (canEdit == true) {
			saveBtn.setVisible(canEdit);
		}
		surestartBtn = new JButton("Save draft");
		surestartBtn.addActionListener(this);
		surestartBtn.setVisible(canEdit);
		JScrollPane jsp = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setPreferredSize(new Dimension(990, 480));
		ButtonLayout btnlayout = new ButtonLayout(ButtonLayout.VERTICAL,
				ButtonLayout.TOP, 10);

		JPanel adddelpane = new JPanel();
		adddelpane.setLayout(btnlayout);
		adddelpane.add(addBtn);
		adddelpane.add(delBtn);

		JPanel savecancelPane = new JPanel();
		savecancelPane.add(surestartBtn);
		// savecancelPane.add(saveBtn);
		savecancelPane.add(cancelBtn);

		this.setLayout(new BorderLayout());
		this.add(adddelpane, BorderLayout.WEST);
		this.add(jsp);
		this.add(savecancelPane, BorderLayout.SOUTH);

	}

	/**
	 * 将列设置为固定宽度。//fix table column width
	 */
	public void setFixColumnWidth(JTable table) {
		TableColumnModel tcm = table.getTableHeader().getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			TableColumn tc = tcm.getColumn(i);
			if (i == 0) {
				tc.setMaxWidth(180);
				tc.setMinWidth(180);
				tc.setPreferredWidth(180);
			} else if (i == 1) {
				tc.setMaxWidth(300);
				tc.setMinWidth(300);
				tc.setPreferredWidth(300);
			} else if (i == 2) {
				tc.setMaxWidth(150);
				tc.setMinWidth(150);
				tc.setPreferredWidth(150);
			} else if (i == 3 || i == 4) {
				tc.setMaxWidth(120);
				tc.setMinWidth(120);
				tc.setPreferredWidth(120);
			} else if (i == 5) {
				tc.setMaxWidth(160);
				tc.setMinWidth(160);
				tc.setPreferredWidth(160);
			}
		}
	}

	/**
	 * 根据数据内容自动调整列宽。 resize column width automatically
	 */
	public void fitTableColumns(JTable myTable) {
		myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JTableHeader header = myTable.getTableHeader();
		int rowCount = myTable.getRowCount();
		Enumeration<TableColumn> columns = myTable.getColumnModel()
				.getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = columns.nextElement();
			int col = header.getColumnModel().getColumnIndex(
					column.getIdentifier());
			int width = (int) header
					.getDefaultRenderer()
					.getTableCellRendererComponent(myTable,
							column.getIdentifier(), false, false, -1, col)
					.getPreferredSize().getWidth();
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) myTable
						.getCellRenderer(row, col)
						.getTableCellRendererComponent(myTable,
								myTable.getValueAt(row, col), false, false,
								row, col).getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth);
			}
			header.setResizingColumn(column); // 此行很重要
			column.setWidth(width + myTable.getIntercellSpacing().width);
		}
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		if (a.getSource() == addBtn) {
			if (!defaultNorHours.equals("")) {
				dtm.addRow(new String[] { "", "", "", defaultNorHours, "0",
						divisionName }); // 表格最下边加
			} else {
				dtm.addRow(new String[] { "", "", "", "0", "0", divisionName });
			}
		} else if (a.getSource() == delBtn) {
			if (table.isEditing()) {
				table.getCellEditor().stopCellEditing();
			}
			int[] rows = table.getSelectedRows();
			if (rows != null) {
				for (int i = 0; i < rows.length; i++) {
					dtm.removeRow(rows[i]);
				}
			}
		} else if (a.getSource() == saveBtn) {
			// saveDatatoProperty
			if (checkTable() == false) {
				MessageBox.post("请检查是否有单元格为空！", "WARNING", MessageBox.WARNING);
			} else {
				if (dialog != null) {
					try {
						form = createForm();
					} catch (ServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TCException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 2014-9-17 判断填报的人员是否出现在其他表单中
				String s = checkOtherForm();
				if (s.equals("")) {
					saveData();
					if (dialog == null) {
						// 删除原来的object
						for (int i = 0; i < oldCompVec.size(); i++) {
							TCComponent comp = oldCompVec.get(i);
							try {
								comp.delete();
								System.out
										.println("after save DATA  I delete ");
							} catch (TCException e) {
								e.printStackTrace();
							}
						}
						oldCompVec.clear();
					} else {
						dialog.dispose();
					}
				} else {
					MessageBox.post(s, "INFORMATION", MessageBox.INFORMATION);
				}
			}
		} else if (a.getSource() == cancelBtn) {
			try {
				if (dialog != null) {
					dialog.dispose();
				} else {
					// 初始化
					dtm.setRowCount(0);
					oldCompVec.removeAllElements();
					TCComponent[] objects = tcProperty.getReferenceValueArray();
					if (objects != null) {
						for (int i = 0; i < objects.length; i++) {
							oldCompVec.add(objects[i]);
						}
					}
					for (int i = 0, size = oldCompVec.size(); i < size; i++) {
						TCComponent comp = oldCompVec.get(i);
						TCProperty[] propertys = comp
								.getTCProperties(wdhProNames);
						Vector<String> vec = new Vector<String>();
						vec.add(propertys[0].getStringValue());
						dtm.addRow(vec);
						TCComponentProject prj = (TCComponentProject) propertys[1]
								.getReferenceValue();
						TCComponentScheduleTask task = (TCComponentScheduleTask) propertys[2]
								.getReferenceValue();
						TCComponentGroup group = (TCComponentGroup) propertys[3]
								.getReferenceValue();
						String normalHour = comp.getProperty("jci6_Hour");
						String extractHour = comp.getProperty("jci6_ExtraHour");
						String project_string = prj
								.getProperty("object_string");
						table.setValueAt(project_string, i, 1);
						String name = "";
						name = task.getProperty("object_name");
						table.setValueAt(name, i, 2);
						table.setValueAt(normalHour, i, 3);
						table.setValueAt(extractHour, i, 4);
						table.setValueAt(group.getFullName().split("\\.")[0],
								i, 5);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (a.getSource() == surestartBtn) {
			// saveDatatoProperty
			if (checkTable() == false) {
				MessageBox.post("请检查是否有单元格为空！", "WARNING", MessageBox.WARNING);
			} else {
				try {
					if (dialog != null) {
						form = createForm();
					}
					String s = checkOtherForm();
					if (s.equals("")) {
						saveData();
						// 删除原来的object
						for (int i = 0; i < oldCompVec.size(); i++) {
							TCComponent comp = oldCompVec.get(i);
							comp.delete();
						}
						oldCompVec.clear();
						if (isprocess || dialog != null) {
							// 发起流程
							System.out.println("template-->" + template);
							TCComponentProcess process = JCI6YFJCUtil
									.createProcess(session, template,
											template.getName(),
											new TCComponent[] { form });
							isprocess = false;
							System.out.println("signoffVec--->" + signoffVec);
							int size = signoffVec.size();
							if (size > 0) {
								TCComponentTask rootTask = process
										.getRootTask();
								for (int i = 0; i < size; i++) {
									// 多次调用UserService
									JCI6YFJCUtil.callUserService(
											session.getUserService(),
											"userservice_setSignOff",
											new Object[] { rootTask,
													signoffVec.get(i) });
								}
								if (dialog != null) {
									dialog.dispose();
								}
								MessageBox.post("Create Process Successfully!",
										"INFORMATION", MessageBox.INFORMATION);
							} else {
								if (dialog != null) {
									dialog.dispose();
								}
								MessageBox
										.post("Cannot find Division Manager or Section Manager,please go to your worklist to assign review task approval",
												"WARNING", MessageBox.WARNING);
							}

						} else {
							if (dialog != null) {
								dialog.dispose();
							}
							MessageBox.post("save  successfully !",
									"INFORMATION", MessageBox.INFORMATION);
						}
					} else {
						MessageBox.post(s, "INFORMATION",
								MessageBox.INFORMATION);
					}
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox
							.post(e.toString(), "Exception", MessageBox.ERROR);
				}
			}

		}
	}

	public JTable getTable() {
		return table;
	}
	
	

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
	
	//modify 2014-12-4
	public String getDefaultNormalHour() {
		for (int i = 0; i < lovValues.length; i++) {
			String[] array = lovValues[i].split("\\.");
			if (array[0].equals(String.valueOf(lastYear))) {
				if (Integer.parseInt(array[1]) == lastMonth) {
					return lovValueDiscrip[i];
				}
			}
		}
		return "";
	}

	public void setProjectVector(String userName) {
		projectV.clear();
		TCComponent[] comp = query(session, "YFJC_SearchUser",
				new String[] { "userName" }, new String[] { userName });
		if (comp != null && comp.length > 0) {
			TCComponentUser user = (TCComponentUser) comp[0];
			try {
				if (user != null) {
					String[] projects = user.getProjectNames();
					System.out.println(" user == " + userName
							+ "    prj length== " + projects.length);
					for (int i = 0; i < projects.length; i++) {
						projectV.add(projects[i]);
						System.out.println(" project name === " + projects[i]);
						TCComponent[] theComp = query(session, "Projects...",
								new String[] { "ProjectName" },
								new String[] { projects[i] });
						if (theComp != null && theComp.length > 0) {
							TCComponentProject project = (TCComponentProject) theComp[0];
							projectMap.put(projects[i], project);
						}
					}
				} else {
					System.out.println(" user " + userName + " is null");
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(" user " + userName + " is null");
		}
	}

	public void setProjectVector(TCComponentUser user) throws TCException {
		projectV.clear();
		if (user != null) {
			String[] key = new String[] { "Id" };
			String[] value = new String[] { loginUser.getUserId() };
			TCComponent[] theComp = query(session, "UserBasedProjects", key,
					value);
			if (theComp != null && theComp.length > 0) {
				for (int i = 0; i < theComp.length; i++) {
					TCComponentProject project = (TCComponentProject) theComp[i];
					String projectName = project.getProperty("object_string");
					projectV.add(projectName);
					projectMap.put(projectName, project);
				}
			} else {
				System.out.println("查询到空");
			}

		}

	}

	// 得到项目的任务  2014-12-15
	public void getProTask(TCComponentProject project) {
		TCComponentSchedule schedule = null;
		TCComponentScheduleTask scheduleTask = null;
		try {
			if (project != null) {
				TCComponent[] comps = project
						.getRelatedComponents("TC_Program_Preferred_Items");
				for (int i = 0; i < comps.length; i++) {
					System.out.println(comps[i].getType());
					// 测试系统项目下挂的是时间表任务类型
					if (comps[i] instanceof TCComponentSchedule) {
						schedule = (TCComponentSchedule) comps[i];
						break;
					}
				}
				if (schedule != null) {
					scheduleTask = (TCComponentScheduleTask) schedule
							.getRelatedComponent("sch_summary_task");
					AIFComponentContext[] context = scheduleTask.getChildren();
					Map<String,TCComponentScheduleTask> taskMap = null;
					for (int j = 0; j < context.length; j++) {
						TCComponent comp1 = (TCComponent) context[j]
								.getComponent();
						if (comp1 instanceof TCComponentScheduleTask) {
							TCComponentScheduleTask task = (TCComponentScheduleTask) comp1;
							String taskName = task.getProperty("object_name");
							System.out.println("taskName---->" + taskName);
							if (taskName.equals(extTaskName)) { // 汇总任务
								// 获得子任务
								TCComponent[] childComp = task
										.getRelatedComponents("child_task_taglist");
								for (int i = 0; i < childComp.length; i++) {
									String s = childComp[i]
											.getProperty("object_name");
									if(proTaskMap.containsKey(project)){	
										taskMap = proTaskMap.get(project);
										taskMap.put(
												s,
												(TCComponentScheduleTask) childComp[i]);
									}else{
										taskMap = new HashMap<String, TCComponentScheduleTask>();
										taskMap.put(s, (TCComponentScheduleTask) childComp[i]);
										proTaskMap.put(project, taskMap);
									}								
								}
							}
						}
					}
				} else {
					System.out.println("schedule is null");
				}
			} else {
				System.out.println(" project === null");
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	public boolean checkTable() {
		if (table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}
		System.out.println("i  am  in  checkTable() ");
		for (int i = 0; i < table.getRowCount(); i++) {
			for (int j = 0; j < table.getColumnCount(); j++) {
				if (table.getValueAt(i, j) == null) {
					return false;
				}
				if (table.getValueAt(i, j).toString().equals("")) {
					return false;
				}
			}
		}
		return true;
	}

	public TCComponent createObject(String userName,
			TCComponentProject project, TCComponentScheduleTask task,
			TCComponentGroup division, double normalHour, double extractHour) {
		TCComponent tccomponent = null;
		IBOCreateDefinition ibocreatedefinition = BOCreateDefinitionFactory
				.getInstance()
				.getCreateDefinition(session, "JCI6_ExtWorkDayHr");
		com.teamcenter.rac.common.create.CreateInstanceInput cii = new CreateInstanceInput(
				ibocreatedefinition);
		cii.add("object_name", "ExtWorkDayHr");
		List list = new ArrayList();
		list.add(cii);
		ArrayList arraylist = new ArrayList(0);
		arraylist.addAll(list);
		List list1;
		try {
			list1 = SOAGenericCreateHelper.create(session, ibocreatedefinition,
					arraylist);
			if (list1 != null && list1.size() > 0)
				tccomponent = (TCComponent) list1.get(0);
			TCProperty[] propertys = tccomponent.getTCProperties(wdhProNames);
			propertys[0].setStringValueData(userName);
			propertys[1].setReferenceValueData(project);
			propertys[2].setReferenceValueData(task);
			propertys[3].setReferenceValueData(division);
			propertys[4].setDoubleValueData(normalHour);
			propertys[5].setDoubleValueData(extractHour);
			propertys[6].setIntValueData(lastYear);
			propertys[7].setIntValueData(lastMonth);
			propertys[8].setReferenceValueData(loginUser);
			tccomponent.setTCProperties(propertys);
		} catch (TCException e) {
			e.printStackTrace();
		}

		return tccomponent;
	}

	// 2014-9-17 判断填报的人员是否出现在其他表单中
	public String checkOtherForm() {
		StringBuffer sb = new StringBuffer();
		try {
			if (imancomponentquerytype == null)

				imancomponentquerytype = (TCComponentQueryType) session
						.getTypeComponent("ImanQuery");

			TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype
					.find("YFJC_SearchExtDayHour");
			if (imantextservice == null)
				imantextservice = session.getTextService();
			String[] query_keys = new String[] { "Division", "Year", "Month",
					"UserName", "ownProxy" };

			if (imancomponentquery != null) {
				String[] queryAttribute = new String[query_keys.length];
				for (int i = 0; i < query_keys.length; ++i) {
					queryAttribute[i] = imantextservice
							.getTextValue(query_keys[i]);
					if (queryAttribute[i] == null
							|| queryAttribute[i].trim().equals("")) {
						queryAttribute[i] = query_keys[i];
					}
				}
				int rowCnt = table.getRowCount();
				String[] query_vals = new String[query_keys.length];
				query_vals[0] = divisionGroup.getFullName();
				query_vals[1] = String.valueOf(lastYear);
				query_vals[2] = String.valueOf(lastMonth);
				query_vals[4] = loginUser.getUserId();
				Map<String, Double> extHrMap = new HashMap<String, Double>();
				//得到当前form关联
				TCComponent[] currentComps = form.getRelatedComponents("jci6_ExtHourArray");
				for (int i = 0; i < rowCnt; i++) {
					String username = (String) table.getValueAt(i, 0);
					double d = Double.valueOf((String) table.getValueAt(i, 3));
					if (extHrMap.containsKey(username)) {
						extHrMap.put(username, extHrMap.get(username) + d);
					} else {
						extHrMap.put(username, d);
					}
				}

				for (Map.Entry<String, Double> entry : extHrMap.entrySet()) {
					query_vals[3] = entry.getKey();
					double oldVal = 0;
					TCComponent[] comps = imancomponentquery.execute(
							queryAttribute, query_vals);
					if (comps != null && comps.length > 0) {
						for (int j = 0; j < comps.length; j++) {
							if (!inArray(comps[j],currentComps)) {
								oldVal = oldVal
										+ comps[j]
												.getDoubleProperty("jci6_Hour");
							}else{
								System.out.println("包含了");
							}
						}
						Double d = entry.getValue();
						System.out.println("oldVal--->"+oldVal);
						System.out.println("d--->"+d);
						System.out.println("defaultHour--->"+defaultHour);
						if (oldVal + d > defaultHour) {
							if (sb.toString().equals("")) {
								sb.append("以下人员在另一张表单中填写的法定工时如下,请检查后重新输入法定工时:\n");
								sb.append(query_vals[3]).append("  ").append(oldVal).append("hrs\n");
							} else {
								sb.append(query_vals[3]).append("  ").append(oldVal).append("hrs\n");
							}
						}
					}

				}

			} else {
				System.out.println("YFJC_SearchExtDayHour查询不存在");
			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private boolean inArray(TCComponent comp,TCComponent[] comps){
		if(comps != null && comps.length > 0){
			for(int i=0;i<comps.length;i++){
				if(comps[i].equals(comp)){
					return true;
				}
			}
		}
		return false;
	}
	
	

	public void saveData() {
		// newCompVec = new Vector();
		// 创建 JCI6_ExtWorkDayHr对象组
		TCComponent[] objects = new TCComponent[table.getRowCount()];
		for (int i = 0; i < table.getRowCount(); i++) {
			String userName = table.getValueAt(i, 0).toString();
			TCComponentProject project = (projectMap.get(table.getValueAt(i, 1)
					.toString()));
			Map<String, TCComponentScheduleTask> taskMap = proTaskMap.get(project);
			TCComponentScheduleTask task = (taskMap.get(table.getValueAt(i, 2)
					.toString()));
			double normalHour = Double.parseDouble(table.getValueAt(i, 3)
					.toString());
			double extractHour = Double.parseDouble(table.getValueAt(i, 4)
					.toString());
			objects[i] = createObject(userName, project, task, divisionGroup,
					normalHour, extractHour);
			// newCompVec.add(objects[i]); // 存放 以备这次操作后再次保存
		}
		try {
			if (tcProperty == null)
				tcProperty = form.getTCProperty("jci6_ExtHourArray");
			tcProperty.setReferenceValueArray(objects);
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	public TCComponent[] query(TCSession session, String query_name,
			String[] arg1, String[] arg2) {
		TCComponent[] component = null;
		try {
			if (imancomponentquerytype == null)
				imancomponentquerytype = (TCComponentQueryType) session
						.getTypeComponent("ImanQuery");
			TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype
					.find(query_name);
			if (imantextservice == null)
				imantextservice = session.getTextService();
			if (imancomponentquery != null) {
				String[] queryAttribute = new String[arg1.length];
				for (int i = 0; i < arg1.length; ++i) {
					queryAttribute[i] = imantextservice.getTextValue(arg1[i]);
					if (queryAttribute[i] == null
							|| queryAttribute[i].trim().equals("")) {
						queryAttribute[i] = arg1[i];
					}
				}
				component = imancomponentquery.execute(queryAttribute, arg2);
			} else {
				System.out.println(query_name + "查询不存在");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return component;
	}
	
	
	// 得到项目的任务
		public void setTaskVector(String prjName) {
			TCComponentSchedule schedule = null;
			TCComponentScheduleTask scheduleTask = null;
			taskV.clear();
			TCComponentProject project = projectMap.get(prjName);
			try {
				if (project != null) {
					TCComponent[] comps = project
							.getRelatedComponents("TC_Program_Preferred_Items");
					for (int i = 0; i < comps.length; i++) {
						System.out.println(comps[i].getType());
						// 测试系统项目下挂的是时间表任务类型
						if (comps[i] instanceof TCComponentSchedule) {
							schedule = (TCComponentSchedule) comps[i];
							break;
						}
					}
					if (schedule != null) {
						scheduleTask = (TCComponentScheduleTask) schedule
								.getRelatedComponent("sch_summary_task");
						AIFComponentContext[] context = scheduleTask.getChildren();
						Map<String,TCComponentScheduleTask> taskMap = null;
						for (int j = 0; j < context.length; j++) {
							TCComponent comp1 = (TCComponent) context[j]
									.getComponent();
							if (comp1 instanceof TCComponentScheduleTask) {
								TCComponentScheduleTask task = (TCComponentScheduleTask) comp1;
								String taskName = task.getProperty("object_name");
								System.out.println("taskName---->" + taskName);
								if (taskName.equals(extTaskName)) { // 汇总任务
									// 获得子任务
									TCComponent[] childComp = task
											.getRelatedComponents("child_task_taglist");
									for (int i = 0; i < childComp.length; i++) {
										String s = childComp[i]
												.getProperty("object_name");
										taskV.add(s);
										if(proTaskMap.containsKey(project)){	
											taskMap = proTaskMap.get(project);
											taskMap.put(
													s,
													(TCComponentScheduleTask) childComp[i]);
										}else{
											taskMap = new HashMap<String, TCComponentScheduleTask>();
											taskMap.put(s, (TCComponentScheduleTask) childComp[i]);
											proTaskMap.put(project, taskMap);
										}								
									}
								}
							}
						}
					} else {
						System.out.println("schedule is null");
					}
				} else {
					System.out.println(" project === null");
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}

	public void initTableDataVector(TCComponent[] objects) {
		oldCompVec = new Vector<TCComponent>(); // 记录表单属性原有对象以备本次保存时删除
		try {
			if (objects != null) {
				for (int i = 0; i < objects.length; i++) {
					oldCompVec.add(objects[i]);
					Vector<String> temp = new Vector<String>();
					TCProperty[] propertys = objects[i]
							.getTCProperties(wdhProNames);
					temp.add(propertys[0].getStringValue());
					TCComponentProject prj = (TCComponentProject) propertys[1]
							.getReferenceValue();
					TCComponentScheduleTask task = (TCComponentScheduleTask) propertys[2]
							.getReferenceValue();
					TCComponentGroup group = (TCComponentGroup) propertys[3]
							.getReferenceValue();
					String normalHour = objects[i].getProperty("jci6_Hour");
					String extractHour = objects[i]
							.getProperty("jci6_ExtraHour");
					String project_string = prj.getProperty("object_string");
					temp.add(project_string);
					projectMap.put(project_string, prj);
					String name = "";
					name = task.getProperty("object_name");
					//哈哈
					getProTask(prj);
//					Map<String,TCComponentScheduleTask> taskMap = null;
//					
//					
//					if(proTaskMap.containsKey(prj)){
//						taskMap = proTaskMap.get(prj);
//						taskMap.put(
//								name,
//								task);
//					}else{
//						taskMap = new HashMap<String, TCComponentScheduleTask>();
//						taskMap.put(name, task);
//						proTaskMap.put(prj, taskMap);
//					}			
					//taskMap.put(name, task);
					temp.add(name);
					temp.add(normalHour);
					temp.add(extractHour);
					temp.add(group.getFullName().split("\\.")[0]);
					data.add(temp);
				}
			}
			// 2014-8-12 modify by wuh 载入项目
			setProjectVector(loginUser);
		} catch (TCException e) {
			e.printStackTrace();
		}

	}

	public DefaultTableModel getDataModel() {
		return dtm;
	}

	/**
	 * 创建ExtWorkDayHourForm
	 * 
	 * @return
	 * @throws TCException
	 * @throws ServiceException
	 */
	private TCComponentForm createForm() throws TCException, ServiceException {
		if (dialog != null) {
			String m = null;
			if (lastMonth < 10) {
				m = "0" + String.valueOf(lastMonth);
			} else {
				m = String.valueOf(lastMonth);
			}
			String formname = divisionName + "_" + lastYear + "." + m
					+ "_ExtSupport Actual Hours";
			form = JCI6YFJCUtil.createForm(session, "JCI6_ExtWDHrForm",
					formname);
			setFormRelate(form);
		}
		return form;
	}

	// 得到当前日期的上一月
	private void getYearAndLastMonth() {
		Date date = new Date();
		int year = date.getYear() + 1900;
		int month = date.getMonth() + 1;
		if (month == 1) {
			lastYear = year - 1;
			lastMonth = 12;
		} else {
			lastYear = year;
			lastMonth = month - 1;
		}

	}

	/**
	 * form关联
	 * 
	 * @param form
	 * @throws TCException
	 */
	private void setFormRelate(TCComponentForm form) throws TCException {
		loginUser.getHomeFolder().add("contents", form);
	}

	// 获得JCI6_Division lov
	private void getDivisionValues() throws TCException {
		System.out.println("getDivisionValues in");
		ListOfValuesInfo info = TCComponentListOfValuesType.findLOVByName(
				session, "JCI6_Division").getListOfValues();
		Object[] object = info.getListOfValues();
		for (int i = 0; i < object.length; i++) {
			if (object[i] instanceof TCComponentGroup) {
				TCComponentGroup group = (TCComponentGroup) object[i];
				String gname = group.getFullName().split("\\.")[0];
				if (gname.equals(divisionName)) {
					divisionGroup = group;
					break;
				}
			}
		}
	}

	private void getDivisionGroup() throws TCException {
		if (form != null) {
			String object_name = form.getProperty("object_name");
			String[] str = object_name.split("_");
			divisionName = str[0];
			String[] yearMonth = str[1].split("\\.");
			lastYear = Integer.valueOf(yearMonth[0]);
			lastMonth = Integer.valueOf(yearMonth[1]);
			getDivisionValues();
		}
	}

	/**
	 * 得到审核人
	 * 
	 * @throws TCException
	 */
	private void getReviewUser() throws TCException {
		TCComponentRole[] roles = divisionGroup.getRoles();
		// 获得部门经理
		for (int j = 0; j < roles.length; j++) {
			if (roles[j].getProperty("role_name").equals("Division Manager")) {
				TCComponentGroupMember[] member = roles[j]
						.getGroupMembers(divisionGroup);
				if (member != null && member.length > 0) {
					for (int m = 0; m < member.length; m++) {
						// 判断当前groupmember是否为激活
						if (!member[m].getTCProperty("status")
								.getLogicalValue()) {
							signoffVec.add(member[m]);
						} else {
							System.out.println("Division Manager not active ");
						}
					}
				}
			}
		}
		int size = signoffVec.size();
		if (size == 0) {
			System.out
					.println("Division Manager not exist or not active,get section manager。。。");
			// 获得同级的section manager
			for (int j = 0; j < roles.length; j++) {
				if (roles[j].getProperty("role_name").equals("Section Manager")) {
					TCComponentGroupMember[] member = roles[j]
							.getGroupMembers(divisionGroup);
					if (member != null && member.length > 0) {
						for (int m = 0; m < member.length; m++) {
							System.out.println("current group section manager");
							if (!member[m].getTCProperty("status")
									.getLogicalValue()) {
								signoffVec.add(member[m]);
							} else {
								System.out
										.println("section manager not active ");
							}

						}
					}
				}
			}
			size = signoffVec.size();
			// 获得子组的section manager
			if (size == 0) {
				System.out
						.println("current group section manager not exist or not active,get child group section manager");
				TCComponentGroup[] childGroups = divisionGroup.getGroups();
				if (childGroups != null && childGroups.length > 0) {
					for (int j = 0; j < childGroups.length; j++) {
						TCComponentRole[] childRoles = childGroups[j]
								.getRoles();
						for (int h = 0; h < childRoles.length; h++) {
							if (childRoles[h].getProperty("role_name").equals(
									"Section Manager")) {
								TCComponentGroupMember[] member = childRoles[h]
										.getGroupMembers(childGroups[j]);
								if (member != null && member.length > 0) {
									for (int m = 0; m < member.length; m++) {
										System.out
												.println("child group section manager");
										if (!member[m].getTCProperty("status")
												.getLogicalValue()) {
											signoffVec.add(member[m]);
										} else {
											System.out
													.println("child group section manager not active ");
										}
									}
								}
							}
						}
					}
				} else {
					System.out.println("child group null");
				}
			}

		}
	}

	private void getExtName(TCSession session, String divisionName,
			String lastMonth_year, String lastMonth_month, String own_proxy)
			throws TCException {
		System.out.println(divisionName + "--->" + lastMonth_year + "--->"
				+ lastMonth_month + "--->" + own_proxy);
		TCComponent[] comps = query(session, "YFJC_SearchExtSupportRelease",
				new String[] { "division_name", "jci6_Year", "jci6_Month",
						"proxy_id" }, new String[] { divisionName,
						lastMonth_year, lastMonth_month, own_proxy });
		if (comps != null && comps.length > 0) {
			for (int i = 0; i < comps.length; i++) {
				extNames.add(comps[i].getProperty("jci6_UserName"));
			}
		}
	}

	public Vector<TCComponent> getOldCompVec() {
		return oldCompVec;
	}

	public Map<TCComponentProject, Map<String, TCComponentScheduleTask>> getProTaskMap() {
		return proTaskMap;
	}

	
	
	

}
