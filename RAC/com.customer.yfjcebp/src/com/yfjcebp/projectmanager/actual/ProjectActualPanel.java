/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: ReadDataFromExcel.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-3-11	liujm  	SP-RICH-FUN-06.查看实际									   
#=============================================================================
 */
package com.yfjcebp.projectmanager.actual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

//import org.apache.log4j.Logger;







import org.eclipse.core.runtime.IProgressMonitor;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.commands.open.OpenCommand;
import com.teamcenter.rac.commands.properties.PropertiesCommand;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.tcservices.TcPropService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.SplitPane;
import com.yfjcebp.projectmanager.budget.ProgressBarThread;

/**
 * @author liujm
 */
public class ProjectActualPanel extends JPanel {

	/**
	 * mi1 open
	 * mi2 显示属性
	 * mi3 refresh
	 * 
	 * 
	 */
	private JPopupMenu pm=null;
	private JMenuItem mi2=null;
	private JMenuItem mi1=null;
	private JMenuItem mi3=null;
	private TCComponent showCompnont_all=null;
	
	private JPanel paneSummary=null;
	
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(ProjectActualPanel.class);

	private String widthStr = "25";
	protected TCSession session;

	
	//private Registry reg;
	
	private static final String BURFLR_NAME = "com.yfjcebp.projectmanager.actual.actual_locale";
	public static final ResourceBundle reg = ResourceBundle
			.getBundle(BURFLR_NAME);

	// Summary 成本预算汇总
	private JScrollPane scrollPaneSum;
	private TCTable summaryTable;
	private String[] table_titleSum;

	// add 2014-6-10
	private JScrollPane pr_scrollPaneSum;
	private TCTable summaryPRTable;
	private String[] table_pr_titleSum;
	private JScrollBar hScrollBar;
	
	// add by yangh  2016-3-23
	//private JScrollPane po_scrollPaneSum;
	private TCTable summaryPOTable;
	private String[] table_po_titleSum;
	private JScrollBar po_hScrollBar;
	//add by yangh 2016/3/23
	

	// Labour Memo 人工成本明细
	private JScrollPane scrollPaneLab;
	private TCTable labourMemoTable;
	private String[] table_titleLab;

	// Non Labour Memo(PR) 非人工成本明细
	private JScrollPane scrollPaneNonPR;
	private TCTable nonLabourMemoPRTable;
	private String[] table_titleNon;
	
	// add by yangh  2016-3-23
	// Non Labour Memo(PO) 非人工成本明细
	//private JScrollPane scrollPaneNonPO;
	private TCTable nonLabourMemoPOTable;
	// add by yangh  2016-3-23
	

	// Non Labour Memo(Actual) 非人工成本明细
	private JScrollPane scrollPaneNonActual;
	private TCTable nonLabourMemoActualTable;

	private TCComponentItem programInfoItem;

	// 存放加载界面的时候的原始数据
	private Vector<TCComponent> VecCostInfo = new Vector<TCComponent>();
	

	public Vector<TCComponent> getVecCostInfo() {
		return VecCostInfo;
	}

	public void setVecCostInfo(Vector<TCComponent> vecCostInfo) {
		VecCostInfo = vecCostInfo;
	}

	public ProjectActualPanel() {
		super(true);
		
		session = RACUIUtil.getTCSession();
		//reg = Registry.getRegistry(this);
		initializePanel();
		
		//add by wuwei
		summaryTable.removeAllRows();
		summaryPRTable.removeAllRows();
		//System.out.println("clear before summaryTable row count is " + summaryTable.getRowCount());
		labourMemoTable.removeAllRows();
		nonLabourMemoPRTable.removeAllRows();
		nonLabourMemoActualTable.removeAllRows();	
	}

	public ProjectActualPanel(TCSession tcsession) {
		super(true);
		session = null;
		session = tcsession;
		//reg = Registry.getRegistry(this);
		initializePanel();
		
		//add by wuwei
		summaryTable.removeAllRows();
		summaryPRTable.removeAllRows();
		//System.out.println("clear before summaryTable row count is " + summaryTable.getRowCount());
		labourMemoTable.removeAllRows();
		nonLabourMemoPRTable.removeAllRows();
		nonLabourMemoActualTable.removeAllRows();	
	}

