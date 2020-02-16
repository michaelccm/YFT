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





/*
 * getScheduleTaskTagUid
 */
std::string getScheduleTaskTagUid(std::map <const std::string, ManHourManageService::ManHourProgram> &prg_map,
		std::string prjid, std::string prj_name, std::string stk_type){

   //TC_write_syslog("getScheduleTaskTagUid start\n");
   char keyBuf[256]={'\0'};
   std::string stkTagUid;
   std::string keyStr;

   sprintf (keyBuf, "%s-%s:%s", prjid.c_str(), prj_name.c_str(), stk_type.c_str());

   keyStr.assign(keyBuf);

   std::map<const std::string, ManHourManageService::ManHourProgram>::iterator iter;

   iter = prg_map.find (keyStr);
   if (iter != prg_map.end())
   {
      ManHourManageService::ManHourProgram mhPrg = iter -> second;
      stkTagUid.assign (mhPrg.stkTag);
   }

  // TC_write_syslog("getScheduleTaskTagUid end\n");
   return stkTagUid;
}

#ifdef __cplusplus
extern "C" {
#endif



/*
 * CreateMHETag
 */
int createMHETag (const ManHourManageService::ManHourEntry &manHour, tag_t *mhe_tag)
{
   int ifail = ITK_ok;
   tag_t mheType = null_tag;
   tag_t mheCreateInput = null_tag;
   tag_t mheTag = null_tag;
   char* dispValues[1];

   dispValues[0] = (char*) "Man Working Hour";

   ITKCALL( ifail = TCTYPE_find_type("D6ManHourEntry", "D6ManHourEntry", &mheType) );

   ITKCALL( ifail = TCTYPE_construct_create_input ( mheType, &mheCreateInput ) );
   ITKCALL( ifail = TCTYPE_set_create_display_value( mheCreateInput, (char*) "object_name", 1, (const char**)(dispValues) ) );
   ITKCALL( ifail = TCTYPE_create_object ( mheCreateInput, &mheTag ) );
   ITKCALL( ifail = AOM_save ( mheTag ) );

   //Setting values
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6UserName", manHour.myUserName.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6Year", manHour.myYear.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6Month", manHour.myMonth.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6Day", manHour.myDay.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6DayOfWeek", manHour.myDayOfWeek.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6Holiday", manHour.myHoliday.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6WorkRequired", manHour.workRequired.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6PrjName", manHour.myPrjName.c_str() ) );

   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6PrjId", manHour.myPrjId.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6TaskType", manHour.myTaskType.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6TaskTypeDval", manHour.myTaskTypeDval.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6WorkingHours", manHour.workingHours.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTag, "d6BillType", manHour.billType.c_str() ) );
   ITKCALL( ifail = AOM_save ( mheTag ) );
   ITKCALL( ifail = AOM_unlock( mheTag ) );

   TC_write_syslog ("createMHETag finished mheTag:%d\n",mheTag);

   *mhe_tag = mheTag;

   return ifail;
}



/*
 * createTSETag
 */
