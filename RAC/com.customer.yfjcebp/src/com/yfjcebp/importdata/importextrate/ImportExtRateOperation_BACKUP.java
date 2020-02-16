package com.yfjcebp.importdata.importextrate;

import java.io.File;
import java.io.FileOutputStream;
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
import com.teamcenter.rac.util.MessageBox;
import com.yfjcebp.importdata.utils.OriginUtil;
import com.yfjcebp.importdata.utils.POIExcelParser;

public class ImportExtRateOperation_BACKUP extends AbstractAIFOperation{
	
	private TCSession session;
	private File excelFile;	
	/**
	 * ��ѡ�YFJC_ImportExtRate_Props
	 * ˵�������ڶ�excel���е�ʶ�𡢹������ļ��С����ڸ�ʽ��ȷ��
	 * 		�е�ʶ���磺3=jci6_GID		����=ֵ��Ӧ��������
	 * 		�е�ʶ�����4�����⣬�ֱ��Ӧ����
	 * 				����˾��			MajorCOMPANY
	 * 				��������			MajorNAME
	 * 				�����֤���롯		MajorIDNUMBER
	 * 				��������ڡ�		MajorFINISHDATE
	 * 				������״̬��		ManjorRateStatus
	 * 		�������ļ��У��磺AssociatedFolder=newFolder		AssociatedFolder=ExtRate��ŵ��ļ���
	 * 		���ڸ�ʽ��ȷ�����磺DateFormat=yyyy.MM.dd		DateFormat=���ڸ�ʽ
	 */
	private String m_PreferenceExtRateProperty="YFJC_ImportExtRate_Props";
	private String ExtRateType = "JCI6_ExtRate";
	
	private TCComponentDataset dataset;
	
	//��ѡ��ֵ
	private String tcFolderName;	//�ļ�������
	private String dateFormat;		//���ڸ�ʽ
	private HashMap<String, Integer> excelProps;	//��Ҫ��ӵ�����
	private Integer componyColNum;		//����˾�����ڵ���
	private Integer nameColNum;			//�����������ڵ���
	private Integer IDNumberColNum;		//�����֤���롯���ڵ���
	private Integer finishDateColNum;	//��������ڡ����ڵ���
	private Integer rateStatus;			//������״̬�����ڵ���
	
	//excelֵ
	private StringBuilder errorStrs;				//excel��������
	private ArrayList<List<String>> addLines;		//������Ա
	private ArrayList<List<String>> updateLines;	//������Ա
	private ArrayList<List<String>> leaveLines;		//��ְ��Ա
	
	//
	private TCComponentFolder specificateFolder;
	
