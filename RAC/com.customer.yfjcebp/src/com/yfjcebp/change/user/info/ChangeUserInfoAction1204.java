package com.yfjcebp.change.user.info;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDiscipline;
import com.teamcenter.rac.kernel.TCComponentPerson;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjc.lccreport.ProgressBarThread;
import com.yfjcebp.extsupport.JCI6YFJCUtil;
/**
 * YFJC1.4������ת���û���Ϣ����
 * ʹ�õ��Ĳ�ѯ:YFJC_SearchDisciplineByID,YFJC_Search_Discipline
 * ʹ�õ�����ѡ��YFJC_PositionFlow_PropMapping
 * @author admin
 *
 */
public class ChangeUserInfoAction1204 extends AbstractAIFAction {

	private Registry reg = Registry.getRegistry(this);
	private TCComponentUserType userType;
	private AbstractAIFUIApplication app;
	private TCComponentQueryType queryType;
	private TCTextService textService;
	private Map<String, TCComponentDiscipline> disciplineMap;// ���е�ѧ��
	private Vector<String> rateDisciplineVec; // ���ʼ���ѧ������ �̶���7��
	private Vector<Map<Integer, String>> dataVec;// excel������
	private Vector<TCComponentUser> userVec;// excel�ж�Ӧ��user
	private StringBuffer sb = new StringBuffer();
	private int user_id_col;
	private int rateLevel_col;
	private int tc_all_col;
	private int discipline_col;
	private String user_id_col_name;
	private String rateLevel_col_name;
	private String tc_all_col_name;
	private String discipline_col_name;

	private String[] person_pronames;// 2,5,6,7,8,9,10
	private Integer[] personProCols;
	private String YFJC_SEARCHDISCIPLINEBYID = "YFJC_SearchDisciplineByID";
	private String[] userid_queryAttribute;
	private String[] userid_val = new String[1];
	private Vector<Vector<TCComponentDiscipline>> dataDisciplineVec;// ÿ����Ҫ���õ�ѧ��
	private TCComponentQuery search_discipline_query;
	private Vector<TCComponentDiscipline> need_set_discipline = new Vector<TCComponentDiscipline>();// ĳ���û���Ҫ���õ�ѧ��
	private ProgressBarThread thread;
	private int dataCnt;
	private Map<String, Integer> personProColMap = new HashMap<String, Integer>();
	private Map<String, Integer> disciplineColMap = new HashMap<String, Integer>();

	private TCSession session;
	private String excelPath;
	private ChangeUserInfoExcel infoExcel;
	 

	public ChangeUserInfoAction1204(AbstractAIFUIApplication app, String arg1) {
		super(app, arg1);
		// TODO Auto-generated constructor stub
		this.app = app;
		dataVec = new Vector<Map<Integer, String>>();
		userVec = new Vector<TCComponentUser>();
	}

