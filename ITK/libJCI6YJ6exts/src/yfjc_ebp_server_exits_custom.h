/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename:yfjc_ebp_server_exits_custom.h

   Function declarations
       
	   (I)      indicates input argument
	   (O)      indicates output argument
       (OF)     indicates output argument which should be passed to POM_free after use

   Declaration:
       
	   Functions head.
============================================================================================================
DATE           Name             Description of Change
04-Mar-2013    zhanggl          creation
$HISTORY$
============================================================================================================*/

#include <tc/iman.h>
#include <tccore/custom.h>
#include <tccore/item_msg.h>
#include <server_exits/user_server_exits.h>
#include <tc/tc.h>
#include <tc/tc_macros.h>

#ifndef SERVER_EXITS_CUSTOM
#define SERVER_EXITS_CUSTOM

#ifdef __cplusplus
extern "C" {
#endif
	
extern DLLAPI int USERSERVICE_custom_register_handlers( int *decision, va_list args );	
extern DLLAPI int USERSERVICE_custom_register_methods( int *decision, va_list args );

#ifdef __cplusplus
}
#endif

#endif 
