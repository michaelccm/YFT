package com.yfjcebp.reorientation.user.privileges;

import java.util.Map;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentGroupMemberType;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class ReoUserPrivilegesOperation extends AbstractAIFOperation {

	private Map<String, TCComponent[]> map;
	private Map<String, TCComponentUser> userMap;
	private TCSession session;

	public ReoUserPrivilegesOperation(TCSession session,
			Map<String, TCComponent[]> map, Map<String, TCComponentUser> userMap) {
		this.session = session;
		this.map = map;
		this.userMap = userMap;
	}

	// modify 2014-6-9
	@Override
	public void executeOperation() throws Exception {
		// TODO Auto-generated method stub
		for (Map.Entry<String, TCComponent[]> entry : map.entrySet()) {
			String user_id = entry.getKey();
			//System.out.println("user_id---->" + user_id);
			TCComponent[] results = entry.getValue();
			TCComponentUser user = userMap.get(user_id);
			// 得到该用户的默认组
			TCComponentGroup defultGroup = (TCComponentGroup) user
					.getRelatedComponent("default_group");
			//2014-6-25得到默认角色
			TCComponentRole defaultRole = defultGroup.getDefaultRole();
			
			//System.out.println("default group name is "+ defultGroup.getGroupName());
					
			TCComponentGroupMember defaultGroupMember = getGroupMember(user,
					defultGroup);
			//获得默认的角色
			TCComponentRole role = defaultGroupMember.getRole();
			for (int i = 0; i < results.length; i++) {
				if (results[i] instanceof TCComponentGroupMember) {
					TCComponentGroupMember groupMember = (TCComponentGroupMember) results[i];
					// 获得ProjectTeam对象
					TCComponent projectTeam = groupMember
							.getRelatedComponent("group");
					if (projectTeam != null) {
						TCComponent[] project_members = projectTeam
								.getRelatedComponents("project_members");
						if (project_members != null
								&& project_members.length > 0) {
							for (int j = 0; j < project_members.length; j++) {
								//System.out.println(i + "--->"	+ "project_members[" + j+ "]  type------->"+ project_members[j].getType());
									
										
										
								if (project_members[j] instanceof TCComponentGroupMember) {
									TCComponentGroupMember project_member = (TCComponentGroupMember) project_members[j];
									// 得到该 GroupMember 属性“user_name”
									String user_name = project_member
											.getProperty("user_name");
									if (user_name != null
											&& user_name.equals(user_id)) {
										if (project_member.equals(defaultGroupMember)) {
											//System.out.println("GroupMember is already  defaultgroupMember,skip......");
													
										} else {
											// 判断默认组是否已经存在
											// 移除旧的并设值默认组的groupmember
											if (!isInArray(defaultGroupMember,
													projectTeam)) {
												//System.out.println("defaultGroupMember is not exit in projectTeam,add...");
														
												projectTeam.add(
														"project_members",
														defaultGroupMember, j);
											}else{
												//System.out.println("defaultGroupMember is already exit in projectTeam,not add...");
												  
											}
											
											projectTeam.remove(
													"project_members",
													project_member);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	// 判断指定对象是否在数组中
	private boolean isInArray(TCComponentGroupMember member,
			TCComponent projectTeam) throws TCException {
		TCComponent[] project_members = projectTeam
				.getRelatedComponents("project_members");
		if (project_members != null && project_members.length > 0) {
			for (int i = 0; i < project_members.length; i++) {
				if (project_members[i].equals(member)) {
					return true;
				}
			}
		}
		return false;
	}

	// 得到用户默认组所在的groupMember
	private TCComponentGroupMember getGroupMember(TCComponentUser user,
			TCComponentGroup group) throws TCException {
		TCComponentGroupMemberType groupMemType = (TCComponentGroupMemberType) session
				.getTypeComponent(TCComponentGroupMemberType.TYPE_NAME);
		TCComponentGroupMember[] groupMembers = groupMemType.find(user, group);
		if (groupMembers != null && groupMembers.length > 0) {
			//System.out.println("groupMembers length--->" + groupMembers.length);
			//判断当前groupmember是否为激活
			for(int i=0;i<groupMembers.length;i++){
				if(!groupMembers[i].getTCProperty("status").getLogicalValue()){
					return groupMembers[i];
				}
				
			}
		}
		return null;
	}

}
