/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.teamcenter.rac.schedule.commands.newtask;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.commands.newitem.AbstractNewItemPanel;
import com.teamcenter.rac.commands.open.OpenCommand;
import com.teamcenter.rac.commands.paste.PasteCommand;
import com.teamcenter.rac.common.create.AbstractCreatePage;
import com.teamcenter.rac.common.create.ICreateInstanceInput;
import com.teamcenter.rac.kernel.BOCreatePropertyDescriptor;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.ScheduleViewApplication;
import com.teamcenter.rac.schedule.ScheduleViewApplicationPanel;
import com.teamcenter.rac.schedule.commands.AbstractSchedulingOperation;
import com.teamcenter.rac.schedule.common.util.SchedulingExceptionHandler;
import com.teamcenter.rac.schedule.project.scheduling.IntValuePair;
import com.teamcenter.rac.schedule.project.scheduling.ModelFactory;
import com.teamcenter.rac.schedule.project.scheduling.ReferenceIDContainer;
import com.teamcenter.rac.schedule.project.scheduling.SchedulingException;
import com.teamcenter.rac.schedule.project.scheduling.StringValuePair;
import com.teamcenter.rac.schedule.project.scheduling.TaskCreateContainer;
import com.teamcenter.rac.schedule.project.scheduling.TaskModel;
import com.teamcenter.rac.schedule.project.scheduling.TypedStringValuePair;
import com.teamcenter.rac.schedule.project.scheduling.UpdateContainer;
import com.teamcenter.rac.schedule.scheduler.componentutils.ScheduleHelper;
import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
import com.teamcenter.rac.schedule.soainterface.CreateTaskDeliverableTemplatesRunner;
import com.teamcenter.rac.stylesheet.AbstractRendering;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.services.rac.projectmanagement._2007_06.ScheduleManagement;
import com.teamcenter.services.rac.projectmanagement._2007_06.ScheduleManagement.TaskDeliverableContainer;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.eclipse.core.runtime.jobs.Job;

public class NewTaskOperation extends AbstractSchedulingOperation {
	private ScheduleViewApplicationPanel schAppPanel = null;
	protected String taskName = null;
	protected String id;
	protected String revid;
	protected String scheduleName = null;
	protected String taskDescription = null;
	protected int taskType = 0;
	protected boolean createPGStruct = false;
	protected GregorianCalendar startDate = null;
	protected GregorianCalendar finishDate = null;
	protected int workTask = 0;
	protected int completeTask = 0;
	protected boolean openOnCreateFlag = false;
	protected InterfaceAIFComponent[] pasteTargets = null;
	protected TCComponent newTask = null;
	protected AIFDesktop desktop = null;
	protected boolean successFlag = true;
	protected TaskAssignmentsPanel assignmentPanel = null;
	protected int wfTriggerType = 0;
	protected String scheduleTaskType;
	protected TCComponent wfTemplate;
	protected TCComponent privUser;
	protected TCComponentUser processOwner;
	ReferenceIDContainer refIDCont = null;
	private Map tmExtendedAttributes;
	private Map trmExtendedAttributes;
	private Map smExtendedAttributesObject;
	private Map smExtendedAttributesRevObject;

	public NewTaskOperation(NewTaskCommand paramNewTaskCommand) {
		this.desktop = null;
		this.taskName = paramNewTaskCommand.taskName;
		this.taskDescription = paramNewTaskCommand.taskDesc;
		this.scheduleName = paramNewTaskCommand.scheduleName;
		this.taskType = paramNewTaskCommand.taskType;
		this.startDate = paramNewTaskCommand.startDate;
		this.finishDate = paramNewTaskCommand.finishDate;
		this.workTask = paramNewTaskCommand.workTask;
		this.completeTask = paramNewTaskCommand.completeTask;
		this.openOnCreateFlag = false;
		this.pasteTargets = paramNewTaskCommand.targetArray;
		this.schAppPanel = ((ScheduleViewApplicationPanel) ScheduleViewApplication
				.getApplication().getApplicationPanel());
	}

