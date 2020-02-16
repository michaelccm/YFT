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

extern "C" int POM_AM__set_application_bypass(logical bypass);

#ifdef __cplusplus
extern "C" {
#endif




/*
 * getCategoryAndTskTypeMapValues 
 */
int getCategoryAndTskTypeMapValues (const char *tsktype_lov_name, std::vector <std::string> &tsktype_vect)
{
   int ifail = ITK_ok;

   tag_t *lovTags     = NULL;     // need to be freed
   tag_t  lovTag      = NULL_TAG;
   int    lovCnt      = 0;

   char **lovValues   = NULL;     // need to be freed
   char  *lovValue    = NULL;
   char **lovDspValues= NULL;     // need to be freed
   char  *lovDspValue = NULL;
   int    lovValueCnt = 0;
   char   tskTypeMappingValue[64]; 

   LOV_usage_t lovUsage;


   ITKCALL (ifail = LOV_find (tsktype_lov_name, &lovCnt, &lovTags));
   if (lovCnt == 0)
   {
      TC_write_syslog ("Can not find the LOV: %s\n", tsktype_lov_name);
   }
   else
   {
      lovTag = lovTags[0];

      ITKCALL (ifail = LOV_ask_values_display_string (lovTag, &lovUsage, &lovValueCnt, &lovDspValues, &lovValues));
      if (lovValueCnt == 0)
      {
         TC_write_syslog ("No value for the LOV: %s\n", tsktype_lov_name);
      }
      else
      {
         for (int tdx = 0; tdx < lovValueCnt; tdx ++)
         {
            lovValue    = lovValues[tdx];        // task-type 
            lovDspValue = lovDspValues[tdx];     // task-type display value
            sprintf (tskTypeMappingValue, "%s=%s", lovValue, lovDspValue);
           
            tsktype_vect.push_back (tskTypeMappingValue);
         }
      }
   }     

//EXIT:
   if (lovValues)
   {
      SAFE_SM_FREE (lovValues);
      lovValues = NULL;
   }

   if (lovTags)
   {
      SAFE_SM_FREE (lovTags);
      lovTags = NULL;
   }

   return ifail;
}


/*
 * getCategoryAndTskTypeMap
 */
int getCategoryAndTskTypeMap (std::map < const std::string, std::vector <string> > &category_tsktype_map)
{
   TC_write_syslog ("getCategoryAndTskTypeMap start \n");
   int ifail = ITK_ok;

   tag_t *lovTags     = NULL;        // need to be freed
   tag_t  lovTag      = NULL_TAG;
   int    lovCnt      = 0;

   char  **lovValues     = NULL;     // need to be freed
   char   *lovValue      = NULL;
   char  **lovDspValues  = NULL;     // need to be freed
   char   *lovDspValue   = NULL;
   int     lovValueCnt   = 0;
   LOV_usage_t lovUsage;


   ITKCALL (ifail = LOV_find (LOV_YFAS_TASKTYPE, &lovCnt, &lovTags));
   if (lovCnt == 0)
   {
      TC_write_syslog ("Can not find the LOV: %s\n", LOV_YFAS_TASKTYPE);
   }
   else
   {
      lovTag = lovTags[0];

      ITKCALL (ifail = LOV_ask_values_display_string (lovTag, &lovUsage, &lovValueCnt, &lovDspValues, &lovValues));
      if (lovValueCnt == 0)
      {
         TC_write_syslog ("No value for the LOV: %s\n", LOV_YFAS_TASKTYPE);
      }
      else
      {
         for (int tdx = 0; tdx < lovValueCnt; tdx ++)
         {
            lovValue = lovValues[tdx];        // category value
            lovDspValue = lovDspValues[tdx];  // LOV name for task-type

            std::vector <string> ttVect;
            ITKCALL (ifail = getCategoryAndTskTypeMapValues (lovDspValue, ttVect));
            category_tsktype_map.insert (std::pair <const std::string, std::vector<string> > (lovValue, ttVect));
         }
      }
   }


//EXIT:
   if (lovValues != NULL)
   {
      SAFE_SM_FREE (lovValues);
      lovValues = NULL;
   }

   if (lovTags != NULL)
   {
      SAFE_SM_FREE (lovTags);
      lovTags = NULL;
   }

   TC_write_syslog ("getCategoryAndTskTypeMap end \n");
   return ifail;
}
   

/*
 * findPreferredItmTag
 */
int findPreferredItemTag (const char *obj_type, const tag_t *itm_tags, const int itm_tags_cnt, tag_t *fnd_tag)
{
   int ifail = ITK_ok;

   tag_t itmTag = NULL_TAG;
   tag_t fndTag = NULL_TAG;
   char *objType = NULL;     // need to be freed.

   *fnd_tag = null_tag;

   for (int i = 0; i < itm_tags_cnt; i++)
   {
      itmTag = itm_tags[i]; 

      if (objType != NULL)
      {
         SAFE_SM_FREE (objType);
         objType = NULL;
      }

      ITKCALL( ifail = AOM_ask_value_string( itmTag , "object_type", &objType));

      if (objType != NULL && !tc_strcmp (objType, obj_type))
      {
         fndTag = itmTag;
         break;
      }
   }

   *fnd_tag = fndTag;

   if (objType != NULL)
   {
      SAFE_SM_FREE (objType);
      objType = NULL;
   }

   return ifail;
}
 

/*
 * getPrograms_from_Project
 */
int getPrograms_from_Project (const char *username, 
    const char *year, 
    const char *month, 
    int total_days,
    std::map <const std::string, std::vector <std::string> > &category_tsktype_map,
    std::vector <ManHourManageService::ManHourProgram> &programs)
{
   int ifail = ITK_ok;

   // project
   int      prjCount    = 0;
   tag_t   *prjTags     = NULL;         // need to be freed
   tag_t    prjTag      = null_tag;     
   char    *prjId       = NULL;         // need to be freed
   char    *prjName     = NULL;         // need to be freed
   logical  isPrjActive = FALSE; 

   int preferredItemNum = 0;
   tag_t *preferredItems = NULL;        // need to be freed

   tag_t   schTag       = NULL_TAG;             
   char   *schTagStr    = NULL;         // need to be freed.
   logical schPublished = false;
  // date_t  startDate    = NULLDATE;
  // date_t  endDate      = NULLDATE;

   tag_t   prgInfoTag    = NULL_TAG;
   tag_t   prgInfoRevTag = NULL_TAG;
   char   *category      = NULL;        // need to be freed


   ITKCALL(ifail = GCOMM_qry( QRY_FIND_MANHOUR_PROEJCT, &prjCount, &prjTags, 2, username, "NP*"));

   POM_AM__set_application_bypass(true);

   for (int i = 0; i < prjCount; i ++)
   {
      prjTag = prjTags [i];

      ITKCALL (ifail = PROJ_is_project_active(prjTag, &isPrjActive));
      if (!isPrjActive)
         continue;

      if (prjId != NULL)
      {
         SAFE_SM_FREE (prjId);
         prjId = NULL;
      }
      ITKCALL( ifail = AOM_ask_value_string( prjTag, "project_id", &prjId ) );

      if (prjName != NULL)
      {
         SAFE_SM_FREE (prjName);
         prjName = NULL;
      }
      ITKCALL( ifail = AOM_ask_value_string( prjTag, "project_name", &prjName ) );

      if (preferredItems != NULL)
      {
         SAFE_SM_FREE (preferredItems);
         preferredItems = NULL;
      }
      ITKCALL( ifail = AOM_ask_value_tags( prjTag , "TC_Program_Preferred_Items", &preferredItemNum, &preferredItems ) );

      ITKCALL (ifail = findPreferredItemTag ("Schedule", preferredItems, preferredItemNum, &schTag));
      if (schTag == null_tag)
      {
         continue;
      } 
      ITKCALL (ifail = AOM_ask_value_logical (schTag, "published", &schPublished));
      if (!schPublished)
      {
          continue;
      }
 
      if (schTagStr != NULL)
      {
         SAFE_SM_FREE (schTagStr);
         schTagStr = NULL;
      }
      ITKCALL( ifail = AOM_tag_to_string( schTag, &schTagStr ) );


      ITKCALL (ifail = findPreferredItemTag ("JCI6_ProgramInfo", preferredItems, preferredItemNum, &prgInfoTag));
      if (prgInfoTag == null_tag)
      {
         continue;
      }
      ITKCALL (ifail = ITEM_ask_latest_rev (prgInfoTag, &prgInfoRevTag));
      if (prgInfoRevTag == null_tag)
      {
         continue;
      }

      date_t startDate = NULLDATE;
      date_t endDate = NULLDATE;
      char startDateBuf[20];
      char endDateBuf[20];
      char myDateBuf[20];

      startDate = NULLDATE;
      endDate = NULLDATE;
      ITKCALL (ifail = AOM_ask_value_date(prgInfoRevTag, "jci6_StartDate", &startDate));
      ITKCALL (ifail = AOM_ask_value_date(prgInfoRevTag, "jci6_EndDate", &endDate));

      sprintf (startDateBuf, "%4d:%02d", startDate.year, startDate.month + 1);
      sprintf (endDateBuf, "%4d:%02d", endDate.year, endDate.month + 1);
      sprintf (myDateBuf, "%s:%s", year, month);

      if (tc_strcmp (myDateBuf, startDateBuf) < 0 || tc_strcmp (myDateBuf, endDateBuf) > 0 )
         continue;

      int startDay = 1;
      if (tc_strcmp (myDateBuf, startDateBuf) == 0)
      {
         startDay = startDate.day;
      }

      int endDay = total_days;
      if (tc_strcmp (myDateBuf, endDateBuf) == 0)
      {
         endDay = endDate.day;
      }

      if (category != NULL)
      {
         SAFE_SM_FREE (category);
         category = NULL;
      }
      ITKCALL( ifail = AOM_ask_value_string( prgInfoRevTag, "jci6_Category", &category ) );
      string category_str = category;

      std::map<const std::string, std::vector <std::string> >::iterator ttIterator;
      ttIterator = category_tsktype_map.find (category_str);
      if (ttIterator != category_tsktype_map.end())
      {
         std::vector <string> ttVect = ttIterator -> second;

         int size = ttVect.size ();
         for (int jdx = 0; jdx < size; jdx ++)
         {
            string tskTypeAndDval = ttVect[jdx];
            char tskType[32] = {'\0'};
            char tskTypeDval[32] = {'\0'};
            sscanf(tskTypeAndDval.c_str(), "%[^=]=%[^=]", tskType, tskTypeDval );

            ManHourManageServiceImpl::ManHourProgram mhPrg;

            mhPrg.prjId = prjId;                 // prjId
            mhPrg.prjName = prjName;             // prjName
            mhPrg.tskType = tskType;            // tskType
            mhPrg.tskTypeDval = tskTypeDval;     // tskTypdDval
            mhPrg.startDay = startDay;           // startDay
            mhPrg.endDay = endDay;               // endDay

            //mhPrg.stkTag = "";                 // ScheduleTask or D6MheTask
            mhPrg.schTag = schTagStr;            // Schedule tag

            mhPrg.prjStartYear = startDate.year;
            mhPrg.prjStartMonth = startDate.month;
            mhPrg.prjStartDay = startDate.day;
            mhPrg.prjEndYear = endDate.year;
            mhPrg.prjEndMonth = endDate.month;
            mhPrg.prjEndDay = endDate.day;

            programs.push_back (mhPrg);
         }
      }
   }

	POM_AM__set_application_bypass(false);

//EXIT:
   if (prjId != NULL)
   {
      SAFE_SM_FREE (prjId);
      prjId = NULL;
   }

   if (preferredItems != NULL)
   {
      SAFE_SM_FREE (preferredItems);
      preferredItems = NULL;
   }

   if (prjTags != NULL)
   {
      SAFE_SM_FREE (prjTags);
      prjTags = NULL;
   }

   if (category != NULL)
   {
      SAFE_SM_FREE (category);
      category = NULL;
   }

   return ifail;
}


/*
 * getProrams_from_MheTask
 */
int getPrograms_from_MheTask (const char *username,
                              const char *year,
                              const char *month,
                              const int total_days,
                              std::vector <ManHourManageService::ManHourProgram> &programs)
{
   TC_write_syslog ("getPrograms_from_MheTask start \n");
   int ifail = ITK_ok;

   int    mheTskCnt   = 0;
   tag_t *mheTskTags  = NULL;      // need to be freed.
   tag_t  mheTskTag   = NULL_TAG;
   char  *tmpStr      = NULL;      // need to be freed.

   ITKCALL(ifail = GCOMM_qry( QRY_FIND_MANHOUR_TASK, &mheTskCnt, &mheTskTags, 2, username, "in_progress"));

   if (mheTskCnt > 0)
   {
      for (int i=0; i < mheTskCnt; i++)
      {
         mheTskTag = mheTskTags[i];
         ManHourManageServiceImpl::ManHourProgram mhPrg;

         ITKCALL (ifail = AOM_refresh(mheTskTag, false));

         // prjId
         if (tmpStr != NULL)
         {
            SAFE_SM_FREE (tmpStr);
            tmpStr = NULL;
         }
         ITKCALL ( ifail = AOM_ask_value_string (mheTskTag, "d6PrjId", &tmpStr));
         mhPrg.prjId = tmpStr;

         if (tmpStr != NULL)
         {
            SAFE_SM_FREE (tmpStr);
            tmpStr = NULL;
         }
         ITKCALL ( ifail = AOM_ask_value_string (mheTskTag, "d6PrjName", &tmpStr));
         mhPrg.prjName = tmpStr;

         if (tmpStr != NULL)
         {
            SAFE_SM_FREE (tmpStr);
            tmpStr = NULL;
         }
         ITKCALL ( ifail = AOM_ask_value_string (mheTskTag, "d6TaskType", &tmpStr));
         mhPrg.tskType = tmpStr;

         if (tmpStr != NULL)
         {
            SAFE_SM_FREE (tmpStr);
            tmpStr = NULL;
         }
         ITKCALL ( ifail = AOM_ask_value_string (mheTskTag, "d6TaskTypeDval", &tmpStr));
         mhPrg.tskTypeDval = tmpStr;

         date_t startDate = NULLDATE;
         date_t endDate = NULLDATE;
         char startDateBuf[20];
         char endDateBuf[20];
         char myDateBuf[20];

         ITKCALL (ifail = AOM_ask_value_date(mheTskTag, "d6PrjStartDate", &startDate));
         ITKCALL (ifail = AOM_ask_value_date(mheTskTag, "d6PrjEndDate", &endDate));
         sprintf (startDateBuf, "%4d:%02d", startDate.year, startDate.month + 1);
         sprintf (endDateBuf, "%4d:%02d", endDate.year, endDate.month + 1);
         sprintf (myDateBuf, "%s:%s", year, month);

         if (tc_strcmp (myDateBuf, startDateBuf) < 0 || tc_strcmp (myDateBuf, endDateBuf) > 0 )
            continue; 

         int startDay = 1;
         if (tc_strcmp (myDateBuf, startDateBuf) == 0)
         {
            startDay = startDate.day;
         }

         int endDay = total_days;
         if (tc_strcmp (myDateBuf, endDateBuf) == 0)
         {
            endDay = endDate.day;
         } 

         mhPrg.startDay = startDay;
         mhPrg.endDay = endDay;

         mhPrg.prjStartYear = startDate.year;
         mhPrg.prjStartMonth = startDate.month;
         mhPrg.prjStartDay = startDate.day;
         mhPrg.prjEndYear = endDate.year;
         mhPrg.prjEndMonth = endDate.month;
         mhPrg.prjEndDay = endDate.day;

         if (tmpStr != NULL)
         {
            SAFE_SM_FREE (tmpStr);
            tmpStr = NULL;
         }
         ITKCALL (ifail = AOM_tag_to_string( mheTskTag, &tmpStr ) );
         mhPrg.stkTag = tmpStr;

         tag_t schTag = NULLTAG;
         if (tmpStr != NULL)
         {
            SAFE_SM_FREE (tmpStr);
            tmpStr = NULL;
         }
         ITKCALL( ifail = AOM_ask_value_tag( mheTskTag, "d6Schedule_tag", &schTag));
         ITKCALL (ifail = AOM_tag_to_string( schTag, &tmpStr ) );
         mhPrg.schTag = tmpStr;

         programs.push_back(mhPrg);
      }
   }

//EXIT:
   if (mheTskTags != NULL)
   {
      SAFE_SM_FREE(mheTskTags);
   }

   if (tmpStr != NULL)
   {
      SAFE_SM_FREE (tmpStr);
   }

   TC_write_syslog ("getPrograms_from_MheTask end \n");
   return ifail;
}


/*
 * doCreateMheTask
 */
int doCreateMheTask (const char *username, ManHourManageService::ManHourProgram &mh_prg)
{
   int ifail = ITK_ok;

   tag_t mheTskType        = null_tag;
   tag_t mheTskCreateInput = null_tag;
   tag_t mheTskTag         = null_tag;
   char* dispValues[1];

   dispValues[0] = (char*) "Man Hour Task";

   ITKCALL( ifail = TCTYPE_find_type("D6MheTask", "D6MheTask", &mheTskType) );

   ITKCALL( ifail = TCTYPE_construct_create_input ( mheTskType, &mheTskCreateInput ) );
   ITKCALL( ifail = TCTYPE_set_create_display_value( mheTskCreateInput, (char*) "object_name", 1, (const char**)(dispValues) ) );
   ITKCALL( ifail = TCTYPE_create_object ( mheTskCreateInput, &mheTskTag ) );
   ITKCALL( ifail = AOM_save ( mheTskTag ) );

   ITKCALL( ifail = AOM_set_value_string( mheTskTag, "d6UserName", username));
   ITKCALL( ifail = AOM_set_value_string( mheTskTag, "d6PrjName", mh_prg.prjName.c_str()));
   ITKCALL( ifail = AOM_set_value_string( mheTskTag, "d6PrjId", mh_prg.prjId.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTskTag, "d6TaskType", mh_prg.tskType.c_str() ) );
   ITKCALL( ifail = AOM_set_value_string( mheTskTag, "d6TaskTypeDval", mh_prg.tskTypeDval.c_str() ) );

   string status   = "in_progress";
   ITKCALL( ifail = AOM_set_value_string( mheTskTag, "d6Status", status.c_str()));

   date_t startDate = NULLDATE;
   startDate.year   =(short) mh_prg.prjStartYear;
   startDate.month  = (byte)mh_prg.prjStartMonth;
   startDate.day    = (byte)mh_prg.prjStartDay;
   ITKCALL( ifail = AOM_set_value_date( mheTskTag, "d6PrjStartDate", startDate));

   date_t endDate   = NULLDATE;
   endDate.year     = (short)mh_prg.prjEndYear;
   endDate.month    = (byte)mh_prg.prjEndMonth;
   endDate.day      = (byte)mh_prg.prjEndDay;
   ITKCALL( ifail = AOM_set_value_date( mheTskTag, "d6PrjEndDate", endDate));

   tag_t schTag = NULLTAG;
   ITKCALL (ifail = POM_string_to_tag( mh_prg.schTag.c_str(), &schTag));
   ITKCALL( ifail = AOM_set_value_tag( mheTskTag, "d6Schedule_tag", schTag ) );

   ITKCALL( ifail = AOM_save ( mheTskTag ) );
   ITKCALL( ifail = AOM_unlock( mheTskTag ) );

   char *mheTskTagStr = NULL;
   ITKCALL (ifail = AOM_tag_to_string( mheTskTag, &mheTskTagStr ) );
   mh_prg.stkTag = mheTskTagStr;
   SAFE_SM_FREE(mheTskTagStr);

   return ifail;
}


/*
 * createMheTask
 */
int createMheTask (const char *username, std::vector <ManHourManageService::ManHourProgram> &programs)
{
   int ifail = ITK_ok;

   if (programs.size () > 0)
   {
      for (int idx = 0; idx < programs.size (); idx ++)
      {
         ManHourManageServiceImpl::ManHourProgram &mhPrg = programs[idx];
         ITKCALL (ifail = doCreateMheTask (username, mhPrg));
      }
   }

//EXIT:
   return ifail;
}


/*
 * doUpdateMheTask
 */
int doUpdateMheTask (ManHourManageService::ManHourProgram &prg_project, ManHourManageService::ManHourProgram &prg_mhetask)
{
   int ifail = ITK_ok;

   prg_project.stkTag = prg_mhetask.stkTag;

   if (prg_mhetask.prjEndYear  != prg_project.prjEndYear ||
       prg_mhetask.prjEndMonth != prg_project.prjEndMonth ||
       prg_mhetask.prjEndDay   != prg_project.prjEndDay)
   {
         date_t endDate = NULLDATE;
         tag_t stkTag_mhetask = NULLTAG;
        
         ITKCALL ( ifail = POM_string_to_tag ( prg_mhetask.stkTag.c_str(), &stkTag_mhetask ) );
         endDate.year =(short) prg_project.prjEndYear;
         endDate.month = (byte)prg_project.prjEndMonth;
         endDate.day =(byte) prg_project.prjEndDay;
        
         ITKCALL ( ifail = AOM_refresh(stkTag_mhetask, true));
         ITKCALL ( ifail = AOM_set_value_date( stkTag_mhetask, "d6PrjEndDate", endDate));
         ITKCALL( ifail = AOM_save ( stkTag_mhetask ) );
         ITKCALL( ifail = AOM_unlock ( stkTag_mhetask));
   }

   return ifail;
}
        

/*
 * updateMheTask
 */
int updateMheTask (const char *username,
    std::vector <ManHourManageService::ManHourProgram> &prgs_project,
    std::vector <ManHourManageService::ManHourProgram> &prgs_mhetask)
{
   int ifail = ITK_ok;
   char keyBuf[256];
   std::map <const std::string, ManHourManageService::ManHourProgram> prgsMap_mhetask;
   std::map <const std::string, ManHourManageService::ManHourProgram>::iterator iter_mhetask;

   ITKCALL ( ifail = bldProgramMap (prgs_mhetask, prgsMap_mhetask));
  
   for (int idx = 0; idx < prgs_project.size (); idx ++)
   {
      sprintf (keyBuf, "%s-%s:%s", prgs_project[idx].prjId.c_str(),
                                   prgs_project[idx].prjName.c_str(),
                                   prgs_project[idx].tskType.c_str());
      string keyStr = keyBuf;

      iter_mhetask = prgsMap_mhetask.find (keyStr);
      if (iter_mhetask != prgsMap_mhetask.end ())
      {
         prgsMap_mhetask.erase (iter_mhetask);

         ITKCALL ( ifail = doUpdateMheTask (prgs_project [idx], iter_mhetask -> second));
      }
      else
      {
         ITKCALL ( ifail = doCreateMheTask (username, prgs_project[idx]));
      } 
   }
  
   iter_mhetask = prgsMap_mhetask.begin();
   while (iter_mhetask != prgsMap_mhetask.end ())
   {
      ManHourManageService::ManHourProgram prg_mhetask = iter_mhetask -> second;
      tag_t stkTag_mhetask = NULLTAG;

      ITKCALL ( ifail = POM_string_to_tag ( prg_mhetask.stkTag.c_str(), &stkTag_mhetask ) );

      ITKCALL ( ifail = AOM_refresh(stkTag_mhetask, true));
      ITKCALL ( ifail = AOM_set_value_string( stkTag_mhetask, "d6Status", "closed"));
      ITKCALL( ifail = AOM_save ( stkTag_mhetask ) );
      ITKCALL( ifail = AOM_unlock ( stkTag_mhetask));

      iter_mhetask ++;
   }
  
   return ifail;
}


/*
 * getPrograms_New
 */
int getPrograms_new (const char *username, ManHourManageServiceImpl::ManHourInfo &mhinfo)
{
   TC_write_syslog("getPrograms_new start \n");
   int ifail = ITK_ok;

   std::map <const std::string, std::vector<std::string> > categoryTskTypeMap;
   std::vector <ManHourManageService::ManHourProgram> prgs_mhetask;
   std::vector <ManHourManageService::ManHourProgram> prgs_project;

   ITKCALL ( ifail = getCategoryAndTskTypeMap (categoryTskTypeMap));
   ITKCALL ( ifail = getPrograms_from_Project (username, 
                                               mhinfo.myYear.c_str(),
                                               mhinfo.myMonth.c_str(), 
                                               mhinfo.totalDays, 
                                               categoryTskTypeMap, 
                                               prgs_project));
 
   ITKCALL ( ifail = getPrograms_from_MheTask (username, 
                                               mhinfo.myYear.c_str(),
                                               mhinfo.myMonth.c_str(),
                                               mhinfo.totalDays,
                                               prgs_mhetask));


   if (prgs_mhetask.size () == 0)
   {
      ITKCALL ( ifail = createMheTask (username, prgs_project));
   } 
   else
   {
      ITKCALL ( ifail = updateMheTask (username, prgs_project, prgs_mhetask));
   }

   mhinfo.theProgramSet = prgs_project;
    
   return ifail;
}


#ifdef __cplusplus
}
#endif


