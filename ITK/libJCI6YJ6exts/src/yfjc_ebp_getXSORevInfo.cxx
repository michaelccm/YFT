
/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: yfjc_ebp_getXSORevInfo.cxx.
   Module  : Extension

============================================================================================================
DATE           Name             Description of Change
16-Mar-2013    xiesq		    creation
$HISTORY$
============================================================================================================*/

#include "yfjc_ebp_head.h"

int getXSORevInfo( METHOD_message_t *msg, va_list args )
{
	tag_t   prop_tag            = va_arg( args,tag_t ),
			rev_prop_tag	    = NULLTAG,
	        jci6_Gate           = NULLTAG,
			Gate                = NULLTAG,
			item_tag            = NULLTAG,
			user                = NULLTAG,
			*revision_list      = NULL,
			*fit_revs           = NULL;

	int		ifail               = ITK_ok,
			rev_num	            = 0,
			pref_num            = 0,
			name_num            = 0,
			rev                 = 0,
			date_status         = 0,
			fit_revs_num        = 0;

	char    *disp_name			= NULL,
		    **pref_value		= NULL,
			**name		        = NULL,
			**pref_values		= NULL,
			*prop_name          = NULL,
			*men				= NULL,
			*jci6_TaskType      = NULL,
			*rev_status         = NULL,
			**prop_value_str    = NULL,
			*valtype_n          = NULL,
		    *user_name_string   = NULL,
			rev_prop_name[20]   = "",
			rev_prop_names[20]	= "jci6_";

	bool    isEqual             = false;

	date_t  *last_mod_date      = NULL,
		    *prop_value_date	= NULL,
		    rev_date	        = NULLDATE;

	logical  verdict            = FALSE;

	PROP_value_type_t           valtype;

	item_tag = msg->object_tag;
	
	ITKCALL( PROP_ask_value_type( prop_tag, &valtype,&valtype_n ) );

	if( valtype == PROP_date )
	{
		prop_value_date = va_arg( args, date_t * ); 
	}
	else
	{
		prop_value_str = va_arg( args, char ** ); 
	}

	name = ( char ** )MEM_alloc( sizeof( char* ) *3 );

	for( int i = 0; i < 3;i ++ )
	{
		name[i] = ( char* )MEM_alloc( sizeof( char ) *20 );
	}

	PROP_UIF_ask_name( prop_tag,&disp_name );

	PROP_ask_name( prop_tag,&prop_name );
	
	char *point = tc_strtok( disp_name,"_" );

	tc_strcpy( name[0], point );

	while(  ( point = tc_strtok( NULL,"_") ) != NULL)
	{  
		tc_strcpy(name[name_num+1], point );

		name_num ++;
	}


	ITKCALL(PREF_ask_char_values( "YFJC_Gate_Types_Mapping",&pref_num,&pref_value ) );
	
	men = ( char * )MEM_alloc( sizeof( char ) *20 );

	for( int i = 0; i < pref_num; i ++ )
	{
		int m = 0;

		pref_values = ( char ** )MEM_alloc( sizeof( char* ) *2 );

		for( int i = 0; i < 2;i ++)
		{
			pref_values[i] = ( char* )MEM_alloc( sizeof( char ) *20);
		}

		char *point2 = tc_strtok( pref_value[i],"=" );

	    tc_strcpy( pref_values[0], point2 );

		while( ( point2 = tc_strtok(NULL,"=" ) ) != NULL )
		{  
			tc_strcpy(pref_values[m+1], point2 );

			m ++;
		}

		if( tc_strcmp( pref_values[0],name[0] ) == 0 )
		{
			tc_strcpy( men, pref_values[1] );
		}

		for(int i = 0; i < 2;i++)
		{
			MEM_free( pref_values[i] );
		}
			MEM_free( pref_values );
	}


	ITKCALL( AOM_ask_value_tags( item_tag,"revision_list",&rev_num,&revision_list ) );

	last_mod_date = ( date_t* )MEM_alloc( sizeof( date_t ) *rev_num );

	for( int i = 0; i<rev_num; i ++ )
	{
		ITKCALL( AOM_ask_value_date( revision_list[i],"last_mod_date",&last_mod_date[i] ) );

		ITKCALL( AOM_ask_value_tag( revision_list[i],"jci6_Gate",&jci6_Gate ) );

	    POM_get_user( &user_name_string, &user );

		ITKCALL(AM_check_users_privilege(user,jci6_Gate,"READ",&verdict));

		if( verdict )
		{
	        ITEM_ask_item_of_rev( jci6_Gate,&Gate);

			ITKCALL( AOM_ask_value_string( Gate,"jci6_TaskType",&jci6_TaskType ) );

			if( tc_strcmp( jci6_TaskType,men ) == 0 )
			{
				fit_revs = ( tag_t * )MEM_realloc( fit_revs,( fit_revs_num+1 ) *sizeof( tag_t ) );

				fit_revs[fit_revs_num] = revision_list[i];

				isEqual = true;

				fit_revs_num ++;
			}
		}
	}


	if( isEqual )
	{
		for( int i = 0; i < fit_revs_num; i++ )
		{
			for( int j = i+1; j< fit_revs_num; j++ )
			{
				bool check = checktime( last_mod_date[i],last_mod_date[j] );

                if( check )
				{
					tag_t tag = fit_revs[j];

					fit_revs[j] = fit_revs[i];

					fit_revs[i] = tag;
				}
			}
		}

	    rev = atoi( name[1] );  

		if( rev <= fit_revs_num )
		{
			tc_strcpy( rev_prop_name,name[2] );

			tc_strcat( rev_prop_names,rev_prop_name );

			date_status = tc_strcmp( rev_prop_name,"ReviewDate" );

			if( date_status == 0 )
			{
				ITKCALL( AOM_ask_value_date(fit_revs[rev-1], rev_prop_names, &rev_date) );

				*prop_value_date = rev_date ;

			}
			else
			{
				ITKCALL( AOM_ask_value_string(fit_revs[rev-1], rev_prop_names, &rev_status ) );
				
				*prop_value_str = rev_status ;
			}
		}
	}

	for(int i = 0; i < 3; i++)
	{
		MEM_free( name[i] );
	}
	MEM_free( name );

	MEM_free( men );
    MEM_free( last_mod_date );
	MEM_free( fit_revs );
	MEM_free( revision_list );

	return ifail;
}



