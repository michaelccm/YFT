package com.teamcenter.rac.project.common;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.AIFComponentEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentPropertyChangeEvent;
import com.teamcenter.rac.aif.kernel.AbstractAIFComponentContextList;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.aifrcp.AifrcpPlugin;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.explorer.common.TCComponentSearch;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentGroupMemberType;
import com.teamcenter.rac.kernel.TCComponentGroupType;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentRoleType;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.UserList;
import com.teamcenter.rac.kernel.services.IPropertyPolicyService;
import com.teamcenter.rac.project.IProjectAdminApplicationPanel;
import com.teamcenter.rac.project.ProjectDataPanel;
import com.teamcenter.rac.project.ProjectSelectionPanel;
import com.teamcenter.rac.project.ProjectUtil;
import com.teamcenter.rac.project.nodes.OrgTreeContentNode;
import com.teamcenter.rac.project.nodes.ProjectTeamContentNode;
import com.teamcenter.rac.project.views.AbstractTCAdminApplicationViewHelper;
import com.teamcenter.rac.project.views.ProjectAMView;
import com.teamcenter.rac.project.views.ProjectTreeView;
import com.teamcenter.rac.query.QueryService;
import com.teamcenter.rac.services.ISessionService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.OSGIUtil;
import com.teamcenter.services.internal.rac.administration.OrganizationManagementService;
import com.teamcenter.services.internal.rac.administration._2011_06.OrganizationManagement.RoleUsers;
import com.teamcenter.services.internal.rac.administration._2012_10.OrganizationManagement;
import com.teamcenter.services.internal.rac.administration._2012_10.OrganizationManagement.GetOrganizationUserMembersResponse;
import com.teamcenter.services.internal.rac.administration._2012_10.OrganizationManagement.GroupElement;
import com.teamcenter.services.internal.rac.administration._2012_10.OrganizationManagement.OrganizationMembersInput;
import com.teamcenter.services.rac.administration.AuthorizationService;
import com.teamcenter.services.rac.administration._2007_06.Authorization;
import com.teamcenter.services.rac.administration._2007_06.Authorization.NameAuthorizationList;
import com.teamcenter.services.rac.administration._2007_06.Authorization.NameAuthorizationResponse;
import com.teamcenter.services.rac.administration._2007_06.Authorization.NameInfo;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core.ProjectLevelSecurityService;
import com.teamcenter.services.rac.core._2007_01.DataManagement;
import com.teamcenter.services.rac.core._2007_01.DataManagement.VecStruct;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ModifyProjectsInfo;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectClientId;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectInformation;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectOpsResponse;
import com.teamcenter.services.rac.core._2012_09.ProjectLevelSecurity.ProjectTeamsResponse;
import com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ModifyProjectsInfo2;
import com.teamcenter.services.rac.core._2017_05.ProjectLevelSecurity.ProjectInformation2;
import com.teamcenter.soa.common.ObjectPropertyPolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ProjectManager implements InterfaceAIFComponentEventListener {
	private static final Logger logger = Logger.getLogger(ProjectManager.class);
	private ProjectLevelSecurityService prjLevelSecurityService = ProjectLevelSecurityService
			.getService(RACUIUtil.getTCSession());
	DataManagementService dmService;
	private TCPreferenceService prefService;
	private static ProjectManager projectManager = null;
	private List<IProjectAdminApplicationPanel> lstProjectChangeListener = new ArrayList();
	private TCComponentProject[] accessible;
	private boolean mustDoRefresh = false;
	private TCComponentQuery gmQuery = null;
	private TCQueryClause[] gmclauses = null;
	private AIFComponentContext[] mtargetContexts = null;
	public static Map<TCComponent, TCComponent[]> m_cacheMap = new HashMap();

	@Deprecated
	public static Map<String, UserList> m_cacheUserListMap = new HashMap();

	@Deprecated
	private static Map<String, TCComponentUser> m_cacheUsersMap = new HashMap();
	public static Map<IC_GroupRoleHashable, TCComponent[]> m_cacheGroupMembersMap = new HashMap();
	public static final String PROJECT_AM_MENU_VISIBLE = "PROJECT_AM_MENU_VISIBLE";
	private static Object app = null;

	public boolean isAMRuleEditable() {
		return (getApp().isPAPrivileged())
				&& ((isSA()) || (isProjectAdministratorAllowedToEdit()));
	}

	public void enableDisableAMMenu(boolean paramBoolean) {
		if ((isAMRuleEditable()) && (paramBoolean))
			Activator.getDefault().setToggleProperty("PROJECT_AM_MENU_VISIBLE",
					true, this);
		else
			Activator.getDefault().setToggleProperty("PROJECT_AM_MENU_VISIBLE",
					false, this);
	}

	private boolean isSA() {
		boolean bool = false;
		try {
			TCSession localTCSession = null;
			localTCSession = getTCSession();
			Object[] arrayOfObject = { TCComponentProject
					.getDisplayName(localTCSession) };
			AuthorizationService localAuthorizationService = AuthorizationService
					.getService(localTCSession);
			Authorization.NameInfo[] arrayOfNameInfo = new Authorization.NameInfo[1];
			arrayOfNameInfo[0] = new Authorization.NameInfo();
			if (arrayOfObject != null)
				arrayOfNameInfo[0].name = arrayOfObject[0].toString();
			arrayOfNameInfo[0].ruleDomain = "application";
			Authorization.NameAuthorizationList localNameAuthorizationList = localAuthorizationService
					.checkAuthorizationAccess(localTCSession.getUser(),
							localTCSession.getGroup(),
							localTCSession.getRole(), arrayOfNameInfo);
			Authorization.NameAuthorizationResponse localNameAuthorizationResponse = localNameAuthorizationList.output[0];
			bool = localNameAuthorizationResponse.verdict;
		} catch (Exception localException) {
			bool = false;
		}
		return bool;
	}

	@Deprecated
	public static TCComponentUser getCachedUser(String paramString)
			throws TCException {
		getInstance();
		return (TCComponentUser) m_cacheUsersMap.get(paramString);
	}

	@Deprecated
	public static TCComponentUser getUser(String paramString)
			throws TCException {
		TCComponentUser localTCComponentUser = getCachedUser(paramString);
		if (localTCComponentUser == null) {
			localTCComponentUser = UserList.getUser(paramString);
			m_cacheUsersMap.put(paramString, localTCComponentUser);
		}
		return localTCComponentUser;
	}

	public TCSession getTCSession() {
		try {
			return (TCSession) Activator.getDefault().getSessionService()
					.getSession(TCSession.class.getName());
		} catch (Exception localException) {
			logger.error(localException.getClass().getName(), localException);
		}
		return null;
	}

	private ProjectManager() {
		getTCSession().addAIFComponentEventListener(this);
		this.dmService = DataManagementService.getService(RACUIUtil
				.getTCSession());
		this.prefService = getTCSession().getPreferenceService();
		try {
			setApp(new AbstractTCAdminApplicationViewHelper());
		} catch (Exception localException) {
			logger.error(localException.getClass().getName(), localException);
		}
	}

	public static void setApp(
			AbstractTCAdminApplicationViewHelper paramAbstractTCAdminApplicationViewHelper) {
		app = paramAbstractTCAdminApplicationViewHelper;
	}

	public static AbstractTCAdminApplicationViewHelper getApp() {
		if (app == null)
			try {
				setApp(new AbstractTCAdminApplicationViewHelper());
			} catch (Exception localException) {
				logger.error(localException.getClass().getName(),
						localException);
			}
		return (AbstractTCAdminApplicationViewHelper) app;
	}

	public ProjectLevelSecurity.ProjectTeamsResponse getProjectTeamMembers(
			ProjectLevelSecurity.ProjectClientId[] paramArrayOfProjectClientId) {
		return this.prjLevelSecurityService
				.getProjectTeams(paramArrayOfProjectClientId);
	}

	public static void setProjectTeamMembersPropertyPolicy() {
		IPropertyPolicyService localIPropertyPolicyService = (IPropertyPolicyService) OSGIUtil
				.getService(Activator.getDefault(),
						IPropertyPolicyService.class);
		ObjectPropertyPolicy localObjectPropertyPolicy = new ObjectPropertyPolicy();
		localObjectPropertyPolicy.addType("Group",
				TCComponentProject.groupPropertyNames);
		localObjectPropertyPolicy.addType("GroupMember",
				TCComponentProject.groupMemberPropertyNames);
		localObjectPropertyPolicy.addType("User",
				TCComponentProject.userPropertyNames);
		String str = localIPropertyPolicyService
				.createPropertyPolicy(localObjectPropertyPolicy);
		localIPropertyPolicyService.setPropertyPolicy(str);
	}

	public static void restorePropertyPolicy() {
		IPropertyPolicyService localIPropertyPolicyService = (IPropertyPolicyService) OSGIUtil
				.getService(Activator.getDefault(),
						IPropertyPolicyService.class);
		localIPropertyPolicyService.restorePropertyPolicy();
	}

	public ProjectLevelSecurity.ProjectOpsResponse createProjects(
			ProjectInformation2[] paramArrayOfProjectInformation2) {
		return this.prjLevelSecurityService
				.createProjects2(paramArrayOfProjectInformation2);
	}

	public ProjectLevelSecurity.ProjectOpsResponse modifyProjects(
			ModifyProjectsInfo2[] paramArrayOfModifyProjectsInfo2) {
		return this.prjLevelSecurityService
				.modifyProjects2(paramArrayOfModifyProjectsInfo2);
	}

	public static ProjectManager getInstance() {
		if (projectManager == null)
			projectManager = new ProjectManager();
		return projectManager;
	}

	private boolean loadAllProjects() {
		TCPreferenceService localTCPreferenceService = getTCSession()
				.getPreferenceService();
		String str = localTCPreferenceService
				.getStringValue("TC_Load_All_Projects");
		return (str == null) || (!str.equalsIgnoreCase("FALSE"));
	}

	public List<TCComponentProject> getProjects() {
		List<TCComponentProject> localObject = null;
		if (!loadAllProjects())
			return localObject;
		TCSession localTCSession = null;
		ISessionService localISessionService = AifrcpPlugin.getSessionService();
		if (localISessionService != null)
			try {
				localTCSession = (TCSession) localISessionService
						.getSession(TCSession.class.getName());
			} catch (Exception localException) {
				logger.error(localException.getClass().getName(),
						localException);
			}
		if ((localTCSession != null)
				&& ((this.accessible == null) || (this.mustDoRefresh))) {
			this.accessible = getAccessibleProjects(localTCSession);
			this.mustDoRefresh = false;
			if (this.accessible != null) {
				localObject = new ArrayList();
				ArrayList localArrayList = new ArrayList();
				for (int i = 0; i < this.accessible.length; i++) {
					((List) localObject).add(this.accessible[i]);
					try {
						TCComponentUser localTCComponentUser = this.accessible[i]
								.getPAUser();
						if (localTCComponentUser == localTCSession.getUser())
							localArrayList.add(this.accessible[i]
									.getProperty("project_id"));
					} catch (TCException localTCException) {
						logger.error(localTCException.getClass().getName(),
								localTCException);
					}
				}
				ProjectUtil.setPAProjectList(localArrayList);
			}
		}
		return localObject;
	}

	public boolean areProjectsLoaded() {
		return this.accessible != null;
	}

	public TCComponentProject[] getLoadedProjects() {
		return this.accessible;
	}

	// modify by wuwei
	private TCComponentProject[] getAccessibleProjects(TCSession paramTCSession) {
		TCComponentProject[] arrayOfTCComponentProject1 = null;
		try {
			TCSession localTCSession = paramTCSession;

			// 修改第二个 bool参数为false
			arrayOfTCComponentProject1 = ProjectSelectionPanel
					.getProjectsForUser(localTCSession.getUser(), true, false,
							false, localTCSession);
			if (arrayOfTCComponentProject1 != null) {
				System.out
						.println("ProjectSelectionPanel::getProjectsForUser   arrayOfTCComponentProject1.length-->"
								+ arrayOfTCComponentProject1.length);
			} else {
				System.out
						.println("ProjectSelectionPanel::getProjectsForUser arrayOfTCComponentProject1[]==null");
			}

			TCComponent[] arrayOfTCComponent = findProjectsByNameOrID("*", true);
			System.out
					.println("getAccessibleProjects::findProjectsByNameOrID   arrayOfTCComponent.length-->"
							+ arrayOfTCComponent.length);

			if (((arrayOfTCComponentProject1 == null) || (arrayOfTCComponentProject1.length == 0))
					&& (arrayOfTCComponent != null)
					&& (arrayOfTCComponent.length > 0)) {
				arrayOfTCComponentProject1 = new TCComponentProject[arrayOfTCComponent.length];
				for (int i = 0; i < arrayOfTCComponent.length; i++)
					arrayOfTCComponentProject1[i] = ((TCComponentProject) arrayOfTCComponent[i]);
			}
			if ((arrayOfTCComponentProject1 == null)
					|| (arrayOfTCComponentProject1.length == 0))
				return arrayOfTCComponentProject1;
			if ((arrayOfTCComponent != null) && (arrayOfTCComponent.length > 0)) {
				ArrayList localArrayList = new ArrayList();
				// Object localObject1;
				for (TCComponentProject localObject1 : arrayOfTCComponentProject1)
					localArrayList.add(localObject1);
				for (TCComponent localObject1 : arrayOfTCComponent) {
					TCComponentProject localTCComponentProject2 = (TCComponentProject) localObject1;
					if (!localArrayList.contains(localTCComponentProject2))
						localArrayList.add(localTCComponentProject2);
				}
				arrayOfTCComponentProject1 = new TCComponentProject[localArrayList
						.size()];
				for (int k = 0; k < localArrayList.size(); k++) {
					TCComponentProject localTCComponentProject1 = (TCComponentProject) localArrayList
							.get(k);
					arrayOfTCComponentProject1[k] = localTCComponentProject1;
				}
			}
			int j = arrayOfTCComponentProject1.length;
			if (j > 0) {
				String[] arrayOfString = new String[j];
				for (int n = 0; n < j; n++)
					arrayOfString[n] = arrayOfTCComponentProject1[n].getUid();
				ProjectDataPanel.loadObjectsInBulk(arrayOfString,
						localTCSession);
			}
			String[] arrayOfString = { "object_string", "owning_site",
					"fnd0Children", "fnd0Parent" };
			List localList = Arrays.asList(arrayOfTCComponentProject1);
			ProjectDataPanel.loadPropertiesInBulk(localList, arrayOfString);
			TCComponentProject[] arrayOfTCComponentProject2 = ProjectUtil
					.getTopLevelProjects(arrayOfTCComponentProject1);

			System.out
					.println("getAccessibleProjects   arrayOfTCComponentProject2.length-->"
							+ arrayOfTCComponentProject2.length);
			return paixu(arrayOfTCComponentProject2);
		} catch (Exception localException) {
			logger.error(localException.getClass().getName(), localException);
		}

		System.out
				.println("getAccessibleProjects   arrayOfTCComponentProject1.length-->"
						+ arrayOfTCComponentProject1.length);

		return paixu(arrayOfTCComponentProject1);
	}

	public TCComponentProject[]  paixu(TCComponentProject[] prjArray) {
		MyComparator myComparator=new MyComparator();
		 Arrays.sort(prjArray, myComparator);
		 System.out.println("---paixu Arrays.sort()-----" );
		 return prjArray;
	}

	class MyComparator implements Comparator<TCComponentProject> {

		@Override
		public int compare(TCComponentProject p1, TCComponentProject p2) {
			String h1 = p1.getProjectID();
			String h2 = p2.getProjectID();

			String[] s1 = h1.split("-", -1);
			String[] s2 = h2.split("-", -1);
			int bool = s1[0].compareTo(s2[0]);
			if (bool > 0)
				return 1;
			return -1;
			
		}

	}

	public void addCreatedProjects(TCComponentProject paramTCComponentProject) {
		// ArrayList localObject1;
		if (this.accessible != null) {
			ArrayList localObject1 = new ArrayList();
			for (TCComponentProject localObject2 : this.accessible)
				((ArrayList) localObject1).add(localObject2);
			((ArrayList) localObject1).add(paramTCComponentProject);
			this.accessible = new TCComponentProject[((ArrayList) localObject1)
					.size()];
			((ArrayList) localObject1).toArray(this.accessible);
			paramTCComponentProject.fireComponentCreateEvent();
		}
		Object localObject2 = this.lstProjectChangeListener.iterator();
		while (((Iterator) localObject2).hasNext()) {
			IProjectAdminApplicationPanel localObject1 = (IProjectAdminApplicationPanel) ((Iterator) localObject2)
					.next();
			((IProjectAdminApplicationPanel) localObject1)
					.projectCreated(paramTCComponentProject);
		}
	}

	public void projectModified(TCComponentProject paramTCComponentProject) {
		if (paramTCComponentProject != null) {
			try {
				paramTCComponentProject.fireComponentChangeEvent();
			} catch (Exception localException) {
				logger.error(localException.getClass().getName(),
						localException);
			}
			Iterator localIterator = this.lstProjectChangeListener.iterator();
			while (localIterator.hasNext()) {
				IProjectAdminApplicationPanel localIProjectAdminApplicationPanel = (IProjectAdminApplicationPanel) localIterator
						.next();
				localIProjectAdminApplicationPanel.projectModified(
						paramTCComponentProject, "", "");
			}
		}
	}

	public void addProjectChangeListener(
			IProjectAdminApplicationPanel paramIProjectAdminApplicationPanel) {
		if (!this.lstProjectChangeListener
				.contains(paramIProjectAdminApplicationPanel))
			this.lstProjectChangeListener
					.add(paramIProjectAdminApplicationPanel);
	}

	public void removeProjectChangeListener(
			IProjectAdminApplicationPanel paramIProjectAdminApplicationPanel) {
		this.lstProjectChangeListener
				.remove(paramIProjectAdminApplicationPanel);
	}

	public void deleteProject(TCComponentProject paramTCComponentProject) {
		Object localObject1;
		if (this.accessible != null) {
			paramTCComponentProject.fireComponentDeleteEvent();
			localObject1 = new ArrayList();
			for (TCComponentProject localObject2 : this.accessible)
				if (localObject2 != paramTCComponentProject)
					((ArrayList) localObject1).add(localObject2);
			this.accessible = new TCComponentProject[((ArrayList) localObject1)
					.size()];
			((ArrayList) localObject1).toArray(this.accessible);
		}
		Object localObject2 = this.lstProjectChangeListener.iterator();
		while (((Iterator) localObject2).hasNext()) {
			localObject1 = (IProjectAdminApplicationPanel) ((Iterator) localObject2)
					.next();
			((IProjectAdminApplicationPanel) localObject1).projectDeleted(
					paramTCComponentProject, "");
		}
	}

	public ServiceData setProjectProperties(
			HashMap<String, DataManagement.VecStruct> paramHashMap,
			TCComponent[] paramArrayOfTCComponent) {
		return this.dmService.setProperties(paramArrayOfTCComponent,
				paramHashMap);
	}

	public TCComponent[] getGroupsAndRoles(
			TCComponentGroup paramTCComponentGroup) {
		TCComponent[] arrayOfTCComponent = (TCComponent[]) m_cacheMap
				.get(paramTCComponentGroup);
		if (arrayOfTCComponent == null)
			try {
				arrayOfTCComponent = paramTCComponentGroup.getRolesAndGroups();
			} catch (TCException localTCException) {
				logger.error(localTCException.getClass().getName(),
						localTCException);
			} finally {
				if (arrayOfTCComponent != null)
					m_cacheMap.put(paramTCComponentGroup, arrayOfTCComponent);
			}
		return arrayOfTCComponent;
	}

	public TCComponentGroupMember[] getGroupMembers(
			TCComponentUser paramTCComponentUser) {
		TCComponentGroupMember[] arrayOfTCComponentGroupMember = (TCComponentGroupMember[]) m_cacheMap
				.get(paramTCComponentUser);
		if (arrayOfTCComponentGroupMember == null)
			try {
				arrayOfTCComponentGroupMember = paramTCComponentUser
						.getGroupMembers();
				cacheUsersForGroupmembers(arrayOfTCComponentGroupMember);
			} catch (TCException localTCException) {
				logger.error(localTCException.getClass().getName(),
						localTCException);
			} finally {
				if (arrayOfTCComponentGroupMember != null)
					m_cacheMap.put(paramTCComponentUser,
							arrayOfTCComponentGroupMember);
			}
		return arrayOfTCComponentGroupMember;
	}

	public TCComponentGroupMember[] getGroupMembers(
			TCComponentGroup paramTCComponentGroup,
			TCComponentRole paramTCComponentRole) {
		TCComponent[] arrayOfTCComponent = (TCComponent[]) m_cacheGroupMembersMap
				.get(new IC_GroupRoleHashable(paramTCComponentGroup,
						paramTCComponentRole));
		// [Lcom.teamcenter.rac.kernel.TCComponentGroupMember.class
		if ((arrayOfTCComponent != null) && (arrayOfTCComponent.length > 0))
			return (TCComponentGroupMember[]) Arrays.copyOf(arrayOfTCComponent,
					arrayOfTCComponent.length);
		TCComponentGroupMember[] arrayOfTCComponentGroupMember = null;
		try {
			OrganizationManagementService localOrganizationManagementService = OrganizationManagementService
					.getService(getTCSession());
			OrganizationManagement.OrganizationMembersInput localOrganizationMembersInput = new OrganizationManagement.OrganizationMembersInput();
			localOrganizationMembersInput.userID = "*";
			localOrganizationMembersInput.userName = "*";
			localOrganizationMembersInput.groupName = paramTCComponentGroup
					.getFullName();
			localOrganizationMembersInput.roleName = paramTCComponentRole
					.getRoleName();
			localOrganizationMembersInput.includeInactive = true;
			localOrganizationMembersInput.includeSubGroups = false;
			OrganizationManagement.GetOrganizationUserMembersResponse localGetOrganizationUserMembersResponse = localOrganizationManagementService
					.getOrganizationGroupMembers(localOrganizationMembersInput);
			OrganizationManagement.GroupElement localGroupElement = (OrganizationManagement.GroupElement) localGetOrganizationUserMembersResponse.groupElementMap
					.get(paramTCComponentGroup);
			if (localGroupElement != null) {
				RoleUsers[] arrayOfRoleUsers = localGroupElement.members;
				if ((arrayOfRoleUsers != null) && (arrayOfRoleUsers.length > 0))
					arrayOfTCComponentGroupMember = arrayOfRoleUsers[0].members;
			}
		} catch (TCException localTCException) {
			logger.error(localTCException.getClass().getName(),
					localTCException);
		}
		if ((arrayOfTCComponentGroupMember != null)
				&& (arrayOfTCComponentGroupMember.length > 0)) {
			cacheUsersForGroupmembers(arrayOfTCComponentGroupMember);
			m_cacheGroupMembersMap.put(new IC_GroupRoleHashable(
					paramTCComponentGroup, paramTCComponentRole),
					arrayOfTCComponentGroupMember);
		}
		return arrayOfTCComponentGroupMember;
	}

	public void cacheUsersForGroupmembers(
			TCComponentGroupMember[] paramArrayOfTCComponentGroupMember) {
		String[] arrayOfString = { "the_group", "the_role", "the_user",
				"status" };
		try {
			TCComponentType.cacheTCPropertiesSet(
					paramArrayOfTCComponentGroupMember, arrayOfString, true);
			TCComponentUser[] arrayOfTCComponentUser = new TCComponentUser[paramArrayOfTCComponentGroupMember.length];
			for (int i = 0; i < paramArrayOfTCComponentGroupMember.length; i++)
				arrayOfTCComponentUser[i] = paramArrayOfTCComponentGroupMember[i]
						.getUser();
			ArrayList localArrayList = new ArrayList(
					Arrays.asList(arrayOfTCComponentUser));
			TCComponentType.getTCPropertiesSet(localArrayList,
					TCComponentProject.userPropertyNames);
		} catch (TCException localTCException) {
			logger.error(localTCException.getClass().getName(),
					localTCException);
		}
	}

	@Deprecated
	public void cacheUsersByFormattedToString(List<TCComponentUser> paramList) {
		Iterator localIterator = paramList.iterator();
		while (localIterator.hasNext()) {
			TCComponentUser localTCComponentUser = (TCComponentUser) localIterator
					.next();
			String str1;
			String str2;
			try {
				str1 = localTCComponentUser.getProperty("user_id");
				str2 = localTCComponentUser.getProperty("user_name");
			} catch (TCException localTCException) {
				logger.error(localTCException.getClass().getName(),
						localTCException);
				break;
			}
			m_cacheUsersMap.put(
					TCComponentUser.getFormattedToString(str2, str1),
					localTCComponentUser);
		}
	}

	@Deprecated
	public TCComponentGroupMember getGroupMember(
			TCComponentGroup paramTCComponentGroup,
			TCComponentRole paramTCComponentRole, String paramString) {
		String str = "";
		if (paramTCComponentRole != null)
			str = paramTCComponentRole.toString();
		if ((paramTCComponentGroup != null) && (paramTCComponentRole != null)) {
			str = paramTCComponentGroup.toString();
			str = str.concat(paramTCComponentRole.toString());
		}
		TCComponent[] arrayOfTCComponent = (TCComponent[]) m_cacheGroupMembersMap
				.get(str);
		TCComponentGroupMember localTCComponentGroupMember1 = null;
		if (arrayOfTCComponent != null) {
			for (int i = 0; i < arrayOfTCComponent.length; i++) {
				TCComponentGroupMember localTCComponentGroupMember2 = (TCComponentGroupMember) arrayOfTCComponent[i];
				TCComponentUser localTCComponentUser = null;
				try {
					localTCComponentUser = localTCComponentGroupMember2
							.getUser();
				} catch (TCException localTCException) {
					logger.error(localTCException.getClass().getName(),
							localTCException);
				}
				if ((localTCComponentUser != null)
						&& (localTCComponentUser.toString().equals(paramString))) {
					localTCComponentGroupMember1 = (TCComponentGroupMember) arrayOfTCComponent[i];
					break;
				}
			}
		} else {
			getGroupMemberNames(paramTCComponentGroup, paramTCComponentRole,
					ProjectTeamContentNode.class);
			return getGroupMember(paramTCComponentGroup, paramTCComponentRole,
					paramString);
		}
		return localTCComponentGroupMember1;
	}

	@Deprecated
	public UserList getGroupMemberNames(TCComponentGroup paramTCComponentGroup,
			TCComponentRole paramTCComponentRole, Class paramClass) {
		UserList localUserList = new UserList(getTCSession());
		if (paramTCComponentRole != null) {
			String str = paramTCComponentRole.toString();
			if (paramTCComponentGroup != null) {
				str = paramTCComponentGroup.toString();
				str = str.concat(paramTCComponentRole.toString());
			}
			if ((paramClass.equals(TCComponentGroupMember.class))
					&& (paramTCComponentGroup != null)) {
				str = paramTCComponentRole.toString();
				str = str.concat(paramTCComponentGroup.toString());
			}
			if ((paramClass.equals(ProjectTeamContentNode.class))
					|| (paramClass.equals(TCComponentGroupMember.class))
					|| (paramClass.equals(OrgTreeContentNode.class))) {
				TCComponent[] arrayOfTCComponent = (TCComponent[]) m_cacheGroupMembersMap
						.get(str);
				// Object localObject1;
				// Object localObject2;
				// Object localObject4;
				// Object localObject3;
				// Object localObject6;
				if (arrayOfTCComponent == null) {
					arrayOfTCComponent = getGroupMembersProjectAdmin(
							paramTCComponentGroup, paramTCComponentRole,
							paramClass);
					if (arrayOfTCComponent != null) {
						m_cacheGroupMembersMap.put(new IC_GroupRoleHashable(
								paramTCComponentGroup, paramTCComponentRole),
								arrayOfTCComponent);
						String[] localObject1 = new String[] { "the_group",
								"user_name", "the_role", "the_user" };
						ArrayList localArrayList = new ArrayList();
						for (TCComponent localObject2 : arrayOfTCComponent)
							localArrayList.add(localObject2);
						ProjectDataPanel.loadPropertiesInBulk(localArrayList,
								(String[]) localObject1);

						ArrayList localObject2 = new ArrayList();
						for (int m = 0; m < arrayOfTCComponent.length; m++) {
							TCComponentGroupMember localObject4 = (TCComponentGroupMember) arrayOfTCComponent[m];
							try {
								TCComponentUser theUser = ((TCComponentGroupMember) localObject4)
										.getUser();
								((ArrayList) localObject2).add(theUser);
							} catch (TCException localTCException2) {
								logger.error(localTCException2.getClass()
										.getName(), localTCException2);
							}
						}
						String[] localObject3 = new String[] { "user_id",
								"user_name", "object_string", "status" };
						ProjectDataPanel.loadPropertiesInBulk(
								(List) localObject2, (String[]) localObject3);
						String[] localObject4 = new String[((ArrayList) localObject2)
								.size()];
						String[] localObject6 = new String[((ArrayList) localObject2)
								.size()];
						int n = 0;
						Iterator localIterator = ((ArrayList) localObject2)
								.iterator();
						while (localIterator.hasNext()) {
							TCComponentUser localTCComponentUser = (TCComponentUser) localIterator
									.next();
							try {
								localObject4[n] = localTCComponentUser
										.getProperty("user_id");
								localObject6[n] = localTCComponentUser
										.getProperty("user_name");
								m_cacheUsersMap.put(TCComponentUser
										.getFormattedToString(localObject6[n],
												localObject4[n]),
										localTCComponentUser);
							} catch (TCException localTCException4) {
								logger.error(localTCException4.getClass()
										.getName(), localTCException4);
							}
							n++;
						}
						localUserList.setUserIds((String[]) localObject4);
						localUserList.setUsers((String[]) localObject6);
					}
				} else {
					ArrayList localObject1 = new ArrayList();
					for (int i = 0; i < arrayOfTCComponent.length; i++) {
						TCComponentGroupMember localObject2 = (TCComponentGroupMember) arrayOfTCComponent[i];
						try {
							TCComponentUser localObject3 = ((TCComponentGroupMember) localObject2)
									.getUser();
							((ArrayList) localObject1).add(localObject3);
						} catch (TCException localTCException1) {
							logger.error(
									localTCException1.getClass().getName(),
									localTCException1);
						}
					}
					String[] arrayOfString = new String[((ArrayList) localObject1)
							.size()];
					String[] localObject2 = new String[((ArrayList) localObject1)
							.size()];
					int k = 0;
					Object localObject6 = ((ArrayList) localObject1).iterator();
					while (((Iterator) localObject6).hasNext()) {
						TCComponentUser localObject4 = (TCComponentUser) ((Iterator) localObject6)
								.next();
						try {
							arrayOfString[k] = ((TCComponentUser) localObject4)
									.getProperty("user_id");
							localObject2[k] = ((TCComponentUser) localObject4)
									.getProperty("user_name");
							m_cacheUsersMap.put(TCComponentUser
									.getFormattedToString(localObject2[k],
											arrayOfString[k]), localObject4);
						} catch (TCException localTCException3) {
							logger.error(
									localTCException3.getClass().getName(),
									localTCException3);
						}
						k++;
					}
					localUserList.setUserIds(arrayOfString);
					localUserList.setUsers((String[]) localObject2);
				}
			}
		}
		return localUserList;
	}

	@Deprecated
	public TCComponent[] getGroupMembersProjectAdmin(
			TCComponentGroup paramTCComponentGroup,
			TCComponentRole paramTCComponentRole, Class paramClass) {
		return getGroupMembersProjectAdmin(paramTCComponentGroup,
				paramTCComponentRole);
	}

	public TCComponent[] getGroupMembersProjectAdmin(
			TCComponentGroup paramTCComponentGroup,
			TCComponentRole paramTCComponentRole) {
		TCComponentGroupMember[] arrayOfTCComponentGroupMember = null;
		try {
			if (paramTCComponentRole.equals(ProjectTeamTreeManager
					.getProjectAdminRole()))
				return searchByGroupRole(paramTCComponentGroup,
						paramTCComponentRole);
			OrganizationManagementService localOrganizationManagementService = OrganizationManagementService
					.getService(getTCSession());
			OrganizationManagement.OrganizationMembersInput localOrganizationMembersInput = new OrganizationManagement.OrganizationMembersInput();
			localOrganizationMembersInput.userID = "*";
			localOrganizationMembersInput.userName = "*";
			localOrganizationMembersInput.groupName = paramTCComponentGroup
					.getFullName();
			localOrganizationMembersInput.roleName = paramTCComponentRole
					.getRoleName();
			localOrganizationMembersInput.includeInactive = true;
			localOrganizationMembersInput.includeSubGroups = false;
			OrganizationManagement.GetOrganizationUserMembersResponse localGetOrganizationUserMembersResponse = localOrganizationManagementService
					.getOrganizationGroupMembers(localOrganizationMembersInput);
			OrganizationManagement.GroupElement localGroupElement = (OrganizationManagement.GroupElement) localGetOrganizationUserMembersResponse.groupElementMap
					.get(paramTCComponentGroup);
			if (localGroupElement != null) {
				RoleUsers[] arrayOfRoleUsers = localGroupElement.members;
				if ((arrayOfRoleUsers != null) && (arrayOfRoleUsers.length > 0))
					arrayOfTCComponentGroupMember = arrayOfRoleUsers[0].members;
			}
		} catch (Exception localException) {
			logger.error(localException.getClass().getName(), localException);
		}
		return arrayOfTCComponentGroupMember;
	}

	public TCComponentGroupMember getGroupMember(
			TCComponentGroup paramTCComponentGroup,
			TCComponentRole paramTCComponentRole,
			TCComponentUser paramTCComponentUser) {
		try {
			OrganizationManagementService localOrganizationManagementService = OrganizationManagementService
					.getService(getTCSession());
			OrganizationManagement.OrganizationMembersInput localOrganizationMembersInput = new OrganizationManagement.OrganizationMembersInput();
			localOrganizationMembersInput.userID = "*";
			localOrganizationMembersInput.userName = "*";
			localOrganizationMembersInput.groupName = paramTCComponentGroup
					.getFullName();
			localOrganizationMembersInput.roleName = paramTCComponentRole
					.getRoleName();
			localOrganizationMembersInput.includeInactive = true;
			localOrganizationMembersInput.includeSubGroups = false;
			OrganizationManagement.GetOrganizationUserMembersResponse localGetOrganizationUserMembersResponse = localOrganizationManagementService
					.getOrganizationGroupMembers(localOrganizationMembersInput);
			OrganizationManagement.GroupElement localGroupElement = (OrganizationManagement.GroupElement) localGetOrganizationUserMembersResponse.groupElementMap
					.get(paramTCComponentGroup);
			if (localGroupElement != null) {
				RoleUsers[] arrayOfRoleUsers = localGroupElement.members;
				if ((arrayOfRoleUsers != null) && (arrayOfRoleUsers.length > 0)
						&& (arrayOfRoleUsers[0].members != null)
						&& (arrayOfRoleUsers[0].members.length > 0)) {
					cacheUsersForGroupmembers(arrayOfRoleUsers[0].members);
					for (TCComponentGroupMember localTCComponentGroupMember : arrayOfRoleUsers[0].members)
						if (paramTCComponentUser
								.equals(localTCComponentGroupMember.getUser()))
							return localTCComponentGroupMember;
				}
			}
		} catch (Exception localException) {
			logger.error(localException.getClass().getName(), localException);
		}
		return null;
	}

	private TCComponent[] searchByGroupRole(
			TCComponentGroup paramTCComponentGroup,
			TCComponentRole paramTCComponentRole) {
		TCComponentQueryType localTCComponentQueryType = null;
		try {
			localTCComponentQueryType = (TCComponentQueryType) getTCSession()
					.getTypeComponent("ImanQuery");
		} catch (TCException localTCException1) {
			localTCException1.printStackTrace();
		}
		String str1 = "__ACTIVE_group_members";
		String str2 = "*";
		String str3 = "*";
		TCComponent[] arrayOfTCComponent = null;
		if (this.gmQuery == null)
			try {
				this.gmQuery = ((TCComponentQuery) localTCComponentQueryType
						.find(str1));
			} catch (TCException localTCException2) {
				localTCException2.printStackTrace();
			}
		if (paramTCComponentRole != null)
			try {
				str2 = paramTCComponentRole.getProperty("role_name");
				if (paramTCComponentGroup != null)
					str3 = paramTCComponentGroup.getFullName();
			} catch (TCException localTCException3) {
				localTCException3.printStackTrace();
			}
		String str4 = "Role";
		String str5 = "Group";
		String str6 = "Status";
		String str7 = "Group Member Status";
		String str8 = "User";
		ArrayList localArrayList1 = new ArrayList();
		try {
			if (this.gmclauses == null)
				this.gmclauses = this.gmQuery.describe();
			if ((this.gmQuery.toInternalString().equalsIgnoreCase(str1))
					&& (this.gmclauses != null))
				for (int i = 0; i < this.gmclauses.length; i++)
					if (this.gmclauses[i].getAttributeName().contains(
							"group.name")) {
						str5 = this.gmclauses[i].getUserEntryNameDisplay();
						localArrayList1.add(str5);
					} else if (this.gmclauses[i].getAttributeName().contains(
							"role.role_name")) {
						str4 = this.gmclauses[i].getUserEntryNameDisplay();
						localArrayList1.add(str4);
					} else if (this.gmclauses[i].getAttributeName()
							.equalsIgnoreCase("user.status")) {
						str6 = this.gmclauses[i].getUserEntryNameDisplay();
						localArrayList1.add(str6);
					} else if (this.gmclauses[i].getAttributeName()
							.equalsIgnoreCase("status")) {
						str7 = this.gmclauses[i].getUserEntryNameDisplay();
						localArrayList1.add(str7);
					} else if (this.gmclauses[i].getAttributeName()
							.equalsIgnoreCase("user.user_id")) {
						str8 = this.gmclauses[i].getUserEntryNameDisplay();
						localArrayList1.add(str8);
					}
		} catch (Exception localException) {
			logger.error(localException.getClass().getName(), localException);
		}
		if (this.gmQuery.toInternalString().equalsIgnoreCase(str1)) {
			String str9 = getActiveInactiveStatus();
			String[] arrayOfString1 = null;
			String[] arrayOfString2 = null;
			if (str9.equalsIgnoreCase("1")) {
				ArrayList localArrayList2 = new ArrayList();
				Iterator localIterator = localArrayList1.iterator();
				while (localIterator.hasNext()) {
					String localObject = (String) localIterator.next();
					if ((((String) localObject).equalsIgnoreCase(str5))
							|| (((String) localObject).equals(str4))
							|| (((String) localObject).equals(str6))
							|| (((String) localObject).equals(str7)))
						localArrayList2.add(localObject);
				}
				arrayOfString2 = new String[localArrayList2.size()];
				arrayOfString2 = (String[]) localArrayList2
						.toArray(arrayOfString2);
				arrayOfString1 = new String[] { str3, str2, "0", "0" };
				Object localObject = new ArrayList();
				if (arrayOfString1.length > arrayOfString2.length) {
					for (int j = 0; j < arrayOfString2.length; j++)
						((ArrayList) localObject).add(arrayOfString1[j]);
					arrayOfString1 = new String[localArrayList2.size()];
					arrayOfString1 = (String[]) ((ArrayList) localObject)
							.toArray(arrayOfString1);
				}
			} else {
				arrayOfString2 = new String[] { str5, str4 };
				arrayOfString1 = new String[] { str3, str2 };
			}
			arrayOfTCComponent = runQueryHelper(this.gmQuery, arrayOfString2,
					arrayOfString1);
			if (arrayOfTCComponent == null)
				arrayOfTCComponent = new TCComponent[0];
		}
		return arrayOfTCComponent;
	}

	public static String getActiveInactiveStatus() {
		String str1 = "0";
		String str2 = "TC_suppress_inactive_group_members";
		String str3 = "";
		try {
			TCPreferenceService localTCPreferenceService = ((TCSession) Activator
					.getDefault().getSessionService()
					.getSession(TCSession.class.getName()))
					.getPreferenceService();
			str3 = localTCPreferenceService.getStringValue(str2);
			if ((str3 != null)
					&& ((str3.equalsIgnoreCase("0")) || (str3
							.equalsIgnoreCase("1"))))
				return str3;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return str1;
	}

	@Deprecated
	public TCComponent[] getGroupMembers(String paramString1,
			String paramString2) {
		TCComponentGroupMember[] arrayOfTCComponentGroupMember = null;
		try {
			TCComponentRoleType localTCComponentRoleType = (TCComponentRoleType) getTCSession()
					.getTypeComponent("Role");
			TCComponentRole localTCComponentRole = localTCComponentRoleType
					.find(paramString1);
			TCComponentGroupType localTCComponentGroupType = (TCComponentGroupType) getTCSession()
					.getTypeComponent("Group");
			TCComponentGroup localTCComponentGroup = localTCComponentGroupType
					.find(paramString2);
			if ((localTCComponentRole != null)
					&& (localTCComponentGroup != null)) {
				TCComponentGroupMemberType localTCComponentGroupMemberType = (TCComponentGroupMemberType) getTCSession()
						.getTypeComponent("GroupMember");
				TCComponentUser localTCComponentUser = null;
				arrayOfTCComponentGroupMember = localTCComponentGroupMemberType
						.getGroupMembers(localTCComponentUser,
								localTCComponentGroup, localTCComponentRole);
			}
		} catch (TCException localTCException) {
			logger.error(localTCException.getClass().getName(),
					localTCException);
		}
		return arrayOfTCComponentGroupMember;
	}

	public String[] getPreferenceStringArray(String paramString) {
		try {
			String[] arrayOfString = this.prefService
					.getStringValues(paramString);
			if (arrayOfString == null)
				return new String[0];
			return arrayOfString;
		} catch (Exception localException) {
			logger.error("getPreferenceStringArray(String keyword="
					+ paramString + ")", localException);
		}
		return new String[0];
	}

	@Deprecated
	public void clearAll() {
		m_cacheUserListMap.clear();
	}

	public boolean isLoggedInUserPTA(
			ProjectTeamTreeManager paramProjectTeamTreeManager) {
		TCComponentUser localTCComponentUser = getTCSession().getUser();
		if ((paramProjectTeamTreeManager.getRoot() != null)
				&& (paramProjectTeamTreeManager.getRoot().getObject() != null)) {
			ProjectTeamStatus localProjectTeamStatus = null;
			if ((paramProjectTeamTreeManager.getRoot().getObject() instanceof ProjectTeamStatus))
				localProjectTeamStatus = (ProjectTeamStatus) paramProjectTeamTreeManager
						.getRoot().getObject();
			if ((localProjectTeamStatus != null)
					&& (localProjectTeamStatus
							.isAProjectTeamAdminMember(localTCComponentUser)))
				return true;
		}
		return false;
	}

	public TCComponent[] findProjectsByNameOrID(String paramString,
			boolean paramBoolean) {
		TCComponent[] arrayOfTCComponent = null;
		try {
			TCTextService localTCTextService = getTCSession().getTextService();
			String str1 = "k_find_project_name";
			TCComponentQueryType localTCComponentQueryType = (TCComponentQueryType) getTCSession()
					.getTypeComponent("ImanQuery");
			str1 = localTCTextService.getTextValue(str1);
			System.out.println("ImanQuery name:" + str1);

			if (str1 != null) {
				TCComponentQuery localTCComponentQuery = (TCComponentQuery) localTCComponentQueryType
						.find(str1);
				TCQueryClause[] arrayOfTCQueryClause = localTCComponentQuery
						.describe();
				String str2 = null;
				String[] arrayOfString1 = null;
				String[] arrayOfString2 = null;
				if (arrayOfTCQueryClause != null) {
					for (int i = 0; i < arrayOfTCQueryClause.length; i++) {
						if ((arrayOfTCQueryClause[i].getAttributeName()
								.equalsIgnoreCase("owning_user.user_id"))
								&& (paramBoolean)) {
							str2 = arrayOfTCQueryClause[i]
									.getUserEntryNameDisplay();

							// add by wuwei
							String userId = getTCSession().getUser()
									.getUserId();
							if (userId.equals("axiaoyz")
									|| userId.equals("infodba")) {
								arrayOfString2 = new String[] { userId };
							} else {
								arrayOfString2 = new String[] { userId };
							}

							break;
						}
						if ((arrayOfTCQueryClause[i].getAttributeName()
								.equalsIgnoreCase("project_name"))
								&& (!paramBoolean)) {
							str2 = arrayOfTCQueryClause[i]
									.getUserEntryNameDisplay();
							arrayOfString2 = new String[] { paramString };
							break;
						}
					}
					arrayOfString1 = new String[] { str2 };
				}
				arrayOfTCComponent = runQueryHelper(localTCComponentQuery,
						arrayOfString1, arrayOfString2);
			}
		} catch (Exception localException) {
			logger.error(localException.getClass().getName(), localException);
		}
		return arrayOfTCComponent;
	}

	public void doRefresh() {
		this.mustDoRefresh = true;
	}

	public void setTargets(AIFComponentContext[] paramArrayOfAIFComponentContext) {
		this.mtargetContexts = paramArrayOfAIFComponentContext;
	}

	public AIFComponentContext[] getTargets() {
		if (this.mtargetContexts != null)
			return this.mtargetContexts;
		return ProjectAMView.getTargetContexts();
	}

	public TCComponent[] runQueryHelper(TCComponentQuery paramTCComponentQuery,
			String[] paramArrayOfString1, String[] paramArrayOfString2) {
		TCComponent[] arrayOfTCComponent1 = null;
		if (paramTCComponentQuery == null)
			return arrayOfTCComponent1;
		TCSession localTCSession = getTCSession();
		String str = getSearchLanguage();
		int i = 1;
		if ((str != null) && (!str.isEmpty())
				&& (!localTCSession.getTcServerLocale().equalsIgnoreCase(str)))
			i = 0;
		try {
			if (i == 0)
				localTCSession.getPreferenceService().setStringValue(
						"TC_language_search",
						localTCSession.getTcServerLocale());
			TCComponentSearch localTCComponentSearch = new TCComponentSearch(
					paramTCComponentQuery, paramArrayOfString1,
					paramArrayOfString2);
			TCComponent[] arrayOfTCComponent2 = new TCComponent[0];
			TCComponentQuery localTCComponentQuery = localTCComponentSearch
					.getQuery();
			String[] arrayOfString1 = localTCComponentSearch.getCriteriaNames();
			String[] arrayOfString2 = localTCComponentSearch
					.getCriteriaInternalValues();
			int j = 0;
			QueryService localQueryService = new QueryService();
			AbstractAIFComponentContextList localAbstractAIFComponentContextList = localQueryService
					.executeQuery(localTCSession,
							localTCComponentQuery.getComponentManager(),
							localTCComponentQuery, arrayOfString1,
							arrayOfString2, arrayOfTCComponent2, 0, j);
			List localList = localAbstractAIFComponentContextList
					.toComponentContextVector();
			arrayOfTCComponent1 = new TCComponent[localList.size()];
			int k = 0;
			Iterator localIterator = localList.iterator();
			while (localIterator.hasNext()) {
				AIFComponentContext localAIFComponentContext = (AIFComponentContext) localIterator
						.next();
				arrayOfTCComponent1[k] = ((TCComponent) localAIFComponentContext
						.getComponent());
				k++;
			}
		} catch (TCException localTCException1) {
			localTCException1.printStackTrace();
			if (i == 0)
				try {
					localTCSession.getPreferenceService().setStringValue(
							"TC_language_search", str);
				} catch (TCException localTCException2) {
					localTCException2.printStackTrace();
				}
		} finally {
			if (i == 0)
				try {
					localTCSession.getPreferenceService().setStringValue(
							"TC_language_search", str);
				} catch (TCException localTCException3) {
					localTCException3.printStackTrace();
				}
		}
		return arrayOfTCComponent1;
	}

	private String getSearchLanguage() {
		TCSession localTCSession = getTCSession();
		String str = localTCSession.getPreferenceService().getStringValue(
				"TC_language_search");
		return str;
	}

	private boolean isProjectAdministratorAllowedToEdit() {
		TCSession localTCSession = getTCSession();
		boolean bool = localTCSession.getPreferenceService()
				.getLogicalValue("TC_allow_project_admins_modify_rules")
				.booleanValue();
		return bool;
	}

	public boolean getUserStatus(TCComponentUser paramTCComponentUser) {
		try {
			if (paramTCComponentUser != null) {
				TCProperty localTCProperty = paramTCComponentUser
						.getTCProperty("status");
				if (localTCProperty != null)
					return localTCProperty.getIntValue() == 0;
			}
		} catch (TCException localTCException) {
			localTCException.printStackTrace();
		}
		return false;
	}

	public void processComponentEvents(
			AIFComponentEvent[] paramArrayOfAIFComponentEvent) {
		for (AIFComponentEvent localAIFComponentEvent : paramArrayOfAIFComponentEvent) {
			InterfaceAIFComponent localInterfaceAIFComponent = localAIFComponentEvent
					.getComponent();
			if ((localInterfaceAIFComponent instanceof TCComponentProject)) {
				if (((localAIFComponentEvent instanceof AIFComponentPropertyChangeEvent))
						&& (((AIFComponentPropertyChangeEvent) localAIFComponentEvent)
								.isChangedProperty("owning_user")))
					m_cacheGroupMembersMap.clear();
				if (((localAIFComponentEvent instanceof AIFComponentPropertyChangeEvent))
						&& (((AIFComponentPropertyChangeEvent) localAIFComponentEvent)
								.isChangedProperty("fnd0Parent"))
						&& (!((TCComponentProject) localInterfaceAIFComponent)
								.isCut())) {
					projectManager.doRefresh();
					ProjectTreeView localProjectTreeView = ProjectTreeView
							.getProjectTreeViewInstance();
					localProjectTreeView.clearProjectsTreeSelection();
				}
			}
		}
	}
}