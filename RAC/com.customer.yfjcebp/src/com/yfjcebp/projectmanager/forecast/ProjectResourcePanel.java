/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: ProjectResourcePanel.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-3-3	liujm  	    SP-RICH-FUN-04.查看成本计划										   
#=============================================================================
 */
package com.yfjcebp.projectmanager.forecast;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

//import org.apache.log4j.Logger;





import org.eclipse.core.runtime.IProgressMonitor;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.commands.open.OpenCommand;
import com.teamcenter.rac.commands.properties.PropertiesCommand;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.explorer.common.AbstractOptionPanel;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.kernel.tcservices.TcPropService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.SplitPane;
import com.yfjcebp.projectmanager.budget.ProgressBarThread;

/**
 * @author liujm
 */
public class ProjectResourcePanel extends JPanel {

	/**
	 * 
	 */
	private JPopupMenu pm=null;
	private JMenuItem mi2=null;
	private JMenuItem mi1=null;
	private JMenuItem mi3=null;
	private TCComponent showCompnont_all=null;
	
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(ProjectResourcePanel.class);
	private String widthStr = "25";

	protected TCSession session;
	//private Registry reg;
	private static final String BURFLR_NAME = "com.yfjcebp.projectmanager.forecast.forecast_locale";
	public static final ResourceBundle reg = ResourceBundle
			.getBundle(BURFLR_NAME);

	

	private JButton refreshButLab = null;
	
	// Summary 成本预算汇总
	private JScrollPane scrollPaneSum;
	private TCTable summaryTable;
	private String[] table_titleSum;

	// Labour Memo 人工成本明细
	private JScrollPane scrollPaneLab;
	private TCTable labourMemoTable;
	private String[] table_titleLab;

	// Non Labour Memo 非人工成本明细
	private JScrollPane scrollPaneNon;

	private TCTable nonLabourMemoTable;
	private String[] table_titleNon;

	private TCComponentItem programInfoItem;

	// 存放加载界面的时候的原始数据或者新创建的数据
	private Vector<TCComponent> VecCostInfo = new Vector<TCComponent>();

	public Vector<TCComponent> getVecCostInfo() {
		return VecCostInfo;
	}

	public void setVecCostInfo(Vector<TCComponent> vecCostInfo) {
		VecCostInfo = vecCostInfo;
	}
	
	public ProjectResourcePanel() {
		super(true);
		
		session = RACUIUtil.getTCSession();
		initializePanel();
		
		summaryTable.removeAllRows();
		labourMemoTable.removeAllRows();
		nonLabourMemoTable.removeAllRows();
	}

	public ProjectResourcePanel(TCSession tcsession) {
		super(true);
		session = null;
		session = tcsession;
		//reg = Registry.getRegistry(this);
		initializePanel();
		
		summaryTable.removeAllRows();
		labourMemoTable.removeAllRows();
		nonLabourMemoTable.removeAllRows();
	}

	public ProjectResourcePanel(TCComponentItem programInfoItem, TCComponentSchedule schedule, TCComponentScheduleTask scheduleSummaryTask) {
		super(true);
		this.programInfoItem = programInfoItem;
	}

	public TCComponentItem getProgramInfoItem() {
		return programInfoItem;
	}

	public void setProgramInfoItem(TCComponentItem programInfoItem) {
		this.programInfoItem = programInfoItem;
	}
	
