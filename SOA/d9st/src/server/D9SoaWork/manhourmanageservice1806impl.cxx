/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2012
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@
*/

#include <mhfuns.hxx>

std::string ManHourManageServiceImpl::clearManHoursOP ( const std::string& theUser, const std::string& theAction, const std::string& thePara )
{
	 // TODO implement operation
		if (!theUser.empty() && !theAction.empty() && !thePara.empty())
		   {
		      if (!tc_strcmp (theAction.c_str(), "Delete_CostInfo"))
		      {
		         ITKCALL (rmCostInfo ((const char *)theUser.c_str(), (const char *)thePara.c_str()));
		      }
		      else
		      {
		         ITKCALL(rmManHourInfo (theUser.c_str(), theAction.c_str(), thePara.c_str()));
		      }
		   }

		   std::string res = "completed";
		   return res;
}


ManHourManageServiceImpl::ManHourInfo ManHourManageServiceImpl::getManHourInfoOP ( const std::string& theUserName, const std::string& theYear, const std::string& theMonth )
{
    // TODO implement operation
	ManHourManageServiceImpl::ManHourInfo theManHourInfo;

		   createManHourInfo (theUserName.c_str(), theYear.empty() ? NULL:theYear.c_str(),
		                   theMonth.empty() ? NULL:theMonth.c_str(), theManHourInfo);

		   // test only
		   date_t myToday;
		   GUTIL_current_date (&myToday);
		   //TC_write_syslog("current date: %d    %d    %d\n", myToday.year, (myToday.month + 1), myToday.day);

		   return theManHourInfo;
}


ManHourManageServiceImpl::ManHourEntrySet ManHourManageServiceImpl::loadOP ( const ManHourEntry& manHourFilter )
{
    // TODO implement operation
	 ManHourManageServiceImpl::ManHourEntrySet mheSet;

		    doLoad(manHourFilter, mheSet);

		    return mheSet;
}


std::string ManHourManageServiceImpl::mheTest ( const std::string& theUser )
{
    // TODO implement operation
	//int ifail = ITK_ok;

		    if (!theUser.empty())
		    {
		//       ITKCALL(ifail = doTest (theUser.c_str()));
		    }

		    std::string res = "completed";

		    return res;
}


ManHourManageServiceImpl::ManHourEntrySet ManHourManageServiceImpl::reviseOP ( const std::string& username, const std::string& year, const std::string& month, const std::vector< ManHourEntry >& manHours )
{
    // TODO implement operation
	ManHourManageServiceImpl::ManHourEntrySet mheSet;

		    doRevise(username.c_str(), year.c_str(), month.c_str(), manHours, mheSet);

		    return mheSet;
}


ManHourManageServiceImpl::ManHourEntrySet ManHourManageServiceImpl::saveOP ( const std::string& username, const std::string& year, const std::string& month, const std::vector< ManHourEntry >& manHours, const std::vector< ManHourProgram >& programs )
{
    // TODO implement operation
	 ManHourManageServiceImpl::ManHourEntrySet mheSet;

		    doSave(username.c_str(), year.c_str(), month.c_str(), manHours, programs, mheSet);

		    return mheSet;
}



