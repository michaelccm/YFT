/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: yfjc_ebp_automatic_calculation_EQU.cxx
   Module  : Extension

============================================================================================================
DATE           Name             Description of Change
16-Mar-2013    xiesq		    creation
$HISTORY$
08-Apr-2013    zhanggl          Judge release type
============================================================================================================*/
#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif

int GTAC_ask_double_value( METHOD_message_t *msg, va_list args )
{
	int      ifail				   = ITK_ok,
		     rev_num			   = 0;

	double   rev_EQU               = 0,  
			 *prop_value,
		     EQU                   = 0;

	tag_t    item_tag              = NULLTAG,
		     *revision_list        = NULL, 
             prop_tag              = va_arg(args,tag_t );

	char     *valtype_n            = NULL;

    PROP_value_type_t       valtype;

	ask_opt_debug();

	item_tag = msg->object_tag;
	
	ITKCALL( PROP_ask_value_type( prop_tag, &valtype,&valtype_n ) );

	prop_value = va_arg( args, double* ); 
 
	ITKCALL( AOM_ask_value_tags( item_tag, "revision_list" ,&rev_num ,&revision_list ) );

	ECHO("rev_num = %d\n", rev_num);
	for(int i = 0; i < rev_num; i++ )
	{
		ITKCALL( AOM_ask_value_double( revision_list[i], "jci6_EQU", &rev_EQU ) );
		char *budgetState	=NULL;
		ITKCALL(AOM_ask_value_string(revision_list[i],"jci6_BudgetState",&budgetState));
		if(tc_strcmp(budgetState,"State4")==0)
		{
			EQU = EQU + rev_EQU;
		}
		if( budgetState )
		{
			MEM_free( budgetState );
			budgetState = NULL;
		}
	}
	*prop_value = EQU;
	if(revision_list)
	{
		MEM_free( revision_list );
		revision_list = NULL;
	}

	return ifail;
}
#ifdef __cplusplus
}
#endif
