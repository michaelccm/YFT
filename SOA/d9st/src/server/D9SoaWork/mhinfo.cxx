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
 * isManHourEditable
 * 
 */
int isManHourEditable (const char *year, const char *month, logical *editable)
{
   TC_write_syslog ("isManHourEditable start\n");
   int ifail = ITK_ok;

   date_t myToday;

   char theYearMonth[10];   
   char myYearMonth[10];   
  
   int  mysCnt = 0;
   int  mysIdx = 0;
   char **mysValues= NULL;  // need to be freed.
   char *mysValue = NULL;

   *editable = FALSE;       // initialization.

   sprintf (theYearMonth,"%s:%s", year, month);

   GUTIL_current_date (&myToday);

   sprintf (myYearMonth, "%4d:%02d", myToday.year, myToday.month + 1);

   if (!tc_strcmp (myYearMonth, theYearMonth))
   {
      *editable = TRUE;
      return ifail;
   } 
 
   if (!(*editable))
   {
      ITKCALL(PREF_ask_char_values(PREF_YFAS_EDITABLE_YEAR_MONTH, &mysCnt, &mysValues));
      if(mysCnt == 0 )
      {
         TC_write_syslog("YFAS_Timesheet_Editable is not defined\n");
      }
      else
      {
         for (mysIdx = 0; mysIdx < mysCnt; mysIdx ++)
         {
            mysValue = *(mysValues + mysIdx);
            if (!tc_strcmp (mysValue, theYearMonth))
            {
            	*editable = TRUE;
                break;
            }
         }
      }
   }

//EXIT:
   if (mysValues)
   {
      SAFE_SM_FREE(mysValues);
      mysValues = NULL;
   }

   TC_write_syslog ("isManHourEditable end\n");
   return ifail;
}


/*
 * isHourlyBasedUser 
 */
int isHourlyBasedUser (const char *username, string &position, logical *is_hourly_usr)
{
   TC_write_syslog ("isHourlyBasedUser start\n");
   int ifail = ITK_ok;

   logical isHourlyUsr   = FALSE;

   tag_t  qryTag         = NULLTAG;   
  // char **qryEntries     = NULL;
   char **qryItems       = NULL;
   char **qryValues      = NULL;   
   char  *qryCnts [1];             // need to be initialized before being used.
   int    qryCnt         = 0;

   tag_t *qryRes         = NULL;    // need to be freed.
   int    qryResFound    = 0;
   
   char  *usrIdBuf       = NULL;    // need to be freed. 
   char  *myPosition     = NULL;    // need to be freed.

   const char positions[256] = "Manager,Lead Engineer,Senior Engineer,Engineer,Assistant Engineer,Engineer Assistant,ExtSupporter";


   ITKCALL( QRY_find( QRY_YFJC_SEARCHDISCIPLINEBYID , &qryTag ) );
   if (qryTag == NULLTAG)
   {
      return -1;
   }

   ITKCALL( QRY_find_user_entries( qryTag , &qryCnt , &qryItems, &qryValues ) );


  (usrIdBuf = ( char* )MEM_alloc( ( tc_strlen( username ) + 1 ) * sizeof( char ) ));

   if (usrIdBuf==NULL)
   {
      return -1;
   }
   tc_strcpy (usrIdBuf, username);  
   qryCnts[0] = usrIdBuf;

   ITKCALL( QRY_execute( qryTag , qryCnt , qryItems , qryCnts , &qryResFound , &qryRes));

  // logical foundPosition = false;

   for( int i = 0 ;i < qryResFound ; i++ )
   {
      if (myPosition != NULL)
      {
         SAFE_SM_FREE (myPosition);
         myPosition = NULL;
      }

      ITKCALL( AOM_ask_value_string( qryRes[i] , CONST_OBJECT_NAME , &myPosition ) );
      if( tc_strstr (positions, myPosition) != NULL)
      {
         position = myPosition;
         if( !tc_strcmp (CONST_HOURLYUSER, myPosition))
         {
            isHourlyUsr = TRUE;
         }
         break;
      }
   }

//CLEANUP:
   if( usrIdBuf != NULL )
   {
      MEM_free( usrIdBuf );
      usrIdBuf = NULL;
   }

   if( qryRes != NULL )
   {
      MEM_free( qryRes );
      qryRes = NULL;
   }

   if (myPosition)
   {
      SAFE_SM_FREE (myPosition);
      myPosition = NULL;
   }

//EXIT:
   *is_hourly_usr = isHourlyUsr;

   TC_write_syslog ("isHourlyBasedUser end\n");
   return ifail;
}


