package com.teamcenter.rac.schedule.commands.assigntotask;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDiscipline;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentResourcePool;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.commands.newtask.AssignToNewTaskDialog;
import com.teamcenter.rac.schedule.common.organization.TaskActorInterface;
import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.services.rac.projectmanagement._2014_06.ScheduleManagement.FilteredUser;
import com.teamcenter.services.rac.workflow.WorkflowService;
import com.teamcenter.services.rac.workflow._2008_06.Workflow.GetResourcePoolOutput;
import com.teamcenter.services.rac.workflow._2008_06.Workflow.GroupRoleRef;
import com.teamcenter.services.rac.workflow._2008_06.Workflow.ResourcePoolInfo;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class AssignmentAddButtonListener
  implements Listener
{
  TaskAssignmentDialog dialog = null;
  private Registry textRegistry = Registry.getRegistry("com.teamcenter.rac.schedule.commands.assigntotask.properties.properties");
  protected DuplicateAssignmentHandler handlerDupAssignment = null;
  protected AssignmentErrorHandler handlerAssignmentError = null;
  private PlaceHolderResourceLoadErrorHandler placeHolderResourceLoadError = null;
  private static final Logger logger = Logger.getLogger(AssignmentAddButtonListener.class);

  public AssignmentAddButtonListener(TaskAssignmentDialog paramTaskAssignmentDialog)
  {
    this.dialog = paramTaskAssignmentDialog;
  }

  public void handleEvent(final Event paramEvent)
  {
    Display.getCurrent().asyncExec(new Runnable()
    {
      public void run()
      {
        AssignmentAddButtonListener.this.doWork(paramEvent);
      }
    });
  }

  protected void doWork(Event paramEvent)
  {
    if ((this.dialog.getSelectedViewer() instanceof TableViewer))
      handleFilteredUserAssignment();
    else
      handleAssignments();
    handleAssignmentErrors();
    this.dialog.getScheduleTaskTreeViewer().refresh(true);
  }

  private void handleFilteredUserAssignment()
  {
    if (!(this.dialog.getSelectedViewer() instanceof TableViewer))
      return;
    TableViewer localTableViewer = (TableViewer)this.dialog.getSelectedViewer();
    TableItem[] arrayOfTableItem = localTableViewer.getTable().getItems();
    for (int i = 0; i < arrayOfTableItem.length; i++)
      if (arrayOfTableItem[i] != null)
      {
        FilteredInternalUser localFilteredInternalUser = (FilteredInternalUser)arrayOfTableItem[i].getData();
        if (localFilteredInternalUser.isCheckedUser())
        {
          TCComponentUser localTCComponentUser = localFilteredInternalUser.user.user;
          ISelection localISelection = this.dialog.getScheduleTaskTreeViewer().getSelection();
          if ((localISelection instanceof IStructuredSelection))
          {
            IStructuredSelection localIStructuredSelection = (IStructuredSelection)localISelection;
            List localList = localIStructuredSelection.toList();
            Iterator localIterator = localList.iterator();
            while (localIterator.hasNext())
            {
              ResourceTreeModel localResourceTreeModel = (ResourceTreeModel)localIterator.next();
              TCComponentDiscipline localTCComponentDiscipline = null;
              TCComponentResourcePool localTCComponentResourcePool = null;
              double d1 = 100.0D;
              Object localObject1;
              Object localObject2;
              TCComponentScheduleTask localTCComponentScheduleTask;
              Object localObject3;
              Object localObject4;
              if ((localResourceTreeModel instanceof PlaceHolderAssignmentTreeModel))
              {
                localObject1 = (PlaceHolderAssignmentTreeModel)localResourceTreeModel;
                localObject2 = (ScheduleTaskTreeModel)((PlaceHolderAssignmentTreeModel)localObject1).getParent();
                localTCComponentScheduleTask = ((ScheduleTaskTreeModel)localObject2).getUnderlyingObject();
                localObject3 = ((PlaceHolderAssignmentTreeModel)localObject1).getUnderlyingObject();
                if ((localObject3 instanceof ResourceAssignmentProxy))
                {
                  localObject4 = (ResourceAssignmentProxy)localObject3;
                  TCComponent localObject5 = ((ResourceAssignmentProxy)localObject3).getResource();
                  if (((localObject5 instanceof TCComponentDiscipline)) && (localFilteredInternalUser.user.discipline != null) && (((TCComponent)localObject5).equals(localFilteredInternalUser.user.discipline)))
                    localTCComponentDiscipline = (TCComponentDiscipline)localObject5;
                  if (((localObject5 instanceof TCComponentResourcePool)) && (((ResourceAssignmentProxy)localObject4).isPlaceHolderAssignment()) && (localFilteredInternalUser.user.groupRole != null) && (((TCComponent)localObject5).equals(localFilteredInternalUser.user.groupRole)))
                    localTCComponentResourcePool = (TCComponentResourcePool)localObject5;
                }
                localObject4 = new ResourceAssignmentProxy(localTCComponentScheduleTask, localTCComponentUser, d1);
                Object localObject5 = new AssignmentTreeModel((ResourceAssignmentProxy)localObject4, this.dialog.getTcSession());
                boolean bool = addAssignment(localTCComponentScheduleTask, 100.0D, (AssignmentTreeModel)localObject5, (ScheduleTaskTreeModel)localObject2, (PlaceHolderAssignmentTreeModel)localObject1);
                double d2;
                double d3;
                if ((localTCComponentDiscipline != null) && (bool))
                {
                  d2 = ((PlaceHolderAssignmentTreeModel)localObject1).getResourceLoad();
                  d3 = d2 - 100.0D;
                  if (d3 <= 0.0D)
                  {
                    d1 = d2;
                    ((PlaceHolderAssignmentTreeModel)localObject1).updateResourceLoad(d3);
                    this.dialog.getAssignmentCache().removeAssignment((ResourceAssignmentProxy)localObject3);
                    ((ScheduleTaskTreeModel)localObject2).removeChild((AssignmentTreeModel)localObject1);
                    ((AssignmentTreeModel)localObject5).updateResourceLoad(d1);
                  }
                  else
                  {
                    ((PlaceHolderAssignmentTreeModel)localObject1).updateResourceLoad(d3);
                  }
                  ((ResourceAssignmentProxy)localObject4).setDiscipline(localTCComponentDiscipline);
                  ((ResourceAssignmentProxy)localObject4).setResourcePoolAssignment(localFilteredInternalUser.user.groupRole);
                }
                if ((localTCComponentResourcePool != null) && (bool))
                {
                  d2 = ((PlaceHolderAssignmentTreeModel)localObject1).getResourceLoad();
                  d3 = d2 - 100.0D;
                  if (d3 <= 0.0D)
                  {
                    d1 = d2;
                    ((PlaceHolderAssignmentTreeModel)localObject1).updateResourceLoad(d3);
                    this.dialog.getAssignmentCache().removeAssignment((ResourceAssignmentProxy)localObject3);
                    ((ScheduleTaskTreeModel)localObject2).removeChild((AssignmentTreeModel)localObject1);
                    ((AssignmentTreeModel)localObject5).updateResourceLoad(d1);
                  }
                  else
                  {
                    ((PlaceHolderAssignmentTreeModel)localObject1).updateResourceLoad(d3);
                  }
                  ((ResourceAssignmentProxy)localObject4).setResourcePoolAssignment(localTCComponentResourcePool);
                  ((ResourceAssignmentProxy)localObject4).setPlaceHolderAssignment(true);
                }
              }
              if ((localResourceTreeModel instanceof ScheduleTaskTreeModel))
              {
                localObject1 = (ScheduleTaskTreeModel)localResourceTreeModel;
                localObject2 = ((ScheduleTaskTreeModel)localObject1).getUnderlyingObject();
                localTCComponentScheduleTask = (TCComponentScheduleTask)localObject2;
                localObject3 = new ResourceAssignmentProxy(localTCComponentScheduleTask, localTCComponentUser, 100.0D);
                localObject4 = new AssignmentTreeModel((ResourceAssignmentProxy)localObject3, this.dialog.getTcSession());
                addAssignment(localTCComponentScheduleTask, 100.0D, (AssignmentTreeModel)localObject4, (ScheduleTaskTreeModel)localObject1);
              }
            }
          }
        }
      }
  }

  private void handleAssignments()
  {
    ISelection localISelection1 = this.dialog.getSelectedViewer().getSelection();
    if (!(localISelection1 instanceof IStructuredSelection))
      return;
    IStructuredSelection localIStructuredSelection1 = (IStructuredSelection)localISelection1;
    List localList1 = localIStructuredSelection1.toList();
    Iterator localIterator1 = localList1.iterator();
    while (localIterator1.hasNext())
    {
      TaskActorInterface localTaskActorInterface = (TaskActorInterface)localIterator1.next();
      TaskActorInterface parentTaskActorInterface = localTaskActorInterface.getParent();
      TCComponent localTCComponent = localTaskActorInterface.find();
      ISelection localISelection2 = this.dialog.getScheduleTaskTreeViewer().getSelection();
      if ((localISelection2 instanceof IStructuredSelection))
      {
        IStructuredSelection localIStructuredSelection2 = (IStructuredSelection)localISelection2;
        List localList2 = localIStructuredSelection2.toList();
        Iterator localIterator2 = localList2.iterator();
        while (localIterator2.hasNext())
        {
          ResourceTreeModel localResourceTreeModel = (ResourceTreeModel)localIterator2.next();
          Object localObject1;
          Object localObject2;
          Object localObject3;
          Object localObject4;
          Object localObject5;
          //Object localObject6;
          Object localObject7;
          Object localObject8;
          //Object localObject9;
          Object localObject11;
          Object localObject12;
          if ((localResourceTreeModel instanceof ScheduleTaskTreeModel))
          {
            localObject1 = (ScheduleTaskTreeModel)localResourceTreeModel;
            localObject2 = null;
            localObject3 = null;
            localObject4 = null;
            localObject5 = ((ScheduleTaskTreeModel)localObject1).getUnderlyingObject();
            GroupRoleRef[] localObject6 = new GroupRoleRef[1];
            localObject6[0] = new GroupRoleRef();
            localObject6[0].allowSubGroup = 0;
            localObject7 = this.dialog.getPlaceHolderOptions();
            Object localObject10;
            if (localObject7 != null)
            {
              if ((localTaskActorInterface.isRole()) && ((localTCComponent instanceof TCComponentRole)))
              {
                localObject3 = (TCComponentRole)localTCComponent;
                if (((BitSet)localObject7).get(2))
                {
                  localObject8 = localTaskActorInterface.getParent();
                  TCComponent localObject9 = ((TaskActorInterface)localObject8).find();
                  if ((((TaskActorInterface)localObject8).isGroup()) && ((localObject9 instanceof TCComponentGroup)))
                    localObject2 = (TCComponentGroup)localObject9;
                }
                else
                {
                  localObject2 = null;
                }
              }
              else if ((localTaskActorInterface.isGroup()) && ((localTCComponent instanceof TCComponentGroup)))
              {
                localObject2 = (TCComponentGroup)localTCComponent;
                localObject3 = null;
              }
              localObject6[0].groupTag = ((TCComponentGroup)localObject2);
              localObject6[0].roleTag = ((TCComponentRole)localObject3);
              localObject8 = WorkflowService.getService(this.dialog.getTcSession());
              GetResourcePoolOutput localObject9 = ((WorkflowService)localObject8).getResourcePool((GroupRoleRef[])localObject6);
              localObject4 = localObject9.resourcePoolInfo[0].resourcePoolTag;
              localObject10 = new ResourceAssignmentProxy((TCComponentScheduleTask)localObject5, (TCComponent)localObject4, 100.0D, (TCComponentResourcePool)localObject4, true);
              localObject11 = new PlaceHolderAssignmentTreeModel((ResourceAssignmentProxy)localObject10, this.dialog.getTcSession());
              addAssignment((TCComponentScheduleTask)localObject5, 100.0D, (AssignmentTreeModel)localObject11, (ScheduleTaskTreeModel)localObject1);
            }
            localObject8 = this.dialog.getResourcePoolOptions();
            if (localObject8 != null)
            {
              if ((localTaskActorInterface.isRole()) && ((localTCComponent instanceof TCComponentRole)))
              {
                localObject3 = (TCComponentRole)localTCComponent;
                if (((BitSet)localObject8).get(2))
                {
                  TaskActorInterface localObject9 = localTaskActorInterface.getParent();
                  localObject10 = ((TaskActorInterface)localObject9).find();
                  if ((((TaskActorInterface)localObject9).isGroup()) && ((localObject10 instanceof TCComponentGroup)))
                    localObject2 = (TCComponentGroup)localObject10;
                }
                else
                {
                  localObject2 = null;
                }
                if (((BitSet)localObject8).get(0))
                  localObject6[0].isAllMembers = 0;
                else
                  localObject6[0].isAllMembers = 1;
              }
              else if ((localTaskActorInterface.isGroup()) && ((localTCComponent instanceof TCComponentGroup)))
              {
                localObject2 = (TCComponentGroup)localTCComponent;
                if (((BitSet)localObject8).get(0))
                  localObject6[0].isAllMembers = 0;
                else
                  localObject6[0].isAllMembers = 1;
                localObject3 = null;
              }
              localObject6[0].groupTag = ((TCComponentGroup)localObject2);
              localObject6[0].roleTag = ((TCComponentRole)localObject3);
              WorkflowService localObject9 = WorkflowService.getService(this.dialog.getTcSession());
              GetResourcePoolOutput yfjc_localObject10 = ((WorkflowService)localObject9).getResourcePool((GroupRoleRef[])localObject6);
              localObject4 = yfjc_localObject10.resourcePoolInfo[0].resourcePoolTag;
              localObject11 = new ResourceAssignmentProxy((TCComponentScheduleTask)localObject5, (TCComponent)localObject4, 100.0D, null, false);
              localObject12 = new ResourcePoolAssignmentTreeModel((ResourceAssignmentProxy)localObject11, this.dialog.getTcSession());
              addAssignment((TCComponentScheduleTask)localObject5, 100.0D, (AssignmentTreeModel)localObject12, (ScheduleTaskTreeModel)localObject1);
            }
            if ((localObject7 == null) && (localObject8 == null) && ((localTCComponent instanceof TCComponentDiscipline)))
            {
              ResourceAssignmentProxy localObject9 = new ResourceAssignmentProxy((TCComponentScheduleTask)localObject5, localTCComponent, 100.0D, null, true);
              localObject10 = new PlaceHolderAssignmentTreeModel((ResourceAssignmentProxy)localObject9, this.dialog.getTcSession());
              addAssignment((TCComponentScheduleTask)localObject5, 100.0D, (AssignmentTreeModel)localObject10, (ScheduleTaskTreeModel)localObject1);
            }
            if ((localObject7 == null) && (localObject8 == null) && (!(localTCComponent instanceof TCComponentDiscipline)))
            {
              ResourceAssignmentProxy localObject9 = new ResourceAssignmentProxy((TCComponentScheduleTask)localObject5, localTCComponent, 100.0D);
              localObject10 = new AssignmentTreeModel((ResourceAssignmentProxy)localObject9, this.dialog.getTcSession());
              addAssignment((TCComponentScheduleTask)localObject5, 100.0D, (AssignmentTreeModel)localObject10, (ScheduleTaskTreeModel)localObject1);
              if(parentTaskActorInterface.isDiscipline()){
            	  localObject9.setDiscipline((TCComponentDiscipline) parentTaskActorInterface.find());
              }
            }
          }
          if ((localResourceTreeModel instanceof PlaceHolderAssignmentTreeModel))
          {
            localObject1 = (PlaceHolderAssignmentTreeModel)localResourceTreeModel;
            localObject2 = (ScheduleTaskTreeModel)((PlaceHolderAssignmentTreeModel)localObject1).getParent();
            localObject3 = ((ScheduleTaskTreeModel)localObject2).getUnderlyingObject();
            localObject4 = ((PlaceHolderAssignmentTreeModel)localObject1).getUnderlyingObject();
            if ((localObject4 instanceof ResourceAssignmentProxy))
            {
              localObject5 = ((ResourceAssignmentProxy)localObject4).getResource();
              if ((localTCComponent instanceof TCComponentDiscipline))
              {
            	ResourceAssignmentProxy localObject6 = (ResourceAssignmentProxy)localObject4;
                localObject7 = ((ResourceAssignmentProxy)localObject6).getResourcePoolAssignment();
                localObject8 = null;
                if ((localObject5 instanceof TCComponentDiscipline))
                  localObject8 = new ResourceAssignmentProxy((TCComponentScheduleTask)localObject3, localTCComponent, 100.0D, (TCComponentResourcePool)localObject7, true);
                else
                  localObject8 = new ResourceAssignmentProxy((TCComponentScheduleTask)localObject3, localTCComponent, 100.0D, (TCComponentResourcePool)localObject5, true);
                PlaceHolderAssignmentTreeModel localObject9 = new PlaceHolderAssignmentTreeModel((ResourceAssignmentProxy)localObject8, this.dialog.getTcSession());
                boolean bool1 = addAssignment((TCComponentScheduleTask)localObject3, 100.0D, (AssignmentTreeModel)localObject9, (ScheduleTaskTreeModel)localObject2);
                if (bool1)
                {
                  this.dialog.getAssignmentCache().removeAssignment((ResourceAssignmentProxy)localObject6);
                  ((ScheduleTaskTreeModel)localObject2).removeChild((AssignmentTreeModel)localObject1);
                }
              }
              if (((localTCComponent instanceof TCComponentGroup)) || ((localTCComponent instanceof TCComponentRole)))
              {
            	TCComponentGroup localObject6 = null;
                localObject7 = null;
                localObject8 = null;
                GroupRoleRef[] localObject9 = new GroupRoleRef[1];
                localObject9[0] = new GroupRoleRef();
                localObject9[0].allowSubGroup = 0;
                BitSet localBitSet = this.dialog.getPlaceHolderOptions();
                if (localBitSet != null)
                {
                  if ((localTaskActorInterface.isRole()) && ((localTCComponent instanceof TCComponentRole)))
                  {
                    localObject7 = (TCComponentRole)localTCComponent;
                    if (localBitSet.get(2))
                    {
                      localObject11 = localTaskActorInterface.getParent();
                      localObject12 = ((TaskActorInterface)localObject11).find();
                      if ((((TaskActorInterface)localObject11).isGroup()) && ((localObject12 instanceof TCComponentGroup)))
                        localObject6 = (TCComponentGroup)localObject12;
                    }
                    else
                    {
                      localObject6 = null;
                    }
                  }
                  else if ((localTaskActorInterface.isGroup()) && ((localTCComponent instanceof TCComponentGroup)))
                  {
                    localObject6 = (TCComponentGroup)localTCComponent;
                    localObject7 = null;
                  }
                  localObject9[0].groupTag = ((TCComponentGroup)localObject6);
                  localObject9[0].roleTag = ((TCComponentRole)localObject7);
                  localObject11 = WorkflowService.getService(this.dialog.getTcSession());
                  GetResourcePoolOutput yfjc_localObject12 = ((WorkflowService)localObject11).getResourcePool((GroupRoleRef[])localObject9);
                  localObject8 = yfjc_localObject12.resourcePoolInfo[0].resourcePoolTag;
                  ResourceAssignmentProxy localResourceAssignmentProxy1 = null;
                  if ((localObject5 instanceof TCComponentResourcePool))
                  {
                    localResourceAssignmentProxy1 = new ResourceAssignmentProxy((TCComponentScheduleTask)localObject3, (TCComponent)localObject8, 100.0D, null, true);
                    ResourceAssignmentProxy localResourceAssignmentProxy2 = (ResourceAssignmentProxy)localObject4;
                    PlaceHolderAssignmentTreeModel localPlaceHolderAssignmentTreeModel = new PlaceHolderAssignmentTreeModel(localResourceAssignmentProxy1, this.dialog.getTcSession());
                    boolean bool2 = addAssignment((TCComponentScheduleTask)localObject3, 100.0D, localPlaceHolderAssignmentTreeModel, (ScheduleTaskTreeModel)localObject2);
                    if (bool2)
                    {
                      this.dialog.getAssignmentCache().removeAssignment(localResourceAssignmentProxy2);
                      ((ScheduleTaskTreeModel)localObject2).removeChild((AssignmentTreeModel)localObject1);
                    }
                  }
                  else
                  {
                    ((PlaceHolderAssignmentTreeModel)localResourceTreeModel).UpdateResourcePool((TCComponent)localObject8);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private void handleAssignmentErrors()
  {
    String str1;
    String str2;
    Shell localShell;
    if (this.handlerDupAssignment != null)
    {
      str1 = this.textRegistry.getString("AssignmentAddButtonListener.AssignmentExists.Warning.Dialog.TITLE");
      str2 = this.handlerDupAssignment.getWarnings();
      localShell = this.dialog.getShlTaskAssignment();
      MessageDialog.openWarning(localShell, str1, str2);
      this.handlerDupAssignment = null;
    }
    if (this.handlerAssignmentError != null)
    {
      str1 = this.textRegistry.getString("AssignmentAddButtonListener.AssignmentExists.Warning.Dialog.TITLE");
      str2 = this.handlerAssignmentError.getWarnings();
      localShell = this.dialog.getShlTaskAssignment();
      MessageDialog.openWarning(localShell, str1, str2);
      this.handlerAssignmentError = null;
    }
    if (this.placeHolderResourceLoadError != null)
    {
      str1 = this.textRegistry.getString("AssignmentAddButtonListener.AssignmentExists.Warning.Dialog.TITLE");
      str2 = this.placeHolderResourceLoadError.getWarnings();
      localShell = this.dialog.getShlTaskAssignment();
      MessageDialog.openWarning(localShell, str1, str2);
      this.placeHolderResourceLoadError = null;
    }
  }

  protected void doResourceAssignments(TCComponentScheduleTask paramTCComponentScheduleTask, TCComponent paramTCComponent, TCSession paramTCSession, ScheduleTaskTreeModel paramScheduleTaskTreeModel)
  {
    ResourceAssignmentProxy localResourceAssignmentProxy = new ResourceAssignmentProxy(paramTCComponentScheduleTask, paramTCComponent, 100.0D, null, false);
    ResourcePoolAssignmentTreeModel localResourcePoolAssignmentTreeModel = new ResourcePoolAssignmentTreeModel(localResourceAssignmentProxy, paramTCSession);
    addAssignment(paramTCComponentScheduleTask, 100.0D, localResourcePoolAssignmentTreeModel, paramScheduleTaskTreeModel);
  }

  protected boolean addAssignment(TCComponentScheduleTask paramTCComponentScheduleTask, double paramDouble, AssignmentTreeModel paramAssignmentTreeModel, ScheduleTaskTreeModel paramScheduleTaskTreeModel)
  {
    boolean bool = false;
    try
    {
      TCComponent localTCComponent = paramAssignmentTreeModel.getUnderlyingObject();
      if ((localTCComponent instanceof ResourceAssignmentProxy))
      {
        ResourceAssignmentProxy localResourceAssignmentProxy = (ResourceAssignmentProxy)localTCComponent;
        List localList = paramScheduleTaskTreeModel.getAssignments();
        int i = 1;
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          AssignmentTreeModel localAssignmentTreeModel = (AssignmentTreeModel)localIterator.next();
          String str;
          if (localAssignmentTreeModel.getResource().equals(localResourceAssignmentProxy.getResource()))
          {
            if (this.handlerDupAssignment == null)
              this.handlerDupAssignment = new DuplicateAssignmentHandler();
            String localObject = localAssignmentTreeModel.getResource().toDisplayString();
            str = (this.dialog instanceof AssignToNewTaskDialog) ? ((AssignToNewTaskDialog)this.dialog).getNewTaskName() : paramScheduleTaskTreeModel.getUnderlyingObject().toDisplayString();
            this.handlerDupAssignment.addDuplicate((String)localObject, str);
            i = 0;
            break;
          }
          Object localObject = paramScheduleTaskTreeModel.getUnderlyingObject();
          if ((localObject != null) && (TaskHelper.getWorkflowProcess((TCComponent)localObject) != null))
          {
            if (this.handlerAssignmentError == null)
              this.handlerAssignmentError = new AssignmentErrorHandler();
            str = paramScheduleTaskTreeModel.getUnderlyingObject().toDisplayString();
            this.handlerAssignmentError.addWFTriggeredTask(str);
            i = 0;
            break;
          }
        }
        if (i != 0)
        {
          if (!(this.dialog instanceof AssignToNewTaskDialog))
            this.dialog.getAssignmentCache().addAssignment(localResourceAssignmentProxy);
          paramScheduleTaskTreeModel.addAssignment(paramAssignmentTreeModel);
          this.dialog.getScheduleTaskTreeViewer().expandToLevel(paramScheduleTaskTreeModel, 1);
          bool = true;
        }
      }
    }
    catch (TCException localTCException)
    {
      logger.error("Exception:removeButton", localTCException);
    }
    return bool;
  }

  private boolean addAssignment(TCComponentScheduleTask paramTCComponentScheduleTask, double paramDouble, AssignmentTreeModel paramAssignmentTreeModel, ScheduleTaskTreeModel paramScheduleTaskTreeModel, PlaceHolderAssignmentTreeModel paramPlaceHolderAssignmentTreeModel)
  {
    boolean bool = false;
    try
    {
      Object localObject2;
      if (paramPlaceHolderAssignmentTreeModel.getResourceLoad() <= 0.0D)
      {
        if (this.placeHolderResourceLoadError == null)
          this.placeHolderResourceLoadError = new PlaceHolderResourceLoadErrorHandler();
        String localObject1 = paramAssignmentTreeModel.getResource().toDisplayString();
        localObject2 = paramPlaceHolderAssignmentTreeModel.toDisplayString();
        this.placeHolderResourceLoadError.addInvalidTask((String)localObject1, (String)localObject2);
        return bool;
      }
      Object localObject1 = paramAssignmentTreeModel.getUnderlyingObject();
      if ((localObject1 instanceof ResourceAssignmentProxy))
      {
        localObject2 = (ResourceAssignmentProxy)localObject1;
        List localList = paramScheduleTaskTreeModel.getAssignments();
        int i = 1;
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          AssignmentTreeModel localAssignmentTreeModel = (AssignmentTreeModel)localIterator.next();
          String str;
          if (localAssignmentTreeModel.getResource().equals(((ResourceAssignmentProxy)localObject2).getResource()))
          {
            if (this.handlerDupAssignment == null)
              this.handlerDupAssignment = new DuplicateAssignmentHandler();
            String localObject3 = localAssignmentTreeModel.getResource().toDisplayString();
            str = (this.dialog instanceof AssignToNewTaskDialog) ? ((AssignToNewTaskDialog)this.dialog).getNewTaskName() : paramScheduleTaskTreeModel.getUnderlyingObject().toDisplayString();
            this.handlerDupAssignment.addDuplicate((String)localObject3, str);
            i = 0;
            break;
          }
          Object localObject3 = paramScheduleTaskTreeModel.getUnderlyingObject();
          if ((localObject3 != null) && (TaskHelper.getWorkflowProcess((TCComponent)localObject3) != null))
          {
            if (this.handlerAssignmentError == null)
              this.handlerAssignmentError = new AssignmentErrorHandler();
            str = paramScheduleTaskTreeModel.getUnderlyingObject().toDisplayString();
            this.handlerAssignmentError.addWFTriggeredTask(str);
            i = 0;
            break;
          }
        }
        if (i != 0)
        {
          if (!(this.dialog instanceof AssignToNewTaskDialog))
            this.dialog.getAssignmentCache().addAssignment((ResourceAssignmentProxy)localObject2);
          paramScheduleTaskTreeModel.addAssignment(paramAssignmentTreeModel);
          this.dialog.getScheduleTaskTreeViewer().expandToLevel(paramScheduleTaskTreeModel, 1);
          bool = true;
        }
      }
    }
    catch (TCException localTCException)
    {
      logger.error("Exception:removeButton", localTCException);
    }
    return bool;
  }

  private void updateAssignment(TCComponentScheduleTask paramTCComponentScheduleTask, double paramDouble, AssignmentTreeModel paramAssignmentTreeModel, ScheduleTaskTreeModel paramScheduleTaskTreeModel)
  {
    try
    {
      int i = 1;
      TCComponent localTCComponent = paramAssignmentTreeModel.getUnderlyingObject();
      if ((localTCComponent instanceof ResourceAssignmentProxy))
      {
        ResourceAssignmentProxy localResourceAssignmentProxy = (ResourceAssignmentProxy)localTCComponent;
        List localList = paramScheduleTaskTreeModel.getAssignments();
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          AssignmentTreeModel localAssignmentTreeModel = (AssignmentTreeModel)localIterator.next();
          String str;
          if (localAssignmentTreeModel.getResource().equals(localResourceAssignmentProxy.getResource()))
          {
            if (this.handlerDupAssignment == null)
              this.handlerDupAssignment = new DuplicateAssignmentHandler();
            String localObject = localAssignmentTreeModel.getResource().toDisplayString();
            str = paramScheduleTaskTreeModel.getUnderlyingObject().toDisplayString();
            this.handlerDupAssignment.addDuplicate((String)localObject, str);
            i = 0;
            break;
          }
          Object localObject = paramScheduleTaskTreeModel.getUnderlyingObject();
          if (TaskHelper.getWorkflowProcess((TCComponent)localObject) != null)
          {
            if (this.handlerAssignmentError == null)
              this.handlerAssignmentError = new AssignmentErrorHandler();
            str = paramScheduleTaskTreeModel.getUnderlyingObject().toDisplayString();
            this.handlerAssignmentError.addWFTriggeredTask(str);
            i = 0;
            break;
          }
        }
        if (i != 0)
        {
          this.dialog.getAssignmentCache().addAssignment(localResourceAssignmentProxy);
          paramScheduleTaskTreeModel.addAssignment(paramAssignmentTreeModel);
          this.dialog.getScheduleTaskTreeViewer().expandToLevel(paramScheduleTaskTreeModel, 1);
        }
      }
    }
    catch (TCException localTCException)
    {
      logger.error("Exception:removeButton", localTCException);
    }
  }
}