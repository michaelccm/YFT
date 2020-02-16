//package com.yfjcebp.change.user.info;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//
//import com.teamcenter.project.cache.CacheManager;
//import com.teamcenter.project.cache.ScheduleCache;
//import com.teamcenter.project.common.dataModel.MemoryTransaction;
//import com.teamcenter.project.common.dataModel.ScheduleTask;
//import com.teamcenter.project.scheduling.ModelFactory;
//import com.teamcenter.project.scheduling.ProxyTaskCreateContainer;
//import com.teamcenter.project.scheduling.ReferenceIDContainer;
//import com.teamcenter.project.scheduling.SchedulingException;
//import com.teamcenter.project.scheduling.StringValuePair;
//import com.teamcenter.project.scheduling.TaskCreateContainer;
//import com.teamcenter.project.scheduling.TaskModel;
//import com.teamcenter.project.scheduling.TypedStringValuePair;
//import com.teamcenter.project.server.RACInterface.RACInterfaceFactory;
//import com.teamcenter.project.server.dataInterface.LoadScheduleOptions;
//import com.teamcenter.project.server.dataInterface.ScheduleLoadResponse;
//import com.teamcenter.rac.aif.AbstractAIFUIApplication;
//import com.teamcenter.rac.aifrcp.AIFUtility;
//import com.teamcenter.rac.kernel.TCComponent;
//import com.teamcenter.rac.kernel.TCComponentScheduleTask;
//import com.teamcenter.rac.kernel.TCException;
//import com.teamcenter.rac.kernel.TCSession;
//import com.teamcenter.rac.kernel.TCUserService;
//import com.teamcenter.rac.schedule.scheduler.componentutils.ScheduleHelper;
//
//public class CreateScheduleTask {
//
//	public static TCComponentScheduleTask createScheduleTask(
//			TCUserService userservice,TCComponent parent, String name,Calendar startDate,Calendar finishDate) throws SchedulingException,
//			TCException {
//
//		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
//
//		TCSession session = (TCSession) app.getSession();
//		TCComponentScheduleTask scheduleTask = null;
//
//		TCComponent schedule = (TCComponent) app.getTargetComponent();
//		if (parent == null)
//			parent = ScheduleHelper.getScheduleSummaryTask(schedule);
//
//		// 获得时间表任务模型
//		ModelFactory modelfactory = ModelFactory.singleton(session);
//		TaskModel taskmodel = modelfactory.getTaskModel();
//		List<TaskCreateContainer> arraylist = new ArrayList<TaskCreateContainer>();
//		TaskCreateContainer taskcreatecontainer = new TaskCreateContainer();
//		taskcreatecontainer.setAutoComplete(false);// 设置自动完成 auto_complete
//		if (parent instanceof TCComponentScheduleTask) {
//			taskcreatecontainer.setParentTaskId(parent.getUid());// 设置新时间表任务的父
//			taskcreatecontainer.setPrevTaskIDSameLevel(parent.getUid());
//		} else {
//			taskcreatecontainer.setParentTaskId(null);
//			taskcreatecontainer.setPrevTaskIDSameLevel(null);
//		}
//
//		
//
//		taskcreatecontainer.setDuration(-1);
//		taskcreatecontainer.setTaskName(name);// object_name
//		taskcreatecontainer.setTaskStart(startDate);//必须设置
//		taskcreatecontainer.setTaskEnd(finishDate);//必须设置
//	
//	
//		taskcreatecontainer.setTaskType(0);
//		ArrayList<StringValuePair> keyVals = new ArrayList<StringValuePair>();
//		keyVals.add(new StringValuePair("priority", 3));//priority
//		keyVals.add(new StringValuePair("item_sub_type", "ScheduleTask"));// ScheduleTask
//		taskcreatecontainer.setStringValuePair(keyVals);
//		arraylist.add(taskcreatecontainer);
//	
//
//		// 必须有
//		LoadScheduleOptions loadscheduleoptions = new LoadScheduleOptions();
//		HashMap hashmap = new HashMap();
//		hashmap.put("SM_START_DEFERRED_MODE", true);
//		loadscheduleoptions.setBooleanOptions(hashmap);
//		HashMap options = new HashMap();
//		options.put("SM_Structure_Client_Context", Integer.valueOf(0));
//		options.put("SM_Structure_Load_Context", Integer.valueOf(0));
//		options.put("SM_Structure_Partial_Context", Integer.valueOf(0));
//		loadscheduleoptions.setIntOptions(options);
//		RACInterfaceFactory.singleton().getRACInterface(session, schedule)
//				.loadSchedule(loadscheduleoptions);
//
//		
//		ReferenceIDContainer idcontainer = taskmodel.createTasks(arraylist,
//				schedule.getUid());
//		
//		Collection<String> localCollection = idcontainer.getObjectIDs();
//		Iterator<String> localIterator = localCollection.iterator();
//		if (localIterator.hasNext()) {
//			TCComponent localTCComponent = (TCComponent) session
//					.stringToComponent((String) localIterator.next());
//			scheduleTask = (TCComponentScheduleTask) localTCComponent;
//		}
//		return scheduleTask;
//	}
//	
//	
//	
//	
//	
//	
//	
//	public static TCComponentScheduleTask createProxyTask(
//			TCUserService userservice,TCComponent parent, String name) throws SchedulingException,
//			TCException {
//
//		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
//
//		TCSession session = (TCSession) app.getSession();
//		
//		
//		System.out.println("session.hasBypass() before---"+session.hasBypass());
//		session.enableBypass(true);
//		System.out.println("session.hasBypass() after---"+session.hasBypass());
//		
//		TCComponentScheduleTask scheduleTask = null;
//
//		TCComponent schedule = (TCComponent) app.getTargetComponent();
//		if (parent == null)
//			parent = ScheduleHelper.getScheduleSummaryTask(schedule);
//
//		// 获得时间表任务模型
//		ModelFactory modelfactory = ModelFactory.singleton(session);
//		TaskModel taskmodel = modelfactory.getTaskModel();
//		Collection arraylist = new ArrayList();
//		ProxyTaskCreateContainer taskcreatecontainer = new ProxyTaskCreateContainer();
//		//taskcreatecontainer.setAutoComplete(false);// 设置自动完成 auto_complete
////		if (parent instanceof TCComponentScheduleTask) {
////			taskcreatecontainer.setParentTaskId(parent.getUid());// 设置新时间表任务的父
////			taskcreatecontainer.setPrevTaskIDSameLevel(parent.getUid());
////		} else {
////			taskcreatecontainer.setParentTaskId(null);
////			taskcreatecontainer.setPrevTaskIDSameLevel(null);
////		}
//		
//		 Date date = new Date();
//
//		 GregorianCalendar finishDate = new GregorianCalendar();
//		 finishDate.setTime(date);
//
//		 taskcreatecontainer.setRefTaskID(parent.getUid());
//		// taskcreatecontainer.set
//		taskcreatecontainer.setName(name);// object_name
//		 taskcreatecontainer.setStartDate(finishDate);
//		taskcreatecontainer.setFinishDate(finishDate);//必须设置
//
//
//		//ArrayList<StringValuePair> keyVals = new ArrayList<StringValuePair>();
//		// keyVals.add(new StringValuePair("priority", 3));//priority
//		//keyVals.add(new StringValuePair("item_sub_type", "ScheduleTask"));// ScheduleTask
//		//taskcreatecontainer.
//		arraylist.add(taskcreatecontainer);
//
//	
//		// MemoryTransaction memoryTransaction = new MemoryTransaction();
//		// ScheduleCache scheduleCache =
//		// CacheManager.singleton().getScheduleCache(schedule.getUid());
//		// scheduleCache.getTaskRelationshipCache().getTask(taskcreatecontainer.getParentTaskId(),
//		// memoryTransaction);
//
//		// 必须有
//		LoadScheduleOptions loadscheduleoptions = new LoadScheduleOptions();
//		HashMap hashmap = new HashMap();
//		hashmap.put("SM_START_DEFERRED_MODE", true);
//		loadscheduleoptions.setBooleanOptions(hashmap);
//		HashMap options = new HashMap();
//		options.put("SM_Structure_Client_Context", Integer.valueOf(0));
//		options.put("SM_Structure_Load_Context", Integer.valueOf(0));
//		options.put("SM_Structure_Partial_Context", Integer.valueOf(0));
//		loadscheduleoptions.setIntOptions(options);
//		RACInterfaceFactory.singleton().getRACInterface(session, schedule)
//				.loadSchedule(loadscheduleoptions);
//
//		
//		ReferenceIDContainer idcontainer = taskmodel.createProxyTasks(arraylist,
//				schedule.getUid());
//		
//		Collection<String> localCollection = idcontainer.getObjectIDs();
//		Iterator<String> localIterator = localCollection.iterator();
//		if (localIterator.hasNext()) {
//			TCComponent localTCComponent = (TCComponent) session
//					.stringToComponent((String) localIterator.next());
//			scheduleTask = (TCComponentScheduleTask) localTCComponent;
//		}
//		System.out.println("close bypass before---"+session.hasBypass());
//		session.enableBypass(false);
//		System.out.println("close bypass after---"+session.hasBypass());
//		return scheduleTask;
//	}
//	
//	
//	public static TCComponentScheduleTask createScheduleTask2(TCSession session, String name, String description, int taskType, 
//			int workTaskInt, int completeTaskInt, Calendar startDate, Calendar finishDate, TCComponent parent, TCComponent project) throws TCException, SchedulingException {
//
//			ModelFactory modelfactory = ModelFactory.singleton(session);
//			TaskModel taskmodel = modelfactory.getTaskModel();
//			TCComponentScheduleTask scheduleTask = null;
//			List<TaskCreateContainer> arraylist = new ArrayList<TaskCreateContainer>();
//			TaskCreateContainer taskcreatecontainer = new TaskCreateContainer();
//			
//			taskcreatecontainer.setAutoComplete(false);
//			taskcreatecontainer.setDuration(-1);
//			taskcreatecontainer.setWorkEstimate(workTaskInt);
//			taskcreatecontainer.setWorkComplete(completeTaskInt);
//			taskcreatecontainer.setFixedType(0);
//			
//			if (parent instanceof TCComponentScheduleTask) {
//				taskcreatecontainer.setParentTaskId(parent.getUid());
//				taskcreatecontainer.setPrevTaskIDSameLevel(parent.getUid());
//			} else {
//				taskcreatecontainer.setParentTaskId(null);
//				taskcreatecontainer.setPrevTaskIDSameLevel(null);
//			}
//			
//			taskcreatecontainer.setTaskConstraint(0);
//			taskcreatecontainer.setTaskEnd(finishDate);
//			taskcreatecontainer.setTaskName(name);
//			taskcreatecontainer.setTaskStart(startDate);
//			taskcreatecontainer.setTaskType(0);
//			ArrayList<StringValuePair> keyVals = new ArrayList<StringValuePair>();
//			keyVals.add(new StringValuePair("priority", 3));
//			keyVals.add(new StringValuePair("item_sub_type", "ScheduleTask"));//ScheduleTask
//			taskcreatecontainer.setStringValuePair(keyVals);
//			
//			arraylist.add(taskcreatecontainer);
//			MemoryTransaction memoryTransaction = new MemoryTransaction();
//			ScheduleCache  scheduleCache = CacheManager.singleton().getScheduleCache(project.getUid());
//			ScheduleTask scheduletask = (ScheduleTask) scheduleCache.getTaskRelationshipCache().getTask(taskcreatecontainer.getParentTaskId(), memoryTransaction);
//			/*if(scheduletask==null){
//				Long l=OidBroker.singleton().getLong(project.getUid());
//				scheduletask=new ScheduleTaskImpl(l,l,l,project.getUid(),startDate,finishDate,1,1,1,1,1,true,startDate,finishDate,0,0.0,0,0,"");
//				scheduleCache.getTaskRelationshipCache().addTask(scheduletask, memoryTransaction);
//			}*/
//			LoadScheduleOptions loadscheduleoptions = new LoadScheduleOptions();
//            HashMap hashmap = new HashMap();
//            hashmap.put("SM_START_DEFERRED_MODE", true);
//            loadscheduleoptions.setBooleanOptions(hashmap);
//            HashMap options=new HashMap();
//            options.put("SM_Structure_Client_Context",Integer.valueOf(0));
//            options.put("SM_Structure_Load_Context",Integer.valueOf(0));
//            options.put("SM_Structure_Partial_Context",Integer.valueOf(0));
//            loadscheduleoptions.setIntOptions(options);
//            /*ScheduleViewApplicationPanel.getPartialContexOptiontFromTCPreference(session);
//            ScheduleViewApplicationPanel.setLoadContextOptionToDefault();*/
//            ScheduleLoadResponse rsb = RACInterfaceFactory.singleton().getRACInterface(session, project).loadSchedule(loadscheduleoptions);
//            ReferenceIDContainer idcontainer = taskmodel.createTasks(arraylist, project.getUid());
//            Collection<String> localCollection = idcontainer.getObjectIDs();
//    		Iterator<String> localIterator = localCollection.iterator();
//    		if (localIterator.hasNext()) {
//    			TCComponent localTCComponent = (TCComponent) session
//    					.stringToComponent((String) localIterator.next());
//    			scheduleTask = (TCComponentScheduleTask) localTCComponent;
//    		}
//    		return scheduleTask;
//	
//	}
//	
//
//
//	// 调用userservice 1open 0close
//	private static void openOrClosePass(TCUserService userservice, int flag)
//			throws TCException {
//		Object[] parameters = new Object[] { flag };
//		userservice.call("open_or_close_pass", parameters);
//
//	}
//
//}