	public void setProgramInfoItem2(TCComponent nodeComponent) {
		
		if (nodeComponent != null) {
			this.programInfoItem = null;
			if (nodeComponent instanceof TCComponentProject) {
				System.out.println("tree select TCComponentProject");
				TCComponentProject currentproject = (TCComponentProject) nodeComponent;
				try {
					TCComponent[] preferred_Items = currentproject
							.getReferenceListProperty("TC_Program_Preferred_Items");
					int project_dataLength = preferred_Items.length;
					System.out
							.println("TC_Program_Preferred_Items   length--->"
									+ project_dataLength);
					for (int i = 0; i < project_dataLength; i++) {
						TCComponent tccomponent = preferred_Items[i];
						if (tccomponent.getType().equals("JCI6_ProgramInfo")) {
							programInfoItem  = (TCComponentItem) tccomponent;
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			} else if (nodeComponent instanceof TCComponentItem) {
				System.out.println("tree select TCComponentItem");
				if (nodeComponent.getType().equals("JCI6_ProgramInfo")) {
					programInfoItem  = (TCComponentItem) nodeComponent;
				} 
				
			} else if (nodeComponent instanceof TCComponentItemRevision) {
				System.out.println("tree select TCComponentItemRevision");
				if (nodeComponent.getType().equals("JCI6_ProgramInfoRevision")) {
					TCComponentItemRevision programRev = (TCComponentItemRevision) nodeComponent;
					try {
						programInfoItem = programRev.getItem();
					} catch (TCException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
			} 
		}
	}

	/**
	 * 初始化这个panel
	 */
	private void initializePanel() {
		
		System.out.println("initializePanel() ------start");
		// tyl  2015/05/05
//		 table_titleSum = new String[] { "jci6_forecast_total_cost",
//		 "jci6_forecast_labor_hc", "jci6_forecast_labor_hours",
//		 "jci6_forecast_labor_cost", "jci6_forecast_sample",
//		 "jci6_forecast_sample_ext", "jci6_forecast_sample_int",
//		 "jci6_forecast_sample_pv", "jci6_forecast_test",
//		 "jci6_forecast_test_dv","jci6_forecast_test_pv",
//		 "jci6_forecast_tool","jci6_forecast_other_freight",
//		 "jci6_forecast_other_support", "jci6_forecast_other_travel" };

		table_titleSum = new String[] { "jci6_forecast_total_cost", "jci6_forecast_labor_hc", "jci6_forecast_labor_hours", "jci6_forecast_labor_cost", "jci6_forecast_sample", "jci6_forecast_sample_ext", "jci6_forecast_sample_int", "jci6_forecast_sample_pv", "jci6_forecast_sample_mnjt","jci6_forecast_test",
				"jci6_forecast_test_dv","jci6_forecast_test_pv", "jci6_forecast_test_wfsy","jci6_forecast_test_sygz", "jci6_forecast_tool", "jci6_forecast_other_freight", "jci6_forecast_other_support", "jci6_forecast_other_travel" };

//		String[] revProNamesWidth = new String[] { widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr };
		String[] revProNamesWidth = new String[] { widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr };
		
		// tyl  2015/05/05
		System.out.println("########ProjectResourcePanel----initializePanel----");
		
		pm = new JPopupMenu();
		mi1 = new JMenuItem("open with      ");
		mi2 = new JMenuItem("View Properties");
		mi3 = new JMenuItem("refresh        ");
		
		pm.add(mi1);
		pm.add(mi2);
		pm.add(mi3);
		mi2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				// TODO Auto-generated method stub
				if(showCompnont_all!=null){
					PropertiesCommand cmd=new PropertiesCommand(showCompnont_all,AIFUtility.getActiveDesktop());
					cmd.executeModeless();
				}
			}
		});
		
		mi1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				// TODO Auto-generated method stub
				if(showCompnont_all!=null){
					OpenCommand cmd=new OpenCommand(AIFUtility.getActiveDesktop(),showCompnont_all);
					cmd.executeModeless();
				}
			}
		});
		
