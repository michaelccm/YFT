/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: yfjc_deleteCostInfo_Handler.cxx
   Module  : Handler

============================================================================================================
DATE           Name             Description of Change
26-Apr-2013    xiesq		    creation
$HISTORY$
============================================================================================================*/

#include "yfjc_ebp_head.h"
#include "yfjc_ebp_error.h"


int yfjc_deleteCostInfo_Handler(EPM_action_message_t msg)
{
	tag_t  root_task         = NULLTAG,
		   *attachments      = NULL,
		   costInfo          = NULLTAG,
		   *ProgramInfo_rev  = NULLTAG,
		   ProgramInfo       = NULLTAG,
		   *rev_list         = NULL,
		   *CostInfo         = NULL,
		   relation_type     = NULLTAG,
		   relation          = NULLTAG;

	int		ifail			 = ITK_ok,
			n_attachments    = 0,
			n_ProgramInfoRev = 0,
			n_CostInfo       = 0,
			n_rev            = 0;  

	char    *type_name       = NULL;
	
	if( msg.action != 105)
	{
		return ifail;
	}
	ECHO("msg.action = %d\n", msg.action);
	POM_AM__set_application_bypass( TRUE );

	ITKCALL( EPM_ask_root_task( msg.task, &root_task ) );

	ITKCALL( EPM_ask_attachments( root_task, EPM_target_attachment, &n_attachments, &attachments ) );

	ITKCALL( EPM_remove_attachments( root_task, n_attachments, attachments ) );

	ITKCALL( GRM_find_relation_type( "IMAN_external_object_link", &relation_type ) );

	if(n_attachments !=0 )
	{
        for (int ii = 0; ii < n_attachments; ii++)
	    {	
            type_name = ( char * ) MEM_alloc( WSO_name_size_c + 1 );

	        AOM_ask_value_string( attachments[ii], "object_type", &type_name );

		    if ( tc_strcmp(type_name, "JCI6_ProgramInfoRevision") == 0)
		    {
			    ProgramInfo_rev = ( tag_t * ) MEM_realloc ( ProgramInfo_rev,( n_ProgramInfoRev+1 ) *sizeof( tag_t ) );

			    ProgramInfo_rev[ n_ProgramInfoRev ] = attachments[ii];

			    n_ProgramInfoRev ++;
		    }
			MEM_free( type_name );
	    }
	}
	if( n_ProgramInfoRev != 0 )
	{
		for ( int ii = 0; ii < n_ProgramInfoRev; ii ++ )
	    {
		    ITKCALL( ITEM_ask_item_of_rev( ProgramInfo_rev[ii], &ProgramInfo ) );

		    ITKCALL( ITEM_list_all_revs( ProgramInfo, &n_rev, &rev_list ) );
		
		    if( ProgramInfo_rev[ii] != rev_list[0] )
		    {
			    ITKCALL( AOM_ask_value_tags( ProgramInfo_rev[ii], "IMAN_external_object_link", &n_CostInfo,&CostInfo ) );	

			    if( n_CostInfo != 0 )
			    {
				    for( int jj = 0; jj < n_CostInfo; jj++)
				    {		
					    ITKCALL( GRM_find_relation( ProgramInfo_rev[ii], CostInfo[jj], relation_type, &relation ) );

					    ITKCALL( GRM_delete_relation( relation ) );

					    ITKCALL( AOM_delete( CostInfo[jj] ) );
				    }
			    }			
			    ITKCALL( AOM_delete( ProgramInfo_rev[ii] ) );
		    }	
	    }
		if( ProgramInfo_rev ) MEM_free( ProgramInfo_rev );
	}
	 POM_AM__set_application_bypass( FALSE ) ;
	ECHO("----------------------------------------------yfjc_deleteCostInfo_Handler_end\n");

	return ifail;
}