int createTSETag (const ManHourManageService::ManHourEntry &manHour, const string &stk_tag_uid, tag_t *tse_tag)
{
   int ifail = ITK_ok;
   tag_t tseType = null_tag;
   tag_t tseCreateInput = null_tag;
   tag_t tseTag = null_tag;

   char* dispValues[1];
   char buf[128] = {0};

   char* dispValuesDate[1];

   if (stk_tag_uid.size()==0)
   {
      TC_write_syslog ("ScheduleTask Tag is null\n");
      return ifail;
   } 


   ITKCALL( ifail = TCTYPE_find_type("TimeSheetEntry", "TimeSheetEntry", &tseType) );
   if (tseType == null_tag ) 
   {
      TC_write_syslog ("TimeSheetEntry Type tag is null\n");
      return ifail;
   }

   ITKCALL( ifail = TCTYPE_construct_create_input ( tseType, &tseCreateInput ) );
   if (tseCreateInput == null_tag)
   {
      TC_write_syslog ("TimeSheetEntry Create Input tag is null\n");
      return ifail;
   }

   sprintf(buf, "%s-%s_%s_%s:TSE", manHour.myPrjId.c_str(), manHour.myPrjName.c_str(), 
   manHour.myTaskTypeDval.c_str(), manHour.myUserName.c_str());

   dispValues[0] = buf;
   ITKCALL( ifail = TCTYPE_set_create_display_value( tseCreateInput, (char*) "object_name", 1, (const  char**)(dispValues) ) );
    printf ("ww object_name is %s\n", dispValues[0]);

   //modify by wuwei
   int iYear = atoi (manHour.myYear.c_str());

   int iMonth = 0;
   char sMonth[10];
   tc_strcpy (sMonth, manHour.myMonth.c_str());
   if (tc_strlen (sMonth) ==2)
   {
      sMonth[2]='\0';
      if (sMonth[0] == '0')
        iMonth = atoi (&sMonth[1]);
      else
        iMonth = atoi (&sMonth[0]);
   }
   else
   {
        iMonth = atoi (&sMonth[0]);
   }

   int iDay = 1;
   char sDay[10];
   tc_strcpy (sDay, manHour.myDay.c_str());
   if (tc_strlen (sDay) ==2)
   {
      sDay[2]='\0';
      if (sDay[0] == '0')
        iDay = atoi (&sDay[1]);
      else
        iDay = atoi (&sDay[0]);
   }
   else
   {
        iDay = atoi (&sDay[0]);
   }

   date_t theDate = NULLDATE;
   theDate.year   = (short)iYear;
   theDate.month  =(byte) iMonth-1;
   theDate.day    =(byte) iDay;

   char *format_str=NULL,*date_str=NULL;
   ITKCALL( DATE_get_internal_date_string_format(&format_str));
   	DATE_date_to_string(theDate,format_str,&date_str);

    TC_write_syslog ("ww date_str is %s\n",date_str);
	

   	dispValuesDate[0]=date_str;

	 printf ("ww date_str is %s\n",dispValuesDate[0]);

   	 ITKCALL( ifail = TCTYPE_set_create_display_value( tseCreateInput, (char*) "date", 1, (const  char**)(dispValuesDate) ) );

   	 SAFE_SM_FREE(format_str);
   	 SAFE_SM_FREE(date_str);



   ITKCALL( ifail = TCTYPE_create_object ( tseCreateInput, &tseTag ) );
   if (tseTag == null_tag)
   {
      TC_write_syslog ("TimeSheetEntry tag is null\n");
      return ifail;
   }
   ITKCALL( ifail = AOM_save ( tseTag ) );


   //ITKCALL( ifail = AOM_set_value_date( tseTag, "date", theDate));


   char *userName = NULL;
   tag_t userTag = null_tag;
   ITKCALL( ifail = POM_get_user( &userName, &userTag ) );
   ITKCALL ( ifail = AOM_set_value_tag (tseTag, "user_tag", userTag));
   SAFE_SM_FREE (userName);


   float minutes_f = (float) (atof(manHour.workingHours.c_str()) * 60.0);
   int minutes = (int) minutes_f;
   ITKCALL ( ifail = AOM_set_value_int (tseTag, "minutes", minutes));

   logical prgEnabled = false;
   tag_t tskTag = null_tag;
   ITKCALL ( ifail = isProgramEnabled (&prgEnabled));
   ITKCALL( ifail = POM_string_to_tag( stk_tag_uid.c_str(), &tskTag ) );
   if (!prgEnabled)   
   {
      ITKCALL( ifail = AOM_set_value_tag( tseTag, "scheduletask_tag", tskTag ) );
   }
   else
   {
      ITKCALL( ifail = AOM_set_value_tag( tseTag, "d6MheTask_tag", tskTag ) );
   }


   ITKCALL( ifail = AOM_set_value_string( tseTag, "bill_type", manHour.billTypeInternal.c_str() ) );


   tag_t brTag = null_tag;
   ITKCALL( ifail = POM_string_to_tag( manHour.myRefBR.c_str(), &brTag ) );
   ITKCALL( ifail = AOM_set_value_tag( tseTag, "billrate_tag", brTag ) );
   ITKCALL( ifail = AOM_save ( tseTag ) );
   ITKCALL( ifail = AOM_unlock( tseTag ) );

   TC_write_syslog ("createTSETag finished tseTag:%d\n",tseTag);

   *tse_tag = tseTag;
   return ifail;
}



