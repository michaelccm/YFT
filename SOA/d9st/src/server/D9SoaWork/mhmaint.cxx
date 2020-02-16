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
 * delCostInfoTag
 */
int delCostInfoTag (tag_t cost_info_tag)
{
   int ifail = ITK_ok;

   int referenceNum = 0;
   int* levelList = NULL;
   char** relationList = NULL;
   tag_t* referenceList = NULLTAG;

   char  refTypeName[TCTYPE_name_size_c+1] = "\0";
   tag_t refTypeTag = NULLTAG;

   tag_t relTypeTag = NULLTAG;
   tag_t relTag     = NULLTAG;


   POM_AM__set_application_bypass(true);

   ITKCALL( ifail = WSOM_where_referenced(cost_info_tag, 1, &referenceNum, &levelList, &referenceList, &relationList) );
   
   for(int i = 0; i < referenceNum; i++)
   {
      refTypeName[0] = '\0';
      ITKCALL (ifail = TCTYPE_ask_object_type (referenceList[i], &refTypeTag));
      ITKCALL (ifail = TCTYPE_ask_name (refTypeTag, refTypeName));

      if (!tc_strcmp (refTypeName, "Folder"))      // for Folder
      {
         ITKCALL (ifail = AOM_refresh(referenceList[i], TRUE));
         ITKCALL (ifail = FL_remove (referenceList[i], cost_info_tag));
         ITKCALL (ifail = AOM_save (referenceList[i]));
         ITKCALL (ifail = AOM_refresh(referenceList[i], FALSE));
      }
      else 
      {
         ITKCALL (ifail = GRM_find_relation_type (relationList[i], &relTypeTag));
         ITKCALL (ifail = GRM_find_relation (referenceList[i], cost_info_tag, relTypeTag, &relTag));
         ITKCALL (ifail = GRM_delete_relation (relTag));
      }
   }

   ITKCALL (ifail = AOM_refresh(cost_info_tag, true));
   ITKCALL( ifail = AOM_delete(cost_info_tag) ); 

   SAFE_SM_FREE(levelList);
   SAFE_SM_FREE(relationList);
   SAFE_SM_FREE(referenceList);

   POM_AM__set_application_bypass(false);

   return ifail;
}


/*
 * doDelete
 */
int doDelete (tag_t del_folder_tag)
{
   TC_write_syslog ("doDelete start \n");
   int ifail = ITK_ok;

   char refType[WSO_name_size_c+1] = "\0";
   char refName[WSO_name_size_c+1] = "\0";
   int refCount   = 0;
   tag_t *refTags = NULL;

   ITKCALL (ifail = FL_ask_references (del_folder_tag, FL_fsc_by_name, &refCount, &refTags));

   if (refCount <= 0)
   {
      return ifail;
   }

   for (int idx = 0; idx < refCount; idx ++)
   {
      refType[0] = '\0';
      refName[0] = '\0';

      ITKCALL ( ifail = WSOM_ask_object_type (refTags [idx], refType));
      ITKCALL ( ifail = WSOM_ask_name (refTags [idx], refName));

      if (!tc_strcmp (refType, "JCI6_CostInfo"))
      {
         ITKCALL ( ifail =  delCostInfoTag (refTags [idx]));
      }
   }
 
//EXIT:
   SAFE_SM_FREE (refTags);
   TC_write_syslog ("doDelete end \n");
   return ifail;
}


/*
 * rmCostInfo
 */
int rmCostInfo (const char *username, const char *fl_name)
{
   int ifail       = ITK_ok;
   tag_t userTag   = null_tag;
   tag_t homeFlTag = null_tag;
   tag_t delFlTag  = null_tag;
   tag_t *refTags  = NULL;
   int refCount    = 0;

   ITKCALL( ifail = SA_find_user(username, &userTag ) );
   ITKCALL( ifail = SA_ask_user_home_folder( userTag, &homeFlTag ) );
   ITKCALL( ifail = FL_ask_references( homeFlTag, FL_fsc_no_order, &refCount, &refTags ) );

   for (int i=0; i<refCount; i++)
   {
      tag_t typeTag = null_tag;
      char typeName[TCTYPE_name_size_c+1] = "\0";

      ITKCALL( ifail = TCTYPE_ask_object_type( refTags[i], &typeTag ) );
      ITKCALL( ifail = TCTYPE_ask_name (typeTag, typeName));
		
      if (tc_strcmp (typeName, "Folder") == 0)
      {
         char wsoName[WSO_name_size_c + 1] = "\0";
         ITKCALL( ifail = WSOM_ask_name( refTags[i], wsoName ) );

         if (tc_strcmp( fl_name, wsoName ) == 0)
         {
            delFlTag = refTags[i];
            break;
         }
      }
   }

   if (delFlTag != NULLTAG)
   {
      ITKCALL (ifail = doDelete (delFlTag));
   }
   else
   {
      TC_write_syslog ("The folder Delete is not created in the home.\n");
   }

//EXIT:
   SAFE_SM_FREE( refTags );
   return ifail;
}

#ifdef __cplusplus
}
#endif

