/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.teamcenter.rac.project.providers;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.ExpansionRule;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.project.Messages;
import com.teamcenter.rac.project.common.ProjectManager;
import com.teamcenter.rac.project.nodes.ProjectRootNode;
import com.teamcenter.rac.project.plugin.Activator;
import com.teamcenter.rac.providers.TCComponentContentProvider;
import com.teamcenter.rac.providers.node.LoadingObject;
import com.teamcenter.rac.providers.node.MultipleObjectsRootNode;
import com.teamcenter.rac.services.ISessionService;
import com.teamcenter.rac.util.AdapterUtil;
import com.teamcenter.rac.util.OSGIUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
//import java.util.List;//<Lcom.teamcenter.rac.aif.kernel.AIFComponentContext>
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

public class ProjectContentProvider extends TCComponentContentProvider {
	private static final Logger logger = Logger
			.getLogger(ProjectContentProvider.class);
	private boolean areProjectsLoaded = false;
	private Viewer m_viewer;

	public Object[] getElements(Object paramObject) {
		return getChildren(paramObject);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer paramViewer, Object paramObject1,
			Object paramObject2) {
		this.m_viewer = paramViewer;
	}

	public Object[] getChildren(Object paramObject) {
		if ((paramObject instanceof TCComponent)
				|| (paramObject instanceof AIFComponentContext)) {
			super.setSyncLoadSwitch(true);

			//System.out.println("getChildren-->" + paramObject);

			Object[] localObject1 = null;
			TCComponent localObject2 = (TCComponent) AdapterUtil.getAdapter(
					paramObject, TCComponent.class);
			if ((localObject2 == null)
					|| (areNoChildrenAllowed((TCComponent) localObject2)))
				localObject1 = new Object[0];
			else
				localObject1 = getChildren((TCComponent) localObject2,
						paramObject);
			if ((localObject1 != null) && (localObject1.length > 0))
				return localObject1;
		}
		Object localObject1 = (MultipleObjectsRootNode) AdapterUtil.getAdapter(
				paramObject, MultipleObjectsRootNode.class, true);
		if (localObject1 != null)
			return ((MultipleObjectsRootNode) localObject1).getElements();
		ProjectRootNode localObject2 = (ProjectRootNode) AdapterUtil.getAdapter(
				paramObject, ProjectRootNode.class, true);
		if (localObject2 != null) {
			
			if (ProjectManager.getInstance().areProjectsLoaded()) {
				System.out.println("----ww getLoadedProjects()----");
				
				TCComponentProject[] loadedProjects = ProjectManager
						.getInstance().getLoadedProjects();
//
//				Comparator<TCComponentProject> myComparator = new Comparator<TCComponentProject>() {
//					public int compare(TCComponentProject p1,
//							TCComponentProject p2) {
//						String h1 = p1.getProjectID();
//						String h2 = p2.getProjectID();
//
//						String[] s1 = h1.split("-", -1);
//						String[] s2 = h2.split("-", -1);
//						int bool = s1[0].compareTo(s2[0]);
//						if (bool > 0)
//							return 1;
//						return -1;
//					}
//
//				};
//
//				Arrays.sort(loadedProjects, myComparator);
				//System.out.println("----Arrays.sort(loadedProjects, myComparator)----");
						
				return loadedProjects;
			}

			//System.out.println("----loadChildrenInJob----");
			loadChildrenInJob((ProjectRootNode) localObject2);
			if (!(this.areProjectsLoaded)) {
				//System.out.println("----return new Object[] { new LoadingObject() }----");
						
				return new Object[] { new LoadingObject() };
			}
		}
		return ((Object[]) (Object) new Object[0]);
	}

	public Object getParent(Object paramObject) {
		return null;
	}

