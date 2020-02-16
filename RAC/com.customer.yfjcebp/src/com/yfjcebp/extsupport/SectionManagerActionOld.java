package com.yfjcebp.extsupport;
//package com.yfjcebp.extsupport;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Vector;
//
//import javax.swing.SwingUtilities;
//
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import com.teamcenter.rac.aif.AbstractAIFUIApplication;
//import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
//import com.teamcenter.rac.kernel.TCComponent;
//import com.teamcenter.rac.kernel.TCComponentDataset;
//import com.teamcenter.rac.kernel.TCComponentQueryType;
//import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
//import com.teamcenter.rac.kernel.TCComponentTcFile;
//import com.teamcenter.rac.kernel.TCComponentUser;
//import com.teamcenter.rac.kernel.TCException;
//import com.teamcenter.rac.kernel.TCSession;
//import com.teamcenter.rac.kernel.TCTextService;
//import com.teamcenter.rac.util.MessageBox;
//import com.teamcenter.rac.util.Registry;
//
//public class SectionManagerActionOld extends AbstractAIFAction {
//
//	private TCSession session;
//	private TCComponentTaskTemplate taskTemplate;
//	private TCComponentQueryType queryType;
//	private TCTextService textService;
//	private XSSFFormulaEvaluator formula;
//	// sectionMeneger / 外包人员
//	private Map<String, Vector<String>> ext_daibiao_map = new HashMap<String, Vector<String>>();
//	// 外包人员/外包圈人者
//	private Map<String, TCComponentUser> ext_user_map = new HashMap<String, TCComponentUser>();
//	// 外包人员
//	private Vector<String> ext_db= new Vector<String>();
//	// 外包人员 ,division
//	private Map<String, String> ext_division = new HashMap<String, String>();
//
//	public SectionManagerActionOld(AbstractAIFUIApplication app, String s) {
//		super(app, s);
//		// TODO Auto-generated constructor stub
//		session = (TCSession) app.getSession();
//	}
//
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		Registry reg = Registry.getRegistry(this);
//		String templateName = reg.getString("ExtPlanReviewProcess");
//		System.out.println("templateName--->" + templateName);
//		try {
//			taskTemplate = JCI6YFJCUtil.findTaskTemplate(session, templateName);
//			if (taskTemplate == null) {
//				String message = templateName
//						+ " workflow template is not exist ";
//				MessageBox.post(message, "INFORMATION",
//						MessageBox.INFORMATION);
//			} else {
//				readyQuery();
//				TCComponent[] comps = JCI6YFJCUtil.query(textService,
//						queryType, "Dataset...",
//						new String[] { "Name","DatasetType"},
//						new String[] { "*_resourcepool*","MSExcelX"});
//				if (comps != null) {
//					for (int i = 0; i < comps.length; i++) {
//						TCComponentTcFile files[] = ((TCComponentDataset) comps[i])
//								.getTcFiles();
//						File file = null;
//						if (files != null && files.length > 0) {
//							file = files[0].getFmsFile();
//						}
//						if (file != null) {
//							// 读excel
//							readExcel(file);
//						}
//					}
//				}
//
//
//					// 读首选项
//					// session.queueOperation(new AbstractAIFOperation());
//					SwingUtilities.invokeLater(new Runnable() {
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							SelectExtDialog selectExtDialog = new SelectExtDialog(
//									session, taskTemplate, ext_daibiao_map,
//									ext_user_map, ext_db,ext_division);
//							try {
//								selectExtDialog.init();
//							} catch (TCException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					});
//				}
//
//			
//		} catch (TCException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	private void readyQuery() throws TCException {
//		if (queryType == null)
//			queryType = (TCComponentQueryType) session
//					.getTypeComponent("ImanQuery");
//		if (textService == null)
//			textService = session.getTextService();
//
//	}
//
//	private void readExcel(File file) throws IOException, TCException {
//		FileInputStream is = new FileInputStream(file.getAbsolutePath());
//		XSSFWorkbook wb = new XSSFWorkbook(is);
//		formula = new XSSFFormulaEvaluator(wb);
//		XSSFSheet sheet = wb.getSheetAt(0);
//		String[] key = new String[] { "userID" };
//		String[] value = new String[1];
//		readyQuery();
//		TCComponentUser user = null;
//		for (int i = 1; i < sheet.getLastRowNum(); i++) {
//			XSSFRow row = sheet.getRow(i);
//			String division_name = row.getCell(1).getStringCellValue();
//			String user_id = row.getCell(0).getStringCellValue();
//			if (ext_daibiao_map.containsKey(user_id)) {
//				Vector<String> vector = ext_daibiao_map.get(user_id);
//				String extusername = row.getCell(3).getStringCellValue();
//				vector.add(extusername);
//				if (user != null) {
//					ext_division.put(extusername, division_name);
//					ext_user_map.put(extusername, user);
//				}
//			} else {
//				value[0] = user_id;
//				// 查找用户
//				TCComponent[] comps = JCI6YFJCUtil.query(textService,
//						queryType, "YFJC_SearchUser", key, value);
//				if (comps != null && comps.length != 0) {
//					user = (TCComponentUser) comps[0];
//					Vector<String> vector = new Vector<String>();
//					XSSFCell cell = row.getCell(3);
//
//					String extusername = JCI6YFJCUtil.getStringCellValue(
//							formula, cell);
//					vector.add(extusername);
//					ext_daibiao_map.put(user_id, vector);
//					ext_user_map.put(extusername, user);	
//					ext_db.add(user_id);
//					ext_division.put(extusername, division_name);
//				}
//			}
//		}
//
//		is.close();
//	}
//
//}
