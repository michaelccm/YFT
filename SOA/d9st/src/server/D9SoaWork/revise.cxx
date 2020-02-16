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


extern int POM_AM__set_application_bypass(logical bypass);


/* 
 * deleteTSETag ()
 */
int deleteTSETag (tag_t tse_tag)
{
   int ifail = ITK_ok;

   int referenceNum = 0;
   int* levelList = NULL;
   char** relationList = NULL;
   tag_t* referenceList = NULLTAG;


   POM_AM__set_application_bypass(true);

   ITKCALL( ifail = WSOM_where_referenced(tse_tag, 1, &referenceNum, &levelList, &referenceList, &relationList) );
   for(int i = 0; i < referenceNum; i++)
   {
      ITKCALL (ifail = AOM_refresh(referenceList[i], true));
      ITKCALL (ifail = AOM_delete( referenceList[i]));
   }

   ITKCALL (ifail = AOM_refresh(tse_tag, true));
   ITKCALL( ifail = AOM_delete(tse_tag) );

   SAFE_SM_FREE(levelList);
   SAFE_SM_FREE(relationList);
   SAFE_SM_FREE(referenceList);

   POM_AM__set_application_bypass(false);

   return ifail;
}


/*
 * reivseTimeSheetEntry
 */
int reviseTimeSheetEntry (const char* revise_wf_name, tag_t tse_tag, logical *is_failed)
{
   int ifail = ITK_ok;

   char  wfName[128]      = {0};
   char  wfTemplate[128]  = {0};
   tag_t wfTemplateTag    = null_tag;
   tag_t wfTags[1];
   int   wfTagAttType[1];
   tag_t newProcess       = null_tag;

   *is_failed = false;

   sprintf(wfName, "%s", revise_wf_name);
   sprintf(wfTemplate, "%s", TM_REVISE_WF );

   wfTags[0] = tse_tag;
   wfTagAttType[0] = EPM_target_attachment;

   ITKCALL( EPM_find_process_template( wfTemplate, &wfTemplateTag ) );
   if (wfTemplateTag == null_tag)
   {
      TC_write_syslog( "reviseTimeSheetEntry template tag is null\n");
      *is_failed = true;
      return ITK_ok;
   }

   ITKCALL (ifail = EPM_create_process( wfName, "Triggered by manhour", wfTemplateTag, 1,
                                        wfTags, wfTagAttType, &newProcess ));

   if (newProcess == null_tag)
   {
      TC_write_syslog( "reviseTimeSheetEntry process tag is null\n");
      *is_failed = true;
      return ITK_ok;
   }

   string status = "";
   ITKCALL ( ifail = checkTSEStatus(tse_tag, status));

   if (status.size () ==0)    
   {
      ITKCALL (ifail = deleteTSETag (tse_tag));
   }

   return ifail;
}


/*
 * deleteMHETag
 */
int deleteMHETag (ManHourManageService::ManHourEntry &mhe)
{
   int ifail = ITK_ok;
   tag_t mheTag = null_tag;

   POM_AM__set_application_bypass(true);

   ITKCALL( ifail = POM_string_to_tag( mhe.myRefMHE.c_str(), &mheTag ) );
   if (mheTag != null_tag)
   {
      ITKCALL (ifail = AOM_refresh(mheTag, true));
      ITKCALL( ifail = AOM_delete(mheTag) );
   }
   mhe.workingHours = "";
   mhe.tseStatus = "Working";
   mhe.myRefTE = "Null";
   mhe.myRefMHE = "Null";

   POM_AM__set_application_bypass(false);

   return ifail;
}


/*
 * doRevise
 */
int doRevise (const char *username, 
              const char *year, 
              const char *month,
              const vector<ManHourManageService::ManHourEntry> &mhe_vector,
              ManHourManageService::ManHourEntrySet &mhe_set)
{
   int ifail = ITK_ok;

   printf("year:%s month-->%s username:%s\n",year,month,username);


   for (int i = 0; i < mhe_vector.size (); i ++)
   {
      ManHourManageService::ManHourEntry mhe = mhe_vector [i];
      string myRefTE = mhe.myRefTE;
      string tseStatus = mhe.tseStatus;

      if (!tc_strcmp (tseStatus.c_str(), "Released"))   
      {
         tag_t tseTag = null_tag;
         ITKCALL( ifail = POM_string_to_tag( mhe.myRefTE.c_str(), &tseTag ) );
         if (tseTag != null_tag)
         {
            logical isFailed = false;
            char wfName[128] = {'\0'};

            sprintf(wfName, "%s_%s_%s_Revise", mhe.myPrjId.c_str(), mhe.myPrjName.c_str(), mhe.myUserName.c_str());
            ITKCALL ( ifail = reviseTimeSheetEntry (wfName, tseTag, &isFailed ));
            if (!isFailed)
            {
               ITKCALL (ifail = deleteMHETag(mhe));
            }
         }
      }
      else if (!tc_strcmp (tseStatus.c_str(), "Failed"))
      {
         ITKCALL (ifail = deleteMHETag (mhe));
      }

      mhe_set.mheSet.push_back (mhe);
   }

   return ifail;
}


#ifdef __cplusplus
}
#endif
