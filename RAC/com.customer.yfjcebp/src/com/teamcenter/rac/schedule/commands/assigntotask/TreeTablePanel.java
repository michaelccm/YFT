/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.teamcenter.rac.schedule.commands.assigntotask;


import com.teamcenter.rac.schedule.project.common.dataModel.ResourceAssignment;
import com.teamcenter.rac.schedule.project.common.l10n.ClassType;
import com.teamcenter.rac.schedule.project.common.l10n.DisplayNameCache;
//import com.teamcenter.rac.schedule.project.common.utils.OidBroker;
import com.teamcenter.rac.schedule.project.scheduling.*;
import com.teamcenter.rac.schedule.project.server.RACInterface.RACInterface;
import com.teamcenter.rac.schedule.project.server.RACInterface.RACInterfaceFactory;
import com.teamcenter.rac.kernel.*;
import com.teamcenter.rac.schedule.ScheduleViewApplicationPanel;
import com.teamcenter.rac.schedule.common.util.SchedulingExceptionHandler;
import com.teamcenter.rac.schedule.componentutils.TypeHelper;
import com.teamcenter.rac.schedule.project.scheduling.AssignmentModel;
import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

import java.util.*;

import org.apache.log4j.Logger;

// Referenced classes of package com.teamcenter.rac.schedule.commands.assigntotask:
//            AssignmentTreeLine, AssignmentTreeTableModel, AssignmentTreeTable, AssignToTaskDialog

public class TreeTablePanel {

	public TreeTablePanel(
			ScheduleViewApplicationPanel scheduleviewapplicationpanel,
			boolean flag, boolean flag1) {
		treeTable = null;
		treeTableModel = null;
		r = Registry.getRegistry(this);
		rootNode = null;
		memList = new Vector();
		modelFactory = null;
		unassignList = null;
		session = null;
		rIfc = null;
		selectedComp = null;
		selectedComponents = null;
		schedule = null;
		previousAssignmentList = new Vector(5);
		assignmentMap = new HashMap();
		previousresourcePercent = new HashMap();
		currentresourcePercent = new HashMap();
		newTaskAssignment = false;
		assignedResIds = null;
		privilegedUserNeeded = false;
		privilegedUser = null;
		notAllowAddorDelResForTriggredTsk = false;
		isWfTaskTriggred = false;
		msIntegLink = false;
		DisplayNameCache displaynamecache = RACInterface
				.getDisplayNameCache(scheduleviewapplicationpanel
						.getApplicationSession());
		String as[] = {
				r.getString("member/disciplinename"),
				r.getString("isassigned"),
				displaynamecache.getAttrDisplayName(ClassType.ASSIGNMENT,
						"resource_level"), r.getString("resourcegraph") };
		rootNode = new AssignmentTreeLine(r.getString("members"));
		schedulePanel = scheduleviewapplicationpanel;
		session = schedulePanel.getApplicationSession();
		schedule = schedulePanel.getCurrentProject();
		modelFactory = ModelFactory.singleton(session);
		newTaskAssignment = flag;
		if (!flag) {
			ArrayList arraylist = schedulePanel.getSelectedComponents();
			boolean flag2 = false;
			Iterator iterator = arraylist.iterator();
			do {
				if (!iterator.hasNext())
					break;
				TCComponent tccomponent = (TCComponent) iterator.next();
				selectedComp = tccomponent;
				privilegedUserNeeded = isWorflowTask();
			} while (privilegedUserNeeded == flag2);
			if (privilegedUserNeeded)
				as[1] = r.getString("assigned/privileged");
		}
		treeTableModel = new AssignmentTreeTableModel(schedulePanel, rootNode,
				as, this, flag1);
		memList.addAll(previousAssignmentList);
		treeTable = new AssignmentTreeTable(treeTableModel, schedulePanel,
				memList, flag);
		if (privilegedUserNeeded) {
			privilegedUser = initPrivilegedUser();
			treeTable.setPrivilegedUserNeeded(privilegedUserNeeded);
			treeTable.setPrivilegedUser(privilegedUser);
		}
	}

