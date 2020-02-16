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


#ifdef __cplusplus
extern "C" {
#endif



/*
 * Retrieve man hours.
 */
int RetrieveManHour(tag_t manHourTag, ManHourManageService::ManHourEntry &manHour)
{
   TC_write_syslog ("RetrieveManHour start\n");
   int ifail = ITK_ok;
   char *strValue = NULL;

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6UserName", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myUserName = strValue; 
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6Year", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myYear = strValue; 
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6Month", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myMonth = strValue; 
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6DayOfWeek", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myDayOfWeek = strValue; 
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6Day", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myDay = strValue; 
      SAFE_SM_FREE(strValue);

   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6DayOfWeek", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myDayOfWeek = strValue; 
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6Holiday", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myHoliday = strValue; 
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6WorkRequired", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.workRequired = strValue; 
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6PrjName", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myPrjName = strValue;
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6PrjId", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myPrjId = strValue;
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6TaskType", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myTaskType = strValue;
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6TaskTypeDval", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myTaskTypeDval = strValue;
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6WorkingHours", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.workingHours = strValue; 
      SAFE_SM_FREE(strValue);
   }

   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6BillType", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.billType = strValue; 
      SAFE_SM_FREE(strValue);
   }

/*
   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6RefBR", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myRefBR = strValue;
      SAFE_SM_FREE(strValue);
   }
*/


   ITKCALL( ifail = AOM_ask_value_string( manHourTag, "d6RefTE", &strValue ) );
   if( !IS_EMPTY(strValue) )
   {
      manHour.myRefTE = strValue; 

      tag_t tseTag = null_tag;

      ITKCALL( ifail = POM_string_to_tag( strValue, &tseTag ) );

      logical tseExist = false;

      ITKCALL (ifail = AOM_refresh (tseTag, false));
      ITKCALL (ifail = POM_instance_exists (tseTag, &tseExist));
      if (tseExist)
      {
         string tseStatus = "";

         ITKCALL ( ifail = checkTSEStatus(tseTag, tseStatus));
         if (tseStatus.size () > 0 && !tc_strcmp (tseStatus.c_str(), "Released"))
         {
            manHour.tseStatus = "Released";
         }
         else
         {
            manHour.tseStatus = "Failed"; 
         }
      }
      else
      {
         manHour.tseStatus = "Failed";  
         manHour.myRefTE = "Null";
      }
      SAFE_SM_FREE(strValue);
   }
   else
   {
      manHour.myRefTE = "Null";
      manHour.tseStatus = "Failed";
   }

   //manHour.myRefMHE
   char *mheTagStr = NULL;
   ITKCALL (ifail = AOM_tag_to_string( manHourTag, &mheTagStr));
   manHour.myRefMHE = mheTagStr;
   SAFE_SM_FREE(mheTagStr);

   // row & col
   manHour.row = -1;
   manHour.col = -1;

   TC_write_syslog ("RetrieveManHour end\n");
   return ifail;
}


/* 
 * Find created man hours by year and month.
 */
int doLoad(const ManHourManageService::ManHourEntry &manHourFilter, ManHourManageServiceImpl::ManHourEntrySet &mhe_set)
{
   TC_write_syslog ("doLoad start \n");
   int ifail = ITK_ok;
   int qryWsoCount = 0;
   tag_t *qryWsoTags = NULL;

   ITKCALL(ifail = GCOMM_qry( QRY_MANHOUR_WITH_YEAR_MONTH, &qryWsoCount, &qryWsoTags, 3,
                   manHourFilter.myUserName.c_str(),
                   manHourFilter.myYear.c_str(),
                   manHourFilter.myMonth.c_str() ));

   TC_write_syslog ("doLoad:: manHourFilter.myUserName-->%s  manHourFilter.myYear-->%s  manHourFilter.myMonth-->%s\n",manHourFilter.myUserName.c_str(),manHourFilter.myYear.c_str(),manHourFilter.myMonth.c_str());

   TC_write_syslog ("doLoad:: qryWsoCount-->%d  \n",qryWsoCount);

   /*
    * return all man hours.
    */
   if (qryWsoCount > 0)
   {
      for (int i=0; i<qryWsoCount; i++)
      {
         ManHourManageService::ManHourEntry manHourEntry;
         ITKCALL( ifail = RetrieveManHour(qryWsoTags[i], manHourEntry) );
         mhe_set.mheSet.push_back(manHourEntry);
      }
   }
   SAFE_SM_FREE(qryWsoTags);

   TC_write_syslog ("doLoad end \n");
   return ifail;
}


#ifdef __cplusplus
}
#endif

