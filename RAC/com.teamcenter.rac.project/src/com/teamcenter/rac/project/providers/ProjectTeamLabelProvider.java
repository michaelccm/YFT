/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.teamcenter.rac.project.providers;

import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.project.Messages;
import com.teamcenter.rac.project.common.IC_GroupRoleHashable;
import com.teamcenter.rac.project.common.IC_UserProxy;
import com.teamcenter.rac.project.common.ProjectConstants;
import com.teamcenter.rac.project.nodes.ProjectTeamContentNode;
import com.teamcenter.rac.project.nodes.ProjectTeamGroupNode;
import com.teamcenter.rac.project.nodes.ProjectTeamRoleNode;
import com.teamcenter.rac.project.nodes.ProjectTeamUserNode;
import com.teamcenter.rac.util.ImageUtilities;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.viewer.provider.node.ExpandingNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class ProjectTeamLabelProvider implements ITableLabelProvider,
		ILabelDecorator {
	private static Image groupIcon;
	private static Image roleIcon = null;
	private static Image puIcon;
	private static Image npuIcon;
	private static Image ptaIcon;
	private static Image paIcon = null;
	private static Image inactivePuIcon;
	private static Image inactiveNpuIcon;
	private static Image inactivePtaIcon;
	private static Image inactivePaIcon = null;
	private static Image gmInactivePuIcon;
	private static Image gmInactiveNpuIcon;
	private static Image gmInactivePtaIcon;
	private static Image gmInactivePaIcon;
	private static String[] projectTeamStatusText = null;
	private static Image overlayImageForInactiveUser = null;
	private static Image overlayImageForInactiveGroupMember = null;

	static {
		Registry localRegistry1 = Registry
				.getRegistry("com.teamcenter.rac.common.organization.organization");
		groupIcon = ImageUtilities.getImageDescriptor(
				localRegistry1.getImageIcon("group.ICON")).createImage();
		roleIcon = ImageUtilities.getImageDescriptor(
				localRegistry1.getImageIcon("role.ICON")).createImage();
		overlayImageForInactiveUser = ImageUtilities.getImageDescriptor(
				localRegistry1.getImageIcon("showAllGMButton.ICON"))
				.createImage();
		overlayImageForInactiveGroupMember = ImageUtilities.resizeImage(
				ImageUtilities.getImageDescriptor(
						localRegistry1.getImageIcon("showAllGMButton.ICON"))
						.createImage(), 10, 10);
		Registry localRegistry2 = Registry
				.getRegistry("com.teamcenter.rac.project.project");
		puIcon = ImageUtilities.getImageDescriptor(
				localRegistry2.getImageIcon("PU.ICON")).createImage();
		npuIcon = ImageUtilities.getImageDescriptor(
				localRegistry2.getImageIcon("NPU.ICON")).createImage();
		ptaIcon = ImageUtilities.getImageDescriptor(
				localRegistry2.getImageIcon("PTA.ICON")).createImage();
		paIcon = ImageUtilities.getImageDescriptor(
				localRegistry2.getImageIcon("PA.ICON")).createImage();
		String[] arrayOfString = new String[4];
		arrayOfString[0] = localRegistry2.getString(
				"ProjectTeam.NonPrivileged", "Non-privileged");
		arrayOfString[1] = localRegistry2.getString("ProjectTeam.Privileged",
				"Privileged");
		arrayOfString[2] = localRegistry2.getString(
				"ProjectTeam.TeamAdministrator", "Team Administrator");
		arrayOfString[3] = "Project Administrator";
		projectTeamStatusText = arrayOfString;
	}

	public Image getColumnImage(Object paramObject, int paramInt) {
		if (paramInt == 0) {
			if (paramObject instanceof ExpandingNode)
				return ((ExpandingNode) paramObject).getDisplayImage();
			if (paramObject instanceof ProjectTeamUserNode) {
				ProjectTeamUserNode localProjectTeamUserNode = (ProjectTeamUserNode) paramObject;
				if (localProjectTeamUserNode.getPrivilege() == ProjectConstants.PRIVILEGED)
					return decorateImage(puIcon, paramObject);
				if (localProjectTeamUserNode.getPrivilege() == ProjectConstants.TEAMADMIN)
					return decorateImage(ptaIcon, paramObject);
				if (localProjectTeamUserNode.getPrivilege() == ProjectConstants.PROJECT_ADMINISTRATOR)
					return decorateImage(paIcon, paramObject);
				return decorateImage(npuIcon, paramObject);
			}
			if (paramObject instanceof ProjectTeamRoleNode)
				return roleIcon;
			if (paramObject instanceof ProjectTeamGroupNode)
				return groupIcon;
		}
		return null;
	}

	public String getColumnText(Object paramObject, int paramInt) {
		if (paramObject instanceof ExpandingNode)
			return ((ExpandingNode) paramObject).toString();
		if (paramObject instanceof ProjectTeamContentNode) {
			ProjectTeamContentNode localProjectTeamContentNode = (ProjectTeamContentNode) paramObject;
			if (localProjectTeamContentNode != null) {
				Object localObject;
				if ((paramInt == 0) && (localProjectTeamContentNode != null)) {
					if (localProjectTeamContentNode.getObject() instanceof IC_GroupRoleHashable) {
						localObject = (IC_GroupRoleHashable) localProjectTeamContentNode
								.getObject();
						return ((IC_GroupRoleHashable) localObject).getGroup()
								.toDisplayString()
								+ "."
								+ ((IC_GroupRoleHashable) localObject)
										.getRole().toDisplayString();
					}
					return localProjectTeamContentNode.getObject().toString();
				}
				if ((paramInt == 1)
						&& (((localProjectTeamContentNode.getObject() instanceof IC_UserProxy) || (localProjectTeamContentNode
								.getObject() instanceof TCComponentUser)))) {
					localObject = "";
					if (localProjectTeamContentNode.getObject() instanceof IC_UserProxy) {
						IC_UserProxy localIC_UserProxy = (IC_UserProxy) localProjectTeamContentNode
								.getObject();
						if (!(localIC_UserProxy.isUserActive()))
							localObject = " ("
									+ Messages
											.getString("inactiveStatusButton.TITLE")
									+ ")";
					}
					
					return projectTeamStatusText[((ProjectTeamUserNode) localProjectTeamContentNode)
							.getPrivilege()] + ((String) localObject);
					
					//return "";
					
				}
			}
		}
		return ((String) null);
	}

	public void addListener(ILabelProviderListener paramILabelProviderListener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object paramObject, String paramString) {
		return false;
	}

	public void removeListener(
			ILabelProviderListener paramILabelProviderListener) {
	}

	public Image decorateImage(Image paramImage, Object paramObject) {
		ProjectTeamContentNode localProjectTeamContentNode = (ProjectTeamContentNode) paramObject;
		if ((((localProjectTeamContentNode.getObject() instanceof IC_UserProxy) || (localProjectTeamContentNode
				.getObject() instanceof TCComponentUser)))
				&& (localProjectTeamContentNode.getObject() instanceof IC_UserProxy)) {
			IC_UserProxy localIC_UserProxy = (IC_UserProxy) localProjectTeamContentNode
					.getObject();
			if (!(localIC_UserProxy.isUserActive()))
				paramImage = getDecoratedInacitveIcon(localProjectTeamContentNode);
			else if (!(localIC_UserProxy.isGroupMemberActive()))
				paramImage = getDecoratedGmInactiveIcon(localProjectTeamContentNode);
		}
		return paramImage;
	}

	private Image getDecoratedInacitveIcon(
			ProjectTeamContentNode paramProjectTeamContentNode) {
		if (paramProjectTeamContentNode instanceof ProjectTeamUserNode) {
			ProjectTeamUserNode localProjectTeamUserNode = (ProjectTeamUserNode) paramProjectTeamContentNode;
			if (localProjectTeamUserNode.getPrivilege() == ProjectConstants.PRIVILEGED) {
				if (inactivePuIcon == null)
					inactivePuIcon = new CustomOverlayImageHelper(puIcon,
							overlayImageForInactiveUser).createImage();
				return inactivePuIcon;
			}
			if (localProjectTeamUserNode.getPrivilege() == ProjectConstants.TEAMADMIN) {
				if (inactivePtaIcon == null)
					inactivePtaIcon = new CustomOverlayImageHelper(ptaIcon,
							overlayImageForInactiveUser).createImage();
				return inactivePtaIcon;
			}
			if (localProjectTeamUserNode.getPrivilege() == ProjectConstants.PROJECT_ADMINISTRATOR) {
				if (inactivePaIcon == null)
					inactivePaIcon = new CustomOverlayImageHelper(paIcon,
							overlayImageForInactiveUser).createImage();
				return inactivePaIcon;
			}
			if (inactiveNpuIcon == null)
				inactiveNpuIcon = new CustomOverlayImageHelper(npuIcon,
						overlayImageForInactiveUser).createImage();
			return inactiveNpuIcon;
		}
		return overlayImageForInactiveUser;
	}

	private Image getDecoratedGmInactiveIcon(
			ProjectTeamContentNode paramProjectTeamContentNode) {
		if (paramProjectTeamContentNode instanceof ProjectTeamUserNode) {
			ProjectTeamUserNode localProjectTeamUserNode = (ProjectTeamUserNode) paramProjectTeamContentNode;
			if (localProjectTeamUserNode.getPrivilege() == ProjectConstants.PRIVILEGED) {
				if (gmInactivePuIcon == null)
					gmInactivePuIcon = new CustomOverlayImageHelper(puIcon,
							overlayImageForInactiveGroupMember, 0)
							.createImage();
				return gmInactivePuIcon;
			}
			if (localProjectTeamUserNode.getPrivilege() == ProjectConstants.TEAMADMIN) {
				if (gmInactivePtaIcon == null)
					gmInactivePtaIcon = new CustomOverlayImageHelper(ptaIcon,
							overlayImageForInactiveGroupMember, 0)
							.createImage();
				return gmInactivePtaIcon;
			}
			if (localProjectTeamUserNode.getPrivilege() == ProjectConstants.PROJECT_ADMINISTRATOR) {
				if (gmInactivePaIcon == null)
					gmInactivePaIcon = new CustomOverlayImageHelper(paIcon,
							overlayImageForInactiveGroupMember, 0)
							.createImage();
				return gmInactivePaIcon;
			}
			if (gmInactiveNpuIcon == null)
				gmInactiveNpuIcon = new CustomOverlayImageHelper(npuIcon,
						overlayImageForInactiveGroupMember, 0).createImage();
			return gmInactiveNpuIcon;
		}
		return overlayImageForInactiveUser;
	}

	public String decorateText(String paramString, Object paramObject) {
		return null;
	}
}