	public AssignmentTreeTable getTreeTable() {
		return treeTable;
	}

	public AssignmentTreeTableModel getTreeTableModel() {
		return treeTableModel;
	}

	public boolean saveAssignments(TCComponent tccomponent) {
		selectedComp = tccomponent;
		ArrayList arraylist = null;
		AssignmentModel assignmentmodel = modelFactory.getAssignmentModel();
		arraylist = new ArrayList();
		try {
			saveAssignments(assignmentmodel, arraylist);
		} catch (SchedulingException schedulingexception) {
			SchedulingExceptionHandler
					.handleScheduleException(schedulingexception);
			return false;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return false;
		}
		savePrivilegedUser(false);
		return true;
	}

	protected Vector saveAssignments(AssignmentModel assignmentmodel,
			ArrayList arraylist) throws SchedulingException {
		//System.out.println("saveAssignments(AssignmentModel assignmentmodel,ArrayList arraylist) start");
				
		Vector vector = treeTableModel.getPreviousAssignmentList();
		boolean flag = isWorflowTask();
		boolean flag1 = false;
		try {
			flag1 = TaskHelper.isMsIntegLink(selectedComp);
		} catch (TCException tcexception) {
			tcexception.printStackTrace();
		}
		if (flag)
			try {
				isWfTaskTriggred = TaskHelper.isTaskTriggered(selectedComp);
			} catch (TCException tcexception1) {
				tcexception1.printStackTrace();
			}
		if (flag1)
			try {
				msIntegLink = TaskHelper.isMsIntegLink(selectedComp);
			} catch (TCException tcexception2) {
				tcexception2.printStackTrace();
			}
		if (!newTaskAssignment && isWfTaskTriggred || !newTaskAssignment
				&& msIntegLink){
			if (vector.size() == memList.size()) {
				for (int i = 0; i < memList.size(); i++)
					if (!vector.contains(memList.get(i))) {
						notAllowAddorDelResForTriggredTsk = true;
						return unassignList;
					}

			} else {
				notAllowAddorDelResForTriggredTsk = true;
				return unassignList;
			}
		}
		if (vector.size() == 0) {
			addAssignments(arraylist);
		} else if (vector.size() > 0) {
			ArrayList arraylist1 = new ArrayList(memList.size());
			for (int j = memList.size() - 1; j >= 0; j--) {
				if (!vector.contains(memList.get(j)))
					continue;
				Number number = (Number) previousresourcePercent.get(memList
						.get(j));
				Number number1 = treeTableModel
						.getResourceLevel((String) memList.get(j));
				if (number != null
						&& number1 != null
						&& Math.abs(number.doubleValue()
								- number1.doubleValue()) < 1.0000000000000001E-005D) {
					vector.remove(memList.get(j));
					memList.remove(memList.get(j));
				} else {
					arraylist1.add(updateAssignments((String) memList.get(j),
							treeTableModel.getAssignmentId((String) vector
									.get(j))));
					vector.remove(memList.get(j));
					memList.remove(memList.get(j));
				}
			}

			if (!arraylist1.isEmpty())
				assignmentmodel.updateAssignments(arraylist1, schedulePanel
						.getCurrentProject().getUid());
			if (memList.size() > 0)
				addAssignments(arraylist);
			if (vector.size() > 0) {
				HashSet hashset = new HashSet();
				for (int k = 0; k < vector.size(); k++)
					hashset.add(treeTableModel.getAssignmentId((String) vector
							.get(k)));

				assignmentmodel.unassignResources(hashset, schedulePanel
						.getCurrentProject().getUid());
			}
		}
		
		if (!arraylist.isEmpty()){
			//System.out.println("schedulePanel.getCurrentProject().getUid()--->"+schedulePanel.getCurrentProject().getUid());
					
			assignmentmodel.assignResources(arraylist, schedulePanel
					.getCurrentProject().getUid());
		}
		return unassignList;
	}

