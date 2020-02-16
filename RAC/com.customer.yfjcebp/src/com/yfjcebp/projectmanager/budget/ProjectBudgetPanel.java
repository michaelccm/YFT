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
#	2013-3-11	liujm  	Ini		SP-RICH-FUN-02.�鿴Ԥ��									   
#=============================================================================
 */
package com.yfjcebp.projectmanager.budget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

//import org.apache.log4j.Logger;






import org.eclipse.core.runtime.IProgressMonitor;

import com.teamcenter.rac.aif.common.AIFTableLine;
import com.teamcenter.rac.aif.common.AIFTableModel;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.commands.open.OpenCommand;
import com.teamcenter.rac.commands.properties.PropertiesCommand;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.common.TCTableModel;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.tcservices.TcPropService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.SplitPane;

/**
 * @author liujm
 */
public class ProjectBudgetPanel extends JPanel {

	/**
	 * 
	 */
	private JPopupMenu pm=null;
	private JMenuItem mi2=null;
	private JMenuItem mi1=null;
	private JMenuItem mi3=null;
	private TCComponent showCompnont_all=null;
	
	private static final long serialVersionUID = 1L;
	private String widthStr = "25";

	//private static Logger logger = Logger.getLogger(ProjectBudgetPanel.class);

	protected TCSession session;
	

	// Summary �ɱ�Ԥ�����
	private JScrollPane scrollPaneSum;
	private TCTable summaryTable;
	private String[] table_titleSum;

	// Labour Memo �˹��ɱ���ϸ
	private JScrollPane scrollPaneLab;
	private TCTable labourMemoTable;
	private String[] table_titleLab;

	// Non Labour Memo ���˹��ɱ���ϸ
	private JScrollPane scrollPaneNon;

	private TCTable nonLabourMemoTable;
	private String[] table_titleNon;

	private TCComponent programInfo;
	
	//add by wuwei
	private TCComponentItemRevision programInfoRev1;

	// ��ż��ؽ����ʱ���ԭʼ��ݻ����´��������
	public Vector<TCComponent> VecCostInfo = new Vector<TCComponent>();
	
	//private Registry reg;
	private static final String BURFLR_NAME = "com.yfjcebp.projectmanager.budget.budget_locale";
	public static final ResourceBundle reg = ResourceBundle
			.getBundle(BURFLR_NAME);
	
	public Vector<TCComponent> getVecCostInfo() {
		return VecCostInfo;
	}

	public void setVecCostInfo(Vector<TCComponent> vecCostInfo) {
		VecCostInfo = vecCostInfo;
	}

	public ProjectBudgetPanel() {
		super(true);
		
		session = RACUIUtil.getTCSession();

		initializePanel();
		
		//add by wuwei
		summaryTable.removeAllRows();
		labourMemoTable.removeAllRows();
		nonLabourMemoTable.removeAllRows();
	}

	public ProjectBudgetPanel(TCSession tcsession) {
		super(true);
		System.out.println("ProjectBudgetPanel initial");
		session = null;
		session = tcsession;
		//reg = Registry.getRegistry(this);
		
		initializePanel();
		
		//add by wuwei
		summaryTable.removeAllRows();
		labourMemoTable.removeAllRows();
		nonLabourMemoTable.removeAllRows();
	}

	public ProjectBudgetPanel(TCComponent nodeComponent) {
		super(true);
		this.programInfo=nodeComponent;
	}

	public TCComponent getProgramInfo() {
		return programInfo;
	}
	
	public void setProgramInfo(TCComponent programInfo){
		this.programInfo=programInfo;
	}

