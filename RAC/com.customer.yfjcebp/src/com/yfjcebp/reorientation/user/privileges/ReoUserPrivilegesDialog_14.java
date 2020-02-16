package com.yfjcebp.reorientation.user.privileges;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponent;
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
import com.teamcenter.rac.util.MLabel;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.iButton;
import com.teamcenter.rac.util.iTextField;

public class ReoUserPrivilegesDialog_14 extends AbstractAIFDialog implements
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

	public ReoUserPrivilegesDialog_14(TCSession session) {
		super(true);
		this.session = session;
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
		setPreferredSize(new Dimension(500, 150));
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
					// add 2014-6-19 modify signoff groupMember
					// 得到系统查询YFJC_SearchforSignoff
					TCComponentQuery signoff_query = getQuery("YFJC_SearchforSignoff");
					if (signoff_query != null) {
						for (int i = 0, len = userIdVec.size(); i < len; i++) {
							String user_id = userIdVec.get(i);
							// 调用系统查询YFJC_Search for Project Member
							String[] textName = new String[] { "user_id" };
							String[] textVals = new String[] { user_id };
							TCComponent[] results = query(signoff_query,
									textName, textVals);
							System.out.println("YFJC_SearchforSignoff result -->"+ results);
							if (results != null && results.length > 0) {
								System.out.println("YFJC_SearchforSignoff result count -->"+ results.length );
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
										System.out.println("defaultGroupMember--->"+defaultGroupMember);
										System.out.println("groupMember--->"+groupMember);
										if (!defaultGroupMember
												.equals(groupMember)) {
											signoff.setReferenceProperty(
													"group_member",
													defaultGroupMember);
										} else {
											System.out
													.println("signoff is always defaultgroupmember,skip...");
										}
									}
								}
							}
						}
					}

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
							String[] textName = new String[] { "user_id" };
							String[] textVals = new String[] { user_id };
							TCComponent[] results = query(query, textName,
									textVals);
							if (results == null || results.length == 0) {
								member_sb.append("\r\n").append(user_id);
							} else {
								System.out.println(user_id+"---->query count is "+ results.length);
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
			searchAttributes[i] = textService.getTextValue(textName[i]);
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
			System.out.println("groupMembers length--->" + groupMembers.length);
			// 判断当前groupmember是否为激活
			for (int i = 0; i < groupMembers.length; i++) {
				System.out.println("status--->"+groupMembers[i].getTCProperty("status").getLogicalValue());
				if (!groupMembers[i].getTCProperty("status").getLogicalValue()) {
					return groupMembers[i];
				}
			}
		}
		return null;
	}

}
