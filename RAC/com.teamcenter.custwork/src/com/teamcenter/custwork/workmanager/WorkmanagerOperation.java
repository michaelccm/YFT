package com.teamcenter.custwork.workmanager;

import java.util.Calendar;



import com.d9.services.rac.work.ManHourManageServiceService;
import com.d9.services.rac.work._2018_06.ManHourManageService;
import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourEntry;
import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourEntrySet;
import com.d9.services.rac.work._2018_06.ManHourManageService.ManHourInfo;


import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.soa.client.model.ServiceData;

public class WorkmanagerOperation {
 
    public static ManHourEntrySet save(TCSession session, ManHourInfo mh_info, ManHourEntry[] mhe_set)
       throws ServiceException
    {
    	ManHourManageServiceService manHourService = ManHourManageServiceService.getService(session);

       ManHourEntrySet mheSet = manHourService.saveOP(mh_info.myUserName, mh_info.myYear, mh_info.myMonth, mhe_set, mh_info.theProgramSet);

       return mheSet;
    }

    
    public static ManHourEntrySet revise(TCSession session, ManHourInfo mh_info, ManHourEntry[] mhe_set)
       throws ServiceException
    {
    	
    	ManHourManageServiceService manHourService = ManHourManageServiceService.getService(session);

       ManHourEntrySet mheSet = manHourService.reviseOP(mh_info.myUserName, mh_info.myYear, mh_info.myMonth, mhe_set);

       return mheSet;
    }


   public static ManHourEntry[] load(TCSession session, ManHourInfo mhInfo) 
        throws ServiceException
   {
	   ManHourManageService.ManHourEntry filter = new ManHourManageService.ManHourEntry();
	   ManHourManageServiceService manHourService = ManHourManageServiceService.getService(session);
 		filter.myUserName = mhInfo.myUserName;
 		filter.myYear = mhInfo.myYear;
		filter.myMonth = mhInfo.myMonth;

	
 		ManHourEntrySet mheSet = manHourService.loadOP(filter);
 		
 		return mheSet.mheSet;
     }


    public static ManHourInfo loadManHourInfo (TCSession session, String year, String month)
      throws ServiceException
    {    
    	String userName = session.getUserName();
    	
    	ManHourManageServiceService manHourService = ManHourManageServiceService.getService(session);
    	System.out.println("userName-->"+userName+"  year:"+year+"  month:"+month);
    	
    	String mheTest = manHourService.mheTest("test");
    	System.out.println("result-->"+mheTest);
    	
    	ManHourInfo mhInfo = manHourService.getManHourInfoOP(userName, year, month);
    	
    	
        return mhInfo;
    }
    
    
    public static void delCostInfo (TCSession session, String action, String para )
    throws ServiceException
    {    
    	
    	String userName = session.getUserName();
    	
    	ManHourManageServiceService manHourService = ManHourManageServiceService.getService(session);
    	String res = manHourService.clearManHoursOP(userName, action, para);
    	
    }

    public static void Test (TCSession session)
    throws ServiceException
    {    
    	String userName = session.getUserName();
    	
    	ManHourManageServiceService manHourService = ManHourManageServiceService.getService(session);
        manHourService.mheTest (userName);
        
    }
 }

