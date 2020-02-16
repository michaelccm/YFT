package com.yfjcebp.reorientation.user.privileges;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDiscipline;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentGroupMemberType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.MLabel;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.iButton;
import com.teamcenter.rac.util.iTextField;


public class ReoUserPrivilegesDialog extends AbstractAIFDialog implements
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TCSession session;
	private iTextField textField;
	private iButton okButton;
	private Registry reg;
	private TCComponentUserType userType;
	private TCUserService userService;

	public ReoUserPrivilegesDialog(TCSession session) {
		super(true);
		this.session = session;
		userService = session.getUserService();
	}
	
	// 初始化界面
	protected void initDialog() {
		reg = Registry.getRegistry(this);
		setTitle(reg.getString("dialog.title"));
		setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());
		MLabel label = new MLabel(reg.getString("label.text"));
		textField = new iTextField(20);
		JPanel s_panel = new JPanel(new GridLayout(3, 1));
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(label);
		panel.add(textField);
		s_panel.add(new JPanel());
		s_panel.add(panel);
		s_panel.add(new JPanel());
		okButton = new iButton(reg.getString("okbutton.name"));
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		okButton.setEnabled(false);
		iButton cancelButton = new iButton(reg.getString("cancelbutton.name"));
		cancelButton.setActionCommand("cancel");
		textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {// 这是更改操作的处理

			}

			@Override
			public void insertUpdate(DocumentEvent e) {// 这是插入操作的处理
				String s = textField.getText().trim();
				if (!s.equals("")) {
					okButton.setEnabled(true);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {// 这是删除操作的处理
				String s = textField.getText().trim();
				if (s.equals("")) {
					okButton.setEnabled(false);
				}
			}
		});
		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		mainPanel.add(BorderLayout.CENTER, s_panel);
		mainPanel.add(BorderLayout.SOUTH, buttonPanel);
		add(mainPanel);
		setPreferredSize(new Dimension(500, 300));
		centerToScreen();
		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent actionevent) {
		// TODO Auto-generated method stub
		String comVal = actionevent.getActionCommand();
		if (comVal.equals("ok")) {
			// 判断用户输入的用户id是否存在
			try {
				Vector<String> userIdVec = new Vector<String>();
				Map<String, TCComponentUser> userMap = new HashMap<String, TCComponentUser>();
				String textVal = textField.getText();
				String[] user_ids = textVal.split(";");
				StringBuffer sb = new StringBuffer(
						reg.getString("user_id_no_exist"));
				userType = (TCComponentUserType) session
						.getTypeComponent("User");
				for (int i = 0; i < user_ids.length; i++) {
					String user_id = user_ids[i];
					userIdVec.add(user_id);
					TCComponentUser user = findUser(user_id);
					if (user == null) {
						sb.append("\r\n").append(user_id);
					} else {
						userMap.put(user_id, user);
					}
				}
				String user_id_not_exit = sb.toString();
				if (!user_id_not_exit.trim().equals(
						reg.getString("user_id_no_exist"))) {
					MessageBox
							.post(user_id_not_exit,
									reg.getString("info.title"),
									MessageBox.INFORMATION);
				} else {
					Map<String,String> disSqlMap = new HashMap<String, String>();
					// add 2014-6-19 modify signoff groupMember
					// 得到系统查询YFJC_SearchforSignoff
					TCComponentQuery signoff_query = getQuery("YFJC_SearchforSignoff");
					
					// 得到系统查询YFJC_SearchDisciplineBytransID
					TCComponentQuery discipline_query = getQuery("YFJC_SearchDisciplineBytransID");
					// 得到系统查询YFJC_SearchAssignment
					TCComponentQuery assignment_query = getQuery("YFJC_SearchAssignment");
					StringBuffer valBuffer = new StringBuffer();
					Map<String, TCComponentDiscipline> discipline_name = new HashMap<String, TCComponentDiscipline>();// SDT
					for (int i = 0, len = userIdVec.size(); i < len; i++) {
						String user_id = userIdVec.get(i);
						// 调用系统查询YFJC_Search for Project Member
						String[] textName = new String[] { "Id" };
						String[] textVals = new String[] { user_id };
						if (signoff_query != null) {
							TCComponent[] results = query(signoff_query,
									textName, textVals);
							//System.out.println("YFJC_SearchforSignoff result -->"+ results);
									
											
							if (results != null && results.length > 0) {
								//System.out.println("YFJC_SearchforSignoff result count -->"+ results.length);
										
												
								TCComponentUser user = userMap.get(user_id);
								// 得到该用户的默认组
								TCComponentGroup defultGroup = (TCComponentGroup) user
										.getRelatedComponent("default_group");
								TCComponentGroupMember defaultGroupMember = getGroupMember(
										user, defultGroup);
								for (int j = 0; j < results.length; j++) {
									if (results[j] instanceof TCComponentSignoff) {
										TCComponentSignoff signoff = (TCComponentSignoff) results[j];
										TCComponentGroupMember groupMember = signoff
												.getGroupMember();
										//System.out.println("defaultGroupMember--->"+ defaultGroupMember);
												
														
										//System.out.println("groupMember--->"+ groupMember);
												
										if (!defaultGroupMember
												.equals(groupMember)) {
											signoff.setReferenceProperty(
													"group_member",
													defaultGroupMember);
										} else {
											//System.out.println("signoff is always defaultgroupmember,skip...");
													
										}
									}
								}
							}
						}
						// 调用查询YFJC_SearchDisciplineBytransID
						if (discipline_query != null
								&& assignment_query != null) {
							valBuffer.setLength(0);
							discipline_name.clear();
							//textVals[0] = "aanli";
							TCComponent[] disciplines = query(discipline_query,
									textName, textVals);
							if (disciplines != null && disciplines.length > 0) {
								for (int j = 0; j < disciplines.length; j++) {
									// 得到学科的名称
									if (disciplines[j] instanceof TCComponentDiscipline) {
										String dis_name = disciplines[j]
												.getTCProperty(
														"discipline_name")
												.getStringValue();
										if (dis_name.startsWith("SDT")) {
											discipline_name
													.put(dis_name,
															(TCComponentDiscipline) disciplines[j]);
										}
										if (valBuffer.toString().equals("")) {
											valBuffer.append(dis_name);
										} else {
											valBuffer.append(";").append(
													dis_name);
										}
									}
								}
							}
							if (discipline_name.size() == 0) {
								//System.out.println("SDT不存在");
							} else {
								// 再调用查询YFJC_SearchAssignment
								String[] assign_keys = new String[] {
										"Id", "NOTEQUALTO" };
								String[] assign_vals = new String[] { user_id,
										valBuffer.toString() };
								//String[] assign_vals = new String[] { "achen113",
								//		"TC_all;SDT_Design Engineer" };
								TCComponent[] assignments = query(
										assignment_query, assign_keys,
										assign_vals);
								if (assignments != null
										&& assignments.length > 0) {
									//System.out.println("assignments.length--->"+assignments.length);
									//String[] disNames = new String[]{"discipline","fnd0AssignedDiscipline"};									
									for (int j = 0; j < assignments.length; j++) {
//										TCComponentDiscipline distag = discipline_name.values().iterator().next();
//										TCProperty[] pros = assignments[j].getTCProperties(disNames);
//										pros[0].setReferenceValue(distag);
//										pros[1].setReferenceValue(distag);
//										assignments[j].setTCProperties(pros);
										userService.call("open_or_close_pass",
												new Object[] { 1 });
										try{
											assignments[j].delete();
										}catch(Exception e){
											//System.out.println("需要更改数据库");
											TCComponentDiscipline distag = discipline_name.values().iterator().next();
											disSqlMap.put(assignments[j].getUid(), distag.getUid());
										}
										userService.call("open_or_close_pass",
												new Object[] { 0 });
									}
								}
							}
						}
					}
					
					//修改数据库
					if(updateDB(disSqlMap)){
						// 得到系统查询YFJC_Search for Project Member
						TCComponentQuery query = getQuery("YFJC_Search for Project Member");
						if (query == null) {
							MessageBox.post(reg.getString("project_member_null"),
									reg.getString("info.title"),
									MessageBox.INFORMATION);
						} else {
							Map<String, TCComponent[]> map = new HashMap<String, TCComponent[]>();
							StringBuffer member_sb = new StringBuffer(
									reg.getString("group_member_null"));
							for (int i = 0, len = userIdVec.size(); i < len; i++) {
								String user_id = userIdVec.get(i);
								// 调用系统查询YFJC_Search for Project Member
								String[] textName = new String[] { "Id" };
								String[] textVals = new String[] { user_id };
								TCComponent[] results = query(query, textName,
										textVals);
								if (results == null || results.length == 0) {
									member_sb.append("\r\n").append(user_id);
								} else {
									//System.out.println(user_id+ "---->query count is "+ results.length);
											
											
									map.put(user_id, results);
								}
							}
							String member_null = member_sb.toString();
							if (!member_null.trim().equals(
									reg.getString("group_member_null"))) {
								MessageBox.post(member_null,
										reg.getString("info.title"),
										MessageBox.INFORMATION);
							} else {
								ReoUserPrivilegesOperation operation = new ReoUserPrivilegesOperation(
										session, map, userMap);
								operation.executeOperation();
								dispose();
								MessageBox.post("Reorientation Success",
										reg.getString("info.title"),
										MessageBox.INFORMATION);
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MessageBox.post("Exception", reg.getString("info.title"),
						MessageBox.INFORMATION);
			}
		} else if (comVal.equals("cancel")) {
			dispose();
		}
	}

	// 查询用户是否存在
	private TCComponentUser findUser(String user_id) {
		try {
			TCComponentUser user = userType.find(user_id);
			return user;
		} catch (TCException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	// 得到系统查询
	private TCComponentQuery getQuery(String queryName) throws TCException {
		TCComponentQueryType queryType = (TCComponentQueryType) session
				.getTypeComponent("ImanQuery");
		return (TCComponentQuery) queryType.find(queryName);
	}

	// 调用系统查询
	private TCComponent[] query(TCComponentQuery query, String[] textName,
			String[] textVals) throws TCException {
		TCTextService textService = session.getTextService();
		String[] searchAttributes = new String[textName.length];
		for (int i = 0; i < textName.length; i++) {
			searchAttributes[i] = textName[i];
		}
		return query.execute(searchAttributes, textVals);
	}

	// 得到用户默认组所在的groupMember
	private TCComponentGroupMember getGroupMember(TCComponentUser user,
			TCComponentGroup group) throws TCException {
		TCComponentGroupMemberType groupMemType = (TCComponentGroupMemberType) session
				.getTypeComponent(TCComponentGroupMemberType.TYPE_NAME);
		TCComponentGroupMember[] groupMembers = groupMemType.find(user, group);
		if (groupMembers != null && groupMembers.length > 0) {
			//System.out.println("groupMembers length--->" + groupMembers.length);
			// 判断当前groupmember是否为激活
			for (int i = 0; i < groupMembers.length; i++) {
				//System.out.println("status--->"+ groupMembers[i].getTCProperty("status").getLogicalValue());
						
								
				if (!groupMembers[i].getTCProperty("status").getLogicalValue()) {
					return groupMembers[i];
				}
			}
		}
		return null;
	}
	
	private boolean updateDB(Map<String,String> disSqlMap){
		boolean flag = true;
		if(disSqlMap.size() > 0){
			String DB_URL = reg.getString("DB_URL");
			String DB_User = reg.getString("DB_User");
			String DB_Password = reg.getString("DB_Password");
			//System.out.println(DB_URL + "---" + DB_User + "---" + DB_Password);
			SqlUtil util = new SqlUtil(DB_URL, DB_User, DB_Password);
			Connection conn = util.getConnection();
			try {
				util.execute(conn, disSqlMap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flag = false;
				MessageBox.post("操作数据库失败", reg.getString("info.title"),
						MessageBox.INFORMATION);
			}finally{
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
}
