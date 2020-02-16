/*
#==========================================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#==========================================================================================
# File name: yfjc_ebp_setXSODefaultDateValue.cxx
# File description: 										   	
#==========================================================================================
#	Date			Name		Action	Description of Change					   
#	2013-3-08	zhangyn  		Ini		function for set JCI6_XSORevision DefaultDateValue
#==========================================================================================
 */
#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif

//SP-EXT-FUN-10. “XSO信息”版本审核日期默认值
int JCI6setXSODefaultDateValue( METHOD_message_t* message, va_list args )
{
    char  rev_type[ITEM_type_size_c + 1],
		  option_name[BMF_EXTENSION_STRGVAL_size_c + 1],
		  *rev_id           = NULL;

	int err         = ITK_ok,
        ifail       = ITK_ok,
		paramCount  = 0,
        revisionNum = 0;


	BMF_extension_arguments_t *input_args = NULL;

	date_t jci6_ReviewDate   = NULLDATE, 
		   finish_date       = NULLDATE;

	tag_t jci6_Gate          = NULLTAG,
          item               = NULLTAG,
          *revisions         = NULL,
		  item_rev           = message->object_tag,
          prop_tag           = va_arg( args, tag_t ),
          jci6_Gate_rev      = va_arg( args, tag_t );
    
    ITKCALL( AOM_lock( item_rev ) );
    ITKCALL( PROP_assign_tag( prop_tag, jci6_Gate_rev ) );
	ITKCALL( AOM_save( item_rev ) );
	ITKCALL( AOM_unlock( item_rev ) );
	ITKCALL( AOM_refresh( item_rev ,true ) );

	ITKCALL( AOM_ask_value_tag( item_rev , ITEMS_TAG , &item ) );
    ITKCALL( AOM_ask_value_tags( item , REVISION_LIST , &revisionNum , &revisions ) );

    if( item_rev != revisions[0] )        
    {
        if( revisions != NULL )
	    {
		    MEM_free( revisions );			
		    revisions = NULL;
	    }
        return ifail;
    }
    if( revisions != NULL )
	{
		MEM_free( revisions );			
		revisions = NULL;
	}

	ITKCALL( AOM_ask_value_date( item_rev , JCI6_REVIEWDATE , &jci6_ReviewDate ) );
	if( jci6_ReviewDate.year != 0)
	{
		return ifail;
	}

	if( jci6_Gate_rev == NULLTAG )
	{
        char * item_id = NULL;

		ITKCALL( AOM_ask_value_string( item_rev , ITEM_ID , &item_id ) );

		EMH_store_error_s1( EMH_severity_user_error , ERROR_Gate_NOT_FOUND ,item_id );

        err = ERROR_Gate_NOT_FOUND ;

		if( item_id != NULL )
		{
			MEM_free( item_id );			
			item_id = NULL;
		}
		return err;
	}
    ITKCALL(AOM_ask_value_tag( jci6_Gate_rev , ITEMS_TAG , &jci6_Gate ) );
	ITKCALL(AOM_ask_value_date( jci6_Gate , FINISH_DATE , &finish_date ) );

	//ifail = BMF_get_user_params( message , &paramCount , &input_args ); 

	//if (ifail == ITK_ok && paramCount == 1) 
	{
		char *option_value      = NULL;
		int  before_date        = 14;

        tc_strcpy( option_name , YFJC_XSO_Dates_Before_Gate );
		//tc_strcpy( option_name , input_args[0].arg_val.str_value );
		//-------------load option
		ITKCALL( PREF_ask_char_value( option_name , 0 , &option_value ) );
		if( option_value == NULL || tc_strcmp( option_value, "") == 0 )
		{
            TC_write_syslog( "请在option中配置选项%s！\n" , option_name );
			before_date = 14;	
		}else
		{
			before_date = atoi( option_value );
		}

		GetBeforeDate( finish_date , before_date , &jci6_ReviewDate );

		ITKCALL( AOM_lock( item_rev ) );
		ITKCALL( AOM_set_value_date( item_rev , JCI6_REVIEWDATE , jci6_ReviewDate ) );
		ITKCALL( AOM_save( item_rev ) );
		ITKCALL( AOM_unlock( item_rev ) );
		ITKCALL( AOM_refresh( item_rev ,true ) );

		if( option_value != NULL )
		{
			MEM_free( option_value );			
			option_value = NULL;
		}
	}

	if( input_args != NULL )
	{
		MEM_free( input_args );			
		input_args = NULL;
	}

	return ifail;
}

#ifdef __cplusplus
}
#endif