/*
 * isLeapYear.
 */
logical isLeapYear(int year)
{
   logical leapYear = FALSE;
   
   if( (year % 4 == 0 && year % 100 != 0) || 
       (year % 400 == 0))
   {
      leapYear = TRUE;
   }

   return leapYear;
}


/*
 * createNWDMap
 */
int createNWDMap (int year, int month, int days, std::map <const std::string, int>& nwd_map)
{
   TC_write_syslog ("createNWDMap start\n");
   int ifail = ITK_ok;

   tag_t tccalendar = NULLTAG;
   char firstDay[24] = {0};
   char lastDay[24] = {0};
   int   nwdCnt   = 0;  
   char **nwdSet  = NULL;    
   char keyBuf[24];

   ITKCALL(ifail = TCCALENDAR_get_base_tccalendar(&tccalendar));

   if(tccalendar!=NULLTAG)
   {
      sprintf (firstDay, "%4d-%02d-01 00:00:00", year, month);
      sprintf (lastDay, "%4d-%02d-%02d 23:59:59", year,month, days);

      ITKCALL(ifail = TCCALENDAR_get_resource_non_working_dates(tccalendar,firstDay, lastDay, &nwdCnt, &nwdSet));

      if (nwdCnt != 0)
      {
         for (int idx = 0; idx < nwdCnt; idx ++)
         {
           char *nwdStr = *(nwdSet + idx);
           tc_strncpy(keyBuf, nwdStr, 10);
           keyBuf[10] = '\0';

           std::string keyStr = keyBuf;
           nwd_map.insert (std::pair <const std::string, int >(keyStr, 1));
         }
      }
   }

   if(nwdSet != NULL)
   {
      MEM_free(nwdSet);
   }

   TC_write_syslog ("createNWDMap end\n");
   return ifail;
}


/*
 * createHolDayMap
 */
int createHolDayMap (int year, int month, std::map <const std::string, int>& hd_map)
{
   TC_write_syslog ("createHolDayMap start\n");
   int ifail = ITK_ok;

   int    optCnt = 0;
   char **optValues = NULL;    // need to be freed.
   char  *optValue  = NULL;
   char   optBuf[24];
   char  *bufIdx = NULL;
   char  *bufIdx_tmp = NULL;

   char   sYear[10];
   char   sMonth[10];
   char   sDay[10];

   ITKCALL(ifail = PREF_ask_char_values(PREF_YFJC_HOLIDAY_ALSO_WEEKEND,&optCnt,&optValues));
   for (int i = 0; i < optCnt; i++)
   {
      tc_strcpy(sYear, "");
      tc_strcpy(sMonth, "");
      tc_strcpy(sDay, "");

      optValue = *(optValues + i);
      if (optValue == NULL)
         continue;

      tc_strcpy (optBuf, optValue);

      bufIdx = optBuf;

      bufIdx_tmp = tc_strstr (bufIdx, "-");
      if (bufIdx_tmp == NULL)
         continue;

      *(bufIdx_tmp) = '\0';
      tc_strcpy (sYear, bufIdx);

      bufIdx = bufIdx_tmp + 1;
    
      bufIdx_tmp = tc_strstr (bufIdx, "-");
      if (bufIdx_tmp == NULL)
         continue;
      
      *(bufIdx_tmp) = '\0';
      tc_strcpy (sMonth, bufIdx);

      bufIdx = bufIdx_tmp + 1;
      tc_strcpy (sDay, bufIdx);

      int iYear = atoi(sYear);
      int iMonth = atoi(sMonth);
      int iDay = atoi (sDay);
      if (iYear==year && iMonth == month)
      {
    	 char keyBuf[24];
         sprintf (keyBuf, "%04d-%02d-%02d", iYear, iMonth, iDay);
         std::string keyStr = keyBuf;

         hd_map.insert (std::pair <const std::string, int >(keyStr, 1));
      }
   }

   if(optValues != NULL)
   {
      MEM_free(optValues);
   }

   TC_write_syslog ("createHolDayMap end\n");
   return ifail;
}


/*
 * getMonthTmp.
 */
