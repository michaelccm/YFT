/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: yfjc_ebp_custom_main.cxx
   Module  : user_exits

   Load the custom library(ies) registered in the preference file and
   Call the entry point function pointer to register custom exits
============================================================================================================
DATE           Name             Description of Change
04-Mar-2011    zhanggl		    creation
$HISTORY$
============================================================================================================*/

#include "yfjc_ebp_server_exits_custom.h"
#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif

DLLAPI int libJCI6YJ6exts_register_callbacks()
{
	int ifail = ITK_ok;

	printf("*******************************************************************************\n");
	printf("*           libYfjc_function_register_callbacks  is starting                  *\n");
	printf("*******************************************************************************\n");

    //Call USERSERVICE_custom_register_handlers for register custom handlers 
    
    fprintf(stdout,"libJCI6YJ6exts_register_callbacks !\n");

	ITKCALL( ifail = CUSTOM_register_exit("libJCI6YJ6exts","USER_init_module",
		(CUSTOM_EXIT_ftn_t)USERSERVICE_custom_register_handlers) );
		
		if(ifail==ITK_ok){
		 fprintf(stdout,"libJCI6YJ6exts_register_callbacks OK!\n");
		}else{
		 fprintf(stdout,"libJCI6YJ6exts_register_callbacks Failed!\n");
		}

	ITKCALL( ifail = CUSTOM_register_exit( "libJCI6YJ6exts", "USERSERVICE_register_methods",
		(CUSTOM_EXIT_ftn_t)USERSERVICE_custom_register_methods) );



	//modify by wuwei-- USERSERVICE_register_methods
    ITKCALL( ifail = CUSTOM_register_exit( "libJCI6YJ6exts", "USERSERVICE_register_methods", 
		(CUSTOM_EXIT_ftn_t)JCI6checkOverDueLogTime) );
	if(ifail==ITK_ok){
		printf("regest ===JCI6checkOverDueLogTime=== OK  \n");
	}

	//modify by wuwei--USERSERVICE_register_methods
    ITKCALL( ifail = CUSTOM_register_exit( "libJCI6YJ6exts", "USERSERVICE_register_methods", 
		(CUSTOM_EXIT_ftn_t)JCI6preActionFillInfo) );
	if(ifail==ITK_ok){
		printf("regest ===JCI6preActionFillInfo=== OK  \n");
	}

	//modify by wuwei--USERSERVICE_register_methods
    ITKCALL( ifail = CUSTOM_register_exit( "libJCI6YJ6exts", "USERSERVICE_register_methods", 
		(CUSTOM_EXIT_ftn_t)JCI6TimesheetMinAlert) );
	if(ifail==ITK_ok){
		printf("regest ===JCI6TimesheetMinAlert=== OK  \n");
	}

	printf("*******************************************************************************\n");
	printf("*             End of libYfjc_function_register_callbacks                      *\n");
	printf("*******************************************************************************\n");

	return ifail;
}


#ifdef __cplusplus
}
#endif
