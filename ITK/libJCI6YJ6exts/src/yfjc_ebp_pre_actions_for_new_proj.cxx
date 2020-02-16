/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

    Filename: yfjc_ebp_pre_ctions_for_new_proj.cxx
    Module  : user_exits

============================================================================================================
DATE           Name             Description of Change
04-Mar-2013    zhanggl          creation
$HISTORY$

============================================================================================================*/
#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif

int Schedule_assign_project_pre_action( METHOD_message_t *msg, va_list args )
{
    int ifail             = ITK_ok,
        secondary_count   = 0;

    tag_t schedule_tag        = NULLTAG,
          currentProject      = NULLTAG,
          relation_type       = NULLTAG,
          old_project         = NULLTAG,
          *secondary_objects  = NULL;

    char object_type[WSO_name_size_c+1]  = "";

	ask_opt_debug();
    ECHO("*****************Schedule_assign_project_pre_action************************\n");

	schedule_tag = va_arg( args, tag_t ); 
    ECHO("schedule_tag = %u\n", schedule_tag);

	currentProject = va_arg( args, tag_t ); 
	ECHO("currentProject = %u \n", currentProject );

    if( schedule_tag && currentProject )
	{
        ITKCALL( AOM_ask_value_tag( schedule_tag, OWNING_PROJECT, &old_project ) );

        if( old_project )
        {
            ECHO("50提示信息\n" );
			//ifail=ERROR_SCHEDULE_ALREADY_EXISTS_IN_OTHER_PROJECTS;
            HANDLE_ERROR( ERROR_SCHEDULE_ALREADY_EXISTS_IN_OTHER_PROJECTS );
        }

        ITKCALL( GRM_find_relation_type( TC_PROGRAM_PREFERRED_ITEMS, &relation_type ) );

        if( relation_type )
        {
            ITKCALL( GRM_list_secondary_objects_only( currentProject, relation_type, &secondary_count, &secondary_objects ) );

            for( int ic = 0; ic < secondary_count; ic++ )
            {
                ITKCALL( WSOM_ask_object_type( secondary_objects[ic], object_type ) );

                if( tc_strcmp( object_type, SCHEDULE) == 0 )
                {
                    MEM_free( secondary_objects );
                    ECHO("56提示信息\n" );
					//ifail=ERROR_EXITSTING_SCHEDULE_OF_THE_PROJECT;
                    HANDLE_ERROR( ERROR_EXITSTING_SCHEDULE_OF_THE_PROJECT );
                }
            }
        }
    }

    MEM_free( secondary_objects );

    ECHO("**************************************************************************\n");

    return ifail;
}

#ifdef __cplusplus
}
#endif