	@Override
	public void run() {
		session = (TCSession) app.getSession();
		
			try {
				if (!isDBA(session)) {
					MessageBox.post(reg.getString("notDBA"), "ERROR",
							MessageBox.ERROR);// ������ϵͳ����Ա
				} else {
					TCComponentDataset dataset = getTarget(app);
					if (dataset == null) {
						MessageBox.post(reg.getString("notTransferDataset"),
								"INFORMATION", MessageBox.INFORMATION);// ��ѡ��MSExcelX���ݼ��ٲ���
					} else {
						excelPath = getExcelPath(dataset);
						if (excelPath == null) {
							MessageBox.post(reg.getString("datasetNoReference"),
									"INFORMATION", MessageBox.INFORMATION);// ѡ�����ݼ�û�����������ļ�
						} else {
							session.queueOperation(new AbstractAIFOperation() {

								@Override
								public void executeOperation() throws Exception {
									// TODO Auto-generated method stub
									try {
									thread = new ProgressBarThread("INFORMATION",
											reg.getString("checkExcel"));
									thread.start();
									infoExcel = new ChangeUserInfoExcel(session,
											excelPath);
									infoExcel.readExcel();
									personProColMap = infoExcel
											.getPersonProColMap();
									disciplineColMap = infoExcel
											.getDisciplineColMap();

									dataVec = infoExcel.getDataVec();
									dataCnt = dataVec.size();
									thread.stopBar();
									if (dataCnt == 0) {
										MessageBox.post(
												reg.getString("excelNoData"),
												"INFORMATION",
												MessageBox.INFORMATION);// û����Ҫת�ڵ��û�
									} else {
										user_id_col_name = infoExcel
												.getUser_id_col_name();
										tc_all_col_name = infoExcel
												.getTc_all_col_name();
										rateLevel_col_name = infoExcel
												.getRateLevel_col_name();
										discipline_col_name = infoExcel
												.getDiscipline_col_name();
										user_id_col = 0;
										System.out.println("disciplineColMap--->"+disciplineColMap);
										System.out.println("disciplineColMap.get(TC_all)--->"+disciplineColMap.get("TC_all"));
										tc_all_col = disciplineColMap.get("TC_all");
										rateLevel_col = disciplineColMap
												.get("jci6_RateLevel");
										discipline_col = disciplineColMap
												.get("Discipline");
										userType = (TCComponentUserType) session
												.getTypeComponent("User");
										String[] str = new String[] { "Manager",
												"Lead Engineer", "Senior Engineer",
												"Engineer", "Assistant Engineer",
												"Engineer Assistant",
												"ExtSupporter" };// ���ʼ���ѧ��
										rateDisciplineVec = new Vector<String>();
										for (int i = 0; i < str.length; i++) {
											rateDisciplineVec.add(str[i]);
										}
										disciplineMap = new HashMap<String, TCComponentDiscipline>();
										dataDisciplineVec = new Vector<Vector<TCComponentDiscipline>>();
										getDisciplineMap(session);// ����ѧ��
										StringBuffer strBuf = new StringBuffer();
										String col = reg.getString("col");
										for (int i = 0; i < dataCnt; i++) {
											// �����û��Ƿ����
											Map<Integer, String> map = dataVec
													.get(i);
											String user_id = map.get(user_id_col);
											TCComponentUser user = null;
											boolean exist = true;
											try {
												user = findUserById(user_id);
											} catch (TCException e) {
											}
											userVec.add(user);
											// �жϷ��ʼ����Ƿ����
											String rateLevel = map
													.get(rateLevel_col);
											String tc_all = map.get(tc_all_col);

											// �ж�ѧ���Ƿ����
											String discipline = map
													.get(discipline_col);
											String[] dis_s = discipline.split(",");
											strBuf.setLength(0);
											Vector<TCComponentDiscipline> disVec = new Vector<TCComponentDiscipline>();

											if (tc_all.trim().equals("Y")) {
												disVec.add(disciplineMap
														.get("TC_all"));
											}

											if (!discipline.trim().equals("")) {
												for (int j = 0; j < dis_s.length; j++) {
													if (!disciplineMap
															.containsKey(dis_s[j])) {
														strBuf.append(" '")
																.append(dis_s[j])
																.append("' ");
													} else {
														disVec.add(disciplineMap
																.get(dis_s[j]));
													}
												}
											}

											if (user == null
													|| (!rateLevel.trim()
															.equals("") && !rateDisciplineVec
															.contains(rateLevel))
													|| !strBuf.toString()
															.equals("")
													|| (!tc_all.trim().equals("Y") && !tc_all
															.trim().equals(""))) {
												exist = false;
												sb.append("The ").append(i + 3)
														.append(" Line ");
											}

											if (user == null) {
												sb.append(user_id_col_name)
														.append(" ").append(col);
											}

											if ((!rateLevel.trim().equals("") && !rateDisciplineVec
													.contains(rateLevel))) {
												if (user == null)
													sb.append(",");
												sb.append(rateLevel_col_name)
														.append(" ").append(col);
											}
											if (!tc_all.trim().equals("")
													&& !tc_all.trim().equals("Y")) {
												if (user == null
														|| (!rateLevel.trim()
																.equals("") && !rateDisciplineVec
																.contains(rateLevel)))
													sb.append(",");
												sb.append(tc_all_col_name)
														.append(" ").append(col);
											}

											if (user == null
													|| (!rateLevel.trim()
															.equals("") && !rateDisciplineVec
															.contains(rateLevel))
													|| (!tc_all.trim().equals("Y") && !tc_all
															.trim().equals("")))
												sb.append(" data error");

											if (!strBuf.toString().equals("")) {
												if (user == null
														|| (!rateLevel.trim()
																.equals("") && !rateDisciplineVec
																.contains(rateLevel))
														|| (!tc_all.trim().equals(
																"Y") && !tc_all
																.trim().equals("")))
													sb.append(",");
												sb.append(discipline_col_name)
														.append(" ")
														.append(col)
														.append(strBuf.toString())
														.append(" Discipline not exist");
											}

											if (!exist) {
												sb.append("\n");
											} else {
												dataDisciplineVec.add(disVec);
											}

										}
										if (!sb.toString().equals("")) {
											MessageBox.post(sb.toString(),
													"INFORMATION",
													MessageBox.INFORMATION);
										} else {
											String[] userid_key = new String[] { "user_id" };
											getDisciplineQuery(
													YFJC_SEARCHDISCIPLINEBYID,
													userid_key);
											if (search_discipline_query == null)
												MessageBox.post(
														YFJC_SEARCHDISCIPLINEBYID
																+ reg.getString("queryNotExist"),
														"INFORMATION",
														MessageBox.INFORMATION);
											else {
												session.queueOperation(new AbstractAIFOperation() {
													@Override
													public void executeOperation()
															throws Exception {
														// TODO Auto-generated
														// method stub
														thread = new ProgressBarThread(
																"INFORMATION",
																reg.getString("excelTransfering"));
														thread.start();
														personProCols = getPersonProCols();
														for (int i = 0; i < dataCnt; i++) {
															Map<Integer, String> map = dataVec
																	.get(i);
															modifyPersonInfo(
																	getUserPerson(userVec
																			.get(i)),
																	map);
															setDiscipline(
																	dataDisciplineVec
																			.get(i),
																	map, userVec
																			.get(i));

														}
														thread.stopBar();
														MessageBox.post(
																reg.getString("transferSuccess"),
																"INFORMATION",
																MessageBox.INFORMATION);
													}
												});
											}
										}
									}

									} catch (TCException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										thread.stopBar();
										MessageBox.post(e.toString(), "TCException", MessageBox.ERROR);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										thread.stopBar();
										MessageBox.post(e.toString(), "IOException", MessageBox.ERROR);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										 e.printStackTrace();
										thread.stopBar();
										MessageBox.post(e.toString(), "Exception", MessageBox.ERROR);
									}
									
								}});

						}

					}
				}
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(thread!= null)
					thread.stopBar();
				MessageBox.post(e.toString(), "IOException", MessageBox.ERROR);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(thread!= null)
					thread.stopBar();
				MessageBox.post(e.toString(), "IOException", MessageBox.ERROR);
			}

		
	}