	public boolean saveMultiTaskAssignments(Collection collection, boolean flag) {
		selectedComponents = collection;
		ArrayList arraylist = null;
		AssignmentModel assignmentmodel = modelFactory.getAssignmentModel();
		arraylist = new ArrayList();
		try {
			if (!flag) {
				if (memList.size() != 0)
					saveMultiTaskAssignments(assignmentmodel, arraylist);
			} else {
				overwriteAssignments(assignmentmodel, arraylist);
			}
		} catch (SchedulingException schedulingexception) {
			SchedulingExceptionHandler
					.handleScheduleException(schedulingexception);
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return false;
		}
		if (flag)
			savePrivilegedUser(true);
		return true;
	}

	protected Vector saveMultiTaskAssignments(AssignmentModel assignmentmodel,
			ArrayList arraylist) throws SchedulingException {
		Vector vector = new Vector();
		boolean flag = false;
		try {
			Iterator iterator = selectedComponents.iterator();
			do {
				if (!iterator.hasNext())
					break;
				TCComponent tccomponent = (TCComponent) iterator.next();
				Vector vector1 = getTaskPreviousAssignments(tccomponent);
				if (vector1.size() == 0) {
					int i = memList.size() - 1;
					while (i >= 0) {
						String s = (String) memList.get(i);
						String as[] = AssignmentTreeLine.parseKey(s);
						String s2 = as[0];
						String s3 = as[1];
						arraylist.add(new AssignmentCreateContainer(tccomponent
								.getUid(), s2, treeTableModel.getResourceLevel(
								s).doubleValue(), s3, null));
						i--;
					}
				} else if (vector1.size() > 0) {
					ArrayList arraylist1 = new ArrayList(memList.size());
					for (int j = memList.size() - 1; j >= 0; j--) {
						flag = true;
						String s1 = (String) memList.get(j);
						if (vector1.contains(s1)
								&& treeTableModel.getAssignmentId(s1) != null) {
							arraylist1.add(updateMultiTaskAssignments(s1,
									treeTableModel.getAssignmentId(s1)));
							vector1.remove(s1);
						} else {
							String as1[] = AssignmentTreeLine.parseKey(s1);
							String s4 = as1[0];
							String s5 = as1[1];
							arraylist.add(new AssignmentCreateContainer(
									tccomponent.getUid(), s4,
									treeTableModel.getResourceLevel(s1)
											.doubleValue(), s5, null));
						}
					}

					if (!arraylist1.isEmpty())
						assignmentmodel.updateAssignments(arraylist1,
								schedulePanel.getCurrentProject().getUid());
					if (vector1.size() > 0 && !flag) {
						HashSet hashset = new HashSet();
						for (int k = 0; k < vector1.size(); k++)
							hashset.add(treeTableModel
									.getAssignmentId((String) vector1.get(k)));

						assignmentmodel.unassignResources(hashset,
								schedulePanel.getCurrentProject().getUid());
					}
				}
			} while (true);
			if (!arraylist.isEmpty())
				assignmentmodel.assignResources(arraylist, schedulePanel
						.getCurrentProject().getUid());
		} catch (Exception exception) {
			if (exception instanceof SchedulingException)
				throw (SchedulingException) exception;
			logger.error("Exception", exception);
		}
		return unassignList;
	}

