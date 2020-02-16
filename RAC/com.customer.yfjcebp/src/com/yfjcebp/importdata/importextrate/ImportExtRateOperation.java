package com.yfjcebp.importdata.importextrate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JList;

import org.apache.poi.ss.usermodel.Sheet;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentReleaseStatusType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.messagebox.MessageBoxFactory;
import com.yfjcebp.importdata.utils.MessageDialog;
import com.yfjcebp.importdata.utils.OriginUtil;

public class ImportExtRateOperation extends AbstractAIFOperation{
	
	private TCSession session;
	private File excelFile;	
	/**
	 * 首选项：YFJC_ImportExtRate_Props
	 * 说明：用于对excel的列的识别、关联的文件夹、日期格式的确定
	 * 		列的识别，如：3=jci6_GID		列名=值对应的属性名
	 * 		列的识别存在4个特殊，分别对应的是
	 * 				‘公司’			MajorCOMPANY
	 * 				‘姓名’			MajorNAME
	 * 				‘身份证号码’		MajorIDNUMBER
	 * 				‘完成日期’		MajorFINISHDATE
	 * 				‘费率状态’		ManjorRateStatus
	 * 		关联的文件夹，如：AssociatedFolder=newFolder		AssociatedFolder=ExtRate存放的文件夹
	 * 		日期格式的确定，如：DateFormat=yyyy.MM.dd		DateFormat=日期格式
	 */
	private String m_PreferenceExtRateProperty="YFJC_ImportExtRate_Props";
	private String ExtRateType = "JCI6_ExtRate";
	
	private TCComponentDataset dataset;
	
	//首选项值
	private String tcFolderName;	//文件夹名称
	private String dateFormat;		//日期格式
	private HashMap<String, Integer> excelProps;	//需要添加的属性
	private Integer componyColNum;		//‘公司’所在的列
	private Integer nameColNum;			//‘姓名’所在的列
	private Integer IDNumberColNum;		//‘身份证号码’所在的列
	private Integer finishDateColNum;	//‘完成日期’所在的列
	private Integer rateStatus;			//‘费率状态’所在的列
	
	//excel值
	private StringBuilder errorStrs;				//excel错误内容
	private ArrayList<List<String>> addLines;		//新增人员
	private ArrayList<List<String>> updateLines;	//更新人员
	private ArrayList<List<String>> leaveLines;		//离职人员
	
	//
	private TCComponentFolder specificateFolder;
	
