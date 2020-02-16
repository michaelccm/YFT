package com.yfjcebp.extsupport.adjustplan;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.commands.open.OpenCommand;
import com.teamcenter.rac.common.lov.LOVComboBox;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.util.HyperLink;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.combobox.iComboBox;

public class AdjustExtSupportPlanDialog_hashSearchShowDialog extends AbstractAIFDialog{
	
	
	private TCSession tcsession;
	private String title = "ExtSupport Management";
	private Vector abandonValue;
	private LOVComboBox sectionCombox;
	private LOVComboBox divisionCombox;
	private Object[] lovSecValues;
	private LOVComboBox searchYearCombobox;
	private LOVComboBox searchMonthCombobox;
	private TitledBorder boarder;
	private JButton searchButton;
	private JButton cancelButton;
	
	private DefaultListModel mod;
	private TCComponent[] searchItemRevisions;
	private JList list;
	private JScrollPane scrollPanel;
	
	private String divisionName = "";
	private String sectionName = "";
	private boolean isControlMonth;

	public AdjustExtSupportPlanDialog_hashSearchShowDialog(TCSession session ) {
		super(true);
		this.tcsession = session;
		String fullname = "";
		try {
			TCComponentGroup group = tcsession.getGroup();
			fullname = group.getFullName();
		} catch (TCException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String[] fullnames = fullname.split("\\.");
		if (fullnames.length == 3) {
			divisionName = fullname;
			sectionName = "";
		}else if (fullnames.length == 4) {
			divisionName = fullname.split("\\.", 2)[1];
			sectionName = fullname;
		}else{
			MessageBox.post(this, "用户所在的组无法提供此查询", "WARNING", MessageBox.WARNING);
			return;
		}
		if (divisionName.trim().length() < 2) {
			MessageBox.post(this, "用户所在的组无法提供此查询", "WARNING", MessageBox.WARNING);
			return;
		}
		init();
	}
	
	private void init(){
		setTitle(title);
		Dimension a = new Dimension(900, 500);
		this.setPreferredSize(a);
		
		
		//***********************查询条件 start***************************
		abandonValue =  getPreference();
		
		
		sectionCombox = new LOVComboBox( "JCI6_Section");
		sectionCombox.setMaximumRowCount(15);
		sectionCombox.setPreferredSize(new Dimension(620, 20));
		sectionCombox.setMinimumSize(new Dimension(620, 20));
		sectionCombox.loadSelectionItems();
//		sectionCombox.setEnabled(false);
		TCComponentListOfValues listlov = sectionCombox.getLovComponent();
		try {
			lovSecValues = listlov.getListOfValues().getListOfValues();
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sectionCombox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (sectionCombox.getSelectedItem() != null
						&& !"".equals(sectionCombox.getTextField().getText())) {
					String secValue = sectionCombox.getSelectedItem().toString();
					secValue = secValue.substring(secValue.indexOf(".") + 1);
					if (divisionCombox.getSelectedItem() == null
							|| "".equals(divisionCombox.getTextField().getText())) {
						divisionCombox.setText(secValue);
						divisionCombox.updateSelections();
					}
				} else if (sectionCombox.getSelectedObject() != null) {
					String secValue = sectionCombox.getSelectedObject().toString();
					secValue = secValue.substring(secValue.indexOf(".") + 1);
					if (divisionCombox.getSelectedItem() == null
							|| "".equals(divisionCombox.getTextField().getText())) {
						divisionCombox.setText(secValue);
						divisionCombox.updateSelections();
					}
				}
			}
		});
		sectionCombox.loadSelectionItems();
		divisionCombox = new LOVComboBox("JCI6_Division");
		divisionCombox.setMaximumRowCount(15);
		divisionCombox.setPreferredSize(new Dimension(620, 20));
		divisionCombox.setMinimumSize(new Dimension(620, 20));
		divisionCombox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
					if (divisionCombox.getSelectedItem() != null
							|| !"".equals(divisionCombox.getTextField().getText())) {
						// 添加联动过滤
						String diviValue = divisionCombox.getSelectedDisplayString();
						diviValue = diviValue.replace(".", ";").split(";")[0];
						sectionCombox.setSelectedIndex(-1);
						sectionCombox.removeAllItems();
						if (lovSecValues != null && lovSecValues.length > 0) {
							for (int i = 0; i < lovSecValues.length; i++) {
								if (lovSecValues[i].toString().indexOf(
										diviValue) >= 0&& !abandonValue.contains(lovSecValues[i].toString())) {
									sectionCombox.addItem(lovSecValues[i]);
								}
							}
						}
					} else if (divisionCombox.getSelectedObject() != null
							|| !"".equals(divisionCombox.getSelectedObject()
									.toString())) {
						// 添加联动过滤
						String diviValue = divisionCombox.getSelectedDisplayString();
						diviValue = diviValue.replace(".", ";").split(";")[0];
						sectionCombox.setSelectedIndex(-1);
						sectionCombox.removeAllItems();
						if (lovSecValues != null && lovSecValues.length > 0) {
							for (int i = 0; i < lovSecValues.length; i++) {
								
								if (lovSecValues[i].toString().indexOf(
										diviValue) >= 0 && !abandonValue.contains(lovSecValues[i].toString())) {
									sectionCombox.addItem(lovSecValues[i]);
								}
							}
						}
					}
					sectionCombox.updateSelections();

			}
		});
		
		//abandonValue
		try {
			Object[] list = divisionCombox.getLovComponent().getListOfValues().getListOfValues();
			if (list != null && list.length > 0 && abandonValue.size() > 0) {
				divisionCombox.loadSelectionItems();
				divisionCombox.removeAllItems();
				for (int i = 0; i < list.length; i++) {
					if (!abandonValue.contains(list[i].toString())) {
						divisionCombox.addItem(list[i]);
					}
				}
			}
			
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (divisionName.length() > 2) {
			divisionCombox.setSelectedString(divisionName);
		}
		if (sectionName.length() > 2) {
			sectionCombox.setSelectedString(sectionName);
			
		}
		divisionCombox.setEnabled(false);
		sectionCombox.setEnabled(false);
		
		
		JLabel divisionLabel = new JLabel("Division");
		JLabel sectionLabel = new JLabel("Section");
		JLabel searchYearLabel = new JLabel("Year");
		JLabel searchMonthLabel = new JLabel("Month");
		
		isControlMonth = controlMonth();
		
		
		searchYearCombobox = new LOVComboBox("JCI6_Year");
		searchYearCombobox.loadSelectionItems();
		searchYearCombobox.setAutoCompleteSuggestive(true);
		searchMonthCombobox = new LOVComboBox("JCI6_Month");
		searchMonthCombobox.setAutoCompleteSuggestive(true);
		searchMonthCombobox.loadSelectionItems();
		
		searchYearCombobox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tmpyear = searchYearCombobox.getSelectedDisplayString().trim();
				if (!tmpyear.isEmpty() && isControlMonth) {
					int currentYear = Calendar.getInstance().get(Calendar.YEAR);
					int tmpYear_i = Integer.parseInt(tmpyear);
					if (tmpYear_i == currentYear) {
						searchMonthCombobox.setEnabled(true);
						searchMonthCombobox.setSelectedIndex(-1);
						int currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
						searchMonthCombobox.removeAllItems();
						for (int i = currentMonth; i < 13; i++) {
							searchMonthCombobox.addItem(i);
						}
					}else if (tmpYear_i > currentYear) {
						searchMonthCombobox.setSelectedIndex(-1);
						searchMonthCombobox.removeAllItems();
						for (int i = 1; i < 13; i++) {
							searchMonthCombobox.addItem(i);
						}
						searchMonthCombobox.setEnabled(true);
					}else if (tmpYear_i < currentYear) {
						searchMonthCombobox.setSelectedIndex(-1);
						searchMonthCombobox.setEnabled(false);
					}
				}
				
			}
		});
		
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchAction();
				scrollPanel.updateUI();
			}
		});
		cancelButton = new JButton("Close");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeAction();
			}
		});
		
		//***********************查询条件 end  ***************************
		
		//*************************查询 panel start****************************
		JPanel searchInfoPanel = new JPanel(new PropertyLayout());
		searchInfoPanel.add("1.1.left.center",divisionLabel);
		searchInfoPanel.add("1.2.left.center",divisionCombox);
		searchInfoPanel.add("2.1.left.center",sectionLabel);
		searchInfoPanel.add("2.2.left.center",sectionCombox);
		searchInfoPanel.add("3.1.left.center",searchYearLabel);
		searchInfoPanel.add("3.2.left.center",searchYearCombobox);
		searchInfoPanel.add("4.1.left.center",searchMonthLabel);
		searchInfoPanel.add("4.2.left.center",searchMonthCombobox);
		searchInfoPanel.add("5.1.left.center",searchButton);
		searchInfoPanel.add("5.2.left.center",cancelButton);
		//*************************查询 panel end   ***************************
		//*************************结果 panel start  **************************
		mod = new DefaultListModel();
		list = new JList(mod);
		list.setCellRenderer(new MyCellRenderer());
		list.addMouseMotionListener(new MouseMotionAdapter(){
		    @Override public void mouseMoved(MouseEvent e){
		        Point current = e.getPoint();
		        int currentIndex = list.locationToIndex(current);
		        list.setSelectedIndex(currentIndex);
		    }
		}); 
		list.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					int selectedIndex = list.getSelectedIndex();
					if (selectedIndex >= 0) {
						try {
							OpenCommand openCommand = new OpenCommand(AIFUtility.getActiveDesktop(), searchItemRevisions[selectedIndex]);
							openCommand.executeModal();
							closeAction();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					
				}
				
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPanel = new JScrollPane(list);
		boarder = new TitledBorder(null, "Search Results", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		scrollPanel.setViewportBorder(boarder);
		
		//*************************结果 panel end   ***************************
		JPanel parentPanel = new JPanel(new VerticalLayout(9, 2,2, 2, 2));
		parentPanel.add("top.bin", searchInfoPanel);
		parentPanel.add("top.bind.left.top", scrollPanel);
		getContentPane().add(parentPanel);
		pack();
		Dimension screen = getToolkit().getScreenSize(); // 得到屏幕尺寸
		setLocation((screen.width - getSize().width) / 2, (screen.height - getSize().height) / 2);
		setVisible(true);
		
		
	}
	
	private boolean controlMonth(){
		TCPreferenceService prefSvc = tcsession.getPreferenceService();
		//YFJC_EXTSupportmanagement_Control_Month
		String str = prefSvc.getString(TCPreferenceService.TC_preference_all, "YFJC_EXTSupportmanagement_Control_Month");
		if (str.contains("t")||str.contains("T")) {
			return true;
		}else if (str.contains("f")||str.contains("F")) {
			return false;
		}
		return false;
		
	}
	
	
	private void searchAction(){
		mod.removeAllElements();
		String divisionStr = divisionName;
		String sectionStr = sectionName;
		String yearStr = searchYearCombobox.getSelectedDisplayString();
		String monthStr = searchMonthCombobox.getSelectedDisplayString();
		System.out.println("divisionName:"+divisionName);
		System.out.println("sectionName:"+sectionName);
		System.out.println("yearStr:"+yearStr);
		System.out.println("monthStr:"+monthStr);
		
		if (yearStr == null  || yearStr.trim().length() == 0 || monthStr == null || monthStr.trim().length() == 0) {
			MessageBox.post(this, "Year和Month必须存在值", "ERROR", MessageBox.ERROR);
			return;
		}
		
		if (!isNumber(yearStr) ||!isNumber(monthStr)) {
			MessageBox.post(this, "Year和Month必须是数字", "ERROR", MessageBox.ERROR);
			return ;
		}
		if (sectionStr.trim().isEmpty() && (monthStr.trim().isEmpty()||monthStr.trim().equals("0"))) {
			System.out.println("Division+Year");
			String[] searchAttrs = { "Division", "Year"};
			String[] searchValues = {divisionStr,yearStr};
			searchItemRevisions = query(tcsession, "__YFJC_LCC_Ext2Supp",
					searchAttrs, searchValues);
		}else if (sectionStr.trim().isEmpty()&& !(monthStr.trim().isEmpty()||monthStr.trim().equals("0"))) {
			System.out.println("Division+Year+Month");
			String[] searchAttrs = { "Division", "Year", "Month" };
			String[] searchValues = {divisionStr,yearStr,monthStr};
			searchItemRevisions = query(tcsession, "__YFJC_LCC_Ext2Supp",
					searchAttrs, searchValues);
		}else if (!sectionStr.trim().isEmpty()&& (monthStr.trim().isEmpty()||monthStr.trim().equals("0"))) {
			System.out.println("Division+Section+Year");
			String[] searchAttrs = { "Division","Section", "Year"};
			String[] searchValues = {divisionStr,sectionStr,yearStr};
			searchItemRevisions = query(tcsession, "__YFJC_LCC_Ext2Supp",
					searchAttrs, searchValues);
		}else{
			System.out.println("Division+Section+Year+Month");
			String[] searchAttrs = { "Division","Section", "Year", "Month" };
			String[] searchValues = {divisionStr,sectionStr,yearStr,monthStr};
			searchItemRevisions = query(tcsession, "__YFJC_LCC_Ext2Supp",
					searchAttrs, searchValues);
		}
		if (searchItemRevisions == null  || searchItemRevisions.length == 0) {
			boarder.setTitle("__YFJC_LCC_Ext2Supp-Results: "+0+" Ext2Supp");
			return ;
		}else{
			System.out.println(searchItemRevisions.length);
			boarder.setTitle("__YFJC_LCC_Ext2Supp-Results: "+searchItemRevisions.length+" Ext2Supp");
			for (int j = 0; j < searchItemRevisions.length; j++) {
				try {
					String name = searchItemRevisions[j].getStringProperty("object_name");
					System.out.println(name);
					HyperLink link = new HyperLink(name);
//					link.setUnderLine(false);
					mod.add(j,link);
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	 class MyCellRenderer extends HyperLink implements ListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			HyperLink com = (HyperLink)value;
			com.setHorizontalAlignment(SwingConstants.LEFT);
			if (isSelected) {
				com.setUnderLine(true);
			}else{
				com.setUnderLine(false);
			}
			return com;
		}
		 
	 }
	 
	
	
	private void closeAction(){
		dispose();
	}
	
	private boolean isNumber(String str){
		if (str == null  ||  str.trim().length() == 0) {
			return true;
		}else{
			for (int i = 0; i < str.length(); i++) {
				if (!Character.isDigit(str.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	/**
	 * 用于查询
	 * 
	 * @param session
	 * @param query_name
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	private TCComponent[] query(TCSession session, String query_name,
			String[] arg1, String[] arg2) {
		TCComponentContextList imancomponentcontextlist = null;
		TCComponent[] component = null;
		try {
			TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session
					.getTypeComponent("ImanQuery");
			TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype
					.find(query_name);
			TCTextService imantextservice = session.getTextService();
			String[] queryAttribute = new String[arg1.length];
			for (int i = 0; i < arg1.length; ++i)
				queryAttribute[i] = imantextservice.getTextValue(arg1[i]);

			String[] queryValues = new String[arg2.length];
			for (int i = 0; i < arg2.length; ++i)
				queryValues[i] = arg2[i];
			imancomponentcontextlist = imancomponentquery
					.getExecuteResultsList(queryAttribute, queryValues);
			component = imancomponentcontextlist.toTCComponentArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return component;
	}
	
	//YFJC_UnusedDivisionAndSection
	private Vector getPreference(){
		Vector returnV = new Vector();
		TCPreferenceService prefSvc = tcsession.getPreferenceService();
		String[] strs = prefSvc.getStringArray(TCPreferenceService.TC_preference_all, "YFJC_abandoned_division_section");
		for (String string : strs) {
			returnV.add(string);
		}
		return returnV;
	}
	
	
}