/*
 * checkTSEStatus
 */
int checkTSEStatus(tag_t tseTag, string &status)
{
   int ifail      = ITK_ok;
   int refCount   = 0;
   tag_t *refTags = NULL;

   ITKCALL (ifail = AOM_refresh(tseTag, false));
   ITKCALL( ifail = AOM_ask_value_tags( tseTag, "release_status_list", &refCount, &refTags ) );
   if (refCount > 0)
   {
      tag_t wfTag = refTags[0];
      char *statusName = NULL;
  
      ITKCALL (ifail = AOM_refresh(wfTag, false));
      ITKCALL( AOM_ask_value_string( wfTag , "object_name", &statusName ) );
      if (statusName != NULL)
      {
         status = statusName;
      }      
      SAFE_SM_FREE(statusName);
   }

   SAFE_SM_FREE(refTags);
   return ifail;
}



/*
 * clearTSEWorkflow
 */
int clearTSEWorkflow(tag_t tseTag)
{
   int ifail      = ITK_ok;
   int refCount   = 0;
   tag_t *refTags = NULL;

   ITKCALL (ifail = AOM_refresh(tseTag, false));
   ITKCALL( ifail = AOM_ask_value_tags( tseTag, "release_status_list", &refCount, &refTags ) );
   if (refCount > 0)
   {
      tag_t wfTag = refTags[0];

      ITKCALL (ifail = AOM_refresh(wfTag, true));
      ITKCALL( ifail = AOM_delete(wfTag) );
      ITKCALL( ifail = AOM_unlock( wfTag ) );
   }
   SAFE_SM_FREE(refTags);

   return ifail;
}



/*
 * createTSEWorkflow
 */
int createTSEWorkflow(const ManHourManageService::ManHourEntry &manHour, tag_t tse_tag,
   tag_t *wf_process_tag)
{
   int ifail = ITK_ok;

   char wfName[128] = {0};
   char wfTemplate[128] = {0};
   tag_t wfTemplateTag = null_tag;
   tag_t wfTags[1];
   int   wfTagAttType[1];
   tag_t wfProcessTag = null_tag;

   if (tse_tag == null_tag)
   {
      TC_write_syslog("tse_tag is null\n");
      return ifail;
   }

   sprintf(wfName, "%s-%s_%s_%s", manHour.myPrjId.c_str(), manHour.myPrjName.c_str(), manHour.myTaskTypeDval.c_str(), manHour.myUserName.c_str());
   sprintf(wfTemplate, "%s", TM_APPROVE_WF );
   wfTags[0] = tse_tag;
   wfTagAttType[0] = EPM_target_attachment;

   ITKCALL( ifail = clearTSEWorkflow(tse_tag) );

   ITKCALL( EPM_find_process_template( wfTemplate, &wfTemplateTag ) );
   if (wfTemplateTag == null_tag)
   {
      TC_write_syslog("work flow template tag is null\n");
      return ifail;
   }

   ITKCALL( ifail = EPM_create_process( wfName, "Triggered by manhour", wfTemplateTag, 1,
                                        wfTags, wfTagAttType, &wfProcessTag ) );

   if(wfProcessTag!=null_tag){
	   TC_write_syslog("EPM_create_process success!\n");
   }

   *wf_process_tag = wfProcessTag;

   TC_write_syslog("createTSEWorkflow end\n");
   return ifail;
}



/*
 * bldProgramMap 
 */
int bldProgramMap (const vector<ManHourManageService::ManHourProgram> &programs,
   std::map <const std::string, ManHourManageService::ManHourProgram> &prg_map)
{
   int ifail = ITK_ok;

   char keyBuf[256];

   for (int idx = 0; idx < programs.size (); idx ++)
   {
      ManHourManageService::ManHourProgram prg = programs [idx];

      sprintf (keyBuf, "%s-%s:%s", prg.prjId.c_str(), prg.prjName.c_str(), prg.tskType.c_str());
      string keyStr = keyBuf;

      prg_map.insert (std::pair <std::string, ManHourManageService::ManHourProgram> (keyStr, prg));
   }

   return ifail;
}







