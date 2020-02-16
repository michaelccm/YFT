/*
#===========================================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#===========================================================================================
# File name: yfjc_ebp_getXSOGBGRateValue.cxx
# File description: 										   	
#===========================================================================================
#	Date			Name		Action	Description of Change					   
#	2013-3-07	zhangyn  		Ini		function for set JCI6_XSORevision runtime attribute								   
#===========================================================================================
 */
#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif

//SP-EXT-FUN-08.“XSO信息”版本上和门前通过率相关信息

extern int  yfjc_ask_jci6_DaysGap_value( METHOD_message_t *  message , va_list  args )
{
    tag_t prop_tag = va_arg( args, tag_t );
	int  *value    = va_arg( args, int * );
	/*****************************************/

	int ifail               = ITK_ok,
		gapday              = 0;

	tag_t item_revision     = NULLTAG,         
		  form              = NULLTAG; 

	date_t jci6_GateDate    = NULLDATE,
		   jci6_ReviewDate  = NULLDATE;

	METHOD_PROP_MESSAGE_OBJECT( message , item_revision ); 

	ITKCALL( AOM_ask_value_date( item_revision , JCI6_GATEDATE , &jci6_GateDate ) );
	ITKCALL( AOM_ask_value_date( item_revision , JCI6_REVIEWDATE , &jci6_ReviewDate ) );
	GapTime( jci6_GateDate , jci6_ReviewDate , &gapday );

	*value = gapday + 0;

	return ifail;
}

extern int  yfjc_ask_jci6_PassByGate_value( METHOD_message_t *  message, va_list  args )
{
    tag_t prop_tag = va_arg( args, tag_t );
	bool  *value    = va_arg( args, bool *);
	/*****************************************/

	int ifail                  = ITK_ok,
		statusNum              = 0;

    bool   get_logical         = false,
		   isPass              = false;

	char  *statusName          = NULL;

	tag_t item_revision        = NULLTAG,         
		  form                 = NULLTAG,
		 *release_status_list  = NULLTAG;

	date_t date_released       = NULLDATE,
		   jci6_GateDate       = NULLDATE,
                        jci6_ReviewDate       = NULLDATE;

	METHOD_PROP_MESSAGE_OBJECT( message , item_revision ); 

	ITKCALL( AOM_ask_value_tags( item_revision , RELEASE_STATUS_LIST , &statusNum , &release_status_list ) );


	for( int i = 0 ; i < statusNum ; i++ )
	{
		ITKCALL( AOM_ask_value_string( release_status_list[i] , OBJECT_NAME ,&statusName ) );
		if( tc_strcmp( statusName, JCI6_PASS ) ==0 )
		{
			ITKCALL( AOM_ask_value_date( release_status_list[i] , DATE_RELEASED ,&date_released ) );
			isPass = true;

			break;
		}
		if( statusName != NULL )
		{
			MEM_free( statusName );
			statusName = NULL;
		}
	}
	if( release_status_list != NULL )
	{
		MEM_free( release_status_list );
		release_status_list = NULL;
	}

	if(isPass)
	{
		ITKCALL( AOM_ask_value_date( item_revision , JCI6_GATEDATE , &jci6_GateDate ) );
                ITKCALL( AOM_ask_value_date( item_revision , JCI6_REVIEWDATE , &jci6_ReviewDate ) );
		get_logical = checktime( jci6_GateDate , jci6_ReviewDate );
	}

    if( statusName != NULL )
	{
		MEM_free( statusName );
		statusName = NULL;
	}

	*value = get_logical ;

	return ifail;
}

extern int  yfjc_ask_jci6_DelayDays_value( METHOD_message_t *  message , va_list  args )
{
    tag_t prop_tag = va_arg( args, tag_t );
	int  *value    = va_arg( args, int *);
	/*****************************************/

	int ifail                  = ITK_ok,
		statusNum              = 0,
		gapday                 = 0;

    bool  isPass               = false;

	char  *statusName          = NULL;

	tag_t item_revision        = NULLTAG,         
		  form                 = NULLTAG,
		  *release_status_list = NULLTAG; 

	date_t date_released       = NULLDATE,
		   jci6_GateDate       = NULLDATE,
		   monthLastDate       = NULLDATE;

	METHOD_PROP_MESSAGE_OBJECT( message , item_revision ); 

	ITKCALL( AOM_ask_value_tags( item_revision , RELEASE_STATUS_LIST , &statusNum , &release_status_list ) );

	for(int i = 0 ; i < statusNum ; i++)
	{
		ITKCALL( AOM_ask_value_string( release_status_list[i] , OBJECT_NAME , &statusName ) );
		if(tc_strcmp( statusName , JCI6_PASS ) ==0 )
		{
			ITKCALL( AOM_ask_value_date( release_status_list[i] , DATE_RELEASED , &date_released ) );
			isPass = true;

			break;
		}
		if( statusName != NULL)
		{
			MEM_free( statusName );
			statusName = NULL;
		}
	}
	if( release_status_list != NULL)
	{
		MEM_free( release_status_list );
		release_status_list = NULL;
	}

    ITKCALL( AOM_ask_value_date( item_revision , JCI6_GATEDATE , &jci6_GateDate ) );
	if( isPass )
	{
		GapTime( date_released , jci6_GateDate , &gapday );
	}else
	{
		getMonthLastDate(&monthLastDate);
		GapTime( monthLastDate , jci6_GateDate , &gapday );
	}

    if( statusName != NULL )
	{
		MEM_free( statusName );
		statusName = NULL;
	}

	*value = gapday + 0;

	return ifail;
}

#ifdef __cplusplus
}
#endif