		mi3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				// TODO Auto-generated method stub
				if(showCompnont_all!=null){
					try {
						showCompnont_all.refresh();
					} catch (TCException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		summaryTable = new TCTable() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		summaryTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent paramMouseEvent) {
				labourMemoTable.clearSelection();
				nonLabourMemoTable.clearSelection();
				if(paramMouseEvent.isMetaDown()){//检测鼠标右键单击
					Point localPoint = new Point(paramMouseEvent.getX(),
							paramMouseEvent.getY());
					int i = summaryTable.rowAtPoint(localPoint);
					if (i != -1)
						summaryTable.setRowSelectionInterval(i, i);
					else
						summaryTable.clearSelection();
					AIFComponentContext[] arrayOfAIFComponentContext = summaryTable
							.getSelectedContextObjects();
					
					if(arrayOfAIFComponentContext==null || arrayOfAIFComponentContext.length==0)
						return;
					
					showCompnont_all=(TCComponent)arrayOfAIFComponentContext[0].getComponent();
					
					//if(mi2==null)
					// mi2 = new JMenuItem("View Properties");

					pm.show(summaryTable, paramMouseEvent.getX(),
							paramMouseEvent.getY());
					
				}
				
			}
		}); 
		
		//ceshi by wuwei
		summaryTable.setDragEnabled(true);
		//summaryTable.getRowComponent(paramInt)
		
		summaryTable.setColumnNames(session, table_titleSum, "JCI6_ProgramInfo");
		summaryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		summaryTable.setColumnWidths(revProNamesWidth);
		summaryTable.setPreferredScrollableViewportSize(new Dimension(1000, 40));
		scrollPaneSum = new JScrollPane(summaryTable);