int getMonthTmp (int year, int month, ManHourManageServiceImpl::ManHourInfo &mhinfo)
{
   TC_write_syslog ("getMonthTmp start\n");
   int ifail = ITK_ok;

   const char *dayOfWeek[]= {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

   static int daysOfMonthInNonLeapYear[12] = {31,28,31,30,31,30,31,31,30,31,30,31};
   static int daysOfMontInLeapYear[12]     = {31,29,31,30,31,30,31,31,30,31,30,31};

   int daysForPreviousYears = 0; 

   int daysForPreviousMonths = 0;

   int daysForPreviousYM = 0;

   int daysOfMonth = 0;

   int firstDayOfMonth = 0;

   int lastDayOfMonth = 0;


   for (int yearIdx = 1; yearIdx < year; yearIdx ++)
   {
      if (isLeapYear (yearIdx))
         daysForPreviousYears += 366;
      else
         daysForPreviousYears += 365;
   }

   for (int mntIdx = 1; mntIdx < month; mntIdx ++)
   {
      if (isLeapYear (year))
         daysForPreviousMonths += daysOfMontInLeapYear[mntIdx -1];
      else
         daysForPreviousMonths += daysOfMonthInNonLeapYear[mntIdx -1];
   }

   daysForPreviousYM = daysForPreviousYears + daysForPreviousMonths;

   if (isLeapYear (year))
      daysOfMonth = daysOfMontInLeapYear[month -1];
   else
      daysOfMonth = daysOfMonthInNonLeapYear[month -1];
   firstDayOfMonth = 1;
   lastDayOfMonth = daysOfMonth;

   std::map <const std::string, int> nwdMap;
   ITKCALL ( ifail = createNWDMap (year, month, daysOfMonth, nwdMap));
   std::map<const std::string, int>::iterator nwdIter;

   std::map <const std::string, int> hdMap;
   ITKCALL ( ifail = createHolDayMap (year, month, hdMap));
   std::map<const std::string, int>::iterator hdIter;

   mhinfo.totalDays = daysOfMonth;
   for (int dayIdx = firstDayOfMonth; dayIdx <= lastDayOfMonth; dayIdx++)
   {
      ManHourManageServiceImpl::ManHourMonthTmp mhmTmp;
 
      mhmTmp.dayOfMonth = dayIdx;

      int dayOfWeekIdx = (daysForPreviousYM + dayIdx)%7;
      mhmTmp.dayOfWeek = dayOfWeek [dayOfWeekIdx];

      if ( !tc_strcmp(mhmTmp.dayOfWeek.c_str(), "Sat") || !tc_strcmp (mhmTmp.dayOfWeek.c_str(), "Sun"))
      {
         mhmTmp.isWeekend = TRUE;
      } 
      else
      {
    	 mhmTmp.isWeekend = FALSE;
      }

      char keyBuf[24];
      sprintf (keyBuf, "%04d-%02d-%02d", year, month, dayIdx);

      std::string keyStr = keyBuf;
      nwdIter = nwdMap.find (keyStr);
      mhmTmp.isWorkRequired = FALSE;
      mhmTmp.isHoliday = FALSE;
      if (nwdIter == nwdMap.end())
      {
         mhmTmp.isWorkRequired = TRUE;
      }
      hdIter = hdMap.find (keyStr);
      if (hdIter != hdMap.end())
      {
         mhmTmp.isHoliday = TRUE;
      }

      mhinfo.theMonthTmp.push_back (mhmTmp);
   } 

   TC_write_syslog ("getMonthTmp end\n");
   return ifail;
} 


/*
 * getPrograms_old
 */
int getPrograms_old (const char *username, ManHourManageServiceImpl::ManHourInfo &mhinfo)
{
   int ifail = ITK_ok;

   tag_t userTag  = NULLTAG;      
   tag_t rsTag    = NULLTAG;      
   tag_t *trvTags = NULL;         
   tag_t  trvTag  = NULLTAG;      
   int   trvCnt   = 0;
   int   trvIdx   = 0;
  
   tag_t  stkTag  = NULLTAG;      
   tag_t  schTag  = NULLTAG;      
   tag_t  prjTag  = NULLTAG;      
   char  *prjName = NULL;         
   char  *prjId   = NULL;
   logical isPrjActive = FALSE;
   char   *stkTagStr = NULL; 
   char   *tskType   = NULL;      
   char   *tskTypeDval = NULL;   

   char  *stkStatus = NULL;      
   logical schPublished = FALSE;


   ITKCALL (ifail = SA_find_user(username, &userTag));  
   ITKCALL (ifail = GRM_find_relation_type("ResourceAssignment", &rsTag));

   ITKCALL (ifail = GRM_list_primary_objects_only(userTag , rsTag , &trvCnt, &trvTags));
   for (trvIdx = 0; trvIdx < trvCnt; trvIdx ++)

   ITKCALL (ifail = GRM_list_primary_objects_only(userTag , rsTag , &trvCnt, &trvTags));
   for (trvIdx = 0; trvIdx < trvCnt; trvIdx ++)
   {
      trvTag = *(trvTags + trvIdx);   

      ITKCALL (ifail = AOM_ask_value_tag( trvTag , "items_tag", &stkTag)); 

      date_t startDate = NULLDATE;
      date_t endDate = NULLDATE;
      char startDateBuf[20];
      char endDateBuf[20];
      char myDateBuf[20];

      ITKCALL (ifail = AOM_ask_value_date(stkTag, "start_date", &startDate));
      ITKCALL (ifail = AOM_ask_value_date(stkTag, "finish_date", &endDate));
      sprintf (startDateBuf, "%4d:%02d", startDate.year, startDate.month + 1);
      sprintf (endDateBuf, "%4d:%02d", endDate.year, endDate.month + 1);
      sprintf (myDateBuf, "%s:%s", mhinfo.myYear.c_str(), mhinfo.myMonth.c_str());
     
      if (tc_strcmp (myDateBuf, startDateBuf) < 0 || tc_strcmp (myDateBuf, endDateBuf) > 0 )
         continue; 

      int startDay = 1;
      if (tc_strcmp (myDateBuf, startDateBuf) == 0)
      {
         startDay = startDate.day;
      }

      int endDay = mhinfo.totalDays;
      if (tc_strcmp (myDateBuf, endDateBuf) == 0)
      {
         endDay = endDate.day;
      } 

      ITKCALL (ifail = AOM_ask_value_string (stkTag, "fnd0state", &stkStatus));
      if (tc_strcmp (stkStatus, "in_progress") != 0)
      {
         SAFE_SM_FREE(stkStatus);
         continue;
      }
      else
      {
         SAFE_SM_FREE(stkStatus);
      }

      ITKCALL (ifail = AOM_ask_value_tag( stkTag , "schedule_tag", &schTag));
      ITKCALL (ifail = AOM_ask_value_logical (schTag, "published", &schPublished));
      if (!schPublished)
      {
    	  continue;
      }

      ITKCALL (ifail = AOM_ask_value_tag( schTag,  "owning_project", &prjTag )); 
      ITKCALL (ifail = PROJ_is_project_active(prjTag, &isPrjActive));
      if (!isPrjActive) 
         continue;

      ManHourManageServiceImpl::ManHourProgram mhPrg;
      ITKCALL (ifail =  AOM_ask_value_string( prjTag , "project_name" , &prjName));
      mhPrg.prjName = prjName;
      SAFE_SM_FREE(prjName);

      ITKCALL (ifail =  AOM_ask_value_string( prjTag , "project_id" , &prjId));
      mhPrg.prjId = prjId;
      SAFE_SM_FREE(prjId);

      ITKCALL (ifail =  AOM_ask_value_string( stkTag , "jci6_TaskType" , &tskType));
      mhPrg.tskType = tskType;
      SAFE_SM_FREE(tskType);

      ITKCALL (ifail = AOM_UIF_ask_value( stkTag , "jci6_TaskType" , &tskTypeDval));
      mhPrg.tskTypeDval = tskTypeDval;
      SAFE_SM_FREE(tskTypeDval);

      ITKCALL (ifail = AOM_tag_to_string( stkTag, &stkTagStr ) );
      mhPrg.stkTag = stkTagStr;
      SAFE_SM_FREE(stkTagStr);

      mhPrg.startDay = startDay;
    
      mhPrg.endDay = endDay;

      mhPrg.prjStartYear = startDate.year;
      mhPrg.prjStartMonth = startDate.month;
      mhPrg.prjStartDay = startDate.day;

   
      mhPrg.prjEndYear = endDate.year;
      mhPrg.prjEndMonth = endDate.month;
      mhPrg.prjEndDay = endDate.day;


      mhinfo.theProgramSet.push_back(mhPrg);
   }   
   

//EXIT:
   SAFE_SM_FREE(trvTags);
   TC_write_syslog ("getPrograms_old end\n");
   return ifail;
}


/*
 * isProgramEnabled
 */
int isProgramEnabled (logical *is_enabled)
{
    int ifail = ITK_ok;

    char  **opts = NULL;
    char *opt = NULL;
    int optCnt = 0;

    *is_enabled = false;

    ITKCALL(ifail = PREF_ask_char_values(PREF_YFAS_ENABLE_PROGRAM, &optCnt, &opts));

    if (optCnt > 0)
    {
       opt = opts[0];
       if (!tc_strcmp (opt, "TRUE"))
       {
          *is_enabled = true; 
       }
    }

    if (opts != NULL)
    {
       SAFE_SM_FREE (opts);
       opts = NULL;
    }

    return ifail;
}


/*
 * getPrograms 
 */
int getPrograms (const char *username, ManHourManageServiceImpl::ManHourInfo &mhinfo)
{
   int ifail = ITK_ok;

   logical prgEnabled = false;

   ITKCALL ( ifail = isProgramEnabled (&prgEnabled));

   if (!prgEnabled)
   {
      ITKCALL ( ifail = getPrograms_old (username, mhinfo));
   }
   else
   {
      ITKCALL ( ifail = getPrograms_new (username, mhinfo));
   }
   
   return ifail;
}


/*
 * getBillRateTag
 */
int getBillRateTag (const char *rateName, string& bt_tag_str )
{
   int ifail = ITK_ok;
   tag_t query_tag    = NULLTAG;
   tag_t *results     = NULL;         // need to be freed
   char  **entries    = NULL;         // need to be freed
   char  **values     = NULL;        // need to be freed
   char  *other_entries[2];          // need to be freed
   char  *other_values[2];
   int   entry_count = 0;
   int   num_found   = 0;

   ITKCALL( QRY_find( "General..." , &query_tag ) );
   ITKCALL( QRY_find_user_entries( query_tag , &entry_count , &entries , &values ) );

   other_entries[0] = ( char* )MEM_alloc( ( tc_strlen( entries[0] ) + 1 ) * sizeof( char ) );
   tc_strcpy( other_entries[0] , entries[0] );
   other_values[0] = ( char* )MEM_alloc( ( tc_strlen( rateName ) + 1 ) * sizeof( char ) );
   tc_strcpy( other_values[0] , rateName );
   other_entries[1] = ( char* )MEM_alloc( ( tc_strlen( entries[2] ) + 1 ) * sizeof( char ) );
   tc_strcpy( other_entries[1] , entries[2] );
   other_values[1] = ( char* )MEM_alloc( ( tc_strlen( "RateModifier" ) + 1 ) * sizeof( char ) );
   tc_strcpy( other_values[1] , "RateModifier" );
   ITKCALL( QRY_execute( query_tag , 2 , other_entries , other_values , &num_found , &results ) );
   if(num_found>0)
   {
      char *tmpStr= NULL;
      ITKCALL (ifail = AOM_tag_to_string( results[0], &tmpStr ) );
      bt_tag_str = tmpStr;
      SAFE_SM_FREE(tmpStr);
   }

   if(entries)
   {
       MEM_free( entries );
       entries = NULL;
   }

   if( values )
   {
      MEM_free( values );
      values = NULL;
   }

   if( other_entries[0])
   {
     MEM_free( other_entries[0] );
     other_entries[0] = NULL;
   }

   if( other_values[0]  )
   {
      MEM_free( other_values[0] );
      other_values[0] = NULL;
   }

   if( other_entries[1]  )
   {
      MEM_free( other_entries[1] );
      other_entries[1] = NULL;
   }

   if( other_values[1]  )
   {
      MEM_free( other_values[1] );
      other_values[1] = NULL;
   }

   if(results )
   {
      MEM_free( results );
      results = NULL;
   }

   return ifail;
}



/*
 * getBillTypes
 */
int getBillTypes(const char *position, ManHourManageServiceImpl::ManHourInfo &mhinfo)
{
   int ifail = ITK_ok;
   char **option_values = NULL;
   char *option = NULL;
   int option_value_count = 0;

  // char tBuf1[128] = "";
 //  char tBuf2[128] = "";
 //  char tBuf3[128] = "";
  // char tBuf4[128] = "";

   char left[30] = {'\0'};
   char right[200] = {'\0'};
   ITKCALL(PREF_ask_char_values("YFJC_User_Rate_Mapping",&option_value_count,&option_values));
   for (int idx = 0; idx < option_value_count; idx ++)
   {
      option = *(option_values + idx);
      sscanf(option, "%[^=]=%[^=]", left,right );
      if (!tc_strcmp (left, (!tc_strcmp (position, "Manager") || 
                             !tc_strcmp (position, "Engineer Assistant") ||
                             !tc_strcmp (position, "ExtSupporter")) ? position : "General"))
      { 
         break;
      }
   }

   char tBuf[4][128];
   sscanf( right, "%[^{]{;}%[^{]{;}%[^{]{;}%[^{]", tBuf[0], tBuf[1], tBuf[2], tBuf[3]);

   char billTypeStr[30];
   char rateName[3];
   for (int idx = 0; idx < 4; idx ++)
   {
      sscanf(tBuf[idx], "%[^,],%s", billTypeStr,rateName );
   
      ManHourManageServiceImpl::ManHourBillType mheBillType;
      mheBillType.billTypeInternal = billTypeStr;
      mheBillType.billRateName = rateName;
      ITKCALL (ifail = getBillRateTag (rateName, mheBillType.myRefBR));
      mhinfo.theBillTypeSet.push_back(mheBillType);
   }

   SAFE_SM_FREE(option_values);
   return ifail;
}


/*
 * createManHourInfo
 */
int createManHourInfo (const char *username, const char *year, const char *month, ManHourManageServiceImpl::ManHourInfo &mhinfo)
{
   int ifail = ITK_ok;

   date_t myToday;
   char myYear[5];
   char myMonth[3];
 
  // tag_t userTag = NULLTAG;
   logical isEditable = TRUE;
   logical isHourlyUsr = FALSE;
   string myPosition = "";

   mhinfo.myUserName = username;
   if (year == NULL || month == NULL)
   {
      GUTIL_current_date (&myToday);
      sprintf (myYear, "%d", myToday.year);
      sprintf (myMonth, "%02d", myToday.month + 1);
      mhinfo.myYear = myYear;
      mhinfo.myMonth = myMonth;
   }
   else
   {
      mhinfo.myYear = year;
      mhinfo.myMonth = month;
   } 
   mhinfo.isHourlyBasedUser = FALSE;
   mhinfo.isManHourEditable = TRUE;
   mhinfo.totalDays = 0;

   ITKCALL (ifail = isManHourEditable (mhinfo.myYear.c_str(), mhinfo.myMonth.c_str(), &isEditable));
   mhinfo.isManHourEditable = isEditable;

   ITKCALL (ifail = isHourlyBasedUser (username, myPosition, &isHourlyUsr));
   mhinfo.isHourlyBasedUser = isHourlyUsr;
   mhinfo.myPosition = myPosition;

   int iYear = atoi(mhinfo.myYear.c_str());
   int iMonth = 0;
   char sMonth[10];
   tc_strcpy (sMonth, mhinfo.myMonth.c_str());
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

   ITKCALL (ifail = getMonthTmp (iYear, iMonth, mhinfo));

   ITKCALL (ifail = getPrograms (username, mhinfo));

   ITKCALL (ifail = getBillTypes(myPosition.c_str(), mhinfo));
   
   TC_write_syslog ("create ManHourInfo end \n");
   return ifail;
}



/*
 * rmManHourInfo
 */
int rmManHourInfo (const char *username, const char *year, const char *month)
{
   int ifail         = ITK_ok;
   int qryWsoCount   = 0;
   tag_t *qryWsoTags = NULL;

   ITKCALL(ifail = GCOMM_qry( QRY_MANHOUR_WITH_YEAR_MONTH, &qryWsoCount, &qryWsoTags, 3, username, year, month));

   if (qryWsoCount > 0)
   {
      for (int i=0; i<qryWsoCount; i++)
      {
         tag_t delTag = qryWsoTags[i];
         ITKCALL( ifail = AOM_lock_for_delete(delTag) );
         ITKCALL( ifail = AOM_delete(delTag) );
      }
   }

   SAFE_SM_FREE(qryWsoTags);
   return ifail;
}


#ifdef __cplusplus
}
#endif