/*
 * doSave
 */
int doSave (const char *username,
            const char *year,
            const char *month,
            const std::vector< ManHourManageService::ManHourEntry >& man_hours,
            const std::vector< ManHourManageService::ManHourProgram >& programs,
            ManHourManageService::ManHourEntrySet &mhe_set)
{
   int ifail = ITK_ok;

   printf("1%s 2%s 3%s \n",username,year,month);

   if (man_hours.size() > 0)
   {
      std::map <const std::string, ManHourManageService::ManHourProgram> stkMap;
      ITKCALL ( ifail = bldProgramMap (programs, stkMap));

      for (size_t i = 0; i < man_hours.size(); i++)
      {
    	 ManHourManageService::ManHourEntry mhe = man_hours[i];
         tag_t mheTag = null_tag;

         ITKCALL (ifail = createMHETag (mhe, &mheTag));

         TC_write_syslog ("doSave::mheTag-->%d\n",mheTag);
         if (mheTag == null_tag)
         {
            mhe.tseStatus = "Failed";
            mhe_set.mheSet.push_back (mhe);
            break;
         }

         char *mheTagStr = NULL;
         ITKCALL (ifail = AOM_tag_to_string( mheTag, &mheTagStr ) );
         TC_write_syslog ("doSave::mheTagStr-->%s\n",mheTagStr);

         mhe.myRefMHE = mheTagStr;
         SAFE_SM_FREE(mheTagStr);

        // char *myRefTE = NULL;
         tag_t tseTag = null_tag;

         string stkTagUid = getScheduleTaskTagUid ( stkMap, mhe.myPrjId, mhe.myPrjName, mhe.myTaskType);
         TC_write_syslog ("doSave::stkTagUid-->%s\n",stkTagUid.c_str());

         ITKCALL( ifail = createTSETag( mhe, stkTagUid, &tseTag ) );

         TC_write_syslog ("doSave::tseTag-->%d\n",tseTag);
         if (tseTag == null_tag)
         {
            mhe.tseStatus = "Failed";
            mhe_set.mheSet.push_back (mhe);
            break;
         }

         char *tseTagStr = NULL;
         ITKCALL( ifail = POM_tag_to_string( tseTag, &tseTagStr ) );


         TC_write_syslog ("doSave::tseTagStr-->%s\n",tseTagStr);

         ITKCALL( ifail = AOM_lock( mheTag ) );
         ITKCALL( ifail = AOM_set_value_string( mheTag, "d6RefTE", tseTagStr ) );
         ITKCALL( ifail = AOM_save ( mheTag ) );
         ITKCALL( ifail = AOM_unlock( mheTag ) );

         mhe.myRefTE = tseTagStr;

         tag_t wfProcessTag = null_tag;
         ITKCALL( ifail = createTSEWorkflow( mhe, tseTag, &wfProcessTag ) );

         TC_write_syslog ("doSave::wfProcessTag-->%d\n",wfProcessTag);
         if (wfProcessTag == null_tag)
         {
            TC_write_syslog ("wfProcessTag is null Failed\n");
            mhe.tseStatus = "Failed";
            mhe_set.mheSet.push_back (mhe);
            break;
         }
          
         string tseStatus = "";
         ITKCALL ( ifail = checkTSEStatus(tseTag, tseStatus));

         TC_write_syslog ("tseStatus is %s\n",tseStatus.c_str());
         if (tseStatus.size () > 0 && !tc_strcmp (tseStatus.c_str(), "Released"))
         {
            mhe.tseStatus = "Released";
         }
         else
         {
            mhe.tseStatus = "Failed";
         }
            
         mhe_set.mheSet.push_back (mhe);
      }
   }

   return ifail;
}



#ifdef __cplusplus
}
#endif