	protected Vector overwriteAssignments(AssignmentModel assignmentmodel,
			ArrayList arraylist) throws SchedulingException {
		Vector vector = new Vector();
		try {
			RACInterface racinterface = RACInterfaceFactory.singleton()
					.getRACInterface(schedulePanel.getApplicationSession(),
							schedulePanel.getCurrentProject());
			Iterator iterator = selectedComponents.iterator();
			HashSet hashset = new HashSet();
			while (iterator.hasNext()) {
				TCComponent tccomponent = (TCComponent) iterator.next();
				Vector vector1 = getTaskPreviousAssignments(tccomponent);
				for (int j = 0; j < vector1.size(); j++)
					if (!vector.contains(vector1.get(j)))
						vector.add(vector1.get(j));

				Collection collection = racinterface
						.getInternalAssignments(tccomponent);
				Iterator iterator1 = collection.iterator();
				while (iterator1.hasNext()) {
					TCComponent resourceassignment = (TCComponent) iterator1
							.next();
					hashset.add(resourceassignment.getUid());
				}
			}
			assignmentmodel.unassignResources(hashset, schedulePanel
					.getCurrentProject().getUid());
			if (vector.size() > 0) {
				for (int i = vector.size() - 1; i >= 0; i--) {
					String s = (String) vector.get(i);
					if (memList.contains(s) && !inNewAssignedList(s))
						memList.remove(s);
				}

			}
			addMultipleTaskAssignments(arraylist);
			if (!arraylist.isEmpty())
				assignmentmodel.assignResources(arraylist, schedulePanel
						.getCurrentProject().getUid());
		} catch (Exception exception) {
			if (exception instanceof SchedulingException)
				throw (SchedulingException) exception;
			logger.error("Exception", exception);
		}
		return unassignList;
	}

	private boolean inNewAssignedList(String s) {
		int i = treeTable.getRowCount();
		for (int j = 0; j < i; j++) {
			AssignmentTreeLine assignmenttreeline = (AssignmentTreeLine) treeTable
					.getRow(j);
			if (treeTable.isAssigned(assignmenttreeline)
					&& assignmenttreeline.getKey().equalsIgnoreCase(s))
				return true;
		}

		return false;
	}

	private boolean isWorflowTask() {
		boolean flag = false;
		Object obj = null;
		boolean flag1 = false;
		if (selectedComp instanceof TCComponentScheduleTask)
			try {
				TCComponent tccomponent = TaskHelper
						.getWorkflowTemplateName(selectedComp);
				int i = TaskHelper.getWorkflowTriggerType(selectedComp);
				if (tccomponent != null || i != 0)
					flag = true;
			} catch (TCException tcexception) {
			}
		return flag;
	}

	private void addAssignments(ArrayList arraylist) {
		//System.out.println("addAssignments start");
		for (int i = 0; i < memList.size(); i++) {
			String s = (String) memList.get(i);
			String as[] = AssignmentTreeLine.parseKey(s);
			String s1 = as[0];
			String s2 = as[1];
			//System.out.println("selectedComp.getUid()--->"+ selectedComp.getUid());
					
			//System.out.println("s------>" + s);
			//System.out.println("s1---->" + s1);
			//System.out.println("s2---->" + s2);
			//try {
				//System.out.println("object name--->"+selectedComp.getProperty("object_name"));
			//} catch (TCException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
			//System.out.println("treeTableModel.getResourceLevel(s).doubleValue()-->"+treeTableModel.getResourceLevel(s).doubleValue());
			arraylist.add(new AssignmentCreateContainer(selectedComp.getUid(),
					s1, treeTableModel.getResourceLevel(s).doubleValue(), s2,
					null));
		}

	}

	private void addMultipleTaskAssignments(ArrayList arraylist) {
		for (int i = 0; i < memList.size(); i++) {
			String s;
			String s1;
			String s2;
			for (Iterator iterator = selectedComponents.iterator(); iterator
					.hasNext(); arraylist
					.add(new AssignmentCreateContainer(((TCComponent) iterator
							.next()).getUid(), s1, treeTableModel
							.getResourceLevel(s).doubleValue(), s2, null))) {
				s = (String) memList.get(i);
				String as[] = AssignmentTreeLine.parseKey(s);
				s1 = as[0];
				s2 = as[1];
			}

		}

	}

	private UpdateContainer updateAssignments(String s, String s1) {
		UpdateContainer updatecontainer = new UpdateContainer();
		ArrayList arraylist = new ArrayList();
		updatecontainer.setUpdatedObjectID(s1);
		arraylist.add(new IntValuePair(3, treeTableModel.getResourceLevel(s)));
		updatecontainer.setDefinedObjectsIntValuePairs(arraylist);
		return updatecontainer;
	}

