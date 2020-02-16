/*==========================================================================================================
Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
Unpublished - All Rights Reserved
============================================================================================================
File description:

Filename: yfjc_ebp_programinfo_property_sum.cxx
Module  : user_exits

import programInfo 
============================================================================================================
DATE           Name             Description of Change

8-Oct-2014    mengyawei       creation
============================================================================================================*/
#include <tc/tc.h>
#include "yfjc_ebp_head.h"
#include "yfjc_ebp_error.h"
#include <math.h>

#define ProgramInfoType             "JCI6_ProgramInfo"
#define ProgramInfoRevType          "JCI6_ProgramInfoRevision"

int  getPropertySum(METHOD_message_t* msg, va_list args)
{
	int ifail = ITK_ok;

	double value = 0.00;

	tag_t item_tag = NULLTAG;
	tag_t rev_tag = NULLTAG;

	char * prop_name = NULL;

	tag_t prop_tag = NULLTAG;
	double * prop_value =NULL;

	int rev_cnt = 0;
	tag_t * revs = NULL;
	tag_t tmp_rev = NULLTAG;
	double tmp_value = 0.00;
	char * budgetState = NULL;

	int i = 0;

	prop_tag = va_arg(args,tag_t );
    prop_value=va_arg(args, double*);
	

	ECHO("getPropertySum===========start \n");

	item_tag = msg->object_tag;

	ITKCALL(PROP_ask_name(prop_tag,&prop_name));

	ECHO(" prop_name = %s  \n" , prop_name );

	ITKCALL(ITEM_list_all_revs( item_tag , &rev_cnt ,&revs ));

	for( i = 0; i < rev_cnt ; i ++ )
	{
		tmp_rev = revs[i];
		
		ITKCALL(AOM_ask_value_double( tmp_rev , prop_name , &tmp_value ));

		ECHO("	value = %.3lf	tmp_value = %.3lf	\n" , value , tmp_value );

		ITKCALL(AOM_ask_value_string( tmp_rev , "jci6_BudgetState" , &budgetState ));
		ECHO("	jci6_BudgetState = %s\n" , budgetState );

		if( !tc_strcmp( budgetState , "State4" ) )
		{

			value += tmp_value ;
		}
		if( budgetState )
		{
			MEM_free( budgetState );
			budgetState = NULL;
		}
		//prop_value += 

		ECHO("11111 value = %.3lf \n" , value );
	}

	if( revs )
	{
		MEM_free( revs );
		revs = NULL;
	}


	if( prop_name )
	{
		MEM_free( prop_name );
		prop_name = NULL;
	}

	ECHO(" *prop_value = %.3lf \n" , value );
	*prop_value = value ;
	ECHO("getPropertySum===========end \n");

	return ifail;
}
