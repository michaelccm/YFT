/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

    Filename: list_division.cxx
    Module  : Extension

============================================================================================================
DATE           Name             Description of Change
07-Mar-2013    zhanggl          creation
$HISTORY$
============================================================================================================*/

#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif

int custom_tag_lov( METHOD_message_t *msg, va_list  args ) 
{
	int status           = ITK_ok,
		allDivisionCnt   = 0;

	char lov_name[LOV_name_size_c + 1] = "";

	TAGLIST *head = NULL,
		    *node = NULL;

	head = ( TAGLIST* )MEM_alloc( NODELEN );
	head->ds_tag = NULL;
	head->next = NULL;

	/* va_list for LOV_ask_values_msg */
	tag_t lov_tag  = va_arg( args, tag_t ); 
	int*  n_values = va_arg( args, int* ); 
	tag_t **values = va_arg( args, tag_t** );

	ITKCALL( LOV_ask_name( lov_tag, lov_name ) );

	if( tc_strcmp( lov_name, JCI6_DIVISION_LOV ) == 0 )
	{
		if( ( status = list_division( head, &allDivisionCnt ) ) != ITK_ok )
		{
			goto CUSTOMOUT;
		}
	}
	else if( tc_strcmp( lov_name, JCI6_SECTION_LOV ) == 0 )
	{
		if( ( status = list_section( head, &allDivisionCnt )) != ITK_ok )
		{
			goto CUSTOMOUT;
		}
	}
    else if( tc_strcmp( lov_name, JCI6_SMTELEADER_LOV ) == 0 )
	{
		if( ( status = listAllSMTELeader( head, &allDivisionCnt )) != ITK_ok )
		{
			goto CUSTOMOUT;
		}
	}
    else
        goto LOVOUT;

	*n_values = allDivisionCnt; 
	*values = (tag_t *)MEM_alloc( ( *n_values + 1 ) * sizeof( tag_t ) );
	//printf("\t n_values = %d\n", *n_values );

	for( node = head->next, allDivisionCnt = 0; node != NULL; node = node->next )
	{
		(*values)[allDivisionCnt] = node->ds_tag;
		allDivisionCnt++;
	}

CUSTOMOUT:

	freelist( head );

LOVOUT:

	return status;
}


 





#ifdef __cplusplus
}
#endif