	private void loadChildrenInJob(ProjectRootNode paramProjectRootNode) {
		final ProjectRootNode val$rootNode = paramProjectRootNode;
		Object[] arrayOfObject = { TCComponentProject
				.getDisplayName(ProjectManager.getInstance().getTCSession()) };
		final String str = Messages.getFormattedString(
				"loadingProjects.MESSAGE", arrayOfObject);
		Job local1 = new Job(str) // paramProjectRootNode
		{
			protected IStatus run(IProgressMonitor paramIProgressMonitor) {
				paramIProgressMonitor.beginTask(str, -1);
				try {
					List localList = ProjectManager.getInstance().getProjects();
					if ((localList != null) && (!(localList.isEmpty()))) {
						val$rootNode.addChildren(localList);
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								ProjectContentProvider.this.m_viewer.refresh();
							}
						});
					} else {
						val$rootNode.addChildren(new ArrayList(0));
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								ProjectContentProvider.this.m_viewer.refresh();
							}
						});
					}
					ProjectContentProvider.this.areProjectsLoaded = true;
				} catch (Exception localException) {
					ProjectContentProvider.logger.error(
							localException.getClass(), localException);
				}
				return Status.OK_STATUS;
			}
		};
		local1.setPriority(10);
		ISessionService localISessionService = (ISessionService) OSGIUtil
				.getService(Activator.getDefault(), ISessionService.class);
		TCSession localTCSession = (TCSession) localISessionService
				.getActivePerspectiveSession();
		local1.setRule(localTCSession.getOperationJobRule());
		local1.schedule();
	}

	public boolean hasChildren(Object paramObject) {
		if (paramObject instanceof ProjectRootNode)
			return true;
		super.setSyncLoadSwitch(true);
		return super.hasChildren(paramObject);
	}

	protected ExpansionRule getExpansionRule() {
		ProjectDataExpansionRule localProjectDataExpansionRule = new ProjectDataExpansionRule();
		if ((super.getExpansionRuleMap() != null)
				&& (!(super.getExpansionRuleMap().isEmpty()))) {
			TCTypeService localTCTypeService = (TCTypeService) OSGIUtil
					.getService(Activator.getDefault(), TCTypeService.class);
			Iterator localIterator1 = super.getExpansionRuleMap().entrySet()
					.iterator();
			while (localIterator1.hasNext()) {
				Map.Entry localEntry = (Map.Entry) localIterator1.next();
				String str1 = (String) localEntry.getKey();
				List localList = (List) localEntry.getValue();
				if ((localList == null) || (localList.isEmpty()))
					continue;
				Iterator localIterator2 = localList.iterator();

				while (localIterator2.hasNext()) {
					String vv1 = (String) localIterator2.next();
					localProjectDataExpansionRule.addRule(str1, vv1);
				}
				try {
					String[] localObject1 = localTCTypeService
							.getChildTypeNames(str1, "subtypes");

					if ((localObject1 != null) && (localObject1.length > 0))
						for (String str22 : localObject1) {
							Iterator localIterator3 = localList.iterator();
							while (localIterator3.hasNext()) {
								String str2 = (String) localIterator3.next();
								localProjectDataExpansionRule.addRule(str22,
										str2);
							}
						}
				} catch (TCException localTCException) {
					logger.error(localTCException.getLocalizedMessage(),
							localTCException);
				}
			}
		}
		return ((ExpansionRule) localProjectDataExpansionRule);
	}

	private class ProjectDataExpansionRule extends ExpansionRule {
		public AIFComponentContext[] getFilteredChildren(
				AIFComponentContext[] paramArrayOfAIFComponentContext) {
			AIFComponentContext[] arrayOfAIFComponentContext = super
					.getFilteredChildren(paramArrayOfAIFComponentContext);
			List localList = removeDuplicationForCheckedOutObjects(new ArrayList(
					Arrays.asList(arrayOfAIFComponentContext)));
			return ((AIFComponentContext[]) localList
					.toArray(new AIFComponentContext[localList.size()]));
		}

		private List<AIFComponentContext> removeDuplicationForCheckedOutObjects(
				List<AIFComponentContext> paramList) {
			ArrayList localArrayList1 = new ArrayList();
			ArrayList localArrayList2 = new ArrayList();
			Object localObject1;
			Object localObject2;
			for (int i = 0; i < paramList.size(); ++i) {
				localObject1 = ((AIFComponentContext) paramList.get(i))
						.getComponent();
				localObject2 = ((InterfaceAIFComponent) localObject1).getType();
				if ((!(((String) localObject2).equalsIgnoreCase("Folder")))
						&& (!(((String) localObject2)
								.equalsIgnoreCase("EmailMaster")))
						&& (!(((String) localObject2)
								.equalsIgnoreCase("Web Link"))))
					continue;
				int j = 0;
				Iterator localIterator = localArrayList2.iterator();
				while (localIterator.hasNext()) {
					InterfaceAIFComponent localInterfaceAIFComponent2 = (InterfaceAIFComponent) localIterator
							.next();
					if ((!(localObject1.toString()
							.equals(localInterfaceAIFComponent2.toString())))
							|| (!(((String) localObject2)
									.equals(localInterfaceAIFComponent2
											.getType()))))
						continue;
					j = 1;
				}
				if (j != 0)
					localArrayList1.add((AIFComponentContext) paramList.get(i));
				else
					localArrayList2.add(localObject1);
			}
			for (int i = paramList.size() - 1; i >= 0; --i) {
				localObject2 = localArrayList1.iterator();
				while (((Iterator) localObject2).hasNext()) {
					localObject1 = (AIFComponentContext) ((Iterator) localObject2)
							.next();
					InterfaceAIFComponent localInterfaceAIFComponent1 = ((AIFComponentContext) paramList
							.get(i)).getComponent();
					if (!(localInterfaceAIFComponent1.getUid()
							.equals(((AIFComponentContext) localObject1)
									.getComponent().getUid())))
						continue;
					paramList.remove(i);
				}
			}
			return ((List<AIFComponentContext>) (List<AIFComponentContext>) paramList);
		}
	}
}