	public NewTaskOperation(NewTaskDialog paramNewTaskDialog) {
		this.schAppPanel = ((ScheduleViewApplicationPanel) ScheduleViewApplication
				.getApplication().getApplicationPanel());
		this.taskName = paramNewTaskDialog.getTaskName();
		this.taskDescription = paramNewTaskDialog.getTaskDescription();
		this.taskType = paramNewTaskDialog.getTaskType();
		this.createPGStruct = paramNewTaskDialog.getCreatePGStruct()
				.booleanValue();
		Date localDate1 = paramNewTaskDialog.getTaskStartDate();
		this.startDate = new GregorianCalendar();
		if (localDate1 != null)
			this.startDate.setTime(localDate1);
		Date localDate2 = paramNewTaskDialog.getTaskduDate();
		this.finishDate = new GregorianCalendar();
		if (localDate2 != null)
			this.finishDate.setTime(localDate2);
		this.workTask = paramNewTaskDialog.getTaskWork();
		this.completeTask = paramNewTaskDialog.getTaskComplete();
		this.pasteTargets = paramNewTaskDialog.pasteTargets;
		this.assignmentPanel = paramNewTaskDialog.getTaskAssignmentPanel();
		this.id = paramNewTaskDialog.getTaskID();
		this.revid = paramNewTaskDialog.getTaskRevID();
		this.scheduleTaskType = paramNewTaskDialog.dialogPanel.getItemType();
		this.wfTriggerType = this.assignmentPanel.getTaskWorkflowTriggerType();
		this.wfTemplate = this.assignmentPanel.getWorkflowTemplate();
		this.privUser = this.assignmentPanel.getPrivUser();
		this.processOwner = this.assignmentPanel.getProcessOwner();
		AbstractRendering localAbstractRendering = paramNewTaskDialog
				.getItemMasterFormStyleSheetPanel();
		if (localAbstractRendering != null)
			this.tmExtendedAttributes = localAbstractRendering
					.getRenderingModified();
		localAbstractRendering = paramNewTaskDialog
				.getItemRevMasterFormStyleSheetPanel();
		if (localAbstractRendering != null)
			this.trmExtendedAttributes = localAbstractRendering
					.getRenderingModified();
		List localList1 = null;
		if (paramNewTaskDialog.taskPanel.taskMasterFormPanel
				.getAdditionalPropertiesPage() != null)
			localList1 = paramNewTaskDialog.taskPanel.taskMasterFormPanel
					.getAdditionalPropertiesPage().getCreateInputs();
		this.smExtendedAttributesObject = new HashMap();
		Object localObject1;
		Object localObject3;
		Object localObject2;
		Object localObject4;
		Object localObject5;
		Object localObject6;
		if (localList1 != null)
			for (int i = 0; i < localList1.size(); ++i) {
				ICreateInstanceInput localICreateInstanceInput = null;
				localICreateInstanceInput = (ICreateInstanceInput) localList1
						.get(i);
				if (localICreateInstanceInput == null)
					continue;
				localObject1 = null;
				localObject1 = localICreateInstanceInput.getCreateInputs();
				if ((localObject1 == null)
						|| (((Map) localObject1).size() <= 0))
					continue;
				localObject3 = ((Map) localObject1).entrySet().iterator();
				while (((Iterator) localObject3).hasNext()) {
					localObject2 = (Map.Entry) ((Iterator) localObject3).next();
					localObject4 = ((BOCreatePropertyDescriptor) ((Map.Entry) localObject2)
							.getKey()).getPropertyDescriptor().getName();
					localObject5 = ((Map.Entry) localObject2).getValue();
					localObject6 = ScheduleHelper
							.populateStrinValueFromDescriptor(
									((BOCreatePropertyDescriptor) ((Map.Entry) localObject2)
											.getKey()).getPropertyDescriptor(),
									localObject5);
					this.smExtendedAttributesObject.put(localObject4,
							localObject6);
				}
			}
		List localList2 = null;
		if (paramNewTaskDialog.taskPanel.taskRevMasterFormPanel
				.getAdditionalPropertiesPage() != null)
			localList2 = paramNewTaskDialog.taskPanel.taskRevMasterFormPanel
					.getAdditionalPropertiesPage().getCreateInputs();
		this.smExtendedAttributesRevObject = new HashMap();
		if (localList2 == null)
			return;
		for (int j = 0; j < localList2.size(); ++j) {
			localObject1 = null;
			localObject1 = (ICreateInstanceInput) localList2.get(j);
			if (localObject1 == null)
				continue;
			localObject2 = null;
			localObject2 = ((ICreateInstanceInput) localObject1)
					.getCreateInputs();
			if ((localObject2 == null) || (((Map) localObject2).size() <= 0))
				continue;
			localObject4 = ((Map) localObject2).entrySet().iterator();
			while (((Iterator) localObject4).hasNext()) {
				localObject3 = (Map.Entry) ((Iterator) localObject4).next();
				localObject5 = ((BOCreatePropertyDescriptor) ((Map.Entry) localObject3)
						.getKey()).getPropertyDescriptor().getName();
				localObject6 = ((Map.Entry) localObject3).getValue();
				StringValuePair localStringValuePair = ScheduleHelper
						.populateStrinValueFromDescriptor(
								((BOCreatePropertyDescriptor) ((Map.Entry) localObject3)
										.getKey()).getPropertyDescriptor(),
								localObject6);
				this.smExtendedAttributesRevObject.put(localObject5,
						localStringValuePair);
			}
		}
	}

