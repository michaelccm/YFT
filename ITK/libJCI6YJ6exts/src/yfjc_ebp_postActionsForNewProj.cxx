/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

    Filename: yfjc_postActionsForNewProj.cxx
    Module  : user_exits

============================================================================================================
DATE           Name             Description of Change
04-Mar-2013    zhanggl          creation
$HISTORY$
11-Mar-2013    zhanggl          modify
12-Apr-2013    zhanggl          modify JCI6Schedule_assign_project to post-action
13-Apr-2013    zhanggl          modify JCI6postActionsForNewProj to post-action
============================================================================================================*/
#include "yfjc_ebp_head.h"


#ifdef __cplusplus
extern "C" {
#endif

int JCI6Schedule_assign_project( METHOD_message_t *msg, va_list args )
{
    int ifail      = ITK_ok;

    tag_t schedule_tag    = NULLTAG,
          currentProject  = NULLTAG,
          relation_type   = NULLTAG,
          relation        = NULLTAG;

	ask_opt_debug();
    ECHO("*****************JCI6Schedule_assign_project************************\n");

	schedule_tag = va_arg( args, tag_t ); 
    ECHO("schedule_tag = %u\n", schedule_tag);

	currentProject = va_arg( args, tag_t ); 
	ECHO("proj_tag = %u \n", currentProject );

    if( schedule_tag && currentProject )
	{
        ITKCALL( GRM_find_relation_type( TC_PROGRAM_PREFERRED_ITEMS, &relation_type ) );

        if( relation_type )
        {
            ITKCALL( GRM_create_relation( currentProject, schedule_tag, relation_type ,NULLTAG, &relation ) );

            if( relation )
            {
                ITKCALL( GRM_save_relation( relation ) );
                ITKCALL( AOM_unlock( relation ) );
            }
        }

        ITKCALL( AOM_lock( schedule_tag ) );
        ITKCALL( AOM_set_value_tag( schedule_tag, OWNING_PROJECT, currentProject ) );
        ITKCALL( AOM_save( schedule_tag ) );
        ITKCALL( AOM_unlock( schedule_tag ) );
    }

    ECHO("**************************************************************************\n");

    return ITK_ok;
}

 // tyl 2015/01/13 创建项目的同时并发送邮件
int sendEmailToProjectTL(tag_t item_tag,tag_t user_tag)
{
    int ifail=ITK_ok;
	tag_t envelope;
	char* programInfoItemId;
	char* programInfoItemName;
	char envelopeContent[1000] = "";
	char envelopeTitle[64] = "";
	tag_t person_tag;
	char* email_address;
			 
    ITKCALL(ifail = AOM_ask_value_string(item_tag, "item_id", &programInfoItemId));
	ITKCALL(ifail = AOM_ask_value_string(item_tag, "object_name", &programInfoItemName));
	ECHO("programInfoItemId===%s\n",programInfoItemId);
	ECHO("programInfoItemName===%s\n",programInfoItemName);
	strcpy(envelopeContent, programInfoItemId);
	strcat(envelopeContent, "   ");
	strcat(envelopeContent, programInfoItemName);
	//strcpy(envelopeTitle, "项目已创建");
	strcpy(envelopeTitle, "Create New Project Successful");
	
	ITKCALL (SA_ask_user_person( user_tag , &person_tag ));
	ITKCALL (SA_ask_person_email_address( person_tag , &email_address ));
	ECHO("email_address = %s\n" , email_address );	
	ECHO("envelopeContent===%s\n",envelopeContent);
	ITKCALL(ifail =MAIL_create_envelope(envelopeTitle,envelopeContent,&envelope));
	if((email_address== NULL)||(tc_strlen(email_address) == 0))
	{
    ITKCALL(ifail =MAIL_add_envelope_receiver(envelope,user_tag));
	}else{
	ITKCALL(MAIL_add_external_receiver( envelope , MAIL_send_to , email_address ));
	}
    ITKCALL(ifail =MAIL_send_envelope(envelope));
			
	if( programInfoItemId )
	{
		MEM_free( programInfoItemId );
	}
    if( programInfoItemName )
	{
	    MEM_free( programInfoItemName );
	}  
       return ifail;		
}
 // tyl 2015/01/13 创建项目的同时并发送邮件
 
int JCI6postActionsForNewProj( METHOD_message_t *msg, va_list args )
{
	int ifail         = ITK_ok;

	char *category_value      = NULL,
		 *templatePath        = NULL;

	bool isNull;

	tag_t item_tag          = NULLTAG,
          rev_tag           = NULLTAG,
		  template_tag      = NULLTAG,
		  project_tag       = NULLTAG,
		  project_team      = NULLTAG,
          jci6_res_tag      = NULLTAG,
          res_in_tag        = NULLTAG;

	va_list local_args;

	BMF_extension_arguments_t *input_args = NULL;

    ask_opt_debug();
	ECHO("**************************************************************************\n");
	ECHO("*                JCI6postActionsForNewProj is comming           %s         *\n",getNowtime2().c_str());
	ECHO("**************************************************************************\n");

	/*USERARG_get_tag_argument( &project_tag ); 
	USERARG_get_tag_argument( &item_tag );
    USERARG_get_tag_argument( &res_in_tag );*/
	item_tag = va_arg( args, tag_t ); 
    ECHO("schedule_tag = %u\n", item_tag );

	project_tag = va_arg( args, tag_t ); 
	ECHO("proj_tag = %u \n", project_tag );

	if( !item_tag || !project_tag )
	{
		goto FOUT;
	}

	// Find team leader
	ITKCALL( AOM_ask_value_tag( project_tag, PROJECT_TEAM, &project_team ) );
	if( project_team )
	{
		int num_of_members = 0;
		char role_name[SA_name_size_c + 1] = "";
		tag_t *member_tags = NULL,
			  role_tag     = NULLTAG;

		ITKCALL( SA_find_groupmembers_by_group( project_team, &num_of_members, &member_tags ) );
		for(int jn = 0; jn < num_of_members; jn++)
		{
			ITKCALL( SA_ask_groupmember_role( member_tags[jn], &role_tag ) );
			ITKCALL( SA_ask_role_name( role_tag, role_name ) );
			if( tc_strcmp( role_name, JCI6_PROJECT_TEAMROLE ) == 0 )
			{
				ITKCALL( SA_ask_groupmember_user( member_tags[jn], &res_in_tag ) );
				break;
			}
		}

		MEM_free( member_tags );
		member_tags = NULLTAG;
	}

	if( ifail = Check_Preference( YFJC_PROJECT_REPOSITORY_TEMPLATE_LOCATION ) )
	{
		HANDLE_ERROR_S1( ifail, YFJC_PROJECT_REPOSITORY_TEMPLATE_LOCATION);
	}

    ITKCALL( ITEM_ask_latest_rev( item_tag, &rev_tag ) );
	ITKCALL( AOM_ask_value_string( rev_tag, JCI6_CATEGORY, &category_value ) );

	ECHO("JCI6_CATEGORY = %s\n", category_value);
	if( category_value )
	{
		if( ( ifail = filter_preferences( YFJC_PROJECT_REPOSITORY_TEMPLATE_LOCATION, 
            category_value, &templatePath) ) != ITK_ok )
		{
			goto FOUT;
		}

		ECHO("templatePath = %s\n", templatePath);
		if( templatePath )
		{
			ITKCALL( POM_string_to_tag( templatePath, &template_tag ) );
            if( template_tag )
            {
                char root_folder_name[WSO_name_size_c + 1] = "",
                     project_id[PROJ_id_size_c + 1]        = "";
				tag_t new_folder_tag		=	NULLTAG;
                char * src_folder_type      = NULL;

                ITKCALL( PROJ_ask_id( project_tag, project_id ) );
                sprintf( root_folder_name, "%s-Program Repository", project_id );
				//ITKCALL( FL_create( root_folder_name, "" , &new_folder_tag) );
                //ITKCALL( AOM_save( new_folder_tag ) );
				//ITKCALL( AOM_unlock( new_folder_tag ) );

                ITKCALL( AOM_ask_value_string( template_tag,"object_type",&src_folder_type));
                createFolder( root_folder_name,src_folder_type,  &new_folder_tag);
								MEM_free(src_folder_type);
                if( ( ifail = folder_deep_copy( new_folder_tag, &project_tag, &template_tag) ) )
                {
                    goto FOUT;
                }

				ECHO("开始复制模板文件夹:  %d\n",new_folder_tag);
				copyAcl(template_tag,new_folder_tag);
            }
        }

        ITKCALL( AOM_ask_value_tag( rev_tag, JCI6_PROJECTTL, &jci6_res_tag ) );
	
		ECHO("JCI6_PROJECTTL = %d\n", jci6_res_tag);
		ECHO("res_in_tag = %d\n", res_in_tag);

		//test by wuwei
       if( !jci6_res_tag && res_in_tag)
        {  
            ITKCALL( AOM_lock( rev_tag ) );
            ITKCALL( AOM_set_value_tag( rev_tag, JCI6_PROJECTTL, res_in_tag ) );
            ITKCALL( AOM_save( rev_tag ) );
            ITKCALL( AOM_unlock( rev_tag ) );
			sendEmailToProjectTL(item_tag,res_in_tag);
        }else
		{
			if(jci6_res_tag)
			{
				sendEmailToProjectTL(item_tag,jci6_res_tag);
			}
		}
	}

FOUT:
	
	if( category_value )
	{
		MEM_free( category_value );
		category_value = NULL;
	}

	if( templatePath )
	{
		MEM_free( templatePath );
		templatePath = NULL;
	}

	ECHO("*******************************************************\n");
	ECHO("*          JCI6postActionsForNewProj is end     %s       *\n",getNowtime2().c_str());
	ECHO("*******************************************************\n");

	return ifail;
}












#ifdef __cplusplus
}
#endif