	private UpdateContainer updateMultiTaskAssignments(String s, String s1) {
		UpdateContainer updatecontainer = new UpdateContainer();
		ArrayList arraylist = new ArrayList();
		updatecontainer.setUpdatedObjectID(s1);
		arraylist.add(new IntValuePair(3, Double.valueOf(treeTableModel
				.getTotalResourceLevel(s,
						(Number) currentresourcePercent.get(s)))));
		updatecontainer.setDefinedObjectsIntValuePairs(arraylist);
		return updatecontainer;
	}

	public Vector getPreviousAssignments() throws TCException {
		try {
			ArrayList arraylist = schedulePanel.getSelectedComponents();
			if (AssignToTaskDialog.getContainCompleteTask()
					&& arraylist.size() > 1) {
				AssignToTaskDialog.removeCompletedTask(arraylist);
				selectedComp = (TCComponent) arraylist.get(0);
			} else {
				selectedComp = schedulePanel.getSelectedComponent();
			}
		} catch (RuntimeException runtimeexception) {
			MessageBox messagebox = new MessageBox(
					r.getString("assignResourceIntimation"),
					r.getString(" Info "), 1);
			messagebox.setVisible(true);
			return null;
		}
		if (selectedComp == null || !TypeHelper.isTask(selectedComp)
				|| isNewTaskAssignment())
			return null;
		rIfc = RACInterfaceFactory.singleton().getRACInterface(
				schedulePanel.getApplicationSession(),
				TaskHelper.getMasterSchedule());
		assignedResIds = rIfc.getInternalAssignments(selectedComp);
		if (assignedResIds != null) {
			Iterator iterator = assignedResIds.iterator();
			do {
				if (!iterator.hasNext())
					break;
				TCComponent resourceassignment = (TCComponent) iterator
						.next();
				if (resourceassignment != null) {
					
					String s = AssignmentTreeLine.getKey(resourceassignment);
					Double double1 = Double.valueOf(resourceassignment.getDoubleProperty("resource_level"));
							
					previousAssignmentList.add(s);
					previousresourcePercent.put(s, double1);
					assignmentMap.put(s, resourceassignment);
				}
			} while (true);
		}
		return previousAssignmentList;
	}

	public Vector getPreviousAssignmentList() {
		return previousAssignmentList;
	}

	public HashMap getAssignmentMap() {
		return assignmentMap;
	}

	public HashMap getResourcePercent() {
		return previousresourcePercent;
	}

	public boolean isNewTaskAssignment() {
		return newTaskAssignment;
	}

	public Vector getTaskPreviousAssignments(TCComponent tccomponent)
			throws TCException {
		rIfc = RACInterfaceFactory.singleton().getRACInterface(
				schedulePanel.getApplicationSession(),
				schedulePanel.getCurrentProject());
		assignedResIds = rIfc.getInternalAssignments(tccomponent);
		if (assignedResIds != null) {
			Iterator iterator = assignedResIds.iterator();
			do {
				if (!iterator.hasNext())
					break;
				TCComponent resourceassignment = (TCComponent) iterator
						.next();
				if (resourceassignment != null) {
					String s = AssignmentTreeLine.getKey(resourceassignment);
					previousAssignmentList.add(s);
					Double double1 = Double.valueOf(resourceassignment.getDoubleProperty("resource_level"));
						
					currentresourcePercent.put(s, double1);
					assignmentMap.put(s, resourceassignment);
				}
			} while (true);
		}
		return previousAssignmentList;
	}

	public void setPrivilegedUserNeeded(boolean flag) {
		treeTable.setPrivilegedUserNeeded(flag && privilegedUserNeeded);
	}

	public Vector getMemberList() {
		return memList;
	}

