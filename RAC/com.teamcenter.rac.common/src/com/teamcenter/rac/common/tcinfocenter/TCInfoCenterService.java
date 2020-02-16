/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.teamcenter.rac.common.tcinfocenter;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.kernel.AIFComponentAddChildEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentChangeEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentChildCountChangeEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentDeleteEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentRemoveChildEvent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.SelectionHelper;
import com.teamcenter.rac.kernel.ITCPreferenceService;
import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentManager;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PlatformHelper;
import com.teamcenter.rac.util.SWTUIUtilities;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.event.ClientEventDispatcher;
import com.teamcenter.rac.util.pjl.PJLToIgnore;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.internal.menus.TrimContributionManager;
import org.eclipse.ui.progress.UIJob;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class TCInfoCenterService implements EventHandler {
	private static final String[] ACCESS = { "WRITE", "DELETE", "CHANGE" };
	private static final String[] PROPERTIES = { "process_stage",
			"release_status_list", "publication_sites", "ics_classified",
			"checked_out" };
	private final InfoStatus m_emptyInfoStatus = new InfoStatus();
	private final Set<Reference<AbstractTCInfoCenterToolbarContribution>> m_lsnrs = new HashSet();
	private String m_currentComponentUID;
	private String m_currentUnderlyingComponentUID;
	private List<InterfaceAIFComponent> m_multipleComponents;
	private UpdateOperation m_updateOp;
	private final HashMap<String, InfoStatus> m_infoCache = new HashMap();
	private String m_partName;
	private IContextActivation m_contextWhereRef;
	private IContextActivation m_contextWhereUsed;
	private IContextActivation m_contextChildCount;
	private IContextActivation m_contextAccess;
	private IContextActivation m_contextState;
	private ITCPreferenceService m_prefSvc;
	private TCAccessControlService m_aclSvc;

	public void activate() {
		ClientEventDispatcher.addEventHandler(this, new String[] {
				"com/teamcenter/rac/aif/kernel/AIFComponentEvent",
				"com/teamcenter/rac/kernel/TCPreferenceService/*",
				"com/teamcenter/rac/aifrcp/event/Session/Changed" });
	}

	public void setPreferenceService(
			ITCPreferenceService paramITCPreferenceService) {
		this.m_prefSvc = paramITCPreferenceService;
	}

	public void setAccessControlService(
			TCAccessControlService paramTCAccessControlService) {
		this.m_aclSvc = paramTCAccessControlService;
	}

	public void addListener(
			AbstractTCInfoCenterToolbarContribution paramAbstractTCInfoCenterToolbarContribution) {
		this.m_lsnrs.add(new WeakReference(
				paramAbstractTCInfoCenterToolbarContribution));
	}

	public void handleEvent(Event paramEvent) {
		String str = paramEvent.getTopic();

		if (str.equals("com/teamcenter/rac/aif/kernel/AIFComponentEvent")) {
			Object localObject1 = (List) paramEvent
					.getProperty("com/teamcenter/rac/aif/kernel/AIFComponentEvents");
			processComponentEvents((List) localObject1);
		} else {
			// Object localObject2;
			TCComponent localTCComponent;
			if (str.equals("org/eclipse/ui/Selection/Changed")) {
				if (this.m_partName == null)
					return;
				ISelection localObject1 = (ISelection) paramEvent
						.getProperty(ISelection.class.getName());
				InterfaceAIFComponent[] localObject2 = SelectionHelper
						.getTargetComponents((ISelection) localObject1);
				localTCComponent = null;
				if ((localObject2 == null) || (localObject2.length == 0)) {
					setComponent(null);
				} else {
					if ((localObject2[0] instanceof TCComponent)
							&& (!(localObject2[0] instanceof TCComponentType))) {
						this.m_multipleComponents = Arrays.asList(localObject2);
						if (localObject2.length == 1) {
							localTCComponent = (TCComponent) localObject2[0];
							if (localTCComponent != null)
								this.m_currentComponentUID = localTCComponent
										.getUid();
						} else {
							this.m_currentComponentUID = null;
						}
					}
					setContexts(null, localTCComponent);
				}
			} else if (str
					.startsWith("com/teamcenter/rac/kernel/TCPreferenceService")) {
				String localObject1 = (String) paramEvent
						.getProperty("prefName");
				if ((this.m_partName == null)
						|| (!(((String) localObject1)
								.startsWith(this.m_partName))))
					return;
				setContexts((String) localObject1, null);
			} else if (str.equals("org/eclipse/ui/Part/Activated")) {
				IWorkbenchPartReference localObject1 = (IWorkbenchPartReference) paramEvent
						.getProperty(IWorkbenchPartReference.class.getName());
				IWorkbenchPart localObject2 = ((IWorkbenchPartReference) localObject1)
						.getPart(false);
				this.m_partName = localObject2.getClass().getName();
				localTCComponent = null;
				ISelectionProvider localISelectionProvider = ((IWorkbenchPart) localObject2)
						.getSite().getSelectionProvider();
				if (localISelectionProvider != null) {
					ISelection localISelection = localISelectionProvider
							.getSelection();
					InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = SelectionHelper
							.getTargetComponents(localISelection);
					if ((arrayOfInterfaceAIFComponent != null)
							&& (arrayOfInterfaceAIFComponent.length == 1)
							&& (arrayOfInterfaceAIFComponent[0] instanceof TCComponent))
						localTCComponent = (TCComponent) arrayOfInterfaceAIFComponent[0];
				}
				setContexts(null, localTCComponent);
			} else if (str
					.equals("com/teamcenter/rac/aifrcp/event/Session/Changed")) {
				Object localObject1 = (TCSession) paramEvent
						.getProperty(TCSession.class.getName());
				this.m_infoCache.clear();
				reassertComponent((TCSession) localObject1);
			} else {
				throw new IllegalArgumentException("Unexcepted event: "
						+ paramEvent);
			}
		}
	}

	private void addContextMenu() {
		try {
			Iterator localIterator = getToolbars().iterator();
			while (localIterator.hasNext()) {
				ToolBar localToolBar = (ToolBar) localIterator.next();
				createContextMenu(localToolBar);
				for (Control localControl : localToolBar.getChildren())
					createContextMenu(localControl);
			}
		} catch (Throwable localThrowable) {
			Logger.getLogger(TCInfoCenterService.class).error(
					localThrowable.getLocalizedMessage(), localThrowable);
		}
	}

	private List<ToolBar> getToolbars() {
		ArrayList localArrayList = new ArrayList();
		IWorkbenchWindow localIWorkbenchWindow = PlatformHelper
				.getCurrentWorkbenchWindow();
		if (localIWorkbenchWindow != null) {
			TrimContributionManager localTrimContributionManager = (TrimContributionManager) Utilities
					.getPrivateField(localIWorkbenchWindow,
							"trimContributionMgr");
			List localList = (List) Utilities.getPrivateField(
					localTrimContributionManager, "contributedTrim");
			Iterator localIterator = localList.iterator();
			while (localIterator.hasNext()) {
				Object localObject = localIterator.next();
				String str = (String) Utilities.getPrivateField(localObject,
						"id");
				if (!(str.equals("com.teamcenter.rac.common.tcinfocenter")))
					continue;
				ToolBar localToolBar = (ToolBar) Utilities.getPrivateField(
						localObject, "tb");
				localArrayList.add(localToolBar);
			}
		}
		return localArrayList;
	}

	private void createContextMenu(Control paramControl) {
		if (paramControl.getMenu() != null)
			return;
		MenuManager localMenuManager = new MenuManager();
		localMenuManager.setRemoveAllWhenShown(true);
		localMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager paramIMenuManager) {
				if (TCInfoCenterService.this.m_partName == null)
					return;
				paramIMenuManager
						.add(new TCInfoCenterService.CMAction(
								"infoCenter.WhereUsed.TITLE",
								".show_where_used", false));
				paramIMenuManager.add(new TCInfoCenterService.CMAction(
						"infoCenter.WhereRefed.TITLE",
						".show_where_referenced", false));
				paramIMenuManager.add(new TCInfoCenterService.CMAction(
						"infoCenter.ChildCount.TITLE", ".show_child_count",
						false));
				paramIMenuManager.add(new TCInfoCenterService.CMAction(
						"infoCenter.Access.TITLE", ".show_access_information",
						true));
				paramIMenuManager.add(new TCInfoCenterService.CMAction(
						"infoCenter.State.TITLE", ".show_state_information",
						true));
			}
		});
		Menu localMenu = localMenuManager.createContextMenu(paramControl);
		paramControl.setMenu(localMenu);
	}

	private void processComponentEvents(List<AIFComponentEvent> paramList) {
		Iterator localIterator = paramList.iterator();
		while (localIterator.hasNext()) {
			AIFComponentEvent localAIFComponentEvent = (AIFComponentEvent) localIterator
					.next();
			int i = 0;
			InterfaceAIFComponent localInterfaceAIFComponent = localAIFComponentEvent
					.getComponent();
			if ((localInterfaceAIFComponent == null)
					|| ((((this.m_updateOp == null) || (localInterfaceAIFComponent != this.m_updateOp.m_component)))
							&& (!(localInterfaceAIFComponent.getUid()
									.equals(this.m_currentComponentUID)))
							&& (!(localInterfaceAIFComponent.getUid()
									.equals(this.m_currentUnderlyingComponentUID))) && (((this.m_multipleComponents == null) || (!(this.m_multipleComponents
							.contains(localInterfaceAIFComponent)))))))
				continue;
			if (localAIFComponentEvent instanceof AIFComponentDeleteEvent) {
				this.m_infoCache.remove(localInterfaceAIFComponent.getUid());
				setComponent(null);
			} else {
				if ((!(localAIFComponentEvent instanceof AIFComponentChangeEvent))
						&& (!(localAIFComponentEvent instanceof AIFComponentAddChildEvent))
						&& (!(localAIFComponentEvent instanceof AIFComponentRemoveChildEvent)))
					continue;
				if ((localAIFComponentEvent instanceof AIFComponentChildCountChangeEvent)
						|| (localAIFComponentEvent instanceof AIFComponentAddChildEvent)
						|| (localAIFComponentEvent instanceof AIFComponentRemoveChildEvent))
					i = 1;
				if (i != 0)
					continue;
				this.m_infoCache.remove(this.m_currentComponentUID);
				this.m_infoCache.remove(this.m_currentUnderlyingComponentUID);
				this.m_infoCache.remove(localInterfaceAIFComponent.getUid());
				TCComponent localTCComponent = getUnderlyingComponent((TCComponent) localInterfaceAIFComponent);
				if (localTCComponent != null)
					this.m_infoCache.remove(localTCComponent.getUid());
				reassertComponent((TCSession) localInterfaceAIFComponent
						.getSession());
			}
		}
	}

	private void reassertComponent(TCSession paramTCSession) {
		if (this.m_currentComponentUID == null)
			return;
		TCComponent localTCComponent = paramTCSession.getComponentManager()
				.getCachedComponent(this.m_currentComponentUID);
		if (localTCComponent == null)
			return;
		this.m_currentComponentUID = null;
		this.m_currentUnderlyingComponentUID = null;
		setComponent(localTCComponent);
	}

	private void setComponent(TCComponent paramTCComponent) {
		if (this.m_updateOp != null)
			this.m_updateOp.cancel();
		if (paramTCComponent == null) {
			updateDisplay(null, this.m_emptyInfoStatus);
		} else {
			InfoStatus localInfoStatus = (InfoStatus) this.m_infoCache
					.get(paramTCComponent.getUid());
			if ((localInfoStatus != null) && (localInfoStatus.isCached()))
				updateDisplay(paramTCComponent, localInfoStatus);
			else
				this.m_updateOp = new UpdateOperation(paramTCComponent);
		}
	}

	private TCComponent getUnderlyingComponent(TCComponent paramTCComponent) {
		try {
			if ((paramTCComponent == null)
					|| (!(paramTCComponent.isRuntimeType()))) {
				return paramTCComponent;
			}
			// break label30;
			return paramTCComponent.getUnderlyingComponent();
		} catch (TCException localTCException) {
			Logger.getLogger(TCInfoCenterService.class).trace(
					localTCException.getLocalizedMessage(), localTCException);
		}
		return null;
		// label30: return paramTCComponent;
	}

	private void updateDisplay(TCComponent paramTCComponent,
			InfoStatus paramInfoStatus) {
		this.m_currentComponentUID = ((paramTCComponent == null) ? null
				: paramTCComponent.getUid());
		TCComponent localTCComponent = getUnderlyingComponent(paramTCComponent);
		this.m_currentUnderlyingComponentUID = ((localTCComponent == null) ? null
				: localTCComponent.getUid());
		final InfoStatus val$infoStatus = paramInfoStatus;
		// paramInfoStatus
		SWTUIUtilities.asyncExec(new Runnable() {
			@PJLToIgnore
			public void run() {
				IWorkbenchWindow localIWorkbenchWindow = PlatformHelper
						.getCurrentWorkbenchWindow();
				if (localIWorkbenchWindow == null)
					return;
				ICommandService localICommandService = (ICommandService) localIWorkbenchWindow
						.getService(ICommandService.class);
				if (TCInfoCenterService.this.m_contextAccess != null) {
					localICommandService.refreshElements(
							"com.teamcenter.rac.common.tcinfocenter.write",
							null);
					localICommandService.refreshElements(
							"com.teamcenter.rac.common.tcinfocenter.delete",
							null);
					localICommandService.refreshElements(
							"com.teamcenter.rac.common.tcinfocenter.change",
							null);
				}
				if (TCInfoCenterService.this.m_contextState != null) {
					localICommandService.refreshElements(
							"com.teamcenter.rac.common.tcinfocenter.inprocess",
							null);
					localICommandService.refreshElements(
							"com.teamcenter.rac.common.tcinfocenter.checkout",
							null);
					localICommandService.refreshElements(
							"com.teamcenter.rac.common.tcinfocenter.released",
							null);
					localICommandService.refreshElements(
							"com.teamcenter.rac.common.tcinfocenter.published",
							null);
					localICommandService
							.refreshElements(
									"com.teamcenter.rac.common.tcinfocenter.classified",
									null);
				}
				int i = 0;
				Iterator localIterator = new ArrayList(
						TCInfoCenterService.this.m_lsnrs).iterator();
				Object localObject;
				while (localIterator.hasNext()) {
					localObject = (Reference) localIterator.next();
					AbstractTCInfoCenterToolbarContribution localAbstractTCInfoCenterToolbarContribution = (AbstractTCInfoCenterToolbarContribution) ((Reference) localObject)
							.get();
					if (localAbstractTCInfoCenterToolbarContribution != null) {
						localAbstractTCInfoCenterToolbarContribution
								.updateElement(val$infoStatus);
						i = 1;
					} else {
						TCInfoCenterService.this.m_lsnrs.remove(localObject);
					}
				}
				if (i != 0) {
					localObject = (IContextService) PlatformUI.getWorkbench()
							.getAdapter(IContextService.class);
					if (TCInfoCenterService.this.m_contextWhereRef != null) {
						((IContextService) localObject)
								.deactivateContext(TCInfoCenterService.this.m_contextWhereRef);
						TCInfoCenterService.this.m_contextWhereRef = ((IContextService) localObject)
								.activateContext("com.teamcenter.rac.common.tcinfocenter.WhereReferenced");
					}
					if (TCInfoCenterService.this.m_contextWhereUsed != null) {
						((IContextService) localObject)
								.deactivateContext(TCInfoCenterService.this.m_contextWhereUsed);
						TCInfoCenterService.this.m_contextWhereUsed = ((IContextService) localObject)
								.activateContext("com.teamcenter.rac.common.tcinfocenter.WhereUsed");
					}
					if (TCInfoCenterService.this.m_contextChildCount != null) {
						((IContextService) localObject)
								.deactivateContext(TCInfoCenterService.this.m_contextChildCount);
						TCInfoCenterService.this.m_contextChildCount = ((IContextService) localObject)
								.activateContext("com.teamcenter.rac.common.tcinfocenter.ChildCount");
					}
				}
				TCInfoCenterService.this.addContextMenu();
			}
		});
	}

	private void setContexts(String paramString, TCComponent paramTCComponent) {
		// "", paramString, paramTCComponent
		System.out.println("setContexts paramString:"+paramString+"  paramTCComponent:"+paramTCComponent);
	
		final String val$prefName = paramString;
		final TCComponent val$tcComponent = paramTCComponent;
		UIJob local3 = new UIJob(paramString) {
			@PJLToIgnore
			public IStatus runInUIThread(IProgressMonitor paramIProgressMonitor) {
				IContextService localIContextService = (IContextService) PlatformUI
						.getWorkbench().getAdapter(IContextService.class);
				if (localIContextService == null)
					return Status.OK_STATUS;
				int i = ((TCInfoCenterService.this.m_partName != null) && (TCInfoCenterService.this.m_currentComponentUID != null)) ? 1
						: 0;
				ArrayList localArrayList = null;
				int j;
				if ((val$prefName == null)
						|| (val$prefName.endsWith(".show_where_referenced"))) {
					j = ((i != 0) && (getPrefValue(".show_where_referenced",
							false))) ? 1 : 0;
					if ((j != 0)
							&& (TCInfoCenterService.this.m_contextWhereRef == null)) {
						TCInfoCenterService.this.m_contextWhereRef = localIContextService
								.activateContext("com.teamcenter.rac.common.tcinfocenter.WhereReferenced");
					} else if ((j == 0)
							&& (TCInfoCenterService.this.m_contextWhereRef != null)) {
						if (localArrayList == null)
							localArrayList = new ArrayList();
						localArrayList
								.add(TCInfoCenterService.this.m_contextWhereRef);
						TCInfoCenterService.this.m_contextWhereRef = null;
					}
				}
				if ((val$prefName == null)
						|| (val$prefName.endsWith(".show_where_used"))) {
					j = ((i != 0) && (getPrefValue(".show_where_used", false))) ? 1
							: 0;
					if ((j != 0)
							&& (TCInfoCenterService.this.m_contextWhereUsed == null)) {
						TCInfoCenterService.this.m_contextWhereUsed = localIContextService
								.activateContext("com.teamcenter.rac.common.tcinfocenter.WhereUsed");
					} else if ((j == 0)
							&& (TCInfoCenterService.this.m_contextWhereUsed != null)) {
						if (localArrayList == null)
							localArrayList = new ArrayList();
						localArrayList
								.add(TCInfoCenterService.this.m_contextWhereUsed);
						TCInfoCenterService.this.m_contextWhereUsed = null;
					}
				}
				if ((val$prefName == null)
						|| (val$prefName.endsWith(".show_child_count"))) {
					j = ((i != 0) && (getPrefValue(".show_child_count", false))) ? 1
							: 0;
					if ((j != 0)
							&& (TCInfoCenterService.this.m_contextChildCount == null)) {
						TCInfoCenterService.this.m_contextChildCount = localIContextService
								.activateContext("com.teamcenter.rac.common.tcinfocenter.ChildCount");
					} else if ((j == 0)
							&& (TCInfoCenterService.this.m_contextChildCount != null)) {
						if (localArrayList == null)
							localArrayList = new ArrayList();
						localArrayList
								.add(TCInfoCenterService.this.m_contextChildCount);
						TCInfoCenterService.this.m_contextChildCount = null;
					}
				}
				if ((val$prefName == null)
						|| (val$prefName.endsWith(".show_access_information"))) {
					j = ((i != 0) && (getPrefValue(".show_access_information",
							true))) ? 1 : 0;
					if ((j != 0)
							&& (TCInfoCenterService.this.m_contextAccess == null)) {
						TCInfoCenterService.this.m_contextAccess = localIContextService
								.activateContext("com.teamcenter.rac.common.tcinfocenter.Access");
					} else if ((j == 0)
							&& (TCInfoCenterService.this.m_contextAccess != null)) {
						if (localArrayList == null)
							localArrayList = new ArrayList();
						localArrayList
								.add(TCInfoCenterService.this.m_contextAccess);
						TCInfoCenterService.this.m_contextAccess = null;
					}
				}

				if ((val$prefName == null)
						|| (val$prefName.endsWith(".show_state_information"))) {
					j = ((i != 0) && (getPrefValue(".show_state_information",
							true))) ? 1 : 0;
					if ((j != 0)
							&& (TCInfoCenterService.this.m_contextState == null)) {
						TCInfoCenterService.this.m_contextState = localIContextService
								.activateContext("com.teamcenter.rac.common.tcinfocenter.State");
					} else if ((j == 0)
							&& (TCInfoCenterService.this.m_contextState != null)) {
						if (localArrayList == null)
							localArrayList = new ArrayList();
						localArrayList
								.add(TCInfoCenterService.this.m_contextState);
						TCInfoCenterService.this.m_contextState = null;
					}
				}
				if (localArrayList != null)
					localIContextService.deactivateContexts(localArrayList);
				if (i != 0)
					TCInfoCenterService.this.addContextMenu();
				if (val$tcComponent != null)
					TCInfoCenterService.this.setComponent(val$tcComponent);
				return Status.OK_STATUS;
			}

			private boolean getPrefValue(String paramString,
					boolean paramBoolean) {
				Boolean localBoolean = TCInfoCenterService.this.m_prefSvc
						.getLogicalValue(TCInfoCenterService.this.m_partName
								+ paramString);
				if (localBoolean != null)
					return localBoolean.booleanValue();
				return paramBoolean;
			}
		};
		local3.schedule();
	}

	public InfoStatus getInfoStatus() {
		InfoStatus localInfoStatus = (this.m_currentComponentUID != null) ? (InfoStatus) this.m_infoCache
				.get(this.m_currentComponentUID) : null;
		if (localInfoStatus == null)
			localInfoStatus = this.m_emptyInfoStatus;
		return localInfoStatus;
	}

	private class CMAction extends Action {
		private final String m_prefName;

		private CMAction(String paramString1, String paramString2,
				boolean paramBoolean) {
			super(Messages.getString(paramString1), 2);
			this.m_prefName = TCInfoCenterService.this.m_partName
					+ paramString2;
			Boolean localBoolean = TCInfoCenterService.this.m_prefSvc
					.getLogicalValue(this.m_prefName);
			if (localBoolean == null)
				localBoolean = Boolean.valueOf(paramBoolean);
			setChecked(localBoolean.booleanValue());
		}

		public void run() {
			try {
				TCInfoCenterService.this.m_prefSvc.setLogicalValue(
						this.m_prefName, isChecked());
			} catch (TCException localTCException) {
				Logger.getLogger(AbstractTCInfoCenterToolbarContribution.class)
						.error(localTCException.getLocalizedMessage(),
								localTCException);
			}
		}
	}

	class InfoStatus {
		boolean processStage = false;
		boolean releaseStatusList = false;
		boolean publicationSites = false;
		boolean icsClassified = false;
		boolean checkoutState = false;
		boolean writeState = false;
		boolean deleteState = false;
		boolean changeState = false;
		int whereUsedCount = -1;
		int whereRefCount = -1;
		int childCount = -1;
		private boolean m_contextAccessCached = TCInfoCenterService.this.m_contextAccess != null;
		private boolean m_contextStateCached = TCInfoCenterService.this.m_contextState != null;

		boolean isCached() {
			return ((((TCInfoCenterService.this.m_contextAccess == null) || (this.m_contextAccessCached)))
					&& (((TCInfoCenterService.this.m_contextChildCount == null) || (this.childCount != -1)))
					&& (((TCInfoCenterService.this.m_contextState == null) || (this.m_contextStateCached)))
					&& (((TCInfoCenterService.this.m_contextWhereRef == null) || (this.whereRefCount != -1))) && (((TCInfoCenterService.this.m_contextWhereUsed == null) || (this.whereUsedCount != -1))));
		}
	}

	private class UpdateOperation extends AbstractAIFOperation {
		private final TCComponent m_component;

		private UpdateOperation(TCComponent paramTCComponent) {
			super(false);
			this.m_component = paramTCComponent;
			setSession(paramTCComponent.getSession());
			setRule(paramTCComponent.getSession().getOperationJobRule());
			setPriority(40);
			schedule(500L);
		}

		public void executeOperation() {
			try {
				TCComponent localTCComponent1 = this.m_component;
				if (localTCComponent1 == null)
					return;
				TCInfoCenterService.InfoStatus localInfoStatus = new TCInfoCenterService.InfoStatus();

				if ((!(isAbortRequested()))
						&& (TCInfoCenterService.this.m_contextWhereRef != null)
						&& (!(localTCComponent1.isRuntimeType())))
					localInfoStatus.whereRefCount = localTCComponent1
							.getWhereReferencedCount();
				TCComponent localTCComponent2;
				if ((!(isAbortRequested()))
						&& (TCInfoCenterService.this.m_contextWhereUsed != null)) {
					localTCComponent2 = TCInfoCenterService.this
							.getUnderlyingComponent(localTCComponent1);
					if (localTCComponent2 instanceof TCComponentItemRevision)
						localInfoStatus.whereUsedCount = ((TCComponentItemRevision) localTCComponent2)
								.getWhereUsedCount();
				}
				if ((!(isAbortRequested()))
						&& (TCInfoCenterService.this.m_contextChildCount != null))
					localInfoStatus.childCount = localTCComponent1
							.getChildrenCount();
				// Object localObject;
				if ((!(isAbortRequested()))
						&& (TCInfoCenterService.this.m_contextState != null)) {
					localTCComponent2 = TCInfoCenterService.this
							.getUnderlyingComponent(localTCComponent1);
					if (localTCComponent2 != null) {
						String[] localObject = localTCComponent2
								.getProperties(TCInfoCenterService.PROPERTIES);
						localInfoStatus.processStage = ((localObject[0] != null) && (localObject[0]
								.length() > 0));
						localInfoStatus.releaseStatusList = ((localObject[1] != null) && (localObject[1]
								.length() > 0));
						localInfoStatus.publicationSites = ((localObject[2] != null) && (localObject[2]
								.length() > 0));
						localInfoStatus.icsClassified = ((localObject[3] != null) && (localObject[3]
								.equalsIgnoreCase("yes")));
						localInfoStatus.checkoutState = ((localObject[4] != null) && (localObject[4]
								.equalsIgnoreCase("Y")));
					}
				}
				if ((!(isAbortRequested()))
						&& (TCInfoCenterService.this.m_contextAccess != null)) {
					localTCComponent2 = localTCComponent1;
					if (localTCComponent1.isRuntimeType())
						localTCComponent2 = TCInfoCenterService.this
								.getUnderlyingComponent(localTCComponent1);
					if ((localTCComponent2 != null)
							&& (!(localTCComponent2.isRuntimeType()))) {
						boolean[] localObject = TCInfoCenterService.this.m_aclSvc
								.checkPrivileges(localTCComponent2,
										TCInfoCenterService.ACCESS);
						localInfoStatus.writeState = localObject[0];
						localInfoStatus.deleteState = localObject[1];
						localInfoStatus.changeState = localObject[2];
					}
				}
				if (!(isAbortRequested()))
					TCInfoCenterService.this.m_infoCache.put(
							localTCComponent1.getUid(), localInfoStatus);
				if (isAbortRequested())
					return;
				TCInfoCenterService.this.updateDisplay(localTCComponent1,
						localInfoStatus);
				
				/* ===========Added by zhanggl on 20130315 Begin=========== */
				if (localTCComponent1 instanceof TCComponentScheduleTask) {
					System.out
							.println("ww--------updateD5OrgScheduleTaskProps TCComponentScheduleTask-----------------------");
					updateD5OrgScheduleTaskProps(localTCComponent1.getSession(),
							(TCComponentScheduleTask) localTCComponent1);
				}
				/* ===========Added by zhanggl on 20130315 End=========== */
			} catch (Throwable localThrowable) {
				Logger.getLogger(TCInfoCenterService.class).error(
						localThrowable.getLocalizedMessage(), localThrowable);
			}
		}
	}

	// add by wuwei
	private void updateD5OrgScheduleTaskProps(TCSession session,
			TCComponentScheduleTask newScheduleTask) {
		try {
			// int status = newScheduleTask.getIntProperty("status");
			String status = newScheduleTask.getProperty("fnd0status");
			String jci6_TaskType = newScheduleTask
					.getStringProperty("jci6_TaskType");
			System.out
					.println("JCI6_updateProgramInfoRevDate userservice start\n");

			TCUserService tcservice = newScheduleTask.getSession()
					.getUserService();
			Object[] obj = { newScheduleTask };
			tcservice.call("JCI6_updateProgramInfoRevDate", obj);
			System.out
					.println("JCI6_updateProgramInfoRevDate userservice end\n");

			System.out.println("status:" + status + "  jci6_TaskType:"
					+ jci6_TaskType);

			if (("Complete".equals(status)) && (jci6_TaskType != null)
					&& (!jci6_TaskType.equals(""))) {
				String[] arrOptions = session.getPreferenceService()
						.getStringArray(4, "YFJC_Gate_Phase_Mapping");
				if ((arrOptions != null) && (arrOptions.length > 0)) {
					for (int j = 0; j < arrOptions.length; j++) {
						String strTempValue = arrOptions[j];
						if (strTempValue.contains("=")) {
							String[] arrValue = strTempValue.split("=", -1);
							if ((arrValue.length == 2)
									&& (jci6_TaskType.equals(arrValue[0]))) {
								Object[] objInput = new Object[2];
								objInput[0] = arrValue[1];
								objInput[1] = newScheduleTask;
								try {
									tcservice.call(
											"JCI6_updateProjInfoRevStatus",
											objInput);
								} catch (TCException e) {
									MessageBox.post(e.getMessage(), "Error", 4);
									e.printStackTrace();
								}
								System.out
										.println("-------------JCI6_updateProjInfoRevStatus OK----------");
							}
						}
					}
				} else {
					MessageBox.post(
							"preference YFJC_Gate_Phase_Mapping not found",
							"error", 4);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

}