	private String[] titleAlin = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V",
			"W","X","Y","Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ"};
	
	public ImportExtRateOperation(TCSession session,File excelFile,TCComponentDataset dataset) {
		this.session = session;
		this.excelFile = excelFile;
		this.dataset = dataset;
	}

	@Override
	public void executeOperation(){
		//初始化首选项值
		TCPreferenceService preference = session.getPreferenceService();
		HashMap<String,String> ExtRateProperty_pref = OriginUtil.getPreferenceMap(preference, m_PreferenceExtRateProperty);
		if (ExtRateProperty_pref.size() == 0) {
			return;
		}
		excelProps = new HashMap<String, Integer>();
		Iterator<Entry<String, String>> iter = ExtRateProperty_pref.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			String value = entry.getValue();
			if (key.equalsIgnoreCase("AssociatedFolder")) {
				tcFolderName = value.trim();
			}else if (key.equalsIgnoreCase("DateFormat")) {
				dateFormat = value.trim();
			}else if (key.matches("\\d+")) {
				//是整数
				if (value.trim().equalsIgnoreCase("MajorCOMPANY")) {
					componyColNum = Integer.parseInt(key);
				}else if (value.trim().equalsIgnoreCase("MajorNAME")) {
					nameColNum = Integer.parseInt(key);
				}else if (value.trim().equalsIgnoreCase("MajorIDNUMBER")) {
					IDNumberColNum = Integer.parseInt(key);
				}else if (value.trim().equalsIgnoreCase("MajorFINISHDATE")) {
					finishDateColNum = Integer.parseInt(key);
				}else if (value.trim().equalsIgnoreCase("ManjorRateStatus")) {
					rateStatus = Integer.parseInt(key);
				}else{
					excelProps.put(value, Integer.parseInt(key));
				}
			}else if (key.length() < 3 && !key.matches("\\d+")) {
				int tmpColNum = 0;
				for (int i = 0; i < titleAlin.length; i++) {
					if (titleAlin[i].equals(key.trim())) {
						tmpColNum = i;
						break;
					}
				}
				if (value.trim().equalsIgnoreCase("MajorCOMPANY")) {
					componyColNum = tmpColNum;
				}else if (value.trim().equalsIgnoreCase("MajorNAME")) {
					nameColNum = tmpColNum;
				}else if (value.trim().equalsIgnoreCase("MajorIDNUMBER")) {
					IDNumberColNum = tmpColNum;
				}else if (value.trim().equalsIgnoreCase("MajorFINISHDATE")) {
					finishDateColNum = tmpColNum;
				}else if (value.trim().equalsIgnoreCase("ManjorRateStatus")) {
					rateStatus = tmpColNum;
				}else{
					excelProps.put(value, tmpColNum);
				}
			}
		}
		
		/**
		 * 初始化excel值
		 * 如果‘身份证号码’或者‘姓名’为空，跳过
		 * 如果‘完成时间’为空，跳过
		 */
		POIExcelParser parser = null;
		try {
			parser = new POIExcelParser(excelFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parser.setInit(0, 0, 1);
		List<String> line = null;
		errorStrs = new StringBuilder();
		addLines = new ArrayList<List<String>>();
		updateLines = new ArrayList<List<String>>();
		leaveLines = new ArrayList<List<String>>();
		ArrayList<Integer> finishRowNum = new ArrayList<Integer>(); //保存已完成内容
		//创建测试用Item
		TCComponentItem testItem = createTestItem();
		//line = parser.parseLine();
		
		////System.out.println("ww line.size()-->"+line.size());
		while ((line = parser.parseLine()) != null) {
			int currentLine = parser.startRow-1;
			String currentCompony = line.get(componyColNum).trim();
			String currentName = line.get(nameColNum).trim();
			String currentIDNumber = line.get(IDNumberColNum).trim();
			String currentFinishDate = line.get(finishDateColNum).trim();
			String currentRateStatus = line.get(rateStatus).trim();
			
			//System.out.println("currentName-->"+currentName);
			//System.out.println("currentFinishDate-->"+currentFinishDate);
			if (currentName.isEmpty() || currentIDNumber.isEmpty()) {
				continue;
			}else if (!currentFinishDate.isEmpty()) {
				continue;
			}
			
			//System.out.println("currentCompony-->"+currentCompony);
			if (currentCompony.isEmpty() || currentRateStatus.isEmpty()) {
				errorStrs.append("第").append(currentLine).append("行的主要属性值存在无值的现象:‘公司’、‘费率状态’\n");
				continue;
			}
			
			//System.out.println("tcFolderName-->"+tcFolderName);
			if (tcFolderName == null || tcFolderName.isEmpty()) {
				
			}else {
				try {
					specificateFolder = getNeedFolder(tcFolderName);
					
					//System.out.println("specificateFolder-->"+specificateFolder);
				} catch (TCException e) {
					MessageBox.post("Home下未找到需要的TC文件夹："+tcFolderName, "WARNING", MessageBox.WARNING);
					e.printStackTrace();
				}
				if (specificateFolder == null) {
					return;
				}
				
			}
			
			//System.out.println("currentRateStatus-->"+currentRateStatus);
			if (currentRateStatus.equals("新增")) {
				//System.out.print("Create:"+parser.startRow);
				
				addLines.add(line);
				printList(line);
				String currentComponyName = line.get(componyColNum);
				currentComponyName = getidFromCompeny(currentComponyName);
				//System.out.println("查询公司："+currentComponyName);
				TCComponentUserType userType = null;
				try {
					userType = (TCComponentUserType) session.getTypeComponent("User");
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				TCComponentUser compenyUser = null;
				try {
					compenyUser = userType.find(currentComponyName);
				} catch (TCException e) {
					errorStrs.append("第").append(currentLine+1).append("行：").append(currentIDNumber).append("未找到需要的公司！").append("\n");
				}
//				if (compenyUser == null) {
//					errorStrs.append("第").append(currentLine+1).append("行：").append(currentIDNumber).append("未找到需要的公司！").append("\n");
//				}
				createExtRateTest(testItem, line, currentLine);
				
			}else if (currentRateStatus.equals("离职")) {
				//System.out.print("Leave:"+parser.startRow);
				leaveLines.add(line);
				printList(line);
				try {
					leaveExtRate(line, currentLine);
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (currentRateStatus.equals("更新")) {
				//System.out.print("Update:"+parser.startRow);
				updateLines.add(line);
				printList(line);
				String currentComponyName = line.get(componyColNum);
				currentComponyName = getidFromCompeny(currentComponyName);
				//System.out.println("查询公司："+currentComponyName);
				TCComponentUserType userType = null;
				try {
					userType = (TCComponentUserType) session.getTypeComponent("User");
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				TCComponentUser compenyUser;
				try {
					compenyUser = userType.find(currentComponyName);
				} catch (TCException e) {
					errorStrs.append("第").append(currentLine+1).append("行：").append(currentIDNumber).append("未找到需要的公司！").append("\n");
				}
//				if (compenyUser == null) {
//					errorStrs.append("第").append(currentLine+1).append("行：").append(currentIDNumber).append("未找到需要的公司！").append("\n");
//				}
				updateExtRateTest(testItem, line, currentLine);
			}
			
		}
		
		
		

		if (errorStrs.length() > 5) {
			new MessageDialog(errorStrs.toString());
//			MessageBox.post(errorStrs.toString(), "ERROR", MessageBox.ERROR);
			return;
		}
		
		for (List<String> list : addLines) {
			TCComponentItem newExrRateItem = createExtRate(list, 000);
			TCUserService userService = session.getUserService();
			if (newExrRateItem != null && specificateFolder != null) {
				try {
					userService.call("open_or_close_pass", new Object[] { 1 });
					newExrRateItem.getLatestItemRevision().setStringProperty("object_desc", "1");
					userService.call("open_or_close_pass", new Object[] { 0 });
					specificateFolder.add("contents", newExrRateItem);
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					specificateFolder.refresh();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//增加公司
				String currentComponyName = list.get(componyColNum);
				currentComponyName = getidFromCompeny(currentComponyName);
				//System.out.println("查询公司："+currentComponyName);
				TCComponentUserType userType = null;
				try {
					userType = (TCComponentUserType) session.getTypeComponent("User");
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				TCComponentUser compenyUser;
				try {
					compenyUser = userType.find(currentComponyName);
					newExrRateItem.add("IMAN_reference", compenyUser);
					newExrRateItem.refresh();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		for (List<String> list : leaveLines) {
			//判断的时候已操作
		}
		
		for (List<String> list : updateLines) {
			TCComponentItem newExrRateItem = updateExtRateRevision(list, 000);
			TCUserService userService = session.getUserService();
			TCComponent[] refs = null;
			try {
				userService.call("open_or_close_pass", new Object[] { 1 });
				newExrRateItem.getLatestItemRevision().setStringProperty("object_desc", "1");
				userService.call("open_or_close_pass", new Object[] { 0 });
				refs = newExrRateItem.getReferenceListProperty("IMAN_reference");
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(refs == null || refs.length == 0){
				//增加公司
				String currentComponyName = list.get(componyColNum);
				currentComponyName = getidFromCompeny(currentComponyName);
				//System.out.println("查询公司："+currentComponyName);
				TCComponentUserType userType = null;
				try {
					userType = (TCComponentUserType) session.getTypeComponent("User");
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				TCComponentUser compenyUser = null;
				try {
					compenyUser = userType.find(currentComponyName);
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					newExrRateItem.add("IMAN_reference", compenyUser);
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					newExrRateItem.refresh();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if (errorStrs.length() > 5) {
			new MessageDialog(errorStrs.toString());
//			MessageBox.post(errorStrs.toString(), "ERROR", MessageBox.ERROR);
			return;
		}
		
		MessageBox.post("ExtRate导入完毕！", "INFORMATION", MessageBox.INFORMATION);
		
	}
	
	/**
	 * 创建测试用
	 * @return
	 */
	private TCComponentItem createTestItem(){
		TCComponentItem newItem = null;
		TCComponentItemType itemType =  null;
		try {
			itemType = (TCComponentItemType) session.getTypeComponent(ExtRateType);
		} catch (TCException e) {
			//System.out.println("没有找到可创建的Item：JCI6_ExtRate");
			e.printStackTrace();
		}
		
		TCComponentItem[] findItems = null;
		try {
			findItems = itemType.findItems("000000111111112222");
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			//System.out.println(e1.getDetailsMessage());
		}
		
		if (findItems != null && findItems.length > 0) {
			return findItems[0];
		}
		
		TCComponentItem newExtRate = null;
		try {
			newExtRate = itemType.create("000000111111112222", "A", ExtRateType, "测试用", "", null);
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return newExtRate;
	}
	
	//创建测试
	private void createExtRateTest(TCComponentItem testItem ,List<String> line,int currenRowNum){
		TCComponentItem newItem = testItem;
		TCComponentItemRevision newExtRateRevision = null;
		try {
			newExtRateRevision = testItem.getLatestItemRevision();
		} catch (TCException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		TCComponentItemType itemType =  null;
		try {
			itemType = (TCComponentItemType) session.getTypeComponent(ExtRateType);
		} catch (TCException e) {
			//System.out.println("没有找到可创建的Item：JCI6_ExtRate");
			e.printStackTrace();
		}
		
		String currentName = line.get(nameColNum).trim();
		String currentIDNumber = line.get(IDNumberColNum).trim();
		
		TCComponentItem[] findItems = null;
		try {
			findItems = itemType.findItems(currentIDNumber);
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			//System.out.println(e1.getDetailsMessage());
		}
		if (findItems != null && findItems.length != 0) {
			errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("在TC中已存在，请使用更新操作！").append("\n");
		}
		
		Set<String> propKeys = excelProps.keySet();
		for (String string : propKeys) {
			//System.out.println("属性判断："+string);
			int colNum = excelProps.get(string);
			TCProperty tmpTCProp = null;
			try {
				tmpTCProp = newExtRateRevision.getTCProperty(string);
			} catch (TCException e1) {
				// TODO Auto-generated catch block
				//System.out.println(e1.getDetailsMessage());
				errorStrs.append("对象没有此属性，首选项YFJC_ImportExtRate_Props设置错误：").append(string).append("\n");
				continue;
			}
			if (tmpTCProp.isType(TCProperty.PROP_string)) {
				try {
					newExtRateRevision.setProperty(string, line.get(colNum));
				} catch (TCException e) {
					//System.out.println(e.getDetailsMessage());
					errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_date)) {
				SimpleDateFormat format = new SimpleDateFormat(dateFormat);
				try {
					//System.out.println("时间转换："+line.get(colNum));
					Date date = format.parse(line.get(colNum));
					try {
						newExtRateRevision.setDateProperty(string, date);
					} catch (TCException e) {
						//System.out.println(e.getDetailsMessage());
						errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
					}
				} catch (ParseException e) {
					
					//System.out.println(e.getMessage());
					errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_double)) {
				double tmpValue = Double.valueOf(line.get(colNum));
				try {
					newExtRateRevision.setDoubleProperty(string, tmpValue);
				} catch (TCException e) {
					//System.out.println(e.getDetailsMessage());
					errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_int)) {
				int tmpValue = Integer.valueOf(line.get(colNum));
				try {
					newExtRateRevision.setIntProperty(string, tmpValue);
				} catch (TCException e) {
					//System.out.println(e.getDetailsMessage());
					errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
				}
			}else if (tmpTCProp.getPropertyDescriptor().getLOV() != null) {

				if (string.equals("jci6_Division") ) {
					String currentValue = line.get(colNum);
					TCComponentListOfValues jci6_Division = tmpTCProp.getPropertyDescriptor().getLOV();
					ListOfValuesInfo list = null;
					try {
						list = jci6_Division.getListOfValues();
					} catch (TCException e) {
						//System.out.println(e.getDetailsMessage());
						errorStrs.append("jci6_Division获取LOV失败").append("\n");
						continue;
					}
					String[] lovValues = list.getLOVDisplayValues();
					for (String string2 : lovValues) {
						if (string2.contains(currentValue.trim())) {
							currentValue = string2;
							break;
						}
					}
					TCComponentGroupType groupType = null;
					try {
						groupType = (TCComponentGroupType)session.getTypeComponent("Group");
					} catch (TCException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					TCComponentGroup currentGroup;
					try {
						currentGroup = groupType.find(currentValue);
					} catch (TCException e) {
						// TODO Auto-generated catch block
						//System.out.println(e.getDetailsMessage());
						errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
						continue;
					}
					try {
						newExtRateRevision.setReferenceProperty(string, currentGroup);
					} catch (TCException e) {
						//System.out.println(e.getDetailsMessage());
						errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
					}
				}else if (string.equals("jci6_Section")) {
					String currentValue = line.get(colNum);
					if (currentValue.trim().length()> 2) {
						TCComponentListOfValues jci6_Division = tmpTCProp.getPropertyDescriptor().getLOV();
						ListOfValuesInfo list = null;
						try {
							list = jci6_Division.getListOfValues();
						} catch (TCException e) {
							//System.out.println(e.getDetailsMessage());
							errorStrs.append("jci6_Division获取LOV失败").append("\n");
							continue;
						}
						String[] lovValues = list.getLOVDisplayValues();
						for (String string2 : lovValues) {
							if (string2.contains(currentValue.trim())) {
								currentValue = string2;
								break;
							}
						}
						TCComponentGroupType groupType = null;
						try {
							groupType = (TCComponentGroupType)session.getTypeComponent("Group");
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						TCComponentGroup currentGroup;
						try {
							currentGroup = groupType.find(currentValue);
						} catch (TCException e) {
							// TODO Auto-generated catch block
							//System.out.println(e.getDetailsMessage());
							errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
							continue;
						}
						try {
							newExtRateRevision.setReferenceProperty(string, currentGroup);
						} catch (TCException e) {
							//System.out.println(e.getDetailsMessage());
							errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
						}
					}
				}
			
			}
		}
		
	}
	
	//更改测试
	private void updateExtRateTest(TCComponentItem testItem ,List<String> line,int currenRowNum){
		TCComponentItem newItem = testItem;
		TCComponentItemRevision newExtRateRevision = null;
		try {
			newExtRateRevision = testItem.getLatestItemRevision();
		} catch (TCException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		TCComponentItemType itemType =  null;
		try {
			itemType = (TCComponentItemType) session.getTypeComponent(ExtRateType);
		} catch (TCException e) {
			//System.out.println("没有找到可创建的Item：JCI6_ExtRate");
			e.printStackTrace();
		}
		
		String currentName = line.get(nameColNum).trim();
		String currentIDNumber = line.get(IDNumberColNum).trim();
		
		TCComponentItem[] items;
		try {
			items = itemType.findItems(currentIDNumber);
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			//System.out.println(e1.getDetailsMessage());
			errorStrs.append("第").append(currenRowNum+1).append("行：").append("在查询TC是否存在").append(currentIDNumber).append("时出错！").append("\n");
			return ;
		}
		if (items.length == 0) {
			errorStrs.append("第").append(currenRowNum+1).append("行：").append("TC 没有找到需要更新的ExtRate对象：").append(currentIDNumber).append("\n");
			return ;
		}else if (items.length > 1) {
			errorStrs.append("第").append(currenRowNum+1).append("行：").append("TC 找到多个ExtRate对象，非唯一："+currentIDNumber).append("\n");
			return ;
		}
		
		Set<String> propKeys = excelProps.keySet();
		for (String string : propKeys) {
			//System.out.println("属性判断："+string);
			int colNum = excelProps.get(string);
			TCProperty tmpTCProp = null;
			try {
				tmpTCProp = newExtRateRevision.getTCProperty(string);
			} catch (TCException e1) {
				// TODO Auto-generated catch block
				//System.out.println(e1.getDetailsMessage());
				errorStrs.append("对象没有此属性，首选项YFJC_ImportExtRate_Props设置错误：").append(string).append("\n");
				continue;
			}
			if (tmpTCProp.isType(TCProperty.PROP_string)) {
				try {
					newExtRateRevision.setProperty(string, line.get(colNum));
				} catch (TCException e) {
					//System.out.println(e.getDetailsMessage());
					errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_date)) {
				SimpleDateFormat format = new SimpleDateFormat(dateFormat);
				try {
					//System.out.println("时间转换："+line.get(colNum));
					Date date = format.parse(line.get(colNum));
					try {
						newExtRateRevision.setDateProperty(string, date);
					} catch (TCException e) {
						//System.out.println(e.getDetailsMessage());
						errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
					}
				} catch (ParseException e) {
					
					//System.out.println(e.getMessage());
					errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_double)) {
				double tmpValue = Double.valueOf(line.get(colNum));
				try {
					newExtRateRevision.setDoubleProperty(string, tmpValue);
				} catch (TCException e) {
					//System.out.println(e.getDetailsMessage());
					errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_int)) {
				int tmpValue = Integer.valueOf(line.get(colNum));
				try {
					newExtRateRevision.setIntProperty(string, tmpValue);
				} catch (TCException e) {
					//System.out.println(e.getDetailsMessage());
					errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
				}
			}else if (tmpTCProp.getPropertyDescriptor().getLOV() != null) {

				if (string.equals("jci6_Division") ) {
					String currentValue = line.get(colNum);
					TCComponentListOfValues jci6_Division = tmpTCProp.getPropertyDescriptor().getLOV();
					ListOfValuesInfo list = null;
					try {
						list = jci6_Division.getListOfValues();
					} catch (TCException e) {
						//System.out.println(e.getDetailsMessage());
						errorStrs.append("jci6_Division获取LOV失败").append("\n");
						continue;
					}
					String[] lovValues = list.getLOVDisplayValues();
					for (String string2 : lovValues) {
						if (string2.contains(currentValue.trim())) {
							currentValue = string2;
							break;
						}
					}
					TCComponentGroupType groupType = null;
					try {
						groupType = (TCComponentGroupType)session.getTypeComponent("Group");
					} catch (TCException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					TCComponentGroup currentGroup;
					try {
						currentGroup = groupType.find(currentValue);
					} catch (TCException e) {
						// TODO Auto-generated catch block
						//System.out.println(e.getDetailsMessage());
						errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
						continue;
					}
					try {
						newExtRateRevision.setReferenceProperty(string, currentGroup);
					} catch (TCException e) {
						//System.out.println(e.getDetailsMessage());
						errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
					}
				}else if (string.equals("jci6_Section")) {
					String currentValue = line.get(colNum);
					if (currentValue.trim().length()> 2) {
						TCComponentListOfValues jci6_Division = tmpTCProp.getPropertyDescriptor().getLOV();
						ListOfValuesInfo list = null;
						try {
							list = jci6_Division.getListOfValues();
						} catch (TCException e) {
							//System.out.println(e.getDetailsMessage());
							errorStrs.append("jci6_Division获取LOV失败").append("\n");
							continue;
						}
						String[] lovValues = list.getLOVDisplayValues();
						for (String string2 : lovValues) {
							if (string2.contains(currentValue.trim())) {
								currentValue = string2;
								break;
							}
						}
						TCComponentGroupType groupType = null;
						try {
							groupType = (TCComponentGroupType)session.getTypeComponent("Group");
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						TCComponentGroup currentGroup;
						try {
							currentGroup = groupType.find(currentValue);
						} catch (TCException e) {
							// TODO Auto-generated catch block
							//System.out.println(e.getDetailsMessage());
							errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
							continue;
						}
						try {
							newExtRateRevision.setReferenceProperty(string, currentGroup);
						} catch (TCException e) {
							//System.out.println(e.getDetailsMessage());
							errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("属性值存在问题，将无法写入TC：").append(string).append("\n");
						}
					}
				}
			
			}
		}
		
	}
	
	//创建
	private TCComponentItem createExtRate(List<String> line,int currenRowNum){
		TCComponentItem newItem = null;
		TCComponentItemType itemType =  null;
		try {
			itemType = (TCComponentItemType) session.getTypeComponent(ExtRateType);
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		String currentName = line.get(nameColNum).trim();
		String currentIDNumber = line.get(IDNumberColNum).trim();
		
		TCComponentItem newExtRate = null;
		try {
			newExtRate = itemType.create(currentIDNumber, "A", ExtRateType, currentName, "", null);
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (newExtRate != null) {
			newItem = newExtRate;
		}else{
			errorStrs.append(currentIDNumber).append("在TC中创建失败！").append("\n");
			return null;
		}
		
		TCComponentItemRevision newExtRateRevision;
		try {
			TCUserService userService = session.getUserService();
			userService.call("open_or_close_pass", new Object[] { 1 });
			newItem.getLatestItemRevision().setStringProperty("object_desc", "1");
			userService.call("open_or_close_pass", new Object[] { 0 });
			newExtRateRevision = newItem.getLatestItemRevision();
			newExtRateRevision.lock();
			
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			//System.out.println(e1.getDetailsMessage());
			errorStrs.append(currentIDNumber).append("在TC中创建后，获取版本失败！").append("\n");
			try {
				newItem.delete();
			} catch (TCException e) {
				// TODO Auto-generated catch block
				//System.out.println(e1.getDetailsMessage());
				errorStrs.append(currentIDNumber).append("在TC中创建后，获取版本失败,后删除已创建item失败！").append("\n");
				return null;
			}
			return null;
		}
		Set<String> propKeys = excelProps.keySet();
		for (String string : propKeys) {
			//System.out.println("属性设置："+string);
			int colNum = excelProps.get(string);
			TCProperty tmpTCProp = null;
			try {
				tmpTCProp = newExtRateRevision.getTCProperty(string);
			} catch (TCException e1) {
				// TODO Auto-generated catch block
				//System.out.println(e1.getDetailsMessage());
			}
			
			if (tmpTCProp.isType(TCProperty.PROP_string)) {
				try {
					newExtRateRevision.setProperty(string, line.get(colNum));
				} catch (TCException e) {
					errorStrs.append("第").append(currenRowNum+1).append("行：创建").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
					//System.out.println(e.getDetailsMessage());
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_date)) {
				SimpleDateFormat format = new SimpleDateFormat(dateFormat);
				try {
					//System.out.println("时间转换："+line.get(colNum));
					Date date = format.parse(line.get(colNum));
					try {
						newExtRateRevision.setDateProperty(string, date);
					} catch (TCException e) {
						errorStrs.append("第").append(currenRowNum+1).append("行：创建").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
						//System.out.println(e.getDetailsMessage());
					}
				} catch (ParseException e) {
					errorStrs.append("第").append(currenRowNum+1).append("行：创建").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
					//System.out.println(e.getMessage());
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_double)) {
				double tmpValue = Double.valueOf(line.get(colNum));
				try {
					newExtRateRevision.setDoubleProperty(string, tmpValue);
				} catch (TCException e) {
					//System.out.println(e.getDetailsMessage());
					errorStrs.append("第").append(currenRowNum+1).append("行：创建").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_int)) {
				int tmpValue = Integer.valueOf(line.get(colNum));
				try {
					newExtRateRevision.setIntProperty(string, tmpValue);
				} catch (TCException e) {
					//System.out.println(e.getDetailsMessage());
					errorStrs.append("第").append(currenRowNum+1).append("行：创建").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
				}
			}else if (tmpTCProp.getPropertyDescriptor().getLOV() != null) {
				if (string.equals("jci6_Division") ) {
					String currentValue = line.get(colNum);
					TCComponentListOfValues jci6_Division = tmpTCProp.getPropertyDescriptor().getLOV();
					ListOfValuesInfo list = null;
					try {
						list = jci6_Division.getListOfValues();
					} catch (TCException e) {
						e.printStackTrace();
					}
					String[] lovValues = list.getLOVDisplayValues();
					for (String string2 : lovValues) {
						if (string2.contains(currentValue.trim())) {
							currentValue = string2;
							break;
						}
					}
					TCComponentGroupType groupType = null;
					try {
						groupType = (TCComponentGroupType)session.getTypeComponent("Group");
					} catch (TCException e1) {
						e1.printStackTrace();
					}
					TCComponentGroup currentGroup = null;
					try {
						currentGroup = groupType.find(currentValue);
					} catch (TCException e) {
						e.printStackTrace();
					}
					try {
						newExtRateRevision.setReferenceProperty(string, currentGroup);
					} catch (TCException e) {
						e.printStackTrace();
					}
				}else if (string.equals("jci6_Section")) {
					String currentValue = line.get(colNum);
					if (currentValue.trim().length()> 2) {
						TCComponentListOfValues jci6_Division = tmpTCProp.getPropertyDescriptor().getLOV();
						ListOfValuesInfo list = null;
						try {
							list = jci6_Division.getListOfValues();
						} catch (TCException e) {
							e.printStackTrace();
						}
						String[] lovValues = list.getLOVDisplayValues();
						for (String string2 : lovValues) {
							if (string2.contains(currentValue.trim())) {
								currentValue = string2;
								break;
							}
						}
						TCComponentGroupType groupType = null;
						try {
							groupType = (TCComponentGroupType)session.getTypeComponent("Group");
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						TCComponentGroup currentGroup = null;
						try {
							currentGroup = groupType.find(currentValue);
						} catch (TCException e) {
							e.printStackTrace();
						}
						try {
							newExtRateRevision.setReferenceProperty(string, currentGroup);
						} catch (TCException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		try {
			newExtRateRevision.setStringProperty("object_desc", "1");
			newExtRateRevision.save();
			newExtRateRevision.unlock();
			newExtRateRevision.refresh();
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			releaseObject(session, newExtRateRevision, "Pass");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newItem;
	}
	//更新
	private TCComponentItem updateExtRateRevision(List<String> line,int currenRowNum){
		TCComponentItemType itemType = null;
		try {
			itemType = (TCComponentItemType) session.getTypeComponent(ExtRateType);
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String currentName = line.get(nameColNum).trim();
		String currentIDNumber = line.get(IDNumberColNum).trim();
		TCComponentItem[] items = null;
		try {
			items = itemType.findItems(currentIDNumber);
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			TCComponentItem currentExtRateItem = items[0];
			TCComponentItemRevision newExtRateRevision = null;
			try {
				TCUserService userService = session.getUserService();
				userService.call("open_or_close_pass", new Object[] { 1 });
				currentExtRateItem.getLatestItemRevision().setStringProperty("object_desc", "0");
				userService.call("open_or_close_pass", new Object[] { 0 });
				newExtRateRevision = currentExtRateItem.revise(currentExtRateItem.getNewRev(), currentName, "");
				newExtRateRevision.setStringProperty("objec_desc", "1");
			} catch (TCException e1) {
				//System.out.println(e1.getDetailsMessage());
				errorStrs.append("TC升版ExtRate对象失败："+currentIDNumber).append("\n");
				return null;
			}
			try {
				newExtRateRevision.lock();
			} catch (TCException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Set<String> propKeys = excelProps.keySet();
			for (String string : propKeys) {
				//System.out.println("属性设置："+string);
				int colNum = excelProps.get(string);
				TCProperty tmpTCProp = null;
				try {
					tmpTCProp = newExtRateRevision.getTCProperty(string);
				} catch (TCException e1) {
					e1.printStackTrace();
				}
				
				if (tmpTCProp.isType(TCProperty.PROP_string)) {
					try {
						newExtRateRevision.setProperty(string, line.get(colNum));
					} catch (TCException e) {
						//System.out.println(e.getDetailsMessage());
						errorStrs.append("第").append(currenRowNum+1).append("行：升版").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
						try {
							newExtRateRevision.delete();
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							//System.out.println(e1.getDetailsMessage());
							errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
							return null;
						}
						return null;
					}
				}else if (tmpTCProp.isType(TCProperty.PROP_date)) {
					SimpleDateFormat format = new SimpleDateFormat(dateFormat);
					try {
						//System.out.println("时间转换："+line.get(colNum));
						Date date = format.parse(line.get(colNum));
						try {
							newExtRateRevision.setDateProperty(string, date);
						} catch (TCException e) {
							//System.out.println(e.getDetailsMessage());
							errorStrs.append("第").append(currenRowNum+1).append("行：升版").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
							try {
								newExtRateRevision.delete();
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								//System.out.println(e1.getDetailsMessage());
								errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
								return null;
							}
							return null;
						}
					} catch (ParseException e) {
						
						//System.out.println(e.getMessage());
						errorStrs.append("第").append(currenRowNum+1).append("行：升版").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
						try {
							newExtRateRevision.delete();
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							//System.out.println(e1.getDetailsMessage());
							errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
							return null;
						}
						return null;
					}
				}else if (tmpTCProp.isType(TCProperty.PROP_double)) {
					double tmpValue = Double.valueOf(line.get(colNum));
					try {
						newExtRateRevision.setDoubleProperty(string, tmpValue);
					} catch (TCException e) {
						//System.out.println(e.getDetailsMessage());
						errorStrs.append("第").append(currenRowNum+1).append("行：升版").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
						try {
							newExtRateRevision.delete();
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							//System.out.println(e1.getDetailsMessage());
							errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
							return null;
						}
						return null;
					}
				}else if (tmpTCProp.isType(TCProperty.PROP_int)) {
					int tmpValue = Integer.valueOf(line.get(colNum));
					try {
						newExtRateRevision.setIntProperty(string, tmpValue);
					} catch (TCException e) {
						//System.out.println(e.getDetailsMessage());
						errorStrs.append("第").append(currenRowNum+1).append("行：升版").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
						try {
							newExtRateRevision.delete();
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							//System.out.println(e1.getDetailsMessage());
							errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
							return null;
						}
						return null;
					}
				}else if (tmpTCProp.getPropertyDescriptor().getLOV() != null) {
					if (string.equals("jci6_Division") ) {
						String currentValue = line.get(colNum);
						TCComponentListOfValues jci6_Division = tmpTCProp.getPropertyDescriptor().getLOV();
						ListOfValuesInfo list = null;
						try {
							list = jci6_Division.getListOfValues();
						} catch (TCException e) {
							//System.out.println(e.getDetailsMessage());
							errorStrs.append("jci6_Division获取LOV失败").append("\n");
							try {
								newExtRateRevision.delete();
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								//System.out.println(e1.getDetailsMessage());
								errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
								return null;
							}
							return null;
						}
						String[] lovValues = list.getLOVDisplayValues();
						for (String string2 : lovValues) {
							if (string2.contains(currentValue.trim())) {
								currentValue = string2;
								break;
							}
						}
						TCComponentGroupType groupType = null;
						try {
							groupType = (TCComponentGroupType)session.getTypeComponent("Group");
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						TCComponentGroup currentGroup;
						try {
							currentGroup = groupType.find(currentValue);
						} catch (TCException e) {
							// TODO Auto-generated catch block
							//System.out.println(e.getDetailsMessage());
							errorStrs.append("第").append(currenRowNum+1).append("行：创建").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
							try {
								newExtRateRevision.delete();
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								//System.out.println(e1.getDetailsMessage());
								errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
								return null;
							}
							return null;
						}
						if (currentGroup == null) {
							try {
								newExtRateRevision.save();
								newExtRateRevision.unlock();
//							newExtRateRevision.delete();
								newExtRateRevision.delete();
							} catch (TCException e) {
								//System.out.println(e.getDetailsMessage());
								errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("在TC中创建后，设置属性失败,后删除已创建item失败！").append("\n");
								try {
									newExtRateRevision.delete();
								} catch (TCException e1) {
									// TODO Auto-generated catch block
									//System.out.println(e1.getDetailsMessage());
									errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
									return null;
								}
								return null;
							}
							errorStrs.append("第").append(currenRowNum+1).append("行：").append("创建").append(currentIDNumber).append("时，设置属性错误：").append("jci6_Division").append("\n");
							try {
								newExtRateRevision.delete();
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								//System.out.println(e1.getDetailsMessage());
								errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
								return null;
							}
							return null;
						}
						try {
							newExtRateRevision.setReferenceProperty(string, currentGroup);
						} catch (TCException e) {
							//System.out.println(e.getDetailsMessage());
							errorStrs.append("第").append(currenRowNum+1).append("行：创建").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
							try {
								newExtRateRevision.delete();
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								//System.out.println(e1.getDetailsMessage());
								errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
								return null;
							}
							return null;
						}
					}else if (string.equals("jci6_Section")) {
						String currentValue = line.get(colNum);
						if (currentValue.trim().length()> 2) {
							TCComponentListOfValues jci6_Division = tmpTCProp.getPropertyDescriptor().getLOV();
							ListOfValuesInfo list = null;
							try {
								list = jci6_Division.getListOfValues();
							} catch (TCException e) {
								//System.out.println(e.getDetailsMessage());
								errorStrs.append("jci6_Division获取LOV失败").append("\n");
								try {
									newExtRateRevision.delete();
								} catch (TCException e1) {
									// TODO Auto-generated catch block
									//System.out.println(e1.getDetailsMessage());
									errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
									return null;
								}
								return null;
							}
							String[] lovValues = list.getLOVDisplayValues();
							for (String string2 : lovValues) {
								if (string2.contains(currentValue.trim())) {
									currentValue = string2;
									break;
								}
							}
							TCComponentGroupType groupType = null;
							try {
								groupType = (TCComponentGroupType)session.getTypeComponent("Group");
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							TCComponentGroup currentGroup;
							try {
								currentGroup = groupType.find(currentValue);
							} catch (TCException e) {
								// TODO Auto-generated catch block
								//System.out.println(e.getDetailsMessage());
								errorStrs.append("第").append(currenRowNum+1).append("行：创建").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
								try {
									newExtRateRevision.delete();
								} catch (TCException e1) {
									// TODO Auto-generated catch block
									//System.out.println(e1.getDetailsMessage());
									errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
									return null;
								}
								return null;
							}
							if (currentGroup == null) {
								try {
									newExtRateRevision.save();
									newExtRateRevision.unlock();
//								newExtRateRevision.delete();
									newExtRateRevision.delete();
								} catch (TCException e) {
									//System.out.println(e.getDetailsMessage());
									errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("在TC中创建后，设置属性失败,后删除已创建item失败！").append("\n");
									try {
										newExtRateRevision.delete();
									} catch (TCException e1) {
										// TODO Auto-generated catch block
										//System.out.println(e1.getDetailsMessage());
										errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
										return null;
									}
									return null;
								}
								errorStrs.append("第").append(currenRowNum+1).append("行：").append("创建").append(currentIDNumber).append("时，设置属性错误：").append("jci6_Section").append("\n");
								try {
									newExtRateRevision.delete();
								} catch (TCException e1) {
									// TODO Auto-generated catch block
									//System.out.println(e1.getDetailsMessage());
									errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
									return null;
								}
								return null;
							}
							try {
								newExtRateRevision.setReferenceProperty(string, currentGroup);
							} catch (TCException e) {
								//System.out.println(e.getDetailsMessage());
								errorStrs.append("第").append(currenRowNum+1).append("行：创建").append(currentIDNumber).append("时，设置属性错误：").append(string).append("\n");
								try {
									newExtRateRevision.delete();
								} catch (TCException e1) {
									// TODO Auto-generated catch block
									//System.out.println(e1.getDetailsMessage());
									errorStrs.append("第").append(currenRowNum+1).append("行：").append(currentIDNumber).append("版本在TC中创建后，设置属性失败,后删除已创建item版本失败！").append("\n");
									return null;
								}
								return null;
							}
						}
					}
				}
			}
			try {
				newExtRateRevision.save();
				newExtRateRevision.unlock();
				newExtRateRevision.refresh();
			} catch (TCException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
//				releaseObject(session, newExtRateRevision, "ReleaseStatusImpl");
				releaseObject(session, newExtRateRevision, "Pass");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return currentExtRateItem;
		
		
	}
	
	///离职
	private boolean leaveExtRate(List<String> line,int currenRowNum) throws TCException{
		TCComponentItemType	itemType = (TCComponentItemType) session.getTypeComponent(ExtRateType);
		String currentName = line.get(nameColNum).trim();
		String currentIDNumber = line.get(IDNumberColNum).trim();
		TCComponentItem[] items = itemType.findItems(currentIDNumber);
		if (items.length == 0) {
			errorStrs.append("第").append(currenRowNum+1).append("行：").append("TC 没有找到需要更新的ExtRate：").append(currentIDNumber).append("\n");
			return false;
		}else if (items.length > 1) {
			errorStrs.append("第").append(currenRowNum+1).append("行：").append("TC 找到多个ExtRate对象，非唯一：").append(currentIDNumber).append("\n");
			return false;
		}else{
			TCComponentItem currentExtRateItem = items[0];
			TCComponent[] coms = currentExtRateItem.getReferenceListProperty("IMAN_reference");
			if (coms == null || coms.length == 0) {
				return true;
			}
			currentExtRateItem.cutOperation("IMAN_reference", coms);
			currentExtRateItem.refresh();
			return true;
		}
	}
	
	private void printList(List<String> line){
		StringBuilder linesb = new StringBuilder();
		linesb.append("[");
		int aa = 0;
		for (String string : line) {
			linesb.append(aa).append(":").append(string).append(",");
			aa++;
		}
		//System.out.println("printList:"+linesb.substring(0, linesb.length()-1)+"]");
	}
	
	/**
	 * 判断内容是否是正确的时间格式
	 * @param line
	 * @param errorStrs
	 * @return
	 */
	private boolean isCorrentDateFormat(List<String> line){
		boolean ifail = false;
		try {
			TCComponentItemType	itemType = (TCComponentItemType) session.getTypeComponent(ExtRateType);
			//System.out.println("itemType:"+itemType.getClassName());
			Set<String> propKeys = excelProps.keySet();
			for (String string : propKeys) {
				//System.out.println("propKeys:"+string);
				
				if (itemType.getPropertyDescriptor(string).isType(TCProperty.PROP_date)) {
					String tmpDateStr = line.get(excelProps.get(string));
					SimpleDateFormat format = new SimpleDateFormat(dateFormat);
					try {
						format.parse(tmpDateStr);
					} catch (ParseException e) {
						ifail = false;
					}
					ifail = true;
				}
			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ifail;
	}
	
	//自动发布
	private void releaseObject(TCSession session, TCComponent obj,
			String releaseType) throws Exception {
		if (obj == null)
			return;

		TCComponent[] coms = obj.getRelatedComponents("release_statuses");
		String releaseString = obj.getProperty("release_statuses");

		if (coms != null && coms.length > 0
				&& releaseString.contains(releaseType)) {
			return;
		} else {
			TCComponentReleaseStatusType rstype = (TCComponentReleaseStatusType) session
					.getTypeComponent("ReleaseStatus");
			TCComponent releasestatus = rstype.create(releaseType);
			//releasestatus.setDateProperty("date_released", new Date());
			//releasestatus.refresh();
			//releasestatus.lock();
			releasestatus.save();
			obj.refresh();
			obj.add("release_status_list", releasestatus);
			//obj.lock();
			//obj.save();
			//releasestatus.unlock();
			//obj.unlock();
		}
	}
	
	/**
	 * 用于找到本用户下的特定文件夹
	 * @param folderName
	 * @return
	 * @throws TCException 
	 */
	private TCComponentFolder getNeedFolder(String folderName) throws TCException{
		TCComponentFolder needFolder = null;
		TCComponentFolder homeFolder = session.getUser().getHomeFolder();
		TCComponent[] subFolders = homeFolder.getRelatedComponents();
		for (TCComponent tcComponent : subFolders) {
			if ( tcComponent instanceof TCComponentFolder && tcComponent.getProperty("object_name").equals(folderName)){
				needFolder = (TCComponentFolder) tcComponent;
				break;
			}
		}
		if (needFolder == null) {
			MessageBox.post("在HOME下未找到特定的文件夹："+folderName+",请创建！", "ERROR", MessageBox.ERROR);
		}
		return needFolder;
	}
	
	/**
	 * 转换公司名称为TC可识别的编号
	 * @param compenyName
	 * @return
	 */
	private String getidFromCompeny(String compenyName){
		if (compenyName.contains("(") && compenyName.contains(")")) {
			String a = compenyName.split("\\(")[1].split("\\)")[0];
			return a;
		}else if (compenyName.contains("（") && compenyName.contains("）")) {
			String a = compenyName.split("（")[1].split("）")[0];
			return a;
		}
		
		return "";
	}
	
	
	public static void main(String[] args) {
		int a = 1;
		//System.out.println(a++);
	}

}
