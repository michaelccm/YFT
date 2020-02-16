/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: yfjc_ebp_importhyperion.cxx
# File description: 										   	
#=============================================================================
#	Date						Name		Action	Description of Change					   
#	2013-3-1					liqz  			Ini		function for hyperion import
#   14-Apr-2013     zhanggl     modify		 create itemRevision 
#	 25-Jun-2013			liqz		modify		active project if it is unactive
#=============================================================================
 */

#include "yfjc_ebp_head.h"
#include "yfjc_ebp_custom_define.h"

/**
	importhyperion
*/
int importhyperion( void * returnValueType )
{
	int ifail       = ITK_ok,
		costInfoLen = 0,
		i           = 0;

	char ** costInfoContent = NULL,
		 *object_name       = NULL,
		 *programid         = NULL,
		 *program_revid     = NULL,
		 *jci_cpt           = NULL,
		 *jci_year          = NULL,
		 *jci_costtype      = NULL,
		 *jci_monthprop     = NULL,
		 *costvalue         = NULL;

	tag_t release_status = NULLTAG,
		  item_tag       = NULLTAG,
		  rev_tag        = NULLTAG,
		  relation_tag   = NULLTAG,
		  project_tag    = NULLTAG,
		  new_rev_tag    = NULLTAG;

    ECHO("importhyperion    start .........\n");

	//cost info properties
	ifail=USERARG_get_string_array_argument(&costInfoLen,&costInfoContent);

	ITKCALL(GRM_find_relation_type( IMAN_EXTERNAL_OBJECT_LINK,&relation_tag));
	ITKCALL(CR_create_release_status(RELEASEDSTATUS,&release_status));
	ITKCALL(AOM_lock(release_status));
	ITKCALL(AOM_load(release_status));
	ITKCALL(AOM_save(release_status));
	ITKCALL(AOM_unlock(release_status));

	ask_opt_debug();

	for(i = 0; i < costInfoLen; i++ )
    {
		rev_tag = NULLTAG;
		item_tag= NULLTAG;
		project_tag = NULLTAG;

		object_name = tc_strtok(costInfoContent[i],VERTICALBAR);
		programid =tc_strtok(NULL,VERTICALBAR);
		program_revid =tc_strtok(NULL,VERTICALBAR);
		jci_cpt = tc_strtok(NULL,VERTICALBAR);
		jci_year = tc_strtok(NULL,VERTICALBAR);
		jci_costtype = tc_strtok(NULL,VERTICALBAR);

        POM_AM__set_application_bypass( TRUE ) ;

		ITKCALL(PROJ_find(programid,&project_tag));

        ECHO("project_tag===%u\n",project_tag);
        
		if(project_tag == NULLTAG)
		{
			continue;
		}
		if(!tc_strstr(program_revid,"NULL"))
		{
			ITKCALL( ITEM_find_rev( programid, program_revid, &rev_tag ) );

            ECHO("rev_tag===%u\n",rev_tag);
			//Add by zhanggl 14-Apr
			if( !rev_tag )
			{
				ITKCALL( ITEM_find_item( programid, &item_tag ) );
				ITKCALL( ITEM_ask_latest_rev( item_tag, &rev_tag ) );
				ITKCALL( ITEM_copy_rev( rev_tag, program_revid, &new_rev_tag ) );
				/*int count = 0;
				tag_t *objects = NULL;
				ITKCALL( ITEM_perform_deepcopy( new_rev_tag, ITEM_revise_operation, 
                rev_tag, &count, &objects) );
				MEM_free( objects );*/
				ITKCALL( AOM_save ( new_rev_tag ) );
				ITKCALL( AOM_unlock ( new_rev_tag ) );
				rev_tag = new_rev_tag;
			}
			ITKCALL( AOM_lock(rev_tag) );
			ITKCALL(AOM_set_value_string(rev_tag,"jci6_PDxSeq",program_revid));
			ITKCALL( AOM_save(rev_tag) );
			ITKCALL( AOM_unlock(rev_tag) );
			// End
		}
		else
		{
			//需要把费用信息挂在项目信息上
			ITKCALL(ITEM_find_item(programid,&item_tag));
		}
        POM_AM__set_application_bypass( FALSE ) ;

		ECHO("object_name= %s\t programid= %s\t program_revid = %s\t jci_cpt = %s\t jci_year=%s\t jci_costtype=%s\t \n",
			object_name,programid,program_revid,jci_cpt,jci_year,jci_costtype);

		tag_t cost_tag;
		createCostInfo(object_name,&cost_tag);

		ITKCALL(AOM_lock ( cost_tag ));
		ITKCALL(AOM_set_value_string(cost_tag,JCI6_CPT,jci_cpt));
		ITKCALL(AOM_set_value_string(cost_tag,JCI6_COSTTYPE,jci_costtype));
		ITKCALL(AOM_set_value_int(cost_tag,JCI6_YEAR,atoi(jci_year)));
		ITKCALL(AOM_set_value_string(cost_tag,JCI6_UNIT,"Yuan"));

		char * tempvalue = tc_strtok(NULL,VERTICALBAR);
		do
		{
			if(tempvalue){
				char jci_month[64] = "",
					jci_monthvalue[256] = "";
				sscanf(tempvalue, "%[^=]=%s", jci_month, jci_monthvalue);
				ECHO("jci_month = %s; \t value = %s\n",jci_month,jci_monthvalue);
				ITKCALL(AOM_set_value_double(cost_tag, jci_month,strtod(jci_monthvalue,NULL)));
			}
			tempvalue =tc_strtok(NULL,VERTICALBAR);
		}while(tempvalue);

		ITKCALL(AOM_save ( cost_tag ));
		ITKCALL(AOM_unlock ( cost_tag ));

		ITKCALL(EPM_add_release_status(release_status,1,&cost_tag,NULL));

       POM_AM__set_application_bypass( TRUE ) ;

		if(rev_tag)
		{
			ITKCALL(ITEM_attach_rev_object_tag(rev_tag,cost_tag,relation_tag));
		}
		else
		{
			ITKCALL( ITEM_attach_object_tag( item_tag, cost_tag, relation_tag ) );
		}

		// assign cost info to project 
		if(project_tag !=  NULLTAG)
		{
				logical active = false;
				ITKCALL(PROJ_is_project_active(project_tag,&active));
				if( !active )
				{
					ITKCALL(AOM_lock ( project_tag ));
					ITKCALL(PROJ_activate_project(project_tag,true));
					ITKCALL(AOM_save ( project_tag ));
					ITKCALL(AOM_unlock ( project_tag ));
				}
                AOM_refresh(cost_tag,1);
                AOM_refresh(project_tag,1);
				PROJ_assign_objects(1,&project_tag,1,&cost_tag);
                ITKCALL(AOM_save ( cost_tag ));
				ITKCALL(AOM_save ( project_tag ));
                AOM_refresh(cost_tag,0);
                AOM_refresh(project_tag,0);

				if( !active )
				{
					ITKCALL(AOM_lock ( project_tag ));
					ITKCALL(PROJ_activate_project(project_tag,false));
					ITKCALL(AOM_save ( project_tag ));
					ITKCALL(AOM_unlock ( project_tag ));
				}
		}

        POM_AM__set_application_bypass( FALSE ) ;
	}
    
    ECHO("importhyperion    end .........\n");

	MEM_free(costInfoContent);
	return 0;
}