	public TCComponent getNewTask() {
		return this.newTask;
	}

	public boolean getSuccessFlag() {
		return this.successFlag;
	}

	public void executeOperation()
    throws Exception
  {
    try
    {
      this.schAppPanel.setCursor(Cursor.getPredefinedCursor(3));
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          try
          {
            NewTaskOperation.this.refIDCont = NewTaskOperation.this.createTask();
          }
          catch (Exception localException)
          {
            if (localException instanceof SchedulingException)
              SchedulingExceptionHandler.handleScheduleException((SchedulingException)localException);
            localException.printStackTrace();
          }
        }
      });
      
      if ((this.refIDCont != null) && (!(this.refIDCont.getObjectIDs().isEmpty())))
      {
        Collection localCollection = this.refIDCont.getObjectIDs();
        Iterator localIterator;
        String str;
        TCComponent localTCComponent;
        Object localObject1;
        if (this.createPGStruct)
        {
          localIterator = localCollection.iterator();
          while (localIterator.hasNext())
          {
            str = (String)localIterator.next();
            localTCComponent = getSession().stringToComponent(str);
            if (localTCComponent == null)
              continue;
            localObject1 = localTCComponent.getTCProperty("task_type");
            if ((localObject1 == null) || (((TCProperty)localObject1).getIntValue() != 4))
              continue;
            ScheduleManagement.TaskDeliverableContainer[] arrayOfTaskDeliverableContainer = this.assignmentPanel.getTaskDeliverables();
            if ((arrayOfTaskDeliverableContainer != null) && (arrayOfTaskDeliverableContainer.length > 0))
            {
              for (int j = 0; j < arrayOfTaskDeliverableContainer.length; ++j)
                arrayOfTaskDeliverableContainer[j].scheduleTask = ((TCComponentScheduleTask)localTCComponent);
              CreateTaskDeliverableTemplatesRunner localObject2 = new CreateTaskDeliverableTemplatesRunner(arrayOfTaskDeliverableContainer);
              ((CreateTaskDeliverableTemplatesRunner)localObject2).execute();
            }
            Object localObject2 = new UpdateContainer();
            ((UpdateContainer)localObject2).setUpdatedObjectID(localTCComponent.getUid());
            ArrayList localArrayList1 = new ArrayList();
            localArrayList1.add(new IntValuePair(14, Integer.valueOf(2)));
            ((UpdateContainer)localObject2).setDefinedObjectsIntValuePairs(localArrayList1);
            ArrayList localArrayList2 = new ArrayList();
            if (this.wfTemplate != null)
              localArrayList2.add(new StringValuePair("workflow_template", this.wfTemplate.getUid()));
            if (this.wfTriggerType > 0)
              localArrayList2.add(new StringValuePair("workflow_trigger_type", this.wfTriggerType));
            if (this.privUser != null)
              localArrayList2.add(new StringValuePair("privileged_user", this.privUser.getUid()));
            if (this.processOwner != null)
              localArrayList2.add(new StringValuePair("fnd0workflow_owner", this.processOwner.getUid()));
            ((UpdateContainer)localObject2).setExtendObjectKeyValuePairs(localArrayList2);
            ArrayList localArrayList3 = new ArrayList();
            localArrayList3.add(localObject2);
            TaskModel localTaskModel = ModelFactory.singleton(localTCComponent.getSession()).getTaskModel();
            try
            {
              localTaskModel.updateTasks(localArrayList3, null, null, TaskHelper.getMasterScheduleUid());
            }
            catch (SchedulingException localSchedulingException)
            {
              SchedulingExceptionHandler.handleScheduleException(localSchedulingException);
            }
            this.assignmentPanel.saveAssignments(localTCComponent);
           // break label589:
            return;
          }
        }
        else
        {
          localIterator = localCollection.iterator();
          while (localIterator.hasNext())
          {
            str = (String)localIterator.next();
            this.assignmentPanel.saveAssignments(getSession().stringToComponent(str));
            localTCComponent = getSession().stringToComponent(str);
            if (localTCComponent == null)
              continue;
            TaskDeliverableContainer[]   arry_localObject1 = this.assignmentPanel.getTaskDeliverables();
            if ((arry_localObject1 == null) || (arry_localObject1.length <= 0))
              continue;
            for (int i = 0; i < arry_localObject1.length; ++i)
            	arry_localObject1[i].scheduleTask = ((TCComponentScheduleTask)localTCComponent);
            CreateTaskDeliverableTemplatesRunner localCreateTaskDeliverableTemplatesRunner = new CreateTaskDeliverableTemplatesRunner(arry_localObject1);
            localCreateTaskDeliverableTemplatesRunner.execute();
          }
        }
      }
      
      if (!(this.successFlag))
        label589: return;
      if (!(isAbortRequested()))
        if (this.openOnCreateFlag)
        {
          openNewTask();
          //break label723:
          this.schAppPanel.newTaskRefIdCont = this.refIDCont;
          this.schAppPanel.jobRedraw.schedule(50L);
          this.schAppPanel.setCursor(Cursor.getPredefinedCursor(0));
          return;
        }
    }
    finally
    {
      this.schAppPanel.newTaskRefIdCont = this.refIDCont;
      this.schAppPanel.jobRedraw.schedule(50L);
      this.schAppPanel.setCursor(Cursor.getPredefinedCursor(0));
    }
    label723: this.schAppPanel.newTaskRefIdCont = this.refIDCont;
    this.schAppPanel.jobRedraw.schedule(50L);
    this.schAppPanel.setCursor(Cursor.getPredefinedCursor(0));
  }

	public ReferenceIDContainer createTask() throws Exception {
		Registry localRegistry = Registry.getRegistry(this);
		TCComponent localTCComponent1 = null;
		TCComponentSchedule localTCComponentSchedule = null;
		Object localObject1 = null;
		TCComponent localTCComponent2 = null;
		localTCComponentSchedule = this.schAppPanel.getCurrentProject();
		localTCComponent1 = this.schAppPanel.getSelectedComponent();
		if (localTCComponent1 instanceof TCComponentScheduleTask) {
			localObject1 = TaskHelper.getParentForTask(localTCComponent1);
			localTCComponent2 = localTCComponent1;
		}
		if (localObject1 == null) {
			localObject1 = ScheduleHelper
					.getScheduleSummaryTask(localTCComponentSchedule);
			localTCComponent2 = null;
		}
		if (localTCComponentSchedule == null) {
			String msg_localObject2 = localRegistry.getString("failToCreate");
			if (this.desktop != null)
				MessageBox.post(this.desktop,msg_localObject2,
						localRegistry.getString("error.TITLE"), 1);
			this.successFlag = false;
			return null;
		}
		getSession().setStatus(
				localRegistry.getString("creatingNewTask") + " "
						+ this.taskName + "  ...");
		Object localObject2 = ModelFactory.singleton(getSession());
		TaskModel localTaskModel = ((ModelFactory) localObject2).getTaskModel();
		ArrayList localArrayList1 = new ArrayList();
		TaskCreateContainer localTaskCreateContainer = new TaskCreateContainer();
		localTaskCreateContainer.setAutoComplete(false);
		localTaskCreateContainer.setDuration(-1);
		localTaskCreateContainer.setWorkEstimate(this.workTask);
		localTaskCreateContainer.setWorkComplete(this.completeTask);
		localTaskCreateContainer.setFixedType(this.taskType);
		if (localObject1 != null)
			localTaskCreateContainer
					.setParentTaskId(((TCComponent) localObject1).getUid());
		else
			localTaskCreateContainer.setParentTaskId(null);
		if (localTCComponent2 != null)
			localTaskCreateContainer.setPrevTaskIDSameLevel(localTCComponent2
					.getUid());
		else
			localTaskCreateContainer.setPrevTaskIDSameLevel(null);
		localTaskCreateContainer.setTaskConstraint(0);
		localTaskCreateContainer.setTaskEnd(this.finishDate);
		localTaskCreateContainer.setTaskName(this.taskName);
		localTaskCreateContainer.setTaskStart(this.startDate);
		ArrayList localArrayList2 = new ArrayList();
		localArrayList2.add(new StringValuePair("priority", 3));
		localArrayList2.add(new StringValuePair("object_desc",
				this.taskDescription));
		System.out.println("lala completeTask:"+completeTask);
		if (this.completeTask > 0) {
			localTaskCreateContainer.setState(1);
			localArrayList2
					.add(new StringValuePair("fnd0state", "in_progress"));
		}
		localArrayList2.add(new StringValuePair("item_id", this.id));
		localArrayList2.add(new StringValuePair("item_sub_type",
				this.scheduleTaskType));
		
		System.out.println("boolean createPGStruct:"+createPGStruct);
		if (!(this.createPGStruct)) {
			if (this.wfTemplate != null)
				localArrayList2.add(new StringValuePair("workflow_template",
						this.wfTemplate.getUid()));
			if (this.wfTriggerType > 0)
				localArrayList2.add(new StringValuePair(
						"workflow_trigger_type", this.wfTriggerType));
			if (this.privUser != null)
				localArrayList2.add(new StringValuePair("privileged_user",
						this.privUser.getUid()));
			if (this.processOwner != null)
				localArrayList2.add(new StringValuePair("fnd0workflow_owner",
						this.processOwner.getUid()));
		}
		
		localTaskCreateContainer.setStringValuePair(localArrayList2);
		if (this.createPGStruct)
			localTaskCreateContainer.setTaskType(3);
		else
			localTaskCreateContainer.setTaskType(0);
		ArrayList localArrayList3 = new ArrayList();
		Collection localCollection;
		TypedStringValuePair localTypedStringValuePair;
		if (!(this.scheduleTaskType.equals("ScheduleTask"))) {
			if (this.tmExtendedAttributes != null) {
				localCollection = ScheduleHelper
						.parseAttributeValue(this.tmExtendedAttributes);
				localTypedStringValuePair = new TypedStringValuePair(
						"ScheduleTask", localCollection);
				localArrayList3.add(localTypedStringValuePair);
			}
			if (this.trmExtendedAttributes != null) {
				localCollection = ScheduleHelper
						.parseAttributeValue(this.trmExtendedAttributes);
				localTypedStringValuePair = new TypedStringValuePair(
						"ScheduleTaskRevision", localCollection);
				localArrayList3.add(localTypedStringValuePair);
			}
		}
		System.out.println("11 localArrayList3.size()-->"+localArrayList3.size());
		
		System.out.println("smExtendedAttributesObject-->"+smExtendedAttributesObject);
		if (this.smExtendedAttributesObject != null) {
			localCollection = ScheduleHelper
					.parseAttribute(this.smExtendedAttributesObject);
			
			Iterator localIterator = localCollection.iterator();
			while (localIterator.hasNext()) {
				StringValuePair localStringValuePair =(StringValuePair) localIterator.next();
				System.out.println("AttributesObject-->"+localStringValuePair.getKey()+"  value:"+localStringValuePair.getValue());
			}
			
			localTypedStringValuePair = new TypedStringValuePair(
					"ScheduleTaskType", localCollection);
			localArrayList3.add(localTypedStringValuePair);
		}
		
		System.out.println("smExtendedAttributesRevObject-->"+smExtendedAttributesRevObject);
		if (this.smExtendedAttributesRevObject != null) {
			localCollection = ScheduleHelper
					.parseAttribute(this.smExtendedAttributesRevObject);
			Iterator localIterator = localCollection.iterator();
			while (localIterator.hasNext()) {
				StringValuePair localStringValuePair =(StringValuePair) localIterator.next();
				System.out.println("AttributesRev-->"+localStringValuePair.getKey()+"  value:"+localStringValuePair.getValue());
			}
			
			localTypedStringValuePair = new TypedStringValuePair(
					"ScheduleTaskRevisionType", localCollection);
			localArrayList3.add(localTypedStringValuePair);
		}
		
		System.out.println("22 localArrayList3.size()-->"+localArrayList3.size());
		if (localArrayList3.size() > 0)
			localTaskCreateContainer.setTypedStringValPair(localArrayList3);
		localArrayList1.add(localTaskCreateContainer);
		return ((ReferenceIDContainer) (ReferenceIDContainer) localTaskModel
				.createTasks(localArrayList1, localTCComponentSchedule.getUid()));
	}

	public void pasteNewTask() throws Exception {
		TCComponent[] arrayOfTCComponent = { this.newTask };
		Registry localRegistry = Registry.getRegistry(this);
		PasteCommand localPasteCommand = (PasteCommand) localRegistry
				.newInstanceForEx("pasteCommand", new Object[] {
						arrayOfTCComponent, this.pasteTargets, this.desktop });
		localPasteCommand.setFailBackFlag(true);
		localPasteCommand.addPropertyChangeListener(this);
		localPasteCommand.executeModal();
		MessageBox.post("Pasted Task", "Title", 2);
	}

	public void openNewTask() throws Exception {
		OpenCommand localOpenCommand = (OpenCommand) Registry.getRegistry(this)
				.newInstanceForEx("openCommand",
						new Object[] { this.desktop, this.newTask });
		localOpenCommand.executeModeless();
	}
}