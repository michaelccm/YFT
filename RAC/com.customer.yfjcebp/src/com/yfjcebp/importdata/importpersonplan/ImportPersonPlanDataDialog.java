/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: ImportPersonPlanDataDialog.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-4-15	zhangyn  		Ini		对话框选择要导入的类型				   
 #=============================================================================
 */
package com.yfjcebp.importdata.importpersonplan;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.VerticalLayout;
import com.yfjcebp.importdata.utils.MessageDialog;
import com.yfjcebp.importdata.utils.OriginUtil;
import com.yfjcebp.importdata.utils.POIExcelParser;

public class ImportPersonPlanDataDialog  extends AbstractAIFDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title = "导入人头计划";
	private String insideSheet = "";
	private String outsideSheet = "";
	private ImportPersonPlanDataHandler importPersonPlanDataHandler;
	private JCheckBox chkInside;
	private JCheckBox chkOutside;
	private List<String> importList;
	private String importPersonPlanPreference = "YFJC_Import_PersonPlan_RowStartEnd";
	private String SearchImportPersonPlan =  "YFJC_SearchCostInfoByImportPersonPlan";
	private TCSession session;
	private File file;
	private TCComponentFolder tcfolder = null;
	private String filename = "";
	private int insideRowStart = 0;
	private int insideRowEnd = 0;
	private int outsideRowStart = 0;
	private int outsideRowEnd = 0;		
	private static Registry reg = Registry.getRegistry(OriginUtil.class);
	
	
	public ImportPersonPlanDataDialog(ImportPersonPlanDataHandler importPersonPlanDataHandler,TCSession session,File file,TCComponentFolder tcfolder,String filename,String insideSheet,String outsideSheet,int insideRowStart, int insideRowEnd,int outsideRowStart,int outsideRowEnd){
		super(true);
		this.importPersonPlanDataHandler = importPersonPlanDataHandler;
		this.session = session;
		this.file = file;
		this.tcfolder = tcfolder;
		this.filename = filename;
		this.insideSheet = insideSheet;
		this.outsideSheet = outsideSheet;
		this.insideRowStart = insideRowStart;
		this.insideRowEnd = insideRowEnd;
		this.outsideRowStart = outsideRowStart;
		this.outsideRowEnd = outsideRowEnd;
		init();
	}
	
	public void init(){
		setTitle(title);
		JPanel parentPanel = new JPanel(new VerticalLayout(5, 2,2, 2, 2));
		JPanel itemInfoPanel = new JPanel(new PropertyLayout());
		
		chkInside  = new JCheckBox(insideSheet);
		chkOutside = new JCheckBox(outsideSheet);
		
		importList = new ArrayList<String>();

		
		itemInfoPanel.add("1.1.left.center",new JLabel("选择导入类型："));
		itemInfoPanel.add("1.2.left.center", chkInside);
		itemInfoPanel.add("1.3.left.center", chkOutside);
		
		JButton btnConfirm = new JButton("导入");
		btnConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chkInside.isSelected()) {
					importList.add(insideSheet);
				}
				if (chkOutside.isSelected()) {
					importList.add(outsideSheet);
				}
				if(importList.size()==0){
					MessageBox.post("请选择要导入的类型，再执行导入!","Information",MessageBox.WARNING);
				}else{
					importPersonPlanDataHandler.setImportList(importList);
					dispose();
					doImport();
				}
				
			}
		});
		
		JButton btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		JPanel panelButton = new JPanel(new FlowLayout());
		panelButton.add(btnConfirm);
		panelButton.add(btnCancel);
		
		parentPanel.add("top.bin", new Separator());
		parentPanel.add("top.bind.left.top", itemInfoPanel);
		parentPanel.add("bottom.nobind.center.top", panelButton);
		
		getContentPane().add(parentPanel);
		pack();
		Dimension screen = getToolkit().getScreenSize(); // 得到屏幕尺寸
		setLocation((screen.width - getSize().width) / 2, (screen.height - getSize().height) / 2);
		setVisible(true);
		
	}
	
	protected void doImport() {
		boolean  cheakExcelData   = true;
		StringBuffer dataNoValue = new StringBuffer();
		
		try {
			POIExcelParser parser = new POIExcelParser(file);
			List list = new ArrayList();
			List insideNoValueList = new ArrayList();
			List outsideNoValueList = new ArrayList();
			for(int n = 0 ;n<importList.size();n++)
			{
				List<String> line = null;
				if(importList.get(n).equals(insideSheet))
				{
					if(!parser.setInit(importList.get(n), 27, insideRowStart-1,insideRowEnd)){
						return;
					}
					while ((line = parser.parsePersonPlanLine()) != null) {
						cheakExcelData = true;
						HashMap monthMap = getInitialInsideMonthMap();
						StringBuffer basic = new StringBuffer("Human Resource Plan"+"|Internal|"+line.get(0).trim()+"|");
						
						for(int i = 1 ;i<line.size();i++)
						{
							String data[] = line.get(i).split("\\|");
							
							if(i==1 && (data[0].trim().length()!=4||data[0].trim().equals("")||data[0].trim().equals(""))){
								
								MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + importPersonPlanPreference + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
								return ;
							}
							String datayear = data[0].trim();
							String datamonth = data[1].trim();
							String monthvalue = data[2].trim();	
							if(monthvalue.equals(""))//如果一行中有列没值，对象不保存
							{
								insideNoValueList.add(insideRowStart+"");
								cheakExcelData = false;
								break;
							}
							if(!datayear.equals(""))
							{
								if(i!=1){
									basic = setInsideMonthMap(basic,monthMap);
									list.add(basic);
									basic = new StringBuffer("Human Resource Plan"+"|Internal|"+line.get(0).trim()+"|"+datayear+"|");
								}else{
									basic.append(datayear+"|");
									
								}
							}
							monthMap.put(datamonth, monthvalue);
						}
						if(cheakExcelData)
						{
							basic = setInsideMonthMap(basic,monthMap);
							list.add(basic);
							
						}
						insideRowStart ++ ;
					}
				}else if(importList.get(n).equals(outsideSheet))
				{
					if(!parser.setInit(importList.get(n), 27, outsideRowStart-1,outsideRowEnd)){
						return;
					}
					while ((line = parser.parsePersonPlanLine()) != null) {
						cheakExcelData = true;
						HashMap monthMap = getInitialOutsideMonthMap();
						StringBuffer basic = new StringBuffer("Human Resource Plan"+"|ExtSupporter|"+line.get(0).trim()+"|");
						for(int i = 1 ;i<line.size();i++)
						{
							String data[] = line.get(i).split("\\|");
							
							if(i==1 && (data[0].trim().length()!=4||data[0].trim().equals("")||data[0].trim().equals(""))){
								MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + importPersonPlanPreference + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
								return ;
							}
							String datayear = data[0].trim();
							String datamonth = data[1].trim();
							String monthvalue = data[2].trim();
							if(monthvalue.equals(""))//如果一行中有列没值，对象不保存
							{
								outsideNoValueList.add(outsideRowStart+"");
								cheakExcelData = false;
								break;
							}
							if(!datayear.equals(""))
							{
								if(i!=1){
									basic = setOutsideMonthMap(basic,monthMap);
									list.add(basic);
									basic = new StringBuffer("Human Resource Plan"+"|ExtSupporter|"+line.get(0).trim()+"|"+datayear+"|");
								}else{
									basic.append(datayear+"|");
									
								}
							}
							monthMap.put(datamonth, monthvalue);
						}
						if(cheakExcelData)
						{
							basic = setOutsideMonthMap(basic,monthMap);
							list.add(basic);
						}
						outsideRowStart ++ ;
					}
				}			
			}
			
			String data[] = new String[list.size()];
			for(int j=0;j<list.size();j++)
			{
				data[j] = list.get(j).toString();
			}
			
			Object obj[] = new Object[3];
			obj[0] = tcfolder;
			obj[1] = data;
			if(insideNoValueList.size()>0||outsideNoValueList.size()>0)
			{
				obj[2] = "ExcelError";
			}else{
				obj[2] = "";
			}
			TCUserService service = session.getUserService();
			String error = (String)service.call("importPersonPlan", obj);
			dataNoValue.append("目前导入的是："+filename+"\r\n"+"\r\n");
			if(error.length()>0)
			{
				if(error.equals(SearchImportPersonPlan)){
					dataNoValue.append("没找到 " + error + " 查询构建器！请联系系统管理员"+"\r\n"+"\r\n");
					
				}else{
					dataNoValue.append("错误信息："+"\r\n");
					dataNoValue.append("没有找到组："+error+"请检查再次导入！"+"\r\n"+"\r\n");
				}
			}
			if(insideNoValueList.size()>0||outsideNoValueList.size()>0){
				dataNoValue.append("警告信息："+"\r\n");
				if(insideNoValueList.size()>0)
				{
					dataNoValue.append(insideSheet+":第");
					for(int i = 0; i<insideNoValueList.size();i++)
					{
						dataNoValue.append(insideNoValueList.get(i)+",");
					}
					dataNoValue.append("行数据不完整，请检查再次导入！"+"\r\n"+"\r\n");
				}
				if(outsideNoValueList.size()>0)
				{
					dataNoValue.append(outsideSheet+":第");
					for(int i = 0; i<outsideNoValueList.size();i++)
					{
						dataNoValue.append(outsideNoValueList.get(i)+",");
					}
					dataNoValue.append("行数据不完整，请检查再次导入！"+"\r\n"+"\r\n");
				}
				
			}
			if(insideNoValueList.size()==0 && outsideNoValueList.size()==0 && error.length()==0)
			{
				dataNoValue.append("导入完成！"+"\r\n");
			}
		}catch (TCException e) {
			e.printStackTrace();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
		new MessageDialog(dataNoValue.toString()); 
	}
	
	/**
	 * 设置内部月份初始值
	 * @return map
	 */
	public HashMap getInitialInsideMonthMap()
	{
		HashMap map = new HashMap();
		map.put("1月", "");
		map.put("2月", "");
		map.put("3月", "");
		map.put("4月", "");
		map.put("5月", "");
		map.put("6月", "");
		map.put("7月", "");
		map.put("8月", "");
		map.put("9月", "");
		map.put("10月", "");
		map.put("11月", "");
		map.put("12月", "");
		
		return map;
	}
	
	/**
	 * 设置内部月份excel数据值
	 * @return StringBuffer basic
	 */
	public StringBuffer setInsideMonthMap(StringBuffer basic , HashMap monthMap)
	{
		basic.append(monthMap.get("1月")+"|");
		basic.append(monthMap.get("2月")+"|");
		basic.append(monthMap.get("3月")+"|");
		basic.append(monthMap.get("4月")+"|");
		basic.append(monthMap.get("5月")+"|");
		basic.append(monthMap.get("6月")+"|");
		basic.append(monthMap.get("7月")+"|");
		basic.append(monthMap.get("8月")+"|");
		basic.append(monthMap.get("9月")+"|");
		basic.append(monthMap.get("10月")+"|");
		basic.append(monthMap.get("11月")+"|");
		basic.append(monthMap.get("12月")+"|");
		
		return basic;
	}
	
	/**
	 * 设置外部月份初始值
	 * @return map
	 */
	public HashMap getInitialOutsideMonthMap()
	{
		HashMap map = new HashMap();
		map.put("Jan", "");
		map.put("Feb", "");
		map.put("Mar", "");
		map.put("Apr", "");
		map.put("May", "");
		map.put("Jun", "");
		map.put("Jul", "");
		map.put("Aug", "");
		map.put("Sep", "");
		map.put("Oct", "");
		map.put("Nov", "");
		map.put("Dec", "");
		
		return map;
	}
	
	/**
	 * 设置外部月份excel数据值
	 * @return String basic
	 */
	public StringBuffer setOutsideMonthMap(StringBuffer basic , HashMap monthMap)
	{
		basic.append(monthMap.get("Jan")+"|");
		basic.append(monthMap.get("Feb")+"|");
		basic.append(monthMap.get("Mar")+"|");
		basic.append(monthMap.get("Apr")+"|");
		basic.append(monthMap.get("May")+"|");
		basic.append(monthMap.get("Jun")+"|");
		basic.append(monthMap.get("Jul")+"|");
		basic.append(monthMap.get("Aug")+"|");
		basic.append(monthMap.get("Sep")+"|");
		basic.append(monthMap.get("Oct")+"|");
		basic.append(monthMap.get("Nov")+"|");
		basic.append(monthMap.get("Dec")+"|");
		
		return basic;
	}

}