//		2016-03-28	mengyawei modify
//		JButton refreshButLab = new JButton(reg.getString("Refresh.LABEL"));

		refreshButLab = new JButton(reg.getString("Refresh.LABEL"));

		refreshButLab.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Runnable able = new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						session.queueOperation(new AbstractAIFOperation() {
							@Override
							public void executeOperation() throws Exception {
								// TODO Auto-generated method stub
								String title = reg.getString("progressbar.title");
								String msg = reg.getString("progressbar.msg");
								System.out.println("2016-04-20	title = "+title+"	msg = "+msg);
								com.yfjc.lccreport.ProgressBarThread bp = new com.yfjc.lccreport.ProgressBarThread(title,msg);
								bp.start();
								refreshLabourMemo();
								bp.stopBar();
							}
						});
					}
				};
				SwingUtilities.invokeLater(able);
			}

		});
		JPanel panelbuttonLab = new JPanel();
		panelbuttonLab.add(refreshButLab);

		JPanel paneSummary = new JPanel(new BorderLayout());
		paneSummary.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("SummaryTable.LABEL")));
		paneSummary.add(BorderLayout.CENTER, scrollPaneSum);
		paneSummary.add(BorderLayout.NORTH, panelbuttonLab);

		String[] costInfoProNames = new String[] { "jci6_Division", "jci6_User", "jci6_RateLevel", "jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
		table_titleLab = new String[17];
		table_titleNon = new String[15];
		for (int i = 0; i < 18; i++) {
			String proName = costInfoProNames[i];
			if (i <= 2) {
				table_titleLab[i] = proName;
			} else {
				if (i > 3) {
					table_titleLab[i - 1] = proName;
				}
				table_titleNon[i - 3] = proName;
			}
		}

		labourMemoTable = new TCTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		labourMemoTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent paramMouseEvent) {
				summaryTable.clearSelection();
				nonLabourMemoTable.clearSelection();
				if(paramMouseEvent.isMetaDown()){//检测鼠标右键单击

					Point localPoint = new Point(paramMouseEvent.getX(),
							paramMouseEvent.getY());
					int i = labourMemoTable.rowAtPoint(localPoint);
					if (i != -1)
						labourMemoTable.setRowSelectionInterval(i, i);
					else
						labourMemoTable.clearSelection();
					AIFComponentContext[] arrayOfAIFComponentContext = labourMemoTable
							.getSelectedContextObjects();
					
					if(arrayOfAIFComponentContext==null || arrayOfAIFComponentContext.length==0)
						return;
					
					showCompnont_all=(TCComponent)arrayOfAIFComponentContext[0].getComponent();
					
					//if(mi2==null)
					// mi2 = new JMenuItem("View Properties");

					pm.show(labourMemoTable, paramMouseEvent.getX(),
							paramMouseEvent.getY());
					
				}
				
			}
		}); 
		
		labourMemoTable.setDragEnabled(true);
		labourMemoTable.setColumnNames(session, table_titleLab, "JCI6_CostInfo");
		labourMemoTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		labourMemoTable.setPreferredScrollableViewportSize(new Dimension(1000, 150));
		scrollPaneLab = new JScrollPane(labourMemoTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JPanel paneLab = new JPanel(new BorderLayout());
		paneLab.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("LabourMemoTable.LABEL")));

		paneLab.add(BorderLayout.CENTER, scrollPaneLab);

		nonLabourMemoTable = new TCTable() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		nonLabourMemoTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent paramMouseEvent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent paramMouseEvent) {
				summaryTable.clearSelection();
				labourMemoTable.clearSelection();
				
				if(paramMouseEvent.isMetaDown()){//检测鼠标右键单击

					Point localPoint = new Point(paramMouseEvent.getX(),
							paramMouseEvent.getY());
					int i = nonLabourMemoTable.rowAtPoint(localPoint);
					if (i != -1)
						nonLabourMemoTable.setRowSelectionInterval(i, i);
					else
						nonLabourMemoTable.clearSelection();
					
					AIFComponentContext[] arrayOfAIFComponentContext = nonLabourMemoTable
							.getSelectedContextObjects();
					
					if(arrayOfAIFComponentContext==null || arrayOfAIFComponentContext.length==0)
						return;
					
					showCompnont_all=(TCComponent)arrayOfAIFComponentContext[0].getComponent();
					
					//if(mi2==null)
					// mi2 = new JMenuItem("View Properties");

					pm.show(nonLabourMemoTable, paramMouseEvent.getX(),
							paramMouseEvent.getY());
					
				}
				
			}
		}); 
		
		nonLabourMemoTable.setDragEnabled(true);
		nonLabourMemoTable.setColumnNames(session, table_titleNon, "JCI6_CostInfo");
		nonLabourMemoTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		nonLabourMemoTable.setPreferredScrollableViewportSize(new Dimension(1000, 150));
		scrollPaneNon = new JScrollPane(nonLabourMemoTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JPanel paneNon = new JPanel(new BorderLayout());
		paneNon.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("NonLabourMemoTable.LABEL")));
		paneNon.add(BorderLayout.CENTER, scrollPaneNon);

		setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(BorderLayout.NORTH, paneSummary);
		SplitPane splitpane = new SplitPane(1);
		splitpane.setTopComponent(paneLab);
		splitpane.setBottomComponent(paneNon);
		mainPanel.add(BorderLayout.CENTER, splitpane);
		add("Center", mainPanel);
		
		System.out.println("initializePanel() ------end");
	}

	/**
	 * 刷新人工成本表
	 */
	public void refreshLabourMemo() {
		VecCostInfo.clear();
		labourMemoTable.clear();
		nonLabourMemoTable.clear();
		refreshButLab.setEnabled(false);
		getCostInfoAgain();
		fillOriginalDataToTable();
		refreshButLab.setEnabled(true);
	}

	/**
	 * 调用c代码重新生成CostInfo对象
	 */
	public void getCostInfoAgain() {
		System.out.println("----getCostInfoAgain()   start====");
		TCUserService service = session.getUserService();
		System.out.println("getCostInfoAgain方法： programInfoItem-->"+programInfoItem);
		Object[] parameters = new Object[] { programInfoItem };
		try {
			service.call("yfjc_ebp_transToCostInfo", parameters);
			
		} catch (TCException e) {
			e.printStackTrace();
		}
		try {
			programInfoItem.refresh();
			TCComponent[] costInfos = programInfoItem.getRelatedComponents("IMAN_external_object_link");
			int costInfoLength = costInfos.length;
			//logger.debug("after refresh,find " + costInfoLength + " CostInfo Object...");
			if (costInfoLength > 0) {
				for (int i = 0; i < costInfoLength; i++) {
					TCComponent costInfo = costInfos[i];
					costInfo.refresh();
					if (costInfo.getType().equals("JCI6_CostInfo")) {
						if (costInfo.getProperty("jci6_CPT").equals(reg.getString("jci6_CPT.NAME"))) {
							VecCostInfo.add(costInfo);
						}
					}
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		System.out.println("----getCostInfoAgain()   end====");
	}

	/**
	 * 刚开始加载界面时候填充的原始数据
	 */

	public void fillOriginalData() {
		System.out.println("----fillOriginalData(1)   start====");
		
		try {
			TCComponent[] costInfos = null;
//			summaryTable.clear();
//			labourMemoTable.clear();
//			nonLabourMemoTable.clear();
			
			//new by wuwei
			summaryTable.removeAllRows();
			labourMemoTable.removeAllRows();
			nonLabourMemoTable.removeAllRows();
			
			if (programInfoItem != null) {
				summaryTable.addRow(programInfoItem);
				programInfoItem.refresh();
				costInfos = programInfoItem.getRelatedComponents("IMAN_external_object_link");
			}
			if (costInfos != null) {
				int costInfoLength = costInfos.length;
				VecCostInfo.clear();
				System.out.println("find " + costInfoLength + " CostInfo Object...");
				if (costInfoLength > 0) {
					for (int i = 0; i < costInfoLength; i++) {
						TCComponent costInfo = costInfos[i];
						if (costInfo.getType().equals("JCI6_CostInfo")) {
							String key1=reg.getString("jci6_CPT.NAME");
							System.out.println("jci6_CPT.NAME--->"+key1);
							if (costInfo.getProperty("jci6_CPT").equals(key1)) {
								VecCostInfo.add(costInfos[i]);
							}
						}
					}
				}
				
				// int forecastCostInfoNum = VecCostInfo.size();
				// if (forecastCostInfoNum == 0) {
				// getCostInfoAgain();
				// }
				fillOriginalDataToTable();
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		System.out.println("----fillOriginalData(1)   end====");
	}

	/**
	 * 把得到的原始数据加载到人工成本和非人工成本的对应的表中
	 */
	public void fillOriginalDataToTable() {
		System.out.println("----fillOriginalDataToTable()   start====");
		int costInfoSize = VecCostInfo.size();
		TCComponent[] costInfos = new TCComponent[costInfoSize];
		costInfos = VecCostInfo.toArray(costInfos);
		String[] proNames = new String[] { "jci6_Division", "jci6_User", "jci6_RateLevel", "jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
		try {
			TcPropService.getProperties(session, costInfos, proNames);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (int i = 0; i < costInfoSize; i++) {
			TCComponent costInfo = VecCostInfo.get(i);
			String costType = "";
			try {
				costType = costInfo.getTCProperty("jci6_CostType").getDisplayValue();
			} catch (TCException e) {
				e.printStackTrace();
			}
			if (costType.equals(reg.getString("jci6_CostType.NAME"))) {
				labourMemoTable.addRow(costInfo);
			} else {
				nonLabourMemoTable.addRow(costInfo);
			}
		}
		
		System.out.println("----fillOriginalDataToTable()   end====");
	}

	// add by wuh 2014-5-14
	public void fillOriginalData(ProgressBarThread thread, int amount) {
		System.out.println("----fillOriginalData(ProgressBarThread thread, int amount)   start====");
		
		try {
			TCComponent[] costInfos = null;
			summaryTable.removeAllRows();
			labourMemoTable.removeAllRows();
			nonLabourMemoTable.removeAllRows();
			if (programInfoItem != null) {
				summaryTable.addRow(programInfoItem);
				programInfoItem.refresh();
				costInfos = programInfoItem.getRelatedComponents("IMAN_external_object_link");
			}
			thread.setProgressValue(amount / 100);
			int next_yb = amount / 100 * 99 / 2;
			if (costInfos != null) {
				int costInfoLength = costInfos.length;
				VecCostInfo.clear();
				System.out.println("jci6_CPT.NAME: " + reg.getString("jci6_CPT.NAME") );
				if (costInfoLength > 0) {
					int for_val = next_yb / costInfoLength;
					for (int i = 0; i < costInfoLength; i++) {
						TCComponent costInfo = costInfos[i];
						if (costInfo.getType().equals("JCI6_CostInfo")) {
							System.out.println("jci6_CPT-->"+costInfo.getProperty("jci6_CPT"));
							if (costInfo.getProperty("jci6_CPT").equals(reg.getString("jci6_CPT.NAME"))) {
								VecCostInfo.add(costInfos[i]);
							}
						}
						thread.setProgressValue(thread.getProgressValue() + for_val);
					}
				}
				thread.setProgressValue(next_yb + amount / 100);
				fillOriginalDataToTable(thread, next_yb);
				thread.setProgressValue(amount);
				//thread.close();
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		System.out.println("----fillOriginalData(ProgressBarThread thread, int amount)   end====");
	}

	// add by wuh 2014-5-14
	public void fillOriginalDataToTable(ProgressBarThread thread, int next_yb) {
		System.out.println("----fillOriginalDataToTable(ProgressBarThread thread, int next_yb)   start====");
		
		int costInfoSize = VecCostInfo.size();
		int for_val = next_yb;
		if (costInfoSize > 0) {
			for_val = next_yb / costInfoSize;
		}
		TCComponent[] costInfos = new TCComponent[costInfoSize];
		costInfos = VecCostInfo.toArray(costInfos);
		String[] proNames = new String[] { "jci6_Division", "jci6_User", "jci6_RateLevel", "jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
		try {
			TcPropService.getProperties(session, costInfos, proNames);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < costInfoSize; i++) {
			TCComponent costInfo = VecCostInfo.get(i);
			String costType = "";
			try {
				costType = costInfo.getTCProperty("jci6_CostType").getDisplayValue();
			} catch (TCException e) {
				e.printStackTrace();
			}
			System.out.println("costType-->"+costType);
			if (costType.equals(reg.getString("jci6_CostType.NAME"))) {
				labourMemoTable.addRow(costInfo);
			} else {
				nonLabourMemoTable.addRow(costInfo);
			}
			thread.setProgressValue(thread.getProgressValue() + for_val);
		}
		
		System.out.println("----fillOriginalDataToTable(ProgressBarThread thread, int next_yb)   end====");
	}

	
	public void sotosfillOriginalDataToTable(IProgressMonitor monitor){
		System.out.println("----fillOriginalDataToTable(ProgressBarThread thread, int next_yb)   start====");
		if (programInfoItem != null) {
			summaryTable.addRow(programInfoItem);
		}
		
		int costInfoSize = VecCostInfo.size();
		
		TCComponent[] costInfos = new TCComponent[costInfoSize];
		costInfos = VecCostInfo.toArray(costInfos);
		String[] proNames = new String[] { "jci6_Division", "jci6_User", "jci6_RateLevel", "jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
		try {
			TcPropService.getProperties(session, costInfos, proNames);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < costInfoSize; i++) {
			TCComponent costInfo = VecCostInfo.get(i);
			String costType = "";
			try {
				costType = costInfo.getTCProperty("jci6_CostType").getDisplayValue();
			} catch (TCException e) {
				e.printStackTrace();
			}
			System.out.println("costType-->"+costType);
			if (costType.equals(reg.getString("jci6_CostType.NAME"))) {
				labourMemoTable.addRow(costInfo);
				double a=(i*1.0/VecCostInfo.size())*100.0;
				String sum=a+"0";
				String t1=cutString(sum,2);
				monitor.setTaskName("complete... "+t1+"%");// 提示信息
                monitor.worked(1);// 进度条前进一步
			} else {
				nonLabourMemoTable.addRow(costInfo);
				double a=(i*1.0/VecCostInfo.size())*100.0;
				String sum=a+"0";
				String t1=cutString(sum,2);
				monitor.setTaskName("complete... "+t1+"%");// 提示信息
                monitor.worked(1);// 进度条前进一步
			}
			
		}
		
		System.out.println("----fillOriginalDataToTable(ProgressBarThread thread, int next_yb)   end====");
	}
	
	// 截取i位小数
			private String cutString(String str, int i) {
				String stringValue = "";
				if (str.contains(".")) {
					stringValue = (str ).substring(0,
							(str).indexOf(".") + (i + 1));
				} else {
					stringValue = str + ".00";
				}
				return stringValue;
			}
}