	/**
	 * �õ���ǰ�û����ڵ��� �ж��Ƿ�Ϊ����Ա
	 * 
	 * @param session
	 * @return
	 * @throws TCException
	 */
	private boolean isDBA(TCSession session) throws TCException {
		String fullName = session.getCurrentGroup().getFullName();// ��ʵȫ����getLocalizedFullName()���ػ�������
		System.out.println("group name ---->" + fullName);
		if (fullName.equals("dba")) {
			return true;
		}
		return false;
	}

	/**
	 * �û��ǲ���excel���ݼ�
	 * 
	 * @param app
	 * @return
	 * @throws Exception
	 */
	private TCComponentDataset getTarget(AbstractAIFUIApplication app)
			throws Exception {
		InterfaceAIFComponent aif = app.getTargetComponent();
		if (aif != null) {
			String type = aif.getType();
			System.out.println("dataset type ---->" + type);
			if (type.equals("MSExcelX")) {
				return (TCComponentDataset) aif;
			}
		}
		return null;
	}

	/**
	 * ͨ���û�Id�����û�
	 * 
	 * @param username
	 * @return
	 * @throws TCException
	 */

	private TCComponentUser findUserById(String userid) throws TCException {
		return userType.find(userid);
	}

	/**
	 * �õ����ݼ������������ļ���·��
	 * 
	 * @param dataset
	 * @return
	 * @throws TCException
	 */
	private String getExcelPath(TCComponentDataset dataset) throws TCException {
		TCComponentTcFile[] files = dataset.getTcFiles();
		if (files == null || files.length == 0) {
			return null;
		} else {
			return files[0].getFmsFile().getAbsolutePath();
		}
	}

	/**
	 * �õ����е�ѧ�ƣ����ò�ѯ
	 * 
	 * @throws TCException
	 */
	private void getDisciplineMap(TCSession session) throws TCException {
		readyQuery(session);
		TCComponent[] comps = JCI6YFJCUtil.query(textService, queryType,
				"YFJC_Search_Discipline", new String[] { "discipline_name" },
				new String[] { "*" });
		if (comps != null) {
			for (int i = 0; i < comps.length; i++) {
				TCComponentDiscipline discipline = (TCComponentDiscipline) comps[i];
				disciplineMap.put(discipline.getTCProperty("discipline_name")
						.getStringValue(), discipline);
			}
		}
	}

	private void readyQuery(TCSession session) throws TCException {
		if (queryType == null)
			queryType = (TCComponentQueryType) session
					.getTypeComponent("ImanQuery");
		if (textService == null)
			textService = session.getTextService();
	}

	/**
	 * �õ�user��Ӧ��person
	 * 
	 * @param user
	 * @return
	 * @throws TCException
	 */
	private TCComponentPerson getUserPerson(TCComponentUser user)
			throws TCException {
		return (TCComponentPerson) user.getReferenceProperty("person");
	}

