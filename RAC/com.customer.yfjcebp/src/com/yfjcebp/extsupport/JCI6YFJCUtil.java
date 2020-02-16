package com.yfjcebp.extsupport;

import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.TCAttachmentType;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentFormType;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentGroupMemberType;
import com.teamcenter.rac.kernel.TCComponentGroupType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentProcessType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.TCUserService;

public class JCI6YFJCUtil {
	
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");
	/**
	 * ��������
	 * 
	 * @param comArray
	 * @throws TCException
	 */
	public static TCComponentProcess createProcess(TCSession session,
			TCComponentTaskTemplate taskTemp, String processName,
			TCComponent[] comArray) throws TCException {
		if (taskTemp != null) {
			TCComponentProcessType tccomponentprocesstype = (TCComponentProcessType) session
					.getTypeComponent("Job");
			int[] ai = new int[comArray.length];
			for (int i = 0; i < comArray.length; i++) {
				ai[i] = TCAttachmentType.TARGET;
			}
			TCComponentProcess process = (TCComponentProcess) tccomponentprocesstype
					.create(processName, processName, taskTemp, comArray, ai);
			return process;
		}
		return null;
	}

	/**
	 * ��������ģ���Ƿ����
	 * 
	 * @param session
	 * @param templateName
	 * @return
	 * @throws TCException
	 */
	public static TCComponentTaskTemplate findTaskTemplate(TCSession session,
			String templateName) throws TCException {
		TCComponentTaskTemplateType templateType = (TCComponentTaskTemplateType) session
				.getTypeComponent("EPMTaskTemplate");
		return templateType.find(templateName,
				TCComponentTaskTemplate.PROCESS_TEMPLATE_TYPE);
	}

	/**
	 * ��ֵ�ַ���ѡ��
	 * 
	 * @param preService
	 * @param optionName
	 * @return
	 */
	public static String getOptionVal(TCPreferenceService preService,
			int optionType, String optionName) {
		return preService.getString(optionType, optionName);
	}

	/**
	 * ��ֵ�ַ���ѡ��
	 * 
	 * @param preService
	 * @param optionName
	 * @return
	 */
	public static String[] getOptionValArray(TCPreferenceService preService,
			int optionType, String optionName) {
		return preService.getStringArray(optionType, optionName);
	}

	/**
	 * ���ò�ѯ
	 * 
	 * @param textservice
	 * @param queryType
	 * @param query_name
	 * @param key
	 * @param value
	 * @return
	 * @throws TCException
	 */
	public static TCComponent[] query(TCTextService textservice,
			TCComponentQueryType queryType, String query_name, String[] key,
			String[] value) throws TCException {
		TCComponentQuery query = (TCComponentQuery) queryType.find(query_name);
		if (query != null) {
			String[] queryAttribute = new String[key.length];
			for (int i = 0; i < key.length; ++i) {
				queryAttribute[i] = textservice.getTextValue(key[i]);
				System.out.println("查询条件 queryAttribute[i]-->"+queryAttribute[i]);
				if (queryAttribute[i] == null
						|| queryAttribute[i].trim().equals("")) {
					queryAttribute[i] = key[i];
				}
			}
			return query.execute(queryAttribute, value);
		}
		return null;
	}

	/**
	 * ����form
	 * 
	 * @param session
	 * @param formType
	 * @param formName
	 * @return
	 * @throws TCException
	 */
	public static TCComponentForm createForm(TCSession session,
			String formType, String formName) throws TCException {
		TCComponentFormType compType = (TCComponentFormType) session
				.getTypeComponent("Form");
		return compType.create(formName, "", formType);// Form��ƣ�Form�������Լ�Form
	}

	// ����ѡ�е��û��ڱ��¶��Ƿ񱻱��sectionmanagerȦ��,���ò�ѯ����ExtSupport
	// jci6_UserName����û���jci6_Year���,jci6_Month�¶�
	public static double getUsagePercent(TCTextService textService,
			TCComponentQueryType queryType, String userName,
			String[] queryKeys, String[] queryValues) throws TCException {
		TCComponent[] comps = query(textService, queryType, "s", queryKeys,
				queryValues);
		double percent = 100;
		if (comps != null) {
			for (int i = 0; i < comps.length; i++) {
				percent = percent - comps[i].getDoubleProperty("jci6_Percent");
			}
		}
		return percent;
	}

	/**
	 * ͨ�����������
	 * 
	 * @param session
	 * @param groupname
	 * @return
	 * @throws TCException
	 */
	public static TCComponentGroup findGroupByName(TCSession session,
			String groupname) throws TCException {
		TCComponentGroupType groupType = (TCComponentGroupType) session
				.getTypeComponent("Group");
		return groupType.find(groupname);
	}

	/**
	 * �õ�groupMember
	 * 
	 * @param session
	 * @param newUser
	 * @param groupname
	 * @return
	 * @throws TCException
	 */
	public TCComponentGroupMember getGroupMember(TCSession session,
			TCComponentUser newUser, String groupname) throws TCException {
		TCComponentGroup group = findGroupByName(session, groupname);
		TCComponentGroupMemberType tccomponentgroupmembertype = (TCComponentGroupMemberType) session
				.getTypeComponent("GroupMember");
		TCComponentGroupMember atccomponentgroupmember[] = tccomponentgroupmembertype
				.find(newUser, group);
		TCComponentGroupMember member = null;
		for (int m = 0; m < atccomponentgroupmember.length; m++) {
			member = atccomponentgroupmember[0];
		}
		return member;
	}