	private boolean savePrivilegedUser(boolean flag) {
		if (isWfTaskTriggred && privilegedUser != treeTable.getPrivilegedUser()
				|| notAllowAddorDelResForTriggredTsk) {
			MessageBox messagebox = new MessageBox(
					r.getString("notAllowAddorDelResForTriggredTsk.MSG"),
					r.getString("notAllowAddorDelResForTriggredTsk.TITLE"), 2);
			messagebox.setModal(true);
			messagebox.setVisible(true);
		} else {
			privilegedUser = treeTable.getPrivilegedUser();
		}
		if (privilegedUser == null)
			return false;
		TaskModel taskmodel;
		ArrayList arraylist;
		taskmodel = modelFactory.getTaskModel();
		arraylist = new ArrayList();
		if (flag) {
			Iterator iterator = selectedComponents.iterator();
			do {
				if (!iterator.hasNext())
					break;
				Object obj = iterator.next();
				selectedComp = (TCComponent) obj;
				if (isWorflowTask())
					addPrivilegedUserToContainList(arraylist);
			} while (true);
		} else if (isWorflowTask() && !msIntegLink)
			addPrivilegedUserToContainList(arraylist);
		else
			return false;
		try {
			if (arraylist.size() > 0) {
				taskmodel.updateTasks(arraylist, null, null, schedule.getUid());
				return true;
			}
		} catch (SchedulingException schedulingexception) {
			SchedulingExceptionHandler.handleScheduleException(
					schedulingexception, null, 2);
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return false;
	}

	private void addPrivilegedUserToContainList(Collection collection) {
		if (privilegedUser == null) {
			return;
		} else {
			UpdateContainer updatecontainer = new UpdateContainer();
			updatecontainer.setUpdatedObjectID(selectedComp.getUid());
			ArrayList arraylist = new ArrayList();
			arraylist
					.add(new StringValuePair("privileged_user", privilegedUser));
			updatecontainer.setExtendObjectKeyValuePairs(arraylist);
			collection.add(updatecontainer);
			return;
		}
	}

	private String initPrivilegedUser() {
		try {
			TCComponent tccomponent;
			tccomponent = selectedComp.getReferenceProperty("privileged_user");
			if (tccomponent instanceof TCComponentResourceAssignment) {
				TCComponent tccomponent1 = tccomponent
						.getReferenceProperty("secondary_object");
				if (tccomponent1 instanceof TCComponentUser)
					return tccomponent1.getUid();
			}

			if (tccomponent instanceof TCComponentUser)
				return tccomponent.getUid();
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return null;
	}

	public void expandSelected() {
		treeTable.expandSelected();
	}

	private AssignmentTreeTable treeTable;
	private AssignmentTreeTableModel treeTableModel;
	private Registry r;
	private AssignmentTreeLine rootNode;
	private Vector memList;
	private ModelFactory modelFactory;
	private Vector unassignList;
	private static final Logger logger = Logger
			.getLogger(com.teamcenter.rac.schedule.commands.assigntotask.TreeTablePanel.class);
	private ScheduleViewApplicationPanel schedulePanel;
	private TCSession session;
	private RACInterface rIfc;
	private TCComponent selectedComp;
	private Collection selectedComponents;
	private TCComponent schedule;
	private Vector previousAssignmentList;
	private HashMap assignmentMap;
	private HashMap previousresourcePercent;
	private HashMap currentresourcePercent;
	private boolean newTaskAssignment;
	private Collection assignedResIds;
	private boolean privilegedUserNeeded;
	private String privilegedUser;
	private final String PRIVILEGED_USER_PROP = "privileged_user";
	private boolean notAllowAddorDelResForTriggredTsk;
	private boolean isWfTaskTriggred;
	private boolean msIntegLink;

}

/*
 * DECOMPILATION REPORT
 * 
 * Decompiled from:
 * C:\Siemens\Teamcenter\OTW9\rac\plugins\com.teamcenter.rac.schedule_9000
 * .1.1.jar Total time: 43 ms Jad reported messages/errors: Couldn't resolve all
 * exception handlers in method savePrivilegedUser Couldn't resolve all
 * exception handlers in method initPrivilegedUser Exit status: 0 Caught
 * exceptions:
 */