	/**
	 * �õ�person���Զ�Ӧ������
	 * 
	 * @return
	 */
	private Integer[] getPersonProCols() {
		int personProSize = personProColMap.size();
		person_pronames = new String[personProSize];
		Integer[] personProCols = new Integer[personProSize];
		int i = 0;
		for (Map.Entry<String, Integer> entry : personProColMap.entrySet()) {
			person_pronames[i] = entry.getKey();
			personProCols[i] = entry.getValue();
			i++;
		}
		return personProCols;
	}

	/**
	 * �޸�person��Ϣ
	 * 
	 * @param person
	 * @param map
	 * @throws TCException
	 */
	private void modifyPersonInfo(TCComponentPerson person,
			Map<Integer, String> map) throws TCException {
		TCProperty[] person_pros = person.getTCProperties(person_pronames);
		for (int i = 0; i < person_pros.length; i++) {
			String s = map.get(personProCols[i]);
			System.out.println(person_pronames[i] + "--->" + s);
			
			
			person_pros[i].setStringValueData(s);
		}
		person.setTCProperties(person_pros);
	}

	/**
	 * �ȵ��ò�ѯ���ҵ�Ŀǰuser������ѧ��
	 * 
	 * @param userid
	 * @return
	 * @throws TCException
	 */
	private Vector<TCComponent> getDisciplineById(String userid)
			throws TCException {
		userid_val[0] = userid;
		TCComponent[] comps = queryDiscipline();
		Vector<TCComponent> arraylist = new Vector<TCComponent>(comps.length);
		Collections.addAll(arraylist, comps);
		return arraylist;
	}

	private void getDisciplineQuery(String query_name, String[] userid_key)
			throws TCException {
		search_discipline_query = (TCComponentQuery) queryType.find(query_name);
		if (search_discipline_query != null) {
			userid_queryAttribute = new String[userid_key.length];
			for (int i = 0; i < userid_key.length; ++i) {
				userid_queryAttribute[i] = textService
						.getTextValue(userid_key[i]);
				if (userid_queryAttribute[i] == null
						|| userid_queryAttribute[i].trim().equals("")) {
					userid_queryAttribute[i] = userid_key[i];
				}
			}
		}
	}

	/**
	 * ���ò�ѯYFJC_SearchDisciplineByID
	 * 
	 * @return
	 * @throws TCException
	 */
	private TCComponent[] queryDiscipline() throws TCException {
		return search_discipline_query.execute(userid_queryAttribute,
				userid_val);
	}

	//
	private void setDiscipline(Vector<TCComponentDiscipline> discipline,
			Map<Integer, String> map, TCComponentUser user) throws TCException {
		// �õ��û�ԭ������ѧ��
		Vector<TCComponent> disciplineVec = getDisciplineById(map
				.get(user_id_col));
		// �õ���ǰ��Ҫ���õķ��ʼ���ѧ��
		TCComponentDiscipline rateLevel = disciplineMap.get(map
				.get(rateLevel_col));
		need_set_discipline.removeAllElements();
		// ���ʼ���ѧ��
		if (rateLevel != null && !disciplineVec.contains(rateLevel)) {
			System.out.println("ԭ���ʼ��𲻴���,��Ҫ��������....");
			need_set_discipline.add(rateLevel);
		} else {
			System.out.println("ԭ���ʼ����Ѿ�����,����Ҫ��������....");
			disciplineVec.remove(rateLevel);
		}
		// ����ѧ��
		for (int i = 0, size = discipline.size(); i < size; i++) {
			TCComponentDiscipline discp = discipline.get(i);
			if (disciplineVec.contains(discp)) {
				System.out.println("ԭѧ�ƴ���,����Ҫ��������....");
				disciplineVec.remove(discp);
			} else {
				if (!need_set_discipline.contains(discp)) {
					System.out.println("ԭѧ�Ʋ�����,��Ҫ��������....");
					need_set_discipline.add(discp);
				}
			}
		}

		// ������ѧ��
		for (int i = 0, size = need_set_discipline.size(); i < size; i++) {
			TCComponentDiscipline discp = need_set_discipline.get(i);
			System.out.println("discp--->" + discp);
			System.out.println("user--->" + user);
			discp.add("TC_discipline_member", user);
		}

		// ���Ƴ�ԭѧ��
		for (int i = 0, size = disciplineVec.size(); i < size; i++) {
			TCComponentDiscipline discp = (TCComponentDiscipline) disciplineVec
					.get(i);
			discp.remove("TC_discipline_member", user);
		}

	}

}
