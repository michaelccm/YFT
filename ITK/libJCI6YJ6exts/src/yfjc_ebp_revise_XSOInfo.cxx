/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: yfjc_revise_XSOInfo.cxx
   Module  : Handler

   Revise XSOInfo
============================================================================================================
DATE           Name             Description of Change
23-Feb-2013    zhanggl		    creation
$HISTORY$
============================================================================================================*/

#include "yfjc_ebp_head.h"
#include "yfjc_ebp_error.h"

#ifdef __cplusplus
extern "C" {
#endif

static logical isDebug = FALSE;

int yfjc_revise_XSOInfo_Handler( EPM_action_message_t msg )
{
	int customError	  = ITK_ok, 
        args_cnt      = 0, 
        ia            = 0;

	char *trans_args_value	= NULL,	 
         *para_flag      	= NULL,
         *para_value	    = NULL;

    ask_opt_debug();
	ECHO("*******************************************************************************\n");
	ECHO("*              yfjc_revise_XSOInfo_Handler is comming £¡                      *\n");
	ECHO("*******************************************************************************\n");
    
	args_cnt = TC_number_of_arguments( msg.arguments );
	TC_init_argument_list( msg.arguments );

	for( ia = 0; ia < args_cnt; ia++ )
	{
		trans_args_value = TC_next_argument( msg.arguments );
		ITKCALL( ITK_ask_argument_named_value( trans_args_value, &para_flag, &para_value ));

		if( tc_strcmp( para_flag, YJ6_DEBUG_STR ) == 0 )
		{
			if( tc_strcasecmp( para_value, YJ6_TRUE ) == 0 )
			{
				isDebug = TRUE;
			}
		}

		if( para_flag != NULL )
		{
			MEM_free( para_flag );
			para_flag = NULL;
		}

		if( para_value != NULL )
		{
			MEM_free( para_value );
			para_value = NULL;
		}
	}

	if( (customError = do_revise_XSOInfo( msg.task ) ) != ITK_ok)
	{
		TEST_ERROR(customError)
	}

	ECHO("*******************************************************************************\n");
	ECHO("*              yfjc_revise_XSOInfo_Handler is end £¡                          *\n");
	ECHO("*******************************************************************************\n");

	return customError;
}



#ifdef __cplusplus
}
#endif