	public ProjectActualPanel(TCComponentItem programInfoItem) {
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
			programInfoItem = null;
			if (nodeComponent instanceof TCComponentProject) {
				//System.out.println("tree select TCComponentProject");
				TCComponentProject currentproject = (TCComponentProject) nodeComponent;
				try {
					TCComponent[] preferred_Items = currentproject
							.getReferenceListProperty("TC_Program_Preferred_Items");
					int project_dataLength = preferred_Items.length;
					//System.out.println("TC_Program_Preferred_Items   length--->"+ project_dataLength);
									
					for (int i = 0; i < project_dataLength; i++) {
						TCComponent tccomponent = preferred_Items[i];
						if (tccomponent.getType().equals("JCI6_ProgramInfo")) {
							programInfoItem =  (TCComponentItem) tccomponent;
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			} else if (nodeComponent instanceof TCComponentItem) {
				//System.out.println("tree select TCComponentItem");
				if (nodeComponent.getType().equals("JCI6_ProgramInfo")) {
					programInfoItem = (TCComponentItem) nodeComponent;
				} 
				
			} else if (nodeComponent instanceof TCComponentItemRevision) {
				//System.out.println("tree select TCComponentItemRevision");
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

		//System.out.println("========lala  ProjectActualPanel  initializePanel===========start");
		// tyl 2015/05/05
		// modify by wuh 2014-6-10
		// table_titleSum = new String[]
		// {"jci6_actual_total_cost","jci6_actual_labor_hc","jci6_actual_labor_hours","jci6_actual_labor_cost",
		// "jci6_actual_sample","jci6_actual_sample_ext","jci6_actual_sample_int","jci6_actual_sample_pv",
		// "jci6_actual_test",
		// "jci6_actual_test_dv","jci6_actual_test_pv","jci6_actual_tool","jci6_actual_other_freight","jci6_actual_other_support","jci6_actual_other_travel"
		// };
		//
		// String[] revProNamesWidth = new String[] { widthStr, widthStr,
		// widthStr, widthStr, widthStr, widthStr, widthStr, widthStr,
		// widthStr, widthStr, widthStr, widthStr, widthStr, widthStr,
		// widthStr};
		// table_pr_titleSum = new String[] { "jci6_pr_total_cost",
		// "jci6_actual_labor_hc", "jci6_actual_labor_hours",
		// "jci6_actual_labor_cost", "jci6_pr_sample", "jci6_pr_sample_ext",
		// "jci6_pr_sample_int", "jci6_pr_sample_pv", "jci6_pr_test",
		// "jci6_pr_test_dv", "jci6_pr_test_pv", "jci6_pr_tool",
		// "jci6_actual_other_freight", "jci6_actual_other_support",
		// "jci6_actual_other_travel" };

		table_titleSum = new String[] { "jci6_actual_total_cost", "jci6_actual_labor_hc", "jci6_actual_labor_hours", "jci6_actual_labor_cost", "jci6_actual_sample", "jci6_actual_sample_ext", "jci6_actual_sample_int", "jci6_actual_sample_pv", "jci6_actual_sample_mnjt","jci6_actual_test", "jci6_actual_test_dv",
				"jci6_actual_test_pv","jci6_actual_test_wfsy","jci6_actual_test_sygz", "jci6_actual_tool", "jci6_actual_other_freight", "jci6_actual_other_support", "jci6_actual_other_travel" };

		String[] revProNamesWidth = new String[] { widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr , widthStr, widthStr, widthStr};

		table_pr_titleSum = new String[] { "jci6_pr_total_cost", "jci6_actual_labor_hc", "jci6_actual_labor_hours", "jci6_actual_labor_cost", "jci6_pr_sample", "jci6_pr_sample_ext", "jci6_pr_sample_int", "jci6_pr_sample_pv","jci6_pr_sample_mnjt", "jci6_pr_test", "jci6_pr_test_dv", "jci6_pr_test_pv","jci6_pr_test_wfsy","jci6_pr_test_sygz", "jci6_pr_tool",
				"jci6_actual_other_freight", "jci6_pr_other_support", "jci6_actual_other_travel" };
		//add by yangh 2016/3/23
		table_po_titleSum = new String[] { "jci6_po_total_cost", "jci6_actual_labor_hc", "jci6_actual_labor_hours", "jci6_actual_labor_cost", "jci6_po_sample", "jci6_po_sample_ext", "jci6_po_sample_int", "jci6_po_sample_pv","jci6_po_sample_mnjt", "jci6_po_test", "jci6_po_test_dv", "jci6_po_test_pv","jci6_po_test_wfsy","jci6_po_test_sygz", "jci6_po_tool",
				"jci6_actual_other_freight", "jci6_po_other_support", "jci6_actual_other_travel" };
		//add by yangh 2016/3/23
		
		
		// tyl 2015/05/05

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

		summaryPRTable = new TCTable() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		//add by yangh 2016/3/23
		summaryPOTable = new TCTable() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		//add by yangh 2016/3/23
		
		
		summaryTable.setColumnNames(session, table_titleSum, "JCI6_ProgramInfo");
		// summaryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		summaryTable.setColumnWidths(revProNamesWidth);
		summaryTable.setPreferredScrollableViewportSize(new Dimension(800, 15));
		
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
				
				summaryPRTable.clearSelection();
				summaryPOTable.clearSelection();
				nonLabourMemoPRTable.clearSelection();
				nonLabourMemoPOTable.clearSelection();
				nonLabourMemoActualTable.clearSelection();
				labourMemoTable.clearSelection();
				
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

		
		summaryPRTable.setDragEnabled(true);
		summaryPRTable.setColumnNames(session, table_pr_titleSum, "JCI6_ProgramInfo");
		// summaryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		summaryPRTable.setColumnWidths(revProNamesWidth);
		summaryPRTable.setPreferredScrollableViewportSize(new Dimension(800, 15));
		summaryPRTable.addMouseListener(new MouseListener() {
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
				summaryPOTable.clearSelection();
				nonLabourMemoPRTable.clearSelection();
				nonLabourMemoPOTable.clearSelection();
				nonLabourMemoActualTable.clearSelection();
				labourMemoTable.clearSelection();
				if(paramMouseEvent.isMetaDown()){//检测鼠标右键单击

					Point localPoint = new Point(paramMouseEvent.getX(),
							paramMouseEvent.getY());
					int i = summaryPRTable.rowAtPoint(localPoint);
					if (i != -1)
						summaryPRTable.setRowSelectionInterval(i, i);
					else
						summaryPRTable.clearSelection();
					AIFComponentContext[] arrayOfAIFComponentContext = summaryPRTable
							.getSelectedContextObjects();
					
					
					if(arrayOfAIFComponentContext==null || arrayOfAIFComponentContext.length==0)
						return;
					
					showCompnont_all=(TCComponent)arrayOfAIFComponentContext[0].getComponent();
					
					//if(mi2==null)
					// mi2 = new JMenuItem("View Properties");

					pm.show(summaryPRTable, paramMouseEvent.getX(),
							paramMouseEvent.getY());
					
				}
				
			}
		}); 
		
		
		
		//add by yangh 2016/3/23
		summaryPOTable.setDragEnabled(true);
		summaryPOTable.setColumnNames(session, table_po_titleSum, "JCI6_ProgramInfo");
		summaryPOTable.setColumnWidths(revProNamesWidth);
		summaryPOTable.setPreferredScrollableViewportSize(new Dimension(800, 15));
		//add by yangh 2016/3/23
		summaryPOTable.addMouseListener(new MouseListener() {
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
				summaryPRTable.clearSelection();
				nonLabourMemoPRTable.clearSelection();
				nonLabourMemoPOTable.clearSelection();
				nonLabourMemoActualTable.clearSelection();
				labourMemoTable.clearSelection();
				if(paramMouseEvent.isMetaDown()){//检测鼠标右键单击

					Point localPoint = new Point(paramMouseEvent.getX(),
							paramMouseEvent.getY());
					int i = summaryPOTable.rowAtPoint(localPoint);
					if (i != -1)
						summaryPOTable.setRowSelectionInterval(i, i);
					else
						summaryPOTable.clearSelection();
					AIFComponentContext[] arrayOfAIFComponentContext = summaryPOTable
							.getSelectedContextObjects();
					
					
					if(arrayOfAIFComponentContext==null || arrayOfAIFComponentContext.length==0)
						return;
					
					showCompnont_all=(TCComponent)arrayOfAIFComponentContext[0].getComponent();
					
					//if(mi2==null)
					// mi2 = new JMenuItem("View Properties");

					pm.show(summaryPOTable, paramMouseEvent.getX(),
							paramMouseEvent.getY());
					
				}
				
			}
		}); 
		
		
		
		
		TCTable test_table=new TCTable();
		// scrollPaneSum = new JScrollPane(summaryTable);
		scrollPaneSum = new JScrollPane(summaryTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//add by yangh 2016/3/23  更改了水平滚动条不显示
		pr_scrollPaneSum = new JScrollPane(summaryPRTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// pr_scrollPaneSum.getVerticalScrollBar().setVisible(false);
		
		//add by yangh 2016/3/23
		//po_scrollPaneSum = new JScrollPane(summaryPOTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//获取po水平滚动条
//		hScrollBar = po_scrollPaneSum.getHorizontalScrollBar();
//
//		hScrollBar.addAdjustmentListener(new AdjustmentListener() {
//			@Override
//			public void adjustmentValueChanged(AdjustmentEvent adjustmentevent) {
//				// TODO Auto-generated method stub
//				// //System.out.println("adjustmentValueChanged");
//				scrollPaneSum.getHorizontalScrollBar().setValue(hScrollBar.getValue());
//				pr_scrollPaneSum.getHorizontalScrollBar().setValue(hScrollBar.getValue());
//			}
//		});
		
		hScrollBar = pr_scrollPaneSum.getHorizontalScrollBar();
		hScrollBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent adjustmentevent) {
				// TODO Auto-generated method stub
				// //System.out.println("adjustmentValueChanged");
				scrollPaneSum.getHorizontalScrollBar().setValue(hScrollBar.getValue());
			}
		});
		
		//add by yangh 2016/3/23
		
		
		// Color lightBlue = new Color(92,155,178);
		// setTableHeaderColor(summaryTable,0,lightBlue);
		// setTableHeaderColor(summaryTable,3,Color.lightGray);
		// setTableHeaderColor(summaryTable,4,Color.lightGray);
		// setTableHeaderColor(summaryTable,8,Color.lightGray);
		// setTableHeaderColor(summaryTable,11,Color.lightGray);
		// setTableHeaderColor(summaryTable,12,Color.lightGray);
		// setTableHeaderColor(summaryTable,13,Color.lightGray);
		// setTableHeaderColor(summaryTable,14,Color.lightGray);
		//
		// setTableHeaderColor(summaryPRTable,0,lightBlue);
		// setTableHeaderColor(summaryPRTable,3,Color.lightGray);
		// setTableHeaderColor(summaryPRTable,4,Color.lightGray);
		// setTableHeaderColor(summaryPRTable,8,Color.lightGray);
		// setTableHeaderColor(summaryPRTable,11,Color.lightGray);
		// setTableHeaderColor(summaryPRTable,12,Color.lightGray);
		// setTableHeaderColor(summaryPRTable,13,Color.lightGray);
		// setTableHeaderColor(summaryPRTable,14,Color.lightGray);

		//add by yangh 2016/3/23 更改成了3行一列
		 paneSummary = new JPanel(new GridLayout(2, 1));
		//add by yangh 2016/3/23 
		TitledBorder actual_border = new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("ActualSummaryTable.LABEL"));
		TitledBorder pr_border = new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("Actual_PRSummaryTable.LABEL"));
		TitledBorder po_border = new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("Actual_POSummaryTable.LABEL"));
