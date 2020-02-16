/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: yfjc_ebp_getXSOReviewStatusValue.h
# File description: 										   	
#=============================================================================
#	Date			Name		Action	Description of Change					   
#	2013-3-11	zhangyn  		Ini		function for set ReviewStatus value									   
#=============================================================================
 */
#include "yfjc_ebp_head.h"

//SP-EXT-FUN-09.“XSO信息”版本的审核状态

#ifdef __cplusplus
extern "C" {
#endif

extern int  yfjc_ask_jci6_ReviewStatus_value( METHOD_message_t *  message , va_list  args )
{
	int ifail               = ITK_ok,
		status_list_num     = 0,
		gapMonthDay         = 0,
		gapNextMonthDay     = 0;

    char *statusName        = NULL,
         reviewstatus[2],
         **value;

	tag_t item_revision          = NULLTAG,         
		  form                   = NULLTAG,
		 *release_status_list    = NULLTAG,
         prop_tag                = NULLTAG; 

    va_list largs;

	date_t jci6_ReviewDate       = NULLDATE,
		   nowDate               = NULLDATE,
		   monthLastDate         = NULLDATE,
		   nextMonthhLastDate    = NULLDATE;

    va_copy( largs , args );
	prop_tag  = va_arg( largs , tag_t );
	value = va_arg( largs , char** );
    va_end( largs );
    *value = NULL;

    tc_strcpy ( reviewstatus , FIVE );

	METHOD_PROP_MESSAGE_OBJECT( message , item_revision ); 

	ITKCALL( AOM_ask_value_tags( item_revision , RELEASE_STATUS_LIST , &status_list_num , &release_status_list ) );
	for( int i = 0 ; i < status_list_num ; i++)
	{
		ITKCALL( AOM_ask_value_string( release_status_list[i] , OBJECT_NAME , &statusName ) );
       
		if( tc_strcmp( statusName , JCI6_PASS ) == 0)
		{
			break;
		}
		else if( tc_strcmp( statusName , JCI6_FAIL ) == 0)
		{
			break;
		}
        if( statusName != NULL )
	    {
		    MEM_free( statusName );			
		    statusName = NULL;
	    }		
	}

	ITKCALL( AOM_ask_value_date( item_revision , JCI6_REVIEWDATE , &jci6_ReviewDate ) );

	getNowTime( &nowDate );

	getMonthLastDate( &monthLastDate );

	getNextMonthhLastDate( &nextMonthhLastDate );

	GapTime( monthLastDate , jci6_ReviewDate , &gapMonthDay );

	GapTime( nextMonthhLastDate , jci6_ReviewDate , &gapNextMonthDay );

	if( ( status_list_num == 0 || tc_strcmp( statusName , JCI6_FAIL ) == 0 ) && jci6_ReviewDate.year != 0 && gapMonthDay >= 0 )
	{
		tc_strcpy ( reviewstatus , ONE );
	}
	else if( tc_strcmp( statusName , JCI6_PASS) == 0 )
	{
		tc_strcpy ( reviewstatus , TWO );
	}
	else if( ( status_list_num == 0 || tc_strcmp( statusName , JCI6_FAIL ) == 0 ) && jci6_ReviewDate.year == 0 )
	{
		tc_strcpy ( reviewstatus , THREE );
	}
	else if((status_list_num == 0 || tc_strcmp(statusName, JCI6_FAIL ) == 0 ) && gapNextMonthDay > 0 )
	{
		tc_strcpy ( reviewstatus , FOUR );
	}
	else if( ( status_list_num == 0 || tc_strcmp( statusName , JCI6_FAIL ) == 0 ) && gapNextMonthDay < 0 )
	{
		tc_strcpy ( reviewstatus, FIVE );
	}

	if( release_status_list != NULL )
	{
		MEM_free( release_status_list );			
		release_status_list = NULL;
	}

    if( statusName != NULL )
    {
	    MEM_free( statusName );			
	    statusName = NULL;
    }

	*value = MEM_string_copy( reviewstatus );

	return ifail;
}


#ifdef __cplusplus
}
#endif