	public static String doubleTrans(double d) {
		if (Math.round(d) - d == 0) {
			return String.valueOf((long) d);
		}
		return String.valueOf(d);
	}

	/**
	 * ����userservice
	 * 
	 * @param userservice
	 * @param serviceName
	 * @param obj
	 * @return
	 * @throws TCException
	 */
	public static Object callUserService(TCUserService userservice,
			String serviceName, Object[] obj) throws TCException {
		return userservice.call(serviceName, obj);
	}

	/**
	 * ͨ���û�Id�����û�
	 * 
	 * @param userType
	 * @param username
	 * @return
	 * @throws TCException
	 */

	public static TCComponentUser findUserById(TCComponentUserType userType,
			String username) throws TCException {
		return userType.find(username);
	}

	/**
	 * ���ò�ѯ������ݼ�
	 * 
	 * @param session
	 * @param dataSetName
	 * @return
	 * @throws TCException
	 */
	public static TCComponentDataset findDataSet(TCSession session,
			String dataSetName) throws TCException {
		TCComponentDatasetType dsType = (TCComponentDatasetType) session
				.getTypeComponent("Dataset");
		return dsType.find(dataSetName);

	}

	/**
	 * �õ���ѯ������ݼ���Ӧ�����������ļ�
	 * 
	 * @param session
	 * @param txtName
	 * @return
	 * @throws TCException
	 */
	public static File getSearchDataset(TCSession session, String txtName)
			throws TCException {
		File fmsFile = null;
		TCComponentDataset dataSet = findDataSet(session, txtName);
		if (dataSet != null) {
			TCComponentTcFile file[] = dataSet.getTcFiles();
			if (file != null && file.length > 0) {
				fmsFile = file[0].getFmsFile();
			}
			return fmsFile;
		} else {
			return null;
		}
	}

	public static String getDatasetTemPath(TCSession session, String optionName)
			throws TCException {
		TCPreferenceService preService = session.getPreferenceService();
		String datasetName = preService.getString(
				TCPreferenceService.TC_preference_site, optionName);
		File file = getSearchDataset(session, datasetName);
		if (file != null) {
			return file.getAbsolutePath();
		}
		return null;
	}

	public static String getStringCellValue(XSSFFormulaEvaluator formula,
			Cell cell) { // ��ȡ��Ԫ���������Ϊ�ַ����͵����
		String strCell = "";
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_FORMULA:
			CellValue cellVal = formula.evaluate(cell);
			strCell = cellVal.getStringValue();
			break;
		case Cell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				strCell = sdf.format(cell.getDateCellValue());
				break;
			} else {
				strCell = String.valueOf(cell.getNumericCellValue());
				break;
			}
		case Cell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			strCell = "";
			break;
		default:
			strCell = "";
			break;
		}
		return strCell;
	}
	
	public static String getStringCellValue2003(HSSFFormulaEvaluator formula,
			Cell cell) { // ��ȡ��Ԫ���������Ϊ�ַ����͵����
		String strCell = "";
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_FORMULA:
			CellValue cellVal = formula.evaluate(cell);
			strCell = cellVal.getStringValue();
			break;
		case Cell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				strCell = sdf.format(cell.getDateCellValue());
				break;
			} else {
				strCell = String.valueOf(cell.getNumericCellValue());
				break;
			}
		case Cell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			strCell = "";
			break;
		default:
			strCell = "";
			break;
		}
		return strCell;
	}
	
	/**
	 * ���͵�
	 * @param aifcomp
	 */
	public static void sendTo(InterfaceAIFComponent[] aifcomp){
		String s = "com.teamcenter.rac.cme.mpp.MSEPerspective";// ���͹��ռ��� Revision���������칤�չ滮��
		//com.teamcenter.rac.pse.PSEPerspective    PSE
		//org.eclipse.ui.perspectives    ��Ӧ��id
		Activator.getDefault().openPerspective(s);
		Activator.getDefault().openComponents(s,aifcomp);
		
		
		/**---------------------**/
		AbstractAIFUIApplication app =  AIFUtility.getCurrentApplication();
		InterfaceAIFComponent comp= app.getTargetComponent();
		AIFDesktop.getActiveDesktop().postApplication(s, comp);
	    
		
		IWorkbenchWindow dwindow = PlatformUI.getWorkbench
			    ().getActiveWorkbenchWindow();
			try {
				PlatformUI.getWorkbench().showPerspective( s, dwindow );
			} catch (WorkbenchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AbstractAIFUIApplication localAbstractAIFUIApplication =
			AIFUtility.getCurrentApplication();
			localAbstractAIFUIApplication.open(aifcomp);
			/**---------------------**/
	}
	
	public static String[] getLovValues(TCSession session,String lovName) throws TCException{
		TCComponentListOfValuesType lovType = (TCComponentListOfValuesType) session.getTypeComponent("ListOfValues");
		TCComponentListOfValues[] listOfVals = lovType.find(lovName);
		if(listOfVals != null && listOfVals.length >0 ){
			TCComponentListOfValues listOfVal= listOfVals[0];
			ListOfValuesInfo info = listOfVal.getListOfValues();
			return info.getLOVDisplayValues();
		}
		
		return null;
	}
	
	

}
