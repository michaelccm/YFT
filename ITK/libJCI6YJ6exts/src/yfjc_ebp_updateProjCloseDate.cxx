/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

    Filename: yfjc_ebp_updateProjCloseDate.cxx
    Module  : user_exits

============================================================================================================
DATE			     Name             Description of Change
25-Oct-2013          zhanggl		  creation
$HISTORY$
============================================================================================================*/
#include "yfjc_ebp_head.h"
#ifdef __cplusplus
extern "C" {
#endif

int JCI6_updateProjCloseDate( METHOD_message_t *msg, va_list args )
{
    int ifail                = ITK_ok,
        option_value_count  = 0;

    char **option_values    = NULL;

    tag_t   programinfo_rev_tag = NULLTAG;

    date_t end_date = NULLDATE;

    time_t now_time;
    tm *local_time;

    tag_t     prop_tag   = va_arg( args, tag_t );
    char*     prop_value = va_arg( args, char* );

    METHOD_PROP_MESSAGE_OBJECT( msg, programinfo_rev_tag );	
    if( ifail = Check_Preference( YFJC_PROGRAMSTATES_TOUPDATE_CLOSEDATE ) )
	{
		HANDLE_ERROR_S1( ifail, YFJC_PROGRAMSTATES_TOUPDATE_CLOSEDATE );
	}

    if( prop_value )
    {
        ITKCALL( PROP_assign_string( prop_tag, prop_value ) );
    }

	// tyl  2015/01/13 项目关闭冻结信息
		logical jci6_ActiveStatus = TRUE;
		ITKCALL(AOM_ask_value_logical(programinfo_rev_tag, "jci6_ActiveStatus", &jci6_ActiveStatus));
		if(!jci6_ActiveStatus)
		{
		return 0;
		}
		// tyl  2015/01/13 项目关闭冻结信息
    ITKCALL( PREF_ask_char_values( YFJC_PROGRAMSTATES_TOUPDATE_CLOSEDATE, &option_value_count, &option_values ) );
    for(int ipCnt = 0; ipCnt < option_value_count; ipCnt++)
    {
        if( tc_strcmp( option_values[ipCnt], prop_value ) == 0 )
        {
            time ( &now_time );
            local_time = localtime ( &now_time );

            end_date.year    = 1900 + local_time->tm_year;
            end_date.month   = local_time->tm_mon;
            end_date.day     = local_time->tm_mday;
            end_date.hour    = local_time->tm_hour;
            end_date.minute  = local_time->tm_min;
            end_date.second  = local_time->tm_sec;

            ITKCALL( AOM_set_value_date( programinfo_rev_tag, JCI6_ENDDATE, end_date ) );

            // 发送SAP
            //---------------------------------------------------------------------------
            break;
        }
    }

    MEM_free( option_values );
    option_values = NULL;

    return ifail;
}

 #ifdef __cplusplus
}
#endif