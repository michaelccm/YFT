/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: yfjc_ebp_auto_assign_PKR.cxx
   Module  : Handler

============================================================================================================
DATE           Name             Description of Change
12-Mar-2013    liujm		    creation
$HISTORY$
============================================================================================================*/

#include "yfjc_ebp_head.h"
#include "yfjc_ebp_error.h"

#ifdef __cplusplus
extern "C" {
#endif

static logical isDebug = FALSE;

int  yfjc_ebp_auto_assign_PKR_Handler( EPM_action_message_t msg )
{
	int customError	= ITK_ok, 
        args_cnt    = 0, 
        ia          = 0;

	char *trans_args_value = NULL,
         *para_flag	       = NULL,	
         *para_value	   = NULL;

	TC_write_syslog("*******************************************************************************\n");
	TC_write_syslog("*              yfjc_auto_assign_PKR_Handler is comming £¡                      *\n");
	TC_write_syslog("*******************************************************************************\n");

	args_cnt = TC_number_of_arguments( msg.arguments );
	TC_init_argument_list( msg.arguments );
    char task1[50] = "",
         task2[50] = "";
	for( ia = 0; ia < args_cnt; ia++ )
	{
		trans_args_value = TC_next_argument( msg.arguments );
		ITKCALL( ITK_ask_argument_named_value( trans_args_value, &para_flag, &para_value ));

		if( tc_strcmp( para_flag, "debug" ) == 0 )
		{
			if( tc_strcasecmp( para_value, "true" ) == 0 )
			{
				isDebug = TRUE;
			}
		}
        
        if( tc_strcmp( para_flag, "tasks" ) == 0 )
		{
            char *newStr = NULL; 
            
            ITKCALL(STRNG_replace_str(para_value,"£»",";",&newStr));
            
            sscanf( newStr, "%[^;];%s", task1,task2 );

            MEM_free( newStr );
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
    if(isDebug)
          TC_write_syslog("task1==%s,task2==%s\n",task1,task2);
	if( (customError = do_auto_assign_PKR( msg.task,task1,task2 ) ) != ITK_ok)
	{
		TEST_ERROR(customError)
	}

	TC_write_syslog("*******************************************************************************\n");
	TC_write_syslog("*               yfjc_auto_assign_PKR_Handler is end £¡                          *\n");
	TC_write_syslog("*******************************************************************************\n");

	return customError;
}




#ifdef __cplusplus
}
#endif