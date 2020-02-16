/**  
 * Project Name:com.yfjc.calendar  
 * File Name:MyDisciplineTreeContentProvider.java  
 * Package Name:com.teamcenter.rac.schedule.commands.assigntotask  
 * Date:2019年11月18日下午10:50:42  
 * Copyright (c) 2019, Real All Rights Reserved.  
 *  
*/  
  
package com.teamcenter.rac.schedule.commands.assigntotask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.teamcenter.rac.kernel.TCComponentDiscipline;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.common.organization.DisciplineRootModel;
import com.teamcenter.rac.schedule.common.organization.DisciplineTreeContentProvider;
import com.teamcenter.rac.schedule.common.organization.TaskActorInterface;
/**  
 * ClassName:MyDisciplineTreeContentProvider <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2019年11月18日 下午10:50:42 <br/>  
 * @author   moni  
 * @version    
 * @since    JDK 1.7  
 * @see        
 */
public class MyDisciplineTreeContentProvider extends DisciplineTreeContentProvider {
	protected List<TCComponentUser> member_users=new ArrayList<TCComponentUser>();
	protected List<TCComponentDiscipline> member_discilines=new ArrayList<TCComponentDiscipline>();
	public MyDisciplineTreeContentProvider(TCSession arg0) {
		super(arg0);
		//System.out.println("MyDisciplineTreeContentProvider arg0");
	}
	public MyDisciplineTreeContentProvider(TCSession arg0,List<TCComponentUser> member_users,List<TCComponentDiscipline> member_discilines){
		super(arg0);
		//System.out.println("MyDisciplineTreeContentProvider 2 parameters");
		this.member_users = member_users;
		this.member_discilines = member_discilines;
	}
	public Object[] getChildren(Object paramObject)
	{
		//System.out.println("getChildren");
	    TaskActorInterface localTaskActorInterface = null;
	    Set localObject = new HashSet();
	    Set localObject2 = new HashSet();
	    if ((paramObject instanceof TaskActorInterface))
	    {
	      //System.out.println("TaskActorInterface111 and memeber sizei "+member_users.size());
	      localTaskActorInterface = (TaskActorInterface)paramObject;
	      if(localTaskActorInterface.isDiscipline())//学科
	      {
	    	  //System.out.println("学科");
	    	  localObject = localTaskActorInterface.getChildrens();//人员
   	    	  Iterator itr = localObject.iterator();
   	    	  while(itr.hasNext()){
   		    	  TaskActorInterface interf = (TaskActorInterface) itr.next();
   		    	  if(interf.isUser()){
   		    		//System.out.println("学科下可配置人："+interf.find().getUid());
   		    		  for (int j = 0; j < member_users.size(); j++) {
   		    		    System.out.println("ScheduleMember可配置人："+member_users.get(j).getUid());
						if(interf.find().equals(member_users.get(j))){
							System.out.println("学科下可配置人匹配");
							localObject2.add(interf);
						}
   		    		  }
   		    	  }
   		      }
	      }
	      else if(paramObject instanceof DisciplineRootModel){
	    	System.out.println("DisciplineRootModel and get input size is "+member_discilines.size());//根任务，允许选择所有的学科
	    	localObject = localTaskActorInterface.getChildrens();//学科
	    	if(localObject != null && localObject.size() > 0){
	    		System.out.println("get the children over");
	    	}
	    	Iterator itr = localObject.iterator();
	    	while(itr.hasNext()){
	    	  TaskActorInterface interf = (TaskActorInterface) itr.next();
	    	  if(interf.isDiscipline()){
	    		  System.out.println("interf isDiscipline");
	    		  for (int j = 0; j < member_discilines.size(); j++) {
					if(interf.find().equals(member_discilines.get(j))){
						System.out.println("根任务下可配置的学科");
						localObject2.add(interf);
					}
	    		  }
	    	  }
	    	}
	    	System.out.println("localObject2 size is "+localObject2.size());
		  }
	    }
	    return localObject2.toArray();
	}

	public Object getParent(Object paramObject)
	{
		//System.out.println("getParent paramObject");
	    TaskActorInterface localTaskActorInterface1 = null;
	    TaskActorInterface localTaskActorInterface2 = null;
	    if ((paramObject instanceof TaskActorInterface))
	    {
	      localTaskActorInterface1 = (TaskActorInterface)paramObject;
	      localTaskActorInterface2 = localTaskActorInterface1.getParent();
	    }
	    return localTaskActorInterface2;
	}

	public boolean hasChildren(Object paramObject)
	{
		//System.out.println("hasChildren paramObject");
	    boolean bool = false;
	    if ((paramObject instanceof TaskActorInterface))
	    {
	      TaskActorInterface localTaskActorInterface = (TaskActorInterface)paramObject;
	      if (localTaskActorInterface.isChildrenFetched())
	      {
	        bool = localTaskActorInterface.getChildrens().size() > 0;
	      }
	      else
	      {
	        Object[] arrayOfObject = getChildren(paramObject);
	        return arrayOfObject != null;
	      }
	    }
	    return bool;
	}
}
  
