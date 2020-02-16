//package com.teamcenter.custwork.workmanager;
//
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Font;
//
//import java.awt.FlowLayout;
//import java.awt.Frame;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.InputMethodEvent;
//import java.awt.event.InputMethodListener;
//import java.awt.Toolkit;
//import java.awt.event.ItemListener;
//import java.awt.event.ItemEvent;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//
//import javax.swing.BorderFactory;
//import javax.swing.DefaultCellEditor;
//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JLabel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//
//
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextField;
//import javax.swing.JToolBar;
//import javax.swing.ListSelectionModel;
//import javax.swing.border.TitledBorder;
//import javax.swing.event.AncestorEvent;
//import javax.swing.event.AncestorListener;
//import javax.swing.event.TableModelEvent;
//import javax.swing.event.TableModelListener;
//import javax.swing.table.TableColumn;
//
//import javax.swing.JFormattedTextField;
//import java.text.NumberFormat;
//
//import javax.swing.border.Border;
//import javax.swing.border.LineBorder;
//import javax.swing.border.BevelBorder;
//
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//
//
//
//import com.d9.services.rac.work.ManHourManageServiceService;
//import com.d9.services.rac.work._2018_06.ManHourManageService;
//import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourInfo;
//import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourMonthTmp;
//import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourEntry;
//import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourEntrySet;
//import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourProgram;
//import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourBillType;
//
//import com.teamcenter.custwork.CacheImage;
//import com.teamcenter.custwork.query.QueryProgressDialog;
//import com.teamcenter.custwork.saves.SaveProgressDialog;
//import com.teamcenter.custwork.toolkit.BuildComponentFactory;
//import com.teamcenter.custwork.toolkit.DateToolkit;
//import com.teamcenter.custwork.toolkit.IWorkManagerTookit;
//import com.teamcenter.custwork.toolkit.component.NumOnlyDocument;
//import com.teamcenter.custwork.toolkit.component.WDatePanel;
//import com.teamcenter.custwork.toolkit.table.GroupHeader;
//import com.teamcenter.custwork.toolkit.table.WDefaultTableModel;
//import com.teamcenter.custwork.toolkit.table.WLeftTableCellRenderer;
//import com.teamcenter.custwork.toolkit.table.WRightTableCellRenderer;
//import com.teamcenter.custwork.toolkit.table.WTable;
//import com.teamcenter.custwork.toolkit.table.WorkSheetTable;
//import com.teamcenter.rac.aif.AbstractAIFDialog;
//import com.teamcenter.rac.aifrcp.AIFUtility;
//import com.teamcenter.rac.commands.subscribe.DateTimeButton;
//import com.teamcenter.rac.commands.subscribe.DateTimePanel;
//import com.teamcenter.rac.kernel.TCException;
//import com.teamcenter.rac.kernel.TCPreferenceService;
//import com.teamcenter.rac.kernel.TCSession;
//import com.teamcenter.rac.util.DateButton;
//import com.teamcenter.rac.util.MessageBox;
//import com.teamcenter.rac.util.iTextField;
//
//import com.teamcenter.rac.util.combobox.iComboBox;
//import com.teamcenter.custwork.workmanager.ManHourObject;
//
//import com.teamcenter.custwork.toolkit.table.WComboBoxEditor;
//import com.teamcenter.custwork.saves.SaveProgressDialog;
//
//
//import java.math.BigDecimal;
//
//
//public class WorkManagerDialog_old extends AbstractAIFDialog implements IWorkManagerTookit,TableModelListener
//{	
//   public TCSession session = null;
//	
//   private QueryProgressDialog queryDialog = null;
//
//   private String logPath = null;
//
//   private String[] table_column_names = null;
//     
//   private HashMap queryMap = null;
//    
//   private WTable tctable;
//
//   private WDefaultTableModel tablemode;
//
//   private JScrollPane scrollPane;
//
//   private Object[] objLastRow = null;
//	
//   private Object[] objIninRow = null;
//	
//   private String[] timeSheetTypes = null;
//
//   private HashMap daysWeekMap = null;
//
//   private JPanel palTable;
//
//   private WTable rightTable;
//
//   private WTable leftTable;
//
//   private WorkSheetTable wTable;
//
//   private String[] newCloumn;
//
//   private WDatePanel datePanel;
//
//
//   public ManHourInfo mhInfo_ = null;
//   int totalNormalHoursAllowed_ = 0;
//   public HashMap <String, ArrayList<String>> pMap_ = null;
//   private HashMap <String, ManHourBillType> btMap_ = null;  
//   private HashMap <String, String> ttDvalAndTskTypeMap_ = null;
//   private HashMap <String, String> pNameMap_ = null; 
//
//   private JButton assignBtt_  = null;
//   private JButton saveAllBtt_ = null;
//   private JButton saveBtt_ = null;
//   private JButton reviseBtt_ = null;
//   private JButton wAddBtt_ = null;
//   private JButton wRemoveBtt_ = null;
//   private JButton nfPal_ = null;
//   private JButton lrPal_ = null;   
//   private JButton reloadBtt_ = null;
//   
//   private JButton dciBtt_ = null;
//   
//
//   private JFormattedTextField oTfd = null;
//   private JFormattedTextField wTfd = null;
//   private JFormattedTextField hTfd = null; 
//   private JFormattedTextField nTfd = null;
//
//   private JComboBox  prgCbox_ = null;
//   private JComboBox  tskCbox_ = null;
//   private boolean  isInitMHD_ = false;     
//
//
//   ManHourEntry [] tstMheInSet_ = null;
//   ArrayList <ManHourEntry> tstMheOutList_ = null;
//
//
//   public WorkManagerDialog_old(Frame frame, boolean flag) throws TCException 
//   {
//      super(frame, flag);
//	
//      table_column_names = new String[]{"No","Program","Task Type","N","O","W","H"};
//      queryMap = new HashMap();
//      session = (TCSession) AIFUtility.getDefaultSession();
//               
//      try
//      {
//         mhInfo_= WorkmanagerOperation.loadManHourInfo(session, null, null);
//      }
//      catch (Exception e)
//      {
//         e.printStackTrace();
//      }
//
//      initUI();
//		
//      initData();
//		
//      initManHourData();
//
//      doSummarize();
//
//      int x = (Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width)/2;
//      int y = (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height)/2;
//      setLocation(x, y);
//   }
//
//
//   public void initUI() throws TCException
//   {
//      setPersistentDisplay(true);
//      setOptimalDisplay(true);
//      setLayout(new BorderLayout());
//      setPreferredSize(new Dimension(1542,800));
//      ImageIcon image = CacheImage.getINSTANCE().convertibaleImageIcon("ProcessVariable_32.png");		
//		super.setIconImage(image.getImage());
//		setLayout(new BorderLayout());	
//		setTitle("Timesheet");
//
//	    palTable = new JPanel(new BorderLayout());     
//		palTable.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),""));
//		palTable.add(BorderLayout.NORTH,getToolBar());
//		add(BorderLayout.NORTH,getTitlePanel());
//		add(BorderLayout.CENTER,palTable);
//		add(BorderLayout.SOUTH,getBottomPanel());
//		pack();
//		centerToScreen(2.0, 2.0);
//	}
//
//
//        private JPanel getTitlePanel() throws TCException
//	{
//           JPanel palTitle = new JPanel();	
//           palTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
//           palTitle.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),"Information"));
//           palTitle.setBackground(Color.WHITE);
//		
//		reloadBtt_ = BuildComponentFactory.makeNavigationButton(
//				"reload.png", "Load", "Load");
//		reloadBtt_.setBackground(Color.WHITE);
//		reloadBtt_.setBorderPainted(false);
//		reloadBtt_.addActionListener(new ActionListener()
//                {
//
//			@Override
//			public void actionPerformed(ActionEvent e) 
//  		        {
//				reload();
//			}
//
//		});
//		palTitle.add(reloadBtt_);
//		
//
//		datePanel = new WDatePanel(new Date());
//		datePanel.setWorkManager(this);
//		palTitle.add(datePanel);
//		
//
//		palTitle.add( new JLabel(" Normal Hours Allowed:"));
//		nfPal_ = new JButton (new String (Integer.toString(totalNormalHoursAllowed_)));
//		nfPal_.setSize(30, 20);
//		nfPal_.setBackground(Color.GRAY);
//		nfPal_.setBorderPainted(false);
//		palTitle.add(nfPal_);
//		
//		palTitle.add( new JLabel(" Logging rate:"));
//		lrPal_ = new JButton("0.00%");
//		lrPal_.setBorderPainted(false);
//		lrPal_.setBackground(Color.GREEN);
//		palTitle.add(lrPal_);
//		
//		return palTitle;
//	}
//
//
//
//	private JToolBar getToolBar() throws TCException{
//		JToolBar toolBar = new JToolBar();
//		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
//		toolBar.setBackground(Color.WHITE);
//		
//		JPanel pal = new JPanel(new FlowLayout(FlowLayout.LEFT));
//		pal.setBackground(Color.WHITE);
//		JLabel labN = new JLabel("  Normal(");
//		JLabel labN2 = new JLabel("N");
//		JLabel labN3 = new JLabel(")");
//		
//		JLabel labO = null;
//		JLabel labO2 = null;
//		JLabel labO3 = null;
//	
//       	        labO = new JLabel("  Overtime(");
//	        labO2 = new JLabel("O");
//	        labO3 = new JLabel(")");
//       
//		JLabel labW = new JLabel("  Weekend(");
//		JLabel labW2 = new JLabel("W");
//		JLabel labW3 = new JLabel(")");
//		
//		JLabel labH = new JLabel("  Holiday(");
//		JLabel labH2 = new JLabel("H");
//		JLabel labH3 = new JLabel(")");
//		
//		labN2.setOpaque(true);
//                labO2.setOpaque(true);
//		labW2.setOpaque(true);
//		labH2.setOpaque(true);
//					
//                labN2.setBackground(ManHourObject.nColor);
//		labO2.setBackground(ManHourObject.oColor);
//		labW2.setBackground(ManHourObject.wColor);
//		labH2.setBackground(ManHourObject.hColor);
//	
//
//                NumberFormat percentFormat = NumberFormat.getNumberInstance();
//                percentFormat.setMaximumFractionDigits(1);		
//		percentFormat.setMaximumIntegerDigits (2);
//
//		nTfd = new JFormattedTextField(percentFormat); 
//		nTfd.setColumns (4);
//
//		oTfd = new JFormattedTextField(percentFormat); 
//		oTfd.setColumns (4);
//
//		wTfd = new JFormattedTextField(percentFormat); 
//		wTfd.setColumns (4);
//
//		hTfd = new JFormattedTextField(percentFormat); 
//		hTfd.setColumns (4);
//		
//		pal.add(labN);
//		pal.add(labN2);
//		pal.add(labN3);
//		pal.add(nTfd);
//		
//		pal.add(labO);
//		pal.add(labO2);
//		pal.add(labO3);
//		pal.add(oTfd);
//	
//                pal.add(labW);
//		pal.add(labW2);
//		pal.add(labW3);
//		pal.add(wTfd);
//	
//		pal.add(labH);
//		pal.add(labH2);
//		pal.add(labH3);	
//		pal.add(hTfd);
//			
//		pal.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),"Bill Type"));
//
//		assignBtt_ = BuildComponentFactory.makeNavigationButton(null, " Assign ", " Assign ");
//		assignBtt_.setBackground(Color.WHITE);
//		assignBtt_.setBorderPainted(true);
//		assignBtt_.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.GRAY, Color.GRAY));
//		assignBtt_.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent e) 
//	    	        {
//                           doAssign ();
//			}
//		});
//
//		JButton clsBtt = BuildComponentFactory.makeNavigationButton(null, "  Clear  ", "  Clear  ");
//		clsBtt.setBackground(Color.WHITE);
//		clsBtt.setBorderPainted(true);
//		clsBtt.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.GRAY, Color.GRAY));  
//		clsBtt.addActionListener(new ActionListener()
//		{
//	           @Override
//                   public void actionPerformed(ActionEvent e) 
//	    	   {
//                      oTfd.setText (new String (""));
//                      wTfd.setText (new String (""));
//                      hTfd.setText (new String (""));
//                      nTfd.setText (new String (""));
//		   }
//		});
//
//               JButton checkStatusBtt = BuildComponentFactory.makeNavigationButton(null, "Status", "Status");
//               checkStatusBtt.setBackground(ManHourObject.toolButtColor);
//               checkStatusBtt.addActionListener(new ActionListener(){
//
//	          @Override
//                  public void actionPerformed(ActionEvent e) 
//                  {
//                      doCheckStatus();
//                  }
//              });
//             
//
//               dciBtt_ = BuildComponentFactory.makeNavigationButton(null, "Del CostInfo", "Del CostInfo");
//               dciBtt_.setBackground(ManHourObject.toolButtColor);
//               dciBtt_.addActionListener(new ActionListener(){
//
//	          @Override
//                  public void actionPerformed(ActionEvent e) 
//                  {
//                     try
//                     {
//                        dciBtt_.setBackground(Color.YELLOW);
//                        WorkmanagerOperation.delCostInfo (session, "Delete_CostInfo", "Delete");
//                        dciBtt_.setBackground(ManHourObject.toolButtColor);
//                     }
//                     catch (Exception ee)
//                     {
//                        ee.printStackTrace();
//                     }
//                  }
//	      });
//
//
//              JButton idataBtt = BuildComponentFactory.makeNavigationButton(null, "I-Data", "I-Data");
//              idataBtt.setBackground(ManHourObject.toolButtColor);
//              idataBtt.addActionListener(new ActionListener(){
//
//	          @Override
//                  public void actionPerformed(ActionEvent e) 
//                  {
//                      if (tstMheInSet_ != null)
//                      {
//			 String mes = printMHESet (tstMheInSet_);
//			 System.out.println ("In-coming data  -- start\n");
//			 System.out.println (mes);
//			 System.out.println ("\nIn-coming data  -- end");
//		      }
//                  }
//	      });
//
//              
//               JButton odataBtt = BuildComponentFactory.makeNavigationButton(null, "O-Data", "O-Data");
//               odataBtt.setBackground(ManHourObject.toolButtColor);
//               odataBtt.addActionListener(new ActionListener(){
//
//	          @Override
//                  public void actionPerformed(ActionEvent e) 
//                  {
//                      if (tstMheOutList_ != null)
//                      {
//			 String mes = printMHEList (tstMheOutList_);
//			 System.out.println ("Out going data  -- start\n");
//			 System.out.println (mes);
//			 System.out.println ("\nOut-going data  -- end");
//		      }
//                  }
//	      });
//
//               JButton mhinfoBtt = BuildComponentFactory.makeNavigationButton(null, "Mhinfo", "Mhinfo");
//               mhinfoBtt.setBackground(ManHourObject.toolButtColor);
//               mhinfoBtt.addActionListener(new ActionListener(){
//
//	          @Override
//                  public void actionPerformed(ActionEvent e) 
//                  {
//		     String mes = printMhinfo (mhInfo_);
//		     System.out.println ("mhinfo data  -- start\n");
//		     System.out.println (mes);
//		     System.out.println ("\nmhinfo data  -- end");
//                  }
//	      });
//              
//
//
//		JPanel buttPanel = new JPanel(new FlowLayout());
//		buttPanel.setBackground(Color.WHITE);
//		buttPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),""));
//		wAddBtt_ = BuildComponentFactory.makeNavigationButton("wAdd.png", "", "");
//		wAddBtt_.setBackground(Color.WHITE);
//		wAddBtt_.setBorderPainted(false);
//		wAddBtt_.setFocusable(false);
//		wAddBtt_.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				wTable.addRowObject(objIninRow);				
//			}
//
//		});
//		wRemoveBtt_ = BuildComponentFactory.makeNavigationButton("wRemove.png", "", "");
//		wRemoveBtt_.setBackground(Color.WHITE);
//		wRemoveBtt_.setBorderPainted(false);
//		wRemoveBtt_.setFocusable(false);
//		wRemoveBtt_.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				int i = leftTable.getSelectedRowCount();
//                              
//				if (i >0)
//                                {
//                                   if (!canBeRemoved())
//                                   {
//                                       JOptionPane.showConfirmDialog(null, "Saved man hours can't be deleted, please recall it first !" , "Delete Confirmation", JOptionPane.DEFAULT_OPTION);
//                                   }
//                                   else
//                                   {
//                                       prgCbox_.setSelectedIndex (-1); 
//                                       tskCbox_.setSelectedIndex (-1); 
//                                       wTable.subRow();
//				       saveAllBtt_.setEnabled (true);
//
//				       doSummarize();
//                                   }
//				}
//			}
//		});
//
//		buttPanel.add(wAddBtt_);
//		buttPanel.add(wRemoveBtt_);
//		toolBar.add(buttPanel);
//		toolBar.addSeparator(new Dimension(2,25));
//		toolBar.add(pal);
//		toolBar.add(assignBtt_);		
//		toolBar.add(clsBtt);		
//		if (session.getUserName().equals ("infodba") || session.getUserName().equals("axiaoyz"))
//		{
//		   toolBar.add(dciBtt_);
//		}
//
//		return toolBar;
//	}
//	
//	
//	  private JPanel getBottomPanel() throws TCException{
//		JPanel palBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//		palBottom.setBackground(Color.WHITE);
//		saveAllBtt_ = BuildComponentFactory.makeNavigationButton(
//				"wsave.png", "Save All", "Save All");
//		saveAllBtt_.setBackground(Color.WHITE);
//		saveAllBtt_.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent e) 
//	    	        {
//			      if (!canAllBeSaved()) return;
//			      try 
//		              {
//			  	  new Thread(new SaveProgressDialog(
//					WorkManagerDialog_old.this, 
//					"save all working hours", session, null, SaveProgressDialog.ACT_SAVEALL)).start();
//			      } 
//			      catch (TCException e1) 
//		              {
//		   	         e1.printStackTrace();
//			      }
//			}
//
//		});
//
//
//		
//		reviseBtt_ = BuildComponentFactory.makeNavigationButton(
//				null, "Recall", "Recall");
//		reviseBtt_.setBackground(Color.WHITE);
//		reviseBtt_.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//                                if (!canBeRevised()) return;
//
//				try {
//					new Thread(new SaveProgressDialog(
//							WorkManagerDialog_old.this, 
//							"Revise working hours", session, null, SaveProgressDialog.ACT_REVISE)).start();
//				} catch (TCException e1) {
//					e1.printStackTrace();
//				}
//			}
//
//		});
//
//
//		saveBtt_ = BuildComponentFactory.makeNavigationButton(
//	   	null, "Save", "Save");
//		saveBtt_.setBackground(Color.WHITE);
//		saveBtt_.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent e) 
//	 	        {
//                           if (!canBeSaved()) return;
//   
//				try {
//					new Thread(new SaveProgressDialog(
//						WorkManagerDialog_old.this, 
//						"Save working hours", session, null, SaveProgressDialog.ACT_SAVE)).start();
//				} catch (TCException e1) {
//					e1.printStackTrace();
//				}
//		        }
//
//		});
//
//
//		JButton closeBtt = BuildComponentFactory.makeNavigationButton(
//				"wclose.png", "Cancel", "Cancel");
//		closeBtt.setBackground(Color.WHITE);
//		closeBtt.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//
//				dispose();
//
//			}
//
//		});
//
//		palBottom.add(saveAllBtt_);
//		palBottom.add(reviseBtt_);
//		palBottom.add(saveBtt_);
//		palBottom.add(closeBtt);
//
//		return palBottom;
//	}
//  
//
//   private int getTotalNormalHoursAllowed(ManHourInfo mh_info)
//   {
//      int hours = 0;
//      int allowedWorkingDays = 0;
//
//      if (mh_info != null)
//      {
//         for (int idx =0; idx < mh_info.totalDays; idx ++)
//         {
//            if (mh_info.theMonthTmp[idx].isWorkRequired)
//            {
//               allowedWorkingDays += 1;
//            }
//         } 
//
//         hours =  8 * allowedWorkingDays;
//
//      }
//
//      return hours;
//   }	
//
//
//   private void initData()
//   {	
//	   if(mhInfo_==null){
//		   return ;
//	   }
//      String mouthName = mhInfo_.myMonth + "." + mhInfo_.myYear;
//      String[] days = new String[mhInfo_.totalDays];
//      String[] weeks = new String[mhInfo_.totalDays];
//      daysWeekMap = new HashMap();
//
//      for(int i=0; i<mhInfo_.totalDays; i++)
//      {
//         days[i] = Integer.toString (mhInfo_.theMonthTmp[i].dayOfMonth);
//         weeks[i] = mhInfo_.theMonthTmp[i].dayOfWeek;
//
//         daysWeekMap.put(days[i], weeks[i]);
//      }
//
//      if(this.wTable != null)
//      {
//         palTable.remove(this.wTable.getScroll());
//      }		
//      setFixedColumnTable(mouthName,days,weeks);
//
//      totalNormalHoursAllowed_ = getTotalNormalHoursAllowed(mhInfo_);
//      nfPal_.setText (new String (Integer.toString (totalNormalHoursAllowed_)));
//
//      if (mhInfo_.isManHourEditable)
//      {
//         assignBtt_.setEnabled(true);
//         wAddBtt_.setEnabled(true);
//         wRemoveBtt_.setEnabled(true);
//         saveBtt_.setEnabled(true);
//         reviseBtt_.setEnabled(true);
//      }
//      else
//      {
//         assignBtt_.setEnabled(false);
//         wAddBtt_.setEnabled(false);
//         wRemoveBtt_.setEnabled(false);
//         saveBtt_.setEnabled(false);
//         reviseBtt_.setEnabled(false);
//      }
//      saveAllBtt_.setEnabled(false);
//      reloadBtt_.setEnabled(false);
//
//      bldPrgAndTskTypeMap();
//      bldBillTypeAndIvalMap();
//      bldTskTypeDvalAndTskTypeMap();
//
//      initPrgComboBox();
//   }
//
//
//   public void enReloadBtt ()
//   {
//      String year = this.datePanel.getYearComb().getTextField().getText();
//      String month = this.datePanel.getMonthComb().getTextField().getText();
//			
//      if (mhInfo_.myYear.equals (year.toString()) && mhInfo_.myMonth.equals (month.toString ()))
//      {
//         reloadBtt_.setEnabled (false);               
//      }	
//      else
//      {
//         reloadBtt_.setEnabled (true);
//      }
//   }
//
//
//   private void reload()
//   {
//      try   
//      {
//         if (saveAllBtt_.isEnabled())
//         {
//            int n = JOptionPane.showConfirmDialog(null, "Save current data?", "Save Confirmation", JOptionPane.YES_NO_OPTION);  
//            if (n == JOptionPane.YES_OPTION) 
//            {
//	       new Thread(new SaveProgressDialog( WorkManagerDialog_old.this, "Save working hours", session, null, SaveProgressDialog.ACT_SAVEALL)).start();
//            }
//         }
//
//         String year = this.datePanel.getYearComb().getTextField().getText();
//         String month = this.datePanel.getMonthComb().getTextField().getText();
//
//         if (mhInfo_.myYear.equals (year.toString()) || mhInfo_.myMonth.equals (month.toString ()))
//         {
//            mhInfo_= null;
//            mhInfo_= WorkmanagerOperation.loadManHourInfo (session, year, month);
//            initData();
//    		
//            new Thread(new QueryProgressDialog( WorkManagerDialog_old.this, "loading working hours", session, null)).start(); 
//         }
//      }
//      catch (Exception e)
//      {
//         e.printStackTrace();
//      }
//   }
//     
//
//   private void setFixedColumnTable(String ymName,String[] days,String[] weeks)
//   {
//      newCloumn = new String[table_column_names.length+weeks.length];
//      System.arraycopy(table_column_names, 0, newCloumn, 0, table_column_names.length);
//
//      for(int i=0; i<weeks.length; i++)
//      {
//         newCloumn[i+table_column_names.length] = days[i];
//      }
//
//      HashMap widMap = new HashMap();
//      objLastRow = new Object[newCloumn.length];
//      objIninRow = new Object[newCloumn.length];
//
//      for(int i=0;i<newCloumn.length;i++)
//      {
//         int wid =45;
//         if (i == 0)
//         {
//            wid = 30;
//         }
//         else if(i == 1)
//         {
//            wid =300; //360;
//         }
//         else if(i == 2)
//         {
//            wid =200; // 250
//         }
//         widMap.put(newCloumn[i], wid);
//         objIninRow[i] = "";
//         if(i == 2)
//         {
//            objLastRow[i] = "Total";
//         }
//         else
//         {
//            objLastRow[i] = "";
//         }
//      }
//
//      List<GroupHeader> list1=new ArrayList<GroupHeader>();
//      list1.add(new GroupHeader(ymName,1,2));
//      list1.add(new GroupHeader("Summary",3,6));
//      List<GroupHeader> list2=new ArrayList<GroupHeader>();
//      int n = 0;
//      for(int i=0;i<weeks.length;i++)
//      {
//         list2.add(new GroupHeader(weeks[i],n,n));
//         n++;
//      }
//		
//      Object[][] objs = new Object[1][newCloumn.length];
//      objs[0]= objLastRow;
//      wTable=new WorkSheetTable(objs,newCloumn,7);
//      leftTable=wTable.getLeftTable();
//      leftTable.setRowHeight(25);
//      leftTable.setWidthsByHashMap(widMap);
//      leftTable.addGroupColumnHeaderList(list1, null);        
//        
//      rightTable=wTable.getRightTable();
//      rightTable.setCellSelectionEnabled(true);
//      rightTable.setRowHeight(25);
//      rightTable.setWidthsByHashMap(widMap);
//      rightTable.addGroupColumnHeaderList(list2, mhInfo_);
//        
//      int leftClomCount = leftTable.getColumnCount();
//      for(int i=0;i<leftClomCount;i++)
//      {
//         TableColumn clom = leftTable.getColumnModel().getColumn(i);
// 
//         WLeftTableCellRenderer lCellRender = new WLeftTableCellRenderer();
//         clom.setCellRenderer(lCellRender);		
//				
//         if(i == 2)
//         {					
//            WComboBoxEditor tskEditor = new WComboBoxEditor(getTskComboBox(), true);
//            tskEditor.setWorkManager(this);
//            clom.setCellEditor(tskEditor);
//         }
//         else if(i == 1)
//         {
//            WComboBoxEditor prgEditor = new WComboBoxEditor(getPrgComboBox(), false);
//            clom.setCellEditor(prgEditor);
//         }
//      }      
//	
//
//      int rightClomCount = rightTable.getColumnCount();
//      for(int i=0; i<rightClomCount;i++)
//      {
//         TableColumn clom = rightTable.getColumnModel().getColumn(i);
//			
//         WRightTableCellRenderer rCellRenderer = new WRightTableCellRenderer();
//	     rCellRenderer.setWorkManager(this);
//
//         if (mhInfo_.theMonthTmp[i].isWeekend)
//         {
//            rCellRenderer.setManHourBgColor (ManHourObject.weekendBackColor);
//         }
//
//         if (mhInfo_.theMonthTmp[i].isHoliday)
//         {
//            rCellRenderer.setManHourBgColor (ManHourObject.holidayBackColor);
//         }				 
//
//	 if (!mhInfo_.theMonthTmp[i].isHoliday && 
//             !mhInfo_.theMonthTmp[i].isWeekend && 
//             !mhInfo_.theMonthTmp[i].isWorkRequired)
//         {
//            rCellRenderer.setManHourBgColor (ManHourObject.noWorkingDayBackColor);
//         }
//
//         clom.setCellRenderer(rCellRenderer);
//      }       
//        
//      JScrollPane scroll = wTable.getScroll();
//      scroll.setBackground(Color.WHITE);
//      palTable.add(BorderLayout.CENTER,scroll);
//      leftTable.updateUI();
//      rightTable.updateUI();
//      palTable.validate();
//      palTable.repaint();
//      
//   }
//	
//		
//   private JComboBox getTskComboBox()
//   {
//      tskCbox_ = new JComboBox();
//      
//
//      tskCbox_.addItemListener(new ItemListener() 
//      {
//         public void itemStateChanged(ItemEvent e) 
//         {
//            if(e.getStateChange() == ItemEvent.SELECTED)
//            {  
//               int row = WComboBoxEditor.getCurRow();
//               int col = WComboBoxEditor.getCurCol();
//
//               String tskStr = (String) e.getItem();
//
//               if (isDuplicatedProgramAndTaskType (row, col, tskStr))
//               {
//                  String mes = "Duplicated program and task-type!";
//                  JOptionPane.showConfirmDialog(null, mes, "Warning...", JOptionPane.DEFAULT_OPTION);
//                  tskCbox_.setSelectedIndex (-1);
//               }
//               
//            }
//            rightTable.repaint();
//         }
//      });
//
//      tskCbox_.setMaximumRowCount (20);
//      tskCbox_.setFont (new Font("Arial", Font.PLAIN, 14));
//      return tskCbox_; 
//   }
//
//
//   private boolean isDuplicatedProgramAndTaskType (int row, int col, String task_type)
//   {
//       boolean isDuplicated = false;
//
//       if (task_type == null || task_type.length() <= 0)
//       {
//          return isDuplicated;
//       }
//
//       int rowCnt =leftTable.getRowCount();
//       int colCnt =leftTable.getColumnCount ();
//       
//       if (row < 0 || row > rowCnt -1) 
//       {
//          return isDuplicated;
//       }
//
//       if (col != 2)
//       {
//          return isDuplicated;
//       }
//
//       Object prgObj = leftTable.getValueAt (row, col -1);
//       if (prgObj == null || prgObj.toString().length() <= 0)
//       {
//          return isDuplicated;
//       }
//       String program = prgObj.toString();
//
//       for (int i = 0; i < rowCnt -1; i ++)
//       {
//          if (i == row)
//          {
//             continue;
//          }
//
//          Object pObj_i = leftTable.getValueAt (i, col -1);
//          if (pObj_i == null || pObj_i.toString().length() <= 0)
//          {
//             continue;
//          }
//          String pStr_i = pObj_i.toString();
//          
//          Object tObj_i = leftTable.getValueAt (i, col);
//          if (tObj_i == null || tObj_i.toString().length() <= 0)
//          {
//             continue; 
//          }
//          String tStr_i = tObj_i.toString();
//          
//          if (program.equals (pStr_i) && task_type.equals (tStr_i))
//          {
//             isDuplicated = true;
//             break;
//          }
//       }
//
//       return isDuplicated;
//   }
//   
//   
//   private void initPrgComboBox()
//   {
//       if (prgCbox_ == null) 
//       {
//          return;
//       }
//
//       isInitMHD_ = true;
//
//       if (pMap_ != null && !pMap_.isEmpty())
//       {
//          Collection <String> keyset = pMap_.keySet();  
//          List<String> list = new ArrayList<String>(keyset);  
//       
//          Collections.sort(list);  
//
//          for (int i = 0; i < list.size(); i++) 
//          {  
//             prgCbox_.addItem(list.get(i)); 
//          }      
//       }
//       
//       isInitMHD_ = false;
//    }
//
//
//    private JComboBox getPrgComboBox()
//    {
//       prgCbox_ = new JComboBox();
//      
//
//       prgCbox_.addActionListener (new ActionListener()
//       {
//          public void actionPerformed(java.awt.event.ActionEvent e) 
//          {
//             if (isInitMHD_)
//	     {
//                return;
//             }
//
//             if (!prgCbox_.isPopupVisible())
//             {
//                try
//                {
//                  prgCbox_.showPopup();
//                }
//                catch (Exception ee)
//                {
//                }
//             } 
//          }
//       });
//
//
//       prgCbox_.addItemListener(new ItemListener() 
//       {
//         int oldIdx = -1;
//
//         public void itemStateChanged(ItemEvent e) 
//         { 
//            if (isInitMHD_)
//	    {
//               return;
//            }
//
//            if(e.getStateChange() == ItemEvent.SELECTED)
//            {  
//               int row = WComboBoxEditor.getCurRow();
//               int col = WComboBoxEditor.getCurCol();
//
//               int idx = prgCbox_.getSelectedIndex();
//               if (idx != oldIdx)
//               {
//		  leftTable.setValueAt (new String (""), row, 2);
//		  leftTable.repaint();
//		  oldIdx = idx;
//               }
//            }
//         }
//      });
//     
//      prgCbox_.setMaximumRowCount (20);
//      prgCbox_.setFont (new Font("Arial", Font.PLAIN, 14));
//      return prgCbox_;
//   }
//
//
//   @Override
//   public void writeInforToFile(String s) {
//   }
//
//
//   @Override
//   public void tableChanged(TableModelEvent e) 
//   {
//      tablemode.removeTableModelListener(this);
//      int row = tctable.getRowCount()-1;
//	        
//      int column = e.getColumn();
//	        
//      if(column == 0 || column == 1 ||  column == 2)
//      { 
//         tablemode.addTableModelListener(this);
//	        	
//         return; 
//      }	
//      if (row>0) 
//      {  	         
//         int countValue = 0;
//         for(int i=0;i<row;i++)
//         {
//            Object data = tctable.getValueAt(i, column);      
//            countValue = countValue+Integer.parseInt(
//            data != null && data.toString().trim().length()>0?data.toString():"0");
//         }
//         tctable.setValueAt(countValue, row, column);
//      }  
//      tablemode.addTableModelListener(this);
//   }
//
//
//	public void seach(QueryProgressDialog progressDialog) {
//		initManHourData();
//
//		doSummarize();
//
//	}
//
//
//   public void saveAll(SaveProgressDialog progressDialog) 
//   {	
//      Object[][] objData = wTable.getData();
//
//      try
//      {       	 
//         progressDialog.setProgressBarMaxiMum(objData.length);
//         ArrayList <ManHourEntry> mheList = new ArrayList<ManHourEntry>();
//
//         int i = 0;
//         for(i=0; i<objData.length - 1; i++)
//         {
//            String prObjectName = objData[i][1].toString();
//            String taskTypeDval = objData[i][2].toString();
//
//            for(int j=7; j<objData[i].length; j++)
//            {
//               Object datObj = objData[i][j];
//         		    	   				   
//               if (datObj == null) 
//               {
//                  continue;
//               }
//
//	       if (!ManHourObject.isMe (datObj))
//               {
//                  continue;
//               }
//
//               ManHourObject mhObj0 = (ManHourObject) datObj;
//               List <ManHourObject> mheObjList = ManHourObject.getMheObjectList (mhObj0);
//               int size = (mheObjList != null) ? mheObjList.size () : 0;
//	           for (int pdx = 0; pdx < size; pdx ++)
//               {
//                  ManHourObject mhObj = mheObjList.get (pdx);
//                  String workHours = mhObj.getWorkingHours();
//                  String billType = mhObj.getBillType ();
//                  String status = mhObj.getTseStatus();
//                  String tseref = mhObj.getTseRef();
//                  String mheref = mhObj.getMheRef();
//		   
//                  if((status == null || !status.equals ("Working")) || 
//                     (tseref == null || !tseref.equals ("Null")) || 
//                     (mheref == null || !mheref.equals ("Null")))
//                  {
//                     continue;
//                  }
//
//                 if((workHours == null || workHours.length()==0 || workHours.equals("0")))
//                 {
//                    continue;
//                 }
//
//                 ManHourBillType mheBt = btMap_.get (billType);
//                 String btInternal= null;
//                 String brRef= null;
//                 if (mheBt != null)
//                 {
//                    btInternal = mheBt.billTypeInternal;
//                    brRef = mheBt.myRefBR;
//                 }
//
//                 ManHourEntry mhe= createManHourEntry (prObjectName, taskTypeDval, billType, btInternal, brRef, workHours, status, tseref, mheref, i, j-7);
//                 mheList.add (mhe);
//               }
//            }
//         }
//
//         progressDialog.setProgressBarState("Completing the process ...", i);
//         if(progressDialog.getAbortOperationFlag())
//         {
//            return;
//         }
// 
//
//         if (mheList != null && mheList.size () > 0)
//         {
//            int mheListSize =mheList.size(); 
//
//            ManHourEntry[] mheObjs = (ManHourEntry[]) mheList.toArray(new ManHourEntry[mheListSize]);
//		   		   
//            ManHourEntrySet mheSet = WorkmanagerOperation.save(session, mhInfo_, mheObjs);
//            doRefreshMHEData (mheSet.mheSet);
//
//            saveAllBtt_.setEnabled (false);
//            doSummarize ();
//
//	    tstMheInSet_ = mheSet.mheSet;
//	    tstMheOutList_ = mheList;
//         }
//
//      }
//      catch(Exception e)
//      {
//         e.printStackTrace();
//      }
//   }
//
//   private boolean isAllSelectedCellEmpty()
//   {
//      int selectedRows[] = rightTable.getSelectedRows();
//      int selectedColumns[] = rightTable.getSelectedColumns();
//      Object[][] objData = wTable.getData(); 
//
//      for (int i= 0; i < selectedRows.length; i ++)         
//      {
//         for (int j = 0; j < selectedColumns.length; j++)
//         {
//            Object obj = objData[ selectedRows[i] ] [ selectedColumns[j] + 7 ];
//            if (obj == null) 
//            {
//               continue;
//            }
//
//            if (!ManHourObject.isMe (obj))
//	    {
//               continue;
//            } 
//    
//            String objValue = obj.toString ();
//	        if (objValue == null || objValue.length() == 0)
//            {
//               continue;
//            }
//
//            return false;
//         }
//      }
//
//      return true;
//   }
//
//
//   private boolean isProgramClosed()
//   {
//      int selectedRows[] = rightTable.getSelectedRows();
//      boolean found = false;
//
//      for (int idx = 0; idx < selectedRows.length; idx ++)
//      {
//         String ptStr = leftTable.getValueAt (selectedRows[idx], 1).toString () + ":" +
//		        leftTable.getValueAt (selectedRows[idx], 2).toString ();
//         for (int jdx = 0; jdx < mhInfo_.theProgramSet.length; jdx ++)
//         {
//            String ptStr2 = mhInfo_.theProgramSet[jdx].prjId + "-" +
//		            mhInfo_.theProgramSet[jdx].prjName + ":" +
//			    mhInfo_.theProgramSet[jdx].tskTypeDval;
//	    if (ptStr2.equals (ptStr))
//            {
//               found = true;
//	       break;
//            }
//         }
//         if (!found)
//         {
//            return true;
//         }
//      }
//
//      return false;
//   }
//
//
//   private boolean findNonEditingCell ()
//   {
//      int selectedRows[] = rightTable.getSelectedRows();
//      int selectedColumns[] = rightTable.getSelectedColumns();
//      Object[][] objData = wTable.getData(); 
//
//      for (int i= 0; i < selectedRows.length; i ++)  
//      {
//         for (int j = 0; j < selectedColumns.length; j++)
//         {
//
//            Object obj = objData[ selectedRows[i] ] [ selectedColumns[j] + 7 ];
//            if (obj == null)
//            {
//               continue;
//            }
//
//            if (!ManHourObject.isMe (obj))
//            {
//               continue;
//            } 
//     
//            ManHourObject mhObj0 = (ManHourObject) obj;
//	    List <ManHourObject> mheList = ManHourObject.getMheObjectList(mhObj0);
//	    int size = (mheList != null) ? mheList.size() : 0;
//
//	    for (int pdx = 0; pdx < size; pdx ++)
//            {
//               ManHourObject mhObj = mheList.get(pdx);
//               String status = mhObj.getTseStatus();
//	       String tseRefStr = mhObj.getTseRef();
//	       String mheRefStr = mhObj.getMheRef();
//	       if (!(status.equals ("Working") && tseRefStr.equals ("Null") && mheRefStr.equals("Null")))
//               {
//                  return true;
//               }
//            }
//         }
//      }
//
//      return false;
//   }
//
//
//   private boolean canBeSaved ()
//   {
//      boolean canSave = false;
//
//      if (rightTable.getSelectedRows().length <= 0 ||
//          rightTable.getSelectedColumns().length <= 0)
//      {
//         String message1 = "Please select a data cell !";
//         JOptionPane.showConfirmDialog(null, message1, "Warning...", JOptionPane.DEFAULT_OPTION);  
//         return canSave;
//      }
//
//      if (isAllSelectedCellEmpty())
//      {
//         String message2 = "Selected celles are empty !";
//         JOptionPane.showConfirmDialog(null, message2, "Warning...", JOptionPane.DEFAULT_OPTION);  
//         return canSave;
//      }
//
//      if (findNonEditingCell ())
//      {
//         String message3 = "Not found the editing data!";
//         JOptionPane.showConfirmDialog(null, message3, "Warning...", JOptionPane.DEFAULT_OPTION);  
//         return canSave;
//      }
//
//      return true;
//   }
//
//
//   public void save(SaveProgressDialog progressDialog) 
//   {	
//      Object[][] objData = wTable.getData(); 
//      int rowCnt = rightTable.getRowCount ();
//      int[] selectedColumns = rightTable.getSelectedColumns(); 
//      int[] selectedRows = rightTable.getSelectedRows();      
//
//      try
//      {       	 
//         progressDialog.setProgressBarMaxiMum(selectedRows.length);
//         ArrayList <ManHourEntry> mheList = new ArrayList<ManHourEntry>();
//
//         int i = 0;
//         for ( i=0; i < selectedRows.length; i++)
//         {
//            String prObjectName = objData[selectedRows[i]][1].toString();
//            String taskTypeDval = objData[selectedRows[i]][2].toString();
//            for(int j=0; j<selectedColumns.length; j++)
//            {
//               Object datObj = objData[ selectedRows[i] ] [ selectedColumns[j] + 7 ];
//               if (datObj == null) 
//               {
//                  continue;
//               }
//
//               if (!ManHourObject.isMe (datObj))
//               {
//                  continue;
//               }
//
//               ManHourObject mhObj0 = (ManHourObject)datObj;
//               List <ManHourObject> objList = ManHourObject.getMheObjectList (mhObj0);
//               int size = (objList != null) ? objList.size () : 0;
//               for (int pdx = 0; pdx < size; pdx ++)
//               {
//                  ManHourObject mhObj = objList.get (pdx);
//                  String workHours = mhObj.getWorkingHours();
//                  String billType = mhObj.getBillType ();
//                  String status = mhObj.getTseStatus();
//                  String tseref = mhObj.getTseRef ();
//                  String mheref = mhObj.getMheRef();
//                  if((workHours == null || workHours.length() ==0 || workHours.equals("0")))
//                  {
//                     continue;
//                  }
//                 
//                  if ((status == null || !status.equals ("Working")) ||
//                      (tseref == null || !tseref.equals ("Null")) ||
//                      (mheref == null || !mheref.equals ("Null")))
//                  {
//                     continue;
//                  }
//                  
//                 ManHourBillType mheBt = btMap_.get (billType);
//                 String btInternal= null;
//                 String brRef= null;
//                 if (mheBt != null)
//                 {
//                    btInternal = mheBt.billTypeInternal;
//                    brRef = mheBt.myRefBR;
//                 }
//                  
//
//                  ManHourEntry mhe= createManHourEntry (prObjectName, taskTypeDval, billType, btInternal, brRef, workHours, status, tseref, mheref, selectedRows[i], selectedColumns[j]);
//                  mheList.add (mhe);
//               }
//            }
//         }
//
//         progressDialog.setProgressBarState("Completing the process ...", i);
//         if(progressDialog.getAbortOperationFlag())
//	 {
//            return;
//         }
//		
//         if (mheList != null && mheList.size () > 0)
//         {
//            int mheListSize =mheList.size(); 
//        
//            ManHourEntry[] mheObjs = (ManHourEntry[]) mheList.toArray(new ManHourEntry[mheListSize]);
//	  		   
//            ManHourEntrySet mheSet = WorkmanagerOperation.save(session, mhInfo_, mheObjs);
//            doRefreshMHEData (mheSet.mheSet);
//
//            doSummarize ();
//
//     	    tstMheInSet_ = mheSet.mheSet;
//	    tstMheOutList_ = mheList;
//         }
//      }
//      catch(Exception e)
//      {
//          e.printStackTrace();
//      }
//   }
//
//
//   private boolean findEditingCell ()
//   {
//      int selectedRows [] = rightTable.getSelectedRows();
//      int selectedColumns [] = rightTable.getSelectedColumns();
//      Object[][] objData = wTable.getData(); 
//
//      try 
//      {
//        for (int i= 0; i < selectedRows.length; i ++)  
//        {
//           for (int j = 0; j < selectedColumns.length; j++)
//           {
//              Object obj = objData[ selectedRows[i] ] [ selectedColumns[j] + 7 ];
//              if (obj == null) 
//              {
//                 continue;
//              }
//
//              if (!ManHourObject.isMe (obj))
//              {
//                 continue;
//              } 
//     
//              ManHourObject mhObj0 = (ManHourObject) obj;
//	      List <ManHourObject> mheList = ManHourObject.getMheObjectList (mhObj0);
//	      int size = (mheList != null) ? mheList.size () : 0;
//             
//	      ManHourObject mhObj = null; 
//              String status = null;
//	      String tseref = null;
//	      String mheref = null;
//	      if (size ==1)
//              {
//                 mhObj = mheList.get (0);
//                 status = mhObj.getTseStatus();
//		 tseref = mhObj.getTseRef();
//		 mheref = mhObj.getMheRef();
//		 if ((status != null && status.equals ("Working")) &&
//                     (tseref != null && tseref.equals ("Null")) &&
//                     (mheref != null && mheref.equals ("Null")))
//                 {
//                    return true;
//                 }
//              }
//              else if (size > 1)
//              {
//	         boolean found = true; 
//	         for (int pdx = 0; pdx < size; pdx ++)
//                 { 
//                    mhObj = mheList.get (pdx);
//                    status = mhObj.getTseStatus();
//		    tseref = mhObj.getTseRef();
//		    mheref = mhObj.getMheRef();
//                    if ((status == null || !status.equals ("Working")) ||
//                        (tseref == null || !tseref.equals ("Null")) || 
//                        (mheref == null || !mheref.equals ("Null")))
//                    {
//                       found = false;
//                    }
//                 }
//                 if (found)
//                 {
//                    return found;
//                 }
//              }
//           }
//         }
//      }
//      catch (Exception e)
//      {
//        e.printStackTrace();              
//      }
//
//      return false;
//   }
//
//
//   private boolean needAdminToHandle ()
//   {
//      int selectedRows [] = rightTable.getSelectedRows();
//      int selectedColumns [] = rightTable.getSelectedColumns();
//      Object[][] objData = wTable.getData(); 
//
//      try 
//      {
//        for (int i= 0; i < selectedRows.length; i ++)  
//        {
//           for (int j = 0; j < selectedColumns.length; j++)
//           {
//              Object obj = objData[ selectedRows[i] ] [ selectedColumns[j] + 7 ];
//              if (obj == null) 
//              {
//                 continue;
//              }
//
//              if (!ManHourObject.isMe (obj))
//              {
//                 continue;
//              } 
//    
//              ManHourObject mhObj0 = (ManHourObject) obj;
//	      List <ManHourObject> mheList = ManHourObject.getMheObjectList(mhObj0);
//	      int size = (mheList != null) ? mheList.size() : 0;
//	      for (int pdx = 0; pdx < size; pdx ++)
//              {
//                 ManHourObject mhObj = mheList.get (pdx);
//                 String status = mhObj.getTseStatus();
//                 String tseref = mhObj.getTseRef ();
//                 String mheref = mhObj.getMheRef ();
//
//                 if ((status != null && status.equals ("Failed")) &&
//                     (tseref != null && !tseref.equals ("Null")))
//                 {
//                    return true;
//                 }
//              }
//           }
//         }
//      }
//      catch (Exception e)
//      {
//        e.printStackTrace();              
//      }
//
//      return false;
//   }
//
//
//   private boolean canBeRevised ()
//   {
//      boolean canRevise = false;
//
//      if (rightTable.getSelectedRows().length <= 0 ||
//          rightTable.getSelectedColumns().length <= 0)
//      {
//         String message1 = "Please select a data cell  !";
//         JOptionPane.showConfirmDialog(null, message1, "Warning...", JOptionPane.DEFAULT_OPTION);  
//         return canRevise;
//      }
//
//      if (isAllSelectedCellEmpty())
//      {
//         String message2 = "Selected celles are empty !";
//         JOptionPane.showConfirmDialog(null, message2, "Warning...", JOptionPane.DEFAULT_OPTION);  
//         return canRevise;
//      }
//
//      if (findEditingCell ())
//      {
//         String message4 = "Found non editing cell !";
//         JOptionPane.showConfirmDialog(null, message4, "Warning...", JOptionPane.DEFAULT_OPTION);  
//         return canRevise;
//      }
//
//      if (isProgramClosed())
//      {
//          String message5 = "The program was closed !";
//          JOptionPane.showConfirmDialog(null, message5, "Warning...", JOptionPane.DEFAULT_OPTION);  
//          return canRevise;
//      }
//
//      if (needAdminToHandle ())
//      {
//         String message6 = "Timesheet status error, ask administroator for help !";
//         JOptionPane.showConfirmDialog(null, message6, "Warning...", JOptionPane.DEFAULT_OPTION);  
//         return canRevise;
//      }
//
//      return true;
//   }
//
//
//   public void revise(SaveProgressDialog progressDialog) 
//   {	
//      Object[][] objData = wTable.getData(); 
//      int rowCnt = rightTable.getRowCount ();
//      int[] selectedColumns = rightTable.getSelectedColumns(); 
//      int[] selectedRows = rightTable.getSelectedRows();       
//
//      try
//      {       	 
//         progressDialog.setProgressBarMaxiMum(selectedRows.length);
//         ArrayList <ManHourEntry> mheList = new ArrayList<ManHourEntry>();
//
//         int i = 0;
//         for ( i=0; i < selectedRows.length; i++)
//         {
//            String prObjectName = objData[selectedRows[i]][1].toString();
//            String taskTypeDval = objData[selectedRows[i]][2].toString();
//            for(int j=0; j<selectedColumns.length; j++)
//            {
//               Object datObj = objData[ selectedRows[i] ] [ selectedColumns[j] + 7 ];
//               if (datObj == null) 
//               {
//                  continue;
//               }
//
//               if (!ManHourObject.isMe (datObj))
//               {
//                  continue;
//               }
//         	   
//               ManHourObject mhObj0 = (ManHourObject) datObj;
//               List <ManHourObject> mheObjList =  ManHourObject.getMheObjectList(mhObj0);
//               int size = (mheObjList != null) ? mheObjList.size(): 0;
//               for (int pdx = 0; pdx < size; pdx ++)
//               {
//                  ManHourObject mhObj = mheObjList.get (pdx);
//                  String workHours = mhObj.getWorkingHours();
//                  String billType = mhObj.getBillType ();
//                  String status = mhObj.getTseStatus();
//                  String tseref = mhObj.getTseRef();
//                  String mheref = mhObj.getMheRef();
//
//                  if (status == null || status.equals ("Working")) 
//                  {
//                     continue;
//                  }
//
//	              if((workHours == null || workHours.length()==0 || workHours.equals("0")))
//                  {
//                     continue;
//                  }
//
//                  ManHourBillType mheBt = btMap_.get (billType);
//                  String btInternal= null;
//                  String brRef= null;
//                  if (mheBt != null)
//                  {
//                     btInternal = mheBt.billTypeInternal;
//                     brRef = mheBt.myRefBR;
//                  }
//
//                  ManHourEntry mhe= createManHourEntry (prObjectName, taskTypeDval, billType, btInternal, brRef, workHours, status, tseref, mheref, selectedRows[i], selectedColumns[j]);
//                  mheList.add (mhe);
//               }
//            }
//         }
//
//   	 progressDialog.setProgressBarState("Completing the process ...", i);
//         if(progressDialog.getAbortOperationFlag())
//	 {
//            return;
//         }
//		
//         if (mheList != null && mheList.size () > 0)
//         {
//            int mheListSize =mheList.size(); 
//        
//            ManHourEntry[] mheObjs = (ManHourEntry[]) mheList.toArray(new ManHourEntry[mheListSize]);
//	  		   
//            ManHourEntrySet mheSet = WorkmanagerOperation.revise(session, mhInfo_, mheObjs);
// 
//            doRefreshMHEData (mheSet.mheSet);
//
//            doSummarize ();
//
//            leftTable.repaint();
//
//	    tstMheOutList_ = mheList; 
//            tstMheInSet_ = mheSet.mheSet;
//         }
//      }
//      catch(Exception e)
//      {
//          e.printStackTrace();
//      }
//   }
//
//
//   private String printMHEObject (ManHourObject mhe_obj)
//   {
//      String mheStr = null;
//      
//      if (mhe_obj== null)
//      {
//         mheStr = "null mhe object";
//      }
//      else
//      {
//         mheStr = "[ " + mhe_obj.getWorkingHours() + 
//		  "  " + mhe_obj.getBillType() + 
//                  "  " + mhe_obj.getTseStatus () +
//                  "  " + mhe_obj.getTseRef () + 
//                  "  " + mhe_obj.getMheRef() + " ]";
//      }
//      return mheStr;
//   }
//      
//      
//   private String printMHEList ( List <ManHourEntry> mhe_list)
//   {
//      StringBuffer strBuf = new StringBuffer ("\n");
//      
//      int size = (mhe_list != null)? mhe_list.size () : 0;
//      for (int i = 0; i < size; i ++)
//      {
//         ManHourEntry mhe = mhe_list.get (i);
//         String mheStr = "[ " + mhe.row +
//		         "  " + mhe.col +
//			 "  " + mhe.workingHours + 
//			 "  " + mhe.billType + 
//			 "  " + mhe.tseStatus +
//			 "  " + mhe.myRefTE + 
//			 "  " + mhe.myRefMHE;
//         strBuf.append (mheStr);
//      }
//      
//      return strBuf.toString();
//   }
//
//
//   private String printMHESet (ManHourEntry[] mhe_set)
//   {
//      StringBuffer strBuf = new StringBuffer ("\n");
//      
//      int size = (mhe_set != null) ? mhe_set.length : 0;
//      for (int i = 0; i < size; i ++)
//      {
//         strBuf.append ("\n");
//         ManHourEntry mhe = mhe_set[i];
//         String mheStr = "[ " + mhe.row +
//		         "  " + mhe.col +
//			 "  " + mhe.workingHours + 
//			 "  " + mhe.billType + 
//			 "  " + mhe.tseStatus +
//			 "  " + mhe.myRefTE + 
//			 "  " + mhe.myRefMHE;
//         strBuf.append (mheStr);
//      }
//      
//      return strBuf.toString();
//   }
//  
//
//   private String printMhinfo (ManHourInfo mhinfo)
//   {
//      StringBuffer strBuf = new StringBuffer ("\n");
//     
//      if (mhinfo == null)
//      {
//         return "no object";
//      }
//
//      String userName = "User Name: " + mhinfo.myUserName + "\n\n";
//      strBuf.append (userName);
//
//      String position = "Position: " + mhinfo.myPosition + "\n\n";
//      strBuf.append (position);
//
//      String yearStr = "Year: " + mhinfo.myYear + "\n\n";
//      strBuf.append (yearStr);
//
//      String monthStr = "Month: " + mhinfo.myMonth + "\n\n";
//      strBuf.append (monthStr);
//
//      String edtStr = "Editable: " + ((mhinfo.isManHourEditable) ? "YES" : "NO") + "\n\n";  
//      strBuf.append (edtStr);
//
//      String hourlyUser = "Hourly Based User: " + ((mhinfo.isHourlyBasedUser) ? "YES" : "NO") + "\n\n";
//      strBuf.append (hourlyUser);
//
//      String billInfo = "Internal Bill Info: \n" +
//                        "   [N] - " + mhinfo.theBillTypeSet[0].billTypeInternal + "  " + mhinfo.theBillTypeSet[0].billRateName +
//                        "   [O] - " + mhinfo.theBillTypeSet[1].billTypeInternal + "  " + mhinfo.theBillTypeSet[1].billRateName +
//                        "   [H] - " + mhinfo.theBillTypeSet[2].billTypeInternal + "  " + mhinfo.theBillTypeSet[2].billRateName +
//                        "   [W] - " + mhinfo.theBillTypeSet[3].billTypeInternal + "  " + mhinfo.theBillTypeSet[3].billRateName + 
//			"\n\n";
//      strBuf.append (billInfo);
//
//      String prgInfo0 = "Working Program: \n";
//      strBuf.append (prgInfo0);
//      for (int i = 0; i < mhinfo.theProgramSet.length; i ++)
//      {
//         String prgInfo = "[ " + mhinfo.theProgramSet[i].prjId + ", " +
//		                 mhinfo.theProgramSet[i].prjName + ", " +
//			       	 mhinfo.theProgramSet[i].tskTypeDval + ", " + "(" +
//				 mhinfo.theProgramSet[i].startDay + ", " + 
//				 mhinfo.theProgramSet[i].endDay + ")" + " ]" + ", " + 
//				 mhinfo.theProgramSet[i].stkTag + "\n";
//	 strBuf.append (prgInfo);
//      }
//
//      strBuf.append ("\n");
//
//      return strBuf.toString ();
//   }
//
//
//   private ManHourEntry createManHourEntry (String projectName, String taskTypeDval, String billType, 
//      String bt_internal, String br_ref, String workingHours, String status, String tseref, String mheref, int row, int col) 
//   {
//        ManHourEntry hourEntry = new ManHourManageService.ManHourEntry();
//
//	hourEntry.myUserName = mhInfo_.myUserName;
//		
//	hourEntry.myYear = mhInfo_.myYear;
//		
//	hourEntry.myMonth = mhInfo_.myMonth;
//		
//	hourEntry.myDay = Integer.toString(mhInfo_.theMonthTmp[col].dayOfMonth);
//
//	hourEntry.myDayOfWeek = mhInfo_.theMonthTmp [col].dayOfWeek;
//		
//	hourEntry.myHoliday = mhInfo_.theMonthTmp[col].holidayName;
//	
//	hourEntry.workingHours = workingHours;
//	
//	hourEntry.myPrjName = pNameMap_.get(projectName);
//	String[] prjs = projectName.split("\\-");
//	hourEntry.myPrjId = prjs[0];
//
//	hourEntry.myTaskTypeDval =taskTypeDval;
//	hourEntry.myTaskType = ttDvalAndTskTypeMap_.get (taskTypeDval);
//		
//	hourEntry.billType = billType; 
//		
//	hourEntry.workRequired = mhInfo_.theMonthTmp[col].isWorkRequired ? "TRUE" : "FALSE";
//
//        hourEntry.tseStatus = status;
//	hourEntry.myRefTE = tseref;
//	hourEntry.myRefMHE = mheref;
//	hourEntry.billTypeInternal = bt_internal;
//	hourEntry.myRefBR = br_ref;
//
//	hourEntry.row = row;
//	hourEntry.col = col;
//
//	return hourEntry;
//    }
//	
// 
//   private void bldPrgAndTskTypeMap()
//   {
//      pMap_ = null;	      
//      if (mhInfo_ == null || mhInfo_.theProgramSet == null || mhInfo_.theProgramSet.length == 0)
//         return;
//
//      pMap_ = new HashMap <String, ArrayList <String>>();
//      pNameMap_ = new HashMap <String, String>();
//
//      for (int idx = 0; idx < mhInfo_.theProgramSet.length; idx ++)
//      {
//         String piStr = mhInfo_.theProgramSet[idx].prjId;
//         String pnStr = mhInfo_.theProgramSet[idx].prjName;
//         String ttStr = mhInfo_.theProgramSet[idx].tskTypeDval;
//         String keyStr = piStr + "-" + pnStr;
//         
//         ArrayList <String> ttList = (ArrayList<String>) pMap_.get(keyStr);
//         if (ttList == null)
//         {
//            ttList = new ArrayList<String>();
//            pMap_.put (keyStr, ttList);
//         }
//         ttList.add(ttStr);
//
//	 String pName = pNameMap_.get (keyStr);
//	 if (pName == null)
//         {
//            pNameMap_.put (keyStr, pnStr);
//         }
//      }
//   } 
//
//
//   private void bldTskTypeDvalAndTskTypeMap()
//   {
//      ttDvalAndTskTypeMap_ = null;
//      if (mhInfo_ == null || 
//          mhInfo_.theProgramSet == null || 
//          mhInfo_.theProgramSet.length == 0)
//      {
//         return;
//      }
//
//      ttDvalAndTskTypeMap_ = new HashMap <String, String> ();
//      for (int idx = 0; idx < mhInfo_.theProgramSet.length; idx ++)
//      {
//         String ttDvalStr = mhInfo_.theProgramSet[idx].tskTypeDval;
//         if ( !ttDvalAndTskTypeMap_.containsKey(ttDvalStr))
//         {
//            String ttStr = mhInfo_.theProgramSet[idx].tskType;
//
//            ttDvalAndTskTypeMap_.put (ttDvalStr, ttStr);
//         }
//      }
//   }
//
//
//   private void bldBillTypeAndIvalMap ()
//   {
//      btMap_ = null;	      
//      if (mhInfo_ == null || mhInfo_.theBillTypeSet == null || mhInfo_.theBillTypeSet.length == 0)
//         return;
//
//      btMap_ = new HashMap();
//      btMap_.put (ManHourObject.nBillType, mhInfo_.theBillTypeSet[0]);
//      btMap_.put (ManHourObject.oBillType, mhInfo_.theBillTypeSet[1]);
//      btMap_.put (ManHourObject.hBillType, mhInfo_.theBillTypeSet[2]);
//      btMap_.put (ManHourObject.wBillType, mhInfo_.theBillTypeSet[3]);
//   }
//
//
//   private void doRefreshMHEData(ManHourEntry[] mhe_set)
//   {	  
//       int mhCount = (mhe_set != null) ? mhe_set.length : 0;
//	    
//       if (mhCount <= 0)  
//       {
//          return;
//       }
//
//       for (int idx = 0; idx < mhCount; idx ++)
//       {
//          ManHourEntry mhEntry = mhe_set[idx];
//          
//          String hrsStr = mhEntry.workingHours;
//          ManHourObject mhObj = new ManHourObject (hrsStr);
//
//          String tseStr = mhEntry.tseStatus;
//          mhObj.setTseStatus (tseStr);
//
//          String tseref = mhEntry.myRefTE;
//          mhObj.setTseRef (tseref);
//
//          String mheref = mhEntry.myRefMHE;
//          mhObj.setMheRef (mheref);
//
//          String btStr = mhEntry.billType;
//          mhObj.setBillType (btStr);
//
//          int row = mhEntry.row;
//          int col = mhEntry.col;
//
//          rightTable.setValueAt (mhObj, row, col);
//       }  
//
//       rightTable.repaint();
//   }
//
//
//   private void doInitMHEData(ManHourEntry[] mhe_set)
//   {	  
//       int mhCount = (mhe_set == null && mhe_set.length ==0) ? 0: mhe_set.length;
//	    
//       if (mhCount > 0)
//       {
//          HashMap <String, Object[]> hmPrgRow = new HashMap();
//          Object[] rowObjSet = null;
//          ManHourObject mhObj = null;
//	     
//          for (int mhIdx = 0; mhIdx < mhCount; mhIdx ++)
//          {
//             ManHourEntry mhEntry = mhe_set [mhIdx];
//             String pNameStr = mhEntry.myPrjName;
//             String pIdStr = mhEntry.myPrjId;
//             String ttStr = mhEntry.myTaskType;
//             String ttDvalStr = mhEntry.myTaskTypeDval;
//             String keyStr = pIdStr + ":" + ttStr;
//
//             rowObjSet = hmPrgRow.get (keyStr);
//
//             if (rowObjSet == null)
//             {
//                rowObjSet = new ManHourObject [table_column_names.length + mhInfo_.totalDays];
//                hmPrgRow.put(keyStr, rowObjSet);
//
//                rowObjSet [1] = new ManHourObject (pIdStr + "-" + pNameStr );
//	        
//                rowObjSet [2] = new ManHourObject(ttDvalStr);
//	     }
//
//             String dayStr = mhEntry.myDay;
//             int myDay = Integer.parseInt(dayStr);
//	        
//             String hrsStr = mhEntry.workingHours;
//             mhObj = new ManHourObject (hrsStr);
//             String btStr = mhEntry.billType;
//             mhObj.setBillType (btStr);
//	     String tseStr = mhEntry.tseStatus;
//             mhObj.setTseStatus (tseStr);
//	     String tseref = mhEntry.myRefTE;
//             mhObj.setTseRef (tseref);
//	     String mheref = mhEntry.myRefMHE;
//	     mhObj.setMheRef (mheref);
//    
//	     Object oldObj =  rowObjSet [ myDay + 6];
//
//	     ManHourObject oldMheObj = (ManHourObject) rowObjSet [ myDay + 6];
//
//             if (oldMheObj != null)
//             {
//                ManHourObject mergedMheObj = ManHourObject.mergeMHEObject (oldMheObj, mhObj);
//                rowObjSet[myDay + 6] = mergedMheObj;
//             }
//	     else
//             {
//                ManHourObject mergedMheObj = ManHourObject.mergeMHEObject (null, mhObj);
//	        rowObjSet[myDay + 6] = mergedMheObj;       
//             }
//          }
//	      
//          Collection <String> keyset = hmPrgRow.keySet();  
//          List<String> list = new ArrayList<String>(keyset);  
//       
//          Collections.sort(list);  
//
//          for (int i = 0; i < list.size(); i++) 
//          {  
//             rowObjSet = hmPrgRow.get(list.get(i)); 
//             wTable.addRowObject(rowObjSet); 
//          }      
//       }  
//   }
//
//
//   private void initManHourData()
//   {	  
//      try
//      {
//         ManHourEntry [] mhEntrySet = WorkmanagerOperation.load(session, mhInfo_);
//         doInitMHEData (mhEntrySet);
//
//	 tstMheInSet_ = mhEntrySet;
//      }
//      catch (Exception e)
//      {
//         e.printStackTrace();
//      }	
//   }
//
//  
//   private boolean anyManHourCellSelected()
//   {
//      boolean selected = false;
//
//      int rowCnt =rightTable.getRowCount();    
//      int colCnt =rightTable.getColumnCount ();
//
//      for (int idx = 0; idx < rowCnt -1; idx ++)
//      {
//         for (int jdx = 0; jdx < colCnt; jdx ++)
//         {
//            if (rightTable.isCellSelected (idx, jdx))
//            {
//               selected = true;
//               break;
//            }
//         }
//     }
//
//     return selected;
//   }
//
//  
//   private int invalidNormalHrsDay (String n_value, boolean over_time)
//   {
//      double n_value_i = (n_value != null && n_value.length() !=0) ? Double.parseDouble (n_value) : 0.0;
//
//      Object[][] datObjSet = wTable.getData();
//      int rowCnt =rightTable.getRowCount();
//      int colCnt =rightTable.getColumnCount ();
//      double hours = 0.0;
//      int dayFound = -1;
//
//
//      for (int jdx =0; jdx < colCnt; jdx ++)  
//      {
//         if (!mhInfo_.theMonthTmp[jdx].isWorkRequired)
//         {
//            continue;
//         }
//
//         hours = 0.0;
//         for (int idx = 0; idx < rowCnt -1; idx ++)    
//         {
//            Object datObj = datObjSet [idx] [jdx + 7];   
//           
//	    if (!rightTable.isCellSelected (idx, jdx))
//            {
//	       if (datObj == null || datObj.toString().length() ==0)
//                  continue;
//
//               if (!ManHourObject.isMe (datObj))
//                  continue;
//
//  	       ManHourObject mhObj0 = (ManHourObject) datObj;
//	       List <ManHourObject> mheList = ManHourObject.getMheObjectList (mhObj0);
//	       int size = (mheList != null) ? mheList.size () : 0;
//	       for (int pdx = 0; pdx < size; pdx ++)
//           {
//              ManHourObject mhObj=mheList.get(pdx);
//	          String billType = mhObj.getBillType ();
//   	          if (!billType.equals (ManHourObject.nBillType))
//                     continue;
//
//               hours += Double.parseDouble (mhObj.toString());
//            }
//	    }
//	    else
//            {
//               hours += n_value_i;
//            }
//         }
//
//         if (over_time)
//         {
//            if (hours < 8.0 )
//            {
//               dayFound = jdx + 1;
//               break;
//            }
//         }
//         else
//         {
//            if (hours > 8.0 )
//            {
//                dayFound = jdx + 1;
//                break;
//            }
//         }
//      }
//
//      return dayFound;
//   }
//
//
//   private int invalidTotalHrsDay (String n_value, String o_value, String w_value, String h_value)
//   {
//      double n_value_i = (n_value != null && n_value.length() !=0) ? Double.parseDouble (n_value) : 0.0;
//      double o_value_i = (o_value != null && o_value.length() !=0) ? Double.parseDouble (o_value) : 0.0;
//      double w_value_i = (w_value != null && w_value.length() !=0) ? Double.parseDouble (w_value) : 0.0;
//      double h_value_i = (h_value != null && h_value.length() !=0) ? Double.parseDouble (h_value) : 0.0;
//
//      Object[][] datObjSet = wTable.getData();
//      int rowCnt =rightTable.getRowCount();
//      int colCnt =rightTable.getColumnCount ();
//      double hours = 0.0;
//      int dayFound = -1;
//
//
//      for (int jdx = 0; jdx < colCnt; jdx ++)  
//      {
//         hours = 0.0;
//         for (int idx = 0; idx < rowCnt -1; idx ++)      
//         {
//	    if (!rightTable.isCellSelected (idx, jdx))
//            {
//               Object datObj = datObjSet[idx][jdx+7];     
//            
//	       if (datObj == null || datObj.toString().length() ==0)
//                  continue;
//
//               if (!ManHourObject.isMe (datObj))
//                  continue;
//
//	       ManHourObject mheObj0 = (ManHourObject) datObj;
//	       List <ManHourObject> mheList = ManHourObject.getMheObjectList (mheObj0);
//	       int size = (mheList !=null) ? mheList.size () : 0;
//	       for (int pdx = 0; pdx < size; pdx ++)
//               {
//                  ManHourObject mheObj = mheList.get (pdx);
//	          hours += Double.parseDouble (mheObj.getWorkingHours());
//               }
//	    }
//	    else
//            {
//               if (n_value_i >0.0) hours += n_value_i;
//               if (o_value_i >0.0) hours += o_value_i;
//               if (w_value_i >0.0) hours += w_value_i;
//               if (h_value_i >0.0) hours += h_value_i;
//            }
//         }
//
//
//	 if (hours > 24.0)
//         {
//            dayFound = jdx + 1;
//	    break;
//         }
//      }
//
//      return dayFound;
//   }
//
//
//   private double getTotalNormalHrsForMonth (String n_value)
//   {
//      double n_value_i = (n_value != null && n_value.length() !=0) ? Double.parseDouble (n_value) : 0.0;
//
//      Object[][] datObjSet = wTable.getData();
//      int rowCnt =rightTable.getRowCount();
//      int colCnt =rightTable.getColumnCount ();
//      double hours = 0.0;
//
//      for (int jdx =0; jdx < colCnt; jdx ++)         
//      {
//         if (!mhInfo_.theMonthTmp[jdx].isWorkRequired)
//         {
//            continue;
//         }
//
//         for (int idx = 0; idx < rowCnt -1; idx ++)    
//         {
//           if (!rightTable.isCellSelected (idx, jdx))
//           {
//               Object datObj = datObjSet[idx][jdx]; 
//            
//	       if (datObj == null || datObj.toString().length() ==0)
//                  continue;
//
//               if (!ManHourObject.isMe (datObj))
//                  continue;
//
//	       ManHourObject mhObj0 = (ManHourObject) datObj;
//               List <ManHourObject> mheList = ManHourObject.getMheObjectList (mhObj0);
//	       int size = (mheList != null) ? mheList.size () : 0;
//	       for (int pdx = 0; pdx < size; pdx ++)
//               {
//                  ManHourObject mhObj = mheList.get(pdx);
//	          String billType = mhObj.getBillType ();
//   	          if (!billType.equals (ManHourObject.nBillType))
//                     continue;
//
//                  hours += Double.parseDouble (mhObj.toString());
//               }
//            }
//	    else
//	    {
//               hours += n_value_i;
//            }
//         }
//
//      }
//      return hours;
//   }
//
//
//   private int invalidWeekendHrsDay ()
//   {
//      int rowCnt =rightTable.getRowCount();
//      int colCnt =rightTable.getColumnCount ();
//      int dayFound = -1;
//
//      for (int jdx =0; jdx < colCnt; jdx ++)  
//      {
//         for (int idx = 0; idx < rowCnt -1; idx ++)    
//         {
//            if (rightTable.isCellSelected (idx, jdx))
//            {
//               boolean isWeekend = mhInfo_.theMonthTmp[jdx].isWeekend;
//               boolean isWorkRequired = mhInfo_.theMonthTmp[jdx].isWorkRequired;
//               if (!isWeekend && isWorkRequired)
//               {
//                  dayFound = jdx +1;
//                  break;
//               } 
//            }
//         }
//         if (dayFound > 0) break;
//      }
//
//      return dayFound;
//   }
//
//
//   private int invalidHolidayHrsDay ()
//   {
//      int rowCnt =rightTable.getRowCount();
//      int colCnt =rightTable.getColumnCount ();
//      int dayFound = -1;
//
//      for (int jdx =0; jdx < colCnt; jdx ++) 
//      {
//         for (int idx = 0; idx < rowCnt -1; idx ++)    
//         {
//	    if (rightTable.isCellSelected (idx, jdx))
//            {
//               boolean isHoliday = mhInfo_.theMonthTmp[jdx].isHoliday;
//               boolean isWorkRequired = mhInfo_.theMonthTmp[jdx].isWorkRequired;
//               if (!isHoliday || isWorkRequired)
//               {
//                  dayFound = jdx +1;
//                  break;
//               } 
//            }
//         }
//         if (dayFound > 0) break;
//      }
//
//      return dayFound;
//   }
//
//
//   private boolean canBeRemoved ()
//   {
//      boolean canRemove = true;
//      Object [][] dataObjs = wTable.getData();
//      int[] selectedRows = leftTable.getSelectedRows();
//      int colCnt =rightTable.getColumnCount ();
//
//      int rowCnt = (selectedRows != null) ? selectedRows.length : 0;
//
//      for (int idx = 0; idx < rowCnt; idx ++)
//      {
//         for (int jdx = 0; jdx < colCnt ; jdx ++)
//         {
//            Object obj = dataObjs [selectedRows[idx]] [ jdx + 7];
//         
//     	    if (obj == null)
//            {
//               continue;
//            } 
//	 
//            if (!ManHourObject.isMe (obj))
//            {
//               continue;
//            }
//
//            ManHourObject mheObj0 = (ManHourObject) obj;
//            List <ManHourObject> mheList = ManHourObject.getMheObjectList (mheObj0);
//            int size = (mheList != null) ? mheList.size () : 0;
//            for (int pdx = 0; pdx < size; pdx ++)
//            {
//               ManHourObject mheObj = mheList.get(pdx);
//               String tseStatus = mheObj.getTseStatus();
//                String tseref = mheObj.getTseRef();
//                String mheref = mheObj.getMheRef();
//               if ((tseStatus != null && !tseStatus.equals ("Working")) &&
//                   (tseref != null && !tseref.equals ("Null")) &&
//                   (mheref != null && !mheref.equals ("Null")))
//               {
//                  canRemove = false;
//                  break; 
//               }
//            }
//         }
//	 if (!canRemove) break;
//      }
//
//      return canRemove;
//   }
//
//
//   private boolean canBeAssigned(String n_value, String o_value, String w_value, String h_value)
//   {
//       Object[][] objData = wTable.getData();
//       boolean canAssign = false;
//       boolean n_ok = false;
//       boolean o_ok = false;
//       boolean w_ok = false;
//       boolean h_ok = false;
//
//       int choiceCnt = 0;
//       double normalHours = 0.0;
//        
//       try
//       {
//	  if (!anyManHourCellSelected())
//          {
//             String message1 = "Please select a data cell !";
//	     JOptionPane.showConfirmDialog(null, message1, "Warning...", JOptionPane.DEFAULT_OPTION);  
//	     return canAssign;
//          }
//
//	  if (!checkProjectStartAndEndDay())
//          {
//             String message1_1 = "Out of project Start or End Day, please contact project owner";
//	     JOptionPane.showConfirmDialog(null, message1_1, "Warning...", JOptionPane.DEFAULT_OPTION);  
//	     return canAssign;
//          }
//
//	  if (findNonEditingCell ())
//          {
//             String message1_2 = "Not found the editing data!";
//	     JOptionPane.showConfirmDialog(null, message1_2, "Warning...", JOptionPane.DEFAULT_OPTION);  
//	     return canAssign;
//          }
//
//	  if (findEmptyProgramforAssign())
//          {
//             String message1_3 = "Please select a program !";
//	     JOptionPane.showConfirmDialog(null, message1_3, "Warning...", JOptionPane.DEFAULT_OPTION);  
//	     return canAssign;
//          }
//
//	  if (findEmptyTaskforAssign())
//          {
//             String message1_4 = "Please select a task!";
//	     JOptionPane.showConfirmDialog(null, message1_4, "Warning...", JOptionPane.DEFAULT_OPTION);  
//	     return canAssign;
//          }
//
//	  if (isProgramClosed())
//          {
//             String message1_5 = "The program was closed !";
//	     JOptionPane.showConfirmDialog(null, message1_5, "Warning...", JOptionPane.DEFAULT_OPTION);  
//	     return canAssign;
//          }
//
//
//          if (n_value != null && n_value.length() > 0 )
//          {
//             choiceCnt ++;
//             n_ok = true;
//          }
//          
//          if (o_value != null && o_value.length() > 0 )
//          {
//             choiceCnt ++;
//	     o_ok = true;
//          }
//
//          if (w_value != null && w_value.length() > 0 )
//          {
//             choiceCnt ++;
//	     w_ok = true;
//          }
//          
//          if (h_value != null && h_value.length() > 0 )
//          {
//             choiceCnt ++;
//	     h_ok = true;
//          }
//
//          if (choiceCnt <1)
//          {
//             String message2 = "Please select a Blll Type !";
//	     JOptionPane.showConfirmDialog(null, message2, "Warning...", JOptionPane.DEFAULT_OPTION);  
//             return canAssign;
//          }
//     
//          if (choiceCnt >1 )
//          {
//             if (w_ok || h_ok)
//             {
//                String message3_1 = "Only one Bill Type can be selected !";
//	        JOptionPane.showConfirmDialog(null, message3_1, "Warning...", JOptionPane.DEFAULT_OPTION);  
//                return canAssign;
//             }
//          }
//
//
//          if (n_ok)
//          {
//             normalHours = Double.parseDouble (n_value);
//	     if (normalHours > 8.0)
//             {
//		String message4 = "Normal hours assigned can't be greater than 8.0 !";
//	        JOptionPane.showConfirmDialog(null, message4, "Warning...", JOptionPane.DEFAULT_OPTION);  
//                return canAssign;
//             }
//          }
//
//
//	  if (n_ok)
//          {
//             int whichday = invalidNormalHrsDay(n_value, false);
//
//	     if (whichday >0)
//             {
//                String message5 = "Normal hours for day " + whichday + " can't be greater than 8 !";
//                JOptionPane.showConfirmDialog(null, message5, "Warning...", JOptionPane.DEFAULT_OPTION);  
//                return canAssign;
//             }
//          }
//
//
//	  if (w_ok)
//          {
//             int whichday_w = invalidWeekendHrsDay ();
//             if (whichday_w > 0)
//             {
//                String message6_0 = "Do not support this type of operation!";
//                JOptionPane.showConfirmDialog(null, message6_0, "Warning...", JOptionPane.DEFAULT_OPTION);  
//                return canAssign;
//             }
//          }
//
//          if (h_ok)
//          {
//             int whichday_h = invalidHolidayHrsDay ();
//             if (whichday_h > 0)
//             {
//                String message6_1 = "Do not support this type of operation!";
//                JOptionPane.showConfirmDialog(null, message6_1, "Warning...", JOptionPane.DEFAULT_OPTION);  
//                return canAssign;
//             }
//          }
//
//          int whichTotalHrsDay = invalidTotalHrsDay(n_value, o_value, w_value, h_value);
//          if (whichTotalHrsDay > 0)
//          {
//             String message7 = "Total hours can not be greater than 24 !";
//             JOptionPane.showConfirmDialog(null, message7, "Warning...", JOptionPane.DEFAULT_OPTION);  
//	     return canAssign;
//          }
//
//	  if (n_ok)
//	  {
//              double totalNormalHoursOfMonth = getTotalNormalHrsForMonth(n_value);
//
//	      if (totalNormalHoursOfMonth > (double) totalNormalHoursAllowed_)
//              {
//                String message8 = "Total normal hours for a month can not be greather the allowed ";
//                JOptionPane.showConfirmDialog(null, message8, "Warning...", JOptionPane.DEFAULT_OPTION);  
//	        return canAssign;
//             }
//          }
//       }
//       catch (Exception e)
//       {
//          e.printStackTrace();
//       }  
//      
//       return true;
//   }
//
//
//   private void doCheckStatus ()
//   {
//      Object [][] datObjs = wTable.getData();
//
//      int [] selectedRows = rightTable.getSelectedRows();
//      int [] selectedColumns = rightTable.getSelectedColumns();
//
//      if (selectedRows.length != 1 && selectedColumns.length != 1)
//      {
//         String mes1 = "Please select on cell !";
//         JOptionPane.showConfirmDialog(null, mes1, "Warning...", JOptionPane.DEFAULT_OPTION);
//	 return;
//      }
//
//      Object obj0 = datObjs[selectedRows[0]][selectedColumns [0] + 7];
//      if (obj0 == null || !ManHourObject.isMe (obj0))
//      {
//         String mes2 = "Please select a data cell !";
//         JOptionPane.showConfirmDialog(null, mes2, "Warning...", JOptionPane.DEFAULT_OPTION);
//	 return;
//      }
//
//      List <ManHourObject> mheList = ManHourObject.getMheObjectList ((ManHourObject)obj0);
//      int size = (mheList != null) ? mheList.size() : 0;
//      StringBuffer strBuf = new StringBuffer("\n");
//      for (int pdx = 0; pdx < size; pdx ++)
//      {
//         ManHourObject mheObj = mheList.get(pdx);
//
//         String mes = "Day: " + (selectedColumns[0] + 1) + "   Work Hrs: " + mheObj.getWorkingHours() + 
//		              "   Status: " + mheObj.getTseStatus () + "   TSE UID: " + mheObj.getTseRef() +
//		              "   MHE UID: " + mheObj.getMheRef() + "\n";
//	 strBuf.append (mes);
//      }
//
//      JOptionPane.showConfirmDialog(null,strBuf.toString(), "Status...", JOptionPane.DEFAULT_OPTION);
//      return;
//   }      
//  
//
//   private void doAssign ()
//   {
//      String n_value = nTfd.getText();
//      String o_value = oTfd.getText();
//      String w_value = wTfd.getText();
//      String h_value = hTfd.getText();
//
//      List <Object> valueList = new ArrayList <Object> ();
//                
//      if (canBeAssigned (n_value, o_value, w_value, h_value))
//      {
//
//         if (n_value != null && n_value.length() > 0 )
//         {
//            ManHourObject mhObj_n = null;
//            if (n_value.equals ("0"))
//            {
//               n_value = "";
//            }
//            mhObj_n = new ManHourObject (n_value);
//            mhObj_n.setBillType (ManHourObject.nBillType);
//	    mhObj_n.setTseStatus ("Working");
//	    mhObj_n.setTseRef ("Null");
//	    mhObj_n.setMheRef ("Null");
//
//	    valueList.add (mhObj_n);
//         }
//         if (w_value != null && w_value.length() > 0 )
//         {
//            ManHourObject mhObj_w = null;
//            if (w_value.equals ("0"))
//            {
//                w_value = "";
//            }
//            mhObj_w = new ManHourObject (w_value);
//	    mhObj_w.setBillType (ManHourObject.wBillType);
//            mhObj_w.setTseStatus ("Working");
//	    mhObj_w.setTseRef ("Null");
//	    mhObj_w.setMheRef ("Null");
//
//	    valueList.add (mhObj_w);
//         }
//         if (h_value != null && h_value.length() > 0 )
//         {
//            ManHourObject mhObj_h = null;
//            if (h_value.equals ("0"))
//            {
//               h_value = "";
//            }
//
//            mhObj_h = new ManHourObject (h_value);
//            mhObj_h.setBillType (ManHourObject.hBillType);
//	    mhObj_h.setTseStatus ("Working");
//	    mhObj_h.setTseRef ("Null");
//	    mhObj_h.setMheRef ("Null");
//       
//       	    valueList.add (mhObj_h);
//         }
//		       
//         if (o_value != null && o_value.length() > 0 )
//         {
//            ManHourObject mhObj_o = null;
//
//            if (o_value.equals ("0"))
//            {
//               o_value = "";
//            }
//            mhObj_o = new ManHourObject (o_value);
//            mhObj_o.setBillType (ManHourObject.oBillType);
//            mhObj_o.setTseStatus ("Working");
//	    mhObj_o.setTseRef ("Null");
//	    mhObj_o.setMheRef ("Null");
//	    valueList.add (mhObj_o);
//         }
//			      
//         wTable.assignCellValue(valueList, mhInfo_);   
//         saveAllBtt_.setEnabled (true);
//         doSummarize();
//      }
//   }
//
//
//
//   private boolean findEmptyProgramforAssign()
//   {
//      int rowCnt =rightTable.getRowCount();
//      int colCnt =rightTable.getColumnCount ();
//      boolean found = false;
//
//
//      for (int idx = 0; idx < rowCnt -1; idx ++)    
//      {
//         for (int jdx =0; jdx < colCnt; jdx ++)    
//         {
//	    if (!rightTable.isCellSelected (idx, jdx))
//               continue;
//
//  	    Object obj = leftTable.getValueAt (idx, 1);
//	    if (obj == null || obj.toString().length() == 0)
//            {
//               found = true;
//            }        
//         }
//      }
//
//      return found;
//   }
//
//
//   private boolean findEmptyTaskforAssign ()
//   {
//      int rowCnt =rightTable.getRowCount();
//      int colCnt =rightTable.getColumnCount ();
//      boolean found = false;
//
//
//      for (int idx = 0; idx < rowCnt -1; idx ++)    
//      {
//         for (int jdx =0; jdx < colCnt; jdx ++)    
//         {
//	    if (!rightTable.isCellSelected (idx, jdx))
//               continue;
//
//  	    Object obj = leftTable.getValueAt (idx, 2);
//	    if (obj == null || obj.toString().length() == 0)
//            {
//               found = true;
//            }        
//         }
//      }
//
//      return found;
//   }
//
//
//   private boolean findEmptyProgram ()
//   {
//      Object[][] datObjSet = wTable.getData();
//      int rowCnt =leftTable.getRowCount();
//      boolean found = false;
//
//      for (int idx = 0; idx < rowCnt -1; idx ++)
//      {
//         Object datObj = datObjSet[idx][1];    
//            
//         if (datObj == null || datObj.toString().length() ==0)
//         {
//            found = true;
//            break;
//         }
//      }
//
//      return found;
//   }
// 
//
//   private boolean findEmptyTask ()
//   {
//      Object[][] datObjSet = wTable.getData();
//      int rowCnt =leftTable.getRowCount();
//      boolean found = false;
//
//      for (int idx = 0; idx < rowCnt -1; idx ++)    
//      {
//         Object datObj = datObjSet[idx][2];    
//            
//         if (datObj == null || datObj.toString().length() ==0)
//         {
//            found = true;
//            break;
//         }
//      }
//
//      return found;
//   } 
//
//   
//   private boolean findEmptyHrs()
//   {
//      Object[][] datObjSet = wTable.getData();
//      int rowCnt =rightTable.getRowCount();
//      int colCnt =rightTable.getColumnCount ();
//      boolean found = true; 
//
//      
//      for (int idx = 0; idx < rowCnt -1; idx ++)   
//      {
//         for (int jdx =0; jdx < colCnt; jdx ++)   
//         {
//            Object datObj = datObjSet [idx] [jdx + 7]; 
//                        
//	    if (datObj != null && datObj.toString().length() >0)
//            {
//               found = false;
//               break;
//            }
//         }
//      }
//
//      return found;
//   }
//
//
//   private boolean checkNormalHrsForSaveAll()
//   {
//      Object[][] datObjSet = wTable.getData();
//      int rowCnt =rightTable.getRowCount();
//      int colCnt =rightTable.getColumnCount ();
//      boolean found = false;
//      double hours = 0.0;
//
//      for (int jdx =0; jdx < colCnt; jdx ++)         
//      {
//         hours = 0.0;
//         for (int idx = 0; idx < rowCnt -1; idx ++) 
//         {
//            Object datObj = datObjSet [idx][jdx +7]; 
//            
//	    if (datObj == null || datObj.toString().length() ==0)
//               continue;
//
//            ManHourObject mhObj0 = (ManHourObject) datObj;
//            List <ManHourObject> mheList= ManHourObject.getMheObjectList (mhObj0);
//            int size = (mheList !=null) ? mheList.size() : 0;
//            for (int pdx = 0; pdx < size; pdx ++)
//            {
//   	       ManHourObject mhObj = (ManHourObject) mheList.get(pdx);
//               String billType = mhObj.getBillType ();
//               if (!billType.equals (ManHourObject.nBillType))
//                 continue;
//
//               hours += Double.parseDouble (mhObj.toString());
//            }
//         }
//         if (hours > 0.0 && hours != 8.0)
//         {
//            found = true;
//         }
//         
//      }
//      return found;
//   }
//   
//
//   private boolean canAllBeSaved ()
//   {
//       boolean canAllSave = false;
//
//       if (findEmptyProgram ())
//       {
//          String message1 = "Found the empty program !";
//          JOptionPane.showConfirmDialog(null, message1, "Warning...", JOptionPane.DEFAULT_OPTION);  
//	  return canAllSave;
//       }
//
//       if (findEmptyTask ())
//       {
//          String message2 = "Found a empty task !";
//          JOptionPane.showConfirmDialog(null, message2, "Warning...", JOptionPane.DEFAULT_OPTION);  
//	  return canAllSave;
//       }
//
//       if (findEmptyHrs())
//       {
//          String message3 = "Found empty man hour cell !";
//          JOptionPane.showConfirmDialog(null, message3, "Warning...", JOptionPane.DEFAULT_OPTION);  
//	  return canAllSave;
//       }
//
//       if (checkNormalHrsForSaveAll())
//       {
//          String message4 = "The normal hours per day must be 8 !";    
//          JOptionPane.showConfirmDialog(null, message4, "Warning...", JOptionPane.DEFAULT_OPTION);   
//	  return canAllSave;
//       }
//      
//       return true;
//   }
//
//
//    private void doSumByBillType ()
//    {
//      Object[][] datObjSet = wTable.getData();
//      int rowCnt =leftTable.getRowCount();
//
//      double n_value_i = 0.0;
//      double o_value_i = 0.0;
//      double w_value_i = 0.0;
//      double h_value_i = 0.0;
//
//      leftTable.setValueAt ("", rowCnt-1, 3);
//      leftTable.setValueAt ("", rowCnt-1, 4);
//      leftTable.setValueAt ("", rowCnt-1, 5);
//      leftTable.setValueAt ("", rowCnt-1, 6);
//
//      for (int idx = 0; idx < rowCnt -1; idx ++)
//      {
//         Object nStr = datObjSet [idx] [3];
//	 Object oStr = datObjSet [idx] [4];
//	 Object wStr = datObjSet [idx] [5];
//	 Object hStr = datObjSet [idx] [6];
//
//         if (nStr != null && nStr.toString().length() > 0)
//            n_value_i += Double.parseDouble(nStr.toString());
//
//         if (oStr != null && oStr.toString().length() > 0)
//            o_value_i += Double.parseDouble(oStr.toString());
//
//
//         if (wStr != null && wStr.toString().length() > 0)
//            w_value_i += Double.parseDouble(wStr.toString());
//
//
//         if (hStr != null && hStr.toString().length() > 0)
//            h_value_i += Double.parseDouble(hStr.toString());
//      }
//
//      if (n_value_i > 0.0)
//      {
//         String n_value_str = String.valueOf (n_value_i);
//	 String[] n_value_set = n_value_str.split ("\\.");
//         if (n_value_set[1].equals ("0"))
//         {		    
//            leftTable.setValueAt (n_value_set[0], rowCnt-1, 3);
//         }
//	 else
//         {
//            leftTable.setValueAt (n_value_str, rowCnt-1, 3);
//         }
//      }
//
//      if (o_value_i > 0.0)
//      {
//         String o_value_str = String.valueOf (o_value_i);
//	 String[] o_value_set = o_value_str.split ("\\.");
//         if (o_value_set[1].equals ("0"))
//         {		    
//            leftTable.setValueAt (o_value_set[0], rowCnt-1, 4);
//         }
//	 else
//         {
//            leftTable.setValueAt (o_value_str, rowCnt-1, 4);
//         }
//      }
//
//      if (w_value_i > 0.0)
//      {
//         String w_value_str = String.valueOf (w_value_i);
//	 String[] w_value_set = w_value_str.split ("\\.");
//         if (w_value_set[1].equals ("0"))
//         {		    
//            leftTable.setValueAt (w_value_set[0], rowCnt-1, 5);
//         }
//	 else
//         {
//            leftTable.setValueAt (w_value_str, rowCnt-1, 5);
//         }	     
//      }
//
//      if (h_value_i > 0.0)
//      {
//         String h_value_str = String.valueOf (h_value_i);
//         String[] h_value_set = h_value_str.split ("\\.");
//         if (h_value_set[1].equals ("0"))
//         {		    
//            leftTable.setValueAt (h_value_set[0], rowCnt-1, 6);
//         }
//	 else
//         {
//            leftTable.setValueAt (h_value_str, rowCnt-1, 6);
//         }	    
//      }
//
//
//      if (totalNormalHoursAllowed_ != 0)
//      {
//         double logging = (100.0) * (n_value_i/(double)totalNormalHoursAllowed_);
//         BigDecimal b = new  BigDecimal(logging);  
//         double logging2 =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();  
//         String logging2_str = "" + logging2 + "%";
//         lrPal_.setText (logging2_str);
//
//         if (logging2 <= 100.0)
//         {
//            lrPal_.setBackground (ManHourObject.loggingBackColor_Green);
//         }
//         if (logging2 < 95.0)
//         {
//            lrPal_.setBackground (ManHourObject.loggingBackColor_Yellow);
//         }
//         if (logging2 < 50.0)
//         {
//            lrPal_.setBackground (ManHourObject.loggingBackColor_Red);
//         }
//      }      
//   }
//
//
//   private void doSumByProgram ()
//   {
//      Object[][] datObjSet = wTable.getData();
//      int rowCnt =rightTable.getRowCount();
//      int colCnt =rightTable.getColumnCount ();
//
//      double n_value_i = 0.0;
//      double o_value_i = 0.0;
//      double w_value_i = 0.0;
//      double h_value_i = 0.0;
//
//
//      for (int idx = 0; idx < rowCnt -1; idx ++)   
//      {
//         n_value_i = 0.0;
//         o_value_i = 0.0;
//         w_value_i = 0.0;
//         h_value_i = 0.0;
//
//	 leftTable.setValueAt ("", idx, 3);
//	 leftTable.setValueAt ("", idx, 4);
//	 leftTable.setValueAt ("", idx, 5);
//	 leftTable.setValueAt ("", idx, 6);
//
//         for (int jdx =0; jdx < colCnt; jdx ++)    
//         {
//            Object datObj = datObjSet [idx] [jdx + 7]; 
//            
//	    if (datObj == null || datObj.toString().length() ==0)
//               continue;
//            
//	    if (!ManHourObject.isMe(datObj))
//               continue;
//
//	    ManHourObject mhObj0 = (ManHourObject) datObj;
//	    List <ManHourObject> mheList= ManHourObject.getMheObjectList (mhObj0);
//            int size = (mheList !=null) ? mheList.size() : 0;
//           
//	    for (int pdx = 0; pdx < size; pdx ++)
//            {
//               ManHourObject mhObj = mheList.get (pdx);
//               String billType = mhObj.getBillType ();
//   	       String workingHours = mhObj.getWorkingHours();
//
//	       if (billType.equals (ManHourObject.nBillType))
//                  n_value_i += Double.parseDouble (workingHours);
//
//               if (billType.equals (ManHourObject.oBillType))
//                  o_value_i += Double.parseDouble (workingHours);      
//
//	       if (billType.equals (ManHourObject.wBillType))
//                  w_value_i += Double.parseDouble (workingHours);
//
//   	       if (billType.equals (ManHourObject.hBillType))
//                  h_value_i += Double.parseDouble (workingHours);
//            }
//         }
//
//	 if (n_value_i >0.0 )
//         {
//            String n_value_str = String.valueOf (n_value_i);
//	    String[] n_value_set = n_value_str.split ("\\.");
//            if (n_value_set[1].equals ("0"))
//            {		    
//               leftTable.setValueAt (n_value_set[0], idx, 3);
//            }
//	    else
//            {
//               leftTable.setValueAt (n_value_str, idx, 3);
//            }
//         }
//
//	 if (o_value_i >0.0 )
//         {
//            String o_value_str = String.valueOf (o_value_i);
//	    String[] o_value_set = o_value_str.split ("\\.");
//            if (o_value_set[1].equals ("0"))
//            {		    
//               leftTable.setValueAt (o_value_set[0], idx, 4);
//            }
//	    else
//            {
//	       leftTable.setValueAt (o_value_str, idx, 4);
//            }
//         }
//
//	 if (w_value_i >0.0 )
//         {
//            String w_value_str = String.valueOf (w_value_i);
//	    String[] w_value_set = w_value_str.split ("\\.");
//            if (w_value_set[1].equals ("0"))
//            {		    
//               leftTable.setValueAt (w_value_set[0], idx, 5);
//            }
//	    else
//            {
//               leftTable.setValueAt (w_value_str, idx, 5);
//            }
//         }
//
//	 if (h_value_i >0.0 )
//         {
//            String h_value_str = String.valueOf (h_value_i);
//	    String[] h_value_set = h_value_str.split ("\\.");
//            if (h_value_set[1].equals ("0"))
//            {		    
//               leftTable.setValueAt (h_value_set[0], idx, 6);
//            }
//	    else
//            {
//               leftTable.setValueAt (h_value_str, idx, 6);
//            }
//         }
//      }
//    }
//
//
//    private void doSummarize()
//    {
//       doSumByProgram();
//
//       doSumByBillType ();
//    }
//
//
//    public boolean checkProjectStartAndEndDay()
//    {
//       boolean found =true;
//       int rowCnt = rightTable.getRowCount ();
//       int[] selectColumns = rightTable.getSelectedColumns();
//       int[] selectRows = rightTable.getSelectedRows();
//
//       int row = 0;
//       int col = 0;
//
//       for(int i=0; i<selectRows.length; i++)
//       {
//          row = selectRows[i];
//	  if (row >= rowCnt -1)
//          {
//             continue;
//          }
//    
//          ManHourProgram mhPrg = getManHourProgramByRow (row);
//          if (mhPrg == null)
//          {
//             continue;
//          }
//
//          for(int j=0; j<selectColumns.length; j++)
//          {	
//             col = selectColumns[j];
//             if (col < mhPrg.startDay -1 || col > mhPrg.endDay -1)
//             {
//                found = false;
//		break;
//	     }
//          }		
//
//
//          if (!found) break;
//      }
//
//      return found;
//   }
//       
//
//   public ManHourProgram getManHourProgramByRow(int row)
//   {
//       ManHourProgram mhPrg = null;
//       int rowCnt =leftTable.getRowCount();
//
//       if ( row < 0 || row > rowCnt -1 )
//       {
//          return mhPrg;
//       }
//
//       if (mhInfo_ == null || mhInfo_.theProgramSet == null || mhInfo_.theProgramSet.length == 0)
//       {
//          return mhPrg;
//       }
//
//       Object prgObj = leftTable.getValueAt (row, 1);
//       Object ttObj = leftTable.getValueAt (row, 2);
//       if (prgObj == null || prgObj.toString().length() <= 0 ||
//           ttObj == null || ttObj.toString ().length() <= 0)
//       {
//          return mhPrg;
//       }
//     
//       String prgStr = prgObj.toString();
//       String ttStr = ttObj.toString ();
//
//       for (int idx = 0; idx < mhInfo_.theProgramSet.length; idx ++)
//       {
//          String prjId = mhInfo_.theProgramSet[idx].prjId;
//          String prjName = mhInfo_.theProgramSet[idx].prjName;
//          String tskTypeDval = mhInfo_.theProgramSet[idx].tskTypeDval;
// 
// 	  String prjId_Name = prjId + "-" + prjName;
//
//	  if (prgStr.equals (prjId_Name) && ttStr.equals (tskTypeDval))
//          {
//             mhPrg = mhInfo_.theProgramSet[idx];
//	     break;
//          }
//       }
//       return mhPrg;
//    }
//    
//
//    private void testing ()
//    {    
//      try
//      {       	 
//         WorkmanagerOperation.Test(session);
//      }
//      catch(Exception e)
//      {
//          e.printStackTrace();
//      }
//    }
//  
//   private void printObj()
//   {  
//   
//      Object gObj = rightTable.getValueAt (0, 7);
//      if (gObj == null)
//      {
//         System.out.println("obj is null");
//      }
//      else
//      {
//	 if (!ManHourObject.isMe (gObj))
//         {
//            System.out.println ("obj: " +  gObj.toString());
//         }
//         else
//         {
//            ManHourObject mgObj = (ManHourObject)gObj;
//            System.out.println ("obj: " + mgObj.toString() +
//                          "  billtype: " + mgObj.getBillType() + 
//                          "  status: " + mgObj.getTseStatus() + 
//			  "  ref: " + mgObj.getTseRef());
//        }
//     }
//  }
//}
//
//
//
