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
 #	2013-4-15	zhangyn  		Ini		�Ի���ѡ��Ҫ���������				   
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
	private String title = "������ͷ�ƻ�";
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

		
		itemInfoPanel.add("1.1.left.center",new JLabel("ѡ�������ͣ�"));
		itemInfoPanel.add("1.2.left.center", chkInside);
		itemInfoPanel.add("1.3.left.center", chkOutside);
		
		JButton btnConfirm = new JButton("����");
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
					MessageBox.post("��ѡ��Ҫ��������ͣ���ִ�е���!","Information",MessageBox.WARNING);
				}else{
					importPersonPlanDataHandler.setImportList(importList);
					dispose();
					doImport();
				}
				
			}
		});
		
		JButton btnCancel = new JButton("ȡ��");
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
		Dimension screen = getToolkit().getScreenSize(); // �õ���Ļ�ߴ�
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
							if(monthvalue.equals(""))//���һ��������ûֵ�����󲻱���
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
							if(monthvalue.equals(""))//���һ��������ûֵ�����󲻱���
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
			dataNoValue.append("Ŀǰ������ǣ�"+filename+"\r\n"+"\r\n");
			if(error.length()>0)
			{
				if(error.equals(SearchImportPersonPlan)){
					dataNoValue.append("û�ҵ� " + error + " ��ѯ������������ϵϵͳ����Ա"+"\r\n"+"\r\n");
					
				}else{
					dataNoValue.append("������Ϣ��"+"\r\n");
					dataNoValue.append("û���ҵ��飺"+error+"�����ٴε��룡"+"\r\n"+"\r\n");
				}
			}
			if(insideNoValueList.size()>0||outsideNoValueList.size()>0){
				dataNoValue.append("������Ϣ��"+"\r\n");
				if(insideNoValueList.size()>0)
				{
					dataNoValue.append(insideSheet+":��");
					for(int i = 0; i<insideNoValueList.size();i++)
					{
						dataNoValue.append(insideNoValueList.get(i)+",");
					}
					dataNoValue.append("�����ݲ������������ٴε��룡"+"\r\n"+"\r\n");
				}
				if(outsideNoValueList.size()>0)
				{
					dataNoValue.append(outsideSheet+":��");
					for(int i = 0; i<outsideNoValueList.size();i++)
					{
						dataNoValue.append(outsideNoValueList.get(i)+",");
					}
					dataNoValue.append("�����ݲ������������ٴε��룡"+"\r\n"+"\r\n");
				}
				
			}
			if(insideNoValueList.size()==0 && outsideNoValueList.size()==0 && error.length()==0)
			{
				dataNoValue.append("������ɣ�"+"\r\n");
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
	 * �����ڲ��·ݳ�ʼֵ
	 * @return map
	 */
	public HashMap getInitialInsideMonthMap()
	{
		HashMap map = new HashMap();
		map.put("1��", "");
		map.put("2��", "");
		map.put("3��", "");
		map.put("4��", "");
		map.put("5��", "");
		map.put("6��", "");
		map.put("7��", "");
		map.put("8��", "");
		map.put("9��", "");
		map.put("10��", "");
		map.put("11��", "");
		map.put("12��", "");
		
		return map;
	}
	
	/**
	 * �����ڲ��·�excel����ֵ
	 * @return StringBuffer basic
	 */
	public StringBuffer setInsideMonthMap(StringBuffer basic , HashMap monthMap)
	{
		basic.append(monthMap.get("1��")+"|");
		basic.append(monthMap.get("2��")+"|");
		basic.append(monthMap.get("3��")+"|");
		basic.append(monthMap.get("4��")+"|");
		basic.append(monthMap.get("5��")+"|");
		basic.append(monthMap.get("6��")+"|");
		basic.append(monthMap.get("7��")+"|");
		basic.append(monthMap.get("8��")+"|");
		basic.append(monthMap.get("9��")+"|");
		basic.append(monthMap.get("10��")+"|");
		basic.append(monthMap.get("11��")+"|");
		basic.append(monthMap.get("12��")+"|");
		
		return basic;
	}
	
	/**
	 * �����ⲿ�·ݳ�ʼֵ
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
	 * �����ⲿ�·�excel����ֵ
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
