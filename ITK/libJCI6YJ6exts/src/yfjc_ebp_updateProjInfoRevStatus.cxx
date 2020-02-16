/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

    Filename: yfjc_updateProjInfoRevStatus.cxx
    Module  : Extension

============================================================================================================
DATE           Name             Description of Change
10-Mar-2013    zhanggl          creation
$HISTORY$
23-Oct-2013    zhanggl          modify:If the preference values contained in the project type, 
                                       whether the current project is compound of this type. True:change status
                                                                                             False:don't change
3-Dec-2013     zhanggl          modify:If the project is inactive, do nothing.
============================================================================================================*/

#include "yfjc_ebp_head.h"
#include "yfjc_ebp_custom_define.h"

#ifdef __cplusplus
extern "C" {
#endif

int JCI6_updateProjInfoRevStatus( void * returnValue ) 
{
	int ifail                  = ITK_ok,
		pointCnt               = 0,
		iCnt                   = 0;

	char schedule_type[ITEM_type_size_c + 1]  = "",
         *prop_value                          = NULL,
         **pppStringList                      = NULL;

	tag_t schedule_task_tag         = NULLTAG;

    ask_opt_debug();
	ECHO("**************************************************************************\n");
	ECHO("*                JCI6_updateProjInfoRevStatus is comming                 *\n");
	ECHO("**************************************************************************\n");

    USERARG_get_string_argument( &prop_value ); 
	USERARG_get_tag_argument( &schedule_task_tag );
    ECHO("prop_value = %s\n", prop_value);

    if( prop_value )
    {
        ITKCALL( EPM__parse_string( prop_value, ".", &pointCnt, &pppStringList ) );
    }

	{
		int objects_Cnt    = 0;

		 char object_type[WSO_name_size_c+1] = "", 
			  *project_ids                   = NULL,
              *category                      = NULL;

		tag_t *proj_assidned_objects  = NULL,
			  schedule_tag            = NULLTAG,
			  programInfo_rev         = NULLTAG;

		ITKCALL( AOM_ask_value_tag( schedule_task_tag, SCHEDULE_TAG, &schedule_tag ) );

		ITKCALL( AOM_ask_value_string( schedule_tag, PROJECT_IDS, &project_ids ) );

		if( project_ids )
		{
            char one_id[32] = "";
            sscanf( project_ids, "%[^,],", one_id );
			ITKCALL( PROJ_ask_assigned_objects( one_id, &objects_Cnt, &proj_assidned_objects ) );
			MEM_free( project_ids );

            tag_t project_tag = NULLTAG;
            logical is_active = FALSE;
            ITKCALL(PROJ_find(one_id, &project_tag));
            if( project_tag )
            {
                ITKCALL(PROJ_is_project_active(project_tag, &is_active));
                if( !is_active )
                {
                    goto FOUT;
                }
            }
		}

		for( iCnt = 0; iCnt < objects_Cnt; iCnt++ )
		{
			ITKCALL( WSOM_ask_object_type( proj_assidned_objects[iCnt], object_type ) );

			if( tc_strcmp( object_type, JCI6_PROGRAMINFO ) == 0 )
			{
				ITKCALL( ITEM_ask_latest_rev( proj_assidned_objects[iCnt], &programInfo_rev ) );

				if( programInfo_rev )
				{
				
				// tyl  2015/01/13 项目关闭冻结信息
		        logical jci6_ActiveStatus = TRUE;
		        ITKCALL(AOM_ask_value_logical(programInfo_rev, "jci6_ActiveStatus", &jci6_ActiveStatus));
		        if(!jci6_ActiveStatus)
		       {
			   ECHO("%s\n","项目非活动");
		       goto PFREE;
		       }
		         // tyl  2015/01/13 项目关闭冻结信息

					 POM_AM__set_application_bypass( TRUE ) ;

					ITKCALL( AOM_lock( programInfo_rev ) );

                    if( pointCnt == 1 )
                    {
					    ITKCALL( AOM_set_value_string( programInfo_rev, JCI6_PROGRAMSTATE, prop_value ) );
                    }
                    else if( pointCnt == 2 )
                    {
                        ITKCALL( AOM_ask_value_string( programInfo_rev, JCI6_CATEGORY, &category ) );
                        if( category && tc_strcmp( category, pppStringList[0] ) == 0 )
                        {
                            ITKCALL( AOM_set_value_string( programInfo_rev, JCI6_PROGRAMSTATE, pppStringList[1] ) );
                        }

                        MEM_free( category );
                        category = NULL;
                    }

					ITKCALL( AOM_save( programInfo_rev ) );

					ITKCALL( AOM_unlock( programInfo_rev ) );

                     POM_AM__set_application_bypass( FALSE ) ;
				}
				else
				{
					EMH_store_initial_error( EMH_severity_error, ERROR_NO_FIND_LATEST_PROGRAMINFOREV );
					goto PFREE;
				}
				break;
			}
		}
PFREE:
		MEM_free( proj_assidned_objects );
	}

FOUT:

    if( pppStringList )
    {
        MEM_free( pppStringList );
        pppStringList = NULL;
    }
	ECHO("**************************************************************\n");
	ECHO("*            JCI6_updateProjInfoRevStatus is end             *\n");
	ECHO("**************************************************************\n");

	return ifail;
 }

#ifdef __cplusplus
}
#endif