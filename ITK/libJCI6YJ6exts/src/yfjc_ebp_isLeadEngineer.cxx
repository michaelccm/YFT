/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: yfjc_ebp_isLeadEngineer.cxx
   Module  : Extension

============================================================================================================
DATE           Name             Description of Change
16-Mar-2013    xiesq		    creation
$HISTORY$
============================================================================================================*/


#include "yfjc_ebp_head.h"

int isLeadEngineer( METHOD_message_t *m, va_list args )
{
	int	  ifail               = ITK_ok,
		  projectNum          = 0,
		  num                 = 0;

	tag_t JCI6_PFARevision    = NULLTAG,
		  programInfo_rev     = NULLTAG,
		  programInfo         = NULLTAG,
	      jci6_ProjectTL      = NULLTAG,
		  item_tag            = NULLTAG,
		  *project_lists      = NULL,
		  user                = NULLTAG;

	char  *project_ids        = NULL,
		  *user_name_string   = NULL;

	bool  is_programInfo      = false;

	METHOD_PROP_MESSAGE_OBJECT( m, JCI6_PFARevision);

	ITKCALL( AOM_ask_value_tags(JCI6_PFARevision,"project_list", &projectNum, &project_lists ) );

	if(projectNum == 0)
	{
		HANDLE_ERROR( ERROR_NO_PROJECT );
	}
	else if(projectNum > 0)
	{	
		int   preferred_ItemNum  = 0;
        tag_t *preferred_Items   = NULL;

        ITKCALL( AOM_ask_value_tags( project_lists[0], "project_data", &preferred_ItemNum, &preferred_Items ) );

		for(int i=0;i<preferred_ItemNum;i++)
        {
			char object_type[WSO_name_size_c+1] = "";

            ITKCALL(WSOM_ask_object_type(preferred_Items[i],object_type));

            if(tc_strcmp(object_type,"JCI6_ProgramInfo")==0)
			{
				is_programInfo = true;

				ITKCALL( ITEM_ask_latest_rev( preferred_Items[i], &programInfo_rev ) );

				ITKCALL( AOM_ask_value_tag( programInfo_rev, JCI6_PROJECTTL, &jci6_ProjectTL ) );

				POM_get_user(&user_name_string, &user);

				if( jci6_ProjectTL != user )
				{
					HANDLE_ERROR( ERROR_NOT_LEADER_ENGINEER );
				}
			}
		}	
		if( is_programInfo == false)
		{
			HANDLE_ERROR( ERROR_NO_PROGRAMINFO );
		}
		MEM_free( preferred_Items );
	}

	MEM_free( project_lists );

	return ifail;
}