//		po_scrollPaneSum.setBorder(po_border);
		scrollPaneSum.setBorder(actual_border);
		pr_scrollPaneSum.setBorder(pr_border);
		paneSummary.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("SummaryTable.LABEL")));
		paneSummary.add(scrollPaneSum);
		paneSummary.add(pr_scrollPaneSum);
		//add by yangh 2016/3/23 新增po scrollpane
//		paneSummary.add(po_scrollPaneSum);
		//add by yangh 2016/3/23

		//"jci6_datasource",--modify by wuwei
		String[] costInfoProNames1 = new String[] { "jci6_Division", "jci6_User", "jci6_RateLevel", "jci6_CostType",  "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
		table_titleLab = new String[18];
		for (int i = 0; i < 18; i++) {
			String proName = costInfoProNames1[i];
			table_titleLab[i] = proName;
		}
		
		String[] costInfoProNames = new String[] { "jci6_Division", "jci6_User", "jci6_RateLevel", "jci6_CostType", "jci6_datasource", "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
		//table_titleLab = new String[19];
		table_titleNon = new String[16];
		for (int i = 0; i < 19; i++) {
			String proName = costInfoProNames[i];
			//table_titleLab[i] = proName;
			if (i >= 3) {
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
		labourMemoTable.setColumnNames(session, table_titleLab, "JCI6_CostInfo");
		labourMemoTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		labourMemoTable.setPreferredScrollableViewportSize(new Dimension(1000, 150));
		scrollPaneLab = new JScrollPane(labourMemoTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

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
				summaryPRTable.clearSelection();
				summaryPOTable.clearSelection();
				nonLabourMemoPRTable.clearSelection();
				nonLabourMemoPOTable.clearSelection();
				nonLabourMemoActualTable.clearSelection();
				
				
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
		
		
		
		
		
		
		
		
		JPanel paneLab = new JPanel(new BorderLayout());
		paneLab.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("LabourMemoTable.LABEL")));
		paneLab.add(BorderLayout.CENTER, scrollPaneLab);

		nonLabourMemoPRTable = new TCTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		
		nonLabourMemoPRTable.addMouseListener(new MouseListener() {
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
				summaryPRTable.clearSelection();
				summaryPOTable.clearSelection();
				labourMemoTable.clearSelection();
				nonLabourMemoPOTable.clearSelection();
				nonLabourMemoActualTable.clearSelection();
				
				if(paramMouseEvent.isMetaDown()){//检测鼠标右键单击

					Point localPoint = new Point(paramMouseEvent.getX(),
							paramMouseEvent.getY());
					int i = nonLabourMemoPRTable.rowAtPoint(localPoint);
					if (i != -1)
						nonLabourMemoPRTable.setRowSelectionInterval(i, i);
					else
						nonLabourMemoPRTable.clearSelection();
					AIFComponentContext[] arrayOfAIFComponentContext = nonLabourMemoPRTable
							.getSelectedContextObjects();
					
					
					if(arrayOfAIFComponentContext==null || arrayOfAIFComponentContext.length==0)
						return;
					
					showCompnont_all=(TCComponent)arrayOfAIFComponentContext[0].getComponent();
					
					//if(mi2==null)
					// mi2 = new JMenuItem("View Properties");

					pm.show(nonLabourMemoPRTable, paramMouseEvent.getX(),
							paramMouseEvent.getY());
					
				}
				
			}
		});
		
		
		
		nonLabourMemoPRTable.setColumnNames(session, table_titleNon, "JCI6_CostInfo");
		nonLabourMemoPRTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		nonLabourMemoPRTable.setPreferredScrollableViewportSize(new Dimension(1000, 150));
		scrollPaneNonPR = new JScrollPane(nonLabourMemoPRTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneNonPR.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("NonLabourMemoPRTable.LABEL")));
		
		//JPanel paneNonPR = new JPanel(new BorderLayout());
		//paneNonPR.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("NonLabourMemoPRTable.LABEL")));
		//paneNonPR.add(BorderLayout.CENTER, scrollPaneNonPR);

		//add by yangh 2016/3/23
		nonLabourMemoPOTable = new TCTable() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		nonLabourMemoPOTable.addMouseListener(new MouseListener() {
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
				summaryPRTable.clearSelection();
				summaryPOTable.clearSelection();
				labourMemoTable.clearSelection();
				nonLabourMemoPRTable.clearSelection();
				nonLabourMemoActualTable.clearSelection();
				
				if(paramMouseEvent.isMetaDown()){//检测鼠标右键单击

					Point localPoint = new Point(paramMouseEvent.getX(),
							paramMouseEvent.getY());
					int i = nonLabourMemoPOTable.rowAtPoint(localPoint);
					if (i != -1)
						nonLabourMemoPOTable.setRowSelectionInterval(i, i);
					else
						nonLabourMemoPOTable.clearSelection();
					AIFComponentContext[] arrayOfAIFComponentContext = nonLabourMemoPOTable
							.getSelectedContextObjects();
					
					
					showCompnont_all=(TCComponent)arrayOfAIFComponentContext[0].getComponent();
					
					//if(mi2==null)
					// mi2 = new JMenuItem("View Properties");

					pm.show(nonLabourMemoPOTable, paramMouseEvent.getX(),
							paramMouseEvent.getY());
					
				}
				
			}
		});
		nonLabourMemoPOTable.setColumnNames(session, table_titleNon, "JCI6_CostInfo");
		nonLabourMemoPOTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		nonLabourMemoPOTable.setPreferredScrollableViewportSize(new Dimension(1000, 150));
