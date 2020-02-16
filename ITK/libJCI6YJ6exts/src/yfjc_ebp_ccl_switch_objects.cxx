/*==================================================================================================
 Copyright(c) 2005 UGS Corp. All rights reserved.
 Unpublished - All rights reserved
 ====================================================================================================
 File description:
 Filename: ccl_switch_objects.c

 This file describe action handler function : CCL-switch-objects

 ====================================================================================================
 Date             Name                  Description of Change
 3-Jun-2009       Cai,Pluto             creation
 08-Sep-2009     Lee, Yu-Chin       change handler name
 11-Sep-2009   Cai, Pluto            change comment to English
 $HISTORY$
 ==================================================================================================*/

#include "yfjc_ebp_head.h"
#include "yfjc_ebp_error.h"

#pragma warning(disable:4996)
#define DEBUG "-debug="
#define ARGUMENT_TYPE "-type="
#define FROM "-from="
#define TO "-to="
#define STATUS "-status="
#ifndef STRU2L
#define STRU2L( str ) \
{ \
	int i=0; \
	int m; \
	m=strlen(str);   \
	for (i=0;i<m;i++){ \
		if (str[i]>=65&&str[i]<=90) \
			str[i]+=32; \
	} \
}
#endif

static logical retain_release_date = false;



