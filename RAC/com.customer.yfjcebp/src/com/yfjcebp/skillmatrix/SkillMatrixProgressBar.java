/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: SkillMatrixProgressBar.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-18	wuh  		Ini		鍒濆鍖� 								   
 #=============================================================================
 */
package com.yfjcebp.skillmatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Utilities;


public class SkillMatrixProgressBar {
//	private Registry reg = Registry.getRegistry(SkillMatrixProgressBar.class);
	private static final String BURFLR_NAME = "com.yfjcebp.skillmatrix.skillmatrix_locale";
	private static final ResourceBundle reg = ResourceBundle.getBundle(BURFLR_NAME);
	public SkillMatrixProgressBar(
			Display display, TCSession session, String path) {
		Shell shell = new Shell(display, SWT.TITLE | SWT.ON_TOP);
		shell.setLayout(new GridLayout());
		// 娣�姞鑷姩閎�鐨勹�搴︽�
		ProgressBar pb2 = new ProgressBar(shell, SWT.HORIZONTAL
				| SWT.INDETERMINATE);
		pb2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// 娣�姞绾跨▼锛屽湻�跨▼涓鐞嗛暱鏃堕棿鐨勍�鍔★紝骞舵渶缁堝弽鏄犲湪骞虫粦杩涘�鏉�笂
		new Thread(new LongRunningOperation(shell, session,path,reg)).start();
		shell.setText(reg.getString("ReadCheckExcelMessage"));
		shell.setSize(280, 60);
		// 璁剧疆shell灞呬�
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.open();
		// 寿��俊鎭惊鐜紝鐩村埌�EUR��埛鍏抽棴绐��
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}

class LongRunningOperation implements Runnable {
	private ArrayList<String[]> list = new ArrayList<String[]>();
	private ExcelDialog doc = null;
	private String[] obj;
	private String warnmessage = "";
	private String errmessage = "";
	private String parsePath;
	private int noUserIdCount;// 鍛樺伐缂栧彿瀵瑰簲鐨動�鎴蜂笉�樺湐�勚�鐩�
	private int noUserNameCount;// excel涓崟鍏冩牸娌℃湁鏁版嵁鐨勬暟鐩�
	private String warningMessage;// ��憡淇℃伅锛堥粍鑹插瓧浣擄�
	private String errorMessage;// 閿欒淇℃伅锛堢孩鑹插瓧浣擄�
	private TCPreferenceService preference;
	private int employeeNo;//鍛樺伐缂栧彿
	private String path;
	private POIReadExcel p;
	private Shell shell;
	private TCSession session;
	private ResourceBundle reg;
	private TCComponentUserType tcccomponentusertype;
	private HashMap<String, String> map = new HashMap<String, String>();
    private HashMap<Integer, String> propertymap = new HashMap<Integer, String>();
    private SkillMatrixSearch  search;
    private String searchName = "YFJC Search User based on PA7";
    private String searchKey = "PA7";
    private HashMap<Integer, TCComponentUser> user_map = new HashMap<Integer, TCComponentUser>();
	public ExcelDialog getDoc() {
		return doc;
	}

	public void setDoc(ExcelDialog doc) {
		this.doc = doc;
	}

	public LongRunningOperation(Shell shell, TCSession session, String path,ResourceBundle reg) {
		this.shell = shell;
		this.session = session;
		this.path = path;
		this.reg = reg;
	}

