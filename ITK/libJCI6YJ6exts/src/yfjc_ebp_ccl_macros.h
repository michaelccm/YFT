/*=============================================================================
                    Copyright(c) 2009 Siemens PLM Software Corp. All rights reserved.
                             Unpublished - All rights reserved
===============================================================================
File description:

    Filename: common_macros.h

    This file describes functions for common macro.

===============================================================================
Date                  Name                Description of Change
10-Mar-2009      Lee, Yu-Chin      creation
30-Mar-2009      Lee, Yu-Chin      add DEBUG_LOG() function definition
08-Sep-2009      Lee, Yu-Chin      rename common_macros.h to ccl_macros.h
$HISTORY$
=============================================================================*/

#ifndef CCL_MACROS_H
#define CCL_MACROS_H
#endif

#include <tc/emh.h>
#include <tc/tc.h>

// define debug mode
static logical debug = false;
// rolllback markpoint
static int markpoint = 0;
// rolllback return
static logical roll_ok; 

// define function DEBUG_LOG()
#define DEBUG_LOG(x) TC_write_syslog(x); printf(x);

/*
    function name : CALL()
    description: print out error message to syslog file and console window, then return error code
*/
#define CALL(x)   \
{   \
    int stat;   \
    char *err_string = NULL;   \
    if((stat = (x)) != ITK_ok)   \
    {    \
        EMH_ask_error_text (stat, &err_string);   \
        TC_write_syslog("[ERROR] %d ERROR MSG: %s.\n", stat, err_string);   \
        TC_write_syslog("[ERROR] Function: %s FILE: %s LINE: %d\n",#x, __FILE__, __LINE__);   \
        printf("[ERROR] %d ERROR MSG: %s.\n", stat, err_string);   \
        printf("[ERROR] Function: %s FILE: %s LINE: %d\n",#x, __FILE__, __LINE__);   \
        if (err_string != NULL)   \
            MEM_free (err_string);   \
        if (markpoint != 0)   \
        {   \
            POM_roll_to_markpoint(markpoint, &roll_ok);   \
            POM_forget_markpoint(markpoint);   \
            TC_write_syslog("[ERROR] Rollback to markpoint %d\n", markpoint);   \
            printf("[ERROR] Rollback to markpoint %d\n", markpoint);   \
        }   \
        return (stat);   \
    }   \
}