	private String[] titleAlin = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V",
			"W","X","Y","Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ"};
	
	public ImportExtRateOperation_BACKUP(TCSession session,File excelFile,TCComponentDataset dataset) {
		this.session = session;
		this.excelFile = excelFile;
		this.dataset = dataset;
	}

	@Override
	public void executeOperation() throws Exception {
		//��ʼ����ѡ��ֵ
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
				//������
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
		 * ��ʼ��excelֵ
		 * ��������֤���롯���ߡ�������Ϊ�գ�����
		 * ��������ʱ�䡯Ϊ�գ�����
		 */
		POIExcelParser parser = new POIExcelParser(excelFile);
		parser.setInit(0, 0, 1);
		List<String> line = null;
		errorStrs = new StringBuilder();
		addLines = new ArrayList<List<String>>();
		updateLines = new ArrayList<List<String>>();
		leaveLines = new ArrayList<List<String>>();
		ArrayList<Integer> finishRowNum = new ArrayList<Integer>(); //�������������
		while ((line = parser.parseLine()) != null) {
			int currentLine = parser.startRow-1;
			String currentCompony = line.get(componyColNum).trim();
			String currentName = line.get(nameColNum).trim();
			String currentIDNumber = line.get(IDNumberColNum).trim();
			String currentFinishDate = line.get(finishDateColNum).trim();
			String currentRateStatus = line.get(rateStatus).trim();
			
			if (currentName.isEmpty() || currentIDNumber.isEmpty()) {
				continue;
			}else if (!currentFinishDate.isEmpty()) {
				continue;
			}
			
			if (currentCompony.isEmpty() || currentRateStatus.isEmpty()) {
				errorStrs.append("��").append(currentLine).append("�е���Ҫ����ֵ������ֵ������:����˾����������״̬��\n");
				continue;
			}
			
//			if (!isCorrentDateFormat(line)) {
//				errorStrs.append("��").append(currentLine).append("�е�ʱ���ʽ����ȷ����ȷ��ʽΪ").append(dateFormat).append("\n");
//			}
			
			if (tcFolderName == null || tcFolderName.isEmpty()) {
				
			}else {
				specificateFolder = getNeedFolder(tcFolderName);
				if (specificateFolder == null) {
					return;
				}
				
			}
			
			if (currentRateStatus.equals("����")) {
				System.out.print("Create:"+parser.startRow);
				
				addLines.add(line);
				printList(line);
				TCComponentItem newExrRateItem = createExtRate(line,currentLine);
				//��ӵ��ļ�����
				if (newExrRateItem != null && specificateFolder != null) {
					finishRowNum.add(currentLine);
					specificateFolder.add("contents", newExrRateItem);
					specificateFolder.refresh();
					//���ӹ�˾
					String currentComponyName = line.get(componyColNum);
					currentComponyName = getidFromCompeny(currentComponyName);
					System.out.println("��ѯ��˾��"+currentComponyName);
					TCComponentUserType userType = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser compenyUser = userType.find(currentComponyName);
					if (compenyUser == null) {
						errorStrs.append(currentIDNumber).append("δ�ҵ���Ҫ�Ĺ�˾��").append("\n");
					}else{
						newExrRateItem.add("IMAN_reference", compenyUser);
						newExrRateItem.refresh();
					}
				}
			}else if (currentRateStatus.equals("��ְ")) {
				System.out.print("Leave:"+parser.startRow);
				leaveLines.add(line);
				printList(line);
				boolean ifail = updateExtRateRevision(line,currentLine);
				if (ifail) {
					finishRowNum.add(currentLine);
				}
			}else if (currentRateStatus.equals("����")) {
				System.out.print("Update:"+parser.startRow);
				updateLines.add(line);
				printList(line);
				boolean ifail = leaveExtRate(line,currentLine);
				if (ifail) {
					finishRowNum.add(currentLine);
				}
			}
			
		}
		
		
		Sheet sheet = parser.getSheet();
		if (sheet != null) {
			for (Integer rowNum : finishRowNum) {
				sheet.getRow(rowNum).getCell(finishDateColNum).setCellValue(new Date());
			}
		}
		String tmpName = excelFile.getName();
		String strPath = System.getenv("TEMP");
		SimpleDateFormat tmpF = new SimpleDateFormat("yyyy_MM_ddHHmmssSSS");
		String tmpFolderName = strPath+"\\"+tmpF.format(new Date());
		String tmpExcelFileStr = tmpFolderName+"\\"+tmpName;
		new File(tmpFolderName).mkdir();
		FileOutputStream output = new FileOutputStream(new File(tmpExcelFileStr));
		parser.getSheet().getWorkbook().write(output);
		output.close();
		
		dataset.lock();
		dataset.removeNamedReference("excel");
		dataset.setFiles(new String[]{tmpExcelFileStr}, new String[]{"excel"});
		dataset.save();
		dataset.unlock();
		

		if (errorStrs.length() > 5) {
			MessageBox.post(errorStrs.toString(), "ERROR", MessageBox.ERROR);
			return;
		}
		
		//�������
//		if (addLines.size() > 0) {
//			for (List<String> list : addLines) {
//				printList(list);
//				TCComponentItem newExrRateItem = createExtRate(list);
//				//��ӵ��ļ�����
//				if (newExrRateItem != null && specificateFolder != null) {
//					specificateFolder.add("contents", newExrRateItem);
//					specificateFolder.refresh();
//					//���ӹ�˾
//					String currentComponyName = list.get(componyColNum);
//					currentComponyName = getidFromCompeny(currentComponyName);
//					System.out.println("��ѯ��˾��"+currentComponyName);
//					TCComponentUserType userType = (TCComponentUserType) session.getTypeComponent("User");
//					TCComponentUser compenyUser = userType.find(currentComponyName);
//					newExrRateItem.add("IMAN_reference", compenyUser);
//					newExrRateItem.refresh();
//				}
//				
//			}
//		}
		
		//�������
//		if (updateLines.size() > 0) {
//			for (List<String> list : updateLines) {
//				printList(list);
//				updateExtRateRevision(list);
//			}
//		}
		
		//������ְ
//		if (leaveLines.size() > 0) {
//			for (List<String> list : leaveLines) {
//				printList(list);
//				leaveExtRate(list);
//			}
//		}
		
		if (errorStrs.length() > 5) {
			MessageBox.post(errorStrs.toString(), "ERROR", MessageBox.ERROR);
			return;
		}
		
	}
	
	//����
	private TCComponentItem createExtRate(List<String> line,int currenRowNum){
		TCComponentItem newItem = null;
		TCComponentItemType itemType =  null;
		try {
			itemType = (TCComponentItemType) session.getTypeComponent(ExtRateType);
		} catch (TCException e) {
			System.out.println("û���ҵ��ɴ�����Item��JCI6_ExtRate");
			e.printStackTrace();
		}
		
		String currentName = line.get(nameColNum).trim();
		String currentIDNumber = line.get(IDNumberColNum).trim();
		TCComponentItem[] findItems = null;
		try {
			findItems = itemType.findItems(currentIDNumber);
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getDetailsMessage());
		}
		if (findItems != null && findItems.length != 0) {
			errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("��TC���Ѵ��ڣ���ʹ�ø��²�����").append("\n");
			return null;
		}
		
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
			errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("��TC�д���ʧ�ܣ�").append("\n");
			return null;
		}
		
		TCComponentItemRevision newExtRateRevision;
		try {
			newExtRateRevision = newItem.getLatestItemRevision();
			newExtRateRevision.lock();
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getDetailsMessage());
			errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("��TC�д����󣬻�ȡ�汾ʧ�ܣ�").append("\n");
			try {
				newItem.delete();
			} catch (TCException e) {
				// TODO Auto-generated catch block
				System.out.println(e1.getDetailsMessage());
				errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("��TC�д����󣬻�ȡ�汾ʧ��,��ɾ���Ѵ���itemʧ�ܣ�").append("\n");
				return null;
			}
			return null;
		}
		Set<String> propKeys = excelProps.keySet();
		for (String string : propKeys) {
			System.out.println("�������ã�"+string);
			int colNum = excelProps.get(string);
			TCProperty tmpTCProp = null;
			try {
				tmpTCProp = newExtRateRevision.getTCProperty(string);
			} catch (TCException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getDetailsMessage());
				errorStrs.append("����û�д����ԣ���ѡ��YFJC_ImportExtRate_Props���ô���").append(string).append("\n");
				try {
					newItem.delete();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getDetailsMessage());
					errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("��TC�д�������������ʧ��,��ɾ���Ѵ���itemʧ�ܣ�").append("\n");
					return null;
				}
			}
			
			if (tmpTCProp.isType(TCProperty.PROP_string)) {
				try {
					newExtRateRevision.setProperty(string, line.get(colNum));
				} catch (TCException e) {
					System.out.println(e.getDetailsMessage());
					errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
					return null;
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_date)) {
				SimpleDateFormat format = new SimpleDateFormat(dateFormat);
				try {
					System.out.println("ʱ��ת����"+line.get(colNum));
					Date date = format.parse(line.get(colNum));
					try {
						newExtRateRevision.setDateProperty(string, date);
					} catch (TCException e) {
						System.out.println(e.getDetailsMessage());
						errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
						return null;
					}
				} catch (ParseException e) {
					
					System.out.println(e.getMessage());
					errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
					return null;
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_double)) {
				double tmpValue = Double.valueOf(line.get(colNum));
				try {
					newExtRateRevision.setDoubleProperty(string, tmpValue);
				} catch (TCException e) {
					System.out.println(e.getDetailsMessage());
					errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
					return null;
				}
			}else if (tmpTCProp.isType(TCProperty.PROP_int)) {
				int tmpValue = Integer.valueOf(line.get(colNum));
				try {
					newExtRateRevision.setIntProperty(string, tmpValue);
				} catch (TCException e) {
					System.out.println(e.getDetailsMessage());
					errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
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
						System.out.println(e.getDetailsMessage());
						errorStrs.append("jci6_Division��ȡLOVʧ��").append("\n");
						return null;
					}
					String[] lovValues = list.getLOVDisplayValues();
					for (String string2 : lovValues) {
						if (string2.contains(currentValue)) {
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
						System.out.println(e.getDetailsMessage());
						errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
						return null;
					}
					if (currentGroup == null) {
						try {
							newExtRateRevision.save();
							newExtRateRevision.unlock();
//						newExtRateRevision.delete();
							newItem.delete();
						} catch (TCException e) {
							System.out.println(e.getDetailsMessage());
							errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("��TC�д�������������ʧ��,��ɾ���Ѵ���itemʧ�ܣ�").append("\n");
							return null;
						}
						errorStrs.append("��").append(currenRowNum+1).append("�У�").append("����").append(currentIDNumber).append("ʱ���������Դ���").append("jci6_Division").append("\n");
						return null;
					}
					try {
						newExtRateRevision.setReferenceProperty(string, currentGroup);
					} catch (TCException e) {
						System.out.println(e.getDetailsMessage());
						errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
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
							System.out.println(e.getDetailsMessage());
							errorStrs.append("jci6_Division��ȡLOVʧ��").append("\n");
							try {
								newItem.delete();
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								System.out.println(e1.getDetailsMessage());
								errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("��TC�д�������������ʧ��,��ɾ���Ѵ���itemʧ�ܣ�").append("\n");
								return null;
							}
							return null;
						}
						String[] lovValues = list.getLOVDisplayValues();
						for (String string2 : lovValues) {
							if (string2.contains(currentValue)) {
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
							System.out.println(e.getDetailsMessage());
							errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
							return null;
						}
						if (currentGroup == null) {
							try {
								newExtRateRevision.save();
								newExtRateRevision.unlock();
//							newExtRateRevision.delete();
								newItem.delete();
							} catch (TCException e) {
								System.out.println(e.getDetailsMessage());
								errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("��TC�д�������������ʧ��,��ɾ���Ѵ���itemʧ�ܣ�").append("\n");
								return null;
							}
							errorStrs.append("��").append(currenRowNum+1).append("�У�").append("����").append(currentIDNumber).append("ʱ���������Դ���").append("jci6_Section").append("\n");
							return null;
						}
						try {
							newExtRateRevision.setReferenceProperty(string, currentGroup);
						} catch (TCException e) {
							System.out.println(e.getDetailsMessage());
							errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
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
//			releaseObject(session, newExtRateRevision, "ReleaseStatusImpl");
			releaseObject(session, newExtRateRevision, "Pass");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newItem;
	}
	//����
	private boolean updateExtRateRevision(List<String> line,int currenRowNum){
		TCComponentItemType itemType = null;
		try {
			itemType = (TCComponentItemType) session.getTypeComponent(ExtRateType);
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String currentName = line.get(nameColNum).trim();
		String currentIDNumber = line.get(IDNumberColNum).trim();
		TCComponentItem[] items;
		try {
			items = itemType.findItems(currentIDNumber);
		} catch (TCException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getDetailsMessage());
			errorStrs.append("�ڲ�ѯTC�Ƿ����").append(currentIDNumber).append("ʱ����").append("\n");
			return false;
		}
		if (items.length == 0) {
			errorStrs.append("TC û���ҵ���Ҫ���µ�ExtRate����").append(currentIDNumber).append("\n");
			return false;
		}else if (items.length > 1) {
			errorStrs.append("TC �ҵ����ExtRate���󣬷�Ψһ��"+currentIDNumber).append("\n");
			return false;
		}else{
			TCComponentItem currentExtRateItem = items[0];
			TCComponentItemRevision newExtRateRevision = null;
			try {
				newExtRateRevision = currentExtRateItem.revise(currentExtRateItem.getNewRev(), currentName, "");
			} catch (TCException e1) {
				System.out.println(e1.getDetailsMessage());
				errorStrs.append("TC����ExtRate����ʧ�ܣ�"+currentIDNumber).append("\n");
				return false;
			}
			try {
				newExtRateRevision.lock();
			} catch (TCException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Set<String> propKeys = excelProps.keySet();
			for (String string : propKeys) {
				System.out.println("�������ã�"+string);
				int colNum = excelProps.get(string);
				TCProperty tmpTCProp = null;
				try {
					tmpTCProp = newExtRateRevision.getTCProperty(string);
				} catch (TCException e1) {
					// TODO Auto-generated catch block
					System.out.println(e1.getDetailsMessage());
					errorStrs.append("����û�д����ԣ���ѡ��YFJC_ImportExtRate_Props���ô���").append(string).append("\n");
					try {
						newExtRateRevision.delete();
					} catch (TCException e) {
						// TODO Auto-generated catch block
						System.out.println(e.getDetailsMessage());
						errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
						return false;
					}
					return false;
				}
				
				if (tmpTCProp.isType(TCProperty.PROP_string)) {
					try {
						newExtRateRevision.setProperty(string, line.get(colNum));
					} catch (TCException e) {
						System.out.println(e.getDetailsMessage());
						errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
						try {
							newExtRateRevision.delete();
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							System.out.println(e1.getDetailsMessage());
							errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
							return false;
						}
						return false;
					}
				}else if (tmpTCProp.isType(TCProperty.PROP_date)) {
					SimpleDateFormat format = new SimpleDateFormat(dateFormat);
					try {
						System.out.println("ʱ��ת����"+line.get(colNum));
						Date date = format.parse(line.get(colNum));
						try {
							newExtRateRevision.setDateProperty(string, date);
						} catch (TCException e) {
							System.out.println(e.getDetailsMessage());
							errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
							try {
								newExtRateRevision.delete();
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								System.out.println(e1.getDetailsMessage());
								errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
								return false;
							}
							return false;
						}
					} catch (ParseException e) {
						
						System.out.println(e.getMessage());
						errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
						try {
							newExtRateRevision.delete();
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							System.out.println(e1.getDetailsMessage());
							errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
							return false;
						}
						return false;
					}
				}else if (tmpTCProp.isType(TCProperty.PROP_double)) {
					double tmpValue = Double.valueOf(line.get(colNum));
					try {
						newExtRateRevision.setDoubleProperty(string, tmpValue);
					} catch (TCException e) {
						System.out.println(e.getDetailsMessage());
						errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
						try {
							newExtRateRevision.delete();
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							System.out.println(e1.getDetailsMessage());
							errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
							return false;
						}
						return false;
					}
				}else if (tmpTCProp.isType(TCProperty.PROP_int)) {
					int tmpValue = Integer.valueOf(line.get(colNum));
					try {
						newExtRateRevision.setIntProperty(string, tmpValue);
					} catch (TCException e) {
						System.out.println(e.getDetailsMessage());
						errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
						try {
							newExtRateRevision.delete();
						} catch (TCException e1) {
							// TODO Auto-generated catch block
							System.out.println(e1.getDetailsMessage());
							errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
							return false;
						}
						return false;
					}
				}else if (tmpTCProp.getPropertyDescriptor().getLOV() != null) {
					if (string.equals("jci6_Division") ) {
						String currentValue = line.get(colNum);
						TCComponentListOfValues jci6_Division = tmpTCProp.getPropertyDescriptor().getLOV();
						ListOfValuesInfo list = null;
						try {
							list = jci6_Division.getListOfValues();
						} catch (TCException e) {
							System.out.println(e.getDetailsMessage());
							errorStrs.append("jci6_Division��ȡLOVʧ��").append("\n");
							try {
								newExtRateRevision.delete();
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								System.out.println(e1.getDetailsMessage());
								errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
								return false;
							}
							return false;
						}
						String[] lovValues = list.getLOVDisplayValues();
						for (String string2 : lovValues) {
							if (string2.contains(currentValue)) {
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
							System.out.println(e.getDetailsMessage());
							errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
							try {
								newExtRateRevision.delete();
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								System.out.println(e1.getDetailsMessage());
								errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
								return false;
							}
							return false;
						}
						if (currentGroup == null) {
							try {
								newExtRateRevision.save();
								newExtRateRevision.unlock();
//							newExtRateRevision.delete();
								newExtRateRevision.delete();
							} catch (TCException e) {
								System.out.println(e.getDetailsMessage());
								errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("��TC�д�������������ʧ��,��ɾ���Ѵ���itemʧ�ܣ�").append("\n");
								try {
									newExtRateRevision.delete();
								} catch (TCException e1) {
									// TODO Auto-generated catch block
									System.out.println(e1.getDetailsMessage());
									errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
									return false;
								}
								return false;
							}
							errorStrs.append("��").append(currenRowNum+1).append("�У�").append("����").append(currentIDNumber).append("ʱ���������Դ���").append("jci6_Division").append("\n");
							try {
								newExtRateRevision.delete();
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								System.out.println(e1.getDetailsMessage());
								errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
								return false;
							}
							return false;
						}
						try {
							newExtRateRevision.setReferenceProperty(string, currentGroup);
						} catch (TCException e) {
							System.out.println(e.getDetailsMessage());
							errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
							try {
								newExtRateRevision.delete();
							} catch (TCException e1) {
								// TODO Auto-generated catch block
								System.out.println(e1.getDetailsMessage());
								errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
								return false;
							}
							return false;
						}
					}else if (string.equals("jci6_Section")) {
						String currentValue = line.get(colNum);
						if (currentValue.trim().length()> 2) {
							TCComponentListOfValues jci6_Division = tmpTCProp.getPropertyDescriptor().getLOV();
							ListOfValuesInfo list = null;
							try {
								list = jci6_Division.getListOfValues();
							} catch (TCException e) {
								System.out.println(e.getDetailsMessage());
								errorStrs.append("jci6_Division��ȡLOVʧ��").append("\n");
								try {
									newExtRateRevision.delete();
								} catch (TCException e1) {
									// TODO Auto-generated catch block
									System.out.println(e1.getDetailsMessage());
									errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
									return false;
								}
								return false;
							}
							String[] lovValues = list.getLOVDisplayValues();
							for (String string2 : lovValues) {
								if (string2.contains(currentValue)) {
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
								System.out.println(e.getDetailsMessage());
								errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
								try {
									newExtRateRevision.delete();
								} catch (TCException e1) {
									// TODO Auto-generated catch block
									System.out.println(e1.getDetailsMessage());
									errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
									return false;
								}
								return false;
							}
							if (currentGroup == null) {
								try {
									newExtRateRevision.save();
									newExtRateRevision.unlock();
//								newExtRateRevision.delete();
									newExtRateRevision.delete();
								} catch (TCException e) {
									System.out.println(e.getDetailsMessage());
									errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("��TC�д�������������ʧ��,��ɾ���Ѵ���itemʧ�ܣ�").append("\n");
									try {
										newExtRateRevision.delete();
									} catch (TCException e1) {
										// TODO Auto-generated catch block
										System.out.println(e1.getDetailsMessage());
										errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
										return false;
									}
									return false;
								}
								errorStrs.append("��").append(currenRowNum+1).append("�У�").append("����").append(currentIDNumber).append("ʱ���������Դ���").append("jci6_Section").append("\n");
								try {
									newExtRateRevision.delete();
								} catch (TCException e1) {
									// TODO Auto-generated catch block
									System.out.println(e1.getDetailsMessage());
									errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
									return false;
								}
								return false;
							}
							try {
								newExtRateRevision.setReferenceProperty(string, currentGroup);
							} catch (TCException e) {
								System.out.println(e.getDetailsMessage());
								errorStrs.append("��").append(currenRowNum+1).append("�У�����").append(currentIDNumber).append("ʱ���������Դ���").append(string).append("\n");
								try {
									newExtRateRevision.delete();
								} catch (TCException e1) {
									// TODO Auto-generated catch block
									System.out.println(e1.getDetailsMessage());
									errorStrs.append("��").append(currenRowNum+1).append("�У�").append(currentIDNumber).append("�汾��TC�д�������������ʧ��,��ɾ���Ѵ���item�汾ʧ�ܣ�").append("\n");
									return false;
								}
								return false;
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
		}
		return true;
		
		
	}
	
	//��ְ
	private boolean leaveExtRate(List<String> line,int currenRowNum) throws TCException{
		TCComponentItemType	itemType = (TCComponentItemType) session.getTypeComponent(ExtRateType);
		String currentName = line.get(nameColNum).trim();
		String currentIDNumber = line.get(IDNumberColNum).trim();
		TCComponentItem[] items = itemType.findItems(currentIDNumber);
		if (items.length == 0) {
			errorStrs.append("TC û���ҵ���Ҫ���µ�ExtRate��").append(currentIDNumber).append("\n");
			return false;
		}else if (items.length > 1) {
			errorStrs.append("TC �ҵ����ExtRate���󣬷�Ψһ��").append(currentIDNumber).append("\n");
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
		System.out.println(linesb.substring(0, linesb.length()-1)+"]");
	}
	
	/**
	 * �ж������Ƿ�����ȷ��ʱ���ʽ
	 * @param line
	 * @param errorStrs
	 * @return
	 */
	private boolean isCorrentDateFormat(List<String> line){
		boolean ifail = false;
		try {
			TCComponentItemType	itemType = (TCComponentItemType) session.getTypeComponent(ExtRateType);
			System.out.println("itemType:"+itemType.getClassName());
			Set<String> propKeys = excelProps.keySet();
			for (String string : propKeys) {
				System.out.println("propKeys:"+string);
				
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
	
	//�Զ�����
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
			releasestatus.refresh();
			releasestatus.lock();
			releasestatus.save();
			obj.refresh();
			obj.add("release_status_list", releasestatus);
			obj.setDateProperty("date_released", new Date());
			obj.lock();
			obj.save();
			releasestatus.unlock();
			obj.unlock();
		}
	}
	
	/**
	 * �����ҵ����û��µ��ض��ļ���
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
			MessageBox.post("��HOME��δ�ҵ��ض����ļ��У�"+folderName+",�봴����", "ERROR", MessageBox.ERROR);
		}
		return needFolder;
	}
	
	/**
	 * ת����˾����ΪTC��ʶ��ı��
	 * @param compenyName
	 * @return
	 */
	private String getidFromCompeny(String compenyName){
		if (compenyName.contains("(") && compenyName.contains(")")) {
			String a = compenyName.split("\\(")[1].split("\\)")[0];
			return a;
		}else if (compenyName.contains("��") && compenyName.contains("��")) {
			String a = compenyName.split("��")[1].split("��")[0];
			return a;
		}
		
		return "";
	}
	
	
	public static void main(String[] args) {
		int a = 1;
		System.out.println(a++);
	}

}