	@Override
	public void run() {
		// 璁剧疆閿欒鍙婅鍛婁俊鎿�
		try {
			warningMessage = reg.getString("warmeg") + ":" + "\n";// ��憡淇℃伅锛�
			errorMessage = reg.getString("errmeg") + ":" + "\n";// 閿欒淇℃伅锛�
			parsePath = parseExcelPath();
			search = new SkillMatrixSearch(session);
			// 寿��excel
			p = new POIReadExcel(path);
			if (!p.getExcelIsNull()) {
				preference = session.getPreferenceService();
				getPreferenceValue("YFJC_Skill_Matrix");
				employeeNo = p.columnNo(map.get("PA7"));
				setErrorMessage();
				setWarningMessage();
				// 鏄剧ず閿欒鍙婅鍛婁俊鎿�
				showMessage(shell);
				// 鍋囧�xceldialog涓虹┖鍗砮xcel瀵煎叆�ｇ�
				if (getDoc() == null) {
					SkillMatrixOperation skillMatrixOperation = new SkillMatrixOperation(
							this, session, user_map, shell , map,propertymap);
				}
			} else {
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						shell.dispose();
					}
				});
			}
		} catch (Exception e) {
			shell.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					shell.dispose();
				}
			});
			MessageBox.post(reg.getString("excelnotformatmessage"),
					reg.getString("infomessage"), MessageBox.INFORMATION);
			e.printStackTrace();
		}
	}

	/**
	 * 璁剧疆閿欒淇℃�(鍗冲憳�ョ紪鍙峰�旂殑user涓嶅瓨鍦ㄦ�鎶ヤ俊鎭�) set error message
	 * 
	 * @throws TCException
	 */
	public void setErrorMessage() throws Exception {

		list = p.getList();

		// 浠庣浜岃寿�濿�
		for (int i = 1; i < list.size(); i++) {
			obj = list.get(i);
			// 绗�1鍒�
			String s = obj[employeeNo];
			if(s.contains(".0")){
				s = s.substring(0, s.length()-2);
			}
			// 鍒ゆ柇Userid鏄惁�樺湔�屼笉瀛樺湾�炲�閿欒淇℃伅
			TCComponentUser user = null;
			user = searchUser(s);
			//鑻ヤ笉�樺湍�欐姤閿�
			if(user == null){
				noUserIdCount = noUserIdCount + 1;
				userNotExitDo(doc, noUserIdCount, s, i);
			}else{
				user_map.put(i, user);
			}
		}
	}
	/**
	 * 璁剧疆鎻愁�妗嗙殑璀�憡淇℃伅,鍗曞厓鏍兼病鏈�暟鎹�
	 */
	public void setWarningMessage() {
		// 寰幆鎵�鏈夊崟鍏冩�,鍒ゆ柇鏄惁鏈�┖鐨勴�鍏冩�
		String o = null;
		for (int i = 0; i < p.getList().size(); i++) {
			obj = p.getList().get(i);
			for (int j = 0; j < obj.length; j++) {
				// 鍙栧緱鍗曞厓鏍间腑鐨勿��
				o = obj[j];
				// 鑻ュ垶�嶆槸闅愯棌鐨�
				if (p.getSheet().isColumnHidden(j) == false) {
					if (o.equals("")) {
						noUserNameCount = noUserNameCount + 1;
						String str = p.excelRange(i, j);
						warnmessage = warnmessage + noUserNameCount + "." + str
								+ reg.getString("cellNoDate.Name") + "锛�" + "\n";
					}
				}
			}
		}

	}

	/**
	 * �EUR��埛涓嶅瓨鍦�
	 * 
	 * @param doc
	 * @param numberToLetter
	 * @param parsePath
	 * @param s1
	 * @param noUserIdCount
	 * @param s
	 * @param k
	 */
	private void userNotExitDo(ExcelDialog doc, int noUserIdCount, String s,
			int k) {

		String str = noUserIdCount + "." + p.excelRange(k, 1)
				+ reg.getString("group.ColumnName")
				+ reg.getString("EmployeeNo.Name") + "\"" + s + "\""
				+ reg.getString("userNotExist.Name") + "\n";
		errmessage = errmessage + str;
	}

	public POIReadExcel getP() {
		return p;
	}

	public void setP(POIReadExcel p) {
		this.p = p;
	}

	/**
	 * 鏄剧ず�煎叆閿欒淇℃�
	 */
	public void showMessage(final Shell shell) {
		if (!warnmessage.equals("") || !errmessage.equals("")) {
			shell.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					shell.dispose();
				}
			});
			doc = new ExcelDialog(parsePath, errorMessage, errmessage,
					warningMessage, warnmessage);
		}
	}

	// 瑙ｆ瀽path:E:/妄��&娴�瘏�版嵁鏂囦�/浜哄ご璁″垝/鍐呴儴浜哄ご璁�垝_2013.2.18.xlsx
	private String parseExcelPath() {
		String str = null;
		String[] s = path.split("\\\\");
		str = s[s.length - 1];
		return str;
	}

	/**
	 * getPreferenceValue::鑾峰彇棣栿�夐�鍊�
	 * 
	 * @param TCSession
	 * @param Registry
	 * @param TCPreferenceService
	 * @param String
	 *            preference name
	 * @return String
	 * */
	public HashMap<String, String> getPreferenceValue(String pPreferenceName) {

		String[] strValue = preference.getStringArray(4, pPreferenceName);
		if (strValue == null) {
			MessageBox.post(
					Utilities.getCurrentFrame(),
					reg.getString("PreferenceName.ColumnName")
							+ pPreferenceName
							+ reg.getString("NoValidValue.ColumnName"),
					reg.getString("infomessage"), MessageBox.WARNING);
		}
		for (int i = 0; i < strValue.length; i++) {
			String[] s;
			try {
				s = parseString(strValue[i]);
				if (s.length > 0) {
					map.put(s[1], s[0]);
	                propertymap.put(p.columnNo(s[0]), s[1]);
				} 
			} catch (Exception e) {
				MessageBox.post(Utilities.getCurrentFrame(),reg.getString("prevaluenotfor.ColumnName"),
						reg.getString("warmessage"), MessageBox.WARNING);
			}
			
		}
		return map;
	}

	// 瑙ｆ瀽棣栿�夐�鍊尖��=鈥�
	private String[] parseString(String str) throws Exception{
		String[] value = null;
		value = str.split("=");
		return value;
	}
	/**
	 * 閰�繃userid鏌ユ壘user
	 * @param o
	 * @return
	 */
	private TCComponentUser findUser(String o) {
		// 閰�繃session鐨刧etTypeComponent鏂�硶��埌TCComponentUserType
		TCComponentUser  user = null;
		try {
			tcccomponentusertype = (TCComponentUserType) session
					.getTypeComponent("User");
			user = tcccomponentusertype.find(o);
		} catch (TCException e) {
		}
		return user;
	}
	/**
	 * 璋冪敤鏌デ�鏋勫缓鍣細YFJC Search User based on PA7
	 */
	private TCComponentUser searchUser(String s){
		TCComponentUser user= null;
		String[] searchValues = new String[1];
		searchValues[0] = s;
		try {
			InterfaceAIFComponent[] component = search.search(searchName, searchKey,searchValues);
			for(int i=0;i<component.length;i++){
				if(component[i] instanceof TCComponentUser){
					user = (TCComponentUser) component[i];
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
}
class SkillMatrixSearch {
	private TCSession session = null;
	private TCTextService tcTextService = null;
    public SkillMatrixSearch(TCSession session){
    	this.session = session;
		tcTextService = this.session.getTextService();
    }
	/**
	 * 璋冪敤鏌デ�鏋勫缓鍣�
	 * @param entries
	 * @param key
	 * @param values
	 * @return
	 * @throws Exception
	 */
    public InterfaceAIFComponent[] search( String entries,String key, String[] values) throws Exception{
    	if(null == key || null == values || null == entries){
			return null;
		}
		String asKey[] = {tcTextService.getTextValue(key)};//"PA7"
		String asValues[] = values;
		InterfaceAIFComponent interfaceAifComponent[] =  session.search(entries, asKey, asValues);//"Item ID"
		return interfaceAifComponent;
	
   }
}