int CCL_switch_objects(EPM_action_message_t msg)
{
	int rcode = ITK_ok;
	char *err_string, *err_function;
	int err_line = -1;
	tag_t job = NULLTAG;
	tag_t root_task = NULLTAG;

	int property_count = 0, i = 0, j = 0, iTypeFind = 0, typeFind = 0, iStatusFind = 0, statusFind = 0, count = 0, k =
			0;
	char args_tmp[400];
	int iDebug = 0;
	int attach_object_count = 0;
	tag_t * attach_object_tags = NULL;
	tag_t attach_object_tag = NULLTAG;
	tag_t iman_type_tag = NULLTAG;
	char obj_class_name[TCTYPE_class_name_size_c + 1];
	char obj_type_name[TCTYPE_name_size_c + 1];
	char sType[128 + 1];
	int attachment_types = EPM_reference_attachment;
	int ifail = ITK_ok;
	char debug_str[POM_MAX_MAX_STRING_LENGTH + 32];
	char fromvalue[10];
	char tovalue[10];
	char statusvalue[128 + 1];
	int typeCount = 0;
	char ** types = NULL;
	int statusCount = 0;
	char ** statuses = NULL;
	int target_status_count = 0;
	tag_t * target_status_list = NULL;
	char release_status_type[WSO_name_size_c + 1];

	//process handler argument
	args_tmp[0] = '\0';
	fromvalue[0] = '\0';
	tovalue[0] = '\0';
	sType[0] = '\0';
	statusvalue[0] = '\0';
	property_count = TC_number_of_arguments(msg.arguments);
	if (property_count <= 0)
	{
		property_count = 0;
		DEBUG_LOG("Error not arguments\n");
		return EPM_wrong_number_of_arguments;
	}
	else
	{
		for (i = 0; i < property_count; i++)
		{
			strcpy(args_tmp, (const char*) TC_next_argument(msg.arguments));
			if (strncmp(args_tmp, ARGUMENT_TYPE, strlen(ARGUMENT_TYPE)) == 0)
			{
				strcpy(sType, args_tmp + strlen(ARGUMENT_TYPE));
				iTypeFind = 1;
			}
			else if (strncmp(args_tmp, DEBUG, strlen(DEBUG)) == 0)
			{
				STRU2L( args_tmp );
				if (strcmp(args_tmp + strlen(DEBUG), "true") == 0)
					iDebug = 1;
			}
			else if (strncmp(args_tmp, FROM, strlen(FROM)) == 0)
			{
				STRU2L( args_tmp );
				strcpy(fromvalue, args_tmp + strlen(FROM));
			}
			else if (strncmp(args_tmp, TO, strlen(TO)) == 0)
			{
				STRU2L( args_tmp );
				strcpy(tovalue, args_tmp + strlen(TO));
			}
			else if (strncmp(args_tmp, STATUS, strlen(STATUS)) == 0)
			{
				strcpy(statusvalue, args_tmp + strlen(STATUS));
				iStatusFind = 1;
			}
		}
	}
	if (iDebug == 1)
	{
		DEBUG_LOG("[DEBUG] begin CCL-switch-objects handler...........\n");
	}

	DEBUG_LOG("[DEBUG] iTypeFind == 1\n");
	CALL(ifail =EPM_ask_job( msg.task, &job ));
	CALL(ifail =EPM_ask_root_task( job, &root_task ));
	//get all attachments EPM_target_attachment or EPM_reference_attachment
	if (strcmp(fromvalue, "target") == 0)
	{
		CALL(ifail =EPM_ask_attachments( root_task, EPM_target_attachment, &attach_object_count, &attach_object_tags ));
	}
	else if (strcmp(fromvalue, "reference") == 0)
	{
		CALL(ifail =EPM_ask_attachments( root_task, EPM_reference_attachment, &attach_object_count, &attach_object_tags ));
	}
	if (attach_object_count > 0)
	{
	    if(iTypeFind == 1)
	        typeCount = splitStr(sType, ',', &types);
	    if(iStatusFind == 1)
	        statusCount = splitStr(statusvalue, ',', &statuses);

		for (i = 0; i < attach_object_count; i++)
		{
			typeFind = 0;
			statusFind = 0;
			attach_object_tag = attach_object_tags[i];
            if (iTypeFind == 1)
            {
                //get object type
                CALL(ifail =TCTYPE_ask_object_type( attach_object_tag,&iman_type_tag ) );
                CALL(ifail =TCTYPE_ask_class_name(iman_type_tag,obj_class_name));//get ClassName of attachment
                CALL(ifail =TCTYPE_ask_name(iman_type_tag,obj_type_name) );
                release_status_type[0] = '\0';
    //			if (target_status_count > 1)
    //			{
    //				CALL(ifail = CR_ask_release_status_type( target_status_list[target_status_count-1], release_status_type ) );
    //			}
    //			if (target_status_list)
    //			{
    //				MEM_free(target_status_list);
    //				target_status_list = NULL;
    //			}

                if (iDebug)
                {
                    sprintf(debug_str, "[DEBUG] Type name : %s\n", obj_type_name);
                    DEBUG_LOG (debug_str);
                    sprintf(debug_str, "[DEBUG] Class name : %s\n", obj_class_name);
                    DEBUG_LOG (debug_str);
                }
//				sprintf(debug_str, "[DEBUG] typeCount : %d\n", typeCount);
//				DEBUG_LOG (debug_str);
				for (j = 0; j < typeCount; j++)
				{
//					sprintf(debug_str, "[DEBUG] types[%d]:%s\n", j, types[j]);
//					DEBUG_LOG (debug_str);
					if (strcmp(obj_type_name, types[j]) == 0)
					{
						typeFind = 1;
					}
				}
				if (typeFind == 0)
					continue;
			}
			if (iStatusFind == 1)
			{
	            CALL(ifail =WSOM_ask_release_status_list(attach_object_tag,&target_status_count,&target_status_list) );
//				sprintf(debug_str, "[DEBUG] statusCount : %d\n", statusCount);
//				DEBUG_LOG (debug_str);
				for (j = 0; j < statusCount; j++)
				{
//					sprintf(debug_str, "[DEBUG] statuses[%d]:%s\n", j, statuses[j]);
//					DEBUG_LOG (debug_str);
					for(k = 0; k < target_status_count;k++)
					{
						CALL(ifail = CR_ask_release_status_type( target_status_list[k], release_status_type ) );
						if (strcmp(release_status_type, statuses[j]) == 0)
						{
							statusFind = 1;
							break;
						}
					}
					if(statusFind == 1) break;
				}
				if (target_status_list)
				{
					MEM_free(target_status_list);
					target_status_list = NULL;
				}

				if (statusFind == 0)
					continue;
			}
			CALL(ifail =EPM_remove_attachments( root_task , 1 , & attach_object_tag ));
			if ((strcmp(fromvalue, "target") == 0) && (strcmp(tovalue, "reference") == 0))
			{
				attachment_types = EPM_reference_attachment;
				CALL(ifail =EPM_add_attachments( root_task , 1 , & attach_object_tag , & attachment_types ));
			}
			else if ((strcmp(fromvalue, "reference") == 0) && (strcmp(tovalue, "target") == 0))
			{
				attachment_types = EPM_target_attachment;
				CALL(ifail =EPM_add_attachments( root_task , 1 , & attach_object_tag , & attachment_types ));
			}
			printf("%d",i);
			attach_object_tag = NULLTAG;
		}
	}

CLEANUP:
    if(typeCount > 0)
    {
        for (j = 0; j < typeCount; j++)
        {
            free(types[j]);
        }
        free(types);
        types = NULL;
    }

    if(statusCount > 0)
    {
        for(j = 0; j < statusCount; j++)
        {
            free(statuses[j]);
        }
        free(statuses);
        statuses = NULL;
    }

	if (attach_object_tags)
	{
		MEM_free(attach_object_tags);
		attach_object_tags = NULL;
	}
	if (target_status_list)
	{
		MEM_free(target_status_list);
		target_status_list = NULL;
	}
	if (rcode != ITK_ok)
	{
		EMH_ask_error_text(rcode, &err_string);
		printf("ERROR: %d ERROR MSG: %s.\n", rcode, err_string);
		printf("FILE: %s LINE: %d\n", __FILE__, err_line);
		MEM_free(err_string);
	}

//	if (iDebug == 1)
	{
		printf("[DEBUG] end CCL-switch-objects handler...........\n");
	}

	return rcode;
}