	public void setProgramInfoComponent(TCComponent nodeComponent) {
		if (nodeComponent != null) {
			programInfo = null;
			if (nodeComponent instanceof TCComponentProject) {
				System.out.println("tree select TCComponentProject");
				TCComponentProject currentproject = (TCComponentProject) nodeComponent;
				try {
					TCComponent[] preferred_Items = currentproject
							.getReferenceListProperty("TC_Program_Preferred_Items");
					int project_dataLength = preferred_Items.length;
					System.out
							.println("TC_Program_Preferred_Items   length--->"+ project_dataLength);
									
					for (int i = 0; i < project_dataLength; i++) {
						TCComponent tccomponent = preferred_Items[i];
						if (tccomponent.getType().equals("JCI6_ProgramInfo")) {
							TCComponentItem programInfoItem = (TCComponentItem) tccomponent;
							programInfo = programInfoItem;
							programInfoRev1= programInfoItem.getLatestItemRevision();
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			} else if (nodeComponent instanceof TCComponentItem) {
				System.out.println("tree select TCComponentItem");
				if (nodeComponent.getType().equals("JCI6_ProgramInfo")) {
					TCComponentItem programInfoItem = (TCComponentItem) nodeComponent;
					programInfo = programInfoItem;
					try {
						programInfoRev1= programInfoItem.getLatestItemRevision();
					} catch (TCException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				
			} else if (nodeComponent instanceof TCComponentItemRevision) {
				System.out.println("tree select TCComponentItemRevision");
				if (nodeComponent.getType().equals("JCI6_ProgramInfoRevision")) {
					programInfo= (TCComponentItemRevision) nodeComponent;
					programInfoRev1=(TCComponentItemRevision) nodeComponent;
				} 
			} 
		}
		
		System.out.println("ww programInfo:"+programInfo);
		System.out.println("ww programInfoRev1:"+programInfoRev1);
	}

	/**
	 * panel
	 */
	private void initializePanel() {
		System.out.println("ProjectBudgetPanel   initializePanel--start");
		// tyl  2015/05/05
		// table_titleSum = new String[] { "jci6_budget_total_cost",
		// "jci6_budget_labor_hc", "jci6_budget_labor_hours",
		// "jci6_budget_labor_cost", "jci6_budget_sample",
		// "jci6_budget_sample_ext", "jci6_budget_sample_int",
		// "jci6_budget_sample_pv", "jci6_budget_test",
		// "jci6_budget_test_dv","jci6_budget_test_pv",
		// "jci6_budget_tool","jci6_budget_other_freight",
		// "jci6_budget_other_support", "jci6_budget_other_travel" };
		table_titleSum = new String[] { "jci6_budget_total_cost", "jci6_budget_labor_hc", "jci6_budget_labor_hours", "jci6_budget_labor_cost", "jci6_budget_sample", "jci6_budget_sample_ext", "jci6_budget_sample_int", "jci6_budget_sample_pv", "jci6_budget_sample_mnjt", "jci6_budget_test", "jci6_budget_test_dv",
				"jci6_budget_test_pv","jci6_budget_test_wfsy","jci6_budget_test_sygz", "jci6_budget_tool", "jci6_budget_other_freight", "jci6_budget_other_support", "jci6_budget_other_travel" };
	
//		String[] revProNamesWidth = new String[] { widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr };
		String[] revProNamesWidth = new String[] { widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr, widthStr };
		
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
		
		
		// tyl  2015/05/05
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
		
		summaryTable.setDragEnabled(true);
		
		//modify by wuwei ----JCI6_ProgramInfo
		summaryTable.setColumnNames(session, table_titleSum, "JCI6_ProgramInfo");
		// summaryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		summaryTable.setColumnWidths(revProNamesWidth);
		summaryTable.setPreferredScrollableViewportSize(new Dimension(1000, 40));
		scrollPaneSum = new JScrollPane(summaryTable,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneSum.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("SummaryTable.LABEL")));
		
		//JPanel paneSummary = new JPanel(new BorderLayout());
		//paneSummary.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("SummaryTable.LABEL")));
		//paneSummary.add(BorderLayout.CENTER, scrollPaneSum);

		String[] costInfoProNames = new String[] { "jci6_Division", "jci6_RateLevel", "jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
		table_titleLab = new String[16];
		table_titleNon = new String[15];
		for (int i = 0; i < 17; i++) {
			String proName = costInfoProNames[i];
			if (i <= 1) {
				table_titleLab[i] = proName;
			} else {
				if (i > 2) {
					table_titleLab[i - 1] = proName;
				}
				table_titleNon[i - 2] = proName;
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
		//scrollPaneLab.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("LabourMemoTable.LABEL")));
		
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
		//scrollPaneNon.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("NonLabourMemoTable.LABEL")));
		
		JPanel paneNon = new JPanel(new BorderLayout());
		paneNon.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), reg.getString("NonLabourMemoTable.LABEL")));
		paneNon.add(BorderLayout.CENTER, scrollPaneNon);

		setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());
		//mainPanel.add(BorderLayout.NORTH, paneSummary); 
		mainPanel.add(BorderLayout.NORTH, scrollPaneSum);
		SplitPane splitpane = new SplitPane(1);
		splitpane.setDividerSize(8);
		
		splitpane.setTopComponent(paneLab); 
		splitpane.setBottomComponent(paneNon);
		//splitpane.setTopComponent(scrollPaneLab);
		//splitpane.setTopComponent(scrollPaneNon);
		
		mainPanel.add(BorderLayout.CENTER, splitpane);
		
		JScrollPane mainScrollPane = new JScrollPane(mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		add("Center", mainScrollPane);
		
		System.out.println("ProjectBudgetPanel   initializePanel--end");
	}

	// add by wuh 2014-5-14
	public void fillOriginalData(ProgressBarThread progressBarThread, int amount) {
		// timer.start();
		System.out.println("fillOriginalData(ProgressBarThread thread, int amount)-------start");

		// modify by wuh 2014-5-14
		summaryTable.removeAllRows();
		labourMemoTable.removeAllRows();
		nonLabourMemoTable.removeAllRows();

		System.out.println("programInfo-->"+programInfo);
		try {
			
			if (programInfo != null) {
				//if (programInfo instanceof TCComponentItemRevision) {
				//	TCComponentItemRevision rev=(TCComponentItemRevision)programInfo;
				//	programInfo=rev.getItem();
				//}
				//try {
				//	TcPropService.getProperties(session,new TCComponent[]{ programInfo}, table_titleSum);
				//} catch (Exception e1) {
				//	e1.printStackTrace();
				//}
				
				
				summaryTable.addRow(programInfo);
				//summaryTable.updateUI();
				// summaryTable.validate();
				VecCostInfo.clear();
				TCComponent[] costInfos = null;
				if (programInfo instanceof TCComponentItem) {
					TCComponentItem programInfoItem = (TCComponentItem) programInfo;
					programInfoItem.refresh();
					TCComponent[] revisions = programInfoItem.getReferenceListProperty("revision_list");
					int revNum = revisions.length;
					System.out.println("revNum:" + revNum);
					int a = amount / 2 / revNum;
					for (int i = 0; i < revNum; i++) {
						revisions[i].refresh();
						if (revisions[i].getTCProperty("jci6_BudgetState").getStringValue().equals("State4")) {
							costInfos = revisions[i].getRelatedComponents("IMAN_external_object_link");
							System.out.println("costInfos.length-->" + costInfos.length);
							addDataToList(costInfos, progressBarThread, a);
						}
					}

				} else {
					programInfo.refresh();
					costInfos = programInfo.getRelatedComponents("IMAN_external_object_link");
					addDataToList(costInfos, progressBarThread, 500000);
				}
				
				
				progressBarThread.setProgressValue(amount / 2);
				// 50%
				if (VecCostInfo.size() > 0) {
					System.out.println("VecCostInfo.size()>0");
					fillOriginalDataToTable(progressBarThread, amount / 2);
				}
				progressBarThread.setProgressValue(amount);
				
			}
			
			//thread.close();
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		System.out.println("fillOriginalData(ProgressBarThread thread, int amount)-------end");
	}

	// add by wuh 2014-5-14
	public void addDataToList(TCComponent[] costInfos, ProgressBarThread thread, int a) {
		if (costInfos != null) {
			int costInfoLength = costInfos.length;
			System.out.println("costInfoLength=" + costInfoLength);
			//logger.debug("find " + costInfoLength + " CostInfo Object...");
			if (costInfoLength > 0) {
				int c = a / costInfoLength;
				for (int i = 0; i < costInfoLength; i++) {
					TCComponent costInfo = costInfos[i];
					if (costInfo.getType().equals("JCI6_CostInfo")) {
						try {
							if (costInfo.getProperty("jci6_CPT").equals(reg.getString("jci6_CPT.NAME"))) {
								VecCostInfo.add(costInfos[i]);
							}
						} catch (TCException e) {
							e.printStackTrace();
						}
					}
					thread.setProgressValue(thread.getProgressValue() + c);
					// System.out.println("�����"+thread.getProgressValue());
				}

			}
		}
	}

	// add by wuh 2014-5-14
	public void fillOriginalDataToTable(ProgressBarThread thread, int yb) {
		System.out.println("fillOriginalDataToTable()-------start");
		
		int costInfoSize = VecCostInfo.size();
		System.out.println("costInfoSize--->" + costInfoSize);
		TCComponent[] costInfos = new TCComponent[costInfoSize];
		costInfos = VecCostInfo.toArray(costInfos);
		String[] proNames = new String[] { "jci6_Division", "jci6_RateLevel", "jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };
		try {
			TcPropService.getProperties(session, costInfos, proNames);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		int a = yb / costInfoSize;
		for (int i = 0; i < costInfoSize; i++) {
			TCComponent costInfo = VecCostInfo.get(i);
			String costType = "";
			try {
				costType = costInfo.getTCProperty("jci6_CostType").getDisplayValue();
			} catch (TCException e) {
				e.printStackTrace();
			}
			if (costType.equals(reg.getString("jci6_CostType1.NAME")) || costType.equals(reg.getString("jci6_CostType2.NAME")) || costType.equals(reg.getString("jci6_CostType3.NAME")) || costType.equals(reg.getString("jci6_CostType4.NAME"))) {
				labourMemoTable.addRow(costInfo);
			} else {
				nonLabourMemoTable.addRow(costInfo);
			}

			thread.setProgressValue(thread.getProgressValue() + a);
			// System.out.println("�����"+thread.getProgressValue());
		}

		System.out.println("fillOriginalDataToTable()-------end");
	}

	/**
	 * �տ�ʼ���ؽ���ʱ������ԭʼ���
	 */
	public void fillOriginalData() {
		//summaryTable.clear();
		//labourMemoTable.clear();
		//nonLabourMemoTable.clear();
		
		//add by wuwei
		summaryTable.removeAllRows();
		labourMemoTable.removeAllRows();
		nonLabourMemoTable.removeAllRows();
		
		try {
			System.out.println("-----fillOriginalData() start    programInfo:"+programInfo+"  type:"+programInfo.getType() );
			if (programInfo != null) {
				//if (programInfo instanceof TCComponentItemRevision) {
					//TCComponentItemRevision rev=(TCComponentItemRevision)programInfo;
					//programInfo=rev.getItem();
				//}
//				try {
//					TcPropService.getProperties(session,new TCComponent[]{ programInfoRev1}, table_titleSum);
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
				
				
				summaryTable.addRow(programInfo);
				//refreshTable(summaryTable,table_titleSum);
				summaryTable.updateUI();
				
				VecCostInfo.clear();
				TCComponent[] costInfos = null;
				if (programInfo instanceof TCComponentItem) {
					TCComponentItem programInfoItem = (TCComponentItem) programInfo;
					TCComponent[] revisions = programInfoItem.getReferenceListProperty("revision_list");
					int revNum = revisions.length;
					for (int i = 0; i < revNum; i++) {
						if (revisions[i].getTCProperty("jci6_BudgetState").getStringValue().equals("State4")) {
							System.out.println("revisions[i]:"+revisions[i]+"  jci6_BudgetState-->State4");
							costInfos = revisions[i].getRelatedComponents("IMAN_external_object_link");
							addDataToList(costInfos);
						}
					}
				} else {
					costInfos = programInfo.getRelatedComponents("IMAN_external_object_link");
					addDataToList(costInfos);
				}
			}
			
			System.out.println("VecCostInfo.size()-->"+VecCostInfo.size());
//			if (VecCostInfo.size() > 0) {
//				fillOriginalDataToTable();
//			}
			
			//refreshTable(labourMemoTable,table_titleLab);
			//refreshTable(nonLabourMemoTable,table_titleNon);
		} catch (TCException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("-----fillOriginalData() end    programInfo:"+programInfo+"  type:"+programInfo.getType() );
	}


	public void addDataToList(TCComponent[] costInfos) {
		if (costInfos != null) {
			int costInfoLength = costInfos.length;
			System.out.println("costInfoLength=" + costInfoLength);
			//logger.debug("find " + costInfoLength + " CostInfo Object...");
			if (costInfoLength > 0) {
				for (int i = 0; i < costInfoLength; i++) {
					TCComponent costInfo = costInfos[i];
					if (costInfo.getType().equals("JCI6_CostInfo")) {
						try {
							String key1 = reg.getString("jci6_CPT.NAME");
							//System.out.println("jci6_CPT.NAME--->"+key1);
							if (costInfo.getProperty("jci6_CPT").equals(key1)) {
								VecCostInfo.add(costInfos[i]);
							}
						} catch (TCException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * �ѵõ���ԭʼ��ݼ��ص��˹��ɱ��ͷ��˹��ɱ��Ķ�Ӧ�ı���
	 */
	public void fillOriginalDataToTable(IProgressMonitor monitor) {
		if (programInfo != null) {
			summaryTable.addRow(programInfo);
		}
	
		int costInfoSize = VecCostInfo.size();
		
		System.out.println("-----fillOriginalDataToTable() start  -------");
		
		//TCComponent[] costInfos = new TCComponent[costInfoSize];
		//costInfos = VecCostInfo.toArray(costInfos);
		//String[] proNames = new String[] { "jci6_Division", "jci6_RateLevel", "jci6_CostType", "jci6_Unit", "jci6_Year", "jci6_Jan", "jci6_Feb", "jci6_Mar", "jci6_Apr", "jci6_May", "jci6_Jun", "jci6_Jul", "jci6_Aug", "jci6_Sep", "jci6_Oct", "jci6_Nov", "jci6_Dec" };

//		try {
//			TcPropService.getProperties(session, costInfos, proNames);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		for (int i = 0; i < costInfoSize; i++) {
			TCComponent costInfo = VecCostInfo.get(i);
			String costType = "";
			try {
				costType = costInfo.getTCProperty("jci6_CostType").getDisplayValue();
			} catch (TCException e) {
				e.printStackTrace();
			}
			if (costType.equals(reg.getString("jci6_CostType1.NAME")) || costType.equals(reg.getString("jci6_CostType2.NAME")) || costType.equals(reg.getString("jci6_CostType3.NAME")) || costType.equals(reg.getString("jci6_CostType4.NAME"))) {

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
		System.out.println("-----fillOriginalDataToTable() end  -------");
	}
	
	
	
	
	//modify by wuwei
	private void refreshTable(TCTable myTable2,String[] propertyArray) throws Exception {
		// TODO Auto-generated method stub
		
		AIFTableModel dataModel = myTable2.dataModel;
		
		String[] newProps=new String[propertyArray.length];
		List<AIFTableLine> allData = dataModel.getAllData();
		String[][] arrayOfString = new String[allData.size()][myTable2.getColumnCount()];
		System.out.println("allData.size()--->"+allData.size());
		
		for(int i=0;i<allData.size();i++){
			AIFTableLine aifTableLine = allData.get(i);
			InterfaceAIFComponent component2 = aifTableLine.getComponent();
		
			System.out.println("ww aifTableLine:"+component2);
			for(int j=0;j<propertyArray.length;j++){
				String[] splits = propertyArray[j].split("\\.",-1);
				System.out.println("splits[0]-->"+splits[0]+"  splits[1]:"+splits[1] );
				newProps[j]=splits[1];
				arrayOfString[i][j]=component2.getProperty(splits[1]);
				System.out.println("value=="+arrayOfString[i][j]);
				
				if("".equals(arrayOfString[i][j].trim())){
					arrayOfString[i][j]="0.0";
				}
				
				if(isDouble(arrayOfString[i][j])){
					BigDecimal bd = new BigDecimal(arrayOfString[i][j]);
					arrayOfString[i][j] = bd.stripTrailingZeros().toPlainString();
				}
				
			}
			
		}
		
		myTable2.clear();
		
		//myTable2.changeDataModel(arrayOfString, newProps);
		//myTable2.dataModel= new AIFTableModel(arrayOfString);
		TCTableModel myDataModel= new TCTableModel(arrayOfString);
		System.out.println("myDataModel.getAllData()--->"+myDataModel.getAllData().size());
		myTable2.setModel(myDataModel);
		List<AIFTableLine> allData2 = myDataModel.getAllData();
		//myTable2.addRow(paramObject)
		
		
		System.out.println("allData2.size()--->"+allData2.size());
		
	}
	
	boolean isDouble(String str)
	{
	try
	{
	Double.parseDouble(str);
	return true;
	}
	catch(NumberFormatException ex){}
	return false;
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