//		scrollPaneNonPO = new JScrollPane(nonLabourMemoPOTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

	//	JPanel paneNonPO = new JPanel(new BorderLayout());
	//	paneNonPO.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("NonLabourMemoPOTable.LABEL")));
	//	paneNonPO.add(BorderLayout.CENTER, scrollPaneNonPO);
		
		// end add by yangh 2016/3/23
		
		nonLabourMemoActualTable = new TCTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		nonLabourMemoActualTable.addMouseListener(new MouseListener() {
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
				summaryPRTable.clearSelection();
				summaryPOTable.clearSelection();
				labourMemoTable.clearSelection();
				nonLabourMemoPRTable.clearSelection();
				nonLabourMemoPOTable.clearSelection();
				
				if(paramMouseEvent.isMetaDown()){//检测鼠标右键单击

					Point localPoint = new Point(paramMouseEvent.getX(),
							paramMouseEvent.getY());
					int i = nonLabourMemoActualTable.rowAtPoint(localPoint);
					if (i != -1)
						nonLabourMemoActualTable.setRowSelectionInterval(i, i);
					else
						nonLabourMemoActualTable.clearSelection();
					AIFComponentContext[] arrayOfAIFComponentContext = nonLabourMemoActualTable
							.getSelectedContextObjects();
					
					if(arrayOfAIFComponentContext==null || arrayOfAIFComponentContext.length==0)
						return;
					
					showCompnont_all=(TCComponent)arrayOfAIFComponentContext[0].getComponent();
					
					//if(mi2==null)
					// mi2 = new JMenuItem("View Properties");

					pm.show(nonLabourMemoActualTable, paramMouseEvent.getX(),
							paramMouseEvent.getY());
					
				}
				
			}
		});
		
		nonLabourMemoActualTable.setColumnNames(session, table_titleNon, "JCI6_CostInfo");
		nonLabourMemoActualTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		nonLabourMemoActualTable.setPreferredScrollableViewportSize(new Dimension(1000, 150));
		scrollPaneNonActual = new JScrollPane(nonLabourMemoActualTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JPanel paneNonActual = new JPanel(new BorderLayout());
		paneNonActual.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("NonLabourMemoActualTable.LABEL")));
		paneNonActual.add(BorderLayout.CENTER, scrollPaneNonActual);

		setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(BorderLayout.NORTH, paneSummary);
		SplitPane mainSplitpane = new SplitPane(1);
		SplitPane bottomSplitpane = new SplitPane(1);
		SplitPane bottomSplitpane1 = new SplitPane(1);// add by yangh 2016/3/23
		bottomSplitpane.setTopComponent(scrollPaneNonPR);
		bottomSplitpane.setBottomComponent(bottomSplitpane1);
		// add by yangh 2016/3/23
		//bottomSplitpane1.setTopComponent(paneNonPO);
		//bottomSplitpane1.setBottomComponent(paneNonActual);
		// add by wuwei 2019/06/20
		bottomSplitpane1.setTopComponent(paneNonActual);
		
		// end add by yangh 2016/3/23
		mainSplitpane.setDividerSize(8);
		mainSplitpane.setTopComponent(paneLab);
		mainSplitpane.setBottomComponent(bottomSplitpane);

		mainPanel.add(BorderLayout.CENTER, mainSplitpane);

		JScrollPane mainScrollPane = new JScrollPane(mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add("Center", mainScrollPane);
		
		//System.out.println("========lala  ProjectActualPanel  initializePanel===========end");
	}

	

	// add by wuh 2014-5-14
	public void fillOriginalData(ProgressBarThread thread, int a) {
		System.out.println("fillOriginalData(ProgressBarThread thread, int a) ------start");
		//summaryTable.removeAllRows();
		//summaryPRTable.removeAllRows();
		//labourMemoTable.removeAllRows();
		//nonLabourMemoPRTable.removeAllRows();
		//nonLabourMemoActualTable.removeAllRows();
		//nonLabourMemoPOTable.removeAllRows();
		VecCostInfo.clear();
		labourMemoTableVec.clear();
		nonLabourMemoActualTableVec.clear();
		nonLabourMemoPRTableVec.clear();
		
		// end add by yangh 2016/3/23
		try {
			TCComponent[] costInfos = null;
			if (programInfoItem != null) {
				//summaryTable.addRow(programInfoItem);
				//summaryPRTable.addRow(programInfoItem);
				
				//modify by wuwei
				//summaryPOTable.addRow(programInfoItem);
				costInfos = programInfoItem.getRelatedComponents("IMAN_external_object_link");
			}
			thread.setProgressValue(thread.getProgressValue() + a / 100);
			int next_yb = a / 100 * 99 / 2;
			if (costInfos != null) {
				int costInfoLength = costInfos.length;
				
				//logger.debug("find " + costInfoLength + " CostInfo Object...");
				if (costInfoLength > 0) {
					int for_next = next_yb / costInfoLength;
					for (int i = 0; i < costInfoLength; i++) {
						TCComponent costInfo = costInfos[i];
						if (costInfo.getType().equals("JCI6_CostInfo")) {
							String jci6_CPT = costInfo.getProperty("jci6_CPT");
							//if (jci6_CPT.equals(reg.getString("jci6_CPT1.NAME")) || jci6_CPT.equals(reg.getString("jci6_CPT2.NAME")) || jci6_CPT.equals(reg.getString("jci6_CPT3.NAME"))) {
							if (jci6_CPT.equals(reg.getString("jci6_CPT1.NAME")) || jci6_CPT.equals(reg.getString("jci6_CPT2.NAME"))){
								VecCostInfo.add(costInfos[i]);
							}
						}
						thread.setProgressValue(thread.getProgressValue() + for_next);
					}
				}
				thread.setProgressValue(next_yb + a / 100);
				fillOriginalDataToTable(thread, next_yb);
			}
			thread.setProgressValue(a);
			//thread.close();
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		System.out.println("fillOriginalData(ProgressBarThread thread, int a) ------end");
	}

	
	Vector<TCComponent> labourMemoTableVec=new Vector<TCComponent>();
	Vector<TCComponent> nonLabourMemoActualTableVec=new Vector<TCComponent>();
	Vector<TCComponent> nonLabourMemoPRTableVec=new Vector<TCComponent>();
	
	// add by wuh 2014-5-14
	public void fillOriginalDataToTable(ProgressBarThread thread, int next_yb) {
		System.out.println("fillOriginalDataToTable(ProgressBarThread thread, int next_yb) ------start");
		
		int costInfoSize = VecCostInfo.size();
		TCComponent[] costInfos = new TCComponent[costInfoSize];
		costInfos = VecCostInfo.toArray(costInfos);
//		String[] proNames = new String[] { "jci6_CPT", "jci6_Division", "jci6_User", "jci6_RateLevel", "jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
//		try {
//			TcPropService.getProperties(session, costInfos, proNames);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}

		int for_val = next_yb;
		if (costInfoSize > 0) {
			for_val = next_yb / costInfoSize;
		}
		for (int i = 0; i < costInfoSize; i++) {
			TCComponent costInfo = VecCostInfo.get(i);
			String[] propertyNames = new String[] { "jci6_CPT", "jci6_CostType" };
			TCProperty[] propertyValues = null;
			try {
				propertyValues = costInfo.getTCProperties(propertyNames);
			} catch (TCException e) {
				e.printStackTrace();
			}
			String cpt = propertyValues[0].getDisplayValue();
			String costType = propertyValues[1].getDisplayValue();
			
			if (costType.equals(reg.getString("jci6_CostType1.NAME")) || costType.equals(reg.getString("jci6_CostType2.NAME")) || costType.equals(reg.getString("jci6_CostType3.NAME")) || costType.equals(reg.getString("jci6_CostType4.NAME")) || costType.equals(reg.getString("jci6_CostType5.NAME"))) {
				//labourMemoTable.addRow(costInfo);
				labourMemoTableVec.add(costInfo);
			} else {
				//System.out.println("cpt--->"+cpt+"  costInfo:"+costInfo);
				if (cpt.equals(reg.getString("jci6_CPT1.NAME"))) {
					//nonLabourMemoActualTable.addRow(costInfo);
					nonLabourMemoActualTableVec.add(costInfo);
				} else if (cpt.equals(reg.getString("jci6_CPT2.NAME"))) {
					//nonLabourMemoPRTable.addRow(costInfo);
					nonLabourMemoPRTableVec.add(costInfo);
				}else if(cpt.equals(reg.getString("jci6_CPT3.NAME"))){
					//nonLabourMemoPOTable.addRow(costInfo);
				}
			}
			thread.setProgressValue(thread.getProgressValue() + for_val);
		}
		
		//System.out.println("fillOriginalDataToTable(ProgressBarThread thread, int next_yb) ------end");
	}

	/**
	 * 把得到的原始数据加载到人工成本和非人工成本的对应的表中
	 * 
	 * @throws TCException
	 */
	public void fillOriginalDataToTable() {
		//System.out.println("fillOriginalDataToTable() ------start");
		
		/*int costInfoSize = VecCostInfo.size();
		TCComponent[] costInfos = new TCComponent[costInfoSize];
		costInfos = VecCostInfo.toArray(costInfos);
		String[] proNames = new String[] { "jci6_CPT", "jci6_Division", "jci6_User", "jci6_RateLevel", "jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
		try {
			TcPropService.getProperties(session, costInfos, proNames);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < costInfoSize; i++) {
			TCComponent costInfo = VecCostInfo.get(i);
			String[] propertyNames = new String[] { "jci6_CPT", "jci6_CostType" };
			TCProperty[] propertyValues = null;
			try {
				propertyValues = costInfo.getTCProperties(propertyNames);
			} catch (TCException e) {
				e.printStackTrace();
			}
			String cpt = propertyValues[0].getDisplayValue();
			String costType = propertyValues[1].getDisplayValue();
			if (costType.equals(reg.getString("jci6_CostType1.NAME")) || costType.equals(reg.getString("jci6_CostType2.NAME")) || costType.equals(reg.getString("jci6_CostType3.NAME")) || costType.equals(reg.getString("jci6_CostType4.NAME")) || costType.equals(reg.getString("jci6_CostType5.NAME"))) {
				labourMemoTable.addRow(costInfo);
			} else {
				if (cpt.equals(reg.getString("jci6_CPT1.NAME"))) {
					nonLabourMemoActualTable.addRow(costInfo);
				} else if (cpt.equals(reg.getString("jci6_CPT2.NAME"))) {
					nonLabourMemoPRTable.addRow(costInfo);
				}
			}
		}*/
		//System.out.println("fillOriginalDataToTable() ------end");
		for(int a=0;a<labourMemoTableVec.size();a++){
			labourMemoTable.addRow(labourMemoTableVec.get(a));
		}
		for(int a=0;a<nonLabourMemoActualTableVec.size();a++){
			nonLabourMemoActualTable.addRow(nonLabourMemoActualTableVec.get(a));
		}
		for(int a=0;a<nonLabourMemoPRTableVec.size();a++){
			nonLabourMemoPRTable.addRow(nonLabourMemoPRTableVec.get(a));
		}
		
	}
	
	
	/**
	 * 刚开始加载界面时候填充的原始数据
	 */
	public void fillOriginalData() {
		System.out.println("fillOriginalData(1) ------start");
		//summaryTable.clear();
		//summaryPRTable.clear();
		//System.out.println("clear before summaryTable row count is " + summaryTable.getRowCount());
		//labourMemoTable.clear();
		//nonLabourMemoPRTable.clear();
		//nonLabourMemoActualTable.clear();
		
		summaryTable.removeAllRows();
		summaryPRTable.removeAllRows();
		//System.out.println("clear before summaryTable row count is " + summaryTable.getRowCount());
		labourMemoTable.removeAllRows();
		nonLabourMemoPRTable.removeAllRows();
		nonLabourMemoActualTable.removeAllRows();		
		
		try {
			TCComponent[] costInfos = null;
			if (programInfoItem != null) {
				summaryTable.addRow(programInfoItem);
				summaryPRTable.addRow(programInfoItem);
				//programInfoItem.refresh();
				//costInfos = programInfoItem.getRelatedComponents("IMAN_external_object_link");
			}
//			if (costInfos != null) {
//				int costInfoLength = costInfos.length;
//				VecCostInfo.clear();
				//logger.debug("find " + costInfoLength + " CostInfo Object...");
//				if (costInfoLength > 0) {
//					for (int i = 0; i < costInfoLength; i++) {
//						TCComponent costInfo = costInfos[i];
//						if (costInfo.getType().equals("JCI6_CostInfo")) {
//							String jci6_CPT = costInfo.getProperty("jci6_CPT");
//							if (jci6_CPT.equals(reg.getString("jci6_CPT1.NAME")) || jci6_CPT.equals(reg.getString("jci6_CPT2.NAME"))) {
//								VecCostInfo.add(costInfos[i]);
//							}
//						}
//					}
//				}
				fillOriginalDataToTable();
//			}
			
//			refreshTable(summaryTable, table_pr_titleSum);
//			refreshTable(summaryPRTable, table_pr_titleSum);
//			refreshTable(labourMemoTable, table_titleLab);
//			refreshTable(nonLabourMemoPRTable, table_titleNon);
//			refreshTable(nonLabourMemoActualTable, table_titleNon);
			
			System.out.println("VecCostInfo.size()---->"+VecCostInfo.size());
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("fillOriginalData(1) ------end");
	}
	
	public void sotosfillOriginalDataToTable(IProgressMonitor monitor) {
		System.out.println("开始移除数据");
		//先清空数据
		summaryTable.removeAllRows();
		summaryPRTable.removeAllRows();
		labourMemoTable.removeAllRows();
		nonLabourMemoPRTable.removeAllRows();
		nonLabourMemoActualTable.removeAllRows();
		nonLabourMemoPOTable.removeAllRows();
		if (programInfoItem != null) {
			summaryTable.addRow(programInfoItem);
			summaryPRTable.addRow(programInfoItem);
			summaryPOTable.addRow(programInfoItem);
		}
		//填充数据
		System.out.println("开始填充数据");
		int costInfoSize = VecCostInfo.size();
		System.out.println("VecCostInfo size 为" + costInfoSize);
		TCComponent[] costInfos = new TCComponent[costInfoSize];
		costInfos = VecCostInfo.toArray(costInfos);
		String[] proNames = new String[] { "jci6_CPT", "jci6_Division", "jci6_User", "jci6_RateLevel", "jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
		try {
			TcPropService.getProperties(session, costInfos, proNames);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (int i = 0; i < costInfoSize; i++) {
			TCComponent costInfo = VecCostInfo.get(i);
			String[] propertyNames = new String[] { "jci6_CPT", "jci6_CostType" };
			TCProperty[] propertyValues = null;
			try {
				propertyValues = costInfo.getTCProperties(propertyNames);
			} catch (TCException e) {
				e.printStackTrace();
			}
			String cpt = propertyValues[0].getDisplayValue();
			String costType = propertyValues[1].getDisplayValue();
			
			if (costType.equals(reg.getString("jci6_CostType1.NAME")) || costType.equals(reg.getString("jci6_CostType2.NAME")) || costType.equals(reg.getString("jci6_CostType3.NAME")) || costType.equals(reg.getString("jci6_CostType4.NAME")) || costType.equals(reg.getString("jci6_CostType5.NAME"))) {
				labourMemoTable.addRow(costInfo);
				double a=(i*1.0/VecCostInfo.size())*100.0;
				String sum=a+"0";
				String t1=cutString(sum,2);
				monitor.setTaskName("complete... "+t1+"%");// 提示信息
                monitor.worked(1);// 进度条前进一步
			} else {
				//System.out.println("cpt--->"+cpt+"  costInfo:"+costInfo);
				if (cpt.equals(reg.getString("jci6_CPT1.NAME"))) {
					nonLabourMemoActualTable.addRow(costInfo);
					double a=(i*1.0/VecCostInfo.size())*100.0;
					String sum=a+"0";
					String t1=cutString(sum,2);
					monitor.setTaskName("complete... "+t1+"%");// 提示信息
	                monitor.worked(1);// 进度条前进一步
				} else if (cpt.equals(reg.getString("jci6_CPT2.NAME"))) {
					nonLabourMemoPRTable.addRow(costInfo);
					double a=(i*1.0/VecCostInfo.size())*100.0;
					String sum=a+"0";
					String t1=cutString(sum,2);
					monitor.setTaskName("complete... "+t1+"%");// 提示信息
	                monitor.worked(1);// 进度条前进一步
				}else if(cpt.equals(reg.getString("jci6_CPT3.NAME"))){
					//nonLabourMemoPOTable.addRow(costInfo);
				}
			}
		}
	}

	// 设置表头背景颜色，并移除表头鼠标监听
	// private void setTableHeaderColor(TCTable table, int columnIndex, Color c)
	// {
	// JTableHeader header = table.getTableHeader();
	// TableColumn column = header.getColumnModel().getColumn(columnIndex);
	// DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
	// cellRenderer.setBackground(c);
	// column.setHeaderRenderer(cellRenderer);
	// MouseListener[] mouseListeners = header.getMouseListeners();
	// //System.out.println("mouseListeners length--->"+mouseListeners.length);
	// for(int i=0;i<mouseListeners.length;i++){
	// header.removeMouseListener(mouseListeners[i]);
	// }
	// }
	
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
